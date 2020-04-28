package com.alex.game.games.dice.struct;

import java.util.ArrayList;
import java.util.List;

/**
 * 输赢记录
 * @author yejuhua
 *
 */
public class PlayerDiceRecord implements Comparable<PlayerDiceRecord>{
	// 玩家id
	public long playerId;
	// 玩家昵称
	public String nickName;
	// 进桌时间
	public long enterTblTime;
	// 头像id
	public int icon;
	// 上一轮局数
	public long lastRound;
	// 最近输赢记录
	private List<Boolean> winHistory=new ArrayList<>();
	//累计下注金额
	public long totalBet=0;
	public void addWinHistory(boolean isWin) {
		if(winHistory.size()>=DiceTable.RANK_HISTORY_NUM) {
			winHistory.remove(0);
		}
		winHistory.add(isWin);
	}
	/**
	 * 获胜局数
	 * @return
	 */
	public int getWinTimes() {
		int winTimes=0;
		for(boolean isWin:winHistory) {
			if(isWin) {
				winTimes++;
			}
		}
		return winTimes;
	}
	@Override
	public int compareTo(PlayerDiceRecord pdr) {
		if(pdr.totalBet>this.totalBet) {
			return 1;
		}
		return -1;
	}
}
