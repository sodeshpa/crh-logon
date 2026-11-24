/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.init.web.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.ejb.EJBException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.crh.logon.business.services.interfaces.ConfigService;
import com.crh.logon.business.services.utils.ConfigReloadResult;
import com.crh.logon.utils.AuthUtils;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.LogonProperties;
import com.crh.logon.utils.Reason;
import com.crh.logon.web.listeners.SpringBeanListener;
import com.crh.logon.web.servlets.LogonServlet;
import com.crh.logon.web.utils.Authorization;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * Simply shows or tests the version of the deployed application
 * 
 * @author AdNovum Informatik AG
 */
public class LogonVersionServlet extends LogonServlet {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LogonVersionServlet.class);
	
	private static final String HTML_DOCTYPE = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">";
	
	private ConfigService configService;
	
	@Override
	public void init() throws ServletException {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
			super.init();
			configService = getServiceLocator().fetchConfigService();
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
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		// [[
		Object[][] params = {{"req", req},
							{"resp", resp}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "doGet", params);
		}
		// ]]
		final PrintWriter pw = resp.getWriter();
		try {
			resp.setContentType("text/html");
			pw.println(HTML_DOCTYPE);
			pw.println("<html><head>");
			pw.println("<style type=\"text/css\">");
			pw.println(".ok { color: green; }");
			pw.println(".nok_ok { color: red; }");
			pw.println("</style>");
			pw.println("</head><body>");

			Authorization.authorize(Authorization.TEST_VERSIONCHECK);
			
			// make sure that we store the 'local' logonproperties into the ConfigBean
			final LogonProperties logonProperties = SpringBeanListener.getLogonProperties(req);
			
			ConfigBean cb = configService.getConfigBean();
			cb.setLogonProperties(logonProperties);
			
			final String buildVersion = logonProperties.getBuildVersion();
			pw.println("Logon build version: " + buildVersion);
			if (logonProperties.getDevmode()) {
				pw.println("<br/>DB Connection: " + cb.getDbConnectionUrl());	
			}
			pw.println("<br/><hr/>");
			
			final String changeToInstance = req.getParameter("instance");
			final boolean notify = BooleanUtils.toBoolean(req.getParameter("notify"));
			final boolean reload = BooleanUtils.toBoolean(req.getParameter("reload"));
			if ( (changeToInstance != null) || (reload) ) {
				String excMsg = null;
				Object[][] eParams = null;
				Collection configReloadResults = null;
				try {
					if (reload) {
						configReloadResults = configService.reloadConfigBean(notify);
					}
					else {
						cb = configService.initializeConfigBean(changeToInstance, logonProperties);
					}
				} 
				catch (EJBException e) {
					if (e.getCause() instanceof LogonException) {
						eParams = ((LogonException)e.getCause()).getParams();
					}
					excMsg = e.getMessage();
				}
				catch (LogonException e) {
					eParams = e.getParams();
					excMsg = e.getMessage();
				}
				catch (Exception e) {
					excMsg = e.getMessage();
				}
				if (excMsg != null) {
					pw.println("<h1 class=\"not_ok\">An error occurred loading the configuration</h1>");
					pw.println("<p>" + StringEscapeUtils.escapeHtml(excMsg));
					pw.println("The application will fail to start until all errors are corrected.<br/>");
					if (eParams != null) {
						Object[] errors = eParams[0];
						for (int i=1;i<errors.length;i++) {
							pw.println(errors[i] + "<br/>");
						}
					}
					pw.println("</p>");
				}
				
				if (configReloadResults != null) {
					pw.println("Reloaded configurations: <br/>");
					for (Iterator it=configReloadResults.iterator();it.hasNext();) {
						ConfigReloadResult crr = (ConfigReloadResult)it.next();
						if (crr.getSource() == null) {
							cb = crr.getConfigBean();
						}
						String source = crr.getSource();
						if (source == null) {
							source = cb.getLogonProperties().getProviderurl() + " (this server)";
						}
						pw.print(source + ": ");
						if (crr.getConfigBean() != null) {
							pw.println("<span class=\"ok\">OK</span> (" + crr.getConfigBean().getInstance() + ")<br/>");	
						}
						else {
							pw.print("<span class=\"not_ok\">FAILED</span>");
							if (crr.getThrowable() != null) {
								pw.println(" (" + crr.getThrowable().toString() + ": " + crr.getThrowable().getMessage() + ")<br/>");
							}
							else {
								pw.println(" (unknown error)<br/>");
							}
						}
					}
					pw.println("<hr/>");
				}
			}
						
			List l = new ArrayList(cb.log());
			Collections.sort(l, new  Comparator() {
				public int compare(Object o1, Object o2) {
					String l1 = (String)o1;
					String l2 = (String)o2;
					if (l1.startsWith("!")) {
						l1 = l1.substring(1);
					}
					if (l2.startsWith("!")) {
						l2 = l2.substring(1);
					}
					
					return l1.compareTo(l2);
				}				
			});
			final String currentInstance = cb.getInstance();
			pw.print("application configuration: instance '" + currentInstance + "'&nbsp;");
			pw.println("<button type=\"button\" onclick=\"javascript:location.href='" + req.getContextPath() + "/versionCheck?reload=true'\">reload</button>&nbsp;&nbsp;");
			pw.println("<button type=\"button\" onclick=\"javascript:location.href='" + req.getContextPath() + "/versionCheck?reload=true&notify=true'\">reload and notify other hosts</button>");
			pw.println("<br/><hr/>");
			pw.println("<pre>");
			boolean overriden = false;
			for (Iterator iterator = l.iterator(); iterator.hasNext();) {
				String line = (String) iterator.next();
				if (line.startsWith("!")) {
					overriden = true;
				}
				pw.println(StringEscapeUtils.escapeHtml(line));
			}
			pw.println("</pre>");
			pw.println("<hr/>");
			
			if (overriden) {
				pw.println("values marked with a '!' were overridden by a system property<br/>");
			}
			
			pw.println("LogonProperties: " + logonProperties.toString());
			pw.println("<br/><hr/>");
			
			pw.println("available instance configurations (click on links to change application " +
					"to a different instance configuration):<br/>");
			
			final InstanceNode rootNode = InstanceNode.parse(
														configService.listInstances(), 
														currentInstance);
			rootNode.printTo(pw, req.getContextPath(), currentInstance);
			
			final String version = req.getParameter("build.version");
			
			if (version == null) {
				resp.setStatus(HttpServletResponse.SC_OK); // 200
				return;
			}
			
			LOG.info("version test:");
			LOG.info("	testing for version: " + version);
			LOG.info("	application version: " + buildVersion);
			
			if (buildVersion.equals(version)) {
				LOG.info("   ---> SUCCESS!");
				resp.setStatus(HttpServletResponse.SC_OK); // 200
			}
			else {
				LOG.info("   ---> FAILED!");
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404 
			}
		}
		catch (LogonException e) {
			ExceptionUtil.errException(LOG, e, params);
			if (ObjectUtils.equals(e.getReason(), Reason.RES_ERROR_SECURITY_NOT_ALLOWED)) {
				pw.println("<pre class=\"not_ok\">");
				pw.println(e.getMessage());
				pw.println("</pre>");
				resp.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 
			}			
		}
		catch (Throwable t) {
			ExceptionUtil.errException(LOG, t, params);
		}
		finally {
			AuthUtils.deauthorize();
			pw.println("</body></html>");
			pw.flush();
			
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * Inner class holding the parsed instances in a node-like hierarchy
	 */
	private static final class InstanceNode {

		/**
		 * a regular expression exception for when not to display instance names as links,
		 * even if they are leafs.
		 */
		private final static Pattern DISPLAY_NEVER = Pattern.compile("(crh)|(adnovum)");
		
		/**
		 * Parses the instance name list, and returns the instance hierarchy
		 * 
		 * @param instanceNames the list of instance names (no preordering required)
		 * @return the root (default) instance node (with name 'null')
		 */
		public static InstanceNode parse(List<String> instanceNames, String currentInstance) {
			
			final InstanceNode rootNode = new InstanceNode(null, null);
			for (String instanceName : instanceNames) {
				if (instanceName == null) {
					rootNode.derived = false;
					continue;
				}

				parse(rootNode, instanceName, false);
			}
			
			// if the currently active instance is not explicitly listed, then add it as derived
			if (!instanceNames.contains(currentInstance)) {
				parse(rootNode, currentInstance, true);				
			}
			
			return rootNode;
		}
		
		private static void parse(InstanceNode rootNode, String instanceName, boolean isDerived) {
			final String[] hierarchy = instanceName.split("\\.");
			
			InstanceNode currentNode = rootNode;
			for (int i=0; i < hierarchy.length; i++) {
				if (currentNode.subInstanceNodes.containsKey(hierarchy[i])) {
					currentNode = currentNode.subInstanceNodes.get(hierarchy[i]);
				}
				else {
					final InstanceNode childNode = new InstanceNode(currentNode.fullName, hierarchy[i]);
					currentNode.subInstanceNodes.put(hierarchy[i], childNode);
					currentNode = childNode;
				}				
			}
			currentNode.derived = isDerived;
		}
		
		/**
		 * if an instance name is derived it does not contain explicit configuration 
		 * data in the database, it is only dispalayed because of the hierarchy.
		 */
		private boolean derived;
		
		/**
		 * the instance's full name (for example 'adnovum.dev')
		 */
		private final String fullName;
		
		/**
		 * the children of this instance further down in the hierarchy, with
		 * their subName as keys (for example in 'crh' this will contain
		 * the values for 'crh.c0' and 'crh.c1' with keys 'c0' and 'c1')
		 */
		private final Map<String, InstanceNode> subInstanceNodes;
		
		/**
		 * constructor for the instance node
		 * 
		 * @param parentFullName for example 'crh' or 'crh.c0'
		 * @param subName for example 'prod' or 'qual'
		 */
		private InstanceNode(String parentFullName, String subName) {
			this.derived = true;
			
			if (parentFullName == null) {
				this.fullName = subName;
			}
			else {
				this.fullName = parentFullName + "." + subName;
			}
			subInstanceNodes = new TreeMap<String, InstanceNode>();
		}

		/**
		 * prints out the hierarchy in a list-sublist format, starting with this node, 
		 * and outputting all of it's descendants
		 * 
		 * @param pw the writer to print to
		 * @param linkPath the link path to include in the href attributes
		 * @param currentInstance the currently active instance
		 */
		public void printTo(PrintWriter pw, String linkPath, String currentInstance) {
			printTo(pw, linkPath, currentInstance, 0);

			pw.println("<ul>legend:");
			pw.println("<li><b>bold</b> - the currently active instance.</li>");
			pw.println("<li><i>italic</i> - instance has explicit values in database.</li>");
			pw.println("<li>not selectable - all '" + InstanceNode.DISPLAY_NEVER.pattern() + "' instances.</li>");
			pw.println("<br/>(clicking the link switches the application's configuration to that instance).</li>");
			pw.println("</ul>");
		}
		
		private void printTo(PrintWriter pw, String linkPath, String currentInstance, int nesting) { 
			if (fullName != null) {
				final boolean isCurrentInstance = StringUtils.equals(currentInstance, fullName);				
				pw.print(StringUtils.repeat("\t", nesting) + "<li>");
				if (isCurrentInstance) {
					pw.print("<b>");
				}
				if (!derived) {
					pw.print("<i>");
				}
				if (InstanceNode.DISPLAY_NEVER.matcher(fullName).matches()) {
					pw.print(fullName);
				}
				else {
					pw.print("<a href='" + linkPath + "/versionCheck?instance=" + fullName + "'>" + fullName + "</a>");
				}
				if (isCurrentInstance) {
					pw.print("</b>");
				}
				if (!derived) {
					pw.print("</i>");
				}
				pw.println("</li>");
			}
			if (!subInstanceNodes.isEmpty()) {
				pw.println(StringUtils.repeat("\t", nesting) + "<ul>");
				for (InstanceNode subInstanceNode : subInstanceNodes.values()) {
					subInstanceNode.printTo(pw, linkPath, currentInstance, nesting + 1);
				}
				pw.println(StringUtils.repeat("\t", nesting) + "</ul>");
			}
		}
	}
}
