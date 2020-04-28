/**
 * Copyright (c) 2015, Alexd.
 * 
 */  
package com.alex.game.common.util;

/**  
 * @author Alexte 2015年10月22日 下午2:43:32  
 *
 */
public class RandomKeyGenerator {

	private static final char[] KEY_CHARS = { 'a', 'b', 'C', 'D', 'E', 'F', 'G', 'h', 'i', 'j',
			'k', 'L', 'm', 'N', 'o', 'P', 'q', 'r', 's', 't', 'U', 'V', 'W',
			'X', 'Y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' }; 
	
	private static final char[] NUM_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9' }; 
	private RandomKeyGenerator() {}
	
	/**
	 * 生成校验码
	 * @pa度
	 * @return
	 */
	public static String generate(int len) {
		char[] data = new char[len];
		for (int i = 0; i < len; i++) {
			data[i] = KEY_CHARS[RandomUtil.random(KEY_CHARS.length)];
		}
		
		return String.valueOf(data);
	}
	
	/**
	 * 生成校验码
	 * @param len		校验码长
	 */
	public static String generateNum(int len) {
		char[] data = new char[len];
		for (int i = 0; i < len; i++) {
			data[i] = NUM_CHARS[RandomUtil.random(NUM_CHARS.length)];
		}
		
		return String.valueOf(data);
	}

}
