package com.alex.game.games.gobang.handler;

import com.alex.game.games.gobang.manager.GobangMgr;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.struct.Player;
import com.alex.game.server.tcp.Ihandler;
import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * 开始游戏
 * @author yejuhua
 *
 */
public class ReqReadyHandler implements Ihandler {

	@Inject
	private GobangMgr gobangMgr;
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		gobangMgr.readyGobang(player);

	}

}
