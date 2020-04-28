package com.alex.game.dbdata.dom;
/**
 * 玩家排行信息
 * @author yejuhua
 *
 */
public class PlayerRankDom implements Comparable<PlayerRankDom>{
	private long playerId;
	private int iconId;
	private String nickName;
	private long gold;
	private String signature;
	public long getPlayerId() {
		return playerId;
	}
	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}
	public int getIconId() {
		return iconId;
	}
	public void setIconId(int iconId) {
		this.iconId = iconId;
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

	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	@Override
	public int compareTo(PlayerRankDom playerRank) {
		if(playerRank.getGold()>this.gold) {
			return 1;
		}
		return -1;
	}
}
