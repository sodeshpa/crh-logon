/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.crh.logon.order.web.forms.bestellung.BestellungForm;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.ordercreation.DeliveryOptionConfigurationDTO;
import com.crh.logon.business.ordercreation.DeliverySettingFilterLogic;
import com.crh.logon.business.ordercreation.DeliverySettingSalesAreaMap;
import com.crh.logon.business.ordercreation.DeliverySettingShiptoMap;
import com.crh.logon.business.ordercreation.dto.DeliverySettingBasicDTO;
import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.business.ordercreation.dto.LoadingPointDTO;
import com.crh.logon.business.ordercreation.dto.PlantDTO;
import com.crh.logon.business.ordercreation.dto.ShippingpointDTO;
import com.crh.logon.business.ordercreation.dto.SupplyPlantDTO;
import com.crh.logon.business.ordercreation.dto.TruckWagonTypeWrapper;
import com.crh.logon.business.services.interfaces.BestellungService;
import com.crh.logon.business.services.interfaces.RefDataGenericsService;
import com.crh.logon.business.services.localinterfaces.RailSchedulingServiceLocalInterface;
import com.crh.logon.business.services.utils.BimodalRailOrderWrapper;
import com.crh.logon.business.services.utils.ContainerMonitoringWrapper;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.business.services.utils.OrderTakingEditorDto;
import com.crh.logon.business.services.utils.OrderTakingEditorOrderItemDto;
import com.crh.logon.business.services.utils.OrderTakingEditorRailDeliveryDto;
import com.crh.logon.business.services.utils.OrderUtils;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.FireqMaterialMixdesignEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.RailwayStationEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.common.TruckRouteEntity;
import com.crh.logon.persistence.entities.refdata.ArchivingStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.CurrencyBsEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderCreationTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderSpiTxEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.BimodalRailOrderEntity;
import com.crh.logon.persistence.entities.views.ContainerMonitoringEntity;
import com.crh.logon.persistence.entities.views.OrderCreateEntity;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.web.utils.JsonUtils;
import com.crh.logon.web.utils.UserProvider;

import flexjson.JSONSerializer;

/**
 * @author AdNovum Informatik AG
 */
public class OrderTakingUtils {
	
	private static final boolean IS_ASCENDING = true;
	private static final boolean IS_ACTIVE = true;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(OrderTakingUtils.class);

	private ServiceLocator serviceLocator;
	private Locale locale;
	
	public OrderTakingUtils(ServiceLocator serviceLocator, Locale locale) {
		this.serviceLocator = serviceLocator;
		this.locale = locale;
	}

