/*
 * Copyright (c) 2015, Alex All Rights Reserved. 
 * 
 */  
package com.alex.game.dbdic.dom;

/**  
 * @author exccel-generator
 *
 */
public class AgentDom {

	// id
	private int id;
	// 商品名称
	private String name;
	// 微信号吗
	private String wx;
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getWx(){
		return wx;
	}
	
	public void setWx(String wx){
		this.wx = wx;
	}
	
}
