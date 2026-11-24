/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.containeradmin;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.forms.AdminRFIDPopupForm;
import com.crh.logon.admin.web.forms.containeradmin.ContainerAdminForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.ContainerAdminService;
import com.crh.logon.business.services.interfaces.RefDataGenericsService;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.refdata.ContainerTrspCatTxEntity;
import com.crh.logon.persistence.entities.refdata.ContainerTypeTxEntity;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_ContainerAdminForm"
 *  path="/containeradmin/ContainerAdminEntry" scope="session"
 *  validate="false" input="/containeradmin/ContainerAdmin.def"
 *
 * @struts.action-forward name="success" path="/containeradmin/ContainerAdmin.def"
 */

public class ContainerAdminEntryAction extends ActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ContainerAdminEntryAction.class);

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
			Authorization.authorize(Authorization.MASTER_DATA_ROAD_CONTAINER);
			AdminRFIDPopupForm.resetForm(request.getSession());
			ContainerAdminForm helpForm = (ContainerAdminForm) form;
			helpForm.clear();
			Locale locale = getLocale(request);
			
			setSelectedTab("masterdata", request);
			setSelectedSub1Tab("road", request);
			setSelectedSub2Tab("container", request);
			String forwardName = "success";
			
			
			RefDataGenericsService refDataService = getRefDataGenericsService();
			ContainerAdminService containerAdminService = getContainerAdminService();
			
			initFormMaps(helpForm, locale, refDataService, containerAdminService);
			
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

	/**
	 * @param helpForm
	 * @param locale
	 * @param refDataService
	 * @param containerAdminService
	 */
	protected static void initFormMaps(ContainerAdminForm helpForm, Locale locale,
			RefDataGenericsService refDataService, ContainerAdminService containerAdminService) {
		List trspCatList = refDataService.findTxEntityList(ContainerTrspCatTxEntity.class, locale);
		Map trspCatMap = new LinkedHashMap();
		//ContainerTrspCatTx. Convert list to map.
		for (Iterator it = trspCatList.iterator(); it.hasNext();) {
			ContainerTrspCatTxEntity trsp = (ContainerTrspCatTxEntity) it.next();
			trspCatMap.put(
					trsp.getContainerTrspCatTxPK().getContainerTrspCatBs().getContainerTrspCatCd(),
					trsp);
		}

		//ContainerTypeTx. Convert list to map.
		List typeList = refDataService.findTxEntityList(ContainerTypeTxEntity.class, locale);
		Map typeMap = new LinkedHashMap();
		for (Iterator it = typeList.iterator(); it.hasNext();) {
			ContainerTypeTxEntity trsp = (ContainerTypeTxEntity) it.next();
			typeMap.put(trsp.getContainerTypeTxPK().getContainerTypeBs().getContainerTypeCd(), trsp);
		}

		
		//Carrier. Convert list to map.
		List carrierList = containerAdminService.fetchCarriers();
		Map carrierMap = new LinkedHashMap();
		for (Iterator it = carrierList.iterator(); it.hasNext();) {
			CarrierEntity carrier = (CarrierEntity) it.next();
			carrierMap.put(carrier.getCarrierId(), carrier);
		}
		helpForm.setCarrierMap(carrierMap);
		helpForm.setContainerTypeMap(typeMap);
		helpForm.setContainerTrspCatMap(trspCatMap);
	}

}
