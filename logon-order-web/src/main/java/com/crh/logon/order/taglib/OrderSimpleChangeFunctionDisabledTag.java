/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.taglib;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.beans.OrderChangeLogic;
import com.crh.logon.order.web.forms.auftrag.AuftragBearbeitungForm;
import com.crh.logon.persistence.entities.common.LdpointMatRestrEntity;
import com.crh.logon.persistence.entities.views.OrdItemAssignmtLoadEntity;
import com.crh.logon.persistence.entities.views.ShippBimodalRoadRailEntity;
import com.crh.logon.utils.StringUtils;

/**
 * Used to determine whether functionality is available for a given order.
 * @author AdNovum Informatik AG
 */
public class OrderSimpleChangeFunctionDisabledTag extends TagSupport { 
	
	private static final String C_NAME = 
		OrderSimpleChangeFunctionDisabledTag.class.getName();

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(C_NAME);

	/**
	 * The context key for getting the value for {@link #lpObject}
	 */
	private String orderKey = null;
	
	private OrdItemAssignmtLoadEntity order = null;
	
	private Long loadId  = null;
	
	private int functionality = -1;
	
	private List orderList = null;

	public String getOrderKey() {
		return orderKey;
	}

	public void setOrderKey(String orderKey) {
		this.orderKey = orderKey;
	}

	public OrdItemAssignmtLoadEntity getOrder() {
		return order;
	}

	public void setOrder(OrdItemAssignmtLoadEntity order) {
		this.order = order;
	}

	public int getFunctionality() {
		return functionality;
	}
	
	public void setFunctionality(int functionality) {
		this.functionality = functionality;
	}
	
	private boolean disabled = true;
	
	public int doStartTag() throws JspException {
		int result = 0;
		if (StringUtils.notEmpty(orderKey)){				
			order = (OrdItemAssignmtLoadEntity) pageContext.findAttribute(orderKey);
		}
		else if (loadId != null && orderList != null){
			Iterator it = orderList.iterator();
			while (it.hasNext()) {
				OrdItemAssignmtLoadEntity type = (OrdItemAssignmtLoadEntity) it.next();
				if (type.getLoadId().equals(Long.valueOf(loadId))){
					order = type;
					break;
				}
			}
		}
		AuftragBearbeitungForm form = (AuftragBearbeitungForm) pageContext.getSession().getAttribute(AuftragBearbeitungForm.FORM_NAME);
		
		Map<Long, List<LdpointMatRestrEntity>> orderLoadingPointListMapping = form.getOrderLoadingPointListMapping();
		Map<Long, ShippBimodalRoadRailEntity> bimodalRoadAndRelatedRailInfo = form.getBimodalRoadAndRelatedRailInfoMap();
		Map<Long, Date> truckNextMinPlannedStartMap = form.getTruckNextOrderMapping();
		
		OrderChangeLogic orderChangeLogic = new OrderChangeLogic(truckNextMinPlannedStartMap, 
				orderLoadingPointListMapping, bimodalRoadAndRelatedRailInfo);
		
		this.disabled = orderChangeLogic.disableSimpleChangeFunctionality(order, functionality);
		return result = EVAL_BODY_INCLUDE;
	}
	
	public int doEndTag(){
		int i = EVAL_PAGE;
		this.disabled = true;
		return i;
	}

	public List getOrderList() {
		return orderList;
	}

	public void setOrderList(List orderList) {
		this.orderList = orderList;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public Long getLoadId() {
		return loadId;
	}

	public void setLoadId(Long loadId) {
		this.loadId = loadId;
	}

}
