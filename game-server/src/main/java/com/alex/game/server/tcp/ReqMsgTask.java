/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.server.tcp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.struct.Player;
import com.alex.game.server.filter.FilterChain;

/**
 * 消息处理task
 *
 * @author Alex
 * @date 2016/8/8 17:22
 */
public class ReqMsgTask implements Runnable {
	// 消息处理超时日志
    private static final Logger MSG_HANDLE_OVERTIME_LOG = LoggerFactory.getLogger("MsgHandleOvertimeLog");
    // 消息异常日志
    private static final Logger MSG_HANDLE_EXCEPTION_LOG = LoggerFactory.getLogger("MsgHandleExceptionLog");

    private final Player player;
    private final CommonMessage msg;

    public ReqMsgTask(Player player, CommonMessage msg) {
        this.player = player;
        this.msg = msg;
    }

    @Override
    public void run() {
        try {
        	if (!FilterChain.doFilter(player, msg)) {
                return;
            }
            long now = System.currentTimeMillis();
            MsgHandlerFactory.getHandler(msg.getId()).action(player, msg);
            long duration = System.currentTimeMillis() - now;
            // 超过10毫秒的操作
            if (duration > 10) {
            	MSG_HANDLE_OVERTIME_LOG.warn("消息[{}]处理时间[{}]", msg.getId(), duration);
            }
        } catch (Exception e) {
        	if (player.dom() != null) {
        		MSG_HANDLE_EXCEPTION_LOG.error("玩家[" + player.getId() + "]会话[ " + player.channel + "]请求消息[" + msg + "]执行异常", e);
			} else {
				MSG_HANDLE_EXCEPTION_LOG.error("会话[ " + player.channel + "]请求消息[" + msg + "]执行异常", e);
			}
        }
    }
}
