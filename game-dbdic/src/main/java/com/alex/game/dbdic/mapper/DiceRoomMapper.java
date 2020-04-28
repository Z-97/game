/*
 * Copyright (c) 2015, Alex All Rights Reserved. 
 * 
 */  
package com.alex.game.dbdic.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.alex.game.dbdic.dom.DiceRoomDom;

public interface DiceRoomMapper {

	@Select("select * from dice_room")
	List<DiceRoomDom> selectAll();
}
