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
import com.alex.game.dbdic.dom.ItemDom;
import com.alex.game.dbdic.mapper.ItemMapper;

@Singleton
public class ItemDic implements Dictionary<ItemDom> {
	
	private ItemMapper mapper;
	private List<ItemDom> list;
	private Map<Integer, ItemDom> map;
	
	@Inject
	public ItemDic(ItemMapper mapper) {
		this.mapper = mapper;
		load();
	}
	
	@Override
	public void load() {
		this.list = mapper.selectAll();
		Map<Integer, ItemDom> map = new HashMap<>(); 
		list.forEach(val -> map.put(val.getId(), val));
		this.map = map;
	}
	
	@Override
	public List<ItemDom> values() {
		
		return list;
	}
	
	@Override
	public ItemDom get(int id) {
		return map.get(id);
	}
}
