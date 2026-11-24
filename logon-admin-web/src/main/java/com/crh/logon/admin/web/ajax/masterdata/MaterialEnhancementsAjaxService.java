/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.ajax.masterdata;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.ajax.util.OrderBy;
import com.crh.logon.business.services.interfaces.MaterialEnhancementsService;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.views.MatPlantMissingEntity;
import com.crh.logon.persistence.entities.views.MaterialEnhancementsEntity;
import com.crh.logon.persistence.entities.views.SapMatPlantNormDescEntity;
import com.crh.logon.persistence.entities.views.SapMaterialDescEntity;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.DropdownEntity;
import com.crh.logon.web.utils.PlantSareaUtils;

/**
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to
 * AJAX components.
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="MaterialEnhancementsAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.web.utils.DropdownEntity"
 */
public class MaterialEnhancementsAjaxService extends DwrServiceSupport {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(MaterialEnhancementsAjaxService.class);

	public List<DropdownEntity> fetchPlantsList(String[] query) {
		// [[
		List<DropdownEntity> result = null;
		Object[][] params = {{"query", query}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchPlantsList", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_GLOBAL_MATERIAL);
			String prodPlantSrearchText = null;
			if (query != null && query.length > 0 && StringUtils.notEmpty(query[0])) {
				prodPlantSrearchText = query[0];
			}
			boolean isActive = true;
			boolean isProductionPlant = true;

			return result = PlantSareaUtils.getPlantList(getPlantSareaService(), null, prodPlantSrearchText, isProductionPlant, isActive);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public ListRange fetchMaterialsList(
			int skip,
			int count,
			String sort,
			String[] query,
			HttpServletRequest request,
			HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"count", count + ""},
							{"sort", sort},
							{"query", query},
							{"request", request},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchMaterialsList", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_GLOBAL_MATERIAL);
			
			OrderBy orderBy = new OrderBy(sort, SapMaterialDescEntity.ATTR_TXT);

			String plantCd = query[0];
			String sapMaterialDescTxt = query[1];
			
			MaterialEnhancementsService materialEnhancementsService = getServiceLocator().getMaterialEnhancementsService();
			return result = materialEnhancementsService.fetchSapMaterialList(skip, count, plantCd, sapMaterialDescTxt, request.getLocale(), orderBy.getField(), orderBy.isAscending());
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public ListRange fetchNotProducedMaterialsList(
			int skip,
			int count,
			String sort,
			String[] query,
			HttpServletRequest request,
			HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"count", count + ""},
							{"sort", sort},
							{"query", query},
							{"request", request},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchNotProducedMaterialsList", params);
		}
		// ]]
		try {
			ListRange result1 = new ListRange();
			Authorization.authorize(Authorization.MASTER_DATA_GLOBAL_MATERIAL);
			String orderby = SapMaterialDescEntity.ATTR_TXT;
			if (StringUtils.notEmpty(sort)) {
				orderby = sort;
			}
			boolean asc = true;
			String plantCd = query[0];
			String sapMaterialDescTxt = query[1];
			
			MaterialEnhancementsService materialEnhancementsService = getServiceLocator().getMaterialEnhancementsService();
			
			List<MatPlantMissingEntity> materials = materialEnhancementsService.fetchNotProducedSapMaterialList(skip, count, plantCd, sapMaterialDescTxt, request.getLocale(), orderby, asc);
			
			result1.setData(materials.toArray());
			result1.setTotalSize(materials.size());
			
			return result = result1;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public ListRange fetchAutoaddMaterialList(
			int skip,
			int count,
			String sort,
			String[] query,
			HttpServletRequest request,
			HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"count", count + ""},
							{"sort", sort},
							{"query", query},
							{"request", request},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAutoaddMaterialList", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_GLOBAL_MATERIAL);

			OrderBy orderBy = new OrderBy(sort, SapMaterialDescEntity.ATTR_TXT);
			
			String sapMaterialDescTxt = query[0];
			
			MaterialEnhancementsService materialEnhancementsService = getServiceLocator().getMaterialEnhancementsService();
			return result = materialEnhancementsService.fetchAutoaddMaterialList(skip, count, sapMaterialDescTxt, request.getLocale(), orderBy.getField(), orderBy.isAscending());
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public List<UomBsEntity> fetchUomList(String[] query) {
		// [[
		List<UomBsEntity> result = null;
		Object[][] params = {{"query", query}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchUomList", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_GLOBAL_MATERIAL);
			return result = getServiceLocator().getRefDataGenericsService().findBsEntityList(UomBsEntity.class);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public ListRange searchMatPlants(
			int skip,
			int count,
			String sort,
			Map<String, String> query,
			HttpServletRequest request,
			HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
				{"count", count + ""},
				{"sort", sort},
				{"query", query},
				{"request", request},
				{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "searchMaterials", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_GLOBAL_MATERIAL);
			
			OrderBy orderBy = new OrderBy(sort, MaterialEnhancementsEntity.ATTR_PLANT_NAME);
						
			String plantCd = query.get(MaterialEnhancementsEntity.ATTR_PLANT_CD);
			String sapMaterialIdStr = query.get(MaterialEnhancementsEntity.ATTR_SAP_MATERIAL_ID);
			Long sapMaterialId = null;
			if (!StringUtils.empty(sapMaterialIdStr)) {
				try {
					sapMaterialId = Long.valueOf(sapMaterialIdStr);
				}
				catch (NumberFormatException e) {
					throw new LogonException(e);
				}
			}

			MaterialEnhancementsService materialEnhancementsService = getServiceLocator().getMaterialEnhancementsService();
			return result = materialEnhancementsService.fetchMatPlantList(skip, count, plantCd, sapMaterialId, request.getLocale(), orderBy.getField(), orderBy.isAscending());
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}
	
	public ListRange searchMatPlantNorms(
			int skip,
			int count,
			String sort,
			Map<String, String> query,
			HttpServletRequest request,
			HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
				{"count", count + ""},
				{"sort", sort},
				{"query", query},
				{"request", request},
				{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "searchMatPlantNorms", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_GLOBAL_MATERIAL);
			
			OrderBy orderBy = new OrderBy(sort, SapMatPlantNormDescEntity.ATTR_PLANT_CD);
			
			String norm = query.get(SapMatPlantNormDescEntity.ATTR_SAP_MATERIAL_NORM_CD);
			String lang = query.get(SapMatPlantNormDescEntity.ATTR_SAP_LANGUAGE_CD);
			String sorg = query.get(SapMatPlantNormDescEntity.ATTR_SORG_CD);
			String plantCd = query.get(MaterialEnhancementsEntity.ATTR_PLANT_CD);
			String sapMaterialIdStr = query.get(MaterialEnhancementsEntity.ATTR_SAP_MATERIAL_ID);
			Long sapMaterialId = null;
			if (!StringUtils.empty(sapMaterialIdStr)) {
				try {
					sapMaterialId = Long.valueOf(sapMaterialIdStr);
				}
				catch (NumberFormatException e) {
					throw new LogonException(e);
				}
			}

			MaterialEnhancementsService materialEnhancementsService = getServiceLocator().getMaterialEnhancementsService();
			return result = materialEnhancementsService.fetchMatPlantNormList(skip, count, norm, lang, sorg, plantCd, sapMaterialId, request.getLocale(), orderBy.getField(), orderBy.isAscending());
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
