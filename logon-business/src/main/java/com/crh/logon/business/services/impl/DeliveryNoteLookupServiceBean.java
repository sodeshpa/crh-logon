/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import com.crh.logon.business.services.localinterfaces.ConfigServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.PDFGeneratorServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TruckAdminServiceLocalInterface;
import org.apache.commons.lang.StringUtils;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.DeliveryNoteLookupServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.DeliveryNoteLookupServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DispokarteServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TimeZoneServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.SchedulingUtils;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.business.stores.domain.MaterialPlantStoreInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.FireqMaterialMixdesignEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;

/**
 * @author AdNovum Informatik AG
 */

@AdnTrace
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class DeliveryNoteLookupServiceBean extends DeliveryNoteLookupServiceDispatcher
		implements DeliveryNoteLookupServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DeliveryNoteLookupServiceBean.class);

	private enum PriceToPrint { DONTPRINT, ZERO, ACTUALPRICE };
	
	public static final Long BULK_PWDR_PKG_CD = Constant.PACKAGE_TYPE_BULK_POWDER;
	public static final Long BULK_DMPD_PKG_CD = Constant.PACKAGE_TYPE_BULK_DUMPED;
	public static final Long BAG_PKG_CD = Constant.PACKAGE_TYPE_BBAG;
	public static final Long SERVICE_PKG_CD = Constant.PACKAGE_TYPE_SERVICE;
	public static final Long BAG_AND_SERVICE = Constant.MIXED_ORDER_BAG_AND_SERVICE;
	public static final Long BULK_AND_SERVICE = Constant.MIXED_ORDER_BULK_AND_SERVICE;
	
	private @EJB DispokarteServiceLocalInterface dispokarteService;
	private @EJB TimeZoneServiceLocalInterface timezoneService;
	private @EJB RefDataGenericsServiceLocalInterface referentialDataService;
	private @EJB
	PDFGeneratorServiceLocalInterface pdfGeneratorService;
	private @EJB OrderServiceLocalInterface orderService;
	private @EJB
	TruckAdminServiceLocalInterface truckAdminService;
	private @EJB DispoServiceLocalInterface dispoService;
	private @EJB
	ConfigServiceLocalInterface configService;
	private @EJB MaterialPlantStoreInterface materialPlantStore;
	
	/**
	 * This method sets the material/package type of the whole order depending on 
	 * packageCd of the order item and the package type of the whole order
	 * <br><br>
	 * It can set bag, bulk, service and mixed values.
	 * 
	 * @param orderPkg
	 * @param packageCdOfOrderItem
	 * @return the updated orderPkg
	 */
	public Long decidePackageTypeOfWholeOrder(Long orderPkg, Long packageCdOfOrderItem) {
		if (packageCdOfOrderItem != null) {
												
			// Bulk powder
			if (BULK_PWDR_PKG_CD.equals(packageCdOfOrderItem) || BULK_DMPD_PKG_CD.equals(packageCdOfOrderItem)) { 
										
				if (SERVICE_PKG_CD.equals(orderPkg)){ // a former order item was service already -> mixed order
					orderPkg = BULK_AND_SERVICE;
				} else if (BULK_DMPD_PKG_CD.equals(packageCdOfOrderItem)){
					orderPkg = BULK_DMPD_PKG_CD;
				} else{
					orderPkg = BULK_PWDR_PKG_CD;
				}
			} 
			// bag
			else if (BAG_PKG_CD.equals(packageCdOfOrderItem)) { 
				
				if (SERVICE_PKG_CD.equals(orderPkg)){ // a former order item was service already -> mixed order
					orderPkg = BAG_AND_SERVICE;
				} else{
					orderPkg = BAG_PKG_CD;
				}
			}
			// service
			else if (SERVICE_PKG_CD.equals(packageCdOfOrderItem)){
				if (BULK_DMPD_PKG_CD.equals(orderPkg) || BULK_PWDR_PKG_CD.equals(orderPkg)){ // a former order item was bulk already -> mixed order
					orderPkg = BULK_AND_SERVICE;
				} else if (BAG_PKG_CD.equals(orderPkg)){ // a former order item was bag already -> mixed order
					orderPkg = BAG_AND_SERVICE;
				} else{
					orderPkg = SERVICE_PKG_CD;
				}
			}
			// something else (e.g. unknown)
			else {
				orderPkg = packageCdOfOrderItem;
			}
		}
		return orderPkg;
	}
	
	public String getDeliveryTimeString(OrderEntity order, Locale locale) {
		// set delivery time
		String deliveryTime = null;
		Filter<AssignmentPartEntity> filter = new FilterImpl<AssignmentPartEntity>();
		filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ORDER_ID, order.getOrderId()));
		ReadWriteDao<AssignmentPartEntity> asspDao = getDaoProvider().getAssignmentPartEntityDao();
		AssignmentPartEntity assp = asspDao.findSingle(filter);
		
		if (assp != null ) {
			TimeZone shipToTZ = getTimeZoneForShipTo(order.getShiptoSapBp(), locale);

			if (order.getNoPdaOrRailEventsFlag() || assp.getRealStart() == null ) {
				// case non-PDA OR the real start is not yet known:  add symbol "~" and round to 10 minutes. In case calculated delivery time is before given time window, use start time of customer time window. 
		
				//start with the planned value
				Date deliveryTimeUncertain = assp.getPlannedStart();
				
				if ( assp.getRealStart() != null ) {
					//if it is definite use that
					deliveryTimeUncertain = assp.getRealStart();
				}
				else {
					//else consider the IS values of the assignment, have to show the value signed on dispo screen for truck, and planned delivery for rail
					try {								
						deliveryTimeUncertain = SchedulingUtils.getCalculatedTimeOfDelivery(order, orderService.findAssignmentById(assp.getAssignment().getAssignmentId()), dispoService, configService, truckAdminService);
					}
					catch (Exception e) {
						LOG.error("Could not calculate delivery time  [order: " + order.getOrderId() + ", assignment: " + assp.getAssignment().getAssignmentId() + "]");
					}
				}
				
				if (deliveryTimeUncertain != null) {
				
					if ((order.getDeliveryFromDat() != null) && (deliveryTimeUncertain.before(order.getDeliveryFromDat()))) {
						deliveryTimeUncertain = order.getDeliveryFromDat();
					}
					
					Calendar cal = shipToTZ.asCalendar(deliveryTimeUncertain);

					if (!order.getDeliveryType().getDeliveryTypeCd().equals(Constant.DELIVERY_TYPE_PICKUP)) {
						//round to 10 minutes
						int minute = cal.get(Calendar.MINUTE);
						BigDecimal minBD = new BigDecimal(minute);
						minBD = minBD.multiply(new BigDecimal("0.1"));
						minBD = minBD.setScale(0, BigDecimal.ROUND_HALF_UP);
						minBD = minBD.multiply(new BigDecimal("10"));
						cal.set(Calendar.MINUTE, minBD.intValue());
					}

					deliveryTime = "~" + shipToTZ.fmtAsDayMonthYearHourMinuteWithOffset(cal.getTime());
				}
			}
			else {
				// case PDA: entry time geofence customer  (don't print if not reached)
				if (assp.getRealStart() != null) {
					deliveryTime = shipToTZ.fmtAsDayMonthYearHourMinuteWithOffset(assp.getRealStart());
				}
			}
		}
		return deliveryTime;
	}
	
	public String buildMaterialMixDesignInfo(OrderItemEntity orderItem) {
		StringBuilder buf = new StringBuilder();

		FireqMaterialMixdesignEntity mixDesignEntity = getMixDesignEntity(orderItem);
		if (mixDesignEntity != null) {
			buf.append(mixDesignEntity.getMixDesignCd());
			buf.append(" (");
			buf.append(mixDesignEntity.getAggRmxCode());
			buf.append(')');
		}

		return buf.toString();
	}

	public String getMaterialMixDesignProdPlantName(OrderItemEntity orderItem) {
		String plantName = null;

		FireqMaterialMixdesignEntity mixDesignEntity = getMixDesignEntity(orderItem);
		if (mixDesignEntity != null) {
			PlantBsEntity plant = mixDesignEntity.getPlant();
			if (plant != null) {
				plantName = plant.getPlantName();
			}
		}

		// TODO 2013-11/30/tito: does this fallback make sense?
		if (StringUtils.isEmpty(plantName) && orderItem.getProductionPlant() != null) {
			plantName = orderItem.getProductionPlant().getPlantName();
		}

		return plantName;
	}

	public FireqMaterialMixdesignEntity getMixDesignEntity(OrderItemEntity orderItem) {
		FireqMaterialMixdesignEntity mixDesign = null;

		LoadEntity load = orderItem.getLoad();
		if (load != null && load.getLoadingTs() != null) {
			mixDesign = load.getMixDesign();
		}

		if (mixDesign == null) {
			mixDesign = orderItem.getMixDesign();
		}

		return mixDesign;
	}
	
	/** get name of the UOM 
	 * @param loc locale
	 * @param uom UomBsEntity
	 * @return
	 */
	public String getUomName(Locale loc, UomBsEntity uom) {
		String uomStr = "";
		
		if (uom != null) {
			String uomCd = uom.getUomCd(); // Not nullable!
			
			// "TO" will be the UOM we see 99% of all cases.
			if (uomCd.equals(Constant.UOM_TONS)) {
				uomStr = Constant.UOM_TONS_TXT;
			}
			else if (uomCd.equals(Constant.UOM_MTQ)) {
				uomStr = Constant.UOM_MTQ_TXT;
			}
			else if (uomCd.equals(Constant.UOM_KG)) {
				uomStr = Constant.UOM_KG_TXT;
			}
			else {
				/*
				 * FIXME 05-Nov-2012/romek: The resulting text may be too wide, in which case
				 * jcan-print will not show the whole line!
				 */ 
				UomTxEntity uomTxEntity = referentialDataService.findTxEntity(UomTxEntity.class, loc, uom);
				
				if (uomTxEntity != null) {
					String originUomStr = uomTxEntity.getTxt();
					// Shortening UOMs to 4 Characters (Bahnwagen -> Bahn)
					uomStr = StringUtils.left(originUomStr, 4);
				}
			}
		}
		
		return uomStr;
	}
	
	/**
	 * This method is to detect the order's package type for the delivery note.
	 */
	public Long getPackageCdOfOrderItem(OrderItemEntity orderItem) {
		if (LogonPackageHandlingUtil.isBag(orderItem)) {
			return Constant.PACKAGE_TYPE_BBAG;
		}
		else if (LogonPackageHandlingUtil.isBulkDumped(orderItem)) {
			return Constant.PACKAGE_TYPE_BULK_DUMPED;
		}
		else if (LogonPackageHandlingUtil.isBulkPowder(orderItem)) {
			return Constant.PACKAGE_TYPE_BULK_POWDER;
		}
		else if (LogonPackageHandlingUtil.isService(orderItem)) {
			return Constant.PACKAGE_TYPE_SERVICE;
		}
		else {
			return null;
		}
	}
	
	public String getDeliveryWindow(Locale locale, SapBpEntity sapBpEntity, Date beginDate, Date endDate) {
		String deliveryWindow = null;
		
		if (beginDate != null && endDate != null) {
			TimeZone shipToTZ = getTimeZoneForShipTo(sapBpEntity, locale);

			StringBuilder fromTime = new StringBuilder();
			fromTime.append(shipToTZ.fmtAsHourMinuteWithOffset(beginDate))
					.append(" (")
					.append(shipToTZ.fmtAsDayMonth(beginDate))
					.append(')');

			StringBuilder toTime = new StringBuilder();
			toTime.append(shipToTZ.fmtAsHourMinuteWithOffset(endDate))
				  .append(" (")
				  .append(shipToTZ.fmtAsDayMonth(endDate))
				  .append(')');

			StringBuilder deliveryWindowTime = new StringBuilder();
			deliveryWindowTime.append(fromTime).append(" - ").append(toTime);
			
			deliveryWindow = deliveryWindowTime.toString();
		}
		
		return deliveryWindow;
	}
	
	/**
	 * Detects if the order has return value.
	 * !!!Currently there is only "BAG" and "BULK" type orders, service only orders are not yet existing!!! 
	 * !!!Please update this method when the service only orders are implemented!!!
	 */
	public boolean orderHasReturn(OrderEntity order) {
		for (OrderItemEntity item : order.getOrderItems()) {
			if (item.getReturnedQuantity() != null) {
				if (LogonPackageHandlingUtil.isBag(item)) {
					return true;
				}
				else if (LogonPackageHandlingUtil.isBulkDumped(item)) {
					return true;
				}
				else if (LogonPackageHandlingUtil.isBulkPowder(item)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public String getLatestLoadingDate(List<OrderItemEntity> orderItems, TimeZone deliveryPlantTZ) {
		Date latestLoadingTs = null;
		String latestLoadingDate = null;
		
		for (OrderItemEntity item : orderItems) {
			if (item.getLoad() != null && item.getLoad().getLoadingTs() != null) {
				Date loadingTs = item.getLoad().getLoadingTs();
				if (latestLoadingTs == null || loadingTs.after(latestLoadingTs)) {
					latestLoadingTs = loadingTs;
				}
			}
		}
		
		latestLoadingDate = deliveryPlantTZ.fmtAsDayMonthYearHourMinuteWithOffset(latestLoadingTs);
		
		return latestLoadingDate;
	}

	public TimeZone getTimeZoneForShipTo(SapBpEntity sapBpEntity, Locale locale) {
		ShiptoEntity shipto = dispokarteService.fetchAndInitializeShiptoBySapId(sapBpEntity.getSapId());

		return timezoneService.loadForShipTo(shipto, locale);
	}
}
