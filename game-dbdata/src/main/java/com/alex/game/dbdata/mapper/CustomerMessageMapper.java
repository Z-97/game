package com.alex.game.dbdata.mapper;

import java.util.List;
import com.alex.game.dbdata.dom.CustomerMessage;

public interface CustomerMessageMapper {
	int deleteByPrimaryKey(Long id);

	int insert(CustomerMessage record);

	CustomerMessage selectByPrimaryKey(Long id);

	List<CustomerMessage> selectByPlayerId(long playerId);

	List<CustomerMessage> selectAll();

	int updateByPrimaryKey(CustomerMessage record);
}