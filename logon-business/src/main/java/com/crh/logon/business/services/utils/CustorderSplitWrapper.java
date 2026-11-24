/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.ordercreation.dto.WagonTypeWrapper;
import com.crh.logon.business.services.beans.UomConversionResult;
import com.crh.logon.business.services.beans.UomConverterBean;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.ordercreation.dto.SimpleVehicleTypeWrapper;
import com.crh.logon.business.ordercreation.dto.TruckTypeWrapper;
import com.crh.logon.business.ordercreation.dto.TruckWagonTypeWrapper;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.CustorderItemEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.refdata.ArchivingStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.CustorderStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialAltUomEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
@AdnTrace
public class CustorderSplitWrapper implements Serializable {

	private static final boolean CONVERSION_SUCEEDED_OR_NOT_YET_HAPPENED = false;

	private static final String CANNOT_CONVERT_TO_TONS_WARNING_MESSAGE = "SAP material could not convert to tons. Material id: ";

	private static final long serialVersionUID = 871712975268881248L;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(CustorderSplitWrapper.class);

	public static final String SPLIT_FAILED_GENERAL_DEBUG_MESSAGE = "Could not obtain split metadata";

	private static final boolean CONVERSION_FAILED = true;
	
	public final int numOfMaxOrders;
	public final int defaultWeightTolerance;

	/* serves as an @{UomBsEntity} (refdata) cache, it maps the UOM_CD to the related UomBsEntity */
	private final Map<String, UomBsEntity> uomCdUomEntityCache;
	
	/* contains UoM converters which are tailored for this customer order */
	private final Map<Long, Map<String, UomConverterBean>> custorderSpecificUomConverters;
	private final CustorderEntity custorder;
	private final List<CustorderItemEntity> materialCois;
	private final List<CustorderItemEntity> serviceCois;
	private final TruckWagonTypeWrapper vehicle;
	private final UomBsEntity fullVehicleUom;
	private final LogonPackageHandlingUtil.PackageType packageType;
	private final DeliveryTypeBsEntity deliveryType;
	private final boolean isPalletOnlyOrder;

	/* contains a map of all order status as refdata */
	private final Map<Long, OrderStatusBsEntity> orderStatusEntityListCache;
	
	/* contains a map of all order status as refdata */
	private final Map<Long, ArchivingStatusBsEntity> archivingStatusEntityListCache;
	
	/* contains a map of all custorder status as refdata */
	private final Map<Long, CustorderStatusBsEntity> custorderStatusEntityListCache;

	private OrderEntity sdoTemplate;
	private OrderItemEntity sdoiTemplate;
	private Map<OrderEntity, Boolean> orderManuallyScheduled;

	// processed and validated values
	private UomBsEntity _orderedUom;
	private BigDecimal _remainingQtyInOrderedUom;
	private BigDecimal _perSdoQtyInOrderedUom;
	private BigDecimal _vehicleMaxLoadInOrderedUom;
	private UomBsEntity _fullVehicleUom;
	private double _deliveryFqyInMinutes;
	private Date _deliveryToDate;
	private int _numOfDeliveries;
	private String _numOfDeliveriesStr;
	private List<VehicleMaterialPlanner> _vehicleMaterialPlanners;

	/**
	 * This is used to indicate error happened during quantity conversion.
	 */
	private boolean conversionFailed = CONVERSION_SUCEEDED_OR_NOT_YET_HAPPENED;

