/*
 * Copyright (c) 2015, Alex All Rights Reserved. 
 * 
 */  
package com.alex.game.dbdic.dom;

/**  
 * @author exccel-generator
 *
 */
public class SysConfigDom {

	// id
	private int id;
	// 0:普通类型,1:开关类型
	private int type;
	// 名称
	private String val;
	// 值
	private String desc;
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public int getType(){
		return type;
	}
	
	public void setType(int type){
		this.type = type;
	}
	
	public String getVal(){
		return val;
	}
	
	public void setVal(String val){
		this.val = val;
	}
	
	public String getDesc(){
		return desc;
	}
	
	public void setDesc(String desc){
		this.desc = desc;
	}
	
}
