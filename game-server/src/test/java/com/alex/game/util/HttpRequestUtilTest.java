package com.alex.game.util;

import java.util.concurrent.ExecutionException;

import org.apache.http.entity.ContentType;
import org.junit.Test;

/**
 * HttpRequestUtilTest
 * 
 * @author Alex
 * @date 2017年4月7日 上午12:47:33
 */
public class HttpRequestUtilTest {

	@Test
	public void testServer() throws InterruptedException, ExecutionException {
		System.out.println(HttpRequestUtil.getAsync("http://172.16.10.120:8080/server", ContentType.MULTIPART_FORM_DATA).get().asString());
	}
	
	@Test
	public void testBaidu() throws InterruptedException, ExecutionException {
		System.out.println(HttpRequestUtil.getAsync("http://www.baidu.com/", ContentType.MULTIPART_FORM_DATA).get().asString());
	}
	@Test
	public void testcallExchange(){
		
	}
}
