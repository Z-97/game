/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */
package com.alex.game.server.http;

import java.util.List;

/**
 * HttpServic请求内容
 * 
 * @author Alex
 * @date 2015年8月25日 上午11:20:51
 */
public class HttpServiceReq {
	// service名称
	private String service;
	// service参数
	private List<String> args;

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public List<String> getArgs() {
		return args;
	}

	public void setArgs(List<String> args) {
		this.args = args;
	}

}
