/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.Calendar;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.dispatchers.DeliveryDateServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.ConfigServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DeliveryDateServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.utils.DeliveryDateUtils;
import com.crh.logon.business.services.utils.SalesAreaWebOrderOnWeekendSettings;
import com.crh.logon.business.services.utils.SchedulingUtils;
import com.crh.logon.business.services.utils.validation.ValidationErrorReason;
import com.crh.logon.constants.CustorderError;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.time.DateTimeUtils;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class DeliveryDateServiceBean extends DeliveryDateServiceDispatcher implements DeliveryDateServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DeliveryDateServiceBean.class);

	@EJB private OrderServiceLocalInterface orderService;
	@EJB private ConfigServiceLocalInterface configService;

	
	/////////1. START VALIDATIONS
	/**
	 * Validates the given sales area constants considering the same values of
	 * the configuration table if needed. Sales are constants have priority above
	 * configuration values, but if not set (null), then config value is taken.
	 * Rules (valid if fulfilled):
	 *   1) allowSdValue (C1) <= allowNdValue (C2)
	 * Currently applySdInMinutes (T1), applyNdInMinutes (T2) are considered valid.
	 * Must be called before saving SalesAreaEntity.
	 * 
	 * @param allowNdInMinutes
	 * @param applyNdInMinutes
	 * @param allowSdInMinutes
	 * @param applySdInMinutes
	 *
	 */
	@AdnTrace
	public ValidationErrorReason validateSareaDateConstants(Long allowNdInMinutes, Long applyNdInMinutes,
			Long allowSdInMinutes, Long applySdInMinutes) {

			long allowSdConfigValue = Long.valueOf(configService.getProperty(ConfigBean.CFG_APPDEFAULTSAREALEADTIMESAMEDAYALLOWWEBORDERUNTILMI));
			long allowSdValue = allowSdInMinutes == null ? allowSdConfigValue : allowSdInMinutes;
			
			long allowNdConfigValue = Long.valueOf(configService.getProperty(ConfigBean.CFG_APPDEFAULTSAREALEADTIMENEXTDAYALLOWWEBORDERUNTILMI));
			long allowNdValue = allowNdInMinutes == null ? allowNdConfigValue : allowNdInMinutes;
			
			if (allowSdValue > allowNdValue) {
				String[] additionalInfo = {};
				return new ValidationErrorReason(
						CustorderError.LEADTIME_WEB_ORDER_INCONSISTENT_ALLOW_SETTING,
						false, null, additionalInfo);
			}
			return null;
	}

	/**
	 * Validates the given shiptoSarea entity constants and its sales are constants, 
	 * considering all the lead time check constants of its sales area and the configuration 
	 * table if needed. ShiptoSarea has higher priority than sales area settings, which has 
	 * higher priority than configuration table values.
	 * Rules (valid if fulfilled):
	 *   1) allowSdValue (C1) <= allowNdValue (C2)
	 * Currently applySdInMinutes (T1), applyNdInMinutes (T2) are considered valid.
	 * Must be called before saving ShiptoSareaEntity.
	 * 
	 * @param shiptoSarea
	 * @param applyNdLeadtimeChkUntilMi
	 * @param applySdLeadtimeChkFromMi
	 *
	 */
	@AdnTrace
	public ValidationErrorReason validateShiptoSareaDateConstants(ShiptoSareaEntity shiptoSarea, Long applyNdLeadtimeChkUntilMi,
			Long applySdLeadtimeChkFromMi) {
			SareaEntity sarea = shiptoSarea.getSarea();
			Long allowNdInMinutes = sarea.getAllowNdWebOrderUntilMi();
			Long allowSdInMinutes = sarea.getAllowSdWebOrderUntilMi();
			Long applyNdInMinutes = sarea.getApplyNdLeadtimeChkUntilMi();
			Long applySdInMinutes = sarea.getApplySdLeadtimeChkFromMi();

			if (applyNdLeadtimeChkUntilMi != null) {
				applyNdInMinutes = applyNdLeadtimeChkUntilMi;
			}
			if (applySdLeadtimeChkFromMi != null) {
				applySdInMinutes = applySdLeadtimeChkFromMi;
			}
			return validateSareaDateConstants(allowNdInMinutes, applyNdInMinutes,
					allowSdInMinutes, applySdInMinutes);
	}
	/////////END VALIDATIONS
	
	/////////2. START GETTING LEADTIME CHECK SETTINGS VALUES	
	/**
	 * Returns the current allowNdWebOrderUntilMi setting of the order considering
	 * the SareaEntity and config table value in this order (first not-null among them).
	 * 
	 * @param order
	 * 
	 * @return allowNdWebOrderUntilMi
	 */
	public long getAllowNdWebOrderUntilMi(OrderEntity order) {
		// [[
		long result = 0;
		Object[][] params = {{"order", order}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getAllowNdWebOrderUntilMi", params);
		}
		// ]]
		try {
			return getAllowNdWebOrderUntilMi(order.getSarea());
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private long getAllowNdWebOrderUntilMi(SareaEntity sarea) {
		// [[
		long result = 0;
		Object[][] params = {{"sarea", sarea}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getAllowNdWebOrderUntilMi", params);
		}
		// ]]
		try {
			final long ndAllowConfigValue = Long.valueOf(configService.getProperty(ConfigBean.CFG_APPDEFAULTSAREALEADTIMENEXTDAYALLOWWEBORDERUNTILMI));
			result = DeliveryDateUtils.getAllowNdWebOrderUntilMiByPrio(sarea, ndAllowConfigValue);
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
	 * Returns the current applyNdLeadtimeChkUntilMi setting of the order considering
	 * the ShiptoSareaEntity, SareaEntity and config table value in this order (first
	 * not-null among them).
	 * 
	 * @return applyNdLeadtimeChkUntilMi
	 */
	@AdnTrace
	public long getApplyNdLeadtimeChkUntilMi(final SareaEntity sarea, final ShiptoSareaEntity shiptoSarea) {
		long result = 0L;
		final long ndApplyConfigValue = Long.valueOf(configService.getProperty(ConfigBean.CFG_APPDEFAULTSAREALEADTIMENEXTDAYAPPLYCHECKUNTILMI));
		result = DeliveryDateUtils.getApplyNdLeadtimeChkUntilMiByPrio(shiptoSarea, sarea, ndApplyConfigValue);
		return result;
	}

	@AdnTrace
	public long getApplyNdLeadtimeChkUntilMi(OrderEntity order) {
		final SareaEntity sarea = order.getSarea();
		final ShiptoSareaEntity shiptoSarea = orderService.fetchShiptoSarea(order.getShiptoSapBp(), order.getSarea());
		return getApplyNdLeadtimeChkUntilMi(sarea, shiptoSarea);
	}
	
	/**
	 * Returns the current allowSdWebOrderUntilMi setting of the order considering
	 * the SareaEntity and config table value in this order (first not-null among them).
	 * 
	 * @param order
	 * 
	 * @return allowSdWebOrderUntilMi
	 */
	public long getAllowSdWebOrderUntilMi(OrderEntity order) {
		// [[
		long result = 0L;
		Object[][] params = {{"order", order}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getAllowSdWebOrderUntilMi", params);
		}
		// ]]
		try {
			return getAllowSdWebOrderUntilMi(order.getSarea());
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private long getAllowSdWebOrderUntilMi(SareaEntity sarea) {
		// [[
		long result = 0L;
		Object[][] params = {{"sarea", sarea}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getAllowSdWebOrderUntilMi", params);
		}
		// ]]
		try {
			final long sdAllowConfigValue = Long.valueOf(configService.getProperty(ConfigBean.CFG_APPDEFAULTSAREALEADTIMESAMEDAYALLOWWEBORDERUNTILMI));
			result = DeliveryDateUtils.getAllowSdWebOrderUntilMiByPrio(sarea, sdAllowConfigValue);
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
	 * Returns the current applySdLeadtimeChkFromMi setting of the order considering
	 * the ShiptoSareaEntity, SareaEntity and config table value in this order (first
	 * not-null among them).
	 * 
	 * @return applySdLeadtimeChkFromMi
	 */
	@AdnTrace
	public long getApplySdLeadtimeChkFromMi(final SareaEntity sarea, final ShiptoSareaEntity shiptoSarea) {
		final long sdApplyConfigValue = Integer.valueOf(configService.getProperty(ConfigBean.CFG_APPDEFAULTSAREALEADTIMESAMEDAYAPPLYCHECKFROMMI));
		return DeliveryDateUtils.getApplySdLeadtimeChkFromMiByPrio(shiptoSarea, sarea, sdApplyConfigValue);
	}

	@AdnTrace
	public long getApplySdLeadtimeChkFromMi(OrderEntity order) {
		final SareaEntity sarea = order.getSarea();
		final ShiptoSareaEntity shiptoSarea = orderService.fetchShiptoSarea(order.getShiptoSapBp(), order.getSarea());
		return getApplySdLeadtimeChkFromMi(sarea, shiptoSarea);
	}
	
	/////////END GETTING LEADTIME CHECK SETTINGS VALUES

	/**
	 * Delivery time window validation for "truck" type web-order creation.
	 *
	 * @return custorderError - null, if there is no error; otherwise a proper error
	 */
	@AdnTrace
	public CustorderError checkTruckLeadTime(Date deliveryFromDat, Date deliveryToDat, SareaEntity sarea,
			ShiptoSareaEntity shiptoSarea) {

		Calendar nowCalendar  = DateTimeUtils.getNowCal();

		Long allowWebOrderOnWeekend = null;
		if(sarea != null) {
			allowWebOrderOnWeekend = sarea.getAllowWebOrderOnWeekend();
		}
		SalesAreaWebOrderOnWeekendSettings salesAreaWebOrderOnWeekendSettings = SalesAreaWebOrderOnWeekendSettings.valueOf(allowWebOrderOnWeekend);

		CustorderError result = DeliveryDateUtils.checkWeekendDelivery(salesAreaWebOrderOnWeekendSettings, deliveryFromDat,
				deliveryToDat);
		if (result != null) {
			return result;
		}

		final long allowNdWebOrderUntilMi = getAllowNdWebOrderUntilMi(sarea);
		final long applyNdLeadtimeChkUntilMi = getApplyNdLeadtimeChkUntilMi(sarea, shiptoSarea);
		final long allowSdWebOrderUntilMi = getAllowSdWebOrderUntilMi(sarea);
		final long applySdLeadtimeChkFromMi = getApplySdLeadtimeChkFromMi(sarea, shiptoSarea);

		//checks if there is weekend on the end of the delivery window and sets the latest possible delivery to time accordingly
		final Long latestPossibleDeliveryToTime = shiptoSarea.getShipto().getOpen2To();
		deliveryToDat = SchedulingUtils.returnLatestPossibleDeliveryToTime(salesAreaWebOrderOnWeekendSettings, deliveryToDat, latestPossibleDeliveryToTime);
		Calendar deliveryToCal = Calendar.getInstance();
		deliveryToCal.setTime(deliveryToDat);

		return DeliveryDateUtils.checkLeadTimes(salesAreaWebOrderOnWeekendSettings, deliveryToCal, nowCalendar,
				allowNdWebOrderUntilMi, applyNdLeadtimeChkUntilMi,
				allowSdWebOrderUntilMi, applySdLeadtimeChkFromMi);

	}

	@AdnTrace
	public CustorderError checkTruckLeadTime(OrderEntity order) {
		final ShiptoSareaEntity shiptoSarea = orderService.fetchShiptoSarea(order.getShiptoSapBp(), order.getSarea());
		return checkTruckLeadTime(order.getDeliveryFromDat(), order.getDeliveryToDat(), order.getSarea(),shiptoSarea);
	}
}
