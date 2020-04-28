package com.alex.game.games.gobang.struct;

import com.alex.game.games.common.AbstractSeat;
import com.alex.game.games.common.AbstractTable;
/**
 * 五子棋座位
 * @author yejuhua
 *
 */
public class GobangSeat extends AbstractSeat {
	// 所属桌子
	public final GobangTable table;
	public boolean online=false;
	// 是否准备
	public boolean readied = false;
	//座位状态
	public GobangSeatStage state = GobangSeatStage.WATING;
	//0无1白2黑
	public int gobangColor=0;
	//局时
	public int totalTime=0;
	
	public GobangSeat(int order,GobangTable table) {
		super(order);
		this.table=table;	
	}

	@Override
	public AbstractTable table() {
		return table;
	}
	/**
	 * 重置座位数据
	 */
	public void reset() {
		readied = false;
		online = false;
		state = GobangSeatStage.WATING;
	    gobangColor=0;
	    totalTime =0;
	}
	/**
	 * 清空座位数据
	 */
	public void clear() {
		this.reset();
		super.clear();
	}

}
