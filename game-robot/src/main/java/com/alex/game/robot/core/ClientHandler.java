package com.alex.game.robot.core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.robot.handler.Ihandler;
import com.alex.game.robot.handler.MsgHandlerFactory;
import com.alibaba.fastjson.JSON;
import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.util.CharsetUtil;

/**
 * 机器人消息处理器
 * 
 * @author Alex
 * @date 2017年4月19日 下午1:45:17ChannelInboundHandlerAdapter
 */
public class ClientHandler extends SimpleChannelInboundHandler<Object> {

	private static final Logger LOG = LoggerFactory.getLogger(ClientHandler.class);
	WebSocketClientHandshaker handshaker;
    ChannelPromise handshakeFuture;
    public void handlerAdded(ChannelHandlerContext ctx) {
        this.handshakeFuture = ctx.newPromise();
    }
    public WebSocketClientHandshaker getHandshaker() {
        return handshaker;
    }

    public void setHandshaker(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    public ChannelPromise getHandshakeFuture() {
        return handshakeFuture;
    }

    public void setHandshakeFuture(ChannelPromise handshakeFuture) {
        this.handshakeFuture = handshakeFuture;
    }

    public ChannelFuture handshakeFuture() {
        return this.handshakeFuture;
    }

    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        FullHttpResponse response;
        if (!this.handshaker.isHandshakeComplete()) {
            try {
                response = (FullHttpResponse)msg;
                //握手协议返回，设置结束握手
                this.handshaker.finishHandshake(ch, response);
                //设置成功
                this.handshakeFuture.setSuccess();
                //LOG.info("WebSocket Client connected! response headers[sec-websocket-extensions]:{}"+response.headers());
            } catch (WebSocketHandshakeException var7) {
                FullHttpResponse res = (FullHttpResponse)msg;
                String errorMsg = String.format("WebSocket Client failed to connect,status:%s,reason:%s", res.status(), res.content().toString(CharsetUtil.UTF_8));
                this.handshakeFuture.setFailure(new Exception(errorMsg));
            }
        } else if (msg instanceof FullHttpResponse) {
            response = (FullHttpResponse)msg;
            //this.listener.onFail(response.status().code(), response.content().toString(CharsetUtil.UTF_8));
            throw new IllegalStateException("Unexpected FullHttpResponse (getStatus=" + response.status() + ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        } else {
            WebSocketFrame frame = (WebSocketFrame)msg;
            if (frame instanceof TextWebSocketFrame) {
               
            } else if (frame instanceof BinaryWebSocketFrame) {
                BinaryWebSocketFrame binFrame = (BinaryWebSocketFrame)frame;
                //消息
                ByteBuf buf = binFrame.content();
				byte[] req = new byte[buf.readableBytes()];
				buf.readBytes(req);
        		//消息
				CommonMessage commonMessage = CommonMessage.parseFrom(req);
        		int msgId = commonMessage.getId();
        		//机器人玩家
        		Player player = ctx.channel().attr(Player.PLAYER_KEY).get();
        		
        		try {
        			player.execute(() -> {
						try {
							Ihandler ihandler =MsgHandlerFactory.getHandler(msgId);
							if(ihandler!=null) {
								ihandler.action(player, commonMessage);
							}else{
								//LOG.error("协议[" + msgId + "]" + commonMessage + "找不到实现类");
							}
									
						} catch (InvalidProtocolBufferException e) {
							
							LOG.error("会话[" + ctx.channel() + "]机器人消息[" + JSON.toJSONString(commonMessage) + "]执行异常[" + e.getMessage() + "]", e);
						}
					});
        		} catch (Exception e) {
        			LOG.error("会话[" + ctx.channel() + "]机器人消息[" + JSON.toJSONString(commonMessage) + "]执行异常[" + e.getMessage() + "]", e);
        		}
            } else if (frame instanceof PongWebSocketFrame) {
                System.out.println("WebSocket Client received pong");
            } else if (frame instanceof CloseWebSocketFrame) {
                System.out.println("receive close frame");
                //this.listener.onClose(((CloseWebSocketFrame)frame).statusCode(), ((CloseWebSocketFrame)frame).reasonText());
                ch.close();
            }

        }
    }
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		super.userEventTriggered(ctx, evt);
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
        ctx.close();
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
	}
}
