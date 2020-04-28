package com.alex.game.games.gobang.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alex.game.games.gobang.struct.GobangRoom;
import com.alex.game.games.gobang.struct.GobangSeat;
import com.google.inject.Inject;
import com.google.inject.Singleton;
@Singleton
public class GobangDataMgr {
	private static final Logger LOG = LoggerFactory.getLogger(GobangDataMgr.class);
	// 房间
	private final Map<Integer, GobangRoom> rooms;
	// 玩家的座位
	private final ConcurrentHashMap<Long, GobangSeat> playerSeats = new ConcurrentHashMap<>();
	public final int roomId=1;
	@Inject
	public GobangDataMgr() {
		this.rooms = createRooms();
	}
	private Map<Integer, GobangRoom> createRooms() {
		Map<Integer, GobangRoom> rooms = new HashMap<>();
		String roomName="五子棋";
		rooms.put(roomId, new GobangRoom(1,roomName,100));
		LOG.info("[五子棋]创建房间[{}][{}]", roomId, roomName);
		return rooms;
	}
	
	/**
	 * 获取座位
	 * 
	 * @param playerId
	 * @return
	 */
	public GobangSeat getPlayerSeat(long playerId) {
		return playerSeats.get(playerId);
	}

	/**
	 * 更新玩家座位
	 * 
	 * @param playerId
	 * @param seat
	 * @return
	 */
	public void updatePlayerSeats(long playerId, GobangSeat seat) {
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
	public GobangRoom getRoom(int roomId) {

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
	public ConcurrentHashMap<Long, GobangSeat> getPlayerSeats() {
		return playerSeats;
	}
	
	/**
	 * 获取所有房间集合
	 * @return
	 */
	public Collection<GobangRoom> rooms() {
		
		return rooms.values();
	}

}
