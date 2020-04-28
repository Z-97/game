/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.event.struct.login;

import com.alex.game.player.struct.Player;

/**
 * 玩家退出游戏事件
 *
 * @author Alex
 * @date 2016/8/8 14:51
 */
public class PlayerLogoutEvent {
	public final Player player;

	public PlayerLogoutEvent(Player player) {
		this.player = player;
	}
}
