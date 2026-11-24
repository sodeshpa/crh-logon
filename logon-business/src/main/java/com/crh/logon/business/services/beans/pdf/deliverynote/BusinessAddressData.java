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
public class BusinessAddressData implements Serializable {
	private static final long serialVersionUID = 6988249487305159924L;
	
	private AddressEntryData shipTo;
	private AddressEntryData billTo;
	private AddressEntryData payer;
	private AddressEntryData soldTo;
	private AddressEntryData commCarrier;
	private AddressEntryData execCarrier;
	
	public List<AddressEntryData> getAddressEntryList() {
		List<AddressEntryData> list = new ArrayList<AddressEntryData>();
		
		list.add(shipTo);
		list.add(billTo);
		list.add(payer);
		list.add(soldTo);
		list.add(commCarrier);
		list.add(execCarrier);
		
		return list;
	}
	public AddressEntryData getShipTo() {
		return shipTo;
	}
	public void setShipTo(AddressEntryData shipTo) {
		this.shipTo = shipTo;
	}
	public AddressEntryData getBillTo() {
		return billTo;
	}
	public void setBillTo(AddressEntryData billTo) {
		this.billTo = billTo;
	}
	public AddressEntryData getPayer() {
		return payer;
	}
	public void setPayer(AddressEntryData payer) {
		this.payer = payer;
	}
	public AddressEntryData getSoldTo() {
		return soldTo;
	}
	public void setSoldTo(AddressEntryData soldTo) {
		this.soldTo = soldTo;
	}
	public AddressEntryData getCommCarrier() {
		return commCarrier;
	}
	public void setCommCarrier(AddressEntryData commCarrier) {
		this.commCarrier = commCarrier;
	}
	public AddressEntryData getExecCarrier() {
		return execCarrier;
	}
	public void setExecCarrier(AddressEntryData execCarrier) {
		this.execCarrier = execCarrier;
	}
}
