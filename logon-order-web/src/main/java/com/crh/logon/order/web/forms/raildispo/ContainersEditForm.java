/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.raildispo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="order_raildispo_ContainersEditForm"
 */
public class ContainersEditForm extends ActionForm{


    private static final long serialVersionUID = 1;

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(ContainersEditForm.class);

	private Long wagonId;
	private WagonEntity wagon;
	private List containers;
	private Map loads;
	private Map orderItems;
	private List loadIds;
	private List containerIds;
	private List newContainers;
	
	public Long getWagonId() {
		return wagonId;
	}

	public void setWagonId(Long wagonId) {
		this.wagonId = wagonId;
	}

	public WagonEntity getWagon() {
		return wagon;
	}

	public void setWagon(WagonEntity wagon) {
		this.wagon = wagon;
		this.newContainers = new ArrayList(wagon.getWagonType().getNrContainers().intValue());
		for (int i = 0; i < wagon.getWagonType().getNrContainers().intValue(); i++) {
			this.newContainers.add(null);
		}
	}

	/**
     * Default constructor.
     */
    public ContainersEditForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "ContainersEditForm");
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
     * fields on the order.web form.
     */
    private void init() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
			wagonId = null;
			wagon = null;
			containers = null;
			loads = null;
			orderItems = null;
			loadIds = null;
			containerIds = null;
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
			;//PMD
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
			
	        /* no need to validate if 'delete' button was pressed */
	        if (request.getParameter("delete") != null) {
	            return result = null;
	        }
	        if (errors == null) {
	            errors = new ActionErrors();
	        }
			
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

	public List getContainers() {
		return containers;
	}

	public void setContainers(List containers) {
		this.containers = containers;
		this.containerIds = new ArrayList(containers.size());
		Iterator i = containers.iterator();
		int cnt = 0;
		while(i.hasNext()){
			ContainerEntity c = (ContainerEntity)i.next();
			this.containerIds.add(c.getContainerId());
			newContainers.set(cnt++, c.getContainerNo());
		}
	}

	public List getLoadIds() {
		return loadIds;
	}

	public List getContainerIds() {
		return containerIds;
	}

	public void setLoads(List loads) {
		this.loads = new HashMap();
		this.loadIds = new ArrayList(loads.size());
		Iterator i = loads.iterator();
		while(i.hasNext()) {
			LoadEntity l = (LoadEntity)i.next();
			this.loads.put(l.getContainer().getContainerId(), l);
			this.loadIds.add(l.getLoadId());
		}
	}

	public void setOrderItems(List orderItems) {
		this.orderItems = new HashMap();
		Iterator i = orderItems.iterator();
		while(i.hasNext()) {
			OrderItemEntity oi = (OrderItemEntity)i.next();
			this.orderItems.put(oi.getLoad().getLoadId(), oi);
		}
	}

	public Map getLoads() {
		return loads;
	}

	public Map getOrderItems() {
		return orderItems;
	}
	
	public void setNewContainer(int index, String containerNo){
		if ((containerNo != null) && (containerNo.trim().length() == 0)){
			containerNo = null;
		}
		newContainers.set(index, containerNo);
	}

	public List getNewContainers() {
		return newContainers;
	}

}
