/*
 * Copyright (c) 2015, Alex All Rights Reserved. 
 * 
 */  
package com.alex.game.dbdic.dic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.alex.game.dbdic.core.Dictionary;
import com.alex.game.dbdic.dom.SysConfigDom;
import com.alex.game.dbdic.mapper.SysConfigMapper;
import com.alibaba.fastjson.JSONObject;
@Singleton
public class SysConfigDic implements Dictionary<SysConfigDom> {
	
	private SysConfigMapper mapper;
	private List<SysConfigDom> list;
	private Map<Integer, SysConfigDom> map;
	
	@Inject
	public SysConfigDic(SysConfigMapper mapper) {
		this.mapper = mapper;
		load();
	}
	
	@Override
	public void load() {
		this.list = mapper.selectAll();
		Map<Integer, SysConfigDom> map = new HashMap<>(); 
		list.forEach(val -> map.put(val.getId(), val));
		this.map = map;
	}
	
	@Override
	public List<SysConfigDom> values() {
		
		return list;
	}
	
	@Override
	public SysConfigDom get(int id) {
		return map.get(id);
	}
	
	/**
	 * 获取int值
	 */
	public int getIntVal(int id) {
		
		return Integer.parseInt(get(id).getVal());
	}
	
	/**
	 * 获取浮点值
	 */
	public double getDoubleVal(int id) {
		
		return Double.parseDouble(get(id).getVal());
	}
	
	/**
	 * 获取bool值
	 */
	public boolean getBoolVal(int id) {
		
		return Boolean.parseBoolean(get(id).getVal());
	}
	
	/**
	 * 获取string值
	 */
	public String getStrVal(int id) {
		
		return get(id).getVal();
	}
	
	public JSONObject  getJsonObjectVal(int id){
		return JSONObject.parseObject(getStrVal(id));
	}
}
