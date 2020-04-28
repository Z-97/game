package com.alex.game.server;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alex.game.common.Game;
import com.alex.game.common.IpTable;
import com.alex.game.event.manager.EventMgr;
import com.alex.game.event.struct.net.ChannelCloseEvent;
import com.alex.game.event.struct.net.ChannelConnectEvent;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.struct.Player;
import com.alex.game.protocol.Protocol;
import com.alex.game.server.tcp.MsgHandlerFactory;
import com.alex.game.server.tcp.ReqMsgTask;
import com.alibaba.fastjson.JSON;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.protobuf.InvalidProtocolBufferException;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

/**
 * 游戏服务器
 * 
 * @author Alex 2015年7月25日 下午5:14:49
 *
 */
@Singleton
public class GameServer extends Thread {
	private static final Logger LOG = LoggerFactory.getLogger(GameServer.class);

	// 监听的端口
	private final int port;
	// 是否开启haProxy代理
	private final boolean haProxy;
	// 大小为监听的端口数目
	private final EventLoopGroup accepterGroup;
	// work线程
	private final EventLoopGroup workGroup;
	// 是否停服，停服后不再接受消息处理
	private boolean shutdown = false;
	@Inject
	private EventMgr eventMgr;
	@Inject
	private ExecutorMgr executorMgr;
	@Inject
	private IpTable ipTable;

	@Inject
	public GameServer(@Named("server.tcpPort") int port, @Named("haProxy") boolean haProxy) {
		super("GameServer");
		this.port = port;
		this.haProxy = haProxy;
		this.accepterGroup = new NioEventLoopGroup(2, (Runnable r) -> new Thread(r, "GameServer-Accepter"));
		this.workGroup = new NioEventLoopGroup(64, (Runnable r) -> new Thread(r, "GameServer-Worker"));
	}

