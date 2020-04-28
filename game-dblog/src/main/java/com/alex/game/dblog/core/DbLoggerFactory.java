package com.alex.game.dblog.core;

import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.io.ResolverUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DbLogger工厂，管理所有的DbLogger
 * 
 * @author Alex
 * @date 2017年4月1日 下午11:50:36
 */
public class DbLoggerFactory {
	
	private static final Logger LOG = LoggerFactory.getLogger(DbLoggerFactory.class);

	// 数据库日志class定义包
	private static final String LOG_PKG = "com.alex.game";
	// 缓存所有的DbLogger
	public static final ConcurrentHashMap<Class<? extends DbLog>, DbLogger> LOGGERS = new ConcurrentHashMap<>();
	
	/**
	 * 扫描包注册所有数据库日志Logger，校验正在写的日志表的列定义是否正确，表是否存在，在开发阶段偶尔会调整字段类型,长度等
	 */
	public static void registerAllLoggers() {
		try {
			ResolverUtil<DbLog> resolver = new ResolverUtil<DbLog>().find(new ResolverUtil.IsA(DbLog.class), LOG_PKG);
			Date now = new Date();
			for (Class<? extends DbLog> logType : resolver.getClasses()) {
				if (!Modifier.isAbstract(logType.getModifiers())) {// 非抽象类才注册、校验
					DbLogger logger = DbLogger.create(logType, now);
					LOGGERS.put(logType, logger);
					// 校验日志表，1.日志表不存在则新增,2.日志调整字段后，需要调整当前日志表的定义
					logger.validate();
				}
			}
		} catch (Exception e) {
			LOG.error("扫描[" + LOG_PKG + "]注册DbLogger错误", e);
			System.exit(1);
		}
	}
	
	/**
	 * 更新DbLogger缓存
	 * 
	 * @param logger
	 */
	public static void updateLogger(DbLogger logger) {
		LOGGERS.put(logger.type, logger);
	}
	
	/**
	 * 获取DbLogger
	 * 
	 * @param type
	 * @return
	 */
	public static DbLogger getLogger(Class<? extends DbLog> type) {
		
		return LOGGERS.get(type);
	}
	
}
