package com.alex.game.plaza.bank.hanlder;

import com.alex.game.bank.BankProto.ReqGiftPresent;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.struct.Player;
import com.alex.game.plaza.bank.manager.BankMgr;
import com.alex.game.server.tcp.Ihandler;
import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;
/**
 * 赠送礼物
 * @author yejuhua
 *
 */
public class ReqGiftPresentHandler implements Ihandler {

	@Inject
	private BankMgr bankMgr;
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		ReqGiftPresent reqGiftPresent=ReqGiftPresent.parseFrom(msg.getContent());
		bankMgr.sendGift(player,reqGiftPresent.getToPlayerId(),reqGiftPresent.getItemId(),reqGiftPresent.getItemNum());
	}

}
