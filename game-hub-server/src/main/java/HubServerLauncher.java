import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alex.game.dblog.core.DbLogService;
import com.alex.game.dblog.core.DbLoggerFactory;
import com.alex.game.dblog.server.ShutDownServerLog;
import com.alex.game.hub.server.ApplicationContext;
import com.alex.game.hub.server.HubServer;
import com.alex.game.hub.server.http.HttpServiceFactory;

/**
 * HubServerLauncher
 * 
 * @author Alex
 * @date 2017年5月14日 下午9:59:18
 */
public class HubServerLauncher {

	private static final Logger LOG = LoggerFactory.getLogger(HubServerLauncher.class);

	public static void main(String[] args) {
		try {
			// 扫描包注册所有数据库日志Logger，校验正在写的日志表的列定义是否正确，在开发阶段偶尔会调整字段类型,长度等
			DbLoggerFactory.registerAllLoggers();
			// 创建程序上下文
			ApplicationContext appCtx = ApplicationContext.createInstance();
			// httpservice工厂扫描package注册所有httpservice
			HttpServiceFactory.registerAllServices();

			HubServer httpServer = appCtx.getBean(HubServer.class);
			// 注册关闭Hook，所有的关闭操作都注册到onShutDown中
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				LOG.info("服务器关闭中...");
				long now = System.currentTimeMillis();
				httpServer.shutdown();
				DbLogService.log(new ShutDownServerLog());
				DbLogService.shutdown();
				LOG.info("服务器关闭成功,耗时[{}]毫秒", System.currentTimeMillis() - now);
			}, "CloseServer"));
			
			httpServer.start();
		} catch (Exception e) {
			LOG.error("服务器启动异常", e);
			System.exit(1);
		}
	}
}
