/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.masterdata;

import java.io.Serializable;
import java.util.List;

import com.crh.logon.persistence.entities.common.LdpointMatRestrEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.SapMaterialPlantDescEntity;

/**
 * @author AdNovum Informatik AG
 */
public class LoadingPointBean implements Serializable{


	private static final long serialVersionUID = 1L;
	private List materialList;
	private LoadingpointEntity lpEntity;
	private String grossWeightLimitKg;
	private String loadingTime;
	private Long packageTypeCd;
	private Long weighSystemLpCd;
	private Long delivNotePrintTypeCd;
	private String delivNoteCopiesLogon;
	private Long printerCd;
	private boolean loadingTimeFlag;
	private String leaveLpDelayMin;

	public boolean getMaterialRestriction(int index){
		SapMaterialPlantDescEntity material = (SapMaterialPlantDescEntity) materialList.get(index);
		
		for (int j = 0; j < lpEntity.getMatRestrictions().size(); j++) {
			LdpointMatRestrEntity lpMaterial = (LdpointMatRestrEntity) lpEntity.getMatRestrictions().get(j);
			if (material.getSapMaterialId().longValue() == lpMaterial.getSapMaterial().getSapMaterialId().longValue() ) {
				return true;
			}
		}
		return false;
	}
	
	public void setMaterialRestriction(int index, boolean value){
		SapMaterialPlantDescEntity material = (SapMaterialPlantDescEntity) materialList.get(index);
		
		if (!isSapMaterial(material.getSapMaterialId())){
			LdpointMatRestrEntity matRest = new LdpointMatRestrEntity();
			
			//dummy SapMaterialEntity
			SapMaterialEntity mat = new SapMaterialEntity();
			mat.setSapMaterialId((Long)material.getSapMaterialId().longValue());
			matRest.setSapMaterial(mat);
			matRest.setLoadingpoint(lpEntity);
			lpEntity.getMatRestrictions().add(matRest);
		}
	}
	
	private boolean isSapMaterial(Long sapMaterialId) {
		for (int i = 0; i <  lpEntity.getMatRestrictions().size(); i++) {
			LdpointMatRestrEntity ldMaterial = (LdpointMatRestrEntity) lpEntity.getMatRestrictions().get(i);
			if (sapMaterialId.longValue() == ldMaterial.getSapMaterial().getSapMaterialId().longValue()){
				return true;
			}
		}
		return false;
	}
	
	public LoadingpointEntity getLpEntity() {
		return lpEntity;
	}

	public void setLpEntity(LoadingpointEntity lpEntity) {
		this.lpEntity = lpEntity;
	}
	
	public List getMaterialList() {
		return materialList;
	}

	public void setMaterialList(List materialList) {
		this.materialList = materialList;
	}
	
	public String getGrossWeightLimitKg() {
		return grossWeightLimitKg;
	}

	public void setGrossWeightLimitKg(String grossWeightLimitKg) {
		this.grossWeightLimitKg = grossWeightLimitKg;
	}
	
	public String getLoadingTime() {
		return loadingTime;
	}

	public void setLoadingTime(String loadingTime) {
		this.loadingTime = loadingTime;
	}

	public boolean isLoadingTimeFlag() {
		return loadingTimeFlag;
	}

	public void setLoadingTimeFlag(boolean loadingTimeFlag) {
		this.loadingTimeFlag = loadingTimeFlag;
	}

	public Long getPackageTypeCd() {
		return packageTypeCd;
	}

	public void setPackageTypeCd(Long packageTypeCd) {
		this.packageTypeCd = packageTypeCd;
	}

	public Long getWeighSystemLpCd() {
		return weighSystemLpCd;
	}

	public void setWeighSystemLpCd(Long weighSystemLpCd) {
		this.weighSystemLpCd = weighSystemLpCd;
	}

	public Long getDelivNotePrintTypeCd() {
		return delivNotePrintTypeCd;
	}

	public void setDelivNotePrintTypeCd(Long delivNotePrintTypeCd) {
		this.delivNotePrintTypeCd = delivNotePrintTypeCd;
	}
	
	public String getDelivNoteCopiesLogon() {
		return delivNoteCopiesLogon;
	}

	public void setDelivNoteCopiesLogon(String delivNoteCopiesLogon) {
		this.delivNoteCopiesLogon = delivNoteCopiesLogon;
	}

	public Long getPrinterCd() {
		return printerCd;
	}

	public void setPrinterCd(Long printerCd) {
		this.printerCd = printerCd;
	}

	public String getLeaveLpDelayMin() {
		return leaveLpDelayMin;
	}

	public void setLeaveLpDelayMin(String leaveLpDelayMin) {
		this.leaveLpDelayMin = leaveLpDelayMin;
	}
}
