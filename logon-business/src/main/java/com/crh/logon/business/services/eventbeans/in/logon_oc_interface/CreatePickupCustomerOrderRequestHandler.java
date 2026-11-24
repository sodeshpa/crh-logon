package com.crh.logon.business.services.eventbeans.in.logon_oc_interface;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import com.crh.logon.business.services.beans.CustorderDTO;
import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.CustorderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.utils.OrderUtils;
import com.crh.logon.business.stores.sap.ContractStoreInterface;
import com.crh.logon.business.stores.sap.ShiptoSareaStoreInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.CustorderItemEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.refdata.CustorderStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderCreationTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.PackageTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.SoldshipSareaFlexSrchEntity;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.logon_oc_interface.BusinessPartnerContactData;
import com.holcim.logon.eventbeans.in.logon_oc_interface.CreatePickupCustomerOrderRequest;
import com.holcim.logon.eventbeans.in.logon_oc_interface.CreatePickupCustomerOrderResponse;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class CreatePickupCustomerOrderRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {

	private static final String FIELD_ORDER = "order";
	private static final String FIELD_ORDER_SOLDTO = ".soldTo";
	private static final String FIELD_ORDER_SHIPTO = ".shipTo";
	private static final String FIELD_ORDER_SUPPLYPLANTNO = ".supplyPlantNo";
	private static final String FIELD_ORDER_OI_MATERIAL = ".orderItem[%d].material";
	private static final String FIELD_ORDER_OI_MATERIAL_PRODPLANTNO = ".orderItem[%d].material.prodPlantNo";
	private static final String FIELD_ORDER_OI_MATERIAL_MATERIALNO = ".orderItem[%d].material.materialNo";
	private static final String FIELD_PAYER_SAP_ID = FIELD_ORDER + ".payerSapId";
	private static final String FIELD_BILLTO_SAP_ID = FIELD_ORDER + ".billtoSapId";
	private static final String FIELD_ORDER_CONTRACT_NUMBER = ".contractNumber";
	private static final String FIELD_ORDER_SHIPPINGPOINTNO = ".shippingpointNo";
	private static final String FIELD_ORDER_OI_MATERIAL_CONTRACTITEMNUMBER = ".orderItem[%d].material.contractItemNumber";
	private static final String FIELD_ORDER_OI_MATERIAL_QUANTITY_UOM = ".orderItem[%d].material.quantityUom";
	private static final String FIELD_MATERIALNO = ".materialNo";
	private static final String FIELD_PRODPLANTNO = ".prodPlantNo";
	private static final String FIELD_UOM = ".quantityUom";
	private static final String  FIELD_ORDER_SHIPTO_SAP_ID = FIELD_ORDER_SHIPTO + ".sapId";


	@EJB
	ShiptoSareaStoreInterface shiptoSareaStore;

	@EJB
	CustorderServiceLocalInterface customerOrderService;

	@EJB private OrderServiceLocalInterface orderService;

	@EJB
	ContractStoreInterface contractStore;

	@Override
	protected void validate(LogonEventRequest eventBean) {
		final CreatePickupCustomerOrderRequest request = (CreatePickupCustomerOrderRequest) eventBean;
		final CreatePickupCustomerOrderRequest.Order orderData = request.getOrder();

		validateMandatory(FIELD_ORDER, orderData);
		validateMandatory(FIELD_ORDER + FIELD_ORDER_SHIPPINGPOINTNO, orderData.getShippingpointNo());
		validateMandatory(FIELD_ORDER + FIELD_ORDER_SUPPLYPLANTNO, orderData.getSupplyPlantNo());
		validateMandatory(FIELD_ORDER + FIELD_ORDER_CONTRACT_NUMBER, orderData.getContractNumber());

		int i = 0;
		final List<CreatePickupCustomerOrderRequest.Order.OrderItem> orderItemList = orderData.getOrderItem();
		if (orderItemList.isEmpty()) {
			throw newApplicationError("OrderItem is mandatory, please provide!");
		}

		for (CreatePickupCustomerOrderRequest.Order.OrderItem orderItem : orderItemList) {
			validateMandatory(String.format(FIELD_ORDER + FIELD_ORDER_OI_MATERIAL_PRODPLANTNO, i), orderItem.getProdPlantNo());
			validateMandatory(String.format(FIELD_ORDER + FIELD_ORDER_OI_MATERIAL_MATERIALNO, i), orderItem.getMaterialNo());
			validateMandatory(String.format(FIELD_ORDER + FIELD_ORDER_OI_MATERIAL_CONTRACTITEMNUMBER, i), orderItem.getContractItemNumber());
			validateMandatory(String.format(FIELD_ORDER + FIELD_ORDER_OI_MATERIAL_QUANTITY_UOM, i), orderItem.getQuantityUom());
			i++;
		}
	}


	/**
	 * Generates {@link CustorderItemEntity}s based on the values available
	 * @param custorderEntity
	 * @param orderData
	 * @return list of generated {@link CustorderItemEntity}s
	 */
	protected final void generateCustorderItemsFromOrderData(CustorderEntity custorderEntity, CreatePickupCustomerOrderRequest.Order orderData) {

		final List<CustorderItemEntity> custorderItemDataList = custorderEntity.getCustorderItems();

		CustorderItemEntity custorderItemEntity;
		//Create CustorderItemEntities (for simple material items)
		int i = 0;
		for (CreatePickupCustomerOrderRequest.Order.OrderItem orderItemData : orderData.getOrderItem()) {

			custorderItemEntity = new CustorderItemEntity();
			custorderItemEntity.setCustorder(custorderEntity);
			setOrderItemDataFieldsToCustorderItem(custorderItemEntity, orderItemData, i);
			custorderItemEntity.setExactCustorderQtyFlag(Boolean.FALSE); // nullable
			custorderItemEntity.setExactSdorderQtyFlag(Boolean.FALSE); // nullable
			custorderItemDataList.add(custorderItemEntity);
			i++;
		}

		// Determine the (logon) package type of the material items
		PackageTypeBsEntity pkgType = null;
		if (!custorderItemDataList.isEmpty()) {
			pkgType = custorderItemDataList.get(0).getSapMaterial().getMaterialPackage().getLogonPackageType();
		}

		// Create CustorderItemEntities (for service material items)
		//		Because of that several not applicable fields are not initialized.
		//		Service items are symbolizing services provided by Crh to the customer. Like 'Cleaning' or 'Weekend delivery'.
		//

		i = 0;
		for (CreatePickupCustomerOrderRequest.Order.ServiceMaterial serviceMaterialData : orderData.getServiceMaterial()) {

			custorderItemEntity = new CustorderItemEntity();
			custorderItemEntity.setCustorder(custorderEntity);
			custorderItemEntity.setExactCustorderQtyFlag(Boolean.FALSE); // nullable
			custorderItemEntity.setExactSdorderQtyFlag(Boolean.FALSE); // nullable
			setServiceMaterialDataToCustorderItem(custorderItemEntity, serviceMaterialData, i);

			custorderItemDataList.add(custorderItemEntity);
			i++;
		}

		// If the (logon) package type is bag, set to true for all custorderItems
		if (pkgType != null && pkgType.getPackageTypeCd().equals(Constant.PACKAGE_TYPE_BBAG)) {
			for (CustorderItemEntity item : custorderItemDataList) {
				item.setExactSdorderQtyFlag(Boolean.TRUE);
			}
		}
	}

	/**
	 * Generates {@link CustorderItemEntity} customer order item entities from the data provided.
	 * The custorder item is an ordinary bag/bulk material.
	 *
	 * @param custorderItemEntity
	 * @param orderItemData - general order item data, which is not material specific
	 * @param i
	 */
	private void setOrderItemDataFieldsToCustorderItem(CustorderItemEntity custorderItemEntity,
			CreatePickupCustomerOrderRequest.Order.OrderItem orderItemData,
			int i) {

		ShippingpointEntity shippingpoint = custorderItemEntity.getCustorder().getShippingpoint();

		if (shippingpoint != null) {
			PlantBsEntity plant = shippingpoint.getPlant();
			if ((plant.getPlantCd() != null) && (orderItemData.getLoadingpointNo() != null)) {
				LoadingpointEntity loadingpoint = getLoadingpoint(plant.getPlantCd(), orderItemData.getLoadingpointNo());
				custorderItemEntity.setLoadingpoint(loadingpoint); // nullable
			}
		}

		Long contractItemNo = null;
		if (orderItemData.getContractItemNumber() != null) {
			contractItemNo = Long.valueOf(orderItemData.getContractItemNumber());
		}

		SapContractItemEntity sapContractItem = getSapContractItemEntity(
				custorderItemEntity.getCustorder().getSapContract(),
				contractItemNo,
				String.format(FIELD_ORDER + FIELD_ORDER_OI_MATERIAL, i) + ".contractItemNumber");
		custorderItemEntity.setSapContractItem(sapContractItem); // nullable

		setMaterialDataFieldsToCustorderItem(custorderItemEntity, orderItemData, i);

	}

	/**
	 * Initializes {link {@link CustorderItemEntity} object's material
	 * @param custorderItemEntity
	 * @param materialData
	 * @param i
	 */
	private void setMaterialDataFieldsToCustorderItem(
			final CustorderItemEntity custorderItemEntity,
			final CreatePickupCustomerOrderRequest.Order.OrderItem materialData, final int i) {

		final String materialFieldName = String.format(FIELD_ORDER + FIELD_ORDER_OI_MATERIAL, i);
		final SapMaterialEntity sapMaterial = getMaterial(materialData.getMaterialNo(), materialFieldName + FIELD_MATERIALNO);
		final PlantBsEntity productionPlant = getPlant(materialData.getProdPlantNo(), materialFieldName + FIELD_PRODPLANTNO);
		final UomBsEntity quantityUom = getUom(materialData.getQuantityUom(), materialFieldName + FIELD_UOM, true);

		custorderItemEntity.setMixDesign(null); // nullable
		final BigDecimal orderedQuantity = materialData.getQuantity();
		custorderItemEntity.setSapMaterial(sapMaterial); // NOT nullable
		custorderItemEntity.setProductionPlant(productionPlant); // NOT nullable
		custorderItemEntity.setOrderedUom(quantityUom); // nullable
		custorderItemEntity.setOrderedQuantity(orderedQuantity); // nullable
		custorderItemEntity.setPerSdorderQuantity(null); // nullable
		custorderItemEntity.setPerSdorderUom(quantityUom); // nullable
		custorderItemEntity.setShiptoSilo(null);
	}

	/**
	 * Generates {@link CustorderItemEntity} customer order item entities from the data provided.
	 * This custorder item is special, as it is a service customer order item. Certain fields are not
	 * applicable for services, others are optional.
	 *
	 * @param custorderItemEntity
	 * @param serviceMaterialData - general order item data, which is not material specific
	 * @param i
	 */
	private void setServiceMaterialDataToCustorderItem(
			CustorderItemEntity custorderItemEntity, CreatePickupCustomerOrderRequest.Order.ServiceMaterial serviceMaterialData,
			int i) {

		custorderItemEntity.setLoadingpoint(null);

		if (StringUtils.isNotEmpty(serviceMaterialData.getContractItemNumber())) {
			SapContractItemEntity  sapContractItem = getSapContractItemEntity(
					custorderItemEntity.getCustorder().getSapContract(),
					serviceMaterialData.getContractItemNumber(),
					String.format(FIELD_ORDER + FIELD_ORDER_OI_MATERIAL, i) + ".contractItemNumber");
			custorderItemEntity.setSapContractItem(sapContractItem); // nullable
		}
		else {
			custorderItemEntity.setSapContractItem(null);
		}

		final SapMaterialEntity sapMaterial;
		final String materialFieldName = String.format(FIELD_ORDER + FIELD_ORDER_OI_MATERIAL, i);
		final PlantBsEntity productionPlant;
		final UomBsEntity quantityUom;
		sapMaterial = getMaterial(serviceMaterialData.getMaterialNo(), materialFieldName + FIELD_MATERIALNO);
		productionPlant = getPlant(serviceMaterialData.getProdPlantNo(), materialFieldName + FIELD_PRODPLANTNO);
		quantityUom = getUom(serviceMaterialData.getQuantityUom(), materialFieldName + FIELD_UOM);
		final BigDecimal orderedQuantity = serviceMaterialData.getQuantity();
		custorderItemEntity.setSapMaterial(sapMaterial); // NOT nullable
		custorderItemEntity.setProductionPlant(productionPlant); // NOT nullable
		custorderItemEntity.setOrderedUom(quantityUom); // nullable
		custorderItemEntity.setOrderedQuantity(orderedQuantity); // nullable
		custorderItemEntity.setPerSdorderQuantity(null); // nullable
		custorderItemEntity.setPerSdorderUom(quantityUom); // nullable
		custorderItemEntity.setShiptoSilo(null);

	}

	@Override
	protected LogonEventResponse perform(LogonEventRequest eventBean) {
		final CreatePickupCustomerOrderRequest request = (CreatePickupCustomerOrderRequest) eventBean;
		final CreatePickupCustomerOrderResponse response = createLogonOKEventResponse(CreatePickupCustomerOrderResponse.class);

		final CreatePickupCustomerOrderRequest.Order orderData = request.getOrder();

		final SapBpEntity soldTo = getSoldTo(orderData.getSoldTo().getSapId(), FIELD_ORDER + FIELD_ORDER_SOLDTO + ".sapId");
		final ShiptoEntity shipTo = getShipTo(orderData.getShipTo().getSapId(), FIELD_ORDER + FIELD_ORDER_SHIPTO_SAP_ID);

		final CustorderEntity custorderEntity = new CustorderEntity();
		if (((CreatePickupCustomerOrderRequest) eventBean).getClientType().equals(CLIENT_TYPE_CUSTOMER_PORTAL)) {
			final OrderCreationTypeBsEntity ocInterfaceCreator =
					referentialDataService.findBsEntity(OrderCreationTypeBsEntity.class, Constant.ORDER_CREATION_TYPE_OC_CUSTOMER_PORTAL);
			custorderEntity.setOrderCreationType(ocInterfaceCreator); // NOT nullable
		}
		else {
			final OrderCreationTypeBsEntity ocInterfaceCreator =
					referentialDataService.findBsEntity(OrderCreationTypeBsEntity.class, Constant.ORDER_CREATION_TYPE_OC_INTERFACE);
			custorderEntity.setOrderCreationType(ocInterfaceCreator); // NOT nullable
		}

		initCustorder(orderData, soldTo,  shipTo, custorderEntity);

		CustorderDTO processCustorderResult = null;
		generateCustorderItemsFromOrderData(custorderEntity, orderData);

		processCustorderResult = customerOrderService.processCustomerOrder(custorderEntity, null, false);

		final BusinessPartnerContactData soldToContactData = orderData.getSoldTo().getBusinessPartnerContactData();
		final BusinessPartnerContactData shipToContactData= orderData.getShipTo().getBusinessPartnerContactData();
		preProcessForOneTimeSoldToAndShipTo(custorderEntity, soldToContactData, shipToContactData);

		CustorderEntity detachedCustomerOrderCreationResult = customerOrderService.initializeAndStoreOrders(
				processCustorderResult, true, true, Boolean.FALSE,
				null, Constant.WEIGH_SYSTEM_LP_LOGON, false, null, false, null, true, false);

		String custorderNumber = null;

		if (detachedCustomerOrderCreationResult != null && detachedCustomerOrderCreationResult.getCustorderNumber() != null) {
			custorderNumber = detachedCustomerOrderCreationResult.getCustorderNumber().toString();
		}

		response.setCustomerOrderNo(custorderNumber);

		// We need to get the orders here again because they became detached when initializeAndStoreOrders returned.
		CustorderEntity attachedCustOrderEntity = getDaoProvider().getCustorderEntityDao().findById(detachedCustomerOrderCreationResult.getCustorderId());

		List<AssignmentEntity> transmitableAssignments = customerOrderService.createNewOrders(true,
				true, Boolean.FALSE,
				attachedCustOrderEntity.getOrders(), null,
				Constant.WEIGH_SYSTEM_LP_LOGON, false);

		em.flush();

		if (transmitableAssignments != null && !transmitableAssignments.isEmpty()) {
			transmitableAssignments.forEach(transmittableAssignment -> {
				orderService.transmitToWeighSystem(transmittableAssignment);
			});
		}

		attachedCustOrderEntity.getOrders().forEach(orderEntity -> {
			CreatePickupCustomerOrderResponse.Order order = new CreatePickupCustomerOrderResponse.Order();
			order.setLogonOrderNo(orderEntity.getOrderNumber().toString());
			response.getOrder().add(order);
		});
		
		return response;
	}

	private final void initCustorder(CreatePickupCustomerOrderRequest.Order orderData, SapBpEntity soldTo,
			ShiptoEntity shipTo, CustorderEntity custorderEntity) {

		custorderEntity.setReleaseSapFlag(Boolean.TRUE); // NOT nullable
		custorderEntity.setFreeDeliveryFlag(Boolean.FALSE); // NOT nullable
		custorderEntity.setSapSuppressMinChargeFlag(Boolean.FALSE);
		custorderEntity.setOnHoldOrderAndLockedFlag(Boolean.FALSE); // NOT nullable

		SapContractEntity contract = contractStore.getSapContractByContractNumber(orderData.getContractNumber());
		final String billToSapId = orderData.getBillToSapId();
		final SapBpEntity billTo = getBillToSapBp(billToSapId, FIELD_BILLTO_SAP_ID, soldTo, contract.getSarea(), shipTo);
		final String payerSapId = orderData.getPayerSapId();
		final SapBpEntity payer = getPayerSapBp(payerSapId, FIELD_PAYER_SAP_ID, soldTo, contract.getSarea());
		if (payer == null && !OrderUtils.isPayerIgnored(contract)) {
			final String sapIfcId = orderData.getPayerSapId();
			throw newEntityNotFoundError("Payer", FIELD_PAYER_SAP_ID, sapIfcId);
		}

		final List<SoldshipSareaFlexSrchEntity> commonSalesAreasForShiptoSoldto =
				shiptoSareaStore.getCommonSalesAreasForShiptoSoldto(
						shipTo, soldTo, contract.getSarea());

		if (commonSalesAreasForShiptoSoldto.isEmpty()) {
			throw newApplicationError(String.format(
					"The shipto and soldto specified are not partners in the sales area. Shipto id = %s, Soldto id = %s, Sales "
							+ "area %s",
					shipTo.getShiptoSapBp().getSapIfcId(),
					soldTo.getSapIfcId(),
					contract.getSarea().getSareaDisplayLongname()));
		}

		final DeliveryTypeBsEntity deliveryTypeCd =
				referentialDataService.findBsEntity(DeliveryTypeBsEntity.class, Constant.DELIVERY_TYPE_PICKUP);
		final PlantBsEntity supplyPlant = getPlant(orderData.getSupplyPlantNo(), FIELD_ORDER + FIELD_ORDER_SUPPLYPLANTNO);
		String shippingpointNo = orderData.getShippingpointNo();
		final ShippingpointEntity shippingpoint = getShippingpoint(shippingpointNo, supplyPlant);

		final String custorderCustrefNo = orderData.getCustorderCustrefNo();
		custorderEntity.setCustorderCustrefNo(custorderCustrefNo); // nullable
		final CustorderStatusBsEntity status =
				referentialDataService.findBsEntity(CustorderStatusBsEntity.class, Constant.CUSTORDER_STATUS_NEW);
		custorderEntity.setCustorderStatus(status);  // NOT nullable
		custorderEntity.setSapContract(contract); // NOT nullable
		custorderEntity.setShiptoSapBp(shipTo.getShiptoSapBp()); // NOT nullable
		custorderEntity.setSoldtoSapBp(soldTo); // NOT nullable
		custorderEntity.setBilltoSapBp(billTo); // nullable
		custorderEntity.setPayerSapBp(payer); // nullable
		custorderEntity.setSarea(contract.getSarea()); // NOT nullable

		custorderEntity.setShippingpoint(shippingpoint); // nullable
		custorderEntity.setDeliveryType(deliveryTypeCd); // NOT nullable
		//moved to orderservice, nothing is using this here
		//		custorderEntity.setDistributionDest(distributionDestCd); // NOT nullable
		final Date deliveryFromDat = (Date) ObjectUtils.defaultIfNull(orderData.getEarliestDeliveryTime(), new Date());
		custorderEntity.setDeliveryFromDat(deliveryFromDat); // NOT nullable
		custorderEntity.setDeliveryToDat(getDeliveryToDat(shipTo, getConfigBean().getAppDefaultShiptoOpen2ToMin(),
				orderData.getLatestDeliveryTime(),
				deliveryFromDat));// NOT nullable
		custorderEntity.setContactPersonTxt(null); // nullable
		custorderEntity.setOrderConfEmailFlag(
				(Boolean) ObjectUtils.defaultIfNull(shipTo.getOrderConfEmailFlag(), Boolean.FALSE)); // NOT nullable
		custorderEntity.setOrderConfEmailAdresses(shipTo.getOrderConfEmailAddresses()); // nullable
		custorderEntity.setOrderConfFaxFlag(
				(Boolean) ObjectUtils.defaultIfNull(shipTo.getOrderConfFaxFlag(), Boolean.FALSE)); // NOT nullable
		custorderEntity.setOrderConfFaxNumber(shipTo.getOrderConfFaxNumber()); // nullable
		custorderEntity.setOrderConfSmsFlag(
				(Boolean) ObjectUtils.defaultIfNull(shipTo.getOrderConfSmsFlag(), Boolean.FALSE)); // NOT nullable
		custorderEntity.setOrderConfSmsNumber(shipTo.getOrderConfSmsNumber()); // nullable
		custorderEntity.setDelivnoteEmailFlag(
				(Boolean) ObjectUtils.defaultIfNull(shipTo.getEmailDelivnoteFlag(), Boolean.FALSE)); // NOT nullable
		custorderEntity.setDelivnoteFaxFlag(
				(Boolean) ObjectUtils.defaultIfNull(shipTo.getFaxDelivnoteFlag(), Boolean.FALSE)); // NOT nullable

		custorderEntity.setLoadConfEmailFlag((Boolean) ObjectUtils.defaultIfNull(shipTo.getLoadConfEmailFlag(), Boolean.FALSE));
		custorderEntity.setLoadConfFaxFlag((Boolean) ObjectUtils.defaultIfNull(shipTo.getLoadConfFaxFlag(), Boolean.FALSE));
		custorderEntity.setLoadConfSmsFlag((Boolean) ObjectUtils.defaultIfNull(shipTo.getLoadConfSmsFlag(), Boolean.FALSE));

		custorderEntity.setDelivnoteSinglePaperFlag( // NOT nullable
				(Boolean) ObjectUtils.defaultIfNull(
						orderData.isPrintDeliveryNote(),
						ObjectUtils.defaultIfNull(shipTo.getPaperSingleDelnoteFlagCem(),
								Boolean.FALSE)));
		custorderEntity.setDelivnoteCollPaperFlag( // NOT nullable
				(Boolean) ObjectUtils.defaultIfNull(shipTo.getPaperCollDelnoteFlagCem(),
						Boolean.FALSE));

		custorderEntity.setCustWeborderNote(null); // nullable

		custorderEntity.setOpenCustorderFlag(Boolean.FALSE);

		custorderEntity.setCustorderItems(new ArrayList<>());
	}


}