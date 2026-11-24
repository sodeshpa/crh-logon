/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.reporting;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;


/**
 * @author AdNovum Informatik AG
 */
public class OrderDataPerRouteCarrier extends OrderReportMetaData {
	
	private Object[] qParams;
	private String queryString;
	
	public Object[] getQParams() {
		
		return qParams;
	}
	
	public String getQueryString() {
		
		return queryString;
	}
	
	public void init(ServiceLocator serviceLocator, long uid) {
		
		List qParamList = new ArrayList();
		List filterQParamList = new ArrayList(Arrays.asList(super.getQParams()));
				
		List qFormatParams = new ArrayList();
		qFormatParams.add(getWhereClause());
		// WHERE ({0}) AND ({1}) AND ({2}) AND ({3}) AND ({4})
		qParamList.addAll(filterQParamList);
		addAccessRestriction(ExportMetaData.USER_SOLDTO_BP_ACC, "oprc.O_SOLDTO_SAP_BP_ID", qParamList, qFormatParams, serviceLocator, uid);
		addAccessRestriction(ExportMetaData.USER_SHIPTO_ACC, "oprc.O_SHIPTO_SAP_BP_ID", qParamList, qFormatParams, serviceLocator, uid);
		addAccessRestriction(ExportMetaData.USER_SAP_VENDOR_ACC, "oprc.COMM_CARRIER_SAP_VENDOR_ID", qParamList, qFormatParams, serviceLocator, uid);
		addAccessRestriction(ExportMetaData.USER_SHIPPINGPOINT_ACC, "oprc.O_SHIPPINGPOINT_ID", qParamList, qFormatParams, serviceLocator, uid);
		
		MessageFormat formatter = new MessageFormat("");
		
		String rawQueryString = getRawQueryString();
		if (rawQueryString == null) {
			
			throw new LogonException("Report query not found", Reason.RES_ERROR_IMPL);
		}
		
		formatter.applyPattern(rawQueryString);
				
		this.queryString = formatter.format(qFormatParams.toArray());
		this.qParams = qParamList.toArray();
	}
}
