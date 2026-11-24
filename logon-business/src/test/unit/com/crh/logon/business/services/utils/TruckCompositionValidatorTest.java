/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import static com.crh.logon.constants.Constant.TKATEGORIE_SILO;
import static com.crh.logon.constants.Constant.TRAILER_TKATEGORIE_BAG;
import static com.crh.logon.constants.Constant.TRAILER_TKATEGORIE_BULK_DUMPED;
import static com.crh.logon.constants.Constant.TRAILER_TKATEGORIE_BULK_POWDER;
import static com.crh.logon.constants.Constant.TRAILER_TKATEGORIE_CONCRETE;
import static com.crh.logon.constants.Constant.TRAILER_TKATEGORIE_CONTAINER;
import static com.crh.logon.constants.Constant.TRUCK_TRSP_CAT_BULK_DUMPED;
import static com.crh.logon.constants.Constant.TRUCK_TRSP_CAT_BULK_POWDER;
import static com.crh.logon.constants.Constant.TRUCK_TRSP_CAT_CONCRETE;
import static com.crh.logon.constants.Constant.TRUCK_TRSP_CAT_CONTAINER;
import static com.crh.logon.constants.Constant.TRUCK_TRSP_CAT_GENERAL_FREIGHT;
import static com.crh.logon.constants.Constant.TRUCK_TRSP_CAT_TRAILER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test cases for the validation of truck/trailer/container compositions.
 * @author AdNovum Informatik AG
 */
public class TruckCompositionValidatorTest
{
	@Test
	public void truckThatRequiresNeitherTrailerNorContainerPasses()
	{
		assertTrue(LogonUtil.isTruckCompositionValid(TRUCK_TRSP_CAT_BULK_DUMPED, null, null));
		assertTrue(LogonUtil.isTruckCompositionValid(TRUCK_TRSP_CAT_BULK_POWDER, null, null));
		assertTrue(LogonUtil.isTruckCompositionValid(TRUCK_TRSP_CAT_CONCRETE, null, null));
		assertTrue(LogonUtil.isTruckCompositionValid(TRUCK_TRSP_CAT_GENERAL_FREIGHT, null, null));
		
		// TODO (marius Oct 31, 2012): should these pass?
		// Non-tractor truck, but with trailer
		//assertTrue(isTruckCompositionValid(TRUCK_TRSP_CAT_BULK_DUMPED, TRAILER_TKATEGORIE_BAG, null));		
	}
	
	@Test
	public void containerTruckWithContainerPasses()
	{
		assertTrue(LogonUtil.isTruckCompositionValid(TRUCK_TRSP_CAT_CONTAINER, null, TKATEGORIE_SILO));
	}
	@Test
	public void tractorTruckWithNoncontainerTrailerPasses()
	{
		assertTrue(LogonUtil.isTruckCompositionValid(TRUCK_TRSP_CAT_TRAILER, TRAILER_TKATEGORIE_BAG, null));
		assertTrue(LogonUtil.isTruckCompositionValid(TRUCK_TRSP_CAT_TRAILER, TRAILER_TKATEGORIE_BULK_DUMPED, null));
		assertTrue(LogonUtil.isTruckCompositionValid(TRUCK_TRSP_CAT_TRAILER, TRAILER_TKATEGORIE_BULK_POWDER, null));
		assertTrue(LogonUtil.isTruckCompositionValid(TRUCK_TRSP_CAT_TRAILER, TRAILER_TKATEGORIE_CONCRETE, null));
	}
	
	@Test
	public void tractorTruckWithContainerTrailerWithContainerPasses()
	{
		assertTrue(LogonUtil.isTruckCompositionValid(TRUCK_TRSP_CAT_TRAILER, TRAILER_TKATEGORIE_CONTAINER, TKATEGORIE_SILO));
	}
	@Test
	
	public void tractorTruckWithMissingTrailerFails()
	{
		assertFalse(LogonUtil.isTruckCompositionValid(TRUCK_TRSP_CAT_TRAILER, null, null));
		
		// Adding this check to make sure it is really the trailer that is checked.
		assertFalse(LogonUtil.isTruckCompositionValid(TRUCK_TRSP_CAT_TRAILER, null, TKATEGORIE_SILO));
	}
	
	@Test
	public void containerTruckWithMissingContainerFails()
	{
		assertFalse(LogonUtil.isTruckCompositionValid(TRUCK_TRSP_CAT_CONTAINER, null, null));
		
		// Adding this test to make sure it is really the container that is checked.
		assertFalse(LogonUtil.isTruckCompositionValid(TRUCK_TRSP_CAT_CONTAINER, TRAILER_TKATEGORIE_BAG, null));
	}
	
	@Test
	public void tractorTruckWithContainerTrailerWithoutContainerFails()
	{
		assertFalse(LogonUtil.isTruckCompositionValid(TRUCK_TRSP_CAT_TRAILER, TRAILER_TKATEGORIE_CONTAINER, null));
	}
}
