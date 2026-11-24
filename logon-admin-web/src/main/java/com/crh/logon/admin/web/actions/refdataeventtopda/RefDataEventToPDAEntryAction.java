/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.refdataeventtopda;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.refdataeventtopda.RefDataEventToPDAForm;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.persistence.entities.refdata.CurrencyTxEntity;
import com.crh.logon.persistence.entities.refdata.FuelTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.PdaErrorMessageTxEntity;
import com.crh.logon.persistence.entities.refdata.PdaMarketinfoTxEntity;
import com.crh.logon.persistence.entities.refdata.RfidobjectTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.TruckMaintenanceTxEntity;
import com.crh.logon.persistence.entities.refdata.WaitingtimeReasonTxEntity;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_RefDataEventToPDAForm"
 *  path="/refdataeventtopda/RefDataEventToPDAEntry" scope="session"
 *  validate="true" input="/refdataeventtopda/RefDataEventToPDATile.def"
 *
 * @struts.action-forward name="success" path="/refdataeventtopda/RefDataEventToPDATile.def"
 */
public class RefDataEventToPDAEntryAction extends ActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(RefDataEventToPDAEntryAction.class);

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
			Authorization.authorize(Authorization.MASTER_DATA_RFID_MAINTENANCE);
			RefDataEventToPDAForm helpForm = (RefDataEventToPDAForm) form;
			helpForm.clear();
			setSelectedTab("masterdata", request);
			setSelectedSub1Tab("pda", request);
			setSelectedSub2Tab("refdataeventtopda", request);
			String forwardName = "success";
			
			Locale locale = getLocale(request);
			helpForm.setLocale(locale);
			
			ServiceLocator serviceLocator = getServiceLocator();
			//langs
            List langs = getRefDataGenericsService().findLangTxEntityList();
            helpForm.setLangs(langs);
			//functs
            List functs = serviceLocator.getRefDataEventToPDAService().fetchFuncList();
            helpForm.setFuncts(functs);
            //truck maintenance
            List truckMaintenances = getRefDataGenericsService().findTxEntityFiltered(TruckMaintenanceTxEntity.class, (Filter) null);
            helpForm.setTruckMaintenances(truckMaintenances);
            //pda error messages
            List pdaErrorMessages = getRefDataGenericsService().findTxEntityFiltered(PdaErrorMessageTxEntity.class, (Filter) null);
            helpForm.setPdaErrorMessages(pdaErrorMessages);
            //pda market info
            List pdaMarketInfos = getRefDataGenericsService().findTxEntityFiltered(PdaMarketinfoTxEntity.class, (Filter) null);
            helpForm.setPdaMarketInfos(pdaMarketInfos);
            //currency
            List currencies = getRefDataGenericsService().findTxEntityFiltered(CurrencyTxEntity.class, (Filter) null);
            helpForm.setCurrencies(currencies);
            //fuel type
            List fuelTypes = getRefDataGenericsService().findTxEntityFiltered(FuelTypeTxEntity.class, (Filter) null);
            helpForm.setFuelTypes(fuelTypes);
            //waiting time reason
            List waitingtimeReasons = getRefDataGenericsService().findTxEntityFiltered(WaitingtimeReasonTxEntity.class, (Filter) null);
            List waitingtimeReasonsShippingpoint = new ArrayList();
            List waitingtimeReasonsShipto = new ArrayList();
            for (Iterator it=waitingtimeReasons.iterator();it.hasNext();) {
            	WaitingtimeReasonTxEntity waitingtimeReason = (WaitingtimeReasonTxEntity)it.next(); 
            	if (waitingtimeReason.getWaitingtimeReasonTxPK().getWaitingtimeReasonBs().getCtlActCd()) {
            		if (waitingtimeReason.getWaitingtimeReasonTxPK().getWaitingtimeReasonBs().getAtShippingpointFlag()) {
            			waitingtimeReasonsShippingpoint.add(waitingtimeReason);
            		}
            		if (waitingtimeReason.getWaitingtimeReasonTxPK().getWaitingtimeReasonBs().getAtShiptoFlag()) {
            			waitingtimeReasonsShipto.add(waitingtimeReason);
            		}
            	}
            	
            }
            helpForm.setWaitingtimeReasonsShippingpoint(waitingtimeReasonsShippingpoint);
            helpForm.setWaitingtimeReasonsShipto(waitingtimeReasonsShipto);
            //rfid object type
            List rfidobjectTypes = getRefDataGenericsService().findTxEntityFiltered(RfidobjectTypeTxEntity.class, (Filter) null);
            helpForm.setRfidobjectTypes(rfidobjectTypes);
            //waiting type at shippingpoint comes from configbean
            String waitingTimeAtShippingpoint = getConfigBean().getWsclientMopWaitingTimeAtShippingpoint();
            helpForm.setWaitingTimeAtShippingpoint(waitingTimeAtShippingpoint);
            //waiting type at shipto comes from configbean
            String waitingTimeAtShipTo = getConfigBean().getWsclientMopWaitingTimeAtShipTo();
            helpForm.setWaitingTimeAtShipTo(waitingTimeAtShipTo);
            
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
