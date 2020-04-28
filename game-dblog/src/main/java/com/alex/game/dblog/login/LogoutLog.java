/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.dblog.login;

import com.alex.game.dbdata.dom.PlayerDom;
import com.alex.game.dblog.PlayerLog;
import com.alex.game.dblog.core.TableType;
import com.alex.game.dblog.core.annotation.Column;
import com.alex.game.dblog.core.annotation.LogTable;

/**
 * 退出登录日志
 * 
 * @author Alex
 * @date 2016/8/8 14:57
 */
@LogTable(name="logout_log", type = TableType.DAY)
public class LogoutLog extends PlayerLog {
	
	@Column(name="mac", type = "varchar(62)", remark = "登出mac")
	public final String mac;
	@Column(name="duration", type = "bigint(20)", remark = "时长(毫秒)")
	public final long duration;

	public LogoutLog(PlayerDom player, long duration) {
		super(player);
		this.mac = player.getLoginMac();
		this.duration = duration;
	}
}
