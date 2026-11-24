package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.views.DispoAssignPartListEntity;
import com.crh.logon.persistence.entities.views.DispoAssignmentListEntity;
import com.crh.logon.persistence.entities.views.DispoOrderItemListEntity;
import com.crh.logon.persistence.entities.views.DispoTruckTypeListEntity;

public class AssignmentWrapper implements Serializable{

	private static final long serialVersionUID = 1L;

	private DispoAssignmentListEntity assignment;
	private LinkedList<AssignmentPartWrapper> parts;
	private Map<Long, List<DispoOrderItemListEntity>> oMap;
	private int orderCnt;
	private boolean isBag;
	private boolean isLandfill;
	private boolean isReturnPalletReverse;
	private Long commCarrierId;
	private Long execCarrierId;
	private boolean isContainerOnly;
	/** The packagetype code of the first ordered item. */
	private Long logonPackageTypeCd;
	
	private Integer firstLoadIndex;
	private Integer firstUnloadIndex;
	private Integer lastUnloadIndex;
	private Integer indexOfFinishedAssignmentPartAfterLatestUnload;
	private Long highestOrderStatus = 0l;
	private Long highestAssignmentPartStatus = 0l;
	private Integer countOfLoadAssignmentParts = 0;
	private Integer countOfUnloadAssignmentParts = 0;
	
	//prescheduling attributes - have effect on UI in case of truck
	private boolean isPreschedulingRequired;
	private boolean isPreschedulingDone;
	private Long plannedTruckId;
	private Double freightCost;
	private String freightCurrency;
	private boolean onHoldOrderAndLockedFlag;
	// LOGON-6654 Different colors for on-time orders
	private boolean isOnTimeOrder = false;
	private boolean isSchedulingOrAfter = false;
	
	private boolean isNotAuthorized = false;
	
	private Long custorderNumber;
	/** Holds the possible truck type matches for the order of the assignment. */
	private List<DispoTruckTypeListEntity> truckTypeList;
	
	/** The list of order status codes. */
	private List<Long> orderStatusCdList;

	private TimeZone timeZone;

	public AssignmentWrapper(DispoAssignmentListEntity assignment, boolean isNotAuthorized, TimeZone timeZone) {
		this.assignment = assignment;
		this.isNotAuthorized = isNotAuthorized;
		parts = new LinkedList<AssignmentPartWrapper>();
		oMap = new HashMap<Long, List<DispoOrderItemListEntity>>();
		isBag = false;
		commCarrierId = null;
		execCarrierId = null;
		isContainerOnly = false;
		orderStatusCdList = new ArrayList<Long>();
		setFirstUnloadIndex(null);
		this.timeZone = timeZone;
	}

