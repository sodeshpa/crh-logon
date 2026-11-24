package com.crh.logon.order.web.actions.dispo;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.forms.dispo.DispoExportPopupForm;
import com.crh.logon.order.web.forms.dispo.DispoForm;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.beans.SelectableGeneralDisplayNameDto;
import com.crh.logon.business.services.interfaces.PoolAdminService;
import com.crh.logon.business.services.utils.PoolCarrierRelationWrapper;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_dispo_DispoExportPopupForm"
 *  path="/dispo/DispoExportPopupEntry" scope="session"
 *  validate="false"
 *  
 * @struts.action-forward name="success" path="DispoExportPopupTile.def"
 */
public class DispoExportPopupEntryAction extends OrderActionSupport {
	
	private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(DispoExportPopupEntryAction.class);
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
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

			Authorization.authorize(Authorization.DISPATCHING_R);
			setSelectedTab("dispo", request);

			DispoExportPopupForm helpForm = (DispoExportPopupForm)form;
			DispoForm dispoForm = (DispoForm) request.getSession().getAttribute(DispoForm.FORM_NAME);
			
			Set<Long> preSelectedPoolIds = new HashSet<Long>();
			
			Long preSelectedPoolId = dispoForm.getSelectedPoolEntityId();
			preSelectedPoolIds.add(preSelectedPoolId);
			
			Set<Long> preSelectedCarriers = dispoForm.getSelectedCarriers();

			Locale locale = request.getLocale();

			PoolAdminService poolAdminService = getServiceLocator().getPoolAdminService();
			PoolCarrierRelationWrapper wrapper 
				= poolAdminService.fetchTruckPoolCarrierConnectionsForLocale(locale, preSelectedPoolIds, preSelectedCarriers);

			List<SelectableGeneralDisplayNameDto> pools = wrapper.getPools();
			List<SelectableGeneralDisplayNameDto> carriers = wrapper.getCarriers();
			Map<Long, List<SelectableGeneralDisplayNameDto>> poolCarrierMap = wrapper.getPoolCarrierMap();

			String selectedCarrierIDs = StringUtils.join(preSelectedCarriers, ',');
			 
			helpForm.setCarriers(carriers);
			helpForm.setPools(pools);
			helpForm.setPoolCarrierMap(poolCarrierMap);
			helpForm.setSelectedCarrierIDs(selectedCarrierIDs);
			helpForm.setSelectedPoolIDs(preSelectedPoolId.toString());
			helpForm.setStringFromDate(dispoForm.getStringDate());
			helpForm.setStringToDate(dispoForm.getStringDate());
			
			String forwardName = "success";
			
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
