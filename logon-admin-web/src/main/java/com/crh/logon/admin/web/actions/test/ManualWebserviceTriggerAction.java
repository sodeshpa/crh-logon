package com.crh.logon.admin.web.actions.test;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.List;
import javax.ejb.EJBException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.forms.test.ManualWebserviceTriggerForm;
import com.crh.logon.admin.web.forms.test.ManualWebserviceTriggerForm.EmailBean;
import com.crh.logon.admin.web.forms.test.ManualWebserviceTriggerForm.PrinterBean;
import com.crh.logon.admin.web.forms.test.ManualWebserviceTriggerForm.WebServiceOptions;
import com.crh.logon.business.services.interfaces.EmailService;
import com.crh.logon.business.services.interfaces.FaxAndSmsService;
import com.crh.logon.business.services.interfaces.NotificationService;
import com.crh.logon.business.services.interfaces.OrderService;
import com.crh.logon.business.services.interfaces.TestService;
import com.crh.logon.constants.Constant;
import com.crh.logon.converters.LongConverter;
import com.crh.logon.converters.URIConverter;
import com.crh.logon.persistence.entities.common.CreditNoteEntity;
import com.crh.logon.persistence.entities.common.GoodsMovementEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.OutputdocEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author AdNovum Informatik AG
 * @version $Revision$ $Date$
 * @struts.action name="admin_ManualWebserviceTriggerForm" path="/test/ManualWebserviceTrigger" scope="session" validate="true"
 * input="/test/ManualWebserviceTriggerTile.def"
 * @struts.action-forward name="optionsSet" path="/test/OptionsTile.def"
 * @struts.action-forward name="main" path="/test/ManualWebserviceTriggerTile.def"
 */
public class ManualWebserviceTriggerAction extends AdminActionSupport {

	private static final JcanLogger LOG =
			JcanLoggerFactory.getLogger(ManualWebserviceTriggerAction.class);

	private static final String MSG_KEY_ERRORS_DETAIL = "errors.detail";

