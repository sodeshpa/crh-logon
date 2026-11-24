/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.constants;

/**
 * Groups various error codes in shipto-salesarea, used in Dispokarte, Order Taking, Webbestellung, ...
 * 
 * This enum was originally part of ShiptoSalesAreaUtil when it was located in com.crh.logon.web.utils,
 * but with the refactoring performed in July 2010 this had to be moved into a globally available constant class.
 * 
 * See ORDER1158, BUSINESS1701: Refactoring for the Sap Material Condition handling
 * See BUSINESS1699, ORDER1157, TERMINAL040, CUST133: References for ShiptoSalesAraUtil are no longer pointing to the common-web but business
 *  
 * @author AdNovum Informatik AG
 */
public enum ShiptoSalesAreaError {

	UOM(CustorderError.UOM),
	PRODUCTION_PLANT (CustorderError.PRODUCTION_PLANT),
	SUPPLY_PLANT(CustorderError.SUPPLY_PLANT),
	SHIPPINGPOINT(CustorderError.SHIPPINGPOINT),
	LOADINGPOINT(CustorderError.LOADINGPOINT),
	POOL(CustorderError.POOL),
	CARRIER(CustorderError.CARRIER),
	SILO(CustorderError.SILO), 
	BILLTO(CustorderError.BILLTO), 
	SALESGROUP(CustorderError.SALESGROUP),
	SAREA(CustorderError.SAREA), 
	SAP_BP_SAREA(CustorderError.SAP_BP_SAREA),
	SAP_BP_SOFFICE(CustorderError.SAP_BP_SOFFICE),
	SAREA_SORG(CustorderError.SAREA_SORG),
	SHIPTO_BILLTO(CustorderError.SHIPTO_BILLTO),
	DELIVERY_TYPE(CustorderError.DELIVERY_TYPE),
	TRANSPORT_CONDITION(CustorderError.TRANSPORT_CONDITION),
	CUSTOMER_CREDIT_BLOCK(CustorderError.CUSTOMER_CREDIT_BLOCK),
	CONTRACT_INVALID(CustorderError.CONTRACT_INVALID),
	MATERIAL_NOT_IN_SAREA(CustorderError.MATERIAL_NOT_IN_SAREA),
	SHIPTO_BILLTO_IN_SAREA(CustorderError.SHIPTO_BILLTO_IN_SAREA),
	PAYER(CustorderError.PAYER),
	AUTOPAYER(CustorderError.AUTOPAYER), 
	AVAILABLE_IN_PICKUP_APP_FLAG(CustorderError.AVAILABLE_IN_PICKUP_APP_FLAG),
	
	BLOCK_ALL_SALES_AREA(CustorderError.BLOCK_ALL_AREA),
	BLOCK_SOME_SALES_AREA(CustorderError.BLOCK_SOME_AREA),
	
	BLOCK_BP(CustorderError.BLOCKED_BP);
	
	final CustorderError correspondingCustorderError;
	
	public CustorderError getCorrespondingCustorderError() {
		return correspondingCustorderError;
	}
	
	private ShiptoSalesAreaError(CustorderError error) {
		correspondingCustorderError=error;
	}
	
	@Override
	public String toString() {
		return String.format(
				"ShiptoSalesArea error %s(%s)",
				name(), 
				getCorrespondingCustorderError().getPlainMessage());
	}
	
}
