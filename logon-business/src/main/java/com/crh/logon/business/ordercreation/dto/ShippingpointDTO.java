package com.crh.logon.business.ordercreation.dto;

import java.io.Serializable;



public class ShippingpointDTO implements Serializable{

	
	private static final long serialVersionUID = 1L;
	private Long shippingpointId;
    private String plantCd;
    private String plantName;
    private String shippingpointName;
    private Long shipppointTypeCd;
    private Long railwayStationId;
    private Double coordLat;
    private Double coordLon;
	
	public ShippingpointDTO(Long shippingpointId, String plantCd, String plantName, String shippingpointName, Long shipppointTypeCd,
			Long railwayStationId, Double coordLat, Double coordLon) {
		super();
		this.shippingpointId = shippingpointId;
		this.plantCd = plantCd;
		this.plantName = plantName;
		this.shippingpointName = shippingpointName;
		this.shipppointTypeCd = shipppointTypeCd;
		this.railwayStationId = railwayStationId;
		this.coordLat = coordLat;
		this.coordLon = coordLon;
	}
	
	/**
	 * @return the plantName
	 */
	public String getPlantName() {
		return plantName;
	}

	/**
	 * @return the shipppointTypeCd
	 */
	public Long getShipppointTypeCd() {
		return shipppointTypeCd;
	}

	/**
	 * @return the shippingpointId
	 */
	public Long getShippingpointId() {
		return shippingpointId;
	}

	/**
	 * @return the plantCd
	 */
	public String getPlantCd() {
		return plantCd;
	}

	/**
	 * @return the shippingpointName
	 */
	public String getShippingpointName() {
		return shippingpointName;
	}

	/**
	 * @return the railwayStation
	 */
	public Long getRailwayStationId() {
		return railwayStationId;
	}

	/**
	 * @return the coordLat
	 */
	public Double getCoordLat() {
		return coordLat;
	}

	/**
	 * @return the coordLon
	 */
	public Double getCoordLon() {
		return coordLon;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ShippingpointDTO [shippingpointId=");
		builder.append(shippingpointId);
		builder.append(", shippingpointName=");
		builder.append(shippingpointName);
		builder.append(", plantCd=");
		builder.append(plantCd);
		builder.append(", shipppointTypeCd=");
		builder.append(shipppointTypeCd);
		builder.append(", railwayStationId=");
		builder.append(railwayStationId);
		builder.append("]");
		return builder.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((shippingpointId == null) ? 0 : shippingpointId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ShippingpointDTO other = (ShippingpointDTO) obj;
		if (shippingpointId == null) {
			if (other.shippingpointId != null)
				return false;
		}
		else if (!shippingpointId.equals(other.shippingpointId))
			return false;
		return true;
	}

}
