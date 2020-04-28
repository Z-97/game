/**
 * Copyright (c) 2015, Alex All Rights Reserved. 
 */  
package com.alex.game.dblog.bank;

import com.alex.game.dbdata.dom.PlayerDom;
import com.alex.game.dblog.PlayerLog;
import com.alex.game.dblog.core.TableType;
import com.alex.game.dblog.core.annotation.Column;
import com.alex.game.dblog.core.annotation.LogTable;

/**
 * 调用兑换银行金币为现金日志
 * 
 * @author Alex
 * @date 2017年4月18日 下午9:54:07
 */
@LogTable(name="call_exchange_cash_log", type=TableType.MONTH)
public class CallExchangeCashLog extends PlayerLog {

	@Column(name="exchange_gold", type="bigint(20)", remark="兑换现金的银行金币")
	public final long exchangeGold;
	@Column(name="order", type="bigint(20)", remark="订单号")
	public final long order;
	@Column(name="success", type="tinyint(1)", remark="是否成功")
	public final boolean success;
	@Column(name="message", type="varchar(2048)", remark="错误消息")
	public final String message;
	
	public CallExchangeCashLog(PlayerDom player, long exchangeGold, long order, boolean success, String message) {
		super(player);
		this.exchangeGold = exchangeGold;
		this.order = order;
		this.success = success;
		this.message = message;
	}
	
	
}
