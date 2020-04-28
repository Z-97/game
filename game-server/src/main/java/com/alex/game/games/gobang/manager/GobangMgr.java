package com.alex.game.games.gobang.manager;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alex.game.common.Game;
import com.alex.game.core.concurrent.TaskExecutor;
import com.alex.game.dbdic.dic.GobangRoomDic;
import com.alex.game.dbdic.dic.GobangTimeDic;
import com.alex.game.dbdic.dom.GobangRoomDom;
import com.alex.game.event.manager.EventMgr;
import com.alex.game.event.struct.game.ExitRoomEvent;
import com.alex.game.event.struct.login.PlayerLoginSuccessEvent;
import com.alex.game.event.struct.login.PlayerLogoutEvent;
import com.alex.game.games.gobang.struct.Gobang;
import com.alex.game.games.gobang.struct.GobangColor;
import com.alex.game.games.gobang.struct.GobangRoom;
import com.alex.game.games.gobang.struct.GobangSeat;
import com.alex.game.games.gobang.struct.GobangSeatStage;
import com.alex.game.games.gobang.struct.GobangStage;
import com.alex.game.games.gobang.struct.GobangTable;
import com.alex.game.games.gobang.struct.WinType;
import com.alex.game.player.manager.PlayerMgr;
import com.alex.game.player.struct.Player;
import com.alex.game.schedule.manager.ScheduleMgr;
import com.alex.game.server.ExecutorMgr;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 五子棋管理器
 * 
 * @author yejuhua
 *
 */
@Singleton
public class GobangMgr {
	private static final Logger LOG = LoggerFactory.getLogger(GobangMgr.class);
	private final EventMgr eventMgr;
	@Inject
	private GobangDataMgr dataMgr;
	// 游戏线程executor
	public final TaskExecutor gameExecutor;
	@Inject
	private GobangMsgMgr gobangMsgMgr;
	@Inject
	private GobangTimeDic timeDic;
	@Inject
	private GobangRoomDic roomDic;
	@Inject
	private ScheduleMgr scheduleMgr;

	@Inject
	private PlayerMgr playerMgr;
	@Inject
	public GobangMgr(EventMgr eventMgr, ExecutorMgr executorMgr) {
		this.eventMgr = eventMgr;
		eventMgr.register(this);
		this.gameExecutor = executorMgr.getGameExecutor(Game.GOBANG);
	}

	/**
	 * 订阅玩家退出游戏事件, 在gameExecutor中执行, 避免线程安全问题
	 */
	@Subscribe
	public void onPlayerExitGame(PlayerLogoutEvent event) {
		long playerId = event.player.getId();
		GobangSeat outSeeat = dataMgr.getPlayerSeat(playerId);
		if (outSeeat == null) {//            
			return;
		}
		
		gameExecutor.execute(() -> {
			GobangSeat seat = dataMgr.getPlayerSeat(playerId);
			// 在游戏线程中还需要判断一次，避免重复退出
			if (seat != null) doExitRoom(seat);
		});
	}
	/**
	 * 订阅登陆成功游戏事件,如果在游戏中直接进入房间
	 */
	@Subscribe
	public void onPlayerLoginSuccess(PlayerLoginSuccessEvent event) {
		Player player = event.player;
		GobangSeat outSeeat = dataMgr.getPlayerSeat(player.getId());
		if (outSeeat == null) {// PlayerLoginSuccessEvent是在登陆线程中触发，需要判断一次
			return;
		}
		GobangRoomDom gobangRoomDom=roomDic.get(outSeeat.table().room().id);
		int gameTime = timeDic.get(GobangStage.GAMEING.val).getTime();
		gameExecutor.execute(() -> {
			GobangSeat seat = dataMgr.getPlayerSeat(player.getId());
			// 在游戏线程中还需要判断一次，避免重复进入
			if (seat != null) {
				gobangMsgMgr.sendResEnterRoomMsg(seat,gobangRoomDom.getStepTime(),gameTime);
				doEnterRoom(player, seat, true);
			}
		});
	}

