/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.auftrag;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.crh.logon.business.services.utils.TimeZone;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.OrderSpecialUtils;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.OrderRepEntity;
import com.crh.logon.persistence.entities.common.OrderRepMaintTypeEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.refdata.MaintTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.views.SapMatDescPackEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.MessageUtils;

import flexjson.JSONSerializer;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="order_auftrag_OrderSpecialForm"
 */
public class OrderSpecialForm extends ActionForm {

	private static final long serialVersionUID = -8910104051337644556L;
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(OrderSpecialForm.class);
    public static final String SESSION_ORDERSPECIALFORM_NAME = "order_auftrag_OrderSpecialForm";
    
    public static int SELECTION_MATERIAL_LOAD_CURRENT = 1;
    public static int SELECTION_MATERIAL_LOAD_PREVIOUS = 2;
    public static int SELECTION_MATERIAL_LOAD_EMPTY = 3;
    public static int SELECTION_MATERIAL_LOAD_ANY = 4;
    
    private String formAction;
	private Locale locale;
	private String wagonNo;
	private WagonEntity wagon;

	public static final String G1 = "G1.0";
	
	//entities to create
	private OrderEntity order;
	private OrderItemEntity orderItem;
	private OrderRepEntity orderRep;
	private OrderRepMaintTypeEntity orderRepMaintType;
	
	//main section
	private Long osShiptoSapBpId;
	private Long osBilltoSapBpId;
	private Long osSoldtoSapBpId;
	private Long osSareaId;
	private String distDestCd;
	private String orderSpecialDelDat;
	private String plantCd;
	private String plantName;
	private String plantRailwayStationCd;
	private Long shippingpointId;
	private String shippingpointName;
	private String prodPlantCd;	
	private Long sapMaterialId;
	private Long carrierId;
	private String carrierDisplayName;
	
	//material/load
	private Integer materialLoad;
	private Map<String, SapMatDescPackEntity> emptyMaterialMap;
	private PlantBsEntity defaultServicePlant;
	private LoadEntity currLoad;
	private String currLoadQty;
	private String currLoadMatDesc;
	private String currLoadPlantName;
	private LoadEntity preLoad;
	private String preLoadQty;
	private String preLoadMatDesc;
	private String preLoadPlantName;
	private String contactPerson;
	private String quantity;
	private String quantityUom;
	private Map uomBsEntityMap = new LinkedHashMap();
	
	private List damageCodeTxList = new ArrayList();
	private Long damageCode;
	
	//maint. type list
	private Map maintTypeTxEntityMap = new LinkedHashMap();
	private List maintTypeBsEntityList = new LinkedList();
	private Boolean maintTypeReturnFlag;
	
	//offer invoice
	private Boolean offerDesiredFlag;

	
	//email
	private Boolean emailSendFlag;
	private String emailTo;
	private String emailCc;
	private String emailBcc;
	private String emailText;

	private OrderDetailUploadAttachmentBean uploadBean;
	
	// used to print an error message if a file upload had been failed
	private Boolean lastUploadFailed = false;
	private String lastUnsupportedFileExtension = "";

	private TimeZone timezone;

	public void setTimeZone(TimeZone timezone) {
		this.timezone = timezone;
	}
	
	public List getOrderRepMaintTypeList() {
		return orderRep.getOrderRepMaintTypes();
	}	

	public Integer getMaterialLoad() {
		return materialLoad;
	}

	public void setMaterialLoad(Integer materialLoad) {
		this.materialLoad = materialLoad;
	}

	
	public String getProdPlantCd() {
		return prodPlantCd;
	}

	public void setProdPlantCd(String prodPlantCd) {
		this.prodPlantCd = prodPlantCd;
	}

	public Long getSapMaterialId() {
		return sapMaterialId;
	}

	public void setSapMaterialId(Long sapMaterialId) {
		this.sapMaterialId = sapMaterialId;
	}
	
	public String getPlantCd() {
		return plantCd;
	}

	public void setPlantCd(String plantCd) {
		this.plantCd = plantCd;
	}

	public Long getShippingpointId() {
		return shippingpointId;
	}

	public void setShippingpointId(Long shippingpointId) {
		this.shippingpointId = shippingpointId;
	}
	
	public String getShippingpointName() {
		return shippingpointName;
	}

