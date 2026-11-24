/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.masterdata;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.forms.masterdata.SalesAreaDetailForm;
import com.crh.logon.business.services.interfaces.DeliveryDateService;
import com.crh.logon.business.services.interfaces.SalesAreaService;
import com.crh.logon.business.services.utils.DeliveryDistanceValidationUtils;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.validation.ValidationErrorReason;
import com.crh.logon.constants.CallerContext;
import com.crh.logon.dto.DistanceValidationEditGuiBean;
import com.crh.logon.persistence.entities.views.SareaDisplayEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_SalesAreaDetailForm" path="/SalesAreaDetail" scope="session"
 *  validate="true" input="SalesAreaDetailTile.def"
 *
 * @struts.action-forward name="salesAreaDetailEntry" path="/SalesAreaDetailEntry.do" 
 * @struts.action-forward name="success" path="SalesAreaDetailCloseTile.def"
 * @struts.action-forward name="close" path="SalesAreaDetailCloseTile.def"
 * @struts.action-forward name="error" path="SalesAreaDetailTile.def"
 */
public class SalesAreaDetailAction extends AdminActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SalesAreaDetailAction.class);

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
			Authorization.authorize(Authorization.MASTER_DATA_GLOBAL_SALES_AREA);
		    // Get the struts form.
			SalesAreaDetailForm helpForm = (SalesAreaDetailForm) form;
			String forwardName = FORWARD_CLOSE;
			
			if (FORWARD_SAVE.equals(helpForm.getFormAction())) {
				helpForm.setFormAction("");
				if (logDebugEnabled) {
					LOG.debug("Action: submit save button clicked!");
				}
				
				validateForSave(helpForm, request);
				
				if (MessageUtils.getActionWarnings(request) != null && MessageUtils.getActionWarnings(request).size() > 0) {
					return result = mapping.findForward(FORWARD_ERROR);
			    }
			
				ActionErrors errors = MessageUtils.getActionErrors(request);
				if (errors == null || errors.isEmpty()) {
					SalesAreaService salesAreaService = getServiceLocator().getSalesAreaService();
				
					SareaDisplayEntity salesAreaSettings = helpForm.getDisplayEntity();
					
					salesAreaService.updateSalesArea(salesAreaSettings);
					Long salesAreaId = salesAreaSettings.getSareaId();
					salesAreaService.maintainPlanningMaterialContractItems(salesAreaId);
					return result = mapping.findForward(FORWARD_SUCCESS);
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

	private void validateForSave(SalesAreaDetailForm helpForm, HttpServletRequest request) {

		SareaDisplayEntity displayEntity = helpForm.getDisplayEntity();
		Long allowNdInMinutes = null;
		Long applyNdInMinutes = null;
		Long allowSdInMinutes = null;
		Long applySdInMinutes = null;

		Long h1EarlyOrderLimitMinutes = null;
		Long h2EarliestDeliveryFromMinutes = null;

		try {
			// Opening times
			Date open1From = DateTimeUtils.parseHoursMinutes(helpForm.getShiptoOpen1From());
			Date open1To = DateTimeUtils.parseHoursMinutes(helpForm.getShiptoOpen1To());
			Date open2From = DateTimeUtils.parseHoursMinutes(helpForm.getShiptoOpen2From());
			Date open2To = DateTimeUtils.parseHoursMinutes(helpForm.getShiptoOpen2To());

			if (open1From != null) {
				if (open1To != null && open1To.before(open1From)) {
					MessageUtils.addActionWarning(request, "salesarea.errors.openwrongorder");
				}
				if (open2From != null && open2From.before(open1From)) {
					MessageUtils.addActionWarning(request, "salesarea.errors.openwrongorder");
				}
				if (open2To != null && open2To.before(open1From)) {
					MessageUtils.addActionWarning(request, "salesarea.errors.openwrongorder");
				}
			}
			if (open1To != null) {
				if (open2From != null && open2From.before(open1To)) {
					MessageUtils.addActionWarning(request, "salesarea.errors.openwrongorder");
				}
				if (open2To != null && open2To.before(open1To)) {
					MessageUtils.addActionWarning(request, "salesarea.errors.openwrongorder");
				}
			}
			if (open2From != null) {
				if (open2To != null && open2To.before(open2From)) {
					MessageUtils.addActionWarning(request, "salesarea.errors.openwrongorder");
				}
			}

			displayEntity.setShiptoOpen1From(DateTimeUtils.getMinAfterMidnight(open1From));
			displayEntity.setShiptoOpen1To(DateTimeUtils.getMinAfterMidnight(open1To));
			displayEntity.setShiptoOpen2From(DateTimeUtils.getMinAfterMidnight(open2From));
			displayEntity.setShiptoOpen2To(DateTimeUtils.getMinAfterMidnight(open2To));

			// Bimodal creation allowed until
			Date allowBimodalCreationTill = DateTimeUtils.parseHoursMinutes(helpForm.getAllowBimodalCreationTillMi());
			displayEntity.setAllowBimodalCreationTillMi(DateTimeUtils.getMinAfterMidnight(allowBimodalCreationTill));

			allowNdInMinutes = DateTimeUtils.getMinutesAfterMidnightFromDateString(helpForm.getAllowNdWebOrderUntilMi());
			applyNdInMinutes = DateTimeUtils.getMinutesAfterMidnightFromDateString(helpForm.getApplyNdLeadtimeChkUntilMi());
			allowSdInMinutes = DateTimeUtils.getMinutesAfterMidnightFromDateString(helpForm.getAllowSdWebOrderUntilMi());
			applySdInMinutes = DateTimeUtils.getMinutesAfterMidnightFromDateString(helpForm.getApplySdLeadtimeChkFromMi());

			if (Boolean.TRUE.equals(displayEntity.getEnableDemandPlanningFlag())) {
				displayEntity.setDemandPlanLatestUntilH(
						Optional.ofNullable(displayEntity.getDemandPlanLatestUntilH()).filter(it -> it >= 0).orElse(0));
			} else {
				displayEntity.setDemandPlanLatestUntilH(null);
			}

		}
		catch (ParseException e) {
			MessageUtils.addActionWarning(request, "salesarea.errors.badTimeFormat");
		}

		passValuesToEntity(helpForm, request, displayEntity);


		DeliveryDateService deliveryDateService = getDeliveryDateService();
		/**
		 * sd and nd lead time sanity check
		 */
		Collection<ValidationErrorReason> reasons = new ArrayList<ValidationErrorReason>();
		ValidationErrorReason validationErrorReason = null;
			validationErrorReason = deliveryDateService.validateSareaDateConstants(allowNdInMinutes,

					applyNdInMinutes, allowSdInMinutes, applySdInMinutes);
		if (validationErrorReason == null) {
			displayEntity.setAllowNdWebOrderUntilMi(allowNdInMinutes);
			displayEntity.setApplyNdLeadtimeChkUntilMi(applyNdInMinutes);
			displayEntity.setAllowSdWebOrderUntilMi(allowSdInMinutes);
			displayEntity.setApplySdLeadtimeChkFromMi(applySdInMinutes);
		}
		else {
			reasons.add(validationErrorReason);
		}

		/**
		 * Distance based validation fields sanity check
		 */
			DeliveryDistanceValidationUtils deliveryDistanceValidationUtils = new DeliveryDistanceValidationUtils();
			List<ValidationErrorReason> distanceErrorReasons =
					deliveryDistanceValidationUtils.sanityCheckSalesAreEdit(displayEntity, false);
			reasons.addAll(distanceErrorReasons);


		/**
		 * Sales area validation
		 */

		Boolean allowPlanningMaterialFlag = displayEntity.getAllowPlanningMaterialFlag();
		if (allowPlanningMaterialFlag) {
			SalesAreaService salesAreaService = getSalesAreaService();
			Collection<ValidationErrorReason> errorReasons = salesAreaService.validateSalesArea(displayEntity.getSorgCd(),
					displayEntity.getDistributionChannel());
			if (errorReasons.size() > 0) {
				reasons.addAll(errorReasons);
			}
		}

		processValidationErrorReasonsAndTrace(request, reasons, CallerContext.CALL_CENTER, false);


		// Email addresses
		if (StringUtils.notEmpty(displayEntity.getMailFrom())
				&& displayEntity.getMailFrom().length() > SareaDisplayEntity.DBSIZE_MAIL_FROM) {
			MessageUtils.addActionWarning(request, "salesarea.errors.toolongEmailAddress",
					Long.toString(SareaDisplayEntity.DBSIZE_MAIL_FROM));
		}
		if (StringUtils.notEmpty(displayEntity.getMailRailadmin())
				&& displayEntity.getMailRailadmin().length() > SareaDisplayEntity.DBSIZE_MAIL_RAILADMIN) {
			MessageUtils.addActionWarning(request, "salesarea.errors.toolongEmailAddress",
					Long.toString(SareaDisplayEntity.DBSIZE_MAIL_RAILADMIN));
		}
		if (StringUtils.notEmpty(displayEntity.getMailTestTo())
				&& displayEntity.getMailTestTo().length() > SareaDisplayEntity.DBSIZE_MAIL_TEST_TO) {
			MessageUtils.addActionWarning(request, "salesarea.errors.toolongEmailAddress",
					Long.toString(SareaDisplayEntity.DBSIZE_MAIL_TEST_TO));
		}
		if (StringUtils.notEmpty(displayEntity.getMailBcc())
				&& displayEntity.getMailBcc().length() > SareaDisplayEntity.DBSIZE_MAIL_BCC) {
			MessageUtils.addActionWarning(request, "salesarea.errors.toolongEmailAddress",
					Long.toString(SareaDisplayEntity.DBSIZE_MAIL_BCC));
		}
		if (!LogonUtil.validEmail(displayEntity.getMailFrom())) {
			MessageUtils.addActionWarning(request, "salesarea.errors.invalidEmailAddress", displayEntity.getMailFrom());
		}
		if (!LogonUtil.validEmail(displayEntity.getMailRailadmin())) {
			MessageUtils.addActionWarning(request, "salesarea.errors.invalidEmailAddress", displayEntity.getMailRailadmin());
		}
		if (!LogonUtil.validEmail(displayEntity.getMailTestTo())) {
			MessageUtils.addActionWarning(request, "salesarea.errors.invalidEmailAddress", displayEntity.getMailTestTo());
		}
		if (!LogonUtil.validEmail(displayEntity.getMailBcc())) {
			MessageUtils.addActionWarning(request, "salesarea.errors.invalidEmailAddress", displayEntity.getMailBcc());
		}
	}

	private void passValuesToEntity(com.crh.logon.admin.web.forms.masterdata.SalesAreaDetailForm helpForm,
			HttpServletRequest request, SareaDisplayEntity displayEntity) {
		try {
			// short-term order threshold
			Long shortTermOrderThreshold = null;

			if (!StringUtils.empty(helpForm.getShortTermOrderThresholdMi())) {
				shortTermOrderThreshold = Long.valueOf(helpForm.getShortTermOrderThresholdMi());
			}

			displayEntity.setShortTermOrderThresholdMi(shortTermOrderThreshold);

			// Exact Customer Order Quantity Tolerance [kg]
			Long exactQuantityOrderTolerance = null;

			if (!StringUtils.empty(helpForm.getExactQuantityOrderTolerance())) {
				exactQuantityOrderTolerance = Long.valueOf(helpForm.getExactQuantityOrderTolerance());
			}

			displayEntity.setExactQuantityOrderTolerance(exactQuantityOrderTolerance);

			DistanceValidationEditGuiBean distanceValidationGuiBean = helpForm.getDistanceValidationGuiBean();
			distanceValidationGuiBean.transmitValuesToSareaEntity(displayEntity);
		}
		catch (NumberFormatException e) {
			MessageUtils.addActionWarning(request, "salesarea.errors.badNumberFormat");
		}
	}

}
