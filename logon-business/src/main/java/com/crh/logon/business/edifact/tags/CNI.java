/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.tags;


/**
 * @author AdNovum Informatik AG
 */
public class CNI extends Tag {

	private DataElementMeta consolidationItemNo = new DataElementMeta(0, 0, false);
	private DataElementMeta documentNo = new DataElementMeta(1, 0, false);
	private DataElementMeta loadSequenceNo = new DataElementMeta(2, 0, false);
	private DataElementMeta[] dataElementMetas = { consolidationItemNo, documentNo, loadSequenceNo };
	
	public CNI() { 
		
		super("CNI", "Consignment information");
	}

	public String getConsolidationItemNo() {
		return consolidationItemNo.getValue();
	}

	public void setConsolidationItemNo(String consolidationItemNo) {
		this.consolidationItemNo.setValue(consolidationItemNo);
	}

	public String getDocumentNo() {
		return documentNo.getValue();
	}

	public void setDocumentNo(String documentNo) {
		this.documentNo.setValue(documentNo);
	}

	public String getLoadSequenceNo() {
		return loadSequenceNo.getValue();
	}

	public void setLoadSequenceNo(String loadSequenceNo) {
		this.loadSequenceNo.setValue(loadSequenceNo);
	}

	@Override
	protected DataElementMeta[] getDataElementMetas() {
		return dataElementMetas;
	}

}
