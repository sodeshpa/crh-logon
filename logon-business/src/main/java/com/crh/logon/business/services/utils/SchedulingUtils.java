/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.impl.FrozenTablesServiceBean;
import com.crh.logon.business.services.localinterfaces.ConfigServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TruckAdminServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.views.DispoDriverBreakListEntity;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.time.DateTimeUtils;

/**
 * @author AdNovum Informatik AG
 */
public class SchedulingUtils {

    private static final long MINUTES_IN_A_DAY = 60l * 24l;
	private static final int ONE_MINUTE_IN_MILLIS = 60*1000;
	private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(SchedulingUtils.class);

    /**
     * Returns the break length in milliseconds.
     * @param from
     * @param to 
     * @param breaks
     * @return
     */
    public static long getBreakLengthBetween(Date from, Date to, List<DispoDriverBreakListEntity> breaks) {

    	Iterator it = breaks.iterator();

    	long breakLength = 0l;
    	while (it.hasNext()) {
    		DispoDriverBreakListEntity driverBreak = (DispoDriverBreakListEntity) it.next();
    		if ( driverBreak.getStartDat().getTime() < from.getTime() && driverBreak.getEndDat().getTime() > from.getTime() ) {					
    			long millis = driverBreak.getEndDat().getTime() - from.getTime();				
    			breakLength += millis;
    		}
    		else if (to != null && driverBreak.getStartDat().getTime() >= from.getTime() && driverBreak.getStartDat().getTime() < to.getTime()) {
    			long millis = driverBreak.getEndDat().getTime() - driverBreak.getStartDat().getTime();				
    			breakLength += millis;
    		}
    	}
    	return breakLength;
    }

