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
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

import com.planetj.servlet.filter.compression.CompressingFilter;

@WebFilter(filterName = "CompressionFilter",
urlPatterns = {"*"}
)
public class CompressionFilter implements Filter {
	
	private CompressingFilter compressingFilter;
	
	public CompressionFilter() {
		compressingFilter = new CompressingFilter();
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		compressingFilter.init(filterConfig);
	}
	
	@Override
	public void destroy() {
		compressingFilter.destroy();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		compressingFilter.doFilter(request, response, filterChain);	
	}	
}
