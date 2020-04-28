/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.dbdic.core;

import java.util.List;

/**
 * 字典表接口
 * 
 * @author Alex
 * @date 2016年12月21日 下午2:27:30
 */
public interface Dictionary<E> {

	/**
	 * 字典所有数据
	 *
	 * @return
	 */
	List<E> values();

	/**
	 * 加载数据
	 */
	void load();

	/**
	 * 根据id获取数据
	 *
	 * @param id
	 * @return
	 */
	E get(int id);
}
