/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.common;

/**
 * 符号常量
 *
 * @author Alex
 * @date 2016/7/5 17:55
 */
public enum Symbol {
	LBRACE("{"), 
	RBRACE("}"), 
	LBRACKET("["), 
	RBRACKET("]"), 
	COMMA(","), 
	COLON(":"),
	;

	public final String val;

	Symbol(String val) {
		this.val = val;
	}
}
