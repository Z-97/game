/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.common.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Alex
 * @date 2016/8/3 16:17
 */
public class MD5UtilTest {

    @Test
    public void testMd5() {
        Assert.assertEquals("14F7C34EED9E04EF8556E7D52756B26E", MD5Util.encodeWithTail("123456"));
        Assert.assertEquals("E287D30B9FDCDFF120AA00880DEEA4F4", MD5Util.encodeWithTail("1"));
    }
}
