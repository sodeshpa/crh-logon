/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.bestellung;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.refdata.OrderStatusTxEntity;

/**
 * Used by the JSP which provides both the input form for external order
 * searches and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="order_bestellung_BestellungExternalSearchForm"
 */
public class BestellungExternalSearchForm extends ActionForm {

	//TODO Oct 17, 2013/orsika: fix serial id
	private static final long serialVersionUID = 1L;
	
	private static final JcanLogger LOG =
		JcanLoggerFactory.getLogger(BestellungExternalSearchForm.class);
	
	private List<OrderStatusTxEntity> orderStatusList;
	
	private String formAction;

	private boolean applyUserDefaultSettings = false;
	
	//user specific default values
	//->jsp
	private String defaultDeliveryFromDateString;
	private String defaultDeliveryToDateString;
	private List<Long> defaultOrderStatusList;
	private String extDeliveryNote;
	
	//<-jsp
	private String inDeliveryFromDate;
	private String inDeliveryToDate;
	private String inOrderStatusList;
	private String vehicleCompositeId;
	private String vehicleName;
	
	//todo orsika 06.05.2013 use map instead of separate variables
	private Long wExtOrderNumber;
	private Long wExtDeliveryNote;
	private Long wShiptoName;
	private Long wShiptoLoadUnload;
	private Long wShiptoContactPersonTxt;
	
	private Long wShiptoStreetHouseNo;
	private Long wShiptoCountry;
	private Long wShiptoPostalCode;
	private Long wShiptoCity;
	private Long wStatusTxt;
	
	private Long wMaterialName;
	private Long wOrderedQuantity;
	private Long wOrderedUomName;
	private Long wDeliveredQuantity;
	private Long wDeliveredUomName;
	
	private Long wShippingpointName;
	private Long wSpLoadUnload;
	private Long wSpContactPersonTxt;
	private Long wSpStreetHouseNo;
	private Long wSpCountry;
	
	private Long wSpPostalCode;
	private Long wSpCity;
	private Long wDeliveryFrom;
	private Long wDeliveryTo;
	private Long wPoolName;
	
	private Long wCarrierName;
	private Long wTruckName;
	
	private Long idxExtOrderNumber;
	private Long idxExtDeliveryNote;
	private Long idxShiptoName;
	private Long idxShiptoLoadUnload;
	private Long idxShiptoContactPersonTxt;
	
	private Long idxShiptoStreetHouseNo;
	private Long idxShiptoCountry;
	private Long idxShiptoPostalCode;
	private Long idxShiptoCity;
	private Long idxStatusTxt;
	
	private Long idxMaterialName;
	private Long idxOrderedQuantity;
	private Long idxOrderedUomName;
	private Long idxDeliveredQuantity;
	private Long idxDeliveredUomName;
	
	private Long idxShippingpointName;
	private Long idxSpLoadUnload;
	private Long idxSpContactPersonTxt;
	private Long idxSpStreetHouseNo;
	private Long idxSpCountry;
	
	private Long idxSpPostalCode;
	private Long idxSpCity;
	private Long idxDeliveryFrom;
	private Long idxDeliveryTo;
	private Long idxPoolName;
	
	private Long idxCarrierName;
	private Long idxTruckName;
	
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
	
	public List<OrderStatusTxEntity> getOrderStatusList() {
		return orderStatusList;
	}

	public void setOrderStatusList(List<OrderStatusTxEntity> orderStatusList) {
		this.orderStatusList = orderStatusList;
	}

	public boolean isApplyUserDefaultSettings() {
		return applyUserDefaultSettings;
	}

	public void setApplyUserDefaultSettings(boolean applyUserDefaultSettings) {
		this.applyUserDefaultSettings = applyUserDefaultSettings;
	}

	public String getDefaultDeliveryFromString() {
		return defaultDeliveryFromDateString;
	}
	
	public void setDefaultDeliveryFromDateString(String defaultDeliveryFromString) {
		this.defaultDeliveryFromDateString = defaultDeliveryFromString;
	}
	
	public String getDefaultDeliveryToString() {
		return defaultDeliveryToDateString;
	}

