/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans;

import java.io.Serializable;
import java.math.BigDecimal;

import com.crh.logon.beans.HasLoadedQuantity;

/**
 * @author AdNovum Informatik AG
 */
public class SimpleVehicleWeighingData implements Serializable, HasLoadedQuantity
{
	private static final long serialVersionUID = 1L;
	public static final String NO_UNIT_OF_MEASUREMENT_SPECIFIED = null;
	
	final Long orderItemId;
	final Long loadingPointId;
	final BigDecimal loadedQuantity;
	final String baseUnitOfMeasurementCode;

	/**
	 * Creates a new simple vehicle load info object.
	 * @param orderItemId The ID of the order item for which something is loaded.
	 * @param loadingPointId The ID of the loading point where the loading is done.
	 * @param loadedQty The quantity of material loaded. The UOM of this quantity is
	 * <ul>
	 * 	<li>the material's base unit of measure for bulk type materials</li>
	 * 	<li>the ordered unit of measure for bag/pallet like materials</li>
	 * </ul>
	 * @throws IllegalStateException if any arguments are null
	 */
	
	public SimpleVehicleWeighingData(Long orderItemId, Long loadingPointId, BigDecimal loadedQty) {
		this(orderItemId, loadingPointId, loadedQty, NO_UNIT_OF_MEASUREMENT_SPECIFIED);
	}
	
	public SimpleVehicleWeighingData(Long orderItemId, Long loadingPointId, 
			BigDecimal loadedQuantity, String baseUnitOfMeasurementCode)
	throws IllegalStateException
	{
		if (orderItemId == null) {
			throw new IllegalStateException("The order item ID cannot be null.");
		}
		if (loadingPointId == null) {
			throw new IllegalStateException("The loading point ID cannot be null.");
		}
		if (loadedQuantity == null) {
			throw new IllegalStateException("The loaded quantity cannot be null.");
		}
		
		this.orderItemId = orderItemId;
		this.loadingPointId = loadingPointId;
		this.loadedQuantity = loadedQuantity;
		this.baseUnitOfMeasurementCode = baseUnitOfMeasurementCode;
	}
	
	/**
	 * @return whether unit of measurement is pre-specified 
	 */
	public boolean isUnitOfMeasurementSpecified() {
		return baseUnitOfMeasurementCode != NO_UNIT_OF_MEASUREMENT_SPECIFIED;
	}
	
	/**
	 * @return the unit of measurement code of the loading data
	 */
	public String getBaseUnitOfMeasurementCode() {
		return baseUnitOfMeasurementCode;
	}

	/**
	 * @return the orderItemId
	 */
	public Long getOrderItemId() {
		return orderItemId;
	}
	/**
	 * @return the loadingPointId
	 */
	public Long getLoadingPointId() {
		return loadingPointId;
	}
	/**
	 * @return the loadedQty
	 */
	public BigDecimal getLoadedQuantity() {
		return loadedQuantity;
	}
}
