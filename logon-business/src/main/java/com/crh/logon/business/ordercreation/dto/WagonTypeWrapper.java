/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ordercreation.dto;

import java.math.BigDecimal;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.common.ShiptoWagonTypeEntity;
import com.crh.logon.persistence.entities.refdata.WagonTypeTxEntity;

/**
 * This class extends the vehicle type wrapper class (TruckWagonTypeWrapper)
 * with implementing Volume m3 and Max gross weight of Wagon Types.
 * 
 * @author AdNovum Informatik AG
 */
public class WagonTypeWrapper extends TruckWagonTypeWrapper {

	private static final long serialVersionUID = -3202724628948933705L;
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(WagonTypeWrapper.class);
	
	final ShiptoWagonTypeEntity shiptoWagonTypeEntity;
	final WagonTypeTxEntity wagonTypeTxEntity;
	
	public WagonTypeWrapper(long priority, WagonTypeTxEntity wagonTypeTxEntity, ShiptoWagonTypeEntity shiptoWagonTypeEntity) {
		super(priority, wagonTypeTxEntity.getTxt(), wagonTypeTxEntity.getWagonTypeTxPK().getWagonTypeBs().getWagonTypeCd());
		// [[
		Object[][] params = {{"priority", priority + ""},
							{"wagonTypeTxEntity", wagonTypeTxEntity},
							{"shiptoWagonTypeEntity", shiptoWagonTypeEntity}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "WagonTypeWrapper", params);
		}
		// ]]
		try {
			this.shiptoWagonTypeEntity = shiptoWagonTypeEntity;
			this.wagonTypeTxEntity = wagonTypeTxEntity;
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
	 * Can be used for testing or where the entity fields are not needed.
	 * This could be removed once a mocking framework is introduced. 
	 */
	public WagonTypeWrapper() {
		super(0L, 0L);
		this.wagonTypeTxEntity = null;
		this.shiptoWagonTypeEntity = null;
	}
	
	public ShiptoWagonTypeEntity getShiptoWagonTypeEntity() {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("getShiptoWagonTypeEntity(): '" + shiptoWagonTypeEntity + "'");
		}
		// ]]
		return shiptoWagonTypeEntity;
	}

	public WagonTypeTxEntity getWagonTypeTxEntity() {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("getWagonTypeTxEntity(): '" + wagonTypeTxEntity + "'");
		}
		// ]]
		return wagonTypeTxEntity;
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
			return result = wagonTypeTxEntity.getWagonTypeTxPK().getWagonTypeBs().getVolumeM3();
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
			return result = wagonTypeTxEntity.getWagonTypeTxPK().getWagonTypeBs().getMaxGrossWeightT();
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
