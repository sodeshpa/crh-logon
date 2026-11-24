/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.auftrag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.OrderRepAttEntity;
import com.crh.logon.persistence.entities.common.OrderRepEntity;
import com.crh.logon.persistence.entities.common.OrderRepMaintTypeEntity;
import com.crh.logon.persistence.entities.refdata.MaintTypeBsEntity;

/**
 * @author AdNovum Informatik AG
 */
public class RepTabBean implements Serializable{

	private OrderRepEntity orderRep;
	
	//maint. type list
	private Map maintTypeTxEntityMap = new LinkedHashMap();
	private List maintTypeBsEntityList = new ArrayList();
	//list which will be saved
	private List orderRepMaintTypeSaveList = new ArrayList();
	
	private List damageCodeTxList = new ArrayList();
	
	//offer invoice
	private Boolean offerDesiredFlag;
	private String offerAcceptDate;
	private String offerAcceptTime;
	private String offerAmount;
	private String offerAmountCur;
	private String invoiceDate;
	private String invoiceTime;
	private String invoiceNo;
	private String invoiceAmount;
	private String invoiceAmountCur;
	private String internalRemark;
	private String offerRemarks;
	private String documentationRemarks;
	private Boolean invoicePaidFlag;
	private Boolean documentationAcceptFlag;
	private Boolean offerAcceptFlag;
	private Map currencyBsEntityMap = new HashMap();
	private Long damageCode;
	private String leavePlantDate;
	private String leavePlantTime;
	private String enterPlantDate;
	private String enterPlantTime;
	
	//email
	private String emailLastSent;
	private String emailTo;
	private String emailCc;
	private String emailBcc;
	private String emailText;
	
	//only helper lists in order not to iterate over and over on the whole list just to show the certain attachment types
	private List emailAttachments;
	private List invoiceAttachments;
	private List offerAttachments;
	private List documentationAttachments;
	
	public List getOrderRepMaintTypeSaveList() {
		return orderRepMaintTypeSaveList;
	}

	public void setOrderRepMaintTypeSaveList(List orderRepMaintTypeSaveList) {
		this.orderRepMaintTypeSaveList = orderRepMaintTypeSaveList;
	}
	
	public OrderRepEntity getOrderRep() {
		return orderRep;
	}

	public void setOrderRep(OrderRepEntity orderRep) {
		this.orderRep = orderRep;
		setAttachmentLists(orderRep);
	}
	
