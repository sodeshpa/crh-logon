/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.auftrag;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author AdNovum Informatik AG
 */
public class OrderDetailBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private String contactPerson;
	private String createdByPerson;
	private String createdByDate;
	private String createdByTime;
	private Boolean onHoldOrderFlag;
	private Boolean railHoldBackReOrderFlag;
	private String delivDateFrom;
	private String delivDateTo;
	private String delivTimeFrom;
	private String delivTimeTo;
	private Date delivDateFromDat;
	private Date delivDateToDat;
	private String orderStatus;
	private String plantCd;
	private String shPointNo;
	private Long deliveryTypeCd;	
	private String spiCd;	
	private Long poolId;
	private Boolean autoPoolAssignmentFlag; 
	private Boolean autoCommCarrierAssignmentFlag;
	private String poolName;
	private Long commCarrierId;
	private String commCarrierVendorSapId;
	private Long execCarrierId;
	private String execCarrierVendorSapId;	
	private String plateNo;
	private Long truckId;
	private String truckName;
	private String driverName;
	private List orderStatusList;
	private String trspCategory;
	private String trspType;
	private String distributionDestCd;
	private boolean showPickupInfo; //*LOGON-4515 Fenics sets these vaules for pickups, where no real truck is assigned.
	private String wsTruckPlateNo;   /*LOGON-4515*/
	private String wsTrailerPlateNo; /*LOGON-4515*/
	private String wsCarrier; 		  /*LOGON-4515*/
	private String selectedVehicles; /* LOGON-5562 */
	private Boolean serviceOnlyOrderFlag;	// LOGON-4743
	private String ldsDeliveryNoteNo;
	private String orderNumberFromSeqUsage;

	public Boolean getOnHoldOrderFlag() {
		return onHoldOrderFlag;
	}
	
	public void setOnHoldOrderFlag(Boolean onHoldOrderFlag) {
		this.onHoldOrderFlag = onHoldOrderFlag;
	}
	
	public Boolean getAutoPoolAssignmentFlag() {
		return autoPoolAssignmentFlag;
	}

	public void setAutoPoolAssignmentFlag(Boolean autoPoolAssignmentFlag) {
		this.autoPoolAssignmentFlag = autoPoolAssignmentFlag;
	}

	
	public String getPlateNo() {
		return plateNo;
	}

	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
	}
	
	public String getTrspCategory() {
		return trspCategory;
	}

	public void setTrspCategory(String trspCategory) {
		this.trspCategory = trspCategory;
	}

	public String getTrspType() {
		return trspType;
	}

	public void setTrspType(String trspType) {
		this.trspType = trspType;
	}
	
	public Long getTruckId() {
		return truckId;
	}

	public void setTruckId(Long truckId) {
		this.truckId = truckId;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	
	public String getTruckName() {
		return truckName;
	}

	public void setTruckName(String truckName) {
		this.truckName = truckName;
	}
	
	public String getCreatedByPerson() {
		return createdByPerson;
	}

	public void setCreatedByPerson(String createdByPerson) {
		this.createdByPerson = createdByPerson;
	}

	public String getCreatedByDate() {
		return createdByDate;
	}

	public void setCreatedByDate(String createdByDate) {
		this.createdByDate = createdByDate;
	}

	public String getCreatedByTime() {
		return createdByTime;
	}

	public void setCreatedByTime(String createdByTime) {
		this.createdByTime = createdByTime;
	}
	
	public List getOrderStatusList() {
		return orderStatusList;
	}

	public void setOrderStatusList(List orderStatusList) {
		this.orderStatusList = orderStatusList;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}	

	public Long getPoolId() {
		return poolId;
	}

	public void setPoolId(Long poolId) {
		this.poolId = poolId;
	}
	
    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }
    
	public String getShPointNo() {
		return shPointNo;
	}

	public void setShPointNo(String shPointNo) {
		this.shPointNo = shPointNo;
	}
	
	public String getPlantCd() {
		return plantCd;
	}

	public void setPlantCd(String plantCd) {
		this.plantCd = plantCd;
	}
	
	public Long getDeliveryTypeCd() {
		return deliveryTypeCd;
	}
	
	public void setDeliveryTypeCd(Long deliveryTypeCd) {
		this.deliveryTypeCd = deliveryTypeCd;
	}	

	
	public String getDelivTimeFrom() {
		return delivTimeFrom;
	}

	public void setDelivTimeFrom(String delivTimeFrom) {
		this.delivTimeFrom = delivTimeFrom;
	}

	public String getDelivTimeTo() {
		return delivTimeTo;
	}

	public void setDelivTimeTo(String delivTimeTo) {
		this.delivTimeTo = delivTimeTo;
	}
	
	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getDelivDateFrom() {
		return delivDateFrom;
	}

	public void setDelivDateFrom(String delivDateFrom) {
		this.delivDateFrom = delivDateFrom;
	}

	public String getDelivDateTo() {
		return delivDateTo;
	}

	public void setDelivDateTo(String delivDateTo) {
		this.delivDateTo = delivDateTo;
	}

	public String getDistributionDestCd() {
		return distributionDestCd;
	}

	public void setDistributionDestCd(String distributionDestCd) {
		this.distributionDestCd = distributionDestCd;
	}

	public Boolean getRailHoldBackReOrderFlag() {
		return railHoldBackReOrderFlag;
	}

	public void setRailHoldBackReOrderFlag(Boolean railHoldBackReOrderFlag) {
		if ( railHoldBackReOrderFlag != null && railHoldBackReOrderFlag.booleanValue() ) {			
			this.railHoldBackReOrderFlag = railHoldBackReOrderFlag;
		}
		else {
			this.railHoldBackReOrderFlag = null;
		}
	}

	public Long getCommCarrierId() {
		return commCarrierId;
	}

	public void setCommCarrierId(Long commCarrierId) {
		this.commCarrierId = commCarrierId;
	}

	public Long getExecCarrierId() {
		return execCarrierId;
	}

	public void setExecCarrierId(Long execCarrierId) {
		this.execCarrierId = execCarrierId;
	}

	public Boolean getAutoCommCarrierAssignmentFlag() {
		return autoCommCarrierAssignmentFlag;
	}

	public void setAutoCommCarrierAssignmentFlag(Boolean autoCommCarrierAssignmentFlag) {
		this.autoCommCarrierAssignmentFlag = autoCommCarrierAssignmentFlag;
	}

	public String getCommCarrierVendorSapId() {
		return commCarrierVendorSapId;
	}

	public void setCommCarrierVendorSapId(String commCarrierVendorSapId) {
		this.commCarrierVendorSapId = commCarrierVendorSapId;
	}

	public String getExecCarrierVendorSapId() {
		return execCarrierVendorSapId;
	}

	public void setExecCarrierVendorSapId(String execCarrierVendorSapId) {
		this.execCarrierVendorSapId = execCarrierVendorSapId;
	}

	public void setWsTruckPlateNo(String wsTruckPlateNo) {
		this.wsTruckPlateNo = wsTruckPlateNo;
	}

	public void setWsTrailerPlateNo(String wsTrailerPlateNo) {
		this.wsTrailerPlateNo = wsTrailerPlateNo;
	}

	public void setWsCarrier(String wsCarrier) {
		this.wsCarrier = wsCarrier;
	}

	public String getWsTruckPlateNo() {
		return wsTruckPlateNo;
	}

	public String getWsTrailerPlateNo() {
		return wsTrailerPlateNo;
	}

	public String getWsCarrier() {
		return wsCarrier;
	}

	public void setShowPickupInfo(boolean showPickupInfo) {
		this.showPickupInfo = showPickupInfo;
	}

	public boolean isShowPickupInfo() {
		return showPickupInfo;
	}

	public String getSpiCd() {
		return spiCd;
	}

	public void setSpiCd(String spiCd) {
		this.spiCd = spiCd;
	}

	public String getSelectedVehicles() {
		return selectedVehicles;
	}

	public void setSelectedVehicles(String selectedVehicles) {
		this.selectedVehicles = selectedVehicles;
	}

	public Boolean getServiceOnlyOrderFlag() {
		return serviceOnlyOrderFlag;
	}

	public void setServiceOnlyOrderFlag(Boolean serviceOnlyOrderFlag) {
		this.serviceOnlyOrderFlag = serviceOnlyOrderFlag;
	}
	
	public List<Long> selectedTruckTypesAsList()
	{
		/* The field "selectedVehicles" is a String of the form
		 * "1,2,3,4". This is how it is set by javascript.
		 * Here we convert it back to a numeric list.
		 */
		final LinkedList<Long> typeList = new LinkedList<Long>();
		
		for (String type : selectedVehicles.split(","))
		{
			typeList.add(Long.valueOf(type));
		}
		
		return typeList;
	}

	public Date getDelivDateFromDat() {
		return delivDateFromDat;
	}

	public void setDelivDateFromDat(Date delivDateFromDat) {
		this.delivDateFromDat = delivDateFromDat;
	}

	public Date getDelivDateToDat() {
		return delivDateToDat;
	}

	public void setDelivDateToDat(Date delivDateToDat) {
		this.delivDateToDat = delivDateToDat;
	}

	public String getLdsDeliveryNoteNo() {
		return ldsDeliveryNoteNo;
	}

	public void setLdsDeliveryNoteNo(String ldsDeliveryNoteNo) {
		this.ldsDeliveryNoteNo = ldsDeliveryNoteNo;
	}

	public String getOrderNumberFromSeqUsage() {
		return orderNumberFromSeqUsage;
	}

	public void setOrderNumberFromSeqUsage(String orderNumberFromSeqUsage) {
		this.orderNumberFromSeqUsage = orderNumberFromSeqUsage;
	}
}
