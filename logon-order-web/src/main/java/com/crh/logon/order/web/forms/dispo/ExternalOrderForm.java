/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.dispo;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.web.utils.UserProvider;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.utils.time.DateTimeUtils;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="order_dispo_ExternalOrderForm"
 */
public class ExternalOrderForm extends ActionForm {

    private static final long serialVersionUID = -8910104051337644556L;
    private static final int DELIVERY_FROM_HOUR = 8;
    private static final int DELIVERY_TO_HOUR = 18;

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(ExternalOrderForm.class);
    
    /* lists & dropdowns */
    private List poolsForExternal;
    private List carriersForExternal;
    private List materialsForExternal;
    private List uomListForExternal;
    private List siteListForExternal;
        
    /* user data */ 
    /* list of the IDs*/
    private Long externalOrderId;
    private Long externalOrderNumber;
    private Long selectedPoolForExternal;
    private Long selectedCarrierForExternal;
    private Long selectedMaterialForExternal;
    private Long selectedShippingPointForExternal;
    private Long selectedShiptoForExternal;
    private String orderQuantityForExtarnal;
    private String loadedQuantityForExtarnal;
    private String deliveredQuantityForExtarnal;
    
    private String pupupTitle;
    
    private String uomCdForExternal;
    private String uomCdLoadedForExternal;
        
    private String deliveryFromDate;
    private String deliveryToDate;
    private String deliveryFromTime;
    private String deliveryToTime;

    private Double shippingPointCoordLat;
    private Double shippingPointCoordLon;
	private Double shiptoCoordLat;
    private Double shiptoCoordLon;
    private String noteTruckDriver;
    
    private String sapId;
        
    private boolean orderStatusTransmittedToDriver;
    private boolean orderStatusShipping;
    private boolean editOrder;
    
    private Date dispoDate;
    
	public List getPoolsForExternal() {
		return poolsForExternal;
	}

	public void setPoolsForExternal(List poolsForExternal) {
		this.poolsForExternal = poolsForExternal;
	}

	public List getCarriersForExternal() {
		return carriersForExternal;
	}

	public void setCarriersForExternal(List carriersForExternal) {
		this.carriersForExternal = carriersForExternal;
	}

	public Long getSelectedPoolForExternal() {
		return selectedPoolForExternal;
	}

	public void setSelectedPoolForExternal(Long selectedPoolForExternal) {
		this.selectedPoolForExternal = selectedPoolForExternal;
	}

	public Long getSelectedCarrierForExternal() {
		return selectedCarrierForExternal;
	}

	public void setSelectedCarrierForExternal(Long selectedCarrierForExternal) {
		this.selectedCarrierForExternal = selectedCarrierForExternal;
	}
	
	public List getMaterialsForExternal() {
		return materialsForExternal;
	}

	public void setMaterialsForExternal(List materialsForExternal) {
		this.materialsForExternal = materialsForExternal;
	}
	
	public Long getSelectedMaterialForExternal() {
		return selectedMaterialForExternal;
	}

	public void setSelectedMaterialForExternal(Long selectedMaterialForExternal) {
		this.selectedMaterialForExternal = selectedMaterialForExternal;
	}

	public List getUomListForExternal() {
		return uomListForExternal;
	}

	public void setUomListForExternal(List uomListForExternal) {
		this.uomListForExternal = uomListForExternal;
	}

	public String getUomCdForExternal() {
		return uomCdForExternal;
	}

	public void setUomCdForExternal(String uomCdForExternal) {
		this.uomCdForExternal = uomCdForExternal;
	}

	public List getSiteListForExternal() {
		return siteListForExternal;
	}

	public void setSiteListForExternal(List siteListForExternal) {
		this.siteListForExternal = siteListForExternal;
	}

	public Long getSelectedShippingPointForExternal() {
		return selectedShippingPointForExternal;
	}

	public void setSelectedShippingPointForExternal(Long selectedShippingPointForExternal) {
		this.selectedShippingPointForExternal = selectedShippingPointForExternal;
	}

	public Long getSelectedShiptoForExternal() {
		return selectedShiptoForExternal;
	}

	public void setSelectedShiptoForExternal(Long selectedShiptoForExternal) {
		this.selectedShiptoForExternal = selectedShiptoForExternal;
	}
	
