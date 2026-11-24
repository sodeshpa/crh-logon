/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.dispatchers.TestServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.AsyncMopEventOutServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.FrozenTablesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.LDSServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.SapOutgoingDataServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TestServiceLocalInterface;
import com.crh.logon.business.services.utils.FileNameSelectionBean;
import com.crh.logon.business.services.utils.LogonTestSimulationException;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.business.stores.domain.AssignmentStoreInterface;
import com.crh.logon.eventbeans.LogonEventOutBean;
import com.crh.logon.persistence.authorization.Authorizer;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.CreditNoteEntity;
import com.crh.logon.persistence.entities.common.GoodsMovementEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.OutputdocEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.frozen.OrderFinalEntity;
import com.crh.logon.persistence.entities.refdata.SignatureTypeTxEntity;
import com.crh.logon.persistence.entities.views.TestSpDropdownEntity;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.webservices.clients.LogonToLDS;
import com.crh.logon.webservices.clients.LogonToSap;
import com.crh.logon.webservices.clients.beans.deliverydocument.SalesDocumentAssembler;
import com.crh.logon.webservices.clients.beans.deliverydocument.SalesDocumentDTO;
import com.crh.logon.webservices.clients.beans.goodsmovement.GoodsMovementAssembler;
import com.crh.logon.webservices.clients.beans.goodsmovement.GoodsMovementDTO;
import com.crh.logon.webservices.clients.beans.logisticreport.LogisticReportAssembler;
import com.crh.logon.webservices.clients.beans.logon_lds_interface.DeleteLoadOrderDTO;
import com.crh.logon.webservices.clients.beans.logon_lds_interface.LoadOrderNotificationDTO;
import com.crh.logon.webservices.clients.beans.logon_lds_interface.LoadOrderNotificationDTO.MessageFunction;
import com.crh.logon.webservices.clients.beans.logon_lds_interface.PdaLoginDTO;
import com.crh.logon.webservices.clients.beans.pricingcall.PricingCallAssembler;
import com.crh.logon.webservices.clients.beans.pricingcall.PricingCallDTO;
import com.crh.logon.webservices.clients.beans.saplogisticreport.SapLogisticReportDTO;
import com.crh.logon.webservices.clients.beans.shipmentdocument.ShipmentDocumentAssembler;
import com.crh.logon.webservices.clients.beans.shipmentdocument.ShipmentDocumentDTO;
import com.crh.logon.webservices.clients.beans.transferdeliverydocument.TransferDeliveryDocumentAssembler;
import com.crh.logon.webservices.clients.beans.transferdeliverydocument.TransferDeliveryDocumentDTO;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class TestServiceBean extends TestServiceDispatcher implements TestServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(TestServiceBean.class);

	private static final Charset ENCODING = StandardCharsets.UTF_8;

	@EJB
	private TestServiceLocalInterface testService;

	@Inject
	private OrderServiceLocalInterface orderService;

	@EJB
	private AsyncMopEventOutServiceLocalInterface asyncMopEventOutService;

	@EJB
	private SapOutgoingDataServiceLocalInterface sapOutgoingDataService;

	@Inject
	private RefDataGenericsServiceLocalInterface referentialDataService;

	@EJB
	private LDSServiceLocalInterface ldsService;

	@EJB
	private AssignmentStoreInterface assignmentStore;

	@EJB
	private FrozenTablesServiceLocalInterface frozenTableService;

	@Inject
	private TestCDIBean testCDIBean;

	/**
	 * @param numbers  - phone number, the sms to send to
	 * @param message - message to send as sms
	 * @return returns true if and only if the call for the webservice is successful.
	 */
	public boolean callSendSms(String serviceProvider, String numbers, String message) {
		// [[
		boolean result = false;
		Object[][] params = { { "serviceProvider", serviceProvider },
				{ "numbers", numbers },
				{ "message", message } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "callSendSms", params);
		}
		// ]]
		try {
			result = FaxAndSmsServiceBean.callSendSms(getServices(), getConfigBean(), serviceProvider, numbers, message);
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

	/**
	 * @param numbers  - phone numbers, the fax to send to
	 * @param document - representation of the attachment as OutputdocEntity
	 * @return returns true if and only if the call for the webservice is successful.
	 */
	public boolean callSendFaxFile(String serviceProvider, String numbers, OutputdocEntity document) {
		// [[
		boolean result = false;
		Object[][] params = { { "serviceProvider", serviceProvider },
				{ "numbers", numbers },
				{ "document", document } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "callSendFaxFile", params);
		}
		// ]]
		try {
			result = FaxAndSmsServiceBean.callSendFaxFile(getServices(), getConfigBean(), serviceProvider, numbers, document);
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

	/**
	 * @param numbers  - phone number, the fax to send to
	 * @param message - message to send as fax
	 * @return returns true if and only if the call for the webservice is successful.
	 */
	public boolean callSendFaxText(String serviceProvider, String numbers, String message) {
		// [[
		boolean result = false;
		Object[][] params = { { "serviceProvider", serviceProvider },
				{ "numbers", numbers },
				{ "message", message } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "callSendFaxText", params);
		}
		// ]]
		try {
			result = FaxAndSmsServiceBean.callSendFaxText(getServices(), getConfigBean(), serviceProvider, numbers, message);
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

	public String[] getFaxImplementations() {
		// [[
		String[] result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getFaxImplementations");
		}
		// ]]
		try {
			result = FaxAndSmsServiceBean.getFaxImplementations();
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

	public String[] getSmsImplementations() {
		// [[
		String[] result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getSmsImplementations");
		}
		// ]]
		try {
			result = FaxAndSmsServiceBean.getSmsImplementations();
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

	/**
	 * Invoked from EjbTestCase
	 */
	public void callMdb(LogonEventOutBean msg) {
		final List<LogonEventOutBean> msgs = new ArrayList<>();
		msgs.add(msg);
		asyncMopEventOutService.callMdb(msgs);
	}

	public OutputdocEntity getOutputdocEntity(Long documentId) {
		// [[
		OutputdocEntity result = null;
		Object[][] params = { { "documentId", documentId } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getOutputdocEntity", params);
		}
		// ]]
		try {
			final ReadOnlyDao<OutputdocEntity> outputdocEntityDao = getDaoProvider().getOutputdocEntityDao();
			final Filter<OutputdocEntity> outputdocEntityFilter = outputdocEntityDao.createFilter();
			outputdocEntityFilter.addExpression(Restrictions.eq(OutputdocEntity.ATTR_OUTPUTDOC_ID, documentId));

			result = outputdocEntityDao.findSingle(outputdocEntityFilter);
			return result;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]orderService
		}
	}

	public OrderItemEntity getOrderItem(Long orderNo, Long orderItemNo) {
		// [[
		OrderItemEntity result = null;
		Object[][] params = { { "orderNo", orderNo },
				{ "orderItemNo", orderItemNo } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getOrderItem", params);
		}
		// ]]
		try {

			final ReadOnlyDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();
			final Filter<OrderEntity> orderFilter = orderDao.createFilter();
			orderFilter.addExpression(Restrictions.eq(OrderEntity.ATTR_ORDER_NUMBER, orderNo));
			OrderEntity order = orderDao.findSingle(orderFilter);

			if (order == null) {
				return result;
			}

			final ReadOnlyDao<OrderItemEntity> orderItemEntitydao = getDaoProvider().getOrderItemEntityDao();
			final Filter<OrderItemEntity> orderItemEntityFilter = orderItemEntitydao.createFilter();
			orderItemEntityFilter.addExpression(Restrictions.eq(OrderItemEntity.ATTR_ORDER_ID, order.getOrderId()));
			orderItemEntityFilter.addExpression(Restrictions.eq(OrderItemEntity.ATTR_ORDER_ITEM_NO, orderItemNo));
			OrderItemEntity orderItem = orderItemEntitydao.findSingle(orderItemEntityFilter);
			if (orderItem != null) {
				orderItem.getOrder().initialize();
			}

			return orderItem;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public CreditNoteEntity getCreditNote(OrderItemEntity orderItem) {

		final ReadOnlyDao<CreditNoteEntity> dao = getDaoProvider().getCreditNoteEntityDao();
		final Filter<CreditNoteEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(CreditNoteEntity.ATTR_ORDER_ITEM, orderItem));

		final CreditNoteEntity creditNote = dao.findSingle(filter);
		if (creditNote != null && creditNote.getOrderItem() != null && creditNote.getOrderItem().getOrder() != null) {
			creditNote.getOrderItem().getOrder().initialize();
		}

		return creditNote;
	}

	/**
	 * This method takes a string as an argument and returns a new String which looks like this: 'received: string' Invoked from
	 * EjbTestCase
	 *
	 * @param in some random String
	 */
	public String hello(String in) {
		// [[
		String result = null;
		Object[][] params = { { "in", in } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "hello", params);
		}
		// ]]
		try {
			// this is how we tell our client that an error happened
			//
			if (in == null) {
				throw new LogonException(Reason.RES_ERROR_UNSPECIFIED);
			}

			String text = testCDIBean.hello(in);
			result = "received: " + text;
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

	public String localizedHello(Locale locale, String key) {
		// [[
		String result = null;
		Object[][] params = { { "locale", locale },
				{ "key", key } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "localizedHello", params);
		}
		// ]]
		try {
			referentialDataService.findTxEntityList(SignatureTypeTxEntity.class, locale);
			referentialDataService.findTxEntity(SignatureTypeTxEntity.class, locale, 0L);
			result = Message.lookup(locale, key);
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


	public LoadingpointEntity getLoadingpoint(Long loadingpointId) {

		return getDaoProvider().getLoadingpointEntityDao().findById(loadingpointId);
	}

	public ShippingpointEntity getShippingpoint(Long shippingpointId) {

		return getDaoProvider().getShippingpointEntityDao().findById(shippingpointId);
	}

	public String sendWebServiceRequest(String xmlRequestData, ShippingpointEntity sp) {

		StringReader stringReader = new StringReader(xmlRequestData);
		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		xmlInputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
		try {
			XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(stringReader);

			if (!xmlStreamReader.hasName()) {
				xmlStreamReader.nextTag();
			}
			while (((xmlStreamReader.getLocalName().equals("Envelope")) ||
					(xmlStreamReader.getLocalName().equals("Body"))) && (xmlStreamReader.hasNext())) {
				xmlStreamReader.nextTag();
			}
			if (xmlStreamReader.hasNext()) {
				String messageType = xmlStreamReader.getLocalName();
				LogonToLDS logonToLDS = new LogonToLDS(getConfigBean());
				switch (messageType) {
					case "SalesDocumentCreation":
						LogonToSap.sendSalesDocumentForTesting(getConfigBean(), xmlStreamReader);
						break;
					case "ShipmentDocumentCreation":
						LogonToSap.sendShipmentDocumentForTesting(getConfigBean(), xmlStreamReader);
						break;
					case "GoodsMovements":
						LogonToSap.sendGoodsMovements(getConfigBean(), xmlStreamReader);
						break;
					case "TransferDeliveryCreation":
						LogonToSap.sendTransferDeliveryDocumentForTesting(getConfigBean(), xmlStreamReader);
						break;
					case "ZFSD_PRICE_SIMULATION":
						return LogonToSap.sendPriceSimulationForTesting(getConfigBean(), xmlStreamReader);
					case "MT_LogisticsReporting_Out":
						LogonToSap.sendLogisticReportForTesting(getConfigBean(), xmlStreamReader);
						break;
					case "loadOrderNotificationRequest":
						return logonToLDS.sendLoadOrderNotificationForTesting(xmlStreamReader,
								sp.getWeighSystemSp().getConfigTableInfix(), sp.getLdsServer());
					case "deleteLoadOrderRequest":
						return logonToLDS.sendDeleteLoadOrderForTesting(xmlStreamReader,
								sp.getWeighSystemSp().getConfigTableInfix(), sp.getLdsServer());
					case "pdaLoginRequest":
						return logonToLDS.sendPdaLoginForTesting(xmlStreamReader, sp.getWeighSystemSp().getConfigTableInfix(),
								sp.getLdsServer());
					default:
						throw new LogonException("unkown message type", Reason.RES_ERROR_UNSPECIFIED);
				}
			}
		}
		catch (Exception e) {

			throw new LogonException(e);
		}

		return null;
	}

	public String getSalesDocumentXml(OrderEntity order, CreditNoteEntity creditNote) {
		try {
			testService._getSalesDocumentDTO(order, creditNote);
			throw new LogonException("Expected SalesDocumentDTO via LogonTestSimulationException or proper error",
					Reason.RES_ERROR_IMPL);
		}
		catch (LogonTestSimulationException exc) {
			SalesDocumentDTO salesDocumentDTO = exc.getData();
			return SalesDocumentAssembler.getXml(salesDocumentDTO);
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void _getSalesDocumentDTO(OrderEntity order, CreditNoteEntity creditNote) {
		ArrayList<CreditNoteEntity> creditNoteList = new ArrayList<>();
		creditNoteList.add(creditNote);
		SalesDocumentDTO dto = sapOutgoingDataService.getSalesDocument(order, creditNoteList);
		throw new LogonTestSimulationException(dto);
	}

	public String getShipmentDocumentXml(OrderEntity order) {
		AssignmentEntity assignment = assignmentStore.findByOrderId(order.getOrderId());
		ShipmentDocumentDTO shipmentDocumentDTO = sapOutgoingDataService.getShipmentDocument(assignment);

		return ShipmentDocumentAssembler.getXml(shipmentDocumentDTO);
	}

	public String getTransferDocumentInboundXml(OrderEntity order, CreditNoteEntity creditNote) {
		TransferDeliveryDocumentDTO transferDocumentDTO =
				sapOutgoingDataService.getTransferDeliveryDocumentInbound(order, creditNote);

		return TransferDeliveryDocumentAssembler.getXml(transferDocumentDTO);
	}

	public String getTransferDocumentOutboundXml(OrderEntity order, CreditNoteEntity creditNote) {

		TransferDeliveryDocumentDTO transferDocumentDTO =
				sapOutgoingDataService.getTransferDeliveryDocumentOutbound(order, creditNote);

		return TransferDeliveryDocumentAssembler.getXml(transferDocumentDTO);
	}

	public String getPricingCallXml(OrderEntity order) {

		List<OrderItemEntity> pricingCallOrderItems = new ArrayList<>();

		SareaEntity sarea = order.getSarea();

		for (OrderItemEntity orderItem : order.getOrderItems()) {
			if (orderService.isPricingCallRequiredForOrderItem(orderItem, sarea)) {
				pricingCallOrderItems.add(orderItem);
			}
		}
		PricingCallDTO dto = sapOutgoingDataService.getPricingCallDTO(order, pricingCallOrderItems);

		return PricingCallAssembler.getXml(dto);

	}

	public String getLogisticReportXml(OrderEntity order) {

		Long orderId = order.getOrderId();

		OrderFinalEntity orderFinal = frozenTableService.loadOrderFinal(orderId);
		if (orderFinal == null) {
			return "Order is not finished yet";
		}
		SapLogisticReportDTO logisticReportDocument =
				sapOutgoingDataService.getLogisticReportDocument(orderFinal.getOrderFinalId());
		return LogisticReportAssembler.getXml(logisticReportDocument);

	}

	public String getGoodsMovementXmlFromGoodsMovementEntity(GoodsMovementEntity goodsMovementEntity) {
		GoodsMovementDTO goodsMovementDTO = this.getGoodsMovementDtoFromGoodsMovementEntity(goodsMovementEntity);
		return GoodsMovementAssembler.getXml(goodsMovementDTO);
	}

	public GoodsMovementDTO getGoodsMovementDtoFromGoodsMovementEntity(GoodsMovementEntity goodsMovementEntity) {
		return sapOutgoingDataService.getGoodsMovementDTOfromGoodsMovementEntity(goodsMovementEntity);
	}

	public void setSapTsToGoodsMovementEntity(Long goodsMovementId) {
		ReadWriteDao<GoodsMovementEntity> goodsMovementEntityDao = getDaoProvider().getGoodsMovementEntityDao();
		Filter<GoodsMovementEntity> filter = goodsMovementEntityDao.createFilter();
		filter.addExpression(Restrictions.eq(GoodsMovementEntity.ATTR_GOODS_MOVEMENT_ID, goodsMovementId));
		Authorizer.disableNextAccessRestriction();
		GoodsMovementEntity goodsMovementEntity = goodsMovementEntityDao.findSingle(filter);
		Date timestamp = Calendar.getInstance().getTime();
		goodsMovementEntity.setSentGoodsmoveToSapTs(timestamp);
		goodsMovementEntityDao.persist(goodsMovementEntity);
	}

	/**
	 * Returns the oldest goodsMovementEntity, which is not sent to SAP yet. This is done with the help of the LoadEntity and
	 * creates a DTO for the GoodsMovement out of it.
	 */
	public List<GoodsMovementEntity> getGoodsMovementEntitiesFromLoadEntity(LoadEntity load) {

		ReadOnlyDao<GoodsMovementEntity> dao = getDaoProvider().getGoodsMovementEntityDao();
		Filter<GoodsMovementEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(GoodsMovementEntity.ATTR_LOAD_ID, load.getLoadId()));
		filter.addExpression(Order.desc(GoodsMovementEntity.ATTR_GOODS_MOVEMENT_ID));
		Authorizer.disableNextAccessRestriction();

		return dao.find(filter);
	}

	private AssignmentEntity getAssignment(OrderEntity order) {

		if (order == null) {
			throw new LogonException("Order number is mandatory", Reason.RES_ORDER_INSUFFICIENT_DATA);
		}

		final ReadOnlyDao<AssignmentPartEntity> assignmentPartEntityDao = getDaoProvider().getAssignmentPartEntityDao();
		final Filter<AssignmentPartEntity> assignmentPartEntityFilter = assignmentPartEntityDao.createFilter();
		assignmentPartEntityFilter.addExpression(
				Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT + "." + AssignmentEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
		assignmentPartEntityFilter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ORDER, order));

		AssignmentPartEntity ap = assignmentPartEntityDao.findSingle(assignmentPartEntityFilter);

		if (ap == null) {
			throw new LogonException("No assignment part found with order " + order.getOrderNumber() + ".",
					Reason.RES_ORDER_INSUFFICIENT_DATA);
		}

		return ap.getAssignment();

	}

	public String getPdaLoginRequestXml(OrderEntity order) {

		final AssignmentEntity assignment = getAssignment(order);

		PdaLoginDTO request = ldsService.createPdaLoginDTO(assignment, order.getShippingpoint(), null);

		ConfigBean configBean = getConfigBean();
		LogonToLDS logonToLDS = new LogonToLDS(configBean);
		return logonToLDS.getXml(request);
	}

	public String getLoadOrderNotificationRequestXml(OrderEntity order) {

		final AssignmentEntity assignment = getAssignment(order);

		MessageFunction msgfn = MessageFunction.UPDATE;
		Long loadOrderId = assignment.getLoadOrderId();
		if (loadOrderId == null) {
			msgfn = MessageFunction.NEW;
			loadOrderId = -1L;
		}

		LoadOrderNotificationDTO request = ldsService.createLoadOrderNotificationDTO(assignment, msgfn, loadOrderId);

		ConfigBean configBean = getConfigBean();
		LogonToLDS logonToLDS = new LogonToLDS(configBean);
		return logonToLDS.getXml(request);
	}

	public String getDeleteLoadOrderRequestXml(OrderEntity order) {

		final AssignmentEntity assignment = getAssignment(order);

		if (assignment.getLoadOrderId() == null) {
			throw new LogonException("Cannot create a deleteLoadOrderRequest: loadOrderId of the assignment is null",
					Reason.RES_ORDER_INSUFFICIENT_DATA);
		}

		DeleteLoadOrderDTO request = ldsService.createDeleteLoadOrderDTO(assignment);

		ConfigBean configBean = getConfigBean();
		LogonToLDS logonToLDS = new LogonToLDS(configBean);
		return logonToLDS.getXml(request);
	}

	public List<TestSpDropdownEntity> loadShippingpoints(Locale locale) {

		final ReadOnlyDao<TestSpDropdownEntity> shippingpointDao = getDaoProvider().getTestSpDropdownEntityDao();
		final Filter<TestSpDropdownEntity> shippingpointFilter = shippingpointDao.createFilter();
		shippingpointFilter.addExpression(Restrictions.eq(TestSpDropdownEntity.ATTR_LANG_CD, locale.getLanguage()));
		shippingpointFilter.addExpression(Order.asc(TestSpDropdownEntity.ATTR_SHIPPINGPOINT_NAME));

		return shippingpointDao.find(shippingpointFilter);
	}

	/**
	 * Returns a File object representing the given pathname relative to the 'wsdumps' folder. To prevent access outside this
	 * wsdumps folder, the canonical path is checked to make sure it's within 'wsdumps'.
	 */
	private File getFile(String pathName) {

		String wsDumpDirName = getConfigBean().getWsclientSoapDumpDir();
		File path = new File(wsDumpDirName + File.separator + pathName);
		File wsDumpDir = new File(wsDumpDirName);
		try {
			if (!path.getCanonicalPath().startsWith(wsDumpDir.getCanonicalPath())) {

				throw new LogonException(Reason.RES_ERROR_SECURITY_TAMPERING);
			}
		}
		catch (IOException e) {

			throw new LogonException(e, Reason.RES_ERROR_SECURITY_TAMPERING);
		}

		return path;
	}

	public List<FileNameSelectionBean> getFileNameSelectionList(String pathName) {

		final File path = getFile(pathName);
		if (!path.isDirectory()) {
			return null;
		}
		final List<FileNameSelectionBean> fileNameSelectionList = new ArrayList<>();
		final File[] files = path.listFiles(
				pathname -> (pathname.isDirectory() && (!pathname.getName().startsWith("incoming-"))) ||
						pathname.getName().endsWith(".request"));
		if (files != null) {
			for (File file : files) {
				fileNameSelectionList.add(new FileNameSelectionBean(file.getName(), file.isDirectory()));
			}
			fileNameSelectionList.sort(Comparator.comparing(FileNameSelectionBean::getName));
		}

		return fileNameSelectionList;
	}

	public String loadFile(String fileName) {

		File file = getFile(fileName);
		if (file.isDirectory()) {

			return null;
		}
		try (FileInputStream fis = new FileInputStream(file);
				InputStreamReader in = new InputStreamReader(new BufferedInputStream(fis), ENCODING)) {
			StringBuilder sb = new StringBuilder();
			int c = in.read();
			while (c != -1) {
				sb.append((char) c);
				c = in.read();
			}

			return sb.toString();
		}
		catch (IOException e) {

			throw new LogonException(e);
		}
	}


	public void deactivateActiveAssignments(List<Long> truckIds, List<Long> statusCds) {
		// [[
		Object[][] params = { { "truckIds", truckIds },
				{ "statusCds", statusCds } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "deactivateActiveAssignments", params);
		}
		// ]]
		try {
			final ReadWriteDao<AssignmentEntity> dao = getDaoProvider().getAssignmentEntityDao();
			final Filter<AssignmentEntity> f = dao.createFilter();

			f.addExpression(Restrictions.in(AssignmentEntity.ATTR_TRUCK_ID, truckIds));
			f.addExpression(Restrictions.in(AssignmentEntity.ATTR_ASSIGN_STATUS_CD, statusCds));
			f.addExpression(Restrictions.eq(AssignmentEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			f.addExpression(Order.asc(AssignmentEntity.ATTR_TRUCK_ID));

			final List<AssignmentEntity> assignments = dao.find(f);

			TruckEntity currentTruck = null;
			LOG.debug("START deactivating active assignments ...");
			for (AssignmentEntity assignment : assignments) {
				if (currentTruck == null ||
						!currentTruck.getTruckId().equals(assignment.getTruck().getTruckId())) {
					currentTruck = assignment.getTruck();
					LOG.debug("  Assignments on truck \"" + currentTruck.getTruckName() + "\"");
				}
				LOG.debug("    Assignment will be deactivated: " + assignment.getAssignmentId());
				assignment.setActiveFlag(Boolean.FALSE);
			}
			LOG.debug("END deactivating active assignments");
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}
}
