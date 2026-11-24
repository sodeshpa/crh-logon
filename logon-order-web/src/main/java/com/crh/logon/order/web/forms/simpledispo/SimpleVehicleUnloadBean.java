/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.simpledispo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import com.crh.logon.persistence.entities.views.SimpvehOrdItemMatEntity;

/**
 * Holds the Simple Vehicle Unload popup related information.
 * 
 * @author AdNovum Informatik AG
 */
public class SimpleVehicleUnloadBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/** These holds the data to show on the popup. */
	private Collection<SimpvehOrdItemMatEntity> items;

	/** The collection of objects to store the user entered data. */
	private Collection<SimpleVehicleUnloadSettingsDTO> userSettings;	

	/** The ID of the order to unload */
	private Long selectedOrderId;

	private Boolean initialReleaseToSAPFlag;

	public SimpleVehicleUnloadBean(Long selectedOrderId, 
			Collection<SimpvehOrdItemMatEntity> items, 
			Boolean releaseToSAPFlag) {

		this.items = items;
		this.selectedOrderId = selectedOrderId;
		this.initialReleaseToSAPFlag = releaseToSAPFlag;

		userSettings = initUserSettings(items);
	}
	
	/**
	 * Initiates the delivered quantity with the loaded quantity for every order item.
	 */
	private static Collection<SimpleVehicleUnloadSettingsDTO> initUserSettings(
			Collection<SimpvehOrdItemMatEntity> items) {

		final Collection<SimpleVehicleUnloadSettingsDTO> userSettings = new ArrayList<SimpleVehicleUnloadSettingsDTO>();

		for (SimpvehOrdItemMatEntity item : items) {
			SimpleVehicleUnloadSettingsDTO userSetting = new SimpleVehicleUnloadSettingsDTO(
					item.getOrderItemId(), item.getLoadedQuantity());

			userSettings.add(userSetting);
		}

		return userSettings;

	}

	public Collection<SimpvehOrdItemMatEntity> getItems() {
		return items;
	}

	public void setItems(Collection<SimpvehOrdItemMatEntity> items) {
		this.items = items;
	}

	public Long getSelectedOrderId() {
		return selectedOrderId;
	}

	public void setSelectedOrderId(Long selectedOrderId) {
		this.selectedOrderId = selectedOrderId;
	}

	public Boolean getInitialReleaseToSAPFlag() {
		return initialReleaseToSAPFlag;
	}

	public void setInitialReleaseToSAPFlag(Boolean initialReleaseToSAPFlag) {
		this.initialReleaseToSAPFlag = initialReleaseToSAPFlag;
	}
	
	public Collection<SimpleVehicleUnloadSettingsDTO> getUserSettings() {
		return userSettings;
	}

	public void setUserSettings(Collection<SimpleVehicleUnloadSettingsDTO> userSettings) {
		this.userSettings = userSettings;
	}

	public BigDecimal getDeliveredQtyIndexed(int index) {
		BigDecimal result = new BigDecimal(-1);

		int idx = 0;
		for (SimpleVehicleUnloadSettingsDTO setting : userSettings) {

			if (idx == index) {
				result = setting.getDeliveredQuantity();
				break;
			}

			idx++;
		}

		return result;
	}

	public void setDeliveredQtyIndexed(int index, BigDecimal value) {
		int idx = 0;
		for (SimpleVehicleUnloadSettingsDTO setting : userSettings) {

			if (idx == index) {
				setting.setDeliveredQuantity(value);
				break;
			}

			idx++;
		}
	}
}
