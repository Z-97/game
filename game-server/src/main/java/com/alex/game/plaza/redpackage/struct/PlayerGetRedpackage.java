package com.alex.game.plaza.redpackage.struct;

import java.util.Date;

public class PlayerGetRedpackage {
	private long id;
	private String nickName;
	private int icon;
	private Date getDate;
	private long redPackageGold;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public int getIcon() {
		return icon;
	}
	public void setIcon(int icon) {
		this.icon = icon;
	}
	public Date getGetDate() {
		return getDate;
	}
	public void setGetDate(Date getDate) {
		this.getDate = getDate;
	}
	public long getRedPackageGold() {
		return redPackageGold;
	}
	public void setRedPackageGold(long redPackageGold) {
		this.redPackageGold = redPackageGold;
	}
	
	
}
