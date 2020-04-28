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
import com.alex.game.dbdic.dic.domwrapper.RedPackageDomWrapper;
import com.alex.game.dbdic.dom.RedPackageDom;
import com.alex.game.dbdic.mapper.RedPackageMapper;

@Singleton
public class RedPackageDic implements Dictionary<RedPackageDomWrapper> {
	
	private RedPackageMapper mapper;
	private List<RedPackageDomWrapper> list;
	private Map<Integer, RedPackageDomWrapper> map;
	
	@Inject
	public RedPackageDic(RedPackageMapper mapper) {
		this.mapper = mapper;
		load();
	}
	
	@Override
	public void load() {
		List<RedPackageDomWrapper> list = new ArrayList<>();
		Map<Integer, RedPackageDomWrapper> map = new HashMap<>(); 
		
		for (RedPackageDom dom : mapper.selectAll()) {
			RedPackageDomWrapper domWrapper = new RedPackageDomWrapper(dom);
			list.add(domWrapper);
			map.put(domWrapper.getId(), domWrapper);
		}
		
		this.list = list;
		this.map = map;
	}
	
	@Override
	public List<RedPackageDomWrapper> values() {
		
		return list;
	}
	
	@Override
	public RedPackageDomWrapper get(int id) {
		return map.get(id);
	}
}
