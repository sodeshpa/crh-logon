/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.websevices.clients;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.holcim.logon.eventbeans.in.logon_lds_interface.LocationData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.VehicleData;
import com.crh.logon.webservices.clients.LogonToLDS;
import com.crh.logon.webservices.clients.beans.logon_lds_interface.PdaLoginDTO;

/**
 * @author AdNovum Informatik AG
 */
public class LogonToLDSTestCase extends BaseClientTestCase {

	private static final JcanLogger LOG = JcanLoggerFactory
			.getLogger(LogonToLDSTestCase.class.getName());
	
	/**
	 * @param arg0
	 */
	public LogonToLDSTestCase(String arg0) {
		super(arg0);
		
		// [[
		Object[][] params = {{"arg0", arg0}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "LogonToLDSTestCase", params);
		}
		// ]]
		try {
			; // PMD
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public void testPdaLogin() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testPdaLogin");
		}
		// ]]
		try {
			
			PdaLoginDTO request = new PdaLoginDTO();
			
			// location
			request.setLocationData(new LocationData());
			request.getLocationData().setSupplyPlantNo("1234");
			request.getLocationData().setShippingpointNo("1234");
			// vehicle
			request.setVehicleData(new VehicleData());
			
			LogonToLDS logonToLDS = new LogonToLDS(this.configBean);
			logonToLDS.sendPdaLogin(request, "parallel", "dotternhausen");
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

}