	public boolean isOrderStatusTransmittedToDriver() {
		return orderStatusTransmittedToDriver;
	}

	public void setOrderStatusTransmittedToDriver(boolean orderStatusTransmittedToDriver) {
		this.orderStatusTransmittedToDriver = orderStatusTransmittedToDriver;
	}
	
	public boolean isOrderStatusShipping() {
		return orderStatusShipping;
	}

	public void setOrderStatusShipping(boolean orderStatusShipping) {
		this.orderStatusShipping = orderStatusShipping;
	}

	public String getDeliveryFromDate() {
		return deliveryFromDate;
	}

	public Date getDeliveryFrom() {
		return getDeliveryDate(deliveryFromDate, deliveryFromTime);
	}

	public Date getDeliveryTo() {
		return getDeliveryDate(deliveryToDate, deliveryToTime);
	}

	private Date getDeliveryDate(String mmddyyy, String hhmm) {
		try {
			TimeZone userTz = UserProvider.getTimezone();
			return userTz.parseDayMonthYear4HourMinute(mmddyyy, hhmm);
		}
		catch (ParseException exc) {
			LOG.error(String.format("Parsing '%s %s' failed", mmddyyy, hhmm), exc);
			return null;
		}
	}

	public void setDeliveryFromDate(String deliveryFromDate) {
		this.deliveryFromDate = deliveryFromDate;
	}

	public String getDeliveryToDate() {
		return deliveryToDate;
	}

	public void setDeliveryToDate(String deliveryToDate) {
		this.deliveryToDate = deliveryToDate;
	}

	public String getDeliveryFromTime() {
		return deliveryFromTime;
	}

	public void setDeliveryFromTime(String deliveryFromTime) {
		this.deliveryFromTime = deliveryFromTime;
	}

	public String getDeliveryToTime() {
		return deliveryToTime;
	}

	public void setDeliveryToTime(String deliveryToTime) {
		this.deliveryToTime = deliveryToTime;
	}

	public String getSapId() {
		return sapId;
	}

	public void setSapId(String sapId) {
		this.sapId = sapId;
	}

	public String getOrderQuantityForExtarnal() {
		return orderQuantityForExtarnal;
	}

	public void setOrderQuantityForExtarnal(String orderQuantityForExtarnal) {
		this.orderQuantityForExtarnal = orderQuantityForExtarnal;
	}

	public String getLoadedQuantityForExtarnal() {
		return loadedQuantityForExtarnal;
	}

	public void setLoadedQuantityForExtarnal(String loadedQuantityForExtarnal) {
		this.loadedQuantityForExtarnal = loadedQuantityForExtarnal;
	}

	public String getDeliveredQuantityForExtarnal() {
		return deliveredQuantityForExtarnal;
	}

	public void setDeliveredQuantityForExtarnal(String deliveredQuantityForExtarnal) {
		this.deliveredQuantityForExtarnal = deliveredQuantityForExtarnal;
	}

	public String getUomCdLoadedForExternal() {
		return uomCdLoadedForExternal;
	}

	public void setUomCdLoadedForExternal(String uomCdLoadedForExternal) {
		this.uomCdLoadedForExternal = uomCdLoadedForExternal;
	}

	public Long getExternalOrderId() {
		return externalOrderId;
	}

	public void setExternalOrderId(Long externalOrderId) {
		this.externalOrderId = externalOrderId;
	}

	public boolean isEditOrder() {
		return editOrder;
	}

	public void setEditOrder(boolean editOrder) {
		this.editOrder = editOrder;
	}

	public Long getExternalOrderNumber() {
		return externalOrderNumber;
	}

	public void setExternalOrderNumber(Long externalOrderNumber) {
		this.externalOrderNumber = externalOrderNumber;
	}

	public String getPupupTitle() {
		return pupupTitle;
	}

	public void setPupupTitle(String pupupTitle) {
		this.pupupTitle = pupupTitle;
	}

	public String getNoteTruckDriver() {
		return noteTruckDriver;
	}

	public void setNoteTruckDriver(String noteTruckDriver) {
		this.noteTruckDriver = noteTruckDriver;
	}
	
	public Date getDispoDate() {
		return dispoDate;
	}

	public void setDispoDate(Date dispoDate) {
		this.dispoDate = dispoDate;
	}
	
