/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.web.filters.utils.InputStreamWrapper;

/**
 * logs which user invokes the ajax driven services
 * 
 * @author AdNovum Informatik AG
 */
public class DwrRequestInfoFilter implements Filter {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DwrRequestInfoFilter.class);
	
	public void init(FilterConfig arg0) throws ServletException {
		LOG.info("DwrRequestInfoFilter initialized!");
	}

	public void destroy() {
		LOG.info("DwrRequestInfoFilter destroyed!");
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		
		LOG.info("processing dwr request");
		if (LOG.isDebugEnabled()) {
			HttpServletRequest request = (HttpServletRequest)req;
			InputStreamWrapper wrappedReq = new InputStreamWrapper(request);
			String[] lines = wrappedReq.getRequestBody().split("\\n");
			LOG.debug("request body:");
			for (int i = 0; i < lines.length; i++) {
				LOG.debug("    " + lines[i]);
			}
	        chain.doFilter(wrappedReq, res);
		}
		else {
	        chain.doFilter(req, res);
		}
	}
}
