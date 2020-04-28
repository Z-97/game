/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.message;
import java.util.HashMap;
import java.util.Map;
import org.apache.ibatis.io.ResolverUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alex.game.message.CommonMessageProto.CommonMessage;

/**
 * 消息工厂
 *
 * @author Alex
 * @date 2016/7/7 11:31
 */
public class MessageFactory {

    private static final Logger LOG = LoggerFactory.getLogger(MessageFactory.class);

    // 扫描消息的包名
    private static final String MSG_PKG_NAME = "com.alex.game";
    // 消息类型
    private static final Map<Integer, CommonMessage> MSG_TYPES = new HashMap<>();

    private MessageFactory() {
    }

    /**
     * 注册消息，扫描package
     */
    public static void registerAllMsgs() {

    }

	


}
