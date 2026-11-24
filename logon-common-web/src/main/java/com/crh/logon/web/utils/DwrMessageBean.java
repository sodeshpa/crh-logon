/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.utils;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author AdNovum Informatik AG
 */
public class DwrMessageBean implements Serializable {

	private String type = null;

	private String recordId = null;

	private String fieldId = null;

	private String message = null;

	public DwrMessageBean() {
		super();
	}

	public DwrMessageBean(String type, String recordId, String fieldId,
			String message) {
		this.type = type;
		this.recordId = recordId;
		this.fieldId = fieldId;
		this.message = message;
	}

	public String getFieldId() {
		return this.fieldId;
	}

	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRecordId() {
		return this.recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		ToStringBuilder stringBuilder = new ToStringBuilder(this);

		stringBuilder.append("type", this.type);
		stringBuilder.append("recordId", this.recordId);
		stringBuilder.append("fieldId", this.fieldId);
		stringBuilder.append("message", this.message);

		return stringBuilder.toString();
	}

	public static final class MsgTypes {

		public static final String ERROR = "error";

		public static final String INFO = "info";
		
		public static final String SUCCESS = "success";

		public static final String WARNING = "warning";

	}

}
