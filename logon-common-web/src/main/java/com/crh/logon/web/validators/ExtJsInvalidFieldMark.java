/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.validators;

/**
 * @author AdNovum Informatik AG
 */
public class ExtJsInvalidFieldMark {

	private String titleKey;
	private String fieldId;
	private String msgTarget; // 'qtip' or 'side'
	private boolean isResize;
	
	public ExtJsInvalidFieldMark(String titleKey, String fieldId, String type, boolean isResize) {
		this.titleKey = titleKey;
		this.fieldId = fieldId;
		this.msgTarget = type;
		this.isResize = isResize;
	}
	
	public String getTitleKey() {
		return titleKey;
	}
	public void setTitleKey(String titleKey) {
		this.titleKey = titleKey;
	}
	public String getFieldId() {
		return fieldId;
	}
	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}
	public boolean isResize() {
		return isResize;
	}
	public void setResize(boolean isResize) {
		this.isResize = isResize;
	}

	public String getMsgTarget() {
		return msgTarget;
	}

	public void setMsgTarget(String msgTarget) {
		this.msgTarget = msgTarget;
	}
	
}
