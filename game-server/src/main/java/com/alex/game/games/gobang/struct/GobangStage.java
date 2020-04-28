package com.alex.game.games.gobang.struct;
/**
 * 五子棋阶段枚举
 * @author yejuhua
 *
 */
public enum GobangStage {
	READY(1, "准备"),
	GAMEING(2, "游戏中"),;
	
	public final int val;
	public final String desc;

	/**
	 * @param val
	 * @param desc
	 */
	private GobangStage(int val, String desc) {
		this.val = val;
		this.desc = desc;
	}
}
