package com.alex.game.games.dice.manager;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import com.alex.game.dice.DiceProto.ApplicantInfoBP;
import com.alex.game.dice.DiceProto.BankerInfo;
import com.alex.game.dice.DiceProto.BetAreaInfo;
import com.alex.game.dice.DiceProto.DiceRank;
import com.alex.game.dice.DiceProto.DiceSeatWinfo;
import com.alex.game.dice.DiceProto.PlayerBankerInfo;
import com.alex.game.dice.DiceProto.ResApplyBanker;
import com.alex.game.dice.DiceProto.ResApplyBankerMsg;
import com.alex.game.dice.DiceProto.ResBanker;
import com.alex.game.dice.DiceProto.ResBankerInfo;
import com.alex.game.dice.DiceProto.ResBetDice;
import com.alex.game.dice.DiceProto.ResBetDiceMsg;
import com.alex.game.dice.DiceProto.ResContinueBet;
import com.alex.game.dice.DiceProto.ResContinueBetMsg;
import com.alex.game.dice.DiceProto.ResDiceRank;
import com.alex.game.dice.DiceProto.ResDiceStage;
import com.alex.game.dice.DiceProto.ResDiceWinInfo;
import com.alex.game.dice.DiceProto.ResEnterDiceRoom;
import com.alex.game.dice.DiceProto.ResEnterDiceRoomMsg;
import com.alex.game.dice.DiceProto.ResExitBanker;
import com.alex.game.dice.DiceProto.ResExitDiceRoom;
import com.alex.game.dice.DiceProto.ResPlayerBankerMsg;
import com.alex.game.dice.DiceProto.ResSixSeatMsg;
import com.alex.game.games.common.dice.DiceType;
import com.alex.game.games.dice.struct.ApplicantInfo;
import com.alex.game.games.dice.struct.AreaBetInfo;
import com.alex.game.games.dice.struct.DiceSeat;
import com.alex.game.games.dice.struct.DiceStage;
import com.alex.game.games.dice.struct.DiceTable;
import com.alex.game.games.dice.struct.PlayerDiceRankInfo;
import com.alex.game.games.dice.struct.PlayerDiceRecord;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.manager.PlayerMsgMgr;
import com.alex.game.player.struct.Player;
import com.alex.game.server.tcp.MsgHandlerFactory;
import com.alex.game.util.TimeUtil;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.protobuf.ByteString;
@Singleton
public class DiceMsgMgr {
	@Inject
	private PlayerMsgMgr msgMgr;
	
	public void sendStageMsg(DiceTable table, DiceStage stage, int stageTime) {
		ResDiceStage.Builder resGobangStage=ResDiceStage.newBuilder();
		resGobangStage.setStage(stage.val);
		resGobangStage.setTime(stageTime+TimeUtil.getCurrentSeconds());
		sendTablePlayersMsg(table, MsgHandlerFactory.getProtocol("dice.ResDiceStage"), resGobangStage.build().toByteString());
	}
	/**
	 * 向同桌的玩家(包括自己)发送消息
	 * 
	 * @param table
	 * @param msg
	 */
	public void sendTablePlayersMsg(DiceTable table, int protocol,ByteString byteString) {
		CommonMessage.Builder commonMessage =CommonMessage.newBuilder();
		commonMessage.setId(protocol);
		commonMessage.setContent(byteString);
		List<DiceSeat> seats = table.seats;
		for (int i = 0; i < seats.size(); i++) {
			long playerId = seats.get(i).playerId;
			if (playerId > 0) msgMgr.writeMsg(playerId, commonMessage.build());
		}
	}
	
