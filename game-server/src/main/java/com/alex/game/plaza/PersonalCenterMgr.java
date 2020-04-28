package com.alex.game.plaza;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alex.game.common.util.MD5Util;
import com.alex.game.dbdata.dom.PlayerDom;
import com.alex.game.dblog.core.DbLogService;
import com.alex.game.dblog.plaza.BindingPhoneLog;
import com.alex.game.login.manager.LoginMgr;
import com.alex.game.player.manager.PlayerMgr;
import com.alex.game.player.struct.Player;
import com.google.inject.Inject;
import com.google.inject.Singleton;
/**
 * 个人中心
 */
@Singleton
public class PersonalCenterMgr {
	private static final Logger LOG = LoggerFactory.getLogger(PersonalCenterMgr.class);
	// 1到5个字母数字下划线汉字可以有空格
	public static final String NICKNAME_PATTERN = "[a-zA-Z0-9_\u0020\u4e00-\u9fa5]{1,18}";
	@Inject
	private LoginMgr loginMgr;
	@Inject
	private PlayerMgr playerMgr;
	@Inject
	private PersonalCenterMsgMgr personalCenterMsgMgr;
	/**
	 * 修改玩家昵称
	 * @param player
	 * @param nickName
	 */
	public void modfiyNickName(Player player, String nickName) {
		if(nickName==null||nickName.equals("")||nickName.length()>32||!nickName.matches(NICKNAME_PATTERN)) {
			personalCenterMsgMgr.sendResModfiyNickName(player, 1);
			return ;
		}
		
		if(player.getIsModfiyNickname()!=null&&player.getIsModfiyNickname()) {
			personalCenterMsgMgr.sendResModfiyNickName(player, 2);
			return ;
		}
		player.setNickName(nickName);
		player.setIsModfiyNickname(true);
		personalCenterMsgMgr.sendResModfiyNickName(player, 0);
		playerMgr.updatePlayerAsync(player);
	}
	/**
	 * 重置密码
	 * @param player
	 * @param userName
	 * @param phone
	 * @param pwd
	 * @param code
	 */
	public void resetPwd(Player player, String userName, String phone, String pwd, String code) {
		
		//检查用户名是否匹配
		if(userName ==null ||userName.equals("")||!player.getUserName().equals(userName)) {
			LOG.warn("会话{}用户[{}]用户名[{}]不匹配", player.channel, player.getUserName(), userName);
			personalCenterMsgMgr.sendResetPwdMsg(player, 1);
			return;
		}
		
		// 密码5~12个字符（字母、数字或特殊符号，区分大小写）
		if (!pwd.matches(LoginMgr.PASSWORD_PATTERN)) {
			LOG.warn("会话{}手机号[{}]重置的密码[{}]必须是5~12个字母、数字或特殊符号", player.channel, player.phone, pwd);
			personalCenterMsgMgr.sendResetPwdMsg(player, 2);
			return;
		}
		
		if(!loginMgr.checkPhoneCode(player, code)) {
			personalCenterMsgMgr.sendResetPwdMsg(player, 3);
			return;
		}
		PlayerDom playerDom = playerMgr.selectByUserName(player.phone);
		if (playerDom == null) {
			LOG.warn("会话{}手机号[{}]重置密码的账号不存在", player.channel, player.phone, pwd);
			personalCenterMsgMgr.sendResetPwdMsg(player, 4);
			return;
		}
		
		playerDom.setPwd(MD5Util.encodeWithTail(pwd));
		playerMgr.updatePlayerAsync(playerDom);
		personalCenterMsgMgr.sendResetPwdMsg(player, 0);
		
		LOG.warn("会话{}手机号[{}]重置密码成功", player.channel, player.phone, pwd);
		
	}
	/**
	 * 绑定手机
	 * @param player
	 * @param phone
	 * @param code
	 */
	public void bindingPhone(Player player, String mobilePhone, String code) {
		if(player.isTourist()) {
			return;
		}
		if (!loginMgr.checkPhoneCode(player, code)) {
			personalCenterMsgMgr.sendbindingMob(player, 1);
			LOG.warn("id[{}]玩家[{}]手机验证码[{}]错误", player.getId(), player.getUserName(), code);
			return;
		}
		
		if (!mobilePhone.matches(LoginMgr.PHONE_PATTERN)) {
			personalCenterMsgMgr.sendbindingMob(player, 3);
			LOG.warn("id[{}]玩家[{}]的手机[{}]格式错误", player.getId(), player.getUserName(), mobilePhone);
			return;
		}

		if (player.getPhone() != null) {
			LOG.warn("id[{}]玩家[{}]已经绑定手机号[{}]", player.getId(), player.getUserName(), player.getPhone());
			personalCenterMsgMgr.sendbindingMob(player,2);
			return;
		}

		PlayerDom oldPlayer = playerMgr.selectByPhone(mobilePhone);
		if (oldPlayer != null) {
			LOG.warn("手机号码[{}]已经绑定玩家[{}]id[{}]", mobilePhone, oldPlayer.getUserName(), oldPlayer.getId());
			personalCenterMsgMgr.sendbindingMob(player, 3);
			return;
		}
		player.setPhone(mobilePhone);
		player.setBindingPhoneTime(new Date());
		player.setTourist(false);
		personalCenterMsgMgr.sendbindingMob(player, 0);
		playerMgr.updatePlayerAsync(player);
		LOG.info("玩家[{}]绑定手机[{}]成功", player.getUserName(), mobilePhone);
		DbLogService.log(new BindingPhoneLog(player));
		
	}
	public void modfiyIcon(Player player, int icomId) {
		if(icomId<1||icomId>8) {
			personalCenterMsgMgr.sendModfiyIcon(player, 1);
			return ;
		}
		player.setIcon(icomId);
		personalCenterMsgMgr.sendModfiyIcon(player, 0);
		playerMgr.updatePlayerAsync(player);
		LOG.info("玩家[{}]修改icon[{}]成功", player.getUserName(), icomId);
	}
	/**
	 * 查看玩家信息
	 * @param player
	 * @param playerId
	 */
	public void loadPlayerInfo(Player player, long playerId) {
		PlayerDom targetPlayerDom = playerMgr.selectById(playerId);
		if (targetPlayerDom == null) {
			personalCenterMsgMgr.sendResLoadPlayerInfo(player, 1,targetPlayerDom);
			return;
		}
		personalCenterMsgMgr.sendResLoadPlayerInfo(player, 0,targetPlayerDom);
	}
	public void reqModfiySignature(Player player, String signature) {
		if(signature==null||signature.length()<1||signature.length()>128) {
			personalCenterMsgMgr.sendResModfiySignature(player, 1);
			return;
		}
		player.setSignature(signature);
		playerMgr.updatePlayerAsync(player);
		personalCenterMsgMgr.sendResModfiySignature(player, 0);
		LOG.info("玩家[{}]修改签名[{}]成功", player.getUserName(), signature);
	
	}
	

}
