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
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.TruckAdminService;
import com.crh.logon.order.web.forms.dispo.EditHomePlantForm;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.DropdownEntity;
import com.crh.logon.web.utils.MessageUtils;
import com.crh.logon.web.utils.PlantSareaUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_dispo_EditHomePlantForm"
 *  path="/dispo/EditHomePlantEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="DispoEditHomePlantPopup.def"
 */
public class EditHomePlantEntryAction extends OrderActionSupport {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(EditHomePlantEntryAction.class);
	
	private final String ACTION_FORWARD_SUCCESS = "success";

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
			
			Long truckId;
			try {
				truckId = Long.valueOf(request.getParameter("truckId"));
			}
			catch (NumberFormatException e) {
				throw new LogonException("truckId parameter can't be converted to Long",
						Reason.RES_ERROR_UNSPECIFIED);
			}
			
			TruckAdminService truckAdminService = getServiceLocator().getTruckAdminService();
			
			TruckEntity truck = truckAdminService.fetchTruck(truckId);
			if (truck != null) {
				
				// Clear the previous values
				helpForm.init();
				
				// Set the truck
				helpForm.setTruck(truck);
				
				// Load the plants
				final List<DropdownEntity> plants = PlantSareaUtils.getPlantList(getPlantSareaService());
				if (plants.isEmpty()) {
					MessageUtils.addActionWarning(request, "dispo.truckhomeplant.message.noplants");
					return result = mapping.findForward(forwardName);
				}
				
				helpForm.setPlants(plants);
				
				List<String> plantCds = new ArrayList<String>();
				for (DropdownEntity plant : plants) {
					plantCds.add(plant.getValue());
				}
				
				// Load all the shipping points for the plants
				List<ShippingpointEntity> allShippingpoints = getServiceLocator().getShippingPointService().
						loadShippingPoints(null, true, plantCds, true);
				helpForm.setAllShippingpoints(allShippingpoints);
				
				// Remove those plants, which do not have connection to a shippingpoint 
				for (Iterator<DropdownEntity> it = plants.iterator(); it.hasNext();) {
					DropdownEntity plant = it.next();
					final String plantCd = plant.getValue();

					boolean exists = PlantSareaUtils.isShippingpointExistForPlantCd(allShippingpoints, plantCd);

					if (!exists) {
						it.remove();
					}
				}
				
				List<ShippingpointEntity> shippingpoints = new ArrayList<ShippingpointEntity>();
				
				// The truck may have Home Plant set, fill in the appropriate values
				if (truck.getReturnShippingpoint() != null) {
					Long returnShippingpointId = truck.getReturnShippingpoint().getShippingpointId();
					String plantCd = truck.getReturnShippingpoint().getPlant().getPlantCd();
					
					//Filter all shipping points for a plantCd 
					shippingpoints = PlantSareaUtils.getShippingpointsForPlantCd(allShippingpoints, plantCd);
					
					helpForm.setSelectedPlant(plantCd);
					helpForm.setSelectedShippingpoint(returnShippingpointId);
				}
				
				helpForm.setShippingpoints(shippingpoints);
			}
			else {
				throw new LogonException(MessageUtils.getMessage(request, "dispo.truckhomeplant.message.loadtruckerror"), 
						Reason.RES_ERROR_UNSPECIFIED);
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