	/**
	 * 下注信息
	 * @param seat
	 * @param area
	 * @param gold
	 */
	public void sendResBetDiceMsg(DiceSeat seat, int area,long gold) {
		ResBetDiceMsg.Builder resGobangStage=ResBetDiceMsg.newBuilder();
		BetAreaInfo.Builder betAreaInfo=BetAreaInfo.newBuilder();
		betAreaInfo.setPlayerId(seat.playerId);
		betAreaInfo.setArea(area);
		betAreaInfo.setBetGold( gold);
		resGobangStage.setBetAreaInfo(betAreaInfo);
		sendTablePlayersMsg(seat.table, MsgHandlerFactory.getProtocol("dice.ResBetDiceMsg"), resGobangStage.build().toByteString());
	}
	public void sendApplyBankersMsg(DiceTable table) {
		ResApplyBankerMsg.Builder resGobangStage=ResApplyBankerMsg.newBuilder();
		if(!table.applicants.isEmpty()) {
			for(ApplicantInfo ainfo:table.applicants) {
				ApplicantInfoBP.Builder abp=ApplicantInfoBP.newBuilder();
				abp.setGold(ainfo.getGold());
				abp.setIcon(ainfo.getIcon());
				abp.setNickName(ainfo.getNickName());
				resGobangStage.addApplicantInfoList(abp);
			}
		}
	
		sendTablePlayersMsg(table, MsgHandlerFactory.getProtocol("dice.ResApplyBankerMsg"), resGobangStage.build().toByteString());
		
	}
	public void sendBankerInfoMsg(DiceTable table) {
		ResBankerInfo.Builder resGobangStage=ResBankerInfo.newBuilder();
		if(table.banker!=null) {
			BankerInfo.Builder bankerInfo=BankerInfo.newBuilder();
			
			bankerInfo.setGold(table.banker.gold);
			bankerInfo.setIcon(table.banker.icon);
			bankerInfo.setNickName(table.banker.nickName);
			bankerInfo.setPlayerId(table.banker.playerId);
			resGobangStage.setBankerInfo(bankerInfo);
		}
		sendTablePlayersMsg(table, MsgHandlerFactory.getProtocol("dice.ResBankerInfo"), resGobangStage.build().toByteString());
		
	}
	public void sendEnterRoomMsg(DiceSeat seat) {
		DiceTable table = seat.table;
		DiceSeat banker = table.banker;
		ResEnterDiceRoomMsg.Builder resEnterDiceRoom=ResEnterDiceRoomMsg.newBuilder();
		int stageTime = (int)table.stageFuture.getDelay(TimeUnit.SECONDS);
		resEnterDiceRoom.setTime(stageTime+TimeUtil.getCurrentSeconds());
		resEnterDiceRoom.setStage(table.stage.val);
		if(table.stage.val!=2) {
			resEnterDiceRoom.addAllBetAreaInfo(table.betAreaInfo);
		}
		
		if(banker!=null) {
			BankerInfo.Builder bankerInfo=BankerInfo.newBuilder();
			bankerInfo.setGold(banker.gold);
			bankerInfo.setIcon(banker.icon);
			bankerInfo.setNickName(banker.nickName);
			bankerInfo.setPlayerId(banker.playerId);
			resEnterDiceRoom.setBanker(bankerInfo);
		}
		if(!table.applicants.isEmpty()) {
			for(ApplicantInfo ainfo:table.applicants) {
				ApplicantInfoBP.Builder abp=ApplicantInfoBP.newBuilder();
				abp.setGold(ainfo.getGold());
				abp.setIcon(ainfo.getIcon());
				abp.setNickName(ainfo.getNickName());
				resEnterDiceRoom.addApplicantInfoList(abp);
			}
		}
		if(!table.diceHistory.isEmpty()) {
			resEnterDiceRoom.addAllDiceHistory(table.diceHistory);
		}
		
		List<PlayerDiceRecord> goldLike=table.getGodLike();
		long godLikeId=0;
		if(!goldLike.isEmpty()) {
			godLikeId=goldLike.get(0).playerId;
			DiceRank.Builder diceRank=DiceRank.newBuilder();
			diceRank.setGold(goldLike.get(0).totalBet);
			diceRank.setIcon(goldLike.get(0).icon);
			diceRank.setNickName(goldLike.get(0).nickName);
			diceRank.setPlayerId(String.valueOf(goldLike.get(0).playerId));
			resEnterDiceRoom.addRankList(diceRank);
		}

		
		long winGold=seat.winGold-seat.loseGold;
		DiceSeatWinfo.Builder diceSeatWinfo=DiceSeatWinfo.newBuilder();
		diceSeatWinfo.setGold(winGold);
		diceSeatWinfo.setPlayerId(seat.playerId);
		diceSeatWinfo.setTotalBet(seat.totalBet);
		diceSeatWinfo.setTax(seat.tax);
		diceSeatWinfo.setNickName(seat.nickName);
		diceSeatWinfo.setIcon(seat.icon);
		table.resDiceWinInfo.setSeatWinfo(diceSeatWinfo);
		resEnterDiceRoom.setResDiceWinInfo(table.resDiceWinInfo);
		resEnterDiceRoom.setGodLikeId(String.valueOf(godLikeId));
		int pdrSize = table.playerRanks.size();
		for (int i = 0; i < pdrSize; i++) {
			PlayerDiceRankInfo pdr = table.playerRanks.get(i);
			DiceRank.Builder diceRank = DiceRank.newBuilder();
			diceRank.setGold(pdr.getGold());
			diceRank.setIcon(pdr.getIcon());
			diceRank.setNickName(pdr.getNickName());
			diceRank.setPlayerId(String.valueOf(pdr.getPlayerId()));
			resEnterDiceRoom.addRankList(diceRank);
			if (resEnterDiceRoom.getRankListCount() >= 6) {
				break;
			}
		}
		
		
		msgMgr.writeMsg(seat.playerId, MsgHandlerFactory.getProtocol("dice.ResEnterDiceRoomMsg"), resEnterDiceRoom.build().toByteString());
	}
	/**
	 * 
	 * @param player
	 * @param res
	 */
	public void sendResApplyBankerMsg(Player player, int res) {
		ResApplyBanker.Builder rescreateGobangRoom =ResApplyBanker.newBuilder();
		rescreateGobangRoom.setRes(res);
		msgMgr.writeMsg(player.getId(), MsgHandlerFactory.getProtocol("dice.ResApplyBanker"), rescreateGobangRoom.build().toByteString());
	}
	/**
	 * 
	 * @param seat
	 * @param res
	 */
	public void sendResBetDice(DiceSeat seat, int res) {
		ResBetDice.Builder rescreateGobangRoom =ResBetDice.newBuilder();
		rescreateGobangRoom.setRes(res);
		msgMgr.writeMsg(seat.playerId, MsgHandlerFactory.getProtocol("dice.ResBetDice"), rescreateGobangRoom.build().toByteString());
	}
	public void sendResEnterDiceRoom(Player player, int res) {
		ResEnterDiceRoom.Builder rescreateGobangRoom =ResEnterDiceRoom.newBuilder();
		rescreateGobangRoom.setRes(res);
		msgMgr.writeMsg(player.getId(), MsgHandlerFactory.getProtocol("dice.ResEnterDiceRoom"), rescreateGobangRoom.build().toByteString());
	}
	public void sendResExitBanker(Player player, int res) {
		ResExitBanker.Builder rescreateGobangRoom =ResExitBanker.newBuilder();
		rescreateGobangRoom.setRes(res);
		msgMgr.writeMsg(player.getId(), MsgHandlerFactory.getProtocol("dice.ResExitBanker"), rescreateGobangRoom.build().toByteString());
	}
	public void sendResExitDiceRoom(Player player, int res) {
		ResExitDiceRoom.Builder rescreateGobangRoom =ResExitDiceRoom.newBuilder();
		rescreateGobangRoom.setRes(res);
		msgMgr.writeMsg(player.getId(), MsgHandlerFactory.getProtocol("dice.ResExitDiceRoom"), rescreateGobangRoom.build().toByteString());
	}
	/**
	 * 结算信息
	 * @param table
	 */
	public void sendResDiceWinInfo(DiceTable table,TreeSet<DiceSeat> sortedWinPlayers) {
		ResDiceWinInfo.Builder resDiceWinInfo =ResDiceWinInfo.newBuilder();
		table.resDiceWinInfo.clear();
		for(DiceType diceType:table.dices) {
			resDiceWinInfo.addDiceList(diceType.points);
			table.resDiceWinInfo.addDiceList(diceType.points);
		}
		resDiceWinInfo.setDiceType(table.getWinArea());
		table.resDiceWinInfo.setDiceType(table.getWinArea());
		if(table.banker!=null) {
			DiceSeatWinfo.Builder diceBankerWinfo=DiceSeatWinfo.newBuilder();
			long winGold=table.banker.winGold-table.banker.loseGold;
			diceBankerWinfo.setGold(winGold);
			diceBankerWinfo.setPlayerId(table.banker.playerId);
			diceBankerWinfo.setTotalBet(table.banker.totalBet);
			diceBankerWinfo.setTax(table.banker.tax);
			diceBankerWinfo.setNickName(table.banker.nickName);
			diceBankerWinfo.setIcon(table.banker.icon);
			resDiceWinInfo.setBanker(diceBankerWinfo);
			table.resDiceWinInfo.setBanker(diceBankerWinfo);
		}
		
		for(DiceSeat seat: sortedWinPlayers) {
			long winGold=seat.winGold-seat.loseGold;
			DiceSeatWinfo.Builder diceSeatWinfo=DiceSeatWinfo.newBuilder();
			diceSeatWinfo.setGold(winGold);
			diceSeatWinfo.setPlayerId(seat.playerId);
			diceSeatWinfo.setTotalBet(seat.totalBet);
			diceSeatWinfo.setNickName(seat.nickName);
			diceSeatWinfo.setTax(seat.tax);
			diceSeatWinfo.setIcon(seat.icon);
			resDiceWinInfo.addWinRankList(diceSeatWinfo);
			table.resDiceWinInfo.addWinRankList(diceSeatWinfo);
			if(resDiceWinInfo.getWinRankListCount()>=5) {
				break;
			}
		}
		for(DiceSeat seat:table.seats) {
			if(seat.playerId>0) {
				long winGold=seat.winGold-seat.loseGold;
				DiceSeatWinfo.Builder diceSeatWinfo=DiceSeatWinfo.newBuilder();
				diceSeatWinfo.setGold(winGold);
				diceSeatWinfo.setPlayerId(seat.playerId);
				diceSeatWinfo.setTotalBet(seat.totalBet);
				diceSeatWinfo.setTax(seat.tax);
				diceSeatWinfo.setNickName(seat.nickName);
				diceSeatWinfo.setIcon(seat.icon);
				resDiceWinInfo.setSeatWinfo(diceSeatWinfo);
				msgMgr.writeMsg(seat.playerId, MsgHandlerFactory.getProtocol("dice.ResDiceWinInfo"), resDiceWinInfo.build().toByteString());
			}
		}
		
		
	}
	public void sendResDiceRankList(long playerId,DiceTable table) {
		ResDiceRank.Builder resDiceWinInfo =ResDiceRank.newBuilder();
		int pdrSize=table.playerRanks.size();
		for(int i=0;i<pdrSize;i++) {
			PlayerDiceRankInfo pdr=table.playerRanks.get(i);
			DiceRank.Builder diceRank=DiceRank.newBuilder();
			diceRank.setGold(pdr.getGold());
			diceRank.setIcon(pdr.getIcon());
			diceRank.setNickName(pdr.getNickName());
			diceRank.setPlayerId(String.valueOf(pdr.getPlayerId()));
			resDiceWinInfo.addRankList(diceRank);
		}
		msgMgr.writeMsg(playerId, MsgHandlerFactory.getProtocol("dice.ResDiceRank"), resDiceWinInfo.build().toByteString());
	
	}
	public void sendResContinueBet(DiceSeat seat, int res) {
		ResContinueBet.Builder rescreateGobangRoom =ResContinueBet.newBuilder();
		rescreateGobangRoom.setRes(res);
		msgMgr.writeMsg(seat.playerId, MsgHandlerFactory.getProtocol("dice.ResContinueBet"), rescreateGobangRoom.build().toByteString());
	}
	public void sendResContinueBetMsg(DiceSeat seat) {
		ResContinueBetMsg.Builder resGobangStage=ResContinueBetMsg.newBuilder();
		resGobangStage.setPlayerId(seat.playerId);
		for(AreaBetInfo abi:seat.hisBet) {
			BetAreaInfo.Builder betInfo=BetAreaInfo.newBuilder();
			betInfo.setArea(abi.getArea());
			betInfo.setBetGold(abi.getGold());
			betInfo.setPlayerId(seat.playerId);
			resGobangStage.addBetList(betInfo);
		}
		sendTablePlayersMsg(seat.table, MsgHandlerFactory.getProtocol("dice.ResContinueBetMsg"), resGobangStage.build().toByteString());
	}
	public void sendResSixSeatMsg(DiceTable table) {
		ResSixSeatMsg.Builder resGobangStage=ResSixSeatMsg.newBuilder();
		List<PlayerDiceRecord> goldLike=table.getGodLike();
		long godLikeId=0;
		if(!goldLike.isEmpty()) {
			godLikeId=goldLike.get(0).playerId;
			DiceRank.Builder diceRank=DiceRank.newBuilder();
			diceRank.setGold(goldLike.get(0).totalBet);
			diceRank.setIcon(goldLike.get(0).icon);
			diceRank.setNickName(goldLike.get(0).nickName);
			diceRank.setPlayerId(String.valueOf(goldLike.get(0).playerId));
			resGobangStage.addRankList(diceRank);
		}
		resGobangStage.setGodLikeId(String.valueOf(godLikeId));
		int pdrSize=table.playerRanks.size();
		for (int i = 0; i < pdrSize; i++) {
			PlayerDiceRankInfo pdr = table.playerRanks.get(i);
			DiceRank.Builder diceRank = DiceRank.newBuilder();
			diceRank.setGold(pdr.getGold());
			diceRank.setIcon(pdr.getIcon());
			diceRank.setNickName(pdr.getNickName());
			diceRank.setPlayerId(String.valueOf(pdr.getPlayerId()));
			resGobangStage.addRankList(diceRank);
			if (resGobangStage.getRankListCount() >= 6) {
				break;
			}
		}
		sendTablePlayersMsg(table, MsgHandlerFactory.getProtocol("dice.ResSixSeatMsg"), resGobangStage.build().toByteString());
	}
	public void sendResBanker(Player player, int res) {
		ResBanker.Builder rescreateGobangRoom =ResBanker.newBuilder();
		rescreateGobangRoom.setRes(res);
		msgMgr.writeMsg(player.getId(), MsgHandlerFactory.getProtocol("dice.ResBanker"), rescreateGobangRoom.build().toByteString());
		
	}
	public void sendResPlayerBankerMsg(DiceTable table, ApplicantInfo applicantInfo) {
		ResPlayerBankerMsg.Builder resPlayerBankerMsg =ResPlayerBankerMsg.newBuilder();
		PlayerBankerInfo pbi=this.getPlayerBankerInfo(applicantInfo);
		resPlayerBankerMsg.setBankerInfo(pbi);
		sendTablePlayersMsg(table, MsgHandlerFactory.getProtocol("dice.ResPlayerBankerMsg"), resPlayerBankerMsg.build().toByteString());
	}
	private PlayerBankerInfo getPlayerBankerInfo(ApplicantInfo applicantInfo) {
		PlayerBankerInfo.Builder playerBankerInfo=PlayerBankerInfo.newBuilder();
		if(applicantInfo.getPlayerId()!=0) {
			playerBankerInfo.setBankerGold(applicantInfo.getGold());
			playerBankerInfo.setIcon(applicantInfo.getIcon());
			playerBankerInfo.setPlayerId(String.valueOf(applicantInfo.getPlayerId()));
			playerBankerInfo.setNickName(applicantInfo.getNickName());
		}
		return playerBankerInfo.build();
	}
//	public void sendResRobBankerInfo(DiceSeat seat, int res) {
//		ResRobBankerInfo.Builder resPlayerBankerMsg =ResRobBankerInfo.newBuilder();
//		PlayerBankerInfo pbi=this.getPlayerBankerInfo(seat.table.robBanker);
//		resPlayerBankerMsg.setBankerInfo(pbi);
//		resPlayerBankerMsg.setRes(res);
//		msgMgr.writeMsg(seat.playerId, MsgHandlerFactory.getProtocol("dice.ResRobBankerInfo"), resPlayerBankerMsg.build().toByteString());
//	}
}
