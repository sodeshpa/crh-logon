/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.beans;

import java.io.Serializable;
import java.util.List;


/**
 * @author AdNovum Informatik AG
 */
public class LogonOrderItem implements Serializable {

    private String orderItemID;
    private String lastEvent;
    private String loadAssignmentPartID;
    private String material;
    private String chamber;
    private String orderedQuantity;
    private String loadedQuantity;
    private boolean returnable;
    private List shipToSiloList; // list of LogonRfidObject

	public String getOrderItemID() {
		return orderItemID;
	}

	public void setOrderItemID(String orderItemID) {
		this.orderItemID = orderItemID;
	}

	public String getLoadAssignmentPartID() {
		return loadAssignmentPartID;
	}

	public void setLoadAssignmentPartID(String loadAssignmentPartID) {
		this.loadAssignmentPartID = loadAssignmentPartID;
	}

	public String getLastEvent() {
		return lastEvent;
	}
	
	public void setLastEvent(String lastEvent) {
		this.lastEvent = lastEvent;
	}
	
    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getChamber() {
		return chamber;
	}

	public void setChamber(String chamber) {
		this.chamber = chamber;
	}

	public String getOrderedQuantity() {
        return orderedQuantity;
    }

    public void setOrderedQuantity(String orderedQuantity) {
        this.orderedQuantity = orderedQuantity;
    }

    public String getLoadedQuantity() {
		return loadedQuantity;
	}

	public void setLoadedQuantity(String loadedQuantity) {
		this.loadedQuantity = loadedQuantity;
	}

	public boolean getReturnable() {
		return returnable;
	}

	public void setReturnable(boolean returnable) {
		this.returnable = returnable;
	}

	public List getShipToSiloList() {
        return shipToSiloList;
    }
	
	public void setShipToSiloList(List shipToSiloList) {
        this.shipToSiloList = shipToSiloList;
    }

}
