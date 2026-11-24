/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.refdataeventtopda;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.refdataeventtopda.RefDataEventToPDAForm;
import com.crh.logon.business.services.interfaces.MopEventService;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;


/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_RefDataEventToPDAForm"
 *  path="/refdataeventtopda/RefDataEventToPDA" scope="session"
 *  validate="true" input="/refdataeventtopda/RefDataEventToPDATile.def"
 *
 * @struts.action-forward name="success" path="/refdataeventtopda/RefDataEventToPDATile.def"
 * @struts.action-forward name="failure" path="/refdataeventtopda/RefDataEventToPDATile.def"
 * 
 */
public class RefDataEventToPDAAction extends ActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(RefDataEventToPDAAction.class);
    
    public static final String FORWARD_ = "";
    
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
			String forwardName = "success";
			
			Authorization.authorize(Authorization.MASTER_DATA_RFID_MAINTENANCE);
			
			setSelectedTab("masterdata", request);
			setSelectedSub1Tab("pda", request);
			setSelectedSub2Tab("refdataeventtopda", request);
			RefDataEventToPDAForm helpForm = (RefDataEventToPDAForm) form;
			
			ServiceLocator serviceLocator = getServiceLocator();
			MopEventService mopEventService = serviceLocator.getMopEventService();
			
			String inputAction = helpForm.getInputAction();
			
			if ("sendSingle".equals(inputAction)) {
			
				int type = helpForm.getEntityType();
				String id = helpForm.getEntityId();
				
				if (StringUtils.empty(id)) {
					MessageUtils.addActionError(request, "refdataeventtopda.errors.invalid.entityid");
					return result = mapping.findForward("failure");
				}
				
				try {
					switch (type) {
					case RefDataEventToPDAForm.ENTITY_WAITING_TIME_AT_SHIPPINGPOINT:
						mopEventService.sendWaitingtimeAtShippingpointToComServer();
						break;
					case RefDataEventToPDAForm.ENTITY_WAITING_TIME_AT_SHIPTO:
						mopEventService.sendWaitingtimeAtShiptoToComServer();
						break;
					default:
						LOG.error("Unsupported refdata entity type : " + type);
						MessageUtils.addActionError(request, "refdataeventtopda.errors.invalid.entityid");
						return result = mapping.findForward("failure");
					}
				}
				catch (NumberFormatException e) {
					LOG.error("Unsupported refdata entity type : " + type);
					MessageUtils.addActionError(request, "refdataeventtopda.errors.invalid.entityid");
					return result = mapping.findForward("failure");
				}
				MessageUtils.addActionMessage(request, "refdataeventtopda.info.sendsinglesuccess");
			}
			else if ("sendSet".equals(inputAction)) {
				int type = helpForm.getEntityType();
				try {
					switch (type) {
					case RefDataEventToPDAForm.ENTITY_LANG:
					    mopEventService.sendLangDataSetToComServer();
						break;
					case RefDataEventToPDAForm.ENTITY_FUNC:
					    mopEventService.sendFuncDataSetToComServer();
						break;
					case RefDataEventToPDAForm.ENTITY_TRUCK_MAINTENANCE:
					    mopEventService.sendTruckMaintenanceDataSetToComServer();
						break;
					case RefDataEventToPDAForm.ENTITY_PDA_ERROR_MESSAGE:
					    mopEventService.sendPdaErrorMessageDataSetToComServer();
						break;
					case RefDataEventToPDAForm.ENTITY_PDA_MARKET_INFO:
					    mopEventService.sendPdaMarketInfoDataSetToComServer();
						break;
					case RefDataEventToPDAForm.ENTITY_CURRENCY:
					    mopEventService.sendCurrencyDataSetToComServer();
						break;
					case RefDataEventToPDAForm.ENTITY_FUEL_TYPE:
					    mopEventService.sendFuelTypeDataSetToComServer();
						break;
					case RefDataEventToPDAForm.ENTITY_WAITINGTIMEREASON_SHIPPINGPOINT:
					    mopEventService.sendWaitingtimeReasonShippingpointDataSetToComServer();
						break;
					case RefDataEventToPDAForm.ENTITY_WAITINGTIMEREASON_SHIPTO:
					    mopEventService.sendWaitingtimeReasonShiptoDataSetToComServer();
						break;
					case RefDataEventToPDAForm.ENTITY_RFIDOBJECT_TYPE:
					    mopEventService.sendRfidobjectTypeDataSetToComServer();
						break;
						
					default:
						LOG.error("Unsupported refdata entity type : " + type);
						MessageUtils.addActionError(request, "refdataeventtopda.errors.invalid.entityid");
						return result = mapping.findForward("failure");
					}
				}
				catch (NumberFormatException e) {
					LOG.error("Unsupported refdata entity type : " + type);
					MessageUtils.addActionError(request, "refdataeventtopda.errors.invalid.entityid");
					return result = mapping.findForward("failure");
				}
				MessageUtils.addActionMessage(request, "refdataeventtopda.info.sendsetsuccess");
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
