/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.net.SocketFactory;

import org.apache.commons.net.SocketClient;
import org.apache.commons.net.ftp.FTPClient;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class FTPProxyClient extends FTPClient {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(FTPProxyClient.class);
	
	private static final String HTTP_CHARSET_ENCODING = "ISO-8859-1";
	private static final String HTTP_OK = "200";
	private static final int DEFAULT_DATA_TIMEOUT_MS = 300000;
	private InetSocketAddress host;
	public static class Proxy {
		public static enum Type {
			SOCKS,
			USER_AT_HOST,
			HTTP_CONNECT;
		}
		private Type type;
		private SocketAddress address;
		
		public Proxy(Type type, SocketAddress address) {
			
			this.type = type;
			this.address = address;
		}
		
		public Type type() {
			
			return type;
		}
		
		public SocketAddress address() {
			
			return address;
		}
	}	
	private Proxy proxy;
	
    public void connect(Proxy proxy, InetAddress host, int port)
    throws SocketException, IOException
    {
    	
    	connect(proxy, new InetSocketAddress(host, port), null);
    }

    public void connect(Proxy proxy, String hostname, int port)
    throws SocketException, IOException
    {
        
    	connect(proxy, new InetSocketAddress(hostname, port), null);
    }

    public void connect(Proxy proxy, String hostname, int port,
                        InetAddress localAddr, int localPort)
    throws SocketException, IOException
    {
        
    	connect(proxy, new InetSocketAddress(hostname, port), new InetSocketAddress(localAddr, localPort));
    }
    
    public void connect(Proxy proxy, InetAddress host, int port,
            InetAddress localAddr, int localPort)
    throws SocketException, IOException
	{
    	
    	connect(proxy, new InetSocketAddress(host, port), new InetSocketAddress(localAddr, localPort));
	}
    
    
    /**
     * Sends a HTTP CONNECT command to the socket and processes the HTTP response.
     * @param socket
     * @param host
     * @throws IOException if the server did not respond with 200.
     */
    public static void doHttpConnect(Socket socket, InetSocketAddress host) throws IOException {
    	
    	BufferedWriter controlOutput = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), HTTP_CHARSET_ENCODING));
		String httpConnectString = "CONNECT " + host.getHostName() + ":" + host.getPort() + " HTTP/1.1" + SocketClient.NETASCII_EOL + 
			"Host: " +  host.getHostName() + ":" + host.getPort() + SocketClient.NETASCII_EOL + SocketClient.NETASCII_EOL;
		LOG.debug("HTTP_CONNECT: " + httpConnectString);
		controlOutput.write(httpConnectString);
		controlOutput.flush();
		
		InputStream in = socket.getInputStream();
		StringBuilder sb = new StringBuilder();
		String statusLine = null;
		String headers = null;
				
		do {
			int n = in.read();
			if (n < 0) {
				LOG.debug("Data received: " + sb.toString());
				
				throw new IOException("Invalid HTTP response received from proxy");
			}
			sb.append((char)n);
			String s = sb.toString();
			if (statusLine == null) {
				if (s.endsWith(SocketClient.NETASCII_EOL)) {
					statusLine = s.substring(0, s.length()-SocketClient.NETASCII_EOL.length());
				}
			}
			else if (s.endsWith(SocketClient.NETASCII_EOL + SocketClient.NETASCII_EOL)) {
				headers = s.substring((statusLine.length()-1) + SocketClient.NETASCII_EOL.length(), s.length()-SocketClient.NETASCII_EOL.length());
			}
		} while (headers == null);
		LOG.debug("Data received: " + sb.toString());
		String[] parts = statusLine.split("\\s");
		if ( (parts.length < 3) || (!parts[1].equals(HTTP_OK)) ) {
		
			throw new IOException("HTTP proxy returned error " + statusLine);
		}
    }
    
    
    public void connect(final Proxy proxy, final InetSocketAddress host, InetSocketAddress localAddr) throws SocketException, IOException {
    	
    	if ( (proxy != null) && ( (proxy.type() == Proxy.Type.SOCKS) || (proxy.type == Proxy.Type.HTTP_CONNECT)) ) {
    		setSocketFactory(new SocketFactory() {
				@Override
				public Socket createSocket() throws IOException {
					
					if (proxy.type() == Proxy.Type.SOCKS) {
						LOG.debug("Creating socket to SOCKS proxy " + proxy.address().toString());
						java.net.Proxy javaProxy = new java.net.Proxy(java.net.Proxy.Type.SOCKS, proxy.address());
						
						return new Socket(javaProxy);
					}
					else {
						
						return new Socket();
					}
				}

				@Override
				public Socket createSocket(String host, int port)
						throws IOException, UnknownHostException {
					
					return createSocket(host != null ? new InetSocketAddress(host, port) :
						  new InetSocketAddress(InetAddress.getByName(null), port),
						  (SocketAddress) null);
				}

				@Override
				public Socket createSocket(InetAddress address, int port)
						throws IOException {
					
					return createSocket(address != null ? new InetSocketAddress(address, port) : null,
							(SocketAddress)null);
				}

				@Override
				public Socket createSocket(String host, int port,
                        InetAddress localAddr, int localPort) 
				throws IOException, UnknownHostException {
					
					return createSocket(host != null ? new InetSocketAddress(host, port) :
						new InetSocketAddress(InetAddress.getByName(null), port),
						new InetSocketAddress(localAddr, localPort));
				}

				@Override
				public Socket createSocket(InetAddress address, int port,
                        InetAddress localAddr, int localPort) 
				throws IOException {
					
					return createSocket(address != null ? new InetSocketAddress(address, port) : null, 
							new InetSocketAddress(localAddr, localPort));
				}
				
				private Socket createSocket(SocketAddress address, SocketAddress localAddr) throws IOException {
					
					Socket socket = createSocket();
					if (localAddr != null) {
						socket.bind(localAddr);
					}
					if ( (proxy != null) && 
			    			(proxy.type() == Proxy.Type.USER_AT_HOST) ||
			    			(proxy.type() == Proxy.Type.HTTP_CONNECT)) {
						
						LOG.debug("Connecting socket to proxy " + proxy.address());
						socket.connect(proxy.address());
						if (proxy.type() == Proxy.Type.HTTP_CONNECT) {
							doHttpConnect(socket, (InetSocketAddress)address);
						}
					}
					else {
						socket.connect(address);
					}
					
					return socket;
				}
			});
    	}
    	
    	_socket_ = _socketFactory_.createSocket();
    	if (localAddr != null) {
    		_socket_.bind(localAddr);
    	}
    	this.proxy = proxy;
    	this.host = host;
    	if ( (proxy != null) && 
    			(proxy.type() == Proxy.Type.USER_AT_HOST) ||
    			(proxy.type() == Proxy.Type.HTTP_CONNECT)) {

    		_socket_.connect(proxy.address(), connectTimeout);
    	}
    	else {
    		_socket_.connect(host, connectTimeout);
    	}
    	if ( (proxy != null) && (proxy.type() == Proxy.Type.HTTP_CONNECT) ) {
    		doHttpConnect(_socket_, host);
    	}
    	setDataTimeout(DEFAULT_DATA_TIMEOUT_MS);
   		_connectAction_();
    }
    
    
    public void disconnect() throws IOException {
    	
    	this.host = null;
    	this.proxy = null;
    	super.disconnect();
    }
    
    
    public boolean login(String username, String password) throws IOException {    	
    	
    	if ( (proxy != null) && (proxy.type() == Proxy.Type.USER_AT_HOST) ) {
    		username = username + "@" + host.getHostName();
    	}
    	
    	return super.login(username, password);
    }
    
    
    public boolean login(String username, String password, String account) throws IOException {
    	
    	if ( (proxy != null) && (proxy.type() == Proxy.Type.USER_AT_HOST) ) {
    		username = username + "@" + host.getHostName();
    	}
    	
    	return super.login(username, password, account);
    }
}