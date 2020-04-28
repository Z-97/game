/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.dblog.bank;

import com.alex.game.dbdata.dom.PlayerDom;
import com.alex.game.dblog.LogAction;
import com.alex.game.dblog.PlayerLog;
import com.alex.game.dblog.core.TableType;
import com.alex.game.dblog.core.annotation.Column;
import com.alex.game.dblog.core.annotation.LogTable;

/**
 * 银行金币变化日志
 *
 * @author Alex
 * @date 2016/8/5 15:35
 */
@LogTable(name="bank_gold_log", type = TableType.DAY)
public class BankGoldLog extends PlayerLog {
    @Column(name="before", type = "bigint(20)", remark = "改变前")
    public final long before;
    @Column(name="after", type = "bigint(20)", remark = "改变后")
    public final long after;
    @Column(name="change", type = "bigint(20)", remark = "改变的值(正：增加，负：减少)")
    public final long change;
    @Column(name="action", type = "int(11)", remark = "LogAction")
    public final int action;
	@Column(name="desc", type = "varchar(256)", remark = "描述说明")
	public final String desc;

    public BankGoldLog(PlayerDom player, long before, long after, LogAction action) {
       this(player, before, after, action, null);
    }

	public BankGoldLog(PlayerDom player, long before, long after, LogAction action, String desc) {
		super(player);
        this.before = before;
        this.after = after;
        this.change = after - before;
        this.action = action.id;
		this.desc = desc;
	}
    
}
