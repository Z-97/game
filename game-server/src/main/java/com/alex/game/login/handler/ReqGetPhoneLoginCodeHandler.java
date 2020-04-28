package com.alex.game.login.handler;
import com.alex.game.login.LoginProto.ReqGetPhoneLoginCode;
import com.alex.game.login.manager.LoginMgr;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.struct.Player;
import com.alex.game.server.tcp.Ihandler;
import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * 发送验证码
 * @author yejuhua
 *
 */
public class ReqGetPhoneLoginCodeHandler implements Ihandler {

	@Inject
	private LoginMgr loginMgr;
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		ReqGetPhoneLoginCode getPhoneLoginCode=ReqGetPhoneLoginCode.parseFrom(msg.getContent());
		loginMgr.refreshPhoneCode(player, getPhoneLoginCode.getPhone());
	}

}
