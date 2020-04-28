/*
 * Copyright (c) 2015, Alex All Rights Reserved. 
 * 
 */  
package com.alex.game.dbdic.dic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.alex.game.dbdic.core.Dictionary;
import com.alex.game.dbdic.dic.domwrapper.DiceRoomDicWrapper;
import com.alex.game.dbdic.dom.DiceRoomDom;
import com.alex.game.dbdic.mapper.DiceRoomMapper;

@Singleton
public class DiceRoomDic implements Dictionary<DiceRoomDicWrapper> {
	
	private DiceRoomMapper mapper;
	private List<DiceRoomDicWrapper> list;
	private Map<Integer, DiceRoomDicWrapper> map;
	
	@Inject
	public DiceRoomDic(DiceRoomMapper mapper) {
		this.mapper = mapper;
		load();
	}
	
	@Override
	public void load() {
		List<DiceRoomDicWrapper> list = new ArrayList<>();
		Map<Integer, DiceRoomDicWrapper> map = new HashMap<>(); 
		
		for (DiceRoomDom dom : mapper.selectAll()) {
			DiceRoomDicWrapper domWrapper = new DiceRoomDicWrapper(dom);
			list.add(domWrapper);
			map.put(domWrapper.getId(), domWrapper);
		}
		
		this.list = list;
		this.map = map;
		
	}
	
	@Override
	public List<DiceRoomDicWrapper> values(){
		return list;
	}
	
	@Override
	public DiceRoomDicWrapper get(int id) {
		return map.get(id);
	}
}
