/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.reporting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.struts.action.ActionForm;

import com.crh.logon.business.services.beans.SelectableGeneralDisplayNameDto;
import com.crh.logon.utils.StringUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="order_reporting_ReportingForm"
 */
public class ReportingForm  extends ActionForm {

	private List distributionDestCdList;
	private Map json;
	private List previewData;
	private List<SelectableGeneralDisplayNameDto> carriers;
	private List<SelectableGeneralDisplayNameDto> pools;
	private Map<Long, List<SelectableGeneralDisplayNameDto>> poolCarrierMap;
    private String selectedCarrierIDs;
    private String selectedPoolIDs;
    private List<SelectableGeneralDisplayNameDto> orderStatusList;

	public List getDistributionDestCdList() {
		return distributionDestCdList;
	}
	
	public void setDistributionDestCdList(List distributionDestCdList) {
		this.distributionDestCdList = distributionDestCdList;
	}
	
	public Map getJson() {
		return json;
	}
	
	public void setJson(Map json) {
		this.json = json;
	}

	public List getPreviewData() {
		return previewData;
	}

	public void setPreviewData(List previewData) {
		this.previewData = previewData;
	}

	public List<SelectableGeneralDisplayNameDto> getCarriers() {
		return carriers;
	}

	public void setCarriers(List<SelectableGeneralDisplayNameDto> carriers) {
		this.carriers = carriers;
	}

	public List<SelectableGeneralDisplayNameDto> getPools() {
		return pools;
	}

	public void setPools(List<SelectableGeneralDisplayNameDto> pools) {
		this.pools = pools;
	}
	
	public Map<Long, List<SelectableGeneralDisplayNameDto>> getPoolCarrierMap() {
		return poolCarrierMap;
	}

	public void setPoolCarrierMap(Map<Long, List<SelectableGeneralDisplayNameDto>> poolCarrierMap) {
		this.poolCarrierMap = poolCarrierMap;
	}

	public String getSelectedCarrierIDs() {
		return this.selectedCarrierIDs;
	}

	public List<Long> getSelectedCarrierIDList() {
		if (StringUtils.empty(selectedCarrierIDs)) {
			return Collections.<Long>emptyList();
		}
		List<Long> carrierIdList = new ArrayList<Long>();
		String[] carrierIds = ((String)selectedCarrierIDs).split(",");
		for (int i=0; i<carrierIds.length; i++) {
			carrierIdList.add((Long.valueOf(carrierIds[i])));
		}
		return carrierIdList;
	}
	
    /**
     * The checkcombo sends it in an array splitted with ','.
     * @param carrierIds
     */
	public void setSelectedCarrierIDs(String selectedCarrierIDs){
		this.selectedCarrierIDs = selectedCarrierIDs;
	}

	public String getSelectedPoolIDs() {
		return selectedPoolIDs;
	}

	public void setSelectedPoolIDs(String selectedPoolIDs) {
		this.selectedPoolIDs = selectedPoolIDs;
	}
	
	public List<Long> getSelectedPoolIDList() {
		if (StringUtils.empty(selectedPoolIDs)) {
			return Collections.<Long>emptyList();
		}
		List<Long> poolIdList = new ArrayList<Long>();
		String[] poolIds = ((String)selectedPoolIDs).split(",");
		for (int i=0; i<poolIds.length; i++) {
			poolIdList.add((Long.valueOf(poolIds[i])));
		}
		return poolIdList;
	}

	public List<SelectableGeneralDisplayNameDto> getOrderStatusList() {
		return orderStatusList;
	}

	public void setOrderStatusList(List<SelectableGeneralDisplayNameDto> orderStatusList) {
		this.orderStatusList = orderStatusList;
	}

}
