/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class MaterialEnhancementsDetailBean implements Serializable {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(MaterialEnhancementsDetailBean.class);

	public static final String FORM_TYPE_NEW = "new";
	public static final String FORM_TYPE_EXISTING = "existing";

	private static final long serialVersionUID = 1L;

	private Locale locale;

	private String matPlantId;
	private String plantCd;
	private String plantName;
	private String sapMaterialId;
	private String sapMaterialDescTxt;
	private String sapMaterialNo;

	private String autoaddMaterialDescTxt;
	private String autoaddSapMaterialId;
	private String autoadd1PerNOfUom;
    private String autoaddNUomCd;

	private String brandSalesName;
	private String normDesc;
	private String cpdNumber;
	private String logoGeneric;
	private String bulkDensityKgpm3;
	private String variableProdCostMat;
	private String futureVariableProdCostMat;
	private String futureValidFromDat;
	private String currency;
	private Map currencyBsEntityMap = new HashMap();
	private Boolean prioOverSapMatNormFlag;
	private String chromatReductionMonths;
	private String logo1;
	private String logo1Z1;
	private String logo1Z2;
	private String nationalNorm;
	private String logo2;
	private String logo2Z1;
	private String logo2Z2;
	private String logo2Z3;
	private String logo3;
	private String logo3Z1;
	private String logo3Z2;
	private String logo3Z3;
	private String logo4;
	private String logo4Z1;
	private String logo4Z2;
	private String logo4Z3;
	private String logo5;
	private String logo5Z1;
	private String logo5Z2;
	private String logo5Z3;
	private String logo6;
	private String logo6Z1;
	private String logo6Z2;
	private String logo6Z3;
	
	private Map<String, String> chromatReductionTexts;

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getMatPlantId() {
		return matPlantId;
	}

	public void setMatPlantId(String matPlantId) {
		this.matPlantId = matPlantId;
	}

	public String getPlantCd() {
		return plantCd;
	}

	public void setPlantCd(String plantCd) {
		this.plantCd = plantCd;
	}

	public String getPlantName() {
		return plantName;
	}

	public void setPlantName(String plantName) {
		this.plantName = plantName;
	}

	public String getSapMaterialId() {
		return sapMaterialId;
	}

	public void setSapMaterialId(String sapMaterialId) {
		this.sapMaterialId = sapMaterialId;
	}

	public String getSapMaterialDescTxt() {
		return sapMaterialDescTxt;
	}

	public void setSapMaterialDescTxt(String sapMaterialDescTxt) {
		this.sapMaterialDescTxt = sapMaterialDescTxt;
	}

	public String getSapMaterialNo() {
		return sapMaterialNo;
	}

	public void setSapMaterialNo(String sapMaterialNo) {
		this.sapMaterialNo = sapMaterialNo;
	}

	public String getBrandSalesName() {
		return brandSalesName;
	}

	public void setBrandSalesName(String brandSalesName) {
		this.brandSalesName = brandSalesName;
	}

	public String getNormDesc() {
		return normDesc;
	}

	public void setNormDesc(String normDesc) {
		this.normDesc = normDesc;
	}

	public String getCpdNumber() {
		return cpdNumber;
	}

	public void setCpdNumber(String cpdNumber) {
		this.cpdNumber = cpdNumber;
	}

	public String getLogoGeneric() {
		return logoGeneric;
	}

	public void setLogoGeneric(String logoGeneric) {
		this.logoGeneric = logoGeneric;
	}

	public String getBulkDensityKgpm3() {
		return bulkDensityKgpm3;
	}

	public void setBulkDensityKgpm3(String bulkDensityKgpm3) {
		this.bulkDensityKgpm3 = bulkDensityKgpm3;
	}

	public String getVariableProdCostMat() {
		return variableProdCostMat;
	}

	public void setVariableProdCostMat(String variableProdCostMat) {
		this.variableProdCostMat = variableProdCostMat;
	}

	public String getFutureVariableProdCostMat() {
		return futureVariableProdCostMat;
	}

	public void setFutureVariableProdCostMat(String futureVariableProdCostMat) {
		this.futureVariableProdCostMat = futureVariableProdCostMat;
	}

	public String getFutureValidFromDat() {
		return futureValidFromDat;
	}

	public void setFutureValidFromDat(String futureValidFromDat) {
		this.futureValidFromDat = futureValidFromDat;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Map getCurrencyBsEntityMap() {
		return currencyBsEntityMap;
	}

	public void setCurrencyBsEntityMap(Map currencyBsEntityMap) {
		this.currencyBsEntityMap = currencyBsEntityMap;
	}

	public String getChromatReductionMonths() {
		return chromatReductionMonths;
	}

	public void setChromatReductionMonths(String chromatReductionMonths) {
		this.chromatReductionMonths = chromatReductionMonths;
	}

	public Boolean getPrioOverSapMatNormFlag() {
		return prioOverSapMatNormFlag;
	}

	public void setPrioOverSapMatNormFlag(Boolean prioOverSapMatNormFlag) {
		this.prioOverSapMatNormFlag = prioOverSapMatNormFlag;
	}

	public String getLogo1() {
		return logo1;
	}

	public void setLogo1(String logo1) {
		this.logo1 = logo1;
	}

	public String getLogo1Z1() {
		return logo1Z1;
	}

	public void setLogo1Z1(String logo1z1) {
		logo1Z1 = logo1z1;
	}

	public String getLogo1Z2() {
		return logo1Z2;
	}

	public void setLogo1Z2(String logo1z2) {
		logo1Z2 = logo1z2;
	}

	public String getNationalNorm() {
		return nationalNorm;
	}

	public void setNationalNorm(String nationalNorm) {
		this.nationalNorm = nationalNorm;
	}

	public String getLogo2() {
		return logo2;
	}

	public void setLogo2(String logo2) {
		this.logo2 = logo2;
	}

	public String getLogo2Z1() {
		return logo2Z1;
	}

	public void setLogo2Z1(String logo2z1) {
		logo2Z1 = logo2z1;
	}

	public String getLogo2Z2() {
		return logo2Z2;
	}

	public void setLogo2Z2(String logo2z2) {
		logo2Z2 = logo2z2;
	}

	public String getLogo2Z3() {
		return logo2Z3;
	}

	public void setLogo2Z3(String logo2z3) {
		logo2Z3 = logo2z3;
	}

	public String getLogo3() {
		return logo3;
	}

	public void setLogo3(String logo3) {
		this.logo3 = logo3;
	}

	public String getLogo3Z1() {
		return logo3Z1;
	}

	public void setLogo3Z1(String logo3z1) {
		logo3Z1 = logo3z1;
	}

	public String getLogo3Z2() {
		return logo3Z2;
	}

	public void setLogo3Z2(String logo3z2) {
		logo3Z2 = logo3z2;
	}

	public String getLogo3Z3() {
		return logo3Z3;
	}

	public void setLogo3Z3(String logo3z3) {
		logo3Z3 = logo3z3;
	}

	public String getLogo4() {
		return logo4;
	}

	public void setLogo4(String logo4) {
		this.logo4 = logo4;
	}

	public String getLogo4Z1() {
		return logo4Z1;
	}

	public void setLogo4Z1(String logo4z1) {
		logo4Z1 = logo4z1;
	}

	public String getLogo4Z2() {
		return logo4Z2;
	}

	public void setLogo4Z2(String logo4z2) {
		logo4Z2 = logo4z2;
	}

	public String getLogo4Z3() {
		return logo4Z3;
	}

	public void setLogo4Z3(String logo4z3) {
		logo4Z3 = logo4z3;
	}

	public String getLogo5() {
		return logo5;
	}

	public void setLogo5(String logo5) {
		this.logo5 = logo5;
	}

	public String getLogo5Z1() {
		return logo5Z1;
	}

	public void setLogo5Z1(String logo5z1) {
		logo5Z1 = logo5z1;
	}

	public String getLogo5Z2() {
		return logo5Z2;
	}

	public void setLogo5Z2(String logo5z2) {
		logo5Z2 = logo5z2;
	}

	public String getLogo5Z3() {
		return logo5Z3;
	}

	public void setLogo5Z3(String logo5z3) {
		logo5Z3 = logo5z3;
	}

	public String getLogo6() {
		return logo6;
	}

	public void setLogo6(String logo6) {
		this.logo6 = logo6;
	}

	public String getLogo6Z1() {
		return logo6Z1;
	}

	public void setLogo6Z1(String logo6z1) {
		logo6Z1 = logo6z1;
	}

	public String getLogo6Z2() {
		return logo6Z2;
	}

	public void setLogo6Z2(String logo6z2) {
		logo6Z2 = logo6z2;
	}

	public String getLogo6Z3() {
		return logo6Z3;
	}

	public void setLogo6Z3(String logo6z3) {
		logo6Z3 = logo6z3;
	}

	public String getAutoaddMaterialDescTxt() {
		return autoaddMaterialDescTxt;
	}

	public void setAutoaddMaterialDescTxt(String autoaddMaterialDescTxt) {
		this.autoaddMaterialDescTxt = autoaddMaterialDescTxt;
	}

	public String getAutoaddSapMaterialId() {
		return autoaddSapMaterialId;
	}

	public void setAutoaddSapMaterialId(String autoaddSapMaterialId) {
		this.autoaddSapMaterialId = autoaddSapMaterialId;
	}

	public String getAutoadd1PerNOfUom() {
		return autoadd1PerNOfUom;
	}

	public void setAutoadd1PerNOfUom(String autoadd1PerNOfUom) {
		this.autoadd1PerNOfUom = autoadd1PerNOfUom;
	}

	public String getAutoaddNUomCd() {
		return autoaddNUomCd;
	}

	public void setAutoaddNUomCd(String autoaddNUomCd) {
		this.autoaddNUomCd = autoaddNUomCd;
	}
	
	public void setChromatReductionTexts(Map<String, String> value) {
		this.chromatReductionTexts = value;
	}
	
	public Map<String, String> getChromatReductionTexts() {
		return this.chromatReductionTexts;
	}

	public String getChromatReductionText(String key) {
		return this.chromatReductionTexts.get(key);
	}
	
	public void setChromatReductionText(String key, String value) {
		this.chromatReductionTexts.put(key, value);
	}
}
