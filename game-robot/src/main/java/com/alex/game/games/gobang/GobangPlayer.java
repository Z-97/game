package com.alex.game.games.gobang;

import com.alex.game.core.concurrent.TaskExecutor;
import com.alex.game.robot.core.Player;

/**
 * 五子棋机器人
 * @author yejuhua
 *
 */
public class GobangPlayer extends Player {

	public GobangPlayer(long playerId, String userName, TaskExecutor taskExecutor, String apiUrl) {
		super(playerId, userName, taskExecutor, apiUrl);

	}

	@Override
	public void schedule() {
		

	}

	@Override
	public int game() {
		
		return 0;
	}

}
