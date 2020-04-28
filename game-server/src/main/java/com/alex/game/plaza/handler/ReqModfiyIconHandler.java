package com.alex.game.plaza.handler;

import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.PlayerProto.ReqModfiyIcon;
import com.alex.game.player.struct.Player;
import com.alex.game.plaza.PersonalCenterMgr;
import com.alex.game.server.tcp.Ihandler;
import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;
/**
 * 修改头像
 * @author yejuhua
 *
 */
public class ReqModfiyIconHandler implements Ihandler {

	@Inject
	private PersonalCenterMgr personalCenterMgr;
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		ReqModfiyIcon reqModfiyIcon=ReqModfiyIcon.parseFrom(msg.getContent());
		personalCenterMgr.modfiyIcon(player, reqModfiyIcon.getIcomId());
	}

}