	/**
	 * 创建桌子密码
	 * 
	 * @param player
	 */
	public void createTable(Player player) {
		if (player.table != null) {
			LOG.warn("[五子棋]玩家[{}][{}]已经在游戏[{}]房间[{}]桌子[{}]中", player.getNickName(), player.getId(),
					player.table.room().game(), player.table.room().name, player.table.id);
			return;
		}
		int roomId = dataMgr.roomId;
		GobangRoom room = dataMgr.getRoom(roomId);
		if (room == null) {
			LOG.warn("[五子棋]玩家[{}][{}]进入的房间[{}]不存在", player.getNickName(), player.getId(), roomId);
			return;
		}
		GobangSeat seat = dataMgr.getPlayerSeat(player.getId());
		if (seat != null) {
			Game game = room.game();
			LOG.warn("[五子棋]玩家[{}][{}]当前在游戏[{}]房间[{}]中不能进入房间", player.getId(), player.getNickName(), game.desc,
					room.name);
			gobangMsgMgr.sendRescreateGobangRoom(player, 1);
			return;
		}
		GobangSeat emptySeat = room.findEmptySeat();
		if (emptySeat == null) {
			gobangMsgMgr.sendRescreateGobangRoom(player, 2);
			LOG.warn("[五子棋]玩家[{}][{}]进入的房间[{}]人数已满", player.getNickName(), player.getId(), roomId);
			return;
		}
		doEnterRoom(player, emptySeat, false);
		GobangTable table = emptySeat.table;
		table.playerId = player.getId();
	}

	/**
	 * 进入游戏
	 * 
	 * @param player
	 * @param seat
	 */
	private void doEnterRoom(Player player, GobangSeat seat, boolean login) {
		
		int gameTime = timeDic.get(GobangStage.GAMEING.val).getTime();
		GobangTable table = seat.table;
		seat.enterTblTime = System.currentTimeMillis();
		seat.playerId = player.getId();
		seat.nickName = player.getNickName();
		seat.online = true;
		System.out.println(gameTime+"----------------"+gameTime);
		seat.totalTime=gameTime;
		seat.icon=player.getIcon();
		// 自动准备
//		if (table.stage == GobangStage.REST) {
//			seat.state = JdnnSeatState.READY;
//			seat.readied = true;
//		} 
		player.table = table;
	
		// 更新玩家座位
		dataMgr.updatePlayerSeats(player.getId(), seat);
		
		GobangRoomDom gobangRoomDom=roomDic.get(table.room().id);
		int stepTime =gobangRoomDom.getStepTime();
		// 发送进入房间消息
		gobangMsgMgr.sendResEnterRoomMsg(seat,stepTime,gameTime);
		if (!login) {
			// 发送进入其他人
			gobangMsgMgr.sendOtherEnterTableMsg(seat);
		}

		if (table.isAllWaittingOver() && table.stage == GobangStage.READY) {
			if (table.stageFuture != null) {
				table.stageFuture.cancel(false);
			}
			tableReady(table);
		}
//		//恢复现场
//		if ((table.stage != JdnnStage.REST && login) || !seat.readied) {
//			int stageTime = (int) table.stageFuture.getDelay(TimeUnit.SECONDS);
//			if (table.stage == JdnnStage.GAMING) {
//				msgMgr.sendResBetEnd(seat);
//				if (table.isComplete) {
//					msgMgr.sendResBalanceEnd(seat);
//				}
//			}
//			gobangMsgMgr.sendResRestoreMsg(seat, table.stage.val, stageTime);
//		}
	}

	/**
	 * 进入房间
	 * @param player
	 * @param pwd
	 */
	public void enterRoom(Player player, int pwd) {
		if (player.table != null) {
			LOG.warn("[五子棋]玩家[{}][{}]已经在游戏[{}]房间[{}]桌子[{}]中", player.getNickName(), player.getId(),
					player.table.room().game(), player.table.room().name, player.table.id);
			return;
		}
		int roomId = dataMgr.roomId;
		GobangRoom room = dataMgr.getRoom(roomId);
		if (room == null) {
			LOG.warn("[五子棋]玩家[{}][{}]进入的房间[{}]不存在", player.getNickName(), player.getId(), roomId);
			return;
		}
		GobangSeat seat = dataMgr.getPlayerSeat(player.getId());
		if (seat != null) {
			Game game = room.game();
			LOG.warn("[五子棋]玩家[{}][{}]当前在游戏[{}]房间[{}]中不能进入房间", player.getId(), player.getNickName(), game.desc,
					room.name);
			gobangMsgMgr.sendRescreateGobangRoom(player, 1);
			return;
		}

		GobangSeat emptySeat = room.findEmptySeat(pwd);
		if (emptySeat == null) {
			gobangMsgMgr.sendRescreateGobangRoom(player, 2);
			LOG.warn("[五子棋]玩家[{}][{}]进入的房间[{}]人数已满", player.getNickName(), player.getId(), roomId);
			return;
		}
		doEnterRoom(player, emptySeat, false);
	}

