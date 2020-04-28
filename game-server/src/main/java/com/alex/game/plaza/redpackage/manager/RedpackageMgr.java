package com.alex.game.plaza.redpackage.manager;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.alex.game.common.util.RandomUtil;
import com.alex.game.dbdata.dom.Redpackage;
import com.alex.game.dbdata.mapper.RedpackageMapper;
import com.alex.game.dbdic.dic.RedPackageDic;
import com.alex.game.dbdic.dic.domwrapper.RedPackageDomWrapper;
import com.alex.game.dblog.LogAction;
import com.alex.game.games.dice.struct.DiceTable;
import com.alex.game.player.manager.PlayerMgr;
import com.alex.game.player.struct.Player;
import com.alex.game.plaza.redpackage.struct.PlayerGetRedpackage;
import com.alibaba.fastjson.JSON;
import com.google.inject.Inject;
import com.google.inject.Singleton;
/**
 * 红包
 * @author yejuhua
 *
 */
@Singleton
public class RedpackageMgr {

	// 1天,红包过期期限
	public static final long ACTIVE_DAY = 1;
	private ConcurrentHashMap<Integer, Redpackage> redpackageMap=new ConcurrentHashMap<Integer, Redpackage>();
	private final RedpackageMapper redpackageMapper;
	@Inject
	private RedPackageDic redPackageDic;
	@Inject
	private RedpackageMsgMgr redpackageMsgMgr;
	@Inject
	private PlayerMgr playerMgr;
	@Inject
    public RedpackageMgr(RedpackageMapper redpackageMapper) {
    	this.redpackageMapper = redpackageMapper;
	}
	@Inject
	public void init() {
		  List<Redpackage> packList=redpackageMapper.selectActiveRedpackage(activePlayerTime());
		  if(!packList.isEmpty()) {
			  for(Redpackage redpackage:packList) {
				  redpackageMap.put(redpackage.getId(), redpackage);
			  }
		  }
	}
	/**
	 * 获取活跃时间点
	 *
	 * @return
	 */
	private Date activePlayerTime() {
		LocalDateTime dateTime = LocalDateTime.now().minusDays(ACTIVE_DAY);
		return Timestamp.valueOf(dateTime);
	}
	
	/**
	 * 清理内存过期红包
	 */
	public void clearRedpackage() {
		ZoneId zoneId = ZoneId.systemDefault();
		List<Integer> clearList=new ArrayList<>();
		if (!redpackageMap.isEmpty()) {
			for (Redpackage redpackage : redpackageMap.values()) {
				Instant instant = redpackage.getCreateTime().toInstant();
				LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
				LocalDateTime dateTime = localDateTime.plusDays(ACTIVE_DAY);
				if (!dateTime.isAfter(LocalDateTime.now())) {
					clearList.add(redpackage.getId());
				}
			}
		}
		for(int configId:clearList) {
			redpackageMap.remove(configId);
		}
	}
	
	public Redpackage createRedpackage(int redPackageConfigId) {
		RedPackageDomWrapper redPackageDomWrapper=redPackageDic.get(redPackageConfigId);
		Redpackage redpackage=new Redpackage();
		redpackage.setCreateTime(new Date());
//		String desc=redPackageDomWrapper.getRedContentArray().get(0);
//		redpackage.setRedpackageDesc(desc);
		redpackage.setNum(redPackageDomWrapper.getNum());
		redpackage.setSum((long) redPackageDomWrapper.getSum());
		redpackage.setRemainder(redpackage.getSum());
		return redpackage;
	}
	/**
	 * 豹子红包
	 * @param table
	 * @param redPackageId
	 */
	public void diceRedPackage(DiceTable table, int redPackageId) {
		Redpackage redpackage=createRedpackage(redPackageId);
		insertRedpackage(redpackage);
		redpackageMsgMgr.sendDiceRedPackage(table,redpackage.getId());
	}


	
	/**
	 * 开红包
	 * @param player
	 * @param redPackageId
	 */
	public void getRedPackage(Player player, int redPackageId) {
		Redpackage redpackage=loadRedpackage(redPackageId);
		if(redpackage==null) {
			redpackageMsgMgr.sendResOpenPackage(player,1,null);
			return;
		}
		if(redpackage.getRemainder()==0) {
			redpackageMsgMgr.sendResOpenPackage(player,2,redpackage);
			return;
		}
		Instant instant = redpackage.getCreateTime().toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
		LocalDateTime dateTime = localDateTime.plusDays(ACTIVE_DAY);
		if(!dateTime.isAfter(LocalDateTime.now())) {
		    redpackageMsgMgr.sendResOpenPackage(player,3,redpackage);
			return;
		}
		List<PlayerGetRedpackage> redList=redpackageMsgMgr.getPlayerGetRedpackageList(redpackage);
		long redGold=0;
		if(redpackage.getNum()-redList.size()==1) {
			redGold=redpackage.getRemainder();
		}else {
			redGold=RandomUtil.random(1, (int) redpackage.getRemainder()/2);
			
		}
		redpackage.setRemainder(redpackage.getRemainder()-redGold);
		PlayerGetRedpackage playerGetRedpackage=new PlayerGetRedpackage();
		playerGetRedpackage.setId(player.getId());
		playerGetRedpackage.setIcon(player.getIcon());
		playerGetRedpackage.setNickName(player.getNickName());
		playerGetRedpackage.setGetDate(new Date());
		playerGetRedpackage.setRedPackageGold(redGold);
		redList.add(playerGetRedpackage);
		if(redpackage.getNum()==redList.size()) {
			redpackage.setCompleteTime(new Date());
		}
		redpackage.setRedpackageDesc(JSON.toJSONString(redList));
		redpackageMapper.updateByPrimaryKey(redpackage);
		playerMgr.addGold(player, redGold, true, true, LogAction.DICE_REDPACKAGE, "豹子红包" );
		redpackageMsgMgr.sendResOpenPackage(player,0,redpackage);
	}
	
	/**
	 * 查看红包详情
	 * @param player
	 * @param redPackageId
	 */
	public void getRedPackageInfo(Player player, int redPackageId) {
		Redpackage redpackage=loadRedpackage(redPackageId);
		if(redpackage==null) {
			redpackageMsgMgr.sendRedPackageInfo(player,1,null);
			return;
		}
		redpackageMsgMgr.sendRedPackageInfo(player,0,redpackage);
	}
	private Redpackage loadRedpackage(int redPackageId) {
		if(redpackageMap.containsKey(redPackageId)) {
			return redpackageMap.get(redPackageId);
		}
		return redpackageMapper.selectByPrimaryKey(redPackageId);
	}
	private void insertRedpackage(Redpackage redpackage) {
		redpackageMapper.insert(redpackage);
		redpackageMap.put(redpackage.getId(), redpackage);
	}
	
}
