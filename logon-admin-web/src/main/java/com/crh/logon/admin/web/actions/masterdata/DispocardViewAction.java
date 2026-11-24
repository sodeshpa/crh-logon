/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.masterdata;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.utils.dispocard.DispocardSearchExportListener;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.masterdata.DispocardViewForm;
import com.crh.logon.admin.web.utils.dispocard.DispocardSearchExportMetaData;
import com.crh.logon.business.services.reporting.SqlDataExporter;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_DispocardViewForm" path="/DispocardView" scope="session"
 *  validate="true"
 *
 * @struts.action-forward name="success" path="DispocardViewTile.def"
 * @struts.action-forward name="failure" path="DispocardViewTile.def"
 */
public class DispocardViewAction extends AdminActionSupport {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DispocardViewAction.class);

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
			
			if (request.getParameter("export") != null) {
				Authorization.authorize(Authorization.MASTER_DATA_DISPOCARD_UPDATE_EXPORT);
				
				setDLHeaders(request, response, null, "dispocard_search_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".csv");
				SqlDataExporter exporter = new SqlDataExporter(getServiceLocator());
				DispocardSearchExportMetaData emd = new DispocardSearchExportMetaData();
				emd.setLangCd(request.getLocale().getLanguage());

				if (!StringUtils.isEmpty(request.getParameter("sapMaterialId"))) {
					emd.setSapMaterialId(Long.valueOf(request.getParameter("sapMaterialId")));
				}

				if (!StringUtils.isEmpty(request.getParameter("deliveryTypeCdList"))) {
					emd.setDeliveryTypeCdList(Arrays.asList(request.getParameter("deliveryTypeCdList").split(",")));
				}

				if (!StringUtils.isEmpty(request.getParameter("poolId"))) {
					emd.setPoolId(Long.valueOf(request.getParameter("poolId")));
				}
				if (!StringUtils.isEmpty(request.getParameter("carrierId"))) {
					emd.setCarrierId(Long.valueOf(request.getParameter("carrierId")));
				}

				if (!StringUtils.isEmpty(request.getParameter("plantCd"))) {
					emd.setPlantCd(request.getParameter("plantCd"));
				}

				emd.init(getServiceLocator(), getUser().getUserId());
				exporter.addExportListener(new DispocardSearchExportListener(getResources(request)));
				exporter.exportSqlData(response.getWriter(),
									   emd.getQueryString(),
									   emd.getQParams(),
									   request.getLocale(),
									   getUserTimezone());
				
				return result = null;
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