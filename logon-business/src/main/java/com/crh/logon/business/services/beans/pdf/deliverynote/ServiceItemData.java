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
public class ServiceItemData extends BaseOrderItemData {
	private static final long serialVersionUID = 8525215529195005644L;

	private String netWeight;

	public String getNetWeight() {
		return netWeight;
	}

	public void setNetWeight(String netWeight) {
		this.netWeight = netWeight;
	}
	
	public void setNetWeight(BigDecimal quantity, String uom) {
		this.netWeight = PdfUtil.getValueOrEmptyStringWeight(quantity, uom);
	}

}
