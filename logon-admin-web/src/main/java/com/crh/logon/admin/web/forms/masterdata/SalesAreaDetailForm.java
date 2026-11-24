/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.masterdata;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.dto.DistanceValidationEditGuiBean;
import com.crh.logon.persistence.entities.views.SareaDisplayEntity;
import com.crh.logon.utils.time.DateTimeUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_SalesAreaDetailForm"
 */
public class SalesAreaDetailForm extends ActionForm {

	private static final long serialVersionUID = -4172910147603503279L;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SalesAreaDetailForm.class);
	public static final String SESSION_SALES_AREA_DETAIL_FORM_NAME = "admin_SalesAreaDetailForm";
	
	// input from the user
	private String formAction;
	private String shiptoOpen1From;
	private String shiptoOpen1To;
	private String shiptoOpen2From;
	private String shiptoOpen2To;
	private String shortTermOrderThresholdMi;
	private String allowBimodalCreationTillMi;
	private String allowSdWebOrderUntilMi;
	private String allowNdWebOrderUntilMi;
	private String applySdLeadtimeChkFromMi;
	private String applyNdLeadtimeChkUntilMi;
	private String exactQuantityOrderTolerance;
	private DistanceValidationEditGuiBean distanceValidationGuiBean;

    // db state
    private SareaDisplayEntity displayEntity;
    
    private Locale locale;

	/**
	 * Default constructor.
	 */
	public SalesAreaDetailForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "SalesAreaDetailForm");
		}
		// ]]
		try {
			init();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * Initializes the form. This has the effect of clearing all the input
	 * fields on the web form.
	 */
	private void init() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
			formAction = null;
			shiptoOpen1From = "";
			shiptoOpen1To = "";
			shiptoOpen2From = "";
			shiptoOpen2To = "";
			shortTermOrderThresholdMi = "";
			allowBimodalCreationTillMi = "";
			allowSdWebOrderUntilMi = "";
			allowNdWebOrderUntilMi = "";
			applySdLeadtimeChkFromMi = "";
			applyNdLeadtimeChkUntilMi = "";
			exactQuantityOrderTolerance = "";
			displayEntity = null;
			distanceValidationGuiBean = null;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * Clears all the input fields on the web form, discards any existing
	 * search results, and resets any existing pages. Called in response to
	 * clicking the reset button on the web form.
	 */
	public void clear() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "clear");
		}
		// ]]
		try {
			init();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * Override struts method.
	 * Note: this is not associated with or invoked by the reset button in the
	 * web form, but rather is called on every request before the struts action
	 * method gets control and, more importantly, before the request parameters
	 * are automatically copied into the form. The primary use is to reset
	 * checkboxes and struts multiboxes and the like for which the browser only
	 * sends the form parameters when they are set.
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// [[
		Object[][] params = {{"mapping", mapping},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "reset", params);
		}
		// ]]
		try {
			if (displayEntity != null){
				displayEntity.setDelnoteUsesWsNumberFlag(false);
				displayEntity.setDocumentDelTimeWindowFlag(false);
				displayEntity.setDocumentDistanceFlag(false);
				displayEntity.setDocumentCommCarrierFlag(false);
				displayEntity.setDocumentExecCarrierFlag(false);
				displayEntity.setAllowPlanningMaterialFlag(false);
				displayEntity.setCustCommFlag(false);
				displayEntity.setCsEnableOneTimeCustFlag(false);
				displayEntity.setCsShowPaymentMthFlag(false);
				displayEntity.setCsShowTotalsTextFlag(false);
				displayEntity.setCsShowCustTaxCodeFlag(false);
				displayEntity.setCsShowContractCurrencyFlag(false);
				displayEntity.setDnShowDispatcherDriverFlag(false);
				displayEntity.setDnShowFixText(false);
				displayEntity.setDnShowPlantCycleFlag(false);
				displayEntity.setDnShowSealsInfoFlag(false);
				displayEntity.setDnShowSiloNoFlag(false);
				displayEntity.setDnShowTrailerFlag(false);
				displayEntity.setDnShowCarrierCrmFlag(false);
				displayEntity.setDnShowShiptoContactFlag(false);
				displayEntity.setExpShowTotalTxtFlag(false);
				displayEntity.setExpShowLegalText1Flag(false);
				displayEntity.setExpShowLegalText2Flag(false);
				displayEntity.setExpShowIssueDatLocFlag(false);
				displayEntity.setExpShowAdInvoiceNoFlag(false);
				displayEntity.setExpShowCtrlNoFlag(false);
				displayEntity.setExpShowZcode12Flag(false);
				displayEntity.setEnablePricingCallFlag(false);
				displayEntity.setAllowWebOrderOnWeekend(0L);
				displayEntity.setRailReturnSareaFlag(false);
				displayEntity.setForceToSapFlag(false);
				displayEntity.setEnableDemandPlanningFlag(false);
				displayEntity.setDemandPlanLatestUntilH(null);
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * to be called by entry form after sales area and all reference data has been set
	 */
	public void populateForm() {
		if (displayEntity != null) {
			if (displayEntity.getShiptoOpen1From() != null) {
				shiptoOpen1From = DateTimeUtils.formatHoursMinutes( 
						DateTimeUtils.getHoursMinutes(displayEntity.getShiptoOpen1From()));
			}
			if (displayEntity.getShiptoOpen1To() != null) {
				shiptoOpen1To = DateTimeUtils.formatHoursMinutes( 
						DateTimeUtils.getHoursMinutes(displayEntity.getShiptoOpen1To()));
			}
			if (displayEntity.getShiptoOpen2From() != null) {
				shiptoOpen2From = DateTimeUtils.formatHoursMinutes( 
						DateTimeUtils.getHoursMinutes(displayEntity.getShiptoOpen2From()));
			}
			if (displayEntity.getShiptoOpen2To() != null) {
				shiptoOpen2To = DateTimeUtils.formatHoursMinutes( 
						DateTimeUtils.getHoursMinutes(displayEntity.getShiptoOpen2To()));
			}
			if (displayEntity.getAllowBimodalCreationTillMi() != null) {
				allowBimodalCreationTillMi = DateTimeUtils.formatHoursMinutes( 
						DateTimeUtils.getHoursMinutes(displayEntity.getAllowBimodalCreationTillMi()));
			}
			if (displayEntity.getShortTermOrderThresholdMi() != null) {
				shortTermOrderThresholdMi = displayEntity.getShortTermOrderThresholdMi().toString();
			}
			if (displayEntity.getAllowSdWebOrderUntilMi() != null) {
				allowSdWebOrderUntilMi = DateTimeUtils.formatHoursMinutes( 
						DateTimeUtils.getHoursMinutes(displayEntity.getAllowSdWebOrderUntilMi()));
			}
			if (displayEntity.getAllowNdWebOrderUntilMi() != null) {
				allowNdWebOrderUntilMi = DateTimeUtils.formatHoursMinutes( 
						DateTimeUtils.getHoursMinutes(displayEntity.getAllowNdWebOrderUntilMi()));
			}
			if (displayEntity.getApplySdLeadtimeChkFromMi() != null) {
				applySdLeadtimeChkFromMi = DateTimeUtils.formatHoursMinutes( 
						DateTimeUtils.getHoursMinutes(displayEntity.getApplySdLeadtimeChkFromMi()));
			}
			if (displayEntity.getApplyNdLeadtimeChkUntilMi() != null) {
				applyNdLeadtimeChkUntilMi = DateTimeUtils.formatHoursMinutes( 
						DateTimeUtils.getHoursMinutes(displayEntity.getApplyNdLeadtimeChkUntilMi()));
			}
			if (displayEntity.getExactQuantityOrderTolerance() != null) {
				exactQuantityOrderTolerance = displayEntity.getExactQuantityOrderTolerance().toString();
			}

			distanceValidationGuiBean = new DistanceValidationEditGuiBean(displayEntity);
		}
	}

	public String getFormAction() {
		return formAction;
	}

	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}

	public String getShiptoOpen1From() {
		return shiptoOpen1From;
	}

	public void setShiptoOpen1From(String shiptoOpen1From) {
		this.shiptoOpen1From = shiptoOpen1From;
	}

	public String getShiptoOpen1To() {
		return shiptoOpen1To;
	}

	public void setShiptoOpen1To(String shiptoOpen1To) {
		this.shiptoOpen1To = shiptoOpen1To;
	}

	public String getShiptoOpen2From() {
		return shiptoOpen2From;
	}

	public void setShiptoOpen2From(String shiptoOpen2From) {
		this.shiptoOpen2From = shiptoOpen2From;
	}

	public String getShiptoOpen2To() {
		return shiptoOpen2To;
	}

	public void setShiptoOpen2To(String shiptoOpen2To) {
		this.shiptoOpen2To = shiptoOpen2To;
	}

	public String getShortTermOrderThresholdMi() {
		return shortTermOrderThresholdMi;
	}

	public void setShortTermOrderThresholdMi(String shortTermOrderThresholdMi) {
		this.shortTermOrderThresholdMi = shortTermOrderThresholdMi;
	}

	public String getAllowBimodalCreationTillMi() {
		return allowBimodalCreationTillMi;
	}

	public void setAllowBimodalCreationTillMi(String allowBimodalCreationTillMi) {
		this.allowBimodalCreationTillMi = allowBimodalCreationTillMi;
	}

	public String getAllowSdWebOrderUntilMi() {
		return allowSdWebOrderUntilMi;
	}

	public void setAllowSdWebOrderUntilMi(String allowSdWebOrderUntilMi) {
		this.allowSdWebOrderUntilMi = allowSdWebOrderUntilMi;
	}

	public String getAllowNdWebOrderUntilMi() {
		return allowNdWebOrderUntilMi;
	}

	public void setAllowNdWebOrderUntilMi(String allowNdWebOrderUntilMi) {
		this.allowNdWebOrderUntilMi = allowNdWebOrderUntilMi;
	}

	public String getApplySdLeadtimeChkFromMi() {
		return applySdLeadtimeChkFromMi;
	}

	public void setApplySdLeadtimeChkFromMi(String applySdLeadtimeChkFromMi) {
		this.applySdLeadtimeChkFromMi = applySdLeadtimeChkFromMi;
	}

	public String getApplyNdLeadtimeChkUntilMi() {
		return applyNdLeadtimeChkUntilMi;
	}

	public void setApplyNdLeadtimeChkUntilMi(String applyNdLeadtimeChkUntilMi) {
		this.applyNdLeadtimeChkUntilMi = applyNdLeadtimeChkUntilMi;
	}

	public SareaDisplayEntity getDisplayEntity() {
		return displayEntity;
	}

	public void setDisplayEntity(SareaDisplayEntity displayEntity) {
		this.displayEntity = displayEntity;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getExactQuantityOrderTolerance() {
		return exactQuantityOrderTolerance;
	}

	public void setExactQuantityOrderTolerance(String exactQuantityOrderTolerance) {
		this.exactQuantityOrderTolerance = exactQuantityOrderTolerance;
	}

	public DistanceValidationEditGuiBean getDistanceValidationGuiBean() {
		return distanceValidationGuiBean;
	}

	public void setDistanceValidationGuiBean(DistanceValidationEditGuiBean distanceValidationGuiBean) {
		this.distanceValidationGuiBean = distanceValidationGuiBean;
	}
}