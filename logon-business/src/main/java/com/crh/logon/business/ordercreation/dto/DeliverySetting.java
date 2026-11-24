/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ordercreation.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;

/**
 *
 * @author AdNovum Informatik AG
 *
 */
public interface DeliverySetting extends Serializable{

	public static long serialVersionUID = 1L;

	public ShiptoSareaRestrEntity getShiptoSareaRestr();
	public SapContractItemEntity getSapContractItem();
	public SareaEntity getSarea();
	public SapMaterialEntity getSapMaterial();

	public Long getShippingpointId();
	public Long getLoadingpointId();
	public Long getBilltoSapBpId();
	public Long getCommercialCarrierId();
	public Long getPoolId();
	public Long getShiptoSiloId();
	public Long getDeliveryTypeCd();
	public String getProductionPlantCd();
	public String getUomCd();
	public String getSupplyPlantCd();
	public Long getPayerSapBpId();
	public BigDecimal getQuantity();
	public List<Long> getVehicleTypeCds();

	public String getCompositeIdentifier();

	public int getResolutionType();
	public int getDeliverySettingType();
	public Long getShiptoSareaRestrId();
	public Long getSapContractItemId();
	public Long getSapMaterialId();
	
	/**
	 * Returns true if the {@link DeliverySetting} is considered a match of
	 * the specified {@link DeliverySetting}
	 * @param matchTo
	 * @return
	 */
	public boolean match(final DeliverySetting matchTo);
	public boolean isAutoPayerSettingValid();
	public boolean isAvailableInPickupAppFlagEnabled();
	public boolean isAvailableInCustWebFlagEnabled();

	public void setAvailableInPickupAppFlagEnabled(boolean isAvailableInPickupAppFlagEnabled);
	
}
