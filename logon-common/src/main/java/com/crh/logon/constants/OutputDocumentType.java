/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.constants;

public enum OutputDocumentType {
	DELIVERY_NOTE(1000l),
    EXPORT_INVOICE (1001l),
	COLLECTIVE_DELIVERY_NOTE (1002l),
	CASH_SALES (1003l);
	
	public final long value ;
	
	OutputDocumentType(long value) {
		this.value = value;
	}
	
	public long getValue() {
		return this.value;
	}
}
