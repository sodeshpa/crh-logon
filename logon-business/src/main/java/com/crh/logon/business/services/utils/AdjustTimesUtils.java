/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.CustorderItemEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.utils.time.DateTimeUtils;

/**
 * The single responsibility of this class is to adjust the delivery window of the {@link OrderEntity}s
 * the {@link CustorderEntity} has based on the {@link ShiptoEntity}'s opening times.
 * @author AdNovum Informatik AG
 */
public class AdjustTimesUtils {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(AdjustTimesUtils.class);
	
	private static final int MINUTES_IN_HRE = 60;

	private static final TimeZone defaultTimeZone = TimeZone.getAppTimeZone();

	private AdjustTimesUtils() {
	}

	public static class ShiptToOpening {
		
		Long open1FromHre;
		Long open1ToHre;
		Long open2FromHre;
		Long open2ToHre;
		
		Long open1FromMin;
		Long open1ToMin;
		Long open2FromMin;
		Long open2ToMin;
		
		public ShiptToOpening(Long open1FromHre, Long open1ToHre, Long open2FromHre,
				Long open2ToHre, Long open1FromMin, Long open1ToMin, Long open2FromMin,
				Long open2ToMin) {
			this.open1FromHre = open1FromHre;
			this.open1ToHre = open1ToHre;
			this.open2FromHre = open2FromHre;
			this.open2ToHre = open2ToHre;
			this.open1FromMin = open1FromMin;
			this.open1ToMin = open1ToMin;
			this.open2FromMin = open2FromMin;
			this.open2ToMin = open2ToMin;
		}



		public ShiptToOpening(ShiptoEntity shiptoEntity) {
			
			open1FromHre = shiptoEntity.getOpen1From() / MINUTES_IN_HRE;
			open1ToHre = shiptoEntity.getOpen1To() / MINUTES_IN_HRE;
			open2FromHre = shiptoEntity.getOpen2From() / MINUTES_IN_HRE;
			open2ToHre = shiptoEntity.getOpen2To() / MINUTES_IN_HRE;
			
			open1FromMin = shiptoEntity.getOpen1From() % MINUTES_IN_HRE;
			open1ToMin = shiptoEntity.getOpen1To() % MINUTES_IN_HRE;
			open2FromMin = shiptoEntity.getOpen2From() % MINUTES_IN_HRE;
			open2ToMin = shiptoEntity.getOpen2To() % MINUTES_IN_HRE;
			
		}
		
	}
	
	/**
	 * Wraps the list of {@link OrderEntity} into a list of {@link SplittedOrderData}
	 */
	private static List<SplittedOrderData> prepareForAdjust(CustorderEntity custorder,
			ShiptoEntity shipto) {
		List<OrderEntity> orders = custorder.getOrders();
		
		List<SplittedOrderData> splittedOrderData = new ArrayList<SplittedOrderData>();
		
		for (int index = 0; index <orders.size(); index++) {
			OrderEntity order = orders.get(index);
			SplittedOrderData.DeliveryType deliveryType = LogonUtil.getDeliveryType(order);
			Boolean exactCustorderQtyFlag = CustorderUtils.getExactCustorderQty(custorder);
			splittedOrderData.add(new SplittedOrderData(order, shipto, deliveryType, exactCustorderQtyFlag, index));
		}
		return splittedOrderData;
	}

	/**
	 * Updates the {@link OrderEntity} list of {@link CustorderEntity} with the orders of the passed 
	 * {@link SplittedOrderData} list.
	 */
	private static void postAdjust(CustorderEntity custorder, List<SplittedOrderData> orderData) {
		
		reIndex(orderData);
		
		List<OrderEntity> updatedOrders = new ArrayList<OrderEntity>();
		for (SplittedOrderData splittedOrderData : orderData) {
			updatedOrders.add(splittedOrderData.getOrder());
		}
		
		custorder.setOrders(updatedOrders);
		
	}
	
	/**
	 * Iterates over the given list of {@code SplittedOrderData}s and sets 
	 * their indexes incrementely starting from zero = 0 into a new list
	 * @param orderData - the list to be updated
	 * @return the updated list
	 */
	static void reIndex(List<SplittedOrderData> orderData) {
		for (int i = 0; i < orderData.size(); i++) {
			SplittedOrderData splittedOrderData = orderData.get(i);
			splittedOrderData.setIndex(i);
		}
	}

