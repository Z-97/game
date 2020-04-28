package com.alex.game.plaza.redpackage.manager;

import java.util.ArrayList;
import java.util.List;

import com.alex.game.dbdata.dom.Redpackage;
import com.alex.game.games.dice.struct.DiceSeat;
import com.alex.game.games.dice.struct.DiceTable;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.manager.PlayerMsgMgr;
import com.alex.game.player.struct.Player;
import com.alex.game.plaza.redpackage.struct.PlayerGetRedpackage;
import com.alex.game.redpackage.RedpackageProto.PlayerRedPackage;
import com.alex.game.redpackage.RedpackageProto.RedPackageInfo;
import com.alex.game.redpackage.RedpackageProto.ResOpenPackage;
import com.alex.game.redpackage.RedpackageProto.ResPackageInfo;
import com.alex.game.redpackage.RedpackageProto.ResRedPackageMsg;
import com.alex.game.server.tcp.MsgHandlerFactory;
import com.alibaba.fastjson.JSON;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.protobuf.ByteString;

@Singleton
public class RedpackageMsgMgr {

	@Inject
	private PlayerMsgMgr msgMgr;
	/**
	 * 
	 * @param table
	 * @param redpackageId
	 */
	public void sendDiceRedPackage(DiceTable table, Integer redpackageId) {
		ResRedPackageMsg.Builder resRedPackageMsg=ResRedPackageMsg.newBuilder();
		resRedPackageMsg.setRedpackageId(redpackageId);
		sendTablePlayersMsg(table, MsgHandlerFactory.getProtocol("redpackage.ResRedPackageMsg"), resRedPackageMsg.build().toByteString());
		
	}
	/**
	 * 向骰同桌的玩家(包括自己)发送消息
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
	public void sendResOpenPackage(Player player, int res, Redpackage redpackage) {
		ResOpenPackage.Builder resRedPackageMsg=ResOpenPackage.newBuilder();
		resRedPackageMsg.setRes(res);
		if(redpackage!=null) {
			resRedPackageMsg.setRedPackageInfo(getRedPackageInfo(redpackage));
		}
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("redpackage.ResOpenPackage"), resRedPackageMsg.build().toByteString());
	}
	
	private RedPackageInfo getRedPackageInfo(Redpackage redpackage) {
		RedPackageInfo.Builder resRedPackageMsg=RedPackageInfo.newBuilder();
		resRedPackageMsg.setId(redpackage.getId());
		resRedPackageMsg.setNum(redpackage.getNum());
		resRedPackageMsg.setRemainder(redpackage.getRemainder());
		resRedPackageMsg.setSum(redpackage.getSum());
		if(redpackage.getCompleteTime()!=null) {
			resRedPackageMsg.setCompleteTime(redpackage.getCompleteTime().getTime()-redpackage.getCreateTime().getTime());
		}
		List<PlayerGetRedpackage> redList=getPlayerGetRedpackageList(redpackage);
	    if(!redList.isEmpty()){
	    	for(PlayerGetRedpackage ogrp:redList) {
	    		PlayerRedPackage prd=getPlayerRedPackage(ogrp);
	    		resRedPackageMsg.addRankList(prd);
	    	}
			
		}
		return resRedPackageMsg.build();
	}
	private PlayerRedPackage getPlayerRedPackage(PlayerGetRedpackage ogrp) {
		PlayerRedPackage.Builder playerRedPackage=PlayerRedPackage.newBuilder();
		playerRedPackage.setIcon(ogrp.getIcon());
		playerRedPackage.setNickName(ogrp.getNickName());
		playerRedPackage.setPlayerId(String.valueOf(ogrp.getId()));
		playerRedPackage.setRedPackageGold(ogrp.getRedPackageGold());
		playerRedPackage.setGetRedpackageTime(ogrp.getGetDate().getTime());
		return playerRedPackage.build();
	}
	public void sendRedPackageInfo(Player player, int res, Redpackage redpackage) {
		ResPackageInfo.Builder resRedPackageMsg=ResPackageInfo.newBuilder();
		resRedPackageMsg.setRes(res);
		
		if(redpackage!=null) {
			resRedPackageMsg.setRedPackageInfo(getRedPackageInfo(redpackage));
		}
		
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("redpackage.ResPackageInfo"), resRedPackageMsg.build().toByteString());
		
	}
	/**
	 * 获取领取详情
	 * @param redpackage
	 * @return
	 */
	public List<PlayerGetRedpackage> getPlayerGetRedpackageList(Redpackage redpackage){
		if(redpackage.getRedpackageDesc()!=null) {
			return JSON.parseArray(redpackage.getRedpackageDesc(), PlayerGetRedpackage.class);
		}else {
			return new ArrayList<PlayerGetRedpackage> ();
		}
		
	}
}
