/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.railcaroptimization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.RequestUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.DispoService;
import com.crh.logon.order.web.forms.railcaroptimization.RailCarOptimizationForm;
import com.crh.logon.order.web.forms.railcaroptimization.RailCarTypeInfoBean;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_RailCarOptimizationForm"
 *  path="/railcaroptimization/RailCarOptimization" scope="session"
 *  validate="true" input="/railcaroptimization/RailCarOptimization.def"
 *
 * @struts.action-forward name="success" path="/railcaroptimization/RailCarOptimization.def"
 * @struts.action-forward name="failure" path="/railcaroptimization/RailCarOptimization.def"
 * @struts.action-forward name="shippingpoints" path="/railcaroptimization/RailCarOptimizationShippingpointsTile.def"
 * @struts.action-forward name="editShippingpoints" path="/railcaroptimization/RailCarOptimizationShippingpointsEditTile.def"
 * @struts.action-forward name="wagontypes" path="/railcaroptimization/RailCarOptimizationWagonTypesTile.def"
 * @struts.action-forward name="editWagonTypes" path="/railcaroptimization/RailCarOptimizationWagonTypesEditTile.def"
 * @struts.action-forward name="wagons" path="/railcaroptimization/RailCarOptimizationWagonsPartTile.def"
 * @struts.action-forward name="wagoncounttable" path="/railcaroptimization/RailCarOptimizationWagonTypeSummaryPartTile.def" 
 * 
 */
public class RailCarOptimizationAction extends OrderActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(RailCarOptimizationAction.class);

    
    public static final String ACTION_SET_PLANT = "setPlant";
    public static final String ACTION_SHOW_WAGONS = "loadWagons";
    public static final String ACTION_EDIT_SHIPPINGPOINTS = "editShippingpoints";
    public static final String ACTION_EDIT_WAGONTYPES = "editWagontypes";
    public static final String ACTION_SET_SHIPPINGPOINTS = "setShippingpoints";
    public static final String ACTION_SET_WAGON_TYPES = "setwagontypes";
    public static final String ACTION_SHOW_WAGON_TYPE_COUNT_TABLE ="wagoncounttable";
    
    public static final String FORWARD_WAGON_TYPE_DROPDOWN ="wagontypes";
    public static final String FORWARD_EDIT_SHIPPINGPOINTS  = "editShippingpoints";
    public static final String FORWARD_EDIT_WAGONTYPES ="editWagonTypes";
    public static final String FORWARD_SHIPPINGPOINT_DROPDOWN ="shippingpoints";
    public static final String FORWARD_SHOW_WAGONS ="wagons";
    public static final String FORWARD_SHOW_WAGON_TYPE_COUNT ="wagoncounttable";
    
    
    
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
			Authorization.authorize(Authorization.RAIL_CAR_OPTIMIZATION);
			setSelectedTab("monitoring", request);
			setSelectedSub1Tab("monitoring", request);
			setSelectedSub2Tab("RailCarOptimization", request);
			
		    // Get the struts form.
			RailCarOptimizationForm helpForm = (RailCarOptimizationForm)form;
			String forwardName = "input";
			
		    DispoService dispoService = getServiceLocator().getDispoService();
			
			if (request.getParameter(ACTION_SET_PLANT) != null) {
			    if (helpForm.getSelectedPlant() != null){
			    	helpForm.setShippingpoints(dispoService.fetchRailShippingpontsForPlant(helpForm.getSelectedPlant()));
			    }
			    else{
			    	helpForm.setShippingpoints(new LinkedList());
			    }
			    helpForm.initSelectedShippingpoints();
				forwardName = FORWARD_SHIPPINGPOINT_DROPDOWN;
			}			
			if (request.getParameter(ACTION_EDIT_SHIPPINGPOINTS) != null){
				forwardName = FORWARD_EDIT_SHIPPINGPOINTS;
			}
			if (request.getParameter(ACTION_SET_SHIPPINGPOINTS) != null){
				forwardName = FORWARD_SHIPPINGPOINT_DROPDOWN;
			}
			if (request.getParameter(ACTION_EDIT_WAGONTYPES) != null){
				forwardName = FORWARD_EDIT_WAGONTYPES;
			}
			if (request.getParameter(ACTION_SET_WAGON_TYPES) != null){
				forwardName = FORWARD_WAGON_TYPE_DROPDOWN;
			}
			if (request.getParameter(ACTION_SHOW_WAGONS) != null){
				//lower table (already assigned wagons table)
				List wagonList = getServiceLocator().getRailCarOptimizationService().fetchWagons(
						new ArrayList(helpForm.getSelectedShippinpointsStationIds()),
						new ArrayList(helpForm.getSelectedWagonTypes()),
						RequestUtils.getUserLocale(request, null).getLanguage(),
						helpForm.getDate());
				helpForm.setWagonList(wagonList);
				forwardName = FORWARD_SHOW_WAGONS;
			}
			if (request.getParameter(ACTION_SHOW_WAGON_TYPE_COUNT_TABLE) != null){
				//summary part (rail car overview part)
				List wagonTypeCount = getServiceLocator().getRailCarOptimizationService().getOptimizationData(helpForm.getDate(), helpForm.getSelectedShippinpointsStationIds(), (helpForm.getSelectedWagonTypes().size() == helpForm.getWagonTypes().size() ? null : helpForm.getSelectedWagonTypes()));
				helpForm.setTypeInfoBean(new RailCarTypeInfoBean(wagonTypeCount));
				List rawList = getServiceLocator().getRailCarOptimizationService().fetchOpenOrders(helpForm.getSelectedShippinpointsStationIds(), request.getLocale().getLanguage() ,helpForm.getDate());
				
				helpForm.setOpenOrders(rawList);
				
				Set shiptoIds = helpForm.getShiptoIds();
				List typePrios = getServiceLocator().getRailCarOptimizationService().getWagonProrityFactorGroupby(shiptoIds, (helpForm.getSelectedWagonTypes().size() == helpForm.getWagonTypes().size() ? null : helpForm.getSelectedWagonTypes()));
				helpForm.getTypeInfoBean().setTypePriorityMap(typePrios);
				
				Map shiptosForWagonTypes = getServiceLocator().getRailCarOptimizationService().getShiptosForWagonTypes( shiptoIds, (helpForm.getSelectedWagonTypes().size() == helpForm.getWagonTypes().size() ? null : helpForm.getSelectedWagonTypes()) );
				helpForm.setShiptoIdsForWagonTypes(shiptosForWagonTypes);
				
				forwardName = FORWARD_SHOW_WAGON_TYPE_COUNT;
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
