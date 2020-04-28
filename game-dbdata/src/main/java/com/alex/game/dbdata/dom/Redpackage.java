package com.alex.game.dbdata.dom;

import java.util.Date;

public class Redpackage {
  
	private Integer id;

	private Date createTime;

	private Date completeTime;

	// 总金额
	private Long sum;

	private Integer num;

	private String redpackageDesc;

	// 剩余金额
	private long remainder;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCompleteTime() {
		return completeTime;
	}

	public void setCompleteTime(Date completeTime) {
		this.completeTime = completeTime;
	}

	public Long getSum() {
		return sum;
	}

	public void setSum(Long sum) {
		this.sum = sum;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public String getRedpackageDesc() {
		return redpackageDesc;
	}

	public void setRedpackageDesc(String redpackageDesc) {
		this.redpackageDesc = redpackageDesc;
	}

	public long getRemainder() {
		return remainder;
	}

	public void setRemainder(long remainder) {
		this.remainder = remainder;
	}
   
}