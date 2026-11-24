/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ordercreation.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.crh.logon.business.services.utils.TruckWagonTypeWrapperUtil;

/**
 * Abstract class to model the common properties and logic of vehicle types.
 * The descendant classes should implement the Volume m3 and Max gross weight
 * based on the concrete vehicle types.
 * 
 * TODO 13-Sep-2013/paksyd: This TruckWagonTypeWrapper should be renamed to VehicleTypeWrapper!
 * 
 * @author AdNovum Informatik AG
 */
public abstract class TruckWagonTypeWrapper implements Comparable<TruckWagonTypeWrapper>, Serializable {
	
	private static final long serialVersionUID = -8910104051347654556L;
	
	private long priority;
	private String typeName;
	private Long typeCd;

	abstract public BigDecimal getVolumeM3();
	abstract public BigDecimal getMaxGrossWeightT();
	
	public TruckWagonTypeWrapper(long priority, String typeName, Long typeCd) {
		this.priority = priority;
		this.typeName = typeName;
		this.typeCd = typeCd;
	}
	
	public TruckWagonTypeWrapper(long priority, Long typeCd) {
		this(priority, "", typeCd);
	}
	
	@Override
	public String toString() {
		return "TruckWagonTypeWrapper(" + typeName + "; prio: " + priority + ", typeCd: " + typeCd + ")";
	}
	
	public int compareTo(TruckWagonTypeWrapper other) 
	{
		return TruckWagonTypeWrapperUtil.compareTruckTypeEntity(this, other);
	}
	
	public long getPriority() {
		return priority;
	}
	public void setPriority(long priority) {
		this.priority = priority;
	}
	
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public Long getTypeCd() {
		return typeCd;
	}
	public void setTypeCd(Long typeCd) {
		this.typeCd = typeCd;
	}
}
