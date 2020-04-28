package com.alex.game.plaza.handler;

import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.PlayerProto.ReqModfiyNickName;
import com.alex.game.player.struct.Player;
import com.alex.game.plaza.PersonalCenterMgr;
import com.alex.game.server.tcp.Ihandler;
import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;
/**
 * 修改昵称
 * @author yejuhua
 *
 */
public class ReqModfiyNickNameHandler implements Ihandler {

	@Inject
	private PersonalCenterMgr personalCenterMgr;
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		
		ReqModfiyNickName reqModfiyNickName=ReqModfiyNickName.parseFrom(msg.getContent());
		personalCenterMgr.modfiyNickName(player,reqModfiyNickName.getNickName());
	}

}
