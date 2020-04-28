import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alex.game.robot.core.ApplicationContext;
import com.alex.game.robot.core.PlayerScheduler;
import com.alex.game.robot.handler.MsgHandlerFactory;

/**
 * 机器人启动器
 * 
 * @author Alex
 * @date 2017年4月19日 下午4:12:29
 */
public class RobotLauncher {
	
	private static final Logger LOG = LoggerFactory.getLogger(RobotLauncher.class);

	public static void main(String[] args) throws InterruptedException {
		try {
			MsgHandlerFactory.loadConfig();
			ApplicationContext app = ApplicationContext.createInstance(); 			
			// handler工厂扫描package注册所有handler  
			MsgHandlerFactory.registerAllHandlers();
			app.getBean(PlayerScheduler.class).startUp();
		} catch (Exception e) {
			LOG.error("机器人启动异常", e);
			System.exit(1);
		}
	}
}
