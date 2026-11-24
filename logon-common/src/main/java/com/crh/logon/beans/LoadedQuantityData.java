/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.beans;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author AdNovum Informatik AG
 */
public class LoadedQuantityData implements Serializable, HasLoadedQuantity {
	
	private BigDecimal loadedQuantity;
	private String loadedUom;
	
	public LoadedQuantityData(BigDecimal loadedQuantity, String loadedUom) {
		this.loadedQuantity = loadedQuantity;
		this.loadedUom = loadedUom;
	}
	
	public BigDecimal getLoadedQuantity() {
		return loadedQuantity;
	}
	
	public String getLoadedUom() {
		return loadedUom;
	}
}
