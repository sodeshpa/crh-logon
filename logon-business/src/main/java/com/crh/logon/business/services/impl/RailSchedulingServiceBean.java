/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.RailSchedulingServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.ConfigServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RailSchedulingServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.OrderTakingEditorRailDeliveryDto;
import com.crh.logon.business.services.utils.OrderUtils;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.business.services.utils.validation.ValidationErrorReason;
import com.crh.logon.business.services.utils.validation.ValidationException;
import com.crh.logon.constants.CustorderError;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.RailDistanceEntity;
import com.crh.logon.persistence.entities.common.RailTimetableEntity;
import com.crh.logon.persistence.entities.common.RailwayStationEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.refdata.TimezoneBsEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.time.DateTimeUtils;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
@Alternative
@AdnTrace
@Interceptors(UserInfoInterceptor.class)
public class RailSchedulingServiceBean extends RailSchedulingServiceDispatcher implements
		RailSchedulingServiceLocalInterface {

	private static final long MINUTES_PER_DAY = 24 * 60L;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RailSchedulingServiceBean.class);

	@EJB ConfigServiceLocalInterface configService;

	/**
	 * Calculates the duration and distance between two railway stations.
	 */
	public Long[] calculateDurDistBetweenRailwayStations(Long depStationId, Long arrStationId) {
		Long[] result = new Long[2];

		Long duration = -1L;
		Long distance = getRailDistance(depStationId, arrStationId);

		RailTimetableEntity timetable = getNextTrainFromTimetable(depStationId, arrStationId);
		if (timetable != null) {
			if (timetable.getArrOffsetDays() == 0) {
				duration = timetable.getArrSwitchingTimeMin() - timetable.getDepTimeMin();
			}
			else {
				Long fullDayMin = MINUTES_PER_DAY;
				duration = fullDayMin - timetable.getDepSwitchingTimeMin()
						+ ((timetable.getArrOffsetDays() - 1) * fullDayMin)
						+ timetable.getArrSwitchingTimeMin();
			}
		}

		result[0] = duration;
		result[1] = distance;

		return result;
	}

	/**
	 * @param delFromDateTime real delivery from date, not shifted with any hours 
	 * @param delToDateTime real delivery to date, not shifted with any hours 
	 */
	public OrderTakingEditorRailDeliveryDto calculateRailDeliveryData(Long departureStationId,
			Long arrivalStationId, Date deliveryFromDateTime, Date deliveryToDateTime,
			ShiptoEntity shipTo, TimeZone userTimeZone, boolean isDualEditor) throws LogonException {
		OrderTakingEditorRailDeliveryDto railDeliveryData = 
				new OrderTakingEditorRailDeliveryDto(userTimeZone);

		DaoProvider daoProvider = getDaoProvider();
		ReadWriteDao<RailTimetableEntity> railTimeTableDao = daoProvider.getRailTimetableEntityDao();

		Long duration = -1L;
		Long distance = getRailDistance(departureStationId, arrivalStationId);

		Filter<RailTimetableEntity> timeTableFilter = new FilterImpl<RailTimetableEntity>();
		timeTableFilter.addExpression(Restrictions.eq(RailTimetableEntity.ATTR_ACTIVE_FLAG,
				Boolean.TRUE));
		timeTableFilter.addExpression(Restrictions.eq(
				RailTimetableEntity.ATTR_DEP_RAILWAY_STATION_ID, departureStationId));
		timeTableFilter.addExpression(Restrictions.eq(
				RailTimetableEntity.ATTR_ARR_RAILWAY_STATION_ID, arrivalStationId));
		timeTableFilter.addExpression(Order.asc(RailTimetableEntity.ATTR_DEP_SWITCHING_TIME_MIN));

		List<RailTimetableEntity> railSchedules = railTimeTableDao.find(timeTableFilter);

		TimezoneBsEntity shipToTimeZoneBs = shipTo.getShiptoSapBp().getTimezone();
		TimeZone shipToTimeZone;
		if (shipToTimeZoneBs == null) {
			shipToTimeZone = TimeZone.getAppTimeZone();
		}
		else {
			String shipToTimeZoneCode = shipToTimeZoneBs.getTimezoneCd();
			shipToTimeZone = new TimeZone(shipToTimeZoneCode, userTimeZone.getLocale());
		}

		Calendar now = shipToTimeZone.asCalendar();

		// --- Iterating through the timetable entries and days ---
		for (RailTimetableEntity railSchedule : railSchedules) {

			Calendar currentDay = shipToTimeZone.asCalendar();

			Long durationInWorkingDays = railSchedule.getArrOffsetDays();
			Long startOfDeparture = railSchedule.getDepSwitchingTimeMin();
			Long endOfArrival = railSchedule.getArrSwitchingTimeMin();
			if (durationInWorkingDays == 0) {
				Long startOfArrivalTimeOfDay = railSchedule.getDepTimeMin();
				duration = endOfArrival - startOfArrivalTimeOfDay;
			}
			else {
				duration = (durationInWorkingDays * MINUTES_PER_DAY) - startOfDeparture
						+ endOfArrival;
			}

			do {
				Calendar currDep = DateTimeUtils.getMorningOfDay(currentDay);
				Calendar currArr = DateTimeUtils.getMorningOfDay(currentDay);

				currDep.add(Calendar.MINUTE, startOfDeparture.intValue());
				currArr.add(Calendar.MINUTE, endOfArrival.intValue());
				if (durationInWorkingDays > 0) {
					currArr.add(Calendar.DAY_OF_YEAR, durationInWorkingDays.intValue());
				}

				if (railDeliveryData.getEarliestDeliveryDatArr() == null
						|| !currArr.getTime().after(railDeliveryData.getEarliestDeliveryDatArr())) {
					if (currDep.after(now)) {
						//LOG.debug("TT: Earliest: " + currDep.getTime() + " / " + currArr.getTime());
						railDeliveryData.setEarliestDeliveryDatDep(currDep.getTime());
						railDeliveryData.setEarliestDeliveryDatArr(currArr.getTime());
						railDeliveryData.setActualDeliveryDatDep(currDep.getTime());
						railDeliveryData.setActualDeliveryDatArr(currArr.getTime());
						railDeliveryData.setDurationMinutes(duration);
					}
				}

				if ((railDeliveryData.getActualDeliveryDatArr() != null && (currArr.getTime().after(
						railDeliveryData.getActualDeliveryDatArr()) || duration < railDeliveryData.getDurationMinutes()))) {
					if (currDep.after(now) && currArr.getTime().before(deliveryToDateTime)) {
						//LOG.debug("TT: actual: " + currDep.getTime() + " / " + currArr.getTime());
						railDeliveryData.setDurationMinutes(duration);
						railDeliveryData.setActualDeliveryDatDep(currDep.getTime());
						railDeliveryData.setActualDeliveryDatArr(currArr.getTime());
					}
				}

				currentDay.add(Calendar.DAY_OF_YEAR, 1);
			} while (currentDay.getTime().before(deliveryToDateTime)
					|| railDeliveryData.getEarliestDeliveryDatArr() == null);
		}

		// --- If the earliest delivery date is later than the field value on the editor ---
		int defaultOpen2ToMin = Integer.valueOf(configService.getProperty(ConfigBean.CFG_APPDEFAULTSHIPTOOPEN2TOMIN));
		int defaultOpen1FromMin = Integer.valueOf(configService.getProperty(ConfigBean.CFG_APPDEFAULTSHIPTOOPEN1FROMMIN));
		Long open1From = shipTo.getOpen1From();
		int open1FromMin = (open1From == null ? defaultOpen1FromMin : open1From.intValue());
		Long open2To = shipTo.getOpen2To();
		int open2ToMin = (open2To == null ? defaultOpen2ToMin : open2To.intValue());
		if (isDualEditor) {
			// --- For bimodal order (in dual editor mode), the LP loading time and ship-to opening hours should be taken into account too (for the truck part) ---
			if (railDeliveryData.getEarliestDeliveryDatArr() != null) {
				Calendar earliestDel = shipToTimeZone.asCalendar(railDeliveryData.getEarliestDeliveryDatArr());

				if (deliveryFromDateTime.before(earliestDel.getTime())) {
					Calendar overriddenDat = (Calendar) earliestDel.clone();
					Calendar closingTimeOnEarliestDelDay = (Calendar) earliestDel.clone();
					closingTimeOnEarliestDelDay.add(Calendar.MINUTE, open2ToMin);
					if (overriddenDat.after(closingTimeOnEarliestDelDay)) {
						overriddenDat = DateTimeUtils.getMorningOfDay(DateTimeUtils.getNextWorkingDay(overriddenDat));
						overriddenDat.add(Calendar.MINUTE, open1FromMin);
					}
					railDeliveryData.setOverriddenDeliveryFromDateTime(overriddenDat.getTime());
				}
				if (deliveryToDateTime.before(earliestDel.getTime())) {
					Calendar overriddenDat = DateTimeUtils.getMorningOfDay(earliestDel);
					overriddenDat.add(Calendar.MINUTE, open2ToMin);

					if (overriddenDat.before(earliestDel)) {
						overriddenDat = DateTimeUtils.getMorningOfDay(DateTimeUtils.getNextWorkingDay(overriddenDat));
						overriddenDat.add(Calendar.MINUTE, open2ToMin);
					}
					railDeliveryData.setOverriddenDeliveryToDateTime(overriddenDat.getTime());
				}
			}
		}
		else {
			// --- Normal rail order, 23:59 on the date of the earliest delivery is OK ---
			if (railDeliveryData.getEarliestDeliveryDatArr() != null) {
				if (deliveryFromDateTime.before(railDeliveryData.getEarliestDeliveryDatArr())) {
					// --- Since there is no time part fields for rail direct order, the from and to dates can be equal ---
					railDeliveryData.setOverriddenDeliveryFromDateTime(DateTimeUtils.getMidnightOfDay(railDeliveryData.getEarliestDeliveryDatArr()));
				}
				if (deliveryToDateTime.before(railDeliveryData.getEarliestDeliveryDatArr())) {
					railDeliveryData.setOverriddenDeliveryToDateTime(DateTimeUtils.getMidnightOfDay(railDeliveryData.getEarliestDeliveryDatArr()));
				}
			}
		}

		railDeliveryData.setDistanceMeters(distance);

		return railDeliveryData;
	}

	private Long getRailDistance(Long departureStationId, Long arrivalStationId) {
		Filter<RailDistanceEntity> distFilter = new FilterImpl<RailDistanceEntity>();
		distFilter.addExpression(Restrictions.eq(RailDistanceEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
		distFilter.addExpression(Restrictions.or(
				Restrictions.and(Restrictions.eq(RailDistanceEntity.ATTR_ARR_RAILWAY_STATION_ID,
						arrivalStationId), Restrictions.eq(
						RailDistanceEntity.ATTR_DEP_RAILWAY_STATION_ID, departureStationId)),
				Restrictions.and(Restrictions.eq(RailDistanceEntity.ATTR_ARR_RAILWAY_STATION_ID,
						departureStationId), Restrictions.eq(
						RailDistanceEntity.ATTR_DEP_RAILWAY_STATION_ID, arrivalStationId))));

		Long distance = -1L;

		ReadWriteDao<RailDistanceEntity> railDistanceDao = getDaoProvider().getRailDistanceEntityDao();
		List<RailDistanceEntity> railDistances = railDistanceDao.find(distFilter);
		if (railDistances.size() > 0) {
			Long tariffDistanceM0 = railDistances.get(0).getTariffDistanceM() != null ? railDistances.get(
					0).getTariffDistanceM()
					: -1L;
			distance = tariffDistanceM0;
			switch (railDistances.size()) {
			case 1: // only 1 distance entry, this is the right distance.
				break;
			case 2: // 2 distances found, if it's A->B and B->A then both entries should have the same distance value.
				Long tariffDistanceM1 = railDistances.get(1).getTariffDistanceM() != null ? railDistances.get(
						1).getTariffDistanceM()
						: -1L;
				if (tariffDistanceM0 != null && tariffDistanceM1 != null
						&& !tariffDistanceM0.equals(tariffDistanceM1)) {
					LOG.error("WARNING: Found unsymmetric distance in TLGNC_RAIL_DISTANCE for stations: "
							+ departureStationId
							+ " and "
							+ arrivalStationId
							+ ". Please verify the DB content");
				}
				break;
			default: // > 2 distances found; log error
				LOG.error("WARNING: Found multiple distances in TLGNC_RAIL_DISTANCE for stations: "
						+ departureStationId + " and " + arrivalStationId
						+ ". Please verify the DB content");
				break;
			}
		}

		return distance;
	}

	/**
	 * Returns the next train between two railway stations, according to the timetable
	 */
	public RailTimetableEntity getNextTrainFromTimetable(Long departureStationId,
			Long arrivalStationId) {
		ReadWriteDao<RailTimetableEntity> dao = getDaoProvider().getRailTimetableEntityDao();

		Filter<RailTimetableEntity> filter = new FilterImpl<RailTimetableEntity>();
		filter.addExpression(Restrictions.eq(RailTimetableEntity.ATTR_DEP_RAILWAY_STATION_ID,
				departureStationId));
		filter.addExpression(Restrictions.eq(RailTimetableEntity.ATTR_ARR_RAILWAY_STATION_ID,
				arrivalStationId));

		List<RailTimetableEntity> railSchedules = dao.findNRecs(0, 1, filter);
		if (railSchedules.size() > 0) {
			return railSchedules.get(0);
		}
		return null;
	}

	/**
	 * The purpose of the function is to determine the actual delivery date for a bimodal rail/rail order.
	 * The method throws exception
	 * in case validation error happens.
	 * 
	 * @param shipTo
	 * @param custOrderEntity
	 * @return
	 */
	public void applyLeadTimeCheck(ShiptoEntity shipTo, CustorderEntity custOrderEntity,
			ConfigBean configBean, TimeZone userTimeZone) throws ValidationException {
		LinkedList<ValidationErrorReason> validationErrorReasons = new LinkedList<ValidationErrorReason>();
		final ShippingpointEntity railwayStationFrom = custOrderEntity.getShippingpoint();
		final RailwayStationEntity railwayStationTo = custOrderEntity.getShiptoSapBp().getRailwayStation();
		final Date morningOfDay = custOrderEntity.getDeliveryFromDat();
		final Date midnightOfDay = custOrderEntity.getDeliveryToDat();
		final Long deliveryTypeCd = custOrderEntity.getDeliveryType().getDeliveryTypeCd();

		OrderTakingEditorRailDeliveryDto railData = calculateRailDeliveryData(
				railwayStationFrom.getRailwayStation().getRailwayStationId(),
				railwayStationTo.getRailwayStationId(), morningOfDay, midnightOfDay,
				shipTo, userTimeZone,
				LogonUtil.isBimodalRailPartOrder(deliveryTypeCd));

		Date railActualDeliveryDep = railData.getActualDeliveryDatDep();

		final SareaEntity sarea = custOrderEntity.getSarea();

		if (railActualDeliveryDep != null) {
			// Check for sales area level default value
			Long latestWebOrderPlacingTime = sarea.getAllowBimodalCreationTillMi();
			// Fallback
			if (latestWebOrderPlacingTime == null) {
				latestWebOrderPlacingTime = Long.valueOf(configBean.getAppDefaultWeborderAllowBimodalCreationUntilMin());
			}
			latestWebOrderPlacingTime = latestWebOrderPlacingTime * 60 * 1000;
			Date now = new Date();
			Date todayMorning = DateTimeUtils.getMorningOfDay(now);
			Date midnightOfDay2 = DateTimeUtils.getMidnightOfDay(now);
			
			long latestWebTime = todayMorning.getTime() + latestWebOrderPlacingTime;
			
			Calendar latestWebOrderTime = Calendar.getInstance();
			latestWebOrderTime.setTimeInMillis(latestWebTime);
			
			Calendar cal = DateTimeUtils.getDateAsCalendar(railActualDeliveryDep);
			boolean isDeparureToday = cal.after(DateTimeUtils.getDateAsCalendar(todayMorning))
					&& cal.before(DateTimeUtils.getDateAsCalendar(midnightOfDay2));
			boolean isLateOrder = (DateTimeUtils.getDateAsCalendar(now)).after(latestWebOrderTime);
			
			if (isDeparureToday && isLateOrder) {
				final String latestWebOrderTimeString = DateTimeUtils.getHoursMinutesString(latestWebOrderTime.getTime());
				String[] additionalInfo = { latestWebOrderTimeString };
				validationErrorReasons.add(new ValidationErrorReason(
						CustorderError.WEB_BIMODAL_RAIL_LEADTIME_CHECK, custOrderEntity,
						additionalInfo));

			}

			if (LOG.isDebugEnabled()) {
				OrderUtils.LOG.debug("--------------------");
				OrderUtils.LOG.debug("Today morning date: " + todayMorning.toString());
				OrderUtils.LOG.debug("Today midnight " + midnightOfDay2.toString());
				OrderUtils.LOG.debug("Now " + now.toString());
				OrderUtils.LOG.debug("Latest web time date: "
						+ latestWebOrderTime.getTime().toString());
				OrderUtils.LOG.debug("Rail actual delivery DEPARTURE date "
						+ railActualDeliveryDep.toString());
				//the order should depart on the day of placing the order AND now is later than the latest web order placing time (config)
				OrderUtils.LOG.debug("Departure is today: " + isDeparureToday);
				OrderUtils.LOG.debug("Now is later than the latest web order placing time: "
						+ isLateOrder);
				OrderUtils.LOG.debug("--------------------");
			}
		}
		else {
			String[] additionalInfo = {};
			validationErrorReasons.add(new ValidationErrorReason(
					CustorderError.ACTUAL_DELIVERY_CALCULATION_FAILURE, custOrderEntity,
					additionalInfo));
		}
		if (!validationErrorReasons.isEmpty()) {
			throw new ValidationException(validationErrorReasons);
		}
	}

}
