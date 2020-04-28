/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.login.struct;

/**
 * 退出登录类型
 *
 * @author Alex
 * @date 2016/8/5 17:17
 */
public enum LogoutType {
    NORMAL(0),         // 正常退出
    TICK(1),           // 同一个账号重复登录被T号退出
    LOCK(2),           // 玩家被冻结退出
    CLEAR(3),          // 玩家被清除游戏退出
    SHUT_DOWN(4);      // 停服玩家T出

    public int val;

    LogoutType(int val) {
        this.val = val;
    }
}
