/*
 * Copyright (c) 2015, Alex All Rights Reserved. 
 * 
 */  
package com.alex.game.dbdic.dom;

/**  
 * @author exccel-generator
 *
 */
public class GobangRoomDom {

	// 房间id
	private int id;
	// 房间名称
	private String name;
	// 桌子
	private int table;
	// 桌子最大人数
	private int tablePlayerNum;
	// 步时
	private int stepTime;
	
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
	
	public int getTable(){
		return table;
	}
	
	public void setTable(int table){
		this.table = table;
	}
	
	public int getTablePlayerNum(){
		return tablePlayerNum;
	}
	
	public void setTablePlayerNum(int tablePlayerNum){
		this.tablePlayerNum = tablePlayerNum;
	}
	
	public int getStepTime(){
		return stepTime;
	}
	
	public void setStepTime(int stepTime){
		this.stepTime = stepTime;
	}
	
}
