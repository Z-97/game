package com.alex.game.event.struct.net;

import com.alex.game.player.struct.Player;

/**
 * 建立网络链接事件
 * 
 * @author Alex
 * @date 2017年4月9日 下午5:47:04
 */
public class ChannelConnectEvent {

	public final Player player;
	
	public ChannelConnectEvent(Player player) {
		this.player = player;
	}
}
