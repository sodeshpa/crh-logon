/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans.pdf.deliverynote;

import java.math.BigDecimal;

import com.crh.logon.business.services.utils.PdfUtil;

/**
 * @author AdNovum Informatik AG
 */
public class BagItemData extends BaseOrderItemData {
	private static final long serialVersionUID = -2883926859858406959L;
	
	private String sapMaterialNumber;
	private String productionPlant;
	private String mixDesign;
	
	private String orderedQuantity;
	private String deliveredQuantity;
	private String netQuantity;
	
	public void setDeliveredQuantity(BigDecimal quantity, String uom) {
		this.deliveredQuantity = PdfUtil.getValueOrEmptyStringWeight(quantity, uom);
	}
	
	public void setOrderedQuantity(BigDecimal quantity, String uom) {
		this.orderedQuantity = PdfUtil.getValueOrEmptyStringWeight(quantity, uom);
	}
	
	public void setNetQuantity(BigDecimal quantity, String uom) {
		this.netQuantity = PdfUtil.getValueOrEmptyStringWeight(quantity, uom);
	}
	
	public String getSapMaterialNumber() {
		return sapMaterialNumber;
	}
	public void setSapMaterialNumber(String sapMaterialNumber) {
		this.sapMaterialNumber = sapMaterialNumber;
	}
	public String getProductionPlant() {
		return productionPlant;
	}
	public void setProductionPlant(String productionPlant) {
		this.productionPlant = productionPlant;
	}
	public String getMixDesign() {
		return mixDesign;
	}
	public void setMixDesign(String mixDesign) {
		this.mixDesign = mixDesign;
	}
	public String getOrderedQuantity() {
		return orderedQuantity;
	}
	public void setOrderedQuantity(String orderedQuantity) {
		this.orderedQuantity = orderedQuantity;
	}
	public String getDeliveredQuantity() {
		return deliveredQuantity;
	}
	public void setDeliveredQuantity(String deliveredQuentity) {
		this.deliveredQuantity = deliveredQuentity;
	}
	public String getNetQuantity() {
		return netQuantity;
	}
	public void setNetQuantity(String netQuantity) {
		this.netQuantity = netQuantity;
	}
}
