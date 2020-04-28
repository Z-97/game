/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.player.manager;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alex.game.dbdata.dom.BankRecord;
import com.alex.game.dbdata.dom.GiftRecord;
import com.alex.game.dbdata.dom.PlayerDom;
import com.alex.game.dbdata.mapper.PlayerMapper;
import com.alex.game.dblog.GiftAction;
import com.alex.game.dblog.LogAction;
import com.alex.game.dblog.bank.BankGoldLog;
import com.alex.game.dblog.bank.GiftLog;
import com.alex.game.dblog.core.DbLogService;
import com.alex.game.dblog.resource.GoldLog;
import com.alex.game.event.manager.EventMgr;
import com.alex.game.event.struct.bank.RechargeSuccessEvent;
import com.alex.game.event.struct.game.ExitRoomEvent;
import com.alex.game.event.struct.login.PlayerLoginEvent;
import com.alex.game.event.struct.server.ShutdownEvent;
import com.alex.game.player.struct.Player;
import com.alex.game.schedule.manager.ScheduleMgr;
import com.alex.game.server.ExecutorMgr;
import com.alex.game.util.TimeUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * 玩家管理,对玩家的操作都在玩家TaskExecutor中
 *
 * @author Alex
 * @date 2016/7/27 11:10
 */
@Singleton
public class PlayerMgr {

	public static final Logger LOG = LoggerFactory.getLogger(PlayerMgr.class);
	public static final Logger PLAYER_SAVE_LOG = LoggerFactory.getLogger("PlayerSaveLog");
	public static final Logger CHANNEL_SAVE_LOG = LoggerFactory.getLogger("ChannelSaveLog");

	// 银行记录保存条数
	private static final int BANK_RECORDS_NUM = 100;
	// 2天,活跃期限
	public static final long ACTIVE_DAY = 2;

	private final PlayerMapper playerMapper;
	private final ExecutorMgr executorMgr;
	// 活跃玩家(包含了在线的数据)
	private final ConcurrentHashMap<Long, PlayerDom> activePlayers = new ConcurrentHashMap<>();
	// 在线玩家
	private final ConcurrentHashMap<Long, Player> players = new ConcurrentHashMap<>();
	// 1到5个字母数字下划线汉字可以有空格
	public static final String NICKNAME_PATTERN = "[a-zA-Z0-9_\u0020\u4e00-\u9fa5]{1,18}";
	@Inject
	private PlayerMsgMgr playerMsgMgr;
	@Inject
	public PlayerMgr(PlayerMapper playerMapper, EventMgr eventMgr, ExecutorMgr executorMgr) {
		this.playerMapper = playerMapper;
		this.executorMgr = executorMgr;
		eventMgr.register(this);
	}

	@Inject
	public void init(@Named("resetOnlineFlg") boolean resetOnline, ScheduleMgr scheduleMgr) {
		if (resetOnline) {
			// 重置在线标示,以防服务器异常关服
			playerMapper.resetOnline();
		}

		// 加载活跃玩家
		loadActivePlayers();
		// 每隔5分钟保存一次在线玩家
		scheduleMgr.scheduleWithFixedDelay(this::updateOnlinePlayersAsync, 5, 5, TimeUnit.MINUTES);
		// 每隔半小时清理保存一次活跃玩家
		scheduleMgr.scheduleWithFixedDelay(this::refreshActivePlayers, 7, 30, TimeUnit.MINUTES);
	}

	@Subscribe
	public void onLogin(PlayerLoginEvent event) {
		playerMsgMgr.sendPlayerInfoMsg(event.player);
	}

	/**
	 * 关服后保存活跃玩家数据
	 * 
	 * @param event
	 */
	@Subscribe
	public void onShutDown(ShutdownEvent event) {
		LOG.info("正在保存玩家数据(数据量大可能需要几分钟)...");
		long now = System.currentTimeMillis();
		for (PlayerDom playerDom : activePlayers.values()) {
			updatePlayer(playerDom);
		}
		LOG.info("保存[{}]位玩家数据成功,耗时[{}]毫秒", activePlayers.size(), System.currentTimeMillis() - now);
	}

