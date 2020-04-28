/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.core.db;

import com.google.inject.Provider;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * HikariDataSourceProvider
 * 
 * @author Alex
 * @date 2016/7/28 16:18
 */
public class HikariDataSourceProvider implements Provider<DataSource> {

	private final DataSource dataSource;

	public HikariDataSourceProvider(Properties props) {
		this.dataSource = new HikariDataSource(config(props));
	}

	private HikariConfig config(Properties props) {
		HikariConfig cfg = new HikariConfig();
		// cfg.setDataSourceClassName(props.getProperty("dataSourceClassName"));
		cfg.setJdbcUrl(props.getProperty("dataSource.url"));
		cfg.setUsername(props.getProperty("dataSource.user"));
		cfg.setPassword(props.getProperty("dataSource.password"));
		cfg.addDataSourceProperty("cachePrepStmts", props.getProperty("dataSource.cachePrepStmts"));
		cfg.addDataSourceProperty("prepStmtCacheSize", props.getProperty("dataSource.prepStmtCacheSize"));
		cfg.addDataSourceProperty("prepStmtCacheSqlLimit", props.getProperty("dataSource.prepStmtCacheSqlLimit"));
		return cfg;
	}

	@Override
	public DataSource get() {
		return dataSource;
	}
}
