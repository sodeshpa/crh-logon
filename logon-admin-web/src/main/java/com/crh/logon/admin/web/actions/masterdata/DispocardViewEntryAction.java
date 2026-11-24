/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.masterdata;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.masterdata.DispocardViewForm;
import com.crh.logon.business.services.interfaces.RefDataGenericsService;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeTxEntity;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.PlantSareaUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_DispocardViewForm" path="/DispocardViewEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="DispocardViewTile.def"
 * @struts.action-forward name="failure" path="DispocardViewTile.def"
 */
public class DispocardViewEntryAction extends AdminActionSupport {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DispocardViewEntryAction.class);

	private static final String FW_NAME_SUCCESS = "success";
	private static final String FW_NAME_FAULIRE = "failure";
	
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
			String forwardName = FW_NAME_SUCCESS;
			
			Authorization.authorize(Authorization.MASTER_DATA_DISPOCARD_UPDATE_VIEW);
			DispocardViewForm helpForm = (DispocardViewForm)form;
			
			setSelectedTab("masterdata", request);
			setSelectedSub1Tab("dpview", request);
			
			Map jsonData = getJsonData(request.getLocale());
			helpForm.setJson(jsonData);
		    
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
	
	protected Map getJsonData(Locale locale) {
		
		RefDataGenericsService refData = getRefDataGenericsService();
		Map jsonData = new HashMap();
	    
		List deliveryTypeTxEntityList = refData.findTxEntityList(DeliveryTypeTxEntity.class, 
				false,
	    		locale,
	    		"deliveryTypeTxPK.deliveryTypeBs.deliveryTypeCd",
	    		true
	    );

	    jsonData.put("plantList", getJsonSerializer().serialize(PlantSareaUtils.getPlantList(getPlantSareaService())));
	    jsonData.put("deliveryTypeList", getJsonSerializer().serialize(deliveryTypeTxEntityList));

	    List poolTxEntities = getPoolAdminService().fetchPoolTxEntities(locale);
		jsonData.put("poolList", getJsonSerializer().serialize(poolTxEntities));
		
		return jsonData;
	}
}
