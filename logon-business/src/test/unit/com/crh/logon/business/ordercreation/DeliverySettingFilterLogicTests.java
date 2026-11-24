/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ordercreation;

import static com.crh.logon.constants.PackageTypeTrspCatMapping.getTruckTrspCatForPackageType;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.crh.logon.constants.Constant;

/**
 * @author AdNovum Informatik AG
 */
public class DeliverySettingFilterLogicTests 
{
	// Uncomment to see debug output during test in Eclipse.
//	@Before
//	public void init()
//	{
//		Logger.getRootLogger().setLevel(Level.DEBUG);
//	}
	
	// Constants for better readability
	final private static long PKG_BULK_POWDER = Constant.PACKAGE_TYPE_BULK_POWDER;
	final private static long PKG_BAG         = Constant.PACKAGE_TYPE_BBAG;
	final private static long PKG_SERVICE     = Constant.PACKAGE_TYPE_SERVICE;
	final private static long PKG_BULK_DUMPED = Constant.PACKAGE_TYPE_BULK_DUMPED;
	
	final private static long TRSP_BULK_POWDER = Constant.TRUCK_TRSP_CAT_BULK_POWDER;
	final private static long TRSP_CONTAINER   = Constant.TRUCK_TRSP_CAT_CONTAINER;
	final private static long TRSP_TRAILER     = Constant.TRUCK_TRSP_CAT_TRAILER;
	final private static long TRSP_GENERAL     = Constant.TRUCK_TRSP_CAT_GENERAL_FREIGHT;
	final private static long TRSP_BULK_DUMPED = Constant.TRUCK_TRSP_CAT_BULK_DUMPED;
	final private static long TRSP_CONCRETE    = Constant.TRUCK_TRSP_CAT_CONCRETE;
	
	
	@Test
	public void getTruckTrspCatForPackageTypeForBulkPowderPackageTypeAreCorrect()
	{
		// See Table 5 in SDS for LRQ 185.0/185.1 (LOGON-5043).
		assertTrue(getTruckTrspCatForPackageType(PKG_BULK_POWDER).contains(TRSP_BULK_POWDER));
		assertTrue(getTruckTrspCatForPackageType(PKG_BULK_POWDER).contains(TRSP_CONTAINER));
		assertFalse(getTruckTrspCatForPackageType(PKG_BULK_POWDER).contains(TRSP_GENERAL));
		assertFalse(getTruckTrspCatForPackageType(PKG_BULK_POWDER).contains(TRSP_BULK_DUMPED));
		assertFalse(getTruckTrspCatForPackageType(PKG_BULK_POWDER).contains(TRSP_CONCRETE));

		assertFalse(getTruckTrspCatForPackageType(PKG_BAG).contains(TRSP_BULK_POWDER));
		assertFalse(getTruckTrspCatForPackageType(PKG_BAG).contains(TRSP_CONTAINER));
		assertTrue(getTruckTrspCatForPackageType(PKG_BAG).contains(TRSP_GENERAL));
		assertTrue(getTruckTrspCatForPackageType(PKG_BAG).contains(TRSP_BULK_DUMPED));
		assertFalse(getTruckTrspCatForPackageType(PKG_BAG).contains(TRSP_CONCRETE));

		assertFalse(getTruckTrspCatForPackageType(PKG_BULK_DUMPED).contains(TRSP_BULK_POWDER));
		assertFalse(getTruckTrspCatForPackageType(PKG_BULK_DUMPED).contains(TRSP_CONTAINER));
		assertFalse(getTruckTrspCatForPackageType(PKG_BULK_DUMPED).contains(TRSP_GENERAL));
		assertTrue(getTruckTrspCatForPackageType(PKG_BULK_DUMPED).contains(TRSP_BULK_DUMPED));
		assertTrue(getTruckTrspCatForPackageType(PKG_BULK_DUMPED).contains(TRSP_CONCRETE));

	}
	
	@Test
	public void trailerTruckIsSuitableForAllPackageTypes()
	{
		assertTrue(getTruckTrspCatForPackageType(PKG_BULK_POWDER).contains(TRSP_TRAILER));
		assertTrue(getTruckTrspCatForPackageType(PKG_BAG        ).contains(TRSP_TRAILER));
		assertTrue(getTruckTrspCatForPackageType(PKG_BULK_DUMPED).contains(TRSP_TRAILER));
		// Except for service package type
	}
	
	@Test
	public void getTruckTrspCatForPackageTypeForServicePackageTypeIsEmpty()
	{
		assertTrue(getTruckTrspCatForPackageType(PKG_SERVICE).isEmpty());
	}
	
	@Test
	public void truckAndPackageTypeMatchingIsCorrect()
	{
		// Build a sample map of truck types to transport categories
		Map<Long,Set<Long>> truckTrspMap = new HashMap<Long,Set<Long>>();
		Set<Long> setOne = new HashSet<Long>();
		Set<Long> setTwo = new HashSet<Long>();
		
		final Long TRUCK1 = Long.valueOf(1);
		final Long TRUCK2 = Long.valueOf(2);
		
		setOne.add(TRSP_BULK_POWDER);
		setOne.add(TRSP_CONTAINER);
		setTwo.add(TRSP_CONTAINER);
		setTwo.add(TRSP_GENERAL);
		
		truckTrspMap.put(Long.valueOf(TRUCK1), setOne);
		truckTrspMap.put(Long.valueOf(TRUCK2), setTwo);
		
		assertTrue(DeliverySettingFilterLogic.truckTypeSuitsPackageType(TRUCK1, PKG_BULK_POWDER, truckTrspMap));
		assertFalse(DeliverySettingFilterLogic.truckTypeSuitsPackageType(TRUCK1, PKG_BAG, truckTrspMap));
		assertFalse(DeliverySettingFilterLogic.truckTypeSuitsPackageType(TRUCK1, PKG_BULK_DUMPED, truckTrspMap));
		assertFalse(DeliverySettingFilterLogic.truckTypeSuitsPackageType(TRUCK1, PKG_SERVICE, truckTrspMap));
		
		assertTrue(DeliverySettingFilterLogic.truckTypeSuitsPackageType(TRUCK2, PKG_BULK_POWDER, truckTrspMap));
		assertTrue(DeliverySettingFilterLogic.truckTypeSuitsPackageType(TRUCK2, PKG_BAG, truckTrspMap));
		assertFalse(DeliverySettingFilterLogic.truckTypeSuitsPackageType(TRUCK2, PKG_BULK_DUMPED, truckTrspMap));
	}
}
