/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.sap.dto;

import java.io.Serializable;
import java.util.Date;

import com.crh.logon.persistence.entities.views.SapContractDispEntity;

public class SapContractDispDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long sapContractId;

	private String sapContractNo;

	private String sapContractTypeCd;

	private Long sareaId;

	private Long soldtoSapBpId;

	private Long shiptoSapBpId;

	private Long billtoSapBpId;

	private Long payerSapBpId;

	private Date validFromDat;

	private Date validUntilDat;

	private Boolean validUntilIgnoreFlag;

	private String sapShippingCondCd;

	private String custPurchaseOrderNo;

	private Boolean quantityControlFlag;

	private String currencyCd;

	private String creditControlAreaCd;

	private String creditLimitAccountNo;

	private String customerCreditGroup;

	private String creditManagementGroup;

	private String creditRiskCategory;

	private String creditCurrencyCd;

	private String salesDistrictCd;

	private String incotermsCd;

	private Long ctlCreUid;

	private Date ctlCreTs;

	private Long ctlModUid;

	private Date ctlModTs;

	private Long ctlTcn;

	private String validationCode;

	private String soldtoName;

	private String soldtoSapId;

	private String shiptoName;

	private String shiptoSapId;

	private String billtoName;

	private String billtoSapId;

	private String payerName;

	private String payerSapId;

	private String sareaDisplayLongname;
	
	private String validFromDatInStringWithOffset;

	private String validUntilDatInStringWithOffset;

	
	public SapContractDispDto(SapContractDispEntity entity) {
		this.sapContractId = entity.getSapContractId();
		this.sapContractNo = entity.getSapContractNo();
		this.sapContractTypeCd = entity.getSapContractTypeCd();
		this.sareaId = entity.getSareaId();
		this.soldtoSapBpId = entity.getSoldtoSapBpId();
		this.shiptoSapBpId = entity.getShiptoSapBpId();
		this.billtoSapBpId = entity.getBilltoSapBpId();
		this.payerSapBpId = entity.getPayerSapBpId();
		this.validFromDat = new Date(entity.getValidFromDat().getTime());
		this.validUntilDat = new Date(entity.getValidUntilDat().getTime());
		this.validUntilIgnoreFlag = entity.getValidUntilIgnoreFlag();
		this.sapShippingCondCd = entity.getSapShippingCondCd();
		this.custPurchaseOrderNo = entity.getCustPurchaseOrderNo();
		
		// int value is fine to use as this is only a decimal(2) in the DB
		// Conversion is based on info found at LOGON-7930 and LOGON-8039
		switch (entity.getItemGroupingType().intValue()) {
		case 1:
			this.quantityControlFlag = false;
			break;
		default:
			this.quantityControlFlag = true;
			break;
		}
	
		this.currencyCd = entity.getCurrencyCd();
		this.creditControlAreaCd = entity.getCreditControlAreaCd();
		this.creditLimitAccountNo = entity.getCreditLimitAccountNo();
		this.customerCreditGroup = entity.getCustomerCreditGroup();
		this.creditManagementGroup = entity.getCreditManagementGroup();
		this.creditRiskCategory = entity.getCreditRiskCategory();
		this.creditCurrencyCd = entity.getCreditCurrencyCd();
		this.salesDistrictCd = entity.getSalesDistrictCd();
		this.incotermsCd = entity.getIncotermsCd();
		this.ctlCreUid = entity.getCtlCreUid();
		this.ctlCreTs = new Date(entity.getCtlCreTs().getTime());
		this.ctlModUid = entity.getCtlModUid();
		this.ctlModTs = new Date(entity.getCtlModTs().getTime());
		this.validationCode = entity.getValidationCode();
		this.soldtoName = entity.getSoldtoName();
		this.soldtoSapId = entity.getSoldtoSapId();
		this.shiptoName = entity.getShiptoName();
		this.shiptoSapId = entity.getShiptoSapId();
		this.billtoName = entity.getBilltoName();
		this.billtoSapId = entity.getBilltoSapId();
		this.payerName = entity.getPayerName();
		this.payerSapId = entity.getPayerSapId();
		this.sareaDisplayLongname = entity.getSareaDisplayLongname();
	}
	
	public static final String ATTR_QUANTITY_CONTROL_FLAG = "quantityControlFlag";
	
	private static final String ATTR_VALID_FROM_DATE_IN_STRING_OFFSET = "validFromDatInStringWithOffset";

	private static final String ATTR_VALID_UNTIL_DATE_IN_STRING_OFFSET = "validUntilDatInStringWithOffset";
	
	/**
	 * This is intended to convert the few changed column name differences between the Entity and DTO
	 * 
	 * @param orderBy column name to order by
	 * @return converted property name
	 */
	public static final String convertOrderByColumn(String orderBy) {
		if (ATTR_QUANTITY_CONTROL_FLAG.equals(orderBy)) {
			return SapContractDispEntity.ATTR_ITEM_GROUPING_TYPE;			
		} else if(ATTR_VALID_FROM_DATE_IN_STRING_OFFSET.equals(orderBy)){
			return SapContractDispEntity.ATTR_VALID_FROM_DAT;
		} else if(ATTR_VALID_UNTIL_DATE_IN_STRING_OFFSET.equals(orderBy)){
			return SapContractDispEntity.ATTR_VALID_UNTIL_DAT;
		} else {
			return orderBy;
		}
	}

	/**
	 * @return the sapContractId
	 */
	public Long getSapContractId() {
		return sapContractId;
	}

	/**
	 * @param sapContractId the sapContractId to set
	 */
	public void setSapContractId(Long sapContractId) {
		this.sapContractId = sapContractId;
	}

	/**
	 * @return the sapContractNo
	 */
	public String getSapContractNo() {
		return sapContractNo;
	}

	/**
	 * @param sapContractNo the sapContractNo to set
	 */
	public void setSapContractNo(String sapContractNo) {
		this.sapContractNo = sapContractNo;
	}

	/**
	 * @return the sapContractTypeCd
	 */
	public String getSapContractTypeCd() {
		return sapContractTypeCd;
	}

	/**
	 * @param sapContractTypeCd the sapContractTypeCd to set
	 */
	public void setSapContractTypeCd(String sapContractTypeCd) {
		this.sapContractTypeCd = sapContractTypeCd;
	}

	/**
	 * @return the sareaId
	 */
	public Long getSareaId() {
		return sareaId;
	}

	/**
	 * @param sareaId the sareaId to set
	 */
	public void setSareaId(Long sareaId) {
		this.sareaId = sareaId;
	}

	/**
	 * @return the soldtoSapBpId
	 */
	public Long getSoldtoSapBpId() {
		return soldtoSapBpId;
	}

	/**
	 * @param soldtoSapBpId the soldtoSapBpId to set
	 */
	public void setSoldtoSapBpId(Long soldtoSapBpId) {
		this.soldtoSapBpId = soldtoSapBpId;
	}

	/**
	 * @return the shiptoSapBpId
	 */
	public Long getShiptoSapBpId() {
		return shiptoSapBpId;
	}

	/**
	 * @param shiptoSapBpId the shiptoSapBpId to set
	 */
	public void setShiptoSapBpId(Long shiptoSapBpId) {
		this.shiptoSapBpId = shiptoSapBpId;
	}

	/**
	 * @return the billtoSapBpId
	 */
	public Long getBilltoSapBpId() {
		return billtoSapBpId;
	}

	/**
	 * @param billtoSapBpId the billtoSapBpId to set
	 */
	public void setBilltoSapBpId(Long billtoSapBpId) {
		this.billtoSapBpId = billtoSapBpId;
	}

	/**
	 * @return the payerSapBpId
	 */
	public Long getPayerSapBpId() {
		return payerSapBpId;
	}

	/**
	 * @param payerSapBpId the payerSapBpId to set
	 */
	public void setPayerSapBpId(Long payerSapBpId) {
		this.payerSapBpId = payerSapBpId;
	}

	/**
	 * @return the validFromDat
	 */
	public Date getValidFromDat() {
		return validFromDat;
	}

	/**
	 * @param validFromDat the validFromDat to set
	 */
	public void setValidFromDat(Date validFromDat) {
		this.validFromDat = validFromDat;
	}

	/**
	 * @return the validUntilDat
	 */
	public Date getValidUntilDat() {
		return validUntilDat;
	}

	/**
	 * @param validUntilDat the validUntilDat to set
	 */
	public void setValidUntilDat(Date validUntilDat) {
		this.validUntilDat = validUntilDat;
	}

	/**
	 * @return the validUntilIgnoreFlag
	 */
	public Boolean getValidUntilIgnoreFlag() {
		return validUntilIgnoreFlag;
	}

	/**
	 * @param validUntilIgnoreFlag the validUntilIgnoreFlag to set
	 */
	public void setValidUntilIgnoreFlag(Boolean validUntilIgnoreFlag) {
		this.validUntilIgnoreFlag = validUntilIgnoreFlag;
	}

	/**
	 * @return the sapShippingCondCd
	 */
	public String getSapShippingCondCd() {
		return sapShippingCondCd;
	}

	/**
	 * @param sapShippingCondCd the sapShippingCondCd to set
	 */
	public void setSapShippingCondCd(String sapShippingCondCd) {
		this.sapShippingCondCd = sapShippingCondCd;
	}

	/**
	 * @return the custPurchaseOrderNo
	 */
	public String getCustPurchaseOrderNo() {
		return custPurchaseOrderNo;
	}

	/**
	 * @param custPurchaseOrderNo the custPurchaseOrderNo to set
	 */
	public void setCustPurchaseOrderNo(String custPurchaseOrderNo) {
		this.custPurchaseOrderNo = custPurchaseOrderNo;
	}

	/**
	 * @return the quantityControlFlag
	 */
	public Boolean getQuantityControlFlag() {
		return quantityControlFlag;
	}

	/**
	 * @param quantityControlFlag the quantityControlFlag to set
	 */
	public void setQuantityControlFlag(Boolean quantityControlFlag) {
		this.quantityControlFlag = quantityControlFlag;
	}

	/**
	 * @return the currencyCd
	 */
	public String getCurrencyCd() {
		return currencyCd;
	}

	/**
	 * @param currencyCd the currencyCd to set
	 */
	public void setCurrencyCd(String currencyCd) {
		this.currencyCd = currencyCd;
	}

	/**
	 * @return the creditControlAreaCd
	 */
	public String getCreditControlAreaCd() {
		return creditControlAreaCd;
	}

	/**
	 * @param creditControlAreaCd the creditControlAreaCd to set
	 */
	public void setCreditControlAreaCd(String creditControlAreaCd) {
		this.creditControlAreaCd = creditControlAreaCd;
	}

	/**
	 * @return the creditLimitAccountNo
	 */
	public String getCreditLimitAccountNo() {
		return creditLimitAccountNo;
	}

	/**
	 * @param creditLimitAccountNo the creditLimitAccountNo to set
	 */
	public void setCreditLimitAccountNo(String creditLimitAccountNo) {
		this.creditLimitAccountNo = creditLimitAccountNo;
	}

	/**
	 * @return the customerCreditGroup
	 */
	public String getCustomerCreditGroup() {
		return customerCreditGroup;
	}

	/**
	 * @param customerCreditGroup the customerCreditGroup to set
	 */
	public void setCustomerCreditGroup(String customerCreditGroup) {
		this.customerCreditGroup = customerCreditGroup;
	}

	/**
	 * @return the creditManagementGroup
	 */
	public String getCreditManagementGroup() {
		return creditManagementGroup;
	}

	/**
	 * @param creditManagementGroup the creditManagementGroup to set
	 */
	public void setCreditManagementGroup(String creditManagementGroup) {
		this.creditManagementGroup = creditManagementGroup;
	}

	/**
	 * @return the creditRiskCategory
	 */
	public String getCreditRiskCategory() {
		return creditRiskCategory;
	}

	/**
	 * @param creditRiskCategory the creditRiskCategory to set
	 */
	public void setCreditRiskCategory(String creditRiskCategory) {
		this.creditRiskCategory = creditRiskCategory;
	}

	/**
	 * @return the creditCurrencyCd
	 */
	public String getCreditCurrencyCd() {
		return creditCurrencyCd;
	}

	/**
	 * @param creditCurrencyCd the creditCurrencyCd to set
	 */
	public void setCreditCurrencyCd(String creditCurrencyCd) {
		this.creditCurrencyCd = creditCurrencyCd;
	}

	/**
	 * @return the salesDistrictCd
	 */
	public String getSalesDistrictCd() {
		return salesDistrictCd;
	}

	/**
	 * @param salesDistrictCd the salesDistrictCd to set
	 */
	public void setSalesDistrictCd(String salesDistrictCd) {
		this.salesDistrictCd = salesDistrictCd;
	}

	/**
	 * @return the incotermsCd
	 */
	public String getIncotermsCd() {
		return incotermsCd;
	}

	/**
	 * @param incotermsCd the incotermsCd to set
	 */
	public void setIncotermsCd(String incotermsCd) {
		this.incotermsCd = incotermsCd;
	}

	/**
	 * @return the ctlCreUid
	 */
	public Long getCtlCreUid() {
		return ctlCreUid;
	}

	/**
	 * @param ctlCreUid the ctlCreUid to set
	 */
	public void setCtlCreUid(Long ctlCreUid) {
		this.ctlCreUid = ctlCreUid;
	}

	/**
	 * @return the ctlCreTs
	 */
	public Date getCtlCreTs() {
		return ctlCreTs;
	}

	/**
	 * @param ctlCreTs the ctlCreTs to set
	 */
	public void setCtlCreTs(Date ctlCreTs) {
		this.ctlCreTs = ctlCreTs;
	}

	/**
	 * @return the ctlModUid
	 */
	public Long getCtlModUid() {
		return ctlModUid;
	}

	/**
	 * @param ctlModUid the ctlModUid to set
	 */
	public void setCtlModUid(Long ctlModUid) {
		this.ctlModUid = ctlModUid;
	}

	/**
	 * @return the ctlModTs
	 */
	public Date getCtlModTs() {
		return ctlModTs;
	}

	/**
	 * @param ctlModTs the ctlModTs to set
	 */
	public void setCtlModTs(Date ctlModTs) {
		this.ctlModTs = ctlModTs;
	}

	/**
	 * @return the ctlTcn
	 */
	public Long getCtlTcn() {
		return ctlTcn;
	}

	/**
	 * @param ctlTcn the ctlTcn to set
	 */
	public void setCtlTcn(Long ctlTcn) {
		this.ctlTcn = ctlTcn;
	}

	/**
	 * @return the validationCode
	 */
	public String getValidationCode() {
		return validationCode;
	}

	/**
	 * @param validationCode the validationCode to set
	 */
	public void setValidationCode(String validationCode) {
		this.validationCode = validationCode;
	}

	/**
	 * @return the soldtoName
	 */
	public String getSoldtoName() {
		return soldtoName;
	}

	/**
	 * @param soldtoName the soldtoName to set
	 */
	public void setSoldtoName(String soldtoName) {
		this.soldtoName = soldtoName;
	}

	/**
	 * @return the soldtoSapId
	 */
	public String getSoldtoSapId() {
		return soldtoSapId;
	}

	/**
	 * @param soldtoSapId the soldtoSapId to set
	 */
	public void setSoldtoSapId(String soldtoSapId) {
		this.soldtoSapId = soldtoSapId;
	}

	/**
	 * @return the shiptoName
	 */
	public String getShiptoName() {
		return shiptoName;
	}

	/**
	 * @param shiptoName the shiptoName to set
	 */
	public void setShiptoName(String shiptoName) {
		this.shiptoName = shiptoName;
	}

	/**
	 * @return the shiptoSapId
	 */
	public String getShiptoSapId() {
		return shiptoSapId;
	}

	/**
	 * @param shiptoSapId the shiptoSapId to set
	 */
	public void setShiptoSapId(String shiptoSapId) {
		this.shiptoSapId = shiptoSapId;
	}

	/**
	 * @return the billtoName
	 */
	public String getBilltoName() {
		return billtoName;
	}

	/**
	 * @param billtoName the billtoName to set
	 */
	public void setBilltoName(String billtoName) {
		this.billtoName = billtoName;
	}

	/**
	 * @return the billtoSapId
	 */
	public String getBilltoSapId() {
		return billtoSapId;
	}

	/**
	 * @param billtoSapId the billtoSapId to set
	 */
	public void setBilltoSapId(String billtoSapId) {
		this.billtoSapId = billtoSapId;
	}

	/**
	 * @return the payerName
	 */
	public String getPayerName() {
		return payerName;
	}

	/**
	 * @param payerName the payerName to set
	 */
	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}

	/**
	 * @return the payerSapId
	 */
	public String getPayerSapId() {
		return payerSapId;
	}

	/**
	 * @param payerSapId the payerSapId to set
	 */
	public void setPayerSapId(String payerSapId) {
		this.payerSapId = payerSapId;
	}

	/**
	 * @return the sareaDisplayLongname
	 */
	public String getSareaDisplayLongname() {
		return sareaDisplayLongname;
	}

	/**
	 * @param sareaDisplayLongname the sareaDisplayLongname to set
	 */
	public void setSareaDisplayLongname(String sareaDisplayLongname) {
		this.sareaDisplayLongname = sareaDisplayLongname;
	}
	
	/**
	 * @return the validFromDatInStringWithOffset
	 */
	public String getValidFromDatInStringWithOffset() {
		return validFromDatInStringWithOffset;
	}

	/**
	 * @return the validUntilDatInStringWithOffset
	 */
	public String getValidUntilDatInStringWithOffset() {
		return validUntilDatInStringWithOffset;
	}
	
	/**
	 * @param validFromDatInStringWithOffset the validFromDatInStringWithOffset to set
	 */
	public void setValidFromDatInStringWithOffset(String validFromDatInStringWithOffset) {
		this.validFromDatInStringWithOffset = validFromDatInStringWithOffset;
	}

	/**
	 * @param validUntilDatInStringWithOffset the validUntilDatInStringWithOffset to set
	 */
	public void setValidUntilDatInStringWithOffset(String validUntilDatInStringWithOffset) {
		this.validUntilDatInStringWithOffset = validUntilDatInStringWithOffset;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "SapContractDispDto [sapContractId=" + sapContractId + ", sapContractNo="
				+ sapContractNo + ", sapContractTypeCd=" + sapContractTypeCd + ", sareaId="
				+ sareaId + ", soldtoSapBpId=" + soldtoSapBpId + ", shiptoSapBpId=" + shiptoSapBpId
				+ ", billtoSapBpId=" + billtoSapBpId + ", payerSapBpId=" + payerSapBpId
				+ ", validFromDat=" + validFromDat + ", validUntilDat=" + validUntilDat
				+ ", validUntilIgnoreFlag=" + validUntilIgnoreFlag + ", sapShippingCondCd="
				+ sapShippingCondCd + ", custPurchaseOrderNo=" + custPurchaseOrderNo
				+ ", quantityControlFlag=" + quantityControlFlag + ", currencyCd=" + currencyCd
				+ ", creditControlAreaCd=" + creditControlAreaCd + ", creditLimitAccountNo="
				+ creditLimitAccountNo + ", customerCreditGroup=" + customerCreditGroup
				+ ", creditManagementGroup=" + creditManagementGroup + ", creditRiskCategory="
				+ creditRiskCategory + ", creditCurrencyCd=" + creditCurrencyCd
				+ ", salesDistrictCd=" + salesDistrictCd + ", incotermsCd=" + incotermsCd
				+ ", ctlCreUid=" + ctlCreUid + ", ctlCreTs=" + ctlCreTs + ", ctlModUid="
				+ ctlModUid + ", ctlModTs=" + ctlModTs + ", ctlTcn=" + ctlTcn + ", validationCode="
				+ validationCode + ", soldtoName=" + soldtoName + ", soldtoSapId=" + soldtoSapId
				+ ", shiptoName=" + shiptoName + ", shiptoSapId=" + shiptoSapId + ", billtoName="
				+ billtoName + ", billtoSapId=" + billtoSapId + ", payerName=" + payerName
				+ ", payerSapId=" + payerSapId + ", sareaDisplayLongname=" + sareaDisplayLongname
				+ "]";
	}

}
