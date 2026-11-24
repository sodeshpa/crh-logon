/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.reporting;

/**
 * @author AdNovum Informatik AG
 */
public class LoadShippingStatText extends LoadShippingStats {

	public static final String ATTR_PRODUCTION_PLANT = "PRODUCTION_PLANT";
	protected String getWhereClause() {
		
		String superWhereClause = super.getWhereClause();
		StringBuilder whereClauseSb = superWhereClause != null ? new StringBuilder(superWhereClause) : null;

		whereClauseSb = whereClauseSb!=null?whereClauseSb.append(" AND "):new StringBuilder();
		whereClauseSb.append(ATTR_PRODUCTION_PLANT + " is not null");
		
		
		return whereClauseSb!=null?whereClauseSb.toString():null;
	}

}