	/**
	 * Create a new Order Editor instance. This is only used on the Order Taking screen. No Contract or
	 * Sales Area is applied here, because that will be defined later by the material selected by the user.
	 * 
	 * @param srvLoc
	 * @param shiptoMap contains various Ship To and Sold To information that we need here
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	public static OrderTakingEditorDto getOrderEditorInstance(ServiceLocator srvLoc,
			DeliverySettingShiptoMap shiptoMap, Locale locale) throws Exception {

		RefDataGenericsService refData = srvLoc.getRefDataGenericsService();
		ShiptoEntity shipto = shiptoMap.getShipto();
		SapBpEntity soldtoSapBp = shiptoMap.getSoldTo();

		// --- Order Editor Data DTO ---
		TimeZone userTimeZone = UserProvider.getTimezone();
		OrderTakingEditorDto editor = new OrderTakingEditorDto(userTimeZone);
		List<OrderTakingEditorOrderItemDto> orderItems = editor.getOrderItems();
		OrderTakingEditorOrderItemDto firstItemDto = orderItems.get(0);

		editor.setMixDesignList(new ArrayList<FireqMaterialMixdesignEntity>());
		editor.setProdPlantList(new ArrayList());
		editor.setSupplyPlantList(new ArrayList());
		editor.setSpList(new ArrayList()); // Filled after SAP Material is selected
		editor.setLpList(new ArrayList()); // Filled after SP is selected
		editor.setBiRailItemList(new ArrayList<BimodalRailOrderWrapper>());
		editor.setPoolList(new ArrayList()); // Filled after Delivery Type is selected
		editor.setCommercialCarrierList(new ArrayList()); // Filled after Pool is selected
		editor.setExecutingCarrierList(new ArrayList()); // Filled after Commercial Carrier is selected
		editor.setBilltoList(new ArrayList()); // Filled after SAP Material is selected
		editor.setPayerList(new ArrayList()); // Filled after SAP Material is selected
		editor.setUomList(new ArrayList<UomTxEntity>());
		editor.setVehicleTypeWrapperList(new ArrayList<TruckWagonTypeWrapper>());
		editor.setTimeUomMap(new HashMap<String, String>());
		editor.setTimeToleranceUomMap(new HashMap<String, String>());
		List<OrderSpiTxEntity> serviceProcessIndicatorList = refData.findTxEntityList(OrderSpiTxEntity.class, IS_ACTIVE, locale, OrderSpiTxEntity.ATTR_TXT, IS_ASCENDING);
		editor.setServiceProcessIndicatorList(serviceProcessIndicatorList);
		
		// --- False by default ---
		editor.setAutoPoolAssignmentFlag(false);
		editor.setAutoCarrierAssignmentFlag(false);
		editor.setExactCustorderQtyFlag(false);
		editor.setExactQtyPerSdoFlag(false);
		editor.setServiceOnlyOrderFlag(false);
		
		// --- 1 order per editor ---
		OrderEntity order = new OrderEntity();
		order.setArchivedFlag(false);
		editor.setOrder(order);
		
		// --- Ship-to / Sold-to ---
		order.setShiptoSapBp(shipto.getShiptoSapBp());
		order.setSoldtoSapBp(soldtoSapBp);
		
		// --- Contact Person (Besteller, free text) ---
		if (shipto.getContactPerson() != null) {
			order.setContactPersonTxt(
					(shipto.getContactPerson().getFirstname() != null
							? shipto.getContactPerson().getFirstname() + " " : "")
					+ (shipto.getContactPerson().getName() != null
							? shipto.getContactPerson().getName() : ""));
		}

		// --- BB ---
		order.setOrderConfFaxFlag(BooleanUtils.isTrue(shipto.getOrderConfFaxFlag())
				&& !StringUtils.isEmpty(shipto.getOrderConfFaxNumber()));
		order.setOrderConfFaxNumber(shipto.getOrderConfFaxNumber());
		order.setOrderConfEmailFlag(BooleanUtils.isTrue(shipto.getOrderConfEmailFlag())
				&& !StringUtils.isEmpty(shipto.getOrderConfEmailAddresses()));
		order.setOrderConfEmailAdresses(shipto.getOrderConfEmailAddresses());
		order.setOrderConfSmsFlag(BooleanUtils.isTrue(shipto.getOrderConfSmsFlag())
				&& !StringUtils.isEmpty(shipto.getOrderConfSmsNumber()));
		order.setOrderConfSmsNumber(shipto.getOrderConfSmsNumber());

		order.setLoadConfFaxFlag(BooleanUtils.isTrue(shipto.getLoadConfFaxFlag())
				&& !StringUtils.isEmpty(shipto.getOrderConfFaxNumber()));
		order.setLoadConfEmailFlag(BooleanUtils.isTrue(shipto.getLoadConfEmailFlag())
				&& !StringUtils.isEmpty(shipto.getOrderConfEmailAddresses()));
		order.setLoadConfSmsFlag(BooleanUtils.isTrue(shipto.getLoadConfSmsFlag())
				&& !StringUtils.isEmpty(shipto.getOrderConfSmsNumber()));
		
		
		// --- LS ---
		order.setDelivnoteFaxFlag(BooleanUtils.isTrue(shipto.getFaxDelivnoteFlag())
				&& !StringUtils.isEmpty(shipto.getOrderConfFaxNumber()));
		order.setDelivnoteEmailFlag(BooleanUtils.isTrue(shipto.getEmailDelivnoteFlag())
				&& !StringUtils.isEmpty(shipto.getOrderConfEmailAddresses()));
		order.setDelivnoteSinglePaperFlag(BooleanUtils.isTrue(shipto.getPaperSingleDelnoteFlagCem()));
		editor.setPrintCollDeliveryNoteFlag(BooleanUtils.isTrue(shipto.getPaperCollDelnoteFlagCem()));

		// --- Order Comments ---
		order.setNoteOrderMgmt(shipto.getNoteOrderMgmt());
		order.setNoteDelivery(shipto.getNoteDelivery());
		order.setNoteDispatching(shipto.getNoteDispatching());
		order.setNoteTruckDriver(shipto.getNoteTruckDriver());
		order.setNoteRailwayDisp(shipto.getNoteRailwayDisp());

		// --- Misc ---
		Long orderCreationType;

		orderCreationType = Constant.ORDER_CREATION_TYPE_CALL_CENTER;

		order.setOrderCreationType(refData.findBsEntity(OrderCreationTypeBsEntity.class, orderCreationType));
		
		order.setArchivingStatus(refData.findBsEntity(ArchivingStatusBsEntity.class, Constant.ARCHIVING_STATUS_NEW));

		// --- Distribution Destination ---
		// (It is only known when Sales Area is defined ==> use OUT as default.)
//		DistributionDestBsEntity distributionDest = refData.findBsEntity(DistributionDestBsEntity.class, 
//				Constant.DISTRIBUTION_DEST_OUTBOUND);
//		order.setDistributionDest(distributionDest);

		// --- 1 helper object ---
		editor.setOrderHelper(new OrderCreateEntity());
		
		// --- Default material/quantity (ID = -1 means no product is selected and the editor is disabled) ---
		// (Quantity Str: needed for BigDecimal field)
		firstItemDto.setSapMatId(Long.valueOf(-1));
		firstItemDto.setQuantityStr("1");

		firstItemDto.setMixDesignId(Long.valueOf(-1));
		
		// List of all valid products for this Ship To (we do not know the contract and sales area either yet)
		Collection<DeliverySettingDTO> productList = shiptoMap.getMaterialDeliverySettingList();

		List<DeliverySettingDTO> delSettingsWithAutoPayerFlagsAndPresetPayer = new ArrayList<DeliverySettingDTO>();
		for (DeliverySettingDTO delSetting: productList ) {
			if (!delSetting.isAutoPayerSettingValid()) {
				delSettingsWithAutoPayerFlagsAndPresetPayer.add(delSetting);
			}
		}
		productList.removeAll(delSettingsWithAutoPayerFlagsAndPresetPayer);

		final JSONSerializer jsonSerializer = JsonUtils.getJsonSerializer();
		jsonSerializer.exclude(DeliverySettingDTO.excludeEntities);

		String jsonProductList = jsonSerializer.serialize(productList);
		firstItemDto.setJsonProductList(jsonProductList);
		
		// List of all valid service products for this Ship To (we do not know the contract and sales area either yet)
		Collection<DeliverySettingDTO> serviceOnlyProductList = shiptoMap.getServiceDeliverySettingList();
		
		// filter out service only products which are not based on a contract (first service only material has to be have a contract in the order)
		List<DeliverySettingDTO> filteredServiceOnlyProductList = new ArrayList<DeliverySettingDTO>();
		DeliveryTypeBsEntity deliveryTypeService = srvLoc.getRefDataGenericsService().findBsEntity(DeliveryTypeBsEntity.class, Constant.DELIVERY_TYPE_SERVICE);
		for (DeliverySettingDTO actDeliverySettingDTO : serviceOnlyProductList) {
			final int deliverySettingType = actDeliverySettingDTO.getDeliverySettingType();
			
			if (!(deliverySettingType != DeliverySettingDTO.DELIVERY_SETTING_BASED_ON_CONTRACT
					&& deliverySettingType != DeliverySettingDTO.DELIVERY_SETTING_BASED_ON_CONTRACT_COMBINED_RESTRICTION)) {
				DeliveryTypeBsEntity deliveryType = actDeliverySettingDTO.getDeliveryType();
				
				if (deliveryType == null || !LogonUtil.isServiceOnlyOrder(deliveryType.getDeliveryTypeCd())) {
					actDeliverySettingDTO.setDeliveryType(deliveryTypeService);
				}
				
				filteredServiceOnlyProductList.add(actDeliverySettingDTO);
			}
		}
		
		String jsonServiceOnlyProductList = jsonSerializer.serialize(filteredServiceOnlyProductList);
		firstItemDto.setJsonServiceOnlyProductList(jsonServiceOnlyProductList);
		
		// --- 1..n items per order ---
		firstItemDto.setItemNoteOrderMgmt("");
		firstItemDto.setItemNoteDelivery("");
		firstItemDto.setItemNoteDispatching("");
		firstItemDto.setItemNoteTruckDriver("");
		firstItemDto.setItemNoteRailway("");
		firstItemDto.getOrderItem().setOrder(order);
		
		return editor;
	}


	public OrderTakingEditorDto getCollectedEditorData(
			OrderTakingEditorParamsDto paramsDto,
			HttpServletRequest request,
			HttpSession session 
		) throws Exception {
		// [[
		OrderTakingEditorDto result = null;
		Object[][] params = {{"paramsDto", paramsDto},
							{"request", request},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getCollectedEditorData", params);
		}
		// ]]
		try {
			
			int editorIdx = paramsDto.getEditorIdx();
			int orderItemIdx = paramsDto.getOrderItemIdx();
			
			RailSchedulingServiceLocalInterface railSchedulingService = serviceLocator.getLocalRailSchedulingService();
			
			BestellungForm orderForm = getBestellungForm(session);
			List<OrderTakingEditorDto> editorList = orderForm.getEditorList();
			OrderTakingEditorDto editor = editorList.get(editorIdx);
			
			editor.setValidationMessages(new ArrayList());
			
			List<OrderTakingEditorOrderItemDto> orderItemDtos = editor.getOrderItems();
			OrderTakingEditorOrderItemDto orderItemDto = orderItemDtos.get(orderItemIdx);
			
			Long sapMatId = null;
			String supplyPlantCd = paramsDto.getSupplyPlantCd(); 
			Long spId = paramsDto.getSpId();
			Long poolId = paramsDto.getPoolId();
			Long commercialCarrierId = paramsDto.getCommercialCarrierId();
			Long delTypeCd = paramsDto.getDelTypeCd();
			String prodPlantCd = paramsDto.getProdPlantCd();
			String uomCd = paramsDto.getUomCd();
			Date delFromDat = paramsDto.getDelFromDat(); 
			Date delToDat = paramsDto.getDelToDat();

	
			DeliverySettingDTO setting = null;
			
			final SapContractItemEntity sapContractItem;
			
			if (paramsDto.getCompositeIdentifier() != null) {
				orderItemDto.setCompositeIdentifier(paramsDto.getCompositeIdentifier());
				/**
				 * Composite identifier comes only when something in the material(first combo) is selected
				 */

				final DeliverySettingShiptoMap shiptoMap;
				
