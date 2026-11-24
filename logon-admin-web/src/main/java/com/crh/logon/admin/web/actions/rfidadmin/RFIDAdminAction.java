/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.rfidadmin;

import java.io.IOException;
import java.util.List;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.admin.web.forms.rfidadmin.RFIDAdminForm;
import com.crh.logon.business.services.interfaces.RFIDService;
import com.crh.logon.business.services.localinterfaces.CronJobServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.LocalServices;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.RfidtagEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


/**
 * @author AdNovum Informatik AG
 * @version $Revision$ $Date$
 * @struts.action name="admin_RFIDAdminForm" path="/rfidadmin/RFIDAdmin" scope="session" validate="true"
 * input="/rfidadmin/RFIDAdminDetailTile.def"
 * @struts.action-forward name="success" path="/rfidadmin/RFIDAdminTile.def"
 * @struts.action-forward name="reload" path="/rfidadmin/RFIDAdminEntry.do"
 * @struts.action-forward name="detail" path="/rfidadmin/RFIDAdminDetailTile.def"
 */
public class RFIDAdminAction extends ActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RFIDAdminAction.class);

	private static final String FORWARD_DETAIL = "detail";

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		// [[
		ActionForward result = null;
		Object[][] params = { { "mapping", mapping },
				{ "form", form },
				{ "request", request },
				{ "response", response } };
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
			setSelectedSub2Tab("rfidmaintenance", request);
			RFIDAdminForm helpForm = (RFIDAdminForm) form;

			RFIDService service = getServiceLocator().getRFIDService();

			RfidtagEntity rfidTag;

			if (request.getParameter("search") != null) {

				Long rfidObjectTypeFilter = null;
				String selectedRfidObjectTypeFilter = helpForm.getObjectLabelFilter();
				if (!"-1".equals(selectedRfidObjectTypeFilter)) {
					rfidObjectTypeFilter = Long.valueOf(selectedRfidObjectTypeFilter);
				}
				String rfidIdentity = helpForm.getRfidIdentityFilter();
				String name = helpForm.getNameFilter();
				String objectLabel = helpForm.getObjectLabelFilter();
				boolean active = true; // TODO (istvan Jun 9, 2009): set correctly

				List rfidTagList = service.fetchRfidDetailList(rfidObjectTypeFilter, rfidIdentity, name, objectLabel, active,
						request.getLocale().getLanguage());
				helpForm.setRfidList(rfidTagList);

			}
			else if (request.getParameter("new") != null) {
				helpForm.clear();
				forwardName = FORWARD_DETAIL;
			}
			else if (request.getParameter("labelCleanup") != null) {
				CronJobServiceLocalInterface cronJobService = null;
				try {
					cronJobService = (CronJobServiceLocalInterface) LocalServices
							.getLocalInterfaceFor("CronJobService", CronJobServiceLocalInterface.class.getName());
				}
				catch (NamingException e) {
					throw new LogonException(e, Reason.RES_ERROR_INIT_SCV_LOOKUP);
				}

				try {
					cronJobService.callCronJob(Constant.CRONSTATUS_RFID_OBJECT_LABEL_CLEANUP);
					MessageUtils.addActionMessage(request, "rfidadmin.info.cleanupsuccess");
				}
				catch (Exception e) {
					LOG.error("RFID sync failed." + e);
					MessageUtils.addActionError(request, "rfidadmin.info.cleanupfailed");
				}

				forwardName = "reload";
			}
			else if (request.getParameter("cancel") != null) {
				helpForm.clear();
			}
			else if (request.getParameter("save") != null) {
				boolean validationFailed = storeRFIDTag(request, helpForm, service);

				if (validationFailed) {
					forwardName = FORWARD_DETAIL;
				}
				else {
					forwardName = "reload";
				}
			}
			else if ("modify".equals(helpForm.getInputAction())) {
				String rfidTagId = helpForm.getRfidtagId();
				Long rfidTagIdLong = Long.valueOf(rfidTagId);
				rfidTag = service.fetchRFIDTagById(rfidTagIdLong);
				helpForm.setRfidTag(rfidTag);
				mapRFIDTagToForm(helpForm, rfidTag);
				forwardName = FORWARD_DETAIL;
			}

			result = mapping.findForward(forwardName);
			return result;

		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}


	private boolean storeRFIDTag(HttpServletRequest request, RFIDAdminForm helpForm, RFIDService service) {
		// [[
		boolean result = false;
		Object[][] params = { { "request", request },
				{ "helpForm", helpForm },
				{ "service", service } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "storeRFIDTag", params);
		}
		// ]]
		try {
			RfidtagEntity rfidTag = helpForm.getRfidTag();

			boolean active = helpForm.isActive();
			String name = helpForm.getName();
			String rfidIdentity = helpForm.getRfidIdentity();

			// TODO (istvan Jun 9, 2009): add validation code here or to form
			try {

				if (rfidTag == null) {
					rfidTag = new RfidtagEntity();
					rfidTag.setActiveFlag(active);
					rfidTag.setName(name);
					rfidTag.setRfidIdentity(rfidIdentity);

					service.persistRFIDTag(rfidTag);
				}
				else {
					rfidTag.setActiveFlag(active);
					rfidTag.setName(name);
					rfidTag.setRfidIdentity(rfidIdentity);

					service.mergeRFIDTag(rfidTag);
				}

				MessageUtils.addActionMessage(request, "rfidadmin.info.saved");
				return result;
			}
			catch (RuntimeException re) {
				if (re.getCause() instanceof LogonException) {
					LogonException le = (LogonException) re.getCause();
					Reason reason = le.getReason();
					if (reason.getReason().equals(Reason.RES_RFIDTAG_IDENTITY_NOT_UNIQUE.getReason())) {
						MessageUtils.addActionWarning(request, le.getMessage());
						result = true;
						return result;
					}
					else {
						throw le;
					}
				}
				else {
					throw re;
				}
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private void mapRFIDTagToForm(RFIDAdminForm helpForm, RfidtagEntity rfidTag) {
		helpForm.setActive(rfidTag.getActiveFlag());
		helpForm.setRfidIdentity(rfidTag.getRfidIdentity());
		helpForm.setName(rfidTag.getName());
	}
}