	/**
	 * 准备
	 * @param player
	 */
	public void readyGobang(Player player) {
		// 座位
		GobangSeat seat = dataMgr.getPlayerSeat(player.getId());
		if(seat==null) {
			return;
		}
		if (seat.readied) {
			gobangMsgMgr.sendResReay(player, 1);
			LOG.warn("[五子棋]玩家[{}][{}]已经准备好,请勿重复准备", player.getNickName(), player.getId());
			return;
		}

		GobangTable table = seat.table;
		if (!GobangStage.READY.equals(table.stage)) {
			LOG.warn("[五子棋]玩家[{}][{}]所在的桌子正在游戏中不能准备", player.getNickName(), player.getId());
			return;
		}
		seat.readied = true;
		seat.state = GobangSeatStage.READY;
		gobangMsgMgr.sendResReay(player, 0);
		gobangMsgMgr.sendReadyMsg(seat);
		if (table.isAllReadyOver() && table.stage == GobangStage.READY) {
			if(table.stageFuture!=null) {
				table.stageFuture.cancel(false);
			}
			tableGameStage(table);
		}

	}

	public void canCelReady(Player player) {
		GobangSeat seat = dataMgr.getPlayerSeat(player.getId());
		if(seat==null) {
			return;
		}
		if (!seat.readied) {
			return;
		}
		GobangTable table = seat.table;
		if (!GobangStage.READY.equals(table.stage)) {
			return;
		}
		seat.readied = false;
		gobangMsgMgr.sendResCancelReady(player,0);
		gobangMsgMgr.sendCancelReadyMsg(seat);
	}
	/**
	 * 落子
	 * 
	 * @param player
	 * @param color
	 * @param x
	 * @param y
	 */
	public void moveGobang(Player player, int x, int y) {
		GobangSeat seat = dataMgr.getPlayerSeat(player.getId());
		if (seat == null) {
			return;
		}
		GobangTable table = seat.table;
		boolean hasChess = table.findChess(x, y);
		if (hasChess) {
			gobangMsgMgr.sendResMoveGobang(player, 1);
			return;
		}
		if (x < 0 || x > table.ROWS || y < 0 || y > table.COLS) {
			gobangMsgMgr.sendResMoveGobang(player, 2);
			return;
		}
		if (table.stage != GobangStage.GAMEING) {
			gobangMsgMgr.sendResMoveGobang(player, 3);
			return;
		}
		int chessColor = seat.gobangColor;
		if ((chessColor == 1 && table.isBack) || (chessColor == 2 && !table.isBack)) {
			gobangMsgMgr.sendResMoveGobang(player, 4);
			return;
		}
		Gobang ch = new Gobang(x, y, table.isBack ? GobangColor.BLACK : GobangColor.WHITE);
	    table.xIndex=x;
	    table.yIndex=y;
		table.chessList[table.chessCount++] = ch;
		
		GobangRoomDom gobangRoomDom=roomDic.get(table.room().id);
		int stepTime=gobangRoomDom.getStepTime();
		int useTime=stepTime-(int) table.stepFuture.getDelay(TimeUnit.SECONDS);
		seat.totalTime-=useTime;
	
		resetPlayerStepTime(table);
		gobangMsgMgr.sendResMoveGobang(player, 0);
		gobangMsgMgr.sendResMoveGobangMsg(seat,x,y);
		// 检查是否胜利
		if (table.isWin()) {
			table.winPlayerId = player.getId();
			if(seat.gobangColor==1) {
				table.winType=WinType.A.val;
			}else {
				table.winType=WinType.B.val;
			}
			LOG.info("[五子棋]房间[{}]桌子[{}]玩家[{}]胜利", table.room().name, table.id,player.getNickName());
			tableBalance(table);
			return;
		}
		// 没有棋子了
		if (table.chessCount == (table.COLS + 1) * (table.ROWS + 1)) {
			table.winType=WinType.P.val;
			tableBalance(table);
			return;
		}
		
		table.isBack = !table.isBack;
		GobangSeat anotherGobang=table.findAnotherGobangSeat(player.getId());
		resetPlayerTotalTime(anotherGobang);
	}

	/**
	 * 认输
	 * 
	 * @param player
	 */
	public void giveUp(Player player) {
		GobangSeat seat = dataMgr.getPlayerSeat(player.getId());
		if (seat == null) {
			return;
		}
		GobangTable table = seat.table;
		if (table.stage != GobangStage.GAMEING) {
			return;
		}
		table.winPlayerId = table.findAnotherPlayerId(player.getId());
		if(seat.gobangColor==1) {
			table.winType=WinType.E.val;
		}else {
			table.winType=WinType.F.val;
		}
		LOG.info("[五子棋]房间[{}]桌子[{}]玩家[{}]认输", table.room().name, table.id,table.isBack);
		tableBalance(table);
	}

