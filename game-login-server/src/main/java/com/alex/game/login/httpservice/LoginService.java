package com.alex.game.login.httpservice;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alex.game.common.util.MD5Util;
import com.alex.game.dbdata.dom.PlayerDom;
import com.alex.game.dbdata.mapper.PlayerMapper;
import com.alex.game.login.server.http.HttpService;
import com.alex.game.login.server.http.HttpServiceRes;
import com.google.inject.Inject;

/**
 * 登陆Service
 * 
 * @author Alex
 * @date 2017年5月15日 下午2:56:38
 */
public class LoginService implements HttpService {
	
	private static final Logger LOG = LoggerFactory.getLogger(LoginService.class);
	
	@Inject
	private PlayerMapper playerMapper;

	@Override
	public String getName() {
		return "LoginService";
	}

	@Override
	public HttpServiceRes service(List<String> args) {
		// 用户名
		String userName = args.get(0);
		// 密码
		String pwd = args.get(1);
		// 登陆key
		String key = args.get(2);
		
		// 根据用户名查找玩家，先在活跃账号缓存找，找不到再去数据库找
		PlayerDom playerDom = playerMapper.selectByUserName(userName);
		if (playerDom == null) {
			return HttpServiceRes.failure("玩家使用的用户名不存在");
		}
		
		if (pwd == null) {// 密码为空使用key登陆
			if (!playerDom.getLoginKey().equals(key)) {
				return HttpServiceRes.failure("登录失败,key错误");
			}
		} else if(!playerDom.getPwd().equals(MD5Util.encodeWithTail(pwd))) {// 密码登陆
			return HttpServiceRes.failure("登录失败,密码错误");
		}
		
		LOG.info("玩家[{}]登陆成功", userName);
		return HttpServiceRes.success("登录成功");
	}

}
