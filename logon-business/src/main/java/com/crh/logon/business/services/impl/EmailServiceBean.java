/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.EmailServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.EmailServiceLocalInterface;
import com.crh.logon.business.services.utils.MailPropertiesConfiguration;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.refdata.MimeTypeBsEntity;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonException;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class EmailServiceBean extends EmailServiceDispatcher implements EmailServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(EmailServiceBean.class);
	
	/**
	 * Create a Properties object containing the email host/port and from properties.
	 * @return
	 */
	private Properties getMailProperties(MailPropertiesConfiguration config) {

		final SareaEntity sarea = config.getSarea();
		final ConfigBean configBean = getConfigBean();		
		final Properties properties = new Properties();
		if (configBean.getMailSmtpHost() != null) {
			properties.setProperty("mail.smtp.host", configBean.getMailSmtpHost());
		}
		properties.setProperty("mail.smtp.port", Integer.toString(configBean.getMailSmtpPort()));
		
		//Crh spam filter may prevent sending of the email with different 'mail.from' other than these two.
		//if another 'mail.from' is supposed to be added that has to be allowed by Crh IT as well otherwise the
		//email will not be sent because of the Crh spam filter
		final String mailFrom;
		switch (config.getConfigurationType()) {
			case DEFAULT:
				if ((sarea != null) && (sarea.getMailFrom() != null)) {
					mailFrom = sarea.getMailFrom();
				}
				else {
					mailFrom = configBean.getMailFrom();
				}
				break;
				
			case REP_ORDER:
				if ((sarea != null) && (sarea.getMailRailadmin() != null)) {
					mailFrom = sarea.getMailRailadmin();
				}
				else {
					mailFrom = configBean.getMailRailadmin();
				}
				break;
				
			default:
				LOG.error("Invalid mail default setting, could not define mail properties");
				throw new LogonException();
		}
		
		if (mailFrom != null) {
			properties.setProperty("mail.from", mailFrom);
		}
		
		if ((configBean.getMailTestEnabled()) && (LOG.isDebugEnabled())) {
			properties.setProperty("mail.debug", "true");
		}
		
		return properties;
	}
	
	private String getEncoding(SareaEntity sarea) {
		if ((sarea != null) && (sarea.getMailEncoding() != null)) {
			return sarea.getMailEncoding();
        }
        else {        
        	return getConfigBean().getMailEncoding();
        }
	}
	
	private String getMessageText(Address[] to, Address[] cc, Address[] bcc, String messageText) {
	
		if (getConfigBean().getMailTestEnabled()) {
			final StringBuilder sb = new StringBuilder("*LOGON TEST-MAIL*\nOriginal recipients: ");
			if (to != null) {
				sb.append(Arrays.toString(to));
			}
			
			if (cc != null) {
				sb.append("\ncc: ");
				sb.append(Arrays.toString(cc));
			}

			if (bcc != null) {
				sb.append("\nbcc: ");
				sb.append(Arrays.toString(bcc));
			}
			
			return sb.toString() + "\n\n" + messageText;  
		}
		
		return messageText;
	}
	
	private MimeMessage createEmptyMimeMessage(Session session, String subject, String message, SareaEntity sarea) throws MessagingException {
		
		final String actualSubject;
		if (getConfigBean().getMailTestEnabled()) {
			actualSubject = subject + " *LOGON TEST-MAIL*";
		}
		else {
			actualSubject = subject;
		}
		final MimeMessage msg = new MimeMessage(session);
        msg.setFrom();
        msg.setSubject(actualSubject, getEncoding(sarea));
        msg.setSentDate(new Date());
        
        return msg;
	}
	
	private void addRecipients(Address[] to, Address[] cc, Address[] bcc, MimeMessage msg, SareaEntity sarea) throws MessagingException {

		final Collection<Address> actualTo = getRecipients(to, Message.RecipientType.TO, sarea);
        final Collection<Address> actualCc = getRecipients(cc, Message.RecipientType.CC, sarea);
        final Collection<Address> actualBcc = getRecipients(bcc, Message.RecipientType.BCC, sarea);
        
        actualCc.removeAll(actualTo);
        actualBcc.removeAll(actualTo);
        actualBcc.removeAll(actualCc);

    	msg.addRecipients(Message.RecipientType.TO, actualTo.toArray(new Address[actualTo.size()]));
    	msg.addRecipients(Message.RecipientType.CC, actualCc.toArray(new Address[actualCc.size()]));
    	msg.addRecipients(Message.RecipientType.BCC, actualBcc.toArray(new Address[actualBcc.size()]));
	}
	
	public Collection<Address> getRecipients(Address[] recipients, Message.RecipientType recipientType, SareaEntity sarea) throws AddressException {
		final Address[] actualRecipients;
		if (getConfigBean().getMailTestEnabled()) {	
			if (ObjectUtils.equals(recipientType, Message.RecipientType.TO)) {

				final String testTo;
				if ((sarea != null) && (sarea.getMailTestTo() != null)) {
					testTo = sarea.getMailTestTo();
		        }
		        else {        
		        	testTo = getConfigBean().getMailTestTo();
		        }
				
				if (StringUtils.isNotBlank(testTo)) {
					actualRecipients = InternetAddress.parse(testTo);
				}
				else {
					actualRecipients = null;
				}
			}
			else if (ObjectUtils.equals(recipientType, Message.RecipientType.BCC)) {

				final String defaultBcc;
				if ((sarea != null) && (sarea.getMailBcc() != null)) {
					defaultBcc = sarea.getMailBcc();
		        }
		        else {        
		        	defaultBcc = getConfigBean().getMailBcc();
		        }
				
				if (StringUtils.isNotBlank(defaultBcc)) {
					actualRecipients = InternetAddress.parse(defaultBcc);
				}
				else {
					actualRecipients = recipients;
				}
			}
			else {
				actualRecipients = null;
			}
		}
		else {
			actualRecipients = recipients;
		}
		
		final Collection<Address> result = new LinkedHashSet<Address>();
		if (actualRecipients != null) {
			for (Address actualRecipient : actualRecipients) {
				if (actualRecipient != null) {
					result.add(actualRecipient);
				}
			}
		}
		return result;
	}	
		
	private Address[] parseAddresses(String[] addresses) throws AddressException {
		final Collection<Address> result = new LinkedHashSet<Address>();
		if (addresses != null) {
			for (String address : addresses) {
				if (StringUtils.isNotBlank(address)) {
					result.add(new InternetAddress(address));
				}
			}
		}
		
		return result.toArray(new Address[result.size()]);
	}
	
	@Override
	public boolean sendButIgnoreExceptions(String[] to, String[] cc, String[] bcc, String subject, String message, MailPropertiesConfiguration config) {
		return sendButIgnoreExceptions(to,cc,bcc, subject, message, config, (MimeBodyPart[]) null);

	}
	
	/**
     *  like normal email sending, but logging any exceptions than swallow it.
     * 	@return true if sending succeeded, false if there was any exception
     * */ 
	@Override
	public boolean sendButIgnoreExceptions(String[] to, String[] cc, String[] bcc, String subject, String message, MailPropertiesConfiguration config, MimeBodyPart[] attachments) {
		try {
			send(to,cc,bcc, subject, message, config, attachments);
			return true;
		} catch (Exception ex) {
			LOG.error("Ignoring exception on purpose", ex);
			return false;
		}
	}
	
	public void send(String[] to, String[] cc, String[] bcc, String subject, String message, MailPropertiesConfiguration config) {
		// [[
		Object[][] params = {{"to", to},
							{"cc", cc},
							{"bcc", bcc},
							{"subject", subject},
							{"message", message},
							{"config", config}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "send", params);
		}
		// ]]
		try {
			send(parseAddresses(to), parseAddresses(cc), parseAddresses(bcc), subject, message, config, (MimeBodyPart[]) null);			
		} catch (AddressException mex) {
			throw new LogonException(mex);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}		
	}

	public void send(String[] to, String[] cc, String[] bcc, String subject, String message, MailPropertiesConfiguration config, MimeBodyPart attachment) {
		// [[
		Object[][] params = {{"to", to},
							{"cc", cc},
							{"bcc", bcc},
							{"subject", subject},
							{"message", message},
							{"attachment", attachment},
							{"config", config}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "send", params);
		}
		// ]]
		try {
			send(parseAddresses(to), parseAddresses(cc), parseAddresses(bcc), subject, message, config, new MimeBodyPart[]{attachment});			
		} catch (AddressException mex) {
			throw new LogonException(mex);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}	
	
	public void send(String[] to, String[] cc, String[] bcc, String subject, String message, MailPropertiesConfiguration config, MimeBodyPart[] attachments) {
		// [[
		Object[][] params = {{"to", to},
							{"cc", cc},
							{"bcc", bcc},
							{"subject", subject},
							{"message", message},
							{"attachments", attachments},
							{"config", config}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "send", params);
		}
		// ]]
		try {
			send(parseAddresses(to), parseAddresses(cc), parseAddresses(bcc), subject, message, config, attachments);			
		} catch (AddressException mex) {
			throw new LogonException(mex);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	private void send(Address[] to, Address[] cc, Address[] bcc, String subject, String message, MailPropertiesConfiguration config, MimeBodyPart[] attachments) {
		// [[
		Object[][] params = {{"to", to},
							{"cc", cc},
							{"bcc", bcc},
							{"subject", subject},
							{"message", message},
							{"config", config},
							{"attachments", attachments}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "send", params);
		}
		// ]]
		try {
			final MailPropertiesConfiguration cfg;
			if (config == null) {
				cfg = new MailPropertiesConfiguration();
			}
			else {
				cfg = config;
			}
			final SareaEntity sarea = cfg.getSarea();
			final Properties properties = getMailProperties(cfg);
			final Session session = Session.getInstance(properties, null);			
			final MimeMessage msg = createEmptyMimeMessage(session, subject, message, sarea);
			
			addRecipients(to, cc, bcc, msg, sarea);
			to = msg.getRecipients(RecipientType.TO);
			cc = msg.getRecipients(RecipientType.CC);
			bcc = msg.getRecipients(RecipientType.BCC);

			final String mailText = getMessageText(to, cc, bcc, message);	
			final String mailEncoding = getEncoding(sarea);		
			
			if ((attachments != null) && (attachments.length > 0)) {
				final MimeMultipart mm = new MimeMultipart(); 
				final MimeBodyPart mbpText = new MimeBodyPart();
				
				mbpText.setText(mailText, mailEncoding);
				mm.addBodyPart(mbpText);
				msg.setContent(mm);
				
				for (MimeBodyPart attachment : attachments) {
					mm.addBodyPart(attachment);								
				}
			}
			else {
				msg.setText(mailText, mailEncoding);
			}
			
			Transport.send(msg);
		} catch (MessagingException mex) {
			throw new LogonException(mex);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public MimeBodyPart createMimeBodyPartAttachment(byte[] attachmentData, MimeTypeBsEntity mimeType, String attachmentName)  {
		// [[
		MimeBodyPart result = null;
		Object[][] params = {{"attachmentData", attachmentData},
							{"mimeType", mimeType},
							{"attachmentName", attachmentName}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createMimeBodyPartAttachment", params);
		}
		// ]]
		try {
			final MimeBodyPart mbpAttachment = new MimeBodyPart();
			mbpAttachment.setDataHandler(new DataHandler(new ByteArrayDataSource(attachmentData, mimeType.getMimeType())));
			mbpAttachment.setDescription(attachmentName);
			
			String filename = attachmentName;
			if (mimeType.getFileExtension() != null) {
				filename += "." + mimeType.getFileExtension();
			}
			
			mbpAttachment.setFileName(filename);
			
			return result = mbpAttachment;
		} catch (MessagingException mex) {
			throw new LogonException(mex);
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
