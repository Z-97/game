package com.alex.game.plaza.email.handler;

import com.alex.game.email.EmailProto.ReqReadEmail;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.struct.Player;
import com.alex.game.plaza.email.manager.EmailMgr;
import com.alex.game.server.tcp.Ihandler;
import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * 查看邮件
 * @author yejuhua
 *
 */
public class ReqReadEmailHandler implements Ihandler {

	@Inject
	private EmailMgr emailMgr;
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		ReqReadEmail reqReadEmail=ReqReadEmail.parseFrom(msg.getContent());
		emailMgr.readEmail(player,reqReadEmail.getEmailId());
	}

}
