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
import com.alex.game.dbdic.dom.GobangRoomDom;
import com.alex.game.dbdic.mapper.GobangRoomMapper;

@Singleton
public class GobangRoomDic implements Dictionary<GobangRoomDom> {
	
	private GobangRoomMapper mapper;
	private List<GobangRoomDom> list;
	private Map<Integer, GobangRoomDom> map;
	
	@Inject
	public GobangRoomDic(GobangRoomMapper mapper) {
		this.mapper = mapper;
		load();
	}
	
	@Override
	public void load() {
		this.list = mapper.selectAll();
		Map<Integer, GobangRoomDom> map = new HashMap<>(); 
		list.forEach(val -> map.put(val.getId(), val));
		this.map = map;
	}
	
	@Override
	public List<GobangRoomDom> values() {
		
		return list;
	}
	
	@Override
	public GobangRoomDom get(int id) {
		return map.get(id);
	}
}
