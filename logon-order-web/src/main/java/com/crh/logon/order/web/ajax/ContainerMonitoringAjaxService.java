/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.ajax;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.interfaces.BestellungService;
import com.crh.logon.utils.ListRange;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.utils.Authorization;
import org.apache.commons.lang.StringUtils;


/**
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to 
 * Ajax components. 
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="ContainerMonitoringAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.order.web.utils.*"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 */
public class ContainerMonitoringAjaxService extends DwrServiceSupport{
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ContainerMonitoringAjaxService.class);

	@AdnTrace
	public ListRange fetchPlantsList(
			int skip,
			int count,
			String sort,
			String query,
			HttpServletRequest request,
			HttpSession session) {		
		Authorization.authorize(Authorization.CONTAINER_MONITORING);
		BestellungService bestellungServ = getServiceLocator().getBestellungService();
		Locale locale = request.getLocale();
		return bestellungServ.fetchContainerMonitoringPlantList(skip, count, query);
	}

	@AdnTrace
	public ListRange fetchMaterialsList(
			int skip,
			int count,
			String sort,
			String query,
			HttpServletRequest request,
			HttpSession session) {

			Authorization.authorize(Authorization.CONTAINER_MONITORING);

			BestellungService bestellungServ = getServiceLocator().getBestellungService();
			return bestellungServ.fetchContainerMonitoringMaterials(skip, count, query, request.getLocale().getLanguage());
	}

			
}
