/*
 * Copyright (c) 2015, Alex All Rights Reserved. 
 * 
 */  
package com.alex.game.dbdic.dom;

/**  
 * @author exccel-generator
 *
 */
public class RedPackageDom {
	// id
	private int id;
	// 红包名称
	private String name;
	// 红包类型
	private int redType;
	// 总金额
	private int sum;
	// 数量
	private int num;
	// 红包语言
	private String redContent;
	
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
	
	public int getRedType(){
		return redType;
	}
	
	public void setRedType(int redType){
		this.redType = redType;
	}
	
	public int getSum(){
		return sum;
	}
	
	public void setSum(int sum){
		this.sum = sum;
	}
	
	public int getNum(){
		return num;
	}
	
	public void setNum(int num){
		this.num = num;
	}
	
	public String getRedContent(){
		return redContent;
	}
	
	public void setRedContent(String redContent){
		this.redContent = redContent;
	}
	
}
