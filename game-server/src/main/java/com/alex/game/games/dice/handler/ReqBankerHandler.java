package com.alex.game.games.dice.handler;

import com.alex.game.dice.DiceProto.ReqBanker;
import com.alex.game.games.dice.manager.DiceMgr;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.struct.Player;
import com.alex.game.server.tcp.Ihandler;
import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;

public class ReqBankerHandler implements Ihandler {

	@Inject
	private DiceMgr diceMgr;
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		ReqBanker reqBetDice=ReqBanker.parseFrom(msg.getContent());
		diceMgr.robBanker(player,reqBetDice.getGold());
	}

}
