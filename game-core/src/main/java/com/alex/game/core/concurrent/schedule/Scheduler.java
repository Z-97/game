/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.core.concurrent.schedule;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 基于的ScheduledThreadPoolExecutor的Scheduler，扩展ScheduledThreadPoolExecutor提供可以调度固定次数和cron的任务
 *
 * @author Alex
 * @date 2016/7/5 16:17
 */
public class Scheduler extends ScheduledThreadPoolExecutor {

	public Scheduler(int corePoolSize, ThreadFactory threadFactory) {
		super(corePoolSize, threadFactory, new CallerRunsPolicy());
	}
	
	/**
	 * cron任务执行
	 * 
	 * @param cmd
	 * @param cron
	 * @return
	 */
	public CronFutureTask schedule(Runnable cmd, String cron) {
		CronExpression cronExp = new CronExpression(cron);
		Date now = new Date();
		// 下一次运行时间
		Date nextTime = cronExp.getTimeAfter(now);
		if (nextTime == null) {
			throw new RuntimeException("cron表达式[" + cron + "]不正确,永远不会运行");
		}
		
		long delay = nextTime.getTime() - now.getTime();
		CronFutureTask t = new CronFutureTask(cmd, cronExp);
		t.outerTask = schedule(t, delay, TimeUnit.MILLISECONDS);
		t.preTime = cronExp.getTimeBefore(now);
		t.nextTime = nextTime;
		
		return t;
	}
	
	/**
	 * Cron任务
	 * 
	 * @author Alex
	 * @date 2017年4月3日 下午9:09:42
	 */
	public class CronFutureTask extends FutureTask<Void> implements RunnableScheduledFuture<Void> {
		final CronExpression cron;
		private final Runnable runnable;
		// 实际执行的任务
		ScheduledFuture<?> outerTask;
		// 上一次运行时间
		volatile Date preTime;
		// 下一次运行时间
		volatile Date nextTime;

		CronFutureTask(Runnable runnable, CronExpression cron) {
			super(runnable, null);
			this.runnable = runnable;
			this.cron = cron;
		}
		
		public Date getPreTime() {
			return preTime;
		}

		public Date getNextTime() {
			return nextTime;
		}

		@Override
		public long getDelay(TimeUnit unit) {
			return outerTask.getDelay(unit);
		}

		@Override
		public int compareTo(Delayed o) {
			return outerTask.compareTo(o);
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			return outerTask.cancel(mayInterruptIfRunning);
		}

		@Override
		public boolean isCancelled() {
			return outerTask.isCancelled();
		}

		@Override
		public boolean isDone() {
			return outerTask.isDone();
		}

		@Override
		public Void get() throws InterruptedException, ExecutionException {
			outerTask.get();
			return null;
		}

		@Override
		public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
			outerTask.get(timeout, unit);
			return null;
		}

		@Override
		public boolean isPeriodic() {
			return false;
		}

		@Override
		protected void done() {
			Date now = new Date();
			this.preTime = this.nextTime;
			// 下一次运行时间
			Date nextTime = this.nextTime = cron.getTimeAfter(now);
			if (!isCancelled() && nextTime != null) {
				long delay = nextTime.getTime() - now.getTime();
				this.outerTask = schedule(new FutureTask<Void>(runnable, null) {
					@Override
					protected void done() {
						CronFutureTask.this.done();
					}
				}, delay, TimeUnit.MILLISECONDS);
			}
		}
	}
}
