/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.ajax;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.order.web.beans.RailRepairBean;
import com.crh.logon.persistence.entities.views.RailRepairEntity;
import com.crh.logon.persistence.entities.views.WagonSearchEntity;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.order.web.utils.PosWrapper;

/**
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to 
 * Ajax components. 
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="RailRepairAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.order.web.utils.*"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.order.web.beans.RailRepairBean"
 */
public class RailRepairAjaxService extends DwrServiceSupport {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RailRepairAjaxService.class);
    
    /**
	 * Used by Wagon Search 
     */
    public ListRange getInstandhaltungs(
    		int skip, 
    		int count, 
    		String sort,
    		Map<String, String> searchCrits,
    		HttpServletRequest request,
    		HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"count", count + ""},
							{"sort", sort},
							{"searchCrits", searchCrits},
							{"request", request},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getInstandhaltungs", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.BW_INSTANDHALTUNG);
			
			String sortBy = WagonSearchEntity.ATTR_WAGON_NO;
			boolean sortAsc = true;
			if (!StringUtils.empty(sort)) {
				String[] sortArr = sort.split(" ");
				sortBy = sortArr[0];
				sortAsc = "ASC".equals(sortArr[1]);
			}

			//--- Retrieve business results ---//
			result = getServiceLocator()
					.getWagonSearchService()
					.searchRailRepair(skip, count, sortBy, sortAsc, searchCrits, request.getLocale());

			//--- Convert to beans to override date/time formatting ---//
			TimeZone userTz = getUserTimeZone();

			List<RailRepairBean> beans = new ArrayList<RailRepairBean>();
			for (Object o: result.getData()) {
				RailRepairEntity entity = (RailRepairEntity)o;

				RailRepairBean bean = new RailRepairBean(entity);
				bean.setCreated(userTz.fmtAsDayMonthYear(entity.getCreated()));
				bean.setArrived(userTz.fmtAsDayMonthYear(entity.getArrived()));
				bean.setPlantEnter(userTz.fmtAsDayMonthYear(entity.getPlantEnter()));
				bean.setPlantLeave(userTz.fmtAsDayMonthYear(entity.getPlantLeave()));

				beans.add(bean);
			}

			result.setData(beans.toArray());

			return result;
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
	 * Used by Wagon Search 
     */
    public ListRange getWagonNoListForPart(
    		int start, 
    		int count, 
    		String orderBy,
    		String query,
    		HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getWagonNoListForPart", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.BW_INSTANDHALTUNG);
			
			ListRange searchRes = getServiceLocator().getWagonSearchService().searchRailRepairWagonNoList(start, count, query);
			
			List<PosWrapper> wrappers = new ArrayList<PosWrapper>();
			for (int i = 0; i < searchRes.getData().length; i++) {
				String wagonNumber = (String) searchRes.getData()[i];
				PosWrapper wrapper = new PosWrapper(new Long(i), wagonNumber);
				wrappers.add(wrapper);
			}
			
			searchRes.setData(wrappers.toArray());
			
			return result = searchRes;
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