	/**
	 * Supposed to return the IS time of the start of the third balken shown on the truck dispo screen for truck orders, and 
	 * does not consider at all (of course) driver breaks in case driver is not passed in.
	 * 
	 * Returns the time of the delivery according to real start values of the assignment parts.
	 * The time depends on the driver, because he has breaks between the certain phases of the assignment,
	 * but as far the breaks are not directly added to the planned starts, we have to count with them.
	 * If there are no real start values, then the calculated delivery time will be according to the plan,
	 * or if that would be in the past, and it is already trasmitted to the driver,
	 *  then we suppose that loading will start asap, and delivery will 
	 * be postponed as time goes on until the first real start is not set.
	 * All the time, breaks can have an effect on the calculated time.
	 * Example:
	 * 1) the driver might have break according to plan on that day - the calculated delivery is not what is on the unload assignment part, but 
	 * we have to add the breaks inbetween according to the plan
	 * 2) if the assignment is not going according to plan, the break must be applied also when the assignment is late,
	 * f. ex. according to plan the driver wills start loading at 12:30 though the load part planned time is 12, because
	 * he has lunch break from 12-12:30. Then if he does not start at 12:30 the assignment is late, suppose its already at 15:00,
	 * and he is supposed to start right in that moment, but if he has break from 15:00-15:15, then for sure he will not start until he has break,
	 * and we can then just suppose that he will start loading at 15:15. As far loading is postponed, delivery will also reflect 
	 * this additional time.
	 * @param order
	 * @param assignment
	 * @param services
	 * @return
	 */
	public static Date getCalculatedTimeOfDelivery(OrderEntity order, AssignmentEntity assignment, 
													DispoServiceLocalInterface dispoService,
													ConfigServiceLocalInterface configService,
													TruckAdminServiceLocalInterface truckAdminService) {
		
		boolean isRail = false;
		boolean isTruck = false;
		
		Date plannedStartKnown = null;
		Date plannedStartUnload = null; 
		Date plannedStartLoad = null;
		Date lastLoadingTs = null;
		Date realstartKnown = null;
		boolean allLoaded = true;
		Date plannedStartTo = null;
		
		DriverEntity driver = null;
		if ( assignment.getWagon() != null ){
			isRail = true;
		}
		if ( assignment.getTruck() != null ){
			driver = truckAdminService.fetchDriver(assignment.getTruck().getTruckId());
			isTruck = true;
		}
		//sort according to sequence number
		List assignmentPartList = new ArrayList(assignment.getAssignmentParts());
		Collections.sort(assignmentPartList, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((AssignmentPartEntity)o1).getAssignmentPartSeqNo().compareTo(((AssignmentPartEntity)o2).getAssignmentPartSeqNo());
			}
		});

		Iterator it = assignmentPartList.iterator();
		if ( isTruck ) {			
			while (it.hasNext()) {
				AssignmentPartEntity part = (AssignmentPartEntity) it.next();
				if ( plannedStartLoad == null && part.getAssignmentPartType().getAssignmentPartTypeCd().equals(Constant.ASSIGNMENT_PART_TYPE_LOAD )){
					//if no real start is available this will be used - the very first one
					plannedStartLoad = part.getPlannedStart();
				}
				if ( part.getAssignmentPartType().getAssignmentPartTypeCd().equals(Constant.ASSIGNMENT_PART_TYPE_LOAD )){
					//if all is loaded, but the status is not yet shipping, the to part does not have yet the real start, therefore use the last loading ts instead
					if (allLoaded && (lastLoadingTs == null || (lastLoadingTs != null && part.getLoad() != null && part.getLoad().getLoadingTs() != null && part.getLoad().getLoadingTs().getTime() > lastLoadingTs.getTime())) )  {
						lastLoadingTs = part.getLoad().getLoadingTs();
					}
					if ( part.getLoad() == null || part.getLoad().getLoadingTs() == null ) {
						allLoaded = false;
					}
				}
				//the first to part planned start 
				if ( plannedStartTo == null && part.getAssignmentPartType().getAssignmentPartTypeCd().equals(Constant.ASSIGNMENT_PART_TYPE_TO )){
					plannedStartTo = part.getPlannedStart();
				}
				//if any of the parts have realstart - that has priority, that will be used!
				if ( part.getRealStart() != null ) {
					realstartKnown = part.getRealStart();				
					plannedStartKnown = part.getPlannedStart();
				}

				if ( part.getAssignmentPartType().getAssignmentPartTypeCd().equals(Constant.ASSIGNMENT_PART_TYPE_UNLOAD ) && part.getOrder() != null && part.getOrder().getOrderId().equals(order.getOrderId())){
					//if it is already known, no need to iterate further - that is definite, not even needs calculation
					if ( part.getRealStart() != null ) {
						//if the unload's real start is known use that
						return part.getRealStart();					
					}
					plannedStartUnload = part.getPlannedStart();
				}
			}

			if ( allLoaded && lastLoadingTs != null ) {
				plannedStartKnown = plannedStartTo;
				realstartKnown = lastLoadingTs;
			}
			Long assignTrStatusCd = assignment.getAssignTrStatus().getAssignTrStatusCd();
			Long driverId = driver != null ? driver.getDriverId() : null;
			return SchedulingUtils.getCalculatedTimeOfDelivery(driverId, plannedStartKnown, realstartKnown, plannedStartUnload,
					plannedStartLoad, assignTrStatusCd, dispoService, configService);
		}
		else if ( isRail ) {
			while (it.hasNext()) {
				AssignmentPartEntity part = (AssignmentPartEntity) it.next();
				if ( part.getAssignmentPartType().getAssignmentPartTypeCd().equals(Constant.ASSIGNMENT_PART_TYPE_UNLOAD ) && part.getOrder() != null && part.getOrder().getOrderId().equals(order.getOrderId())){
					if ( part.getRealStart() != null ) {
						return part.getRealStart();
					}
					else {
						return part.getPlannedStart();
					}
				}
			}
		}
		else {
			LOG.error("Planned delivery time: could not determine assignment type, [assignment: " + assignment.getAssignmentId() + "]");
		}
		return null;
	}

	/**
	 * Supposed to return the IS time of the start of the first balken shown on the truck dispo screen.
	 *  
	 * See more description of getCalculatedTimeOfDelivery(DriverEntity driver, OrderEntity order...
	 * @param plannedStartKnown
	 * @param realstartKnown
	 * @param plannedStartUnload
	 * @param plannedStartLoad
	 * @param assignTrStatusCd
	 * @param disposerv
	 * @param configService
	 * @param driver
	 * @return
	 */
	public static Date getCalculatedTimeOfDelivery(Long driverId, Date plannedStartKnown,
			Date realstartKnown, Date plannedStartUnload, Date plannedStartLoad,
			Long assignTrStatusCd, DispoServiceLocalInterface dispoService, ConfigServiceLocalInterface configService) {
		
		Date from = realstartKnown;
		
		Date to = null;
		long duration = 0;
		//if the real start of the drive to is not yet known that means that loading did not happen yet, use the plan for calculation
		if ( from == null ) {
			//no real start is known
			if ( assignTrStatusCd.equals(Constant.ASSIGN_STATUS_TR_TRANSMITTED) && plannedStartLoad.before(new Date())) {
				//if it is already transmitted to the truck act as if loading would start right now
				duration = plannedStartUnload.getTime() - plannedStartLoad.getTime();
				from = new Date();
				to = new Date(from.getTime() + duration);
			}
			else {
				duration = plannedStartUnload.getTime() - plannedStartLoad.getTime(); 
				from = plannedStartLoad;
				to = plannedStartUnload;				
			}
		}
		else {
			duration = plannedStartUnload.getTime() - plannedStartKnown.getTime();			
			to = new Date(from.getTime() + duration);
		}
			
		if ( driverId != null ) {			
			List breaks = DriverBreaksUtils.getBreaksIncludingLunchBreak(driverId, from, dispoService, configService);
			
			long breakLength = getBreakLengthBetween(from, to, breaks);
			return new Date(from.getTime() + duration + breakLength);
		}
		else {
			return new Date(from.getTime() + duration);
		}
	}

	/**
	 * Supposed to return the IS time of the start of the first balken shown on the truck dispo screen.
	 * 
	 * Returns the planned start of the load. These can have an effect on the calculated value:
	 * 1) the driver might have break according to plan on that day - the calculated planned start is not what is on the load assignment part, 
	 * but must be corrected with the break
	 * 2) if the assignment is not going according to plan, the break must be applied also when the assignment is late,
	 * f. ex. according to plan the driver wills start loading at 12:30 though the load part planned time is 12, because
	 * he has lunch break from 12-12:30. Then if he does not start at 12:30 the assignment is late,
	 * and it is already trasmitted to the driver, suppose its already at 15:00,
	 * and he is supposed to start right in that moment, but if he has break from 15:00-15:15, then for sure he will not start until he has break,
	 * and we can then just suppose that he will start loading at 15:15.
	 * @param driverId
	 * @param assignTrStatusCd
	 * @param plannedStartLoad
	 * @param disposerv
	 * @param configService
	 * @return
	 */
	public static Date getCalculatedTimeOfLoad(Long driverId, Long assignTrStatusCd,
			Date plannedStartLoad, DispoServiceLocalInterface dispoService, ConfigServiceLocalInterface configService) {
		Date calculatedStartLoad;
		//this is the default
		if (!assignTrStatusCd.equals(Constant.ASSIGN_STATUS_TR_TRANSMITTED)) {				
			calculatedStartLoad = plannedStartLoad;
		}
		else{
			calculatedStartLoad = new Date();
		}
		if (driverId != null) {
			Long length = (Long) driverId;
			calculatedStartLoad = new Date(calculatedStartLoad.getTime() + length);
		}
		
		if ( driverId != null ) {			
			List breaks = DriverBreaksUtils.getBreaksIncludingLunchBreak(driverId, calculatedStartLoad, dispoService, configService);
			
			long breakLength = getBreakLengthBetween(calculatedStartLoad, null, breaks);
			return new Date(calculatedStartLoad.getTime() + breakLength);
		}
		else {
			return new Date(calculatedStartLoad.getTime());
		}
	}
	
	public static class CalculatedDeliveryTimes{
		private Calendar fromDate;
		private Calendar toDate;
		private Long netTimeInMinutes;
		
		public Calendar getFromDate() {
			return fromDate;
		}
		
		public void setFromDate(Calendar fromDate) {
			this.fromDate = fromDate;
		}
		
		public Calendar getToDate() {
			return toDate;
		}
		
		public void setToDate(Calendar toDate) {
			this.toDate = toDate;
		}
		
		public Long getNetTimeInMinutes() {
			return netTimeInMinutes;
		}
		
		public void setNetTimeInMinutes(Long netTime) {
			this.netTimeInMinutes = netTime;
		}
		
	}
	
	/**
	 * The delivery net time is in fact the time what crh had to deliver the product. This does not
	 * include weekends, and non working hours (non working hours are those, which are not in the time interval 
	 * defined by open1 and open2 of the shipto.
	 * Idea of the calculation: 
	 * 1.adjust the delivery from to the first valid minute when it is possible to start delivery
	 * (this means in case it is on the weekend, set it forward to monday, and in case it is in such a minute of the day which
	 * is not between a valid opening time (not  in open1 and not even in open2) then adjust the delivery minute to the next 
	 * minute when the shipto is open. (Of course this adjust time must be considered in the final result.)
	 * 2. the same for delivery to, but adjust it to the last valid minute when it is possible to end
	 * the delivery etc.
	 */
	public static CalculatedDeliveryTimes calculateDeliveryFromToAndNetTime(ShiptoEntity shiptoEntity,
			final Date deliveryFromDate, final Date deliveryToDate, final TimeZone timeZone, final SalesAreaWebOrderOnWeekendSettings webOrderOnWeekend) {
		
		// [[
		CalculatedDeliveryTimes result = null;
		Object[][] params = {{"shiptoEntity", shiptoEntity},
							{"deliveryFromDate", deliveryFromDate},
							{"deliveryToDate", deliveryToDate},
							{"timeZone", timeZone}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SchedulingUtils.class, "calculateDeliveryFromToAndNetTime", params);
		}
		// ]]
		
		try {
			SchedulingUtils.CalculatedDeliveryTimes calculatedDeliveryTimes = new CalculatedDeliveryTimes();
			
			Calendar deliveryFrom = null;
			Calendar deliveryTo = null;
			Calendar timePartOfDeliveryFrom = null;
			Calendar timePartOfDeliveryTo = null;
			Calendar nearestWorkingDayOfDeliveryTo = null;
			Calendar nearestWorkingDayOfDeliveryFrom = null;
			
			if (deliveryFromDate != null ) {				
				deliveryFrom = timeZone.asCalendar(deliveryFromDate);
				
				timePartOfDeliveryFrom = DateTimeUtils.getTimePart(deliveryFrom);
				
				if (logDebugEnabled) {
					LOG.debug("DeliveryFrom: " + deliveryFrom.getTime());
				}

				nearestWorkingDayOfDeliveryFrom = adjustDeliveryFromToWorkingDay(deliveryFrom,webOrderOnWeekend);				
			}
			
			if ( deliveryToDate != null ) {				
				deliveryTo = timeZone.asCalendar(deliveryToDate);
				
				timePartOfDeliveryTo = DateTimeUtils.getTimePart(deliveryTo);
				
				if (logDebugEnabled) {
					LOG.debug("DeliveryTo: " + deliveryTo.getTime());
				}
				
				nearestWorkingDayOfDeliveryTo = adjustDeliveryToToWorkingDay(deliveryTo,webOrderOnWeekend);				
			}
			
			if (deliveryFrom != null && deliveryTo != null && shiptoEntity.getOpen1From() != null &&
					shiptoEntity.getOpen2From() != null &&
					shiptoEntity.getOpen1To() != null &&
					shiptoEntity.getOpen2To() != null ) {

				Calendar base = timeZone.asCalendar();

				//Calendar containing info only about the HOUR + MINUTE 
				final Calendar open1From = DateTimeUtils.getTimePart(DateTimeUtils.getHoursMinutesCal(base, shiptoEntity.getOpen1From()));
				final Calendar open1To = DateTimeUtils.getTimePart(DateTimeUtils.getHoursMinutesCal(base, shiptoEntity.getOpen1To()));
				final Calendar open2From = DateTimeUtils.getTimePart(DateTimeUtils.getHoursMinutesCal(base, shiptoEntity.getOpen2From()));
				final Calendar open2To = DateTimeUtils.getTimePart(DateTimeUtils.getHoursMinutesCal(base, shiptoEntity.getOpen2To()));

				final boolean isDeliveryFromWeekend = isDateNonWorkingday(webOrderOnWeekend, deliveryFrom);
				
				
				//in case it is on weekend, it is not valid, adjust the time window
				if (isDeliveryFromWeekend) {					
					timePartOfDeliveryFrom = open1From;
					LOG.debug("FROM is a weekend day, adjusting to next open1From  " + timeZone.fmtAsHourMinute(timePartOfDeliveryFrom.getTime()));
				}
				
				final boolean isDeliveryToNonWorkingDay = isDateNonWorkingday(webOrderOnWeekend, deliveryTo);
				
				if (isDeliveryToNonWorkingDay) {
					timePartOfDeliveryTo = open2To;
					LOG.debug("To is a weekend day, adjusting to last open2From  " + timeZone.fmtAsHourMinute(timePartOfDeliveryTo.getTime()));
				}
				
				//If from is between an opening interval: Define the amount of minutes which has to be removed later - from will be anyway an open intervals start
				Long deliveryFromDiffToOpeningInMin = calculateDifferentToOpeningTime(timePartOfDeliveryFrom, open1From, open1To, open2From, open2To);

				//If to is between an opening interval: Define the amount of minutes which has to be removed later - to will be anyway an open intervals end
				Long deliveryToDifferentClosingInMin = calculateDifferentToClosingTime(timePartOfDeliveryTo, open1From, open1To, open2From, open2To);				
								
				timePartOfDeliveryFrom = adjustTimePartOfDeliveryFrom(timePartOfDeliveryFrom, deliveryFrom, open1From, open1To, open2From, open2To);
				timePartOfDeliveryTo = adjustTimePartOfDeliveryTo(timePartOfDeliveryTo, timePartOfDeliveryFrom, deliveryTo, open1From, open1To, open2From, open2To);

				Long totalDifferentInMinutes = deliveryFromDiffToOpeningInMin + deliveryToDifferentClosingInMin;
				LOG.debug("When adjusting FROM and TO to start/end of opening time " + totalDifferentInMinutes + " minutes has been added");
										
				if (logDebugEnabled) {
					String adjustedDeliveryFromHour = timeZone.fmtAsHourMinute(timePartOfDeliveryFrom.getTime());
					String asjustedDeilveryToHour = timeZone.fmtAsHourMinute(timePartOfDeliveryTo.getTime());
					LOG.debug("ADJUSTING HOURS IS DONE. adjustedDeliveryFromHour is " + adjustedDeliveryFromHour + " , asjustedDeilveryToHour " + asjustedDeilveryToHour);					
				}
							
								
				Long calculatedNetTimeInMinutes = calculateNetTimeInMinutes(
						timePartOfDeliveryFrom, timePartOfDeliveryTo,
						open1From, open1To, open2From, open2To, shiptoEntity,
						nearestWorkingDayOfDeliveryFrom,
						nearestWorkingDayOfDeliveryTo,
						webOrderOnWeekend);
				
				
				//subtract the time what was added when the times were adjusted to valid opening times
				calculatedNetTimeInMinutes -= totalDifferentInMinutes;
										
				timePartOfDeliveryFrom.add(Calendar.MINUTE, -deliveryFromDiffToOpeningInMin.intValue());
				timePartOfDeliveryTo.add(Calendar.MINUTE, -deliveryToDifferentClosingInMin.intValue());
				
				Calendar calculatedDeliveryFrom = DateTimeUtils.setTimePart(
						nearestWorkingDayOfDeliveryFrom,
						timePartOfDeliveryFrom.get(Calendar.HOUR_OF_DAY),
						timePartOfDeliveryFrom.get(Calendar.MINUTE),
						timePartOfDeliveryFrom.get(Calendar.SECOND),
						timePartOfDeliveryFrom.get(Calendar.MILLISECOND));
				
				Calendar calculatedDeliveryTo = DateTimeUtils.setTimePart(
						nearestWorkingDayOfDeliveryTo,
						timePartOfDeliveryTo.get(Calendar.HOUR_OF_DAY),
						timePartOfDeliveryTo.get(Calendar.MINUTE),
						timePartOfDeliveryTo.get(Calendar.SECOND),
						timePartOfDeliveryTo.get(Calendar.MILLISECOND));
				
				calculatedDeliveryTimes.setFromDate(calculatedDeliveryFrom);
				calculatedDeliveryTimes.setToDate(calculatedDeliveryTo);
				calculatedDeliveryTimes.setNetTimeInMinutes(calculatedNetTimeInMinutes);				
			}
			else {
				
				if (timePartOfDeliveryFrom != null && nearestWorkingDayOfDeliveryFrom != null) {
					Calendar calculatedDeliveryFrom = DateTimeUtils.setTimePart(
									nearestWorkingDayOfDeliveryFrom,
									timePartOfDeliveryFrom.get(Calendar.HOUR_OF_DAY),
									timePartOfDeliveryFrom.get(Calendar.MINUTE),
									timePartOfDeliveryFrom.get(Calendar.SECOND),
									timePartOfDeliveryFrom.get(Calendar.MILLISECOND));
					
					calculatedDeliveryTimes.setFromDate(calculatedDeliveryFrom);
				}

				if (timePartOfDeliveryTo != null && nearestWorkingDayOfDeliveryTo != null) {
					Calendar calculatedDeliveryTo = DateTimeUtils.setTimePart(
							nearestWorkingDayOfDeliveryTo,
							timePartOfDeliveryTo.get(Calendar.HOUR_OF_DAY),
							timePartOfDeliveryTo.get(Calendar.MINUTE),
							timePartOfDeliveryTo.get(Calendar.SECOND),
							timePartOfDeliveryTo.get(Calendar.MILLISECOND));
					
					calculatedDeliveryTimes.setToDate(calculatedDeliveryTo);
				}
				
			}
			return result = calculatedDeliveryTimes;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * Calculates the difference between the timePartOfDeliveryTo and the closing time
	 * 
	 * @param timePartOfDeliveryFrom
	 * @param open1From
	 * @param open1To
	 * @param open2From
	 * @param open2To
	 * @return
	 */
	private static Long calculateDifferentToClosingTime(
			final Calendar timePartOfDeliveryTo,
			final Calendar open1From, final Calendar open1To,
			final Calendar open2From, final Calendar open2To) {

		// [[
		Long result = null;
		Object[][] params = { { "timePartOfDeliveryTo", timePartOfDeliveryTo },
				{ "open1From", open1From }, { "open1To", open1To },
				{ "open2From", open2From }, { "open2To", open2To } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SchedulingUtils.class, "calculateDifferentToClosingTime", params);
		}
		// ]]
		try {
			Long deliveryToDiffToClosingInMin = 0l;

			if (timePartOfDeliveryTo.after(open1From) && timePartOfDeliveryTo.before(open1To)) {
				deliveryToDiffToClosingInMin = DateTimeUtils.getMinAfterMidnight(open1To.getTime()) - DateTimeUtils.getMinAfterMidnight(timePartOfDeliveryTo.getTime());
				if (logDebugEnabled) {
					LOG.debug("When adjusting TO to end of opening time " + deliveryToDiffToClosingInMin + " minutes has been added (adjusted TO to open1To)");
				}
			} else if (timePartOfDeliveryTo.after(open2From) && timePartOfDeliveryTo.before(open2To)) {
				deliveryToDiffToClosingInMin = DateTimeUtils.getMinAfterMidnight(open2To.getTime()) - DateTimeUtils.getMinAfterMidnight(timePartOfDeliveryTo.getTime());
				if (logDebugEnabled) {
					LOG.debug("When adjusting TO to end of opening time " + deliveryToDiffToClosingInMin + " minutes has been added (adjusted TO to open2To)");
				}
			}

			return result = deliveryToDiffToClosingInMin;
		} finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	/**
	 * Calculates the different between the timePartOfDeliveryFrom and the opening time
	 * 
	 * @param timePartOfDeliveryFrom
	 * @param open1From
	 * @param open1To
	 * @param open2From
	 * @param open2To
	 * @return
	 */
	private static Long calculateDifferentToOpeningTime(
			final Calendar timePartOfDeliveryFrom, final Calendar open1From,
			final Calendar open1To, final Calendar open2From,
			final Calendar open2To) {

		// [[
		Long result = null;
		Object[][] params = {
				{ "originalTimePartOfDeliveryTo", timePartOfDeliveryFrom },
				{ "open1From", open1From }, { "open1To", open1To },
				{ "open2From", open2From }, { "open2To", open2To } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SchedulingUtils.class, "calculateDifferentToOpeningTime", params);
		}
		// ]]		
		try {
			Long deliveryFromDiffToOpeningInMin = 0l;
			
			if (timePartOfDeliveryFrom.after(open1From) && timePartOfDeliveryFrom.before(open1To)) {
				deliveryFromDiffToOpeningInMin = DateTimeUtils.getMinAfterMidnight(timePartOfDeliveryFrom.getTime()) - DateTimeUtils.getMinAfterMidnight(open1From.getTime());

				if (logDebugEnabled) {
					LOG.debug("When adjusting FROM to start of opening time " + deliveryFromDiffToOpeningInMin + " minutes has been added (adjusted FROM to open1From)");
				}

			} else if (timePartOfDeliveryFrom.after(open2From) && timePartOfDeliveryFrom.before(open2To)) {
				deliveryFromDiffToOpeningInMin = DateTimeUtils.getMinAfterMidnight(timePartOfDeliveryFrom.getTime()) - DateTimeUtils.getMinAfterMidnight(open2From.getTime());

				if (logDebugEnabled) {
					LOG.debug("When adjusting FROM to start of opening time " + deliveryFromDiffToOpeningInMin + " minutes has been added (adjusted FROM to open2From)");
				}
			}
			return result = deliveryFromDiffToOpeningInMin;
		} finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	/**
	 * Adjust the timePartOfDeliveryTo to opening interval if needed
	 * 
	 * @param timePartOfDeliveryTo
	 * @param deliveryTo
	 * @param open1From
	 * @param open1To
	 * @param open2From
	 * @param open2To
	 * @return
	 */
	private static Calendar adjustTimePartOfDeliveryTo(
			final Calendar originalTimePartOfDeliveryTo, final Calendar timePartOfDeliveryFrom,
			final Calendar deliveryTo, final Calendar open1From, 
			final Calendar open1To, final Calendar open2From,
			final Calendar open2To) {

		// [[
		Calendar result = null;
		Object[][] params = {
				{ "originalTimePartOfDeliveryTo", originalTimePartOfDeliveryTo },
				{ "timePartOfDeliveryFrom", timePartOfDeliveryFrom },
				{ "deliveryTo", deliveryTo }, { "open1From", open1From },
				{ "open1To", open1To }, { "open2From", open2From },
				{ "open2To", open2To } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SchedulingUtils.class, "adjustTimePartOfDeliveryTo", params);
		}
		// ]]
		try {
			Calendar timePartOfDeliveryTo = originalTimePartOfDeliveryTo;

			if (timePartOfDeliveryTo.after(open1From) && timePartOfDeliveryTo.before(open1To)) {
				timePartOfDeliveryTo = open1To;
			}
			else if (timePartOfDeliveryTo.after(open2From) && timePartOfDeliveryTo.before(open2To) ) {
				timePartOfDeliveryTo = open2To;
			}			
			
			//If to is not between an opening interval the opening hours to will be used
			if (timePartOfDeliveryTo.after(open1To) && timePartOfDeliveryTo.before(open2From)) {
				if (logDebugEnabled) {						
					LOG.debug("TO is between GAP2 (lunch break gap) TO is adjusted to open1To");
				}
				timePartOfDeliveryTo = open1To;
			}
			else if (timePartOfDeliveryTo.after(open2To) || timePartOfDeliveryFrom.before(open1From)) {
				timePartOfDeliveryTo = open2To;
				if (logDebugEnabled) {						
					LOG.debug("TO is between GAP1 (late evening, night, early morning gap) TO is adjusted to open2To");
				}
			}
		
			return result = timePartOfDeliveryTo;
		} finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}		
	}

	/**
	 * Adjust the timePartOfDeliveryFromto opening interval if needed
	 * 
	 * @param originalTimePartOfDeliveryFrom
	 * @param deliveryFrom
	 * @param open1From
	 * @param open1To
	 * @param open2From
	 * @param open2To
	 * @return
	 */
	private static Calendar adjustTimePartOfDeliveryFrom(
			final Calendar originalTimePartOfDeliveryFrom, final Calendar deliveryFrom,
			final Calendar open1From, final Calendar open1To, 
			final Calendar open2From, final Calendar open2To) {
		
		// [[
		Calendar result = null;
		Object[][] params = { { "originalTimePartOfDeliveryFrom", originalTimePartOfDeliveryFrom },
				{ "deliveryFrom", deliveryFrom },
				{ "open1From", open1From } ,
				{ "open1To", open1To } ,
				{ "open2From", open2From } ,
				{ "open2To", open2To } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SchedulingUtils.class, "adjustTimePartOfDeliveryFrom", params);
		}
		// ]]
		try{
			Calendar timePartOfDeliveryFrom = originalTimePartOfDeliveryFrom;
			
			if (timePartOfDeliveryFrom.after(open1From) && timePartOfDeliveryFrom.before(open1To)) {
				timePartOfDeliveryFrom = open1From;
			}
			else if (timePartOfDeliveryFrom.after(open2From) && timePartOfDeliveryFrom.before(open2To)) {
				timePartOfDeliveryFrom = open2From;
			}
			
			//If from is not between an opening interval the opening hours from will be used 
			if (timePartOfDeliveryFrom.after(open1To) && timePartOfDeliveryFrom.before(open2From)) {
				timePartOfDeliveryFrom = open2From;
				if (logDebugEnabled) {						
					LOG.debug("FROM is between GAP2 (lunch break gap) FROM is adjusted to open2From");
				}
			}
			else if (timePartOfDeliveryFrom.after(open2To) || timePartOfDeliveryFrom.before(open1From)) {
				timePartOfDeliveryFrom = open1From;
				if (logDebugEnabled) {						
					LOG.debug("FROM is between GAP1 (late evening, night, early morning gap) FROM is adjusted to open1From");
				}
			}
		
			return result = timePartOfDeliveryFrom;
		}
		finally{
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}		
	}

	/**
	 * It calculates net time in minutes
	 * 
	 * @param timePartOfDeliveryFrom
	 * @param timePartOfDeliveryTo
	 * @param open1From
	 * @param open1To
	 * @param open2From
	 * @param open2To
	 * @param shiptoEntity
	 * @param nearestWorkingDayOfDeliveryFrom
	 * @param nearestWorkingDayOfDeliveryTo
	 * @return
	 */
	private static Long calculateNetTimeInMinutes(
			final Calendar timePartOfDeliveryFrom,
			final Calendar timePartOfDeliveryTo, 
			final Calendar open1From,
			final Calendar open1To, 
			final Calendar open2From, 
			final Calendar open2To, 
			final ShiptoEntity shiptoEntity,
			final Calendar nearestWorkingDayOfDeliveryFrom,
			final Calendar nearestWorkingDayOfDeliveryTo,
			final SalesAreaWebOrderOnWeekendSettings webOrderOnWeekend) {

		// [[
		Long result = null;
		Object[][] params = {
				{ "timePartOfDeliveryFrom", timePartOfDeliveryFrom },
				{ "timePartOfDeliveryTo", timePartOfDeliveryTo },
				{ "open1From", open1From },
				{ "open1To", open1To },
				{ "open2From", open2From },
				{ "open2To", open2To },
				{ "shiptoEntity", shiptoEntity },
				{ "nearestWorkingDayOfDeliveryFrom",
						nearestWorkingDayOfDeliveryFrom },
				{ "nearestWorkingDayOfDeliveryTo",
						nearestWorkingDayOfDeliveryTo } };

		boolean logDebugEnabled = LOG.isDebugEnabled();

		if (logDebugEnabled) {
			LOG.entering(SchedulingUtils.class, "calculateNetTimeInMinutes", params);
		}
		// ]]
		try {
			Long calculatedNetTime = 0l;
			
			Long wholeDayMinutesToAdd = 0l;

			// Mind the gaps for days in between in case there are whole days
			//Length of GAP1 ((late evening, night, early morning gap)
			Long gap1Length = DateTimeUtils.getMinAfterMidnight(open1From.getTime()) + ((DateTimeUtils.getMillisBeforeMidnight(open2To) / ONE_MINUTE_IN_MILLIS) + 1);
			//Length of GAP2 (lunch break gap)
			Long gap2Length = shiptoEntity.getOpen2From() - shiptoEntity.getOpen1To();

			if (logDebugEnabled) {
				String open1FromLog = DateTimeUtils.formatHoursMinutes(open1From.getTime());
				String open1ToLog = DateTimeUtils.formatHoursMinutes(open1To.getTime());
				String open2FromLog = DateTimeUtils.formatHoursMinutes(open2From.getTime());
				String open2ToLog = DateTimeUtils.formatHoursMinutes(open2To.getTime());
				
				LOG.debug( "open1From: " + open1FromLog + ", open1To: " + open1ToLog + ", open2From: " + open2FromLog + ", open2To: " + open2ToLog); 
				LOG.debug( "gap1length(late evening, night, early morning gap): " + gap1Length + " min, gap2length(lunch break gap): " + gap2Length + " min");				
			}

			if (timePartOfDeliveryFrom.compareTo(open1From) == 0) {
				calculatedNetTime = DateTimeUtils.getMinAfterMidnight(timePartOfDeliveryTo.getTime()) - DateTimeUtils.getMinAfterMidnight(timePartOfDeliveryFrom.getTime());

				if (timePartOfDeliveryTo.compareTo(open2To) == 0) {
					calculatedNetTime -= gap2Length;
				}			
			}

			else if (timePartOfDeliveryFrom.compareTo(open2From) == 0 ) {
				if (timePartOfDeliveryTo.compareTo(open2To) == 0){
					calculatedNetTime = DateTimeUtils.getMinAfterMidnight(timePartOfDeliveryTo.getTime()) - DateTimeUtils.getMinAfterMidnight(timePartOfDeliveryFrom.getTime());
				}
				else{
					calculatedNetTime=0l;
				}
			}

			final Long dayDiff = DateTimeUtils.getDayDiff(
					nearestWorkingDayOfDeliveryFrom,
					nearestWorkingDayOfDeliveryTo);

			// if there are more days between the from and the to, there will be
			// first of all the full net window per day * the days between, BUT
			// weekends do not count
			if (dayDiff >= 1) {
				// this is the length of the delivery window for a whole day
				Long minutesPerDay = MINUTES_IN_A_DAY - gap1Length - gap2Length;
				int numberOfWeekends = DateTimeUtils.getWeekendsBetween(nearestWorkingDayOfDeliveryFrom, nearestWorkingDayOfDeliveryTo);

				if (logDebugEnabled) {
					LOG.debug("Number of minutes in a day " + MINUTES_IN_A_DAY + " minutes. Delivery net window for a whole day " + minutesPerDay + " minutes");
					LOG.debug("Day diff between from and to is " + dayDiff +". Number of weekends between from and to " + numberOfWeekends + " thus " + numberOfWeekends + "*2 days should not be considered as working days");
				}
				
				int numberOfDaysOnWeekend = webOrderOnWeekend.getNumberOfDaysOnWeekend();
				wholeDayMinutesToAdd = (dayDiff - (numberOfDaysOnWeekend * numberOfWeekends)) * minutesPerDay;
			}

			calculatedNetTime += wholeDayMinutesToAdd;

			if (logDebugEnabled) {
				LOG.debug("Net window: " + calculatedNetTime);
			}

			return result = calculatedNetTime;
		} finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

		
	/**
	 * Adjusts the delivery_from day to the next Monday, if it is a non-working day.
	 * @param delfromclone
	 */
	private static Calendar adjustDeliveryFromToWorkingDay(Calendar delfrom, SalesAreaWebOrderOnWeekendSettings webOrderOnWeekend) {
		// [[
		Calendar result = null;
		Object[][] params = {{"delfrom", delfrom}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SchedulingUtils.class.getName(), "adjustDeliveryFromToWorkingDay", params);
		}
		// ]]
		try {
			Calendar delfromclone = (Calendar) delfrom.clone();					
			Boolean adjustmentDone = false;			
			int dayOfWeek = delfromclone.get(Calendar.DAY_OF_WEEK);

			switch (dayOfWeek) {

			case Calendar.SATURDAY:
				if (SalesAreaWebOrderOnWeekendSettings.DISABLED.equals(webOrderOnWeekend)) {
					delfromclone.add(Calendar.DAY_OF_MONTH, 2);
					adjustmentDone = true;
				}
				break;

			case Calendar.SUNDAY:
				if (!SalesAreaWebOrderOnWeekendSettings.SATURDAY_AND_SUNDAY.equals(webOrderOnWeekend)) {
					delfromclone.add(Calendar.DAY_OF_MONTH, 1);
					adjustmentDone = true;					
				}
				break;

			default:
				//Other days do not need adjustment
				break;
			}
			
			
			if (logDebugEnabled && adjustmentDone) {
				LOG.debug("FROM is a weekend day, adjusting to next MONDAY "
						+ DateTimeUtils.formatDayMonthYear(delfromclone.getTime(), null));
			}

			if (logDebugEnabled) {
				LOG.debug("ADJUSTING TIMES IS DONE. Adjusted DELIVERY FROM is " +  DateTimeUtils.formatDayMonthYear(delfromclone.getTime(), null));
			}
			return result = delfromclone;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	/**
	 * @param delto
	 * @return
	 */
	private static boolean isDateNonWorkingday(SalesAreaWebOrderOnWeekendSettings salesAreaWebOrderOnWeekendSettings, Calendar delto) {
		
		return (SalesAreaWebOrderOnWeekendSettings.DISABLED.equals(salesAreaWebOrderOnWeekendSettings) && delto.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) 
				|| (!SalesAreaWebOrderOnWeekendSettings.SATURDAY_AND_SUNDAY.equals(salesAreaWebOrderOnWeekendSettings) && delto.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);
	}

	/**
	 * Adjusts the delivery_to to the previous Friday, if it is a non-working day.
	 * @param delto
	 * @return
	 */
	private static Calendar adjustDeliveryToToWorkingDay(Calendar delto,SalesAreaWebOrderOnWeekendSettings webOrderOnWeekend) {
		// [[
		Calendar result = null;
		Object[][] params = {{"delto", delto}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SchedulingUtils.class.getName(), "adjustDeliveryToToWeekday", params);
		}
		// ]]
		try {
			if (logDebugEnabled) {					
				LOG.debug("DelTo: " + delto.getTime()) ;					
			}

			Calendar deltoclone = (Calendar) delto.clone();
			Boolean adjustmentDone = false;
			int dayOfWeek = deltoclone.get(Calendar.DAY_OF_WEEK);

			switch (dayOfWeek) {

			case Calendar.SATURDAY:
				if (SalesAreaWebOrderOnWeekendSettings.DISABLED.equals(webOrderOnWeekend)) {
					deltoclone.add(Calendar.DAY_OF_MONTH, -1);
					adjustmentDone = true;
				}
				break;

			case Calendar.SUNDAY:
				deltoclone.add(Calendar.DAY_OF_MONTH, (-1 * webOrderOnWeekend.getNumberOfDaysOnWeekend()));
				adjustmentDone = true;					
				break;
				
			default:
				//Other days do not need adjustment
				break;
			}
			
			if (logDebugEnabled && adjustmentDone) {
				LOG.debug("TO is a weekend day, adjusting to last FRIDAY "
						+ DateTimeUtils.formatDayMonthYear(deltoclone.getTime(), null));
			}

			if (logDebugEnabled) {
				LOG.debug("ADJUSTING TIMES IS DONE. Adjusted DELIVERY TO is " + DateTimeUtils.formatDayMonthYear(deltoclone.getTime(), null));
			}
			return result = deltoclone;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Returns the loading time for the order depending on the loadingpoint setting of the order items.
	 * @param orderItems
	 * @param configBean
	 * @return
	 */
	public static Long calculateLoadingTimeForOrder(OrderEntity order, ConfigBean configBean) {
		// [[
		Long result = null;
		Object[][] params = {{"order", order},
							{"configBean", configBean}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(FrozenTablesServiceBean.class.getName(), "getLoadingTimeForOrder", params);
		}
		// ]]
		try {
			Collection<OrderItemEntity> orderItems = LogonPackageHandlingUtil.getMaterialItems(order);
			Long sumLoadingTime = 0L;

			for (OrderItemEntity orderItem : orderItems) {
				sumLoadingTime += orderItem.getPlannedLoadingTimeMin();
			}
			return result = sumLoadingTime;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Returns true if the whole time interval is on one weekend, false otherwise.
	 */
	public static boolean isWholeIntervalOnNonWorkingDays(SalesAreaWebOrderOnWeekendSettings salesAreaWebOrderOnWeekendSettings, Date deliveryFromDat, Date deliveryToDat) {
		// [[
		boolean result = false;
		Object[][] params = {{"deliveryFromDat", deliveryFromDat},
							{"deliveryToDat", deliveryToDat}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SchedulingUtils.class, "isWholeDurationOnWeekendDays", params);
		}
		// ]]
		try {
			Calendar deliveryStart = DateTimeUtils.getDateAsCalendar(deliveryFromDat);
			Calendar deliveryEnd = DateTimeUtils.getDateAsCalendar(deliveryToDat);
			
			Long dayDiff = DateTimeUtils.getDayDiff(deliveryStart, deliveryEnd);
			boolean isDeliveryStartOnNonWorkingDay = isDateNonWorkingday(salesAreaWebOrderOnWeekendSettings, deliveryStart);
			boolean isDeliveryEndOnNonWorkingDay = isDateNonWorkingday(salesAreaWebOrderOnWeekendSettings, deliveryEnd);
			
			int possibleDayDiff = salesAreaWebOrderOnWeekendSettings == null ? 2 : salesAreaWebOrderOnWeekendSettings.getNumberOfDaysOnWeekend();
			return result = isDeliveryStartOnNonWorkingDay && isDeliveryEndOnNonWorkingDay && dayDiff < possibleDayDiff;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Returns the latest possible deliveryToDate for a delivery window.
	 * The functionality assumes, that the time interval is not totally in a weekend.
	 */
	@AdnTrace
	public static Date returnLatestPossibleDeliveryToTime(SalesAreaWebOrderOnWeekendSettings salesAreaWebOrderOnWeekendSettings,
			Date deliveryToDat, Long latestPossibleDeliveryToTime) {
			Calendar deliveryToDatCal = Calendar.getInstance();
			deliveryToDatCal.setTime(deliveryToDat);
			
			boolean isDeliveryToDateOnWeekend = isDateNonWorkingday(salesAreaWebOrderOnWeekendSettings, deliveryToDatCal);
			
			if (isDeliveryToDateOnWeekend) {
				int latestHour = latestPossibleDeliveryToTime.intValue() / 60;
				int latestMinute = latestPossibleDeliveryToTime.intValue() % 60;
				
				//find the latest weekday backwards
				while (isDateNonWorkingday(salesAreaWebOrderOnWeekendSettings, deliveryToDatCal)) {
					deliveryToDatCal.add(Calendar.DAY_OF_YEAR, -1);
				}
				
				deliveryToDatCal.set(Calendar.HOUR_OF_DAY, latestHour);
				deliveryToDatCal.set(Calendar.MINUTE, latestMinute);
				return deliveryToDatCal.getTime();
			}
			else {
				return deliveryToDat;
			}
	}
	
	/**
	 * Returns the next working day.
	 */
	public static Date getNextWorkingDay(SalesAreaWebOrderOnWeekendSettings salesAreaWebOrderOnWeekendSettings, Date date) {
		// [[
		Date result = null;
		Object[][] params = {{"date", date}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SchedulingUtils.class, "getNextWorkingDay", params);
		}
		// ]]
		try {
			Calendar cal = DateTimeUtils.getDateAsCalendar(date);
			
			do {
				cal.add(Calendar.DAY_OF_YEAR, 1);
			} while (isDateNonWorkingday(salesAreaWebOrderOnWeekendSettings, cal));
			
			return result = cal.getTime();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Checks if the inputDate parameter is on the next working day from referenceDate.
	 */
	public static Boolean isOnNextWorkingDay(SalesAreaWebOrderOnWeekendSettings salesAreaWebOrderOnWeekendSettings, Date referenceDate, Date inputDate){
		// [[
		Boolean result = null;
		Object[][] params = {{"referenceDate", referenceDate},
							{"inputDate", inputDate}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SchedulingUtils.class, "isOnNextWorkingDay", params);
		}
		// ]]
		try {
			Date nextWorkingDay = getNextWorkingDay(salesAreaWebOrderOnWeekendSettings, referenceDate);
			return result = DateTimeUtils.isOnSameDay(nextWorkingDay, inputDate);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * Returns the latest time when the order has to be started. Loading time is determined according to the loadingpoints set, or
	 * if unknown config values. Transport duration is determined from the order either google estimate or the value corrected with the 
	 * correction factor. In case the delivery to is not a weekday, the delivery to will be considered to be friday. 
	 * The parameter plannedDelivery overrides the delivery to from the point of view, that in case that is specified the latest time when the order
	 * has to be started will be considered to that date.
	 * @param order
	 * @param configBean
	 * @param plannedDelivery if this date is given then the calculation will NOT consider the delivery to of the order, but will consider this date
	 * @return
	 */
	public static Date calculateLatestDeliveryStart(OrderEntity order, ConfigBean configBean, ShiptoEntity shiptoEntity, final Date plannedDelivery, final TimeZone timeZone) {
		// [[
		Date result = null;
		Object[][] params = {{"order", order},
							{"configBean", configBean},
							{"shiptoEntity", shiptoEntity},
							{"plannedDelivery", plannedDelivery},
							{"timeZone", timeZone}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SchedulingUtils.class, "calculateLatestDeliveryStart", params);
		}
		// ]]
		try {
			Date deliveryTo = plannedDelivery == null ? order.getDeliveryToDat() : plannedDelivery;  
			Long allowWebOrderOnWeekend = null;
			if(order.getSarea() != null) {
				allowWebOrderOnWeekend = order.getSarea().getAllowWebOrderOnWeekend();				
			}
			SalesAreaWebOrderOnWeekendSettings webOrderOnWeekend = SalesAreaWebOrderOnWeekendSettings.valueOf(allowWebOrderOnWeekend);
			
			SchedulingUtils.CalculatedDeliveryTimes calculatedTimes = calculateDeliveryFromToAndNetTime(shiptoEntity, order.getDeliveryFromDat(),  deliveryTo, timeZone,webOrderOnWeekend);
			
			Calendar deliveryToDate = calculatedTimes.getToDate();				
			
			if (deliveryToDate != null){
					deliveryTo =  deliveryToDate.getTime();	
			}
			else{
					if ( logDebugEnabled ) {
						LOG.debug("Could not detemine the latest delivery to when the shipto is open for the order, using actual set delivery To until it can be transported. [orderNumber=" + order.getOrderNumber()  + ", orderId=" + order.getOrderId() + "]");	
					}
			}
			
								
			LOG.debug("Actual latest valid delivery to date=" + DateTimeUtils.formatDayMonthYear(deliveryTo, Locale.ENGLISH) + "time=" + DateTimeUtils.formatHoursMinutes(deliveryTo));
			
			//Calc end load to depends on the delivery to and the planned transport duration
			Calendar cal = timeZone.asCalendar(deliveryTo);

			final Integer deliveryTime = calculateDeliveryDuration(order, configBean);
			
			if ( deliveryTime == null ) {
				return result = null;
			}
			
			cal.add(Calendar.MINUTE, -deliveryTime);			
			LOG.debug("Latest start of loading=" + DateTimeUtils.formatDayMonthYear(cal.getTime(), Locale.ENGLISH) + "time=" + DateTimeUtils.formatHoursMinutes(cal.getTime()));
			Date dat = cal.getTime();
			return result = dat;
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Calculates the delivery duration (estimate) based on master data and config settings.
	 * Loading time: depends on the loading point
	 * Delivery time is supposed to be pre-set in the order otherwise null is returned
	 * @param order
	 * @param configBean
	 * @return
	 */
	@AdnTrace
	public static Integer calculateDeliveryDuration(OrderEntity order,
			ConfigBean configBean) {

		Long transportDurationMin = order.getTransportDurationMin();
		if (transportDurationMin == null) {
			transportDurationMin = order.getTransportDurPlanGoogleMin();
		}
		if (transportDurationMin == null) {
			return null;
		}
		final int transportDuration = transportDurationMin.intValue();
		final int loadingTime = calculateLoadingTimeForOrder(order, configBean)
				.intValue();
		final int deliveryTime = transportDuration + loadingTime;
		return deliveryTime;
	}

}
