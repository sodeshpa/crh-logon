/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.terminal.web.actions.bagterminal;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.BagTerminalService;
import com.crh.logon.business.services.utils.LogonPrintException;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.PickupLoadingpointEntity;
import com.crh.logon.terminal.web.actions.TerminalActionSupport;
import com.crh.logon.terminal.web.forms.bagterminal.BagTerminalForm;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="terminal_BagTerminalForm"
 *  path="/bagterminal/BagTerminal" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="fetchfirst" path="/bagterminal/BagTerminalEntry.do"
 * @struts.action-forward name="selectNewTruck" path="SelectNewTruckTile.def"
 * @struts.action-forward name="counter" path="LogoutCounter.def"
 * 
 */
public class BagTerminalAction extends TerminalActionSupport {

	public static final int maxRowNumber = 100;

	private static final JcanLogger LOG =
		JcanLoggerFactory.getLogger(BagTerminalAction.class);

	public static final String ACTION_PRINT_DELIVERY_NOTE_AND_FINISH = "print";
	public static final String ACTION_CHANGE_TRUCK = "changeTruck";
	public static final String ACTION_SELECT_NEW_TRUCK = "selectNewTruck";
	public static final String ACTION_CANCEL_CHANGE_TRUCK = "cancelChangeTruck";

	private static final String FORWARD_MAIN = "fetchfirst";
	private static final String FORWARD_SELECT_NEW_TRUCK = "selectNewTruck";
	private static final String FORWARD_COUNTER = "counter";
	
	public static final String ACTION_URL = "bagterminal/BagTerminal.do";

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
			
			String inputAction = request.getParameter("inputAction");

			if ( inputAction == null ) {
				return result = mapping.findForward(FORWARD_MAIN);
			}
			
			String forward;
			if (inputAction.equals(ACTION_PRINT_DELIVERY_NOTE_AND_FINISH)) {
				Authorization.authorize(Authorization.BAG_ORDER_TERMINAL_DELIVERY_NOTE);
				forward = loadAndPrintAssignment(request, bagTerminalForm);
			}	
			else if (inputAction.equals(ACTION_SELECT_NEW_TRUCK)) {
				Authorization.authorize(Authorization.BAG_ORDER_TERMINAL_CHANGE_TRUCK);
				List truckList = getServiceLocator().getBagTerminalService().fetchFreeTrucks(bagTerminalForm.getCarrier());
				bagTerminalForm.setFreeTruckList(truckList);
				forward = FORWARD_SELECT_NEW_TRUCK;
			}
			else if (inputAction.equals(ACTION_CANCEL_CHANGE_TRUCK)) {
				Authorization.authorize(Authorization.BAG_ORDER_TERMINAL_CHANGE_TRUCK);
				forward = FORWARD_MAIN;
			}
			else if (inputAction.equals(ACTION_CHANGE_TRUCK)) {
				Authorization.authorize(Authorization.BAG_ORDER_TERMINAL_CHANGE_TRUCK);
				String truckIdStr = bagTerminalForm.getInputNewTruckId();
				Long truckId = null;
				try{
					truckId = Long.valueOf(truckIdStr);
					getServiceLocator().getBagTerminalService().changeTruck(bagTerminalForm.getDriver(), truckId);
				}
				catch (NumberFormatException e) {
					MessageUtils.addActionError(request, "pickup.errors.changetruck");
				}				
				catch (Exception e) {
					LOG.error(getExceptionString(e));
					LOG.error("ERROR WHILE CHANGING TRUCK ");
					MessageUtils.addActionError(request, "pickup.errors.changetruck");
				}
				forward = FORWARD_MAIN;
			}
			else {
				forward = FORWARD_MAIN;
			}
			return result = mapping.findForward(forward);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private String loadAndPrintAssignment(HttpServletRequest request,
			BagTerminalForm bagTerminalForm) {
		
		final Long assignmentId = Long.valueOf(bagTerminalForm.getInputAssignmentId());
		final PickupLoadingpointEntity pickupLoadingpoint = getPickupLoadingpoint();
		if (assignmentId != null) {
			try{
				BagTerminalService bagTerminalService = getServiceLocator().getBagTerminalService();
				bagTerminalService.loadAndPrintAssignment(assignmentId,
						pickupLoadingpoint);
			}
			catch (LogonPrintException logonPrintException) {
				MessageUtils.addActionError(request, "terminal.common.printfailed");
				return FORWARD_MAIN;
			}
			catch (Exception e) {
				LOG.error("ERROR WHILE SETTING THE LOAD FOR ASSIGNMENT " + assignmentId);
				LOG.error(getExceptionString(e));
				MessageUtils.addActionError(request, "pickup.errors.processing");
				return FORWARD_MAIN;
			}
			
			request.setAttribute("loadableTerminal", pickupLoadingpoint.getOrdersLoadableTerminalFlag());
			request.setAttribute("orderValidationCode", getOrderValidationCode(bagTerminalForm));
			request.setAttribute("orderNumber", getOrderNumber(bagTerminalForm));
		}
		
		return FORWARD_COUNTER;
	}

	private String getOrderValidationCode(BagTerminalForm bagTerminalForm) {
		Long inputAssignmentId = Long.parseLong(bagTerminalForm.getInputAssignmentId());
		BagTerminalService bagTerminalService = getServiceLocator().getBagTerminalService();
		List<OrderEntity> ordersByAssignmentId = bagTerminalService.getOrdersByAssignmentId(inputAssignmentId);
		return getOrderValidationCode(ordersByAssignmentId);
	}

	private String getOrderNumber(BagTerminalForm bagTerminalForm) {
		String inputAssignmentIdTxt = bagTerminalForm.getInputAssignmentId();
		Long inputAssignmentId = Long.parseLong(inputAssignmentIdTxt);
		BagTerminalService bagTerminalService = getServiceLocator().getBagTerminalService();
		List<OrderEntity> ordersByAssignmentId = bagTerminalService.getOrdersByAssignmentId(inputAssignmentId);
		return getOrderNumber(ordersByAssignmentId);
	}	
	
}
