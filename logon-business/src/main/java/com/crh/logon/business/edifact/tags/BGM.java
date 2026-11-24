/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.tags;



/**
 * @author AdNovum Informatik AG
 */
public class BGM extends Tag {
	
	private DataElementMeta documentNameCode = new DataElementMeta(0, 0, false);
	private DataElementMeta documentNo = new DataElementMeta(1, 0, false);
	private DataElementMeta msgFuncCode = new DataElementMeta(2, 0, false);
	private DataElementMeta[] dataElementMetas = { documentNameCode, documentNo, msgFuncCode };
	
	public BGM() {
		
		super("BGM", "Beginning of message");
	}

	public String getDocumentNameCode() {
		return documentNameCode.getValue();
	}

	public void setDocumentNameCode(String documentNameCode) {
		this.documentNameCode.setValue(documentNameCode);
	}

	public String getDocumentNo() {
		return documentNo.getValue();
	}

	public void setDocumentNo(String documentNo) {
		this.documentNo.setValue(documentNo);
	}

	public String getMsgFuncCode() {
		return msgFuncCode.getValue();
	}

	public void setMsgFuncCode(String msgFuncCode) {
		this.msgFuncCode.setValue(msgFuncCode);
	}

	@Override
	protected DataElementMeta[] getDataElementMetas() {
		
		return dataElementMetas;
	}

	
}
