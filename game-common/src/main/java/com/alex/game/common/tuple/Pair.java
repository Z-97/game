/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.common.tuple;

/**
 * 二元祖封装
 *
 * @author Alex
 * @date 2016/7/6 10:52
 */
public class Pair<A, B> {
    public final A v1;
    public final B v2;

    public Pair(A v1, B v2) {
        this.v1 = v1;
        this.v2 = v2;
    }
}
