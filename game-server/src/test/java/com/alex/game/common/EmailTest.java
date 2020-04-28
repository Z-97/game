package com.alex.game.common;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * 
 * @author Alex
 * @date 2017年6月6日 下午8:40:17
 */
public class EmailTest {

	@Test
	public void testEmail() {
//		"^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$"  
		String EMAIL_PATTERN = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";		
		
		Assert.assertTrue("lsqowieidkxl;dpdlkdldkdjdjdjdjdjdjdjdjdjdjdjdj1".matches(EMAIL_PATTERN));
		
	}
}
