/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.dispokarte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.utils.AjaxPagedResultset;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.DispokarteService;
import com.crh.logon.order.web.forms.AjaxExtjsPagerQueryForm;
import com.crh.logon.utils.ListRange;

import flexjson.JSONSerializer;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="AjaxExtjsPagerQueryForm"
 *  path="/dispokarte/AjaxSearchShipto" scope="request"
 *  validate="false" 
 */
public class AjaxSearchShiptoAction extends OrderActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(AjaxSearchShiptoAction.class);

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
			AjaxExtjsPagerQueryForm helpForm = (AjaxExtjsPagerQueryForm)form;
			DispokarteService dispokarteService = getServiceLocator().getDispokarteService();
			ListRange listRange = dispokarteService.fetchShipTos(
					helpForm.getStart(),
					helpForm.getLimit(),
					helpForm.getQuery());
			AjaxPagedResultset results = new AjaxPagedResultset( new ArrayList(Arrays.asList(listRange.getData())), listRange.getTotalSize() );
			
			response.setContentType("application/x-json;charset=UTF-8");
			
			String jsonRes = new JSONSerializer().include("rows.sapBp.sapBpId", "rows.sapBp.name1",
					"rows.sapBp.postalCode", "rows.sapBp.city", "rows.sapSoldtoBp.sapBpId", 
					"rows.sapSoldtoBp.name1").exclude("*").serialize(results);
			LOG.debug(jsonRes);
			
			response.getWriter().append(jsonRes);
			response.flushBuffer();
			
			return result = null;
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