	public AssignmentPartWrapper addPart(DispoAssignPartListEntity assignmentPart) {
		Long orderStatusCd = assignmentPart.getOrderStatusCd();
		Long assignmentPartTypeCd = assignmentPart.getAssignmentPartTypeCd();
		
		List<DispoOrderItemListEntity> items = oMap.get(assignmentPart.getOrderId());
		if (items == null) {
			items = new LinkedList<DispoOrderItemListEntity>();
			oMap.put(assignmentPart.getOrderId(), items);
		}
		
		AssignmentPartWrapper curr = new AssignmentPartWrapper(assignmentPart, items, timeZone);
		if (!parts.isEmpty()) {
			AssignmentPartWrapper last = parts.getLast();
			last.checkDuration(curr);
		}
		parts.add(curr);
		Integer indexOfCurrentAssignmentPart = parts.size() -1;
		if (assignmentPartTypeCd == Constant.ASSIGNMENT_PART_TYPE_UNLOAD) {
			orderCnt++;
			countOfUnloadAssignmentParts++;
			if (getFirstUnloadIndex() == null) {
				setFirstUnloadIndex(indexOfCurrentAssignmentPart);
			}
			setLastUnloadIndex(indexOfCurrentAssignmentPart);
		}
		else if(assignmentPartTypeCd == Constant.ASSIGNMENT_PART_TYPE_LOAD) {
			countOfLoadAssignmentParts ++;
			if (getFirstLoadIndex() == null) {
				setFirstLoadIndex(indexOfCurrentAssignmentPart);
			}
		}
		else if(assignmentPartTypeCd == Constant.ASSIGNMENT_PART_TYPE_FINISHED 
					&& getLastUnloadIndex() != null) {
			setIndexOfFinishedAssignmentPartAfterLatestUnload(indexOfCurrentAssignmentPart);
		}
		
		if (assignmentPart.getCommCarrierId() != null){
			commCarrierId = assignmentPart.getCommCarrierId();
		}
		if (assignmentPart.getCommCarrierId() != null){
			execCarrierId = assignmentPart.getExecCarrierId();
		}
		if ( assignmentPart.getOnHoldOrderAndLockedFlag() != null && assignmentPart.getOnHoldOrderAndLockedFlag().booleanValue() ) {
			this.onHoldOrderAndLockedFlag = true;
		}
		if(assignmentPart.getCustorderNumber() != null){
			custorderNumber = assignmentPart.getCustorderNumber();
		}
		if(orderStatusCd != null) {
			orderStatusCdList.add(orderStatusCd);
			if (getHighestOrderStatus() < orderStatusCd) {
				setHighestOrderStatus(orderStatusCd);
			}
		}
		
		if (getHighestAssignmentPartStatus() < assignmentPart.getAssignPartStatusCd()) {
			setHighestAssignmentPartStatus(assignmentPart.getAssignPartStatusCd());
		}
		if(assignmentPart.getDeliveryFromDat() != null && assignmentPart.getDeliveryToDat() != null) {
			if(assignmentPart.getDeliveryFromDat().compareTo(assignmentPart.getDeliveryToDat()) == 0) {
				isOnTimeOrder = true;
			}
		}
		if (LogonStatusUtils.isDispatchedOrBeyond(orderStatusCd)) {
			isSchedulingOrAfter = true;
		}
		
		return curr;
	}
	
	public DispoAssignmentListEntity getAssignment() {
		return assignment;
	}

	public LinkedList<AssignmentPartWrapper> getParts() {
		return parts;
	}

	public void addItem(DispoOrderItemListEntity orderItem) {
		List<DispoOrderItemListEntity> items = oMap.get(orderItem.getOrderId());
		items.add(orderItem);
		if (orderItem.getMaterialPackageCd().equals(Constant.MATERIAL_PACKAGE_BAG) ||
				orderItem.getMaterialPackageCd().equals(Constant.MATERIAL_PACKAGE_PALET_EMPTY)) {
			isBag = true;
		}
		
		isLandfill = orderItem.getIslandfill();
		isReturnPalletReverse = LogonUtil.isReturnDeliveredOrder(orderItem.getDeliveryTypeCd());
				 		
		if (orderItem.getUomCd().equals(Constant.UOM_CONTAINER)){
			isContainerOnly = true;
		}
		if (items.size() == 1) {
			logonPackageTypeCd = orderItem.getLogonPackageTypeCd();
		}
	}
	
	public boolean getIsBag(){
		return isBag;
	}


	public int getOrderCnt() {
		return orderCnt;
	}

	public void initialize() {
		for (AssignmentPartWrapper ap : parts) {
			ap.initialize();
		}
	}

	public boolean getIsContainerOnly() {
		return isContainerOnly;
	}

	/**
	 * Sums up the assignment part's revenue planning data, and stores on assignment level.
	 */
	public void initPlanningRevenueAttributes() {
		
		for (AssignmentPartWrapper curr : parts) {
			if ( curr.isPreschedulingRequired() ) {			
				isPreschedulingRequired = true;
			}
			if ( curr.isPreschedulingDone() ) {
				isPreschedulingDone = true;
			}
			if (curr.getFreightCurrency() != null) {
				freightCurrency = curr.getFreightCurrency(); 				
			}
			if ( curr.getFreightCost() != null ) {
				freightCost = (freightCost != null ? freightCost : 0) + curr.getFreightCost();
			}
			if (curr.getPlannedTruckId() != null ) {				
				plannedTruckId = curr.getPlannedTruckId();
			}
		}
		
	}

	public Double getFreightCost() {
		return freightCost;
	}

	public String getFreightCurrency() {
		return freightCurrency;
	}

	public boolean getIsPreschedulingRequired() {
		return isPreschedulingRequired;
	}

	public boolean getIsPreschedulingDone() {
		return isPreschedulingDone;
	}

