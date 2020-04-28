package com.alex.game.games.gobang.struct;
/**
 * 五子棋游戏座位状态
 * @author yejuhua
 *
 */
public enum GobangSeatStage {
	WATING(1),			//  等待
	READY(2),			//  准备
	PLAYING(3),			//  游戏中
	;
	public final int id;
	private GobangSeatStage(int id) {
		this.id = id;
	}
}
