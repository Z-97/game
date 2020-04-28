package com.alex.game.http;

import java.util.List;
import java.util.ArrayList;
import org.apache.http.entity.ContentType;
import org.junit.Test;
import com.alex.game.common.util.RandomUtil;

import com.alex.game.server.http.HttpServiceReq;
import com.alex.game.util.HttpRequestUtil;
import com.alibaba.fastjson.JSON;

public class AddGoldTest {
	public static final String host = "http://localhost:7001";
	@Test
	public void refundAgentService(){
		String serviceName="AddPlayerGoldService";
		int random=RandomUtil.random(100);
		long addGold =1000000000;
		List<String> args = new ArrayList<>();
		args.add(String.valueOf(1));
		args.add(String.valueOf(365518));
		args.add(String.valueOf(addGold));
		args.add(String.valueOf("管理员加钱"));
		sendReqeust(serviceName, args);
	}
	public String sendReqeust(String serviceName,List<String> args){
		HttpServiceReq req = new HttpServiceReq();
		req.setService(serviceName);
		req.setArgs(args);
		String resStr = null;
		try {
			resStr = HttpRequestUtil.post(host, ContentType.APPLICATION_JSON, JSON.toJSONString(req));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return resStr;
	}
}
