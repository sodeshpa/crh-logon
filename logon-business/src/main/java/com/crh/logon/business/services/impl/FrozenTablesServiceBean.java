/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import com.crh.logon.business.services.beans.UomConversionResult;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.FrozenTablesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TimeZoneServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UomConverterServiceLocalInterface;
import org.apache.commons.lang.ObjectUtils;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Expression;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.FrozenTablesServiceDispatcher;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.SalesAreaWebOrderOnWeekendSettings;
import com.crh.logon.business.services.utils.SapMaterialUtil;
import com.crh.logon.business.services.utils.SchedulingUtils;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.DriverBreakEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.DriverWaitingreasonEntity;
import com.crh.logon.persistence.entities.common.GoodsMovementEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.MatPlantEntity;
import com.crh.logon.persistence.entities.common.MatShippingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.RailwayStationEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.common.ShiptoTruckTypeEntity;
import com.crh.logon.persistence.entities.common.ShiptoWagonTypeEntity;
import com.crh.logon.persistence.entities.common.SimpleVehicleEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.frozen.OrderFinalEntity;
import com.crh.logon.persistence.entities.frozen.OrderItemFinalEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.DivisionBsEntity;
import com.crh.logon.persistence.entities.refdata.SapTrspModeBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.WagonTypeBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapBpSareaEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialSareaEntity;
import com.crh.logon.persistence.entities.sap.SapVendorEntity;
import com.crh.logon.persistence.entities.views.OrderItemMarginEntity;
import com.crh.logon.persistence.entities.views.SapNetpOrderItemSumEntity;
import com.crh.logon.utils.LogonConversionException;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.time.DateTimeUtils;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class FrozenTablesServiceBean extends FrozenTablesServiceDispatcher implements FrozenTablesServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory
			.getLogger(FrozenTablesServiceBean.class);

	@EJB private UomConverterServiceLocalInterface unitOfMeasurementConverterService;
	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;
	@EJB private DispoServiceLocalInterface dispoService;
	@EJB private OrderServiceLocalInterface orderService;
	@EJB private TimeZoneServiceLocalInterface timeZoneService;


	
	/**
	 * Selects all needed order and order item data and saves to frozen tables
	 * 
	 * @param orderId
	 *            for which to load margins
	 * @adnlog.exclude return
	 */
	public OrderFinalEntity freezeOrder(OrderEntity order) {
		// [[
		Object[][] params = {{"order", order}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "freezeOrder", params);
		}
		// ]]
		try {
			
			OrderFinalEntity orderFinal;
			
			
			//check if order final entry already exists 
			orderFinal = loadOrderFinal(order.getOrderId());

			if (orderFinal == null) {
				//insert a new frozen order
				orderFinal = new OrderFinalEntity();
				orderFinal.setOrderFinalItems(new LinkedList<OrderItemFinalEntity>());
			}
			//else update a frozen order

			orderFinal.setOOrderId(order.getOrderId());

			orderFinal.setOOrderNumber(order.getOrderNumber());
			orderFinal.setOOrderStatusCd(order.getOrderStatus().getOrderStatusCd());
			orderFinal.setOShiptoSapBpId(order.getShiptoSapBp().getSapBpId());

			orderFinal.setOBpCorrectedFlag(order.getBpCorrectedFlag());

			// Content of tlgns_sap_bp (shipto)
			SapBpEntity shiptoSapBp = order.getShiptoSapBp();
			ShiptoEntity shipTo = dispoService.fetchShipto(order.getShiptoSapBp().getSapBpId());
			orderFinal.setWeSapId(shiptoSapBp.getSapId());
			orderFinal.setWeMarkedForDeletion(shiptoSapBp.getMarkedForDeletion());
			orderFinal.setWeName1(shiptoSapBp.getName1());
			orderFinal.setWeName2(shiptoSapBp.getName2());
			orderFinal.setWeCountry(shiptoSapBp.getCountry().getSapCountryCd());
			orderFinal.setWePostalCode(shiptoSapBp.getPostalCode());
			orderFinal.setWeCity(shiptoSapBp.getCity());
			orderFinal.setWeRegion(shiptoSapBp.getRegion());
			orderFinal.setWeStreet(shiptoSapBp.getStreet());
			orderFinal.setWeHouseNumber(shiptoSapBp.getHouseNumber());
			if (shiptoSapBp.getLang() != null) {
				orderFinal.setWeLangCd(shiptoSapBp.getLang().getLangCd());
			}
			orderFinal.setWeCustomerClassification(shiptoSapBp.getCustomerClassification());
			if (shiptoSapBp.getRailwayStation() != null) {
				orderFinal.setWeRailwayStationId(shiptoSapBp.getRailwayStation().getRailwayStationId());
				orderFinal.setWeRsStationName(shiptoSapBp.getRailwayStation().getRailwayStationName());
				orderFinal.setWeRsStationNo(shiptoSapBp.getRailwayStation().getRailwayStationNo());
				orderFinal.setWeRsUicCountryCd(shiptoSapBp.getRailwayStation().getUicCountry().getUicCountryCd());
				
				
				RailwayStationEntity commRailwayStation = LogonUtil.getCommercialRailwayStationEntity(shiptoSapBp.getRailwayStation(),  getDaoProvider().getRailwayStationCommEntityDao());
				
				orderFinal.setWeRsComStationName(commRailwayStation.getRailwayStationName());
				orderFinal.setWeRsComStationNo(commRailwayStation.getRailwayStationNo());
				orderFinal.setWeRsComUicCountryCd(commRailwayStation.getUicCountry().getUicCountryCd());
			}
			orderFinal.setWeTrainStUnloadingPoint(shipTo.getTrainStationUnloadingPoint());
			orderFinal.setWeSbbCustomerNumber(shipTo.getSbbCustomerNumber());
			orderFinal.setWeBpIsShipto(shiptoSapBp.getBpIsShipto());
			orderFinal.setWeBpIsSoldto(shiptoSapBp.getBpIsSoldto());
			orderFinal.setWeBpIsBillto(shiptoSapBp.getBpIsBillto());
			orderFinal.setWeChromatReductionMonths(shiptoSapBp.getChromatReductionMonths());
			
			// Content of tlgns_sap_bp_sarea for the sarea_cd of the order (shipto)
			final ReadOnlyDao<SapBpSareaEntity> bpSareaDao = getDaoProvider().getSapBpSareaEntityDao();
			final Filter<SapBpSareaEntity> weSareaFilter = bpSareaDao.createFilter();
			weSareaFilter.addExpression(Restrictions.eq(SapBpSareaEntity.ATTR_SAP_BP_ID,
					shiptoSapBp.getSapBpId()));
			weSareaFilter.addExpression(Restrictions.eq(SapBpSareaEntity.ATTR_SAREA_ID,
					order.getSarea().getSareaId()));
			SapBpSareaEntity shiptoSarea = bpSareaDao.findSingle(weSareaFilter);
			
			if (shiptoSarea != null) {
				orderFinal.setWeSapBpSareaId(shiptoSarea.getSapBpSareaId());
				orderFinal.setWeSareaMarkedForDeletion(shiptoSarea.getMarkedForDeletion());
				orderFinal.setWeSalesDistrict(shiptoSarea.getSalesDistrict());
				if (shiptoSarea.getSoffice() != null) {
					orderFinal.setWeSofficeCd(shiptoSarea.getSoffice().getSofficeCd());
					orderFinal.setWeSofficeName(shiptoSarea.getSoffice().getSofficeName());
				}
				if (shiptoSarea.getSgroup() != null) {
					orderFinal.setWeSgroupCd(shiptoSarea.getSgroup().getSgroupCd());
					orderFinal.setWeSgroupName(shiptoSarea.getSgroup().getSgroupName());
					orderFinal.setWeSgroupEmailAddresses(shiptoSarea.getSgroup().getEmailAddresses());
				}
				orderFinal.setWeCustomerGroup(shiptoSarea.getCustomerGroup());
				orderFinal.setWeAbcClass(shiptoSarea.getAbcClass());
				if (shiptoSarea.getCurrency() != null) {
					orderFinal.setWeCurrencyCd(shiptoSarea.getCurrency().getCurrencyCd());
				}
				if (shiptoSarea.getDistributionDest() != null) {
					orderFinal.setWeDistributionDestCd(shiptoSarea.getDistributionDest().getDistributionDestCd());
				}
				orderFinal.setWeCustomerOrderBlock(shiptoSarea.getCustomerOrderBlock());
				orderFinal.setWeCustomerDeliveryBlock(shiptoSarea.getCustomerDeliveryBlock());
				orderFinal.setWeCustomOffice(shiptoSarea.getCustomOffice());
				if (shiptoSarea.getDefaultPlant() != null) {
					orderFinal.setWeDefaultPlantCd(shiptoSarea.getDefaultPlant().getPlantCd());
				}
			}
			
			// Content of tlgnc_shipto
			ReadWriteDao<ShiptoEntity> shiptoDao = getDaoProvider().getShiptoEntityDao();
			Filter<ShiptoEntity> shiptoFilter = shiptoDao.createFilter();
			shiptoFilter.addExpression(Restrictions.eq(ShiptoEntity.ATTR_SHIPTO_SAP_BP_ID,
					shiptoSapBp.getSapBpId()));
			final ShiptoEntity shiptoEntity = shiptoDao.findSingle(shiptoFilter);
			
			if (shiptoEntity != null) {
				orderFinal.setWeShiptoId(shiptoEntity.getShiptoId());
				orderFinal.setWeActiveFlag(shiptoEntity.getActiveFlag());
				orderFinal.setWeShiptoTypeCd(shiptoEntity.getShiptoType().getShiptoTypeCd());
				orderFinal.setWeSignatureTypeCd(shiptoEntity.getSignatureType().getSignatureTypeCd());
				orderFinal.setWeCoordLat(shiptoEntity.getCoordLat());
				orderFinal.setWeCoordLon(shiptoEntity.getCoordLon());
				orderFinal.setWeCoordFixFlag(shiptoEntity.getCoordFixFlag());
				orderFinal.setWeGeofenceRadius(shiptoEntity.getGeofenceRadius());
				orderFinal.setWeUnloadingTime(order.getPlannedUnloadingTimeMin());
				
				// Content of tlgnc_shippingpoint for Mat Chain
				ShippingpointEntity sp = shiptoEntity.getMatChainShippingpoint();
				if (sp != null) {
					orderFinal.setWeMatChainShippingpointId(sp.getShippingpointId());
					orderFinal.setWeMatChainSpPlantCd(sp.getPlant().getPlantCd());
					orderFinal.setWeMatChainSpSapShipptNo(sp.getSapShippingpointNo());
					orderFinal.setWeMatChainSpShipptName(sp.getShippingpointName());
				}
				// Content of tlgnc_shippingpoint for Mat Chain
				sp = shiptoEntity.getRailChainShippingpoint();
				if (sp != null) {
					orderFinal.setWeMatChainShippingpointId(sp.getShippingpointId());
					orderFinal.setWeMatChainSpPlantCd(sp.getPlant().getPlantCd());
					orderFinal.setWeMatChainSpSapShipptNo(sp.getSapShippingpointNo());
					orderFinal.setWeMatChainSpShipptName(sp.getShippingpointName());
				}
				orderFinal.setWeRailHoldBackReOrder(shiptoEntity.getRailHoldBackReOrderFlag());
				
				// Content of tlgnc_shipto_sarea
				ReadWriteDao<ShiptoSareaEntity> shiptoSareaDao =
						getDaoProvider().getShiptoSareaEntityDao();
				Filter<ShiptoSareaEntity> shiptoSareaFilter = shiptoSareaDao.createFilter();
				shiptoSareaFilter.addExpression(Restrictions.eq(ShiptoSareaEntity.ATTR_SHIPTO_ID,
						shiptoEntity.getShiptoId()));
				shiptoSareaFilter.addExpression(Restrictions.eq(ShiptoSareaEntity.ATTR_SAREA_ID,
						order.getSarea().getSareaId()));
				ShiptoSareaEntity shiptoSareaEntity = shiptoSareaDao.findSingle(shiptoSareaFilter);
				
				if (shiptoSareaEntity != null) {
					orderFinal.setWeStSareaShiptoSareaId(shiptoSareaEntity.getShiptoSareaId());
					orderFinal.setWeStSareaFreeDelivery(shiptoSareaEntity.getFreeDeliveryFlag());
					orderFinal.setWeStSareaReleaseSapOpt(shiptoSareaEntity.getReleaseSapOptionalFlag());
					orderFinal.setWeStDistributionDestCd(shiptoSareaEntity.getDistributionDest().getDistributionDestCd());
				}
				
				
				// Content of tlgnc_shipto_truck_type
				ReadWriteDao<ShiptoTruckTypeEntity> shiptoTruckDao =
						getDaoProvider().getShiptoTruckTypeEntityDao();
				Filter<ShiptoTruckTypeEntity> shiptoTruckFilter = shiptoTruckDao.createFilter();
				shiptoTruckFilter.addExpression(Restrictions.eq(
						ShiptoTruckTypeEntity.ATTR_SHIPTO_ID, shiptoEntity.getShiptoId()));
				shiptoTruckFilter.addExpression(Order.asc(
						ShiptoTruckTypeEntity.ATTR_EVALUATION_PRIORITY));
				List<ShiptoTruckTypeEntity> shiptoTruckList =
						shiptoTruckDao.find(shiptoTruckFilter);
				
				if ( (shiptoTruckList != null) && (!shiptoTruckList.isEmpty()) ) {
					ShiptoTruckTypeEntity shTruck = shiptoTruckList.get(0);
					orderFinal.setWeOptimalTruckTypeCd(shTruck.getTruckType().getTruckTypeCd());
				}
				
				
				// Content of tlgnc_shipto_truck_type
				ReadWriteDao<ShiptoWagonTypeEntity> shiptoWagonDao =
						getDaoProvider().getShiptoWagonTypeEntityDao();
				Filter<ShiptoWagonTypeEntity> shiptoWagonFilter = shiptoWagonDao.createFilter();
				shiptoWagonFilter.addExpression(Restrictions.eq(
						ShiptoWagonTypeEntity.ATTR_SHIPTO_ID, shiptoEntity.getShiptoId()));
				shiptoWagonFilter.addExpression(Order.asc(
						ShiptoWagonTypeEntity.ATTR_EVALUATION_PRIORITY));
				List<ShiptoWagonTypeEntity> shiptoWagonList =
						shiptoWagonDao.find(shiptoWagonFilter);
				
				if ( (shiptoWagonList != null) && (!shiptoWagonList.isEmpty()) ) {
					ShiptoWagonTypeEntity shWagon = shiptoWagonList.get(0);
					orderFinal.setWeOptimalWagonTypeCd(shWagon.getWagonType().getWagonTypeCd());
				}
				
			}
		
			
			// Content of tlgns_sap_bp (soldto)
			SapBpEntity soldto = order.getSoldtoSapBp(); // Until 31.3.2010 (prod: until ca 16.4.): this was 'order.getShiptoSapBP', i.e. data from SHIPTO was written instead of SOLDTO!! -> ADN52631
			orderFinal.setOSoldtoSapBpId(soldto.getSapBpId());
			
			orderFinal.setAgSapId(soldto.getSapId());
			orderFinal.setAgMarkedForDeletion(soldto.getMarkedForDeletion());
			orderFinal.setAgName1(soldto.getName1());
			orderFinal.setAgName2(soldto.getName2());
			orderFinal.setAgCountry(soldto.getCountry().getSapCountryCd());
			orderFinal.setAgPostalCode(soldto.getPostalCode());
			orderFinal.setAgCity(soldto.getCity());
			orderFinal.setAgRegion(soldto.getRegion());
			orderFinal.setAgStreet(soldto.getStreet());
			orderFinal.setAgHouseNumber(soldto.getHouseNumber());
			if (soldto.getLang() != null) {
				orderFinal.setAgLangCd(soldto.getLang().getLangCd());
			}
			orderFinal.setAgCustomerClassification(soldto.getCustomerClassification());
			if (soldto.getRailwayStation() != null) {
				orderFinal.setAgRailwayStationId(soldto.getRailwayStation().getRailwayStationId());
			}
			orderFinal.setAgBpIsShipto(soldto.getBpIsShipto());
			orderFinal.setAgBpIsSoldto(soldto.getBpIsSoldto());
			orderFinal.setAgBpIsBillto(soldto.getBpIsBillto());
			orderFinal.setAgChromatReductionMonths(soldto.getChromatReductionMonths());
			
			// Content of tlgns_sap_bp_sarea for the sarea_cd of the order (soldto)
			final Filter<SapBpSareaEntity> agSareaFilter = bpSareaDao.createFilter();
			agSareaFilter.addExpression(Restrictions.eq(SapBpSareaEntity.ATTR_SAP_BP_ID,
					soldto.getSapBpId()));
			agSareaFilter.addExpression(Restrictions.eq(SapBpSareaEntity.ATTR_SAREA_ID,
					order.getSarea().getSareaId()));
			SapBpSareaEntity soldtoSarea = bpSareaDao.findSingle(agSareaFilter);
			
			if (soldtoSarea != null) {
				orderFinal.setAgSapBpSareaId(soldtoSarea.getSapBpSareaId());
				orderFinal.setAgSareaMarkedForDeletion(soldtoSarea.getMarkedForDeletion());
				orderFinal.setAgSalesDistrict(soldtoSarea.getSalesDistrict());
				if (soldtoSarea.getSoffice() != null) {
					orderFinal.setWeSofficeCd(soldtoSarea.getSoffice().getSofficeCd());
					orderFinal.setWeSofficeName(soldtoSarea.getSoffice().getSofficeName());
				}
				if (soldtoSarea.getSgroup() != null) {
					orderFinal.setWeSgroupCd(soldtoSarea.getSgroup().getSgroupCd());
					orderFinal.setWeSgroupName(soldtoSarea.getSgroup().getSgroupName());
					orderFinal.setWeSgroupEmailAddresses(soldtoSarea.getSgroup().getEmailAddresses());
				}
				orderFinal.setAgCustomerGroup(soldtoSarea.getCustomerGroup());
				orderFinal.setAgAbcClass(soldtoSarea.getAbcClass());
				if (soldtoSarea.getCurrency() != null) {
					orderFinal.setAgCurrencyCd(soldtoSarea.getCurrency().getCurrencyCd());
				}
				if (soldtoSarea.getDistributionDest() != null) {
					orderFinal.setAgDistributionDestCd(soldtoSarea.getDistributionDest().getDistributionDestCd());
				}
				orderFinal.setAgCustomerOrderBlock(soldtoSarea.getCustomerOrderBlock());
				orderFinal.setAgCustomerDeliveryBlock(soldtoSarea.getCustomerDeliveryBlock());
				orderFinal.setAgCustomOffice(soldtoSarea.getCustomOffice());
				if (soldtoSarea.getDefaultPlant() != null) {
					orderFinal.setAgDefaultPlantCd(soldtoSarea.getDefaultPlant().getPlantCd());
				}
			}
			
			freezeBillToData(order, orderFinal);
			
			orderFinal.setOSareaId(order.getSarea().getSareaId());
			orderFinal.setSareaDisplayLongname(order.getSarea().getSareaDisplayLongname());
			//Content of tlgnr_sarea.
			orderFinal.setSareaSorgCd(order.getSarea().getSorg().getSorgCd());
			orderFinal.setSareaDistributionChannel(order.getSarea().getDistributionChannel());
			orderFinal.setSareaDivisionCd(order.getSarea().getDivision().getDivisionCd());
			
			orderFinal.setSareaDivisionSegmentCd(order.getSarea().getDivision().getSegmentCode());
			orderFinal.setSareaCompanycodeCd(order.getSarea().getSorg().getCompanycode().getCompanycodeCd());
			orderFinal.setSareaCountryCd(order.getSarea().getSorg().getCompanycode().getRegionCd());
			
			if (order.getPool() != null) {
				orderFinal.setOPoolId(order.getPool().getPoolId());
			}
			
			CarrierEntity commCarrier = order.getCommCarrier();
			if (commCarrier != null) {
				orderFinal.setOCommCarrierId(commCarrier.getCarrierId());
				
				orderFinal.setCommCarrierDisplayName(commCarrier.getDisplayName());
				SapVendorEntity commVendor = commCarrier.getSapVendor();
				orderFinal.setCommCarrierSapVendorId(commVendor.getSapVendorId());
				orderFinal.setCommCarrierVSapId(commVendor.getSapId());
				orderFinal.setCommCarrierVAccountGroup(commVendor.getAccountGroup());
				orderFinal.setCommCarrierVMarkedForDel(commVendor.getMarkedForDeletion());
				orderFinal.setCommCarrierVName1(commVendor.getName1());
				orderFinal.setCommCarrierVName2(commVendor.getName2());
				if (commVendor.getCountry() != null) {
					orderFinal.setCommCarrierVCountryCd(commVendor.getCountry().getSapCountryCd());
				}
				orderFinal.setCommCarrierVPostalCode(commVendor.getPostalCode());
				orderFinal.setCommCarrierVCity(commVendor.getCity());
				orderFinal.setCommCarrierVLocationDist(commVendor.getLocationDistrict());
				orderFinal.setCommCarrierVRegion(commVendor.getRegion());
				orderFinal.setCommCarrierVStreet(commVendor.getStreet());
				orderFinal.setCommCarrierVHouseNumber(commVendor.getHouseNumber());
				if (commVendor.getLang() != null) {
					orderFinal.setCommCarrierVLangCd(commVendor.getLang().getLangCd());
				}
				orderFinal.setCommCarrierVVatRegistNo(commVendor.getVatRegistrationNumber());
				orderFinal.setCommCarrierIsInternal(commVendor.getVnIsInternal());
			}
			
			CarrierEntity execCarrier = order.getExecCarrier();
			if (execCarrier != null) {
				orderFinal.setOExecCarrierId(execCarrier.getCarrierId());
				
				orderFinal.setExecCarrierDisplayName(execCarrier.getDisplayName());
				SapVendorEntity execVendor = execCarrier.getSapVendor();
				orderFinal.setExecCarrierSapVendorId(execVendor.getSapVendorId());
				orderFinal.setExecCarrierVSapId(execVendor.getSapId());
				orderFinal.setExecCarrierVAccountGroup(execVendor.getAccountGroup());
				orderFinal.setExecCarrierVMarkedForDel(execVendor.getMarkedForDeletion());
				orderFinal.setExecCarrierVName1(execVendor.getName1());
				orderFinal.setExecCarrierVName2(execVendor.getName2());
				if (execVendor.getCountry() != null) {
					orderFinal.setExecCarrierVCountryCd(execVendor.getCountry().getSapCountryCd());
				}
				orderFinal.setExecCarrierVPostalCode(execVendor.getPostalCode());
				orderFinal.setExecCarrierVCity(execVendor.getCity());
				orderFinal.setExecCarrierVLocationDist(execVendor.getLocationDistrict());
				orderFinal.setExecCarrierVRegion(execVendor.getRegion());
				orderFinal.setExecCarrierVStreet(execVendor.getStreet());
				orderFinal.setExecCarrierVHouseNumber(execVendor.getHouseNumber());
				if (execVendor.getLang() != null) {
					orderFinal.setExecCarrierVLangCd(execVendor.getLang().getLangCd());
				}
				orderFinal.setExecCarrierVVatRegistNo(execVendor.getVatRegistrationNumber());
				orderFinal.setExecCarrierIsInternal(execVendor.getVnIsInternal());
			}
			
			//Content of tlgnc_shippingpoint.
			ShippingpointEntity sp = order.getShippingpoint();
			if (sp != null) {
				orderFinal.setOShippingpointId(sp.getShippingpointId());
				orderFinal.setSpPlantCd(sp.getPlant().getPlantCd());
				orderFinal.setSpPlantName(sp.getPlant().getPlantName());
				orderFinal.setSpSapShippingpointNo(sp.getSapShippingpointNo());
				orderFinal.setSpShippingpointName(sp.getShippingpointName());
				orderFinal.setSpShipppointTypeCd(sp.getShipppointType().getShipppointTypeCd());
				orderFinal.setSpSapShippingCondCd(sp.getSapShippingCond().getSapShippingCondCd());
				orderFinal.setSpStorageLocation(sp.getStorageLocation());
				
				RailwayStationEntity rs = sp.getRailwayStation();
				if (rs != null) {
					orderFinal.setSpRailwayStationId(rs.getRailwayStationId());
					orderFinal.setSpRsUicCountryCd(rs.getUicCountry().getUicCountryCd());
					orderFinal.setSpRsRailwayStationNo(rs.getRailwayStationNo());
					orderFinal.setSpRsRailwayStationName(rs.getRailwayStationName());
				}
				orderFinal.setSpRailwayStationLp(sp.getRailwayStationLoadingPoint());
				orderFinal.setSpWeighSystemSpCd(sp.getWeighSystemSp().getWeighSystemSpCd());
				orderFinal.setSpLdsServer(sp.getLdsServer());
				orderFinal.setSpShippingpointComment(sp.getShippingpointComment());
				orderFinal.setSpCoordLat(sp.getCoordLat());
				orderFinal.setSpCoordLon(sp.getCoordLon());
				orderFinal.setSpCoordFixFlag(sp.getCoordFixFlag());
				orderFinal.setSpGeofenceRadius(sp.getGeofenceRadius());
				orderFinal.setSpFvlMixShiptoPerWagonFl(sp.getFvlMixShiptoPerWagonFlag());
			}
			
			//Content of tlgnc_railway_station of ACTUAL_DEP_RAILWAY_STATION_ID
			RailwayStationEntity rs = order.getActualDepRailwayStation();
			if (rs != null) {
				orderFinal.setOActDepRailwayStationId(rs.getRailwayStationId());
				orderFinal.setOActDepRsUicCountryCd(rs.getUicCountry().getUicCountryCd());
				orderFinal.setOActDepRsStationNo(rs.getRailwayStationNo());
				orderFinal.setOActDepRsStationName(rs.getRailwayStationName());
				
				RailwayStationEntity commRailwayStation = LogonUtil.getCommercialRailwayStationEntity(shiptoSapBp.getRailwayStation(), getDaoProvider().getRailwayStationCommEntityDao());
				
				orderFinal.setOActDepRsComStationName(commRailwayStation.getRailwayStationName());
				orderFinal.setOActDepRsComStationNo(commRailwayStation.getRailwayStationNo());
				orderFinal.setOActDepRsComUicCntryCd(commRailwayStation.getUicCountry().getUicCountryCd());
			}
			
			//orderFinal.setOSareaCd(order.getSarea().getSareaCd());
			if (order.getOrderCreationType() != null) {
				orderFinal.setOOrderCreationTypeCd(order.getOrderCreationType().getOrderCreationTypeCd());
			}
			
			orderFinal.setODeliveryTypeCd(order.getDeliveryType().getDeliveryTypeCd());
			if (order.getDistributionDest() != null) {
				orderFinal.setODistributionDestCd(order.getDistributionDest().getDistributionDestCd());
			}
			orderFinal.setODeliveryFromDat(order.getDeliveryFromDat());
			orderFinal.setODeliveryToDat(order.getDeliveryToDat());
			
			calculateAndSetDeliveryNetTime(order, orderFinal, shiptoEntity);
			
			orderFinal.setOActualDeliveryDat(order.getActualDeliveryDat());
			
			// orderFinal.setOCalcLateDeliveryFlag(...); -> this was moved forward, because we need the biz events for the decision
			
			orderFinal.setOTranspDurPlanMin(order.getTransportDurationMin());
			orderFinal.setOTranspDurPlanGoogleMin(order.getTransportDurPlanGoogleMin());
			
			calculateAndSetLoadTimingReportValues(order, orderFinal);
			
			orderFinal.setOTranspDistancePlanM(order.getTransportDistanceM());
			orderFinal.setOCustomsNotificationNumber(order.getCustomsNotificationNumber());
			if (order.getDeliveryTrspZone() != null) {
				orderFinal.setODeliveryTrspZoneCd(order.getDeliveryTrspZone().getTrspZoneCd());
			}
			if (order.getSpi() != null) {
				orderFinal.setOSpiCd(order.getSpi().getOrderSpiCd());
			}
			orderFinal.setOContactPersonTxt(order.getContactPersonTxt());
			orderFinal.setOContactPersonSignatureTxt(order.getContactPersonSignatureTxt());
			orderFinal.setONoPdaOrRailEventsFlag(order.getNoPdaOrRailEventsFlag());
			orderFinal.setOReleaseSapFlag(order.getReleaseSapFlag());
			orderFinal.setoForceToSapFlag(order.getForceToSapFlag());
			orderFinal.setOFreeDeliveryFlag(order.getFreeDeliveryFlag());
			orderFinal.setOSapSuppressMinChargeFlag(order.getSapSuppressMinChargeFlag());
			orderFinal.setOOrderConfEmailFlag(order.getOrderConfEmailFlag());
			orderFinal.setOOrderConfEmailAdresses(order.getOrderConfEmailAdresses());
			orderFinal.setOOrderConfFaxFlag(order.getOrderConfFaxFlag());
			orderFinal.setOOrderConfFaxNumber(order.getOrderConfFaxNumber());
			orderFinal.setOOrderConfSmsFlag(order.getOrderConfSmsFlag());
			orderFinal.setOOrderConfSmsNumber(order.getOrderConfSmsNumber());
			orderFinal.setODelivnoteEmailFlag(order.getDelivnoteEmailFlag());
			orderFinal.setODelivnoteFaxFlag(order.getDelivnoteFaxFlag());
			orderFinal.setODelivnoteSinglePaperFlag(order.getDelivnoteSinglePaperFlag());
			orderFinal.setOCustWeborderNote(order.getCustWeborderNote());
			orderFinal.setONoteOrderMgmt(order.getNoteOrderMgmt());
			orderFinal.setONoteDelivery(order.getNoteDelivery());
			orderFinal.setONoteDispatching(order.getNoteDispatching());
			orderFinal.setONoteTruckDriver(order.getNoteTruckDriver());
			orderFinal.setONoteRailwayDisp(order.getNoteRailwayDisp());
			orderFinal.setOCarrierDeliveryNote(order.getCarrierDeliveryNote());
			orderFinal.setOCarrierTruckDriverNote(order.getCarrierTruckDriverNote());
			orderFinal.setOCarrierDispatchingNote(order.getCarrierDispatchingNote());
			orderFinal.setOCustDeliveryNoteComment(order.getCustDeliveryNoteComment());
			
			//Content of tlgnc_truck.
			TruckEntity truckOrder = order.getDeliveredTruck();
			if (truckOrder != null) {
				orderFinal.setODeliveredTruckId(truckOrder.getTruckId());
				orderFinal.setTruckName(truckOrder.getTruckName());
				orderFinal.setTruckPlateNo(truckOrder.getTruckPlateNo());
				orderFinal.setTruckTransportCatCd(truckOrder.getTruckTransportCat().getTruckTransportCatCd());
				orderFinal.setTruckTypeCd(truckOrder.getTruckType().getTruckTypeCd());
				orderFinal.setTruckMaxGrossWeightT(truckOrder.getMaxGrossWeightT());
				orderFinal.setTruckTareWeightT(truckOrder.getTareWeightT());
				orderFinal.setTruckNrChambers(truckOrder.getNrChambers());
				orderFinal.setTruckMaxNetWChamber1T(truckOrder.getMaxNetWeightChamber1T());
				orderFinal.setTruckMaxNetWChamber2T(truckOrder.getMaxNetWeightChamber2T());
				orderFinal.setTruckSapInternalFlag(truckOrder.getSapVehicleIsInternalFlag());
				// Not reliable! Therefore removed: orderFinal.setTruckPdaAvailableFlag(truckOrder.getPdaAvailableFlag()); 
			}
			
			//Content of tlgnc_wagon.
			WagonEntity wagoOrder = order.getDeliveredWagon();
			if (wagoOrder != null) {
				orderFinal.setODeliveredWagonId(wagoOrder.getWagonId());
				orderFinal.setWagonNo(wagoOrder.getWagonNo());
				orderFinal.setWagonTypeCd(wagoOrder.getWagonType().getWagonTypeCd());
			    WagonTypeBsEntity wt = wagoOrder.getWagonType();
			    orderFinal.setWagonTypeName(wt.getWagonTypeName());
			    orderFinal.setWagonUicClassCd(wt.getWagonUicClassCd());
			    orderFinal.setWagonAxisCount(wt.getAxisCount());
			    orderFinal.setWagonLimitCreditNoteT(wt.getLimitCreditNoteT());
			    
			    if (wt.getWagonTrspCat() != null) {
			    	orderFinal.setWagonTrspCatCd(wt.getWagonTrspCat().getWagonTrspCatCd()); 
			    }
			    orderFinal.setWagonTareWeightT(wagoOrder.getTareWeightT());
			    
			    if (wagoOrder.getMaxGrossWeightT() == null) {
			    	orderFinal.setWagonMaxGrossWeightT(wt.getMaxGrossWeightT());
			    }
			    else {
			    	orderFinal.setWagonMaxGrossWeightT(wagoOrder.getMaxGrossWeightT());
				}
			    if (wagoOrder.getVolumeM3() == null) {
			    	orderFinal.setWagonVolumeM3(wt.getVolumeM3());
			    }
			    else {
			    	orderFinal.setWagonVolumeM3(wagoOrder.getVolumeM3());
				}
			}
			
			
			//Content of tlgnc_driver, tlgnu_user, tlgnc_person
			DriverEntity driver = order.getDeliveredDriver();
			if (driver != null) {
				orderFinal.setODeliveredDriverId(order.getDeliveredDriver().getDriverId());
				UserEntity user =driver.getUser();
				orderFinal.setDriverName(user.getPerson().getName());
				orderFinal.setDriverFirstname(user.getPerson().getFirstname());
				orderFinal.setDriverUserTypeCd(user.getUserType().getUserTypeCd());
				
				if (driver.getUser().getSapBp() != null) {
					orderFinal.setDriverSapBpNo(driver.getUser().getSapBp().getSapId());
				}
				if (driver.getUser().getSapVendor() != null) {
					orderFinal.setDriverSapVendorNo(driver.getUser().getSapVendor().getSapId());
				}
				orderFinal.setDriverCertifiedFromDat(driver.getCertifiedFromDat());
				orderFinal.setDriverCertifiedToDat(driver.getCertifiedToDat());
			}
			
			orderFinal.setOSentSalesdocToSapTs(order.getSentSalesdocToSapTs());
			orderFinal.setOSentShipmentdocToSapTs(order.getSentShipmentdocToSapTs());
			
			// Content of tlgnc_assignment_part
			// Find LoadEntity: LOADING_TS -> latest
			LoadEntity loadTypeLoad = getLatestLoad(order);
			
			ReadWriteDao<AssignmentPartEntity> asspDao =
					getDaoProvider().getAssignmentPartEntityDao();
			
			AssignmentPartEntity asspLoad = null;
			if (loadTypeLoad != null) {
				//loading part 
				Filter<AssignmentPartEntity> asspFilter = asspDao.createFilter();
				asspFilter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_LOAD, loadTypeLoad));
				asspLoad = asspDao.findSingle(asspFilter);
				
				if (asspLoad != null) {
					orderFinal.setAsspLoadingPlannedStart(asspLoad.getPlannedStart());
					orderFinal.setAsspLoadingRealStart(asspLoad.getRealStart());
				}
			}
			
			//unloading part
			Filter<AssignmentPartEntity> asspFilter = asspDao.createFilter();
			asspFilter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ORDER, order));
			AssignmentPartEntity asspUnload = asspDao.findSingle(asspFilter);
			
			if (asspUnload != null) {
				orderFinal.setAsspUnloadingPlannedStart(asspUnload.getPlannedStart());
				orderFinal.setAsspUnloadingRealStart(asspUnload.getRealStart());
			}
			
			final AssignmentEntity assignment;
			if (asspUnload != null && asspUnload.getAssignment() != null) {
				assignment = asspUnload.getAssignment();
			}
			else if (asspLoad != null && asspLoad.getAssignment() != null) {
				assignment = asspLoad.getAssignment();
			}
			else {
				assignment = null;
			}
			
			
			// Content of tlgnc_bizevent.
			ReadWriteDao<BizeventEntity> bizeventDao = getDaoProvider().getBizeventEntityDao();
			Filter<BizeventEntity> bizeventFilter = bizeventDao.createFilter();
			
			final Expression orderIdMatches = Restrictions.eq(BizeventEntity.ATTR_ORDER_ID, order.getOrderId());
			
			if (assignment != null) {
				final Expression orderIdNull = Restrictions.isNull(BizeventEntity.ATTR_ORDER_ID);
				final Expression assignmentIdMatches = Restrictions.eq(BizeventEntity.ATTR_ASSIGNMENT_ID,
						assignment.getAssignmentId());
				final Expression orderIdNullAndAssignmentIdMatches = Restrictions.and(orderIdNull, assignmentIdMatches);
				
				bizeventFilter.addExpression(Restrictions.or(orderIdMatches, orderIdNullAndAssignmentIdMatches));
			}
			else {
				bizeventFilter.addExpression(orderIdMatches);
			}

			bizeventFilter.addExpression(Order.asc(BizeventEntity.ATTR_EVENT_TS));
			
			final List<BizeventEntity> bizeventList = bizeventDao.find(bizeventFilter);
			
			
			//
			if (bizeventList != null) {
				double geoeventCount = 0;
				double trafficjamCount = 0;
				long trafficjamDurationMin = 0;
				Date trafficjamStartTmp = null;
				
				final Map<Long, Date> bizEventTimestampMap = new HashMap<Long, Date>();
				for (final Long bizEvent : Constant.BIZEVENTS_WITH_TS_FOR_ORDER_FINAL) {
					bizEventTimestampMap.put(bizEvent, null);
				}

				for (final BizeventEntity bizevent: bizeventList) {
					final Long bizTypeCd = bizevent.getBizeventType().getBizeventTypeCd();
					final Date bizEventTs = bizevent.getEventTs();
					
					// bizevents where TS is used
					if (Constant.BIZEVENTS_WITH_TS_FOR_ORDER_FINAL.contains(bizTypeCd)) {
						if (Constant.BIZEVENTS_FIRST_TS_NEEDED_FOR_ORDER_FINAL.contains(bizTypeCd)) {
							if (bizEventTimestampMap.get(bizTypeCd) == null) {
								bizEventTimestampMap.put(bizTypeCd, bizEventTs);
							}
						}
						else {
							bizEventTimestampMap.put(bizTypeCd, bizEventTs);
						}
					}
					// bizevents where TS is not (directly) used
					else if (Constant.BIZEVENT_TYPE_POSITION == bizTypeCd) {
						geoeventCount++;
					}
					else if (Constant.BIZEVENT_TYPE_TRAFFIC_JAM_START == bizTypeCd) {
						trafficjamStartTmp = bizevent.getEventTs();
					}
					else if (Constant.BIZEVENT_TYPE_TRAFFIC_JAM_END == bizTypeCd) {
						if (trafficjamStartTmp != null) {
							long diff = bizevent.getEventTs().getTime() - trafficjamStartTmp.getTime();
							trafficjamDurationMin += diff / (60 * 1000); 
							trafficjamCount++;
						}
						trafficjamStartTmp = null;
					}
				}
				
				// set geoevent count and traffic jam related parameters
				orderFinal.setBizGeoeventCount(geoeventCount);
				orderFinal.setBizTrafficjamCount(trafficjamCount);
				if (trafficjamDurationMin < 1000000) {
					orderFinal.setBizTrafficjamDurationMin(trafficjamDurationMin);
				}			
				
				// time stamps
				final Date bizNewTs = bizEventTimestampMap.get(Constant.BIZEVENT_TYPE_NEW);
				orderFinal.setBizNewTs(bizNewTs);

				final Date bizTransToPoolTs = bizEventTimestampMap.get(Constant.BIZEVENT_TYPE_TRANSMITTED_TO_POOL);
				orderFinal.setBizTransmPoolTs(bizTransToPoolTs);
				
				final Date bizTransToPoolAckTs = bizEventTimestampMap.get(Constant.BIZEVENT_TYPE_POOL_ACK);
				orderFinal.setBizTransmPoolAckTs(bizTransToPoolAckTs);

				final Date bizTransToCommCarrierTs = bizEventTimestampMap.get(Constant.BIZEVENT_TYPE_TRANSMITTED_TO_COMM_CARRIER);
				orderFinal.setBizTransmCommCarrierTs(bizTransToCommCarrierTs);
				
				final Date bizTransToCommCarrierAckTs = bizEventTimestampMap.get(Constant.BIZEVENT_TYPE_COMM_CARRIER_ACK);
				orderFinal.setBizTransmCommCarrierAckTs(bizTransToCommCarrierAckTs);
				
				final Date bizTransSetExecCarrierTs = bizEventTimestampMap.get(Constant.BIZEVENT_TYPE_SET_EXEC_CARRIER);
				orderFinal.setBizTransmExecCarrierTs(bizTransSetExecCarrierTs);
				
				final Date bizDispatchedTs = bizEventTimestampMap.get(Constant.BIZEVENT_TYPE_DISPATCHED);
				orderFinal.setBizDispatchedTs(bizDispatchedTs);
				
				final Date bizTransToTruckTs = bizEventTimestampMap.get(Constant.BIZEVENT_TYPE_TRANSMITTED_TO_TRUCK);
				orderFinal.setBizTransmTruckTs(bizTransToTruckTs);
				
				final Date bizTransToTruckAckTs = bizEventTimestampMap.get(Constant.BIZEVENT_TYPE_TRANSMITTED_TO_TRUCK_ACK);
				orderFinal.setBizTransmTruckAckTs(bizTransToTruckAckTs);
				
				final Date bizTransToRailTs = bizEventTimestampMap.get(Constant.BIZEVENT_TYPE_TRANSMITTED_TO_RAIL);
				orderFinal.setBizTransRailCompanyTs(bizTransToRailTs);
				
				final Date bizArrivedAtLPTs = bizEventTimestampMap.get(Constant.BIZEVENT_TYPE_ARRIVED_AT_LOADINGPOINT);
				orderFinal.setBizEnterSpTs(bizArrivedAtLPTs);
				
				final Date bizStartLoadTs = bizEventTimestampMap.get(Constant.BIZEVENT_TYPE_LOADING);
				orderFinal.setBizStartLoadTs(bizStartLoadTs);
				
				final Date bizLoadedTs = bizEventTimestampMap.get(Constant.BIZEVENT_TYPE_LOADED);
				orderFinal.setBizEndLoadTs(bizLoadedTs);
				
				final Date bizShippingTs = bizEventTimestampMap.get(Constant.BIZEVENT_TYPE_SHIPPING);
				orderFinal.setBizLeaveSpTs(bizShippingTs);
				
				final Date bizArrivedAtDelPointTs = bizEventTimestampMap.get(Constant.BIZEVENT_TYPE_ARRIVED_AT_DELIVERY_POINT);
				orderFinal.setBizEnterStTs(bizArrivedAtDelPointTs);
				
				final Date bizUnloadingTs = bizEventTimestampMap.get(Constant.BIZEVENT_TYPE_UNLOADING);
				orderFinal.setBizStartUnloadTs(bizUnloadingTs);
				
				final Date bizDeliveredTs = bizEventTimestampMap.get(Constant.BIZEVENT_TYPE_DELIVERED);
				orderFinal.setBizEndUnloadTs(bizDeliveredTs);
				
				final Date bizLeaveDelPoint = bizEventTimestampMap.get(Constant.BIZEVENT_TYPE_LEAVE_SHIPTO);
				orderFinal.setBizLeaveStTs(bizLeaveDelPoint);
				
				// KPI 1
				// BIZ_ENTER_SP_TS bizArrivedAtDelPointTs Biz Event
				// O_ACTUAL_END_LOAD_DAT last load's end loading time set in FrozenTablesServiceBean#calculateAndSetLoadTimingReportValues
				Date arrivalTime;
				if (LogonUtil.isTruckOrder(order.getDeliveryType().getDeliveryTypeCd())) {
					arrivalTime = getArrivalTime(order.getDeliveryFromDat(),
							bizArrivedAtDelPointTs, orderFinal.getBizEndLoadTs(),
							order.getTransportDurationMin().intValue());					
				} else {
					arrivalTime = order.getActualDeliveryDat();
				}
				Boolean notOnTime = deliveredEarlyOrLate(order.getDeliveryFromDat(),
						order.getDeliveryToDat(), arrivalTime);
				//Note: the name "LateDeliveryFlag" is not totally correct anymore. Based on SAP reporting spec., this is flag indicates late OR early deliveries.
				orderFinal.setOCalcLateDeliveryFlag(notOnTime);
				orderFinal.setOArrivalTs(arrivalTime);

				//SAP BW KPI 20
				if (bizArrivedAtDelPointTs != null && bizShippingTs != null) {
					Long enterStTsMs = bizArrivedAtDelPointTs.getTime();
					Long leaveSpTsMs = bizShippingTs.getTime();
					Long actualDrivingTimeMs = enterStTsMs - leaveSpTsMs;
					Long actualDrivingTimeMin = TimeUnit.MILLISECONDS.toMinutes(actualDrivingTimeMs);
					orderFinal.setActualDrivingTimeMin(actualDrivingTimeMin);
				}
				
				//SAP BW KPI 0.1
				SapOutgoingDataServiceBean sapOutGoingDataServiceBean = new SapOutgoingDataServiceBean();
				String LIFEX = sapOutGoingDataServiceBean.getExternalDeliveryNumber(order);
				orderFinal.setoLifexNo(LIFEX);
				
				// Net Unloading time 
				// SAP BW KPI 13 , LDUR_5
				if (bizUnloadingTs != null && bizDeliveredTs != null) {
					long endUnloadMs = bizDeliveredTs.getTime();
					long startUnloadMs = bizUnloadingTs.getTime();
					long diffMs = endUnloadMs - startUnloadMs;
					orderFinal.setBizUnloadingMin(diffMs / 1000 / 60);
				}
				
				// waiting reason
				final ReadOnlyDao<DriverWaitingreasonEntity> waitingReasonDao = getDaoProvider().getDriverWaitingreasonEntityDao();
				
				if (orderFinal.getBizEnterSpTs() != null && orderFinal.getBizEndLoadTs() != null && orderFinal.getODeliveredDriverId() != null && orderFinal.getOShippingpointId() != null){
					Filter<DriverWaitingreasonEntity> waitingReasonFilter = waitingReasonDao.createFilter();
					waitingReasonFilter.addExpression(Restrictions.ge(DriverWaitingreasonEntity.ATTR_EVENT_TS, orderFinal.getBizEnterSpTs()));
					waitingReasonFilter.addExpression(Restrictions.le(DriverWaitingreasonEntity.ATTR_EVENT_TS, orderFinal.getBizEndLoadTs()));
					waitingReasonFilter.addExpression(Restrictions.eq(DriverWaitingreasonEntity.ATTR_DRIVER_ID, orderFinal.getODeliveredDriverId()));
					waitingReasonFilter.addExpression(Restrictions.eq(DriverWaitingreasonEntity.ATTR_SHIPPINGPOINT_ID, orderFinal.getOShippingpointId()));
					waitingReasonFilter.addExpression(Order.desc(DriverWaitingreasonEntity.ATTR_EVENT_TS));
					List<DriverWaitingreasonEntity> waitingReason = waitingReasonDao.findNRecs(0, 1, waitingReasonFilter);
					if (waitingReason != null && !waitingReason.isEmpty()) {
						DriverWaitingreasonEntity entity = waitingReason.get(0);
						orderFinal.setBizWaitingtimeReasonSpCd(entity.getWaitingtimeReason().getWaitingtimeReasonCd());
					}
				}
				
				if (orderFinal.getBizEnterStTs() != null && orderFinal.getBizEndUnloadTs() != null && orderFinal.getODeliveredDriverId() != null && shiptoEntity.getShiptoId() != null){
					Filter<DriverWaitingreasonEntity> waitingReasonFilter = waitingReasonDao.createFilter();
					waitingReasonFilter.addExpression(Restrictions.ge(DriverWaitingreasonEntity.ATTR_EVENT_TS, orderFinal.getBizEnterStTs()));
					waitingReasonFilter.addExpression(Restrictions.le(DriverWaitingreasonEntity.ATTR_EVENT_TS, orderFinal.getBizEndUnloadTs()));
					waitingReasonFilter.addExpression(Restrictions.eq(DriverWaitingreasonEntity.ATTR_DRIVER_ID, orderFinal.getODeliveredDriverId()));
					waitingReasonFilter.addExpression(Restrictions.eq(DriverWaitingreasonEntity.ATTR_SHIPTO_ID, shiptoEntity.getShiptoId()));
					waitingReasonFilter.addExpression(Order.desc(DriverWaitingreasonEntity.ATTR_EVENT_TS));
					List<DriverWaitingreasonEntity> waitingReason = waitingReasonDao.findNRecs(0, 1, waitingReasonFilter);
					if (waitingReason != null && !waitingReason.isEmpty()) {
						DriverWaitingreasonEntity entity = waitingReason.get(0);
						orderFinal.setBizWaitingtimeReasonStCd(entity.getWaitingtimeReason().getWaitingtimeReasonCd());
					}
				}

				
				//Net Reaction time order manager
				if (bizNewTs != null) {
					if (bizTransToPoolTs != null) {
						long diffMin = calcDiffMin(bizTransToPoolTs.getTime(), bizNewTs.getTime());
						orderFinal.setBizReactTimeOrdMngMin(diffMin);
					} 
					//FIXME (emoke Nov 30, 2011): Split carrier to executional and commercial carrier! executional is not handled.
					else if (bizTransToCommCarrierTs != null) {
						long diffMin = calcDiffMin(bizTransToCommCarrierTs.getTime(), bizNewTs.getTime());
						orderFinal.setBizReactTimeOrdMngMin(diffMin);
					}
				}
				
				//Net Reaction time pool manager
				if (bizTransToPoolTs != null) {
					if (bizTransToPoolAckTs != null) {
						long diffMin = calcDiffMin(bizTransToPoolAckTs.getTime(), bizTransToPoolTs.getTime());
						orderFinal.setBizReactTimePoolMngMin(diffMin);
					}
					else if (bizTransToCommCarrierTs != null) {
						long diffMin = calcDiffMin(bizTransToCommCarrierTs.getTime(), bizTransToPoolTs.getTime());
						orderFinal.setBizReactTimePoolMngMin(diffMin);
					}		 
				}
				
				//Net Reaction time dispatcher
				if (bizTransToCommCarrierTs != null) {
					if (bizTransToCommCarrierAckTs != null) {
						long diffMin = calcDiffMin(bizTransToCommCarrierAckTs.getTime(), bizTransToCommCarrierTs.getTime());
						orderFinal.setBizReactTimeDispatcherMin(diffMin);
					}	 
					else if (bizDispatchedTs != null) {
						long diffMin = calcDiffMin(bizDispatchedTs.getTime(), bizTransToCommCarrierTs.getTime());
						orderFinal.setBizReactTimeDispatcherMin(diffMin);
					} 
				}
				
				//Net Reaction time truck driver
				if ((bizTransToTruckTs != null) && (bizTransToTruckAckTs != null)) {
					long diffMin = calcDiffMin(bizTransToTruckAckTs.getTime(), bizTransToTruckTs.getTime());
					orderFinal.setBizReactTimeTruckDrvMin(diffMin);
				}
				
				//Net Delivery time window
				if ((order.getDeliveryToDat() != null) && (order.getDeliveryFromDat() != null)) {
					long diffMin = calcDiffMin(order.getDeliveryToDat().getTime(), order.getDeliveryFromDat().getTime());
					orderFinal.setBizReactTimeNetWindowMin(diffMin);
				}
				
				//Net Loading time
				Date startLoadingDate = orderFinal.getBizStartLoadTs();
				Date endLoadingDate = orderFinal.getBizEndLoadTs();
				if (startLoadingDate != null && endLoadingDate != null) {
					long endLoadMs =endLoadingDate.getTime();
					long startLoadMs= startLoadingDate.getTime();
					long diffMs = endLoadMs - startLoadMs;
					orderFinal.setBizLoadingTimeMin(diffMs/1000/60);
				}
				
				//Net driving time
				if ( (bizArrivedAtDelPointTs != null) && (bizLoadedTs != null) && (driver != null) ) {
					
					//load driver's breaks
					ReadWriteDao<DriverBreakEntity> driverDao =
							getDaoProvider().getDriverBreakEntityDao();
					Filter<DriverBreakEntity> driverFilter = driverDao.createFilter();
					driverFilter.addExpression(Restrictions.ge(DriverBreakEntity.ATTR_START_DAT,
							bizLoadedTs));
					driverFilter.addExpression(Restrictions.le(DriverBreakEntity.ATTR_END_DAT,
							bizArrivedAtDelPointTs));
					driverFilter.addExpression(Restrictions.eq(DriverBreakEntity.ATTR_DRIVER,
							driver));
					driverFilter.addExpression(Restrictions.eq(DriverBreakEntity.ATTR_ACTIVE_FLAG, 
							Boolean.TRUE));
					List<DriverBreakEntity> breakList = driverDao.find(driverFilter);
					
					long breakCount = 0;
					long breakMin = 0;
					
					if (breakList != null) {
						breakCount = breakList.size();
						
						for (DriverBreakEntity driverBreak: breakList) {
							long diff = calcDiffMin(driverBreak.getEndDat().getTime(), driverBreak.getStartDat().getTime());
							breakMin += diff;		 
						}
					}
					
					orderFinal.setBizBreakDrivingCount(breakCount);
					orderFinal.setBizBreakDrivingDurationMin(breakMin);
					
					//driving time
					long diffMin = calcDiffMin(bizArrivedAtDelPointTs.getTime(), bizLoadedTs.getTime());
					orderFinal.setBizCalcDrivingTimeMin(diffMin);
					//driving time minus breaks
					diffMin -= breakMin;
					orderFinal.setBizCalcNetDrivingTimeMin(diffMin);
				}
				
				//SAP BW KPI 3
				//Net Cycle time at shipping station 
				Date BIZ_ENTER_SP_TS = bizArrivedAtLPTs;
				Date BIZ_LEAVE_SP_TS = bizShippingTs;
				
				//Enter and leave timestamp might be null, try and fill them according to fallback logic.
				if (BIZ_ENTER_SP_TS == null) {
					if (bizDispatchedTs != null) {
						BIZ_ENTER_SP_TS = bizDispatchedTs;
					}
				}
				
				if (BIZ_LEAVE_SP_TS == null && bizLoadedTs != null) {
					Date BIZ_END_LOAD_TS = bizLoadedTs;
					Long sumDelay = 0l;
					List<Long> loadingPointsVisited = new ArrayList<Long>();
					for (OrderItemEntity orderItem : order.getOrderItems()) {
						LoadEntity load = orderItem.getLoad();
						if (load != null) {
							LoadingpointEntity loadingPoint = load.getLoadingpoint();
							if (loadingPoint != null) {
								Long delay = loadingPoint.getLeaveLpDelayMin();
								Long loadingPointID = loadingPoint.getLoadingpointId();
								if (delay == null) {
									delay = 0l;
								}
								if (!loadingPointsVisited.contains(loadingPointID)) {
									loadingPointsVisited.add(loadingPointID);
									sumDelay += delay;
								}
							}
						}
					}
					Calendar calculatedLeaveSp = Calendar.getInstance();
					calculatedLeaveSp.setTime(BIZ_END_LOAD_TS);
					calculatedLeaveSp.add(Calendar.MINUTE, sumDelay.intValue());
					BIZ_LEAVE_SP_TS = calculatedLeaveSp.getTime();
				}
				
				if ((BIZ_ENTER_SP_TS != null) && (BIZ_LEAVE_SP_TS != null)) {
					long diffMin = calcDiffMin(BIZ_LEAVE_SP_TS.getTime(), BIZ_ENTER_SP_TS.getTime());
					orderFinal.setBizCycleTimeAtSpMin(diffMin);
				}
				
				//	 Net Cycle time at ship-to 
				if ((bizArrivedAtDelPointTs != null) && (bizLeaveDelPoint != null)) {
					long diffMin = calcDiffMin(bizLeaveDelPoint.getTime(), bizArrivedAtDelPointTs.getTime());
					orderFinal.setBizCycleTimeAtShiptoMin(diffMin);
				}				
			}
			
			
			final UserEntity orderCreatorUser = getDaoProvider().getUserEntityDao().findById(order.getCtlCreUid());
			final String orderCreatorName = LogonUtil.getFullName(orderCreatorUser,
					(int) OrderFinalEntity.DBSIZE_O_CTL_CRE_UID_NAME);
			orderFinal.setOCtlCreUidName(orderCreatorName);
			orderFinal.setOCtlCreUid(order.getCtlCreUid());
			orderFinal.setOCtlCreTs(order.getCtlCreTs());

			final UserEntity orderModifierUser = getDaoProvider().getUserEntityDao().findById(
					order.getCtlModUid());
			final String orderModifierName = LogonUtil.getFullName(orderModifierUser, (int)OrderFinalEntity.DBSIZE_O_CTL_MOD_UID_NAME);
			orderFinal.setOCtlModUidName(orderModifierName);
			orderFinal.setOCtlModUid(order.getCtlModUid());
			orderFinal.setOCtlModTs(order.getCtlModTs());
			
			// Check for sales area level threshold

			// Fallback
			//SAP BW KPI 9
			Date O_CREATION_DAT = order.getCtlCreTs();
			LOG.debug("O_CREATION_DAT" + O_CREATION_DAT.toString());
			Date O_DELIVERY_TO_DAT = order.getDeliveryToDat();
			LOG.debug("O_DELIVERY_TO_DAT" + O_DELIVERY_TO_DAT.toString());
			int diffDays = DateTimeUtils.getDayDifference(O_CREATION_DAT, O_DELIVERY_TO_DAT);
			LOG.debug("diffDays" + diffDays);
			Boolean oShortTermOrderFlag = true;
			
			if (diffDays >= 2 ) {
				oShortTermOrderFlag = false;
			}			
			else if(diffDays == 1)
			{
				int shortTermHours;
				int shortTermMinutes;
				String timeToCompare;
				
				DeliveryTypeBsEntity typeEntity = order.getDeliveryType();
				SapTrspModeBsEntity trspModeEntity = typeEntity.getSapTrspMode();
				String sapTrspModeCd = trspModeEntity.getSapTrspModeCd();
				//Values are defined by SAP. 10=LKW/Truck, 50=Bahn/Train, 00=Ex Plant (Pickup). Note the leading zero for 00, therefore treat as text.
				
				//10=truck, 50=train
				if (Constant.TRANSPORTATION_MODE_TRUCK.equals(sapTrspModeCd)) {
					timeToCompare = getConfigBean().getAppDefaultShortTermOrder10();
				}
				else {
					timeToCompare = getConfigBean().getAppDefaultShortTermOrder50();
				}
				//timeToCompare is in format "HH:mm"
				String hoursString = Character.toString(timeToCompare.charAt(0))
						+ Character.toString(timeToCompare.charAt(1));
				shortTermHours = Integer.parseInt(hoursString);

				String minutesString = Character.toString(timeToCompare.charAt(3))
						+ Character.toString(timeToCompare.charAt(4));
				shortTermMinutes = Integer.parseInt(minutesString);

				Calendar oCreationDatCal = Calendar.getInstance();
				oCreationDatCal.setTime(O_CREATION_DAT);
				int creationHours = oCreationDatCal.get(Calendar.HOUR_OF_DAY);
				int creationMinutes = oCreationDatCal.get(Calendar.MINUTE);
				
				if ((creationHours < shortTermHours)
						|| (creationHours == shortTermHours && creationMinutes <= shortTermMinutes)) {
					oShortTermOrderFlag = false;
				}
			}
			LOG.debug("oShortTermOrderFlag" + oShortTermOrderFlag.toString());
			orderFinal.setOShortTermOrderFlag(oShortTermOrderFlag);
			
			orderFinal.setOTripIncInStatisticsFlag(Boolean.FALSE);
			if (order.getReschedulingFlag() != null) {				
				orderFinal.setOReschedulingFlag(order.getReschedulingFlag());
			}
			else{
				orderFinal.setOReschedulingFlag(Boolean.FALSE);
			}
			if (order.getNoPdaOrRailEventsFlag() != null) {				
				orderFinal.setONoPdaOrRailEventsFlag(order.getNoPdaOrRailEventsFlag());
			}
			else{
				orderFinal.setONoPdaOrRailEventsFlag(Boolean.FALSE);
			}
			orderFinal.setReportSvcLevelRelevantFlag(Boolean.FALSE);
			
			// This flag must be correctly set (and later updated!) based on the decision, if all
			// its items are financially OK, i.e. no 'null' values in cost/price/margin etc
			orderFinal.setReportFinishedCommFlag(Boolean.TRUE);
			
			// "Location not fixed" address for Shipto
			if (shiptoSapBp.getTreatAsOnetimeFlag().booleanValue()) {
				orderFinal.setOLnfCoordLat(order.getLnfCoordLat());
				orderFinal.setOLnfCoordLon(order.getLnfCoordLon());
				orderFinal.setOLnfSiteName(order.getLnfSiteName());
				orderFinal.setOLnfSiteStreetHouseNo(order.getLnfSiteStreetHouseNo());
				orderFinal.setOLnfSiteCountry(order.getLnfSiteCountry());
				orderFinal.setOLnfSitePostalCode(order.getLnfSitePostalCode());
				orderFinal.setOLnfSiteCity(order.getLnfSiteCity());
				orderFinal.setOLnfSiteVatNo(order.getLnfSiteVatNo());
			}
			
			// One-Time Soldto address
			if (soldto.getTreatAsOnetimeFlag().booleanValue()) {
				orderFinal.setOOneTimeSoldtoName(order.getOneTimeSoldtoName());
				orderFinal.setOOneTimeSoldtoStreetHno(order.getOneTimeSoldtoStreetHouseno());
				orderFinal.setOOneTimeSoldtoCity(order.getOneTimeSoldtoCity());
				orderFinal.setOOneTimeSoldtoPostalCode(order.getOneTimeSoldtoPostalCode());
				orderFinal.setOOneTimeSoldtoCountry(order.getOneTimeSoldtoCountry());
				orderFinal.setOOneTimeSoldtoVatNo(order.getOneTimeSoldtoVatNo());
			}
			
			// Extra costs
			if (order.getExtraCost1() != null) {
				orderFinal.setExtraCost1(order.getExtraCost1().getSapOrderExtraCostCd());
			}
			if (order.getExtraCost2() != null) {
				orderFinal.setExtraCost2(order.getExtraCost2().getSapOrderExtraCostCd());
			}
			if (order.getExtraCost3() != null) {
				orderFinal.setExtraCost3(order.getExtraCost3().getSapOrderExtraCostCd());
			}
			if (order.getExtraCost4() != null) {
				orderFinal.setExtraCost4(order.getExtraCost4().getSapOrderExtraCostCd());
			}
			if (order.getExtraCostWaitingTimeMin() != null) {
				orderFinal.setExtraCostWaitingTimeMin(order.getExtraCostWaitingTimeMin());
			}
			
			//SAP BW KPI 14
			orderFinal.setODelivTypePlanActual(order.getDelivTypePlanActual());
			
			addVehicleInformation(orderFinal, order);
			
			//update frozen order item table
			for (OrderItemEntity oi : (List<OrderItemEntity>) order.getOrderItems()) {
				OrderItemFinalEntity oiFinal = null;

				//check if the record already exists (frozen)and th
				for (OrderItemFinalEntity oif :
						(List<OrderItemFinalEntity>) orderFinal.getOrderFinalItems()) {
					if (ObjectUtils.equals(oif.getOiOrderItemId(), oi.getOrderItemId())) {
						oiFinal = oif;
						break;
					}
				}
				
				if (oiFinal == null){
					oiFinal = new OrderItemFinalEntity();
				}
				
				oiFinal.setOrderFinal(orderFinal);
				oiFinal.setOiOrderItemId(oi.getOrderItemId());
				oiFinal.setOiOrderId(order.getOrderId());
				oiFinal.setOiOrderNumber(order.getOrderNumber());
				oiFinal.setOiOrderItemNo(oi.getOrderItemNo());
				oiFinal.setOiMixDesignId(oi.getMixDesign() != null ? oi.getMixDesign().getMixDesignId() : null);
				
				//SAP BW KPI 7
				oiFinal.setOiDispocardAvailalbe(oi.getDispocardAvailalbe());
				//SAP BW KPI 15
				oiFinal.setOiOrderPlanActual(oi.getOrderPlanActual());
				
				LoadEntity load = oi.getLoad();
				
				//SAP BW KPI 6
				setLoadedWithLdsFlag(oiFinal, load);
				
				if (load != null) {
					oiFinal.setOiLoadId(load.getLoadId());
					
					//Content of tlgnc_load.
					if (load.getLoadedFromLoad() != null) {
						oiFinal.setLoadLoadedFromLoadId(load.getLoadedFromLoad().getLoadId());
					}
					oiFinal.setLoadSapMaterialId(load.getSapMaterial().getSapMaterialId());
					oiFinal.setLoadProductionPlantCd(load.getProductionPlant().getPlantCd());
					oiFinal.setLoadTareQuantity(load.getTareWeightT());
					oiFinal.setLoadNetQuantity(load.getNetQuantity());
					oiFinal.setNetQuantityUomCd(load.getNetQuantityUom().getUomCd());
					oiFinal.setLoadGrossQuantity(load.getGrossWeightT());
					oiFinal.setLoadLoadOrderId(load.getLoadOrderId());
					oiFinal.setLoadChamberNumber(load.getChamberNumber());
					oiFinal.setLoadLoadingTs(load.getLoadingTs());
					oiFinal.setLoadUnloadingTs(load.getUnloadingTs());
					oiFinal.setLoadWsSiloNo(load.getWsSiloNo());
					oiFinal.setLoadWsSiloName(load.getWsSiloName());
					oiFinal.setLoadWsSampleNo(load.getWsSampleNo());
					oiFinal.setLoadWsCustomerOrderNo(load.getWsCustomerOrderNo());
					oiFinal.setLoadWsOrderTemplateNo(load.getWsOrderTemplateNo());
					oiFinal.setLoadWsEichbeleg1(load.getWsEichbeleg1());
					oiFinal.setLoadWsEichbeleg1Time(load.getWsEichbeleg1Time());
					oiFinal.setLoadWsEichbeleg1Balance(load.getWsEichbeleg1Balance());
					oiFinal.setLoadWsEichbeleg2(load.getWsEichbeleg2());
					oiFinal.setLoadWsEichbeleg2Time(load.getWsEichbeleg2Time());
					oiFinal.setLoadWsEichbeleg2Balance(load.getWsEichbeleg2Balance());
					oiFinal.setLoadWsEnterShipppointTime(load.getWsEnterShippingpointTime());
					oiFinal.setLoadWsLeaveShipppointTime(load.getWsLeaveShippingpointTime());
					oiFinal.setLoadWsSampleNo(load.getWsSampleNo());
					oiFinal.setLoadWsTareQuitWarnFlag(load.getWsTareQuitWarnFlag());
					if (load.getWsStartLoadingpoint() != null) { 
						oiFinal.setLoadWsStartLoadingpointId(load.getWsStartLoadingpoint().getLoadingpointId());
					}
					if (load.getWsEndLoadingpoint() != null) { 
						oiFinal.setLoadWsEndLoadingpointId(load.getWsEndLoadingpoint().getLoadingpointId());
					}
					oiFinal.setLoadWsOrigGrossMeasureT(load.getWsOriginalGrossMeasureT());
					oiFinal.setLoadWsOrigTareMeasureT(load.getWsOriginalTareMeasureT());
					oiFinal.setLoadWsTruckPlateNo(load.getWsTruckPlateNo());
					oiFinal.setLoadWsTrailerPlateNo(load.getWsTrailerPlateNo());
					oiFinal.setLoadWsCarrier(load.getWsCarrier());
					
					//Content of tlgnc_loadingpoint.
					LoadingpointEntity detachedLp = load.getLoadingpoint();
					LoadingpointEntity managedLp = null;
					// Loads of bimodal road order item don't have any loadingpoint defined.
					if (detachedLp != null) {
						Long loadingpointId = detachedLp.getLoadingpointId();
						managedLp = em.find(LoadingpointEntity.class, loadingpointId);
					}
					if (managedLp != null) {
						oiFinal.setLoadLoadingpointId(managedLp.getLoadingpointId());
						oiFinal.setLpLpointName(managedLp.getLoadingpointName());
						oiFinal.setLpLpointObjectLabel(managedLp.getLoadingpointObjectLabel());
						oiFinal.setLpPackageTypeCd(managedLp.getPackageType().getPackageTypeCd());
						oiFinal.setLpWeighSystemLpCd(managedLp.getWeighSystemLp().getWeighSystemLpCd());
						oiFinal.setLpDelivNotePrintTypeCd(managedLp.getDelivNotePrintType().getDelivNotePrintTypeCd());
						oiFinal.setLpWeighSysLoadingpointNo(managedLp.getWeighSystemLoadingpointNo());
						if (managedLp.getPrinter() != null) {
							oiFinal.setLpPrinterCd(managedLp.getPrinter().getPrinterCd());
						}
						oiFinal.setLpLoadingpointComment(managedLp.getLoadingpointComment());
						oiFinal.setLpActiveFlag(managedLp.getActiveFlag());
						oiFinal.setLpLoadingTime(managedLp.getLoadingTime());
						oiFinal.setLpGrossWeightLimitKg(managedLp.getGrossWeightLimitKg());
						if (managedLp.getRfidobject() != null) {
							oiFinal.setLpRfidObjectLabel(managedLp.getRfidobject().getObjectLabel());
						}
					}
					
					// Setting goodsmovement values
					ReadOnlyDao<GoodsMovementEntity> gmDao =
							getDaoProvider().getGoodsMovementEntityDao();
					Filter<GoodsMovementEntity> gmFilter = gmDao.createFilter();
					gmFilter.addExpression(Restrictions.eq(GoodsMovementEntity.ATTR_LOAD_ID,
							load.getLoadId()));
					
					List<GoodsMovementEntity> goodsMovementEntities = gmDao.find(gmFilter);
					for(GoodsMovementEntity goodsMovementEntity : goodsMovementEntities) {
						oiFinal.setLoadStorageLocation(goodsMovementEntity.getFromStorageLoc());
						oiFinal.setLoadStorageLocInTransit(goodsMovementEntity.getToStorageLoc());
						oiFinal.setLoadSentGoodsmoveToQueTs(goodsMovementEntity.getSentGoodsmoveToQueueTs());
						oiFinal.setLoadSentGoodsmoveToSapTs(goodsMovementEntity.getSentGoodsmoveToSapTs());
					}

					
				} 

				
				//Content of tlgns_sap_material.
				SapMaterialEntity mat = oi.getSapMaterial();
				ReadWriteDao<MatPlantEntity> matPlantDao = getDaoProvider().getMatPlantEntityDao();
				Filter<MatPlantEntity> matPlantFilter = matPlantDao.createFilter();
				matPlantFilter.addExpression(Restrictions.eq(MatPlantEntity.ATTR_SAP_MATERIAL_ID,
						mat.getSapMaterialId()));
				matPlantFilter.addExpression(Restrictions.eq(MatPlantEntity.ATTR_PLANT_CD,
						oi.getProductionPlant().getPlantCd()));
				MatPlantEntity matPlantEntitiy = matPlantDao.findSingle(matPlantFilter);
				
				
				oiFinal.setOiSapMaterialId(mat.getSapMaterialId());
				oiFinal.setMatSapMaterialNo(mat.getSapMaterialNo());
				oiFinal.setMatMaterialGroupDesc(mat.getMaterialGroupDesc());
				oiFinal.setMatBaseUomCd(mat.getBaseUom().getUomCd());
				if (matPlantEntitiy != null){
					oiFinal.setMatNormDesc(matPlantEntitiy.getNormDesc());
				}
				DivisionBsEntity division = mat.getDivision();
				if (division != null) {
					oiFinal.setMatDivisionCd(division.getDivisionCd());
				}
				oiFinal.setMatIndStandardDesc(mat.getIndustryStandardDesc());
				oiFinal.setMatMaterialPackageCd(mat.getMaterialPackage().getMaterialPackageCd());
				oiFinal.setMatMarkedForDeletion(mat.getMarkedForDeletion());
				
				//Content of tlgns_sap_material_sare.
				ReadWriteDao<SapMaterialSareaEntity> matSareaDao =
						getDaoProvider().getSapMaterialSareaEntityDao();
				Filter<SapMaterialSareaEntity> matSareaFilter = matSareaDao.createFilter();
				matSareaFilter = matSareaDao.createFilter();
				matSareaFilter.addExpression(Restrictions.eq(
						SapMaterialSareaEntity.ATTR_SAP_MATERIAL, oi.getSapMaterial()));
				SapMaterialUtil.addSareaRestriction(matSareaFilter, order.getSarea());
				SapMaterialSareaEntity matSareaEntity = matSareaDao.findSingle(matSareaFilter);
				
				if (matSareaEntity != null && matSareaEntity.getSalesUom() != null) {
					oiFinal.setMatSaSalesUomCd(matSareaEntity.getSalesUom().getUomCd());
				}
				
				
				oiFinal.setOiProductionPlantCd(oi.getProductionPlant().getPlantCd());
				oiFinal.setOiCurrencyCd(oi.getCurrency().getCurrencyCd());
				oiFinal.setOiRate(oi.getRate());
				if (oi.getOrderedUom() != null) {
					oiFinal.setOiOrderedUomCd(oi.getOrderedUom().getUomCd());
				}
				if (oi.getLoadedUom() != null) {
					oiFinal.setOiLoadedUomCd(oi.getLoadedUom().getUomCd());
				}
				if (oi.getReturnedUom() != null) {
					oiFinal.setOiReturnedUomCd(oi.getReturnedUom().getUomCd());
				}
				if (oi.getReturnQuantHandling() != null) {
					oiFinal.setOiReturnQuantHandlingCd(oi.getReturnQuantHandling().getReturnQuantHandlingCd());
				}
				if (oi.getDeliveredUom() != null) {
					oiFinal.setOiDeliveredUomCd(oi.getDeliveredUom().getUomCd());
				}
				oiFinal.setOiOrderedUomNumerator(oi.getOrderedUomNumerator());
				oiFinal.setOiOrderedUomDenominator(oi.getOrderedUomDenominator());
				oiFinal.setOiLrUomNumerator(oi.getLrUomNumerator());
				oiFinal.setOiLrUomDenominator(oi.getLrUomDenominator());
				oiFinal.setOiOrderedQuantity(oi.getOrderedQuantity());
				oiFinal.setOiLoadedQuantity(oi.getLoadedQuantity());
				oiFinal.setOiReturnedQuantity(oi.getReturnedQuantity());
				oiFinal.setOiDeliveredQuantity(oi.getDeliveredQuantity());
				
				if (sp != null) {
		        	MatShippingpointEntity matShip = orderService.fetchMatShippingpoint(
		        			oi.getSapMaterial().getSapMaterialId(), sp.getShippingpointId());
		        	if ( matShip != null && !StringUtils.empty(matShip.getStorageLocation())) {
		        		oiFinal.setOiStorageLocation(matShip.getStorageLocation());
		        	}
		        	else {
		        		oiFinal.setOiStorageLocation(sp.getStorageLocation());
		        	}
				}
									
				//Content of tlgnc_shipto_silo.
				ShiptoSiloEntity shiptoSilo = oi.getShiptoSilo();
				
				if (shiptoSilo != null) {
					oiFinal.setOiShiptoSiloId(shiptoSilo.getShiptoSiloId());
					oiFinal.setSiloSiloName(shiptoSilo.getSiloName());
					oiFinal.setSiloSiloTypeCd(shiptoSilo.getSiloType().getSiloTypeCd());
					if (shiptoSilo.getSapMaterial() != null){
						oiFinal.setSiloSapMaterialNo(shiptoSilo.getSapMaterial().getSapMaterialNo());
					}
					oiFinal.setSiloMaxVolumeTons(shiptoSilo.getMaxVolumeTons());
					oiFinal.setSiloMaxVolumeM3(shiptoSilo.getMaxVolumeM3());
					oiFinal.setSiloSiloComment(shiptoSilo.getSiloComment());
					oiFinal.setSiloActiveFlag(shiptoSilo.getActiveFlag());
					oiFinal.setSiloUnloadingTime(shiptoSilo.getUnloadingTime());
					oiFinal.setSiloPortableSiloFlag(shiptoSilo.getPortableSiloFlag());
					if (shiptoSilo.getRfidobject() != null) {
						oiFinal.setSiloRfidObjectLabel(shiptoSilo.getRfidobject().getObjectLabel());
					}
				}
				
				// KPI 8
				if (oi.getShiptoSiloVerifType() != null) {						
					oiFinal.setOiShiptoSiloVerifTypeCd(oi.getShiptoSiloVerifType().getSiloVerifTypeCd());
				} else {
					// This defaulting logic was added as part of the SAP BW reporting logic
					oiFinal.setOiShiptoSiloVerifTypeCd(Long.valueOf(1)); // based on specs
				}
					
				oiFinal.setOiItemNoteOrderMgmt("");
				oiFinal.setOiItemNoteDelivery("");
				oiFinal.setOiItemNoteDispatching("");
				oiFinal.setOiItemNoteTruckDriver("");
				oiFinal.setOiItemNoteRailway("");
				if (oi.getBimodalRailOrderItem() != null) {
					oiFinal.setOiBimodalRailOrderItemId(oi.getBimodalRailOrderItem().getOrderItemId());
				}
				oiFinal.setOiGrossPriceMat(oi.getGrossPriceMat());
				oiFinal.setOiProdCostMat(oi.getProdCostMat());
				oiFinal.setOiHandlingCostMat(oi.getHandlingCostMat());
				oiFinal.setOiIudCost(oi.getIudCost());
				oiFinal.setOiOutCost(oi.getOutCost());
				oiFinal.setOiDeliveryDatExchangeRate(oi.getDeliveryDatExchangeRate());
								
				//Content of vlgnc_order_item_margin.
				ReadOnlyDao<OrderItemMarginEntity> marginDao = getDaoProvider().getOrderItemMarginEntityDao();
				Filter<OrderItemMarginEntity> marginFilter = marginDao.createFilter();
				marginFilter.addExpression(Restrictions.eq(OrderItemMarginEntity.ATTR_ORDER_ITEM_ID,
						oi.getOrderItemId()));
				OrderItemMarginEntity margin = marginDao.findSingle(marginFilter);
				
				if (margin != null) {
					oiFinal.setOiMarginQuantity(margin.getQuantity());
					oiFinal.setOiMarginCurrencyCd(margin.getCurrencyCd());
					oiFinal.setOiMarginDeliveredUomCd(margin.getDeliveredUomCd());
					oiFinal.setOiMarginGrossPrice(margin.getGrossPrice());
					oiFinal.setOiMarginGrossPricePt(margin.getGrossPricePt());
					oiFinal.setOiMarginDiscounts(margin.getDiscounts());
					oiFinal.setOiMarginDiscountsPt(margin.getDiscountsPt());
					oiFinal.setOiMarginNetPrice(margin.getNetPrice());
					oiFinal.setOiMarginNetPricePt(margin.getNetPricePt());
					oiFinal.setOiMarginRebates(margin.getRebates());
					oiFinal.setOiMarginRebatesPt(margin.getRebatesPt());
					oiFinal.setOiMarginCashDiscount(margin.getCashDiscount());
					oiFinal.setOiMarginCashDiscountPt(margin.getCashDiscountPt());
					oiFinal.setOiMarginNetSalesEarning(margin.getNetSalesEarning());
					oiFinal.setOiMarginNetSalesEarningPt(margin.getNetSalesEarningPt());
					oiFinal.setOiMarginProdCost(margin.getProdCost());
					oiFinal.setOiMarginProdCostPt(margin.getProdCostPt());
					oiFinal.setOiMarginIudCost(margin.getIudCost());
					oiFinal.setOiMarginIudCostPt(margin.getIudCostPt());
					oiFinal.setOiMarginHandlingCost(margin.getHandlingCost());
					oiFinal.setOiMarginHandlingCostPt(margin.getHandlingCostPt());
					oiFinal.setOiMarginOutCost(margin.getOutCost());
					oiFinal.setOiMarginOutCostPt(margin.getOutCostPt());
					oiFinal.setOiMarginTotalDistCost(margin.getTotalDistCost());
					oiFinal.setOiMarginTotalDistCostPt(margin.getTotalDistCostPt());
					oiFinal.setOiMarginTotalCost(margin.getTotalCost());
					oiFinal.setOiMarginTotalCostPt(margin.getTotalCostPt());
					oiFinal.setOiMarginContribMargin(margin.getContribMargin());
					oiFinal.setOiMarginContribMarginPt(margin.getContribMarginPt());
					
					if (order.getOrderStatus().getOrderStatusCd().longValue() == Constant.ORDER_STATUS_DELETED) {
						orderFinal.setReportFinishedCommFlag(Boolean.FALSE);
					}
					else if ( (oi.getDeliveredUom() == null) ||
							(oi.getDeliveredQuantity() == null) || // delivered_quantity
							(margin.getGrossPrice() == null) || // gross_price_mat
							(margin.getProdCost() == null) || // prod_cost_mat
							(margin.getHandlingCost() == null) || // handling_cost_mat
							(margin.getIudCost() == null) ||
							(margin.getOutCost() == null) ||
							(oi.getDeliveryDatExchangeRate() == null) ) {
						orderFinal.setReportFinishedCommFlag(Boolean.FALSE);
					}
					else {
						ReadOnlyDao<SapNetpOrderItemSumEntity> sapNetpSumDao =
								getDaoProvider().getSapNetpOrderItemSumEntityDao();
						Filter<SapNetpOrderItemSumEntity> sapNetpSumFilter =
								sapNetpSumDao.createFilter();
						sapNetpSumFilter.addExpression(Restrictions.eq(
								SapNetpOrderItemSumEntity.ATTR_ORDER_ITEM_ID, oi.getOrderItemId()));
						SapNetpOrderItemSumEntity sapNetPSum =
								sapNetpSumDao.findSingle(sapNetpSumFilter);
						
						if ( (sapNetPSum == null) || (sapNetPSum.getNetpriceQuantity() == null) || (sapNetPSum.getNetpriceQuantity().compareTo(oi.getDeliveredQuantity()) != 0) ||
								(sapNetPSum.getNetpriceNetValue() == null) ||
								(sapNetPSum.getNetpriceRebateTotal() == null) ||
								(sapNetPSum.getNetpriceTax() == null) ||
								(sapNetPSum.getNetpriceCashDiscount() == null) ) {
							orderFinal.setReportFinishedCommFlag(Boolean.FALSE);
						}
					}
				}
				else {
					orderFinal.setReportFinishedCommFlag(Boolean.FALSE);
				}
				
				//Content of Reporting tab (Order detail screen)
				BigDecimal deliveredQuantityT = null;
				String oiDeliveredUomCd = oiFinal.getOiDeliveredUomCd();
				
				if ( (oi.getDeliveredQuantity() != null) && (oiDeliveredUomCd != null) ){
					
					if (Constant.UOM_TONS.equals(oiDeliveredUomCd)) {
						deliveredQuantityT = oi.getDeliveredQuantity();
					}
					else {
						UomBsEntity deliveredUom = referentialDataService.findBsEntity(UomBsEntity.class, 
								oiDeliveredUomCd);
						UomBsEntity uomTons = referentialDataService.findBsEntity(UomBsEntity.class, Constant.UOM_TONS);
						BigDecimal deliveredQuantity = oi.getDeliveredQuantity();						
						
						try {
							UomConversionResult ucr = unitOfMeasurementConverterService.convert(deliveredQuantity,
									deliveredUom, uomTons, mat, null, null, false);
							deliveredQuantityT = ucr.getQuantity();
						}
						catch (LogonConversionException e) {
							LOG.warn("Cannot convert delivered quantity (in UoM "
									+ oiDeliveredUomCd + ") to tons while finalizing order "
									+ order.getOrderNumber() + " (sapMaterialId = "
										+ mat.getSapMaterialId() + "): " + e.getMessage());
							
							orderFinal.setReportFinishedCommFlag(Boolean.FALSE);
						}
					}
				}
				
				final BigDecimal distanceKm;
				
				if ((deliveredQuantityT != null) && (orderFinal.getOTranspDistancePlanM() != null)
						&& (orderFinal.getOTranspDistancePlanM().longValue() > 1)) {
					
					final BigDecimal M_PER_KM = BigDecimal.valueOf(1000);
					final BigDecimal distanceM = BigDecimal.valueOf(orderFinal.getOTranspDistancePlanM());
					distanceKm = distanceM.divide(M_PER_KM, Constant.SCALE, Constant.LOGON_ROUNDING_MODE);
					final BigDecimal capacityTonKm = deliveredQuantityT.multiply(distanceKm).setScale(Constant.SCALE, Constant.LOGON_ROUNDING_MODE);
					
					oiFinal.setOiRefundTrspCapaTkm(capacityTonKm);
					
				}
				else {
					distanceKm = null;
				}
				
				if (oiFinal.getOiMarginOutCost() != null) {
					
					if ( (deliveredQuantityT != null) && (deliveredQuantityT.compareTo(BigDecimal.ZERO) != 0) ) {
						BigDecimal netWeight = oiFinal.getOiMarginOutCost().divide(deliveredQuantityT , Constant.SCALE, BigDecimal.ROUND_HALF_UP);
						oiFinal.setOiRefundNetWeightCurT(netWeight);
					}
					
					if ( (distanceKm != null) && (distanceKm.compareTo(BigDecimal.ZERO) != 0) ) {
						BigDecimal distance = oiFinal.getOiMarginOutCost().divide(distanceKm, Constant.SCALE, BigDecimal.ROUND_HALF_UP);
						oiFinal.setOiRefundDistanceCurKm(distance);
					}
					
					if ( (oiFinal.getOiRefundTrspCapaTkm() != null) && (oiFinal.getOiRefundTrspCapaTkm().compareTo(BigDecimal.ZERO) != 0) ) {
						BigDecimal capacity = oiFinal.getOiMarginOutCost().divide(oiFinal.getOiRefundTrspCapaTkm(), Constant.SCALE, BigDecimal.ROUND_HALF_UP);
						oiFinal.setOiRefundTrspCapaCurTkm(capacity);
					}
				}
				
		
		
				
				//Content of tlgnc_truck.
				TruckEntity truck = oi.getDeliveredLoadTruck();
				if (truck != null) {
					oiFinal.setOiDeliveredLoadTruckId(truck.getTruckId());
					oiFinal.setTruckName(truck.getTruckName());
					oiFinal.setTruckPlateNo(truck.getTruckPlateNo());
					oiFinal.setTruckTransportCatCd(truck.getTruckTransportCat().getTruckTransportCatCd());
					oiFinal.setTruckTypeCd(truck.getTruckType().getTruckTypeCd());
					oiFinal.setTruckMaxGrossWeightT(truck.getMaxGrossWeightT());
					oiFinal.setTruckTareWeightT(truck.getTareWeightT());
					oiFinal.setTruckNrChambers(truck.getNrChambers());
					oiFinal.setTruckMaxNetWChamber1T(truck.getMaxNetWeightChamber1T());
					oiFinal.setTruckMaxNetWChamber2T(truck.getMaxNetWeightChamber2T());
					oiFinal.setTruckSapInternalFlag(truck.getSapVehicleIsInternalFlag());
					// Not reliable! Therefore removed: oiFinal.setTruckPdaAvailableFlag(truck.getPdaAvailableFlag()); 
				}
			    
				//Content of tlgnc_trailer.
				TrailerEntity trailer = oi.getDeliveredLoadTrailer();
				if (trailer != null) {
					oiFinal.setOiDeliveredLoadTrailerId(trailer.getTrailerId());
					oiFinal.setTrailerName(trailer.getTrailerName());
					oiFinal.setTrailerPlateNo(trailer.getTrailerPlateNo());
					oiFinal.setTrailerTrspCatCd(trailer.getTrailerTrspCat().getTrailerTrspCatCd());
					oiFinal.setTrailerTypeCd(trailer.getTrailerType().getTrailerTypeCd());
					oiFinal.setTrailerMaxGrossWeightT(trailer.getMaxGrossWeightT());
					oiFinal.setTrailerTareWeightT(trailer.getTareWeightT());
					oiFinal.setTrailerNrChambers(trailer.getNrChambers());
					oiFinal.setTrailerMaxNetWChamber1T(trailer.getMaxNetWeightChamber1T());
					oiFinal.setTrailerMaxNetWChamber2T(trailer.getMaxNetWeightChamber2T());
					oiFinal.setTrailerSapInternalFlag(trailer.getSapVehicleIsInternalFlag());
				}
		
				//Content of tlgnc_wagon.
				WagonEntity wagon = oi.getDeliveredLoadWagon();
				if (wagon != null) {
					
					oiFinal.setOiDeliveredLoadWagonId(wagon.getWagonId());
					oiFinal.setWagonNo(wagon.getWagonNo());
				    oiFinal.setWagonTypeCd(wagon.getWagonType().getWagonTypeCd());
				    WagonTypeBsEntity wt = wagon.getWagonType();
					oiFinal.setWagonTypeName(wt.getWagonTypeName());
				    oiFinal.setWagonUicClassCd(wt.getWagonUicClassCd());
				    oiFinal.setWagonAxisCount(wt.getAxisCount());
				    oiFinal.setWagonLimitCreditNoteT(wt.getLimitCreditNoteT());
				    
				    if (wt.getWagonTrspCat() != null) {
				    	oiFinal.setWagonTrspCatCd(wt.getWagonTrspCat().getWagonTrspCatCd()); 
				    }
				    oiFinal.setWagonTareWeightT(wagon.getTareWeightT());
				  
				    if (wagon.getMaxGrossWeightT() == null) {
				     	oiFinal.setWagonMaxGrossWeightT(wt.getMaxGrossWeightT());
				    }
				    else {
				       	oiFinal.setWagonMaxGrossWeightT(wagon.getMaxGrossWeightT());
					}
				    if (wagon.getVolumeM3()== null) {
				     	oiFinal.setWagonVolumeM3(wt.getVolumeM3());
				    }
				    else {
				       	oiFinal.setWagonVolumeM3(wagon.getVolumeM3());
					}
				   
				}
				
				//Content of tlgnc_container.
				ContainerEntity container = oi.getDeliveredLoadContainer();
				if (container != null) {
					oiFinal.setOiDeliveredLoadContainerId(container.getContainerId());
					oiFinal.setContainerNo(container.getContainerNo());
					oiFinal.setContainerName(container.getContainerName());
					oiFinal.setContainerTrspCatCd(container.getContainerTrspCat().getContainerTrspCatCd());
					oiFinal.setContainerTypeCd(container.getContainerType().getContainerTypeCd());
					oiFinal.setContainerTareWeightT(container.getTareWeightT());
					oiFinal.setContainerMaxNetWeightT(container.getMaxNetWeightT()); 
				}
				
				if (truck != null || wagon != null) {
					oiFinal.setCompositionTareWeightT(LogonUtil.sumTareWeights(trailer, container, truck, wagon, null));
				}
				
				oiFinal.setOiRestrDelivTypeDiffFlag(Boolean.FALSE);
				oiFinal.setOiRestrOrdQUomDiffFlag(Boolean.FALSE);
				//Content of tlgnc_shipto_sarea_restr.
				/*if (shiptoSareaId != null) {
				*/
					/**
					 * As far since LOGON 2.0 multiple restrictions can exist for the same material coming from different sap contracts
					 * and sap contract items, looking for a single restriction is theoretically bad. In the meantime for several use cases
					 * like bimodal and other we now require to have only one, but that might change also in the future.
					 * The recommendation of Uma in ticket 75536 is that we should check delivery types etc. and shippingpoint, but multiple attributes
					 * are used from this restriction not only the shippingpoint, therefore rather deactivate now not to cause freezing failure.
					 */
					/*ReadWriteDao<ShiptoSareaRestrEntity> restrDao = getDaoProvider().getShiptoSareaRestrEntityDao();
					filter = new FilterImpl();
					filter.addExpression(Restrictions.eq(ShiptoSareaRestrEntity.ATTR_SHIPTO_SAREA_ID, shiptoSareaId));
					filter.addExpression(Restrictions.eq(ShiptoSareaRestrEntity.ATTR_SAP_MATERIAL, oi.getSapMaterial()));
					ShiptoSareaRestrEntity restr = restrDao.findSingle(filter);
					
					if (restr != null) {
						if (restr.getDeliveryType() != null) {
							oiFinal.setOiRestrDeliveryTypeCd(restr.getDeliveryType().getDeliveryTypeCd());
							if (order.getDeliveryType() != null){									
								if (!restr.getDeliveryType().getDeliveryTypeCd().equals(order.getDeliveryType().getDeliveryTypeCd())){
									oiFinal.setOiRestrDelivTypeDiffFlag(Boolean.TRUE);									
								}
							}
						}
						oiFinal.setOiRestrOrderedQuantity(restr.getQuantity());
						
						if (restr.getUom() != null) {
							oiFinal.setOiRestrOrderedUomCd(restr.getUom().getUomCd());
						}
						if (oi.getDeliveredUom() != null && restr.getUom() != null ){
							if (!oi.getDeliveredUom().getUomCd().equals(restr.getUom().getUomCd())) {
								oiFinal.setOiRestrOrdQUomDiffFlag(Boolean.TRUE);
							}
						}
						if (oi.getDeliveredQuantity() != null && restr.getQuantity() != null ){
							if (!oi.getDeliveredQuantity().equals(restr.getQuantity())) {
								oiFinal.setOiRestrOrdQUomDiffFlag(Boolean.TRUE);
							}
						}							
					}
				}*/

				
				final UserEntity orderItemCreatorUser = getDaoProvider().getUserEntityDao().findById(oi.getCtlCreUid());
				final String oiCreatorName = LogonUtil.getFullName(orderItemCreatorUser, (int)OrderItemFinalEntity.DBSIZE_OI_CTL_CRE_UID_NAME);
				oiFinal.setOiCtlCreUidName(oiCreatorName);
				oiFinal.setOiCtlCreUid(oi.getCtlCreUid());
				oiFinal.setOiCtlCreTs(oi.getCtlCreTs());

				final UserEntity orderItemModifierUser = getDaoProvider().getUserEntityDao().findById(oi.getCtlModUid());
				final String oiModifierName = LogonUtil.getFullName(orderItemModifierUser, (int)OrderItemFinalEntity.DBSIZE_OI_CTL_MOD_UID_NAME);
				oiFinal.setOiCtlModUidName(oiModifierName);
				oiFinal.setOiCtlModUid(oi.getCtlModUid());
				oiFinal.setOiCtlModTs(oi.getCtlModTs());
				
				// add only if it is a new OrderItemFinal
				if (oiFinal.getOrderItemFinalId() == null) {
					orderFinal.getOrderFinalItems().add(oiFinal);
				}
			}
			
			// persist frozen order
			if (orderFinal.getOrderFinalId() == null) {
				//insert a new one
				ReadWriteDao<OrderFinalEntity> orderFDao =
						getDaoProvider().getOrderFinalEntityDao();
				orderFinal = orderFDao.persist(orderFinal);
				if (logDebugEnabled) {
					LOG.debug("A new OrderFinalEntity was saved: " + orderFinal);
				}		
			}
			
			return orderFinal;
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
	 * Fill out vehicle related fields
	 * @param orderFinal
	 * @param order : original order
	 */
	@AdnTrace
	private void addVehicleInformation(OrderFinalEntity orderFinal, OrderEntity order) {
		Long deliveryTypeCd = order.getDeliveryType().getDeliveryTypeCd();
		boolean isSimpleVehicle = LogonUtil.isSimpleVehicleOrder(deliveryTypeCd) ||
				LogonUtil.isSimpleVehiclePickupOrder(deliveryTypeCd);
		
		if (isSimpleVehicle) {
			addSimpleVehicleInformation(orderFinal, order);
		}
	}


	/**
	 * Fill out simple vehicle related fields 
	 * @param orderFinal
	 * @param order : original order
	 */
	@AdnTrace
	private void addSimpleVehicleInformation(OrderFinalEntity orderFinal, OrderEntity order) {
		SimpleVehicleEntity deliveredSimpleVehicle = order.getDeliveredSimpleVehicle();
		
		if (deliveredSimpleVehicle != null) {
			orderFinal.setODeliveredSimpleVehicleId(deliveredSimpleVehicle.getSimpleVehicleId());
			orderFinal.setVehicleName(deliveredSimpleVehicle.getVehicleName());
			orderFinal.setVehicleRegNo(deliveredSimpleVehicle.getVehicleRegNo());
			orderFinal.setVehicleTareWeightT(deliveredSimpleVehicle.getTareWeightT());
			orderFinal.setVehicleMaxGrossWeightT(deliveredSimpleVehicle.getMaxGrossWeightT());
			orderFinal.setVehicleNrChambers(deliveredSimpleVehicle.getNrChambers());
			
			Long vehicleTypeCd = deliveredSimpleVehicle.getSimpleVehicleType().getSimpleVehicleTypeCd();
			orderFinal.setVehicleTypeCd(vehicleTypeCd);
		}
	}


	/** Find latest load (check LOADING_TS)
	 * @param order
	 * @return LoadEntity
	 */
	private LoadEntity getLatestLoad(OrderEntity order) {
		// [[
		LoadEntity result = null;
		Object[][] params = {{"order", order}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getLatestLoad", params);
		}
		// ]]
		try {
			
			LoadEntity loadTypeLoad = null;
			final Collection<LoadEntity> loadList = new ArrayList<LoadEntity>();

			for (OrderItemEntity oie : LogonPackageHandlingUtil.getMaterialItems(order)) {
				if (oie.getLoad() != null) {
					loadList.add(oie.getLoad());
					
					//set the first item to compare
					if (loadTypeLoad == null) {
						loadTypeLoad = oie.getLoad();
					}
				}
			}
			
			for (LoadEntity load : loadList) {
				if (load.getLoadingTs() != null) {
					//in case first selected load has LoadingTs null
					if (loadTypeLoad.getLoadingTs() == null) {
						loadTypeLoad = load;
					}
					else if (load.getLoadingTs().compareTo(loadTypeLoad.getLoadingTs()) > 0) {
						loadTypeLoad = load;
					}
				}
			}
			return result = loadTypeLoad;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}


	private void calculateAndSetDeliveryNetTime(OrderEntity order, OrderFinalEntity orderFinal,
			ShiptoEntity shiptoEntity) {
		// [[
		Object[][] params = {{"order", order},
							{"orderFinal", orderFinal},
							{"shiptoEntity", shiptoEntity}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "calculateAndSetDeliveryNetTime", params);
		}
		// ]]
		try {

			TimeZone shiptoTz = timeZoneService.loadForShipTo(shiptoEntity, Locale.ENGLISH);
			
			Long allowWebOrderOnWeekend = null;
			SareaEntity sareaEntity = order.getSarea();
			if(sareaEntity != null) {
				allowWebOrderOnWeekend = sareaEntity.getAllowWebOrderOnWeekend(); 
			}
			SalesAreaWebOrderOnWeekendSettings webOrderOnWeekend = SalesAreaWebOrderOnWeekendSettings.valueOf(allowWebOrderOnWeekend);

			SchedulingUtils.CalculatedDeliveryTimes calculatedTimes =
					SchedulingUtils.calculateDeliveryFromToAndNetTime(
							shiptoEntity, order.getDeliveryFromDat(), order.getDeliveryToDat(), shiptoTz,webOrderOnWeekend);

			orderFinal.setODeliveryNetTimeWindow(calculatedTimes.getNetTimeInMinutes());
			
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
	 * Updates the fields starting with O_CALC related to load timing.
	 * @param order
	 * @param orderFinal
	 */
	private void calculateAndSetLoadTimingReportValues(final OrderEntity order, final OrderFinalEntity orderFinal) {
		// [[
		Object[][] params = {{"order", order},
							{"orderFinal", orderFinal}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "calculateAndSetLoadTimingReportValues", params);
		}
		// ]]
		try {
			final Date deliveryFrom = order.getDeliveryFromDat();
			final Date deliveryTo = order.getDeliveryToDat();
			
			LOG.debug("Delivery from: " + deliveryFrom);
			LOG.debug("Delivery to: " + deliveryTo);
			
			if ( orderFinal.getOTranspDurPlanMin() != null ) {
				LOG.debug("Transport duration plan: "+  orderFinal.getOTranspDurPlanMin());
			
				if (deliveryFrom != null){
					//Calc end load from depends on the delivery to and the planned transport duration
					Calendar cal = Calendar.getInstance();
					cal.setTime(deliveryFrom);
					cal.add(Calendar.MINUTE, -orderFinal.getOTranspDurPlanMin().intValue());
					Date endLoadFrom = cal.getTime();
					orderFinal.setOCalcEndLoadFromDat(endLoadFrom);
					if (logDebugEnabled) {						
						LOG.debug("End load from: " + endLoadFrom);
					}
				}
			
				if (deliveryTo != null){
					//Calc end load to depends on the delivery to and the planned transport duration
					Calendar cal = Calendar.getInstance();
					cal.setTime(deliveryTo);
					cal.add(Calendar.MINUTE, -orderFinal.getOTranspDurPlanMin().intValue());
					Date endLoadTo = cal.getTime();
					orderFinal.setOCalcEndLoadToDat(endLoadTo);
					if (logDebugEnabled) {						
						LOG.debug("End load to: " + endLoadTo);
					}
				}
			}
			
			
			{
				Long loadingTime = SchedulingUtils.calculateLoadingTimeForOrder(order, getConfigBean());
				orderFinal.setOPlanLoadingTimeMin(loadingTime);
			}
			
			Collection<OrderItemEntity> orderItems = LogonPackageHandlingUtil.getMaterialItems(order);
			final Long planLoadingTimeMin = orderFinal.getOPlanLoadingTimeMin();
			final Date calcEndLoadToDat = orderFinal.getOCalcEndLoadToDat();
			if ( planLoadingTimeMin != null ) {
				
				if ( orderFinal.getOCalcEndLoadFromDat() != null ) {
					//Calc start load from depends on the delivery to and the planned loading time of the loadingpoint / default
					Calendar cal = Calendar.getInstance();
					cal.setTime(orderFinal.getOCalcEndLoadFromDat());
					cal.add(Calendar.MINUTE, -planLoadingTimeMin.intValue());
					Date startLoadFromDat = cal.getTime();
					orderFinal.setOCalcStartLoadFromDat(startLoadFromDat);
					if (logDebugEnabled) {						
						LOG.debug("Start load from: " + startLoadFromDat);
					}
				}
			
				if ( calcEndLoadToDat != null ) {
					//Calc start load to depends on the delivery to and the planned loading time of the loadingpoint / default
					Calendar cal = Calendar.getInstance();
					cal.setTime(calcEndLoadToDat);
					cal.add(Calendar.MINUTE, -planLoadingTimeMin.intValue());
					Date startLoadToDat = cal.getTime();
					orderFinal.setOCalcStartLoadToDat(startLoadToDat);
					if (logDebugEnabled) {						
						LOG.debug("Start load to: " + startLoadToDat);
					}
				}
			}
			
			if (orderItems != null) {
			
				// Find LoadEntity: LOADING_TS -> latest
				LoadEntity loadTypeLoad = getLatestLoad(order);
				
				if (loadTypeLoad != null) {	
					
					//actual start load dat
					final ReadOnlyDao<AssignmentPartEntity> dao = getDaoProvider().getAssignmentPartEntityDao();
					final Filter<AssignmentPartEntity> filter = dao.createFilter();
					filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_LOAD, loadTypeLoad));		
					AssignmentPartEntity ap = dao.findSingle(filter);
	
					Date loadingPartStarted = (ap != null) ? ap.getRealStart() : null;
					orderFinal.setOActualStartLoadDat(loadingPartStarted);
					if (logDebugEnabled) {					
						LOG.debug("Actual start load: " + loadingPartStarted);
					}
				
	
					//actual end load dat
					orderFinal.setOActualEndLoadDat(loadTypeLoad.getLoadingTs());
					if (logDebugEnabled) {					
						LOG.debug("Actual end load: " + loadTypeLoad.getLoadingTs());
					}
				}
			}
			
			//flag if loading has started late compared to the latest time when it should be started to be in time to deliver it in time
			if(orderFinal.getOCalcStartLoadToDat() != null &&
					orderFinal.getOActualStartLoadDat() != null ) {
				
				if (orderFinal.getOActualStartLoadDat().after(orderFinal.getOCalcStartLoadToDat())) {
					orderFinal.setOCalcLateStartLoadDat(Boolean.TRUE);
				}
				else{
					orderFinal.setOCalcLateStartLoadDat(Boolean.FALSE);
				}
				
			}
			
			//flag if loading has ended late compared to the latest time when it should be started to be in time to deliver it in time
			if (calcEndLoadToDat != null  &&
					orderFinal.getOActualEndLoadDat() != null ) {
				
				if (orderFinal.getOActualEndLoadDat().after(calcEndLoadToDat)) {
					orderFinal.setOCalcLateEndLoadDat(Boolean.TRUE);
				}
				else{
					orderFinal.setOCalcLateEndLoadDat(Boolean.FALSE);
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
	 * Updates the orderFinal with the bill to related data of the order.
	 * @param order - from which the billto related data will be copied to the object orderFinal
	 * @param orderFinal - freezed order to which the billto related data is going to be copied
	 */
	private void freezeBillToData(OrderEntity order, OrderFinalEntity orderFinal) {
		// [[
		Object[][] params = {{"order", order},
							{"orderFinal", orderFinal}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "freezeBillToData", params);
		}
		// ]]
		try {
			
			// Content of tlgns_sap_bp (billto)
			SapBpEntity billto = order.getBilltoSapBp();
			if (billto != null) {
				orderFinal.setOBilltoSapBpId(billto.getSapBpId());
				orderFinal.setReSapId(billto.getSapId());
				orderFinal.setReMarkedForDeletion(billto.getMarkedForDeletion());
				orderFinal.setReName1(billto.getName1());
				orderFinal.setReName2(billto.getName2());
				orderFinal.setReCountry(billto.getCountry().getSapCountryCd());
				orderFinal.setRePostalCode(billto.getPostalCode());
				orderFinal.setReCity(billto.getCity());
				orderFinal.setReRegion(billto.getRegion());
				orderFinal.setReStreet(billto.getStreet());
				orderFinal.setReHouseNumber(billto.getHouseNumber());
				if (billto.getLang() != null) {
					orderFinal.setReLangCd(billto.getLang().getLangCd());
				}
				orderFinal.setReCustomerClassification(billto.getCustomerClassification());
				if (billto.getRailwayStation() != null) {
					orderFinal.setReRailwayStationId(billto.getRailwayStation().getRailwayStationId());
				}
				orderFinal.setReBpIsShipto(billto.getBpIsShipto());
				orderFinal.setReBpIsSoldto(billto.getBpIsSoldto());
				orderFinal.setReBpIsBillto(billto.getBpIsBillto());
				orderFinal.setReChromatReductionMonths(billto.getChromatReductionMonths());
				
				// Content of tlgns_sap_bp_sarea for the sarea_cd of the order (billto)
				final ReadOnlyDao<SapBpSareaEntity> dao = getDaoProvider().getSapBpSareaEntityDao();
				final Filter<SapBpSareaEntity> f = dao.createFilter();
				f.addExpression(Restrictions.eq(SapBpSareaEntity.ATTR_SAP_BP_ID, billto.getSapBpId()));
				f.addExpression(Restrictions.eq(SapBpSareaEntity.ATTR_SAREA_ID, order.getSarea().getSareaId()));
				SapBpSareaEntity billtoSarea = dao.findSingle(f);
				
				if (billtoSarea != null) {
					orderFinal.setReSapBpSareaId(billtoSarea.getSapBpSareaId());
					orderFinal.setReSareaMarkedForDeletion(billtoSarea.getMarkedForDeletion());
					orderFinal.setReSalesDistrict(billtoSarea.getSalesDistrict());
					if (billtoSarea.getSoffice() != null) {
						orderFinal.setReSofficeCd(billtoSarea.getSoffice().getSofficeCd());
					}
					if (billtoSarea.getSgroup() != null) {
						orderFinal.setReSgroupCd(billtoSarea.getSgroup().getSgroupCd());
					}
					orderFinal.setReCustomerGroup(billtoSarea.getCustomerGroup());
					orderFinal.setReAbcClass(billtoSarea.getAbcClass());
					if (billtoSarea.getCurrency() != null) {
						orderFinal.setReCurrencyCd(billtoSarea.getCurrency().getCurrencyCd());
					}
					if (billtoSarea.getDistributionDest() != null) {
						orderFinal.setReDistributionDestCd(billtoSarea.getDistributionDest().getDistributionDestCd());
					}
					orderFinal.setReCustomerOrderBlock(billtoSarea.getCustomerOrderBlock());
					orderFinal.setReCustomerDeliveryBlock(billtoSarea.getCustomerDeliveryBlock());
					orderFinal.setReCustomOffice(billtoSarea.getCustomOffice());
					if (billtoSarea.getDefaultPlant() != null) {
						orderFinal.setReDefaultPlantCd(billtoSarea.getDefaultPlant().getPlantCd());
					}
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
	
	/** Setting the loaded with lds flag on the order final entity based on the availability of ids in the load*/
	private void setLoadedWithLdsFlag(OrderItemFinalEntity item, LoadEntity load) {
		//SAP BW KPI 6
		if (load == null) { // when load entity is not available we can be sure that no weighing system was used for loading
			item.setLdsLoadedFlag(false);
		}
		else if (load.getLoadOrderItemId() != null || load.getLoadOrderId() != null) {
			item.setLdsLoadedFlag(true);
		}
		else {
			item.setLdsLoadedFlag(false);
		}
	}

	private long calcDiffMin(long toDate, long fromDate) {
		// [[
		long result = 0L;
		Object[][] params = {{"toDate", toDate + ""},
							{"fromDate", fromDate + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "calcDiffMin", params);
		}
		// ]]
		try {
			long diff = toDate - fromDate;
			long diffMin = diff / (60 * 1000);
			return result = diffMin;
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
	 * @param order deliveryFromDat. this is used then the biz_enter_st_ts is not set
	 * @param bizArrivedAtDelPointTs BIZ_ENTER_ST_TS
	 * @param loadedTs O_ACTUAL_END_LOAD_DAT last load's end loading time
	 * @param transportDurationMin this is used to calculate the arrive time based on the load end date when PDA was not available
	 * @return
	 */
	protected Date getArrivalTime(Date deliveryFromDat, Date bizArrivedAtDelPointTs, Date loadedTs, int transportDurationMin) {
		final int TOLERANCE = 300;
		
		// return value
		Date arrivalTime = null;

		if (bizArrivedAtDelPointTs == null) {

			if (loadedTs == null) { // we can't do much if both are null
				LOG.debug("Arrival time: loadedTs is null. using actualDeliveryDate");
				return deliveryFromDat;
			}
			
			Calendar loaded = Calendar.getInstance();
			loaded.setTime(loadedTs);
			loaded.add(Calendar.MINUTE, transportDurationMin);
			loaded.add(Calendar.MINUTE, TOLERANCE);

			if (deliveryFromDat == null) {
				LOG.debug("Arrival time: actualDeliveryDate is null. using loadedTs + transportDurationMin");
				return loaded.getTime();
			}
			
			Calendar deliveryDate = Calendar.getInstance();
			deliveryDate.setTime(deliveryFromDat);

			if (loaded.compareTo(deliveryDate) <= 0) { // pre-loading
				LOG.debug("Arrival time: actualDeliveryDate used");
				arrivalTime = null;
			}
			else { // no pre-loading
				LOG.debug("Arrival time: load time used");
				// the tolerance was only needed for the comparison. 
				// it's simpler just to subtract again than creating a separate calendar
				loaded.add(Calendar.MINUTE, -1 * TOLERANCE);
				arrivalTime = loaded.getTime();
			}

		}
		else { // When PDA is available
			LOG.debug("Arrival time: BIZ_ENTER_ST_TS used");
			arrivalTime = bizArrivedAtDelPointTs;
		}

		return arrivalTime;
	}
	
	/**
	 * This is the calculation for KPI 1 which indicates whether the delivery was made too early or too late.
	 * <p>
	 * This takes app.default.delivery.tolerance.from and app.default.delivery.tolerance.to into account from tlgnc_config.
	 * <p> 		
	 *	Within delivery window: DEL_FROM - [tolerance_from] <= [ARRIVAL] <= DEL_TO + [tolerance_to] <br>
	 *	Early delivery: DEL_FROM - [tolerance_from] > [ARRIVAL] < DEL_TO + [tolerance_to] <br>
	 *	Late delivery: DEL_FROM - [tolerance_from] < [ARRIVAL] > DEL_TO + [tolerance_to] <br>
     *	Invalid: If any of the timestamps is missing / not calculable <br>
	 * <p>
	 *  @return true if the delivery was early or late. false if it was on time. null if any of the imput params were null
	 * */
	protected Boolean deliveredEarlyOrLate(Date deliveryFromDate, Date deliveryToDate, Date arrival) {
		int toleranceFrom = getConfigBean().getAppDefaultDeliveryToleranceFrom();
		int toleranceTo = getConfigBean().getAppDefaultDeliveryToleranceTo();

		if (deliveryFromDate == null || deliveryToDate == null || arrival == null) {
			LOG.debug("deliveredEarlyOrLate calculation skipped as one of the input params are null");
			return null; // if any of the dates are missing we cannot determine the on time flag
		}

		Calendar from = Calendar.getInstance();
		from.setTime(deliveryFromDate);
		// adding minutes can cause the cal to jump to previous day. which is desired here
		from.add(Calendar.MINUTE, -1 * toleranceFrom);

		Calendar to = Calendar.getInstance();
		to.setTime(deliveryToDate);
		to.add(Calendar.MINUTE, toleranceTo);

		Calendar arr = Calendar.getInstance();
		arr.setTime(arrival);

		boolean isEarly = from.compareTo(arr) > 0;
		boolean isLate = to.compareTo(arr) < 0;

		LOG.debug("deliveredEarlyOrLate: early={0} / late={1}", new Object[] { isEarly, isLate });

		if (isEarly || isLate) {
			return Boolean.TRUE;
		}
		else {
			return Boolean.FALSE;
		}
	}
	
	/**
	 * loads Order final for a given order id
	 * @param orderId 
	 * @return
	 */
	public OrderFinalEntity loadOrderFinal(Long orderId) {

		// [[
		OrderFinalEntity result = null;
		Object[][] params = {{"orderId", orderId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadOrderFinal", params);
		}
		// ]]
		try {
			
			final ReadOnlyDao<OrderFinalEntity> dao = getDaoProvider().getOrderFinalEntityDao(); 
			final Filter<OrderFinalEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(OrderFinalEntity.ATTR_O_ORDER_ID, orderId));
			filter.addExpression(Order.desc(OrderFinalEntity.ATTR_ORDER_FINAL_ID));			
			final List<OrderFinalEntity> orderFinalList = dao.findNRecs(0, 1, filter);
			
			//take the latest one
			if ((orderFinalList != null) && (!orderFinalList.isEmpty())) {
				result = (OrderFinalEntity)orderFinalList.get(0);
				if (result != null) {
					result.initialize();
				}
				return result;
			}
			
			return result = null;
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
	 * Updates the business partner related data of the frozen order related to the 
	 * parameter order.
	 * @param order - copy information from this order
	 * @return
	 */
	public OrderFinalEntity updateFrozenOrderBPData(OrderEntity order) {
		// [[
		OrderFinalEntity result = null;
		Object[][] params = {{"order", order}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateFrozenOrderBPData", params);
		}
		// ]]
		try {
			
			//update frozen order table
			OrderFinalEntity orderFinal  = loadOrderFinal(order.getOrderId());
			
			if (orderFinal ==  null){
				return result = orderFinal; 
			}
			
			freezeBillToData(order, orderFinal);
			
			// merge frozen order
			ReadWriteDao<OrderFinalEntity> orderFDao = getDaoProvider().getOrderFinalEntityDao();
			orderFinal = orderFDao.merge(orderFinal);
			if (logDebugEnabled) {
				LOG.debug("Updated OrderFinalEntity: " + orderFinal);
			}
			
			return result = orderFinal;
			
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
	 * Updates the order status information with the order status of the original order. 
	 * parameter order.
	 * @param order - copy information from this order
	 * @return
	 */
	public OrderFinalEntity updateFrozenOrderStatus(OrderEntity order) {
		// [[
		OrderFinalEntity result = null;
		Object[][] params = {{"order", order}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateFrozenOrderStatus", params);
		}
		// ]]
		try {
			//update frozen order table
			OrderFinalEntity orderFinal  = loadOrderFinal(order.getOrderId());
			
			if (orderFinal ==  null){
				return result = orderFinal; 
			}
			
			LOG.info("Updating frozen order " + orderFinal.getOrderFinalId() + "original order status has been changed from: " + orderFinal.getOOrderStatusCd() + " to: " + order.getOrderStatus().getOrderStatusCd());
			
			orderFinal.setOOrderStatusCd(order.getOrderStatus().getOrderStatusCd());
			
			// merge frozen order
			ReadWriteDao<OrderFinalEntity> orderFDao = getDaoProvider().getOrderFinalEntityDao();
			orderFinal = orderFDao.merge(orderFinal);
			if (logDebugEnabled) {
				LOG.debug("Updated OrderFinalEntity: " + orderFinal);
			}
			return result = orderFinal;	
			
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
	 * Updates the service level information. 
	 * @param orderFinal - already loaded OrderFinalEntity
	 * @return
	 */
	public OrderFinalEntity updateFrozenOrderSvcLevelInfo(OrderFinalEntity orderFinal) {
		// [[
		OrderFinalEntity result = null;
		Object[][] params = {{"orderFinal", orderFinal}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateFrozenOrderSvcLevelInfo", params);
		}
		// ]]
		try {
			
			// merge frozen order
			ReadWriteDao<OrderFinalEntity> orderFDao = getDaoProvider().getOrderFinalEntityDao();
			orderFinal = orderFDao.merge(orderFinal);
			if (logDebugEnabled) {
				LOG.debug("Updated OrderFinalEntity (service level info): " + orderFinal);
			}
			return result = orderFinal;	
			
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
