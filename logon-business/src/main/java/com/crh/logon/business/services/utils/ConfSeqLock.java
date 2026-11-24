/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.crh.logon.business.services.impl.ConfigurableSequencesServiceBean;

/**
 * <strong>Do not use this class outside {@link ConfigurableSequencesServiceBean}</strong>
 * @author AdNovum Informatik AG
 */
public class ConfSeqLock implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final Map<Long, Map<Long, Long>> counterMap;
	private final Set<Long> counterSet;
	
	/**
	 * <strong>You must not call this constructor outside {@link ConfigurableSequencesServiceBean}</strong>
	 */
	public ConfSeqLock() {
		counterMap = new HashMap<Long, Map<Long, Long>>();
		counterSet = new HashSet<Long>();
	}
	
	public void add(Long contractId, Long shippingpointId, Long counterId) {
		if (counterMap.get(contractId) == null) {
			counterMap.put(contractId, new HashMap<Long, Long>());
		}
		counterMap.get(contractId).put(shippingpointId, counterId);
		if (counterId != null) {
			counterSet.add(counterId);
		}
	}
	
	public boolean contains(Long contractId, Long shippingpointId) {
		if (counterMap.containsKey(contractId)) {
			Map<Long, Long> innerMap = counterMap.get(contractId);
			if (innerMap != null) {
				if (innerMap.containsKey(shippingpointId)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public Set<Long> getCounters() {
		return counterSet;
	}
	
	public Long get(Long contractId, Long shippingpointId) {
		if (!contains(contractId, shippingpointId)) {
			// TODO 02-Apr-2013/stich: throw exception instead
			return null;
		}
		return counterMap.get(contractId).get(shippingpointId);
	}
}