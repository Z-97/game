package com.alex.game.games.dice.manager;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alex.game.common.Game;
import com.alex.game.common.util.IdGenerator;
import com.alex.game.common.util.RandomUtil;
import com.alex.game.core.concurrent.TaskExecutor;
import com.alex.game.dbdic.dic.DiceRoomDic;
import com.alex.game.dbdic.dic.NickNameDic;
import com.alex.game.dbdic.dic.domwrapper.DiceRoomDicWrapper;
import com.alex.game.dice.DiceProto.BetAreaInfo;
import com.alex.game.games.dice.struct.AreaBetInfo;
import com.alex.game.games.dice.struct.DiceRoom;
import com.alex.game.games.dice.struct.DiceSeat;
import com.alex.game.games.dice.struct.DiceStage;
import com.alex.game.games.dice.struct.DiceTable;
import com.alex.game.player.manager.PlayerMgr;
import com.alex.game.schedule.manager.ScheduleMgr;
import com.alex.game.server.ExecutorMgr;
import com.google.inject.Inject;

public class DiceRobotScheduler {

	private static final Logger LOG = LoggerFactory.getLogger(DiceRobotScheduler.class);
	@Inject
	private DiceMsgMgr diceMsgMgr;
	@Inject
	private DiceRoomDic roomDic;
	@Inject
	private DiceDataMgr dataMgr;
	@Inject
    private ScheduleMgr scheduleMgr;
	// 游戏线程executor
	public final TaskExecutor gameExecutor;
	@Inject
	private PlayerMgr playerMgr;
	@Inject
	private DiceMgr diceMgr;
	@Inject
	private NickNameDic nickNameDic;
	@Inject
	public DiceRobotScheduler(ExecutorMgr executorMgr) {
		this.gameExecutor = executorMgr.getGameExecutor(Game.DICE);
	} 
	
