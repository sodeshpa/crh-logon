/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.ajax;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.beans.OrderSearchInformations;
import com.crh.logon.beans.ShippingOrderFetchListBean;
import com.crh.logon.business.ordercreation.DeliveryOptionConfigurationDTO;
import com.crh.logon.business.ordercreation.DeliverySettingSalesAreaMap;
import com.crh.logon.business.ordercreation.dto.DeliverySettingBasicDTO;
import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.business.ordercreation.dto.SimpleVehicleTypeWrapper;
import com.crh.logon.business.ordercreation.dto.SupplyPlantDTO;
import com.crh.logon.business.ordercreation.dto.TruckTypeWrapper;
import com.crh.logon.business.ordercreation.dto.TruckWagonTypeWrapper;
import com.crh.logon.business.ordercreation.dto.WagonTypeWrapper;
import com.crh.logon.business.rfidadministration.parameters.RFIDDataCache;
import com.crh.logon.business.services.beans.LoadShippingOrdersBean;
import com.crh.logon.business.services.beans.ShippingOrderLoadBean;
import com.crh.logon.business.services.beans.UomConversionResult;
import com.crh.logon.business.services.interfaces.BestellungService;
import com.crh.logon.business.services.interfaces.ConfigService;
import com.crh.logon.business.services.interfaces.CustorderService;
import com.crh.logon.business.services.interfaces.DispokarteService;
import com.crh.logon.business.services.interfaces.OrderChangeService;
import com.crh.logon.business.services.interfaces.RefDataGenericsService;
import com.crh.logon.business.services.interfaces.ShippingPointService;
import com.crh.logon.business.services.interfaces.UomConverterService;
import com.crh.logon.business.services.localinterfaces.BestellungServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RFIDServiceLocalInterface;
import com.crh.logon.business.services.reporting.SqlDataExporter;
import com.crh.logon.business.services.utils.AdjustTimesUtils;
import com.crh.logon.business.services.utils.AdjustTimesUtils.ShiptToOpening;
import com.crh.logon.business.services.utils.BeanUtilsBuilder;
import com.crh.logon.business.services.utils.CustorderSplitWrapper;
import com.crh.logon.business.services.utils.CustorderUtils;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.MaterialColumn;
import com.crh.logon.business.services.utils.MaterialType;
import com.crh.logon.business.services.utils.OrderSearchListRange;
import com.crh.logon.business.services.utils.OrderTakingEditorDto;
import com.crh.logon.business.services.utils.OrderTakingEditorOrderItemDto;
import com.crh.logon.business.services.utils.OrderUtils;
import com.crh.logon.business.services.utils.RFIDBean;
import com.crh.logon.business.services.utils.RFIDBean.AbstractRFIDBean;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.business.services.utils.SplittedOrderData;
import com.crh.logon.business.services.utils.SplittedOrderItemData;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.business.services.utils.TruckWagonTypeWrapperUtil;
import com.crh.logon.business.services.utils.validation.ValidationException;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.CustorderError;
import com.crh.logon.converters.Base64ByteArrayConverter;
import com.crh.logon.converters.BigDecimalConverter;
import com.crh.logon.converters.BooleanConverter;
import com.crh.logon.converters.DateConverter;
import com.crh.logon.converters.LongConverter;
import com.crh.logon.converters.StringConverter;
import com.crh.logon.order.web.actions.auftrag.AuftragBearbeitungAction;
import com.crh.logon.order.web.beans.ExtOrderSearchBean;
import com.crh.logon.order.web.beans.OrderSearchResultBean;
import com.crh.logon.order.web.forms.OrderRFIDPopupForm;
import com.crh.logon.order.web.forms.auftrag.AuftragBearbeitungForm;
import com.crh.logon.order.web.forms.bestellung.BestellungForm;
import com.crh.logon.order.web.forms.dispokarte.DispokarteForm;
import com.crh.logon.order.web.forms.dispokarte.SilobelegungEditForm;
import com.crh.logon.order.web.utils.DeliveryOptionConfigurationWrapper;
import com.crh.logon.order.web.utils.OrderSearchExportMetaData;
import com.crh.logon.order.web.utils.OrderSearchExportUtil;
import com.crh.logon.order.web.utils.OrderTakingEditorParamsDto;
import com.crh.logon.order.web.utils.OrderTakingEditorSplitParamsDto;
import com.crh.logon.order.web.utils.OrderTakingUomConversionDto;
import com.crh.logon.order.web.utils.OrderTakingUtils;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.CustorderItemEntity;
import com.crh.logon.persistence.entities.common.LdpointMatRestrEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.OrderTruckTypeEntity;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.common.ShsareaRestrTruckTypeEntity;
import com.crh.logon.persistence.entities.refdata.ArchivingStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.OrderSpiBsEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.SimpleVehicleTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.TruckTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.WagonTypeTxEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialAltUomEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.CommExecCarrierPfEntity;
import com.crh.logon.persistence.entities.views.CustordOnetimeDataEntity;
import com.crh.logon.persistence.entities.views.CustorderCreateEntity;
import com.crh.logon.persistence.entities.views.ExtOrderSearchEntity;
import com.crh.logon.persistence.entities.views.OrdItemAssignmtLoadEntity;
import com.crh.logon.persistence.entities.views.OrderCreateEntity;
import com.crh.logon.persistence.entities.views.OrderSearchEntity;
import com.crh.logon.persistence.entities.views.PoolCarrierCarrierEntity;
import com.crh.logon.persistence.entities.views.SdoOnetimeDataEntity;
import com.crh.logon.persistence.entities.views.ShippBimodalRoadRailEntity;
import com.crh.logon.persistence.entities.views.SoldshipFlexSrchEntity;
import com.crh.logon.persistence.entities.views.VehicleEntity;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.NumberUtils;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.WagonNumberUtils;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.forms.RFIDPopupForm;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;
import com.crh.logon.web.utils.UserProvider;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;


/**
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to 
 * Ajax components. 
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="OrderAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.order.web.utils.*"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.beans.OrderSearchInformations"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.order.web.utils.OrderTakingEditorParamsDto" 
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.services.utils.OrderTakingEditorDto" exclude="salesAreaMap,order,orderHelper,fullShippintpointList,fullSupplyPlantList,possibleSupplyPlantCdSet,possibleShippingPointIdSet,possibleShippingPointNoSet"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.services.utils.SplittedOrderData"
 * @adnbus.dwr.convert converter="map" match="java.util.Map"/>
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.ordercreation.dto.DeliverySettingDTO"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.ordercreation.dto.CommercialCarrierDTO"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.ordercreation.dto.ExecutingCarrierDTO"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.ordercreation.dto.PlantDTO"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.ordercreation.dto.SupplyPlantDTO"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.ordercreation.dto.ShippingpointDTO"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.ordercreation.dto.LoadingPointDTO"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.ordercreation.dto.DeliverySettingDTO$DeliverySettingLangDepData"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.ordercreation.dto.DeliverySettingBasicDTO"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.ordercreation.DeliveryOptionConfigurationDTO"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.ordercreation.dto.TruckWagonTypeWrapper"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.order.web.beans.OrderSearchResultBean"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.order.web.beans.ExtOrderSearchBean"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.beans.MaterialQuantityUomBean"
 * @adnbus.dwr.signature method="updateRestriction" parameters="int restrIdx, DeliverySettingBasicDTO inputDeliverySettingParameters, HttpServletRequest request" imports="com.crh.logon.business.ordercreation.dto.DeliverySettingBasicDTO, javax.servlet.http.HttpSession" 
 * 
 */
public class OrderAjaxService extends DwrServiceSupport {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(OrderAjaxService.class);
    
    public static final String CALLER_ORDER_CREATE = "orderCreate";
    public static final String CALLER_ORDER_SEARCH = "orderSearch";
    public static final String CALLER_DISPOKARTE = "dispokarte";
	
