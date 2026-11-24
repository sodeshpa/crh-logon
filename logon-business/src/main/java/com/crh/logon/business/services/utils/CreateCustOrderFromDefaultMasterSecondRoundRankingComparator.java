package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.util.Comparator;

import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;

public class CreateCustOrderFromDefaultMasterSecondRoundRankingComparator
		implements Comparator<ShiptoSareaRestrEntity>, Serializable {
	private static final long serialVersionUID = 8297859284983828112L;

	@Override
	public int compare(ShiptoSareaRestrEntity firstSsRest, ShiptoSareaRestrEntity secondSsRest) {
		if (firstSsRest.getPool() != null && secondSsRest.getPool() == null) {
			return 1;
		}
		else if (firstSsRest.getPool() == null && secondSsRest.getPool() != null) {
			return -1;
		}

		if (Boolean.TRUE.equals(firstSsRest.getAutoPoolAssignmentFlag()) && Boolean.FALSE.equals(secondSsRest.getAutoPoolAssignmentFlag())) {
			return 1;
		}
		else if (Boolean.FALSE.equals(firstSsRest.getAutoPoolAssignmentFlag()) && Boolean.TRUE.equals(secondSsRest.getAutoPoolAssignmentFlag())) {
			return -1;
		}

		if (firstSsRest.getCommCarrier() != null && secondSsRest.getCommCarrier() == null) {
			return 1;
		}
		else if (firstSsRest.getCommCarrier() == null && secondSsRest.getCommCarrier() != null) {
			return -1;
		}

		if (Boolean.TRUE.equals(firstSsRest.getAutoCarrierAssignmentFlag()) && Boolean.FALSE.equals(secondSsRest.getAutoCarrierAssignmentFlag())) {
			return 1;
		}
		else if (Boolean.FALSE.equals(firstSsRest.getAutoCarrierAssignmentFlag()) && Boolean.TRUE.equals(secondSsRest.getAutoCarrierAssignmentFlag())) {
			return -1;
		}

		if (firstSsRest.getExecCarrier() != null && secondSsRest.getExecCarrier() == null) {
			return 1;
		}
		else if (firstSsRest.getExecCarrier() == null && secondSsRest.getExecCarrier() != null) {
			return -1;
		}

		return firstSsRest.getCtlCreTs().compareTo(secondSsRest.getCtlCreTs());
	}
}