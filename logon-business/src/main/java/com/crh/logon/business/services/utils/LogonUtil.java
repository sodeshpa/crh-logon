/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.jacc.PolicyContext;
import javax.servlet.http.HttpServletRequest;

import com.crh.logon.business.services.directjpa.JpaQueryData;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.SplittedOrderData.DeliveryType;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.PackageTypeTrspCatMapping;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.CustorderItemEntity;
import com.crh.logon.persistence.entities.common.DriverBreakEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.PersonEntity;
import com.crh.logon.persistence.entities.common.RailTimetableEntity;
import com.crh.logon.persistence.entities.common.RailwayStationCommEntity;
import com.crh.logon.persistence.entities.common.RailwayStationEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.SimpleVehicleEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.refdata.CurrencyBsEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.MopStatusTxEntity;
import com.crh.logon.persistence.entities.refdata.SapVehicleCharBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.CurrentExchangeRateEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.time.DateTimeUtils;

/**
 * @author AdNovum Informatik AG
 */
public class LogonUtil {
	
    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(LogonUtil.class);
    
    private static final List<String> clientIpRelevantHeaders = new ArrayList<String>();
    static {
    	clientIpRelevantHeaders.add("X-Forwarded-For");
    	clientIpRelevantHeaders.add("Proxy-Client-IP");
    	clientIpRelevantHeaders.add("WL-Proxy-Client-IP");
    	clientIpRelevantHeaders.add("HTTP_CLIENT_IP");
    	clientIpRelevantHeaders.add("HTTP_X_FORWARDED_FOR");
    	clientIpRelevantHeaders.add("HTTP_X_CLUSTER_CLIENT_IP");
    }

    /**
	 * This value (0) is returned when the compared {@link UnallocatedOrderComparisonData}s have the same sorting order.
	 */
	public static final int SORTING_ORDER_IS_EQUAL = 0;

	/**
	 * {@link nullSafeCompare} returns this value, if value1 < value2
	 */
	public static int SORTING_ORDER_FIRST_VALUE_IS_THE_LESSER = -1;

	/**
	 * {@link nullSafeCompare} returns this value, if value1 > value2
	 */
	public static int SORTING_ORDER_FIRST_VALUE_IS_THE_GREATER = 1;
    
