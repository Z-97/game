package com.alex.game.dbdata.dom;
/**
 * 玩家机器人
 * 
 * @author Alex
 * @date 2017年4月19日 下午2:35:27
 */
public class PlayerRobotDom {
	// 玩家id
	private long id;
	// 用户名
	private String userName;
	// 昵称
	private String nickName;
	// 渠道id
	private String channelId;
	// 包id
	private String packageId;
	// 是否游客
	private boolean tourist;
	// 游客mac
	private String touristMac;
	// 玩家类型(0:普通玩家,1:代理,2:线上推广员,3:线下推广员)
    private int playerType;
	// 手机
	private String phone;
	// 支付宝
	private String alipay;
	// 支付宝姓名
	private String alipayName;
	// 是否封号
	private boolean locked;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	public boolean isTourist() {
		return tourist;
	}

	public void setTourist(boolean tourist) {
		this.tourist = tourist;
	}

	public String getTouristMac() {
		return touristMac;
	}

	public void setTouristMac(String touristMac) {
		this.touristMac = touristMac;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAlipay() {
		return alipay;
	}

	public void setAlipay(String alipay) {
		this.alipay = alipay;
	}

	public String getAlipayName() {
		return alipayName;
	}

	public void setAlipayName(String alipayName) {
		this.alipayName = alipayName;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public int getPlayerType() {
		return playerType;
	}

	public void setPlayerType(int playerType) {
		this.playerType = playerType;
	}
	
}
