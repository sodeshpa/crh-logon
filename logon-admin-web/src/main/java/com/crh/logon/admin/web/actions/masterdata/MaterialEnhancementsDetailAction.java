/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.masterdata;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.beans.MaterialEnhancementsDetailBean;
import com.crh.logon.admin.web.forms.masterdata.MaterialEnhancementsDetailForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.MaterialEnhancementsService;
import com.crh.logon.business.services.interfaces.RefDataGenericsService;
import com.crh.logon.persistence.entities.common.MatPlantEntity;
import com.crh.logon.persistence.entities.common.MatPlantTxEntity;
import com.crh.logon.persistence.entities.refdata.CurrencyBsEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_MaterialEnhancementsDetailForm" path="/MaterialEnhancementsDetail" scope="session"
 *  validate="true" input="MaterialEnhancementsDetailTile.def"
 *
 * @struts.action-forward name="materialEnhancementsDetailEntry" path="/MaterialEnhancementsDetailEntry.do"
 * @struts.action-forward name="save" path="MaterialEnhancementsDetailTile.def"
 * @struts.action-forward name="close" path="MaterialEnhancementsDetailCloseTile.def"
 * @struts.action-forward name="error" path="MaterialEnhancementsDetailTile.def"
 */
public class MaterialEnhancementsDetailAction extends AdminActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(MaterialEnhancementsDetailAction.class);

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
			Authorization.authorize(Authorization.MASTER_DATA_GLOBAL_MATERIAL);
			MaterialEnhancementsDetailForm helpForm;
			String forwardName = FORWARD_CLOSE;
			helpForm = (MaterialEnhancementsDetailForm) form;

			if (FORWARD_SAVE.equals(helpForm.getFormAction())) {
				helpForm.setFormAction("");
				if (logDebugEnabled) {
					LOG.debug("Action: submit save button clicked!");
				}

				ActionErrors errors = MessageUtils.getActionErrors(request);
				if (errors == null || errors.isEmpty()) {
					MaterialEnhancementsDetailBean materialEnhancementsDetailBean = helpForm.getMaterialEnhancementsDetails();
					MaterialEnhancementsService materialEnhancementsService = getServiceLocator().getMaterialEnhancementsService();
					String matPlantIdParam = request.getParameter("materialEnhancementsDetails.matPlantId");
					
					MatPlantEntity matPlant = null;
					if (!StringUtils.empty(matPlantIdParam)) {
						Long matPlantId = null;
						try {
							matPlantId = Long.valueOf(matPlantIdParam);
						}
						catch (NumberFormatException e) {
							LOG.error("The given matPlantId (" + matPlantId.toString() + ") is not a number.");
							throw new LogonException(e);
						}
						
						matPlant = copyBeanPropertiesToEntities(materialEnhancementsDetailBean, matPlantId);
						matPlant = materialEnhancementsService.updateMatPlant(matPlant);
						MessageUtils.addActionMessage(request,"materialenhancements.message.matPlantUpdated");
					}
					else {
						matPlant = copyBeanPropertiesToEntities(materialEnhancementsDetailBean, null);
						matPlant = materialEnhancementsService.createMatPlant(matPlant);
						MessageUtils.addActionMessage(request,"materialenhancements.message.newMatPlantCreated");
					}
					
					updateChromatReductionTexts(matPlant, materialEnhancementsDetailBean);
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
	
	public MatPlantEntity copyBeanPropertiesToEntities(MaterialEnhancementsDetailBean materialEnhancementsDetailBean, Long matPlantId) {
		MatPlantEntity matPlant;
		Locale locale = materialEnhancementsDetailBean.getLocale();
		
		if (matPlantId == null) { // new MatPlant
			matPlant = new MatPlantEntity();
		}
		else {
			MaterialEnhancementsService materialEnhancementsService = getServiceLocator().getMaterialEnhancementsService();
			matPlant = materialEnhancementsService.loadMatPlant(matPlantId);
			if (matPlant == null) {
				LOG.error("Cannot find MatPlant for the given matPlantId (" + matPlantId.toString() + ")");
				matPlant = new MatPlantEntity();
			}
		}
		
		if (matPlant.getPlant() == null) { // new MatPlant
			RefDataGenericsService refDataService = getServiceLocator().getRefDataGenericsService();
			PlantBsEntity plant = refDataService.findBsEntity(PlantBsEntity.class, materialEnhancementsDetailBean.getPlantCd());
			matPlant.setPlant(plant);
		}

		if (matPlant.getSapMaterial() == null) { // new MatPlant
			Long sapMaterialId = null;
			try {
				sapMaterialId = Long.valueOf(materialEnhancementsDetailBean.getSapMaterialId());
			}
			catch (NumberFormatException e) {
				throw new LogonException(e);
			}

			MaterialEnhancementsService materialEnhancementsService = getServiceLocator().getMaterialEnhancementsService();
			SapMaterialEntity sapMaterial = materialEnhancementsService.loadSapMaterial(sapMaterialId);
			matPlant.setSapMaterial(sapMaterial);
		}

		matPlant.setBrandSalesName(materialEnhancementsDetailBean.getBrandSalesName());
		matPlant.setNormDesc(materialEnhancementsDetailBean.getNormDesc());
		matPlant.setCpdNumber(materialEnhancementsDetailBean.getCpdNumber());
		matPlant.setLogoGeneric(materialEnhancementsDetailBean.getLogoGeneric());

		String bulkDensityKgpm3 = materialEnhancementsDetailBean.getBulkDensityKgpm3();
		if (StringUtils.notEmpty(bulkDensityKgpm3)) {
			matPlant.setBulkDensityKgpm3(new BigDecimal(bulkDensityKgpm3.replace(',', '.')));
		}
		else {
			matPlant.setBulkDensityKgpm3(null);
		}

		String variableProdCostMat = materialEnhancementsDetailBean.getVariableProdCostMat();
		if (StringUtils.notEmpty(variableProdCostMat)) {
			matPlant.setVariableProdCostMat(new BigDecimal(variableProdCostMat.replace(',', '.')));
		}
		else {
			matPlant.setVariableProdCostMat(null);
		}

		String futureVariableProdCostMat = materialEnhancementsDetailBean.getFutureVariableProdCostMat();
		if (StringUtils.notEmpty(futureVariableProdCostMat)) {
			matPlant.setFutureVariableProdCostMat(new BigDecimal(futureVariableProdCostMat.replace(',', '.')));
		}
		else {
			matPlant.setFutureVariableProdCostMat(null);
		}

		String futureValidFromDat = materialEnhancementsDetailBean.getFutureValidFromDat();
		if (StringUtils.notEmpty(futureValidFromDat)) {
			try {
				matPlant.setFutureValidFromDat(
						getUserTimezone().parseDayMonthYear(futureValidFromDat));
			}
			catch (ParseException e) {
				throw new LogonException(e);
			}
		}
		else {
			matPlant.setFutureValidFromDat(null);
		}

		String currency = materialEnhancementsDetailBean.getCurrency();
		CurrencyBsEntity currencyBsEntity = (CurrencyBsEntity) materialEnhancementsDetailBean.getCurrencyBsEntityMap().get(currency);
		matPlant.setCurrency(currencyBsEntity);

		String chromatReductionMonths = materialEnhancementsDetailBean.getChromatReductionMonths();
		if (StringUtils.notEmpty(chromatReductionMonths)) {
			matPlant.setChromatReductionMonths(Long.valueOf(chromatReductionMonths));
		}
		else {
			matPlant.setChromatReductionMonths(null);
		}
		
		String autoaddSapMaterialIdStr = materialEnhancementsDetailBean.getAutoaddSapMaterialId();
		SapMaterialEntity autoaddMaterial = null;
		if(StringUtils.notEmpty(autoaddSapMaterialIdStr)){	
			Long autoaddSapMaterialId = null;
			try {
				autoaddSapMaterialId = Long.valueOf(autoaddSapMaterialIdStr);
			}
			catch (NumberFormatException e) {
				//store null value if not found
				//throw new LogonException(e);
			}
			if(autoaddSapMaterialId != null){
				MaterialEnhancementsService materialEnhancementsService = getServiceLocator().getMaterialEnhancementsService();
				autoaddMaterial = materialEnhancementsService.loadSapMaterial(autoaddSapMaterialId);
			}
		}

		matPlant.setAutoaddSapMaterial(autoaddMaterial);
		
		String autoadd1PerNOfUom = materialEnhancementsDetailBean.getAutoadd1PerNOfUom();
		BigDecimal autoadd1PerNOfUomBd = null;
		if(StringUtils.notEmpty(autoadd1PerNOfUom)){
			try {
				autoadd1PerNOfUomBd = new BigDecimal(autoadd1PerNOfUom.replace(',', '.'));
			}
			catch (NumberFormatException e) {
				throw new LogonException(e);
			}
		}
		matPlant.setAutoadd1PerNOfUom(autoadd1PerNOfUomBd);
		
		String autoaddNUomCd = materialEnhancementsDetailBean.getAutoaddNUomCd();
		UomBsEntity findUomBsEntity =  null;
		if(StringUtils.notEmpty(autoaddNUomCd)){
			RefDataGenericsService refDataService = getServiceLocator().getRefDataGenericsService();
			findUomBsEntity = refDataService.findBsEntity(UomBsEntity.class, autoaddNUomCd);
		}
		matPlant.setAutoaddNUom(findUomBsEntity);
		
		matPlant.setPrioOverSapMatNormFlag(materialEnhancementsDetailBean.getPrioOverSapMatNormFlag());
		matPlant.setLogo1(materialEnhancementsDetailBean.getLogo1());
		matPlant.setLogo1Z1(materialEnhancementsDetailBean.getLogo1Z1());
		matPlant.setLogo1Z2(materialEnhancementsDetailBean.getLogo1Z2());
		matPlant.setNationalNorm(materialEnhancementsDetailBean.getNationalNorm());
		matPlant.setLogo2(materialEnhancementsDetailBean.getLogo2());
		matPlant.setLogo2Z1(materialEnhancementsDetailBean.getLogo2Z1());
		matPlant.setLogo2Z2(materialEnhancementsDetailBean.getLogo2Z2());
		matPlant.setLogo2Z3(materialEnhancementsDetailBean.getLogo2Z3());
		matPlant.setLogo3(materialEnhancementsDetailBean.getLogo3());
		matPlant.setLogo3Z1(materialEnhancementsDetailBean.getLogo3Z1());
		matPlant.setLogo3Z2(materialEnhancementsDetailBean.getLogo3Z2());
		matPlant.setLogo3Z3(materialEnhancementsDetailBean.getLogo3Z3());
		matPlant.setLogo4(materialEnhancementsDetailBean.getLogo4());
		matPlant.setLogo4Z1(materialEnhancementsDetailBean.getLogo4Z1());
		matPlant.setLogo4Z2(materialEnhancementsDetailBean.getLogo4Z2());
		matPlant.setLogo4Z3(materialEnhancementsDetailBean.getLogo4Z3());
		matPlant.setLogo5(materialEnhancementsDetailBean.getLogo5());
		matPlant.setLogo5Z1(materialEnhancementsDetailBean.getLogo5Z1());
		matPlant.setLogo5Z2(materialEnhancementsDetailBean.getLogo5Z2());
		matPlant.setLogo5Z3(materialEnhancementsDetailBean.getLogo5Z3());
		matPlant.setLogo6(materialEnhancementsDetailBean.getLogo6());
		matPlant.setLogo6Z1(materialEnhancementsDetailBean.getLogo6Z1());
		matPlant.setLogo6Z2(materialEnhancementsDetailBean.getLogo6Z2());
		matPlant.setLogo6Z3(materialEnhancementsDetailBean.getLogo6Z3());
		
		return matPlant;
	}
	
	public void updateChromatReductionTexts(MatPlantEntity matPlant, MaterialEnhancementsDetailBean bean) {
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateChromatReductionTexts");
		}
		// ]]
		try {
			MaterialEnhancementsService service = getServiceLocator().getMaterialEnhancementsService();

			for (Map.Entry<String, String> entry: bean.getChromatReductionTexts().entrySet()) {
				MatPlantTxEntity entity = service.loadMatPlantTx(
						matPlant.getMatPlantId(), 
						new Locale(entry.getKey()));

				if (entity == null) {
					entity = new MatPlantTxEntity();
				}
				
				createOrUpdateMatPlantTxEntity(
						entity, 
						matPlant, 
						new Locale(entry.getKey()), 
						entry.getValue());
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
	
	private void createOrUpdateMatPlantTxEntity(
			MatPlantTxEntity entity, MatPlantEntity matPlant, 
			Locale langCd, String value) {
		
		MaterialEnhancementsService service = getServiceLocator().getMaterialEnhancementsService();

		if (entity.getMatPlantTxPK() == null) {
			LOG.debug("Creating Chromat reduction text [" + value + "]");
			entity.setChromatReductionTxt(value);
			service.createMatPlantTx(matPlant, langCd, entity);
		}
		else {
			if (! StringUtils.areEqual(entity.getChromatReductionTxt(), value)) {
				LOG.debug("Updating Chromat reduction text [" +
						entity.getChromatReductionTxt() + "] with [" + 
						value + "]");		
				entity.setChromatReductionTxt(value);
				service.updateMatPlantTx(entity);
			}
		}
	}
}