	/**
	 * Delivery To (time) is important. LRQ 070.0.0/74.0 - 2.2.2.3 
	 * <b>Please note this method assumes that from-to is symmetrical. But it will not necessarily leave it like that.</b>
	 */
	public static List<SplittedOrderData> adjustTimesDeliveryTo(ShiptToOpening opening, CustorderEntity custorder, ShiptoEntity shipto, CustorderSplitWrapper wrapper) {

		List<SplittedOrderData> splittedOrderData = prepareForAdjust(custorder, shipto);
		
		Calendar shipToOpen1FromCal = getCalendarFromTime(opening.open1FromHre.intValue(), opening.open1FromMin.intValue());
		Calendar shipToOpen1ToCal = getCalendarFromTime(opening.open1ToHre.intValue(), opening.open1ToMin.intValue());
		Calendar shipToOpen2FromCal = getCalendarFromTime(opening.open2FromHre.intValue(), opening.open2FromMin.intValue());
		Calendar shipToOpen2ToCal = getCalendarFromTime(opening.open2ToHre.intValue(), opening.open2ToMin.intValue());
		
		long toleranceInMillis =  CustorderUtils.deliveryTimeToleranceInMillis(custorder);
		
		Date custorderDeliveryFromDat = custorder.getDeliveryFromDat();
		Date custorderDeliveryToDat = custorder.getDeliveryToDat();

		Interval custorderDeliveryInterval = new Interval(custorderDeliveryFromDat, custorderDeliveryToDat);
		
		// Bulk has exactly one COI which has a bulk material
		CustorderItemEntity custorderItemEntity = (CustorderItemEntity) custorder.getCustorderItems().get(0);

		// Logic below is that if the qty per hre is filled we calculate the residual frequency based on that.
		// If it's missing than we use the every time attribute
		String custorderItemOrderedUomCd = custorderItemEntity.getOrderedUom().getUomCd();
		BigDecimal perSdorderQuantity = wrapper.get_perSdoQtyInOrderedUom();
		SapMaterialEntity sapMaterial = custorderItemEntity.getSapMaterial();
		
		BigDecimal deliveryFreqQtyperhre = selectFreqDivisorForDeliveryToCase(custorder, wrapper.get_perSdoQtyInOrderedUom());		
		
		String delFreqQtyPerHreUomCd = custorder.getDeliveryFreqQtyperhre() == null ? custorderItemOrderedUomCd : custorder.getDeliveryFreqQtyperhreUom().getUomCd();
		
		BigDecimal frequencyDivisor = wrapper.convertQuantity(sapMaterial, delFreqQtyPerHreUomCd, custorderItemOrderedUomCd, deliveryFreqQtyperhre).getQuantity();
		BigDecimal residualFrequency = perSdorderQuantity.divide(frequencyDivisor, 2, BigDecimal.ROUND_HALF_UP);
		
		// How often we run the trucks (delay)
		long deliveryFrequencyMillis = (long) (TimeUnit.HOURS.toMillis(1) * residualFrequency.doubleValue());
		
		Interval shipToPauseInterval = new Interval(shipToOpen1ToCal, shipToOpen2FromCal);

		Long fastForward = null;
		Iterator<SplittedOrderData> iterator = splittedOrderData.iterator();

		while (iterator.hasNext()) {

			SplittedOrderData splittedOrder = iterator.next();
			
			OrderEntity orderEntity = splittedOrder.getOrder();

			Date deliveryFromDat = orderEntity.getDeliveryFromDat();
			Calendar deliveryFromCal = defaultTimeZone.asCalendar();
			deliveryFromCal.setTime(deliveryFromDat);

			Date deliveryToDat = orderEntity.getDeliveryToDat();
			Calendar deliveryToCal = defaultTimeZone.asCalendar();
			deliveryToCal.setTime(deliveryToDat);

			Interval deliveryInterval = new Interval(deliveryFromCal, deliveryToCal);

			// Here we expect symmetric window!
			Calendar deliveryTimeWithOutTolerance = (Calendar) deliveryFromCal.clone();
			deliveryTimeWithOutTolerance.add(Calendar.MILLISECOND, (int) toleranceInMillis);

			// If the planned delivery time is in the 
			if (shipToPauseInterval.contains(deliveryTimeWithOutTolerance)) {
				fastForward = shipToPauseInterval.lengthInMillis();
			}

			if (fastForward != null) {

				deliveryTimeWithOutTolerance.setTimeInMillis(deliveryTimeWithOutTolerance.getTimeInMillis() + fastForward);

				// If the new shifted time is after the 'to' of the custorder delivery window we have to 
				// remove the order and move on
				if (custorderDeliveryInterval.finishesBefore(deliveryTimeWithOutTolerance)) {
					iterator.remove();
					continue;
				}
				

				// Se to the new delivery time
				deliveryFromCal = (Calendar) deliveryTimeWithOutTolerance.clone();
				deliveryToCal = (Calendar) deliveryTimeWithOutTolerance.clone();

				// Shift with tolerance to create window
				deliveryFromCal.add(Calendar.MILLISECOND, (int) -toleranceInMillis);
				deliveryToCal.add(Calendar.MILLISECOND, (int) toleranceInMillis);

			}
			
			// Here even if the delivery time and the window was shifted we check if the current window
			// intersects with the pause time
			if (deliveryInterval.intersectsBefore(shipToPauseInterval)) {
				deliveryFromCal.set(Calendar.HOUR_OF_DAY, opening.open2FromHre.intValue());
				deliveryFromCal.set(Calendar.MINUTE, opening.open2FromMin.intValue());
				deliveryFromCal = clearTime(deliveryFromCal);
			}
			else if (deliveryInterval.contains(shipToOpen1FromCal)) {
				deliveryFromCal.set(Calendar.HOUR_OF_DAY, opening.open1FromHre.intValue());
				deliveryFromCal.set(Calendar.MINUTE, opening.open1FromMin.intValue());
				deliveryFromCal = clearTime(deliveryFromCal);
			}
			// If the delivery overlaps the open2To - starts before finishes after
			else if (deliveryInterval.contains(shipToOpen2ToCal) && deliveryTimeWithOutTolerance.before(shipToOpen2ToCal)) {
				deliveryToCal.set(Calendar.HOUR_OF_DAY, opening.open2ToHre.intValue());
				deliveryToCal.set(Calendar.MINUTE, opening.open2ToMin.intValue());
				deliveryToCal = clearTime(deliveryToCal);
			}
			// If the delivery is completely before a the shipTo open we remove the delivery
			else if (deliveryInterval.startsBefore(shipToOpen1FromCal) && deliveryInterval.finishesBefore(shipToOpen1FromCal)) {
				iterator.remove();
				continue;
			}
			// If the delivery is completely after a the shipTo closed we remove the delivery
			else if (!deliveryInterval.finishesBefore(shipToOpen2ToCal)) {
				iterator.remove();
				continue;
			}
			// If the delivery overlaps the open1From - starts before finishes after

			orderEntity.setDeliveryFromDat(deliveryFromCal.getTime());
			orderEntity.setDeliveryToDat(deliveryToCal.getTime());
			
			splittedOrder.initialize();
			
			Calendar latestEdge = custorderDeliveryInterval.finishesBefore(shipToOpen2ToCal) ? custorderDeliveryInterval.getTo() : shipToOpen2ToCal;
			Calendar latestEdgeTimeOnly = DateTimeUtils.getTimePart(latestEdge);
			Calendar deliveryTimeWithOutToleranceTimeOnly = DateTimeUtils.getTimePart(deliveryTimeWithOutTolerance);
			
			long millisLeft = latestEdgeTimeOnly.getTimeInMillis() - deliveryTimeWithOutToleranceTimeOnly.getTimeInMillis();
			
			// If the remaining time is less than hour ...
			if (millisLeft < TimeUnit.HOURS.toMillis(1)) {
				
				if (millisLeft < deliveryFrequencyMillis) {
					
					long minutesLeft = TimeUnit.MILLISECONDS.toMinutes(millisLeft);
					double quantityLeft = frequencyDivisor.doubleValue() / TimeUnit.HOURS.toMinutes(1) * minutesLeft;
					
					OrderItemEntity orderItem = bulkGetFirstItem(orderEntity);
					orderItem.setOrderedQuantity(new BigDecimal(quantityLeft));
					
				}
				
			}

		}
		
		postAdjust(custorder, splittedOrderData);
		
		return splittedOrderData;

	}
	
