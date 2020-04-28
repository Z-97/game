package com.alex.game.dblog.core;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alex.game.dblog.core.annotation.Column;
import com.alex.game.dblog.core.annotation.LogTable;
import com.alex.game.dblog.core.util.ColumnInfo;
import com.alex.game.dblog.core.util.DbLogHelper;

/**
 * DbLogger，日志记录器，一个DbLogger对应一张表，如果按日志时间超过DbLogger对应的时间则创建一个新的DbLogger实例
 * 
 * @author Alex
 * @date 2017年4月1日 下午11:52:17
 */
public class DbLogger {

	private static final Logger LOG = LoggerFactory.getLogger(DbLogger.class);
	// 批量提交日志的限制
	private static final int BATCH_LIMIT = 1000;

	// DbLog Class
	public final Class<? extends DbLog> type;
	// 缓存的DbLog Class的属性字段，不从class中获取Fields,因为每次都是copy
	private final Field[] fields;
	// 正在写入的表名
	public final String tblName;
	// 正在写入的日志表的开始时间,单表为long最小值
	public final long startTime;
	// 正在写入的日志表的结束时间,单表为long最大值
	public final long endTime;
	// 插入数据PreparedStatement sql语句，提高性能，避免拼接sql，防注入，同时减少因大量拼接sql字符串导致的垃圾回收
	private final String prepInsertSql;
	// 创建表的sql
	public final String createTblSql;
	// 由于记录日志的量很大，缓冲DbLog后批量提交
	private final ConcurrentLinkedQueue<DbLog> logs = new ConcurrentLinkedQueue<>();

	private DbLogger(Class<? extends DbLog> type, long startTime, long endTime) {
		this.type = type;
		this.fields = type.getFields();
		this.startTime = startTime;
		this.endTime = endTime;
		this.tblName = DbLogHelper.tableName(type, new Date(startTime));
		this.prepInsertSql = DbLogHelper.buildPrepInsertSql(tblName, fields);
		this.createTblSql = DbLogHelper.buildCreateTblSql(tblName, fields);
	}

	/**
	 * 根据日志Class和一个时间点创建DbLogger，根据时间点自动计算日志表startTime和endTime
	 * 
	 * @param type
	 * @param time
	 *            时间点
	 * @return
	 */
	public static DbLogger create(Class<? extends DbLog> type, Date time) {
		ZoneId zone = ZoneId.systemDefault();
		LocalDateTime dateTime = LocalDateTime.ofInstant(time.toInstant(), zone);
		// 日志表注解
		LogTable logTable = type.getAnnotation(LogTable.class);
		// 表类型,默认单表
		TableType tblType = logTable != null ? logTable.type() : TableType.SINGLE;

		long startTime = 0;
		long endTime = 0;
		if (tblType == TableType.YEAR) {// 年表
			// 当年
			LocalDate curYear = LocalDate.ofYearDay(dateTime.getYear(), 1);
			// 下年
			LocalDate nextYear = curYear.plusYears(1);

			startTime = Date.from(curYear.atStartOfDay(zone).toInstant()).getTime();
			endTime = Date.from(nextYear.atStartOfDay(zone).toInstant()).getTime();
		} else if (tblType == TableType.MONTH) {// 月表
			// 当月
			LocalDate curMonth = LocalDate.of(dateTime.getYear(), dateTime.getMonth(), 1);
			// 下月
			LocalDate nextMonth = curMonth.plusMonths(1);

			startTime = Date.from(curMonth.atStartOfDay(zone).toInstant()).getTime();
			endTime = Date.from(nextMonth.atStartOfDay(zone).toInstant()).getTime();
		} else if (tblType == TableType.DAY) {// 日表
			// 当日
			LocalDate curDay = LocalDate.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth());
			// 下日
			LocalDate nextDay = curDay.plusDays(1);

			startTime = Date.from(curDay.atStartOfDay(zone).toInstant()).getTime();
			endTime = Date.from(nextDay.atStartOfDay(zone).toInstant()).getTime();
		} else {// 单表
			startTime = Long.MIN_VALUE;
			endTime = Long.MAX_VALUE;
		}

