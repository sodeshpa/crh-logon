/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.terminal.web.actions.bagterminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.crh.logon.business.services.interfaces.BagTerminalService;
import com.crh.logon.business.services.interfaces.TruckAdminService;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.PickupLoadingpointEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.views.BagTerminalEntity;
import com.crh.logon.terminal.web.actions.TerminalActionSupport;
import com.crh.logon.terminal.web.forms.BagTerminalOrderDTO;
import com.crh.logon.terminal.web.forms.bagterminal.BagTerminalForm;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.UserProvider;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="terminal_BagTerminalForm"
 *  path="/bagterminal/BagTerminalEntry" scope="session"
 *  validate="true"
 *
 * @struts.action-forward name="success" path="BagTerminalAssignmentListTile.def"
 * @struts.action-forward name="initfailed" path="/bagterminal/BagTerminalEntry.do"
 */
public class BagTerminalEntryAction extends TerminalActionSupport {

	private static final String FORWARD_SUCCESS = "success";
	
	public static final String ACTION_URL = "/bagterminal/BagTerminalEntry.do";
	
	private static final JcanLogger LOG =
			JcanLoggerFactory.getLogger(BagTerminalEntryAction.class);

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
			Authorization.authorize(Authorization.BAG_ORDER_TERMINAL_R);
			BagTerminalForm bagTerminalForm = (BagTerminalForm) form;
			
			bagTerminalForm.clear();
			
			UserEntity user = UserProvider.getUser();
			ServiceLocator serviceLocator = getServiceLocator();
			BagTerminalService bagTerminalService = serviceLocator.getBagTerminalService();
			CarrierEntity carrier = bagTerminalService.getCarrierForUser(user);
			DriverEntity driver = bagTerminalService.getDriverForUser(user);
			bagTerminalForm.setCarrier(carrier);
			bagTerminalForm.setDriver(driver);
			
			TruckAdminService truckAdminService = serviceLocator.getTruckAdminService();
			boolean isDriverOnline = truckAdminService.isDriverOnline(driver);
			bagTerminalForm.setDriverOnline(isDriverOnline);
			HttpSession session = request.getSession();
			session.setAttribute("driver", driver);
			List<BagTerminalEntity> bagTerminalOrderList = Collections.<BagTerminalEntity> emptyList();
			LoadingpointEntity loadingpoint = getLoadingpoint();
			ShippingpointEntity shippingpoint = loadingpoint.getShippingpoint();
			if (driver != null && driver.getCurrentTruck() != null ) {
				//no orders are there if the truck is unknown
				Boolean bulkPickupCreationFlag = getPickupLoadingpoint().getBulkPickupCreationFlag();
				Long loadingpointId = loadingpoint.getLoadingpointId();
				Long shippingpointId = shippingpoint.getShippingpointId();
				Locale locale = request.getLocale();
				bagTerminalOrderList = bagTerminalService.loadOrders(user, locale,
						shippingpointId, loadingpointId, bulkPickupCreationFlag);
			}

			Locale locale = getLocale(request);

			TimeZone shippingPointTimeZone = getTimeZoneService().loadForShippingPoint(
					shippingpoint, locale);
			List<BagTerminalOrderDTO> terminalOrderDTOList = convertToBagTerminalOrderDTOList(
					bagTerminalOrderList, shippingPointTimeZone);
			bagTerminalForm.setOrderDTOList(terminalOrderDTOList);
			bagTerminalForm.setShippingPointTimeZone(shippingPointTimeZone);
			
			PickupLoadingpointEntity pickupLoadingpoint = getPickupLoadingpoint();
			
			Boolean ordersLoadableTerminalFlag = pickupLoadingpoint.getOrdersLoadableTerminalFlag();
			bagTerminalForm.setOrdersLoadableTerminalFlag(ordersLoadableTerminalFlag);
			
			String forwardName = FORWARD_SUCCESS;
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

	private List<BagTerminalOrderDTO> convertToBagTerminalOrderDTOList(
			List<BagTerminalEntity> bagTerminalOrderList, TimeZone shippingpointTimeZone) {
		List<BagTerminalOrderDTO> terminalOrderDTOList = new ArrayList<BagTerminalOrderDTO>();
		for (BagTerminalEntity bagTerminalOrder : bagTerminalOrderList) {
			BagTerminalOrderDTO terminalOrderDTO = new BagTerminalOrderDTO(bagTerminalOrder,
					shippingpointTimeZone);
			terminalOrderDTOList.add(terminalOrderDTO);
		}
		return terminalOrderDTOList;
	}
}
