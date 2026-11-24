/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.dispokarte;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.order.web.forms.bestellung.BestellungForm;
import com.crh.logon.order.web.forms.dispokarte.DispokarteForm;
import com.crh.logon.order.web.forms.dispokarte.SilobelegungEditForm;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.refdata.SiloTypeTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_dispokarte_SilobelegungEditForm"
 *  path="/dispokarte/SilobelegungEntry" scope="session"
 *  validate="true" input="DispokarteSilobelegungEditTile.def"
 *
  * @struts.action-forward name="edit" path="DispokarteSilobelegungEditTile.def"
 */
public class SilobelegungEntryAction extends OrderActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(SilobelegungEntryAction.class);

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
			Authorization.authorize(Authorization.MASTER_DATA_SHIPTO_CARD_R);
			SilobelegungEditForm helpForm = (SilobelegungEditForm) form;
			helpForm.clear();
			
			String referrer = helpForm.getReferrer();
			DispokarteForm dispoForm = null;
			BestellungForm orderForm = null;
			ShiptoEntity shipto = null;
			SapBpEntity soldto = null;
			
			// --- If called from Dispokarte... ---
			if (SilobelegungEditForm.REFERRER_DISPOKARTE.equals(referrer)) {
				dispoForm = (DispokarteForm) request.getSession().getAttribute(DispokarteForm.FORM_NAME);
				shipto = dispoForm.getShipTo();
				soldto = dispoForm.getSoldTo();
			}
			// --- ...or from Order taking ---
			else {
				Authorization.authorize(Authorization.ORDER);
				orderForm = BestellungForm.getSessionInstance(request);
				shipto = orderForm.getShipTo();
				soldto = orderForm.getSoldTo();
			}
			
			// get silo names (will be used to check if silo name is unique)
			ListIterator siloIt = shipto.getSilos().listIterator();
			Map siloNameMap = new HashMap();
			while ( siloIt.hasNext() ) {
				ShiptoSiloEntity next = (ShiptoSiloEntity) siloIt.next();
				siloNameMap.put(next.getSiloName(), next);
			}
			helpForm.setSiloNameMap(siloNameMap);
			
			List list = getRefDataGenericsService().findTxEntityList(SiloTypeTxEntity.class, request.getLocale());
			helpForm.setSiloTypes(list);
		    String forwardName = "edit";
		    ShiptoSiloEntity a;
		    if (helpForm.getAction().equals("edit")){
		    	a = (ShiptoSiloEntity)shipto.getSilos().get(helpForm.getId());
		    	helpForm.setSiloTypeCd(a.getSiloType().getSiloTypeCd());
		    }
		    else{
				Authorization.authorize(Authorization.MASTER_DATA_SHIPTO_CARD_W);
		    	//this is create
		    	/*if ( helpForm.getSiloType() == null ) {
		    		SiloTypeBsEntity siloType = getServiceLocator().getRefDataGenericsService().findBsEntity(SiloTypeBsEntity.class, new Long(1));
		    		helpForm.setSiloType(siloType);
		    	}*/
		    	
		    	a = new ShiptoSiloEntity();
		    	a.setShipto(shipto);
		    	a.setActiveFlag(true);
		    	a.setSiloType(null);
		    	a.setPortableSiloFlag(false);
		    }
		    helpForm.setSilo(a);
		    helpForm.setMaterials(getDispokarteService().fetchSapMaterialDescByLang(request.getLocale().getLanguage(), shipto, soldto, null));
		    
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
