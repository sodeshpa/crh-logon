/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.auftrag;

import java.io.Serializable;
import java.util.Map;
/**
 * @author AdNovum Informatik AG
 */
public class ChangeBpBean implements Serializable{


	private static final long serialVersionUID = 1L;
	
	//sold to
	private String soldtoSapId;
	private String soldtoName;

	//ship to
	private String shiptoSapId;
	private String shiptoName;

	//bill to
	private String billtoSapBpId;
	private String billtoName;
	
	private String sareaId;
	private Map <String, ShiptoBilltoBean> billtoMap;

	
	public String getSoldtoSapId() {
		return soldtoSapId;
	}
	public void setSoldtoSapId(String soldtoSapId) {
		this.soldtoSapId = soldtoSapId;
	}
	public String getShiptoSapId() {
		return shiptoSapId;
	}
	public void setShiptoSapId(String shiptoSapId) {
		this.shiptoSapId = shiptoSapId;
	}
	public String getSoldtoName() {
		return soldtoName;
	}
	public void setSoldtoName(String soldtoName) {
		this.soldtoName = soldtoName;
	}
	public String getShiptoName() {
		return shiptoName;
	}
	public void setShiptoName(String shiptoName) {
		this.shiptoName = shiptoName;
	}
	public String getBilltoName() {
		return billtoName;
	}
	public void setBilltoName(String billtoName) {
		this.billtoName = billtoName;
	}
	public String getSareaId() {
		return sareaId;
	}
	public void setSareaId(String sareaId) {
		this.sareaId = sareaId;
	}
	public Map <String, ShiptoBilltoBean> getBilltoMap() {
		return billtoMap;
	}
	public void setBilltoMap(Map <String, ShiptoBilltoBean> billtoMap) {
		this.billtoMap = billtoMap;
	}
	public String getBilltoSapBpId() {
		return billtoSapBpId;
	}
	public void setBilltoSapBpId(String billtoSapBpId) {
		this.billtoSapBpId = billtoSapBpId;
	}
	
}
