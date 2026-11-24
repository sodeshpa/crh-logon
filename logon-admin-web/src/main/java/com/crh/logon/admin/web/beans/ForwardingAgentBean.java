/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.PoolCarrierEntity;
import com.crh.logon.persistence.entities.common.PoolTxEntity;
import com.crh.logon.persistence.entities.sap.SapVendorEntity;

/**
 * @author AdNovum Informatik AG
 */
public class ForwardingAgentBean implements Serializable {
	
    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(ForwardingAgentBean.class);
    
	private boolean edited;
	private String sapId;
	private Long sapVendorId;
    private String name;
    private String postalCode;
    private String city;
    private String street;
    
    private CarrierEntity carrier;
    private Object coordinates; // TODO (istvan May 27, 2009): purpose not yet known
    private Map possiblePools;
    private Map selectedPools;
    
    //implement same as uadmin -> selectedroles
	
	public ForwardingAgentBean(SapVendorEntity sapVendor, CarrierEntity carrier, List possiblePoolsList, List selectedPoolsList) {
			this.edited = false;
			this.sapId = sapVendor.getSapId();
			this.sapVendorId = sapVendor.getSapVendorId();
			this.name = sapVendor.getName1();
			this.postalCode = sapVendor.getPostalCode();
			this.street = sapVendor.getStreet();
			this.city = sapVendor.getCity();
		if (carrier != null) {
			this.carrier = carrier;
		}
		else {
			this.carrier = new CarrierEntity();
			this.carrier.setSapVendor(sapVendor); // TODO (istvan May 28, 2009): should it be done here ?
		}
		this.possiblePools = new HashMap();
		if (possiblePoolsList != null && ! possiblePoolsList.isEmpty()) {
			for (Iterator iterator = possiblePoolsList.iterator(); iterator.hasNext();) {
				PoolTxEntity poolEntity = (PoolTxEntity) iterator.next();
				possiblePools.put(poolEntity.getPoolTxPK().getPoolId(), poolEntity);
			}
		}
		this.selectedPools = new HashMap();
		if (selectedPoolsList != null && ! selectedPoolsList.isEmpty()) {
			for (Iterator iterator = selectedPoolsList.iterator(); iterator.hasNext();) {
				PoolCarrierEntity poolEntity = (PoolCarrierEntity) iterator.next();
				Long poolId = poolEntity.getPool().getPoolId();
				selectedPools.put(poolId, poolId);
			}
		}
}
	
	public ForwardingAgentBean() {
		super();
	}

	public String getSapId() {
		return sapId;
	}

	public void setSapId(String sapId) {
		this.sapId = sapId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public Object getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Object coordinates) {
		this.coordinates = coordinates;
	}

	public Map getSelectedPools() {
		return selectedPools;
	}

	public void setSelectedPools(Map selectedPools) {
		this.selectedPools = selectedPools;
	}

	public Map getPossiblePools() {
		return possiblePools;
	}

	public void setPossiblePools(Map possiblePools) {
		this.possiblePools = possiblePools;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
    public boolean isEdited() {
		return edited;
	}

	public void setEdited(boolean edited) {
		this.edited = edited;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}
	
	
    public CarrierEntity getCarrier() {
		return carrier;
	}

	public void setCarrier(CarrierEntity carrier) {
		this.carrier = carrier;
	}

	public Long getSapVendorId() {
		return sapVendorId;
	}

	public void setSapVendorId(Long sapVendorId) {
		this.sapVendorId = sapVendorId;
	}

	public Object getSelectedPoolMapEntry(String key) {
		// [[
		Object result = null;
		Object[][] params = {{"key", key}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getSelectedPoolMapEntry", params);
		}
		// ]]
		try {
			boolean booleanResult = false;
			if(this.selectedPools != null && this.selectedPools.get(Long.valueOf(key)) != null){
				booleanResult = true;
			}
			return result = String.valueOf(booleanResult);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }

    public void setSelectedPoolMapEntry(String key, Object value) {
		// [[
		Object[][] params = {{"key", key},
							{"value", value}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setSelectedPoolMapEntry", params);
		}
		// ]]
		try {
			PoolTxEntity pool = (PoolTxEntity) this.possiblePools.get(Long.valueOf(key));
			Long poolId = pool.getPoolTxPK().getPoolId();
			this.selectedPools.put(poolId, poolId);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }
    
    public void resetSelectedPools() {
    	this.selectedPools = new HashMap();
    }
}
