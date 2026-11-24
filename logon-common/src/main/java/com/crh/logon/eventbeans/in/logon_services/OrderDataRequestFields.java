/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.eventbeans.in.logon_services;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author AdNovum Informatik AG
 */
public interface OrderDataRequestFields {

//	public Date getEventTime();
//	public String getSenderInfo();
//	public String getEventName();
//	
//	public String getUserId();
//	public String getLanguage();
//	public String getTruckPlateNo();
//	public String getSupplyPlantNo();
//	public String getSupplyPlantAltNo();
//  public String getShippingpointNo();
//  public String getLoadingpointNo();
//  public String getLoadingpointName();
    
    public String getSalesArea();
	public String getSoldTo();
	public String getShipTo();
	public List<String> getMaterialRestrictions();
	public String getMaterialNo();
	public String getProdPlantNo();
	public String getProdPlantAltNo();
	public BigDecimal getQuantityKg();
	public Boolean isQuantityFullFlag();
	public String getDeliveryNoteTxt();
}