/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans.pdf.deliverynote;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author AdNovum Informatik AG
 */
public class AddressEntryData implements Serializable {
	private static final long serialVersionUID = -2414711601987441325L;

	boolean visible = true;
	
	private String name;
	private String id;
	private String address1;
	private String address2;
	private String address3;
	private String phone;
	private String vat;
	private String tax;
	
	/**
	 * Contact name + telephone number used in the case of Ship-To Contact, could be null.
	 * Not included in the body field list.
	 */
	private String contact;



	public List<String> getBodyFieldList() {
		List<String> list = new ArrayList<String>();
		
		list.add(this.address1);
		list.add(this.address2);
		list.add(this.address3);
		list.add(this.phone);
		list.add(this.vat);
		list.add(this.tax);
		
		return list;
	}
	
	public AddressEntryData deepCopy() {
		AddressEntryData copy = new AddressEntryData();
		
		copy.setAddress1(this.getAddress1());
		copy.setAddress2(this.getAddress2());
		copy.setAddress3(this.getAddress3());
		copy.setId(this.getId());
		copy.setVat(this.getVat());
		copy.setTax(this.getTax());
		copy.setName(this.getName());
		copy.setContact(this.getContact());
		
		return copy;
	}
	
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public String getVat() {
		return vat;
	}
	public void setVat(String vat) {
		this.vat = vat;
	}
	public String getTax() {
		return tax;
	}
	public void setTax(String tax) {
		this.tax = tax;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getAddress3() {
		return address3;
	}
	public void setAddress3(String address3) {
		this.address3 = address3;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Returns the contact information
	 * @return the contact
	 */
	public String getContact() {
		return contact;
	}

	/**
	 * Sets the Contact information
	 * @param contact the contact to set
	 */
	public void setContact(String contact) {
		this.contact = contact;
	}
	
	/**
	 * Returns true, if the {@link AddressEntryData} has Contact information.
	 * @return true, if the {@link AddressEntryData} has Contact information.
	 */
	public boolean hasContact() {
		return this.contact != null;
	}
}