	/**
	 * 1充值后经验改变2发送充值成功邮件
	 * 
	 * @param event
	 */
	@Subscribe
	public void onRechargeSuccess(RechargeSuccessEvent event) {
		PlayerDom playerDom = selectById(event.playerId);
		if (playerDom == null) {
			return;
		}
		
		// 通知玩家充值金额更改

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 状态0已经通知1未通知
		int notifyState = 1;

		if (onLine(event.playerId)) {
			// 在线玩家发送充值金额更改
//			Player player = this.getPlayer(event.playerId);
//			playerMsgMgr.sendRechargeMoney(player);
//			ArrayList<RechargeTips> tips = new ArrayList<RechargeTips>();
//			RechargeTips rechargeTips=new RechargeTips();
//			rechargeTips.setType(event.type);
//			rechargeTips.setRecharge((long) event.money);
//			tips.add(rechargeTips);
//			playerMsgMgr.sendRechargeSuccessTips(player, tips);
//			notifyState = 0;
		}
		// 发送充值成功邮件
	
	}

	/**
	 * 退出房间后打死鱼金币经验改变
	 * 
	 * @param event
	 */
	@Subscribe
	public void onExitRoom(ExitRoomEvent event) {
		
	}

	/**
	 * 加载活跃玩家
	 */
	private void loadActivePlayers() {
		LOG.info("正在加载活跃玩家数据...", this.activePlayers.size());
		long now = System.currentTimeMillis();
		List<PlayerDom> activePlayers = playerMapper.selectActivePlayers(activePlayerTime());
		for (PlayerDom playerDomain : activePlayers) {
			this.activePlayers.put(playerDomain.getId(), playerDomain);
		}
		LOG.info("共加载了[{}]位活跃玩家数据,耗时[{}]毫秒", this.activePlayers.size(), System.currentTimeMillis() - now);
	}
	
	/**
	 * 异步保存在线玩家数据
	 */
	public void updateOnlinePlayersAsync() {
		for (Player player : players.values()) {
			updatePlayerAsync(player);
		}
	}

