/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.bimodalrailcarmonitoring;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.forms.bimodalrailcarmonitoring.BimodalRailcarMonitoringForm;
import com.crh.logon.order.web.utils.OrderTakingUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.BimodalRailOrderWrapper;
import com.crh.logon.persistence.entities.views.BimodalRailOrderEntity;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_BimodalRailcarMonitoringForm"
 *  path="/bimodalrailcarmonitoring/BimodalRailcarMonitoring" scope="session"
 *  validate="true" input="/bimodalrailcarmonitoring/BimodalRailcarMonitoring.def"
 *
 * @struts.action-forward name="success" path="/bimodalrailcarmonitoring/BimodalRailcarMonitoring.def"
 * @struts.action-forward name="failure" path="/bimodalrailcarmonitoring/BimodalRailcarMonitoring.def"
 * @struts.action-forward name="shiptos" path="/bimodalrailcarmonitoring/BimodalRailcarMonitoringShiptosPartTile.def"
 * @struts.action-forward name="editShiptos" path="/bimodalrailcarmonitoring/BimodalRailcarMonitoringShiptosEditPartTile.def"
 * @struts.action-forward name="refreshResults" path="/bimodalrailcarmonitoring/BimodalRailcarMonitoringResultsPartTile.def" 
 * 
 */
public class BimodalRailcarMonitoringAction extends OrderActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(BimodalRailcarMonitoringAction.class);

    //ajax calls
    public static final String AJAX_ACTION_SET_COMPANYCODE = "setCompanyCode";
    public static final String AJAX_ACTION_EDIT_SHIPTOS = "editShiptos";
    public static final String AJAX_ACTION_SET_SHIPTOS = "setShiptos";
    
    //normal post
    public static final String ACTION_SEARCH = "searchBimodalOrders";
    
    public static final String FORWARD_EDIT_SHIPTOS  = "editShiptos";
    public static final String FORWARD_SHIPTO_DROPDOWN ="shiptos";
    public static final String FORWARD_REFRESH_RESULTS ="refreshResults";
    
    
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
			Authorization.authorize(Authorization.BIMODAL_MONITORING);
			setSelectedTab("monitoring", request);
			setSelectedSub1Tab("monitoring", request);
			setSelectedSub2Tab("BimodalRailcarMonitoring", request);
			
		    // Get the struts form.
			BimodalRailcarMonitoringForm helpForm = (BimodalRailcarMonitoringForm)form;
			String forwardName = "input";
			
			if ( request.getParameter(AJAX_ACTION_SET_COMPANYCODE) != null) {
				List fvlShiptos = getServiceLocator().getBimodalRailcarMonitoringService().fetchFvlShiptos(helpForm.getSelectedCompanyCode());
				helpForm.setShiptos(fvlShiptos);
				helpForm.setSelectedShiptos(new HashSet());
				forwardName = FORWARD_SHIPTO_DROPDOWN;
			}
			
			if ( request.getParameter(AJAX_ACTION_EDIT_SHIPTOS) != null) {
				forwardName = FORWARD_EDIT_SHIPTOS;
			}
			
			if ( request.getParameter(AJAX_ACTION_SET_SHIPTOS) != null) {
				forwardName = FORWARD_SHIPTO_DROPDOWN;
			}
			
			if ( request.getParameter(ACTION_SEARCH) != null) {
				final String selectedSapMatId = helpForm.getSelectedSapMatId();
				
				List <BimodalRailOrderEntity> resultList = getServiceLocator().getBimodalRailcarMonitoringService().fetchBimodalRailOrders(helpForm.getSelectedCompanyCode(), helpForm.getSelectedShiptoIdList(), helpForm.getSelectedProductionPlantCd(), selectedSapMatId != null ? Long.valueOf(selectedSapMatId) : null, request.getLocale().getLanguage(), helpForm.getSortByColumn(), helpForm.isSortAsc());					
				List <BimodalRailOrderWrapper> biRailOrderListWrapped = OrderTakingUtils.wrapBimodalRailOrders(
						getServiceLocator(),
						null,
						resultList,
						getUserLocale());

				helpForm.setResultList(biRailOrderListWrapped);
				
				forwardName = FORWARD_REFRESH_RESULTS;
			}
			
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




}
