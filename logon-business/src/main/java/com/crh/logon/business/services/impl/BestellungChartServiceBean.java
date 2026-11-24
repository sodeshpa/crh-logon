/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.ejb.Stateless;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Expression;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.JPAQuery;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.gdi.jpa.filter.SelectExpression;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.BestellungChartServiceDispatcher;
import com.crh.logon.business.services.localinterfaces.BestellungChartServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.PoolTruckEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.common.TruckUnavailabilityEntity;
import com.crh.logon.persistence.entities.views.ChartLoadScheduledEntity;
import com.crh.logon.persistence.entities.views.ChartLoadUnscheduledEntity;
import com.crh.logon.persistence.entities.views.DispoWagonTypeListEntity;
import com.crh.logon.persistence.entities.views.OrdChartNotAssignedEntity;
import com.crh.logon.persistence.entities.views.PlantSareaCompcodeEntity;
import com.crh.logon.persistence.entities.views.RailChartEntity;
import com.crh.logon.persistence.entities.views.TruckChartAssignedEntity;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.utils.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
public class BestellungChartServiceBean extends BestellungChartServiceDispatcher 
	implements BestellungChartServiceLocalInterface {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(BestellungChartServiceBean.class);
	
	public long getFilteredTruckTotalCount(List poolIds, List carrierIds, List truckTypes, List productType) {
		// [[
		long result = 0L;
		Object[][] params = {{"poolIds", poolIds},
							{"carrierIds", carrierIds},
							{"truckTypes", truckTypes},
							{"productType", productType}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getFilteredTruckTotalCount", params);
		}
		// ]]
		try {
			Filter filter = new FilterImpl();
			if (!isAll(poolIds)) {
				Filter poolFilter = new FilterImpl();
				poolFilter.addExpression(Restrictions.in(PoolTruckEntity.ATTR_POOL_ID, poolIds));
				JPAQuery jpaQuery = getDaoProvider().getPoolTruckEntityDao().createJPAQuery();
				jpaQuery.setFilter(poolFilter);
				jpaQuery.addSelect(SelectExpression.simpleSelect(PoolTruckEntity.ATTR_TRUCK_ID));
				jpaQuery.setDistinct(true);
				List truckIdList = getDaoProvider().getPoolTruckEntityDao().find(jpaQuery);
				if (truckIdList.isEmpty()) {
					
					return 0l;
				}
				filter.addExpression(Restrictions.in(TruckEntity.ATTR_TRUCK_ID, truckIdList));
			}
			if (!isAll(carrierIds)) {
				filter.addExpression(Restrictions.in(TruckEntity.ATTR_CARRIER_ID, carrierIds));
			}
			if (!isAll(truckTypes)) {
				filter.addExpression(Restrictions.in(TruckEntity.ATTR_TRUCK_TYPE_CD, truckTypes));
			}
			// -- product type
			if (!isAll(productType)) {
				if (productType.contains(Integer.valueOf(Constant.PRODUCT_TYPE_BAG))) {					
					filter.addExpression(Restrictions.eq(TruckEntity.ATTR_TRUCK_TRANSPORT_CAT_CD,
							Constant.TRUCK_TRANSPORT_CAT_CD_BAG));
				}
				/*
				 * TODO (marius Oct 10, 2012):
				 * Possibly implement here the logic of table 5 in the LRQ 185.0/185.1 SDS:
				 * Depending on the product type, filter only the applicable transport types.
				 */
				// marius/2012-10-10: Removed old filtering by isAgg and isCem flags.
				
				// TODO (marius Oct 10, 2012): Remove old uncommented code when new implementation is finished.
//				if (productType.contains(Integer.valueOf(Constant.PRODUCT_TYPE_BULK_LANDFILL))
//						|| productType.contains(Integer.valueOf(Constant.PRODUCT_TYPE_BULK_AGG))) {
//					filter.addExpression(Restrictions.eq(TruckEntity.ATTR_TRUCK_TYPE + "."
//							+ TruckTypeBsEntity.ATTR_TRUCK_TYPE_IS_AGG, Boolean.TRUE));
//				}
//				if (productType.contains(Integer.valueOf(Constant.PRODUCT_TYPE_BULK_CEM))) {
//					filter.addExpression(Restrictions.eq(TruckEntity.ATTR_TRUCK_TYPE + "."
//							+ TruckTypeBsEntity.ATTR_TRUCK_TYPE_IS_CEM, Boolean.TRUE));
//				}
			}
			filter.addExpression(Restrictions.eq(TruckEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			
			return result = getDaoProvider().getTruckEntityDao().count(filter);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public List fetchFilteredTruckUnavailabilities(List poolIds, List carrierIds, List truckTypes,
			List productType, Date day) {
		// [[
		List result = null;
		Object[][] params = {{"poolIds", poolIds},
							{"carrierIds", carrierIds},
							{"truckTypes", truckTypes},
							{"productType", productType},
							{"day", day}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchFilteredTruckUnavailabilities", params);
		}
		// ]]
		try {
			Filter filter = new FilterImpl();
			if (!isAll(poolIds)) {
				Filter poolFilter = new FilterImpl();
				poolFilter.addExpression(Restrictions.in(PoolTruckEntity.ATTR_POOL_ID, poolIds));
				JPAQuery jpaQuery = getDaoProvider().getPoolTruckEntityDao().createJPAQuery();
				jpaQuery.setFilter(poolFilter);
				jpaQuery.addSelect(SelectExpression.simpleSelect(PoolTruckEntity.ATTR_TRUCK));
				jpaQuery.setDistinct(true);
				List truckList = getDaoProvider().getPoolTruckEntityDao().find(jpaQuery);
				if (truckList.isEmpty()) {
					
					return new ArrayList();
				}
				filter.addExpression(Restrictions.in(TruckUnavailabilityEntity.ATTR_TRUCK, truckList));
			}
			
			Date morning = DateTimeUtils.getMorningOfDay(day);
			Date evening = DateTimeUtils.getMidnightOfDay(day);
			
			filter.addExpression(
					Restrictions.or(
						// 1 & 2
						Restrictions.and(
								Restrictions.le(TruckUnavailabilityEntity.ATTR_START_DAT, morning),
								Restrictions.ge(TruckUnavailabilityEntity.ATTR_END_DAT, morning)
						),
						// 3
						Restrictions.and(
								Restrictions.ge(TruckUnavailabilityEntity.ATTR_START_DAT, morning),
								Restrictions.le(TruckUnavailabilityEntity.ATTR_END_DAT, evening)
						),
						// 4 
						Restrictions.and(
								Restrictions.le(TruckUnavailabilityEntity.ATTR_START_DAT, evening),
								Restrictions.ge(TruckUnavailabilityEntity.ATTR_END_DAT, evening)
						)
					)
			);
			
			filter.addExpression(Restrictions.eq(TruckUnavailabilityEntity.ATTR_TRUCK + "." + TruckEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			if (!isAll(carrierIds)) {
				filter.addExpression(Restrictions.in(TruckUnavailabilityEntity.ATTR_TRUCK + "." + TruckEntity.ATTR_CARRIER_ID, carrierIds));
			}
			if (!isAll(truckTypes)) {
				filter.addExpression(Restrictions.in(TruckUnavailabilityEntity.ATTR_TRUCK + "." + TruckEntity.ATTR_TRUCK_TYPE_CD, truckTypes));
			}
			// -- product type
			if (!isAll(productType)) {
				if (productType.contains(Integer.valueOf(Constant.PRODUCT_TYPE_BAG))) {					
					filter.addExpression(Restrictions.eq(TruckUnavailabilityEntity.ATTR_TRUCK + "." + TruckEntity.ATTR_TRUCK_TRANSPORT_CAT_CD,
							Constant.TRUCK_TRANSPORT_CAT_CD_BAG));
				}
				/*
				 * TODO (marius Oct 10, 2012):
				 * Possibly implement here the logic of table 5 in the LRQ 185.0/185.1 SDS:
				 * Depending on the product type, filter only the applicable transport types.
				 */
				// marius/2012-10-10: Removed old filtering by isAgg and isCem flags.
				
				// TODO (marius Oct 10, 2012): Leaving old code here as indication how to implement the new filtering.
				// Remove when implemented. 
				
//				if (productType.contains(Integer.valueOf(Constant.PRODUCT_TYPE_BULK_LANDFILL))
//						|| productType.contains(Integer.valueOf(Constant.PRODUCT_TYPE_BULK_AGG))) {
//					filter.addExpression(Restrictions.eq(TruckUnavailabilityEntity.ATTR_TRUCK + "." + TruckEntity.ATTR_TRUCK_TYPE + "."
//							+ TruckTypeBsEntity.ATTR_TRUCK_TYPE_IS_AGG, Boolean.TRUE));
//				}
//				if (productType.contains(Integer.valueOf(Constant.PRODUCT_TYPE_BULK_CEM))) {
//					filter.addExpression(Restrictions.eq(TruckUnavailabilityEntity.ATTR_TRUCK + "." + TruckEntity.ATTR_TRUCK_TYPE + "."
//							+ TruckTypeBsEntity.ATTR_TRUCK_TYPE_IS_CEM, Boolean.TRUE));
//				}
			}
			filter.addExpression(Restrictions.eq(TruckUnavailabilityEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			result = getDaoProvider().getTruckUnavailabilityEntityDao().find(filter);
			
			if (result == null) {
				result = new ArrayList(); // return empty list
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
    

	/*
	 * TODO gergely cleanup/eliminate
	 */
	public List fetchFilteredNonAssignedOrders(List poolIds, List carrierIds, List truckTypeIds, String plantCd,
			List shippingPointIds, List productTypes, Date day) {
		// [[
		List result = null;
		Object[][] params = {{"poolIds", poolIds},
							{"carrierIds", carrierIds},
							{"truckTypeIds", truckTypeIds},
							{"plantCd", plantCd},
							{"shippingPointIds", shippingPointIds},
							{"productTypes", productTypes},
							{"day", day}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchFilteredNonAssignedOrders", params);
		}
		// ]]
		try {
			
			ReadOnlyDao<OrdChartNotAssignedEntity> noassignDao = getDaoProvider().getOrdChartNotAssignedEntityDao();
			
			Filter filter = new FilterImpl();
			
			Calendar yestCal = DateTimeUtils.getMorningOfDay(DateTimeUtils.getDateAsCalendar(day));
			yestCal.add(Calendar.HOUR, -24);
			Date yesterdayMorning = yestCal.getTime();
			
			Calendar tomCal = DateTimeUtils.getMidnightOfDay(DateTimeUtils.getDateAsCalendar(day));
			tomCal.add(Calendar.HOUR, 24);
			Date tomorrowEvening = tomCal.getTime();
			
			filter.addExpression(
					Restrictions.and(
								Restrictions.ge(OrdChartNotAssignedEntity.ATTR_DELIVERY_FROM_DAT, yesterdayMorning),
								Restrictions.le(OrdChartNotAssignedEntity.ATTR_DELIVERY_TO_DAT, tomorrowEvening)
					)
			);
			
			if (!isAll(poolIds)) {
				filter.addExpression(Restrictions.in(OrdChartNotAssignedEntity.ATTR_POOL_ID, poolIds));
			}
			if (!isAll(carrierIds)) {
				filter.addExpression(Restrictions.in(OrdChartNotAssignedEntity.ATTR_EXEC_CARRIER_ID, carrierIds));
			}
			if (!isAll(plantCd)) {
				filter.addExpression(Restrictions.eq(OrdChartNotAssignedEntity.ATTR_PLANT_CD, plantCd));
			}
			if (!isAll(shippingPointIds)) {
				filter.addExpression(Restrictions.in(OrdChartNotAssignedEntity.ATTR_SHIPPINGPOINT_ID, shippingPointIds));
			}
			if (!isAll(productTypes)) {
				addProductTypeMaterialFilterRestr(filter, productTypes, OrdChartNotAssignedEntity.ATTR_MATERIAL_PACKAGE_CD,
						OrdChartNotAssignedEntity.ATTR_MATERIAL_IS_LF, OrdChartNotAssignedEntity.ATTR_MATERIAL_IS_AGG,
						OrdChartNotAssignedEntity.ATTR_MATERIAL_IS_CEM);
			}
			if (!isAll(truckTypeIds)) {
				filter.addExpression(Restrictions.in(OrdChartNotAssignedEntity.ATTR_TRUCK_TYPE_CD, truckTypeIds));
			}
			result = noassignDao.find(filter);
			
			if (result == null) {
				result = Collections.<OrdChartNotAssignedEntity>emptyList(); // return empty list
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
	 * 
	 * @param tarnsCat
	 * @param poolIds
	 * @param carrierIds
	 * @param truckTypeIds
	 * @param deliveryTypes
	 * @param plantCd
	 * @param shippingPointIds
	 * @param productTypes
	 * @param day
	 * @return
	 */
	public List fetchFilteredNonAssignedLoadings(int tarnsCat, List poolIds, List carrierIds, List truckTypeIds,
			List deliveryTypes, String plantCd, List shippingPointIds, List productTypes, Date day) {
		// [[
		List result = null;
		Object[][] params = {{"tarnsCat", tarnsCat + ""},
							{"poolIds", poolIds},
							{"carrierIds", carrierIds},
							{"truckTypeIds", truckTypeIds},
							{"deliveryTypes", deliveryTypes},
							{"plantCd", plantCd},
							{"shippingPointIds", shippingPointIds},
							{"productTypes", productTypes},
							{"day", day}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchFilteredNonAssignedLoadings", params);
		}
		// ]]
		try {			
			ReadOnlyDao<ChartLoadUnscheduledEntity> noassignDao = getDaoProvider().getChartLoadUnscheduledEntityDao();
			
			Filter filter = new FilterImpl();
			
			Calendar yestCal = DateTimeUtils.getMorningOfDay(DateTimeUtils.getDateAsCalendar(day));
			yestCal.add(Calendar.HOUR, -24);
			Date yesterdayMorning = yestCal.getTime();
			
			Calendar tomCal = DateTimeUtils.getMidnightOfDay(DateTimeUtils.getDateAsCalendar(day));
			tomCal.add(Calendar.HOUR, 24);
			Date tomorrowEvening = tomCal.getTime();
			
			filter.addExpression(
					Restrictions.and(
							Restrictions.ge(ChartLoadUnscheduledEntity.ATTR_DELIVERY_FROM_DAT, yesterdayMorning),
							Restrictions.le(ChartLoadUnscheduledEntity.ATTR_DELIVERY_TO_DAT, tomorrowEvening)
							)
					);
			
			if (!isAll(poolIds)) {
				filter.addExpression(Restrictions.in(ChartLoadUnscheduledEntity.ATTR_POOL_ID, poolIds));
			}
			if (!isAll(carrierIds)) {
				filter.addExpression(Restrictions.in(ChartLoadUnscheduledEntity.ATTR_EXEC_CARRIER_ID, carrierIds));
			}
			if (!isAll(plantCd)) {
				filter.addExpression(Restrictions.eq(ChartLoadUnscheduledEntity.ATTR_PLANT_CD, plantCd));				
			}
			if (!isAll(shippingPointIds)) {
				filter.addExpression(Restrictions.in(ChartLoadUnscheduledEntity.ATTR_SHIPPINGPOINT_ID, shippingPointIds));				
			}
			if (!isAll(productTypes)) {
				addProductTypeMaterialFilterRestr(filter, productTypes, ChartLoadUnscheduledEntity.ATTR_MATERIAL_PACKAGE_CD,
						ChartLoadUnscheduledEntity.ATTR_MATERIAL_IS_LF, ChartLoadUnscheduledEntity.ATTR_MATERIAL_IS_AGG,
						ChartLoadUnscheduledEntity.ATTR_MATERIAL_IS_CEM);
			}
			if (!isAll(truckTypeIds)) {
				filter.addExpression(Restrictions.in(ChartLoadUnscheduledEntity.ATTR_TRUCK_TYPE_CD, truckTypeIds));
			}
			if (!isAll(deliveryTypes)
					&& !(deliveryTypes.contains(Constant.CHART_DELIVERY_TYPE_PICKUP) && deliveryTypes.contains(Constant.CHART_DELIVERY_TYPE_DELIVERED))) {
				if (deliveryTypes.contains(Constant.CHART_DELIVERY_TYPE_PICKUP)) {
					filter.addExpression(Restrictions.eq(ChartLoadUnscheduledEntity.ATTR_DELIVERY_TYPE_CD, Constant.DELIVERY_TYPE_PICKUP));					
				}
				else {
					filter.addExpression(Restrictions.neq(ChartLoadUnscheduledEntity.ATTR_DELIVERY_TYPE_CD, Constant.DELIVERY_TYPE_PICKUP));										
				}
			}
			
			List<Long> tmpDeliveryTypes = new ArrayList<Long>();
			if (Constant.CHART_TRANS_CAT_RAIL == tarnsCat) {
				tmpDeliveryTypes.add(Long.valueOf(Constant.DELIVERY_TYPE_BIMODAL_RAIL));
				tmpDeliveryTypes.add(Long.valueOf(Constant.DELIVERY_TYPE_RAIL_DIRECT));
			}
			else {
				tmpDeliveryTypes.add(Long.valueOf(Constant.DELIVERY_TYPE_ROAD_DIRECT));
				tmpDeliveryTypes.add(Long.valueOf(Constant.DELIVERY_TYPE_BIMODAL_ROAD));
				tmpDeliveryTypes.add(Long.valueOf(Constant.DELIVERY_TYPE_PICKUP));
			}
			filter.addExpression(Restrictions.in(ChartLoadUnscheduledEntity.ATTR_DELIVERY_TYPE_CD, tmpDeliveryTypes));									
			
			result = noassignDao.find(filter);
			if (result == null) {
				result = new ArrayList(); // return empty list
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
	 * 
	 * @param tarnsCat
	 * @param poolIds
	 * @param carrierIds
	 * @param truckTypeIds
	 * @param deliveryTypes
	 * @param plantCd
	 * @param shippingPointIds
	 * @param productTypes
	 * @param day
	 * @return
	 */
	public List fetchFilteredScheduledLoadings(int tarnsCat, List poolIds, List carrierIds, List truckTypes,
			List deliveryTypes, String plantCd, List shippingPointIds, List productTypes, Date day) {
		// [[
		List result = null;
		Object[][] params = {{"tarnsCat", tarnsCat + ""},
							{"poolIds", poolIds},
							{"carrierIds", carrierIds},
							{"truckTypes", truckTypes},
							{"deliveryTypes", deliveryTypes},
							{"plantCd", plantCd},
							{"shippingPointIds", shippingPointIds},
							{"productTypes", productTypes},
							{"day", day}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchFilteredScheduledLoadings", params);
		}
		// ]]
		try {			
			ReadOnlyDao<ChartLoadScheduledEntity> noassignDao = getDaoProvider().getChartLoadScheduledEntityDao();
			
			Filter filter = new FilterImpl();
			
			Calendar yestCal = DateTimeUtils.getMorningOfDay(DateTimeUtils.getDateAsCalendar(day));
			yestCal.add(Calendar.HOUR, -24);
			Date yesterdayMorning = yestCal.getTime();
			
			Calendar tomCal = DateTimeUtils.getMidnightOfDay(DateTimeUtils.getDateAsCalendar(day));
			tomCal.add(Calendar.HOUR, 24);
			Date tomorrowEvening = tomCal.getTime();
			
			filter.addExpression(
					Restrictions.and(
							Restrictions.ge(ChartLoadScheduledEntity.ATTR_FIRST_LOAD_DAT, yesterdayMorning),
							Restrictions.le(ChartLoadScheduledEntity.ATTR_FIRST_TO_DAT, tomorrowEvening)
							)
					);
			
			if (!isAll(poolIds)) {
				filter.addExpression(Restrictions.in(ChartLoadScheduledEntity.ATTR_POOL_ID, poolIds));
			}
			if (!isAll(carrierIds)) {
				filter.addExpression(Restrictions.in(ChartLoadScheduledEntity.ATTR_CARRIER_ID, carrierIds));
			}
			if (!isAll(plantCd)) {
				filter.addExpression(Restrictions.eq(ChartLoadScheduledEntity.ATTR_PLANT_CD, plantCd));				
			}
			if (!isAll(shippingPointIds)) {
				filter.addExpression(Restrictions.in(ChartLoadScheduledEntity.ATTR_SHIPPINGPOINT_ID, shippingPointIds));				
			}
			if (!isAll(productTypes)) {
				addProductTypeMaterialFilterRestr(filter, productTypes, ChartLoadScheduledEntity.ATTR_MATERIAL_PACKAGE_CD,
						ChartLoadScheduledEntity.ATTR_MATERIAL_IS_LF, ChartLoadScheduledEntity.ATTR_MATERIAL_IS_AGG,
						ChartLoadScheduledEntity.ATTR_MATERIAL_IS_CEM);
			}
			if (!isAll(truckTypes)) {
				filter.addExpression(Restrictions.in(ChartLoadScheduledEntity.ATTR_TRUCK_TYPE_CD, truckTypes));
			}
			if (!isAll(deliveryTypes)
					&& !(deliveryTypes.contains(Constant.CHART_DELIVERY_TYPE_PICKUP) && deliveryTypes.contains(Constant.CHART_DELIVERY_TYPE_DELIVERED))) {
				if (deliveryTypes.contains(Constant.CHART_DELIVERY_TYPE_PICKUP)) {
					filter.addExpression(Restrictions.eq(ChartLoadScheduledEntity.ATTR_DELIVERY_TYPE_CD, Constant.DELIVERY_TYPE_PICKUP));					
				}
				else {
					filter.addExpression(Restrictions.neq(ChartLoadScheduledEntity.ATTR_DELIVERY_TYPE_CD, Constant.DELIVERY_TYPE_PICKUP));										
				}
			}
			
			List<Long> tmpDeliveryTypes = new ArrayList<Long>();
			if (Constant.CHART_TRANS_CAT_RAIL == tarnsCat) {
				tmpDeliveryTypes.add(Long.valueOf(Constant.DELIVERY_TYPE_BIMODAL_RAIL));
				tmpDeliveryTypes.add(Long.valueOf(Constant.DELIVERY_TYPE_RAIL_DIRECT));
			}
			else {
				tmpDeliveryTypes.add(Long.valueOf(Constant.DELIVERY_TYPE_ROAD_DIRECT));
				tmpDeliveryTypes.add(Long.valueOf(Constant.DELIVERY_TYPE_BIMODAL_ROAD));
				tmpDeliveryTypes.add(Long.valueOf(Constant.DELIVERY_TYPE_PICKUP));
			}
			filter.addExpression(Restrictions.in(ChartLoadScheduledEntity.ATTR_DELIVERY_TYPE_CD, tmpDeliveryTypes));									
			
			result = noassignDao.find(filter);
			if (result == null) {
				result = new ArrayList(); // return empty list
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

    
    public List<TruckChartAssignedEntity> fetchAssignedTrucks(List poolIds, List carrierIds, List truckTypeIds, String plantCd,
			List shippingPointIds, List productTypes, Date day) {
		// [[
		List<TruckChartAssignedEntity> result = null;
		Object[][] params = {{"poolIds", poolIds},
							{"carrierIds", carrierIds},
							{"truckTypeIds", truckTypeIds},
							{"plantCd", plantCd},
							{"shippingPointIds", shippingPointIds},
							{"productTypes", productTypes},
							{"day", day}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAssignedTrucks", params);
		}
		// ]]
		try {			
			ReadOnlyDao<TruckChartAssignedEntity> assignDao = getDaoProvider().getTruckChartAssignedEntityDao();
			
			Filter filter = new FilterImpl();
			
			Calendar yestCal = DateTimeUtils.getMorningOfDay(DateTimeUtils.getDateAsCalendar(day));
			yestCal.add(Calendar.HOUR, -24);
			Date yesterdayMorning = yestCal.getTime();
			
			Calendar tomCal = DateTimeUtils.getMidnightOfDay(DateTimeUtils.getDateAsCalendar(day));
			tomCal.add(Calendar.HOUR, 24);
			Date tomorrowEvening = tomCal.getTime();
			
			filter.addExpression(
					Restrictions.and(
								Restrictions.ge(TruckChartAssignedEntity.ATTR_ASSIGNMENT_DAT, yesterdayMorning),
								Restrictions.le(TruckChartAssignedEntity.ATTR_ASSIGNMENT_DAT, tomorrowEvening)
					)
			);
			
			if (!isAll(poolIds)) {
				filter.addExpression(Restrictions.in(TruckChartAssignedEntity.ATTR_POOL_ID, poolIds));
			}
			if (!isAll(carrierIds)) {
				filter.addExpression(Restrictions.in(TruckChartAssignedEntity.ATTR_CARRIER_ID, carrierIds));
			}
			if (!isAll(plantCd)) {
				filter.addExpression(Restrictions.eq(TruckChartAssignedEntity.ATTR_PLANT_CD, plantCd));				
			}
			if (!isAll(shippingPointIds)) {
				filter.addExpression(Restrictions.in(TruckChartAssignedEntity.ATTR_SHIPPINGPOINT_ID, shippingPointIds));				
			}
			if (!isAll(productTypes)) {
				addProductTypeMaterialFilterRestr(filter, productTypes, TruckChartAssignedEntity.ATTR_MATERIAL_PACKAGE_CD,
						TruckChartAssignedEntity.ATTR_MATERIAL_IS_LF, TruckChartAssignedEntity.ATTR_MATERIAL_IS_AGG,
						TruckChartAssignedEntity.ATTR_MATERIAL_IS_CEM);
			}
			if (!isAll(truckTypeIds)) {
				filter.addExpression(Restrictions.in(TruckChartAssignedEntity.ATTR_TRUCK_TYPE_CD, truckTypeIds));				
			}
			result = assignDao.find(filter);
			if (result == null) {
				result = new ArrayList(); // return empty list
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
    
    public List fetchShiptoWagonTypeList(Long sapBpId, Locale locale) {
		// [[
		List result = null;
		Object[][] params = {{"sapBpId", sapBpId},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchShiptoWagonTypeList", params);
		}
		// ]]
		try {
			ReadOnlyDao<DispoWagonTypeListEntity> wTypeDao = getDaoProvider().getDispoWagonTypeListEntityDao();
			
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(DispoWagonTypeListEntity.ATTR_SHIPTO_SAP_BP_ID, sapBpId));
			filter.addExpression(Restrictions.eq(DispoWagonTypeListEntity.ATTR_LANG_CD, locale.getLanguage()));
			filter.addExpression(Order.asc(DispoWagonTypeListEntity.ATTR_EVALUATION_PRIORITY));
			
			return result = wTypeDao.find(filter);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }  
    
    public List fetchNonAssignedRailOrders(Long poolId, Long carrierId, Long priority, Long wagonCat, Long wagonType, Date day) {
		// [[
		List result = null;
		Object[][] params = {{"poolId", poolId},
							{"carrierId", carrierId},
							{"priority", priority},
							{"wagonCat", wagonCat},
							{"wagonType", wagonType},
							{"day", day}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchNonAssignedRailOrders", params);
		}
		// ]]
		try {
			
			ReadOnlyDao<OrdChartNotAssignedEntity> noassignDao = getDaoProvider().getOrdChartNotAssignedEntityDao();
			
			Filter filter = new FilterImpl();
			
			if (!isAll(poolId)) {
				filter.addExpression(Restrictions.eq(OrdChartNotAssignedEntity.ATTR_POOL_ID, poolId));
			}
			if (!isAll(carrierId)) {
				filter.addExpression(Restrictions.eq(OrdChartNotAssignedEntity.ATTR_EXEC_CARRIER_ID, carrierId));
			}
			
			return result = noassignDao.find(filter);
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }
    
    public List fetchAssignedRailOrders(Long poolId, Long carrierId, Long priority, Long wagonCat, Long wagonType, Date day) {
		// [[
		List result = null;
		Object[][] params = {{"poolId", poolId},
							{"carrierId", carrierId},
							{"priority", priority},
							{"wagonCat", wagonCat},
							{"wagonType", wagonType},
							{"day", day}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAssignedRailOrders", params);
		}
		// ]]
		try {
			
			ReadOnlyDao<OrdChartNotAssignedEntity> noassignDao = getDaoProvider().getOrdChartNotAssignedEntityDao();
			
			Filter filter = new FilterImpl();
			
			if (!isAll(poolId)) {
				filter.addExpression(Restrictions.eq(OrdChartNotAssignedEntity.ATTR_POOL_ID, poolId));
			}
			if (!isAll(carrierId)) {
				filter.addExpression(Restrictions.eq(OrdChartNotAssignedEntity.ATTR_COMM_CARRIER_ID, carrierId));
			}
			
			return result = noassignDao.find(filter);
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }
    
    private boolean isAll(Long val) {
    	return (val == null || val.equals(-1L));     	
    }
    private boolean isAll(String val) {
    	return (StringUtils.empty(val) || val.equals("-1"));     	
    }
    private boolean isAll(List val) {
    	return (val == null || val.isEmpty());     	
    }

    private Long getDef(Long val) {
    	return val == null ? -1L : val; 
    }
    
	 /**
     * Special query, which is needed for rail chart. The rail chart is visible for one specific day (railChartDate).
     * The returned list contains as much entries as much timeSlice is in a day. For example if the timeSliceLength is 5, it will return
     * (60/5)*24 entries.
     * The paramteres returned are count of certain groups, which should be visible in different colors on the rail chart.
     * 
	 * @param railChartDate
	 * @param railwayStationId
	 * @return
	 */
	public List getRailChartStatistics(Date railChartDate, Set railwayStationIds, List wagonTypeCds, List wagonTrspCatCds) {
		// [[
		List result = null;
		Object[][] params = {{"railChartDate", railChartDate},
							{"railwayStationIds", railwayStationIds},
							{"wagonTypeCds", wagonTypeCds},
							{"wagonTrspCatCds", wagonTrspCatCds}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getRailChartStatistics", params);
		}
		// ]]
		try {

			List arrguments = new ArrayList();
			Date date = DateTimeUtils.getMorningOfDay(railChartDate);
			arrguments.add(date);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.add(Calendar.DAY_OF_YEAR, 1);
			Date d2 = c.getTime();
			Integer timeSliceLength = new Integer(5);
			arrguments.add(DateTimeUtils.getMorningOfDay(d2));
			
			final int startIdxRailStationIds = arrguments.size() + 1;
			arrguments.addAll(railwayStationIds);
			String railStationIds = generatePrecompiletSQLScript(startIdxRailStationIds, railwayStationIds);
			
			StringBuilder whereCond = new StringBuilder(
					" WHERE ((last_known_railway_station_id in "
							+ railStationIds
							+ " and arrival_last_known_station_dat <= ?2) OR (return_railway_station_id in "
							+ railStationIds + " and planned_start<=?2)) AND active_flag <> 0 ");
			if (!isAll(wagonTypeCds) ) {	
				final int startIdxWagonTypeCds = arrguments.size() + 1;
				String wagonTypeCdsStr = generatePrecompiletSQLScript(startIdxWagonTypeCds, wagonTypeCds);
				whereCond.append(" AND wagon_Type_Cd in " + wagonTypeCdsStr);
				arrguments.addAll(wagonTypeCds);
			}
			if ( !isAll(wagonTrspCatCds) ) {
				final int startIdxwagonTrspCatCds = arrguments.size() + 1;
				String wagonTrspCatCdsStr = generatePrecompiletSQLScript(startIdxwagonTrspCatCds, wagonTrspCatCds);
				whereCond.append(" AND wagon_Trsp_Cat_Cd in " + wagonTrspCatCdsStr);
				arrguments.addAll(wagonTrspCatCds);
			}
			
			Object[] argumentsArray = arrguments.toArray();
            List list = getDaoProvider().getDirectJpaDao().findNative(
                    " SELECT start_date, end_date,  gr, COUNT(*)	FROM ("+
                      " SELECT start_date, end_date," +
                      " CASE" +
                      " WHEN(last_known_railway_station_id in " + railStationIds + " AND arrival_last_known_station_dat <= ?2 AND outgoing_planned_start >= ?1 AND(outgoing_planned_start >= end_date)) THEN 'outgoing'"+
                       " WHEN(return_railway_station_id in " + railStationIds + " AND planned_start <= ?2 AND (planned_start <= start_date)) THEN 'incoming' WHEN (last_known_railway_station_id in " + railStationIds + " AND arrival_last_known_station_dat <= ?2 AND planned_start IS NULL AND outgoing_planned_start IS NULL AND(load_id IS NULL OR loading_ts >= end_date)) THEN 'not loaded'"+
                       " WHEN(last_known_railway_station_id in " + railStationIds + " AND arrival_last_known_station_dat <= ?2 AND planned_start IS NULL AND outgoing_planned_start IS NULL AND(load_id IS NOT NULL AND loading_ts <= end_date)) THEN 'loaded' " +
                       " END gr FROM " + Constant.SCHEMA_USER_PREPEND + RailChartEntity.TABLE_NAME + ", (SELECT LEVEL AS min_group,(?1 +(LEVEL -1) *" + timeSliceLength + " /(24 *60)) AS start_date, (?1 + " + timeSliceLength + " *(LEVEL)/(24 *60)) AS end_date" +
                       " FROM dual CONNECT BY LEVEL <=(60 / " + timeSliceLength + ") *24) " + whereCond + " ) WHERE gr IS NOT NULL " +
                       "GROUP BY start_date,  end_date,  gr ORDER BY start_date,  end_date, gr", argumentsArray);
                    return result = list;
		}
        catch (Exception e) {
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
	 * 
	 * @param startIndex
	 * @param railStationIds
	 * @return
	 */
	private String generatePrecompiletSQLScript(int startIndex, Collection<Long> railStationIds) {
		// [[
		String result = null;
		Object[][] params = {{"startIndex", startIndex + ""},
							{"railStationIds", railStationIds}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "generatePrecompiletSQLScript", params);
		}
		// ]]
		try {
			if (railStationIds == null || railStationIds.isEmpty()) {
				throw new IllegalArgumentException("The list os rail station ids cannot be null or empty!");
			}
			
			Set<Long> railStationIdsSet = new HashSet<Long>(railStationIds);
			
			StringBuilder builder = new StringBuilder("(");
			
			for (Iterator<Long> iter = railStationIdsSet.iterator(); iter.hasNext();) {
				iter.next();
				builder.append("?" + startIndex ++);
				if (iter.hasNext()) {
					builder.append(", ");
				}
			}
			builder.append(")");
			
			return result = builder.toString();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
    
	private void addProductTypeMaterialFilterRestr(Filter filter, List productTypes, String colNamePckCd, String colNameIsLf,
			String colNameIsAgg, String colNameIsCem) {
		// [[
		Object[][] params = {{"filter", filter},
							{"productTypes", productTypes},
							{"colNamePckCd", colNamePckCd},
							{"colNameIsLf", colNameIsLf},
							{"colNameIsAgg", colNameIsAgg},
							{"colNameIsCem", colNameIsCem}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "addProductTypeMaterialFilterRestr", params);
		}
		// ]]
		try {
			
			if (productTypes.contains(Integer.valueOf(Constant.PRODUCT_TYPE_BAG))) {
				filter.addExpression(Restrictions.eq(colNamePckCd,
						Constant.MATERIAL_PACKAGE_BAG));
			}
			
			boolean hasBulkLandfill = productTypes.contains(Integer.valueOf(Constant.PRODUCT_TYPE_BULK_LANDFILL));
			boolean hasBulkAgg = productTypes.contains(Integer.valueOf(Constant.PRODUCT_TYPE_BULK_AGG));
			boolean hasBulkCem = productTypes.contains(Integer.valueOf(Constant.PRODUCT_TYPE_BULK_CEM));
			
			// note the if statement order is important 
			List<Expression> exprList = new ArrayList<Expression>();
			if (hasBulkLandfill) {
				exprList.add(Restrictions.eq(colNameIsLf, Boolean.TRUE));
			}
			if (hasBulkAgg) {
				exprList.add(Restrictions.eq(colNameIsAgg, Boolean.TRUE));
			}
			if (hasBulkCem) {
				exprList.add(Restrictions.eq(colNameIsCem, Boolean.TRUE));
			}
			if (!exprList.isEmpty()) {
				filter.addExpression(Restrictions.or(exprList));
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }
	
	/**
	 * 
	 */
	public List getPlantByCompanycodeCd(String companycodeCd) {
		// [[
		List result = null;
		Object[][] params = {{"companycodeCd", companycodeCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getPlantByCompanycodeCd", params);
		}
		// ]]
		try {
			ReadOnlyDao<PlantSareaCompcodeEntity> dao = getDaoProvider().getPlantSareaCompcodeEntityDao();
			
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(PlantSareaCompcodeEntity.ATTR_COMPANYCODE_CD, companycodeCd));
			filter.addExpression(Order.asc(PlantSareaCompcodeEntity.ATTR_PLANT_NAME));
			
			return result = dao.find(filter);
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
