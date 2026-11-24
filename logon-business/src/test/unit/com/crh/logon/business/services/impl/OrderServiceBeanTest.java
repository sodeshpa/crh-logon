/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.builders.DeliveryTypeBsEntityBuilder;
import com.crh.logon.persistence.entities.common.builders.OrderEntityBuilder;
import com.crh.logon.persistence.entities.common.builders.OrderStatusBsEntityBuilder;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;

public class OrderServiceBeanTest {
	private static SapContractEntity EXW = new SapContractEntity();;

	private static SapContractEntity FOB = new SapContractEntity();;

	private static SapContractEntity DDP = new SapContractEntity();;

	private static List<DeliveryTypeBsEntityBuilder> pickupDeliveryTypes = Lists.newArrayList(
			DeliveryTypeBsEntityBuilder.DELIVERY_TYPE_PICKUP//
			, DeliveryTypeBsEntityBuilder.DELIVERY_TYPE_SHIP_PICKUP //
			, DeliveryTypeBsEntityBuilder.DELIVERY_TYPE_BLOCKTRAIN_PICKUP//
			, DeliveryTypeBsEntityBuilder.DELIVERY_TYPE_ROAD_DIRECT// with EXW incoterm
	);

	private static List<DeliveryTypeBsEntityBuilder> notPickupDeliveryTypes = Lists.newArrayList(
			DeliveryTypeBsEntityBuilder.DELIVERY_TYPE_RAIL_DIRECT//
			, DeliveryTypeBsEntityBuilder.DELIVERY_TYPE_BIMODAL_RAIL//
			, DeliveryTypeBsEntityBuilder.DELIVERY_TYPE_BIMODAL_ROAD//
			, DeliveryTypeBsEntityBuilder.DELIVERY_TYPE_BLOCK_TRAIN//
			, DeliveryTypeBsEntityBuilder.DELIVERY_TYPE_SHIP//
			, DeliveryTypeBsEntityBuilder.DELIVERY_TYPE_SERVICE//
			, DeliveryTypeBsEntityBuilder.DELIVERY_TYPE_RETURN_PICKUP//
			, DeliveryTypeBsEntityBuilder.DELIVERY_TYPE_RETURN_DELIVERED//
			, DeliveryTypeBsEntityBuilder.DELIVERY_TYPE_ROAD_DIRECT// not with EXW/FOB incoterm
	);

	@BeforeClass
	public static void before() {
		EXW.setIncotermsCd(Constant.INCOTERMS_EXW);

		FOB.setIncotermsCd(Constant.INCOTERMS_FOB);

		DDP.setIncotermsCd(Constant.INCOTERMS_DDP);
	}

	@Test
	public void allowedDeliveryTypesWithoutActualDeliveryDateWithDisallowedOrderStatus() {
		OrderEntityBuilder orderBuilder = OrderEntityBuilder.get()//
		.orderStatus(OrderStatusBsEntityBuilder.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT.build())//
		.actualDeliveryDat(null)//
		.sapContract(EXW);

		String result = checkValidPickups(orderBuilder, pickupDeliveryTypes, false);

		assertEquals("Failed delivery types: " + result, "", result);
	}

	@Test
	public void allowedDeliveryTypesWithActualDeliveryDateWithDisallowedOrderStatus() {
		OrderEntityBuilder orderBuilder = OrderEntityBuilder.get()//
		.orderStatus(OrderStatusBsEntityBuilder.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT.build())//
		.actualDeliveryDat(new Date())//
		.sapContract(EXW);

		String result = checkValidPickups(orderBuilder, pickupDeliveryTypes, false);

		assertEquals("Failed delivery types: " + result, "", result);
	}

