package com.crh.logon.business.services.beans;

import java.io.Serializable;

import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;

public class OrderAndAssignmentDTO implements Serializable {
	
	private OrderEntity order;
	
	private AssignmentEntity assignMent;
	
	public OrderEntity getOrder() {
		return order;
	}
	public void setOrder(OrderEntity order) {
		this.order = order;
	}
	public AssignmentEntity getAssignMent() {
		return assignMent;
	}
	public void setAssignMent(AssignmentEntity assignMent) {
		this.assignMent = assignMent;
	}
	
	public OrderAndAssignmentDTO(OrderEntity order, AssignmentEntity assignment) {
		this.order = order;
		this.assignMent = assignment;
	}

}
