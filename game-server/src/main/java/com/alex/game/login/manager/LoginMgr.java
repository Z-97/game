package com.alex.game.login.manager;

import com.google.inject.Singleton;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alex.game.common.tuple.Pair;
import com.alex.game.common.util.IdGenerator;
import com.alex.game.common.util.MD5Util;
import com.alex.game.common.util.RandomKeyGenerator;
import com.alex.game.common.util.RandomUtil;
import com.alex.game.dbdata.dom.PlayerDom;
import com.alex.game.dbdic.dic.GameDic;
import com.alex.game.dblog.core.DbLogService;
import com.alex.game.dblog.login.LoginLog;
import com.alex.game.dblog.login.LogoutLog;
import com.alex.game.dblog.login.RegisterLog;
import com.alex.game.event.manager.EventMgr;
import com.alex.game.event.struct.login.PlayerLoginEvent;
import com.alex.game.event.struct.login.PlayerLoginSuccessEvent;
import com.alex.game.event.struct.login.PlayerLogoutEvent;
import com.alex.game.event.struct.net.ChannelCloseEvent;
import com.alex.game.event.struct.server.ShutdownEvent;
import com.alex.game.login.LoginProto.ResTouristRegister;
import com.alex.game.login.struct.LogoutType;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.msg.MsgUtil;
import com.alex.game.player.manager.PlayerMgr;
import com.alex.game.player.manager.PlayerMsgMgr;
import com.alex.game.player.struct.Player;
import com.alex.game.server.ExecutorMgr;
import com.alex.game.server.SessionKey;
import com.alex.game.server.tcp.MsgHandlerFactory;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import io.netty.channel.Channel;
@Singleton
public class LoginMgr {
	private static final Logger HTTP_SERVICE_LOG = LoggerFactory.getLogger("HttpServiceLog");
	private static final Logger LOG = LoggerFactory.getLogger(LoginMgr.class);
	// 密码格式,5-12个 字母 数字 点 减号 下划线
	public static final String PASSWORD_PATTERN = "[\\p{Alnum}\\.\\-\\_]{5,12}";
	// 1开头的手机号
	public static final String PHONE_PATTERN ="^[1][0-9]{10}$";
	// 1到18个字母数字下划线汉字可以有空格
	public static final String USER_PATTERN = "[\\p{Alnum}\\.\\-\\_]{6,16}";
	/*
	 *  缓存手机号发送的验证码,在一定时间内发同一个验证码,提高绑定率,不保存在player中(玩家有可能切后台去看手机验证码断线重连信息丢失)
	 *  Pair:验证码和验证码发送时间
	 */
	private final ConcurrentHashMap<String, Pair<String, Long>> phoneCodes = new ConcurrentHashMap<>();
	@Inject
	private PlayerMgr playerMgr;
	@Inject
	private PlayerMsgMgr playerMsgMgr;
	@Inject
	private ExecutorMgr executorMgr;
	@Inject
	private LoginMsgMgr loginMsgMgr;
	@Inject
	private GameDic gameDic;
	
	private final EventMgr eventMgr;
	@Inject
	public LoginMgr(EventMgr eventMgr) {
		eventMgr.register(this);
		this.eventMgr = eventMgr;
	}
	
	/**
	 * 出错极端情况：
	 * 1.A登录游戏,禁用网络，客户端知道掉线，服务器不知道(认为客户端还在线)；
	 * 2.A开启网络，断线重连，重新登录，服务器一般先收到当前网络连接的登录，后收到1步骤建立的连接的掉线；会导致A在当前连接重新登录成功后(在线玩家列表注册当前玩家)，
	 * 立马处理1步骤建立的连接掉线(在线玩家列表注册移除当前玩家)， 结果就是在线玩家列表中没有当前登录的玩家，
	 * 修正方法:玩家掉线时，掉线的玩家和当前在线的玩家是否为同一个(player == playerMgr.getPlayer(player.getId()))
	 * 
	 * @param event
	 */
	@Subscribe
	public void onChannelClose(ChannelCloseEvent event) {
		executorMgr.getLoginExecutor(event.player.channel).execute(() -> {
			Player player = event.player;
			if (!player.logouted && player == playerMgr.getPlayer(player.getId())) {
				logout(player, LogoutType.NORMAL);
			}
		});
	}
	
