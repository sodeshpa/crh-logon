/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.common.generics.BusinessPartner;
import com.crh.logon.business.ordercreation.DeliveryOptionConfigurationDTO;
import com.crh.logon.business.ordercreation.DeliverySettingFilterLogic;
import com.crh.logon.business.ordercreation.DeliverySettingSalesAreaMap;
import com.crh.logon.business.ordercreation.DeliverySettingShiptoMap;
import com.crh.logon.business.ordercreation.dto.DeliverySetting;
import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.business.ordercreation.dto.OrderItemDeliverySetting;
import com.crh.logon.business.ordercreation.dto.SupplyPlantDTO;
import com.crh.logon.business.ordercreation.dto.TruckTypeWrapper;
import com.crh.logon.business.ordercreation.dto.TruckWagonTypeWrapper;
import com.crh.logon.business.services.beans.ConversionFactor;
import com.crh.logon.business.services.beans.UomConversionResult;
import com.crh.logon.business.services.interfaces.DeliverySettingService;
import com.crh.logon.business.services.interfaces.DispokarteService;
import com.crh.logon.business.services.interfaces.GoodsMovementService;
import com.crh.logon.business.services.interfaces.OrderDetailService;
import com.crh.logon.business.services.interfaces.RefDataGenericsService;
import com.crh.logon.business.services.interfaces.UomConverterService;
import com.crh.logon.business.services.localinterfaces.DispokarteServiceLocalInterface;
import com.crh.logon.business.services.orderdetail.dto.BimodalTruckOrderItemDTO;
import com.crh.logon.business.services.utils.BusinessPartnerUtil;
import com.crh.logon.business.services.utils.DateTimeAwareCustomerOrderWrapper;
import com.crh.logon.business.services.utils.DateTimeAwareOrderWrapper;
import com.crh.logon.business.services.utils.DeliveryDistanceValidationUtils;
import com.crh.logon.business.services.utils.DistanceValidationInfoBean;
import com.crh.logon.business.services.utils.GoodsMovementUtils;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.LogonStatusUtils;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.OrderUtils;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.business.services.utils.SortOrder;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.constants.Constant;
import com.crh.logon.order.web.actions.bestellung.BestellungAction;
import com.crh.logon.order.web.forms.auftrag.GoodsMovementDisplayBean;
import com.crh.logon.order.web.forms.auftrag.LiveKarteBean;
import com.crh.logon.order.web.forms.auftrag.LiveKarteForm;
import com.crh.logon.order.web.forms.auftrag.MaterialTabBean;
import com.crh.logon.order.web.forms.auftrag.OrderDetailBean;
import com.crh.logon.order.web.forms.auftrag.OrderDetailForm;
import com.crh.logon.order.web.forms.auftrag.OrderItemBean;
import com.crh.logon.order.web.forms.auftrag.OrderItemConvFactors;
import com.crh.logon.order.web.forms.auftrag.PayerSapBpBean;
import com.crh.logon.order.web.forms.auftrag.PlanningMaterialBean;
import com.crh.logon.order.web.forms.auftrag.ServicesExtraCostTabBean;
import com.crh.logon.order.web.forms.auftrag.ShiptoBilltoBean;
import com.crh.logon.order.web.forms.bestellung.BestellungForm;
import com.crh.logon.order.web.forms.dispokarte.DispokarteForm;
import com.crh.logon.order.web.utils.comparator.CarrierComparator;
import com.crh.logon.order.web.utils.comparator.PlantComparator;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.FireqMaterialMixdesignEntity;
import com.crh.logon.persistence.entities.common.GoodsMovementEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.MatPlantEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.OrderOutputdocEntity;
import com.crh.logon.persistence.entities.common.OrderTruckTypeEntity;
import com.crh.logon.persistence.entities.common.PersonEntity;
import com.crh.logon.persistence.entities.common.PoolTxEntity;
import com.crh.logon.persistence.entities.common.RailwayStationEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoBilltoEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.common.SimpleVehicleEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.MaterialPackageBsEntity;
import com.crh.logon.persistence.entities.refdata.MaterialPackageTxEntity;
import com.crh.logon.persistence.entities.refdata.OrderSpiTxEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusTxEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.ReturnMaterialUsageBsEntity;
import com.crh.logon.persistence.entities.refdata.ReturnOrderReasonBsEntity;
import com.crh.logon.persistence.entities.refdata.ReturnQuantHandlingBsEntity;
import com.crh.logon.persistence.entities.refdata.SapCountryBsEntity;
import com.crh.logon.persistence.entities.refdata.SapOrderExtraCostBsEntity;
import com.crh.logon.persistence.entities.refdata.SapOrderExtraCostTxEntity;
import com.crh.logon.persistence.entities.refdata.ShiptoTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.ShiptoTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.SiloVerifTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.SiloVerifTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.SimpleVehicleTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.TrailerTrspCatTxEntity;
import com.crh.logon.persistence.entities.refdata.TrailerTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.TruckTransportCatTxEntity;
import com.crh.logon.persistence.entities.refdata.TruckTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.TruckTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.refdata.UomTxPK;
import com.crh.logon.persistence.entities.refdata.WagonTrspCatTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapBpSareaEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialDescriptionEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.sap.SapVendorEntity;
import com.crh.logon.persistence.entities.views.BizeventOrderLogEntity;
import com.crh.logon.persistence.entities.views.OrderFiltersEntity;
import com.crh.logon.persistence.entities.views.SapMaterialDescEntity;
import com.crh.logon.utils.LogonConversionException;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.WagonNumberUtils;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.JsonUtils;
import com.crh.logon.web.utils.MessageUtils;
import flexjson.JSONSerializer;
import org.apache.struts.action.ActionForm;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 */
@AdnTrace
public class OrderActionSupport extends ActionSupport {
	
	private static final int SELECTED_ORDER_ITEM_INDEX = 0;

	private static final boolean MATERIAL_TAB_IS_OPENED = true;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(OrderActionSupport.class);
	
	private static final Boolean DISABLE_BILLTO_AND_PAYER = true;
	private static final Boolean ENABLE_BILLTO_AND_PAYER = false;

	private static final CarrierEntity UNSET_CARRIER = null;

	private static Long[] orderStatusesConsideredAsLoadingStarted = new Long[] {
			Constant.ORDER_STATUS_LOADED, Constant.ORDER_STATUS_SHIPPING,
			Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT, Constant.ORDER_STATUS_DELIVERED,
			Constant.ORDER_STATUS_FINISHED, Constant.ORDER_STATUS_DELETED };
	
