/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
public class FtpClientFactory {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(FtpClientFactory.class);
	private static final String PROXY_TYPE_SOCKS = "SOCKS";
	private static final String PROXY_TYPE_USER_AT_HOST = "USER@HOST";
	private static final String PROXY_TYPE_HTTP_CONNECT = "HTTPCONNECT";
	
	private static final String FILE_TYPE_ASCII = "ascii";
	private static final String FILE_TYPE_BINARY = "binary";
	
	private static final String CFG_KEY_PROXY_TYPE = "proxy_type";
	private static final String CFG_KEY_PROXY_HOST = "proxy_host";
	private static final String CFG_KEY_PROXY_PORT = "proxy_port";
	private static final String CFG_KEY_FTP_URL = "url";
	private static final String CFG_KEY_REUSE_CONNECTION = "reuse_connection";
	private static final String CFG_KEY_FTP_USER = "user";
	private static final String CFG_KEY_FTP_PASSWORD = "password";
	private static final String CFG_KEY_FILE_TYPE = "file_type";
	private static final String CFG_KEY_KEEP_ALIVE_TIMEOUT = "keep_alive_timeout";
		
	private static final long POOL_WAIT_TIMEOUT = 1000*1800;
	
	private static ExecutorService executorService;
	private static KeepAliveManager keepAliveManager;
	
	// This pool always consists of only 1 connection. That should be sufficient for now. Reason to reuse this connection
	// is that connecting to the SBB FTP from Crh is extremely slow (>60sec). So a connection is kept open to transmit
	// new orders to SBB 'immediately'. This is relatively fast and does not happen that often and in parallel that more 
	// than 1 connection is needed. If more connections are need, use commons-pool or something similar.
	private static class FtpConnectionPool implements Serializable {
		private String ftpServiceName;
		private String proxyType;
		private String proxyHost;
		private int proxyPort;
		private URL url;
		private String user;
		private String password;
		private String fileType;
		private boolean reuseConnection;
		private long keepAliveTimeout; // Timeout in sec before sending a NOOP to keep the connection open. Only used when reuseConnection is true.
		private boolean connectionInUse; // Only set when reuseConnection is true.
		private long lastActivityTs; // Last time there was activity on this connection. Used to keep the connection alive.
		private transient FTPClient ftpClient; // Only set when reuseConnection is true.
		
		public FtpConnectionPool() {
			// Auto-generated constructor stub
		}
		public String getFtpServiceName() {
			return ftpServiceName;
		}
		public void setFtpServiceName(String ftpServiceName) {
			this.ftpServiceName = ftpServiceName;
		}
		public String getProxyType() {
			return proxyType;
		}
		public void setProxyType(String proxyType) {
			this.proxyType = proxyType;
		}
		public String getProxyHost() {
			return proxyHost;
		}
		public void setProxyHost(String proxyHost) {
			this.proxyHost = proxyHost;
		}
		public int getProxyPort() {
			return proxyPort;
		}
		public void setProxyPort(int proxyPort) {
			this.proxyPort = proxyPort;
		}
		public URL getUrl() {
			return url;
		}
		public void setUrl(URL url) {
			this.url = url;
		}
		public String getUser() {
			return user;
		}
		public void setUser(String user) {
			this.user = user;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public String getFileType() {
			return fileType;
		}
		public void setFileType(String fileType) {
			this.fileType = fileType;
		}
		public boolean isReuseConnection() {
			return reuseConnection;
		}
		public void setReuseConnection(boolean reuseConnection) {
			this.reuseConnection = reuseConnection;
		}
		public long getKeepAliveTimeout() {
			return keepAliveTimeout;
		}
		public void setKeepAliveTimeout(long keepAliveTimeout) {
			this.keepAliveTimeout = keepAliveTimeout;
		}
		public boolean isConnectionInUse() {
			return connectionInUse;
		}
		public void setConnectionInUse(boolean connectionInUse) {
			this.connectionInUse = connectionInUse;
		}
		public long getLastActivityTs() {
			return lastActivityTs;
		}
		public void setLastActivityTs(long lastActivityTs) {
			this.lastActivityTs = lastActivityTs;
		}
		public FTPClient getFtpClient() {
			return ftpClient;
		}
		public void setFtpClient(FTPClient ftpClient) {
			this.ftpClient = ftpClient;
		}		
		private void writeObject(ObjectOutputStream out) throws IOException {
			this.connectionInUse = false;
			doDisconnectFtp(ftpClient);
			out.defaultWriteObject(); 
		}
	}
	private static final Map<String, FtpConnectionPool> FTP_CONNECTION_POOLS = new HashMap<String, FtpConnectionPool>();
	private static class KeepAliveManager implements Runnable {
		private boolean doKeepAlive = true;
		
