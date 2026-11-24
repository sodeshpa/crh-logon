/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.ajax;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.beans.SelectableGeneralDisplayNameDto;
import com.crh.logon.business.services.interfaces.PoolAdminService;
import com.crh.logon.business.services.reporting.ReportMetaData;
import com.crh.logon.order.web.actions.reporting.ReportingAuthorization;
import com.crh.logon.order.web.forms.reporting.ReportingForm;
import com.crh.logon.web.ajax.DwrServiceSupport;

/**
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to 
 * Ajax components. 
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="ReportingAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.order.web.utils.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.services.utils.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.services.beans.*"
  */
public class ReportingAjaxService extends DwrServiceSupport {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ReportingAjaxService.class);
	
	/**
	 * In case the pool selection was changed on the pop-up this method is called.
	 * It will add / remove carriers based on the changed pool selection.
	 * Carriers belonging to multiple pools will be kept.
	 * @param newPoolSelectionIds - current carrier selection on the GUI 
	 * @param request
	 * @return the updated carrier list where the selection field is also set
	 * @throws ParseException
	 */
	public List<SelectableGeneralDisplayNameDto> updateSelectedPoolIds(List<String> newPoolSelectionIds, HttpServletRequest request) {
		// [[
		List<SelectableGeneralDisplayNameDto> result = null;
		Object[][] params = {{"poolIds", newPoolSelectionIds},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateSelectedPoolIds", params);
		}
		// ]]
		try {
			ReportingAuthorization.authorize(getServiceLocator());
			
			HttpSession session = request.getSession();
			ReportingForm reportingForm = (ReportingForm) session.getAttribute("order_reporting_ReportingForm");
			
			List<SelectableGeneralDisplayNameDto> formCarriers = reportingForm.getCarriers();
			List<Long> oldPoolSelectionIds = reportingForm.getSelectedPoolIDList();
			Map<Long, List<SelectableGeneralDisplayNameDto>> poolCarrierMap = reportingForm.getPoolCarrierMap();
			
			PoolAdminService poolAdminService = getServiceLocator().getPoolAdminService();
			List<SelectableGeneralDisplayNameDto> newCarriers = poolAdminService.chooseCarriersBasedOnPoolSelection(newPoolSelectionIds, oldPoolSelectionIds, formCarriers, poolCarrierMap);
			
			Comparator<SelectableGeneralDisplayNameDto> ascComparator = new Comparator<SelectableGeneralDisplayNameDto>() {
				@Override
				public int compare(SelectableGeneralDisplayNameDto arg0, SelectableGeneralDisplayNameDto arg1) {
						return arg0.getName().compareTo(arg1.getName());
				}
			};
			Collections.sort(newCarriers, ascComparator);
			
			String selectedPoolIds = StringUtils.join(newPoolSelectionIds, ',');
			
			reportingForm.setSelectedPoolIDs(selectedPoolIds);
			reportingForm.setCarriers(newCarriers);
			
			return result = newCarriers;
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * When the carrier selection is changed on the GUI this method is called.
	 * The corresponding {@coed SelectableGeneralDisplayNameDto}s will be set to to 'selected' on the form. 
	 * @param carrierIds - currently selected carrier ids
	 * @param request
	 */
	public void updateSelectedCarrierIds(List<String> carrierIds, HttpServletRequest request) {
		// [[
		Object[][] params = {{"carrierIds", carrierIds},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateSelectedCarrierIds", params);
		}
		// ]]
		try {
			ReportingAuthorization.authorize(getServiceLocator());
			
			HttpSession session = request.getSession();
			ReportingForm reportingForm = (ReportingForm) session.getAttribute("order_reporting_ReportingForm");
			
			List<SelectableGeneralDisplayNameDto> formCarriers = reportingForm.getCarriers();
			for (SelectableGeneralDisplayNameDto selectableGeneralDisplayNameDto : formCarriers) {
				
				Long carrierId = selectableGeneralDisplayNameDto.getId();
				boolean carrierSelected = carrierIds.contains(carrierId.toString());
				
				selectableGeneralDisplayNameDto.setSelected(carrierSelected);
				
			}
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * Returns a different set of available order status list depending on the selected report.
	 * IF transplus: all valid order status
	 * IF orderreport: finished and finished commercial
	 * @param newPoolSelectionIds - current carrier selection on the GUI 
	 * @param request
	 * @return the updated carrier list where the selection field is also set
	 * @throws ParseException
	 */
	public List<SelectableGeneralDisplayNameDto> updateOrderStatusList(String reportType, HttpServletRequest request) {
		// [[
		List<SelectableGeneralDisplayNameDto> result = null;
		Object[][] params = {{"reportType", reportType},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateOrderStatusList", params);
		}
		// ]]
		try {
			ReportingAuthorization.authorize(getServiceLocator());
			
			HttpSession session = request.getSession();
			ReportingForm reportingForm = (ReportingForm) session.getAttribute("order_reporting_ReportingForm");
			
			result = new ArrayList<SelectableGeneralDisplayNameDto>();
			
			if (reportType.equals("transplus")) {
				result.addAll(reportingForm.getOrderStatusList());
			}
			else if (reportType.equals("orderreportmetadata")) {
				Long finishedId = Long.valueOf(ReportMetaData.VIRTUAL_ORDER_STATUS_FINISHED);
				Long finishedCommercialId = Long.valueOf(ReportMetaData.VIRTUAL_ORDER_STATUS_FINISHED_COMMERCIAL);
				result.add(new SelectableGeneralDisplayNameDto(finishedId, "finished", true));
				result.add(new SelectableGeneralDisplayNameDto(finishedCommercialId, "finished commercial", true));
			}
			else {
				throw new IllegalArgumentException("Wrong report type!");
			}
			
			return result;
			
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
