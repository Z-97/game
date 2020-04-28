package com.alex.game.dblog.game;

import com.alex.game.dbdata.dom.PlayerDom;
import com.alex.game.dblog.PlayerLog;
import com.alex.game.dblog.core.TableType;
import com.alex.game.dblog.core.annotation.LogTable;
/**
 * 骰子日志
 * @author yejuhua
 *
 */
@LogTable(name="dice_game_log", type=TableType.DAY)
public class DiceGameLog extends PlayerLog {

	public DiceGameLog(PlayerDom player) {
		super(player);
		
	}

}