	private CustorderSplitWrapper(CustorderEntity customerOrder, TruckWagonTypeWrapper vehicle, ReadOnlyDao<SapMaterialAltUomEntity> sapMatAltDao
			, RefDataGenericsServiceLocalInterface refdataService, ConfigBean configBean, OrderServiceLocalInterface orderService) {
		boolean logDebugEnabled = LOG.isDebugEnabled();

		CustorderUtils.validateCustorder(customerOrder);
		
		this.custorder = customerOrder;
		this.vehicle = vehicle;
		this.custorderSpecificUomConverters = new HashMap<Long, Map<String, UomConverterBean>>();
		this.uomCdUomEntityCache = new HashMap<String, UomBsEntity>();
		this.materialCois = new ArrayList<CustorderItemEntity>();
		this.serviceCois = new ArrayList<CustorderItemEntity>();
		this.numOfMaxOrders = configBean.getAppDefaultCustorderMaxNumOfOrders();
		this.defaultWeightTolerance = configBean.getAppDefaultSareaExactQuantityOrderTolerance();
		this.deliveryType = customerOrder.getDeliveryType();
		
		this.orderManuallyScheduled = new HashMap<OrderEntity, Boolean>();
		List<OrderEntity> sdOrders = customerOrder.getOrders();
		if (sdOrders != null) {
			for (Object o : sdOrders) {
				OrderEntity order = (OrderEntity)o;
				if ( order != null ) {
					AssignmentPartEntity assignmentPartUnload = orderService.getAssignmentPartUnload(order);
					if (assignmentPartUnload != null && assignmentPartUnload.getAssignment() != null) {
						AssignmentEntity assignment = assignmentPartUnload.getAssignment();
						Boolean manuallyScheduled = assignment.getManuallyScheduled();
						orderManuallyScheduled.put(order, manuallyScheduled);
					}
				}
			}
		}
		
		if (logDebugEnabled) {
			LOG.debug("Maximum number of Single Delivery Orders is configured to " + this.numOfMaxOrders);
		}
		
		Set<LogonPackageHandlingUtil.PackageType> packageTypes =
				CustorderUtils.separateServiceAndMaterialCustorderItems(
						this.custorder, this.materialCois, this.serviceCois);
		
		if (packageTypes == null || packageTypes.isEmpty()) {
			String errMsg = "incorrect validation (no package type(s))";
			LOG.error(errMsg);
			throw new LogonException(errMsg, Reason.RES_ERROR_IMPL);
		}
		LogonPackageHandlingUtil.PackageType tmpPackageType = null;
		if (packageTypes.size() == 1 && packageTypes.contains(LogonPackageHandlingUtil.PackageType.SERVICE)) {
			// service only order (LRQ 070.3 / LOGON-4743)
			tmpPackageType = LogonPackageHandlingUtil.PackageType.SERVICE;
		}
		else {
			for (LogonPackageHandlingUtil.PackageType packageTypeFromSet : packageTypes) {
				if (LogonPackageHandlingUtil.PackageType.SERVICE.equals(packageTypeFromSet)) {
					continue;
				}
				tmpPackageType = packageTypeFromSet;
				break;
			}
		}
		
		this.packageType = tmpPackageType;

		if (this.packageType == null) {
			String errMsg = "incorrect validation (package type couldn't be obtained)";
			LOG.error(errMsg);
			throw new LogonException(errMsg, Reason.RES_ERROR_IMPL);
		}
		
		// LOGON doesn't differentiate bag and pallet packaging types (both are mapped to bag) so an additional check is needed
		// to identify pallet only orders (like return pallets) 
		if (LogonPackageHandlingUtil.PackageType.BAG.equals(this.packageType)) {
			boolean isAllMaterialPallet = true;
			for (CustorderItemEntity coi : this.materialCois) {
				if (!LogonPackageHandlingUtil.isPallet(coi.getSapMaterial())) {
					isAllMaterialPallet = false;
					break;
				}
			}
			this.isPalletOnlyOrder = isAllMaterialPallet;
		}
		else {
			this.isPalletOnlyOrder = false;
		}
		
		// initialize uomCdUomBsCache
		List<UomBsEntity> uomBsEntities = refdataService.findBsEntityList(UomBsEntity.class);
		for (UomBsEntity uomBsEntity : uomBsEntities) {
			uomCdUomEntityCache.put(uomBsEntity.getUomCd(), uomBsEntity);
		}

		this.orderStatusEntityListCache = new HashMap<Long, OrderStatusBsEntity>();
		List<OrderStatusBsEntity> orderStatusBsList = refdataService.findBsEntityList(OrderStatusBsEntity.class);
		for (OrderStatusBsEntity orderStatusBs : orderStatusBsList) {
			orderStatusEntityListCache.put(orderStatusBs.getOrderStatusCd(), orderStatusBs);
		}
		
		this.archivingStatusEntityListCache = new HashMap<Long, ArchivingStatusBsEntity>();
		List<ArchivingStatusBsEntity> archivingStatusList = refdataService.findBsEntityList(ArchivingStatusBsEntity.class);
		for (ArchivingStatusBsEntity archivingStatusBsEntity : archivingStatusList) {
			archivingStatusEntityListCache.put(archivingStatusBsEntity.getArchivingStatusCd(), archivingStatusBsEntity);
		}
		
		this.custorderStatusEntityListCache = new HashMap<Long, CustorderStatusBsEntity>();
		List<CustorderStatusBsEntity> custorderStatusBsList = refdataService.findBsEntityList(CustorderStatusBsEntity.class);
		for (CustorderStatusBsEntity custorderStatusBs : custorderStatusBsList) {
			custorderStatusEntityListCache.put(custorderStatusBs.getCustorderStatusCd(), custorderStatusBs);
		}

		// initialize full vehicle UoM
		if (vehicle instanceof TruckTypeWrapper) {
			fullVehicleUom = uomCdUomEntityCache.get(Constant.UOM_LKW);
		}
		else if (vehicle instanceof WagonTypeWrapper) {
			fullVehicleUom = uomCdUomEntityCache.get(Constant.UOM_WAGON);
		}
		else if (vehicle instanceof SimpleVehicleTypeWrapper) {
			fullVehicleUom = uomCdUomEntityCache.get(Constant.UOM_LKW);
		}
		else {
			String errMsg = "unknown TruckWagonTypeWrapper subclass";
			LOG.error(errMsg);
			throw new LogonException(errMsg, Reason.RES_ERROR_IMPL);
		}
		
		// initialize material specific converters
		List<CustorderItemEntity> customerOrderItems = customerOrder.getCustorderItems();
		for (CustorderItemEntity customerOrderItem : customerOrderItems) {
			SapMaterialEntity sapMaterial = customerOrderItem.getSapMaterial();
			UomBsEntity defaultUnitOfMeasure = sapMaterial.getBaseUom();
			
			// add default converters (for each UomBsEntity in the cache create converter to its dimension if possible)
			for (UomBsEntity unitOfMeasure : uomCdUomEntityCache.values()) {
				if (unitOfMeasure.getNumeratorForConversion() != null && unitOfMeasure.getDenominatorForConversion() != null && unitOfMeasure.getUomDimension() != null) {
					Long numerator = unitOfMeasure.getNumeratorForConversion();
					Long denumerator = unitOfMeasure.getDenominatorForConversion();
					addUomConverter(sapMaterial, unitOfMeasure.getUomCd(), unitOfMeasure.getUomDimension().getUomDimensionCd(), numerator, denumerator);
				}
			}
			
			// find alternative UoMs of the materials and create converters for it
			Filter<SapMaterialAltUomEntity> sapMatAltFilter = sapMatAltDao.createFilter();
			sapMatAltFilter.addExpression(Restrictions.eq(SapMaterialAltUomEntity.ATTR_SAP_MATERIAL, sapMaterial));
			
			List<SapMaterialAltUomEntity> sapMatAltUoms =  sapMatAltDao.find(sapMatAltFilter);
			String defaultUnitOfMeasureCode = defaultUnitOfMeasure.getUomCd();
			if (sapMatAltUoms != null && !sapMatAltUoms.isEmpty()) {
				for (SapMaterialAltUomEntity sapMatAltUom : sapMatAltUoms) {
					Long numerator = sapMatAltUom.getNumeratorForConversion();
					Long denominator = sapMatAltUom.getDenominatorForConversion();
					
					if (numerator == null || denominator == null || numerator <= 0 || denominator <= 0) {
						String errMsg = "invalid SapMaterialAltUomEntity, id: " + sapMatAltUom.getSapMaterialAltUomId();
						LOG.error(errMsg);
						throw new LogonException(errMsg, Reason.RES_ERROR_IMPL);
					}
					
					UomBsEntity alternativeUnitOfMeasure = sapMatAltUom.getUom();
					String alternativeUnitOfMeasureCode = alternativeUnitOfMeasure.getUomCd();
					this.addUomConverter(sapMaterial, alternativeUnitOfMeasureCode, defaultUnitOfMeasureCode,
							numerator, denominator);
				}
			}
			
			// add vehicle specific converter
			String vehicleUomCd = fullVehicleUom.getUomCd();
			BigDecimal maxGrossWeightT = vehicle.getMaxGrossWeightT();
			addUomConverter(sapMaterial, vehicleUomCd, Constant.UOM_TONS, maxGrossWeightT.longValue(), 1L);
			
			// check whether the material could be converted to tons, if not then add a converter which will convert it to zero tons (e.g. service material, palett)
			if (!Constant.UOM_TONS.equals(defaultUnitOfMeasureCode) &&
					isTonnConversionRequiredForMaterial(sapMaterial)) {
				try {
					UomConversionResult conversionResult = this.convertQuantity(sapMaterial, defaultUnitOfMeasureCode,
							Constant.UOM_TONS, BigDecimal.ONE);
					conversionResult.getQuantity();
				}
				catch (LogonException logonException) {
					boolean isWarningEnabled = LOG.isWarnEnabled();
					if (isWarningEnabled) {
						Long sapMaterialIdentifier = sapMaterial.getSapMaterialId();
						String warningMessage = CANNOT_CONVERT_TO_TONS_WARNING_MESSAGE
								+ sapMaterialIdentifier;
						LOG.warn(warningMessage);
					}
				}
			}
		}
		
		// at bag case check whether the per single delivery quantity could be expressed in tons (multiple materials)
		if (LogonPackageHandlingUtil.PackageType.BAG.equals(this.packageType)) {
			if (this.materialCois.get(0).getPerSdorderQuantity() != null) {
				BigDecimal perSdoQuantityInTons = CustorderUtils.calculatePerSingleDeliveryQuantityInTonsForBagCase(this);
				if (perSdoQuantityInTons == null) {
					String errMsg = "The Per Single Delivery quantity and UoM could not convert to a common UoM";
					LOG.error(errMsg);
					throw new LogonException(errMsg, Reason.RES_ERROR_IMPL);
				}
				else if (BigDecimal.ZERO.compareTo(perSdoQuantityInTons) == 0) {
					LOG.warn("The Per Single Delivery quantity was converted to zero tons!");
				}
			}
		}
	}
	
