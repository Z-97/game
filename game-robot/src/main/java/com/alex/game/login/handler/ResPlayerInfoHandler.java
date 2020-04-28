package com.alex.game.login.handler;

import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.PlayerProto.ResPlayerInfo;
import com.alex.game.robot.core.Player;
import com.alex.game.robot.handler.Ihandler;
import com.google.protobuf.InvalidProtocolBufferException;

public class ResPlayerInfoHandler implements Ihandler {

	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		System.out.println("ResPlayerInfoHandler");
		ResPlayerInfo playerInfo=ResPlayerInfo.parseFrom(msg.getContent());
		player.data=playerInfo.getPlayer();
	}

}