				/**
				 * This here is very strange, and most likely could be simplified. But the cases to be considered are  different
				 * bimodal calls are also coming here, in that case the form's shiptomap should not be used, as that belongs to the
				 * selected shipto and not to the FVL shipto. Currenly bimodal rail goes to the else case because the sales area map is
				 * initialized in the action, but this is bad/not understandable like that. 
				 */
				if (editor.getSalesAreaMap() == null ) {
					shiptoMap = orderForm.getShiptoMap();
					/**
					 * For the first time, preset everything according to the default and/or required and restricted settings.
					 */
					setting = shiptoMap.getDeliverySettingByCompositeIdentifier(paramsDto.getCompositeIdentifier());
					if (orderItemIdx == 0) {
						final DeliverySettingSalesAreaMap sareaMap = shiptoMap.getSalesAreaMapForSareaId(setting.getSarea().getSareaId());
						editor.setShiptoSarea(sareaMap.getShiptoSarea());
						editor.setSalesAreaMap(sareaMap);
					}
				}
				else {
					setting = editor.getSalesAreaMap().getDeliverySettingByCompositeIdentifier(paramsDto.getCompositeIdentifier());
					shiptoMap = editor.getSalesAreaMap().getShiptoSettings();
					if ( setting == null ) {
						/**
						 * Could not locate in the sales area, try on shipto level, probably switched sales area as well by selecting 
						 * another option in the first order item material dropdown, if so reinitialize the editor's settings too.
						 */
						setting = shiptoMap.getDeliverySettingByCompositeIdentifier(paramsDto.getCompositeIdentifier());
						final DeliverySettingSalesAreaMap sareaMap = shiptoMap.getSalesAreaMapForSareaId(setting.getSarea().getSareaId());
						editor.setShiptoSarea(sareaMap.getShiptoSarea());
						editor.setSalesAreaMap(sareaMap);
					}
				}
				
				final ShiptoSareaEntity shiptoSarea = editor.getShiptoSarea();
				
				/**
				 * Originally this wrapper is in the material combo.
				 */
				sapMatId = setting.getSapMaterial().getSapMaterialId();
				
				// Setting sapMaterial to orderItem in order to display the default unloading time on the UI
				orderItemDto.getOrderItem().setSapMaterial(setting.getSapMaterial());
								
				if (orderItemIdx == 0) {
					editor.setShiptoSarea(shiptoSarea);
					
					editor.setQuantityPerSdoStr(setting.getPerSdorderQuantity() != null ? setting.getPerSdorderQuantity().toString() : null);
					editor.setQuantityPerSdoUomCd(setting.getPerSdorderUom() != null ? setting.getPerSdorderUom().getUomCd() : Constant.UOM_TONS);
					editor.setQuantityPerSdoText(getQuantityPerSdoLabel(editor.getQuantityPerSdoUomCd()));
					editor.setExactCustorderQtyFlag(setting.getExactCustorderQtyFlag() != null ? setting.getExactCustorderQtyFlag() : Boolean.FALSE);
					editor.setExactQtyPerSdoFlag(setting.getExactSdorderQtyFlag() != null ? setting.getExactSdorderQtyFlag() : Boolean.FALSE);
					editor.setDeliveryFqyEveryTimeStr(setting.getDeliveryFreqEveryTime() != null ? setting.getDeliveryFreqEveryTime().toString() : null);
					editor.setDeliveryFqyEveryTimeUomCd(setting.getDeliveryFreqEveryTimeUomCd());
					editor.setDeliveryFqyQtyPerHreStr(setting.getDeliveryFreqQtyperhre() != null ? setting.getDeliveryFreqQtyperhre().toPlainString() : null );
					editor.setDeliveryFqyQtyPerHreUomCd( setting.getDeliveryFreqQtyperhreUomCd() );
					editor.setDeliveryTimeToleranceStr(setting.getDeliveryTimeTolerance() != null ? setting.getDeliveryTimeTolerance().toString() : null);
					editor.setDeliveryTimeToleranceUomCd(setting.getDeliveryTimeToleranceUomCd());
					
				    editor.setFreeDeliveryFlag(setting.getFreeDeliveryFlag() != null ? setting.getFreeDeliveryFlag() : Boolean.FALSE);
				    editor.setReleaseSapFlag(setting.getReleaseSapFlag() != null ? setting.getReleaseSapFlag() : Boolean.TRUE);
				    editor.setSapSuppressMinChargeFlag(setting.getSapSuppressMinChargeFlag() != null ? setting.getSapSuppressMinChargeFlag() : Boolean.FALSE);
				    editor.setOpenCustorderFlag(setting.getOpenCustorderFlag() != null ? setting.getOpenCustorderFlag() : Boolean.FALSE);
				    editor.setOnHoldOrderAndLockedFlag(setting.getOnHoldOrderAndLockedFlag() != null ? setting.getOnHoldOrderAndLockedFlag() : Boolean.FALSE);
				    editor.setAutoReleaseFlag(setting.getAutoReleaseFlag() != null ? setting.getAutoReleaseFlag() : Boolean.FALSE);
				    
					OrderEntity order = editor.getOrder();
					order.setSarea(shiptoSarea.getSarea());
					
					/*
					 * Reset the contract reference. This is needed so we can still check later if all
					 * materials belong to the same contract in the same editor, even if the user selects
					 * a different material before re-attempting to validate the order.
					 */
					order.setSapContract(null);
				}
				/**
				 * If the setting is located, set all the fields of the order item which depend on the original delivery setting selection
				 */
				sapContractItem = setting.getSapContractItem();
				orderItemDto.setSapContractItemId(sapContractItem != null ? sapContractItem.getSapContractItemId() : null);
				orderItemDto.setSapContractId(sapContractItem != null ? sapContractItem.getSapContract().getSapContractId() : null);
				orderItemDto.setSapMatId(setting.getSapMaterial().getSapMaterialId());
				orderItemDto.setSareaId(setting.getSarea().getSareaId());
				orderItemDto.setShiptoSareaRestrId(setting.getShiptoSareaRestr() != null ? setting.getShiptoSareaRestr().getShiptoSareaRestrId() : null);
				orderItemDto.setMixDesignId(setting.getMixDesignId());
				orderItemDto.setSapMatPrice(getSapMatPrice(sapContractItem));
				orderItemDto.setSapMatNo(setting.getSapMaterial().getSapMaterialNo());

