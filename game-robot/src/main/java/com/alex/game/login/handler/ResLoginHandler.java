package com.alex.game.login.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alex.game.login.LoginProto.ResLogin;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.robot.common.PlayerPosition;
import com.alex.game.robot.core.Player;
import com.alex.game.robot.handler.Ihandler;
import com.google.protobuf.InvalidProtocolBufferException;

public class ResLoginHandler implements Ihandler {
	private static final Logger LOG = LoggerFactory.getLogger(ResLoginHandler.class);
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
      ResLogin resLogin = ResLogin.parseFrom(msg.getContent());
//    0:成功,1:用户名不存在,2:登陆key错误,3:密码错误
        LOG.info("resLogin.getRes()"+resLogin.getRes());
		if (resLogin.getRes() == 0) {
			player.position = PlayerPosition.HALL;
	        LOG.info("玩家[{}]登陆成功耗时[{}]毫秒", player.id, System.currentTimeMillis() - player.sendLoginMsgTime);
		} else if (resLogin.getRes() == 1) {
			LOG.warn("玩家[{}]登陆失败,用户名不存在", player.id);
			player.close();
		} else if (resLogin.getRes() == 2) {
			LOG.warn("玩家[{}]登陆失败,登陆key错误", player.id);
			player.close();
		} else if (resLogin.getRes() == 3) {
			LOG.warn("玩家[{}]登陆失败,密码错误", player.id);
			player.close();
		}

	}

}
