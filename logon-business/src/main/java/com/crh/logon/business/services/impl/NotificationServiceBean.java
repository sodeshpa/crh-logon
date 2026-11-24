/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Future;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import com.crh.logon.business.services.beans.OrderAndAssignmentDTO;
import com.crh.logon.business.services.dispatchers.NotificationServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.interfaces.NotificationService;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Expression;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.impl.cron.ScheduledTask;
import com.crh.logon.business.services.localinterfaces.ConfigServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.EmailServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.FaxAndSmsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.LoadingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.NotificationServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderDetailServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderShippingNotificationServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.PDFGeneratorServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TruckAdminServiceLocalInterface;
import com.crh.logon.business.services.utils.LangUtil;
import com.crh.logon.business.services.utils.MailPropertiesConfiguration;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.OrderShippNotificationEntity;
import com.crh.logon.persistence.entities.views.SapMaterialDescEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
@AdnTrace
public class NotificationServiceBean extends NotificationServiceDispatcher implements
		NotificationServiceLocalInterface, Serializable {

	private static enum NotificationType {
		ORDER_CONFIRMATION,
		LOAD_CONFIRMATION
	}
	
	private static enum MessageType {
		EMAIL,
		FAX,
		SMS
	}

	private static final String LOAD_CONFIRM_FAX_MESSAGE = "load.confirm.fax.message";
	private static final String ORDER_CONFIRM_FAX_MESSAGE = "order.confirm.fax.message";
	private static final String ORDER_CONFIRM_FAX_ORDER_ITEM = "order.confirm.fax.order_item";	
	private static final String ORDER_CONFIRM_SMS_MESSAGE_2 = "order.confirm.sms.message.2";
	private static final String ORDER_CONFIRM_SMS_MESSAGE_1 = "order.confirm.sms.message.1";
	private static final String ORDER_CONFIRM_SMS_MESSAGE = "order.confirm.sms.message";	
	private static final String ORDER_CONFIRM_SMS_ORDER_ITEM = "order.confirm.sms.order_item";
	private static final String ORDER_CONFIRM_EMAIL_SUBJECT_KEY = "order.confirm.email.subject";
	private static final String ORDER_CONFIRM_EMAIL_ORDER_ITEM_KEY = "order.confirm.email.order_item";
	private static final String ORDER_CONFIRM_EMAIL_MESSAGE_KEY = "order.confirm.email.message";
	private static final String LOAD_CONFIRM_EMAIL_SUBJECT_KEY = "load.confirm.email.subject";
	private static final String LOAD_CONFIRM_EMAIL_ORDER_ITEM_KEY = "load.confirm.email.order_item";
	private static final String LOAD_CONFIRM_EMAIL_MESSAGE_KEY = "load.confirm.email.message";
	private static final String LOAD_CONFIRM_SMS_MESSAGE_2 = "load.confirm.sms.message.2";
	private static final String LOAD_CONFIRM_SMS_MESSAGE_1 = "load.confirm.sms.message.1";
	private static final String LOAD_CONFIRM_SMS_MESSAGE = "load.confirm.sms.message";
	
	private static final String DATE_WITHOUT_TIME_FORMAT = "yyyy-MM-dd";
	private static final long SIX_MIN_TIMEOUT_IN_MILIS = 360000L;
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(NotificationServiceBean.class);

	@EJB
	EmailServiceLocalInterface emailService;

	@EJB
	FaxAndSmsServiceLocalInterface faxAndSmsService;

	@EJB
	PDFGeneratorServiceLocalInterface pdfGeneratorService;

	@EJB
	RefDataGenericsServiceLocalInterface referentialDataService;

	@EJB
	LoadingServiceLocalInterface loadingService;
	
	@EJB private OrderShippingNotificationServiceLocalInterface orderShippingNotificationSender;
	
	@EJB
	NotificationService notificationService;
	
	@EJB
	ConfigServiceLocalInterface configService;
	
	@EJB
	DispoServiceLocalInterface dispoService;
	
	@EJB
	TruckAdminServiceLocalInterface truckAdminService;
	
	@EJB
	OrderDetailServiceLocalInterface orderDetailServiceLocalInterface;
	
	public void sendOrderConfirmation(OrderEntity order) {
		if (order.getOrderConfFaxFlag().booleanValue()) {
			try {
				sendOrderConfirmationByFax(order);
			}
			catch (Exception ex) {
				LOG.error("Fax sending failed for order id " + order.getOrderId(), ex);
			}
		}
		if (order.getOrderConfSmsFlag().booleanValue()) {
			try {
				sendOrderConfirmationBySms(order);
			}
			catch (Exception ex) {
				LOG.error("SMS sending failed for order id " + order.getOrderId(), ex);
			}
		}
		if (order.getOrderConfEmailFlag().booleanValue()) {
			try {
				sendOrderConfirmationByEmail(order);
			}
			catch (Exception ex) {
				LOG.error(
						"Sending order confirmation by email failed for order "
								+ order.getOrderId(), ex);
			}
		}
	}

	/**
	 * Sends an order confirmation by fax.
	 */
	public void sendOrderConfirmationByFax(OrderEntity order) {
		if (StringUtils.isEmpty(order.getOrderConfFaxNumber())) {
			LOG.error("No fax number specified for order " + order.getOrderId());
			return;
		}
		String message = getOrderConfirmationTextMessageBody(order,
				ORDER_CONFIRM_FAX_MESSAGE, ORDER_CONFIRM_FAX_ORDER_ITEM);
		if (!faxAndSmsService.callSendFaxText(order.getOrderConfFaxNumber(), message)) {
			throw new LogonException("Sending to fax failed", Reason.RES_ERROR_UNSPECIFIED);
		}
	}

	/**
	 * Sends an order confirmation by sms.
	 */
	public void sendOrderConfirmationBySms(OrderEntity order) {
		if (StringUtils.isEmpty(order.getOrderConfSmsNumber())) {
			LOG.error("No sms number specified for order " + order.getOrderId());

			return;
		}

		List<String> messages = new ArrayList<String>();
		String fullMessage = getOrderConfirmationTextMessageBody(order,
				ORDER_CONFIRM_SMS_MESSAGE, ORDER_CONFIRM_SMS_ORDER_ITEM);
		if ((fullMessage != null) && (fullMessage.length() > Constant.SMS_MAX_LENGTH_IN_CHARS)) {
			messages.add(getOrderConfirmationTextMessageBody(order,
					ORDER_CONFIRM_SMS_MESSAGE_1, ORDER_CONFIRM_SMS_ORDER_ITEM));
			messages.add(getOrderConfirmationTextMessageBody(order,
					ORDER_CONFIRM_SMS_MESSAGE_2, ORDER_CONFIRM_SMS_ORDER_ITEM));
		}
		else {
			messages.add(fullMessage);
		}

		for (Iterator<String> it = messages.iterator(); it.hasNext();) {
			String message = it.next();
			if (!faxAndSmsService.callSendSms(order.getOrderConfSmsNumber(), message)) {

				throw new LogonException("Sending sms failed", Reason.RES_ERROR_UNSPECIFIED);
			}
		}
	}

	/**
	 * Sends an order confirmation by email.
	 */
	public void sendOrderConfirmationByEmail(OrderEntity order) {
		if (StringUtils.isEmpty(order.getOrderConfEmailAdresses())) {
			LOG.error("No email addresses specified for order " + order.getOrderId());

			return;
		}

		final String messageSubject = getOrderConfirmationTextMessageSubject(order);
		final String messageBody = getOrderConfirmationTextMessageBody(order,
				ORDER_CONFIRM_EMAIL_MESSAGE_KEY, ORDER_CONFIRM_EMAIL_ORDER_ITEM_KEY);
		final String[] to = StringUtils.split(order.getOrderConfEmailAdresses(), ',');
		final String[] bcc = StringUtils.split(order.getSarea().getMailBcc(), ',');

		/* exceptions would be ignored at the call point of this method anyway,
		 * but the exception went throught the EBJ boundaries between this OrderService and EmailService,
		 * thus JBoss rollbacked the transaction automatically.	*/
		emailService.sendButIgnoreExceptions(to, null, bcc, messageSubject, messageBody,
				new MailPropertiesConfiguration(order));
	}

	/**
	 * @param order
	 * @return
	 */
	private String getOrderConfirmationTextMessageSubject(OrderEntity order) {
		if (order == null) {
			throw new LogonException(
					"Unable to compose confirmation text. Reason: invalid input!",
					Reason.RES_ERROR_IMPL);
		}

		Locale locale = LangUtil.getOrderLocale(order);
		// 0=[ORDER_NUMBER]
		return pdfGeneratorService.formatText(ORDER_CONFIRM_EMAIL_SUBJECT_KEY, locale,
				new Object[] { order.getOrderNumber().toString() });
	}

	/**
	 * @param order
	 * @return
	 */
	private String getOrderConfirmationTextMessageBody(OrderEntity order, String template,
			String itemTemplate) {
		if (order == null) {
			throw new LogonException(
					"Unable to compose confirmation text. Reason: invalid input!",
					Reason.RES_ERROR_IMPL);
		}

		String shiptoSilo = null;
		for (Iterator<?> it = order.getOrderItems().iterator(); it.hasNext();) {
			OrderItemEntity orderItem = (OrderItemEntity) it.next();
			if (orderItem.getShiptoSilo() != null) {
				shiptoSilo = orderItem.getShiptoSilo().getSiloName();
				break;
			}
		}

		SapBpEntity soldtoSapBp = order.getSoldtoSapBp();
		String soldtoName1;
		String soldtoName2;

		if (!soldtoSapBp.getTreatAsOnetimeFlag().booleanValue()) {
			soldtoName1 = soldtoSapBp.getName1();
			soldtoName2 = soldtoSapBp.getName2();
		}
		else {
			soldtoName1 = order.getOneTimeSoldtoName();
			soldtoName2 = null;
		}

		// 0=[ORDER_NUMBER]. 1=order.confirmation.email.order_item, 2=(Date)delivery from, 
		// 3=(Date)delivery to, 4=[SoldTo Name1], 5=[SoldTo Name2], 6=[ShipTo Name1], 
		// 7=[ShipTo Name2], 8=[Street], 9=[House_number], 10=[country], 
		// 11=[Postal_code], 12=[CITY], 13=[ShipTo Silo], 14=[Contact Person]
		SapBpEntity shiptoSapBp = order.getShiptoSapBp();

		final String firstName;
		final String lastName;
		final String street;
		final String houseNumber;
		final String sapCountryCd;
		final String postalCode;
		final String city;

		if (shiptoSapBp.getTreatAsOnetimeFlag() != null
				&& !shiptoSapBp.getTreatAsOnetimeFlag().booleanValue()) {
			firstName = shiptoSapBp.getName1();
			lastName = shiptoSapBp.getName2();
			street = shiptoSapBp.getStreet();
			houseNumber = shiptoSapBp.getHouseNumber();
			sapCountryCd = shiptoSapBp.getCountry().getSapCountryCd();
			postalCode = shiptoSapBp.getPostalCode();
			city = shiptoSapBp.getCity();
		}
		else {
			firstName = order.getLnfSiteName();
			lastName = null;
			street = order.getLnfSiteStreetHouseNo();
			houseNumber = null;
			sapCountryCd = order.getLnfSiteCountry();
			postalCode = order.getLnfSitePostalCode();
			city = order.getLnfSiteCity();
		}

		Locale locale = LangUtil.getOrderLocale(order);
		java.util.TimeZone timeZone = null;
		if (shiptoSapBp.getTimezone() != null) {
			timeZone = java.util.TimeZone.getTimeZone(shiptoSapBp.getTimezone().getTimezoneCd());
		}

		return pdfGeneratorService.formatText(
				template,
				locale,
				timeZone,
				new Object[] {
						order.getOrderNumber().toString(),
						getOrderItemsText(locale, order, itemTemplate, NotificationType.ORDER_CONFIRMATION, null),
						order.getDeliveryFromDat(),
						order.getDeliveryToDat(),
						org.apache.commons.lang.StringUtils.defaultString(soldtoName1),
						org.apache.commons.lang.StringUtils.defaultString(soldtoName2),
						org.apache.commons.lang.StringUtils.defaultString(firstName),
						org.apache.commons.lang.StringUtils.defaultString(lastName),
						org.apache.commons.lang.StringUtils.defaultString(street),
						org.apache.commons.lang.StringUtils.defaultString(houseNumber),
						org.apache.commons.lang.StringUtils.defaultString(sapCountryCd),
						org.apache.commons.lang.StringUtils.defaultString(postalCode),
						org.apache.commons.lang.StringUtils.defaultString(city),
						org.apache.commons.lang.StringUtils.defaultString(shiptoSilo),
						org.apache.commons.lang.StringUtils.defaultString(order.getContactPersonTxt()),
						org.apache.commons.lang.StringUtils.defaultIfEmpty(order.getCustorder().getCustorderCustrefNo(), "-")});
	}

	private String getOrderItemsText(Locale locale, OrderEntity order, String template,
			NotificationType notificationType, MessageType messageType) {
		StringBuilder sb = new StringBuilder();
		for (Iterator<?> it = order.getOrderItems().iterator(); it.hasNext();) {
			OrderItemEntity orderItem = (OrderItemEntity) it.next();

			SapMaterialDescEntity matDesc = getMaterialDescription(orderItem.getSapMaterial(),
					locale);

			BigDecimal quantity = null;
			
			String uomTxt = "";
			
			if (notificationType == NotificationType.LOAD_CONFIRMATION) {
				quantity = orderItem.getLoadedQuantity();
				UomBsEntity bsEntity = orderItem.getLoadedUom();
				
				if (MessageType.SMS == messageType) {
					if (bsEntity != null) {
						uomTxt = bsEntity.getUomCd();
					}
				}
				else {
					if (bsEntity != null) {
						UomTxEntity uomTx = referentialDataService
								.findTxEntity(UomTxEntity.class, locale,
										bsEntity.getUomCd());

						uomTxt = uomTx != null ? org.apache.commons.lang.StringUtils
								.defaultString(uomTx.getTxt()) : "";
					}
				}
				
			}
			else {
				quantity = orderItem.getOrderedQuantity();
				UomBsEntity bsEntity = orderItem.getOrderedUom();
				
				if (bsEntity != null) {
					UomTxEntity uomTx = referentialDataService.findTxEntity(UomTxEntity.class, locale,
							bsEntity.getUomCd());
					
					 uomTxt = uomTx != null ? org.apache.commons.lang.StringUtils.defaultString(uomTx.getTxt()) : "";
				}
			}

			// 0=amount, 1=uom, 2=material
			String itemText = pdfGeneratorService.formatText(
					template,
					locale,
					new Object[] {
							quantity,
							uomTxt,
							matDesc != null ? org.apache.commons.lang.StringUtils.defaultString(matDesc.getTxt()) : "" });
			if (itemText != null) {
				sb.append(itemText);
			}
		}
		return sb.toString();
	}

	private SapMaterialDescEntity getMaterialDescription(SapMaterialEntity material, Locale locale) {
		if (material == null) {
			LOG.error("Unable to get the description of material."
					+ " Reason:  invalid input - material is null!");

			throw new LogonException(Reason.RES_ERROR_UNSPECIFIED);
		}
		ReadOnlyDao<SapMaterialDescEntity> matDescDao = getDaoProvider().getSapMaterialDescEntityDao();
		Filter<SapMaterialDescEntity> filter = new FilterImpl<SapMaterialDescEntity>();
		filter.addExpression(Restrictions.eq(SapMaterialDescEntity.ATTR_SAP_MATERIAL_ID,
				material.getSapMaterialId()));
		filter.addExpression(Restrictions.eq(SapMaterialDescEntity.ATTR_LANG_CD,
				locale.getLanguage()));

		return matDescDao.findSingle(filter);
	}
	
	public void sendLoadConfirmation(OrderEntity order, AssignmentEntity assignment) {
		try {
			if (order.getLoadConfFaxFlag().booleanValue()) {
				try {
					sendLoadConfirmationByFax(order, assignment);
				}
				catch (Exception ex) {
					LOG.error("Fax sending failed for order id " + order.getOrderId(), ex);
				}
			}
			if (order.getLoadConfSmsFlag().booleanValue()) {
				try {
					sendLoadConfirmationBySms(order, assignment);
				}
				catch (Exception ex) {
					LOG.error("SMS sending of load confirmation failed for order id " + order.getOrderId(), ex);
				}
			}
			if (order.getLoadConfEmailFlag().booleanValue()) {
				try {
					sendLoadConfirmationByEmail(order, assignment);
				}
				catch (Exception ex) {
					LOG.error(
							"Sending load confirmation by email failed for order "
									+ order.getOrderId(), ex);
				}
			}
		}
		catch (Exception e) {
			LOG.error("Error while trying to send Notifications.", e);
		}
	}
	
	public void sendLoadConfirmation(OrderEntity order, Long loadId) {
		try {
			AssignmentPartEntity ape = loadingService.findAssignmentPartForLoad(loadId);
			AssignmentEntity assignment = ape.getAssignment();
			sendLoadConfirmation(order, assignment);	
		}
		catch (Exception e) {
			LOG.error("Error while trying to send Notifications.", e);
		}
	}
	/**
	 * Sends an order confirmation by email.
	 */
	public void sendLoadConfirmationByEmail(OrderEntity order, AssignmentEntity assignment) {
		try {
			if (StringUtils.isEmpty(order.getOrderConfEmailAdresses())) {
				LOG.error("No email addresses specified for order " + order.getOrderId());
				return;
			}
			Locale locale = LangUtil.getOrderLocale(order);
			// 0=[ORDER_NUMBER]
			String subject = pdfGeneratorService.formatText(LOAD_CONFIRM_EMAIL_SUBJECT_KEY, locale,
					new Object[] { order.getOrderNumber().toString() });

			final String messageSubject = subject;
			final String messageBody = getLoadConfirmationTextMessageBody(order,
					LOAD_CONFIRM_EMAIL_MESSAGE_KEY, LOAD_CONFIRM_EMAIL_ORDER_ITEM_KEY, assignment, MessageType.EMAIL);
			final String[] to = StringUtils.split(order.getOrderConfEmailAdresses(), ',');
			final String[] bcc = StringUtils.split(order.getSarea().getMailBcc(), ',');

			/* exceptions would be ignored at the call point of this method anyway,
			 * but the exception went throught the EBJ boundaries between this OrderService and EmailService,
			 * thus JBoss rollbacked the transaction automatically.	*/
			emailService.sendButIgnoreExceptions(to, null, bcc, messageSubject, messageBody,
					new MailPropertiesConfiguration(order));
		}
		catch (Exception e) {
			LOG.error("Error while trying to send load confirmation by email. ", e);
		}
	}

	/**
	 * @param order
	 * @return
	 */
	private String getLoadConfirmationTextMessageBody(OrderEntity order, String template,
			String itemTemplate, AssignmentEntity assignment, MessageType messageType) {
		// Example:
		// Dear Customer, your delivery to BW Geneve (nr 30012487) has been loaded 
		// in truck SG4879PT (TransPlus) with 27tons of Fluvio 4, and is planned to arrive 17/05/2013 after 14:45h. 
		if (order == null) {
			throw new LogonException("Unable to compose confirmation text. Reason: invalid input!",
					Reason.RES_ERROR_IMPL);
		}
		SapBpEntity shiptoSapBp = order.getShiptoSapBp();
		final String firstName;
		final String lastName;
		if (shiptoSapBp.getTreatAsOnetimeFlag() != null
				&& !shiptoSapBp.getTreatAsOnetimeFlag().booleanValue()) {
			firstName = shiptoSapBp.getName1();
			lastName = shiptoSapBp.getName2();
		}
		else {
			firstName = order.getLnfSiteName();
			lastName = null;
		}
		Locale locale = LangUtil.getOrderLocale(order);
		java.util.TimeZone timeZone = null;
		if (shiptoSapBp.getTimezone() != null) {
			timeZone = java.util.TimeZone.getTimeZone(shiptoSapBp.getTimezone().getTimezoneCd());
		}
		CarrierEntity execCarrier = order.getExecCarrier();
		String carrierName = execCarrier.getDisplayName();
		String vehicleNr = getPlateOrWagonNumbers(assignment, order);
		order.getDeliveryType();
		
		String contactPersonTxt = order.getContactPersonTxt();
		if (contactPersonTxt == null) {
			contactPersonTxt = Constant.NO_DATA;
		}

		// {0} {1} shipto name
		// {2} order number
		// {3} truck / wagon number
		// {4} commercial or executive carrier
		// {5} materials (i.e. orderitems)
		// {6} Delivery date from
		// {7} Delivery date to
		// {8} ordered by:
		Object[] messageArguments = new Object[] {
				org.apache.commons.lang.StringUtils.defaultString(firstName),
				org.apache.commons.lang.StringUtils.defaultString(lastName),
				order.getOrderNumber().toString(), vehicleNr, carrierName,
				getOrderItemsText(locale, order, itemTemplate, NotificationType.LOAD_CONFIRMATION, messageType), order.getDeliveryFromDat(),
				order.getDeliveryToDat(), contactPersonTxt };
		return pdfGeneratorService.formatText(template, locale, timeZone, messageArguments);
	}

	/**
	 * @throws LogonException, if the order misses it's orderitem, load, or truck/wagon entity.
	 * @param order
	 * @return
	 */
	private String getPlateOrWagonNumbers(AssignmentEntity assignment, OrderEntity order) {
		DeliveryTypeBsEntity deliveryType = order.getDeliveryType();
		Long deliveryTypeCd = deliveryType.getDeliveryTypeCd();
		if (Constant.DELIVERY_TYPE_ROAD_DIRECT == deliveryTypeCd
				|| Constant.DELIVERY_TYPE_BIMODAL_ROAD == deliveryTypeCd
				|| Constant.DELIVERY_TYPE_PICKUP == deliveryTypeCd) {
			TruckEntity truck = assignment.getTruck();
			return truck.getTruckPlateNo();
		}
		if (Constant.DELIVERY_TYPE_RAIL_DIRECT == deliveryTypeCd) {
			WagonEntity wagon = assignment.getWagon();
			return wagon.getWagonNo();
		}
		return Constant.N_A;
	}
	

	/**
	 * Sends an order confirmation by sms.
	 */
	public void sendLoadConfirmationBySms(OrderEntity order, AssignmentEntity assignment) {
		if (StringUtils.isEmpty(order.getOrderConfSmsNumber())) {
			LOG.error("No sms number specified for order " + order.getOrderId());
			return;
		}

		List<String> messages = new ArrayList<String>();
		String fullMessage = getLoadConfirmationTextMessageBody(order,
				LOAD_CONFIRM_SMS_MESSAGE, ORDER_CONFIRM_SMS_ORDER_ITEM, assignment, MessageType.SMS);
		if ((fullMessage != null) && (fullMessage.length() > Constant.SMS_MAX_LENGTH_IN_CHARS)) {
			String messagePartOne = getLoadConfirmationTextMessageBody(order,
					LOAD_CONFIRM_SMS_MESSAGE_1, ORDER_CONFIRM_SMS_ORDER_ITEM, assignment, MessageType.SMS);
			messages.add(com.crh.logon.utils.StringUtils.cutStringWithDots(messagePartOne, Constant.SMS_MAX_LENGTH_IN_CHARS));
			
			messages.add(getLoadConfirmationTextMessageBody(order,
					LOAD_CONFIRM_SMS_MESSAGE_2, ORDER_CONFIRM_SMS_ORDER_ITEM, assignment, MessageType.SMS));
		}
		else {
			messages.add(fullMessage);
		}
		for (Iterator<String> it = messages.iterator(); it.hasNext();) {
			String message = it.next();
			if (!faxAndSmsService.callSendSms(order.getOrderConfSmsNumber(), message)) {
				throw new LogonException("Sending sms failed", Reason.RES_ERROR_UNSPECIFIED);
			}
		}
	}
	
	public void sendLoadConfirmationByFax(OrderEntity order, AssignmentEntity assignment) {
		if (StringUtils.isEmpty(order.getOrderConfFaxNumber())) {
			LOG.error("No fax number specified for order " + order.getOrderId());
			return;
		}
		String message = getLoadConfirmationTextMessageBody(order,
				LOAD_CONFIRM_FAX_MESSAGE, ORDER_CONFIRM_FAX_ORDER_ITEM, assignment, MessageType.FAX);
		if (!faxAndSmsService.callSendFaxText(order.getOrderConfFaxNumber(), message)) {
			throw new LogonException("Sending to fax failed", Reason.RES_ERROR_UNSPECIFIED);
		}
	}
	
	@ScheduledTask(name = "order_notification", second = "0", minute = "7/10", hour = "*", configurationMethodName = "getOrderNotificationCronEnabled", schedule = true)
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void sendNotificationAfterLoading() {

		boolean sendingEnabled = configService.getConfigBean()
				.getOrderNotificationToCustomerBeforeShippingCronEnabled();

		if (!sendingEnabled) {
			LOG.info("Sending Load Notification is not enabled");
			return;
		}

		List<OrderShippNotificationEntity> notificationsToSend = gatherOrderShippingNotifications();

		if (notificationsToSend == null || notificationsToSend.isEmpty()) {
			LOG.info("No new orders for notification to be sent");
			return;
		}

		for (OrderShippNotificationEntity orderShippNotificationEntity : notificationsToSend) {
			try {
				OrderAndAssignmentDTO orderAssignmentTO = notificationService
						.loadMessageData(orderShippNotificationEntity);
				
				OrderEntity order = orderAssignmentTO.getOrder();
				
				boolean messageSentSuccessFully = sendMessagesForOrder(
						order,
						orderAssignmentTO.getAssignMent());

				if (messageSentSuccessFully) {
					notificationService.setSentTimeOrder(order);
				}
				
			} catch (Exception e) {
				LOG.error("Error at sending notification for order: "
						+ orderShippNotificationEntity.getOrderId(), e);
			}

		}

	}
		
	public OrderAndAssignmentDTO loadMessageData(OrderShippNotificationEntity orderShippNotificationEntity) {
		OrderEntity order = findOrderEntityById(orderShippNotificationEntity.getOrderId());
		order.initialize();
		
		AssignmentEntity assignment = orderDetailServiceLocalInterface.loadAssignment(order.getOrderId());
		assignment.initialize();
		
		OrderAndAssignmentDTO orderAndAssignmentDTO = new OrderAndAssignmentDTO(order, assignment);
		return orderAndAssignmentDTO;
		
		
	}
	
	public void setSentTimeOrder(OrderEntity orderEntity) {
		
		DaoProvider daoProvider = getDaoProvider();
		ReadWriteDao<OrderEntity> orderEnityDao = daoProvider.getOrderEntityDao();
		
		Date currentDate = new Date();
		orderEntity.setLoadNotificationSentTs(currentDate);
		orderEnityDao.merge(orderEntity);
		
	}
	
	private OrderEntity findOrderEntityById(Long id) {
		DaoProvider daoProvider = getDaoProvider();
		ReadWriteDao<OrderEntity> dao = daoProvider.getOrderEntityDao();
		return dao.findById(id);
	}
	
	private List<OrderShippNotificationEntity> gatherOrderShippingNotifications() {
		
		DaoProvider daoProvider = getDaoProvider();
		ReadOnlyDao<OrderShippNotificationEntity> orderShippingNotiDao = daoProvider.getOrderShippNotificationEntityDao();
		
		Filter<OrderShippNotificationEntity> filter = orderShippingNotiDao.createFilter();
		
		addOrderShippingNotificationFilters(filter);
		
		List<OrderShippNotificationEntity> result = orderShippingNotiDao.find(filter);
		
		return result;
		
	}
	
	private void addOrderShippingNotificationFilters(Filter<OrderShippNotificationEntity> filter) {
		filter.addExpression(Restrictions.or(getCriteriaForSendingPickup(), getCriteriaForSendingRail(), getCriteriaForSendingRoad()));
	}
	
	//SENDING CRITERIA ROAD
	private Expression getCriteriaForSendingRoad() {
		Expression result = Restrictions.and(getCriteriaOrderStatusRoadWF(), getCriteriaRoadWF(), getCriteriaIgnorePreload(), getCriteriaPreventLateDelivery());
		return result;
	}
	
	//SENDING CRITERIA RAIL
	private Expression getCriteriaForSendingRail() {
		Expression result = Restrictions.and(getCriteriaRailWF(), getCriteriaOrderStatusRailWF());
		return result;
	}
	
	//SENDING CRITERIA PICKUP
	private Expression getCriteriaForSendingPickup() {
		Expression result = Restrictions.and(getCriteriaPickupWF(), getCriteriaOrderStatusPickupWF());
		return result;
	}
	
	private Expression getCriteriaRoadWF() {
		
		Expression result = Restrictions.or(Restrictions.eq(
				OrderShippNotificationEntity.ATTR_DELIVERY_TYPE_CD,
				Long.valueOf(Constant.DELIVERY_TYPE_BIMODAL_ROAD)), Restrictions.eq(
				OrderShippNotificationEntity.ATTR_DELIVERY_TYPE_CD,
				Long.valueOf(Constant.DELIVERY_TYPE_ROAD_DIRECT)));

		return result;
	}
	
	private Expression getCriteriaOrderStatusRoadWF() {
		List<Long> orderStatuses = new ArrayList<Long>();
		
		orderStatuses.add(Constant.ORDER_STATUS_SHIPPING);
		orderStatuses.add(Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT);
		orderStatuses.add(Constant.ORDER_STATUS_UNLOADING);
		orderStatuses.add(Constant.ORDER_STATUS_DELIVERED);
		orderStatuses.add(Constant.ORDER_STATUS_FINISHED);
		
		Expression result = Restrictions.in(OrderShippNotificationEntity.ATTR_ORDER_STATUS_CD, orderStatuses);
		
		return result;
	}
	
	private Expression getCriteriaRailWF() {
		
		Expression result = Restrictions.eq(
				OrderShippNotificationEntity.ATTR_DELIVERY_TYPE_CD,
				Long.valueOf(Constant.DELIVERY_TYPE_RAIL_DIRECT));

		return result;
		
	}
	
	private Expression getCriteriaOrderStatusRailWF() {
		
		Expression transmittedToCarrier = Restrictions.and(Restrictions.eq(
				OrderShippNotificationEntity.ATTR_TRANSMIT_TO_CARRIER_FLAG,
				true), Restrictions.eq(
				OrderShippNotificationEntity.ATTR_ORDER_STATUS_CD,
				Constant.ORDER_STATUS_SHIPPING));

		
		List<Long> orderStatuses = new ArrayList<Long>();
				
				orderStatuses.add(Constant.ORDER_STATUS_SHIPPING);
				orderStatuses.add(Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL);

		
		Expression notTransmittedToCarrier = Restrictions.and(Restrictions.eq(
				OrderShippNotificationEntity.ATTR_TRANSMIT_TO_CARRIER_FLAG,
				false), Restrictions.in(
				OrderShippNotificationEntity.ATTR_ORDER_STATUS_CD,
				orderStatuses));

		return Restrictions.or(transmittedToCarrier, notTransmittedToCarrier);
	}
	
	private Expression getCriteriaPickupWF() {
		
		Expression result = Restrictions.eq(
				OrderShippNotificationEntity.ATTR_DELIVERY_TYPE_CD,
				Long.valueOf(Constant.DELIVERY_TYPE_PICKUP));

		return result;
	}
	
	private Expression getCriteriaOrderStatusPickupWF() {
		
		Expression result = Restrictions.eq(OrderShippNotificationEntity.ATTR_ORDER_STATUS_CD, Constant.ORDER_STATUS_DELIVERED);
		return result;
	}
	
	private Expression getCriteriaPreventLateDelivery() {

		DateTime currentDate = new DateTime();
		int hours = currentDate.getHourOfDay();
		int minutes = currentDate.getMinuteOfHour();
		
		//this conversation is needed for the value of the column.
		Long currentTimeConverted = (long) (hours * 60) + (long) minutes;

		Expression result = Restrictions.ge(
				OrderShippNotificationEntity.ATT_PREV_LATE_DELIVERY,
				currentTimeConverted);
		return result;
	}
	
	private Expression getCriteriaIgnorePreload() {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_WITHOUT_TIME_FORMAT);
		Date date = new Date();
		try {
			date = sdf.parse(sdf.format(date));
		} catch (ParseException e) {
			LOG.error("Arror parsing date", e);
		}
		
		Expression result = Restrictions.le(OrderShippNotificationEntity.ATTR_PLANNED_START, date);
		return result;
	}
	
	
	private boolean sendMessagesForOrder(OrderEntity order, AssignmentEntity assignment) throws InterruptedException {
		
		boolean result = false;
		
		Future<Boolean> emailSent = null;
		Future<Boolean> smsSent = null;
		Future<Boolean> faxSent = null;
		List<Future<Boolean>> allSentNotification = new ArrayList<Future<Boolean>>();
		
		if (Boolean.TRUE.equals(order.getLoadConfEmailFlag())) {
			emailSent = orderShippingNotificationSender.sendEmail(order, assignment);
			allSentNotification.add(emailSent);
		}

		if (Boolean.TRUE.equals(order.getLoadConfSmsFlag())) {
			smsSent = orderShippingNotificationSender.sendSMS(order, assignment);
			allSentNotification.add(smsSent);
		}

		if (Boolean.TRUE.equals(order.getLoadConfFaxFlag())) {
			faxSent = orderShippingNotificationSender.sendFax(order, assignment);
			allSentNotification.add(faxSent);
		}
		
		result = getFirstDoneAndSuccessful(allSentNotification);
				
		return result;
				
	}
	
	private static boolean getFirstDoneAndSuccessful(
			List<Future<Boolean>> futures) throws InterruptedException {

		long timeOfStart = System.currentTimeMillis();
		
		boolean timeOutReached = false;
		
		while (!timeOutReached) {
			Iterator<Future<Boolean>> it = futures.iterator();
			
			while (it.hasNext()) {
				Future<Boolean> future = it.next();
				if (future.isDone()) {
					Boolean result = null;
					try {
						result = future.get();
					} catch (Exception e) {
						LOG.error("Failed at reading async message", e);
					}
					if (Boolean.TRUE.equals(result)) {
						return true;
					} else {
						it.remove();
					}
				}
			}

			long currentTime = System.currentTimeMillis();
			
			//AFTER SIX MIN CANCEL ALL THE ASYNC THREADS AND RETURN WITH UNSUCCESSFUL(false)
			if (currentTime - timeOfStart > SIX_MIN_TIMEOUT_IN_MILIS) {
				for (Future<Boolean> future : futures) {
					future.cancel(true);
				}
				timeOutReached = true;
			}

			if (futures.isEmpty()) {
				return false;
			}

			Thread.sleep(1000);

		}
		
		return false;
	}
}
