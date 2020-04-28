/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.dblog.core.util;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.alex.game.dblog.core.DbLog;
import com.alex.game.dblog.core.TableType;
import com.alex.game.dblog.core.annotation.Column;
import com.alex.game.dblog.core.annotation.LogTable;

/**
 * 数据库日志帮助类，提供工具方法，方便使用
 *
 * @author Alex
 * @date 2016/7/4 16:31
 */
public class DbLogHelper {

	private static final SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat YYYY_MM = new SimpleDateFormat("yyyyMM");
	private static final SimpleDateFormat YYYY = new SimpleDateFormat("yyyy");

	public DbLogHelper() {
	}

	/**
	 * 构建创建表sql，主键id 自增
	 * 
	 * @param tableName
	 * @return
	 */
	public static String buildCreateTblSql(String tableName, Field[] fields) {
		// 表定义sql
		StringBuilder ddl = new StringBuilder(256);
		ddl.append("CREATE TABLE IF NOT EXISTS `").append(tableName);
		ddl.append("` (\n`id` int(11) NOT NULL AUTO_INCREMENT,\n");
		
		for (ColumnInfo colInfo : columns(fields).values()) {
			ddl.append(colInfo.ddl() + ",\n");
		}
		
		// 实际测试InnoDB比MyISAM写入速度快，见基准测试
//		ddl.append("PRIMARY KEY (`id`)) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4");
		ddl.append("PRIMARY KEY (`id`)) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4");
		return ddl.toString();
	}

	/**
	 * 通过表名从数据库获取列信息
	 * 
	 * @param conn
	 * @param tblName
	 * @return
	 * @throws Exception
	 */
	public static Map<String, ColumnInfo> columns(Connection conn, String tblName) throws Exception {
		Map<String, ColumnInfo> columns = new LinkedHashMap<>();
		String query = "SELECT COLUMN_NAME, COLUMN_TYPE, COLUMN_COMMENT FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '%s' AND TABLE_SCHEMA = '%s'";
		// conn.getMetaData().getColumns(null, "%", tblName, "%")这种方式拿到的int
		// bigint大小少1，bit(1)又是正确的，不采用
		try (Statement statement = conn.createStatement();
				ResultSet resSet = statement.executeQuery(String.format(query, tblName, conn.getCatalog()))) {
			while (resSet.next()) {
				String name = resSet.getString("COLUMN_NAME");
				String type = resSet.getString("COLUMN_TYPE");
				String remark = resSet.getString("COLUMN_COMMENT");
				
				columns.put(name, new ColumnInfo(name, type, remark));
			}
		}

		return columns;
	}

	/**
	 * 通过日志Class获取列信息
	 *
	 * @param logType
	 * @return
	 */
	public static Map<String, ColumnInfo> columns(Field[] fields) {
		Map<String, ColumnInfo> columns = new LinkedHashMap<>();
		for (Field field : fields) {
			Column column = field.getAnnotation(Column.class);
			if (column != null) {
				String colName = column.name();
				columns.put(colName, new ColumnInfo(colName, column.type(), column.remark()));
			}
		}

		return columns;
	}

	/**
	 *  构建PrepStatement 插入数据预编译Sql
	 *  
	 * @param tableName
	 * @param fields
	 * @return
	 */
	public static String buildPrepInsertSql(String tableName, Field[] fields) {
		StringBuilder sql = new StringBuilder(256);
		// 表名是不支持占位符的，表面需要拼接
		sql.append("insert into `" + tableName + "` ");

		// 字段字符串
		StringBuilder fieldsStr = new StringBuilder(128);
		// 占位符字符串
		StringBuilder placeholdersStr = new StringBuilder(128);
		fieldsStr.append("(");
		placeholdersStr.append("(");

		// 所有的字段必须是public的且被Column注解
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			Column fieldAnn = field.getAnnotation(Column.class);
			if (fieldAnn != null) {
				fieldsStr.append("`" + fieldAnn.name() + "`,");
				placeholdersStr.append("?,");
			}
		}
		// 删除最后的逗号?
		fieldsStr.deleteCharAt(fieldsStr.length() - 1);
		fieldsStr.append(")");
		// 删除最后的逗号?
		placeholdersStr.deleteCharAt(placeholdersStr.length() - 1);
		placeholdersStr.append(")");

		return sql.append(fieldsStr).append(" values ").append(placeholdersStr).toString();
	}

	/**
	 * 根据日志class和日期计算表名
	 *
	 * @param logType
	 * @param time
	 * @return
	 */
	public static String tableName(Class<? extends DbLog> logType, Date time) {
		// 日志表注解
		LogTable logTable = logType.getAnnotation(LogTable.class);
		// 表名
		StringBuilder tblName = new StringBuilder(logTable.name());
		// 表类型,默认单表
		TableType tblType = logTable != null?logTable.type() : TableType.SINGLE;

		switch (tblType) {
			case YEAR: tblName.append(YYYY.format(time)); break;
			case MONTH: tblName.append(YYYY_MM.format(time)); break;
			case DAY: tblName.append(YYYY_MM_DD.format(time)); break;
			case SINGLE:
			default: break;
		}

		return tblName.toString();
	}

}
