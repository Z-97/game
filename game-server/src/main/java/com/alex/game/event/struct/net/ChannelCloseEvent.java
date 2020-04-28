/**
 * Copyright (c) 2015, Alex All Rights Reserved.
 * 
 */  
package com.alex.game.event.struct.net;

import com.alex.game.player.struct.Player;

/**
 * ChannelCloseEvent
 * 
 * @author Alex
 * @date 2017年4月9日 下午5:50:34
 */
public class ChannelCloseEvent {

	public final Player player;
	
	public ChannelCloseEvent(Player player) {
		this.player = player;
	}
}
