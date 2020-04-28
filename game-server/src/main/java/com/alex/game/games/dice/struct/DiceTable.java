package com.alex.game.games.dice.struct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import com.alex.game.dice.DiceProto.BetAreaInfo;
import com.alex.game.dice.DiceProto.ResDiceWinInfo;
import com.alex.game.games.common.AbstractRoom;
import com.alex.game.games.common.AbstractSeat;
import com.alex.game.games.common.AbstractTable;
import com.alex.game.games.common.dice.DiceType;

public class DiceTable extends AbstractTable {
	// 玩家排名人数
	public static final int PLAYER_RANK_NUM = 15;
	// 申请庄家的最大人数
	public static final int APPLY_BANKER_SIZE = 15;
	// 系统座位数
	public static final int SYS_AREA_NUM = 3;
	// 历史记录条数
	public static final int HISTORY_NUM = 66;
	// 排行榜记录条数
	public static final int RANK_HISTORY_NUM = 20;
	// 游戏阶段(休息、下注、开奖、结算)定时Future
	public ScheduledFuture<?> stageFuture = null;
	// 房间
	public final DiceRoom room;
	// 桌子区域下注信息
	public final Map<Integer, AreaBetInfo> areaBets;
	// 上庄列表
	public final List<ApplicantInfo> applicants = new ArrayList<>();
	// 当前庄家
	public DiceSeat banker;
	// 压大的总金币
	public long totalBigBet;
	// 压小的总金币
	public long totalSmallBet;
	// 压豹子的总金币
	public long totalSameBet;
	// 当前阶段
	public DiceStage stage = DiceStage.READY;
	// 房间里面的位置(order:0)
	public final List<DiceSeat> seats;
	// 第几局
	public long round = System.currentTimeMillis();
	// 总骰子数
	private final List<DiceType> cards = new ArrayList<>();
	// 当前点数
	public List<DiceType> dices = new ArrayList<>(3);
	// 庄家赢金币
	public long bankerWinGold = 0;
	// 庄家输金币
	public long bankerLoseGold = 0;
	// 总税
	public long totalTax = 0;
	// 下注信息
	public final List<BetAreaInfo> betAreaInfo = new ArrayList<>();
	//历史记录
	public List<Integer> diceHistory = new ArrayList<>();
	//先保留，等确定后优化
	public List<PlayerDiceRecord> playerDiceRecord=new ArrayList<>();
	//排行信息
	public List<PlayerDiceRankInfo> playerRanks =new ArrayList<>() ;
	//结算结果排行
	public ResDiceWinInfo.Builder resDiceWinInfo=ResDiceWinInfo.newBuilder();
	//系统庄名字
	private final String systemName="鬼谷子";
	public DiceTable(int id, DiceRoom room, int playerNum) {
		super(id);
		this.room = room;
		this.seats = Collections.unmodifiableList(createSeats(playerNum));
		this.areaBets = new HashMap<>();
		this.banker = new DiceSeat(0, this);
		this.banker.nickName = systemName;
		for (int area = 1; area < SYS_AREA_NUM + 1; area++) {
			AreaBetInfo areaBet = new AreaBetInfo();
			areaBet.setArea(area);
			this.areaBets.put(area, areaBet);
		}
		cards.addAll(Arrays.asList(DiceType.values()));
		cards.addAll(Arrays.asList(DiceType.values()));
		cards.addAll(Arrays.asList(DiceType.values()));
	}

	/**
	 * 创建位置
	 * 
	 * @param num
	 * @return
	 */
	private List<DiceSeat> createSeats(int num) {
		List<DiceSeat> seats = new ArrayList<>(num);
		for (int i = 0; i < num; i++) {
			seats.add(new DiceSeat(i, this));
		}
		return seats;
	}

	@Override
	public AbstractRoom room() {
		return room;
	}

	@Override
	public List<? extends AbstractSeat> seats() {
		return seats;
	}

	/**
	 * reset桌子
	 */
	public void reset() {
		if (stageFuture != null) {
			stageFuture.cancel(false);
		}
		this.totalBigBet = 0;
		this.totalSameBet = 0;
		this.totalSmallBet = 0;
		this.bankerLoseGold = 0;
		this.bankerWinGold = 0;
		this.totalTax = 0;
		this.betAreaInfo.clear();
		this.banker.gold=0;
		dices.clear();
		for (AreaBetInfo areaBet : this.areaBets.values()) {
			areaBet.setGold(0);
		}
		playerRanks.clear();
	}

	/**
	 * 是否已经在申请上庄列表
	 * 
	 * @param playerId
	 * @return
	 */
	public boolean isInBankerLists(long playerId) {
		for (ApplicantInfo ainfo : applicants) {
			if (ainfo.getPlayerId() == playerId) {
				return true;
			}
		}
		return false;
	}

	public List<DiceType> getCards() {
		return cards;
	}

	/**
	 * 获取总点数
	 * 
	 * @return
	 */
	public int getTotalDicesPoints() {
		int totalPoints = 0;
		for (DiceType dt : this.dices) {
			totalPoints = totalPoints + dt.points;
		}
		return totalPoints;
	}