	public boolean getMaintTypeBs(int index){
		MaintTypeBsEntity maintTypeBs = (MaintTypeBsEntity) maintTypeBsEntityList.get(index);
		
		if (maintTypeBs != null) {
			for (int j = 0; j < orderRep.getOrderRepMaintTypes().size(); j++) {
				OrderRepMaintTypeEntity orderRepMaintType = (OrderRepMaintTypeEntity) orderRep.getOrderRepMaintTypes().get(j);
				if (maintTypeBs.getMaintTypeCd().equals(orderRepMaintType.getMaintType().getMaintTypeCd())) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void setMaintTypeBs(int index, boolean value){
		MaintTypeBsEntity maintTypeBs = (MaintTypeBsEntity) maintTypeBsEntityList.get(index);
		
		if (maintTypeBs != null) {
			OrderRepMaintTypeEntity orderRepMaintType= null;
			//if already exists 
			for (int j = 0; j < orderRep.getOrderRepMaintTypes().size(); j++) {
				OrderRepMaintTypeEntity oRep = (OrderRepMaintTypeEntity) orderRep.getOrderRepMaintTypes().get(j);
				if (maintTypeBs.getMaintTypeCd().equals(oRep.getMaintType().getMaintTypeCd())) {
					orderRepMaintType = oRep;
					break;
				}
			}
			
			//if a new one
			if (orderRepMaintType == null) {
				orderRepMaintType = new OrderRepMaintTypeEntity();
				orderRepMaintType.setOrderRep(orderRep);
				orderRepMaintType.setMaintType(maintTypeBs);
			}
			
			orderRepMaintTypeSaveList.add(orderRepMaintType);
		}
	}

	
	public Boolean getInvoicePaidFlag() {
		return invoicePaidFlag;
	}

	public void setInvoicePaidFlag(Boolean invoicePaidFlag) {
		this.invoicePaidFlag = invoicePaidFlag;
	}
	public Map getMaintTypeTxEntityMap() {
		return maintTypeTxEntityMap;
	}
	public void setMaintTypeTxEntityMap(Map maintTypeTxEntityMap) {
		this.maintTypeTxEntityMap = maintTypeTxEntityMap;
	}
	public List getMaintTypeBsEntityList() {
		return maintTypeBsEntityList;
	}
	public void setMaintTypeBsEntityList(List maintTypeBsEntityList) {
		this.maintTypeBsEntityList = maintTypeBsEntityList;
	}
	public Boolean getOfferDesiredFlag() {
		return offerDesiredFlag;
	}
	public void setOfferDesiredFlag(Boolean offerDesiredFlag) {
		this.offerDesiredFlag = offerDesiredFlag;
	}
	public String getOfferAcceptDate() {
		return offerAcceptDate;
	}
	public void setOfferAcceptDate(String offerAcceptDate) {
		this.offerAcceptDate = offerAcceptDate;
	}
	public String getOfferAcceptTime() {
		return offerAcceptTime;
	}
	public void setOfferAcceptTime(String offerAcceptTime) {
		this.offerAcceptTime = offerAcceptTime;
	}
	public String getOfferAmount() {
		return offerAmount;
	}
	public void setOfferAmount(String offerAmount) {
		this.offerAmount = offerAmount;
	}
	public String getOfferAmountCur() {
		return offerAmountCur;
	}
	public void setOfferAmountCur(String offerAmountCur) {
		this.offerAmountCur = offerAmountCur;
	}
	public String getInvoiceDate() {
		return invoiceDate;
	}
	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	public String getInvoiceTime() {
		return invoiceTime;
	}
	public void setInvoiceTime(String invoiceTime) {
		this.invoiceTime = invoiceTime;
	}
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public String getInvoiceAmount() {
		return invoiceAmount;
	}
	public void setInvoiceAmount(String invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}
	public String getInvoiceAmountCur() {
		return invoiceAmountCur;
	}
	public void setInvoiceAmountCur(String invoiceAmountCur) {
		this.invoiceAmountCur = invoiceAmountCur;
	}
	public String getInternalRemark() {
		return internalRemark;
	}
	public void setInternalRemark(String internalRemark) {
		this.internalRemark = internalRemark;
	}
	public Map getCurrencyBsEntityMap() {
		return currencyBsEntityMap;
	}
	public void setCurrencyBsEntityMap(Map currencyBsEntityMap) {
		this.currencyBsEntityMap = currencyBsEntityMap;
	}
	public String getEmailLastSent() {
		return emailLastSent;
	}
	public void setEmailLastSent(String emailLastSent) {
		this.emailLastSent = emailLastSent;
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

	public Boolean getDocumentationAcceptFlag() {
		return documentationAcceptFlag;
	}

	public void setDocumentationAcceptFlag(Boolean documentationAcceptFlag) {
		this.documentationAcceptFlag = documentationAcceptFlag;
	}

	public Long getDamageCode() {
		return damageCode;
	}

	public void setDamageCode(Long damageCode) {
		this.damageCode = damageCode;
	}

	public List getDamageCodeTxList() {
		return damageCodeTxList;
	}

	public void setDamageCodeTxList(List damageCodeTxList) {
		this.damageCodeTxList = damageCodeTxList;
	}

	public String getLeavePlantDate() {
		return leavePlantDate;
	}

	public void setLeavePlantDate(String leavePlantDate) {
		this.leavePlantDate = leavePlantDate;
	}

	public String getLeavePlantTime() {
		return leavePlantTime;
	}

	public void setLeavePlantTime(String leavePlantTime) {
		this.leavePlantTime = leavePlantTime;
	}

	public String getEnterPlantDate() {
		return enterPlantDate;
	}

	public void setEnterPlantDate(String enterPlantDate) {
		this.enterPlantDate = enterPlantDate;
	}

	public String getEnterPlantTime() {
		return enterPlantTime;
	}

	public void setEnterPlantTime(String enterPlantTime) {
		this.enterPlantTime = enterPlantTime;
	}

	public String getOfferRemarks() {
		return offerRemarks;
	}

	public void setOfferRemarks(String offerRemarks) {
		this.offerRemarks = offerRemarks;
	}

	public String getDocumentationRemarks() {
		return documentationRemarks;
	}

	public void setDocumentationRemarks(String documentationRemarks) {
		this.documentationRemarks = documentationRemarks;
	}

	private void setAttachmentLists(OrderRepEntity orderRep) {
		this.emailAttachments = new ArrayList();
		this.offerAttachments = new ArrayList();
		this.invoiceAttachments = new ArrayList();
		this.documentationAttachments = new ArrayList();
		if ( orderRep != null && orderRep.getOrderRepAttachments() != null && !orderRep.getOrderRepAttachments().isEmpty()) {
			Iterator it = orderRep.getOrderRepAttachments().iterator();
			while (it.hasNext()) {
				OrderRepAttEntity att = (OrderRepAttEntity) it.next();
				List list = getFormListForAttachmentType(att);
				list.add(att);				
			}
		}
	}

	/**
	 * @param att
	 * @return
	 */
	public List getFormListForAttachmentType(OrderRepAttEntity att) {
		List list = null;
		if (att.getAttachmentType().getAttachmentTypeCd().equals(Constant.ORDER_REP_ATTACHMENT_TYPE_EMAIL)) {
			list = this.emailAttachments;
		}
		else if (att.getAttachmentType().getAttachmentTypeCd().equals(Constant.ORDER_REP_ATTACHMENT_TYPE_INVOICE)) {
			list = this.invoiceAttachments;
		}
		else if (att.getAttachmentType().getAttachmentTypeCd().equals(Constant.ORDER_REP_ATTACHMENT_TYPE_OFFER)) {
			list = this.offerAttachments;
		}
		else if (att.getAttachmentType().getAttachmentTypeCd().equals(Constant.ORDER_REP_ATTACHMENT_TYPE_DOCUMENTATION)) {
			list = this.documentationAttachments;
		}
		return list;
	}
	
	public List getEmailAttachments() {
		return emailAttachments;
	}

	public List getInvoiceAttachments() {
		return invoiceAttachments;
	}

	public List getOfferAttachments() {
		return offerAttachments;
	}

	public List getDocumentationAttachments() {
		return documentationAttachments;
	}

	public Boolean getOfferAcceptFlag() {
		return offerAcceptFlag;
	}

	public void setOfferAcceptFlag(Boolean offerAcceptFlag) {
		this.offerAcceptFlag = offerAcceptFlag;
	}

	
}

