package com.alex.game.robot.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alex.game.common.Game;
import com.alex.game.common.util.NetUtil;
import com.alex.game.common.util.RandomUtil;
import com.alex.game.core.concurrent.TaskExecutor;
import com.alex.game.core.concurrent.schedule.Scheduler;
import com.alex.game.dbdata.dom.PlayerRobotDom;
import com.alex.game.dbdata.mapper.PlayerMapper;
import com.alex.game.games.dice.DicePlayer;
import com.alex.game.plaza.PlazaPlayer;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * 机器人玩家Scheduler
 * 
 * @author Alex
 * @date 2017年4月19日 下午1:53:54
 */
@Singleton
public class PlayerScheduler {
	private static final Logger LOG = LoggerFactory.getLogger(PlayerScheduler.class);
	// 每次schedule时最多登陆人数
//	private static final int SCHEDULE_LOGIN_LIMIT = 2000;
	// 服务器ip
	private final String serverIp;
	// 服务器端口
	private final int serverPort;
	// 捕鱼机器人人数from
	private final int robotPlayersFrom;
	// 捕鱼机器人人数to
	private final int robotPlayersTo;
	// 捕鱼机器人人数配置
	private final JSONObject robotPlayersConfig;
	private final PlayerMapper playerMapper;
	// 机器人玩家定时调度器
	private final Scheduler scheduler = new Scheduler(1, r -> new Thread(r, "RobotPlayer-Scheduler"));
	// 游戏Executor
	public final TaskExecutor[] taskExecutors = TaskExecutor.createExecutors("Game-Executor", 32);
	// 所有机器人Dom
	private final ConcurrentHashMap<Long, PlayerRobotDom> robotDoms = new ConcurrentHashMap<>();
	// 已经创建的机器人
	private final ConcurrentHashMap<Long, Player> players = new ConcurrentHashMap<>();
	private final int loginLimit;
	private final boolean robotCheck;
	private final List<String> apiUrls;;
	
	@Inject
	public PlayerScheduler(PlayerMapper playerMapper,
			@Named("server.ip")String serverIp, 
			@Named("server.tcpPort")int serverPort,
			@Named("robot.players.from")int robotPlayersFrom,
			@Named("robot.players.to")int robotPlayersTo,
			@Named("buyu.loginLimit")int loginLimit,
			@Named("robot.check")boolean robotCheck,
			@Named("robot.apiUrls")String apiUrls,
			@Named("robot.players")String robotPlayersConfigStr) {
		this.playerMapper = playerMapper;
		this.serverIp = serverIp;
		this.serverPort = serverPort;
		this.loginLimit = loginLimit;
		this.robotPlayersFrom = robotPlayersFrom;
		this.robotPlayersTo = robotPlayersTo;
		this.robotPlayersConfig = JSON.parseObject(robotPlayersConfigStr);
		this.robotCheck = robotCheck;
		this.apiUrls = new ArrayList<>();
		for (String apiUrl : apiUrls.split(",")) {
			this.apiUrls.add(apiUrl);
		}
		// 加载机器人
		loadRobots();
	}
	
	/**
	 * 加载机器人数据
	 */
	private void loadRobots() {
		LOG.info("正在加载玩家数据....");
		List<PlayerRobotDom> robotsList = playerMapper.selectRobots(robotPlayersFrom, robotPlayersTo - robotPlayersFrom);
		Collections.shuffle(robotsList);
		for (PlayerRobotDom robotDom : robotsList) {
			if (robotDom.isLocked()) {
				continue;
			}
			robotDoms.put(robotDom.getId(), robotDom);
		}
		LOG.info("共加载[{}]条玩家数据", robotsList.size());
	}
	
	/**
	 * 启动，每隔一分钟schedule一次机器人玩家
	 */
	public void startUp() {
		scheduler.scheduleWithFixedDelay(this::schedulePlayers, 0, 1, TimeUnit.MINUTES);
		LOG.info("机器人服务器启动成功");
	}
	
