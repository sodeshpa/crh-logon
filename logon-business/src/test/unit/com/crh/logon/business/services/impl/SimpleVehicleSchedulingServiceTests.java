/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.crh.logon.business.services.beans.SimpleVehicleAssignmentInfo;
import com.crh.logon.business.services.beans.UnallocatedOrderData;
import org.junit.Test;

import com.crh.logon.persistence.entities.views.SimpleVehicleAssgEntity;
import com.crh.logon.persistence.entities.views.SimpvehOrdVhcEntity;
import com.crh.logon.persistence.entities.views.SimpvehUnalcOrdEntity;


/**
 * @author AdNovum Informatik AG
 */
public class SimpleVehicleSchedulingServiceTests
{
	@Test
	public void correctSimpleVehicleDataForVehicleWithoutAssignment()
	{
		final List<SimpleVehicleAssgEntity> viewResults = makeVehicleAssgViewResultWithoutAssignment();
		final Collection<SimpleVehicleAssignmentInfo> assgInfos;
		
		assgInfos = SimpleVehicleSchedulingServiceBean.createSimpleVehicleAssgInfos(viewResults);
		
		assertEquals(1, assgInfos.size());
		
		final SimpleVehicleAssignmentInfo info = assgInfos.iterator().next();
		
		assertNull(info.getAssignmentId());
		assertTrue(info.getParts().isEmpty());
	}
	
	@Test
	public void correctUnallocatedOrdersInSimplestCase()
	{
		final SimpvehUnalcOrdEntity orderRow = new SimpvehUnalcOrdEntity();
		orderRow.setOrderId(Long.valueOf(1));
		orderRow.setCustorderNumber(Long.valueOf(123456));
		orderRow.setPoolId(null);
		orderRow.setCustorderNumber(Long.valueOf(20000037));
		orderRow.setNoteDispatching(null);
		orderRow.setMatDesc("material");
		orderRow.setOrderedQuantity(BigDecimal.valueOf(40));
		orderRow.setOrderedUomTxt("tons");
		orderRow.setDeliveryFromDat(new Date());
		orderRow.setDeliveryToDat(new Date());
		orderRow.setSoldtoName("soldTo");
		orderRow.setShiptoName("shipTo");
		orderRow.setCarrierName("test carrier");
		orderRow.setLangCd("en");
		
		final SimpvehOrdVhcEntity vehicleRow = new SimpvehOrdVhcEntity();
		vehicleRow.setCompositeId("1_2");
		vehicleRow.setOrderId(Long.valueOf(1));
		vehicleRow.setSimpleVehicleId(Long.valueOf(2));
		vehicleRow.setVehicleName("my ship");
		vehicleRow.setCarrierId(Long.valueOf(77));
		
		final List<SimpvehUnalcOrdEntity> orderResults = new LinkedList<SimpvehUnalcOrdEntity>();
		final List<SimpvehOrdVhcEntity> vehicleResults = new LinkedList<SimpvehOrdVhcEntity>();
		orderResults.add(orderRow);
		vehicleResults.add(vehicleRow);
		
		final Collection<UnallocatedOrderData> orderData = SimpleVehicleSchedulingServiceBean.makeUnallocatedOrderDataList(orderResults, vehicleResults);
		
		// Exactly one UnallocatedOrderData object must be created.
		assertEquals(1, orderData.size());
		
		// There should be exactly one vehicle.
		final UnallocatedOrderData firstData = orderData.iterator().next();
		assertEquals(1, firstData.getVehicles().size());
	}

	/**
	 * @return a list with a single view result corresponding
	 * to a simple vehicle for which there aren't any assignments
	 */
	private List<SimpleVehicleAssgEntity> makeVehicleAssgViewResultWithoutAssignment() {
		final List<SimpleVehicleAssgEntity> viewResults = new LinkedList<SimpleVehicleAssgEntity>();
		
		final SimpleVehicleAssgEntity row1 = new SimpleVehicleAssgEntity();
		row1.setCompositeId("xxx"); // not important
		row1.setSimpleVehicleId(Long.valueOf(1));
		row1.setVehicleName("Ship 1");
		row1.setVehicleRegNo("SHIP1");
		row1.setSimpleVehicleTypeCd(Long.valueOf(1)); // Ship
		row1.setVehicleTypeTx("Ship");
		row1.setPriority(Long.valueOf(0));
		row1.setMaxGrossWeightT(null);
		row1.setTareWeightT(BigDecimal.valueOf(23));
		row1.setCarrierId(Long.valueOf(77));
		row1.setCarrierName("ShipCarrier");
		row1.setAssignmentId(null);
		row1.setAssignmentDat(null);
		row1.setOrderId(null);
		row1.setOrderNumber(null);
		row1.setShipto(null);
		row1.setLangCd(null);
		
		viewResults.add(row1);
		return viewResults;
	}
}
