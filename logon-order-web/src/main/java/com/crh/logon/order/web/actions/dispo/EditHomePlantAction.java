/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.dispo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.forms.dispo.EditHomePlantForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.TruckAdminService;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;
import com.crh.logon.web.utils.PlantSareaUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_dispo_EditHomePlantForm"
 *  path="/dispo/EditHomePlant" scope="session"
 *  validate="false" input="DispoTile2.def"
 *
 * @struts.action-forward name="success" path="DispoEditHomePlantPopup.def"
 * @struts.action-forward name="saveClose" path="DispoCloseEditHomePlantPopup.def"
 */
public class EditHomePlantAction extends OrderActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(EditHomePlantAction.class);
	
	private final String ACTION_FORWARD_SUCCESS = "success";
	private final String ACTION_FORWARD_SAVECLOSE = "saveClose";
	
	private final String PARAM_SETPLANT = "setPlant";
	private final String PARAM_SAVECLOSE = "saveClose";

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
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
			Authorization.authorize(Authorization.DISPATCHING_W);
			
			EditHomePlantForm helpForm = (EditHomePlantForm) form;
			String forwardName = ACTION_FORWARD_SUCCESS;
			
			if (request.getParameter(PARAM_SETPLANT) != null) {
			
				if (!Constant.INVALID_OBJECT_ID_STR.equals(helpForm.getSelectedPlant())) {
			
					helpForm.resetShippingpiointFields();
					String plantCd = helpForm.getSelectedPlant();
			
					List<ShippingpointEntity> allShippingpoints = helpForm.getAllShippingpoints();
					List<ShippingpointEntity> shippingpoints = PlantSareaUtils.
							getShippingpointsForPlantCd(allShippingpoints, plantCd);
					helpForm.setShippingpoints(shippingpoints);
			
					if (shippingpoints.size() == 1) {
						Long shippingpointId = ((ShippingpointEntity) shippingpoints.get(0)).getShippingpointId();
						helpForm.setSelectedShippingpoint(shippingpointId);
					}
				}
				else {
					List<ShippingpointEntity> shippingpoints = new ArrayList<ShippingpointEntity>();
					helpForm.setShippingpoints(shippingpoints);
				}
				
			}
			
			if (request.getParameter(PARAM_SAVECLOSE) != null) {
			
				TruckEntity truck = helpForm.getTruck();
				Long returnShippingpointId = helpForm.getSelectedShippingpoint();
				ShippingpointEntity returnShippingpoint = null;
			
				if (returnShippingpointId != -1) {
					List<ShippingpointEntity> shippingpoints = helpForm.getShippingpoints();
					for (Iterator<ShippingpointEntity> it = shippingpoints.iterator(); it.hasNext();) {
						ShippingpointEntity shippingpoint = it.next();
						if (shippingpoint.getShippingpointId().equals(returnShippingpointId)) {
							returnShippingpoint = shippingpoint;
							break;
						}
					}
				}
				truck.setReturnShippingpoint(returnShippingpoint);
				
				// Update the truck
				TruckAdminService truckAdminService = getServiceLocator().getTruckAdminService();
				truck = truckAdminService.updateTruckSimple(truck, true);
				helpForm.setTruck(truck);
				
				MessageUtils.addActionMessage(request, "dispo.truckhomeplant.message.success.save");

				callReloadTrucksJs(request);

				forwardName = ACTION_FORWARD_SAVECLOSE;
				return result = mapping.findForward(forwardName);
				
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
	
	/**
	 * Sets the callReloadTrucksJs = true attribute in the request,
	 * which indicates that the truck list need to be refreshed on the screen. 
	 */
	private void callReloadTrucksJs(HttpServletRequest request) {
		request.setAttribute("callReloadTrucksJs", true);
	}

}