	static OrderItemEntity bulkGetFirstItem(OrderEntity order) {
		
		List<OrderItemEntity> orderItems = order.getOrderItems();
		OrderItemEntity orderItemEntity = orderItems.get(0);
		
		return orderItemEntity;
		
	}

	/**
	 * Quantity is important. LRQ 070.0.0/74.0 - 2.2.2.2 
	 * <b>Please note this method assumes that from-to is symmetrical. But it will not necessarily leave it like that.</b>
	 */
	public static List<SplittedOrderData> adjustTimesQuantity(ShiptToOpening opening, CustorderEntity custorder, ShiptoEntity shipto) {
		
		List<SplittedOrderData> splittedOrderData = prepareForAdjust(custorder, shipto);

		Calendar shipToOpen1FromCal = getCalendarFromTime(opening.open1FromHre.intValue(), opening.open1FromMin.intValue());
		Calendar shipToOpen1ToCal = getCalendarFromTime(opening.open1ToHre.intValue(), opening.open1ToMin.intValue());
		Calendar shipToOpen2FromCal = getCalendarFromTime(opening.open2FromHre.intValue(), opening.open2FromMin.intValue());
		Calendar shipToOpen2ToCal = getCalendarFromTime(opening.open2ToHre.intValue(), opening.open2ToMin.intValue());
		
		long toleranceInMillis = CustorderUtils.deliveryTimeToleranceInMillis(custorder);
		
		Interval shipToPauseInterval = new Interval(shipToOpen1ToCal, shipToOpen2FromCal);

		Long fastForward = null;
		
		Iterator<SplittedOrderData> iterator = splittedOrderData.iterator();

		while (iterator.hasNext()) {

			SplittedOrderData orderData = iterator.next();
			
			OrderEntity orderEntity = orderData.getOrder();

			Date deliveryFromDat = orderEntity.getDeliveryFromDat();
			Calendar deliveryFromCal = defaultTimeZone.asCalendar();
			deliveryFromCal.setTime(deliveryFromDat);

			Date deliveryToDat = orderEntity.getDeliveryToDat();
			Calendar deliveryToCal = defaultTimeZone.asCalendar();
			deliveryToCal.setTime(deliveryToDat);

			Interval deliveryInterval = new Interval(deliveryFromCal, deliveryToCal);

			// Here we expect symmetric window!
			Calendar deliveryTimeWithOutTolerance = (Calendar) deliveryFromCal.clone();
			deliveryTimeWithOutTolerance.add(Calendar.MILLISECOND, (int) toleranceInMillis);

			// If the planned delivery time is in the 
			// if (shipToPauseInterval.contains(deliveryTimeWithOutTolerance)) {
			if (shipToPauseInterval.intersectsAfter(deliveryInterval) || (shipToPauseInterval.startsBefore(deliveryInterval.getFrom()) && shipToPauseInterval.finishesAfter(deliveryInterval.getTo()))) {
				fastForward = shipToPauseInterval.lengthInMillis();
			}

			if (fastForward != null) {

				deliveryTimeWithOutTolerance.setTimeInMillis(deliveryTimeWithOutTolerance.getTimeInMillis() + fastForward);

				// Se to the new delivery time
				deliveryFromCal = (Calendar) deliveryTimeWithOutTolerance.clone();
				deliveryToCal = (Calendar) deliveryTimeWithOutTolerance.clone();

				// Shift with tolerance to create window
				deliveryFromCal.add(Calendar.MILLISECOND, (int) -toleranceInMillis);
				deliveryToCal.add(Calendar.MILLISECOND, (int) toleranceInMillis);

			}

			Interval newDeliveryInterval = new Interval(deliveryFromCal, deliveryToCal);
			
			// Here even if the delivery time and the window was shifted we check if the current window
			// intersects with the pause time
			if (newDeliveryInterval.intersectsBefore(shipToPauseInterval)) {
				deliveryFromCal.set(Calendar.HOUR_OF_DAY, opening.open2FromHre.intValue());
				deliveryFromCal.set(Calendar.MINUTE, opening.open2FromMin.intValue());
				deliveryFromCal = clearTime(deliveryFromCal);
			}
			else if (shipToPauseInterval.intersectsBefore(newDeliveryInterval)) {
				deliveryFromCal.set(Calendar.HOUR_OF_DAY, opening.open2FromHre.intValue());
				deliveryFromCal.set(Calendar.MINUTE, opening.open2FromMin.intValue());
				deliveryFromCal = clearTime(deliveryFromCal);
			} 
			// If the delivery is completely before a the shipTo open we remove the delivery
			else if (deliveryInterval.startsBefore(shipToOpen1FromCal) && deliveryInterval.finishesBefore(shipToOpen1FromCal)) {
				iterator.remove();
				continue;
			}
			// If the delivery is completely after a the shipTo closed we remove the delivery
			else if (!deliveryInterval.finishesBefore(shipToOpen2ToCal) && deliveryInterval.startsAfter(shipToOpen2ToCal)) {
				iterator.remove();
				continue;
			}
			// If the delivery overlaps the open1From - starts before finishes after
			else if (deliveryInterval.startsBefore(shipToOpen1FromCal)) {
				deliveryFromCal.set(Calendar.HOUR_OF_DAY, opening.open1FromHre.intValue());
				deliveryFromCal.set(Calendar.MINUTE, opening.open1FromMin.intValue());
				deliveryFromCal = clearTime(deliveryFromCal);
			}
			// If the delivery overlaps the open2To - starts before finishes after
			else if (deliveryInterval.finishesAfter(shipToOpen2ToCal)) {
				deliveryToCal.set(Calendar.HOUR_OF_DAY, opening.open2ToHre.intValue());
				deliveryToCal.set(Calendar.MINUTE, opening.open2ToMin.intValue());
				deliveryToCal = clearTime(deliveryToCal);
			}

			orderEntity.setDeliveryFromDat(deliveryFromCal.getTime());
			orderEntity.setDeliveryToDat(deliveryToCal.getTime());
			
			orderData.initialize();

		}
		
		postAdjust(custorder, splittedOrderData);
		
		return splittedOrderData;

	}

