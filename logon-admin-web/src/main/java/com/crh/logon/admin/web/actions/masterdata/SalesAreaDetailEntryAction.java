/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.masterdata;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.masterdata.SalesAreaDetailForm;
import com.crh.logon.business.services.interfaces.SalesAreaService;
import com.crh.logon.persistence.entities.views.SareaDisplayEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.ConfigBeanProvider;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_SalesAreaDetailForm" path="/SalesAreaDetailEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="SalesAreaDetailTile.def"
 * @struts.action-forward name="failure" path="SalesAreaDetailTile.def"
 */
public class SalesAreaDetailEntryAction extends AdminActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SalesAreaDetailEntryAction.class);

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
			Authorization.authorize(Authorization.MASTER_DATA_GLOBAL_SALES_AREA);
			SalesAreaDetailForm helpForm;
			String forwardName = "success";
			helpForm = (SalesAreaDetailForm) form;
			helpForm.clear();
			
			String compositeId = request.getParameter("salesAreaDetails.compositeId");
			SareaDisplayEntity sareaDisplayEntity;
			SalesAreaService service = getServiceLocator().getSalesAreaService();
			sareaDisplayEntity = service.loadSalesArea(compositeId);
			if (sareaDisplayEntity == null) {
				LOG.error("Cannot find SareaSareaConfigEntity for the given compositeId (" + compositeId.toString() + ")");
				throw new LogonException();
			}
			helpForm.setDisplayEntity(sareaDisplayEntity);
			
			Locale locale = request.getLocale();
			helpForm.setLocale(locale);
			helpForm.populateForm();
						
			// If ExactQuantityOrderTolerance was not set before, we load the app default.
			if (StringUtils.empty(helpForm.getExactQuantityOrderTolerance())) {
				Integer appDefaultSareaExactQuantityOrderTolerance = ConfigBeanProvider.getConfigBean().getAppDefaultSareaExactQuantityOrderTolerance(); 
				helpForm.setExactQuantityOrderTolerance(appDefaultSareaExactQuantityOrderTolerance.toString());
			}
			
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
