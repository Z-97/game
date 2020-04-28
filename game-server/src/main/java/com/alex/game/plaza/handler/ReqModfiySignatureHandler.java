package com.alex.game.plaza.handler;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.PlayerProto.ReqModfiySignature;
import com.alex.game.player.struct.Player;
import com.alex.game.plaza.PersonalCenterMgr;
import com.alex.game.server.tcp.Ihandler;
import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;

public class ReqModfiySignatureHandler implements Ihandler {
	@Inject
	private PersonalCenterMgr personalCenterMgr;
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		ReqModfiySignature reqModfiySignature=ReqModfiySignature.parseFrom(msg.getContent());
		personalCenterMgr.reqModfiySignature(player,reqModfiySignature.getSignature());
	}

}
