/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.actions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DownloadAction;
import org.apache.struts.actions.DownloadAction.StreamInfo;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * Generic support class for actions that implement downloading a file generated on-the-fly, where file
 * content is provided by a byte array. File name, content type, and content bytes can be specified by
 * the Action extending this class.
 * 
 * <p>TODO: Consider adding an internal cache if needed. (Like in GenericBlobServlet.)</p> 
 * 
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 */
public abstract class ByteArrayDownloadAction extends ActionSupport {

	private static final JcanLogger LOG =
		JcanLoggerFactory.getLogger(ByteArrayDownloadAction.class);


	/**
	 * Called once per request by
	 * {@link #execute(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)}, the extender of
	 * this class only has to implement this method. See {@link DownloadData} for more information.
	 */
	protected abstract DownloadData getDownloadData(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response);


	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// [[
		ActionForward result = null;
		Object[][] params = {{"mapping", mapping},
							{"form", form},
							{"request", request},
							{"response", response}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "execute", params);
		}
		// ]]
		try {
			WrappedDownloadAction wrappedDownloadAction = new WrappedDownloadAction();
			return result = wrappedDownloadAction.execute(mapping, form, request, response);
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
	 * Defines the data for download, including the designated file name and content type.
	 */
	public static class DownloadData {
		
		private final String fileName;
		private final String contentType;
		private final byte[] bytes;


		public DownloadData(String fileName, String contentType, byte[] bytes) {
			super();
			this.fileName = fileName;
			this.contentType = contentType;
			this.bytes = bytes;
		}


		public String getFileName() {
			return fileName;
		}


		public String getContentType() {
			return contentType;
		}


		public byte[] getBytes() {
			return bytes;
		}

	}


	/**
	 * This is our extension to the original DownloadAction, however, we only use it internally, because
	 * we already extended ActionSupport.
	 */
	private class WrappedDownloadAction extends DownloadAction {

		@Override
		protected StreamInfo getStreamInfo(ActionMapping mapping,
				ActionForm form,
				HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			// [[
			StreamInfo result = null;
			Object[][] params = {{"mapping", mapping},
								{"form", form},
								{"request", request},
								{"response", response}};
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(this, "getStreamInfo", params);
			}
			// ]]
			try {
				DownloadData downloadData = getDownloadData(mapping, form, request, response);
				
				response.setHeader("Content-disposition", "attachment; filename = \""
						+ downloadData.getFileName() + "\"");
				
				// This is a generated content, therefore the browser should not cache it.
				/*
				 * LOGON-5404/marius Oct 9, 2012:
				 * To work around an issue in IE8, we need to set a different cache-control
				 * header for that browser, see comment here:
				 * https://aww.adnovum.ch/jira/browse/LOGON-5404?focusedCommentId=338101&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-338101
				 */
				final String cacheHeader;
				if (isIE8OrOlder(request)) {
					cacheHeader = "no-store, no-cache, must-revalidate";
				}
				else {
					cacheHeader = "no-cache";
					response.setHeader("Pragma","no-cache");
				}
				response.setHeader("Cache-Control", cacheHeader);
				response.setDateHeader ("Expires", 0);
				
				return result = new ByteArrayStreamInfo(downloadData.getContentType(),
						downloadData.getBytes());
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
		 * Checks if the current request comes from IE8 or an older
		 * IE browser.
		 * @param request
		 * @return
		 */
		private boolean isIE8OrOlder(HttpServletRequest request) 
		{
			final String userAgent = request.getHeader("user-agent");
			LOG.debug("Found user-agent header: {}", new Object[] {userAgent});
			
			if (userAgent == null) {
				return false;
			}
			
			// Note that Opera user agent strings can also contan "MSIE":
			// http://www.useragentstring.com/pages/Opera/
			final int msiePos = userAgent.indexOf("MSIE");
			if (msiePos == -1 || userAgent.contains("Opera")) {
				LOG.debug("Current browser is not MSIE.");
				return false;
			}
			
			/*
			 * Sample user agent string for MSIE:
			 * Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; Media Center PC 6.0; InfoPath.2; MS-RTC LM 8
			 */
			final String versionString = userAgent.substring(msiePos + 5, msiePos + 8);
			LOG.debug("Detected version number string: '{}'", new Object[] {versionString});
			
			// Simplified version
			return versionString.equals("8.0") || versionString.equals("7.0") || 
					versionString.equals("6.1") || versionString.equals("6.0");
		}


		@Override
		public ActionForward execute(ActionMapping mapping, ActionForm form,
				HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			// [[
			ActionForward result = null;
			Object[][] params = {{"mapping", mapping},
								{"form", form},
								{"request", request},
								{"response", response}};
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(this, "execute", params);
			}
			// ]]
			try {
				return result = super.execute(mapping, form, request, response);
			}
			catch (Exception e) {
				if (e.getCause() instanceof SocketException) {
					/*
					 * Most probably the client connection is lost (eg. connection reset by peer or broken
					 * pipe). At this point the DownloadData has already been generated. It is safe to just
					 * ignore the error (although it could be useful to log it anyway).
					 */
					LOG.error("Client connection lost: " + e);
					return result = null;
				}
				else {
					throw e;
				}
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


	/**
	 * This class is for passing data to the original DownloadAction provided by Struts.
	 */
	private static class ByteArrayStreamInfo implements StreamInfo {

		protected final String contentType;
		protected final byte[] bytes;


		public ByteArrayStreamInfo(String contentType, byte[] bytes) {
			this.contentType = contentType;
			this.bytes = bytes;
		}


		public String getContentType() {
			return contentType;
		}


		public InputStream getInputStream() throws IOException {
			return new ByteArrayInputStream(bytes);
		}

	}

}
