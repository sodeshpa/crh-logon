/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.reporting;

import javax.servlet.http.HttpServletRequest;

import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
public class StagingAreaErrors extends ReportMetaData {

	private String queryString;

	@Override
	protected String getWhereClause() {
		// Not used.
		return null;
	}
	
	public Object[] getQParams() {
		// Not used.
		return null;
	}
	
	public String getQueryString() {
		
		return queryString;
	}

	public void init(ServiceLocator serviceLocator, long uid) {
		
		String rawQueryString = getRawQueryString();
		if (rawQueryString == null) {
			throw new LogonException("Report query not found", Reason.RES_ERROR_IMPL);
		}
		
		queryString = rawQueryString;

	}

	@Override
	public void setupFilters(HttpServletRequest request) {
		// Not used.
	}
}
