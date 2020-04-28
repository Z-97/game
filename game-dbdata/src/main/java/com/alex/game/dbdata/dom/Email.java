package com.alex.game.dbdata.dom;

import java.util.Date;

/**
 * 邮件
 * @author yejuhua
 *
 */
public class Email {
	private int id;
	private boolean isRead;
	private String title;
	private String content;
	private String sendName;
	private long senderId;
	private Date createTime;
	/**
	 * 0个人1系统
	 */
	private int emailType;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public boolean isRead() {
		return isRead;
	}
	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSendName() {
		return sendName;
	}
	public void setSendName(String sendName) {
		this.sendName = sendName;
	}
	public long getSenderId() {
		return senderId;
	}
	public void setSenderId(long senderId) {
		this.senderId = senderId;
	}
	public int getEmailType() {
		return emailType;
	}
	public void setEmailType(int emailType) {
		this.emailType = emailType;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
	
}
