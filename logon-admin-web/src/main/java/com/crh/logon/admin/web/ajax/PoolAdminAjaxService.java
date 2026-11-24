/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.ajax;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.beans.ForwardingAgentBean;
import com.crh.logon.business.services.interfaces.PoolAdminService;
import com.crh.logon.persistence.entities.sap.SapVendorEntity;
import com.crh.logon.utils.ListRange;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.utils.Authorization;

/**
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to 
 * Ajax components. 
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="PoolAdminAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.admin.web.beans.*"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRangeWithMessages"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.web.utils.DwrMessageBean"
 *
 */
public class PoolAdminAjaxService extends DwrServiceSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PoolAdminAjaxService.class);


	/**
	 * @param start
	 * @param count
	 * @param orderBy
	 * @param query
	 * @param request
	 * @return
	 */
	public ListRange fetchForwardingAgents(int start, int count, String orderBy,
			String query, HttpServletRequest request) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchForwardingAgents", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_ROAD_POOL);

			final PoolAdminService service = getServiceLocator().getPoolAdminService();
			
			final ListRange listRange = new ListRange();			
			final List<SapVendorEntity> sapVendors = service.fetchSapVendorList(start, count, orderBy, query);					
			final List<ForwardingAgentBean> forwardingAgentBeans = new ArrayList<ForwardingAgentBean>();
			
			if ((sapVendors != null) && (!sapVendors.isEmpty())) {
			
				for (SapVendorEntity sapVendorEntity : sapVendors) {
					ForwardingAgentBean forwardingAgentBean = getForwardingAgentBean(sapVendorEntity);
			
					if (forwardingAgentBean != null) {
						forwardingAgentBeans.add(forwardingAgentBean);
					}
				}
			}
			
			listRange.setData(forwardingAgentBeans.toArray());
			long totalSize = service.countSapVendorList(query);
			listRange.setTotalSize(totalSize);
			
			return result = listRange;
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
	 * @param creditNoteEntity
	 * @return
	 */
	private final ForwardingAgentBean getForwardingAgentBean(SapVendorEntity sapVendorEntity) {
		// [[
		ForwardingAgentBean result = null;
		Object[][] params = {{"sapVendorEntity", sapVendorEntity}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getForwardingAgentBean", params);
		}
		// ]]
		try {
			ForwardingAgentBean fwaBean = null;
			if (sapVendorEntity != null) {
				fwaBean = new ForwardingAgentBean();
				fwaBean.setEdited(false);
				fwaBean.setSapId(sapVendorEntity.getSapId());
				fwaBean.setName(sapVendorEntity.getName1());
				fwaBean.setCity(sapVendorEntity.getCity());
				fwaBean.setPostalCode(sapVendorEntity.getPostalCode());
				fwaBean.setCoordinates(null);
				
				//we don't set pools here
			}
			
			return result = fwaBean;
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
