package com.alex.game.login;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.alex.game.common.IpTable;
import com.alex.game.common.util.IdGenerator;
import com.alex.game.common.util.MD5Util;
import com.alex.game.common.util.RandomKeyGenerator;
import com.alex.game.common.util.RandomUtil;
import com.alex.game.dbdata.dom.PlayerDom;
import com.alex.game.login.manager.LoginMgr;
import com.alex.game.player.manager.PlayerMgr;
import com.alex.game.player.struct.Player;
import com.alex.game.server.ApplicationContext;
import com.alex.game.server.tcp.MsgHandlerFactory;

public class LoginTest {

private ApplicationContext app = null;
	
	@Before
	public void Before() {
		MsgHandlerFactory.loadConfig();
		this.app = ApplicationContext.createInstance();
		try {
			MsgHandlerFactory.registerAllHandlers();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void testIpTable() {
		PlayerMgr playerMgr = app.getBean(PlayerMgr.class);
		Date now = new Date();
		PlayerDom playerDom = new PlayerDom();
		playerDom.setTourist(true);
		playerDom.setIcon(1);
		playerDom.setSex(1);
		playerDom.setRegisterIp("127.0.0.1");
		playerDom.setRegisterTime(now);
		playerDom.setLoginTime(now);
		playerDom.setLoginIp("127.0.0.1");
		playerDom.setLoginCount(1);
		playerDom.setOnline(true);
		playerDom.setUpdateTime(now);
		playerDom.setSex(1);//默认为女，默认为1
		playerDom.setIcon(1);//默认女性头像为1
		playerDom.setLevel(1);
		int channelId = 0;
		playerDom.setChannelId(channelId + "");
		playerDom.setPackageId("packageId");
		playerDom.setBankPwd(MD5Util.encodeWithTail("8888"));
		playerDom.setVipLevel(0);
		playerDom.setPlayerType(0);
		
		for(int i =0 ;i<3000;i++) {
			long id=RandomUtil.random(1, 10000)+IdGenerator.nextId();
			playerDom.setId(id);
			playerDom.setUserName("r"+id+RandomKeyGenerator.generate(3));
			playerDom.setNickName("r"+id+RandomKeyGenerator.generate(2));
			playerDom.setPwd(MD5Util.encodeWithTail("pwd"+i));
			String mac=RandomKeyGenerator.generate(10);
			playerDom.setLoginMac(mac);
			playerDom.setRegisterMac(mac);
			String deviceModel=RandomKeyGenerator.generate(6);
			playerDom.setRegisterDevice(1);
			playerDom.setRegisterDeviceModel(deviceModel);
			String deviceId=RandomKeyGenerator.generate(10);
			playerDom.setDeviceId(deviceId);
			playerDom.setTouristMac(mac);
			// 插入数据
			playerMgr.insertPlayer(playerDom);
		}
		

		
	}
}
