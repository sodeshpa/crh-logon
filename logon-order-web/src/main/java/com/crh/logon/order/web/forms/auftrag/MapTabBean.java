/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.auftrag;

import java.io.Serializable;
import java.util.Date;

/**
 * @author AdNovum Informatik AG
 */
public class MapTabBean implements Serializable{

	private String shpointCoordLat;
	private String shpointCoordLon;
	private String shpointName;
	private String shiptoCoordLat;
	private String shiptoCoordLon;
	private String shiptoName;
	private String plantCoordLat;
	private String plantCoordLon;
	private String plantName;
	private Date loadingTimestamp;
	private Date unloadingTimestamp;
	
	public Date getLoadingTimestamp() {
		return loadingTimestamp;
	}
	public void setLoadingTimestamp(Date loadingTimestamp) {
		this.loadingTimestamp = loadingTimestamp;
	}
	public Date getUnloadingTimestamp() {
		return unloadingTimestamp;
	}
	public void setUnloadingTimestamp(Date unloadingTimestamp) {
		this.unloadingTimestamp = unloadingTimestamp;
	}
	public String getShpointCoordLat() {
		return shpointCoordLat;
	}
	public void setShpointCoordLat(String shpointCoordLat) {
		this.shpointCoordLat = shpointCoordLat;
	}
	public String getShpointCoordLon() {
		return shpointCoordLon;
	}
	public void setShpointCoordLon(String shpointCoordLon) {
		this.shpointCoordLon = shpointCoordLon;
	}
	public String getShpointName() {
		return shpointName;
	}
	public void setShpointName(String shpointName) {
		this.shpointName = shpointName;
	}
	public String getShiptoCoordLat() {
		return shiptoCoordLat;
	}
	public void setShiptoCoordLat(String shiptoCoordLat) {
		this.shiptoCoordLat = shiptoCoordLat;
	}
	public String getShiptoCoordLon() {
		return shiptoCoordLon;
	}
	public void setShiptoCoordLon(String shiptoCoordLon) {
		this.shiptoCoordLon = shiptoCoordLon;
	}
	public String getShiptoName() {
		return shiptoName;
	}
	public void setShiptoName(String shiptoName) {
		this.shiptoName = shiptoName;
	}
	public String getPlantCoordLat() {
		return plantCoordLat;
	}
	public void setPlantCoordLat(String plantCoordLat) {
		this.plantCoordLat = plantCoordLat;
	}
	public String getPlantCoordLon() {
		return plantCoordLon;
	}
	public void setPlantCoordLon(String plantCoordLon) {
		this.plantCoordLon = plantCoordLon;
	}
	public String getPlantName() {
		return plantName;
	}
	public void setPlantName(String plantName) {
		this.plantName = plantName;
	}
	private static final long serialVersionUID = 1L;

	
}

