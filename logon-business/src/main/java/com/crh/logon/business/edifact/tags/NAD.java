/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.tags;

/**
 * @author AdNovum Informatik AG
 */
public class NAD extends Tag {

	private DataElementMeta partyQualifier = new DataElementMeta(0, 0, true);
	private DataElementMeta partyId = new DataElementMeta(1, 0, false);
	private DataElementMeta codeListQualifier = new DataElementMeta(1, 1, false);
	private DataElementMeta codeListResponsibleAgency = new DataElementMeta(1, 2, false);
	private DataElementMeta nameAndAddressLine = new DataElementMeta(2, 0, false);
	private DataElementMeta partyName = new DataElementMeta(3, 0, false);
	private DataElementMeta streetAndHouseNo = new DataElementMeta(4, 0, false);
	private DataElementMeta city = new DataElementMeta(5, 0, false);
	private DataElementMeta postalCode = new DataElementMeta(7, 0, false);
	private DataElementMeta country = new DataElementMeta(8, 0, false);
	private DataElementMeta[] dataElementMetas = { partyQualifier, partyId, codeListQualifier, codeListResponsibleAgency, nameAndAddressLine, partyName, streetAndHouseNo, city, postalCode, country };
	
	public NAD() {
		
		super("NAD", "Name and address");
	}
	
	public String getPartyQualifier() {
		return partyQualifier.getValue();
	}
	
	public void setPartyQualifier(String partyQualifier) {
		this.partyQualifier.setValue(partyQualifier);
	}
	
	public String getPartyId() {
		return partyId.getValue();
	}
	
	public void setPartyId(String partyId) {
		this.partyId.setValue(partyId);
	}
	
	public String getCodeListQualifier() {
		return codeListQualifier.getValue();
	}
	
	public void setCodeListQualifier(String codeListQualifier) {
		this.codeListQualifier.setValue(codeListQualifier);
	}
	
	public String getCodeListResponsibleAgency() {
		return codeListResponsibleAgency.getValue();
	}
	
	public void setCodeListResponsibleAgency(String codeListResponsibleAgency) {
		this.codeListResponsibleAgency.setValue(codeListResponsibleAgency);
	}
	
	public String getNameAndAddressLine() {
		return nameAndAddressLine.getValue();
	}

	public void setNameAndAddressLine(String nameAndAddressLine) {
		this.nameAndAddressLine.setValue(nameAndAddressLine);
	}

	public String getPartyName() {
		return partyName.getValue();
	}

	public void setPartyName(String partyName) {
		this.partyName.setValue(partyName);
	}

	public String getStreetAndHouseNo() {
		return streetAndHouseNo.getValue();
	}

	public void setStreetAndHouseNo(String streetAndHouseNo) {
		this.streetAndHouseNo.setValue(streetAndHouseNo);
	}

	public String getCity() {
		return city.getValue();
	}

	public void setCity(String city) {
		this.city.setValue(city);
	}

	public String getPostalCode() {
		return postalCode.getValue();
	}

	public void setPostalCode(String postalCode) {
		this.postalCode.setValue(postalCode);
	}

	public String getCountry() {
		return country.getValue();
	}
	
	public void setCountry(String country) {
		this.country.setValue(country);
	}
	
	@Override
	protected DataElementMeta[] getDataElementMetas() {
		return dataElementMetas;
	}
	
	
}
