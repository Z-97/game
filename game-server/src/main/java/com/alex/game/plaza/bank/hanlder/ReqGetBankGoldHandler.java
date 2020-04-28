package com.alex.game.plaza.bank.hanlder;

import com.alex.game.bank.BankProto.ReqGetBankGold;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.struct.Player;
import com.alex.game.plaza.bank.manager.BankMgr;
import com.alex.game.server.tcp.Ihandler;
import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;

public class ReqGetBankGoldHandler implements Ihandler {

	@Inject
	private BankMgr bankMgr;
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		ReqGetBankGold reqGetBankGold=ReqGetBankGold.parseFrom(msg.getContent());
		bankMgr.reqGetBankGold(player,reqGetBankGold.getGold());
	}

}
