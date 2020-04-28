/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.server.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.io.ResolverUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.struct.Player;
import com.alex.game.server.ApplicationContext;

/**
 * 过滤器链
 *
 * @author Alex
 * @date 2016/8/8 17:54
 */
public class FilterChain {

    private static final Logger LOG = LoggerFactory.getLogger(FilterChain.class);
    
    // 扫描filter的包名
    private static final String FILTER_PKG_NAME = "com.alex.game";
    // 过滤器
    public static final List<Filter> FILTERS = new ArrayList<>();

    /**
     * 扫描包注册所有的过滤器
     */
    public static void registerAllFilters() {
        ResolverUtil<Filter> filterResolver = new ResolverUtil<Filter>().find((type) -> Filter.class.isAssignableFrom(type) && type != Filter.class, FILTER_PKG_NAME);
        try {
            Set<Class<? extends Filter>> filterClasses = filterResolver.getClasses();
            for (Class<? extends Filter> filterClass : filterClasses) {
                Filter filter = filterClass.getDeclaredConstructor().newInstance();
                ApplicationContext.getInstance().injectMembers(filter);
                FILTERS.add(filter);
            }
            
            Collections.sort(FILTERS, (f1, f2) -> f1.gePriority() >= f2.gePriority()?1:-1);
        } catch (Exception e) {
            LOG.error("扫描[" + FILTER_PKG_NAME + "]Filter错误", e);
            System.exit(1);
        }
    }

    /**
     * 过滤消息
     *
     * @param player
     * @param msg
     * @return
     */
    public static boolean doFilter(Player player, CommonMessage msg) {
        for (int i = 0; i < FILTERS.size(); i++) {
            Filter filter = FILTERS.get(i);
            int moduleId = filter.getModuleId();
            if ((moduleId == 0 || moduleId == msg.getId()/100) && !filter.doFilter(player, msg)) {
                return false;
            }
        }

        return true;
    }
}
