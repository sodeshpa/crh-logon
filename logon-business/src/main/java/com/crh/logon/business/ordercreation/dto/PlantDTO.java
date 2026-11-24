package com.crh.logon.business.ordercreation.dto;

import java.io.Serializable;


public class PlantDTO implements Serializable {
	private static final long serialVersionUID = 1L;
    private String plantCd;
    private String plantName;
    private Boolean productionPlant;
    
    
	/**
	 * @param plantCd
	 * @param plantName
	 */
	public PlantDTO(String plantCd, String plantName, Boolean productionPlant) {
		super();
		this.plantCd = plantCd;
		this.plantName = plantName;
		this.productionPlant = productionPlant;
	}
	
	/**
	 * @return the productionPlant
	 */
	public Boolean getProductionPlant() {
		return productionPlant;
	}

	/**
	 * @return the plantCd
	 */
	public String getPlantCd() {
		return plantCd;
	}
	/**
	 * @return the plantName
	 */
	public String getPlantName() {
		return plantName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((plantCd == null) ? 0 : plantCd.hashCode());
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
		PlantDTO other = (PlantDTO) obj;
		if (plantCd == null) {
			if (other.plantCd != null)
				return false;
		}
		else if (!plantCd.equals(other.plantCd))
			return false;
		return true;
	}

}
