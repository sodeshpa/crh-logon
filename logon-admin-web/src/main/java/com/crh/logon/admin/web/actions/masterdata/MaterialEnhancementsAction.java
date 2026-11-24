/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.masterdata;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.forms.masterdata.MaterialEnhancementsForm;
import org.apache.commons.lang.BooleanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.MaterialEnhancementsService;
import com.crh.logon.persistence.entities.common.MatPlantEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_MaterialEnhancementsForm" path="/MaterialEnhancements" scope="session"
 *  validate="true" input="MaterialEnhancementsTile.def"
 *
 * @struts.action-forward name="materialEnhancementsEntry" path="/MaterialEnhancementsEntry.do"
 * @struts.action-forward name="success" path="MaterialEnhancementsDeleteTile.def"
 */
public class MaterialEnhancementsAction extends AdminActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(MaterialEnhancementsAction.class);

	public static final String FORWARD_SUCCESS = "success";
	public static final String FORWARD_DELETE = "delete";

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
			String forwardName = FORWARD_SUCCESS;
			helpForm = (MaterialEnhancementsForm) form;

			if (FORWARD_DELETE.equals(helpForm.getFormAction())) {
				helpForm.setFormAction("");
				if (logDebugEnabled) {
					LOG.debug("Action: delete clicked!");
				}

				String matPlantIdParam = request.getParameter("matPlantId");
				Long matPlantId = null;
				if (!StringUtils.empty(matPlantIdParam)) {
					try {
						matPlantId = Long.valueOf(matPlantIdParam);
					}
					catch (NumberFormatException e) {
						throw new LogonException(e);
					}
				}
				MaterialEnhancementsService materialEnhancementsService = getServiceLocator().getMaterialEnhancementsService();
				MatPlantEntity matPlant = materialEnhancementsService.loadMatPlant(matPlantId);
				if (matPlant != null) {
					
					if (BooleanUtils.isTrue(matPlant.getPlant().getProductionPlant())) {
						MessageUtils.addActionError(request, "materialenhancements.error.wontDeleteProductionPlant");
					}
					else {
						materialEnhancementsService.deleteMatPlant(matPlantId);
						MessageUtils.addActionMessage(request, "materialenhancements.message.matPlantDeleted");
					}
				}
				else {
					LOG.error("Cannot find MatPlant for the given matPlantId (" + matPlantId.toString() + ")");
				}
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
