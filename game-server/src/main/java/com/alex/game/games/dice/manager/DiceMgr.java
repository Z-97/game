package com.alex.game.games.dice.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alex.game.common.Game;
import com.alex.game.common.tuple.Pair;
import com.alex.game.common.util.RandomUtil;
import com.alex.game.core.concurrent.TaskExecutor;
import com.alex.game.dbdata.dom.PlayerDom;
import com.alex.game.dbdic.dic.DiceRoomDic;
import com.alex.game.dbdic.dic.DiceTimeDic;
import com.alex.game.dbdic.dic.domwrapper.DiceRoomDicWrapper;
import com.alex.game.dbdic.dom.DiceRoomDom;
import com.alex.game.dblog.LogAction;
import com.alex.game.dblog.core.DbLogService;
import com.alex.game.dblog.game.DiceGameBalanceLog;
import com.alex.game.dice.DiceProto.BetAreaInfo;
import com.alex.game.event.manager.EventMgr;
import com.alex.game.event.struct.game.ExitRoomEvent;
import com.alex.game.event.struct.login.PlayerLoginSuccessEvent;
import com.alex.game.event.struct.login.PlayerLogoutEvent;
import com.alex.game.games.common.dice.DiceType;
import com.alex.game.games.dice.struct.ApplicantInfo;
import com.alex.game.games.dice.struct.AreaBetInfo;
import com.alex.game.games.dice.struct.DiceRoom;
import com.alex.game.games.dice.struct.DiceSeat;
import com.alex.game.games.dice.struct.DiceStage;
import com.alex.game.games.dice.struct.DiceTable;
import com.alex.game.games.dice.struct.PlayerDiceRankInfo;
import com.alex.game.games.dice.struct.PlayerDiceRecord;
import com.alex.game.player.manager.PlayerMgr;
import com.alex.game.player.struct.Player;
import com.alex.game.plaza.redpackage.manager.RedpackageMgr;
import com.alex.game.schedule.manager.ScheduleMgr;
import com.alex.game.server.ExecutorMgr;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DiceMgr {
	private static final Logger LOG = LoggerFactory.getLogger(DiceMgr.class);
	private final EventMgr eventMgr;
	@Inject
	private DiceDataMgr dataMgr;
	// 游戏线程executor
	public final TaskExecutor gameExecutor;
	@Inject
	private DiceMsgMgr diceMsgMgr;
	@Inject
	private DiceTimeDic timeDic;
	@Inject
	private DiceRoomDic roomDic;
	@Inject
	private ScheduleMgr scheduleMgr;

	@Inject
	private PlayerMgr playerMgr;
//	@Inject
//	private DiceRobotScheduler diceRobotScheduler;
	@Inject
	private RedpackageMgr redpackageMgr;
	
	@Inject
	public DiceMgr(EventMgr eventMgr, ExecutorMgr executorMgr) {
		this.eventMgr = eventMgr;
		eventMgr.register(this);
		this.gameExecutor = executorMgr.getGameExecutor(Game.DICE);
	}
//	@Inject
//	public void initRoomSchedule() {
//		for(DiceRoom diceRoom:dataMgr.rooms.values()){
//			diceRoom.robotFuture=scheduleMgr.schedule(() -> diceRobotScheduler.scheduleAddRobot(diceRoom), 5, TimeUnit.SECONDS, gameExecutor);
//		}
//	}
	/**
	 * 订阅玩家退出游戏事件, 在gameExecutor中执行, 避免线程安全问题
	 */
	@Subscribe
	public void onPlayerExitGame(PlayerLogoutEvent event) {
		long playerId = event.player.getId();
		DiceSeat outSeeat = dataMgr.getPlayerSeat(playerId);
		if (outSeeat == null) {// PlayerLogoutEvent是在登陆线程中触发，需要判断一次
			return;
		}

		gameExecutor.execute(() -> {
			DiceSeat seat = dataMgr.getPlayerSeat(playerId);
			// 在游戏线程中还需要判断一次，避免重复退出
			if (seat != null)
				doExitRoom(seat);
		});
	}

	/**
	 * 订阅登陆成功游戏事件,如果在游戏中直接进入房间
	 */
	@Subscribe
	public void onPlayerLoginSuccess(PlayerLoginSuccessEvent event) {
		Player player = event.player;
		DiceSeat outSeeat = dataMgr.getPlayerSeat(player.getId());
		if (outSeeat == null) {// PlayerLoginSuccessEvent是在登陆线程中触发，需要判断一次
			return;
		}

		gameExecutor.execute(() -> {
			DiceSeat seat = dataMgr.getPlayerSeat(player.getId());
			// 在游戏线程中还需要判断一次，避免重复进入
			if (seat != null) {
				diceMsgMgr.sendEnterRoomMsg(seat);
				doEnterRoom(player, seat);
			}
		});
	}

	/**
	 * 开始游戏
	 */
	@Inject
	private void startGame() {
		LOG.info("[骰子]游戏开始");
		for (DiceRoom room : dataMgr.rooms.values()) {
			for (DiceTable table : room.tables) {
				tableReadyStage(table);
			}
		}
	}

//	/**
//	 * 桌子抢庄
//	 * @param table
//	 */
//	private void tableBankerStage(DiceTable table) {
//		if(table.bankerList.size()>0) {
//			tableReadyStage(table);
//			return;
//		}
//		table.stage = DiceStage.BANKER;
//		int time = timeDic.get(table.stage.val).getTime();
//		diceMsgMgr.sendStageMsg(table, table.stage, time);
//		table.stageFuture = scheduleMgr.schedule(() -> tableReadyStage(table), time, TimeUnit.SECONDS, gameExecutor);
//		LOG.info("[骰子]房间[{}]桌子[{}]轮[{}][{}]阶段",
//				table.room.name, table.id, table.round,
//				table.stage.desc);
//	}
	/**
	 * 桌子准备
	 * @param table
	 */
	private void tableReadyStage(DiceTable table) {
		table.stage = DiceStage.READY;
		int time = timeDic.get(table.stage.val).getTime();
		diceMsgMgr.sendStageMsg(table, table.stage, time);
		// 刷新玩家排名
		refreshPlayerRank(table);
		// 刷新座位
		refreshSeats(table);

		// 刷新申请庄家的玩家
		refreshApplyBanker(table);

		// 改变系统庄家
		changeSysBanker(table);
		diceMsgMgr.sendBankerInfoMsg(table);
		diceMsgMgr.sendApplyBankersMsg(table);
		
		table.stageFuture = scheduleMgr.schedule(() -> tableGameStage(table), time, TimeUnit.SECONDS, gameExecutor);
		LOG.info("[骰子]房间[{}]桌子[{}]轮[{}][{}]阶段",
				table.room.name, table.id, table.round,
				table.stage.desc);
	}
	/**
	 * 桌子下注
	 * 
	 * @param table
	 */
	private void tableGameStage(DiceTable table) {
		table.stage = DiceStage.BET;
		int time = timeDic.get(table.stage.val).getTime();
		diceMsgMgr.sendResSixSeatMsg(table);
		diceMsgMgr.sendStageMsg(table, table.stage, time);

		;
		table.round++;
		table.reset();
		table.stageFuture = scheduleMgr.schedule(() -> tableBalanceStage(table), time, TimeUnit.SECONDS, gameExecutor);
//		diceRobotScheduler.scheduleTableBet(table);
		LOG.info("[骰子]房间[{}]桌子[{}]轮[{}]进入[{}]阶段", table.room.name, table.id,
		 table.round, table.stage.desc);
	}

	private void refreshPlayerRank(DiceTable table) {
		table.clearPlayerDiceRecord();
		for (DiceSeat seat : table.seats) {
			if (seat.playerId == 0) {
				continue;
			}
			if(seat.totalBet>0) {
				table.updateDiceRecord(seat, table.round);
			}
			
		}
		//Collections.sort(table.playerDiceRecord);
		TreeSet<Pair<DiceSeat, Long>> sortedPlayers = new TreeSet<>((p1, p2) -> p1.v2 > p2.v2?-1:(p1.v2 < p2.v2?1:0));
		for (DiceSeat seat : table.seats) {
			if (seat.playerId == 0) {
				continue;
			} else if (seat.playerId > 0) {
			    if(seat.isRobot) {
			    	sortedPlayers.add(new Pair<>(seat,seat.gold));
			    }else {
			    	sortedPlayers.add(new Pair<>(seat, playerMgr.selectById(seat.playerId).gold()));
			    }
				
			}
		}
		List<PlayerDiceRecord> goldLike=table.getGodLike();
		long godLikeId=0;
		if(!goldLike.isEmpty()) {
			godLikeId=goldLike.get(0).playerId;
		}
		table.playerRanks.clear();
		int index = 0;
		for (Pair<DiceSeat, Long> soredPlayer : sortedPlayers) {
			if(godLikeId!=soredPlayer.v1.playerId) {
				PlayerDiceRankInfo playerRank = new PlayerDiceRankInfo();
				playerRank.setGold(soredPlayer.v2);
				playerRank.setIcon(soredPlayer.v1.icon);
				playerRank.setNickName(soredPlayer.v1.nickName);
				playerRank.setPlayerId(soredPlayer.v1.playerId);
				table.playerRanks.add(playerRank);
				index++;
				if (index >= DiceTable.PLAYER_RANK_NUM) {
					break;
				}
			}
			
		}
	}

	/**
	 * 结算
	 * 
	 * @param table
	 */
	private void tableBalanceStage(DiceTable table) {
		table.stage = DiceStage.BALANCE;
		int time = timeDic.get(table.stage.val).getTime();
		diceMsgMgr.sendStageMsg(table, table.stage, time);
		table.stageFuture = scheduleMgr.schedule(() -> tableReadyStage(table), time, TimeUnit.SECONDS, gameExecutor);
		table.dices = dealCards(table);
		table.addDiceHistory(table.getWinArea());
		if(table.getWinArea()==3) {
			redpackageMgr.diceRedPackage(table,roomDic.get(table.room.id).getRedPackageId());
		
		}
		balanceTable(table);
		table.clearBanker();
		LOG.info("[骰子]房间[{}]桌子[{}]轮[{}][{}]阶段下注总共[{}]税收为[{}]大小为:[{}][{}]",
		table.room.name, table.id, table.round,
		table.stage.desc,(table.totalBigBet+table.totalSameBet+table.totalSmallBet),table.totalTax,table.getWinDiceType(),table.dices);
	}

	
	/**
	 * 结算
	 * 
	 * @param table
	 */
	private void balanceTable(DiceTable table) {
		DiceSeat banker = table.banker;
		calcTable(table);
		// 结算庄家
		balanceBanker(banker);
		// 结算下注玩家
		balanceSeats(table);
		// 记录数据库结算日志
		logTableBalance(table);

		TreeSet<DiceSeat> sortedWinPlayers=sortSeatWin(table);
		diceMsgMgr.sendResDiceWinInfo(table,sortedWinPlayers);
	}

	private TreeSet<DiceSeat> sortSeatWin(DiceTable table) {
		TreeSet<DiceSeat> ts = new TreeSet<DiceSeat>(new DiceSeatWinGoldComparator());
		for(DiceSeat seat:table.seats) { 
			if(seat.playerId>0&&seat.totalBet>0) {
				ts.add(seat);
			}	
		}
		return ts;
	}
	private class DiceSeatWinGoldComparator implements Comparator<DiceSeat>{
		@Override
		public int compare(DiceSeat o1, DiceSeat o2) {
			long winGold1 = o1.winGold-o1.tax;
			long winGold2 = o2.winGold-o2.tax;
			if(winGold1>winGold2) {
				return 1;
			}
			return 0;
		}
		
	}

	/**
	 * 结算日志
	 * 
	 * @param table
	 */
	private void logTableBalance(DiceTable table) {
		if (table.getTotalBet() == 0) {
			return;
		}
		if (!table.hasPersonPlayer()) {// 没有玩家下注且是系统坐庄不记录
			return;
		}
		DiceSeat banker = table.banker;
		DiceRoom room = table.room;
		long bankerId = 0;
		String bankerName = null;
		long bankerTax = 0;
		long bankerWinGold = 0;
		long bankerLoseGold = 0;
		if (banker != null) {
			bankerId = banker.playerId;
			bankerName = banker.nickName;
			bankerTax = banker.tax;
			bankerWinGold = banker.winGold;
			bankerLoseGold = banker.loseGold;
		}

		DbLogService.log(new DiceGameBalanceLog(room.id, room.name, table.id, bankerId, bankerName, bankerTax,
				bankerWinGold, bankerLoseGold, table.totalTax, table.totalBigBet, table.totalSmallBet,
				table.totalSameBet, table.round, table.getWinArea(), table.dices.toString()));

	}

	/**
	 * 结算玩家
	 * 
	 * @param table
	 */
	private void balanceSeats(DiceTable table) {
		for (DiceSeat seat : table.seats) {
			if (seat.playerId != 0 && seat.playerId != table.banker.playerId && (seat.totalBet > 0)) {
				long winGold = seat.winGold-seat.tax;
				if (winGold > 0) {
					if(seat.isRobot) {
						seat.gold+=winGold;
					}else {
						PlayerDom playerDom = playerMgr.selectById(seat.playerId);
						playerMgr.addGold(playerDom, winGold, true, true, LogAction.DICE_BALANCE, "税收:" + seat.tax / 100f);
					}
					
				}

			}
		}

	}

	/**
	 * 庄家结算
	 * 
	 * @param banker
	 */
	private void balanceBanker(DiceSeat banker) {
		long totalBankerGold = banker.winGold - banker.loseGold-banker.tax;
		PlayerDom bankerDom = playerMgr.selectById(banker.playerId);
		if (banker.playerId != 0) {
			if(banker.isRobot) {
				banker.gold+=totalBankerGold;
			}else {
				playerMgr.addGold(bankerDom, totalBankerGold, true, true, LogAction.DICE_BALANCE, "税收:" + banker.tax / 100);
			}
		
		} else {
			// 系统当庄记录日志
			
		}

	}

	/**
	 * 
	 * 预先计算输赢
	 * 
	 * @param table
	 * @param area  1大区2小区3豹子区
	 * @return
	 */
	private Pair<Boolean, String> preCalcTable(DiceTable table, int area) {
		boolean isSystemWin = false;
		long sysWinGold = 0;
		long robotWinGold = 0;
		long playerWinGold = 0;
		long bankerWinGold = 0;
		long bankerLoseGold = 0;
		// 计算下注玩家的输赢
		for (DiceSeat seat : table.seats) {
			if (seat.playerId != 0 && seat.playerId != table.banker.playerId) {
				long totalSeatWinGold = 0;
				for (AreaBetInfo areaBetInfo : seat.areaBets.values()) {
					if (areaBetInfo.getGold() > 0) {

						if (areaBetInfo.getArea() == area) {
							int multiples = DiceDataMgr.multiples;
							if (areaBetInfo.getArea() == 3) {
								multiples = DiceDataMgr.sameMultiples;
							}
							// 赢钱
							long winGold = areaBetInfo.getGold() * multiples;
							bankerLoseGold += (winGold - areaBetInfo.getGold());
							totalSeatWinGold += winGold;
						} else {
							// 输钱
							long loseGold = seat.loseGold + areaBetInfo.getGold();
							bankerWinGold += loseGold;
						}
					}
				}

				long realWinGold = totalSeatWinGold - seat.totalBet;
				playerWinGold += realWinGold;
				if (seat.isRobot) {
					robotWinGold += realWinGold;
				}
			}
		}

		long totalWinBanker = bankerWinGold - bankerLoseGold;

		if (table.banker.playerId == 0 || table.banker.isRobot) {

			if (table.banker.isRobot) {
				robotWinGold += totalWinBanker;
			} else {
				sysWinGold += totalWinBanker;
			}

		}
		sysWinGold += robotWinGold;

		String result = ("真实系统输赢" + sysWinGold) + ("是否系统庄" + table.banker.playerId + "是否机器人庄:" + table.banker.isRobot
				+ "1大区2小区3豹子区---------->" + area + "玩家总输赢:" + playerWinGold + "---->机器人总输赢:" + robotWinGold
				+ "------------->|庄家总输赢:" + totalWinBanker + "--庄家赢:" + bankerWinGold + "--庄家输:" + bankerLoseGold
				+ "------------>大区下注" + table.totalBigBet + "------------>小区下注" + table.totalSmallBet
				+ "------------>豹子区下注" + table.totalSameBet);

		if (sysWinGold >= 0) {
			isSystemWin = true;
		}
		return new Pair<Boolean, String>(isSystemWin, result);

	}

	/**
	 * 计算桌子输赢
	 * 
	 * @param table
	 * @return
	 */
	private void calcTable(DiceTable table) {
		/**
		 * 要考虑玩家金币不够的情况
		 */
		// 计算下注玩家的输赢
		int winArea = table.getWinArea();
		// 税率百分比
		int taxRate = roomDic.get(table.room.id).getTaxRate();
		long bankerWinGold = 0;
		long bankerLoseGold = 0;
		long totalTax = 0;
		for (DiceSeat seat : table.seats) {
			if (seat.playerId != 0 && seat.playerId != table.banker.playerId) {
				for (AreaBetInfo areaBetInfo : seat.areaBets.values()) {
					if (areaBetInfo.getGold() > 0) {
						if (areaBetInfo.getArea() == winArea) {
							int multiples = DiceDataMgr.multiples;
							if (areaBetInfo.getArea() == 3) {
								multiples = DiceDataMgr.sameMultiples;
							}
							long tax = areaBetInfo.getGold() * taxRate / 100;
							seat.tax = tax;
							// 赢钱
							seat.winGold = areaBetInfo.getGold() * multiples;
							bankerLoseGold += seat.winGold-areaBetInfo.getGold();
							totalTax += seat.tax;
						} else {
							// 输钱
							seat.loseGold = seat.loseGold + areaBetInfo.getGold();
							bankerWinGold += areaBetInfo.getGold();
						}
					}
				}
			}
		}
	
		table.banker.winGold = bankerWinGold;
		table.banker.loseGold = bankerLoseGold;
		long totalBankerGold = bankerWinGold - bankerLoseGold;
		// && table.banker.playerId > 0
		if (totalBankerGold > 0) {
			table.banker.tax = totalBankerGold * taxRate / 100;
			totalTax += table.banker.tax;
		}

		table.bankerLoseGold = bankerLoseGold;
		table.bankerWinGold = bankerLoseGold;
		table.totalTax = totalTax;
	}

	/**
	 * 发牌
	 * 
	 * @param table
	 * @return
	 */
	private List<DiceType> dealCards(DiceTable table) {
		table.dices.clear();
		// 房间控制概率
		int controlRate = roomDic.get(table.room.id).getControlRate();
		List<DiceType> cards = new ArrayList<>(table.getCards());
		Collections.shuffle(cards);
		int index = RandomUtil.random(0, cards.size() - 3);
		List<DiceType> card = new ArrayList<>();
		controlRate=10;
		if (RandomUtil.probable(controlRate, 100)) {
			if (table.getTotalBet() > 0) {
				Pair<Boolean, String> f1=preCalcTable(table, 1);
				Pair<Boolean, String> f2=preCalcTable(table, 2);
				Pair<Boolean, String> f3=preCalcTable(table, 3);
			
				if (preCalcTable(table, 1).v1) {
					return getAreaCards(cards, 1);
				}
				if (preCalcTable(table, 2).v1) {
					return getAreaCards(cards, 2);
				}
				if (preCalcTable(table, 3).v1) {
					return getAreaCards(cards, 3);
				}
				if(!f1.v1&&!f2.v1&&!f3.v1) {
					LOG.warn("[骰子]出现系统亏损[{}]轮[{}]|[{}]|[{}]", table.round,f1.v2,f2.v2,f3.v2);
				}
			}
			card = cards.subList(index, index + 3);
		} else {
			card = cards.subList(index, index + 3);
		}
		return card;
	}

	/**
	 * 获取指定大小牌型
	 * 
	 * @param cards
	 * @param area
	 * @return
	 */
	private List<DiceType> getAreaCards(List<DiceType> cards, int area) {
		if (area == 3) {
			return getSameDice(cards);

		}
		return getAreaDice(cards, area);

	}

	private List<DiceType> getAreaDice(List<DiceType> cards, int area) {
		List<DiceType> dices = new ArrayList<>();
		int index = 0;
		boolean flag = true;
		while (flag) {
			Collections.shuffle(cards);
			dices = cards.subList(index, index + 3);
			if (getWinArea(dices) == area) {
				break;
			}
		}
		return dices;
	}

	/**
	 * 获取豹子
	 * 
	 * @param cards2
	 * @return
	 */
	public List<DiceType> getSameDice(List<DiceType> cards) {
		List<DiceType> dices = new ArrayList<>();
		Collections.shuffle(cards);
		dices.add(cards.get(0));
		dices.add(cards.get(0));
		dices.add(cards.get(0));
		return dices;
	}

	/**
	 * 获取获胜的区域
	 * 
	 * @return 1大2小3豹子
	 */
	public int getWinArea(List<DiceType> dices) {
		if (isSamePoints(dices)) {
			return 3;
		}
		int totalPoints = getTotalDicesPoints(dices);
		if (totalPoints > 10) {
			return 1;
		}
		return 2;
	}

	/**
	 * 是否是豹子所有点数一样。
	 * 
	 * @return
	 */
	public boolean isSamePoints(List<DiceType> dices) {
		boolean same = true;
		int points = 0;
		for (DiceType dt : dices) {
			if (points == 0) {
				points = dt.points;
			}
			if (points != dt.points) {
				return false;
			}
		}
		return same;

	}

	/**
	 * 获取总点数
	 * 
	 * @return
	 */
	public int getTotalDicesPoints(List<DiceType> dices) {
		int totalPoints = 0;
		for (DiceType dt : dices) {
			totalPoints = totalPoints + dt.points;
		}
		return totalPoints;
	}

	/**
	 * 进入房间
	 * 
	 * @param player
	 */
	public void enterRoom(Player player) {
//		if (player.table != null) {
//			diceMsgMgr.sendResEnterDiceRoom(player,1);
//			LOG.warn("[骰子]玩家[{}][{}]已经在游戏[{}]房间[{}]桌子[{}]中", player.getNickName(), player.getId(), 
//					player.table.room().game(), player.table.room().name, player.table.id);
//			return;
//		}
		int roomId = dataMgr.roomId;
		DiceRoom room = dataMgr.getRoom(roomId);
		if (room == null) {
			diceMsgMgr.sendResEnterDiceRoom(player, 2);
			LOG.warn("[骰子牛牛]玩家[{}][{}]进入的房间[{}]不存在", player.getNickName(), player.getId(), roomId);
			return;
		}

		DiceSeat seat = dataMgr.getPlayerSeat(player.getId());
		if (seat != null) {// 玩家是在恢复现场？
			doEnterRoom(player, seat);
			return;
		}

		DiceRoomDom roomDom = roomDic.get(roomId);
		// 进入不校验
		if (player.gold() < roomDom.getLower()) {
			// playerMsgMgr.sendWarnMsg(player, 10004, "" + roomDom.getLower());
			LOG.warn("[骰子]玩家[{}][{}]进入的房间[{}]低于下限[{}]金币", player.getNickName(), player.getId(), roomDom.getName(),
					roomDom.getLower());
			return;
		}

		DiceSeat emptySeat = room.findEmptySeat();
		if (emptySeat == null) {
			diceMsgMgr.sendResEnterDiceRoom(player, 3);
			LOG.warn("[骰子]玩家[{}][{}]进入的房间[{}]人数已满", player.getNickName(), player.getId(), roomDom.getName());
			return;
		}

		// 进入房间
		doEnterRoom(player, emptySeat);
		diceMsgMgr.sendResEnterDiceRoom(player, 0);
	}

	/**
	 * 进入房间
	 * 
	 * @param player
	 * @param seat
	 */
	private void doEnterRoom(Player player, DiceSeat seat) {
		DiceTable table = seat.table;
		DiceRoom room = table.room;
		seat.enterTblTime = System.currentTimeMillis();
		seat.playerId = player.getId();
		seat.nickName = player.getNickName();
		seat.online = true;
		seat.icon = player.getIcon();
		seat.gold = player.gold();
		seat.isRobot = player.getIsRobot();
		player.table = table;

		// 更新玩家座位
		dataMgr.updatePlayerSeats(player.getId(), seat);
		// 发送进入房间消息
		diceMsgMgr.sendEnterRoomMsg(seat);
		LOG.info("[骰子]玩家[{}][{}]进入房间[{}]成功", player.getNickName(), player.getId(), room.name);
	}

	/**
	 * 退出房间
	 * 
	 * @param player
	 */
	public void exitRoom(Player player) {
		DiceSeat seat = dataMgr.getPlayerSeat(player.getId());
		if (seat == null) {
			return;
		}
		DiceTable table = seat.table;
		if (table.banker.playerId == seat.playerId) {
			diceMsgMgr.sendResExitDiceRoom(player, 1);
			return;
		}
		doExitRoom(seat);
		player.table = null;
		diceMsgMgr.sendResExitDiceRoom(player, 0);
	}

	private void doExitRoom(DiceSeat seat) {
		DiceTable table = seat.table;
		long playerId = seat.playerId;
		if (playerId == 0) {
			return;
		}
		Player player = playerMgr.getPlayer(playerId);
		if (player != null) {
			player.table = null;
		}
		seat.online = false;
		
		LOG.info("[骰子]玩家[{}][{}]退出房间[{}]", seat.playerId, seat.nickName, table.room.name);
		if (table.stage == DiceStage.BALANCE || (seat.totalBet == 0 && table.banker.playerId != seat.playerId)) {// 休息阶段或者下注阶段没有下注且不是庄家才能自由完全退出
			if (table.removeBankerList(seat.playerId)) {// 在申请庄家列表中
				diceMsgMgr.sendApplyBankersMsg(table);
			}

//			if (table.banker == seat) {// 庄家退
//				table.banker = table.sysBankerSeat();
//				changeSysBanker(table);
//				brnnMsgMgr.sendBankerInfoMsg(table);
//			}
			dataMgr.removePlayerSeat(playerId);
			seat.clear();
		}
		// 触发退出房间事件
		eventMgr.post(new ExitRoomEvent(playerId));
	}

	/**
	 * 下注
	 * 
	 * @param player
	 * @param betGold
	 * @param isBig
	 */
	public void betGold(Player player, long betGold, int area) {
		DiceSeat seat = dataMgr.getPlayerSeat(player.getId());
		DiceTable table = seat.table;
		DiceRoomDicWrapper roomDom = roomDic.get(seat.table.room.id);
		if (area < 0 || area > 3) {
			diceMsgMgr.sendResBetDice(seat, 3);
			return;
		}
		if (betGold <= 0) {
			diceMsgMgr.sendResBetDice(seat, 4);
			return;
		}
		if (player.gold() < betGold) {
			LOG.warn("[骰子]玩家[{}][{}]下注筹码[{}]金币不足[{}]", player.getNickName(), player.getId(), betGold, player.gold());
			diceMsgMgr.sendResBetDice(seat, 2);
			return;
		}
		if (table.stage != DiceStage.BET) {
			diceMsgMgr.sendResBetDice(seat, 1);
			return;
		}
		if (table.banker.playerId == seat.playerId) {
			diceMsgMgr.sendResBetDice(seat, 5);
			return;
		}
		if (!roomDom.ableBetChips().contains(betGold)) {
			diceMsgMgr.sendResBetDice(seat, 6);
			LOG.warn("[骰子]玩家[{}][{}]下注筹码[{}]不符合规则", player.getNickName(), player.getId(), betGold);
			return;
		}
		DiceSeat banker = table.banker;
		long bankerGold =0;
		// 非系统庄检查金币上限
		if (banker.playerId > 0) {
			bankerGold = banker.gold;
			// 1大区2小区3豹子区
			long totalBigGold = table.areaBets.get(1).getGold();
			long totalSmalGold = table.areaBets.get(2).getGold();
			long totalSameGold = table.areaBets.get(3).getGold();
			boolean isCanBt = isCanBet(totalBigGold, totalSmalGold, totalSameGold, bankerGold, betGold, area);
			if (!isCanBt) {
				diceMsgMgr.sendResBetDice(seat, 7);
				return;
			}
		}
//		else {
//			bankerGold =20000;
//		}
		
		doPlayerBet(player, seat, area, betGold);
	}

	private void doPlayerBet(Player player, DiceSeat seat, int area, long betGold) {
		DiceTable table = seat.table;
		playerMgr.addGold(player, -betGold, LogAction.DICE_BET);
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
		seat.gold=player.gold();
		// 发送下注成功
		diceMsgMgr.sendResBetDiceMsg(seat, area, betGold);
		LOG.info("[骰子]玩家[{}][{}]下注区域[{}][{}]成功", seat.nickName, seat.playerId, area, betGold);
	}

	/**
	 * 续押
	 * @param player
	 */
	public void continueBetGold(Player player) {
		DiceSeat seat = dataMgr.getPlayerSeat(player.getId());
		DiceTable table = seat.table;
		if(seat.hisBet.isEmpty()) {
			diceMsgMgr.sendResContinueBet(seat, 1);
			return;
		}
		int betGold=0;
		for(AreaBetInfo abi:seat.hisBet) {
			betGold+=abi.getGold();
		}
		if (betGold <= 0) {
			diceMsgMgr.sendResContinueBet(seat, 2);
			return;
		}
		if (player.gold() < betGold) {
			diceMsgMgr.sendResContinueBet(seat, 3);
			return;
		}
		if (table.stage != DiceStage.BET) {
			diceMsgMgr.sendResContinueBet(seat, 4);
			return;
		}
		if (table.banker.playerId == seat.playerId) {
			diceMsgMgr.sendResContinueBet(seat, 5);
			return;
		}
		
		
		DiceSeat banker = table.banker;
		long bankerGold =0;
		
//		else {
//			bankerGold =20000;
//		}
		// 1大区2小区3豹子区
		long totalBigGold = table.areaBets.get(1).getGold();
		long totalSmalGold = table.areaBets.get(2).getGold();
		long totalSameGold = table.areaBets.get(3).getGold();
		
		for(AreaBetInfo abi:seat.hisBet) {
			// 非系统庄检查金币上限
			if (banker.playerId > 0) {
				bankerGold = banker.gold;
				boolean isCanBt = isCanBet(totalBigGold, totalSmalGold, totalSameGold, bankerGold, abi.getGold(), abi.getArea());
				if (!isCanBt) {
					diceMsgMgr.sendResContinueBet(seat, 6);
					return;
				}
			}
			
		}
		doContinuePlayerBet(player, seat);
	}
	private void doContinuePlayerBet(Player player, DiceSeat seat) {
		DiceTable table = seat.table;
		for(AreaBetInfo abi:seat.hisBet) {
			long betGold=abi.getGold();
			int area=abi.getArea();
			playerMgr.addGold(player, -betGold, LogAction.DICE_BET);
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
		    seat.gold=player.gold();
		}
		diceMsgMgr.sendResContinueBet(seat, 0);
		diceMsgMgr.sendResContinueBetMsg(seat);
	}
	/**
	 * 是否能够续压
	 * 
	 * @param totalBigGold  大区总金币
	 * @param totalSmalGold 小区区总金币
	 * @param totalSameGold 豹子区总金币
	 * @param bankerGold    庄家当前金币
	 * @param betGold       当前压的金币
	 * @param area          1大区2小区3豹子区
	 * @return
	 */
	public boolean isCanBet(long totalBigGold, long totalSmalGold, long totalSameGold, long bankerGold, long betGold,
			int area) {
		boolean flag = false;
		long canGiveGold = 0;
		long playerBetGold = 0;
		if (area == 1) {
			canGiveGold = totalSmalGold + totalSameGold + bankerGold;
			playerBetGold = (totalBigGold + betGold) * DiceDataMgr.multiples;
		} else if (area == 2) {
			canGiveGold = totalBigGold + totalSameGold + bankerGold;
			playerBetGold = (totalSmalGold + betGold) * DiceDataMgr.multiples;
		} else {
			canGiveGold = totalBigGold + totalSmalGold + bankerGold;
			playerBetGold = (totalSameGold + betGold) * DiceDataMgr.sameMultiples;
		}

		if (canGiveGold >= playerBetGold) {
			flag = true;
		}

		return flag;
	}

	/**
	 * 申请上庄
	 * 
	 * @param player
	 */
	public void applyBanker(Player player,long bankerGold) {
		DiceSeat seat = dataMgr.getPlayerSeat(player.getId());
		DiceTable table = seat.table;
		if (seat.playerId == table.banker.playerId) {
			diceMsgMgr.sendResApplyBankerMsg(player, 1);
			LOG.info("[骰子]玩家[{}][{}]已经是庄家申请失败", player.getNickName(), player.getId());
			return;
		}
		DiceRoomDicWrapper roomDom = roomDic.get(seat.table.room.id);
		if (player.gold() < roomDom.getBankerLowGold()) {
			diceMsgMgr.sendResApplyBankerMsg(player, 2);
			LOG.info("[骰子]玩家[{}][{}]不够庄家最小筹码数[{}]申请失败", player.getNickName(), player.getId(),
					roomDom.getBankerLowGold());
			return;
		}
		if(bankerGold<roomDom.getBankerLowGold()) {
			diceMsgMgr.sendResApplyBankerMsg(player, 5);
			return;
		}

		if(bankerGold<=0) {
			return;
		}
		if(player.gold()<bankerGold) {
			diceMsgMgr.sendResApplyBankerMsg(player, 2);
			return;
		}
		if (table.applicants.size() >= DiceTable.APPLY_BANKER_SIZE) {
			diceMsgMgr.sendResApplyBankerMsg(player, 3);
			LOG.info("[骰子]玩家[{}][{}]申请庄家时人数超过上限[{}]", player.getNickName(), player.getId(),
					DiceTable.APPLY_BANKER_SIZE);
			return;
		}

		if (table.isInBankerLists(player.getId())) {
			diceMsgMgr.sendResApplyBankerMsg(player, 4);
			LOG.info("[骰子]玩家[{}][{}]已经在庄家列表中,申请失败", player.getNickName(), player.getId());
			return;
		}
	
		doApplyBanker(seat,bankerGold,roomDom.getBankerSortGold());
		diceMsgMgr.sendResApplyBankerMsg(player, 0);
	}
	

	/**
	 * 上庄
	 * 
	 * @param seat
	 * @param gold
	 */
	private void doApplyBanker(DiceSeat seat, long gold,long highGold) {
		DiceTable table = seat.table;
		ApplicantInfo applicant = new ApplicantInfo();
		applicant.setGold(gold);
		applicant.setNickName(seat.nickName);
		applicant.setPlayerId(seat.playerId);
		table.addApplicants(applicant,highGold);
//		if (table.stage == DiceStage.BALANCE && changeSysBanker(table)) {// 休息阶段改变系统庄家?
//			//diceMsgMgr.sendBankerInfoMsg(table);
//		}
		diceMsgMgr.sendApplyBankersMsg(table);
		LOG.info("[骰子]房间[{}]玩家[{}][{}]申请庄家成功", table.room.id, seat.nickName, seat.playerId);

	}

	/**
	 * 获取游戏排行榜
	 * 
	 * @param player
	 */
	public void loadDiceRank(Player player) {
		DiceSeat seat = dataMgr.getPlayerSeat(player.getId());
		if (seat == null) {
			return;
		}
		DiceTable table = seat.table;
		diceMsgMgr.sendResDiceRankList(player.getId(), table);
	}

	private boolean changeSysBanker(DiceTable table) {
		if (table.applicants.size() > 0) {
			ApplicantInfo applicant = table.applicants.remove(0);
			table.setBanker(dataMgr.getPlayerSeat(applicant.getPlayerId()),applicant.getGold());
			return true;
		}
		return false;
	}

	/**
	 * 下庄
	 * 
	 * @param player
	 */
	public void exitBanker(Player player) {
		DiceSeat seat = dataMgr.getPlayerSeat(player.getId());
		DiceSeat banker = seat.table.banker;
		if (banker.playerId == seat.playerId) {
			diceMsgMgr.sendResExitBanker(player, 1);
			LOG.warn("[骰子]玩家[{}][{}]不能在游戏中下庄", player.getNickName(), player.getId());
			return;
		}
		DiceTable table = seat.table;

		if (!table.removeBankerList(seat.playerId)) {
			diceMsgMgr.sendResExitBanker(player, 2);
			return;
		}
		if(!banker.isRobot) {
			seat.gold=player.gold();
		}
		
		diceMsgMgr.sendResExitBanker(player, 0);
		diceMsgMgr.sendApplyBankersMsg(table);
		LOG.info("[骰子]玩家[{}][{}]休息阶段下庄成功", player.getNickName(), player.getId());

	}
	/**
	 * 抢庄
	 * @param player
	 * @param gold
	 */
    public void robBanker(Player player, long bankerGold) {
		DiceSeat seat = dataMgr.getPlayerSeat(player.getId());
//		DiceTable table = seat.table;
		DiceRoomDicWrapper roomDom = roomDic.get(seat.table.room.id);
		if (bankerGold <= 0) {
			diceMsgMgr.sendResBanker(player, 2);
			return;
		}
		if (player.gold() < bankerGold) {
			diceMsgMgr.sendResBanker(player, 1);
			return;
		}
//		if (table.stage != DiceStage.BANKER) {
//			diceMsgMgr.sendResBanker(player, 3);
//			return;
//		}
		if(bankerGold<roomDom.getBankerSortGold()) {
			diceMsgMgr.sendResBanker(player, 5);
			return;
		}
//		ApplicantInfo applicantInfo=table.robBanker;
//		if(applicantInfo.getGold()>bankerGold) {
//			diceMsgMgr.sendResBanker(player, 4);
//			return;
//		}
//		table.updateApplicantInfo(player, bankerGold);
//		diceMsgMgr.sendResBanker(player, 0);
//		diceMsgMgr.sendResPlayerBankerMsg(seat.table, applicantInfo);
//		//判断金额是否大于上庄
//        if(applicantInfo.getGold()>=roomDom.getBankerLowGold()) {
//        	if(table.stageFuture!=null) {
//        		table.stageFuture.cancel(false);
//        	}
//        	table.bankerList.add(applicantInfo);
//        	tableReadyStage(table);
//		}
	}
    public void reqRobBankerInfo(Player player) {
//    	DiceSeat seat = dataMgr.getPlayerSeat(player.getId());
//    	diceMsgMgr.sendResRobBankerInfo(seat,0);
	}
	/**
	 * 刷新申请庄家的玩家,申请上庄、退出申请上庄、休息阶段刷新队列,金币不足上庄和已经退出游戏的玩家需要清理
	 * 
	 * @param table
	 */
	private void refreshApplyBanker(DiceTable table) {
		DiceRoomDicWrapper roomDom = roomDic.get(table.room.id);
		Iterator<ApplicantInfo> applicantsIt = table.applicants.iterator();
		while (applicantsIt.hasNext()) {
			ApplicantInfo applicant = applicantsIt.next();
			if (applicant.getPlayerId() > 0) {
				applicant.setGold(playerMgr.selectById(applicant.getPlayerId()).gold());
				DiceSeat seat = dataMgr.getPlayerSeat(applicant.getPlayerId());
				if (applicant.getGold() < roomDom.getBankerLowGold() || seat == null || !seat.online) {
					applicantsIt.remove();
				}
			}
		}
	}

	/**
	 * 刷新座位，重置座位数据，离线的玩家清空座位
	 * 
	 * @param table
	 */
	private void refreshSeats(DiceTable table) {
		for (DiceSeat seat : table.seats) {
			long playerId = seat.playerId;
			if (playerId > 0) {
				if (!seat.online) {// 离线?
					dataMgr.removePlayerSeat(playerId);
					seat.clear();
				}
			}
			seat.reset();
		}
	}
	
}
