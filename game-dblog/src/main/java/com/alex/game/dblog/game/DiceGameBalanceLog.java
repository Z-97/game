package com.alex.game.dblog.game;

import com.alex.game.dblog.core.DbLog;
import com.alex.game.dblog.core.TableType;
import com.alex.game.dblog.core.annotation.Column;
import com.alex.game.dblog.core.annotation.LogTable;

@LogTable(name = "dice_game_balance_Log", type = TableType.SINGLE)
public class DiceGameBalanceLog extends DbLog {
	@Column(name = "room_id", type = "int(11)", remark = "房间id")
	public final int roomId;
	@Column(name = "room_name", type = "varchar(32)", remark = "房间名称")
	public final String roomName;
	@Column(name = "table_id", type = "int(11)", remark = "桌子id")
	public final int tableId;
	@Column(name = "banker", type = "bigint(20)", remark = "庄家id(0:系统)")
	public final long banker;
	@Column(name = "banker_name", type = "varchar(32)", remark = "庄家昵称")
	public final String bankerName;
	@Column(name = "banker_tax", type = "bigint(20)", remark = "庄家税收(系统做庄税收为0)")
	public final long bankerTax;
	@Column(name = "banker_win_gold", type = "bigint(20)", remark = "庄家赢金币")
	public final long bankerWinGold;
	@Column(name = "banker_lose_gold", type = "bigint(20)", remark = "庄家输金币")
	public final long bankerLoseGold;
	@Column(name = "total_tax", type = "bigint(20)", remark = "合计税收")
	public final long totalTax;

	@Column(name = "total_big_bet", type = "bigint(20)", remark = "压大的总金币")
	public final long totalBigBet;

	@Column(name = "total_small_bet", type = "bigint(20)", remark = "压小的总金币")
	public final long totalSmallBet;

	@Column(name = "total_same_bet", type = "bigint(20)", remark = "压豹子的总金币")
	public final long totalSameBet;
	@Column(name = "round", type = "bigint(20)", remark = "局id")
	public final long round;
	@Column(name = "dice_type", type = "int(11)", remark = "1大2小3豹子")
	public final long diceType;
	@Column(name = "dice", type = "varchar(64)", remark = "骰子牌型")
	public final String dice;
	public DiceGameBalanceLog(int roomId, String roomName, int tableId, long banker, String bankerName, long bankerTax,
			long bankerWinGold, long bankerLoseGold, long totalTax, long totalBigBet, long totalSmallBet,
			long totalSameBet, long round, long diceType,String dice) {
		super();
		this.roomId = roomId;
		this.roomName = roomName;
		this.tableId = tableId;
		this.banker = banker;
		this.bankerName = bankerName;
		this.bankerTax = bankerTax;
		this.bankerWinGold = bankerWinGold;
		this.bankerLoseGold = bankerLoseGold;
		this.totalTax = totalTax;
		this.totalBigBet = totalBigBet;
		this.totalSmallBet = totalSmallBet;
		this.totalSameBet = totalSameBet;
		this.round = round;
		this.diceType = diceType;
		this.dice=dice;
	}

}
