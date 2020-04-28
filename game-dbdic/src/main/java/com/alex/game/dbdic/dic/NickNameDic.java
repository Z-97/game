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
import com.alex.game.dbdic.dom.NickNameDom;
import com.alex.game.dbdic.mapper.NickNameMapper;

@Singleton
public class NickNameDic implements Dictionary<NickNameDom> {
	
	private NickNameMapper mapper;
	private List<NickNameDom> list;
	private Map<Integer, NickNameDom> map;
	
	@Inject
	public NickNameDic(NickNameMapper mapper) {
		this.mapper = mapper;
		load();
	}
	
	@Override
	public void load() {
		this.list = mapper.selectAll();
		Map<Integer, NickNameDom> map = new HashMap<>(); 
		list.forEach(val -> map.put(val.getId(), val));
		this.map = map;
	}
	
	@Override
	public List<NickNameDom> values() {
		return list;
	}
	
	@Override
	public NickNameDom get(int id) {
		return map.get(id);
	}
}
