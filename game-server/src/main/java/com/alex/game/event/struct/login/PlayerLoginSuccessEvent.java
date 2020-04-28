package com.alex.game.event.struct.login;

import com.alex.game.player.struct.Player;

/**
 * 登录成功事件
 * 
 * @author yougo 2017年3月27日
 */
public class PlayerLoginSuccessEvent {

	public final Player player;

	public PlayerLoginSuccessEvent(Player player) {
		this.player = player;
	}

}
