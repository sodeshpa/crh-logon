package com.crh.logon.order.web.forms.dispo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.struts.action.ActionForm;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.beans.SelectableGeneralDisplayNameDto;
import com.crh.logon.constants.Constant;
import com.crh.logon.utils.StringUtils;

import flexjson.JSONSerializer;

/**
 * DispoExport popup called from Dispatching screen by clicking on the export scheduling button. 
 * Possible to export the scheduling data of a selected region/carrier company.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="order_dispo_DispoExportPopupForm"
 */
public class DispoExportPopupForm extends ActionForm {
	
	private static final long serialVersionUID = 123456789L;

	private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(DispoExportPopupForm.class);
	
	public static final String FORM_NAME = "order_dispo_DispoExportPopupForm";
	
	private String stringFromDate;
    private String stringToDate;
    private String exportType;
    
    private List<SelectableGeneralDisplayNameDto> carriers;
    private List<SelectableGeneralDisplayNameDto> pools;
    private Map<Long, List<SelectableGeneralDisplayNameDto>> poolCarrierMap;
    private String selectedCarrierIDs;
    private String selectedPoolIDs;
    
    public static final String GENERATION_TYPE_PDF = Constant.MIME_TYPE_PDF + "";
    public static final String GENERATION_TYPE_EXCEL = Constant.MIME_TYPE_XLS + "";
    
    
    /**
     * Default constructor.
     */
    public DispoExportPopupForm() {
		// [[
	
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "DispoForm");
		}
		// ]]
		try {
	        init();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }
    
	/**
     * Initializes the form.
     */
    private void init() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
			stringFromDate = "";
			stringToDate = "";
			exportType = "";
			carriers = new ArrayList<SelectableGeneralDisplayNameDto>();
			pools = new ArrayList<SelectableGeneralDisplayNameDto>();
			selectedCarrierIDs = null;
			selectedPoolIDs = null;
			poolCarrierMap = null;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }

	public String getStringFromDate() {
		return stringFromDate;
	}

	public void setStringFromDate(String stringFromDate) {
		this.stringFromDate = stringFromDate;
	}

	public String getStringToDate() {
		return stringToDate;
	}

	public void setStringToDate(String stringToDate) {
		this.stringToDate = stringToDate;
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

	public String getExportType() {
		return exportType;
	}

	public void setExportType(String exportType) {
		this.exportType = exportType;
	}
	
	public Map<Long, List<SelectableGeneralDisplayNameDto>> getPoolCarrierMap() {
		return poolCarrierMap;
	}

	public void setPoolCarrierMap(Map<Long, List<SelectableGeneralDisplayNameDto>> poolCarrierMap) {
		this.poolCarrierMap = poolCarrierMap;
	}

	public String getJsonCarrierList(){
		return new JSONSerializer().
		include("id").
		include("name").
		include("selected").
		exclude("*").serialize(getCarriers());
	}
	
	public String getJsonPoolList(){
		return new JSONSerializer().
		include("id").
		include("name").
		include("selected").
		exclude("*").serialize(getPools());
	}

}