	public void setDefaultDeliveryFromTo(HttpServletRequest request) {
		TimeZone userTz = UserProvider.getTimezone();

		Calendar cal = userTz.asCalendar();

		Calendar maxDeliveryFromTimeToday = userTz.asCalendar();
		maxDeliveryFromTimeToday = DateTimeUtils.setTimePart(maxDeliveryFromTimeToday, 17, 45, 0, 0);

		String deliveryFromDateString;
		String deliveryFromTimeString;
		String deliveryToDateString;
		String deliveryToTimeString;

		Date deliveryFromDate = dispoDate;
		if (deliveryFromDate == null) {
			deliveryFromDate = new Date();
		}

		deliveryFromDateString = userTz.fmtAsDayMonthYear(deliveryFromDate);
		if ( userTz.isToday(deliveryFromDate) ) {
			if( cal.after(maxDeliveryFromTimeToday) ) {
				// nextworking day, 8:00 - 18:00
				cal.setTime(userTz.nextWorkingDay());
				cal.set(Calendar.HOUR_OF_DAY, DELIVERY_FROM_HOUR);
				cal.set(Calendar.MINUTE, 0);

				deliveryFromDateString = userTz.fmtAsDayMonthYear(cal.getTime());
				deliveryToDateString = deliveryFromDateString;

				deliveryFromTimeString = userTz.fmtAsHourMinute(cal.getTime());
				cal.set(Calendar.HOUR_OF_DAY, DELIVERY_TO_HOUR);
				deliveryToTimeString = userTz.fmtAsHourMinute(cal.getTime());
			}
			else {
				// today, nextQuarter - 18:00
				deliveryToDateString = request.getParameter("dateString");

				cal = DateTimeUtils.getNextQuarterHour(userTz.asCalendar());
				deliveryFromTimeString = userTz.fmtAsHourMinute(cal.getTime());
				cal.set(Calendar.HOUR_OF_DAY, DELIVERY_TO_HOUR);
				cal.set(Calendar.MINUTE, 0);
				deliveryToTimeString = userTz.fmtAsHourMinute(cal.getTime());
			}
		}
		else {
			// that day, 8:00 - 18:00
			cal.setTime(deliveryFromDate);
			cal.set(Calendar.HOUR_OF_DAY, DELIVERY_FROM_HOUR);
			cal.set(Calendar.MINUTE, 0);

			deliveryToDateString = deliveryFromDateString;
			deliveryFromTimeString = userTz.fmtAsHourMinute(cal.getTime());
			cal.set(Calendar.HOUR_OF_DAY, DELIVERY_TO_HOUR);
			deliveryToTimeString = userTz.fmtAsHourMinute(cal.getTime());
		}

		setDeliveryFromDate(deliveryFromDateString);
		setDeliveryToDate(deliveryToDateString);
		setDeliveryFromTime(deliveryFromTimeString);
		setDeliveryToTime(deliveryToTimeString);
	}