	/**
	 * @param shiptoEntity
	 * @param orders
	 */
	public static List<SplittedOrderData> adjustTimesElse(ShiptToOpening opening, CustorderEntity custorder, ShiptoEntity shipto) {

		List<SplittedOrderData> splittedOrderData = prepareForAdjust(custorder, shipto);
		
		Calendar shipToOpen1FromCal = getCalendarFromTime(opening.open1FromHre.intValue(), opening.open1FromMin.intValue());
		Calendar shipToOpen1ToCal = getCalendarFromTime(opening.open1ToHre.intValue(), opening.open1ToMin.intValue());
		Calendar shipToOpen2FromCal = getCalendarFromTime(opening.open2FromHre.intValue(), opening.open2FromMin.intValue());
		Calendar shipToOpen2ToCal = getCalendarFromTime(opening.open2ToHre.intValue(), opening.open2ToMin.intValue());

		Interval shipToPauseInterval = new Interval(shipToOpen1ToCal, shipToOpen2FromCal);

		for (SplittedOrderData orderData : splittedOrderData) {
			
			OrderEntity orderEntity = orderData.getOrder();

			Date deliveryFromDat = orderEntity.getDeliveryFromDat();
			Calendar deliveryFromCal = defaultTimeZone.asCalendar();
			deliveryFromCal.setTime(deliveryFromDat);

			Date deliveryToDat = orderEntity.getDeliveryToDat();
			Calendar deliveryToCal = defaultTimeZone.asCalendar();
			deliveryToCal.setTime(deliveryToDat);

			Interval deliveryInterval = new Interval(deliveryFromCal, deliveryToCal);

			int deliveryFromHre = deliveryInterval.getFrom().get(Calendar.HOUR_OF_DAY);
			int deliveryToHre = deliveryInterval.getTo().get(Calendar.HOUR_OF_DAY);

			// Inside Pause
			if (deliveryInterval.inside(shipToPauseInterval)) {

				Interval[] residual = shipToPauseInterval.substract(deliveryInterval);

				Interval beforeInterval = residual[0];
				Interval afterInterval = residual[1];

				if (beforeInterval.shorterThan(afterInterval)) {
					deliveryFromCal.set(Calendar.HOUR_OF_DAY, opening.open1ToHre.intValue());
					deliveryFromCal.set(Calendar.MINUTE, opening.open1FromMin.intValue());
					orderEntity.setDeliveryFromDat(clearTime(deliveryFromCal).getTime());
					deliveryToCal.set(Calendar.HOUR_OF_DAY, opening.open1ToHre.intValue());
					deliveryToCal.set(Calendar.MINUTE, opening.open1FromMin.intValue());
					orderEntity.setDeliveryToDat(clearTime(deliveryToCal).getTime());
				}
				else {
					deliveryFromCal.set(Calendar.HOUR_OF_DAY, opening.open2FromHre.intValue());
					deliveryFromCal.set(Calendar.MINUTE, opening.open2FromMin.intValue());
					orderEntity.setDeliveryFromDat(clearTime(deliveryFromCal).getTime());
					deliveryToCal.set(Calendar.HOUR_OF_DAY, opening.open2FromHre.intValue());
					deliveryToCal.set(Calendar.MINUTE, opening.open2FromMin.intValue());
					orderEntity.setDeliveryToDat(clearTime(deliveryToCal).getTime());
				}
				
			}
			// Overlap before after
			// else if (calendarBeforeOrEqHre(deliveryFromCal, opening.open1ToHre)
			//		&& !calendarBeforeOrEqHre(deliveryToCal, opening.open2FromHre)) {
			else if (deliveryInterval.startsBefore(shipToPauseInterval.getFrom())
					&& deliveryInterval.finishesAfter(shipToPauseInterval.getTo())) {

				long beforeDifference = opening.open1ToHre - deliveryFromHre;
				long afterDifference = deliveryToHre - opening.open2FromHre;

				// After
				if (beforeDifference < afterDifference) {
					deliveryFromCal.set(Calendar.HOUR_OF_DAY, opening.open2FromHre.intValue());
					deliveryFromCal.set(Calendar.MINUTE, opening.open2FromMin.intValue());
					orderEntity.setDeliveryFromDat(clearTime(deliveryFromCal).getTime());
				}
				// Before
				else {
					deliveryToCal.set(Calendar.HOUR_OF_DAY, opening.open1ToHre.intValue());
					deliveryToCal.set(Calendar.MINUTE, opening.open1ToMin.intValue());
					orderEntity.setDeliveryToDat(clearTime(deliveryToCal).getTime());
				}

			}
			// Intersect before
			else if (deliveryInterval.intersectsBefore(shipToPauseInterval)) {
				deliveryToCal.set(Calendar.HOUR_OF_DAY, opening.open1ToHre.intValue());
				deliveryToCal.set(Calendar.MINUTE, opening.open1ToMin.intValue());
				orderEntity.setDeliveryToDat(clearTime(deliveryToCal).getTime());
			}
			// Intersect after
			else if (deliveryInterval.intersectsAfter(shipToPauseInterval)) {
				deliveryFromCal.set(Calendar.HOUR_OF_DAY, opening.open2FromHre.intValue());
				deliveryFromCal.set(Calendar.MINUTE, opening.open2FromMin.intValue());
				orderEntity.setDeliveryFromDat(clearTime(deliveryFromCal).getTime());
			}
			
			if (deliveryInterval.startsBefore(shipToOpen1FromCal)) {
				deliveryFromCal.set(Calendar.HOUR_OF_DAY, opening.open1FromHre.intValue());
				deliveryFromCal.set(Calendar.MINUTE, opening.open1FromMin.intValue());
				orderEntity.setDeliveryFromDat(clearTime(deliveryFromCal).getTime());
			}
			if (deliveryInterval.finishesBefore(shipToOpen1FromCal)) {
				deliveryToCal.set(Calendar.HOUR_OF_DAY, opening.open1FromHre.intValue());
				deliveryToCal.set(Calendar.MINUTE, opening.open1FromMin.intValue());
				orderEntity.setDeliveryToDat(clearTime(deliveryToCal).getTime());
			}
			
			if (deliveryInterval.finishesAfter(shipToOpen2ToCal)) {
					deliveryToCal.set(Calendar.HOUR_OF_DAY, opening.open2ToHre.intValue());
					deliveryToCal.set(Calendar.MINUTE, opening.open2ToMin.intValue());
					orderEntity.setDeliveryToDat(clearTime(deliveryToCal).getTime());
			}
			
			if (deliveryToCal.equals(deliveryFromCal)) {
				orderData.setOnTime(true);
			}
			
			orderData.initialize();

		}
		
		postAdjust(custorder, splittedOrderData);
		
		return splittedOrderData;
		
	}

