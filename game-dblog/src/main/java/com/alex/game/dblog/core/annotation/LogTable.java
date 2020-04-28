/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.dblog.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.alex.game.dblog.core.TableType;

/**
 * 日志表生成策略
 *
 * @author Alex
 * @date 2016/7/1 18:45
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface LogTable {
	// 表名
	public String name();
	public TableType type() default TableType.SINGLE;

}
