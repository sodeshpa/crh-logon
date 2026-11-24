/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.ajax;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.crh.logon.order.web.forms.bimodalrailcarmonitoring.BimodalRailcarMonitoringForm;
import org.apache.commons.lang.StringUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.BimodalRailcarMonitoringService;
import com.crh.logon.utils.ListRange;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.utils.Authorization;

/**
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to 
 * Ajax components. 
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="BimodalRailcarMonitoringAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.order.web.utils.*"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 */
public class BimodalRailcarMonitoringAjaxService extends DwrServiceSupport {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(BimodalRailcarMonitoringAjaxService.class);
    
	public ListRange fetchPlantsList(
				int skip,
				int count,
				String sort,
				String[] query,
				HttpServletRequest request,
				HttpSession session) {		
		// [[
		ListRange result = null;
		Object[][] params = {{"query", query}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchPlantsList", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.BIMODAL_MONITORING);
			String companyCode = StringUtils.isNotEmpty(query[0]) ? query[0] : null;
			String prodPlantSrearchText = StringUtils.isNotEmpty(query[1]) ? query[1] : null;
			
			final BimodalRailcarMonitoringForm form = (BimodalRailcarMonitoringForm)session.getAttribute(BimodalRailcarMonitoringForm.SESSION_FORM_NAME);
			
			BimodalRailcarMonitoringService biRailMonServ = getServiceLocator().getBimodalRailcarMonitoringService();
			return result = biRailMonServ.fetchPlantList( skip, count, 	form.getSelectedShiptoIdList(), companyCode, prodPlantSrearchText);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public ListRange fetchMaterialsList(
			int skip,
			int count,
			String sort,
			String[] query,
			HttpServletRequest request,
			HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"count", count + ""},
							{"sort", sort},
							{"query", query},
							{"request", request},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchMaterialsList", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.BIMODAL_MONITORING);

			String companyCode = StringUtils.isNotEmpty(query[0]) ? query[0] : null;
			String plantCd = StringUtils.isNotEmpty(query[1]) ? query[1] : null;
			String sapMaterialDescTxt = StringUtils.isNotEmpty(query[2]) ? query[2] : null;
			
			final BimodalRailcarMonitoringForm form = (BimodalRailcarMonitoringForm)session.getAttribute(BimodalRailcarMonitoringForm.SESSION_FORM_NAME);
			
			BimodalRailcarMonitoringService biRailMonServ = getServiceLocator().getBimodalRailcarMonitoringService();
			return result = biRailMonServ.fetchSapMaterialList( skip, count, form.getSelectedShiptoIdList(), companyCode, plantCd, sapMaterialDescTxt, request.getLocale());
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
    
    
}
