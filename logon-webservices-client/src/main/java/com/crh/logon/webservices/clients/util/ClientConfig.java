/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.util;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrSubstitutor;

import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
public final class ClientConfig {
	
	public static final String CONFIG_PREFIX = "wsclient";
	public static final String CONFIG_POSTFIX_HOST_NAME = "host.name";
	public static final String CONFIG_POSTFIX_URL = "host.url";
	public static final String CONFIG_POSTFIX_USER = "user";
	public static final String CONFIG_POSTFIX_PASSWORD = "password";
	public static final String CONFIG_POSTFIX_CONNECT_TIMEOUT = "connect_timeout";
	public static final String CONFIG_POSTFIX_PROXY = "proxy";
	public static final String CONFIG_POSTFIX_NOCALL = "nocall";
	public static final String CONFIG_KEY_PROXY_NAME = "wsclient.proxy_host";
	public static final String CONFIG_KEY_PROXY_PORT = "wsclient.proxy_port";
	private static final String CONFIG_POSTFIX_TRANSFER_ENCODING_CHUNKED = "chunked.transfer.encoding";
		
	private final ConfigBean configBean;
    private final String url;
    private final String proxyName;
    private final String proxyPort;
    private final String user;
    private final String password;
    private Integer connectTimeout;
    private final boolean proxy;
    private final boolean noCall;
    private final boolean sendEmail;
    private final boolean chunked;
    private final boolean ignoreUnexpectedEofException;
    private final ConfigurationContext configurationContext;

	private final static class HostNameLookup extends StrLookup {

		private final String hostnameKey;
		private final String hostname;
		
		private HostNameLookup(String hostnameKey, String hostname) {
			this.hostnameKey = hostnameKey;
			this.hostname = hostname;
		}

		@Override
		public String lookup(String key) {
			if (StringUtils.equals(key, "name")) {
				if (hostname != null) {
					return hostname;
				}
				else {
					throw new LogonException(
							hostnameKey + " is not in tlgnr_config!",
							Reason.RES_ERROR_WSCLIENT_LOADINGPONT_UNKNOWN);
				}
			}
			else {
				return null;
			}
		}
		
		public static String replace(ConfigBean configBean, String configInfix, String serverName) {
			final String url = configBean.getProperty(ClientConfig.CONFIG_PREFIX + "." + configInfix + "." + ClientConfig.CONFIG_POSTFIX_URL);
			
			if (serverName == null) {
				return url;
			}
			
			final String hostnameKey = ClientConfig.CONFIG_PREFIX + "." + configInfix + "." + CONFIG_POSTFIX_HOST_NAME + "." + serverName;
			final String hostname = configBean.getProperty(hostnameKey);
		  	
			return new StrSubstitutor(new HostNameLookup(hostnameKey, hostname)).replace(url);
		}
	}

    public ClientConfig(ConfigBean configBean, String configLabel, boolean sendEmail, boolean ignoreUnexpectedEofException) throws AxisFault {    	
    	this(configBean, 
    			configLabel,
    			sendEmail, 
    			ignoreUnexpectedEofException,
    			ConfigurationContextFactory.createConfigurationContextFromFileSystem(null, null));
    }
    
    public ClientConfig(ConfigBean configBean, String configLabel, boolean sendEmail, boolean ignoreUnexpectedEofException, ConfigurationContext configurationContext) {
    	this(
    			configBean,
    			configBean.getProperty(CONFIG_PREFIX + "." + configLabel + "." + CONFIG_POSTFIX_URL),
    			configBean.getProperty(CONFIG_KEY_PROXY_NAME),
    			configBean.getProperty(CONFIG_KEY_PROXY_PORT),
    			configBean.getProperty(CONFIG_PREFIX + "." + configLabel + "." + CONFIG_POSTFIX_USER),
    			configBean.getProperty(CONFIG_PREFIX + "." + configLabel + "." + CONFIG_POSTFIX_PASSWORD),
    			Boolean.parseBoolean(configBean.getProperty(CONFIG_PREFIX + "." + configLabel + "." + CONFIG_POSTFIX_PROXY)),
    			Boolean.parseBoolean(configBean.getProperty(CONFIG_PREFIX + "." + configLabel + "." + CONFIG_POSTFIX_NOCALL)),
    			sendEmail,
    			Boolean.parseBoolean(configBean.getProperty(CONFIG_PREFIX + "." + configLabel + "." + CONFIG_POSTFIX_TRANSFER_ENCODING_CHUNKED)),
    			ignoreUnexpectedEofException,
    			configBean.getProperty(CONFIG_PREFIX + "." + configLabel + "." + CONFIG_POSTFIX_CONNECT_TIMEOUT),
    			configurationContext);
    	
    	System.out.println(this + " configLabel="+configLabel);    	
    }
    
