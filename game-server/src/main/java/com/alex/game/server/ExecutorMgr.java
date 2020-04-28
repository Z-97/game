/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alex.game.common.Game;
import com.alex.game.core.concurrent.TaskExecutor;
import com.alex.game.server.tcp.MsgHandlerFactory;
import com.google.inject.Singleton;

import io.netty.channel.Channel;

/**
 * 任务处理器管理
 *
 * @author Alex
 * @date 2016/7/11 16:49
 */
@Singleton
public class ExecutorMgr {
	private static final Logger LOG = LoggerFactory.getLogger(ExecutorMgr.class);
	// 登录Executor大小
	public static final int LOGIN_EXECUTOR_SIZE = 8;
	// 玩家数据保存Executor大小
	public static final int PLAYER_SAVE_EXECUTORS_SIZE = 16;
	// 广场Executor大小
	public static final int PLAZA_EXECUTORS_SIZE = 8;
	// 游戏Executor大小
	public static final int GAME_EXECUTORS_SIZE = 32;

	// 登录任务执行线程组,该线程组处理用户的任务，还未产生玩家的任务，如：用户登录，注册等用户任务。
	public final TaskExecutor[] loginExecutors = TaskExecutor.createExecutors("Login-Executor", LOGIN_EXECUTOR_SIZE);
	
	// 玩家数据保存线程,2的冥，考虑几万人同时在线8个玩家保存线程，即使是批量提交，一个数据量一般的玩家平均耗时大概1毫秒
	public final TaskExecutor[] playerSaveExecutors = TaskExecutor.createExecutors("Player-Save-Executor", PLAYER_SAVE_EXECUTORS_SIZE);
	
	// 广场任务Executor,该Executor处理所有广场(背包、商城、个人中心等)的消息
	public final TaskExecutor[] plazaExecutors =  TaskExecutor.createExecutors("Plaza-Executor", PLAZA_EXECUTORS_SIZE);
	
	// 游戏Executor
	public final TaskExecutor[] gameExecutors = TaskExecutor.createExecutors("Game-Executor", GAME_EXECUTORS_SIZE);
	
	/**
	 * 根据Channel获取登录TaskExecutor
	 *
	 * @param channel
	 * @return
	 */
	public TaskExecutor getLoginExecutor(Channel channel) {

		return loginExecutors[channel.hashCode() & (LOGIN_EXECUTOR_SIZE - 1)];
	}

	/**
	 * 获取广场Executor
	 *
	 * @return
	 */
	public TaskExecutor getPlazaExecutor(long playerId) {

		return plazaExecutors[(int)(playerId & (plazaExecutors.length - 1))];
	}
	
    /**
     * 根据val快速取模计算用于保存玩家数据的TaskExecutor
     * 
     * @param val
     * @return
     */
    public TaskExecutor playerSaveExecutor(long val) {
    	return playerSaveExecutors[(int)(val & (playerSaveExecutors.length - 1))];
    }

	/**
	 * 根据tableId获取Executor(eg:捕鱼高频游戏特殊处理)
	 *
	 * @param tableId
	 * @return
	 */
	public TaskExecutor getTableExecutor(int tableId) {

		return gameExecutors[tableId & (GAME_EXECUTORS_SIZE - 1)];
	}
	
	/**
	 * 根据获取游戏Executor
	 * 
	 * @param moduleId
	 * @param roomId
	 * @return
	 */
	public TaskExecutor getGameExecutor(Game game) {
		return getGameExecutor(MsgHandlerFactory.getModuleId(game.moduleId));
	}
	
	/**
	 * 根据moduleId和roomId获取游戏Executor
	 * 
	 * @param moduleId
	 * @return
	 */
	public TaskExecutor getGameExecutor(int moduleId) {
		return gameExecutors[moduleId & (GAME_EXECUTORS_SIZE - 1)];
	}
	
	/**
	 * 关闭
	 */
	public void shutdown() {
		// 登陆TaskExecutor立即关闭，未执行的task丢弃
		for (TaskExecutor executor : loginExecutors) {
			executor.shutdownNow();
		}
		
		// 广场TaskExecutor立即关闭，未执行的task丢弃
		for (TaskExecutor executor : plazaExecutors) {
			executor.shutdownNow();
		}
		
		// 具体游戏TaskExecutor立即关闭，未执行的task丢弃
		for (TaskExecutor executor : gameExecutors) {
			executor.shutdownNow();
		}
		
		// 玩家保存TaskExecutor立即关闭，未执行的task需要执行
		for (TaskExecutor executor : playerSaveExecutors) {
			executor.shutdownNow();
		}
		LOG.info("所有TaskExecutor关闭");
	}
}
