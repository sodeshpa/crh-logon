/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.masterdata;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.forms.masterdata.ShippingPointForm;
import com.crh.logon.business.services.utils.TimeZone;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.refdata.RfidobjectTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.ShipppointTypeTxEntity;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_ShippingPointForm" path="/ShippingPointEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="ShippingPointTile.def"
 * @struts.action-forward name="failure" path="ShippingPointTile.def"
 */
public class ShippingPointEntryAction extends AdminActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ShippingPointEntryAction.class);

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
			ShippingPointForm helpForm;
			String forwardName = "success";
			setSelectedTab("masterdata", request);
			setSelectedSub1Tab("global", request);
			setSelectedSub2Tab("shippingpoint", request);
			
			// Get the struts form.
			helpForm = (ShippingPointForm) form;
			helpForm.setFormAction("");
			helpForm.clear();
			RfidobjectTypeBsEntity rfidObjectType = getServiceLocator().getRefDataGenericsService().findBsEntity(RfidobjectTypeBsEntity.class, Constant.RFIDOBJECT_TYPE_LOADINGPOINT_SILO);
			helpForm.setRfidType(rfidObjectType);
			
			// load shippingpoint types
			List spTypeList = getServiceLocator().getRefDataGenericsService().findTxEntityList(ShipppointTypeTxEntity.class, true, request.getLocale(), ShipppointTypeTxEntity.ATTR_TXT, true);
			Map spTypeMap = new HashMap();
			if (spTypeList != null) {
				ListIterator spTypeIt = spTypeList.listIterator();
				while (spTypeIt.hasNext()) {
					ShipppointTypeTxEntity spTypeEntity = (ShipppointTypeTxEntity) spTypeIt.next();
					spTypeMap.put(spTypeEntity.getShipppointTypeTxPK().getShipppointTypeBs().getShipppointTypeCd(), spTypeEntity.getShipppointTypeTxPK().getShipppointTypeBs());
				}
				helpForm.setShippingPointTypeMap(spTypeMap);
			}

			Locale locale = getUserLocale();
			helpForm.setUserLocale(locale);

			List<TimeZone> zones = getTimeZoneService().loadTimeZonesForLocale(locale);
			Collections.sort(zones, new TimeZone.AscComparator(locale));
			helpForm.setTimeZoneList(zones);
			
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
