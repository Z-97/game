package com.alex.game.plaza.customer.handler;

import com.alex.game.customer.CustomerProto.ReqAddCustomerMessage;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.struct.Player;
import com.alex.game.plaza.customer.manager.CustomerMgr;
import com.alex.game.server.tcp.Ihandler;
import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;

public class ReqAddCustomerMessageHandler implements Ihandler {
	@Inject
	private CustomerMgr customerMgr;
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		ReqAddCustomerMessage reqAddCustomerMessage=ReqAddCustomerMessage.parseFrom(msg.getContent());
		customerMgr.addCustomerMessage(player,reqAddCustomerMessage.getContent(),reqAddCustomerMessage.getPhone());

	}

}