	/**
	 * schedule机器人玩家
	 * @param players
	 */
	private void schedulePlayers() {
		try {
			// 服务器端口开放才启动玩家
			if (!robotCheck && !NetUtil.remotePortAbled(serverIp, serverPort)) {
				LOG.warn("远程IP[{}]端口[{}]不可用不能schedule机器人", serverIp, serverPort);
				return;
			}
			
			for (Entry<String, Object> configEtr : robotPlayersConfig.entrySet()) {
				schedulePlayers(Integer.parseInt(configEtr.getKey()), Integer.parseInt(configEtr.getValue().toString()));
			}
			
		} catch (Exception e) {
			LOG.error("schedulePlayers异常", e);
		}
	}
	
	/**
	 * schedule大厅机器人
	 * 
	 * @param game					游戏,0:代表大厅
	 * @param playersNum			需要进行游戏的人数
	 */
	private void schedulePlayers(int game, int playersNum) {
		// 没有进入游戏可以被使用的玩家
		List<PlayerRobotDom> usableRobotDoms = new ArrayList<>();
		// 机器人玩家
		List<Player> gamePlayers = new ArrayList<>();
		
		for (PlayerRobotDom robotDom : robotDoms.values()) {
			long playerId = robotDom.getId();
			Player player = players.get(playerId);
			if (player == null) {
				usableRobotDoms.add(robotDom);
			} else if(player.game() == game) {
				gamePlayers.add(player);
			}
		}
		
		if (playersNum > gamePlayers.size()) { // 人数不足，补充人数
			for (int i = 0; i < usableRobotDoms.size() && i < playersNum - gamePlayers.size() && i < loginLimit; i++) {
				
				PlayerRobotDom robotDom = usableRobotDoms.get(i);
				Player player = createPlayer(robotDom, game);
				if (player != null) {
					players.put(player.id, player);
					if (robotDom.isTourist()) {
						scheduler.schedule(() -> player.touristLogin(serverIp, serverPort, robotDom.getTouristMac(), robotCheck), RandomUtil.random(1, 60), TimeUnit.SECONDS);
					} else {
						scheduler.schedule(() -> player.login(serverIp, serverPort, robotDom.getUserName(), robotDom.getTouristMac(), robotCheck), RandomUtil.random(1, 60), TimeUnit.SECONDS);
					}
				}
				
			}
			LOG.info("[{}]没有进入游戏可以被使用的玩家数量:[{}]机器人玩家:[{}]需要进行游戏的人数[{}]已经创建机器人数[{}]",game,usableRobotDoms.size(),gamePlayers.size(),playersNum,players.size());
		} else if (playersNum < gamePlayers.size()) { // 人数过多
			for (int i = 0; i < gamePlayers.size() && i < gamePlayers.size() - playersNum; i++) {
				//gamePlayers.get(i).delayLogout();
			}
		}
		
		// schedule玩家
		for (Player p : gamePlayers) {
			scheduler.schedule(() -> p.schedule(), RandomUtil.random(0, 60), TimeUnit.SECONDS);
		}
	}
	
	/**
	 * 创建玩家
	 * 
	 * @param robotDom
	 * @param game
	 * @return
	 */
	public Player createPlayer(PlayerRobotDom robotDom, int game) { 
		long playerId = robotDom.getId();
		String userName = robotDom.getUserName();
		TaskExecutor taskExecutor = taskExecutors[(int)(playerId & (taskExecutors.length - 1))];
		String apiUrl = RandomUtil.randomList(apiUrls);
		if (game == 0) {
			return new PlazaPlayer(playerId, userName, taskExecutor, apiUrl);
		}else if (game == Game.DICE.id) {
			return new DicePlayer(playerId, userName, taskExecutor, apiUrl);
		} 
		
		return null;
	}
	
	/**
	 * 移除玩家
	 * @param player
	 */
	public void removePlayer(Player player) {
		long playerId = player.id;
		if (players.remove(playerId) != null) {
			LOG.info("机器人玩家[{}]退出游戏 ", playerId);
			player.clear();
		}
	}
	
	/**
	 * 获取玩家
	 * @param playerId
	 */
	public Player getPlayer(long playerId) {
	
		return players.get(playerId);
	}
	public ConcurrentHashMap<Long, Player> getPlayers() {
		return players;
	}
}
