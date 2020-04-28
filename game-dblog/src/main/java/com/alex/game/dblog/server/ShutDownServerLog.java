/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.dblog.server;

import com.alex.game.dblog.core.DbLog;
import com.alex.game.dblog.core.TableType;
import com.alex.game.dblog.core.annotation.Column;
import com.alex.game.dblog.core.annotation.LogTable;

/**
 * 关闭服务器日志
 *
 * @author Alex
 * @date 2016/7/6 16:36
 */
@LogTable(name="shutdown_server_log", type = TableType.SINGLE)
public class ShutDownServerLog extends DbLog {
	@Column(name="no", type = "bigint(20)", remark = "关服序号,和开服序号一致")
	public final long no = StartUpServerLog.START_SERVER_NO;
}
