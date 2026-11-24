/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import com.crh.logon.business.ordercreation.dto.TruckTypeWrapper;
import com.crh.logon.business.ordercreation.dto.TruckWagonTypeWrapper;
import com.crh.logon.persistence.entities.common.ShiptoWagonTypeEntity;
import com.crh.logon.persistence.entities.refdata.TruckTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.TruckTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.TruckTypeTxPK;

/**
 * @author AdNovum Informatik AG
 */
public class TruckWagonTypeWrapperUtil {

	/**
	 * Finds the truck/wagon type wrapper of highest priority among a given list.
	 * @param vehicleTypeWrapperList - list of possible trucks
	 * @return the selected truck. If the input list is empty or if all trucks are disabled,
	 * null is returned.
	 */
	public static TruckWagonTypeWrapper findProperTruck(List<TruckWagonTypeWrapper> vehicleTypeWrapperList)
	{
		if (vehicleTypeWrapperList == null) {
			throw new IllegalArgumentException("input list must not be null");
		}
		
		if (vehicleTypeWrapperList.isEmpty()) {
			return null;
		}
		else {
			final TruckWagonTypeWrapper best = Collections.max(vehicleTypeWrapperList);
			if (best.getPriority() != 0) {
				return best;
			}
			else {
				// All are disabled
				return null;
			}
		}
	}
	
	
	/**
	 * Compares two TruckWagonTypeWrappers according to business rules.
	 * This method compares the two vehicle types, taking into account their
	 * evaluation priority, planning weight, and volume.
	 * @param t1 the first TruckWagonTypeWrapper to compare
	 * @param t2 the second TruckWagonTypeWrapper to compare
	 * @return The returned value is &gt; 0 if t1 should be preferred to t2,
	 * &lt; 0 if t2 should be preferred to t1, and equal to 0 if it doesn't matter.
	 */
	public static final int compareTruckTypeEntity(TruckWagonTypeWrapper t1, TruckWagonTypeWrapper t2)
	{
		final Long t1Prio = t1.getPriority();
		final Long t2Prio = t2.getPriority();
		final BigDecimal t1Capacity = t1.getMaxGrossWeightT();
		final BigDecimal t2Capacity = t2.getMaxGrossWeightT();
		final BigDecimal t1Volume = t1.getVolumeM3();
		final BigDecimal t2Volume = t2.getVolumeM3();
		final Long t1Cd = t1.getTypeCd();
		final Long t2Cd = t2.getTypeCd();
		
		return genericVehicleCompareTo(t1Prio, t2Prio, t1Capacity, t2Capacity, t1Volume, t2Volume,
				t1Cd, t2Cd);
	}


	/**
	 * Compares two ShiptoWagonTypeEntities according to business rules.
	 * This method compares two shipto wagon truck types, taking into account their
	 * evaluation priority, planning weight, and volume.
	 * @param t1 a shipto wagon type
	 * @param t2 another shipto wagon type
	 * @return The returned value is &gt; 0 if t1 should be preferred to t2,
	 * &lt; 0 if t2 should be preferred to t1, and equal to 0 if it doesn't matter.
	 */
	public static final int compareWagonTypeEntity(ShiptoWagonTypeEntity t1, ShiptoWagonTypeEntity t2)
	{
		final Long t1Prio = t1.getEvaluationPriority();
		final Long t2Prio = t2.getEvaluationPriority();
		final BigDecimal t1Capacity = t1.getWagonType().getMaxGrossWeightT();
		final BigDecimal t2Capacity = t2.getWagonType().getMaxGrossWeightT();
		final BigDecimal t1Volume = t1.getWagonType().getVolumeM3();
		final BigDecimal t2Volume = t2.getWagonType().getVolumeM3();
		final Long t1Cd = t1.getWagonType().getWagonTypeCd();
		final Long t2Cd = t2.getWagonType().getWagonTypeCd();
		
		return genericVehicleCompareTo(t1Prio, t2Prio, t1Capacity, t2Capacity, t1Volume, t2Volume,
				t1Cd, t2Cd);
	}


