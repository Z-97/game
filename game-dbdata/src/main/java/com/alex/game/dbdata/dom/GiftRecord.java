package com.alex.game.dbdata.dom;

public class GiftRecord {

	private String playerName;
	// 时间
	private long time;
	// 类型1送2收
	private int getType;
	//价值金币
	private long giftGold;
	private int itemId;
	private int itemNum;

	public long getGiftGold() {
		return giftGold;
	}

	public void setGiftGold(long giftGold) {
		this.giftGold = giftGold;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public int getGetType() {
		return getType;
	}

	public void setGetType(int getType) {
		this.getType = getType;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getItemNum() {
		return itemNum;
	}

	public void setItemNum(int itemNum) {
		this.itemNum = itemNum;
	}
}