	public void setShippingpointName(String shippingpointName) {
		this.shippingpointName = shippingpointName;
	}

	public Long getOsShiptoSapBpId() {
		return osShiptoSapBpId;
	}

	public void setOsShiptoSapBpId(Long osShiptoSapBpId) {
		this.osShiptoSapBpId = osShiptoSapBpId;
	}

	public Long getOsBilltoSapBpId() {
		return osBilltoSapBpId;
	}

	public void setOsBilltoSapBpId(Long osBilltoSapBpId) {
		this.osBilltoSapBpId = osBilltoSapBpId;
	}

	public Long getOsSoldtoSapBpId() {
		return osSoldtoSapBpId;
	}

	public void setOsSoldtoSapBpId(Long osSoldtoSapBpId) {
		this.osSoldtoSapBpId = osSoldtoSapBpId;
	}

	public Long getOsSareaId() {
		return osSareaId;
	}

	public void setOsSareaId(Long osSareaId) {
		this.osSareaId = osSareaId;
	}
	
	public String getOrderSpecialDelDat() {
		if (orderSpecialDelDat == null) {
			orderSpecialDelDat = timezone.fmtAsDayMonthYear(new Date());
		}
		return orderSpecialDelDat;
	}

	public void setOrderSpecialDelDat(String orderSpecialDelDat) {
		this.orderSpecialDelDat = orderSpecialDelDat;
	}

	public String getDistDestCd() {
		return distDestCd;
	}

	public void setDistDestCd(String distDestCd) {
		this.distDestCd = distDestCd;
	}
	public OrderItemEntity getOrderItem() {
		return orderItem;
	}
	
	public void setOrderItem(OrderItemEntity orderItem) {
		this.orderItem = orderItem;
	}
	
	public OrderRepEntity getOrderRep() {
		return orderRep;
	}
	
	public OrderRepMaintTypeEntity getOrderRepMaintType() {
		return orderRepMaintType;
	}
	
	public void setOrderRepMaintType(OrderRepMaintTypeEntity orderRepMaintType) {
		this.orderRepMaintType = orderRepMaintType;
	}
	
	public String getCurrLoadQty() {
		return currLoadQty;
	}
	
	public void setCurrLoadQty(String currLoadQty) {
		this.currLoadQty = currLoadQty;
	}
	
	public String getPreLoadQty() {
		return preLoadQty;
	}
	
	public void setPreLoadQty(String preLoadQty) {
		this.preLoadQty = preLoadQty;
	}
	
	
	public String getCurrLoadPlantName() {
		return currLoadPlantName;
	}
	
	public void setCurrLoadPlantName(String currLoadPlantName) {
		this.currLoadPlantName = currLoadPlantName;
	}
	
	public String getPreLoadPlantName() {
		return preLoadPlantName;
	}
	
	public void setPreLoadPlantName(String preLoadPlantName) {
		this.preLoadPlantName = preLoadPlantName;
	}
	
	public Long getEmptyWagonMaterialId() {
		SapMatDescPackEntity emptyMaterialDescriptionEntity = getEmptyWagonSapMatDesc();
		return emptyMaterialDescriptionEntity != null ? emptyMaterialDescriptionEntity.getSapMaterialId() : 0;
	}
	
	public String getEmptyWagonMaterialNo() {
		String distDest = getDistDestCd();
		String emptySapMaterial = OrderSpecialUtils.getEmptySapMaterialNo(distDest, orderRep, this.getWagon());
		return emptySapMaterial != null ? emptySapMaterial : "";
	}
	
	public String getEmptyWagonMatDesc() {
		SapMatDescPackEntity sapMaterialDescriptionEntity = getEmptyWagonSapMatDesc();
		if (sapMaterialDescriptionEntity != null ) {
			return sapMaterialDescriptionEntity.getTxt();
		}
		else {
			return "n.a.";
		}
	}

	/**
	 * @return
	 */
	private SapMatDescPackEntity getEmptyWagonSapMatDesc() {
		String materialNo = getEmptyWagonMaterialNo();
		SapMatDescPackEntity sapMaterialDescriptionEntity = getEmptyMaterialDescriptionEntity(materialNo);
		return sapMaterialDescriptionEntity;
	}

