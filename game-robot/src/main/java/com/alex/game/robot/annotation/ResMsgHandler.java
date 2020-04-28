/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.robot.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ResHandler注解，通过扫描ResHandler注入到guice容器中
 * 
 * @author Alex
 * @date 2016/7/26 20:50
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ TYPE })
public @interface ResMsgHandler {
	int value();
}
