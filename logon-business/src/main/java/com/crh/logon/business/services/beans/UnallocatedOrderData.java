/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.persistence.entities.views.SimpvehOrdVhcEntity;
import com.crh.logon.persistence.entities.views.SimpvehUnalcOrdEntity;

/**
 * Wrapper class to hold a SimpvehUnalcOrdEntity (an unallocated order
 * taken from VLGNC_SIMPVEH_UNALC_ORD) and a corresponding list of
 * SimpvehOrdVhcEntities (the list of simple vehicles to which the
 * order can be allocated, from VLGNC_SIMPVEH_ORD_VHC).
 * @author AdNovum Informatik AG
 */
public class UnallocatedOrderData implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private final SimpvehUnalcOrdEntity order;
	private final Collection<SimpvehOrdVhcEntity> vehicles;
	
	private TimeZone userTimeZone;

	/**
	 * Creates a new UnallocatedOrderData object with the
	 * specified data.
	 * @param order must not be null
	 * @param vehicles must not be null, but can be empty
	 */
	public UnallocatedOrderData(
			SimpvehUnalcOrdEntity order,
			Collection<SimpvehOrdVhcEntity> vehicles)
	{
		if (order == null) {
			throw new IllegalArgumentException("order must not be null");
		}
		if (vehicles == null) {
			throw new IllegalArgumentException("vehicles list must not be null");
		}
		
		this.order = order;
		this.vehicles = vehicles;
	}


	/**
	 * @return the order
	 */
	public SimpvehUnalcOrdEntity getOrder() {
		return order;
	}


	/**
	 * @return the vehicles
	 */
	public Collection<SimpvehOrdVhcEntity> getVehicles() {
		return vehicles;
	}

	public String getOrderDeliveryFrom() {
		return userTimeZone.fmtAsDayMonthYear4HourMinute(order.getDeliveryFromDat());
	}

	public String getOrderDeliveryTo() {
		return userTimeZone.fmtAsDayMonthYear4HourMinute(order.getDeliveryToDat());
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("[Unallocated order ")
		.append(order.getOrderNumber())
		.append(" for vehicles [");
		
		for (Iterator<SimpvehOrdVhcEntity> i = vehicles.iterator(); i.hasNext(); )
		{
			final SimpvehOrdVhcEntity vehicle = i.next();
			sb.append(vehicle.getVehicleName());
			if (i.hasNext()) {
				sb.append(", ");
			}
		}
		sb.append("]]");
		
		return sb.toString();
	}

	public void setUserTimeZone(TimeZone userTimeZone) {
		this.userTimeZone = userTimeZone;
	}
}
