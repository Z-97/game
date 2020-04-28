package com.alex.game.login.handler;

import com.alex.game.login.manager.LoginMsgMgr;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.struct.Player;
import com.alex.game.server.tcp.Ihandler;
import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;
/**
 * 获取代理列表
 * @author yejuhua
 *
 */
public class ReqAgentListHandler implements Ihandler {
	@Inject
	private LoginMsgMgr loginMsgMgr;
	
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		loginMsgMgr.sendAgentList(player);

	}

}
