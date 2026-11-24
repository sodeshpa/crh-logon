/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.crh.logon.business.services.beans.SelectableGeneralDisplayNameDto;

/**
 * Represents relationship between pools and carriers.
 * @author AdNovum Informatik AG
 */
public class PoolCarrierRelationWrapper implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<SelectableGeneralDisplayNameDto> carriers;
	private List<SelectableGeneralDisplayNameDto> pools;
	private Map<Long, List<SelectableGeneralDisplayNameDto>> poolCarrierMap;
	private Set<Long> selectedPoolIds = new HashSet<Long>();
	private Set<Long> selectedCarrierIds = new HashSet<Long>();
	
	public PoolCarrierRelationWrapper(
			List<SelectableGeneralDisplayNameDto> pools,
			List<SelectableGeneralDisplayNameDto> carriers,
			Set<Long> selectedPoolIds,
			Set<Long> selectedCarrierIds,
			Map<Long, List<SelectableGeneralDisplayNameDto>> poolCarrierMap) {
		this.pools = pools;
		this.carriers = carriers;
		this.selectedPoolIds = selectedPoolIds;
		this.selectedCarrierIds = selectedCarrierIds;
		this.poolCarrierMap = poolCarrierMap;
	}

	public List<SelectableGeneralDisplayNameDto> getCarriers() {
		return carriers;
	}

	public List<SelectableGeneralDisplayNameDto> getPools() {
		return pools;
	}

	public Map<Long, List<SelectableGeneralDisplayNameDto>> getPoolCarrierMap() {
		return poolCarrierMap;
	}

	public Set<Long> getSelectedPoolIds() {
		return selectedPoolIds;
	}

	public Set<Long> getSelectedCarrierIds() {
		return selectedCarrierIds;
	}

}