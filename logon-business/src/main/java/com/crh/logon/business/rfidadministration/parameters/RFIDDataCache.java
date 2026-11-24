package com.crh.logon.business.rfidadministration.parameters;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

import com.crh.logon.persistence.entities.RfidTagComparator;
import com.crh.logon.persistence.entities.common.RfidtagEntity;

/**
 * @author AdNovum Informatik AG
 * 
 * Used to maintain rfid tag assignment statuses within a persistence boundary
 * In practice: when rfid-tag to rfid-object assignments change within a boundary 
 * without being persisted, their temporary assignment status is maintained here. 
 */
public class RFIDDataCache implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String SESSION_KEY = "rfidDataCache";

	private final Set<RfidtagEntity> rfidsToAddOnPersist;
	private final Set<RfidtagEntity> rfidsToRemoveOnPersist;

	public RFIDDataCache() {
		RfidTagComparator rfidTagComparator = new RfidTagComparator();
		rfidsToAddOnPersist = new TreeSet<RfidtagEntity>(rfidTagComparator);
		rfidsToRemoveOnPersist = new TreeSet<RfidtagEntity>(rfidTagComparator);
	}

	public Set<RfidtagEntity> getRfidsToAddOnPersist() {
		return rfidsToAddOnPersist;
	}

	public Set<RfidtagEntity> getRfidsToRemoveOnPersist() {
		return rfidsToRemoveOnPersist;
	}
}