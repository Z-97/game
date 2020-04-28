/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.hub.server.http;

import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alex.game.dblog.core.DbLogService;
import com.alex.game.dblog.httpservice.HttpServiceLog;
import com.alibaba.fastjson.JSON;
import com.google.inject.Singleton;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;

/**
 * http处理
 * 
 * @author Alex
 * @date 2016年12月13日 下午5:02:45
 */
@Singleton
@Sharable
public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	private static final Logger HTTP_SERVICE_LOG = LoggerFactory.getLogger("HttpServiceLog");

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
		Channel channel = ctx.channel();
		// Handle a bad request.
		if (!req.decoderResult().isSuccess()) {
			HTTP_SERVICE_LOG.error("http会话[{}]错误的请求", channel);
			channel.close();
			return;
		}

		if (req.method() != POST) {
			HTTP_SERVICE_LOG.warn("http会话[{}]请求的方法不是POST", channel);
			channel.close();
			return;
		}

		String ip = ((InetSocketAddress) ctx.channel().remoteAddress()).getHostString();
		// 请求内容
		String contentText = req.content().toString(StandardCharsets.UTF_8);
		HttpServiceRes serviceRes = null;
		try {
			// HttpService请求
			HttpServiceReq serviceReq = JSON.parseObject(contentText, HttpServiceReq.class);
			String serviceName = serviceReq.getService();
			// HttpService返回,HttpService直接在netty网络线程中执行
			HttpService service = HttpServiceFactory.getService(serviceName);
			if (service == null) {
				HTTP_SERVICE_LOG.warn("IP[{}]发送的请求命令[{}]不存在", ip, serviceName);
				serviceRes = HttpServiceRes.failure("命令:" + serviceName + "不存在");
			} else {
				serviceRes = service.service(serviceReq.getArgs());
			}
		} catch (Exception e) {
			serviceRes = HttpServiceRes.failure("命令[" + contentText + "]执行异常:" + e.getMessage());
		}
		
		String serviceResJson = JSON.toJSONString(serviceRes);
		HTTP_SERVICE_LOG.info("收到IP[{}]httpservice请求[{}]返回[{}]", ip, contentText, serviceResJson);
		DbLogService.log(new HttpServiceLog(ip, contentText, serviceResJson));
		
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK,
				Unpooled.wrappedBuffer(serviceResJson.getBytes(StandardCharsets.UTF_8)));
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
		response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
		ctx.write(response);
		ctx.flush();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		HTTP_SERVICE_LOG.info("建立http连接:[{}]", ctx.channel());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		HTTP_SERVICE_LOG.info("断开http连接:[{}]", ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		HTTP_SERVICE_LOG.error("http会话[{}]异常[{}]", ctx.channel(), cause.getMessage());
		ctx.close();
	}
}
