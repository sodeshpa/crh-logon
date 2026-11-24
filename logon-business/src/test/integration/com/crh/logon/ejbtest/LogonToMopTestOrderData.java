/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.ejbtest;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.DispoService;
import com.crh.logon.business.services.interfaces.OrderService;
import com.crh.logon.business.services.interfaces.UnitTestHelperService;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;


/**
 * @author AdNovum Informatik AG
 */
public class LogonToMopTestOrderData extends JunitSupport {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(
			LogonToMopTestCase.class.getName());
	
	public LogonToMopTestOrderData(String testCaseName) {
		super(testCaseName);
	}
	
	/**
	 * testOrderData
	 * 
	 * triggers the soapUI Test Case "orderDataWithParallel9013_Remote"
	 * 
	 * IMPORTANT: 
	 * The hard coded "truckID" must correspond with the one belonging to 
	 * "truckLabel" in logon-simulator/etc/soapUI/testdata.properties
	 * 
	 * the order belonging to the hard coded "orderNr" 
	 * can be created through the application. status must be "assigned to carrier"
	 * 
	 */
	public void testOrderData() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testOrderData");
		}
		// ]]
		try {
			LOG.info("* * *	t e s t O r d e r D a t a	* * *");
			final long orderNr = 10001285;
			final long truckID = 1427; //in DB Hu this is Zwissig79
			//final long truckID = 1700; //in DB Hu this is Zwissig14
			final int loadDuration = 30; //in min.
			final int transportDuration = 120; //in min.
			final int unloadDuration = 30; //in min
			Calendar rightNow = Calendar.getInstance();
			rightNow.set(Calendar.HOUR_OF_DAY, 9);
			Date startTime = rightNow.getTime();
			rightNow.set(Calendar.HOUR_OF_DAY, 17);
			Date endTime = rightNow.getTime();

			TimeZone timeZone = new TimeZone("Europe/Paris", Locale.ENGLISH);
			
			OrderService orderService = serviceLocator.getOrderService();
			
			// update deliveryFrom/To of the order			
			OrderEntity orderEntity = orderService.findOrderByOrderNumber(orderNr, true);
			orderEntity.setDeliveryFromDat(startTime);
			orderEntity.setDeliveryToDat(endTime);
			orderService.saveOrder(orderEntity);
			
			UnitTestHelperService helperService = serviceLocator.getUnitTestHelperService();
			// FIXME (laszlo Aug 31, 2009): This is commented out because there is a compile error,
			// fix the compile error because its needed.
			/*if (helperService.existsActiveAssignmentToOrderNr(orderNr)) {
				helperService.removeAssignmentFromTestOrder(orderNr);
			}*/
			
			AssignmentEntity assignmentEntity = orderService.createAssignment(
					truckID, null, orderNr, startTime, timeZone, loadDuration, transportDuration, unloadDuration);
			
			DispoService dispoService = serviceLocator.getDispoService();
			
			dispoService.transmitToDriver(assignmentEntity.getAssignmentId(), Constant.WEIGH_SYSTEM_SP_NONE, Constant.WEIGH_SYSTEM_LP_LOGON);
			
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
