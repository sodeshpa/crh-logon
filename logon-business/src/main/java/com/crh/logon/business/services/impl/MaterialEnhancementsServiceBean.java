/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.JPAQuery;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.gdi.jpa.filter.SelectExpression;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.MaterialEnhancementsServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.MaterialEnhancementsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.stores.sap.ContractItemStoreInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.MatPlantEntity;
import com.crh.logon.persistence.entities.common.MatPlantTxEntity;
import com.crh.logon.persistence.entities.common.MatPlantTxPK;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.SapLanguageTxEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialPlantEntity;
import com.crh.logon.persistence.entities.views.MatPlantMissingEntity;
import com.crh.logon.persistence.entities.views.MaterialEnhancementsEntity;
import com.crh.logon.persistence.entities.views.SapMatPlantNormDescEntity;
import com.crh.logon.persistence.entities.views.SapMaterialDescEntity;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
@Interceptors(UserInfoInterceptor.class)
@Named
public class MaterialEnhancementsServiceBean extends MaterialEnhancementsServiceDispatcher implements MaterialEnhancementsServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(MaterialEnhancementsServiceBean.class);

	@EJB
	RefDataGenericsServiceLocalInterface referentialDataService;
	@EJB
	ContractItemStoreInterface contractItemStore;
	
	public ListRange fetchSapMaterialList(int skip, int count, String plantCd, String sapMaterialDescTxt, Locale locale, String orderby, boolean asc) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
				{"count", count + ""},
				{"plantCd", plantCd},
				{"locale", locale},
				{"orderby", orderby},
				{"asc", asc}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSapMaterialList", params);
		}
		// ]]
		try {
			ReadOnlyDao<MatPlantEntity> matPlantEntityDao = getDaoProvider().getMatPlantEntityDao();
			ReadOnlyDao<SapMaterialDescEntity> sapMaterialDescDao = getDaoProvider().getSapMaterialDescEntityDao();

			Filter<MatPlantEntity> matPlantFilter = new FilterImpl<MatPlantEntity>();
			matPlantFilter.addExpression(Restrictions.eq(MatPlantEntity.ATTR_PLANT_CD, plantCd));
			matPlantFilter.addExpression(Order.asc(MatPlantEntity.ATTR_MAT_PLANT_ID));
			JPAQuery<MatPlantEntity> query = matPlantEntityDao.createJPAQuery();
			query.addSelect(SelectExpression.simpleSelect(MatPlantEntity.ATTR_SAP_MATERIAL_ID));
			query.setFilter(matPlantFilter);
			@SuppressWarnings("unchecked")
			List<Long> sapMaterialIdList = matPlantEntityDao.find(query);

			result = new ListRange();
			if (sapMaterialIdList.isEmpty()) {
				result.setData(new Object[] {});
				result.setTotalSize(0);
				return result;
			}

			Filter<SapMaterialDescEntity> sapMaterialDescFilter = new FilterImpl<SapMaterialDescEntity>();
			sapMaterialDescFilter.addExpression(Restrictions.eq(SapMaterialDescEntity.ATTR_LANG_CD, locale.getLanguage()));
			sapMaterialDescFilter.addExpression(Restrictions.in(SapMaterialDescEntity.ATTR_SAP_MATERIAL_ID, sapMaterialIdList));
			sapMaterialDescFilter.addExpression(Restrictions.ilike(SapMaterialDescEntity.ATTR_DISPLAY, "%" + sapMaterialDescTxt + "%"));
			sapMaterialDescFilter.addExpression(asc ? Order.asc(orderby) : (Order.desc(orderby)));
			result.setData(sapMaterialDescDao.findNRecs(skip, count, sapMaterialDescFilter).toArray());
			result.setTotalSize(sapMaterialDescDao.count(sapMaterialDescFilter));

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
	
	public List<MatPlantMissingEntity> fetchNotProducedSapMaterialList(int skip, int count, String plantCd, String sapMaterialDescTxt, Locale locale, String orderby, boolean asc) {
		// [[
		List<MatPlantMissingEntity> result = null;
		Object[][] params = {{"skip", skip + ""},
							{"count", count + ""},
							{"plantCd", plantCd},
							{"sapMaterialDescTxt", sapMaterialDescTxt},
							{"locale", locale},
							{"orderby", orderby},
							{"asc", asc + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchNotProducedSapMaterialList", params);
		}
		// ]]
		try {
			ReadOnlyDao<MatPlantMissingEntity> matPlantMissingEntityDao = getDaoProvider().getMatPlantMissingEntityDao();
			
			Filter<MatPlantMissingEntity> filter = matPlantMissingEntityDao.createFilter();
			filter.addExpression(Restrictions.eq(MatPlantMissingEntity.ATTR_PLANT_CD, plantCd));
			filter.addExpression(Restrictions.eq(MatPlantMissingEntity.ATTR_LANG_CD, locale.getLanguage()));
			
			if (!sapMaterialDescTxt.isEmpty()) {
				filter.addExpression(Restrictions.ilike(MatPlantMissingEntity.ATTR_TXT, "%" + sapMaterialDescTxt + "%"));
			}
			
			return result = matPlantMissingEntityDao.find(filter);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public ListRange fetchAutoaddMaterialList(int skip, int count, String sapMaterialDescTxt, Locale locale, String orderby, boolean asc) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"count", count + ""},
							{"sapMaterialDescTxt", sapMaterialDescTxt},
							{"locale", locale},
							{"orderby", orderby},
							{"asc", asc + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAutoaddMaterialList", params);
		}
		// ]]
		try {
			ReadOnlyDao<SapMaterialDescEntity> sapMaterialDescDao = getDaoProvider().getSapMaterialDescEntityDao();
			
			ListRange r = new ListRange();
			Filter<SapMaterialDescEntity> sapMaterialDescFilter = new FilterImpl<SapMaterialDescEntity>();
			sapMaterialDescFilter.addExpression(Restrictions.eq(SapMaterialDescEntity.ATTR_LANG_CD, locale.getLanguage()));
			sapMaterialDescFilter.addExpression(Restrictions.ilike(SapMaterialDescEntity.ATTR_DISPLAY, "%" + sapMaterialDescTxt + "%"));
			sapMaterialDescFilter.addExpression(asc ? Order.asc(orderby) : (Order.desc(orderby)));
			r.setData(sapMaterialDescDao.findNRecs(skip, count, sapMaterialDescFilter).toArray());
			r.setTotalSize(sapMaterialDescDao.count(sapMaterialDescFilter));
			
			return result = r;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public ListRange fetchMatPlantList(int skip, int count, String plantCd, Long sapMaterialId, Locale locale, String orderby, boolean asc) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"count", count + ""},
							{"plantCd", plantCd},
							{"sapMaterialId", sapMaterialId},
							{"locale", locale},
							{"orderby", orderby},
							{"asc", asc + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchMatPlantList", params);
		}
		// ]]
		try {
			ReadOnlyDao<MaterialEnhancementsEntity> materialEnhancementsEntityDao = getDaoProvider().getMaterialEnhancementsEntityDao();
			
			Filter<MaterialEnhancementsEntity> materialEnhancementsFilter = new FilterImpl<MaterialEnhancementsEntity>();
			if (!StringUtils.empty(plantCd)) {
				materialEnhancementsFilter.addExpression(Restrictions.eq(MaterialEnhancementsEntity.ATTR_PLANT_CD, plantCd));
			}
			if (sapMaterialId != null) {
				materialEnhancementsFilter.addExpression(Restrictions.eq(MaterialEnhancementsEntity.ATTR_SAP_MATERIAL_ID, sapMaterialId));
			}
			materialEnhancementsFilter.addExpression(Restrictions.eq(MaterialEnhancementsEntity.ATTR_LANG_CD, locale.getLanguage()));
			materialEnhancementsFilter.addExpression(asc ? Order.asc(orderby) : (Order.desc(orderby)));
			materialEnhancementsFilter.addExpression(Order.asc(MaterialEnhancementsEntity.ATTR_MATERIAL_DESC));
			
			result = new ListRange();
			List<MaterialEnhancementsEntity> materialEnhancementsList = materialEnhancementsEntityDao.findNRecs(skip, count, materialEnhancementsFilter);
			if (materialEnhancementsList.isEmpty()) {
				result.setData(new Object[] {});
				result.setTotalSize(0);
			}
			else {
				result.setData(materialEnhancementsList.toArray());
				result.setTotalSize(materialEnhancementsEntityDao.count(materialEnhancementsFilter));
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
	
	public ListRange fetchMatPlantNormList(int skip, int count, String norm, String lang, String sorg, String plantCd, Long sapMaterialId, Locale locale, String orderby, boolean asc) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"count", count + ""},
							{"norm", norm},
							{"lang", lang},
							{"sorg", sorg},
							{"orderby", orderby},
							{"asc", asc + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchMatPlantNormList", params);
		}
		// ]]
			
		try {
			ReadOnlyDao<SapMatPlantNormDescEntity> sapMatPlantNormEntityDao = getDaoProvider().getSapMatPlantNormDescEntityDao();
			Filter<SapMatPlantNormDescEntity> filter = sapMatPlantNormEntityDao.createFilter();
			
			if (!StringUtils.empty(plantCd)) {
				filter.addExpression(Restrictions.eq(SapMatPlantNormDescEntity.ATTR_PLANT_CD, plantCd));
			}
			if (sapMaterialId != null) {
				filter.addExpression(Restrictions.eq(SapMatPlantNormDescEntity.ATTR_SAP_MATERIAL_ID, sapMaterialId));
			}
			
			if (!StringUtils.empty(norm)) {
				filter.addExpression(Restrictions.ilike(SapMatPlantNormDescEntity.ATTR_SAP_MATERIAL_NORM_CD, "%" + norm + "%"));	
			}
			
			if (!StringUtils.empty(lang)) {
				filter.addExpression(Restrictions.eq(SapMatPlantNormDescEntity.ATTR_SAP_LANGUAGE_CD, lang));
			}
			
			if (!StringUtils.empty(sorg)) {
				filter.addExpression(Restrictions.ilike(SapMatPlantNormDescEntity.ATTR_SORG_CD, "%" + sorg + "%"));
			}
			filter.addExpression(Restrictions.eq(SapMatPlantNormDescEntity.ATTR_LANG_CD, locale.getLanguage()));
			
			filter.addExpression(asc ? Order.asc(orderby) : (Order.desc(orderby)));
			
			result = new ListRange();
			List<SapMatPlantNormDescEntity> materialNormList = sapMatPlantNormEntityDao.findNRecs(skip, count, filter);
			if (materialNormList.isEmpty()) {
				result.setData(new Object[] {});
				result.setTotalSize(0);
			}
			else {
				result.setData(materialNormList.toArray());
				result.setTotalSize(sapMatPlantNormEntityDao.count(filter));
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

	public MatPlantEntity loadMatPlant(Long matPlantId) {
		// [[
		MatPlantEntity result = null;
		Object[][] params = {{"matPlantId", matPlantId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadMatPlant", params);
		}
		// ]]
		try {
			ReadOnlyDao<MatPlantEntity> matPlantDao = getDaoProvider().getMatPlantEntityDao();
			return result = matPlantDao.findById(matPlantId);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}
	
	/**
	 * Returns the language dependent MatPlantTxEntity
	 * 
	 * @param matPlantId
	 * @param locale
	 * @return
	 */
	public MatPlantTxEntity loadMatPlantTx(Long matPlantId, Locale locale) {
		// [[
		MatPlantTxEntity result = null;
		Object[][] params = {{"matPlantId", matPlantId},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadMatPlantTx", params);
		}
		// ]]
		try {
			ReadWriteDao<MatPlantTxEntity> dao = getDaoProvider().getMatPlantTxEntityDao();
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.and(Restrictions.eq("matPlantTxPK.matPlantId",matPlantId), Restrictions.eq("matPlantTxPK.langCd", locale.getLanguage())));
			MatPlantTxEntity entity = dao.findSingle(filter);
			return result = entity;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public SapMaterialDescEntity loadSapMaterialDesc(Long sapMaterialId, Locale locale) {
		// [[
		SapMaterialDescEntity result = null;
		Object[][] params = {{"sapMaterialId", sapMaterialId},
				{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadSapMaterialDesc", params);
		}
		// ]]
		try {
			ReadOnlyDao<SapMaterialDescEntity> sapMaterialDescDao = getDaoProvider().getSapMaterialDescEntityDao();
			Filter<SapMaterialDescEntity> filter = new FilterImpl<SapMaterialDescEntity>();
			filter.addExpression(Restrictions.eq(SapMaterialDescEntity.ATTR_SAP_MATERIAL_ID, sapMaterialId));
			filter.addExpression(Restrictions.eq(SapMaterialDescEntity.ATTR_LANG_CD, locale.getLanguage()));
			return result = sapMaterialDescDao.findSingle(filter);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public SapMaterialEntity loadSapMaterial(Long sapMaterialId) {
		// [[
		SapMaterialEntity result = null;
		Object[][] params = {{"sapMaterialId", sapMaterialId}};
		final boolean isEnteringExitingEnabled = LOG.isEnteringExitingEnabled();
		if (isEnteringExitingEnabled) {
			LOG.entering(this, "loadSapMaterial", params);
		}
		// ]]
		try {
			ReadOnlyDao<SapMaterialEntity> sapMaterialEntityDao = getDaoProvider().getSapMaterialEntityDao();
			return result = sapMaterialEntityDao.findById(sapMaterialId);
		}
		finally {
		// [[
			if (isEnteringExitingEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public long countMatPlants(String plantCd, Long sapMaterialId) {
		// [[
		long result = -1;
		Object[][] params = {{"plantCd", plantCd},
				{"sapMaterialId", sapMaterialId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "countMatPlants", params);
		}
		// ]]
		try {
			ReadOnlyDao<MatPlantEntity> matPlantDao = getDaoProvider().getMatPlantEntityDao();
			Filter<MatPlantEntity> matPlantFilter = new FilterImpl<MatPlantEntity>();
			matPlantFilter.addExpression(Restrictions.eq(MatPlantEntity.ATTR_PLANT_CD, plantCd));
			matPlantFilter.addExpression(Restrictions.eq(MatPlantEntity.ATTR_SAP_MATERIAL_ID, sapMaterialId));
			return result = matPlantDao.count(matPlantFilter);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public MatPlantEntity createMatPlant(MatPlantEntity matPlant) {
		// [[
		MatPlantEntity result = null;
		Object[][] params = {{"matPlant", matPlant}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createMatPlant", params);
		}
		// ]]
		try {
			ReadWriteDao<MatPlantEntity> matPlantDao = getDaoProvider().getMatPlantEntityDao();
			return result = matPlantDao.persist(matPlant);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public MatPlantEntity updateMatPlant(MatPlantEntity matPlant) {
		// [[
		MatPlantEntity result = null;
		Object[][] params = {{"matPlant", matPlant}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateMatPlant", params);
		}
		// ]]
		try {
			ReadWriteDao<MatPlantEntity> matPlantDao = getDaoProvider().getMatPlantEntityDao();
			return result = matPlantDao.merge(matPlant);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public void createMatPlantTx(MatPlantEntity matPlant, Locale locale, MatPlantTxEntity matPlantTx) {
		// [[
		Object[][] params = {{"matPlantTx", matPlantTx}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createMatPlantTx", params);
		}
		// ]]
		try {
			MatPlantTxPK pk = new MatPlantTxPK();
			pk.setLangCd(locale.getLanguage());
			pk.setMatPlantId(matPlant.getMatPlantId());
			
			matPlantTx.setMatPlantTxPK(pk);

			ReadWriteDao<MatPlantTxEntity> matPlantDao = getDaoProvider().getMatPlantTxEntityDao();
			matPlantDao.persist(matPlantTx);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}
	
	public void updateMatPlantTx(MatPlantTxEntity matPlantTx) {
		// [[
		Object[][] params = {{"matPlantTx", matPlantTx}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateMatPlantTx", params);
		}
		// ]]
		try {
			ReadWriteDao<MatPlantTxEntity> matPlantDao = getDaoProvider().getMatPlantTxEntityDao();
			matPlantDao.merge(matPlantTx);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public void deleteMatPlant(Long matPlantId) {
		// [[
		Object[][] params = {{"matPlantId", matPlantId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "deleteMatPlant", params);
		}
		// ]]
		try {
			MatPlantEntity matPlant = loadMatPlant(matPlantId);
			ReadWriteDao<MatPlantEntity> matPlantDao = getDaoProvider().getMatPlantEntityDao();
			matPlantDao.remove(matPlant);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}
	
	public long countSapMaterialPlants(String plantCd, Long sapMaterialId) {
		// [[
		long result = -1;
		Object[][] params = {{"plantCd", plantCd},
				{"sapMaterialId", sapMaterialId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "countSapMaterialPlants", params);
		}
		// ]]
		try {
			ReadOnlyDao<SapMaterialPlantEntity> sapMaterialPlantDao = getDaoProvider().getSapMaterialPlantEntityDao();
			Filter<SapMaterialPlantEntity> sapMaterialPlantFilter = new FilterImpl<SapMaterialPlantEntity>();
			sapMaterialPlantFilter.addExpression(Restrictions.eq(SapMaterialPlantEntity.ATTR_PLANT_CD, plantCd));
			sapMaterialPlantFilter.addExpression(Restrictions.eq(SapMaterialPlantEntity.ATTR_SAP_MATERIAL_ID, sapMaterialId));
			return result = sapMaterialPlantDao.count(sapMaterialPlantFilter);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}
	
	/**
	 * Service to fetch refdata for material norms on MaterialEnchancement tab.
	 * @param request
	 * @return
	 */
	public Map<String, String> getSapLanguageCodes(Locale locale) {
		// [[
		Map<String, String> result1 = null;
		Object[][] params = {{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getSapLanguageCodes", params);
		}
		// ]]
		try {
			
			FilterImpl<SapLanguageTxEntity> filter = new FilterImpl<SapLanguageTxEntity>();
			List<SapLanguageTxEntity> sapLanguageTxEntityList = referentialDataService.findTxEntityList(SapLanguageTxEntity.class, true, locale, SapLanguageTxEntity.ATTR_TXT, true);
			
			Map<String, String> result = new LinkedHashMap<String, String>(sapLanguageTxEntityList.size());
			
			for (SapLanguageTxEntity sapLanguageTxEntity : sapLanguageTxEntityList) {
				result.put(sapLanguageTxEntity.getSapLanguageTxPK().getSapLanguageBs().getSapLanguageCd(), sapLanguageTxEntity.getTxt());
			}
			
			return result1 = result;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result1);
			}
		// ]]
		}
	}
	
	/**
	 * Loads the contract items, which has a sap material, which can replace the
	 * planning material given in the input sapContractItem. The input contract 
	 * item is also included in the list.
	 * If the input contract item is not a planning material, null is returned.
	 * If the input contract item has no replacement material in the same contract,
	 * empty list is returned.
	 */
	public List<SapContractItemEntity> getReplacingContractItemList(Long sapContractItemId) {
		// [[
		List<SapContractItemEntity> result = null;
		Object[][] params = {{"sapContractItemId", sapContractItemId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getReplacingContractItemList", params);
		}
		// ]]
		try {
			ReadOnlyDao<SapContractItemEntity> contractItemDao = getDaoProvider().getSapContractItemEntityDao();
			SapContractItemEntity sapContractItem = contractItemDao.findById(sapContractItemId);
			
			boolean isAgg = false;
			if (LogonUtil.isAggPlanningMaterial(sapContractItem.getSapMaterial())) {
				isAgg = true;
			}
			boolean isLandfill = false;
			if (LogonUtil.isLandfillPlanningMaterial(sapContractItem.getSapMaterial())) {
				isLandfill = true;
			}
			if (!isAgg && !isLandfill) {
				return result;
			}
			
			Long sapContractId = sapContractItem.getSapContract().getSapContractId();
			ReadOnlyDao<SapContractEntity> sapContractDao = getDaoProvider().getSapContractEntityDao();
			SapContractEntity sapContractEntity = sapContractDao.findById(sapContractId);
			
			result = new ArrayList<SapContractItemEntity>();
			for (SapContractItemEntity siblingSapContractItem : sapContractEntity.getSapContractItems()) {
				SapMaterialEntity siblingMaterial = siblingSapContractItem.getSapMaterial();
				if (Constant.MATERIAL_PACKAGE_BULK_DUMPED.equals(siblingMaterial.getMaterialPackage().getMaterialPackageCd())
						&& ((isLandfill && siblingMaterial.getMaterialIsLf()) 
								|| (isAgg && siblingMaterial.getMaterialIsAgg())
								|| siblingSapContractItem.getSapContractItemId().equals(sapContractItemId))) {
					
					result.add(siblingSapContractItem);
				}
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

	/**
	 * Tries to find a contract item with the give contractItemNumber and sapMaterialIfcNo, which can be
	 * used as a replacing contract item for the given sap contract item (which must be a planning material).
	 */
	@AdnTrace
	public SapContractItemEntity findMatchingContractItem(Long contractItemNumber, String sapMaterialIfcNo, 
			SapContractItemEntity planningMaterialContractItem) {
		SapContractItemEntity replacingContractItem = null;
		List<SapContractItemEntity> replacingSapContractItemList = getReplacingContractItemList(planningMaterialContractItem.getSapContractItemId());
		if (replacingSapContractItemList == null) {
			return replacingContractItem;
		}
		for (SapContractItemEntity possibleReplacingSapContractItem : replacingSapContractItemList) {
			boolean contractItemMatching = false;
			boolean materialMatching = false;
			if (contractItemNumber == null
					|| contractItemNumber.equals(possibleReplacingSapContractItem.getContractItemNo())) {
				contractItemMatching = true;
			}
			if (sapMaterialIfcNo.equals(possibleReplacingSapContractItem.getSapMaterial().getSapMaterialIfcNo())) {
				materialMatching = true;
			}
			if (contractItemMatching && materialMatching) {
				replacingContractItem = possibleReplacingSapContractItem;
				break;
			}
		}
		return replacingContractItem;
	}

	/**
	 * Must be called when a new material is chosen by the user instead of the planning material. Sets
	 * the material, contractitem in the related orderitem. The orderitem must be a valid orderitem of
	 * the given order.
	 * 
	 */
	@AdnTrace
	public OrderEntity replaceMaterialInItem(OrderEntity order, Long orderItemId, Long sapContractItemId) {
		OrderItemEntity orderItem = null;
		for (OrderItemEntity anItem : order.getOrderItems()) {
			if (anItem.getOrderItemId().equals(orderItemId)) {
				orderItem = anItem;
				break;
			}
		}
		if (orderItem == null) {
			throw new LogonException("orderItemId is not found in order", Reason.RES_IMPL_BUG);
		}

		SapContractItemEntity sapContractItemEntity = contractItemStore.findById(sapContractItemId);
		orderItem.setSapContractItem(sapContractItemEntity);

		SapMaterialEntity realMaterial = sapContractItemEntity.getSapMaterial();
		orderItem.setSapMaterial(realMaterial);

		LoadEntity load = orderItem.getLoad();
		if (load != null) {
			load.setSapMaterial(realMaterial);
		}

		return order;
	}

	/**
	 * Must be called when a new material is chosen by the user instead of the planning material. Sets
	 * the material, contractitem in the related orderitem. The orderitem must be a valid orderitem of
	 * the given order.
	 */
	@AdnTrace
	public OrderEntity updateMaterialAttributes(OrderEntity order, Long orderItemId, 
			PlantBsEntity prodPlant, UomBsEntity uomBs, BigDecimal quantity) {

		OrderItemEntity orderItem = null;
		for (OrderItemEntity anItem : order.getOrderItems()) {
			if (anItem.getOrderItemId().equals(orderItemId)) {
				orderItem = anItem;
				break;
			}
		}
		if (orderItem == null) {
			throw new LogonException("orderItemId is not found in order", Reason.RES_IMPL_BUG);
		}

		orderItem.setProductionPlant(prodPlant);
		if (uomBs != null) {
			orderItem.setOrderedUom(uomBs);
		}
		if (quantity != null) {
			orderItem.setOrderedQuantity(quantity);
		}

		return order;

	}

	/**
	 * Gets the default production plant for the material, which is connected to the given contractItem.
	 * Tries to get it in the following order (until found one):
	 * 1. material.prodPlant
	 * 2. contractItem.productionPlant
	 * 
	 * @param contractItem
	 * @return a production plant, or null if not found
	 */
	public PlantBsEntity getDefaultProductionPlant(SapContractItemEntity contractItem) {
		// [[
		PlantBsEntity result = null;
		Object[][] params = {{"contractItem", contractItem}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getDefaultProductionPlant", params);
		}
		// ]]
		try {
			SapMaterialEntity material = contractItem.getSapMaterial();
			PlantBsEntity prodPlant = material.getProdPlant();
			if (prodPlant == null) {
				prodPlant = contractItem.getProductionPlant();
			}
			return result = prodPlant;
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