	/**
	 * Create a {@link Calendar} instance with the given hre value and clears seconds, milliseconds
	 * {@link #clearTime(Calendar)}
	 */
	static Calendar getCalendarFromTime(int hre, int minute) {
		Calendar calendar = defaultTimeZone.asCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, hre);
		calendar.set(Calendar.MINUTE, minute);
		Calendar cleared = clearTime(calendar);
		return cleared;
	}

	/**
	 * Clears second, millisecond from the given calendar
	 */
	static Calendar clearTime(Calendar fromCal) {
		
		Calendar clone = (Calendar) fromCal.clone();
		
		clone.set(Calendar.SECOND, 0);
		clone.set(Calendar.MILLISECOND, 0);
		
		return clone;
		
	}

	/**
	 * Formats the given two Calendar to be humanly readable.
	 * YYYY-MM-DD HH:MM:SS
	 */
	public static String formatForError(Calendar windowFrom, Calendar deliveryFromCal) {

		String from = formatCalendar(windowFrom);
		String fromCal = formatCalendar(deliveryFromCal);

		return String.format("%S <> %S", from, fromCal);

	}

	private static String formatCalendar(Calendar calendar) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");
		String from = sdf.format(calendar.getTime());
		return from;
	}
	
	/**
	 * input: "10:00"
	 * output: []{10, 00}
	 */
	public static long[] parseOpeningWindow(String opening) {

		int sepIndex = 2; // 10:23 ':' index is 2
		
		String open1fromHreStr = opening.substring(0, sepIndex);
		String open1fromMinStr = opening.substring(sepIndex + 1);

		long open1fromHre = Long.parseLong(open1fromHreStr);
		long open1fromMin = Long.parseLong(open1fromMinStr);
		
		long open1From[] = new long[] { open1fromHre, open1fromMin };
		
		return open1From;
	}

	/**
	 * Returns deliveryFreqQtyperhre if exists.
	 * If not calculates the qty to be delivered per hour using the deliveryFreqEveryTime. 
	 */
	public static BigDecimal selectFreqDivisorForDeliveryToCase(CustorderEntity custorder, BigDecimal perSdoOrderQtyInOrderedUom) {
		// [[
		BigDecimal result = null;
		Object[][] params = {{"custorder", custorder},
							{"perSdoOrderQtyInOrderedUom", perSdoOrderQtyInOrderedUom}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(AdjustTimesUtils.class, "selectFreqDivisorForDeliveryToCase", params);
		}
		// ]]
		try {
			
			if (custorder.getDeliveryFreqQtyperhre() != null) {
				return result = custorder.getDeliveryFreqQtyperhre();
			}
				
			else if (custorder.getDeliveryFreqEveryTime() != null) {
				BigDecimal freqEveryTime = new BigDecimal(custorder.getDeliveryFreqEveryTime().toString());
				
				if (custorder.getDeliveryFreqEveryTimeUom().equals(Constant.UOM_HRE)) {
					result = perSdoOrderQtyInOrderedUom.divide(freqEveryTime);
				}
				else if (custorder.getDeliveryFreqEveryTimeUom().equals(Constant.UOM_MIN)) {
					BigDecimal minutesInHour = new BigDecimal("60");
					BigDecimal deliveriesPerHre = minutesInHour.divide(freqEveryTime);
					result = deliveriesPerHre.multiply(perSdoOrderQtyInOrderedUom);
				} 
				
				return result;
			}
			
			throw new IllegalStateException("For delivery-to case one of the following fields must be filled! [deliveryFreqQtyperhre, deliveryFreqEveryTime]");
			
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