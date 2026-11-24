/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.test;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.GoodsMovementEntity;
import com.crh.logon.web.forms.AbstractActionForm;
import com.crh.logon.web.validators.TypeValidation;

/**
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_ManualWebserviceTriggerForm"
 */
public class ManualWebserviceTriggerForm extends AbstractActionForm {

	private static final long serialVersionUID = -8910104051337644556L;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ManualWebserviceTriggerForm.class);

	public static final String WS_SAP_SALESDOC = "SAP:SalesDocumentCreation";
	public static final String WS_SAP_SHIPMENTDOC = "SAP:ShipmentDocumentCreation";
	public static final String WS_SAP_TRANSFERDOC_INBOUND = "SAP:TransferDeliveryCreation:Inbound";
	public static final String WS_SAP_TRANSFERDOC_OUTBOUND = "SAP:TransferDeliveryCreation:Outbound";
	public static final String WS_SAP_GOODSMOVEMENT = "SAP:GoodsMovement";
	public static final String WS_SAP_PRICING_CALL = "SAP:PricingCall";
	public static final String WS_SAP_LOGISTIC_REPORT = "SAP:LogisticReport";
	public static final String WS_LDS_PDA_LOGIN = "pdaLogin";
	public static final String WS_LDS_LOAD_ORDER_NOTIFICATION = "loadOrderNotification";
	public static final String WS_LDS_DELETE_LOAD_ORDER = "deleteLoadOrder";
	
	public class WebServiceOptions implements Serializable {

		private static final long serialVersionUID = 1L;
		private String orderNo;
		private String orderItemNo;
		private Boolean sendCreditNote = Boolean.FALSE;
		private String selectedMessageType = WS_SAP_SALESDOC;
		private List pathElements;
		private String fileName;
		private String request;
		private String response;
		private List messageTypes;
		private Long goodsMovementId;
		private List shippingpointList;
		private String shippingpointId = Constant.INVALID_OBJECT_ID_STR;

		public List getMessageTypes() {

			if (messageTypes == null) {
				messageTypes = new ArrayList();
				messageTypes.add(WS_SAP_SALESDOC);
				messageTypes.add(WS_SAP_SHIPMENTDOC);
				messageTypes.add(WS_SAP_TRANSFERDOC_INBOUND);
				messageTypes.add(WS_SAP_TRANSFERDOC_OUTBOUND);
				messageTypes.add(WS_SAP_GOODSMOVEMENT);
				messageTypes.add(WS_SAP_PRICING_CALL);
				messageTypes.add(WS_SAP_LOGISTIC_REPORT);
				messageTypes.add(WS_LDS_PDA_LOGIN);
				messageTypes.add(WS_LDS_LOAD_ORDER_NOTIFICATION);
				messageTypes.add(WS_LDS_DELETE_LOAD_ORDER);
			}

			return messageTypes;
		}
		
		public List getShippingpointList() {
			return shippingpointList;
		}

		public void setShippingpointList(List shippingpointList) {
			this.shippingpointList = shippingpointList;
		}

		public String getOrderNo() {
			return orderNo;
		}

		public void setOrderNo(String orderNo) {
			this.orderNo = orderNo;
		}

		public String getOrderItemNo() {
			return orderItemNo;
		}

		public void setOrderItemNo(String orderItemNo) {
			this.orderItemNo = orderItemNo;
		}

		public Boolean getSendCreditNote() {
			return sendCreditNote;
		}

		public void setSendCreditNote(Boolean sendCreditNote) {
			this.sendCreditNote = sendCreditNote;
		}

		public String getSelectedMessageType() {
			return selectedMessageType;
		}

		public void setSelectedMessageType(String selectedMessageType) {
			this.selectedMessageType = selectedMessageType;
		}

		public List getPathElements() {
			if (pathElements == null) {
				pathElements = new ArrayList();
			}
			return pathElements;
		}

		public void setPathElements(List pathElements) {
			this.pathElements = pathElements;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getRequest() {
			return request;
		}

		public void setRequest(String request) {
			this.request = request;
		}

		public String getResponse() {
			return response;
		}

		public void setResponse(String response) {
			this.response = response;
		}

		public void setGoodsMovementId(Long goodsMovementId) {
			this.goodsMovementId = goodsMovementId;
		}
		public Long getGoodsMovementId() {
			return this.goodsMovementId;
		}

		public String getShippingpointId() {
			return shippingpointId;
		}

		public void setShippingpointId(String shippingpointId) {
			this.shippingpointId = shippingpointId;
		}

	}

	private WebServiceOptions webServiceOptions;

	public class EmailBean implements Serializable {

		private static final long serialVersionUID = 1L;
		private String to;
		private String text;
		private String orderNo;
		private String documentId;

		public String getTo() {
			return to;
		}

		public void setTo(String to) {
			this.to = to;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		@TypeValidation(Long.class)
		public String getOrderNo() {
			return orderNo;
		}

		public void setOrderNo(String orderNo) {
			this.orderNo = orderNo;
		}

		@TypeValidation(Long.class)
		public String getDocumentId() {
			return documentId;
		}

		public void setDocumentId(String documentId) {
			this.documentId = documentId;
		}
	}

	private EmailBean email;

	public class PrinterBean implements Serializable {

		private static final long serialVersionUID = 1L;
		private String printerUri;
		private String printerName;
		private String documentId;
		private String orderNo;

		@TypeValidation(URI.class)
		public String getPrinterUri() {
			return printerUri;
		}

		public void setPrinterUri(String printerUri) {
			this.printerUri = printerUri;
		}

		public String getPrinterName() {
			return printerName;
		}

		public void setPrinterName(String printerName) {
			this.printerName = printerName;
		}

		@TypeValidation(Long.class)
		public String getOrderNo() {
			return orderNo;
		}

		public void setOrderNo(String orderNo) {
			this.orderNo = orderNo;
		}

		@TypeValidation(Long.class)
		public String getDocumentId() {
			return documentId;
		}

		public void setDocumentId(String documentId) {
			this.documentId = documentId;
		}
	}

	private PrinterBean printer;

	private GoodsMovementEntity goodsMovementEntity = null;

	/**
	 * Default constructor.
	 */
	public ManualWebserviceTriggerForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "ManualWebserviceTriggerForm");
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
	 * fields on the web form.
	 */
	private void init() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
			clear();
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
	 * Clears all the input fields on the web form, discards any existing
	 * search results, and resets any existing pages. Called in response to
	 * clicking the reset button on the web form.
	 */
	public void clear() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "clear");
		}
		// ]]
		try {
			webServiceOptions = new WebServiceOptions();
			email = new EmailBean();
			printer = new PrinterBean();
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
		if (webServiceOptions != null) {
			webServiceOptions.setSendCreditNote(Boolean.FALSE);
		}
	}

	public WebServiceOptions getWebServiceOptions() {
		return webServiceOptions;
	}

	public void setWebServiceOptions(WebServiceOptions webServiceOptions) {
		this.webServiceOptions = webServiceOptions;
	}

	/**
	 * Called from an action, to validate a form within a popup or part
	 * @param mapping
	 * @param request
	 * @return
	 */
	public ActionErrors performValidation(ActionMapping mapping, HttpServletRequest request) {

		ActionErrors errors = null;

		return errors;
	}

	/**
	 * Called on a form submit
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		// [[
		ActionErrors result = null;
		Object[][] params = { { "mapping", mapping }, { "request", request } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validate", params);
		}
		// ]]
		try {
			ActionErrors errors = new ActionErrors();

			if (request.getParameter("callEmailInfo") != null) {
				errors = validateBean(mapping, request, email);
			}
			else if (request.getParameter("sendToPrinter") != null) {
				errors = validateBean(mapping, request, printer);
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

	public EmailBean getEmail() {
		return email;
	}

	public void setEmail(EmailBean email) {
		this.email = email;
	}

	public PrinterBean getPrinter() {
		return printer;
	}

	public void setPrinter(PrinterBean printer) {
		this.printer = printer;
	}

	public void setGoodsMovementEntity(GoodsMovementEntity goodsMovementEntity) {
		this.goodsMovementEntity = goodsMovementEntity;
	}
	
	public GoodsMovementEntity getGoodsMovementEntity() {
		return this.goodsMovementEntity;
	}
}
