/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 游戏枚举
 *
 * @author Alex
 * @date 2016/6/28 21:29
 */
public enum Game {

	GOBANG(1, "gobang", "五子棋"),
	/**
	 * 骰子
	 */
	DICE(2, "dice", "骰子");
	// 游戏id
	public final int id;
	// 游戏模块id，通过它可以获取游戏线程执行器
	public final String moduleId;
	// 游戏描述
	public final String desc;

	private Game(int id, String moduleId, String desc) {
		this.id = id;
		this.moduleId = moduleId;
		this.desc = desc;
	}

	@Override
	public String toString() {
		return id + "_" + desc;
	}

	private static final Map<Integer, Game> games;

	static {
		games = new HashMap<>(48);
		for (Game game : values()) {
			games.put(game.id, game);
		}
	}

	/**
	 * 根据game id获取Game
	 *
	 * @param id
	 * @return
	 */
	public static Game getGame(int id) {

		return games.get(id);
	}

}
