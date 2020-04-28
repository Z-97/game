package com.alex.game.login.handler;

import com.alex.game.login.manager.LoginMsgMgr;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.struct.Player;
import com.alex.game.server.tcp.Ihandler;
import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;

public class ReqPingHandler implements Ihandler {


	@Inject
	private LoginMsgMgr loginMsgMgr;
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		loginMsgMgr.sendPong(player);
	}

}
