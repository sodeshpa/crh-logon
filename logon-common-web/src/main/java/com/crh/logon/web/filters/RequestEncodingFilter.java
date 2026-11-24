package com.crh.logon.web.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.constants.Constant;

public class RequestEncodingFilter implements Filter {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RequestEncodingFilter.class);

	private static final String FILTER_CONFIG_ENCODING = "encoding";

	private String encoding;

	@Override
	public void destroy() {
		// nothing to do
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// [[
		Object[][] params = { { "request", request },
				{ "response", response },
				{ "chain", chain } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "doFilter", params);
		}
		// ]]
		try {
			if (request.getCharacterEncoding() == null) {
				LOG.debug("Using encoding '" + encoding + "' for current request");
				request.setCharacterEncoding(encoding);
			}

			chain.doFilter(request, response);
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
	public void init(FilterConfig config) {
		// [[
		Object[][] params = { { "config", config } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init", params);
		}
		// ]]
		try {
			encoding = config.getInitParameter(FILTER_CONFIG_ENCODING);
			if (encoding == null || encoding.trim().length() == 0) {
				LOG.warn(String.format("No init-parameter named '%s' found. Defaulting to '%s'",
						FILTER_CONFIG_ENCODING, Constant.DEFAULT_ENCODING));

				encoding = Constant.DEFAULT_ENCODING;
			}
			LOG.info(this.getClass().getName() + " initialized with encoding '" + encoding + "'!");
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

}
