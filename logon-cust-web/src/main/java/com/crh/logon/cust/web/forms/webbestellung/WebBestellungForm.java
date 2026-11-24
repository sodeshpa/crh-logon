/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.cust.web.forms.webbestellung;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.ordercreation.DeliverySettingSalesAreaMap;
import com.crh.logon.business.ordercreation.DeliverySettingShiptoMap;
import com.crh.logon.business.ordercreation.InvalidMapSupport;
import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.business.services.beans.CustorderDTO;
import com.crh.logon.business.services.utils.DateTimeAwareOrderWrapper;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.MaterialColumn;
import com.crh.logon.business.services.utils.MaterialType;
import com.crh.logon.business.services.utils.OrderUtils;
import com.crh.logon.business.services.utils.SplittedOrderData;
import com.crh.logon.business.services.utils.SplittedOrderItemData;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.constants.ShiptoSalesAreaError;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.CustorderItemEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.WeborderShipToSoldToEntity;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.web.utils.JsonUtils;
import com.crh.logon.web.utils.MessageUtils;
import com.crh.logon.web.utils.UserProvider;
import flexjson.JSONSerializer;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="cust_WebBestellungForm"
 */
public class WebBestellungForm extends ActionForm {

    private static final long serialVersionUID = -8910104051337644556L;
    
    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(WebBestellungForm.class);
    
    public static final String PICKUP_SELECTION_DELIVERY="delivery";
    public static final String PICKUP_SELECTION_PICKUP="pickup";
	public static final Long DELIVERY_FROM_DEFAULT_TIME = new Long(300);
    public static final Long DELIVERY_TO_DEFAULT_TIME = new Long(1380);
    
    // input from the user (BestellungShipTo)
    private String compositeId;
    
    // input from the user (Bestellung)
    private String formAction;
    
    private HashMap multipleSalesAreaShiptos;
    
    //duration of the route form the shipping point to the shipTo
    private String durationTime;
    private String distanceMeters;
    private String durationCorrFactor;
    
    // db state
    private ShiptoEntity shipTo;
    private SapBpEntity soldTo;

	private TimeZone shipToTz;
    
    private String sareaId;
    
    private String deliveryFromDatStr; 
    private String deliveryToDatStr;
    private String deliveryFromTime; 
    private String deliveryToTime;
    
    private String pickupTypeSelect = PICKUP_SELECTION_DELIVERY;
    
	private WeborderShipToSoldToEntity sapSoldShip;
    
	private List<WeborderShipToSoldToEntity> shiptoList = new ArrayList<WeborderShipToSoldToEntity>();
    
    private Map bimodalRailRestrictionMap;
    private Long bimodalShiptoSareaRestrId;
    
    private String deliverySettingCompositeId;
    private String custWeborderNote;
    private String custWeborderRefNo;
    private String billtoSapBpId;
    private String payerSapBpId;
    private String orderedUomCd;
    
    private boolean copyCustomerRemarkToDriver;
    
	private DeliverySettingShiptoMap shipToMap;

	// adjust deliveries data section begin
	private List <SplittedOrderData> splittedOrderDatas;
	private Boolean onHoldOrderAndLockedFlag;
	private MaterialType materialType;
	CustorderDTO custorderDTO;
	Set<MaterialColumn> materialColumns;
	// adjust deliveries data section end


