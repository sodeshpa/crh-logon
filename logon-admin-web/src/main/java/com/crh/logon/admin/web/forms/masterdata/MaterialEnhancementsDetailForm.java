/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.masterdata;

import java.math.BigDecimal;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import com.crh.logon.utils.time.DateTimeParser;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.actions.masterdata.MaterialEnhancementsDetailAction;
import com.crh.logon.admin.web.beans.MaterialEnhancementsDetailBean;
import com.crh.logon.business.services.interfaces.MaterialEnhancementsService;
import com.crh.logon.persistence.entities.common.MatPlantEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.ServiceLocatorProvider;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_MaterialEnhancementsDetailForm"
 */
public class MaterialEnhancementsDetailForm extends ActionForm {

	private static final long serialVersionUID = -4172910147603503279L;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(MaterialEnhancementsDetailForm.class);
	public static final String SESSION_MATERIAL_ENHANCEMENTS_DETAIL_FORM_NAME = "admin_MaterialEnhancementsDetailForm";

	private String formAction;

	private MaterialEnhancementsDetailBean materialEnhancementsDetails;

	/**
	 * Default constructor.
	 */
	public MaterialEnhancementsDetailForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "MaterialEnhancementsDetailForm");
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
			this.setMaterialEnhancementsDetails(new MaterialEnhancementsDetailBean());
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
			;
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
	 * Perform form-specific validation of input parameters.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		// [[
		ActionErrors result = null;
		Object[][] params = {{"mapping", mapping},
				{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validate", params);
		}
		// ]]
		try {
			ActionErrors errors = super.validate(mapping, request);
			if (errors == null) {
				errors = new ActionErrors();
			}

			if (MaterialEnhancementsDetailAction.FORWARD_SAVE.equals(formAction)) {
				if (StringUtils.empty(request.getParameter("materialEnhancementsDetails.matPlantId"))) {
					if (StringUtils.empty(request.getParameter("materialEnhancementsDetails.plantCd"))) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("materialenhancements.error.fieldEmpty.plantCd"));
					}
					if (StringUtils.empty(request.getParameter("materialEnhancementsDetails.sapMaterialId"))) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("materialenhancements.error.fieldEmpty.sapMaterialId"));
					}
					if (StringUtils.notEmpty(request.getParameter("materialEnhancementsDetails.plantCd")) &&
							StringUtils.notEmpty(request.getParameter("materialEnhancementsDetails.sapMaterialId"))) {
						String plantCd = request.getParameter("materialEnhancementsDetails.plantCd");
						String sapMaterialIdStr = request.getParameter("materialEnhancementsDetails.sapMaterialId");
						Long sapMaterialId = null;
						try {
							sapMaterialId = Long.valueOf(sapMaterialIdStr);
						}
						catch (NumberFormatException e) {
							throw new LogonException(e);
						}

						Authorization.authorize(Authorization.MASTER_DATA_GLOBAL_MATERIAL);
						MaterialEnhancementsService materialEnhancementsService = ServiceLocatorProvider.getServiceLocator().getMaterialEnhancementsService();
						long matPlantCount = materialEnhancementsService.countMatPlants(plantCd, sapMaterialId);
						if (matPlantCount > 0) {
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("materialenhancements.error.existingMatPlant"));
						}
					}
				}

				if (StringUtils.empty(request.getParameter("materialEnhancementsDetails.normDesc"))) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("materialenhancements.error.fieldEmpty.productLineText1"));
				}

				String bulkDensityKgpm3Str = request.getParameter("materialEnhancementsDetails.bulkDensityKgpm3");
				if (StringUtils.notEmpty(bulkDensityKgpm3Str)) {
					try {
						new BigDecimal(bulkDensityKgpm3Str.replace(',', '.'));
					}
					catch (NumberFormatException e) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("materialenhancements.error.fieldInvalid.bulkDensityKgpm3"));
					}
				}

				String variableProdCostMatStr = request.getParameter("materialEnhancementsDetails.variableProdCostMat");
				if (StringUtils.notEmpty(variableProdCostMatStr)) {
					try {
						BigDecimal variableProdCostMat = new BigDecimal(variableProdCostMatStr.replace(',', '.'));
						BigDecimal variableProdCostMatMaxValue = BigDecimal.TEN.pow((int)(MatPlantEntity.DBSIZE_VARIABLE_PROD_COST_MAT - MatPlantEntity.DBSIZE_PRECISION_VARIABLE_PROD_COST_MAT));
						if (variableProdCostMat.compareTo(variableProdCostMatMaxValue) > -1) {
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("materialenhancements.error.fieldValueOutOfRange.variableProdCostMat"));
						}
					}
					catch (NumberFormatException e) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("materialenhancements.error.fieldInvalid.variableProdCostMat"));
					}
				}

				String futureVariableProdCostMatStr = request.getParameter("materialEnhancementsDetails.futureVariableProdCostMat");
				if (StringUtils.notEmpty(futureVariableProdCostMatStr)) {
					try {
						BigDecimal futureVariableProdCostMat = new BigDecimal(futureVariableProdCostMatStr.replace(',', '.'));
						BigDecimal futureVariableProdCostMatMaxValue = BigDecimal.TEN.pow((int)(MatPlantEntity.DBSIZE_FUTURE_VARIABLE_PROD_COST_MAT - MatPlantEntity.DBSIZE_PRECISION_FUTURE_VARIABLE_PROD_COST_MAT));
						if (futureVariableProdCostMat.compareTo(futureVariableProdCostMatMaxValue) > -1) {
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("materialenhancements.error.fieldValueOutOfRange.futureVariableProdCostMat"));
						}
					}
					catch (NumberFormatException e) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("materialenhancements.error.fieldInvalid.futureVariableProdCostMat"));
					}
				}

				String futureValidFromDatStr = request.getParameter("materialEnhancementsDetails.futureValidFromDat");
				if (StringUtils.notEmpty(futureValidFromDatStr)) {
					try {
                        DateTimeParser.parseLogonDayMonthYear(request.getLocale(), futureValidFromDatStr);
					}
					catch (ParseException e) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("materialenhancements.error.fieldInvalid.futureValidFromDat"));
					}
				}

				String chromatReductionMonthsStr = request.getParameter("materialEnhancementsDetails.chromatReductionMonths");
				if (StringUtils.notEmpty(chromatReductionMonthsStr)) {
					try {
						Long.valueOf(chromatReductionMonthsStr);
					}
					catch (NumberFormatException e) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("materialenhancements.error.fieldInvalid.chromatReductionMonths"));
					}
				}

				String autoadd1PerNOfUomStr = request.getParameter("materialEnhancementsDetails.autoadd1PerNOfUom");
				if (StringUtils.notEmpty(autoadd1PerNOfUomStr)) {
					
					try {												
						new BigDecimal(autoadd1PerNOfUomStr.replace(',', '.'));
					}
					catch (NumberFormatException e) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("materialenhancements.error.fieldInvalid.autoadd1PerNOfUomStr"));
					}					
				}
			}

			if (!errors.isEmpty()) {
				formAction = "";
			}
			return result = errors;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public String getFormAction() {
		return formAction;
	}

	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}

	public void setMaterialEnhancementsDetails(MaterialEnhancementsDetailBean materialEnhancementsDetails) {
		this.materialEnhancementsDetails = materialEnhancementsDetails;
	}

	public MaterialEnhancementsDetailBean getMaterialEnhancementsDetails() {
		return materialEnhancementsDetails;
	}
}
