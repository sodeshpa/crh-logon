/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.auftrag;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.web.utils.MessageUtils;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="order_auftrag_LiveKarteForm"
 */
public class LiveKarteForm extends ActionForm {

	private static final long serialVersionUID = -8910104051337644556L;
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LiveKarteForm.class);
	public static final String SESSION_LIVEKARTEFORM_NAME = "order_auftrag_LiveKarteForm";
	
    public static final String defaultDateString = "dd.MM.yyyy";
    public static final int DATE_TODAY_SELECTION = 0;
    public static final int DATE_TOMORROW_SELECTION = 1;
    public static final int DATE_THIS_WEEK_SELECTION = 2;
    public static final int DATE_THIS_MONTH_SELECTION = 3;
    public static final int DATE_THIS_YEAR_SELECTION = 4;
    public static final int DATE_ONE_DAY_FORWARD_SELECTION = 5;
    public static final int DATE_ONE_DAY_BACKWARD_SELECTION = 6;

	private String formAction;
	private List orderList;
	private List filtOrderList;	//filtered order list
	private String orderNumber;

	//Filters
	private List orderStatusList;
	private Map plantMap;
	private Map shPointMap;
	private Map ldPointMap;
	private Map poolMap;
	private Map carrierMap;
	private Map truckMap;
	private Map driverMap;	
	private Map sareaMap;
	private Map sofficeMap;
	private Map sgroupMap;	
	private Map materialMap;
	private Map orderNumMap;
	private Map shiptoMap;
	private Map soldtoMap;	
	
	//date search
	private String searchFromDate;
	private String searchToDate;
	private Date fromDate;
	private Date toDate;
    private int selectedDateOption;
    private boolean refreshFlag;
    
	
	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	public List getOrderStatusList() {
		return orderStatusList;
	}

	public void setOrderStatusList(List orderStatusList) {
		this.orderStatusList = orderStatusList;
	}
	
	public boolean isRefreshFlag() {
		return refreshFlag;
	}

	public void setRefreshFlag(boolean refreshFlag) {
		this.refreshFlag = refreshFlag;
	}
	
	public int getSelectedDateOption() {
		return selectedDateOption;
	}

	public void setSelectedDateOption(int selectedDateOption) {
		this.selectedDateOption = selectedDateOption;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	
	public String getSearchFromDate() {
		return searchFromDate;
	}

	public void setSearchFromDate(String searchFromDate) {
		this.searchFromDate = searchFromDate;
	}

	public String getSearchToDate() {
		return searchToDate;
	}

	public void setSearchToDate(String searchToDate) {
		this.searchToDate = searchToDate;
	}
	
	public List getFiltOrderList() {
		return filtOrderList;
	}

	public void setFiltOrderList(List filtOrderList) {
		this.filtOrderList = filtOrderList;
	}

	public Map getMaterialMap() {
		return materialMap;
	}

	public void setMaterialMap(Map materialMap) {
		this.materialMap = materialMap;
	}

	public Map getOrderNumMap() {
		return orderNumMap;
	}

	public void setOrderNumMap(Map orderNumMap) {
		this.orderNumMap = orderNumMap;
	}

	public Map getShiptoMap() {
		return shiptoMap;
	}

	public void setShiptoMap(Map shiptoMap) {
		this.shiptoMap = shiptoMap;
	}

	public Map getSoldtoMap() {
		return soldtoMap;
	}

	public void setSoldtoMap(Map soldtoMap) {
		this.soldtoMap = soldtoMap;
	}
	
	public Map getSofficeMap() {
		return sofficeMap;
	}

	public void setSofficeMap(Map sofficeMap) {
		this.sofficeMap = sofficeMap;
	}

	public Map getSgroupMap() {
		return sgroupMap;
	}

	public void setSgroupMap(Map sgroupMap) {
		this.sgroupMap = sgroupMap;
	}
	
	public Map getDriverMap() {
		return driverMap;
	}

	public void setDriverMap(Map driverMap) {
		this.driverMap = driverMap;
	}

	public Map getSareaMap() {
		return sareaMap;
	}

	public void setSareaMap(Map sareaMap) {
		this.sareaMap = sareaMap;
	}
	
	public Map getCarrierMap() {
		return carrierMap;
	}

	public void setCarrierMap(Map carrierMap) {
		this.carrierMap = carrierMap;
	}

	public Map getTruckMap() {
		return truckMap;
	}

	public void setTruckMap(Map truckMap) {
		this.truckMap = truckMap;
	}
	
	public Map getShPointMap() {
		return shPointMap;
	}

	public void setShPointMap(Map shPointMap) {
		this.shPointMap = shPointMap;
	}

	public Map getPlantMap() {
		return plantMap;
	}

	public void setPlantMap(Map plantMap) {
		this.plantMap = plantMap;
	}

	public Map getLdPointMap() {
		return ldPointMap;
	}

	public void setLdPointMap(Map ldPointMap) {
		this.ldPointMap = ldPointMap;
	}

	public Map getPoolMap() {
		return poolMap;
	}

	public void setPoolMap(Map poolMap) {
		this.poolMap = poolMap;
	}



	public boolean getDriverCheckBoxes(int index){
		// TODO (valdas Apr 28, 2009): use key instead of index
		String key = getMapKey(index, driverMap.entrySet().iterator());
		return ((LiveKarteBean) driverMap.get(key)).isChecked();
	}
	
	public void setDriverCheckBoxes(int index, boolean value){
		// TODO (valdas Apr 28, 2009): use key instead of index
		String key = getMapKey(index, driverMap.entrySet().iterator());
		((LiveKarteBean) driverMap.get(key)).setChecked(true);
	}
	
	public boolean getTruckCheckBoxes(int index){
		// TODO (valdas Apr 28, 2009): use key instead of index
		String key = getMapKey(index, truckMap.entrySet().iterator());
		return ((LiveKarteBean) truckMap.get(key)).isChecked();
	}
	
	public void setTruckCheckBoxes(int index, boolean value){
		// TODO (valdas Apr 28, 2009): use key instead of index
		String key = getMapKey(index, truckMap.entrySet().iterator());
		((LiveKarteBean) truckMap.get(key)).setChecked(true);
	}

	public boolean getSgroupCheckBoxes(int index){
		// TODO (valdas Apr 28, 2009): use key instead of index
		String key = getMapKey(index, sgroupMap.entrySet().iterator());
		return ((LiveKarteBean) sgroupMap.get(key)).isChecked();
	}
	
	public void setSgroupCheckBoxes(int index, boolean value){
		// TODO (valdas Apr 28, 2009): use key instead of index
		String key = getMapKey(index, sgroupMap.entrySet().iterator());
		((LiveKarteBean) sgroupMap.get(key)).setChecked(true);
	}
	
	public boolean getSofficeCheckBoxes(int index){
		// TODO (valdas Apr 28, 2009): use key instead of index
		String key = getMapKey(index, sofficeMap.entrySet().iterator());
		return ((LiveKarteBean) sofficeMap.get(key)).isChecked();
	}
	
	public void setSofficeCheckBoxes(int index, boolean value){
		// TODO (valdas Apr 28, 2009): use key instead of index
		String key = getMapKey(index, sofficeMap.entrySet().iterator());
		((LiveKarteBean) sofficeMap.get(key)).setChecked(true);
	}
	
	public boolean getSoldtoCheckBoxes(int index){
		// TODO (valdas Apr 28, 2009): use key instead of index
		String key = getMapKey(index, soldtoMap.entrySet().iterator());
		return ((LiveKarteBean) soldtoMap.get(key)).isChecked();
	}
	
	public void setSoldtoCheckBoxes(int index, boolean value){
		// TODO (valdas Apr 28, 2009): use key instead of index
		String key = getMapKey(index, soldtoMap.entrySet().iterator());
		((LiveKarteBean) soldtoMap.get(key)).setChecked(true);
	}
	
	public boolean getShiptoCheckBoxes(int index){
		// TODO (valdas Apr 28, 2009): use key instead of index
		String key = getMapKey(index, shiptoMap.entrySet().iterator());
		return ((LiveKarteBean) shiptoMap.get(key)).isChecked();
	}
	
	public void setShiptoCheckBoxes(int index, boolean value){
		// TODO (valdas Apr 28, 2009): use key instead of index
		String key = getMapKey(index, shiptoMap.entrySet().iterator());
		((LiveKarteBean) shiptoMap.get(key)).setChecked(true);
	}
	
	public boolean getShPointCheckBoxes(int index){
		// TODO (valdas Apr 28, 2009): use key instead of index
		String key = getMapKey(index, shPointMap.entrySet().iterator());
		return ((LiveKarteBean) shPointMap.get(key)).isChecked();
	}
	
	public void setShPointCheckBoxes(int index, boolean value){
		// TODO (valdas Apr 28, 2009): use key instead of index
		String key = getMapKey(index, shPointMap.entrySet().iterator());
		((LiveKarteBean) shPointMap.get(key)).setChecked(true);
	}
	
	public boolean getMaterialCheckBoxes(int index){
		// TODO (valdas Apr 28, 2009): use key instead of index
		String key = getMapKey(index, materialMap.entrySet().iterator());
		return ((LiveKarteBean) materialMap.get(key)).isChecked();
	}
	
	public void setMaterialCheckBoxes(int index, boolean value){
		// TODO (valdas Apr 28, 2009): use key instead of index
		String key = getMapKey(index, materialMap.entrySet().iterator());
		((LiveKarteBean) materialMap.get(key)).setChecked(true);
	}
	
	public boolean getSareaCheckBoxes(int index){
		// TODO (valdas Apr 28, 2009): use key instead of index
		String key = getMapKey(index, sareaMap.entrySet().iterator());
		return ((LiveKarteBean) sareaMap.get(key)).isChecked();
	}
	
	public void setSareaCheckBoxes(int index, boolean value){
		// TODO (valdas Apr 28, 2009): use key instead of index
		String key = getMapKey(index, sareaMap.entrySet().iterator());
		((LiveKarteBean) sareaMap.get(key)).setChecked(true);
	}	
	
	public boolean getOrderNumCheckBoxes(int index){
		// TODO (valdas Apr 28, 2009): use key instead of index
		String key = getMapKey(index, orderNumMap.entrySet().iterator());
		return ((LiveKarteBean) orderNumMap.get(key)).isChecked();
	}

	public void setOrderNumCheckBoxes(int index, boolean value){
		// TODO (valdas Apr 28, 2009): use key instead of index
		String key = getMapKey(index, orderNumMap.entrySet().iterator());
		((LiveKarteBean) orderNumMap.get(key)).setChecked(true);
	}
	
	public boolean getLdPointCheckBoxes(int index){
		// TODO (valdas Apr 28, 2009): use key instead of index
		String key = getMapKey(index, ldPointMap.entrySet().iterator());
		return ((LiveKarteBean) ldPointMap.get(key)).isChecked();
	}
	
	public void setLdPointCheckBoxes(int index, boolean value){
		// TODO (valdas Apr 28, 2009): use key instead of index
		String key = getMapKey(index, ldPointMap.entrySet().iterator());
		((LiveKarteBean) ldPointMap.get(key)).setChecked(true);
	}
	
	public boolean getPlantCheckBoxes(int index){
		// TODO (valdas Apr 28, 2009): use key instead of index
		String key = getMapKey(index, plantMap.entrySet().iterator());
		return ((LiveKarteBean) plantMap.get(key)).isChecked();
	}
	
	public void setPlantCheckBoxes(int index, boolean value){
		// TODO (valdas Apr 28, 2009): use key instead of index
		String key = getMapKey(index, plantMap.entrySet().iterator());
		((LiveKarteBean) plantMap.get(key)).setChecked(true);
	}
	
	public boolean getPoolCheckBoxes(int index){
		// TODO (valdas Apr 28, 2009): use key instead of index
		String key = getMapKey(index, poolMap.entrySet().iterator());
		return ((LiveKarteBean) poolMap.get(key)).isChecked();
	}
	
	public void setPoolCheckBoxes(int index, boolean value){
		// TODO (valdas Apr 28, 2009): use key instead of index
		String key = getMapKey(index, poolMap.entrySet().iterator());
		((LiveKarteBean) poolMap.get(key)).setChecked(true);
	}

	public boolean getCarrierCheckBoxes(int index){
		// TODO (valdas Apr 28, 2009): use key instead of index
		String key = getMapKey(index, carrierMap.entrySet().iterator());
		return ((LiveKarteBean) carrierMap.get(key)).isChecked();
	}
	
	public void setCarrierCheckBoxes(int index, boolean value){
		// TODO (valdas Apr 28, 2009): use key instead of index
		String key = getMapKey(index, carrierMap.entrySet().iterator());
		((LiveKarteBean) carrierMap.get(key)).setChecked(true);
	}

	private String getMapKey(int index, Iterator it) {
		int indexTmp = 0;
		while (it.hasNext()) {
			
		    if (indexTmp == index) {
		    	Map.Entry entry = (Map.Entry)it.next();
		    	return (String)entry.getKey();
		    }
		    else {
		    	it.next();
		    }
		    indexTmp++;
		}
		
		return "";
	}
	

	public String getFormAction() {
		return formAction;
	}

	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}

	public List getOrderList() {
		return orderList;
	}

	public void setOrderList(List orderList) {
		this.orderList = orderList;
	}

	
	/**
	 * Default constructor.
	 */
	public LiveKarteForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "LiveKarteForm");
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
	 * Initializes the form. This has the effect of clearing all the input
	 * fields on the ordermgr.web form.
	 */
	private void init() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
			//default search date is today
			searchFromDate = DateTimeUtils.getToday();
			searchToDate = searchFromDate;
			
			try {
				
				orderStatusList = new ArrayList();
				//set the real search date according to the input, but leave it untouched outside on the GUI
				fromDate = DateTimeUtils.parseDayMonthYear(searchFromDate);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(fromDate);
				//to date is one day forward's
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				calendar.add(Calendar.SECOND, 1);
				toDate = calendar.getTime();
				
				refreshFlag  = false;
			}
			catch (ParseException e) {
				LOG.error("Parse error when parsing the date search field");
			}
			
			initFlters();
		
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
	 * Initializes the lists of the form.
	 */
	public void initFlters() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initFlters");
		}
		// ]]
		try {
			orderList = new ArrayList();
			
			plantMap = new LinkedHashMap();
			shPointMap = new LinkedHashMap();
			ldPointMap = new LinkedHashMap();
			poolMap = new LinkedHashMap();
			carrierMap = new LinkedHashMap();
			truckMap = new LinkedHashMap();
			driverMap = new LinkedHashMap();
			sareaMap = new LinkedHashMap();
			sofficeMap = new LinkedHashMap();
			sgroupMap = new LinkedHashMap();
			materialMap = new LinkedHashMap();
			orderNumMap = new LinkedHashMap();
			shiptoMap = new LinkedHashMap();
			soldtoMap = new LinkedHashMap();
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
	 * Clears all the input fields on the ordermgr.web form, discards any existing
	 * search results, and resets any existing pages. Called in response to
	 * clicking the reset button on the ordermgr.web form.
	 */
	public void clear() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "clear");
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
	 * Override struts method.
	 * Note: this is not associated with or invoked by the reset button in the
	 * ordermgr.web form, but rather is called on every request before the struts action
	 * method gets control and, more importantly, before the request parameters
	 * are automatically copied into the form. The primary use is to reset
	 * checkboxes and struts multiboxes and the like for which the browser only
	 * sends the form parameters when they are set.
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// [[
		Object[][] params = {{"mapping", mapping},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "reset", params);
		}
		// ]]
		try {
			formAction = "";
			if (logDebugEnabled) {
				LOG.debug("Reset all checkboxes!");
			}
			
			resetCheckBoxes(plantMap.entrySet().iterator());
			resetCheckBoxes(shPointMap.entrySet().iterator());
			resetCheckBoxes(ldPointMap.entrySet().iterator());
			resetCheckBoxes(poolMap.entrySet().iterator());
			resetCheckBoxes(truckMap.entrySet().iterator());
			resetCheckBoxes(carrierMap.entrySet().iterator());
			resetCheckBoxes(driverMap.entrySet().iterator());
			resetCheckBoxes(sareaMap.entrySet().iterator());
			resetCheckBoxes(sofficeMap.entrySet().iterator());	
			resetCheckBoxes(sgroupMap.entrySet().iterator());
			resetCheckBoxes(soldtoMap.entrySet().iterator());
			resetCheckBoxes(shiptoMap.entrySet().iterator());
			resetCheckBoxes(materialMap.entrySet().iterator());
			resetCheckBoxes(orderNumMap.entrySet().iterator());
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	private void resetCheckBoxes(Iterator it) {
		while (it.hasNext()) {
		    Map.Entry entry = (Map.Entry)it.next();
		    ((LiveKarteBean) entry.getValue()).setChecked(false);
		}
	}

	/**
	 * Override struts method.
	 * Perform form-specific validation of input parameters.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		// [[
		ActionErrors result = null;
		Object[][] params = {{"mapping", mapping},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validate", params);
		}
		// ]]
		try {
			ActionErrors errors = super.validate(mapping, request);
			
			if (errors == null) {
				errors = new ActionErrors();
			}
			
	        //validate date input field
	        try {
	        	Date fromDate = DateTimeUtils.parseDayMonthYear(searchFromDate);
	        	Date toDate = DateTimeUtils.parseDayMonthYear(searchToDate);
	        	
	        	if (fromDate == null){
	        		searchFromDate = DateTimeUtils.getToday();
	        		fromDate = DateTimeUtils.parseDayMonthYear(searchFromDate);
	        	}
	        	if (toDate == null){
	        		searchToDate = DateTimeUtils.getToday();
	        		toDate = DateTimeUtils.parseDayMonthYear(searchToDate);
	        	}
	        	
	        	Calendar calendar = Calendar.getInstance();
	        	calendar.setTime(toDate);
	        	calendar.add(Calendar.DAY_OF_MONTH, 1);
	        	calendar.add(Calendar.SECOND, 1);
	        	toDate = calendar.getTime();					
	        	
	        	this.setFromDate(fromDate);
	        	this.setToDate(toDate);
	        }
	        catch (ParseException e) {
	        	MessageUtils.addActionMessage(request, "auftragbearbeitung.errors.datebadformat");
	        }
	        if(!fromDate.equals(toDate) && fromDate.after(toDate)){
	        	MessageUtils.addActionMessage(request, "auftragbearbeitung.errors.dateproblem");
	        }
			
			
			return result = errors;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

}
