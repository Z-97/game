package com.alex.game.schedule.cron;

import com.alex.game.dblog.core.DbLogger;
import com.alex.game.dblog.core.DbLoggerFactory;
import com.alex.game.schedule.cron.base.CronTask;

/**
 * 创建dblog表任务,提前下一张日志表生成,后台统计需要表不为空
 * 
 * @author Alex
 * @date 2017年5月30日 下午10:44:13
 */
public class CreateNextDblogTblTask implements CronTask{

	@Override
	public void run() {
		for (DbLogger dbLogger : DbLoggerFactory.LOGGERS.values()) {
			dbLogger.createNextDbTbl();
		}
	}

	@Override
	public String cron() {
		// 每隔1小时检测
		return "0 0 0/1 * * ? ";
	}

}
