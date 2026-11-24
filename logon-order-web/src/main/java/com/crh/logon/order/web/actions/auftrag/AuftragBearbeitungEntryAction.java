/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.auftrag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.views.OrdItemAssignmtLoadEntity;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.constants.Constant;
import com.crh.logon.order.web.forms.auftrag.AuftragBearbeitungForm;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.DropdownEntity;
import com.crh.logon.web.utils.PlantSareaUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_auftrag_AuftragBearbeitungForm"
 *  path="/auftrag/AuftragBearbeitungEntry" scope="session"
 *  validate="true"
 *
 * @struts.action-forward name="success" path="AuftragBearbeitungTile.def"
 * @struts.action-forward name="initfailed" path="/auftrag/AuftragBearbeitungEntry.do"
 */
public class AuftragBearbeitungEntryAction extends OrderActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(AuftragBearbeitungEntryAction
			.class.getName());

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
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
			Authorization.authorize(Authorization.SHIPPING_R);
			setSelectedTab("auftragbearbeitung", request);
			
			AuftragBearbeitungForm helpForm = (AuftragBearbeitungForm) form;
			helpForm.clear();
			
			String forwardName = "success";
			String plantCd = helpForm.getSelectedPlantCd();
			
			//load available plants for user
			List<DropdownEntity> listPlant = PlantSareaUtils.getPlantList(getPlantSareaService());
			helpForm.setSelectablePlants(listPlant);
			if (listPlant != null && !listPlant.isEmpty()){					
				//select the first plant
				plantCd = listPlant.get(0).getValue();
				helpForm.setSelectedPlantCd(plantCd);
			
				loadStatusesAndInitSelection(helpForm, request);
			
				//load all shipping points availanle for the plant and user combination - leave it unselected
				List listShippingpoint = getServiceLocator().getOrderChangeService().loadShippingPointsForPlant(plantCd);
				helpForm.setSelectableShippingPoints(listShippingpoint);

				// choose the first shippingpoint for timezone conversions
				// realistically, all shippingpoints should be in the same timezone, but we need to verify
				// this assumption
				if (!listShippingpoint.isEmpty()) {
					ShippingpointEntity entity = (ShippingpointEntity) listShippingpoint.get(0);
					TimeZone tz = getTimeZoneService().loadForShippingPoint(entity, getUserLocale());
					helpForm.setShippingpointTZ(tz);
				}
			}

			// Guarantee a timezone even if it is the default
			if (helpForm.getShippingpointTZ() == null) {
				helpForm.setShippingpointTZ(getTimeZoneService().createDefault(getUserLocale()));
			}
			TimeZone shippingPointTimeZone = helpForm.getShippingpointTZ();
			
			//--- Setup our initial from/to dates (this isn't possible from within Form.init()) ---//
			Date fromDate = shippingPointTimeZone.today();
			helpForm.setFromDate(fromDate);
			helpForm.setSearchFromDate(shippingPointTimeZone.fmtAsDayMonthYear(fromDate));

			Calendar toDate = helpForm.getShippingpointTZ().asCalendar();
			toDate.setTime(fromDate);
			toDate.add(Calendar.DAY_OF_MONTH, 1);
			toDate.add(Calendar.SECOND, 1);
			helpForm.setSearchToDate(shippingPointTimeZone.fmtAsDayMonthYear(toDate.getTime()));
			toDate.add(Calendar.DAY_OF_MONTH, 1); //have to add 1 more day to the toDate only, to show the results correctly
			helpForm.setToDate(toDate.getTime());
			helpForm.setSelectedShippingPointId(AuftragBearbeitungForm.EMPTY_SELECTION);
			helpForm.setOrderList(new ArrayList<OrdItemAssignmtLoadEntity>(), new HashMap<Long, Date>());
			
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
	 * Loads all available order status and preselects them all.
	 * @param helpForm
	 */
	private void loadStatusesAndInitSelection(AuftragBearbeitungForm helpForm, HttpServletRequest request) {
		Authorization.authorize(Authorization.SHIPPING_R);
		//load available order status - select them all for the first time
		 Long [] selectableStatuses = {
					Constant.ORDER_STATUS_NEW,
					Constant.ORDER_STATUS_TRANSMITTED_TO_POOL,
					Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK,
					Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER,
					Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK,
					Constant.ORDER_STATUS_DISPATCHED,				 
					Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER,
					Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK ,
					Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT,
					Constant.ORDER_STATUS_LOADING,
					Constant.ORDER_STATUS_LOADED,
					Constant.ORDER_STATUS_SHIPPING,
					Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT,
					Constant.ORDER_STATUS_UNLOADING,
					Constant.ORDER_STATUS_DELIVERED,
					Constant.ORDER_STATUS_DELETED
				};
		List statuses = getServiceLocator().getOrderChangeService().findOrderStatuses(request.getLocale(), Arrays.asList(selectableStatuses));
		
		helpForm.setSelectableStatusList(statuses);
		
		long [] initialSelection = {
				Constant.ORDER_STATUS_NEW,
				Constant.ORDER_STATUS_TRANSMITTED_TO_POOL,
				Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK,
				Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER,
				Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK,
				Constant.ORDER_STATUS_DISPATCHED,
				Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER,	
				Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK,	
				Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT,
				Constant.ORDER_STATUS_LOADING};
		helpForm.setSelStatus(initialSelection);
		helpForm.setSelectedStatus(initialSelection);
	}

}
