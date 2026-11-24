/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;

/**
 * @author AdNovum Informatik AG
 */
public class TableBrowserSequenceWrapper implements Serializable {

	private String sequenceName;
	private boolean isTargetEntryId; 
	
	public TableBrowserSequenceWrapper(String sequenceName, boolean isTargetEntryId) {
		
		this.sequenceName = sequenceName;
		this.isTargetEntryId = isTargetEntryId;
	}

	public String getSequenceName() {
		return sequenceName;
	}

	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}
	
	public boolean isTargetEntryId() {
		return isTargetEntryId;
	}

	public void setTargetEntryId(boolean isTargetEntryId) {
		this.isTargetEntryId = isTargetEntryId;
	}
}