	/**
	 * 关服事件处理
	 */
	@Subscribe
	public void onShutDown(ShutdownEvent event) {
		for (Player player : playerMgr.onLinePlayers().values()) {
			logout(player, LogoutType.SHUT_DOWN);
			if (player.channel.isActive()) {
				player.channel.close();
			}
		}
		LOG.info("关服所有玩家踢下线成功");
	}
	
	/**
	 * 登陆
	 * 
	 * @param userName
	 * @param pwd
	 * @param mac
	 */
	public void login(Player player, String userName, String pwd, String mac, int device, String deviceModel) {
		Channel channel = player.channel;
		Long oldPlayerId = channel.attr(SessionKey.PLAYER_ID).get();
		player.mac = mac;
		player.device = device;
		player.deviceModel = deviceModel;
//		if (mac == null || mac.trim().length() == 0) {
//			LOG.warn("会话{}玩家[{}]使用空mac账号登录", player.channel, userName);
//			return;
//		}
		
		if (oldPlayerId != null) {
			LOG.warn("会话{}已经使用玩家[{}][{}]登录,请退出登录后再使用用户名[{}]登录", channel, player.getNickName(), player.getId(), userName);
			return;
		}
		// 根据用户名查找玩家，先在活跃账号缓存找，找不到再去数据库找
		PlayerDom playerDom = playerMgr.selectByUserName(userName);
		if (playerDom == null) {
			LOG.warn("会话{}玩家使用的用户名[{}]不存在", channel, userName);
			loginMsgMgr.sendLoginMsg(player, 1);
			return;
		}
		
		if (pwd == null) {// 密码为空使用key登陆
			LOG.warn("会话{}玩家[{}]登录失败,密码错误", player.channel, userName);
			loginMsgMgr.sendLoginMsg(player, 3);
			return;
		}
		if(!pwd.equals(pwd)) {
			LOG.warn("会话{}玩家[{}]登录失败,密码错误", player.channel, userName);
			loginMsgMgr.sendLoginMsg(player, 3);
			return;
		}
		// 重复登录,将前一个客户端的账号T下线
		Player prePlayer = playerMgr.getPlayer(playerDom.getId());
		if (prePlayer != null) {
			LOG.warn("会话{}玩家[{}]重复登录,之前登录的账号将会被强制下线", channel, userName);
			logout(prePlayer, LogoutType.TICK);
			prePlayer.channel.close();
		}
		
		loginMsgMgr.sendLoginMsg(player, 0);
		playerDom.setLoginDevice(device);
		playerDom.setLoginDeviceModel(deviceModel);
		doLogin(player, playerDom);
	}
	
	/**
	 * 登录
	 * @param player
	 */
	private void doLogin(Player player, PlayerDom playerDom) {
		Date now = new Date();
		// 设置玩家数据
		player.setPlayerDom(playerDom);
		player.setLoginMac(player.mac);
		player.setOnline(true);
		player.setLoginIp(player.ip);
		player.setLoginTime(now);
		player.setLoginCount(player.getLoginCount() + 1);
		player.setLoginKey(MD5Util.encodeWithTail("" + System.currentTimeMillis()));
		
		// 登记玩家
		playerMgr.registerPlayer(player);
		// 异步更新玩家数据，后台统计需要使用在线标识、登陆ip等
		playerMgr.updatePlayerAsync(player);
		// channel设置playerId属性，一个网络连接不能登录2个账号
		player.channel.attr(SessionKey.PLAYER_ID).set(player.getId());
		LOG.info("会话{}玩家[{}][{}]登陆成功", player.channel, player.getNickName(), player.getId());
		
		// 触发玩家登录事件
		eventMgr.post(new PlayerLoginEvent(player));
		// 游戏开始消息
		loginMsgMgr.sendGameListMsg(player,gameDic.values());
		//触发玩家登录成功事件
		eventMgr.post(new PlayerLoginSuccessEvent(player));
		DbLogService.log(new LoginLog(player, player.mac, player.device, player.deviceModel));
	}

