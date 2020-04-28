package com.alex.game.common;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.alex.game.server.ApplicationContext;

/**
 * IpTableTest
 * 
 * @author Alex
 * @date 2017年5月24日 下午4:09:15
 */
public class IpTableTest {

	private ApplicationContext app = null;
	
	@Before
	public void Before() {
		this.app = ApplicationContext.createInstance();
	}
	
	@Test
	public void testIpTable() {
		IpTable ipTable = app.getBean(IpTable.class);
		
		// 测试香港ip
		String hongKongIp = "203.198.69.78";
		System.out.println(ipTable.replaceSensitiveIp(hongKongIp));
		
		// 柬埔寨ip
		String jpzIp = "117.20.118.43";
		System.out.println(ipTable.replaceSensitiveIp(jpzIp));
		
		// 测试中国ip
		String chinaIp = "61.139.2.69";
		System.out.println(ipTable.replaceSensitiveIp(chinaIp));
		System.out.println(Arrays.toString(ipTable.find("210.39.64.195")));
	}
}
