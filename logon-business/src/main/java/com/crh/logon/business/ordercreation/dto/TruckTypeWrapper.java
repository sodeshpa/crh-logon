/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ordercreation.dto;

import java.math.BigDecimal;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.common.ShiptoTruckTypeEntity;
import com.crh.logon.persistence.entities.refdata.TruckTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.TruckTypeTxEntity;

/**
 * This class extends the vehicle type wrapper class (TruckWagonTypeWrapper)
 * with implementing Volume m3 and Max gross weight of Truck Types.
 * 
 * @author AdNovum Informatik AG
 */
public class TruckTypeWrapper extends TruckWagonTypeWrapper 
{
	private static final long serialVersionUID = 2609271239145195329L;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(TruckTypeWrapper.class);
	
	private final ShiptoTruckTypeEntity shiptoTruckTypeEntity;
	/**
	 * This truckTypeBsEntity is not null in any cases.
	 */
	private final TruckTypeBsEntity truckTypeBsEntity;
	
	/**
	 * This truckTypeTxEntity could be null if the TruckTypeBsEntity constructor is called.
	 */
	private final TruckTypeTxEntity truckTypeTxEntity;

	/**
	 * With calling this constructor, no need no provide a TruckTypeTxEntity.
	 * Choose this constructor if DON'T need to display TruckType-s on the UI.
	 */
	public TruckTypeWrapper(long priority, TruckTypeBsEntity truckTypeBsEntity,
			ShiptoTruckTypeEntity shiptoTruckTypeEntity) {
		super(priority, truckTypeBsEntity.getTruckTypeCd());
		// [[
		Object[][] params = {{"priority", priority + ""},
							{"truckTypeBsEntity", truckTypeBsEntity},
							{"shiptoTruckTypeEntity", shiptoTruckTypeEntity}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "TruckTypeWrapper", params);
		}
		// ]]
		try {
			this.shiptoTruckTypeEntity = shiptoTruckTypeEntity;
			this.truckTypeBsEntity = truckTypeBsEntity;
			this.truckTypeTxEntity = null;
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
	 * With calling this constructor, TruckTypeTxEntity should be provided.
	 * Choose this constructor if you need to display TruckType-s on the UI.
	 */
	public TruckTypeWrapper(long priority, TruckTypeTxEntity truckTypeTxEntity,
			ShiptoTruckTypeEntity shiptoTruckTypeEntity) {
		super(priority, truckTypeTxEntity.getTxt(),
				truckTypeTxEntity.getTruckTypeTxPK().getTruckTypeBs().getTruckTypeCd());
		// [[
		Object[][] params = {{"priority", priority + ""},
							{"truckTypeTxEntity", truckTypeTxEntity},
							{"shiptoTruckTypeEntity", shiptoTruckTypeEntity}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "TruckTypeWrapper", params);
		}
		// ]]
		try {
			this.shiptoTruckTypeEntity = shiptoTruckTypeEntity;
			this.truckTypeTxEntity = truckTypeTxEntity;
			this.truckTypeBsEntity = truckTypeTxEntity.getTruckTypeTxPK().getTruckTypeBs();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	
	public ShiptoTruckTypeEntity getShiptoTruckTypeEntity() {
		return shiptoTruckTypeEntity;
	}

	public TruckTypeBsEntity getTruckTypeBsEntity() {
		return truckTypeBsEntity;
	}
	
	public TruckTypeTxEntity getTruckTypeTxEntity() {
		return truckTypeTxEntity;
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
			return result = truckTypeBsEntity.getVolumeM3();
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
			return result = truckTypeBsEntity.getPlanningNetWeightT();
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
