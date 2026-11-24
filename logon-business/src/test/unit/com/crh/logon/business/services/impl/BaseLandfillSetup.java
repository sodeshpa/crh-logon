/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.crh.logon.business.services.localinterfaces.AsyncMopEventOutServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TruckAdminServiceLocalInterface;
import com.crh.logon.business.services.utils.ConfSeqLock;
import com.crh.logon.persistence.entities.common.SareaEntity;
import org.junit.Before;

import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.GoodsMovementServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.LDSServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.testdtos.TruckComposition;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.EventSource;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.refdata.AssignmentPartTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.AssignmentTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.MaterialPackageBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.PackageTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.SapVehicleCharBsEntity;
import com.crh.logon.persistence.entities.refdata.ShiptoTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.TruckTransportCatBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;

/**
 * This abstract class aims to setup (create) all entities needed for a junit test
 * testing landfill cases. All such test classes should extend this class. This class
 * shouldn't be used as an individual test class, that's why it is abstract.
 * 
 * @author AdNovum Informatik AG
 */
public abstract class BaseLandfillSetup {

	protected BizeventEntity event;
	protected LoadEntity load;
	protected LoadingpointEntity loadingpoint;
	protected BigDecimal tareWeightT;
	protected BigDecimal grossWeightT;
	protected BigDecimal netQuantity;
	protected EventSource source;
	protected boolean isAccurate;
	protected boolean validateComposition;
	protected ConfSeqLock lock;
	
	//the class to be tested
	protected MockOrderServiceBean orderServiceBean;
	
	protected AsyncMopEventOutServiceLocalInterface asyncMopEventOutService;
	protected DispoServiceLocalInterface dispoService;
	protected GoodsMovementServiceLocalInterface goodsMovementService;
	protected RefDataGenericsServiceLocalInterface refDataGenericsService;
	protected TruckAdminServiceLocalInterface truckAdminService;
	protected LDSServiceLocalInterface ldsService;
	
	protected OrderEntity order;
	protected TruckEntity truck;
	protected DriverEntity driver;
	
	protected final Date SYSDATE = new Date();
	
	private final Long LANDFILL_SAP_MATERIAL_ID = -5010L;
	private final Long LANDFILL_MATERIAL_PACKAGE_TYPE_CD = Constant.PACKAGE_TYPE_BULK_DUMPED;
	private final BigDecimal LANDFILL_TRUCK_TARE_WEIGHT_T = new BigDecimal(7);
	private final BigDecimal LANDFILL_TRUCK_GROSS_WEIGHT_T = new BigDecimal(42);
	private final Long LANDFILL_TRUCK_TRSP_CAT_BULK_DUMPED = Constant.TRUCK_TRSP_CAT_BULK_DUMPED;
	
	@Before
	public void setup() {
		TruckComposition truckComposition = setupTruckComposition(LANDFILL_TRUCK_TARE_WEIGHT_T, 
				LANDFILL_TRUCK_GROSS_WEIGHT_T, LANDFILL_TRUCK_TRSP_CAT_BULK_DUMPED);
		truck = truckComposition.getTruck();
		TrailerEntity trailer = truckComposition.getTrailer();
		ContainerEntity container = truckComposition.getContainer();
		
		driver = setupDriver(truck);
		AssignmentEntity assignment = setupAssignment(truck);
		event = setupBizEvent(assignment, truck);
		UomBsEntity uomBsEntityTons = setupUomBsEntityTons();
		SapMaterialEntity sapMaterial = setupSapMaterial(LANDFILL_SAP_MATERIAL_ID, LANDFILL_MATERIAL_PACKAGE_TYPE_CD, uomBsEntityTons);
		load = setupLoad(sapMaterial, uomBsEntityTons);
		List<OrderItemEntity> orderItems = setupOrderItems(load, sapMaterial);
		order = setupOrder(orderItems);
		List<AssignmentPartEntity> assignmentParts = setupAssignmentParts(assignment, load, order);
		
		loadingpoint = null;
		ShiptoEntity shipto = setupShipto();
		
		
		MockOrderServiceBean spyOrderServiceBean = new MockOrderServiceBean();
		orderServiceBean  = spy(spyOrderServiceBean);
		orderServiceBean.setOrderItems(orderItems);
		orderServiceBean.setAssignment(assignment);
		orderServiceBean.setAssignmentParts(assignmentParts);
		
		//mocking services
		createMockServices();
		
		when(truckAdminService.fetchTrailerForTruck(truck.getTruckId(), false)).thenReturn(trailer);
		when(truckAdminService.fetchContainerForTruckTrailerComposition(truck, trailer)).thenReturn(container);
		when(truckAdminService.fetchDriver(truck.getTruckId())).thenReturn(driver);
		when(refDataGenericsService.findBsEntity(UomBsEntity.class, Constant.UOM_TONS)).thenReturn(uomBsEntityTons);
		when(goodsMovementService.createGoodsMovementEntity(load, null, true, false, false)).thenReturn(null);		
		doNothing().when(orderServiceBean).cancelOnWeighSystem(assignment, order.getShippingpoint()) ;

	
		SapBpEntity shiptoSapBp = order.getShiptoSapBp();
		when(dispoService.fetchShipto(shiptoSapBp.getSapBpId())).thenReturn(shipto);
		
		setMockServices();
	}
	