	@Test
	public void allowedDeliveryTypesWithoutActualDeliveryDateWithAllowedOrderStatus() {
		OrderEntityBuilder orderBuilder = OrderEntityBuilder.get()//
		.orderStatus(OrderStatusBsEntityBuilder.ORDER_STATUS_DELIVERED.build())//
		.actualDeliveryDat(null)//
		.sapContract(EXW);

		String result = checkValidPickups(orderBuilder, pickupDeliveryTypes, false);

		assertEquals("Failed delivery types: " + result, "", result);

		orderBuilder = OrderEntityBuilder.get()//
		.orderStatus(OrderStatusBsEntityBuilder.ORDER_STATUS_FINISHED.build())//
		.actualDeliveryDat(null)//
		.sapContract(EXW);

		result = checkValidPickups(orderBuilder, pickupDeliveryTypes, false);

		assertEquals("Failed delivery types: " + result, "", result);
	}

	@Test
	public void allowedDeliveryTypesWithActualDeliveryDateWithAllowedOrderStatus() {
		OrderEntityBuilder orderBuilder = OrderEntityBuilder.get()//
		.orderStatus(OrderStatusBsEntityBuilder.ORDER_STATUS_DELIVERED.build())//
		.actualDeliveryDat(new Date())//
		.sapContract(EXW);

		String result = checkValidPickups(orderBuilder, pickupDeliveryTypes, true);

		assertEquals("Failed delivery types: " + result, "", result);

		orderBuilder = OrderEntityBuilder.get()//
		.orderStatus(OrderStatusBsEntityBuilder.ORDER_STATUS_FINISHED.build())//
		.actualDeliveryDat(new Date())//
		.sapContract(EXW);

		result = checkValidPickups(orderBuilder, pickupDeliveryTypes, true);

		assertEquals("Failed delivery types: " + result, "", result);
	}

	@Test
	public void sapContractMissingForRoadDirect() {
		OrderEntityBuilder orderBuilder = OrderEntityBuilder.get()//
		.orderStatus(OrderStatusBsEntityBuilder.ORDER_STATUS_DELIVERED.build())//
		.actualDeliveryDat(new Date())//
		.sapContract(null); // if the contract is null, we expect the check to return false

		// the last param could be just set to false, but I wanted to test a reverse case
		String result = checkValidPickups(orderBuilder,
				Lists.newArrayList(DeliveryTypeBsEntityBuilder.DELIVERY_TYPE_ROAD_DIRECT), true);

		assertEquals("Failed delivery types: " + result, "" + Constant.DELIVERY_TYPE_ROAD_DIRECT,
				result);
	}

	@Test
	public void notPickupTypes() {
		OrderEntityBuilder orderBuilder = OrderEntityBuilder.get() //
		.orderStatus(OrderStatusBsEntityBuilder.ORDER_STATUS_FINISHED.build()) //
		.actualDeliveryDat(new Date())
		.sapContract(DDP);

		String result = checkValidPickups(orderBuilder, notPickupDeliveryTypes, true);

		assertEquals("Failed delivery types: " + result, "", result);
	}

	private static String checkValidPickups(OrderEntityBuilder order,
			List<DeliveryTypeBsEntityBuilder> deliveryTypes, Boolean expected) {
		List<Long> failedDeliveryTypes = Lists.newArrayList();
		for (DeliveryTypeBsEntityBuilder deliveryTypeBuilder : deliveryTypes) {
			DeliveryTypeBsEntity deliveryTypeBs = deliveryTypeBuilder.build();
			order.deliveryType(deliveryTypeBs);

			boolean result = false;
			try {
				result = OrderServiceBean.isValidForSending(order.build());
			}
			catch (Exception e) {
				e.printStackTrace();
				failedDeliveryTypes.add(deliveryTypeBs.getDeliveryTypeCd());
				continue; // doesn't matter at this point whether we matched the expected result or not
			}
			if (expected != result) {
				failedDeliveryTypes.add(deliveryTypeBs.getDeliveryTypeCd());
			}
		}
		return StringUtils.join(failedDeliveryTypes, ",");
	}
}