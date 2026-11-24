/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans;

import java.io.Serializable;
import java.util.Collection;

import com.crh.logon.persistence.entities.views.SimpvehLoadOrderItemEntity;
import com.crh.logon.persistence.entities.views.SimpvehLoadingpointEntity;


/**
 * This class represents one item on the Simple Vehicle scheduling screen's loading popup.
 * It contains an order item's data and a list of loading points at which the item can be loaded.
 * @author AdNovum Informatik AG
 */
public class SimpleVehicleLoadableItem implements Serializable
{
	private static final long serialVersionUID = -8655681026544654793L;
	
	final private SimpvehLoadOrderItemEntity loadOrderItem;
	final private Collection<SimpvehLoadingpointEntity> loadingPoints;
	
	/**
	 * Creates a new SimpleVehicleLoadableItem
	 * @param loadOrderItem
	 * @param loadingPoints
	 */
	public SimpleVehicleLoadableItem(SimpvehLoadOrderItemEntity loadOrderItem,
			Collection<SimpvehLoadingpointEntity> loadingPoints) {
		super();
		this.loadOrderItem = loadOrderItem;
		this.loadingPoints = loadingPoints;
	}

	public SimpvehLoadOrderItemEntity getLoadOrderItem() {
		return loadOrderItem;
	}

	public Collection<SimpvehLoadingpointEntity> getLoadingPoints() {
		return loadingPoints;
	}
}