	private DriverEntity setupDriver(TruckEntity truck) {
		DriverEntity driver = new DriverEntity();
		driver.setCurrentTruck(truck);
		
		return driver;
	}
	
	private AssignmentEntity setupAssignment(TruckEntity truck) {
		AssignmentEntity assignment = new AssignmentEntity();
		assignment.setAssignmentId(1001L);
		AssignmentTypeBsEntity assignmentType = new AssignmentTypeBsEntity();
		assignmentType.setAssignmentTypeCd(Constant.ASSIGNMENT_TYPE_INTERNAL);
		assignment.setAssignmentType(assignmentType);
		assignment.setTruck(truck);
		
		return assignment;
	}
	
	private TruckComposition setupTruckComposition(BigDecimal tareWeightT, BigDecimal maxGrossWeightT, 
			Long truckTranportCategoryCd) {
		
		TruckEntity truck = new TruckEntity();
		truck.setTruckId(1003L);
		truck.setTareWeightT(tareWeightT);
		truck.setMaxGrossWeightT(maxGrossWeightT);
		SapVehicleCharBsEntity sapVehicleChar = new SapVehicleCharBsEntity();
		sapVehicleChar.setSapVehicleCharCd(1L);
		truck.setSapVehicleChar(sapVehicleChar);
		TruckTransportCatBsEntity transportCatBs = new TruckTransportCatBsEntity();
		transportCatBs.setTruckTransportCatCd(truckTranportCategoryCd);
		truck.setTruckTransportCat(transportCatBs);
		
		TrailerEntity trailer = null;
		ContainerEntity container = null;
		
		TruckComposition truckComposition = new TruckComposition(truck, trailer, container);
		return truckComposition;
	}
	
	private BizeventEntity setupBizEvent(AssignmentEntity assignment, TruckEntity truck) {
		BizeventEntity event = new BizeventEntity();
		event.setAssignment(assignment);
		event.setTruck(truck);
		event.setEventTs(SYSDATE);
		
		return event;
	}
	
	private SapMaterialEntity setupSapMaterial(Long sapMaterialId, Long packageTypeCd, UomBsEntity uomBsEntityTons) {
		SapMaterialEntity sapMaterial = new SapMaterialEntity();
		sapMaterial.setSapMaterialId(sapMaterialId);
		sapMaterial.setMaterialIsLf(true);
		sapMaterial.setBaseUom(uomBsEntityTons);
		
		MaterialPackageBsEntity materialPackageBs = new MaterialPackageBsEntity();
		PackageTypeBsEntity logonPackageType = new PackageTypeBsEntity();
		logonPackageType.setPackageTypeCd(packageTypeCd);
		materialPackageBs.setLogonPackageType(logonPackageType);
		sapMaterial.setMaterialPackage(materialPackageBs);
		
		return sapMaterial;
	}
	
	private LoadEntity setupLoad(SapMaterialEntity sapMaterial, UomBsEntity uomBsEntityTons) {
		LoadEntity load = new LoadEntity();
		load.setLoadId(1000L);
		load.setSapMaterial(sapMaterial);
		load.setNetQuantityUom(uomBsEntityTons);
		load.setLdsOfflineMode(false);
		
		return load;
	}
	
	private List<OrderItemEntity> setupOrderItems(LoadEntity load, SapMaterialEntity sapMaterial) {
		OrderItemEntity orderItem = new OrderItemEntity();
		orderItem.setSapMaterial(sapMaterial);
		orderItem.setLoad(load);
		
		List<OrderItemEntity> orderItems = new ArrayList<OrderItemEntity>();
		orderItems.add(orderItem);
		
		return orderItems;
	}
	
	private OrderEntity setupOrder(List<OrderItemEntity> orderItems) {
		OrderEntity order = new OrderEntity();
		order.setArchivedFlag(false);
		order.setNoPdaOrRailEventsFlag(false);
		order.setOrderId(1002L);
		order.setOrderItems(orderItems);
		for (OrderItemEntity orderItem : orderItems) {
			orderItem.setOrder(order);
		}
		OrderStatusBsEntity orderStatus = new OrderStatusBsEntity();
		order.setOrderStatus(orderStatus);
		DeliveryTypeBsEntity deliveryTypeBs = new DeliveryTypeBsEntity();
		deliveryTypeBs.setDeliveryTypeCd(Constant.DELIVERY_TYPE_ROAD_DIRECT);
		order.setDeliveryType(deliveryTypeBs);
		order.setDeliveryFromDat(SYSDATE);
		
		SapBpEntity shiptoSapBp = new SapBpEntity();
		shiptoSapBp.setSapBpId(1004L);
		order.setShiptoSapBp(shiptoSapBp);
		SareaEntity sarea = new SareaEntity();
		sarea.setITrackAvailableFlag(Boolean.FALSE);
		order.setSarea(sarea);
		return order;
	}
	
