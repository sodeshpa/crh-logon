/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * @author AdNovum Informatik AG
 */
public class VehicleCoordinatesBean implements Serializable {
	private Long truckId;
	private Double coordLat;
	private Double coordLon;
	private Long bizeventTypeCd;
	private Long bizeventId;
	private Date eventTs;
	private Long orderId;
	private Long trailerId;
	private Long containerId;
	private Long wagonId;
	
	public Long getTruckId() {
		return truckId;
	}
	public void setTruckId(Long truckId) {
		this.truckId = truckId;
	}
	public Double getCoordLat() {
		return coordLat;
	}
	public void setCoordLat(Double coordLat) {
		this.coordLat = coordLat;
	}
	public Double getCoordLon() {
		return coordLon;
	}
	public void setCoordLon(Double coordLon) {
		this.coordLon = coordLon;
	}
	public Long getBizeventTypeCd() {
		return bizeventTypeCd;
	}
	public void setBizeventTypeCd(Long bizeventTypeCd) {
		this.bizeventTypeCd = bizeventTypeCd;
	}
	public Long getBizeventId() {
		return bizeventId;
	}
	public void setBizeventId(Long bizeventId) {
		this.bizeventId = bizeventId;
	}
	public Date getEventTs() {
		return eventTs;
	}
	public void setEventTs(Date eventTs) {
		this.eventTs = eventTs;
	}
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public Long getTrailerId() {
		return trailerId;
	}
	public void setTrailerId(Long trailerId) {
		this.trailerId = trailerId;
	}
	public Long getContainerId() {
		return containerId;
	}
	public void setContainerId(Long containerId) {
		this.containerId = containerId;
	}
	public Long getWagonId() {
		return wagonId;
	}
	public void setWagonId(Long wagonId) {
		this.wagonId = wagonId;
	}
	
}
