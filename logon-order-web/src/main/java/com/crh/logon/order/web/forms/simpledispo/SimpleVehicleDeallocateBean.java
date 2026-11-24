/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.simpledispo;

import java.io.Serializable;
import java.util.Collection;
import com.crh.logon.business.services.beans.SimpleVehicleAssignmentInfo;
import com.crh.logon.business.services.beans.SimpleVehicleAssignmentPart;
import com.crh.logon.business.services.utils.LogonStatusUtils;

/** Simple bean to hold the information for the Simple Vehicle Scheduling - Deallocate popup. 
 * @author AdNovum Informatik AG
 */
public class SimpleVehicleDeallocateBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/** The ID of the simple vehicle to load */
	private final SimpleVehicleAssignmentInfo simpleVehicle;

	private String[] selectedOrderIds;

	/**
	 * Creates a new SimpleVehicleDeallocateBean instance with the given simple vehicle info.
	 * 
	 * @throws IllegalStateException - if the vehicle doesn't have any allocated order to deallocate.
	 */
	public SimpleVehicleDeallocateBean(SimpleVehicleAssignmentInfo simpleVehicle)
			throws IllegalStateException {
		
		this.simpleVehicle = simpleVehicle;

		initArray(simpleVehicle);
	}

	private final void initArray(SimpleVehicleAssignmentInfo simpleVehicle)
			throws IllegalStateException {

		Collection<SimpleVehicleAssignmentPart> assigmentParts = simpleVehicle.getParts();
		if (assigmentParts != null) {
			selectedOrderIds = new String[assigmentParts.size()];
			
			// By default, all the orders are selected to deallocation except for those
			// in status 'delivered'
			int j = 0;
			for (SimpleVehicleAssignmentPart part : assigmentParts) {
				final Long orderStatusCd = part.getOrderStatusCd();
				if (LogonStatusUtils.isDeliveredOrBeyond(orderStatusCd)) {
					selectedOrderIds[j] = "off";
				}
				else {
					selectedOrderIds[j] = "on";
				}
				j++;
			}
		}
		else {
			throw new IllegalStateException("The vehicle doesn't have any allocated order to deallocate.!");
		}
	}

	public SimpleVehicleAssignmentInfo getSimpleVehicle() {
		return simpleVehicle;
	}

	public String[] getSelectedOrderIds() {
		return selectedOrderIds;
	}

	public void setSelectedOrderIds(String[] selectedOrderIds) {
		this.selectedOrderIds = selectedOrderIds;
	}
	
	public String getSelectedOrderIdIndexed(int index) {
		return selectedOrderIds[index];
	}
	
	public void setSelectedOrderIdIndexed(int index, String value) {
		selectedOrderIds[index] = value;
	}
}
