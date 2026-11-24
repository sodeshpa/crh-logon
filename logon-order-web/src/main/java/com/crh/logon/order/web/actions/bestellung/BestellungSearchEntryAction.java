/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.bestellung;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.utils.OrderSearchExportMetaData;
import com.crh.logon.order.web.utils.OrderSearchExportUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.reporting.SqlDataExportListener;
import com.crh.logon.business.services.reporting.SqlDataExporter;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.constants.Constant;
import com.crh.logon.order.web.forms.bestellung.BestellungSearchForm;
import com.crh.logon.order.web.forms.bestellung.MaterialPackageTypeBean;
import com.crh.logon.persistence.entities.refdata.DistributionDestTxEntity;
import com.crh.logon.persistence.entities.refdata.MaterialPackageTxEntity;
import com.crh.logon.persistence.entities.refdata.OrderCreationTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusTxEntity;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_bestellung_BestellungSearchForm" path="/bestellung/BestellungSearchEntry" scope="session" validate="false"
 *
 * @struts.action-forward name="success" path="BestellungSearchTile.def"
 */
public class BestellungSearchEntryAction extends AbstractBestellungSearchEntryAction {

	Long[] STATUS_LIST_NEW = {	
			Constant.ORDER_STATUS_NEW,
			Constant.ORDER_STATUS_DELETED
			};
	
	Long[] STATUS_LIST_POOL = {	
			Constant.ORDER_STATUS_TRANSMITTED_TO_POOL,
			Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK
			};
		
	Long[] STATUS_LIST_WORKFLOW = {
			Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER,
			Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK,			
			Constant.ORDER_STATUS_DISPATCHED,
			Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER,	
			Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK,	
			Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL,			
			Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT,
			Constant.ORDER_STATUS_LOADING,
			Constant.ORDER_STATUS_LOADED,
			Constant.ORDER_STATUS_SHIPPING,
			Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT,
			Constant.ORDER_STATUS_UNLOADING,
			Constant.ORDER_STATUS_DELIVERED_PENDING_LDS_DATA,
			Constant.ORDER_STATUS_DELIVERED,
			Constant.ORDER_STATUS_FINISHED
			};
	
    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(BestellungSearchEntryAction.class);

