package com.alex.game.plaza.email.manager;
import java.util.concurrent.ConcurrentHashMap;
import com.alex.game.dbdata.dom.Email;
import com.alex.game.email.EmailProto.EmailInfo;
import com.alex.game.email.EmailProto.ResDelAllEmail;
import com.alex.game.email.EmailProto.ResDelEmail;
import com.alex.game.email.EmailProto.ResEmailList;
import com.alex.game.email.EmailProto.ResNewEmailMsg;
import com.alex.game.email.EmailProto.ResReadEmail;
import com.alex.game.player.manager.PlayerMsgMgr;
import com.alex.game.player.struct.Player;
import com.alex.game.server.tcp.MsgHandlerFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class EmailMsgMgr {

	@Inject
	private PlayerMsgMgr msgMgr;
	/**
	 * 返回邮件列表
	 * @param player
	 * @param emails
	 */
	public void sendResmailList(Player player, ConcurrentHashMap<Integer, Email> emails) {
		ResEmailList.Builder resmailList =ResEmailList.newBuilder();
        for(Email email:emails.values()) {
        	EmailInfo.Builder emailInfo=EmailInfo.newBuilder();
        	emailInfo.setEmailId(email.getId());
        	emailInfo.setTitle(email.getTitle());
        	resmailList.addEmailList(emailInfo);
        }
        msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("email.ResEmailList"), resmailList.build().toByteString());
	}
	/**
	 * 查看邮件结果
	 * @param player
	 * @param res
	 */
	public void sendResReadEmail(Player player, int res,Email email) {
		ResReadEmail.Builder resReadEmail =ResReadEmail.newBuilder();
		resReadEmail.setRes(res);
		if(email!=null) {
			com.alex.game.email.EmailProto.Email.Builder  pEmail=com.alex.game.email.EmailProto.Email.newBuilder();
			pEmail.setEmailId(email.getId());
			pEmail.setContent(email.getContent());
			pEmail.setTitle(email.getTitle());
			pEmail.setIsRead(email.isRead());
			pEmail.setDate((email.getCreateTime().getTime()/1000));
			resReadEmail.setEmail(pEmail);
		}
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("email.ResReadEmail"), resReadEmail.build().toByteString());
	}
	public void sendResDelAllEmail(Player player, int res) {
		ResDelAllEmail.Builder resReadEmail =ResDelAllEmail.newBuilder();
		resReadEmail.setRes(res);
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("email.ResDelAllEmail"), resReadEmail.build().toByteString());
		
	}
	public void sendResDelEmail(Player player, int res) {
		ResDelEmail.Builder resReadEmail =ResDelEmail.newBuilder();
		resReadEmail.setRes(res);
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("email.ResDelEmail"), resReadEmail.build().toByteString());
	}

	public void sendResNewEmail(Player player, Email email) {
		ResNewEmailMsg.Builder resReadEmail =ResNewEmailMsg.newBuilder();
		com.alex.game.email.EmailProto.Email.Builder  pEmail=com.alex.game.email.EmailProto.Email.newBuilder();
		pEmail.setEmailId(email.getId());
		pEmail.setContent(email.getContent());
		pEmail.setTitle(email.getTitle());
		pEmail.setIsRead(email.isRead());
		pEmail.setDate((email.getCreateTime().getTime()/1000));
		resReadEmail.setEmail(pEmail);
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("email.ResDelEmail"), resReadEmail.build().toByteString());
	}
}
