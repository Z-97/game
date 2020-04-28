package com.alex.game.plaza.email.manager;
import java.util.Date;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import com.alex.game.dbdata.dom.Email;
import com.alex.game.event.manager.EventMgr;
import com.alex.game.event.struct.login.PlayerLoginSuccessEvent;
import com.alex.game.player.manager.PlayerMgr;
import com.alex.game.player.struct.Player;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
@Singleton
public class EmailMgr {

	@Inject
	private EmailMsgMgr emailMsgMgr;
	/**
	 * 保留邮件总条数
	 */
	private static final int EMAIL_RECORDS_NUM = 50;
	@Inject
	private PlayerMgr playerMgr;
	
	@Inject
	public EmailMgr(EventMgr eventMgr) {
		eventMgr.register(this);
	}
	/**
	 * 响应登录成功事件，加载系统邮件，检查邮件是否过期
	 * 
	 * @param event
	 */
	@Subscribe
	public void onPlayerLoginSuccess(PlayerLoginSuccessEvent event) {
		Player player = event.player;
		ConcurrentHashMap<Integer, Email> emails = player.getEmails();
		if (emails == null) {
			return;
		}
//		LocalDate nowDate = LocalDate.now();
//		TreeSet<Integer> keyset = new TreeSet<>(emails.keySet());

		// 删除过期邮件
//		int size = emails.size();
//		for (int i = 0; i < size; i++) {
//			Integer key = keyset.pollLast();
//			if (key == null) {
//				break;
//			}
//			Email email = emails.get(key);
//			if (email == null) {
//				continue;
//			}
//		    Instant instant = email.getCreateTime().toInstant();
//		    ZoneId zoneId = ZoneId.systemDefault();
//
//		     
//		    LocalDate tmpLocalDate = instant.atZone(zoneId).toLocalDate();
//			LocalDate outLocalDate = tmpLocalDate.plusDays(3);
//			if (nowDate.isAfter(outLocalDate)||nowDate.isEqual(outLocalDate)) {
//				emails.remove(key);
//			}
//		}

		checkEmails(emails);
	}
	
	/**
	 * 删除多于的邮件
	 * @param emails
	 */
	private void checkEmails(ConcurrentHashMap<Integer, Email> emails) {
		int dSize = emails.size() - EMAIL_RECORDS_NUM;
		// 删除超过30条的多余邮件
		TreeSet<Integer> keyset = new TreeSet<>(emails.keySet());
		
		if (dSize > 0) {
			for (int i = 0; i < dSize; i++) {
				Integer key = keyset.pollFirst();
				if (key == null) {
					break;
				}
				emails.remove(key);
			}
		}
	}
	/**
	 * 获取邮件了列表
	 * @param player
	 */
	public void loadEmail(Player player) {
		emailMsgMgr.sendResmailList(player,player.getEmails());
	}
	/**
	 * 查看邮件
	 * @param player
	 * @param emailId
	 */
	public void readEmail(Player player, int emailId) {
		if(player.getEmails()==null||player.getEmails().isEmpty()||!player.getEmails().containsKey(emailId)) {
			emailMsgMgr.sendResReadEmail(player,1,null);
			return;
		}
		ConcurrentHashMap<Integer, Email> emails=player.getEmails();
		Email email=emails.get(emailId);
		email.setRead(true);
		playerMgr.updatePlayerAsync(player);
		emailMsgMgr.sendResReadEmail(player,0,email);
	}
	/**
	 * 删除所有邮件
	 * @param player
	 */
	public void delAllEmail(Player player) {
		if(player.getEmails()!=null&&!player.getEmails().isEmpty()) {
			player.getEmails().clear();
			playerMgr.updatePlayerAsync(player);
		}
		emailMsgMgr.sendResDelAllEmail(player,0);
	}
	public void delEmail(Player player, int emailId) {
		if(player.getEmails()==null||player.getEmails().isEmpty()||!player.getEmails().containsKey(emailId)) {
			emailMsgMgr.sendResDelEmail(player,1);
			return;
		}
		player.getEmails().remove(emailId);
		playerMgr.updatePlayerAsync(player);
		emailMsgMgr.sendResDelEmail(player,0);
	}
	
	
	
	/**
	 * 创建邮件
	 * @param emailId
	 * @return
	 */
	public Email creteEmail(int emailId,String content,int emailType,long senderId,String title,String sendName) {
		Email email=new Email();
		email.setId(emailId);
		email.setContent(content);
		email.setCreateTime(new Date());
		email.setEmailType(emailType);
		email.setSenderId(senderId);
		email.setTitle(title);
		email.setSendName(sendName);
		return email;
	}
	

	public void addEmail(Player player,Email email) {
		ConcurrentHashMap<Integer, Email> emails=player.getEmails();
		emails.put(email.getId(), email);
		checkEmails(emails);
	}
	
}