	public ActionForward execute(
		ActionMapping mapping, ActionForm form,
		HttpServletRequest request,	HttpServletResponse response)
		throws Exception
	{
		// [[
		ActionForward result = null;
		Object[][] params = {{"mapping", mapping},
							{"form", form},
							{"request", request},
							{"response", response}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "execute", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.ORDER_LIST);
			setSelectedTab("orderlist", request);
			setSelectedSub1Tab(null, request);
			setSelectedSub2Tab(null, request);
			BestellungSearchForm searchForm = (BestellungSearchForm)form;
			
			String forwardName;
			String formAction = !StringUtils.isEmpty(searchForm.getFormAction()) ? searchForm.getFormAction() : "";
			if (ACTION_SAVE_PREFERENCES.equals(formAction)) {
				handleSaveButtonClick(searchForm, Constant.USER_PREFERENCE_SCREEN_ORDER_SEARCH);
				
				addSuccessFullySavedMessageForAjaxResponse(request, response);
	    		
				return result = null;
			}
			else if (ACTION_RESET.equals(formAction)) {
				searchForm.clearDefaults();
				forwardName = "success";
				return result = mapping.findForward(forwardName);
			}
			else if (request.getParameter("export") != null) {
				return exportData(request, response);
			}
			
			//load
			List<OrderStatusTxEntity> statusList = buildValidStatusSelectList(request.getLocale());
			searchForm.setOrderStatusList(statusList);
			
			List<OrderCreationTypeTxEntity> orderCreationTypeList = getRefDataGenericsService().findTxEntityList(OrderCreationTypeTxEntity.class, request.getLocale());
			searchForm.setOrderCreationTypeList(orderCreationTypeList);
			
			List distributionDestCdList = getRefDataGenericsService().findTxEntityList(DistributionDestTxEntity.class, request.getLocale());
			searchForm.setDistributionDestCdList(distributionDestCdList);
			
			Collection<Object> packageTypes = new HashSet<Object>();
			packageTypes.add(Constant.MATERIAL_PACKAGE_BAG);
			packageTypes.add(Constant.MATERIAL_PACKAGE_BULK_POWDER);
			packageTypes.add(Constant.MATERIAL_PACKAGE_BULK_DUMPED);

			ArrayList<MaterialPackageTxEntity> materialPackageTxListAll = (ArrayList<MaterialPackageTxEntity>)getRefDataGenericsService().
					findTxEntityList(MaterialPackageTxEntity.class, false, request.getLocale(), MaterialPackageTxEntity.ATTR_TXT, true);
			
			ArrayList<MaterialPackageTypeBean> materialPackageTypeBeanList = new ArrayList<MaterialPackageTypeBean>();
			for (MaterialPackageTxEntity matPackageTx : materialPackageTxListAll) {
				String matPackageCd = matPackageTx.getMaterialPackageTxPK().getMaterialPackageBs().getMaterialPackageCd();
				if (packageTypes.contains(matPackageCd)) {
					MaterialPackageTypeBean materialPackageTypeBean = new MaterialPackageTypeBean();
					materialPackageTypeBean.setMaterialPackageCd(matPackageCd);
					materialPackageTypeBean.setMaterialPackageTxt(matPackageTx.getTxt());
					materialPackageTypeBeanList.add(materialPackageTypeBean);
				}
			}
			searchForm.setMaterialPackageTypeBean(materialPackageTypeBeanList);
			
			Map jsonData = getJsonData(request.getLocale());
			searchForm.setJson(jsonData);
			
			populateForm(searchForm, request.getLocale(), orderCreationTypeList, statusList,
					Constant.USER_PREFERENCE_SCREEN_ORDER_SEARCH);
			
			forwardName = "success";
			
			return result = mapping.findForward(forwardName);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	private ActionForward exportData(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		// [[
		ActionForward result = null;
		Object[][] params = {{"request", request},
							{"response", response}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "exportData", params);
		}
		// ]]
		try {
			TimeZone userTimeZone = getUserTimezone();
			setDLHeaders(request, response, null, "order_search_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".csv");
			Locale userLocale = request.getLocale();
			
			Map<String, String> searchCriteria = new HashMap<String, String>();			

			Map parameters = request.getParameterMap();
			Iterator entries = parameters.entrySet().iterator();
			while (entries.hasNext()) {
			    Map.Entry entry = (Map.Entry) entries.next();
			    String key = (String)entry.getKey();
			    String[] value = (String[])entry.getValue();
			    if(value == null || value.length == 0) {
					continue;
				}
				String criteriaVale = value[0];
				searchCriteria.put(key, criteriaVale);

			}
			
			OrderSearchExportMetaData emd = OrderSearchExportUtil.createOrderSearchExportMetaData(searchCriteria, userLocale, userTimeZone, getServiceLocator());
			
			SqlDataExporter exporter = new SqlDataExporter(getServiceLocator());
			CustomSqlExportListener listener = createListenerForCSVExport();
			TimeZone defaultSystemTimeZone = getTimeZoneService().createDefault(getUserLocale());
			listener.setDefaultSystemTimeZone(defaultSystemTimeZone);
			
			exporter.setExportListener(listener);
			exporter.exportSqlData(response.getWriter(),
								   emd.getQueryString(),
								   emd.getQParams(),
								   request.getLocale(),
								   getUserTimezone());
			
			return result = null;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	private class CustomSqlExportListener implements SqlDataExportListener {
		private TimeZone defaultSystemTimeZone;
		
		public void setDefaultSystemTimeZone(TimeZone defaultSystemTimeZone) {
			this.defaultSystemTimeZone = defaultSystemTimeZone;
		}
		
		public String[] exportHeader(String[] columnNames, Locale locale) {
			return columnNames;
		}

		public void exportRow(String[] columnNames, Object[] rowData, Map data, Locale locale, TimeZone userTimezone) {
			
			for (int i=0;i<columnNames.length;i++) {
				String columnName = columnNames[i];
				Object value = data.get(columnName);
				if (value instanceof Date) {
					Date currentDate =(Date) value;
					data.put(columnName, userTimezone.fmtAsDayMonthYearHourMinuteWithOffset(
							currentDate));
				}
			}
		}
	};

	
	private CustomSqlExportListener createListenerForCSVExport() {
		return new CustomSqlExportListener();
	}
	
	/**
	 * @param locale
	 * @return
	 */
	private List buildValidStatusSelectList(Locale locale) {
		
		//visible anyway
		List requestStatuses = new ArrayList();
		
		requestStatuses.addAll(Arrays.asList(STATUS_LIST_WORKFLOW));
		
		if( Authorization.isAllowed(Authorization.ORDER_LIST_AUFTRAGSANNAHME)) {			
			requestStatuses.addAll(Arrays.asList(STATUS_LIST_NEW));
		}

		if( Authorization.isAllowed(Authorization.ORDER_LIST_FOR_POOL)) {			
			requestStatuses.addAll(Arrays.asList(STATUS_LIST_POOL));
		}
		
		return getBestellungService().getStatusList(requestStatuses, locale.getLanguage());
	}

}
