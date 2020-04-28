package com.alex.game.login.handler;
import com.alex.game.login.LoginProto.ReqTouristRegister;
import com.alex.game.login.manager.LoginMgr;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.struct.Player;
import com.alex.game.server.tcp.Ihandler;
import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;

public class ReqTouristRegisterHandler implements Ihandler {

	@Inject
	private LoginMgr loginMgr;
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		ReqTouristRegister touristRegisterReq=ReqTouristRegister.parseFrom(msg.getContent());
		loginMgr.touristRegister(player,touristRegisterReq.getDevice(),touristRegisterReq.getDeviceId(),touristRegisterReq.getDeviceModel());
	}

}
