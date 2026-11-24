/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ordercreation;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.crh.logon.business.ordercreation.dto.AbstractDeliverySetting;
import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.constants.ShiptoSalesAreaError;

public class DeliverySettingProductMap implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final DeliverySettingDTO DELIVERY_SETTING_NOT_FOUND = null;

	/**
	 * Parent {@link DeliverySettingSalesAreaMap} of this object. 
	 */
	private final DeliverySettingSalesAreaMap salesAreaMap;

	private final Map<Object, List<ShiptoSalesAreaError>> invalidMap = new HashMap<Object, List<ShiptoSalesAreaError>>();

	protected Collection<DeliverySettingDTO> possibleDeliverySettings = null;

	/**
	 * Depending on the caller delivery setting context initializes the group of available delivery settings.
	 * Example: for order taking, all delivery settings, which come from restrictions have to be initialized.
	 * In the meantime all the contract delivery settings have to be available, which are not yet overridden with a
	 * restriction delivery setting.
	 * 
	 * For web order, only delivery settings based on restrictions have to be initialized, only those count as valid
	 * selections.
	 * 
	 * For loading systems currently only contracts count as base of the possible delivery settings.
	 * @param deliverySettingSalesAreaMap
	 * @param possibleDeliverySettings
	 */
	public DeliverySettingProductMap(DeliverySettingSalesAreaMap deliverySettingSalesAreaMap, List<DeliverySettingDTO> possibleDeliverySettings) {
		this.salesAreaMap = deliverySettingSalesAreaMap;
		this.possibleDeliverySettings = possibleDeliverySettings;
	}	
	
	public void setPossibleDeliverySettings(Collection<DeliverySettingDTO> possibleDeliverySettings) {
		this.possibleDeliverySettings = possibleDeliverySettings;
	}

	/**
	 * In some cases there is a need to match a delivery setting to a delivery settings available.
	 * This is supposed to happen for example in case an delivery setting formed from the attributes
	 * of an order must be matched with a delivery setting which is based on a default. Purpose: if
	 * the default is still available, the setting is still valid. The method {@link #findDeliverySettingByCompositeIdentifier(String)}
	 * would not return it as the order knows nothing about the default - if any - on what it was once based on.
	 * The restriction id is not available in the delivery setting formed from the order/order item, but
	 * is part of the composite identifier of the delivery setting based on the default.
	 * 
	 * (Probably this counts as a workaround, but the composite identifier needed to be 
	 * unique, and it is not true without considering the restriction id as part of the identifier.
	 * 
	 * @param deliverySetting
	 * @return if found a complete match return that
	 */
	public DeliverySettingDTO matchDeliverySetting(AbstractDeliverySetting setting) {
		DeliverySettingDTO matchingDeliverySetting = null;
		String compositeIdentifier = setting.getCompositeIdentifier();
		matchingDeliverySetting = findByCompositeIdentifier(compositeIdentifier);
		
		if (matchingDeliverySetting != null) {
			return matchingDeliverySetting;
		}
		
		for (DeliverySettingDTO deliverySetting : possibleDeliverySettings) {
			//otherwise match ALL MEANINGFUL attributes of the setting, and return the first one which is a match
			if (setting.match(deliverySetting)) {
				return deliverySetting;
			}
		}
		return null;
	}

	public DeliverySettingDTO findByCompositeIdentifier(String compositeIdentifier) {
		for (DeliverySettingDTO currentDeliverySetting : possibleDeliverySettings) {
			String currentCompositeIdentifier = currentDeliverySetting.getCompositeIdentifier();
			if (currentCompositeIdentifier.equals(compositeIdentifier)) {
				return currentDeliverySetting;
			}
		}
		return DELIVERY_SETTING_NOT_FOUND;
	}


	/**
	 * The list of {@link #possibleDeliverySettingsWrapped} partitioned by sapContractId.
	 * 
	 * @return
	 */
	public Collection<DeliverySettingDTO> getPossibleDeliverySettings() {
		return possibleDeliverySettings;
	}


	/**
	 * Validates all delivery settings.
	 */
	protected void validateDeliverySettings() {
		for (DeliverySettingDTO setting : possibleDeliverySettings) {
			final DeliveryOptionConfigurationDTO deliveryOptionConfiguration = DeliverySettingFilterLogic.getDeliveryOptionConfiguration(setting, this.salesAreaMap);
			salesAreaMap.validateDeliverySetting(setting, deliveryOptionConfiguration, invalidMap);
			setting.setDeliveryOptionConfiguration(deliveryOptionConfiguration);
		}
	}

	public Map<Object, List<ShiptoSalesAreaError>> getInvalidMap() {
		return invalidMap;
	}


	public DeliverySettingSalesAreaMap getSalesAreaMap() {
		return salesAreaMap;
	}

}
