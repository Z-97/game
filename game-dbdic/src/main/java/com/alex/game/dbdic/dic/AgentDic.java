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
import com.alex.game.dbdic.dom.AgentDom;
import com.alex.game.dbdic.mapper.AgentMapper;

@Singleton
public class AgentDic implements Dictionary<AgentDom> {
	
	private AgentMapper mapper;
	private List<AgentDom> list;
	private Map<Integer, AgentDom> map;
	
	@Inject
	public AgentDic(AgentMapper mapper) {
		this.mapper = mapper;
		load();
	}
	
	@Override
	public void load() {
		this.list = mapper.selectAll();
		Map<Integer, AgentDom> map = new HashMap<>(); 
		list.forEach(val -> map.put(val.getId(), val));
		this.map = map;
	}
	
	@Override
	public List<AgentDom> values() {
		
		return list;
	}
	
	@Override
	public AgentDom get(int id) {
		return map.get(id);
	}
}
