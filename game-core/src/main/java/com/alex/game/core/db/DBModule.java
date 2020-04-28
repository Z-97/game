/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.core.db;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.mybatis.guice.MyBatisModule;

import com.google.inject.PrivateModule;
import com.google.inject.name.Names;

/**
 * 数据库模块,没有使用xml配置的mybatise模块
 *
 * @author Alex
 * @date 2016/7/28 15:46
 */
public class DBModule extends PrivateModule {

	// 数据库jdbc配置文件路径
	private final Properties jdbcProps = new Properties();
	// 数据库mapper包
	private final String mapperPkg;
	private Set<Class<?>> mapperClasses;

	public DBModule(String jdbcPath, String mapperPkg) {
		this.mapperPkg = mapperPkg;
		try (FileReader jdbcReader = new FileReader(jdbcPath)) {
			jdbcProps.load(jdbcReader);
		} catch (IOException e) {
			throw new RuntimeException("读取jdbc配置文件[" + jdbcPath + "]错误", e);
		}
	}

	@Override
	protected void configure() {
		install(new MyBatisModule() {

			@Override
			protected void initialize() {
				bindDataSourceProvider(new HikariDataSourceProvider(jdbcProps));
				bindTransactionFactoryType(JdbcTransactionFactory.class);
				Names.bindProperties(this.binder(), jdbcProps);
				mapperClasses = new ResolverUtil<Object>().find((type) -> type.isInterface(), mapperPkg).getClasses();
				// 将mapperPkg包下的接口都导入
				addMapperClasses(mapperClasses);
			}
		});

		// expose所有mapper
		for(Class<?> type:mapperClasses) {
			expose(type);
		}
		//mapperClasses.forEach((type) -> expose(type));

		/*
		 * expose SqlSessionFactory，SqlSession，SqlSessionManager
		 */
		/*
		 * Named xx = Names.named("xxxxx");
		 * bind(SqlSessionFactory.class).annotatedWith(xx).to(SqlSessionFactory.
		 * class); expose(SqlSessionFactory.class).annotatedWith(xx);
		 * 
		 * bind(SqlSession.class).annotatedWith(xx).toProvider(
		 * SqlSessionManagerProvider.class).in(Scopes.SINGLETON);
		 * expose(SqlSession.class).annotatedWith(xx);
		 * 
		 * bind(SqlSessionManager.class).annotatedWith(xx).toProvider(
		 * SqlSessionManagerProvider.class).in(Scopes.SINGLETON);
		 * expose(SqlSessionManager.class).annotatedWith(xx);
		 */
	}
}
