/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;
import javax.persistence.NonUniqueResultException;

import com.crh.logon.business.edifact.sbb.Cibef102Bean;
import com.crh.logon.business.services.beans.UomConversionResult;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.EdifactExportServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UomConverterServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonUtil;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.edifact.sbb.Cibef102Converter;
import com.crh.logon.business.edifact.tags.Tag;
import com.crh.logon.business.services.dispatchers.EdifactExportServiceDispatcher;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.business.services.utils.FtpClientFactory;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.authorization.Authorizer;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.RailRouteEntity;
import com.crh.logon.persistence.entities.common.RailTariffEntity;
import com.crh.logon.persistence.entities.common.RailwayStationEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialDescriptionEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialPlantEntity;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonConversionException;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class EdifactExportServiceBean extends EdifactExportServiceDispatcher implements EdifactExportServiceLocalInterface {

	private static final String FTP_SERVICE_NAME_TRANSMIT_TO_SBB = "transmit_to_sbb";
	private static final String CONTAINER_EMPTY_NHM_CODE = "993100";
	private static final String CONTAINER_LOADED_NHM_CODE = "994100";
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(EdifactExportServiceBean.class);
	

	@EJB private UomConverterServiceLocalInterface unitOfMeasurementConverterService;
	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;
	@EJB private DispoServiceLocalInterface dispoService;

	private String getTariffNo(String nhmCode, RailwayStationEntity depRailwayStation, RailwayStationEntity arrRailwayStation, String wagonUicClassCd) {
		
		/*
		 * marius/2013-01-29: This works according to the following algorithm:
		 * Try first a filter with the strictest criteria, then loosen the criteria
		 * (by setting wagon_uic_class_cd to null), then loosen again by setting arr_railway_station
		 * to null, etc.
		 * 
		 * Below, we query the DAO for a tariff entity using the filters in decreasing order of
		 * strictness. The first time a result is returned, we use that tariff.
		 * 
		 * If none of the filters return a value, the filter with all attributes set to NULL is
		 * used as a last fallback. This means there must exist EXACTLY ONE entry in
		 * tlgnc_rail_tariff with 
		 *    nhm_code = dep_railway_station = arr_railway_station = wagon_uic_class_cd = NULL.
		 *    
		 * Usually this entry has ID rail_tariff_id = 1000, but LOGON does not depend on that ID
		 * value (see LOGON-4148 for original discussion and LOGON-6392 for a later issue).
		 * 
		 * The issue LOGON-6392 arose because because one of the filters (the one for NHM code only)
		 * returned MORE THAN ONE result (two, in the event). This is a data error and must be
		 * avoided. 
		 */
		Filter<RailTariffEntity> filter;
		final ReadOnlyDao<RailTariffEntity> dao = getDaoProvider().getRailTariffEntityDao();
		final Collection<Filter<RailTariffEntity>> filterList = new ArrayList<Filter<RailTariffEntity>>();
		if (nhmCode != null) {
			if ( (depRailwayStation != null) && (arrRailwayStation != null) ) {
				if (wagonUicClassCd != null) {
					filter = dao.createFilter();
					filter.addExpression(Restrictions.eq(RailTariffEntity.ATTR_NHM_CODE, nhmCode));
					filter.addExpression(Restrictions.eq(RailTariffEntity.ATTR_DEP_RAILWAY_STATION, depRailwayStation));
					filter.addExpression(Restrictions.eq(RailTariffEntity.ATTR_ARR_RAILWAY_STATION, arrRailwayStation));
					filter.addExpression(Restrictions.eq(RailTariffEntity.ATTR_WAGON_UIC_CLASS_CD, wagonUicClassCd));
					filterList.add(filter);
				}
				
				filter = dao.createFilter();
				filter.addExpression(Restrictions.eq(RailTariffEntity.ATTR_NHM_CODE, nhmCode));
				filter.addExpression(Restrictions.eq(RailTariffEntity.ATTR_DEP_RAILWAY_STATION, depRailwayStation));
				filter.addExpression(Restrictions.eq(RailTariffEntity.ATTR_ARR_RAILWAY_STATION, arrRailwayStation));
				filter.addExpression(Restrictions.isNull(RailTariffEntity.ATTR_WAGON_UIC_CLASS_CD));
				filterList.add(filter);
			}
			if (depRailwayStation != null) {
				filter = dao.createFilter();
				filter.addExpression(Restrictions.eq(RailTariffEntity.ATTR_NHM_CODE, nhmCode));
				filter.addExpression(Restrictions.eq(RailTariffEntity.ATTR_DEP_RAILWAY_STATION, depRailwayStation));
				filter.addExpression(Restrictions.isNull(RailTariffEntity.ATTR_ARR_RAILWAY_STATION));
				filter.addExpression(Restrictions.isNull(RailTariffEntity.ATTR_WAGON_UIC_CLASS_CD));
				filterList.add(filter);
			}
			if (arrRailwayStation != null) {
				filter = dao.createFilter();
				filter.addExpression(Restrictions.eq(RailTariffEntity.ATTR_NHM_CODE, nhmCode));
				filter.addExpression(Restrictions.isNull(RailTariffEntity.ATTR_DEP_RAILWAY_STATION));
				filter.addExpression(Restrictions.eq(RailTariffEntity.ATTR_ARR_RAILWAY_STATION, arrRailwayStation));
				filter.addExpression(Restrictions.isNull(RailTariffEntity.ATTR_WAGON_UIC_CLASS_CD));
				filterList.add(filter);				
			}
			if (wagonUicClassCd != null) {
				filter = dao.createFilter();
				filter.addExpression(Restrictions.eq(RailTariffEntity.ATTR_NHM_CODE, nhmCode));
				filter.addExpression(Restrictions.isNull(RailTariffEntity.ATTR_DEP_RAILWAY_STATION));
				filter.addExpression(Restrictions.isNull(RailTariffEntity.ATTR_ARR_RAILWAY_STATION));
				filter.addExpression(Restrictions.eq(RailTariffEntity.ATTR_WAGON_UIC_CLASS_CD, wagonUicClassCd));
				filterList.add(filter);
			}
			filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(RailTariffEntity.ATTR_NHM_CODE, nhmCode));
			filter.addExpression(Restrictions.isNull(RailTariffEntity.ATTR_DEP_RAILWAY_STATION));
			filter.addExpression(Restrictions.isNull(RailTariffEntity.ATTR_ARR_RAILWAY_STATION));
			filter.addExpression(Restrictions.isNull(RailTariffEntity.ATTR_WAGON_UIC_CLASS_CD));
			filterList.add(filter);
		}
		
		filter = dao.createFilter();
		filter.addExpression(Restrictions.isNull(RailTariffEntity.ATTR_NHM_CODE));
		filter.addExpression(Restrictions.isNull(RailTariffEntity.ATTR_DEP_RAILWAY_STATION));
		filter.addExpression(Restrictions.isNull(RailTariffEntity.ATTR_ARR_RAILWAY_STATION));
		filter.addExpression(Restrictions.isNull(RailTariffEntity.ATTR_WAGON_UIC_CLASS_CD));
		filterList.add(filter);
		
		try {
			for (Filter<RailTariffEntity> f : filterList) {
				final RailTariffEntity railTariff = getDaoProvider().getRailTariffEntityDao().findSingle(f);
				if (railTariff != null) {
					return railTariff.getTariffNumber();
				}
			}
		}
		catch (NonUniqueResultException e) {
			// This indicates a data error, see long comment above.
			final String msg = String.format("Found multiple matching entries in tlgnc_rail_tariff. " +
					"This indicates a data error. The query attributes were: " +
					"nhmCode = %s, depRailwayStationId = %d, arrRailwayStationId = %d, wagonUicCassCd = %s",
					nhmCode,
					depRailwayStation.getRailwayStationId(),
					arrRailwayStation.getRailwayStationId(),
					wagonUicClassCd);
			
			LOG.error(msg);
			throw new LogonException(msg, e);
		}
		
		throw new LogonException("No matching tariff number found", Reason.RES_ORDER_INSUFFICIENT_DATA);
	}
	
	
	private RailRouteEntity getRailRoute(RailwayStationEntity depRailwayStation, RailwayStationEntity arrRailwayStation, Date date) {
		final ReadOnlyDao<RailRouteEntity> dao = getDaoProvider().getRailRouteEntityDao();
		final Filter<RailRouteEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(RailRouteEntity.ATTR_DEP_RAILWAY_STATION, depRailwayStation));
		filter.addExpression(Restrictions.eq(RailRouteEntity.ATTR_ARR_RAILWAY_STATION, arrRailwayStation));
		
		final Calendar c = Calendar.getInstance();
		c.setTime(date);
		
		final Long dayOfWeek;
		switch (c.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.MONDAY:
				dayOfWeek = Long.valueOf(1l);
				break;
			case Calendar.TUESDAY:
				dayOfWeek = Long.valueOf(2l);
				break;
			case Calendar.WEDNESDAY:
				dayOfWeek = Long.valueOf(3l);
				break;
			case Calendar.THURSDAY:
				dayOfWeek = Long.valueOf(4l);
				break;
			case Calendar.FRIDAY:
				dayOfWeek = Long.valueOf(5l);
				break;
			case Calendar.SATURDAY:
				dayOfWeek = Long.valueOf(6l);
				break;
			case Calendar.SUNDAY:
				dayOfWeek = Long.valueOf(7l);
				break;
			default:
				throw new LogonException("Unknown day value " + c.get(Calendar.DAY_OF_WEEK), Reason.RES_ERROR_IMPL);
		}		
		filter.addExpression(Restrictions.or(
				Restrictions.eq(RailRouteEntity.ATTR_DAY_OF_WEEK, dayOfWeek),
				Restrictions.isNull(RailRouteEntity.ATTR_DAY_OF_WEEK)));
		
		final Collection<RailRouteEntity> railRoutes = dao.find(filter);
		// This might return 0, 1 or 2 (with and without day of week)
		RailRouteEntity result = null;
		for (RailRouteEntity railRoute : railRoutes) {
			// If it has the day of week, then that entry will be returned.
			// Otherwise, the last one without day of week,
			if ((railRoute.getDayOfWeek() != null) || (result == null)) {
				result = railRoute;
			}
		}
		
		return result;
	}
	
	
	private ShippingpointEntity getSendShippingpoint(OrderEntity order, ShiptoEntity shipToEntity) {
		
		ShippingpointEntity sp = order.getShippingpoint();
		if ( sp != null && !sp.getKnownAlsoInSapFlag() ) {
			//in case this flag is false other shippingpoint must be used
			if ( sp.getSendSapShippingpoint() != null ) {
				sp = sp.getSendSapShippingpoint();
			}
			else if (shipToEntity != null) {
				sp = shipToEntity.getRailChainShippingpoint();
				if ( sp != null && !sp.getKnownAlsoInSapFlag() ) {
					sp = sp.getSendSapShippingpoint();
				}
			}
			
			if (sp == null) {
				//EXCEPTIONAL CASE HANDLING SEE ADN53613
				//LOG error, this is not normal, but must be able to handle!
				LOG.error(String.format(
						"Error while processing SAP data of order: %d; order number: %d. Shippingpoint info could not be determined!" + 
						"Please check master data of shippingpoint \"%s\" (id: %d)!",
						order.getOrderId(),
						order.getOrderNumber(),
						order.getShippingpoint().getShippingpointName(),
						order.getShippingpoint().getShippingpointId()));
				
				if (LOG.isInfoEnabled()) {
					LOG.info(String.format(
							"No valid SAP shippingpoint could be determined for order: %d; order number: %d. Using hardcoded shippingpoint (SAP_SHIPPINGPOINT_NO: \"%s\")",
							order.getOrderId(),
							order.getOrderNumber(),
							Constant.SAP_EXCEPTIONAL_SHIPPINGPOINT_CODE));
				}
				
				final ReadOnlyDao<ShippingpointEntity> dao = getDaoProvider().getShippingpointEntityDao();
				final Filter<ShippingpointEntity> filter = dao.createFilter();
				filter.addExpression(Restrictions.eq(ShippingpointEntity.ATTR_SAP_SHIPPINGPOINT_NO, Constant.SAP_EXCEPTIONAL_SHIPPINGPOINT_CODE));
				
				Authorizer.disableNextAccessRestriction();
				sp = dao.findSingle(filter);
				
				if (sp == null) {
					final String message = "Shippingpoint for SAP_SHIPPINGPOINT_NO : " + Constant.SAP_EXCEPTIONAL_SHIPPINGPOINT_CODE + " is missing.";
					LOG.error(message);
					throw new LogonException(message, Reason.RES_ORDER_INSUFFICIENT_DATA);
				}
			}
		}

		//now it it definite which shippingpoint it is there are several attributes what depend on the shippingpoint
		return sp;
	}	
	
	private Cibef102Bean getCibef102Bean(final OrderEntity order, final WagonEntity wagon) {
		Authorizer.disableNextAccessRestriction();
		ShiptoEntity shipTo = dispoService.fetchShipto(order.getShiptoSapBp().getSapBpId());
		final ConfigBean cb = getConfigBean();
		final long interchangeControlReference = fetchSequenceNextVal("SLGNC_CIBEF102_ICR");
		
		// Since there's always 1 message per interchange, we can use the same sequence.
		final Cibef102Bean cibef102Bean = new Cibef102Bean("ICR" + StringUtils.leftPad("" + interchangeControlReference, 11, '0'), "1");
		cibef102Bean.setDateTimeOfMessage(new Date());
		cibef102Bean.setSenderCompanyNo(cb.getTransmitToSbbContentCompanyNo());
		cibef102Bean.setSenderCompanyLocNo(cb.getTransmitToSbbContentCompanyLocNo());
		cibef102Bean.setTestIndicator(getConfigBean().getLogonProperties().getDevmode());
		cibef102Bean.setOrderNo(order.getOrderNumber().toString());
		cibef102Bean.setTransportPriority(cb.getTransmitToSbbContentTransPrio());
		cibef102Bean.setActualDepCountryCode(order.getShippingpoint().getRailwayStation().getUicCountry().getUicCountryCd().toString());
		cibef102Bean.setActualDepRailwayStationNo(order.getShippingpoint().getRailwayStation().getRailwayStationNo());
		cibef102Bean.setActualDepRailwayStationLoadingPoint(order.getShippingpoint().getRailwayStationLoadingPoint());
		
		final RailwayStationEntity commDepRailwayStation = LogonUtil.getCommercialRailwayStationEntity(
				order.getShippingpoint().getRailwayStation(),
				getDaoProvider().getRailwayStationCommEntityDao());
		
		cibef102Bean.setCommDepCountryCode(commDepRailwayStation.getUicCountry().getUicCountryCd().toString());
		cibef102Bean.setCommDepRailwayStationNo(commDepRailwayStation.getRailwayStationNo());
		cibef102Bean.setActualArrCountryCode(order.getShiptoSapBp().getRailwayStation().getUicCountry().getUicCountryCd().toString());
		cibef102Bean.setActualArrRailwayStationNo(order.getShiptoSapBp().getRailwayStation().getRailwayStationNo());
		cibef102Bean.setActualArrRailwayStationUnloadingPoint(shipTo.getTrainStationUnloadingPoint());
		
		
		final RailwayStationEntity commArrRailwayStation = LogonUtil.getCommercialRailwayStationEntity(
				order.getShiptoSapBp().getRailwayStation(),
				getDaoProvider().getRailwayStationCommEntityDao());
		cibef102Bean.setCommArrCountryCode(commArrRailwayStation.getUicCountry().getUicCountryCd().toString());
		cibef102Bean.setCommArrRailwayStationNo(commArrRailwayStation.getRailwayStationNo());		
		
		final RailRouteEntity railRoute = getRailRoute(
				order.getShippingpoint().getRailwayStation(),
				order.getShiptoSapBp().getRailwayStation(),
				cibef102Bean.getDateTimeOfMessage());
		
		if (railRoute != null) {
			if (railRoute.getViaRailwayStation() != null) {
				RailwayStationEntity commViaRailwayStation = LogonUtil.getCommercialRailwayStationEntity(railRoute.getViaRailwayStation(), getDaoProvider().getRailwayStationCommEntityDao());
				cibef102Bean.setActualViaCountryCode(railRoute.getViaRailwayStation().getUicCountry().getUicCountryCd().toString());
				cibef102Bean.setActualViaRailwayStationNo(railRoute.getViaRailwayStation().getRailwayStationNo());
				cibef102Bean.setCommViaCountryCode(commViaRailwayStation.getUicCountry().getUicCountryCd().toString());
				cibef102Bean.setCommViaRailwayStationNo(commViaRailwayStation.getRailwayStationNo());
			}
			cibef102Bean.setRouteNumber(railRoute.getRouteNumber());
		}
		cibef102Bean.setTransportationMode(cb.getTransmitToSbbContentTransMode());
		cibef102Bean.setChargeCategory(cb.getTransmitToSbbContentChargeCat());
		cibef102Bean.setChargeMethodOfPayment(cb.getTransmitToSbbContentChargeMethodOfPayment());
		if (shipTo.getSbbCustomerNumber() != null) {
			cibef102Bean.setConsigneeCustomerNo(shipTo.getSbbCustomerNumber().replaceAll("\\s", ""));
		}
		cibef102Bean.setConsigneeName(order.getShiptoSapBp().getName1());
		cibef102Bean.setConsigneeStreetAndHouseNo(order.getShiptoSapBp().getStreet());
		cibef102Bean.setConsigneeCity(order.getShiptoSapBp().getCity());
		cibef102Bean.setConsigneePostalCode(order.getShiptoSapBp().getPostalCode());
		cibef102Bean.setConsigneeCustomerCountryCode(order.getShiptoSapBp().getRailwayStation().getUicCountry().getUicCountryCd().toString());
		if (order.getShippingpoint().getSbbCustomerNumber() != null) {
			cibef102Bean.setConsignorCustomerNo(order.getShippingpoint().getSbbCustomerNumber().replaceAll("\\s", ""));
		}
		cibef102Bean.setConsignorCustomerCountryCode(order.getShippingpoint().getRailwayStation().getUicCountry().getUicCountryCd().toString());
		cibef102Bean.setConsignorName(order.getShippingpoint().getShippingpointName());
		cibef102Bean.setDespatchChargePayer(wagon.getWagonKeeper().getDespatchChargePayer());			
		
		final List<OrderItemEntity> orderItemList = new ArrayList<OrderItemEntity>((Collection<OrderItemEntity>) order.getOrderItems());
		
		Collections.sort(orderItemList, new Comparator<OrderItemEntity>() {
			public int compare(OrderItemEntity o1, OrderItemEntity o2) {
				return o1.getOrderItemNo().compareTo(o2.getOrderItemNo());
			}
		});

		final Map<Long, Collection<Cibef102Bean.GoodsItem>> goodsItemsForContainers = new HashMap<Long, Collection<Cibef102Bean.GoodsItem>>();

		final Cibef102Bean.Wagon cibef102Wagon = cibef102Bean.new Wagon();
		cibef102Wagon.setWagonNo(wagon.getWagonNo());
		
		Collection<Cibef102Bean.GoodsItem> goodsItemList;
		for (OrderItemEntity orderItem : orderItemList) {
			final String sapMaterialNo = orderItem.getSapMaterial().getSapMaterialNo();

			final Cibef102Bean.GoodsItem gi = cibef102Bean.new GoodsItem();
			final ShippingpointEntity sendShippingpoint = findSendShippingpoint(order);
			
			final String nhmCode = orderItemNhmCode(orderItem, sendShippingpoint);
			if (nhmCode != null) {
				// TODO (marius Oct 29, 2012): Why 6? Replace magic constant please.
				gi.setNhmCode(nhmCode.substring(0, 6));
			}
			
			
			if (cibef102Bean.getTariffNo() == null) {
				cibef102Bean.setTariffNo(
						getTariffNo(
								gi.getNhmCode(),
								order.getShippingpoint().getRailwayStation(),
								order.getShiptoSapBp().getRailwayStation(),
								wagon.getWagonType().getWagonUicClassCd()));
			}
			
			final ReadOnlyDao<SapMaterialDescriptionEntity> dao = getDaoProvider().getSapMaterialDescriptionEntityDao();
			final Filter<SapMaterialDescriptionEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq("sapMaterialDescriptionPK.sapMaterialId", orderItem.getSapMaterial().getSapMaterialId()));
			filter.addExpression(Restrictions.eq("sapMaterialDescriptionPK.langCd", Constant.LANGUAGE_CODE_GERMAN));
			
			Authorizer.disableNextAccessRestriction();
			final SapMaterialDescriptionEntity sapMatDesc = dao.findSingle(filter);
			
			final BigDecimal loadedQty = (BigDecimal) ObjectUtils.defaultIfNull(
					orderItem.getLoadedQuantity(),
					BigDecimal.ZERO);
			
			BigDecimal netWeightForRailOrder;
			
			if (Constant.SPECIAL_MATERIAL_QTY_FOR_SBB.containsKey(sapMaterialNo)) {
				/* LOGON-4893: For certain materials we must use a hardcoded special quantity.
				 * Attention: If at some point we need to interface with rail companies other than
				 * SBB, the handling needs to be changed here, as it is specific to SBB.
				 */
				netWeightForRailOrder = Constant.SPECIAL_MATERIAL_QTY_FOR_SBB.get(sapMaterialNo);
			}
			else if (orderItem.getLoadedUom() != null) {
				final UomBsEntity loadedUom = orderItem.getLoadedUom();
				final UomBsEntity uomTons = referentialDataService.findBsEntity(UomBsEntity.class, Constant.UOM_TONS);
				
				try {
					final UomConversionResult uomConversionResult = unitOfMeasurementConverterService.convert(
							loadedQty,
							loadedUom,
							uomTons,
							orderItem.getSapMaterial(),
							null, null, false);
					
					netWeightForRailOrder = uomConversionResult.getQuantity();
				}
				catch (LogonConversionException e) {
					LOG.error(String.format(
							"Could not convert to TO though the uom dimesion is TO [orderNumber=%d, orderId=%d, orderItemId=%d, loadedUom=%s]",
							order.getOrderNumber(),
							order.getOrderId(),
							orderItem.getOrderItemId(),
							loadedUom.getUomCd()),
							e);
					
					netWeightForRailOrder = loadedQty;
				}
			}
			else {
				netWeightForRailOrder = loadedQty;
			}
			
			gi.setDescription(sapMaterialNo + " " + StringUtils.defaultString(
																sapMatDesc.getTxt(),
																netWeightForRailOrder.toPlainString()));
			gi.setNetWeightT(netWeightForRailOrder);
			
			if ((orderItem.getLoad() != null) && (orderItem.getLoad().getContainer() != null)) {
				final ContainerEntity container = orderItem.getLoad().getContainer();
				goodsItemList = goodsItemsForContainers.get(container.getContainerId());
				if (goodsItemList == null) {
					goodsItemList = new ArrayList<Cibef102Bean.GoodsItem>();
					goodsItemsForContainers.put(container.getContainerId(), goodsItemList);
				}
				goodsItemList.add(gi);
			}
			else {
				cibef102Wagon.getGoodsItems().add(gi);
			}
		}
				
		final ReadOnlyDao<ContainerEntity> containerEntityDao = getDaoProvider().getContainerEntityDao();
		final Filter<ContainerEntity> filter = containerEntityDao.createFilter();
		filter.addExpression(Restrictions.eq(ContainerEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
		filter.addExpression(Restrictions.eq(ContainerEntity.ATTR_CURRENT_WAGON, wagon));
		
		Authorizer.disableNextAccessRestriction();
		final Collection<ContainerEntity> containerList = getDaoProvider().getContainerEntityDao().find(filter);
		
		for (ContainerEntity container : containerList) {
			
			Cibef102Bean.Container cibef102Container = cibef102Bean.new Container();
			cibef102Container.setContainerNo(container.getContainerName());
			goodsItemList = goodsItemsForContainers.get(container.getContainerId());
			if (goodsItemList != null) {
				cibef102Container.getGoodsItems().addAll(goodsItemList);
			}
			if (!cibef102Container.getGoodsItems().isEmpty()) {
				cibef102Container.setNhmCode(CONTAINER_LOADED_NHM_CODE);
			}
			else {
				cibef102Container.setNhmCode(CONTAINER_EMPTY_NHM_CODE);
			}
			if (container.getLengthFt() != null) {
				cibef102Container.setLengthCode(container.getLengthFt().toPlainString());
			}
			if (container.getTareWeightT() != null) {
				cibef102Container.setTareWeightKg(Constant.TON_TO_KG.multiply(container.getTareWeightT()));
			}
			if (container.getLengthFt() != null) {
				cibef102Container.setLengthMm(container.getLengthFt().multiply(BigDecimal.valueOf(1200).divide(BigDecimal.valueOf(3937), RoundingMode.HALF_UP)));
			}
			if (container.getWidthMm() != null) {
				cibef102Container.setWidthMm(container.getWidthMm());
			}
			if (container.getHeightMm() != null) {
				cibef102Container.setHeightMm(container.getHeightMm());	
			}
			cibef102Wagon.getContainers().add(cibef102Container);
		}
		cibef102Bean.setWagon(cibef102Wagon);
		
		return cibef102Bean;
	}


	/**
	 * Finds an order item's NHM code.
	 * @param orderItem
	 * @param shippingPoint
	 * @return The order item's NHM code, or null if the NHM code could not be found.
	 * This is for example the case if there is no entry in the TLGNS_SAP_MATERIAL_PLANT table
	 * or an entry is found in the table but no NHM code is set (the NHM column is nullable).
	 */
	private String orderItemNhmCode(OrderItemEntity orderItem, ShippingpointEntity shippingPoint) {
		if (orderItem == null) {
			throw new IllegalArgumentException("orderItem must not be null");
		}
		if (shippingPoint == null) {
			throw new IllegalArgumentException("shippingPoint must not be null");
		}

		// Load the right SapMaterialPlant entry for the order material and the plant.
		final ReadWriteDao<SapMaterialPlantEntity> dao = getDaoProvider().getSapMaterialPlantEntityDao();
		final Filter<SapMaterialPlantEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(SapMaterialPlantEntity.ATTR_PLANT, shippingPoint.getPlant()));
		filter.addExpression(Restrictions.eq(SapMaterialPlantEntity.ATTR_SAP_MATERIAL, orderItem.getSapMaterial()));
		
		/*
		 * TODO (marius Oct 29, 2012): Please put a comment why the access restriction is disabled
		 * here. It is not obvious.
		 */
		Authorizer.disableNextAccessRestriction();
		final SapMaterialPlantEntity sapMatPlant = dao.findSingle(filter);
		if (sapMatPlant != null) {
			return sapMatPlant.getNhmCode();
		}
		else {
			return null;
		}
	}
