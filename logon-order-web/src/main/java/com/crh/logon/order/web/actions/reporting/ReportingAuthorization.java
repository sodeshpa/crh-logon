/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.reporting;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.crh.logon.business.services.reporting.ExternalOrder;
import com.crh.logon.business.services.reporting.FreightCarrierRevenuePdfMetaData;
import com.crh.logon.business.services.reporting.GoodsMovementReport;
import com.crh.logon.business.services.reporting.LoadShippingStats;
import com.crh.logon.business.services.reporting.OrderDataPerCarrier;
import com.crh.logon.business.services.reporting.OrderDataPerCustomer;
import com.crh.logon.business.services.reporting.OrderDataPerPlantMat;
import com.crh.logon.business.services.reporting.OrderDataPerPool;
import com.crh.logon.business.services.reporting.OrderDataPerRoute;
import com.crh.logon.business.services.reporting.OrderDataPerRouteCarrier;
import com.crh.logon.business.services.reporting.OrderDataPerSpMatpkg;
import com.crh.logon.business.services.reporting.OrderList;
import com.crh.logon.business.services.reporting.OrderListCarrier;
import com.crh.logon.business.services.reporting.OrderListComm;
import com.crh.logon.business.services.reporting.OrderListCust;
import com.crh.logon.business.services.reporting.OrderListCrh;
import com.crh.logon.business.services.reporting.OrderWinSped;
import com.crh.logon.business.services.reporting.ReportMetaData;
import com.crh.logon.business.services.reporting.SalesPerCust;
import com.crh.logon.business.services.reporting.SalesPerCustMat;
import com.crh.logon.business.services.reporting.SalesPerMat;
import com.crh.logon.business.services.reporting.SapBwExport;
import com.crh.logon.business.services.reporting.StagingAreaErrors;
import com.crh.logon.business.services.reporting.TransPerPlant;
import com.crh.logon.business.services.reporting.TransPerPlantDt;
import com.crh.logon.business.services.reporting.TransPlus;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.utils.AuthUtils;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.web.utils.Authorization;

/**
 * @author AdNovum Informatik AG
 */
public class ReportingAuthorization {
	
	private static final Map<Class, Long> reportFunctionMap;
	
	static {
		Map<Class, Long> authMap = new HashMap<Class, Long>();
		authMap.put(OrderDataPerCarrier.class, Authorization.REPORT_ORDER_DATA_PER_CARRIER);
		authMap.put(OrderDataPerCustomer.class, Authorization.REPORT_ORDER_DATA_PER_CUSTOMER);
		authMap.put(OrderList.class, Authorization.REPORT_ORDER_LIST);
		authMap.put(SapBwExport.class, Authorization.REPORT_SAP_BW_EXPORT);
		authMap.put(OrderDataPerRouteCarrier.class, Authorization.REPORT_ORDER_DATA_PER_ROUTE_CARRIER);
		authMap.put(OrderDataPerRoute.class, Authorization.REPORT_ORDER_DATA_PER_ROUTE);
		authMap.put(OrderDataPerPool.class, Authorization.REPORT_ORDER_DATA_PER_POOL);
		authMap.put(OrderDataPerSpMatpkg.class, Authorization.REPORT_ORDER_DATA_PER_SP_MATPKG);
		authMap.put(OrderDataPerPlantMat.class, Authorization.REPORT_ORDER_DATA_PER_PLANT_MAT);
		authMap.put(TransPerPlantDt.class, Authorization.REPORT_TRANS_PER_PLANT_DT);
		authMap.put(TransPerPlant.class, Authorization.REPORT_TRANS_PER_PLANT);
		authMap.put(SalesPerCustMat.class, Authorization.REPORT_SALES_PER_CUST_MAT);
		authMap.put(SalesPerCust.class, Authorization.REPORT_SALES_PER_CUST);
		authMap.put(SalesPerMat.class, Authorization.REPORT_SALES_PER_MAT);
		authMap.put(OrderListCust.class, Authorization.REPORT_ORDER_LIST_CUST);
		authMap.put(OrderListCarrier.class, Authorization.REPORT_ORDER_LIST_CARRIER);
		authMap.put(OrderListCrh.class, Authorization.REPORT_ORDER_LIST_CRH);
		authMap.put(OrderListComm.class, Authorization.REPORT_ORDER_LIST_COMM);
		authMap.put(OrderWinSped.class, Authorization.REPORT_ORDER_WINSPED);
		authMap.put(ExternalOrder.class, Authorization.REPORT_EXTERNAL_ORDERS);
		authMap.put(LoadShippingStats.class, Authorization.REPORT_LOAD_SHIPPING_STATS);
		authMap.put(FreightCarrierRevenuePdfMetaData.class, Authorization.REPORT_FREIGHT_CARRIER_REVENUE_PDF);
		authMap.put(StagingAreaErrors.class, Authorization.REPORT_STAGING_AREA_ERRORS);
		authMap.put(GoodsMovementReport.class, Authorization.REPORT_GOODS_MOVEMENTS);
		authMap.put(TransPlus.class, Authorization.REPORT_TRANSPLUS);
		
		reportFunctionMap = Collections.unmodifiableMap(authMap);
	}
	
	public static boolean isAllowed(Class reportMetaDataClass) {
		
		Long functionId = (Long)reportFunctionMap.get(reportMetaDataClass);
		if (functionId == null) {
			
			throw new LogonException("Report authorization function not found", Reason.RES_ERROR_IMPL);
		}
		
		return Authorization.isAllowed(functionId);
	}
	
	public static void authorize(ServiceLocator serviceLocator) {
		
		AuthUtils.setAuthorized(true);
		List allReports = serviceLocator.getReportingService().getAllReportMetaData(Locale.getDefault());
		AuthUtils.deauthorize();
		boolean authorized = false;
		
		for (Iterator it=allReports.iterator();it.hasNext();) {
			ReportMetaData rmd = (ReportMetaData)it.next();
			if (isAllowed(rmd.getClass())) {
				Long functionId = (Long)reportFunctionMap.get(rmd.getClass());
				Authorization.authorize(functionId);
				authorized = true;
				break;
			}
		}
		
		if (!authorized) {
			
			throw new LogonException("Not authorized for any report", Reason.RES_ERROR_SECURITY_NOT_ALLOWED);
		}
	}
}
