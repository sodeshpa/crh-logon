/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans;

/**
 * Common placeholder for returning the converted delivered quantities to be sent in
 * Sales/Transfer/Shipment documents exported to SAP. (Note: Shipment document has no
 * "deliveredQtyInTons" field.)
 * 
 * @author AdNovum Informatik AG
 */
public class DeliveredQtyInSalesUomAndTons {
	
	final String deliveredQtyInSalesUomStr;
	final String salesUomStr;
	final String deliveredQtyInTonsStr;
	final String weightUnitStr;
	
	/**
	 * @param deliveredQtyInSalesUomStr
	 * @param salesUomStr
	 * @param deliveredQtyInTonsStr
	 * @param weightUnitStr
	 */
	public DeliveredQtyInSalesUomAndTons(String deliveredQtyInSalesUomStr, String salesUomStr,
			String deliveredQtyInTonsStr, String weightUnitStr) {
		this.deliveredQtyInSalesUomStr = deliveredQtyInSalesUomStr;
		this.salesUomStr = salesUomStr;
		this.deliveredQtyInTonsStr = deliveredQtyInTonsStr;
		this.weightUnitStr = weightUnitStr;
	}
	
	public String getDeliveredQtyInSalesUomStr() {
		return deliveredQtyInSalesUomStr;
	}
	
	public String getSalesUomStr() {
		return salesUomStr;
	}
	
	public String getDeliveredQtyInTonsStr() {
		return deliveredQtyInTonsStr;
	}
	
	public String getWeightUnitStr() {
		return weightUnitStr;
	}
	
}
