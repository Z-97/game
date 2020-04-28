package com.alex.game.plaza.handler;

import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.PlayerProto.ReqLoadPlayerInfo;
import com.alex.game.player.struct.Player;
import com.alex.game.plaza.PersonalCenterMgr;
import com.alex.game.server.tcp.Ihandler;
import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;
/**
 * 查看玩家信息
 * @author yejuhua
 *
 */
public class ReqLoadPlayerInfoHandler implements Ihandler {

	@Inject
	private PersonalCenterMgr personalCenterMgr;
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		ReqLoadPlayerInfo reqLoadPlayerInfo=ReqLoadPlayerInfo.parseFrom(msg.getContent());
		personalCenterMgr.loadPlayerInfo(player,reqLoadPlayerInfo.getPlayerId());
	}

}
