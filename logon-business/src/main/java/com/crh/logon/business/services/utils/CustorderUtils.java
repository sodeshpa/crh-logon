/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Deque;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.crh.logon.business.services.beans.UomConversionResult;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.beans.MaterialQuantityUomBean;
import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.business.services.interfaces.DispokarteService;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.utils.validation.ValidationException;
import com.crh.logon.business.services.utils.validation.ValidationErrorReason;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.CustorderError;
import com.crh.logon.converters.Base64ByteArrayConverter;
import com.crh.logon.converters.BigDecimalConverter;
import com.crh.logon.converters.BooleanConverter;
import com.crh.logon.converters.DateConverter;
import com.crh.logon.converters.LongConverter;
import com.crh.logon.converters.StringConverter;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.CustorderItemEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.OrderTruckTypeEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.refdata.ArchivingStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.CustorderStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialDescriptionEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
public class CustorderUtils {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(CustorderUtils.class);
	
	private static final int INTERNAL_SCALE = 16;
	private static final MathContext INTERNAL_MATH_CONTEXT = new MathContext(INTERNAL_SCALE, RoundingMode.HALF_UP);
	
	private static BeanUtilsBean beanUtilsBean;
	private static ConvertUtilsBean convertUtilsBean;
	static {
		beanUtilsBean = new BeanUtilsBean();
		convertUtilsBean = beanUtilsBean.getConvertUtils();
		convertUtilsBean.register(new BigDecimalConverter(), BigDecimal.class);
		convertUtilsBean.register(new LongConverter(), Long.class);
		convertUtilsBean.register(new BooleanConverter(), Boolean.class);
		convertUtilsBean.register(new DateConverter(), Date.class);
		convertUtilsBean.register(new Base64ByteArrayConverter(), byte[].class);
		convertUtilsBean.register(new StringConverter(), String.class);
	}
	
