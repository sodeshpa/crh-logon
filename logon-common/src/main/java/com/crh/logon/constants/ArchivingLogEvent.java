/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.constants;

public enum ArchivingLogEvent {
	START_ORDER_ARCHIVING (1000l),
    TRANSMIT_DATA_TO_ARCHIVING_SERVICE (1001l),
	RECEIVE_RESPONSE_FROM_ARCHIVING_SERVICE (1002l),
	UNABLE_TO_DELETE_ORDER (1027l),
	WHOLE_BATCH_FAILED(1028l);
	
	public final long value ;
	
	ArchivingLogEvent(long value) {
		this.value = value;
	}
	
	public long getValue() {
		return this.value;
	}
}
