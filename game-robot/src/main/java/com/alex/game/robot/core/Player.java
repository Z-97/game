package com.alex.game.robot.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alex.game.common.tuple.Pair;
import com.alex.game.common.util.RandomKeyGenerator;
import com.alex.game.common.util.RandomUtil;
import com.alex.game.core.concurrent.TaskExecutor;
import com.alex.game.login.LoginProto.ReqLogin;
import com.alex.game.login.LoginProto.ReqLogout;
import com.alex.game.login.LoginProto.ReqTouristLogin;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.PlayerProto.PlayerPB;
import com.alex.game.robot.common.PlayerPosition;
import com.alex.game.robot.handler.MsgHandlerFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.util.AttributeKey;

/**
 * 机器人玩家
 * 
 * @author Alex
 * @date 2016/7/26 20:59
 */
public abstract class Player {
    protected final Logger LOG = LoggerFactory.getLogger(getClass());
    // 测试游客mac
    public static final String TEST_TOURIST_MAC = "4AB9410A-4485-47AF-A417-D8B92DF3208X";
    		
    public static final AttributeKey<Player> PLAYER_KEY = AttributeKey.valueOf("player");
    // 机器人玩家网络线程数量
//    public static final int EVENT_LOOP_THREADS = Runtime.getRuntime().availableProcessors() * 2 + 1;
    public static final int EVENT_LOOP_THREADS = 32;
    // 机器人网络线程组
    public static final EventLoopGroup EVENT_LOOP_GROUP = new NioEventLoopGroup(EVENT_LOOP_THREADS);
    
    private static final String API_PARAMS;
    
    static {
    	JSONObject apiParams = new JSONObject();
    	apiParams.put("pkg_id", "I000000");
    	apiParams.put("pkg_version", "0.0.1");
    	API_PARAMS = apiParams.toJSONString();
    }
    
    // 玩家id
    public final long id;
    // 用户名
    public final String userName;
    // 网络客户端
    public final Client client = new Client();
    private final TaskExecutor taskExecutor;
	// 进入的房间
	public int roomId;
    // 玩家位置
    public PlayerPosition position;
    // 玩家数据信息
    public PlayerPB data;
    // 发送登陆消息时间
    public long sendLoginMsgTime = 0;
    private final String apiUrl;
    
    public Player(long playerId, String userName, TaskExecutor taskExecutor, String apiUrl) {
		this.id = playerId;
		this.userName = userName;
		this.taskExecutor = taskExecutor;
		this.apiUrl = apiUrl;
	}

	/**
     * 发送消息
     * @param msg
     */
    public void sendMsg(CommonMessage msg) {
        client.sendMsg(msg);
    }

    public void sendMsg(int protocol,ByteString bytes) {
    	CommonMessage.Builder msg=CommonMessage.newBuilder();
    	msg.setId(protocol);
    	msg.setContent(bytes);
    	client.sendMsg(msg.build());
    }
    /**
     * 游客登陆
     */
    public void touristLogin(String ip, int port, String mac, boolean check) {
    	
    	this.sendLoginMsgTime = System.currentTimeMillis();
    	if (check) {
    		Pair<String, Integer> slb = callGetSlb();
    		if (slb != null) {
				ip = slb.v1;
				port = slb.v2;
			}
		}
   
        client.connect(ip, port, () -> {
        	ReqTouristLogin.Builder msg = ReqTouristLogin.newBuilder();
        	msg.setMac(mac);
            msg.setDevice(0);
            msg.setDeviceModel("xiaomi4");
            msg.setDeviceId(RandomKeyGenerator.generate(10));
            msg.setPackageId("111");
            sendMsg(MsgHandlerFactory.getProtocol("login.ReqTouristLogin"),msg.build().toByteString());
        });
    }
    
    /**
     * 普通登陆
     * 
     * @param ip
     * @param port
     * @param userName
     * @param pwd
     * @param mac
     */
    public void login(String ip, int port, String pwd, String mac, boolean check) {
  
    	this.sendLoginMsgTime = System.currentTimeMillis();
    	if (check) {
    		Pair<String, Integer> slb = callGetSlb();
    		if (slb != null) {
				ip = slb.v1;
				port = slb.v2;
			}
		}
        client.connect(ip, port, () -> {
        	ReqLogin.Builder msg = ReqLogin.newBuilder();
        	msg.setDevice(0);
        	msg.setDeviceModel("xiaomi4");
        	msg.setMac(mac);
        	msg.setPwd(pwd);
        	msg.setUserName(userName);
        	msg.setDeviceId("DeviceId");
        	 System.out.println("login");
            sendMsg(MsgHandlerFactory.getProtocol("login.ReqLogin"),msg.build().toByteString());
        });
    }
    
	/**
	 * 调用web获取slb的地址和ip
	 * @return
	 */
	public Pair<String, Integer> callGetSlb() {
	  	try {
    		String res = Request.Post(apiUrl).bodyString(API_PARAMS, ContentType.APPLICATION_JSON).connectTimeout(5000).socketTimeout(5000)
    				.addHeader(HTTP.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString()).execute().returnContent().asString(StandardCharsets.UTF_8);
    		JSONArray serverIpsArray = JSON.parseObject(res).getJSONArray("server");
    		JSONObject serverIpJson = serverIpsArray.getJSONObject(RandomUtil.random(serverIpsArray.size()));
    		String ip = serverIpJson.getString("ip");
    		int port = serverIpJson.getIntValue("port");
    	 	LOG.info("玩家[{}][{}]通过网关[{}]获取slb地址[{}][{}]成功", id, userName, apiUrl, ip, port);
    	 	return new Pair<String, Integer>(ip, port);
		} catch (Throwable e) {
			LOG.error("玩家[" + id + "]["  + userName + "]通过网关[" + apiUrl + "]获取slb地址失败,原因[" + e.getMessage() + "]", e);
			return null;
		}
	}
    