	public Long getCommCarrierId() {
		return commCarrierId;
	}

	public Long getExecCarrierId() {
		return execCarrierId;
	}

	public Long getPlannedTruckId() {
		return plannedTruckId;
	}

	public boolean getIsNotAuthorized() {
		return isNotAuthorized;
	}

	public boolean getIsOnHoldOrderAndLockedFlag() {
		return onHoldOrderAndLockedFlag;
	}
	
	public boolean getIsOnTimeOrder() {
		return isOnTimeOrder;
	}
	
	public boolean getIsSchedulingOrAfter() {
		return isSchedulingOrAfter;
	}
	
	/**
	 * @return the custorderNumber
	 */
	public Long getCustorderNumber() {
		return custorderNumber;
	}

	/**
	 * @param custorderNumber the custorderNumber to set
	 */
	public void setCustorderNumber(Long custorderNumber) {
		this.custorderNumber = custorderNumber;
	}

	public List<Long> getOrderStatusCdList() {
		return orderStatusCdList;
	}
	
	public void setOrderStatusCdList(List<Long> orderStatusCdList) {
		this.orderStatusCdList = orderStatusCdList;
	}

	public List<DispoTruckTypeListEntity> getTruckTypeList() {
		return truckTypeList;
	}

	public void setTruckTypeList(List<DispoTruckTypeListEntity> truckTypeList) {
		this.truckTypeList = truckTypeList;
	}

	public Long getLogonPackageTypeCd() {
		return logonPackageTypeCd;
	}

	public void setLogonPackageTypeCd(Long logonPackageTypeCd) {
		this.logonPackageTypeCd = logonPackageTypeCd;
	}

	public boolean getIsLandfill() {
		return isLandfill;
	}

	public void setLandfill(boolean isLandfill) {
		this.isLandfill = isLandfill;
	}

	public Integer getFirstUnloadIndex() {
		return firstUnloadIndex;
	}

	public void setFirstUnloadIndex(Integer firstUnloadIndex) {
		this.firstUnloadIndex = firstUnloadIndex;
	}

	public Long getHighestOrderStatus() {
		return highestOrderStatus;
	}

	public void setHighestOrderStatus(Long highestOrderStatus) {
		this.highestOrderStatus = highestOrderStatus;
	}

	public Long getHighestAssignmentPartStatus() {
		return highestAssignmentPartStatus;
	}

	public void setHighestAssignmentPartStatus(
			Long highestAssignmentPartStatus) {
		this.highestAssignmentPartStatus = highestAssignmentPartStatus;
	}

	public Integer getFirstLoadIndex() {
		return firstLoadIndex;
	}

	public void setFirstLoadIndex(Integer firstLoadIndex) {
		this.firstLoadIndex = firstLoadIndex;
	}

	public Integer getCountOfUnloadAssignmentParts() {
		return countOfUnloadAssignmentParts;
	}

	public void setCountOfUnloadAssignmentParts(
			Integer countOfUnloadAssignmentParts) {
		this.countOfUnloadAssignmentParts = countOfUnloadAssignmentParts;
	}

	public Integer getCountOfLoadAssignmentParts() {
		return countOfLoadAssignmentParts;
	}

	public void setCountOfLoadAssignmentParts(Integer countOfLoadAssignmentParts) {
		this.countOfLoadAssignmentParts = countOfLoadAssignmentParts;
	}

	public Integer getIndexOfFinishedAssignmentPartAfterLatestUnload() {
		return indexOfFinishedAssignmentPartAfterLatestUnload;
	}

	public void setIndexOfFinishedAssignmentPartAfterLatestUnload(
			Integer indexOfFinishedAssignmentPartAfterLatestUnload) {
		this.indexOfFinishedAssignmentPartAfterLatestUnload = indexOfFinishedAssignmentPartAfterLatestUnload;
	}

	public Integer getLastUnloadIndex() {
		return lastUnloadIndex;
	}

	public void setLastUnloadIndex(Integer lastUnloadIndex) {
		this.lastUnloadIndex = lastUnloadIndex;
	}

	public boolean getIsReturnPalletReverse() {
		return isReturnPalletReverse;
	}

	public void setReturnPallet(boolean isReturnPalletReverse) {
		this.isReturnPalletReverse = isReturnPalletReverse;
	}
}
