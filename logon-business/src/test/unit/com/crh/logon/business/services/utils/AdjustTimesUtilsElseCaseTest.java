/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.util.Calendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;

/**
 * @author AdNovum Informatik AG
 */
public class AdjustTimesUtilsElseCaseTest extends AdjustTimesUtilsTest {

	@Before
	public void setUp() {
		super.setUp();
	}
	
	@Test
	public void testElseBeforeBreak() {

		String[][] deliveryWindows = new String[][] {{"09:00", "10:00"}};
		String[] expected = deliveryWindows[0];

		adjustDeliveryWindowForShipTo(deliveryWindows, expected);
	}

	@Test
	public void testElseAfterBreak() {

		String[][] deliveryWindows = new String[][] {{"14:00", "16:00"}};
		String[] expected = deliveryWindows[0];

		adjustDeliveryWindowForShipTo(deliveryWindows, expected);
	}

	/*
	ShipTo Opening Times
	07:00			11:00 |			| 13:10			18:00
	-------------------------------------------------
  XOOOO	             
	*/
	@Test
	public void testElseBeforeOpening() {

		String[][] deliveryWindows = new String[][] {{"06:30", "09:00"}};
		String[] expected = new String[] { formInTwoDigits(shipToOpen1FromHre) + ":" + shipToOpen1FromMin, "09:00"};

		adjustDeliveryWindowForShipTo(deliveryWindows, expected);
	}
	
	@Test
	public void testElseBeforeOpeningDuringNight() {

		String[][] deliveryWindows = new String[][] {{"01:00", "03:00"}};
		String[] expected = new String[] { formInTwoDigits(shipToOpen1FromHre) + ":" + shipToOpen1FromMin, formInTwoDigits(shipToOpen1FromHre) + ":" + shipToOpen1FromMin};

		adjustDeliveryWindowForShipTo(deliveryWindows, expected, new boolean[]{true});
	}
	
	/*
	ShipTo Opening Times
	07:00			11:00 |			| 13:10			17:00
	-------------------------------------------------
                                                              OOOOXX	             
	*/
	@Test
	public void testElseAfterOpening() {

		String[][] deliveryWindows = new String[][] {{"16:30", "18:00"}};
		String[] expected = new String[] { "16:30", shipToOpen2ToHre + ":" + shipToOpen2ToMin};

		adjustDeliveryWindowForShipTo(deliveryWindows, expected);
	}
	
	/*
	ShipTo Opening Times
	07:00			11:00 |			| 13:00			18:00
	-------------------------------------------------
	                OOOOOX	             
	*/
	@Test
	public void testElseIntersectBefore() {

		String[][] deliveryWindows = new String[][] {{"09:00", "12:11"}};
		String[] expected = new String[] { "09:00", shipToOpen1ToHre + ":00"};

		adjustDeliveryWindowForShipTo(deliveryWindows, expected);
	}

	/*
	ShipTo Opening Times
	07:00			11:00 |			| 13:00			18:00
	-------------------------------------------------
	                                     XOOOOO	             
	*/
	@Test
	public void testElseIntersectAfter() {

		String[][] deliveryWindows = new String[][] {{"12:05", "15:00"}};
		String[] expected = new String[] {shipToOpen2FromHre + ":" + shipToOpen2FromMin,  "15:00"};

		adjustDeliveryWindowForShipTo(deliveryWindows, expected);
	}

	/*
	ShipTo Opening Times
	07:00			11:00 |			| 13:00			18:00
	-------------------------------------------------
	                        OOXXXXX	             
	*/
	@Test
	public void testElseOverlapBefore() {

		String[][] deliveryWindows = new String[][] {{"09:00", "14:01"}};
		String[] expected = new String[] { "09:00", shipToOpen1ToHre + ":" + shipToOpen1ToMin};

		adjustDeliveryWindowForShipTo(deliveryWindows, expected);
	}

	/*
	ShipTo Opening Times
	07:00			11:00 |			| 13:00			18:00
	-------------------------------------------------
	                            XXXXXOO	             
	*/
	@Test
	public void testElseOverlapAfter() {

		String[][] deliveryWindows = new String[][] {{"11:09", "15:00"}};
		String[] expected = new String[] {shipToOpen2FromHre + ":" + shipToOpen2FromMin, "15:00"};

		adjustDeliveryWindowForShipTo(deliveryWindows, expected);

	}

	/*
	ShipTo Opening Times
	07:00			11:00 |			| 13:00			18:00
	-------------------------------------------------
	                            XXXXXO	             
	*/
	@Test
	public void testElseOverlapEquals() {

		String[][] deliveryWindows = new String[][] {{"10:00", "14:22"}};
		String[] expected = new String[] { "10:00", shipToOpen1ToHre + ":00"};
		
		adjustDeliveryWindowForShipTo(deliveryWindows, expected);

	}
	
	@Test
	public void testElseOverlapBeforeMinutes() {

		initializeShipTo("07:00", "12:10", "13:00", "17:00");
		
		String[][] deliveryWindows = new String[][] {{"12:00", "12:55"}};
		String[] expected = new String[] { "12:00", "12:10"};

		adjustDeliveryWindowForShipTo(deliveryWindows, expected);
	}