	public static void validateCustorder(final CustorderEntity custorder) {
		// [[
		Object[][] params = {{"custorder", custorder}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "validateCustorder", params);
		}
		// ]]
		try {
			
			if (custorder == null) {
				String errMsg = "input CustorderEntity is null";
				LOG.error(errMsg);
				throw new LogonException(errMsg, Reason.RES_ERROR_IMPL);
			}
			
			// check package type combination (existence of COIs, more than one bulk item, mixed bag/bulk, ...)
			LogonPackageHandlingUtil.checkLogonPackageCombination(custorder, true);
			
			final List<CustorderItemEntity> allCois = custorder.getCustorderItems();
			final List<CustorderItemEntity> materialCois = new ArrayList<CustorderItemEntity>();
			final List<CustorderItemEntity> serviceCois = new ArrayList<CustorderItemEntity>();
			final Set<LogonPackageHandlingUtil.PackageType> packageTypes = CustorderUtils.separateServiceAndMaterialCustorderItems(custorder, materialCois, serviceCois);
			
			if (packageTypes == null || packageTypes.isEmpty()) {
				String errMsg = "incorrect package combination validation";
				LOG.error(errMsg);
				throw new LogonException(errMsg, Reason.RES_ERROR_IMPL);
			}
			
			// obtain package type of customer order
			LogonPackageHandlingUtil.PackageType tmpPackageType = null;
			if (packageTypes.size() == 1 && packageTypes.contains(LogonPackageHandlingUtil.PackageType.SERVICE)) {
				tmpPackageType = LogonPackageHandlingUtil.PackageType.SERVICE;	// service only order (LRQ 070.3 / LOGON-4743)
			}
			else {
				for (LogonPackageHandlingUtil.PackageType pkgType : packageTypes) {
					if (LogonPackageHandlingUtil.PackageType.SERVICE.equals(pkgType)) {
						continue;
					}
					if (tmpPackageType == null) {
						tmpPackageType = pkgType;
					}
					else {
						String errMsg = "incorrect package combination validation";
						LOG.error(errMsg);
						throw new LogonException(errMsg, Reason.RES_ERROR_IMPL);
					}
				}
			}
			
			final LogonPackageHandlingUtil.PackageType packageType = tmpPackageType;
			
			// DELIVERY TYPE
			DeliveryTypeBsEntity deliveryType = custorder.getDeliveryType();
			if (deliveryType == null) {
				if (LogonPackageHandlingUtil.PackageType.SERVICE.equals(packageType)) {
					// delivery type is (or could be) null for service only orders, use a dummy one only for this validation
					deliveryType = new DeliveryTypeBsEntity();
					deliveryType.setDeliveryTypeCd(-1L);
				}
				else {
					LOG.error("custorder deliveryType is null");
					String[] additionalInfo = {};
					throw new ValidationException(new ValidationErrorReason(CustorderError.DELIVERY_TYPE, custorder, additionalInfo));
				}
			}
			
			final long deliveryTypeCd = deliveryType.getDeliveryTypeCd();
			
			// DELIVERY FREQUENCIES (performance orders)
			final boolean isPerformanceOrder;
			if (custorder.getDeliveryFreqEveryTime() != null && custorder.getDeliveryFreqQtyperhre() != null) {
				LOG.error("both deliveryFreqEveryTime and deliveryFreqQtyperhre is set");
				String[] additionalInfo = {};
				throw new ValidationException(new ValidationErrorReason(CustorderError.INVALID_DEL_FQY_SETTINGS_ETIME_QTYPHRE, custorder, additionalInfo));
			}
			else if (custorder.getDeliveryFreqEveryTime() != null || custorder.getDeliveryFreqQtyperhre() != null) {	// it is a performance order
				isPerformanceOrder = true;
				
				// performance order is only for bulk materials
				if (!LogonPackageHandlingUtil.PackageType.BULK_DUMPED.equals(packageType) && !LogonPackageHandlingUtil.PackageType.BULK_POWDER.equals(packageType)) {
					LOG.error("performance order is not supported for this package type: " + packageType.getTypeName());
					String[] additionalInfo = {};
					throw new ValidationException(new ValidationErrorReason(CustorderError.INVALID_DEL_FQY_PACKAGE_TYPE, custorder, additionalInfo));
				}
				
				// performance order is only for road direct, pickup and simple vehicles (ship and block train)
				if (deliveryTypeCd != Constant.DELIVERY_TYPE_ROAD_DIRECT && deliveryTypeCd != Constant.DELIVERY_TYPE_PICKUP
						&& deliveryTypeCd != Constant.DELIVERY_TYPE_SHIP && deliveryTypeCd != Constant.DELIVERY_TYPE_BLOCK_TRAIN) {
					LOG.error("performance order is not supported for this delivery type: " + deliveryTypeCd);
					String[] additionalInfo = {};
					throw new ValidationException(new ValidationErrorReason(CustorderError.INVALID_DEL_FQY_DELIVERY_TYPE, custorder, additionalInfo));
				}
			}
			else {
				isPerformanceOrder = false;
			}
			
			
			// DELIVERY FREQUENCY (deliveryFreqEveryTime)
			if (custorder.getDeliveryFreqEveryTime() != null) {
				final double deliveryFreqEveryTime = custorder.getDeliveryFreqEveryTime();
				if (deliveryFreqEveryTime < Constant.CUSTORDER_DELIVERY_FQY_EVERY_TIME_MIN_VALUE
					|| deliveryFreqEveryTime > Constant.CUSTORDER_DELIVERY_FQY_EVERY_TIME_MAX_VALUE) {
						String[] additionalInfo = { Constant.CUSTORDER_DELIVERY_FQY_EVERY_TIME_MIN_VALUE_STR, Constant.CUSTORDER_DELIVERY_FQY_EVERY_TIME_MAX_VALUE_STR };
						throw new ValidationException(
								new ValidationErrorReason(CustorderError.INVALID_DEL_FQY, custorder, additionalInfo));
				}
				
				final String deliveryFreqEveryTimeUom = custorder.getDeliveryFreqEveryTimeUom();
				if (deliveryFreqEveryTimeUom == null) {
					LOG.error("custorder deliveryFreqEveryTimeUom is null");
					String[] additionalInfo = {};
					throw new ValidationException(new ValidationErrorReason(CustorderError.INVALID_DEL_FQY_UOM_EVERYTIME, custorder, additionalInfo));
				}
				else if (!Constant.UOM_MIN.equals(deliveryFreqEveryTimeUom) && !Constant.UOM_HRE.equals(deliveryFreqEveryTimeUom)) {
					LOG.error("custorder deliveryFreqEveryTimeUom is not allowed: " + deliveryFreqEveryTimeUom);
					String[] additionalInfo = {};
					throw new ValidationException(new ValidationErrorReason(CustorderError.INVALID_DEL_FQY_UOM_EVERYTIME, custorder, additionalInfo));
				}
			}
			
			
			// DELIVERY FREQUENCY (deliveryFreqQtyperhre)
			if (custorder.getDeliveryFreqQtyperhre() != null) {
				final BigDecimal deliveryFreqQtyperhre = custorder.getDeliveryFreqQtyperhre();

				if (Constant.CUSTORDER_DELIVERY_FQY_QTY_PER_HRE_MAX_VALUE.compareTo(deliveryFreqQtyperhre) < 0
					|| Constant.CUSTORDER_DELIVERY_FQY_QTY_PER_HRE_MIN_VALUE.compareTo(deliveryFreqQtyperhre) > 0) {
						String[] additionalInfo = { Constant.CUSTORDER_DELIVERY_FQY_QTY_PER_HRE_MIN_VALUE_STR, Constant.CUSTORDER_DELIVERY_FQY_QTY_PER_HRE_MAX_VALUE_STR };
						throw new ValidationException(
								new ValidationErrorReason(CustorderError.INVALID_DEL_FQY, custorder, additionalInfo));
				}

				final UomBsEntity deliveryFreqQtyperhreUom = custorder.getDeliveryFreqQtyperhreUom();
				if (deliveryFreqQtyperhreUom == null) {
					LOG.error("custorder deliveryFreqQtyperhreUom is null");
					String[] additionalInfo = {};
					throw new ValidationException(new ValidationErrorReason(CustorderError.INVALID_DEL_FQY_UOM_QTYPERHRE, custorder, additionalInfo));
				}
			}
			
			
			// DELIVERY FROM/TO DATE
			final Date deliveryFromDate = custorder.getDeliveryFromDat();
			if (deliveryFromDate == null) {
				LOG.error("custorder deliveryFromDat is null");
				String[] additionalInfo = {};
				throw new ValidationException(new ValidationErrorReason(CustorderError.DELIVERY_FROM_UNKNOWN, custorder, additionalInfo));
			}
			
			final Date deliveryToDate = custorder.getDeliveryToDat();
			
			if (deliveryToDate == null) {
				if (!isPerformanceOrder) {
					LOG.error("custorder deliveryToDate is null (not performance order)");
					String[] additionalInfo = {};
					throw new ValidationException(new ValidationErrorReason(CustorderError.DELIVERY_TO_UNKNOWN, custorder, additionalInfo));
				}
			}
			else {
				if (deliveryFromDate.after(deliveryToDate)) {
					String[] additionalInfo = {};
					throw new ValidationException(new ValidationErrorReason(CustorderError.DELIVERY_TO_BEFORE_DELIVERY_FROM, custorder, additionalInfo));
				}
				
				if (isPerformanceOrder) {
					/* At order creation either the deliveryToDat or the ordered quantity of a performance order is calculated (one of them should be defined, the other one should be null).
					 * Later e.g. at changes via Order Details both parameters are already persisted but changing only one of them
					 * might cause inconsistency.
					 */
					
					boolean isQuantityDefined = false;
					for (CustorderItemEntity coi : allCois) {
						/* LOGON-6133:
						 * Service items are allowed to have an ordered quantity, even in performance
						 * orders.
						 */
						if (!LogonPackageHandlingUtil.isService(coi.getSapMaterial()) && coi.getOrderedQuantity() != null) {
							isQuantityDefined = true;
							break;
						}
					}
					
					if (isQuantityDefined) {	// performance order and both ordered quantity and delivery to date is defined -> might be inconsistent
						if (custorder.getCustorderId() == null || custorder.getCtlCreTs() == null) {	// probably customer order is not yet persisted
							LOG.error("both deliveryToDat and orderedQuantity is defined for a performance custorder");
							String[] additionalInfo = {};
							throw new ValidationException(new ValidationErrorReason(CustorderError.INVALID_DEL_FQY_SETTINGS_DTODAT_OQTY, custorder, additionalInfo));
						}
						LOG.warn("both deliveryToDat and orderedQuantity is defined for a performance custorder, might cause inconsistency during custorder split");
					}
				}
			}
			
			
			// EXACT CUSTORDER QUANTITY FLAG, EXACT SINGLE DELIVERY QUANTITY FLAG and FUZZY ORDERED UoM
			Boolean isExactCustorderQtyFlag = null;
			Boolean isExactSdorderQtyFlag = null;
			for (CustorderItemEntity coi : allCois) {
				final Boolean actualExactCustorderQtyFlag = coi.getExactCustorderQtyFlag();
				final Boolean actualExactSdorderQtyFlag = coi.getExactSdorderQtyFlag();
				
				// exactCustorderQtyFlag is set
				if (actualExactCustorderQtyFlag == null) {
					LOG.error("custorder exactCustorderQtyFlag is not set (null)");
					String[] additionalInfo = {};
					throw new ValidationException(new ValidationErrorReason(CustorderError.INVALID_EXACT_CUSTORDER_QTY_FLAG, custorder, additionalInfo));
				}
				
				// exactSdorderQtyFlag is set
				if (actualExactSdorderQtyFlag == null) {
					LOG.error("custorder exactSdorderQtyFlag is not set (null)");
					String[] additionalInfo = {};
					throw new ValidationException(new ValidationErrorReason(CustorderError.INVALID_EXACT_SDORDER_QTY_FLAG, custorder, additionalInfo));
				}
				
				// exactCustorderQtyFlag has the same value for all custorder items
				if (isExactCustorderQtyFlag == null) {
					isExactCustorderQtyFlag = actualExactCustorderQtyFlag;
				}
				else if (!isExactCustorderQtyFlag.equals(actualExactCustorderQtyFlag)) {
					LOG.error("custorder exactCustorderQtyFlag settings are inconsistent (all custorder item shuld have the same value)");
					String[] additionalInfo = {};
					throw new ValidationException(new ValidationErrorReason(CustorderError.INVALID_EXACT_CUSTORDER_QTY_FLAG_SETT, custorder, additionalInfo));
				}
				
				// exactSdorderQtyFlag has the same value for all custorder items
				if (isExactSdorderQtyFlag == null) {
					isExactSdorderQtyFlag = actualExactSdorderQtyFlag;
				}
				else if (!isExactSdorderQtyFlag.equals(actualExactSdorderQtyFlag)) {
					LOG.error("custorder exactSdorderQtyFlag settings are inconsistent (all custorder item shuld have the same value)");
					String[] additionalInfo = {};
					throw new ValidationException(new ValidationErrorReason(CustorderError.INVALID_EXACT_SDORDER_QTY_FLAG_SETTING, custorder, additionalInfo));
				}
				
				if (LogonPackageHandlingUtil.PackageType.BAG.equals(packageType)) {
					// exactCustorderQtyFlag should be FALSE for bag products
					if (!Boolean.FALSE.equals(actualExactCustorderQtyFlag)) {
						LOG.error("custorder exactCustorderQtyFlag should be FALSE for packageType " + packageType);
						String[] additionalInfo = {};
						throw new ValidationException(new ValidationErrorReason(CustorderError.INVALID_EXACT_CUSTORDER_QTY_FLAG_BAG, custorder, additionalInfo));
					}
					
					// exactSdorderQtyFlag should be TRUE for bag products
					if (!Boolean.TRUE.equals(actualExactSdorderQtyFlag)) {
						LOG.error("custorder exactSdorderQtyFlag should be TRUE for packageType " + packageType);
						String[] additionalInfo = {};
						throw new ValidationException(new ValidationErrorReason(CustorderError.INVALID_EXACT_SDORDER_QTY_FLAG_BAG, custorder, additionalInfo));
					}
				}
				
				// restrictions for fuzzy ordered UoM
				if (coi.getOrderedUom() != null && coi.getOrderedUom().getFuzzyQuantityFlag() != null && coi.getOrderedUom().getFuzzyQuantityFlag().booleanValue()) {
					// exactCustorderQtyFlag should be FALSE if ordered UoM is fuzzy
					if (!Boolean.FALSE.equals(actualExactCustorderQtyFlag)) {
						LOG.error("custorder exactCustorderQtyFlag should be false if ordered UoM is fuzzy");
						String[] additionalInfo = {};
						throw new ValidationException(new ValidationErrorReason(CustorderError.INVALID_EXACT_CUSTORDER_QTY_FLAG_FUZZY, custorder, additionalInfo));
					}
					
					// no fuzzy ordered UoM for simple vehicles
					if (LogonUtil.isSimpleVehicleOrder(deliveryTypeCd)) {
						LOG.error("fuzzy ordered UoM is not allowed for simple vehicles");
						String[] additionalInfo = {};
						throw new ValidationException(new ValidationErrorReason(CustorderError.INVALID_ORDERED_UOM_SIPLVEH_FUZZY, custorder, additionalInfo));
					}
					
					// no fuzzy ordered UoM for bag products, only container
					if (LogonPackageHandlingUtil.PackageType.BAG.equals(packageType) && coi.getOrderedUom() != null && !Constant.UOM_CONTAINER.equalsIgnoreCase(coi.getOrderedUom().getUomCd())) {
						LOG.error("fuzzy ordered UoM is not allowed for bag products");
						String[] additionalInfo = {};
						throw new ValidationException(new ValidationErrorReason(CustorderError.INVALID_ORDERED_UOM_BAG_FUZZY, custorder, additionalInfo));
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
	
	
	public static void processAndValidateCustorderSplitWrapper(CustorderSplitWrapper splitWrapper)
			throws ValidationException {
		// [[
		Object[][] params = {{"splitWrapper", splitWrapper}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "processAndValidateCustorderSplitWrapper", params);
		}
		// ]]
		try {
			if (splitWrapper == null) {
				String errMsg = "splitWrapper is null";
				LOG.error(errMsg);
				throw new LogonException(errMsg, Reason.RES_ERROR_IMPL);
			}
			
			CustorderEntity custorder = splitWrapper.getCustorder();
			
			LogonPackageHandlingUtil.checkLogonPackageCombination(custorder, true);
			
			LogonPackageHandlingUtil.PackageType packageType = splitWrapper.getPackageType();
			if (LogonPackageHandlingUtil.PackageType.BULK_POWDER.equals(packageType) ||
					LogonPackageHandlingUtil.PackageType.BULK_DUMPED.equals(packageType)) {
				if (custorder.getDeliveryFreqEveryTime() != null || custorder.getDeliveryFreqQtyperhre() != null) {
					CustorderUtils.processAndValidatePerformanceBulkCustorderSplitWrapper(splitWrapper);
				}
				else {
					CustorderUtils.processAndValidateNormalBulkCustorderSplitWrapper(splitWrapper);
				}
			}
			else if (LogonPackageHandlingUtil.PackageType.BAG.equals(packageType)) {
				if (splitWrapper.isPalletOnlyOrder() && LogonUtil.isReturnPickupOrder(splitWrapper.getDeliveryTypeCd())) {
					CustorderUtils.processAndValidateReturnPickupPalletCustorderSplitWrapper(splitWrapper);
				}
				else {
					CustorderUtils.processAndValidateBagCustorderSplitWrapper(splitWrapper);
				}
			}
			else if (LogonPackageHandlingUtil.PackageType.SERVICE.equals(packageType)) {
				CustorderUtils.processAndValidateServiceOnlyCustorderSplitWrapper(splitWrapper);
			}
			else {
				String errMsg = "invalid packaging type";
				LOG.error(errMsg);
				throw new LogonException(errMsg, Reason.RES_ERROR_IMPL);
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
	
	public static void splitCustorder(CustorderSplitWrapper splitWrapper) {
		// [[
		Object[][] params = {{"splitWrapper", splitWrapper}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "splitCustorder", params);
		}
		// ]]
		try {
			CustorderEntity custorder = splitWrapper.getCustorder();
			
			LogonPackageHandlingUtil.PackageType packageType = splitWrapper.getPackageType();
			if (LogonPackageHandlingUtil.PackageType.BULK_POWDER.equals(packageType) ||
					LogonPackageHandlingUtil.PackageType.BULK_DUMPED.equals(packageType)) {
				if (custorder.getDeliveryFreqEveryTime() != null || custorder.getDeliveryFreqQtyperhre() != null) {
					CustorderUtils.processAndValidatePerformanceBulkCustorderSplitWrapper(splitWrapper);
					CustorderUtils.splitPerformanceBulkCustorder(splitWrapper);
				}
				else {
					CustorderUtils.processAndValidateNormalBulkCustorderSplitWrapper(splitWrapper);
					CustorderUtils.splitNormalBulkCustorder(splitWrapper);
				}
			}
			else if (LogonPackageHandlingUtil.PackageType.BAG.equals(packageType)) {
				if (splitWrapper.isPalletOnlyOrder() && LogonUtil.isReturnPickupOrder(splitWrapper.getDeliveryTypeCd())) {
					CustorderUtils.processAndValidateReturnPickupPalletCustorderSplitWrapper(splitWrapper);
					CustorderUtils.splitReturnPickupPalletCustorder(splitWrapper);
				}
				else {
					CustorderUtils.processAndValidateBagCustorderSplitWrapper(splitWrapper);
					CustorderUtils.splitBagCustorder(splitWrapper);
				}
			}
			else if (LogonPackageHandlingUtil.PackageType.SERVICE.equals(packageType)) {
				CustorderUtils.processAndValidateServiceOnlyCustorderSplitWrapper(splitWrapper);
				CustorderUtils.splitServiceOnlyCustorder(splitWrapper);
			}
			else {
				String errMsg = "invalid packaging type";
				LOG.error(errMsg);
				throw new LogonException(errMsg, Reason.RES_ERROR_IMPL);
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
	
	
	private static void processAndValidateServiceOnlyCustorderSplitWrapper(CustorderSplitWrapper splitWrapper) {
		// [[
		Object[][] params = {{"splitWrapper", splitWrapper}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "processAndValidateServiceOnlyCustorderSplitWrapper", params);
		}
		// ]]
		try {
			CustorderEntity custorder = splitWrapper.getCustorder();
			
			// deliveryTo and From date should be the same
			custorder.setDeliveryFromDat(custorder.getDeliveryToDat());
			
			splitWrapper.set_numOfDeliveries(1);
			splitWrapper.set_numOfDeliveriesStr("" + 1);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	protected static void splitServiceOnlyCustorder(CustorderSplitWrapper splitWrapper) {
		// [[
		Object[][] params = {{"splitWrapper", splitWrapper}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "splitServiceOnlyCustorder", params);
		}
		// ]]
		try {
			CustorderEntity custorder = splitWrapper.getCustorder();
			List<OrderEntity> orders = new ArrayList<OrderEntity>();
			custorder.setOrders(orders);
			
			final CustorderStatusBsEntity custorderStatusOpen = splitWrapper.getCustrderStatusEntity(Constant.CUSTORDER_STATUS_FINISHED);
			custorder.setCustorderStatus(custorderStatusOpen);
			
			// service only orders will have only one SDO and order status will be set to delivered immediately
			final OrderEntity sdo = new OrderEntity();
			sdo.setArchivedFlag(false);
			initSdo(sdo, splitWrapper.getSdoTemplate(), custorder, splitWrapper);
			final OrderStatusBsEntity orderStatusDelivered = splitWrapper.getOrderStatusEntity(Constant.ORDER_STATUS_DELIVERED);
			sdo.setOrderStatus(orderStatusDelivered);
			final ArchivingStatusBsEntity archivingStatusBsEntity = splitWrapper.getArchivingStatusEntity(Constant.ARCHIVING_STATUS_NEW);
			sdo.setArchivingStatus(archivingStatusBsEntity);
			sdo.setActualDeliveryDat(sdo.getDeliveryToDat());
			
			// create and initialize order items
			final List<CustorderItemEntity> serviceMaterials = splitWrapper.getServiceCois();
			for (CustorderItemEntity coi : serviceMaterials) {
				OrderItemEntity sdoi = new OrderItemEntity();
				initLoadedSdoi(splitWrapper, sdoi, splitWrapper.getSdoiTemplate(), sdo, coi);
				
				// since service only orders will be set to delivered immediately we have to update the delivered UoM and quantity
				sdoi.setDeliveredUom(sdoi.getOrderedUom());
				sdoi.setDeliveredQuantity(sdoi.getOrderedQuantity());
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
	private static void processAndValidateReturnPickupPalletCustorderSplitWrapper(CustorderSplitWrapper splitWrapper) {
		// [[
		Object[][] params = {{"splitWrapper", splitWrapper}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "processAndValidateReturnPickupPalletCustorderSplitWrapper", params);
		}
		// ]]
		try {
			// TODO (08052013/lajos): adapt logic for return delivered case, at the moment only return pickup is supported (LRQ 018.2 / LOGON-6761)
			
			CustorderEntity custorder = splitWrapper.getCustorder();
			
			// deliveryTo and From date should be the same
			custorder.setDeliveryFromDat(custorder.getDeliveryToDat());
			
			splitWrapper.set_numOfDeliveries(1);
			splitWrapper.set_numOfDeliveriesStr("" + 1);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	protected static void splitReturnPickupPalletCustorder(CustorderSplitWrapper splitWrapper) {
		// [[
		Object[][] params = {{"splitWrapper", splitWrapper}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "splitReturnPickupPalletCustorder", params);
		}
		// ]]
		try {
			// TODO (08052013/lajos): adapt logic for return delivered case, at the moment only return pickup is supported (LRQ 018.2 / LOGON-6761)
			
			CustorderEntity custorder = splitWrapper.getCustorder();
			List<OrderEntity> orders = new ArrayList<OrderEntity>();
			custorder.setOrders(orders);
			
			final CustorderStatusBsEntity custorderStatusOpen = splitWrapper.getCustrderStatusEntity(Constant.CUSTORDER_STATUS_FINISHED);
			custorder.setCustorderStatus(custorderStatusOpen);
			
			// pickup return pallet orders will have only one SDO and order status will be set to delivered immediately
			final OrderEntity sdo = new OrderEntity();
			sdo.setArchivedFlag(false);
			initSdo(sdo, splitWrapper.getSdoTemplate(), custorder, splitWrapper);
			final OrderStatusBsEntity orderStatusDelivered = splitWrapper.getOrderStatusEntity(Constant.ORDER_STATUS_DELIVERED);
			sdo.setOrderStatus(orderStatusDelivered);
			final ArchivingStatusBsEntity archivingStatusBsEntity = splitWrapper.getArchivingStatusEntity(Constant.ARCHIVING_STATUS_NEW);
			sdo.setArchivingStatus(archivingStatusBsEntity);
			sdo.setActualDeliveryDat(sdo.getDeliveryToDat());
			
			// create and initialize pallet order items
			final List<CustorderItemEntity> palletMaterials = splitWrapper.getMaterialCois();
			for (CustorderItemEntity coi : palletMaterials) {
				OrderItemEntity sdoi = new OrderItemEntity();
				initLoadedSdoi(splitWrapper, sdoi, splitWrapper.getSdoiTemplate(), sdo, coi);
				
				// since pallet only orders will be set to delivered immediately we have to update the delivered UoM and quantity
				sdoi.setDeliveredUom(sdoi.getOrderedUom());
				sdoi.setDeliveredQuantity(sdoi.getOrderedQuantity());
			}
			
			// create and initialize service order items
			final List<CustorderItemEntity> serviceMaterials = splitWrapper.getServiceCois();
			if (serviceMaterials != null && !serviceMaterials.isEmpty()) {
				for (CustorderItemEntity coi : serviceMaterials) {
					OrderItemEntity sdoi = new OrderItemEntity();
					initLoadedSdoi(splitWrapper, sdoi, splitWrapper.getSdoiTemplate(), sdo, coi);
					
					// since pallet only orders will be set to delivered immediately we have to update the delivered UoM and quantity
					sdoi.setDeliveredUom(sdoi.getOrderedUom());
					sdoi.setDeliveredQuantity(sdoi.getOrderedQuantity());
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
	
	private static void processAndValidateBagCustorderSplitWrapper(CustorderSplitWrapper splitWrapper) {
		// [[
		Object[][] params = {{"splitWrapper", splitWrapper}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "processAndValidateBagCustorderSplitWrapper", params);
		}
		// ]]
		try {
			List<CustorderItemEntity> bagMaterials = splitWrapper.getMaterialCois();
			
			List<VehicleMaterialPlanner> vehicleMaterialPlanners = new ArrayList<VehicleMaterialPlanner>();
			VehicleMaterialPlanner actVehicleMaterialPlanner = null;
			BigDecimal vehicleRemainingCapacityInOrderedUom = null;
			//if not exact qty is given (bag or pallet), then remaining capacity depends on the first item
			for (CustorderItemEntity coi : bagMaterials) {
				SapMaterialEntity sapMaterial = coi.getSapMaterial();
				String orderedUomCd = coi.getOrderedUom().getUomCd();
				BigDecimal remainingQtyInOrderedUom = BigDecimal.ZERO.add(coi.getOrderedQuantity());
				
				// init new vehicle material planner for the first CustorderItem
				if (actVehicleMaterialPlanner == null && remainingQtyInOrderedUom.compareTo(BigDecimal.ZERO) > 0) {
					actVehicleMaterialPlanner = new VehicleMaterialPlanner(splitWrapper);
					vehicleRemainingCapacityInOrderedUom = actVehicleMaterialPlanner.getRemainingCapacity(sapMaterial, orderedUomCd);
					vehicleMaterialPlanners.add(actVehicleMaterialPlanner);
				}
				
				while (remainingQtyInOrderedUom.compareTo(BigDecimal.ZERO) > 0) {
					vehicleRemainingCapacityInOrderedUom = actVehicleMaterialPlanner.getRemainingCapacity(sapMaterial, orderedUomCd);
					if (!isThereRemainingCapacity(orderedUomCd, vehicleRemainingCapacityInOrderedUom)) {
						// no integral amount in ordered UoM fits into the vehicle => create new
						actVehicleMaterialPlanner = new VehicleMaterialPlanner(splitWrapper);
						vehicleRemainingCapacityInOrderedUom = actVehicleMaterialPlanner.getRemainingCapacity(sapMaterial, orderedUomCd);
						vehicleMaterialPlanners.add(actVehicleMaterialPlanner);
						
						if (!isThereRemainingCapacity(orderedUomCd, vehicleRemainingCapacityInOrderedUom)) {
							// one ordered UoM does not fit into an empty vehicle ?!?
							String errMsg = "vehicle capacity is too small for this ordered UoM";
							LOG.error(errMsg);
							throw new LogonException(errMsg, Reason.RES_ERROR_IMPL);
						}
					}
					
					BigDecimal integralVehicleRemainingCapacityInOrderedUom = roundRemainingCapacityInOrderedUom(orderedUomCd, vehicleRemainingCapacityInOrderedUom);
					if (integralVehicleRemainingCapacityInOrderedUom.compareTo(remainingQtyInOrderedUom) >= 0) {
						actVehicleMaterialPlanner.addMaterial(sapMaterial, remainingQtyInOrderedUom.stripTrailingZeros(), orderedUomCd);
						remainingQtyInOrderedUom = BigDecimal.ZERO;
					}
					else {
						actVehicleMaterialPlanner.addMaterial(sapMaterial, integralVehicleRemainingCapacityInOrderedUom.stripTrailingZeros(), orderedUomCd);
						remainingQtyInOrderedUom = remainingQtyInOrderedUom.subtract(integralVehicleRemainingCapacityInOrderedUom);
					}
				}
			}
			
			int numOfDeliveries = vehicleMaterialPlanners.size();
			
			splitWrapper.set_vehicleMaterialPlanners(vehicleMaterialPlanners);
			splitWrapper.set_numOfDeliveries(numOfDeliveries);
			splitWrapper.set_numOfDeliveriesStr("" + numOfDeliveries);
			
			if (numOfDeliveries > splitWrapper.getNumOfMaxOrders()) {
				String errMsg = "too many orders: " + numOfDeliveries;
				LOG.error(errMsg);
				String[] additionalInfo = { ""+splitWrapper.getNumOfMaxOrders(), ""+numOfDeliveries };
				throw new ValidationException(new ValidationErrorReason(CustorderError.MAX_NUMBER_OF_ORDERS, splitWrapper.getCustorder(), additionalInfo));
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
	
	private static boolean isThereRemainingCapacity(String orderedUomCd, BigDecimal vehicleRemainingCapacityInOrderedUom) {
		// [[
		boolean result = false;
		Object[][] params = {{"orderedUomCd", orderedUomCd},
							{"vehicleRemainingCapacityInOrderedUom", vehicleRemainingCapacityInOrderedUom}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "isThereRemainingCapacity", params);
		}
		// ]]
		try {
			BigDecimal integralVehicleRemainingCapacityInOrderedUom = roundRemainingCapacityInOrderedUom(
					orderedUomCd, vehicleRemainingCapacityInOrderedUom);
			BigDecimal minQtyToAdd;
			if (Constant.UOM_BAG.equals(orderedUomCd) || Constant.UOM_PAL.equals(orderedUomCd)) {
				minQtyToAdd = BigDecimal.ONE;
			}
			else {
				minQtyToAdd = BigDecimal.ZERO;
			}
			
			return result = !(integralVehicleRemainingCapacityInOrderedUom.compareTo(BigDecimal.ZERO) == 0
				|| integralVehicleRemainingCapacityInOrderedUom.compareTo(minQtyToAdd) < 0);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private static BigDecimal roundRemainingCapacityInOrderedUom(String orderedUomCd, 
			BigDecimal vehicleRemainingCapacityInOrderedUom) {
		// [[
		BigDecimal result = null;
		Object[][] params = {{"orderedUomCd", orderedUomCd},
							{"vehicleRemainingCapacityInOrderedUom", vehicleRemainingCapacityInOrderedUom}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "roundRemainingCapacityInOrderedUom", params);
		}
		// ]]
		try {
			if (Constant.UOM_BAG.equals(orderedUomCd) || Constant.UOM_PAL.equals(orderedUomCd)) {
				result = vehicleRemainingCapacityInOrderedUom.
						divideToIntegralValue(BigDecimal.ONE, INTERNAL_MATH_CONTEXT);
			}
			else {
				result = vehicleRemainingCapacityInOrderedUom;
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
	
	protected static void splitBagCustorder(CustorderSplitWrapper splitWrapper) {
		// [[
		Object[][] params = {{"splitWrapper", splitWrapper}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "splitBagCustorder", params);
		}
		// ]]
		try {
			// get the CO and _THE_ bulk COI
			CustorderEntity custorder = splitWrapper.getCustorder();
			List<OrderEntity> orders = new ArrayList<OrderEntity>();
			custorder.setOrders(orders);
			
			final List<CustorderItemEntity> materialCois = splitWrapper.getMaterialCois();
			final Map<Long, CustorderItemEntity> sapMaterialIdMaterialCoiMap = new HashMap<Long, CustorderItemEntity>();
			for (CustorderItemEntity coi : materialCois) {
				sapMaterialIdMaterialCoiMap.put(coi.getSapMaterial().getSapMaterialId(), coi);
			}
			
			final List<CustorderItemEntity> serviceCois = splitWrapper.getServiceCois();
			
			List<VehicleMaterialPlanner> vehicleMaterialPlanners = splitWrapper.get_vehicleMaterialPlanners();
			if (vehicleMaterialPlanners.size() > splitWrapper.getNumOfMaxOrders()) {
				String errMsg = "too many orders: " + vehicleMaterialPlanners.size();
				LOG.error(errMsg);
				String[] additionalInfo = { ""+splitWrapper.getNumOfMaxOrders(), ""+vehicleMaterialPlanners.size() };
				throw new ValidationException(new ValidationErrorReason(CustorderError.MAX_NUMBER_OF_ORDERS, custorder, additionalInfo));
			}
			
			final ArchivingStatusBsEntity archivingStatusBsEntity = splitWrapper.getArchivingStatusEntity(Constant.ARCHIVING_STATUS_NEW);
			for (VehicleMaterialPlanner vehicleMaterialPlanner : vehicleMaterialPlanners) {
				OrderEntity sdo = new OrderEntity();
				sdo.setArchivedFlag(false);
				sdo.setArchivingStatus(archivingStatusBsEntity);
				initSdo(sdo, splitWrapper.getSdoTemplate(), custorder, splitWrapper);
				
				// init and add real material SDOIs
				final List<MaterialQuantityUomBean> addedMaterialQuantitUomCds = vehicleMaterialPlanner.getAddedMaterialQuantityUomCds();
				for (MaterialQuantityUomBean addedMaterialQuantitUomCd : addedMaterialQuantitUomCds) {
					CustorderItemEntity coi = sapMaterialIdMaterialCoiMap.get(addedMaterialQuantitUomCd.getSapMaterialId());
					OrderItemEntity sdoi = new OrderItemEntity();
					initSdoi(sdoi, splitWrapper.getSdoiTemplate(), sdo, coi);
				
					sdoi.setOrderedQuantity(addedMaterialQuantitUomCd.getQuantity());
					UomBsEntity orderedUom = splitWrapper.getUomBsEntityFromCache(addedMaterialQuantitUomCd.getUomCd());
					sdoi.setOrderedUom(orderedUom);
				}
				
				// init and add service SDOIs
				if (serviceCois != null && !serviceCois.isEmpty()) {
					for (CustorderItemEntity serviceCoi : serviceCois) {
						OrderItemEntity sdoi = new OrderItemEntity();
						initLoadedSdoi(splitWrapper, sdoi, splitWrapper.getSdoiTemplate(), sdo, serviceCoi);
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
	
	private static void processAndValidateNormalBulkCustorderSplitWrapper(
			CustorderSplitWrapper splitWrapper) {
		// [[
		Object[][] params = {{"splitWrapper", splitWrapper}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "processAndValidateNormalBulkCustorderSplitWrapper", params);
		}
		// ]]
		try {
			// CO: Customer Order, COI: Customer Order Item, SDO: Single Delivery Order (OrderEntity), SDOI: Single Delivery Order Item (OrderItemEntity)
			
			// get the CO, _THE_ bulk COI and SAP material
			final CustorderEntity custorder = splitWrapper.getCustorder();
			final CustorderItemEntity bulkCustorderItem = CustorderUtils.getSingleBulkCustorderItem(custorder);
			final SapMaterialEntity sapMaterial = bulkCustorderItem.getSapMaterial(); 
			
			// ordered bulk quantity and UoM
			final BigDecimal orderedBulkQuantity = bulkCustorderItem.getOrderedQuantity();
			final UomBsEntity orderedUom = bulkCustorderItem.getOrderedUom();
			
			final long deliveryTypeCd = custorder.getDeliveryType().getDeliveryTypeCd();
			
			// per SDO quantity in ordered UoM, full vehicle UoM, max load of vehicle
			final BigDecimal perSdoQtyInOrderedUom;
			final BigDecimal vehicleMaxLoadInOrderedUom;
			if (isDeliveryTypeOnlyMultiorderSplit(deliveryTypeCd)) {
				if (isUomMultiorder(orderedUom)) {
					perSdoQtyInOrderedUom = splitWrapper.getVehicleMaxCapacityInUomCd(sapMaterial, orderedUom.getUomCd());
					vehicleMaxLoadInOrderedUom = perSdoQtyInOrderedUom;
				}
				else { 
					perSdoQtyInOrderedUom = orderedBulkQuantity;
					vehicleMaxLoadInOrderedUom = orderedBulkQuantity;
				}
			}
			else if (isUomMultiorder(orderedUom)) {
				perSdoQtyInOrderedUom = splitWrapper.getVehicleMaxCapacityInUomCd(sapMaterial, orderedUom.getUomCd());
				vehicleMaxLoadInOrderedUom = perSdoQtyInOrderedUom;
			}
			else if (bulkCustorderItem.getPerSdorderQuantity() != null) {
				perSdoQtyInOrderedUom = splitWrapper.convertQuantity(sapMaterial, bulkCustorderItem.getPerSdorderUom().getUomCd()
						, orderedUom.getUomCd(), bulkCustorderItem.getPerSdorderQuantity()).getQuantity();
				vehicleMaxLoadInOrderedUom = perSdoQtyInOrderedUom;
			}
			else {
				vehicleMaxLoadInOrderedUom = splitWrapper.getVehicleMaxCapacityInUomCd(sapMaterial, orderedUom.getUomCd());
				perSdoQtyInOrderedUom = vehicleMaxLoadInOrderedUom;
			}
			
			final UomBsEntity fullVehicleUom;
			if (isUomMultiorder(orderedUom)) {
				fullVehicleUom = orderedUom;
			}
			else {
				fullVehicleUom = splitWrapper.getFullVehicleUom();
			}
			
			// check mandatory parameters
			if (orderedBulkQuantity == null || Constant.CUSTORDER_ORDERED_QTY_MAX_VALUE.compareTo(orderedBulkQuantity) < 0
				|| Constant.CUSTORDER_ORDERED_QTY_MIN_VALUE.compareTo(orderedBulkQuantity) > 0) {
					StringBuilder errMsg = new StringBuilder("invalid orderedBulkQuantity");
					errMsg.append(" orderedBulkQuantity: ").append(orderedBulkQuantity == null ? null : orderedBulkQuantity.toString());
					LOG.error(errMsg.toString());
					String[] additionalInfo = { Constant.CUSTORDER_ORDERED_QTY_MIN_VALUE_STR, Constant.CUSTORDER_ORDERED_QTY_MAX_VALUE_STR };					
					throw new ValidationException(new ValidationErrorReason(CustorderError.INVALID_ORDERED_QUANTITY, bulkCustorderItem, additionalInfo));
			}
			else if (perSdoQtyInOrderedUom == null || vehicleMaxLoadInOrderedUom == null
				|| Constant.CUSTORDER_QTY_PER_SDO_MAX_VALUE.compareTo(perSdoQtyInOrderedUom) < 0
				|| Constant.CUSTORDER_QTY_PER_SDO_MIN_VALUE.compareTo(perSdoQtyInOrderedUom) > 0
				|| Constant.CUSTORDER_QTY_PER_SDO_MAX_VALUE.compareTo(vehicleMaxLoadInOrderedUom) < 0
				|| Constant.CUSTORDER_QTY_PER_SDO_MIN_VALUE.compareTo(vehicleMaxLoadInOrderedUom) > 0) {
					StringBuilder errMsg = new StringBuilder("invalid perSdoQtyInOrderedUom and/or vehicleMaxLoadInOrderedUom");
					errMsg.append(" perSdoQtyInOrderedUom: ").append(perSdoQtyInOrderedUom == null ? null : perSdoQtyInOrderedUom.toString());
					errMsg.append(" vehicleMaxLoadInOrderedUom: ").append(vehicleMaxLoadInOrderedUom == null ? null : vehicleMaxLoadInOrderedUom.toString());
					LOG.error(errMsg.toString());
					String[] additionalInfo = { Constant.CUSTORDER_QTY_PER_SDO_MIN_VALUE_STR, Constant.CUSTORDER_QTY_PER_SDO_MAX_VALUE_STR };
					throw new ValidationException(new ValidationErrorReason(CustorderError.INVALID_QUANTITY_PER_DELIVERY, bulkCustorderItem, additionalInfo));
			}
			
			// initialize remaining quantity
			BigDecimal remainingQtyInOrderedUom = BigDecimal.ZERO.add(orderedBulkQuantity);
			
			// check number of expected deliveries
			int numOfDeliveries;
			String numOfDeliveriesStr;
			if (bulkCustorderItem.getExactCustorderQtyFlag() || bulkCustorderItem.getExactSdorderQtyFlag()) {
				BigDecimal[] divAndRem = remainingQtyInOrderedUom.divideAndRemainder(perSdoQtyInOrderedUom, INTERNAL_MATH_CONTEXT);
				if (divAndRem[1].compareTo(BigDecimal.ZERO) == 0) {	// no remainder
					numOfDeliveries = divAndRem[0].intValue();
					BigDecimal rem = perSdoQtyInOrderedUom.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
					numOfDeliveriesStr = ""+(numOfDeliveries - 1) + " +1 ("+rem.toPlainString()+" "+orderedUom.getUomCd() +")";
				}
				else {
					numOfDeliveries = divAndRem[0].intValue();
					BigDecimal rem = divAndRem[1].divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
					numOfDeliveriesStr = ""+numOfDeliveries + " +1 ("+rem.toPlainString() +" "+orderedUom.getUomCd() +")";
					++numOfDeliveries;	// reminder delivery
				}
			}
			else {
				BigDecimal num = remainingQtyInOrderedUom.divide(vehicleMaxLoadInOrderedUom, 0, BigDecimal.ROUND_CEILING).stripTrailingZeros();
				numOfDeliveries = num.intValue();
				numOfDeliveriesStr = "" + numOfDeliveries;
			}
			
			Date deliveryToDate = custorder.getDeliveryToDat();
			
			// store processed and validated values
			splitWrapper.set_orderedUom(orderedUom);
			splitWrapper.set_remainingQtyInOrderedUom(remainingQtyInOrderedUom);
			splitWrapper.set_perSdoQtyInOrderedUom(perSdoQtyInOrderedUom);
			splitWrapper.set_vehicleMaxLoadInOrderedUom(vehicleMaxLoadInOrderedUom);
			splitWrapper.set_fullVehicleUom(fullVehicleUom);
			splitWrapper.set_deliveryToDate(deliveryToDate);
			splitWrapper.set_numOfDeliveries(numOfDeliveries);
			splitWrapper.set_numOfDeliveriesStr(numOfDeliveriesStr);
			
			if (numOfDeliveries > splitWrapper.getNumOfMaxOrders()) {
				String errMsg = "too many orders: " + numOfDeliveries;
				LOG.error(errMsg);
				String[] additionalInfo = { ""+splitWrapper.getNumOfMaxOrders(), ""+numOfDeliveries };
				throw new ValidationException(new ValidationErrorReason(CustorderError.MAX_NUMBER_OF_ORDERS, custorder, additionalInfo));
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
	
	protected static void splitNormalBulkCustorder(CustorderSplitWrapper splitWrapper) {
		// [[
		Object[][] params = {{"splitWrapper", splitWrapper}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "splitNormalBulkCustorder", params);
		}
		// ]]
		try {
			splitBulkCustorder(splitWrapper, false);
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
	 * Creates new order/orderItem and/or modifies the existing orders belonging to the customer order, 
	 * in order to get the sum of the order quantities increased upto the customer order's ordered 
	 * quantity. Before modifying an order, there is a check if it's still modifyable.
	 * 
	 */
	private static void createAdjustOrders(CustorderSplitWrapper splitWrapper, BigDecimal remainingQtyInOrderedUom, CustorderItemEntity bulkCustorderItem, boolean performance) {
		// [[
		Object[][] params = {{"splitWrapper", splitWrapper},
				{"remainingQtyInOrderedUom", remainingQtyInOrderedUom},
				{"bulkCustorderItem", bulkCustorderItem},
				{"performance", performance}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "createAdjustOrders", params);
		}
		// ]]
		try {
			CustorderEntity custorder = splitWrapper.getCustorder();
			ArrayList<OrderItemEntity> adjustableOrders = makePriorityGroupsOfOrders(splitWrapper);
			OrderItemEntity bulkOrderItem = null;
			long deliveryTimeToleranceInMillisec = deliveryTimeToleranceInMillis(custorder);
			UomBsEntity orderedUom = bulkCustorderItem.getOrderedUom();
			BigDecimal remainingInTons = splitWrapper.convertQuantity(bulkCustorderItem.getSapMaterial(), 
					orderedUom.getUomCd(), Constant.UOM_TONS, remainingQtyInOrderedUom).getQuantity();
			
			boolean useTolerance = checkCustorderTolerance(splitWrapper.getCustorder());
			BigDecimal toleranceInTons = BigDecimal.ZERO;
			if (useTolerance) {
				toleranceInTons = getWeightToleranceInTons(splitWrapper);
			}
			
			// create SDOs while there is remaining quantity from ordered quantity
			int numOfLoops = 0;
			while (remainingInTons.compareTo(toleranceInTons) > 0) {
				
				// last line of defense against infinite loops
				++numOfLoops;
				if (numOfLoops > splitWrapper.getNumOfMaxOrders()) {
					String errMsg = "too many orders, limit reached: " + numOfLoops;
					LOG.error(errMsg);
					String[] additionalInfo = { ""+splitWrapper.getNumOfMaxOrders(), ""+numOfLoops };	
					throw new ValidationException(new ValidationErrorReason(CustorderError.MAX_NUMBER_OF_ORDERS, custorder, additionalInfo));
				}
				
				if (adjustableOrders != null && !adjustableOrders.isEmpty()) {
					bulkOrderItem = adjustableOrders.remove(0);//get highest (1st) priority to modify
				}
				else {
					bulkOrderItem = null;
				}
				
				remainingQtyInOrderedUom = createOrAdjustOrder(splitWrapper, bulkOrderItem, bulkCustorderItem, 
						remainingQtyInOrderedUom, performance);
				remainingInTons = splitWrapper.convertQuantity(bulkCustorderItem.getSapMaterial(), 
						orderedUom.getUomCd(), Constant.UOM_TONS, remainingQtyInOrderedUom).getQuantity();
			}
			if (performance) {
				completeCustOrderFields(splitWrapper, bulkCustorderItem, deliveryTimeToleranceInMillisec);
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
	 * Check if a tolerance can be used for the given <code>custorder</code>.
	 * 
	 * A tolerance can be used for custorder if:
	 * 1.) Is already persisted (the custorder is not from Order Entry)
	 * 2.) All custorder items have the flag TLGNC_CUSTORDER_ITEM.EXACT_CUSTORDER_QTY_FLAG set.
	 * 
	 * 
	 * @param custorder
	 * 			The {@link CustorderEntity} to check. Nullable
	 * @return
	 * 			<code>true</code> if tolerance can be usedfor the given <code>custorder</code>, 
	 * 			otherwise <code>false</code>
	 * 
	 * @throws LogonException if the custorder is inconsistent
	 */
	private static boolean checkCustorderTolerance(CustorderEntity custorder) {
		// [[
		boolean result = false;
		Object[][] params = {{"custorder", custorder}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "checkCustorderTolerance", params);
		}
		// ]]
		try {
			// RULE 1: the custorder is persisted
			if (!isCustorderPersisted(custorder)) {
				return result = false;
			}
			
			// RULE 2: all custorder items have the flag TLGNC_CUSTORDER_ITEM.EXACT_CUSTORDER_QTY_FLAG (they must have the same value)
			int nrOfFlags = 0;
			List<CustorderItemEntity> custorderItemsList = custorder.getCustorderItems();
			for (Iterator<CustorderItemEntity> iter = custorderItemsList.iterator(); iter.hasNext();) {
				CustorderItemEntity custorderItemEntity = iter.next();
				
				nrOfFlags = custorderItemEntity.getExactCustorderQtyFlag() ? ++ nrOfFlags : nrOfFlags;
			}
			
			if (nrOfFlags != 0 && nrOfFlags != custorderItemsList.size()) { 
				// the custorder is inconsistent
				String errorMsg = "The custorder items for custorder with id: '" + custorder.getCustorderId() + "' have different TLGNC_CUSTORDER_ITEM.EXACT_CUSTORDER_QTY_FLAG set!";
				LOG.error(errorMsg);
				
				throw new LogonException(errorMsg, Reason.RES_ERROR_CUSTORDER_ITEM_INCOSISTENCY);
			}
			
			return result = (nrOfFlags > 0);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private static boolean isCustorderPersisted(CustorderEntity custorder) {
		// [[
		boolean result = true;
		Object[][] params = {{"custorder", custorder}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "isCustorderPersisted", params);
		}
		// ]]
		try {
			if (custorder == null) {
				LOG.warn("The given custorder entity is <NULL>");
				
				return result = false;
			}
			
			List<CustorderItemEntity> custorderItemsList = custorder.getCustorderItems();
			if (custorderItemsList == null) {
				LOG.warn("The given custorder entity with id: '" + custorder.getCustorderId() +  "', has no custorder items!");
				
				return result = false;
			}
			
			if (custorder.getCustorderId() == null) { // RULE 1: the custorder is persisted
				return result = false;
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
	
	private static BigDecimal getWeightToleranceInTons(CustorderSplitWrapper splitWrapper) {
		// [[
		BigDecimal result = null;
		Object[][] params = {{"splitWrapper", splitWrapper}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "getWeightToleranceInTons", params);
		}
		// ]]
		try {
			CustorderEntity custorder = splitWrapper.getCustorder();
			BigDecimal toleranceInKg = null;
			SareaEntity salesArea = custorder.getSarea();
			if (salesArea == null || salesArea.getExactQuantityOrderTolerance() == null) {
				Integer appDefaultSareaExactQuantityOrderTolerance = splitWrapper.getDefaultWeightTolerance();
				toleranceInKg = new BigDecimal(appDefaultSareaExactQuantityOrderTolerance);
			}
			else {
				toleranceInKg = new BigDecimal(salesArea.getExactQuantityOrderTolerance());
			}
			return result = toleranceInKg.divide(new BigDecimal(1000), Constant.SCALE, BigDecimal.ROUND_HALF_EVEN);
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
	 * Creates a new order/orderItem or modifies the one from the input. The extra quantity will be
	 * assigned to this order. 
	 * 
	 * @return
	 * The quantity which is still remaining according to the quantity of the customer order 
	 * and the sum of the order quantities.
	 * 
	 */
	private static BigDecimal createOrAdjustOrder(CustorderSplitWrapper splitWrapper, OrderItemEntity bulkOrderItem, CustorderItemEntity bulkCustorderItem, 
			BigDecimal remainingQtyInOrderedUom, boolean performance) {
		// [[
		Object[][] params = {{"splitWrapper", splitWrapper},
				{"bulkOrderItem", bulkOrderItem},
				{"bulkCustorderItem", bulkCustorderItem},
				{"remainingQtyInOrderedUom", remainingQtyInOrderedUom},
				{"performance", performance}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "createOrAdjustOrder", params);
		}
		// ]]
		try {
			CustorderEntity custorder = splitWrapper.getCustorder();
			boolean isPersisted = isCustorderPersisted(custorder);
			
			//orders should be adjusted or created only if
			// - new order creating is running right now (!isPersisted)
			// - customer order flag is checked
			if (!isPersisted || bulkCustorderItem.getExactCustorderQtyFlag()) {
				// init new bulk order item if needed
				if (bulkOrderItem == null) {
					bulkOrderItem = initSingleDelivery(splitWrapper, bulkCustorderItem, performance);
				}
				
				//adjust/set new quantity for the given order
				remainingQtyInOrderedUom = adjustSdoQuantities(splitWrapper, performance, 
						bulkOrderItem, bulkCustorderItem, remainingQtyInOrderedUom);
			}
			else {
				remainingQtyInOrderedUom = BigDecimal.ZERO;
			}
			return remainingQtyInOrderedUom;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(remainingQtyInOrderedUom);
			}
		// ]]
		}
	}
	
	/**
	 * Adjust new quantity value for the given single order according to remainingQtyInOrderedUom. 
	 * Respects if order is not editable. Decides if exact or uom quantity is needed to be set.
	 * 
	 * @return
	 * The quantity which is still remaining according to the quantity of the customer order 
	 * and the sum of the order quantities. Zero returned if no quantity remained.
	 * 
	 */
	private static BigDecimal adjustSdoQuantities(CustorderSplitWrapper splitWrapper, boolean performance, 
			OrderItemEntity bulkOrderItem, CustorderItemEntity bulkCustorderItem, BigDecimal remainingQtyInOrderedUom) {
		// [[
		BigDecimal result = BigDecimal.ZERO;
		Object[][] params = {{"splitWrapper", splitWrapper},
				{"performance", performance},
				{"bulkOrderItem", bulkOrderItem},
				{"bulkCustorderItem", bulkCustorderItem},
				{"remainingQtyInOrderedUom", remainingQtyInOrderedUom}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "adjustSdoQuantities", params);
		}
		// ]]
		try {
			UomBsEntity orderedUom = splitWrapper.get_orderedUom();
			BigDecimal vehicleMaxLoadInOrderedUom = splitWrapper.get_vehicleMaxLoadInOrderedUom();
			UomBsEntity fullVehicleUom = splitWrapper.get_fullVehicleUom();
			BigDecimal perSdoQtyInOrderedUom = splitWrapper.get_perSdoQtyInOrderedUom();
			
			BigDecimal origOrderedPlusRemaining = remainingQtyInOrderedUom.add(bulkOrderItem.getOrderedQuantity());
			vehicleMaxLoadInOrderedUom = getConnectedTruckNetWeight(bulkOrderItem, vehicleMaxLoadInOrderedUom);
			
			OrderItemEntity lastOrderItem = getLatestOrderItem(splitWrapper);
			boolean wasLastSdo = true;
			if (bulkOrderItem.getOrderItemId() != null && lastOrderItem != null && lastOrderItem.getOrderItemId() != null) {
				wasLastSdo = (bulkOrderItem.getOrderItemId().equals(lastOrderItem.getOrderItemId()));
			}
			boolean isLastSdo = origOrderedPlusRemaining.compareTo(vehicleMaxLoadInOrderedUom) <= 0;
			
			if (logDebugEnabled) {
				LOG.debug("wasLastSdo: " + wasLastSdo);
				LOG.debug("isLastSdo: " + isLastSdo);
			}
			
			boolean isEditableSdo = isOrderEditable(splitWrapper, wasLastSdo, bulkCustorderItem);
			
			if (!bulkOrderItem.getOrderedQuantity().equals(vehicleMaxLoadInOrderedUom) && isEditableSdo) {
				if (isLastSdo) { // last SDO
					if (LogonUtil.isRailDirectOrder(splitWrapper.getDeliveryTypeCd()) && orderedUom.getUomCd().equals(Constant.UOM_CONTAINER)) {
						for (int i=0; i < origOrderedPlusRemaining.intValue(); i++) {
							if (i == 0) {
								bulkOrderItem.setOrderedQuantity(BigDecimal.ONE);
								bulkOrderItem.setOrderedUom(orderedUom);
								remainingQtyInOrderedUom = origOrderedPlusRemaining.subtract(BigDecimal.ONE);
								bulkOrderItem.setOrderedUomNumerator(1L);
							}
							else {
								OrderItemEntity newBulkOrderItem = new OrderItemEntity();
								initSdoi(newBulkOrderItem, bulkOrderItem, bulkOrderItem.getOrder(), getSingleBulkCustorderItem(splitWrapper.getCustorder()));
								newBulkOrderItem.setOrderedQuantity(BigDecimal.ONE);
								newBulkOrderItem.setOrderedUom(orderedUom);
								remainingQtyInOrderedUom = remainingQtyInOrderedUom.subtract(BigDecimal.ONE);
								newBulkOrderItem.setOrderedUomNumerator(1L);
							}
						}
					}
					else if (useExactQuantities(splitWrapper, bulkCustorderItem)) {
						bulkOrderItem.setOrderedQuantity(BigDecimal.ZERO.add(origOrderedPlusRemaining.setScale(Constant.SCALE, BigDecimal.ROUND_HALF_UP).stripTrailingZeros()));
						bulkOrderItem.setOrderedUom(orderedUom);
						remainingQtyInOrderedUom = BigDecimal.ZERO;
						bulkOrderItem.setOrderedUomNumerator(1L);
					}
					else {	// last SDO has to be a full vehicle
						bulkOrderItem.setOrderedQuantity(BigDecimal.ONE);
						bulkOrderItem.setOrderedUom(fullVehicleUom);
						remainingQtyInOrderedUom = BigDecimal.ZERO;
						bulkOrderItem.setOrderedUomNumerator(fullVehicleUom.getNumeratorForConversion());
					}
				}
				else {	// not the last SDO
					if (LogonUtil.isRailDirectOrder(splitWrapper.getDeliveryTypeCd()) && orderedUom.getUomCd().equals(Constant.UOM_CONTAINER)) {
						for (int i=0; i < vehicleMaxLoadInOrderedUom.intValue(); i++) {
							if (i == 0) {
								bulkOrderItem.setOrderedQuantity(BigDecimal.ONE);
								bulkOrderItem.setOrderedUom(orderedUom);
								remainingQtyInOrderedUom = origOrderedPlusRemaining.subtract(BigDecimal.ONE);
								bulkOrderItem.setOrderedUomNumerator(1L);
							}
							else {
								OrderItemEntity newBulkOrderItem = new OrderItemEntity();
								initSdoi(newBulkOrderItem, bulkOrderItem, bulkOrderItem.getOrder(), getSingleBulkCustorderItem(splitWrapper.getCustorder()));
								newBulkOrderItem.setOrderedQuantity(BigDecimal.ONE);
								newBulkOrderItem.setOrderedUom(orderedUom);
								remainingQtyInOrderedUom = remainingQtyInOrderedUom.subtract(BigDecimal.ONE);
								newBulkOrderItem.setOrderedUomNumerator(1L);
							}
						}
					}
					if (bulkCustorderItem.getExactSdorderQtyFlag()) {	// exact SDO quantity
						bulkOrderItem.setOrderedQuantity(perSdoQtyInOrderedUom.setScale(Constant.SCALE, BigDecimal.ROUND_HALF_UP).stripTrailingZeros());
						bulkOrderItem.setOrderedUom(orderedUom);
						remainingQtyInOrderedUom = origOrderedPlusRemaining.subtract(perSdoQtyInOrderedUom);
						bulkOrderItem.setOrderedUomNumerator(1L);
					}
					else {
						bulkOrderItem.setOrderedQuantity(BigDecimal.ONE);
						bulkOrderItem.setOrderedUom(fullVehicleUom);
						remainingQtyInOrderedUom = origOrderedPlusRemaining.subtract(vehicleMaxLoadInOrderedUom);
						bulkOrderItem.setOrderedUomNumerator(fullVehicleUom.getNumeratorForConversion());
					}
				}
			}
			
			return result = remainingQtyInOrderedUom;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private static boolean useExactQuantities(CustorderSplitWrapper splitWrapper, CustorderItemEntity bulkCustorderItem) {
		// [[
		boolean result = false;
		Object[][] params = {{"splitWrapper", splitWrapper},
				{"bulkCustorderItem", bulkCustorderItem}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "useExactQuantities", params);
		}
		// ]]
		try {
			UomBsEntity orderedUom = splitWrapper.get_orderedUom();
			
			result = bulkCustorderItem.getExactCustorderQtyFlag() || bulkCustorderItem.getExactSdorderQtyFlag();
			
			if (!result) {
				CustorderEntity custorder = bulkCustorderItem.getCustorder();
				// all order items go to one truck at bimodal road-part if the ordered UoM is not forcing the multiorder option
				final long deliveryTypeCd = custorder.getDeliveryType().getDeliveryTypeCd();
				if (isDeliveryTypeOnlyMultiorderSplit(deliveryTypeCd) && !isUomMultiorder(orderedUom)) {
					result = true;
				}
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
	 * There are delivery types where we are not splitting the order by default only if the ordered UoM has the multiorder flag.
	 * This method helps to identify these delivery types.
	 * @param deliveryTypeCd
	 * @return
	 */
	private static boolean isDeliveryTypeOnlyMultiorderSplit(final long deliveryTypeCd) {
		if (Constant.DELIVERY_TYPE_BIMODAL_ROAD == deliveryTypeCd || Constant.DELIVERY_TYPE_BIMODAL_RAIL == deliveryTypeCd
				|| Constant.DELIVERY_TYPE_RAIL_DIRECT == deliveryTypeCd ) {
					return true;
			}
		return false;
	}
	
	private static boolean isUomMultiorder(final UomBsEntity orderedUom) {
		if (orderedUom != null && orderedUom.getCreateMultiOrderFlag() != null) {
			return orderedUom.getCreateMultiOrderFlag().booleanValue();
		}
		return false;
	}
	
	/**
	 * Takes the orderItems belonging to the customerOrder contained in the splitWrapper.
	 * Makes a sorting by deliveryFrom (and orderNumber, if deliveryFrom is the same), where
	 * the later one and higher orderNumber is in the beginning of the list.
	 * 
	 * @return the orderItem, which was created the last time
	 */
	public static OrderItemEntity getLatestOrderItem(CustorderSplitWrapper splitWrapper) {
		// [[
		OrderItemEntity result = null;
		Object[][] params = {{"splitWrapper", splitWrapper}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "getLatestOrderItem", params);
		}
		// ]]
		try {
			final Collection<OrderItemEntity> orderItems = getOrderItems(splitWrapper);
		
			if (!orderItems.isEmpty()) {
				result = Collections.min(orderItems, new OrderComparator());
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
	 * Takes the orderItems belonging to the customerOrder contained in the splitWrapper.
	 * Makes a sorting by deliveryFrom (and orderNumber, if deliveryFrom is the same), where
	 * the later one and higher orderNumber is in the beginning of the list.
	 * 
	 * @return the orderItem, which was created the first time
	 */
	public static OrderItemEntity getFirstOrderItem(CustorderSplitWrapper splitWrapper) {
		// [[
		OrderItemEntity result = null;
		Object[][] params = {{"splitWrapper", splitWrapper}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "getFirstOrderItem", params);
		}
		// ]]
		try {
			final Collection<OrderItemEntity> orderItems = getOrderItems(splitWrapper);
		
			if (!orderItems.isEmpty()) {
				result = Collections.max(orderItems, new OrderComparator());
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

	private static Collection<OrderItemEntity> getOrderItems(CustorderSplitWrapper splitWrapper) {
		// [[
		Collection<OrderItemEntity> result = null;
		Object[][] params = {{"splitWrapper", splitWrapper}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "getOrderItems", params);
		}
		// ]]
		try {

			final CustorderEntity custorder = splitWrapper.getCustorder();
			final Collection<OrderItemEntity> orderItems = new ArrayList<OrderItemEntity>();
			
			Iterator<OrderEntity> orderIt = createOrderIterator(custorder);
			while (orderIt.hasNext()) {
				OrderEntity nextOrder = orderIt.next();
				if (nextOrder.getOrderNumber() != null) {//is already persisted
					OrderItemEntity bulkOrderItem = getBulkDumpOrPowderOrderItem(nextOrder);
					orderItems.add(bulkOrderItem);
				}
			}
			return result = orderItems;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/* Checks if quantity of an order can be modified or not. It assumes that the order belongs to 
	 * one of the priority groups (see getAdjustableOrders).
	 * 
	 * Quantity in an sdo is almost always editable. Exception: if it is an already existing customer order 
	 * (not this is the time when it is being created), customer order quantity is exact, but single order
	 * quantity is not exact.
	 * 
	 */
	private static boolean isOrderEditable(CustorderSplitWrapper splitWrapper, boolean isLastSdo, 
			CustorderItemEntity bulkCustorderItem) {
		// [[
		boolean result = false;
		Object[][] params = {{"splitWrapper", splitWrapper},
				{"isLastSdo", isLastSdo},
				{"bulkCustorderItem", bulkCustorderItem}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "isOrderEditable", params);
		}
		// ]]
		try {
			boolean isPersisted = isCustorderPersisted(bulkCustorderItem.getCustorder());
			
			boolean readOnly = 
					isPersisted //coming from order details modification/loading 
					&& !isLastSdo //(current) last sdo can be edited
					&& bulkCustorderItem.getExactCustorderQtyFlag() //sum must be exact
					&& !bulkCustorderItem.getExactSdorderQtyFlag() //orders before the last one don't need to be exact (must remain as is)
					;
			return result = !readOnly;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
		
	private static Calendar initPerformanceDeliveryTime(CustorderSplitWrapper splitWrapper) {
		// [[
		Calendar result = null;
		Object[][] params = {{"splitWrapper", splitWrapper}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "initPerformanceDeliveryTime", params);
		}
		// ]]
		try {
			CustorderEntity custorder = splitWrapper.getCustorder();
			double deliveryFqyInMinutes = splitWrapper.get_deliveryFqyInMinutes();
			
			Calendar deliveryTime = new GregorianCalendar();
			deliveryTime.setTime(custorder.getDeliveryFromDat());
			
			int n = 0;
			if (custorder.getOrders() != null) {
				n = custorder.getOrders().size();
			}
			
			deliveryTime.add(Calendar.MINUTE, (n - 1) * (int) Math.round(deliveryFqyInMinutes));
			
			return result = deliveryTime;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private static void completeCustOrderFields(CustorderSplitWrapper splitWrapper, CustorderItemEntity bulkCustorderItem, long deliveryTimeToleranceInMillisec) {
		// [[
		Object[][] params = {{"splitWrapper", splitWrapper},
				{"bulkCustorderItem", bulkCustorderItem},
				{"deliveryTimeToleranceInMillisec", deliveryTimeToleranceInMillisec}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "completeCustOrderFields", params);
		}
		// ]]
		try {
			UomBsEntity orderedUom = splitWrapper.get_orderedUom();
			BigDecimal perSdoQtyInOrderedUom = splitWrapper.get_perSdoQtyInOrderedUom();
			CustorderEntity custorder = splitWrapper.getCustorder();
			
			// complete CO fields
			if (custorder.getDeliveryToDat() == null) {
				List<OrderEntity> sdos = custorder.getOrders();
				if (sdos != null && !sdos.isEmpty()) {
					OrderEntity lastSdo = sdos.get(sdos.size() -1);
					Date deliveryToDateFromLastSdo = lastSdo.getDeliveryToDat();
					Date custorderDeliveryToDate = new Date(deliveryToDateFromLastSdo.getTime() - deliveryTimeToleranceInMillisec);
					custorder.setDeliveryToDat(custorderDeliveryToDate);
				}
				else {
					String errMsg = "no single delivert orders were found in custorder after split";
					LOG.error(errMsg);
					throw new LogonException(errMsg, Reason.RES_IMPL_BUG);
				}
			}
			
			// complete bulk COI fields
			if (bulkCustorderItem.getOrderedQuantity() == null) {
				bulkCustorderItem.setOrderedQuantity(perSdoQtyInOrderedUom);
				bulkCustorderItem.setOrderedUom(orderedUom);
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
	
	//remainingQtyInOrderedUom <= 0 expected, otherwise no sdo deleted/adjusted
	private static void deleteAdjustOrders(CustorderSplitWrapper splitWrapper, BigDecimal remainingQtyInOrderedUom, CustorderItemEntity bulkCustorderItem) {
		// [[
		Object[][] params = {{"splitWrapper", splitWrapper},
				{"remainingQtyInOrderedUom", remainingQtyInOrderedUom},
				{"bulkCustorderItem", bulkCustorderItem}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "deleteAdjustOrders", params);
		}
		// ]]
		try {
			CustorderEntity custorder = splitWrapper.getCustorder();
			ArrayList<OrderItemEntity> adjustableOrders = makePriorityGroupsOfOrders(splitWrapper);
			BigDecimal vehicleMaxLoadInOrderedUom = splitWrapper.get_vehicleMaxLoadInOrderedUom();
					
			OrderItemEntity bulkOrderItem = null;
			
			// delete/adjust SDOs while too much qty is loaded
			int numOfLoops = 0;
			
			UomBsEntity orderedUom = bulkCustorderItem.getOrderedUom();
			BigDecimal remainingInTons = splitWrapper.convertQuantity(bulkCustorderItem.getSapMaterial(), 
					orderedUom.getUomCd(), Constant.UOM_TONS, remainingQtyInOrderedUom).getQuantity();
			
			boolean useTolerance = checkCustorderTolerance(splitWrapper.getCustorder());
			BigDecimal toleranceInTons = BigDecimal.ZERO;
			if (useTolerance) {
				toleranceInTons = getWeightToleranceInTons(splitWrapper);
			}
			
			//remainingInTons should always be <=0 (as this is the delete case); toleranceInTons is always positive
			//ex: remainingInTons = -2, toleranceInTons = 10 => as -10 <= -2 <= 10, no adjust/delete
			while (adjustableOrders != null && !adjustableOrders.isEmpty() 
					&& remainingInTons.multiply(new BigDecimal(-1)).compareTo(toleranceInTons) > 0) {
				
				// last line of defense against infinite loops
				++numOfLoops;
				if (numOfLoops > splitWrapper.getNumOfMaxOrders()) {
					String errMsg = "too many orders, limit reached: " + numOfLoops;
					LOG.error(errMsg);
					String[] additionalInfo = { ""+splitWrapper.getNumOfMaxOrders(), ""+numOfLoops };					
					throw new ValidationException(new ValidationErrorReason(CustorderError.MAX_NUMBER_OF_ORDERS, custorder, additionalInfo));
				}
				
				if (!adjustableOrders.isEmpty()) {
					bulkOrderItem = adjustableOrders.remove(0);//highest (1st) priority to modify
				}
				
				if (bulkOrderItem != null) {
					vehicleMaxLoadInOrderedUom = getConnectedTruckNetWeight(bulkOrderItem, vehicleMaxLoadInOrderedUom);
					
					remainingQtyInOrderedUom = deleteOrAdjustOrder(splitWrapper, bulkOrderItem, bulkCustorderItem, remainingQtyInOrderedUom, 
							vehicleMaxLoadInOrderedUom);
					remainingInTons = splitWrapper.convertQuantity(bulkCustorderItem.getSapMaterial(), 
							orderedUom.getUomCd(), Constant.UOM_TONS, remainingQtyInOrderedUom).getQuantity();
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
	
	private static BigDecimal deleteOrAdjustOrder(CustorderSplitWrapper splitWrapper, OrderItemEntity bulkOrderItem, CustorderItemEntity bulkCustorderItem, BigDecimal remainingQtyInOrderedUom, BigDecimal vehicleMaxLoadInOrderedUom) {
		// [[
		BigDecimal result = BigDecimal.ZERO;
		Object[][] params = {{"splitWrapper", splitWrapper},
				{"bulkOrderItem", bulkOrderItem},
				{"bulkCustorderItem", bulkCustorderItem},
				{"remainingQtyInOrderedUom", remainingQtyInOrderedUom},
				{"vehicleMaxLoadInOrderedUom", vehicleMaxLoadInOrderedUom}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "deleteOrAdjustOrder", params);
		}
		// ]]
		try {
			// get processed and validated parameters
			UomBsEntity orderedUom = splitWrapper.get_orderedUom();
			UomBsEntity fullVehicleUom = splitWrapper.get_fullVehicleUom();
			CustorderEntity custorder = splitWrapper.getCustorder();
			
			BigDecimal qtyToRemoveInOrderedUom = remainingQtyInOrderedUom.abs();
			
			BigDecimal orderedQtyInOrderedUom = splitWrapper.convertQuantity(bulkCustorderItem.getSapMaterial(), bulkOrderItem.getOrderedUom().getUomCd(),
					bulkCustorderItem.getOrderedUom().getUomCd(), bulkOrderItem.getOrderedQuantity()).getQuantity();
			
			boolean exact = bulkCustorderItem.getExactCustorderQtyFlag() || bulkCustorderItem.getExactSdorderQtyFlag();
			BigDecimal diff = orderedQtyInOrderedUom.subtract(qtyToRemoveInOrderedUom);

			boolean isPersisted = isCustorderPersisted(custorder);
			LOG.debug("isCustorderPersisted: " + isPersisted);
			//orders should be adjusted or deleted only if
			// - new order creating is running right now (!isPersisted)
			// - customer order flag is checked
			if (!isPersisted || bulkCustorderItem.getExactCustorderQtyFlag()) {
				// if <: not last, less qty ordered than to remove --> delete this sdo, adjust/delete following one
				// if =: last one, exactly same qty ordered than to remove --> delete sdo
				if (diff.compareTo(BigDecimal.ZERO) <= 0) {
					qtyToRemoveInOrderedUom = diff;
					setNextOrderToDeleted(bulkOrderItem.getOrder(), splitWrapper);
				}
				// last one, more qty ordered than to remove --> adjust sdo
				else {//if (diff.compareTo(BigDecimal.ZERO) > 0) {
					
					if (exact) {
						bulkOrderItem.setOrderedQuantity(diff.setScale(Constant.SCALE, BigDecimal.ROUND_HALF_UP).stripTrailingZeros());
						bulkOrderItem.setOrderedUom(orderedUom);
						qtyToRemoveInOrderedUom = BigDecimal.ZERO;
						bulkOrderItem.setOrderedUomNumerator(1L);
					}
					else {
						bulkOrderItem.setOrderedQuantity(BigDecimal.ONE);
						bulkOrderItem.setOrderedUom(fullVehicleUom);
						qtyToRemoveInOrderedUom = BigDecimal.ZERO;
						bulkOrderItem.setOrderedUomNumerator(fullVehicleUom.getNumeratorForConversion());
					}
				}
			}
			
			return result = qtyToRemoveInOrderedUom;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/*
	 * Sorts the single orders of the customer order by priorities, where the priorities:
	 * 1st: not yet scheduled
	 * 2nd: scheduled on a planning truck
	 * 3rd: scheduled by LDS
	 * Orders within every priority group are further sorted by deliveryFrom date and orderNumber.
	 * 
	 * First order in the returned array should be modified first.
	 */
	private static ArrayList<OrderItemEntity> makePriorityGroupsOfOrders(CustorderSplitWrapper custorderSplitWrapper) {
		// [[
		ArrayList<OrderItemEntity> result = null;
		Object[][] params = {{"custorderSplitWrapper", custorderSplitWrapper}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "makePriorityGroupsOfOrders", params);
		}
		// ]]
		try {
			CustorderEntity custorder = custorderSplitWrapper.getCustorder();
			
			ArrayList<OrderItemEntity> adjustableOrders = new ArrayList<OrderItemEntity>();
			OrderItemEntity bulkOrderItem;
			
			//contains orders any in any state before scheduling
			ArrayList<OrderItemEntity> ordersBeforeScheduling = new ArrayList<OrderItemEntity>();
			
			//contains orders in scheduled state, assigned to a planning truck
			ArrayList<OrderItemEntity> ordersScheduledPlanningTruck = new ArrayList<OrderItemEntity>();
			
			//contains orders in scheduled state, not assigned to a planning truck, scheduled by LDS
			ArrayList<OrderItemEntity> ordersScheduledByLds = new ArrayList<OrderItemEntity>();
			
			Iterator<OrderEntity> orderIt = createOrderIterator(custorder);
			while (orderIt.hasNext()) {
				OrderEntity nextOrder = orderIt.next();
				OrderStatusBsEntity orderStatus = nextOrder.getOrderStatus();
				bulkOrderItem = getBulkDumpOrPowderOrderItem(nextOrder);
				
				if (orderStatus.getOrderStatusCd().equals(Constant.ORDER_STATUS_DISPATCHED)) {
					boolean assignedToPlanningTruck = false;
					if (bulkOrderItem.getLoad() != null && bulkOrderItem.getLoad().getTruck() != null
							&& LogonUtil.isPlanningTruck(bulkOrderItem.getLoad().getTruck())) {
						assignedToPlanningTruck = true;
					}
					LOG.debug("assignedToPlanningTruck:" + assignedToPlanningTruck);

					if (assignedToPlanningTruck) {
						ordersScheduledPlanningTruck.add(bulkOrderItem);
					}
				}
				else if (orderStatus.getOrderStatusCd().equals(Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER)) {
					boolean manuallyScheduled = custorderSplitWrapper.getOrderManuallyScheduled(nextOrder);
					LOG.debug("manuallyScheduled:" + manuallyScheduled);
					if (!manuallyScheduled) {//scheduled by LDS
						ordersScheduledByLds.add(bulkOrderItem);
					}
				}
				// Dispatched Status is before *TRANSMITTED
				else if (LogonStatusUtils.isDispatchedOrBefore(orderStatus)) {
					ordersBeforeScheduling.add(bulkOrderItem);
				}
			}
			
			Collections.sort(ordersBeforeScheduling, new OrderComparator());
			Collections.sort(ordersScheduledPlanningTruck, new OrderComparator());
			Collections.sort(ordersScheduledByLds, new OrderComparator());
			
			//order of priorities
			adjustableOrders.addAll(ordersBeforeScheduling);//Prio 1
			adjustableOrders.addAll(ordersScheduledPlanningTruck);//Prio 2
			adjustableOrders.addAll(ordersScheduledByLds);//Prio 3
			
			return result = adjustableOrders;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public static class OrderComparator implements Comparator<OrderItemEntity> {
		
		//latest order is the first, oldest is the last
		public int compare(OrderItemEntity o1, OrderItemEntity o2) {
			Date d1 = null;
			Date d2 = null;
			if (o1 == null || o2 == null || o1.getOrder() == null || o2.getOrder() == null) {
				return 0;
			}
			d1 = o1.getOrder().getDeliveryFromDat();
			d2 = o2.getOrder().getDeliveryFromDat();

			int compResult = 0;
			if (d1 != null && d2 != null) {
				compResult = d2.compareTo(d1);//desc by date
			}
			
			if (compResult == 0 && o2.getOrder().getOrderNumber() != null && o1.getOrder().getOrderNumber() != null) {
				compResult = o2.getOrder().getOrderNumber().compareTo(o1.getOrder().getOrderNumber());//desc by ordernumber
			}
			return compResult;
		}
	}

	
	private static BigDecimal getConnectedTruckNetWeight(OrderItemEntity bulkOrderItem, BigDecimal defaultWeight) {
		// [[
		BigDecimal result = BigDecimal.ZERO;
		Object[][] params = {{"bulkOrderItem", bulkOrderItem},
				{"defaultWeight", defaultWeight}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "getConnectedTruckNetWeight", params);
		}
		// ]]
		try {
			BigDecimal vehicleMaxLoadInOrderedUom = defaultWeight;
			if (bulkOrderItem != null) {
				OrderEntity sdo = bulkOrderItem.getOrder();
				if (sdo.getOrderStatus().getOrderStatusCd().equals(Constant.ORDER_STATUS_DISPATCHED) 
						&& bulkOrderItem.getLoad().getTruck() != null) {
					TruckEntity truck = bulkOrderItem.getLoad().getTruck();
					
					//if sdo is assigned to a truck, calc with that truck capacity
					if (truck.getMaxGrossWeightT() != null && truck.getTareWeightT() != null) {
						vehicleMaxLoadInOrderedUom = truck.getMaxGrossWeightT().subtract(truck.getTareWeightT());
					}
				}
			}
			return result = vehicleMaxLoadInOrderedUom;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private static void processAndValidatePerformanceBulkCustorderSplitWrapper(CustorderSplitWrapper splitWrapper) {
		// [[
		Object[][] params = {{"splitWrapper", splitWrapper}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "processAndValidatePerformanceBulkCustorderSplitWrapper", params);
		}
		// ]]
		try {
			// CO: Customer Order, COI: Customer Order Item, SDO: Single Delivery Order (OrderEntity), SDOI: Single Delivery Order Item (OrderItemEntity)
			
			// get the CO
			CustorderEntity custorder = splitWrapper.getCustorder();
			
			// initialize references for _THE_ bulk COI and a list for service material COIs
			CustorderItemEntity bulkCustorderItem = CustorderUtils.getSingleBulkCustorderItem(custorder);
			
			// calculate the required parameters
			final BigDecimal deliveryQtyInOrderedUom;
			final UomBsEntity orderedUom;
			final BigDecimal perSdoQtyInOrderedUom;
			final double deliveryFqyInMinutes;
			if (custorder.getDeliveryFreqEveryTime() != null) {		/* Case #1 and #2 */
				double deliveryFqy = custorder.getDeliveryFreqEveryTime();
				String deliveryFqyUom = custorder.getDeliveryFreqEveryTimeUom();
				if (Constant.UOM_HRE.equals(deliveryFqyUom)) {
					deliveryFqyInMinutes = deliveryFqy * 60.0;
				}
				else if (Constant.UOM_MIN.equals(deliveryFqyUom)) {
					deliveryFqyInMinutes = deliveryFqy;
				}
				else {
					String errMsg = "unknown time UoM: " + deliveryFqyUom;
					LOG.error(errMsg);
					throw new LogonException(errMsg, Reason.RES_ERROR_IMPL);
				}
				
				if (bulkCustorderItem.getOrderedQuantity() != null) {	/* Case #1 */
					deliveryQtyInOrderedUom = bulkCustorderItem.getOrderedQuantity();
					orderedUom = bulkCustorderItem.getOrderedUom();
					
					Date inputDeliveryToDat = null;
					if (custorder.getOrders() == null || custorder.getOrders().isEmpty()) {
						inputDeliveryToDat = custorder.getDeliveryToDat();
					}
					
					if (bulkCustorderItem.getPerSdorderQuantity() == null && inputDeliveryToDat == null) {	/* Case #1/A */
						perSdoQtyInOrderedUom = splitWrapper.getVehicleMaxCapacityInUomCd(bulkCustorderItem.getSapMaterial(), orderedUom.getUomCd());
					}
					else if (bulkCustorderItem.getPerSdorderQuantity() != null && inputDeliveryToDat == null) {	/* Case #1/B */
						if (isUomMultiorder(orderedUom)) {
							perSdoQtyInOrderedUom = splitWrapper.getVehicleMaxCapacityInUomCd(bulkCustorderItem.getSapMaterial(), orderedUom.getUomCd());
						}
						else {							
							perSdoQtyInOrderedUom = splitWrapper.convertQuantity(bulkCustorderItem.getSapMaterial(), bulkCustorderItem.getPerSdorderUom().getUomCd()
									, orderedUom.getUomCd(), bulkCustorderItem.getPerSdorderQuantity()).getQuantity();
						}
					}
					else if (bulkCustorderItem.getPerSdorderQuantity() == null && inputDeliveryToDat != null) {
						BigDecimal vehicleMaxLoadInOrderedUom = splitWrapper.getVehicleMaxCapacityInUomCd(bulkCustorderItem.getSapMaterial(), orderedUom.getUomCd());
						
						int deliveryIntervalInMinutes = DateTimeUtils.getDifferenceInMinutes(custorder.getDeliveryFromDat(), custorder.getDeliveryToDat());
						double numDeliveries = deliveryIntervalInMinutes / deliveryFqyInMinutes;
						BigDecimal maxDeliveryQtyInIntervalInOrderedUom = vehicleMaxLoadInOrderedUom.multiply(new BigDecimal(numDeliveries));
						if (maxDeliveryQtyInIntervalInOrderedUom.compareTo(deliveryQtyInOrderedUom) < 0) {
							String errMsg = "unable to fulfill all input parameters";
							LOG.error(errMsg);
							throw new LogonException(errMsg, Reason.RES_ERROR_IMPL);
						}
						
						perSdoQtyInOrderedUom = vehicleMaxLoadInOrderedUom;
					}
					else {	// bulkCustorderItem.getPerSdorderQuantity() != null && custorder.getDeliveryToDat() != null
						if (isUomMultiorder(orderedUom)) {
							perSdoQtyInOrderedUom = splitWrapper.getVehicleMaxCapacityInUomCd(bulkCustorderItem.getSapMaterial(), orderedUom.getUomCd());
						}
						else {							
							perSdoQtyInOrderedUom = splitWrapper.convertQuantity(bulkCustorderItem.getSapMaterial(), bulkCustorderItem.getPerSdorderUom().getUomCd()
									, orderedUom.getUomCd(), bulkCustorderItem.getPerSdorderQuantity()).getQuantity();
						}
						
						int deliveryIntervalInMinutes = DateTimeUtils.getDifferenceInMinutes(custorder.getDeliveryFromDat(), custorder.getDeliveryToDat());
						double numDeliveries = deliveryIntervalInMinutes / deliveryFqyInMinutes;
						
						BigDecimal plannedDeliveryQtyInOrderedUom = perSdoQtyInOrderedUom.multiply(new BigDecimal(numDeliveries));
						
						BigDecimal diff = plannedDeliveryQtyInOrderedUom.subtract(deliveryQtyInOrderedUom).abs();
						diff = diff.divide(BigDecimal.ONE, INTERNAL_SCALE, BigDecimal.ROUND_HALF_UP);
						if (diff.compareTo(BigDecimal.ZERO) != 0) {
							String errMsg = "unable to fulfill all input parameters";
							LOG.error(errMsg);
							throw new LogonException(errMsg, Reason.RES_ERROR_IMPL);
						}
					}
				}
				else if (custorder.getDeliveryToDat() != null) {	/* Case #2 */
					orderedUom = bulkCustorderItem.getSapMaterial().getBaseUom();
					
					if (bulkCustorderItem.getPerSdorderQuantity() != null && !isUomMultiorder(orderedUom)) {
						perSdoQtyInOrderedUom = splitWrapper.convertQuantity(bulkCustorderItem.getSapMaterial(), bulkCustorderItem.getPerSdorderUom().getUomCd()
								, orderedUom.getUomCd(), bulkCustorderItem.getPerSdorderQuantity()).getQuantity();
					}
					else {
						perSdoQtyInOrderedUom = splitWrapper.getVehicleMaxCapacityInUomCd(bulkCustorderItem.getSapMaterial(), orderedUom.getUomCd());
					}
					
					int deliveryIntervalInMinutes = DateTimeUtils.getDifferenceInMinutes(custorder.getDeliveryFromDat(), custorder.getDeliveryToDat());
					double numDeliveries = deliveryIntervalInMinutes / deliveryFqyInMinutes;
					
					deliveryQtyInOrderedUom = perSdoQtyInOrderedUom.multiply(new BigDecimal(numDeliveries));
				}
				else {
					String errMsg = "not enough input parameter in customer order / customer order item";
					LOG.error(errMsg);
					throw new LogonException(errMsg, Reason.RES_ERROR_IMPL);
				}
			}
			else if (custorder.getDeliveryFreqQtyperhre() != null) {	/* Case #3 and #4 */
				BigDecimal deliveryQtyPerHre = custorder.getDeliveryFreqQtyperhre();
				UomBsEntity deliveryQtyPerHreUom = custorder.getDeliveryFreqQtyperhreUom();
				
				if (bulkCustorderItem.getOrderedQuantity() != null) {	/* Case #3 */
					deliveryQtyInOrderedUom = bulkCustorderItem.getOrderedQuantity();
					orderedUom = bulkCustorderItem.getOrderedUom();
					
					if (bulkCustorderItem.getPerSdorderQuantity() != null && !isUomMultiorder(orderedUom)) {
						perSdoQtyInOrderedUom = splitWrapper.convertQuantity(bulkCustorderItem.getSapMaterial(), bulkCustorderItem.getPerSdorderUom().getUomCd()
								, orderedUom.getUomCd(), bulkCustorderItem.getPerSdorderQuantity()).getQuantity();
					}
					else {
						perSdoQtyInOrderedUom = splitWrapper.getVehicleMaxCapacityInUomCd(bulkCustorderItem.getSapMaterial(), orderedUom.getUomCd());
					}
			
					BigDecimal deliveryQtyPerHreInOrderedUom = splitWrapper.convertQuantity(bulkCustorderItem.getSapMaterial(), deliveryQtyPerHreUom.getUomCd()
							, orderedUom.getUomCd(), deliveryQtyPerHre).getQuantity();
					
					BigDecimal deliveryFqyInHre = perSdoQtyInOrderedUom.divide(deliveryQtyPerHreInOrderedUom, INTERNAL_SCALE, BigDecimal.ROUND_HALF_UP);
					deliveryFqyInMinutes = deliveryFqyInHre.multiply(new BigDecimal(60), INTERNAL_MATH_CONTEXT).doubleValue();
				}
				else if (custorder.getDeliveryToDat() != null) {	/* Case #4 */
					int deliveyIntervalInMinutes = DateTimeUtils.getDifferenceInMinutes(custorder.getDeliveryFromDat(), custorder.getDeliveryToDat());
					
					BigDecimal deliveryQtyPerMinutes = deliveryQtyPerHre.divide(new BigDecimal(60), INTERNAL_SCALE, BigDecimal.ROUND_HALF_UP);
					BigDecimal deliveryQtyInPerHreUom = deliveryQtyPerMinutes.multiply(new BigDecimal(deliveyIntervalInMinutes));
					
					orderedUom = splitWrapper.getUomBsEntityFromCache(bulkCustorderItem.getSapMaterial().getBaseUom().getUomCd());
					BigDecimal deliveryQtyInOrderedUom_INTERNAL_SCALE = splitWrapper.convertQuantity(bulkCustorderItem.getSapMaterial(), deliveryQtyPerHreUom.getUomCd()
							, orderedUom.getUomCd(), deliveryQtyInPerHreUom).getQuantity();
					
					deliveryQtyInOrderedUom = deliveryQtyInOrderedUom_INTERNAL_SCALE.divide(BigDecimal.ONE, 3, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
					
					if (bulkCustorderItem.getPerSdorderQuantity() != null && !isUomMultiorder(orderedUom)) {
						perSdoQtyInOrderedUom = splitWrapper.convertQuantity(bulkCustorderItem.getSapMaterial(), bulkCustorderItem.getPerSdorderUom().getUomCd()
								, orderedUom.getUomCd(), bulkCustorderItem.getPerSdorderQuantity()).getQuantity();
					}
					else {
						perSdoQtyInOrderedUom = splitWrapper.getVehicleMaxCapacityInUomCd(bulkCustorderItem.getSapMaterial(), orderedUom.getUomCd());
					}
					
					BigDecimal deliveryQtyPerHreInOrderedUom = splitWrapper.convertQuantity(bulkCustorderItem.getSapMaterial(), deliveryQtyPerHreUom.getUomCd()
							, orderedUom.getUomCd(), deliveryQtyPerHre).getQuantity();
					BigDecimal deliveryFqyInHours = perSdoQtyInOrderedUom.divide(deliveryQtyPerHreInOrderedUom, INTERNAL_SCALE, BigDecimal.ROUND_HALF_UP);
					deliveryFqyInMinutes = deliveryFqyInHours.multiply(new BigDecimal(60)).doubleValue();
				}
				else {
					String errMsg = "not enough input parameter in customer order / customer order item";
					LOG.error(errMsg);
					throw new LogonException(errMsg, Reason.RES_IMPL_BUG);
				}
			}
			else {
				String errMsg = "not enough input parameter in customer order / customer order item";
				LOG.error(errMsg);
				throw new LogonException(errMsg, Reason.RES_IMPL_BUG);
			}
			
			final BigDecimal vehicleMaxLoadInOrderedUom;
			vehicleMaxLoadInOrderedUom = perSdoQtyInOrderedUom;
			
			// full vehicle UoM
			final UomBsEntity fullVehicleUom;
			if (isUomMultiorder(orderedUom)) {
				fullVehicleUom = orderedUom;
			}
			else {
				fullVehicleUom = splitWrapper.getFullVehicleUom();
			}
			
			// check mandatory parameters
			if (deliveryQtyInOrderedUom == null || Constant.CUSTORDER_ORDERED_QTY_MAX_VALUE.compareTo(deliveryQtyInOrderedUom) < 0
				|| Constant.CUSTORDER_ORDERED_QTY_MIN_VALUE.compareTo(deliveryQtyInOrderedUom) > 0) {
					StringBuilder errMsg = new StringBuilder("invalid deliveryQtyInOrderedUom");
					errMsg.append(" deliveryQtyInOrderedUom: ").append(deliveryQtyInOrderedUom == null ? null : deliveryQtyInOrderedUom.toString());
					LOG.error(errMsg.toString());
					String[] additionalInfo = { Constant.CUSTORDER_ORDERED_QTY_MIN_VALUE_STR, Constant.CUSTORDER_ORDERED_QTY_MAX_VALUE_STR };		
					throw new ValidationException(new ValidationErrorReason(CustorderError.INVALID_ORDERED_QUANTITY, bulkCustorderItem, additionalInfo));
			}
			else if (perSdoQtyInOrderedUom == null || vehicleMaxLoadInOrderedUom == null
				|| Constant.CUSTORDER_QTY_PER_SDO_MAX_VALUE.compareTo(perSdoQtyInOrderedUom) < 0
				|| Constant.CUSTORDER_QTY_PER_SDO_MIN_VALUE.compareTo(perSdoQtyInOrderedUom) > 0
				|| Constant.CUSTORDER_QTY_PER_SDO_MAX_VALUE.compareTo(vehicleMaxLoadInOrderedUom) < 0
				|| Constant.CUSTORDER_QTY_PER_SDO_MIN_VALUE.compareTo(vehicleMaxLoadInOrderedUom) > 0) {
					StringBuilder errMsg = new StringBuilder("invalid deliveryQtyPerSdoInOrderedUom/vehicleMaxLoadInOrderedUom");
					errMsg.append(" deliveryQtyPerSdoInOrderedUom: ").append(perSdoQtyInOrderedUom == null? null : perSdoQtyInOrderedUom.toString());
					errMsg.append(" vehicleMaxLoadInOrderedUom: ").append(vehicleMaxLoadInOrderedUom == null ? null : vehicleMaxLoadInOrderedUom.toString());
					LOG.error(errMsg.toString());
					String[] additionalInfo = { Constant.CUSTORDER_QTY_PER_SDO_MIN_VALUE_STR, Constant.CUSTORDER_QTY_PER_SDO_MAX_VALUE_STR };
					throw new ValidationException(new ValidationErrorReason(CustorderError.INVALID_QUANTITY_PER_DELIVERY, bulkCustorderItem, additionalInfo));
			}
			else if (deliveryFqyInMinutes < 0.0) {
				
				if (custorder.getDeliveryFreqEveryTime() != null) {
					StringBuilder errMsg = new StringBuilder("invalid deliveryFqyInMinutes (deliveryFreqEveryTime)");
					errMsg.append(" deliveryFqyInMinutes: ").append(deliveryFqyInMinutes);
					LOG.error(errMsg.toString());
					String[] additionalInfo = { Constant.CUSTORDER_DELIVERY_FQY_EVERY_TIME_MIN_VALUE_STR, Constant.CUSTORDER_DELIVERY_FQY_EVERY_TIME_MAX_VALUE_STR };		
					throw new ValidationException(new ValidationErrorReason(CustorderError.INVALID_DEL_FQY, custorder, additionalInfo));
				}
				StringBuilder errMsg = new StringBuilder("invalid deliveryFqyInMinutes (deliveryFreqQtyPerHre)");
				errMsg.append(" deliveryFqyInMinutes: ").append(deliveryFqyInMinutes);
				LOG.error(errMsg.toString());
				String[] additionalInfo = { Constant.CUSTORDER_DELIVERY_FQY_QTY_PER_HRE_MIN_VALUE_STR, Constant.CUSTORDER_DELIVERY_FQY_QTY_PER_HRE_MAX_VALUE_STR };
				throw new ValidationException(new ValidationErrorReason(CustorderError.INVALID_DEL_FQY, custorder, additionalInfo));
			}
			
			// initialize remaining quantity
			BigDecimal remainingQtyInOrderedUom = BigDecimal.ZERO.add(deliveryQtyInOrderedUom);
			
			// check number of expected deliveries
			int numOfDeliveries;
			String numOfDeliveriesStr;
			if (bulkCustorderItem.getExactCustorderQtyFlag() || bulkCustorderItem.getExactSdorderQtyFlag()) {
				BigDecimal[] divAndRem = remainingQtyInOrderedUom.divideAndRemainder(perSdoQtyInOrderedUom, INTERNAL_MATH_CONTEXT);
				if (divAndRem[1].compareTo(BigDecimal.ZERO) == 0) {	// no remainder
					numOfDeliveries = divAndRem[0].intValue();
					BigDecimal rem = perSdoQtyInOrderedUom.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
					numOfDeliveriesStr = ""+(numOfDeliveries - 1) + " +1 ("+rem.toPlainString()+" "+orderedUom.getUomCd() +")";
				}
				else {
					numOfDeliveries = divAndRem[0].intValue();
					BigDecimal rem = divAndRem[1].divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
					numOfDeliveriesStr = ""+numOfDeliveries + " +1 ("+rem.toPlainString() +" "+orderedUom.getUomCd() +")";
					++numOfDeliveries;	// reminder delivery
				}
			}
			else {
				BigDecimal num = remainingQtyInOrderedUom.divide(vehicleMaxLoadInOrderedUom, 0, BigDecimal.ROUND_CEILING).stripTrailingZeros();
				numOfDeliveries = num.intValue();
				numOfDeliveriesStr = "" + numOfDeliveries;
			}
			
			Date deliveryToDate;
			if (custorder.getDeliveryToDat() == null) {
				Calendar deliveryTime = new GregorianCalendar();
				deliveryTime.setTime(custorder.getDeliveryFromDat());
				deliveryTime.add(Calendar.MINUTE, (int) Math.round(deliveryFqyInMinutes) * numOfDeliveries);
				deliveryToDate = deliveryTime.getTime();
			}
			else {
				deliveryToDate = custorder.getDeliveryToDat();
			}
			
			// store processed and validated parameters
			splitWrapper.set_orderedUom(orderedUom);
			splitWrapper.set_remainingQtyInOrderedUom(remainingQtyInOrderedUom);
			splitWrapper.set_perSdoQtyInOrderedUom(perSdoQtyInOrderedUom);
			splitWrapper.set_vehicleMaxLoadInOrderedUom(vehicleMaxLoadInOrderedUom);
			splitWrapper.set_fullVehicleUom(fullVehicleUom);
			splitWrapper.set_deliveryFqyInMinutes(deliveryFqyInMinutes);
			splitWrapper.set_deliveryToDate(deliveryToDate);
			splitWrapper.set_numOfDeliveries(numOfDeliveries);
			splitWrapper.set_numOfDeliveriesStr(numOfDeliveriesStr);
			
			if (numOfDeliveries > splitWrapper.getNumOfMaxOrders()) {
				String errMsg = "too many orders: " + numOfDeliveries;
				LOG.error(errMsg);
				String[] additionalInfo = { ""+splitWrapper.getNumOfMaxOrders(), ""+numOfDeliveries };		
				throw new ValidationException(new ValidationErrorReason(CustorderError.MAX_NUMBER_OF_ORDERS, custorder, additionalInfo));
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
	
	protected static void splitPerformanceBulkCustorder(CustorderSplitWrapper splitWrapper) {
		// [[
		Object[][] params = {{"splitWrapper", splitWrapper}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "splitPerformanceBulkCustorder", params);
		}
		// ]]
		try {
			splitBulkCustorder(splitWrapper, true);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	protected static void splitBulkCustorder(CustorderSplitWrapper splitWrapper, boolean performance) {
		// [[
		Object[][] params = {{"splitWrapper", splitWrapper},
				{"performance", performance}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "splitBulkCustorder", params);
		}
		// ]]
		try {
			CustorderEntity custorder = splitWrapper.getCustorder();
			CustorderItemEntity bulkCustorderItem = CustorderUtils.getSingleBulkCustorderItem(custorder);
			// List<CustorderItemEntity> serviceMaterials = CustorderUtils.getServiceCustorderItems(custorder);
			BigDecimal remainingQtyInOrderedUom = splitWrapper.get_remainingQtyInOrderedUom();
			
			Iterator<OrderEntity> orderIt = createOrderIterator(custorder);
			
			BigDecimal itemsQty = BigDecimal.ZERO;
			OrderItemEntity bulkOrderItem = null;
			
			BigDecimal notEditableCustorderQty = BigDecimal.ZERO;
			
			ArrayList<OrderItemEntity> adjustableOrders = makePriorityGroupsOfOrders(splitWrapper);
			
			//sum up ordered quantities
			while (orderIt.hasNext()) {
				OrderEntity nextOrder = orderIt.next();
				Long orderStatusCd = nextOrder.getOrderStatus().getOrderStatusCd();
				bulkOrderItem = getBulkDumpOrPowderOrderItem(nextOrder);
				
				if (!orderStatusCd.equals(Constant.ORDER_STATUS_DELETED)) {
					BigDecimal bulkOrderItemQtyInOrderedUom = getLatestQuantityInOrderedUom(bulkOrderItem, bulkCustorderItem, splitWrapper);
					itemsQty = itemsQty.add(bulkOrderItemQtyInOrderedUom);
					
					boolean isQuantityEditable = adjustableOrders != null && adjustableOrders.contains(bulkOrderItem);
					if (!isQuantityEditable) {
						notEditableCustorderQty = notEditableCustorderQty.add(bulkOrderItemQtyInOrderedUom);
					}
				}
			}
			
			// Checking, if the entered custorder item quantity is allowed. If not, throw exception.
			// Skip, if the order has no orderedQuantity (yet, Bulk-Performance)
			BigDecimal orderedQuantity = bulkCustorderItem.getOrderedQuantity();
			if (orderedQuantity != null) {
				// Quantity which is editable, can be deleted. If the new qty is lower than editable, it's not possible.
				BigDecimal remainingQty = orderedQuantity.subtract(notEditableCustorderQty);
				boolean validCustorderQty = remainingQty.compareTo(BigDecimal.ZERO) >= 0;
				if (!validCustorderQty) {
					String notEditableCustorderQtyString = notEditableCustorderQty.toString()+" "
							+bulkCustorderItem.getOrderedUom().getUomCd();
					LOG.warn(notEditableCustorderQtyString);
					return;		// do not throw exception, could be a valid use-case at manual loading, see LOGON-6376
				}
			}
			
			remainingQtyInOrderedUom = remainingQtyInOrderedUom.subtract(itemsQty);//=difference
			if (remainingQtyInOrderedUom.signum() < 0) {
				deleteAdjustOrders(splitWrapper, remainingQtyInOrderedUom, bulkCustorderItem);
			}
			else if (remainingQtyInOrderedUom.signum() > 0) {
				createAdjustOrders(splitWrapper, remainingQtyInOrderedUom, bulkCustorderItem, performance);
			}
			else {//if (remainingQtyInOrderedUom.longValue() == 0) {
				setExactQuantityForOrders(splitWrapper, bulkCustorderItem);
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
	
	private static void setNextOrderToDeleted(OrderEntity nextOrder, CustorderSplitWrapper splitWrapper) {
		OrderStatusBsEntity deletedOrderStatus = splitWrapper.getOrderStatusEntity(Constant.ORDER_STATUS_DELETED);
		nextOrder.setOrderStatus(deletedOrderStatus);
		LOG.debug("SDO deleted because it's not needed anymore based on the calculated qty: " + nextOrder.getOrderNumber());
	}

	private static OrderItemEntity initSingleDelivery(CustorderSplitWrapper splitWrapper, 
			CustorderItemEntity bulkCustorderItem, boolean performance) {
		// [[
		OrderItemEntity result = null;
		Object[][] params = {{"splitWrapper", splitWrapper},
				{"bulkCustorderItem", bulkCustorderItem},
				{"performance", performance}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "initSingleDelivery", params);
		}
		// ]]
		try {
			OrderItemEntity bulkOrderItem;
		
			CustorderEntity custorder = splitWrapper.getCustorder();
			long deliveryTimeToleranceInMillisec = deliveryTimeToleranceInMillis(custorder);
			List<CustorderItemEntity> serviceMaterials = getServiceCustorderItems(custorder);
			
			OrderEntity sdo = new OrderEntity();
			sdo.setArchivedFlag(false);
			final ArchivingStatusBsEntity archivingStatusBsEntity = splitWrapper.getArchivingStatusEntity(Constant.ARCHIVING_STATUS_NEW);
			sdo.setArchivingStatus(archivingStatusBsEntity);
			initSdo(sdo, splitWrapper.getSdoTemplate(), custorder, splitWrapper);
			
			//TODO orsika 20121201 : this is a workaround: transport duration should come 
			//from splitWrapper.sdoTemplate 
			if (sdo.getTransportDurationMin() == null && custorder.getOrders() != null) {
				OrderItemEntity oi = getFirstOrderItem(splitWrapper);
				if (oi != null && oi.getOrder() != null) {
					sdo.setTransportDurationMin(oi.getOrder().getTransportDurationMin());
				}
			}
			
			// initialize new bulk SDOI
			bulkOrderItem = new OrderItemEntity();
			initSdoi(bulkOrderItem, splitWrapper.getSdoiTemplate(), sdo, bulkCustorderItem);
			
			// initialize service SDOIs and add ALL to the SDO
			if (!serviceMaterials.isEmpty()) {
				for (CustorderItemEntity serviceCoi : serviceMaterials) {
					OrderItemEntity sdoi = new OrderItemEntity();
					initLoadedSdoi(splitWrapper, sdoi, splitWrapper.getSdoiTemplate(), sdo, serviceCoi);
				}
			}
			
			bulkOrderItem.setOrderedQuantity(BigDecimal.ZERO);
			if (performance) {
				Calendar deliveryTime = initPerformanceDeliveryTime(splitWrapper);
				sdo.setDeliveryFromDat(new Date(deliveryTime.getTime().getTime() - deliveryTimeToleranceInMillisec));
				sdo.setDeliveryToDat(new Date(deliveryTime.getTime().getTime() + deliveryTimeToleranceInMillisec));
			}
			
			return result = bulkOrderItem;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	private static Iterator<OrderEntity> createOrderIterator(CustorderEntity custorder) {
		/* The orders list *MUST* be copied because the list itself changes during the splitting process. Without, this would make the iterator break (rightly!).
		 * The iterator is used to determine, if there are orders, which can be changed or have to be deleted. From the point where orders are created, 
		 * the existing ones are not relevant for the splitting process anymore.
		 * 
		 * There are 3 basic scenarios:
		 * 1) No existing
		 *	hasNext() is never true and all the recalculation will be skipped. Only new orders are created, none deleted.
		 * 2) Existing, but expanding/staying
		 *	   hasNext() is true in the beginning. If necessary, the orders which can still be changed are adjusted.
		 *	   When hasNext is false, the creation of new order begins. The iterator is irrelevant from that point.
		 * 3) Existing, but decreasing
		 *	   hasNext() is always true. The orders which can still be changed are adjusted, if necessary. The remaining quantity is zero, but there  are 
		 *	   still orders left. Those orders will get deleted, because there is no quantity left for those. The iterator for the copied list is still valid.
		 */
		
		final Deque<OrderEntity> orderList = new LinkedList<OrderEntity>();
		final Collection<OrderEntity> loadedOrders = custorder.getOrders();
		if (loadedOrders != null) {
			// Shallow copy is enough for this purpose! Just the list itself should not change
			for(OrderEntity o : loadedOrders) {
				
				// TODO (pascal Sep 3, 2012): Might be good to have an ordering by prio. Means that 
				// the lowest order state gets modified first. Since it goes for the ordernumber first, 
				// that should not be a big problem. If they mix up, it will end up in some funny 
				// (but still correct) status.
				
				// Putting the unchangeable first and change the changeables later on
				OrderStatusBsEntity orderStatus = o.getOrderStatus();
				if (LogonStatusUtils.isDispatchedOrBefore(orderStatus)) {
					orderList.addLast(o);
				}
				else {
					orderList.addFirst(o);
				}
			}
		}
		else {
			// initialize a list for the new split SDOs and add them to the CO
			custorder.setOrders(new ArrayList<OrderEntity>());
		}
		return orderList.iterator();
	}


	/**
	 * @param custorder
	 * @return
	 */
	static long deliveryTimeToleranceInMillis(CustorderEntity custorder) {
		long deliveryTimeToleranceInMillisec = 0L;
		if (custorder.getDeliveryTimeTolerance() != null) {
			double toleranceInToleranceUom = custorder.getDeliveryTimeTolerance();
			String toleranceUom = custorder.getDeliveryTimeToleranceUom();
			if (Constant.UOM_HRE.equals(toleranceUom)) {
				deliveryTimeToleranceInMillisec = Math.round(toleranceInToleranceUom * 60.0 * 60.0 * 1000.0);
			}
			else if (Constant.UOM_MIN.equals(toleranceUom)) {
				deliveryTimeToleranceInMillisec = Math.round(toleranceInToleranceUom * 60.0 * 1000.0);
			}
			else {
				String errMsg = "invalid time tolerance UoM: " + toleranceUom;
				LOG.error(errMsg);
				throw new LogonException(errMsg, Reason.RES_IMPL_BUG);
			}
		}
		return deliveryTimeToleranceInMillisec;
	}
	
	private static void initSdo(OrderEntity sdo, final OrderEntity sdoTemplate, final CustorderEntity custorder, CustorderSplitWrapper splitWrapper) {
		// [[
		Object[][] params = {{"sdo", sdo},
							{"sdoTemplate", sdoTemplate},
							{"custorder", custorder}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "initSdo", params);
		}
		// ]]
		try {
			if (sdoTemplate != null) {
				beanUtilsBean.copyProperties(sdo, sdoTemplate);
				
				if (sdoTemplate.getOrderVehicleTypes() != null) {
					
					sdo.setOrderVehicleTypes(new ArrayList<OrderTruckTypeEntity>());
					
					/*List<OrderTruckTypeEntity> templateOrderVehicleTypes = sdoTemplate.getOrderVehicleTypes();
					List<OrderTruckTypeEntity> orderVehicleTypes = new ArrayList<OrderTruckTypeEntity>(templateOrderVehicleTypes);
					Collections.copy(orderVehicleTypes, templateOrderVehicleTypes);
					for (OrderTruckTypeEntity orderTruckType : orderVehicleTypes) {
						orderTruckType.setOrder(sdo);
					}
					sdo.setOrderVehicleTypes(orderVehicleTypes);*/
					
					for (Object obj : sdoTemplate.getOrderVehicleTypes() ) {
						
						OrderTruckTypeEntity truckType = (OrderTruckTypeEntity) obj;
						OrderTruckTypeEntity truckTypeClone = (OrderTruckTypeEntity)BeanUtils.cloneBean(truckType);
						truckTypeClone.setOrder(sdo);
						sdo.getOrderVehicleTypes().add(truckTypeClone);
					}
				}
			}
			
			beanUtilsBean.copyProperties(sdo, custorder);
			
			/**
			 * Order status transitions are handled after persisting depending on the flags provided by each order taking screen/handler.
			 * See {@link CustorderServiceBean#processNewOrders} here the status is initialized only to the status new.
			 */
			if (sdo.getOrderStatus() == null) {
				OrderStatusBsEntity newOrderStatus = splitWrapper.getOrderStatusEntity(Constant.ORDER_STATUS_NEW);
				sdo.setOrderStatus(newOrderStatus);
			}
			
			if (sdo.getArchivingStatus() == null) {
				ArchivingStatusBsEntity archivingStatusBsEntity = splitWrapper.getArchivingStatusEntity(Constant.ARCHIVING_STATUS_NEW);
				sdo.setArchivingStatus(archivingStatusBsEntity);
			}

			custorder.getOrders().add(sdo);
			
			sdo.setCustorder(custorder);
			sdo.setOrderItems(new ArrayList<OrderItemEntity>());
		}
		catch (Exception e) {
			LOG.error("exception occurred during order property copy", e);
			throw new LogonException(e, Reason.RES_IMPL_BUG);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	private static void initSdoi(OrderItemEntity sdoi, final OrderItemEntity sdoiTemplate, final OrderEntity sdo, final CustorderItemEntity custorderItem) {
		// [[
		Object[][] params = {{"sdoi", sdoi},
							{"sdoiTemplate", sdoiTemplate},
							{"sdo", sdo},
							{"custorderItem", custorderItem}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "initSdoi", params);
		}
		// ]]
		try {
			if (sdoiTemplate != null) {
				beanUtilsBean.copyProperties(sdoi, sdoiTemplate);
			}
			
			if (custorderItem != null) {
				beanUtilsBean.copyProperties(sdoi, custorderItem);
			}
			
			sdo.getOrderItems().add(sdoi);
			sdoi.setOrder(sdo);
		}
		catch (Exception e) {
			LOG.error("exception occurred during order item property copy", e);
			throw new LogonException(e, Reason.RES_IMPL_BUG);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	
	private static void initLoadedSdoi(final CustorderSplitWrapper splitWrapper, OrderItemEntity sdoi
			, final OrderItemEntity sdoiTemplate, final OrderEntity sdo, final CustorderItemEntity coi) {
		// [[
		Object[][] params = {{"splitWrapper", splitWrapper},
							{"sdoi", sdoi},
							{"sdoiTemplate", sdoiTemplate},
							{"sdo", sdo},
							{"coi", coi}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "initLoadedSdoi", params);
		}
		// ]]
		try {
			// first initialize as a standard sdoi
			initSdoi(sdoi, sdoiTemplate, sdo, coi);
			
			final SapMaterialEntity sapMaterial = sdoi.getSapMaterial();
			final BigDecimal qtyInOrderedUom = BigDecimal.ZERO.add(coi.getOrderedQuantity());
			final UomBsEntity orderedUom = coi.getOrderedUom();
			
			// use converter only to obtain numerator and denominator
			final UomConversionResult convertToBaseUom = splitWrapper.convertQuantity(sapMaterial, orderedUom.getUomCd(), sapMaterial.getBaseUom().getUomCd(), qtyInOrderedUom);
			
			// set ordered quantity/UoM numerator and denominator
			sdoi.setOrderedQuantity(qtyInOrderedUom);
			sdoi.setOrderedUom(orderedUom);
			sdoi.setOrderedUomNumerator(convertToBaseUom.getNumerator());
			sdoi.setOrderedUomDenominator(convertToBaseUom.getDenominator());
			
			// set also the loaded quantity/UoM and numerator/denominator of service materials with the same values as ordered ones at order creation, see SDS of LRQ 017.1
			sdoi.setLoadedQuantity(qtyInOrderedUom);
			sdoi.setLoadedUom(orderedUom);
			sdoi.setLrUomNumerator(convertToBaseUom.getNumerator());
			sdoi.setLrUomDenominator(convertToBaseUom.getDenominator());
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
	 * Returns the latest available value, which is the most accurate to reality.
	 * @param bulkOrderItem
	 * @return
	 */
	private static BigDecimal getLatestQuantityInOrderedUom(OrderItemEntity orderItem, CustorderItemEntity custorderItem, CustorderSplitWrapper splitWrapper) {
		// [[
		BigDecimal result = null;
		Object[][] params = {{"orderItem", orderItem},
							{"custorderItem", custorderItem},
							{"splitWrapper", splitWrapper}};
		final boolean isEnteringExitingEnabled = LOG.isEnteringExitingEnabled();
		if (isEnteringExitingEnabled) {
			LOG.entering(CustorderUtils.class, "getLatestQuantityInOrderedUom", params);
		}
		// ]]
		try {
			if  (orderItem.getDeliveredQuantity() != null) {
				return result = splitWrapper.convertQuantity(orderItem.getSapMaterial(), orderItem.getDeliveredUom().getUomCd()
						, custorderItem.getOrderedUom().getUomCd(), orderItem.getDeliveredQuantity()).getQuantity();
			}
			else if (orderItem.getLoadedQuantity() != null){
				return result = splitWrapper.convertQuantity(orderItem.getSapMaterial(), orderItem.getLoadedUom().getUomCd()
						, custorderItem.getOrderedUom().getUomCd(), orderItem.getLoadedQuantity()).getQuantity();
			}
			else if (orderItem.getOrder().getOrderStatus().getOrderStatusCd().equals(Constant.ORDER_STATUS_DISPATCHED)) {
				BigDecimal truckNetW = getConnectedTruckNetWeight(orderItem, BigDecimal.ZERO);
				BigDecimal orderedQtyInCOrderUom = splitWrapper.convertQuantity(orderItem.getSapMaterial(), orderItem.getOrderedUom().getUomCd()
						, custorderItem.getOrderedUom().getUomCd(), orderItem.getOrderedQuantity()).getQuantity();
				BigDecimal orderedQtyInTons;
				if (Constant.UOM_TONS.equals(custorderItem.getOrderedUom().getUomCd())) {
					orderedQtyInTons = orderedQtyInCOrderUom;
				}
				else {
					orderedQtyInTons = splitWrapper.convertQuantity(orderItem.getSapMaterial(), orderItem.getOrderedUom().getUomCd()
							, Constant.UOM_TONS, orderItem.getOrderedQuantity()).getQuantity();
				}
				
				if (BigDecimal.ZERO.equals(truckNetW)) {
					result = orderedQtyInCOrderUom;
				}
				else if (truckNetW.compareTo(orderedQtyInTons) > 0) {
					result = orderedQtyInCOrderUom;
				}
				else {
					result = truckNetW;
				}
				return result;
			}
			else {
				return result = splitWrapper.convertQuantity(orderItem.getSapMaterial(), orderItem.getOrderedUom().getUomCd()
						, custorderItem.getOrderedUom().getUomCd(), orderItem.getOrderedQuantity()).getQuantity();
			}
		}
		finally {
		// [[
			if (isEnteringExitingEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * 
	 * @param bulkOrderItem
	 * @return
	 */
	private static void setExactQuantityForOrders(CustorderSplitWrapper splitWrapper, CustorderItemEntity custorderItem) {
		// [[
		Object[][] params = {{"splitWrapper", splitWrapper},
				{"custorderItem", custorderItem}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "setExactQuantityForOrders", params);
		}
		// ]]
		try {
			if (Constant.UOM_TONS.equals(custorderItem.getOrderedUom().getUomCd())) {
				if (custorderItem.getExactSdorderQtyFlag()) {
					ArrayList<OrderItemEntity> adjustableOrders = makePriorityGroupsOfOrders(splitWrapper);
					for (OrderItemEntity orderItem : adjustableOrders) {
						setExactQuantityForOrderItem(orderItem, splitWrapper, custorderItem);
					}
				}
				else if (custorderItem.getExactCustorderQtyFlag()) {
					ArrayList<OrderItemEntity> adjustableOrders = makePriorityGroupsOfOrders(splitWrapper);
					if (!adjustableOrders.isEmpty()) {
						OrderItemEntity lastOrderItem = adjustableOrders.get(0);
						setExactQuantityForOrderItem(lastOrderItem, splitWrapper, custorderItem);
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

	/**
	 * TODO
	 * 
	 * @param TODO
	 * @return TODO
	 */
	private static void setExactQuantityForOrderItem(OrderItemEntity orderItem, CustorderSplitWrapper splitWrapper, CustorderItemEntity custorderItem) {
		// [[
		Object[][] params = {{"orderItem", orderItem},
				{"splitWrapper", splitWrapper},
				{"custorderItem", custorderItem}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "setExactQuantityForOrderItem", params);
		}
		// ]]
		try {
			if (!Constant.UOM_TONS.equals(orderItem.getOrderedUom().getUomCd())) {
				BigDecimal orderedQtyInTons = splitWrapper.convertQuantity(orderItem.getSapMaterial(), 
						orderItem.getOrderedUom().getUomCd(), Constant.UOM_TONS, orderItem.getOrderedQuantity()).getQuantity();
				orderItem.setOrderedQuantity(orderedQtyInTons);
				orderItem.setOrderedUom(custorderItem.getOrderedUom());
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
	 * In the automated splitting/adjusting case, there is only one bulk
	 * material allowed per custorder. So there can only be one bulk material
	 * but several service materials. This method extracts this one bulk {@link OrderItemEntity}
	 * 
	 * @param o
	 */
	public static OrderItemEntity getBulkDumpOrPowderOrderItem(OrderEntity order) {
		OrderItemEntity bulkOrderItem = null;
		for (OrderItemEntity oi : (List<OrderItemEntity>)order.getOrderItems()) {
			if (LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(oi.getSapMaterial())) {
				if (bulkOrderItem != null) {
					String errMsg = "more than one bulk material found in customer order";
					LOG.error(errMsg);
					throw new LogonException(errMsg, Reason.RES_IMPL_BUG);
				}
				bulkOrderItem = oi;
			}
		}
		if (bulkOrderItem == null) {
			String errMsg = "no bulk material found on this order [" + order.getOrderNumber() + "]";
			LOG.error(errMsg);
			throw new LogonException(errMsg, Reason.RES_IMPL_BUG);
		}
		return bulkOrderItem;
	}
	
	/**
	 * In the automated splitting/adjusting case, there is only one bulk custorder
	 * item allowed per custorder. This method extracts this one
	 * {@link CustorderItemEntity}
	 * 
	 * @param o
	 */
	public static CustorderItemEntity getSingleBulkCustorderItem(CustorderEntity custorder) {
		CustorderItemEntity bulkCustorderItem = null;
		for (CustorderItemEntity coi : (List<CustorderItemEntity>)custorder.getCustorderItems()) {
			SapMaterialEntity sapMaterial = coi.getSapMaterial();
			if (LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(sapMaterial)) {	// get the bulk COI
				if (bulkCustorderItem != null) {
					String errMsg = "more than one custorder item found in custorder [" + custorder.getCustorderNumber() + "]";
					LOG.error(errMsg);
					throw new LogonException(errMsg, Reason.RES_IMPL_BUG);
				}
				bulkCustorderItem = coi;
			}
		}
		if (bulkCustorderItem == null) {
			String errMsg = "no bulk custorder item found in custorder [" + custorder.getCustorderNumber() + "]";
			LOG.error(errMsg);
			throw new LogonException(errMsg, Reason.RES_IMPL_BUG);
		}
		return bulkCustorderItem;
	}
	
	/**
	 * Returns all CustorderItems which are services.
	 * @param custorder
	 * @return a list of service CustorderItems
	 */
	public static List<CustorderItemEntity> getServiceCustorderItems(CustorderEntity custorder) {
		ArrayList<CustorderItemEntity> serviceCustorderItems = new ArrayList<CustorderItemEntity>();
		for (CustorderItemEntity coi : (List<CustorderItemEntity>)custorder.getCustorderItems()) {
			SapMaterialEntity sapMaterial = coi.getSapMaterial();
			if (LogonPackageHandlingUtil.isService(sapMaterial)) {
				serviceCustorderItems.add(coi);
			}
		}
		return serviceCustorderItems;
	}
	
	public static List<SplittedOrderData> getSplittedOrderData(ShiptoEntity shipTo,
			CustorderEntity custorder, TimeZone shiptoTimeZone) {
		// [[
		List<SplittedOrderData> result = null;
		Object[][] params = {{"shipTo", shipTo}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "getSplittedOrderData", params);
		}
		// ]]
		try {
			
			result = new ArrayList<SplittedOrderData>();
			
			List<OrderEntity> orders = custorder.getOrders();
			if (orders != null) {
				for (int i=0; i < orders.size(); i++) {
					OrderEntity order = orders.get(i);
					SplittedOrderData.DeliveryType deliveryType = LogonUtil.getDeliveryType(order);
					Boolean exactCustorderQtyFlag = CustorderUtils.getExactCustorderQty(custorder);
					SplittedOrderData splittedOrderData = new SplittedOrderData(order, shipTo,
							deliveryType, exactCustorderQtyFlag, i, shiptoTimeZone);
					result.add(splittedOrderData);
				}
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
	 * Returns the difference between two @CustorderEntity
	 * @param oldCustorder
	 * @param newCustorder
	 * @return the difference split wrapper
	 */
	public static CustorderSplitDiffWrapper getCustorderDiff(CustorderEntity oldCustorder, CustorderEntity newCustorder) {
		// [[
		CustorderSplitDiffWrapper result = null;
		Object[][] params = {{"oldCustorder", oldCustorder},
							{"newCustorder", newCustorder}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "getCustorderDiff", params);
		}
		// ]]
		try {
			
			List<OrderEntity> oldOrders = oldCustorder.getOrders();
			List<OrderEntity> newOrders = newCustorder.getOrders();
			
			CustorderSplitDiffWrapper diff = new CustorderSplitDiffWrapper();
			List<OrderEntity> tobeCreatedOrders = diff.getTobeCreatedOrders();
			List<OrderEntity> tobeDeletedOrders = diff.getTobeDeletedOrders();
			List<OrderSplitDiffWrapper> tobeEditedOrders = diff.getTobeEditedOrders();
			
			for (OrderEntity newOrder : newOrders) {
				
				OrderEntity oldOrder = getMatchingOldOrder(newOrder.getOrderId(), oldOrders); 
				
				// If no order on the db found, the one we have here has just been created
				if (oldOrder == null) {
					tobeCreatedOrders.add(newOrder);
					continue;
				}
				
				// every order needs to have a status (null not possible)
				OrderStatusBsEntity newOrderStatus = newOrder.getOrderStatus();
				if (newOrderStatus.getOrderStatusCd().equals(Constant.ORDER_STATUS_DELETED)) {
					OrderStatusBsEntity oldOrderStatus = oldOrder.getOrderStatus();
					if (oldOrderStatus == null) {
						String errMsg = "No matching order found for order ["+newOrder.getOrderNumber()+":"+newOrder.getOrderId()+"]";
						LOG.error(errMsg);
						throw new LogonException(errMsg, Reason.RES_IMPL_BUG);
					}
					if (!oldOrderStatus.getOrderStatusCd().equals(Constant.ORDER_STATUS_DELETED)) {
						tobeDeletedOrders.add(newOrder);
						continue;
					}
				}

				OrderSplitDiffWrapper orderDiff = getDiffIfChanged(newOrder, oldOrder);
				// null means: no changes applied
				if (orderDiff != null) {
					tobeEditedOrders.add(orderDiff);
				}
			} // end for-loop
			
			return result = diff;
			
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
	 * Returns the order entity which matches to the one, existing on the DB.
	 * @param newOrderId 
	 * @param oldOrders 
	 * @return the existing order entity
	 */
	private static OrderEntity getMatchingOldOrder(Long newOrderId, List<OrderEntity> oldOrders) {
		for(OrderEntity o : oldOrders) {
			Long oldOrderId = o.getOrderId();
			if(oldOrderId.equals(newOrderId)) {
				return o;
			}
		}
		return null;
	}

	/**
	 * Returns the difference within the wrapper, which contains new and old quanities and uom
	 * @param newOrder
	 * @param oldOrder
	 * @return null if not changed, or the wrapper which contains new and old quanities and uom
	 */
	private static OrderSplitDiffWrapper getDiffIfChanged(OrderEntity newOrder, OrderEntity oldOrder) {
		OrderItemEntity oldOrderItem = (OrderItemEntity) oldOrder.getOrderItems().get(0); // There has to be one and only one bulk item
		OrderItemEntity newOrderItem = (OrderItemEntity) newOrder.getOrderItems().get(0); // There has to be one and only one bulk item
		
		BigDecimal oldOrderedQty = oldOrderItem.getOrderedQuantity();
		BigDecimal newOrderedQty = newOrderItem.getOrderedQuantity();
		String oldUom = oldOrderItem.getOrderedUom().getUomCd();
		String newUom = newOrderItem.getOrderedUom().getUomCd();
				
		// null means: they are not different and have not to be changed though
		if (oldOrderedQty.equals(newOrderedQty) && oldUom.equals(newUom)) {
			return null;
		}
		
		OrderSplitDiffWrapper orderDiff = new OrderSplitDiffWrapper();
		orderDiff.setOrder(newOrder);
		orderDiff.setOldOrderedQty(BigDecimal.ZERO.add(oldOrderedQty.setScale(Constant.SCALE, BigDecimal.ROUND_HALF_UP).stripTrailingZeros()));
		orderDiff.setOldOrderedUom(oldUom);
		orderDiff.setNewOrderedQty(BigDecimal.ZERO.add(newOrderedQty.setScale(Constant.SCALE, BigDecimal.ROUND_HALF_UP).stripTrailingZeros()));
		orderDiff.setNewOrderedUom(newUom);

		return orderDiff;
	}
	


	/**
	 * @param custOrderEntity
	 * @param deliverySettingDTO
	 */
	public static void setupCustorderAttributes(CustorderEntity custOrderEntity,
			DeliverySettingDTO deliverySettingDTO) {
		
		custOrderEntity.setBilltoSapBp(deliverySettingDTO.getBilltoSapBp());		
		custOrderEntity.setPayerSapBp(deliverySettingDTO.getPayerSapBp());
		custOrderEntity.setFreeDeliveryFlag(deliverySettingDTO.getFreeDeliveryFlag());
		custOrderEntity.setShippingpoint(deliverySettingDTO.getShippingpoint()); // nullable
		custOrderEntity.setReleaseSapFlag(deliverySettingDTO.getReleaseSapFlag());
		custOrderEntity.setPool(deliverySettingDTO.getPool()); // nullable
		custOrderEntity.setCommCarrier(deliverySettingDTO.getCommercialCarrier()); // nullable
		custOrderEntity.setExecCarrier(deliverySettingDTO.getExecutingCarrier()); // nullable
		
		custOrderEntity.setDeliveryTimeTolerance(deliverySettingDTO.getDeliveryTimeTolerance());
		custOrderEntity.setDeliveryTimeToleranceUom(deliverySettingDTO.getDeliveryTimeToleranceUomCd());
		custOrderEntity.setDeliveryFreqEveryTime(deliverySettingDTO.getDeliveryFreqEveryTime());
		custOrderEntity.setDeliveryFreqEveryTimeUom(deliverySettingDTO.getDeliveryFreqEveryTimeUomCd());
		custOrderEntity.setDeliveryFreqQtyperhre(deliverySettingDTO.getDeliveryFreqQtyperhre());
		custOrderEntity.setDeliveryFreqQtyperhreUom(deliverySettingDTO.getDeliveryFreqQtyperhreUom());
		
		// Special Order Indicators
		custOrderEntity.setOpenCustorderFlag(deliverySettingDTO.getOpenCustorderFlag());
		custOrderEntity.setOnHoldOrderAndLockedFlag(deliverySettingDTO.getOnHoldOrderAndLockedFlag());
		custOrderEntity.setSapSuppressMinChargeFlag(deliverySettingDTO.getSapSuppressMinChargeFlag());
		custOrderEntity.setFreeDeliveryFlag(deliverySettingDTO.getFreeDeliveryFlag());
		custOrderEntity.setReleaseSapFlag(deliverySettingDTO.getReleaseSapFlag());
	}

	/**
	 * @param deliverySettingDTO
	 * @param custorderItemEntity
	 */
	public static void setupCustorderItemAttributes(DeliverySettingDTO deliverySettingDTO,
			CustorderItemEntity custorderItemEntity) {
		
		custorderItemEntity.setLoadingpoint(deliverySettingDTO.getLoadingpoint());
		custorderItemEntity.setSapMaterial(deliverySettingDTO.getSapMaterial());
		custorderItemEntity.setExactCustorderQtyFlag(deliverySettingDTO.getExactCustorderQtyFlag());
		custorderItemEntity.setExactSdorderQtyFlag(deliverySettingDTO.getExactSdorderQtyFlag());
		custorderItemEntity.setPerSdorderQuantity(deliverySettingDTO.getPerSdorderQuantity());
		custorderItemEntity.setPerSdorderUom(deliverySettingDTO.getPerSdorderUom());
		custorderItemEntity.setShiptoSilo(deliverySettingDTO.getShiptoSilo()); // nullable
		custorderItemEntity.setProductionPlant(deliverySettingDTO.getProductionPlant());
	}

	/**
	 * @param custorderEntity
	 * @param deliverySetting
	 * @return
	 */
	public static CustorderItemEntity addNewCustOrderItem(CustorderEntity custOrderEntity,
			DeliverySettingDTO deliverySettingDTO) {

		CustorderItemEntity custorderItemEntity = new CustorderItemEntity();
		
		CustorderUtils.setupCustorderItemAttributes(deliverySettingDTO, custorderItemEntity);
		

		OrderUtils.setContractDependentAttributes(custOrderEntity, custorderItemEntity, deliverySettingDTO.getSapContractItem());
						
		final long orderItemNo = custOrderEntity.getCustorderItems() != null ? (custOrderEntity.getCustorderItems().size() + 1) * 10 : Constant.ORDER_FIRST_ITEM_CD;
		custorderItemEntity.setCustorderItemNo( orderItemNo );
		
		custorderItemEntity.setCustorder(custOrderEntity);
		custOrderEntity.getCustorderItems().add(custorderItemEntity);
		return custorderItemEntity;
	}
	
	/**
	 * Returns the value of the first @link{CustorderItemEntity} exactCustorderQtyFlag
	 * <b>This mehtod is only valid until the @link{CustorderItemEntity}s have the same value for the flag</b> 
	 * @param custorderEntity
	 */
	public static Boolean getExactCustorderQty(CustorderEntity custorderEntity) {
		// [[
		Boolean result = false;
		Object[][] params = {{"custorderEntity", custorderEntity}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "exactCustorderQty", params);
		}
		// ]]
		try {
			
			List<CustorderItemEntity> custorderItems = custorderEntity.getCustorderItems();
			if (custorderItems == null || custorderItems.isEmpty()) {
				return false;
			}
			
			CustorderItemEntity custorderItem = custorderItems.get(0);
			
			return result = custorderItem.getExactCustorderQtyFlag();
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	
	public static BigDecimal calculatePerSingleDeliveryQuantityInTonsForBagCase(CustorderSplitWrapper splitWrapper) {
		// [[
		BigDecimal result = null;
		Object[][] params = {{"splitWrapper", splitWrapper}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "calculatePerSingleDeliveryQuantityInTonsForBagCase", params);
		}
		// ]]
		try {
			List<CustorderItemEntity> cois = splitWrapper.getMaterialCois();
			
			for (CustorderItemEntity coi : cois) {
				SapMaterialEntity sapMaterial = coi.getSapMaterial();
				
				if (LogonPackageHandlingUtil.isService(sapMaterial)) {
					continue;
				}
				
				BigDecimal perSdoQty = coi.getPerSdorderQuantity();
				UomBsEntity perSdoUom = coi.getPerSdorderUom();
				
				if (perSdoQty != null && perSdoUom != null) {
					try {
						result = splitWrapper.convertQuantity(sapMaterial, perSdoUom.getUomCd(), Constant.UOM_TONS, perSdoQty).getQuantity();
					}
					catch (LogonException e) {
						if (logDebugEnabled) {
							LOG.debug("SAP material (non service) could not convert to Per Single Delivery UoM. id: " + sapMaterial.getSapMaterialId());
						}
						result = null;
					}
					
					if (result != null) {
						if (logDebugEnabled) {
							LOG.debug("Per Single Delivery Quantity is " + result.toPlainString() + " tons, using material specific converter, material ID: " + sapMaterial.getSapMaterialId());
						}
						return result;
					}
				}
			}
			
			result = splitWrapper.getVehicle().getMaxGrossWeightT();
			if (logDebugEnabled) {
				LOG.debug("Per Single Delivery Quantity is " + result.toPlainString() + " tons, using vehicle max. gross weight.");
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
	 * Creates as many {@link SplittedOrderItemData} as the count of the  orderItems
	 */
	public static List<SplittedOrderItemData> populateSplittedOrdetItems(List<OrderItemEntity> orderItems)  {
		
		List<SplittedOrderItemData> splittedOrderDatas = new ArrayList<SplittedOrderItemData>();
		
		int itemIndex = 0;
		
		for (OrderItemEntity orderItemEntity : orderItems) {
			// Check for the material type
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
			splittedOrderDatas.add(new SplittedOrderItemData(orderItemEntity, itemIndex++, materialType));
		}
		
		return splittedOrderDatas;
		
	}

	/**
	 * @param qtyPerMaterial
	 * @param splittedOrderItemDatas
	 */
	public static void calculateQtyPerMaterial(CustorderSplitWrapper wrapper,
			Map<Long, BigDecimal> qtyPerMaterial, List<SplittedOrderItemData> splittedOrderItemDatas) {
		for (SplittedOrderItemData splittedOrderItemData : splittedOrderItemDatas) {
			OrderItemEntity orderItem = splittedOrderItemData.getOrderItem();
			// Sum up the quantities per material
			Long sapMaterialId = orderItem.getSapMaterial().getSapMaterialId();
			if (qtyPerMaterial.containsKey(sapMaterialId)) {
				BigDecimal currentQuantity = qtyPerMaterial.get(sapMaterialId);
				BigDecimal baseItemOrderedQuantity = getBaseUomOrderedQty(wrapper, orderItem); 
				BigDecimal newValue = currentQuantity.add(baseItemOrderedQuantity);
				qtyPerMaterial.put(sapMaterialId, newValue);
			} 
			else {
				BigDecimal itemOrderedQuantity = getBaseUomOrderedQty(wrapper, orderItem);
				qtyPerMaterial.put(sapMaterialId, itemOrderedQuantity);
			}
		}
	}

	/**
	 * Ordered quantity in base unit = OrderedQty * Nominator / denominator 
	 */
	public static BigDecimal getBaseUomOrderedQty(CustorderSplitWrapper wrapper, OrderItemEntity orderItemEntity) {

		SapMaterialEntity sapMaterial = orderItemEntity.getSapMaterial();
		String fromUomCd = orderItemEntity.getOrderedUom().getUomCd();
		String toUomCd = orderItemEntity.getSapMaterial().getBaseUom().getUomCd();
		BigDecimal quantity = orderItemEntity.getOrderedQuantity();
		
		BigDecimal convertedQty = wrapper.convertQuantity(sapMaterial, fromUomCd, toUomCd, quantity).getQuantity();
		
		return convertedQty;
	}
	
	/**
	 * @param request
	 * @param custorder
	 * @param qtyPerMaterial
	 * @return
	 */
	public static Set<MaterialColumn> generateMaterialColumns(Locale locale,
			CustorderEntity custorder, Map<Long, BigDecimal> qtyPerMaterial,
			DispokarteService dispokarteService) {
		Set<SapMaterialEntity> collectedMaterials = OrderUtils.collectAllMaterialsForOrders(custorder.getOrders());
		Set<MaterialColumn> materialColumns = new HashSet<MaterialColumn>();
		for (SapMaterialEntity sapMaterialEntity : collectedMaterials) {
			Long sapMaterialId = sapMaterialEntity.getSapMaterialId();
			SapMaterialDescriptionEntity materialDescription = dispokarteService.fetchSapMaterialDescription(
					sapMaterialId, locale.getLanguage());
			// Material column created from the current sum of qty per material and the description for the locale 
			BigDecimal orderedQuantity = qtyPerMaterial.get(sapMaterialId).stripTrailingZeros();
			MaterialColumn materialColumn = new MaterialColumn(materialDescription,
					sapMaterialEntity, orderedQuantity);
			materialColumns.add(materialColumn);
		}
		return materialColumns;
	}
	
	/**
	 * Adds missing {@link OrderItemEntity} objects to the <b>orderItems</b> where there is one missing based
	 *  on the available materials in the {@link CustorderItemEntity} list.
	 *  <b>New elements will be added with zero quantity!</b>
	 *  @param orderItems - list to enrich
	 */
	public static void addMissingOrderItemsBasedOnCOI(CustorderSplitWrapper wrapper,
			OrderEntity order, List<OrderItemEntity> orderItems,
			List<CustorderItemEntity> custorderItems) {
		
		Iterator<CustorderItemEntity> iterator = custorderItems.iterator();
		while (iterator.hasNext()) {
			
			boolean missing = true;
			
			CustorderItemEntity next = iterator.next();
			for (OrderItemEntity orderItemEntity : orderItems) {
				if (next.getSapMaterial().getSapMaterialId().equals(orderItemEntity.getSapMaterial().getSapMaterialId())) {
					missing = false;
					break;
				}
			}
			
			if (missing) {
				OrderItemEntity orderItemEntity = new OrderItemEntity();
				initSdoi(orderItemEntity, wrapper.getSdoiTemplate(), order, next);
				orderItemEntity.setOrderedQuantity(BigDecimal.ZERO);
				orderItemEntity.setSapMaterial(next.getSapMaterial());
			}
			
		}
	}
	
	public static Set<LogonPackageHandlingUtil.PackageType> separateServiceAndMaterialCustorderItems(final CustorderEntity custorder, List<CustorderItemEntity> materialCois, List<CustorderItemEntity> serviceCois) {
		// [[
		Set<LogonPackageHandlingUtil.PackageType> result = null;
		Object[][] params = {{"custorder", custorder},
							{"materialCois", materialCois},
							{"serviceCois", serviceCois}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "separateServiceAndMaterialCustorderItems", params);
		}
		// ]]
		try {
			if (custorder == null) {
				String errMsg = "input custorder is null";
				LOG.error(errMsg);
				throw new IllegalArgumentException(errMsg);
			}
			else if (materialCois == null) {
				String errMsg = "input materialCois is null";
				LOG.error(errMsg);
				throw new IllegalArgumentException(errMsg);
			}
			else if (serviceCois == null) {
				String errMsg = "input serviceCois is null";
				LOG.error(errMsg);
				throw new IllegalArgumentException(errMsg);
			}
			
			result = new HashSet<LogonPackageHandlingUtil.PackageType>();
			
			Map<LogonPackageHandlingUtil.PackageType, List<CustorderItemEntity>> packageTypeCustorderItemMap = LogonPackageHandlingUtil.getPackageTypeCustorderItemMap(custorder);
			for (LogonPackageHandlingUtil.PackageType packageType : packageTypeCustorderItemMap.keySet()) {
				final List<CustorderItemEntity> tmpCois = packageTypeCustorderItemMap.get(packageType);
				if (tmpCois != null && !tmpCois.isEmpty()) {
					result.add(packageType);
					
					if (LogonPackageHandlingUtil.isMaterialType(packageType)) {	// after validation only one of them is expected!
						materialCois.addAll(tmpCois);
					}
					else if (LogonPackageHandlingUtil.PackageType.SERVICE.equals(packageType)) {
						serviceCois.addAll(tmpCois);
					}
					else {
						String errMsg = "SAP material is neither service nor standard material, packageType: " + packageType;
						LOG.error(errMsg);
						throw new LogonException(errMsg, Reason.RES_ERROR_IMPL);
					}
				}
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
	 * Detach the orders witch are not persisted from the given <code>orders</code> list.
	 * In this case the "detach" means, remove it from the orders list.
	 * 
	 * @param orders
	 * 			A list of orders {@link List <@link OrderEntity}> }
	 * 
	 * @return
	 * 			List of detached orders.
	 */
	public static List<OrderEntity> detachNewOrders(List<OrderEntity> orders) {
		// [[
		List<OrderEntity> result = null;
		Object[][] params = {{"orders", orders}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "detachNewOrders", params);
		}
		// ]]
		try {		
			if (orders == null || orders.isEmpty()) {
				LOG.warn("The orders list is <NULL> or empty!");
				return result;
			}
			
			result = new ArrayList<OrderEntity>();		
			final Iterator<OrderEntity> ordersIterator = orders.iterator();
			while (ordersIterator.hasNext()) {
				
				OrderEntity order = ordersIterator.next();
				if (order.getOrderId() == null) {
					ordersIterator.remove();
					
					result.add(order);
				}
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
	
	public static void initServiceOnlyCustomerOrder(CustorderEntity custorder, final RefDataGenericsServiceLocalInterface refdataService) {
		// [[
		Object[][] params = {{"custorder", custorder},
							{"refdataService", refdataService}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "initServiceOnlyCustomerOrder", params);
		}
		// ]]
		try {
			custorder.setDeliveryType(refdataService.findBsEntity(DeliveryTypeBsEntity.class, Constant.DELIVERY_TYPE_SERVICE));
			custorder.setPool(null);
			custorder.setCommCarrier(null);
			custorder.setDeliveryFreqEveryTime(null);
			custorder.setDeliveryFreqEveryTimeUom(null);
			custorder.setDeliveryFreqQtyperhre(null);
			custorder.setDeliveryFreqQtyperhreUom(null);
			custorder.setDeliveryTimeTolerance(null);
			custorder.setDeliveryTimeToleranceUom(null);
			custorder.setNoteDispatching(null);
			custorder.setNoteTruckDriver(null);
			custorder.setSapSuppressMinChargeFlag(Boolean.FALSE);
			
			if (custorder.getCustorderItems() != null) {
				for (CustorderItemEntity coi : custorder.getCustorderItems()) {
					coi.setExactCustorderQtyFlag(false);
					coi.setExactSdorderQtyFlag(false);
					coi.setLoadingpoint(null);
					coi.setMixDesign(null);
					coi.setPerSdorderQuantity(null);
					coi.setPerSdorderUom(null);
					coi.setShiptoSilo(null);
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
	
	public static void initReturnPickupCustomerOrder(CustorderEntity custorder, final RefDataGenericsServiceLocalInterface refdataService) {
		// [[
		Object[][] params = {{"custorder", custorder},
							{"refdataService", refdataService}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(CustorderUtils.class, "initReturnPickupCustomerOrder", params);
		}
		// ]]
		try {
			custorder.setDeliveryType(refdataService.findBsEntity(DeliveryTypeBsEntity.class, Constant.DELIVERY_TYPE_RETURN_PICKUP));
			custorder.setDeliveryFreqEveryTime(null);
			custorder.setDeliveryFreqEveryTimeUom(null);
			custorder.setDeliveryFreqQtyperhre(null);
			custorder.setDeliveryFreqQtyperhreUom(null);
			custorder.setDeliveryTimeTolerance(null);
			custorder.setDeliveryTimeToleranceUom(null);
			custorder.setNoteDispatching(null);
			custorder.setNoteTruckDriver(null);
			if (custorder.getSapSuppressMinChargeFlag() == null) {
				custorder.setSapSuppressMinChargeFlag(Boolean.FALSE);
			}
			
			if (custorder.getCustorderItems() != null) {
				for (CustorderItemEntity coi : custorder.getCustorderItems()) {
					coi.setExactCustorderQtyFlag(false);
					coi.setExactSdorderQtyFlag(true);
					coi.setMixDesign(null);
					coi.setPerSdorderQuantity(null);
					coi.setPerSdorderUom(null);
					coi.setShiptoSilo(null);
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
	 * Helper method to determine whether the custorderEntity has any custorderItem yet,
	 * or is it totally new without any items.
	 * 
	 * @param custorderEntity
	 * @return
	 */
	public static boolean containsCustorderItem(CustorderEntity custorderEntity) {
		return (custorderEntity != null &&
				custorderEntity.getCustorderItems().size() > 0);
	}
	
}
