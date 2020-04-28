package com.alex.game.schedule.cron;
import com.alex.game.schedule.cron.base.CronTask;
import com.google.inject.Singleton;

/**
 * 记录玩家在线日志task
 * 
 * @author Alex
 * @date 2017年5月11日 下午9:10:54
 */
@Singleton
public class PlayersOnlineLogTask implements CronTask {
	@Override
	public void run() {
		// 包在线玩家
		pkgOnline();
		// 游戏在线
		gameOnline();
		// 推广员在线
	}
	
	/**
	 * 包在线
	 */
	private void pkgOnline() {
		
	}
	
	/**
	 * 游戏在线
	 */
	private void gameOnline() {
		
	}
	

	
	@Override
	public String cron() {
		return "0 */10 * * * ?";
	}

}
