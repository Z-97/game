package com.alex.game.plaza.email.handler;

import com.alex.game.email.EmailProto.ReqDelEmail;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.struct.Player;
import com.alex.game.plaza.email.manager.EmailMgr;
import com.alex.game.server.tcp.Ihandler;
import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * 删除邮件
 * @author yejuhua
 *
 */
public class ReqDelEmailHandler implements Ihandler {

	@Inject
	private EmailMgr emailMgr;
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		ReqDelEmail reqDelEmail=ReqDelEmail.parseFrom(msg.getContent());
		emailMgr.delEmail(player,reqDelEmail.getEmailId());
	}

}
