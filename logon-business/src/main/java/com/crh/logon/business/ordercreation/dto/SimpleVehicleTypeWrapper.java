/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ordercreation.dto;

import java.math.BigDecimal;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.refdata.SimpleVehicleTypeTxEntity;

/**
 * This class extends the vehicle type wrapper class (TruckWagonTypeWrapper)
 * with implementing Volume m3 and Max gross weight of Simple Vehicle Types.
 * 
 * @author AdNovum Informatik AG
 */
public class SimpleVehicleTypeWrapper extends TruckWagonTypeWrapper {

	private static final long serialVersionUID = 8096614856025013787L;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SimpleVehicleTypeWrapper.class);
	
	private final SimpleVehicleTypeTxEntity simpleVehicleTypeTxEntity;
	
	
	public SimpleVehicleTypeWrapper(SimpleVehicleTypeTxEntity simpleVehicleTypeTxEntity) {
		super(1L, simpleVehicleTypeTxEntity.getTxt(), simpleVehicleTypeTxEntity.getSimpleVehicleTypeTxPK().getSimpleVehicleTypeBs().getSimpleVehicleTypeCd());
		// [[
		Object[][] params = {{"simpleVehicleTypeTxEntity", simpleVehicleTypeTxEntity}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "SimpleVehicleTypeWrapper", params);
		}
		// ]]
		try {
			this.simpleVehicleTypeTxEntity = simpleVehicleTypeTxEntity;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	
	/**
	 * @return the simpleVehicleTypeTxEntity
	 */
	public SimpleVehicleTypeTxEntity getSimpleVehicleTypeTxEntity() {
		return simpleVehicleTypeTxEntity;
	}

	
	@Override
	public BigDecimal getVolumeM3() {
		// [[
		BigDecimal result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getVolumeM3");
		}
		// ]]
		try {
			result = this.simpleVehicleTypeTxEntity.getSimpleVehicleTypeTxPK().getSimpleVehicleTypeBs().getVolumeM3();
			return result;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	
	@Override
	public BigDecimal getMaxGrossWeightT() {
		// [[
		BigDecimal result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getMaxGrossWeightT");
		}
		// ]]
		try {
			result = this.simpleVehicleTypeTxEntity.getSimpleVehicleTypeTxPK().getSimpleVehicleTypeBs().getPlanningNetWeightT();
			return result;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
}
