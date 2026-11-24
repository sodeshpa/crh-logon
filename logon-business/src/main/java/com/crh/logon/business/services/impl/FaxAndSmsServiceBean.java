/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PrinterName;

import com.crh.logon.webservices.clients.beans.ferrari.FileMessageBean;
import com.crh.logon.webservices.clients.beans.sirocco20.TextMessageBean;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.ipp.IppPrintService;
import com.crh.logon.business.services.dispatchers.FaxAndSmsServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.interfaces.EmailService;
import com.crh.logon.business.services.localinterfaces.FaxAndSmsServiceLocalInterface;
import com.crh.logon.business.services.utils.Services;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.OutputdocEntity;
import com.crh.logon.persistence.entities.refdata.MimeTypeBsEntity;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.webservices.clients.LogonToFFax;
import com.crh.logon.webservices.clients.LogonToFSms;
import com.crh.logon.webservices.clients.LogonToSirocco20Sms;

/**
 * @author AdNovum Informatik AG
 */
@AdnTrace
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class FaxAndSmsServiceBean extends FaxAndSmsServiceDispatcher implements FaxAndSmsServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(FaxAndSmsServiceBean.class);
	
	private static final String NUMBER_REGEXP_SPLIT = "[,;]";

	public void sendToPrinter(URI loadingpointsPrinterURI, String printerName,
			OutputdocEntity document, int numberOfCopies) throws PrintException {
		URI uriToBeUsed;
		ConfigBean cb = getConfigBean();
		boolean printerTestEnabled = cb.getPrinterTestEnabled();
		if (printerTestEnabled) {
			String printerTestAddress = cb.getPrinterTestAddress();
			if (!com.crh.logon.utils.StringUtils.notEmpty(printerTestAddress)) {
				LOG.debug("Test printing enabled, "
						+ "but empty test printer address specified, so printing skipped.");
				return;
			}
			try {
				uriToBeUsed = URI.create(printerTestAddress);
			}
			catch (IllegalArgumentException exception) {
				String message = String.format("Test printing enabled, "
						+ "but invalid test printer address specified: '%s', "
						+ "so printing skipped.", printerTestAddress);
				throw new PrintException(message);
			}
		}
		else {
			uriToBeUsed = loadingpointsPrinterURI;
		}
		PrintService printService = null;
		DocFlavor docFlavor = null;
		MimeTypeBsEntity dataMimeType = document.getDataMimeType();
		Long mimeTypeCd = dataMimeType.getMimeTypeCd();
		if (mimeTypeCd.equals(Constant.MIME_TYPE_PDF)) {
			LOG.error("PDF documents cannot be printed.");
			return;
		}
		if (uriToBeUsed != null) {
			String uriString = uriToBeUsed.toString();
			if (!uriString.startsWith("ipp://") || !uriString.endsWith("/ipp")) {
				throw new PrintException(
						"Printer URI must start with 'ipp://' and end with '/ipp' (" + uriString
								+ ")");
			}
			printService = new IppPrintService(uriToBeUsed);
			docFlavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
		}
		else if (printerName != null) {
			PrintServiceAttributeSet aset = new HashPrintServiceAttributeSet();
			Locale defaultLocale = Locale.getDefault();
			PrinterName newPrinterName = new PrinterName(printerName, defaultLocale);
			aset.add(newPrinterName);
			PrintService[] pservices = PrintServiceLookup.lookupPrintServices(null, aset);
			if (pservices.length == 1) {
				printService = pservices[0];
				if (mimeTypeCd.equals(Constant.MIME_TYPE_PS)) {
					docFlavor = DocFlavor.BYTE_ARRAY.POSTSCRIPT;
				}
				else {
					throw new PrintException("Unsupported document type");
				}
			}
			else {
				LOG.error(pservices.length + " printers found with the name " + printerName);
				LOG.info("The following printers are available: ");
				pservices = PrintServiceLookup.lookupPrintServices(null, null);
				for (int i = 0; i < pservices.length; i++) {
					PrintService currentPrintService = pservices[i];
					String currentPrintServiceName = currentPrintService.getName();
					LOG.info(currentPrintServiceName);
				}
			}
		}
		else {
			throw new PrintException("Either uri or printerName must be given");
		}

		if ((printService != null) && (docFlavor != null)) {
			LOG.info("Printing to " + printService.getName());
			DocPrintJob pj = printService.createPrintJob();
			byte[] data = document.getData();
			Doc doc = new SimpleDoc(data, docFlavor, null);
			PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
			Copies copies = new Copies(numberOfCopies);
			attributes.add(copies);
			pj.print(doc, attributes);
		}
		else {
			throw new PrintException("Printer " + uriToBeUsed + " [" + printerName + "] not found");
		}
	}

	private static Collection<String> splitNumbers(String numbers) {
		final Collection<String> numberList = new ArrayList<String>();

		String[] numberArray = numbers.split(NUMBER_REGEXP_SPLIT);
		for (int i = 0; i < numberArray.length; i++) {
			if (StringUtils.isNotBlank(numberArray[i])) {
				numberList.add(numberArray[i]);
			}
		}
		return numberList;
	}

	@SuppressWarnings("unchecked")
	private static final <T> T getImplementation(Class<T> clazz, String serviceProvider) {
		try {
			final String implClassName = FaxAndSmsServiceBean.class.getName() + "$_Impl_" + StringUtils.upperCase(serviceProvider);
			
			final Class<?> implClass = ClassUtils.getClass(implClassName);	
						
			if (ClassUtils.isAssignable(implClass, clazz)) {				
				return (T) implClass.getEnumConstants()[0];
			}
			else {
				throw new IllegalArgumentException(String.format(
						"'%s' is not assignable to '%s'!",
						implClass, clazz));
			}
		}
		catch (Exception e) {
			LOG.error(String.format(
					"Failed to get specified provider (%s), skipping!",
					serviceProvider), e);
			return null;
		}
	}

	/**
	 * 
	 * @param number - phone number, the sms to send to
	 * @param message - message to send as sms
	 * @return returns true if and only if the call for the webservice is successful.
	 */
	public boolean callSendSms(String numbers, String message) {
		final String serviceProvider = getConfigBean().getAppDefaultProviderSms();
		return callSendSms(getServices(), getConfigBean(), serviceProvider, numbers, message);
	}

	/**
	 * 
	 * @param numbers - phone numbers, the fax to send to
	 * @param document - representation of the attachment as OutputdocEntity
	 * @return returns true if and only if the call for the webservice is successful.
	 */
	public boolean callSendFaxFile(String numbers, OutputdocEntity document) {
		final String serviceProvider = getConfigBean().getAppDefaultProviderFax();
		return callSendFaxFile(getServices(), getConfigBean(), serviceProvider, numbers, document);
	}

	/**
	 * 
	 * @param number - phone number, the fax to send to
	 * @param message - message to send as fax
	 * @return returns true if and only if the call for the webservice is successful.
	 */
	public boolean callSendFaxText(String numbers, String message) {
		final String serviceProvider = getConfigBean().getAppDefaultProviderFax();
		return callSendFaxText(getServices(), getConfigBean(), serviceProvider, numbers, message);
	}
	

	/**
	 * 
	 * @param number - phone number, the sms to send to
	 * @param message - message to send as sms
	 * @return returns true if and only if the call for the webservice is successful.
	 */
	static boolean callSendSms(Services services, ConfigBean configBean, String serviceProvider, String numbers, String message) {
		boolean result = false;
		final Collection<String> numberList = splitNumbers(numbers);

		if (StringUtils.isBlank(serviceProvider)) {
			result = true;
			LOG.info("No service provider specified for callSendSms, skipping.");
		}
		else {
			final SmsSender impl = getImplementation(SmsSender.class, serviceProvider);
			
			if (impl != null) {
				result = impl.sendSms(services, configBean, numberList, message);
			}
		}
		return result;
	}

	/**
	 * 
	 * @param numbers - phone numbers, the fax to send to
	 * @param document - representation of the attachment as OutputdocEntity
	 * @return returns true if and only if the call for the webservice is successful.
	 */
	static boolean callSendFaxFile(Services services, ConfigBean configBean, String serviceProvider, String numbers, OutputdocEntity document) {
		boolean result = false;
		final Collection<String> numberList = splitNumbers(numbers);

		if (StringUtils.isBlank(serviceProvider)) {
			result = true;
			LOG.info("No service provider specified for callSendFaxFile, skipping.");
		}
		else {
			final FaxSender impl = getImplementation(FaxSender.class, serviceProvider);
			
			if (impl != null) {
				result = impl.sendFaxFile(services, configBean, numberList, document);
			}
		}

		return result;
	}

	/**
	 * 
	 * @param number - phone number, the fax to send to
	 * @param message - message to send as fax
	 * @return returns true if and only if the call for the webservice is successful.
	 */
	static boolean callSendFaxText(Services services, ConfigBean configBean, String serviceProvider, String numbers, String message) {
		boolean result = false;
		final Collection<String> numberList = splitNumbers(numbers);

		if (StringUtils.isBlank(serviceProvider)) {
			result = true;
			LOG.info("No service provider specified for callSendFaxFile, skipping.");
		}
		else {
			final FaxSender impl = getImplementation(FaxSender.class, serviceProvider);
			
			if (impl != null) {
				result = impl.sendFaxText(services, configBean, numberList, message);
			}
		}

		return result;
	}

	static String[] getFaxImplementations() {
		return getImplementations(FaxSender.class);
	}
	
	static String[] getSmsImplementations() {
		return getImplementations(SmsSender.class);
	}
	
	private static String[] getImplementations(Class<?> clazz) {		
		final Collection<String> result = new ArrayList<String>();
		for (Class<?> impl : FaxAndSmsServiceBean.class.getDeclaredClasses()) {			
			if (clazz.isAssignableFrom(impl) && impl.isEnum()) {
				result.add(impl.getSimpleName().replaceAll("^_Impl_", ""));
			}
		}
		
		return result.toArray(new String[]{});
	}
	
	static interface SmsSender {
		boolean sendSms(Services services, ConfigBean configBean, Collection<String> numbers, String message);
	}

	static interface FaxSender {
		boolean sendFaxFile(Services services, ConfigBean configBean, Collection<String> numbers, OutputdocEntity document);
		boolean sendFaxText(Services services, ConfigBean configBean, Collection<String> numbers, String message);
	}
	
	@SuppressWarnings("unused")
	private enum _Impl_RFAX implements FaxSender {
		
		inst;
		
		private String[] getRightFaxRecipients(ConfigBean configBean, Collection<String> numbers) {
			final String[] recipients = new String[numbers.size()];
			final String hostname = configBean.getAppRightfaxHostname();
			final String recipientSyntax = configBean.getAppRightfaxRecipientSyntax();

			int i = 0;
			for (String number : numbers) {
				recipients[i++] = String.format(recipientSyntax, number, hostname);
			}
			return recipients;
		}
	
		public boolean sendFaxFile(Services services, ConfigBean configBean, Collection<String> numbers, OutputdocEntity document) {
			boolean result = false;
			if (numbers == null) {
				numbers = Collections.emptyList();
			}
			result = true;
			final EmailService emailService = services.getEmailService();
			final String[] to = getRightFaxRecipients(configBean, numbers);
			final String subject = configBean.getAppRightfaxSubject();

			emailService.send(
					to, null, null,
					subject, "",					 
					null,
					emailService.createMimeBodyPartAttachment(	
							document.getData(),
							document.getDataMimeType(),
							document.getOutputdocName()));
			return result;
		}
	
		public boolean sendFaxText(Services services, ConfigBean configBean, Collection<String> numbers, String message) {
			boolean result = false;
			if (numbers == null) {
				numbers = Collections.emptyList();
			}
			result = true;
			final EmailService emailService = services.getEmailService();
			final String[] to = getRightFaxRecipients(configBean, numbers);
			final String subject = configBean.getAppRightfaxSubject();

			emailService.send(to, null, null, subject, message, null);

			return result;
		}
	}
	
	@SuppressWarnings("unused")
	private enum _Impl_FFAX implements FaxSender {

		inst;

		/**
		 * This method is used to call SendFaxFile operation of external 
		 * webservice FFax. This is the way to send a file as an attachment 
		 * for a fax via FFax webservice.
		 * 
		 * @param numbers - phone number, the fax to send to
		 * @param document - representation of the attachment as OutputdocEntity
		 * @return returns true if and only if the call for the webservice is successful.
		 */
		public boolean sendFaxFile(Services services, ConfigBean configBean, Collection<String> numbers, OutputdocEntity document) {
			boolean result = true;
			FileMessageBean faxMsg;
			MimeTypeBsEntity mime;
			String fullAttachmentName;
			for (String number : numbers) {	
				if (document == null) {
					LOG.error("Unable to compose FileMessageBean."
							+ "Invalid input: BLOB is null!");
				
					throw new LogonException(Reason.RES_ERROR_UNSPECIFIED);
				}
				
				mime = document.getDataMimeType();
				fullAttachmentName = document.getOutputdocName();
				if (mime.getFileExtension() != null) {
					fullAttachmentName += "." + mime.getFileExtension();
				}
				
				faxMsg = new FileMessageBean(
						number, 
						mime.getMimeType(),
						fullAttachmentName, 
						document.getData());
				try {
		            LogonToFFax.sendFaxFile(faxMsg, configBean);
				}
				catch (LogonException e) {
					result = false;
					LOG.error(e.getMessage(), e);
				}
			}
			
			return result;
		}
		
		/**
		 * This method is used to call SendFaxText operation of external 
		 * webservice FFax. This is the way to send a text-message as fax 
		 * via FFax webservice.
		 * 
		 * @param numbers - phone number, the fax to send to
		 * @param message - message to send as fax
		 * @return returns true if and only if the call for the webservice is successful.
		 */
		public boolean sendFaxText(Services services, ConfigBean configBean, Collection<String> numbers, String message) {
			boolean result = true;
			com.crh.logon.webservices.clients.beans.ferrari.TextMessageBean faxMsg;

			for (String number : numbers) {
				faxMsg = new com.crh.logon.webservices.clients.beans.ferrari.TextMessageBean(
						number, message);
				try {
					LogonToFFax.sendFaxText(faxMsg, configBean);
				}
				catch (LogonException e) {
					result = false;
					LOG.error(e.getMessage(), e);
				}
			}

			return result;
		}
	}
		
	@SuppressWarnings("unused")
	private enum _Impl_FSMS implements SmsSender {

		inst;
		
		/**
		 * This method is used to call SendSms operation of external webservice 
		 * FSms. This is the way to send SMS via FSms webservice.
		 * 
		 * @param numbers - phone number, the sms to send to
		 * @param message - message to send as sms
		 * @return returns true if and only if the call for the webservice is successful.
		 */
		public boolean sendSms(Services services, ConfigBean configBean, Collection<String> numbers, String message) {
			boolean result = true;
			com.crh.logon.webservices.clients.beans.ferrari.TextMessageBean sms;

			for (String number : numbers) {
				sms = new com.crh.logon.webservices.clients.beans.ferrari.TextMessageBean(
						number, message);
				try {
					LogonToFSms.sendSms(sms, configBean);
				}
				catch (LogonException e) {
					result = false;
					LOG.error(e.getMessage(), e);
				}
			}

			return result;
		}
	}
	
	@SuppressWarnings("unused")
	private enum _Impl_SSMS implements SmsSender {

		inst;
		
		/**
		 * This method is used to call SendSms operation of external webservice 
		 * OxyaSms. This is the way to send SMS via OxyaSms webservice.
		 * 
		 * @param numbers - phone number, the sms to send to
		 * @param message - message to send as sms
		 * @return returns true if and only if the call for the webservice is successful.
		 */
		public boolean sendSms(Services services, ConfigBean configBean, Collection<String> numbers, String message) {
			boolean result = true;
			try {
				TextMessageBean sms;
				
				sms = new TextMessageBean(
						configBean.getWsclientSsmsRequestUser(),
						configBean.getWsclientSsmsRequestPassword(),
						configBean.getWsclientSsmsShortcode(),
						numbers, message);
				LogonToSirocco20Sms.sendSms(sms, configBean);

				return result;
			}
			catch (LogonException e) {
				result = false;
				LOG.error(e.getMessage(), e);
			    return result;
			}
		}
	}
}
