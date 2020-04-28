package com.alex.game.login.handler;
import com.alex.game.login.LoginProto.ReqTouristLogin;
import com.alex.game.login.manager.LoginMgr;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.struct.Player;
import com.alex.game.server.tcp.Ihandler;
import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;

public class ReqTouristLoginHandler implements Ihandler {

	@Inject
	private LoginMgr loginMgr;
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		ReqTouristLogin touristRegisterReq=ReqTouristLogin.parseFrom(msg.getContent());
		loginMgr.touristLogin(player,touristRegisterReq.getMac(),touristRegisterReq.getDevice(),touristRegisterReq.getDeviceId(),touristRegisterReq.getDeviceModel(),touristRegisterReq.getPackageId());

	}

}
