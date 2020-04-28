package com.alex.game.plaza.customer.manager;

import java.util.List;

import com.alex.game.customer.CustomerProto.CustomerMessagePB;
import com.alex.game.customer.CustomerProto.ResAddCustomerMessage;
import com.alex.game.customer.CustomerProto.ResCustomerMessage;
import com.alex.game.customer.CustomerProto.ResReplyCustomerMessage;
import com.alex.game.dbdata.dom.CustomerMessage;
import com.alex.game.player.manager.PlayerMsgMgr;
import com.alex.game.player.struct.Player;
import com.alex.game.server.tcp.MsgHandlerFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class CustomerMsgMgr {
	@Inject
	private PlayerMsgMgr msgMgr;
	public void sendResAddCustomerMessage(Player player,int res) {
		ResAddCustomerMessage.Builder resAddCustomerMessage =ResAddCustomerMessage.newBuilder();
		resAddCustomerMessage.setRes(res);
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("customer.ResAddCustomerMessage"), resAddCustomerMessage.build().toByteString());
	}
	public void sendResReplyCustomerMessage(Player player,CustomerMessage customerMessage) {
		ResReplyCustomerMessage.Builder resAddCustomerMessage =ResReplyCustomerMessage.newBuilder();
		CustomerMessagePB.Builder customerMessagePB=CustomerMessagePB.newBuilder();
		customerMessagePB.setId(customerMessage.getId());
		customerMessagePB.setContent(customerMessage.getContent());
		customerMessagePB.setPhone(customerMessage.getPhone());
		customerMessagePB.setPlayerId(customerMessage.getPlayerId());
		customerMessagePB.setDate(customerMessage.getSendTime().getTime()/1000);
		resAddCustomerMessage.setCustomerMessage(customerMessagePB);
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("customer.ResReplyCustomerMessage"), resAddCustomerMessage.build().toByteString());
	}
	/**
	 * 发送客服消息列表
	 * @param player
	 * @param customerMessageList
	 */
	public void sendResCustomerMessage(Player player, List<CustomerMessage> customerMessageList) {
		ResCustomerMessage.Builder resAddCustomerMessage =ResCustomerMessage.newBuilder();
		if(customerMessageList!=null&&!customerMessageList.isEmpty()) {
			for(CustomerMessage customerMessage:customerMessageList) {
				CustomerMessagePB.Builder customerMessagePB=CustomerMessagePB.newBuilder();
				customerMessagePB.setId(customerMessage.getId());
				customerMessagePB.setContent(customerMessage.getContent());
				customerMessagePB.setDate(customerMessage.getSendTime().getTime()/1000);
				customerMessagePB.setPlayerId(customerMessage.getPlayerId());
				resAddCustomerMessage.addCustomerMessageList(customerMessagePB);
			}
			
		}
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("customer.ResCustomerMessage"), resAddCustomerMessage.build().toByteString());
	}
}
