/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans;

import java.io.Serializable;

public class FieldsForRankEvaluationDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long shiptoSareaId;

	private Long sapMaterialId;

	private String productionPlantCd;

	private Long shippingpointId;

	private Long loadingpointId;

	private String uomCd;

	private Long deliveryTypeCd;

	private Long sapContractItemId;

	public void setShiptoSareaId(Long shiptoSareaId) {
		this.shiptoSareaId = shiptoSareaId;
	}

	public void setSapMaterialId(Long sapMaterialId) {
		this.sapMaterialId = sapMaterialId;
	}

	public void setProductionPlantCd(String productionPlantCd) {
		this.productionPlantCd = productionPlantCd;
	}

	public void setShippingpointId(Long shippingpointId) {
		this.shippingpointId = shippingpointId;
	}

	public void setLoadingpointId(Long loadingpointId) {
		this.loadingpointId = loadingpointId;
	}

	public void setUomCd(String uomCd) {
		this.uomCd = uomCd;
	}

	public void setDeliveryTypeCd(Long deliveryTypeCd) {
		this.deliveryTypeCd = deliveryTypeCd;
	}

	public void setSapContractItemId(Long sapContractItemId) {
		this.sapContractItemId = sapContractItemId;
	}

	public Long getShiptoSareaId() {
		return shiptoSareaId;
	}

	public Long getSapMaterialId() {
		return sapMaterialId;
	}

	public String getProductionPlantCd() {
		return productionPlantCd;
	}

	public Long getShippingpointId() {
		return shippingpointId;
	}

	public Long getLoadingpointId() {
		return loadingpointId;
	}

	public String getUomCd() {
		return uomCd;
	}

	public Long getDeliveryTypeCd() {
		return deliveryTypeCd;
	}

	public Long getSapContractItemId() {
		return sapContractItemId;
	}
}
