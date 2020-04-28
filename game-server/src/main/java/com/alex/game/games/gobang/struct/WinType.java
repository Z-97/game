package com.alex.game.games.gobang.struct;

public enum WinType {
	A(1, "白方胜利"), 
	B(2,"黑方胜利"),
	E(3, "白方认输"), 
	F(4,"黑方认输"),
	H(5,"白方和棋"),
	M(6,"黑方和棋"),
	N(7,"白方总时间超时"),
	P(8,"棋子用完"),
	Q(9,"白方单步超时"),
	O(10,"黑方单步超时"),
	R(11,"黑方总时间超时"),;
	public final int val;
	public final String desc;

	/**
	 * @param val
	 * @param desc
	 */
	private WinType(int val, String desc) {
		this.val = val;
		this.desc = desc;
	}
}
