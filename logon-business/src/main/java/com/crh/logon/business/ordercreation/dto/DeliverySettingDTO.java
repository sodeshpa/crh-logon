/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ordercreation.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.crh.logon.business.ordercreation.DeliveryOptionConfigurationDTO;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.TimeZone;
import org.apache.commons.lang.StringUtils;

import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.FireqMaterialMixdesignEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.PoolEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.common.ShsareaRestrTruckTypeEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * 
 * @author AdNovum Informatik AG
 * 
 *         This represents the dispocard / delivery settings provided on
 *         MasterData / Shipto / second tab per material.
 * 
 */
public class DeliverySettingDTO extends AbstractDeliverySetting implements
		DeliverySetting {

	private static final long serialVersionUID = 1L;

	public static final int DELIVERY_SETTING_BASED_ON_CONTRACT = 0;
	public static final int DELIVERY_SETTING_BASED_ON_CONTRACT_COMBINED_RESTRICTION = 1;
	public static final int DELIVERY_SETTING_BASED_ON_RESTRICTION = 2;
	public static final int DELIVERY_SETTING_NOT_DEFINED = 3;

	public static final int RESOLUTION_NOT_RELEVANT = 1;
	public static final int RESOLUTION_FROM_PRICING_GROUP_TYPE_GENERIC_MATERIAL = 2;
	public static final int RESOLUTION_EXPLICIT_IN_CONTRACT = 3;
	public static final int RESOLUTION_MISSING = 4;
	public static final int RESOLUTION_FROM_DIVISION_TYPE_GENERIC_MATERIAL = 5;

	private int resolutionType = RESOLUTION_NOT_RELEVANT;

	private final DeliverySettingLangDepData langDepData;
	private BigDecimal quantity;

	Boolean exactCustorderQtyFlag = null;
	Boolean exactSdorderQtyFlag = null;

	// only relevant for UI and restriction based settings
	Boolean autoCarrierAssignmentFlag = null;
	Boolean autoPoolAssignmentFlag = null;
	Boolean autoReleaseFlag = null;
	Boolean showDeliveryPlantWebOrderFlag = null;
	Boolean showPriceInWebOrderFlag = null;

	private FireqMaterialMixdesignEntity mixDesign;
	private Boolean freeDeliveryFlag;
	private Boolean releaseSapFlag;
	private Boolean sapSuppressMinChargeFlag;
	private Boolean openCustorderFlag;
	private Boolean onHoldOrderAndLockedFlag;

	private BigDecimal perSdorderQuantity;
	private UomBsEntity perSdorderUom;
	private Double deliveryFreqEveryTime;
	private String deliveryFreqEveryTimeUomCd;
	private BigDecimal deliveryFreqQtyperhre;
	private UomBsEntity deliveryFreqQtyperhreUom;
	private Double deliveryTimeTolerance;
	private String deliveryTimeToleranceUomCd;

	private PlantBsEntity productionPlant;
	private ShippingpointEntity shippingpoint;
	private CarrierEntity commercialCarrier;
	private CarrierEntity executingCarrier;
	private TimeZone shipToTimeZone;

	// please make sure that in case you name field "pool" differently the code
	// parts which use this also change!
	// name of the property attribute
	public static final String ATTR_POOL_ID = "pool." + PoolEntity.ATTR_POOL_ID;
	private PoolEntity pool;

	private ShiptoSiloEntity shiptoSilo;
	private SapBpEntity billtoSapBp;
	private PlantBsEntity supplyPlant;
	private LoadingpointEntity loadingpoint;

	// please make sure that in case you name field "deliveryTypeSetting"
	// differently the code parts which use this also change!
	// name of the property attribute
	public static final String ATTR_DELIVERY_TYPE_CD = "deliveryType."
			+ DeliveryTypeBsEntity.ATTR_DELIVERY_TYPE_CD;
	private DeliveryTypeBsEntity deliveryType;

	public static final String ATTR_UOM_CD = "uom." + UomBsEntity.ATTR_UOM_CD;
	private UomBsEntity uom;
	private SapBpEntity payerSapBp;
	private final ShiptoSareaRestrEntity shiptoSareaRestr;
	private final ShiptoSareaEntity shiptoSarea;
	private String serviceProcessIndicatorId;

	private boolean isQuantityControlled;
	private boolean isCashSales;

	public String getCompanyCode() {
		return getSarea().getSorg().getCompanycode().getCompanycodeCd();
	}

	// the delivery setting option configuration valid for the delivery setting
	private DeliveryOptionConfigurationDTO deliveryOptionConfiguration;

	private List<TruckWagonTypeWrapper> selectedVehicleTypeWrapperList;

	public static final String[] excludeEntities = {
		"productionPlant", 
		"shippingpoint",
		"commercialCarrier", 
		"executingCarrier", 
		"pool", 
		"shiptoSilo", 
		"billtoSapBp",
		"supplyPlant", 
		"loadingpoint", 
		"deliveryType", 
		"uom", 
		"payerSapBp", 
		"shiptoSareaRestr",
		"sapContractItem", 
		"sapMaterial", 
		"sarea", 
		"shiptoSarea" 
	};

	public DeliverySettingDTO(int resolutionType, DeliverySettingLangDepData langDepData,
			BigDecimal quantity, PlantBsEntity productionPlant, ShippingpointEntity shippingpoint,
			CarrierEntity commercialCarrier, CarrierEntity executingCarrier, PoolEntity pool,
			ShiptoSiloEntity shiptoSilo, SapBpEntity billtoSapBp, PlantBsEntity supplyPlant,
			LoadingpointEntity loadingpoint, DeliveryTypeBsEntity deliveryType, UomBsEntity uom,
			SapBpEntity payerSapBp, ShiptoSareaEntity shiptoSarea, Boolean exactCustorderQtyFlag,
			Boolean exactSdorderQtyFlag, Boolean autoPoolAssignmentFlag,
			Boolean autoCarrierAssignmentFlag, Boolean autoReleaseFlag, Boolean freeDeliveryFlag,
			Boolean releaseSapFlag, Boolean onHoldOrderAndLockedFlag, Boolean openCustorderFlag,
			Boolean sapSuppressMinChargeFlag, BigDecimal perSdorderQuantity,
			UomBsEntity perSdorderUom, Double deliveryFreqEveryTime,
			String deliveryFreqEveryTimeUomCd, BigDecimal deliveryFreqQtyperhre,
			UomBsEntity deliveryFreqQtyperhreUom, Double deliveryTimeTolerance,
			String deliveryTimeToleranceUomCd, FireqMaterialMixdesignEntity mixDesign,
			SapContractItemEntity sapContractItem, SapMaterialEntity sapMaterial,
			List<TruckWagonTypeWrapper> selectedVehicleTypeWrapperList,
			ShiptoSareaRestrEntity shiptoSareaRestr, String serviceProcessIndicatorId,
			boolean isCashSales) {

		this.resolutionType = resolutionType;
		this.langDepData = langDepData;
		this.quantity = quantity;
		this.productionPlant = productionPlant;
		this.shippingpoint = shippingpoint;
		this.commercialCarrier = commercialCarrier;
		this.executingCarrier = executingCarrier;
		this.pool = pool;
		this.shiptoSilo = shiptoSilo;
		this.billtoSapBp = billtoSapBp;
		this.supplyPlant = supplyPlant;
		this.loadingpoint = loadingpoint;
		this.deliveryType = deliveryType;
		this.uom = uom;
		this.payerSapBp = payerSapBp;
		this.shiptoSarea = shiptoSarea;
		this.sarea = shiptoSarea.getSarea();

		this.shiptoSareaRestr = shiptoSareaRestr;
		this.sapContractItem = sapContractItem;
		this.sapMaterial = sapMaterial;

		this.exactCustorderQtyFlag = exactCustorderQtyFlag;
		this.exactSdorderQtyFlag = exactSdorderQtyFlag;
		this.autoPoolAssignmentFlag = autoPoolAssignmentFlag;
		this.autoCarrierAssignmentFlag = autoCarrierAssignmentFlag;
		this.autoReleaseFlag = autoReleaseFlag;
		this.serviceProcessIndicatorId = serviceProcessIndicatorId;

		this.freeDeliveryFlag = freeDeliveryFlag;
		this.releaseSapFlag = releaseSapFlag;
		this.onHoldOrderAndLockedFlag = onHoldOrderAndLockedFlag;
		this.openCustorderFlag = openCustorderFlag;
		this.sapSuppressMinChargeFlag = sapSuppressMinChargeFlag;

		this.perSdorderQuantity = perSdorderQuantity;
		this.perSdorderUom = perSdorderUom;
		this.deliveryFreqEveryTime = deliveryFreqEveryTime;
		this.deliveryFreqEveryTimeUomCd = deliveryFreqEveryTimeUomCd;
		this.deliveryFreqQtyperhre = deliveryFreqQtyperhre;
		this.deliveryFreqQtyperhreUom = deliveryFreqQtyperhreUom;
		this.deliveryTimeTolerance = deliveryTimeTolerance;
		this.deliveryTimeToleranceUomCd = deliveryTimeToleranceUomCd;

		this.mixDesign = mixDesign;

		this.selectedVehicleTypeWrapperList = selectedVehicleTypeWrapperList;
		this.isCashSales = isCashSales;
		
		this.showDeliveryPlantWebOrderFlag = this.sarea != null ? this.sarea.getShowDelplantWebOrderFlag() : Boolean.FALSE;
		this.showPriceInWebOrderFlag = this.sarea != null ? this.sarea.getShowPriceInWebOrderFlag() : Boolean.FALSE;
	}

	public DeliverySettingDTO(SapContractItemEntity contractItem,
			SapMaterialEntity sapMaterial, int resolutionType,
			ShiptoSareaEntity shiptoSarea) {
		this(contractItem, sapMaterial, resolutionType, shiptoSarea, null, null);
	}

	public DeliverySettingDTO(SapContractItemEntity contractItem,
			SapMaterialEntity sapMaterial, int resolutionType,
			ShiptoSareaEntity shiptoSarea, BigDecimal openQuantity, TimeZone shiptoTZ) {

		this.sarea = contractItem.getSapContract().getSarea();
		this.sapMaterial = sapMaterial;
		this.sapContractItem = contractItem;
		this.shiptoSareaRestr = null;
		this.shiptoSarea = shiptoSarea;
		this.shipToTimeZone = shiptoTZ;
		this.resolutionType = resolutionType;

		initDeliverySettingDTO();

		if (openQuantity != null) {
			this.quantity = openQuantity;
			this.isQuantityControlled = true;
		}

		this.langDepData = this.new DeliverySettingLangDepData();

		this.selectedVehicleTypeWrapperList = new LinkedList<TruckWagonTypeWrapper>();
		this.showDeliveryPlantWebOrderFlag = this.sarea != null ? this.sarea.getShowDelplantWebOrderFlag() : Boolean.FALSE;
		this.showPriceInWebOrderFlag = this.sarea != null ? this.sarea.getShowPriceInWebOrderFlag() : Boolean.FALSE;
	}

	public DeliverySettingDTO(ShiptoSareaRestrEntity restr, int resolutionType,
			BigDecimal openQuantity, TimeZone shiptoTZ) {

		this.sarea = restr.getShiptoSarea().getSarea();
		this.shiptoSareaRestr = restr;
		this.sapContractItem = restr.getSapContractItem();
		this.sapMaterial = restr.getSapMaterial();
		this.serviceProcessIndicatorId = restr.getSpiCd();
		this.shipToTimeZone = shiptoTZ;
		
		this.shiptoSarea = restr.getShiptoSarea();

		this.resolutionType = resolutionType;

		initDeliverySettingDTO();

		if (openQuantity != null) {
			this.quantity = openQuantity;
			this.isQuantityControlled = true;
		}

		this.langDepData = this.new DeliverySettingLangDepData();

		this.showDeliveryPlantWebOrderFlag = this.sarea != null ? this.sarea.getShowDelplantWebOrderFlag() : Boolean.FALSE;
		this.showPriceInWebOrderFlag = this.sarea != null ? this.sarea.getShowPriceInWebOrderFlag() : Boolean.FALSE;
	}

	public ShiptoSareaEntity getShiptoSarea() {
		return shiptoSarea;
	}

	public DeliverySettingDTO(ShiptoSareaRestrEntity restr, TimeZone shiptoTZ) {

		this.sarea = restr.getShiptoSarea().getSarea();
		this.shiptoSareaRestr = restr;
		this.sapContractItem = restr.getSapContractItem();
		this.sapMaterial = restr.getSapMaterial();
		this.shipToTimeZone = shiptoTZ;
		
		this.shiptoSarea = restr.getShiptoSarea();
		this.serviceProcessIndicatorId = restr.getSpiCd();

		initDeliverySettingDTO();

		this.langDepData = this.new DeliverySettingLangDepData();
		this.showDeliveryPlantWebOrderFlag = this.sarea != null ? this.sarea.getShowDelplantWebOrderFlag() : Boolean.FALSE;
		this.showPriceInWebOrderFlag = this.sarea != null ? this.sarea.getShowPriceInWebOrderFlag() : Boolean.FALSE;
	}

	public DeliverySettingDTO(SapMaterialEntity sapMaterial,
			ShiptoSareaEntity shiptoSarea, TimeZone shiptoTZ) {

		this.sarea = shiptoSarea.getSarea();
		this.sapMaterial = sapMaterial;
		this.sapContractItem = null;
		this.shiptoSareaRestr = null;
		this.shipToTimeZone = shiptoTZ;
		
		this.shiptoSarea = shiptoSarea;

		initDeliverySettingDTO();

		this.langDepData = this.new DeliverySettingLangDepData();
		this.selectedVehicleTypeWrapperList = new LinkedList<TruckWagonTypeWrapper>();
		this.showDeliveryPlantWebOrderFlag = this.sarea != null ? this.sarea.getShowDelplantWebOrderFlag() : Boolean.FALSE;
		this.showPriceInWebOrderFlag = this.sarea != null ? this.sarea.getShowPriceInWebOrderFlag() : Boolean.FALSE;
	}

	private void initDeliverySettingDTO() {

		initDeliverySettingType();

		if (this.deliverySettingType == DELIVERY_SETTING_BASED_ON_CONTRACT) {

			this.billtoSapBp = this.sapContractItem.getSapContract()
					.getBilltoSapBp();
			this.productionPlant = this.sapMaterial.getProdPlant();
			this.supplyPlant = this.sapContractItem.getSupplyPlant();
			// ignore contract payer for contracts where payer is determined
			// automatically
			if (this.sapContractItem.getSapContract().getSapContractType()
					.getAutoPayerDetermFlag()
					&& this.sarea.getAutoPayerDetermFlag()) {
				this.payerSapBp = null;
			} else {
				this.payerSapBp = this.sapContractItem.getSapContract()
						.getPayerSapBp();
			}
			this.uom = this.sapContractItem.getTargetQuantityUom();
			this.shippingpoint = null;
			this.loadingpoint = null;
			this.commercialCarrier = null;
			this.executingCarrier = null;
			this.pool = null;
			this.shiptoSilo = null;
			this.deliveryType = null;
			this.quantity = this.sapContractItem.getTargetQuantity();

			this.exactCustorderQtyFlag = Boolean.FALSE;
			this.exactSdorderQtyFlag = Boolean.FALSE;
			this.autoPoolAssignmentFlag = Boolean.FALSE;
			this.autoCarrierAssignmentFlag = Boolean.FALSE;
			this.autoReleaseFlag = Boolean.FALSE;

			this.freeDeliveryFlag = Boolean.FALSE;
			this.releaseSapFlag = Boolean.TRUE;
			this.onHoldOrderAndLockedFlag = Boolean.FALSE;
			this.openCustorderFlag = Boolean.FALSE;
			this.sapSuppressMinChargeFlag = Boolean.FALSE;

			this.perSdorderQuantity = null;
			this.perSdorderUom = null;
			this.deliveryFreqEveryTime = null;
			this.deliveryFreqEveryTimeUomCd = null;
			this.deliveryFreqQtyperhre = null;
			this.deliveryFreqQtyperhreUom = null;
			this.deliveryTimeTolerance = null;
			this.deliveryTimeToleranceUomCd = null;

			this.mixDesign = null;
		} else if (this.deliverySettingType == DELIVERY_SETTING_BASED_ON_RESTRICTION) {
			// this type is currently the type where the restriction has no
			// contract attribute (like service only)
			this.shippingpoint = this.shiptoSareaRestr.getShippingpoint();
			this.supplyPlant = shippingpoint != null ? shippingpoint.getPlant()
					: null;
			this.loadingpoint = this.shiptoSareaRestr.getLoadingpoint();
			this.billtoSapBp = this.shiptoSareaRestr.getBilltoSapBp();
			this.commercialCarrier = this.shiptoSareaRestr.getCommCarrier();
			this.executingCarrier = this.shiptoSareaRestr.getExecCarrier();
			this.shiptoSilo = this.shiptoSareaRestr.getShiptoSilo();
			this.deliveryType = this.shiptoSareaRestr.getDeliveryType();
			this.uom = this.shiptoSareaRestr.getUom();
			this.productionPlant = this.shiptoSareaRestr.getProductionPlant();
			this.payerSapBp = this.shiptoSareaRestr.getPayerSapBp();
			this.pool = this.shiptoSareaRestr.getPool();
			this.quantity = this.shiptoSareaRestr.getQuantity();

			this.exactCustorderQtyFlag = this.shiptoSareaRestr
					.getExactCustorderQtyFlag();
			this.exactSdorderQtyFlag = this.shiptoSareaRestr
					.getExactSdorderQtyFlag();
			this.autoPoolAssignmentFlag = this.shiptoSareaRestr
					.getAutoPoolAssignmentFlag();
			this.autoCarrierAssignmentFlag = this.shiptoSareaRestr
					.getAutoCarrierAssignmentFlag();
			this.autoReleaseFlag = this.shiptoSareaRestr.getAutoReleaseFlag();

			this.freeDeliveryFlag = getShiptoSareaRestr().getFreeDeliveryFlag();
			this.releaseSapFlag = getShiptoSareaRestr().getReleaseSapFlag();
			this.onHoldOrderAndLockedFlag = getShiptoSareaRestr()
					.getOnHoldOrderAndLockedFlag();
			this.openCustorderFlag = getShiptoSareaRestr()
					.getOpenCustorderFlag();
			this.sapSuppressMinChargeFlag = getShiptoSareaRestr()
					.getSapSuppressMinChargeFlag();
			this.mixDesign = getShiptoSareaRestr().getMixDesign();

			this.perSdorderQuantity = getShiptoSareaRestr()
					.getPerSdorderQuantity();
			this.perSdorderUom = getShiptoSareaRestr().getPerSdorderUom();
			this.deliveryFreqEveryTime = getShiptoSareaRestr()
					.getDeliveryFreqEveryTime();
			this.deliveryFreqEveryTimeUomCd = getShiptoSareaRestr()
					.getDeliveryFreqEveryTimeUom();
			this.deliveryFreqQtyperhre = getShiptoSareaRestr()
					.getDeliveryFreqQtyperhre();
			this.deliveryFreqQtyperhreUom = getShiptoSareaRestr()
					.getDeliveryFreqQtyperhreUom();
			this.deliveryTimeTolerance = getShiptoSareaRestr()
					.getDeliveryTimeTolerance();
			this.deliveryTimeToleranceUomCd = getShiptoSareaRestr()
					.getDeliveryTimeToleranceUom();

			this.mixDesign = getShiptoSareaRestr().getMixDesign();
			this.serviceProcessIndicatorId = this.shiptoSareaRestr.getSpiCd();

			setVehichleTypeCdsBasedOnRestriction();

		} else if (this.deliverySettingType == DELIVERY_SETTING_BASED_ON_CONTRACT_COMBINED_RESTRICTION) {
			// this type is the one where the restriction also has contract item
			// reference
			this.shippingpoint = this.shiptoSareaRestr.getShippingpoint();
			this.commercialCarrier = this.shiptoSareaRestr.getCommCarrier();
			this.executingCarrier = this.shiptoSareaRestr.getExecCarrier();
			this.pool = this.shiptoSareaRestr.getPool();
			this.shiptoSilo = this.shiptoSareaRestr.getShiptoSilo();
			this.billtoSapBp = this.shiptoSareaRestr.getBilltoSapBp();
			this.supplyPlant = shippingpoint != null ? shippingpoint.getPlant()
					: null;
			this.uom = this.shiptoSareaRestr.getUom();
			this.deliveryType = this.shiptoSareaRestr.getDeliveryType();
			this.loadingpoint = this.shiptoSareaRestr.getLoadingpoint();
			this.serviceProcessIndicatorId = this.shiptoSareaRestr.getSpiCd();

			this.exactCustorderQtyFlag = this.shiptoSareaRestr
					.getExactCustorderQtyFlag();
			this.exactSdorderQtyFlag = this.shiptoSareaRestr
					.getExactSdorderQtyFlag();
			this.autoPoolAssignmentFlag = this.shiptoSareaRestr
					.getAutoPoolAssignmentFlag();
			this.autoCarrierAssignmentFlag = this.shiptoSareaRestr
					.getAutoCarrierAssignmentFlag();
			this.autoReleaseFlag = this.shiptoSareaRestr.getAutoReleaseFlag();

			this.freeDeliveryFlag = getShiptoSareaRestr().getFreeDeliveryFlag();
			this.releaseSapFlag = getShiptoSareaRestr().getReleaseSapFlag();
			this.onHoldOrderAndLockedFlag = getShiptoSareaRestr()
					.getOnHoldOrderAndLockedFlag();
			this.openCustorderFlag = getShiptoSareaRestr()
					.getOpenCustorderFlag();
			this.sapSuppressMinChargeFlag = getShiptoSareaRestr()
					.getSapSuppressMinChargeFlag();

			this.perSdorderQuantity = getShiptoSareaRestr()
					.getPerSdorderQuantity();
			this.perSdorderUom = getShiptoSareaRestr().getPerSdorderUom();
			this.deliveryFreqEveryTime = getShiptoSareaRestr()
					.getDeliveryFreqEveryTime();
			this.deliveryFreqEveryTimeUomCd = getShiptoSareaRestr()
					.getDeliveryFreqEveryTimeUom();
			this.deliveryFreqQtyperhre = getShiptoSareaRestr()
					.getDeliveryFreqQtyperhre();
			this.deliveryFreqQtyperhreUom = getShiptoSareaRestr()
					.getDeliveryFreqQtyperhreUom();
			this.deliveryTimeTolerance = getShiptoSareaRestr()
					.getDeliveryTimeTolerance();
			this.deliveryTimeToleranceUomCd = getShiptoSareaRestr()
					.getDeliveryTimeToleranceUom();

			this.mixDesign = getShiptoSareaRestr().getMixDesign();

			setVehichleTypeCdsBasedOnRestriction();

			if (this.sapMaterial.getProdPlant() != null) {
				this.productionPlant = this.sapMaterial.getProdPlant();
			} else {
				this.productionPlant = this.shiptoSareaRestr
						.getProductionPlant();
			}

			/**
			 * Contract does not have these settings at all, use the
			 * restriction's settings, if available. The material might be
			 * resolved to a generic material, it is not necessarily the same as
			 * the contract material. Always use restriction material!
			 */

			this.payerSapBp = this.shiptoSareaRestr.getPayerSapBp();
			this.quantity = this.shiptoSareaRestr.getQuantity();
		} else { // initialize at least the not nullable flags
			this.exactCustorderQtyFlag = Boolean.FALSE;
			this.exactSdorderQtyFlag = Boolean.FALSE;
			this.autoPoolAssignmentFlag = Boolean.FALSE;
			this.autoCarrierAssignmentFlag = Boolean.FALSE;
			this.autoReleaseFlag = Boolean.FALSE;
			this.freeDeliveryFlag = Boolean.FALSE;
			this.releaseSapFlag = Boolean.TRUE;
			this.onHoldOrderAndLockedFlag = Boolean.FALSE;
			this.openCustorderFlag = Boolean.FALSE;
			this.sapSuppressMinChargeFlag = Boolean.FALSE;
		}

		initCompositeIdentifier();
	}

	/**
	 * Uses the {@link ShiptoSareaRestrEntity} to find and set the
	 * vehicleTypeCds
	 */
	private void setVehichleTypeCdsBasedOnRestriction() {

		List<Long> restVehicleTypeCds = new ArrayList<Long>();
		List<TruckWagonTypeWrapper> selectedVehicleTypeWrapperList = new ArrayList<TruckWagonTypeWrapper>();
		List<ShsareaRestrTruckTypeEntity> restrTruckTypeEntities = getShiptoSareaRestr()
				.getRestrTruckTypes();

		if (restrTruckTypeEntities != null) {
			for (ShsareaRestrTruckTypeEntity shsareaRestrTruckTypeEntity : restrTruckTypeEntities) {
				restVehicleTypeCds.add(shsareaRestrTruckTypeEntity
						.getTruckType().getTruckTypeCd());
				selectedVehicleTypeWrapperList.add(new TruckTypeWrapper(shsareaRestrTruckTypeEntity.getEvaluationPriority(), shsareaRestrTruckTypeEntity.getTruckType(),
						null));
			}
		}
		this.selectedVehicleTypeWrapperList = selectedVehicleTypeWrapperList;
		this.vehicleTypeCds = restVehicleTypeCds;

	}

	public void setDeliveryType(DeliveryTypeBsEntity deliveryType) {
		this.deliveryType = deliveryType;
	}

	/**
	 * @return
	 */
	private void initDeliverySettingType() {
		int deliverySettingType = DELIVERY_SETTING_NOT_DEFINED;

		if (this.sapContractItem != null && this.shiptoSareaRestr == null) {
			deliverySettingType = DELIVERY_SETTING_BASED_ON_CONTRACT;
		} else if (this.shiptoSareaRestr != null
				&& this.shiptoSareaRestr.getSapContractItem() == null) {

			deliverySettingType = DELIVERY_SETTING_BASED_ON_RESTRICTION;
		} else if (this.shiptoSareaRestr != null
				&& this.shiptoSareaRestr.getSapContractItem() != null) {
			deliverySettingType = DELIVERY_SETTING_BASED_ON_CONTRACT_COMBINED_RESTRICTION;
		} else if (getSarea() != null && this.sapMaterial != null) {
			deliverySettingType = DELIVERY_SETTING_NOT_DEFINED;
		} else {
			throw new LogonException(Reason.RES_ERROR_IMPL);
		}
		this.deliverySettingType = deliverySettingType;
	}

	/**
	 * The composite identifier is combined from the source objects, on what the
	 * delivery setting is based. It is important, that it is unique.
	 * 
	 * In the case of restrictions the restriction id guarantees that it is
	 * unique. For sap contract item it is not guaranteed to be unique, it is
	 * only unique if combined with the sap material id.
	 */
	private void initCompositeIdentifier() {

		String compositeIdentifier = "";
		compositeIdentifier += getSarea().getSareaId() + "_";
		compositeIdentifier += this.shiptoSareaRestr != null ? this.shiptoSareaRestr
				.getShiptoSareaRestrId() + "_"
				: "";
		compositeIdentifier += this.sapContractItem != null ? this.sapContractItem
				.getSapContractItemId() + "_"
				: "";
		compositeIdentifier += this.sapMaterial.getSapMaterialId();
		this.compositeIdentifier = compositeIdentifier;
	}

	public final Long getShippingpointId() {
		return (shippingpoint != null ? shippingpoint.getShippingpointId()
				: null);
	}

	public final Long getLoadingpointId() {
		return (loadingpoint != null ? loadingpoint.getLoadingpointId() : null);
	}

	public final Long getBilltoSapBpId() {
		return (billtoSapBp != null ? billtoSapBp.getSapBpId() : null);
	}

	public final Long getCommercialCarrierId() {
		return (commercialCarrier != null ? commercialCarrier.getCarrierId() : null);
	}

	public final Long getExecutingCarrierId() {
		return (executingCarrier != null ? executingCarrier.getCarrierId() : null);
	}

	public final Long getPoolId() {
		return (pool != null ? pool.getPoolId() : null);
	}

	public final Long getShiptoSiloId() {
		return (shiptoSilo != null ? shiptoSilo.getShiptoSiloId() : null);
	}

	public final Long getDeliveryTypeCd() {
		return (deliveryType != null ? deliveryType.getDeliveryTypeCd() : null);
	}

	public final String getProductionPlantCd() {
		return (productionPlant != null ? productionPlant.getPlantCd() : null);
	}

	public final String getUomCd() {
		return (uom != null ? uom.getUomCd() : null);
	}

	public final SareaEntity getSarea() {
		return sarea;
	}

	public final String getSupplyPlantCd() {
		return (supplyPlant != null ? supplyPlant.getPlantCd() : null);
	}

	public final SapMaterialEntity getSapMaterial() {

		return sapMaterial;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[compositeIdentifer=").append(this.compositeIdentifier);
		sb.append("][deliverySettingType=").append(this.deliverySettingType);
		sb.append("][sarea=").append(this.sarea != null ? this.sarea.getSareaId() : "null");
		sb.append("][sapMaterial=").append(this.getSapMaterialId());
		sb.append("][shippingpoint=").append(this.getShippingpointId());
		sb.append("][loadingpoint=").append(this.getLoadingpointId());
		sb.append("][billto=").append(this.getBilltoSapBpId());
		sb.append("][commercialCarrier=").append(this.getCommercialCarrierId());
		sb.append("][executingCarrier=").append(this.getExecutingCarrierId());
		sb.append("][pool=").append(this.getPoolId());
		sb.append("][silo=").append(this.getShiptoSiloId());
		sb.append("][deliveryTypeCd=").append(this.getDeliveryTypeCd());
		sb.append("][uomCd=").append(this.getUomCd());
		sb.append("][productionPlantCd=").append(this.getProductionPlantCd());
		sb.append("][quantity=").append(this.quantity);
		sb.append("][isQuantityControlled=").append(this.isQuantityControlled);
		sb.append("][supplyPlantCd=").append(this.getSupplyPlantCd());
		sb.append("][shiptoSareaRestrId=").append(this.getShiptoSareaRestrId());
		sb.append("][sapContractItemId=").append(this.getSapContractItemId()).append("]");
		return sb.toString();
	}

	public void setSupplyPlant(PlantBsEntity supplyPlant) {
		this.supplyPlant = supplyPlant;
	}

	public Long getPayerSapBpId() {
		return (getPayerSapBp() != null ? getPayerSapBp().getSapBpId() : null);
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public String getCompositeIdentifier() {
		return compositeIdentifier;
	}

	public ShiptoSareaRestrEntity getShiptoSareaRestr() {
		return shiptoSareaRestr;
	}

	public SapContractItemEntity getSapContractItem() {
		return sapContractItem;
	}

	public int getResolutionType() {
		return resolutionType;
	}

	public Long getSapContractItemId() {
		return sapContractItem != null ? sapContractItem.getSapContractItemId()
				: null;
	}

	public Long getSapMaterialId() {
		return sapMaterial.getSapMaterialId();
	}

	public Boolean getIsLandfillMaterial() {
		return sapMaterial.getMaterialIsLf();
	}

	public Long getShiptoSareaRestrId() {
		return shiptoSareaRestr != null ? shiptoSareaRestr
				.getShiptoSareaRestrId() : null;
	}

	public PlantBsEntity getProductionPlant() {
		return productionPlant;
	}

	public ShippingpointEntity getShippingpoint() {
		return shippingpoint;
	}

	public CarrierEntity getCommercialCarrier() {
		return commercialCarrier;
	}

	public CarrierEntity getExecutingCarrier() {
		return executingCarrier;
	}

	public ShiptoSiloEntity getShiptoSilo() {
		return shiptoSilo;
	}

	public SapBpEntity getBilltoSapBp() {
		return billtoSapBp;
	}

	public PlantBsEntity getSupplyPlant() {
		return supplyPlant;
	}

	public LoadingpointEntity getLoadingpoint() {
		return loadingpoint;
	}

	public DeliveryTypeBsEntity getDeliveryType() {
		return deliveryType;
	}

	public UomBsEntity getUom() {
		return uom;
	}

	public SapBpEntity getPayerSapBp() {
		return payerSapBp;
	}

	public PoolEntity getPool() {
		return pool;
	}

	public int getDeliverySettingType() {
		return deliverySettingType;
	}

	public String getContractType() {
		// return "DTO_HARDCODE_CONT_TYPE";
		return this.getContractTypeTxt();
	}

	/**
	 * If the sales area defines that the releasing of SAP is automatic, then
	 * this flag cannot be false.
	 * 
	 * @return
	 */
	public Boolean getReleaseSapFlag() {
		if (getShiptoSarea().getReleaseSapOptionalFlag().equals(Boolean.FALSE)) {
			return Boolean.TRUE;
		}
		if (getDeliveryTypeCd() != null
				&& LogonUtil.isRailOrder(getDeliveryTypeCd())) {
			return Boolean.TRUE;
		}

		return this.releaseSapFlag;
	}

	public Boolean getAutoPoolAssignmentFlag() {
		if (getDeliveryTypeCd() != null
				&& LogonUtil.isPickupOrder(getDeliveryTypeCd())) {
			return true;
		} else if (getPool() == null) {
			return false;
		} else {
			return this.autoPoolAssignmentFlag;
		}
	}

	public Boolean getAutoCarrierAssignmentFlag() {
		if (getDeliveryTypeCd() != null
				&& LogonUtil.isPickupOrder(getDeliveryTypeCd())) {
			return true;
		} else if (getCommercialCarrier() == null) {
			return false;
		} else if (getCommercialCarrier() != null
				&& getCommercialCarrier().getCarrierRequiresPlanningFlag().equals(
						Boolean.TRUE)) {
			return false;
		} else if (getAutoPoolAssignmentFlag() != null
				&& getAutoPoolAssignmentFlag().equals(Boolean.FALSE)) {
			return false;
		} else {
			return this.autoCarrierAssignmentFlag;
		}
	}

	/**
	 * For rail and simple vehicle orders the flag cannot be activated.
	 * 
	 * @return
	 */
	public Boolean getAutoReleaseFlag() {
		if (getDeliveryTypeCd() != null
				&& LogonUtil.isPickupOrder(getDeliveryTypeCd())) {
			return true;
		} else if (getDeliveryTypeCd() != null
				&& (LogonUtil.isRailOrder(getDeliveryTypeCd()) || LogonUtil
						.isSimpleVehicleOrder(getDeliveryTypeCd()))) {
			return false;
		} else if (getOnHoldOrderAndLockedFlag() != null
				&& getOnHoldOrderAndLockedFlag().equals(Boolean.TRUE)) {
			return false;
		} else {
			return this.autoReleaseFlag;
		}
	}

	/**
	 * 'Auto release' flag editability depends on the delivery type. chance to
	 * enable it on the UI.
	 * 
	 * @return
	 */
	public boolean isAutoReleaseFlagEditable() {

		if (getDeliveryTypeCd() != null
				&& (LogonUtil.isPickupOrder(getDeliveryTypeCd()) || LogonUtil
						.isServiceOnlyOrder(getDeliveryTypeCd()))) {
			return false;
		} else if (getDeliveryTypeCd() != null
				&& (LogonUtil.isRailOrder(getDeliveryTypeCd()) || LogonUtil
						.isSimpleVehicleOrder(getDeliveryTypeCd()))) {
			return false;
		} else if (getOnHoldOrderAndLockedFlag() != null
				&& getOnHoldOrderAndLockedFlag().equals(Boolean.TRUE)) {
			return false;
		}
		return true;
	}

	// 26-Jan-2014/toan: add new flag for on-hold order
	/**
	 * 'On Hold Order' flag editability depends on the delivery type.
	 * 
	 * @return false if the delivery type is pickup
	 */
	public boolean isOnHoldOrderAndLockedFlagEditable() {
		return !LogonUtil.isAnyTypeOfPickupOrder(getDeliveryTypeCd());
	}

	/**
	 * Free of charge flag cannot be other than false if the sap contract is a
	 * cash sales contract.
	 * 
	 * @return
	 */
	public Boolean getFreeDeliveryFlag() {
		if (isCashSales) {
			return Boolean.FALSE;
		}

		final Boolean freeDeliveryFlag = getShiptoSarea().getFreeDeliveryFlag();
		if (freeDeliveryFlag.equals(Boolean.FALSE)) {
			return false;
		}

		return this.freeDeliveryFlag;
	}

	public String getMaterialPackageCd() {
		return getSapMaterial().getMaterialPackage().getMaterialPackageCd();
	}

	public Boolean getMaterialIsAgg() {
		return getSapMaterial().getMaterialIsAgg();
	}

	public Boolean getMaterialIsLf() {
		return getSapMaterial().getMaterialIsLf();
	}

	public java.math.BigDecimal getPerSdorderQuantity() {
		return this.perSdorderQuantity;
	}

	public Boolean getExactCustorderQtyFlag() {
		if (getDeliveryTypeCd() != null
				&& (LogonUtil.isSimpleVehicleOrder(getDeliveryTypeCd())
						|| LogonUtil.isShipPickupOrder(getDeliveryTypeCd()) || LogonUtil
							.isBlockTrainPickupOrder(getDeliveryTypeCd()))) {
			return false;
		} else if (LogonPackageHandlingUtil.isBag(getSapMaterial())) {
			return false;
		} else if (uom != null && uom.getFuzzyQuantityFlag() != null
				&& uom.getFuzzyQuantityFlag().booleanValue()) {
			return false;
		} else {
			return this.exactCustorderQtyFlag;
		}
	}

	public Boolean getExactSdorderQtyFlag() {
		if (getDeliveryTypeCd() != null
				&& (LogonUtil.isSimpleVehicleOrder(getDeliveryTypeCd())
						|| LogonUtil.isShipPickupOrder(getDeliveryTypeCd()) || LogonUtil
							.isBlockTrainPickupOrder(getDeliveryTypeCd()))) {
			return true;
		} else if (LogonPackageHandlingUtil.isBag(getSapMaterial())) {
			return true;
		} else {
			return this.exactSdorderQtyFlag;
		}
	}

	public Boolean getOpenCustorderFlag() {
		if (LogonPackageHandlingUtil.isBag(getSapMaterial())) {
			return false;
		} else {
			return this.openCustorderFlag;
		}
	}

	public Boolean getOnHoldOrderAndLockedFlag() {
		if (LogonUtil.isAnyTypeOfPickupOrder(getDeliveryTypeCd())) {
			return false;
		}

		return this.onHoldOrderAndLockedFlag;
	}

	public java.math.BigDecimal getDeliveryFreqQtyperhre() {
		return this.deliveryFreqQtyperhre;
	}

	public Double getDeliveryFreqEveryTime() {
		if (LogonPackageHandlingUtil.isBag(getSapMaterial())) {
			return null;
		} else {
			return this.deliveryFreqEveryTime;
		}
	}

	public String getDeliveryFreqEveryTimeUomCd() {
		if (LogonPackageHandlingUtil.isBag(getSapMaterial())) {
			return null;
		} else {
			return this.deliveryFreqEveryTimeUomCd;
		}
	}

	public Double getDeliveryTimeTolerance() {
		if (LogonPackageHandlingUtil.isBag(getSapMaterial())) {
			return null;
		} else {
			return this.deliveryTimeTolerance;
		}
	}

	public String getDeliveryTimeToleranceUomCd() {
		if (LogonPackageHandlingUtil.isBag(getSapMaterial())) {
			return null;
		} else {
			return this.deliveryTimeToleranceUomCd;
		}
	}

	public Boolean getSapSuppressMinChargeFlag() {
		return this.sapSuppressMinChargeFlag;
	}

	public FireqMaterialMixdesignEntity getMixDesign() {
		return this.mixDesign;
	}

	public Long getMixDesignId() {
		return getMixDesign() != null ? getMixDesign().getMixDesignId() : null;
	}

	public UomBsEntity getPerSdorderUom() {
		return this.perSdorderUom;
	}

	public String getPerSdorderUomCd() {
		return getPerSdorderUom() != null ? getPerSdorderUom().getUomCd()
				: null;
	}

	public UomBsEntity getDeliveryFreqQtyperhreUom() {
		if (LogonPackageHandlingUtil.isBag(getSapMaterial())) {
			return null;
		} else {
			return this.deliveryFreqQtyperhreUom;
		}
	}

	public String getDeliveryFreqQtyperhreUomCd() {
		return getDeliveryFreqQtyperhreUom() != null ? getDeliveryFreqQtyperhreUom()
				.getUomCd() : null;
	}

	/**
	 * @param deliveryOptionConfiguration
	 */
	public void setDeliveryOptionConfiguration(
			DeliveryOptionConfigurationDTO deliveryOptionConfiguration) {
		this.deliveryOptionConfiguration = deliveryOptionConfiguration;

	}

	public DeliveryOptionConfigurationDTO getDeliveryOptionConfiguration() {
		return deliveryOptionConfiguration;
	}

	// public Boolean getAvailableInCustWebFlag() {
	// return getShiptoSareaRestr() != null ?
	// getShiptoSareaRestr().getAvailableInCustWebFlag() : null;
	// }
	//
	// public Boolean getAvailableInPickupAppFlag() {
	// return getShiptoSareaRestr() != null ?
	// getShiptoSareaRestr().getAvailableInPickupAppFlag() : null;
	// }

	// public Boolean getEnabledFlag() {
	// return getShiptoSareaRestr() != null ?
	// getShiptoSareaRestr().getEnabledFlag() : Boolean.FALSE;
	// }
	//
	//
	public Long getBilltoRuletypeCd() {
		return getShiptoSareaRestr() != null ? (getShiptoSareaRestr()
				.getBilltoRuletype() != null ? getShiptoSareaRestr()
				.getBilltoRuletype().getBilltoRuletypeCd() : null) : null;
	}

	public String getNoteOrderMgmt() {
		return getShiptoSareaRestr() != null ? getShiptoSareaRestr()
				.getNoteOrderMgmt() : null;
	}

	public String getNoteDelivery() {
		return getShiptoSareaRestr() != null ? getShiptoSareaRestr()
				.getNoteDelivery() : null;
	}

	public String getNoteDispatching() {
		return getShiptoSareaRestr() != null ? getShiptoSareaRestr()
				.getNoteDispatching() : null;
	}

	public String getNoteTruckDriver() {
		return getShiptoSareaRestr() != null ? getShiptoSareaRestr()
				.getNoteTruckDriver() : null;
	}

	public String getNoteRailwayDisp() {
		return getShiptoSareaRestr() != null ? getShiptoSareaRestr()
				.getNoteRailwayDisp() : null;
	}

	/**
	 * In case the delivery frequency is set, it is(will be) a performance
	 * order. Used for determining if certain comboboxes are not editable. See
	 * switch customer order mode button.
	 * 
	 * @return
	 */
	private Boolean isPerformanceCustorder() {
		return getDeliveryFreqQtyperhre() != null;
	}

	public Long getSapContractId() {
		return getSapContractItem() != null ? getSapContractItem()
				.getSapContract().getSapContractId() : null;
	}

	public String getSapContractNo() {
		return getSapContractItem() != null ? getSapContractItem()
				.getSapContract().getSapContractNo() : null;
	}

	public Long getContractItemNo() {
		return getSapContractItem() != null ? getSapContractItem()
				.getContractItemNo() : null;
	}

	public String getSapContractIncoterms() {
		return getSapContractItem() != null ? StringUtils
				.defaultString(getSapContractItem().getSapContract()
						.getIncotermsCd()) : null;
	}

	public Long getContractSapMaterialId() {
		return getSapContractItem() != null ? getSapContractItem()
				.getSapMaterial().getSapMaterialId() : null;
	}

	public String getContractSapMaterialTxt() {
		return getSapContractItem() != null ? getSapContractItem()
				.getSapMaterial().getSapMaterialNo() : null;
	}

	public String getSupplyPlantName() {
		return getSupplyPlant() != null ? getSupplyPlant().getPlantName()
				: null;
	}

	public String getSareaDisplayName() {
		return getSarea().getSareaDisplayLongname();
	}

	public String getSiloName() {
		return getShiptoSilo() != null ? getShiptoSilo().getSiloName() : null;
	}

	public String getPayerName() {
		return getPayerSapBp() != null ? getPayerSapBp().getName1() : null;
	}

	public String getCommercialCarrierName() {
		return commercialCarrier != null ? commercialCarrier.getDisplayName() : null;
	}

	public String getExecutingCarrierName() {
		return executingCarrier != null ? executingCarrier.getDisplayName() : null;
	}

	public String getLoadingpointName() {
		return getLoadingpoint() != null ? getLoadingpoint()
				.getLoadingpointName() : null;
	}

	public String getBilltoName() {
		return getBilltoSapBp() != null ? getBilltoSapBp().getName1() : null;
	}

	public String getDeliveryPlantName() {
		ShippingpointEntity shippingpoint = getShippingpoint();
		if (shippingpoint != null && shippingpoint.getPlant() != null) {
			return shippingpoint.getPlant().getPlantName();
		} else {
			return "";
		}
	}

	public String getShippingpointName() {
		return getShippingpoint() != null ? getShippingpoint()
				.getShippingpointName() : null;
	}

	public String getSapMaterialNo() {
		return getSapMaterial().getSapMaterialNo();
	}
	
	public BigDecimal getNetPrice() {
		return getSapContractItem() != null ? getSapContractItem().getNetValue() : null;
	
	}
	
	public String getCurrency() {
		return getSapContractItem() != null ? getSapContractItem()
			.getSapContract().getCurrency().getCurrencyCd() : null;
	}
	
	public BigDecimal getVAT() {
		return getSapContractItem() != null ? getSapContractItem()
			.getVatPercentage() : null;
	}

	public Long getLogonPackageTypeCd() {
		return this.getSapMaterial().getMaterialPackage().getLogonPackageType()
				.getPackageTypeCd();
	}

	public Boolean getShowDeliveryPlantWebOrderFlag() {
		return showDeliveryPlantWebOrderFlag;
	}

	public Boolean getShowPriceInWebOrderFlag() {
		return showPriceInWebOrderFlag;
	}

	public String getContractValidityFromDate() {
		String result = null;
		if( getSapContractItem() != null ) {
			SapContractEntity sapContract = getSapContractItem().getSapContract();
			if( sapContract != null ) {
				if( shipToTimeZone != null ) {
					result = shipToTimeZone.fmtAsDayMonthYearHourMinuteWithOffset(sapContract.getValidFromDat());
				}
			}
		}
		return result;
	}
	
	public String getContractValidityToDate() {
		String result = null;
		if( getSapContractItem() != null ) {
			SapContractEntity sapContract = getSapContractItem().getSapContract();
			if( sapContract != null ) {
				if( shipToTimeZone != null ) {
					result = shipToTimeZone.fmtAsDayMonthYearHourMinuteWithOffset(sapContract.getValidUntilDat());
				}
			}
		}
		return result;
	}
	
	public String getSearchByText() {

		if (getAdditionalData() == null) {
			throw new LogonException(
					"searchByText is supposed to use additional data which needs to be loaded in order to be used",
					Reason.RES_ERROR_IMPL);
		}
		String text = "";

		text += StringUtils.isNotEmpty(getSapMaterialNo()) ? getSapMaterialNo()
				+ "_" : "";
		text += StringUtils.isNotEmpty(getSapContractNo()) ? getSapContractNo()
				+ "(" + getContractItemNo() + ")" + "_" : "";
		text += StringUtils.isNotEmpty(getSareaDisplayName()) ? getSareaDisplayName()
				+ "_"
				: "";
		text += StringUtils.isNotEmpty(getContractSapMaterialTxt()) ? getContractSapMaterialTxt()
				+ "_"
				: "";
		text += StringUtils.isNotEmpty(getAdditionalData().txt) ? getAdditionalData().txt
				+ "_"
				: "";
		return text;
	}

	public String getDeliveryTypeTxt() {
		return getAdditionalData().deliveryTypeTxt;
	}

	public String getMaterialPackageTxt() {
		return getAdditionalData().materialPackageTxt;
	}

	public String getTxt() {
		return getAdditionalData().txt;
	}

	public String getPoolName() {
		return getAdditionalData().poolName;
	}

	public String getUomTxt() {
		return getAdditionalData().uomTxt;
	}

	public String getContractTypeTxt() {
		return getAdditionalData().contractTypeTxt;
	}

	/**
	 * Inner class holding additional data for this delivery setting, which
	 * needs language resolution, and cannot be determined from the Entity
	 * references.
	 * 
	 * @author AdNovum Informatik AG
	 */
	public class DeliverySettingLangDepData implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private String deliveryTypeTxt;
		private String materialPackageTxt;
		private String txt;
		private String poolName;
		private String uomTxt;
		private String contractTypeTxt;

		public void setDeliveryTypeTxt(String deliveryTypeTxt) {
			this.deliveryTypeTxt = deliveryTypeTxt;

		}

		public void setMaterialPackageTxt(String txt) {
			this.materialPackageTxt = txt;

		}

		public void setTxt(String txt) {
			this.txt = txt;

		}

		public void setPoolName(String poolName) {
			this.poolName = poolName;
		}

		public void setUomTxt(String uomTxt) {
			this.uomTxt = uomTxt;
		}

		public void setContractTypeTxt(String contractTypeTxt) {
			this.contractTypeTxt = contractTypeTxt;
		}
	}

	public DeliverySettingLangDepData getAdditionalData() {
		return langDepData;
	}

	/**
	 * @return the selectedVehicleTypeWrapperList
	 */
	public List<TruckWagonTypeWrapper> getSelectedVehicleTypeWrapperList() {
		return selectedVehicleTypeWrapperList;
	}

	/**
	 * Free of charge editability depends on the contract type and the sales
	 * area setting free of charge delivery is possible.
	 * 
	 * @return
	 */
	public boolean isFreeOfChargeEditable() {
		if (isCashSales) {
			return Boolean.FALSE;
		}
		
		final Boolean freeDeliveryFlag = getShiptoSarea().getFreeDeliveryFlag();
		if (freeDeliveryFlag.equals(Boolean.FALSE)) {
			return false;
		}
		return true;
	}

	/**
	 * Release SAP editability depends on the sales area setting: in case
	 * automatic, there is no chance to enable it on the UI.
	 * 
	 * @return
	 */
	public boolean isReleaseSapFlagEditable() {

		final Boolean releaseSapOptionalFlag = getShiptoSarea()
				.getReleaseSapOptionalFlag();
		if (releaseSapOptionalFlag.equals(Boolean.FALSE)) {
			return false;
		}
		return true;
	}

	/**
	 * Production plant editability depends on the fact that the sap material
	 * has a production plant attribute or not. In case it has, the setting will
	 * return this production plant, and it has to be disabled.
	 * 
	 * @return
	 */
	public boolean isProductionPlantEditable() {
		PlantBsEntity defaultProdPlant = getSapMaterial().getProdPlant();

		// if defaultProdPlant AND not equals with the one what is saved in the
		// restriction. This can happen if materials gets an update for example.
		if (defaultProdPlant != null
				&& getProductionPlant() != null
				&& defaultProdPlant.getPlantCd().equals(
						getProductionPlant().getPlantCd())) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Editability of the exactCustomerOrderQtyFlag depends on the packaging
	 * type.
	 * 
	 * @return
	 */
	public boolean isExactCustorderQtyFlagEditable() {

		if (getDeliveryTypeCd() != null
				&& (LogonUtil.isSimpleVehicleOrder(getDeliveryTypeCd())
						|| LogonUtil.isShipPickupOrder(getDeliveryTypeCd())
						|| LogonUtil
								.isBlockTrainPickupOrder(getDeliveryTypeCd()) || LogonUtil
							.isServiceOnlyOrder(getDeliveryTypeCd()))) {
			return false;
		} else if (LogonPackageHandlingUtil.isBag(getSapMaterial())
				|| LogonPackageHandlingUtil.isService(getSapMaterial())) {
			return false;
		} else if (LogonUtil.isPlanningMaterial(getSapMaterial())) {
			return false;
		} else if (uom != null && uom.getFuzzyQuantityFlag() != null
				&& uom.getFuzzyQuantityFlag().booleanValue()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Editability of the openCustorderFlag depends on the packaging type.
	 * 
	 * @return
	 */
	public boolean isOpenCustorderFlagEditable() {

		if (LogonPackageHandlingUtil.isBag(getSapMaterial())) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Auto pool flag editability depends on the delivery type of the order.
	 * 
	 * @return
	 */
	public boolean isAutoPoolAssignmentFlagEditable() {

		if (getDeliveryTypeCd() != null
				&& (LogonUtil.isPickupOrder(getDeliveryTypeCd()) || LogonUtil
						.isServiceOnlyOrder(getDeliveryTypeCd()))) {
			return false;
		} else if (getPool() == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Auto carrier flag editability depends on the delivery type of the order.
	 * @return
	 */
	public boolean isAutoCarrierAssignmentFlagEditable() {

		if (getDeliveryTypeCd() != null
				&& (LogonUtil.isPickupOrder(getDeliveryTypeCd()) || LogonUtil
						.isServiceOnlyOrder(getDeliveryTypeCd()))) {
			return false;
		}
		if (getCommercialCarrier() != null
				&& getCommercialCarrier().getCarrierRequiresPlanningFlag().equals(
						Boolean.TRUE)) {
			return false;
		} else if (getPool() == null || getCommercialCarrier() == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Editability of the ExactSdorderQtyFlag depends on the packaging type.
	 * 
	 * @return
	 */
	public boolean isExactSdorderQtyFlagEditable() {

		if (getDeliveryTypeCd() != null
				&& (LogonUtil.isSimpleVehicleOrder(getDeliveryTypeCd())
						|| LogonUtil.isShipPickupOrder(getDeliveryTypeCd())
						|| LogonUtil
								.isBlockTrainPickupOrder(getDeliveryTypeCd()) || LogonUtil
							.isServiceOnlyOrder(getDeliveryTypeCd()))) {
			return false;
		} else if (LogonPackageHandlingUtil.isBag(getSapMaterial())
				|| LogonPackageHandlingUtil.isService(getSapMaterial())) {
			return false;
		} else if (LogonUtil.isPlanningMaterial(getSapMaterial())) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Editability of the DeliveryFreqEveryTime depends on the packaging type.
	 * 
	 * @return
	 */
	public boolean isDeliveryFreqEveryTimeEditable() {
		if (getDeliveryTypeCd() != null
				&& LogonUtil.isServiceOnlyOrder(getDeliveryTypeCd())) {
			return false;
		}
		if (LogonPackageHandlingUtil.isBag(getSapMaterial())) {
			return false;
		}
		if (LogonUtil.isPlanningMaterial(getSapMaterial())) {
			return false;
		} else {
			if (isPerformanceCustorder()) {
				return false;
			}
			return true;
		}
	}

	/**
	 * Editability of the DeliveryFreqEveryTimeUomCd depends on the packaging
	 * type.
	 * 
	 * @return
	 */
	public boolean isDeliveryFreqEveryTimeUomCdEditable() {
		if (getDeliveryTypeCd() != null
				&& LogonUtil.isServiceOnlyOrder(getDeliveryTypeCd())) {
			return false;
		}
		if (LogonPackageHandlingUtil.isBag(getSapMaterial())) {
			return false;
		}
		if (LogonUtil.isPlanningMaterial(getSapMaterial())) {
			return false;
		} else {
			if (isPerformanceCustorder()) {
				return false;
			}
			return true;
		}
	}

	/**
	 * Editability of the DeliveryTimeTolerance depends on the packaging type.
	 * 
	 * @return
	 */
	public boolean isDeliveryTimeToleranceEditable() {
		if (getDeliveryTypeCd() != null
				&& LogonUtil.isServiceOnlyOrder(getDeliveryTypeCd())) {
			return false;
		}
		if (LogonPackageHandlingUtil.isBag(getSapMaterial())) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Editability of the DeliveryTimeToleranceUom depends on the packaging
	 * type.
	 * 
	 * @return
	 */
	public boolean isDeliveryTimeToleranceUomCdEditable() {
		if (getDeliveryTypeCd() != null
				&& LogonUtil.isServiceOnlyOrder(getDeliveryTypeCd())) {
			return false;
		}
		if (LogonPackageHandlingUtil.isBag(getSapMaterial())) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Editability of the PerSdorderQuantity depends on the ordered UoM.
	 * 
	 * @return
	 */
	public boolean isPerSdorderQuantityEditable() {
		if (getDeliveryTypeCd() != null
				&& LogonUtil.isServiceOnlyOrder(getDeliveryTypeCd())) {
			return false;
		}
		if (uom != null && uom.getFuzzyQuantityFlag() != null
				&& uom.getFuzzyQuantityFlag().booleanValue()) {
			return false;
		}
		return true;
	}

	/**
	 * Editability of the PerSdorderUom depends on the packaging type.
	 * 
	 * @return
	 */
	public boolean isPerSdorderUomEditable() {
		if (getDeliveryTypeCd() != null
				&& LogonUtil.isServiceOnlyOrder(getDeliveryTypeCd())) {
			return false;
		}

		return true;
	}

	/**
	 * Editability of the DeliveryFreqQtyperhre depends on the packaging type.
	 * 
	 * @return
	 */
	public boolean isDeliveryFreqQtyperhreEditable() {
		if (getDeliveryTypeCd() != null
				&& LogonUtil.isServiceOnlyOrder(getDeliveryTypeCd())) {
			return false;
		}
		if (LogonPackageHandlingUtil.isBag(getSapMaterial())) {
			return false;
		}
		if (LogonUtil.isPlanningMaterial(getSapMaterial())) {
			return false;
		} else {
			if (!isPerformanceCustorder()) {
				return false;
			}
			return true;
		}
	}

	/**
	 * Editability of the DeliveryFreqQtyperhreUom depends on the packaging
	 * type.
	 * 
	 * @return
	 */
	public boolean isDeliveryFreqQtyperhreUomEditable() {
		if (getDeliveryTypeCd() != null
				&& LogonUtil.isServiceOnlyOrder(getDeliveryTypeCd())) {
			return false;
		}
		if (LogonPackageHandlingUtil.isBag(getSapMaterial())) {
			return false;
		}
		if (LogonUtil.isPlanningMaterial(getSapMaterial())) {
			return false;
		} else {
			if (!isPerformanceCustorder()) {
				return false;
			}
			return true;
		}
	}

	public boolean isDeliveryTypeEditable() {
		if (getDeliveryTypeCd() != null
				&& LogonUtil.isServiceOnlyOrder(getDeliveryTypeCd())) {
			return false;
		}
		return true;
	}

	public boolean isSupplyPlantEditable() {
		if (getDeliveryTypeCd() != null
				&& LogonUtil.isServiceOnlyOrder(getDeliveryTypeCd())) {
			return false;
		}
		return true;
	}

	public boolean isCarrierEditable() {
		if (getDeliveryTypeCd() != null
				&& LogonUtil.isServiceOnlyOrder(getDeliveryTypeCd())) {
			return false;
		}
		return true;
	}

	public boolean isPoolEditable() {
		if (getDeliveryTypeCd() != null
				&& LogonUtil.isServiceOnlyOrder(getDeliveryTypeCd())) {
			return false;
		}
		return true;
	}

	public boolean isSapSuppressMinChargeEditable() {
		if (getDeliveryTypeCd() != null
				&& LogonUtil.isServiceOnlyOrder(getDeliveryTypeCd())) {
			return false;
		}
		return true;
	}

	public boolean isShiptoSiloEditable() {
		if (getDeliveryTypeCd() != null
				&& LogonUtil.isReturnPickupOrder(getDeliveryTypeCd())) {
			return false;
		}
		return true;
	}

	public String getServiceProcessIndicatorId() {
		return serviceProcessIndicatorId;
	}

	public void setServiceProcessIndicatorId(String serviceProcessIndicatorId) {
		this.serviceProcessIndicatorId = serviceProcessIndicatorId;
	}

	public boolean isQuantityControlled() {
		return isQuantityControlled;
	}
	
}
