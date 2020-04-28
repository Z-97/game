/**
 * Copyright (c) 2015, Alex All Rights Reserved. 
 * 
 */  
package com.alex.game.games.common;

import com.alex.game.common.Game;

/**  
 * 游戏房间
 * @author Alexte 2015年8月6日 下午6:08:31  
 *
 */
public abstract class AbstractRoom {
	
	// 房间id
	public final int id;
	// 房间名称
	public final String name;
	
	/**
	 * @param id
	 * @param name
	 */
	protected AbstractRoom(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	/**
	 * 房间所属的游戏
	 * @return
	 */
	public abstract Game game();
	
}
