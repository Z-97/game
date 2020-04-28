package com.alex.game.plaza.customer.handler;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.struct.Player;
import com.alex.game.plaza.customer.manager.CustomerMgr;
import com.alex.game.server.tcp.Ihandler;
import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;
/**
 * 
 * @author yejuhua
 *
 */
public class ReqCustomerMessageHandler implements Ihandler {
	@Inject
	private CustomerMgr customerMgr;
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		customerMgr.loadCustomerMessage(player);
	}

}
