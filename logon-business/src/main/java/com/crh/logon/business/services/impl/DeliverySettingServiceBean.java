/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
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
import com.crh.logon.business.ordercreation.DeliverySettingFilterLogic;
import com.crh.logon.business.ordercreation.DeliverySettingLanguageDependentResolver;
import com.crh.logon.business.ordercreation.DeliverySettingProductMap;
import com.crh.logon.business.ordercreation.DeliverySettingSalesAreaMap;
import com.crh.logon.business.ordercreation.DeliverySettingShiptoMap;
import com.crh.logon.business.ordercreation.GlobalTransportBackgroundData;
import com.crh.logon.business.ordercreation.MaterialAvailabilityBackgroundData;
import com.crh.logon.business.ordercreation.MaterialSelectionInitializer;
import com.crh.logon.business.ordercreation.SapFinancialData;
import com.crh.logon.business.ordercreation.ShiptoSalesAreaFinancialbackgroundData;
import com.crh.logon.business.ordercreation.dto.CommercialCarrierDTO;
import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO.DeliverySettingLangDepData;
import com.crh.logon.business.ordercreation.dto.ExecutingCarrierDTO;
import com.crh.logon.business.ordercreation.dto.LoadingPointDTO;
import com.crh.logon.business.ordercreation.dto.PlantDTO;
import com.crh.logon.business.ordercreation.dto.PoolDTO;
import com.crh.logon.business.ordercreation.dto.ShippingpointDTO;
import com.crh.logon.business.ordercreation.dto.SimpleVehicleTypeWrapper;
import com.crh.logon.business.ordercreation.dto.TruckTypeWrapper;
import com.crh.logon.business.ordercreation.dto.WagonTypeWrapper;
import com.crh.logon.business.services.dispatchers.DeliverySettingServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.BestellungServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.CarrierServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DeliverySettingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DispokarteServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderDetailServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.PoolAdminServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.QuantityRestrictionServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TimeZoneServiceLocalInterface;
import com.crh.logon.business.services.utils.ContractItemQuantity;
import com.crh.logon.business.services.utils.DbUtils;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.SapMaterialUtil;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.business.stores.dictionaries.UnitOfMeasurementStoreInterface;
import com.crh.logon.business.stores.domain.PoolStoreInterface;
import com.crh.logon.business.stores.domain.ShiptoSiloStoreInterface;
import com.crh.logon.business.stores.domain.views.IncotermDeliveryTypeViewStoreInterface;
import com.crh.logon.business.stores.sap.AlternativeUnitOfMeasurementInterface;
import com.crh.logon.business.stores.sap.BusinessPartnerInterface;
import com.crh.logon.business.stores.sap.ContractItemStoreInterface;
import com.crh.logon.business.stores.sap.ContractStoreInterface;
import com.crh.logon.business.stores.sap.ContractTypeStoreInterface;
import com.crh.logon.business.stores.sap.SapMaterialStoreInterface;
import com.crh.logon.business.stores.sap.ShiptoSareaStoreInterface;
import com.crh.logon.constants.CallerContext;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.authorization.Authorizer;
import com.crh.logon.persistence.entities.common.FireqMaterialMixdesignEntity;
import com.crh.logon.persistence.entities.common.PoolEntity;
import com.crh.logon.persistence.entities.common.PoolTxEntity;
import com.crh.logon.persistence.entities.common.PoolTxPK;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoBilltoEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import com.crh.logon.persistence.entities.common.ShiptoTruckTypeEntity;
import com.crh.logon.persistence.entities.common.ShiptoWagonTypeEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeTxPK;
import com.crh.logon.persistence.entities.refdata.MaterialPackageBsEntity;
import com.crh.logon.persistence.entities.refdata.MaterialPackageTxEntity;
import com.crh.logon.persistence.entities.refdata.MaterialPackageTxPK;
import com.crh.logon.persistence.entities.refdata.OrderSpiTxEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.SapContractTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.SapContractTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.SapContractTypeTxPK;
import com.crh.logon.persistence.entities.refdata.SimpleVehicleTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.TruckTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.TruckTypeTrspCatBsEntity;
import com.crh.logon.persistence.entities.refdata.TruckTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.refdata.UomTxPK;
import com.crh.logon.persistence.entities.refdata.WagonTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.WagonTypeTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpCompanycodeEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapBpSareaEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialAltUomEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.CommExecCarrPfActiveEntity;
import com.crh.logon.persistence.entities.views.IncotermDelivTypeEntity;
import com.crh.logon.persistence.entities.views.OrderMatPointsEntity;
import com.crh.logon.persistence.entities.views.PoolCommCarrierEntity;
import com.crh.logon.persistence.entities.views.SapFinancialDataEntity;
import com.crh.logon.persistence.entities.views.SapMatSareaContractEntity;
import com.crh.logon.persistence.entities.views.SapMaterialDescEntity;
import com.crh.logon.persistence.entities.views.SapMaterialPlantSareaEntity;
import com.crh.logon.persistence.entities.views.SapMaterialSareaInfoEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * Service which is supposed to retrieved data for different delivery setting related stuff.
 * 
 * @author AdNovum Informatik AG
 */
@Stateless
@AdnTrace
@Interceptors(UserInfoInterceptor.class)
public class DeliverySettingServiceBean extends DeliverySettingServiceDispatcher implements DeliverySettingServiceLocalInterface {

	private static final boolean IS_ASCENDING = true;
	private static final boolean IS_ACTIVE = true;
	
	private static final int INCLUDE_ALL = 0;
	private static final int INCLUDE_SERVICES_ONLY = 1;
	