	public static CustorderSplitWrapper create(CustorderEntity custorder, TruckWagonTypeWrapper vehicle
			, ReadOnlyDao<SapMaterialAltUomEntity> sapMatAltDao, RefDataGenericsServiceLocalInterface refdataService, ConfigBean configBean,
			OrderServiceLocalInterface orderService) {
		// some settings are needed for service only orders
		if (LogonPackageHandlingUtil.isServiceOnlyCustomerOrder(custorder)) {
			vehicle = TruckWagonTypeWrapperUtil.getDummyWrapper(BigDecimal.ZERO);
			CustorderUtils.initServiceOnlyCustomerOrder(custorder, refdataService);
		}
		else if (LogonPackageHandlingUtil.isPalletCustomerOrder(custorder)) {
			final DeliveryTypeBsEntity deliveryType = custorder.getDeliveryType();
			if (deliveryType != null
					&& LogonUtil.isReturnPickupOrder(deliveryType.getDeliveryTypeCd())) {
				// immense capacity to make sure all orders will fit!
				vehicle = TruckWagonTypeWrapperUtil.getDummyWrapper(Constant.CUSTORDER_ORDERED_QTY_MAX_VALUE);
				CustorderUtils.initReturnPickupCustomerOrder(custorder, refdataService);
			}
		}

		// initialize new customer order split wrapper instance
		CustorderSplitWrapper bulkSplitWrapper = new CustorderSplitWrapper(custorder, vehicle,
				sapMatAltDao, refdataService, configBean, orderService);
		return bulkSplitWrapper;
	}
	