	/**
	 * 是否是豹子所有点数一样。
	 * 
	 * @return
	 */
	public boolean isSamePoints() {
		boolean same = true;
		int points = 0;
		for (DiceType dt : this.dices) {
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
	 * 获取获胜的区域
	 * 
	 * @return 1大2小3豹子
	 */
	public int getWinArea() {
		if (isSamePoints()) {
			return 3;
		}
		int totalPoints = getTotalDicesPoints();
		if (totalPoints > 10) {
			return 1;
		}
		return 2;
	}

	public String getWinDiceType() {
		if (isSamePoints()) {
			return "豹子";
		}
		int totalPoints = getTotalDicesPoints();
		if (totalPoints > 10) {
			return "大";
		}
		return "小";
	}

	public boolean removeBankerList(long playerId) {
		for (ApplicantInfo applicantInfo : applicants) {
			if (applicantInfo.getPlayerId() == playerId) {
				applicants.remove(applicantInfo);
				return true;
			}
		}
		return false;
	}

	public void addDiceHistory(int diceResult) {
		if (diceHistory.size() >= HISTORY_NUM) {
			diceHistory.clear();
		}
		diceHistory.add(diceResult);
	}

	public void setBanker(DiceSeat playerSeat,long bankergold) {
		this.banker.playerId = playerSeat.playerId;
		this.banker.icon = playerSeat.icon;
		this.banker.nickName = playerSeat.nickName;
		this.banker.gold = bankergold;
		this.banker.isRobot=playerSeat.isRobot;
	}

	public void clearBanker() {
		this.banker.clear();
		this.banker.nickName = systemName;
		this.banker.isRobot=false;
		
	}

	/**
	 * 清楚过期记录
	 * @param round
	 */
	public void clearPlayerDiceRecord() {
		List<PlayerDiceRecord> clearRecord = new ArrayList<>();
		for (PlayerDiceRecord pdr : playerDiceRecord) {
			//超过20局清楚记录
			if ((round - pdr.lastRound) > RANK_HISTORY_NUM) {
				clearRecord.add(pdr);
			}
		}
		playerDiceRecord.removeAll(clearRecord);
	}

	public void updateDiceRecord(DiceSeat seat, long round) {
		PlayerDiceRecord pdr=getPlayerDiceRecord(seat.playerId);
		long winGold=seat.winGold-seat.loseGold;
		boolean isWin=false;
		if(winGold>0) {
			isWin=true;
		}
		if(pdr==null) {
			pdr=new PlayerDiceRecord ();
			pdr.playerId=seat.playerId;
			pdr.nickName=seat.nickName;
			pdr.lastRound=round;
			pdr.totalBet=seat.totalBet;
			pdr.addWinHistory(isWin);
			playerDiceRecord.add(pdr);
		}else {
			pdr.lastRound=round;
			pdr.totalBet+=seat.totalBet;
			pdr.addWinHistory(isWin);
			
		}
		
	}
	private PlayerDiceRecord getPlayerDiceRecord(long playerId) {
		for(PlayerDiceRecord pdr :playerDiceRecord) {
			if(pdr.playerId==playerId) {
				return pdr;
			}
		}
		 return null;
	}
	public boolean hasPersonPlayer() {
		boolean has=false;
		for(DiceSeat seat:seats) {
			if(seat.playerId!=0&&!seat.isRobot&&seat.totalBet>0) {
				return true;
			}
		}
		return has;
		
	}

	/**
	 * 获取下注总金额
	 * @return
	 */
	public long getTotalBet() {
		return totalBigBet +totalSameBet+totalSmallBet;
	}

	/**
	 * 增加上庄列表申请
	 * @param applicant
	 * @param highGold 优先金币，大于本金币
	 */
	public void addApplicants(ApplicantInfo applicant, long highGold) {
		int index=0;
		if(applicant.getGold()>=highGold) {
			index=this.getLimitIndex(highGold);
		}else {
			index=this.getLimitIndex(applicant.getGold());
		}
		if(index!=0) {
			this.applicants.add(index, applicant);
		}else {
			this.applicants.add(applicant);
		}
	}
	private int getLimitIndex(long gold) {
		int size=applicants.size();
		for(int i=0;i<size;i++) {
			if(applicants.get(i).getGold()<gold) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * 获取近20局神算子
	 */
	public List<PlayerDiceRecord> getGodLike() {
		List<PlayerDiceRecord> goldLike=new ArrayList<>();
		int curWinTimes=0;
		for(PlayerDiceRecord pdr:playerDiceRecord) {
			int pWinTimes=pdr.getWinTimes();
			if(pWinTimes==curWinTimes) {
				goldLike.add(pdr);
			}
			if(pWinTimes>curWinTimes) {
				goldLike.clear();
				curWinTimes=pWinTimes;
				goldLike.add(pdr);
			}
		}
		return goldLike;
	}
}
