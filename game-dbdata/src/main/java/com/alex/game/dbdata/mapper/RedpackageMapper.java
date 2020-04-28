package com.alex.game.dbdata.mapper;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.alex.game.dbdata.dom.Redpackage;
public interface RedpackageMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(Redpackage record);

	Redpackage selectByPrimaryKey(Integer id);

	List<Redpackage> selectAll();

	int updateByPrimaryKey(Redpackage record);

	List<Redpackage> selectActiveRedpackage(@Param("activeTime") Date activeTime);
}