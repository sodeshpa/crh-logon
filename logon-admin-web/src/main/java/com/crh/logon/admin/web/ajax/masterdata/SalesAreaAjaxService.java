/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.ajax.masterdata;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.ajax.util.OrderBy;
import com.crh.logon.business.services.interfaces.SalesAreaService;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.refdata.SorgBsEntity;
import com.crh.logon.utils.ListRange;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.utils.Authorization;

/**
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to
 * AJAX components.
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="SalesAreaAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 */
public class SalesAreaAjaxService extends DwrServiceSupport {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SalesAreaAjaxService.class);

	public ListRange searchSaleasAreas(
			int skip,
			int count,
			String sort,
			Map<String, String> query,
			HttpServletRequest request,
			HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"count", count + ""},
							{"sort", sort},
							{"query", query},
							{"request", request},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "searchSaleasAreas", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_GLOBAL_SALES_AREA);
			String sorgCd = query.get("sorgCd");
			OrderBy orderBy = new OrderBy(sort, SareaEntity.ATTR_SAREA_ID);
			SalesAreaService salesAreaService = getServiceLocator().getSalesAreaService();
			Locale locale = request.getLocale();
			return result = salesAreaService.fetchSalesAreaList(skip, count, sorgCd, locale, orderBy.getField(), orderBy.isAscending());
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public List<SorgBsEntity> fetchSalesOrganisations(Map<String, String> query) {
		// [[
		List result = null;
		Object[][] params = {{"query", query}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSalesOrganisations", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_GLOBAL_SALES_AREA);
			final List<SorgBsEntity> findSorgBsEntityFiltered = getServiceLocator().getRefDataGenericsService().findBsEntityList(SorgBsEntity.class);	
			return result = findSorgBsEntityFiltered;
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
