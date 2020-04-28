/*
 * Copyright (c) 2015, Alex All Rights Reserved. 
 * 
 */  
package com.alex.game.dbdic.dom;

/**  
 * @author exccel-generator
 *
 */
public class DiceRoomDom {

	// 房间id
	private int id;
	// 房间名称
	private String name;
	// 进入下限(金币)
	private int lower;
	// 机器人人数
	private int robotNum;
	// 机器人携带金币下限
	private int robotLowerGold;
	// 机器人携带金币上限
	private int robotUpperGold;
	// 下注下限百分比
	private int robotBetLowerRate;
	// 下注上限百分比
	private int robotBetUpperRate;
	// 机器人下注局数下限
	private int robotLowerRound;
	// 机器人下注局数上限
	private int robotUpperRound;
	// 桌子
	private int table;
	// 桌子最大人数
	private int tablePlayerNum;
	// 玩家时间顺序上庄机制
	private int BankerSortGold;
	// 玩家最低做庄金币
	private int bankerLowGold;
	// 庄家局数
	private int bankerRound;
	// 系统坐庄金币
	private int sysBankerGold;
	// 税率百分比
	private int taxRate;
	// 下注筹码(金币)规则
	private String ableBetChips;
	// 房间控制概率
	private int controlRate;
	// 豹子红包Id
	private int redPackageId;
	
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
	
	public int getLower(){
		return lower;
	}
	
	public void setLower(int lower){
		this.lower = lower;
	}
	
	public int getRobotNum(){
		return robotNum;
	}
	
	public void setRobotNum(int robotNum){
		this.robotNum = robotNum;
	}
	
	public int getRobotLowerGold(){
		return robotLowerGold;
	}
	
	public void setRobotLowerGold(int robotLowerGold){
		this.robotLowerGold = robotLowerGold;
	}
	
	public int getRobotUpperGold(){
		return robotUpperGold;
	}
	
	public void setRobotUpperGold(int robotUpperGold){
		this.robotUpperGold = robotUpperGold;
	}
	
	public int getRobotBetLowerRate(){
		return robotBetLowerRate;
	}
	
	public void setRobotBetLowerRate(int robotBetLowerRate){
		this.robotBetLowerRate = robotBetLowerRate;
	}
	
	public int getRobotBetUpperRate(){
		return robotBetUpperRate;
	}
	
	public void setRobotBetUpperRate(int robotBetUpperRate){
		this.robotBetUpperRate = robotBetUpperRate;
	}
	
	public int getRobotLowerRound(){
		return robotLowerRound;
	}
	
	public void setRobotLowerRound(int robotLowerRound){
		this.robotLowerRound = robotLowerRound;
	}
	
	public int getRobotUpperRound(){
		return robotUpperRound;
	}
	
	public void setRobotUpperRound(int robotUpperRound){
		this.robotUpperRound = robotUpperRound;
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
	
	public int getBankerSortGold(){
		return BankerSortGold;
	}
	
	public void setBankerSortGold(int BankerSortGold){
		this.BankerSortGold = BankerSortGold;
	}
	
	public int getBankerLowGold(){
		return bankerLowGold;
	}
	
	public void setBankerLowGold(int bankerLowGold){
		this.bankerLowGold = bankerLowGold;
	}
	
	public int getBankerRound(){
		return bankerRound;
	}
	
	public void setBankerRound(int bankerRound){
		this.bankerRound = bankerRound;
	}
	
	public int getSysBankerGold(){
		return sysBankerGold;
	}
	
	public void setSysBankerGold(int sysBankerGold){
		this.sysBankerGold = sysBankerGold;
	}
	
	public int getTaxRate(){
		return taxRate;
	}
	
	public void setTaxRate(int taxRate){
		this.taxRate = taxRate;
	}
	
	public String getAbleBetChips(){
		return ableBetChips;
	}
	
	public void setAbleBetChips(String ableBetChips){
		this.ableBetChips = ableBetChips;
	}
	
	public int getControlRate(){
		return controlRate;
	}
	
	public void setControlRate(int controlRate){
		this.controlRate = controlRate;
	}
	
	public int getRedPackageId(){
		return redPackageId;
	}
	
	public void setRedPackageId(int redPackageId){
		this.redPackageId = redPackageId;
	}
	
}
