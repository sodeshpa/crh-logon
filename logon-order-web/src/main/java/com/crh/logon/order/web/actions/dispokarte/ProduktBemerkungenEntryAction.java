/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.dispokarte;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.order.web.forms.dispokarte.DispokarteForm;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import com.crh.logon.web.utils.Authorization;

/**
 * @author AdNovum Informatik AG
 *
 * @struts.action name="order_dispokarte_DispokarteForm" 
 *  path="/dispokarte/ProduktBemerkungenEntryAction" scope="session"
 *  validate="true" input="ProduktBemerkungenTile.def"
 *
 * @struts.action-forward name="edit" path="ProduktBemerkungenTile.def"
 * 
 */
public class ProduktBemerkungenEntryAction extends OrderActionSupport {

	   private static final JcanLogger LOG =
	        JcanLoggerFactory.getLogger(ProduktBemerkungenEntryAction.class);

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
				Authorization.authorize(Authorization.MASTER_DATA_SHIPTO_CARD_R);
				DispokarteForm helpForm = (DispokarteForm) request.getSession().getAttribute(DispokarteForm.FORM_NAME);
				String forwardName = "edit";
				
		    	int index = new Integer(request.getParameter("id")).intValue();
		    	
		    	helpForm.setSelectedIndex(index);
		    	
		    	ShiptoSareaRestrEntity restr = ((DeliverySettingDTO) helpForm.getCurrSalesAreaBean().getRestrictionDeliverySettings().get(index)).getShiptoSareaRestr();
		    	
		    	helpForm.setProductDeliveryNote(restr.getNoteDelivery());
		    	helpForm.setProductDispatchingNote(restr.getNoteDispatching());
		    	helpForm.setProductOrderNote(restr.getNoteOrderMgmt());
		    	helpForm.setProductRailwayNote(restr.getNoteRailwayDisp());
		    	helpForm.setProductTruckNote(restr.getNoteTruckDriver());
				
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