	/**
	 * 求和
	 * 
	 * @param player
	 */
	public void reqPeace(Player player) {
		GobangSeat seat = dataMgr.getPlayerSeat(player.getId());
		if (seat == null) {
			return;
		}
		GobangTable table = seat.table;
		if (table.stage != GobangStage.GAMEING) {
			return;
		}
		long winPlayerId = table.findAnotherPlayerId(player.getId());
		gobangMsgMgr.sendResPeace(player,0);
		gobangMsgMgr.sendResPeaceMsg(winPlayerId);
	}

	/**
	 * 应答求和
	 * @param player
	 * @param state
	 */
	public void answerReqPeaceMsg(Player player, boolean state) {

		GobangSeat seat = dataMgr.getPlayerSeat(player.getId());
		if (seat == null) {
			return;
		}
		GobangTable table = seat.table;
		if (table.stage != GobangStage.GAMEING) {
			return;
		}
		if(state) {
			if(seat.gobangColor==1) {
				table.winType=WinType.M.val;
			}else {
				table.winType=WinType.H.val;
			}
			LOG.info("[五子棋]房间[{}]桌子[{}]玩家[{}]求和", table.room().name, table.id,table.isBack);
			tableBalance(table);
			return;
		}
		long winPlayerId = table.findAnotherPlayerId(player.getId());
		gobangMsgMgr.sendResAnswerPeace(player,0);
		gobangMsgMgr.sendResPeaceResult(winPlayerId,state);
	}
	/**
	 * 请求悔棋
	 * @param player
	 */
	public void reqRetractChess(Player player) {
		GobangSeat seat = dataMgr.getPlayerSeat(player.getId());
		if (seat == null) {
			return;
		}
		GobangTable table = seat.table;
		if (table.stage != GobangStage.GAMEING) {
			gobangMsgMgr.sendResRetractChess(player ,1);
			return;
		}
		if((table.isBack&seat.gobangColor==1)||(!table.isBack&seat.gobangColor==2)) {
			gobangMsgMgr.sendResRetractChess(player ,1);
			return;
		}
		long anotherPlayerId = table.findAnotherPlayerId(player.getId());
		gobangMsgMgr.sendResRetractChess(player ,0);
		gobangMsgMgr.sendResRetractChessMsg(anotherPlayerId);
	}
	/**
	 * 应答悔棋
	 * @param player
	 * @param state
	 */
	public void answerRetractChess(Player player, boolean state) {
		GobangSeat seat = dataMgr.getPlayerSeat(player.getId());
		if (seat == null) {
			return;
		}
		GobangTable table = seat.table;
		if (table.stage != GobangStage.GAMEING) {
			return;
		}
		
		if(state) {
			Gobang gobang1=table.goback();
			Gobang gobang2=table.goback();
			gobangMsgMgr.sendResRetractChessResult(table,state,gobang1,gobang2);
		} else {
			long anotherPlayerId=table.findAnotherPlayerId(player.getId());
			gobangMsgMgr.sendResRetractChessResult(anotherPlayerId,state);
		}
		gobangMsgMgr.sendResAnswerRetractChess(player,0);
		
	}
	/**
	 * 退出房间
	 * @param player
	 */
    public void reqExitRoom(Player player) {
    	GobangSeat seat = dataMgr.getPlayerSeat(player.getId());
		if (seat.playerId > 0 && seat.state == GobangSeatStage.PLAYING) {
			gobangMsgMgr.sendResExitRoom(player, 1);
			return;
		}
		doExitRoom(seat);
		if (seat.playerId == 0) {
			player.table = null;
		}
	}
	/**
	 * 退出桌子 
	 * 
	 * @param seat
	 */
	private void doExitRoom(GobangSeat seat) {
		long playerId = seat.playerId;
		Player player = playerMgr.getPlayer(playerId);
		if (player != null) {
			player.table = null;
		}
		GobangTable table = seat.table;
		seat.online = false;
		if (table.stage == GobangStage.READY 
				|| !seat.state.equals(GobangSeatStage.PLAYING)) {
			gobangMsgMgr.sendResExitRoom(player, 0);
			dataMgr.removePlayerSeat(playerId);
			seat.clear();
		}
		if(table.isAllExit()) {
			table.clear();;
		}
		// 触发退出房间事件
		eventMgr.post(new ExitRoomEvent(playerId));
	}
	/**
	 * 准备
	 * 
	 * @param table
	 */
	public void tableReady(GobangTable table) {
		// 踢出不在线的玩家
		for (GobangSeat seat : table.seats) {
			if (seat.playerId > 0 && !seat.online) {
				 doExitRoom(seat);
			}
		}
		table.reset();
		// 终止计时
		if (table.playerNum() == 0) {
			table.stageFuture.cancel(false);
			return;
		}
		table.stage = GobangStage.READY;
		int time = timeDic.get(table.stage.val).getTime()*2;
		gobangMsgMgr.sendResStage(table, table.stage.val, time);
		LOG.info("[五子棋]房间[{}]桌子[{}]进入[{}]阶段", table.room().name, table.id, table.stage.desc);
		
	}

