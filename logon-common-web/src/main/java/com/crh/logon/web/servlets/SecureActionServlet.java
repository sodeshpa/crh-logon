package com.crh.logon.web.servlets;

import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletException;

import ch.nevis.jcan.log.AdnTrace;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.SuppressPropertiesBeanIntrospector;
import org.apache.struts.action.ActionServlet;

/***
 *
 * Address security vulnerabilities of struts 1:
 * - CVE-2014-0114
 * - CVE-2016-1181
 * - CVE-2016-1182
 *
 * Making sure that certain properties are excluded from bean injection.
 * This allowed remote attackers to "manipulate" the ClassLoader.
 *
 *See CRHLOGON-99, CRHLOGON-120, LOGON-12087
 *
 */
@AdnTrace
public class SecureActionServlet extends ActionServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void initOther() throws ServletException {
		Set<String> suppressProperties = new HashSet<String>();
		suppressProperties.add("class");
		suppressProperties.add("multipartRequestHandler");
		suppressProperties.add("resultValueMap");


		PropertyUtils.addBeanIntrospector(new SuppressPropertiesBeanIntrospector(suppressProperties));
		PropertyUtils.clearDescriptors();

		super.initOther();
	}

}