	/*
	ShipTo Opening Times
	07:00			11:00 |			| 13:10			18:00
	-------------------------------------------------
	                                 XX	             
	*/
	@Test
	public void testElseDeliveryWindowInsidePauseEqual() {

		String[][] deliveryWindows = new String[][] {{"12:15", "12:55"}};
		String[] expected = new String[] { shipToOpen2FromHre + ":" + shipToOpen2FromMin, shipToOpen2FromHre + ":" + shipToOpen2FromMin};

		adjustDeliveryWindowForShipTo(deliveryWindows, expected, new boolean[]{true});

	}
	
	/*
	ShipTo Opening Times
	07:00			11:00 |			| 13:00			18:00
	-------------------------------------------------
	                                 XX	             
	*/
	@Test
	public void testElseDeliveryWindowInsidePauseCloserBefore() {

		String[][] deliveryWindows = new String[][] {{"12:15", "12:30"}};
		String[] expected = new String[] { shipToOpen1ToHre + ":" + shipToOpen1ToMin, shipToOpen1ToHre + ":" + shipToOpen1ToMin};

		adjustDeliveryWindowForShipTo(deliveryWindows, expected, new boolean[]{true});

	}
	
	@Test
	public void testElseDeliveryWindowInsidePauseCloserBefore2() {

		String[][] deliveryWindows = new String[][] {{"11:47", "12:07"}};
		String[] expected = new String[] { "11" + ":" + "47", shipToOpen1ToHre + ":" + shipToOpen1ToMin};

		adjustDeliveryWindowForShipTo(deliveryWindows, expected, new boolean[]{false});

	}
	
	
	/*
	ShipTo Opening Times
	07:00			11:00 |			| 13:00			18:00
	-------------------------------------------------
	                                 XX	             
	*/
	@Test
	public void testElseDeliveryWindowInsidePauseCloserAfter() {

		String[][] deliveryWindows = new String[][] {{"12:30", "12:59"}};
		String[] expected = new String[] { shipToOpen2FromHre + ":" + shipToOpen2FromMin, shipToOpen2FromHre + ":" + shipToOpen2FromMin};

		adjustDeliveryWindowForShipTo(deliveryWindows, expected, new boolean[]{true});

	}
	
	@Test
	public void testSplittedOrderSynchInsidePauseCloserAfter() {

		String[][] deliveryWindows = new String[][] {{"12:30", "12:59"}};

		CustorderEntity custorder = createBulkCustorderWithSDOs(deliveryWindows);

		List<SplittedOrderData> splittedOrderData = AdjustTimesUtils.adjustTimesElse(opening, custorder, shiptoEntity);
		
		SplittedOrderData orderData = splittedOrderData.get(0);
		OrderEntity order = orderData.getOrder();

		Calendar[] deliveryWindow = createCalDeliveryWindow(new String[]{orderData.getDeliveryFromTime(), orderData.getDeliveryToTime()});

		assertOrderIsSameAsDeliveryWindow(order, deliveryWindow);

	}
	
	@Test
	public void testDeliveryBeforeOpening() {

		initializeShipTo("08:15", "12:00", "13:00", "17:00");
		
		String[][] deliveryWindows = new String[][] {{"07:00", "08:00"}};
		String[] expected = new String[]{"08:15", "08:15"};

		adjustDeliveryWindowForShipTo(deliveryWindows, expected, new boolean[] { true });

	}
	
	@Test
	public void testElseInsidePauseOnPauseStart() {

		initializeShipTo("07:00", "08:00", "13:00", "17:00");
		
		String[][] deliveryWindows = new String[][] {{"08:00", "09:00"}};
		String[] expected = new String[]{"08:00", "08:00"};

		adjustDeliveryWindowForShipTo(deliveryWindows, expected,  new boolean[] { true });

	}
	
	private void adjustDeliveryWindowForShipTo(String[][] deliveryWindows, String[] expected) {
		adjustDeliveryWindowForShipTo(deliveryWindows, expected, new boolean[expected.length]);
	}
	
	private void adjustDeliveryWindowForShipTo(String[][] deliveryWindows, String[] expected,
			boolean[] expectedOnTime) {
		
		CustorderEntity custorder = createBulkCustorderWithSDOs(deliveryWindows);

		List<SplittedOrderData> splittedOrderData = AdjustTimesUtils.adjustTimesElse(opening, custorder, shiptoEntity);
		
		for (int i = 0; i < splittedOrderData.size(); i++) {
			SplittedOrderData orderData = splittedOrderData.get(i);
			OrderEntity order = orderData.getOrder();
			Calendar[] deliveryWindow = createCalDeliveryWindow(expected);
			assertOrderIsSameAsDeliveryWindow(order, deliveryWindow);
			Assert.assertEquals(expectedOnTime[i], orderData.isOnTime());
		}
		
	}
	
	@Test
	public void testCutMinutesFromCal() {
		Calendar calendar = applicationTimeZone.asCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, 11);
		calendar.set(Calendar.MINUTE, 12);
		calendar.set(Calendar.SECOND, 13);
		calendar.set(Calendar.MILLISECOND, 14);
		
		Calendar cutCal = AdjustTimesUtils.clearTime(calendar);
		
		Assert.assertEquals(11, cutCal.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(12, cutCal.get(Calendar.MINUTE));
		Assert.assertEquals(0, cutCal.get(Calendar.SECOND));
		Assert.assertEquals(0, cutCal.get(Calendar.MILLISECOND));
		
	}

}