	public String getJsonEmptyMaterialList(){
		List list = new ArrayList();
		list.addAll(emptyMaterialMap.values());
		return new JSONSerializer().
		include("txt").
		include("sapMaterialDescriptionPK.sapMaterialId").
		exclude("*").serialize(list);
	}

	public SapMatDescPackEntity getEmptyMaterialDescriptionEntity(String materialNo) {
		SapMatDescPackEntity sapMaterialDescriptionEntity = emptyMaterialMap.get(materialNo);
		return sapMaterialDescriptionEntity;
	}
	

	public LoadEntity getPreLoad() {
		return preLoad;
	}
	
	public void setPreLoad(LoadEntity preLoad) {
		this.preLoad = preLoad;
	}
	
	public String getPreLoadMatDesc() {
		return preLoadMatDesc;
	}
	
	public void setPreLoadMatDesc(String preLoadMatDesc) {
		this.preLoadMatDesc = preLoadMatDesc;
	}

	public LoadEntity getCurrLoad() {
		return currLoad;
	}
	
	public void setCurrLoad(LoadEntity currLoad) {
		this.currLoad = currLoad;
	}
	
	public String getCurrLoadMatDesc() {
		return currLoadMatDesc;
	}
	
	public void setCurrLoadMatDesc(String currLoadMatDesc) {
		this.currLoadMatDesc = currLoadMatDesc;
	}

	public Boolean getEmailSendFlag() {
		return emailSendFlag;
	}

	public void setEmailSendFlag(Boolean emailSendFlag) {
		this.emailSendFlag = emailSendFlag;
	}

