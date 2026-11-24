/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.business.services.localinterfaces;

import com.crh.logon.business.services.utils.CustOrderWrapper;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.utils.TransferContext;

import java.lang.Integer;
import java.lang.Long;
import java.lang.String;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.ejb.Local;
@Local
public interface DispoServiceLocal {
    
	/**
	 * Fetches the list of external and LOGON orders for the Truck Scheduling screen.
	 * The external and LOGON orders are wrapped into CustOrderWrappers.
	 * The LOGON orders are grouped together by customer orders,
	 * the external orders are grouped together by the shipto ID.
	 * 
	 * @param ctxN
	 * @param requestLocale The locale of the request.
	 * @param custorderNumber An existing Customer Order number to filter for that only.
	 * @param hideScheduledCustorders Should we hide those Customer Orders which are in the Scheduled state.
	 * @param hideClosedCustorders Should we hide those Customer Orders which are in the Closed state.
	 * @param date The actual date for fetching the orders.
	 * @param poolId The id of the selected pool for which to fetch orders.
	 * @param carriers The id of the selected carriers for which to fetch orders.
	 * @param plantCds The codes of the selected plants for which to fetch orders.
	 * @param noCarrier Include orders with no assigned carrier.
	 * @param userLocale 
	 * @param extOrders Should return external orders also.
	 * @param userTimeZone The timezone of the user.
	 * @return A list of CustOrderWrapper objects.
	 */
    public List<CustOrderWrapper> getOrderListLocal(TransferContext ctxN, Locale requestLocale, Long custorderNumber, boolean hideScheduledCustorders, boolean hideClosedCustorders, Date date, Long poolId, List carriers,
													Collection<String> plantCds, boolean noCarrier, Locale userLocale, boolean extOrders, TimeZone userTimeZone);
	
    /**
     * Fetches a list of TruckWrappers, a list of AssignmentWrappers and a list of truck IDs.
     * 
     * @param transferContext
     * @param date
     * @param language
     * @param locale
     * @param poolId
     * @param dispoCarriers
     * @param showOnlyTrucksWithOrders
     * @param hideTrucksWithoutDeliveries
     * @param hidePlanningTrucks
     * @param hideInactiveTrucks
     * @param filterForGenericTruck
     * @param orderDeliveryStatusCds
     * @param plantCds
     * @param custorderNumber
     * @param sizeOfOrderStatusList
     * @param filterTruckIds If not empty only fetch the trucks with the given truck IDs.
     * @return
     */
    public List[] getTruckAndAssigmentList(TransferContext transferContext, Date date, final String language, Locale locale, Long poolId, List dispoCarriers, Boolean showOnlyTrucksWithOrders,
			Boolean hideTrucksWithoutDeliveries, Boolean hidePlanningTrucks, Boolean hideInactiveTrucks, Integer  filterForGenericTruck,
			Collection<Long> orderDeliveryStatusCds, Collection<String> plantCds, Long custorderNumber, Integer sizeOfOrderStatusList,
			List<Long> filterTruckIds, TimeZone userTimeZone);

}