	private static final boolean FILTER_OUT_FUTURE_CONTRACTS_ON = true;
	private static final boolean FILTER_OUT_FUTURE_CONTRACTS_OFF = false;
	private static final boolean ALLOW_ONLY_GROUPLEADER_FOR_QUANTITY_CONTROLLED_CONTRACTS_ON = true;
	private static final boolean ALLOW_ONLY_GROUPLEADER_FOR_QUANTITY_CONTROLLED_CONTRACTS_OFF = false;
	private static final boolean REMOVE_CONTRACT_ITEMS_WITH_ZERO_QUANTITY_ON = true;
	private static final boolean REMOVE_CONTRACT_ITEMS_WITH_ZERO_QUANTITY_OFF = false;
	private static final boolean KEEP_BASIC_COMBINATION_IF_RESTRICTION_EXISTS_ON = true;
	private static final boolean KEEP_BASIC_COMBINATION_IF_RESTRICTION_EXISTS_OFF = false;
	private static final boolean KEEP_INVALID_RESTRICTIONS_ON = true;
	private static final boolean KEEP_INVALID_RESTRICTIONS_OFF = false;
	private static final String TRANSLATION_MISSING = "(translation missing)";
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DeliverySettingServiceBean.class);

	@EJB
	UnitOfMeasurementStoreInterface unitOfMeasurementStore;
	@EJB ContractTypeStoreInterface sapContractTypeStore;
	@EJB
	AlternativeUnitOfMeasurementInterface alternativeUnitOfMeasurementStore;
	@EJB ShiptoSiloStoreInterface siloStore;
	@EJB PoolStoreInterface poolStore;
	@EJB BusinessPartnerInterface businessPartnerStore;
	@EJB SapMaterialStoreInterface sapMaterialStore;
	@EJB ContractItemStoreInterface sapContractItemStore;
	@EJB IncotermDeliveryTypeViewStoreInterface incotermDeliveryTypeStore;
	@EJB
	QuantityRestrictionServiceLocalInterface quantityRestrictionService;
	@EJB private TimeZoneServiceLocalInterface timeZoneService; 
    @EJB private BestellungServiceLocalInterface bestellungService;
    @EJB private CarrierServiceLocalInterface carrierService;
    @EJB private RefDataGenericsServiceLocalInterface referentialDataService;
    @EJB private PoolAdminServiceLocalInterface poolAdminService;
	@EJB private OrderDetailServiceLocalInterface orderDetailService;
	@EJB private DispokarteServiceLocalInterface dispokarteService;

	@EJB
	ShiptoSareaStoreInterface shiptoSareaStore;

	@EJB
	private ContractStoreInterface contractStore;

	/**
	 * Returns all the {@link SapMaterialAltUomEntity} entries symbolizing alternative uoms for the materials provided.
	 * 
	 * Depending on the caller context for the uoms filters might be applied. For example from web order taking
	 * uoms which are defined not to be available on web orders should not be returned.
	 * @param context
	 * @param sapMaterials
	 * 
	 * @return
	 */
	@Deprecated
	public List<SapMaterialAltUomEntity> fetchAltUomsForMaterials(CallerContext context, List<SapMaterialEntity> sapMaterials) {

		final ReadWriteDao<SapMaterialAltUomEntity> sapMaterialAltUomEntityDao = getDaoProvider().getSapMaterialAltUomEntityDao();
		final Filter<SapMaterialAltUomEntity> filter = sapMaterialAltUomEntityDao.createFilter();
		filter.addExpression(Restrictions.in(SapMaterialAltUomEntity.ATTR_SAP_MATERIAL, sapMaterials));
		if (context == CallerContext.WEB_ORDER) {
			filter.addExpression(Restrictions.eq(SapMaterialAltUomEntity.ATTR_UOM + "." + UomBsEntity.ATTR_AVAILABLE_IN_WEB_ORDER_FLAG, Boolean.TRUE));
		}
		filter.addExpression(Restrictions.eq(SapMaterialAltUomEntity.ATTR_UOM + "." + UomBsEntity.ATTR_CTL_ACT_CD, Boolean.TRUE));

		return sapMaterialAltUomEntityDao.find(filter);
	}

	/**
	 * Retrieves all active shippingpoints for the specified plant Cds.
	 * 
	 * @param plants to filter the result
	 * @return List <ShippingpointEntity>
	 */
	@AdnTrace
	public List <ShippingpointEntity> fetchShippingpointList(List<String> plantCds) {
		if (plantCds == null || plantCds.isEmpty()) {
			return Collections.<ShippingpointEntity> emptyList();
		}

		Filter<ShippingpointEntity> filter = new FilterImpl<ShippingpointEntity>();
		filter.addExpression(Restrictions.in(ShippingpointEntity.ATTR_PLANT_CD, plantCds));
		filter.addExpression(Restrictions.eq(ShippingpointEntity.ATTR_PLANT + "." + PlantBsEntity.ATTR_CTL_ACT_CD, Boolean.TRUE));
		filter.addExpression(Order.asc(ShippingpointEntity.ATTR_SHIPPINGPOINT_NAME));
		return getDaoProvider().getShippingpointEntityDao().find(filter);
	}

	/**
	 * Retrieves all active shippingpoints for the specified plant code, filtered on delivery type.
	 * 
	 * @param plantCd to filter the result
	 * @param deliveryTypeCd to filter the result
	 * @return  List<ShippingpointEntity>
	 */
	@AdnTrace
	public List<ShippingpointEntity> fetchShippingpointListWithDeliveryType(String plantCd, Long deliveryTypeCd) {
		List<ShippingpointEntity> result = new ArrayList<ShippingpointEntity>();
		List<String> plantList = new ArrayList<String>();
		plantList.add(plantCd);
		List<ShippingpointEntity> shippingpointList = fetchShippingpointList(plantList);
		for (ShippingpointEntity sp : shippingpointList) {
			if (DeliverySettingFilterLogic.matchShippingpointDeliveryType(sp.getShipppointType().getShipppointTypeCd(), deliveryTypeCd)) {
				result.add(sp);
			}
		}
		return result;
	}


	/**
	 * Returns a list of {@link SapMaterialPlantSareaEntity} objects. The entry exists in case a material is available in a certain plant,
	 * and the plant belongs to the parameter sales area.
	 * 
	 * @param sapMaterialIds
	 * @return List<SapMaterialPlantSareaEntity>
	 */
	@AdnTrace
	public List<SapMaterialPlantSareaEntity> fetchSapMaterialPlantList(List<Long> sapMaterialIds, Long salesAreaId) {

		if (sapMaterialIds == null || sapMaterialIds.isEmpty()) {
			return Collections.<SapMaterialPlantSareaEntity> emptyList();
		}
		Authorizer.disableNextAccessRestriction();
		ReadOnlyDao<SapMaterialPlantSareaEntity> materialDao = getDaoProvider().getSapMaterialPlantSareaEntityDao();
		Filter<SapMaterialPlantSareaEntity> filter = materialDao.createFilter();
		filter.addExpression(DbUtils.createSplittedInRestriction(SapMaterialPlantSareaEntity.ATTR_SAP_MATERIAL_ID, sapMaterialIds));
		filter.addExpression(Restrictions.eq(SapMaterialPlantSareaEntity.ATTR_SAREA_ID, salesAreaId));
		List<SapMaterialPlantSareaEntity> sapMaterialPlantList = materialDao.find(filter);

		return sapMaterialPlantList;
	}

	/**
	 * Returns a list of {@link OrderMatPointsEntity} objects. An entry mainly reflects that the material
	 * is available at a specific plant considering also material restrictions on loadingpoints of the plant.
	 * 
	 * @param Set<String> plantCds
	 * @param List<Long> sapMaterialIds
	 * @return List <OrderMatPointsEntity>
	 */
	public List <OrderMatPointsEntity> getShippingPointLoadingPoinListBySapMaterialId(Set<String> plantCds, List<Long> sapMaterialIds) {
		if ( plantCds.isEmpty() || sapMaterialIds.isEmpty()) {
			return Collections.<OrderMatPointsEntity> emptyList();
		}
		Authorizer.disableNextAccessRestriction();
		ReadOnlyDao<OrderMatPointsEntity> matpointDao = getDaoProvider().getOrderMatPointsEntityDao();
		Filter<OrderMatPointsEntity> filter = new FilterImpl<OrderMatPointsEntity>();
		Expression exp = DbUtils.createSplittedInRestriction(OrderMatPointsEntity.ATTR_SAP_MATERIAL_ID, sapMaterialIds);
		filter.addExpression(
				Restrictions.or(
						exp,
						Restrictions.isNull(OrderMatPointsEntity.ATTR_SAP_MATERIAL_ID)
						));
		filter.addExpression(Restrictions.in(OrderMatPointsEntity.ATTR_PLANT_CD, plantCds));
		return matpointDao.find(filter);
	}

	@AdnTrace
	/**
	 * Retrieves the {@link SapBpSareaEntity} for the given parameters. 
	 * @param sapBpId
	 * @param salesAreaId
	 * @return SapBpSareaEntity
	 */
	public SapBpSareaEntity fetchSapBpSalesArea(Long sapBpId, Long salesAreaId) {
		ReadOnlyDao<SapBpSareaEntity> sapBpSalesAreaDao = getDaoProvider().getSapBpSareaEntityDao();
		Filter<SapBpSareaEntity> f = sapBpSalesAreaDao.createFilter();
		f.addExpression(Restrictions.eq(SapBpSareaEntity.ATTR_SAP_BP_ID, sapBpId));
		f.addExpression(Restrictions.eq(SapBpSareaEntity.ATTR_SAREA_ID, salesAreaId));
		return sapBpSalesAreaDao.findSingle(f);
	}

	/**
	 * Returns the list of {@link SapBpCompanycodeEntity} for the parameter business partner id.
	 * @param Long sapBpId
	 * @return List <SapBpCompanycodeEntity>
	 */
	@AdnTrace
	public List <SapBpCompanycodeEntity> fetchSapBpCompCodes(Long sapBpId) {
		ReadWriteDao<SapBpCompanycodeEntity> sapBpCompanyCodeDao = getDaoProvider().getSapBpCompanycodeEntityDao();
		Filter<SapBpCompanycodeEntity> f = sapBpCompanyCodeDao.createFilter();
		f.addExpression( Restrictions.eq(SapBpCompanycodeEntity.ATTR_SAP_BP_ID, sapBpId) );
		f.addExpression( Restrictions.eq(SapBpCompanycodeEntity.ATTR_MARKED_FOR_DELETION,
				Boolean.FALSE) );

		return sapBpCompanyCodeDao.find(f);
	}


	public List <DeliveryTypeTxEntity> findDeliveryTypeTxEntityFiltered(Set<Long> ids ,Locale locale){
		if ( ids.isEmpty() ) {
			return Collections.<DeliveryTypeTxEntity> emptyList();
		}
		Filter<DeliveryTypeTxEntity> filt = new FilterImpl<DeliveryTypeTxEntity>();
		filt.addExpression(Restrictions.in("deliveryTypeTxPK.deliveryTypeBs.deliveryTypeCd", ids));
		filt.addExpression(Restrictions.eq("deliveryTypeTxPK.langCd", locale.getLanguage()));
		return referentialDataService.findTxEntityFiltered(DeliveryTypeTxEntity.class, filt);
	}

	public List <MaterialPackageTxEntity> findMaterialPackageTxEntityFiltered(Set<String> materialPackageCdList ,Locale locale) {
		if ( materialPackageCdList.isEmpty() ) {
			return Collections.<MaterialPackageTxEntity> emptyList();
		}
		Filter<MaterialPackageTxEntity> filt = new FilterImpl<MaterialPackageTxEntity>();
		filt.addExpression(Restrictions.in("materialPackageTxPK.materialPackageBs.materialPackageCd", materialPackageCdList));
		filt.addExpression(Restrictions.eq("materialPackageTxPK.langCd", locale.getLanguage()));
		return referentialDataService.findTxEntityFiltered(MaterialPackageTxEntity.class, filt);
	}

	/**
	 * Fetch pool txt for a list of pool ids.
	 * @return List <PoolTxEntity>
	 */
	@AdnTrace
	public List <PoolTxEntity> fetchPoolTxsForPoolIds(Set<Long> ids, Locale locale) {
		if (ids.isEmpty()) {
			return Collections.<PoolTxEntity> emptyList();
		}

		Filter<PoolTxEntity> filt = new FilterImpl<PoolTxEntity>();
		ReadOnlyDao<PoolTxEntity> poolTxDao = getDaoProvider().getPoolTxEntityDao();
		filt.addExpression(Restrictions.eq("poolTxPK.langCd", locale.getLanguage()));

		filt.addExpression(Restrictions.in("poolTxPK.poolId", ids));
		filt.addExpression(Order.asc(PoolTxEntity.ATTR_NAME));
		// next dao call will retrieve unrestricted data
		Authorizer.disableNextAccessRestriction();
		return poolTxDao.find(filt);
	}

	@AdnTrace
	public List<DeliveryTypeTxEntity> findDeliveryTypeTxEntityList(Locale locale, String orderBy, boolean active ){
		return referentialDataService.findTxEntityList(DeliveryTypeTxEntity.class, true, locale, orderBy , active);
	}

	@AdnTrace
	public SapContractItemEntity getSapContractItemById (Long sapContractItemId ){
		return sapContractItemStore.findById(sapContractItemId);
	}

	@AdnTrace
	public SapMaterialEntity getSapMaterialById ( Long sapMaterialId ){
		return sapMaterialStore.findById(sapMaterialId);
	}

	/**
	 * Fetch Shipto by BpId
	 * @param bpId
	 * @return ShiptoEntity
	 */
	public ShiptoEntity fetchShiptoByBpIdDisableAccessRestriction(String sapId) {
		/**
		 * This method is called from a context where the shipto sales area was already authorized for a user.
		 */
		Authorizer.disableNextAccessRestriction();
		return dispokarteService.fetchShiptoBySapId(sapId);
	}

	/**
	 * Fetch Shipto by BpId
	 * @param bpId
	 * @return ShiptoEntity
	 */
	@AdnTrace
	public ShiptoSareaEntity fetchShiptoSareaDisableAccessRestriction(ShiptoEntity shipto, Long salesAreaId) {
		/**
		 * This method is called from a context where the shipto sales area was already authorized for a user.
		 */
		Authorizer.disableNextAccessRestriction();
		return shiptoSareaStore.getShiptoSarea(shipto.getShiptoId(), salesAreaId);
	}

	/**
	 * Retrieves the defaults defined on Dispokarte according to the settings of the caller context.
	 * If contract id is specified, only the defaults are returned for which the contract is the same as specified,
	 * or null. Null is allowed because in case there is no sap contract, the item is allowed to be added to any order.
	 * 
	 * @param shiptoSareaId
	 * @param sapContractIds
	 * @param sapMaterialIds
	 * @param materialSelection
	 * @return
	 */
	@AdnTrace
	private List <ShiptoSareaRestrEntity> fetchShiptoRestrList(Long shiptoSareaId, Set<Long> sapContractIds, Set<Long> sapMaterialIds, int materialSelection) {

		//problem: there is a join through the nullable sap_contract_item_id -> result set is not correct if not using JPA query see LOGON-4858
		Authorizer.disableNextAccessRestriction();
		String queryString = "select x from ShiptoSareaRestrEntity x " +
				"left join x.sapContractItem sai " +
				"where x.shiptoSarea.shiptoSareaId = :shiptoSarea_shiptoSareaId " +
				"and x.enabledFlag = :enabledFlag " +
				"and x.sapMaterial.markedForDeletion <> :markedForDeletion ";
		
		if (materialSelection == INCLUDE_SERVICES_ONLY){
			queryString += " and x.sapMaterial.materialPackage.logonPackageType.packageTypeCd =:servicePackageTypeCd ";
		}

		if ( sapContractIds != null && !sapContractIds.isEmpty() )  {
			String inParameter = "";
			for(int j = 0;  j < sapContractIds.size();j++){
				inParameter += ":sapContractItem_sapContract_sapContractId_"+ j; 
				if(j < sapContractIds.size()-1){
					inParameter += ", ";
				}
			}
			queryString += "and (sai.sapContract.sapContractId in (" + inParameter + ") or x.sapContractItem is null) ";
		}
		if ( sapMaterialIds != null && !sapMaterialIds.isEmpty() )  {
			String inParameter = "";
			for(int j = 0;  j < sapMaterialIds.size();j++){
				inParameter += ":sapMaterial_sapMaterialId_"+ j; 
				if(j < sapMaterialIds.size()-1){
					inParameter += ", ";
				}
			}
			queryString += "and (x.sapMaterial.sapMaterialId in (" + inParameter + "))";
		}

		
		Query jpaQuery = em.createQuery(queryString);
		jpaQuery.setParameter("shiptoSarea_shiptoSareaId", shiptoSareaId);
		jpaQuery.setParameter("enabledFlag", Boolean.TRUE);
		jpaQuery.setParameter("markedForDeletion", Boolean.TRUE);
		
		if (materialSelection == INCLUDE_SERVICES_ONLY){			
			jpaQuery.setParameter("servicePackageTypeCd", Constant.PACKAGE_TYPE_SERVICE);
		}

		if ( sapContractIds != null && !sapContractIds.isEmpty() )  {
			int j = 0;
			for(Long sapContractId : sapContractIds){
				String inParameter = "sapContractItem_sapContract_sapContractId_"+ j; 
				jpaQuery.setParameter(inParameter, sapContractId);
				j++;
			}
		}
		if ( sapMaterialIds != null && !sapMaterialIds.isEmpty() )  {
			int j = 0;
			for (Long sapMaterialId : sapMaterialIds){
				String inParameter = "sapMaterial_sapMaterialId_"+ j; 
				jpaQuery.setParameter(inParameter, sapMaterialId);
				j++;
			}
		}
		return jpaQuery.getResultList();
	}
	
	@AdnTrace
	private List <ShiptoSareaRestrEntity> fetchShiptoRestrListForWebOrder(Long shiptoSareaId, Set<Long> sapContractIds) {
		//problem: there is a join through the nullable sap_contract_item_id -> result set is not correct if not using JPA query see LOGON-4858
		Authorizer.disableNextAccessRestriction();
		String queryString = "select x from ShiptoSareaRestrEntity x " +
				"left join x.sapContractItem sai " +
				"where x.shiptoSarea.shiptoSareaId = :shiptoSarea_shiptoSareaId " +
				"and x.enabledFlag = :enabledFlag " +
				"and x.sapMaterial.markedForDeletion <> :markedForDeletion ";
		queryString += "and x.availableInCustWebFlag = :availableInCustWebFlag ";

		if ( sapContractIds != null && !sapContractIds.isEmpty() )  {
			String inParameter = "";
			for(int j = 0;  j < sapContractIds.size();j++){
				inParameter += ":sapContractItem_sapContract_sapContractId_"+ j; 
				if(j < sapContractIds.size()-1){
					inParameter += ", ";
				}
			}
			queryString += "and (sai.sapContract.sapContractId in (" + inParameter + ") or x.sapContractItem is null) ";
		}

		Query jpaQuery = em.createQuery(queryString);
		jpaQuery.setParameter("shiptoSarea_shiptoSareaId", shiptoSareaId);
		jpaQuery.setParameter("enabledFlag", Boolean.TRUE);
		jpaQuery.setParameter("markedForDeletion", Boolean.TRUE);

		jpaQuery.setParameter("availableInCustWebFlag", Boolean.TRUE);
		
		if ( sapContractIds != null && !sapContractIds.isEmpty() )  {
			int j = 0;
			for(Long sapContractId : sapContractIds){
				String inParameter = "sapContractItem_sapContract_sapContractId_"+ j; 
				jpaQuery.setParameter(inParameter, sapContractId);
				j++;
			}
		}
		return jpaQuery.getResultList();
	}
	
	@AdnTrace
	private List <ShiptoSareaRestrEntity> fetchShiptoRestrListForPickup(Long shiptoSareaId, Set<Long> sapContractIds) {
		//problem: there is a join through the nullable sap_contract_item_id -> result set is not correct if not using JPA query see LOGON-4858
		Authorizer.disableNextAccessRestriction();
		String queryString = "select x from ShiptoSareaRestrEntity x " +
				"left join x.sapContractItem sai " +
				"where x.shiptoSarea.shiptoSareaId = :shiptoSarea_shiptoSareaId " +
				"and x.enabledFlag = :enabledFlag " +
				"and x.sapMaterial.markedForDeletion <> :markedForDeletion ";

		queryString += "and x.availableInPickupAppFlag = :availableInPickupAppFlag " +
				"and x.uom is not null " +
				"and x.shippingpoint is not null ";
		
		queryString += 	"and ( ( x.deliveryType.deliveryTypeCd IN (:deliveryTypesAvailableInTerminal) and " + 
					"x.sapMaterial.materialPackage.logonPackageType.packageTypeCd IN (:bagPackageTypeCds)" +
					")"+
					"or x.sapMaterial.materialPackage.logonPackageType.packageTypeCd = :servicePackageTypeCd " +
					" or x.sapMaterial.materialPackage.logonPackageType.packageTypeCd IN (:bulkPackageTypeCds)" +
					") ";
		
		

		if ( sapContractIds != null && !sapContractIds.isEmpty() )  {
			String inParameter = "";
			for(int j = 0;  j < sapContractIds.size();j++){
				inParameter += ":sapContractItem_sapContract_sapContractId_"+ j; 
				if(j < sapContractIds.size()-1){
					inParameter += ", ";
				}
			}
			queryString += "and (sai.sapContract.sapContractId in (" + inParameter + ") or x.sapContractItem is null) ";
		}

		Query jpaQuery = em.createQuery(queryString);
		jpaQuery.setParameter("shiptoSarea_shiptoSareaId", shiptoSareaId);
		jpaQuery.setParameter("enabledFlag", Boolean.TRUE);
		jpaQuery.setParameter("markedForDeletion", Boolean.TRUE);
		jpaQuery.setParameter("availableInPickupAppFlag", Boolean.TRUE);
		jpaQuery.setParameter("deliveryTypesAvailableInTerminal", Constant.deliveryTypesAvailableInTerminal);
		jpaQuery.setParameter("bagPackageTypeCds", Constant.bagPackageTypeCds);
		jpaQuery.setParameter("servicePackageTypeCd", Constant.PACKAGE_TYPE_SERVICE);
		jpaQuery.setParameter("bulkPackageTypeCds", Constant.bulkPakcageTypeCds);

		if ( sapContractIds != null && !sapContractIds.isEmpty() )  {
			int j = 0;
			for(Long sapContractId : sapContractIds){
				String inParameter = "sapContractItem_sapContract_sapContractId_"+ j; 
				jpaQuery.setParameter(inParameter, sapContractId);
				j++;
			}
		}
		return jpaQuery.getResultList();
	}

	/**
	 * Returns all active truck types
	 * @param locale
	 * @return
	 */
	public List<TruckTypeTxEntity> fetchActiveTruckTypes(Locale locale) {
		String jpqlQuery = "SELECT DISTINCT t FROM TruckTypeTxEntity t JOIN FETCH t.truckTypeTxPK.truckTypeBs ty WHERE ty.ctlActCd = TRUE AND t.truckTypeTxPK.langCd = :langCd ORDER BY t.txt";
		Query query = em.createQuery(jpqlQuery);
		query.setParameter("langCd", locale.getLanguage());
		List<TruckTypeTxEntity> truckTypeTxList = query.getResultList();
		return truckTypeTxList;
	}

	/**
	 * 
	 * @param locale
	 * @return
	 */
	public List <WagonTypeTxEntity> findWagonTypeTxEntityList(Locale locale) {
		return referentialDataService.findTxEntityList(WagonTypeTxEntity.class, true, locale, WagonTypeTxEntity.ATTR_TXT, true);
	}

	/**
	 * Simple Vehicles (Ship and Block-Train) share the same refdata table but has only one type per category (see LRQ 108.0 Managing Ship & Block-Train)
	 * @param locale
	 * @param simpleVehicleTypeCd either Constant.SIMPLE_VEHICLE_SHIP_TYPE_CD or SIMPLE_VEHICLE_BLOCK_TRAIN_TYPE_CD are supported at the moment.
	 * @return
	 */
	@AdnTrace
	private SimpleVehicleTypeTxEntity getSimpleVehicleTypeTxEntity(Locale locale, long simpleVehicleTypeCd) {
		return referentialDataService.findTxEntity(SimpleVehicleTypeTxEntity.class, locale, simpleVehicleTypeCd);
	}


	/**
	 * fetch truck types for a shipto 
	 * @param shiptoId
	 * @return list of ShiptoTruckTypeEntity
	 */
	@AdnTrace
	public List<ShiptoTruckTypeEntity> fetchTruckTypesByShiptoId(Long shiptoId) {
		Filter<ShiptoTruckTypeEntity> f = new FilterImpl<ShiptoTruckTypeEntity>();
		f.addExpression(Restrictions.eq(ShiptoTruckTypeEntity.ATTR_SHIPTO_ID, shiptoId ));
		f.addExpression(Order.asc(ShiptoTruckTypeEntity.ATTR_EVALUATION_PRIORITY));
		f.addExpression(Order.asc(ShiptoTruckTypeEntity.ATTR_TRUCK_TYPE));

		ReadWriteDao<ShiptoTruckTypeEntity> sttDao = getDaoProvider().getShiptoTruckTypeEntityDao();
		List<ShiptoTruckTypeEntity> shiptoTruckTypeEntityList = sttDao.find(f);
		return shiptoTruckTypeEntityList;
	}


	/**
	 * fetch wagon types for a shipto 
	 * @param shiptoId
	 * @return list of ShiptoWagonTypeEntity
	 */
	@AdnTrace
	public List<ShiptoWagonTypeEntity> fetchWagonTypesByShiptoId(Long shiptoId) {

		Filter<ShiptoWagonTypeEntity> f = new FilterImpl<ShiptoWagonTypeEntity>();
		f.addExpression(Restrictions.eq(ShiptoWagonTypeEntity.ATTR_SHIPTO_ID, shiptoId ));
		f.addExpression(Order.asc(ShiptoWagonTypeEntity.ATTR_EVALUATION_PRIORITY));
		f.addExpression(Order.asc(ShiptoWagonTypeEntity.ATTR_WAGON_TYPE));

		ReadWriteDao<ShiptoWagonTypeEntity> swtDao = getDaoProvider().getShiptoWagonTypeEntityDao();
		List<ShiptoWagonTypeEntity> shiptoWaognTypeEntityList = swtDao.find(f);
		return shiptoWaognTypeEntityList;
	}

	@AdnTrace
	private ShiptoSalesAreaFinancialbackgroundData initFinancialBackgroundData(ShiptoSareaEntity shiptoSarea, SapBpEntity soldTo) {
		ShiptoSalesAreaFinancialbackgroundData data = new ShiptoSalesAreaFinancialbackgroundData();

		final Long soldtoSapBpId = soldTo.getSapBpId();
		final Long shiptoSapBpId = shiptoSarea.getShipto().getShiptoSapBp().getSapBpId();
		final Long shiptoId = shiptoSarea.getShipto().getShiptoId();
		final Long sareaId = shiptoSarea.getSarea().getSareaId();

		final List<SapFinancialDataEntity> sapFinancialDataList = fetchSapFinancialDataFromView(soldtoSapBpId, shiptoSapBpId, sareaId);

		if (sapFinancialDataList.isEmpty()) {
			return null;
		}

		final SapFinancialData sapFinancialData = new SapFinancialData(sapFinancialDataList);

		data.setSoldToSapBp(soldTo);
		data.setShiptoSarea(shiptoSarea);

		data.setSoldtoCustomerDeliveryBlock(sapFinancialData.getSoldtoCustomerDeliveryBlock());
		data.setSoldtoCustomerOrderBlock(sapFinancialData.getSoldtoCustomerOrderBlock());
		data.setSoldtoCustomerPostingBlock(sapFinancialData.getSoldtoCustomerPostingBlock());

		data.setShiptoSareaMarkedForDel(sapFinancialData.getShiptoSareaMarkedForDel());

		data.setShiptoSareaSgroupActive(sapFinancialData.getShiptoSareaSgroupActive());
		data.setShiptoSareaSofficeActive(sapFinancialData.getShiptoSareaSofficeActive());

		data.setValidBilltoSapBpIds(sapFinancialData.getSoldtoBilltoSapBpIds());


		List<ShiptoBilltoEntity> billtos = orderDetailService.loadBilltosUnrestricted(shiptoId, sareaId);
		data.setShiptoBilltos(billtos);

		if (sapFinancialData.getSoldtoPayerSapBpIds().isEmpty()) {
			data.setValidPayerList(new ArrayList<SapBpEntity>());
		}
		else {
			Set<Long> payerBusinessPartnerIds = sapFinancialData.getSoldtoPayerSapBpIds();
			data.setValidPayerList(businessPartnerStore.fetchPayers(payerBusinessPartnerIds));
		}

		data.setSoldtoCompanycodeBlocked(sapFinancialData.isSoldtoCompanycodeBlocked());
		return data;
	}

	/**
	 * Fetches all rows for a (soldto, shipto, sarea) tuple from the view VLGNS_SAP_FINANCIAL_DATA.
	 * This is almost all the data needed to populate the ShiptoSalesAreaFinacialbackgroundData
	 * structure.
	 * 
	 * @param soldtoSapBpId sap_bp_id of the soldto SAP business partner
	 * @param shiptoSapBpId sap_bp_id of the shipto SAP business partner
	 * @param sareaId ID of the sarea
	 * @return A set of entities, corresponding to matching rows from the view.
	 */
	@AdnTrace
	private List<SapFinancialDataEntity> fetchSapFinancialDataFromView(
			final Long soldtoSapBpId,
			final Long shiptoSapBpId, final Long sareaId) {

		final ReadOnlyDao<SapFinancialDataEntity> dao = getDaoProvider().getSapFinancialDataEntityDao();
		final Filter<SapFinancialDataEntity> filter = dao.createFilter();

		filter.addExpression(Restrictions.eq(SapFinancialDataEntity.ATTR_SOLDTO_SAP_BP_ID, soldtoSapBpId));
		filter.addExpression(Restrictions.eq(SapFinancialDataEntity.ATTR_SHIPTO_SAP_BP_ID, shiptoSapBpId));
		filter.addExpression(Restrictions.eq(SapFinancialDataEntity.ATTR_SAREA_ID, sareaId));

		/*
		 * [LOGON-6443] Explicitly disabling next restriction to enable the bimodal case without 
		 * tedious propagation of a 'skipAuthorization' argument. Authorization is not required 
		 * at this point, since sold-to and ship-to must have been authorized beforehand (when 
		 * accessing call-center or dispocard).
		 */
		Authorizer.disableNextAccessRestriction();
		return dao.find(filter);
	}

	@AdnTrace
	private MaterialSelectionInitializer initMaterialSelectionForShipto(
			SapBpEntity soldTo,
			ShiptoSareaEntity shiptoSarea,
			Set<Long> sapMaterialIds, DeliverySettingSalesAreaMap salesAreaMap,  
			boolean removeMaterialsWithZeroQuantity, 
			boolean allowOnlyGroupLeadersForQuantityControlledContract,
			boolean keepBasicCombinationIfRestrictionExists,
			boolean keepInvalidRestrictions,
			Set<Long> validContractIds,
			List<ShiptoSareaRestrEntity> restrictions, Locale locale) {
		
		MaterialSelectionInitializer materialSelectionInitializer = new MaterialSelectionInitializer();
		materialSelectionInitializer.setShiptoSarea(shiptoSarea);
		TimeZone shiptoTZ = timeZoneService.loadForShipTo(shiptoSarea.getShipto(), locale);
		
		final Set<Long> availableSapMaterialIds = new HashSet<Long>();
		final Map<Long, ContractItemQuantity> quantitiesByContractItemIdsMap;
		
		final List<DeliverySettingDTO> materialProductsDeliverySettings = new ArrayList<DeliverySettingDTO>();
		final List<DeliverySettingDTO> materialRestrictionDeliverySettings = new ArrayList<DeliverySettingDTO>();
		final List<DeliverySettingDTO> serviceProductsDeliverySettings = new ArrayList<DeliverySettingDTO>();
		final List<DeliverySettingDTO> serviceRestrictionDeliverySettings  = new ArrayList<DeliverySettingDTO>();
		
		final Map<Long, Set<Long>> restrictedMaterials;
		
		/*
		 * Step 0: Find out what combinations are restricted.
		 */
		if (restrictions != null) {
			restrictedMaterials = getRestrictedSapMaterialAndContractCombinations(restrictions);
		}
		else {
			restrictions = new ArrayList<ShiptoSareaRestrEntity>();
			restrictedMaterials = new HashMap<Long, Set<Long>>();
		}
		
		/*
		 * Part 1: Initialize delivery settings which come from the contract, and have no restriction defined.
		 * If the caller requires so, do not add those entries, which have at least one restriction.
		 */
		final Map<Long, List<SapMatSareaContractEntity>> materialsDefinedExplicitelyInContractsFilteredByQuantities;
		
		if (!validContractIds.isEmpty()) {
			
			/*
			 * WARNING step alters the list sent in, purpose is to filter out contracts, which are
			 * quantity restricted contracts, and there is no remaining quantity present.
			 * See LRQ 303.0
			 */
			materialsDefinedExplicitelyInContractsFilteredByQuantities = getContractMaterials(validContractIds, sapMaterialIds, allowOnlyGroupLeadersForQuantityControlledContract, removeMaterialsWithZeroQuantity, materialSelectionInitializer);
			
			quantitiesByContractItemIdsMap = materialSelectionInitializer.getQuantitiesByContractItemIdsMap();
			/*
			 * WARNING: alters the input map in case the shipto sales area rules define so does not allow 
			 * contracts with certain contract hierarchy levels. See LRQ 002.
			 */
			applyContractHierarchyLevelCheck(shiptoSarea.getSarea(), materialsDefinedExplicitelyInContractsFilteredByQuantities);
			
			/*
			 * Load sap materials and contract items in one run, and build up map storing the entities.
			 */
			Set<Long> contractItemIds = new HashSet<Long>();
			Set<Long> sapMaterialsToLoad = new HashSet<Long>();

			for (Map.Entry<Long, List<SapMatSareaContractEntity>> entry : materialsDefinedExplicitelyInContractsFilteredByQuantities.entrySet()) {
				for (SapMatSareaContractEntity item : entry.getValue()) {
					contractItemIds.add(item.getSapContractItemId());
					sapMaterialsToLoad.add(item.getSapMaterialId());
				}
			}
			final Map <Long, SapContractItemEntity> sapContractItemMap = loadSapContractItems(contractItemIds);
			final Map<Long, SapMaterialEntity> sapMaterialMap = loadSapMaterials(sapMaterialsToLoad);
			
			for (Map.Entry<Long, List<SapMatSareaContractEntity>> entry: materialsDefinedExplicitelyInContractsFilteredByQuantities.entrySet()) {
				
				for (SapMatSareaContractEntity material : entry.getValue()) {
					final int resolutionType;
					final ContractItemQuantity qty;
					final SapContractItemEntity sapContractItem;
					final BigDecimal openQuantity; 
					/*
					 * If a restriction exists, then do not add, if the caller does not allow that.
					 */
					if (!keepBasicCombinationIfRestrictionExists && restrictedMaterials.containsKey(material.getSapMaterialId()) && restrictedMaterials.get(material.getSapMaterialId()).contains(material.getSapContractItemId())) {
						continue;
					}
					else {
						resolutionType = getResolutionType(material);
						qty = quantitiesByContractItemIdsMap.get(material.getSapContractItemId());
						sapContractItem = sapContractItemMap.get(material.getSapContractItemId());
						openQuantity = qty != null ? qty.getOpenQuantity() : null;
					}
					
				DeliverySettingDTO setting = new DeliverySettingDTO(
							sapContractItem,
							sapMaterialMap.get(material.getSapMaterialId()),
							resolutionType,
							shiptoSarea,
							openQuantity,
							shiptoTZ);
					
					availableSapMaterialIds.add(material.getSapMaterialId());
					
					if (LogonPackageHandlingUtil.isService(material.getLogonPackageTypeCd())) {
						serviceProductsDeliverySettings.add(setting);
					}
					else {
						materialProductsDeliverySettings.add(setting);
					}
				}
			}
		}
		else {
			materialsDefinedExplicitelyInContractsFilteredByQuantities = new HashMap<Long, List<SapMatSareaContractEntity>>();
			quantitiesByContractItemIdsMap = new HashMap<Long, ContractItemQuantity>();
		}

		/*
		 * Part 2: Initialize delivery settings which come from restrictions.
		 */
		for (ShiptoSareaRestrEntity restriction : restrictions) {
			
			final DeliverySettingDTO setting;
			
			if (restriction.getSapContractItem() != null) {
				/*
				 * Check if it is still available, if not, then depending on the caller either include or
				 * exclude.
				 */
				SapMatSareaContractEntity sapContractItem = locateSapContractItemInMap(
						restriction,
						materialsDefinedExplicitelyInContractsFilteredByQuantities);
				
				if (sapContractItem != null) {
					final int resolutionType = getResolutionType(sapContractItem);
					
					setting = createRestrictionDeliverySetting(restriction,
							resolutionType,
							quantitiesByContractItemIdsMap.get(sapContractItem.getSapContractItemId()), shiptoTZ);
					
				}
				else if (keepInvalidRestrictions) {
						setting = createRestrictionDeliverySetting(restriction,
								DeliverySettingDTO.RESOLUTION_MISSING,
								null  /* DISPOKARTE is never restricted */, shiptoTZ);
				}
				else {
					continue;
				}
				if ( LogonPackageHandlingUtil.isService(restriction.getSapMaterial())) {
					serviceRestrictionDeliverySettings.add(setting);
				}
				else {
					materialRestrictionDeliverySettings.add(setting);						
				}
			}
			else {
				setting = new DeliverySettingDTO(restriction, shiptoTZ);
				serviceRestrictionDeliverySettings.add(setting);
			}
			availableSapMaterialIds.add(restriction.getSapMaterial().getSapMaterialId());
		}
		
		/*
		 * Part 3: Add service materials, which are not coming from the contract.
		 * LRQ 017.3 and requirements for considering services as available for a shipto
		 */
		final SareaEntity salesArea = materialSelectionInitializer.getShiptoSarea().getSarea();
		final String sorgCd = salesArea.getSorg().getSorgCd();
		final String distributionChannel = salesArea.getDistributionChannel();
		final Set<Long> servicematerialsNotInContract = getServicesNotInContract(sorgCd, distributionChannel, sapMaterialIds);
		
		if (!servicematerialsNotInContract.isEmpty()) {			
			final List<SapMaterialEntity> sapMaterials = sapMaterialStore.findSapMaterialsById(servicematerialsNotInContract);
			
			Map<Long,SapMaterialEntity> sapMaterialMap = new HashMap<Long, SapMaterialEntity>();
			for (SapMaterialEntity sapMaterial : sapMaterials) {
				sapMaterialMap.put(sapMaterial.getSapMaterialId(), sapMaterial);
			}
			
			for (Long serviceMaterialNotInContract : servicematerialsNotInContract) {
				if (!keepBasicCombinationIfRestrictionExists && restrictedMaterials.containsKey(serviceMaterialNotInContract)) {
					continue;
				}
				
				DeliverySettingDTO setting = null;
				SapMaterialEntity sapMaterial = sapMaterialMap.get(serviceMaterialNotInContract);
				setting = new DeliverySettingDTO(sapMaterial, shiptoSarea, shiptoTZ);
				serviceProductsDeliverySettings.add(setting);
				availableSapMaterialIds.add(serviceMaterialNotInContract);
			}
		}
		
		salesAreaMap.setServiceDeliverySettings(new DeliverySettingProductMap(salesAreaMap, serviceProductsDeliverySettings));
		salesAreaMap.setServiceRestrictionDeliverySettings(new DeliverySettingProductMap(salesAreaMap, serviceRestrictionDeliverySettings));		
		salesAreaMap.setMaterialDeliverySettings(new DeliverySettingProductMap(salesAreaMap, materialProductsDeliverySettings));
		salesAreaMap.setMaterialRestrictionDeliverySettings(new DeliverySettingProductMap(salesAreaMap, materialRestrictionDeliverySettings));
		
		materialSelectionInitializer.setSapMaterialIds(availableSapMaterialIds);
		return materialSelectionInitializer;
	}

	private Map<Long, SapMaterialEntity> loadSapMaterials(
			Set<Long> sapMaterialsToLoad) {
		final List<SapMaterialEntity> sapMaterials;
		if (!sapMaterialsToLoad.isEmpty()) {
			sapMaterials = sapMaterialStore.findSapMaterialsById(sapMaterialsToLoad);
		}
		else {
			return new HashMap<Long, SapMaterialEntity>();
		}
		
		
		Map<Long,SapMaterialEntity>sapMaterialMap = new HashMap<Long, SapMaterialEntity>();
		for (SapMaterialEntity sapMaterial : sapMaterials) {
			sapMaterialMap.put(sapMaterial.getSapMaterialId(), sapMaterial);
		}
		return sapMaterialMap;
	}

	private Map<Long, SapContractItemEntity> loadSapContractItems(
			Set<Long> contractItemIds) {
		final List<SapContractItemEntity> sapContractItems;
		Authorizer.disableNextAccessRestriction();
		if (!contractItemIds.isEmpty()) {
			sapContractItems =  sapContractItemStore.findSapContractItemsById(contractItemIds);
		}
		else {				
			return new HashMap<Long, SapContractItemEntity>();
		}
		Map<Long,SapContractItemEntity> sapContractItemMap = new HashMap<Long, SapContractItemEntity>();
		for (SapContractItemEntity contractItem : sapContractItems) {
			sapContractItemMap.put(contractItem.getSapContractItemId(), contractItem);
		}
		return sapContractItemMap;
	}

