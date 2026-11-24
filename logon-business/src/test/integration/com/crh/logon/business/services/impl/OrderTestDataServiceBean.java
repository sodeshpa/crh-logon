/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Order;

import com.crh.logon.business.services.interfaces.OrderTestDataService;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.SimpleVehicleEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.refdata.CurrencyBsEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.DistributionDestBsEntity;
import com.crh.logon.persistence.entities.refdata.DivisionBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.utils.DaoProvider;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
public class OrderTestDataServiceBean implements OrderTestDataService {

	@PersistenceContext(unitName = "logon")
	EntityManager em;

	static final long TEST_SAP_MATERIAL_ID = -1040;
	static final long TEST_DENOMINATOR = 1;
	static final long TEST_NUMERATOR = 1;
	static final ch.nevis.gdi.jpa.Authorizer DO_NOT_AUTHORIZE = null;
	static final String TEST_VENDOR_SAP_NO = "TEST_VENDOR_SAP_NO";
	static final long TEST_ORDER_ITEM_NUMBER = 123456789;
	static final long TEST_SHIPTO_SAP_BP_ID = -41;
	static final long TEST_SHIPPING_POINT_ID = 1000;
	static final String TEST_TRAILER_PLATE_NUMBER = "TEST_TRAILER_PLATE_NUMBER";
	static final BigDecimal TEST_DELIVERED_QUANTITY = new BigDecimal(1);
	static final long TEST_ORDER_NUMBER = 1234567;

	public OrderEntity createOrderForTransferDeliveryDocumentGenerationTesting() {
		OrderEntity testOrder = new OrderEntity();
		testOrder.setOrderNumber(TEST_ORDER_NUMBER);

		setupDummyOrderItems(testOrder);
		setupDummyDelivery(testOrder);
		setupDummySarea(testOrder);
		setupDummyShippingPoint(testOrder);
		setupDummyContract(testOrder);

		testOrder.setSapSuppressMinChargeFlag(Constant.ON);

		return testOrder;
	}

	private void setupDummyOrderItems(OrderEntity testOrder) {
		List<OrderItemEntity> orderItems = new ArrayList<OrderItemEntity>();
		OrderItemEntity orderItem = createDummyOrderItem(testOrder);
		orderItems.add(orderItem);
		testOrder.setOrderItems(orderItems);
	}

	private void setupDummyDelivery(OrderEntity testOrder) {
		SapBpEntity shiptoSapBp = new SapBpEntity();
		shiptoSapBp.setSapBpId(TEST_SHIPTO_SAP_BP_ID);
		testOrder.setShiptoSapBp(shiptoSapBp);

		DeliveryTypeBsEntity deliveryType = new DeliveryTypeBsEntity();
		deliveryType.setDeliveryTypeCd(Constant.DELIVERY_TYPE_ROAD_DIRECT);
		testOrder.setDeliveryType(deliveryType);

		DistributionDestBsEntity distributionDest = new DistributionDestBsEntity();
		distributionDest.setDistributionDestCd(Constant.DISTRIBUTION_DEST_OUTBOUND);
		testOrder.setDistributionDest(distributionDest);

		SimpleVehicleEntity deliveredSimpleVehicle = new SimpleVehicleEntity();
		testOrder.setDeliveredSimpleVehicle(deliveredSimpleVehicle);

		TrailerEntity deliveredTrailer = new TrailerEntity();
		deliveredTrailer.setTrailerPlateNo(TEST_TRAILER_PLATE_NUMBER);
		testOrder.setDeliveredTrailer(deliveredTrailer);
	}

	private void setupDummyShippingPoint(OrderEntity testOrder) {
		ShippingpointEntity shippingpoint = new ShippingpointEntity();
		shippingpoint.setShippingpointId(TEST_SHIPPING_POINT_ID);
		testOrder.setShippingpoint(shippingpoint);
	}

	private void setupDummyContract(OrderEntity testOrder) {
		SapContractEntity sapContract = new SapContractEntity();
		sapContract.setVendorSapNo(TEST_VENDOR_SAP_NO);
		testOrder.setSapContract(sapContract);
	}

	private void setupDummySarea(OrderEntity testOrder) {
		SareaEntity sarea = new SareaEntity();
		DivisionBsEntity division = new DivisionBsEntity();
		division.setDivisionCd(Constant.DIVISON_CD_CEMENT);
		sarea.setDivision(division);
		testOrder.setSarea(sarea);
	}

	private OrderItemEntity createDummyOrderItem(OrderEntity testOrder) {
		UomBsEntity baseUom = new UomBsEntity();
		baseUom.setUomCd(Constant.UOM_BAG);
		baseUom.setFuzzyQuantityFlag(Boolean.FALSE);
		DaoProvider daoProvider = new DaoProvider(em, DO_NOT_AUTHORIZE);
		ReadOnlyDao<SapMaterialEntity> sapMaterialDao = daoProvider.getSapMaterialEntityDao();
		SapMaterialEntity sapMaterial = sapMaterialDao.findById(TEST_SAP_MATERIAL_ID);
		OrderItemEntity orderItem = new OrderItemEntity();
		orderItem.setOrderItemNo(TEST_ORDER_ITEM_NUMBER);
		orderItem.setSapMaterial(sapMaterial);
		orderItem.setOrder(testOrder);
		orderItem.setDeliveredUom(baseUom);
		orderItem.setDeliveredQuantity(TEST_DELIVERED_QUANTITY);
		orderItem.setLrUomDenominator(TEST_DENOMINATOR);
		orderItem.setLrUomNumerator(TEST_NUMERATOR);
		List<LoadEntity> loads = new ArrayList<LoadEntity>();
		ReadOnlyDao<LoadEntity> loadDao = daoProvider.getLoadEntityDao();
		Filter<LoadEntity> filter = loadDao.createFilter();
		filter.addExpression(Order.desc(LoadEntity.ATTR_CTL_CRE_TS));
		loads = loadDao.findNRecs(0, 1, filter);
		LoadEntity load = loads.get(0);
		orderItem.setLoad(load);
		CurrencyBsEntity currency = new CurrencyBsEntity();
		currency.setCurrencyCd(Constant.CURRENCY_CODE_EUR);
		orderItem.setCurrency(currency);
		return orderItem;
	}
		
}
