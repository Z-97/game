package com.alex.game.plaza.rank.manager;

import java.util.List;

import com.alex.game.dbdata.dom.PlayerRankDom;
import com.alex.game.player.manager.PlayerMsgMgr;
import com.alex.game.player.struct.Player;
import com.alex.game.rank.RankProto.RankInfo;
import com.alex.game.rank.RankProto.ResRankList;
import com.alex.game.server.tcp.MsgHandlerFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class RankMsgMgr {

	@Inject
	private PlayerMsgMgr msgMgr;

	public void sendResRankList(Player player, List<PlayerRankDom> agentGoldRankList) {
		ResRankList.Builder resmailList =ResRankList.newBuilder();
		int size=agentGoldRankList.size();
        for(int i=0;i<size;i++) {
        	PlayerRankDom playerRankDom=agentGoldRankList.get(i);
        	RankInfo.Builder rankInfo=RankInfo.newBuilder();
        	rankInfo.setPlayerId(playerRankDom.getPlayerId());
        	rankInfo.setRank(i+1);
        	rankInfo.setNickName(playerRankDom.getNickName());
        	rankInfo.setSignature(playerRankDom.getNickName());
        	rankInfo.setGold(playerRankDom.getGold());
        	rankInfo.setIconId(playerRankDom.getIconId());
        	resmailList.addRankInfoList(rankInfo);
        }
        
        msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("rank.ResRankList"), resmailList.build().toByteString());
		
	}
	
}
