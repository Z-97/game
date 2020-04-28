package com.alex.game.plaza.handler;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.PlayerProto.ReqResetPwd;
import com.alex.game.player.struct.Player;
import com.alex.game.plaza.PersonalCenterMgr;
import com.alex.game.server.tcp.Ihandler;
import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;
/**
 * 重置密码
 * @author yejuhua
 *
 */
public class ReqResetPwdHandler implements Ihandler {

	@Inject
	private PersonalCenterMgr personalCenterMgr;
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		ReqResetPwd resetPwdReq =ReqResetPwd.parseFrom(msg.getContent());
		personalCenterMgr.resetPwd(player,resetPwdReq.getUserName(),resetPwdReq.getPhone(),resetPwdReq.getNewPwd(),resetPwdReq.getCode());
	}

}
