/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.player.struct;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import com.alex.game.dbdata.dom.BankRecord;
import com.alex.game.dbdata.dom.Email;
import com.alex.game.dbdata.dom.GiftRecord;
import com.alex.game.dbdata.dom.PlayerDom;
import com.alex.game.games.common.AbstractTable;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * 玩家
 *
 * @author Alex
 * @date 2016/7/27 11:10
 */
public class Player extends PlayerDom {

    public static final AttributeKey<Player> PLAYER_KEY = AttributeKey.valueOf("player");
    private transient PlayerDom dom;
	// 玩家通道会话
	public transient final Channel channel;
	// 玩家ip
	public transient String ip;
	// 玩家机器码
	public transient String mac;
	// 玩家当前设备(0:Android,1:iphone)
	public transient int device;
	// 玩家当前设备型号
	public transient String deviceModel;
	// 手机
	public transient String phone = null;
	// 手机验证码失败次数
	public transient int phoneCodeFailureNum = 0;
	// 是否已经退出登录
	public transient volatile boolean logouted = false;
	// 玩家当前桌子
	public transient volatile AbstractTable table = null;
	//是否已经进入保险箱
	public transient boolean isEnterBank=false;
    public Player(Channel channel, String ip) {
        this.channel = channel;
        this.ip = ip;
    }

    /**
     * 重置玩家数据
     */
    public void reset() {
    	this.mac = null;
    	this.device = 0;
    	this.deviceModel = null;
    	this.phone = null;
    	this.phoneCodeFailureNum = 0;
    	this.table = null;
    	this.logouted = false;
    }
    
    /**
     * 设置玩家代理数据
     * @param playerDom
     */
    public void setPlayerDom(PlayerDom playerDom) {
		this.dom = playerDom;
	}
    
    /**
     * 获取代理数据
     * @return
     */
    public PlayerDom dom() {
		return dom;
	}
    
	public long gold() {
		return dom.gold();
	}

	public long bankGold() {
		return dom.bankGold();
	}

	public long getId() {
		return dom.getId();
	}

	public void setId(long id) {
		dom.setId(id);
	}

	public String getUserName() {
		return dom.getUserName();
	}

	public void setUserName(String userName) {
		dom.setUserName(userName);
	}

	public String getNickName() {
		return dom.getNickName();
	}

	public void setNickName(String nickName) {
		dom.setNickName(nickName);
	}

	public String getPwd() {
		return dom.getPwd();
	}

	public void setPwd(String pwd) {
		dom.setPwd(pwd);
	}

	public String getChannelId() {
		return dom.getChannelId();
	}

	public void setChannelId(String channelId) {
		dom.setChannelId(channelId);
	}

	public String getPackageId() {
		return dom.getPackageId();
	}

	public void setPackageId(String packageId) {
		dom.setPackageId(packageId);
	}

	public String getRegisterIp() {
		return dom.getRegisterIp();
	}

	public void setRegisterIp(String registerIp) {
		dom.setRegisterIp(registerIp);
	}

	public int getRegisterDevice() {
		return dom.getRegisterDevice();
	}

	public void setRegisterDevice(int registerDevice) {
		dom.setRegisterDevice(registerDevice);
	}

	public String getRegisterDeviceModel() {
		return dom.getRegisterDeviceModel();
	}

	public void setRegisterDeviceModel(String registerDeviceModel) {
		dom.setRegisterDeviceModel(registerDeviceModel);
	}

	public Date getRegisterTime() {
		return dom.getRegisterTime();
	}

	public void setRegisterTime(Date registerTime) {
		dom.setRegisterTime(registerTime);
	}

	public String getRegisterMac() {
		return dom.getRegisterMac();
	}

	public void setRegisterMac(String registerMac) {
		dom.setRegisterMac(registerMac);
	}

	public String getLoginKey() {
		return dom.getLoginKey();
	}

	public void setLoginKey(String loginKey) {
		dom.setLoginKey(loginKey);
	}

	public String getLoginIp() {
		return dom.getLoginIp();
	}

	public void setLoginIp(String loginIp) {
		dom.setLoginIp(loginIp);
	}

	public int getLoginDevice() {
		return dom.getLoginDevice();
	}

	public void setLoginDevice(int loginDevice) {
		dom.setLoginDevice(loginDevice);
	}

	public String getLoginDeviceModel() {
		return dom.getLoginDeviceModel();
	}

	public void setLoginDeviceModel(String loginDeviceModel) {
		dom.setLoginDeviceModel(loginDeviceModel);
	}

	public Date getLoginTime() {
		return dom.getLoginTime();
	}

	public void setLoginTime(Date loginTime) {
		dom.setLoginTime(loginTime);
	}

	public String getLoginMac() {
		return dom.getLoginMac();
	}

	public void setLoginMac(String loginMac) {
		dom.setLoginMac(loginMac);
	}

	public int getLoginCount() {
		return dom.getLoginCount();
	}

	public void setLoginCount(int loginCount) {
		dom.setLoginCount(loginCount);
	}

	public Date getLogoutTime() {
		return dom.getLogoutTime();
	}

	public void setLogoutTime(Date logoutTime) {
		dom.setLogoutTime(logoutTime);
	}

	public boolean isTourist() {
		return dom.isTourist();
	}

	public void setTourist(boolean tourist) {
		dom.setTourist(tourist);
	}

	public String getTouristMac() {
		return dom.getTouristMac();
	}

