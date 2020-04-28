package com.alex.game.schedule.cron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alex.game.core.concurrent.TaskExecutor;
import com.alex.game.dblog.core.DbLogService;
import com.alex.game.games.dice.manager.DiceDataMgr;
import com.alex.game.games.gobang.manager.GobangDataMgr;
import com.alex.game.player.manager.PlayerMgr;
import com.alex.game.schedule.cron.base.CronTask;
import com.alex.game.server.ExecutorMgr;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 监控任务
 * 
 * @author Alex
 * @date 2017年4月7日 下午3:19:19
 */
@Singleton
public class ServerMointorTask implements CronTask {

	// 监控日志
	private static final Logger MOINTOR_LOG = LoggerFactory.getLogger("MointorLog");
	@Inject
	private DiceDataMgr diceDataMgr;
	@Inject
	private GobangDataMgr gobangDataMgr;
	@Inject
	private PlayerMgr playerMgr;
	@Inject
	private ExecutorMgr executorMgr;
	@Inject
	@Override
	public void run() {
		
		// 服务器在线人数
		long onLinePlayersNum = playerMgr.onLinePlayers().size();
		// 服务器活跃人数
		long activePlayersNum = playerMgr.activePlayers().size();
		// 五子棋在线人数
		int gobangPlayersNum = gobangDataMgr.playersNum();
		// 骰子在线人数
		int dicePlayersNum = diceDataMgr.playersNum();
		// 游戏任务数
		long gameTaskNum = 0;
		TaskExecutor[] gameExecutors = executorMgr.gameExecutors;
		for (int i = 0; i < gameExecutors.length; i++) {
			gameTaskNum += gameExecutors[i].getQueue().size();
		}
		// 登陆注册任务数
		long loginTaskNum = 0;
		TaskExecutor[] loginExecutors = executorMgr.loginExecutors;
		for (int i = 0; i < loginExecutors.length; i++) {
			loginTaskNum += loginExecutors[i].getQueue().size();
		}
		// 数据库日志任务数
		long dbLogTaskNum = DbLogService.queueSize();
		// 玩家异步保存任务数
		long playerSaveTaskNum = 0;
		TaskExecutor[] playerSaveExecutors = executorMgr.playerSaveExecutors;
		for (int i = 0; i < playerSaveExecutors.length; i++) {
			playerSaveTaskNum += playerSaveExecutors[i].getQueue().size();
		}
		// 广场任务数
		long plazaTaskNum = 0;
		TaskExecutor[] plazaExecutors = executorMgr.plazaExecutors;
		for (int i = 0; i < plazaExecutors.length; i++) {
			plazaTaskNum += plazaExecutors[i].getQueue().size();
		}

		MOINTOR_LOG.info(
				"服务器在线[{}],活跃[{}],游戏Task[{}],数据库日志Task[{}],登陆注册Task[{}],玩家数据异步保存Task[{}],广场Task[{}],"
				+ "五子棋在线[{}],   骰子在线[{}]",
				onLinePlayersNum, activePlayersNum, gameTaskNum, dbLogTaskNum, loginTaskNum, playerSaveTaskNum,
				plazaTaskNum, gobangPlayersNum, dicePlayersNum);
	}

	@Override
	public String cron() {
		return "*/10 * * * * ?";
	}

}