		return new DbLogger(type, startTime, endTime);
	}

	/**
	 * 增加需要记录的日志，但是现在还为入库
	 * 
	 * @param log
	 */
	public void addLog(DbLog log) {
		logs.add(log);
	}

	/**
	 * 从logs批量取出记录到数据库,抛出异常给上层处理
	 * 
	 * @throws Exception
	 */
	public void log() {
		try (Connection conn = DbLogService.getConnection();
				PreparedStatement prepStatement = conn.prepareStatement(prepInsertSql)) {
			DbLog headLog = null;
			int num = 0;

			while ((headLog = logs.poll()) != null) {
				for (int i = 0, parameterIndex = 1; i < fields.length; i++) {
					Field field = fields[i];
					if (field.getAnnotation(Column.class) != null) {
						prepStatement.setObject(parameterIndex, field.get(headLog));
						parameterIndex++;
					}
				}
				prepStatement.addBatch();
				if (++num >  BATCH_LIMIT) break;
			}

			prepStatement.executeBatch();
			prepStatement.clearBatch();
		} catch (Exception e) {
			// 是否人为把正在写的日志表删除了导致的异常?如:测试时大家都连同一个日志库，有时为了测试会删除日志表，导致另一个游戏服出错
			try (Connection conn = DbLogService.getConnection(); Statement statement = conn.createStatement();) {
				if (!conn.getMetaData().getTables(null, null, tblName, null).next()) {
					createDbTbl();
					log();
					return;
				}
			} catch (Exception e1) {
				e = e1;
			}
			LOG.error("[" + type.getName() + "]记录数据库日志错误", e);
		}
	}

	/**
	 * 校验日志表，1.日志表不存在则新增,2.日志调整字段后，需要调整当前日志表的定义
	 * 在开发阶段偶尔会调整日志表的策略以及调整表列的大小、类型，新增字段等
	 * 
	 * @throws Exception
	 */
	public void validate() throws Exception {
		try (Connection conn = DbLogService.getConnection(); Statement statement = conn.createStatement()) {
			// java class中定义的列信息
			Map<String, ColumnInfo> classCols = DbLogHelper.columns(fields);
			// 数据库中定义的列信息
			Map<String, ColumnInfo> dbCols = DbLogHelper.columns(conn, tblName);

			if (dbCols.size() == 0) {// 日志表还未产生?新增
				createDbTbl();
				return;
			}

			/*
			 * 比较日志class中定义的列和表中定义的列，以代码中的列定义为准
			 */
			// 需要执行的sql语句
			List<String> sqls = new ArrayList<>();
			for (Map.Entry<String, ColumnInfo> classColEtr : classCols.entrySet()) {
				String colName = classColEtr.getKey();
				String classColDDL = classColEtr.getValue().ddl();
				ColumnInfo dbCol = dbCols.get(colName);

				if (dbCol == null) {// 新增列?
					sqls.add("ALTER TABLE " + tblName + " ADD COLUMN " + classColDDL + ";");
				} else if (!classColDDL.equalsIgnoreCase(dbCol.ddl())) {// 修改列?
					sqls.add("ALTER TABLE " + tblName + " MODIFY COLUMN " + classColDDL + ";");
				}
			}

			if (sqls.size() > 0) {
				for (String sql : sqls) {
					LOG.info("检查到日志表变更[{}]", sql);
					statement.addBatch(sql);
				}
				statement.executeBatch();
			}
		}
	}

	/**
	 * 创建数据库日志表
	 */
	void createDbTbl() {
		try (Connection conn = DbLogService.getConnection(); Statement statement = conn.createStatement();) {
			statement.execute(createTblSql);
		} catch (Exception e) {
			throw new RuntimeException("创建日志表[" + tblName + "]sql[" + createTblSql + "]错误", e);
		}
	}
	
	/**
	 * 下一次的日志表名字,单表则不变
	 * 
	 * @return
	 */
	private String nextTableName() {
		// 下日(减一分钟,防止当前时间点是0点加一天后就是过明天了)
		Date nextDay = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000 - 60 * 1000);
		return DbLogHelper.tableName(type, nextDay);
	}

	/**
	 * 创建下一次的日志表
	 */
	public void createNextDbTbl() {
		String nextTableName = nextTableName();
		String createNextTblSql = DbLogHelper.buildCreateTblSql(nextTableName, fields);
		
		try (Connection conn = DbLogService.getConnection(); Statement statement = conn.createStatement();) {
			if (!conn.getMetaData().getTables(null, null, nextTableName, null).next()) {
				statement.execute(createNextTblSql);
			}
		} catch (Exception e) {
			throw new RuntimeException("创建下次日志表[" + nextTableName + "]sql[" + createNextTblSql + "]错误", e);
		}
	}
}
