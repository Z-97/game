package com.alex.game.dblog.plaza;

import com.alex.game.dbdata.dom.PlayerDom;
import com.alex.game.dblog.PlayerLog;
import com.alex.game.dblog.core.TableType;
import com.alex.game.dblog.core.annotation.Column;
import com.alex.game.dblog.core.annotation.LogTable;

/**
 * 绑定手机
 * 
 * @author yougo 2017年5月23日
 */
@LogTable(name = "binding_phone_log", type = TableType.SINGLE)
public class BindingPhoneLog extends PlayerLog {

	@Column(name = "binding_phone", type = "varchar(62)", remark = "绑定手机号码")
	public final String bindingPhone;

	public BindingPhoneLog(PlayerDom player) {
		super(player);
		this.bindingPhone = player.getPhone();

	}

}
