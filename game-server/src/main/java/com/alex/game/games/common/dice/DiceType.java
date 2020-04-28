package com.alex.game.games.common.dice;
/**
 * 点数类型
 * @author yejuhua
 *
 */
public enum DiceType {
	DICE_1(1),               //1点
	DICE_2(2),               //2点
	DICE_3(3),               //3点
	DICE_4(4),               //4点
	DICE_5(5),               //5点
	DICE_6(6),               //6点
	;             
	public final int points;
	private DiceType(int niu) {
		this.points = niu;
	}
	
}
