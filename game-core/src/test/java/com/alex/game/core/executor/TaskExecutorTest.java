/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.core.executor;

import org.junit.Test;

import com.alex.game.core.concurrent.TaskExecutor;

import java.util.concurrent.*;

/**
 * @author Alex
 * @date 2016/7/1 10:09
 */
public class TaskExecutorTest {
	
    /**
     * 测试提交的任务出现异常后会不会没有捕获新增线程
     */
    @Test
    public void testRunException() throws InterruptedException {
        TaskExecutor executor = TaskExecutor.createExecutor("test");
        executor.execute(new RunExceptionTask());
        executor.execute(new RunExceptionTask());
        executor.execute(new RunExceptionTask());
        executor.execute(new RunExceptionTask());
        executor.execute(new RunExceptionTask());

        Thread.sleep(3000);
    }
}

class RunExceptionTask implements Runnable {

    @Override
    public void run() {
        System.out.println("线程id:" + Thread.currentThread().getId());
        if (ThreadLocalRandom.current().nextBoolean()) {
            throw new RuntimeException("运行异常");
        }
    }
}


