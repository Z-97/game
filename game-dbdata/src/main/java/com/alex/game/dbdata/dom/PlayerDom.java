/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.dbdata.dom;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 玩家域对象
 * 
 * @author Alex
 * @date 2016年12月21日 下午2:24:54
 */
public class PlayerDom {
	// 玩家id
	private long id;
	// 用户名
	private String userName;
	// 昵称
	private String nickName;
	// 密码
	private String pwd;
	// 渠道id
	private String channelId;
	// 包id
	private String packageId;
	// 注册ip
	private String registerIp;
	// 注册设备(0:Android,1:iphone)
	private int registerDevice;
	// 注册设备型号
	private String registerDeviceModel;
	// 注册时间
	private Date registerTime;
	// 注册mac
	private String registerMac;
	// 快速登陆的key
	private String loginKey;
	// 登陆ip
	private String loginIp;
	// 登陆设备(0:Android,1:iphone)
	private int loginDevice;
	// 登陆设备型号
	private String loginDeviceModel;
	// 登陆时间
	private Date loginTime;
	// 登陆mac
	private String loginMac;
	// 登陆次数
	private int loginCount;
	// 登出时间
	private Date logoutTime;
	// 是否游客
	private boolean tourist;
	// 游客mac
	private String touristMac;
	// 玩家类型(0:普通玩家,1:代理,2:线上推广员,3:线下推广员)
    private int playerType;
	// 性别,0:男性,1:女性
	private int sex;
	// 头像
	private int icon;
	// 手机
	private String phone;
	// 绑定手机时间
	private Date bindingPhoneTime;
	// 玩家金币
	private AtomicLong gold = new AtomicLong(0);
	// 玩家税收
	private AtomicLong tax = new AtomicLong(0);
	// 玩家捕鱼游戏赢的金币
	private AtomicLong winGold = new AtomicLong(0);
	// 玩家捕鱼游戏输的金币
	private AtomicLong loseGold = new AtomicLong(0);
	// 玩家充值人民币金额
	private AtomicLong rechargeMoney = new AtomicLong(0);
	// 经验
	private long exp;
	// 等级
	private int level;
	// vip等级
	private int vipLevel;
	// 是否在线
	private boolean online;
	// 是否封号
	private boolean locked;
	// 银行密码
	private String bankPwd;
	// 银行金币
	private AtomicLong bankGold = new AtomicLong(0);
	// 数据更新时间
	private Date updateTime;

    // 平台id
    private int platId;
    // 唯一设备码
    private String deviceId;
    //是否已经修改过昵称
    private Boolean isModfiyNickname;
   //邮件
    private ConcurrentHashMap<Integer, Email> emails = new ConcurrentHashMap<>();
    //银行记录
	private CopyOnWriteArrayList<BankRecord> bankRecords = new CopyOnWriteArrayList<>();
	//邮件提示
	private boolean emailTips;
	//转账提示
	private boolean transferGoldTips;
	//客服提示
	private boolean customerTips;
	//礼物列表
	private CopyOnWriteArrayList<GiftRecord> giftRecords = new CopyOnWriteArrayList<>();
	//签名
	private String signature;
	//是否是机器人
	private Boolean isRobot;
	/**
	 * 玩家金币
	 * @return
	 */
	public long gold() {
		return gold.get();
	}
	
	/**
	 * 玩家银行金币
	 * @return
	 */
	public long bankGold() {
		return bankGold.get();
	}

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

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
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

	public String getRegisterIp() {
		return registerIp;
	}

	public void setRegisterIp(String registerIp) {
		this.registerIp = registerIp;
	}

	public int getRegisterDevice() {
		return registerDevice;
	}

	public void setRegisterDevice(int registerDevice) {
		this.registerDevice = registerDevice;
	}

	public String getRegisterDeviceModel() {
		return registerDeviceModel;
	}

	public void setRegisterDeviceModel(String registerDeviceModel) {
		this.registerDeviceModel = registerDeviceModel;
	}

	public Date getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(Date registerTime) {
		this.registerTime = registerTime;
	}

	public String getRegisterMac() {
		return registerMac;
	}

	public void setRegisterMac(String registerMac) {
		this.registerMac = registerMac;
	}

	public String getLoginKey() {
		return loginKey;
	}

	public void setLoginKey(String loginKey) {
		this.loginKey = loginKey;
	}

