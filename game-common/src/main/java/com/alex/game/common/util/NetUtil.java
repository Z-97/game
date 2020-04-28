/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.common.util;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * 网络工具类
 *
 * @author Alex
 * @date 2016/7/6 13:24
 */
public class NetUtil {

	private static final String LOCAL_IP = "127.0.0.1";

	private NetUtil() {
	}

	/**
	 * 远程端口是否可用
	 *
	 * @param ip
	 * @param port
	 * @return
	 */
	public static boolean remotePortAbled(String ip, int port) {
		try (Socket s = new Socket()) {
			SocketAddress add = new InetSocketAddress(ip, port);
			s.connect(add, 3000);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 本机端口是否可用
	 *
	 * @param port
	 * @return
	 */
	public static boolean localPortAbled(int port) {
		try (Socket s = new Socket()) {
			s.bind(new InetSocketAddress(LOCAL_IP, port));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
