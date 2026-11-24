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

/**
 * @author AdNovum Informatik AG
 *
 * This filter logs the elapsed time for processing a request.
 * It prints lines like this:
 *
 * [exec] 13:51:18,685 INFO  time elapsed (*******************): 19194ms
 *
 * This makes it easy to spot long runners immetiately without calculating
 * times or postprocessing the logs with other tools.
 */
public class TimeIndicatorFilter implements Filter {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(TimeIndicatorFilter.class);

    protected FilterConfig filterConfig;

    public TimeIndicatorFilter() {
        filterConfig = null;
    }

    public void doFilter(ServletRequest request, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        /* special tracing... we don't wan't indentation here on purpose */
        HttpServletRequest req = (HttpServletRequest) request;
        long start = 0;
        if (LOG.isInfoEnabled()) {
            start = System.currentTimeMillis();
        }

        // Produce the output from the rest of the chain
        chain.doFilter(req, res);

        if (LOG.isInfoEnabled()) {
            long elapsedTimeMillis = System.currentTimeMillis()-start;
            int sec = (int)elapsedTimeMillis/1000;
            StringBuilder buffer = new StringBuilder();
            if (sec  > 0) {
                for (int i = 0; i < sec; i++) {
                	buffer.append("*");
                }
                LOG.info("time elapsed (" + buffer.toString() + "): " + elapsedTimeMillis + "ms");
            }
            else {
                int rest = (int)elapsedTimeMillis - sec*1000;
                int ds = rest/100;
                for (int i = 0; i < ds; i++) {
                	buffer.append(".");
                }
                LOG.info("time elapsed (" + buffer.toString() + "): " + elapsedTimeMillis + "ms");
            }
        }
    }

    public void init(FilterConfig config) {
    	LOG.info("setting up '" + TimeIndicatorFilter.class + "'");
    }

    public void destroy() {
    	LOG.info("destroying '" + TimeIndicatorFilter.class + "'");
    }
}