	/**
     * Default constructor.
     */
    public WebBestellungForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "WebBestellungForm");
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
			//shipToId = "";
			compositeId = "";
			shipTo = null;
			formAction = "";
			bimodalRailRestrictionMap=new HashMap();
			bimodalRailCustorder = null;
			copyCustomerRemarkToDriver = false;
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
    		//shipToId = "";
    		compositeId = "";
    		formAction = "";
    		pickupTypeSelect = PICKUP_SELECTION_DELIVERY;
    		copyCustomerRemarkToDriver = false;
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
			
	        if ( getBilltoSapBpId()!= null && (getBilltoSapBpId().equals("0") || getBilltoSapBpId().equals(""))) {
	        	setBilltoSapBpId(null);
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
	
	public boolean shipToInitialized(){
		return shipTo != null;
	}

	public ShiptoEntity getShipTo() {
		if (shipTo == null){
			// never return null, there can be problems when user hits reload
			return new ShiptoEntity();
		}
		return shipTo;
	}

	public void setShipTo(ShiptoEntity shipTo, TimeZone shipToTz) {
		this.shipTo = shipTo;
		this.shipToTz = shipToTz;
	}
	
	/**
	 * to be called by entry form after shipto and all reference data has been set
	 */
	public void populateForm(){
		
		if (shipTo != null) {
			deliveryFromDatStr = getShipToTimeZone().fmtAsDayMonthYear(
					getShipToTimeZone().nextWorkingDay());

			deliveryToDatStr = deliveryFromDatStr;
		}
	}

	public String getFormAction() {
		return formAction;
	}

	public void setFormAction(String action) {
		this.formAction = action;
	}

	public SapBpEntity getSoldTo() {
		return soldTo;
	}

	public void setSoldTo(SapBpEntity soldTo) {
		this.soldTo = soldTo;
	}
	
	private CompletedOrdersBean completedOrdersBean;
	private String orderQuantityStr;
    private List<DateTimeAwareOrderWrapper> orderList; // Lists of new and ongoing orders
    private List<DateTimeAwareOrderWrapper> completedOrderList; // Lists of completed orders
    private Collection <DeliverySettingDTO> productList;
    
    private HashMap json;

    private CustorderEntity custOrder;
    private CustorderEntity bimodalRailCustorder;
    private ShiptoSareaEntity shiptoSarea;
    private SareaEntity sarea;

	private Map<Object, List<ShiptoSalesAreaError>> invalidMap;

	public void setOrderList(List<DateTimeAwareOrderWrapper> orderList) {
		for (DateTimeAwareOrderWrapper order: orderList) {
			order.setDeletionAllowed(
				OrderUtils.isWebOrderDeletable(order.getOrderStatusCd(), order.getDeliveryTypeCd().longValue()));
		}
		
		this.orderList = orderList;
	}

	public void setCompletedOrderList(List<DateTimeAwareOrderWrapper> completedOrderList) {
		this.completedOrderList = completedOrderList;
	}

	public List<DateTimeAwareOrderWrapper> getOrderList() {
		return orderList;
	}

	public List<DateTimeAwareOrderWrapper> getCompletedOrderList() {
	    return completedOrderList;
	}

	public Collection<DeliverySettingDTO>  getProductList() {
		return productList;
	}

	public void setProductList(Collection <DeliverySettingDTO>  productList) {
		this.productList = productList;
	}
	
	public HashMap getJson() {
		return json;
	}

	public void setJson(HashMap json) {
		this.json = json;
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

	public String getDeliveryFromDatStr() {
		return deliveryFromDatStr;
	}

	public void setDeliveryFromDatStr(String deliveryFromDatStr) {
		this.deliveryFromDatStr = deliveryFromDatStr;
	}

	public String getDeliveryToDatStr() {
		return deliveryToDatStr;
	}

	public void setDeliveryToDatStr(String deliveryToDatStr) {
		this.deliveryToDatStr = deliveryToDatStr;
	}

	public Date getDeliveryFrom() throws ParseException {
		return getShipToTimeZone().parseDayMonthYearHourMinute(deliveryFromDatStr, deliveryFromTime);
	}
	
	public Date getDeliveryTo() throws ParseException {
		return getShipToTimeZone().parseDayMonthYearHourMinute(deliveryToDatStr, deliveryToTime);
	}	

	public String getDurationTime() {
		return durationTime;
	}

	public void setDurationTime(String durationTime) {
		this.durationTime = durationTime;
	}

	public CompletedOrdersBean getCompletedOrdersBean() {
		return completedOrdersBean;
	}

	public void setCompletedOrdersBean(CompletedOrdersBean completedOrdersBean) {
		this.completedOrdersBean = completedOrdersBean;
	}

	public SareaEntity getSarea() {
		return sarea;
	}

	public void setSarea(SareaEntity sareaBs) {
		this.sarea = sareaBs;
	}

	public ShiptoSareaEntity getShiptoSarea() {
		return shiptoSarea;
	}

	public void setShiptoSarea(ShiptoSareaEntity shiptoSarea) {
		this.shiptoSarea = shiptoSarea;
	}

	public String getSareaId() {
		return sareaId;
	}

	public void setSareaId(String sareaId) {
		this.sareaId = sareaId;
	}

	public String getShiptoNr() {
		return compositeId;
	}

	public void setShiptoNr(String shiptoNr) {
		this.compositeId = shiptoNr;
	}
	
	public String getSelectedShipToName() {
		
		if (sapSoldShip == null) {
			return null;
		}
		
		return sapSoldShip.getShiptoName();
	}
	
	public String getSelectedShipToStreet() {
		
		if (sapSoldShip == null) {
			return null;
		}
		return sapSoldShip.getStreet();
	}
	
	public String getSelectedShipToHouseNumber() {
		
		if (sapSoldShip == null) {
			return null;
		}
		return sapSoldShip.getHouseNumber();
	}
	
	public String getSelectedShiptoPostalCode() {
		if (sapSoldShip == null) {
			return null;
		}
		return sapSoldShip.getPostalCode();
	}
	
	public String getSelectedShiptoCountryCd() {
		if (sapSoldShip == null) {
			return null;
		}
		return sapSoldShip.getCountryCd();
	}
	
	public String getSelectedShiptoCity() {
		if (sapSoldShip == null) {
			return null;
		}
		return sapSoldShip.getCity();
	}

	public String getOrderQuantityStr() {
		return orderQuantityStr;
	}
	
	public void initEditorFields() {

		ShiptoEntity shipto = getShipTo();

		// -------------------
		// Setup of Delivery To / From time fields
		// -------------------
		setDeliveryFromTime(DateTimeUtils.getHourMinuteTimePart(shipto.getOpen1From(), DELIVERY_FROM_DEFAULT_TIME));
		setDeliveryToTime(DateTimeUtils.getHourMinuteTimePart(shipto.getOpen2To(), DELIVERY_TO_DEFAULT_TIME));

		// --------------------
		// Helper object to collect the data for a new order
		// --------------------
		// Default material/quantity (ID = -1 means the editor is disabled)
		setDeliverySettingCompositeId("-1");
		setOrderQuantityStr(BigDecimal.ONE.toString());

		// -------------------
		// JSON for ExtJS widgets
		// -------------------
		HashMap jsonData =getJson();
		jsonData.put("billToList", new JSONSerializer()
			.include("billtoSapBp.name1")
			.include("billtoSapBp.sapBpId")
			.include("billtoSapBp.sapId")
			.include("billtoSapBp.name2")
			.exclude("*"));
		jsonData.put("payerList", new JSONSerializer()
			.include("name1")
			.include("sapBpId")
			.include("sapId")
			.include("name2")
			.exclude("*"));
	}
	
	public CustorderEntity getCustorder() {
		return custOrder;
	}

	public void setCustorder(CustorderEntity custOrder) {
		this.custOrder = custOrder;
	}

	public List getShiptoList() {
		return shiptoList;
	}

	public void setShiptoList(List<WeborderShipToSoldToEntity> shiptoList) {
		this.shiptoList = shiptoList;
		HashMap singleSalesAreaShipTos = new HashMap();
		if (shiptoList != null)  {
			multipleSalesAreaShiptos = new HashMap();
			for (WeborderShipToSoldToEntity wss : shiptoList) {
				if (multipleSalesAreaShiptos.containsKey(wss.getShiptoSapBpId())) {
					continue;
				}
				if (singleSalesAreaShipTos.containsKey(wss.getShiptoSapBpId())){
					singleSalesAreaShipTos.remove(wss.getShiptoSapBpId());
					multipleSalesAreaShiptos.put(wss.getShiptoSapBpId(),wss);
				}
				else{
					singleSalesAreaShipTos.put(wss.getShiptoSapBpId(), wss);
				}
			}
		}
	}

	public String getCompositeId() {
		return compositeId;
	}

	public void setCompositeId(String compositeId) {
		this.compositeId = compositeId;
		if (!StringUtils.isEmpty(compositeId) && shiptoList != null && !shiptoList.isEmpty()) {			
			Iterator it = shiptoList.iterator();
			while (it.hasNext()) {
				WeborderShipToSoldToEntity type = (WeborderShipToSoldToEntity) it.next();
				if (type.getCompositeId().equals(compositeId)) {
					setSapSoldShip(type);
					break;
				}
			}
		}
		else{
			setSapSoldShip(null);
		}
	}

	public WeborderShipToSoldToEntity getSapSoldShip() {
		return sapSoldShip;
	}

	private void setSapSoldShip(WeborderShipToSoldToEntity sapSoldShip) {
		this.sapSoldShip = sapSoldShip;
	}

	public String getDistanceMeters() {
		return distanceMeters;
	}

	public void setDistanceMeters(String distanceMeters) {
		this.distanceMeters = distanceMeters;
	}

	public String getDurationCorrFactor() {
		return durationCorrFactor;
	}

	public void setDurationCorrFactor(String durationCorrFactor) {
		this.durationCorrFactor = durationCorrFactor;
	}

	public HashMap getMultipleSalesAreaShiptos() {
		return multipleSalesAreaShiptos;
	}


	public Map<Object, List<ShiptoSalesAreaError>> getInvalidMap() {
		return invalidMap;
	}

	
	/**
	 * Returns false in case the material is not valid - crucial information of the restriction is invalid,
	 * or missing.
	 * @return
	 */
	public boolean isMaterialValid(DeliverySettingDTO deliverySetting) {
		Map<Object, List<ShiptoSalesAreaError>> invalidMap = getInvalidMap();
		// If customer is blocked from taking orders, no warning until attempting to save the order.
		if (InvalidMapSupport.containsErrorExcept(invalidMap, shiptoSarea, ShiptoSalesAreaError.CUSTOMER_CREDIT_BLOCK)) {
			return false;
		}
		
		SapMaterialEntity sapMaterial = deliverySetting.getSapMaterial();
		if (sapMaterial != null && LogonPackageHandlingUtil.isService(sapMaterial)) {
			return true;
		}
				
		//these must be set in order to be able to use!
		if (deliverySetting.getShippingpointId() == null){
			return false;	
		}
		if (deliverySetting.getProductionPlantCd() == null) {
			return false;
		}
		if (deliverySetting.getDeliveryTypeCd() == null) {
			return false;
		}
		if (deliverySetting.getMaterialPackageCd() == null || deliverySetting.getMaterialPackageCd().equals("NULL")) {
			return false;
		}
    	boolean isShippingpointValid = true;
    	boolean isProductionPlantValid = true;
    	boolean isDeliveryTypeValid = true;
    	boolean isContractValid = true;
    	
		if(invalidMap.containsKey(deliverySetting)) {
			List<ShiptoSalesAreaError> errors = invalidMap.get(deliverySetting);
			isShippingpointValid = !errors.contains(ShiptoSalesAreaError.SHIPPINGPOINT);
			isProductionPlantValid = !errors.contains(ShiptoSalesAreaError.PRODUCTION_PLANT);
			isDeliveryTypeValid = !errors.contains(ShiptoSalesAreaError.DELIVERY_TYPE);
			isContractValid = !errors.contains(ShiptoSalesAreaError.CONTRACT_INVALID);
		}
		
		if (!isShippingpointValid || !isProductionPlantValid || !isDeliveryTypeValid || !isContractValid) {
			return false;
		}
		else{
			return true;
		}
	}


	public String getPickupTypeSelect() {
		return pickupTypeSelect;
	}

	public void setPickupTypeSelect(String pickupTypeSelect) {
		this.pickupTypeSelect = pickupTypeSelect;
	}

	public Map getBimodalRailRestrictionMap() {
		return bimodalRailRestrictionMap;
	}

	public void setBimodalRailRestrictionMap(Map bimodalRailRestrictionMap) {
		this.bimodalRailRestrictionMap = bimodalRailRestrictionMap;
	}

	public Long getBimodalShiptoSareaRestrId() {
		return bimodalShiptoSareaRestrId;
	}

	public void setBimodalShiptoSareaRestrId(Long bimodalShiptoSareaRestrId) {
		this.bimodalShiptoSareaRestrId = bimodalShiptoSareaRestrId;
	}


	public String getCustWeborderNote() {
		return custWeborderNote;
	}

	public void setCustWeborderNote(String custWeborderNote) {
		this.custWeborderNote = custWeborderNote;
	}
	
	public String getCustWeborderRefNo() {
		return custWeborderRefNo;
	}

	public void setCustWeborderRefNo(String custWeborderRefNo) {
		this.custWeborderRefNo = custWeborderRefNo;
	}

	public String getBilltoSapBpId() {
		return billtoSapBpId;
	}

	public void setBilltoSapBpId(String billtoSapBpId) {
		this.billtoSapBpId = billtoSapBpId;
	}
	
	public String getPayerSapBpId() {
		return payerSapBpId;
	}

	public void setPayerSapBpId(String payerSapBpId) {
		this.payerSapBpId = payerSapBpId;
	}

	public String getOrderedUomCd() {
		return orderedUomCd;
	}

	public void setOrderedUomCd(String orderedUomCd) {
		this.orderedUomCd = orderedUomCd;
	}
	
	public boolean getCopyCustomerRemarkToDriver(){
		return copyCustomerRemarkToDriver;
	}
	
	public void setCopyCustomerRemarkToDriver(boolean copyCustomerRemarkToDriver){
		this.copyCustomerRemarkToDriver = copyCustomerRemarkToDriver;
	}

	public void setOrderQuantityStr(String orderQuantityStr) {
		this.orderQuantityStr = orderQuantityStr;
	}

	public CustorderEntity getBimodalRailCustorder() {
		return bimodalRailCustorder;
	}

	public void setBimodalRailCustorder(CustorderEntity bimodalRailOrder) {
		this.bimodalRailCustorder = bimodalRailOrder;
	}

	public DeliverySettingSalesAreaMap getSalesAreaMap() {
		if (this.shipToMap == null || this.sarea == null || this.sarea.getSareaId() == null) {
			return null;
		}
		return this.shipToMap.getSalesAreaMapForSareaId(this.sarea.getSareaId());
	}

	public String getDeliverySettingCompositeId() {
		return deliverySettingCompositeId;
	}

	public void setDeliverySettingCompositeId(String deliverySettingCompositeId) {
		this.deliverySettingCompositeId = deliverySettingCompositeId;
	}

	public DeliverySettingDTO getDeliverySettingByCompositeIdentifier(String deliverySettingCompositeId) {
		final DeliverySettingDTO deliverySettingByCompositeIdentifier = getShipToMap().getDeliverySettingByCompositeIdentifier(
				deliverySettingCompositeId);
		return deliverySettingByCompositeIdentifier;
		
	}

	/**
	 * @param request
	 * @param locale
	 * @return
	 */
	public DeliverySettingDTO getSelectedDeliverySetting(HttpServletRequest request, Locale locale) {
		String deliverySettingCompositeId = null;
		try{			
			deliverySettingCompositeId = getDeliverySettingCompositeId();
		}
		catch (Exception e) {
			MessageUtils.addActionWarning(request,"error.field.required", MessageUtils.getMessage(request, locale, "fields.produkt.produkt"));
		}
		final DeliverySettingDTO deliverySetting = getDeliverySettingByCompositeIdentifier(deliverySettingCompositeId);
	
		if ( deliverySetting == null ) {
			MessageUtils.addActionWarning(request,"webbestellung.error.material.not.available.in.salesarea");
		}
		return deliverySetting;
	}

	public boolean getDisplayShipToTimezone() {
		return !getShipToTimeZone().isSame(UserProvider.getTimezone());
	}

	public TimeZone getShipToTimeZone() {
		if (shipToTz != null) {
			return shipToTz;
		}

		return TimeZone.getAppTimeZone();
	}

	public void setInvalidMap(Map<Object, List<ShiptoSalesAreaError>> invalidMap) {
		this.invalidMap = invalidMap;
	}

	public DeliverySettingShiptoMap getShipToMap() {
		return shipToMap;
	}

	public void setShipToMap(DeliverySettingShiptoMap shipToMap) {
		this.shipToMap = shipToMap;
	}

	public List<SplittedOrderData> getSplittedOrderDatas() {
		return splittedOrderDatas;
	}

	public void setSplittedOrderDatas(List<SplittedOrderData> splittedOrderDatas) {
		this.splittedOrderDatas = splittedOrderDatas;
	}

	public Boolean getOnHoldOrderAndLockedFlag() {
		return onHoldOrderAndLockedFlag;
	}

	public void setOnHoldOrderAndLockedFlag(Boolean onHoldOrderAndLockedFlag) {
		this.onHoldOrderAndLockedFlag = onHoldOrderAndLockedFlag;
	}

	public MaterialType getMaterialType() {
		return materialType;
	}

	public void setMaterialType(MaterialType materialType) {
		this.materialType = materialType;
	}

	public CustorderDTO getCustorderDTO() {
		return custorderDTO;
	}

	public void setCustorderDTO(CustorderDTO custorderDTO) {
		this.custorderDTO = custorderDTO;
	}

	public Set<MaterialColumn> getMaterialColumns() {
		return materialColumns;
	}

	public void setMaterialColumns(Set<MaterialColumn> materialColumns) {
		this.materialColumns = materialColumns;
	}

	/**
	 * Finds the maximum quantity value for an sdo.
	 * Inspired by adjustSdoQuantities@CustorderUtils
	 * @return
	 */
	public float getVehicleMaxLoadInOrderedUomForAdjustDeliveries(){
		float result = 0f;
		if (custOrder != null){
			List<CustorderItemEntity> custorderItems = custOrder.getCustorderItems();
			if (custorderItems != null && custorderItems.size() > 0) {
				CustorderItemEntity custorderItemEntity = custorderItems.get(0);
				if (custorderItemEntity.getExactSdorderQtyFlag()) {
					result = custorderDTO.getSplitWrapper().get_vehicleMaxLoadInOrderedUom().floatValue();
				}
				else {
					result = 1f;
				}
			}
		}

		return result;
	}

	public String getOrderedUomCdForAdjustDeliveries(){
		String result = "";

		if (splittedOrderDatas != null && splittedOrderDatas.size() > 0) {
			SplittedOrderData data = splittedOrderDatas.get(0);
			SplittedOrderItemData splittedOrderItemData = data.getSplittedOrderItemDatas().get(0);
			result = splittedOrderItemData.getOrderedUom();
		}

		return result;
	}

	public String getJsonSplittedOrders() {
		return JsonUtils.getJsonSerializer().include(
				"index",
				"deliveryFromDatStr",
				"deliveryToDatStr",
				"deliveryFromTime",
				"deliveryToTime",
				"onHoldOrderAndLockedFlag",
				"deliveryType",
				"exactCustorderQtyFlag",
				"serviceProcessIndicatorId",
				"splittedOrderItemDatas",
				"splittedOrderItemDatas.uomList.*",
				"splittedOrderItemDatas.orderedQuantity",
				"splittedOrderItemDatas.orderedUom",
				"splittedOrderItemDatas.materialType",
				"splittedOrderItemDatas.sapMaterialId",
				"splittedOrderItemDatas.orderItem.orderedUom",
				"splittedOrderItemDatas.orderItem.orderedUom.uomCd",
				"splittedOrderItemDatas.orderItem.orderedQuantity").exclude("*").serialize(splittedOrderDatas);
	}

}
