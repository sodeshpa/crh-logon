/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import static com.crh.logon.business.services.utils.LogonUtil.isTruckCompositionValidForMaterialPackage;
import static com.crh.logon.constants.Constant.PACKAGE_TYPE_BULK_DUMPED;
import static com.crh.logon.constants.Constant.TRAILER_TKATEGORIE_BULK_DUMPED;
import static com.crh.logon.constants.Constant.TRAILER_TKATEGORIE_CONCRETE;
import static com.crh.logon.constants.Constant.TRUCK_TRSP_CAT_BULK_DUMPED;
import static com.crh.logon.constants.Constant.TRUCK_TRSP_CAT_CONCRETE;
import static com.crh.logon.constants.Constant.TRUCK_TRSP_CAT_TRAILER;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.refdata.TrailerTrspCatBsEntity;
import com.crh.logon.persistence.entities.refdata.TruckTransportCatBsEntity;

/**
 * @author AdNovum Informatik AG
 */
public class TruckCompositionPackageTypeValidationTests 
{
	@Test
	public void aggregateMaterialFitsRightTrucks()
	{
		assertTrue(isTruckCompositionValidForMaterialPackage(PACKAGE_TYPE_BULK_DUMPED, makeTruck(TRUCK_TRSP_CAT_BULK_DUMPED), null, null));
		assertTrue(isTruckCompositionValidForMaterialPackage(PACKAGE_TYPE_BULK_DUMPED, makeTruck(TRUCK_TRSP_CAT_CONCRETE), null, null));
	}
	
	@Test
	public void aggregateMaterialFitsRightTrailers()
	{
		final TruckEntity tractor = makeTruck(TRUCK_TRSP_CAT_TRAILER);
		
		assertTrue(isTruckCompositionValidForMaterialPackage(PACKAGE_TYPE_BULK_DUMPED, tractor, makeTrailer(TRAILER_TKATEGORIE_BULK_DUMPED), null));
		assertTrue(isTruckCompositionValidForMaterialPackage(PACKAGE_TYPE_BULK_DUMPED, tractor, makeTrailer(TRAILER_TKATEGORIE_CONCRETE), null));
	}

	private static TruckEntity makeTruck(long trspCat) 
	{
		final TruckEntity truck = new TruckEntity();
		truck.setTruckTransportCat(new TruckTransportCatBsEntity());
		truck.getTruckTransportCat().setTruckTransportCatCd(trspCat);
		return truck;
	}

	private static TrailerEntity makeTrailer(long trspCat) 
	{
		final TrailerEntity trailer = new TrailerEntity();
		trailer.setTrailerTrspCat(new TrailerTrspCatBsEntity());
		trailer.getTrailerTrspCat().setTrailerTrspCatCd(trspCat);
		return trailer;
	}

}