	@Override
	public void run() {
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(accepterGroup, workGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							// ch.pipeline().addLast("log",new LoggingHandler(LogLevel.INFO));
//            				if (haProxy) ch.pipeline().addLast(new HAProxyMessageDecoder());
//            				ch.pipeline().addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
//            				ch.pipeline().addLast("protobufDecoder",
//            						new ProtobufDecoder(CommonMessage.getDefaultInstance()));
//            				// 用于在序列化的字节数组前加上一个简单的包头，只包含序列化的字节长度。
//            				ch.pipeline().addLast("frameEncoder",
//            						new ProtobufVarint32LengthFieldPrepender());
//            				//配置Protobuf编码器，发送的消息会先经过编码
//            				ch.pipeline().addLast("protobufEncoder", new ProtobufEncoder());
							// HttpServerCodec: 针对http协议进行编解码

							ch.pipeline().addLast("httpServerCodec", new HttpServerCodec());
							ch.pipeline().addLast("httpObjectAggregator", new HttpObjectAggregator(65536));
							ch.pipeline().addLast("chunkedWriteHandler", new ChunkedWriteHandler());
							// ch.pipeline().addLast("handshake", new WebSocketServerProtocolHandler("/ws",
							// "", true));
							// 用于处理websocket, /ws为访问websocket时的uri
							// ch.pipeline().addLast("webSocketServerProtocolHandler", new
							// WebSocketServerProtocolHandler("/ws"));
//            		        ch.pipeline().addLast(new ProtobufDecoder(CommonMessage.getDefaultInstance()));    //protbuf解码
//            		        ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());             //半包处理
//            		        ch.pipeline().addLast(new ProtobufMessageHandler());       
//            				ch.pipeline().addLast(new IdleStateHandler(15, 0, 0));

							// ch.pipeline().addLast(new InboundHandler());
							ch.pipeline().addLast(new WebSocketFrameHandler());
						}
					}).option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_KEEPALIVE, true)
					.option(ChannelOption.SO_BACKLOG, 8192).option(ChannelOption.SO_LINGER, 0)
					.option(ChannelOption.SO_RCVBUF, 1024 * 64).option(ChannelOption.SO_SNDBUF, 1024 * 64)
					.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
					.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
					.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000);

			Channel channel = b.bind(port).sync().channel();
			LOG.warn("监听Tcp端口[{}]成功", port);
			channel.closeFuture().sync();
		} catch (Exception e) {
			LOG.error("GameServer启动异常", e);
			System.exit(1);
		} finally {
			accepterGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}

	/**
	 * 关闭
	 */
	public void shutdown() {
		this.shutdown = true;
		accepterGroup.shutdownGracefully();
		workGroup.shutdownGracefully();
		LOG.info("GameServer关闭");
	}

	private class WebSocketFrameHandler extends ChannelInboundHandlerAdapter {
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			if (msg instanceof FullHttpRequest) {// 如果是HTTP请求，进行HTTP操作
				handleHttpRequest(ctx, (FullHttpRequest) msg);
			} else if (msg instanceof WebSocketFrame) {// 如果是Websocket请求，则进行websocket操作
				handleWebSocketFrame(ctx, (WebSocketFrame) msg);

			}
			ReferenceCountUtil.release(msg);
		}

		private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
			if (frame instanceof TextWebSocketFrame) {
				// 返回应答消息
				String requestmsg = ((TextWebSocketFrame) frame).text();
				ctx.writeAndFlush("serever:" + requestmsg);
			}
			if (frame instanceof CloseWebSocketFrame) {
				LOG.info("CloseWebSocketFrame消息======" + frame);
			}
			if (frame instanceof PingWebSocketFrame) {
				LOG.info("PingWebSocketFrame消息======" + frame);
			}
			Channel channel = ctx.channel();
			if (frame instanceof BinaryWebSocketFrame) {
				BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame) frame;
				ByteBuf buf = binaryWebSocketFrame.content();
				byte[] req = new byte[buf.readableBytes()];
				buf.readBytes(req);
				try {
					CommonMessage commonMessage = CommonMessage.parseFrom(req);
					dispatch(channel.attr(Player.PLAYER_KEY).get(), commonMessage);
				} catch (InvalidProtocolBufferException e) {

				}
			}

		}

		private WebSocketServerHandshaker handshaker;

		/*
		 * 功能：处理HTTP的代码
		 */
		private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request)
				throws UnsupportedEncodingException {
			// 如果HTTP解码失败，返回HHTP异常
			if (!request.decoderResult().isSuccess() || (!"websocket".equals(request.headers().get("Upgrade")))) {
				LOG.warn("HTTP解码失败:[{}]");
				return;
			}

			// 正常WebSocket的Http连接请求，构造握手响应返回
			WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
					"ws://" + request.headers().get(HttpHeaderNames.HOST), null, false);
			handshaker = wsFactory.newHandshaker(request);
			if (handshaker == null) { // 无法处理的websocket版本
				LOG.warn("无法处理的websocket版本");
				WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
			} else { // 向客户端发送websocket握手,完成握手
				handshaker.handshake(ctx.channel(), request);
				// 记录管道处理上下文，便于服务器推送数据到客户端
				LOG.info("向客户端发送websocket握手,完成握手");
			}

		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			super.channelActive(ctx);
			Channel channel = ctx.channel();
			String ip = ((InetSocketAddress) channel.remoteAddress()).getHostString();
			// 需要替换敏感ip
			ip = ipTable.replaceSensitiveIp(ip);
			Player player = new Player(channel, ip);
			channel.attr(Player.PLAYER_KEY).set(player);
			LOG.info("建立连接成功:[{}]", channel);
			eventMgr.post(new ChannelConnectEvent(player));
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			super.channelInactive(ctx);
			LOG.info("断开连接成功:[{}]", ctx.channel());
			Player player = ctx.channel().attr(Player.PLAYER_KEY).get();
			if (player != null && player.dom() != null) {
				eventMgr.post(new ChannelCloseEvent(player));
			}

		}

		/**
		 * 分发网络消息到指定线程执行
		 *
		 * @param player
		 * @param msg
		 */
		private void dispatch(Player player, CommonMessage msg) {
			// 消息模块id
			int moduleId = msg.getId() / 100;
			if (msg.getId() == 100150) {// 心跳直接返回,不分发到登陆线程(人很多的时候是负担)
				player.channel.write(getPONGMSG());
			} else if (moduleId == MsgHandlerFactory.getModuleId("login")) {// 登录消息在登录线程中执行
				executorMgr.getLoginExecutor(player.channel).execute(new ReqMsgTask(player, msg));
			} else if (moduleId == MsgHandlerFactory.getModuleId("bank")
					|| moduleId == MsgHandlerFactory.getModuleId("customer")
					|| moduleId == MsgHandlerFactory.getModuleId("email")
					|| moduleId == MsgHandlerFactory.getModuleId("player")
					|| moduleId == MsgHandlerFactory.getModuleId("rank")
					|| moduleId == MsgHandlerFactory.getModuleId("redpackage")) {// 广场消息(邮件、消息、公告、银行、背包、商城等)
				executorMgr.getPlazaExecutor(player.getId()).execute(new ReqMsgTask(player, msg));
			} else if (moduleId == MsgHandlerFactory.getModuleId(Game.DICE.moduleId)
					|| moduleId == MsgHandlerFactory.getModuleId(Game.GOBANG.moduleId)) {// 游戏消息(扎金花、斗地主、捕鱼等),根据桌子分发
				// 根据游戏分发
				executorMgr.getGameExecutor(moduleId).execute(new ReqMsgTask(player, msg));
			} else {
				LOG.warn("消息id[{}]无法找到对应的executor,请按规则定义", msg.getId());
				player.channel.close();
			}
		}
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			LOG.warn("会话[{}]发生异常[{}]", ctx.channel(), cause.getMessage());
			ctx.close();
		}

		@Override
		public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
			super.userEventTriggered(ctx, evt);
			// 心跳处理
			if (evt instanceof IdleStateEvent) {
				IdleStateEvent event = (IdleStateEvent) evt;
				if (event.state() == IdleState.READER_IDLE) {// 读超时
					LOG.warn("{}idle timeout close channel", ctx.channel());
					ctx.close();
				}
			}
		}

	}

	private class InboundHandler extends ChannelInboundHandlerAdapter {
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			super.channelRead(ctx, msg);
			if (shutdown) {
				return;
			}

			Channel channel = ctx.channel();
			if (msg instanceof CommonMessage) {
				// 分发消息
				dispatch(channel.attr(Player.PLAYER_KEY).get(), (CommonMessage) msg);
			} else if (msg instanceof HAProxyMessage) {// 代理消息
				HAProxyMessage haProxyMsg = (HAProxyMessage) msg;
				Field remoteAddressField = AbstractChannel.class.getDeclaredField("remoteAddress");
				remoteAddressField.setAccessible(true);
				// 需要替换敏感ip
				String ip = ipTable.replaceSensitiveIp(haProxyMsg.sourceAddress());
				remoteAddressField.set(channel, new InetSocketAddress(ip, haProxyMsg.sourcePort()));
				// 改变玩家的ip
				channel.attr(Player.PLAYER_KEY).get().ip = ip;
			} else {
				LOG.warn("会话[{}]消息[{}]不是确定格式消息", channel, JSON.toJSONString(msg));
				ctx.close();
			}
		}

		/**
		 * 分发网络消息到指定线程执行
		 *
		 * @param player
		 * @param msg
		 */
		private void dispatch(Player player, CommonMessage msg) {
			// 消息模块id
			int moduleId = msg.getId() / 100;
			LOG.warn("消息moduleId[{}]", moduleId);
			if (msg.getId() == 100150) {// 心跳直接返回,不分发到登陆线程(人很多的时候是负担)
				player.channel.write(getPONGMSG());
			} else if (moduleId == MsgHandlerFactory.getModuleId("login")) {// 登录消息在登录线程中执行
				executorMgr.getLoginExecutor(player.channel).execute(new ReqMsgTask(player, msg));
			} else if (moduleId >= 101 && moduleId <= 500) {// 广场消息(邮件、消息、公告、银行、背包、商城等)
				executorMgr.getPlazaExecutor(player.getId()).execute(new ReqMsgTask(player, msg));
			} else if (moduleId >= 501 && moduleId <= 999) {// 游戏消息(扎金花、斗地主、捕鱼等),根据桌子分发
				// 非捕鱼根据游戏分发
				executorMgr.getGameExecutor(moduleId).execute(new ReqMsgTask(player, msg));

			} else {
				LOG.warn("消息id[{}]无法找到对应的executor,请按规则定义", msg.getId());
				player.channel.close();
			}
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			super.channelActive(ctx);
			Channel channel = ctx.channel();
			String ip = ((InetSocketAddress) channel.remoteAddress()).getHostString();
			// 需要替换敏感ip
			ip = ipTable.replaceSensitiveIp(ip);
			Player player = new Player(channel, ip);
			channel.attr(Player.PLAYER_KEY).set(player);
			LOG.info("建立连接成功:[{}]", channel);
			eventMgr.post(new ChannelConnectEvent(player));
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			super.channelInactive(ctx);
			LOG.info("断开连接成功:[{}]", ctx.channel());
			Player player = ctx.channel().attr(Player.PLAYER_KEY).get();
			if (player != null && player.dom() != null) {
				eventMgr.post(new ChannelCloseEvent(player));
			}
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			LOG.warn("会话[{}]发生异常[{}]", ctx.channel(), cause.getMessage());
			ctx.close();
		}

		@Override
		public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
			super.userEventTriggered(ctx, evt);
			// 心跳处理
			if (evt instanceof IdleStateEvent) {
				IdleStateEvent event = (IdleStateEvent) evt;
				if (event.state() == IdleState.READER_IDLE) {// 读超时
					LOG.warn("{}idle timeout close channel", ctx.channel());
					ctx.close();
				}
			}
		}
	}

	/**
	 * 返回心跳
	 * 
	 * @return
	 */
	public CommonMessage getPONGMSG() {
		CommonMessage.Builder commonMessage = CommonMessage.newBuilder();
		commonMessage.setId(Protocol.ResPong);
		return commonMessage.build();
	}

}
