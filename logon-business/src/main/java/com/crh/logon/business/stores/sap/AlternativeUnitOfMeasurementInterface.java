package com.crh.logon.business.stores.sap;

import java.util.List;

import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericReadOnlyInterface;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialAltUomEntity;

@Local
public interface AlternativeUnitOfMeasurementInterface extends GenericReadOnlyInterface<UomTxEntity>{
	

	/**
	 * Returns all the {@link SapMaterialAltUomEntity} entries symbolizing alternative unit of measurements for the materials provided.
	 * 
	 * @param sapMaterials
	 * 
	 * @return List<SapMaterialAltUomEntity>
	 */
	public List<SapMaterialAltUomEntity> fetchForMaterials(List<Long> sapMaterials);

	/**
	 * Returns all the {@link SapMaterialAltUomEntity} entries symbolizing alternative unit of measurements for the materials provided, but for WebOrder only.
	 * @param sapMaterials
	 * 
	 * @return List<SapMaterialAltUomEntity>
	 */
	public List<SapMaterialAltUomEntity> fetchForMaterialsWithWebOrderOnly(List<Long> sapMaterials);
	
	/**
	 * Returns the {@link SapMaterialAltUomEntity} entry for the given material and unit of measurement code pair
	 */
	public SapMaterialAltUomEntity fetchByMaterialAndUnitOfMeasureCode(Long materialId, String unitOfMeasureCode);

}
