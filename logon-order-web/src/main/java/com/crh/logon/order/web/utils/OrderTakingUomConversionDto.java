/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.utils;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 * @author AdNovum Informatik AG
 */
public class OrderTakingUomConversionDto {
	
	private BigDecimal inQuantity;
	private String inFromUomCd;
	private String inToUomCd;
	private Long inSapMaterialId;
	private Long inVehicleTypeCd;
	private Long inDeliveryType;
    private boolean inAllowFuzzy;
    
    private BigDecimal outQuantity;
    private String outUom;

    
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	public BigDecimal getInQuantity() {
		return inQuantity;
	}
	public void setInQuantity(BigDecimal inQuantity) {
		this.inQuantity = inQuantity;
	}
	public String getInFromUomCd() {
		return inFromUomCd;
	}
	public void setInFromUomCd(String inFromUomCd) {
		this.inFromUomCd = inFromUomCd;
	}
	public String getInToUomCd() {
		return inToUomCd;
	}
	public void setInToUomCd(String inToUomCd) {
		this.inToUomCd = inToUomCd;
	}
	public Long getInSapMaterialId() {
		return inSapMaterialId;
	}
	public void setInSapMaterialId(Long inSapMaterialId) {
		this.inSapMaterialId = inSapMaterialId;
	}
	public Long getInVehicleTypeCd() {
		return inVehicleTypeCd;
	}
	public void setInVehicleTypeCd(Long inVehicleTypeCd) {
		this.inVehicleTypeCd = inVehicleTypeCd;
	}
	public Long getInDeliveryType() {
		return inDeliveryType;
	}
	public void setInDeliveryType(Long inDeliveryType) {
		this.inDeliveryType = inDeliveryType;
	}
	public boolean isInAllowFuzzy() {
		return inAllowFuzzy;
	}
	public void setInAllowFuzzy(boolean inAllowFuzzy) {
		this.inAllowFuzzy = inAllowFuzzy;
	}
	public BigDecimal getOutQuantity() {
		return outQuantity;
	}
	public void setOutQuantity(BigDecimal outQuantity) {
		this.outQuantity = outQuantity;
	}
	public String getOutUom() {
		return outUom;
	}
	public void setOutUom(String outUom) {
		this.outUom = outUom;
	}
}
