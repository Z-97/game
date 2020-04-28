package com.alex.game.games.gobang.struct;
/**
 * 五子棋颜色
 * @author yejuhua
 *
 */
public enum GobangColor {
	WHITE(1, "白色"),
	BLACK(2, "黑色");
	
	public final int val;
	public final String desc;

	/**
	 * @param val
	 * @param desc
	 */
	private GobangColor(int val, String desc) {
		this.val = val;
		this.desc = desc;
	}

}
