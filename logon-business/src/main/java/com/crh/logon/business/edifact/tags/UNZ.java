/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.tags;


/**
 * @author AdNovum Informatik AG
 */
public class UNZ extends Tag {
	
	private DataElementMeta interchangeControlCount = new DataElementMeta(0, 0, true);
	private DataElementMeta interchangeControlReference = new DataElementMeta(1, 0, true);
	private DataElementMeta[] dataElementMetas = { interchangeControlCount, interchangeControlReference };
	
	public UNZ() {
		
		super("UNZ", "Interchange Trailer");
	}

	public String getInterchangeControlCount() {
		return interchangeControlCount.getValue();
	}

	public void setInterchangeControlCount(String interchangeControlCount) {
		this.interchangeControlCount.setValue(interchangeControlCount);
	}

	public String getInterchangeControlReference() {
		return interchangeControlReference.getValue();
	}

	public void setInterchangeControlReference(String interchangeControlReference) {
		this.interchangeControlReference.setValue(interchangeControlReference);
	}

	@Override
	protected DataElementMeta[] getDataElementMetas() {
		
		return dataElementMetas;
	}

}
