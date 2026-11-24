/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.auftrag;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.OrderChangeConstants;
import com.crh.logon.business.services.utils.OrderStatistic;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.LdpointMatRestrEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.views.OrdItemAssignmtLoadEntity;
import com.crh.logon.persistence.entities.views.ShippBimodalRoadRailEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.DropdownEntity;
import com.crh.logon.web.utils.MessageUtils;
import com.crh.logon.web.utils.UserProvider;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 * Form of AuftragBearbeitungTile.jsp. The page is designed to edit tara nad brutto messung, container
 * of orders.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="order_auftrag_AuftragBearbeitungForm"
 */
public class AuftragBearbeitungForm extends ActionForm {

	private static final long serialVersionUID = -8910104051337644556L;
    
    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(AuftragBearbeitungForm.class);
    
    public static final String EMPTY_SELECTION = "";
    public static final int DATE_TODAY_SELECTION = 0;
    public static final int DATE_TOMORROW_SELECTION = 1;
    public static final int DATE_THIS_WEEK_SELECTION = 2;
    public static final int DATE_THIS_MONTH_SELECTION = 3;
    public static final int DATE_THIS_YEAR_SELECTION = 4;
    public static final int DATE_ONE_DAY_FORWARD_SELECTION = 5;
    public static final int DATE_ONE_DAY_BACKWARD_SELECTION = 6;
    public static final int DATE_CUSTOM_SELECTION = 7;
    
    public static final String FORM_NAME = "order_auftrag_AuftragBearbeitungForm";
    
	public static final String REQUEST_CONTAINERLIST = "containers";
	public static final String REQUEST_ORDER_ITEM_ID = "orderItemId";
	public static final String REQUEST_CONTAINER_ID = "containerId";
    
    public static final String TRANSPORT_TYPE_BULK_POWDER = OrderChangeConstants.SHIPPING_UI_TRANSPORTATION_CHOICE_BULK_POWDER;
    public static final String TRANSPORT_TYPE_BULK_DUMPED = OrderChangeConstants.SHIPPING_UI_TRANSPORTATION_CHOICE_BULK_DUMPED;
    public static final String TRANSPORT_TYPE_BAG = OrderChangeConstants.SHIPPING_UI_TRANSPORTATION_CHOICE_BAG;
  
    public OrderStatistic stat = new OrderStatistic();
    
    //selectable on screen
    private List selectableShippingPoints = new ArrayList();
    private List<DropdownEntity> selectablePlants = new ArrayList();
    private List selectableStatusList = new ArrayList();
    private int selectedDateOption;
    
    //used in loading point change popup
    private List<LdpointMatRestrEntity> selectableOrderLoadingpointList = new ArrayList<LdpointMatRestrEntity>();
    
    private String selectedOrderBy = OrdItemAssignmtLoadEntity.ATTR_LOADING_MIN_PLANNED_START;
    private boolean sortAsc = true;
    
    private List<OrdItemAssignmtLoadEntity> orderList;
	private List<OrdItemAssignmentLoadBean> orderBeans;

    private List<Long> assignmentIdList = new ArrayList<Long>();
    //contains the OrdItemAssignmtLoadEntity ids and the available loading points for the represented order
    private Map<Long, List<LdpointMatRestrEntity>> orderLoadingPointListMapping = new HashMap<Long, List<LdpointMatRestrEntity>>();
    
    private Map<Long, Date> truckNextOrderMapping = new HashMap<Long, Date>();
    private Map<Long, ShippBimodalRoadRailEntity> bimodalRoadAndRelatedRailInfoMap = new HashMap<Long, ShippBimodalRoadRailEntity>();

    private String searchFromDate = "";
    private String searchToDate = "";
    
	private Date toDate = null;
	private Date fromDate = null;    

    // input from the user (dropdown content)
    private String selectedPlantCd = "";
    private String selectedShippingPointId = "";
    private String selectedTransportCat = TRANSPORT_TYPE_BULK_POWDER;
    private long[] selectedStatus;
    private long[] selStatus;