	public String getLoginIp() {
		return loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	public int getLoginDevice() {
		return loginDevice;
	}

	public void setLoginDevice(int loginDevice) {
		this.loginDevice = loginDevice;
	}

	public String getLoginDeviceModel() {
		return loginDeviceModel;
	}

	public void setLoginDeviceModel(String loginDeviceModel) {
		this.loginDeviceModel = loginDeviceModel;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public String getLoginMac() {
		return loginMac;
	}

	public void setLoginMac(String loginMac) {
		this.loginMac = loginMac;
	}

	public int getLoginCount() {
		return loginCount;
	}

	public void setLoginCount(int loginCount) {
		this.loginCount = loginCount;
	}

	public Date getLogoutTime() {
		return logoutTime;
	}

	public void setLogoutTime(Date logoutTime) {
		this.logoutTime = logoutTime;
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

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Date getBindingPhoneTime() {
		return bindingPhoneTime;
	}

	public void setBindingPhoneTime(Date bindingPhoneTime) {
		this.bindingPhoneTime = bindingPhoneTime;
	}


	public AtomicLong getGold() {
		return gold;
	}

	public void setGold(AtomicLong gold) {
		this.gold = gold;
	}

	public AtomicLong getWinGold() {
		return winGold;
	}

	public void setWinGold(AtomicLong winGold) {
		this.winGold = winGold;
	}

	public AtomicLong getLoseGold() {
		return loseGold;
	}

	public void setLoseGold(AtomicLong loseGold) {
		this.loseGold = loseGold;
	}

	public AtomicLong getRechargeMoney() {
		return rechargeMoney;
	}

	public void setRechargeMoney(AtomicLong rechargeMoney) {
		this.rechargeMoney = rechargeMoney;
	}
	public long getExp() {
		return exp;
	}

	public void setExp(long exp) {
		this.exp = exp;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getVipLevel() {
		return vipLevel;
	}

	public void setVipLevel(int vipLevel) {
		this.vipLevel = vipLevel;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public String getBankPwd() {
		return bankPwd;
	}

	public void setBankPwd(String bankPwd) {
		this.bankPwd = bankPwd;
	}

	public AtomicLong getBankGold() {
		return bankGold;
	}

	public void setBankGold(AtomicLong bankGold) {
		this.bankGold = bankGold;
	}
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public AtomicLong getTax() {
		return tax;
	}

	public void setTax(AtomicLong tax) {
		this.tax = tax;
	}
	public boolean isAgent() {
		return playerType == 1;
	}
	
	public boolean isExtensionPerson() {
		return playerType == 2 || playerType == 3;
	}

	public int getPlayerType() {
		return playerType;
	}

	public void setPlayerType(int playerType) {
		this.playerType = playerType;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public int getPlatId() {
		return platId;
	}

	public void setPlatId(int platId) {
		this.platId = platId;
	}

	public Boolean getIsModfiyNickname() {
		return isModfiyNickname;
	}

	public void setIsModfiyNickname(Boolean isModfiyNickname) {
		this.isModfiyNickname = isModfiyNickname;
	}

	public ConcurrentHashMap<Integer, Email> getEmails() {
		return emails;
	}

	public void setEmails(ConcurrentHashMap<Integer, Email> emails) {
		this.emails = emails;
	}

	public CopyOnWriteArrayList<BankRecord> getBankRecords() {
		return bankRecords;
	}

	public void setBankRecords(CopyOnWriteArrayList<BankRecord> bankRecords) {
		this.bankRecords = bankRecords;
	}

	public boolean isEmailTips() {
		return emailTips;
	}

	public void setEmailTips(boolean emailTips) {
		this.emailTips = emailTips;
	}

	public boolean isTransferGoldTips() {
		return transferGoldTips;
	}

	public void setTransferGoldTips(boolean transferGoldTips) {
		this.transferGoldTips = transferGoldTips;
	}

	public boolean isCustomerTips() {
		return customerTips;
	}

	public void setCustomerTips(boolean customerTips) {
		this.customerTips = customerTips;
	}

	public CopyOnWriteArrayList<GiftRecord> getGiftRecords() {
		return giftRecords;
	}

	public void setGiftRecords(CopyOnWriteArrayList<GiftRecord> giftRecords) {
		this.giftRecords = giftRecords;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public Boolean getIsRobot() {
		return isRobot;
	}

	public void setIsRobot(Boolean isRobot) {
		this.isRobot = isRobot;
	}

}
