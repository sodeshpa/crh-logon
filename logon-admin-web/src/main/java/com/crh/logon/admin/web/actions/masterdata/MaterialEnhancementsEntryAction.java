/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.masterdata;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.forms.masterdata.MaterialEnhancementsForm;
import com.crh.logon.business.services.interfaces.MaterialEnhancementsService;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_MaterialEnhancementsForm" path="/MaterialEnhancementsEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="MaterialEnhancementsTile.def"
 * @struts.action-forward name="failure" path="MaterialEnhancementsTile.def"
 */
public class MaterialEnhancementsEntryAction extends AdminActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(MaterialEnhancementsEntryAction.class);

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
			Authorization.authorize(Authorization.MASTER_DATA_GLOBAL_MATERIAL);
			MaterialEnhancementsForm helpForm;
			String forwardName = "success";
			setSelectedTab("masterdata", request);
			setSelectedSub1Tab("global", request);
			setSelectedSub2Tab("material", request);
			
			helpForm = (MaterialEnhancementsForm) form;
			helpForm.clear();
			
			initializeMaterialNorms(helpForm, request);
			
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
	
	private void initializeMaterialNorms(MaterialEnhancementsForm form, HttpServletRequest request) {
		// [[
		Object[][] params = {{"form", form},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initializeMaterialNorms", params);
		}
		// ]]
		try {
			MaterialEnhancementsService materialEnhancementsService = getServiceLocator().getMaterialEnhancementsService();
			
			Map<String, String> sapLanguageMap = materialEnhancementsService.getSapLanguageCodes(request.getLocale());
			form.setSapLanguageMap(sapLanguageMap);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
}