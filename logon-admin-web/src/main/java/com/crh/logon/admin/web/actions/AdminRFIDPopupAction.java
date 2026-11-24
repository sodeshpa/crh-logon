/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.RFIDBean;
import com.crh.logon.business.services.utils.RFIDBean.AbstractRFIDBean;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.web.actions.RFIDPopupAction;
import com.crh.logon.web.utils.Authorization;

/**
 *	Calls RFIDPopupTile, which contains dropdown with all available rfidTags.
 *	It is all about attaching rfIdTags to rfIdObjects. Supposed that an rfIdObject is
 *	set to the session with the name RFIDPopupForm.SESSION_RFIDOBJECT_NAME. The selected rfidTag is removed
 *	from that object back to the list in case we remove it on RFIDsTile. The div named DIV_NAME
 *	is going to be reloaded each case of add/remove.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 * 
 *  @struts.action name="admin_RFIDPopupForm"
 *  path="/AdminRFIDPopup" scope="session"
 *  validate="false" input="/AdminRFIDPopupTile.def"
 *  
 * @struts.action-forward name="RFIDPopup" path="/AdminRFIDPopupTile.def"
 * @struts.action-forward name="RFIDsTile" path="/AdminRFIDsTile.def"
 */
public class AdminRFIDPopupAction extends RFIDPopupAction {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(AdminRFIDPopupAction.class);
	
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
			AbstractRFIDBean bean = (AbstractRFIDBean) request.getSession().getAttribute( RFIDBean.SESSION_KEY );
			RfidobjectEntity rfidObj = (RfidobjectEntity) bean.getRfidObject();
			Object containingObject = bean.getContextObject();
			Long rfidobjectTypeCd = rfidObj.getRfidobjectType().getRfidobjectTypeCd();
			switch (rfidobjectTypeCd.intValue()) {
			case (int)Constant.RFIDOBJECT_TYPE_TRUCK:
			    if (containingObject != null && !(containingObject instanceof TruckEntity)) {
			        throw new LogonException();
			    }
			    Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRUCK);
			    break;
			
            case (int)Constant.RFIDOBJECT_TYPE_TRAILER:
                if (containingObject != null && !(containingObject instanceof TrailerEntity)) {
                    throw new LogonException();
                }
                Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRAILER);
                break;
			
            case (int)Constant.RFIDOBJECT_TYPE_CONTAINER:
                if (containingObject != null && !(containingObject instanceof ContainerEntity)) {
                    throw new LogonException();
                }
                Authorization.authorize(Authorization.MASTER_DATA_ROAD_CONTAINER);
                break;
			
            case (int)Constant.RFIDOBJECT_TYPE_SHIPTO_SILO:
                if (containingObject != null && (!(containingObject instanceof ShiptoSiloEntity) || (!((ShiptoSiloEntity)containingObject).getPortableSiloFlag()))) {
                    throw new LogonException();
                }
                Authorization.authorize(Authorization.MASTER_DATA_ROAD_SILO);
                break;
			
            case (int)Constant.RFIDOBJECT_TYPE_LOADINGPOINT_SILO:
                if (containingObject != null && !(containingObject instanceof LoadingpointEntity)) {
                    throw new LogonException();
                }
                Authorization.authorize(Authorization.MASTER_DATA_GLOBAL_SHIPPINGPOINT);
                break;
			
            case (int)Constant.RFIDOBJECT_TYPE_WAGON:
                if (containingObject != null && !(containingObject instanceof WagonEntity)) {
                    throw new LogonException();
                }
                Authorization.authorize(Authorization.MASTER_DATA_RAIL_CARS);
                break;
			
            case (int)Constant.RFIDOBJECT_TYPE_PDA_USER:
                if (containingObject != null && !(containingObject instanceof UserEntity)) {
                    throw new LogonException();
                }
                Authorization.authorize(Authorization.PUBLIC_FUNCTION);
                break;
			
			default:
			    throw new LogonException("Could not recognise the rfid object type code.", Reason.RES_ERROR_IMPL);
			}
			ActionForward forward = super.execute(mapping, form, request, response);
			
			return result = forward;
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