	public void addUomConverter(SapMaterialEntity sapMaterial, String uomACd, String uomBCd, Long numeratorA2B, Long denominatorA2B) {
		boolean logDebugEnabled = LOG.isDebugEnabled();
		final UomConverterBean uomConverterBean = new UomConverterBean(uomACd, uomBCd,
				numeratorA2B, denominatorA2B);
		Map<String, UomConverterBean> sapMaterialConverterMap = custorderSpecificUomConverters.get(sapMaterial.getSapMaterialId());
		if (sapMaterialConverterMap == null) {
			sapMaterialConverterMap = new HashMap<String, UomConverterBean>();
			custorderSpecificUomConverters.put(sapMaterial.getSapMaterialId(),
					sapMaterialConverterMap);
		}

		UomConverterBean oldUomConverterBean = sapMaterialConverterMap.put(
				uomConverterBean.getKey(), uomConverterBean);

		if (oldUomConverterBean != null) {
			if (logDebugEnabled) {
				LOG.debug("converter bean updated, key: " + uomConverterBean.getKey());
			}
		}
	}
	
	public static boolean isTonnConversionRequiredForMaterial(SapMaterialEntity sapMaterial) {
		boolean isPallet = LogonPackageHandlingUtil.isPallet(sapMaterial);
		boolean isService = LogonPackageHandlingUtil.isService(sapMaterial);
		
		return !isPallet && !isService;
	}
	
	public UomBsEntity getUomBsEntityFromCache(String uomCd) {
		return uomCdUomEntityCache.get(uomCd);
	}
	
