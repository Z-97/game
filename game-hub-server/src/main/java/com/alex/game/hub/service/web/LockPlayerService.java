package com.alex.game.hub.service.web;

import java.util.List;

import com.alex.game.hub.server.http.HttpService;
import com.alex.game.hub.server.http.HttpServiceRes;

/**
 * 封号
 * 
 * @author Alex
 * @date 2017年4月5日 下午8:41:27
 */
public class LockPlayerService implements HttpService {

	@Override
	public String getName() {
		return "LockPlayerService";
	}

	@Override
	public HttpServiceRes service(List<String> args) {
		return HttpServiceRes.success("封锁账号成功");
	}

}
