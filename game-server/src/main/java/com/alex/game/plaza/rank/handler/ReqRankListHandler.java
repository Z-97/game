package com.alex.game.plaza.rank.handler;

import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.struct.Player;
import com.alex.game.plaza.rank.manager.RankMgr;
import com.alex.game.server.tcp.Ihandler;
import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;

public class ReqRankListHandler implements Ihandler {

	@Inject
	private RankMgr rankMgr;
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		rankMgr.loadAgentGoldRankList(player);
	}

}