	public UomConversionResult convertQuantity(final SapMaterialEntity material, 
			final String sourceUnitOfMeasureCode, 
			final String targetUnitOfMeasureCode, 
			final BigDecimal quantity) {
		this.conversionFailed = CONVERSION_SUCEEDED_OR_NOT_YET_HAPPENED;
		if (sourceUnitOfMeasureCode.equals(targetUnitOfMeasureCode)) {
			return new UomConversionResult(quantity, 1L, 1L);
		}
		
		Long materialIdentifier = material.getSapMaterialId();
		Map<String, UomConverterBean> materialConverterMap = this.custorderSpecificUomConverters.get(materialIdentifier);
		String converterKey = UomConverterBean.generateKey(sourceUnitOfMeasureCode, targetUnitOfMeasureCode);
		
		UomConverterBean converter = materialConverterMap.get(converterKey);
		
		if (converter != null) {
			return converter.convertQuantity(sourceUnitOfMeasureCode, targetUnitOfMeasureCode, quantity);
		}

		UomBsEntity defaultUnitOfMeasure = material.getBaseUom();
		String defaultUnitOfMeasureCode = defaultUnitOfMeasure.getUomCd();

		boolean isSourceToDefaultUnitOfMeasureConverterExist = false;
		boolean isDefaultToTargetUnitOfMeasureConverterExist = false;

		if (!sourceUnitOfMeasureCode.equals(defaultUnitOfMeasureCode)) {
			converterKey = UomConverterBean.generateKey(sourceUnitOfMeasureCode, defaultUnitOfMeasureCode);
			if (materialConverterMap.get(converterKey) != null) {
				isSourceToDefaultUnitOfMeasureConverterExist = true;
			}
		}
		else {
			isSourceToDefaultUnitOfMeasureConverterExist = true;
		}

		if (!defaultUnitOfMeasureCode.equals(targetUnitOfMeasureCode)) {
			converterKey = UomConverterBean.generateKey(defaultUnitOfMeasureCode, targetUnitOfMeasureCode);
			if (materialConverterMap.get(converterKey) != null) {
				isDefaultToTargetUnitOfMeasureConverterExist = true;
			}
		}
		else {
			isDefaultToTargetUnitOfMeasureConverterExist = true;
		}

		if (isSourceToDefaultUnitOfMeasureConverterExist && isDefaultToTargetUnitOfMeasureConverterExist) {
			return convertUsingIntermediateUnitOfMeasure(sourceUnitOfMeasureCode,
					targetUnitOfMeasureCode, quantity, materialConverterMap,
					defaultUnitOfMeasureCode);
		}
		else if (!Constant.UOM_TONS.equals(defaultUnitOfMeasureCode)) {
			String tonsUnitOfMeasureCode = Constant.UOM_TONS;
			boolean isSourceToTonsConverterExist = false;
			boolean isTonsToTargetConverterExist = false;
			
			if (!sourceUnitOfMeasureCode.equals(tonsUnitOfMeasureCode)) {
				converterKey = UomConverterBean.generateKey(sourceUnitOfMeasureCode,
						tonsUnitOfMeasureCode);
				if (materialConverterMap.get(converterKey) != null) {
					isSourceToTonsConverterExist = true;
				}
			}
			else {
				isSourceToTonsConverterExist = true;
			}
			
			if (!tonsUnitOfMeasureCode.equals(targetUnitOfMeasureCode)) {
				converterKey = UomConverterBean.generateKey(tonsUnitOfMeasureCode,
						targetUnitOfMeasureCode);
				if (materialConverterMap.get(converterKey) != null) {
					isTonsToTargetConverterExist = true;
				}
			}
			else {
				isTonsToTargetConverterExist = true;
			}
			
			if (isSourceToTonsConverterExist && isTonsToTargetConverterExist) {
				return convertUsingIntermediateUnitOfMeasure(sourceUnitOfMeasureCode,
						targetUnitOfMeasureCode, quantity, materialConverterMap,
						tonsUnitOfMeasureCode);
			}
		}

		converterKey = UomConverterBean.generateKey(sourceUnitOfMeasureCode, targetUnitOfMeasureCode);
		String errorMessage = "No converter found for key: " + converterKey + ", material ID: "
				+ materialIdentifier;
		boolean isWarningEnabled = LOG.isWarnEnabled();
		if (isWarningEnabled) {
			LOG.warn(errorMessage); // log only as warning, see LOGON-6346
		}
		if (isTonnConversionRequiredForMaterial(material)) {
			this.conversionFailed = CONVERSION_FAILED;
		}
		throw new LogonException(errorMessage, Reason.RES_ERROR_CANNOT_CONVERT_UOM);
	}

