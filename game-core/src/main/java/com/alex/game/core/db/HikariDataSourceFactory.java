/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.core.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * HikariDataSourceFactory
 * 
 * @author Alex
 * @date 2016/7/29 14:17
 */
public class HikariDataSourceFactory implements DataSourceFactory {
	private Properties props;

	@Override
	public void setProperties(Properties props) {
		this.props = props;
	}

	@Override
	public DataSource getDataSource() {
		HikariDataSource ds = new HikariDataSource(new HikariConfig(props));
		
		return ds;
	}
}
