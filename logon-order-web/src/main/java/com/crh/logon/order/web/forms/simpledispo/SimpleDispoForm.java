/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.simpledispo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.beans.SimpleVehicleAssignmentInfo;
import com.crh.logon.business.services.beans.UnallocatedOrderData;
import com.crh.logon.business.services.utils.DispoCarrierWrapper;
import com.crh.logon.order.web.forms.dispo.AbstractDispoForm;
import com.crh.logon.persistence.entities.common.PoolTxEntity;

/**
 * Struts form for Simple Vehicle Scheduling
 * 
 * @author AdNovum Informatik AG
 * @version     $Revision$ $Date$
 * 
 * @struts.form name="order_simpledispo_SimpleDispoForm"
 */
public class SimpleDispoForm extends AbstractDispoForm {

	private static final long serialVersionUID = 4084479693116495278L;

	public static final String FORM_NAME = "order_simpledispo_SimpleDispoForm";

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SimpleDispoForm.class);
	
	/* lists & dropdowns */
	/** The collection of pools to choose from. */
    private Collection<PoolTxEntity> pools;
    /** The collection of carriers to choose from, which are assignemd to the selected pool. */
	private Collection<DispoCarrierWrapper> carriers;
	/** The collection of vehicles to display. */
	private Collection<SimpleVehicleAssignmentInfo> vehicles;
	/** The collection of orders to display. */
	private Collection<UnallocatedOrderData> orders;
	/** The possible Truck priority values to choose from */
	private long[] truckPriorities;
	
	/* user data */ 
    /** The list of the IDs of the selected carriers */
    private Set<Long> selectedCarriers;
    /** The index of the selected pool from pools list. */
    private int selectedPool;
    
    /** This bean provides data for the load vehicle popup. */
    private SimpleVehicleLoadBean simpleVehicleLoadBean;
    
    /** This bean provides data for the deallocate popup. */
    private SimpleVehicleDeallocateBean simpleVehicleDeallocateBean;

    /** This bean provides data for the unload order popup. */ 
    private SimpleVehicleUnloadBean simpleVehicleUnloadBean;
    
    /**
     * Default constructor.
     */
    public SimpleDispoForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "SimpleDispoForm");
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
    protected void init() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
			setPools(null);
			setCarriers(null);
			setSelectedCarriers(new HashSet<Long>());
			setOrders(new ArrayList<UnallocatedOrderData>());
			setSelectedPool(0);
			setVehicles(null);
			setTruckPriorities(null);
			setSimpleVehicleLoadBean(null);
			setSimpleVehicleDeallocateBean(null);
			setSimpleVehicleUnloadBean(null);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }
    
    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
    	super.reset(mapping, request);
    	/*
    	 *  If the post does not contain the checkbox fields of simpleVehicleDeallocateBean.selectedOrderIds, 
    	 *  these should be set to non-checked (empty string). 
    	 */
    	if (simpleVehicleDeallocateBean != null && simpleVehicleDeallocateBean.getSelectedOrderIds() != null) {
    		int length = simpleVehicleDeallocateBean.getSelectedOrderIds().length;
    		int i = 0;
			while(i < length) {
				Object param = request.getParameter("simpleVehicleDeallocateBean.selectedOrderIdIndexed[" + i + "]");
				if(param == null) {
					simpleVehicleDeallocateBean.setSelectedOrderIdIndexed(i, "");
				}
				i++;
			}
    	}
    	/* 
    	 * This is needed for the release To SAP Flag on the unload popup.
    	 * The unchecked values does not get posted to the server, so we need to "uncheck" it here. 
    	 */
		if (simpleVehicleUnloadBean != null
				&& request.getParameter("simpleVehicleUnloadBean.initialReleaseToSAPFlag") == null) {
			simpleVehicleUnloadBean.setInitialReleaseToSAPFlag(false);
		}
    }

	public Collection<PoolTxEntity> getPools() {
		return pools;
	}

	public void setPools(Collection<PoolTxEntity> pools) {
		this.pools = pools;
	}

	public Collection<DispoCarrierWrapper> getCarriers() {
		return carriers;
	}

	public void setCarriers(Collection<DispoCarrierWrapper> carriers) {
		this.carriers = carriers;
	}
	
	/**
	 * The SimpleDispoForm stores only the index of the selected pool not the poolId itself.
	 * These method
	 */
	public Long getSelectedPoolId() {
		Long selectedPoolId = null;
		
		int idx = 0;
		for (Iterator<PoolTxEntity> it = pools.iterator(); it.hasNext();) {
			PoolTxEntity pool = it.next();
			if (idx == selectedPool) {
				selectedPoolId = pool.getPoolTxPK().getPoolId();
				break;
			}
			idx++;
		}
		return selectedPoolId;
	}

	public int getSelectedPool() {
		return selectedPool;
	}

	public void setSelectedPool(int selectedPool) {
		this.selectedPool = selectedPool;
	}

	public Set<Long> getSelectedCarriers() {
		return selectedCarriers;
	}

	public void setSelectedCarriers(Set<Long> selectedCarriers) {
		this.selectedCarriers = selectedCarriers;
	}

	public Collection<SimpleVehicleAssignmentInfo> getVehicles() {
		return vehicles;
	}

	public void setVehicles(Collection<SimpleVehicleAssignmentInfo> vehicles) {
		this.vehicles = vehicles;
	}

	public Collection<UnallocatedOrderData> getOrders() {
		return orders;
	}

	public void setOrders(Collection<UnallocatedOrderData> orders) {
		this.orders = orders;
	}

	public long[] getTruckPriorities() {
		return truckPriorities;
	}

	public void setTruckPriorities(long[] truckPriorities) {
		this.truckPriorities = truckPriorities;
	}

	public SimpleVehicleLoadBean getSimpleVehicleLoadBean() {
		return simpleVehicleLoadBean;
	}

	public void setSimpleVehicleLoadBean(SimpleVehicleLoadBean simpleVehicleLoadBean) {
		this.simpleVehicleLoadBean = simpleVehicleLoadBean;
	}

	public SimpleVehicleDeallocateBean getSimpleVehicleDeallocateBean() {
		return simpleVehicleDeallocateBean;
	}

	public void setSimpleVehicleDeallocateBean(SimpleVehicleDeallocateBean simpleVehicleDeallocateBean) {
		this.simpleVehicleDeallocateBean = simpleVehicleDeallocateBean;
	}

	public SimpleVehicleUnloadBean getSimpleVehicleUnloadBean() {
		return simpleVehicleUnloadBean;
	}

	public void setSimpleVehicleUnloadBean(SimpleVehicleUnloadBean simpleVehicleUnloadBean) {
		this.simpleVehicleUnloadBean = simpleVehicleUnloadBean;
	}
}
