/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.crh.logon.business.services.utils.AdjustTimesUtils.ShiptToOpening;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;

/**
 * @author AdNovum Informatik AG
 */
public class AdjustTimesUtilsTest {

	// TODO (balakirev Aug 31, 2012): rollback if fails

	ShiptoEntity shiptoEntity;
	ShiptToOpening opening;

	Calendar[] shipToOpen1, shipToOpen2;

	int shipToOpen1FromHre = 7;
	int shipToOpen1FromMin = 0;
	
	int shipToOpen1ToHre = 12;
	int shipToOpen1ToMin = 0;
	
	int shipToOpen2FromHre = 13;
	int shipToOpen2FromMin = 10;
	
	int shipToOpen2ToHre = 17;
	int shipToOpen2ToMin = 0;
	
	protected static TimeZone applicationTimeZone = TimeZone.getAppTimeZone();
	
	@Before
	public void setUp() {
		initializeShipTo(
				String.format("%s:%s", formInTwoDigits(shipToOpen1FromHre), formInTwoDigits(shipToOpen1FromMin)),
				String.format("%s:%s", formInTwoDigits(shipToOpen1ToHre), formInTwoDigits(shipToOpen1ToMin)),
				String.format("%s:%s", formInTwoDigits(shipToOpen2FromHre), formInTwoDigits(shipToOpen2FromMin)),
				String.format("%s:%s", formInTwoDigits(shipToOpen2ToHre), formInTwoDigits(shipToOpen2ToMin))
		);

	}

	/**
	 * Parses opening windows and instantiates the shipto and opening variables. 
	 * This method can be called from subclasses to override opening from tests.
	 */
	void initializeShipTo(String open1From, String open1To, String open2From, String open2To) {
		
		long[] open1FromWindow = AdjustTimesUtils.parseOpeningWindow(open1From);
		long[] open1ToWindow = AdjustTimesUtils.parseOpeningWindow(open1To);
		long[] open2FromWindow = AdjustTimesUtils.parseOpeningWindow(open2From);
		long[] open2ToWindow = AdjustTimesUtils.parseOpeningWindow(open2To);
		
		long shipToOpen1FromHre = open1FromWindow[0];
		long shipToOpen1ToHre = open1ToWindow[0];
		long shipToOpen2FromHre = open2FromWindow[0];
		long shipToOpen2ToHre = open2ToWindow[0];
		
		long shipToOpen1FromMin = open1FromWindow[1];
		long shipToOpen1ToMin = open1ToWindow[1];
		long shipToOpen2FromMin = open2FromWindow[1];
		long shipToOpen2ToMin = open2ToWindow[1];
		
		Calendar shipToOpen1FromCal = AdjustTimesUtils.getCalendarFromTime((int)shipToOpen1FromHre, (int)shipToOpen1FromMin);
		Calendar shipToOpen1ToCal = AdjustTimesUtils.getCalendarFromTime((int)shipToOpen1ToHre, (int)shipToOpen1ToMin);

		Calendar shipToOpen2FromCal = AdjustTimesUtils.getCalendarFromTime((int)shipToOpen2FromHre, (int)shipToOpen2FromMin);
		Calendar shipToOpen2ToCal = AdjustTimesUtils.getCalendarFromTime((int)shipToOpen2ToHre, (int)shipToOpen2ToMin);

		shipToOpen1 = new Calendar[] { shipToOpen1FromCal, shipToOpen1ToCal };
		shipToOpen2 = new Calendar[] { shipToOpen2FromCal, shipToOpen2ToCal };

		
		shiptoEntity = new ShiptoEntity();
		shiptoEntity.setOpen1From(shipToOpen1FromHre * 60 + shipToOpen1FromMin);
		shiptoEntity.setOpen1To( shipToOpen1ToHre * 60 + shipToOpen1ToMin);
		shiptoEntity.setOpen2From(shipToOpen2FromHre * 60 + shipToOpen2FromMin);
		shiptoEntity.setOpen2To(shipToOpen2ToHre * 60 + shipToOpen2ToMin);
		
		 opening = new ShiptToOpening(shiptoEntity);
		 
	}
	
