package com.alex.game.core.concurrent.schedule;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

/**
 * 
 * 选择的时间点是3月1号，因为3月1号比较特殊，往前推是2月，2月只有28或者29号，测试最坏情况的边界，结果：
job开始时间:2014-10-12 0:00:00	cron表达式:0 15 10 LW * ?	job下一步执行时间:2014-10-31 10:15:00	job上一步执行时间:2014-9-30 10:15:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 0 12 * * ?	job下一步执行时间:2014-3-1 12:00:00	job上一步执行时间:2014-2-28 12:00:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 15 10 ? * *	job下一步执行时间:2014-3-1 10:15:00	job上一步执行时间:2014-2-28 10:15:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 15 10 * * ?	job下一步执行时间:2014-3-1 10:15:00	job上一步执行时间:2014-2-28 10:15:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 15 10 * * ? *	job下一步执行时间:2014-3-1 10:15:00	job上一步执行时间:2014-2-28 10:15:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 15 10 * * ? 2005	job下一步执行时间:	job上一步执行时间:2005-12-31 10:15:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 * 14 * * ?	job下一步执行时间:2014-3-1 14:00:00	job上一步执行时间:2014-2-28 14:59:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 0/5 14 * * ?	job下一步执行时间:2014-3-1 14:00:00	job上一步执行时间:2014-2-28 14:55:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 0/5 14,18 * * ?	job下一步执行时间:2014-3-1 14:00:00	job上一步执行时间:2014-2-28 18:55:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 0-5 14 * * ?	job下一步执行时间:2014-3-1 14:00:00	job上一步执行时间:2014-2-28 14:05:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 10,44 14 ? 3 WED	job下一步执行时间:2014-3-5 14:10:00	job上一步执行时间:2013-3-27 14:44:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 15 10 ? * MON-FRI	job下一步执行时间:2014-3-3 10:15:00	job上一步执行时间:2014-2-28 10:15:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 15 10 15 * ?	job下一步执行时间:2014-3-15 10:15:00	job上一步执行时间:2014-2-15 10:15:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 15 10 L * ?	job下一步执行时间:2014-3-31 10:15:00	job上一步执行时间:2014-2-28 10:15:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 15 10 L-2 * ?	job下一步执行时间:2014-3-29 10:15:00	job上一步执行时间:2014-2-26 10:15:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 15 10 ? * 6L	job下一步执行时间:2014-3-28 10:15:00	job上一步执行时间:2014-2-28 10:15:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 15 10 ? * 6L	job下一步执行时间:2014-3-28 10:15:00	job上一步执行时间:2014-2-28 10:15:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 15 10 ? * 6L 2002-2005	job下一步执行时间:	job上一步执行时间:2005-12-30 10:15:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 15 10 ? * 6#3	job下一步执行时间:2014-3-21 10:15:00	job上一步执行时间:2014-2-21 10:15:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 0 12 1/5 * ?	job下一步执行时间:2014-3-1 12:00:00	job上一步执行时间:2014-2-26 12:00:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 11 11 11 11 ?	job下一步执行时间:2014-11-11 11:11:00	job上一步执行时间:2013-11-11 11:11:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 0 9,12,18,21 * * ? *	job下一步执行时间:2014-3-1 9:00:00	job上一步执行时间:2014-2-28 21:00:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 0 0 L-28W * ?	job下一步执行时间:2014-3-3 0:00:00	job上一步执行时间:2014-1-3 0:00:00	
job开始时间:2014-3-1 0:00:00	cron表达式:10-20 10-20 * * * ? *	job下一步执行时间:2014-3-1 0:10:10	job上一步执行时间:2014-2-28 23:20:20	
job开始时间:2014-3-1 0:00:00	cron表达式:0 0 0 28,29,30,31 * ?	job下一步执行时间:2014-3-28 0:00:00	job上一步执行时间:2014-2-28 0:00:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0/5 * * * * ? *	job下一步执行时间:2014-3-1 0:00:05	job上一步执行时间:2014-2-28 23:59:55	
job开始时间:2014-3-1 0:00:00	cron表达式:0 0 6 * * ? *	job下一步执行时间:2014-3-1 6:00:00	job上一步执行时间:2014-2-28 6:00:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 0 9,12,18,21 * * ? * 	job下一步执行时间:2014-3-1 9:00:00	job上一步执行时间:2014-2-28 21:00:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 0 21 * * ? * 	job下一步执行时间:2014-3-1 21:00:00	job上一步执行时间:2014-2-28 21:00:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 0 6 * * ? * 	job下一步执行时间:2014-3-1 6:00:00	job上一步执行时间:2014-2-28 6:00:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 0/5 * * * ? *	job下一步执行时间:2014-3-1 0:05:00	job上一步执行时间:2014-2-28 23:55:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 0 0 * * ? *	job下一步执行时间:2014-3-2 0:00:00	job上一步执行时间:2014-2-28 0:00:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 0 0 1 * ? *	job下一步执行时间:2014-4-1 0:00:00	job上一步执行时间:2014-2-1 0:00:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 0 6 * * ? *	job下一步执行时间:2014-3-1 6:00:00	job上一步执行时间:2014-2-28 6:00:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 0 21 * * ? *	job下一步执行时间:2014-3-1 21:00:00	job上一步执行时间:2014-2-28 21:00:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 0 6 * * ? * 	job下一步执行时间:2014-3-1 6:00:00	job上一步执行时间:2014-2-28 6:00:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 0 6 * * ? * 	job下一步执行时间:2014-3-1 6:00:00	job上一步执行时间:2014-2-28 6:00:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 0 6 * * ? * 	job下一步执行时间:2014-3-1 6:00:00	job上一步执行时间:2014-2-28 6:00:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 0 6 * * ? * 	job下一步执行时间:2014-3-1 6:00:00	job上一步执行时间:2014-2-28 6:00:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 0 21 * * ? *	job下一步执行时间:2014-3-1 21:00:00	job上一步执行时间:2014-2-28 21:00:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 0 6 * * ? *	job下一步执行时间:2014-3-1 6:00:00	job上一步执行时间:2014-2-28 6:00:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 0 6 * * ? *	job下一步执行时间:2014-3-1 6:00:00	job上一步执行时间:2014-2-28 6:00:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 0 9,12,18,21 * * ? * 	job下一步执行时间:2014-3-1 9:00:00	job上一步执行时间:2014-2-28 21:00:00	
job开始时间:2014-3-1 0:00:00	cron表达式:0 0 6 * * ? * 	job下一步执行时间:2014-3-1 6:00:00	job上一步执行时间:2014-2-28 6:00:00	
 * @author Alex
 * @date 2017年4月3日 下午8:04:30
 */
public class CronExpressionTest {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss z");

	@Test
	public void test() {
		Date date = new Date();
		String dateStr = DATE_FORMAT.format(date);
//		CronExpression cron = new CronExpression("1 1 1 1 1 ? 2014");
		CronExpression cron = new CronExpression("* * * * * ?");
		
		System.out.println("cron表达式:" + cron);
		System.out.println("开始时间:" + dateStr);
		Date timeBefore = cron.getTimeBefore(date);
		System.out.println("上一步执行时间:" + (timeBefore == null ? "" : DATE_FORMAT.format(timeBefore)));
		Date timeAfter = cron.getTimeAfter(date);
		System.out.println("下一步执行时间:" + (timeAfter == null ? "" : DATE_FORMAT.format(timeAfter)));
	}
}
