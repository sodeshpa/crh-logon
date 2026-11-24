package com.crh.logon.unittest;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.crh.logon.business.services.utils.CreateCustOrderFromDefaultMasterSecondRoundRankingComparator;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.PoolEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import org.junit.Test;

/**
 * @author AdNovum Informatik AG
 */
public class ShiptoSareaRestrEntityComparatorTest {


	private static Long BEST_SHIPTO_SAREA_REST_ENTITY_ID = 50L;

	@Test
	public void testCase1() {
		//TODO: add meaningful test function name
		List<ShiptoSareaRestrEntity> testShiptoSareaRestrEntities = getTestList();

		printAllElementsToStout(testShiptoSareaRestrEntities);

		ShiptoSareaRestrEntity shiptoSareaRestrEntity1 = Collections.max(testShiptoSareaRestrEntities, new
				CreateCustOrderFromDefaultMasterSecondRoundRankingComparator());

		System.out.println("Collections.max() of the input list: " + shiptoSareaRestrEntity1.toString());
		assertEquals(shiptoSareaRestrEntity1.getShiptoSareaRestrId(), BEST_SHIPTO_SAREA_REST_ENTITY_ID);
	}

	private void printAllElementsToStout(List<ShiptoSareaRestrEntity> inputList) {
		//just for debugging
		System.out.println("##################################/nElements:");
		for (ShiptoSareaRestrEntity element : inputList) {
			System.out.println(element);
		}
		System.out.println("##################################");
	}

	private List<ShiptoSareaRestrEntity> getTestList() {
		List<ShiptoSareaRestrEntity> shiptoSareaRestrEntityList = new ArrayList<ShiptoSareaRestrEntity>();

		//test data 1: everything is null -> lowest value
		ShiptoSareaRestrEntity s1 = new ShiptoSareaRestrEntity();
		s1.setShiptoSareaRestrId(1L);
		s1.setCtlCreTs(new Date());
		shiptoSareaRestrEntityList.add(s1);

		//test data 3: only pool is available -> 2. lowest value
		ShiptoSareaRestrEntity s2 = new ShiptoSareaRestrEntity();
		s2.setShiptoSareaRestrId(2L);
		s2.setPool(new PoolEntity());
		s2.setCtlCreTs(new Date());
		shiptoSareaRestrEntityList.add(s2);

		//test data 3: pool and pool flag available -> 3. lowest value
		ShiptoSareaRestrEntity s3 = new ShiptoSareaRestrEntity();
		s3.setShiptoSareaRestrId(3L);
		s3.setPool(new PoolEntity());
		s3.setAutoPoolAssignmentFlag(true);
		s3.setCtlCreTs(new Date());
		shiptoSareaRestrEntityList.add(s3);

		//test data 4: every field is available + date is the latest -> highest value -> winner
		ShiptoSareaRestrEntity winnerShiptoSareaRestrEntity = new ShiptoSareaRestrEntity();
		winnerShiptoSareaRestrEntity.setShiptoSareaRestrId(BEST_SHIPTO_SAREA_REST_ENTITY_ID);
		winnerShiptoSareaRestrEntity.setPool(new PoolEntity());
		winnerShiptoSareaRestrEntity.setExecCarrier(new CarrierEntity());
		winnerShiptoSareaRestrEntity.setCommCarrier(new CarrierEntity());
		winnerShiptoSareaRestrEntity.setAutoPoolAssignmentFlag(true);
		winnerShiptoSareaRestrEntity.setAutoCarrierAssignmentFlag(true);
		Calendar c = new GregorianCalendar();
		c.add(Calendar.DATE, 2);    //2 days after today -> latest created one
		winnerShiptoSareaRestrEntity.setCtlCreTs(c.getTime());
		shiptoSareaRestrEntityList.add(winnerShiptoSareaRestrEntity);

		//test data 4: every field is available + date is the current timestamp -> 2. highest value
		ShiptoSareaRestrEntity s4 = new ShiptoSareaRestrEntity();
		s4.setShiptoSareaRestrId(4L);
		s4.setPool(new PoolEntity());
		s4.setExecCarrier(new CarrierEntity());
		s4.setCommCarrier(new CarrierEntity());
		s4.setAutoPoolAssignmentFlag(true);
		s4.setAutoCarrierAssignmentFlag(true);
		s4.setCtlCreTs(new Date());
		shiptoSareaRestrEntityList.add(s4);

		return shiptoSareaRestrEntityList;
	}
}