//	private List<ShiptoSareaRestrEntity> getRestrictions(CallerContext context,
//			ShiptoSareaEntity shiptoSarea, Set<Long> sapMaterialIds,
//			final Set<Long> validContractIds) {
//		List<ShiptoSareaRestrEntity> restrictions;
//		Long shiptoSareaId = shiptoSarea.getShiptoSareaId();
//		if (context == CallerContext.DISPOKARTE) {
//			restrictions = shiptoSarea.getRestrictions();
//		}
//		else if (context == CallerContext.PICKUP_TERMINAL) {
//			restrictions = fetchShiptoRestrListForPickup(shiptoSareaId, validContractIds, sapMaterialIds);
//		}
//		else if (context == CallerContext.WEB_ORDER) {
//			restrictions = fetchShiptoRestrListForWebOrder(shiptoSareaId, validContractIds, sapMaterialIds);
//		}
//		else {
//			restrictions = fetchShiptoRestrList(shiptoSareaId, validContractIds, sapMaterialIds, INCLUDE_ALL);
//		}
//		
//		return restrictions;
//	}

	private int getResolutionType(SapMatSareaContractEntity material) {
		final int resolutionType;
		if (material.getMaterialGroup4() != null) {
			if (material.getMaterialGroup4().equals(Constant.GENERIC_MATERIAL_GROUPING_CRITERIA_PRICING_GROUP)) {
				resolutionType = DeliverySettingDTO.RESOLUTION_FROM_PRICING_GROUP_TYPE_GENERIC_MATERIAL;
			}
			else if (material.getMaterialGroup4().equals(Constant.GENERIC_MATERIAL_GROUPING_CRITERIA_DIVISION)){
				resolutionType = DeliverySettingDTO.RESOLUTION_FROM_DIVISION_TYPE_GENERIC_MATERIAL;
			}
			else {
				resolutionType = DeliverySettingDTO.RESOLUTION_MISSING;
				LOG.error("Unknown resolution type.");
			}
		}
		else {
			resolutionType = DeliverySettingDTO.RESOLUTION_EXPLICIT_IN_CONTRACT;					
		}
		return resolutionType;
	}
	
	/**
	 * Returns a map containing information what sap materials are available for
	 * the specified contract ids, and also basic information coming from the contract and contract
	 * items. Respects quantity restrictions for the contract items, it does not return a certain
	 * entry representing material in a contract in case there is no remaining quantity left
	 * for the contract item.
	 */
	@AdnTrace
	public Map<Long, List<SapMatSareaContractEntity>> getContractMaterials(	
			Set<Long> validContractIds, Set<Long> sapMaterialIds,
			boolean allowOnlyGroupLeadersForQuantityControlledContract, boolean removeMaterialsWithZeroQuantity, MaterialSelectionInitializer materialSelectionInitializer){
		
		/**
		 * This map contains all material contractitem list mappings coming from the DB. IT IS NOT YET RESOLVED!!
		 */
		final Map<Long, List<SapMatSareaContractEntity>> materialsDefinedExplicitelyInContracts = new HashMap<Long, List<SapMatSareaContractEntity>>();

		Authorizer.disableNextAccessRestriction();
		ReadOnlyDao<SapMatSareaContractEntity> sapMaterialSareaContractEntityDao = getDaoProvider().getSapMatSareaContractEntityDao();
		final Filter<SapMatSareaContractEntity> filter = sapMaterialSareaContractEntityDao.createFilter();

		filter.addExpression(Restrictions.in(SapMatSareaContractEntity.ATTR_SAP_CONTRACT_ID, validContractIds));
		if (sapMaterialIds != null && !sapMaterialIds.isEmpty()) {
			filter.addExpression(Restrictions.in(SapMatSareaContractEntity.ATTR_SAP_MATERIAL_ID, sapMaterialIds));
		}

		List<SapMatSareaContractEntity> materials = sapMaterialSareaContractEntityDao.find(filter);

		Map<Long, Long> quantityControlledContractItems = new HashMap<Long, Long>();

		for ( SapMatSareaContractEntity contractMaterial : materials) {
			boolean quantityControlledContract = LogonUtil.isQuantityControlledContract(contractMaterial.getItemGroupingType());

			// we only include the contract item if it represents the group leader unless we are
			// dealing with an LDS call - external systems are not aware of our group leader
			// concept so we replace the group leader with the first group member we encounter
			if (quantityControlledContract) {
				if (!allowOnlyGroupLeadersForQuantityControlledContract) {

					if (LogonUtil.isPlanningMaterial(contractMaterial.getSapMaterialNo())) {
						// Planning materials should not be sent to LDS
						continue;
					}

					if (contractMaterial.getIsApGroupLeader()) {
						// the current sapContractItemEntity IS the group leader - skip it
						continue;
					}

					if (quantityControlledContractItems.containsKey(contractMaterial.getApGroupLeaderId())) {
						// we've already seen a member with this group leader - we only substitute the
						// first member for the group leader
						continue;
					}

					quantityControlledContractItems.put(contractMaterial.getSapContractItemId(), contractMaterial.getApGroupLeaderId());

				}
				else {
					// in the normal case, we only allow group leaders to pass through.
					if (!contractMaterial.getIsApGroupLeader()) {
						continue;
					}
				}
			}

			if (!materialsDefinedExplicitelyInContracts.containsKey(contractMaterial.getSapMaterialId())) {
				materialsDefinedExplicitelyInContracts.put(contractMaterial.getSapMaterialId(), new ArrayList<SapMatSareaContractEntity>());
			}
			materialsDefinedExplicitelyInContracts.get(contractMaterial.getSapMaterialId()).add(contractMaterial);

			if (quantityControlledContract && contractMaterial.getIsApGroupLeader()) {
				quantityControlledContractItems.put(
						contractMaterial.getSapContractItemId(),
						contractMaterial.getSapContractItemId());
			}
		}


		//--- For Quantity Controlled items we remove materials with 0 quantity ---//
		//
		// in the case of Dispokarte and LDS we never remove anything, otherwise it is not possible to configure
		// restrictions on materials which no longer have an open quantity. for the rest of the callers,
		// we prune the list based on the results AND we save the mapping of material id to QuantityBean
		// for later use when we generate the DeliverySettingDTOs.
		if (!quantityControlledContractItems.isEmpty() && removeMaterialsWithZeroQuantity) {
			Map<Long, ContractItemQuantity> quantitiesByContractItemIdsMap =
					quantityRestrictionService.computeQuantitiesForQuantityControlledContractItemIds(new ArrayList<Long>(quantityControlledContractItems.values()), null);

			SapMaterialUtil.pruneQuantityRestrictedMaterials(materialsDefinedExplicitelyInContracts, quantitiesByContractItemIdsMap);

			if (materialSelectionInitializer != null) {				
				materialSelectionInitializer.setQuantitiesByContractItemIdsMap(quantitiesByContractItemIdsMap);
			}
		}
		return materialsDefinedExplicitelyInContracts;
	}

	/**
	 * Performs a simple linear search on collection to find the first 
	 * SapContractItemEntity with the given sapContractItemId.
	 * 
	 * This method is a replacement for the former ListUtils.findListItem... methods. 
	 * Those are quite slow because those uses reflection technique. 
	 * 
	 * @param collection in which the SapContractItemEntity should be searched.
	 * @param sapContractItemId this ID is used when searching for the SapContractItemEntity.
	 * @return the SapMatSareaContractEntity if found, null otherwise.
	 */
	private static SapMatSareaContractEntity findSapContractItemInCollection(
			final Collection<SapMatSareaContractEntity> collection, final Long sapContractItemId) {

		if (collection == null || sapContractItemId == null) {
			throw new IllegalArgumentException(
					"The collection and sapContractItemId parameters should not be null!");
		}

		SapMatSareaContractEntity result = null;

		for (SapMatSareaContractEntity sapContractItemEntity : collection) {
			if (sapContractItemEntity.getSapContractItemId().equals(sapContractItemId)) {
				result = sapContractItemEntity;
				break;
			}
		}

		return result;
	}



	/**
	 * LOGON-4071 implementation
	 * F03.2 LRQ002.0 New Contract Document - Contract Hierarchies (CR5)
	 * 
	 * Depending on the contracts hierarchy level there is a precedence to offer a material for selection.
	 * If a higher level contract exists even though the material is valid, and exists in the contract, it should not
	 * be considered valid.
	 * 
	 * The function modifies the maps sent in as input parameter according to the contract hierarchy level logic.
	 */
	private void applyContractHierarchyLevelCheck(SareaEntity salesArea, Map<Long, List<SapMatSareaContractEntity>> materialsDefinedExplicitelyInContractsFilteredByQuantities) {
		
		final boolean contractHierarchyLevel2 = salesArea.getOrderTakingClevel2Flag();
		final boolean contractHierarchyLevel3 = salesArea.getOrderTakingClevel3Flag();
		final Date now = new Date();

		// if both flags are true we don't have to do any filtering (return)
		if ((contractHierarchyLevel2 == true) && (contractHierarchyLevel3 == true)) {
			return;
		}

		// the map for the 'contract with the lowest level' mapped the the material
		final Map<Long, Long> minContractLevelMap = new HashMap<Long, Long>();

		/*
		 * 1) search for the lowest hierarchy levels for each material 
		 */
		long contractHierarchyLevelMin;
		long contractHierarchyLevel;
		Long sapMaterialId;

		for (Map.Entry<Long, List<SapMatSareaContractEntity>> materialContracts : materialsDefinedExplicitelyInContractsFilteredByQuantities.entrySet()) {
			sapMaterialId = materialContracts.getKey();

			// if there is an entry from the previous loop (map), use it
			if (minContractLevelMap.containsKey(sapMaterialId)) {
				contractHierarchyLevelMin = minContractLevelMap.get(sapMaterialId);
			}
			else {
				contractHierarchyLevelMin = Long.MAX_VALUE;
			}
			for (SapMatSareaContractEntity contractItem : materialContracts.getValue()) {
				contractHierarchyLevel = contractItem.getContractHierarchyLevel();

				// if the contract is a lower level one, and it's in the past
				// modify the minimum (and add it to the map).
				if ((contractHierarchyLevel < contractHierarchyLevelMin) && (now.after(contractItem.getValidFromDat()))) {
					contractHierarchyLevelMin = contractHierarchyLevel;
					minContractLevelMap.put(sapMaterialId, contractHierarchyLevel);						
				}
			}
		}
		

		/*
		 * 2) remove higher level contract if the conditions are met
		 */
		Iterator<Map.Entry<Long, List<SapMatSareaContractEntity>>> materialContractsIter;
		Map.Entry<Long, List<SapMatSareaContractEntity>> materialContracts;
		List<SapMatSareaContractEntity> contracts;
		Iterator<SapMatSareaContractEntity> contractsIter;
		materialContractsIter = materialsDefinedExplicitelyInContractsFilteredByQuantities.entrySet().iterator();
		while (materialContractsIter.hasNext()) {
			materialContracts = materialContractsIter.next();
			sapMaterialId = materialContracts.getKey();

			// lowest level for the material
			if (minContractLevelMap.containsKey(sapMaterialId)) {
				contractHierarchyLevelMin = minContractLevelMap.get(sapMaterialId);					
			}
			else {
				// every contractItem of the material belonged to a future contract
				// and it was skipped entirely in 1).
				continue; 
			}				

			// if the lowest level is 1 we have to filter (because either cLevel2Flag or
			// cLevel3Flag is false)
			// if the lowest level is 2 we only have to filter if the cLevel3Flag is
			// false (that means we have to filter out level3 contracts only)
			if ((contractHierarchyLevelMin == 1) || ((contractHierarchyLevelMin == 2) && (contractHierarchyLevel3 == false))) {
				contracts = materialContracts.getValue();
				contractsIter = contracts.iterator();
				while (contractsIter.hasNext()) {
					SapMatSareaContractEntity contractItem = contractsIter.next();
					contractHierarchyLevel = contractItem.getContractHierarchyLevel();

					// if the contract is higher level than the minimum,
					// and in the past filter it (if the specific flag is set)
					if ((contractHierarchyLevel > contractHierarchyLevelMin) && (now.after(contractItem.getValidFromDat()))) {

						// if the corresponding flag is false we have to filter
						if (((contractHierarchyLevel == 2) && (contractHierarchyLevel2 == false)) ||
								((contractHierarchyLevel == 3) && (contractHierarchyLevel3 == false))) {
							contractsIter.remove();
						}							
					}
				}
				// if there are no more contracts associated with the 
				// material in this map remove the entry itself.
				if (contracts.isEmpty()) {
					materialContractsIter.remove();
				}
			}				
		}
		
	}

	/**
	 * Returns a set of ids, which are available for the specified sales organization code and distribution channel.
	 * In case sapMaterialIds parameter is specified returns only those, which are available.
	 * 
	 */
	@AdnTrace
	public Set<Long> getServicesNotInContract(String sorgCd, String distributionChannel, Set<Long> sapMaterialIds) {
		
		// collect service material not in contract (service materials of sales area)
		final Set<Long> servicesNotInContract = new HashSet<Long>();
		final List<SapMaterialSareaInfoEntity> serviceMaterialsInSalesArea = fetchServiceSapMatSarea(sorgCd, distributionChannel, sapMaterialIds);

		if (serviceMaterialsInSalesArea != null && !serviceMaterialsInSalesArea.isEmpty()) {
			for (SapMaterialSareaInfoEntity sapMaterialSarea : serviceMaterialsInSalesArea) {
				servicesNotInContract.add(sapMaterialSarea.getSapMaterialId());
			}
		}
		return servicesNotInContract;
	}


	/**
	 * Load valid contracts for the shipto+soldto combination. 
	 * <p>
	 * Contracts as well as contract items loaded will depend on the both the
	 * <em>callerContext</em> as well as the contract type. For Advance Payment (AP)
	 * contracts, we need to ensure that only the AP Contract Item Group Leaders are
	 * returned in the list of contract items. Furthermore, for AP contracts we need
	 * to check, depending on the <em>callerContext</em>, whether the list of materials
	 * has been exhausted or not and only include allow those contract items through for
	 * which we still have available materials.
	 * 
	 * @return a List<SapContractEntity> with each of the contract item lists properly
	 * 		pruned.
	 */
	public Set<Long> loadValidContracts(
			Long salesAreaId,
			Long shiptoSapBpId,
			Long soldtoSapBpId,
			boolean filterOutExpired ) {

		return contractStore.fetchContractsForShiptoSareaSoldto(
				salesAreaId,
				shiptoSapBpId,
				soldtoSapBpId,
				filterOutExpired);
	}


	/**
	 * Returns a map containing materials having any kind of relevant restriction.
	 * Can contain a material with empty contractSet in case the restriction refers to a service.
	 * @param restrictions
	 * @return
	 */
	private Map<Long, Set<Long>> getRestrictedSapMaterialAndContractCombinations(
			List<ShiptoSareaRestrEntity> restrictions) {
		Map<Long, Set<Long>> materialAndContractRestrictions = new HashMap<Long, Set<Long>>();

		for(ShiptoSareaRestrEntity restriction : restrictions) {
			SapContractItemEntity sapContractItem = restriction.getSapContractItem();
			Long sapMaterialId = restriction.getSapMaterial().getSapMaterialId();
			
			final Long sapContractItemId;
			if (sapContractItem != null) {
				sapContractItemId = sapContractItem.getSapContractItemId();
			}
			else {
				//also possible for services that there is no contract
				sapContractItemId = null;
			}
			Set<Long> contractset = materialAndContractRestrictions.get(sapMaterialId);
			if(contractset == null) {
				contractset = new HashSet<Long>();
				materialAndContractRestrictions.put(sapMaterialId, contractset);
			}
			if (sapContractItemId != null) {
				contractset.add(sapContractItemId);
			}
		}
		return materialAndContractRestrictions;
	}

	/**
	 * @param shiptoSareaRestrEntity
	 * @param resolutionType
	 * @param shiptoTZ 
	 * @return
	 */
	private DeliverySettingDTO createRestrictionDeliverySetting(
			ShiptoSareaRestrEntity shiptoSareaRestrEntity,
			final int resolutionType,
			ContractItemQuantity qty, TimeZone shiptoTZ) {

		DeliverySettingDTO setting;
		setting = new DeliverySettingDTO(shiptoSareaRestrEntity,
				resolutionType,
				qty != null ? qty.getOpenQuantity() : null, shiptoTZ);
		return setting;
	}

	/**
	 * @param shiptoSareaRestrEntity
	 * @param map
	 * @return
	 */
	private SapMatSareaContractEntity locateSapContractItemInMap(ShiptoSareaRestrEntity shiptoSareaRestrEntity,
			final Map<Long, List<SapMatSareaContractEntity>> map) {
		SapMatSareaContractEntity sapContractItem = null;
		final Long sapMaterialId = shiptoSareaRestrEntity.getSapMaterial().getSapMaterialId();
		final Set<Long> sapMaterialEntitySet = map.keySet();

		final Long sapMaterial = SapMaterialUtil.findSapMaterialInCollection(
				sapMaterialEntitySet, sapMaterialId);

		if (sapMaterial != null) {
			final Long sapContractItemId = shiptoSareaRestrEntity.getSapContractItem().getSapContractItemId();
			final List<SapMatSareaContractEntity> contractsForGenericMaterial = map.get(sapMaterial);

			sapContractItem = findSapContractItemInCollection(contractsForGenericMaterial, sapContractItemId);
		}
		return sapContractItem;
	}

	@AdnTrace
	private DeliverySettingLanguageDependentResolver initResolver (Locale locale, DeliverySettingProductMap productMap) {
		DeliverySettingLanguageDependentResolver resolver = new DeliverySettingLanguageDependentResolver();	
		
		final Collection<DeliverySettingDTO> possibleDeliverySettings =  productMap.getPossibleDeliverySettings();

		final HashMap<Long, DeliveryTypeTxEntity> deliveryTypeTxMap = new HashMap<Long, DeliveryTypeTxEntity>();
		final HashMap<String, UomTxEntity> uomTxMap = new HashMap<String, UomTxEntity>();
		final HashMap<Long, PoolTxEntity> poolTxMap = new HashMap<Long, PoolTxEntity>();
		final HashMap<String, SapContractTypeTxEntity> contractTypeTxMap = new HashMap<String, SapContractTypeTxEntity>();
		final Map<String, MaterialPackageTxEntity> materialPackageTxMap = new HashMap<String, MaterialPackageTxEntity>();
		Map<Long, SapMaterialDescEntity> sapMaterialDescriptionMap = new HashMap<Long, SapMaterialDescEntity>();
		
		if (!possibleDeliverySettings.isEmpty()) {
			Set<Long> poolIds = new HashSet<Long>();
			Set<Long> deliveryTypeCds = new HashSet<Long>();
			Set<String> unitOfMeasurementCds = new HashSet<String>();
			Set<String> contractTypeCds = new HashSet<String>();
			Set<String> materialPackageCdList = new HashSet<String>();
			Set<Long> sapMaterialIds = new HashSet<Long>();
			for (DeliverySettingDTO deliverySetting : possibleDeliverySettings) {
				
				sapMaterialIds.add(deliverySetting.getSapMaterialId());
				
				if (deliverySetting.getPool() != null) {
					PoolEntity pool = deliverySetting.getPool();
					Long poolId = pool.getPoolId();
					poolIds.add(poolId);
				}
				if (deliverySetting.getDeliveryType() != null) {
					DeliveryTypeBsEntity deliveryType = deliverySetting.getDeliveryType();
					Long deliveryTypeCd = deliveryType.getDeliveryTypeCd();
					deliveryTypeCds.add(deliveryTypeCd);
				}
				if (deliverySetting.getUom() != null) {
					UomBsEntity unitOfMeasurement = deliverySetting.getUom();
					String unitOfMeasurementCd = unitOfMeasurement.getUomCd();
					unitOfMeasurementCds.add(unitOfMeasurementCd);
				}
				if (deliverySetting.getSapContractItem() != null) {
					SapContractItemEntity sapContractItem = deliverySetting.getSapContractItem();
					SapContractEntity sapContract = sapContractItem.getSapContract();
					SapContractTypeBsEntity sapContractType = sapContract.getSapContractType();
					String sapContractTypeCd = sapContractType.getSapContractTypeCd();
					contractTypeCds.add(sapContractTypeCd);
				}
				if ( deliverySetting.getMaterialPackageCd() != null ) {
					materialPackageCdList.add(deliverySetting.getMaterialPackageCd());
				}
			}

			final List <DeliveryTypeTxEntity> deliveryTypeTxFilteredList = findDeliveryTypeTxEntityFiltered(deliveryTypeCds, locale);
			List <MaterialPackageTxEntity> packTx = findMaterialPackageTxEntityFiltered(materialPackageCdList, locale);

			for (MaterialPackageTxEntity matTx : packTx) {
				MaterialPackageTxPK deliveryTypeTxPk = matTx.getMaterialPackageTxPK();
				MaterialPackageBsEntity deliveryTypeBs = deliveryTypeTxPk.getMaterialPackageBs();
				materialPackageTxMap.put(deliveryTypeBs.getMaterialPackageCd(), matTx);
			}

			for (DeliveryTypeTxEntity deliveryTypeTx : deliveryTypeTxFilteredList) {
				DeliveryTypeTxPK deliveryTypeTxPk = deliveryTypeTx.getDeliveryTypeTxPK();
				DeliveryTypeBsEntity deliveryTypeBs = deliveryTypeTxPk.getDeliveryTypeBs();
				Long deliveryTypeCd = deliveryTypeBs.getDeliveryTypeCd();
				deliveryTypeTxMap.put(deliveryTypeCd, deliveryTypeTx);
			}

			List<PoolTxEntity> poolTxList = fetchPoolTxsForPoolIds(poolIds, locale);
			for (PoolTxEntity poolTx : poolTxList) {
				PoolTxPK poolTxPk = poolTx.getPoolTxPK();
				Long poolId  = poolTxPk.getPoolId();
				poolTxMap.put(poolId, poolTx);
			}

			List<UomTxEntity> txs = unitOfMeasurementStore.findTextsByCodes(unitOfMeasurementCds, locale); 
			for (UomTxEntity uomTx : txs) {
				UomTxPK uomTxPk = uomTx.getUomTxPK();
				UomBsEntity uomBs = uomTxPk.getUomBs();
				String uomCd = uomBs.getUomCd();
				uomTxMap.put(uomCd, uomTx);
			}


			List<SapContractTypeTxEntity> contractTypeTxList = sapContractTypeStore.fetchTexts(contractTypeCds, locale);
			for (SapContractTypeTxEntity sapContractTypeTx : contractTypeTxList) {
				SapContractTypeTxPK sapContractTypeTxPk = sapContractTypeTx.getSapContractTypeTxPK();
				SapContractTypeBsEntity sapContractType = sapContractTypeTxPk.getSapContractTypeBs();
				String sapContractTypeCd = sapContractType.getSapContractTypeCd();
				contractTypeTxMap.put(sapContractTypeCd, sapContractTypeTx);
			}
			
			final List<SapMaterialDescEntity> sapMaterialDescriptionList = dispokarteService.fetchSapMaterialDescByLang(new ArrayList<Long>(sapMaterialIds), locale.getLanguage());
			
			for (SapMaterialDescEntity sapMaterialDesc : sapMaterialDescriptionList) {
				Long sapMaterialId = sapMaterialDesc.getSapMaterialId();
				sapMaterialDescriptionMap.put(sapMaterialId, sapMaterialDesc);
			}
			
		}
		
		resolver.setMaterialDescriptionMap(sapMaterialDescriptionMap);
		resolver.setDelivTypeTxMap(deliveryTypeTxMap);
		resolver.setPoolTxMap(poolTxMap);
		resolver.setMaterialPackageTxMap(materialPackageTxMap);
		resolver.setUomTxMap(uomTxMap);
		resolver.setContractTypeTxMap(contractTypeTxMap);

		return resolver;
	}


	/**
	 * Fills out all the fields of the product map, which are language dependent.
	 * @param productMap
	 */
	private void resolveFields(DeliverySettingProductMap productMap, DeliverySettingLanguageDependentResolver resolver) {

		for ( DeliverySettingDTO deliverySetting : productMap.getPossibleDeliverySettings() ){

			final DeliverySettingLangDepData wrappedSetting = deliverySetting.getAdditionalData();
			/**
			 * Set all language dependent data according to the maps retrieved.
			 */
			SapMaterialDescEntity desc = resolver.getMaterialDescriptionMap().get(deliverySetting.getSapMaterial().getSapMaterialId());

			// LOGON-5628: Graceful fallback if no description exists in language
			if (desc == null)
			{
				LOG.warn("Material description for SAP material no. {} missing in user's language, " +
						"displaying fallback.", new Object[] {deliverySetting.getSapMaterial().getSapMaterialNo()});

				wrappedSetting.setTxt("" + deliverySetting.getSapMaterial().getSapMaterialId() + "_NO_LANG_DESC");
			}
			else {
				wrappedSetting.setTxt(desc.getTxt());
			}

			if (deliverySetting.getDeliveryTypeCd() != null ) {
				DeliveryTypeTxEntity deliveryTypeTxEntity = resolver.getDelivTypeTxMap().get(deliverySetting.getDeliveryTypeCd());
				String deliveryTypeTxt =  deliveryTypeTxEntity != null ? deliveryTypeTxEntity.getTxt() : deliverySetting.getDeliveryTypeCd() + " " + TRANSLATION_MISSING;
				wrappedSetting.setDeliveryTypeTxt(deliveryTypeTxt);
			}

			if (deliverySetting.getSapContractItem() != null) {
				String typeCd = deliverySetting.getSapContractItem().getSapContract().getSapContractType().getSapContractTypeCd();
				SapContractTypeTxEntity sapContractTypeTxEntity = resolver.getContractTypeTxMap().get(typeCd);
				String typeTx = sapContractTypeTxEntity != null ? sapContractTypeTxEntity.getTxt() : typeCd + " " + TRANSLATION_MISSING;
				wrappedSetting.setContractTypeTxt(typeTx);
			}

			MaterialPackageTxEntity materialPackageTxEntity = resolver.getMaterialPackageTxMap().get(deliverySetting.getMaterialPackageCd());
			String txt = materialPackageTxEntity != null ? materialPackageTxEntity.getTxt() : deliverySetting.getMaterialPackageCd() + " " + TRANSLATION_MISSING;
			wrappedSetting.setMaterialPackageTxt(txt);

			if ( deliverySetting.getPool() != null ) {
				PoolTxEntity poolTxEntity = resolver.getPoolTxMap().get(deliverySetting.getPoolId());
				String poolName = poolTxEntity != null ? poolTxEntity.getName() : deliverySetting.getPoolId() + " " + TRANSLATION_MISSING;
				wrappedSetting.setPoolName(poolName);
			}

			if ( deliverySetting.getUom() != null ) {
				UomTxEntity uomTxEntity = resolver.getUomTxMap().get(deliverySetting.getUomCd());
				String uomTxt = uomTxEntity != null ? uomTxEntity.getTxt() : deliverySetting.getUomCd()+" "+TRANSLATION_MISSING;
				wrappedSetting.setUomTxt(uomTxt);
			}

		}

	}

	@AdnTrace
	private MaterialAvailabilityBackgroundData initMaterialAvailabilityBackgroundData(ShiptoSareaEntity shiptoSarea, Locale locale, CallerContext caller, Set<Long> sapMaterials) {

		final MaterialAvailabilityBackgroundData materialAvailabilityBackgroundData = new MaterialAvailabilityBackgroundData();
		final Map <String, List<ShippingpointDTO>> plantShippingPointMap = new HashMap<String, List<ShippingpointDTO>> ();
		final Map <Long, Set<Long>> materialLoadingpointMap = new HashMap <Long, Set<Long>>();
		final Map <Long, List<LoadingPointDTO>> shippingPointLoadingPointMap = new HashMap <Long, List<LoadingPointDTO>>();
		Long shiptoId = shiptoSarea.getShipto().getShiptoId();

		if ( !sapMaterials.isEmpty() ) {
			materialAvailabilityBackgroundData.setSapMaterialIds(new ArrayList<Long>(sapMaterials));
		}
		else {
			//no products, nothing to initialize!
			return materialAvailabilityBackgroundData;
		}

		Set<String> plantCds = initPlantsForMaterials(materialAvailabilityBackgroundData, shiptoSarea);

		//perhaps could further be filtered for material + possible plants instead of just materials
		initMaterialMixDesigns(materialAvailabilityBackgroundData);

		if ( !plantCds.isEmpty() ) {

			/**
			 * Retrieving material loadingpoint relations, and retrieving the loadingpointList accordingly.
			 */
			List <OrderMatPointsEntity> materialLoadingEntries = getShippingPointLoadingPoinListBySapMaterialId(plantCds, materialAvailabilityBackgroundData.getSapMaterialIds());

			for (OrderMatPointsEntity materialLoadingEntry : materialLoadingEntries ) {

				Long sapMaterialId = materialLoadingEntry.getSapMaterialId();
				Long loadingpointId = materialLoadingEntry.getLoadingpointId();
				String plantCd = materialLoadingEntry.getPlantCd();

				if ( !materialLoadingpointMap.containsKey(sapMaterialId) ) {
					materialLoadingpointMap.put(sapMaterialId, new HashSet<Long>());
				}
				materialLoadingpointMap.get(sapMaterialId).add(loadingpointId);

				if ( !plantShippingPointMap.containsKey(plantCd) ) {
					plantShippingPointMap.put(plantCd, new ArrayList<ShippingpointDTO>());
				}

				Long shippingpointId = materialLoadingEntry.getShippingpointId();
				ShippingpointDTO shippingpoint = new ShippingpointDTO(shippingpointId, materialLoadingEntry.getPlantCd(), materialLoadingEntry.getPlantName(), materialLoadingEntry.getShippingpointName(),
						materialLoadingEntry.getShipppointTypeCd(), materialLoadingEntry.getRailwayStationId(), materialLoadingEntry.getCoordLat(),materialLoadingEntry.getCoordLon());

				if ( !plantShippingPointMap.get(plantCd).contains(shippingpoint) ) {						
					plantShippingPointMap.get(plantCd).add(shippingpoint);
				}

				if ( !shippingPointLoadingPointMap.containsKey(shippingpointId) ) {
					shippingPointLoadingPointMap.put(shippingpointId, new ArrayList<LoadingPointDTO>());
				}
				if (materialLoadingEntry.getLoadingpointId() != null) {
					//can be null as the view includes shippingpoints without loadingpoint. Reason: certain type of shippingpoints do not have loadingpoints at all.
					LoadingPointDTO loadingpoint = new LoadingPointDTO(materialLoadingEntry.getLoadingpointId(), materialLoadingEntry.getLoadingpointName(), materialLoadingEntry.getPackageTypeCd(), materialLoadingEntry.getShippingpointId());
					if ( !shippingPointLoadingPointMap.get(shippingpointId).contains(loadingpoint) ) {						
						shippingPointLoadingPointMap.get(shippingpointId).add(loadingpoint);
					}
				}

			}
		}
		materialAvailabilityBackgroundData.setShippingPointLoadingPointMap(shippingPointLoadingPointMap);
		materialAvailabilityBackgroundData.setMaterialLoadingpointMap(materialLoadingpointMap);
		materialAvailabilityBackgroundData.setPlantShippingPointMap(plantShippingPointMap);

		materialAvailabilityBackgroundData.setSilos(siloStore.findActiveOnlyByShiptoId(shiptoId));
		initializeUnitOfMeasurementsForMaterials(materialAvailabilityBackgroundData, locale, caller);

		List<OrderSpiTxEntity> serviceProcessIndicatorList = referentialDataService.findTxEntityList(OrderSpiTxEntity.class, IS_ACTIVE, locale, OrderSpiTxEntity.ATTR_TXT, IS_ASCENDING);
		materialAvailabilityBackgroundData.setServiceProcessIndicatorList(serviceProcessIndicatorList);

		return materialAvailabilityBackgroundData;
	}

	private void initMaterialMixDesigns(MaterialAvailabilityBackgroundData materialAvailabilityBackgroundData){
		final FilterImpl<FireqMaterialMixdesignEntity> filterImpl = new FilterImpl<FireqMaterialMixdesignEntity>();
		ReadOnlyDao<FireqMaterialMixdesignEntity> mixdesignEntityDao = getDaoProvider().getFireqMaterialMixdesignEntityDao();
		filterImpl.addExpression(Restrictions.in(FireqMaterialMixdesignEntity.ATTR_SAP_MATERIAL_ID, materialAvailabilityBackgroundData.getSapMaterialIds()));
		List<FireqMaterialMixdesignEntity> mixDesignList = mixdesignEntityDao.find(filterImpl);

		Map<Long, List<FireqMaterialMixdesignEntity>> materialMixDesignMap = new HashMap<Long, List<FireqMaterialMixdesignEntity>>();
		for (FireqMaterialMixdesignEntity fireqMaterialMixdesign : mixDesignList) {
			SapMaterialEntity sapMaterial = fireqMaterialMixdesign.getSapMaterial();
			Long sapMaterialId = sapMaterial.getSapMaterialId();

			List<FireqMaterialMixdesignEntity> fireqMaterialMixdesignList = materialMixDesignMap.get(sapMaterialId);
			if (fireqMaterialMixdesignList == null) {
				fireqMaterialMixdesignList = new ArrayList<FireqMaterialMixdesignEntity>();
				materialMixDesignMap.put(sapMaterialId, fireqMaterialMixdesignList);
			}

			fireqMaterialMixdesignList.add(fireqMaterialMixdesign);
		}

		materialAvailabilityBackgroundData.setMaterialMixDesignMap(materialMixDesignMap);
	}
	/**
	 * Returns all the plants which are in relation with any of the provided materials, and which is active, and are in the sales area
	 * specified in the constructor.
	 * @param deliveryService
	 * @return
	 */
	private Set<String> initPlantsForMaterials(MaterialAvailabilityBackgroundData materialAvailabilityBackgroundData,ShiptoSareaEntity shiptoSarea){

		Map <Long, List<PlantDTO>> materialPlantListMap = new HashMap <Long, List<PlantDTO>> ();
		/**
		 * Query for those plants, for which
		 * 1. The material exists in the sales area
		 * 2. The plant is in the sales area
		 * 3. the material is registered for the plant
		 */
		SareaEntity salesArea = shiptoSarea.getSarea();
		Long salesAreaId = salesArea.getSareaId();
		List <SapMaterialPlantSareaEntity> sapMatPlants = fetchSapMaterialPlantList(materialAvailabilityBackgroundData.getSapMaterialIds() , salesAreaId);
		Set<String> plantCdSet = new HashSet<String>();
		for (SapMaterialPlantSareaEntity sapMaterialPlantSarea : sapMatPlants) {

			Long sapMaterialId = sapMaterialPlantSarea.getSapMaterialId();
			String plantCd = sapMaterialPlantSarea.getPlantCd();
			plantCdSet.add(plantCd);
			List<PlantDTO> plantList = materialPlantListMap.get(sapMaterialId);
			if (plantList == null) {
				plantList = new ArrayList<PlantDTO>();
				materialPlantListMap.put(sapMaterialId, plantList);
			}
			final PlantDTO plantDTO = new PlantDTO(plantCd, sapMaterialPlantSarea.getPlantName(), sapMaterialPlantSarea.getProductionPlant());
			plantList.add(plantDTO);

		}

		materialAvailabilityBackgroundData.setMaterialPlantListMap(materialPlantListMap);
		return plantCdSet;
	}


	/**
	 * Initializes the uom map for each sap material. 
	 * 
	 * The available uoms currently:
	 * 
	 * each alternative uom for each sap material
	 * the base uom for each sap material
	 * Logon aid uoms: for bulk materials add all, for bag materials only container, all other materials (e.g. service) none of the aid UoMs
	 * @param materialAvailabilityBackgroundData
	 * @param locale
	 * @param caller
	 */
	private void initializeUnitOfMeasurementsForMaterials(MaterialAvailabilityBackgroundData materialAvailabilityBackgroundData, Locale locale, CallerContext caller) {
		Map <Long, List<UomTxEntity>> unitOfMeasurements = materialAvailabilityBackgroundData.getMaterialUomTxListMap();

		Set<String> altUomCds = new HashSet<String>();

		final List<Long> sapMaterialIdList = materialAvailabilityBackgroundData.getSapMaterialIds();

		if (sapMaterialIdList.isEmpty()) {
			return;
		}
		List<SapMaterialEntity> sapMaterialEntityList = sapMaterialStore.findSapMaterialsById(sapMaterialIdList);

		HashMap<Long, SapMaterialEntity> sapMaterialEntityMap = new HashMap<Long, SapMaterialEntity>();
		for (SapMaterialEntity sapMaterialEntity : sapMaterialEntityList) {
			sapMaterialEntityMap.put(sapMaterialEntity.getSapMaterialId(), sapMaterialEntity);
			UomBsEntity uom = sapMaterialEntity.getBaseUom();
			String uomCd = uom.getUomCd();
			altUomCds.add(uomCd);
		}


		List <SapMaterialAltUomEntity> alternativeUnitOfMeasurements;
		if (caller == CallerContext.WEB_ORDER) {
			alternativeUnitOfMeasurements = alternativeUnitOfMeasurementStore.fetchForMaterialsWithWebOrderOnly(sapMaterialIdList);
		}
		else {
			alternativeUnitOfMeasurements = alternativeUnitOfMeasurementStore.fetchForMaterials(sapMaterialIdList);
		}
		//build material -> alternative uom map				
		final Map<Long, List<SapMaterialAltUomEntity>> materialToAltUomMap = new HashMap<Long, List<SapMaterialAltUomEntity>>();

		for(SapMaterialAltUomEntity sapMaterialAltUom : alternativeUnitOfMeasurements) {
			SapMaterialEntity sapMaterial = sapMaterialAltUom.getSapMaterial();
			Long sapMaterialId = sapMaterial.getSapMaterialId();
			List <SapMaterialAltUomEntity> sapMaterialAltUomFilteredList = materialToAltUomMap.get(sapMaterialId);
			if (sapMaterialAltUomFilteredList == null) {
				sapMaterialAltUomFilteredList = new ArrayList<SapMaterialAltUomEntity>();
				materialToAltUomMap.put(sapMaterialId, sapMaterialAltUomFilteredList);
			}
			sapMaterialAltUomFilteredList.add(sapMaterialAltUom);

			UomBsEntity uom = sapMaterialAltUom.getUom();
			String uomCd = uom.getUomCd();
			altUomCds.add(uomCd);
		}

		//extrant base uom cds to retrieve the uom txt for
		final List<String> aidUoms = Arrays.asList(Constant.LOGON_AID_UOMS);

		Set<String> unitOfMeasurementCds = new HashSet<String>();
		unitOfMeasurementCds.addAll(altUomCds);
		unitOfMeasurementCds.addAll(aidUoms);

		//retrieve uom txts for all the codes found for the materials		
		final List<UomTxEntity> unitOfMeasurementTextsSource = unitOfMeasurementStore.findTextsByCodes(unitOfMeasurementCds, locale);
		final Map <String, UomTxEntity> unitOfMeasurementTexts = new HashMap<String, UomTxEntity>();

		for (UomTxEntity unitOfMeasurementText : unitOfMeasurementTextsSource) {
			UomTxPK unitOfMeasurementTextPrimaryKey = unitOfMeasurementText.getUomTxPK();
			UomBsEntity unitOfMeasurementBase = unitOfMeasurementTextPrimaryKey.getUomBs();
			String unitOfMeasurementCd = unitOfMeasurementBase.getUomCd();

			unitOfMeasurementTexts.put(unitOfMeasurementCd, unitOfMeasurementText);
		}

		//build final material -> uomTx map containing alternative uoms and also the base uom

		for (Long sapMaterialId : sapMaterialIdList ) {
			SapMaterialEntity sapMaterial = sapMaterialEntityMap.get(sapMaterialId);
			unitOfMeasurements.put(sapMaterialId, new ArrayList<UomTxEntity>());

			final List<SapMaterialAltUomEntity> altUomListForMaterial = materialToAltUomMap.get(sapMaterialId);

			//add aid UoMs
			if (LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(sapMaterial)) {
				for ( String aidUom : aidUoms ) {
					final UomTxEntity uomTxEntity = unitOfMeasurementTexts.get(aidUom);
					if ( uomTxEntity != null ) {					
						unitOfMeasurements.get(sapMaterialId).add(uomTxEntity);
					}
				}
			}
			else if (LogonPackageHandlingUtil.isBag(sapMaterial)) {
				if (aidUoms.contains(Constant.UOM_CONTAINER)) {
					final UomTxEntity uomTxEntity = unitOfMeasurementTexts.get(Constant.UOM_CONTAINER);
					if (uomTxEntity != null) {					
						unitOfMeasurements.get(sapMaterialId).add(uomTxEntity);
					}
				}
			}


			if ( altUomListForMaterial != null ) {

				//add each alternative uom's tx
				for ( SapMaterialAltUomEntity sapMaterialAltUomEntity : altUomListForMaterial ) {

					final UomTxEntity uomTxEntity = unitOfMeasurementTexts.get(sapMaterialAltUomEntity.getUom().getUomCd());
					if ( uomTxEntity != null ) {					
						unitOfMeasurements.get(sapMaterialId).add(uomTxEntity);
					}
				}
			}
			//additional uom is the base uom of the material
			final UomTxEntity baseUom = unitOfMeasurementTexts.get(sapMaterial.getBaseUom().getUomCd());

			if ( baseUom != null  && !unitOfMeasurements.get(sapMaterial.getSapMaterialId()).contains(baseUom)) {
				unitOfMeasurements.get(sapMaterial.getSapMaterialId()).add(baseUom);
			}


		}
	}

	/**
	 * Initializes the maps containing language dependent data for the available selections.
	 * 
	 * @param locale
	 * @param deliverySettingSalesAreaMap
	 * @param initMaterialSelectionForShipto
	 */
	private void initLanguageDependentData(Locale locale,
			DeliverySettingSalesAreaMap deliverySettingSalesAreaMap,
			final MaterialSelectionInitializer initMaterialSelectionForShipto) {

		DeliverySettingProductMap materialProducts = deliverySettingSalesAreaMap.getMaterialDeliverySettings();
		DeliverySettingProductMap serviceProducts = deliverySettingSalesAreaMap.getServiceDeliverySettings();
		DeliverySettingProductMap serviceRestrictionProducts = deliverySettingSalesAreaMap.getServiceRestrictionDeliverySettings();
		DeliverySettingProductMap materialRestrictionProducts = deliverySettingSalesAreaMap.getMaterialRestrictionDeliverySettings();

		
		DeliverySettingLanguageDependentResolver resolvermaterialProducts = initResolver(locale, materialProducts);
		resolveFields(materialProducts, resolvermaterialProducts);
		
		DeliverySettingLanguageDependentResolver resolverserviceProducts = initResolver(locale, serviceProducts);
		resolveFields(serviceProducts, resolverserviceProducts);

		DeliverySettingLanguageDependentResolver resolverserviceRestrProducts = initResolver(locale, serviceRestrictionProducts);
		resolveFields(serviceRestrictionProducts, resolverserviceRestrProducts);

		
		DeliverySettingLanguageDependentResolver resolvermaterialRestrProducts = initResolver(locale, materialRestrictionProducts);
		resolveFields(materialRestrictionProducts, resolvermaterialRestrProducts);
	}

	@AdnTrace
	private GlobalTransportBackgroundData initGlobalTransportBackgroundData ( CallerContext context, Locale locale) {

		GlobalTransportBackgroundData globalTransportBackgroundData = new GlobalTransportBackgroundData();

		initPoolCommercialCarrierMap(locale, globalTransportBackgroundData);
		
		globalTransportBackgroundData.setCommercialExecutingCarrierMap(initExecutingCarrierMap());
		globalTransportBackgroundData.setContext(context);		
		globalTransportBackgroundData.setDeliveryTypeTxList(findDeliveryTypeTxEntityList(locale, "txt" , true));
		globalTransportBackgroundData.setTruckTypeTxMap(loadActiveTruckTypeMap(locale));
		globalTransportBackgroundData.setWagonTypeTxMap(loadWagonTypeMap(locale));
		globalTransportBackgroundData.setShipTypeTxMap(loadSimpleVehicleTypeMap(locale, Constant.SIMPLE_VEHICLE_SHIP_TYPE_CD));
		globalTransportBackgroundData.setBlockTrainTypeTxMap(loadSimpleVehicleTypeMap(locale, Constant.SIMPLE_VEHICLE_BLOCK_TRAIN_TYPE_CD));
		globalTransportBackgroundData.setTruckTypeToTrspCat(loadTruckTypeTrspCatMap());
		final Map<String, String> timeUomMap = loadTimeUnits(locale);
		globalTransportBackgroundData.setTimeUomMap(timeUomMap);

		return globalTransportBackgroundData;
	}

	/**
	 * Loads the map of truck types to transport categories from the database. This is the table <code>TLGNR_TRUCK_TYPE_TRSP_CAT_BS</code>.
	 * @return A map containing for each truck type the set of supported transport categories.
	 * If there are no transport categories in the table for a truck type, the map will have no entry for this truck type
	 * (rather than return an empty set). So you must always check using <code>containsKey()</code>. 
	 */
	@AdnTrace
	private Map<Long, Set<Long>> loadTruckTypeTrspCatMap() {

		// Map to return
		final Map<Long, Set<Long>> truckTypeToTransportCatMap = new HashMap<Long,Set<Long>>();

		String jpqlQuery = "SELECT DISTINCT t FROM TruckTypeTrspCatBsEntity t JOIN FETCH t.truckTransportCat JOIN FETCH t.truckType";
		Query query = em.createQuery(jpqlQuery);

		List<TruckTypeTrspCatBsEntity> truckTypeList = query.getResultList();

		for (TruckTypeTrspCatBsEntity truckTypeTransportCategory : truckTypeList)
		{
			final Long truckTypeCd = truckTypeTransportCategory.getTruckType().getTruckTypeCd();
			final Long trspCat = truckTypeTransportCategory.getTruckTransportCat().getTruckTransportCatCd();

			/* Initialize with empty set if this is the first time
			 * we encounter a truck type.
			 */
			if (!truckTypeToTransportCatMap.containsKey(truckTypeCd)) {
				truckTypeToTransportCatMap.put(truckTypeCd, new HashSet<Long>());
			}

			truckTypeToTransportCatMap.get(truckTypeCd).add(trspCat);
		}

		return truckTypeToTransportCatMap;
	}

	/**
	 * Hardcoded two uoms hour and minutes. Currently there is no need to return the uom itself.
	 * It's enough to return the 
	 * @param locale
	 * @return
	 */
	private Map<String, String> loadTimeUnits(Locale locale) {
		String [] TIME_UOMS = {"HRE","MIN"};
		final Set<String> codesOfTimeUnits = new HashSet<String>(Arrays.asList(TIME_UOMS));
		final List<UomTxEntity> textsOfTimeUnits = unitOfMeasurementStore.findTextsByCodes(codesOfTimeUnits, locale);

		Map<String, String> timeUomMap = new HashMap<String, String>();
		for (UomTxEntity timeUom : textsOfTimeUnits ) {
			UomTxPK uomTxPk = timeUom.getUomTxPK();
			UomBsEntity uomBs = uomTxPk.getUomBs();
			String uomCd = uomBs.getUomCd();
			timeUomMap.put(uomCd, timeUom.getTxt());
		}
		return timeUomMap;

	}


	/**
	 * Returns a Map <Long, TruckTypeTxEntity> containing all possible active truck types.
	 * 
	 * @param locale
	 * @return
	 */
	@AdnTrace
	private Map <Long, TruckTypeTxEntity> loadActiveTruckTypeMap(Locale locale) {
		final List<TruckTypeTxEntity> truckTypeList = fetchActiveTruckTypes(locale);

		/** Truck type CD --> TruckTypeEntity */
		final Map <Long, TruckTypeTxEntity> truckTypeTxMap = new LinkedHashMap<Long, TruckTypeTxEntity>();
		for (TruckTypeTxEntity truckTypeTxEntity : truckTypeList) {
			Long truckTypeCd = truckTypeTxEntity.getTruckTypeTxPK().getTruckTypeBs().getTruckTypeCd();
			truckTypeTxMap.put(truckTypeCd, truckTypeTxEntity);
		}

		return truckTypeTxMap;
	}

	/**
	 * Returns a Map <Long, WagonTypeTxEntity> containing all possible wagon types.
	 * @param locale
	 * @return
	 */
	@AdnTrace
	private Map<Long, WagonTypeTxEntity> loadWagonTypeMap(Locale locale) {
		final List<WagonTypeTxEntity> wagonTypeList = findWagonTypeTxEntityList(locale);

		/** Wagon type CD --> WagonTypeTxEntity */
		final Map <Long, WagonTypeTxEntity> wagonTypeTxMap = new LinkedHashMap<Long, WagonTypeTxEntity>();
		for (WagonTypeTxEntity wagonTypeTxEntity : wagonTypeList) {
			Long wagonTypeCd = wagonTypeTxEntity.getWagonTypeTxPK().getWagonTypeBs().getWagonTypeCd();
			wagonTypeTxMap.put(wagonTypeCd, wagonTypeTxEntity);
		}

		return wagonTypeTxMap;
	}

	/**
	 * Simple Vehicles (Ship and Block-Train) share the same refdata table but has only one type per category (see LRQ 108.0 Managing Ship & Block-Train)
	 * @param locale
	 * @param simpleVehicleTypeCd either Constant.SIMPLE_VEHICLE_SHIP_TYPE_CD or SIMPLE_VEHICLE_BLOCK_TRAIN_TYPE_CD are supported at the moment.
	 * @return
	 */
	@AdnTrace
	private Map<Long, SimpleVehicleTypeTxEntity> loadSimpleVehicleTypeMap(Locale locale, long simpleVehicleTypeCd) {
		SimpleVehicleTypeTxEntity simpleVehicleType = getSimpleVehicleTypeTxEntity(locale, simpleVehicleTypeCd);
		final Map <Long, SimpleVehicleTypeTxEntity> simpleVehicleTypeTxMap = new LinkedHashMap<Long, SimpleVehicleTypeTxEntity>();
		Long key =  simpleVehicleType.getSimpleVehicleTypeTxPK().getSimpleVehicleTypeBs().getSimpleVehicleTypeCd();
		simpleVehicleTypeTxMap.put(key, simpleVehicleType);

		return simpleVehicleTypeTxMap;
	}



	/**
	 * Initializes a map containing the pools and the commercial carriers for them.
	 * The return value is a Map containing list of {@link CommercialCarrierDTO} entries
	 * for each active pool.
	 * @param locale
	 * @param globalTransportBackgroundData 
	 * @return
	 */
	@AdnTrace
	private void initPoolCommercialCarrierMap(Locale locale, GlobalTransportBackgroundData globalTransportBackgroundData) {

		List<PoolCommCarrierEntity> poolCommercialCarrierCombinations = poolAdminService.fetchPoolCommCarrierWithSapTrspModeCd();

		Map<Long, CommercialCarrierDTO> commercialCarrierDTOs =  new HashMap<Long, CommercialCarrierDTO>();
		Map <Long, List<CommercialCarrierDTO>> poolCommercialCarrierPartnerships = new HashMap<Long, List<CommercialCarrierDTO>>();
		Set<Long> poolIds = new HashSet<Long>();
		List<PoolDTO> pools = new ArrayList<PoolDTO>();
		for (PoolCommCarrierEntity poolCommercialCarrier : poolCommercialCarrierCombinations ) {

			final CommercialCarrierDTO commercialCarrierDTO;

			if ( commercialCarrierDTOs.containsKey(poolCommercialCarrier.getCarrierId())) {
				commercialCarrierDTO =  commercialCarrierDTOs.get(poolCommercialCarrier.getCarrierId());
			}
			else {					
				commercialCarrierDTO = new CommercialCarrierDTO(poolCommercialCarrier.getCarrierId(), poolCommercialCarrier.getDisplayName(), poolCommercialCarrier.getCarrierRequiresPlanningFlag());
				commercialCarrierDTOs.put(poolCommercialCarrier.getCarrierId(), commercialCarrierDTO);
			}

			Long poolId = poolCommercialCarrier.getPoolId();
			poolIds.add(poolId);
			PoolDTO pool = new PoolDTO(poolId, poolCommercialCarrier.getSapTrspModeCd());
			if (!pools.contains(pool)) {
				pools.add(pool);
			}
			if ( poolCommercialCarrierPartnerships.containsKey(poolId)) {
				poolCommercialCarrierPartnerships.get(poolId).add(commercialCarrierDTO);
			}
			else {
				final List<CommercialCarrierDTO> commercialCarriers = new ArrayList<CommercialCarrierDTO>();
				commercialCarriers.add(commercialCarrierDTO);
				poolCommercialCarrierPartnerships.put(poolId, commercialCarriers);
			}
		}
		globalTransportBackgroundData.setPools(pools);
		globalTransportBackgroundData.setPoolTxMap(poolAdminService.fetchAllPoolTxEntities(locale, poolIds));
		globalTransportBackgroundData.setPoolCarrierMap(poolCommercialCarrierPartnerships);
		return;
	}

		/**
		 * Based on the previously created Pool-Commercial Carrier map fetches the Executing Carriers 
		 * into a map where the key is the Commercial Carrier Id and the value is a list of Executing Carriers 
		 * The return value is a list of {@link ExecutingCarrierDTO} for each commercial carrier id.
		 * @return map containing Commercial Carrier Id->List of Executing Carriers
		 */
		private Map<Long, List<ExecutingCarrierDTO>> initExecutingCarrierMap() {

			Map <Long, List<ExecutingCarrierDTO>> commercialExecutingCarrierPartnerships = new HashMap<Long, List<ExecutingCarrierDTO>>();
			
			Map<Long, ExecutingCarrierDTO> executingCarrierDTOs =  new HashMap<Long, ExecutingCarrierDTO>();
			
			List<CommExecCarrPfActiveEntity> commercialExecCarrierCombinations = carrierService.findCommercialExecCarrierPartnersOrdered();

			for (CommExecCarrPfActiveEntity commExecCarrierPartner : commercialExecCarrierCombinations) {
				final ExecutingCarrierDTO executingCarrierDTO;
				
				if ( executingCarrierDTOs.containsKey(commExecCarrierPartner.getExecCarrierId())) {
					executingCarrierDTO =  executingCarrierDTOs.get(commExecCarrierPartner.getExecCarrierId());
				}
				else {					
					executingCarrierDTO = new ExecutingCarrierDTO(commExecCarrierPartner.getExecCarrierId(), commExecCarrierPartner.getExecCarrierDisplayName());
					executingCarrierDTOs.put(commExecCarrierPartner.getExecCarrierId(), executingCarrierDTO);
				}
				
				Long commCarrierId = commExecCarrierPartner.getCommCarrierId();
				
				if ( commercialExecutingCarrierPartnerships.containsKey(commCarrierId)) {
					commercialExecutingCarrierPartnerships.get(commCarrierId).add(executingCarrierDTO);
				}
				else {
					final List<ExecutingCarrierDTO> executingCarriers = new ArrayList<ExecutingCarrierDTO>();
					executingCarriers.add(executingCarrierDTO);
					commercialExecutingCarrierPartnerships.put(commExecCarrierPartner.getCommCarrierId(), executingCarriers);
				}
			}

			return commercialExecutingCarrierPartnerships;

		}

	/**
	 * It loads the active incoterm delivery types regarding the given sales area id's list. 
	 * @param salesAreaIdList
	 * @return
	 */
	@AdnTrace
	private Map<String, Set<Long>> loadActiveIncotermDeliveryTypeMap(Long salesAreaId) {
		Map<String, Set<Long>> deliveryTypesByIncoterm = new HashMap<String, Set<Long>>();
		List<IncotermDelivTypeEntity> deliveryTypes = incotermDeliveryTypeStore.findBySalesAreaId(salesAreaId);

		if (deliveryTypes.isEmpty()) {
			return deliveryTypesByIncoterm;
		}

		for (IncotermDelivTypeEntity deliveryType : deliveryTypes) {

			String incoterm = deliveryType.getSapIncotermCd();
			Set<Long> deliveryTypesOfIncoterm = deliveryTypesByIncoterm.get(incoterm);
			if (deliveryTypesOfIncoterm == null) {
				deliveryTypesOfIncoterm = new HashSet<Long>();
				deliveryTypesByIncoterm.put(incoterm, deliveryTypesOfIncoterm);
			}

			Long deliveryTypeCd = deliveryType.getDeliveryTypeCd();
			deliveryTypesOfIncoterm.add(deliveryTypeCd);
		}

		return deliveryTypesByIncoterm;
	}
	
	/** 
	 * Initializes a parent {@link DeliverySettingShiptoMap} object for the provided shiptoSalesAreas, for which multiple {@link DeliverySettingSalesAreaMap} objects will be
	 * created.
	 * 
	 */
	@AdnTrace
	public DeliverySettingShiptoMap initDeliverySettingShiptoMapForCallCenter(SapBpEntity soldTo,
			Locale locale, List<ShiptoSareaEntity> shiptoSalesAreasList, Boolean allowFutureContract) {
		CallerContext caller = CallerContext.CALL_CENTER;
		final ShiptoEntity shipTo = getCommonShiptoOfSalesAreas(soldTo,
				shiptoSalesAreasList);
		
		DeliverySettingShiptoMap shiptoMap = initDeliverySettingShiptoMap(
				shipTo, soldTo, locale, caller);
		
		try {
			/**
			 * Initialize the data objects containing data for the sales areas. 
			 */
			for (ShiptoSareaEntity shiptoSarea : shiptoSalesAreasList) {
				Long sareaId = shiptoSarea.getSarea().getSareaId();

				DeliverySettingSalesAreaMap deliverySettingSalesAreaMap = new DeliverySettingSalesAreaMap(shiptoSarea);

				deliverySettingSalesAreaMap.setFinancialbackgroundData(initFinancialBackgroundData(shiptoSarea, soldTo));
				Boolean filterOutFutureContracts = allowFutureContract ? FILTER_OUT_FUTURE_CONTRACTS_OFF : FILTER_OUT_FUTURE_CONTRACTS_ON; 
				final Set<Long> validContractIds = loadValidContracts(sareaId, shipTo.getShiptoSapBp().getSapBpId(), soldTo.getSapBpId(), filterOutFutureContracts);
				final List <ShiptoSareaRestrEntity> restrictions = fetchShiptoRestrList(shiptoSarea.getShiptoSareaId(), validContractIds, null, INCLUDE_ALL);
				
				final MaterialSelectionInitializer initMaterialSelectionForShipto = initMaterialSelectionForShipto(soldTo, shiptoSarea, null, deliverySettingSalesAreaMap, 
						REMOVE_CONTRACT_ITEMS_WITH_ZERO_QUANTITY_ON,
						ALLOW_ONLY_GROUPLEADER_FOR_QUANTITY_CONTROLLED_CONTRACTS_ON,
						KEEP_BASIC_COMBINATION_IF_RESTRICTION_EXISTS_OFF,
						KEEP_INVALID_RESTRICTIONS_OFF,
						validContractIds,
						restrictions,
						locale
						);
				deliverySettingSalesAreaMap.setMaterialSelectionInitializer(initMaterialSelectionForShipto);
				
				initLanguageDependentData(locale, deliverySettingSalesAreaMap,
							initMaterialSelectionForShipto);
				
				final MaterialAvailabilityBackgroundData materialAvailability = initMaterialAvailabilityBackgroundData(shiptoSarea, locale, caller, deliverySettingSalesAreaMap.getMaterialSelectionInitializer().getSapMaterialIds());
				deliverySettingSalesAreaMap.setMaterialAvailabilityBackgroundData(materialAvailability);

				deliverySettingSalesAreaMap.setDeliveryTypesByIncotermMap(loadActiveIncotermDeliveryTypeMap(shiptoSarea.getSarea().getSareaId()));
				
				deliverySettingSalesAreaMap.setShiptoSettings(shiptoMap);

				shiptoMap.getSareaMaps().put(sareaId, deliverySettingSalesAreaMap);
			}
		}
		catch (Exception e) {
			LOG.error("Could not initialize sales area maps [ShiptoId=" + shipTo.getShiptoId() +  ";shiptoSapBp.sapId=" + shipTo.getShiptoSapBp().getSapId()+ ";shiptoSapBpName=" + shipTo.getShiptoSapBp().getName1() + ";soldtoSapBp.sapId=" + soldTo.getSapId() +"]");
			throw new LogonException(e);
		}
		
		return shiptoMap;
	}
	
	
	/** 
	 * Initializes a parent {@link DeliverySettingShiptoMap} object for the provided shiptoSalesAreas, for which multiple {@link DeliverySettingSalesAreaMap} objects will be
	 * created.
	 * 
	 */
	@AdnTrace
	public DeliverySettingShiptoMap initDeliverySettingShiptoMapForMasterDataShipto (SapBpEntity soldTo, Locale locale, List<ShiptoSareaEntity> shiptoSalesAreasList) {
		CallerContext caller = CallerContext.DISPOKARTE;
		final ShiptoEntity shipTo = getCommonShiptoOfSalesAreas(soldTo,
				shiptoSalesAreasList);
		
		DeliverySettingShiptoMap shiptoMap = initDeliverySettingShiptoMap(
				shipTo, soldTo, locale, caller);
		
		try {
			/**
			 * Initialize the data objects containing data for the sales areas. 
			 */
			for (ShiptoSareaEntity shiptoSarea : shiptoSalesAreasList) {
				Long sareaId = shiptoSarea.getSarea().getSareaId();

				DeliverySettingSalesAreaMap deliverySettingSalesAreaMap = new DeliverySettingSalesAreaMap(shiptoSarea);

				deliverySettingSalesAreaMap.setFinancialbackgroundData(initFinancialBackgroundData(shiptoSarea, soldTo));
			
				final Set<Long> validContractIds = loadValidContracts(sareaId, shipTo.getShiptoSapBp().getSapBpId(), soldTo.getSapBpId(), FILTER_OUT_FUTURE_CONTRACTS_OFF);
				List <ShiptoSareaRestrEntity> restrictions = shiptoSarea.getRestrictions();
				
				final MaterialSelectionInitializer initMaterialSelectionForShipto = initMaterialSelectionForShipto(soldTo, shiptoSarea, null, deliverySettingSalesAreaMap,
						REMOVE_CONTRACT_ITEMS_WITH_ZERO_QUANTITY_OFF,
						ALLOW_ONLY_GROUPLEADER_FOR_QUANTITY_CONTROLLED_CONTRACTS_ON,
						KEEP_BASIC_COMBINATION_IF_RESTRICTION_EXISTS_ON,
						KEEP_INVALID_RESTRICTIONS_ON,
						validContractIds,
						restrictions,
						locale);
				deliverySettingSalesAreaMap.setMaterialSelectionInitializer(initMaterialSelectionForShipto);
				
				initLanguageDependentData(locale, deliverySettingSalesAreaMap, initMaterialSelectionForShipto);
				
				final MaterialAvailabilityBackgroundData materialAvailability = initMaterialAvailabilityBackgroundData(shiptoSarea, locale, caller, deliverySettingSalesAreaMap.getMaterialSelectionInitializer().getSapMaterialIds());
				deliverySettingSalesAreaMap.setMaterialAvailabilityBackgroundData(materialAvailability);

				deliverySettingSalesAreaMap.setDeliveryTypesByIncotermMap(loadActiveIncotermDeliveryTypeMap(shiptoSarea.getSarea().getSareaId()));
				
				deliverySettingSalesAreaMap.setShiptoSettings(shiptoMap);

				shiptoMap.getSareaMaps().put(sareaId, deliverySettingSalesAreaMap);
			}
		}
		catch (Exception e) {
			LOG.error("Could not initialize sales area maps [ShiptoId=" + shipTo.getShiptoId() +  ";shiptoSapBp.sapId=" + shipTo.getShiptoSapBp().getSapId()+ ";shiptoSapBpName=" + shipTo.getShiptoSapBp().getName1() + ";soldtoSapBp.sapId=" + soldTo.getSapId() +"]");
			throw new LogonException(e);
		}
		
		return shiptoMap;		
	}
	
	/** 
	 * Initializes a parent {@link DeliverySettingShiptoMap} object for the provided shiptoSalesAreas, for which multiple {@link DeliverySettingSalesAreaMap} objects will be
	 * created.
	 * 
	 */
	@AdnTrace
	public DeliverySettingShiptoMap initDeliverySettingShiptoMapForPickupTerminal (SapBpEntity soldTo, Locale locale, List<ShiptoSareaEntity> shiptoSalesAreasList) {
		CallerContext caller = CallerContext.PICKUP_TERMINAL;
		final ShiptoEntity shipTo = getCommonShiptoOfSalesAreas(soldTo,
				shiptoSalesAreasList);
		
		DeliverySettingShiptoMap shiptoMap = initDeliverySettingShiptoMap(
				shipTo, soldTo, locale, caller);
		
		try {
			/**
			 * Initialize the data objects containing data for the sales areas. 
			 */
			for (ShiptoSareaEntity shiptoSarea : shiptoSalesAreasList) {
				Long sareaId = shiptoSarea.getSarea().getSareaId();

				DeliverySettingSalesAreaMap deliverySettingSalesAreaMap = new DeliverySettingSalesAreaMap(shiptoSarea);

				deliverySettingSalesAreaMap.setFinancialbackgroundData(initFinancialBackgroundData(shiptoSarea, soldTo));

				final Set<Long> validContractIds = loadValidContracts(sareaId, shipTo.getShiptoSapBp().getSapBpId(), soldTo.getSapBpId(), FILTER_OUT_FUTURE_CONTRACTS_ON);

				//TODO (emoke) still too wide set of restrictions loaded, example: there is no filter for loadingpoint, only the pickup web component filters those restrictions out onwards, and that's why they don't show up on screen
				//add as specific filter criteria as possible!
				List <ShiptoSareaRestrEntity> restrictions = fetchShiptoRestrListForPickup(shiptoSarea.getShiptoSareaId(), validContractIds);
						
				final MaterialSelectionInitializer initMaterialSelectionForShipto = initMaterialSelectionForShipto(soldTo, shiptoSarea, null, deliverySettingSalesAreaMap,
						REMOVE_CONTRACT_ITEMS_WITH_ZERO_QUANTITY_ON,
						ALLOW_ONLY_GROUPLEADER_FOR_QUANTITY_CONTROLLED_CONTRACTS_ON,
						KEEP_BASIC_COMBINATION_IF_RESTRICTION_EXISTS_OFF,
						KEEP_INVALID_RESTRICTIONS_OFF,
						validContractIds,
						restrictions,
						locale
						);
				deliverySettingSalesAreaMap.setMaterialSelectionInitializer(initMaterialSelectionForShipto);
				
				initLanguageDependentData(locale, deliverySettingSalesAreaMap, initMaterialSelectionForShipto);
				
				final MaterialAvailabilityBackgroundData materialAvailability = initMaterialAvailabilityBackgroundData(shiptoSarea, locale, caller, deliverySettingSalesAreaMap.getMaterialSelectionInitializer().getSapMaterialIds());
				deliverySettingSalesAreaMap.setMaterialAvailabilityBackgroundData(materialAvailability);

				deliverySettingSalesAreaMap.setDeliveryTypesByIncotermMap(loadActiveIncotermDeliveryTypeMap(shiptoSarea.getSarea().getSareaId()));
				
				deliverySettingSalesAreaMap.setShiptoSettings(shiptoMap);

				shiptoMap.getSareaMaps().put(sareaId, deliverySettingSalesAreaMap);
			}
		}
		catch (Exception e) {
			LOG.error("Could not initialize sales area maps [ShiptoId=" + shipTo.getShiptoId() +  ";shiptoSapBp.sapId=" + shipTo.getShiptoSapBp().getSapId()+ ";shiptoSapBpName=" + shipTo.getShiptoSapBp().getName1() + ";soldtoSapBp.sapId=" + soldTo.getSapId() +"]");
			throw new LogonException(e);
		}
		
		return shiptoMap;
	}

	/** 
	 * Initializes a parent {@link DeliverySettingShiptoMap} object for the provided shiptoSalesAreas, for which multiple {@link DeliverySettingSalesAreaMap} objects will be
	 * created.
	 * 
	 */
	@AdnTrace
	public DeliverySettingShiptoMap initDeliverySettingShiptoMapForWebOrder(SapBpEntity soldTo,
			Locale locale, List<ShiptoSareaEntity> shiptoSalesAreas) {
		CallerContext caller = CallerContext.WEB_ORDER;
		final ShiptoEntity shipTo = getCommonShiptoOfSalesAreas(soldTo, shiptoSalesAreas);

		DeliverySettingShiptoMap shipToMap = initDeliverySettingShiptoMap(shipTo, soldTo, locale,
				caller);

		try {
			/**
			 * Initialize the data objects containing data for the sales areas. 
			 */
			for (ShiptoSareaEntity shipToSalesArea : shiptoSalesAreas) {
				Long salesAreaId = shipToSalesArea.getSarea().getSareaId();

				DeliverySettingSalesAreaMap deliverySettingSalesAreaMap = new DeliverySettingSalesAreaMap(
						shipToSalesArea);

				deliverySettingSalesAreaMap.setFinancialbackgroundData(initFinancialBackgroundData(
						shipToSalesArea, soldTo));

				final Set<Long> validContractIds = loadValidContracts(salesAreaId,
						shipTo.getShiptoSapBp().getSapBpId(), soldTo.getSapBpId(),
						FILTER_OUT_FUTURE_CONTRACTS_ON);

				List<ShiptoSareaRestrEntity> restrictions = fetchShiptoRestrListForWebOrder(
						shipToSalesArea.getShiptoSareaId(), validContractIds);

				final MaterialSelectionInitializer initMaterialSelectionForShipto = initMaterialSelectionForShipto(
						soldTo, shipToSalesArea, null, deliverySettingSalesAreaMap,
						REMOVE_CONTRACT_ITEMS_WITH_ZERO_QUANTITY_ON,
						ALLOW_ONLY_GROUPLEADER_FOR_QUANTITY_CONTROLLED_CONTRACTS_ON,
						KEEP_BASIC_COMBINATION_IF_RESTRICTION_EXISTS_OFF,
						KEEP_INVALID_RESTRICTIONS_OFF, validContractIds, restrictions,locale);
				deliverySettingSalesAreaMap.setMaterialSelectionInitializer(initMaterialSelectionForShipto);

				initLanguageDependentData(locale, deliverySettingSalesAreaMap,
						initMaterialSelectionForShipto);

				final MaterialAvailabilityBackgroundData materialAvailability = initMaterialAvailabilityBackgroundData(
						shipToSalesArea,
						locale,
						caller,
						deliverySettingSalesAreaMap.getMaterialSelectionInitializer().getSapMaterialIds());
				deliverySettingSalesAreaMap.setMaterialAvailabilityBackgroundData(materialAvailability);

				deliverySettingSalesAreaMap.setDeliveryTypesByIncotermMap(loadActiveIncotermDeliveryTypeMap(shipToSalesArea.getSarea().getSareaId()));

				deliverySettingSalesAreaMap.setShiptoSettings(shipToMap);

				shipToMap.getSareaMaps().put(salesAreaId, deliverySettingSalesAreaMap);
			}
		}
		catch (Exception exception) {
			LOG.error("Could not initialize sales area maps [ShiptoId=" + shipTo.getShiptoId()
					+ ";shiptoSapBp.sapId=" + shipTo.getShiptoSapBp().getSapId()
					+ ";shiptoSapBpName=" + shipTo.getShiptoSapBp().getName1()
					+ ";soldtoSapBp.sapId=" + soldTo.getSapId() + "]");
			throw new LogonException(exception);
		}

		return shipToMap;
	}

	private ShiptoEntity getCommonShiptoOfSalesAreas(SapBpEntity soldTo,
			List<ShiptoSareaEntity> shiptoSalesAreasList) {
		if ( shiptoSalesAreasList == null || shiptoSalesAreasList.isEmpty() ) {
			LOG.error("No available sales area found for soldTo. [Soldto SAP Nr=" +  soldTo.getSapId() + "]");
			throw new LogonException("No available sales area found for soldTo. [soldtoSapBp.sapId=" +  soldTo.getSapId() + "]", Reason.RES_ERROR_DISPOKARTE_NO_ACTIVE_SAREA_FOR_SHIPTO);
		}
		final ShiptoSareaEntity firstShiptoSarea = shiptoSalesAreasList.get(0);

		final ShiptoEntity shipTo = firstShiptoSarea.getShipto();
		
		
		for (ShiptoSareaEntity shiptoSarea : shiptoSalesAreasList) {
			if (!shipTo.getShiptoId().equals(shiptoSarea.getShipto().getShiptoId())) {
				/**
				 * The shipto is initialized from the common shipto of the sales areas provided.  
				 * The shipto must be EQUAL for all, as the rules are initialized for the common shipto.
				 *  there must be ONE, not more, and not less
				 *  */
				throw new LogonException("Sales areas belonging to the same shipto are expected.", Reason.RES_ERROR_IMPL);
			}
		}
		return shipTo;
	}

	/**
	 * Initializes a parent {@link DeliverySettingShiptoMap}. For this parent structure one single {@link DeliverySettingSalesAreaMap} object will be
	 * created - one belonging to the specified shiptoSalesArea parameter. The structure will be initialized only for the specified {@link SapMaterialEntity}
	 */
	public DeliverySettingShiptoMap initDeliverySettingShiptoMapForBimodalOrderCreation (SapBpEntity soldTo, Locale locale, ShiptoSareaEntity shiptoSalesArea, Long sapMaterialId) {
		CallerContext caller = CallerContext.CALL_CENTER_BIMODAL;
		if (sapMaterialId == null) {
			throw new IllegalArgumentException("Sap material has to be specified.");
		}
		Set<Long> sapMaterialIds = new HashSet<Long>();
		sapMaterialIds.add(sapMaterialId);

		final ShiptoEntity shipTo = shiptoSalesArea.getShipto();
		
		DeliverySettingShiptoMap shiptoMap = initDeliverySettingShiptoMap(
				shipTo, soldTo, locale, caller);
		
		final Long sareaId = shiptoSalesArea.getSarea().getSareaId();

		DeliverySettingSalesAreaMap deliverySettingSalesAreaMap = new DeliverySettingSalesAreaMap(shiptoSalesArea);

		deliverySettingSalesAreaMap.setFinancialbackgroundData(initFinancialBackgroundData(shiptoSalesArea, soldTo));

		final Set<Long> validContractIds = loadValidContracts(sareaId, shipTo.getShiptoSapBp().getSapBpId(), soldTo.getSapBpId(), FILTER_OUT_FUTURE_CONTRACTS_ON);
		
		final List <ShiptoSareaRestrEntity> restrictions = fetchShiptoRestrList(shiptoSalesArea.getShiptoSareaId(), validContractIds, sapMaterialIds, INCLUDE_ALL);
		
		final MaterialSelectionInitializer initMaterialSelectionForShipto = initMaterialSelectionForShipto(soldTo, shiptoSalesArea, sapMaterialIds, deliverySettingSalesAreaMap,
				REMOVE_CONTRACT_ITEMS_WITH_ZERO_QUANTITY_ON,
				ALLOW_ONLY_GROUPLEADER_FOR_QUANTITY_CONTROLLED_CONTRACTS_ON,
				KEEP_BASIC_COMBINATION_IF_RESTRICTION_EXISTS_OFF,
				KEEP_INVALID_RESTRICTIONS_OFF,
				validContractIds,
				restrictions,
				locale);
		deliverySettingSalesAreaMap.setMaterialSelectionInitializer(initMaterialSelectionForShipto);
		
		//TODO (emoke) might not be necessary to load further data, it should be enough to load the delivery settings 
		final MaterialAvailabilityBackgroundData materialAvailability = initMaterialAvailabilityBackgroundData(shiptoSalesArea, locale, caller, deliverySettingSalesAreaMap.getMaterialSelectionInitializer().getSapMaterialIds());
		deliverySettingSalesAreaMap.setMaterialAvailabilityBackgroundData(materialAvailability);

		deliverySettingSalesAreaMap.setDeliveryTypesByIncotermMap(loadActiveIncotermDeliveryTypeMap(shiptoSalesArea.getSarea().getSareaId()));
		
		deliverySettingSalesAreaMap.setShiptoSettings(shiptoMap);

		shiptoMap.getSareaMaps().put(sareaId, deliverySettingSalesAreaMap);
		
		return shiptoMap;
	}
	
	/**
	 * Initializes a parent {@link DeliverySettingShiptoMap}. For this parent structure one single {@link DeliverySettingSalesAreaMap} object will be
	 * created - one belonging to the specified shiptoSalesArea parameter. The structure will be initialized only for the specified {@link SapMaterialEntity}
	 */
	public DeliverySettingShiptoMap initDeliverySettingShiptoMapForWebOrder(SapBpEntity soldTo, Locale locale, ShiptoSareaEntity shiptoSalesArea) {
		 CallerContext caller = CallerContext.WEB_ORDER;
		 final ShiptoEntity shipTo = shiptoSalesArea.getShipto();

		 DeliverySettingShiptoMap shiptoMap = initDeliverySettingShiptoMap(
				 shipTo, soldTo, locale, caller);

		 final Long sareaId = shiptoSalesArea.getSarea().getSareaId();

		 DeliverySettingSalesAreaMap deliverySettingSalesAreaMap = new DeliverySettingSalesAreaMap(shiptoSalesArea);

		 deliverySettingSalesAreaMap.setFinancialbackgroundData(initFinancialBackgroundData(shiptoSalesArea, soldTo));

		final Set<Long> validContractIds = loadValidContracts(sareaId, shipTo.getShiptoSapBp().getSapBpId(), soldTo.getSapBpId(), FILTER_OUT_FUTURE_CONTRACTS_ON);
		
		final List <ShiptoSareaRestrEntity> restrictions = fetchShiptoRestrListForWebOrder(shiptoSalesArea.getShiptoSareaId(), validContractIds);
			
		 final MaterialSelectionInitializer initMaterialSelectionForShipto = initMaterialSelectionForShipto(soldTo, shiptoSalesArea, null, deliverySettingSalesAreaMap,
				 REMOVE_CONTRACT_ITEMS_WITH_ZERO_QUANTITY_ON,
				 ALLOW_ONLY_GROUPLEADER_FOR_QUANTITY_CONTROLLED_CONTRACTS_ON,
				 KEEP_BASIC_COMBINATION_IF_RESTRICTION_EXISTS_OFF,
				 KEEP_INVALID_RESTRICTIONS_OFF,
				validContractIds,
				restrictions,
				locale);
		 deliverySettingSalesAreaMap.setMaterialSelectionInitializer(initMaterialSelectionForShipto);

		 initLanguageDependentData(locale, deliverySettingSalesAreaMap, initMaterialSelectionForShipto);

		 final MaterialAvailabilityBackgroundData materialAvailability = initMaterialAvailabilityBackgroundData(shiptoSalesArea, locale, caller, deliverySettingSalesAreaMap.getMaterialSelectionInitializer().getSapMaterialIds());
		 deliverySettingSalesAreaMap.setMaterialAvailabilityBackgroundData(materialAvailability);

		 deliverySettingSalesAreaMap.setDeliveryTypesByIncotermMap(loadActiveIncotermDeliveryTypeMap(shiptoSalesArea.getSarea().getSareaId()));
		 deliverySettingSalesAreaMap.setShiptoSettings(shiptoMap);

		 shiptoMap.getSareaMaps().put(sareaId, deliverySettingSalesAreaMap);
		 return shiptoMap;
	}
	
	/**
	 * Initializes a parent {@link DeliverySettingShiptoMap}. For this parent structure one single {@link DeliverySettingSalesAreaMap} object will be
	 * created - one belonging to the specified shiptoSalesArea parameter. The structure will be initialized only for the specified {@link SapMaterialEntity}
	 * or if null is specified all available sap materials.
	 */
	
	public DeliverySettingShiptoMap initDeliverySettingShiptoMapForLDS (SapBpEntity soldTo, Locale locale,  ShiptoSareaEntity shiptoSalesArea, Long sapMaterialId) {
		CallerContext caller = CallerContext.LDS;
		
		final ShiptoEntity shipTo = shiptoSalesArea.getShipto();
		DeliverySettingShiptoMap shiptoMap = initDeliverySettingShiptoMap(
				shipTo, soldTo, locale, caller);
		
		final Long sareaId = shiptoSalesArea.getSarea().getSareaId();
		DeliverySettingSalesAreaMap deliverySettingSalesAreaMap = new DeliverySettingSalesAreaMap(shiptoSalesArea);
		
		deliverySettingSalesAreaMap.setFinancialbackgroundData(initFinancialBackgroundData(shiptoSalesArea, soldTo));
		final Set<Long> sapMaterialIds;
		if (sapMaterialId != null) {
			sapMaterialIds = new HashSet<Long>();
			sapMaterialIds.add(sapMaterialId);
			
		}
		else {			
			sapMaterialIds = null;
		}
		
		final Set<Long> validContractIds = loadValidContracts(sareaId, shipTo.getShiptoSapBp().getSapBpId(), soldTo.getSapBpId(), FILTER_OUT_FUTURE_CONTRACTS_ON);
		
		final List <ShiptoSareaRestrEntity> restrictions = fetchShiptoRestrList(shiptoSalesArea.getShiptoSareaId(), validContractIds, sapMaterialIds, INCLUDE_ALL);
		
		//TODO (emoke) it seems that the call from getMaterialsForShipto seems to be interested only in this step here, anything initialized onwards the requesthandler ignores, and does not use for anything
		final MaterialSelectionInitializer initMaterialSelectionForShipto = initMaterialSelectionForShipto(soldTo, shiptoSalesArea, sapMaterialIds, deliverySettingSalesAreaMap,
				REMOVE_CONTRACT_ITEMS_WITH_ZERO_QUANTITY_OFF,
				ALLOW_ONLY_GROUPLEADER_FOR_QUANTITY_CONTROLLED_CONTRACTS_OFF,
				KEEP_BASIC_COMBINATION_IF_RESTRICTION_EXISTS_OFF,
				KEEP_INVALID_RESTRICTIONS_OFF,
				validContractIds,
				restrictions,
				locale);
		deliverySettingSalesAreaMap.setMaterialSelectionInitializer(initMaterialSelectionForShipto);
		
		final MaterialAvailabilityBackgroundData materialAvailability = initMaterialAvailabilityBackgroundData(shiptoSalesArea, locale, caller, deliverySettingSalesAreaMap.getMaterialSelectionInitializer().getSapMaterialIds());
		deliverySettingSalesAreaMap.setMaterialAvailabilityBackgroundData(materialAvailability);
		
		deliverySettingSalesAreaMap.setDeliveryTypesByIncotermMap(loadActiveIncotermDeliveryTypeMap(shiptoSalesArea.getSarea().getSareaId()));
		
		deliverySettingSalesAreaMap.setShiptoSettings(shiptoMap);
		
		shiptoMap.getSareaMaps().put(sareaId, deliverySettingSalesAreaMap);
		
		return shiptoMap;	
	}
	
	
	/**
	 * Initializes a parent {@link DeliverySettingShiptoMap}. For this parent structure one single {@link DeliverySettingSalesAreaMap} object will be
	 * created - one belonging to the specified shiptoSalesArea parameter. The structure will be initialized only for the specified {@link SapMaterialEntity}
	 */	
	public DeliverySettingShiptoMap initDeliverySettingShiptoMapForPlanningMaterialUpdate (SapBpEntity soldTo, Locale locale, ShiptoSareaEntity shiptoSalesArea, Long sapMaterialId) {
		if (sapMaterialId == null) {
			throw new IllegalArgumentException("Sap material has to be specified.");
		}
		Set<Long> sapMaterialIds = new HashSet<Long>();
		sapMaterialIds.add(sapMaterialId);
		//TODO (emoke) the caller seems not to be interested in everyhthing loaded here, check and do not call those parts which are not needed
		return initDeliverySettingShiptoMapForDeliverySettingValidation(soldTo,locale, shiptoSalesArea, sapMaterialIds);
	}
	
	@AdnTrace
	public DeliverySettingShiptoMap initDeliverySettingShiptoMapForDeliverySettingValidation (SapBpEntity soldTo, Locale locale, ShiptoSareaEntity shiptoSalesArea, Set<Long> sapMaterialIds) {
		return initDeliverySettingShiptoMapForDeliverySettingValidation (soldTo, locale, shiptoSalesArea, sapMaterialIds, true);
	}
	
	/**
	 * Initializes a parent {@link DeliverySettingShiptoMap}. For this parent structure one single {@link DeliverySettingSalesAreaMap} object will be
	 * created - one belonging to the specified shiptoSalesArea parameter. The structure will be initialized only for the specified {@link SapMaterialEntity} objects.
	 */
	@AdnTrace
	public DeliverySettingShiptoMap initDeliverySettingShiptoMapForDeliverySettingValidation (SapBpEntity soldTo, Locale locale, ShiptoSareaEntity shiptoSalesArea, Set<Long> sapMaterialIds, boolean filterFutureContracts) {
		CallerContext caller = CallerContext.ORDER_SAVE;
		if (sapMaterialIds == null || sapMaterialIds.isEmpty()) {
			throw new IllegalArgumentException("Sap material ids has to be specified.");
		}
		final ShiptoEntity shipTo = shiptoSalesArea.getShipto();
		
		DeliverySettingShiptoMap shiptoMap = initDeliverySettingShiptoMap(
				shipTo, soldTo, locale, caller);
		
		final Long sareaId = shiptoSalesArea.getSarea().getSareaId();

		DeliverySettingSalesAreaMap deliverySettingSalesAreaMap = new DeliverySettingSalesAreaMap(shiptoSalesArea);

		deliverySettingSalesAreaMap.setFinancialbackgroundData(initFinancialBackgroundData(shiptoSalesArea, soldTo));

		final Set<Long> validContractIds = loadValidContracts(sareaId, shipTo.getShiptoSapBp().getSapBpId(), soldTo.getSapBpId(), filterFutureContracts);
		
		final MaterialSelectionInitializer initMaterialSelectionForShipto = initMaterialSelectionForShipto(soldTo, shiptoSalesArea, sapMaterialIds, deliverySettingSalesAreaMap,
				REMOVE_CONTRACT_ITEMS_WITH_ZERO_QUANTITY_ON,
				ALLOW_ONLY_GROUPLEADER_FOR_QUANTITY_CONTROLLED_CONTRACTS_ON,
				KEEP_BASIC_COMBINATION_IF_RESTRICTION_EXISTS_OFF,
				KEEP_INVALID_RESTRICTIONS_OFF,
				validContractIds,
				null,
				locale);
		deliverySettingSalesAreaMap.setMaterialSelectionInitializer(initMaterialSelectionForShipto);
		
		final MaterialAvailabilityBackgroundData materialAvailability = initMaterialAvailabilityBackgroundData(shiptoSalesArea, locale, caller, deliverySettingSalesAreaMap.getMaterialSelectionInitializer().getSapMaterialIds());
		deliverySettingSalesAreaMap.setMaterialAvailabilityBackgroundData(materialAvailability);

		deliverySettingSalesAreaMap.setDeliveryTypesByIncotermMap(loadActiveIncotermDeliveryTypeMap(shiptoSalesArea.getSarea().getSareaId()));
		
		deliverySettingSalesAreaMap.setShiptoSettings(shiptoMap);

		shiptoMap.getSareaMaps().put(sareaId, deliverySettingSalesAreaMap);
		
		return shiptoMap;
	}

	/**
	 * Initializes a one single {@link DeliverySettingSalesAreaMap}. The structure will be initialized only for the specified {@link SapMaterialEntity} objects, if received as parameter.
	 */
	@AdnTrace
	public DeliverySettingSalesAreaMap initDeliverySettingSalesAreaMapForOrderDetail(SapBpEntity soldtoSapBp, SapBpEntity shiptoSapBp, SareaEntity sarea, Locale locale, Set<Long> sapMaterialIds, SapContractEntity contract) {

		CallerContext caller = CallerContext.ORDER_DETAIL;
				
		final ShiptoSareaEntity shiptoSalesArea = shiptoSareaStore.findShiptoSareaWithActiveShipto(shiptoSapBp, sarea);
		final ShiptoEntity shipTo = shiptoSalesArea.getShipto();
		
		DeliverySettingShiptoMap shiptoMap = initDeliverySettingShiptoMap(
				shipTo, soldtoSapBp, locale, caller);
		
		final Long sareaId = shiptoSalesArea.getSarea().getSareaId();

		DeliverySettingSalesAreaMap deliverySettingSalesAreaMap = new DeliverySettingSalesAreaMap(shiptoSalesArea);

		deliverySettingSalesAreaMap.setFinancialbackgroundData(initFinancialBackgroundData(shiptoSalesArea, soldtoSapBp));
		
		Set<Long> validContractIds = new HashSet<Long>();

		if (contract != null) {
			//case the order has a contract -> load data for that specific contract, and for that contract only, also if the contract is not anymore valid
			//if the order has no contract only materials present on behalf of the sales area will be loaded -> set contractids shall be empty
			validContractIds.add(contract.getSapContractId());
		}
		
		
		final MaterialSelectionInitializer initMaterialSelectionForShipto = initMaterialSelectionForShipto(soldtoSapBp, shiptoSalesArea, sapMaterialIds, deliverySettingSalesAreaMap,
				REMOVE_CONTRACT_ITEMS_WITH_ZERO_QUANTITY_OFF,
				ALLOW_ONLY_GROUPLEADER_FOR_QUANTITY_CONTROLLED_CONTRACTS_ON,
				KEEP_BASIC_COMBINATION_IF_RESTRICTION_EXISTS_OFF,
				KEEP_INVALID_RESTRICTIONS_OFF,
				validContractIds,
				null,
				locale);
		
		deliverySettingSalesAreaMap.setMaterialSelectionInitializer(initMaterialSelectionForShipto);
		
		initLanguageDependentData(locale, deliverySettingSalesAreaMap,
				initMaterialSelectionForShipto);
		
		final MaterialAvailabilityBackgroundData materialAvailability = initMaterialAvailabilityBackgroundData(shiptoSalesArea, locale, caller, deliverySettingSalesAreaMap.getMaterialSelectionInitializer().getSapMaterialIds());
		deliverySettingSalesAreaMap.setMaterialAvailabilityBackgroundData(materialAvailability);

		deliverySettingSalesAreaMap.setDeliveryTypesByIncotermMap(loadActiveIncotermDeliveryTypeMap(shiptoSalesArea.getSarea().getSareaId()));
		
		deliverySettingSalesAreaMap.setShiptoSettings(shiptoMap);

		shiptoMap.getSareaMaps().put(sareaId, deliverySettingSalesAreaMap);
		
		return deliverySettingSalesAreaMap;
	}

	/**
	 * Initializes Sales area independent data for the specified parameter
	 * shipto and soldto combination.
	 * 
	 * @param shipTo
	 * @param soldTo
	 * @param locale
	 * @param caller
	 * @return
	 */
	@AdnTrace
	private DeliverySettingShiptoMap initDeliverySettingShiptoMap(
			final ShiptoEntity shipTo, SapBpEntity soldTo, Locale locale,
			CallerContext caller) {
		
		DeliverySettingShiptoMap shiptoMap = new DeliverySettingShiptoMap(caller);

		shiptoMap.setSoldTo(soldTo);
		shiptoMap.setShipto(shipTo);

		/**
		 * Dependent on the caller, but (currently) independent of delivery setting context. 
		 */
		final GlobalTransportBackgroundData globalTransportBackgroundData = initGlobalTransportBackgroundData(caller, locale);
		shiptoMap.setGlobalTransportData(globalTransportBackgroundData);
		
		List<TruckTypeWrapper> truckTypeWrappers = loadTruckTypeWrappers(shipTo, globalTransportBackgroundData);
		shiptoMap.setTruckTypeWrappers(truckTypeWrappers);
		
		List<WagonTypeWrapper> wagonTypeWrappers = loadWagonTypeWrappers(shipTo, globalTransportBackgroundData);
		shiptoMap.setWagonTypeWrappers(wagonTypeWrappers);
		
		List <SimpleVehicleTypeWrapper> shipTypeWrappers = loadShipTypeWrappers(globalTransportBackgroundData);
		shiptoMap.setShipTypeWrappers(shipTypeWrappers);
		
		final List<SimpleVehicleTypeWrapper> blockTrainTypeWrappers = loadBlockTrainTypeWrappers(globalTransportBackgroundData);
		shiptoMap.setBlockTrainTypeWrappers(blockTrainTypeWrappers);
		return shiptoMap;
		
	}

	/**
	 * Fetches a list of active service SapMaterialSarea entities for the given sales organization code and 
	 * distribution channel.
	 * 
	 * @param sorgCd - The sales organization code to filter for.
	 * @param distCh - The distribution channel to filter for.
	 * @param sapMaterialIds - if specified only load entries for the specified sapMaterialIds
	 * @return List of SapMaterialSarea entities.
	 */
	@AdnTrace
	private List<SapMaterialSareaInfoEntity> fetchServiceSapMatSarea(String sorgCd, String distCh, Set<Long> sapMaterialIds) {

		ReadOnlyDao<SapMaterialSareaInfoEntity> sapMaterialSareaInfoEntityDao = getDaoProvider().getSapMaterialSareaInfoEntityDao();
		final Filter<SapMaterialSareaInfoEntity> filter = sapMaterialSareaInfoEntityDao.createFilter();

		filter.addExpression(Restrictions.eq(SapMaterialSareaInfoEntity.ATTR_SORG_CD, sorgCd));
		filter.addExpression(Restrictions.eq(SapMaterialSareaInfoEntity.ATTR_DISTRIBUTION_CHANNEL, distCh));
		filter.addExpression(Restrictions.eq(SapMaterialSareaInfoEntity.ATTR_LOGON_PACKAGE_TYPE_CD, Constant.PACKAGE_TYPE_SERVICE));
		filter.addExpression(Restrictions.eq(SapMaterialSareaInfoEntity.ATTR_MARKED_FOR_DELETION, Constant.MARKED_FOR_DELETION_FALSE));
		
		if (sapMaterialIds != null && !sapMaterialIds.isEmpty()) {
			List<Long> sapMaterialIdList = new ArrayList<Long>(sapMaterialIds);
			filter.addExpression(DbUtils.createSplittedInRestriction(SapMaterialSareaInfoEntity.ATTR_SAP_MATERIAL_ID, sapMaterialIdList));
		}
		
		return sapMaterialSareaInfoEntityDao.find(filter);
	}

	/**
	 * Basically creating {@link TruckTypeWrapper} wrapper objects for the current valid 
	 * truck types.
	 * The idea behind is that even if the shipto did not enable the new truck types explicitly they should be
	 * considered as available with least priority. Those truck types which are though not anymore available 
	 * in the global cem truck types should not be considered anymore.
	 * 
	 * Might be possible to replace with view? But it was too heavily used on Dispokarte updates.
	 * 
	 */
	@AdnTrace
	private List <TruckTypeWrapper> loadTruckTypeWrappers (final ShiptoEntity shipto, final GlobalTransportBackgroundData globalTransportData) {
		final List<ShiptoTruckTypeEntity> shiptoTruckTypes = fetchTruckTypesByShiptoId(shipto.getShiptoId());
		final Map <Long, TruckTypeTxEntity> globalTypeMap = globalTransportData.getTruckTypeTxMap();

		final List <TruckTypeWrapper> truckTypeWrappers = new ArrayList<TruckTypeWrapper> ();

		Map <Long, ShiptoTruckTypeEntity> shiptoTypeMap = new HashMap<Long, ShiptoTruckTypeEntity>();
		for (ShiptoTruckTypeEntity shiptoTruckType : shiptoTruckTypes) {
			TruckTypeBsEntity truckType = shiptoTruckType.getTruckType();
			Long truckTypeCd = truckType.getTruckTypeCd();
			shiptoTypeMap.put(truckTypeCd, shiptoTruckType);
		}

		Set <Long> shiptoTypesPresentInGlobal = new HashSet<Long>();
		shiptoTypesPresentInGlobal.addAll(shiptoTypeMap.keySet());
		shiptoTypesPresentInGlobal.retainAll(globalTypeMap.keySet());

		for ( Map.Entry<Long, TruckTypeTxEntity> globalType : globalTypeMap.entrySet() ) {
			TruckTypeWrapper wrapper = null;

			final TruckTypeTxEntity truckTypeTxEntity = globalType.getValue();

			if ( shiptoTypesPresentInGlobal.contains(globalType.getKey())) {
				ShiptoTruckTypeEntity shiptoTruckType = shiptoTypeMap.get(globalType.getKey());
				final long priority = shiptoTruckType.getEvaluationPriority().longValue();

				wrapper = new TruckTypeWrapper(priority, truckTypeTxEntity, shiptoTruckType);
			}
			else {
				wrapper = new TruckTypeWrapper(new Long(-1), truckTypeTxEntity, null);
			}
			truckTypeWrappers.add(wrapper);
		}
		return truckTypeWrappers;
	}

	/**
	 * Basically creating {@link WagonTypeWrapper} wrapper objects for the current valid 
	 * truck types.
	 * Those wagon types which are not present in the db, should be considered as forbidden!
	 * @param shipto 
	 * @param 
	 * 
	 */
	@AdnTrace
	private List<WagonTypeWrapper> loadWagonTypeWrappers (final ShiptoEntity shipto, final GlobalTransportBackgroundData globalTransportData) {

		final List<ShiptoWagonTypeEntity> shiptoWagonTypeList = fetchWagonTypesByShiptoId(shipto.getShiptoId());
		final Map <Long, WagonTypeTxEntity> globalTypeMap = globalTransportData.getWagonTypeTxMap();

		final List <WagonTypeWrapper> wagonTypeWrapper = new ArrayList<WagonTypeWrapper> ();
		Map <Long, ShiptoWagonTypeEntity> shiptoTypeMap = new HashMap <Long, ShiptoWagonTypeEntity>();

		for (ShiptoWagonTypeEntity shiptoType : shiptoWagonTypeList) {
			WagonTypeBsEntity wagonType = shiptoType.getWagonType();
			Long wagonTypeCd = wagonType.getWagonTypeCd();
			shiptoTypeMap.put(wagonTypeCd, shiptoType);
		}

		Set <Long> shiptoTypesPresentInGlobal = new HashSet<Long>();
		shiptoTypesPresentInGlobal.addAll(shiptoTypeMap.keySet());
		shiptoTypesPresentInGlobal.retainAll(globalTypeMap.keySet());

		for ( Map.Entry<Long, WagonTypeTxEntity> globalType : globalTypeMap.entrySet() ) {
			WagonTypeWrapper wrapper = null;

			if ( shiptoTypesPresentInGlobal.contains(globalType.getKey())) {
				ShiptoWagonTypeEntity shiptoWagonType = shiptoTypeMap.get(globalType.getKey());

				final long priority = shiptoWagonType.getEvaluationPriority().longValue();
				final WagonTypeTxEntity txEnt = globalType.getValue();

				wrapper = new WagonTypeWrapper(priority, txEnt, shiptoWagonType);
			}
			else {
				//if a wagon type is forbidden, it is not present in the db --> add it with 0 priority
				final WagonTypeTxEntity txEnt = globalType.getValue();
				wrapper = new WagonTypeWrapper(new Long(0), txEnt, null);
			}
			wagonTypeWrapper.add(wrapper);
		}
		return wagonTypeWrapper;
	}

	private List <SimpleVehicleTypeWrapper> loadShipTypeWrappers (final GlobalTransportBackgroundData globalTransportData) {
		List<SimpleVehicleTypeWrapper> simpleVehicleTypeWrappers = new ArrayList<SimpleVehicleTypeWrapper>();

		final GlobalTransportBackgroundData globalTypeMap = globalTransportData;
		for (SimpleVehicleTypeTxEntity shipTx : globalTypeMap.getShipTypeTxMap().values()) {
			final SimpleVehicleTypeWrapper wrapper = new SimpleVehicleTypeWrapper(shipTx);
			simpleVehicleTypeWrappers.add(wrapper);
		}

		return simpleVehicleTypeWrappers;
	}

	@AdnTrace
	private List <SimpleVehicleTypeWrapper> loadBlockTrainTypeWrappers (final GlobalTransportBackgroundData globalTransportData) {

		List<SimpleVehicleTypeWrapper>  result = new ArrayList<SimpleVehicleTypeWrapper>();

		final GlobalTransportBackgroundData globalTypeMap = globalTransportData;
		for (SimpleVehicleTypeTxEntity blockTrainTx : globalTypeMap.getBlockTrainTypeTxMap().values()) {
			final SimpleVehicleTypeWrapper wrapper = new SimpleVehicleTypeWrapper(blockTrainTx);
			result.add(wrapper);
		}

		return result;
	}

	/**
	 * Takes a collection of delivery settings and
	 * removes the ones with invalid auto-payer settings.
	 * @return 
	 */
	@AdnTrace
	public Collection<DeliverySettingDTO> removeInvalidDeliverySettings(Collection<DeliverySettingDTO> allSettings) {
		List<DeliverySettingDTO> invalidDeliverySettings = new ArrayList<DeliverySettingDTO>();
		for(DeliverySettingDTO setting : allSettings) {
			if (!setting.isAutoPayerSettingValid()) {
				invalidDeliverySettings.add(setting);
			}
		}
		allSettings.removeAll(invalidDeliverySettings);
		return allSettings;
	}

	/**
	 * Returns the pool for the specified poolId, authorization is not applied.
	 */
	public PoolEntity findPoolById(Long poolId){
		Authorizer.disableNextAccessRestriction();
		final ReadWriteDao<PoolEntity> poolEntityDao = getDaoProvider().getPoolEntityDao();
		return poolEntityDao.findById(poolId);
	}
}