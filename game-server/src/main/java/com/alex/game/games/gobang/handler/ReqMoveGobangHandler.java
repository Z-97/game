package com.alex.game.games.gobang.handler;

import com.alex.game.games.gobang.manager.GobangMgr;
import com.alex.game.gobang.GobangProto.ReqMoveGobang;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.struct.Player;
import com.alex.game.server.tcp.Ihandler;
import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;

public class ReqMoveGobangHandler implements Ihandler {

	@Inject
	private GobangMgr gobangMgr;
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		ReqMoveGobang resMoveGobang=ReqMoveGobang.parseFrom(msg.getContent());
		gobangMgr.moveGobang(player,resMoveGobang.getX(),resMoveGobang.getY());
	}

}
