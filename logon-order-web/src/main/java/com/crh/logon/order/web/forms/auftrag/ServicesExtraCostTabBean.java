/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.auftrag;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.crh.logon.persistence.entities.refdata.SapOrderExtraCostBsEntity;
import com.crh.logon.persistence.entities.refdata.SapOrderExtraCostTxEntity;

/**
 * @author AdNovum Informatik AG
 */
public class ServicesExtraCostTabBean implements Serializable{

	private static final long serialVersionUID = 1L;
	
	//Extra cost
	private String extraCost; //adding and removing extra cost for order
	private boolean extraCostFull;
	private boolean extraCostEmpty;
	
	private Long extraCostWaitingTimeMin;
	private List<SapOrderExtraCostBsEntity> activeExtraCostList;
	
	private Map <String, SapOrderExtraCostTxEntity> extraCostMap;
	private String extraCostsJSON;
	
	
	//Service materials
	private String selectedServiceCompositeIdentifier; //adding and removing extra cost for order
	private String serviceListJSON;
	
	public Long getExtraCostWaitingTimeMin() {
		return extraCostWaitingTimeMin;
	}
	public void setExtraCostWaitingTimeMin(Long extraCostWaitingTimeMin) {
		this.extraCostWaitingTimeMin = extraCostWaitingTimeMin;
	}
	public List<SapOrderExtraCostBsEntity> getActiveExtraCostList() {
		return activeExtraCostList;
	}
	public void setActiveExtraCostList(List<SapOrderExtraCostBsEntity> activeExtraCostList) {
		this.activeExtraCostList = activeExtraCostList;
	}
	public Map<String, SapOrderExtraCostTxEntity> getExtraCostMap() {
		return extraCostMap;
	}
	public void setExtraCostMap(Map<String, SapOrderExtraCostTxEntity> extraCostMap) {
		this.extraCostMap = extraCostMap;
	}
	public String getExtraCost() {
		return extraCost;
	}
	public void setExtraCost(String extraCost) {
		this.extraCost = extraCost;
	}
	public boolean isExtraCostFull() {
		return extraCostFull;
	}
	public void setExtraCostFull(boolean extraCostFull) {
		this.extraCostFull = extraCostFull;
	}
	public String getExtraCostsJSON() {
		return extraCostsJSON;
	}
	public void setExtraCostsJSON(String extraCostsJSON) {
		this.extraCostsJSON = extraCostsJSON;
	}
	public boolean isExtraCostEmpty() {
		return extraCostEmpty;
	}
	public void setExtraCostEmpty(boolean extraCostEmpty) {
		this.extraCostEmpty = extraCostEmpty;
	}
	public String getServiceListJSON() {
		return serviceListJSON;
	}
	public void setServiceListJSON(String serviceListJSON) {
		this.serviceListJSON = serviceListJSON;
	}
	public String getSelectedServiceCompositeIdentifier() {
		return selectedServiceCompositeIdentifier;
	}
	public void setSelectedServiceCompositeIdentifier(String selectedServiceCompositeIdentifier) {
		this.selectedServiceCompositeIdentifier = selectedServiceCompositeIdentifier;
	}
}