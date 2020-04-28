package com.alex.game.hub.schedule.manager;

import java.text.SimpleDateFormat;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alex.game.core.concurrent.schedule.Scheduler;
import com.alex.game.core.concurrent.schedule.Scheduler.CronFutureTask;
import com.alex.game.hub.schedule.cron.base.CronTask;
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

	/**
	 * 驱动cron，所有的cron任务都写在这里
	 */
	@Inject
	public void scheduleCronTasks() {

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

		LOG.info("启动Cron任务[{}],表达式:{},上次时间:{},首次时间:{}", cronName, cron, preTime, nextTime);
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
		if (executor == null)
			throw new NullPointerException();
		return scheduler.schedule(() -> executor.execute(cmd), delay, unit);
	}

	public ScheduledFuture<?> scheduleAtFixedRate(Runnable cmd, long initDelay, long period, TimeUnit unit,
			Executor executor) {
		if (executor == null)
			throw new NullPointerException();
		return scheduler.scheduleAtFixedRate(() -> executor.execute(cmd), initDelay, period, unit);
	}

	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable cmd, long initDelay, long delay, TimeUnit unit,
			Executor executor) {
		if (executor == null)
			throw new NullPointerException();
		return scheduler.scheduleWithFixedDelay(() -> executor.execute(cmd), initDelay, delay, unit);
	}

}