	public UomConversionResult convertUsingIntermediateUnitOfMeasure(
			final String sourceUnitOfMeasureCode, final String targetUnitOfMeasureCode,
			final BigDecimal quantity, Map<String, UomConverterBean> materialConverterMap,
			String intermediateUnitOfMeasureCode) {
		String converterKey;
		UomConverterBean converter;
		converterKey = UomConverterBean.generateKey(sourceUnitOfMeasureCode, intermediateUnitOfMeasureCode);
		converter = materialConverterMap.get(converterKey);
		final UomConversionResult sourceToIntermediateConversion = converter.convertQuantity(sourceUnitOfMeasureCode, intermediateUnitOfMeasureCode, quantity);
		converterKey = UomConverterBean.generateKey(intermediateUnitOfMeasureCode, targetUnitOfMeasureCode);
		converter = materialConverterMap.get(converterKey);
		final UomConversionResult intermediateToTargetConversion = converter.convertQuantity(intermediateUnitOfMeasureCode, targetUnitOfMeasureCode, sourceToIntermediateConversion.getQuantity());

		Long sourceToIntermediateNumerator = sourceToIntermediateConversion.getNumerator();
		Long intermediateToTargetNumerator = intermediateToTargetConversion.getNumerator();
		final Long numerator = sourceToIntermediateNumerator * intermediateToTargetNumerator;
		Long intermediateToTargetDenominator = intermediateToTargetConversion.getDenominator();
		Long sourceToIntermediateDenominator = sourceToIntermediateConversion.getDenominator();
		final Long denominator = sourceToIntermediateDenominator * intermediateToTargetDenominator;
		final Long greatestCommonDevisor = LogonUtil.findGCD(numerator, denominator);
		
		BigDecimal convertedQuantity = intermediateToTargetConversion.getQuantity();
		return new UomConversionResult(convertedQuantity, numerator / greatestCommonDevisor, denominator / greatestCommonDevisor);
	}
	
	// converters are all material specific ones!!!
	public BigDecimal getVehicleMaxCapacityInUomCd(SapMaterialEntity sapMaterial, String inUomCd) {
		// special UoMs like LKW, BW and CON
		UomBsEntity uomBs = uomCdUomEntityCache.get(inUomCd);
		if (uomBs != null && uomBs.getCreateMultiOrderFlag() != null
				&& uomBs.getCreateMultiOrderFlag().booleanValue()) {
			if (uomBs.getUomCd().equals(Constant.UOM_CONTAINER)) {
				final long deliveryTypeCd = this.custorder.getDeliveryType().getDeliveryTypeCd();
				if (LogonUtil.isRailDirectOrder(deliveryTypeCd)) {
					return new BigDecimal(3.0);
				}
			}
			
			return BigDecimal.ONE;
		}

		// calculate max capacity of vehicle using its volume
		BigDecimal vehicleMaxFromVolumeInUom = null;
		if (vehicle.getVolumeM3() != null) {
			try {
				vehicleMaxFromVolumeInUom = convertQuantity(sapMaterial, Constant.UOM_MTQ, inUomCd,
						vehicle.getVolumeM3()).getQuantity();
			}
			catch (LogonException e) {
				vehicleMaxFromVolumeInUom = null;
			}
		}

		// calculate max capacity of vehicle using maxGrossWeight
		BigDecimal vehicleMaxFromTInUom = convertQuantity(sapMaterial, Constant.UOM_TONS, inUomCd,
				vehicle.getMaxGrossWeightT()).getQuantity();

		if (vehicleMaxFromVolumeInUom == null) {
			return vehicleMaxFromTInUom;
		}
		// return the smaller one!
		if (vehicleMaxFromVolumeInUom.compareTo(vehicleMaxFromTInUom) <= 0) {
			return vehicleMaxFromVolumeInUom;
		}
		return vehicleMaxFromTInUom;
	}
	
	public Boolean getOrderManuallyScheduled(OrderEntity order) {
		Boolean result = Boolean.TRUE;
		if (orderManuallyScheduled != null) {
			result = orderManuallyScheduled.get(order);
		}
		return result;
	}
	
	public Long getDeliveryTypeCd() {
		if (this.deliveryType != null) {
			return this.deliveryType.getDeliveryTypeCd();
		}
		return null;
	}
	
	
	public OrderStatusBsEntity getOrderStatusEntity(long orderStatusCd) {
		return orderStatusEntityListCache.get(orderStatusCd);
	}
	
