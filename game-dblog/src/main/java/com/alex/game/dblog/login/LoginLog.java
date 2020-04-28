/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.dblog.login;

import java.util.Date;

import com.alex.game.dbdata.dom.PlayerDom;
import com.alex.game.dblog.PlayerLog;
import com.alex.game.dblog.core.TableType;
import com.alex.game.dblog.core.annotation.Column;
import com.alex.game.dblog.core.annotation.LogTable;

/**
 * 登陆日志
 * 
 * @author Alex
 * @date 2016/8/8 14:42
 */
@LogTable(name="login_log", type= TableType.DAY)
public class LoginLog extends PlayerLog {
	
    @Column(name="mac", type = "varchar(62)", remark = "登录mac")
    public final String mac;
	@Column(name="device", type = "int(11)", remark = "登陆设备(0:Android,1:iphone)")
	public final int device;
	@Column(name="device_model", type = "varchar(128)", remark = "设备型号")
	public final String deviceModel;
	@Column(name="register_time", type = "datetime", remark = "注册时间")
	public final Date registerTime;
	public LoginLog(PlayerDom player, String mac, int device, String deviceModel) {
		super(player);
		this.mac = mac;
		this.device = device;
		this.deviceModel = deviceModel;
		this.registerTime = player.getRegisterTime();

	}

}
