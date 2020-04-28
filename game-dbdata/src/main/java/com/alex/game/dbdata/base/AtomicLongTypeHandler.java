/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */
package com.alex.game.dbdata.base;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * AtomicLongTypeHandler
 * 
 * @author Alex
 * @date 2016年12月27日 上午11:49:01
 */
public class AtomicLongTypeHandler extends BaseTypeHandler<AtomicLong> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, AtomicLong parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setLong(i, parameter.get());
	}

	@Override
	public AtomicLong getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return new AtomicLong(rs.getLong(columnName));
	}

	@Override
	public AtomicLong getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return new AtomicLong(rs.getLong(columnIndex));
	}

	@Override
	public AtomicLong getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return new AtomicLong(cs.getLong(columnIndex));
	}
}
