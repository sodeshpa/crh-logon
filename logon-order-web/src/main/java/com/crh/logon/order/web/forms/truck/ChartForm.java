/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.truck;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.crh.logon.web.utils.UserProvider;
import org.apache.struts.action.ActionForm;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.order.web.utils.chart.ChartUtils;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.BestellungChartBean;
import com.crh.logon.web.utils.CheckboxDropdownEntity;
import com.crh.logon.web.utils.DropdownEntity;

/**
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="order_ChartForm"
 */
public class ChartForm extends ActionForm {
	
    private static final long serialVersionUID = -8910104051337644556L;
    
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ChartForm.class);
    
    public static final String FORM_NAME = "order_ChartForm";

    private BestellungChartBean chartBean;    
	private Map json;
	
	/** Indicates, that the form is initialized already */
	private boolean initialized = false;
	
	private List<CheckboxDropdownEntity> poolList;
	private List<CheckboxDropdownEntity> truckTypeList;
	private List<CheckboxDropdownEntity> transCatList;
	private List<CheckboxDropdownEntity> deliveryTypeList;
	private List<DropdownEntity> productTypeList;
	private List<CheckboxDropdownEntity> wagonTrspCatList;
	private List<CheckboxDropdownEntity> wagonTypeList;
	private List<DropdownEntity> plantList;
	private List<DropdownEntity> companycodeList;
	
	/**
	 * Input fields on the form
	 */
	private String poolIds;
	private String carrierIds;
	private String truckTypes;
	private String deliveryTypes;
	private String transCat;
	private String plant;
	private String shippingPoints;
	private String productTypes;
	private String wagonTrspCat;
	private String wagonType;
	private String chartType;
	private String selectedDayDat;
	private String companycode;
	private String selectedShippingPoint;
	
	/**
	 * Chart parameters
	 */	
	private String filterPage;
	private String imgHeight;
	private String imgWidth;
	private String checkboxWidth;
	
	/**
	 * Clear the form ...
	 */
	public void clear() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "clear");
		}
		// ]]
		try {
			this.chartBean = null;
			this.json = null;
			this.poolList = null;
			this.truckTypeList = null;
			this.transCatList = null;
			this.deliveryTypeList = null;
			this.productTypeList = null;
			this.plantList = null;
			this.poolIds = null;
			this.carrierIds = null;
			this.truckTypes = null;
			this.deliveryTypes = null;
			this.transCat = null;
			this.plant = null;
			this.shippingPoints = null;
			this.productTypes = null;
			this.wagonTrspCatList = null;
			this.wagonTrspCat = null;
			this.wagonType = null;
			this.chartType = null;
			this.companycode = null;
			this.companycodeList = null;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public void init() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
			clear();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public void initPreselectedValues() {
		setTruckTypes(ChartUtils.getSelectedValues(this.truckTypeList));
		setDeliveryTypes(ChartUtils.getSelectedValues(this.deliveryTypeList));
	}

	/**
	 * Return the selected Pools in {@link java.util.ArrayList<Long>}
	 * 
	 * @return
	 */
	public List<Long> getSelectedPoolIds() {
		// [[
		List<Long> result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getSelectedPoolIds");
		}
		// ]]
		try {
			return result = StringUtils.stringToArrayList(this.poolIds);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Return the selected executing carriers in {@link java.util.ArrayList<Long>}
	 * 
	 * @return
	 */
	public List<Long> getSelectedCarrierIds() {
		// [[
		List<Long> result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getSelectedCarrierIds");
		}
		// ]]
		try {
			return result = StringUtils.stringToArrayList(this.carrierIds);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public List<Long> getSelectedTruckTypes() {
		// [[
		LOG.debug("getSelectedTruckTypes()");
		// ]]
		return StringUtils.stringToArrayList(this.truckTypes);
	}
	
	public List<Integer> getSelectedDeliveryTypes() {
		// [[
		LOG.debug("getSelectedDeliveryTypes()");
		// ]]
		return StringUtils.stringToIntegerArrayList(this.deliveryTypes);
	}
	
	public int getSelectedTransCat() {
		// [[
		LOG.debug("getSelectedTransCat()");
		// ]]
		return Integer.valueOf(this.transCat);
	}
	
	public String getSelectedPlant() {
		// [[
		LOG.debug("getSelectedPlant()");
		// ]]
		if (StringUtils.empty(this.plant)) {
			return null;
		}
		return this.plant;
	}
	
	public List<Long> getSelectedShippingPoints() {
		// [[
		LOG.debug("getSelectedShippingPoints()");
		// ]]
		return StringUtils.stringToArrayList(this.shippingPoints);
	}
	
	public List<Integer> getSelectedproductTypes() {
		// [[
		LOG.debug("getSelectedproductTypes()");
		// ]]
		return StringUtils.stringToIntegerArrayList(this.productTypes);
	}
	
	public List<Long> getSelectedWagonTrspCat() {
		// [[
		LOG.debug("getSelectedWagonTrspCat()");
		// ]]
		return StringUtils.stringToArrayList(this.wagonTrspCat);
	}
	
	public List<Long> getSelectedWagonType() {
		// [[
		LOG.debug("getSelectedWagonTrspCat()");
		// ]]
		return StringUtils.stringToArrayList(this.wagonType);
	}
	
	public int getSelectedChartType() {
		// [[
		LOG.debug("getSelectedTransCat()");
		// ]]
		return Integer.valueOf(this.chartType);
	}
	
	public String getPoolIds() {
		return poolIds;
	}
	
	public void setPoolIds(String poolIds) {
		this.poolIds = poolIds;
	}

	public Map getJson() {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("getJson(): '" + json + "'");
		}
		// ]]
		return json;
	}

	public void setJson(Map json) {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("setJson(): '" + json + "'");
		}
		// ]]
		this.json = json;
	}

	public BestellungChartBean getChartBean() {
		return chartBean;
	}

	public void setChartBean(BestellungChartBean chartBean) {
		this.chartBean = chartBean;
	}

	public String getCarrierIds() {
		return carrierIds;
	}

	public void setCarrierIds(String carrierIds) {
		this.carrierIds = carrierIds;
	}

	public List<CheckboxDropdownEntity> getPoolList() {
		return poolList;
	}

	public void setPoolList(List<CheckboxDropdownEntity> poolList) {
		this.poolList = poolList;
	}

	public List<CheckboxDropdownEntity> getTruckTypeList() {
		return truckTypeList;
	}

	public void setTruckTypeList(List<CheckboxDropdownEntity> truckTypeList) {
		this.truckTypeList = truckTypeList;
	}

	public List<CheckboxDropdownEntity> getTransCatList() {
		return transCatList;
	}

	public void setTransCatList(List<CheckboxDropdownEntity> transCatList) {
		this.transCatList = transCatList;
	}

	public List<CheckboxDropdownEntity> getDeliveryTypeList() {
		return deliveryTypeList;
	}

	public void setDeliveryTypeList(List<CheckboxDropdownEntity> deliveryTypeList) {
		this.deliveryTypeList = deliveryTypeList;
	}

	public String getTransCat() {
		return transCat;
	}

	public void setTransCat(String transCat) {
		this.transCat = transCat;
	}

	public String getFilterPage() {
		return filterPage;
	}

	public void setFilterPage(String filterPage) {
		this.filterPage = filterPage;
	}

	public String getImgHeight() {
		return imgHeight;
	}

	public void setImgHeight(String imgHeight) {
		this.imgHeight = imgHeight;
	}

	public String getImgWidth() {
		return imgWidth;
	}

	public void setImgWidth(String imgWidth) {
		this.imgWidth = imgWidth;
	}

	public String getTruckTypes() {
		return truckTypes;
	}

	public void setTruckTypes(String truckTypes) {
		this.truckTypes = truckTypes;
	}

	public String getDeliveryTypes() {
		return deliveryTypes;
	}

	public void setDeliveryTypes(String deliveryTypes) {
		this.deliveryTypes = deliveryTypes;
	}

	public String getPlant() {
		return plant;
	}

	public void setPlant(String plant) {
		this.plant = plant;
	}

	public String getShippingPoints() {
		return shippingPoints;
	}

	public void setShippingPoints(String shippingPoints) {
		this.shippingPoints = shippingPoints;
	}

	public String getProductTypes() {
		return productTypes;
	}

	public void setProductTypes(String productTypes) {
		this.productTypes = productTypes;
	}

	public String getWagonTrspCat() {
		return wagonTrspCat;
	}

	public void setWagonTrspCat(String wagonTrspCat) {
		this.wagonTrspCat = wagonTrspCat;
	}

	public List<DropdownEntity> getProductTypeList() {
		return productTypeList;
	}

	public void setProductTypeList(List<DropdownEntity> productTypeList) {
		this.productTypeList = productTypeList;
	}

	public List<DropdownEntity> getPlantList() {
		return plantList;
	}

	public void setPlantList(List<DropdownEntity> plantList) {
		this.plantList = plantList;
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public String getWagonType() {
		return wagonType;
	}

	public void setWagonType(String wagonType) {
		this.wagonType = wagonType;
	}

	public List<CheckboxDropdownEntity> getWagonTrspCatList() {
		return wagonTrspCatList;
	}

	public void setWagonTrspCatList(List<CheckboxDropdownEntity> wagonTrspCatList) {
		this.wagonTrspCatList = wagonTrspCatList;
	}

	public String getCheckboxWidth() {
		return checkboxWidth;
	}

	public void setCheckboxWidth(String checkboxWidth) {
		this.checkboxWidth = checkboxWidth;
	}

	public List<CheckboxDropdownEntity> getWagonTypeList() {
		return wagonTypeList;
	}

	public void setWagonTypeList(List<CheckboxDropdownEntity> wagonTypeList) {
		this.wagonTypeList = wagonTypeList;
	}

	public List<DropdownEntity> getCompanycodeList() {
		return companycodeList;
	}

	public void setCompanycodeList(List<DropdownEntity> companycodeList) {
		this.companycodeList = companycodeList;
	}

	public String getCompanycode() {
		return companycode;
	}

	public void setCompanycode(String companycode) {
		this.companycode = companycode;
	}

	public String getSelectedDayDat() {
		// [[
		String result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getSelectedDayDat");
		}
		// ]]
		try {
			return result = selectedDayDat;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public void setSelectedDayDat(Date selectedDayDat) {
		// [[
		Object[][] params = {{"selectedDayDat", selectedDayDat}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setSelectedDayDat", params);
		}
		// ]]
		try {
            this.selectedDayDat = UserProvider.getTimezone().fmtAsDayMonthYear(selectedDayDat);
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
	 * @return the selectedShippingPoint
	 */
	public String getSelectedShippingPoint() {
		return selectedShippingPoint;
	}

	/**
	 * @param selectedShippingPoint the selectedShippingPoint to set
	 */
	public void setSelectedShippingPoint(String selectedShippingPoint) {
		this.selectedShippingPoint = selectedShippingPoint;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
    
}
