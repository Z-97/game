package com.alex.game.event.struct.bank;

import com.alex.game.player.struct.Player;

/**
 * 玩家存取金币事件
 * 
 */
public class DepositeWithdrawGoldEvent {

	public final Player player;

	public DepositeWithdrawGoldEvent(Player player) {
		this.player = player;
	}
}
