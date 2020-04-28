/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.login.server.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.io.ResolverUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HttpService工厂类
 * 
 * @author Alex
 * @date 2016年12月18日 下午1:54:10
 */
public class HttpServiceFactory {

	private static final Logger LOG = LoggerFactory.getLogger(HttpServiceFactory.class);

	// 扫描HttpService的包名
	private static final String SERVICE_PKG = "com.alex.game.login.httpservice";
	// SERVICE_PKG下所有的HttpService实例，key：service名称
	private static final Map<String, HttpService> HTTP_SERVICES = new HashMap<>();

	private HttpServiceFactory() {
	}

	/**
	 * 扫描SERVICE_PKG查找HttpService手动注入
	 */
	public static void registerAllServices() {
		Class<?> serviceClz = HttpService.class;
		ResolverUtil<HttpService> serviceResolver = new ResolverUtil<HttpService>()
				.find(type -> type != serviceClz && serviceClz.isAssignableFrom(type), SERVICE_PKG);
		try {
			Set<Class<? extends HttpService>> serviceClasses = serviceResolver.getClasses();
			for (Class<? extends HttpService> serviceClass : serviceClasses) {
				HttpService service = serviceClass.newInstance();
				String serviceName = service.getName();

				if (HTTP_SERVICES.containsKey(serviceName)) {
					throw new RuntimeException(String.format("[%s]和[%s]名称重复", serviceName,
							HTTP_SERVICES.get(serviceName).getClass().getName()));
				}

				// 手动注入guic依赖
//				ApplicationContext.getInstance().injectMembers(service);
				HTTP_SERVICES.put(serviceName, service);
			}
		} catch (Exception e) {
			LOG.error("扫描[" + SERVICE_PKG + "]HttpService错误", e);
			System.exit(1);
		}
	}

	/**
	 * 获取HttpService
	 *
	 * @param name
	 * @return
	 */
	public static final HttpService getService(String name) {

		return HTTP_SERVICES.get(name);
	}
}
