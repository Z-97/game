package com.alex.game.games.dice.struct;

/**
 * 上庄信息
 * 
 * @author yejuhua
 *
 */
public class ApplicantInfo {

	private long playerId;
	private String nickName;
	private long gold;
	private int icon;
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

	public long getGold() {
		return gold;
	}

	public void setGold(long gold) {
		this.gold = gold;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}
	public void clear() {
		this.playerId=0;
		this.nickName=null;
		this.icon=0;
		this.gold=0;
		
	}

}