	/**
	 * 游客登录
	 * 
	 * @param player
	 * @param mac
	 */
	public void touristLogin(Player player, String mac, int device, String deviceId, String deviceModel,
			String packageId) {
		player.mac = mac;
		player.device = device;
		player.deviceModel = deviceModel;
		
		if (mac == null || mac.trim().length() == 0 || mac.trim().length() > 64) {
			LOG.warn("会话{}游客登录mac[{}]格式不正确", player.channel, mac);
			return;
		}
		
		if (deviceId != null && deviceId.length() > 128) {
			LOG.warn("会话{}玩家使用的唯一设备码[{}]过长", player.channel, deviceId);
			return;
		}
		LOG.info("会话{}玩家使用mac[{}]游客登陆", player.channel, mac);
		// 根据用户名查找玩家，先在活跃账号缓存找，找不到再去数据库找  
		PlayerDom playerDom = playerMgr.selectByTouristMac(mac);
		if (playerDom == null) {
			// 检查注册ip
			String userName = "游客" + IdGenerator.nextId()+RandomKeyGenerator.generate(6);
			String pwd = "" + System.currentTimeMillis();
			playerDom = doRegister(player, userName, pwd, true, "游客", 1, 0, packageId, 0, deviceId,false);
		} else if (!playerDom.isTourist()) {
			//playerMsgMgr.sendWarnMsg(player, 9);
			LOG.warn("会话{}玩家使用的mac[{}]已经被[{}][{}]绑定为正式账号", player.channel, mac, playerDom.getNickName(), playerDom.getId());
			return;
		} else {
			// 重复登录,将前一个客户端的账号T下线
			Player prePlayer = playerMgr.getPlayer(playerDom.getId());
			if (prePlayer != null) {
				LOG.warn("会话{}玩家使用mac[{}]重复登录,之前登录的账号将会被强制下线", player.channel, mac);
				logout(prePlayer, LogoutType.TICK);
				prePlayer.channel.close();
			}
		}
		
		loginMsgMgr.sendLoginMsg(player, 0);
		playerDom.setLoginDevice(device);
		playerDom.setLoginDeviceModel(deviceModel);
		doLogin(player, playerDom);
	}
	/**
	 * 注册账号
	 * 
	 * @param player
	 * @param userName
	 * @param pwd
	 * @param tourist
	 * @param nickName
	 * @param icon
	 * @param sex
	 * @param packageId
	 * @param playerType			(0:普通玩家,1:代理,2:线上推广员,3:线下推广员)
	 * @return
	 */
	public PlayerDom doRegister(Player player, String userName, String pwd, boolean tourist, String nickName, int icon, int sex, String packageId, int playerType, String deviceId,boolean isRobot) {
		Date now = new Date();
		PlayerDom playerDom = new PlayerDom();
		playerDom.setId(IdGenerator.nextId());
		playerDom.setUserName(userName);
		playerDom.setPwd(MD5Util.encodeWithTail(pwd));
		playerDom.setTourist(tourist);
		playerDom.setNickName(nickName);
		playerDom.setIcon(icon);
		playerDom.setSex(sex);
		playerDom.setRegisterIp(player.ip);
		playerDom.setRegisterTime(now);
		playerDom.setLoginTime(now);
		playerDom.setLoginIp(player.ip);
		playerDom.setLoginCount(1);
		playerDom.setOnline(true);
		playerDom.setLoginMac(player.mac);
		playerDom.setRegisterMac(player.mac);
		playerDom.setRegisterDevice(player.device);
		playerDom.setRegisterDeviceModel(player.deviceModel);
		playerDom.setUpdateTime(now);
		playerDom.setSex(1);//默认为女，默认为1
		playerDom.setIcon(1);//默认女性头像为1
		playerDom.setLevel(1);
		int channelId = 0;
		playerDom.setChannelId(channelId + "");
		playerDom.setPackageId(packageId);
		playerDom.setVipLevel(0);
		playerDom.setPlayerType(playerType);
		playerDom.setDeviceId(deviceId);
		if (tourist) playerDom.setTouristMac(player.mac);
		playerDom.setIsRobot(isRobot);
		playerDom.setGold(new AtomicLong(10000000L));
		// 插入数据
		playerMgr.insertPlayer(playerDom);
		// 必须设置PlayerDom，PlayerDom才是数据
		player.setPlayerDom(playerDom);
	
		// 注册日志
		DbLogService.log(new RegisterLog(playerDom, player.mac, player.device, player.deviceModel));
		return playerDom;
	}
	