	/**
	 * Compares two vehicles according to which one should be
	 * preferred. This is a general method; if you want to compare other
	 * vehicle types than the ones above, make a new method and make it
	 * call this generic one.
	 * @param t1Prio The evaluation priority of the first vehicle
	 * @param t2Prio The evaluation priority of the second vehicle
	 * @param t1Capacity The planning weight capacity of the first vehicle
	 * @param t2Capacity The planning weight capacity of the second vehicle
	 * @param t1Volume The volume of the first vehicle
	 * @param t2Volume The volume of the second vehicle
	 * @param t1Cd The type code of the first vehicle
	 * @param t2Cd The type code of the second vehicle
	 * @return The returned value is &gt; 0 if the first vehicle should be preferred,
	 * &lt; 0 if the second one should be preferred, and 0 if it doesn't matter.
	 */
	public static int genericVehicleCompareTo(final Long t1Prio, final Long t2Prio,
			final BigDecimal t1Capacity, final BigDecimal t2Capacity, final BigDecimal t1Volume,
			final BigDecimal t2Volume, final Long t1Cd, final Long t2Cd) 
	{
		if (!t1Prio.equals(t2Prio)) {
			return compareVehiclePrio(t1Prio, t2Prio);
		}
		else if (!t1Capacity.equals(t2Capacity)) {
			return t1Capacity.compareTo(t2Capacity);
		}
		else if (t1Volume != null && t2Volume == null) {
			// If only one has nonnull volume, it is always higher.
			return 1;
		}
		else if (t2Volume != null && t1Volume == null) {
			return -1;
		}
		else if (t1Volume != null && t2Volume != null) {
			// Normal numeric comparison
			return t1Volume.compareTo(t2Volume);
		}
		else {
			// Both are null, we use truckTypeCd to have a unique comparison
			
			return t1Cd.compareTo(t2Cd);
		}
	}
	
	
	/**
	 * Compares two vehicle evaluation priorities, taking into account
	 * the special values -1 for "no priority specified" and 0 for
	 * "disabled".
	 * @param p1 a vehicle evaluation priority
	 * @param p2 another vehicle evaluation priority
	 * @return 1 if is the higher priority, -1 if p2 is the 
	 * higher priority, and 0 if p1 and p2 are equal. 
	 */
	static final int compareVehiclePrio(long p1, long p2)
	{
		if (p1 == p2) {
			return 0;
		}
		else if (p1 == 0L) {
			// p1 is disabled but p2 isn't, so p2 is higher
			return -1;
		}
		else if (p2 == 0L) {
			// p2 is disabled but p1 isn't, so p1 is higher
			return 1;
		}
		else if (p1 < 0) {
			// p2 has explicit prio, so it is always higher
			return -1;
		}
		else if (p2 < 0) {
			// p1 has explicit prio but p2 has implicit, so p1 is higher
			return 1;
		}
		else {
			// We need to revert the order because smaller numer == higher prio
			return Long.valueOf(p2).compareTo(Long.valueOf(p1));
		}
	}
	
	/**
	 * Used for disable splitting.
	 * 
	 * This is a weird thing, that we disable splitting by: 
	 * - setting the truck's capacity to the maximum allowed and 
	 * - setting custorderitem's perSdorderQuantity and perSdorderUomCd to null
	 * 
	 * We should rather explicit skip the split, however, the splitCustorder is not only doing
	 * splitting, but other things as well which should not skipped at all.
	 * 
	 * @param planningNetWeightInTons
	 * @return
	 */
	public static TruckWagonTypeWrapper getDummyWrapper(BigDecimal planningNetWeightInTons) {
		TruckTypeBsEntity truckTypeBs = new TruckTypeBsEntity();
		truckTypeBs.setTruckTypeCd(-1L);
		truckTypeBs.setPlanningNetWeightT(planningNetWeightInTons);
		truckTypeBs.setTruckTypeShortName("dmy");
		TruckTypeTxPK truckTypeTxPK = new TruckTypeTxPK();
		truckTypeTxPK.setLangCd("en");
		truckTypeTxPK.setTruckTypeBs(truckTypeBs);
		TruckTypeTxEntity truckTypeTx = new TruckTypeTxEntity();
		truckTypeTx.setTxt("DummyTruck");
		truckTypeTx.setTruckTypeTxPK(truckTypeTxPK);
		TruckTypeWrapper truckWrapper = new TruckTypeWrapper(1L, truckTypeBs, null);
		
		return truckWrapper;
	}
}