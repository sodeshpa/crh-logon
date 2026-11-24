package com.crh.logon.business.services.pdftools.store;

import java.util.Comparator;

import com.crh.logon.persistence.entities.refdata.SapMaterialNormBsEntity;
import com.crh.logon.persistence.entities.sap.SapMatPlantNormEntity;

public class MaterialPlantNormComparator implements
		Comparator<SapMatPlantNormEntity> {
	/**
	 * Compares SapMatPlantNormEntity object by sequence number
	 */
	public int compare(SapMatPlantNormEntity oneMaterialPlantNorm, SapMatPlantNormEntity otherMaterialPlantNorm) {
		SapMaterialNormBsEntity oneMaterialNorm = oneMaterialPlantNorm.getSapMaterialNorm();
		Long oneSequenceNumber = oneMaterialNorm.getSequenceNo();
		SapMaterialNormBsEntity otherMaterialNorm = otherMaterialPlantNorm.getSapMaterialNorm();
		Long otherSequenceNumber = otherMaterialNorm.getSequenceNo();
		return otherSequenceNumber.compareTo(oneSequenceNumber);
	}
}
