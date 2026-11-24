/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.logon_lds_interface;

import com.holcim.logon.eventbeans.in.logon_lds_interface.LocationData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.Request;
import com.holcim.logon.eventbeans.in.logon_lds_interface.VehicleData;

/**
 * @author AdNovum Informatik AG
 */
public class PdaLoginDTO extends Request {

	private LocationData locationData;
	private VehicleData vehicleData;

	public LocationData getLocationData() {
		return locationData;
	}

	public void setLocationData(LocationData locationData) {
		this.locationData = locationData;
	}

	public VehicleData getVehicleData() {
		return vehicleData;
	}

	public void setVehicleData(VehicleData vehicleData) {
		this.vehicleData = vehicleData;
	}

}
