package com.alex.game.plaza.handler;

import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.struct.Player;
import com.alex.game.plaza.PersonalCenterMsgMgr;
import com.alex.game.server.tcp.Ihandler;
import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;

public class ReqGiftListHandler implements Ihandler {

	@Inject
	private PersonalCenterMsgMgr personalCenterMsgMgr;
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		personalCenterMsgMgr.sendResGiftList(player);
	}

}
