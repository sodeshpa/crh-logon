/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.containermonitoring;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.forms.containermonitoring.ContainerMonitoringForm;
import com.crh.logon.order.web.utils.OrderTakingUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.ContainerMonitoringWrapper;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.views.ContainerMonitoringEntity;
import com.crh.logon.web.utils.Authorization;

/**
 * @author AdNovum Informatik AG
 * 
 * @struts.action name="order_ContainerMonitoringForm" 
 *  path="/containermonitoring/ContainerMonitoring" scope="session"
 *  validate="true" input="/containermonitoring/ContainerMonitoring.def"
 * 
 * @struts.action-forward name="success" path="/containermonitoring/ContainerMonitoring.def"
 * @struts.action-forward name="failure" path="/containermonitoring/ContainerMonitoring.def"
 * @struts.action-forward name="shippingpoints" path="/containermonitoring/ContainerMonitoringShippingPointsPartTile.def" 
 * @struts.action-forward name="editShippingpoints" path="/containermonitoring/ContainerMonitoringShippingPointsEditPartTile.def"
 * @struts.action-forward name="refreshResults" path="/containermonitoring/ContainerMonitoringResultsPartTile.def" 
 */
public class ContainerMonitoringAction extends OrderActionSupport {

	private static final JcanLogger LOG =
		        JcanLoggerFactory.getLogger(ContainerMonitoringAction.class);
	 
	public static final String ACTION = "action";

    //ajax calls
    public static final String AJAX_ACTION_EDIT_SHIPPINGPOINTS = "editShippingpoints";
    public static final String AJAX_ACTION_SET_SHIPPINGPOINTS = "setShippingpoints";
    
    //normal post
    public static final String ACTION_SEARCH = "searchContainers";
    
    public static final String FORWARD_EDIT_SHIPPINGPOINTS  = "editShippingpoints";
    public static final String FORWARD_SHIPPINGPOINT_DROPDOWN ="shippingpoints";
    public static final String FORWARD_REFRESH_RESULTS ="refreshResults";
  
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
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
			Authorization.authorize(Authorization.CONTAINER_MONITORING);
			setSelectedTab("monitoring", request);
			setSelectedSub1Tab("monitoring", request);
			setSelectedSub2Tab("ContainerMonitoring", request);
	
			// Get the struts form.
			ContainerMonitoringForm helpForm = (ContainerMonitoringForm)form;
			String forwardName = "input";
						
			if (AJAX_ACTION_EDIT_SHIPPINGPOINTS.equals(request.getParameter(ACTION))) {
				forwardName = ContainerMonitoringAction.FORWARD_EDIT_SHIPPINGPOINTS;
				
			}else if (AJAX_ACTION_SET_SHIPPINGPOINTS.equals(request.getParameter(ACTION))) {				
				forwardName = ContainerMonitoringAction.FORWARD_SHIPPINGPOINT_DROPDOWN;
				
			}else if (ACTION_SEARCH.equals(request.getParameter(ACTION))) {

				List<ShippingpointEntity> possibleContainerTerminals = getServiceLocator().getBestellungService().fetchContainerTerminalShippingPoints();
				helpForm.setShippingPoints(possibleContainerTerminals);
				
				Long selectedSapMatId = null;
				if(helpForm.getSelectedSapMatId() != null)
					selectedSapMatId = Long.valueOf(helpForm.getSelectedSapMatId());
				final String selectedProdPlantCd = helpForm.getSelectedProductionPlantCd();
				final List<Long> selectedShippingPointList = helpForm.getSelectedShippingPointIdList();
				final String sortByColumn = helpForm.getSortByColumn();
				final boolean sortAsc = helpForm.isSortAsc();
				final String langCd = request.getLocale().getLanguage();
				
				List<ContainerMonitoringEntity> resultList = getServiceLocator()
						                                    .getBestellungService()
						                                    .fetchContainerMonitoringEntries(selectedProdPlantCd
						                                		                  , selectedSapMatId
						                                		                  , selectedShippingPointList
						                                		                  , sortByColumn
						                                		                  , sortAsc
						                                		                  , langCd);		

				TimeZone defaultTz = getTimeZoneService().createDefault(getUserLocale());

				List <ContainerMonitoringWrapper> containerListWrapped =
						OrderTakingUtils.wrapContainerMonitoringEntities(resultList,
																		 getUserTimezone(),
																		 getUserLocale(),
																		 defaultTz);
			
				helpForm.setResultList(containerListWrapped);
				
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
