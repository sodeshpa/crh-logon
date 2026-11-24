/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.masterdata;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.forms.masterdata.LoadingPointBean;
import com.crh.logon.admin.web.forms.masterdata.ShippingPointForm;
import com.crh.logon.admin.web.forms.masterdata.ShippingPointRFIDPopupForm;
import com.crh.logon.business.services.interfaces.RFIDService;
import com.crh.logon.business.services.interfaces.RefDataGenericsService;
import com.crh.logon.business.services.localinterfaces.RFIDServiceLocalInterface;
import com.crh.logon.business.services.utils.RFIDBean;
import com.crh.logon.business.services.utils.RFIDBean.AbstractRFIDBean;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.common.RfidtagEntity;
import com.crh.logon.persistence.entities.refdata.RfidobjectTypeBsEntity;
import com.crh.logon.web.actions.RFIDPopupAction;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.ServiceLocatorProvider;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_ShippingPointRFIDPopupForm" path="/ShippingPointRFIDPopup" scope="session"
 *  validate="true" input="ShippingPointRFIDPopupTile.def"
 *
 * @struts.action-forward name="shipPointRFIDPopupEntry" path="/ShippingPointRFIDPopupEntry.do" 
 * @struts.action-forward name="list"  path="ShippingPointRFIDPopupTile.def"
 * @struts.action-forward name="close" path="ShippingPointRFIDPopupClose.def"
 */
public class ShippingPointRFIDPopupAction extends AdminActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ShippingPointRFIDPopupAction.class);

	private RFIDServiceLocalInterface rfidService;
	
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
			ServiceLocator serviceLocator = ServiceLocatorProvider.getServiceLocator();
			rfidService = serviceLocator.getLocalRFIDService();
			// Get the struts form(s).
			ShippingPointRFIDPopupForm helpForm = (ShippingPointRFIDPopupForm) form;
			ShippingPointForm spForm = (ShippingPointForm) request.getSession().getAttribute("admin_ShippingPointForm");
			String forwardName = "list";
			
			if (request.getParameter("save") != null) {
				if (logDebugEnabled) {
					LOG.debug("Action: save button is pressed");
				}
			
				LoadingPointBean lpBean = spForm.getLoadingPointWrapperList().get(helpForm.getLpIndex());
			
				//generate the object label for the attached rfid object (if it exists at all) will set the label
				//RFIDBean.generateRFIDObjectLabel(lpBean.getLpEntity());
				AbstractRFIDBean bean = RFIDBean.LoadingpointRFIDBean(lpBean.getLpEntity());
				RFIDPopupAction.validateRFIDObject(request, bean, rfidService);
			
				//in case it is a first item
				if (lpBean.getLpEntity().getRfidobject() == null) {
					RfidobjectEntity rfidObj = new RfidobjectEntity();
					RefDataGenericsService refServ = getServiceLocator().getRefDataGenericsService();
					RfidobjectTypeBsEntity bs = refServ.findBsEntity(RfidobjectTypeBsEntity.class, Long.valueOf(Constant.RFIDOBJECT_TYPE_LOADINGPOINT_SILO));
					rfidObj.setRfidobjectType(bs);
					lpBean.getLpEntity().setRfidobject(rfidObj);
				}
			
				//in case it is a first item in the list
				if (lpBean.getLpEntity().getRfidobject().getRfids() == null) {
					lpBean.getLpEntity().getRfidobject().setRfids(new ArrayList());
				}
			
				//get rfid from db
				RFIDService rfidService = getServiceLocator().getRFIDService();
				RfidtagEntity rfidEntity = rfidService.fetchRFIDTagById(helpForm.getRfidTagId());
			
				rfidEntity.setRfidobject(lpBean.getLpEntity().getRfidobject());
				lpBean.getLpEntity().getRfidobject().getRfids().add(rfidEntity);
				//add rfid to the assigned list
				if (spForm.getUnassignedRfidSet().contains(rfidEntity)){
					spForm.getUnassignedRfidSet().remove(rfidEntity);
				}
				else {
					spForm.getAssignedRfidSet().add(rfidEntity);
				}
			
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
