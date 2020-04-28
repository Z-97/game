/*
 * Copyright (c) 2015, Alex All Rights Reserved. 
 * 
 */  
package com.alex.game.dbdic.dom;

/**  
 * @author exccel-generator
 *
 */
public class GameDom {

	// id
	private int id;
	// 游戏名称
	private String name;
	// 描述
	private String desc;
	// 状态(0关闭1开启2暂未开放)
	private int state;
	// 游戏显示顺序
	private int seq;
	
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
	
	public String getDesc(){
		return desc;
	}
	
	public void setDesc(String desc){
		this.desc = desc;
	}
	
	public int getState(){
		return state;
	}
	
	public void setState(int state){
		this.state = state;
	}
	
	public int getSeq(){
		return seq;
	}
	
	public void setSeq(int seq){
		this.seq = seq;
	}
	
}
