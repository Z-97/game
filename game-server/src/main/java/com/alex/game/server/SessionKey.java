/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.server;

import io.netty.util.AttributeKey;

/**
 * SessionKey
 * 
 * @author Alex
 * @date 2016/7/6 17:27
 */
public class SessionKey {
	private SessionKey() {
	}

	// 玩家ID
	public static final AttributeKey<Long> PLAYER_ID = AttributeKey.valueOf("playerId");
	// 消息请求顺序
	public static final AttributeKey<Integer> REQ_MSG_ORDER = AttributeKey.valueOf("reqMsgOrder");

}
