/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.cust.web.forms.webbestellung;

import java.io.Serializable;

/**
 * @author AdNovum Informatik AG
 */
public class DummyFields implements Serializable {

	private String language = "Dummy language";
	private String topSeller = "Dummy topSeller";
	private Boolean delNoteFaxFlag;
	private Boolean delNoteMailFlag;
	private Boolean delNoteShippingPointFlag;
	private Boolean autoPoolAssignmentFlag;
	private Boolean autoCarrierAssignmentFlag;
	private String noteCustWebOrder = "Dummy noteCustWebOrder";
	// TODO move to BestellungForm
	private String delTimeMin;
	private String delTimeMax;
	
	public String getNoteCustWebOrder() {
		return noteCustWebOrder;
	}
	public void setNoteCustWebOrder(String noteCustWebOrder) {
		this.noteCustWebOrder = noteCustWebOrder;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String languagel) {
		this.language = languagel;
	}
	public String getTopSeller() {
		return topSeller;
	}
	public void setTopSeller(String topSeller) {
		this.topSeller = topSeller;
	}
	public Boolean getDelNoteFaxFlag() {
		return delNoteFaxFlag;
	}
	public void setDelNoteFaxFlag(Boolean delNoteFaxFlag) {
		this.delNoteFaxFlag = delNoteFaxFlag;
	}
	public Boolean getDelNoteMailFlag() {
		return delNoteMailFlag;
	}
	public void setDelNoteMailFlag(Boolean delNoteMailFlag) {
		this.delNoteMailFlag = delNoteMailFlag;
	}
	public Boolean getDelNoteShippingPointFlag() {
		return delNoteShippingPointFlag;
	}
	public void setDelNoteShippingPointFlag(Boolean delNoteShippingPointFlag) {
		this.delNoteShippingPointFlag = delNoteShippingPointFlag;
	}
	public Boolean getAutoPoolAssignmentFlag() {
		return autoPoolAssignmentFlag;
	}
	public void setAutoPoolAssignmentFlag(Boolean autoPoolAssignment) {
		this.autoPoolAssignmentFlag = autoPoolAssignment;
	}
	public Boolean getAutoCarrierAssignmentFlag() {
		return autoCarrierAssignmentFlag;
	}
	public void setAutoCarrierAssignmentFlag(Boolean autoCarrierAssignment) {
		this.autoCarrierAssignmentFlag = autoCarrierAssignment;
	}
	public String getDelTimeMin() {
		return delTimeMin;
	}
	public void setDelTimeMin(String delTimeMin) {
		this.delTimeMin = delTimeMin;
	}
	public String getDelTimeMax() {
		return delTimeMax;
	}
	public void setDelTimeMax(String delTimeMax) {
		this.delTimeMax = delTimeMax;
	}
	
}
