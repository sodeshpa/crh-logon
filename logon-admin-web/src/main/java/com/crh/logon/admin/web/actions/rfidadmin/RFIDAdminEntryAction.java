/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.rfidadmin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.rfidadmin.RFIDAdminForm;
import com.crh.logon.business.services.interfaces.RFIDService;
import com.crh.logon.business.services.interfaces.RefDataGenericsService;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.persistence.entities.refdata.RfidobjectTypeTxEntity;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_RFIDAdminForm"
 *  path="/rfidadmin/RFIDAdminEntry" scope="session"
 *  validate="true" input="/rfidadmin/RFIDAdminTile.def"
 *
 * @struts.action-forward name="success" path="/rfidadmin/RFIDAdminTile.def"
 */
public class RFIDAdminEntryAction extends ActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(RFIDAdminEntryAction.class);

	public ActionForward execute(
		ActionMapping mapping, ActionForm form,
		HttpServletRequest request,	HttpServletResponse response)
		throws IOException, ServletException
	{
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
			Authorization.authorize(Authorization.MASTER_DATA_RFID_MAINTENANCE);
			RFIDAdminForm helpForm = (RFIDAdminForm) form;
			helpForm.clear();
			setSelectedTab("masterdata", request);
			setSelectedSub1Tab("pda", request);
			setSelectedSub2Tab("rfidmaintenance", request);
			String forwardName = "success";
			
			ServiceLocator serviceLocator = getServiceLocator();
			RFIDService  service = serviceLocator.getRFIDService();
			RefDataGenericsService refDataService = serviceLocator.getRefDataGenericsService();
			
			
			//			List rfidList = service.fetchRfidDetailList(null, null, null, null, null, request.getLocale().getLanguage());	 
			
			//			helpForm.setRfidList(rfidList);
			
			List rfidObjcetTypes = refDataService.findTxEntityList(RfidobjectTypeTxEntity.class, false, request.getLocale(), RfidobjectTypeTxEntity.ATTR_TXT, true);
			Map rfIdTypeMap = new HashMap();
			
			Iterator rfIdTypeIterator = rfidObjcetTypes.iterator();
			
			while (rfIdTypeIterator.hasNext()) {
				RfidobjectTypeTxEntity rfidType = (RfidobjectTypeTxEntity) rfIdTypeIterator.next();
				rfIdTypeMap.put(rfidType.getRfidobjectTypeTxPK().getRfidobjectTypeBs().getRfidobjectTypeCd(), rfidType.getTxt());
			}
			helpForm.setRfidObjectTypeMap(rfIdTypeMap);
			
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
