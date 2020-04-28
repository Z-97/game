package com.alex.game.plaza.bank.hanlder;

import com.alex.game.bank.BankProto.ReqEnterBank;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.struct.Player;
import com.alex.game.plaza.bank.manager.BankMgr;
import com.alex.game.server.tcp.Ihandler;
import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;

public class ReqEnterBankHandler implements Ihandler {

	@Inject
	private BankMgr bankMgr;
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		ReqEnterBank reqEnterBank=ReqEnterBank.parseFrom(msg.getContent());
		bankMgr.enterBank(player,reqEnterBank.getBankPwd());
	}

}
