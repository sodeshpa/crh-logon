/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.masterdata;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.beans.MaterialEnhancementsDetailBean;
import com.crh.logon.admin.web.forms.masterdata.MaterialEnhancementsDetailForm;
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
import com.crh.logon.persistence.entities.refdata.LangBsEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.SapMaterialDescEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.ServiceLocatorProvider;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_MaterialEnhancementsDetailForm" path="/MaterialEnhancementsDetailEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="MaterialEnhancementsDetailTile.def"
 * @struts.action-forward name="failure" path="MaterialEnhancementsDetailTile.def"
 */
public class MaterialEnhancementsDetailEntryAction extends AdminActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(MaterialEnhancementsDetailEntryAction.class);

	public static final String ACTION_CHROMAT_REDUCTION_TEXTS = "chromat.reduction.texts";
	
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
			helpForm = (MaterialEnhancementsDetailForm) form;
			helpForm.clear();

			String matPlantIdParam = request.getParameter("materialEnhancementsDetails.matPlantId");

			MaterialEnhancementsDetailBean materialEnhancementsDetailBean = new MaterialEnhancementsDetailBean();

			Long matPlantId = convertMatPlantIdToLong(matPlantIdParam);
			initBean(materialEnhancementsDetailBean, request, matPlantId);
			
			helpForm.setMaterialEnhancementsDetails(materialEnhancementsDetailBean);

			String forwardName = "success";
			if (ACTION_CHROMAT_REDUCTION_TEXTS.equals(helpForm.getFormAction())) {
				forwardName = "edit_chromat_text";
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
	
	public void initBean(MaterialEnhancementsDetailBean materialEnhancementsDetailBean, HttpServletRequest request, Long matPlantId) {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initBean");
		}
		// ]]
		try {
			materialEnhancementsDetailBean.setLocale(request.getLocale());
			initCurrencyBsEntityMap(materialEnhancementsDetailBean);
			initChromatReductionTextMap(materialEnhancementsDetailBean, matPlantId);
			setBeanPropertiesFromEntities(materialEnhancementsDetailBean, matPlantId);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public void initCurrencyBsEntityMap(MaterialEnhancementsDetailBean materialEnhancementsDetailBean) {
		List currBsEntityList = ServiceLocatorProvider.getServiceLocator().getRefDataGenericsService().findBsEntityList(CurrencyBsEntity.class);
		Map currencyBsEntityMap = new LinkedHashMap();
		for (Iterator it = currBsEntityList.iterator(); it.hasNext();) {
			CurrencyBsEntity bs = (CurrencyBsEntity) it.next();
			currencyBsEntityMap.put(bs.getCurrencyCd(), bs);
		}
		materialEnhancementsDetailBean.setCurrencyBsEntityMap(currencyBsEntityMap);
	}
	
	public void initChromatReductionTextMap(MaterialEnhancementsDetailBean bean, Long matPlantId) {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initChromatReductionTextMap");
		}
		// ]]
		try {
			List<String> languageCodes = getLanguageCodes();

			Map texts = loadMatPlantChromatReductionTexts(matPlantId, languageCodes);
			bean.setChromatReductionTexts(texts);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}
	
	public Map loadMatPlantChromatReductionTexts(Long matPlantId, List<String> languageCodes) {
		// [[
		Map result = new LinkedHashMap();
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			Object[][] params = {{"matPlantId", matPlantId},
					{"languageCodes", languageCodes}};
			LOG.entering(this, "loadMatPlantChromatReductionTexts", params);
		}
		// ]]
		try {
			MaterialEnhancementsService service = getServiceLocator().getMaterialEnhancementsService();

			for (String code: languageCodes) {
				// default value
				result.put(code, "");

				if (matPlantId != null) {
					MatPlantTxEntity entity = service.loadMatPlantTx(matPlantId, new Locale(code));

					if (entity != null && entity.getChromatReductionTxt() != null) {
						String value = entity.getChromatReductionTxt();

						LOG.debug("Loaded Chromat reduction [" + value + "] for id: " + 
								matPlantId + ", code: " + code);
						result.put(code, value);
					}
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

		return result;
	}
	
	private Long convertMatPlantIdToLong(String matPlantId) {
		if (!StringUtils.empty(matPlantId)) {
			try {
				return Long.valueOf(matPlantId);
			}
			catch (NumberFormatException e) {
				LOG.error("The given matPlantId (" + matPlantId + ") is not a number.");
				// continue
			}
		}
		
		return null;
	}
	
	public List<String> getLanguageCodes() {
		// [[
		List result = new ArrayList<String>();
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getLanguageCodes");
		}
		// ]]
		try {
			List entities = ServiceLocatorProvider.getServiceLocator().getRefDataGenericsService().findBsEntityList(LangBsEntity.class);
			for (Object o: entities) {
				LangBsEntity entity = (LangBsEntity) o;
				result.add(entity.getLangCd());
			}

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

	public void setBeanPropertiesFromEntities(MaterialEnhancementsDetailBean materialEnhancementsDetailBean, Long matPlantId) {
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

		if (matPlant.getMatPlantId() == null) { // new MatPlant
			materialEnhancementsDetailBean.setMatPlantId(null);
		}
		else {
			materialEnhancementsDetailBean.setMatPlantId(matPlant.getMatPlantId().toString());
		}

		if (matPlant.getPlant() == null) { // new MatPlant
			materialEnhancementsDetailBean.setPlantCd(null);
			materialEnhancementsDetailBean.setPlantName(null);
		}
		else {
			String plantCd = matPlant.getPlant().getPlantCd();
			RefDataGenericsService refDataService = ServiceLocatorProvider.getServiceLocator().getRefDataGenericsService();
			PlantBsEntity plant = refDataService.findBsEntity(PlantBsEntity.class, plantCd);
			materialEnhancementsDetailBean.setPlantCd(plant.getPlantCd());
			materialEnhancementsDetailBean.setPlantName(plant.getPlantName());
		}

		if (matPlant.getSapMaterial() == null) { // new MatPlant
			materialEnhancementsDetailBean.setSapMaterialId(null);
			materialEnhancementsDetailBean.setSapMaterialDescTxt(null);
			materialEnhancementsDetailBean.setSapMaterialNo(null);
		}
		else {
			Long sapMaterialId = matPlant.getSapMaterial().getSapMaterialId();
			MaterialEnhancementsService materialEnhancementsService = ServiceLocatorProvider.getServiceLocator().getMaterialEnhancementsService();
			SapMaterialEntity sapMaterial = materialEnhancementsService.loadSapMaterial(sapMaterialId);
			SapMaterialDescEntity sapMaterialDesc = materialEnhancementsService.loadSapMaterialDesc(sapMaterialId, locale);

			materialEnhancementsDetailBean.setSapMaterialId(sapMaterial.getSapMaterialId().toString());
			materialEnhancementsDetailBean.setSapMaterialDescTxt(sapMaterialDesc.getTxt());
			materialEnhancementsDetailBean.setSapMaterialNo(sapMaterial.getSapMaterialNo());
		}

		materialEnhancementsDetailBean.setBrandSalesName(matPlant.getBrandSalesName());
		materialEnhancementsDetailBean.setNormDesc(matPlant.getNormDesc());
		materialEnhancementsDetailBean.setCpdNumber(matPlant.getCpdNumber());
		materialEnhancementsDetailBean.setLogoGeneric(matPlant.getLogoGeneric());
		if (matPlant.getBulkDensityKgpm3() != null) {
			materialEnhancementsDetailBean.setBulkDensityKgpm3(matPlant.getBulkDensityKgpm3().toString());
		}
		else {
			materialEnhancementsDetailBean.setBulkDensityKgpm3(null);
		}

		if (matPlant.getVariableProdCostMat() != null) {
			materialEnhancementsDetailBean.setVariableProdCostMat(matPlant.getVariableProdCostMat().toString());
		}
		else {
			materialEnhancementsDetailBean.setVariableProdCostMat(null);
		}
		if (matPlant.getFutureVariableProdCostMat() != null) {
			materialEnhancementsDetailBean.setFutureVariableProdCostMat(matPlant.getFutureVariableProdCostMat().toString());
		}
		else {
			materialEnhancementsDetailBean.setFutureVariableProdCostMat(null);
		}
		if (matPlant.getFutureValidFromDat() != null) {
			materialEnhancementsDetailBean.setFutureValidFromDat(
					getUserTimezone().fmtAsDayMonthYear(matPlant.getFutureValidFromDat()));
		}
		else {
			materialEnhancementsDetailBean.setFutureValidFromDat(null);
		}
		if (matPlant.getCurrency() != null) {
			materialEnhancementsDetailBean.setCurrency(matPlant.getCurrency().getCurrencyCd());
		}
		else {
			materialEnhancementsDetailBean.setCurrency(null);
		}
		if (matPlant.getChromatReductionMonths() != null) {
			materialEnhancementsDetailBean.setChromatReductionMonths(matPlant.getChromatReductionMonths().toString());
		}
		else {
			materialEnhancementsDetailBean.setChromatReductionMonths(null);
		}
		
		materialEnhancementsDetailBean.setAutoaddMaterialDescTxt(null);
		materialEnhancementsDetailBean.setAutoaddNUomCd(null);
		materialEnhancementsDetailBean.setAutoadd1PerNOfUom(null);		
		
		SapMaterialEntity autoaddSapMaterial = matPlant.getAutoaddSapMaterial();
		if(autoaddSapMaterial != null){
			materialEnhancementsDetailBean.setAutoaddSapMaterialId(autoaddSapMaterial.getSapMaterialId().toString());
			MaterialEnhancementsService materialEnhancementsService = ServiceLocatorProvider.getServiceLocator().getMaterialEnhancementsService();
			SapMaterialDescEntity autoaddMaterialDesc = materialEnhancementsService.loadSapMaterialDesc(autoaddSapMaterial.getSapMaterialId(), locale);
			materialEnhancementsDetailBean.setAutoaddMaterialDescTxt(autoaddMaterialDesc.getTxt());									
		}
		
		UomBsEntity autoaddNUom = matPlant.getAutoaddNUom();
		if(autoaddNUom != null){
			materialEnhancementsDetailBean.setAutoaddNUomCd(autoaddNUom.getUomCd());
		}
		
		BigDecimal autoadd1PerNOfUom = matPlant.getAutoadd1PerNOfUom();
		if(autoadd1PerNOfUom != null){
			materialEnhancementsDetailBean.setAutoadd1PerNOfUom(autoadd1PerNOfUom.toString());	
		}
		
		materialEnhancementsDetailBean.setPrioOverSapMatNormFlag(matPlant.getPrioOverSapMatNormFlag());
		materialEnhancementsDetailBean.setLogo1(matPlant.getLogo1());
		materialEnhancementsDetailBean.setLogo1Z1(matPlant.getLogo1Z1());
		materialEnhancementsDetailBean.setLogo1Z2(matPlant.getLogo1Z2());
		materialEnhancementsDetailBean.setNationalNorm(matPlant.getNationalNorm());
		materialEnhancementsDetailBean.setLogo2(matPlant.getLogo2());
		materialEnhancementsDetailBean.setLogo2Z1(matPlant.getLogo2Z1());
		materialEnhancementsDetailBean.setLogo2Z2(matPlant.getLogo2Z2());
		materialEnhancementsDetailBean.setLogo2Z3(matPlant.getLogo2Z3());
		materialEnhancementsDetailBean.setLogo3(matPlant.getLogo3());
		materialEnhancementsDetailBean.setLogo3Z1(matPlant.getLogo3Z1());
		materialEnhancementsDetailBean.setLogo3Z2(matPlant.getLogo3Z2());
		materialEnhancementsDetailBean.setLogo3Z3(matPlant.getLogo3Z3());
		materialEnhancementsDetailBean.setLogo4(matPlant.getLogo4());
		materialEnhancementsDetailBean.setLogo4Z1(matPlant.getLogo4Z1());
		materialEnhancementsDetailBean.setLogo4Z2(matPlant.getLogo4Z2());
		materialEnhancementsDetailBean.setLogo4Z3(matPlant.getLogo4Z3());
		materialEnhancementsDetailBean.setLogo5(matPlant.getLogo5());
		materialEnhancementsDetailBean.setLogo5Z1(matPlant.getLogo5Z1());
		materialEnhancementsDetailBean.setLogo5Z2(matPlant.getLogo5Z2());
		materialEnhancementsDetailBean.setLogo5Z3(matPlant.getLogo5Z3());
		materialEnhancementsDetailBean.setLogo6(matPlant.getLogo6());
		materialEnhancementsDetailBean.setLogo6Z1(matPlant.getLogo6Z1());
		materialEnhancementsDetailBean.setLogo6Z2(matPlant.getLogo6Z2());
		materialEnhancementsDetailBean.setLogo6Z3(matPlant.getLogo6Z3());
	}
}
