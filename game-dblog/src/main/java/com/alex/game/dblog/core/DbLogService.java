/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.dblog.core;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alex.game.core.concurrent.TaskExecutor;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * 数据库日志记录服务
 *
 * @author Alex
 * @date 2016/7/4 10:02
 */
public class DbLogService {
	
	private static final Logger LOG = LoggerFactory.getLogger(DbLogService.class);

	// jdbc配置文件
	private static final String JDBC_PATH = "config/jdbc/logdb.properties";
	// 记录日志线程数,需要为2的冥,快速取模
    private static final int THREADS = 16;
    
	// 数据源
	private static final DataSource DS = new HikariDataSource(new HikariConfig(JDBC_PATH));
	// 数据库日志服务线程执行器
	private static final TaskExecutor[] EXECUTORS = TaskExecutor.createExecutors("DbLogger", THREADS);

	private DbLogService() {
	}

	/**
	 * 获取队列大小
	 * 
	 * @return
	 */
	public static long queueSize() {
		long size = 0;
		for (TaskExecutor executor : EXECUTORS) {
			size += executor.getQueue().size();
		}
		return size;
	}
	
	/**
	 * 纪录数据库日志(异步的)
	 *
	 * @param log
	 */
	public static void log(DbLog log) {
		if (log == null) {
			return;
		}
		DbLogger logger = DbLoggerFactory.getLogger(log.getClass());
		TaskExecutor executor = EXECUTORS[log.hashCode() & (THREADS - 1)];
		
		long time = log.time.getTime();
		if (time >= logger.startTime && time < logger.endTime) {
			logger.addLog(log);
			// 异步记录日志
			executor.execute(() -> logger.log());
		} else {
			DbLogger newLogger = DbLogger.create(log.getClass(), log.time);
			newLogger.createDbTbl();
			newLogger.addLog(log);
			DbLoggerFactory.updateLogger(newLogger);
			// 异步记录日志
			executor.execute(() -> newLogger.log());
		}
	}

	/**
	 * 关闭数据库日志服务
	 */
	public static void shutdown() {
		LOG.info("正在关闭数据库日志服务...");
		long now = System.currentTimeMillis();
		for (TaskExecutor executor : EXECUTORS) {
			// 执行队列中的task
			for (Runnable r : executor.shutdownNow()) {
				r.run();
			}
		}
		LOG.info("关闭数据库日志服务成功,耗时[{}]毫秒", System.currentTimeMillis() - now);
	}

	/**
	 * 获取数据库连接
	 *
	 * @return
	 * @throws SQLException 
	 * @throws ex
	 */
	public static Connection getConnection() throws SQLException {
		return DS.getConnection();
	}

}
