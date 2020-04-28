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
 * 存/取钱日志
 * 
 * @author Alexte 2015年12月28日 上午11:30:16  
 *
 */
@LogTable(name="deposite_withdraw_gold_log", type=TableType.MONTH)
public class DepositeWithdrawGoldLog extends PlayerLog {

	@Column(name="type", type="tinyint(4)", remark="类型(0:存款,1:取款)")
	public final short type;
	@Column(name="before_gold", type="bigint(20)", remark="改变前身上金币")
	public final long beforeGold;
	@Column(name="before_bank_gold", type="bigint(20)", remark="改变前银行金币")
	public final long beforeBankGold;
	@Column(name="after_gold", type="bigint(20)", remark="改变后身上金币")
	public final long afterGold;
	@Column(name="after_bank_gold", type="bigint(20)", remark="改变后银行金币")
	public final long afterBankGold;
	@Column(name="change_gold", type="bigint(20)", remark="改变的玩家身上金币(正：增加，负：减少)")
	public final long changeGold;
	@Column(name="change_bank_gold", type="bigint(20)", remark="改变的玩家银行金币(正：增加，负：减少)")
	public final long changeBankGold;
	
	/**
	 * @param player
	 * @param type			类型(0:存款,1:取款)
	 * @param beforeGold
	 * @param beforeBankGold
	 * @param afterGold
	 * @param afterBankGold
	 */
	public DepositeWithdrawGoldLog(PlayerDom player, int type, long beforeGold, long beforeBankGold, long afterGold, long afterBankGold) {
		super(player);
		this.type = (short)type;
		this.beforeGold = beforeGold;
		this.beforeBankGold = beforeBankGold;
		this.afterGold = afterGold;
		this.afterBankGold = afterBankGold;
		this.changeGold = afterGold - beforeGold;
		this.changeBankGold = afterBankGold - beforeBankGold;
	}

}
