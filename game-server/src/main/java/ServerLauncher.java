/*

 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alex.game.common.util.NetUtil;
import com.alex.game.dblog.core.DbLogService;
import com.alex.game.dblog.core.DbLoggerFactory;
import com.alex.game.dblog.server.ShutDownServerLog;
import com.alex.game.event.manager.EventMgr;
import com.alex.game.event.struct.server.ShutdownEvent;
import com.alex.game.schedule.manager.ScheduleMgr;
import com.alex.game.server.ApplicationContext;
import com.alex.game.server.ExecutorMgr;
import com.alex.game.server.GameServer;
import com.alex.game.server.HttpServer;
import com.alex.game.server.filter.FilterChain;
import com.alex.game.server.http.HttpServiceFactory;
import com.alex.game.server.tcp.MsgHandlerFactory;

/**
 * ServerLauncher
 * 
 * @author Alex
 * @date 2016/7/5 17:59
 */
public class ServerLauncher {

	private static final Logger LOG = LoggerFactory.getLogger(ServerLauncher.class);
    // server配置文件
    private static final String SERVER_CFG_FILE = "config/application.properties";
   
	public static void main(String[] args) {
		try {
			// 校验本地端口
			checkLocalPort();
			// 扫描包注册所有数据库日志Logger，校验正在写的日志表的列定义是否正确，在开发阶段偶尔会调整字段类型,长度等
			DbLoggerFactory.registerAllLoggers();
			//加载协议配置文件
			MsgHandlerFactory.loadConfig();
			// 创建程序上下文
			ApplicationContext appCtx = ApplicationContext.createInstance();
			
			// handler工厂扫描package注册所有handler         
			MsgHandlerFactory.registerAllHandlers();
		    //httpservice工厂扫描package注册所有httpservice
			HttpServiceFactory.registerAllServices();
			// filter链扫描package注册所有filter
			FilterChain.registerAllFilters();

			GameServer gameServer = appCtx.getBean(GameServer.class);
			HttpServer httpServer = appCtx.getBean(HttpServer.class); 
			EventMgr eventMgr = appCtx.getBean(EventMgr.class);
			ExecutorMgr executorMgr = appCtx.getBean(ExecutorMgr.class);
			ScheduleMgr scheduleMgr = appCtx.getBean(ScheduleMgr.class);
			
			// 注册关闭Hook，所有的关闭操作都注册到onShutDown中
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				LOG.warn("游戏服务器关闭中...");
				long now = System.currentTimeMillis();
				gameServer.shutdown();
				httpServer.shutdown();
				scheduleMgr.shutdown();
				// 处理游戏中的关服逻辑，如把玩家T下线，保存玩家数据等
				eventMgr.post(new ShutdownEvent());
				executorMgr.shutdown();
				DbLogService.log(new ShutDownServerLog());
				DbLogService.shutdown();
				LOG.warn("游戏服务器关闭成功,耗时[{}]毫秒", System.currentTimeMillis() - now);
			}, "CloseServer"));
			
			gameServer.start();
			httpServer.start();
		} catch (Exception e) {
			LOG.error("游戏服务器启动异常", e);
			System.exit(1);
		}
	}
	
	/**
	 * 校验本地端口
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void checkLocalPort() throws FileNotFoundException, IOException {
		Properties serverProps = new Properties();
		serverProps.load(new FileReader(SERVER_CFG_FILE));
		int tcpPort = Integer.parseInt("" + serverProps.get("server.tcpPort"));
		int httpPort = Integer.parseInt("" + serverProps.get("server.httpPort"));
		// 端口是否已经被占用
		if (!NetUtil.localPortAbled(tcpPort)) {
			LOG.warn("本地端口[{}]已被占用", tcpPort);
			System.exit(1);
		}
		
		if (!NetUtil.localPortAbled(httpPort)) {
			LOG.warn("本地端口[{}]已被占用", httpPort);
			System.exit(1);
		}
	}
	
}
