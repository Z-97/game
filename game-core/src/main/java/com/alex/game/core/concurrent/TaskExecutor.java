/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.core.concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 游戏中的任务处理器，一个TaskExecutor对应一个线程,游戏中的逻辑基本都是多生产者，单消费者 win7 64位 i7
 * 16g下测试,1000w的吞吐基于Disruptor(最稳定的那个模式下)的任务处理器10900ms左右和基于ThreadPoolExecutor的任务处理器11300ms左右,
 * 在多生产者单消费者时处理吞吐时相差很小(不到300ms)，不从ScheduledThreadPoolExecutor扩展，
 * ScheduledThreadPoolExecutor采用DelayedWorkQueue，性能没有ThreadPoolExecutor好，但也无法提供Schedule功能
 *
 * @author Alex
 * @date 2016/6/29 14:51
 */
public class TaskExecutor extends ThreadPoolExecutor {
	// 线程名称
	private static final String DEFAULT_EXECUTOR_NAME = "task-executor";

	private TaskExecutor() {
		this(DEFAULT_EXECUTOR_NAME);
	}

	private TaskExecutor(final String name) {
		// 当任务被拒绝执行时在调用线程中执行
		super(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), (r) -> new Thread(r, name),
				(r, executor) -> r.run());
	}
	
	/**
	 * 创建TaskExecutor
	 * @param name
	 * @return
	 */
	public static TaskExecutor createExecutor(String name) {
		return new TaskExecutor(name);
	}
	
	/**
	 * 创建多个TaskExecutor
	 * @param num
	 * @return
	 */
	public static TaskExecutor[] createExecutors(String name, int num) {
		TaskExecutor[] executors = new TaskExecutor[num];
		for (int i = 0; i < num; i++) {
			executors[i] = new TaskExecutor(name + "-" + i);
		}
		
		return executors;
	}

	@Override
	public void execute(Runnable cmd) {
		super.execute(new Task(cmd));
	}
}

/**
 * 在提交的Runnable封装一层,主要捕获异常记录日志，不然异常抛出，如果异常未捕获ThreadPoolExecutor会新增线程替代
 * 
 * @author Alex
 * @date 2016年12月22日 下午8:18:15
 */
final class Task implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(Task.class);

	final Runnable cmd;

	Task(Runnable cmd) {
		this.cmd = cmd;
	}

	@Override
	public void run() {
		try {
			cmd.run();
		} catch (Throwable e) {
			LOG.error("任务执行异常", e);
		}
	}
}
