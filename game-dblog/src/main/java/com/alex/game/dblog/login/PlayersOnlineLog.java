package com.alex.game.dblog.login;

import com.alex.game.dblog.core.DbLog;
import com.alex.game.dblog.core.TableType;
import com.alex.game.dblog.core.annotation.Column;
import com.alex.game.dblog.core.annotation.LogTable;

/**
 * 玩家在线日志
 * 
 * @author Alex
 * @date 2017年5月11日 下午9:03:21
 */
@LogTable(name="players_online_log", type = TableType.SINGLE)
public class PlayersOnlineLog extends DbLog {

	@Column(name = "pkg_id", type = "varchar(32)", remark = "包id")
	public final String pkgId;
	@Column(name = "num", type = "int(11)", remark = "在线人数")
	public final int num;
	@Column(name = "gaming_num", type = "int(11)", remark = "游戏人数")
	public final int gamingNum;

	public PlayersOnlineLog(String pkgId, int num, int gamingNum) {
		this.pkgId = pkgId;
		this.num = num;
		this.gamingNum = gamingNum;
	}
	
}
