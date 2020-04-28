package com.alex.game.schedule.cron;
import com.alex.game.plaza.redpackage.manager.RedpackageMgr;
import com.alex.game.schedule.cron.base.CronTask;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 刷新过去红包CronTask
 * 
 * @author Alex
 * @date 2017年4月3日 下午10:09:07
 */
@Singleton
public class RefreshRedpackageTask implements CronTask {

	@Inject
	private RedpackageMgr redpackageMgr;
	@Override
	public void run() {
		redpackageMgr.clearRedpackage();
	}

	@Override
	public String cron() {
		// 每天0点05分执行0 5 20 * * ?
		return "0 5 0 * * ? ";
	}

}
