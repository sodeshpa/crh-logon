package com.crh.logon.admin.web.actions.test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.forms.test.ManualFaxSmsTriggerForm;
import com.crh.logon.business.services.interfaces.NotificationService;
import com.crh.logon.business.services.interfaces.OrderService;
import com.crh.logon.business.services.interfaces.TestService;
import com.crh.logon.converters.LongConverter;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OutputdocEntity;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_ManualFaxSmsTriggerForm" path="/test/ManualFaxSmsTrigger" scope="session"
 *  validate="true" input="/test/ManualFaxSmsTriggerTile.def"
 */
public class ManualFaxSmsTriggerAction extends AdminActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(ManualFaxSmsTriggerAction.class);
    
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
			Authorization.authorize(Authorization.TEST_FAX_SMS);
			
			final ManualFaxSmsTriggerForm fsForm = (ManualFaxSmsTriggerForm) form;
			final TestService testService = getServiceLocator().getTestService();
			final OrderService orderService = getServiceLocator().getOrderService();
			final NotificationService notificationService = getServiceLocator().getNotificationService();

			if (StringUtils.isEmpty(fsForm.getServiceProvider())) {
				MessageUtils.addActionError(request, "errors.detail", "Please select a service provider.");
			}
			else {			
				OrderEntity order = null;
				if (fsForm.isFax()) {
					OutputdocEntity doc = null;
					if (StringUtils.isNotEmpty(fsForm.getMessage())) {
						if (testService.callSendFaxText(
								fsForm.getServiceProvider(), 
								fsForm.getNumber(), 
								fsForm.getMessage())) {
							MessageUtils.addActionMessage(request, "errors.detail", "Fax sent");
						}
						else {
							MessageUtils.addActionError(request, "errors.detail", "Fax could not be sent");
						}
					}
					if (StringUtils.isNotEmpty(fsForm.getOrderNo())) {
						order = orderService.findOrderByOrderNumber(LongConverter.convertDefault(fsForm.getOrderNo()), true);
					}
					if (StringUtils.isNotEmpty(fsForm.getDocumentId())) {
						doc = testService.getOutputdocEntity(LongConverter.convertDefault(fsForm.getDocumentId()));
					}
					if (doc != null) {
						if (order != null) {
							orderService.sendDeliveryNoteByFax(
									order, 
									doc);
							MessageUtils.addActionMessage(request, "errors.detail", "Fax delivery note sent");
						}
						else {
							if (testService.callSendFaxFile(
									fsForm.getServiceProvider(), 
									fsForm.getNumber(), 
									doc)) {
								MessageUtils.addActionMessage(request, "errors.detail", "Fax document sent");
							}
							else {
								MessageUtils.addActionError(request, "errors.detail", "Fax document could not be sent");
							}
						}
					}
				}
				else {
					if ((StringUtils.isNotEmpty(fsForm.getNumber())) && (StringUtils.isNotEmpty(fsForm.getMessage()))) {
						if (testService.callSendSms(
								fsForm.getServiceProvider(), 
								fsForm.getNumber(), 
								fsForm.getMessage())) {
							MessageUtils.addActionMessage(request, "errors.detail", "SMS sent");
						}			
						else {
							MessageUtils.addActionMessage(request, "errors.detail", "SMS could not be sent");
						}
					}
					
					if (StringUtils.isNotEmpty(fsForm.getOrderNo())) {
						order = orderService.findOrderByOrderNumber(LongConverter.convertDefault(fsForm.getOrderNo()), true);
					}
				}
				
				if (order == null) {
					MessageUtils.addActionError(request, "errors.detail", "Order not found");
				}
				else {
					if (fsForm.isFax()) {
						notificationService.sendOrderConfirmationByFax(order);
						MessageUtils.addActionMessage(request, "errors.detail", "Fax order confirmation sent");
					}
					else {
						notificationService.sendOrderConfirmationBySms(order);
						MessageUtils.addActionMessage(request, "errors.detail", "SMS order confirmation sent");
					}
				}
			}
			return result = mapping.getInputForward();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
}