	public ClientConfig (ConfigBean configBean, String configLabel, String serverName) throws AxisFault {
		this(
				configBean,
				HostNameLookup.replace(configBean, configLabel, serverName), 
				configBean.getProperty(CONFIG_KEY_PROXY_NAME),
				configBean.getProperty(ClientConfig.CONFIG_KEY_PROXY_PORT),
				configBean.getProperty(CONFIG_PREFIX + "." + configLabel + "." + CONFIG_POSTFIX_USER), 
				configBean.getProperty(CONFIG_PREFIX + "." + configLabel + "." + CONFIG_POSTFIX_PASSWORD), 
				Boolean.parseBoolean(configBean.getProperty(CONFIG_PREFIX + "." + configLabel + "." + CONFIG_POSTFIX_PROXY)),
				Boolean.parseBoolean(configBean.getProperty(CONFIG_PREFIX + "." + configLabel + "." + CONFIG_POSTFIX_NOCALL)),
				false,
				Boolean.parseBoolean(configBean.getProperty(CONFIG_PREFIX + "." + configLabel + "." + CONFIG_POSTFIX_TRANSFER_ENCODING_CHUNKED)),
				true,
    			configBean.getProperty(CONFIG_PREFIX + "." + configLabel + "." + CONFIG_POSTFIX_CONNECT_TIMEOUT),
				ConfigurationContextFactory.createConfigurationContextFromFileSystem(null, null));
	}

	
	private ClientConfig(ConfigBean configBean, String url, String proxyName, String proxyPort,
			String user, String password, boolean proxy, boolean noCall, boolean sendEmail, boolean chunked,
			boolean ignoreUnexpectedEofException, String connectTimeout, ConfigurationContext configurationContext) {
		this.configBean = configBean;
		this.url = url;
		this.proxyName = proxyName;
		this.proxyPort = proxyPort;
		this.user = user;
		this.password = password;
		this.proxy = proxy;
		this.noCall = noCall;
		this.sendEmail = sendEmail;
		this.chunked = chunked;
		this.ignoreUnexpectedEofException = ignoreUnexpectedEofException;
		if(connectTimeout != null) {
			try {
				this.connectTimeout = Integer.valueOf(connectTimeout);							
			} catch (NumberFormatException e) {}
		}
		this.configurationContext = configurationContext;		
	}

	public ConfigBean getConfigBean() {
		return configBean;
	}

	public String getUrl() {
		return url;
	}

	public String getProxyName() {
		return proxyName;
	}

	public String getProxyPort() {
		return proxyPort;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public boolean isProxy() {
		return proxy;
	}

	public boolean isNoCall() {
		return noCall;
	}

	public boolean isSendEmail() {
		return sendEmail;
	}

	public boolean isChunked() {
		return chunked;
	}

	public boolean isIgnoreUnexpectedEofException() {
		return ignoreUnexpectedEofException;
	}

	public Integer getConnectTimeout() {
		return connectTimeout;
	}
	
	public ConfigurationContext getConfigurationContext() {
		return configurationContext;
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		// builder.append("configBean", configBean);
		builder.append("url", url);
		builder.append("proxyName", proxyName);
		builder.append("proxyPort", proxyPort);
		builder.append("user", user);
		builder.append("password", "****");
		builder.append("proxy", proxy);
		builder.append("noCall", noCall);
		builder.append("sendEmail", sendEmail);
		builder.append("chunked", chunked);
		builder.append("ignoreUnexpectedEofException", ignoreUnexpectedEofException);
		builder.append("connectTimeout", connectTimeout);
		builder.append("configurationContext", configurationContext);
		return builder.toString();
	}
}
