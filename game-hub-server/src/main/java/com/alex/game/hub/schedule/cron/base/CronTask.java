package com.alex.game.hub.schedule.cron.base;

/**
 * CronTask,CronTask是在定时线程中执行的，注意CronTask中修改数据的线程安全问题
 * 
 * @author Alex
 * @date 2017年4月3日 下午10:01:03
 */
public interface CronTask extends Runnable {

	String cron();
}
