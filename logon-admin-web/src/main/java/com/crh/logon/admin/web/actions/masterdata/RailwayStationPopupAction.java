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

import com.crh.logon.admin.web.forms.masterdata.ShippingPointForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.RailwayStationPopupForm;
import com.crh.logon.persistence.entities.common.RailwayStationEntity;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;

/**
 *
 * @author      AdNovum Informatik AG
 * 
 *  @struts.action name="admin_RailwayStationPopupForm"
 *  path="/RailwayStationPopup" scope="session"
 *  validate="false" input="RailwayStationPopupTile.def"
 *  
 * @struts.action-forward name="railwayStationPopup" path="RailwayStationPopupTile.def"
 * @struts.action-forward name="close" path="RailwayStationPopupClose.def"
 *  
 */

public class RailwayStationPopupAction extends ActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RailwayStationPopupAction.class);
	
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
				Authorization.authorize(Authorization.MASTER_DATA_GLOBAL_SHIPPINGPOINT);
				String forwardName = "railwayStationPopup";
				RailwayStationPopupForm helpForm = (RailwayStationPopupForm) form;
				ShippingPointForm spForm = (ShippingPointForm) request.getSession().getAttribute("admin_ShippingPointForm");
				if (request.getParameter("save") != null) {
					String railwayStationId = helpForm.getRailwayStationId();
					
					RailwayStationEntity[] selectedStations = getShippingPointService().loadOpAndCommRailwayStations( Long.parseLong(railwayStationId));
					
					if ( selectedStations == null ) {
						spForm.setOperationStation(null);
						spForm.setCommStation(null);
					} 
					else {
						spForm.setOperationStation(selectedStations[0]);
						spForm.setCommStation(selectedStations[1]);
					}
					forwardName = "close";
					
				}
				else if (request.getParameter("remove") != null) { 
					spForm.setOperationStation(null);
					spForm.setCommStation(null);
					forwardName = "close";
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