	public static Long[] createNominatorDenumerator(BigDecimal value, int scale) {
		// [[
		Long[] result = null;
		Object[][] params = {{"value", value},
							{"scale", scale + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonUtil.class, "createNominatorDenumerator", params);
		}
		// ]]
		try {
			BigDecimal tmp = BigDecimal.TEN;
			tmp = tmp.pow(scale);
			
			value = value.multiply(tmp);
			value = value.divideToIntegralValue(BigDecimal.ONE);
			Long numerator = value.longValue();
			Long denominator = tmp.longValue();
			
			Long gcd = LogonUtil.findGCD(numerator, denominator);
			if (gcd != null) {
				numerator = numerator / gcd;
				denominator = denominator / gcd;
			}
			
			result = new Long[]{numerator, denominator};
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
	 * Finds GCD (Greatest Common Divisor) of two numbers.
	 * GCD could be used for simplify fractions.
	 * @param inputA
	 * @param inputB
	 * @return the greatest common divisor (positive natural number) of inputA and inputB, or null if one of the inputs is null
	 */
	public static Long findGCD(final Long inputA, final Long inputB) {
		if (inputA == null || inputB == null) {
			LOG.error("inputA and/or inputB is null");
			return null;
		}

		BigInteger bigA = new BigInteger(inputA.toString());
		BigInteger bigB = new BigInteger(inputB.toString());
		return bigA.gcd(bigB).longValue();
	}
	
    public static boolean validSmsNumber(String smsNumber) {
        if (StringUtils.isNotEmpty(smsNumber) ) {
            return smsNumber.replaceAll("[\\(\\) ]", "").matches("[+]{0,1}[0-9]+([,;][+]{0,1}[0-9]+)*");
        }
        return true;
    }
    
    public static boolean validFaxNumber(String faxNumber) {
        return validSmsNumber(faxNumber);
    }
    
    public static boolean validEmail(String email) {
        if (StringUtils.isNotEmpty(email)) {
            Pattern p = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}(( )?,( )?[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4})*$", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(email);
            return m.matches();
        }
        return true;
    }
    
    
    /*
     *  TODO gergely -> quick fix: this method has been removed though still referenced from WebOrderAjaxService 
     */
    public static Calendar getNextTrainArr(RailTimetableEntity timetableEntity) {

    	Calendar now = Calendar.getInstance();
    	Calendar nextTrainDep = DateTimeUtils.getCalendarFromMinAfterMidnight(timetableEntity.getDepSwitchingTimeMin());
    	Calendar nextTrainArr = DateTimeUtils.getCalendarFromMinAfterMidnight(timetableEntity.getArrSwitchingTimeMin());

    	// --- If next train is only tomorrow ---
    	if (nextTrainDep.before(now)) {
    		nextTrainDep.add(Calendar.DAY_OF_YEAR, 1);
    		nextTrainArr.add(Calendar.DAY_OF_YEAR, 1);
    	}

    	// --- If departure and arrival is not on the same day --- 
    	nextTrainArr.add(Calendar.DAY_OF_YEAR, timetableEntity.getArrOffsetDays().intValue());

    	// --- Weekend ---
    	if (nextTrainArr.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
    		nextTrainArr.add(Calendar.DAY_OF_YEAR, 2);
    	}
    	else if (nextTrainArr.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
    		nextTrainArr.add(Calendar.DAY_OF_YEAR, 1);
    	}

    	return nextTrainArr;
	}
    
    /* TODO gergely elimiate this
    public static Calendar getActualTrainArr(RailTimetableEntity timetableEntity, Date deliveryToDat) {
    	Calendar result;
    	Calendar now = Calendar.getInstance();
    	Calendar nextTrainDep = DateTimeUtils.getCalendarFromMinAfterMidnight(timetableEntity.getDepSwitchingTimeMin());
    	Calendar actualTrainArr = DateTimeUtils.getCalendarFromMinAfterMidnight(timetableEntity.getArrSwitchingTimeMin());

    	// --- If next train is only tomorrow ---
    	if (nextTrainDep.before(now)) {
    		nextTrainDep.add(Calendar.DAY_OF_YEAR, 1);
    		actualTrainArr.add(Calendar.DAY_OF_YEAR, 1);
    	}

    	// --- If departure and arrival is not on the same day --- 
    	actualTrainArr.add(Calendar.DAY_OF_YEAR, timetableEntity.getArrOffsetDays().intValue());

    	// --- Weekend ---
    	if (actualTrainArr.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
    		actualTrainArr.add(Calendar.DAY_OF_YEAR, 2);
    	}
    	else if (actualTrainArr.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
    		actualTrainArr.add(Calendar.DAY_OF_YEAR, 1);
    	}

    	return result = actualTrainArr;
	}
	*/
	
	/**
	 * Sums the tare weights of a composition's members passed. Returns the sum of the tare weights.
	 * @param trailer
	 * @param container
	 * @param truck
	 * @param wagon
	 * @return
	 */
	public static BigDecimal sumTareWeights(TrailerEntity trailer, ContainerEntity container,
			TruckEntity truck, WagonEntity wagon, SimpleVehicleEntity simpleVehicle) {
		BigDecimal tareQuantity;
		if (truck != null){
			tareQuantity = truck.getTareWeightT();
		}
		else if (wagon != null) {
			tareQuantity = wagon.getTareWeightT();
		}
		else if (simpleVehicle != null) {
			tareQuantity = simpleVehicle.getTareWeightT();
		}
		else {
			throw new RuntimeException("You must specify either a truck, wagon, or simple vehicle.");
		}
		if ( trailer != null && trailer.getTareWeightT() != null){
			BigDecimal truckTrailerMeasure = tareQuantity.add(trailer.getTareWeightT());
			tareQuantity = truckTrailerMeasure;
		}
		if ( container != null && container.getTareWeightT() != null){
			BigDecimal truckContainerMeasure = tareQuantity.add(container.getTareWeightT());
			tareQuantity = truckContainerMeasure;
		}
		return tareQuantity;
	}
	
	/**
	 * Returns the commercial railway station for the given railwaystation entity.
	 * WARNING. If there is no commercial railway station for it, it is considered to
	 * be the commercial rs.
	 * @param railwayStation
	 * @return
	 */
	public static RailwayStationEntity getCommercialRailwayStationEntity (
			RailwayStationEntity railwayStation, ReadWriteDao<RailwayStationCommEntity> commDao) {
		
		final Filter<RailwayStationCommEntity> commFilter = commDao.createFilter();
		commFilter.addExpression(Restrictions.eq(RailwayStationCommEntity.ATTR_OP_RAILWAY_STATION_ID, railwayStation.getRailwayStationId()));
		
		final RailwayStationCommEntity commEnt = commDao.findSingle(commFilter);

		final RailwayStationEntity commRailwayStation;
		if (commEnt != null) {
			commRailwayStation = commEnt.getCommRailwayStation();
		}
		else {
			commRailwayStation = railwayStation;
		}
		return commRailwayStation;
	}
	
	private static String[] returnDistributionDestCds = null;
	public static boolean isReturnDistributionDest(String distributionDestCd) {
	
		if (returnDistributionDestCds == null) {
			returnDistributionDestCds = new String[]
			{ 	
				Constant.DISTRIBUTION_DEST_OUTBOUND_RETURN,
				Constant.DISTRIBUTION_DEST_INTERUNIT_DIST_RETURN,
				Constant.DISTRIBUTION_DEST_INTERUNIT_PROD_RETURN,
				Constant.DISTRIBUTION_DEST_INBOUND_RETURN
			};
			Arrays.sort(returnDistributionDestCds);
		}
		
		return distributionDestCd != null && Arrays.binarySearch(returnDistributionDestCds, distributionDestCd) >= 0;		
	}
	
	public static boolean isRepairDistributionDest(String distributionDestCd) {
		return Constant.DISTRIBUTION_DEST_REPAIR.equals(distributionDestCd);
	}

	/**
	 * @param from
	 * @param to
	 * @param breaks
	 * @return
	 */
	public static long getBreakLengthForAssignmentInfo(Date from, Date to, List<DriverBreakEntity> breaks) {
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(from);
//		Calendar calTo = Calendar.getInstance();
//		calTo.setTime(to);
//		Calendar calFromNoon = Calendar.getInstance();
//		calFromNoon.setTime(from);
//		calFromNoon.set(Calendar.HOUR, 12);
//		calFromNoon.set(Calendar.MINUTE, 0);
//		calFromNoon.set(Calendar.SECOND,0);
//		calFromNoon.set(Calendar.MILLISECOND,0);
//		
//		Calendar calFromOne = Calendar.getInstance();
//		calFromOne.setTime(from);
//		calFromOne.set(Calendar.HOUR, 13);
//		calFromOne.set(Calendar.MINUTE, 0);
//		calFromOne.set(Calendar.SECOND,0);
//		calFromOne.set(Calendar.MILLISECOND,0);
//		
//		long breakLength = 0l;
//		if (cal.after(calFromNoon) && cal.before(calFromOne) ||  calTo.after(calFromNoon) && calTo.before(calFromOne) ) {
//			//suppose that the driver will have lunch break between 12-13
//			long millis = calFromOne.getTimeInMillis() - from.getTime();
//			breakLength += millis;
//		}

		long breakLength = 0l;
		for (DriverBreakEntity driverBreak : breaks) {
			if (from.after(driverBreak.getStartDat()) && from.before(driverBreak.getEndDat())) {
				//from is between the break, add the break end - from difference
				long millis = driverBreak.getEndDat().getTime() - from.getTime();
				breakLength += millis;
			}
			else if (to.after(driverBreak.getStartDat()) && to.before(driverBreak.getEndDat())) {
				//add the whole break
				long millis = driverBreak.getEndDat().getTime() -driverBreak.getStartDat().getTime();
				breakLength += millis;
			}
		}
		return breakLength;
	}
	
	/**
	 * Finds the bulk order item in a collection of order items
	 * @param orderItems A collection of order items, for example all assigned to the same order or load
	 * @return The bulk order item in the collection or null if none found
	 * @throws LogonException if multiple bulk order items are found in the collection
	 */
	public static OrderItemEntity getBulkDumpOrPowderOrderItem(Collection<OrderItemEntity> orderItems) {
		
		OrderItemEntity result = null;
		for (OrderItemEntity orderItem : orderItems) {
			if (LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(orderItem)) {
				if (result == null) {					
					result = orderItem;
				}
				else {					
					throw new LogonException("Multiple bulk order items found in collection", Reason.RES_ERROR_IMPL);
				}
			}
		}
		
		return result;
	}

	/**
	 * Finds the "bulk dumped" order item in a collection of order items
	 * @param orderItems A collection of order items, for example all assigned to the same order or load
	 * @return The "bulk dumped" order item in the collection or null if none found
	 * @throws LogonException if multiple bulk order items are found in the collection
	 */
	public static OrderItemEntity getBulkDumpedItem(Collection<OrderItemEntity> items)
	{
		OrderItemEntity result = null;
		
		for (final OrderItemEntity item : items) {
			if (LogonPackageHandlingUtil.isBulkDumped(item)) {
				if (result == null) {
					result = item;
				}
				else {
					throw new LogonException("Multiple bulk dumped order items found in collection", Reason.RES_ERROR_IMPL);
				}
			}
		}
		return result;
	}
	
	/**
	 * Finds the "bulk powder" order item in a collection of order items
	 * @param orderItems A collection of order items, for example all assigned to the same order or load
	 * @return The "bulk powder" order item in the collection or null if none found
	 * @throws LogonException if multiple bulk order items are found in the collection
	 */
	public static OrderItemEntity getBulkPowderItem(Collection<OrderItemEntity> items)
	{
		OrderItemEntity result = null;
		
		for (final OrderItemEntity item : items) {
			if (LogonPackageHandlingUtil.isBulkPowder(item)) {
				if (result == null) {
					result = item;
				}
				else {
					throw new LogonException("Multiple bulk powder order items found in collection", Reason.RES_ERROR_IMPL);
				}
			}
		}
		return result;
	}
	
	/**
	 * Finds the bag order item in a collection of order items
	 * @param orderItems A collection of order items, for example all assigned to the same order or load
	 * @return The bulk order item in the collection or null if none found
	 * @throws LogonException if multiple bulk order items are found in the collection
	 */
	public static OrderItemEntity getBagOrderItem(Collection<OrderItemEntity> orderItems) {
		
		OrderItemEntity result = null;
		for (OrderItemEntity orderItem : orderItems) {
			if (LogonPackageHandlingUtil.isBag(orderItem)) {
				if (result == null) {					
					return result = orderItem;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Returns true in case the order is a bag order. As bag and bulk products
	 * cannot be mixed see {@link LogonPackageHandlingUtil#checkLogonPackageCombination(List)}
	 * if there is at least one bag item -> the order is bag.
	 * @param orderItems
	 * @return
	 */
	public static boolean isBagOrder(OrderEntity order) {
		
		final Collection<OrderItemEntity> materialItems = LogonPackageHandlingUtil.getMaterialItems(order);
		OrderItemEntity bagOrderItem = LogonUtil.getBagOrderItem(materialItems);
		
		if (bagOrderItem != null) {
			return true;
		}
		return false;
		
	}
	
	/**
	 * Returns true in case the order is a bulk order. As bag and bulk products
	 * cannot be mixed see {@link LogonPackageHandlingUtil#checkLogonPackageCombination(List)}
	 * if there is at least one bag item -> the order is bag.
	 * @param orderItems
	 * @return
	 */
	public static boolean isBulkOrder(OrderEntity order) {
		
		final Collection<OrderItemEntity> materialItems = LogonPackageHandlingUtil.getMaterialItems(order);
		OrderItemEntity bulkOrderItem = LogonUtil.getBulkDumpOrPowderOrderItem(materialItems);
		
		if (bulkOrderItem != null) {
			return true;
		}
		return false;
		
	}
	
	public static boolean isServiceOnlyOrder(OrderEntity order) {
		
		final Collection<OrderItemEntity> materialItems = LogonPackageHandlingUtil.getMaterialItems(order);
		if (materialItems != null && !materialItems.isEmpty()) {
			return false;
		}
		
		final Collection<OrderItemEntity> serviceItems = LogonPackageHandlingUtil.getServiceItems(order);
		if (serviceItems != null && !serviceItems.isEmpty()) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Updates the load measure data.
	 * @throws LogonException if the uoms of the order items do not share the uom
	 * @return
	 */
	public static BigDecimal getSumLoadQuantitiesFromOrderItems(
			Collection<OrderItemEntity> orderItems) throws LogonException {
		if ((orderItems != null) && (!orderItems.isEmpty())) {
			
			BigDecimal loadNetQuantity = BigDecimal.ZERO;
			UomBsEntity loadedUom = null;
			
			for (OrderItemEntity oie : orderItems) {
				if (oie.getLoadedQuantity() != null) {
					loadNetQuantity = loadNetQuantity.add(oie.getLoadedQuantity());
					if ((loadedUom != null) && (oie.getLoadedUom() != null)
							&& (!oie.getLoadedUom().getUomCd().equals(loadedUom.getUomCd()))) {
						//it must be the same uom by the way for ALL
						throw new LogonException("Load " + oie.getLoad().getLoadId()
								+ " is shared by order items of different UoMs",
								Reason.RES_ERROR_IMPL);
					}
					loadedUom = oie.getLoadedUom();
				}
			}
			return loadNetQuantity;
		}
		return null;
	}
	
	/**
	 * Finds the first material order item in a collection of order items
	 * @param orderItems A collection of order items, for example all assigned to the same order or load
	 * @return The bag order item in the collection or null if none found
	 */
	public static OrderItemEntity getFirstMaterialItem(Collection<OrderItemEntity> orderItems) {
		
		OrderItemEntity result = null;
		for (OrderItemEntity orderItem : orderItems) {
			if (LogonPackageHandlingUtil.isBag(orderItem) ||  LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(orderItem)) {
				return result = orderItem;
			}
		}
		
		return result;
	}

	/**
	 * Returns the orderitem from the order, if it has a planning material.
	 * If no orderitem found with planning material, then null is returned.
	 */
	public static OrderItemEntity findPlanningMaterialOrderItem(List<OrderItemEntity> orderItems) {

		OrderItemEntity planningMaterialOrderItem = null;
		for (OrderItemEntity orderItem : orderItems) {
			if (LogonUtil.isPlanningMaterial(orderItem.getSapMaterial())) {
				planningMaterialOrderItem = orderItem;
				break;
			}
		}
		return planningMaterialOrderItem;

	}

	/**
	 * Finds the first material order item in a collection of order items
	 * @param orderItems A collection of order items, for example all assigned to the same order or load
	 * @return The bag order item in the collection or null if none found
	 */
	public static CustorderItemEntity getFirstMaterialCustorderItem(Collection<CustorderItemEntity> orderItems) {
		
		CustorderItemEntity result = null;
		for (CustorderItemEntity orderItem : orderItems) {
			if (LogonPackageHandlingUtil.isBag(orderItem.getSapMaterial()) ||  LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(orderItem.getSapMaterial())) {
				return result = orderItem;
			}
		}
		
		return result;
	}
	
	
	public static CustorderItemEntity getFirstServiceCustorderItem(Collection<CustorderItemEntity> orderItems) {
		
		CustorderItemEntity result = null;
		for (CustorderItemEntity orderItem : orderItems) {
			if (LogonPackageHandlingUtil.isService(orderItem.getSapMaterial())) {
				return result = orderItem;
			}
		}
		
		return result;
	}
	
	/**
	 * Copies the ordered quantity of the order item to the loaded quantity field of the order item.
	 * Also copies the ordered UoM and related numerator/denominator.
	 * @param orderItems
	 * @throws LogonException in case the uoms of the orderItems differ (they must be the same, since they share the load)
	 */
	public static void copyToLoadedFromOrdered(Collection<OrderItemEntity> orderItems) throws LogonException {

		String uomCdOfPreviousItem = null;
		for (OrderItemEntity orderItem : orderItems) {
			orderItem.setLoadedQuantity(orderItem.getOrderedQuantity());
			orderItem.setLoadedUom(orderItem.getOrderedUom());
			orderItem.setLrUomNumerator(orderItem.getOrderedUomNumerator());
			orderItem.setLrUomDenominator(orderItem.getOrderedUomDenominator());
			
			if (uomCdOfPreviousItem == null) {
				uomCdOfPreviousItem = orderItem.getLoadedUom().getUomCd();
			}
			else if (!uomCdOfPreviousItem.equals(orderItem.getLoadedUom().getUomCd())) {
				throw new LogonException("Load " + orderItem.getLoad().getLoadId() + " is shared by order items of different UoMs", Reason.RES_ERROR_IMPL);
			}
		}
	}
	
	/**
	 * Returns the max value which will
	 * @param precision
	 * @return
	 */
	public static BigDecimal getMaxValueForDbSizeAndPrecision (  Long dbSize, Long precision ) {
		BigDecimal result = BigDecimal.ZERO;
		if ( precision == null || dbSize == null ) {
			return result;
		}
		if( dbSize.compareTo(precision) < 0) {
			return result;
		}
		
		BigDecimal ten = BigDecimal.TEN;
		BigDecimal val = ten.pow( dbSize.intValue() - precision.intValue() );
		BigDecimal fragment = BigDecimal.ONE.divide(ten.pow(precision.intValue()));
		result = val.subtract(fragment);
		
		return result;
	}
	
	public static boolean isSimpleVehicleOrder(Long delTypeCd) {
		return delTypeCd != null && (delTypeCd.equals(Constant.DELIVERY_TYPE_SHIP) || delTypeCd.equals(Constant.DELIVERY_TYPE_BLOCK_TRAIN));
	}
	
	public static boolean isTruckOrder(Long delTypeCd) {
		return delTypeCd != null
				&& (delTypeCd.equals(Constant.DELIVERY_TYPE_ROAD_DIRECT) 
						|| delTypeCd.equals(Constant.DELIVERY_TYPE_BIMODAL_ROAD)
						|| delTypeCd.equals(Constant.DELIVERY_TYPE_RETURN_DELIVERED));
	}
    
	public static boolean isRoadDirectOrder(Long delTypeCd) {
		return delTypeCd != null && (delTypeCd.equals(Constant.DELIVERY_TYPE_ROAD_DIRECT));
	}
	
	public static boolean isRailOrder(Long delTypeCd) {
		return delTypeCd != null && (delTypeCd.equals(Constant.DELIVERY_TYPE_RAIL_DIRECT) || delTypeCd.equals(Constant.DELIVERY_TYPE_BIMODAL_RAIL));
	}
	
	public static boolean isRailRepairOrder(Long delTypeCd, String distDestCd) {
		return isRailOrder(delTypeCd)	&& (Constant.DISTRIBUTION_DEST_REPAIR.equals(distDestCd));
	}
	
	public static boolean isRailReturnOrder(Long delTypeCd, String distDestCd) {
		return isRailOrder(delTypeCd) && isReturnDistributionDest(distDestCd);
	}
	
	public static boolean isRailDirectOrder(Long delTypeCd) {
		return delTypeCd != null && (delTypeCd.equals(Constant.DELIVERY_TYPE_RAIL_DIRECT));
	}
	
	public static boolean isPickupOrder(Long delTypeCd) {
		return delTypeCd != null && delTypeCd.equals(Constant.DELIVERY_TYPE_PICKUP);
	}
	
	public static boolean isSimpleVehiclePickupOrder(Long delTypeCd) {
		return delTypeCd != null && (delTypeCd.equals(Constant.DELIVERY_TYPE_BLOCKTRAIN_PICKUP) || delTypeCd.equals(Constant.DELIVERY_TYPE_SHIP_PICKUP));
	}	
	
	public static boolean isAnyTypeOfPickupOrder(Long delTypeCd) {
		return (LogonUtil.isPickupOrder(delTypeCd) 
				|| LogonUtil.isReturnPickupOrder(delTypeCd)
				|| LogonUtil.isSimpleVehiclePickupOrder(delTypeCd));
	}
	
	/**
	 * It decides that the given deliveryType and incoterm are meaning a pickup order or not.
	 * 
	 * @param deliveryTypeCd
	 * @param incotermsCd
	 * @return 
	 */
	public static boolean isScheculedPickupOrder(Long deliveryTypeCd, String incotermsCd) {
		Boolean isRoadDirect = LogonUtil.isRoadDirectOrder(deliveryTypeCd); 
		Boolean isPickupIncoterm = Constant.INCOTERMS_EXW.equals(incotermsCd) 
									|| Constant.INCOTERMS_FOB.equals(incotermsCd);
		return isRoadDirect && isPickupIncoterm;
	}
	
	public static boolean isScheduledPickupOrder(OrderEntity order){
		SapContractEntity sapContract = order.getSapContract();
		
		if (sapContract == null) {
			String errMsg = "Sap Contract of Order Number " + order.getOrderNumber() + " is NULL";
			LOG.error(errMsg);
			throw new LogonException(errMsg, Reason.RES_IMPL_BUG);
		}
		
		String incotermsCd = sapContract != null ? sapContract.getIncotermsCd() : null;
		DeliveryTypeBsEntity deliveryTypeBs = order.getDeliveryType();
		Long deliveryTypeCd = deliveryTypeBs.getDeliveryTypeCd();
		
		return isScheculedPickupOrder(deliveryTypeCd, incotermsCd);
	}
	
	public static boolean isBimodalTruckPartOrder(Long delTypeCd) {
		return delTypeCd != null && delTypeCd.equals(Constant.DELIVERY_TYPE_BIMODAL_ROAD);
	}
	
	public static boolean isBimodalRailPartOrder(Long delTypeCd) {
		return delTypeCd != null && delTypeCd.equals(Constant.DELIVERY_TYPE_BIMODAL_RAIL);
	}

	public static boolean isShipOrder(Long delTypeCd) {
		return delTypeCd != null && delTypeCd.equals(Constant.DELIVERY_TYPE_SHIP);
	}
	
	public static boolean isBlockTrainOrder(Long delTypeCd) {
		return delTypeCd != null && delTypeCd.equals(Constant.DELIVERY_TYPE_BLOCK_TRAIN);
	}
	
	public static boolean isServiceOnlyOrder(Long delTypeCd) {
		return delTypeCd != null && delTypeCd.equals(Constant.DELIVERY_TYPE_SERVICE);
	}
	
	public static boolean isBlockTrainPickupOrder(Long deliveryTypeCd) {
		return deliveryTypeCd != null && deliveryTypeCd.equals(Constant.DELIVERY_TYPE_BLOCKTRAIN_PICKUP);
	}

	public static boolean isShipPickupOrder(Long deliveryTypeCd) {
		return deliveryTypeCd != null && deliveryTypeCd.equals(Constant.DELIVERY_TYPE_SHIP_PICKUP);
	}	
	
	public static boolean isReturnPalletOnly(OrderItemEntity orderItem) {
		OrderEntity order = orderItem.getOrder();
		return isReturnPalletOnly(order);
	}
	
	public static boolean isReturnPalletOnly(OrderEntity order) {
		if (order != null && order.getDeliveryType() != null) {
			return isReturnPalletOnly(order.getDeliveryType().getDeliveryTypeCd());
		}
		else {
			return false;
		}
	}
	
	public static boolean isReturnPalletOnly(Long delTypeCd) {
		return (isReturnPickupOrder(delTypeCd) || isReturnDeliveredOrder(delTypeCd));
	}
	
	public static boolean isReturnPickupOrder(Long delTypeCd) {
		return delTypeCd != null && delTypeCd.equals(Constant.DELIVERY_TYPE_RETURN_PICKUP);
	}
	
	public static boolean isReturnDeliveredOrder(Long delTypeCd) {
		return delTypeCd != null && delTypeCd.equals(Constant.DELIVERY_TYPE_RETURN_DELIVERED);
	}
	
	public static boolean isReturnDeliveredOrder(OrderEntity order) {
		if (order != null && order.getDeliveryType() != null) {
			return isReturnDeliveredOrder(order.getDeliveryType().getDeliveryTypeCd());
		}
		else {
			return false;
		}
	}
	
	public static boolean isLnfShipto(ShiptoEntity shipto) {
		return shipto.getShiptoType().getShiptoTypeCd() == Constant.SHIPTO_TYPE_LOCATION_NOT_FIXED;
	}
	
	public static boolean isShipOrBlocktrainPickup(SimpleVehicleEntity simpleVehicle){
		return simpleVehicle != null &&	(Constant.SIMPLE_VEHICLE_BLOCKTRAIN_PICKUP_CD == simpleVehicle.getSimpleVehicleId()
											|| Constant.SIMPLE_VEHICLE_SHIP_PICKUP_CD == simpleVehicle.getSimpleVehicleId());
	}
	
	/**
	 * In case logon package type is required to be checked then returns false in case the composition given by truck + trailer + container
	 * is not consistent with the logon package type (ie. bulk/bag) given. 
	 * 
	 * WARNING: this method will return false if ANY of the required parts are missing from the composition. It does not allow part compositions for example
	 * a truck and a container type trailer without a container which is in itself valid until it does not have to carry anything
	 * Method should only be used at places, where the composition MUST be whole, and not at places
	 * where this is not a problem at all if a truck does not have a trailer/container, and is just getting to the point where the parts are going to be attached to the truck/trailer.
	 * 
	 * If the composition has to be checked to be complete according to material type:
	 * 
	 * BULK:
	 * - Either truck of type silo,
	 * - or container-truck and container of type silo
	 * - or trailer-truck + trailer of type silo
	 * - or trailer-truck and trailer of type container and container of type silo -> for materials of type bulk
	 * 
	 * BAG:
	 * - only truck of type bag or trailer-truck + trailer of type bag are possible (there are no containers of type bag)
	 * 
	 * In case the logon package type cd is not given, the composition counts as valid, if it is either valid for bag or bulk.
	 * 
	 * @param logonPackageTypeCd - send null if you just need to check the validity of the composition, but you dont need to check it against transportation mode
	 * @param truck
	 * @param trailer
	 * @param container
	 */
	public static boolean isTruckCompositionValidForMaterialPackage(final Long logonPackageTypeCd, final TruckEntity truck, final TrailerEntity trailer, final ContainerEntity container) 
	{
		final Long truckTrspCd = truck != null ? truck.getTruckTransportCat().getTruckTransportCatCd() : null;
		final Long trailerTrspCd = trailer != null ? trailer.getTrailerTrspCat().getTrailerTrspCatCd() : null;
		final Long containerTrspCd = container != null ? container.getContainerTrspCat().getContainerTrspCatCd() : null;
		
		if (!isTruckCompositionValid(truckTrspCd, trailerTrspCd, containerTrspCd)) {
			//it is not valid at all
			LOG.error("Composition is invalid: truck (" + truck.getTruckId() + ", " + truck.getTruckName() +  ") trailer ( " + ( trailer != null ? trailer.getTrailerId() + ", " + trailer.getTrailerName() : "" ) +  ") container (" +  (container != null ? container.getContainerId() + ", " + container.getContainerName()  : "") + ")");
			LOG.error("Transport categories of truck/trailer/container are {}/{}/{}, respectively.", 
					new Object[] {truckTrspCd, trailerTrspCd, containerTrspCd});
			return false;
		}
		else if ( logonPackageTypeCd == null ) {
			/* It is valid as far as the composition is valid, 
			 * and checking against the material package cd was not required.
			 */
			LOG.debug("Truck composition is valid, and no material was specified, so valid.");
			return true;
		}
		else if (!packageTypeMatchesTransportCategory(logonPackageTypeCd, truckTrspCd, trailerTrspCd, containerTrspCd))
		{
			LOG.error("Package type {} doesn't match the truck composition's transport categories.", new Object[] {logonPackageTypeCd});
			LOG.error("Transport categories of truck/trailer/container are {}/{}/{}, respectively.", 
					new Object[] {truckTrspCd, trailerTrspCd, containerTrspCd});
			return false;
		}
		else {
			return true;
		}
	}
	
	/**
	 * Checks if a given package code is compatible with the transport categories
	 * of the truck composition.
	 * This function assumes that the truck composition is valid (i.e., it passes
	 * <code>isTruckCompositionValid()</code> and that therefore, for example,
	 * a truck transport category of "tractor" implies a nonnull trailer
	 * transport category.
	 * @param logonPackageTypeCd The package code of the order's main material
	 * @param truckTrspCd The truck transport code
	 * @param trailerTrspCd The trailer's transport code, or null if no trailer is present in the composition
	 * @param containerTrspCd The container's transport code, or null if no container is present in the composition.
	 * @return True if the package type matches the composition's transport categories.
	 */
	static boolean packageTypeMatchesTransportCategory(
			final Long packageTypeCd, 
			final Long truckTrspCd,
			final Long trailerTrspCd, 
			final Long containerTrspCd) 
	{
		/* We explicitly assume here that the composition type is valid
		 * and don't do anymore checks for it.
		 */
		if (truckTrspCd == Constant.TRUCK_TRSP_CAT_TRAILER) 
		{
			/* Normally we would need to go on and check either the trailer's transport
			 * category against the package type (if it is a non-container trailer) or,
			 * if the trailer is a container trailer, we would have to check the container's
			 * transport category against the package type. But since there is only one
			 * container transport type, the compatibility is only defined with respect to
			 * the trailer transport type. (This is the same case as for the truck-container
			 * composition, see below.
			 */
			return PackageTypeTrspCatMapping.isTrailerCatAllowedForPackageType(trailerTrspCd, packageTypeCd);
		}
		else{
			/* Since container has only one possible transport category (at the moment),
			 * the compatibility of materials with containers is defined on a
			 * truck transport category basis. (I.e., the truck transport category "container"
			 * identifies uniquely the single possible container transport category.)
			 */
			return PackageTypeTrspCatMapping.isTruckCatAllowedForPackageType(truckTrspCd, packageTypeCd);
		}
	}

	/** 
	 * Verifies the validity of a truck/trailer/container composition.
	 * This method checks if the given combination of a truck, trailer, and container are valid, according to the following rules:
	 * <ol>
	 * 	<li>If the truck has transport category "tractor", then a trailer must be present.</li>
	 * 	<li>If the truck has transport category "container", then a container must be present.</li>
	 * 	<li>If the truck has transport category "tractor", a trailer is present, and the trailer has
	 * 	transport category "container", then a container must be present.
	 * </ol>
	 * @param truckTrspCd The transport category of the truck, or null if no truck is present.
	 * @param trailerTrspCd The transport category of the trailer, or null if no trailer is present.
	 * @param containerTrspCd The transport category of the container, or null if no container is present.
	 * @return true if all of the above rules are satisfied, false otherwise.
	 */
	public static boolean isTruckCompositionValid(
			final Long truckTrspCd, final Long trailerTrspCd, final Long containerTrspCd)
	{
		if (truckTrspCd == null) {
			// No truck present, can't do anything without a truck.
			return false;
		}
		else if (truckTrspCd == Constant.TRUCK_TRSP_CAT_TRAILER) 
		{
			if (trailerTrspCd == null) {
				// Truck is tractor but missing it's trailer.
				return false;
			}
			else if (trailerTrspCd == Constant.TRAILER_TKATEGORIE_CONTAINER) {
				/* We have a tractor truck and a trailer, so it
				 * is ok if and only if there is also a container.
				 */
				return (containerTrspCd != null);
			}
			else {
				/* Tractor truck with trailer, and trailer doesn't require
				 * a container, so we're good.
				 */
				return true;
			}
		}
		else if (truckTrspCd == Constant.TRUCK_TRSP_CAT_CONTAINER) 
		{
			// Ok if and only if there is also a container.
			return containerTrspCd != null;
		}
		else {
			/* The truck requires neither a trailer nor a container, so we're good
			 * in the first place.
			 */
			return true;
		}
	}
	
	/**
	 * @param order
	 * @return
	 */
	public static boolean isServingByCarrierStockPossible(OrderEntity order) {
		for (OrderItemEntity item : (List<OrderItemEntity>) LogonPackageHandlingUtil.getMaterialItems(order)) {
			if (LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(item)) {
				return false;
			}
		}
		return ((order.getCommCarrier() != null) && (order.getCommCarrier().getBagStockShippingpoint() != null));
	}
	
	public static String createLNFAddressString(OrderEntity order) {
		final String lnfSiteNameString = order.getLnfSiteName();
		final String lnfSiteStreetHouseNoString = order.getLnfSiteStreetHouseNo();
		final String lnfSiteCountryString = order.getLnfSiteCountry();
		final String lnfSitePostalCodeString = order.getLnfSitePostalCode();
		final String lnfSiteCityString = order.getLnfSiteCity();
		
		final StringBuilder sb = new StringBuilder();
		if (StringUtils.isNotEmpty(lnfSiteNameString)) {
			sb.append(lnfSiteNameString);
		}
		if (StringUtils.isNotEmpty(lnfSiteStreetHouseNoString)) {
			if (sb.length() > 0) {
				sb.append('/');
			}
			sb.append(lnfSiteStreetHouseNoString);
		}
		
		final StringBuilder sb2 = new StringBuilder();
		if (StringUtils.isNotEmpty(lnfSiteCountryString)) {
			sb2.append(lnfSiteCountryString);
		}
		if (StringUtils.isNotEmpty(lnfSitePostalCodeString)) {
			if (sb2.length() > 0) {
				sb2.append(' ');
			}
			sb2.append(lnfSitePostalCodeString);
		}
		if (StringUtils.isNotEmpty(lnfSiteCityString)) {
			if (sb2.length() > 0) {
				sb2.append(' ');
			}
			sb2.append(lnfSiteCityString);
		}
		
		if (sb2.length() > 0) {	
			if (sb.length() > 0) {
				sb.append('/');
			}
			sb.append(sb2);
		}
		return sb.toString();
	}
	
	/**
	 * Compares BigDecimals. Null values treated as 0s.
	 * @param value1 first {@link BigDecimal}, could be null
	 * @param value2 second {@link BigDecimal}, could be null
	 * @return the same value as {@link BigDecimal#compareTo(BigDecimal)}
	 */
	public static int compare(BigDecimal value1, BigDecimal value2) {
		if (value1 == null) {
			value1 = new BigDecimal(0);
		}
		if (value2 == null) {
			value2 = new BigDecimal(0);
		}
		return value1.compareTo(value2);
	}
	
	public static boolean isSentToSap(OrderEntity order) {
		if (order.getSentSalesdocToSapTs() != null && order.getSentShipmentdocToSapTs() != null) {
			return true;
		}
		else if (order.getSentSalesdocToSapTs() != null 
				&& (LogonUtil.isAnyTypeOfPickupOrder(order.getDeliveryType().getDeliveryTypeCd())
						|| LogonUtil.isScheduledPickupOrder(order))) {
			
			// no shipment doc generated for pickup/scheduled pickup (see: LOGON-5473 and LOGON-6770)
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Returns true if the truck has to be considered a planning truck. False if it is a real truck, known also for SAP.
	 * @param truck
	 */
	public static boolean isPlanningTruck(final TruckEntity truck) {
		final SapVehicleCharBsEntity sapVehicleChar = truck.getSapVehicleChar();
		final Long sapVehicleCharCd = sapVehicleChar.getSapVehicleCharCd();
		return isPlanningTruck(sapVehicleCharCd);
	}
	
	/**
	 * Returns true if the trailer has to be considered a planning trailer. False if it is a real trailer, known also for SAP.
	 * @param trailer
	 */
	public static boolean isPlanningTrailer(final TrailerEntity trailer) {
		final Long sapVehicleCharCd = trailer.getSapVehicleChar().getSapVehicleCharCd();
		return isPlanningTruck(sapVehicleCharCd);
	}

	/**
	 * Returns true if the truck characteristic code has to be considered planning.
	 * @param sapVehicleCharCd
	 */
	public static boolean isPlanningTruck(final long sapVehicleCharCd) {
		if ( sapVehicleCharCd == Constant.VEHICLE_CHARACTERISTIC_PLANNING ) {
			return true;
		}
		if (sapVehicleCharCd == Constant.VEHICLE_CHARACTERISTIC_GENERIC || 
				sapVehicleCharCd == Constant.VEHICLE_CHARACTERISTIC_INDIVIDUAL ) {
			return false;
		}
		throw new LogonException();
	}
	
	/**
	 * Returns the value given in fromCurrency converted to toCurrency using the
	 * current exchange rate (current_date).
	 * @param fromCurrency the currency of the value
	 * @param toCurrency the destination currency
	 * @param value the value to be converted
	 * @param cerDao
	 * @return
	 */
	public static BigDecimal getCurrencyConvertedValue(CurrencyBsEntity fromCurrency,
			CurrencyBsEntity toCurrency, BigDecimal value,
			ReadOnlyDao<CurrentExchangeRateEntity> cerDao) {
		BigDecimal result = null;
		
		if (fromCurrency == null || toCurrency == null || value == null) {
			return result;
		}
		
		String fromCurrencyCd = fromCurrency.getCurrencyCd();
		String toCurrencyCd = toCurrency.getCurrencyCd();
		
		if (fromCurrencyCd.equals(toCurrencyCd)) {
			return value;
		}
		
		Filter<CurrentExchangeRateEntity> f;
		
		f = new FilterImpl<CurrentExchangeRateEntity>();
		f.addExpression(Restrictions.eq(CurrentExchangeRateEntity.ATTR_CURRENCY_CD, fromCurrencyCd));
		CurrentExchangeRateEntity rateFrom = cerDao.findSingle(f);

		f = new FilterImpl<CurrentExchangeRateEntity>();
		f.addExpression(Restrictions.eq(CurrentExchangeRateEntity.ATTR_CURRENCY_CD, toCurrencyCd));
		CurrentExchangeRateEntity rateTo = cerDao.findSingle(f);

		if (rateFrom == null) {
			LOG.error("No exchange rate for " + fromCurrencyCd + " has been defined!");
			return result;
		}
		else if (rateTo == null) {
			LOG.error("No exchange rate for " + toCurrencyCd + " has been defined!");
			return result;
		}
		
		result = value;
        if (rateFrom.getExchangeRate().compareTo(BigDecimal.ONE) != 0) {
            result = result.divide(rateFrom.getExchangeRate(), Constant.SCALE, BigDecimal.ROUND_HALF_UP);
        }
        if (rateTo.getExchangeRate().compareTo(BigDecimal.ONE) != 0) {
        	result = result.multiply(rateTo.getExchangeRate());
        }
		
		return result;
	}
	
	/**
	 * The method is responsible to decide if a shippingpoint-type is prevented from having applied loading points.
	 * @param shipppointTypeCd
	 * @return
	 */
	public static boolean isLoadingpointPreventedForShippingpointType(Long shipppointTypeCd) {
		return shipppointTypeCd.equals(Constant.SHIPPINGPOINT_TYPE_FVL_BIMODAL_TRUCK) ||
				shipppointTypeCd.equals(Constant.SHIPPINGPOINT_TYPE_RAIL_SUPPLIER) ||
				shipppointTypeCd.equals(Constant.SHIPPINGPOINT_TYPE_RAIL_REPAIR) ||
				shipppointTypeCd.equals(Constant.SHIPPINGPOINT_TYPE_RAIL_EXTERNAL_JUST_RETURN);
	}
	
	
	/**
	 * Try to obtain the client IP address using the request from the PolicyContext.
	 * Could be usefull if the request is not available directly (e.g. in the business layer)
	 * @return the IP address of the client or null
	 */
	public static String getClientIP() {
		// [[
		String result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonUtil.class, "getClientIP");
		}
		// ]]
		try {
			String ipAddress = null;
			HttpServletRequest request = (HttpServletRequest) PolicyContext.getContext(Constant.POLICY_CONTEXT_REQUEST_KEY);

			if (request != null) {
				for (String header : clientIpRelevantHeaders) {
					ipAddress = request.getHeader(header);
					if (ipAddress != null && ipAddress.length() > 0
							&& !"unknown".equalsIgnoreCase(ipAddress)) {
						if (LOG.isDebugEnabled()) {
							LOG.debug("client IP address obtained using " + header
									+ " request header, client IP: " + ipAddress);
						}
						break;
					}
				}

				if (ipAddress == null || ipAddress.length() == 0
						|| "unknown".equalsIgnoreCase(ipAddress)) {
					ipAddress = request.getRemoteAddr();
					if (LOG.isDebugEnabled()) {
						LOG.debug("client IP address obtained using request.getRemoteAddr(), client IP: "
								+ ipAddress);
					}
				}
			}
			else {
				LOG.error("could not obtain request (null)");
			}

			return result = ipAddress;
		}
		catch (Exception e) {
			LOG.error("could not obtain client IP address", e);
			return result = null;
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
	 * Returns true if the order starts at the shipto instead of shipping point
	 * @param order
	 * @return boolean
	 */
	public static boolean isOrderWithReversedDirection(OrderEntity order) {
		return isLandfill(order) || isReturnDeliveredOrder(order);
	}
	
	/**
	 * Returns true if the first order item turns out to be a landfill order.
	 * @param order
	 * @return
	 */
	public static boolean isLandfill(OrderEntity order) {
		if ( order == null ) {
			return false;
		}
		final OrderItemEntity orderItem = (OrderItemEntity) order.getOrderItems().get(0);
		return isLandfill(orderItem.getSapMaterial());
	}
	
	/**
	 * Returns true if the first custorder item turns out to be a landfill custorder.
	 * @param order
	 * @return
	 */
	public static boolean isLandfill(CustorderEntity custorder) {
		if (custorder == null) {
			return false;
		}
		final CustorderItemEntity custorderItem = custorder.getCustorderItems().get(0);
		return isLandfill(custorderItem.getSapMaterial());
	}
	
	/**
	 * Checks if the sap material is a landfill sap material according to the flags.
	 * @param sapMaterial
	 * @return true if and only if it is definitely landfill
	 */
	public static boolean isLandfill(SapMaterialEntity sapMaterial) {
		return (sapMaterial.getMaterialIsLf());
	}
	
	/**
	 * Checks if the sap material is a planning material by sapMaterialNo.
	 * @param sapMaterial
	 * @return true if and only if it is definitely planning material
	 */
	public static boolean isPlanningMaterial(SapMaterialEntity sapMaterial) {
		final String sapMaterialNo = sapMaterial.getSapMaterialNo();
		return isPlanningMaterial(sapMaterialNo);
	}

	/**
	 * Checks if the sap material is a planning material by sapMaterialNo.
	 * @param sapMaterial
	 * @return true if and only if it is definitely planning material
	 */
	public static boolean isPlanningMaterial(final String sapMaterialNo) {
		return isLandfillPlanningMaterial(sapMaterialNo) || isAggPlanningMaterial(sapMaterialNo);
	}
	
	/**
	 * Checks if the sap material is a landfill type planning material by sapMaterialNo.
	 * @param sapMaterial
	 * @return true if and only if it is definitely a landfill planning material
	 */
	public static boolean isLandfillPlanningMaterial(SapMaterialEntity sapMaterial) {
		final String sapMaterialNo = sapMaterial.getSapMaterialNo();
		boolean isLandfillPlanningMaterial = isLandfillPlanningMaterial(sapMaterialNo);
		return isLandfillPlanningMaterial;
	}

	/**
	 * Checks if the sap material is a landfill type planning material by sapMaterialNo.
	 * @param sapMaterial
	 * @return true if and only if it is definitely a landfill planning material
	 */
	public static boolean isLandfillPlanningMaterial(final String sapMaterialNo) {
		boolean isLandfillPlanningMaterial = Constant.SAP_MATERIAL_NO_LANDFILL_PLANNING.equals(sapMaterialNo);
		return isLandfillPlanningMaterial;
	}
	
	/**
	 * Checks if the sap material is an aggregate type planning material by sapMaterialNo.
	 * @param sapMaterial
	 * @return true if and only if it is definitely an aggregate planning material
	 */
	public static boolean isAggPlanningMaterial(SapMaterialEntity sapMaterial) {
		final String sapMaterialNo = sapMaterial.getSapMaterialNo();
		final boolean isAggPlanningMaterial = isAggPlanningMaterial(sapMaterialNo);
		return isAggPlanningMaterial;
	}

	public static boolean isAggPlanningMaterial(final String sapMaterialNo) {
		final boolean isAggPlanningMaterial = Constant.SAP_MATERIAL_NO_AGG_PLANNING.equals(sapMaterialNo);
		return isAggPlanningMaterial;
	}
	
	/**
	 * Planning material must be replaced by a real material until the order reaches
	 * a specific state:
	 * - in case of aggregate planning material: can be replaced before loading state
	 * - in case of landfill planning material: can be replaced before unloading state
	 * Replacing the material can be done on order details and by lds.
	 * 
	 * @param order
	 * @param sapMaterial
	 * @return
	 */
	public static boolean isStillReplacablePlanningMaterial(OrderEntity order, SapMaterialEntity sapMaterial) {
		// [[
		boolean result = false;
		Object[][] params = {{"order", order},
							{"sapMaterial", sapMaterial}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonUtil.class, "isStillReplacablePlanningMaterial", params);
		}
		// ]]
		try {
			boolean isMaterialEditable = false;
			if (LogonUtil.isAggPlanningMaterial(sapMaterial)) {
				isMaterialEditable = LogonStatusUtils.isBeforeLoading(order.getOrderStatus());
			}
			else if (LogonUtil.isLandfillPlanningMaterial(sapMaterial)) {
				isMaterialEditable = LogonStatusUtils.isArrivedAtUnloadingPointOrBefore(order.getOrderStatus());
			}
			return result = isMaterialEditable;
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
	 * Decides if the given order is container type order.
	 *  
	 * @param order A valid OrderEntity instance.
	 * @return true if the given order is container, otherwise false. 
	 */
	public static boolean isContainerOrder(OrderEntity order) {
		if (order == null) {
			throw new IllegalArgumentException("The order should not be null!");
		}
		
		final List<OrderItemEntity> orderItems = order.getOrderItems();
		
		if (orderItems == null || orderItems.isEmpty()) {
			return false;
		}
		
		for (OrderItemEntity orderItemEntity : orderItems) {
			final UomBsEntity uomBsEntity = orderItemEntity.getOrderedUom();
			if (uomBsEntity != null) {
				final String uomCd = uomBsEntity.getUomCd();
				
				if (uomCd.equals(Constant.UOM_CONTAINER)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * A contract is quantity controlled, if the contract item generating method was
	 * either quantity restricted or advance payment or unknown (inheritance).
	 */
	public static boolean isQuantityControlledContract(SapContractEntity contract) {
		// [[
		boolean result = false;
		Object[][] params = {{"contract", contract}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonUtil.class, "isQuantityControlledContract", params);
		}
		// ]]
		try {
			if (contract == null) {
				return result = false;
			}
			
			final Long itemGroupingType = contract.getItemGroupingType();
			result = isQuantityControlledContract(itemGroupingType);
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
	 * A contract is quantity controlled, if the contract item generating method was
	 * either quantity restricted or advance payment or unknown (inheritance).
	 */
	public static boolean isQuantityControlledContract(
			Long itemGroupingType) {
		
		boolean result = false;
		
		if (Constant.SAP_CONTRACT_ITEM_GROUPING_TYPE_QUANTITY_RESTRICTED == itemGroupingType
				|| Constant.SAP_CONTRACT_ITEM_GROUPING_TYPE_ADVANCE_PAYMENT == itemGroupingType
				|| Constant.SAP_CONTRACT_ITEM_GROUPING_TYPE_UNKNOWN == itemGroupingType) {
			result = true;
		}
		return result;
	}
	
	/**
	 * @param primitive
	 * @return
	 */
	public static List convertToLongList(long[] primitive) {
		// [[
		List result = null;
		Object[][] params = {{"primitive", primitive}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonUtil.class, "convertToLongList", params);
		}
		// ]]
		try {
			List returnList = new ArrayList();
			if (primitive != null){
				for (int i=0; i< primitive.length; i++){
					returnList.add(Long.valueOf(primitive[i]));
				}
			}
			return result = returnList;
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
	 * Create a Map from the Lost of object array. The List contains object array returned 
	 * from the DirectJpaDao using the JpaQueryData API.
	 * 
	 * @param objectList
	 * @param jpaQueryData
	 * @param keyColumn
	 * @param textColumns
	 * @param format
	 * @return
	 */
	public static Map<Object, String> createMapFromList(List<Object[]> objectList,
														JpaQueryData jpaQueryData, String keyColumn, List<String> textColumns, String format) {
		// [[
		Map<Object, String> result = null;
		Object[][] params = {{"objectList", objectList},
							{"jpaQueryData", jpaQueryData},
							{"keyColumn", keyColumn},
							{"textColumns", textColumns},
							{"format", format}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonUtil.class, "toKeyTextBeanList", params);
		}
		// ]]
		try {
			if (jpaQueryData == null || com.crh.logon.utils.StringUtils.empty(keyColumn) || textColumns == null || textColumns.isEmpty()) {
				
				String errorMsg = "The 'jpaQueryData' and/or 'keyColumn' and/or 'textColumns' parameter is empty or NULL";
				LOG.error(errorMsg);
				throw new IllegalArgumentException(errorMsg);
			}
			
			if (objectList == null) {
				return result = null;
			}
			
			result = new LinkedHashMap<Object, String>();
			int keyColIdx = jpaQueryData.getIndexOf(keyColumn);
			
			List<Integer> textColIdxList = new ArrayList<Integer>();
			for (Iterator<String> iter = textColumns.iterator(); iter.hasNext();) {
				textColIdxList.add(jpaQueryData.getIndexOf(iter.next()));
			}
			
			List<String> helperList = new ArrayList<String>(20);
			for (Iterator<Object[]> iter = objectList.iterator(); iter.hasNext();) { 
				Object[] objArray = iter.next();
				
				helperList.clear();
				for (Iterator<Integer> iter2 = textColIdxList.iterator(); iter2.hasNext();) { 
					helperList.add(com.crh.logon.utils.StringUtils.nvl(String.valueOf(objArray[iter2.next()])));
				}
				
				Object key = objArray[keyColIdx];
				String text = String.format(format, helperList.toArray());
				result.put(key, text);
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
	
	public static boolean isLoadForPickupTruck(TruckEntity truck) {
		if (truck == null) {
			return false;
		}
		final Long truckId = truck.getTruckId();
		
		return 	Constant.ARTIFICIAL_PICKUP_BAG_TRUCK_ID.equals(truckId) || 
				Constant.ARTIFICIAL_PICKUP_BULK_PWDR_TRUCK_ID.equals(truckId) ||
				Constant.ARTIFICIAL_PICKUP_BULK_DMP_TRUCK_ID.equals(truckId);
	}
	
	public static SplittedOrderData.DeliveryType getDeliveryType(OrderEntity orderEntity) {
		// [[
		SplittedOrderData.DeliveryType result = null;
		Object[][] params = {{"orderEntity", orderEntity}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonUtil.class, "getDeliveryType", params);
		}
		// ]]
		try {
			
			DeliveryTypeBsEntity deliveryType = orderEntity.getDeliveryType();
			
			if (deliveryType == null) {
				return null;
			}
			
			Long deliveryTypeCd = deliveryType.getDeliveryTypeCd();
			
			if (isRoadDirectOrder(deliveryTypeCd) || isSimpleVehicleOrder(deliveryTypeCd)) {
				return result = DeliveryType.ROAD;
			}
			else if (isRailDirectOrder(deliveryTypeCd)) {
				return result = DeliveryType.RAIL;
			}
			else if (isPickupOrder(deliveryTypeCd)) {
				return result = DeliveryType.PICKUP;
			}
			else if (isBlockTrainPickupOrder(deliveryTypeCd)) {
				return result = DeliveryType.BLOCKTRAINPICKUP;
			}
			else if (isShipPickupOrder(deliveryTypeCd)) {
				return result = DeliveryType.SHIPPICKUP;
			}
			else if (isBimodalRailPartOrder(deliveryTypeCd) || isBimodalTruckPartOrder(deliveryTypeCd)) {
				return result = DeliveryType.BIMODAL;
			}
			else if (isServiceOnlyOrder(deliveryTypeCd)) {
				return result = DeliveryType.SERVICE;
			}
			else if (isReturnPickupOrder(deliveryTypeCd)) {
				return result = DeliveryType.RETURN_PICKUP;
			}
			else if (isReturnDeliveredOrder(deliveryTypeCd)) {
				return result = DeliveryType.RETURN_DELIVERED;
			}
			
			throw new RuntimeException("Delivery type [" + deliveryTypeCd + "] not expected!");
			
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
	 * Returns true if an Object represets a combobox that has no item selected (i.e. the value is "" or -1 or -1L or "-1");
	 * Used tipically by DWR calls
	 * 
	 * @param comboValue
	 * @return
	 */
	public static boolean emptyValue(Object comboValue) {
		return comboValue == null || 
			(
				"".equals(comboValue) || ObjectUtils.equals(comboValue, -1L) || 
				ObjectUtils.equals(comboValue, -1) || "-1".equals(comboValue)
			);
	}
	
	public static String getFullName(UserEntity user, Integer maxLength) {
		// [[
		String result = null;
		Object[][] params = {{"user", user}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonUtil.class, "getFullName", params);
		}
		// ]]
		try {
			if (user == null || user.getPerson() == null) {
				return result = "";
			}
			
			PersonEntity person = user.getPerson();
			
			String firstName = com.crh.logon.utils.StringUtils.nvl(person.getFirstname());
			String lastName = com.crh.logon.utils.StringUtils.nvl(person.getName());
			
			StringBuffer sb = new StringBuffer(firstName);
			sb.append(" ");
			sb.append(lastName);
			
			result = sb.toString().trim();
			
			if (maxLength != null && result.length() > maxLength) {
				result = result.substring(0, maxLength);
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
	 * Sets the password validity to infinity (null) if the user is customer or vendor
	 * @param user
	 */
	public static UserEntity setExtLoginPxdValidForCustomerVendor(UserEntity user){
		// [[
		UserEntity result = null;
		Object[][] params = {{"user", user}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonUtil.class, "setExtLoginPxdValidForCustomerVendor", params);
		}
		// ]]
		try {
			int userTypeCd = user.getUserType().getUserTypeCd().intValue();
			if(userTypeCd == Constant.USER_TYPE_CUSTOMER || userTypeCd == Constant.USER_TYPE_VENDOR){
				user.setExtLoginPwdValidToDat(null);
			}
			return result = user;
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
	 * The method constructs a message using the provided static string {@link MopStatusTxEntity#getTxt()}
	 * and the provided format parameters.
	 * 
	 * @param locale
	 * @param formatParameters
	 * @param mopStatusTx
	 * @return
	 */
	public static String resolveMopStatusText(Locale locale, Object[] formatParameters,
			MopStatusTxEntity mopStatusTx) {
		String txt = null;
		if (mopStatusTx != null) {
			String refTxt = mopStatusTx.getTxt();
			if (refTxt != null) {
				MessageFormat formatter = new MessageFormat("");
				formatter.setLocale(locale);
				formatter.applyPattern(refTxt);
				Object[] sFmtParams = new Object[formatParameters.length];
				for (int i=0;i<formatParameters.length;i++) {
					sFmtParams[i] = formatParameters[i]!=null?formatParameters[i].toString():null;
				}
				
				txt = formatter.format(sFmtParams);
			}
		}
		
		return txt;
	}

	/**
	 * Null safe compare of two values. Null value is considered the lesser value. 
	 * @param value1 the first value
	 * @param value2 the second value
	 * @return <ul>
	 * <li>-1 if value1 is less than value2 (or value1 is null, while value2 is not null)
	 * <li> 0 if they are equals (or both values are null)
	 * <li> 1 if value1 is greater than value2 (or value2 is null, while value1 is not null)
	 * </ul>
	 */
	@SuppressWarnings("unchecked")
	public static <T> int nullSafeCompare(Comparable<T> value1, Comparable<T> value2) {
	
		if (value1 == null && value2 == null) {
			return LogonUtil.SORTING_ORDER_IS_EQUAL;
		}
		if (value1 != null && value2 != null) {
			return value1.compareTo((T) value2);
		}
		if (value1 == null && value2 != null) {
			return LogonUtil.SORTING_ORDER_FIRST_VALUE_IS_THE_LESSER;
		}
	
		return LogonUtil.SORTING_ORDER_FIRST_VALUE_IS_THE_GREATER;
	}

	
}
