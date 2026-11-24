/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.masterdata;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.forms.masterdata.WagonSearchForm;
import com.crh.logon.persistence.entities.refdata.DistributionDestTxEntity;
import com.crh.logon.persistence.entities.refdata.WagonInactiveReasonTxEntity;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_WagonSearchForm" path="/WagonSearchEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="WagonSearchTile.def"
 */
public class WagonSearchEntryAction extends AdminActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(WagonSearchEntryAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		// [[
		ActionForward result = null;
		Object[][] params = {{"mapping", mapping},
							{"form", form},
							{"request", request},
							{"response", response}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "execute", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_RAIL_CARS);
			WagonSearchForm searchForm = (WagonSearchForm)form;
			searchForm.clear();
			String forwardName = "success";
			setSelectedTab("masterdata", request);
			setSelectedSub1Tab("rail", request);
			setSelectedSub2Tab("wagon", request);
			
			HashMap jsonData = new HashMap();
		    
   		    jsonData.put("wagonTypeList", getJsonSerializer().serialize(getServiceLocator().getWagonSearchService().getWagonTypeBsList()));
   		    List keeperList = getServiceLocator().getWagonDetailService().getWagonKeeperList();
   		    
   		    jsonData.put("wagonKeeperList", getJsonSerializer()
   		    		.include("wagonKeeperId")
   		    		.include("wagonKeeperName")
   		    		.exclude("*").serialize(keeperList));
   		    
   		    jsonData.put("wagonInactiveReasonList", getJsonSerializer().serialize(getRefDataGenericsService().findTxEntityList(WagonInactiveReasonTxEntity.class, request.getLocale())));
   		    jsonData.put("orderStatusList", getJsonSerializer().serialize(getServiceLocator().getWagonSearchService().getOrderStatusList(request.getLocale())));
   		    jsonData.put("distributionDestList", getJsonSerializer().serialize(getRefDataGenericsService().findTxEntityList(DistributionDestTxEntity.class, request.getLocale())));
   		    
    		searchForm.setJson(jsonData);
			
			return result = mapping.findForward(forwardName);
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
