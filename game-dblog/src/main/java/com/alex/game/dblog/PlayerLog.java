/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.dblog;

import com.alex.game.dbdata.dom.PlayerDom;
import com.alex.game.dblog.core.DbLog;
import com.alex.game.dblog.core.annotation.Column;

/**
 * 玩家日志基类
 * 
 * @author Alex
 * @date 2016/8/4 15:25
 */
public abstract class PlayerLog extends DbLog {

	@Column(name = "player_id", type = "bigint(20)", remark = "玩家id")
	public final long playerId;
	@Column(name = "user_name", type = "varchar(32)", remark = "用户名")
	public final String userName;
	@Column(name = "player_type", type = "int(11)", remark = "玩家类型(0:普通玩家,1:代理,2:线上推广员,3:线下推广员)")
	public final int playerType;
	@Column(name = "nick_name", type = "varchar(32)", remark = "角色名(昵称)")
	public final String nickName;
	@Column(name = "channel_id", type = "varchar(32)", remark = "渠道id")
	public final String channelId;
	@Column(name = "pkg_id", type = "varchar(32)", remark = "包id")
	public final String packageId;
	@Column(name = "ip", type = "varchar(128)", remark = "ip")
	public final String ip;
	@Column(name = "plat_id", type = "int(11)", remark = "平台id")
	public final int platId;

	public PlayerLog(PlayerDom player) {
		this(player.getId(), player.getUserName(), player.getNickName(), player.getChannelId(), player.getPackageId(),
				player.getLoginIp(), player.getPlayerType(), player.getPlatId());
	}

	public PlayerLog(long playerId, String userName, String nickName, String channelId, String packageId, String ip,
			int playerType, int platId) {
		this.playerId = playerId;
		this.userName = userName;
		this.nickName = nickName;
		this.channelId = channelId;
		this.packageId = packageId;
		this.ip = ip;
		this.playerType = playerType;
		this.platId = platId;
	}

}
