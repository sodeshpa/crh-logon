package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.views.DispoAssignPartListEntity;
import com.crh.logon.persistence.entities.views.DispoOrderItemListEntity;
import com.crh.logon.persistence.entities.views.DispoRailAssListEntity;

public class RailAssignmentWrapper implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private DispoRailAssListEntity assignment;
	private LinkedList<AssignmentPartWrapper> parts;
	private HashMap<Long, List<DispoOrderItemListEntity>> oMap;
	private List<AssignmentPartWrapper> upList;
	private boolean isBag;
	private TimeZone timeZone;
	
	public RailAssignmentWrapper(DispoRailAssListEntity assignment, TimeZone timeZone) {
		this.assignment = assignment;
		parts = new LinkedList<AssignmentPartWrapper>();
		oMap = new HashMap<Long, List<DispoOrderItemListEntity>>();
		isBag = false;
		upList = new LinkedList<AssignmentPartWrapper>();
		this.timeZone = timeZone;
	}

	public void addPart(DispoAssignPartListEntity assignmentPart) {
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
		if (assignmentPart.getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_UNLOAD) {
			upList.add(curr);
		}
	}
	
	public DispoRailAssListEntity getAssignment() {
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
	}
	
	public boolean getIsBag(){
		return isBag;
	}

	public int getOrderCnt() {
		return upList.size();
	}
	
	public List<AssignmentPartWrapper> unloadParts() {
		return upList;
	}


}
