/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.reporting;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.beans.SelectableGeneralDisplayNameDto;
import com.crh.logon.business.services.interfaces.BestellungService;
import com.crh.logon.business.services.interfaces.PoolAdminService;
import com.crh.logon.business.services.reporting.ReportMetaData;
import com.crh.logon.business.services.utils.PoolCarrierRelationWrapper;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.order.web.forms.reporting.ReportingForm;
import com.crh.logon.persistence.entities.refdata.DistributionDestTxEntity;
import com.crh.logon.persistence.entities.refdata.MaterialPackageTxEntity;
import com.crh.logon.utils.LogonException;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_reporting_ReportingForm" path="/reporting/ReportingEntry" scope="session" validate="false"
 *
 * @struts.action-forward name="success" path="ReportingTile.def"
 */
public class ReportingEntryAction extends OrderActionSupport {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ReportingEntryAction.class);
	private static final Collection HIDDEN_PROPERTIES;
	static {
		HIDDEN_PROPERTIES = new HashSet();
		HIDDEN_PROPERTIES.add("class");
		HIDDEN_PROPERTIES.add("queryString");
		HIDDEN_PROPERTIES.add("QParams");
		HIDDEN_PROPERTIES.add("rawQueryString");
	}
	public static final class ReportDescription {
		private String reportCd;
		private String reportName;
		private List<String> propertyNames;
		public String getReportCd() {
			return reportCd;
		}
		public void setReportCd(String reportCd) {
			this.reportCd = reportCd;
		}
		public String getReportName() {
			return reportName;
		}
		public void setReportName(String reportName) {
			this.reportName = reportName;
		}
		public List<String> getPropertyNames() {
			if (propertyNames == null) {
				propertyNames = new ArrayList<String>();
			}
			return propertyNames;
		}
	}
	
	public ActionForward execute(
		ActionMapping mapping, ActionForm form,
		HttpServletRequest request,	HttpServletResponse response)
		throws IOException, ServletException
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
			setSelectedTab("reporting", request);
			setSelectedSub1Tab(null, request);
			setSelectedSub2Tab(null, request);
			ReportingAuthorization.authorize(getServiceLocator());
			
			ReportingForm searchForm = (ReportingForm) form;
			ServiceLocator serviceLocator = getServiceLocator();
			Locale locale = request.getLocale();
			
			PoolAdminService poolAdminService = serviceLocator.getPoolAdminService();
			PoolCarrierRelationWrapper poolCarrierConnectionWrapper 
				= poolAdminService.fetchTruckPoolCarrierConnectionsForLocale(locale, null, null);
			
			List<SelectableGeneralDisplayNameDto> carriers = poolCarrierConnectionWrapper.getCarriers();
			searchForm.setCarriers(carriers);
			
			List<SelectableGeneralDisplayNameDto> pools = poolCarrierConnectionWrapper.getPools();
			searchForm.setPools(pools);
			
			Map<Long, List<SelectableGeneralDisplayNameDto>> poolCarrierMap = poolCarrierConnectionWrapper.getPoolCarrierMap();
			searchForm.setPoolCarrierMap(poolCarrierMap);
			
			Set<Long> selectedPoolIds = poolCarrierConnectionWrapper.getSelectedPoolIds();
			searchForm.setSelectedPoolIDs(StringUtils.join(selectedPoolIds, ','));
			
			Set<Long> selectedCarrierIds = poolCarrierConnectionWrapper.getSelectedCarrierIds();
			searchForm.setSelectedCarrierIDs(StringUtils.join(selectedCarrierIds, ','));
			
			Map jsonData = getJsonData(locale, searchForm);
			searchForm.setJson(jsonData);
			
			BestellungService bestellungService = serviceLocator.getBestellungService();
			List<SelectableGeneralDisplayNameDto> orderStatusEntries = bestellungService.fetchOrderStatusDisplayNameDtosForLocale(locale, true);
			searchForm.setOrderStatusList(orderStatusEntries);
			
			String forwardName = "success";
			
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

	
	private Map getJsonData(Locale locale, ReportingForm searchForm) {
		
		Map jsonData = new HashMap();
	    
		jsonData.put("distributionDestList", getJsonSerializer().serialize(getRefDataGenericsService().findTxEntityList(DistributionDestTxEntity.class, locale)));
	    jsonData.put("reportList", getJsonSerializer().deepSerialize(getReportDescriptions(locale)));
	    jsonData.put("langList", getJsonSerializer().serialize(getRefDataGenericsService().findLangTxEntityList(locale)));
	    jsonData.put("materialPackageList", getJsonSerializer().serialize(getRefDataGenericsService().findTxEntityList(MaterialPackageTxEntity.class, locale)));
		jsonData.put("carrierList", getJsonSerializer().serialize(searchForm.getCarriers()));
		jsonData.put("poolList", getJsonSerializer().serialize(searchForm.getPools()));
		jsonData.put("orderStatusList", getJsonSerializer().serialize(searchForm.getOrderStatusList()));
		
		return jsonData;
	}
	
	
	private List getReportDescriptions(Locale locale) {
		
		List resultList = new ArrayList();
		List allReports = getServiceLocator().getReportingService().getAllReportMetaData(locale);

		for (Iterator it=allReports.iterator();it.hasNext();) {
			ReportMetaData rmd = (ReportMetaData)it.next();
			if (ReportingAuthorization.isAllowed(rmd.getClass())) {
				ReportDescription rd = new ReportDescription();
				rd.setReportCd(rmd.getReportCd());
				rd.setReportName(rmd.getReportName());
				BeanInfo info;
				try {
					info = Introspector.getBeanInfo(rmd.getClass());
				}
				catch (IntrospectionException e) {
					
					throw new LogonException(e);
				}
				PropertyDescriptor[] propertyDescriptors = info.getPropertyDescriptors();
				for (PropertyDescriptor pd: propertyDescriptors) {
					if (!HIDDEN_PROPERTIES.contains(pd.getName())) {
						rd.getPropertyNames().add(pd.getName());
					}
				}
				resultList.add(rd);
			}
		}
		
		return resultList;
	}
}
