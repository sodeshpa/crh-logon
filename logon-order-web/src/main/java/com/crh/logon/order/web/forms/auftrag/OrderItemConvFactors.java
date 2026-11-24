/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.auftrag;

import java.util.Map;

import com.crh.logon.business.services.beans.ConversionFactor;

/**
 * Conversion factors used in Order Details / Material tab, per each material.
 * 
 * @author AdNovum Informatik AG
 */
public class OrderItemConvFactors {
	
	private ConversionFactor fromNetQtyToLoadedUom;
	private ConversionFactor fromNetQtyToTons;
	private Map<String, ConversionFactor> fromLoadedUomToDelivUom;
	
	
	public ConversionFactor getFromNetQtyToLoadedUom() {
		return fromNetQtyToLoadedUom;
	}
	
	public void setFromNetQtyToLoadedUom(ConversionFactor fromNetQtyToLoadedUom) {
		this.fromNetQtyToLoadedUom = fromNetQtyToLoadedUom;
	}
	
	public ConversionFactor getFromNetQtyToTons() {
		return fromNetQtyToTons;
	}
	
	public void setFromNetQtyToTons(ConversionFactor fromNetQtyToTons) {
		this.fromNetQtyToTons = fromNetQtyToTons;
	}
	
	public Map<String, ConversionFactor> getFromLoadedUomToDelivUom() {
		return fromLoadedUomToDelivUom;
	}
	
	public void setFromLoadedUomToDelivUom(Map<String, ConversionFactor> fromLoadedUomToDelivUom) {
		this.fromLoadedUomToDelivUom = fromLoadedUomToDelivUom;
	}
	
}
