/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.login.server.http;

import java.util.List;

/**
 * HttpService接口
 * 
 * @author Alex
 * @date 2016年12月18日 下午1:56:29
 */
public interface HttpService {

	/**
	 * service名称
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * http服务方法
	 * 
	 * @param args
	 * @return 服务返回
	 */
	public HttpServiceRes service(List<String> args);
}
