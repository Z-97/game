/**
 * Copyright (c) 2015, Alex All Rights Reserved. 
 * 
 */  
package com.alex.game.games.common;

/**  
 *  Alexte 2015年8月6日 下午5:47:42  
 *
 */
public abstract class AbstractSeat {
	// 玩家id
	public long playerId;
	// 玩家昵称
	public String nickName;
	// 座位顺序(从0开始),没特殊需求最好不该变它
	public final int order;
	// 进桌时间
	public long enterTblTime;
	//头像id
	public int icon;
	/**
	 * @param order
	 */
	public AbstractSeat(int order) {
		this.order = order;
	}
	
	public void clear() {
		this.playerId = 0;
		this.nickName = null;
		this.enterTblTime = 0;
		this.icon= 0;
	}
	
	/**
	 * 房间所属的桌子
	 * @return
	 */
	public abstract AbstractTable table();
	
}
