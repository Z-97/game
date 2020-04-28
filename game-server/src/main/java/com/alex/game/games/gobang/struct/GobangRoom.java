package com.alex.game.games.gobang.struct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import com.alex.game.common.Game;
import com.alex.game.common.util.IdGenerator;
import com.alex.game.games.common.AbstractRoom;

public class GobangRoom extends AbstractRoom {

	public final List<GobangTable> tables;
	public ScheduledFuture<?> robotFuture = null;
	public GobangRoom(int roomId, String roomName, int tableNum) {
		super(roomId, roomName);
		this.tables = createTables(tableNum);
	}

	private List<GobangTable> createTables(int tableNum) {
		List<GobangTable> tables = new ArrayList<>();
		for (int tableId = 1; tableId <= tableNum; tableId++) {
			int id=(int) (IdGenerator.nextId()+tableId);
			tables.add(new GobangTable(id, this));
		}
		return Collections.unmodifiableList(tables);
	}

	@Override
	public Game game() {
		return Game.GOBANG;
	}

	/**
	 * 查找全空位座位
	 * @return
	 */
	public GobangSeat findEmptySeat() {
		List<GobangSeat> emptySeats = new ArrayList<>();
		GobangSeat emptySeat = null;
		for (GobangTable table : this.tables) {
			boolean hasPlayer = false;
			GobangSeat tableEmptySeat = null;
			for (GobangSeat seat : table.seats) {
				if (seat.playerId == 0) {
					tableEmptySeat = seat;
				} else {
					hasPlayer = true;
				}
			}
			if (!hasPlayer && tableEmptySeat != null) {
				emptySeats.add(tableEmptySeat);
				
			}
		}
		Collections.shuffle(emptySeats);
		if(!emptySeats.isEmpty()) {
			emptySeat=emptySeats.get(0);
		}
		return emptySeat;
	}
	/**
	 * 通过密码查找座位
	 * @param pwd
	 * @return
	 */
	public GobangSeat findEmptySeat(int pwd) {
		GobangSeat emptySeat = null;
		for (GobangTable table : this.tables) {
			if(table.id==pwd) {
				GobangSeat tableEmptySeat = null;
				for (GobangSeat seat : table.seats) {
					if (seat.playerId == 0) {
						tableEmptySeat = seat;
						return tableEmptySeat;
					}
				}
			}
		}
		return emptySeat;
	}
}