	public CustorderStatusBsEntity getCustrderStatusEntity(long custorderStatusCd) {
		return custorderStatusEntityListCache.get(custorderStatusCd);
	}
	
	public ArchivingStatusBsEntity getArchivingStatusEntity(long archivingStatusCd) {
		return archivingStatusEntityListCache.get(archivingStatusCd);
	}
	
	public CustorderEntity getCustorder() {
		return custorder;
	}
	public int getNumOfMaxOrders() {
		return numOfMaxOrders;
	}
	public int getDefaultWeightTolerance() {
		return defaultWeightTolerance;
	}
	public List<CustorderItemEntity> getMaterialCois() {
		return materialCois;
	}
	public List<CustorderItemEntity> getServiceCois() {
		return serviceCois;
	}
	public TruckWagonTypeWrapper getVehicle() {
		return vehicle;
	}
	public UomBsEntity getFullVehicleUom() {
		return fullVehicleUom;
	}
	public OrderEntity getSdoTemplate() {
		return sdoTemplate;
	}
	public void setSdoTemplate(OrderEntity sdoTemplate) {
		this.sdoTemplate = sdoTemplate;
	}
	public OrderItemEntity getSdoiTemplate() {
		return sdoiTemplate;
	}
	public void setSdoiTemplate(OrderItemEntity sdoiTemplate) {
		this.sdoiTemplate = sdoiTemplate;
	}
	public LogonPackageHandlingUtil.PackageType getPackageType() {
		return packageType;
	}
	public DeliveryTypeBsEntity getDeliveryType() {
		return deliveryType;
	}
	public BigDecimal get_remainingQtyInOrderedUom() {
		return _remainingQtyInOrderedUom;
	}
	public void set_remainingQtyInOrderedUom(BigDecimal _remainingQtyInOrderedUom) {
		this._remainingQtyInOrderedUom = _remainingQtyInOrderedUom;
	}
	public BigDecimal get_perSdoQtyInOrderedUom() {
		return _perSdoQtyInOrderedUom;
	}
	public void set_perSdoQtyInOrderedUom(BigDecimal _perSdoQtyInOrderedUom) {
		this._perSdoQtyInOrderedUom = _perSdoQtyInOrderedUom;
	}
	public BigDecimal get_vehicleMaxLoadInOrderedUom() {
		return _vehicleMaxLoadInOrderedUom;
	}
	public void set_vehicleMaxLoadInOrderedUom(BigDecimal _vehicleMaxLoadInOrderedUom) {
		this._vehicleMaxLoadInOrderedUom = _vehicleMaxLoadInOrderedUom;
	}
	public double get_deliveryFqyInMinutes() {
		return _deliveryFqyInMinutes;
	}
	public void set_deliveryFqyInMinutes(double _deliveryFqyInMinutes) {
		this._deliveryFqyInMinutes = _deliveryFqyInMinutes;
	}
	public UomBsEntity get_orderedUom() {
		return _orderedUom;
	}
	public void set_orderedUom(UomBsEntity _orderedUom) {
		this._orderedUom = _orderedUom;
	}
	public UomBsEntity get_fullVehicleUom() {
		return _fullVehicleUom;
	}
	public void set_fullVehicleUom(UomBsEntity _fullVehicleUom) {
		this._fullVehicleUom = _fullVehicleUom;
	}
	public int get_numOfDeliveries() {
		return _numOfDeliveries;
	}
	public void set_numOfDeliveries(int _numOfDeliveries) {
		this._numOfDeliveries = _numOfDeliveries;
	}
	public String get_numOfDeliveriesStr() {
		return _numOfDeliveriesStr;
	}
	public void set_numOfDeliveriesStr(String _numOfDeliveriesStr) {
		this._numOfDeliveriesStr = _numOfDeliveriesStr;
	}
	public List<VehicleMaterialPlanner> get_vehicleMaterialPlanners() {
		return _vehicleMaterialPlanners;
	}
	public void set_vehicleMaterialPlanners(List<VehicleMaterialPlanner> _vehicleMaterialPlanners) {
		this._vehicleMaterialPlanners = _vehicleMaterialPlanners;
	}
	public Date get_deliveryToDate() {
		return _deliveryToDate;
	}
	public void set_deliveryToDate(Date _deliveryToDate) {
		this._deliveryToDate = _deliveryToDate;
	}
	public boolean isPalletOnlyOrder() {
		return isPalletOnlyOrder;
	}
	
