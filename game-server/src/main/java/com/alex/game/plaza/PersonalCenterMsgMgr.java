package com.alex.game.plaza;
import com.alex.game.dbdata.dom.GiftRecord;
import com.alex.game.dbdata.dom.PlayerDom;
import com.alex.game.player.PlayerProto.Gift;
import com.alex.game.player.PlayerProto.PlayerPB;
import com.alex.game.player.PlayerProto.ResBindingPhone;
import com.alex.game.player.PlayerProto.ResGiftList;
import com.alex.game.player.PlayerProto.ResLoadPlayerInfo;
import com.alex.game.player.PlayerProto.ResModfiyIcon;
import com.alex.game.player.PlayerProto.ResModfiyNickName;
import com.alex.game.player.PlayerProto.ResModfiySignature;
import com.alex.game.player.PlayerProto.ResResetPwd;
import com.alex.game.player.manager.PlayerMsgMgr;
import com.alex.game.player.struct.Player;
import com.alex.game.server.tcp.MsgHandlerFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 个人中心
 * @author yejuhua
 *
 */
@Singleton
public class PersonalCenterMsgMgr {

	@Inject
	private PlayerMsgMgr msgMgr;
	/**
     * 发送修改昵称返回
     * @param player
     * @param i
     */
	public void sendResModfiyNickName(Player player, int res) {
		ResModfiyNickName.Builder resModfiyNickName=ResModfiyNickName.newBuilder();
		resModfiyNickName.setRes(res);
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("player.ResModfiyNickName"), resModfiyNickName.build().toByteString());
	}
	/**
	 * 发送绑定手机结果
	 * @param player
	 * @param res 
	 *         1手机号码格式不正确2已经绑定手机号码3手机号码已经被绑定
	 */
	public void sendbindingMob(Player player, int res) {
		ResBindingPhone.Builder resBindingPhone =ResBindingPhone.newBuilder();
		resBindingPhone.setRes(res);
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("player.ResBindingPhone"), resBindingPhone.build().toByteString());
	}
	public void sendModfiyIcon(Player player, int res) {
		ResModfiyIcon.Builder resBindingPhone =ResModfiyIcon.newBuilder();
		resBindingPhone.setRes(res);
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("player.ResModfiyIcon"), resBindingPhone.build().toByteString());
	}
	/**
	 * 发送重置密码消息
	 * 
	 * @param player
	 * @param res
	 *            0:成功,1:用户名不匹配
	 */
	public void sendResetPwdMsg(Player player, int res) {
		ResResetPwd.Builder msg = ResResetPwd.newBuilder();
		msg.setRes(res);
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("player.ResResetPwd"),msg.build().toByteString());
	}
	public void sendResLoadPlayerInfo(Player player, int res, PlayerDom playerDom) {
		ResLoadPlayerInfo.Builder msg = ResLoadPlayerInfo.newBuilder();
		msg.setRes(res);
		if(playerDom!=null) {
			PlayerPB.Builder playerInfo =msgMgr.getPlayerInfo(playerDom);
			msg.setPlayerPB(playerInfo);
		}
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("player.ResLoadPlayerInfo"),msg.build().toByteString());
	}
	public void sendResGiftList(Player player) {
		ResGiftList.Builder msg = ResGiftList.newBuilder();
		if(player.getGiftRecords()!=null) {
			for(GiftRecord giftRecord:player.getGiftRecords()) {
				Gift.Builder gift=Gift.newBuilder();
				gift.setGetType(giftRecord.getGetType());
				gift.setGiftGold(giftRecord.getGiftGold());
                gift.setGiftTime(giftRecord.getTime());
                gift.setPlayerName(giftRecord.getPlayerName());
                msg.addGiftList(gift);
			}
		}
		System.out.println(msg.build());
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("player.ResGiftList"),msg.build().toByteString());
	}
	public void sendResModfiySignature(Player player, int res) {
		ResModfiySignature.Builder msg = ResModfiySignature.newBuilder();
		msg.setRes(res);
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("player.ResModfiySignature"),msg.build().toByteString());
		
	}
	
}
