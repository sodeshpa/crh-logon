/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.ejbtest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.crh.logon.constants.Constant;
import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.ordercreation.dto.TruckTypeWrapper;
import com.crh.logon.business.ordercreation.dto.TruckWagonTypeWrapper;
import com.crh.logon.business.ordercreation.dto.WagonTypeWrapper;
import com.crh.logon.business.services.interfaces.CustorderService;
import com.crh.logon.business.services.interfaces.OrderSpecialService;
import com.crh.logon.business.services.interfaces.RefDataGenericsService;
import com.crh.logon.business.services.utils.CustorderSplitWrapper;
import com.crh.logon.business.services.utils.CustorderUtils;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.CustorderItemEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.TruckTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.TruckTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.TruckTypeTxPK;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.WagonTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.WagonTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.WagonTypeTxPK;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;

/**
 * @author AdNovum Informatik AG
 */
public class CustorderServiceTestCase extends JunitSupport {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(
			CustorderServiceTestCase.class);
	
	private static final Long TEST_SAP_BULK_MATERIAL_ID = -1054L;
	private static final Long TEST_SAP_BAG_MATERIAL_ID_1 = -2401L;
	private static final Long TEST_SAP_BAG_MATERIAL_ID_2 = -2701L;
	private static final Long TEST_SAP_BAG_MATERIAL_ID_3 = -1049L;
	private static final Long TEST_SAP_SERVICE_MATERIAL = -9992L;
	
	private UomBsEntity UOM_TO;
	private UomBsEntity UOM_LKW;
	private UomBsEntity UOM_BAG;
	private UomBsEntity UOM_PAL;
	private UomBsEntity UOM_PCE;
	private UomBsEntity UOM_CONTAINER;
	private UomBsEntity UOM_WAGON;
	
	SapMaterialEntity bulkSapMaterial;
	SapMaterialEntity bagSapMaterial1;
	SapMaterialEntity bagSapMaterial2;
	SapMaterialEntity bagSapMaterial3;
	SapMaterialEntity serviceSapMaterial;
	
	CustorderItemEntity bulkCustorderItem;
	CustorderItemEntity bagCustorderItem1;
	CustorderItemEntity bagCustorderItem2;
	CustorderItemEntity bagCustorderItem3;
	CustorderItemEntity serviceCustorderItem;
	
	CustorderEntity custorderEntity;
	
	List<CustorderItemEntity> serviceCustorderItems;
	
	TruckTypeTxPK truckTypeTxPK;
	TruckTypeTxEntity truckTypeTx;
	TruckTypeBsEntity truckTypeBs;
	TruckWagonTypeWrapper truckWrapper;
	
	WagonTypeTxPK wagonTypeTxPK;
	WagonTypeTxEntity wagonTypeTx;
	WagonTypeBsEntity wagonTypeBs;
	TruckWagonTypeWrapper wagonWrapper;
	
	DeliveryTypeBsEntity deliveryTypeRoadDirect;
	DeliveryTypeBsEntity deliveryTypePickup;
	DeliveryTypeBsEntity deliveryTypeRailDirect;
	DeliveryTypeBsEntity deliveryTypeBimodalRoad;
	DeliveryTypeBsEntity deliveryTypeBimodalRail;
	
	RefDataGenericsService refdataService;
	
	OrderSpecialService orderSpecialService;
	
