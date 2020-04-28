package com.alex.game.dblog.core;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.junit.Test;

import com.alex.game.dblog.core.annotation.LogTable;

/**
 * DbLogger单元测试
 * 
 * @author Alex
 * @date 2017年4月2日 下午2:15:59
 */
public class DbLoggerTest {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
	
	/**
	 * 测试几个特殊点，当年的开始和当年的2月最后一天
	 */
	@Test
	public void testTime() {
		ZoneId zone = ZoneId.systemDefault();
		// 2017年1月1日
		LocalDateTime testTime = LocalDateTime.of(2017, 1, 1, 0, 0);
		doTestTime(Date.from(testTime.atZone(zone).toInstant()));
		// 2017年2月28日0点
		testTime = LocalDateTime.of(2017, 2, 28, 0, 0);
		doTestTime(Date.from(testTime.atZone(zone).toInstant()));
		// 2017年2月28日23点59分59秒
		testTime = LocalDateTime.of(2017, 2, 28, 23, 59, 59);
		doTestTime(Date.from(testTime.atZone(zone).toInstant()));
	}
	
	/**
	 * 测试DbLogger的日期计算是否正确
	 */
	private void doTestTime(Date time) {
		System.out.println("测试时间:" + DATE_FORMAT.format(time));
		// 测试日logger
		DbLogger dayLogger = DbLogger.create(DayLog.class, time);
		System.out.println("开始日:" + DATE_FORMAT.format(new Date(dayLogger.startTime)));
		System.out.println("结束日:" + DATE_FORMAT.format(new Date(dayLogger.endTime)));
		// 测试月logger
		DbLogger monthLogger = DbLogger.create(MonthLog.class, time);
		System.out.println("开始月:" + DATE_FORMAT.format(new Date(monthLogger.startTime)));
		System.out.println("结束月:" + DATE_FORMAT.format(new Date(monthLogger.endTime)));
		// 测试年logger
		DbLogger yearLogger = DbLogger.create(YearLog.class, time);
		System.out.println("开始年:" + DATE_FORMAT.format(new Date(yearLogger.startTime)));
		System.out.println("结束年:" + DATE_FORMAT.format(new Date(yearLogger.endTime)));
		// 测试单表logger
		DbLogger singleLogger = DbLogger.create(SingleLog.class, time);
		System.out.println("开始:" + singleLogger.startTime);
		System.out.println("结束:" + singleLogger.endTime);
	}
}

@LogTable(name="day_log", type = TableType.DAY)
abstract class DayLog extends DbLog {}

@LogTable(name="month_log", type = TableType.MONTH)
abstract class MonthLog extends DbLog {}

@LogTable(name="year_log", type = TableType.YEAR)
abstract class YearLog extends DbLog {}

@LogTable(name="single_log", type = TableType.SINGLE)
abstract class SingleLog extends DbLog {}