	@Override
	public ActionForward execute(
			ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		// [[
		ActionForward result = null;
		Object[][] params = { { "mapping", mapping },
				{ "form", form },
				{ "request", request },
				{ "response", response } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "execute", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.TEST_WEBSERVICE);
			if (!getConfigBean().getWsclientEnableManualWebserviceTrigger()) {
				throw new LogonException("The web service test screen is disabled by the administrator",
						Reason.RES_ERROR_CONFIG_NOT_VALID);
			}
			final ManualWebserviceTriggerForm wsForm = (ManualWebserviceTriggerForm) form;
			String forwardName = null;

			if (request.getParameter("getOptions") != null) {
				handleGetOptions(wsForm.getWebServiceOptions(), request);
				forwardName = "optionsSet";
			}
			else if (request.getParameter("loadFile") != null) {
				handleLoadFile(wsForm.getWebServiceOptions(), request);
				forwardName = "optionsSet";
			}
			else if (request.getParameter("callWebService") != null) {
				handleCallWebService(wsForm.getWebServiceOptions(), request);
				MessageUtils.addActionMessage(request, MSG_KEY_ERRORS_DETAIL, "Message sent. No Timestamps set.");
				forwardName = "main";
			}
			else if (request.getParameter("callEmailInfo") != null) {
				handleCallEmailInfo(wsForm.getEmail(), request);
			}
			else if (request.getParameter("sendToPrinter") != null) {
				handleSendToPrinter(wsForm.getPrinter(), request);
			}
			else {
				throw new LogonException("No action specified", Reason.RES_ERROR_IMPL);
			}

			result = forwardName != null ? mapping.findForward(forwardName) : mapping.getInputForward();
			return result;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private String prettyPrintXml(String xml) {

		String formattedXml = xml;
		try {
			StreamSource src = new StreamSource(new StringReader(xml));
			TransformerFactory transFactory = TransformerFactory.newInstance();
			transFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			Transformer transformer = transFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			StreamResult result = new StreamResult(new StringWriter());
			transformer.transform(src, result);
			formattedXml = result.getWriter().toString();
		}
		catch (Exception e) {
			LOG.debug("Error formatting xml", e);
		}

		return formattedXml;
	}

	private boolean handleGetOptions(WebServiceOptions webServiceOptions, HttpServletRequest request) {

		final TestService testService = getServiceLocator().getTestService();
		final String messageType = webServiceOptions.getSelectedMessageType();
		final Long orderNo = LongConverter.convertDefault(webServiceOptions.getOrderNo());
		final Long orderItemNo = LongConverter.convertDefault(webServiceOptions.getOrderItemNo());

		OrderItemEntity orderItem = null;
		OrderEntity order = null;
		CreditNoteEntity creditNote = null;

		if (orderItemNo != null) {
			orderItem = testService.getOrderItem(orderNo, orderItemNo);
			if (orderItem == null) {
				MessageUtils.addActionError(request, MSG_KEY_ERRORS_DETAIL, "Order item not found");

				return false;
			}
			order = orderItem.getOrder();
			if (webServiceOptions.getSendCreditNote() != null && webServiceOptions.getSendCreditNote()) {
				creditNote = testService.getCreditNote(orderItem);
				if (creditNote == null) {
					MessageUtils.addActionError(request, MSG_KEY_ERRORS_DETAIL, "Credit note not found");

					return false;
				}
			}
		}
		else if (orderNo != null) {
			order = getServiceLocator().getOrderService().findOrderByOrderNumber(orderNo, true);
			if (order == null) {
				MessageUtils.addActionError(request, MSG_KEY_ERRORS_DETAIL, "Order not found");

				return false;
			}
		}

		String xml = null;
		try {
			if (ManualWebserviceTriggerForm.WS_SAP_SALESDOC.equals(messageType)) {
				xml = testService.getSalesDocumentXml(order, creditNote);
			}
			else if (ManualWebserviceTriggerForm.WS_SAP_SHIPMENTDOC.equals(messageType)) {
				xml = testService.getShipmentDocumentXml(order);
			}
			else if (ManualWebserviceTriggerForm.WS_SAP_TRANSFERDOC_INBOUND.equals(messageType)) {
				xml = testService.getTransferDocumentInboundXml(order, creditNote);
			}
			else if (ManualWebserviceTriggerForm.WS_SAP_TRANSFERDOC_OUTBOUND.equals(messageType)) {
				xml = testService.getTransferDocumentOutboundXml(order, creditNote);
			}
			else if (ManualWebserviceTriggerForm.WS_SAP_PRICING_CALL.equals(messageType)) {
				xml = testService.getPricingCallXml(order);
			}
			else if (ManualWebserviceTriggerForm.WS_SAP_LOGISTIC_REPORT.equals(messageType)) {
				xml = testService.getLogisticReportXml(order);
			}
			else if (ManualWebserviceTriggerForm.WS_LDS_PDA_LOGIN.equals(messageType)) {
				xml = testService.getPdaLoginRequestXml(order);
			}
			else if (ManualWebserviceTriggerForm.WS_LDS_LOAD_ORDER_NOTIFICATION.equals(messageType)) {
				xml = testService.getLoadOrderNotificationRequestXml(order);
			}
			else if (ManualWebserviceTriggerForm.WS_LDS_DELETE_LOAD_ORDER.equals(messageType)) {
				xml = testService.getDeleteLoadOrderRequestXml(order);
			}
		}
		catch (EJBException e) {
			Exception causedBy = e.getCausedByException();
			if (causedBy instanceof LogonException) {
				MessageUtils.addActionError(request, MSG_KEY_ERRORS_DETAIL, causedBy.getMessage());
				return false;
			}
			else {
				throw e;
			}
		}

		// 06-Sept-2012/stich: moved special case down here, since it does not fit into the exception handling above
		if (ManualWebserviceTriggerForm.WS_SAP_GOODSMOVEMENT.equals(messageType)) {
			if ((orderItem == null) || (orderItem.getLoad() == null)) {
				MessageUtils.addActionWarning(request,
						Reason.RES_ERROR_GOODSMOVMENT_NO_ORDERITEM_NOR_LOAD_FOUND.toString(),
						"Order item not found or does not have a load order id");
				return false;
			}
			LoadEntity load = orderItem.getLoad();
			List<GoodsMovementEntity> goodsMovementEntities = testService.getGoodsMovementEntitiesFromLoadEntity(load);
			GoodsMovementEntity goodsMovementEntity = null;

			// Only the first/oldest element matters
			if (!goodsMovementEntities.isEmpty()) {
				goodsMovementEntity = goodsMovementEntities.get(0);
				xml = testService.getGoodsMovementXmlFromGoodsMovementEntity(goodsMovementEntity);
				Long goodsMovementId = goodsMovementEntity.getGoodsMovementId();
				webServiceOptions.setGoodsMovementId(goodsMovementId);

				if (goodsMovementEntities.size() > 1) {
					// if there is more then one, we use only the oldest one not sent yet and print a msg
					MessageUtils.addActionWarning(request, MSG_KEY_ERRORS_DETAIL, "Found " + goodsMovementEntities.size()
							+ " GoodsMovements. Processing oldest only.");
				}
			}
			else {
				MessageUtils.addActionWarning(request, MSG_KEY_ERRORS_DETAIL,
						"There is no GoodsMovement entry for the given OrderNumber and OrderItemNumber.");
				xml = " ";
			}
		}

		if (xml != null) {
			xml = prettyPrintXml(xml);
		}
		webServiceOptions.setRequest(xml);
		webServiceOptions.setResponse(null);

		return true;
	}

	private boolean handleLoadFile(WebServiceOptions webServiceOptions, HttpServletRequest request) {
		final String fileName = webServiceOptions.getFileName();
		if (fileName == null || StringUtils.isEmpty(fileName)) {
			MessageUtils.addActionWarning(request, "manualwebservicetrigger.error.filename.notselected");
			return false;
		}

		final String filePath = StringUtils.join(webServiceOptions.getPathElements().toArray(), File.separator) + File.separator
				+ webServiceOptions.getFileName();
		String xml = getServiceLocator().getTestService().loadFile(filePath);
		if (xml != null) {
			xml = prettyPrintXml(xml);
		}
		webServiceOptions.setRequest(xml);
		webServiceOptions.setResponse(null);

		return true;
	}

	private boolean handleCallWebService(WebServiceOptions webServiceOptions, HttpServletRequest request) {

		final TestService testService = getServiceLocator().getTestService();
		final Long orderNo = LongConverter.convertDefault(webServiceOptions.getOrderNo());
		Long shippingpointId = null;

		OrderEntity order;
		ShippingpointEntity sp = null;

		if (!webServiceOptions.getShippingpointId().equals(Constant.INVALID_OBJECT_ID_STR)) {
			shippingpointId = LongConverter.convertDefault(webServiceOptions.getShippingpointId());
		}

		if (orderNo != null) {
			order = getServiceLocator().getOrderService().findOrderByOrderNumber(orderNo, true);
			if (order == null) {
				MessageUtils.addActionError(request, MSG_KEY_ERRORS_DETAIL, "Order not found");

				return false;
			}
			sp = order.getShippingpoint();
		}

		if (shippingpointId != null) {
			sp = testService.getShippingpoint(shippingpointId);
		}

		try {
			String wsResponse = testService.sendWebServiceRequest(webServiceOptions.getRequest(), sp);
			if (wsResponse != null) {
				wsResponse = prettyPrintXml(wsResponse);
			}
			webServiceOptions.setResponse(wsResponse);
		}
		catch (Exception e) {
			MessageUtils.addActionError(request, MSG_KEY_ERRORS_DETAIL, "Error calling web service : " + e);
		}

		return true;
	}

	private boolean handleCallEmailInfo(EmailBean emailBean, HttpServletRequest request) {
		final TestService testService = getServiceLocator().getTestService();
		final OrderService orderService = getServiceLocator().getOrderService();
		final EmailService emailService = getServiceLocator().getEmailService();
		final NotificationService notificationService = getServiceLocator().getNotificationService();

		OrderEntity order = null;
		OutputdocEntity doc = null;
		if (!StringUtils.isEmpty(emailBean.getDocumentId())) {
			Long docId = LongConverter.convertDefault(emailBean.getDocumentId());
			doc = testService.getOutputdocEntity(docId);
		}
		if (!StringUtils.isEmpty(emailBean.getOrderNo())) {
			Long orderNo = LongConverter.convertDefault(emailBean.getOrderNo());
			order = orderService.findOrderByOrderNumber(orderNo, true);
		}
		if (doc != null) {
			if (order != null) {
				orderService.sendDeliveryNoteByEmail(order, doc);
				MessageUtils.addActionMessage(request, MSG_KEY_ERRORS_DETAIL, "Email delivery note sent");
			}
			else {

				final String[] to = new String[] { emailBean.getTo() };
				emailService.send(to, null, null, "test message", emailBean.getText(), null,
						emailService.createMimeBodyPartAttachment(
								doc.getData(), doc.getDataMimeType(), doc.getOutputdocName()));
				MessageUtils.addActionMessage(request, MSG_KEY_ERRORS_DETAIL, "Email document sent");
			}
		}
		if (order != null) {
			notificationService.sendOrderConfirmationByEmail(order);
			MessageUtils.addActionMessage(request, MSG_KEY_ERRORS_DETAIL, "Email order confirmation sent");
		}
		if (!StringUtils.isEmpty(emailBean.getText())) {

			final String[] to = new String[] { emailBean.getTo() };
			emailService.send(to, null, null, "test message", emailBean.getText(), null);
			MessageUtils.addActionMessage(request, MSG_KEY_ERRORS_DETAIL, "Email message sent");
		}

		return true;
	}

	private boolean handleSendToPrinter(PrinterBean printerBean, HttpServletRequest request) {
		final OrderService orderService = getServiceLocator().getOrderService();
		final FaxAndSmsService faxAndSmsService = getServiceLocator().getFaxAndSmsService();
		final TestService testService = getServiceLocator().getTestService();

		OutputdocEntity doc = null;
		OrderEntity order = null;
		if (!StringUtils.isEmpty(printerBean.getDocumentId())) {
			Long docId = LongConverter.convertDefault(printerBean.getDocumentId());
			doc = testService.getOutputdocEntity(docId);
		}
		if (!StringUtils.isEmpty(printerBean.getOrderNo())) {
			Long orderNo = LongConverter.convertDefault(printerBean.getOrderNo());
			order = orderService.findOrderByOrderNumber(orderNo, true);
		}

		if (doc != null) {
			try {
				if (order != null) {
					orderService.sendDeliveryNoteToPrinter(order, doc, null);
				}
				else {
					URI printerURI = null;
					if (!StringUtils.isEmpty(printerBean.getPrinterUri())) {
						printerURI = URIConverter.convertDefault(printerBean.getPrinterUri());
					}
					faxAndSmsService.sendToPrinter(printerURI, printerBean.getPrinterName(), doc, 1);
					MessageUtils.addActionMessage(request, MSG_KEY_ERRORS_DETAIL, "Document sent to printer");
				}
			}
			catch (Exception exception) {
				String message = ExceptionUtils.getMessage(exception);
				String fullMessage = "Document printing failed: " + message;
				MessageUtils.addActionError(request, MSG_KEY_ERRORS_DETAIL, fullMessage);
			}
		}

		return true;
	}
}