	/**
	 * 刷新保存活跃玩家,清理登录时间和登出时间都小于活跃玩家时间点
	 */
	private void refreshActivePlayers() {
		long now = System.currentTimeMillis();
		int activePlayersNum = activePlayers.size();
		// 活跃玩家的时间点
		long activeTime = activePlayerTime().getTime();
		Iterator<PlayerDom> activePlayersIt = activePlayers.values().iterator();

		while (activePlayersIt.hasNext()) {
			PlayerDom playerDom = activePlayersIt.next();
			Date loginTime = playerDom.getLoginTime();
			Date logoutTime = playerDom.getLogoutTime();
			updatePlayerAsync(playerDom);
			
			// ACTIVE_DAY天没有登录和下线视为非活跃玩家
			if (!onLine(playerDom.getId()) && loginTime != null && loginTime.getTime() < activeTime && logoutTime != null
					&& logoutTime.getTime() < activeTime) {
				activePlayersIt.remove();
				LOG.info("玩家[{}][{}]上次登录和退出超过[{}]天,从活跃玩家中清理出去", playerDom.getId(), playerDom.getNickName(), ACTIVE_DAY);
			}
		}

		LOG.info("刷新[{}]位活跃玩家数据用时[{}]毫秒", activePlayersNum, System.currentTimeMillis() - now);
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
	 * 注册在线玩家
	 *
	 * @param player
	 */
	public void registerPlayer(Player player) {
		players.put(player.getId(), player);
		activePlayers.put(player.getId(), player.dom());
	}

	/**
	 * 取消登记在线玩家,活跃玩家不变，活跃玩家通过定时器来刷新
	 *
	 * @param player
	 */
	public void unregisterPlayer(Player player) {
		players.remove(player.getId());
	}

	/**
	 * 获取在线玩家
	 *
	 * @param playerId
	 * @return
	 */
	public Player getPlayer(long playerId) {
		return players.get(playerId);
	}

	/**
	 * 根据玩家id查找玩家,先在活跃玩家中找，找不到再去数据库找，如果在数据库中找到则放入活跃玩家中
	 *
	 * @param playerId
	 * @return
	 */
	public PlayerDom selectById(long playerId) {
		PlayerDom playerDom = activePlayers.get(playerId);
		if (playerDom == null) {
			playerDom = playerMapper.selectById(playerId);
			if (playerDom != null) {
				activePlayers.put(playerId, playerDom);
			}
		}

		return playerDom;
	}

	/**
	 * 先在活跃玩家中找，找不到再去数据库找
	 * 
	 * @param touristMac
	 * @return
	 */
	public PlayerDom selectByTouristMac(String touristMac) {
		for (PlayerDom dom : activePlayers.values()) {
			if (dom != null && touristMac.equals(dom.getTouristMac())) {
				return dom;
			}
		}

		PlayerDom playerDom = playerMapper.selectByTouristMac(touristMac);
		if (playerDom != null) {
			activePlayers.put(playerDom.getId(), playerDom);
		}

		return playerDom;
	}

	/**
	 * 根据用户名查找玩家,如果是手机用户,用户名就是手机号,先在活跃账号缓存找，找不到再去数据库找，如果在数据库中找到则放入活跃玩家中
	 *
	 * @param userName
	 * @return
	 */
	public PlayerDom selectByUserName(String userName) {
		for (PlayerDom dom : activePlayers.values()) {
			if (dom != null && dom.getUserName().equals(userName)) {
				return dom;
			}
		}

		PlayerDom playerDom = playerMapper.selectByUserName(userName);
		if (playerDom != null) {
			activePlayers.put(playerDom.getId(), playerDom);
		}

		return playerDom;
	}

	/**
	 * 根据手机查找
	 * @param mobilePhone
	 * @return
	 */
	public PlayerDom selectByPhone(String mobilePhone) {
		for (PlayerDom dom : activePlayers.values()) {
			if (dom != null && dom.getPhone()!=null&&dom.getPhone().equals(mobilePhone)) {
				return dom;
			}
		}

		PlayerDom playerDom = playerMapper.selectByPhone(mobilePhone);
		if (playerDom != null) {
			activePlayers.put(playerDom.getId(), playerDom);
		}

		return playerDom;
	}
	/**
	 * 玩家是否在线
	 *
	 * @param playerId
	 * @return
	 */
	public boolean onLine(long playerId) {
		return players.containsKey(playerId);
	}

	/**
	 * 所有在线玩家
	 *
	 * @return
	 */
	public ConcurrentHashMap<Long, Player> onLinePlayers() {

		return players;
	}

	/**
	 * 活跃玩家
	 *
	 * @return
	 */
	public ConcurrentHashMap<Long, PlayerDom> activePlayers() {

		return activePlayers;
	}

	/**
	 * 同步插入玩家数据
	 */
	public void insertPlayer(PlayerDom playerDom) {
		try {
			// 如果保存数据失败(如：数据库连接断开,网络异常等)则保存数据到日志文件以便恢复
			playerMapper.insert(playerDom);
		} catch (Exception e) {
			PLAYER_SAVE_LOG.error(String.format("玩家[%s][%s]数据[%s]插入失败", playerDom.getNickName(), playerDom.getId(),
					JSON.toJSONString(playerDom)), e);
		}
	}

	/**
	 * saveExecutors快速取模后保存玩家数据
	 * 
	 * @param player
	 */
	public void updatePlayerAsync(Player player) {
		executorMgr.playerSaveExecutor(player.getId()).execute(() -> this.updatePlayer(player.dom()));
	}

	/**
	 * saveExecutors快速取模后保存玩家数据
	 * 
	 * @param player
	 */
	public void updatePlayerAsync(PlayerDom playerDom) {
		executorMgr.playerSaveExecutor(playerDom.getId()).execute(() -> this.updatePlayer(playerDom));
	}

	/**
	 * 更新玩家数据
	 *
	 * @param playerDom
	 */
	private void updatePlayer(PlayerDom playerDom) {
		try {
			playerDom.setUpdateTime(new Date());
			// 如果保存数据失败(如：数据库连接断开,网络异常等)则保存数据到日志文件以便恢复
			playerMapper.updateById(playerDom);
		} catch (Exception e) {
			PLAYER_SAVE_LOG.error(String.format("玩家[%s][%s]数据[%s]保存失败", playerDom.getNickName(), playerDom.getId(),
					JSON.toJSONString(playerDom)), e);
		}
	}

	/**
	 * 更新玩家渠道
	 * 
	 * @param newChannelId
	 * @param oldChannelId
	 * @return
	 */
	public int updatePlayerChannelId(String newChannelId, String oldChannelId, String packageId) {
		try {
			return this.playerMapper.updatePlayerChannelId(newChannelId, oldChannelId, packageId);
		} catch (Exception e) {
			CHANNEL_SAVE_LOG.error(
					String.format("channelId更新旧[%s]为[%s]packageId[%s]保存失败", oldChannelId, newChannelId, packageId), e);
		}

		return 0;
	}

	/**
	 * 增加玩家金币
	 * 
	 * @param player
	 * @param gold
	 * @param send		是否发送金币同步消息
	 * @param log		是否记录日志
	 * @param action
	 */
	public void addGold(Player player, long gold, boolean send, boolean log, LogAction action) {
		doAddGold(player.dom(), gold, log, action, null);
		if (send) {
			playerMsgMgr.sendGoldChangeMsg(player);
		}
	}

	/**
	 * 增加玩家金币
	 *
	 * @param player
	 * @param gold
	 * @param action
	 * @return
	 */
	public void addGold(Player player, long gold, LogAction action) {
		addGold(player, gold, true, true, action);
	}

	/**
	 * 增加金币
	 * 
	 * @param playerDom
	 * @param gold
	 * @param send		是否发送消息
	 * @param log		是否记录日志
	 * @param action
	 * @param desc
	 */
	public void addGold(PlayerDom playerDom, long gold, boolean send, boolean log, LogAction action, String desc) {
		doAddGold(playerDom, gold, log, action, desc);
		Player player = getPlayer(playerDom.getId());
		if (send && player != null) {
			playerMsgMgr.sendGoldChangeMsg(player);
		}
		
	}

	/**
	 * 增加金币
	 * 
	 * @param playerDom
	 * @param gold
	 * @param send
	 * @param log
	 * @param action
	 */
	public void addGold(PlayerDom playerDom, long gold, boolean send, boolean log, LogAction action) {
		addGold(playerDom, gold, send, log, action, null);
	}

	/**
	 * 增加玩家金币
	 *
	 * @param playerDom
	 * @param gold
	 * @param action
	 * @return
	 */
	public void addGold(PlayerDom playerDom, long gold, LogAction action) {
		addGold(playerDom, gold, true, true, action, null);
	}

	/**
	 * 增加玩家金币
	 *
	 * @param playerId
	 * @param gold
	 * @param action
	 * @return
	 */
	public void addGold(long playerId, long gold, LogAction action) {
		addGold(selectById(playerId), gold, action);
	}

	/**
	 * 增加玩家银行金币
	 *
	 * @param playerDom
	 * @param gold
	 * @param action
	 * @return
	 */
	public void addBankGold(PlayerDom playerDom, long gold, LogAction action) {
		doAddBankGold(playerDom, gold, action, null);
		Player player = getPlayer(playerDom.getId());
		if (player != null) {
//			playerMsgMgr.sendBankGoldChangeMsg(player);
		}
	}

	/**
	 * 增加玩家银行金币
	 * 
	 * @param playerDom
	 * @param gold
	 * @param action
	 * @param desc
	 */
	public void addBankGold(PlayerDom playerDom, long gold, LogAction action, String desc) {
		doAddBankGold(playerDom, gold, action, desc);
		Player player = getPlayer(playerDom.getId());
		if (player != null) {
//			playerMsgMgr.sendBankGoldChangeMsg(player);
		}
	}

	/**
	 * 增加玩家银行金币
	 *
	 * @param player
	 * @param gold
	 * @param action
	 * @return
	 */
	public void addBankGold(Player player, long gold, LogAction action) {
		doAddBankGold(player.dom(), gold, action, null);
//		playerMsgMgr.sendBankGoldChangeMsg(player);
	}

	/**
	 * 增加玩家银行金币
	 *
	 * @param playerDom
	 * @param val
	 * @param action
	 */
	private void doAddBankGold(PlayerDom playerDom, long val, LogAction action, String desc) {
		if (val == 0) {
			// LOG.warn("玩家[{}][{}]增加的银行金币不能为0", playerDom.getPlayerName(),
			// playerDom.getId());
			return;
		}

		AtomicLong bankGold = playerDom.getBankGold();
		long beforeBankGold = bankGold.get();
		long afterBankGold = bankGold.addAndGet(val);

		// 玩家所有银行记录
		List<BankRecord> bankRecords = playerDom.getBankRecords();
		// 银行记录
		BankRecord record = new BankRecord(System.currentTimeMillis(), action.desc, val, beforeBankGold, afterBankGold);
		bankRecords.add(record);
		if (bankRecords.size() > BANK_RECORDS_NUM) {// 只保留RECORDS_NUM条
			bankRecords.remove(0);
		}

		DbLogService.log(new BankGoldLog(playerDom, beforeBankGold, afterBankGold, action, desc));
	}

	/**
	 * 增加玩家金币
	 * 
	 * @param playerDom
	 * @param val
	 * @param log		是否记录日志
	 * @param action
	 */
	private void doAddGold(PlayerDom playerDom, long val, boolean log, LogAction action, String desc) {
		if (val == 0) {
			LOG.warn("玩家[{}][{}]增加的金币不能为0", playerDom.getNickName(),playerDom.getId());
			return;
		}
		AtomicLong gold = playerDom.getGold();
		long beforeGold = gold.get();
		long afterGold = gold.addAndGet(val);
		if (log) {
			DbLogService.log(new GoldLog(playerDom, beforeGold, afterGold, action, desc));
		}
	}

	/**
	 * 增加玩家礼物记录
	 * @param player
	 * @param playerName
	 * @param giftAction
	 * @param itemId
	 * @param itemNum
	 * @param itemGold
	 */
	public void addAddGiftRecord(Player player ,String playerName,GiftAction giftAction,int itemId,int itemNum,long itemGold) {
		doAddGiftRecord(player.dom(), playerName, giftAction, itemId, itemNum, itemGold);
	}
	public void addAddGiftRecord(PlayerDom player ,String playerName,GiftAction giftAction,int itemId,int itemNum,long itemGold) {
		doAddGiftRecord(player, playerName, giftAction, itemId, itemNum, itemGold);
	}
	private void doAddGiftRecord(PlayerDom playerDom ,String playerName,GiftAction giftAction,int itemId,int itemNum,long itemGold) {
		GiftRecord giftRecord=new GiftRecord();
		giftRecord.setItemId(itemId);
		giftRecord.setGetType(giftAction.type);
		giftRecord.setItemNum(itemNum);
		giftRecord.setPlayerName(playerName);
		giftRecord.setGiftGold(itemGold);
		giftRecord.setTime(TimeUtil.getCurrentSeconds());
		List<GiftRecord> getGiftRecords=playerDom.getGiftRecords();
		getGiftRecords.add(giftRecord);
		if (getGiftRecords.size() > BANK_RECORDS_NUM) {// 只保留RECORDS_NUM条
			getGiftRecords.remove(0);
		}
		
		DbLogService.log(new GiftLog(playerDom, giftAction.type, itemId, itemNum, itemGold));
	}
	/**
	 * 活跃玩家数据
	 * 
	 * @return
	 */
	public ConcurrentHashMap<Long, PlayerDom> getActivePlayers() {
		return activePlayers;
	}

	

}

	
