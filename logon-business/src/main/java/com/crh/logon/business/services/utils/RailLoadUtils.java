/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.views.DispoOrderItemListEntity;
import com.crh.logon.persistence.entities.views.DispoOrderListEntity;
import com.crh.logon.persistence.entities.views.DispoWContainerListEntity;
import com.crh.logon.persistence.entities.views.DispoWagonListEntity;
import com.crh.logon.persistence.entities.views.DispoWagonTypeListEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.time.DateTimeUtils;

/**
 * @author AdNovum Informatik AG
 */
public class RailLoadUtils {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RailLoadUtils.class);

	/**
	 * Compute possible order which can be load on the corresponding wagon
	 * 
	 * @param wagonWrapper The wrapper object of the current wagon
	 * @param orders The list of the selected orders which might be loaded on the current wagon.
	 * @param timeZone 
	 * @throws LogonException If the order is an external order a technical exception will be thrown, because
	 * 							the external orders are not handled/scheduled by the rail case.
	 * */
	static public void computePossibleOrdersToLoad(WagonWrapper wagonWrapper,
			List<OrderWrapper> orders, Date referenceDate, TimeZone timeZone) {
		// [[
		Object[][] params = { { "wagonWrapper", wagonWrapper }, { "orders", orders },
				{ "referenceDate", referenceDate }, { "timeZone", timeZone } };
		final boolean isEnteringExitingEnabled = LOG.isEnteringExitingEnabled();
		if (isEnteringExitingEnabled) {
			LOG.entering(RailLoadUtils.class, "computePossibleOrdersToLoad", params);
		}
		// ]]
		try {
			Set<OrderPrioWrapper> prio = new TreeSet<OrderPrioWrapper>();
			DispoWagonListEntity wagon = wagonWrapper.getWagon();
			for (OrderWrapper orderWrapper : orders) {
				if (orderWrapper.isExternalOrder()) {
					LogonException externalOrderException = new LogonException(
							"External orders are not handled on Wagon scheduling",
							Reason.RES_IMPL_BUG);
					throw externalOrderException;
				}

				DispoOrderListEntity order = orderWrapper.getLogonOrder();

				if (order.getArrOffsetDays() == null) {
					//no train schedule, skip order
					continue;
				}

				if (order.getOnHoldOrderAndLockedFlag()) {
					//call-off, skip order
					continue;
				}

				boolean railOrderIsCompatibleWithWagon = matchWagonTypeWithOrder(wagonWrapper,
						orderWrapper);
				if (isOrderArrivesInTime(order, referenceDate, timeZone)
						&& railOrderIsCompatibleWithWagon) {
					List<DispoWagonTypeListEntity> wagonTypes = orderWrapper.getWagonTypeList();
					for (DispoWagonTypeListEntity wagonType : wagonTypes) {
						if (wagonType.getWagonTypeCd().equals(wagon.getWagonTypeCd())) {
							Long evaluationPriority = wagonType.getEvaluationPriority();
							OrderPrioWrapper orderPrioWrapper = new OrderPrioWrapper(orderWrapper,
									evaluationPriority);
							prio.add(orderPrioWrapper);
							break;
						}
					}
				}
			}
			wagonWrapper.setPossibleOrders(new ArrayList<OrderPrioWrapper>(prio));
		}
		finally {
			// [[
			if (isEnteringExitingEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	/**
	 * Calculate the order is automatically loadable or manually scheduling is needed
	 * 
	 * @param order The current order
	 * @param referenceDate The reference date
	 * @param userTimeZone 
	 * @return result If true the order arrives at Time on the dispo and can be pre-loaded else false.
	 * 
	 * */
	static public boolean isOrderArrivesInTime(DispoOrderListEntity order, Date referenceDate,
			TimeZone userTimeZone) {
		// [[
		boolean result = false;
		Object[][] params = { { "order", order }, { "referenceDate", referenceDate },
				{ "userTimeZone", userTimeZone } };
		final boolean isEnteringExitingEnabled = LOG.isEnteringExitingEnabled();
		if (isEnteringExitingEnabled) {
			LOG.entering(RailLoadUtils.class, "orderArrivesInTime", params);
		}
		// ]]
		try {
			int railTimeTableArrOffsetDays = order.getArrOffsetDays().intValue();

			// convert the reference date to the user's time zone
			Calendar referenceDateDayStart = userTimeZone.asCalendar(referenceDate);

			Date dayStartOfLatestPossibleDelivery = getLoadDateForDeliveryDate(
					order.getDeliveryToDat(), railTimeTableArrOffsetDays);
			// convert latest delivery time to user's time zone
			Calendar dayStartOfLatestPossibleDeliveryCal = userTimeZone.asCalendar(dayStartOfLatestPossibleDelivery);
			if (dayStartOfLatestPossibleDeliveryCal.before(referenceDateDayStart)) {
				// can't arrive on time anymore, skip order (needs manual scheduling)
				return result = false;
			}
			
			Date dayStartOfEarliestPossibleDelivery = getLoadDateForDeliveryDate(
					order.getDeliveryFromDat(), railTimeTableArrOffsetDays);
			// convert earliest delivery time to user's time zone
			Calendar dayStartOfEarliestPossibleDeliveryCal = userTimeZone.asCalendar(dayStartOfEarliestPossibleDelivery);
			if (dayStartOfEarliestPossibleDeliveryCal.after(referenceDateDayStart)) {
				// too early, skip order (needs manual scheduling)
				return result = false;
			}
			
			return result = true;
		}
		finally {
			// [[
			if (isEnteringExitingEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	/**
	 * Subtracts the time needed for arrival and any subsequent weekend days from the given orderDeliveryDate
	 * 
	 * @param orderDeliveryDate
	 * @param railTimeTableArrOffsetDays
	 * @return 
	 */
	private static Date getLoadDateForDeliveryDate(Date orderDeliveryDate,
			int railTimeTableArrOffsetDays) {

		Calendar orderDeliveryCal = Calendar.getInstance();
		orderDeliveryCal.setTime(orderDeliveryDate);
		Calendar dayStartOfPossibleDelivery = DateTimeUtils.getMorningOfDay(orderDeliveryCal);

		dayStartOfPossibleDelivery.add(Calendar.DAY_OF_YEAR, -1 * railTimeTableArrOffsetDays);

		switch (dayStartOfPossibleDelivery.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.SATURDAY:
			dayStartOfPossibleDelivery.add(Calendar.DAY_OF_YEAR, -1);
			break;
		case Calendar.SUNDAY:
			dayStartOfPossibleDelivery.add(Calendar.DAY_OF_YEAR, -2);
			break;
		default:
			// we don't have to do anything if it isn't a weekend day
			break;
		}

		return dayStartOfPossibleDelivery.getTime();
	}

	/**
	 * Match the input wagon type with the order and determine the compatibility
	 * @param orderWrapper The order
	 * @param wagonWrapper The wagon
	 * @return isCompatible If the order mat.package.cd based params compatible with
	 * 						the wagon transport Category then true else false.
	 * 
	 * */
	static public boolean matchWagonTypeWithOrder(WagonWrapper wagonWrapper,
			OrderWrapper orderWrapper) {
		// [[
		boolean result = false;
		Object[][] params = { { "wagonWrapper", wagonWrapper }, { "orderWrapper", orderWrapper } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(RailLoadUtils.class, "matchWagonTypeWithOrder", params);
		}
		// ]]
		try {
			boolean isCompatible = true;
			DispoWagonListEntity wagon = wagonWrapper.getWagon();
			if (wagon.getWagonTrspCatCd() == Constant.WAGON_TKATEGORIE_CONTAINER) {
				//check if for every order item there is either an empty container or a container 
				//loaded with the right material
				if (!orderWrapper.getIsContainerType()) {
					return result = false;
				}
				List<DispoOrderItemListEntity> items = new ArrayList<DispoOrderItemListEntity>(
						orderWrapper.getMaterialItems());
				int nrEmpty = 0;
				Iterator<ContainerWrapper> containerIterator = wagonWrapper.getContainers().iterator();
				while (containerIterator.hasNext() && (nrEmpty < items.size())) {
					ContainerWrapper containerWrapper = containerIterator.next();
					DispoWContainerListEntity container = containerWrapper.getContainer();

					if (container.getSapMaterialId() != null) {
						Iterator<DispoOrderItemListEntity> orderItemsIterator = items.iterator();
						while (orderItemsIterator.hasNext()) {
							DispoOrderItemListEntity item = orderItemsIterator.next();
							if (!container.getSapMaterialId().equals(item.getSapMaterialId())) {
								continue;
							}
							if (!container.getProductionPlantCd().equals(
									item.getProductionPlantCd())) {
								continue;
							}
						}
						orderItemsIterator.remove();
					}
					else {
						nrEmpty++;
					}
				}
				if (nrEmpty < items.size()) {
					return result = false;
				}
			}
			else {
				if (wagon.getWagonTrspCatCd() == Constant.WAGON_TKATEGORIE_BAG
						&& !orderWrapper.getIsBag()
						|| wagon.getWagonTrspCatCd() == Constant.WAGON_TKATEGORIE_SILO
						&& !orderWrapper.getIsRailSiloType()) {
					return result = false;
				}

				//If wagon preloaded - check the preloaded material
				if (wagon.getSapMaterialId() != null) {
					DispoOrderItemListEntity item = (DispoOrderItemListEntity) orderWrapper.getItems().get(
							0);
					if (!wagon.getSapMaterialId().equals(item.getSapMaterialId())) {
						return result = false;
					}
					if (!wagon.getProductionPlantCd().equals(item.getProductionPlantCd())) {
						return result = false;
					}
				}
			}
			return result = isCompatible;
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
