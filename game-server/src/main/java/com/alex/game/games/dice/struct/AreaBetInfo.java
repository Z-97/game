package com.alex.game.games.dice.struct;

/**
 * 下注信息
 * @author yejuhua
 *
 */
public class AreaBetInfo {
	//1大区2小区3豹子区
	private int area;
	//下注金币
	private long gold;

	public int getArea() {
		return area;
	}
	public void setArea(int area) {
		this.area = area;
	}
	public long getGold() {
		return gold;
	}
	public void setGold(long gold) {
		this.gold = gold;
	}
	
}
