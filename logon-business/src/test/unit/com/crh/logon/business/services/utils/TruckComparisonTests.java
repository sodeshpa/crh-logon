/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import static com.crh.logon.business.services.utils.TruckWagonTypeWrapperUtil.compareVehiclePrio;
import static com.crh.logon.business.services.utils.TruckWagonTypeWrapperUtil.genericVehicleCompareTo;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

/**
 * @author AdNovum Informatik AG
 */
public class TruckComparisonTests 
{
	final static BigDecimal smallCapacity = BigDecimal.ONE;
	final static BigDecimal bigCapacity = BigDecimal.TEN;
	final static BigDecimal smallVolume = BigDecimal.ONE;
	final static BigDecimal bigVolume = BigDecimal.TEN;
	final static Long someCode = 1L;

	@Test
	public void explicitPriorityIsAlwaysHigherThanUnspecifiedPriority()
	{
		/* A strictly positive priority means it has been explicitly
		 * defined by a user.
		 * A negative priority (normally -1) means that the vehicle type
		 * has not yet been given an explicit priority, but also
		 * has not been explicitly disabled. 
		 * Any explicit priority must always be higher than an unspecified one.
		 */
		assertTrue("Priority 1 must be higher than -1", compareVehiclePrio(1, -1) > 0);
		assertTrue("{riority 99 must be higher than -1", compareVehiclePrio(99, -1) > 0);
		assertTrue("Priority -1 must be lower than 1", compareVehiclePrio(-1, 1) < 0);
		assertTrue("Priority -1 must be lower than 99", compareVehiclePrio(-1, 99) < 0);
	}
	
	@Test
	public void twoDisabledPrioritiesAreEqual()
	{
		assertTrue("Two zero (disabled) priorities must be equal", compareVehiclePrio(0, 0) == 0);
	}

	@Test
	public void twoUnspecifiedPrioritiesAreEqual()
	{
		assertTrue("Two -1 (unspecified) priorities must be equal", compareVehiclePrio(-1, -1) == 0);
	}

	@Test
	public void twoEqualPositivePrioritiesAreComparedAsEqual()
	{
		assertTrue("25 and 25 should be treated as the same priority", compareVehiclePrio(25, 25) == 0);
	}

	@Test
	public void strictlyPositivePrioritiesAreCorrectlyCompared()
	{
		assertTrue("1 should be higher priority than 99", compareVehiclePrio(1, 99) > 0);
		assertTrue("1 should be higher priority than 2", compareVehiclePrio(1, 2) > 0);
		assertTrue("2 should be lower priority than 1", compareVehiclePrio(2, 1) < 0);
		assertTrue("99 should be lower priority than 2", compareVehiclePrio(99, 2) < 0);
	}
	
	@Test
	public void trucksWithDifferentPrioritiesAreSortedByPriorityNotCapacity()
	{
		
		// Check that the priority overrides the capacity 
		assertTrue(genericVehicleCompareTo(1L, 10L, smallCapacity, bigCapacity, smallVolume, smallVolume, someCode, someCode) > 0);
		assertTrue(genericVehicleCompareTo(10L, 1L, smallCapacity, bigCapacity, smallVolume, smallVolume, someCode, someCode) < 0);
	}
	
	@Test
	public void trucksWithEqualPrioAreSortedByCapacity()
	{
		assertTrue(genericVehicleCompareTo(1L, 1L, smallCapacity, bigCapacity, smallVolume, bigVolume, someCode, someCode) < 0);
		assertTrue(genericVehicleCompareTo(1L, 1L, smallCapacity, bigCapacity, bigVolume, smallVolume, someCode, someCode) < 0);
		assertTrue(genericVehicleCompareTo(1L, 1L, bigCapacity, smallCapacity, smallVolume, bigVolume, someCode, someCode) > 0);
		assertTrue(genericVehicleCompareTo(1L, 1L, bigCapacity, smallCapacity, bigVolume, smallVolume, someCode, someCode) > 0);
	}

	@Test
	public void trucksWithEqualPrioAndCapacityAreSortedByVolume()
	{
		assertTrue(genericVehicleCompareTo(1L, 1L, smallCapacity, smallCapacity, smallVolume, bigVolume, someCode, someCode) < 0);
		assertTrue(genericVehicleCompareTo(1L, 1L, smallCapacity, smallCapacity, bigVolume, smallVolume, someCode, someCode) > 0);
	}

}
