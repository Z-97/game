package com.alex.game.server.tcp;

import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.struct.Player;
import com.google.protobuf.InvalidProtocolBufferException;
/**
 * 消息处理接口
 * @author yejuhua
 *
 */
public interface Ihandler {

	/**
     * 消息处理
     * @param player
     * @param msg
	 * @throws InvalidProtocolBufferException 
     */
    void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException;
}
