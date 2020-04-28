package com.alex.game.games.dice.handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alex.game.dice.DiceProto.ResEnterDiceRoom;
import com.alex.game.games.dice.DicePlayer;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.robot.common.PlayerPosition;
import com.alex.game.robot.core.Player;
import com.alex.game.robot.handler.Ihandler;
import com.google.protobuf.InvalidProtocolBufferException;

public class ResEnterDiceRoomHandler implements Ihandler {
	private static final Logger LOG = LoggerFactory.getLogger(ResEnterDiceRoomHandler.class);
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		ResEnterDiceRoom resEnterDiceRoomMsg=ResEnterDiceRoom.parseFrom(msg.getContent());
		DicePlayer dicePlayer = (DicePlayer) player;
		if(resEnterDiceRoomMsg.getRes()==0) {
			dicePlayer.position=PlayerPosition.ROOM;
			LOG.warn("玩家[{}][{}]进入房间成功", player.id,player.data.getNickName());
		}
	
	}

}