	private List<AssignmentPartEntity> setupAssignmentParts(AssignmentEntity assignment, LoadEntity load, OrderEntity order) {
		if (assignment.getAssignmentParts() == null) {
			List<AssignmentPartEntity> assignmentParts = new ArrayList<AssignmentPartEntity>();
			assignment.setAssignmentParts(assignmentParts);
		}
		List<AssignmentPartEntity> assignmentParts = assignment.getAssignmentParts();
		
		AssignmentPartEntity assignmentPartLoadType = setupLoadAssignmentPart(assignment, load);
		AssignmentPartEntity assignmentPartUnloadType = setupUnloadAssignmentPart(assignment, order);
		
		assignmentParts.add(assignmentPartLoadType);
		assignmentParts.add(assignmentPartUnloadType);
		
		return assignmentParts;
	}
	
	private AssignmentPartEntity setupLoadAssignmentPart(AssignmentEntity assignment, LoadEntity load) {
		AssignmentPartEntity assignmentPartLoadType = new AssignmentPartEntity();
		assignmentPartLoadType.setLoad(load);
		assignmentPartLoadType.setAssignment(assignment);
		assignmentPartLoadType.setAssignmentPartType(setupAssignmentPartType(Constant.ASSIGNMENT_PART_TYPE_LOAD));
		assignmentPartLoadType.setAssignmentPartSeqNo(2L);
		assignmentPartLoadType.setRealStart(SYSDATE);
		assignmentPartLoadType.setPlannedStart(SYSDATE);
		
		return assignmentPartLoadType;
	}
	
	private AssignmentPartEntity setupUnloadAssignmentPart(AssignmentEntity assignment, OrderEntity order) {
		AssignmentPartEntity assignmentPartUnloadType = new AssignmentPartEntity();
		assignmentPartUnloadType.setOrder(order);
		assignmentPartUnloadType.setAssignment(assignment);
		assignmentPartUnloadType.setAssignmentPartType(setupAssignmentPartType(Constant.ASSIGNMENT_PART_TYPE_UNLOAD));
		assignmentPartUnloadType.setAssignmentPartSeqNo(4L);
		assignmentPartUnloadType.setRealStart(SYSDATE);
		assignmentPartUnloadType.setPlannedStart(SYSDATE);
		
		return assignmentPartUnloadType;
	}
	
	private AssignmentPartTypeBsEntity setupAssignmentPartType(long assignmentPartTypeBsCd) {
		AssignmentPartTypeBsEntity assignmentPartTypeBs = new AssignmentPartTypeBsEntity();
		assignmentPartTypeBs.setAssignmentPartTypeCd(assignmentPartTypeBsCd);
		
		return assignmentPartTypeBs;
	}
	
	private UomBsEntity setupUomBsEntityTons() {
		UomBsEntity uomBsEntityTons = new UomBsEntity();
		uomBsEntityTons.setUomCd(Constant.UOM_TONS);
		uomBsEntityTons.setNumeratorForConversion(1L);
		uomBsEntityTons.setDenominatorForConversion(1L);
		uomBsEntityTons.setFuzzyQuantityFlag(false);
		
		return uomBsEntityTons;
	}
	
	private ShiptoEntity setupShipto() {
		ShiptoTypeBsEntity shiptoTypeBs = new ShiptoTypeBsEntity();
		shiptoTypeBs.setShiptoTypeCd(Constant.SHIPTO_TYPE_STANDARD);
		ShiptoEntity shipto = new ShiptoEntity();
		shipto.setShiptoType(shiptoTypeBs);
		
		return shipto;
	}
	
	private void createMockServices() {
		truckAdminService = mock(TruckAdminServiceLocalInterface.class);
		refDataGenericsService = mock(RefDataGenericsServiceLocalInterface.class);
		goodsMovementService = mock(GoodsMovementServiceLocalInterface.class);
		dispoService = mock(DispoServiceLocalInterface.class);
		asyncMopEventOutService = mock(AsyncMopEventOutServiceLocalInterface.class);
	}
	
	private void setMockServices() {
		orderServiceBean.setTruckAdminService(truckAdminService);
		orderServiceBean.setRefDataGenericsService(refDataGenericsService);
		orderServiceBean.setGoodsMovementService(goodsMovementService);
		orderServiceBean.setDispoService(dispoService);
		orderServiceBean.setAsyncMopEventOutService(asyncMopEventOutService);		
	}
}
