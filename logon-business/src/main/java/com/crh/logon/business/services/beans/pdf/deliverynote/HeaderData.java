/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans.pdf.deliverynote;

import java.io.Serializable;

/**
 * @author AdNovum Informatik AG
 */
public class HeaderData implements Serializable {
	private static final long serialVersionUID = -5266491123445606486L;
	
	private TitleData title;
	private SalesOrganizationData salesOrganization;
	private OrderHeaderData orderHeader;
	private BusinessAddressData businessAddress;
	private CrossBoundaryData crossBoundary;
	
	public BusinessAddressData getBusinessAddress() {
		return businessAddress;
	}
	public void setBusinessAddress(BusinessAddressData businessAddress) {
		this.businessAddress = businessAddress;
	}
	public CrossBoundaryData getCrossBoundary() {
		return crossBoundary;
	}
	public void setCrossBoundary(CrossBoundaryData crossBoundary) {
		this.crossBoundary = crossBoundary;
	}
	public TitleData getTitle() {
		return title;
	}
	public void setTitle(TitleData title) {
		this.title = title;
	}
	public SalesOrganizationData getSalesOrganization() {
		return salesOrganization;
	}
	public void setSalesOrganization(SalesOrganizationData salesOrganization) {
		this.salesOrganization = salesOrganization;
	}
	public OrderHeaderData getOrderHeader() {
		return orderHeader;
	}
	public void setOrderHeader(OrderHeaderData orderHeader) {
		this.orderHeader = orderHeader;
	}
}
