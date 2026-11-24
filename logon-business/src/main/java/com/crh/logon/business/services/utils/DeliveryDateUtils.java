/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.util.Calendar;
import java.util.Date;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.constants.CustorderError;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.utils.time.DateTimeUtils;

/**
 * @author AdNovum Informatik AG
 */
public class DeliveryDateUtils {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DeliveryDateUtils.class);

	/////////1. START GETTING LEADTIME CHECK SETTINGS VALUES
	/**
	 * Getting the highest priority value for the allowNdWebOrderUntilMi based on the input
	 * parameters.
	 * Priorities: sarea > configValue.
	 * 
	 * @param sarea
	 * @param configValue
	 * 
	 * @return allowNdWebOrderUntilMi
	 */
	public static long getAllowNdWebOrderUntilMiByPrio(SareaEntity sarea, long configValue) {
		// [[
		long result = 0L;
		Object[][] params = {{"sarea", sarea},
				{"configValue", configValue}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DeliveryDateUtils.class, "getAllowNdWebOrderUntilMiByPrio", params);
		}
		// ]]
		try {
			if (sarea.getAllowNdWebOrderUntilMi() != null) {
				return result = sarea.getAllowNdWebOrderUntilMi();
			}
			else {
				return result = configValue;
			}
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
	 * Getting the highest priority value for the applyNdLeadtimeChkUntilMi based on the input
	 * parameters.
	 * Priorities: shiptoSarea > sarea > configValue.
	 * 
	 * @param shiptoSarea
	 * @param sarea
	 * @param configValue
	 * 
	 * @return applyNdLeadtimeChkUntilMi
	 */
	public static long getApplyNdLeadtimeChkUntilMiByPrio(ShiptoSareaEntity shiptoSarea, SareaEntity sarea, 
			long ndApplyConfigValue) {
		// [[
		long result = 0L;
		Object[][] params = {{"shiptoSarea", shiptoSarea},
				{"sarea", sarea},
				{"ndApplyConfigValue", ndApplyConfigValue}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DeliveryDateUtils.class, "getApplyNdLeadtimeChkUntilMiByPrio", params);
		}
		// ]]
		try {
			if (shiptoSarea.getApplyNdLeadtimeChkUntilMi() != null) {
				return shiptoSarea.getApplyNdLeadtimeChkUntilMi();
			}
			else if (sarea.getApplyNdLeadtimeChkUntilMi() != null) {
				return sarea.getApplyNdLeadtimeChkUntilMi();
			}
			else {
				return ndApplyConfigValue;
			}
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
	 * Getting the highest priority value for the allowSdWebOrderUntilMi based on the input
	 * parameters.
	 * Priorities: sarea > configValue.
	 * 
	 * @param sarea
	 * @param configValue
	 * 
	 * @return AllowSdWebOrderUntilMi
	 */
	public static long getAllowSdWebOrderUntilMiByPrio(SareaEntity sarea, long configValue) {
		// [[
		long result = 0;
		Object[][] params = {{"sarea", sarea},
				{"configValue", configValue}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DeliveryDateUtils.class, "getAllowSdWebOrderUntilMiByPrio", params);
		}
		// ]]
		try {
			if (sarea.getAllowSdWebOrderUntilMi() != null) {
				return result = sarea.getAllowSdWebOrderUntilMi();
			}
			else {
				return result = configValue;
			}
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
	 * Getting the highest priority value for the applySdLeadtimeChkFromMi based on the input
	 * parameters.
	 * Priorities: shiptoSarea > sarea > configValue.
	 * 
	 * @param shiptoSarea
	 * @param sarea
	 * @param configValue
	 * 
	 * @return applySdLeadtimeChkFromMi
	 */
	public static long getApplySdLeadtimeChkFromMiByPrio(ShiptoSareaEntity shiptoSarea, SareaEntity sarea, long sdApplyConfigValue) {
		// [[
		long result = 0;
		Object[][] params = {{"shiptoSarea", shiptoSarea},
				{"sarea", sarea},
				{"sdApplyConfigValue", sdApplyConfigValue}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DeliveryDateUtils.class, "getApplyNdLeadtimeChkUntilMiByPrio", params);
		}
		// ]]
		try {
			if (shiptoSarea.getApplySdLeadtimeChkFromMi() != null) {
				return shiptoSarea.getApplySdLeadtimeChkFromMi();
			}
			else if (sarea.getApplySdLeadtimeChkFromMi() != null) {
				return sarea.getApplySdLeadtimeChkFromMi();
			}
			else {
				return sdApplyConfigValue;
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	/////////END GETTING LEADTIME CHECK SETTINGS VALUES
	
	/**
	 * Checks the deliveryTo date compared to the order creation date, considering the
	 * lead time check constants.
	 * If deliveryTo date is valid for this creation date, null is returned, otherwise
	 * an error.
	 * 
	 * @param deliveryToCal
	 * @param nowCalendar
	 * @param allowNdWebOrderUntilMi
	 * @param applyNdLeadtimeChkUntilMi
	 * @param allowSdWebOrderUntilMi
	 * @param applySdLeadtimeChkFromMi
	 * @return
	 */
	public static CustorderError checkLeadTimes(SalesAreaWebOrderOnWeekendSettings salesAreaWebOrderOnWeekendSettings, Calendar deliveryToCal, Calendar nowCalendar,
			long allowNdWebOrderUntilMi, long applyNdLeadtimeChkUntilMi, 
			long allowSdWebOrderUntilMi, long applySdLeadtimeChkFromMi) {
		// [[
		CustorderError result = null;
		Object[][] params = {{"deliveryToCal", deliveryToCal},
				{"nowCalendar", nowCalendar},
				{"allowNdWebOrderUntilMi", allowNdWebOrderUntilMi},
				{"applyNdLeadtimeChkUntilMi", applyNdLeadtimeChkUntilMi},
				{"allowSdWebOrderUntilMi", allowSdWebOrderUntilMi},
				{"applySdLeadtimeChkFromMi", applySdLeadtimeChkFromMi}
				};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DeliveryDateUtils.class, "checkLeadTimes", params);
		}
		// ]]
		try {
			boolean isDeliveryToOnCurrentDay = DateTimeUtils.isOnSameDay(nowCalendar.getTime(), deliveryToCal.getTime());
			boolean isDeliveryToDateOnNextWorkingDay = SchedulingUtils.isOnNextWorkingDay(salesAreaWebOrderOnWeekendSettings, nowCalendar.getTime(), deliveryToCal.getTime());
			
			long deliveryToTimeInMinutes = DateTimeUtils.getTimePartInMinutes(deliveryToCal);
			long currentTimeInMinutes = DateTimeUtils.getTimePartInMinutes(nowCalendar);
			
			//dividing today and tomorrow based on deliveryTo time; 
			//checking if order creation time (i.e. currentTime) is in invalid interval
			//case a-d is a division of this and next day (deliveryTo is divided). They are
			//disjunct intervals covering these 2 days. For figure, see attachment of LOGON-6590.
			if (isDeliveryToOnCurrentDay && deliveryToTimeInMinutes < applySdLeadtimeChkFromMi) {//case a)
				return result = CustorderError.LEADTIME_WEB_ORDER_SD_EARLY;
			}
			else if (isDeliveryToOnCurrentDay && deliveryToTimeInMinutes >= applySdLeadtimeChkFromMi) {//case b)
				if (currentTimeInMinutes >= allowSdWebOrderUntilMi) {
					return result = CustorderError.LEADTIME_WEB_ORDER_SD_LATER;
				}
			}
			else if (isDeliveryToDateOnNextWorkingDay && deliveryToTimeInMinutes < applyNdLeadtimeChkUntilMi) {//case c)
				if (currentTimeInMinutes >= allowNdWebOrderUntilMi) {
					return result = CustorderError.LEADTIME_WEB_ORDER_ND_EARLY;
				}
			}
			else if (isDeliveryToDateOnNextWorkingDay && deliveryToTimeInMinutes >= applyNdLeadtimeChkUntilMi) {//case d)
				;//the whole interval is accepted
			}
			return result;
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
	 * Checks if the delivery of the order is completely included within the weekend. If so,
	 * then returns an error, otherwise it's null.
	 * 
	 * @param deliveryFromDate
	 * @param deliveryToDate
	 * @return
	 */
	public static CustorderError checkWeekendDelivery(SalesAreaWebOrderOnWeekendSettings salesAreaWebOrderOnWeekendSettings, Date deliveryFromDate, Date deliveryToDate) {
		// [[
		CustorderError result = null;
		Object[][] params = {{"deliveryFromDate", deliveryFromDate},
				{"deliveryToDate", deliveryToDate}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DeliveryDateUtils.class, "checkWeekendDelivery", params);
		}
		// ]]
		try {
			boolean isWholeDeliveryOnWeekendDays = SchedulingUtils.isWholeIntervalOnNonWorkingDays(salesAreaWebOrderOnWeekendSettings, deliveryFromDate, deliveryToDate);
			if (isWholeDeliveryOnWeekendDays) {
				return result = CustorderError.LEADTIME_WEB_ORDER_WEEKEND;
			}
			return result;
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
