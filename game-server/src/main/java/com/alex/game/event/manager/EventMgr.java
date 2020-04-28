/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.event.manager;

import com.alex.game.event.core.LoggingSubscriberExceptionHandler;
import com.google.common.eventbus.EventBus;
import com.google.inject.Singleton;

/**
 * 基于google EventBus的事件管理器
 *
 * @author Alex
 * @date 2016/8/3 16:48
 */
@Singleton
public class EventMgr extends EventBus {

	public EventMgr() {
		// guava
		// EventBus的默认发布事件处理异常处理器LoggingSubscriberExceptionHandler没有记录异常堆栈，不能很好的定位问题
		super(new LoggingSubscriberExceptionHandler());
	}
}
