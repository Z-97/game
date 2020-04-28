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
 * 转账日志
 * 
 * @author Alex
 * @date 2017年7月6日 下午3:22:01
 */
@LogTable(name="transfer_gold_log", type=TableType.MONTH)
public class TransferGoldLog extends PlayerLog {
	@Column(name="target_player", type="bigint(20)", remark="转账的目标玩家")
	public final long targetPlayer;
	@Column(name="before_Gold", type="bigint(20)", remark="改变前身上金币")
	public final long beforeGold;
	@Column(name="before_safe_gold", type="bigint(20)", remark="改变前保险箱金币")
	public final long beforeSafeGold;
	@Column(name="after_gold", type="bigint(20)", remark="改变后身上金币")
	public final long afterGold;
	@Column(name="after_safe_gold", type="bigint(20)", remark="改变后保险箱金币")
	public final long afterSafeGold;
	@Column(name="change_gold", type="bigint(20)", remark="改变的玩家身上金币(正：增加，负：减少)")
	public final long changeGold;
	@Column(name="change_safe_gold", type="bigint(20)", remark="改变的玩家保险箱金币(正：增加，负：减少)")
	public final long changeSafeGold;
	public TransferGoldLog(PlayerDom player, long targetPlayer, long beforeGold, long beforeSafeGold, long afterGold, long afterSafeGold) {
		super(player);
		this.targetPlayer = targetPlayer;
		this.beforeGold = beforeGold;
		this.beforeSafeGold = beforeSafeGold;
		this.afterGold = afterGold;
		this.afterSafeGold = afterSafeGold;
		this.changeGold = afterGold - beforeGold;
		this.changeSafeGold = afterSafeGold - beforeSafeGold;
		
	}

}
