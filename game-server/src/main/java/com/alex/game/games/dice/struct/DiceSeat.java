package com.alex.game.games.dice.struct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alex.game.games.common.AbstractSeat;
import com.alex.game.games.common.AbstractTable;

public class DiceSeat extends AbstractSeat {
	// 所属桌子
	public final DiceTable table;
	// 合计下注
	public long totalBet;
	// 区域下注
	public final Map<Integer, AreaBetInfo> areaBets;
	public boolean online =false;
	// 每次赢金币
    public long winGold;
    // 每次税收
    public long tax;
    //每次输金币
    public long loseGold;
    //携带金币
    public long gold;
    //当前累计总下注
    public long sumBets;
    //是否是机器人
    public boolean  isRobot=false;
    //上一轮下注记录
    public List<AreaBetInfo> hisBet=new ArrayList<>();
    //游戏次数
    public int playerTimes=0;
    //赢次数
    public int winTimes=0;
 	// 机器人局数
 	public int robotRound;
	public DiceSeat(int order, DiceTable table) {
		super(order);
		this.table = table;
		this.areaBets = new HashMap<>();
		for (int area =  1; area < DiceTable.SYS_AREA_NUM+1; area++) {
			AreaBetInfo areaBet = new AreaBetInfo();
			areaBet.setArea(area);
			this.areaBets.put(area, areaBet);
		}
	}

	/**
	 * 重置座位数据
	 */
	public void reset() {
		this.totalBet = 0;
		this.loseGold = 0;
		this.winGold = 0;
		this.tax = 0;
		List<AreaBetInfo> betArray = new ArrayList<>();
		for (AreaBetInfo areaBet : this.areaBets.values()) {
			if (areaBet.getGold() > 0) {
				AreaBetInfo hisAreaBet = new AreaBetInfo();
				hisAreaBet.setArea(areaBet.getArea());
				hisAreaBet.setGold(areaBet.getGold());
				betArray.add(hisAreaBet);
			}
			areaBet.setGold(0);
		}
		if (!betArray.isEmpty()) {
			this.hisBet.clear();
			this.hisBet.addAll(betArray);
		}
		
	}

	/**
	 * 清空座位数据
	 */
	public void clear() {
		super.clear();
		reset();
		this.icon = 1;
		this.online = false;
		this.isRobot = false;
		this.hisBet.clear();
		this.winTimes = 0;
		this.playerTimes = 0;
		this.robotRound = 0;
		
	}

	@Override
	public AbstractTable table() {
		return table;
	}
}
