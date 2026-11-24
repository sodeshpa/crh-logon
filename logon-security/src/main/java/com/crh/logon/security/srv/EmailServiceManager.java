/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.security.srv;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.security.auth.login.LoginException;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.dao.to.ConfigLoginModuleValue;
import com.crh.logon.persistence.dao.util.DAOException;

/**
 * @author AdNovum Informatik AG
 */
public class EmailServiceManager extends ServiceManagerBase {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(EmailServiceManager.class);
    
	private static final String CFG_MAILBCC = "mail.bcc";
    private static final String CFG_MAILENCODING = "mail.encoding";
    private static final String CFG_MAILFROM = "mail.from";
    private static final String CFG_MAILSMTPHOST = "mail.smtp.host";
    private static final String CFG_MAILSMTPPORT = "mail.smtp.port";
    private static final String CFG_MAILTESTENABLED = "mail.test.enabled";
    private static final String CFG_MAILTESTTO = "mail.test.to";
    
    private String instance;
    private final RefDataServiceManager refDataSrv;

	/**
     * Constructor. Visible only in the package.
     * NOTE: No public constructor for this manager
     * 
     * @param conn
     * 			A connection (session) with a specific database. (Cannot be null).
     */
    protected EmailServiceManager(Connection conn, String instance, RefDataServiceManager refDataSrv) {
    	super(conn);
		// [[
		Object[][] params = {{"conn", conn}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "EmailServiceManager", params);
		}
		// ]]
		try {
			this.instance = instance;
			this.refDataSrv = refDataSrv;
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
     * Sends a plain text message with the given parameters.
     * 
     * @param recipient
     * @param subject
     * @param message
     * @throws DAOException
     * @throws LoginException
     */
	public void send(String recipient, String subject, String message)
			throws DAOException, LoginException {
		
		String[] toArray = null;
		if (recipient != null) {
			toArray = new String[1];
			toArray[0] = recipient;
		}
		
		send(toArray, null, subject, message);
		
	}

	/**
	* Sends a plain text message with the given parameters
	* @param recipient
	* @param subject
	* @param message
	*/
	private void send(String[] to, String[] cc, String subject, String message) throws DAOException, LoginException {
		// [[
		Object[][] params = { { "to", to }, { "cc", cc }, { "subject", subject },
				{ "message", message } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(EmailServiceManager.class, "send", params);
		}
		// ]]
		try {

			Properties properties = getMailProperties();
			Session session = Session.getInstance(properties, null);

			try {
				MimeMessage msg = createPlainTextMimeMessage(session, to, cc, subject, message);
				Transport.send(msg);
			}
			catch (MessagingException mex) {
				String errroMsg = "An MessagingException caught!";
				LOG.error(errroMsg, mex);
				throw new LoginException(errroMsg);
			}
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
	 * Create a Properties object containing the email host/port and from properties.
	 * @param config TODO
	 * @param defaultMailSetting in case 
	 * @return
	 */
	private Properties getMailProperties() throws DAOException, LoginException {		
		// [[
		Properties result = null;
		Object[][] params = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(EmailServiceManager.class, "getMailProperties", params);
		}
		// ]]
		try {
			Properties properties = new Properties();
			String mailSmtpHost = getConfigValue(CFG_MAILSMTPHOST);
			if (mailSmtpHost != null) {
				properties.setProperty("mail.smtp.host", mailSmtpHost);
			}
			properties.setProperty("mail.smtp.port", "" + getConfigValue(CFG_MAILSMTPPORT));
			String mailFrom = getConfigValue(CFG_MAILFROM);
			if (mailFrom != null) {
				properties.setProperty("mail.from", mailFrom);
			}
			boolean mailTestEnabled = new Boolean(getConfigValue(CFG_MAILTESTENABLED));
			if (mailTestEnabled && LOG.isDebugEnabled() ) {
				properties.setProperty("mail.debug", "true");
			}
			
			return result = properties;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	private String getConfigValue(String key) throws DAOException, LoginException {
		// [[
		String result = null;
		Object[][] params = {{"key", key}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(EmailServiceManager.class, "getConfigValue", params);
		}
		// ]]
		try {
			ConfigLoginModuleValue configVal = getRefDataSrv().getConfigByKeyAndInstance(key, instance);
			if (configVal == null) {
				String errorMsg = "Couldn't load config value found with key: '" + key + "' for the instance:'" + instance + "'!";
				LOG.error(errorMsg);
				//throw new LoginException(errorMsg);
				return null;
			}
			return result = configVal.getValue();
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
	 * Creates a basic mime message with the given paramters set.
	 * @param session
	 * @param to
	 * @param subject
	 * @param message
	 * @return
	 * @throws MessagingException
	 */
	private MimeMessage createPlainTextMimeMessage(Session session, String[] to,
			String[] cc, String subject, String message) throws DAOException, LoginException, MessagingException {
		// [[
		MimeMessage result = null;
		Object[][] params = {{"session", session},
							{"to", to},
							{"cc", cc},
							{"subject", subject},
							{"message", message}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createPlainTextMimeMessage", params);
		}
		// ]]
		try {
			
			MimeMessage msg = createEmptyMimeMessage(session, to, cc, subject, message);
			String mailEncoding = getConfigValue(CFG_MAILENCODING);
			msg.setText(getMessageText(to, cc, null, message), mailEncoding);
			
			return result = msg;
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
	 * Creates an empty MimeMessage with the given parameters set.
	 * @param session
	 * @param to
	 * @param subject
	 * @param message
	 * @return
	 * @throws MessagingException
	 */
	private MimeMessage createEmptyMimeMessage(Session session, String[] to, String[] cc,
			String subject, String message) throws DAOException, LoginException, MessagingException {
		// [[
		MimeMessage result = null;
		Object[][] params = {{"session", session},
							{"to", to},
							{"cc", cc},
							{"subject", subject},
							{"message", message}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createEmptyMimeMessage", params);
		}
		// ]]
		try {
			
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom();
			
			String[] actualTo = getActualTo(to);
			if (actualTo != null) {
				for (String s: actualTo) {
					msg.addRecipients(Message.RecipientType.TO, s);
				}
			}
			
			String[] actualCc = getActualCc(cc);
			if (actualCc != null) {
				for (String s: actualCc) {
					msg.addRecipients(Message.RecipientType.CC, s);				
				}
			}
			
			String bcc = getConfigValue(CFG_MAILBCC);
			if (bcc != null) {
				msg.setRecipients(Message.RecipientType.BCC, bcc);
			}
			
			String mailEncoding = getConfigValue(CFG_MAILENCODING);
			
			msg.setSubject(getSubject(subject), mailEncoding);
			msg.setSentDate(new Date());
			
			return result = msg;
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
	 * Determines the recipient of the email, based on whether test mode is enabled or not.
	 * @param recipient
	 * @return
	 */
	private String[] getActualTo(String[] recipients) throws DAOException, LoginException {
		// [[
		String[] result = null;
		Object[][] params = {{"recipients", recipients}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getActualTo", params);
		}
		// ]]
		try {
			
			boolean mailTestEnabled = new Boolean(getConfigValue(CFG_MAILTESTENABLED));
			
			if (mailTestEnabled) {
				String[] addresses = null;
				String mailTestTo = getConfigValue(CFG_MAILTESTTO);
				if (mailTestTo != null) {
					addresses = new String[1];
					addresses[0] = mailTestTo;
				}
				
				return result = addresses;  
			}
			
			return result = recipients;
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
	 * Determines the recipient of the email, based on whether test mode is enabled or not.
	 * @param recipient
	 * @return
	 */
	private String[] getActualCc(String[] recipients) throws DAOException, LoginException {
		// [[
		String[] result = null;
		Object[][] params = {{"recipients", recipients}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getActualCc", params);
		}
		// ]]
		try {
			
			boolean mailTestEnabled = new Boolean(getConfigValue(CFG_MAILTESTENABLED));
			
			if (mailTestEnabled) {
				
				return result = null;
			}
			
			return result = recipients;
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
	 * Formats the message subject text, based on whether test mode is enabled or not.
	 * @param subject
	 * @return
	 */
	private String getSubject(String subject) throws DAOException, LoginException {
		// [[
		String result = null;
		Object[][] params = {{"subject", subject}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getSubject", params);
		}
		// ]]
		try {
			boolean mailTestEnabled = new Boolean(getConfigValue(CFG_MAILTESTENABLED));
			if (mailTestEnabled) {
				
				return result = subject + " *LOGON TEST-MAIL*";  
			}
			
			return result = subject;
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
	 * Formats the message body text, based on whether test mode is enabled or not.
	 * @param recipient
	 * @param message
	 * @return
	 */
	private String getMessageText(String[] to, String[] cc, String[] bcc, String messageText) throws DAOException, LoginException {
		// [[
		String result = null;
		Object[][] params = {{"to", to},
							{"cc", cc},
							{"bcc", bcc},
							{"messageText", messageText}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getMessageText", params);
		}
		// ]]
		try {
			boolean mailTestEnabled = new Boolean(getConfigValue(CFG_MAILTESTENABLED));
			if (mailTestEnabled) {
				StringBuilder sb = new StringBuilder("*LOGON TEST-MAIL*\nOriginal recipients: ");
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
			
				
				return result = sb.toString() + "\n\n" + messageText;  
			}
			
			return result = messageText;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
    
    public RefDataServiceManager getRefDataSrv() {
		return refDataSrv;
	}
}