    public ListRange fetchShipToKundeList(
    		int start, 
    		int count, 
    		String orderBy,
    		String query,
    		HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchShipToKundeList", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_SHIPTO_CARD_R);					
			return result = getServiceLocator().getDispokarteService().fetchShipTos(start, count, query);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	@AdnTrace
	public ListRange fetchShipToKundeListWithCemOrAggFilter(
			int start,
			int count,
			String query,
			boolean activeCem,
			boolean activeAgg) {
		Authorization.authorize(Authorization.MASTER_DATA_SHIPTO_CARD_R);
		return getServiceLocator().getDispokarteService().fetchShipTos(start, count, query, activeCem, activeAgg);
	}
    
    public ListRange fetchRealSapBpList(
    		int start, 
    		int count, 
    		String orderBy,
    		String query,
    		HttpServletRequest request,
    		HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"request", request},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchRealSapBpList", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_SHIPTO_CARD_R);
			
			final DispokarteForm dispoForm = getDispokarteForm(session);
			final Long soldtoSapBpIdFilter = dispoForm.getSoldTo().getSapBpId();
			
			return result = getServiceLocator().getBestellungService().fetchRealSoldtoShiptoSareaList(start, count, query, soldtoSapBpIdFilter, request.getLocale());
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
	 * Function called from Dispokarte sales area tabs in case any of the dropdowns changes. The change is supposed to recalculate
	 * all the valid lists and is supposed to render on the ui these lists, and unselect those settings which turn out to be invalid
	 * when specifying a parameter.
	 * 
	 * Example: In case Truck is selected as uom, and the user changes the delivery type to rail, it is not anymore a valid uom, what is 
	 * selected -> from the restriction it will be deleted, and the user needs to specify other.
	 * 
	 * @param restrIdx
	 * @param inputDeliverySettingParameters - wrapper object containing all the attributes received on the client side a deliverysettingBasicdto javascript object is
	 * created and that is passed in here
	 * @param request
	 * @return
	 */
	public DeliverySettingDTO updateRestriction(int restrIdx,
			DeliverySettingBasicDTO inputDeliverySettingParameters, HttpServletRequest request) {

		Authorization.authorize(Authorization.MASTER_DATA_SHIPTO_CARD_W);

		DispokarteForm form = getDispokarteForm(request.getSession());
		form.setActiveIdx(restrIdx);

		final DeliverySettingDTO deliverySettingDTO = form.getCurrSalesAreaBean().getRestrictionDeliverySettings().get(restrIdx);
		final DeliverySettingSalesAreaMap salesAreaMap = form.getCurrSalesAreaBean().getSalesAreaMap();
		
		//this is not good like that, something will be needed in order that we don't need to set this manually!
		inputDeliverySettingParameters.setSapMaterial(deliverySettingDTO.getSapMaterial());
		inputDeliverySettingParameters.setSarea(deliverySettingDTO.getSarea());
		
		boolean isCashSales = false;
		
		if (deliverySettingDTO.getSapContractItem() != null){
			SapContractEntity contract = deliverySettingDTO.getSapContractItem().getSapContract();
			isCashSales = getServiceLocator().getOrderService()
					.isCashSales(contract, deliverySettingDTO.getSarea());
		}
		DeliverySettingDTO deliverySetting = OrderUtils.getDeliverySettingFromBasicDTO(
				inputDeliverySettingParameters, salesAreaMap, deliverySettingDTO, isCashSales, getServiceLocator().getTruckAdminService(), getServiceLocator().getBestellungService(), getServiceLocator().getDeliverySettingService());
				
		final DeliveryOptionConfigurationDTO optionConfigForInputParameters = DeliveryOptionConfigurationWrapper.getDeliveryOptionConfiguration(salesAreaMap, deliverySetting);
		final ShiptoSareaRestrEntity shiptoSareaRestr = deliverySettingDTO.getShiptoSareaRestr();

		List<TruckWagonTypeWrapper> selectedVehicleTypeWrapperList = deliverySetting.getSelectedVehicleTypeWrapperList();
		List<ShsareaRestrTruckTypeEntity> shsareaRestrTruckTypeEntities = new ArrayList<ShsareaRestrTruckTypeEntity>(selectedVehicleTypeWrapperList.size());
		for (TruckWagonTypeWrapper truckWagonTypeWrapper : selectedVehicleTypeWrapperList) {
			
			if (truckWagonTypeWrapper instanceof TruckTypeWrapper) {
				
				ShsareaRestrTruckTypeEntity shsareaRestrTruckTypeEntity = new ShsareaRestrTruckTypeEntity();
				shsareaRestrTruckTypeEntity.setEvaluationPriority(truckWagonTypeWrapper.getPriority());
				shsareaRestrTruckTypeEntity.setShiptoSareaRestr(shiptoSareaRestr);
				
				// If the truckWagonTypeWrapper is really (!?! naming problem?) a truck wrapper we can set the truckBsEntity
				// What is not implemented here is the WagonTypeWrapper
				TruckTypeBsEntity bsEntity = ((TruckTypeWrapper) truckWagonTypeWrapper).getTruckTypeBsEntity();
				
				shsareaRestrTruckTypeEntity.setTruckType(bsEntity);
				shsareaRestrTruckTypeEntities.add(shsareaRestrTruckTypeEntity);
			}
		}
		
		shiptoSareaRestr.setRestrTruckTypes(shsareaRestrTruckTypeEntities);
		TruckWagonTypeWrapper maxPrioVehicle = null;
		if (deliverySetting.getPerSdorderQuantity() == null) {
			if (selectedVehicleTypeWrapperList != null && !selectedVehicleTypeWrapperList.isEmpty()) {
				maxPrioVehicle = TruckWagonTypeWrapperUtil.findProperTruck(selectedVehicleTypeWrapperList);
			}
			else {
				List<TruckWagonTypeWrapper> vehiclesForInput = optionConfigForInputParameters.getVehicleTypeWrapperList();
				if (vehiclesForInput != null && !vehiclesForInput.isEmpty()) {
					maxPrioVehicle = TruckWagonTypeWrapperUtil.findProperTruck(vehiclesForInput);
				}
			}
			
			if (maxPrioVehicle != null) {
				UomBsEntity uomTo = getServiceLocator().getRefDataGenericsService().findBsEntity(UomBsEntity.class, Constant.UOM_TONS);
				shiptoSareaRestr.setPerSdorderQuantity(maxPrioVehicle.getMaxGrossWeightT());
				shiptoSareaRestr.setPerSdorderUom(uomTo);
			}
		}
		else {
			shiptoSareaRestr.setPerSdorderQuantity(deliverySetting.getPerSdorderQuantity());	
			shiptoSareaRestr.setPerSdorderUom(deliverySetting.getPerSdorderUom());
		}
		
		
		shiptoSareaRestr.setUom(deliverySetting.getUom());
		
		// convert per single delivery quantity
		if (shiptoSareaRestr.getPerSdorderQuantity() != null && shiptoSareaRestr.getUom() != null && shiptoSareaRestr.getSapMaterial() != null) {
			
			OrderTakingUomConversionDto conversionDto = new OrderTakingUomConversionDto();
			conversionDto.setInQuantity(shiptoSareaRestr.getPerSdorderQuantity());
			
			if (shiptoSareaRestr.getPerSdorderUom() == null) {
				UomBsEntity uomTo = getServiceLocator().getRefDataGenericsService().findBsEntity(UomBsEntity.class, Constant.UOM_TONS);
				shiptoSareaRestr.setPerSdorderUom(uomTo);
			}
			
			conversionDto.setInFromUomCd(shiptoSareaRestr.getPerSdorderUom().getUomCd());
			conversionDto.setInToUomCd(shiptoSareaRestr.getUom().getUomCd());
			conversionDto.setInSapMaterialId(shiptoSareaRestr.getSapMaterial().getSapMaterialId());
			conversionDto.setInDeliveryType(shiptoSareaRestr.getDeliveryType() != null ? shiptoSareaRestr.getDeliveryType().getDeliveryTypeCd() : null);
			conversionDto.setInVehicleTypeCd(maxPrioVehicle != null ? maxPrioVehicle.getTypeCd() : null);
			conversionDto.setInAllowFuzzy(false);
			
			conversionDto = convertUomWithFallbacks(conversionDto);
			
			if (conversionDto != null && conversionDto.getOutQuantity() != null && conversionDto.getOutUom() != null) {
				shiptoSareaRestr.setPerSdorderQuantity(conversionDto.getOutQuantity());	
				UomBsEntity uom = getServiceLocator().getRefDataGenericsService().findBsEntity(UomBsEntity.class, conversionDto.getOutUom());
				shiptoSareaRestr.setPerSdorderUom(uom);
			}
		}
		
		shiptoSareaRestr.setAutoCarrierAssignmentFlag(deliverySetting.getAutoCarrierAssignmentFlag());
		shiptoSareaRestr.setAutoPoolAssignmentFlag(deliverySetting.getAutoPoolAssignmentFlag());
		shiptoSareaRestr.setAutoReleaseFlag(deliverySetting.getAutoReleaseFlag());
		shiptoSareaRestr.setOpenCustorderFlag(deliverySetting.getOpenCustorderFlag());
		shiptoSareaRestr.setSapSuppressMinChargeFlag(deliverySetting.getSapSuppressMinChargeFlag());
		shiptoSareaRestr.setOnHoldOrderAndLockedFlag(deliverySetting.getOnHoldOrderAndLockedFlag());
		shiptoSareaRestr.setFreeDeliveryFlag(deliverySetting.getFreeDeliveryFlag());
		shiptoSareaRestr.setReleaseSapFlag(deliverySetting.getReleaseSapFlag());
		shiptoSareaRestr.setExactCustorderQtyFlag(deliverySetting.getExactCustorderQtyFlag());
		shiptoSareaRestr.setExactSdorderQtyFlag(deliverySetting.getExactSdorderQtyFlag());
		shiptoSareaRestr.setQuantity(deliverySetting.getQuantity());
		shiptoSareaRestr.setDeliveryTimeTolerance(deliverySetting.getDeliveryTimeTolerance());
		shiptoSareaRestr.setDeliveryTimeToleranceUom(deliverySetting.getDeliveryTimeToleranceUomCd());
		shiptoSareaRestr.setDeliveryFreqQtyperhre(deliverySetting.getDeliveryFreqQtyperhre());
		shiptoSareaRestr.setDeliveryFreqQtyperhreUom(deliverySetting.getDeliveryFreqQtyperhreUom());				
		shiptoSareaRestr.setDeliveryFreqEveryTime(deliverySetting.getDeliveryFreqEveryTime());
		shiptoSareaRestr.setDeliveryFreqEveryTimeUom(deliverySetting.getDeliveryFreqEveryTimeUomCd());
		shiptoSareaRestr.setDeliveryType(deliverySetting.getDeliveryType());
		shiptoSareaRestr.setShippingpoint(deliverySetting.getShippingpoint());
		shiptoSareaRestr.setLoadingpoint(deliverySetting.getLoadingpoint());
		shiptoSareaRestr.setShiptoSilo(deliverySetting.getShiptoSilo());
		shiptoSareaRestr.setProductionPlant(deliverySetting.getProductionPlant());
		shiptoSareaRestr.setCommCarrier(deliverySetting.getCommercialCarrier());
		shiptoSareaRestr.setExecCarrier(deliverySetting.getExecutingCarrier());
		shiptoSareaRestr.setPool(deliverySetting.getPool());
		shiptoSareaRestr.setPayerSapBp(deliverySetting.getPayerSapBp());
		shiptoSareaRestr.setBilltoSapBp(deliverySetting.getBilltoSapBp() );
		shiptoSareaRestr.setMixDesign(deliverySetting.getMixDesign());	
		
		if (Constant.INVALID_OBJECT_ID_STR.equals(deliverySetting.getServiceProcessIndicatorId())) {
			shiptoSareaRestr.setSpiCd(null);
		}
		else {
			shiptoSareaRestr.setSpiCd(deliverySetting.getServiceProcessIndicatorId());
		}
		TimeZone userTimeZone = getUserTimeZone();		
		DeliverySettingDTO  newDto  = new DeliverySettingDTO(shiptoSareaRestr, userTimeZone);
		/**
		 * Unfortunately the {@link ShiptoSareaRestrEntity} does not contain supply plant attribute. This is problematic as we are here
		 * recreating the setting from the restriction, and if it has no shippingpoint yet, the supply plant will be nulled also.
		 * Therefore 'manually' setting back to the original value here.
		 * THis would be very good if we could just add that field to the defaults, would make life easier.
		 */
		BestellungService bestellungService = getBestellungService();
		if  (inputDeliverySettingParameters.getSupplyPlantCd() != null) {
			String plantCd = inputDeliverySettingParameters.getSupplyPlantCd();
			List<SupplyPlantDTO> plantList = optionConfigForInputParameters.getSupplyPlantList();
			for (SupplyPlantDTO currentPlant : plantList) {
				String currentPlantCd = currentPlant.getPlantCd();
				if (plantCd.equals(currentPlantCd)) {
					PlantBsEntity currentPlantBs = bestellungService.findPlantByCdNoAuthorization(currentPlant.getPlantCd());
					newDto.setSupplyPlant(currentPlantBs);
					break;
				}
			}
		}
		
		form.getCurrSalesAreaBean().getSalesAreaMap().getMaterialRestrictionDeliverySettings().getPossibleDeliverySettings().remove(deliverySettingDTO);
		form.getCurrSalesAreaBean().getSalesAreaMap().getServiceRestrictionDeliverySettings().getPossibleDeliverySettings().remove(deliverySettingDTO);
		form.getCurrSalesAreaBean().getNewDeliverySettingList().remove(deliverySettingDTO);
		
		newDto.getAdditionalData().setMaterialPackageTxt(deliverySettingDTO.getMaterialPackageTxt());
		newDto.getAdditionalData().setTxt(deliverySettingDTO.getTxt());
		
		form.getCurrSalesAreaBean().getNewDeliverySettingList().add(newDto);
        DeliveryOptionConfigurationDTO options = DeliveryOptionConfigurationWrapper.getDeliveryOptionConfiguration(salesAreaMap, newDto);
        
		if (deliverySettingDTO.getDeliveryType() != null && LogonUtil.isServiceOnlyOrder(deliverySettingDTO.getDeliveryType().getDeliveryTypeCd())) {
			newDto.setDeliveryType(deliverySettingDTO.getDeliveryType());
			
			if (options.getDeliveryTypeList() != null) {
				Iterator<DeliveryTypeTxEntity> iter = options.getDeliveryTypeList().iterator();
				while (iter.hasNext()) {
					DeliveryTypeTxEntity actDeliveryTypeTx = iter.next();
					if (actDeliveryTypeTx.getDeliveryTypeTxPK().getDeliveryTypeBs().getDeliveryTypeCd() != Constant.DELIVERY_TYPE_SERVICE) {
						iter.remove();
					}
				}
			}
			
			final List emptyList = new ArrayList();
			final Map<String, String> emptyMap = new HashMap<String, String>();
			options.setCommercialCarrierList(emptyList);
			options.setExecutingCarrierList(emptyList);
			options.setDeliveryFreqEveryTimeUomMap(emptyMap);
			options.setDeliveryTimeToleranceUomMap(emptyMap);
			options.setLoadingpointList(emptyList);
			options.setMaterialMixDesignList(emptyList);
			options.setPoolList(emptyList);
			options.setShippingpoinList(emptyList);
			options.setSiloList(emptyList);
			options.setSupplyPlantList(emptyList);
			options.setVehicleTypeWrapperList(emptyList);
		}
        
        newDto.setDeliveryOptionConfiguration(options);	
        newDto.setVehicleTypeCds(inputDeliverySettingParameters.getVehicleTypeCds());
		boolean isAvailableInPickupAppFlagEnabled =
				salesAreaMap.isAvailableInPickupAppFlagEnabled(newDto);
		newDto.setAvailableInPickupAppFlagEnabled(isAvailableInPickupAppFlagEnabled);

		return newDto;
    }
  
	/**
     * Returns the filtered (by query ComboBox) list of vlgns_sap_sold_ship_sarea_srch elements
     * Used by the Ship-to search screen of UC 4012 Order Taking
     * 
     * @param start
     * @param count
     * @param orderBy
     * @param query
     * @param custWithoutOrderToday
     * @param activeCem
     * @param activeAgg
     * @return
     */
    public ListRange fetchSoldtoShiptoBestList(
    		int start, 
    		int count, 
    		String orderBy,
    		String query,
    		Boolean custWithoutOrderToday,
    		Boolean activeCem,
    		Boolean activeAgg,
    		HttpServletRequest request) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"custWithoutOrderToday", custWithoutOrderToday},
							{"activeCem", activeCem},
							{"activeAgg", activeAgg},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSoldtoShiptoBestList", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.ORDER);
			return result = getServiceLocator().getBestellungService().fetchSoldtoShiptoBestList(start, count, query,
					custWithoutOrderToday, activeCem, activeAgg, request.getLocale());
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
     * Returns the filtered (by query ComboBox) list of vlgnc_editbp_soldship_sarea elements
     * Used by the Ship-to search screen of UC 4012 Order Taking
     * 
     * @param start
     * @param count
     * @param orderBy
     * @param query
     * @param custWithoutOrderToday
     * @return
     */
    public ListRange fetchSoldtoShiptoModifyBpList(
    		int start, 
    		int count, 
    		String orderBy,
    		String query,
    		HttpServletRequest request) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSoldtoShiptoBestList", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.ORDER);
			return result = getServiceLocator().getBestellungService().fetchSoldtoShiptoModifyBpList(start, count, query, request.getLocale());
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