	public void setDefaultDeliveryToDateString(String defaultDeliveryToString) {
		this.defaultDeliveryToDateString = defaultDeliveryToString;
	}

	public List<Long> getDefaultOrderStatusList() {
		return defaultOrderStatusList;
	}

	public void setDefaultOrderStatusList(List<Long> defaultOrderStatusList) {
		this.defaultOrderStatusList = defaultOrderStatusList;
	}
	
	public String getInDeliveryFromDate() {
		return inDeliveryFromDate;
	}

	public void setInDeliveryFromDate(String inDeliveryFromDate) {
		this.inDeliveryFromDate = inDeliveryFromDate;
	}

	public String getInDeliveryToDate() {
		return inDeliveryToDate;
	}

	public void setInDeliveryToDate(String inDeliveryToDate) {
		this.inDeliveryToDate = inDeliveryToDate;
	}

	public String getInOrderStatusList() {
		return inOrderStatusList;
	}

	public void setInOrderStatusList(String inOrderStatusList) {
		this.inOrderStatusList = inOrderStatusList;
	}

	public String getVehicleCompositeId() {
		return vehicleCompositeId;
	}

	public void setVehicleCompositeId(String vehicleCompositeId) {
		this.vehicleCompositeId = vehicleCompositeId;
	}
	
	public String getVehicleName() {
		return vehicleName;
	}

	public void setVehicleName(String vehicleName) {
		this.vehicleName = vehicleName;
	}


	public String getDefaultDeliveryFromDateString() {
		return defaultDeliveryFromDateString;
	}

	public String getDefaultDeliveryToDateString() {
		return defaultDeliveryToDateString;
	}

