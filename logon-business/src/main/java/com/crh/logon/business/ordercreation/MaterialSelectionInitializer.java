/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ordercreation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.ContractItemQuantity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;


public class MaterialSelectionInitializer implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(MaterialSelectionInitializer.class);

	private ShiptoSareaEntity shiptoSarea;
	private Set<Long> sapMaterialIds;

	/**
	 * Mapping of contract item ids to quantity information. The contract item ids refer to contract
	 * items found in the Map<SapMaterialEntity, List<SapMatSareaContractEntity>> member variables.
	 * Not all contract items are guaranteed to have quantity information.
	 */
	Map<Long, ContractItemQuantity> quantitiesByContractItemIdsMap;


	/**
	 * Returns all product ids available for this sales area in ANY form (either coming from contract, or restriction)
	 * in the maps which were initialized for the certain sources.
	 * @return
	 */
	public Set<Long> getSapMaterialIds() {
		return sapMaterialIds;
	}

	/**
	 * @param sapMaterialIds the sapMaterialIds to set
	 */
	public void setSapMaterialIds(Set<Long> sapMaterialIds) {
		this.sapMaterialIds = sapMaterialIds;
	}

	public ShiptoSareaEntity getShiptoSarea() {
		return shiptoSarea;
	}
	
	public void setShiptoSarea(ShiptoSareaEntity shiptoSarea) {
		this.shiptoSarea = shiptoSarea;
	}

	public void setQuantitiesByContractItemIdsMap(Map<Long, ContractItemQuantity> quantitiesByContractItemIdsMap) {
		this.quantitiesByContractItemIdsMap = quantitiesByContractItemIdsMap;
	}

	public Map<Long, ContractItemQuantity> getQuantitiesByContractItemIdsMap() {
		if (quantitiesByContractItemIdsMap == null) {
			quantitiesByContractItemIdsMap = new HashMap<Long, ContractItemQuantity>();
		}
		return quantitiesByContractItemIdsMap;
	}
}
