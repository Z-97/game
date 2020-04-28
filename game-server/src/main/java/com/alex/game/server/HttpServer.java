/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */
package com.alex.game.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alex.game.server.http.HttpHandler;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * 游戏HttpServer,处理http请求
 * 
 * @author Alex
 * @date 2017年4月12日 下午1:39:20
 */
public final class HttpServer extends Thread {

	private static final Logger LOG = LoggerFactory.getLogger(HttpServer.class);

	// http端口
	private final int port;
	// Http处理器
	private final HttpHandler handler;
	// 大小为监听的端口数目
	private final EventLoopGroup accepterGroup;
	// work 1个线程
	private final EventLoopGroup workGroup;

	@Inject
	public HttpServer(@Named("server.httpPort") int port, HttpHandler handler) {
		super("HttpServer");
		this.port = port;
		this.handler = handler;
		this.accepterGroup = new NioEventLoopGroup(1, (Runnable r) -> new Thread(r, "HttpServer-Accepter"));
		this.workGroup = new NioEventLoopGroup(1, (Runnable r) -> new Thread(r, "HttpServer-Worker"));
	}

	@Override
	public void run() {
		try {
			ServerBootstrap b = new ServerBootstrap().option(ChannelOption.SO_BACKLOG, 1024);
			b.group(accepterGroup, workGroup).childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new HttpServerCodec());
					ch.pipeline().addLast(new HttpObjectAggregator(65536));
					ch.pipeline().addLast(new HttpContentCompressor(1));
					ch.pipeline().addLast(handler);
				}                                                                                                                                                                  
			}).channel(NioServerSocketChannel.class);

			Channel ch = b.bind(port).sync().channel();
			LOG.warn("监听Http端口[{}]成功", port);
			ch.closeFuture().sync();
		} catch (Exception e) {
			LOG.error("HttpServer启动异常", e);
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
		accepterGroup.shutdownGracefully();
		workGroup.shutdownGracefully();
		LOG.info("HttpServer关闭");
	}

}