	/**
	 * 游戏
	 * 
	 * @param table
	 */
	public void tableGameStage(GobangTable table) {
		table.setSeatColor();
	    this.resetPlayerStepTime(table);
		table.stage = GobangStage.GAMEING;
		int time = timeDic.get(table.stage.val).getTime();
		gobangMsgMgr.sendResStage(table, table.stage.val, time);
		gobangMsgMgr.sendResSeatColor(table);
		LOG.info("[五子棋]房间[{}]桌子[{}]进入[{}]阶段", table.room().name, table.id, table.stage.desc);

	}

	private void resetPlayerStepTime(GobangTable table) {
		if(table.stepFuture!=null) {
			table.stepFuture.cancel(false);
		}
		
		GobangRoomDom gobangRoomDom=roomDic.get(table.room().id);
		int stepTime=gobangRoomDom.getStepTime();
		table.stepFuture = scheduleMgr.schedule(() -> playerStepTime(table), stepTime, TimeUnit.SECONDS, gameExecutor);
	}
	/**
	 * 局时倒计时
	 * @param seat
	 */
	private void resetPlayerTotalTime(GobangSeat seat) {
		GobangTable table=seat.table;
		if(table.totalFuture!=null) {
			table.totalFuture.cancel(false);
		}
		System.out.println(seat.nickName+"table.totalTime---------------"+seat.totalTime);
		GobangRoomDom gobangRoomDom=roomDic.get(seat.table.room().id);
		int stepTime=gobangRoomDom.getStepTime();
		if(seat.totalTime<stepTime) {
			System.out.println(seat.nickName+"table.totalTime---------------启动定时器:"+seat.totalTime);
			table.totalFuture = scheduleMgr.schedule(() -> playerTotalTime(seat), seat.totalTime, TimeUnit.SECONDS, gameExecutor);
			LOG.info("[五子棋]房间[{}]桌子[{}]玩家[{}]开启总局时[{}]秒倒计时", table.room().name, table.id,table.isBack,seat.totalTime);
		}
	}
	

	/**
	 * 玩家超时认输
	 * @param table
	 */
	private void playerStepTime(GobangTable table) {
		if(table.isBack) {
			table.winType=WinType.O.val;
		}else {
			table.winType=WinType.Q.val;
		}
		LOG.info("[五子棋]房间[{}]桌子[{}]玩家[{}]超时认输", table.room().name, table.id,table.isBack);
		tableBalance(table);
	}
	
	private void playerTotalTime(GobangSeat seat) {
		GobangTable table=seat.table;
		if(table.isBack) {
			table.winType=WinType.O.val;
		}else {
			table.winType=WinType.Q.val;
		}
		LOG.info("[五子棋]房间[{}]桌子[{}]玩家[{}]总局时[{}]超时认输", table.room().name, table.id,table.isBack,seat.totalTime);
		tableBalance(table);
	}

	/**
	 * 结算
	 * 
	 * @param table
	 */
	public void tableBalance(GobangTable table) {
		// 游戏提前结算中止
		if (table.stageFuture != null) {
			table.stageFuture.cancel(false);
		}
	    //停止计步记时
		if (table.stepFuture != null) {
			table.stepFuture.cancel(false);
		}
		//停止总局时记时
		if(table.totalFuture!=null) {
			table.totalFuture.cancel(false);
		}
	
		int time = timeDic.get(table.stage.val).getTime();
		table.stage = GobangStage.READY;
		gobangMsgMgr.sendResStage(table, table.stage.val, time);
		gobangMsgMgr.sendGobangWinfo(table);
		LOG.info("[五子棋]房间[{}]桌子[{}]进入结算阶段", table.room().name, table.id);
		table.reset();
		int gameTime = timeDic.get(GobangStage.GAMEING.val).getTime();
		for(GobangSeat seat:table.seats) {
			seat.reset();
			seat.totalTime=gameTime;
		}
	}

	
}
