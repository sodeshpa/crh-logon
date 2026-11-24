/*
uthor            : AdNovum Informatik AGpublic static final String FORWARD_RENDER_SHIPPINGPOINTS ="shippingpoints";public static final String FORWARD_RENDER_SHIPPINGPOINTS ="shippingpoints";public static final String FORWARD_RENDER_SHIPPINGPOINTS ="shippingpoints";public static final String FORWARD_RENDER_SHIPPINGPOINTS ="shippingpoints";public static final String FORWARD_RENDER_SHIPPINGPOINTS ="shippingpoints";public static final String FORWARD_RENDER_SHIPPINGPOINTS ="shippingpoints";public static final String FORWARD_RENDER_SHIPPINGPOINTS ="shippingpoints";public static final String FORWARD_RENDER_SHIPPINGPOINTS ="shippingpoints";public static final String FORWARD_RENDER_SHIPPINGPOINTS ="shippingpoints";
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.railcaroptimization;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.forms.railcaroptimization.RailCarOptimizationForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.DispoService;
import com.crh.logon.persistence.entities.views.RailPlantEntity;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_RailCarOptimizationForm" path="/railcaroptimization/RailCarOptimizationEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="/railcaroptimization/RailCarOptimization.def"
 * @struts.action-forward name="failure" path="/railcaroptimization/RailCarOptimization.def"
 */
public class RailCarOptimizationEntryAction extends OrderActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RailCarOptimizationEntryAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		// [[
		ActionForward result1 = null;
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
			// [[
			ActionForward result = null;
			
				Authorization.authorize(Authorization.RAIL_CAR_OPTIMIZATION);
				RailCarOptimizationForm helpForm;
				String forwardName = "success";
				setSelectedTab("monitoring", request);
				setSelectedSub1Tab("monitoring", request);
				setSelectedSub2Tab("RailCarOptimization", request);
				
				
				// Get the struts form.
				helpForm = (RailCarOptimizationForm) form;
				helpForm.clear();

				helpForm.setTimeZone(getUserTimezone());
				helpForm.setDate(getUserTimezone().today());
				
				if (helpForm.getPlants() == null) {
					DispoService dispoService = getServiceLocator().getDispoService();
					List plants = dispoService.fetchAllRailPlants();
					if ( !plants.isEmpty() ){
						helpForm.setPlants(plants);
						helpForm.setSelectedPlant(((RailPlantEntity)plants.get(0)).getPlantCd());
						if ((plants != null) && (plants.size() > 0)){
							helpForm.setShippingpoints(dispoService.fetchRailShippingpontsForPlant(helpForm.getSelectedPlant()));
						}
						else{
							helpForm.setShippingpoints(new LinkedList());
				
						}
						helpForm.initSelectedShippingpoints();
					}			
				} 
				List wagonTypes  = getServiceLocator().getWagonSearchService().getWagonTypeBsList();
				helpForm.setWagonTypes(wagonTypes);

				if ((wagonTypes != null) && (wagonTypes.size() > 0)){
					helpForm.setWagonTypes(wagonTypes);
				}
				else{
					helpForm.setShippingpoints(new LinkedList());

				}
				helpForm.initSelectedWagonTypes();

				return result1 = result = mapping.findForward(forwardName);
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result1);
			}
		// ]]
		}
	}
	}
