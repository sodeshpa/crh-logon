/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ordercreation.dto;

import java.util.List;

import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.OrderUtils;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.refdata.MaterialPackageBsEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;

/**
 * @author AdNovum Informatik AG
 */
public abstract class AbstractDeliverySetting implements DeliverySetting{

	protected SapContractItemEntity sapContractItem;
	protected SareaEntity sarea;
	
	//please make sure that in case you name field "sapMaterial" differently the code parts which use this also change!
	//name of the property attribute
	public static final String ATTR_SAP_MATERIAL_ID = "sapMaterial." + SapMaterialEntity.ATTR_SAP_MATERIAL_ID;
	public static final String ATTR_MATERIAL_PACKAGE_CD = "sapMaterial.materialPackage." + MaterialPackageBsEntity.ATTR_MATERIAL_PACKAGE_CD;	
	protected SapMaterialEntity sapMaterial;

	//please make sure that in case you name field "compositeIdentifier" differently the code parts which use this also change!
	//name of the property attribute
	public static final String ATTR_COMPOSITE_IDENTIFIER = "compositeIdentifier";
	protected String compositeIdentifier;
	protected int deliverySettingType;

	protected List<Long> vehicleTypeCds;
	private boolean availableInPickupAppFlagEnabled;
	
	public void setAvailableInPickupAppFlagEnabled(boolean availableInPickupAppFlagEnabled) {
		this.availableInPickupAppFlagEnabled = availableInPickupAppFlagEnabled;
	}

	/**
	 * Not all attributes have to be considered, only the meaningful ones, which 
	 * cannot be left empty for any order. For example if the order's carrier was carrier A
	 * and the carrier of the new default is empty, that only means that the new order will
	 * not set the carrier in advance. Order taking is still possible for this basic setting.
	 * 
	 */
	public boolean match( DeliverySetting matchTo ) {
		if ( typeMatchValue(this.sarea.getSareaId(), matchTo.getSarea().getSareaId())  &&
				typeMatchValue(this.getSapContractItemId(), matchTo.getSapContractItemId() )&&
				typeMatchValue(this.getSapMaterialId(), matchTo.getSapMaterialId()) ){
			return true;
		}
		return false;
	}
	
	private static boolean typeMatchValue(Long subject, Long matchTo) {
		if (basicMatchValue(subject, matchTo)) {
			if (subject != null && matchTo != null) {
				return subject.equals(matchTo);
			}
			return true;
		}
		return false;
	}
	
	private static boolean basicMatchValue(Object subject, Object matchTo) {
		if ( subject != null && matchTo == null ) {
			return false;
		}
		if ( subject == null && matchTo != null ) {
			return false;
		}
		return true;
	}
	
	public List<Long> getVehicleTypeCds() {
		return vehicleTypeCds;
	}

	public void setVehicleTypeCds(List<Long> vehicleTypeCds) {
		this.vehicleTypeCds = vehicleTypeCds;
	}	

	/**
	 * Dispocard setting with preset payer should be completely omitted
	 * if the contract type's and sales area's auto payer-determination flag
	 * are both set. See LOGON-6762 for details.
	 * 
	 * @return true if this dispocard setting can be used during ordertaking 
	 */
	public boolean isAutoPayerSettingValid() {
		boolean result = true;
		Long payerId = getPayerSapBpId();//preset payer
		if (payerId != null && sapContractItem != null) {
			//auto-payer determ. flags are true:
			boolean isPayerIgnored = OrderUtils.isPayerIgnored(sapContractItem.getSapContract());
			if (isPayerIgnored) {
				return result = false;
			}
		}
		return result;
	}
	
	/**
	 * Lots of rules for dispocard validation whether it can be used in the Terminal Application.
	 * 
	 * Production plant, UoM, ShippingPoint is required.
	 * Only materials with EXW (or REXW) incoterm type contract can be used.
	 * Planning material is not available in terminal.
	 * Loadingpoint validation depends on material type.
	 * 
	 */
	public boolean isAvailableInPickupAppFlagEnabled() {
		return availableInPickupAppFlagEnabled;
	}
	
	/*
	 * Planning material is not available as web order.
	 */
	public boolean isAvailableInCustWebFlagEnabled() {
		if (getDeliveryTypeCd() != null && LogonUtil.isReturnPickupOrder(getDeliveryTypeCd())) {
			return false;
		}
		if (LogonUtil.isPlanningMaterial(sapMaterial)) {
			return false;
		}
		return true;
	}

	public boolean isPayerIgnored() {
		return (sapContractItem != null)
				&& OrderUtils.isPayerIgnored(sapContractItem.getSapContract());
	}

}
