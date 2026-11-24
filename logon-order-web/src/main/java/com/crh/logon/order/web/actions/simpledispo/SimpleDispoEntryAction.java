/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.simpledispo;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.SimpleVehicleSchedulingService;
import com.crh.logon.business.services.utils.DispoCarrierWrapper;
import com.crh.logon.order.web.forms.simpledispo.SimpleDispoForm;
import com.crh.logon.persistence.entities.common.PoolTxEntity;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;
import com.crh.logon.web.utils.TruckPriorityUtil;

/**
 * Struts Entry Action for Simple Vehicle Scheduling
 * 
 * @author AdNovum Informatik AG
 * @version     $Revision$ $Date$
 * 
 * @struts.action name="order_simpledispo_SimpleDispoForm"
 *  path="/simpledispo/SimpleDispoEntry" scope="session"
 *  validate="false"
 *  
 * @struts.action-forward name="success" path="SimpleDispoTile.def"
 */
public class SimpleDispoEntryAction extends SimpleDispoAbstractAction {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SimpleDispoEntryAction.class);
	
	public ActionForward execute(
			ActionMapping mapping, ActionForm form,
			HttpServletRequest request,	HttpServletResponse response)
			throws IOException, ServletException 
		{
			// [[
			ActionForward result = null;
			Object[][] params = {{"mapping", mapping},
								{"form", form},
								{"request", request},
								{"response", response}};
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(this, "execute", params);
			}
			// ]]
			try {
				Authorization.authorize(Authorization.SIMPLE_VEHICLE_DISPATCHING_R);
				setSelectedTab("simpledispo", request);
				
				String forwardName = "success";
				
				SimpleDispoForm helpForm = (SimpleDispoForm)form;
				helpForm.setUserTimeZone(getUserTimezone());
				helpForm.setDate(getUserTimezone().today());

				SimpleVehicleSchedulingService simpleVehicleSchedulingService = getServiceLocator().getSimpleVehicleSchedulingService();
				
				if (helpForm.getTruckPriorities() == null) {
					populateTruckPriorities(helpForm);
				}
				
				if (helpForm.getPools() == null) {
					loadPoolsAndCarriers(simpleVehicleSchedulingService, helpForm, request.getLocale());
				}
				if (helpForm.getPools() == null || helpForm.getPools().isEmpty()) {
					/*
					 * If there are no pools to choose from (eg. because of data authorization), the screen could not be used.
					 * (A warning message describes the situation for the user.)
					 */
					MessageUtils.addActionWarning(request, "dispo.texte.nopoolsorcarriers");
				}
				else {
					/*
					 *  If the user already selected carriers before, but later ends up on the entry,
					 * the vehicle list should be loaded.
					 */
					if (!helpForm.getSelectedCarriers().isEmpty()) {
						loadVehicles(simpleVehicleSchedulingService, helpForm, request.getLocale());
					}
					loadOrders(simpleVehicleSchedulingService, helpForm, request.getLocale());
				}
				return result = mapping.findForward(forwardName);
			}
			finally {
			// [[
				if (logDebugEnabled) {
					LOG.exiting(result);
				}
			// ]]
			}
	}
	
	/**
	 * Loads the list of pools and carriers and removes those pools, which has no carrier assigned.
	 * Sets these lists to the struts form and selects the first pool.
	 */
	private static void loadPoolsAndCarriers(SimpleVehicleSchedulingService simpleVehicleSchedulingService, 
			SimpleDispoForm helpForm, Locale locale) {
		
		//all= true would disable the access restriction, and we need to restrict pools
		Collection<PoolTxEntity> pools = simpleVehicleSchedulingService.getPools(locale);
		
		// Include ONLY the pools in the dropdown where there are actually carriers in it, when respecting authorization rules.
		if (!pools.isEmpty()) {
			/** Initially we want to load the carriers of the first pool which the user has right to access. */
			Collection<DispoCarrierWrapper> firstValidPoolsCarriers = new LinkedList<DispoCarrierWrapper>();
			for (Iterator<PoolTxEntity> it = pools.iterator(); it.hasNext();) {
				PoolTxEntity poolTxEntity = it.next();
				Long poolId = poolTxEntity.getPoolTxPK().getPoolId();
				Collection<DispoCarrierWrapper> carriers = simpleVehicleSchedulingService.getCarriers(poolId);
				if (carriers == null || carriers.isEmpty()) {
					// The pool does not have any carriers set or the user does not have right to access any carriers of this pool, so we remove this pool. 
					it.remove();
				}
				else {
					if (firstValidPoolsCarriers.isEmpty()) {
						firstValidPoolsCarriers = carriers;
					}
				}
			}

			helpForm.setCarriers(firstValidPoolsCarriers);
			helpForm.setPools(pools);
			helpForm.setSelectedPool(0);
			
			helpForm = setAllCarriersSelected(firstValidPoolsCarriers, helpForm);
		}
	}
	
	/**
	 * LOGON-6654: Select all carriers by default.
	 * 
	 * @param carriers The list of carrier wrappers to be selected.
	 * @param simpleDispoForm the SimpleDispoForm struts action form.
	 * 
	 * @return the SimpleDispoForm.
	 */
	static SimpleDispoForm setAllCarriersSelected(Collection<DispoCarrierWrapper> carriers,
			SimpleDispoForm simpleDispoForm) {
		
		Set<Long> carrierIds = new HashSet<Long>();
		for (DispoCarrierWrapper carrierWrapper : carriers) {
			Long carrierId = carrierWrapper.getCarrierId();
			carrierIds.add(carrierId);
		}
		simpleDispoForm.setSelectedCarriers(carrierIds);
		
		return simpleDispoForm;
	}
	
	/**
	 * Creates an array which contains the possible values for Truck Priority 
	 * and sets it to the struts form. 
	 */
	private void populateTruckPriorities(SimpleDispoForm helpForm) {
		long[] truckPriorities = TruckPriorityUtil.createSimpleVehiclePriorityArray();

		helpForm.setTruckPriorities(truckPriorities);
	}
}
