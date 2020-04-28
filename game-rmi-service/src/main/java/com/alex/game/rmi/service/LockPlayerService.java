package com.alex.game.rmi.service;

/**
 * 封锁账号service
 * 
 * @author Alex
 * @date 2017年5月15日 下午5:00:59
 */
public interface LockPlayerService extends java.rmi.Remote {

	/**
	 * 封锁账号
	 * @param playerId
	 * @return	true成功,false:玩家不存在
	 */
	boolean lockPlayer(long playerId);
}
