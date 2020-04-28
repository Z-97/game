package com.alex.game.plaza;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alex.game.core.concurrent.TaskExecutor;
import com.alex.game.email.EmailProto.ReqEmailList;
import com.alex.game.robot.core.Player;
import com.alex.game.robot.handler.MsgHandlerFactory;

/**
 * 大厅机器人
 * @author yejuhua
 *
 */
public class PlazaPlayer extends Player {
	private static final Logger LOG = LoggerFactory.getLogger(PlazaPlayer.class);
	public PlazaPlayer(long playerId, String userName, TaskExecutor taskExecutor, String apiUrl) {
		super(playerId, userName, taskExecutor, apiUrl);
	}
	@Override
	public void schedule() {
	
	}

	@Override
	public int game() {
		return 0;
	}
	/***邮件相关***/
	private void reqEmailList() {
		ReqEmailList.Builder msg=ReqEmailList.newBuilder();
		sendMsg(MsgHandlerFactory.getProtocol("email.ReqEmailList"), msg.build().toByteString());
	}

	private void reqReadEmail(int emailId) {
		
	}
}
