package com.alex.game.robot.core;

import java.io.FileReader;
import java.util.Properties;
import java.util.Set;

import org.apache.ibatis.io.ResolverUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alex.game.core.db.DBModule;
import com.alex.game.core.db.XmlDBModule;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.Stage;
import com.google.inject.name.Names;

/**
 * ApplicationContext
 * 
 * @author Alex
 * @date 2016/7/26 21:00
 */
public class ApplicationContext {
	private static final Logger LOG = LoggerFactory.getLogger(ApplicationContext.class);
	
	private static final String ROBOT_CFG_FILE = "config/application.properties";
	
    // game-data数据库jdbc路径
    private static final String DATADB_JDBC_PATH = "config/jdbc/datadb.properties";
    // game-data数据库environment
    private static final String DATADB_ENVIRONMENT_ID = "datadb_product";
    // game-data数据库xml配置文件
    private static final String DATADB_XML_CFG_PATH = "datadb-config.xml";

    // game-dic数据库jdbc路径
    private static final String DICDB_JDBC_PATH = "config/jdbc/dicdb.properties";
    // game-dic数据库mapper包
    private static final String DICDB_MAPPER_PKG = "com.alex.game.dbdic.mapper";
    
    private static final String PACKAGE_PATH = "com.alex.game";
	
	/**
	 * 单例，最好为volatile或AtomicReference,但是ApplicationContext调用很频繁，不加性能可能会更好，
	 * 由于ApplicationContext在main中初始化一次后不再改变，instance可视化问题几乎不存在*/ 
	private static ApplicationContext instance;
	
	/**guice的injector*/ 
	private final Injector injector;
	
	private ApplicationContext() {
		this.injector = Guice.createInjector(Stage.PRODUCTION, 
				// game-data数据库模块
				new XmlDBModule(DATADB_JDBC_PATH, DATADB_ENVIRONMENT_ID, DATADB_XML_CFG_PATH),
				// game-dic数据库模块
				new DBModule(DICDB_JDBC_PATH, DICDB_MAPPER_PKG),
				new AbstractModule() {
					
					@Override
					protected void configure() {
						Properties props = new Properties();
						try {
							props.load(new FileReader(ROBOT_CFG_FILE));
						} catch (Exception e) {
							LOG.error("加载文件[{}]失败，请检查后再启动", ROBOT_CFG_FILE);
							System.exit(1);
						}
						Names.bindProperties(binder(), props);
						 Set<Class<? extends Object>> singletonTypes = new ResolverUtil<Object>().find(new ResolverUtil.AnnotatedWith(Singleton.class), PACKAGE_PATH).getClasses();
						 for (Class<? extends Object> singletonType : singletonTypes) {
							binder().bind(singletonType);
						}
					}
					
				});
	}

	/**
	 * @Description:创建实例,同步方法创建
	 * @return
	 */
	public static synchronized ApplicationContext createInstance() {
		
		if (instance == null) {
			LOG.info("创建ApplicationContext...");
			instance = new ApplicationContext();
			LOG.info("创建ApplicationContext成功");
		} else {
			LOG.warn("ApplicationContext实例已经创建，请勿重复创建");
		}
		
		return instance;
	}
	
	/**
	 * @Description:获取实例
	 * @return
	 */
	public static ApplicationContext getInstance() {
		
		if (instance == null) {
			createInstance();
		}
		
		return instance;
	}
	
	/**
	 * @Description:获取bean实例
	 * @param type
	 * @return
	 */
	public <T> T getBean(Class<T> type) {
		return injector.getInstance(type);
	}
	
	/**
	 * @Description:获取bean实例
	 * @param key
	 * @return
	 */
	public <T> T getBean(Key<T> key) {
		
		return injector.getInstance(key);
	}
	
	/**
	 * @Description:手动注入
	 * @param instance
	 */
	public void injectMembers(Object instance) {
		injector.injectMembers(instance);
	}
}
