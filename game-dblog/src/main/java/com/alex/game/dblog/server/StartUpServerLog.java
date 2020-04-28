/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.dblog.server;

import com.alex.game.dblog.core.DbLog;
import com.alex.game.dblog.core.annotation.Column;
import com.alex.game.dblog.core.annotation.LogTable;

/**
 * 启动服务器日志
 *
 * @author Alex
 * @date 2016/7/6 13:59
 */
@LogTable(name="startup_server_log")
public class StartUpServerLog extends DbLog {
	// 启动服务器日志标识
	public static final long START_SERVER_NO = System.nanoTime();

	@Column(name="no", type = "bigint(20)", remark = "开服序号,和关服序号一致")
	public final long no = START_SERVER_NO;
}
