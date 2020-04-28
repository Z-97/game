/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.event.struct.login;

import com.alex.game.player.struct.Player;

/**
 * 玩家登录事件
 *
 * @author Alex
 * @date 2016/8/8 14:30
 */
public class PlayerLoginEvent {
	public final Player player;

	public PlayerLoginEvent(Player player) {
		this.player = player;
	}
}
