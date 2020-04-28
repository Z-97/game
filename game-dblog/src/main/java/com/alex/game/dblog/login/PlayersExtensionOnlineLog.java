package com.alex.game.dblog.login;

import com.alex.game.dblog.core.DbLog;
import com.alex.game.dblog.core.TableType;
import com.alex.game.dblog.core.annotation.Column;
import com.alex.game.dblog.core.annotation.LogTable;

/**
 * 玩家推广员在线日志在线日志
 * 
 * @author Alex
 * @date 2017年8月26日 下午3:37:00
 */
@LogTable(name="players_extension_online_log", type = TableType.SINGLE)
public class PlayersExtensionOnlineLog extends DbLog {
	
	@Column(name = "code", type = "varchar(64)", remark = "推广员code")
	public final String code;
	@Column(name = "num", type = "int(11)", remark = "在线人数")
	public final int num;

	public PlayersExtensionOnlineLog(String code, int num) {
		this.code = code;
		this.num = num;
	}
}
