package com.alex.game.event.struct.bank;

import com.alex.game.player.struct.Player;
/**
 * 转账
 * @author yejuhua
 *
 */
public class TransferAccounts {
	public final Player player;
	public TransferAccounts(Player player) {
		this.player = player;
	}
}