//
//	/**
//	 * Returns the SendShippingPointEntity for a given order.
//	 * @param order An order
//	 * @return The shippingpoint entity for the order.
//	 */
	private ShippingpointEntity findSendShippingpoint(final OrderEntity order) {
		// [[
		ShippingpointEntity result = null;
		Object[][] params = {{"order", order}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findSendShippingpoint", params);
		}
		// ]]
		try {
			if (order == null) {
				throw new IllegalArgumentException("order must not be null");
			}
			
			final ReadWriteDao<ShiptoEntity> dao = getDaoProvider().getShiptoEntityDao();
			final Filter<ShiptoEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(ShiptoEntity.ATTR_SHIPTO_SAP_BP, order.getShiptoSapBp()));
			
			/* TODO (marius Oct 29, 2012): Please add a comment 
			 * why the access restriction must be disabled here.
			 * It is not obvious.
			 */
			Authorizer.disableNextAccessRestriction();
			final ShiptoEntity shipTo = dao.findSingle(filter);
			return result = getSendShippingpoint(order, shipTo);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public void transmitToSbb(OrderEntity order, WagonEntity wagon) {
	
		final Cibef102Bean cibef102Bean = getCibef102Bean(order, wagon);
		
		if (StringUtils.isEmpty(getConfigBean().getTransmitToSbbUrl())) {
			throw new LogonException("Config " + ConfigBean.CFG_TRANSMITTOSBBURL + " not specified, import disabled", Reason.RES_ERROR_CONFIG_NOT_EXISTENT);
		}
		
		FTPClient ftpClient = null;
		InputStream in = null;
		Writer w = null;
		try {
			ftpClient = FtpClientFactory.createFtpClient(getConfigBean(), FTP_SERVICE_NAME_TRANSMIT_TO_SBB);
			File outputDumpFile = null;
			
			final String fileName = MessageFormat.format(
					getConfigBean().getTransmitToSbbFileName(),
					cibef102Bean.getInterchangeConrolReference(),
					cibef102Bean.getDateTimeOfMessage());
			
			final Charset transmitToSbbCharset = Charset.forName(getConfigBean().getTransmitToSbbCharset());
			final Collection<Tag> interchange = Cibef102Converter.convert(cibef102Bean);
			if (getConfigBean().getTransmitToSbbDumpEnable()) {
				final String dirName = MessageFormat.format(
						getConfigBean().getTransmitToSbbDumpDir(),
						cibef102Bean.getDateTimeOfMessage());
				
				outputDumpFile = new File(dirName, fileName);
				final File dir = new File(outputDumpFile.getParent());
				if (!dir.exists()) {
					if (!dir.mkdirs()) {
						throw new IOException("File " + outputDumpFile.getAbsoluteFile() + " could not be created");
					}
				}
				w = new OutputStreamWriter(new FileOutputStream(outputDumpFile), transmitToSbbCharset);
			}
			else {
				OutputStream os = ftpClient.storeFileStream(fileName);
				w = new OutputStreamWriter(os, transmitToSbbCharset);
			}
			
			for (Tag t : interchange) {
				t.write(w);
			}
			w.flush();
			
			if (getConfigBean().getTransmitToSbbDumpEnable()) {
				in = new FileInputStream(outputDumpFile); 
				if (!ftpClient.storeFile(fileName, in)) {
					throw new LogonException("Failed to upload file " + fileName, Reason.RES_ERROR_UNSPECIFIED);
				}
			}
			else {
				if (!ftpClient.completePendingCommand()) {
					throw new LogonException("Failed to upload file " + fileName, Reason.RES_ERROR_UNSPECIFIED);
				}
			}
			
			final String newFileName = MessageFormat.format(
					getConfigBean().getTransmitToSbbRenamedFileName(),
					cibef102Bean.getInterchangeConrolReference(),
					cibef102Bean.getDateTimeOfMessage());
			
			if ( (StringUtils.isNotEmpty(newFileName)) && (!newFileName.equals(fileName)) ) {
				ftpClient.rename(fileName, newFileName);
				int replyCode = ftpClient.getReplyCode();
				if (!FTPReply.isPositiveCompletion(replyCode)) {
				
					throw new IOException("Error renaming file " + fileName + " to " + newFileName + ": " + ftpClient.getReplyString());
				}
			}
		}
		catch (IOException e) {
			throw new LogonException(e);
		}
		finally {
			if (in != null) {
				try {
					in.close();
				}
				catch (IOException e) {
					LOG.error("Error closing dump file inputstream", e);
				}
			}
			if (w != null) {
				try {
					w.close();
				}
				catch (IOException e) {
					LOG.error("Error closing ftp writer", e);
				}
			}				
			if ( (ftpClient != null) && (ftpClient.isConnected()) ) {
				FtpClientFactory.disconnectFtp(ftpClient);
			}
		}
	}
	
	@PreDestroy
	public void preDestroy() {
		FtpClientFactory.shutdown();
	}
	
}