	/**
	 * 玩家退出登陆
	 * 
	 * @param player
	 * @param typ	1:同一个账号重复登录被T号退出,2:玩家被冻结退出,3:游戏服务器维护被T下线
	 */
	public void logout(Player player, LogoutType type) {
		if (player.dom() == null) {// 玩家没有登录成功就在发退出登陆
			LOG.warn("会话[{}]玩家还没有登陆成功就在请求退出登陆", player.channel);
			return;
		}
		
		player.logouted = true;
		player.setOnline(false);
		player.setLogoutTime(new Date());
	
		
		loginMsgMgr.sendLogoutMsg(player, type);
		// 触发玩家退出事件
		eventMgr.post(new PlayerLogoutEvent(player));
		// channel移除playerId属性
		player.channel.attr(SessionKey.PLAYER_ID).set(null);
		// 取消登记在线玩家
		playerMgr.unregisterPlayer(player);
		playerMgr.updatePlayerAsync(player);
		DbLogService.log(new LogoutLog(player, (System.currentTimeMillis() - player.getLoginTime().getTime()) / 1000));
		LOG.info("会话{}玩家[{}][{}]退出游戏,退出类型[{}]", player.channel, player.getNickName(), player.getId(), type);
		player.reset();
	}

	/**
	 * 注册
	 * @param player
	 * @param userName
	 * @param pwd
	 * @param device
	 * @param deviceId
	 * @param deviceModel
	 */
	public void register(Player player, String userName, String pwd, int device, String deviceId, String deviceModel,String mac) {
		if(userName==null||userName.equals("")||!userName.matches(USER_PATTERN)) {
			loginMsgMgr.sendResRegisterg(player, 1);
			return ;
		}
		PlayerDom playerDom =playerMgr.selectByUserName(userName);
		if (playerDom != null) {
			loginMsgMgr.sendResRegisterg(player, 2);
			return ;
		}
		if(mac!=null) {
			playerDom =playerMgr.selectByTouristMac(mac);
			if(playerDom!=null) {
				playerDom.setUserName(userName);
				playerDom.setPwd(MD5Util.encodeWithTail(pwd));
				playerDom.setTourist(false);
				loginMsgMgr.sendResRegisterg(player, 0);
				return ;
			}
			
		}
		String packageId="0";
		playerDom = doRegister(player, userName, pwd, false, userName, 1, 0, packageId, 0, deviceId,false);
		loginMsgMgr.sendResRegisterg(player, 0);
		
	}
	/**
	 * 游客注册
	 * @param device
	 * @param deviceId
	 * @param deviceModel
	 */
	public void touristRegister(Player player,int device, String deviceId, String deviceModel) {
		String mac=RandomKeyGenerator.generate(10);
		// 根据用户名查找玩家，先在活跃账号缓存找，找不到再去数据库找  
		PlayerDom playerDom = playerMgr.selectByTouristMac(mac);
		if (playerDom == null) {
			String userName = "游客" + IdGenerator.nextId()+RandomKeyGenerator.generate(7);
			String pwd = "" + System.currentTimeMillis();
			String packageId="0";
			player.mac=mac;
			playerDom = doRegister(player, userName, pwd, true, "游客", 1, 0, packageId, 0, deviceId,false);
			ResTouristRegister.Builder msg =ResTouristRegister.newBuilder();
			msg.setMac(mac);
			CommonMessage.Builder commonMessage =CommonMessage.newBuilder();
			commonMessage.setId(MsgHandlerFactory.getProtocol("login.ResTouristRegister"));
			commonMessage.setContent(msg.build().toByteString());
			playerMsgMgr.writeMsg(player, commonMessage.build());
		}
		
	}

	
	/**
	 * 校验手机验证码
	 * 
	 * @param player
	 * @param code
	 */
	public boolean checkPhoneCode(Player player, String code)  {
		if (code == null || code.trim().length() == 0) {
			LOG.warn("会话{}玩家手机登录验证码为空", player.channel);
			return false;
		}
		
		Pair<String, Long> phoneCodePair = null;
		if (player.phone == null || (phoneCodePair = this.phoneCodes.get(player.phone)) == null) {
			LOG.warn("会话{}玩家未获取验证码直接校验手机验证码", player.channel);
			return false;
		}
		
		long now = System.currentTimeMillis();
		// 手机登录验证码过期时间(秒)
		int loginPhoneCodeOvertime = 60 * 1000;
		if (now - phoneCodePair.v2 > loginPhoneCodeOvertime) {
			LOG.warn("会话{}玩家手机[{}]验证码已过期", player.channel, player.phone);
			//loginMsgMgr.sendResGetPhoneLoginCode(player, 2);
			return false;
		}
		
		if (!phoneCodePair.v1.equalsIgnoreCase(code)) {
			player.phoneCodeFailureNum++;
			// 手机验证码失败超过最大次数
			int phoneCodeMaxFailureNum = 5;
			// 手机验证码是否正确
			if (player.phoneCodeFailureNum >= phoneCodeMaxFailureNum) {
				//loginMsgMgr.sendResGetPhoneLoginCode(player, 3);
			} else {
				//loginMsgMgr.sendResGetPhoneLoginCode(player, 4);
			}
			LOG.warn("会话{}玩家手机[{}]验证码错误", player.channel, player.phone);
			return false;
		}
		
		phoneCodes.remove(player.phone);
		return true;
	}
	/**
	 * 刷新手机验证码
	 * 
	 * @param player
	 * @param phone
	 */
	public void refreshPhoneCode(Player player, String phone) {
		
		if(phone==null&&player.getPhone()==null) {
			loginMsgMgr.sendResGetPhoneLoginCode(player, 2);
			LOG.warn("会话{}玩家获取验证码的手机[{}]格式不正确", player.channel, phone);
			return;
		}
		if (!phone.matches(PHONE_PATTERN)) {
			loginMsgMgr.sendResGetPhoneLoginCode(player, 1);
			LOG.warn("会话{}玩家获取验证码的手机[{}]格式不正确", player.channel, phone);
			return;
		}
		doRefreshPhoneCode(player, phone);
	}
	/**
	 * 刷新手机验证码
	 * 
	 * @param player
	 * @param phone
	 * @param seq
	 */
	private void doRefreshPhoneCode(Player player, String phone) {
		long now = System.currentTimeMillis();
		// 手验证码过期时间(毫秒)
		int phoneCodeOvertime = 60 * 1000;
		Pair<String, Long> phoneCodePair = this.phoneCodes.get(phone);
		String phoneCode = null;
		if (phoneCodePair != null && now - phoneCodePair.v2 < phoneCodeOvertime) {
			phoneCode = phoneCodePair.v1;
		} else {
			phoneCode = RandomKeyGenerator.generateNum(6);
			this.phoneCodes.put(phone, new Pair<>(phoneCode, now));
		}
		
		player.phone = phone;
		player.phoneCodeFailureNum = 0;
		
		// 调用后台web发送短信
		 boolean isSend=MsgUtil.sendMsg(phone, phoneCode);
			LOG.info("玩家{}手机{}获取验证码{}发送{}", player.getNickName(),phone,phoneCode,isSend);
		loginMsgMgr.sendResGetPhoneLoginCode(player, 0);
		LOG.info("会话{}玩家通过手机[{}]获取验证码[{}]", player.channel, phone, phoneCode);
	}
}
