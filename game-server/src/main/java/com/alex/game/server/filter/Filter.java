/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.server.filter;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.struct.Player;

/**
 * 游戏消息过滤器，用于实现权限、黑名单、白名单等很方便
 *
 * @author Alex
 * @date 2016/8/8 17:55
 */
public interface Filter {

    /**
     * 消息过滤
     *
     * @param player
     * @param msg
     * @return
     */
    boolean doFilter(Player player, CommonMessage msg);

    /**
     * 过滤消息的模块id,0：所有模块都过滤
     *
     * @return
     */
    int getModuleId();
    
    /**
     * filter的优先级,越小越优先
     * 
     * @return
     */
    default int gePriority() {
    	return getModuleId();
    }
}
