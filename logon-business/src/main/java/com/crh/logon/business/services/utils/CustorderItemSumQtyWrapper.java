/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;

import com.crh.logon.persistence.entities.views.CustordItemSumQtyExtEntity;

/**
 * @author AdNovum Informatik AG
 */
public class CustorderItemSumQtyWrapper implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private CustordItemSumQtyExtEntity custorderItemSumQtyEntity;
	private String materialName;
	
	/**
	 * @return the custorderItemSumQtyEntity
	 */
	public CustordItemSumQtyExtEntity getCustorderItemSumQtyEntity() {
		return custorderItemSumQtyEntity;
	}
	/**
	 * @param custorderItemSumQtyEntity
	 * @param materialName
	 */
	public CustorderItemSumQtyWrapper(CustordItemSumQtyExtEntity custorderItemSumQtyEntity,
			String materialName) {
		super();
		this.custorderItemSumQtyEntity = custorderItemSumQtyEntity;
		this.materialName = materialName;
	}
	/**
	 * @param custorderItemSumQtyEntity the custorderItemSumQtyEntity to set
	 */
	public void setCustorderItemSumQtyEntity(CustordItemSumQtyExtEntity custorderItemSumQtyEntity) {
		this.custorderItemSumQtyEntity = custorderItemSumQtyEntity;
	}
	/**
	 * @return the materialName
	 */
	public String getMaterialName() {
		return materialName;
	}
	/**
	 * @param materialName the materialName to set
	 */
	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

}
