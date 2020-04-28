package com.alex.game.games.dice.struct;

public enum DiceStage {
	READY(1,"准备"),
	BET(2, "下注"), 
	BALANCE(3,"结算");
	
	public final int val;
	public final String desc;

	/**
	 * @param val
	 * @param desc
	 */
	private DiceStage(int val, String desc) {
		this.val = val;
		this.desc = desc;
	}
}