	@Test
	public void testReIndexStartsFromZero() {
	
		String[][] deliveryWindows = new String[][] {
				{"09:00", "09:30"}, {"10:00", "10:30"}, {"11:00", "11:30"}, {"12:00", "12:30"}
		};

		CustorderEntity custorder = createBulkCustorderWithSDOs(deliveryWindows);
		List<OrderEntity> orders = custorder.getOrders();
		
		// Create a list with 'corrupted'indexes 
		List<SplittedOrderData> alteredList= new ArrayList<SplittedOrderData>();
		for (int i = 0; i < orders.size(); i++) {
			OrderEntity orderEntity = orders.get(i);
			SplittedOrderData splittedOrderData = new SplittedOrderData(orderEntity, null, null, null, (i+1)*2);
			alteredList.add(splittedOrderData);
		}
		
		AdjustTimesUtils.reIndex(alteredList);
		
		for (int i = 0; i < alteredList.size(); i++) {
			SplittedOrderData splittedOrderData = alteredList.get(i);
			Assert.assertEquals(i, splittedOrderData.getIndex());
		}
		
		
	}
	
	@Test
	public void createCustorderWithSDOsByDeliveryTimes() {

		String[][] deliveryWindows = new String[][] {{"09:00", "17:00"}, {"10:00", "13:00"}};

		CustorderEntity custorder = createBulkCustorderWithSDOs(deliveryWindows);
		List<OrderEntity> orders = custorder.getOrders();
		Assert.assertEquals(2, orders.size());

		for (int i = 0; i < deliveryWindows.length; i++) {
			OrderEntity order = orders.get(i);
			Calendar[] expectedDeliveryWindow = createCalDeliveryWindow(deliveryWindows[i]);
			assertOrderIsSameAsDeliveryWindow(order, expectedDeliveryWindow);
		}

	}
	
	@Test
	public void testCreateShiptToOpening() {
		
		Assert.assertEquals(shipToOpen1FromHre, opening.open1FromHre.intValue());
		Assert.assertEquals(shipToOpen1ToHre, opening.open1ToHre.intValue());
		Assert.assertEquals(shipToOpen2FromHre, opening.open2FromHre.intValue());
		Assert.assertEquals(shipToOpen2ToHre, opening.open2ToHre.intValue());
		
		Assert.assertEquals(shipToOpen1FromMin, opening.open1FromMin.intValue());
		Assert.assertEquals(shipToOpen1ToMin, opening.open1ToMin.intValue());
		Assert.assertEquals(shipToOpen2FromMin, opening.open2FromMin.intValue());
		Assert.assertEquals(shipToOpen2ToMin, opening.open2ToMin.intValue());
		
	}
	
	@Test
	public void testFormatInTwoDigits() {
		
		int hour = 7;
		
		Assert.assertEquals("07", formInTwoDigits(hour));
		
	}
	
	String formInTwoDigits(int hour) {
		
		String format;
		
		if (hour < 10) {
			format = String.format("0%d", hour);
		} 
		else {
			format = Integer.toString(hour);
		}
		
		return format;
	}

	/**
	 * @param order
	 * @param deliveryWindow
	 */
	void assertOrderIsSameAsDeliveryWindow(OrderEntity order, Calendar[] deliveryWindow) {
		
		Date orderDatadeliveryFromDat = order.getDeliveryFromDat();
		Calendar deliveryFromCal = applicationTimeZone.asCalendar();
		deliveryFromCal.setTime(orderDatadeliveryFromDat);

		Date deliveryToDat = order.getDeliveryToDat();
		Calendar deliveryToCal = applicationTimeZone.asCalendar();
		deliveryToCal.setTime(deliveryToDat);

		Calendar windowFrom = deliveryWindow[0];
		Calendar windowTo = deliveryWindow[1];
		
		Assert.assertEquals(AdjustTimesUtils.formatForError(windowFrom, deliveryFromCal), windowFrom, deliveryFromCal);
		Assert.assertEquals(AdjustTimesUtils.formatForError(windowTo, deliveryToCal), windowTo, deliveryToCal);
	}
	
