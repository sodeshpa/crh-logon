/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.HostKey;
import com.jcraft.jsch.HostKeyRepository;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Logger;
import com.jcraft.jsch.Proxy;
import com.jcraft.jsch.ProxyHTTP;
import com.jcraft.jsch.ProxySOCKS5;
import com.jcraft.jsch.Session;

/**
 * @author AdNovum Informatik AG
 */
public final class SFtpClientFactory {

	private static final class Jsch2JcanLogger implements Logger {

		private final static Map<Integer, String> logLevelNames = new HashMap<Integer, String>();
		
		static {
			logLevelNames.put(Logger.DEBUG, "DEBUG");
			logLevelNames.put(Logger.INFO, "INFO");
			logLevelNames.put(Logger.WARN, "WARN");
			logLevelNames.put(Logger.ERROR, "ERROR");
			logLevelNames.put(Logger.FATAL, "FATAL");
		}
		
		public boolean isEnabled(int level) {
			return true;
		}

		public void log(int level, String message) {				
			if (LOG.isDebugEnabled()) {		
				final String logLevelName;
				if (logLevelNames.containsKey(level)) {
					logLevelName = logLevelNames.get(level);
				}
				else {
					logLevelName = "UNKNOWN";
				}
				LOG.debug("[JSch] " + logLevelName + ": " + message);
			}
		}
	}
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SFtpClientFactory.class);
	
	private static final String PROXY_TYPE_SOCKS = "SOCKS";
	private static final String PROXY_TYPE_USER_AT_HOST = "USER@HOST";
	private static final String PROXY_TYPE_HTTP_CONNECT = "HTTPCONNECT";

	private static final String CFG_PREFIX_SFTP = "sftpclient";

	private static final String CFG_KEY_SSH_KEY_PUBLIC = "ssh.key.public";
	private static final String CFG_KEY_SSH_KEY_PRIVATE = "ssh.key.private";
	private static final String CFG_KEY_SSH_PASSPHRASE = "ssh.key.passphrase.utf8";
	private static final String CFG_KEY_SSH_KNOWN_HOSTS_FILE = "ssh.known.hosts.file";
	
	private static final String CFG_KEY_USER = "user";
	private static final String CFG_KEY_PASSWORD = "password";
	
	private static final String CFG_KEY_HOST_URL = "host.url";
	private static final String CFG_KEY_HOST_PORT = "host.port";
	
	private static final String CFG_KEY_PROXY_HOST = "proxy.host";
	private static final String CFG_KEY_PROXY_PORT = "proxy.port";
	private static final String CFG_KEY_PROXY_TYPE = "proxy.type";
	
	static {
		JSch.setLogger(new Jsch2JcanLogger());
	}
	
	private static JSch getJSch(ConfigBean configBean, String sftpServiceName) {
		final String configPrefix = CFG_PREFIX_SFTP + '.' + sftpServiceName + '.';

		
		final byte[] passphaseBytes;
		final String passphase = configBean.getProperty(configPrefix + CFG_KEY_SSH_PASSPHRASE);
		if (StringUtils.isNotEmpty(passphase)) {
			try {
				passphaseBytes = passphase.getBytes("UTF-8");
			}
			catch (UnsupportedEncodingException e) {
				throw new LogonException("Unable to get passphase bytes, specified encoding (UTF-8) is not supported!", e);
			}
		}
		else {
			if (LOG.isWarnEnabled()) {
				LOG.warn(String.format(
						"No passphase specified for service '%s' (config key '%s' is empty or non-existent).",
						sftpServiceName, 
						configPrefix + CFG_KEY_SSH_PASSPHRASE));
			}
			passphaseBytes = null;
		}
		
		final JSch jsch = new JSch();

		final String known_hosts = configBean.getProperty(configPrefix + CFG_KEY_SSH_KNOWN_HOSTS_FILE);
		if (StringUtils.isNotEmpty(known_hosts)) {
	        try {
	        	jsch.setKnownHosts(known_hosts);
			}
			catch (JSchException e) {
				throw new LogonException(String.format(
						"Unable to set known_hosts file '%s' for Jsch (for service '%s')!", 
						sftpServiceName,
						known_hosts), e);
			}
		}
		else {
			if (LOG.isWarnEnabled()) {
				LOG.warn(String.format(
						"No known_hosts file specified for service '%s' (config key '%s' is empty or non-existent).",
						sftpServiceName, 
						configPrefix + CFG_KEY_SSH_KNOWN_HOSTS_FILE));
			}
		}
        
        try {
    		final String privKeyLoc = configBean.getProperty(configPrefix + CFG_KEY_SSH_KEY_PRIVATE);
    		if (StringUtils.isEmpty(privKeyLoc)) {
        		throw new LogonException(String.format(
        				"No private key file specified for service '%s' (config key '%s')!",
						sftpServiceName,
						configPrefix + CFG_KEY_SSH_KEY_PRIVATE),
        				Reason.RES_ERROR_CONFIG_NOT_EXISTENT);
        	}
        	
    		final String pubKeyLoc = configBean.getProperty(configPrefix + CFG_KEY_SSH_KEY_PUBLIC);    		
    		if (StringUtils.isEmpty(pubKeyLoc)) {
        		throw new LogonException(String.format(
        				"No public key file specified for service '%s' (config key '%s')!",
						sftpServiceName, 
						configPrefix + CFG_KEY_SSH_KEY_PUBLIC),
        				Reason.RES_ERROR_CONFIG_NOT_EXISTENT);        		
        	}
			jsch.addIdentity(privKeyLoc, pubKeyLoc, passphaseBytes);
		}
		catch (JSchException e) {
			throw new LogonException("Failed to add required identity for initiating an SSH2 connection!", e);
		}
        
        return jsch;
	}
	
	private static Session getSession(ConfigBean configBean, String sftpServiceName) {
		final JSch jsch = getJSch(configBean, sftpServiceName);

		try {
        	final String configPrefix = CFG_PREFIX_SFTP + '.' + sftpServiceName + '.';
			
			String hostUrl = configBean.getProperty(configPrefix + CFG_KEY_HOST_URL);
			String hostUrlPortStr = configBean.getProperty(configPrefix + CFG_KEY_HOST_PORT);
			int hostUrlPort = hostUrlPortStr!=null ? ConfigBean.toInt(configPrefix + CFG_KEY_HOST_PORT, hostUrlPortStr) : 22;
	
			String user = configBean.getProperty(configPrefix + CFG_KEY_USER);
			final String password = configBean.getProperty(configPrefix + CFG_KEY_PASSWORD);
							
			Proxy proxy = null;
			final String proxyType = configBean.getProperty(configPrefix + CFG_KEY_PROXY_TYPE);
			if (proxyType != null) {
				final String proxyHost = configBean.getProperty(configPrefix + CFG_KEY_PROXY_HOST);
				final String proxyPortStr = configBean.getProperty(configPrefix + CFG_KEY_PROXY_PORT);
				final int proxyPort = ConfigBean.toInt(configPrefix + CFG_KEY_PROXY_PORT, proxyPortStr);
				
				if (PROXY_TYPE_SOCKS.equals(proxyType)) {
					proxy = new ProxySOCKS5(proxyHost, proxyPort);
				}
				else if (PROXY_TYPE_USER_AT_HOST.equals(proxyType)) {
					user = user + '@' + hostUrl;
					hostUrl = proxyPortStr;
					hostUrlPort = proxyPort;
				}
				else if (PROXY_TYPE_HTTP_CONNECT.equals(proxyType)) {
					proxy = new ProxyHTTP(proxyHost, proxyPort);
				}
				else {
					throw new IllegalArgumentException(String.format(
							"Invalid proxy type '%s' specified (config key '%s')!",
							proxyType,
							configPrefix + CFG_KEY_PROXY_HOST));
				}
			}
		
			final HostKeyRepository hostKeyRepository = jsch.getHostKeyRepository();
			final boolean noHostKeys;
			if (hostKeyRepository != null) {
				final HostKey[] hostKeys = hostKeyRepository.getHostKey();
				noHostKeys = ((hostKeys == null) || (hostKeys.length == 0));
			}
			else {
				noHostKeys = true;
			}
			
			final Session session = jsch.getSession(user, hostUrl, hostUrlPort);
			if (noHostKeys) {
				if (LOG.isWarnEnabled()) {
					LOG.warn(String.format(
							"Host key repository is empty for service '%s', setting 'StrictHostKeyChecking' to 'no'.", 
							sftpServiceName));
				}
		        session.setConfig("StrictHostKeyChecking", "no");
			}
			
	        session.setPassword(password);
	        if (proxy != null) {
	        	session.setProxy(proxy);
	        }
	        
	        session.connect();
	        
	        return session;
		}
		catch (Exception e) {
			throw new LogonException(String.format(
					"SSH2 session connection attempt failed (for service '%s')!",
					sftpServiceName), e);				
		}
	}
	
	public static ChannelSftp connectSftp(ConfigBean configBean, String sftpServiceName) throws LogonException {
		final Session session = getSession(configBean, sftpServiceName);
		
		try {			
	        final Channel channel = session.openChannel("sftp");
	        channel.connect();	
	        
	        if (LOG.isInfoEnabled()) {
	        	LOG.info(String.format(
	        			"Connected to SFTP (service '%s').",
	        			sftpServiceName));
	        }
	        
	        return (ChannelSftp) channel;        
		}
		catch (Exception e) {
			if (session != null) {
				if (session.isConnected()) {
					session.disconnect();
				}
			}
			throw new LogonException(String.format(
					"SFTP channel connection attempt failed (for service '%s')!",
					sftpServiceName), e);				
		}
	}

	public static void disconnectSftp(ChannelSftp channelSftp) {
		if ((channelSftp != null) && (channelSftp.isConnected())) {			
			try {
				final Session session = channelSftp.getSession();
				channelSftp.exit();	
	            session.disconnect();
	            if (LOG.isInfoEnabled()) {
	            	LOG.info("Disconnected from SFTP.");
	            }
			} catch (Exception e) {
				LOG.error("Error disconnecting from SFTP: " + e.getMessage(), e);
			}
		}
	}
}