	@Override
	public String toString() {
		List<CustorderItemEntity> custorderItems = custorder.getCustorderItems();
		List<OrderEntity> sdOrders = custorder.getOrders();
		
		StringBuilder sb = new StringBuilder();
		sb.append(">>> CUSTORDER >>>\n");
		sb.append("  DeliveryFromDat         : ").append(custorder.getDeliveryFromDat()).append("\n");
		sb.append("  DeliveryToDat           : ").append(custorder.getDeliveryToDat()).append("\n");
		sb.append("  DeliveryTimeTolerance   : ").append(custorder.getDeliveryTimeTolerance()).append("\n");
		sb.append("  DeliveryTimeToleranceUom: ").append(custorder.getDeliveryTimeToleranceUom()).append("\n");
		sb.append("  DeliveryFreqEveryTime   : ").append(custorder.getDeliveryFreqEveryTime()).append("\n");
		sb.append("  DeliveryFreqEveryTimeUom: ").append(custorder.getDeliveryFreqEveryTimeUom()).append("\n");
		sb.append("  DeliveryFreqQtyperhre   : ").append(custorder.getDeliveryFreqQtyperhre()).append("\n");
		sb.append("  DeliveryFreqQtyperhreUom: ").append(custorder.getDeliveryFreqQtyperhreUom() == null ? null : custorder.getDeliveryFreqQtyperhreUom().getUomCd()).append("\n");
		sb.append("  VEHICLE Priority        : ").append(vehicle.getPriority()).append("\n");
		sb.append("  VEHICLE MaxGrossWeightT : ").append(vehicle.getMaxGrossWeightT()).append("\n");
		sb.append("  VEHICLE VolumeM3        : ").append(vehicle.getVolumeM3()).append("\n");

		if (custorderItems != null) {
			for (int i=0; i < custorderItems.size(); i++) {
				sb.append("  >>> CUSTORDER ITEM "+ i +" >>>\n");
				CustorderItemEntity custorderItem = custorderItems.get(i);
				sb.append("    OrderedQuantity      : ").append(custorderItem.getOrderedQuantity()).append("\n");
				sb.append("    OrderedUom           : ").append(custorderItem.getOrderedUom() == null ? null : custorderItem.getOrderedUom().getUomCd()).append("\n");
				sb.append("    ExactCustorderQtyFlag: ").append(custorderItem.getExactCustorderQtyFlag()).append("\n");
				sb.append("    PerSdorderQuantity   : ").append(custorderItem.getPerSdorderQuantity()).append("\n");
				sb.append("    PerSdorderUom        : ").append(custorderItem.getPerSdorderUom() == null ? null : custorderItem.getPerSdorderUom().getUomCd()).append("\n");
				sb.append("    ExactSdorderQtyFlag  : ").append(custorderItem.getExactSdorderQtyFlag()).append("\n");
				sb.append("  <<< CUSTORDER ITEM "+ i +" <<<\n");
			}
		}
		
		if (sdOrders != null) {
			for (int i=0; i < sdOrders.size(); i++) {
				sb.append("  >>> SINGLE DELIVERY "+ i +" >>>\n");
				OrderEntity sdOrder = sdOrders.get(i);
				sb.append("    DeliveryFromDat      : ").append(sdOrder.getDeliveryFromDat()).append("\n");
				sb.append("    DeliveryToDat        : ").append(sdOrder.getDeliveryToDat()).append("\n");
				
				List<OrderItemEntity> orderItems = sdOrder.getOrderItems();
				if (orderItems != null) {
					for (int j=0; j < orderItems.size(); j++) {
						OrderItemEntity orderItem = orderItems.get(j);
						sb.append("    >>> SINGLE DELIVERY ITEM "+ j +" >>>\n");
						sb.append("      OrderedQuantity      : ").append(orderItem.getOrderedQuantity()).append("\n");
						sb.append("      OrderedUom           : ").append(orderItem.getOrderedUom() == null ? null : orderItem.getOrderedUom().getUomCd()).append("\n");
						sb.append("    <<< SINGLE DELIVERY ITEM "+ j +" <<<\n");
					}
				}
				sb.append("  <<< SINGLE DELIVERY "+ i +" <<<\n");
			}
		}
		
		sb.append("<<< CUSTORDER <<<\n");
		
		return sb.toString();
	}

	public boolean isConversionFailed() {
		return conversionFailed;
	}

}
