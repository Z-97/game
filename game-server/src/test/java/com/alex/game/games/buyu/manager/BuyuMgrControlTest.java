package com.alex.game.games.buyu.manager;

import org.junit.Test;

import com.alex.game.common.util.RandomUtil;

/**
 * 
 * 
 * @author Alex
 * @date 2017年7月7日 下午2:52:10
 */
public class BuyuMgrControlTest {

	private static final int TEST_COUNT = 1000;
	private static final int TEST_PARAM = 20000;
	
	@Test
	public void test() {
		for (int i = 0; i < TEST_COUNT; i++) {
			System.out.println(RandomUtil.random(TEST_PARAM));
		}
	}
}
