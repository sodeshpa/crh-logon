/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.constants;

import static com.crh.logon.constants.PackageTypeTrspCatMapping.isTruckCatAllowedForPackageType;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author AdNovum Informatik AG
 */
public class PackageTypeTrspCatMappingTests 
{
	@Test
	public void iCanCallAMethodWithoutAnException()
	{
		assertTrue(isTruckCatAllowedForPackageType(Constant.TRUCK_TRSP_CAT_TRAILER, Constant.PACKAGE_TYPE_BULK_POWDER));
		assertFalse(isTruckCatAllowedForPackageType(Constant.TRUCK_TRSP_CAT_GENERAL_FREIGHT, Constant.PACKAGE_TYPE_BULK_POWDER));
	}
}
