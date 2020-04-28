package com.alex.game.event.struct.bank;

public class RechargeSuccessEvent {
	public final long playerId;
	public final long gold;
	//0身上1银行
	public final int type;
	public RechargeSuccessEvent(long playerId,long gold,int type) {
		this.playerId = playerId;
		this.gold = gold;
		this.type = type;
	}
	
}
