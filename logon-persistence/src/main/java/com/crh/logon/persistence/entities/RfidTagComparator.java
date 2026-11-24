package com.crh.logon.persistence.entities;

import java.io.Serializable;
import java.util.Comparator;

import com.crh.logon.persistence.entities.common.RfidtagEntity;

public class RfidTagComparator implements Comparator<RfidtagEntity>, Serializable {
	private static final long serialVersionUID = 8297859284983828112L;

	@Override
	public int compare(RfidtagEntity firstTag, RfidtagEntity secondTag) {
		String firstTagName = firstTag.getName();
		String secondTagName = secondTag.getName();
		return firstTagName.compareTo(secondTagName);
	}
}