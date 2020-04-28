package com.alex.game.games.dice.struct;

public class PlayerDiceRankInfo {
	// 玩家id
	private long playerId;
	// 玩家昵称
	private String nickName;
	// 进桌时间
	private long enterTblTime;
	// 头像id
	private int icon;

	private long gold;

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public long getEnterTblTime() {
		return enterTblTime;
	}

	public void setEnterTblTime(long enterTblTime) {
		this.enterTblTime = enterTblTime;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public long getGold() {
		return gold;
	}

	public void setGold(long gold) {
		this.gold = gold;
	}

}
