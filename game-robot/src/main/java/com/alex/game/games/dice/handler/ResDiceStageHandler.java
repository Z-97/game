package com.alex.game.games.dice.handler;

import java.util.concurrent.TimeUnit;
import com.alex.game.common.util.RandomUtil;
import com.alex.game.common.util.TimeUtil;
import com.alex.game.dice.DiceProto.ResDiceStage;
import com.alex.game.games.dice.DicePlayer;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.robot.common.PlayerPosition;
import com.alex.game.robot.core.Player;
import com.alex.game.robot.handler.Ihandler;
import com.google.protobuf.InvalidProtocolBufferException;

public class ResDiceStageHandler implements Ihandler {
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		ResDiceStage resDiceStage = ResDiceStage.parseFrom(msg.getContent());
		int nextTime = resDiceStage.getTime() - TimeUtil.getCurrentSeconds();

		DicePlayer dicePlayer = (DicePlayer) player;
		dicePlayer.betTimes = nextTime;
		if (dicePlayer.position == PlayerPosition.HALL) {
			return;
		}
		// 3游戏4结算
		if (resDiceStage.getStage() == 2) {
			int random = RandomUtil.random(100);
			if (random <= 80) {
				int randomTime = RandomUtil.random(2000, dicePlayer.betTimes * 1000);
				dicePlayer.betTimes = dicePlayer.betTimes - randomTime;
				dicePlayer.schedule(dicePlayer::betDice, randomTime, TimeUnit.MILLISECONDS);
				return;
			}
			if (random <= 90) {
				if (dicePlayer.isApplyBanker) {
					int randomExit = RandomUtil.random(100);
					if (randomExit <= 50) {
						dicePlayer.schedule(dicePlayer::exitBanker, RandomUtil.random(nextTime), TimeUnit.SECONDS);
						return;
					}
				}
				dicePlayer.schedule(dicePlayer::applyBanker, RandomUtil.random(nextTime), TimeUnit.SECONDS);
			}
		}
		if (resDiceStage.getStage() == 3) {
			dicePlayer.reset();
			int random = RandomUtil.random(100);
			if (random <= 10) {
				dicePlayer.schedule(dicePlayer::exitRoom, RandomUtil.random(nextTime), TimeUnit.SECONDS);
				return;
			}
			return;
		}
	}

}
