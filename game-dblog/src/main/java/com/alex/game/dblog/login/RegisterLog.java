/**
 * Copyright (c) 2015, Alex All Rights Reserved. 
 */  
package com.alex.game.dblog.login;

import com.alex.game.dbdata.dom.PlayerDom;
import com.alex.game.dblog.PlayerLog;
import com.alex.game.dblog.core.TableType;
import com.alex.game.dblog.core.annotation.Column;
import com.alex.game.dblog.core.annotation.LogTable;

/**  
 * or Alexte 2015年12月28日 上午10:42:30  
 *
 */
@LogTable(name="register_log", type = TableType.SINGLE)
public class RegisterLog extends PlayerLog {
	
	@Column(name="mac", type = "varchar(62)", remark = "注册mac")
	public final String mac;
	@Column(name="device", type = "int(11)", remark = "注册设备(0:Android,1:iphone)")
	public final int device;
	@Column(name="device_model", type = "varchar(128)", remark = "设备型号")
	public final String deviceModel;
	
	public RegisterLog(PlayerDom player, String mac, int device, String deviceModel) {
		super(player);
		this.mac = mac;
		this.device = device;
		this.deviceModel = deviceModel;
	}

}
