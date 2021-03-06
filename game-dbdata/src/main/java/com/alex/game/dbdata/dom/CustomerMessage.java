package com.alex.game.dbdata.dom;

import java.util.Date;

public class CustomerMessage {

	private Long id;

	private Long sendId;

	private String content;

	private Date sendTime;

	private Long playerId;
	
    private String phone;
	public Long getId() {
		return id;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of
	 * the database column customer_message.id
	 * 
	 * @param id the value for customer_message.id
	 * @mbg.generated Tue Jul 17 13:38:06 CST 2018
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value
	 * of the database column customer_message.send_id
	 * 
	 * @return the value of customer_message.send_id
	 * @mbg.generated Tue Jul 17 13:38:06 CST 2018
	 */
	public Long getSendId() {
		return sendId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of
	 * the database column customer_message.send_id
	 * 
	 * @param sendId the value for customer_message.send_id
	 * @mbg.generated Tue Jul 17 13:38:06 CST 2018
	 */
	public void setSendId(Long sendId) {
		this.sendId = sendId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value
	 * of the database column customer_message.content
	 * 
	 * @return the value of customer_message.content
	 * @mbg.generated Tue Jul 17 13:38:06 CST 2018
	 */
	public String getContent() {
		return content;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of
	 * the database column customer_message.content
	 * 
	 * @param content the value for customer_message.content
	 * @mbg.generated Tue Jul 17 13:38:06 CST 2018
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value
	 * of the database column customer_message.send_time
	 * 
	 * @return the value of customer_message.send_time
	 * @mbg.generated Tue Jul 17 13:38:06 CST 2018
	 */
	public Date getSendTime() {
		return sendTime;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of
	 * the database column customer_message.send_time
	 * 
	 * @param sendTime the value for customer_message.send_time
	 * @mbg.generated Tue Jul 17 13:38:06 CST 2018
	 */
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value
	 * of the database column customer_message.player_id
	 * 
	 * @return the value of customer_message.player_id
	 * @mbg.generated Tue Jul 17 13:38:06 CST 2018
	 */
	public Long getPlayerId() {
		return playerId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of
	 * the database column customer_message.player_id
	 * 
	 * @param playerId the value for customer_message.player_id
	 * @mbg.generated Tue Jul 17 13:38:06 CST 2018
	 */
	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	public String getPhone() {
		return phone;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of
	 * the database column customer_message.phone
	 *
	 * @param phone the value for customer_message.phone
	 *
	 * @mbg.generated Mon Aug 06 11:13:07 CST 2018
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
}