package com.alex.game.login.manager;

import java.util.List;

import com.alex.game.dbdic.dic.AgentDic;
import com.alex.game.dbdic.dom.AgentDom;
import com.alex.game.dbdic.dom.GameDom;
import com.alex.game.login.LoginProto.Agent;
import com.alex.game.login.LoginProto.Game;
import com.alex.game.login.LoginProto.ResAgentList;
import com.alex.game.login.LoginProto.ResGameList;
import com.alex.game.login.LoginProto.ResGetPhoneLoginCode;
import com.alex.game.login.LoginProto.ResLogin;
import com.alex.game.login.LoginProto.ResLogout;
import com.alex.game.login.LoginProto.ResPing;
import com.alex.game.login.LoginProto.ResRegister;
import com.alex.game.login.struct.LogoutType;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.manager.PlayerMsgMgr;
import com.alex.game.player.struct.Player;
import com.alex.game.server.tcp.MsgHandlerFactory;
import com.alex.game.util.TimeUtil;
import com.google.inject.Inject;
import com.google.inject.Singleton;
@Singleton
public class LoginMsgMgr {
	@Inject
	private PlayerMsgMgr msgMgr;
	@Inject
    private AgentDic agentDic;   
	public void sendAgentList(Player player) {
		ResAgentList.Builder msg = ResAgentList.newBuilder();
	    for(AgentDom agentDom:agentDic.values()) {
	    	Agent.Builder agent=Agent.newBuilder();
	    	agent.setId(agentDom.getId());
	    	agent.setName(agentDom.getName());
	    	agent.setWx(agentDom.getWx());
	    	msg.addAgentList(agent);
	    }
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("login.ResAgentList"),msg.build().toByteString());
	}
	/**
	 * 发送登陆结果
	 * 
	 * @param player
	 * @param res
	 *            0:成功,1:用户名不存在,2:登陆key错误,3:密码错误
	 */
	public void sendLoginMsg(Player player, int res) {
		ResLogin.Builder msg =ResLogin.newBuilder();
		msg.setRes(res);
		CommonMessage.Builder commonMessage =CommonMessage.newBuilder();
		commonMessage.setId(MsgHandlerFactory.getProtocol("login.ResLogin"));
		commonMessage.setContent(msg.build().toByteString());
		msgMgr.writeMsg(player, commonMessage.build());
	}
	
	/**
	 * 发送登录结果消息
	 *
	 * @param player
	 * @param type
	 *            0:正常退出,1:同一个账号重复登录被T号退出,2:玩家被冻结退出,3:游戏服务器维护被T下线,4:停服玩家被T下线
	 */
	public void sendLogoutMsg(Player player, LogoutType type) {
		ResLogout.Builder msg = ResLogout.newBuilder();
		msg.setRes(type.val);
		CommonMessage.Builder commonMessage =CommonMessage.newBuilder();
		commonMessage.setId(MsgHandlerFactory.getProtocol("login.ResLogout"));
		commonMessage.setContent(msg.build().toByteString());
		msgMgr.writeMsg(player, commonMessage.build());
	}

	
	/**
	 * 发送验证码
	 * @param player
	 * @param res
	 *           0成功，1手机格式不正确
	 */
	public void sendResGetPhoneLoginCode(Player player, int res) {
		ResGetPhoneLoginCode.Builder msg = ResGetPhoneLoginCode.newBuilder();
		msg.setRes(res);
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("login.ResGetPhoneLoginCode"),msg.build().toByteString());
	}

	public void sendResRegisterg(Player player, int res) {
		ResRegister.Builder msg = ResRegister.newBuilder();
		msg.setRes(res);
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("login.ResRegister"),msg.build().toByteString());
	}

	/**
	 * 
	 * @param player
	 * @param gameList
	 */
	public void sendGameListMsg(Player player, List<GameDom> gameList) {
		ResGameList.Builder msg = ResGameList.newBuilder();
		for(GameDom gameDom:gameList) {
			if(gameDom.getState()!=0) {
				Game.Builder game=Game.newBuilder();
				game.setId(gameDom.getId());
				game.setName(gameDom.getName());
				game.setState(gameDom.getState());
				game.setSeq(gameDom.getSeq());
				game.setDesc(gameDom.getDesc());
				msg.addGameList(game);
			}
		}
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("login.ResGameList"),msg.build().toByteString());		
	}
	public void sendPong(Player player) {
		ResPing.Builder msg = ResPing.newBuilder();
		msg.setServerTime(TimeUtil.getCurrentSeconds());
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("login.ResPing"),msg.build().toByteString());		
	}
	
}