    @AdnTrace
    public List<PoolCarrierCarrierEntity> getCarrierListByPoolId(Long poolId, HttpServletRequest request) {
		//monitoring and order list does not have the same visibility rules. entryactions are authorized.
		Authorization.authorize(Authorization.PUBLIC_FUNCTION);
		Locale locale = request.getLocale();
		Set<Long> poolIdSet = new HashSet<Long>();
		poolIdSet.add(poolId);
		
		return getServiceLocator().getPoolAdminService().fetchPoolCarrierCarrierEntitysByPoolIds(poolIdSet , locale);
    }
    
    @AdnTrace
    public List<CommExecCarrierPfEntity> getExecCarrierListByPoolId(String carrierIds, HttpServletRequest request) {
		Authorization.authorize(Authorization.PUBLIC_FUNCTION);
		List<Long> carrierIdList = new ArrayList<Long>();
		if (!StringUtils.isEmpty(carrierIds)) {
			String[] attr = carrierIds.split(",");
			for (int i=0; i<attr.length; i++) {
				carrierIdList.add(Long.valueOf(attr[i]));
			}
		}
		return getServiceLocator().getCarrierService().findPartnerExecCarriersOrdered(carrierIdList);
    }
    
    public ShippingpointEntity getShippingPointById(Long spId, HttpSession session) {
		// [[
		ShippingpointEntity result = null;
		Object[][] params = {{"spId", spId},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getShippingPointById", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.ORDER);
			ServiceLocator serviceLocator = getServiceLocator();
			ShippingPointService shippingPointService = serviceLocator.getShippingPointService();
			return result = shippingPointService.getShippingPointById(spId);		
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }
    
    
    public ListRange fetchSapBpList(
    		int start, 
    		int count, 
    		String orderBy,
    		String query,
    		boolean bpIsSoldto,
    		boolean bpIsShipto,
    		boolean bpIsBillto,
    		boolean includingDummyBp,
    		HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"bpIsSoldto", bpIsSoldto + ""},
							{"bpIsShipto", bpIsShipto + ""},
							{"bpIsBillto", bpIsBillto + ""},
							{"includingDummyBp", includingDummyBp + ""},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSapBpList", params);
		}
		// ]]
		try {
			//monitoring and order list does not have the same visibility rules. entryactions are authorized.
			Authorization.authorize(Authorization.PUBLIC_FUNCTION);

			final ListRange listRange = new ListRange();
			final BestellungService bestService = getServiceLocator().getBestellungService();
			listRange.setData( bestService.fetchSapBpList(start, count, query, bpIsSoldto, bpIsShipto, bpIsBillto, includingDummyBp).toArray());
			listRange.setTotalSize( bestService.countSapBpList(query, bpIsSoldto, bpIsShipto, bpIsBillto, includingDummyBp));
			
			return result = listRange;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
    
    
    public ListRange fetchVehicleList(int start,
    		int count, 
    		String orderBy,
    		String query,
    		HttpServletRequest request,
    		HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"request", request},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchVehicleList", params);
		}
		// ]]
		try {
			
			Authorization.authorize(Authorization.PUBLIC_FUNCTION);
			
			ListRange vehicleListRange = getServiceLocator().getBestellungService().fetchVehicleList(start, count, query); 
			Object[] vehicleArray = vehicleListRange.getData();
			
			//format wagon no
			for (int i = 0; i < vehicleArray.length; i++) {
				VehicleEntity vehicle = (VehicleEntity) vehicleArray[i];
				if (Constant.VEHICLE_TYPE_WAGON.equals(vehicle.getVehicleType()) && (com.crh.logon.utils.StringUtils.notEmpty(vehicle.getVehicleName()))) {
					vehicle.setVehicleName(WagonNumberUtils.toDisplayNo(vehicle.getVehicleName()));
				}
			}
			
			return result = vehicleListRange;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private static void adjustSearchCrits(Map<String, String> searchCrits, Locale locale) {
		searchCrits.put(OrderSearchEntity.ATTR_LANG_CD, locale.getLanguage());
		if (!StringUtils.isEmpty(searchCrits.get(OrderSearchEntity.ATTR_ORDER_NUMBER))) {
			String validatedOrderNo = searchCrits.get(OrderSearchEntity.ATTR_ORDER_NUMBER);
			validatedOrderNo = validatedOrderNo.replaceAll("[^\\d]", "");
			searchCrits.put(OrderSearchEntity.ATTR_ORDER_NUMBER, validatedOrderNo);
		}
		if (StringUtils.isNotEmpty(searchCrits.get(ExtOrderSearchEntity.ATTR_EXT_ORDER_NUMBER))) {
			String validatedExtOrderNo = searchCrits.get(ExtOrderSearchEntity.ATTR_EXT_ORDER_NUMBER);
			validatedExtOrderNo = validatedExtOrderNo.replaceAll("[^\\d]", "");
			searchCrits.put(ExtOrderSearchEntity.ATTR_EXT_ORDER_NUMBER, validatedExtOrderNo);
		}
		if (StringUtils.isNotEmpty(searchCrits.get("vehicleCompositeId"))) {
			String compositeId = searchCrits.get("vehicleCompositeId");
			String vehicleIdString = null;
			String vehicleType = null;
			int separatorPosition = compositeId.indexOf("_");
			//x_1 => at least 1 character before and after the separator is needed
			if (separatorPosition > 0 && separatorPosition < compositeId.length() - 1) {
				vehicleType = compositeId.substring(0, separatorPosition);
				vehicleIdString = compositeId.substring(separatorPosition+1);
				try {
					Long.parseLong(vehicleIdString);
				}
				catch (NumberFormatException e) {
					throw new LogonException("unexpected format of compositeId (number_type expected): " + compositeId, Reason.RES_ERROR_IMPL);
				}
			}
			else {
				throw new LogonException("unexpected format of compositeId (number_type expected) (no _ found): " + compositeId, Reason.RES_ERROR_IMPL);
			}
			
			if (Constant.VEHICLE_TYPE_TRUCK.equals(vehicleType)) {
				searchCrits.put(OrderSearchEntity.ATTR_TRUCK_ID, vehicleIdString);
			}
			else if (Constant.VEHICLE_TYPE_WAGON.equals(vehicleType)) {
				searchCrits.put(OrderSearchEntity.ATTR_WAGON_ID, vehicleIdString);
			}
			else if (Constant.VEHICLE_TYPE_SIMPLE_VEHICLE.equals(vehicleType)) {
				searchCrits.put(OrderSearchEntity.ATTR_SIMPLE_VEHICLE_ID, vehicleIdString);
			}
			else {
				throw new LogonException("Unknown vehicle type " + searchCrits.get(VehicleEntity.ATTR_VEHICLE_TYPE), Reason.RES_ERROR_IMPL);
			}
		}
	}
	
	
    /**
	 * Used by UC 4015 (Order Search) and UC 40XX (Order Monitoring)
     */
    public OrderSearchListRange searchOrders(
    		int skip, 
    		int count, 
    		String sort,
    		HashMap<String, String> searchCriteria,
    		Boolean monitoring,
    		HttpServletRequest request,
    		HttpSession session) {
		// [[
    	OrderSearchListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"count", count + ""},
							{"sort", sort},
							{"searchCrits", searchCriteria},
							{"monitoring", monitoring},
							{"request", request},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "searchOrders", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.PUBLIC_FUNCTION);
			
			String sortBy = OrderSearchEntity.ATTR_DELIVERY_TO_DAT;
			final boolean sortAscending;
			
			if (!StringUtils.isEmpty(sort)) {
				String[] sortArr = sort.split(" ");
				sortBy = sortArr[0];
				if (sortBy.equals(OrderSearchEntity.ATTR_STATUS_TXT)) {
					sortBy = OrderSearchEntity.ATTR_ORDER_STATUS_CD;
				}
				else if (OrderSearchResultBean.ATTR_DELIVERY_FROM.equals(sortBy)) {
					sortBy = OrderSearchEntity.ATTR_DELIVERY_FROM_DAT;
				}
				else if (OrderSearchResultBean.ATTR_DELIVERY_TO.equals(sortBy)) {
					sortBy = OrderSearchEntity.ATTR_DELIVERY_TO_DAT;
				}
				else if (OrderSearchResultBean.ATTR_PLANNED_DELIVERY.equals(sortBy)) {
					sortBy = OrderSearchEntity.ATTR_PLANNED_DELIVERY_DAT;
				}
				else if (OrderSearchResultBean.ATTR_LOADING.equals(sortBy)) {
					sortBy = OrderSearchEntity.ATTR_LOADING_TS;
				}
				sortAscending = "ASC".equals(sortArr[1]);
			}
			else {
				sortAscending = true;
			}
			
