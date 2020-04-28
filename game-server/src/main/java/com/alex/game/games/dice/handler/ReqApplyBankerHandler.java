package com.alex.game.games.dice.handler;
import com.alex.game.dice.DiceProto.ReqApplyBanker;
import com.alex.game.games.dice.manager.DiceMgr;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.struct.Player;
import com.alex.game.server.tcp.Ihandler;
import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;
public class ReqApplyBankerHandler implements Ihandler {

	@Inject
	private DiceMgr diceMgr;
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		ReqApplyBanker reqBetDice=ReqApplyBanker.parseFrom(msg.getContent());
		diceMgr.applyBanker(player,reqBetDice.getBankerGold());
	}

}
