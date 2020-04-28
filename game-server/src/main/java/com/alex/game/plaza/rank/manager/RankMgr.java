package com.alex.game.plaza.rank.manager;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alex.game.dbdata.dom.PlayerRankDom;
import com.alex.game.dbdata.mapper.PlayerMapper;
import com.alex.game.event.manager.EventMgr;
import com.alex.game.event.struct.bank.RechargeSuccessEvent;
import com.alex.game.event.struct.game.ExitRoomEvent;
import com.alex.game.player.manager.PlayerMgr;
import com.alex.game.player.struct.Player;
import com.alex.game.util.FixSizedPriorityQueue;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
/**
 * 排行榜
 * @author yejuhua
 *
 */
@Singleton
public class RankMgr {
	public static final Logger LOG = LoggerFactory.getLogger(RankMgr.class);
	private List<PlayerRankDom> agentGoldRankList = new ArrayList<PlayerRankDom>();
	private final PlayerMapper playerMapper;
	@Inject
	private PlayerMgr playerMgr;
	private final FixSizedPriorityQueue fixSizedPriorityQueue = new FixSizedPriorityQueue(100);
	@Inject
	private RankMsgMgr rankMsgMgr;
	@Inject
    public RankMgr(PlayerMapper playerMapper, EventMgr eventMgr) {
    	this.playerMapper = playerMapper;
		eventMgr.register(this);
	}
	
	private void addPlayertoRank(long playerId) {
		Player player=playerMgr.getPlayer(playerId);
		if(player!=null) {
			PlayerRankDom playerRankDom=new PlayerRankDom();
			playerRankDom.setGold(player.bankGold()+playerRankDom.getGold());
			playerRankDom.setPlayerId(playerId);
			playerRankDom.setIconId(player.getIcon());
			playerRankDom.setNickName(player.getNickName());
			playerRankDom.setSignature(player.getSignature());
			boolean flag=fixSizedPriorityQueue.add(playerRankDom);
			if(flag) {
				agentGoldRankList=fixSizedPriorityQueue.sortedList();
			}
			
		}
		
	}
	@Subscribe
	public void onTransferAccounts(ExitRoomEvent event) {
		long playerId=event.playerId;
		addPlayertoRank(playerId);
		
	}
	

	@Subscribe
	public void onRechargeSuccessEvent(RechargeSuccessEvent event) {
		long playerId=event.playerId;
		addPlayertoRank(playerId);
	}
	@Subscribe
	public void onExitRoomEvent(ExitRoomEvent event) {
		long playerId=event.playerId;
		addPlayertoRank(playerId);
	}
	@Inject
	public void init() {
		agentGoldRankList=playerMapper.selectAgentByGold();
		LOG.info("加载排行榜[{}]位玩家数据成功[{}]", agentGoldRankList.size(), agentGoldRankList);
	}
	/**
	 * 
	 * @param player
	 */
	public void loadAgentGoldRankList(Player player) {
		rankMsgMgr.sendResRankList(player,agentGoldRankList);
	}

	
}