    /**
     * 登出
     */
    public void logout() {
    	ReqLogout.Builder resLogout=ReqLogout.newBuilder();
        sendMsg(MsgHandlerFactory.getProtocol("login.ReqLogout"),resLogout.build().toByteString());
        close();
    }
    
    /**
     * 延时登出
     */
    public void delayLogout() {
		schedule(this::logout, RandomUtil.random(1, 10), TimeUnit.SECONDS);
	}

    /**
     * 关闭客户端
     */
    public void close() {
    	Channel channel = client.channel;
        if (channel != null && channel.isOpen()) {
        	channel.close();
        }
    }
    
	/**
	 * 清空数据
	 */
	public void clear() {
	}

    public ScheduledFuture<?> schedule(Runnable cmd, long delay, TimeUnit unit) {
    	
    	return client.channel.eventLoop().schedule(() -> taskExecutor.execute(cmd), delay, unit);
    }
    
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable cmd, long initDelay, long delay, TimeUnit unit) {
        return client.channel.eventLoop().scheduleWithFixedDelay(() -> taskExecutor.execute(cmd), initDelay, delay, unit);
    }
    
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable cmd, long initDelay, long period, TimeUnit unit) {
    	return client.channel.eventLoop().scheduleAtFixedRate(cmd, initDelay, period, unit);
    }
    
	/**
	 * 执行任务
	 * 
	 * @param cmd
	 */
	public void execute(Runnable cmd) {
		taskExecutor.execute(cmd);
	}
    
	/**
	 * 调度，实现该方法
	 */
	public abstract void schedule();
	
	/**
	 * 返回null,代表大厅机器人
	 */
	public abstract int game();
	
    /**
     * 机器人玩家客户端
     * 
     * @author Alex
     * @date 2017年4月19日 下午1:51:53
     */
    private class Client {
        private volatile boolean connected = false;
        private volatile Channel channel = null;

        /**
         * 连接服务器
         * @param ip
         * @param port
         * @param callBack        成功连接服务器后的回调函数
         */
        public void connect(String ip, int port, Runnable callBack) {
            if (connected) {
                LOG.warn("[" + id + "]该客户端已经连接服务器,请勿重复连接");
                return;
            }
//			EventLoopGroup group = new NioEventLoopGroup();
//			Bootstrap boot = new Bootstrap();
//			boot.option(ChannelOption.SO_KEEPALIVE, true).option(ChannelOption.TCP_NODELAY, true)
//					.option(ChannelOption.SO_BACKLOG, 1024 * 1024 * 10).group(group)
//					.handler(new LoggingHandler(LogLevel.INFO)).channel(NioSocketChannel.class)
            Bootstrap b = new Bootstrap()
                    .channel(NioSocketChannel.class)
                    .group(EVENT_LOOP_GROUP)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000)
					.handler(new ChannelInitializer<SocketChannel>() {
						protected void initChannel(SocketChannel socketChannel) throws Exception {
							ChannelPipeline p = socketChannel.pipeline();
							p.addLast(new ChannelHandler[] { new HttpClientCodec(),
									new HttpObjectAggregator(1024 * 1024 * 10) });
							p.addLast("hookedHandler", new ClientHandler());
						}
					});
	      
			try {
				URI websocketURI = new URI("ws://localhost:7000/ws");
				HttpHeaders httpHeaders = new DefaultHttpHeaders();
				// 进行握手
				WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(websocketURI,
						WebSocketVersion.V13, (String) null, true, httpHeaders);

				Channel channel;
				channel = b.connect(websocketURI.getHost(), websocketURI.getPort()).sync().channel();
				ClientHandler handler = (ClientHandler) channel.pipeline().get("hookedHandler");
				handler.setHandshaker(handshaker);
				handshaker.handshake(channel);
				// 阻塞等待是否握手成功
				ChannelFuture f =handler.handshakeFuture().sync();
				f.addListener((ChannelFuture future) -> {
                    if (future.isSuccess()) {
                        this.channel = future.channel();
                        this.channel.attr(Player.PLAYER_KEY).set(Player.this);
                        callBack.run();
                    } else {
                        LOG.error("机器人[" + userName + "]与服务器建立连接失败", future.cause());
                        ApplicationContext.getInstance().getBean(PlayerScheduler.class).removePlayer(Player.this);
                    }
                });
				connected = true;
			} catch (InterruptedException | URISyntaxException e) {
				LOG.error("机器人[" + userName + "]与服务器建立连接失败[{}]", userName, e.getCause());
            	ApplicationContext.getInstance().getBean(PlayerScheduler.class).removePlayer(Player.this);
			}
	        
          
	        LOG.info("机器人[" + userName + "]与服务器建立连接成功");
        }

        /**
         * 发送消息
         * @param msg
         */
        public void sendMsg(CommonMessage msg) {
            if (channel != null && channel.isActive()) {
            	ByteBuf result = Unpooled.buffer();
                result.writeBytes(msg.toByteArray());
            	BinaryWebSocketFrame frame=new BinaryWebSocketFrame(result);
            	channel.writeAndFlush(frame);
            }
        }
    }
}
