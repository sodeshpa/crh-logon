package com.crh.logon.admin.web.ajax.util;

import com.crh.logon.utils.StringUtils;

public class OrderBy {
	
	private static final String ORDERBY_FIELD_AND_DIRECTION_SEPARATOR = " ";
	
	private static final String ORDER_ASCENDING_KEY = "ASC";
	
	private String field;
	
	private boolean ascending;	
	
	public OrderBy(String orderBy, String defaultColumn) {
		if (defaultColumn == null) {
			throw new IllegalArgumentException("Default column must not be null");
		}
		if (StringUtils.notEmpty(orderBy)) {
			//in received ajax request the first part is the column to order by second part is the direction
			String[] orderByFieldAndDirection = orderBy.split(ORDERBY_FIELD_AND_DIRECTION_SEPARATOR);
			field = orderByFieldAndDirection[0];
			if (orderByFieldAndDirection.length == 2) {
				ascending = ORDER_ASCENDING_KEY.equalsIgnoreCase(orderByFieldAndDirection[1]);
			}
		}
		else {
			field = defaultColumn;
		}
	}
	
	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}


}
