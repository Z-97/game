/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.dblog.core.util;

/**
 * 列信息
 *
 * @author Alex
 * @date 2016/7/5 14:04
 */
public class ColumnInfo {
	// 列名
	private final String name;
	// 列的类型(包含大小)
	private final String type;
	// 备注
	public final String remark;

	public ColumnInfo(String name, String type, String remark) {
		this.name = name;
		this.type = type;
		this.remark = remark;
	}

	/**
	 * 列ddl语句
	 *
	 * @return
	 */
	public String ddl() {
		StringBuilder ddl = new StringBuilder(64);
		ddl.append("`");
		ddl.append(name);
		ddl.append("` ");
		ddl.append(type);
		ddl.append(" DEFAULT NULL ");
		ddl.append(" COMMENT '" + remark.trim() + "'");

		return ddl.toString();
	}
}
