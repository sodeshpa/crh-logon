/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans.pdf.deliverynote;

import java.math.BigDecimal;

import com.crh.logon.business.services.utils.PdfUtil;
import org.apache.commons.lang.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
public class BulkItemData extends BaseOrderItemData {
	private static final long serialVersionUID = 3272979880121009495L;

	private String netWeight;
	private String tareWeight;
	private String grossWeight;
	private String returnWeight;
	
	private String sapMaterialNumber;
	private String productionPlant;
	private String mixDesign;
	
	public boolean renderReturnWeight() {
		return StringUtils.isNotEmpty(returnWeight);
	}
	
	public void setTareWeight(BigDecimal quantity, String uom) {
		this.tareWeight = PdfUtil.getValueOrEmptyStringWeight(quantity, uom);
	}
	public void setNetWeight(BigDecimal quantity, String uom) {
		this.netWeight = PdfUtil.getValueOrEmptyStringWeight(quantity, uom);
	}
	public void setGrossWeight(BigDecimal quantity, String uom) {
		this.grossWeight = PdfUtil.getValueOrEmptyStringWeight(quantity, uom);
	}
	public void setReturnWeight(BigDecimal quantity, String uom) {
		this.returnWeight = PdfUtil.getValueOrEmptyStringWeight(quantity, uom);
	}
	
	public String getNetWeight() {
		return netWeight;
	}

	public void setNetWeight(String netWeight) {
		this.netWeight = netWeight;
	}

	public String getTareWeight() {
		return tareWeight;
	}

	public void setTareWeight(String tareWeight) {
		this.tareWeight = tareWeight;
	}

	public String getGrossWeight() {
		return grossWeight;
	}

	public void setGrossWeight(String grossWeight) {
		this.grossWeight = grossWeight;
	}

	public String getReturnWeight() {
		return returnWeight;
	}

	public void setReturnWeight(String returnWeight) {
		this.returnWeight = returnWeight;
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
}
