package com.alex.game.games.dice;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.alex.game.common.Game;
import com.alex.game.common.util.RandomUtil;
import com.alex.game.core.concurrent.TaskExecutor;
import com.alex.game.dice.DiceProto.ReqApplyBanker;
import com.alex.game.dice.DiceProto.ReqBetDice;
import com.alex.game.dice.DiceProto.ReqEnterDiceRoom;
import com.alex.game.dice.DiceProto.ReqExitBanker;
import com.alex.game.dice.DiceProto.ReqExitDiceRoom;
import com.alex.game.robot.common.PlayerPosition;
import com.alex.game.robot.core.Player;
import com.alex.game.robot.handler.MsgHandlerFactory;

public class DicePlayer extends Player {
	public boolean isApplyBanker=false;
	public List<Integer> ableBetScore = new ArrayList<>();
	public int betTimes;
	public DicePlayer(long playerId, String userName, TaskExecutor taskExecutor, String apiUrl) {
		super(playerId, userName, taskExecutor, apiUrl);
		ableBetScore.add(500);
		ableBetScore.add(1000);
		ableBetScore.add(10000);
		ableBetScore.add(100000);
		ableBetScore.add(1000000);
	}
	@Override
	public void schedule() {
	
		if (position == PlayerPosition.HALL) {
			enterRoom();
		}
	}
	/**
	 * 进入房间
	 */
	private void enterRoom() {
		 ReqEnterDiceRoom.Builder msg = ReqEnterDiceRoom.newBuilder();
		 sendMsg(MsgHandlerFactory.getProtocol("dice.ReqEnterDiceRoom"),msg.build().toByteString());
		
	}

	@Override
	public int game() {
		return Game.DICE.id;
	}

	public void betDice() {
		ReqBetDice.Builder msg = ReqBetDice.newBuilder();
		//1大2小3豹子
		int random = RandomUtil.random(100);
		if (random <= 45) {
			msg.setArea(1);
		}
		if(random>45&&random <= 95) {
			msg.setArea(2);
		}
		
		if(random>95) {
			msg.setArea(3);
		}
		int index = RandomUtil.random(0, 3);
		msg.setBetGold(ableBetScore.get(index));
		sendMsg(MsgHandlerFactory.getProtocol("dice.ReqBetDice"), msg.build().toByteString());
		
		if(this.betTimes>1000) {
			int randomTime=RandomUtil.random(betTimes*1000);
			betTimes=betTimes-randomTime;
			schedule(this::betDice, randomTime, TimeUnit.MILLISECONDS);
		}
	}

	public void exitRoom() {
		ReqExitDiceRoom.Builder msg = ReqExitDiceRoom.newBuilder();
		sendMsg(MsgHandlerFactory.getProtocol("dice.ReqExitDiceRoom"), msg.build().toByteString());
	}
	public void applyBanker() {
		ReqApplyBanker.Builder msg = ReqApplyBanker.newBuilder();
		sendMsg(MsgHandlerFactory.getProtocol("dice.ReqApplyBanker"), msg.build().toByteString());
	}
	public void exitBanker() {
		ReqExitBanker.Builder msg = ReqExitBanker.newBuilder();
		sendMsg(MsgHandlerFactory.getProtocol("dice.ReqExitBanker"), msg.build().toByteString());
	}
	public void reset() {
		this.isApplyBanker=false;
	}
}
