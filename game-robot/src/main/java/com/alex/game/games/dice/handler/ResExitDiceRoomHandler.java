package com.alex.game.games.dice.handler;

import com.alex.game.dice.DiceProto.ResExitDiceRoom;
import com.alex.game.games.dice.DicePlayer;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.robot.common.PlayerPosition;
import com.alex.game.robot.core.Player;
import com.alex.game.robot.handler.Ihandler;
import com.google.protobuf.InvalidProtocolBufferException;

public class ResExitDiceRoomHandler implements Ihandler {

	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		ResExitDiceRoom resExitDiceRoom = ResExitDiceRoom.parseFrom(msg.getContent());
		DicePlayer dicePlayer = (DicePlayer) player;
		if(resExitDiceRoom.getRes()==0) {
			dicePlayer.position=PlayerPosition.HALL;
		}
		
	}

}
