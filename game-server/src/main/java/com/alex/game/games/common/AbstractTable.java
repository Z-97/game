/**
 * Copyright (c) 2015, Alex All Rights Reserved. 
 * 
 */  
package com.alex.game.games.common;

import java.util.List;

/**  
 *  Alexte 2015年8月6日 下午8:49:25  
 *
 */
public abstract class AbstractTable {
	// 最大桌子数10w
	public static final int MAX_TABLE = 100000;
	
	//桌子id
	public final int id;
	
	// 进桌玩家限制金币
	public long limitGold = 0;
	// 进桌玩家限制ip
	public String limitIp = null;

	
	/**
	 * @param id
	 */
	public AbstractTable(int id) {
		this.id = id;
	}
	
	// 房间所属的房间
	public abstract AbstractRoom room();
	public abstract List<? extends AbstractSeat> seats();
}
