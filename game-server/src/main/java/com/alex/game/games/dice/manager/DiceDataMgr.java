package com.alex.game.games.dice.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alex.game.dbdic.dic.DiceRoomDic;
import com.alex.game.dbdic.dom.DiceRoomDom;
import com.alex.game.games.dice.struct.DiceRoom;
import com.alex.game.games.dice.struct.DiceSeat;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DiceDataMgr {

	private static final Logger LOG = LoggerFactory.getLogger(DiceDataMgr.class);
	// 房间字典
	private final DiceRoomDic roomDic;
	// 房间
	final Map<Integer, DiceRoom> rooms;
	public final int roomId=1;
	// 玩家的座位
	private final ConcurrentHashMap<Long, DiceSeat> playerSeats = new ConcurrentHashMap<>();
	public static final  int sameMultiples=25;
	public static final  int multiples=2;
	@Inject
	public DiceDataMgr(DiceRoomDic roomDic) {
		this.roomDic = roomDic;
		this.rooms = createRooms();
	}
	
	/**
	 * 创建房间
	 * 
	 * @return
	 */
	private Map<Integer, DiceRoom> createRooms() {
		Map<Integer, DiceRoom> rooms = new HashMap<>();
		for (DiceRoomDom roomDom : roomDic.values()) {
			rooms.put(roomDom.getId(), new DiceRoom(roomDom.getId(), roomDom.getName(), roomDom.getTable(), roomDom.getTablePlayerNum()));
			LOG.info("[骰子]创建房间[{}][{}]", roomDom.getId(), roomDom.getName());
		}

		return rooms;
	}

	/**
	 * 获取座位
	 * 
	 * @param playerId
	 * @return
	 */
	public DiceSeat getPlayerSeat(long playerId) {

		return playerSeats.get(playerId);
	}

	/**
	 * 更新玩家座位
	 * 
	 * @param playerId
	 * @param seat
	 * @return
	 */
	public void updatePlayerSeats(long playerId, DiceSeat seat) {
		playerSeats.put(playerId, seat);
	}

	/**
	 * 移除玩家seat
	 * 
	 * @param playerId
	 */
	public void removePlayerSeat(long playerId) {
		playerSeats.remove(playerId);
	}

	/**
	 * 获取房间
	 * 
	 * @param roomId
	 * @return
	 */
	public DiceRoom getRoom(int roomId) {

		return rooms.get(roomId);
	}
	
	/**
	 * 玩家人数
	 * 
	 * @return
	 */
	public int playersNum() {
		return playerSeats.size();
	}
	
	public ConcurrentHashMap<Long, DiceSeat> getPlayerSeats() {
		return playerSeats;
	}
	
	/**
	 * 获取所有房间集合
	 * @return
	 */
	public Collection<DiceRoom> rooms() {
		return rooms.values();
	}
	
}