	/** Initializes Live Karte screen (loads orders and fills filters) 
	 * @param helpForm
	 * @param locale
	 */
	public void initLiveKarte(LiveKarteForm helpForm, Locale locale, List<Long> orderStatusCdList, Long orderNumber) {
		// [[
		Object[][] params = {{"helpForm", helpForm},
							{"locale", locale},
							{"orderStatusCdList", orderStatusCdList},
							{"orderNumber", orderNumber}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initLiveKarte", params);
		}
		// ]]
		try {
			
			
			//load initial set of orders
			List orders;
			if ((orderStatusCdList != null) && (!orderStatusCdList.isEmpty())) {
				orders = getLiveKarteService().loadOrderFilters(locale.getLanguage(), orderStatusCdList, orderNumber);
			} 
			else {
				orders = new ArrayList();
			}
			
			
			helpForm.setOrderList(orders);
			//in the beginning the list is not filtered
			helpForm.setFiltOrderList((List) ((ArrayList) orders).clone());
			
			for (Iterator iterator = orders.iterator(); iterator.hasNext();) {
				OrderFiltersEntity orderFilter = (OrderFiltersEntity) iterator.next();
				
				//create plant list
				if (orderFilter.getProductionPlantCd() != null) {
					String key = orderFilter.getProductionPlantCd();
					if (!helpForm.getPlantMap().containsKey(key)) {
						LiveKarteBean lkBean = new LiveKarteBean();
						lkBean.setKey(key);
						lkBean.setValue(orderFilter.getPlantName());
						helpForm.getPlantMap().put(key, lkBean);
					}
				}
			
				//create shipping point list
				if (orderFilter.getShippingpointId() != null) {
					String key = orderFilter.getShippingpointId().toString();
					if (!helpForm.getShPointMap().containsKey(key)) {
						LiveKarteBean lkBean = new LiveKarteBean();
						lkBean.setKey(key);
						lkBean.setValue(orderFilter.getShippingpointName());
						helpForm.getShPointMap().put(key, lkBean);
					}
				}
			
				//create loading point list
				if (orderFilter.getLoadingpointId() != null) {
					String key = orderFilter.getLoadingpointId().toString();
					if (!helpForm.getLdPointMap().containsKey(key)) {
						LiveKarteBean lkBean = new LiveKarteBean();
						lkBean.setKey(key);
						lkBean.setValue(orderFilter.getLoadingpointName());
						helpForm.getLdPointMap().put(key, lkBean);
					}
				}
			
				//create carrier list
				if (orderFilter.getCommCarrierId() != null) {
					String key = orderFilter.getCommCarrierId().toString();
					if (!helpForm.getCarrierMap().containsKey(key)) {
						LiveKarteBean lkBean = new LiveKarteBean();
						lkBean.setKey(key);
						lkBean.setValue(orderFilter.getCommCarrierName());
						helpForm.getCarrierMap().put(key, lkBean);
					}
				}
			
				//create pool list
				if (orderFilter.getPoolId() != null) {
					String key = orderFilter.getPoolId().toString();
					if (!helpForm.getPoolMap().containsKey(key)) {
						LiveKarteBean lkBean = new LiveKarteBean();
						lkBean.setKey(key);
						lkBean.setValue(orderFilter.getPoolName());
						helpForm.getPoolMap().put(key, lkBean);
					}
				}
			
				//create sales area list
				String keySarea = orderFilter.getSareaId().toString();
				if (!helpForm.getSareaMap().containsKey(keySarea)) {
					LiveKarteBean lkBean = new LiveKarteBean();
					lkBean.setKey(keySarea);
					lkBean.setValue(orderFilter.getSareaName());
					helpForm.getSareaMap().put(keySarea, lkBean);
				}
			
				//create order number list
				String keyOrd = orderFilter.getOrderNumber().toString();
				if (!helpForm.getOrderNumMap().containsKey(keyOrd)) {
					LiveKarteBean lkBean = new LiveKarteBean();
					lkBean.setKey(keyOrd);
					lkBean.setValue(keyOrd);
					helpForm.getOrderNumMap().put(keyOrd, lkBean);
				}
			
				//create shipto list
				String keyShip = orderFilter.getShiptoSapBpId().toString();
				if (!helpForm.getShiptoMap().containsKey(keyShip)) {
					LiveKarteBean lkBean = new LiveKarteBean();
					lkBean.setKey(keyShip);
					lkBean.setValue(orderFilter.getShiptoName1());
					helpForm.getShiptoMap().put(keyShip, lkBean);
				}
			
				//create soldto list
				String keySold = orderFilter.getSoldtoSapBpId().toString();
				if (!helpForm.getSoldtoMap().containsKey(keySold)) {
					LiveKarteBean lkBean = new LiveKarteBean();
					lkBean.setKey(keySold);
					lkBean.setValue(orderFilter.getSoldtoName1());
					helpForm.getSoldtoMap().put(keySold, lkBean);
				}
			
				//create material list
				if (orderFilter.getSapMaterialId() != null) {
					String key = orderFilter.getSapMaterialId().toString();
					if (!helpForm.getMaterialMap().containsKey(key)) {
						LiveKarteBean lkBean = new LiveKarteBean();
						lkBean.setKey(key);
						lkBean.setValue(orderFilter.getMaterialTxt() + "("
								+ orderFilter.getMaterialNo() + ")");
						helpForm.getMaterialMap().put(key, lkBean);
					}
				}
			
				//get sales office
				if (orderFilter.getSofficeCd() != null) {
					String key = orderFilter.getSofficeCd();
					if (!helpForm.getSofficeMap().containsKey(key)) {
						LiveKarteBean lkBean = new LiveKarteBean();
						lkBean.setKey(key);
						lkBean.setValue(orderFilter.getSofficeName());
						helpForm.getSofficeMap().put(key, lkBean);
					}
				}
			
				//get sales group
				if (orderFilter.getSgroupCd() != null) {
					String key = orderFilter.getSgroupCd();
					if (!helpForm.getSgroupMap().containsKey(key)) {
						LiveKarteBean lkBean = new LiveKarteBean();
						lkBean.setKey(key);
						lkBean.setValue(orderFilter.getSgroupName());
						helpForm.getSgroupMap().put(key, lkBean);
					}
				}
			
				//get trucks 
				if (orderFilter.getTruckId() != null) {
					String key = orderFilter.getTruckId().toString();
					if (!helpForm.getTruckMap().containsKey(key)) {
						LiveKarteBean lkBean = new LiveKarteBean();
						lkBean.setKey(key);
						lkBean.setValue(orderFilter.getTruckName());
						helpForm.getTruckMap().put(key, lkBean);
					}
				}
			
				//get drivers
				if (orderFilter.getDriverId() != null) {
					String key = orderFilter.getDriverId().toString();
					if (!helpForm.getDriverMap().containsKey(key)) {
						LiveKarteBean lkBean = new LiveKarteBean();
						lkBean.setKey(key);
						lkBean.setValue(orderFilter.getDriverName());
						helpForm.getDriverMap().put(key, lkBean);
					}
				}
			
			}
			
			//sort maps by value
			helpForm.setPlantMap(sortByValue(helpForm.getPlantMap()));
			helpForm.setShPointMap(sortByValue(helpForm.getShPointMap()));
			helpForm.setLdPointMap(sortByValue(helpForm.getLdPointMap()));
			helpForm.setCarrierMap(sortByValue(helpForm.getCarrierMap()));
			helpForm.setPoolMap(sortByValue(helpForm.getPoolMap()));
			helpForm.setSareaMap(sortByValue(helpForm.getSareaMap()));
			helpForm.setOrderNumMap(sortByValue(helpForm.getOrderNumMap()));
			helpForm.setShiptoMap(sortByValue(helpForm.getShiptoMap()));
			helpForm.setSoldtoMap(sortByValue(helpForm.getSoldtoMap()));
			helpForm.setMaterialMap(sortByValue(helpForm.getMaterialMap()));
			helpForm.setSofficeMap(sortByValue(helpForm.getSofficeMap()));
			helpForm.setSgroupMap(sortByValue(helpForm.getSgroupMap()));
			helpForm.setTruckMap(sortByValue(helpForm.getTruckMap()));
			helpForm.setDriverMap(sortByValue(helpForm.getDriverMap()));
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
		
	}
	
	public Map sortByValue(Map<String, LiveKarteBean> map) {
		List list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				//return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
				return ((Comparable<String>) ((LiveKarteBean) ((Map.Entry) (o1)).getValue()).getValue()).compareTo(((LiveKarteBean) ((Map.Entry) (o2)).getValue()).getValue());
			}
		});
		
		Map result = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
	
	protected JSONSerializer getJsonSerializer() {
		return JsonUtils.getJsonSerializer();
	}
	
	/**	Initializes main part of Order Details screen
	 * @param helpForm
	 * @param order
	 * @param request
	 */
    public void initOrderDetails(OrderDetailForm helpForm, OrderEntity order, HttpServletRequest request) {
		// [[
		Object[][] params = {{"helpForm", helpForm},
							{"order", order}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initOrderDetails", params);
		}
		// ]]
		try {
			CustorderEntity custorder = order.getCustorder();
			String customerOrderNumber = Constant.NO_DATA;
			if (custorder != null) {
				customerOrderNumber = custorder.getCustorderNumber() + "-";
			}
			
			// LOGON-5035
			helpForm.setTitle(customerOrderNumber + order.getOrderNumber() + " " + order.getSoldtoSapBp().getName1()
					+ ", " + order.getShiptoSapBp().getName1() + " ("
					+ order.getSarea().getSorg().getSorgCd() + "/"
					+ order.getSarea().getDistributionChannel() + "/"
					+ order.getSarea().getDivision().getDivisionCd() + ")");
			
			OrderDetailBean orderBean = new OrderDetailBean();
			orderBean.setContactPerson(order.getContactPersonTxt());
			
			//set shipto
			ShiptoEntity shipto = getOrderDetailService().loadShipto(order.getShiptoSapBp().getSapBpId());
			helpForm.setShipto(shipto);

			SareaEntity sareaEntity = order.getSarea();
			ShiptoSareaEntity shiptoSalesArea = getOrderService().fetchShiptoSarea(order.getShiptoSapBp(), sareaEntity);


			DeliveryDistanceValidationUtils deliveryDistanceValidationUtils = new
					DeliveryDistanceValidationUtils();
			DistanceValidationInfoBean distanceValidationInfoBean = deliveryDistanceValidationUtils
					.distanceBaseLeadTimeCheckCreatePopUpText(shiptoSalesArea, sareaEntity, custorder, null, request.getLocale());

			String distanceValidationPopUpText = deliveryDistanceValidationUtils.getDistanceValidationPopUpText
					(distanceValidationInfoBean, request.getLocale());

			helpForm.setDistanceValidationPopUpText(distanceValidationPopUpText);
			helpForm.setShiptoSarea(shiptoSalesArea);

			TimeZone shiptoTZ = getTimeZoneService().loadForShipTo(shipto, getUserLocale());
			helpForm.setShiptoTimeZone(shiptoTZ);
			
			//set order status
			Locale locale = helpForm.getLocale();
			if (order.getOrderStatus() != null) {
				OrderStatusTxEntity orderStatus = getRefDataGenericsService().findTxEntity(OrderStatusTxEntity.class, locale,
						order.getOrderStatus().getOrderStatusCd());
				orderBean.setOrderStatus(orderStatus.getTxt());
			}
			String distributionDestCd = order.getDistributionDest().getDistributionDestCd();
			orderBean.setDistributionDestCd(distributionDestCd);
			
			
			orderBean.setOnHoldOrderFlag(order.getOnHoldOrderAndLockedFlag());
			
			orderBean.setLdsDeliveryNoteNo(order.getLdsDeliveryNoteNo());
			orderBean.setOrderNumberFromSeqUsage(order.getOrderNumberFromSeqUsage());
			
			// set the special processing indicator
			if (order.getSpi() != null) {
				orderBean.setSpiCd(order.getSpi().getOrderSpiCd());
			}
			
			//set delivery date and time
			orderBean.setDelivDateFromDat(order.getDeliveryFromDat());
			String delivDateFromStr = shiptoTZ.fmtAsDayMonthYear(order.getDeliveryFromDat());
			orderBean.setDelivDateFrom(delivDateFromStr);

			orderBean.setDelivDateToDat(order.getDeliveryToDat());
			String delivDateToStr = shiptoTZ.fmtAsDayMonthYear(order.getDeliveryToDat());
			orderBean.setDelivDateTo(delivDateToStr);
			
			String delivTimeFromStr = shiptoTZ.fmtAsHourMinute(order.getDeliveryFromDat());
			orderBean.setDelivTimeFrom(delivTimeFromStr);
			String delivTimeToStr = shiptoTZ.fmtAsHourMinute(order.getDeliveryToDat());
			orderBean.setDelivTimeTo(delivTimeToStr);
			
			//set created by info
			PersonEntity person = getOrderDetailService().loadPerson(order.getCtlCreUid());
			orderBean.setCreatedByPerson(person.getFirstname() + " " + person.getName());
			String createdByDateStr = getUserTimezone().fmtAsDayMonthYear(order.getCtlCreTs());
			orderBean.setCreatedByDate(createdByDateStr);
			String createdByTimeStr = getUserTimezone().fmtAsHourMinute(order.getCtlCreTs());
			orderBean.setCreatedByTime(createdByTimeStr);
			orderBean.setRailHoldBackReOrderFlag(order.getRailHoldBackReOrderFlag());
			
			//set service only order flag
			orderBean.setServiceOnlyOrderFlag(LogonUtil.isServiceOnlyOrder(order));
			
			// get lists for Drop Downs
			List<DeliveryTypeTxEntity> deliveryTypeList = getRefDataGenericsService().findTxEntityList(DeliveryTypeTxEntity.class, locale);
			helpForm.setDeliveryTypeList(deliveryTypeList);
			List poolList = getOrderDetailService().loadPoolList(locale);
			helpForm.setPoolList(poolList);
			List<TruckTypeTxEntity> truckTypeList = getRefDataGenericsService().findTxEntityList(TruckTypeTxEntity.class, true, locale, TruckTypeTxEntity.ATTR_TXT, true);
			helpForm.setTruckTypeList(truckTypeList);
			List<OrderSpiTxEntity> spiList = getRefDataGenericsService().findTxEntityList(OrderSpiTxEntity.class, true, locale, OrderSpiTxEntity.ATTR_TXT, true);
			helpForm.setSpiList(spiList);

			
			if (order.getPool() != null) {
				Map carrierMap = getOrderDetailService().loadCommCarrierMap(order.getPool().getPoolId());
				helpForm.setCommCarrierMap(carrierMap);
			}
			
			if (order.getPool() != null &&  order.getCommCarrier() != null ) {
				Long commercialCarrierId = order.getCommCarrier().getCarrierId();
				Long preSelectedExecutingCarrierId = order.getExecCarrier() != null ? order.getExecCarrier().getCarrierId() :
						null;
				List<CarrierEntity> execCarrierList = 
						getOrderDetailService().loadExecCarrierList(commercialCarrierId, preSelectedExecutingCarrierId);
				Collections.sort(execCarrierList, new CarrierComparator());
				helpForm.setExecCarrierList(execCarrierList);
			}
			
			//set plant and shipping point
			if (order.getShippingpoint() != null) {
				String plantCd = order.getShippingpoint().getPlant().getPlantCd();
				orderBean.setPlantCd(plantCd);
				orderBean.setShPointNo(order.getShippingpoint().getSapShippingpointNo());
			}
			
			DeliverySettingSalesAreaMap salesAreaMap = initSalesAreaMap(order,
					locale, true);
			helpForm.setSalesAreaMap(salesAreaMap);
			
			//set vehicle wrapper which is used for the allowed vehicle type multiselection dropdown
			if (helpForm.getSalesAreaMap() != null && helpForm.getSalesAreaMap().getShiptoSettings() != null) 
			{
				final List<TruckTypeWrapper> vehicleTypes = truckTypeWrappersFromOrder(
						order, 
						helpForm.getSalesAreaMap().getShiptoSettings(), 
						locale);
				helpForm.setTruckTypeWrappers(vehicleTypes);
			}
			
			//set pool
			if (order.getPool() != null) {
				orderBean.setPoolId(order.getPool().getPoolId());
				PoolTxEntity poolTx = getPoolAdminService().fetchPoolTxEntity(orderBean.getPoolId(), locale);
				if(poolTx != null) {
					orderBean.setPoolName(poolTx.getName());					
				}
			}
			else {
				orderBean.setPoolId(Constant.INVALID_OBJECT_ID);
			}
			
			//set carrier
			if (order.getCommCarrier() != null) {
				orderBean.setCommCarrierId(order.getCommCarrier().getCarrierId());
				orderBean.setCommCarrierVendorSapId(order.getCommCarrier().getSapVendor().getSapId());
			}
			else {
				orderBean.setCommCarrierId(Constant.INVALID_OBJECT_ID);
			}
			
			//set carrier
			if (order.getExecCarrier() != null) {
				orderBean.setExecCarrierId(order.getExecCarrier().getCarrierId());
				orderBean.setExecCarrierVendorSapId(order.getExecCarrier().getSapVendor().getSapId());
			}
			else {
				orderBean.setExecCarrierId(Constant.INVALID_OBJECT_ID);
			}
			
			
			// set pool and carrier assignment flags
			setPoolAndCarrierFlags(orderBean, order.getOrderStatus().getOrderStatusCd().longValue());
			
			
			//set truck and driver
			AssignmentEntity ass = getOrderDetailService().loadAssignment(order.getOrderId());
			helpForm.setAssignment(ass);
			
			if (ass != null) {
			
				TruckEntity truck = ass.getTruck();
				//delivery by truck
				if (truck != null) {
					orderBean.setTruckId(truck.getTruckId());
					orderBean.setTruckName(truck.getTruckName());
					orderBean.setPlateNo(truck.getTruckPlateNo());
				
					PersonEntity driver = getOrderDetailService().loadDriver(truck.getTruckId());
					if (driver != null) {
						orderBean.setDriverName(driver.getFirstname() + " " + driver.getName());
					}
					
					//get txt
					TruckTransportCatTxEntity truckTrspCatTx = getRefDataGenericsService().findTxEntity(TruckTransportCatTxEntity.class, locale, truck.getTruckTransportCat());
					TruckTypeTxEntity truckTypeTx = getRefDataGenericsService().findTxEntity(TruckTypeTxEntity.class, locale, truck.getTruckType());
			
					
					if (Constant.TRUCK_TRSP_CAT_TRAILER == truck.getTruckTransportCat().getTruckTransportCatCd()) {
						
						TrailerEntity trailer = getOrderDetailService().loadTrailer(truck.getTruckId());
						if (trailer != null) {
							//get txt
							TrailerTrspCatTxEntity trailerTrspTx = getRefDataGenericsService().findTxEntity(TrailerTrspCatTxEntity.class, locale, trailer.getTrailerTrspCat());
							TrailerTypeTxEntity trailerTypeTx = getRefDataGenericsService().findTxEntity(TrailerTypeTxEntity.class, locale, trailer.getTrailerType());
							
							orderBean.setTrspCategory(truckTrspCatTx.getTxt() + " - " + trailerTrspTx.getTxt());
							orderBean.setTrspType(truckTypeTx.getTxt() + " - " + trailerTypeTx.getTxt());
						}
					} 
					else {
						orderBean.setTrspCategory(truckTrspCatTx.getTxt());
						orderBean.setTrspType(truckTypeTx.getTxt());
					}
					
					orderBean.setShowPickupInfo(false);
					if (truck.getTruckId() == Constant.ARTIFICIAL_PICKUP_BAG_TRUCK_ID
							|| truck.getTruckId() == Constant.ARTIFICIAL_PICKUP_BULK_PWDR_TRUCK_ID
							|| truck.getTruckId() == Constant.ARTIFICIAL_PICKUP_BULK_DMP_TRUCK_ID) {
						orderBean.setShowPickupInfo(true);
						List<OrderItemEntity> orderItems = order.getOrderItems();
						LoadEntity load = null;
						for (OrderItemEntity oie : orderItems) {
							load = oie.getLoad();
							if (load != null) {
								orderBean.setWsTruckPlateNo(load.getWsTruckPlateNo());
								orderBean.setWsTrailerPlateNo(load.getWsTrailerPlateNo());
								orderBean.setWsCarrier(load.getWsCarrier());
								break;
							}
						}
					}
				}
			
				WagonEntity wagon = ass.getWagon();
				//delivery by wagon
				if (wagon != null) {
					
					String wagonNo = wagon.getWagonNo();
					if (wagonNo != null) {
						orderBean.setPlateNo(WagonNumberUtils.toDisplayNo(wagonNo));
					}
					
					if (wagon.getWagonType().getWagonTrspCat() != null) {
						WagonTrspCatTxEntity wagonTrspTx = getRefDataGenericsService().findTxEntity(WagonTrspCatTxEntity.class, locale, wagon.getWagonType().getWagonTrspCat());
						orderBean.setTrspCategory(wagonTrspTx.getTxt());
					}
					
					orderBean.setTrspType(wagon.getWagonType().getWagonTypeName());
				}
				
				SimpleVehicleEntity simpleVehicle = ass.getSimpleVehicle();
				if(null != simpleVehicle) {
					
					String vehicleName = simpleVehicle.getVehicleName();
					if(null != vehicleName) {
						orderBean.setPlateNo(vehicleName);
					}
					
					if(null != simpleVehicle.getSimpleVehicleType()) {
						SimpleVehicleTypeTxEntity simpleVehicleTx = getRefDataGenericsService().findTxEntity(SimpleVehicleTypeTxEntity.class, 
								locale,simpleVehicle.getSimpleVehicleType());
						orderBean.setTrspType(simpleVehicleTx.getTxt());
					}
				}
			}
			
			//set delivery type
			if (order.getDeliveryType() != null) {
				orderBean.setDeliveryTypeCd(order.getDeliveryType().getDeliveryTypeCd());
			}
			else {
				orderBean.setDeliveryTypeCd(Constant.INVALID_OBJECT_ID);
			}
			
			//set order status log
			List orderStatusFormatedList = getOrderStatusList(locale, order);
			orderBean.setOrderStatusList(orderStatusFormatedList);
					
			final SapBpSareaEntity sapBpSarea = 
					getOrderDetailService().loadSapBpSarea(
							order.getShiptoSapBp().getSapBpId(), 
							order.getSarea().getSareaId());
			helpForm.setSapBpSarea(sapBpSarea);
			
			helpForm.setOrderBean(orderBean);
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	private DeliverySettingSalesAreaMap initSalesAreaMap(OrderEntity order,
			Locale locale, boolean applySapMaterialFilter) {
		final SapBpEntity shiptoSapBpEntity = order.getShiptoSapBp();
		final SapBpEntity soldtoSapBp = order.getSoldtoSapBp();
		final SareaEntity sarea = order.getSarea();

		final Set<Long> sapMaterialIds;
		if (applySapMaterialFilter) {
			sapMaterialIds = getSapMaterialIdsFromOrder(order);
		}
		else {
			sapMaterialIds = null;
		}
				
		DeliverySettingSalesAreaMap salesAreaMap = getServiceLocator().getDeliverySettingService().initDeliverySettingSalesAreaMapForOrderDetail(
				soldtoSapBp, shiptoSapBpEntity, sarea, locale, sapMaterialIds,
				order.getSapContract());
		return salesAreaMap;
	}

	/**
	 * Extracts the vehicles allowed for the order. This is the list of vehicles <em>selectable</em>,
	 * but not necessarily selected. The list is determined by the enabled vehicle types in the ship
	 * to and limited to the vehicle types that are suitable for the order material.
	 * @param order
	 * @param locale
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<TruckTypeWrapper> truckTypeWrappersFromOrder(
			OrderEntity order, 
			DeliverySettingShiptoMap shiptoSettings,
			final Locale locale) 
	{
		if (order.getOrderItems().isEmpty()) {
			// No vehicles required so select if there are no items in this order.
			// Not sure if this can even happen though.
			return new LinkedList<TruckTypeWrapper>();
		}
		
		final OrderItemEntity firstItem = OrderUtils.getOrderItemsFromOrderSorted(order).get(0);
		final Long deliveryTypeCd = order.getDeliveryType().getDeliveryTypeCd();
		final SapMaterialEntity sapMaterial = firstItem.getSapMaterial();
		
		final List<TruckWagonTypeWrapper> vehicleList = 
				(List<TruckWagonTypeWrapper>) DeliverySettingFilterLogic.filterVehicleTypeWrappers(
						shiptoSettings, 
						sapMaterial, 
						deliveryTypeCd);
		
		// This list is only for truck types, not for rail types. Therefore we filter
		// and return only truck types.
		final List<TruckTypeWrapper> truckList = new LinkedList<TruckTypeWrapper>();
		for (TruckWagonTypeWrapper twtw : vehicleList) {
			if (twtw instanceof TruckTypeWrapper) {
				truckList.add((TruckTypeWrapper) twtw);
			}
		}
		
		return truckList;
	}

	/**
	 * Initalize the options available as delivery plant, shippingpoint and loadingpoint. The dropdowns supply (delivery) plant, shippingpoint
	 * contain all those options which would be available on order taking for the order's first material item.
	 * Loadingpoint dropdown was NOT YET (03.11.2011) supposed to be filtered according to the material restrictions, or anything else, therefore
	 * it simply contains all the loadingpoints available for the specified shippingpoint id.
	 * 
	 * ATTENTION: the option the order has MUST BE available, in order to be able to save exactly as it was created, even if the 
	 * material loading background data changed in the meantime (editing past orders, etc. require this).
	 * 
	 * @param helpForm
	 * @param plantCd
	 * @param shippingpointId
	 */
	protected void initFirstMaterialItemDependentLists(OrderDetailForm helpForm, String plantCd, Long shippingpointId) {
		// prepare services
		OrderDetailService orderDetailService = getOrderDetailService();
		DeliverySettingService deliverySettingService = getDeliverySettingService();
		
		final List<SupplyPlantDTO> supplyPlantList = new ArrayList<SupplyPlantDTO>();
		final Map<String, ShippingpointEntity> shPointMap = new HashMap<String, ShippingpointEntity>();
		
		final OrderEntity order = helpForm.getOrder();
		final ShippingpointEntity orderShippingpoint = order.getShippingpoint();
		final OrderItemBean bean = helpForm.getFirstMaterialItemBean();
		final boolean isRailReturnOrRepairOrder = orderDetailService.isRailReturnOrRepairOrder(order);

		// In case the order has material items, initialize dropdowns by fetching all possible options
		// from the services. Otherwise, in the two cases below, only initialize the dropdown with the
		// selected option in the order:
		//     => case #1: repair/return order has no material item
		//     => case #2: only-service orders do not have non-service material items, either
		if (!isRailReturnOrRepairOrder && bean != null) {
			supplyPlantList.addAll(bean.getSupplyPlantList());
			//get shipping point list for drop down
			if (plantCd != null) {
				List<ShippingpointEntity> shPointList = deliverySettingService.fetchShippingpointListWithDeliveryType(plantCd, (order.getDeliveryType() == null) ? null : order.getDeliveryType().getDeliveryTypeCd());
				if (shPointList != null) {
					for (ShippingpointEntity shippingpointEntity : shPointList) {
						shPointMap.put(shippingpointEntity.getSapShippingpointNo(), shippingpointEntity);
					}
				}
			}
			
			if (shippingpointId != null) {
				// get loading point list for drop down
				List<LoadingpointEntity> loadingPoints = orderDetailService.findLoadingPointsByShippingPointId(shippingpointId);
				if (loadingPoints != null) {
					Map<Long, LoadingpointEntity> ldPointMap = new HashMap<Long, LoadingpointEntity>();
					for (LoadingpointEntity lp : loadingPoints) {
						ldPointMap.put(lp.getLoadingpointId(), lp);
					}
					helpForm.setLdPointMap(ldPointMap);
				}
			}
		}
		
		// always add the option saved on the order
		if (orderShippingpoint != null) {
			// LOGON-5055 Order Details: Same Delivery Plant is shown twice in dropdown
			// if ( !supplyPlantList.contains(order.getShippingpoint().getPlant())) {
			// 	supplyPlantList.add(order.getShippingpoint().getPlant());
			// }
			// -> Plants not read in same transaction, so contains does not work -> for now quick solution check manually by id.
			
			boolean addOrderPlant = true;
			PlantBsEntity orderPlant = orderShippingpoint.getPlant();
			String orderPlantCd = orderPlant.getPlantCd();
			for (SupplyPlantDTO plant : supplyPlantList) {
				if (orderPlantCd.equals(plant.getPlantCd())) {
					addOrderPlant = false;
					break;
				}
			}
			if (addOrderPlant) {
				SupplyPlantDTO supplyPlant = new SupplyPlantDTO(orderPlantCd, orderPlant.getPlantName());
				supplyPlantList.add(supplyPlant);
			}
			
			String orderSapShippingpointNo = orderShippingpoint.getSapShippingpointNo();
			shPointMap.put(orderSapShippingpointNo, orderShippingpoint);
		}
		
		Comparator<SupplyPlantDTO> comp = new PlantComparator();
		Collections.sort(supplyPlantList, comp);
		
		helpForm.setPlantList(supplyPlantList);
		helpForm.setShPointMap(shPointMap);
	}

	/** sets pool and carrier assignment flags
	 * @param orderBean
	 * @param orderStatus
	 */
	protected void setPoolAndCarrierFlags(OrderDetailBean orderBean, long orderStatus) {
		if (orderStatus == Constant.ORDER_STATUS_NEW) {
			// No checkbox checked -> state "new"
			orderBean.setAutoPoolAssignmentFlag(false);
			orderBean.setAutoCommCarrierAssignmentFlag(false);
		}
		else if ( (orderStatus == Constant.ORDER_STATUS_TRANSMITTED_TO_POOL) || (orderStatus == Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK) ) {
			// Pool checked -> state "transmitted to pool"
			orderBean.setAutoPoolAssignmentFlag(true);
		}
		else {
			orderBean.setAutoPoolAssignmentFlag(true);
			orderBean.setAutoCommCarrierAssignmentFlag(true);
		}
		
		//reset flags if pool or carrier is not set
		if (orderBean.getPoolId() == null) {
			orderBean.setAutoPoolAssignmentFlag(false);
			orderBean.setAutoCommCarrierAssignmentFlag(false);
		}
		if (orderBean.getCommCarrierId() == null) {
			orderBean.setAutoCommCarrierAssignmentFlag(false);
		}
	}

	/**
	 * loads all order status list
	 * @param helpForm
	 * @param order
	 * @return order status list for a given order
	 */
	protected List<String> getOrderStatusList(Locale locale, OrderEntity order) {
		// [[
		List<String> result = null;
		Object[][] params = {{"locale", locale},
							{"order", order}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getOrderStatusList", params);
		}
		// ]]
		try {
			List<SortOrder> sortOrders = new LinkedList<SortOrder>();
			sortOrders.add(SortOrder.desc(BizeventOrderLogEntity.ATTR_EVENT_TS));
			sortOrders.add(SortOrder.desc(BizeventOrderLogEntity.ATTR_BIZEVENT_ID));
			
			List<BizeventOrderLogEntity> orderStatusList = getOrderDetailService().loadBizevents(
					order.getOrderId(), locale.getLanguage(), sortOrders);
			List<String> orderStatusFormatedList = new ArrayList<String>();
			
			for (BizeventOrderLogEntity bizevent : orderStatusList) {
				String statusDateTime = getUserTimezone().fmtAsDayMonthYearHourMinute(bizevent.getEventTs());
				orderStatusFormatedList.add(statusDateTime + " " + bizevent.getBizeventTypeTxt());
			}
			return result = orderStatusFormatedList;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public void initMaterialTab(OrderDetailForm helpForm) {
		// [[
		Object[][] params = {{"helpForm", helpForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initMaterialTab", params);
		}
		// ]]
		try {
			OrderEntity order = helpForm.getOrder();
			SapContractEntity sapContract = order.getSapContract();
			helpForm.setPayerIgnored(OrderUtils.isPayerIgnored(sapContract));

			MaterialTabBean materialSettings = new MaterialTabBean();
			
			OrderDetailService orderDetailService = getOrderDetailService();
			
			RefDataGenericsService referenceDataGenericsService = getRefDataGenericsService();
			List<ReturnOrderReasonBsEntity> returnReasons = referenceDataGenericsService.findBsEntityList(ReturnOrderReasonBsEntity.class);
			Map<String, ReturnOrderReasonBsEntity> returnReasonMap = helpForm.getReturnReasonMap();
			for (ReturnOrderReasonBsEntity returnReason : returnReasons) {
				String returnReasonCode = returnReason.getReturnOrderReasonCd();
				returnReasonMap.put(returnReasonCode, returnReason);
			}
			
			Locale locale = helpForm.getLocale();

			ReturnOrderReasonBsEntity railReturnReason = referenceDataGenericsService.findBsEntity(
					ReturnOrderReasonBsEntity.class, Constant.RETURN_ORDER_REASON_RAIL_UNLOADING_PROBLEM);
			helpForm.setReturnOrderReasonRailUnloadingProblem(railReturnReason);
			
			materialSettings.setSentToSapIsNullFlag(!LogonUtil.isSentToSap(order));
			
			SapBpEntity soldToBusinessPartner = order.getSoldtoSapBp();
			SapBpEntity soldTo = soldToBusinessPartner;
			String soldToIdentifier = soldTo.getSapId();
			materialSettings.setSoldtoSapId(StringUtils.nvl(soldToIdentifier));
			
			boolean isOneTimeSoldTo = soldTo.getTreatAsOnetimeFlag();
			setupSoldTo(order, materialSettings);
			
			//set ship to (cannot be null)
			SapBpEntity shipToBusinessPartner = order.getShiptoSapBp();
			Long shipToBusinessPartnerIdentifier = shipToBusinessPartner.getSapBpId();
			setupShipTo(order, materialSettings);
			
			DeliveryTypeBsEntity deliveryType = order.getDeliveryType();
			Long deliveryTypeCode = deliveryType.getDeliveryTypeCd();
			CarrierEntity commercialCarrier = order.getCommCarrier();
			ShippingpointEntity shippingPoint = order.getShippingpoint();
			if (Constant.ORD_DET_CARRIER_INFO_SET.contains(deliveryTypeCode)) {
				setupCommercialCarrier(materialSettings, commercialCarrier);
				CarrierEntity executingCarrier = order.getExecCarrier();
				setupExecutingCarrier(materialSettings, executingCarrier);
			} 
			else if (LogonUtil.isRailOrder(deliveryTypeCode)) {
				if (commercialCarrier != null) {
					SapVendorEntity commercialVendor = commercialCarrier.getSapVendor();
					String commercialVendorSapIdentifier = commercialVendor.getSapId();
					String displayName = commercialCarrier.getDisplayName();
					materialSettings.setCommCarrierName(StringUtils.nvl(commercialVendorSapIdentifier
							+ " " + displayName));
				}
				if (shippingPoint != null) {
					ShiptoEntity selectedShipTo = helpForm.getShipto();
					setupRailwayStations(order, selectedShipTo, materialSettings,
							shipToBusinessPartner, shippingPoint);
				}
			}
			
			ShiptoEntity shipTo = orderDetailService.loadShipto(shipToBusinessPartnerIdentifier);
			helpForm.setShipto(shipTo);
			
			Long soldToBusinessPartnerIdentifier = soldTo.getSapBpId();
			
			SareaEntity salesArea = order.getSarea();
			Long salesAreaIdentifier = salesArea.getSareaId();

			SapBpEntity billTo = order.getBilltoSapBp();
			if (shipTo != null) {
				Long shipToIdentifier = shipTo.getShiptoId();
				List<ShiptoBilltoEntity> billTos = orderDetailService.loadBilltos(shipToIdentifier,
						salesAreaIdentifier);
				setupLNFAdditionalInfo(materialSettings, referenceDataGenericsService, locale, shipTo, order);
				setupBillTo(helpForm, isOneTimeSoldTo, soldToBusinessPartnerIdentifier, billTo,
						billTos);
			}
			
			initSelectedBilltoAddress(materialSettings, billTo, soldToBusinessPartner, order,
					helpForm);
			
			setupPayer(helpForm, order, materialSettings, soldTo, soldToBusinessPartnerIdentifier,
					salesAreaIdentifier);
			
			OrderStatusBsEntity orderStatus = order.getOrderStatus();
			Long orderStatusCode = orderStatus.getOrderStatusCd();
			
			if (orderStatusCode == Constant.ORDER_STATUS_DELETED) {
				materialSettings.setDisabledBillToAndPayer(DISABLE_BILLTO_AND_PAYER);
			}
			else {
				materialSettings.setDisabledBillToAndPayer(ENABLE_BILLTO_AND_PAYER);
			}
			
			final Map<String, UomTxEntity> unitOfMeasureMap = buildUnitOfMeasureTextMap(locale);
			
			materialSettings.setSelOrderItemIndex(SELECTED_ORDER_ITEM_INDEX);
			List<OrderItemBean> orderItems = new ArrayList<OrderItemBean>();
			
			final List<OrderItemEntity> sortedOrderItems = OrderUtils.getOrderItemsFromOrderSorted(order);
			
			for (int orderItemIndex = 0; orderItemIndex < sortedOrderItems.size(); orderItemIndex++) {
				OrderItemBean orderItem = setupOrderItem(helpForm, order, materialSettings,
						orderDetailService, referenceDataGenericsService, locale, unitOfMeasureMap,
						orderItemIndex, sortedOrderItems);
				orderItems.add(orderItem);
			}
			
			materialSettings.setOrderItemList(orderItems);
			generateConvFactorsJson(materialSettings, orderItems);
			checkGoodsMovementsForLoad(SELECTED_ORDER_ITEM_INDEX, materialSettings);
			
			final Map<Long, String> deliveryNoteMap = loadDeliveryNoteMap(order, locale);
			materialSettings.setDeliveryNoteMap(deliveryNoteMap);
			
			helpForm.setMaterialTabOpened(MATERIAL_TAB_IS_OPENED);
			helpForm.setMaterialBean(materialSettings);
			
			String shippingPlantCode = null;
			Long shippingPointId = null;
			if (shippingPoint != null) {
				PlantBsEntity shippingPlant = shippingPoint.getPlant();
				shippingPlantCode = shippingPlant.getPlantCd();
				shippingPointId = shippingPoint.getShippingpointId();
			}
			initFirstMaterialItemDependentLists(helpForm, shippingPlantCode, shippingPointId);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	public void setupPayer(OrderDetailForm helpForm, OrderEntity order,
			MaterialTabBean materialSettings, SapBpEntity soldTo,
			Long soldToBusinessPartnerIdentifier, Long salesAreaIdentifier) {
		DispokarteService dispokarteService = getDispokarteService();
		List<SapBpEntity> payers = dispokarteService.loadPayers(salesAreaIdentifier,
				soldToBusinessPartnerIdentifier);
		
		SapBpEntity payerSapBusinessPartner = order.getPayerSapBp();
		if (payers.isEmpty() && payerSapBusinessPartner != null) {
			if (!containsBusinessPartner(payers, payerSapBusinessPartner)) {
				payers.add(payerSapBusinessPartner);
			}
		}
		
		Map<Long, PayerSapBpBean> payerMap = populatePayerSapBpBean(order, soldTo, payers);
		helpForm.setPayerMap(payerMap);
		
		initSelectedPayerAddress(materialSettings, payerSapBusinessPartner, soldTo,
				order, helpForm);
	}

	public OrderItemBean setupOrderItem(OrderDetailForm helpForm, OrderEntity order,
			MaterialTabBean materialSettings, OrderDetailService orderDetailService,
			RefDataGenericsService referenceDataGenericsService, Locale locale,
			final Map<String, UomTxEntity> unitOfMeasureMap, int orderItemIndex,
			List<OrderItemEntity> sortedOrderItems) {
		OrderItemEntity orderItem = sortedOrderItems.get(orderItemIndex);
		OrderStatusBsEntity orderStatus = order.getOrderStatus();
		Long orderStatusCode = orderStatus.getOrderStatusCd();
		DeliveryTypeBsEntity deliveryType = order.getDeliveryType();
		Long deliveryTypeCode = deliveryType.getDeliveryTypeCd();
		ShippingpointEntity shippingPoint = order.getShippingpoint();
		OrderItemBean orderItemSettings = new OrderItemBean();
		SapMaterialEntity sapMaterial = orderItem.getSapMaterial();
		Long sapMaterialId = sapMaterial.getSapMaterialId();
		
		DeliverySetting deliverySetting  = new OrderItemDeliverySetting(order, orderItem);
		DeliverySettingSalesAreaMap salesAreaMap = helpForm.getSalesAreaMap();
		final DeliveryOptionConfigurationDTO deliveryOptionConfiguration = 
				DeliverySettingFilterLogic.getDeliveryOptionConfiguration(deliverySetting, salesAreaMap);
		
		orderItemSettings.setOptionConfiguration(deliveryOptionConfiguration);
		
		orderItemSettings.setUomMap(unitOfMeasureMap);
		
		//check if it is a bag order
		MaterialPackageBsEntity materialPackage = sapMaterial.getMaterialPackage();
		String materialPackageCode = materialPackage.getMaterialPackageCd();
		if (Constant.MATERIAL_PACKAGE_BAG.equals(materialPackageCode)) {
			helpForm.setBagOrderFlag(Constant.YES);
		}

		if (helpForm.getPlanningMaterialContractItemId() == null) {
			setPlanningMaterialBeanAttributes(orderItem, orderItemSettings, helpForm);
		}
		
		Long orderItemIdentifier = orderItem.getOrderItemId();
		if (orderItemIndex == 0) {
			materialSettings.setSelOrderItemId(orderItemIdentifier);
		}

		if (orderItemIdentifier != null) {
			orderItemSettings.setOrderItemId(orderItemIdentifier);
			orderItemSettings.setIsNew(Boolean.FALSE);
		}
		else {
			orderItemSettings.setOrderItemId(-1L * sortedOrderItems.indexOf(orderItem));
			orderItemSettings.setIsNew(Boolean.TRUE);
		}
		Long orderItemNumber = orderItem.getOrderItemNo();
		orderItemSettings.setOrderItemNo(orderItemNumber.toString());
		
		if (orderDetailService.isRailReturnOrRepairOrder(order)) {
			orderItemSettings.setIsRailReturnRepair(Boolean.TRUE);
		}
		
		SapContractItemEntity contractItem = orderItem.getSapContractItem();
		if (contractItem != null) {
			Long contractItemNumber = contractItem.getContractItemNo();
			orderItemSettings.setContractItemNo(contractItemNumber);
			Long contractItemIdentifier = contractItem.getSapContractItemId();
			orderItemSettings.setSapContractItemId(contractItemIdentifier);
		}
		
		PlantBsEntity productionPlant = orderItem.getProductionPlant();
		if (productionPlant != null) {
			String plantCode = productionPlant.getPlantCd();
			orderItemSettings.setPlantCd(plantCode);
			String plantName = productionPlant.getPlantName();
			orderItemSettings.setPlantName(plantName);
		}
		else {
			orderItemSettings.setPlantCd(Constant.INVALID_OBJECT_ID_STR);
			orderItemSettings.setPlantName(Constant.NO_DATA);
		}
		
		List<PlantBsEntity> productionPlants = orderDetailService.loadSapMaterialPlantList(
				sapMaterialId, true);
		orderItemSettings.setProdPlantList(productionPlants);
		orderItemSettings.setSapMaterialId(sapMaterialId);
		String sapMaterialNumber = sapMaterial.getSapMaterialNo();
		orderItemSettings.setSapMaterialNo(sapMaterialNumber);
		
		//initialization for planning material, where production plant is still empty
		materialSettings.setMixDesignAggRmxMapJson("{}");
		// MixDesign
		if (productionPlant != null) {
			List<FireqMaterialMixdesignEntity> fireqMatMixdesignList = getServiceLocator().getOrderDetailService().fetchMixDesignList(
					productionPlant.getPlantCd(), sapMaterialId);
			HashMap<Long, String> mixDesignAggRmxMap = new HashMap<Long, String>();
			for (FireqMaterialMixdesignEntity mixDesign : fireqMatMixdesignList) {
				Long mixDesignId = mixDesign.getMixDesignId();
				String aggRmxCode = mixDesign.getAggRmxCode();
				mixDesignAggRmxMap.put(mixDesignId, aggRmxCode);
			}
			materialSettings.setMixDesignAggRmxMap(mixDesignAggRmxMap);
			JSONSerializer jsonSerializer = JsonUtils.getJsonSerializer();
			materialSettings.setMixDesignAggRmxMapJson(jsonSerializer.serialize(
					mixDesignAggRmxMap));

			//get mixdesign list / property
			orderItemSettings.setFireqMatMixdesignList(fireqMatMixdesignList);
			FireqMaterialMixdesignEntity mixDesign = orderItem.getMixDesign();
			if (mixDesign != null) {
				Long mixDesignId = mixDesign.getMixDesignId();
				orderItemSettings.setMixDesignId(mixDesignId);
				orderItemSettings.setAggRmxCode(mixDesign.getAggRmxCode());
			}
		}
		
		//get material description 
		SapMaterialDescriptionEntity loadMaterialDesc = orderDetailService.loadMaterialDesc(sapMaterialId, locale.getLanguage());
		orderItemSettings.setSapMaterialName(loadMaterialDesc.getTxt());
		
		//get material package description
		if (materialPackage != null) {
			MaterialPackageTxEntity matPackTxt = referenceDataGenericsService.findTxEntity(MaterialPackageTxEntity.class, locale, materialPackage);
			orderItemSettings.setMaterialPackageCd(materialPackageCode);
			orderItemSettings.setMaterialPackageName(matPackTxt.getTxt());
			orderItemSettings.setLogonPackageTypeCd(materialPackage.getLogonPackageType() != null ? materialPackage.getLogonPackageType().getPackageTypeCd()
					: null);
		}
		
		// load LS-info
		String plantCode = orderItemSettings.getPlantCd();
		if (plantCode != null) {
			MatPlantEntity matNorm = orderDetailService.loadMaterialNorm(sapMaterialId,
					plantCode);
			if (matNorm != null) {
				orderItemSettings.setMaterialNorm(matNorm.getNormDesc());
				orderItemSettings.setMaterialCENorm(matNorm.getLogo1Z1());
				orderItemSettings.setCertification(matNorm.getLogo1Z2());
				orderItemSettings.setCpdNumber(matNorm.getCpdNumber());
			}
		}
		
		//add ordered quantity 
		BigDecimal orderedQuantity = orderItem.getOrderedQuantity();
		if (orderedQuantity != null) {
			String orderedQuantityLabel = orderedQuantity.toString();
			orderItemSettings.setOrderedQuantity(orderedQuantityLabel);
		}
		UomBsEntity orderedUnitOfMeasure = orderItem.getOrderedUom();
		if (orderedUnitOfMeasure != null) {
			String orderedUnitOfMeasureCode = orderedUnitOfMeasure.getUomCd();
			orderItemSettings.setOrderedUom(orderedUnitOfMeasureCode);
		}
		
		//add loaded quantity
		BigDecimal loadedQuantity = orderItem.getLoadedQuantity();
		if (loadedQuantity != null){
			String loadedQuantityLabel = loadedQuantity.toString();
			orderItemSettings.setLoadedQuantity(loadedQuantityLabel);
		}
		
		UomBsEntity loadedUnitOfMeasure = orderItem.getLoadedUom();
		
		if (loadedUnitOfMeasure != null) {
			String loadedUomCd = loadedUnitOfMeasure.getUomCd();
			orderItemSettings.setLoadedUom(loadedUomCd);
		}
		
		//add returned quantity
		ReturnOrderReasonBsEntity returnOrderReason = orderItem.getReturnOrderReason();
		if (returnOrderReason != null){
			String returnOrderReasonCode = returnOrderReason.getReturnOrderReasonCd();
			orderItemSettings.setReturnReason(returnOrderReasonCode);
		}
		PlantBsEntity returnPlant = orderItem.getReturnPlant();
		orderItemSettings.setReturnPlant(returnPlant);
		ShippingpointEntity returnShippingpoint = orderItem.getReturnShippingpoint();
		orderItemSettings.setReturnShippingpoint(returnShippingpoint);
		ReturnMaterialUsageBsEntity returnMaterialUsage = orderItem.getReturnMaterialUsage();
		orderItemSettings.setReturnMaterialUsage(returnMaterialUsage);
		BigDecimal returnedQuantity = orderItem.getReturnedQuantity();
		if (returnedQuantity != null){
			String returnedQuantityLabel = returnedQuantity.toString();
			orderItemSettings.setReturnedQuantity(returnedQuantityLabel);
		}
		UomBsEntity returnedUnitOfMeasure = orderItem.getReturnedUom();
		
		if (returnedUnitOfMeasure != null) {
			String returnedUnitOfMeasureCode = returnedUnitOfMeasure.getUomCd();
			
			orderItemSettings.setReturnedUom(returnedUnitOfMeasureCode);
		}
		else if (loadedUnitOfMeasure != null) {
			// Returned UoM must be the same as Loaded UoM anyway. Will be cleared in
			// "OrderDetailForm.validateOrderDetail" if quantity is empty.
			String loadedUnitOfMeasureCode = loadedUnitOfMeasure.getUomCd();
			orderItemSettings.setReturnedUom(loadedUnitOfMeasureCode);
		}
		
		ReturnQuantHandlingBsEntity orderItemReturnQuantHandling = orderItem.getReturnQuantHandling();
		Long returnQuantityHandlingCode = null;
		
		if (orderItemReturnQuantHandling == null) {
			returnQuantityHandlingCode = Constant.RETURN_QUANT_HANDLING_NOT_SET;
		}
		else {
			returnQuantityHandlingCode = orderItemReturnQuantHandling.getReturnQuantHandlingCd();
		}
		
		orderItemSettings.setReturnQuantHandling(returnQuantityHandlingCode);
		
		if (LogonUtil.isReturnPalletOnly(deliveryTypeCode) && !LogonPackageHandlingUtil.isService(orderItem)) {
			orderItemSettings.setIsCreditNoteDisabled(Constant.ON);
			orderItemSettings.setIsReturnedQuantityDisabled(Constant.ON);
		}
		//CN checkbox editable only as of status finished if no credit note was granted so far and return quantity is empty
		else if (LogonUtil.isSentToSap(order)) {
			if (returnQuantityHandlingCode == Constant.RETURN_QUANT_HANDLING_NOT_SET) {
				orderItemSettings.setCreditNoteFlag(Constant.OFF);
				orderItemSettings.setIsCreditNoteDisabled(Constant.NO);
				orderItemSettings.setIsReturnedQuantityDisabled(Constant.ON);
			}
			else if (returnQuantityHandlingCode == Constant.RETURN_QUANT_HANDLING_NO_ADJUSTMENT) {
				if ((returnedQuantity == null) || (returnedQuantity.compareTo(BigDecimal.ZERO) == 0)) {
					orderItemSettings.setCreditNoteFlag(Constant.OFF);
					orderItemSettings.setIsCreditNoteDisabled(Constant.NO);
					orderItemSettings.setIsReturnedQuantityDisabled(Constant.YES);
				}
			}
			else if (returnQuantityHandlingCode == Constant.RETURN_QUANT_HANDLING_CREDIT_NOTE_NEEDED) {//CN needed
				orderItemSettings.setCreditNoteFlag(Constant.ON);
				orderItemSettings.setIsCreditNoteDisabled(Constant.NO);
			}
			else if (returnQuantityHandlingCode == Constant.RETURN_QUANT_HANDLING_CREDIT_NOTE_CREATED) {
				orderItemSettings.setCreditNoteFlag(Constant.ON);
				orderItemSettings.setIsCreditNoteDisabled(Constant.YES);
			}
			else if (returnQuantityHandlingCode == Constant.RETURN_QUANT_HANDLING_ADJUSMENT_NEEDED && orderItemSettings.getReturnedQuantity() == null) {
				orderItemSettings.setIsCreditNoteDisabled(Constant.NO);
			}
		}
		else if (LogonStatusUtils.isBeforeLoaded(orderStatus)) {
			orderItemSettings.setIsReturnedQuantityDisabled(Constant.YES);
		}
		
		BigDecimal deliveredQuantity = orderItem.getDeliveredQuantity();
		UomBsEntity deliveredUnitOfMeasure = orderItem.getDeliveredUom();
		
		if (deliveredQuantity != null) {
			String deliveredQuantityLabel = deliveredQuantity.toString();
			orderItemSettings.setDeliveredQuantity(deliveredQuantityLabel);
		}
		if (deliveredUnitOfMeasure != null) {
			String deliveredUnitOfMeasureCode = deliveredUnitOfMeasure.getUomCd();
			orderItemSettings.setDeliveredUom(deliveredUnitOfMeasureCode);
		}
		
		initLoadedQtyConvFactors(orderItemSettings, sapMaterial, deliveredUnitOfMeasure);
		
		LoadingpointEntity loadingPoint = orderItem.getLoadingpoint();
		if (loadingPoint != null) {
			Long loadingPointId = loadingPoint.getLoadingpointId();
			orderItemSettings.setLoadingpointId(loadingPointId);
		}
		LoadEntity load = orderItem.getLoad();
		if (load != null) {
			setupLoad(orderDetailService, shippingPoint, unitOfMeasureMap,
					orderItemSettings, sapMaterial, loadedUnitOfMeasure, load);
		}
		else {
			if (Arrays.asList(orderStatusesConsideredAsLoadingStarted).contains(
					orderStatusCode)) {
				orderItemSettings.setTareNetWeightsAreNotNullFlag(Constant.ON);
			}
			else {
				orderItemSettings.setTareNetWeightsAreNotNullFlag(Constant.OFF);
			}
			orderItemSettings.setOrderItemAndLoadRel11Flag(Constant.OFF);
		}
		
		if (orderItem.getShiptoSilo() != null) {
			setupSilo(referenceDataGenericsService, locale, orderItem, orderItemSettings);
		}
		
		OrderItemEntity bimodalOrderItem = orderItem.getBimodalRailOrderItem();
		if (deliveryTypeCode == Constant.DELIVERY_TYPE_BIMODAL_ROAD
				&& bimodalOrderItem != null) {
			setupBimodalTruckOrder(orderDetailService,
					unitOfMeasureMap, orderItemSettings, bimodalOrderItem);
		}
		
		if (deliveryTypeCode == Constant.DELIVERY_TYPE_BIMODAL_RAIL
				&& orderItemIdentifier != null) {
			List<BimodalTruckOrderItemDTO> bimodalTruckOrderItems 
				= orderDetailService.loadBimodalTruckList(orderItemIdentifier, locale.getLanguage());
			orderItemSettings.setBimodalTruckOrderItems(bimodalTruckOrderItems);
		}
		
		boolean serviceMaterial = LogonPackageHandlingUtil.isService(orderItem);
		orderItemSettings.setServiceMaterial(serviceMaterial);
		if (serviceMaterial) {
			materialSettings.setHasServiceMaterial(serviceMaterial);
		}
		return orderItemSettings;
	}

	public void setupRailwayStations(OrderEntity order, ShiptoEntity selectedShipTo,
			MaterialTabBean materialSettings, SapBpEntity shipToBusinessPartner,
			ShippingpointEntity shippingPoint) {
		RailwayStationEntity departureStation = order.getActualDepRailwayStation();
		if (departureStation != null) {
			materialSettings.setRailDepUicCountryCd(departureStation.getUicCountry().getUicCountryCd().toString());
			materialSettings.setRailDepStationNo(departureStation.getRailwayStationNo());
			materialSettings.setRailDepStationName(departureStation.getRailwayStationName());
			materialSettings.setRailDepStationLP(StringUtils.nvl(shippingPoint.getRailwayStationLoadingPoint()));
		}
		RailwayStationEntity arrivalStation = shipToBusinessPartner.getRailwayStation();
		if (arrivalStation != null) {
			materialSettings.setRailArrUicCountryCd(arrivalStation.getUicCountry().getUicCountryCd().toString());
			materialSettings.setRailArrStationNo(arrivalStation.getRailwayStationNo());
			materialSettings.setRailArrStationName(arrivalStation.getRailwayStationName());
			materialSettings.setRailArrStationUP(StringUtils.nvl(selectedShipTo.getTrainStationUnloadingPoint()));
		}
	}

	public void setupShipTo(OrderEntity order, MaterialTabBean materialSettings) {

		final BusinessPartner businessPartnerData = BusinessPartnerUtil.getShiptoBusinessPartner(order);

		materialSettings.setShiptoName(businessPartnerData.getFirstName() + " " + businessPartnerData.getLastName());
		materialSettings.setShiptoStreet(businessPartnerData.getStreet());
		materialSettings.setShiptoHouseNumber(businessPartnerData.getHouseNumber());
		materialSettings.setShipToStreetHouseNumber(businessPartnerData.getStreetHouseNumber());
		materialSettings.setShiptoCountry(businessPartnerData.getSapCountryCd());
		materialSettings.setShiptoPostalCode(businessPartnerData.getPostalCode());
		materialSettings.setShiptoCity(businessPartnerData.getCity());
		materialSettings.setShipToVatNumber(businessPartnerData.getVatRegistrationNumber());
			
	}

	public void setupSoldTo(OrderEntity order, MaterialTabBean materialSettings) {
		final BusinessPartner businessPartnerData = BusinessPartnerUtil.getSoldtoBusinessPartner(order);

		materialSettings.setSoldtoSapId(businessPartnerData.getSapId());
		materialSettings.setSoldtoName(businessPartnerData.getFirstName() + " " + businessPartnerData.getLastName());
		materialSettings.setSoldtoStreet(businessPartnerData.getStreet());
		materialSettings.setSoldtoHouseNumber(businessPartnerData.getHouseNumber());
		materialSettings.setSoldToStreetHouseNumber(businessPartnerData.getStreetHouseNumber());		
		materialSettings.setSoldtoCountry(businessPartnerData.getSapCountryCd());
		materialSettings.setSoldtoPostalCode(businessPartnerData.getPostalCode());
		materialSettings.setSoldtoCity(businessPartnerData.getCity());
		materialSettings.setSoldToVatNumber(businessPartnerData.getVatRegistrationNumber());
	}

	public void setupLoad(OrderDetailService orderDetailService, ShippingpointEntity shippingPoint,
			final Map<String, UomTxEntity> unitOfMeasureMap, OrderItemBean orderItemSettings,
			SapMaterialEntity sapMaterial, UomBsEntity loadedUnitOfMeasure, LoadEntity load) {
		Long loadId = load.getLoadId();
		String loadIdLabel = loadId.toString();
		orderItemSettings.setLoadId(loadIdLabel);

		Long loadOrderId = load.getLoadOrderId();
		if (loadOrderId != null) {
			String loadOrderIdLabel = loadOrderId.toString();
			orderItemSettings.setParallelVerladId("/ " + loadOrderIdLabel);
		}

		LoadingpointEntity loadingPoint = load.getLoadingpoint();
		if (loadingPoint != null) {
			Long loadingPointId = loadingPoint.getLoadingpointId();
			orderItemSettings.setLoadingpointId(loadingPointId);
		}

		BigDecimal netQuantity = load.getNetQuantity();
		UomBsEntity unitOfMeasure = load.getNetQuantityUom();

		if (netQuantity != null) {
			String netQuantityLabel = netQuantity.toString();
			orderItemSettings.setNetWeightQuantity(netQuantityLabel);
		}

		if (unitOfMeasure != null) {
			String unitOfMeasureCode = unitOfMeasure.getUomCd();
			if (Constant.UOM_TONS.equals(unitOfMeasureCode)) {
				orderItemSettings.setNetQuantityUom(Constant.UOM_TONS_TXT);
			}
			else {
				UomTxEntity unitOfMeasureText = unitOfMeasureMap.get(unitOfMeasureCode);
				String unitOfMeasureLabel = unitOfMeasureText.getTxt();
				orderItemSettings.setNetQuantityUom(unitOfMeasureLabel);
			}
			initNetQtyConvFactors(orderItemSettings, sapMaterial, loadedUnitOfMeasure,
					unitOfMeasure);
		}

		ContainerEntity container = load.getContainer();
		if (container != null) {
			String containerNumber = container.getContainerNo();
			orderItemSettings.setContainerNo(StringUtils.nvl(containerNumber));
			String containerName = container.getContainerName();
			orderItemSettings.setContainerName(StringUtils.nvl(containerName));
		}

		/*
		 * Check if loaded quantity and Tare/Gross/Net fields are editable.
		 * 
		 * According to Roland, normally they should not be edited here, but there seem
		 * to be a need for a workaround when they have remaining quantity on the truck,
		 * because the LDS cannot handle it correctly.
		 *
		 * Current rules:
		 *
		 * 1) the loaded quantity and uom is editable if tare/net weights are NOT null,
		 *	but there is NO 1:1 relation between the order and the load
		 * 2) the weighing / tare quantity and gross quantity are editable if
		 *	(tare/net weights are NOT null, and order/load has a 1:1 relation)
		 *	AND material package is NOT bag
		 * 3) the net quantity is only editable if (tare/net weights are NOT null,
		 *	and order/load has a 1:1 relation).
		 * 
		 * TODO 20-Nov-2012/romek: Rename setTareNetWeightsAreNotNullFlag so that
		 *	 even the gross weight cannot be null now.
		 */
		BigDecimal tareWeightInTons = load.getTareWeightT();
		BigDecimal grossWeightInTons = load.getGrossWeightT();

		if ((tareWeightInTons != null) && (grossWeightInTons != null) && (netQuantity != null)) {
			orderItemSettings.setTareNetWeightsAreNotNullFlag(Constant.ON);
			String tareWeightInTonsLabel = tareWeightInTons.toPlainString();
			orderItemSettings.setTareWeightQuantity(tareWeightInTonsLabel);
			String grossWeightInTonsLabel = grossWeightInTons.toPlainString();
			orderItemSettings.setGrossWeightQuantity(grossWeightInTonsLabel);

			boolean loadMappedToExactlyOneOrderItem = orderDetailService.isLoadMappedToExactlyOneOrderItem(loadId);
			orderItemSettings.setOrderItemAndLoadRel11Flag(loadMappedToExactlyOneOrderItem);

		}
		else {
			orderItemSettings.setTareNetWeightsAreNotNullFlag(Constant.OFF);
		}

		AssignmentPartEntity loadAssignmentPart = orderDetailService.loadAssignmentPart(loadId);

		if (loadAssignmentPart != null) {
			ShippingpointEntity loadingShippingPoint = loadAssignmentPart.getShippingpoint();
			if (loadingShippingPoint != null && shippingPoint != null) {
				Long loadingShippingPointId = loadingShippingPoint.getShippingpointId();
				Long shippingPointId = shippingPoint.getShippingpointId();
				if (!loadingShippingPointId.equals(shippingPointId)) {
					String loadingShippingPointName = loadingShippingPoint.getShippingpointName();
					orderItemSettings.setCarrierStock(loadingShippingPointName);
				}
			}
		}
	}

	public void setupLNFAdditionalInfo(MaterialTabBean materialSettings,
			RefDataGenericsService referenceDataGenericsService, Locale locale,
			ShiptoEntity shipTo, OrderEntity order) {
		ShiptoTypeBsEntity shiptoType = shipTo.getShiptoType();
		Long shiptoTypeCode = shiptoType.getShiptoTypeCd();
		ShiptoTypeTxEntity shipToType = referenceDataGenericsService.findTxEntity(ShiptoTypeTxEntity.class, locale, 
				shiptoTypeCode);
		String shipToTypeText = shipToType.getTxt();
		materialSettings.setShiptoSapId(shipTo.getShiptoSapBp() != null ? shipTo.getShiptoSapBp().getSapId() : "");
		materialSettings.setShiptoType(shipToTypeText);
		materialSettings.setShiptoTypeCd(shiptoTypeCode);
		if (shiptoTypeCode == Constant.SHIPTO_TYPE_LOCATION_NOT_FIXED) {
			String locationNotFixedSiteName = order.getLnfSiteName();
			String locationNotFixedSiteHouseNumber = order.getLnfSiteStreetHouseNo();
			String locationNotFixedSiteCountry = order.getLnfSiteCountry();
			String locationNotFixedSitePostalCode = order.getLnfSitePostalCode();
			String locationNotFixedSiteCity = order.getLnfSiteCity();
			String locationNotFixed = StringUtils.nvl(locationNotFixedSiteName) + "/"
					+ StringUtils.nvl(locationNotFixedSiteHouseNumber) + "/"
					+ StringUtils.nvl(locationNotFixedSiteCountry) + " "
					+ StringUtils.nvl(locationNotFixedSitePostalCode) + " "
					+ StringUtils.nvl(locationNotFixedSiteCity);
			materialSettings.setLocationNotFixed(locationNotFixed);
		}
	}

	public void setupBillTo(OrderDetailForm helpForm, boolean treatAsOneTime,
			Long soldToBusinessPartnerIdentifier, SapBpEntity billTo,
			List<ShiptoBilltoEntity> billTos) {
		Map<String, ShiptoBilltoBean> billToMap = new HashMap<String, ShiptoBilltoBean>();
		OrderEntity order = helpForm.getOrder();
		String oneTimeSoldToName = order.getOneTimeSoldtoName();
		for (ShiptoBilltoEntity currentBillTo : billTos) {
			SapBpEntity billToSapBusinessPartner = currentBillTo.getBilltoSapBp();
			ShiptoBilltoBean shipToBillToSettings = getShipToBillToBean(treatAsOneTime,
					oneTimeSoldToName, soldToBusinessPartnerIdentifier,
					billToSapBusinessPartner);
			Long billToSapBusinessPartnerIdentifier = billToSapBusinessPartner.getSapBpId();
			String billToSapBusinessPartnerIdentifierAsString = billToSapBusinessPartnerIdentifier.toString();
			billToMap.put(billToSapBusinessPartnerIdentifierAsString, shipToBillToSettings);
		}
		
		// Enforce that the order's billTo is added to the list; can only do if the order has a billTo
		if (billTos.isEmpty() && billTo != null) {
			ShiptoBilltoBean shipToBillToSettings = getShipToBillToBean(treatAsOneTime,
					oneTimeSoldToName, soldToBusinessPartnerIdentifier, billTo);
			Long billToSapBusinessPartnerIdentifier = billTo.getSapBpId();
			String billToSapBusinessPartnerIdentifierAsString = billToSapBusinessPartnerIdentifier.toString();
			billToMap.put(billToSapBusinessPartnerIdentifierAsString, shipToBillToSettings);
		}
		
		helpForm.setBilltoMap(billToMap);
	}

	public void setupSilo(RefDataGenericsService referenceDataGenericsService, Locale locale,
			OrderItemEntity orderItem, OrderItemBean orderItemSettings) {
		ShiptoSiloEntity shipToSilo = orderItem.getShiptoSilo();
		String siloName = shipToSilo.getSiloName();
		orderItemSettings.setSiloName(StringUtils.nvl(siloName));
		
		String siloVerificationTypeLabel = Constant.NO_DATA;
		SiloVerifTypeBsEntity siloVerificationType = orderItem.getShiptoSiloVerifType();
		if (siloVerificationType != null) {
			SiloVerifTypeTxEntity siloVerificationTypeText = 
					referenceDataGenericsService.findTxEntity(SiloVerifTypeTxEntity.class, locale, siloVerificationType);
			if (siloVerificationTypeText != null) {
				siloVerificationTypeLabel = siloVerificationTypeText.getTxt();
			}
		}
		orderItemSettings.setSiloVerificationType(siloVerificationTypeLabel);
	}

	public void setupBimodalTruckOrder(OrderDetailService orderDetailService,
			final Map<String, UomTxEntity> unitOfMeasureMap, OrderItemBean orderItemSettings,
			OrderItemEntity bimodalTruckOrderItem) {
		OrderEntity bimodalTruckOrder = bimodalTruckOrderItem.getOrder();
		Long bimodalTruckOrderId = bimodalTruckOrder.getOrderId();
		orderItemSettings.setBimodalTruckOrderId(bimodalTruckOrderId.toString());
		Long bimodlTruckOrderNumber = bimodalTruckOrder.getOrderNumber();
		orderItemSettings.setBimodalTruckOrderNo(bimodlTruckOrderNumber.toString());

		if (orderDetailService.loadOrder(bimodalTruckOrderId) != null) {
			orderItemSettings.setAuthorizedBimodalTruckOrder(true);
		}

		LoadEntity load = bimodalTruckOrderItem.getLoad();
		if (load != null) {
			Date loadingTimeStamp = load.getLoadingTs();
			if (loadingTimeStamp != null) {
				BigDecimal netQuantity = load.getNetQuantity();
				UomBsEntity unitOfMeasure = load.getNetQuantityUom();
				String netWeight = Constant.NO_DATA;
				if (netQuantity != null) {
					netWeight = netQuantity.toPlainString();
				}
				String unitOfMeasureLabel = Constant.NO_DATA;
				if (unitOfMeasure != null) {
					String unitOfMeasureCode = unitOfMeasure.getUomCd();
					UomTxEntity unitOfMeasureText = unitOfMeasureMap.get(unitOfMeasureCode);
					if (unitOfMeasureText != null) {
						unitOfMeasureLabel = unitOfMeasureText.getTxt();
					}
				}
				orderItemSettings.setBimodalTruckNetWeight(netWeight + " " + unitOfMeasureLabel);
			}
		}

		AssignmentEntity assignment = orderDetailService.loadAssignment(bimodalTruckOrderId);
		if (assignment != null) {
			WagonEntity wagon = assignment.getWagon();
			if (wagon != null) {
				String wagonNumber = wagon.getWagonNo();
				String wagonNumberToDisplay = WagonNumberUtils.toDisplayNo(wagonNumber);
				orderItemSettings.setBimodalTruckWagonNo(wagonNumberToDisplay);
			}
		}
	}

	public void setupCommercialCarrier(MaterialTabBean materialSettings,
			CarrierEntity commercialCarrier) {
		if (commercialCarrier == UNSET_CARRIER) {
			return;
		}
		SapVendorEntity vendor = commercialCarrier.getSapVendor();
		String vendorIdentifier = vendor.getSapId();
		materialSettings.setCommCarrierVendorSapId(StringUtils.nvl(vendorIdentifier));
		String lastName = vendor.getName1();
		String firstName = vendor.getName2();
		materialSettings.setCommCarrierName(StringUtils.nvl(lastName) + " "
				+ StringUtils.nvl(firstName));
		String street = vendor.getStreet();
		materialSettings.setCommCarrierStreet(StringUtils.nvl(street));
		String houseNumber = vendor.getHouseNumber();
		materialSettings.setCommCarrierHouseNumber(StringUtils.nvl(houseNumber));
		SapCountryBsEntity country = vendor.getCountry();
		String countryCode = Constant.NO_DATA;
		if (country != null) {
			countryCode = country.getSapCountryCd();
		}
		materialSettings.setCommCarrierCountry(countryCode);
		String postalCode = vendor.getPostalCode();
		materialSettings.setCommCarrierPostalCode(StringUtils.nvl(postalCode));
		String city = vendor.getCity();
		materialSettings.setCommCarrierCity(StringUtils.nvl(city));
	}

	public void setupExecutingCarrier(MaterialTabBean materialSettings,
			CarrierEntity executingCarrier) {
		if (executingCarrier != null) {
			SapVendorEntity vendor = executingCarrier.getSapVendor();
			String vendorSapIndentifier = vendor.getSapId();
			materialSettings.setExecCarrierVendorSapId(StringUtils.nvl(vendorSapIndentifier));
			String lastName = vendor.getName1();
			String firstName = vendor.getName2();
			materialSettings.setExecCarrierName(StringUtils.nvl(lastName) + " "
					+ StringUtils.nvl(firstName));
			String street = vendor.getStreet();
			materialSettings.setExecCarrierStreet(StringUtils.nvl(street));
			String houseNumber = vendor.getHouseNumber();
			materialSettings.setExecCarrierHouseNumber(StringUtils.nvl(houseNumber));
			SapCountryBsEntity country = vendor.getCountry();
			String countryCode = Constant.NO_DATA;
			if (country != null) {
				countryCode = country.getSapCountryCd();
			}
			materialSettings.setExecCarrierCountry(countryCode);
			String postalCode = vendor.getPostalCode();
			materialSettings.setExecCarrierPostalCode(StringUtils.nvl(postalCode));
			String city = vendor.getCity();
			materialSettings.setExecCarrierCity(StringUtils.nvl(city));
		}
	}

	public static boolean containsBusinessPartner(List<SapBpEntity> businessPartners,
			SapBpEntity businessPartner) {
		for (SapBpEntity payer : businessPartners) {
			Long sapBusinessPartnerId = payer.getSapBpId();
			Long ordersPayerSapBusinessPartnerId = businessPartner.getSapBpId();
			if (sapBusinessPartnerId.equals(ordersPayerSapBusinessPartnerId)) {
				return Constant.CONTAINS;
			}
		}
		return Constant.DOES_NOT_CONTAIN;
	}
	
	/**
	 * Creates the delivery note file list - used on Order Detail. 
	 * 
	 * @param order The order for which to create delivery note map.
	 * @param locale The locale of the user.
	 * 
	 * @return A map in which the OutputdocId-s are associated with the 
	 * delivery note information (String).
	 */
	protected Map<Long, String> loadDeliveryNoteMap(OrderEntity order, Locale locale) {
		final List<OrderOutputdocEntity> deliveryNoteList = getOrderService().loadDeliveryNoteList(
				order.getOrderId());
		final Map<Long, String> deliveryNoteMap = new LinkedHashMap<Long, String>();

		for (Iterator<OrderOutputdocEntity> iterator = deliveryNoteList.iterator(); iterator.hasNext();) {
			OrderOutputdocEntity orderDoc = iterator.next();
			if (orderDoc.getPdfOutputdoc() != null) {

				final StringBuilder deliveryNoteInfo = new StringBuilder();
				deliveryNoteInfo.append(getUserTimezone().fmtAsDayMonthYearHourMinute(
						orderDoc.getCtlCreTs()));

				final OrderStatusTxEntity orderStatus = getRefDataGenericsService().findTxEntity(
						OrderStatusTxEntity.class, locale, orderDoc.getGeneratedInOrderStatus());
				if (orderStatus != null) {
					deliveryNoteInfo.append(", ").append(orderStatus.getTxt());
				}

				/* LOGON-6197: Quick fix to allow display of both delivery note and export
				 * invoice. This depends on how the filename is set in
				 * PDFGeneratorServiceBean.generateAndStoreDocuments().
				 */
				final String fileName = orderDoc.getPdfOutputdoc().getOutputdocName();
				final boolean isExportInvoice = fileName.startsWith(Constant.DOC_NAME_EXPINV);
				final boolean isCashSales = fileName.startsWith(Constant.DOC_NAME_CASHSALES);
				if (isExportInvoice) {
					deliveryNoteInfo.append(" (").append(Constant.DOC_NAME_EXPINV).append(")");
				}
				else if (isCashSales){
					deliveryNoteInfo.append(" (").append(Constant.DOC_NAME_CASHSALES).append(")");
				}

				deliveryNoteMap.put(orderDoc.getPdfOutputdoc().getOutputdocId(),
						deliveryNoteInfo.toString());
			}
		}

		return deliveryNoteMap;
	}

	private void setPlanningMaterialBeanAttributes(OrderItemEntity orderItem, OrderItemBean orderItemSettings,
			OrderDetailForm orderDetailForm) {
		// [[
		Object[][] params = { { "orderItem", orderItem },
				{ "orderItemSettings", orderItemSettings },
							{"orderDetailForm", orderDetailForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setPlanningMaterialBeanAttributes", params);
		}
		// ]]
		try {
			boolean isMaterialEditable = false;
			if (LogonUtil.isPlanningMaterial(orderItem.getSapMaterial())) {
				isMaterialEditable = LogonUtil.isStillReplacablePlanningMaterial(orderItem.getOrder(), 
						orderItem.getSapMaterial());
				orderDetailForm.setPlanningMaterialContractItemId(orderItem.getSapContractItem().getSapContractItemId());
			}
			orderDetailForm.setPlanningMaterialStillEditable(isMaterialEditable);
			
			if (orderDetailForm.getPlanningMaterialContractItemId() != null && !LogonPackageHandlingUtil.isService(orderItem)) {
				List<PlanningMaterialBean> planningMaterialReplacementList = new ArrayList<PlanningMaterialBean>();
				
				List<SapContractItemEntity> replacingSapContractItemList = getMaterialEnhancementsService().
						getReplacingContractItemList(orderDetailForm.getPlanningMaterialContractItemId());
				for (SapContractItemEntity replacingSapContractItem : replacingSapContractItemList) {
					PlanningMaterialBean planningMaterialBean = new PlanningMaterialBean();
					
					SapMaterialEntity replacingSapMaterial = replacingSapContractItem.getSapMaterial();
					planningMaterialBean.setSapContractItemId(replacingSapContractItem.getSapContractItemId());
					
					Long replacingMaterialId = replacingSapMaterial.getSapMaterialId();
					planningMaterialBean.setSapMaterialId(replacingMaterialId);
					
					SapMaterialDescriptionEntity replacingMaterialNameDesc = getOrderDetailService().loadMaterialDesc(
							replacingMaterialId, orderDetailForm.getLocale().getLanguage());
					planningMaterialBean.setSapMaterialName(replacingMaterialNameDesc.getTxt());
					
					planningMaterialBean.setContractItemNo(replacingSapContractItem.getContractItemNo());
					
					List <PlantBsEntity> prodPlantList = getOrderDetailService().loadSapMaterialPlantList(replacingMaterialId, true);
					planningMaterialBean.setProdPlantList(prodPlantList);
					
					PlantBsEntity prodPlant = getMaterialEnhancementsService().getDefaultProductionPlant(replacingSapContractItem);
					
					if (prodPlant != null && !prodPlantList.isEmpty() && prodPlantList.contains(prodPlant)) {
						planningMaterialBean.setPlantCd(prodPlant.getPlantCd());
						planningMaterialBean.setPlantName(prodPlant.getPlantName());
					}
					
					planningMaterialReplacementList.add(planningMaterialBean);
				}
				orderDetailForm.setPlanningMaterialReplacementList(planningMaterialReplacementList);
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
	 * Returns a {@link ShiptoBilltoBean}. The name will be altered if the soldTo == shipTo
	 * @param treatAsOneTime
	 * @param oneTimeSoldToName
	 * @param soldToSapBusinessPartnerId
	 * @param billToSapBusinessPartner
	 * @return
	 */
	public ShiptoBilltoBean getShipToBillToBean(boolean treatAsOneTime, String oneTimeSoldToName,
			Long soldToSapBusinessPartnerId, SapBpEntity billToSapBusinessPartner) {
		boolean useAlteredName = false;
		if (treatAsOneTime) {
			if (soldToSapBusinessPartnerId.equals(billToSapBusinessPartner.getSapBpId())) {
				useAlteredName = true;
			}
		}
		ShiptoBilltoBean shipToBillToBean = new ShiptoBilltoBean(billToSapBusinessPartner, useAlteredName, oneTimeSoldToName);
		return shipToBillToBean;
	}

	/**
	 * @param order
	 * @param soldtoSapBp
	 * @param payerList
	 * @return
	 */
	public Map<Long, PayerSapBpBean> populatePayerSapBpBean(OrderEntity order,
			SapBpEntity soldtoSapBp, List<SapBpEntity> payerList) {
		Map<Long, PayerSapBpBean> payerMap = new HashMap<Long, PayerSapBpBean>();
		
		for (SapBpEntity payerSapBp: payerList) {
			boolean useAlteredName = false;
			if (soldtoSapBp.getTreatAsOnetimeFlag().booleanValue()) {
				if (soldtoSapBp.getSapBpId().equals(payerSapBp.getSapBpId())) {
					useAlteredName = true;
				}
			}
			PayerSapBpBean payerSapBpBean = new PayerSapBpBean(payerSapBp, useAlteredName, order.getOneTimeSoldtoName());
			payerMap.put(payerSapBp.getSapBpId(), payerSapBpBean);
		}
		return payerMap;
	}

	/**	Initializes services and extra cost tab of Order Detail screen.  
	 * @param helpForm
	 */
    public void initServicesExtraCostTab(OrderDetailForm helpForm, Locale locale) {    	
		// [[
		Object[][] params = {{"helpForm", helpForm},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initServicesExtraCostTab", params);
		}
		// ]]
		try {
			/**
			 * The sales area map present in the form is used to retrieve
			 * valid lists for the materials. In the meantime normally the structure is
			 * only loaded for the materials present in the order. The reason behind is
			 * to load less data due to the more specific filter criteria.
			 * 
			 * In the meantime when a service material is going to be added to the order
			 * valid lists (like uom etc.) need to be known for those, and the structure 
			 * salesareamap does not contain anymore the data for the additional service,
			 * which was added later on.
			 * 
			 * Therefore if the services tab is accessed reload the structure, and do not apply 
			 * the sap material filter parameter. This makes the service tab slower, as it needs to reload
			 * the structure when accessing.
			 */
			DeliverySettingSalesAreaMap salesAreaMap = initSalesAreaMap(helpForm.getOrder(),
					locale, false);
			helpForm.setSalesAreaMap(salesAreaMap);
			
			ServicesExtraCostTabBean bean = helpForm.getServicesExtraCostBean();
			
			if (bean == null) {
				bean = new ServicesExtraCostTabBean();
				
				//1. extra cost list
				List<SapOrderExtraCostBsEntity> activeExtraCostList = getOrderExtraCostsList(helpForm.getOrder());
				bean.setActiveExtraCostList(activeExtraCostList);
				
				Long extraCostWaitingTimeMin = helpForm.getOrder().getExtraCostWaitingTimeMin();
				bean.setExtraCostWaitingTimeMin(extraCostWaitingTimeMin);
				
				//2. extra cost map and extra cost json
				List<SapOrderExtraCostTxEntity> sapOrderExtraCostTxEntityList = getRefDataGenericsService().findTxEntityList(
						SapOrderExtraCostTxEntity.class, true, locale, null, true);
				
				Map<String, SapOrderExtraCostTxEntity> extraCostMap = new HashMap<String, SapOrderExtraCostTxEntity>();
				List<List<String>> extraCosts = new ArrayList<List<String>>(sapOrderExtraCostTxEntityList.size());
				
				for (SapOrderExtraCostTxEntity extraCostTx : sapOrderExtraCostTxEntityList) {
					//map
					extraCostMap.put(extraCostTx.getSapOrderExtraCostTxPK().getSapOrderExtraCostBs().getSapOrderExtraCostCd(), extraCostTx);
					
					//json
					List<String> extraCost = new ArrayList<String>(2);
					String sapOrderExtraCostCd = extraCostTx.getSapOrderExtraCostTxPK().getSapOrderExtraCostBs().getSapOrderExtraCostCd();
					String txt = extraCostTx.getTxt();
					extraCost.add(sapOrderExtraCostCd);
					extraCost.add(txt);
					extraCosts.add(extraCost);
				}
				//map
				bean.setExtraCostMap(extraCostMap);
				final JSONSerializer jsonSerializer = JsonUtils.getJsonSerializer();
				
				//json
				String serializedExtraCosts = jsonSerializer.serialize(extraCosts);				
				bean.setExtraCostsJSON(serializedExtraCosts);
				
				
			}
			else {
				bean.setExtraCost(null);
			}
			
			//3. Service materials dropdown	
			final OrderEntity order = helpForm.getOrder();
			Long sapContractId = null;
			String productionPlantCd = null;
			
			final SapContractEntity sapContract = order.getSapContract();
			if (sapContract != null) {
				sapContractId = sapContract.getSapContractId();
			}
			
			final List<OrderItemEntity> sortedOrderItems = OrderUtils.getOrderItemsFromOrderSorted(order);
			if (sortedOrderItems != null && !sortedOrderItems.isEmpty()) {
				productionPlantCd = sortedOrderItems.get(0).getProductionPlant().getPlantCd();
			}
			
			final List<DeliverySettingDTO> services = helpForm.getSalesAreaMap().getServicesForFirstDeliverySetting(sapContractId, productionPlantCd);
			String serializedServices = JsonUtils.getDeliverySettingJSONSerializer().serialize(services);
			bean.setServiceListJSON(serializedServices);
			
			
			//4. extra cost setting dependent vars
			List<SapOrderExtraCostBsEntity> activeExtraCostList = bean.getActiveExtraCostList();
			bean.setExtraCostFull(activeExtraCostList.size() == Constant.MAX_ORDER_EXTRA_COST_COUNT);
			bean.setExtraCostEmpty(activeExtraCostList.isEmpty());
			
			helpForm.setMaterialTabOpened(false);
			helpForm.setServicesExtraCostBean(bean);
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
	 * Returns the extra cost items of an order in a list.
	 */
	public List<SapOrderExtraCostBsEntity> getOrderExtraCostsList(OrderEntity order) {
		// [[
		List<SapOrderExtraCostBsEntity> result = null;
		Object[][] params = {{"order", order}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getOrderExtraCostsList", params);
		}
		// ]]
		try {
			
			List<SapOrderExtraCostBsEntity> extraCostList = new ArrayList<SapOrderExtraCostBsEntity>(4);
			
			SapOrderExtraCostBsEntity extraCost1 = order.getExtraCost1();
			if (extraCost1 != null) {
				extraCostList.add(extraCost1);
			}
			
			SapOrderExtraCostBsEntity extraCost2 = order.getExtraCost2();
			if (extraCost2 != null) {
				extraCostList.add(extraCost2);
			}
			
			SapOrderExtraCostBsEntity extraCost3 = order.getExtraCost3();
			if (extraCost3 != null) {
				extraCostList.add(extraCost3);
			}
			
			SapOrderExtraCostBsEntity extraCost4 = order.getExtraCost4();
			if (extraCost4 != null) {
				extraCostList.add(extraCost4);
			}
			
			return result = extraCostList;
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
     * Adds an extra cost to the order.
     * @param helpForm
     */
    protected void addExtraCost(OrderDetailForm helpForm) {    	
		// [[
		Object[][] params = {{"helpForm", helpForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "addExtraCost", params);
		}
		// ]]
		try {
			ServicesExtraCostTabBean bean = helpForm.getServicesExtraCostBean();
			
			String extraCostCd = bean.getExtraCost();
			SapOrderExtraCostTxEntity sapOrderExtraCostTxEntity = bean.getExtraCostMap().get(extraCostCd);
			
			SapOrderExtraCostBsEntity sapOrderExtraCostBs = sapOrderExtraCostTxEntity.getSapOrderExtraCostTxPK().getSapOrderExtraCostBs();
			
			bean.getActiveExtraCostList().add(sapOrderExtraCostBs);
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
     * Adds a new service item to the order.
     * 
     * @param helpForm
     * @param request TODO
     */
    protected void addServiceItem(OrderDetailForm helpForm, HttpServletRequest request) {    	
		// [[
		Object[][] params = {{"helpForm", helpForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "addServiceItem", params);
		}
		// ]]
		try {
			ServicesExtraCostTabBean servicesExtraCostTab = helpForm.getServicesExtraCostBean();
			
			DeliverySettingSalesAreaMap salesAreaMap = helpForm.getSalesAreaMap();
			String selectedServiceCompositeIdentifier = 
					servicesExtraCostTab.getSelectedServiceCompositeIdentifier();
			DeliverySettingDTO selectedDeliverySetting = salesAreaMap.
					getServiceDeliverySettingByCompositeIdentifier(
							selectedServiceCompositeIdentifier);
			
			final OrderItemEntity orderItem = new OrderItemEntity();
			final OrderEntity order = helpForm.getOrder();
			
			SapMaterialEntity selectedSapMaterial = selectedDeliverySetting.getSapMaterial();
			Long selectedSapMaterialId = selectedSapMaterial.getSapMaterialId();
			List<OrderItemEntity> orderItemsInOrder = order.getOrderItems();
			boolean isSelectedMaterialAlreadyAddedToTheOrder = containsOrderWithMaterialIdentifier(
					selectedSapMaterialId, orderItemsInOrder);
			if (isSelectedMaterialAlreadyAddedToTheOrder) {
				MessageUtils.addActionWarning(request, "bestellung.error.alreadyselected");
				return;
			}
			int countOfOrderItemsInOrder = orderItemsInOrder.size();
			long newOrderItemNumber = new Long(countOfOrderItemsInOrder + 1) * 10;
			orderItem.setOrderItemNo(newOrderItemNumber);
			
			SapContractItemEntity selectedSapContractItem = selectedDeliverySetting.getSapContractItem();
			orderItem.setSapContractItem(selectedSapContractItem);
			orderItem.setSapMaterial(selectedSapMaterial);
			
			PlantBsEntity selectedProductionPlant = selectedDeliverySetting.getProductionPlant();
			orderItem.setProductionPlant(selectedProductionPlant);
			
			/**
			 * Further initialization will take place in {@link OrderDetailAction#initNewOrderItems}
			 */
			orderItemsInOrder.add(orderItem);
			orderItem.setOrder(order);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }

	public static boolean containsOrderWithMaterialIdentifier(Long materialIdentifier,
			List<OrderItemEntity> orderItems) {
		for (OrderItemEntity currentOrderItem : orderItems) {
			SapMaterialEntity currentSapMaterial = currentOrderItem.getSapMaterial();
			Long currentSapMaterialId = currentSapMaterial.getSapMaterialId();
			if (currentSapMaterialId.equals(materialIdentifier)) {
				return Constant.CONTAINS;
			}
		}
		return Constant.DOES_NOT_CONTAIN;
	}
    
    /**
     * Removes an extra cost from the order.
     * @param helpForm
     */
    protected void removeExtraCost(OrderDetailForm helpForm) {    	
		// [[
		Object[][] params = {{"helpForm", helpForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "removeExtraCost", params);
		}
		// ]]
		try {
			ServicesExtraCostTabBean bean = helpForm.getServicesExtraCostBean();
			
			String extraCostCd = bean.getExtraCost();
			SapOrderExtraCostTxEntity sapOrderExtraCostTxEntity = bean.getExtraCostMap().get(extraCostCd);
			
			SapOrderExtraCostBsEntity sapOrderExtraCostBs = sapOrderExtraCostTxEntity.getSapOrderExtraCostTxPK().getSapOrderExtraCostBs();
			
			List<SapOrderExtraCostBsEntity> activeExtraCostList = bean.getActiveExtraCostList();
			for(SapOrderExtraCostBsEntity activeExtraCost : activeExtraCostList) {
				if (activeExtraCost.getSapOrderExtraCostCd().equals(sapOrderExtraCostBs.getSapOrderExtraCostCd())) {
					activeExtraCostList.remove(activeExtraCost);
					break;
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

	protected void initSelectedPayerAddress(MaterialTabBean mBean, SapBpEntity payerSapBp, SapBpEntity soldtoSapBp, OrderEntity order, OrderDetailForm helpForm) {
		// [[
		Object[][] params = {{"mBean", mBean},
							{"payerSapBp", payerSapBp},
							{"soldtoSapBp", soldtoSapBp},
							{"order", order},
							{"helpForm", helpForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initSelectedPayerAddress", params);
		}
		// ]]
		try {
			helpForm.setPayerSameAsOneTimeSoldto(Boolean.FALSE);
			if (payerSapBp != null) {
				mBean.setPayerId(payerSapBp.getSapBpId().toString());
				mBean.setPayerSapId(StringUtils.nvl(payerSapBp.getSapId()));
				mBean.setPayerName(StringUtils.nvl(payerSapBp.getName1()) + " "
						+ StringUtils.nvl(payerSapBp.getName2()));
				mBean.setPayerStreet(StringUtils.nvl(payerSapBp.getStreet()));
				mBean.setPayerHouseNumber(StringUtils.nvl(payerSapBp.getHouseNumber()));
				mBean.setPayerCountry(StringUtils.nvl(payerSapBp.getCountry().getSapCountryCd()));
				mBean.setPayerPostalCode(StringUtils.nvl(payerSapBp.getPostalCode()));
				mBean.setPayerCity(StringUtils.nvl(payerSapBp.getCity()));
			
				if (soldtoSapBp.getTreatAsOnetimeFlag().booleanValue()) {
					if (soldtoSapBp.getSapBpId().equals(payerSapBp.getSapBpId())) {
						mBean.setPayerName(StringUtils.nvl(order.getOneTimeSoldtoName()));
						mBean.setPayerStreet(StringUtils.nvl(order.getOneTimeSoldtoStreetHouseno()));
						mBean.setPayerHouseNumber(null); // field not used
						mBean.setPayerCountry(StringUtils.nvl(order.getOneTimeSoldtoCountry()));
						mBean.setPayerPostalCode(StringUtils.nvl(order.getOneTimeSoldtoPostalCode()));
						mBean.setPayerCity(StringUtils.nvl(order.getOneTimeSoldtoCity()));	
						helpForm.setPayerSameAsOneTimeSoldto(Boolean.TRUE);
					}
				}
			}
			else {
				mBean.setPayerId(Constant.NO_DATA);
				mBean.setPayerSapId(Constant.NO_DATA);
				mBean.setPayerName(Constant.NO_DATA);
				mBean.setPayerStreet(Constant.NO_DATA);
				mBean.setPayerHouseNumber(Constant.NO_DATA);
				mBean.setPayerCountry(Constant.NO_DATA);
				mBean.setPayerPostalCode(Constant.NO_DATA);
				mBean.setPayerCity(Constant.NO_DATA);
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
	
	protected void initSelectedBilltoAddress(MaterialTabBean mBean, SapBpEntity billtoSapBp, SapBpEntity soldtoSapBp, OrderEntity order, OrderDetailForm helpForm) {
		// [[
		Object[][] params = {{"mBean", mBean},
							{"billtoSapBp", billtoSapBp},
							{"soldtoSapBp", soldtoSapBp},
							{"order", order},
							{"helpForm", helpForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initSelectedBilltoAddress", params);
		}
		// ]]
		try {
			helpForm.setBilltoSameAsOneTimeSoldto(Boolean.FALSE);
			if (billtoSapBp != null) {
				
				mBean.setBilltoSapId(billtoSapBp.getSapId().toString());
				mBean.setBilltoSapBpId(billtoSapBp.getSapBpId().toString());
				mBean.setBilltoName(StringUtils.nvl(billtoSapBp.getName1()) + " " + StringUtils.nvl(billtoSapBp.getName2()));
				mBean.setBilltoStreet(StringUtils.nvl(billtoSapBp.getStreet()));
				mBean.setBilltoHouseNumber(StringUtils.nvl(billtoSapBp.getHouseNumber()));
				mBean.setBilltoCountry(StringUtils.nvl(billtoSapBp.getCountry().getSapCountryCd()));
				mBean.setBilltoPostalCode(StringUtils.nvl(billtoSapBp.getPostalCode()));
				mBean.setBilltoCity(StringUtils.nvl(billtoSapBp.getCity()));
			
				if (soldtoSapBp.getTreatAsOnetimeFlag().booleanValue()) {
					if (soldtoSapBp.getSapBpId().equals(billtoSapBp.getSapBpId())) {
						mBean.setBilltoName(StringUtils.nvl(order.getOneTimeSoldtoName()));
						mBean.setBilltoStreet(StringUtils.nvl(order.getOneTimeSoldtoStreetHouseno()));
						mBean.setBilltoHouseNumber(null); // field not used
						mBean.setBilltoCountry(StringUtils.nvl(order.getOneTimeSoldtoCountry()));
						mBean.setBilltoPostalCode(StringUtils.nvl(order.getOneTimeSoldtoPostalCode()));
						mBean.setBilltoCity(StringUtils.nvl(order.getOneTimeSoldtoCity()));	
						helpForm.setBilltoSameAsOneTimeSoldto(Boolean.TRUE);
					}
				}
			}
			else {
				mBean.setBilltoSapBpId(Constant.NO_DATA);
				mBean.setBilltoSapId(Constant.NO_DATA);
				mBean.setBilltoName(Constant.NO_DATA);
				mBean.setBilltoStreet(Constant.NO_DATA);
				mBean.setBilltoHouseNumber(Constant.NO_DATA);
				mBean.setBilltoCountry(Constant.NO_DATA);
				mBean.setBilltoPostalCode(Constant.NO_DATA);
				mBean.setBilltoCity(Constant.NO_DATA);
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
	 * Load material descriptions for the defined materials.
	 * @param form
	 * @param locale
	 */
	protected void loadSiloMaterialDescriptions(ActionForm form, Locale locale) {
		ShiptoEntity shipTo = null;
		SapBpEntity soldTo = null;
		
		if (form instanceof DispokarteForm) {
			DispokarteForm helpForm = (DispokarteForm) form;
			shipTo = helpForm.getShipTo();
			soldTo = helpForm.getSoldTo();
		}
		else if (form instanceof BestellungForm) {
			BestellungForm helpForm = (BestellungForm) form;
			shipTo = helpForm.getShipTo();
			soldTo = helpForm.getSoldTo();
		}
		Map<Long, SapMaterialDescEntity> materialDescriptionsByMaterialId = new HashMap<Long, SapMaterialDescEntity>();
		
		List<Long> siloMaterialIds = new ArrayList<Long>();

		if (shipTo != null) {
			List<ShiptoSiloEntity> shiptoSilos = shipTo.getSilos();
			for (ShiptoSiloEntity shiptoSilo : shiptoSilos) {
				SapMaterialEntity sapMaterial = shiptoSilo.getSapMaterial();
				if (sapMaterial != null) {
					siloMaterialIds.add(sapMaterial.getSapMaterialId());
				}
			}
		}

		if (!siloMaterialIds.isEmpty()) {
			ServiceLocator serviceLocator = getServiceLocator();
			DispokarteServiceLocalInterface localDispokarteService = serviceLocator.getLocalDispokarteService();
			String language = locale.getLanguage();
			List<SapMaterialDescEntity> materialDescriptions = localDispokarteService.fetchSapMaterialDescByLang(
					language, shipTo, soldTo, siloMaterialIds);
			materialDescriptionsByMaterialId = new HashMap<Long, SapMaterialDescEntity>();
			for (SapMaterialDescEntity materialDescription : materialDescriptions) {
				materialDescriptionsByMaterialId.put(materialDescription.getSapMaterialId(),
						materialDescription);
			}
		}

		if (form instanceof DispokarteForm) {
			DispokarteForm helpForm = (DispokarteForm) form;
			helpForm.setSiloMaterialDescriptionMap(materialDescriptionsByMaterialId);
		}
		else if (form instanceof BestellungForm) {
			BestellungForm helpForm = (BestellungForm) form;
			helpForm.setSiloMaterialDescriptionMap(materialDescriptionsByMaterialId);
		}
	}


	/**
	 * Creates a list of OrderTruckType entities based on a vehicle list and the
	 * list of selected vehicles. The order entry and order detail screens contain
	 * a checkable combo box in which the user can enable or disable vehicle types
	 * for an order. This method takes the whole list of selectable vehicle types,
	 * <code>vehicleTypes</code> as well as the list of selected (checked) vehicle types,
	 * <code>selectedVehicleTypes</code>. It returns then a list of order entities
	 * which can be set in an order.  
	 * @param order The order entity, needed because it must be set in each of its
	 * child OrderTruckType entities.
	 * @param selectedVehicleTypes The list of selected vehicle entities (type codes)
	 * @param vehicleTypes The list of selectable vehicle types.
	 * @return A list of OrderTruckTypeEntities, which should then be set in the
	 * order entity's <code>orderVehicleTypes</code> property. 
	 */
	final protected static List<OrderTruckTypeEntity> createOrderTruckTypes(
			final OrderEntity order,
			final List<Long> selectedVehicleTypeCds, 
			final List<TruckWagonTypeWrapper> vehicleTypes) 
	{
		final List<OrderTruckTypeEntity> orderTruckTypes = new LinkedList<OrderTruckTypeEntity>();
		
		// add selected truck types to the order
		if (selectedVehicleTypeCds != null && !selectedVehicleTypeCds.isEmpty()
				&& vehicleTypes != null && !vehicleTypes.isEmpty()) {
					for (TruckWagonTypeWrapper wrapper : vehicleTypes) {
						if (selectedVehicleTypeCds.contains(wrapper.getTypeCd())) {
							
							TruckTypeBsEntity truckTypeBs = null;
							if (wrapper instanceof TruckTypeWrapper) {
								truckTypeBs = ((TruckTypeWrapper) wrapper).getTruckTypeBsEntity();
							}
							else {
								LOG.error("invalid truck type");
								continue;
							}
							
							OrderTruckTypeEntity orderTruckType = new OrderTruckTypeEntity();
							orderTruckType.setEvaluationPriority(wrapper.getPriority());
							orderTruckType.setTruckType(truckTypeBs);
							orderTruckType.setOrder(order);
							
							orderTruckTypes.add(orderTruckType);
						}
					}
		}
		return orderTruckTypes;
	}

	/**
	 * Converts a String to a list of Long. 
	 * <p><em>Example:</em> If the input is "1,2,3,4" then the output is the <code>List&gt;Long&lt;</code>
	 * <code>[1, 2, 3, 4]</code>.</p> 
	 * @param listString A comma separated string of numbers.
	 * @return A list containing the numbers in the string. If something
	 * that cannot be parsed is encountered, only the numbers up to that point
	 * are contained in the returned list.
	 */
	final protected static List<Long> parseNumberList(final String listString) 
	{
		// [[
		List<Long> result = null;
		Object[][] params = {{"listString", listString}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(BestellungAction.class, "parseNumberList", params);
		}
		// ]]
		try {
			final List<Long> numberList = new LinkedList<Long>();
			
			if (StringUtils.notEmpty(listString))
			{
				final String[] splittedSelectedVehicleTypes = listString.split(",");
				
				for (final String numberStr : splittedSelectedVehicleTypes) 
				{
					try {
						final Long parsedNumber = Long.parseLong(numberStr);
						numberList.add(parsedNumber);
					}
					catch (NumberFormatException e) {
						LOG.error("invalid vehicleTypeCd: " + numberStr);
					}
				}
			}
			
			return result = numberList;
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
	 * Initializes the material bean's goodsMovementBeanswhen the popup is opening.
	 */
	public void initializeGoodsMovementsForItemIndex(int matIndex, MaterialTabBean mBean, HttpServletRequest request) {
		// [[
		Object[][] params = { { "matIndex", matIndex + "" },
							{"mBean", mBean},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initializeGoodsMovementsForItemIndex", params);
		}
		// ]]
		try {
			boolean goodsMovementPresent = checkGoodsMovementsForLoad(matIndex, mBean);
			Long loadId = getLoadIdByIndexIfPresent(matIndex, mBean);
			
			if (goodsMovementPresent) {
				GoodsMovementService goodsMovementService = getServiceLocator().getGoodsMovementService();
				List<GoodsMovementEntity> goodsMovements = goodsMovementService.getAllGoodsMovementsForLoad(loadId);
				List<GoodsMovementDisplayBean> goodsMovementBeans = new ArrayList<GoodsMovementDisplayBean>(goodsMovements.size());
				for(GoodsMovementEntity goodsMovement : goodsMovements) {
					
					GoodsMovementDisplayBean bean = new GoodsMovementDisplayBean();
					bean.setEntity(goodsMovement);
					
					String endLoadReason = MessageUtils.getMessage(request, GoodsMovementUtils.END_LOAD_REASON_KEY);
					String returnedReason = MessageUtils.getMessage(request, GoodsMovementUtils.RETURNED_REASON_KEY);
					String creditNoteReason = MessageUtils.getMessage(request, GoodsMovementUtils.CREDIT_NOTE_REASON_KEY);
					String goodsMovementReason = GoodsMovementUtils.getGoodsMovementReason(goodsMovement, endLoadReason, returnedReason, creditNoteReason);
					bean.setReason(goodsMovementReason);
					
					String pendingLabel = MessageUtils.getMessage(request, GoodsMovementUtils.PENDING_LABEL_KEY);
					String goodsMovementDate = GoodsMovementUtils.getGoodsMovementDate(goodsMovement, pendingLabel, request.getLocale());
					bean.setDate(goodsMovementDate);
					
					String movementLabel = MessageUtils.getMessage(request, GoodsMovementUtils.MOVEMENT_KEY);
					bean.setMovement(movementLabel + " #" + (goodsMovements.size() - goodsMovements.indexOf(goodsMovement)));
					
					goodsMovementBeans.add(bean);
					String deliveryPlant = GoodsMovementUtils.getGoodsMovementDeliveryPlant(goodsMovement);
					bean.setDeliveryPlant(deliveryPlant);
					String fromPlant = GoodsMovementUtils.getGoodsMovementFromPlant(goodsMovement);
					bean.setFromPlant(fromPlant);
				}
				
				mBean.setGoodsMovementBeans(goodsMovementBeans);
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
	 * Checks if there is any goods movement for the current load and sets the bean property accordingly
	 */
	public boolean checkGoodsMovementsForLoad(int matIndex, MaterialTabBean mBean) {
		// [[
		boolean result1 = false;
		Object[][] params = { { "matIndex", matIndex + "" },
							{"mBean", mBean}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "checkGoodsMovementsForLoad", params);
		}
		// ]]
		try {
			boolean result = false;
			
			Long loadId = getLoadIdByIndexIfPresent(matIndex, mBean);
			
			if (loadId != null) {
				GoodsMovementService goodsMovementService = getServiceLocator().getGoodsMovementService();
				result = goodsMovementService.goodsMovementsPresentForLoad(loadId);
			}
			
			mBean.setGoodsMovementPresent(result);
			return result1 = result;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result1);
			}
		// ]]
		}
	}
	
	/**
	 * Tries to parse the loadId based on the bean, sets the status flag accordingly.
	 */
	public Long getLoadIdByIndexIfPresent(int matIndex, MaterialTabBean mBean) {
		// [[
		Long result = null;
		Object[][] params = { { "matIndex", matIndex + "" },
							{"mBean", mBean}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getLoadIdByIndexIfPresent", params);
		}
		// ]]
		try {
			OrderItemBean itemBean = mBean.getOrderItemList().get(matIndex);
			String loadIdStr = itemBean.getLoadId();
			Long loadId = null;
			
			if (!StringUtils.empty(loadIdStr)) {
				try {
					loadId = Long.valueOf(loadIdStr);
				}
				catch (NumberFormatException e) {
					mBean.setGoodsMovementPresent(false);
				}
			}
			return result = loadId;
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
	 * @param iBean
	 * @param sapMaterial
	 * @param loadedUom
	 * @param netQuantityUom must not be null!
	 */
	private void initNetQtyConvFactors(OrderItemBean iBean, SapMaterialEntity sapMaterial,
			UomBsEntity loadedUom, UomBsEntity netQuantityUom) {
		// [[
		Object[][] params = {{"iBean", iBean},
							{"sapMaterial", sapMaterial},
							{"loadedUom", loadedUom},
							{"netQuantityUom", netQuantityUom}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initNetQtyConvFactors", params);
		}
		// ]]
		try {
			OrderItemConvFactors convFactors = iBean.getConvFactors();
			
			if (loadedUom != null) {
				// Net Quantity UoM -> Loaded UoM
				UomConverterService uomConverterService = getServiceLocator().getUomConverterService();
				String netQuantityUomCd = netQuantityUom.getUomCd();
				
				try {
					UomConversionResult ucs = uomConverterService.convert(BigDecimal.ONE,
							netQuantityUom, loadedUom, sapMaterial, null, null, false);
					
					Long numerator = ucs.getNumerator();
					Long denominator = ucs.getDenominator();
					
					ConversionFactor factor = new ConversionFactor(numerator, denominator);
					convFactors.setFromNetQtyToLoadedUom(factor);
				}
				catch (LogonConversionException e)
				{
						String loadedUomCd = loadedUom.getUomCd();
						Long sapMaterialId = sapMaterial.getSapMaterialId();
						
						LOG.warn("Cannot convert from net quantity in UoM " + netQuantityUomCd
								+ " to loaded UoM " + loadedUomCd 
								+ " (sapMaterialId = " + sapMaterialId + "): " 
								+ e.getMessage());
				}

				// Net Quantity UoM -> Tons
				UomBsEntity uomTons = iBean.getUomMap().get(Constant.UOM_TONS).getUomTxPK().getUomBs();
				
				try {
					UomConversionResult ucs = uomConverterService.convert(BigDecimal.ONE,
							netQuantityUom, uomTons, sapMaterial, null, null, false);
					
					Long numerator = ucs.getNumerator();
					Long denominator = ucs.getDenominator();
					
					ConversionFactor factor = new ConversionFactor(numerator, denominator);
					convFactors.setFromNetQtyToTons(factor);
				}
				catch (LogonConversionException e)
				{
					Long sapMaterialId = sapMaterial.getSapMaterialId();
					
					LOG.warn("Cannot convert from net quantity in UoM " + netQuantityUomCd
							+ " to tons (sapMaterialId = " + sapMaterialId + "): "
							+ e.getMessage());
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
	 * @param iBean
	 * @param sapMaterial must not be null!
	 * @param deliveredUom 
	 */
	private void initLoadedQtyConvFactors(OrderItemBean iBean, SapMaterialEntity sapMaterial,
			UomBsEntity deliveredUom) {
		// [[
		Object[][] params = {{"iBean", iBean},
							{"sapMaterial", sapMaterial},
							{"deliveredUom", deliveredUom}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initLoadedQtyConvFactors", params);
		}
		// ]]
		try {
			OrderItemConvFactors convFactors = iBean.getConvFactors();
			List<UomTxEntity> uomList = iBean.getUomList();
			
			if (uomList == null || uomList.isEmpty()) {
				Collection<UomTxEntity> uomsFromOrderItemMap = iBean.getUomMap().values();
				if (uomsFromOrderItemMap != null && !uomsFromOrderItemMap.isEmpty()) {
					uomList.addAll(uomsFromOrderItemMap);
				}
			}
			
			Map<String, ConversionFactor> factors =
					new HashMap<String, ConversionFactor>();
			
			if (deliveredUom != null) {
				// Loaded UoM -> Delivered UoM
				UomConverterService uomConverterService =
						getServiceLocator().getUomConverterService();
				
				for (UomTxEntity uomTx : uomList) {
					UomTxPK uomTxPK = uomTx.getUomTxPK();
					UomBsEntity uom = uomTxPK.getUomBs();
					String uomCd = uom.getUomCd();
					
					try {
						UomConversionResult ucs = uomConverterService.convert(BigDecimal.ONE,
								uom, deliveredUom, sapMaterial, null, null, false);
						
						Long numerator = ucs.getNumerator();
						Long denominator = ucs.getDenominator();
						
						ConversionFactor factor = new ConversionFactor(numerator, denominator);
						factors.put(uomCd, factor);
					}
					catch (LogonConversionException e)
					{
						Long sapMaterialId = sapMaterial.getSapMaterialId();
						
						LOG.warn("Cannot convert from loaded quantity in UoM " + uomCd
								+ " to delivered UoM " + deliveredUom.getUomCd()
								+ " (sapMaterialId = " + sapMaterialId + "): "
								+ e.getMessage());
					}
				}
			}
			
			convFactors.setFromLoadedUomToDelivUom(factors);
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
	 * @param mBean
	 * @param orderItemList 
	 */
	private void generateConvFactorsJson(MaterialTabBean mBean,
			List<OrderItemBean> orderItemList) {
		// [[
		Object[][] params = {{"mBean", mBean},
							{"orderItemList", orderItemList}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "generateConvFactorsJson", params);
		}
		// ]]
		try {
			// orderItemId -> OrderItemConvFactors
			Map<Long, OrderItemConvFactors> convFactorsMap =
					new HashMap<Long, OrderItemConvFactors>();
			
			for (OrderItemBean iBean : orderItemList) {
				Long orderItemId = iBean.getOrderItemId();
				OrderItemConvFactors convFactors = iBean.getConvFactors();
				
				convFactorsMap.put(orderItemId, convFactors);
			}
			
			String convFactorsMapJson = new JSONSerializer()
					.include("fromLoadedUomToDelivUom")
					.exclude("*.class")
					.serialize(convFactorsMap);
			
			mBean.setConvFactorsMapJson(convFactorsMapJson);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	private Map<String, UomTxEntity> buildUnitOfMeasureTextMap(Locale locale)
	{
		final List<UomTxEntity> uomTxList;
		uomTxList = getRefDataGenericsService().findTxEntityList(UomTxEntity.class, locale);
		
		final Map<String, UomTxEntity> uomTxMap = new HashMap<String, UomTxEntity>();
		for (UomTxEntity uom : uomTxList) {
			uomTxMap.put(uom.getUomTxPK().getUomBs().getUomCd(), uom);
		}
		
		return uomTxMap;
	}

	protected TimeZone createTimeZoneFor(ShiptoEntity shipto) {
		return getTimeZoneService().loadForShipTo(shipto, getUserLocale());
	}


	protected List<DateTimeAwareCustomerOrderWrapper> fetchOpenCustorders(
			ShiptoEntity shipTo,
			Locale locale) {

		return fetchCustordersByStatus(shipTo, false, locale);
	}

	protected List<DateTimeAwareOrderWrapper> fetchOngoingOrders(
			ShiptoEntity shipTo,
			Locale locale) {

		return fetchOrdersByStatus(shipTo, false, locale);
	}

	protected List<DateTimeAwareOrderWrapper> fetchCompletedOrders(
			ShiptoEntity shipTo,
			Locale locale) {

		return fetchOrdersByStatus(shipTo, true, locale);
	}

	protected List<DateTimeAwareCustomerOrderWrapper> fetchClosedCustorders(
			ShiptoEntity shipTo,
			Locale locale) {

		return fetchCustordersByStatus(shipTo, true, locale);
	}

	private List<DateTimeAwareOrderWrapper> fetchOrdersByStatus(
			ShiptoEntity shipTo,
			boolean isCompletedOrder,
			Locale locale) {

		return getBestellungService().fetchDateTimeAwareOrdersByStatus(
				false,
				shipTo,
				isCompletedOrder,
				locale,
				getUserTimezone());
	}

	private List<DateTimeAwareCustomerOrderWrapper> fetchCustordersByStatus(
			ShiptoEntity shipTo,
			boolean isClosedCustorder,
			Locale locale) {

		return getBestellungService().fetchDateTimeAwareCustordersByStatus(
				false,
				shipTo,
				isClosedCustorder,
				locale,
				getUserTimezone());
	}
	
	public Set<Long> getSapMaterialIdsFromOrder(OrderEntity order) {
		Set<Long> sapMaterialIds = new HashSet<Long>();
		for (OrderItemEntity orderItem : order.getOrderItems()) {
			sapMaterialIds.add(orderItem.getSapMaterial().getSapMaterialId());
		}
		return sapMaterialIds;
	}
}