			adjustSearchCrits(searchCriteria, request.getLocale());
			ServiceLocator serviceLocator = getServiceLocator();
			BestellungServiceLocalInterface localBestellungService = serviceLocator.getLocalBestellungService();
			TimeZone userTimeZone = getUserTimeZone();
			Locale userLocale = getUserLocale();
			return result = localBestellungService.searchOrders(skip, count, sortBy, sortAscending,
					searchCriteria, monitoring, userLocale, userTimeZone);
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
	 * Used by UC 4015 (Order Search) and UC 40XX (Order Monitoring)
     * @throws ParseException 
     */
    public OrderSearchInformations getOrderSearchInformations(
    		int skip, 
    		int count, 
    		String sort,
			HashMap<String, String> searchCriteria,
    		Boolean monitoring,
    		HttpServletRequest request) throws ParseException {
		// [[
		OrderSearchInformations result = null;
		Object[][] params = {{"skip", skip + ""},
							{"count", count + ""},
							{"sort", sort},
				{ "searchCrits", searchCriteria },
							{"monitoring", monitoring},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getOrderSearchInformations", params);
		}
		// ]]
		try {
			//monitoring and order list does not have the same visibility rules. entryactions are authorized.
			Authorization.authorize(Authorization.PUBLIC_FUNCTION);
			
			String sortBy = OrderSearchEntity.ATTR_DELIVERY_TO_DAT;
			final boolean sortAscending;
			
			if (!StringUtils.isEmpty(sort)) {
				String[] sortArr = sort.split(" ");
				sortBy = sortArr[0];
				if (sortBy.equals(OrderSearchEntity.ATTR_STATUS_TXT)) {
					sortBy = OrderSearchEntity.ATTR_ORDER_STATUS_CD;
				}
				else if (OrderSearchResultBean.ATTR_DELIVERY_FROM.equals(sortBy)) {
					sortBy = OrderSearchEntity.ATTR_DELIVERY_FROM_DAT;
				}
				else if (OrderSearchResultBean.ATTR_DELIVERY_TO.equals(sortBy)) {
					sortBy = OrderSearchEntity.ATTR_DELIVERY_TO_DAT;
				}
				else if (OrderSearchResultBean.ATTR_PLANNED_DELIVERY.equals(sortBy)) {
					sortBy = OrderSearchEntity.ATTR_PLANNED_DELIVERY_DAT;
				}
				sortAscending = "ASC".equals(sortArr[1]);
			}
			else {
				sortAscending = true;
			}
			
			adjustSearchCrits(searchCriteria, request.getLocale());
			ServiceLocator serviceLocator = getServiceLocator();
			BestellungServiceLocalInterface localBestellungService = serviceLocator.getLocalBestellungService();
			
			Locale userLocale = getUserLocale();
			TimeZone userTimeZone = getUserTimeZone();
			return result = localBestellungService.getOrderSearchInformations(skip, count, sortBy,
					sortAscending, searchCriteria, monitoring, userLocale, userTimeZone);
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
	 * Used by External Order Search
     */
    public ListRange searchExternalOrders(
    		int skip, 
    		int count, 
    		String sort,
    		HashMap<String, String> searchCrits,
    		HttpServletRequest request,
    		HttpSession session) throws ParseException {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"count", count + ""},
							{"sort", sort},
							{"searchCrits", searchCrits},
							{"request", request},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "searchExternalOrders", params);
		}
		// ]]
		try {
			//monitoring and order list does not have the same visibility rules. entryactions are authorized.
			Authorization.authorize(Authorization.PUBLIC_FUNCTION);
			
			String sortBy = OrderSearchEntity.ATTR_DELIVERY_TO_DAT;
			boolean sortAsc = true;
			if (!StringUtils.isEmpty(sort)) {
				String[] sortArr = sort.split(" ");
				sortBy = sortArr[0];
				if (sortBy.equals(OrderSearchEntity.ATTR_STATUS_TXT)){
					sortBy = OrderSearchEntity.ATTR_ORDER_STATUS_CD;
				}
				else if (ExtOrderSearchBean.ATTR_DELIVERY_FROM.equals(sortBy)) {
					sortBy = OrderSearchEntity.ATTR_DELIVERY_FROM_DAT;
				}
				else if (ExtOrderSearchBean.ATTR_DELIVERY_TO.equals(sortBy)) {
					sortBy = OrderSearchEntity.ATTR_DELIVERY_TO_DAT;
				}
				sortAsc = "ASC".equals(sortArr[1]);
			}
			
			adjustSearchCrits(searchCrits, request.getLocale());
			ListRange searchRes = getServiceLocator().getExternalOrderService().searchExternalOrders(
					skip, count, sortBy, sortAsc, searchCrits,
					request.getLocale(),
					getUserTimeZone());

			List<ExtOrderSearchBean> beans = new ArrayList<ExtOrderSearchBean>();
			for (Object o: searchRes.getData()) {
				ExtOrderSearchEntity entity = (ExtOrderSearchEntity)o;

				ExtOrderSearchBean bean = new ExtOrderSearchBean(entity);
				bean.setDeliveryFrom(getUserTimeZone()
											 .fmtAsDayMonthYearHourMinute(
													 entity.getDeliveryFromDat()));
				bean.setDeliveryTo(getUserTimeZone()
										   .fmtAsDayMonthYearHourMinute(
												   entity.getDeliveryToDat()));
				beans.add(bean);
			}
			searchRes.setData(beans.toArray());

			return result = searchRes;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
    
    public String getSummedQuantities(HashMap<String, String> searchCrits) throws ParseException {
		// [[
		String result = null;
		Object[][] params = {{"searchCrits", searchCrits}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getSummedQuantities", params);
		}
		// ]]
		try {
			//monitoring and order list does not have the same visibility rules. entryactions are authorized.
			Authorization.authorize(Authorization.PUBLIC_FUNCTION);
			
			adjustSearchCrits(searchCrits, Locale.GERMAN);
			double summedQuantities = getServiceLocator().getBestellungService().getSummedQuantities(searchCrits, getUserTimeZone());
			
			return result = NumberUtils.formatInteger((int) summedQuantities);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }
    
    public ListRange fetchSapMaterialList(
    		int start, 
    		int count, 
    		String orderBy,
    		String query,
    		HttpServletRequest request,
    		HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"request", request},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSapMaterialList", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.PUBLIC_FUNCTION);
			ServiceLocator serviceLocator = getServiceLocator();
			BestellungServiceLocalInterface localBestellungService = serviceLocator.getLocalBestellungService();
			Locale locale = request.getLocale();
			return result = localBestellungService.fetchSapMaterialList(start, count, query, locale);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	private static DispokarteForm getDispokarteForm(HttpSession session) {
		return (DispokarteForm) session.getAttribute(DispokarteForm.FORM_NAME);
	}

	/**
	 * Called when rfid icon is pressed for a silo on Dispokarte or Order Taking. Initializes the rfidForm.
	 * @param request
	 */
	public void selectSiloByIndex(String siloIndex, String referrer, HttpServletRequest request) {
		// [[
		Object[][] params = {{"siloIndex", siloIndex},
							{"referrer", referrer},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "selectSiloByIndex", params);
		}
		// ]]
		try {
			int index = -1;
			index = Integer.parseInt(siloIndex);
			HttpSession session = request.getSession();

			DispokarteForm dispoForm = null;
			BestellungForm orderForm = null;
			ShiptoEntity shipTo = null;

			if (SilobelegungEditForm.REFERRER_DISPOKARTE.equals(referrer)) {
				Authorization.authorize(Authorization.MASTER_DATA_SHIPTO_CARD_R);
				dispoForm = getDispokarteForm(session);
				shipTo = dispoForm.getShipTo();
			}
			else if (SilobelegungEditForm.REFERRER_ORDER_TAKING.equals(referrer)) {
				Authorization.authorize(Authorization.ORDER);
				orderForm = BestellungForm.getSessionInstance(request);
				shipTo = orderForm.getShipTo();
			}
			if (shipTo == null) {
				LOG.warn("Missing shipto, can not select silo: '" + siloIndex + "'");
				return;
			}
			ServiceLocator serviceLocator = getServiceLocator();
			RFIDServiceLocalInterface rfidService = serviceLocator.getLocalRFIDService();
			AbstractRFIDBean siloSelection = rfidService.initSiloSelection(index, shipTo);
			OrderRFIDPopupForm.initForm(session, siloSelection);
		}
		catch (IndexOutOfBoundsException indexOutOfBOundException) {
			LOG.warn("Invalid silo index was provided: '" + siloIndex + "'");
		}
		catch (NumberFormatException numberFormatException) {
			LOG.warn("Invalid silo index was provided: '" + siloIndex + "'");
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
	 * Called when pressing Back on the OrderRFIDPopupForm. Resets the rfidForm.
	 * @param request
	 */
	public void closeRfidPopup(HttpServletRequest request) {
		Authorization.authorize(Authorization.MASTER_DATA_SHIPTO_CARD_R);
		HttpSession session = request.getSession();
		AbstractRFIDBean bean = (AbstractRFIDBean) session.getAttribute(RFIDBean.SESSION_KEY);
		RfidobjectEntity selectedRfidObject = bean.getRfidObject();
		ShiptoSiloEntity selectedSilo = (ShiptoSiloEntity) bean.getContextObject();
		selectedSilo.setRfidobject(selectedRfidObject);
		OrderRFIDPopupForm.initForm(session, null);
	}

	/**
	 * Called when RFID popup is opened to fill RFID drop down 
	 */
	@AdnTrace
	public ListRange fetchRfidList(int start, int count, String query, HttpSession session) {
		Authorization.authorize(Authorization.PUBLIC_FUNCTION);
		ServiceLocator serviceLocator = getServiceLocator();
		final RFIDServiceLocalInterface rfidService = serviceLocator.getLocalRFIDService();
		RFIDDataCache rfidDataCache = RFIDPopupForm.getRFIDDataCache(session);
		return rfidService.findAssignableRfids(start, count, query, rfidDataCache);
	}
	
	
	/**
	 * Method called by the "copy" icon in Order Taking (using an existing order as a template for a new order)
	 * 
	 * @param orderItemId
	 * @return
	 */
	public Object[] getExistingOrder(Long orderItemId, HttpServletRequest request) {
		// [[
		Object[] result = new Object[2];
		Object[][] params = {{"orderItemId", orderItemId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getExistingOrder", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.ORDER);
			OrderCreateEntity oc = getBestellungService().getExistingOrder(orderItemId);
			result[0] = oc;
			/**
			 * This is an implicit validation if the contract item+material is still valid. If it is present among the loaded possible delivery settings
			 * then it is valid. In this case OK to clone. Otherwise send back -1 as composite identifier and refuse cloning.
			 */
			DeliverySettingDTO sketchDeliverySetting = validateDeliverySettingForCloneSetupEditor(request, oc.getSapContractItemId(), oc.getSapMaterialId());
			if (sketchDeliverySetting != null ) {
				result[1] = sketchDeliverySetting.getCompositeIdentifier();
			}
			else {
				result[1] = -1L;
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

	/**
	 * Returns a delivery setting in case the sapContractItemId and sapMaterialId combination is still 
	 * valid for any of the sales areas, returns null in case it is not present anymore. 
	 * @param request
	 * @param sapContractItemId the contract item which is supposed to be the base of the order item
	 * @param sapMaterialId the sap material which is supposed to be available in context of the sap contract item
	 * @return
	 */
	@Deprecated
	private DeliverySettingDTO validateDeliverySettingForCloneSetupEditor(HttpServletRequest request,
			Long sapContractItemId, Long sapMaterialId) {
		// [[
		DeliverySettingDTO result = null;
		Object[][] params = {{"request", request},
							{"sapContractItemId", sapContractItemId},
							{"sapMaterialId", sapMaterialId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validateDeliverySettingForClone", params);
		}
		// ]]
		try {
			// TODO (emoke Oct 10, 2012):Cloning is disabled since a while, check and remove if this is so
			BestellungForm orderForm = BestellungForm.getSessionInstance(request);
			final SapMaterialEntity sapMaterial = getServiceLocator().getDeliverySettingService().getSapMaterialById(sapMaterialId);
			final SapContractItemEntity sapContractItem = getServiceLocator().getDeliverySettingService().getSapContractItemById(sapContractItemId);
			final ShiptoEntity shipTo = orderForm.getShipTo();
			final ShiptoSareaEntity shiptoSarea = getBestellungService().getShiptoSarea(shipTo.getShiptoId(), sapContractItem.getSapContract().getSarea().getSareaId());
			
			DeliverySettingDTO sketchDeliverySetting = new DeliverySettingDTO( sapContractItem, sapMaterial, DeliverySettingDTO.RESOLUTION_NOT_RELEVANT, shiptoSarea);
			
			if ( logDebugEnabled ) {
				LOG.debug("Looking for composite identifier" + sketchDeliverySetting.getCompositeIdentifier());
			}
			DeliverySettingDTO copyDeliverySetting = orderForm.getShiptoMap().getDeliverySettingByCompositeIdentifier(sketchDeliverySetting.getCompositeIdentifier());
			if ( copyDeliverySetting != null ) {
				/**
				 * Set the sales area map for this delivery setting.
				 */
				try {
					OrderTakingUtils util = new OrderTakingUtils(getServiceLocator(), request.getLocale());
					OrderTakingEditorParamsDto paramsDto = new OrderTakingEditorParamsDto();
					paramsDto.setCompositeIdentifier(sketchDeliverySetting.getCompositeIdentifier());
					util.getCollectedEditorData(paramsDto, request, request.getSession());
				}
				catch (Exception e) {
					LOG.error(ExceptionUtils.getMessage(e));
					return null;
				}
			}
			return result = sketchDeliverySetting;
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
	 * Method called when adding single-delivery orders to a customer order.
	 * 
	 * @param custorderItemId
	 * @return
	 */
	public Object[] getExistingCustorder(Long custorderItemId, HttpServletRequest request) {
		// [[
		Object[] result = new Object[2];
		Object[][] params = {{"custorderItemId", custorderItemId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getExistingCustorder", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.ORDER);
			final CustorderCreateEntity coc = getBestellungService().getExistingCustorder(custorderItemId);
			result[0] = coc;
			/**
			 * This is an implicit validation if the contract item+material is still valid. If it is present among the loaded possible delivery settings
			 * then it is valid. In this case OK to clone. Otherwise send back -1 as composite identifier and refuse cloning.
			 */
			DeliverySettingDTO sketchDeliverySetting = validateDeliverySettingForCloneSetupEditor(request, coc.getSapContractItemId(), coc.getSapMaterialId());
			if (sketchDeliverySetting != null ) {
				result[1] = sketchDeliverySetting.getCompositeIdentifier();
			}
			else {
				result[1] = -1L;
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
	
	/**
	 * Called by the Order Taking screen when the bill-to belonging to the selected product is defined by a schedule
	 * 
	 * @param restrId
	 * @return
	 */
	public Long findBilltoByRhythm(Date delivery, Long restrId) {
		// [[
		Long result = null;
		Object[][] params = {{"delivery", delivery},
							{"restrId", restrId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findBilltoByRhythm", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.ORDER);
			
			Calendar delCal = DateTimeUtils.getDateAsCalendar(delivery);
			return result = getDispokarteService().findBilltoByRhythm(delCal, restrId);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
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
			Authorization.authorize(Authorization.ORDER);
			
			OrderTakingUtils util = new OrderTakingUtils(getServiceLocator(), request.getLocale());
			
			return result = util.getCollectedEditorData(paramsDto, request, session);
			
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
	 * 
	 * @param parameters
	 * @param request
	 * @param session
	 * @return null if parameters are null or not valid, otherwise the split meta data
	 * @throws Exception
	 */
	@AdnTrace
	public OrderTakingEditorSplitParamsDto getCustorderSplitMetaData(
			OrderTakingEditorSplitParamsDto parameters, HttpServletRequest request,
			HttpSession session) throws Exception {
		if (parameters == null || !parameters.isValid()) {
			return null;
		}
		Authorization.authorize(Authorization.ORDER);

		ServiceLocator serviceLocator = getServiceLocator();
		BestellungService bestellungService = serviceLocator.getBestellungService();
		List<Long> sapMaterialIdentifiers = parameters.getSapMatIds();
		List<SapMaterialEntity> sapMaterials = bestellungService.getSapMaterialsById(sapMaterialIdentifiers);

		boolean isExactCustorder = BooleanUtils.isTrue(parameters.getExactCustorderQtyFlag());
		boolean isExactSdorderQty = BooleanUtils.isTrue(parameters.getExactQtyPerSdoFlag());

		BigDecimal perSingleDeliveryOrderQuantity = null;
		UomBsEntity perSingleDeliveryOrderQuantityUnitOfMeasure = null;
		List<String> orderedUnitOfMeasureCodes = parameters.getOrderedUomCds();
		RefDataGenericsService referenceDataGenericsService = serviceLocator.getRefDataGenericsService();
		Long deliveryTypeCode = parameters.getDeliveryTypeCd();
		if (parameters.getQuantityPerSdo() != null) {
			OrderTakingUomConversionDto conversionParameters = new OrderTakingUomConversionDto();
			conversionParameters.setInQuantity(parameters.getQuantityPerSdo());
			conversionParameters.setInFromUomCd(parameters.getQuantityPerSdoUomCd());
			conversionParameters.setInToUomCd(orderedUnitOfMeasureCodes.get(0));
			conversionParameters.setInSapMaterialId(sapMaterials.get(0).getSapMaterialId());
			conversionParameters.setInDeliveryType(deliveryTypeCode);
			conversionParameters.setInVehicleTypeCd(parameters.getMaxPrioTruckTypeCd());
			conversionParameters.setInAllowFuzzy(false);

			conversionParameters = convertUomWithFallbacks(conversionParameters);
	
			perSingleDeliveryOrderQuantity = conversionParameters.getOutQuantity();
			perSingleDeliveryOrderQuantityUnitOfMeasure = referenceDataGenericsService.findBsEntity(
					UomBsEntity.class, conversionParameters.getOutUom());
		}

		List<CustorderItemEntity> customerOrderItems = new ArrayList<CustorderItemEntity>();
		List<BigDecimal> orderedQuantities = parameters.getOrderedQuantities();
		for (int i = 0; i < sapMaterials.size(); i++) {
			CustorderItemEntity newCustomerOrderItem = new CustorderItemEntity();
			customerOrderItems.add(newCustomerOrderItem);

			SapMaterialEntity currentSapMaterial = sapMaterials.get(i);
			newCustomerOrderItem.setSapMaterial(currentSapMaterial);
			
			BigDecimal currentOrderedQuantity = orderedQuantities.get(i);
			String currentOrderedUnitOfMeasureCode = orderedUnitOfMeasureCodes.get(i);
			if (currentOrderedQuantity != null && currentOrderedUnitOfMeasureCode != null) {
				UomBsEntity currentOrderedUnitOfMeasure = referenceDataGenericsService.findBsEntity(
						UomBsEntity.class, currentOrderedUnitOfMeasureCode);
				
				if (currentOrderedUnitOfMeasure != null) {
					newCustomerOrderItem.setOrderedQuantity(currentOrderedQuantity);
					newCustomerOrderItem.setOrderedUom(currentOrderedUnitOfMeasure);
				}
				else {
					return null;
				}
			}

			newCustomerOrderItem.setExactCustorderQtyFlag(isExactCustorder);
			newCustomerOrderItem.setExactSdorderQtyFlag(isExactSdorderQty);
			newCustomerOrderItem.setPerSdorderQuantity(perSingleDeliveryOrderQuantity);
			newCustomerOrderItem.setPerSdorderUom(perSingleDeliveryOrderQuantityUnitOfMeasure);
		}

		CustorderEntity customerOrder = new CustorderEntity();
		customerOrder.setCustorderItems(customerOrderItems);

		Date deliveryFromDate = parameters.getDelFromDat();
		customerOrder.setDeliveryFromDat(deliveryFromDate);
		Date deliveryToDate = parameters.getDelToDat();
		customerOrder.setDeliveryToDat(deliveryToDate);

		DeliveryTypeBsEntity deliveryType = referenceDataGenericsService.findBsEntity(
				DeliveryTypeBsEntity.class, deliveryTypeCode);
		customerOrder.setDeliveryType(deliveryType);

		Double deliveryFrequency = parameters.getDeliveryFqyEveryTime();
		if (deliveryFrequency != null) {
			customerOrder.setDeliveryFreqEveryTime(deliveryFrequency);
			String deliveryFrequencyUnitOfMeasureCode = parameters.getDeliveryFqyEveryTimeUomCd();
			if (deliveryFrequencyUnitOfMeasureCode != null) {
				customerOrder.setDeliveryFreqEveryTimeUom(deliveryFrequencyUnitOfMeasureCode);
			}
			else {
				return null;
			}
		}

		Double deliveryQuantityPerHour = parameters.getDeliveryFqyQtyPerHre();
		if (deliveryQuantityPerHour != null) {
			customerOrder.setDeliveryFreqQtyperhre(new BigDecimal(deliveryQuantityPerHour));
			String deliveryFrequencyPerHourUnitOfMeasureCode = parameters.getDeliveryFqyQtyPerHreUomCd();
			if (deliveryFrequencyPerHourUnitOfMeasureCode != null) {
				UomBsEntity deliveryFrequencyPerHourUnitOfMeasure = referenceDataGenericsService.findBsEntity(
						UomBsEntity.class, deliveryFrequencyPerHourUnitOfMeasureCode);
				if (deliveryFrequencyPerHourUnitOfMeasure != null) {
					customerOrder.setDeliveryFreqQtyperhreUom(deliveryFrequencyPerHourUnitOfMeasure);
				}
				else {
					return null;
				}
			}
			else {
				return null;
			}
		}

		Double deliveryTimeTolerance = parameters.getDeliveryTimeTolerance();
		if (deliveryTimeTolerance != null) {
			customerOrder.setDeliveryTimeTolerance(deliveryTimeTolerance);
			String deliveryTimeToleranceUnitOfMeasureCode = parameters.getDeliveryTimeToleranceUomCd();
			if (deliveryTimeToleranceUnitOfMeasureCode != null) {
				customerOrder.setDeliveryTimeToleranceUom(deliveryTimeToleranceUnitOfMeasureCode);
			}
			else {
				return null;
			}
		}

		final TruckWagonTypeWrapper truckWagonTypeWrapper;
		if (LogonUtil.isTruckOrder(deliveryTypeCode) || LogonUtil.isPickupOrder(deliveryTypeCode)) {
			TruckTypeBsEntity truckTypeBs = referenceDataGenericsService.findBsEntity(
					TruckTypeBsEntity.class, parameters.getMaxPrioTruckTypeCd());
			if (truckTypeBs == null) {
				return null;
			}
			truckWagonTypeWrapper = new TruckTypeWrapper(1L, truckTypeBs, null);
		}
		else if (LogonUtil.isRailOrder(deliveryTypeCode)) {
			WagonTypeTxEntity wagonTypeTx = referenceDataGenericsService.findTxEntity(
					WagonTypeTxEntity.class, request.getLocale(),
					parameters.getMaxPrioTruckTypeCd());
			if (wagonTypeTx == null) {
				return null;
			}
			truckWagonTypeWrapper = new WagonTypeWrapper(1L, wagonTypeTx, null);
		}
		else if (LogonUtil.isSimpleVehicleOrder(deliveryTypeCode)) {
			SimpleVehicleTypeTxEntity simpleVehicleTypeTx = referenceDataGenericsService.findTxEntity(
					SimpleVehicleTypeTxEntity.class, request.getLocale(),
					parameters.getMaxPrioTruckTypeCd());
			if (simpleVehicleTypeTx == null) {
				return null;
			}
			truckWagonTypeWrapper = new SimpleVehicleTypeWrapper(simpleVehicleTypeTx);
		}
		else {
			return null;
		}

		// get split metadata (no real split)
		CustorderSplitWrapper splitWrapper = null;
		try {
			CustorderService customerOrderService = serviceLocator.getCustorderService();
			splitWrapper = customerOrderService.createCustorderSplitWrapper(
					customerOrder, truckWagonTypeWrapper);
			CustorderUtils.processAndValidateCustorderSplitWrapper(splitWrapper);
		}
		catch (ValidationException validationException) {
			if (validationException.containsError(CustorderError.MAX_NUMBER_OF_ORDERS)) {
				LOG.debug("too many orders would be generated", validationException); // we can continue in this case to inform user
			}
			else {
				LOG.debug(CustorderSplitWrapper.SPLIT_FAILED_GENERAL_DEBUG_MESSAGE,
						validationException); // only debug level, due to it is not a real split
				return null;
			}
		}
		catch (Exception exception) {
			LOG.debug(CustorderSplitWrapper.SPLIT_FAILED_GENERAL_DEBUG_MESSAGE, exception);
			return null;
		}
		boolean isConversionFailed = splitWrapper != null && splitWrapper.isConversionFailed();
		if (isConversionFailed) {
			throw new LogonException(Reason.RES_ERROR_CANNOT_CONVERT_UOM);
		}

		// collect output parameters after split
		OrderTakingEditorSplitParamsDto result = new OrderTakingEditorSplitParamsDto();

		CustorderItemEntity firstCustomerOrderItem = splitWrapper.getCustorder().getCustorderItems().get(0);

		SapMaterialEntity sapMaterialOfFirstCustomerOrderItem = firstCustomerOrderItem.getSapMaterial();
		if (LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(sapMaterialOfFirstCustomerOrderItem)) {
			// updated Ordered Quantity and UoM
			result.setOrderedQuantities(orderedQuantities);
			result.getOrderedQuantities().set(0, splitWrapper.get_remainingQtyInOrderedUom());
			result.setOrderedUomCds(orderedUnitOfMeasureCodes);
			result.getOrderedUomCds().set(0, splitWrapper.get_orderedUom().getUomCd());

			// updated Delivery To Date
			result.setDelToDat(splitWrapper.get_deliveryToDate());
		}

		// per single delivery quantity and UoM
		result.setQuantityPerSdo(perSingleDeliveryOrderQuantity);
		result.setQuantityPerSdoUomCd(perSingleDeliveryOrderQuantityUnitOfMeasure != null ? perSingleDeliveryOrderQuantityUnitOfMeasure.getUomCd()
				: Constant.UOM_TONS);
		OrderTakingUtils orderTakingUtils = new OrderTakingUtils(serviceLocator,
				request.getLocale());
		String quantityPerSdoStr = orderTakingUtils.getQuantityPerSdoLabel(result.getQuantityPerSdoUomCd());
		result.setQuantityPerSdoStr(quantityPerSdoStr);

		// Number of Deliveries
		result.setNumDeliveries(splitWrapper.get_numOfDeliveries());
		result.setNumDeliveriesStr(splitWrapper.get_numOfDeliveriesStr());
		result.setMaxNumOfOrders(splitWrapper.getNumOfMaxOrders());

		return result;
	}
	
	
	/**
	 * Used by UC4012 Order Taking, and Dispokarte when the ship-to coordinates are initially null 
	 * 
	 * @param coordLat
	 * @param coordLng
	 * @param session
	 */
	public void setCoordinatesForShipto(Double coordLat, Double coordLng, String source, HttpSession session) {
		// [[
		Object[][] params = {{"coordLat", coordLat},
							{"coordLng", coordLng},
							{"source", source},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setCoordinatesForShipto", params);
		}
		// ]]
		try {
			if (source.equals(CALLER_ORDER_CREATE)) {				
				Authorization.authorize(Authorization.ORDER);
				BestellungForm orderForm = OrderTakingUtils.getBestellungForm(session);
				ShiptoEntity shipto = orderForm.getShipTo();
				setCoordinatesForShipto(coordLat, coordLng, shipto);
				DispokarteService dispoService = getServiceLocator().getDispokarteService();
				ShiptoEntity mergedShipto = dispoService.mergeShipto(shipto, null);
				orderForm.setShipTo(mergedShipto,
									getServiceLocator().getTimeZoneService().loadForShipTo(
											shipto,
											UserProvider.getLocale()));
			}
			else if (source.equals(CALLER_DISPOKARTE)) {
				//do not merge immediately it will be merged as soon as the user save on dispokarte
				Authorization.authorize(Authorization.MASTER_DATA_SHIPTO_CARD_W);
				DispokarteForm dispoForm = getDispokarteForm(session);
				ShiptoEntity shipto = dispoForm.getShipTo();
				setCoordinatesForShipto(coordLat, coordLng, shipto);
			}
			else{
				throw new IllegalArgumentException("Invalid caller");
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
	 * @param coordLat
	 * @param coordLng
	 * @param shipto
	 * @return
	 */
	private void setCoordinatesForShipto(Double coordLat, Double coordLng,
			ShiptoEntity shipto) {
		// [[
		Object[][] params = {{"coordLat", coordLat},
							{"coordLng", coordLng},
							{"shipto", shipto}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setCoordinatesForShipto", params);
		}
		// ]]
		try {
			if (coordLat == null || coordLng == null || coordLat == 0 || coordLng == 0) {
				throw new IllegalArgumentException("Invalid coordinates");
			}
			
			shipto.setCoordLat(coordLat);
			shipto.setCoordLon(coordLng);
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	public int cancelBimodalOrderPartEditor(HttpServletRequest request, HttpSession session)
	throws Exception {
		// [[
		int result = 0;
		Object[][] params = {{"request", request},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "cancelBimodalOrderPartEditor", params);
		}
		// ]]
		try {
			BestellungForm form = OrderTakingUtils.getBestellungForm(session);
			OrderTakingEditorDto editor = form.getEditorList().remove(1);
			if (!LogonUtil.isBimodalRailPartOrder(editor.getOrderHelper().getDeliveryTypeCd())) {
				throw new IllegalStateException("Illegal state: editorList[1] is not a bimodal rail part editor");
			}
			return result = 1;
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
     * Removing a product from the product list
     * 
     * @param request
     * @return
     * @throws Exception
     */
    public int removeOrderItem(int itemIdx, int selectedEditorIdx, HttpServletRequest request) throws Exception {
		// [[
		int result = 0;
		Object[][] params = {{"itemIdx", itemIdx + ""},
							{"selectedEditorIdx", selectedEditorIdx + ""},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "removeOrderItem", params);
		}
		// ]]
		try {
			
			HttpSession session = request.getSession();
			
			BestellungForm form = OrderTakingUtils.getBestellungForm(session);
			if (itemIdx > 0) {
				// --- Instead of removing, leave 'holes' in the list so re-indexing the client side component can be avoided --- 
				List<OrderTakingEditorDto> editorList = form.getEditorList();
				OrderTakingEditorDto orderTakingEditorDto = editorList.get(selectedEditorIdx);
				List<OrderTakingEditorOrderItemDto> orderItems = orderTakingEditorDto.getOrderItems();
				orderItems.set(itemIdx, null);
			}
			
			return result = 1;
			
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
     * Removing an {@code OrderTakingEditorDTO} product from editor list
     * @param request 
     */
    public void removeEditor(int editorIndex, HttpServletRequest request) {
		// [[
		Object[][] params = {{"editorIndex", editorIndex + ""},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "removeEditor", params);
		}
		// ]]
		try {
			
			HttpSession session = request.getSession();
			
			BestellungForm form = OrderTakingUtils.getBestellungForm(session);
			
			List<OrderTakingEditorDto> editorList = form.getEditorList();
			editorList.remove(editorIndex);
			
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
     * Synchronize the data between the GUI state and the model.
     * @param splittedOrdersPerEditors
     * @param session
     */
	@AdnTrace
	public void sendSplittedOrderData(List<List<SplittedOrderData>> splittedOrdersPerEditors, HttpSession session) {
		Authorization.authorize(Authorization.ORDER);
		BestellungForm form = OrderTakingUtils.getBestellungForm(session);
		List<OrderTakingEditorDto> editorList = form.getEditorList();

		for (int editorIndex = 0; editorIndex < splittedOrdersPerEditors.size(); editorIndex++) {

			OrderTakingEditorDto editor = editorList.get(editorIndex);
			List<SplittedOrderData> ordersListFromCustOrders = editor.getOrdersListFromCustOrders();
			
			List<SplittedOrderData> splittedOrders = splittedOrdersPerEditors.get(editorIndex);
			if (splittedOrders != null) {
				for (SplittedOrderData srcSplittedOrderData : splittedOrders) {
					if (ordersListFromCustOrders != null) {
						OrderSpiBsEntity serviceProcessIndicator = getServiceLocator().getRefDataGenericsService().findBsEntity(
								OrderSpiBsEntity.class,
								srcSplittedOrderData.getServiceProcessIndicatorId());

						int index = srcSplittedOrderData.getIndex();
						SplittedOrderData dstSplittedOrderData = ordersListFromCustOrders.get(index);
						dstSplittedOrderData.updateFrom(srcSplittedOrderData,
								serviceProcessIndicator);
					}
				}
			}

		}
	}

	public SdoOnetimeDataEntity getOneTimeDataForOrder(Long orderId) {
		// [[
		SdoOnetimeDataEntity result = null;
		Object[][] params = {{"orderId", orderId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getOneTimeDataForOrder", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.ORDER);
			return result = getBestellungService().getOneTimeDataForOrder(orderId);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public CustordOnetimeDataEntity getOneTimeDataForCustorder(Long custorderId) {
		// [[
		CustordOnetimeDataEntity result = null;
		Object[][] params = {{"custorderId", custorderId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getOneTimeDataForCustorder", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.ORDER);
			return result = getBestellungService().getOneTimeDataForCustorder(custorderId);
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
	 * Loads all visible orders for shipping screen's grid.
	 * @param start
	 * @param count
	 * @param orderBy
	 * @param request
	 * @return
	 */
	public ListRange loadShippingOrders(int start, int count, String orderBy, HttpServletRequest request) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadShippingOrders", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.SHIPPING_R);
			
			HttpSession session = request.getSession();
			
			AuftragBearbeitungForm helpForm = (AuftragBearbeitungForm) session.getAttribute(AuftragBearbeitungForm.FORM_NAME);
			
			// TODO (orsika Sep 25, 2013): LOGON-7221:
			//if date validation returns false, currently no warning message displayed (messageWarning is not processed)
//			boolean validDates = helpForm.validateDateSetting(request);
			
			String selectedTransportCat = helpForm.getSelectedTransportCat();
			String selectedShippingPointId = helpForm.getSelectedShippingPointId();
			
			String transportCat = StringUtils.isEmpty(selectedTransportCat) ? null : selectedTransportCat;
			Long shippingPointId = StringUtils.isEmpty(selectedShippingPointId) ? null : Long.valueOf(selectedShippingPointId);
			
			long[] selectedStatus = helpForm.getSelectedStatus();
			List<Long> selectedStatusList = Arrays.asList(ArrayUtils.toObject(selectedStatus));
			
			boolean sortAscending = false;
			String orderByColumn = "";
			
			if (!StringUtils.isEmpty(orderBy)) {
				String[] sortArr = orderBy.split(" ");
				orderByColumn = sortArr[0];
				if ("ASC".equals(sortArr[1])) {
					sortAscending = true;
				}
			}
			
			String languageCd = request.getLocale().getLanguage();
			
			setAssignmentIdList(helpForm, languageCd, shippingPointId, transportCat);
			List<Long> assignmentIdList = helpForm.getAssignmentIdList();
			
			String selectedPlantCd = helpForm.getSelectedPlantCd();
			
			Date fromDate = helpForm.getFromDate();
			Date toDate = helpForm.getToDate();
			String searchText = helpForm.getSearchtext();
			String searchOrderNo = helpForm.getSearchOrderNo();
			
			Locale userLocale = getUserLocale();
			TimeZone userTimeZone = getUserTimeZone();
			
			Map<Long, List<LdpointMatRestrEntity>> orderLoadingPointListMapping = helpForm.getOrderLoadingPointListMapping();
			Map<Long, ShippBimodalRoadRailEntity> bimodalRoadAndRelatedRailInfo = helpForm.getBimodalRoadAndRelatedRailInfoMap();
			
			OrderChangeService orderChangeService = getServiceLocator().getOrderChangeService();

			LoadShippingOrdersBean loadShippingOrdersBean = orderChangeService.loadShippingOrders(
					start, count, transportCat, shippingPointId, selectedStatusList, 
					sortAscending, orderByColumn, languageCd, selectedPlantCd, fromDate, toDate, 
					searchText, assignmentIdList, searchOrderNo, userLocale, userTimeZone,
					orderLoadingPointListMapping, bimodalRoadAndRelatedRailInfo);
			
			ShippingOrderFetchListBean shippingOrderFetchListBean = loadShippingOrdersBean.getShippingOrderFetchListBean();
			List<OrdItemAssignmtLoadEntity> orderItemAssignmentLoadList = 
					shippingOrderFetchListBean.getOrderItemAssignmentLoadList();
			List<ShippingOrderLoadBean> shipppingOrderLoadBeans = loadShippingOrdersBean.getShipppingOrderLoadBeans();
			Map<Long, Date> plannedLoadByOrderId = loadShippingOrdersBean.getPlannedLoadByOrderId();
			
			helpForm.setOrderList(orderItemAssignmentLoadList, plannedLoadByOrderId);
			helpForm.setOrderLoadingPointListMapping(new HashMap<Long, List<LdpointMatRestrEntity>>());
			
			Map<Long, Date> truckNextOrderMapping = loadShippingOrdersBean.getTruckNextOrderMapping();
			helpForm.setTruckNextOrderMapping(truckNextOrderMapping);
			
			result = new ListRange();
			result.setData(shipppingOrderLoadBeans.toArray());
			result.setTotalSize(shippingOrderFetchListBean.getTotalCount());
			
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
	
	private void setAssignmentIdList(AuftragBearbeitungForm helpForm, String languageCd, 
			Long shippingPointId, String transportCat) {
		// [[
		Object[][] params = {{"helpForm", helpForm},
							{"languageCd", languageCd},
							{"shippingPointId", shippingPointId},
							{"transportCat", transportCat}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setAssignmentIdList", params);
		}
		// ]]
		try {
			OrderChangeService orderChangeService = getServiceLocator().getOrderChangeService();
			long[] selStatus = helpForm.getSelStatus();
			List<Long> statusIdList = Arrays.asList(ArrayUtils.toObject(selStatus));
			Date fromDate = helpForm.getFromDate();
			Date toDate = helpForm.getToDate();
			String generalFilter = helpForm.getSearchtext();
			String searchOrderNo = helpForm.getSearchOrderNo();
			String plantCd = helpForm.getSelectedPlantCd();
			
			@SuppressWarnings("rawtypes")
			final List<Long> involvedAssignmentList = orderChangeService.getInvolvedAssignmentIdList(
					plantCd, shippingPointId, statusIdList, fromDate, toDate, 
					generalFilter, languageCd, transportCat, searchOrderNo, AuftragBearbeitungAction.maxRowNumber);
			helpForm.setAssignmentIdList(involvedAssignmentList);
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
	 * Collects the editability rules for the selected order-load on shipping screen within a serialized bean.
	 * @param compositeId
	 * @param checkBimodal flag highlighting to check for Bimodal loading case as well.
	 * @param request
	 * @param session
	 * @return
	 */
	public ShippingOrderLoadBean getShippingOrderLoadBean(String compositeId, boolean checkBimodal, HttpServletRequest request, HttpSession session) {
		// [[
		ShippingOrderLoadBean result = null;
		Object[][] params = {{"compositeId", compositeId},
							{"checkBimodal", checkBimodal + ""},
							{"request", request},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getShippingOrderLoadBean", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.SHIPPING_R);
			final boolean INITIALIZE_FOR_EDIT = true;
			
			Locale locale = request.getLocale();
			
			AuftragBearbeitungForm helpForm = (AuftragBearbeitungForm) session.getAttribute(AuftragBearbeitungForm.FORM_NAME);
			
			Map<Long, List<LdpointMatRestrEntity>> orderLoadingPointListMapping = helpForm.getOrderLoadingPointListMapping();
			Map<Long, ShippBimodalRoadRailEntity> bimodalRoadAndRelatedRailInfo = helpForm.getBimodalRoadAndRelatedRailInfoMap();
			
			OrderChangeService orderChangeService = getServiceLocator().getOrderChangeService();
			
			OrdItemAssignmtLoadEntity ordItemAssignmtLoad = orderChangeService.
					fetchOrdItemAssignmtLoadEntity(compositeId, locale.getLanguage());
			
			List<String> fuzzyUomCds = orderChangeService.fetchFuzzyUomCds();
			
			BestellungService bestellungService = getBestellungService();
			Long sapMaterialId = ordItemAssignmtLoad.getSapMaterialId();
			SapMaterialEntity sapMaterial = bestellungService.getSapMaterialById(sapMaterialId);
			List<SapMaterialAltUomEntity> sapMaterialAltUomEntities = orderChangeService.getAlternativeUoms(sapMaterial, fuzzyUomCds);
			
			Map<Long, Date> truckNextMinPlannedStartMap = null;
			
			ShippingOrderLoadBean bean = orderChangeService.getShippingOrderLoadBean(
					ordItemAssignmtLoad, checkBimodal, locale, 
					orderLoadingPointListMapping, bimodalRoadAndRelatedRailInfo, 
					truckNextMinPlannedStartMap, sapMaterial, sapMaterialAltUomEntities, INITIALIZE_FOR_EDIT);
			
			truckNextMinPlannedStartMap = bean.getTruckNextMinPlannedStartMap();
			helpForm.setTruckNextOrderMapping(truckNextMinPlannedStartMap);
			
			Map<Long, ShippBimodalRoadRailEntity> bimodalRoadAndRelatedRailInfoMap = 
					bean.getBimodalRoadAndRelatedRailInfoMap();
			helpForm.setBimodalRoadAndRelatedRailInfoMap(bimodalRoadAndRelatedRailInfoMap);
			
			return result = bean;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public void applyOpeningTimes(int editorIndex, String opening1FromTime, String opening1ToTime, String opening2FromTime, String opening2ToTime, HttpSession session) {
		// [[
		Object[][] params = {  { "session", session } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "applyOpeningTimes", params);
		}
		// ]]
		try {
			
			Authorization.authorize(Authorization.ORDER);
			BestellungForm form = OrderTakingUtils.getBestellungForm(session);
			List<OrderTakingEditorDto> editorList = form.getEditorList();
			OrderTakingEditorDto editor = editorList.get(editorIndex);
			CustorderSplitWrapper wrapper = editor.getWrapper();
			
			CustorderEntity custorder = wrapper.getCustorder();
			
			ShiptoEntity shiptoEntity = form.getShipTo();
			
			// Save selected opening times
			editor.setOpen1From(opening1FromTime);
			editor.setOpen1To(opening1ToTime);
			editor.setOpen2From(opening2FromTime);
			editor.setOpen2To(opening2ToTime);
			
			long[] open1From = AdjustTimesUtils.parseOpeningWindow(opening1FromTime);
			long[] open1To = AdjustTimesUtils.parseOpeningWindow(opening1ToTime);
			long[] open2From = AdjustTimesUtils.parseOpeningWindow(opening2FromTime);
			long[] open2To = AdjustTimesUtils.parseOpeningWindow(opening2ToTime);
						
			ShiptToOpening opening = new ShiptToOpening(open1From[0], open1To[0], open2From[0], open2To[0], open1From[1], open1To[1], open2From[1], open2To[1]);

			List<SplittedOrderData> splittedOrderDataList;
			
			// Quantity is important because the time is calculated
			if (Constant.PERFORMANCE_ORDER_CALCULATE_DELIVERYTO.equals(editor.getPerformanceOrderCalculationMode())) {
				splittedOrderDataList = AdjustTimesUtils.adjustTimesQuantity(opening, custorder, shiptoEntity);
			}
			// Delivery time is important because the quantity is calculated
			else if (Constant.PERFORMANCE_ORDER_CALCULATE_QUANTITY.equals(editor.getPerformanceOrderCalculationMode())) {
				splittedOrderDataList = AdjustTimesUtils.adjustTimesDeliveryTo(opening, custorder, shiptoEntity, wrapper);
			}
			else {
				splittedOrderDataList = AdjustTimesUtils.adjustTimesElse(opening, custorder, shiptoEntity);
			}

			Set<Long> sapMaterialIds = new HashSet<Long>();
			
			// Iterating over the SplittedOrderData and create SplittedOrderItemData per OrderItemEntity  
			for (SplittedOrderData splittedOrderData : splittedOrderDataList) {
				OrderEntity order = splittedOrderData.getOrder();
				List<SplittedOrderItemData> splittedOrderItemDatas = new ArrayList<SplittedOrderItemData>();
				List<OrderItemEntity> orderItems = order.getOrderItems();
				
				int itemIndex = 0;
				for (OrderItemEntity orderItemEntity : orderItems) {
					MaterialType materialType = null;
					if (LogonPackageHandlingUtil.isBag(orderItemEntity)) {
						materialType = MaterialType.BAG;
					}
					else if (LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(orderItemEntity)) {
						materialType = MaterialType.BULK;
					}
					else if (LogonPackageHandlingUtil.isService(orderItemEntity)) {
						materialType = MaterialType.SERVICE;
					}
					splittedOrderItemDatas.add(new SplittedOrderItemData(orderItemEntity, itemIndex++, materialType));
					
					// Collect the sap material ids for later calculation on quantities
					sapMaterialIds.add(orderItemEntity.getSapMaterial().getSapMaterialId());
				}
				splittedOrderData.setSplittedOrderItemDatas(splittedOrderItemDatas);
			}
			
			editor.setOrdersListFromCustOrders(splittedOrderDataList);
			
			for (Long sapMaterialId : sapMaterialIds) {
				sumOrderedQtyInBase(editorIndex, splittedOrderDataList, sapMaterialId, session);
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
	 * Calculates and returns the sum quantity for the given material in base UOM.
	 * Please note it <b> will also save on the editors material column </b>!
	 * @param editorIndex - selected editor's id
	 * @param orderDatas - records containing the quantities
	 * @param sapMaterialId - the id of the selected column 
	 */
	public BigDecimal sumOrderedQtyInBase(int editorIndex, List<SplittedOrderData> orderDatas, Long sapMaterialId, HttpSession session) {
		// [[
		BigDecimal result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sumOrderedQtyInBase");
		}
		// ]]
		try {
			
			
			Authorization.authorize(Authorization.ORDER);
			BestellungForm form = OrderTakingUtils.getBestellungForm(session);
			List<OrderTakingEditorDto> editorList = form.getEditorList();
			OrderTakingEditorDto editor = editorList.get(editorIndex);
			CustorderSplitWrapper wrapper = editor.getWrapper();
			
			BigDecimal sumInBase = new BigDecimal("0");
			
			for (SplittedOrderData splittedOrderData : orderDatas) {
				List<SplittedOrderItemData> splittedOrderItemDatas = splittedOrderData.getSplittedOrderItemDatas();
				for (SplittedOrderItemData splittedOrderItemData : splittedOrderItemDatas) {
					if (splittedOrderItemData.getSapMaterialId().equals(sapMaterialId)) {
						
						List<SplittedOrderData> formSplittedOrders = editor.getOrdersListFromCustOrders();
						SplittedOrderData formsplittedOrder = formSplittedOrders.get(splittedOrderData.getIndex());
						List<SplittedOrderItemData> formSplittedOrderItems = formsplittedOrder.getSplittedOrderItemDatas();
						SplittedOrderItemData formSplittedOrderItem = formSplittedOrderItems.get(splittedOrderItemData.getIndex());
						
						formSplittedOrderItem.updateFrom(splittedOrderItemData);
						
						BigDecimal baseUomOrderedQty = CustorderUtils.getBaseUomOrderedQty(wrapper, formSplittedOrderItem.getOrderItem());
						sumInBase = sumInBase.add(baseUomOrderedQty);
					}
				}
			}
			
			// We save the calculated quantity on the column
			Set<MaterialColumn> materialColumns = editor.getMaterialColumns();
			for (MaterialColumn materialColumn : materialColumns) {
				if (materialColumn.getMaterial().getSapMaterialId().equals(sapMaterialId)) {
					materialColumn.setOrderedQuantity(sumInBase);
				}
			}
			
			return result = sumInBase.stripTrailingZeros();
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
	 * Deletes an order from the splitted order list of the form based on the passed index
	 * Also re-indexes the entries because of the 'collapse' effect that occurs when removing
	 * an indexed entry from the list. This originally caused problem when removing elements
	 * then trying to use their indexes as keys.
	 * @param editorIndex - selected editor from which the element will be deleted
	 * @param index - designated order index
	 * @param session
	 */
	public void deleteSplittedOrderData(int editorIndex, int index, HttpSession session) {
		// [[
		Object[][] params = {{"editorIndex", editorIndex + ""},
							{"index", index + ""},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "deleteSplittedOrderData", params);
		}
		// ]]
		try {
			
			Authorization.authorize(Authorization.ORDER);
			BestellungForm form = OrderTakingUtils.getBestellungForm(session);
			List<OrderTakingEditorDto> editorList = form.getEditorList();
			OrderTakingEditorDto editor = editorList.get(editorIndex);
			
			CustorderSplitWrapper wrapper = editor.getWrapper();			
			CustorderEntity custorder = wrapper.getCustorder();			
			List<OrderEntity> orders = custorder.getOrders();
			
			// Need to re-index the entries because delete causes a 'collapse'
			int newIndex = 0;
			
			// Delete splitted order elements
			List<SplittedOrderData> ordersListFromCustOrders = editor.getOrdersListFromCustOrders();
			Iterator<SplittedOrderData> iterator = ordersListFromCustOrders.iterator();
			while (iterator.hasNext()) {
				SplittedOrderData next = iterator.next();
				if (next.getIndex() == index) {
			
					List<SplittedOrderItemData> splittedOrderItemDatas = next.getSplittedOrderItemDatas();
					for (SplittedOrderItemData splittedOrderItemData : splittedOrderItemDatas) {
						Long sapMaterialId = splittedOrderItemData.getSapMaterialId();
						Set<MaterialColumn> materialColumns = editor.getMaterialColumns();
						for (MaterialColumn materialColumn : materialColumns) {
							if (sapMaterialId.equals(materialColumn.getMaterial().getSapMaterialId())) {
								BigDecimal oldQty = materialColumn.getOrderedQuantity();
								BigDecimal baseUomOrderedQty = CustorderUtils.getBaseUomOrderedQty(wrapper, splittedOrderItemData.getOrderItem());
								BigDecimal newQty = oldQty.subtract(baseUomOrderedQty);
								materialColumn.setOrderedQuantity(newQty.stripTrailingZeros());
							}
						}
					}
			
					// Delete SDO
					orders.remove(index);
					// Delete SplittedOrderData
					iterator.remove();
					
				}
				else {
					// New index because of 'collapse' effect
					next.setIndex(newIndex++);
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
	 * Adds a {@code SplittedOrderData} element to the selected editor
	 * @param editorIndex - selected editor
	 * @param request
	 */
	public void addSplittedOrderData(int editorIndex, HttpServletRequest request) {
		// [[
		Object[][] params = {{"editorIndex", editorIndex + ""},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "addSplittedOrderData", params);
		}
		// ]]
		try {
			
			Authorization.authorize(Authorization.ORDER);
			HttpSession session = request.getSession();
			BestellungForm form = OrderTakingUtils.getBestellungForm(session);
			List<OrderTakingEditorDto> editorList = form.getEditorList();
			OrderTakingEditorDto editor = editorList.get(editorIndex);
			
			CustorderSplitWrapper wrapper = editor.getWrapper();
			
			CustorderEntity custorder = wrapper.getCustorder();
			
			List<OrderEntity> orders = custorder.getOrders();
			
			// If there are no existing SDOs we can't template
			if (orders == null || orders.isEmpty()) {
				MessageUtils.addActionError(request, "errors.general");
				throw new RuntimeException("Cannot template new SDO as there are no existing SDO in CO!");
			}
			
			OrderEntity lastOrderAsTemplate = orders.get(orders.size() - 1);
			
			// Delete splitted order elements
			List<SplittedOrderData> ordersListFromCustOrders = editor.getOrdersListFromCustOrders();
			
			// Create SDO
			OrderEntity newOrder = new OrderEntity();
			newOrder.setArchivedFlag(false);
			newOrder.setArchivingStatus(getArchivingStatusBsEntity(Constant.ARCHIVING_STATUS_NEW));
			BeanUtilsBean beanUtilsBean = new BeanUtilsBuilder().
					addConverter(new BigDecimalConverter()).
					addConverter(new LongConverter()).
					addConverter(new BooleanConverter()).
					addConverter(new DateConverter()).
					addConverter(new Base64ByteArrayConverter()).
					addConverter(new StringConverter()).
					createBean();
			
			try {
				// List variables cannot be copied!
				beanUtilsBean.copyProperties(newOrder, lastOrderAsTemplate);
			
				// Need to point order vehicle types to new order
				List<OrderTruckTypeEntity> orderVehicleTypes = createNewOrderTruckTypesForOrder(beanUtilsBean, newOrder);
				newOrder.setOrderVehicleTypes(orderVehicleTypes);
				
				List<OrderItemEntity> orderItems = lastOrderAsTemplate.getOrderItems();
				List<OrderItemEntity> newOrderItems = new ArrayList<OrderItemEntity>();
				for (OrderItemEntity orderItemEntity : orderItems) {
					OrderItemEntity newOrderItemEntity = new OrderItemEntity();
					beanUtilsBean.copyProperties(newOrderItemEntity, orderItemEntity);
					newOrderItemEntity.setOrderedQuantity(BigDecimal.ZERO);
					newOrderItems.add(newOrderItemEntity);
				}
				newOrder.setOrderItems(newOrderItems);
			}
			catch (Exception e) {
				MessageUtils.addActionError(request, e);
				return;
			}
			
			orders.add(newOrder);
			
			// SplittedOrderData attributes
			ShiptoEntity shipTo = form.getShipTo();
			SplittedOrderData.DeliveryType deliveryType = LogonUtil.getDeliveryType(newOrder);
			Boolean exactCustorderQtyFlag = CustorderUtils.getExactCustorderQty(custorder);
			int index = ordersListFromCustOrders.size();
			
			TimeZone shiptoTimeZone = form.getShipToTimeZone();
			SplittedOrderData splittedOrderData = new SplittedOrderData(newOrder, shipTo,
					deliveryType, exactCustorderQtyFlag, index, shiptoTimeZone);
			ordersListFromCustOrders.add(splittedOrderData);
			
			List<OrderItemEntity> orderItems = newOrder.getOrderItems();
			// Enrich list
			CustorderUtils.addMissingOrderItemsBasedOnCOI(wrapper, newOrder, orderItems, custorder.getCustorderItems());
			
			List<SplittedOrderItemData> splittedOrderItemDatas = CustorderUtils.populateSplittedOrdetItems(orderItems);
			splittedOrderData.setSplittedOrderItemDatas(splittedOrderItemDatas);			
			
			Map<Long, BigDecimal> qtyPerMaterial = new HashMap<Long, BigDecimal>();
			CustorderUtils.calculateQtyPerMaterial(wrapper, qtyPerMaterial, splittedOrderItemDatas);
			
			// Add the new order items qtys on top of the existing quantities
			Set<MaterialColumn> materialColumns = editor.getMaterialColumns();
			for (MaterialColumn materialColumn : materialColumns) {
				Long sapMaterialId = materialColumn.getMaterial().getSapMaterialId();
				BigDecimal newOrderItemQty = qtyPerMaterial.get(sapMaterialId);
				
				// Can be in case of tricky bag orders
				if (newOrderItemQty == null) {
					continue;
				}
				
				BigDecimal oldMaterialQty = materialColumn.getOrderedQuantity();
				BigDecimal newMaterialQty = newOrderItemQty.add(oldMaterialQty);
				materialColumn.setOrderedQuantity(newMaterialQty.stripTrailingZeros());
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
	
	private ArchivingStatusBsEntity getArchivingStatusBsEntity(Long statusId) {
		return getRefGenericsService().findBsEntity(ArchivingStatusBsEntity.class, statusId);
	}

	/**
	 * Creates new {@link OrderTruckTypeEntity} entities for the cloned order
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private static List<OrderTruckTypeEntity> createNewOrderTruckTypesForOrder(
			BeanUtilsBean beanUtilsBean, OrderEntity newOrder) throws IllegalAccessException,
			InvocationTargetException {

		List<OrderTruckTypeEntity> srcOrderVehicleTypes = newOrder.getOrderVehicleTypes();
		List<OrderTruckTypeEntity> dstOrderVehicleTypes = new ArrayList<OrderTruckTypeEntity>();
		
		for (OrderTruckTypeEntity srcTruck : srcOrderVehicleTypes) {
			OrderTruckTypeEntity dstTruck = new OrderTruckTypeEntity();
			beanUtilsBean.copyProperties(dstTruck, srcTruck);
			// This is very important because the trucks were also copied from another order
			dstTruck.setOrder(newOrder);
			dstOrderVehicleTypes.add(dstTruck);
		}
		
		return dstOrderVehicleTypes;
	}
	
	/**
     * Clears the SDOI items (and their respective {@link SplittedOrderItemData} where the ordered qty is zero
     * => if an SDO is left without any SDOI it also gets deleted 
     * @param session
     */
	public void clearZeroOrders(HttpSession session) {

		Authorization.authorize(Authorization.ORDER);
		BestellungForm form = OrderTakingUtils.getBestellungForm(session);
		List<OrderTakingEditorDto> editorList = form.getEditorList();
		
		for (OrderTakingEditorDto editor : editorList) {
			
			// clear splitted order and items where zero
			List<SplittedOrderData> splittedOrders = editor.getOrdersListFromCustOrders();
			Iterator<SplittedOrderData> splittedOrderIterator = splittedOrders.iterator();
			
			while (splittedOrderIterator.hasNext()) {
				SplittedOrderData splittedOrderData = splittedOrderIterator.next();
				List<SplittedOrderItemData> splittedOrderItemDatas = splittedOrderData.getSplittedOrderItemDatas();
				Iterator<SplittedOrderItemData> splittedOrderItemIterator = splittedOrderItemDatas.iterator();
				while (splittedOrderItemIterator.hasNext()) {
					SplittedOrderItemData next = splittedOrderItemIterator.next();
					if (next.getOrderedQuantity().equals(BigDecimal.ZERO)) {
						splittedOrderItemIterator.remove();
					}
				}
				// if there is no item left
				if (splittedOrderItemDatas.isEmpty()) {
					splittedOrderIterator.remove();
				}
			}
			
			CustorderSplitWrapper wrapper = editor.getWrapper();
			CustorderEntity custorder = wrapper.getCustorder();
			
			// clear SDOI and SDO
			List<OrderEntity> orders = custorder.getOrders();
			Iterator<OrderEntity> ordersIterator = orders.iterator();
			
			while (ordersIterator.hasNext()) {
				OrderEntity order = ordersIterator.next();
				List<OrderItemEntity> orderItems = order.getOrderItems();
				Iterator<OrderItemEntity> orderItemIterator = orderItems.iterator();
				while (orderItemIterator.hasNext()) {
					OrderItemEntity next = orderItemIterator.next();
					if (next.getOrderedQuantity().equals(BigDecimal.ZERO)) {
						orderItemIterator.remove();
					}
				}
				// if there is no item left
				if (orderItems.isEmpty()) {
					ordersIterator.remove();
				}
			}		
			
		}
		
	}	
	
	public OrderTakingUomConversionDto convertUomWithFallbacks(OrderTakingUomConversionDto inputDto) {
		// [[
		OrderTakingUomConversionDto result = null;
		Object[][] params = {{"inputDto", inputDto}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "convertUomWithFallbacks", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.ORDER);
			
			if (inputDto == null) {
				throw new IllegalArgumentException("inputDto is null");
			}
			
			SapMaterialEntity sapMaterial = getServiceLocator().getBestellungService().getSapMaterialById(inputDto.getInSapMaterialId());
			if (sapMaterial == null) {
				throw new IllegalArgumentException("unknown SAP material ID: " + inputDto.getInSapMaterialId());
			}
			
			UomConverterService uomConverterService = getServiceLocator().getUomConverterService();
			result = inputDto;
			
			// set vehicle type CDs according to the delivery type
			final Long truckTypeCd;
			final Long wagonTypeCd;
			Long deliveryType = inputDto.getInDeliveryType();
			if (deliveryType != null && inputDto.getInVehicleTypeCd() != null) {
				if (LogonUtil.isTruckOrder(deliveryType) || LogonUtil.isPickupOrder(deliveryType)) {
					truckTypeCd = inputDto.getInVehicleTypeCd();
					wagonTypeCd = null;
				}
				else if (LogonUtil.isRailOrder(deliveryType)) {
					truckTypeCd = null;
					wagonTypeCd = inputDto.getInVehicleTypeCd();
				}
				else {
					truckTypeCd = null;
					wagonTypeCd = null;
				}
			}
			else {
				truckTypeCd = null;
				wagonTypeCd = null;
			}
			
			// conversion using the original input parameters
			UomConversionResult conversionResult = null;
			if (!String.valueOf(Constant.DROPDOWN_NOT_SELECTED).equals(inputDto.getInToUomCd())) {
				try {
					conversionResult = uomConverterService.convert(inputDto.getInQuantity(), inputDto.getInFromUomCd(), inputDto.getInToUomCd(),
							inputDto.getInSapMaterialId(), truckTypeCd, wagonTypeCd, inputDto.isInAllowFuzzy());
					 
					result.setOutQuantity(conversionResult.getQuantity());
					result.setOutUom(result.getInToUomCd());
					 
					return result;
				}
				catch (Exception e) {
					LOG.warn("could not execute conversion, try to convert it to the base UoM of the material (fall back 1). " + result.toString());
					// if exception occurred then try to convert it to the base UoM of the material or to tons, see below
				}				
			}
			
			// try to convert it to the base UoM of the material if possible
			String sapMaterialBaseUomCd = sapMaterial.getBaseUom().getUomCd();
			if (!sapMaterialBaseUomCd.equals(inputDto.getInToUomCd())) {
				try {
					 conversionResult = uomConverterService.convert(inputDto.getInQuantity(), inputDto.getInFromUomCd(), sapMaterialBaseUomCd,
								inputDto.getInSapMaterialId(), truckTypeCd, wagonTypeCd, inputDto.isInAllowFuzzy());
					 
					 result.setOutQuantity(conversionResult.getQuantity());
					 result.setOutUom(sapMaterialBaseUomCd);
					 
					 return result;
				}
				catch (Exception e) {
					LOG.warn("could not convert quantity to the base UoM of the material, try to convert it to tons (fall back 2). " + result.toString());
					// if exception occurred then try to convert it to tons, see below
				}
			}
			
			// try to convert it to tons
			if (!Constant.UOM_TONS.equals(inputDto.getInToUomCd()) && !Constant.UOM_TONS.equals(sapMaterialBaseUomCd)) {
				try {
					 conversionResult = uomConverterService.convert(inputDto.getInQuantity(), inputDto.getInFromUomCd(), Constant.UOM_TONS,
								inputDto.getInSapMaterialId(), truckTypeCd, wagonTypeCd, inputDto.isInAllowFuzzy());
					 
					 result.setOutQuantity(conversionResult.getQuantity());
					 result.setOutUom(Constant.UOM_TONS);
					 
					 return result;
				}
				catch (Exception e) {
					LOG.warn("could not convert quantity to tons, return original values without conversion (fall back 3) " + result.toString());
					result.setOutQuantity(inputDto.getInQuantity());
					result.setOutUom(inputDto.getInFromUomCd());
					return result;
				}
			}
			else {	// we already tried base UoM and tons
				LOG.warn("could not convert quantity, return original values without conversion (fall back 3) " + result.toString());
				result.setOutQuantity(inputDto.getInQuantity());
				result.setOutUom(inputDto.getInFromUomCd());
				return result;
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	@AdnTrace
	public boolean checkExportRestriction(Map<String, String> searchCriteria) {
		TimeZone userTimeZone = getUserTimeZone();
		Locale userLocale = getUserLocale();
		
		Authorization.authorize(Authorization.ORDER_LIST);
		OrderSearchExportMetaData emd = OrderSearchExportUtil.createOrderSearchExportMetaData(searchCriteria, userLocale, userTimeZone, getServiceLocator());
		SqlDataExporter exporter = new SqlDataExporter(getServiceLocator());		
		int rowCount = exporter.fetchRowCount(emd.getCountQueryString(), emd.getQParams(), userLocale, userTimeZone);
		
		ConfigService configService = getServiceLocator().getConfigService();
		ConfigBean configBean = configService.getConfigBean();
		int numOfRecInExport = configBean.getAppDefaultOrdersearchMaxNumOfRecordsInExport();
		
		return numOfRecInExport > 0 && rowCount <= numOfRecInExport;
	}

	public void setWithActiveCemFilterForBestellung(boolean withActiveCem, HttpSession session) throws ParseException {
		Authorization.authorize(Authorization.ORDER);
		BestellungForm bestellungForm = (BestellungForm) session.getAttribute("order_bestellung_BestellungForm");
		bestellungForm.setWithActiveCemContracts(withActiveCem);
	}

	public void setWithActiveAggFilterForBestellung(boolean withActiveAgg, HttpSession session) throws ParseException {
		Authorization.authorize(Authorization.ORDER);
		BestellungForm bestellungForm = (BestellungForm) session.getAttribute("order_bestellung_BestellungForm");
		bestellungForm.setWithActiveAggContracts(withActiveAgg);
	}

	public void setWithActiveCemFilterForDispokarte(boolean withActiveCem, HttpSession session) throws ParseException {
		Authorization.authorize(Authorization.MASTER_DATA_SHIPTO_CARD_R);
		DispokarteForm dispokarteForm = (DispokarteForm) session.getAttribute("order_dispokarte_DispokarteForm");
		dispokarteForm.setWithActiveCemContracts(withActiveCem);
	}

	public void setWithActiveAggFilterForDispokarte(boolean withActiveAgg, HttpSession session) throws ParseException {
		Authorization.authorize(Authorization.MASTER_DATA_SHIPTO_CARD_R);
		DispokarteForm dispokarteForm = (DispokarteForm) session.getAttribute("order_dispokarte_DispokarteForm");
		dispokarteForm.setWithActiveAggContracts(withActiveAgg);
	}
}
