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
import com.alex.game.dbdic.dom.GameDom;
import com.alex.game.dbdic.mapper.GameMapper;

@Singleton
public class GameDic implements Dictionary<GameDom> {
	
	private GameMapper mapper;
	private List<GameDom> list;
	private Map<Integer, GameDom> map;
	
	@Inject
	public GameDic(GameMapper mapper) {
		this.mapper = mapper;
		load();
	}
	
	@Override
	public void load() {
		this.list = mapper.selectAll();
		Map<Integer, GameDom> map = new HashMap<>(); 
		list.forEach(val -> map.put(val.getId(), val));
		this.map = map;
	}
	
	@Override
	public List<GameDom> values() {
		
		return list;
	}
	
	@Override
	public GameDom get(int id) {
		return map.get(id);
	}
}
