/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.util.Calendar;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;

/**
 * @author AdNovum Informatik AG
 */
public class AdjustTimesUtilsQuantityCaseTest extends AdjustTimesUtilsTest {

	@Before
	public void setUp() {
		super.setUp();
	}

	@Test
	public void testQuantityIsImportant() {

		String[][] deliveryWindows = new String[][] { 
				{ "10:45", "11:15" }, { "11:21", "11:51" }, { "11:57", "12:27" }, { "12:33", "13:03" }, { "13:09", "13:39" } 
		};
		String[][] expectedDeliveryWindows = new String[][] { 
				{ "10:45", "11:15" }, { "11:21", "11:51" }, { "13:10", "13:37" }, { "13:43", "14:13" }, { "14:19", "14:49" } 
		};
		CustorderEntity custorder = prepareCustorder(deliveryWindows, 36D, 15D, "MIN");
		checkIfCorrect(expectedDeliveryWindows, custorder);

	}
	
	@Test
	public void testQuantityIsImportant22() {

		shipToOpen2FromHre = 13;
		shipToOpen2FromMin = 0;
		
		shipToOpen2ToHre = 17;
		shipToOpen2ToMin = 0;
		
		initializeShipTo(
				String.format("%s:%s", formInTwoDigits(shipToOpen1FromHre), formInTwoDigits(shipToOpen1FromMin)),
				String.format("%s:%s", formInTwoDigits(shipToOpen1ToHre), formInTwoDigits(shipToOpen1ToMin)),
				String.format("%s:%s", formInTwoDigits(shipToOpen2FromHre), formInTwoDigits(shipToOpen2FromMin)),
				String.format("%s:%s", formInTwoDigits(shipToOpen2ToHre), formInTwoDigits(shipToOpen2ToMin))
		);
		
		String[][] deliveryWindows = new String[][] { 
				{ "07:50", "08:10" }, { "09:09", "09:29" }, { "10:28", "10:48" }, { "11:47", "12:07" }, { "13:06", "13:26" } 
		};
		String[][] expectedDeliveryWindows = new String[][] { 
				{ "07:50", "08:10" }, { "09:09", "09:29" }, { "10:28", "10:48" }, { "13:00", "13:07" }, { "14:06", "14:26" }
		};
		CustorderEntity custorder = prepareCustorder(deliveryWindows, 79D, 10D, "MIN");
		checkIfCorrect(expectedDeliveryWindows, custorder);

	}
	
	@Test
	public void testQuantityIsImportant2() {

		String[][] deliveryWindows = new String[][] { 
				{ "07:50", "08:10" }, { "09:09", "09:29" }, { "10:28", "10:48" }, { "11:47", "12:07" }, { "13:06", "13:26" } 
		};
		String[][] expectedDeliveryWindows = new String[][] { 
				{ "07:50", "08:10" }, { "09:09", "09:29" }, { "10:28", "10:48" }, { "13:10", "13:17" }, { "14:16", "14:36" }
		};
		CustorderEntity custorder = prepareCustorder(deliveryWindows, 79D, 10D, "MIN");
		checkIfCorrect(expectedDeliveryWindows, custorder);

	}
	
	@Test
	public void testQuantityIsImportant3() {

		String[][] deliveryWindows = new String[][] { 
				{ "07:50", "08:10" }, { "09:09", "09:29" }, { "10:28", "10:48" }, { "12:47", "13:07" } 
		};
		String[][] expectedDeliveryWindows = new String[][] { 
				{ "07:50", "08:10" }, { "09:09", "09:29" }, { "10:28", "10:48" }, { "13:57", "14:17" }
		};
		CustorderEntity custorder = prepareCustorder(deliveryWindows, 79D, 10D, "MIN");
		checkIfCorrect(expectedDeliveryWindows, custorder);

	}
	
	@Test
	public void testQuantityIsImportant4() {

		String[][] deliveryWindows = new String[][] { 
				{ "07:50", "08:10" }, { "09:09", "09:29" }, { "10:28", "10:48" }, { "12:57", "13:17" } 
		};
		String[][] expectedDeliveryWindows = new String[][] { 
				{ "07:50", "08:10" }, { "09:09", "09:29" }, { "10:28", "10:48" }, { "13:10", "13:17" }
		};
		CustorderEntity custorder = prepareCustorder(deliveryWindows, 79D, 10D, "MIN");
		checkIfCorrect(expectedDeliveryWindows, custorder);

	}

	@Test
	public void testQuantityIsImportant5ShipToOpensLater() {

		initializeShipTo("10:00", "12:00", "13:00", "17:00");

		String[][] deliveryWindows = new String[][] { 
				{ "09:45", "10:15" }, 
				{ "10:45", "11:15" }
		};

		String[][] expectedDeliveryWindows = new String[][] {
				{ "10:00", "10:15" },
				{ "10:45", "11:15" }
		};
		
		CustorderEntity custorder = prepareCustorder(deliveryWindows, 79D, 10D, "MIN");
		checkIfCorrect(expectedDeliveryWindows, custorder);

	}
	
	@Test
	public void testQuantityIsImportant6ShipToClosesMeanwhile() {

		initializeShipTo("10:00", "12:00", "13:00", "17:00");

		String[][] deliveryWindows = new String[][] { 
				{ "15:45", "16:15" }, 
				{ "16:45", "17:15" }
		};

		String[][] expectedDeliveryWindows = new String[][] {
				{ "15:45", "16:15" },
				{ "16:45", "17:00" }
		};
		
		CustorderEntity custorder = prepareCustorder(deliveryWindows, 79D, 10D, "MIN");
		checkIfCorrect(expectedDeliveryWindows, custorder);

	}

	/**
	 * @param expectedDeliveryWindows
	 * @param custorder
	 */
	private void checkIfCorrect(String[][] expectedDeliveryWindows, CustorderEntity custorder) {
		
		List<SplittedOrderData> splittedOrderData = AdjustTimesUtils.adjustTimesQuantity(opening, custorder, shiptoEntity);
		
		Assert.assertEquals(expectedDeliveryWindows.length, splittedOrderData.size());
		Assert.assertEquals(expectedDeliveryWindows.length, custorder.getOrders().size());
		
		for (int i = 0; i < expectedDeliveryWindows.length; i++) {
			
			SplittedOrderData orderData = splittedOrderData.get(i);
			
			OrderEntity order = orderData.getOrder();
			Calendar[] expectedDeliveryWindow = createCalDeliveryWindow(expectedDeliveryWindows[i]);
			assertOrderIsSameAsDeliveryWindow(order, expectedDeliveryWindow);
			Assert.assertEquals(expectedDeliveryWindows[i][0],orderData.getDeliveryFromTime());
			Assert.assertEquals(expectedDeliveryWindows[i][1],orderData.getDeliveryToTime());
			
		}
		
	}

	/**
	 * @param deliveryWindows
	 * @param freqEveryTime TODO
	 * @param tolerance TODO
	 * @param toleranceUom TODO
	 * @return
	 */
	private CustorderEntity prepareCustorder(String[][] deliveryWindows, double freqEveryTime, double tolerance, String toleranceUom) {
		CustorderEntity custorder = createBulkCustorderWithSDOs(deliveryWindows);
		custorder.setDeliveryFreqEveryTime(freqEveryTime);
		custorder.setDeliveryTimeTolerance(tolerance);
		custorder.setDeliveryTimeToleranceUom(toleranceUom);
		return custorder;
	}

}