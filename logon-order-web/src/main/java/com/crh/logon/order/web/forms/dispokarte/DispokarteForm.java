/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.dispokarte;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.ordercreation.DeliverySettingSalesAreaMap;
import com.crh.logon.business.ordercreation.DeliverySettingShiptoMap;
import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.business.ordercreation.dto.ShippingpointDTO;
import com.crh.logon.business.ordercreation.dto.TruckTypeWrapper;
import com.crh.logon.business.ordercreation.dto.TruckWagonTypeWrapper;
import com.crh.logon.business.ordercreation.dto.WagonTypeWrapper;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.ShiptoSalesAreaError;
import com.crh.logon.order.web.beans.ContractItemDataBean;
import com.crh.logon.dto.DistanceValidationEditGuiBean;
import com.crh.logon.persistence.entities.common.PersonEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import com.crh.logon.persistence.entities.refdata.BilltoRuletypeBsEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.DistributionDestTxEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.ShiptoTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.SignatureTypeTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapBpSareaEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.views.SapMaterialDescEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.time.DateTimeUtils;
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
 * @struts.form name="order_dispokarte_DispokarteForm"
 */
public class DispokarteForm extends ActionForm {

	private static final long serialVersionUID = -8910104051337644756L;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DispokarteForm.class);

	public static final String FORM_NAME = "order_dispokarte_DispokarteForm";
	public static final String TAB_SHIPTO = "TabShipTo";
	public static final String TAB_SALES_AREA = "TabSalesArea";
	public static final String TAB_SHIPTO_TYPE_ROAD = "TabShipToTypeRoad";
	public static final String TAB_SHIPTO_TYPE_RAIL = "TabShipToTypeRail";
	public static final String TAB_CONTRACT_DATA = "contractData";
	public static final Long RULE_DEFINED_ID = new Long(Constant.SPECIAL_OBJECT_TWO_ID_STR);

	DeliverySettingSalesAreaMap salesAreaMap;

	//Map which contains objects and a list of codes describing the type of the error used in the tag InvalidAttributeMarker
	private Map<Object, List<ShiptoSalesAreaError>> invalidObjectInformationMap;

	// input from the user (DispokarteShipTo)
	private String searchtext;

	// input from the user (Dispokarte)
	private String formAction;

	private int selectedSalesAreaId;

	private String open1From;
	private String open1To;
	private String open2From;
	private String open2To;

	private int restrToRemove;

	private String tab;

	private String shiptoTypeTab;

	private String language;
    
	/** In the business parter entity we only have the country code. 
	 * For the first lookup in google maps we need the actual country name 
	 * to be able to have correct coordinates especially for russian and azeri adresses.  */
    private String countryForShipto;
    
	private boolean customerCentralBlocked;
	private boolean customerSalesAreaBlocked;

	private SapBpEntity soldTo;
	private SapBpSareaEntity soldToSapBpSarea;

	private int currSalesAreaIndex;

	private DeliverySettingShiptoMap shiptoMap;

	private String orderCallback;

	private long selectedShipToType;


	private List<SareaEntity> salesAreas;
	private List<ShiptoTypeTxEntity> shipToTypeList;
	private List<DeliveryTypeTxEntity> deliveryTypeList;
	// contains all delivery types; selectable delivery types are chosen from here filtered according to shipto-typ 
	private List<DeliveryTypeTxEntity> allDeliveryTypeList;
	private List<SignatureTypeTxEntity> signatureTypeList;
	private List<DistributionDestTxEntity> distributionDestTxList;	
	//list containing all plants which might appear in relation with any of the sales areas
	private List<PlantBsEntity> plantList;
	private List<TruckTypeWrapper> truckTypeWrapperList;
	private List<WagonTypeWrapper> wagonTypeWrapperList;

	private int activeIdx;

	private String productOrderNote;
	private String productDeliveryNote;
	private String productDispatchingNote;
	private String productTruckNote;
	private String productRailwayNote;

	private int selectedIndex;

	private BilltoRuletypeBsEntity defaultRuleType;


	// at save -> compare list of wagon types to this set and delete the deactivated ones from DB
	private Set<Long> wagonTypesInDB;
	private Set<Long> truckTypesInDB;

	private Map<Long, SalesAreaBean> salesAreaBeanMap;
	private Map<String, List<ShippingpointDTO>> allPlantShippingPointMap;
	private Map<Long, SapMaterialDescEntity> siloMaterialDescriptionMap;

	private long selectedContactPersonId;
	private String matChainShippingpointId;
	private String railChainShippingpointId;
	private String shipToId;
	private String soldToId;

	private long selectedSignatureTypeCd;
	private String matChainPlantCd;
	private String plantPlantCd;
	private String railChainPlantCd;

	private String materialComboSelectedCompositeIdentifier;

	private ContractItemDataBean contractItemData;

	private TimeZone shiptoTZ;
	private TimeZone userTZ;

	private boolean withActiveCemContracts;
	private boolean withActiveAggContracts;
    
    /**
     * Default constructor.
     */
    public DispokarteForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "DispokarteForm");
		}
		// ]]
		try {
	        completeInit();
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
	 * Completely re-initializes the form.
	 */
	public void completeInit() {
		init();
		tab = "";
		currSalesAreaIndex = 0;
		shiptoTypeTab = "";
		shipToId = "";
		soldToId = "";
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
			searchtext = "";
			formAction = "";
			siloMaterialDescriptionMap = new HashMap<Long, SapMaterialDescEntity>();
			deliveryTypeList = new ArrayList <DeliveryTypeTxEntity>();
			allDeliveryTypeList = new ArrayList <DeliveryTypeTxEntity>();
			salesAreaBeanMap = new HashMap<Long, SalesAreaBean>();
			selectedContactPersonId = Constant.DROPDOWN_NOT_SELECTED;
			selectedShipToType = Constant.DROPDOWN_NOT_SELECTED;
			selectedSignatureTypeCd = Constant.DROPDOWN_NOT_SELECTED;
			open1From = "";
			open1To = "";
			open2From = "";
			open2To = "";
			invalidObjectInformationMap = new HashMap<Object, List<ShiptoSalesAreaError>>();
			wagonTypesInDB = new HashSet<Long>();
			truckTypesInDB = new HashSet<Long>();
			withActiveCemContracts = false;
			withActiveAggContracts = false;
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
    		restrToRemove = Constant.DROPDOWN_NOT_SELECTED;

    		materialComboSelectedCompositeIdentifier = null;
			contractItemData = null;
    		
    		if(tab.startsWith(TAB_SALES_AREA)){
    			getCurrSalesAreaBean().reset();
    		}
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
			final ShiptoEntity shipTo = getShipTo();
			/* no need to validate if 'delete' button was pressed */
			if (request.getParameter("delete") != null) {
				return result = null;
			}
			if (errors == null) {
				errors = new ActionErrors();
			}

			if (shipTo != null && shipTo.getUnloadingTimeCemMin() != null
					&& shipTo.getUnloadingTimeCemMin().equals(0l)) {
				shipTo.setUnloadingTimeCemMin(null);
			}
			if (shipTo != null && shipTo.getUnloadingTimeAggMin() != null
					&& shipTo.getUnloadingTimeAggMin().equals(0l)) {
				shipTo.setUnloadingTimeAggMin(null);
			}
			if (shipTo != null && shipTo.getLoadingTimeLfMin() != null
					&& shipTo.getLoadingTimeLfMin().equals(0l)) {
				shipTo.setLoadingTimeLfMin(null);
			}

			/*
			 * real validation is done from DispokarteAction
			 */

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
    
    public String getContractValidity() {
    	final ShiptoSareaRestrEntity restr = getCurrSalesAreaBean().getRestrictionForSettingByIndex(activeIdx);
		SapContractItemEntity sapContractItem = restr.getSapContractItem();
		if (sapContractItem == null) {
			return "-";
		}

		StringBuilder validity = new StringBuilder();

		SapContractEntity sapContract = sapContractItem.getSapContract();
		validity.append(this.userTZ.fmtAsDayMonthYear(sapContract.getValidFromDat()))
				.append(" -");

		if (!sapContract.getValidUntilIgnoreFlag()) {
			validity.append(' ')
					.append(this.userTZ.fmtAsDayMonthYear(sapContract.getValidUntilDat()));
		}

		return validity.toString();
	}
    

	public String getSearchtext() {
		return searchtext;
	}

	public void setSearchtext(String searchtext) {
		this.searchtext = searchtext;
	}
	
	public boolean shipToInitialized(){
		return getShipTo() != null;
	}

	public ShiptoEntity getShipTo() {
		if ( getShiptoMap() == null ) {
			return null;
		}
		else {			
			return getShiptoMap().getShipto();
		}
	}

	
	public List<ShiptoSareaEntity> getShiptoSalesAreas() {
		if ( getShiptoMap() == null ) {
			return null;
		}
		else {
			
			final Map<Long, DeliverySettingSalesAreaMap> sareaMaps = shiptoMap.getSareaMaps();
		    List <ShiptoSareaEntity> shiptoSalesAreas = new ArrayList<ShiptoSareaEntity>();
			for ( DeliverySettingSalesAreaMap map : sareaMaps.values() ) {            	
				shiptoSalesAreas.add(map.getShiptoSarea());
			}
			return shiptoSalesAreas;
		}
	}


	/**
	 * to be called by entry form after shipto and all reference data has been set
	 */
	public void populateForm() {
		final ShiptoEntity shipTo = getShipTo();
		if (shipTo != null){
			
			final PersonEntity contactPerson = getShipTo().getContactPerson();
			if ( contactPerson != null ) {				
				selectedContactPersonId = contactPerson.getPersonId();
			}
			
			if ( StringUtils.empty(open1From)) {
				open1From =DateTimeUtils.formatHoursMinutes( 
						DateTimeUtils.getHoursMinutes(shipTo.getOpen1From()));
			}
			if ( StringUtils.empty(open1To)) {
				open1To =DateTimeUtils.formatHoursMinutes( 
						DateTimeUtils.getHoursMinutes(shipTo.getOpen1To()));
			}
			if ( StringUtils.empty(open2From)) {
				open2From =DateTimeUtils.formatHoursMinutes( 
						DateTimeUtils.getHoursMinutes(shipTo.getOpen2From()));
			}
			if ( StringUtils.empty(open2To)) {
				open2To =DateTimeUtils.formatHoursMinutes( 
						DateTimeUtils.getHoursMinutes(shipTo.getOpen2To()));
			}
			
			HashMap<Long, ShiptoSareaEntity> newShiptoSalesAreaMap = new HashMap<Long, ShiptoSareaEntity>();
			final List<ShiptoSareaEntity> shiptoSalesAreas = getShiptoSalesAreas();
			if (shiptoSalesAreas != null) {
				Iterator<ShiptoSareaEntity> i = shiptoSalesAreas.iterator();
				while (i.hasNext()) {
					ShiptoSareaEntity sve = i.next();
					newShiptoSalesAreaMap.put(sve.getSarea().getSareaId(), sve);
				}
			}
			if (salesAreas != null) {
				Iterator<SareaEntity> i = salesAreas.iterator();
				while (i.hasNext()) {
					SareaEntity sarea = i.next();
					ShiptoSareaEntity sve = newShiptoSalesAreaMap.get(sarea.getSareaId());
					if (sve == null) {
						// Missing sales areas should've been created in dispokarteservice.initShiptoSalesareas
						throw new LogonException("Shipto sales area not found", Reason.RES_IMPL_BUG);
					}
					getSalesAreaBean(sarea).setShiptoSarea(sve);
					DistanceValidationEditGuiBean distanceValidationGuiBeanShipto = new DistanceValidationEditGuiBean(sve);
					getSalesAreaBean(sarea).setDistanceValidationGuiBeanShipto(distanceValidationGuiBeanShipto);
					DistanceValidationEditGuiBean distanceValidationGuiBeanSarea = new DistanceValidationEditGuiBean(sve.getSarea());
					getSalesAreaBean(sarea).setDistanceValidationGuiBeanSarea(distanceValidationGuiBeanSarea);
					// add backing map for bill-tos
					getSalesAreaBean(sarea).addShiptoBilltos(sve.getBilltos());
					getSalesAreaBean(sarea).addShiptoSilos(shipTo.getSilos());
				}
			}
		}
	}

	public String getShipToId() {
		return shipToId;
	}

	public void setShipToId(String shipToId) {
		this.shipToId = shipToId;
	}

	public String getTab() {
		return tab;
	}

	public void setTab(String tab) {
		this.tab = !StringUtils.empty(tab) ? tab : null;
	}

	public String getFormAction() {
		return formAction;
	}

	public void setFormAction(String action) {
		this.formAction = action;
	}

	public int getCurrSalesAreaIndex() {
		return currSalesAreaIndex;
	}	
	
	public void setCurrSalesAreaIndex(int currSalesAreaIndex) {
		this.currSalesAreaIndex = currSalesAreaIndex;
	}

	public List<SareaEntity> getSalesAreas() {
		return salesAreas;
	}
	
	public void setSalesAreas(List<SareaEntity> salesAreas){
		this.salesAreas = salesAreas;
	}
	
    public String getShiptoTypeTab() {
		return shiptoTypeTab;
	}

	public void setShiptoTypeTab(String shiptoTypeTab) {
		this.shiptoTypeTab = shiptoTypeTab;
	}
	
	public int getSelectedSalesAreaId() {
		return selectedSalesAreaId;
	}

	public void setSelectedSalesAreaId(int selectedSalesAreaId) {
		this.selectedSalesAreaId = selectedSalesAreaId;
	}


	public String getOpen1From() {
		return open1From;
	}

	public void setOpen1From(String open1From) {
		this.open1From = open1From;
		try {
			this.open1From = DateTimeUtils.formatHoursMinutes(DateTimeUtils.parseHoursMinutes(open1From));
		}
		catch (ParseException e) {
			// do nothing
		}	
	}

	public String getOpen1To() {
		return open1To;
	}

	public void setOpen1To(String open1To) {
		this.open1To = open1To;
		try {
			this.open1To = DateTimeUtils.formatHoursMinutes(DateTimeUtils.parseHoursMinutes(open1To));
		}
		catch (ParseException e) {
			// do nothing
		}	
	}

	public String getOpen2From() {
		return open2From;
	}

	public void setOpen2From(String open2From) {
		this.open2From = open2From;
		try {
			this.open2From = DateTimeUtils.formatHoursMinutes(DateTimeUtils.parseHoursMinutes(open2From));
		}
		catch (ParseException e) {
			// do nothing
		}	
	}

	public String getOpen2To() {
		return open2To;
	}

	public void setOpen2To(String open2To) {
		this.open2To = open2To;
		try {
			this.open2To = DateTimeUtils.formatHoursMinutes(DateTimeUtils.parseHoursMinutes(open2To));
		}
		catch (ParseException e) {
			// do nothing
		}
	}

	public String getSoldToId() {
		return soldToId;
	}

	public void setSoldToId(String soldToId) {
		this.soldToId = soldToId;
	}

	public SapBpEntity getSoldTo() {
		return soldTo;
	}

	public void setSoldTo(SapBpEntity soldTo) {
		this.soldTo = soldTo;
	}
	
	public String getCountryForShipto() {
		return countryForShipto;
	}

	public void setCountryForShipto(String countryForShipto) {
		this.countryForShipto = countryForShipto;
	}

	public SalesAreaBean getSalesAreaBean(SareaEntity sarea){
		 SalesAreaBean res = salesAreaBeanMap.get(sarea.getSareaId());
		 if (res == null){
			res = new SalesAreaBean();
			res.setDefaultRuleType(defaultRuleType);
			salesAreaBeanMap.put(sarea.getSareaId(), res);
		 }
		 return res;
	}
	
	public SalesAreaBean getCurrSalesAreaBean(){
		if ((currSalesAreaIndex < 0) || (salesAreas == null) || salesAreas.isEmpty()){
			return null;
		}
		return  getSalesAreaBean(salesAreas.get(currSalesAreaIndex));
	}


	public int getRestrToRemove() {
		return restrToRemove;
	}

	public void setRestrToRemove(int restrToRemove) {
		this.restrToRemove = restrToRemove;
	}


	public long getSelectedContactPersonId() {
		return selectedContactPersonId;
	}

	public void setSelectedContactPersonId(long selectedContactPersonId) {
		this.selectedContactPersonId = selectedContactPersonId;
	}

	public List<ShiptoTypeTxEntity> getShipToTypeList() {
		return shipToTypeList;
	}

	public void setShipToTypeList(List<ShiptoTypeTxEntity> shipToTypeList) {
		this.shipToTypeList = shipToTypeList;
	}

	public String getOrderCallback() {
		return orderCallback;
	}

	public void setOrderCallback(String orderCallback) {
		this.orderCallback = orderCallback;
	}

	public long getSelectedShipToType() {
		return selectedShipToType;
	}

	public void setSelectedShipToType(long selectedShipToType) {

		fillDeliveryTypes(selectedShipToType);
		
		this.selectedShipToType = selectedShipToType;
	}

	public long getSelectedSignatureTypeCd() {
		return selectedSignatureTypeCd;
	}

	public void setSelectedSignatureTypeCd(long selectedSignatureTypeCd) {
		this.selectedSignatureTypeCd = selectedSignatureTypeCd;
	}

	public List <SignatureTypeTxEntity> getSignatureTypeList() {
		return signatureTypeList;
	}

	public void setSignatureTypeList(List <SignatureTypeTxEntity> signatureTypeList) {
		this.signatureTypeList = signatureTypeList;
	}

	public List  <DeliveryTypeTxEntity>getDeliveryTypeList() {
		return deliveryTypeList;
	}

	public void setDeliveryTypeList(List <DeliveryTypeTxEntity>deliveryTypeList) {
		this.deliveryTypeList = deliveryTypeList;
	}

	public int getActiveIdx() {
		return activeIdx;
	}

	public void setActiveIdx(int activeIdx) {
		this.activeIdx = activeIdx;
	}

	public String getProductOrderNote() {
		return productOrderNote;
	}

	public void setProductOrderNote(String productOrderNote) {
		this.productOrderNote = productOrderNote;
	}

	public String getProductDeliveryNote() {
		return productDeliveryNote;
	}

	public void setProductDeliveryNote(String productDeliveryNote) {
		this.productDeliveryNote = productDeliveryNote;
	}

	public String getProductDispatchingNote() {
		return productDispatchingNote;
	}

	public void setProductDispatchingNote(String productDispatchingNote) {
		this.productDispatchingNote = productDispatchingNote;
	}

	public String getProductTruckNote() {
		return productTruckNote;
	}

	public void setProductTruckNote(String productTruckNote) {
		this.productTruckNote = productTruckNote;
	}

	public String getProductRailwayNote() {
		return productRailwayNote;
	}

	public void setProductRailwayNote(String productRailwayNote) {
		this.productRailwayNote = productRailwayNote;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	
	public Long getRuleDefinedId() {
		return RULE_DEFINED_ID; 
	}


	public BilltoRuletypeBsEntity getDefaultRuleType() {
		return defaultRuleType;
	}

	public void setDefaultRuleType(BilltoRuletypeBsEntity defaultRuleType) {
		this.defaultRuleType = defaultRuleType;
	}
	
	/**
	 * Returns the restriction attribute of the delivery setting.
	 * @param restrIdx
	 * @return
	 */
	public DeliverySettingDTO getDeliverySettingForActiveIndex() {
		return  getCurrSalesAreaBean().getRestrictionDeliverySettings().get(activeIdx);
	}

	


	/**
	 * @return the truckTypeWrapperList
	 */
	public List<TruckTypeWrapper> getTruckTypeWrapperList() {
		return truckTypeWrapperList;
	}

	/**
	 * @param truckTypeWrapperList the truckTypeWrapperList to set
	 */
	public void setTruckTypeWrapperList(List<TruckTypeWrapper> truckTypeWrapperList) {
		this.truckTypeWrapperList = truckTypeWrapperList;
	}

	public List <WagonTypeWrapper> getWagonTypeWrapperList() {
		return wagonTypeWrapperList;
	}

	public void setWagonTypeWrapperList(List <WagonTypeWrapper> wagonTypeWrapperList) {
		this.wagonTypeWrapperList = wagonTypeWrapperList;
	}

	public void setTruckAllowance(int idx, String value){
		TruckTypeWrapper tw = truckTypeWrapperList.get(idx);
		if ( (tw.getPriority() < 0) && "false".equals(value) ) {
			tw.setPriority(0); 
		}
		else if ( (tw.getPriority() == 0) && "true".equals(value) ) {
			tw.setPriority(-1); 
		} 
		else if ( (tw.getPriority() > 0) && "false".equals(value) ) {
			tw.setPriority(0); 
		}
	}
	
	public String getTruckAllowance(int idx){
		String result = null; 
		TruckTypeWrapper tw = truckTypeWrapperList.get(idx);
		if ( (tw.getPriority() < 0)) {
			result = "true"; 
		}
		else if ( (tw.getPriority() == 0)) {
			result = "false"; 
		} 
		else if ( (tw.getPriority() > 0)) {
			result = "true"; 
		}
		return result;
	}
	
	public void setTruckPrio(int idx, String value) {
		TruckTypeWrapper tw = truckTypeWrapperList.get(idx);
		if ( StringUtils.empty(value) ) {
			tw.setPriority(-1);
		}
		else if ("-".equals(value)) {
			tw.setPriority(0);
		}
		else {
			tw.setPriority(Long.parseLong(value));
		}
	}
	
	public String getTruckPrio(int idx) {
		TruckTypeWrapper tw = truckTypeWrapperList.get(idx);
		if ( tw.getPriority() == -1) {
			return "";
		}
		else if ( tw.getPriority() == 0) {
			return "-";
		}
		else {
			return String.valueOf(tw.getPriority());
		}
	}
	
	
	public void setWagonAllowance(int idx, String value){
		WagonTypeWrapper tw = wagonTypeWrapperList.get(idx);
		if ( (tw.getPriority() < 0) && "false".equals(value) ) {
			tw.setPriority(0); 
		}
		else if ( (tw.getPriority() == 0) && "true".equals(value) ) {
			tw.setPriority(-1); 
		} 
		else if ( (tw.getPriority() > 0) && "false".equals(value) ) {
			tw.setPriority(0); 
		}
	}
	
	public String getWagonAllowance(int idx){
		String result = null; 
		WagonTypeWrapper tw = wagonTypeWrapperList.get(idx);
		if ( (tw.getPriority() < 0)) {
			result = "false"; 
		}
		else if ( (tw.getPriority() == 0)) {
			result = "false"; 
		} 
		else if ( (tw.getPriority() > 0)) {
			result = "true"; 
		}
		return result;
	}
	
	public void setWagonPrio(int idx, String value) {
		WagonTypeWrapper tw = wagonTypeWrapperList.get(idx);
		if ( StringUtils.empty(value) ) {
			tw.setPriority(-1);
		}
		else if ("-".equals(value)) {
			tw.setPriority(0);
		}
		else {
			tw.setPriority(Long.parseLong(value));
		}
	}
	
	public String getWagonPrio(int idx) {
		WagonTypeWrapper tw = wagonTypeWrapperList.get(idx);
		if ( tw.getPriority() == -1) {
			return "";
		}
		else if ( tw.getPriority() == 0) {
			return "-";
		}
		else {
			return String.valueOf(tw.getPriority());
		}
	}


	public String getMatChainShippingpointId() {
		return matChainShippingpointId;
	}

	public void setMatChainShippingpointId(String matChainShippingpointId) {
		this.matChainShippingpointId = matChainShippingpointId;
	}

	public String getRailChainShippingpointId() {
		return railChainShippingpointId;
	}

	public void setRailChainShippingpointId(String railChainShippingpointId) {
		this.railChainShippingpointId = railChainShippingpointId;
	}

	public String getMatChainPlantCd() {
		return matChainPlantCd;
	}

	public void setMatChainPlantCd(String matChainPlantCd) {
		this.matChainPlantCd = matChainPlantCd;
	}
	
	public String getPlantPlantCd() {
		return plantPlantCd;
	}

	public void setPlantPlantCd(String plantPlantCd) {
		this.plantPlantCd = plantPlantCd;
	}

	public String getRailChainPlantCd() {
		return railChainPlantCd;
	}

	public void setRailChainPlantCd(String railChainPlantCd) {
		this.railChainPlantCd = railChainPlantCd;
	}

	public Map<String, List<ShippingpointDTO>> getAllPlantShippingPointMap() {
		return allPlantShippingPointMap;
	}

	public void setAllPlantShippingPointMap(
			Map<String, List<ShippingpointDTO>> allPlantShippingPointMap) {
		this.allPlantShippingPointMap = allPlantShippingPointMap;
	}
	
	public List<ShippingpointDTO> getShippingpointListForMatChainPlant() {
		final ShiptoEntity shipTo = getShipTo();
		List<ShippingpointDTO> result = new ArrayList<ShippingpointDTO>();
		List<ShippingpointDTO> shippingPoints = allPlantShippingPointMap.get(matChainPlantCd);
		if ( StringUtils.empty(matChainPlantCd) || Constant.INVALID_OBJECT_ID_STR.equals(matChainPlantCd) || shippingPoints == null){
			return result;
		}
		long shiptoType = getSelectedShipToType();
		for (ShippingpointDTO shippinPoint : shippingPoints) {
			long shippingPointType = shippinPoint.getShipppointTypeCd().longValue();
			if ((shiptoType == Constant.SHIPTO_TYPE_FVL && shippingPointType == Constant.SHIPPINGPOINT_TYPE_FVL_BIMODAL_TRUCK)
					|| (shiptoType == Constant.SHIPTO_TYPE_CONTAINER_TERMINAL && shippingPointType == Constant.SHIPPINGPOINT_TYPE_CONTAINER_TERMINAL_RAIL)
					|| (shiptoType == Constant.SHIPTO_TYPE_CONTAINER_TERMINAL && shippingPointType == Constant.SHIPPINGPOINT_TYPE_CONTAINER_TERMINAL_TRUCK)) {
				result.add(shippinPoint);
			}
			else if (getDisplayRailChainSelection() && shippinPoint.getRailwayStationId() != null) {
				long stationId = shipTo.getShiptoSapBp().getRailwayStation().getRailwayStationId().longValue();
				if (stationId == shippinPoint.getRailwayStationId().longValue()) {
					result.add(shippinPoint);
				}
			}
		}
		return result;
	}
	
	public List<ShippingpointDTO> getShippingpointListForRailChainPlant() {
		List<ShippingpointDTO> result = new ArrayList<ShippingpointDTO>();
		final ShiptoEntity shipTo = getShipTo();
		List<ShippingpointDTO> shippingPoints = allPlantShippingPointMap.get(railChainPlantCd);
		if ( StringUtils.empty(railChainPlantCd) || Constant.INVALID_OBJECT_ID_STR.equals(railChainPlantCd) || shippingPoints == null){
			return result;
		}

		for (ShippingpointDTO shippingPoint : shippingPoints) {
			if (getDisplayRailChainSelection() && shippingPoint.getRailwayStationId() != null) {
				long stationId = shipTo.getShiptoSapBp().getRailwayStation().getRailwayStationId().longValue();
				if (stationId == shippingPoint.getRailwayStationId().longValue()) {
					result.add(shippingPoint);
				}
			}
		}
		return result;
		
	}
	
	public boolean getDisplayRailChainSelection() {
		final ShiptoEntity shipTo = getShipTo();
		return shipTo.getShiptoSapBp().getRailwayStation() == null ? false : true;
	}
	
	public boolean getDisplayMatChainSelection() {
		return Constant.SHIPTO_TYPE_FVL == selectedShipToType || Constant.SHIPTO_TYPE_CONTAINER_TERMINAL == selectedShipToType;
	}
	
	public boolean getPlantSelection() {
		return Constant.SHIPTO_TYPE_PLANT == selectedShipToType;
	}	
	
	public boolean getRailHoldBackReOrderFlagEditable () {
		final ShiptoEntity shipTo = getShipTo();
		long shiptoType = shipTo.getShiptoType().getShiptoTypeCd().longValue();
		if ( shiptoType == Constant.SHIPTO_TYPE_FVL || shiptoType == Constant.SHIPTO_TYPE_CONTAINER_TERMINAL ) {
			return true;
		}
		return false;
	}
	
    public boolean isValidShiptoCoords() {
    	final ShiptoEntity shipTo = getShipTo();
    	return shipTo.getCoordLat() != null && shipTo.getCoordLon() != null 
    	&& !shipTo.getCoordLat().equals(Constant.DEFAULT_COORD_LAT) && !shipTo.getCoordLon().equals(Constant.DEFAULT_COORD_LON);
    }

    public void setPlantList(List<PlantBsEntity> plantList) {
        this.plantList = plantList;
    }

    public List <PlantBsEntity>getPlantList() {
        return plantList;
    }
	
	public Map<Object, List<ShiptoSalesAreaError>> getInvalidObjectInformationMap() {
		return invalidObjectInformationMap;
	}

	public void setInvalidObjectInformationMap(Map<Object, List<ShiptoSalesAreaError>> invalidObjectInformationMap) {
		this.invalidObjectInformationMap = invalidObjectInformationMap;
	}

	public Set<Long> getWagonTypesInDB() {
		return wagonTypesInDB;
	}

	public void setWagonTypesInDB(Set <Long> wagonTypesInDB) {
		this.wagonTypesInDB = wagonTypesInDB;
	}
	
	public Set <Long> getTruckTypesInDB() {
		return truckTypesInDB;
	}

	public void setTruckTypesInDB(Set <Long> truckTypesInDB) {
		this.truckTypesInDB = truckTypesInDB;
	}
	
	public List  <DeliveryTypeTxEntity> getAllDeliveryTypeList() {
		return allDeliveryTypeList;
	}

	public void setAllDeliveryTypeList(List  <DeliveryTypeTxEntity> allDeliveryTypeList) {
		this.allDeliveryTypeList = allDeliveryTypeList;
	}
	
	public void fillDeliveryTypes(long selectedShipToType) {
		// filter delivery types according to the selected shiptotype
		deliveryTypeList = new ArrayList<DeliveryTypeTxEntity>();
		ListIterator<DeliveryTypeTxEntity> dtIt = allDeliveryTypeList.listIterator();
		while (dtIt.hasNext()) {
			DeliveryTypeTxEntity dtTxEntity = dtIt.next();
			long typeCd = dtTxEntity.getDeliveryTypeTxPK().getDeliveryTypeBs().getDeliveryTypeCd().longValue();
			if ( Constant.DELIVERY_TYPE_PICKUP == typeCd &&
					// NOTE: ADN52225: pickup is selectable in the restriction even is the "Pickup possible" 
					// checkbox is unchecked. Order Taking will just ignore the value anyway in these cases. 
					//shipTo.getSelfServiceFlag() && 
					(Constant.SHIPTO_TYPE_STANDARD  == selectedShipToType || Constant.SHIPTO_TYPE_PLANT == selectedShipToType)) {
				deliveryTypeList.add(dtTxEntity);
			}
			else if ( Constant.DELIVERY_TYPE_ROAD_DIRECT == typeCd &&
					(Constant.SHIPTO_TYPE_STANDARD  == selectedShipToType || Constant.SHIPTO_TYPE_CONTAINER_TERMINAL  == selectedShipToType || Constant.SHIPTO_TYPE_PLANT == selectedShipToType)) {
				deliveryTypeList.add(dtTxEntity);
			}
			else if ( Constant.DELIVERY_TYPE_BIMODAL_ROAD == typeCd && 
					Constant.SHIPTO_TYPE_STANDARD  == selectedShipToType || Constant.SHIPTO_TYPE_PLANT == selectedShipToType) {
				deliveryTypeList.add(dtTxEntity);
			}
			else if ( Constant.DELIVERY_TYPE_BIMODAL_RAIL == typeCd && 
					Constant.SHIPTO_TYPE_FVL  == selectedShipToType) {
				deliveryTypeList.add(dtTxEntity);
			}
			else if ( Constant.DELIVERY_TYPE_RAIL_DIRECT == typeCd &&
					(Constant.SHIPTO_TYPE_STANDARD  == selectedShipToType || Constant.SHIPTO_TYPE_CONTAINER_TERMINAL  == selectedShipToType || Constant.SHIPTO_TYPE_MAINTENANCE == selectedShipToType || Constant.SHIPTO_TYPE_PLANT == selectedShipToType) ) {
				deliveryTypeList.add(dtTxEntity);
			}
			else if (Constant.SHIPTO_TYPE_LOCATION_NOT_FIXED  == selectedShipToType) {
				deliveryTypeList.add(dtTxEntity);
			}
		}
	}
	
	
	/**
	 * Sets the truck and wagon types of the form and collects the type codes in to fields  
	 */
	public void handleTruckWagonTypes() {
		
		// Use a local comparator type for text based sorting not the TruckWagonTypeWrapper's own sorting
		Comparator<TruckWagonTypeWrapper> truckWagonTypeTextComparator = new Comparator<TruckWagonTypeWrapper>() {
			@Override
			public int compare(TruckWagonTypeWrapper o1, TruckWagonTypeWrapper o2) {
				return o1.getTypeName().compareTo(o2.getTypeName());
			}
		};
		
		final List<TruckTypeWrapper> truckTypeWrappers = getShiptoMap().getTruckTypeWrappers();
		Collections.sort(truckTypeWrappers, truckWagonTypeTextComparator);
		setTruckTypeWrapperList(truckTypeWrappers);
		
		final List<WagonTypeWrapper> wagonTypeWrappers = getShiptoMap().getWagonTypeWrappers();
		Collections.sort(wagonTypeWrappers, truckWagonTypeTextComparator);
		setWagonTypeWrapperList(wagonTypeWrappers);
		
		for ( WagonTypeWrapper wrapper : wagonTypeWrappers ) {
			if ( wrapper.getShiptoWagonTypeEntity() != null && wrapper.getShiptoWagonTypeEntity().getShiptoWagonTypeId() != null ) {
				wagonTypesInDB.add(wrapper.getTypeCd());
			}
		}
		
		for ( TruckTypeWrapper wrapper : truckTypeWrappers ) {
			if ( wrapper.getShiptoTruckTypeEntity() != null && wrapper.getShiptoTruckTypeEntity().getShiptoTruckTypeId() != null ) {
				truckTypesInDB.add(wrapper.getTypeCd());
			}
		}
	}

	public boolean isTreatAsOneTime() {
		// [[
		boolean result = false;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "isTreatAsOneTime");
		}
		// ]]
		try {
			final ShiptoEntity shipTo = getShipTo();
			SapBpEntity shiptoSapBp = shipTo.getShiptoSapBp();
			return result = shiptoSapBp.getTreatAsOnetimeFlag();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}


	public void setSalesAreaBeanMap(Map <Long, SalesAreaBean>salesAreaBeanMap) {
		this.salesAreaBeanMap = salesAreaBeanMap;
	}

	public String getMaterialComboSelectedCompositeIdentifier() {
		return materialComboSelectedCompositeIdentifier;
	}

	public void setMaterialComboSelectedCompositeIdentifier(
			String materialComboSelectedCompositeIdentifier) {
		this.materialComboSelectedCompositeIdentifier = materialComboSelectedCompositeIdentifier;
	}

	public DeliverySettingSalesAreaMap getSalesAreaMap() {
		return salesAreaMap;
	}

	public void setSalesAreaMap(DeliverySettingSalesAreaMap salesAreaMap) {
		this.salesAreaMap = salesAreaMap;
	}

	public ContractItemDataBean getContractItemData() {
		return contractItemData;
	}

	public void setContractItemData(ContractItemDataBean contractItemData) {
		this.contractItemData = contractItemData;
	}

	public DeliverySettingDTO getWrappedDeliverySettingForActiveIdx() {
		return getCurrSalesAreaBean().getWrappedDeliverySettingForIdx(activeIdx);
	}

	public Map<Long, SapMaterialDescEntity> getSiloMaterialDescriptionMap() {
		return siloMaterialDescriptionMap;
	}

	public void setSiloMaterialDescriptionMap(
			Map<Long, SapMaterialDescEntity> siloMaterialDescriptionMap) {
		this.siloMaterialDescriptionMap = siloMaterialDescriptionMap;
	}

	public List<DistributionDestTxEntity> getDistributionDestTxList() {
		return distributionDestTxList;
	}

	public void setDistributionDestTxList(List<DistributionDestTxEntity> distributionDestTxList) {
		this.distributionDestTxList = distributionDestTxList;
	}
	
    public DeliverySettingShiptoMap getShiptoMap() {
		return shiptoMap;
	}

	public void setShiptoMap(DeliverySettingShiptoMap shiptoMap) {
		this.shiptoMap = shiptoMap;
	}

	public void setShipToTimeZone(TimeZone timezone) {
		this.shiptoTZ = timezone;
	}

	public TimeZone getShipToTimeZone() {
		return this.shiptoTZ;
	}

	public void setUserTimeZone(TimeZone timezone) {
		this.userTZ = timezone;
	}

	public boolean getDisplayShipToOpeningHoursTimezone() {
		return !this.shiptoTZ.isSame(this.userTZ);
	}

	public SapBpSareaEntity getSoldToSapBpSarea() {
		return soldToSapBpSarea;
	}

	public void setSoldToSapBpSarea(SapBpSareaEntity soldToSapBpSarea) {
		this.soldToSapBpSarea = soldToSapBpSarea;
	}

	public boolean getCustomerCentralBlocked() {
		return customerCentralBlocked;
	}

	public void setCustomerCentralBlocked(boolean customerCentralBlocked) {
		this.customerCentralBlocked = customerCentralBlocked;
	}

	public boolean getCustomerSalesAreaBlocked() {
		return customerSalesAreaBlocked;
	}

	public void setCustomerSalesAreaBlocked(boolean customerSalesAreaBlocked) {
		this.customerSalesAreaBlocked = customerSalesAreaBlocked;
	}

	public boolean isWithActiveCemContracts() {
		return withActiveCemContracts;
	}

	public void setWithActiveCemContracts(boolean withActiveCemContracts) {
		this.withActiveCemContracts = withActiveCemContracts;
	}

	public boolean isWithActiveAggContracts() {
		return withActiveAggContracts;
	}

	public void setWithActiveAggContracts(boolean withActiveAggContracts) {
		this.withActiveAggContracts = withActiveAggContracts;
	}
}
