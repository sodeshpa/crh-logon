/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.persistence.dao.util;

import java.io.Serializable;

/**
 * @author AdNovum Informatik AG
 */
public class ColumnRestriction implements Serializable {
	
	private String columnName;
	private Object columnValue;
	private DAOExpression expr;
	
	public ColumnRestriction(Object columnValue, String columnName, DAOExpression expr) {
		this.columnValue = columnValue;
		this.columnName = columnName;
		this.expr = expr;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public Object getColumnValue() {
		return columnValue;
	}

	public void setColumnValue(Object columnValue) {
		this.columnValue = columnValue;
	}

	public DAOExpression getExpr() {
		return expr;
	}

	public void setExpr(DAOExpression expr) {
		this.expr = expr;
	}

}