		public void setDoKeepAlive(boolean doKeepAlive) {
			this.doKeepAlive = doKeepAlive;
		}
		public boolean isDoKeepAlive() {
			return doKeepAlive;
		}	
		
		public void run() {
			while (doKeepAlive) {
				if (FTP_CONNECTION_POOLS != null) {
					for (String ftpServiceName: FTP_CONNECTION_POOLS.keySet()) {
						FtpConnectionPool pool = FTP_CONNECTION_POOLS.get(ftpServiceName);
						synchronized(pool) {
							if ( (!pool.isConnectionInUse()) && (pool.getKeepAliveTimeout() > 0) ) {
								FTPClient ftpClient = pool.getFtpClient();
								if ( (ftpClient != null) && (ftpClient.isConnected()) && 
										((System.currentTimeMillis() - pool.getLastActivityTs()) > (1000*pool.getKeepAliveTimeout())) ) {
									try {
										ftpClient.sendNoOp();
										int replyCode = ftpClient.getReplyCode();
										if (!FTPReply.isPositiveCompletion(replyCode)) {
											LOG.error("Noop failed for " + ftpServiceName + ", unable to keep connection alive");
											doDisconnectFtp(ftpClient);
										}
										else {
											LOG.info("Noop succeeded for " + ftpServiceName);
										}
									}
									catch (IOException e) {
										LOG.error("Noop failed for " + ftpServiceName + ", unable to keep connection alive", e);
										doDisconnectFtp(ftpClient);
									}
									pool.setLastActivityTs(System.currentTimeMillis());
								}
							}
						}
					}
				}
				try {
					Thread.sleep(1000);
				}
				catch (InterruptedException e) {
					LOG.error("KeepAliveThread interrupted", e);
					doKeepAlive = false;
				}
			}
		}
	}
	static {
		executorService = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "FtpKeepAliveThread");
			}
		});
		keepAliveManager = new KeepAliveManager();
		executorService.execute(keepAliveManager);
	}
	
	/**
	 * Loads the config settings into the pool object.
	 * @returns true if any of the config values were changed in the pool object. false otherwise.
	 */
	private static boolean loadConnectionConfig(FtpConnectionPool pool, ConfigBean configBean, String ftpServiceName) throws IOException {
		
		String strUrl = configBean.getProperty(ftpServiceName + "." + CFG_KEY_FTP_URL);
		if (strUrl == null) {
			
			throw new LogonException("Config property " + ftpServiceName + "." + CFG_KEY_FTP_URL + " not set", Reason.RES_ERROR_CONFIG_NOT_EXISTENT);
		}
		URL url = new URL(strUrl);
		String proxyType = configBean.getProperty(ftpServiceName + "." + CFG_KEY_PROXY_TYPE);
		String proxyHost = configBean.getProperty(ftpServiceName + "." + CFG_KEY_PROXY_HOST);
		
		String key = ftpServiceName + "." + CFG_KEY_PROXY_PORT;
		String propValue = configBean.getProperty(key);
		int proxyPort = propValue!=null?ConfigBean.toInt(key, propValue):1080;
		
		String user = configBean.getProperty(ftpServiceName + "." + CFG_KEY_FTP_USER);
		String password = configBean.getProperty(ftpServiceName + "." + CFG_KEY_FTP_PASSWORD);
		String fileType = configBean.getProperty(ftpServiceName + "." + CFG_KEY_FILE_TYPE);
		
		key = ftpServiceName + "." + CFG_KEY_REUSE_CONNECTION;
		propValue = configBean.getProperty(key);
		boolean reuseConnection = propValue!=null?ConfigBean.toBoolean(key, propValue):false;
		
		key = ftpServiceName + "." + CFG_KEY_KEEP_ALIVE_TIMEOUT;
		propValue = configBean.getProperty(key);
		int keepAliveTimeout = propValue!=null?ConfigBean.toInt(key, propValue):0;
		
		boolean result = false;
		if (pool.getUrl() == null) {
			if (url != null) {
				pool.setUrl(url);
				result = true;
			}
		}
		else if (!pool.getUrl().equals(url)) {
			pool.setUrl(url);
			result = true;
		}
		
		if (pool.getProxyType() == null) {
			if (proxyType != null) {
				pool.setProxyType(proxyType);
				result = true;
			}
		}
		else if (!pool.getProxyType().equals(proxyType)) {
			pool.setProxyType(proxyType);
			result = true;
		}
		
		if (pool.getProxyHost() == null) {
			if (proxyHost != null) {
				pool.setProxyHost(proxyHost);
				result = true;
			}
		}
		else if (!pool.getProxyHost().equals(proxyHost)) {
			pool.setProxyHost(proxyHost);
			result = true;
		}
		
		if (proxyPort != pool.getProxyPort()) {
			pool.setProxyPort(proxyPort);
			result = true;
		}
		
		if (pool.getUser() == null) {
			if (user != null) {
				pool.setUser(user);
				result = true;
			}
		}
		else if (!pool.getUser().equals(user)) {
			pool.setUser(user);
			result = true;
		}
		
		if (pool.getPassword() == null) {
			if (password != null) {
				pool.setPassword(password);
				result = true;
			}
		}
		else if (!pool.getPassword().equals(password)) {
			pool.setPassword(password);
			result = true;
		}
		
		if (pool.getFileType() == null) {
			if (fileType != null) {
				pool.setFileType(fileType);
				result = true;
			}
		}
		else if (!pool.getFileType().equals(fileType)) {
			pool.setFileType(fileType);
			result = true;
		}
		
		if (reuseConnection != pool.isReuseConnection()) {
			pool.setReuseConnection(reuseConnection);
			result = true;
		}
		
		if (keepAliveTimeout != pool.getKeepAliveTimeout()) {
			pool.setKeepAliveTimeout(keepAliveTimeout);
			result = true;
		}
		
		return result;
	}
	
	public static FTPClient createFtpClient(ConfigBean configBean, String ftpServiceName) throws IOException {
		
		FtpConnectionPool pool = null;
		
		// Find the right connection pool given the servicename (for example 'edifact_import')
		synchronized(FTP_CONNECTION_POOLS) {
			pool = FTP_CONNECTION_POOLS.get(ftpServiceName);
			if (pool == null) {
				pool = new FtpConnectionPool();
				pool.setFtpServiceName(ftpServiceName);
				FTP_CONNECTION_POOLS.put(ftpServiceName, pool);
			}
		}
		
		synchronized(pool) {
			if (pool.isReuseConnection()) {
				// If the pool allows reusing existing connections, wait for the connection to become available.
				if (pool.isConnectionInUse()) {
					try {
						LOG.info("FTP Connection is in use, waiting for it to be released");
						pool.wait(POOL_WAIT_TIMEOUT);
					}
					catch (InterruptedException e) {
						ExceptionUtil.errException(LOG, e, null);
					}
				}
				// The pool is available, now reload the config values
				if (loadConnectionConfig(pool, configBean, ftpServiceName)) {
					// The config was changed, disconnect so a new connection will be made with the new settings.
					doDisconnectFtp(pool.getFtpClient());
					pool.setFtpClient(null);
				}
			}
			else {
				loadConnectionConfig(pool, configBean, ftpServiceName);
				pool.setFtpClient(null);
			}
			pool.setConnectionInUse(true);
		}
		try {
			
			return connectFtpClient(pool);
		}
		catch (IOException e) {
			pool.setConnectionInUse(false);
			
			throw e;
		}
		catch (RuntimeException e) {
			pool.setConnectionInUse(false);
			
			throw e;
		}
	}

	private static FTPClient connectFtpClient(FtpConnectionPool pool) throws IOException {
		// [[
		FTPClient result = null;
		Object[][] params = {{"pool", pool}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(FtpClientFactory.class, "connectFtpClient", params);
		}
		// ]]
		try {
			FTPClient ftpClient = pool.getFtpClient();
			if (ftpClient != null) {
				try {
					if (ftpClient.isConnected()) {
						ftpClient.sendNoOp();
						int replyCode = ftpClient.getReplyCode();
						if (FTPReply.isPositiveCompletion(replyCode)) {
							LOG.info("Re-using existing ftp connection");
					
							return result = ftpClient;
						}
						LOG.error("Noop failed, unable to reuse existing ftp connection");
						doDisconnectFtp(ftpClient);
					}
				}
				catch (IOException e) {
					LOG.error("Error reusing connection", e);
				}
			}
			URL url = pool.getUrl();
			String proxyTypeDesc = pool.getProxyType();
			String proxyHost = pool.getProxyHost();
			int proxyPort = pool.getProxyPort();
			String user = pool.getUser();
			String password = pool.getPassword();
			String fileType = pool.getFileType();
			final String ftpServiceName = pool.getFtpServiceName();
			
			FTPProxyClient.Proxy proxy = null;
			if ( (!StringUtils.isEmpty(proxyTypeDesc)) && (!StringUtils.isEmpty(proxyHost)) ) {
				FTPProxyClient.Proxy.Type proxyType = null;
				if (PROXY_TYPE_SOCKS.equals(proxyTypeDesc)) {
					proxyType = FTPProxyClient.Proxy.Type.SOCKS;
				}
				else if (PROXY_TYPE_USER_AT_HOST.equals(proxyTypeDesc)) {
					proxyType = FTPProxyClient.Proxy.Type.USER_AT_HOST;
				}
				else if (PROXY_TYPE_HTTP_CONNECT.equals(proxyTypeDesc)) {
					proxyType = FTPProxyClient.Proxy.Type.HTTP_CONNECT;
				}
				else {
					
					throw new IllegalArgumentException("Invalid proxy type " + proxyTypeDesc + " specified");
				}
				
				proxy = new FTPProxyClient.Proxy(proxyType, new InetSocketAddress(proxyHost, proxyPort));
			}
						
			if (proxy != null) {
				ftpClient = new FTPProxyClient();
			}
			else {
				ftpClient = new FTPClient();
			}
			if (LOG.isDebugEnabled()) {
				ftpClient.addProtocolCommandListener(new ProtocolCommandListener() {
			
					@SuppressWarnings("synthetic-access")
					public void protocolCommandSent(ProtocolCommandEvent event) {
						
						if ("PASS".equals(event.getCommand())) {
							LOG.debug("ftp." + ftpServiceName + "> PASS ****"); 
						}
						else {
							LOG.debug("ftp." + ftpServiceName + "> " + event.getMessage());
						}
					}
			
					@SuppressWarnings("synthetic-access")
					public void protocolReplyReceived(ProtocolCommandEvent event) {
						
						LOG.debug("ftp." + ftpServiceName + "< " + event.getMessage());
					}
				});
			}
			try {
				if (proxy != null) {
					LOG.debug("connecting to proxy " + proxyHost + ":" + proxyPort);
					((FTPProxyClient)ftpClient).connect(proxy, url.getHost(), url.getPort()==-1?url.getDefaultPort():url.getPort());
				}
				else {
					ftpClient.connect(url.getHost(), url.getPort()==-1?url.getDefaultPort():url.getPort());
				}
				int replyCode = ftpClient.getReplyCode();
				if (!FTPReply.isPositiveCompletion(replyCode)) {
					
					throw new IOException("FTP server refused connection.");
				}
				if (!ftpClient.login(user, password)) {
					
					throw new IOException("Login denied");
				}
				if (!StringUtils.isEmpty(url.getPath())) { 
					ftpClient.changeWorkingDirectory(url.getPath());
					replyCode = ftpClient.getReplyCode();
					if (!FTPReply.isPositiveCompletion(replyCode)) {
					
						throw new IOException("Error changing to directory " + url.getPath() + ". " +  ftpClient.getReplyString());
					}
				}
				if (!StringUtils.isEmpty(fileType)) {
					if (fileType.equals(FILE_TYPE_ASCII)) {
						if (!ftpClient.setFileType(FTP.ASCII_FILE_TYPE)) {
							
							throw new LogonException("Error setting file type " + fileType, Reason.RES_ERROR_UNSPECIFIED);
						}
					}
					else if (fileType.equals(FILE_TYPE_BINARY)) {
						if (!ftpClient.setFileType(FTP.BINARY_FILE_TYPE)) {

							throw new LogonException("Error setting file type " + fileType, Reason.RES_ERROR_UNSPECIFIED);
						}
					}
					else {
						
						throw new LogonException("Unknown file type " + fileType, Reason.RES_ERROR_UNSPECIFIED);
					}
				}
				ftpClient.enterLocalPassiveMode();
			}
			catch (IOException e) {
				doDisconnectFtp(ftpClient);
				
				throw e;
			}
			synchronized(pool) {
				if (pool.isReuseConnection()) {
					pool.setFtpClient(ftpClient);
				}
			}
			
			return result = ftpClient;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	static void doDisconnectFtp(FTPClient ftpClient) {
		
		if ( (ftpClient != null) && (ftpClient.isConnected()) ) {
			boolean disconnected = true;
			try {
				ftpClient.logout();
			} catch (Exception e) {
				disconnected = false;
				LOG.error("Error logging out from FTP: " + e.getMessage());
			}
			try {
				ftpClient.disconnect();
			} catch (Exception e) {
				disconnected = false;
				LOG.error("Error disconnecting from FTP: " + e.getMessage());
			}
			if (disconnected) {
				LOG.info("Disconnected from FTP");
			}
		}
	}
	
	public static void disconnectFtp(FTPClient ftpClient) {
		// [[
		Object[][] params = {{"ftpClient", ftpClient}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(FtpClientFactory.class, "disconnectFtp", params);
		}
		// ]]
		try {
			
			for (FtpConnectionPool pool:FTP_CONNECTION_POOLS.values()) {
				if (pool.getFtpClient() == ftpClient) {
					synchronized(pool) {
						pool.setConnectionInUse(false);
						if (!pool.isReuseConnection()) {
							if (pool.getFtpClient().isConnected()) {
								doDisconnectFtp(pool.getFtpClient());
							}
							pool.setFtpClient(null);
						}
						else {
							LOG.info("Not disconnecting from FTP, connection will be reused");
						}
						pool.setLastActivityTs(System.currentTimeMillis());
						pool.notifyAll();
					}
					
					return;
				}
			}
			doDisconnectFtp(ftpClient);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public static void shutdown() {
		if (executorService != null) {
			synchronized (FtpClientFactory.class) {
				if (executorService!= null && !executorService.isTerminated() && !executorService.isShutdown()) {
					keepAliveManager.setDoKeepAlive(false);
					executorService.shutdownNow();

					closeConnections();

					keepAliveManager = null;
					executorService = null;
				}
			}
		}
	}
	
	private static void closeConnections() {
		if (FTP_CONNECTION_POOLS != null) {
			for (String ftpServiceName: FTP_CONNECTION_POOLS.keySet()) {
				FtpConnectionPool pool = FTP_CONNECTION_POOLS.get(ftpServiceName);
				synchronized(pool) {
					FTPClient ftpClient = pool.getFtpClient();
					if (ftpClient != null && ftpClient.isConnected()) {
						try {
							ftpClient.sendNoOp();
							int replyCode = ftpClient.getReplyCode();
							if (!FTPReply.isPositiveCompletion(replyCode)) {
								LOG.error("Noop failed for " + ftpServiceName + ", unable to keep connection alive");
								doDisconnectFtp(ftpClient);
							}
							else {
								LOG.info("Noop succeeded for " + ftpServiceName);
							}
						}
						catch (IOException e) {
							LOG.error("Noop failed for " + ftpServiceName + ", unable to keep connection alive", e);
							doDisconnectFtp(ftpClient);
						}
					}
				}
			}
		}		
	}
	
}
