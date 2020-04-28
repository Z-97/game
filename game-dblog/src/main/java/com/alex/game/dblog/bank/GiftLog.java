package com.alex.game.dblog.bank;
import com.alex.game.dbdata.dom.PlayerDom;
import com.alex.game.dblog.PlayerLog;
import com.alex.game.dblog.core.TableType;
import com.alex.game.dblog.core.annotation.Column;
import com.alex.game.dblog.core.annotation.LogTable;
/**
 * 礼物日志
 * @author yejuhua
 *
 */
@LogTable(name="gift_log", type=TableType.MONTH)
public class GiftLog extends PlayerLog {
	@Column(name="action", type = "int(11)", remark = "GiftAction")
	public final int action;
	@Column(name="item_id", type = "int(11)", remark = "物品id")
	public final int itemId;
	@Column(name="item_num", type = "int(11)", remark = "物品数量")
	public final int itemNum;
	@Column(name="item_gold", type = "bigint(20)", remark = "价值金币")
	public final long itemGold;
	public GiftLog(PlayerDom player,int action,int itemId,int itemNum,long itemGold) {
		super(player);
		this.action=action;
		this.itemId=itemId;
		this.itemNum=itemNum;
		this.itemGold=itemGold;
	}

}
