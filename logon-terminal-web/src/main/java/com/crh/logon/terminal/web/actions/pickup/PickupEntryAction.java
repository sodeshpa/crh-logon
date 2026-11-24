/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.terminal.web.actions.pickup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.PickupService;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.PickupLoadingpointEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.views.PickupTerminalEntity;
import com.crh.logon.terminal.web.actions.TerminalActionSupport;
import com.crh.logon.terminal.web.forms.PickupTerminalOrderDTO;
import com.crh.logon.terminal.web.forms.pickup.PickupForm;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="terminal_PickupForm"
 *  path="/pickup/PickupEntry" scope="session"
 *  validate="true"
 *
 * @struts.action-forward name="success" path="PickupListTile.def"
 * 
 */
public class PickupEntryAction extends TerminalActionSupport {

	private static final String FORWARD_SUCCESS = "success";
	private static final String FORWARD_FATAL_ERROR = "fatalError";
	
	public static final String ACTION_URL = "/pickup/PickupEntry.do";
	
    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(PickupEntryAction.class);

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
			Authorization.authorize(Authorization.PICKUP_ORDER_TERMINAL_R);
			PickupForm pickupForm = (PickupForm) form;
			pickupForm.clear();
			
			final PickupService pickupService = getServiceLocator().getPickupService();
			List shipToList =  pickupService.fetchPickupShipTosWithoutSarea();
			if ( shipToList == null || shipToList.isEmpty() ) {
				MessageUtils.addActionError(request, "pickup.errors.shiptonotavailable");
				return result = mapping.findForward(FORWARD_SUCCESS);
			}
			
			pickupForm.setShiptoList(shipToList);
			pickupForm.presetShipto();
			LoadingpointEntity loadingpoint = getLoadingpoint();
			
			if ( loadingpoint == null) {
				//shippingpoint, loadingpoint should be known - where the terminal stands
				return result = mapping.findForward(FORWARD_FATAL_ERROR);
			}
			
			ShippingpointEntity shippingpoint = loadingpoint.getShippingpoint();
			Long shippingpointId = shippingpoint.getShippingpointId();
			Long loadingpointId = loadingpoint.getLoadingpointId();
			
			PickupLoadingpointEntity pickupLoadingpoint = getPickupLoadingpoint();
			Boolean bulkPickupCreationFlag = pickupLoadingpoint.getBulkPickupCreationFlag();
			Locale locale = request.getLocale();
			List<PickupTerminalEntity> pickupTerminalOrders = pickupService.loadOrders(locale,
					shippingpointId,
					loadingpointId, bulkPickupCreationFlag);
			
			TimeZone shippingPointTimeZone = getTimeZoneService().loadForShippingPoint(
					shippingpoint, locale);
			pickupForm.setOrderDTOList(convertToPickupTerminalOrderDTOList(pickupTerminalOrders, shippingPointTimeZone));
			pickupForm.setShippingPointTimeZone(shippingPointTimeZone);
			
			Boolean ordersLoadableTerminalFlag = pickupLoadingpoint.getOrdersLoadableTerminalFlag();
			pickupForm.setOrdersLoadableTerminalFlag(ordersLoadableTerminalFlag);			
			
			return result = mapping.findForward(FORWARD_SUCCESS);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	private List<PickupTerminalOrderDTO> convertToPickupTerminalOrderDTOList(
			List<PickupTerminalEntity> pickupTerminalOrderList, TimeZone shippingpointTimeZone) {
		List<PickupTerminalOrderDTO> terminalOrderDTOList = new ArrayList<PickupTerminalOrderDTO>();
		for (PickupTerminalEntity pickupTerminalOrder : pickupTerminalOrderList) {
			PickupTerminalOrderDTO terminalOrderDTO = new PickupTerminalOrderDTO(
					pickupTerminalOrder,
					shippingpointTimeZone);
			terminalOrderDTOList.add(terminalOrderDTO);
		}
		return terminalOrderDTOList;
	}

}
