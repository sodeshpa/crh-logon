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
import com.crh.logon.order.web.forms.bestellung.BestellungForm;
import com.crh.logon.order.web.forms.dispokarte.AnsprechpartnerEditForm;
import com.crh.logon.order.web.forms.dispokarte.DispokarteForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoPersonEntity;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_dispokarte_AnsprechpartnerEditForm" 
 *  path="/dispokarte/AnsprechpartnerEntry" scope="session"
 *  validate="true" input="DispokarteAnsprechpartnerEditTile.def"
 *
  * @struts.action-forward name="edit" path="DispokarteAnsprechpartnerEditTile.def"
 */
public class AnsprechpartnerEntryAction extends OrderActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(AnsprechpartnerEntryAction.class);

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
		    // Get the struts form(s).
			AnsprechpartnerEditForm helpForm = (AnsprechpartnerEditForm) form;
			String referrer = helpForm.getReferrer();
			DispokarteForm dispoForm = null;
			BestellungForm orderForm = null;
			ShiptoEntity shipto = null;
			
			Authorization.authorize(Authorization.MASTER_DATA_SHIPTO_CARD_R);
			// --- If called from Dispokarte... ---
			if (AnsprechpartnerEditForm.REFERRER_DISPOKARTE.equals(referrer)) {
				dispoForm = (DispokarteForm) request.getSession().getAttribute(DispokarteForm.FORM_NAME);
				shipto = dispoForm.getShipTo();
			}
			// --- ...or from Order taking ---
			else {
				Authorization.authorize(Authorization.ORDER);
				orderForm = BestellungForm.getSessionInstance(request);
				shipto = orderForm.getShipTo();
			}
				
			
			helpForm.clear();
			
		    String forwardName = "edit";
			
		    if (helpForm.getAction().equals("edit")){
		    	helpForm.setNewContact(false);
		    	int index = new Integer(request.getParameter("id")).intValue();
		    	helpForm.setId(index);
		    	ShiptoPersonEntity selectedContact = (ShiptoPersonEntity) shipto.getShiptoPersonList().get(index);
		    	helpForm.setContactperson(selectedContact);
		    }
		    else {
		    	helpForm.setNewContact(true);
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
