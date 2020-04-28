/*
 * Copyright (c) 2015, Alex All Rights Reserved. 
 * 
 */  
package com.alex.game.dbdic.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.alex.game.dbdic.dom.GobangRoomDom;

public interface GobangRoomMapper {

	@Select("select * from gobang_room")
	List<GobangRoomDom> selectAll();
}
