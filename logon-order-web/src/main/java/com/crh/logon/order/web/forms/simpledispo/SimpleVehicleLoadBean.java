/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.simpledispo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedList;

import com.crh.logon.business.services.beans.SimpleVehicleLoadableItem;

/**
 * Holds the Simple Vehicle Load popup related information.
 * 
 * @author AdNovum Informatik AG
 */
public class SimpleVehicleLoadBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/** The ID of the simple vehicle to load */
	private final Long simpleVehicleId;
	
	/** These holds the data to show on the popup. */
	private final Collection<SimpleVehicleLoadableItem> loadableItems;

	/** The collection of SimpleVehicleLoadSettingsDTO objects to store the user entered data. */
	private Collection<SimpleVehicleLoadSettingsDTO> userSettings;

	public SimpleVehicleLoadBean(Long simpleVehicleId,
			Collection<SimpleVehicleLoadableItem> loadItemsEntities) {

		this.simpleVehicleId = simpleVehicleId;
		this.loadableItems = loadItemsEntities;

		this.setUserSettings(initUserSettings(loadItemsEntities));
	}
	
	/** Initializes the userSettings by creating a collection of SimpleVehicleLoadSettingsDTO. */
	private static final Collection<SimpleVehicleLoadSettingsDTO> initUserSettings(
			Collection<SimpleVehicleLoadableItem> loadableItems) {
		
		Collection<SimpleVehicleLoadSettingsDTO> userSettings = new LinkedList<SimpleVehicleLoadSettingsDTO>();

		for (SimpleVehicleLoadableItem loadableItem : loadableItems) {
			BigDecimal loadedQuantity = loadableItem.getLoadOrderItem().getLoadedQuantity();
			// If the order is not loaded yet, initialize loaded quantity with zero.
			if (loadedQuantity == null) {
				loadedQuantity = BigDecimal.valueOf(0);
			}
			SimpleVehicleLoadSettingsDTO userSetting = new SimpleVehicleLoadSettingsDTO(
					loadableItem.getLoadOrderItem().getOrderItemId(), 
					loadableItem.getLoadOrderItem().getLoadingpointId(),
					loadedQuantity);

			userSettings.add(userSetting);
		}

		return userSettings;
	}
	
	public Collection<SimpleVehicleLoadableItem> getLoadableItems() {
		return loadableItems;
	}
	
	public Long getSimpleVehicleId() {
		return simpleVehicleId;
	}

	public Collection<SimpleVehicleLoadSettingsDTO> getUserSettings() {
		return userSettings;
	}

	public void setUserSettings(Collection<SimpleVehicleLoadSettingsDTO> userSettings) {
		this.userSettings = userSettings;
	}
	
	/**
	 * Indexed property getter for SimpleVehicleLoadSettingsDTO.getLoadedQty().
	 */
	public BigDecimal getLoadedQtyIndexed(int index) {
		BigDecimal result = null;

		int idx = 0;
		for (SimpleVehicleLoadSettingsDTO userSetting : userSettings) {
			if (idx == index) {
				result = userSetting.getLoadedQty();
				break;
			}
			idx++;
		}
		return result;
	}
	
	/**
	 * Indexed property setter for SimpleVehicleLoadSettingsDTO.setLoadedQty().
	 */
	public void setLoadedQtyIndexed(int index, BigDecimal value) {
		int idx = 0;
		for (SimpleVehicleLoadSettingsDTO userSetting : userSettings) {
			if (idx == index) {
				userSetting.setLoadedQty(value);
				break;
			}
			idx++;
		}
	}
	
	/**
	 * Indexed property getter for SimpleVehicleLoadSettingsDTO.getLoadingPointId().
	 */
	public Long getLoadingPointIdIndexed(int index) {
		Long result = null;

		int idx = 0;
		for (SimpleVehicleLoadSettingsDTO userSetting : userSettings) {
			if (idx == index) {
				result = userSetting.getLoadingPointId();
				break;
			}
			idx++;
		}
		return result;
	}
	
	/**
	 * Indexed property setter for SimpleVehicleLoadSettingsDTO.setLoadingPointId().
	 */
	public void setLoadingPointIdIndexed(int index, Long value) {
		int idx = 0;
		for (SimpleVehicleLoadSettingsDTO userSetting : userSettings) {
			if (idx == index) {
				userSetting.setLoadingPointId(value);
				break;
			}
			idx++;
		}
	}
}
