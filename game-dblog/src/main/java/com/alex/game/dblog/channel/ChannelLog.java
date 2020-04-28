package com.alex.game.dblog.channel;

import com.alex.game.dblog.core.DbLog;
import com.alex.game.dblog.core.TableType;
import com.alex.game.dblog.core.annotation.Column;
import com.alex.game.dblog.core.annotation.LogTable;

/**
 * 渠道更改日志
 * 
 * @author yougo 2017年4月6日
 */
@LogTable(name = "channel_log", type = TableType.SINGLE)
public class ChannelLog extends DbLog {

	@Column(name = "before_channel", type = "varchar(62)", remark = "改变前平台id")
	public final String beforeChannel;
	@Column(name = "after_channel", type = "varchar(62)", remark = "改变后平台id")
	public final String afterChannel;
	@Column(name = "package_id", type = "varchar(62)", remark = "包id")
	public final String packageId;
	@Column(name = "operation_id", type = "bigint(20)", remark = "后台操作人id")
	public final long operationId;
	@Column(name = "operation_name", type = "varchar(62)", remark = "后台操作人名称")
	public final String operationName;
	@Column(name = "change_nums", type = "int(11)", remark = "变更玩家数量")
	public final int changeNums;

	public ChannelLog(String beforeChannel, String afterChannel, String packageId, long operationId,
			String operationName, int changeNums) {
		this.beforeChannel = beforeChannel;
		this.afterChannel = afterChannel;
		this.packageId = packageId;
		this.operationId = operationId;
		this.operationName = operationName;
		this.changeNums = changeNums;
	}

}
