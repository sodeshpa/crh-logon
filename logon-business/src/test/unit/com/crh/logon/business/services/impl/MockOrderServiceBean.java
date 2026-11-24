/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.ArrayList;
import java.util.List;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.localinterfaces.AsyncMopEventOutServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TruckAdminServiceLocalInterface;
import com.crh.logon.business.services.utils.AssignmentUtils;
import com.crh.logon.business.services.utils.ConfSeqLock;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.GoodsMovementServiceLocalInterface;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusBsEntity;

/**
 * @author AdNovum Informatik AG
 */
public class MockOrderServiceBean extends OrderServiceBean {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(MockOrderServiceBean.class);
	
	private List<OrderItemEntity> orderItems;
	private AssignmentEntity assignment;
	private List<AssignmentPartEntity> assignmentParts;
	
	
	public void setOrderItems(List<OrderItemEntity> orderItems) {
		this.orderItems = orderItems;
	}

	public void setAssignment(AssignmentEntity assignment) {
		this.assignment = assignment;
	}
	
	public void setAssignmentParts(List<AssignmentPartEntity> assignmentParts) {
		this.assignmentParts = assignmentParts;
	}
	
	public void setTruckAdminService(TruckAdminServiceLocalInterface truckAdminService) {
		this.truckAdminService = truckAdminService;
	}

	public void setRefDataGenericsService(RefDataGenericsServiceLocalInterface refDataGenericsService) {
		this.referentialDataService = refDataGenericsService;
	}
	
	public void setGoodsMovementService(GoodsMovementServiceLocalInterface goodsMovementService) {
		this.goodsMovementService = goodsMovementService;
	}

	public void setDispoService(DispoServiceLocalInterface dispoService) {
		this.dispoService = dispoService;
	}


	public void setAsyncMopEventOutService(AsyncMopEventOutServiceLocalInterface asyncMopEventOutService) {
		this.asyncMopEventOutService = asyncMopEventOutService;
	}

	
	@Override
	public List<OrderItemEntity> getOrderItems(LoadEntity load) {
		if (load == null) {
			return null;
		}
		
		List<OrderItemEntity> orderItemList = new ArrayList<OrderItemEntity>();
		if (orderItems != null) {
			for (OrderItemEntity orderItem : orderItems) {
				if (orderItem.getLoad() != null 
						&& orderItem.getLoad().getLoadId().equals(load.getLoadId())
						&& !orderItemList.contains(orderItem)) {
					orderItemList.add(orderItem);
				}
			}
		}
		
		return orderItemList;
	}
	
	@Override
	public AssignmentPartEntity getAssignmentPartLoad(LoadEntity load) {
		for (AssignmentPartEntity assignmentPart : assignmentParts) {
			if (assignmentPart.getLoad() != null && assignmentPart.getLoad().getLoadId().equals(load.getLoadId())) {
				return assignmentPart;
			}
		}
		return null;
	}
	
	@Override
	protected OrderStatusBsEntity findOrderStatusBs(Long statusCd) {
		OrderStatusBsEntity orderStatusBs = new OrderStatusBsEntity();
		orderStatusBs.setOrderStatusCd(statusCd);
		return orderStatusBs;
	}
	
	@Override
	protected long countNotLoadedItems(LoadEntity load, OrderEntity order, boolean strict) {
		// [[
		long result = 0L;
		Object[][] params = {{"load", load},
							{"order", order},
							{"strict", strict + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "countNotLoadedItems", params);
		}
		// ]]
		try {
			if (strict){
				if ( load != null ) {
					Long res = null;
					for (OrderItemEntity orderItem : orderItems) {
						if (orderItem.getLoad() != null
								&& !orderItem.getLoad().getLoadId().equals(load.getLoadId())
								&& orderItem.getOrder().getOrderId().equals(order.getOrderId())
								&& (load.getLoadingTs() == null || load.getNetQuantity() == null)) {
							if (res == null) {
								res = 0L;
							}
							res++;
						}
						else {
							res = 0L;
						}
						
					}
					if ( res == null ) {
						LOG.error("Error when querying for not loaded loads for order " + order.getOrderId());
						return result;
					}
					else{						
						return result = res.longValue();
					}
				}
				else {
					LOG.info("not supported functionality in test");
					return result;
				}
			}
			else {
				LOG.info("not supported functionality in test");
				return result;
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	@Override
	protected void updateCustomSequenceNumbers(LoadEntity load, ConfSeqLock lock) {
		LOG.info("updateCustomSequenceNumbers is not covered by this test");
	}
	
	
	@Override
	protected List<AssignmentPartEntity> getAssignmentPartsInSeqOrder(Long assignmentId) {
		return AssignmentUtils.getSortedAssignmentParts(assignment);
	}

}
