/**
 * Copyright (c) 2015, Alex All Rights Reserved.
 * 
 */
package com.alex.game.dblog.recharge;

import com.alex.game.dbdata.dom.PlayerDom;
import com.alex.game.dblog.PlayerLog;
import com.alex.game.dblog.core.TableType;
import com.alex.game.dblog.core.annotation.Column;
import com.alex.game.dblog.core.annotation.LogTable;

/**
 * 玩家充值日志
 * 
 * @author Alex
 * @date 2015年9月2日 上午10:16:29
 *
 */
@LogTable(name = "recharge_log", type = TableType.MONTH)
public class RechargeLog extends PlayerLog {
	@Column(name = "agent_id", type = "bigint(20)", remark = "代理id")
	public final long agentId;
	@Column(name = "gold", type = "bigint(20)", remark = "充值金币")
	public final long gold;
	@Column(name = "recharge_type", type = "int(11)", remark = "充值类型0普通充值1代理充值2管理员充值3代理充值退款")
	public final int rechargeType;
	@Column(name = "pay_type", type = "int(11)", remark = "支付类型0无1支付宝2微信3银行卡4苹果支付(IAP)")
	public final int payType;

	public RechargeLog(PlayerDom player,long agentId, long gold, int rechargeType, int payType) {
		super(player);
		this.agentId=agentId;
		this.gold = gold;
		this.rechargeType = rechargeType;
		this.payType = payType;
	}
}
