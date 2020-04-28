/*
 * Copyright (c) 2015, Alex All Rights Reserved. 
 * 
 */  
package com.alex.game.dbdic.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.alex.game.dbdic.dom.NickNameDom;

public interface NickNameMapper {

	@Select("select * from nick_name")
	List<NickNameDom> selectAll();
}