	CustorderService custorderService;
	
	
	public CustorderServiceTestCase(String testCaseName) {
		super(testCaseName);
	}
	
	
	@Before
	public void setUp() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setUp");
		}
		// ]]
		try {
			// Refdata Service
			refdataService = serviceLocator.getRefDataGenericsService();
			
			// UOMs
			UOM_TO = refdataService.findBsEntity(UomBsEntity.class, Constant.UOM_TONS);
			UOM_LKW = refdataService.findBsEntity(UomBsEntity.class, Constant.UOM_LKW);
			UOM_BAG = refdataService.findBsEntity(UomBsEntity.class, Constant.UOM_BAG);
			UOM_PAL = refdataService.findBsEntity(UomBsEntity.class, Constant.UOM_PAL);
			UOM_PCE = refdataService.findBsEntity(UomBsEntity.class, Constant.UOM_PCE);
			UOM_CONTAINER = refdataService.findBsEntity(UomBsEntity.class, Constant.UOM_CONTAINER);
			UOM_WAGON = refdataService.findBsEntity(UomBsEntity.class, Constant.UOM_WAGON);
			
			
			// OrderSpecialService
			orderSpecialService = serviceLocator.getOrderSpecialService();
			
			// SAP materials
			bulkSapMaterial = orderSpecialService.fetchSapMaterial(TEST_SAP_BULK_MATERIAL_ID);
			bagSapMaterial1 = orderSpecialService.fetchSapMaterial(TEST_SAP_BAG_MATERIAL_ID_1);
			bagSapMaterial2 = orderSpecialService.fetchSapMaterial(TEST_SAP_BAG_MATERIAL_ID_2);
			bagSapMaterial3 = orderSpecialService.fetchSapMaterial(TEST_SAP_BAG_MATERIAL_ID_3);
			serviceSapMaterial = orderSpecialService.fetchSapMaterial(TEST_SAP_SERVICE_MATERIAL);
			
			// Vehicles
			truckTypeBs = new TruckTypeBsEntity();
			truckTypeBs.setTruckTypeCd(-1L);
			truckTypeTxPK = new TruckTypeTxPK();
			truckTypeTxPK.setLangCd("en");
			truckTypeTxPK.setTruckTypeBs(truckTypeBs);
			truckTypeTx = new TruckTypeTxEntity();
			truckTypeTx.setTxt("TestTruck");
			truckTypeTx.setTruckTypeTxPK(truckTypeTxPK);
			truckWrapper = new TruckTypeWrapper(1L, truckTypeBs, null);
			wagonTypeBs = new WagonTypeBsEntity();
			wagonTypeBs.setWagonTypeCd(-1L);
			wagonTypeTxPK = new WagonTypeTxPK();
			wagonTypeTxPK.setLangCd("en");
			wagonTypeTxPK.setWagonTypeBs(wagonTypeBs);
			wagonTypeTx = new WagonTypeTxEntity();
			wagonTypeTx.setTxt("TestWagon");
			wagonTypeTx.setWagonTypeTxPK(wagonTypeTxPK);
			wagonWrapper = new WagonTypeWrapper(1L, wagonTypeTx, null);
			
			// Customer Order items
			bulkCustorderItem = new CustorderItemEntity();
			bulkCustorderItem.setSapMaterial(bulkSapMaterial);
			bagCustorderItem1 = new CustorderItemEntity();
			bagCustorderItem1.setSapMaterial(bagSapMaterial1);
			bagCustorderItem2 = new CustorderItemEntity();
			bagCustorderItem2.setSapMaterial(bagSapMaterial2);
			bagCustorderItem3 = new CustorderItemEntity();
			bagCustorderItem3.setSapMaterial(bagSapMaterial3);
			serviceCustorderItem = new CustorderItemEntity();
			serviceCustorderItem.setSapMaterial(serviceSapMaterial);
			
			// Delivery Type
			deliveryTypeRoadDirect = refdataService.findBsEntity(DeliveryTypeBsEntity.class, Constant.DELIVERY_TYPE_ROAD_DIRECT);
			deliveryTypePickup = refdataService.findBsEntity(DeliveryTypeBsEntity.class, Constant.DELIVERY_TYPE_PICKUP);
			deliveryTypeRailDirect = refdataService.findBsEntity(DeliveryTypeBsEntity.class, Constant.DELIVERY_TYPE_RAIL_DIRECT);
			deliveryTypeBimodalRoad = refdataService.findBsEntity(DeliveryTypeBsEntity.class, Constant.DELIVERY_TYPE_BIMODAL_ROAD);
			deliveryTypeBimodalRail = refdataService.findBsEntity(DeliveryTypeBsEntity.class, Constant.DELIVERY_TYPE_BIMODAL_RAIL);
			
			// Customer Order
			custorderEntity = new CustorderEntity();
			
			// CustorderService
			custorderService = serviceLocator.getCustorderService();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	@After
	public void tearDown() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "tearDown");
		}
		// ]]
		try {
			custorderEntity = null;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	/**
	 * Bulk Cement Road direct
	 * 
	 * For more detailed info: file:///share/doc/crh/logon_2_1/spec/biz/block4/SDS_Logon_block4_agg_070.0_074.0_order_entry_dispocard_V1_7_20120926.pdf
	 */
	@Test
	public void test_LRQ_070_0_case1() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_LRQ_070_0_case1");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bulkCustorderItem.setOrderedQuantity(new BigDecimal(60));
			bulkCustorderItem.setOrderedUom(UOM_TO);
			bulkCustorderItem.setExactCustorderQtyFlag(false);
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bulkCustorderItem.setPerSdorderQuantity(new BigDecimal(25));
			bulkCustorderItem.setPerSdorderUom(UOM_TO);
			bulkCustorderItem.setExactSdorderQtyFlag(false);
			bulkCustorderItem.setCustorder(custorderEntity);
			
			// initialize truck type
			truckTypeBs.setPlanningNetWeightT(new BigDecimal(25));
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeRoadDirect);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(null);
			custorderEntity.setDeliveryFreqEveryTimeUom(null);
			custorderEntity.setDeliveryFreqQtyperhre(null);
			custorderEntity.setDeliveryFreqQtyperhreUom(null);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 11);
			final Date deliveryToDat = cal.getTime();
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(null);
			custorderEntity.setDeliveryTimeToleranceUom(null);
			
			// add bulk customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bulkCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBulkSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, truckWrapper);
			
			// call the bulk customer order split function
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
//			System.out.println("### LRQ_70.0 TestCase 1 >>>");
//			System.out.println(custorderBulkSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBulkSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(3, sdos.size());
			
			for (OrderEntity sdo : sdos) {
				// check delivery from/to date&time
				Assert.assertEquals(deliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(deliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				Assert.assertEquals(Constant.UOM_LKW, sdoi.getOrderedUom().getUomCd());
				Assert.assertEquals(0, BigDecimal.ONE.compareTo(sdoi.getOrderedQuantity()));
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * Bulk Agg Road direct Fuzzy
	 * 
	 * For more detailed info: file:///share/doc/crh/logon_2_1/spec/biz/block4/SDS_Logon_block4_agg_070.0_074.0_order_entry_dispocard_V1_7_20120926.pdf
	 */
	@Test
	public void test_LRQ_070_0_case6() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_LRQ_070_0_case6");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bulkCustorderItem.setOrderedQuantity(new BigDecimal(2));
			bulkCustorderItem.setOrderedUom(UOM_LKW);
			bulkCustorderItem.setExactCustorderQtyFlag(false);
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bulkCustorderItem.setPerSdorderQuantity(null);
			bulkCustorderItem.setPerSdorderUom(null);
			bulkCustorderItem.setExactSdorderQtyFlag(false);
			bulkCustorderItem.setCustorder(custorderEntity);
			
			// initialize truck type and related converter
			truckTypeBs.setPlanningNetWeightT(new BigDecimal(30));
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeRoadDirect);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(null);
			custorderEntity.setDeliveryFreqEveryTimeUom(null);
			custorderEntity.setDeliveryFreqQtyperhre(null);
			custorderEntity.setDeliveryFreqQtyperhreUom(null);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 11);
			final Date deliveryToDat = cal.getTime();
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(null);
			custorderEntity.setDeliveryTimeToleranceUom(null);
			
			// add bulk customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bulkCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBulkSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, truckWrapper);
			
			// call the bulk customer order split function
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
//			System.out.println("### LRQ_70.0 TestCase 6 >>>");
//			System.out.println(custorderBulkSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBulkSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(2, sdos.size());
			
			for (OrderEntity sdo : sdos) {
				// check delivery from/to date&time
				Assert.assertEquals(deliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(deliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				Assert.assertEquals(Constant.UOM_LKW, sdoi.getOrderedUom().getUomCd());
				Assert.assertEquals(0, BigDecimal.ONE.compareTo(sdoi.getOrderedQuantity()));
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * Bulk Agg Road direct Fuzzy
	 * 
	 * For more detailed info: file:///share/doc/crh/logon_2_1/spec/biz/block4/SDS_Logon_block4_agg_070.0_074.0_order_entry_dispocard_V1_7_20120926.pdf
	 */
	@Test
	public void test_LRQ_070_0_case7() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_LRQ_070_0_case7");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bulkCustorderItem.setOrderedQuantity(new BigDecimal(2));
			bulkCustorderItem.setOrderedUom(UOM_LKW);
			bulkCustorderItem.setExactCustorderQtyFlag(false);
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bulkCustorderItem.setPerSdorderQuantity(null);
			bulkCustorderItem.setPerSdorderUom(null);
			bulkCustorderItem.setExactSdorderQtyFlag(false);
			bulkCustorderItem.setCustorder(custorderEntity);

			// initialize truck type and related converter
			truckTypeBs.setPlanningNetWeightT(new BigDecimal(30));
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeRoadDirect);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 11);
			final Date deliveryToDat = null;
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(0.0);
			custorderEntity.setDeliveryTimeToleranceUom(Constant.UOM_MIN);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(25.0);
			custorderEntity.setDeliveryFreqEveryTimeUom(Constant.UOM_MIN);
			custorderEntity.setDeliveryFreqQtyperhre(null);
			custorderEntity.setDeliveryFreqQtyperhreUom(null);
			
			// add bulk customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bulkCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBulkSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, truckWrapper);
			
			// call the bulk customer order split function
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
//			System.out.println("### LRQ_70.0 TestCase 7 >>>");
//			System.out.println(custorderBulkSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBulkSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(2, sdos.size());
			
			// initialize delivery frequency in milliseconds
			long deliveryFqyInMillisec = Math.round(custorderEntity.getDeliveryFreqEveryTime() * 60.0 * 1000.0);
			long deliveryTimeToleranceInMillisec = Math.round(custorderEntity.getDeliveryTimeTolerance() * 60.0 * 1000.0);
			
			for (int i=0; i < sdos.size(); i++) {
				OrderEntity sdo = sdos.get(i);
				
				// check delivery from/to date&time
				Date expectedDeliveryFromDat = new Date(deliveryFromDat.getTime() + i * deliveryFqyInMillisec - deliveryTimeToleranceInMillisec);
				Date expectedDeliveryToDat = new Date(deliveryFromDat.getTime() + i * deliveryFqyInMillisec + deliveryTimeToleranceInMillisec);
				Assert.assertEquals(expectedDeliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(expectedDeliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				Assert.assertEquals(Constant.UOM_LKW, sdoi.getOrderedUom().getUomCd());
				Assert.assertEquals(0, BigDecimal.ONE.compareTo(sdoi.getOrderedQuantity()));
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * Bulk Agg Road direct Fuzzy
	 * 
	 * For more detailed info: file:///share/doc/crh/logon_2_1/spec/biz/block4/SDS_Logon_block4_agg_070.0_074.0_order_entry_dispocard_V1_7_20120926.pdf
	 */
	@Test
	public void test_LRQ_070_0_case8() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_LRQ_070_0_case8");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bulkCustorderItem.setOrderedQuantity(new BigDecimal(2));
			bulkCustorderItem.setOrderedUom(UOM_LKW);
			bulkCustorderItem.setExactCustorderQtyFlag(false);
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bulkCustorderItem.setPerSdorderQuantity(null);
			bulkCustorderItem.setPerSdorderUom(null);
			bulkCustorderItem.setExactSdorderQtyFlag(false);
			bulkCustorderItem.setCustorder(custorderEntity);
			
			// initialize truck type and related converter
			truckTypeBs.setPlanningNetWeightT(new BigDecimal(30));
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeRoadDirect);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 11);
			final Date deliveryToDat = null;
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(3.0);
			custorderEntity.setDeliveryTimeToleranceUom(Constant.UOM_MIN);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(25.0);
			custorderEntity.setDeliveryFreqEveryTimeUom(Constant.UOM_MIN);
			custorderEntity.setDeliveryFreqQtyperhre(null);
			custorderEntity.setDeliveryFreqQtyperhreUom(null);
			
			// add bulk customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bulkCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBulkSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, truckWrapper);
			
			// call the bulk customer order split function
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
//			System.out.println("### LRQ_70.0 TestCase 8 >>>");
//			System.out.println(custorderBulkSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBulkSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(2, sdos.size());
			
			// initialize delivery frequency and tolerance in milliseconds
			long deliveryFqyInMillisec = Math.round(custorderEntity.getDeliveryFreqEveryTime() * 60.0 * 1000.0);
			long deliveryTimeToleranceInMillisec = Math.round(custorderEntity.getDeliveryTimeTolerance() * 60.0 * 1000.0);
			
			for (int i=0; i < sdos.size(); i++) {
				OrderEntity sdo = sdos.get(i);
				
				// check delivery from/to date&time
				Date expectedDeliveryFromDat = new Date(deliveryFromDat.getTime() + i * deliveryFqyInMillisec - deliveryTimeToleranceInMillisec);
				Date expectedDeliveryToDat = new Date(deliveryFromDat.getTime() + i * deliveryFqyInMillisec + deliveryTimeToleranceInMillisec);
				Assert.assertEquals(expectedDeliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(expectedDeliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				Assert.assertEquals(Constant.UOM_LKW, sdoi.getOrderedUom().getUomCd());
				Assert.assertEquals(0, BigDecimal.ONE.compareTo(sdoi.getOrderedQuantity()));
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * Bulk Agg Road direct ExactQ
	 * 
	 * For more detailed info: file:///share/doc/crh/logon_2_1/spec/biz/block4/SDS_Logon_block4_agg_070.0_074.0_order_entry_dispocard_V1_7_20120926.pdf
	 */
	@Test
	public void test_LRQ_070_0_case9() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_LRQ_070_0_case9");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bulkCustorderItem.setOrderedQuantity(new BigDecimal(50));
			bulkCustorderItem.setOrderedUom(UOM_TO);
			bulkCustorderItem.setExactCustorderQtyFlag(false);
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bulkCustorderItem.setPerSdorderQuantity(new BigDecimal(30));
			bulkCustorderItem.setPerSdorderUom(UOM_TO);
			bulkCustorderItem.setExactSdorderQtyFlag(false);
			bulkCustorderItem.setCustorder(custorderEntity);
			
			// initialize truck type and related converter
			truckTypeBs.setPlanningNetWeightT(new BigDecimal(30));
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeRoadDirect);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 11);
			final Date deliveryToDat = cal.getTime();
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(null);
			custorderEntity.setDeliveryTimeToleranceUom(null);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(null);
			custorderEntity.setDeliveryFreqEveryTimeUom(null);
			custorderEntity.setDeliveryFreqQtyperhre(null);
			custorderEntity.setDeliveryFreqQtyperhreUom(null);
			
			// add bulk customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bulkCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBulkSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, truckWrapper);
			
			// call the bulk customer order split function
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
//			System.out.println("### LRQ_70.0 TestCase 9 >>>");
//			System.out.println(custorderBulkSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBulkSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(2, sdos.size());
			
			for (int i=0; i < sdos.size(); i++) {
				OrderEntity sdo = sdos.get(i);
				
				// check delivery from/to date&time
				Assert.assertEquals(deliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(deliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				Assert.assertEquals(Constant.UOM_LKW, sdoi.getOrderedUom().getUomCd());
				Assert.assertEquals(0, BigDecimal.ONE.compareTo(sdoi.getOrderedQuantity()));
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * Bulk Agg Road direct ExactQ
	 * 
	 * For more detailed info: file:///share/doc/crh/logon_2_1/spec/biz/block4/SDS_Logon_block4_agg_070.0_074.0_order_entry_dispocard_V1_7_20120926.pdf
	 */
	@Test
	public void test_LRQ_070_0_case10() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_LRQ_070_0_case10");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bulkCustorderItem.setOrderedQuantity(new BigDecimal(50));
			bulkCustorderItem.setOrderedUom(UOM_TO);
			bulkCustorderItem.setExactCustorderQtyFlag(true);
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bulkCustorderItem.setPerSdorderQuantity(new BigDecimal(30));
			bulkCustorderItem.setPerSdorderUom(UOM_TO);
			bulkCustorderItem.setExactSdorderQtyFlag(false);
			bulkCustorderItem.setCustorder(custorderEntity);
			
			// initialize truck type and related converter
			truckTypeBs.setPlanningNetWeightT(new BigDecimal(25));
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeRoadDirect);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 11);
			final Date deliveryToDat = cal.getTime();
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(null);
			custorderEntity.setDeliveryTimeToleranceUom(null);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(null);
			custorderEntity.setDeliveryFreqEveryTimeUom(null);
			custorderEntity.setDeliveryFreqQtyperhre(null);
			custorderEntity.setDeliveryFreqQtyperhreUom(null);
			
			// add bulk customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bulkCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBulkSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, truckWrapper);
			
			// call the bulk customer order split function
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
//			System.out.println("### LRQ_70.0 TestCase 10 >>>");
//			System.out.println(custorderBulkSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBulkSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(2, sdos.size());
			
			for (int i=0; i < sdos.size(); i++) {
				OrderEntity sdo = sdos.get(i);
				
				// check delivery from/to date&time
				Assert.assertEquals(deliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(deliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				if (i == 0) {
					Assert.assertEquals(Constant.UOM_LKW, sdoi.getOrderedUom().getUomCd());
					Assert.assertEquals(0, BigDecimal.ONE.compareTo(sdoi.getOrderedQuantity()));
				}
				else if (i == 1) {
					Assert.assertEquals(Constant.UOM_TONS, sdoi.getOrderedUom().getUomCd());
					Assert.assertEquals(0, new BigDecimal(20).compareTo(sdoi.getOrderedQuantity()));
				}
				else {
					Assert.fail("single delivery No. " + i + " was not expected");
				}
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * Bulk Agg Road direct ExactQ
	 * 
	 * For more detailed info: file:///share/doc/crh/logon_2_1/spec/biz/block4/SDS_Logon_block4_agg_070.0_074.0_order_entry_dispocard_V1_7_20120926.pdf
	 */
	@Test
	public void test_LRQ_070_0_case11() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_LRQ_070_0_case11");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bulkCustorderItem.setOrderedQuantity(new BigDecimal(50));
			bulkCustorderItem.setOrderedUom(UOM_TO);
			bulkCustorderItem.setExactCustorderQtyFlag(true);
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bulkCustorderItem.setPerSdorderQuantity(new BigDecimal(24));
			bulkCustorderItem.setPerSdorderUom(UOM_TO);
			bulkCustorderItem.setExactSdorderQtyFlag(false);
			bulkCustorderItem.setCustorder(custorderEntity);
			
			// initialize truck type and related converter
			truckTypeBs.setPlanningNetWeightT(new BigDecimal(25));
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeRoadDirect);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 11);
			final Date deliveryToDat = cal.getTime();
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(null);
			custorderEntity.setDeliveryTimeToleranceUom(null);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(null);
			custorderEntity.setDeliveryFreqEveryTimeUom(null);
			custorderEntity.setDeliveryFreqQtyperhre(null);
			custorderEntity.setDeliveryFreqQtyperhreUom(null);
			
			// add bulk customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bulkCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBulkSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, truckWrapper);
			
			// call the bulk customer order split function
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
//			System.out.println("### LRQ_70.0 TestCase 11 >>>");
//			System.out.println(custorderBulkSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBulkSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(3, sdos.size());
			
			for (int i=0; i < sdos.size(); i++) {
				OrderEntity sdo = sdos.get(i);
				
				// check delivery from/to date&time
				Assert.assertEquals(deliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(deliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				if (i == 0 || i == 1) {
					Assert.assertEquals(Constant.UOM_LKW, sdoi.getOrderedUom().getUomCd());
					Assert.assertEquals(0, BigDecimal.ONE.compareTo(sdoi.getOrderedQuantity()));
				}
				else if (i == 2) {
					Assert.assertEquals(Constant.UOM_TONS, sdoi.getOrderedUom().getUomCd());
					Assert.assertEquals(0, new BigDecimal(2).compareTo(sdoi.getOrderedQuantity()));
				}
				else {
					Assert.fail("single delivery No. " + i + " was not expected");
				}
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * Bulk Agg Road direct ExactQ
	 * 
	 * For more detailed info: file:///share/doc/crh/logon_2_1/spec/biz/block4/SDS_Logon_block4_agg_070.0_074.0_order_entry_dispocard_V1_7_20120926.pdf
	 */
	@Test
	public void test_LRQ_070_0_case12() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_LRQ_070_0_case12");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bulkCustorderItem.setOrderedQuantity(new BigDecimal(50));
			bulkCustorderItem.setOrderedUom(UOM_TO);
			bulkCustorderItem.setExactCustorderQtyFlag(false);
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bulkCustorderItem.setPerSdorderQuantity(new BigDecimal(24));
			bulkCustorderItem.setPerSdorderUom(UOM_TO);
			bulkCustorderItem.setExactSdorderQtyFlag(true);
			bulkCustorderItem.setCustorder(custorderEntity);
			
			// initialize truck type and related converter
			truckTypeBs.setPlanningNetWeightT(new BigDecimal(25));
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeRoadDirect);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 11);
			final Date deliveryToDat = cal.getTime();
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(null);
			custorderEntity.setDeliveryTimeToleranceUom(null);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(null);
			custorderEntity.setDeliveryFreqEveryTimeUom(null);
			custorderEntity.setDeliveryFreqQtyperhre(null);
			custorderEntity.setDeliveryFreqQtyperhreUom(null);
			
			// add bulk customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bulkCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBulkSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, truckWrapper);
			
			// call the bulk customer order split function
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
//			System.out.println("### LRQ_70.0 TestCase 12 >>>");
//			System.out.println(custorderBulkSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBulkSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(3, sdos.size());
			
			for (int i=0; i < sdos.size(); i++) {
				OrderEntity sdo = sdos.get(i);
				
				// check delivery from/to date&time
				Assert.assertEquals(deliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(deliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				if (i == 0 || i == 1) {
					Assert.assertEquals(Constant.UOM_TONS, sdoi.getOrderedUom().getUomCd());
					Assert.assertEquals(0, new BigDecimal(24).compareTo(sdoi.getOrderedQuantity()));
				}
				else if (i == 2) {
					Assert.assertEquals(Constant.UOM_TONS, sdoi.getOrderedUom().getUomCd());
					Assert.assertEquals(0, new BigDecimal(2).compareTo(sdoi.getOrderedQuantity()));
				}
				else {
					Assert.fail("single delivery No. " + i + " was not expected");
				}
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * Bulk Agg Road direct ExactQ
	 * 
	 * For more detailed info: file:///share/doc/crh/logon_2_1/spec/biz/block4/SDS_Logon_block4_agg_070.0_074.0_order_entry_dispocard_V1_7_20120926.pdf
	 */
	@Test
	public void test_LRQ_070_0_case13() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_LRQ_070_0_case13");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bulkCustorderItem.setOrderedQuantity(new BigDecimal(50));
			bulkCustorderItem.setOrderedUom(UOM_TO);
			bulkCustorderItem.setExactCustorderQtyFlag(true);
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bulkCustorderItem.setPerSdorderQuantity(new BigDecimal(23));
			bulkCustorderItem.setPerSdorderUom(UOM_TO);
			bulkCustorderItem.setExactSdorderQtyFlag(true);
			bulkCustorderItem.setCustorder(custorderEntity);
			
			// initialize truck type and related converter
			truckTypeBs.setPlanningNetWeightT(new BigDecimal(30));
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeRoadDirect);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			final Date deliveryToDat = null;
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(9.0);
			custorderEntity.setDeliveryTimeToleranceUom(Constant.UOM_MIN);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(10.0);
			custorderEntity.setDeliveryFreqEveryTimeUom(Constant.UOM_MIN);
			custorderEntity.setDeliveryFreqQtyperhre(null);
			custorderEntity.setDeliveryFreqQtyperhreUom(null);
			
			// add bulk customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bulkCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBulkSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, truckWrapper);
			
			// call the bulk customer order split function
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
//			System.out.println("### LRQ_70.0 TestCase 13 >>>");
//			System.out.println(custorderBulkSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBulkSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(3, sdos.size());
			
			// initialize delivery frequency and tolerance in milliseconds
			long deliveryFqyInMillisec = Math.round(custorderEntity.getDeliveryFreqEveryTime() * 60.0 * 1000.0);
			long deliveryTimeToleranceInMillisec = Math.round(custorderEntity.getDeliveryTimeTolerance() * 60.0 * 1000.0);
			
			for (int i=0; i < sdos.size(); i++) {
				OrderEntity sdo = sdos.get(i);
				
				// check delivery from/to date&time
				Date expectedDeliveryFromDat = new Date(deliveryFromDat.getTime() + i * deliveryFqyInMillisec - deliveryTimeToleranceInMillisec);
				Date expectedDeliveryToDat = new Date(deliveryFromDat.getTime() + i * deliveryFqyInMillisec + deliveryTimeToleranceInMillisec);
				Assert.assertEquals(expectedDeliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(expectedDeliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				if (i == 0 || i == 1) {
					Assert.assertEquals(Constant.UOM_TONS, sdoi.getOrderedUom().getUomCd());
					Assert.assertEquals(0, new BigDecimal(23).compareTo(sdoi.getOrderedQuantity()));
				}
				else if (i == 2) {
					Assert.assertEquals(Constant.UOM_TONS, sdoi.getOrderedUom().getUomCd());
					Assert.assertEquals(0, new BigDecimal(4).compareTo(sdoi.getOrderedQuantity()));
				}
				else {
					Assert.fail("single delivery No. " + i + " was not expected");
				}
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * Bulk Cement Road direct ExactQ
	 * 
	 * For more detailed info: file:///share/doc/crh/logon_2_1/spec/biz/block4/SDS_Logon_block4_agg_070.0_074.0_order_entry_dispocard_V1_7_20120926.pdf
	 */
	@Test
	public void test_LRQ_070_0_case14() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_LRQ_070_0_case14");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bulkCustorderItem.setOrderedQuantity(null);
			bulkCustorderItem.setOrderedUom(UOM_TO);
			bulkCustorderItem.setExactCustorderQtyFlag(false);
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bulkCustorderItem.setPerSdorderQuantity(new BigDecimal(25));
			bulkCustorderItem.setPerSdorderUom(UOM_TO);
			bulkCustorderItem.setExactSdorderQtyFlag(true);
			bulkCustorderItem.setCustorder(custorderEntity);
			
			// initialize truck type and related converter
			truckTypeBs.setPlanningNetWeightT(new BigDecimal(30));
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeRoadDirect);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 3);
			final Date deliveryToDat = cal.getTime();
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(0.0);
			custorderEntity.setDeliveryTimeToleranceUom(Constant.UOM_MIN);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(null);
			custorderEntity.setDeliveryFreqEveryTimeUom(null);
			custorderEntity.setDeliveryFreqQtyperhre(new BigDecimal(10));
			custorderEntity.setDeliveryFreqQtyperhreUom(UOM_TO);
			
			// add bulk customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bulkCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBulkSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, truckWrapper);
			
			// call the bulk customer order split function
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
//			System.out.println("### LRQ_70.0 TestCase 14 >>>");
//			System.out.println(custorderBulkSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBulkSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(2, sdos.size());
			
			// initialize delivery frequency and tolerance in milliseconds
			long deliveryFqyInMillisec = Math.round(bulkCustorderItem.getPerSdorderQuantity().divide(custorderEntity.getDeliveryFreqQtyperhre(), Constant.SCALE, BigDecimal.ROUND_HALF_UP).doubleValue() * 3600000);
			long deliveryTimeToleranceInMillisec = Math.round(custorderEntity.getDeliveryTimeTolerance() * 60.0 * 1000.0);
			
			for (int i=0; i < sdos.size(); i++) {
				OrderEntity sdo = sdos.get(i);
				
				// check delivery from/to date&time
				Date expectedDeliveryFromDat = new Date(deliveryFromDat.getTime() + i * deliveryFqyInMillisec - deliveryTimeToleranceInMillisec);
				Date expectedDeliveryToDat = new Date(deliveryFromDat.getTime() + i * deliveryFqyInMillisec + deliveryTimeToleranceInMillisec);
				Assert.assertEquals(expectedDeliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(expectedDeliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				if (i == 0) {
					Assert.assertEquals(Constant.UOM_TONS, sdoi.getOrderedUom().getUomCd());
					Assert.assertEquals(0, new BigDecimal(25).compareTo(sdoi.getOrderedQuantity()));
				}
				else if (i == 1) {
					Assert.assertEquals(Constant.UOM_TONS, sdoi.getOrderedUom().getUomCd());
					Assert.assertEquals(0, new BigDecimal(5).compareTo(sdoi.getOrderedQuantity()));
				}
				else {
					Assert.fail("single delivery No. " + i + " was not expected");
				}
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * Bulk Cement Road direct ExactQ
	 * 
	 * For more detailed info: file:///share/doc/crh/logon_2_1/spec/biz/block4/SDS_Logon_block4_agg_070.0_074.0_order_entry_dispocard_V1_7_20120926.pdf
	 */
	@Test
	public void test_LRQ_070_0_case15() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_LRQ_070_0_case15");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bulkCustorderItem.setOrderedQuantity(null);
			bulkCustorderItem.setOrderedUom(UOM_TO);
			bulkCustorderItem.setExactCustorderQtyFlag(true);
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bulkCustorderItem.setPerSdorderQuantity(new BigDecimal(30));
			bulkCustorderItem.setPerSdorderUom(UOM_TO);
			bulkCustorderItem.setExactSdorderQtyFlag(false);
			bulkCustorderItem.setCustorder(custorderEntity);
			
			// initialize truck type and related converter
			truckTypeBs.setPlanningNetWeightT(new BigDecimal(30));
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeRoadDirect);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 1);
			cal.add(Calendar.MINUTE, 30);
			final Date deliveryToDat = cal.getTime();
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(1.0);
			custorderEntity.setDeliveryTimeToleranceUom(Constant.UOM_HRE);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(null);
			custorderEntity.setDeliveryFreqEveryTimeUom(null);
			custorderEntity.setDeliveryFreqQtyperhre(new BigDecimal(1.5));
			custorderEntity.setDeliveryFreqQtyperhreUom(UOM_LKW);
			
			// add bulk customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bulkCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBulkSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, truckWrapper);
			
			// call the bulk customer order split function
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
//			System.out.println("### LRQ_70.0 TestCase 15 >>>");
//			System.out.println(custorderBulkSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBulkSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(3, sdos.size());
			
			// initialize delivery frequency and tolerance in milliseconds
			long deliveryFqyInMillisec = Math.round(custorderBulkSplitWrapper.convertQuantity(bulkCustorderItem.getSapMaterial()
					, UOM_TO.getUomCd(), UOM_LKW.getUomCd(), bulkCustorderItem.getPerSdorderQuantity()).getQuantity().divide(custorderEntity.getDeliveryFreqQtyperhre()
							, Constant.SCALE, BigDecimal.ROUND_HALF_UP).doubleValue() * 3600000);
			long deliveryTimeToleranceInMillisec = Math.round(custorderEntity.getDeliveryTimeTolerance() * 60.0 * 60.0 * 1000.0);
			
			for (int i=0; i < sdos.size(); i++) {
				OrderEntity sdo = sdos.get(i);
				
				// check delivery from/to date&time
				Date expectedDeliveryFromDat = new Date(deliveryFromDat.getTime() + i * deliveryFqyInMillisec - deliveryTimeToleranceInMillisec);
				Date expectedDeliveryToDat = new Date(deliveryFromDat.getTime() + i * deliveryFqyInMillisec + deliveryTimeToleranceInMillisec);
				Assert.assertEquals(expectedDeliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(expectedDeliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				if (i == 0 || i == 1) {
					Assert.assertEquals(Constant.UOM_LKW, sdoi.getOrderedUom().getUomCd());
					Assert.assertEquals(0, BigDecimal.ONE.compareTo(sdoi.getOrderedQuantity()));
				}
				else if (i == 2) {
					Assert.assertEquals(Constant.UOM_TONS, sdoi.getOrderedUom().getUomCd());
					Assert.assertEquals(0, new BigDecimal(7.5).compareTo(sdoi.getOrderedQuantity()));
				}
				else {
					Assert.fail("single delivery No. " + i + " was not expected");
				}
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * Bulk Agg Picku ExactQ
	 * 
	 * For more detailed info: file:///share/doc/crh/logon_2_1/spec/biz/block4/SDS_Logon_block4_agg_070.0_074.0_order_entry_dispocard_V1_7_20120926.pdf
	 */
	@Test
	public void test_LRQ_070_0_case16() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_LRQ_070_0_case16");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bulkCustorderItem.setOrderedQuantity(null);
			bulkCustorderItem.setOrderedUom(UOM_TO);
			bulkCustorderItem.setExactCustorderQtyFlag(false);
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bulkCustorderItem.setPerSdorderQuantity(new BigDecimal(30));
			bulkCustorderItem.setPerSdorderUom(UOM_TO);
			bulkCustorderItem.setExactSdorderQtyFlag(true);
			bulkCustorderItem.setCustorder(custorderEntity);
			
			// initialize truck type and related converter
			truckTypeBs.setPlanningNetWeightT(new BigDecimal(30));
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeRoadDirect);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.MINUTE, 31);
			final Date deliveryToDat = cal.getTime();
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(0.0);
			custorderEntity.setDeliveryTimeToleranceUom(Constant.UOM_MIN);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(null);
			custorderEntity.setDeliveryFreqEveryTimeUom(null);
			custorderEntity.setDeliveryFreqQtyperhre(new BigDecimal(3));
			custorderEntity.setDeliveryFreqQtyperhreUom(UOM_LKW);
			
			// add bulk customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bulkCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBulkSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, truckWrapper);
			
			// call the bulk customer order split function
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
//			System.out.println("### LRQ_70.0 TestCase 16 >>>");
//			System.out.println(custorderBulkSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBulkSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(2, sdos.size());
			
			// initialize delivery frequency and tolerance in milliseconds
//			BigDecimal perSdoQtyInLkwUom = custorderBulkSplitWrapper.convertQuantity(UOM_TO.getUomCd(), UOM_LKW.getUomCd(), bulkCustorderItem.getPerSdorderQuantity());
//			BigDecimal deliveryFqyInHre = perSdoQtyInLkwUom.divide(custorderEntity.getDeliveryFreqQtyperhre(), Constant.SCALE, BigDecimal.ROUND_HALF_UP);
//			long deliveryFqyInMillisec = deliveryFqyInHre.multiply(new BigDecimal(3600000)).longValue();
			long deliveryFqyInMillisec = 20 * 60 * 1000;	// 20 minutes
			long deliveryTimeToleranceInMillisec = Math.round(custorderEntity.getDeliveryTimeTolerance() * 60.0 * 60.0 * 1000.0);
			
			for (int i=0; i < sdos.size(); i++) {
				OrderEntity sdo = sdos.get(i);
				
				// check delivery from/to date&time
				Date expectedDeliveryFromDat = new Date(deliveryFromDat.getTime() + i * deliveryFqyInMillisec - deliveryTimeToleranceInMillisec);
				Date expectedDeliveryToDat = new Date(deliveryFromDat.getTime() + i * deliveryFqyInMillisec + deliveryTimeToleranceInMillisec);
				Assert.assertEquals(expectedDeliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(expectedDeliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				if (i == 0) {
					Assert.assertEquals(Constant.UOM_TONS, sdoi.getOrderedUom().getUomCd());
					Assert.assertEquals(0, new BigDecimal(30).compareTo(sdoi.getOrderedQuantity()));
				}
				else if (i == 1) {
					Assert.assertEquals(Constant.UOM_TONS, sdoi.getOrderedUom().getUomCd());
					Assert.assertEquals(0, new BigDecimal(16.5).compareTo(sdoi.getOrderedQuantity()));
				}
				else {
					Assert.fail("single delivery No. " + i + " was not expected");
				}
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	@Test
	public void test_LRQ_070_0_perSdoQtyVsTruckCapacity() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_LRQ_070_0_perSdoQtyVsTruckCapacity");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bulkCustorderItem.setOrderedQuantity(new BigDecimal(50));
			bulkCustorderItem.setOrderedUom(UOM_TO);
			bulkCustorderItem.setExactCustorderQtyFlag(false);
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bulkCustorderItem.setPerSdorderQuantity(new BigDecimal(10));
			bulkCustorderItem.setPerSdorderUom(UOM_TO);
			bulkCustorderItem.setExactSdorderQtyFlag(false);
			bulkCustorderItem.setCustorder(custorderEntity);
			
			// initialize truck type
			truckTypeBs.setPlanningNetWeightT(new BigDecimal(20));
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeRoadDirect);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(null);
			custorderEntity.setDeliveryFreqEveryTimeUom(null);
			custorderEntity.setDeliveryFreqQtyperhre(null);
			custorderEntity.setDeliveryFreqQtyperhreUom(null);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 11);
			final Date deliveryToDat = cal.getTime();
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(null);
			custorderEntity.setDeliveryTimeToleranceUom(null);
			
			// add bulk customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bulkCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBulkSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, truckWrapper);
			
			// call the bulk customer order split function
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
//			System.out.println("### LRQ_70.0 TestCase perSdoQtyVsTruckCapacity >>>");
//			System.out.println(custorderBulkSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBulkSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(5, sdos.size());
			
			for (OrderEntity sdo : sdos) {
				// check delivery from/to date&time
				Assert.assertEquals(deliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(deliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				Assert.assertEquals(Constant.UOM_LKW, sdoi.getOrderedUom().getUomCd());
				Assert.assertEquals(0, BigDecimal.ONE.compareTo(sdoi.getOrderedQuantity()));
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	@Test
	public void test_LRQ_070_0_case13_perfOrderPerSdoQtyNull() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_LRQ_070_0_case13_perfOrderPerSdoQtyNull");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bulkCustorderItem.setOrderedQuantity(new BigDecimal(50));
			bulkCustorderItem.setOrderedUom(UOM_TO);
			bulkCustorderItem.setExactCustorderQtyFlag(true);
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bulkCustorderItem.setPerSdorderQuantity(null);
			bulkCustorderItem.setPerSdorderUom(null);
			bulkCustorderItem.setExactSdorderQtyFlag(true);
			bulkCustorderItem.setCustorder(custorderEntity);
			
			// initialize truck type and related converter
			truckTypeBs.setPlanningNetWeightT(new BigDecimal(30));
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeRoadDirect);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			final Date deliveryToDat = null;
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(9.0);
			custorderEntity.setDeliveryTimeToleranceUom(Constant.UOM_MIN);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(10.0);
			custorderEntity.setDeliveryFreqEveryTimeUom(Constant.UOM_MIN);
			custorderEntity.setDeliveryFreqQtyperhre(null);
			custorderEntity.setDeliveryFreqQtyperhreUom(null);
			
			// add bulk customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bulkCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBulkSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, truckWrapper);
			
			// call the bulk customer order split function
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
//			System.out.println("### LRQ_70.0 TestCase perfOrderPerSdoQtyNull >>>");
//			System.out.println(custorderBulkSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBulkSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(2, sdos.size());
			
			// initialize delivery frequency and tolerance in milliseconds
			long deliveryFqyInMillisec = Math.round(custorderEntity.getDeliveryFreqEveryTime() * 60.0 * 1000.0);
			long deliveryTimeToleranceInMillisec = Math.round(custorderEntity.getDeliveryTimeTolerance() * 60.0 * 1000.0);
			
			for (int i=0; i < sdos.size(); i++) {
				OrderEntity sdo = sdos.get(i);
				
				// check delivery from/to date&time
				Date expectedDeliveryFromDat = new Date(deliveryFromDat.getTime() + i * deliveryFqyInMillisec - deliveryTimeToleranceInMillisec);
				Date expectedDeliveryToDat = new Date(deliveryFromDat.getTime() + i * deliveryFqyInMillisec + deliveryTimeToleranceInMillisec);
				Assert.assertEquals(expectedDeliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(expectedDeliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				if (i == 0) {
					Assert.assertEquals(Constant.UOM_TONS, sdoi.getOrderedUom().getUomCd());
					Assert.assertEquals(0, new BigDecimal(30).compareTo(sdoi.getOrderedQuantity()));
				}
				else if (i == 1) {
					Assert.assertEquals(Constant.UOM_TONS, sdoi.getOrderedUom().getUomCd());
					Assert.assertEquals(0, new BigDecimal(20).compareTo(sdoi.getOrderedQuantity()));
				}
				else {
					Assert.fail("single delivery No. " + i + " was not expected");
				}
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	@Test
	public void test_LRQ_003_3_case1(){
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_LRQ_003_3_case1");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bagCustorderItem1.setOrderedQuantity(new BigDecimal(100));
			bagCustorderItem1.setOrderedUom(UOM_TO);
			bagCustorderItem1.setExactCustorderQtyFlag(false);
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bagCustorderItem1.setPerSdorderQuantity(new BigDecimal(25));
			bagCustorderItem1.setPerSdorderUom(UOM_TO);
			bagCustorderItem1.setExactSdorderQtyFlag(true);
			
			// initialize truck type
			truckTypeBs.setPlanningNetWeightT(new BigDecimal(30));
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeRoadDirect);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(null);
			custorderEntity.setDeliveryFreqEveryTimeUom(null);
			custorderEntity.setDeliveryFreqQtyperhre(null);
			custorderEntity.setDeliveryFreqQtyperhreUom(null);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 11);
			final Date deliveryToDat = cal.getTime();
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(null);
			custorderEntity.setDeliveryTimeToleranceUom(null);
			
			// add bag customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bagCustorderItem1);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBagSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, truckWrapper);
			
			// call customer order split function
			CustorderUtils.splitCustorder(custorderBagSplitWrapper);
//			System.out.println("### LRQ_003.3 TestCase 1 >>>");
//			System.out.println(custorderBagSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBagSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(4, sdos.size());
			
			for (OrderEntity sdo : sdos) {
				// check delivery from/to date&time
				Assert.assertEquals(deliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(deliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				Assert.assertEquals(Constant.UOM_TONS, sdoi.getOrderedUom().getUomCd());
				Assert.assertEquals(0, new BigDecimal(25).compareTo(sdoi.getOrderedQuantity()));
			}
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	@Test
	public void test_LRQ_003_3_case2(){
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_LRQ_003_3_case2");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bagCustorderItem1.setOrderedQuantity(new BigDecimal(10));
			bagCustorderItem1.setOrderedUom(UOM_PAL);
			bagCustorderItem1.setExactCustorderQtyFlag(false);
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bagCustorderItem1.setPerSdorderQuantity(new BigDecimal(20));
			bagCustorderItem1.setPerSdorderUom(UOM_PAL);
			bagCustorderItem1.setExactSdorderQtyFlag(true);
			
			// initialize truck type
			truckTypeBs.setPlanningNetWeightT(new BigDecimal(30));
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeRoadDirect);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(null);
			custorderEntity.setDeliveryFreqEveryTimeUom(null);
			custorderEntity.setDeliveryFreqQtyperhre(null);
			custorderEntity.setDeliveryFreqQtyperhreUom(null);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 11);
			final Date deliveryToDat = cal.getTime();
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(null);
			custorderEntity.setDeliveryTimeToleranceUom(null);
			
			// add bag customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bagCustorderItem1);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBagSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, truckWrapper);
			
			// call customer order split function
			CustorderUtils.splitCustorder(custorderBagSplitWrapper);
//			System.out.println("### LRQ_003.3 TestCase 2 >>>");
//			System.out.println(custorderBagSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBagSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(1, sdos.size());
			
			for (OrderEntity sdo : sdos) {
				// check delivery from/to date&time
				Assert.assertEquals(deliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(deliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				Assert.assertEquals(Constant.UOM_PAL, sdoi.getOrderedUom().getUomCd());
				Assert.assertEquals(0, new BigDecimal(10).compareTo(sdoi.getOrderedQuantity()));
			}
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	/**
	 * Test case not exactly the same as in the SDS since the UoM conversion factors are differ
	 * SDS:
	 * PAL48 -> 1.2 TO
	 * PAL42 -> 1.0 TO
	 * TO -> TO
	 * Testcase:
	 * PAL(mat1) -> 1.2 TO
	 * PAL(mat2) -> 1.4 TO
	 * TO -> TO
	 */
	@Test
	public void test_LRQ_003_3_case3_mod(){
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_LRQ_003_3_case3_mod");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bagCustorderItem1.setOrderedQuantity(new BigDecimal(10));
			bagCustorderItem1.setOrderedUom(UOM_PAL);
			bagCustorderItem1.setExactCustorderQtyFlag(false);
			bagCustorderItem2.setOrderedQuantity(new BigDecimal(15));
			bagCustorderItem2.setOrderedUom(UOM_PAL);
			bagCustorderItem2.setExactCustorderQtyFlag(false);
			bagCustorderItem3.setOrderedQuantity(new BigDecimal(15.25));
			bagCustorderItem3.setOrderedUom(UOM_TO);
			bagCustorderItem3.setExactCustorderQtyFlag(false);
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bagCustorderItem1.setPerSdorderQuantity(new BigDecimal(20));
			bagCustorderItem1.setPerSdorderUom(UOM_PAL);
			bagCustorderItem1.setExactSdorderQtyFlag(true);
			bagCustorderItem2.setPerSdorderQuantity(new BigDecimal(20));
			bagCustorderItem2.setPerSdorderUom(UOM_PAL);
			bagCustorderItem2.setExactSdorderQtyFlag(true);
			bagCustorderItem3.setPerSdorderQuantity(new BigDecimal(20));
			bagCustorderItem3.setPerSdorderUom(UOM_PAL);
			bagCustorderItem3.setExactSdorderQtyFlag(true);
			
			// initialize truck type
			truckTypeBs.setPlanningNetWeightT(new BigDecimal(30));
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeRoadDirect);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(null);
			custorderEntity.setDeliveryFreqEveryTimeUom(null);
			custorderEntity.setDeliveryFreqQtyperhre(null);
			custorderEntity.setDeliveryFreqQtyperhreUom(null);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 11);
			final Date deliveryToDat = cal.getTime();
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(null);
			custorderEntity.setDeliveryTimeToleranceUom(null);
			
			// add bag customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bagCustorderItem1);
			custorderItems.add(bagCustorderItem2);
			custorderItems.add(bagCustorderItem3);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBagSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, truckWrapper);
			
			// call customer order split function
			CustorderUtils.splitCustorder(custorderBagSplitWrapper);
//			System.out.println("### LRQ_003.3 TestCase 3 >>>");
//			System.out.println(custorderBagSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBagSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(3, sdos.size());
			
			for (int i=0; i < 3; i++) {
				
				OrderEntity sdo = sdos.get(i);
				
				// check delivery from/to date&time
				Assert.assertEquals(deliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(deliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				if (i == 0) {
					// check number of order items of order
					@SuppressWarnings("unchecked")
					List<OrderItemEntity> sdois = sdo.getOrderItems();
					Assert.assertEquals(2, sdois.size());
					
					for (OrderItemEntity sdoi : sdois) {
						SapMaterialEntity sapMaterial = sdoi.getSapMaterial();				
						if (sapMaterial.getSapMaterialId().equals(bagCustorderItem1.getSapMaterial().getSapMaterialId())) {
							// check ordered quantity and UoM
							Assert.assertEquals(Constant.UOM_PAL, sdoi.getOrderedUom().getUomCd());
							Assert.assertEquals(0, new BigDecimal(10).compareTo(sdoi.getOrderedQuantity()));
						}
						else if (sapMaterial.getSapMaterialId().equals(bagCustorderItem2.getSapMaterial().getSapMaterialId())) {
							// check ordered quantity and UoM
							Assert.assertEquals(Constant.UOM_PAL, sdoi.getOrderedUom().getUomCd());
							Assert.assertEquals(0, new BigDecimal(8).compareTo(sdoi.getOrderedQuantity()));
						}
						else {
							Assert.fail("SAP material id: " + sapMaterial.getSapMaterialId() + " was not expected");
						}
					}
				}
				else if (i == 1) {
					// check number of order items of order
					@SuppressWarnings("unchecked")
					List<OrderItemEntity> sdois = sdo.getOrderItems();
					Assert.assertEquals(2, sdois.size());
					
					for (OrderItemEntity sdoi : sdois) {
						SapMaterialEntity sapMaterial = sdoi.getSapMaterial();				
						if (sapMaterial.getSapMaterialId().equals(bagCustorderItem2.getSapMaterial().getSapMaterialId())) {
							// check ordered quantity and UoM
							Assert.assertEquals(Constant.UOM_PAL, sdoi.getOrderedUom().getUomCd());
							Assert.assertEquals(0, new BigDecimal(7).compareTo(sdoi.getOrderedQuantity()));
						}
						else if (sapMaterial.getSapMaterialId().equals(bagCustorderItem3.getSapMaterial().getSapMaterialId())) {
							// check ordered quantity and UoM
							Assert.assertEquals(Constant.UOM_TONS, sdoi.getOrderedUom().getUomCd());
							Assert.assertEquals(0, new BigDecimal("14.2").compareTo(sdoi.getOrderedQuantity()));
						}
						else {
							Assert.fail("SAP material id: " + sapMaterial.getSapMaterialId() + " was not expected");
						}
					}
				}
				else if (i == 2) {
					// check number of order items of order
					@SuppressWarnings("unchecked")
					List<OrderItemEntity> sdois = sdo.getOrderItems();
					Assert.assertEquals(1, sdois.size());
					
					for (OrderItemEntity sdoi : sdois) {
						SapMaterialEntity sapMaterial = sdoi.getSapMaterial();				
						if (sapMaterial.getSapMaterialId().equals(bagCustorderItem3.getSapMaterial().getSapMaterialId())) {
							// check ordered quantity and UoM
							Assert.assertEquals(Constant.UOM_TONS, sdoi.getOrderedUom().getUomCd());
							Assert.assertEquals(0, new BigDecimal("1.05").compareTo(sdoi.getOrderedQuantity()));
						}
						else {
							Assert.fail("SAP material id: " + sapMaterial.getSapMaterialId() + " was not expected");
						}
					}
				}
				else {
					Assert.fail("single delivery No. " + i + " was not expected");
				}
			}
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	

	@Test
	public void test_LRQ_003_3_bagWithServiceMaterial(){
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_LRQ_003_3_bagWithServiceMaterial");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bagCustorderItem1.setOrderedQuantity(new BigDecimal(10));
			bagCustorderItem1.setOrderedUom(UOM_PAL);
			bagCustorderItem1.setExactCustorderQtyFlag(false);
			bagCustorderItem2.setOrderedQuantity(new BigDecimal(15));
			bagCustorderItem2.setOrderedUom(UOM_PAL);
			bagCustorderItem2.setExactCustorderQtyFlag(false);
			bagCustorderItem3.setOrderedQuantity(new BigDecimal(15.25));
			bagCustorderItem3.setOrderedUom(UOM_TO);
			bagCustorderItem3.setExactCustorderQtyFlag(false);
			serviceCustorderItem.setOrderedQuantity(new BigDecimal(1));
			serviceCustorderItem.setOrderedUom(UOM_PCE);
			serviceCustorderItem.setExactCustorderQtyFlag(false);
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bagCustorderItem1.setPerSdorderQuantity(new BigDecimal(20));
			bagCustorderItem1.setPerSdorderUom(UOM_PAL);
			bagCustorderItem1.setExactSdorderQtyFlag(true);
			bagCustorderItem2.setPerSdorderQuantity(new BigDecimal(20));
			bagCustorderItem2.setPerSdorderUom(UOM_PAL);
			bagCustorderItem2.setExactSdorderQtyFlag(true);
			bagCustorderItem3.setPerSdorderQuantity(new BigDecimal(20));
			bagCustorderItem3.setPerSdorderUom(UOM_PAL);
			bagCustorderItem3.setExactSdorderQtyFlag(true);
			serviceCustorderItem.setPerSdorderQuantity(new BigDecimal(20));
			serviceCustorderItem.setPerSdorderUom(UOM_PAL);
			serviceCustorderItem.setExactSdorderQtyFlag(true);
			
			// initialize truck type
			truckTypeBs.setPlanningNetWeightT(new BigDecimal(30));
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeRoadDirect);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(null);
			custorderEntity.setDeliveryFreqEveryTimeUom(null);
			custorderEntity.setDeliveryFreqQtyperhre(null);
			custorderEntity.setDeliveryFreqQtyperhreUom(null);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 11);
			final Date deliveryToDat = cal.getTime();
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(null);
			custorderEntity.setDeliveryTimeToleranceUom(null);
			
			// add bag customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bagCustorderItem1);
			custorderItems.add(bagCustorderItem2);
			custorderItems.add(bagCustorderItem3);
			custorderItems.add(serviceCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBagSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, truckWrapper);
			
			// call customer order split function
			CustorderUtils.splitCustorder(custorderBagSplitWrapper);
//						System.out.println("### LRQ_003.3 TestCase 3 >>>");
//						System.out.println(custorderBagSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBagSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(3, sdos.size());
			
			for (int i=0; i < 3; i++) {
				
				OrderEntity sdo = sdos.get(i);
				
				// check delivery from/to date&time
				Assert.assertEquals(deliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(deliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				if (i == 0) {
					// check number of order items of order
					@SuppressWarnings("unchecked")
					List<OrderItemEntity> sdois = sdo.getOrderItems();
					Assert.assertEquals(3, sdois.size());
					
					for (OrderItemEntity sdoi : sdois) {
						SapMaterialEntity sapMaterial = sdoi.getSapMaterial();				
						if (sapMaterial.getSapMaterialId().equals(bagCustorderItem1.getSapMaterial().getSapMaterialId())) {
							// check ordered quantity and UoM
							Assert.assertEquals(Constant.UOM_PAL, sdoi.getOrderedUom().getUomCd());
							Assert.assertEquals(0, new BigDecimal(10).compareTo(sdoi.getOrderedQuantity()));
						}
						else if (sapMaterial.getSapMaterialId().equals(bagCustorderItem2.getSapMaterial().getSapMaterialId())) {
							// check ordered quantity and UoM
							Assert.assertEquals(Constant.UOM_PAL, sdoi.getOrderedUom().getUomCd());
							Assert.assertEquals(0, new BigDecimal(8).compareTo(sdoi.getOrderedQuantity()));
						}
						else if (sapMaterial.getSapMaterialId().equals(serviceCustorderItem.getSapMaterial().getSapMaterialId())) {
							// check ordered quantity and UoM
							Assert.assertEquals(Constant.UOM_PCE, sdoi.getOrderedUom().getUomCd());
							Assert.assertEquals(0, new BigDecimal(1).compareTo(sdoi.getOrderedQuantity()));
						}
						else {
							Assert.fail("SAP material id: " + sapMaterial.getSapMaterialId() + " was not expected");
						}
					}
				}
				else if (i == 1) {
					// check number of order items of order
					@SuppressWarnings("unchecked")
					List<OrderItemEntity> sdois = sdo.getOrderItems();
					Assert.assertEquals(3, sdois.size());
					
					for (OrderItemEntity sdoi : sdois) {
						SapMaterialEntity sapMaterial = sdoi.getSapMaterial();				
						if (sapMaterial.getSapMaterialId().equals(bagCustorderItem2.getSapMaterial().getSapMaterialId())) {
							// check ordered quantity and UoM
							Assert.assertEquals(Constant.UOM_PAL, sdoi.getOrderedUom().getUomCd());
							Assert.assertEquals(0, new BigDecimal(7).compareTo(sdoi.getOrderedQuantity()));
						}
						else if (sapMaterial.getSapMaterialId().equals(bagCustorderItem3.getSapMaterial().getSapMaterialId())) {
							// check ordered quantity and UoM
							Assert.assertEquals(Constant.UOM_TONS, sdoi.getOrderedUom().getUomCd());
							Assert.assertEquals(0, new BigDecimal("14.2").compareTo(sdoi.getOrderedQuantity()));
						}
						else if (sapMaterial.getSapMaterialId().equals(serviceCustorderItem.getSapMaterial().getSapMaterialId())) {
							// check ordered quantity and UoM
							Assert.assertEquals(Constant.UOM_PCE, sdoi.getOrderedUom().getUomCd());
							Assert.assertEquals(0, new BigDecimal(1).compareTo(sdoi.getOrderedQuantity()));
						}
						else {
							Assert.fail("SAP material id: " + sapMaterial.getSapMaterialId() + " was not expected");
						}
					}
				}
				else if (i == 2) {
					// check number of order items of order
					@SuppressWarnings("unchecked")
					List<OrderItemEntity> sdois = sdo.getOrderItems();
					Assert.assertEquals(2, sdois.size());
					
					for (OrderItemEntity sdoi : sdois) {
						SapMaterialEntity sapMaterial = sdoi.getSapMaterial();				
						if (sapMaterial.getSapMaterialId().equals(bagCustorderItem3.getSapMaterial().getSapMaterialId())) {
							// check ordered quantity and UoM
							Assert.assertEquals(Constant.UOM_TONS, sdoi.getOrderedUom().getUomCd());
							Assert.assertEquals(0, new BigDecimal("1.05").compareTo(sdoi.getOrderedQuantity()));
						}
						else if (sapMaterial.getSapMaterialId().equals(serviceCustorderItem.getSapMaterial().getSapMaterialId())) {
							// check ordered quantity and UoM
							Assert.assertEquals(Constant.UOM_PCE, sdoi.getOrderedUom().getUomCd());
							Assert.assertEquals(0, new BigDecimal(1).compareTo(sdoi.getOrderedQuantity()));
						}
						else {
							Assert.fail("SAP material id: " + sapMaterial.getSapMaterialId() + " was not expected");
						}
					}
				}
				else {
					Assert.fail("single delivery No. " + i + " was not expected");
				}
			}
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	@Test
	public void test_multiorder_normal_road_direct_LKW() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_multiorder_normal_road_direct_LKW");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bulkCustorderItem.setOrderedQuantity(new BigDecimal(3));
			bulkCustorderItem.setOrderedUom(UOM_LKW);
			bulkCustorderItem.setExactCustorderQtyFlag(false);
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bulkCustorderItem.setPerSdorderQuantity(new BigDecimal(25));
			bulkCustorderItem.setPerSdorderUom(UOM_TO);
			bulkCustorderItem.setExactSdorderQtyFlag(false);
			bulkCustorderItem.setCustorder(custorderEntity);
			
			// initialize truck type
			truckTypeBs.setPlanningNetWeightT(new BigDecimal(25));
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeRoadDirect);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(null);
			custorderEntity.setDeliveryFreqEveryTimeUom(null);
			custorderEntity.setDeliveryFreqQtyperhre(null);
			custorderEntity.setDeliveryFreqQtyperhreUom(null);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 11);
			final Date deliveryToDat = cal.getTime();
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(null);
			custorderEntity.setDeliveryTimeToleranceUom(null);
			
			// add bulk customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bulkCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBulkSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, truckWrapper);
			
			// call the bulk customer order split function
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
			//			System.out.println("### test_multiorder_normal_road_direct_LKW >>>");
			//			System.out.println(custorderBulkSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBulkSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(3, sdos.size());
			
			for (OrderEntity sdo : sdos) {
				// check delivery from/to date&time
				Assert.assertEquals(deliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(deliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				Assert.assertEquals(Constant.UOM_LKW, sdoi.getOrderedUom().getUomCd());
				Assert.assertEquals(0, BigDecimal.ONE.compareTo(sdoi.getOrderedQuantity()));
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	@Test
	public void test_multiorder_normal_road_direct_CON() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_multiorder_road_direct_CON");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bulkCustorderItem.setOrderedQuantity(new BigDecimal(3));
			bulkCustorderItem.setOrderedUom(UOM_CONTAINER);
			bulkCustorderItem.setExactCustorderQtyFlag(false);
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bulkCustorderItem.setPerSdorderQuantity(new BigDecimal(25));
			bulkCustorderItem.setPerSdorderUom(UOM_TO);
			bulkCustorderItem.setExactSdorderQtyFlag(false);
			bulkCustorderItem.setCustorder(custorderEntity);
			
			// initialize truck type
			truckTypeBs.setPlanningNetWeightT(new BigDecimal(25));
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeRoadDirect);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(null);
			custorderEntity.setDeliveryFreqEveryTimeUom(null);
			custorderEntity.setDeliveryFreqQtyperhre(null);
			custorderEntity.setDeliveryFreqQtyperhreUom(null);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 11);
			final Date deliveryToDat = cal.getTime();
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(null);
			custorderEntity.setDeliveryTimeToleranceUom(null);
			
			// add bulk customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bulkCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBulkSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, truckWrapper);
			
			// call the bulk customer order split function
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
//						System.out.println("### test_multiorder_normal_road_direct_CON() >>>");
//						System.out.println(custorderBulkSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBulkSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(3, sdos.size());
			
			for (OrderEntity sdo : sdos) {
				// check delivery from/to date&time
				Assert.assertEquals(deliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(deliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				Assert.assertEquals(Constant.UOM_CONTAINER, sdoi.getOrderedUom().getUomCd());
				Assert.assertEquals(0, BigDecimal.ONE.compareTo(sdoi.getOrderedQuantity()));
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	@Test
	public void test_multiorder_normal_rail_direct_BW() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_multiorder_normal_rail_direct_BW");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bulkCustorderItem.setOrderedQuantity(new BigDecimal(3));
			bulkCustorderItem.setOrderedUom(UOM_WAGON);
			bulkCustorderItem.setExactCustorderQtyFlag(false);
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bulkCustorderItem.setPerSdorderQuantity(new BigDecimal(25));
			bulkCustorderItem.setPerSdorderUom(UOM_TO);
			bulkCustorderItem.setExactSdorderQtyFlag(false);
			bulkCustorderItem.setCustorder(custorderEntity);
			
			// initialize wagon type
			wagonTypeBs.setMaxGrossWeightT(new BigDecimal(90));
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeRailDirect);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(null);
			custorderEntity.setDeliveryFreqEveryTimeUom(null);
			custorderEntity.setDeliveryFreqQtyperhre(null);
			custorderEntity.setDeliveryFreqQtyperhreUom(null);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 11);
			final Date deliveryToDat = cal.getTime();
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(null);
			custorderEntity.setDeliveryTimeToleranceUom(null);
			
			// add bulk customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bulkCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBulkSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, wagonWrapper);
			
			// call the bulk customer order split function
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
			//			System.out.println("### test_multiorder_normal_rail_direct_BW() >>>");
			//			System.out.println(custorderBulkSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBulkSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(3, sdos.size());
			
			for (OrderEntity sdo : sdos) {
				// check delivery from/to date&time
				Assert.assertEquals(deliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(deliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				Assert.assertEquals(Constant.UOM_WAGON, sdoi.getOrderedUom().getUomCd());
				Assert.assertEquals(0, BigDecimal.ONE.compareTo(sdoi.getOrderedQuantity()));
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	@Test
	public void test_multiorder_normal_rail_direct_CON() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_multiorder_normal_rail_direct_CON");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bulkCustorderItem.setOrderedQuantity(new BigDecimal(3));
			bulkCustorderItem.setOrderedUom(UOM_CONTAINER);
			bulkCustorderItem.setExactCustorderQtyFlag(false);
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bulkCustorderItem.setPerSdorderQuantity(new BigDecimal(25));
			bulkCustorderItem.setPerSdorderUom(UOM_TO);
			bulkCustorderItem.setExactSdorderQtyFlag(false);
			bulkCustorderItem.setCustorder(custorderEntity);
			
			// initialize wagon type
			wagonTypeBs.setMaxGrossWeightT(new BigDecimal(90));
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeRailDirect);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(null);
			custorderEntity.setDeliveryFreqEveryTimeUom(null);
			custorderEntity.setDeliveryFreqQtyperhre(null);
			custorderEntity.setDeliveryFreqQtyperhreUom(null);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 11);
			final Date deliveryToDat = cal.getTime();
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(null);
			custorderEntity.setDeliveryTimeToleranceUom(null);
			
			// add bulk customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bulkCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBulkSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, wagonWrapper);
			
			// call the bulk customer order split function
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
			//			System.out.println("### test_multiorder_normal_rail_direct_CON >>>");
			//			System.out.println(custorderBulkSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBulkSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(1, sdos.size());
			
			for (OrderEntity sdo : sdos) {
				// check delivery from/to date&time
				Assert.assertEquals(deliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(deliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(3, sdois.size());
				
				for (OrderItemEntity sdoi : sdois) {					
					// check ordered quantity and UoM
					Assert.assertEquals(Constant.UOM_CONTAINER, sdoi.getOrderedUom().getUomCd());
					Assert.assertEquals(0, BigDecimal.ONE.compareTo(sdoi.getOrderedQuantity()));
				}
				
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	@Test
	public void test_multiorder_normal_rail_direct_TO() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_multiorder_normal_rail_direct_TO");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bulkCustorderItem.setOrderedQuantity(new BigDecimal(1000));
			bulkCustorderItem.setOrderedUom(UOM_TO);
			bulkCustorderItem.setExactCustorderQtyFlag(false);
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bulkCustorderItem.setPerSdorderQuantity(new BigDecimal(25));
			bulkCustorderItem.setPerSdorderUom(UOM_TO);
			bulkCustorderItem.setExactSdorderQtyFlag(false);
			bulkCustorderItem.setCustorder(custorderEntity);
			
			// initialize wagon type
			wagonTypeBs.setMaxGrossWeightT(new BigDecimal(90));
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeRailDirect);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(null);
			custorderEntity.setDeliveryFreqEveryTimeUom(null);
			custorderEntity.setDeliveryFreqQtyperhre(null);
			custorderEntity.setDeliveryFreqQtyperhreUom(null);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 11);
			final Date deliveryToDat = cal.getTime();
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(null);
			custorderEntity.setDeliveryTimeToleranceUom(null);
			
			// add bulk customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bulkCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBulkSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, wagonWrapper);
			
			// call the bulk customer order split function
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
			//			System.out.println("### test_multiorder_normal_rail_direct_TO >>>");
			//			System.out.println(custorderBulkSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBulkSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(1, sdos.size());
			
			for (OrderEntity sdo : sdos) {
				// check delivery from/to date&time
				Assert.assertEquals(deliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(deliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				Assert.assertEquals(Constant.UOM_TONS, sdoi.getOrderedUom().getUomCd());
				Assert.assertEquals(0, bulkCustorderItem.getOrderedQuantity().compareTo(sdoi.getOrderedQuantity()));
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	@Test
	public void test_multiorder_normal_bimodal_rail_BW() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_multiorder_normal_bimodal_rail_BW");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bulkCustorderItem.setOrderedQuantity(new BigDecimal(3));
			bulkCustorderItem.setOrderedUom(UOM_WAGON);
			bulkCustorderItem.setExactCustorderQtyFlag(false);
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bulkCustorderItem.setPerSdorderQuantity(new BigDecimal(25));
			bulkCustorderItem.setPerSdorderUom(UOM_TO);
			bulkCustorderItem.setExactSdorderQtyFlag(false);
			bulkCustorderItem.setCustorder(custorderEntity);
			
			// initialize wagon type
			wagonTypeBs.setMaxGrossWeightT(new BigDecimal(90));
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeBimodalRail);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(null);
			custorderEntity.setDeliveryFreqEveryTimeUom(null);
			custorderEntity.setDeliveryFreqQtyperhre(null);
			custorderEntity.setDeliveryFreqQtyperhreUom(null);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 11);
			final Date deliveryToDat = cal.getTime();
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(null);
			custorderEntity.setDeliveryTimeToleranceUom(null);
			
			// add bulk customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bulkCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBulkSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, wagonWrapper);
			
			// call the bulk customer order split function
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
			//			System.out.println("### test_multiorder_normal_bimodal_rail_BW >>>");
			//			System.out.println(custorderBulkSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBulkSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(3, sdos.size());
			
			for (OrderEntity sdo : sdos) {
				// check delivery from/to date&time
				Assert.assertEquals(deliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(deliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				Assert.assertEquals(Constant.UOM_WAGON, sdoi.getOrderedUom().getUomCd());
				Assert.assertEquals(0, BigDecimal.ONE.compareTo(sdoi.getOrderedQuantity()));
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	@Test
	public void test_multiorder_normal_bimodal_rail_CON() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_multiorder_normal_bimodal_rail_CON");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bulkCustorderItem.setOrderedQuantity(new BigDecimal(3));
			bulkCustorderItem.setOrderedUom(UOM_CONTAINER);
			bulkCustorderItem.setExactCustorderQtyFlag(false);
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bulkCustorderItem.setPerSdorderQuantity(new BigDecimal(25));
			bulkCustorderItem.setPerSdorderUom(UOM_TO);
			bulkCustorderItem.setExactSdorderQtyFlag(false);
			bulkCustorderItem.setCustorder(custorderEntity);
			
			// initialize wagon type
			wagonTypeBs.setMaxGrossWeightT(new BigDecimal(90));
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeBimodalRail);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(null);
			custorderEntity.setDeliveryFreqEveryTimeUom(null);
			custorderEntity.setDeliveryFreqQtyperhre(null);
			custorderEntity.setDeliveryFreqQtyperhreUom(null);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 11);
			final Date deliveryToDat = cal.getTime();
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(null);
			custorderEntity.setDeliveryTimeToleranceUom(null);
			
			// add bulk customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bulkCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBulkSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, wagonWrapper);
			
			// call the bulk customer order split function
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
			//			System.out.println("### test_multiorder_normal_bimodal_rail_CON >>>");
			//			System.out.println(custorderBulkSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBulkSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(3, sdos.size());
			
			for (OrderEntity sdo : sdos) {
				// check delivery from/to date&time
				Assert.assertEquals(deliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(deliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				Assert.assertEquals(Constant.UOM_CONTAINER, sdoi.getOrderedUom().getUomCd());
				Assert.assertEquals(0, BigDecimal.ONE.compareTo(sdoi.getOrderedQuantity()));
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	@Test
	public void test_multiorder_normal_bimodal_rail_TO() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_multiorder_normal_bimodal_rail_TO");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bulkCustorderItem.setOrderedQuantity(new BigDecimal(1000));
			bulkCustorderItem.setOrderedUom(UOM_TO);
			bulkCustorderItem.setExactCustorderQtyFlag(false);
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bulkCustorderItem.setPerSdorderQuantity(new BigDecimal(25));
			bulkCustorderItem.setPerSdorderUom(UOM_TO);
			bulkCustorderItem.setExactSdorderQtyFlag(false);
			bulkCustorderItem.setCustorder(custorderEntity);
			
			// initialize wagon type
			wagonTypeBs.setMaxGrossWeightT(new BigDecimal(90));
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeBimodalRail);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(null);
			custorderEntity.setDeliveryFreqEveryTimeUom(null);
			custorderEntity.setDeliveryFreqQtyperhre(null);
			custorderEntity.setDeliveryFreqQtyperhreUom(null);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 11);
			final Date deliveryToDat = cal.getTime();
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(null);
			custorderEntity.setDeliveryTimeToleranceUom(null);
			
			// add bulk customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bulkCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBulkSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, wagonWrapper);
			
			// call the bulk customer order split function
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
			//			System.out.println("### test_multiorder_normal_bimodal_rail_TO >>>");
			//			System.out.println(custorderBulkSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBulkSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(1, sdos.size());
			
			for (OrderEntity sdo : sdos) {
				// check delivery from/to date&time
				Assert.assertEquals(deliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(deliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				Assert.assertEquals(Constant.UOM_TONS, sdoi.getOrderedUom().getUomCd());
				Assert.assertEquals(0, bulkCustorderItem.getOrderedQuantity().compareTo(sdoi.getOrderedQuantity()));
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	@Test
	public void test_multiorder_normal_bimodal_road_LKW() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_multiorder_normal_bimodal_road_LKW");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bulkCustorderItem.setOrderedQuantity(new BigDecimal(3));
			bulkCustorderItem.setOrderedUom(UOM_LKW);
			bulkCustorderItem.setExactCustorderQtyFlag(false);
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bulkCustorderItem.setPerSdorderQuantity(new BigDecimal(25));
			bulkCustorderItem.setPerSdorderUom(UOM_TO);
			bulkCustorderItem.setExactSdorderQtyFlag(false);
			bulkCustorderItem.setCustorder(custorderEntity);
			
			// initialize wagon type
			truckTypeBs.setPlanningNetWeightT(new BigDecimal(25));
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeBimodalRoad);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(null);
			custorderEntity.setDeliveryFreqEveryTimeUom(null);
			custorderEntity.setDeliveryFreqQtyperhre(null);
			custorderEntity.setDeliveryFreqQtyperhreUom(null);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 11);
			final Date deliveryToDat = cal.getTime();
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(null);
			custorderEntity.setDeliveryTimeToleranceUom(null);
			
			// add bulk customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bulkCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBulkSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, truckWrapper);
			
			// call the bulk customer order split function
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
			//			System.out.println("### test_multiorder_normal_bimodal_road_LKW >>>");
			//			System.out.println(custorderBulkSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBulkSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(3, sdos.size());
			
			for (OrderEntity sdo : sdos) {
				// check delivery from/to date&time
				Assert.assertEquals(deliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(deliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				Assert.assertEquals(Constant.UOM_LKW, sdoi.getOrderedUom().getUomCd());
				Assert.assertEquals(0, BigDecimal.ONE.compareTo(sdoi.getOrderedQuantity()));
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	@Test
	public void test_multiorder_normal_bimodal_road_CON() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_multiorder_normal_bimodal_road_CON");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bulkCustorderItem.setOrderedQuantity(new BigDecimal(3));
			bulkCustorderItem.setOrderedUom(UOM_CONTAINER);
			bulkCustorderItem.setExactCustorderQtyFlag(false);
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bulkCustorderItem.setPerSdorderQuantity(new BigDecimal(25));
			bulkCustorderItem.setPerSdorderUom(UOM_TO);
			bulkCustorderItem.setExactSdorderQtyFlag(false);
			bulkCustorderItem.setCustorder(custorderEntity);
			
			// initialize wagon type
			truckTypeBs.setPlanningNetWeightT(new BigDecimal(25));
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeBimodalRoad);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(null);
			custorderEntity.setDeliveryFreqEveryTimeUom(null);
			custorderEntity.setDeliveryFreqQtyperhre(null);
			custorderEntity.setDeliveryFreqQtyperhreUom(null);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 11);
			final Date deliveryToDat = cal.getTime();
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(null);
			custorderEntity.setDeliveryTimeToleranceUom(null);
			
			// add bulk customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bulkCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBulkSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, truckWrapper);
			
			// call the bulk customer order split function
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
			//			System.out.println("### test_multiorder_normal_bimodal_road_CON >>>");
			//			System.out.println(custorderBulkSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBulkSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(3, sdos.size());
			
			for (OrderEntity sdo : sdos) {
				// check delivery from/to date&time
				Assert.assertEquals(deliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(deliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				Assert.assertEquals(Constant.UOM_CONTAINER, sdoi.getOrderedUom().getUomCd());
				Assert.assertEquals(0, BigDecimal.ONE.compareTo(sdoi.getOrderedQuantity()));
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	@Test
	public void test_multiorder_normal_bimodal_road_TO() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_multiorder_normal_bimodal_road_TO");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bulkCustorderItem.setOrderedQuantity(new BigDecimal(1000));
			bulkCustorderItem.setOrderedUom(UOM_TO);
			bulkCustorderItem.setExactCustorderQtyFlag(false);
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bulkCustorderItem.setPerSdorderQuantity(new BigDecimal(25));
			bulkCustorderItem.setPerSdorderUom(UOM_TO);
			bulkCustorderItem.setExactSdorderQtyFlag(false);
			bulkCustorderItem.setCustorder(custorderEntity);
			
			// initialize wagon type
			truckTypeBs.setPlanningNetWeightT(new BigDecimal(25));
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeBimodalRail);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(null);
			custorderEntity.setDeliveryFreqEveryTimeUom(null);
			custorderEntity.setDeliveryFreqQtyperhre(null);
			custorderEntity.setDeliveryFreqQtyperhreUom(null);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 11);
			final Date deliveryToDat = cal.getTime();
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(null);
			custorderEntity.setDeliveryTimeToleranceUom(null);
			
			// add bulk customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bulkCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBulkSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, truckWrapper);
			
			// call the bulk customer order split function
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
			//			System.out.println("### test_multiorder_normal_bimodal_road_TO >>>");
			//			System.out.println(custorderBulkSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBulkSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(1, sdos.size());
			
			for (OrderEntity sdo : sdos) {
				// check delivery from/to date&time
				Assert.assertEquals(deliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(deliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				Assert.assertEquals(Constant.UOM_TONS, sdoi.getOrderedUom().getUomCd());
				Assert.assertEquals(0, bulkCustorderItem.getOrderedQuantity().compareTo(sdoi.getOrderedQuantity()));
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	@Test
	public void test_multiorder_performance_road_LKW() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_LRQ_070_0_case7");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bulkCustorderItem.setOrderedQuantity(new BigDecimal(3));
			bulkCustorderItem.setOrderedUom(UOM_LKW);
			bulkCustorderItem.setExactCustorderQtyFlag(false);
			
			// initialize truck type and related converter
			truckTypeBs.setPlanningNetWeightT(new BigDecimal(30));
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bulkCustorderItem.setPerSdorderQuantity(truckWrapper.getMaxGrossWeightT());
			bulkCustorderItem.setPerSdorderUom(UOM_TO);
			bulkCustorderItem.setExactSdorderQtyFlag(false);
			bulkCustorderItem.setCustorder(custorderEntity);

			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeRoadDirect);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 11);
			final Date deliveryToDat = null;
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(0.0);
			custorderEntity.setDeliveryTimeToleranceUom(Constant.UOM_MIN);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(null);
			custorderEntity.setDeliveryFreqEveryTimeUom(null);
			custorderEntity.setDeliveryFreqQtyperhre(BigDecimal.ONE);
			custorderEntity.setDeliveryFreqQtyperhreUom(UOM_LKW);
			
			// add bulk customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bulkCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBulkSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, truckWrapper);
			
			// call the bulk customer order split function
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
//			System.out.println("### test_multiorder_performance_road_LKW>>>");
//			System.out.println(custorderBulkSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBulkSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(3, sdos.size());
			
			// initialize delivery frequency and tolerance in milliseconds
			BigDecimal perSdoQtyInOrderedUom = custorderBulkSplitWrapper.convertQuantity(
					bulkCustorderItem.getSapMaterial()
					, bulkCustorderItem.getPerSdorderUom().getUomCd()
					, bulkCustorderItem.getOrderedUom().getUomCd()
					, bulkCustorderItem.getPerSdorderQuantity()).getQuantity();
			BigDecimal qtyPerHreInOrderedUom = custorderBulkSplitWrapper.convertQuantity(
					bulkCustorderItem.getSapMaterial()
					, custorderEntity.getDeliveryFreqQtyperhreUom().getUomCd()
					, bulkCustorderItem.getOrderedUom().getUomCd()
					, custorderEntity.getDeliveryFreqQtyperhre()).getQuantity();
			long deliveryFqyInMillisec = Math.round(perSdoQtyInOrderedUom.divide(qtyPerHreInOrderedUom, Constant.SCALE, BigDecimal.ROUND_HALF_UP).doubleValue() * 3600000);
			long deliveryTimeToleranceInMillisec = Math.round(custorderEntity.getDeliveryTimeTolerance() * 60.0 * 1000.0);
			
			for (int i=0; i < sdos.size(); i++) {
				OrderEntity sdo = sdos.get(i);
				
				// check delivery from/to date&time
				Date expectedDeliveryFromDat = new Date(deliveryFromDat.getTime() + i * deliveryFqyInMillisec - deliveryTimeToleranceInMillisec);
				Date expectedDeliveryToDat = new Date(deliveryFromDat.getTime() + i * deliveryFqyInMillisec + deliveryTimeToleranceInMillisec);
				Assert.assertEquals(expectedDeliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(expectedDeliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				Assert.assertEquals(Constant.UOM_LKW, sdoi.getOrderedUom().getUomCd());
				Assert.assertEquals(0, BigDecimal.ONE.compareTo(sdoi.getOrderedQuantity()));
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	@Test
	public void test_multiorder_performance_road_CON() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_LRQ_070_0_case7");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bulkCustorderItem.setOrderedQuantity(new BigDecimal(3));
			bulkCustorderItem.setOrderedUom(UOM_CONTAINER);
			bulkCustorderItem.setExactCustorderQtyFlag(false);
			
			// initialize truck type and related converter
			truckTypeBs.setPlanningNetWeightT(new BigDecimal(3));
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bulkCustorderItem.setPerSdorderQuantity(truckWrapper.getMaxGrossWeightT());
			bulkCustorderItem.setPerSdorderUom(UOM_TO);
			bulkCustorderItem.setExactSdorderQtyFlag(false);
			bulkCustorderItem.setCustorder(custorderEntity);
			
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypeRoadDirect);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 11);
			final Date deliveryToDat = null;
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(0.0);
			custorderEntity.setDeliveryTimeToleranceUom(Constant.UOM_MIN);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(null);
			custorderEntity.setDeliveryFreqEveryTimeUom(null);
			custorderEntity.setDeliveryFreqQtyperhre(BigDecimal.ONE);
			custorderEntity.setDeliveryFreqQtyperhreUom(UOM_CONTAINER);
			
			// add bulk customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bulkCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBulkSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, truckWrapper);
			
			// call the bulk customer order split function
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
//			System.out.println("### test_multiorder_performance_road_CON >>>");
//			System.out.println(custorderBulkSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBulkSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(3, sdos.size());
			
			// initialize delivery frequency and tolerance in milliseconds
			BigDecimal perSdoQtyInOrderedUom = custorderBulkSplitWrapper.getVehicleMaxCapacityInUomCd(bulkCustorderItem.getSapMaterial(), bulkCustorderItem.getOrderedUom().getUomCd());
			BigDecimal qtyPerHreInOrderedUom = custorderBulkSplitWrapper.convertQuantity(
					bulkCustorderItem.getSapMaterial()
					, custorderEntity.getDeliveryFreqQtyperhreUom().getUomCd()
					, bulkCustorderItem.getOrderedUom().getUomCd()
					, custorderEntity.getDeliveryFreqQtyperhre()).getQuantity();
			long deliveryFqyInMillisec = Math.round(perSdoQtyInOrderedUom.divide(qtyPerHreInOrderedUom, Constant.SCALE, BigDecimal.ROUND_HALF_UP).doubleValue() * 3600000);
			long deliveryTimeToleranceInMillisec = Math.round(custorderEntity.getDeliveryTimeTolerance() * 60.0 * 1000.0);
			
			for (int i=0; i < sdos.size(); i++) {
				OrderEntity sdo = sdos.get(i);
				
				// check delivery from/to date&time
				Date expectedDeliveryFromDat = new Date(deliveryFromDat.getTime() + i * deliveryFqyInMillisec - deliveryTimeToleranceInMillisec);
				Date expectedDeliveryToDat = new Date(deliveryFromDat.getTime() + i * deliveryFqyInMillisec + deliveryTimeToleranceInMillisec);
				Assert.assertEquals(expectedDeliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(expectedDeliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				Assert.assertEquals(Constant.UOM_CONTAINER, sdoi.getOrderedUom().getUomCd());
				Assert.assertEquals(0, BigDecimal.ONE.compareTo(sdoi.getOrderedQuantity()));
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}
	
	@Test
	public void test_multiorder_performance_pickup_LKW() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_LRQ_070_0_case7");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bulkCustorderItem.setOrderedQuantity(new BigDecimal(3));
			bulkCustorderItem.setOrderedUom(UOM_LKW);
			bulkCustorderItem.setExactCustorderQtyFlag(false);
			
			// initialize truck type and related converter
			truckTypeBs.setPlanningNetWeightT(new BigDecimal(30));
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bulkCustorderItem.setPerSdorderQuantity(truckWrapper.getMaxGrossWeightT());
			bulkCustorderItem.setPerSdorderUom(UOM_TO);
			bulkCustorderItem.setExactSdorderQtyFlag(false);
			bulkCustorderItem.setCustorder(custorderEntity);
			
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypePickup);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 11);
			final Date deliveryToDat = null;
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(0.0);
			custorderEntity.setDeliveryTimeToleranceUom(Constant.UOM_MIN);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(null);
			custorderEntity.setDeliveryFreqEveryTimeUom(null);
			custorderEntity.setDeliveryFreqQtyperhre(BigDecimal.ONE);
			custorderEntity.setDeliveryFreqQtyperhreUom(UOM_LKW);
			
			// add bulk customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bulkCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBulkSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, truckWrapper);
			
			// call the bulk customer order split function
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
//			System.out.println("### test_multiorder_performance_pickup_LKW");
//			System.out.println(custorderBulkSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBulkSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(3, sdos.size());
			
			// initialize delivery frequency and tolerance in milliseconds
			BigDecimal perSdoQtyInOrderedUom = custorderBulkSplitWrapper.convertQuantity(
					bulkCustorderItem.getSapMaterial()
					, bulkCustorderItem.getPerSdorderUom().getUomCd()
					, bulkCustorderItem.getOrderedUom().getUomCd()
					, bulkCustorderItem.getPerSdorderQuantity()).getQuantity();
			BigDecimal qtyPerHreInOrderedUom = custorderBulkSplitWrapper.convertQuantity(
					bulkCustorderItem.getSapMaterial()
					, custorderEntity.getDeliveryFreqQtyperhreUom().getUomCd()
					, bulkCustorderItem.getOrderedUom().getUomCd()
					, custorderEntity.getDeliveryFreqQtyperhre()).getQuantity();
			long deliveryFqyInMillisec = Math.round(perSdoQtyInOrderedUom.divide(qtyPerHreInOrderedUom, Constant.SCALE, BigDecimal.ROUND_HALF_UP).doubleValue() * 3600000);
			long deliveryTimeToleranceInMillisec = Math.round(custorderEntity.getDeliveryTimeTolerance() * 60.0 * 1000.0);
			
			for (int i=0; i < sdos.size(); i++) {
				OrderEntity sdo = sdos.get(i);
				
				// check delivery from/to date&time
				Date expectedDeliveryFromDat = new Date(deliveryFromDat.getTime() + i * deliveryFqyInMillisec - deliveryTimeToleranceInMillisec);
				Date expectedDeliveryToDat = new Date(deliveryFromDat.getTime() + i * deliveryFqyInMillisec + deliveryTimeToleranceInMillisec);
				Assert.assertEquals(expectedDeliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(expectedDeliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				Assert.assertEquals(Constant.UOM_LKW, sdoi.getOrderedUom().getUomCd());
				Assert.assertEquals(0, BigDecimal.ONE.compareTo(sdoi.getOrderedQuantity()));
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}
	
	@Test
	public void test_multiorder_performance_pickup_CON() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "test_LRQ_070_0_case7");
		}
		// ]]
		try {
			// initialize ordered quantity / UoM / exact customer order flag
			bulkCustorderItem.setOrderedQuantity(new BigDecimal(3));
			bulkCustorderItem.setOrderedUom(UOM_CONTAINER);
			bulkCustorderItem.setExactCustorderQtyFlag(false);
			
			// initialize truck type and related converter
			truckTypeBs.setPlanningNetWeightT(new BigDecimal(3));
			
			// initialize per single delivery quantity / UoM / exact single delivery quantity flag
			bulkCustorderItem.setPerSdorderQuantity(truckWrapper.getMaxGrossWeightT());
			bulkCustorderItem.setPerSdorderUom(UOM_TO);
			bulkCustorderItem.setExactSdorderQtyFlag(false);
			bulkCustorderItem.setCustorder(custorderEntity);
			
			
			// initialize delivery type
			custorderEntity.setDeliveryType(deliveryTypePickup);
			
			// initialize customer order delivery from/to date&time
			Calendar cal = new GregorianCalendar();
			cal.set(2012, 06, 20, 7, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			final Date deliveryFromDat = cal.getTime();
			cal.add(Calendar.HOUR_OF_DAY, 11);
			final Date deliveryToDat = null;
			custorderEntity.setDeliveryFromDat(deliveryFromDat);
			custorderEntity.setDeliveryToDat(deliveryToDat);
			
			// initialize delivery time tolerance
			custorderEntity.setDeliveryTimeTolerance(0.0);
			custorderEntity.setDeliveryTimeToleranceUom(Constant.UOM_MIN);
			
			// initialize "every X time units" and "X units per hour"
			custorderEntity.setDeliveryFreqEveryTime(null);
			custorderEntity.setDeliveryFreqEveryTimeUom(null);
			custorderEntity.setDeliveryFreqQtyperhre(BigDecimal.ONE);
			custorderEntity.setDeliveryFreqQtyperhreUom(UOM_CONTAINER);
			
			// add bulk customer order item to customer order
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			custorderItems.add(bulkCustorderItem);
			custorderEntity.setCustorderItems(custorderItems);
			
			// initialize customer order split wrapper
			CustorderSplitWrapper custorderBulkSplitWrapper = custorderService.createCustorderSplitWrapper(custorderEntity, truckWrapper);
			
			// call the bulk customer order split function
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
//			System.out.println("### test_multiorder_performance_pickup_CON >>>");
//			System.out.println(custorderBulkSplitWrapper.toString());
			
			// check results
			@SuppressWarnings("unchecked")
			List<OrderEntity> sdos = custorderBulkSplitWrapper.getCustorder().getOrders();
			
			// check number of single delivery orders
			Assert.assertEquals(3, sdos.size());
			
			// initialize delivery frequency and tolerance in milliseconds
			BigDecimal perSdoQtyInOrderedUom = custorderBulkSplitWrapper.getVehicleMaxCapacityInUomCd(bulkCustorderItem.getSapMaterial(), bulkCustorderItem.getOrderedUom().getUomCd());
			BigDecimal qtyPerHreInOrderedUom = custorderBulkSplitWrapper.convertQuantity(
					bulkCustorderItem.getSapMaterial()
					, custorderEntity.getDeliveryFreqQtyperhreUom().getUomCd()
					, bulkCustorderItem.getOrderedUom().getUomCd()
					, custorderEntity.getDeliveryFreqQtyperhre()).getQuantity();
			long deliveryFqyInMillisec = Math.round(perSdoQtyInOrderedUom.divide(qtyPerHreInOrderedUom, Constant.SCALE, BigDecimal.ROUND_HALF_UP).doubleValue() * 3600000);
			long deliveryTimeToleranceInMillisec = Math.round(custorderEntity.getDeliveryTimeTolerance() * 60.0 * 1000.0);
			
			for (int i=0; i < sdos.size(); i++) {
				OrderEntity sdo = sdos.get(i);
				
				// check delivery from/to date&time
				Date expectedDeliveryFromDat = new Date(deliveryFromDat.getTime() + i * deliveryFqyInMillisec - deliveryTimeToleranceInMillisec);
				Date expectedDeliveryToDat = new Date(deliveryFromDat.getTime() + i * deliveryFqyInMillisec + deliveryTimeToleranceInMillisec);
				Assert.assertEquals(expectedDeliveryFromDat.toString(), sdo.getDeliveryFromDat().toString());
				Assert.assertEquals(expectedDeliveryToDat.toString(), sdo.getDeliveryToDat().toString());
				
				// check number of order items of order
				@SuppressWarnings("unchecked")
				List<OrderItemEntity> sdois = sdo.getOrderItems();
				Assert.assertEquals(1, sdois.size());
				OrderItemEntity sdoi = sdois.get(0);
				
				// check ordered quantity and UoM
				Assert.assertEquals(Constant.UOM_CONTAINER, sdoi.getOrderedUom().getUomCd());
				Assert.assertEquals(0, BigDecimal.ONE.compareTo(sdoi.getOrderedQuantity()));
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}
	
}