	public String getFormAction() {
		return formAction;
	}

	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}
	
	public String getExtDeliveryNote() {
		return extDeliveryNote;
	}
	
	public void setExtDeliveryNote(String extDeliveryNote) {
		this.extDeliveryNote = extDeliveryNote;
	}
	
	public Long getwExtOrderNumber() {
		return wExtOrderNumber;
	}

	public void setwExtOrderNumber(Long wExtOrderNumber) {
		this.wExtOrderNumber = wExtOrderNumber;
	}

	public Long getwExtDeliveryNote() {
		return wExtDeliveryNote;
	}

	public void setwExtDeliveryNote(Long wExtDeliveryNote) {
		this.wExtDeliveryNote = wExtDeliveryNote;
	}

	public Long getwShiptoName() {
		return wShiptoName;
	}

	public void setwShiptoName(Long wShiptoName) {
		this.wShiptoName = wShiptoName;
	}

	public Long getwShiptoLoadUnload() {
		return wShiptoLoadUnload;
	}

	public void setwShiptoLoadUnload(Long wShiptoLoadUnload) {
		this.wShiptoLoadUnload = wShiptoLoadUnload;
	}

	public Long getwShiptoContactPersonTxt() {
		return wShiptoContactPersonTxt;
	}

	public void setwShiptoContactPersonTxt(Long wShiptoContactPersonTxt) {
		this.wShiptoContactPersonTxt = wShiptoContactPersonTxt;
	}

	public Long getwShiptoStreetHouseNo() {
		return wShiptoStreetHouseNo;
	}

	public void setwShiptoStreetHouseNo(Long wShiptoStreetHouseNo) {
		this.wShiptoStreetHouseNo = wShiptoStreetHouseNo;
	}

	public Long getwShiptoCountry() {
		return wShiptoCountry;
	}

	public void setwShiptoCountry(Long wShiptoCountry) {
		this.wShiptoCountry = wShiptoCountry;
	}

	public Long getwShiptoPostalCode() {
		return wShiptoPostalCode;
	}

	public void setwShiptoPostalCode(Long wShiptoPostalCode) {
		this.wShiptoPostalCode = wShiptoPostalCode;
	}

	public Long getwShiptoCity() {
		return wShiptoCity;
	}

	public void setwShiptoCity(Long wShiptoCity) {
		this.wShiptoCity = wShiptoCity;
	}

	public Long getwStatusTxt() {
		return wStatusTxt;
	}

	public void setwStatusTxt(Long wStatusTxt) {
		this.wStatusTxt = wStatusTxt;
	}

	public Long getwMaterialName() {
		return wMaterialName;
	}

	public void setwMaterialName(Long wMaterialName) {
		this.wMaterialName = wMaterialName;
	}

	public Long getwOrderedQuantity() {
		return wOrderedQuantity;
	}

	public void setwOrderedQuantity(Long wOrderedQuantity) {
		this.wOrderedQuantity = wOrderedQuantity;
	}

	public Long getwOrderedUomName() {
		return wOrderedUomName;
	}

	public void setwOrderedUomName(Long wOrderedUomName) {
		this.wOrderedUomName = wOrderedUomName;
	}

	public Long getwDeliveredQuantity() {
		return wDeliveredQuantity;
	}

	public void setwDeliveredQuantity(Long wDeliveredQuantity) {
		this.wDeliveredQuantity = wDeliveredQuantity;
	}

	public Long getwDeliveredUomName() {
		return wDeliveredUomName;
	}

	public void setwDeliveredUomName(Long wDeliveredUomName) {
		this.wDeliveredUomName = wDeliveredUomName;
	}

	public Long getwShippingpointName() {
		return wShippingpointName;
	}

	public void setwShippingpointName(Long wShippingpointName) {
		this.wShippingpointName = wShippingpointName;
	}

	public Long getwSpLoadUnload() {
		return wSpLoadUnload;
	}

	public void setwSpLoadUnload(Long wSpLoadUnload) {
		this.wSpLoadUnload = wSpLoadUnload;
	}

	public Long getwSpContactPersonTxt() {
		return wSpContactPersonTxt;
	}

	public void setwSpContactPersonTxt(Long wSpContactPersonTxt) {
		this.wSpContactPersonTxt = wSpContactPersonTxt;
	}

	public Long getwSpStreetHouseNo() {
		return wSpStreetHouseNo;
	}

	public void setwSpStreetHouseNo(Long wSpStreetHouseNo) {
		this.wSpStreetHouseNo = wSpStreetHouseNo;
	}

	public Long getwSpCountry() {
		return wSpCountry;
	}

	public void setwSpCountry(Long wSpCountry) {
		this.wSpCountry = wSpCountry;
	}

	public Long getwSpPostalCode() {
		return wSpPostalCode;
	}

	public void setwSpPostalCode(Long wSpPostalCode) {
		this.wSpPostalCode = wSpPostalCode;
	}

	public Long getwSpCity() {
		return wSpCity;
	}

	public void setwSpCity(Long wSpCity) {
		this.wSpCity = wSpCity;
	}

	public Long getwDeliveryFrom() {
		return wDeliveryFrom;
	}

	public void setwDeliveryFrom(Long wDeliveryFrom) {
		this.wDeliveryFrom = wDeliveryFrom;
	}

	public Long getwDeliveryTo() {
		return wDeliveryTo;
	}

	public void setwDeliveryTo(Long wDeliveryTo) {
		this.wDeliveryTo = wDeliveryTo;
	}

	public Long getwPoolName() {
		return wPoolName;
	}

	public void setwPoolName(Long wPoolName) {
		this.wPoolName = wPoolName;
	}

	public Long getwCarrierName() {
		return wCarrierName;
	}

	public void setwCarrierName(Long wCarrierName) {
		this.wCarrierName = wCarrierName;
	}

	public Long getwTruckName() {
		return wTruckName;
	}

	public void setwTruckName(Long wTruckName) {
		this.wTruckName = wTruckName;
	}

	public Long getIdxExtOrderNumber() {
		return idxExtOrderNumber;
	}

	public void setIdxExtOrderNumber(Long idxExtOrderNumber) {
		this.idxExtOrderNumber = idxExtOrderNumber;
	}

	public Long getIdxExtDeliveryNote() {
		return idxExtDeliveryNote;
	}

	public void setIdxExtDeliveryNote(Long idxExtDeliveryNote) {
		this.idxExtDeliveryNote = idxExtDeliveryNote;
	}

	public Long getIdxShiptoName() {
		return idxShiptoName;
	}

	public void setIdxShiptoName(Long idxShiptoName) {
		this.idxShiptoName = idxShiptoName;
	}

	public Long getIdxShiptoLoadUnload() {
		return idxShiptoLoadUnload;
	}

	public void setIdxShiptoLoadUnload(Long idxShiptoLoadUnload) {
		this.idxShiptoLoadUnload = idxShiptoLoadUnload;
	}

	public Long getIdxShiptoContactPersonTxt() {
		return idxShiptoContactPersonTxt;
	}

	public void setIdxShiptoContactPersonTxt(Long idxShiptoContactPersonTxt) {
		this.idxShiptoContactPersonTxt = idxShiptoContactPersonTxt;
	}

	public Long getIdxShiptoStreetHouseNo() {
		return idxShiptoStreetHouseNo;
	}

	public void setIdxShiptoStreetHouseNo(Long idxShiptoStreetHouseNo) {
		this.idxShiptoStreetHouseNo = idxShiptoStreetHouseNo;
	}

	public Long getIdxShiptoCountry() {
		return idxShiptoCountry;
	}

	public void setIdxShiptoCountry(Long idxShiptoCountry) {
		this.idxShiptoCountry = idxShiptoCountry;
	}

	public Long getIdxShiptoPostalCode() {
		return idxShiptoPostalCode;
	}

	public void setIdxShiptoPostalCode(Long idxShiptoPostalCode) {
		this.idxShiptoPostalCode = idxShiptoPostalCode;
	}

	public Long getIdxShiptoCity() {
		return idxShiptoCity;
	}

	public void setIdxShiptoCity(Long idxShiptoCity) {
		this.idxShiptoCity = idxShiptoCity;
	}

	public Long getIdxStatusTxt() {
		return idxStatusTxt;
	}

	public void setIdxStatusTxt(Long idxStatusTxt) {
		this.idxStatusTxt = idxStatusTxt;
	}

	public Long getIdxMaterialName() {
		return idxMaterialName;
	}

	public void setIdxMaterialName(Long idxMaterialName) {
		this.idxMaterialName = idxMaterialName;
	}

	public Long getIdxOrderedQuantity() {
		return idxOrderedQuantity;
	}

	public void setIdxOrderedQuantity(Long idxOrderedQuantity) {
		this.idxOrderedQuantity = idxOrderedQuantity;
	}

	public Long getIdxOrderedUomName() {
		return idxOrderedUomName;
	}

	public void setIdxOrderedUomName(Long idxOrderedUomName) {
		this.idxOrderedUomName = idxOrderedUomName;
	}

	public Long getIdxDeliveredQuantity() {
		return idxDeliveredQuantity;
	}

	public void setIdxDeliveredQuantity(Long idxDeliveredQuantity) {
		this.idxDeliveredQuantity = idxDeliveredQuantity;
	}

	public Long getIdxDeliveredUomName() {
		return idxDeliveredUomName;
	}

	public void setIdxDeliveredUomName(Long idxDeliveredUomName) {
		this.idxDeliveredUomName = idxDeliveredUomName;
	}

	public Long getIdxShippingpointName() {
		return idxShippingpointName;
	}

	public void setIdxShippingpointName(Long idxShippingpointName) {
		this.idxShippingpointName = idxShippingpointName;
	}

	public Long getIdxSpLoadUnload() {
		return idxSpLoadUnload;
	}

	public void setIdxSpLoadUnload(Long idxSpLoadUnload) {
		this.idxSpLoadUnload = idxSpLoadUnload;
	}

	public Long getIdxSpContactPersonTxt() {
		return idxSpContactPersonTxt;
	}

	public void setIdxSpContactPersonTxt(Long idxSpContactPersonTxt) {
		this.idxSpContactPersonTxt = idxSpContactPersonTxt;
	}

	public Long getIdxSpStreetHouseNo() {
		return idxSpStreetHouseNo;
	}

	public void setIdxSpStreetHouseNo(Long idxSpStreetHouseNo) {
		this.idxSpStreetHouseNo = idxSpStreetHouseNo;
	}

	public Long getIdxSpCountry() {
		return idxSpCountry;
	}

	public void setIdxSpCountry(Long idxSpCountry) {
		this.idxSpCountry = idxSpCountry;
	}

	public Long getIdxSpPostalCode() {
		return idxSpPostalCode;
	}

	public void setIdxSpPostalCode(Long idxSpPostalCode) {
		this.idxSpPostalCode = idxSpPostalCode;
	}

	public Long getIdxSpCity() {
		return idxSpCity;
	}

	public void setIdxSpCity(Long idxSpCity) {
		this.idxSpCity = idxSpCity;
	}

	public Long getIdxDeliveryFrom() {
		return idxDeliveryFrom;
	}

	public void setIdxDeliveryFrom(Long idxDeliveryFrom) {
		this.idxDeliveryFrom = idxDeliveryFrom;
	}

	public Long getIdxDeliveryTo() {
		return idxDeliveryTo;
	}

	public void setIdxDeliveryTo(Long idxDeliveryTo) {
		this.idxDeliveryTo = idxDeliveryTo;
	}

	public Long getIdxPoolName() {
		return idxPoolName;
	}

	public void setIdxPoolName(Long idxPoolName) {
		this.idxPoolName = idxPoolName;
	}

	public Long getIdxCarrierName() {
		return idxCarrierName;
	}

	public void setIdxCarrierName(Long idxCarrierName) {
		this.idxCarrierName = idxCarrierName;
	}

	public Long getIdxTruckName() {
		return idxTruckName;
	}

	public void setIdxTruckName(Long idxTruckName) {
		this.idxTruckName = idxTruckName;
	}

	public void clearDefaults() {
		formAction = null;
		
		applyUserDefaultSettings = false;
		
		defaultDeliveryFromDateString = null;
		defaultDeliveryToDateString = null;
		defaultOrderStatusList = null;
		
		inDeliveryFromDate = null;
		inDeliveryToDate = null;
		inOrderStatusList = null;
		vehicleCompositeId = null;
		vehicleName = null;
		
		wExtOrderNumber = null;
		wExtDeliveryNote = null;
		wShiptoName = null;
		wShiptoLoadUnload = null;
		wShiptoContactPersonTxt = null;
		
		wShiptoStreetHouseNo = null;
		wShiptoCountry = null;
		wShiptoPostalCode = null;
		wShiptoCity = null;
		wStatusTxt = null;
		
		wMaterialName = null;
		wOrderedQuantity = null;
		wOrderedUomName = null;
		wDeliveredQuantity = null;
		wDeliveredUomName = null;
		
		wShippingpointName = null;
		wSpLoadUnload = null;
		wSpContactPersonTxt = null;
		wSpStreetHouseNo = null;
		wSpCountry = null;
		
		wSpPostalCode = null;
		wSpCity = null;
		wDeliveryFrom = null;
		wDeliveryTo = null;
		wPoolName =  null;
		
		wCarrierName = null;
		wTruckName = null;
		
		idxExtOrderNumber = null;
		idxExtDeliveryNote = null;
		idxShiptoName = null;
		idxShiptoLoadUnload = null;
		idxShiptoContactPersonTxt = null;
		
		idxShiptoStreetHouseNo = null;
		idxShiptoCountry = null;
		idxShiptoPostalCode = null;
		idxShiptoCity = null;
		idxStatusTxt = null;
		
		idxMaterialName = null;
		idxOrderedQuantity = null;
		idxOrderedUomName = null;
		idxDeliveredQuantity = null;
		idxDeliveredUomName = null;
		
		idxShippingpointName = null;
		idxSpLoadUnload = null;
		idxSpContactPersonTxt = null;
		idxSpStreetHouseNo = null;
		idxSpCountry = null;
		
		idxSpPostalCode = null;
		idxSpCity = null;
		idxDeliveryFrom = null;
		idxDeliveryTo = null;
		idxPoolName =  null;
		
		idxCarrierName = null;
		idxTruckName = null;
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
			clearDefaults();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
}
