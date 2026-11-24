/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.servlets;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.persistence.dto.BlobDTO;
import com.crh.logon.persistence.entities.common.ImageEntity;
import com.crh.logon.persistence.entities.common.OrderRepAttEntity;
import com.crh.logon.persistence.entities.common.OutputdocEntity;
import com.crh.logon.persistence.entities.common.SignatureEntity;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;

/**
 * @author AdNovum Informatik AG
 */
public class GenericBlobServlet extends LogonServlet {
	
	private static class CacheEntry {
		
		private BlobDTO bDTO;
		private long storageTime;
		
		public CacheEntry(BlobDTO bDTO) {
			
			this.bDTO = bDTO;
		}
		
		public BlobDTO getBDTO() {
			return bDTO;
		}
		public void setBDTO(BlobDTO bdto) {
			bDTO = bdto;
		}
		public long getStorageTime() {
			return storageTime;
		}
		public void setStorageTime(long storageTime) {
			this.storageTime = storageTime;
		}
	}

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(GenericBlobServlet.class);
	private ServiceLocator locator;
	private String defaultEntity, defaultKeyName;
	private boolean restricted;
	private int cacheTTL; // 0 = no cache
	private Map cache;
	/**
     * The content-type of the images to return.
     */
    private static final String CONTENT_TYPE = "image/x-icon";
    
	@Override
	public void init() throws ServletException {
		locator = getServiceLocator();
		super.init();
		
		this.defaultEntity = getServletConfig().getInitParameter("entity");
		this.defaultKeyName = getServletConfig().getInitParameter("keyName");
		this.restricted =  Boolean.parseBoolean(getServletConfig().getInitParameter("restricted"));
		if (getServletConfig().getInitParameter("cacheTTL") != null) {
			this.cacheTTL = Integer.parseInt(getServletConfig().getInitParameter("cacheTTL"));
		}		
		cache = new Hashtable(); // must be synchronized
	}
	
	private void cachePut(String entity, String keyName, String keyValue, BlobDTO bDTO) {
		
		String cacheKey = entity + "/" + keyName + "/" + keyValue;
		if (LOG.isDebugEnabled()) {
			LOG.debug("Caching " + cacheKey + " mime: " + bDTO.getMime());
		}
		CacheEntry ce = new CacheEntry(bDTO);
		ce.setStorageTime(System.currentTimeMillis());
		
		cache.put(cacheKey, ce);
	}
		
	private BlobDTO cacheGet(String entity, String keyName, String keyValue) {
		
		String cacheKey = entity + "/" + keyName + "/" + keyValue;
		if (LOG.isDebugEnabled()) {
			LOG.debug("Retrieving " + cacheKey);
		}
		CacheEntry ce = (CacheEntry)cache.get(cacheKey);
		if (ce == null) {
			
			return null;
		}
		if ( (cacheTTL > 0) && (System.currentTimeMillis() > (ce.getStorageTime() + 1000*cacheTTL)) ) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Cached object expired");
			}
			
			return null;
		}
		
		return (BlobDTO)ce.getBDTO();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException,
			IOException {
		// [[
		Object[][] params = {{"req", req},
							{"resp", response}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "doGet", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.PUBLIC_FUNCTION);
			// Get values of initialization parameters
			String entityParam = defaultEntity;
			String keyName = defaultKeyName;
			if (restricted) {
				if (entityParam == null) {
					entityParam = req.getParameter("entity");
				}
				if (keyName == null) {
					keyName = req.getParameter("keyName");
				}
			}
			String keyValue = req.getParameter("keyValue");
			if (entityParam == null || keyName == null || keyValue == null) {
				throw new ServletException("mandatory parameter missing! "
						+ "(mandatory: 'entity = " + entityParam + "', 'keyName = " + keyName
						+ "', 'keyValue = " + keyValue + "')");
			}
			
			BlobDTO bDTO = null;
			boolean authorized = authorized(entityParam);
			if (authorized) {
				boolean noCache = Boolean.parseBoolean(req.getParameter("nocache"));
				
				if (!noCache) {
					bDTO = cacheGet(entityParam, keyName, keyValue);
					if ( (logDebugEnabled) && (bDTO != null) ) {
						LOG.debug("Data retrieved from cache");
					}
				}
			}
			if (bDTO == null) {
				bDTO = locator.getGenericBlobService().fetchBlob(entityParam, keyName, keyValue, authorized);
				if ((bDTO != null) && (cacheTTL != 0)) {
					cachePut(entityParam, keyName, keyValue, bDTO);
				}
			}
			
			ServletOutputStream out = response.getOutputStream();
			boolean isFavicon = Boolean.parseBoolean(req.getParameter("favicon"));
			if(isFavicon){
				response.setContentType(CONTENT_TYPE);
			} else {
				String mime = bDTO.getMime();
				response.setContentType(mime);
			}
			response.setHeader("Pragma", "cache");
			response.setHeader("Expires", "0");
			response.setHeader("Cache-control", "private");
			response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
			
			if (bDTO.getName() != null && !StringUtils.empty(bDTO.getName())) {
				response.setHeader("Content-disposition", "attachment; filename = \"" + bDTO.getName() + "\"");
			}
			byte[] inStream = bDTO.getStream();
			
			if (inStream != null) {
				// Transfer bytes from in to out
				response.setContentLength(inStream.length);
				out.write(inStream, 0, inStream.length);
			}
		}
		catch (Throwable t) {
			ExceptionUtil.errException(LOG, t, params);
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
	 * @param entity
	 * @return true if the user is allowed to see the object, no further checking is needed
	 * 			false if the user might be allowed to see the object, based on further checking (dataraum for example)
	 * @throws LogonException if the user is definitely not allowed to read the object
	 */
	private boolean authorized(String entity) {

		if (ImageEntity.class.getSimpleName().equals(entity)) {
			
			return true;
		}
		if (Authorization.isAllowed(Authorization.TABLE_BROWSER)) {
			
			return true;
		}
		if (SignatureEntity.class.getSimpleName().equals(entity)) {
			
			return Authorization.isAllowed(Authorization.USER_ADMIN_LOGON);
		}
		else if (OutputdocEntity.class.getSimpleName().equals(entity)) {
			if ( (Authorization.isAllowed(Authorization.ORDER_DETAIL_MATERIAL_R)) || (Authorization.isAllowed(Authorization.SHIPPING_R)) ) {
				
				return false;
			}
		}
		else if (OrderRepAttEntity.class.getSimpleName().equals(entity)) {
			if (Authorization.isAllowed(Authorization.WAGON_SPECIAL_ORDER_CREATE_EDIT) )  {
				
				return false;
			}
		}		
		
		throw new LogonException("User is not allowed to access " + entity, Reason.RES_ERROR_SECURITY_NOT_ALLOWED);
	}
}
