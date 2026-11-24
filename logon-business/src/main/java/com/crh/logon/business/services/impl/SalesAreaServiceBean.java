/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;
import javax.persistence.Query;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Expression;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.dispatchers.SalesAreaServiceDispatcher;
import com.crh.logon.business.services.impl.cron.ScheduledTask;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.SalesAreaServiceLocalInterface;
import com.crh.logon.business.services.utils.SapMaterialUtil;
import com.crh.logon.business.services.utils.validation.ValidationErrorReason;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.CustorderError;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.SapMaterialSareaInfoEntity;
import com.crh.logon.persistence.entities.views.SareaDisplayEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class SalesAreaServiceBean extends SalesAreaServiceDispatcher implements SalesAreaServiceLocalInterface {

	private static final String UPDATE_PLANNING_MAT_SAREA = "begin " + Constant.SCHEMA_USER_PREPEND + "update_planning_mat_sarea(); end;";
	private static final String UPSERT_PLANNING_CONTRACT_ITEMS_FOR_ALL_SALES_AREA = "begin " + Constant.SCHEMA_USER_PREPEND + "upsert_planning_contract_items(); end;";
	private static final String UPSERT_PLANNING_CONTRACT_ITEMS_FOR_ONE_SALES_AREA = "begin " + Constant.SCHEMA_USER_PREPEND + "upsert_planning_contract_items(i_sales_area_id=>?); end;";

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SalesAreaServiceBean.class);
	private static final String EMPTY_PLANNING_SAP_MATERIAL_NUMBER_PARAMETER = null;

	public ListRange fetchSalesAreaList(int skip, int count,String sorgCd, Locale locale, String orderby, boolean asc) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"count", count + ""},
							{"sorgCd", sorgCd},
							{"locale", locale},
							{"orderby", orderby},
							{"asc", asc + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSalesAreaList", params);
		}
		// ]]
		try {
			ReadOnlyDao<SareaDisplayEntity> readOnlyDao = getDaoProvider().getSareaDisplayEntityDao();
			
			Filter<SareaDisplayEntity> filterSSCE = new FilterImpl<SareaDisplayEntity>();
			
			filterSSCE.addExpression(asc ? Order.asc(orderby): (Order.desc(orderby)));
			filterSSCE.addExpression(Restrictions.eq(SareaDisplayEntity.ATTR_LANG_CD, locale.getLanguage()));
			if(StringUtils.notEmpty(sorgCd)){
				filterSSCE.addExpression(Restrictions.eq(SareaDisplayEntity.ATTR_SORG_CD, sorgCd));
			}
			ListRange listRange = new ListRange();
			
			List<SareaDisplayEntity> sareaEntities = readOnlyDao.findNRecs(skip, count, filterSSCE);
			
			if (sareaEntities.isEmpty()) {
				listRange.setData(new Object[] {});
				listRange.setTotalSize(0);
			}
			else {
				listRange.setData(sareaEntities.toArray());
				listRange.setTotalSize(readOnlyDao.count(filterSSCE));
			}
			
			return result = listRange;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public SareaDisplayEntity loadSalesArea(String compositeId){
		// [[
		SareaDisplayEntity result = null;
		Object[][] params = {{"compositeId", compositeId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadSalesArea", params);
		}
		// ]]
		try {
			ReadOnlyDao<SareaDisplayEntity> readOnlyDao = getDaoProvider().getSareaDisplayEntityDao();
			SareaDisplayEntity s = readOnlyDao.findById(compositeId);
			return result = s;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	@AdnTrace
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateSalesArea(SareaDisplayEntity salesAreaUISettings) {
			ReadWriteDao<SareaEntity> rwDao = getDaoProvider().getSareaEntityDao();
			SareaEntity salesArea = rwDao.findById(salesAreaUISettings.getSareaId());
			
			// mapping
			salesArea.setDelnoteUsesWsNumberFlag(salesAreaUISettings.getDelnoteUsesWsNumberFlag());
			salesArea.setDocumentDelTimeWindowFlag(salesAreaUISettings.getDocumentDelTimeWindowFlag());
			salesArea.setDocumentDistanceFlag(salesAreaUISettings.getDocumentDistanceFlag());
			salesArea.setDocumentCommCarrierFlag(salesAreaUISettings.getDocumentCommCarrierFlag());
			salesArea.setDocumentExecCarrierFlag(salesAreaUISettings.getDocumentExecCarrierFlag());
			salesArea.setSareaDisplayLongname(salesAreaUISettings.getSareaDisplayLongname());			
			salesArea.setAllowBimodalCreationTillMi(salesAreaUISettings.getAllowBimodalCreationTillMi());
			salesArea.setShortTermOrderThresholdMi(salesAreaUISettings.getShortTermOrderThresholdMi());
			salesArea.setShiptoOpen1From(salesAreaUISettings.getShiptoOpen1From());
			salesArea.setShiptoOpen1To(salesAreaUISettings.getShiptoOpen1To());
			salesArea.setShiptoOpen2From(salesAreaUISettings.getShiptoOpen2From());
			salesArea.setShiptoOpen2To(salesAreaUISettings.getShiptoOpen2To());
			salesArea.setMailFrom(salesAreaUISettings.getMailFrom());
			salesArea.setMailTestTo(salesAreaUISettings.getMailTestTo());
			salesArea.setMailBcc(salesAreaUISettings.getMailBcc());
			salesArea.setMailRailadmin(salesAreaUISettings.getMailRailadmin());
			salesArea.setDelaySendToSapMi(salesAreaUISettings.getDelaySendToSapMi());
			salesArea.setAllowNdWebOrderUntilMi(salesAreaUISettings.getAllowNdWebOrderUntilMi());
			salesArea.setAllowSdWebOrderUntilMi(salesAreaUISettings.getAllowSdWebOrderUntilMi());
			salesArea.setApplyNdLeadtimeChkUntilMi(salesAreaUISettings.getApplyNdLeadtimeChkUntilMi());
			salesArea.setApplySdLeadtimeChkFromMi(salesAreaUISettings.getApplySdLeadtimeChkFromMi());
			salesArea.setExactQuantityOrderTolerance(salesAreaUISettings.getExactQuantityOrderTolerance());
			salesArea.setCustCommFlag(salesAreaUISettings.getCustCommFlag());
			salesArea.setCsEnableOneTimeCustFlag(salesAreaUISettings.getCsEnableOneTimeCustFlag());
			salesArea.setCsShowPaymentMthFlag(salesAreaUISettings.getCsShowPaymentMthFlag());
			salesArea.setCsPriceType(salesAreaUISettings.getCsPriceType());  
			salesArea.setCsShowTotalsTextFlag(salesAreaUISettings.getCsShowTotalsTextFlag());
			salesArea.setCsShowCustTaxCodeFlag(salesAreaUISettings.getCsShowCustTaxCodeFlag());
			salesArea.setCsShowContractCurrencyFlag(salesAreaUISettings.getCsShowContractCurrencyFlag());
			salesArea.setDnShowDispatcherDriverFlag(salesAreaUISettings.getDnShowDispatcherDriverFlag());
			salesArea.setDnShowFixText(salesAreaUISettings.getDnShowFixText());
			salesArea.setDnShowPlantCycleFlag(salesAreaUISettings.getDnShowPlantCycleFlag());
			salesArea.setDnShowSealsInfoFlag(salesAreaUISettings.getDnShowSealsInfoFlag());
			salesArea.setDnShowSiloNoFlag(salesAreaUISettings.getDnShowSiloNoFlag());
			salesArea.setDnShowTrailerFlag(salesAreaUISettings.getDnShowTrailerFlag());
			salesArea.setDnShowCarrierCrmFlag(salesAreaUISettings.getDnShowCarrierCrmFlag());
			salesArea.setDnShowShiptoContactFlag(salesAreaUISettings.getDnShowShiptoContactFlag());
			salesArea.setExpShowTotalTxtFlag(salesAreaUISettings.getExpShowTotalTxtFlag());
			salesArea.setExpShowLegalText1Flag(salesAreaUISettings.getExpShowLegalText1Flag());
			salesArea.setExpShowLegalText2Flag(salesAreaUISettings.getExpShowLegalText2Flag());
			salesArea.setExpShowIssueDatLocFlag(salesAreaUISettings.getExpShowIssueDatLocFlag());
			salesArea.setExpShowAdInvoiceNoFlag(salesAreaUISettings.getExpShowAdInvoiceNoFlag());
			salesArea.setExpShowCtrlNoFlag(salesAreaUISettings.getExpShowCtrlNoFlag());
			salesArea.setExpMatPriceType(salesAreaUISettings.getExpMatPriceType());
			salesArea.setExpShowZcode12Flag(salesAreaUISettings.getExpShowZcode12Flag());
			salesArea.setEnablePricingCallFlag(salesAreaUISettings.getEnablePricingCallFlag());
			salesArea.setAllowWebOrderOnWeekend(salesAreaUISettings.getAllowWebOrderOnWeekend());
			salesArea.setForceToSapFlag(salesAreaUISettings.getForceToSapFlag());
			salesArea.setRailReturnSareaFlag(salesAreaUISettings.getRailReturnSareaFlag());
			salesArea.setDbltDistanceL1(salesAreaUISettings.getDbltDistanceL1());
			salesArea.setDbltDistanceL2(salesAreaUISettings.getDbltDistanceL2());
			salesArea.setH1EarlyOrderLimit(salesAreaUISettings.getH1EarlyOrderLimit());
			salesArea.setH2EarliestDeliveryFrom(salesAreaUISettings.getH2EarliestDeliveryFrom());
			salesArea.setDbltOffsetD1(salesAreaUISettings.getDbltOffsetD1());
			salesArea.setDbltOffsetD2(salesAreaUISettings.getDbltOffsetD2());
			salesArea.setDbltOffsetD3(salesAreaUISettings.getDbltOffsetD3());
			salesArea.setDbltOffsetD4(salesAreaUISettings.getDbltOffsetD4());
			salesArea.setDbltOffsetD5(salesAreaUISettings.getDbltOffsetD5());
			salesArea.setDbltOffsetD6(salesAreaUISettings.getDbltOffsetD6());
			salesArea.setEnableDemandPlanningFlag(salesAreaUISettings.getEnableDemandPlanningFlag());
			salesArea.setDemandPlanLatestUntilH(salesAreaUISettings.getDemandPlanLatestUntilH());
			
			if (isAllowPlanningMaterialFlagChangedByUser(salesAreaUISettings, salesArea)) {
				salesArea.setAllowPlanningMaterialFlag(salesAreaUISettings.getAllowPlanningMaterialFlag());			
			}
		
	}

	private boolean isAllowPlanningMaterialFlagChangedByUser(SareaDisplayEntity salesAreaUISettings,
			SareaEntity salesArea) {
		Boolean originalAllowPlanningMaterialFlag = salesArea.getAllowPlanningMaterialFlag();
		Boolean inputAllowPlanningMaterialFlag = salesAreaUISettings.getAllowPlanningMaterialFlag();
		return !originalAllowPlanningMaterialFlag.equals(inputAllowPlanningMaterialFlag);
	}
	
	@AdnTrace
	public void maintainAllPlanningMaterialContractItems() {
		Query nativeQuery = em.createNativeQuery(UPSERT_PLANNING_CONTRACT_ITEMS_FOR_ALL_SALES_AREA);
		nativeQuery.executeUpdate();
	}
	
	/**
	 * Called as a cron job. Calls update_planning_mat_sarea stored procedure.
	 */
	@AdnTrace
	@ScheduledTask(name = "update_planning_mat_sarea", schedule = false)
	public void updatePlanningMatSarea() {
		Query nativeQuery = em.createNativeQuery(UPDATE_PLANNING_MAT_SAREA);
		nativeQuery.executeUpdate();
	}
	
	@AdnTrace
	public void maintainPlanningMaterialContractItems(Long salesAreaId) {
		Query nativeQuery = em.createNativeQuery(UPSERT_PLANNING_CONTRACT_ITEMS_FOR_ONE_SALES_AREA);
		nativeQuery.setParameter(1, salesAreaId);
		nativeQuery.executeUpdate();
	}
	
	/**
	 * Creates one contract item for every contract of the given sarea
	 * with the planning material, if it's still not existing.
	 * 
	 * Intended to call when the allow_planning_material_flag has just
	 * been set to true.
	 * 
	 * @param landfillPlanningMaterial
	 * @param aggregatePlanningMaterial
	 * @param sapContractList
	 */
	public void enablePlanningMaterialContractItem(SapMaterialEntity landfillPlanningMaterial, 
													SapMaterialEntity aggregatePlanningMaterial, 
													List<SapContractEntity> sapContractList) {
			// [[
			Object[][] params = {{"landfillPlanningMaterial", landfillPlanningMaterial},
								{"aggregatePlanningMaterial", aggregatePlanningMaterial},
								{"sapContractList", sapContractList}};
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(this, "enablePlanningMaterialContractItem", params);
			}
			// ]]
			try {
				for (SapContractEntity sapContract : sapContractList) {
					createOrUpdatePlanningMaterialContractItem(Constant.SAP_MATERIAL_NO_LANDFILL_PLANNING, landfillPlanningMaterial, sapContract);
					createOrUpdatePlanningMaterialContractItem(Constant.SAP_MATERIAL_NO_AGG_PLANNING, aggregatePlanningMaterial, sapContract);
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
	 * It creates a planning material contract item based on the parameters if it doesn't exists.
	 * If it exists then it will be marked for not deletion (MARKED_FOR_DELETION_FALSE)
	 * 
	 * @param planningMaterialNumber
	 * @param planningMaterial
	 * @param sapContractEntity
	 */
	private void createOrUpdatePlanningMaterialContractItem(String planningMaterialNumber, SapMaterialEntity planningMaterial, SapContractEntity sapContractEntity) {
			// [[
			Object[][] params = {{"planningMaterialNumber", planningMaterialNumber},
								{"planningMaterial", planningMaterial},
								{"sapContractEntity", sapContractEntity}};
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(this, "createOrUpdateContractItem", params);
			}
			// ]]
			try {
			SapContractItemEntity planningMaterialContractItem = SapMaterialUtil.getPlanningMaterialContractItem(
					planningMaterialNumber, sapContractEntity);
				
				if (planningMaterialContractItem == null) {
					ReadWriteDao<SapContractItemEntity> sapContractItemDao = getDaoProvider().getSapContractItemEntityDao();
					SapContractItemEntity sapContractItemEntity = SapMaterialUtil.
							createNewPlanningMaterialContractItem(sapContractEntity, planningMaterial);
					sapContractItemDao.persist(sapContractItemEntity);
				}
				else {
					planningMaterialContractItem.setMarkedForDeletion(Constant.MARKED_FOR_DELETION_FALSE);
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
	
	@AdnTrace
	private ValidationErrorReason validateLandfillPlanningMaterialEnabled(String sorgCd, String distributionChannel) {
		return validatePlanningMaterialEnabled(Constant.SAP_MATERIAL_NO_LANDFILL_PLANNING, sorgCd,
				distributionChannel);
	}
	
	@AdnTrace
	private ValidationErrorReason validateAggPlanningMaterialEnabled(String sorgCd, String distributionChannel) {
		return validatePlanningMaterialEnabled(Constant.SAP_MATERIAL_NO_AGG_PLANNING, sorgCd,
				distributionChannel);
	}

	@AdnTrace
	public Collection<ValidationErrorReason> validateSalesArea(String sorgCd, String distributionChannel) {
		Collection<ValidationErrorReason> reasons = new ArrayList<ValidationErrorReason>();

		ValidationErrorReason validationErrorReason1 =
				validatePlanningMaterialEnabled(Constant.SAP_MATERIAL_NO_LANDFILL_PLANNING, sorgCd,
						distributionChannel);
		if (validationErrorReason1 != null) {
			reasons.add(validationErrorReason1);
		}

		ValidationErrorReason validationErrorReason2 =
				validatePlanningMaterialEnabled(Constant.SAP_MATERIAL_NO_AGG_PLANNING, sorgCd,
						distributionChannel);
		if (validationErrorReason2 != null) {
			reasons.add(validationErrorReason2);
		}

		return reasons;
	}

	/**
	 * Checks whether for the given sales area exactly one of the specified planning materials exist
	 * if the planning material flag in turned on for that sales area.
	 * 
	 * @param planningSapMaterialNumber
	 * @param salesOrganisationCode
	 * @param distributionChannel
	 *
	 */
	private ValidationErrorReason validatePlanningMaterialEnabled(String planningSapMaterialNumber,
			String salesOrganisationCode, String distributionChannel) {
		if (planningSapMaterialNumber == EMPTY_PLANNING_SAP_MATERIAL_NUMBER_PARAMETER) {
			throw new LogonException("planningMaterialSapMatNo mustn't be null",
					Reason.RES_IMPL_BUG);
		}
		final List<SapMaterialSareaInfoEntity> sapMaterialSalesAreas = fetchSapMatSarea(
				salesOrganisationCode, distributionChannel, planningSapMaterialNumber);
		if (sapMaterialSalesAreas.isEmpty()) {
			return new ValidationErrorReason(
					CustorderError.PLANNING_MATERIAL_NOT_AVAILABLE_TO_SAREA,
					ValidationErrorReason.DOES_NOT_REFER_TO_NEW_ENTITY,
					ValidationErrorReason.EMPTY_ADDITIONAL_INFO);
		}
		return null;
	}
	
	/**
	 * Removes a contract item for every contract of the given sarea
	 * with the planning material, if it's existing.
	 * 
	 * Intended to call when the allow_planning_material_flag has just
	 * been set to false.
	 */
	public void disablePlanningMaterialContractItem(SapMaterialEntity landfillPlanningMaterial, 
													SapMaterialEntity aggPlanningMaterial,
													List<SapContractEntity> sapContractList) {
			// [[
			Object[][] params = {{"landfillPlanningMaterial", landfillPlanningMaterial},
								{"aggPlanningMaterial", aggPlanningMaterial},
								{"sapContractList", sapContractList}};
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(this, "disablePlanningMaterialContractItem", params);
			}
			// ]]
			try {
				for (SapContractEntity sapContract : sapContractList) {
					SapContractItemEntity aggregatePlanningMaterialContractItem = SapMaterialUtil.getPlanningMaterialContractItem(Constant.SAP_MATERIAL_NO_AGG_PLANNING, sapContract);
					if (aggregatePlanningMaterialContractItem != null) {
						deleteContractItem(landfillPlanningMaterial, aggPlanningMaterial, aggregatePlanningMaterialContractItem);
					}
					
					SapContractItemEntity landfillPlanningMaterialContractItem = SapMaterialUtil.getPlanningMaterialContractItem(Constant.SAP_MATERIAL_NO_LANDFILL_PLANNING, sapContract);
					if (landfillPlanningMaterialContractItem != null) {
						deleteContractItem(landfillPlanningMaterial, aggPlanningMaterial, landfillPlanningMaterialContractItem);
					}
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
	 * Only marks the contract item for deletion.
	 */
	private void deleteContractItem(SapMaterialEntity landfillPlanningMaterial, 
									SapMaterialEntity aggregatePlanningMaterial, 
									SapContractItemEntity sapContractItemEntity) {
				deleteAllPlanningMatRestr(landfillPlanningMaterial, aggregatePlanningMaterial, sapContractItemEntity);
				sapContractItemEntity.setMarkedForDeletion(Constant.MARKED_FOR_DELETION);
	}
		
	public SareaEntity findSalesAreaByIdentifier(java.lang.Long sareaId) {
		return getDaoProvider().getSareaEntityDao().findById(sareaId);
	}

	public SareaEntity findSalesAreaByDisplayName(String salesAreaDisplayLongName) {
		DaoProvider dataAccessProvider = getDaoProvider();
		final ReadOnlyDao<SareaEntity> dataAccess = dataAccessProvider.getSareaEntityDao();
		final Filter<SareaEntity> filter = dataAccess.createFilter();
		Expression displayedLongNameMatches = Restrictions.eq(
				SareaEntity.ATTR_SAREA_DISPLAY_LONGNAME, 
				salesAreaDisplayLongName);
		filter.addExpression(displayedLongNameMatches);
		SareaEntity singleResult = dataAccess.findSingle(filter);
		return singleResult;
	}

	public List<SareaEntity> findSareaEntityList() {
		return findSareaEntityList(false, null, true);
	}

	public List<SareaEntity> findActiveSareaEntityList() {
		return findSareaEntityList(true, null, true);
	}
	
	public List<SareaEntity> findSareaEntityList(boolean isActive, String orderByField, boolean isAscending) {
		// [[
		List<SareaEntity> result = null;
		Object[][] params = {{"isActive", isActive + ""},
							{"orderByField", orderByField},
							{"isAscending", isAscending + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findSareaEntityList", params);
		}
		// ]]
		try {
			final ReadOnlyDao<SareaEntity> dao = getDaoProvider().getSareaEntityDao();
			final Filter<SareaEntity> filter = dao.createFilter();
			if (isActive) {
				filter.addExpression(Restrictions.eq(SareaEntity.ATTR_CTL_ACT_CD, isActive));
			}
			if (orderByField == null) {
				orderByField = "sareaId";
			}
			if (isAscending) {
				filter.addExpression(Order.asc(orderByField));
			}
			else {
				filter.addExpression(Order.desc(orderByField));
			}
			return result = dao.find(filter);
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
	 * It search for the shipto salesarea restrictions which related to the sapcontractitem and the sap material is a landfill planning material or
	 * an aggregate planning material
	 * @param landfillPlanningMaterial
	 * @param aggregatePlanningMaterial
	 * @param sapContractItem
	 * @return List<ShiptoSareaRestrEntity>
	 */
	private List<ShiptoSareaRestrEntity> findPlanningMatRestr(SapMaterialEntity landfillPlanningMaterial,
																SapMaterialEntity aggregatePlanningMaterial,
																SapContractItemEntity sapContractItem) {
			// [[
			List<ShiptoSareaRestrEntity> result = null;
			Object[][] params = {{"landfillPlanningMaterial", landfillPlanningMaterial},
								{"aggregatePlanningMaterial", aggregatePlanningMaterial},
								{"sapContractItem", sapContractItem}};
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(this, "findPlanningMatRestr", params);
			}
			// ]]
			try {
				ReadOnlyDao<ShiptoSareaRestrEntity> dao = getDaoProvider().getShiptoSareaRestrEntityDao();
				
				Filter<ShiptoSareaRestrEntity> filter = dao.createFilter();
				filter.addExpression(Restrictions.eq(ShiptoSareaRestrEntity.ATTR_SAP_CONTRACT_ITEM_ID, sapContractItem.getSapContractItemId()));
				
				Expression exprLandfill = Restrictions.eq(ShiptoSareaRestrEntity.ATTR_SAP_MATERIAL_ID, landfillPlanningMaterial.getSapMaterialId());
				Expression exprAgg = Restrictions.eq(ShiptoSareaRestrEntity.ATTR_SAP_MATERIAL_ID, aggregatePlanningMaterial.getSapMaterialId());
				filter.addExpression(Restrictions.or(exprLandfill, exprAgg));
				
				return result = dao.find(filter);
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
	 * It deletes all planning material restrictions where the sapmaterial is landfill planning material or an aggregate landfill material and 
	 * the restriction is related to the given sap contract item.
	 * 
	 * @param landfillPlanningMaterial
	 * @param aggregatePlanningMaterial
	 * @param sapContractItem
	 */
	private void deleteAllPlanningMatRestr(SapMaterialEntity landfillPlanningMaterial,
											SapMaterialEntity aggregatePlanningMaterial,
											SapContractItemEntity sapContractItem) {
			// [[
			Object[][] params = {{"landfillPlanningMaterial", landfillPlanningMaterial},
								{"aggregatePlanningMaterial", aggregatePlanningMaterial},
								{"sapContractItem", sapContractItem}};
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(this, "deleteAllPlanningMatRestr", params);
			}
			// ]]
			try {
				List<ShiptoSareaRestrEntity> planningMatRestrList = findPlanningMatRestr(landfillPlanningMaterial, aggregatePlanningMaterial, sapContractItem);
				ReadWriteDao<ShiptoSareaRestrEntity> dao = getDaoProvider().getShiptoSareaRestrEntityDao();
				for (ShiptoSareaRestrEntity shiptoSareaRestrEntity : planningMatRestrList) {
					dao.remove(shiptoSareaRestrEntity);
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
	 * Fetches a list of SapMaterialSarea entities for the given sales organization code and 
	 * distribution channel.
	 * When the sapMaterialNo is provided it will filter for that also.
	 * 
	 * @param sorgCd - The sales organization code to filter for.
	 * @param distCh - The distribution channel to filter for.
	 * @param sapMaterialNo - When provided, will add to the filter the SAP material No. also.
	 * 
	 * @return List of SapMaterialSarea entities.
	 */
	@AdnTrace
	public List<SapMaterialSareaInfoEntity> fetchSapMatSarea(String sorgCd, String distCh, String sapMaterialNo) {

		ReadOnlyDao<SapMaterialSareaInfoEntity> sapMaterialSareaInfoEntityDao = getDaoProvider().getSapMaterialSareaInfoEntityDao();
		final Filter<SapMaterialSareaInfoEntity> filter = sapMaterialSareaInfoEntityDao.createFilter();

		filter.addExpression(Restrictions.eq(SapMaterialSareaInfoEntity.ATTR_SORG_CD, sorgCd));
		filter.addExpression(Restrictions.eq(SapMaterialSareaInfoEntity.ATTR_DISTRIBUTION_CHANNEL, distCh));

		if (StringUtils.notEmpty(sapMaterialNo)) {
			filter.addExpression(Restrictions.eq(SapMaterialSareaInfoEntity.ATTR_SAP_MATERIAL_NO, sapMaterialNo));
		}

		return sapMaterialSareaInfoEntityDao.find(filter);
	}
}
