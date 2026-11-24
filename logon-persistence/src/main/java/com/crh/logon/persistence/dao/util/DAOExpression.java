/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.persistence.dao.util;

/**
 * @author AdNovum Informatik AG
 */
public enum DAOExpression {
	EQUAL("="), 
	NOT_EQUAL("!="), 
	SMALLER("<"), 
	SMALLER_OR_EQUAL("<="), 
	GREATER(">"), 
	GREATER_OR_EQUAL(">="),
	IS("is");
	
	private String exprType;
	
	DAOExpression(String exprType) {
		this.exprType = exprType;
	}
	
	public String toString() {
		return this.exprType;
	}
}
