package com.alex.game.games.dice.struct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import com.alex.game.common.Game;
import com.alex.game.common.util.IdGenerator;
import com.alex.game.games.common.AbstractRoom;

public class DiceRoom extends AbstractRoom {
	// 桌子
	public final List<DiceTable> tables;
	public ScheduledFuture<?> robotFuture = null;
	public DiceRoom(int id, String name,int table, int tablePlayerNum) {
		super(id, name);
		this.tables = createTables(table, tablePlayerNum);
	}

	@Override
	public Game game() {
		return Game.DICE;
	}
	private List<DiceTable> createTables(int tableNum, int tablePlayerNum) {
		List<DiceTable> tables = new ArrayList<>();
		
		for (int tableId = 1; tableId <= tableNum; tableId++) {
			int id=(int) (IdGenerator.nextId()+tableId);
			tables.add(new DiceTable(id, this, tablePlayerNum));
		}
		return Collections.unmodifiableList(tables);
	}
	
	/**
	 * 查找空位置,优先查找有人的桌子
	 * @param room
	 * @return null,当前没有位置
	 */
	public DiceSeat findEmptySeat() {
		DiceSeat emptySeat = null;
		for (DiceTable table : this.tables) {
			boolean hasPlayer = false;
			DiceSeat tableEmptySeat = null;

			for (DiceSeat seat : table.seats) {
				if (seat.playerId == 0) {
					emptySeat = seat;
					tableEmptySeat = seat;
				} else {
					hasPlayer = true;
				}
			}

			if (hasPlayer && tableEmptySeat != null) {
				return tableEmptySeat;
			}
		}

		return emptySeat;
	}
	/**
	 * 机器人玩家人数
	 * 
	 * @return
	 */
	public int robotNum() {
		int playersNum = 0;
		for (DiceTable table : this.tables) {
			for (DiceSeat seat : table.seats) {
				if (seat.playerId != 0&&seat.isRobot) {
					playersNum++;
				}
			}
		}
		return playersNum;
	}
}