	public String getEmailTo() {
		return emailTo;
	}

	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}

	public String getEmailCc() {
		return emailCc;
	}

	public void setEmailCc(String emailCc) {
		this.emailCc = emailCc;
	}

	public String getEmailBcc() {
		return emailBcc;
	}

	public void setEmailBcc(String emailBcc) {
		this.emailBcc = emailBcc;
	}

	public String getEmailText() {
		return emailText;
	}

	public void setEmailText(String emailText) {
		this.emailText = emailText;
	}


	public Boolean getOfferDesiredFlag() {
		return offerDesiredFlag;
	}

	public void setOfferDesiredFlag(Boolean offerDesiredFlag) {
		this.offerDesiredFlag = offerDesiredFlag;
	}

	public Boolean getMaintTypeReturnFlag() {
		return maintTypeReturnFlag;
	}

	public void setMaintTypeReturnFlag(Boolean maintTypeReturnFlag) {
		this.maintTypeReturnFlag = maintTypeReturnFlag;
	}

	public boolean getMaintTypeBs(int index) {
		final MaintTypeBsEntity maintTypeBs = (MaintTypeBsEntity) maintTypeBsEntityList.get(index);
		if (maintTypeBs.getMaintTypeCd().equals(G1)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void setMaintTypeBs(int index, boolean value) {
		OrderRepMaintTypeEntity orderRepType = new 	OrderRepMaintTypeEntity();
		orderRepType.setMaintType((MaintTypeBsEntity) maintTypeBsEntityList.get(index));
		orderRepType.setOrderRep(orderRep);
		orderRep.getOrderRepMaintTypes().add(orderRepType);
	}

	public List getMaintTypeBsEntityList() {
		return maintTypeBsEntityList;
	}

	public void setMaintTypeBsEntityList(List maintTypeBsEntityList) {
		this.maintTypeBsEntityList = maintTypeBsEntityList;
	}

	public Map getMaintTypeTxEntityMap() {
		return maintTypeTxEntityMap;
	}
	
	public void setMaintTypeTxEntityMap(Map maintTypeTxEntityMap) {
		this.maintTypeTxEntityMap = maintTypeTxEntityMap;
	}

	public String getQuantity() {
		return quantity;
	}
	
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	
	public String getQuantityUom() {
		return quantityUom;
	}
	
	public void setQuantityUom(String quantityUom) {
		this.quantityUom = quantityUom;
	}
	
	public Map getUomBsEntityMap() {
		return uomBsEntityMap;
	}
	
	public void setUomBsEntityMap(Map uomBsEntityMap) {
		this.uomBsEntityMap = uomBsEntityMap;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public WagonEntity getWagon() {
		return wagon;
	}
	
	public void setWagon(WagonEntity wagon) {
		this.wagon = wagon;
	}
	
	public String getWagonNo() {
		return wagonNo;
	}

	public void setWagonNo(String wagonNo) {
		this.wagonNo = wagonNo;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public OrderEntity getOrder() {
		return order;
	}

	public void setOrder(OrderEntity order) {
		this.order = order;
	}

	public String getFormAction() {
		return formAction;
	}

	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}

	
	/**
	 * Default constructor.
	 */
	public OrderSpecialForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "OrderSpecialForm");
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
			//reset 
		    formAction = "";
			locale = null;
			wagonNo = "";
			wagon = null;
			
			//entities to create
			order = new OrderEntity(); // special rail order
			order.setArchivedFlag(false);
			orderItem  = new OrderItemEntity();
			orderRep = new OrderRepEntity();
			orderRep.setOrderRepMaintTypes(new ArrayList());
			orderRep.setDamageCdList(new ArrayList());
			orderRep.setOrderRepAttachments(new ArrayList());
			orderRepMaintType = new OrderRepMaintTypeEntity();
			
			//main section
			osShiptoSapBpId = null;
			osBilltoSapBpId = null;
			osSoldtoSapBpId = null;
			osSareaId = null;
			distDestCd = null;
			orderSpecialDelDat = null;
			plantCd = null;
			shippingpointId = null;
			shippingpointName = null;
			prodPlantCd = null;	
			sapMaterialId = null;
			plantName = null;
			plantRailwayStationCd = null;
			carrierId = null;
			carrierDisplayName = null;
			
			//material/load
			materialLoad = 0;
			currLoad  = null;
			currLoadQty  = null;
			currLoadMatDesc = null;
			currLoadPlantName = null;
			preLoad = null;
			preLoadQty = null;
			preLoadMatDesc = null;
			preLoadPlantName = null;
			contactPerson = null;
			quantity = null;
			quantityUom = null;
			uomBsEntityMap = new LinkedHashMap();
			emptyMaterialMap = new HashMap();
			defaultServicePlant = null;
			
			//maint. type list
			maintTypeTxEntityMap = new LinkedHashMap();
			maintTypeBsEntityList = new LinkedList();
			maintTypeReturnFlag = false;
			
			//offer invoice
			offerDesiredFlag = false;
			
			//email
			emailSendFlag = false;
			emailTo = null;
			emailCc = null;
			emailBcc = null;
			emailText = null;
			uploadBean = new OrderDetailUploadAttachmentBean();
			setDamageCodeTxList(new ArrayList());
			
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
			
			setEmailSendFlag(false);
			setOfferDesiredFlag(false);
			//setInvoicePaidFlag(false);
			setMaintTypeReturnFlag(false);
			orderRep.setDocumentationAcceptFlag(Boolean.FALSE);
			orderRep.getOrderRepMaintTypes().clear();
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	public void validateOrderSpecial(HttpServletRequest request ) {
		// [[
		Object[][] params = {{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validateOrderSpecial", params);
		}
		// ]]
		try {
			
			order = new OrderEntity(); // special rail order
			order.setArchivedFlag(false);
			orderItem = new OrderItemEntity();
			
			if (shippingpointId == 0)  {
				MessageUtils.addActionWarning(request, "orderspecial.errors.shpoint.required");
			}
			if ((osShiptoSapBpId == 0) ||  (osSoldtoSapBpId == 0) || (osSareaId == 0)){
				MessageUtils.addActionWarning(request, "orderspecial.errors.shipto.sarea.required");
			}
			if (osBilltoSapBpId == 0) {
				MessageUtils.addActionWarning(request, "orderspecial.errors.billto.required");
			}
			if (carrierId == 0) {
				MessageUtils.addActionWarning(request, "orderspecial.errors.carrier.required");
			}
			
			if (StringUtils.empty(distDestCd)) {
				MessageUtils.addActionWarning(request, "orderspecial.errors.dist.dest.required");
			}
			
			//current load
			if (materialLoad == SELECTION_MATERIAL_LOAD_CURRENT) {
				if (currLoad != null) {
					orderItem.setProductionPlant(currLoad.getProductionPlant());
					orderItem.setSapMaterial(currLoad.getSapMaterial());
					
					if ( currLoad.getNetQuantity() != null ) {
						orderItem.setOrderedQuantity(currLoad.getNetQuantity());
						orderItem.setOrderedUom(currLoad.getNetQuantityUom());
					}
				}
				else {
					MessageUtils.addActionWarning(request, "orderspecial.errors.curr.load.required");
				}
				
			}
			//previous load
			else if (materialLoad == SELECTION_MATERIAL_LOAD_PREVIOUS) {
				if (preLoad != null) {
					orderItem.setProductionPlant(preLoad.getProductionPlant());
					orderItem.setSapMaterial(preLoad.getSapMaterial());
					
					try {
						final BigDecimal qty = new BigDecimal(quantity);
						orderItem.setOrderedQuantity(qty);
						orderItem.setOrderedUom(preLoad.getNetQuantityUom());
					} 
					catch (NumberFormatException e) {
						MessageUtils.addActionWarning(request, "orderspecial.errors.quantity.format");
					}
				}
				else {
					MessageUtils.addActionWarning(request, "orderspecial.errors.prev.load.required");
				}
			}
			//empty wagon
			else if (materialLoad == SELECTION_MATERIAL_LOAD_EMPTY) {
				if (StringUtils.empty(prodPlantCd)) {
					MessageUtils.addActionWarning(request, "orderspecial.errors.prod.plant.required");
				}
				
				if (getEmptyWagonMaterialId() == 0) {
					MessageUtils.addActionWarning(request, "orderspecial.errors.empty.wagon.mat.required");
				}
			}
			//any material
			else if (materialLoad == SELECTION_MATERIAL_LOAD_ANY) {
				
				if (StringUtils.empty(prodPlantCd)) {
					MessageUtils.addActionWarning(request, "orderspecial.errors.prod.plant.required");
				}
				if (sapMaterialId == 0) {
					MessageUtils.addActionWarning(request, "orderspecial.errors.sap.mat.required");
				}
			
			
				try {
					final BigDecimal qty = new BigDecimal(quantity);
					orderItem.setOrderedQuantity(qty);
					
					if (Constant.INVALID_OBJECT_ID_STR.equals(quantityUom)) {
						MessageUtils.addActionWarning(request, "orderspecial.errors.quantity.uom.required");
					}
					else {
						orderItem.setOrderedUom((UomBsEntity)uomBsEntityMap.get(quantityUom));
					}
					
				} 
				catch (NumberFormatException e) {
					MessageUtils.addActionWarning(request, "orderspecial.errors.quantity.format");
				}
				
				if (StringUtils.nvl(contactPerson).length() > OrderEntity.DBSIZE_CONTACT_PERSON_TXT) {
					MessageUtils.addActionWarning(request, "orderspecial.errors.contactPerson.tooLong");
				}
				else {
					order.setContactPersonTxt(contactPerson);
				}
				
			}
			
			try {
				if (StringUtils.notEmpty(orderSpecialDelDat)) {
					//parse delivery dates
					final Date delivDate = timezone.parseDayMonthYear(orderSpecialDelDat);
					order.setDeliveryFromDat(delivDate);
					// set the same for daelivery_to_dat because it was not specified in UI.
					order.setDeliveryToDat(delivDate);
				}
				else {
					MessageUtils.addActionWarning(request, "orderspecial.errors.del.from.dat.required");
				}
			}
			catch (ParseException e) {
				MessageUtils.addActionWarning(request, "orderspecial.errors.del.from.dat.format");
			}
			
			
			
			// only order of type repair, maintenance 
			if (Constant.DISTRIBUTION_DEST_REPAIR.equals(distDestCd)) {
								
				//email
				orderRep.setEmailSendFlag(emailSendFlag);
				
				if ((emailSendFlag) && StringUtils.empty(emailTo)) {
					MessageUtils.addActionWarning(request, "orderspecial.errors.email.to.required");
				}
				
				if (StringUtils.nvl(emailTo).length() > OrderRepEntity.DBSIZE_EMAIL_TO_ADRESSES) {
					MessageUtils.addActionWarning(request, "orderspecial.errors.email.to.tooLong");
				}
				else {
					orderRep.setEmailToAdresses(emailTo);
				}
				
				if (StringUtils.nvl(emailCc).length() > OrderRepEntity.DBSIZE_EMAIL_CC_ADRESSES) {
					MessageUtils.addActionWarning(request, "orderspecial.errors.email.cc.tooLong");
				}
				else {
					orderRep.setEmailCcAdresses(emailCc);
				}
				
				if (StringUtils.nvl(emailBcc).length() > OrderRepEntity.DBSIZE_EMAIL_BCC_ADRESSES) {
					MessageUtils.addActionWarning(request, "orderspecial.errors.email.bcc.tooLong");
				}
				else {
					orderRep.setEmailBccAdresses(emailBcc);
				}
				
				if (StringUtils.nvl(emailText).length() > OrderRepEntity.DBSIZE_EMAIL_ADDITIONAL_REMARKS) {
					MessageUtils.addActionWarning(request, "orderspecial.errors.email.remarks.tooLong");
				}
				else {
					orderRep.setEmailAdditionalRemarks(emailText);
				}		
				
				
				//offer
				if (offerDesiredFlag != null) {
					orderRep.setOfferDesiredFlag(offerDesiredFlag);
				}
				else {
					orderRep.setOfferDesiredFlag(false);
				}
				
				orderRep.setInvoicePaidFlag(false);
				orderRep.setOfferAcceptFlag(Boolean.FALSE);
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
			
			if (errors == null) {
				errors = new ActionErrors();
			}
			
			// validation is done in validateOrderDetail method which is called from action because it is a popup screen  and we cannot use struts validation 
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

	public String getPlantName() {
		return plantName;
	}

	public void setPlantName(String plantName) {
		this.plantName = plantName;
	}

	public String getPlantRailwayStationCd() {
		return plantRailwayStationCd;
	}

	public void setPlantRailwayStationCd(String plantRailwayStationCd) {
		this.plantRailwayStationCd = plantRailwayStationCd;
	}
	
	public String getOrigPlantRailwayStationCd() {
		if (wagon.getLastKnownRailwayStation() != null) {
			return wagon.getLastKnownRailwayStation().getRailwayStationId().toString();
		}
		else {
			return "";
		}
	}

	public Long getCarrierId() {
		return carrierId;
	}

	public void setCarrierId(Long carrierId) {
		this.carrierId = carrierId;
	}

	public String getCarrierDisplayName() {
		return carrierDisplayName;
	}

	public void setCarrierDisplayName(String carrierDisplayName) {
		this.carrierDisplayName = carrierDisplayName;
	}


	public OrderDetailUploadAttachmentBean getUploadBean() {
		return uploadBean;
	}

	public void setUploadBean(OrderDetailUploadAttachmentBean uploadBean) {
		this.uploadBean = uploadBean;
	}

	public List getDamageCodeTxList() {
		return damageCodeTxList;
	}

	public void setDamageCodeTxList(List damageCodeTxList) {
		this.damageCodeTxList = damageCodeTxList;
	}

	public Long getDamageCode() {
		return damageCode;
	}

	public void setDamageCode(Long damageCode) {
		this.damageCode = damageCode;
	}

	private String jsonDistDestData;


	public String getJsonDistDestData() {
		return jsonDistDestData;
	}

	public void setJsonDistDestData(String jsonDistDestData) {
		this.jsonDistDestData = jsonDistDestData;
	}

	public Map getEmptyMaterialMap() {
		return emptyMaterialMap;
	}

	public void setEmptyMaterialMap(Map emptyMaterialMap) {
		this.emptyMaterialMap = emptyMaterialMap;
	}

	public PlantBsEntity getDefaultPlant() {
		SapMatDescPackEntity emptyWagonSapMatDesc = getEmptyWagonSapMatDesc();
		if (emptyWagonSapMatDesc != null && LogonPackageHandlingUtil.isService(emptyWagonSapMatDesc.getLogonPackageTypeCd())) {			
			return defaultServicePlant;
		}
		else {
			return null;
		}
	}

	public void setDefaultServicePlant(PlantBsEntity defaultServicePlant) {
		this.defaultServicePlant = defaultServicePlant;
	}


	public Boolean isLastUploadFailed() {
		return lastUploadFailed;
	}

	public void setLastUploadFailed(Boolean lastUploadFailed) {
		this.lastUploadFailed = lastUploadFailed;
	}

	public String getLastUnsupportedFileExtension() {
		return lastUnsupportedFileExtension;
	}

	public void setLastUnsupportedFileExtension(String lastUnsupportedFileExtension) {
		this.lastUnsupportedFileExtension = lastUnsupportedFileExtension;
	}

}
