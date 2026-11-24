/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.ejbtest;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.DispokarteService;

/**
 * @author AdNovum Informatik AG
 */
public class DispokarteServiceTestCase extends JunitSupport {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(
			DispokarteServiceTestCase.class.getName());
	
	public DispokarteServiceTestCase(String testCaseName) {
		super(testCaseName);
	}
	
	/**
	 * Tests the sample service
	 */
	public void testHasBpRestrictions() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testHasBpRestrictions");
		}
		// ]]
		try {
			DispokarteService dispokarteService = serviceLocator.getDispokarteService();
			
			//this is shipto rutschung fabrikwald
			//ShiptoEntity shipTo = dispokarteService.fetchShiptoByBpIdForEdit("1210275");
			//List silos = shipTo.getSilos();
			
			//comment out if you want to move from bausilo rutschung to the first silo
			//RfidobjectEntity removeFrom = ((ShiptoSiloEntity) silos.get(2)).getRfidobject();
			//RfidobjectEntity addTo = ((ShiptoSiloEntity) silos.get(0)).getRfidobject();
			
			//comment out if you want to move from first silo to bausilo rutschung
			//RfidobjectEntity removeFrom = ((ShiptoSiloEntity) silos.get(0)).getRfidobject();
			//RfidobjectEntity addTo = ((ShiptoSiloEntity) silos.get(2)).getRfidobject();
			
			//RfidtagEntity tag = (RfidtagEntity) removeFrom.getRfids().get(0);
			//tag.setRfidobject(addTo);
			
			//TEST 1
			//THIS IS THE CURRENT UI VERSION
			//ok, but throws detached in case of moving from second to first
			//if you comment out rfidservicebean row 102 nothing happens
			
			//TEST 2
			//Object object = removeFrom.getRfids().get(0);
			//removeFrom.getRfids().remove(object);
			//addTo.getRfids().add(object);
			//not ok, deletes and inserts
			//if you comment out rfidservicebean row 102 nothing happens
			
			//TEST 3
			//RfidtagEntity tag = (RfidtagEntity) removeFrom.getRfids().get(0);
			//removeFrom.getRfids().remove(tag);
			//tag.setRfidobject(addTo);
			//addTo.getRfids().add(tag);
			//ok if 0 to 2, detached exception in case 2 to 0, otherwise deletes and inserts -- > NOT OK
			//if you comment out rfidservicebean row 102 nothing happens
			
			//dispokarteService.mergeShipto(shipTo);
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






