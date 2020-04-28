/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.dblog.core.annotation;

import java.lang.annotation.*;

/**
 * 日志表列注解
 *
 * @author Alex
 * @date 2016/7/1 18:45
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface Column {
	// 列名称
	public String name();
	// 列类型，包含大小
	public String type();
	// 列备注
	public String remark();
}
