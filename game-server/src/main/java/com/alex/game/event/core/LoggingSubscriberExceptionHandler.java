/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.event.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

/**
 * EventBus的默认发布事件处理异常处理器LoggingSubscriberExceptionHandler没有记录异常堆栈，不能很好的定位问题
 * 
 * @author Alex
 * @date 2016/8/3 16:47
 */
public class LoggingSubscriberExceptionHandler implements SubscriberExceptionHandler {

	public static final Logger LOG = LoggerFactory.getLogger(LoggingSubscriberExceptionHandler.class);

	@Override
	public void handleException(Throwable exception, SubscriberExceptionContext context) {
		LOG.error("Could not dispatch event: " + context.getSubscriber() + " to " + context.getSubscriberMethod(),
				exception);
	}
}
