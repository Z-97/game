package com.alex.game.dbdic.dic.domwrapper;

import java.util.TreeSet;

import com.alex.game.dbdic.dom.DiceRoomDom;
import com.alibaba.fastjson.JSON;

public class DiceRoomDicWrapper extends DiceRoomDom {
	// 可以下注的筹码
	private final TreeSet<Long> ableBetChips;
	private final DiceRoomDom dom;
	public int getBankerSortGold() {
		return dom.getBankerSortGold();
	}
	public void setBankerSortGold(int BankerSortGold) {
		dom.setBankerSortGold(BankerSortGold);
	}
	public DiceRoomDicWrapper(DiceRoomDom dom) {
		this.dom=dom;
		this.ableBetChips = new TreeSet<>(JSON.parseArray("[" +dom.getAbleBetChips() + "]" , Long.class));
	}
	public int getBankerLowGold() {
		return dom.getBankerLowGold();
	}
	public void setBankerLowGold(int bankerLowGold) {
		dom.setBankerLowGold(bankerLowGold);
	}
	public TreeSet<Long> ableBetChips() {
		return ableBetChips;
	}
	public int getId() {
		return dom.getId();
	}
	public void setId(int id) {
		dom.setId(id);
	}
	public String getName() {
		return dom.getName();
	}
	public void setName(String name) {
		dom.setName(name);
	}
	public int getLower() {
		return dom.getLower();
	}
	public void setLower(int lower) {
		dom.setLower(lower);
	}
	public int getRobotNum() {
		return dom.getRobotNum();
	}
	public void setRobotNum(int robotNum) {
		dom.setRobotNum(robotNum);
	}
	public int getRobotLowerGold() {
		return dom.getRobotLowerGold();
	}
	public void setRobotLowerGold(int robotLowerGold) {
		dom.setRobotLowerGold(robotLowerGold);
	}
	public int getRobotUpperGold() {
		return dom.getRobotUpperGold();
	}
	public void setRobotUpperGold(int robotUpperGold) {
		dom.setRobotUpperGold(robotUpperGold);
	}
	public int getRobotBetLowerRate() {
		return dom.getRobotBetLowerRate();
	}
	public void setRobotBetLowerRate(int robotBetLowerRate) {
		dom.setRobotBetLowerRate(robotBetLowerRate);
	}
	public int getRobotBetUpperRate() {
		return dom.getRobotBetUpperRate();
	}
	public void setRobotBetUpperRate(int robotBetUpperRate) {
		dom.setRobotBetUpperRate(robotBetUpperRate);
	}
	public int getTable() {
		return dom.getTable();
	}
	public void setTable(int table) {
		dom.setTable(table);
	}
	public int getTablePlayerNum() {
		return dom.getTablePlayerNum();
	}
	public void setTablePlayerNum(int tablePlayerNum) {
		dom.setTablePlayerNum(tablePlayerNum);
	}

	public int getBankerRound() {
		return dom.getBankerRound();
	}
	public void setBankerRound(int bankerRound) {
		dom.setBankerRound(bankerRound);
	}
	public int getSysBankerGold() {
		return dom.getSysBankerGold();
	}
	public void setSysBankerGold(int sysBankerGold) {
		dom.setSysBankerGold(sysBankerGold);
	}
	public int getTaxRate() {
		return dom.getTaxRate();
	}
	public void setTaxRate(int taxRate) {
		dom.setTaxRate(taxRate);
	}
	public String getAbleBetChips() {
		return dom.getAbleBetChips();
	}
	public void setAbleBetChips(String ableBetChips) {
		dom.setAbleBetChips(ableBetChips);
	}

	public int getControlRate() {
		return dom.getControlRate();
	}
	public void setControlRate(int controlRate) {
		dom.setControlRate(controlRate);
	}
	public int getRobotLowerRound() {
		return dom.getRobotLowerRound();
	}
	public void setRobotLowerRound(int robotLowerRound) {
		dom.setRobotLowerRound(robotLowerRound);
	}
	public int getRobotUpperRound() {
		return dom.getRobotUpperRound();
	}
	public void setRobotUpperRound(int robotUpperRound) {
		dom.setRobotUpperRound(robotUpperRound);
	}
	public int getRedPackageId() {
		return dom.getRedPackageId();
	}
	public void setRedPackageId(int redPackageId) {
		dom.setRedPackageId(redPackageId);
	}
}
