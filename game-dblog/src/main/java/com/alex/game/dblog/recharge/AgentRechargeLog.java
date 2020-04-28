package com.alex.game.dblog.recharge;

import com.alex.game.dbdata.dom.PlayerDom;
import com.alex.game.dblog.PlayerLog;
import com.alex.game.dblog.core.TableType;
import com.alex.game.dblog.core.annotation.Column;
import com.alex.game.dblog.core.annotation.LogTable;

/**
 * 代理充值兑换退款日志
 * 
 * @author yougo 2017年6月13日
 */
@LogTable(name = "agent_recharge_log", type = TableType.MONTH)
public class AgentRechargeLog extends PlayerLog {

	@Column(name = "money", type = "decimal(19,2)", remark = "交易金额")
	public final double money;
	@Column(name = "gold", type = "bigint(20)", remark = "交易金币")
	public final long gold;
	@Column(name = "transaction", type = "int(11)", remark = "交易类型:1,RECHARGE,2,REFUND,3,EXCHANGE")
	public final int transaction;
	@Column(name = "operation_id", type = "bigint(20)", remark = "后台操作人id")
	public final long operationId;
	@Column(name = "operation_name", type = "varchar(62)", remark = "后台操作人名称")
	public final String operationName;
	@Column(name="before_Gold", type="bigint(20)", remark="改变前身上金币")
	public final long beforeGold;
	@Column(name="after_gold", type="bigint(20)", remark="改变后身上金币")
	public final long afterGold;
	@Column(name="before_safe_gold", type="bigint(20)", remark="改变前保险箱金币")
	public final long beforeSafeGold;
	@Column(name="after_safe_gold", type="bigint(20)", remark="改变后保险箱金币")
	public final long afterSafeGold;
	public AgentRechargeLog(PlayerDom player, double money, long gold, int transaction, long operationId,
			String operationName,long beforeGold,long afterGold,long beforeSafeGold,long afterSafeGold) {
		super(player);
		this.money = money;
		this.gold = gold;
		this.transaction = transaction;
		this.operationId = operationId;
		this.operationName = operationName;
		this.beforeGold=beforeGold;
		this.afterGold=afterGold;
		this.beforeSafeGold = beforeSafeGold;
		this.afterSafeGold = afterSafeGold;
		
	}

}
