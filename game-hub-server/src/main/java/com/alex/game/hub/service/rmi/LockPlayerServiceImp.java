package com.alex.game.hub.service.rmi;

import java.rmi.RemoteException;

import com.alex.game.rmi.service.LockPlayerService;

/**
 * LockPlayerServiceImp
 * 
 * @author Alex
 * @date 2017年5月15日 下午5:06:47
 */
public class LockPlayerServiceImp extends java.rmi.server.UnicastRemoteObject implements LockPlayerService {

	private static final long serialVersionUID = 1L;

	protected LockPlayerServiceImp() throws RemoteException {
		super();
	}

	@Override
	public boolean lockPlayer(long playerId) {
		return true;
	}

}
