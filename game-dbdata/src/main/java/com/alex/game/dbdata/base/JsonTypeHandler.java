/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */
package com.alex.game.dbdata.base;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * JsonTypeHandler,json字符串到对象的解析
 * 
 * @author Alex
 * @date 2016年12月27日 上午11:49:01
 */
public class JsonTypeHandler<T extends Object> extends BaseTypeHandler<T> {
	private Class<T> clazz;

	public JsonTypeHandler(Class<T> clazz) {
		this.clazz = clazz;
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, JSON.toJSONString(parameter, SerializerFeature.WriteClassName));
	}

	@Override
	public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return JSON.parseObject(rs.getString(columnName), clazz);
	}

	@Override
	public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return JSON.parseObject(rs.getString(columnIndex), clazz);
	}

	@Override
	public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return JSON.parseObject(cs.getString(columnIndex), clazz);
	}

}
