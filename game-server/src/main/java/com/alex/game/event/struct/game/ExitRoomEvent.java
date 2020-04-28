package com.alex.game.event.struct.game;

/**
 * 退出房间事件
 * 
 * @author Alex
 * @date 2017年4月22日 下午7:28:24
 */
public class ExitRoomEvent {

	public final long playerId;

	public ExitRoomEvent(long playerId) {
		this.playerId = playerId;
	}
	
}
