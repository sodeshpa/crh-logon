/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;

import com.crh.logon.persistence.entities.views.BizeventAuditDisplayEntity;
import com.crh.logon.utils.LogonException;

public class BizeventAuditWrapper implements Serializable {

	private static final long serialVersionUID = 1L;

	private final BizeventAuditDisplayEntity entity;

	private final String eventTime;

	private String oldValueDisplay;

	private String newValueDisplay;

	public BizeventAuditWrapper(BizeventAuditDisplayEntity entity, TimeZone timeZone) {
		super();
		this.entity = entity;
		this.eventTime = timeZone.fmtAsDayMonthYear4HourMinute(entity.getEventTime());
	}

	public BizeventAuditDisplayEntity getEntity() {
		return entity;
	}


	/**
	 * Returns the attribute which needs to be resolved. Depending on the type of the audited field the value is stored in one of
	 * the 3 fields: Long, String, Dat.
	 */
	public Object getOldDbValue() {

		final Long auditBizeventConfigCd = entity.getAuditBizeventConfigCd();
		if (BizeventAuditUtil.isAuditBizeventStoringId(auditBizeventConfigCd)) {
			return entity.getOldValueId();
		}
		else if (BizeventAuditUtil.isAuditBizeventStoringString(auditBizeventConfigCd)) {
			return entity.getOldValueStr();
		}
		else if (BizeventAuditUtil.isAuditBizeventStoringDat(auditBizeventConfigCd)) {
			return entity.getOldValueDat();
		}
		throw new LogonException();
	}


	/**
	 * Returns the attribute which needs to be resolved. Depending on the type of the audited field the value is stored in one of
	 * the 3 fields: Long, String, Date.
	 */
	public Object getNewDbValue() {

		final Long auditBizeventConfigCd = entity.getAuditBizeventConfigCd();
		if (BizeventAuditUtil.isAuditBizeventStoringId(auditBizeventConfigCd)) {
			return entity.getNewValueId();
		}
		else if (BizeventAuditUtil.isAuditBizeventStoringString(auditBizeventConfigCd)) {
			return entity.getNewValueStr();
		}
		else if (BizeventAuditUtil.isAuditBizeventStoringDat(auditBizeventConfigCd)) {
			return entity.getNewValueDat();
		}
		throw new LogonException();
	}

	public void setOldValueDisplay(String oldValueDisplay) {
		this.oldValueDisplay = oldValueDisplay;
	}

	public void setNewValueDisplay(String newValueDisplay) {
		this.newValueDisplay = newValueDisplay;
	}


	public String getOldValueDisplay() {
		return oldValueDisplay;
	}


	public String getNewValueDisplay() {
		return newValueDisplay;
	}

	public boolean isValueChanged() {
		return (getNewValueDisplay() != null && getOldValueDisplay() != null && !getNewValueDisplay()
				.equals(getOldValueDisplay()))
				|| (getNewValueDisplay() == null && getOldValueDisplay() != null)
				|| (getNewValueDisplay() != null && getOldValueDisplay() == null);
	}

	public String getEventTime() {
		return eventTime;
	}
}
