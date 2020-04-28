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
import com.alex.game.dbdic.dom.DiceTimeDom;
import com.alex.game.dbdic.mapper.DiceTimeMapper;

@Singleton
public class DiceTimeDic implements Dictionary<DiceTimeDom> {
	
	private DiceTimeMapper mapper;
	private List<DiceTimeDom> list;
	private Map<Integer, DiceTimeDom> map;
	
	@Inject
	public DiceTimeDic(DiceTimeMapper mapper) {
		this.mapper = mapper;
		load();
	}
	
	@Override
	public void load() {
		this.list = mapper.selectAll();
		Map<Integer, DiceTimeDom> map = new HashMap<>(); 
		list.forEach(val -> map.put(val.getId(), val));
		this.map = map;
	}
	
	@Override
	public List<DiceTimeDom> values() {
		return list;
	}
	
	@Override
	public DiceTimeDom get(int id) {
		return map.get(id);
	}
}
