package com.alex.game.login.handler;

import com.alex.game.login.LoginProto.ReqRegister;
import com.alex.game.login.manager.LoginMgr;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.struct.Player;
import com.alex.game.server.tcp.Ihandler;
import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;
/**
 * 注册
 * @author yejuhua
 *
 */
public class ReqRegisterHandler implements Ihandler {
	@Inject
	private LoginMgr loginMgr;
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		ReqRegister reqRegister=ReqRegister.parseFrom(msg.getContent());
		loginMgr.register(player, reqRegister.getUserName(),reqRegister.getPwd(),reqRegister.getDevice(),reqRegister.getDeviceId(),reqRegister.getDeviceModel(),reqRegister.getTouristMac());

	}

}