	/**
	 * 
	 * @param table
	 */
	public void scheduleAddRobot(DiceRoom diceRoom) {
		DiceRoomDicWrapper roomDom = roomDic.get(diceRoom.id);
		if (diceRoom.robotNum() <= roomDom.getRobotNum()) {
			int addRobotNum = roomDom.getRobotNum() - diceRoom.robotNum();
			int tableRobot = 0;
			if (addRobotNum > 3) {
				tableRobot = RandomUtil.random(2, 4);
				addRobotNum = addRobotNum - tableRobot;
			} else {
				tableRobot = addRobotNum;
				addRobotNum = 0;
			}
			for (int j = 0; j < tableRobot; j++) {
				
				int robotGold = RandomUtil.random(roomDom.getRobotLowerGold(), roomDom.getRobotUpperGold());
				int robotRound = RandomUtil.random(roomDom.getRobotLowerRound(), roomDom.getRobotUpperRound());
				int enterTableTime = RandomUtil.random(2, 26);
				
				DiceSeat emptySeat = diceRoom.findEmptySeat();
				scheduleMgr.schedule(() -> scheduleEnterTable(emptySeat, robotGold, robotRound), enterTableTime-1,
						TimeUnit.SECONDS, gameExecutor);

			}
		}
		
		int robotEnterRoomTime = RandomUtil.random(2, 11);
		scheduleMgr.schedule(() -> scheduleAddRobot(diceRoom), robotEnterRoomTime-1, TimeUnit.SECONDS, gameExecutor);
	}
	/**
	 * 机器人进入房间
	 * @param robotSeat
	 * @param robotGold
	 * @param robotRound
	 */
	private void scheduleEnterTable(DiceSeat seat, int robotGold, int robotRound) {
		if (seat.playerId != 0) {
			return;
		}
		DiceRoomDicWrapper roomDom = roomDic.get(seat.table.room.id);
		if (robotGold < roomDom.getLower()) {
			return;
		}
		DiceTable table = seat.table;
		seat.enterTblTime = System.currentTimeMillis();
		seat.online = true;
		seat.playerId = IdGenerator.nextId();
		int nameIndex=RandomUtil.random(0,nickNameDic.values().size()-1);
		String nickName=nickNameDic.values().get(nameIndex).getName();
		seat.nickName = nickName;
		seat.icon = RandomUtil.random(1, 8);
		seat.gold = robotGold;
		seat.robotRound = robotRound;
		seat.isRobot=true;
		// 更新玩家座位
		dataMgr.updatePlayerSeats(seat.playerId, seat);
		LOG.info("[骰子]机器人玩家[{}][{}]进入房间[{}]成功", seat.nickName, seat.playerId,table.room.name);
	}
	/**
	 * 调度桌子下注
	 * 
	 * @param table
	 */
	void scheduleTableBet(DiceTable table) {
		int stageTime = (int) table.stageFuture.getDelay(TimeUnit.MILLISECONDS);
		for (DiceSeat seat : table.seats) {
			if (seat.playerId != 0) {
				seat.robotRound--;
				if (seat.isRobot) {
					long nextTime=RandomUtil.random(1000, stageTime);
					scheduleMgr.schedule(() -> bet(seat), nextTime, TimeUnit.MILLISECONDS, gameExecutor);
				}
			}
		}
		
	}
	private void bet(DiceSeat seat) {
		DiceTable table = seat.table;
		if (table.stage != DiceStage.BET) {
			LOG.info("[骰子]玩家[{}][{}]下注失败当前状态[{}]", seat.nickName, seat.playerId, table.stage.desc);
			return;
		}
		if(seat.gold<=0) {
			LOG.info("[骰子]玩家金币不足[{}]，[{}][{}]下注失败", seat.gold,seat.nickName, seat.playerId);
			return;
		}
		DiceRoomDicWrapper roomDom = roomDic.get(seat.table.room.id);
		int index = RandomUtil.random(0, roomDom.ableBetChips().size()-1);
		long betGold = (long) roomDom.ableBetChips().toArray()[index];
		if (seat.gold < betGold) {
			LOG.info("[骰子]玩家金币不足[{}]下注金额[{}]，[{}][{}]下注失败当前状态", seat.gold,betGold,seat.nickName, seat.playerId);
			return;
		}
		int area=1;
		int random = RandomUtil.random(100);
		if(random>45&&random <= 95) {
			area=2;
		}
		
		if(random>95) {
			area=3;
		}
		DiceSeat banker = table.banker;
		long bankerGold =0;
		// 非系统庄检查金币上限
		if (banker.playerId > 0) {
			bankerGold = playerMgr.selectById(banker.playerId).gold();
			// 1大区2小区3豹子区
			long totalBigGold = table.areaBets.get(1).getGold();
			long totalSmalGold = table.areaBets.get(2).getGold();
			long totalSameGold = table.areaBets.get(3).getGold();
			boolean isCanBt = diceMgr.isCanBet(totalBigGold, totalSmalGold, totalSameGold, bankerGold, betGold, area);
			if (!isCanBt) {
				LOG.info("[骰子]玩家[{}][{}]下注失败,超过庄家限额", seat.gold,betGold,seat.nickName);
				return;
			}
		}
		
		doRobotBet(seat, betGold, area);
	
		int stageTime = (int) table.stageFuture.getDelay(TimeUnit.MILLISECONDS);
		if(stageTime>1000) {
			int randomTime=RandomUtil.random(stageTime*1000);
			scheduleMgr.schedule(() -> bet(seat), RandomUtil.random(1000, randomTime), TimeUnit.MILLISECONDS, gameExecutor);
			
		}
	}

	void doRobotBet(DiceSeat seat,long betGold,int area) {
		DiceTable table = seat.table;
		seat.gold=seat.gold-betGold;
		// 玩家区域下注
		AreaBetInfo playerAreaBet = seat.areaBets.get(area);
		// 桌子区域下注
		AreaBetInfo tblAreaBet = table.areaBets.get(area);
		seat.totalBet += betGold;
		seat.sumBets += betGold;
		if (area == 1) {
			table.totalBigBet += betGold;
		} else if (area == 2) {
			table.totalSmallBet += betGold;
		} else {
			table.totalSameBet += betGold;
		}
		BetAreaInfo.Builder betAreaInfo = BetAreaInfo.newBuilder();
		betAreaInfo.setArea(area);
		betAreaInfo.setBetGold(betGold);
		betAreaInfo.setPlayerId(seat.playerId);
		table.betAreaInfo.add(betAreaInfo.build());

		playerAreaBet.setGold(playerAreaBet.getGold() + betGold);
		tblAreaBet.setGold(tblAreaBet.getGold() + betGold);
		
		// 发送下注成功
		diceMsgMgr.sendResBetDiceMsg(seat, area, betGold);
		LOG.info("[骰子]玩家[{}][{}]下注区域[{}][{}]成功", seat.nickName, seat.playerId, area, betGold);
	}
	/**
	 * 调度桌子玩家离开
	 * 
	 * @param table
	 */
	void scheduleTableExit(DiceTable table) {
		for (DiceSeat seat : table.seats) {
			if(seat.isRobot&&seat.robotRound<1) {
				seat.clear();
			}
		}
	}
	/**
	 * 调度桌子玩家申请庄家
	 * 
	 * @param table
	 */
	void scheduleApplyBanker(DiceTable table) {
		
	}
}
