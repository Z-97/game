/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.dblog.core;

import java.util.Date;

import com.alex.game.dblog.core.annotation.Column;

/**
 * 日志类基类，子类不能出现id列,所有的字段必须是public的且被Column注解
 *
 * @author Alex
 * @date 2016/7/4 9:53
 */
public abstract class DbLog {
	/**
	 * 由日志系统插入
	 */
	@Column(name="time", type = "datetime", remark = "时间")
	public Date time = new Date();
	
}