	/**
     * Default constructor.
     */
    public ExternalOrderForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "ExternalOrderForm");
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
     * fields on the order.web form.
     */
    private void init() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
			
	        deliveryFromDate = null;
	        deliveryToDate = null;
	        deliveryFromTime = null;
	        deliveryToTime = null;			
			externalOrderId = null;
			externalOrderNumber = null;
		    poolsForExternal = null;
		    carriersForExternal = null;
		    materialsForExternal = null;
		    uomListForExternal = null;
		    siteListForExternal = null;
		    
		    selectedPoolForExternal = null;
		    selectedCarrierForExternal = null;
		    selectedMaterialForExternal = null;
		    selectedShippingPointForExternal = null;
		    selectedShiptoForExternal = null;
			
		    uomCdForExternal = null;
		    
		    orderStatusTransmittedToDriver = Boolean.FALSE;
		    orderStatusShipping = Boolean.FALSE;
		    editOrder = Boolean.FALSE;

		    shippingPointCoordLat =  null;
		    shippingPointCoordLon =  null;
		    shiptoCoordLat =  null;
		    shiptoCoordLon =  null;
		    noteTruckDriver = null;
		    dispoDate = null;
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
			
	        /* no need to validate if 'delete' button was pressed */
	        if (request.getParameter("delete") != null) {
	            return result = null;
	        }
	        if (errors == null) {
	            errors = new ActionErrors();
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
    
    
    public void resetFields(){
        
        deliveryFromDate = null;
        deliveryToDate = null;
        deliveryFromTime = null;
        deliveryToTime = null;
    	poolsForExternal = null;
        carriersForExternal = null;
        materialsForExternal = null;
        uomListForExternal = null;
        siteListForExternal = null;
        selectedPoolForExternal = null;
        selectedCarrierForExternal = null;
        selectedMaterialForExternal = null;
        selectedShippingPointForExternal = null;
        selectedShiptoForExternal = null;
        uomCdForExternal = null;
        sapId = null;
        orderQuantityForExtarnal = null;
        loadedQuantityForExtarnal = null;
        deliveredQuantityForExtarnal = null;
        uomCdLoadedForExternal = null;
        externalOrderId = null;
        externalOrderNumber = null;
        editOrder = Boolean.FALSE;
        orderStatusTransmittedToDriver = Boolean.FALSE;
        orderStatusShipping = Boolean.FALSE;
        shippingPointCoordLat =  null;
	    shippingPointCoordLon =  null;
	    shiptoCoordLat =  null;
	    shiptoCoordLon =  null;
	    noteTruckDriver = null;
    }
    
    public void resetFieldsWithoutPools(){
        
        carriersForExternal = null;
        materialsForExternal = null;
        uomListForExternal = null;
        siteListForExternal = null;
        
        selectedCarrierForExternal = null;
        selectedMaterialForExternal = null;
        selectedShippingPointForExternal = null;
        selectedShiptoForExternal = null;
        uomCdForExternal = null;
        sapId =  null;
        orderQuantityForExtarnal = null;
        loadedQuantityForExtarnal = null;
        deliveredQuantityForExtarnal = null;
        uomCdLoadedForExternal = null;
        shippingPointCoordLat =  null;
	    shippingPointCoordLon =  null;
	    shiptoCoordLat =  null;
	    shiptoCoordLon =  null;
    }
    
    public void resetFieldsWithoutPoolsAndCarriers(){
        
        materialsForExternal = null;
        uomListForExternal = null;
        siteListForExternal = null;
        
        selectedMaterialForExternal = null;
        selectedShippingPointForExternal = null;
        selectedShiptoForExternal = null;
        uomCdForExternal = null;
        sapId =  null;
        orderQuantityForExtarnal = null;
        loadedQuantityForExtarnal = null;
        deliveredQuantityForExtarnal = null;
        uomCdLoadedForExternal = null;
        shippingPointCoordLat =  null;
	    shippingPointCoordLon =  null;
	    shiptoCoordLat =  null;
	    shiptoCoordLon =  null;
    }

	/**
	 * @return the shippingPointCoordLat
	 */
	public Double getShippingPointCoordLat() {
		return shippingPointCoordLat;
	}

	/**
	 * @param shippingPointCoordLat the shippingPointCoordLat to set
	 */
	public void setShippingPointCoordLat(Double shippingPointCoordLat) {
		this.shippingPointCoordLat = shippingPointCoordLat;
	}

	/**
	 * @return the shippingPointCoordLon
	 */
	public Double getShippingPointCoordLon() {
		return shippingPointCoordLon;
	}

	/**
	 * @param shippingPointCoordLon the shippingPointCoordLon to set
	 */
	public void setShippingPointCoordLon(Double shippingPointCoordLon) {
		this.shippingPointCoordLon = shippingPointCoordLon;
	}

	/**
	 * @return the shiptoCoordLat
	 */
	public Double getShiptoCoordLat() {
		return shiptoCoordLat;
	}

	/**
	 * @param shiptoCoordLat the shiptoCoordLat to set
	 */
	public void setShiptoCoordLat(Double shiptoCoordLat) {
		this.shiptoCoordLat = shiptoCoordLat;
	}

	/**
	 * @return the shiptoCoordLon
	 */
	public Double getShiptoCoordLon() {
		return shiptoCoordLon;
	}

	/**
	 * @param shiptoCoordLon the shiptoCoordLon to set
	 */
	public void setShiptoCoordLon(Double shiptoCoordLon) {
		this.shiptoCoordLon = shiptoCoordLon;
	}
}
