/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.simpledispo;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;

import com.crh.logon.business.services.beans.SimpleVehicleAssignmentInfo;
import com.crh.logon.business.services.beans.UnallocatedOrderData;
import com.crh.logon.business.services.interfaces.SimpleVehicleSchedulingService;
import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.forms.simpledispo.SimpleDispoForm;

/**
 * Ancestor class for Simple Vehicle Scheduling Actions.
 * It contains common methods to call.
 * 
 * @author AdNovum Informatik AG
 */
public class SimpleDispoAbstractAction extends OrderActionSupport {
	
	/**
	 * Loads the list of orders for the selected carrier and pool, then sets these to the struts form.
	 */
	protected void loadOrders(SimpleVehicleSchedulingService simpleVehicleSchedulingService, 
			SimpleDispoForm helpForm, Locale locale) {
		
		Long selectedPoolId = helpForm.getSelectedPoolId();
		Set<Long> selectedCarrierIds = helpForm.getSelectedCarriers();
		
		Collection<UnallocatedOrderData> orders = null;

		if (!selectedCarrierIds.isEmpty()) {
			orders = simpleVehicleSchedulingService.getUnallocatedOrders(selectedPoolId, selectedCarrierIds, locale);
			for (UnallocatedOrderData orderData : orders) {
				orderData.setUserTimeZone(getUserTimezone());
			}
		}

		helpForm.setOrders(orders);
	}
	
	/**
	 * Loads the list of vehicles for the selected carrier and sets it in the struts form.
	 */
	protected void loadVehicles(SimpleVehicleSchedulingService simpleVehicleSchedulingService,
			SimpleDispoForm helpForm, Locale locale) {
		
		Set<Long> selectedCarrierIds = helpForm.getSelectedCarriers();
		Collection<SimpleVehicleAssignmentInfo> vehicles = null;
		
		if (!selectedCarrierIds.isEmpty()) {
			vehicles = simpleVehicleSchedulingService.getVehicleAssignmentData(selectedCarrierIds, locale);
			for (SimpleVehicleAssignmentInfo vehicle : vehicles) {
				vehicle.setUserTimeZone(getUserTimezone());
			}
		}

		helpForm.setVehicles(vehicles);
	}
}
