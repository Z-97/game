package com.alex.game.games.dice.handler;

import com.alex.game.dice.DiceProto.ResApplyBanker;
import com.alex.game.games.dice.DicePlayer;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.robot.core.Player;
import com.alex.game.robot.handler.Ihandler;
import com.google.protobuf.InvalidProtocolBufferException;

public class ResApplyBankerHandler implements Ihandler {

	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		ResApplyBanker resEnterDiceRoomMsg=ResApplyBanker.parseFrom(msg.getContent());
		DicePlayer dicePlayer = (DicePlayer) player;
		if(resEnterDiceRoomMsg.getRes()==0) {
			dicePlayer.isApplyBanker=true;
		}

	}

}
