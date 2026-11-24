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

import com.crh.logon.admin.web.actions.AdminActionSupport;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.beans.ShippingPointCreateBean;
import com.crh.logon.admin.web.forms.masterdata.ShippingPointCreatePopupForm;
import com.crh.logon.business.services.interfaces.RefDataGenericsService;
import com.crh.logon.business.services.interfaces.ShippingPointService;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.SapShippingCondBsEntity;
import com.crh.logon.persistence.entities.refdata.ShipppointTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.WeighSystemSpBsEntity;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_ShippingPointCreatePopupForm" path="/ShippingPointCreatePopup" scope="session"
 *  validate="true" input="ShippingPointCreatePopupTile.def"
 *
 * @struts.action-forward name="shippingPointCreatePopupEntry" path="/ShippingPointCreatePopupEntry.do"
 * @struts.action-forward name="save" path="ShippingPointCreatePopupTile.def"
 * @struts.action-forward name="close" path="ShippingPointCreatePopupClose.def"
 * @struts.action-forward name="error" path="ShippingPointCreatePopupTile.def"
 */
public class ShippingPointCreatePopupAction extends AdminActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ShippingPointCreatePopupAction.class);

	public static final String FORWARD_SUCCESS = "success";
	public static final String FORWARD_SAVE = "save";
	public static final String FORWARD_ERROR = "error";
	public static final String FORWARD_CLOSE = "close";

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
			Authorization.authorize(Authorization.MASTER_DATA_GLOBAL_SHIPPINGPOINT_CREATE);
			ShippingPointCreatePopupForm helpForm;
			String forwardName = FORWARD_CLOSE;
			helpForm = (ShippingPointCreatePopupForm) form;

			if (FORWARD_SAVE.equals(helpForm.getCreateFormAction())) {
				helpForm.setCreateFormAction("");
				if (logDebugEnabled) {
					LOG.debug("Action: submit save button clicked!");
				}

				ActionErrors errors = MessageUtils.getActionErrors(request);
				if (errors == null || errors.isEmpty()) {
					ShippingPointCreateBean shippingPointCreateBean = helpForm.getShippingPointCreateBean();
					ShippingPointService shippingPointService = getServiceLocator().getShippingPointService();

					String plantCd = shippingPointCreateBean.getPlantCd();
					RefDataGenericsService refDataService = getServiceLocator().getRefDataGenericsService();
					PlantBsEntity plant = refDataService.findBsEntity(PlantBsEntity.class, plantCd);

					ShippingpointEntity shippingpoint = new ShippingpointEntity();
					shippingpoint.setPlant(plant);
					shippingpoint.setSapShippingpointNo(shippingPointCreateBean.getSapShippingpointNo());
					shippingpoint.setShippingpointName(shippingPointCreateBean.getShippingpointName());
					
					// setting predefined default values
					shippingpoint.setKnownAlsoInSapFlag(true);
					ShipppointTypeBsEntity shipppointType = refDataService.findBsEntity(ShipppointTypeBsEntity.class, Long.valueOf(0)); // Road
					shippingpoint.setShipppointType(shipppointType);
					SapShippingCondBsEntity sapShippingCond = refDataService.findBsEntity(SapShippingCondBsEntity.class, Constant.SHIPPINGPOINT_CONDITION_TRUCK);
					shippingpoint.setSapShippingCond(sapShippingCond);
					shippingpoint.setCoordFixFlag(true);
					shippingpoint.setFvlMixShiptoPerWagonFlag(false);
					shippingpoint.setWeighSystemSp(refDataService.findBsEntity(WeighSystemSpBsEntity.class, 0L)); // No LDS (only manual LOGON/PDA)
		
					long l = shippingPointService.countShippingpoint(shippingpoint.getSapShippingpointNo());
					if (l > 0){
						MessageUtils.addActionWarning(request, "shippingpoint.message.alreadyExists");
						return result = mapping.findForward(FORWARD_ERROR);	
					}
					
					shippingpoint = shippingPointService.createShippingpoint(shippingpoint);
					shippingPointCreateBean.setShippingpointId(shippingpoint.getShippingpointId());
					MessageUtils.addActionMessage(request, "shippingpoint.message.newShippingpointCreated");
				}
				else {
					return result = mapping.findForward(FORWARD_ERROR);
				}
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