				calculateOpenQuantity(editor, orderItemDto);
				
			}
			else {

				sapMatId = orderItemDto.getSapMatId();
				SapMaterialEntity sapMat = serviceLocator.getBestellungService().getSapMaterialById(sapMatId);
				
				// TODO (emoke Aug 3, 2012):TODO replace ordertakingeditorparamsdto with basic dto!
				DeliverySettingBasicDTO settingB = new DeliverySettingBasicDTO();
				//this is not good like that, something will be needed in order that we don't need to set this manually!
				settingB.setSarea(editor.getShiptoSarea().getSarea());
				settingB.setSapMaterial(sapMat);
				settingB.setShippingpointId((spId == null ? editor.getSpId() : LogonUtil.emptyValue(spId) ? null : spId));
				settingB.setPoolId((poolId == null ? editor.getPoolId() : !LogonUtil.emptyValue(poolId) ? poolId : null));
				settingB.setCommercialCarrierId((commercialCarrierId == null ? editor.getCommercialCarrierId() : !LogonUtil.emptyValue(commercialCarrierId) ? commercialCarrierId : null));
				settingB.setDeliveryTypeCd((delTypeCd == null ? editor.getDelTypeCd() : !LogonUtil.emptyValue(delTypeCd) ? delTypeCd : null));
				settingB.setUomCd((uomCd == null ? orderItemDto.getUomCd() : !LogonUtil.emptyValue(uomCd) ? uomCd : null));
				settingB.setProductionPlantCd((prodPlantCd == null ? orderItemDto.getProdPlantCd() : !LogonUtil.emptyValue(prodPlantCd) ? prodPlantCd : null));
				settingB.setSupplyPlantCd((supplyPlantCd == null ? editor.getSupplyPlantCd() : !LogonUtil.emptyValue(supplyPlantCd) ? supplyPlantCd : null ));
				settingB.setSapSuppressMinChargeFlag(paramsDto.getSapSuppressMinChargeFlag() == null ? editor.getSapSuppressMinChargeFlag() : paramsDto.getSapSuppressMinChargeFlag());
				settingB.setOpenCustorderFlag(paramsDto.getOpenCustorderFlag() == null ? editor.getOpenCustorderFlag() : paramsDto.getOpenCustorderFlag());
				settingB.setOnHoldOrderAndLockedFlag(paramsDto.getOnHoldOrderAndLockedFlag() == null ? editor.getOnHoldOrderAndLockedFlag() : paramsDto.getOnHoldOrderAndLockedFlag());
				settingB.setFreeDeliveryFlag(paramsDto.getFreeDeliveryFlag() == null ? editor.getFreeDeliveryFlag() : paramsDto.getFreeDeliveryFlag());
				settingB.setReleaseSapFlag(paramsDto.getReleaseSapFlag() == null ? editor.getReleaseSapFlag() : paramsDto.getReleaseSapFlag());
				settingB.setAutoReleaseFlag(paramsDto.getAutoReleaseFlag() == null ? editor.getAutoReleaseFlag() : paramsDto.getAutoReleaseFlag());
				settingB.setMixDesignId(paramsDto.getMixDesignId() == null ? editor.getMixDesignId() : (!LogonUtil.emptyValue(paramsDto.getMixDesignId())? paramsDto.getMixDesignId(): null));

				/**
				 * This here makes not much sense, currently these flags are not used in validation, they have no effect on anything. But in the meantime on the UI there is a lot of
				 * logic here, like in case of cash sales the free delivery is forbidden, etc. not to allow that combination should not be implemented in javascript,
				 * but rather the validation of the setting should reset that flag instead. In that case we'll constructe the settting to be found above from the UI, send into
				 * the validate, and if the validate changed the value, then the editor's value will guaranteed to be valid.
				 */
			    editor.setFreeDeliveryFlag(settingB.getFreeDeliveryFlag() != null ? settingB.getFreeDeliveryFlag() : Boolean.FALSE);
			    editor.setReleaseSapFlag(settingB.getReleaseSapFlag() != null ? settingB.getReleaseSapFlag() : Boolean.TRUE);
			    editor.setSapSuppressMinChargeFlag(settingB.getSapSuppressMinChargeFlag() != null ? settingB.getSapSuppressMinChargeFlag() : Boolean.FALSE);
			    editor.setOpenCustorderFlag(settingB.getOpenCustorderFlag() != null ? settingB.getOpenCustorderFlag() : Boolean.FALSE);
			    editor.setOnHoldOrderAndLockedFlag(settingB.getOnHoldOrderAndLockedFlag() != null ? settingB.getOnHoldOrderAndLockedFlag() : Boolean.FALSE);
			    editor.setAutoReleaseFlag(settingB.getAutoReleaseFlag() != null ? settingB.getAutoReleaseFlag() : Boolean.FALSE);
			    
			    orderItemDto.setMixDesignId(settingB.getMixDesignId());
			    DeliverySettingDTO originalDeliverySetting = editor.getSalesAreaMap().getDeliverySettingByCompositeIdentifier(orderItemDto.getCompositeIdentifier());
			    OrderEntity order = editor.getOrder();

			    boolean isCashSales = serviceLocator.getOrderService().isCashSales(order.getSapContract(), order.getSarea());

				setting = OrderUtils.getDeliverySettingFromBasicDTO(settingB, editor.getSalesAreaMap(), originalDeliverySetting, isCashSales, serviceLocator.getTruckAdminService(), serviceLocator.getBestellungService(), serviceLocator.getDeliverySettingService());

				sapContractItem = setting.getSapContractItem();

			}
			orderItemDto.setDeliverySetting(setting);
			setting.setDeliveryOptionConfiguration(DeliverySettingFilterLogic.getDeliveryOptionConfiguration(setting, editor.getSalesAreaMap()));
			
			
			final DeliveryOptionConfigurationDTO deliveryOptionConfiguration =
				DeliveryOptionConfigurationWrapper.getDeliveryOptionConfiguration(editor.getSalesAreaMap(),
						setting);
			
			// Try to set the selected shiptoSilo to orderItemDto.getOrderItem()
			ShiptoSiloEntity selectedShiptoSilo = null;
			if(paramsDto.getShiptoSiloId() != null) {
				List<ShiptoSiloEntity> shiptoSilos = orderForm.getShiptoActiveSilos();
				
				Long shiptoSiloId = paramsDto.getShiptoSiloId();
				for (ShiptoSiloEntity currentShiptoSilo : shiptoSilos) {
					Long currentShiptoSiloId = currentShiptoSilo.getShiptoSiloId();
					if (shiptoSiloId.equals(currentShiptoSiloId)) {
						selectedShiptoSilo = currentShiptoSilo;
						break;
					}
				}
			}
			orderItemDto.getOrderItem().setShiptoSilo(selectedShiptoSilo);
			
			if (orderItemIdx == 0) {
				//these setters would not be necessary if the deliverysetting itself together with its optionconfiguration would be sent out. in the javascript it's possible to reach those lists by deliverysetting.deliveryOptionConfiguration.poolList/carrierList/etc,
				editor.setMixDesignList(deliveryOptionConfiguration.getMaterialMixDesignList());
				editor.setSpList(deliveryOptionConfiguration.getShippingpoinList());
				editor.setLpList(deliveryOptionConfiguration.getLoadingpointList());
				editor.setPoolList(deliveryOptionConfiguration.getPoolList());
				editor.setCommercialCarrierList(deliveryOptionConfiguration.getCommercialCarrierList());
				editor.setExecutingCarrierList(deliveryOptionConfiguration.getExecutingCarrierList());
				editor.setSupplyPlantList(deliveryOptionConfiguration.getSupplyPlantList());
				editor.setBilltoList(deliveryOptionConfiguration.getBilltoList());
				editor.setPayerList(deliveryOptionConfiguration.getPayerList());
				
				editor.setSpId(setting.getShippingpointId());
				editor.setPoolId(setting.getPoolId());
				editor.setCommercialCarrierId(setting.getCommercialCarrierId());
				editor.setExecutingCarrierId(setting.getExecutingCarrierId());
				editor.setSupplyPlantCd(setting.getSupplyPlantCd());
				editor.setDelTypeCd(setting.getDeliveryTypeCd());
				
				editor.setVehicleTypeWrapperList(deliveryOptionConfiguration.getVehicleTypeWrapperList());
				editor.setTimeUomMap(deliveryOptionConfiguration.getDeliveryFreqEveryTimeUomMap());
				editor.setTimeToleranceUomMap(deliveryOptionConfiguration.getDeliveryTimeToleranceUomMap());
			}
			
			editor.setProdPlantList(deliveryOptionConfiguration.getProdPlantList());
			orderItemDto.setProdPlantCd(setting.getProductionPlantCd());
			
			if (LogonUtil.isPlanningMaterial(setting.getSapMaterial())) {
				spId = (spId == null ? editor.getSpId() : LogonUtil.emptyValue(spId) ? null : spId);
				supplyPlantCd = (supplyPlantCd == null ? editor.getSupplyPlantCd()
						: !LogonUtil.emptyValue(supplyPlantCd) ? supplyPlantCd : null );
				
				TimeZone timeZone = UserProvider.getTimezone();
				Map<String, Object> plantDetails = serviceLocator.getBestellungService().
						calcDeliveryPlantDetails(setting.getSapContractItem(), editor.getShiptoSarea(), editor.getSalesAreaMap(), supplyPlantCd, spId, timeZone);
				List<SupplyPlantDTO> supplyPlantList = (List<SupplyPlantDTO>)plantDetails.get("supplyPlantList");
				List<ShippingpointDTO> shippingpointList = (List<ShippingpointDTO>)plantDetails.get("shippingpointList");
				List<LoadingPointDTO> loadingpointList = (List<LoadingPointDTO>)plantDetails.get("loadingpointList");
				List<PlantDTO> prodPlantList = (List<PlantDTO>)plantDetails.get("prodPlantList");
				String prodPlantCode = (String)plantDetails.get("prodPlantCode");
				
				editor.setSupplyPlantList(supplyPlantList);
				editor.setSpList(shippingpointList);
				editor.setLpList(loadingpointList);
				editor.setProdPlantList(prodPlantList);
				orderItemDto.setProdPlantCd(prodPlantCode);
				
				if (supplyPlantCd != null) {
					editor.setSupplyPlantCd(supplyPlantCd);
				}
				if (spId != null) {
					editor.setSpId(spId);
				}
			}
			
			editor.setUomList(deliveryOptionConfiguration.getUomList());
			
			if (setting.getServiceProcessIndicatorId() != null){
			    editor.setServiceProcessIndicatorId(setting.getServiceProcessIndicatorId());
			}
			else{
			    editor.setServiceProcessIndicatorId(Constant.INVALID_OBJECT_ID_STR);
			}
			
			if (orderItemIdx == 0) {
				// --- Reload bimodal rail order list for truck part when the delivery-to date (and only that one) is changed ---
				if (spId == null && delToDat != null && editor.getSpId() != null       // spId == null to prevent multiple refresh
						&& LogonUtil.isBimodalTruckPartOrder(editor.getDelTypeCd())) { 
					
					composeBimodalRailOrderListContent(editor, paramsDto, orderForm);
					
				}
				
				// --- Changed SP value: refresh LP (or Bimodal Rail Order Item list) and Carrier list (remove TC info if no SP is selected) ---
				if (spId != null) {
					if (LogonUtil.emptyValue(spId) /*|| !result.getPossibleShippingPointIdSet().contains(spId)*/) { // Empty selection
						editor.setBiRailItemList(new ArrayList<BimodalRailOrderWrapper>());
						TimeZone userTimeZone = UserProvider.getTimezone();
						editor.setRailData(new OrderTakingEditorRailDeliveryDto(userTimeZone));
					}
					else {
						if (LogonUtil.isBimodalTruckPartOrder(editor.getDelTypeCd())) {
							
							composeBimodalRailOrderListContent(editor, paramsDto, orderForm);
							
						} 
					}
				}
				
				if (delTypeCd != null || (spId != null && !LogonUtil.emptyValue(spId)) || delToDat != null) {
					
					ShiptoEntity shipto = editor.getShiptoSarea().getShipto();
					
					// --- Railway transport duration and distance ---
					if (LogonUtil.isRailOrder(editor.getDelTypeCd()) && !LogonUtil.emptyValue(editor.getSpId())) {
						
						RailwayStationEntity shiptoStation = shipto.getShiptoSapBp().getRailwayStation();
						Long shippingpointRailwaystationId = null;
						List<ShippingpointDTO> shippingpointList = editor.getSpList();
						Long shippingpointId = editor.getSpId();
						ShippingpointDTO sp = null;
						for (ShippingpointDTO currentShippingpoint : shippingpointList) {
							Long currentShippingpointId = currentShippingpoint.getShippingpointId();
							if (shippingpointId.equals(currentShippingpointId)) {
								sp = currentShippingpoint;
								break;
							}
						}
						if (sp != null) {
							shippingpointRailwaystationId = sp.getRailwayStationId();
						}
						
						if (shippingpointRailwaystationId != null) {
							boolean isRailDirect = LogonUtil.isRailDirectOrder(editor.getDelTypeCd()); 
							ShiptoEntity orderShipTo = orderForm.getShipTo();
							TimeZone userTimeZone = UserProvider.getTimezone();
							OrderTakingEditorRailDeliveryDto railData = railSchedulingService.calculateRailDeliveryData(
									shippingpointRailwaystationId, 
									shiptoStation.getRailwayStationId(), 
									isRailDirect ? DateTimeUtils.getMorningOfDay(delFromDat) : delFromDat, 
									isRailDirect ? DateTimeUtils.getMidnightOfDay(delToDat) : delToDat,
									orderShipTo,
									userTimeZone,
									editorIdx > 0);
							
							editor.setRailData(railData);
						}
						else {
							TimeZone userTimeZone = UserProvider.getTimezone();
							editor.setRailData(new OrderTakingEditorRailDeliveryDto(userTimeZone));
							editor.getRailData().setDurationMinutes(-2L);
							editor.getRailData().setDistanceMeters(-2L);
						}
					}
					
					TruckRouteEntity route = getTruckRoute(editor.getSpId(), shipto.getShiptoId(), editor.getDelTypeCd());
					if (route != null) {
							
						if (LogonUtil.isTruckOrder(delTypeCd) || LogonUtil.isShipOrder(delTypeCd)
								|| LogonUtil.isBlockTrainOrder(delTypeCd)) {
							editor.setDurationCorrectionFactor(route.getTimeCorrectionFactor().doubleValue());
						}
						else {
							editor.setDurationCorrectionFactor(1d);
						}

						if (route.getDistanceCorrectionFactor() !=null) {
							editor.setDistanceCorrectionFactor(route.getDistanceCorrectionFactor().doubleValue());
						} else {
							editor.setDistanceCorrectionFactor(1d);
						}
					} else {
						editor.setDurationCorrectionFactor(1d);
						editor.setDistanceCorrectionFactor(1d);
					}
				}
			}
			editor.initDeliveryNoteFlags(setting.getSapMaterial());
			
			boolean payerIgnored = sapContractItem != null && 
					OrderUtils.isPayerIgnored(sapContractItem.getSapContract());
			editor.setPayerIgnored(payerIgnored);
			
			return result = editor;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private void composeBimodalRailOrderListContent(OrderTakingEditorDto editor,
			OrderTakingEditorParamsDto paramsDto, BestellungForm orderForm) throws Exception{
		
		Long selectedSPId = editor.getSpId();
		if (selectedSPId == null){
			return;
		}
		
		BestellungService bestService = serviceLocator.getBestellungService();
		
		List<OrderTakingEditorOrderItemDto> orderItemDtos = editor.getOrderItems();
		OrderTakingEditorOrderItemDto firstOrderItemDto = orderItemDtos.get(0);
		Long sapMatId = firstOrderItemDto.getSapMatId();
		String prodPlantCd = firstOrderItemDto.getProdPlantCd();

		ShippingpointDTO sp = null;
		for (ShippingpointDTO entity : editor.getSpList()) {
			Long shippingpointId = entity.getShippingpointId();
			if (shippingpointId.equals(selectedSPId)){
				sp = entity;
				break;
			}
		}
		if (sp == null){
			LOG.warn("The Shippingpoint id: " + selectedSPId + " was not present in the list of editor.getSPList.");
			return;
		}
		
		Long shippingpointId = sp.getShippingpointId();
		Boolean fvlMixShiptoPerWagonFlag = bestService.getShippingpointFvlFlag(sp.getShippingpointId());

		ShiptoEntity shipTo = orderForm.getShipTo();
		Long truckPartShiptoSapBpId = shipTo.getShiptoSapBp().getSapBpId();
		Date delToDat = paramsDto.getDelToDat();
		Long sareaId = firstOrderItemDto.getSareaId();

		List<BimodalRailOrderEntity> biRailOrderList = bestService.fetchRailOrderListForBimodalTruckPart(
				sapMatId,
				prodPlantCd,
				shippingpointId,
				(fvlMixShiptoPerWagonFlag != null ? fvlMixShiptoPerWagonFlag : false),
				truckPartShiptoSapBpId,
				delToDat,
				locale);
		List<ShiptoSareaRestrEntity> restrList = bestService.fetchShiptoRestrListForBimodalRailPart(
				selectedSPId,
				sapMatId, 
				prodPlantCd, 
				sareaId);
		
		// --- Values to display in case of null ---
		for (BimodalRailOrderEntity order : biRailOrderList) {
			if (order.getWagonNo() == null) {
				order.setWagonNo("-");
			}
			if (order.getWagonType() == null) {
				order.setWagonType("-");
			}
		}
		
		List<BimodalRailOrderWrapper> biRailOrderListWrapped = wrapBimodalRailOrders(
				serviceLocator,
				truckPartShiptoSapBpId,
				biRailOrderList,
				orderForm.getLocale());
		
		Map<Long, ShiptoSareaRestrEntity> shiptoSalesAreaRestrictionMap = new HashMap<Long, ShiptoSareaRestrEntity>();
		for (ShiptoSareaRestrEntity shiptoSalesAreaRestriction : restrList) {
			Long shiptoSalesAreaRestrictionId = shiptoSalesAreaRestriction.getShiptoSareaRestrId();
			shiptoSalesAreaRestrictionMap.put(shiptoSalesAreaRestrictionId, shiptoSalesAreaRestriction);
		
			SapBpEntity shiptoSapBp = shiptoSalesAreaRestriction.getShiptoSarea().getShipto().getShiptoSapBp();
			sareaId = shiptoSalesAreaRestriction.getShiptoSarea().getSarea().getSareaId();
			
			List<SapBpEntity> soldtoBpList = bestService.fetchSoldtoByShiptoSarea(shiptoSapBp.getSapBpId(), sareaId, Boolean.FALSE);
			
			for (SapBpEntity soldtoSapBp : soldtoBpList) {
				BimodalRailOrderEntity appendShipto = new BimodalRailOrderEntity();
				BimodalRailOrderWrapper appendShiptoWrapped =  new BimodalRailOrderWrapper(appendShipto);
				appendShiptoWrapped.setBimodalSoldtoSapId(soldtoSapBp.getSapId());
				appendShiptoWrapped.setBimodalSoldtoName(soldtoSapBp.getName1());
				appendShiptoWrapped.setBimodalShiptoName(shiptoSapBp.getName1());
				appendShiptoWrapped.setBimodalShiptoSareaRestrId(shiptoSalesAreaRestriction.getShiptoSareaRestrId());
				biRailOrderListWrapped.add(appendShiptoWrapped);
			}
		}
		
		editor.setRestrictionMap(shiptoSalesAreaRestrictionMap);
			
		editor.setBiRailItemList(biRailOrderListWrapped);
		
	}

	/**
	 * Creates {@link BimodalRailOrderWrapper} list from the parameter bimodal rail order list. The wrapper is constructed
	 * to know details about dependent bimodal road truck orders, and show information regarding the further usability of the
	 * bimodal rail order.
	 * 
	 * The wrappers are show both on call center order taking loadingpoint dropdown and on Order monitoring/Bimodal monitoring.
	 * 
	 * @param truckPartShiptoSapBpId - if specified the wrapper will show if the rail order used for multiple shiptos
	 * @param biRailOrderList
	 * @return
	 * @throws Exception
	 */
	public static  List<BimodalRailOrderWrapper> wrapBimodalRailOrders(
			ServiceLocator serviceLocator,
			final Long truckPartShiptoSapBpId,
			List<BimodalRailOrderEntity> biRailOrderList,
			Locale locale) {
		BestellungService bestService = serviceLocator.getBestellungService();
		
		List<Long> railOrderItemIdList = new ArrayList<Long>();
		for (BimodalRailOrderEntity bimodalRailOrder : biRailOrderList) {
			Long orderItemId = bimodalRailOrder.getOrderItemId();
			railOrderItemIdList.add(orderItemId);
		}

		List<BimodalRailOrderEntity> depItemList = bestService.fetchDependentTruckOrderList(railOrderItemIdList, Locale.ENGLISH.getLanguage());

		Map<Long, TimeZone> shiptoTZCache = new HashMap<Long, TimeZone>();

		// --- Calculate the dependent truck order related info for each rail item ---
		List<BimodalRailOrderWrapper> biRailOrderListWrapped = new ArrayList<BimodalRailOrderWrapper>();
		
		for (BimodalRailOrderEntity railItem : biRailOrderList) {
			
			BimodalRailOrderWrapper railItemWrapped = new BimodalRailOrderWrapper(railItem);
			int depCount = 0;
			boolean mixedDelivery = false;
			Double railQty = 0d;
			Double sumDepTruckQty = 0d;
			String dedicatedShiptoName = null;
			Map dedicatedShiptoSapBpIdMap = new HashMap();
			Long dedicatedShiptoSapBpId = truckPartShiptoSapBpId;
			Boolean depFvlMixShiptoPerWagonFlag = Boolean.FALSE;
			
			// --- Rail Order Qty ---
			if (railItem.getLoadNetMeasure() != null) { // case 3
				railQty = railItem.getLoadNetMeasure();
			}
			else if (railItem.getWagonId() != null) { // case 2
				if (railItem.getOrderedMeasure() != null && railItem.getWagonMaxNetWeightT() != null) {
					if (railItem.getOrderedMeasure() <= railItem.getWagonMaxNetWeightT()) {
						railQty = railItem.getOrderedMeasure();
					}
					else {
						railQty = railItem.getWagonMaxNetWeightT();
					}
				}
				else if (railItem.getWagonMaxNetWeightT() != null) {
					railQty = railItem.getWagonMaxNetWeightT();
				}
				else if (railItem.getOrderedMeasure() != null) {
					railQty = railItem.getOrderedMeasure();
				}
			}
			else { // case 1
				railQty = railItem.getOrderedMeasure();
			}
			

			/**
			 * Warning! Naming is misleading, this iteration here is actually NOT on bimodal rail orders, but on their dependent bimodal road
			 * orders! TODO: rename the view not to contain Bimodail rail, but bimodal simply!
			 */
			for (BimodalRailOrderEntity depItem : depItemList) {
				
				if (depItem.getBimodalRailOrderItemId().equals(railItem.getOrderItemId())) {
					
					//in case no parameter was defined mixed delivery should still be indicated if it is a mixed delivery
					if ( dedicatedShiptoSapBpId == null ){
						dedicatedShiptoSapBpId = depItem.getShiptoSapBpId();
					}
					
					//these are always the same, as they are coming from the same FVL shippintpoint (the material chain shippingpoint of the bimodal rail order) this should actually come
					//from the bimodal rail order, but for that we would need to join to the view the shipto and the shippingpoint table through material chain shippingpoint one more time.
					depFvlMixShiptoPerWagonFlag = depItem.getFvlMixShiptoPerWagonFlag();
					
					//if the shippingpoint allows multiple shiptos it is not a problem that it differs!
					if ( depFvlMixShiptoPerWagonFlag.equals(Boolean.TRUE) ) {							
						dedicatedShiptoName = "-";
					}
					else {					
						dedicatedShiptoName = depItem.getName1();
					}
					if ( !dedicatedShiptoSapBpIdMap.containsKey(depItem.getShiptoSapBpId() )) {						
						dedicatedShiptoSapBpIdMap.put(depItem.getShiptoSapBpId(), depItem.getName1());
					}
					
					Double depTruckQty = 0d;
					
					// --- Dependent truck order qty ---
					if (depItem.getLoadNetMeasure() != null) { // case 3
						depTruckQty = depItem.getLoadNetMeasure();
					}
					else if (depItem.getTruckId() != null) { // case 2
						if (depItem.getOrderedMeasure() != null && depItem.getTruckMaxNetWeightT() != null) {
							if (depItem.getOrderedMeasure() <= depItem.getTruckMaxNetWeightT()) {
								depTruckQty = depItem.getOrderedMeasure();
							}
							else {
								depTruckQty = depItem.getTruckMaxNetWeightT();
							}
						}
						else if (depItem.getTruckMaxNetWeightT() != null) {
							depTruckQty = depItem.getTruckMaxNetWeightT();
						}
						else if (depItem.getOrderedMeasure() != null) {
							depTruckQty = depItem.getOrderedMeasure();
						}
					}
					else { // case 1
						depTruckQty = depItem.getOrderedMeasure();
					}
					
					depCount++;
					sumDepTruckQty+=depTruckQty;
					// --- Different ship-to, add "*" marker ---
					if ( !dedicatedShiptoSapBpId.equals(depItem.getShiptoSapBpId()) ) {
						mixedDelivery = true;
					}
				}
			}
			BigDecimal diff = BigDecimal.valueOf(railQty - sumDepTruckQty).setScale(2, RoundingMode.HALF_UP);
			railItemWrapped.setRemainingLoad("" + diff.toPlainString() + " t");
			railItemWrapped.setDepTruckOrderDelivery(depCount + (mixedDelivery ? " *" : ""));
			railItemWrapped.setRailQty(railQty);
			railItemWrapped.setDedicatedShiptoName(dedicatedShiptoName);
			railItemWrapped.setDedicatedShiptoMap(dedicatedShiptoSapBpIdMap);

			// --- Load timezones for shiptos / we optimize by caching locally ---//
			// FIXME tito/05.08.2013: Extend the view to pull in the TIMEZONE_CD for the ShipTo entities
			TimeZone shiptoTZ = shiptoTZCache.get(railItem.getShiptoSapBpId());
			if (shiptoTZ == null) {
				ShiptoEntity shipto = serviceLocator.getOrderDetailService().loadShipto(railItem.getShiptoSapBpId());
				shiptoTZ = serviceLocator.getTimeZoneService().loadForShipTo(shipto, locale);
				shiptoTZCache.put(railItem.getShiptoSapBpId(), shiptoTZ);
			}
			railItemWrapped.setActualDelivery(
					shiptoTZ.fmtAsDayMonthYearHourMinuteWithOffset(
							railItem.getActualDeliveryDat(),
							UserProvider.getTimezone()));

			biRailOrderListWrapped.add(railItemWrapped);
		}
		return biRailOrderListWrapped;
	}
	
	public static  List<ContainerMonitoringWrapper> wrapContainerMonitoringEntities(
			List<ContainerMonitoringEntity> entityList,
			TimeZone userTz,
			Locale userLocale,
			TimeZone defaultSystemTz) {

		List<ContainerMonitoringWrapper> wrappers = new ArrayList<ContainerMonitoringWrapper>();
		
		for (ContainerMonitoringEntity entity : entityList) {
			ContainerMonitoringWrapper wrapper = new ContainerMonitoringWrapper(entity);
			if(entity.getMaterialName() != null && entity.getMaterialQuantity() != null && entity.getUom() != null){
				wrapper.setMaterialAndQty(entity.getMaterialName() + " ( " + entity.getMaterialQuantity() + " " + entity.getUom() + " )");
			}
			wrapper.setProdPlant(entity.getPlantName());
			wrapper.setStandingTimeInMins(entity.getStandingTime());

			TimeZone shippingPointTz;
			if (StringUtils.isNotEmpty(entity.getShippingpointTimezoneCd())) {
				shippingPointTz = new TimeZone(entity.getShippingpointTimezoneCd(),
											   userLocale);
			}
			else {
				shippingPointTz = defaultSystemTz;
			}

			wrapper.setLoadingTime(shippingPointTz.fmtAsDayMonthYearHourMinuteWithOffset(
					entity.getLoadingTime(),
					userTz));

			wrapper.setArrivalTime(shippingPointTz.fmtAsDayMonthYearHourMinuteWithOffset(
					entity.getArrivalTime(),
					userTz));

			wrappers.add(wrapper);
		}
		
		return wrappers;
		
	}
	
	@AdnTrace
	private TruckRouteEntity getTruckRoute(Long shippingPointId, Long shiptoId, Long delTypeCd) {
		return serviceLocator.getOrderService().getTruckRoute(shippingPointId,
				shiptoId, delTypeCd);
	}
	
	public static BestellungForm getBestellungForm(HttpSession session) {
    	return (BestellungForm)session.getAttribute(BestellungForm.SESSION_KEY);
    }

	/**
	 * Returns false if the passed value is null, 0, or -1 (these values indicate special non-selection or
	 * invalid values). Used for combobox values.
	 *
	 * @param value
	 * @return
	 */
	public static boolean isValidSelection(Object value) {
		return (value != null && !"".equals(value) && !String.valueOf(value).equals("0")
				&& !String.valueOf(value).equals("-1"));
	}
	

	/**
	 * Builds the String representation of the price of the SAP material, based on contract data.
	 * 
	 * NOTE (romek Aug 31, 2011): In Logon 1.6 (branch logon_1_6_0_1) this logic was based on
	 * material conditions from SAP (TLGNS_SAP_MAT_COND), but in EBM contracts define this data.
	 * In case material conditions are needed again, for more information you can check our Twiki:
	 * https://aww.adnovum.ch/twiki/bin/view/CrhPegasus/WorkshopCrh20090622#Minutes_1_Transport_Conditions
	 * 
	 * @param contractItem
	 * @return
	 */
	public static String getSapMatPrice(SapContractItemEntity contractItem) {
		// [[
		String result = null;
		Object[][] params = {{"contractItem", contractItem}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(OrderTakingUtils.class, "getSapMatPrice", params);
		}
		// ]]
		try {
			if (contractItem != null) {
				BigDecimal rate = OrderUtils.calculateRate(contractItem);
				SapContractEntity contract = contractItem.getSapContract();
				CurrencyBsEntity currency = contract.getCurrency();
			
				if (rate != null && currency != null) {
					return result = rate + " " + currency.getCurrencyCd();
				}
			}
			
			return result = BestellungForm.NO_LIST_PRICE;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	private void calculateOpenQuantity(OrderTakingEditorDto editor,
			OrderTakingEditorOrderItemDto orderItemDto) {
		// [[
		Object[][] params = {{"editor", editor},
							{"orderItemDto", orderItemDto}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "calculateOpenQuantity", params);
		}
		// ]]
		try {
			List<OrderTakingEditorOrderItemDto> orderItems;
			
			if (orderItemDto == null) {
				orderItems = editor.getOrderItems();
			}
			else {
				orderItems = new ArrayList<OrderTakingEditorOrderItemDto>(1);
				orderItems.add(orderItemDto);
			}
			
			calculateOpenQuantities(editor, orderItems);
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
	 * Calculates the open quantity of each given order item. The result is formatted, used for
	 * displaying this information in the New Order panel.
	 * 
	 * @param editor
	 * @param orderItems
	 */
	private void calculateOpenQuantities(OrderTakingEditorDto editor, 
			List<OrderTakingEditorOrderItemDto> orderItems) {
		// [[
		Object[][] params = {{"editor", editor},
							{"orderItems", orderItems}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "calculateOpenQuantities", params);
		}
		// ]]
		try {
			// local cache (key: UomBsEntity.getUomCd, value: Localized Display Text for UoM)
			Map<String, String> uomLocalizedDisplayTextCache = new HashMap<String, String>();

			int orderItemsSize = orderItems.size();
			DeliverySettingSalesAreaMap salesAreaMap = editor.getSalesAreaMap();

			for (int i = 0; i < orderItemsSize; i++) {
				OrderTakingEditorOrderItemDto orderItemDto = orderItems.get(i);
				String compositeIdentifier = orderItemDto.getCompositeIdentifier();
				final DeliverySettingDTO setting =
						salesAreaMap.getDeliverySettingByCompositeIdentifier(compositeIdentifier);
				
				SapContractItemEntity sapContractItem = setting.getSapContractItem();
				
				if (sapContractItem == null) {
					continue;
				}

				if (setting.isQuantityControlled()) {
					setOpenQuantityOnItem(orderItemDto,
										  setting.getQuantity(),
										  sapContractItem.getTargetQuantityUom(),
										  uomLocalizedDisplayTextCache);
				}
				else {
					setOpenQuantityOnItem(orderItemDto,
										  null,
										  null,
										  uomLocalizedDisplayTextCache);
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

	private void setOpenQuantityOnItem(OrderTakingEditorOrderItemDto item,
									   BigDecimal qty,
									   UomBsEntity uom,
									   Map<String, String> uomLocalizedDisplayTextCache) {

		String formattedQty;

		if (qty != null) {
			String uomCd = uom.getUomCd();

			//--- Use our local cache to avoid hitting the DB unnecessarily
			String uomDisplayTxt = uomLocalizedDisplayTextCache.get(uomCd);
			if (uomDisplayTxt == null) {
				UomTxEntity uomTx = serviceLocator.getDispokarteService()
												  .fetchUomTxEntity(uomCd, locale);

				uomDisplayTxt = uomTx.getTxt();
				uomLocalizedDisplayTextCache.put(uomCd, uomDisplayTxt);
			}

			BigDecimal scaledQty = qty.setScale(2, BigDecimal.ROUND_HALF_UP);
			formattedQty = new StringBuilder()
					.append(new DecimalFormat("#0.##").format(scaledQty))
					.append(' ')
					.append(uomDisplayTxt)
					.toString();
		}
		else {
			formattedQty = BestellungForm.NO_OPEN_QUANTITY;
		}

		item.setOpenQuantity(formattedQty);
	}

	public String getQuantityPerSdoLabel(String uomCd) {
		// [[
		String result = null;
		Object[][] params = {{"uomCd", uomCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getQuantityPerSdoLabel", params);
		}
		// ]]
		try {
			UomTxEntity perSdoQtyUomTx = serviceLocator.getRefDataGenericsService().findTxEntity(UomTxEntity.class, locale, uomCd);
			
		    String deliveryAbbreviationStr = Message.lookup(locale, "dispokarte.texte.perSdorderQuantity.delivery.abbreviation"); 
			
			result = perSdoQtyUomTx.getTxt() + " / " + deliveryAbbreviationStr;
			return result;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Returns a deliveryFrom calendar based on the given {@code OrderTakingEditorDto}
	 * See: {@link OrderUtils#getDeliveryDateTime(String, String, Long, Long, TimeZone)}
	 * @return the calculated calendar instance
	 */
	public static Calendar getEditorDeliveryFrom(
			OrderTakingEditorDto orderTakingEditorDto,
			ShiptoEntity shipTo,
			TimeZone timeZone) {
		String deliveryFromDatStr = orderTakingEditorDto.getDeliveryFromDatStr();
		String deliveryFromTime = orderTakingEditorDto.getDeliveryFromTime();
		Long open1From = shipTo.getOpen1From();
		Calendar deliveryFromCal = OrderUtils.parseDeliveryDateTime(
				timeZone,
				deliveryFromDatStr,
				deliveryFromTime, open1From, Constant._5_HOURS_IN_MINUTES);
		return deliveryFromCal;
	}

	/**
	 * Returns a deliveryTo calendar based on the given {@code OrderTakingEditorDto}
	 * See: {@link OrderUtils#getDeliveryDateTime(String, String, Long, Long, TimeZone)}
	 * @return the calculated calendar instance
	 */
	public static Calendar getEditorDeliveryTo(
			OrderTakingEditorDto orderTakingEditorDto,
			ShiptoEntity shipTo,
			TimeZone timeZone) {
		String deliveryToDatStr = orderTakingEditorDto.getDeliveryToDatStr();
		String deliveryToTime = orderTakingEditorDto.getDeliveryToTime();
		
		Long open2To = shipTo.getOpen2To();
		Calendar deliveryToCal = OrderUtils.parseDeliveryDateTime(
				timeZone,
				deliveryToDatStr,
				deliveryToTime, open2To, Constant._23_HOURS_IN_MINUTES);
		return deliveryToCal;
	}

	public static boolean hasRealMaterialForReplacePlanningMaterial(OrderTakingEditorOrderItemDto planningMaterialItem, 
			String planningMaterialType, DeliverySettingShiptoMap shiptoMap) {
		// [[
		boolean result = false;
		Object[][] params = {{"planningMaterialItem", planningMaterialItem},
							{"planningMaterialType", planningMaterialType},
							{"shiptoMap", shiptoMap}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(OrderTakingUtils.class, "hasRealMaterialForReplacePlanningMaterial", params);
		}
		// ]]
		try {
			if (planningMaterialItem != null) {
				Long planningMatContractId = planningMaterialItem.getSapContractId();
				Long planningMatContractItemId = planningMaterialItem.getSapContractItemId();
				
				Collection<DeliverySettingDTO> productList = shiptoMap.getMaterialDeliverySettingList();
				for (DeliverySettingDTO delSetting: productList ) {
					if (delSetting.getSapContractId() != null
							&& delSetting.getSapContractId().equals(planningMatContractId)
							&& !delSetting.getSapContractItemId().equals(planningMatContractItemId)
							&& Constant.MATERIAL_PACKAGE_BULK_DUMPED.equals(delSetting.getMaterialPackageCd())
							&& ( (delSetting.getMaterialIsAgg() && Constant.SAP_MATERIAL_NO_AGG_PLANNING.equals(planningMaterialType))
									|| (delSetting.getMaterialIsLf() && Constant.SAP_MATERIAL_NO_LANDFILL_PLANNING.equals(planningMaterialType))) ) {
						return result = true;
					}
				}
			}
			else {
				return result = true;
			}
			return result;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
}
