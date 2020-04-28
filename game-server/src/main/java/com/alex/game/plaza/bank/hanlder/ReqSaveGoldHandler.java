package com.alex.game.plaza.bank.hanlder;

import com.alex.game.bank.BankProto.ReqSaveGold;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.struct.Player;
import com.alex.game.plaza.bank.manager.BankMgr;
import com.alex.game.server.tcp.Ihandler;
import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;
/**
 * 银行存金币
 * @author yejuhua
 *
 */
public class ReqSaveGoldHandler implements Ihandler {

	@Inject
	private BankMgr bankMgr;
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		ReqSaveGold reqSaveGold=ReqSaveGold.parseFrom(msg.getContent());
		bankMgr.saveBankGold(player,reqSaveGold.getGold());
	}

}