    private String searchtext = "";
    private String searchOrderNo = "";
    
    private String inputTaraMeasureValue;
    private String inputNettoMeasureValue;
    //not editable, but must be considered in validation
    private Long inputLoadingPointId;
    
    private Long inputOrderId;
    private Long inputLoadId;
    private Long inputDelivNoteId;
    private String inputContainerIdSelect;
    
    private String formAction;

	private TimeZone shippingpointTZ;
    
	public long[] getSelectedStatus() {
		return selectedStatus;
	}

	public void setSelectedStatus(long[] selectedStatus) {
		this.selectedStatus = selectedStatus;
	}

	/**
     * Default constructor.
     */
    public AuftragBearbeitungForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "AuftragBearbeitungForm");
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
			// default search date is today
			fromDate = new Date();
			toDate = new Date();
			searchFromDate = "";
			searchToDate = "";
			selectedPlantCd="";
			selectedShippingPointId = "";
			selectedTransportCat = "";
			formAction = "";
			searchtext = "";
			searchOrderNo = "";
			selectablePlants = new ArrayList<DropdownEntity>();
			orderLoadingPointListMapping = new HashMap<Long, List<LdpointMatRestrEntity>>();
			truckNextOrderMapping = new HashMap<Long, Date>();
			bimodalRoadAndRelatedRailInfoMap = new HashMap<Long, ShippBimodalRoadRailEntity>();
			selectableShippingPoints = new ArrayList();
			selectableOrderLoadingpointList = new ArrayList<LdpointMatRestrEntity>();
			assignmentIdList = new ArrayList<Long>();
			/*
			 * FIXME DM (cosmin Aug 10, 2009):OrdItemAssignmtLoad needs to be reimplemented
			selectedOrderBy = OrdItemAssignmtLoadEntity.ATTR_DELIVERY_TO_DAT;
			*/
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
     * Clears all the input fields on the form. 
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
     * Called on every request before the struts action
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
			selectedPlantCd="";
			selectedShippingPointId = "";
			selectedTransportCat = "";
			formAction = "";		
			inputNettoMeasureValue = "";
			inputTaraMeasureValue = "";
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
	 * Validates if the the request contains any errors when the measures are invalid.
	 * 
	 * @param request
	 * @param logonPackageTypeCd 
	 * @return false if the request contains any errors / warnings => 
	 * this can happen if the taraMeasure or the nettoMeasure values are out of the scope of the corresponding DB field or the given values are <b>zero</b> or negative. 
	 */
	public boolean validateSaveInput(HttpServletRequest request, Long logonPackageTypeCd, String loadedQuantityString) {

		boolean success = true;

		// Check if the values are out of the DB field length 
		if (StringUtils.notEmpty(inputTaraMeasureValue)){
			validateMeasures(request, inputTaraMeasureValue, logonPackageTypeCd);
			ActionSupport.checkFieldMaxValue(request, inputTaraMeasureValue, LoadEntity.DBSIZE_TARE_WEIGHT_T, LoadEntity.DBSIZE_PRECISION_TARE_WEIGHT_T, "auftragbearbeitung.texte.ordercolumn.tarameasure");
		}
		if (StringUtils.notEmpty(inputNettoMeasureValue)){
			validateMeasures(request, inputNettoMeasureValue, logonPackageTypeCd);
			ActionSupport.checkFieldMaxValue(request, inputNettoMeasureValue, LoadEntity.DBSIZE_NET_QUANTITY, LoadEntity.DBSIZE_PRECISION_NET_QUANTITY, "auftragbearbeitung.texte.ordercolumn.nettoweight");
		}
		if(StringUtils.notEmpty(loadedQuantityString)){
			validateMeasures(request, loadedQuantityString, logonPackageTypeCd);
			ActionSupport.checkFieldMaxValue(request, inputTaraMeasureValue, LoadEntity.DBSIZE_NET_QUANTITY, LoadEntity.DBSIZE_PRECISION_TARE_WEIGHT_T, "auftragbearbeitung.texte.ordercolumn.nettoweight");
		}
		
		if ( MessageUtils.getActionWarnings(request) != null && !MessageUtils.getActionWarnings(request).isEmpty()) {
			success = false;
		}
		else{
			success = true;
		}

		return success;

	}

	/**
	 * Will add an action warning in case the measured value is less then or equal to zero
	 */
	private void validateMeasures(HttpServletRequest request, String measureValueStr, Long logonPackageTypeCd) {
		try {
			BigDecimal measureValue = new BigDecimal(measureValueStr);
			if(logonPackageTypeCd != null && logonPackageTypeCd.equals(Constant.PACKAGE_TYPE_BBAG)){
				if ( measureValue.compareTo(BigDecimal.ZERO) < 0 ) {
					MessageUtils.addActionWarning(request, "auftragbearbeitung.errors.loadmeasuremustnotbenegative");
				}
			}
			else{
				if ( measureValue.compareTo(BigDecimal.ZERO) <= 0 ) {
					MessageUtils.addActionWarning(request, "auftragbearbeitung.errors.loadmeasureshouldbepositive");
				}				
			}
		} catch (NumberFormatException formatException) {
			// We don't add any warnings here because the validation is duplicated in the ActionSupport.checkFieldMaxValue method
		}
	}

	/**
	 * @param request
	 */
	public boolean validateDateSetting(HttpServletRequest request) {
		boolean success = true;
		try {
			//--- TO date
			Date toDate = shippingpointTZ.parseDayMonthYear(searchToDate);
			if (toDate == null) {
				toDate = shippingpointTZ.today();
			}

			Calendar calendar = shippingpointTZ.asCalendar();
			calendar.setTime(toDate);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			calendar.add(Calendar.SECOND, 1);
			toDate = calendar.getTime();

			this.searchToDate = shippingpointTZ.fmtAsDayMonthYear(toDate);
			setToDate(toDate);

			//--- FROM date
			Date fromDate = shippingpointTZ.parseDayMonthYear(searchFromDate);
			if (fromDate == null) {
				fromDate = shippingpointTZ.today();
			}

			this.searchFromDate = shippingpointTZ.fmtAsDayMonthYear(fromDate);
			setFromDate(fromDate);
		}
		catch (ParseException e) {
			MessageUtils.addActionWarning(request, "auftragbearbeitung.errors.datebadformat");
			success = false;	        		        	
		}
		if(!fromDate.equals(toDate) && fromDate.after(toDate)){
			MessageUtils.addActionWarning(request, "auftragbearbeitung.errors.dateproblem");
			success = false;	        	
		}
		return success;
	}

	/**
	 * Rounds up from 5 to 10. Below to 0.
	 * @return
	 */
	private double round(Double inputValue) {
		double returnVal = inputValue.doubleValue();
		double remainder = inputValue % 10;
		if (remainder != 0 && remainder >=5 ){
			returnVal = inputValue.doubleValue() + (10 - remainder);
		}
		else if (remainder != 0 && remainder < 5 ) {
			returnVal = inputValue.doubleValue()- remainder;
		}
		return returnVal;
	}

	public String getSearchtext() {
		return searchtext;
	}

	public void setSearchtext(String searchtext) {
		this.searchtext = searchtext;
	}
	
	public String getSearchOrderNo() {
		return searchOrderNo;
	}

	public void setSearchOrderNo(String searchOrderNo) {
		this.searchOrderNo = searchOrderNo;
	}

	public List getSelectableShippingPoints() {
		return selectableShippingPoints;
	}

	public void setSelectableShippingPoints(List selectableShippingPoints) {
		this.selectableShippingPoints = selectableShippingPoints;
	}

	public List<DropdownEntity> getSelectablePlants() {
		return selectablePlants;
	}

	public void setSelectablePlants(List<DropdownEntity> selectablePlants) {
		this.selectablePlants = selectablePlants;
	}

	public String getSelectedPlantCd() {
		return selectedPlantCd;
	}

	public void setSelectedPlantCd(String selectedPlantCd) {
		this.selectedPlantCd = selectedPlantCd;
	}

	public String getSelectedShippingPointId() {
		return selectedShippingPointId;
	}

	public void setSelectedShippingPointId(String selectedShippingPointId) {
		this.selectedShippingPointId = selectedShippingPointId;
	}

	public String getFormAction() {
		return formAction;
	}

	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}

	public List getSelectableStatusList() {
		return selectableStatusList;
	}

	public void setSelectableStatusList(List selectableStatusList) {
		this.selectableStatusList = selectableStatusList;
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

	public int getSelectedDateOption() {
		return selectedDateOption;
	}

	public void setSelectedDateOption(int selectedDateOption) {
		this.selectedDateOption = selectedDateOption;
	}

	public long[] getSelStatus() {
		return selStatus;
	}

	public void setSelStatus(long[] selStatus) {
		this.selStatus = selStatus;
	}

	public List<OrdItemAssignmtLoadEntity> getOrderList() {
		return orderList;
	}

	public List<OrdItemAssignmentLoadBean> getOrderBeans() {
		return orderBeans;
	}

	public void setOrderList(List<OrdItemAssignmtLoadEntity> orderEntityList, Map updatedPlannedTimes) {
		if (orderEntityList != null && !orderEntityList.isEmpty() && updatedPlannedTimes != null && !updatedPlannedTimes.isEmpty()) {
			Iterator<OrdItemAssignmtLoadEntity> it = orderEntityList.iterator();
			while (it.hasNext()) {
				OrdItemAssignmtLoadEntity object = it.next();
				Date info = (Date)updatedPlannedTimes.get(object.getOrderId());
				if (info != null) {					
					object.setLoadingRealStart(info);
				}
			}
		}

		this.orderList = orderEntityList;

		//--- Copy over to beans for date/time conversions ---//
		this.orderBeans = new ArrayList<OrdItemAssignmentLoadBean>();
		if (orderEntityList != null && !orderEntityList.isEmpty()) {
			TimeZone userTz = UserProvider.getTimezone();
			for (OrdItemAssignmtLoadEntity entity : orderEntityList) {
				String loadingPlannedStart = shippingpointTZ.fmtAsDayMonthYearHourMinuteWithOffset(
						entity.getLoadingPlannedStart(),
						userTz);

				String loadingRealStart = shippingpointTZ.fmtAsDayMonthYearHourMinuteWithOffset(
						entity.getLoadingRealStart(),
						userTz);

				OrdItemAssignmentLoadBean bean = new OrdItemAssignmentLoadBean(
						entity, loadingPlannedStart, loadingRealStart);
				this.orderBeans.add(bean);
			}
		}
	}

	public OrderStatistic getStat() {
		return stat;
	}

	public void setStat(OrderStatistic stat) {
		this.stat = stat;
	}


	public String getInputTaraMeasureValue() {
		return inputTaraMeasureValue;
	}

	public void setInputTaraMeasureValue(String inputTaraMeasureValue) {
		this.inputTaraMeasureValue = replaceComma(inputTaraMeasureValue);
	}

	public String getInputNettoMeasureValue() {
		return inputNettoMeasureValue;
	}

	public void setInputNettoMeasureValue(String inputNettoMeasureValue) {
		this.inputNettoMeasureValue = replaceComma(inputNettoMeasureValue);
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public String getSelectedOrderBy() {
		return selectedOrderBy;
	}

	public void setSelectedOrderBy(String selectedOrderBy) {
		if (StringUtils.empty(selectedOrderBy)) {
			selectedOrderBy = OrdItemAssignmtLoadEntity.ATTR_LOADING_MIN_PLANNED_START; 
		}
		else{			
			this.selectedOrderBy = selectedOrderBy;
		}
	}

	public boolean isSortAsc() {
		return sortAsc;
	}

	public void setSortAsc(boolean sortAsc) {
		this.sortAsc = sortAsc;
	}

	public Map<Long, List<LdpointMatRestrEntity>> getOrderLoadingPointListMapping() {
		return orderLoadingPointListMapping;
	}

	public void setOrderLoadingPointListMapping(Map<Long, List<LdpointMatRestrEntity>> orderLoadingPointListMapping) {
		this.orderLoadingPointListMapping = orderLoadingPointListMapping;
	}

	public Long getInputLoadingPointId() {
		return inputLoadingPointId;
	}

	public void setInputLoadingPointId(Long inputLoadingPointId) {
		this.inputLoadingPointId = inputLoadingPointId;
	}

	public Long getInputOrderId() {
		return inputOrderId;
	}

	public boolean isLoadingpointUnset() {
		return getInputLoadingPointId() != null ? getInputLoadingPointId().equals(0L) : true;
	}

	public void setInputOrderId(Long inputOrderId) {
		this.inputOrderId = inputOrderId;
	}

	public Long getInputDelivNoteId() {
		return inputDelivNoteId;
	}

	public void setInputDelivNoteId(Long inputDelivNoteId) {
		this.inputDelivNoteId = inputDelivNoteId;
	}

	public Map<Long, Date> getTruckNextOrderMapping() {
		return truckNextOrderMapping;
	}

	public void setTruckNextOrderMapping(Map<Long, Date> truckNextOrderMapping) {
		this.truckNextOrderMapping = truckNextOrderMapping;
	}

	public List<LdpointMatRestrEntity> getSelectableOrderLoadingpointList() {
		return selectableOrderLoadingpointList;
	}

	public void setSelectableOrderLoadingpointList(List<LdpointMatRestrEntity> selectableOrderLoadingpointList) {
		this.selectableOrderLoadingpointList = selectableOrderLoadingpointList;
	}

	public String getSelectedTransportCat() {
		return selectedTransportCat;
	}

	public void setSelectedTransportCat(String selectedTransportCat) {
		this.selectedTransportCat = selectedTransportCat;
	}

	public Long getInputLoadId() {
		return inputLoadId;
	}

	public void setInputLoadId(Long inputLoadId) {
		this.inputLoadId = inputLoadId;
	}

	private String replaceComma(String str) {
		if (str != null ) {		
			str = str.replaceAll(",", ".");
		}
		return str;
	}

	public Map<Long, ShippBimodalRoadRailEntity> getBimodalRoadAndRelatedRailInfoMap() {
		return bimodalRoadAndRelatedRailInfoMap;
	}

	public void setBimodalRoadAndRelatedRailInfoMap(Map<Long, ShippBimodalRoadRailEntity> bimodalRoadAndRelatedRailInfoMap) {
		this.bimodalRoadAndRelatedRailInfoMap = bimodalRoadAndRelatedRailInfoMap;
	}

	public String getInputContainerIdSelect() {
		return inputContainerIdSelect;
	}

	public void setInputContainerIdSelect(String inputContainerIdSelect) {
		this.inputContainerIdSelect = inputContainerIdSelect;
	}

	public List<Long> getAssignmentIdList() {
		return assignmentIdList;
	}

	public void setAssignmentIdList(List<Long> assignmentIdList) {
		if (assignmentIdList!= null && assignmentIdList.contains(null)) {
			int index = assignmentIdList.indexOf(null);
			assignmentIdList.remove(index);
		}
		this.assignmentIdList = assignmentIdList;
	}

	public TimeZone getShippingpointTZ() {
		return shippingpointTZ;
	}

	public void setShippingpointTZ(TimeZone shippingpointTZ) {
		this.shippingpointTZ = shippingpointTZ;
	}
}
