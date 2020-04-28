package com.alex.game.plaza.redpackage.handler;

import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.struct.Player;
import com.alex.game.plaza.redpackage.manager.RedpackageMgr;
import com.alex.game.redpackage.RedpackageProto.ReqOpenPackage;
import com.alex.game.server.tcp.Ihandler;
import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;

public class ReqOpenPackageHandler implements Ihandler {

	@Inject
	private RedpackageMgr redpackageMgr;
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		ReqOpenPackage reqOpenPackage=ReqOpenPackage.parseFrom(msg.getContent());
		redpackageMgr.getRedPackage(player, reqOpenPackage.getRedpackageId());
	}

}