	public void setTouristMac(String touristMac) {
		dom.setTouristMac(touristMac);
	}

	public boolean isAgent() {
		return dom.isAgent();
	}
	public int getSex() {
		return dom.getSex();
	}

	public void setSex(int sex) {
		dom.setSex(sex);
	}

	public int getIcon() {
		return dom.getIcon();
	}

	public void setIcon(int icon) {
		dom.setIcon(icon);
	}

	public String getPhone() {
		return dom.getPhone();
	}

	public void setPhone(String phone) {
		dom.setPhone(phone);
	}

	public Date getBindingPhoneTime() {
		return dom.getBindingPhoneTime();
	}

	public void setBindingPhoneTime(Date bindingPhoneTime) {
		dom.setBindingPhoneTime(bindingPhoneTime);
	}
	public AtomicLong getGold() {
		return dom.getGold();
	}

	public void setGold(AtomicLong gold) {
		dom.setGold(gold);
	}

	public AtomicLong getWinGold() {
		return dom.getWinGold();
	}

	public void setWinGold(AtomicLong winGold) {
		dom.setWinGold(winGold);
	}

	public AtomicLong getLoseGold() {
		return dom.getLoseGold();
	}

	public void setLoseGold(AtomicLong loseGold) {
		dom.setLoseGold(loseGold);
	}

	public AtomicLong getRechargeMoney() {
		return dom.getRechargeMoney();
	}

	public void setRechargeMoney(AtomicLong rechargeMoney) {
		dom.setRechargeMoney(rechargeMoney);
	}
	public long getExp() {
		return dom.getExp();
	}

	public void setExp(long exp) {
		dom.setExp(exp);
	}

	public int getLevel() {
		return dom.getLevel();
	}

	public void setLevel(int level) {
		dom.setLevel(level);
	}

	public int getVipLevel() {
		return dom.getVipLevel();
	}

	public void setVipLevel(int vipLevel) {
		dom.setVipLevel(vipLevel);
	}

	public boolean isOnline() {
		return dom.isOnline();
	}

	public void setOnline(boolean online) {
		dom.setOnline(online);
	}

	public boolean isLocked() {
		return dom.isLocked();
	}

	public void setLocked(boolean locked) {
		dom.setLocked(locked);
	}

	public String getBankPwd() {
		return dom.getBankPwd();
	}

	public void setBankPwd(String bankPwd) {
		dom.setBankPwd(bankPwd);
	}

	public AtomicLong getBankGold() {
		return dom.getBankGold();
	}

	public void setBankGold(AtomicLong bankGold) {
		dom.setBankGold(bankGold);
	}

	public Date getUpdateTime() {
		return dom.getUpdateTime();
	}

	public void setUpdateTime(Date updateTime) {
		dom.setUpdateTime(updateTime);
	}

	public AtomicLong getTax() {
		return dom.getTax();
	}

	public void setTax(AtomicLong tax) {
		dom.setTax(tax);
	}
	public int getPlayerType() {
		return dom.getPlayerType();
	}

	public void setPlayerType(int playerType) {
		dom.setPlayerType(playerType);
	}

	public boolean isExtensionPerson() {
		return dom.isExtensionPerson();
	}
	public String getDeviceId() {
		return dom.getDeviceId();
	}

	public void setDeviceId(String deviceId) {
		dom.setDeviceId(deviceId);
	}

	public int getPlatId() {
		return dom.getPlatId();
	}

	public void setPlatId(int platId) {
		dom.setPlatId(platId);
	}
	
	public Boolean getIsModfiyNickname() {
		return dom.getIsModfiyNickname();
	}

	public void setIsModfiyNickname(Boolean isModfiyNickname) {
		dom.setIsModfiyNickname(isModfiyNickname);
	}
	public ConcurrentHashMap<Integer, Email> getEmails() {
		return dom.getEmails();
	}

	public void setEmails(ConcurrentHashMap<Integer, Email> emails) {
		dom.setEmails(emails);
	}
	public CopyOnWriteArrayList<BankRecord> getBankRecords() {
		return dom.getBankRecords();
	}

	public void setBankRecords(CopyOnWriteArrayList<BankRecord> bankRecords) {
		dom.setBankRecords(bankRecords);
	}

	public boolean isEmailTips() {
		return dom.isEmailTips();
	}

	public void setEmailTips(boolean emailTips) {
		dom.setEmailTips(emailTips);
	}

	public boolean isTransferGoldTips() {
		return dom.isTransferGoldTips();
	}

	public void setTransferGoldTips(boolean transferGoldTips) {
		dom.setTransferGoldTips(transferGoldTips);
	}

	public boolean isCustomerTips() {
		return dom.isCustomerTips();
	}

	public void setCustomerTips(boolean customerTips) {
		dom.setCustomerTips(customerTips);
	}
	public CopyOnWriteArrayList<GiftRecord> getGiftRecords() {
		return dom.getGiftRecords();
	}

	public void setGiftRecords(CopyOnWriteArrayList<GiftRecord> giftRecords) {
		dom.setGiftRecords(giftRecords);
	}
	public String getSignature() {
		return dom.getSignature();
	}

	public void setSignature(String signature) {
		dom.setSignature(signature);
	}
	public Boolean getIsRobot() {
		return dom.getIsRobot();
	}

	public void setIsRobot(Boolean isRobot) {
		dom.setIsRobot(isRobot);
	}
}
