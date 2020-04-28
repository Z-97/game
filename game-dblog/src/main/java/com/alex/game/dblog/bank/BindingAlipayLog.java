package com.alex.game.dblog.bank;
import com.alex.game.dbdata.dom.PlayerDom;
import com.alex.game.dblog.PlayerLog;
import com.alex.game.dblog.core.TableType;
import com.alex.game.dblog.core.annotation.Column;
import com.alex.game.dblog.core.annotation.LogTable;
/***
 * 支付宝账号绑定日志
 * @author yougo
 *2017年5月10日
 */
@LogTable(name="binding_alipay_log", type = TableType.SINGLE)
public class BindingAlipayLog extends PlayerLog {

	@Column(name="alipay", type = "varchar(62)", remark = "支付宝账号")
	public final String alipay;
	@Column(name="alipay_name", type = "varchar(62)", remark = "支付宝姓名")
	public final String alipayName;
	public BindingAlipayLog(PlayerDom player,String alipay,String alipayName) {
		super(player);
		this.alipay = alipay;
		this.alipayName = alipayName;
		
	}

}
