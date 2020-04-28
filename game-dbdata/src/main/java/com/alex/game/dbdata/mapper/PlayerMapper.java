/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.dbdata.mapper;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.alex.game.dbdata.dom.PlayerDom;
import com.alex.game.dbdata.dom.PlayerRankDom;
import com.alex.game.dbdata.dom.PlayerRobotDom;

/**
 * PlayerMapper mybatise
 * 
 * @author Alexte 2015年8月25日 下午4:16:25
 *
 */
public interface PlayerMapper {


	/**
	 * 获取所有玩家机器人属性
	 * 
	 * @return
	 */
	List<PlayerRobotDom> selectRobots(@Param("from") int from, @Param("limit") int limit);
	/**
	 * 根据玩家id获取
	 * 
	 * @param id
	 * @return
	 */
	PlayerDom selectById(@Param("id") long id);
	
	/**
	 * 根据用户名获取
	 * 
	 * @param id
	 * @return
	 */
	PlayerDom selectByUserName(@Param("userName") String userName);
	
	/**
	 * 根据用户名获取
	 * 
	 * @param id
	 * @return
	 */
	PlayerDom selectByPhone(@Param("phone") String phone);
	/**
	 * 根据银行卡
	 * @param bankCard
	 * @return
	 */
	PlayerDom selectByBankCard(@Param("bankCard") String bankCard);
	/**
	 * 根据mac查询游客
	 * 
	 * @param userName
	 * @return
	 */
	PlayerDom selectByTouristMac(@Param("mac") String mac);
	
	/**
	 * 查询登录或者登出时间大于activeTime的玩家
	 * 
	 * @param activeTime
	 * @return
	 */
	List<PlayerDom> selectActivePlayers(@Param("activeTime") Date activeTime);
	
	/**
	 * 查询代理金币（银行+身上）排行
	 * @return
	 */
	List<PlayerRankDom> selectAgentByGold();
	/**
	 * 新增玩家数据
	 * 
	 * @param playerDom
	 * @return
	 */
	int insert(PlayerDom playerDom);

	/**
	 * 根据玩家id更新数据
	 * 
	 * @param playerDom
	 * @return
	 */
	int updateById(PlayerDom playerDom);
	

	
	/***
	 * 更新玩家渠道id
	 * @param newChannelId
	 * @param oldChannelId
	 * @return
	 */                      
	int updatePlayerChannelId(@Param("newChannelId")String newChannelId, @Param("oldChannelId")String oldChannelId,@Param("packageId")String packageId);
	
	/**
	 * 根据注册ip更新locked
	 * @param locked
	 * @param registerIp
	 * @return
	 */
	int updateLockedByRegisterIp(@Param("locked")boolean locked, @Param("ip")String ip);
	
	/**
	 * 根据注册mac更新locked
	 * @param locked
	 * @param registerIp
	 * @return
	 */
	int updateLockedByRegisterMac(@Param("locked")boolean locked, @Param("mac")String mac);
	
	

	/**
	 * 重置在线标识，比如服务器异常官服导致数据库在线字段未被修改，在开发阶段由于可能共用一个库，可不清除
	 * 
	 * @return
	 */
	int resetOnline();
	
	/**
	 * 根据玩家id获取
	 * 
	 * @param id
	 * @return
	 */
	int selectNumByAlipay(@Param("alipay") String alipay);
	
	
	
}