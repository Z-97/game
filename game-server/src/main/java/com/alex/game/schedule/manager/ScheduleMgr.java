/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.schedule.manager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alex.game.core.concurrent.schedule.Scheduler;
import com.alex.game.core.concurrent.schedule.Scheduler.CronFutureTask;
import com.alex.game.schedule.cron.CreateNextDblogTblTask;
import com.alex.game.schedule.cron.PlayersOnlineLogTask;
import com.alex.game.schedule.cron.RefreshRedpackageTask;
import com.alex.game.schedule.cron.ServerMointorTask;
import com.alex.game.schedule.cron.base.CronTask;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 任务调度管理,可以cron、固定次数、回调executor执行
 *
 * @author Alex
 * @date 2016/8/3 17:30
 */
@Singleton
public class ScheduleMgr {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final Logger LOG = LoggerFactory.getLogger(ScheduleMgr.class);
    
    // java线程池Scheduler，扩展ScheduledThreadPoolExecutor提供可以调度固定次数和cron的任务
    private final Scheduler scheduler = new Scheduler(8, r -> new Thread(r, "Game-Scheduler"));
    // 缓存CronTask任务的Future,因为游戏中需要从CronFutureTask拿到上一次任务的运行时间
    private final ConcurrentHashMap<Class<? extends CronTask>, CronFutureTask> cronFutures = new ConcurrentHashMap<>();

    /**
     * 驱动cron，所有的cron任务都写在这里
     */
    @Inject
    public void scheduleCronTasks(
    		RefreshRedpackageTask refreshPlayerRankTask, 
    		PlayersOnlineLogTask playersOnlineLogTask, 
    		CreateNextDblogTblTask createNextDblogTblTask, 
    		ServerMointorTask serverMointorTask) {
    	
    	scheduleCronTask(serverMointorTask);
    	scheduleCronTask(refreshPlayerRankTask);
    	scheduleCronTask(playersOnlineLogTask);
    	scheduleCronTask(createNextDblogTblTask);
	}
	/**
	 * 驱动cron表达式
	 * 
	 * @param cronTask
	 */
	private void scheduleCronTask(CronTask cronTask) {
		String cron = cronTask.cron();
		CronFutureTask cronFuture = scheduler.schedule(cronTask, cron);
		String cronName = cronTask.getClass().getName();
		String preTime = cronFuture.getPreTime() == null ? "" : DATE_FORMAT.format(cronFuture.getPreTime());
		String nextTime = cronFuture.getNextTime() == null ? "" : DATE_FORMAT.format(cronFuture.getNextTime());

		cronFutures.put(cronTask.getClass(), cronFuture);
		LOG.info("启动Cron任务[{}],表达式:{},上次时间:{},首次时间:{}", cronName, cron, preTime, nextTime);
	}
	
	/**
	 * 根据cron任务类型获取它的上次执行时间
	 * 
	 * @param type
	 * @return
	 */
	public Date getCronTaskPreTime(Class<? extends CronTask> type) {
		
		return cronFutures.get(type).getPreTime();
	}

	public ScheduledFuture<?> schedule(Runnable cmd, long delay, TimeUnit unit) {
		return scheduler.schedule(cmd, delay, unit);
	}

	public ScheduledFuture<?> scheduleAtFixedRate(Runnable cmd, long initialDelay, long period, TimeUnit unit) {
		return scheduler.scheduleAtFixedRate(cmd, initialDelay, period, unit);
	}

	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable cmd, long initialDelay, long delay, TimeUnit unit) {
		return scheduler.scheduleWithFixedDelay(cmd, initialDelay, delay, unit);
	}

    public ScheduledFuture<?> schedule(Runnable cmd, long delay, TimeUnit unit, Executor executor) {
    	if (executor == null) throw new NullPointerException();
    	return scheduler.schedule(() -> executor.execute(cmd), delay, unit);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable cmd, long initDelay, 
    		long period, TimeUnit unit, Executor executor) {
        if (executor == null) throw new NullPointerException();
        return scheduler.scheduleAtFixedRate(() -> executor.execute(cmd), initDelay, period, unit);
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable cmd, long initDelay, 
    		long delay, TimeUnit unit, Executor executor) {
        if (executor == null) throw new NullPointerException();
        return scheduler.scheduleWithFixedDelay(() -> executor.execute(cmd), initDelay, delay, unit);
    }

	/**
	 * 关闭
	 */
	public void shutdown() {
		// 停止调度，丢弃已产生的task
		scheduler.shutdownNow();
		LOG.info("Scheduler关闭");
	}
}
