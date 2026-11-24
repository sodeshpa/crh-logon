/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.ajax;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.crh.logon.order.web.forms.simpledispo.SimpleDispoForm;
import org.apache.struts.util.RequestUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.DispoService;
import com.crh.logon.business.services.utils.OrderItemOrderedQtyWrapper;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.utils.Authorization;

/**
 * DWR ajax service for Simple Vehicle Scheduling
 * 
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to 
 * Ajax components. 
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="SimpleDispoAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.order.web.utils.*"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 */
public class SimpleDispoAjaxService extends DwrServiceSupport {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SimpleDispoAjaxService.class);
	
	/**
	 * Sets the given carriers to the SimpleDispoForm.
	 * @param selectedCarriers - the set of carriers to save
	 * @param session - this holds the struts form
	 */
	public void setSelectedCarriers(Set<Long> selectedCarriers, HttpSession session) {
		// [[
		Object[][] params = {{"selectedCarriers", selectedCarriers},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setSelectedCarriers", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.SIMPLE_VEHICLE_DISPATCHING_R);
			SimpleDispoForm dispoForm = (SimpleDispoForm) session.getAttribute(SimpleDispoForm.FORM_NAME);
			// TODO (paksyd Nov 14, 2012): We should not forget the old ones, but it works now!
			dispoForm.setSelectedCarriers(selectedCarriers);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	/** Returns the list of detailed material info for a single delivery order. */
	public List<OrderItemOrderedQtyWrapper> getDetailedMaterialInfo(Long deliveryOrderNumber,
			HttpServletRequest request) {
		// [[
		List<OrderItemOrderedQtyWrapper> result = null;
		Object[][] params = {{"deliveryOrderNumber", deliveryOrderNumber},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getDetailedMaterialInfo", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.DISPATCHING_R);
			
			List<OrderItemOrderedQtyWrapper> list = new ArrayList<OrderItemOrderedQtyWrapper>();
			
			final Locale userLocale = RequestUtils.getUserLocale(request, null);
			final String langCd = userLocale.getLanguage();
			DispoService dispoService = getServiceLocator().getDispoService();
			list = dispoService.fetchOrderItemOrderedQtyWrappers(deliveryOrderNumber, langCd);
			return result = list;
			
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
