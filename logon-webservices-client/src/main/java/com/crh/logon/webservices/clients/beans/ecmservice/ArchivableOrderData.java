/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.ecmservice;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

	/**
	 * @author AdNovum Informatik AG
	 */
	public class ArchivableOrderData {
		
		// Metadata as defined in LRQ 090.1 Archiving of Deliveries in LOGON chapter 3.6
		private Date documentCreationDate;
		private Long documentValidity;
		private String companyCode;
		private String salesOrganization;
		private String division;
		private String distributionChannel;
		private Long orderNumber;
		private String deliveryNoteNumber;
		private String sequentialOrderNumber;
		private Long customerOrderNumber;
		private String shipToSapId;
		private String shipToSapName;
		private String soldToSapId;
		private String soldToSapName;
		private String billToSapId;
		private String billToSapName;
		private String payerSapId;
		private String payerSapName;
		private String commercialCarrierSapId;
		private String commercialCarrierName;
		private String executingCarrierSapId;
		private String executingCarrierName;
		private String contractNumber;
		private String contractType;
		private String contractIncoterm;
		private Long deliveryType;
		private Long poolName;
		private Date deliveryDateFrom;
		private Date deliveryDateTo;
		private Date actualDeliveryDate;
		private Date loadingDate;
		private String deliveryPlant;
		private String shippingPoint;
		private String shippingCondition;
		
		// Single Delivery Order PDFs as defined in LRQ 090.1 Archiving of Deliveries in LOGON chapter 3.3
		private List<PdfData> pdfData = new ArrayList<PdfData>();
		
		// Order Log TXT as defined in LRQ 090.1 Archiving of Deliveries in LOGON chapter 3.4
		private String orderLog;
		
		// Additional information for logging, not included in webservice request
		private Long orderId;
		
		
		
		public Calendar getDocumentCreationDate() {
			return toCalendar(documentCreationDate);
		}
		public void setDocumentCreationDate(Date documentCreationDate) {
			this.documentCreationDate = documentCreationDate;
		}
		public Long getDocumentValidity() {
			return documentValidity;
		}
		public void setDocumentValidity(Long documentValidity) {
			this.documentValidity = documentValidity;
		}
		public String getCompanyCode() {
			return companyCode;
		}
		public void setCompanyCode(String companyCode) {
			this.companyCode = companyCode;
		}
		public String getSalesOrganization() {
			return salesOrganization;
		}
		public void setSalesOrganization(String salesOrganization) {
			this.salesOrganization = salesOrganization;
		}
		public String getDivision() {
			return division;
		}
		public void setDivision(String division) {
			this.division = division;
		}
		public String getDistributionChannel() {
			return distributionChannel;
		}
		public void setDistributionChannel(String distributionChannel) {
			this.distributionChannel = distributionChannel;
		}
		public String getOrderNumber() {
			return orderNumber.toString();
		}
		public void setOrderNumber(Long orderNumber) {
			this.orderNumber = orderNumber;
		}
		public String getDeliveryNoteNumber() {
			return deliveryNoteNumber;
		}
		public void setDeliveryNoteNumber(String deliveryNoteNumber) {
			this.deliveryNoteNumber = deliveryNoteNumber;
		}
		public String getSequentialOrderNumber() {
			return sequentialOrderNumber;
		}
		public void setSequentialOrderNumber(String sequentialOrderNumber) {
			this.sequentialOrderNumber = sequentialOrderNumber;
		}
		public String getCustomerOrderNumber() {
			return customerOrderNumber != null ? customerOrderNumber.toString() : null;
		}
		public void setCustomerOrderNumber(Long customerOrderNumber) {
			this.customerOrderNumber = customerOrderNumber;
		}
		public String getShipToSapId() {
			return shipToSapId;
		}
		public void setShipToSapId(String shipToSapId) {
			this.shipToSapId = shipToSapId;
		}
		public String getShipToSapName() {
			return shipToSapName;
		}
		public void setShipToSapName(String shipToSapName) {
			this.shipToSapName = shipToSapName;
		}
		public String getSoldToSapId() {
			return soldToSapId;
		}
		public void setSoldToSapId(String soldToSapId) {
			this.soldToSapId = soldToSapId;
		}
		public String getSoldToSapName() {
			return soldToSapName;
		}
		public void setSoldToSapName(String soldToSapName) {
			this.soldToSapName = soldToSapName;
		}
		public String getBillToSapId() {
			return billToSapId;
		}
		public void setBillToSapId(String billToSapId) {
			this.billToSapId = billToSapId;
		}
		public String getBillToSapName() {
			return billToSapName;
		}
		public void setBillToSapName(String billToSapName) {
			this.billToSapName = billToSapName;
		}
		public String getPayerSapId() {
			return payerSapId;
		}
		public void setPayerSapId(String payerSapId) {
			this.payerSapId = payerSapId;
		}
		public String getPayerSapName() {
			return payerSapName;
		}
		public void setPayerSapName(String payerSapName) {
			this.payerSapName = payerSapName;
		}
		public String getCommercialCarrierSapId() {
			return commercialCarrierSapId;
		}
		public void setCommercialCarrierSapId(String commercialCarrierSapId) {
			this.commercialCarrierSapId = commercialCarrierSapId;
		}
		public String getCommercialCarrierName() {
			return commercialCarrierName;
		}
		public void setCommercialCarrierName(String commercialCarrierName) {
			this.commercialCarrierName = commercialCarrierName;
		}
		public String getExecutingCarrierSapId() {
			return executingCarrierSapId;
		}
		public void setExecutingCarrierSapId(String executingCarrierSapId) {
			this.executingCarrierSapId = executingCarrierSapId;
		}
		public String getExecutingCarrierName() {
			return executingCarrierName;
		}
		public void setExecutingCarrierName(String executingCarrierName) {
			this.executingCarrierName = executingCarrierName;
		}
		public String getContractNumber() {
			return contractNumber;
		}
		public void setContractNumber(String contractNumber) {
			this.contractNumber = contractNumber;
		}
		public String getContractType() {
			return contractType;
		}
		public void setContractType(String contractType) {
			this.contractType = contractType;
		}
		public String getContractIncoterm() {
			return contractIncoterm;
		}
		public void setContractIncoterm(String contractIncoterm) {
			this.contractIncoterm = contractIncoterm;
		}
		public int getDeliveryType() {
			return deliveryType.intValue();
		}
		public void setDeliveryType(Long deliveryType) {
			this.deliveryType = deliveryType;
		}
		public String getPoolName() {
			return poolName != null ? poolName.toString() : null;
		}
		public void setPoolName(Long poolName) {
			this.poolName = poolName;
		}
		public Calendar getDeliveryDateFrom() {
			return toCalendar(deliveryDateFrom);
		}
		public void setDeliveryDateFrom(Date deliveryDateFrom) {
			this.deliveryDateFrom = deliveryDateFrom;
		}
		public Calendar getDeliveryDateTo() {
			return toCalendar(deliveryDateTo);
		}
		public void setDeliveryDateTo(Date deliveryDateTo) {
			this.deliveryDateTo = deliveryDateTo;
		}
		public Calendar getActualDeliveryDate() {
			return toCalendar(actualDeliveryDate);
		}
		public void setActualDeliveryDate(Date actualDeliveryDate) {
			this.actualDeliveryDate = actualDeliveryDate;
		}
		public Calendar getLoadingDate() {
			return toCalendar(loadingDate);
		}

		private Calendar toCalendar(Date date) {
			Calendar calendar = null;
			if (date != null) {
				calendar = Calendar.getInstance();
				calendar.setTime(date);
			}
			return calendar;
		}
		
		public void setLoadingDate(Date loadingDate) {
			this.loadingDate = loadingDate;
		}
		public String getDeliveryPlant() {
			return deliveryPlant;
		}
		public void setDeliveryPlant(String deliveryPlant) {
			this.deliveryPlant = deliveryPlant;
		}
		public String getShippingPoint() {
			return shippingPoint;
		}
		public void setShippingPoint(String shippingPoint) {
			this.shippingPoint = shippingPoint;
		}
		public String getShippingCondition() {
			return shippingCondition;
		}
		public void setShippingCondition(String shippingCondition) {
			this.shippingCondition = shippingCondition;
		}
		public List<PdfData> getPdfData() {
			return pdfData;
		}
		public void setPdfData(List<PdfData> pdfData) {
			this.pdfData = pdfData;
		}
		public String getOrderLog() {
			return orderLog;
		}
		public void setOrderLog(String orderLog) {
			this.orderLog = orderLog;
		}
		public Long getOrderId() {
			return orderId;
		}
		public void setOrderId(Long orderId) {
			this.orderId = orderId;
		}
		@Override
		public String toString() {
			return "ArchivableOrderData [documentCreationDate="
					+ documentCreationDate + ", documentValidity="
					+ documentValidity + ", companyCode=" + companyCode
					+ ", salesOrganization=" + salesOrganization
					+ ", division=" + division + ", distributionChannel="
					+ distributionChannel + ", orderNumber=" + orderNumber
					+ ", deliveryNoteNumber=" + deliveryNoteNumber
					+ ", sequentialOrderNumber=" + sequentialOrderNumber
					+ ", customerOrderNumber=" + customerOrderNumber
					+ ", shipToSapId=" + shipToSapId + ", shipToSapName="
					+ shipToSapName + ", soldToSapId=" + soldToSapId
					+ ", soldToSapName=" + soldToSapName + ", billToSapId="
					+ billToSapId + ", billToSapName=" + billToSapName
					+ ", payerSapId=" + payerSapId + ", payerSapName="
					+ payerSapName + ", commercialCarrierSapId="
					+ commercialCarrierSapId + ", commercialCarrierName="
					+ commercialCarrierName + ", executingCarrierSapId="
					+ executingCarrierSapId + ", executingCarrierName="
					+ executingCarrierName + ", contractNumber="
					+ contractNumber + ", contractType=" + contractType
					+ ", contractIncoterm=" + contractIncoterm
					+ ", deliveryType=" + deliveryType + ", poolName="
					+ poolName + ", deliveryDateFrom=" + deliveryDateFrom
					+ ", deliveryDateTo=" + deliveryDateTo
					+ ", actualDeliveryDate=" + actualDeliveryDate
					+ ", loadingDate=" + loadingDate + ", deliveryPlant="
					+ deliveryPlant + ", shippingPoint=" + shippingPoint
					+ ", shippingCondition=" + shippingCondition + ", pdfData="
					+ pdfData + ", orderLog=" + orderLog + ", orderId="
					+ orderId + "]";
		}
				
	}