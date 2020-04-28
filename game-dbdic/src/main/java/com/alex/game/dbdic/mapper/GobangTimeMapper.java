/*
 * Copyright (c) 2015, Alex All Rights Reserved. 
 * 
 */  
package com.alex.game.dbdic.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.alex.game.dbdic.dom.GobangTimeDom;

public interface GobangTimeMapper {

	@Select("select * from gobang_time")
	List<GobangTimeDom> selectAll();
}