	void assertBulkOrderHasQtyOf(OrderEntity order, double expectedQty) {
		
		List<OrderItemEntity> orderItems = order.getOrderItems();
		OrderItemEntity orderItemEntity = orderItems.get(0);
		Assert.assertEquals(expectedQty, orderItemEntity.getOrderedQuantity().doubleValue(), 0D);
		
	}

	/**
	 * Creates a {@link CustorderEntity} with {@link OrderEntity}s with opening times as defined in  deliveryWindows
	 * @param deliveryWindows as follows: {fromHourOrder1, toHourOrder1}, ..., {fromHourOrderN, toHourOrderN}
	 * @return created {@link CustorderEntity} 
	 */
	CustorderEntity createBulkCustorderWithSDOs(String[][] deliveryWindows) {

		double[] quantities = new double[deliveryWindows.length];
		return createBulkCustorderWithSDOs(deliveryWindows, quantities );
		
	}
	
	/**
	 * Creates a {@link CustorderEntity} with {@link OrderEntity}s with opening times as defined in  deliveryWindows
	 * @param deliveryWindows as follows: {fromHourOrder1, toHourOrder1}, ..., {fromHourOrderN, toHourOrderN}
	 * @return created {@link CustorderEntity} 
	 */
	CustorderEntity createBulkCustorderWithSDOs(String[][] deliveryWindows, double[] quantities) {

		List<OrderEntity> orders = new ArrayList<OrderEntity>();

		for (int i = 0; i < deliveryWindows.length; i++) {

			String[] deliveryWindow = deliveryWindows[i];

			OrderEntity order = new OrderEntity();
			order.setArchivedFlag(false);
			
			double qty = quantities[i];
			
			OrderItemEntity item = new OrderItemEntity();
			item.setOrderedQuantity(new BigDecimal(qty));
			
			List<OrderItemEntity> items = new ArrayList<OrderItemEntity>();
			items.add(item);
			
			order.setOrderItems(items);
			
			Calendar[] deliveryWindowCal = createCalDeliveryWindow(deliveryWindow);
			
			Calendar fromCal = deliveryWindowCal[0];
			Calendar toCal = deliveryWindowCal[1];
			
			order.setDeliveryFromDat(fromCal.getTime());
			order.setDeliveryToDat(toCal.getTime());
			
			DeliveryTypeBsEntity deliveryType = new DeliveryTypeBsEntity();
			deliveryType.setDeliveryTypeCd(1L);
			order.setDeliveryType(deliveryType);

			orders.add(order);
		}

		CustorderEntity custorder = new CustorderEntity();
		custorder.setOrders(orders);

		return custorder;
	}

	/**
	 * Transforms the array containing HRE values to a {@link Calendar} array.
	 * {@link Calendar} instances will be cleared from minutes, seconds, milliseconds.
	 * {@link #clearTime(Calendar)}.
	 */
	Calendar[] createCalDeliveryWindow(String[] is) {
		
		int sepIndex = 2; // 10:23 ':' index is 2
		
		String fromHreStr = is[0].substring(0, sepIndex);
		String fromMinStr = is[0].substring(sepIndex + 1);

		String toHreStr = is[1].substring(0, sepIndex);
		String toMinStr = is[1].substring(sepIndex + 1);
		
		int fromHre = Integer.parseInt(fromHreStr);
		int fromMin = Integer.parseInt(fromMinStr);
		int toHre = Integer.parseInt(toHreStr);
		int toMin = Integer.parseInt(toMinStr);
		
		Calendar from = AdjustTimesUtils.getCalendarFromTime(fromHre, fromMin);
		Calendar to = AdjustTimesUtils.getCalendarFromTime(toHre, toMin);
		
		return new Calendar[]{from, to};
	}
	
}