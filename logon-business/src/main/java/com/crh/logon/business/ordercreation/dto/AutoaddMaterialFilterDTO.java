/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ordercreation.dto;

import java.io.Serializable;


/**
 * Class holding the basic parameters for identifying if adding an autoadded material is necessary for 
 * a material + production plant + uom cd triple.
 * 
 * @author AdNovum Informatik AG
 */
public class AutoaddMaterialFilterDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private final String plantCd;
	private final Long sapMaterialId;
	private final String uomCd;
	

	public AutoaddMaterialFilterDTO(String plantCd, Long sapMaterialId, String uomCd) {
		super();
		this.plantCd = plantCd;
		this.sapMaterialId = sapMaterialId;
		this.uomCd = uomCd;
	}
	
	public String getUomCd() {
		return uomCd;
	}
	
	public String getPlantCd() {
		return plantCd;
	}
	
	public Long getSapMaterialId() {
		return sapMaterialId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((plantCd == null) ? 0 : plantCd.hashCode());
		result = prime * result
				+ ((sapMaterialId == null) ? 0 : sapMaterialId.hashCode());
		result = prime * result + ((uomCd == null) ? 0 : uomCd.hashCode());
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
		AutoaddMaterialFilterDTO other = (AutoaddMaterialFilterDTO) obj;
		if (plantCd == null) {
			if (other.plantCd != null)
				return false;
		} else if (!plantCd.equals(other.plantCd))
			return false;
		if (sapMaterialId == null) {
			if (other.sapMaterialId != null)
				return false;
		} else if (!sapMaterialId.equals(other.sapMaterialId))
			return false;
		if (uomCd == null) {
			if (other.uomCd != null)
				return false;
		} else if (!uomCd.equals(other.uomCd))
			return false;
		return true;
	}
}
