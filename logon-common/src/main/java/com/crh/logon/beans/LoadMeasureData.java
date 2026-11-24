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
public class LoadMeasureData implements Serializable {
	
	private BigDecimal tareWeightT;
	private BigDecimal grossWeightT;
	
	public LoadMeasureData(BigDecimal tareWeightT, BigDecimal grossWeightT) {
		this.tareWeightT = tareWeightT;
		this.grossWeightT = grossWeightT;
	}
	
	public BigDecimal getTareWeightT() {
		return tareWeightT;
	}
	
	public BigDecimal getGrossWeightT() {
		return grossWeightT;
	}
}
