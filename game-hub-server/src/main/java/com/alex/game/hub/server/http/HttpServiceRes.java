/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.hub.server.http;

/**
 * HttpService请求处理的返回
 * 
 * @author Alex
 * @date 2016年2月23日 下午3:11:33
 */
public class HttpServiceRes {

	// res成功常量
	public static final int SUCCESS = 1;
	// res失败常量
	public static final int FAILURE = 0;

	// HttpService执行后的状态(0:失败，1:成功)
	private int res;
	// 消息说明
	private String msg;
	// 有返回的HttpService执行结果,如果不是单个字符串或数值 则将结果序列化为json字符串;无返回是为空串
	private Object data;

	public HttpServiceRes() {
	}

	/**
	 * @param res
	 * @param msg
	 * @param data
	 */
	public HttpServiceRes(int res, String msg, Object data) {
		this.res = res;
		this.msg = msg;
		this.data = data;
	}

	/**
	 * 返回成功的HttpRes
	 * 
	 * @param msg
	 * @return
	 */
	public static HttpServiceRes success(String msg) {

		return new HttpServiceRes(SUCCESS, msg, "");
	}

	/**
	 * 返回成功的HttpRes
	 * 
	 * @param data
	 * @return
	 */
	public static HttpServiceRes success(Object data) {

		return new HttpServiceRes(SUCCESS, "", data);
	}

	/**
	 * 返回成功的HttpRes
	 * 
	 * @param msg
	 * @param data
	 * @return
	 */
	public static HttpServiceRes success(String msg, Object data) {

		return new HttpServiceRes(SUCCESS, msg, data);
	}

	/**
	 * 返回失败的HttpRes
	 * 
	 * @param msg
	 * @return
	 */
	public static HttpServiceRes failure(String msg) {

		return new HttpServiceRes(FAILURE, msg, "");
	}

	/**
	 * 返回失败的HttpRes
	 * 
	 * @param msg
	 * @param data
	 * @return
	 */
	public static HttpServiceRes failure(String msg, Object data) {

		return new HttpServiceRes(FAILURE, msg, data);
	}

	public int getRes() {
		return res;
	}

	public void setRes(int res) {
		this.res = res;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
