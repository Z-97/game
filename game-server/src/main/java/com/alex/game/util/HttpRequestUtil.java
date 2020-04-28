package com.alex.game.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Async;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 简单的内部http通讯工具类
 * 
 * @author Alex
 * @date 2017年4月7日 上午12:15:31
 */
public class HttpRequestUtil {
	
	private static final Logger HTTP_SERVICE_LOG = LoggerFactory.getLogger("HttpServiceLog");
	
	private static final int CONNECT_TIME_OUT = 5000;
	private static final int SOCKET_TIME_OUT = 5000;
	
    private static final Async ASYNC = Async.newInstance().use(Executors.newFixedThreadPool(8, r -> new Thread(r, "HttpRequest-Executor")));
    
	/**
	 * 异步get 请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static Future<Content> getAsync(String url, ContentType contentType, BasicNameValuePair... params) {
		StringJoiner paramsJoiner = new StringJoiner("&");
		for (BasicNameValuePair param : params) {
			 paramsJoiner.add(param.getName() + "=" + param.getValue());
		}
		String fullUrl = url + "?" + paramsJoiner.toString();
		HTTP_SERVICE_LOG.info("发送异步Get请求[{}]", fullUrl);
		return ASYNC.execute(Request.Get(fullUrl).connectTimeout(CONNECT_TIME_OUT).socketTimeout(SOCKET_TIME_OUT).addHeader(HTTP.CONTENT_TYPE, contentType.toString()));
	}
	
	/**
	 * 异步post 请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static Future<Content> postAsync(String url, ContentType contentType, List<BasicNameValuePair> params) {
		HTTP_SERVICE_LOG.info("发送异步Post请求[{}]值[{}]", url, params);
		return ASYNC.execute(Request.Post(url).bodyForm(params).connectTimeout(CONNECT_TIME_OUT).socketTimeout(SOCKET_TIME_OUT).addHeader(HTTP.CONTENT_TYPE, contentType.toString()));
	}
	
	/**
	 * 同步post 提交
	 * @param url
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String post(String url, ContentType contentType, List<BasicNameValuePair> params) throws ClientProtocolException, IOException {
		HTTP_SERVICE_LOG.info("发送同步Post请求[{}]值[{}]", url, params.toString());
		return Request.Post(url).bodyForm(params,StandardCharsets.UTF_8).connectTimeout(CONNECT_TIME_OUT).socketTimeout(SOCKET_TIME_OUT).addHeader(HTTP.CONTENT_TYPE, contentType.toString())
				.execute().returnContent().asString(StandardCharsets.UTF_8);
	}
	
	/**
	 * 同步post 提交
	 * @param url
	 * @param data
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String post(String url, ContentType contentType, String data) throws ClientProtocolException, IOException {
		HTTP_SERVICE_LOG.info("发送同步Post请求[{}]值[{}]", url, data);
		return Request.Post(url).bodyString(data, contentType).connectTimeout(CONNECT_TIME_OUT).socketTimeout(SOCKET_TIME_OUT).addHeader(HTTP.CONTENT_TYPE, contentType.toString())
				.execute().returnContent().asString(StandardCharsets.UTF_8);
	}
	
	/**
	 * 同步get 提交
	 * @param url
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String get(String url, ContentType contentType, List<BasicNameValuePair> params) throws ClientProtocolException, IOException {
		StringBuilder paramsStr = new StringBuilder(64);
		for (BasicNameValuePair param : params) {
			 paramsStr.append(param.getName()).append("=").append(param.getValue());
		}
		String fullUrl = url + "?" + paramsStr;
		HTTP_SERVICE_LOG.info("发送同步Get请求[{}]", fullUrl);
		return Request.Get(fullUrl).connectTimeout(CONNECT_TIME_OUT).socketTimeout(SOCKET_TIME_OUT).addHeader(HTTP.CONTENT_TYPE, contentType.toString())
				.execute().returnContent().asString(StandardCharsets.UTF_8);
	}
}
