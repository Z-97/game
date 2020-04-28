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
import com.alex.game.dbdic.dom.GobangTimeDom;
import com.alex.game.dbdic.mapper.GobangTimeMapper;

@Singleton
public class GobangTimeDic implements Dictionary<GobangTimeDom> {
	
	private GobangTimeMapper mapper;
	private List<GobangTimeDom> list;
	private Map<Integer, GobangTimeDom> map;
	
	@Inject
	public GobangTimeDic(GobangTimeMapper mapper) {
		this.mapper = mapper;
		load();
	}
	
	@Override
	public void load() {
		this.list = mapper.selectAll();
		Map<Integer, GobangTimeDom> map = new HashMap<>(); 
		list.forEach(val -> map.put(val.getId(), val));
		this.map = map;
	}
	
	@Override
	public List<GobangTimeDom> values() {
		
		return list;
	}
	
	@Override
	public GobangTimeDom get(int id) {
		return map.get(id);
	}
}
