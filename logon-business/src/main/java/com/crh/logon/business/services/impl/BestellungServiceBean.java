/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;


import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;
import javax.persistence.Query;

import ch.nevis.gdi.jpa.BaseEntity;
import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Expression;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.JPAQuery;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.gdi.jpa.filter.SelectExpression;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.beans.OrderSearchInformations;
import com.crh.logon.business.ordercreation.DeliveryOptionConfigurationDTO;
import com.crh.logon.business.ordercreation.DeliverySettingFilterLogic;
import com.crh.logon.business.ordercreation.DeliverySettingSalesAreaMap;
import com.crh.logon.business.ordercreation.dto.AutoaddMaterialDTO;
import com.crh.logon.business.ordercreation.dto.AutoaddMaterialFilterDTO;
import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.business.ordercreation.dto.LoadingPointDTO;
import com.crh.logon.business.ordercreation.dto.PlantDTO;
import com.crh.logon.business.ordercreation.dto.ShippingpointDTO;
import com.crh.logon.business.ordercreation.dto.SupplyPlantDTO;
import com.crh.logon.business.services.beans.GeneralDisplayNameDto;
import com.crh.logon.business.services.beans.SelectableGeneralDisplayNameDto;
import com.crh.logon.business.services.dispatchers.BestellungServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.BestellungServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.ConfigServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DeliverySettingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.MaterialEnhancementsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TimeZoneServiceLocalInterface;
import com.crh.logon.business.services.utils.DateTimeAwareCustomerOrderWrapper;
import com.crh.logon.business.services.utils.DateTimeAwareOrderWrapper;
import com.crh.logon.business.services.utils.DbUtils;
import com.crh.logon.business.services.utils.DriverBreaksUtils;
import com.crh.logon.business.services.utils.OrderSearchBean;
import com.crh.logon.business.services.utils.OrderSearchListRange;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.business.stores.domain.ShippingPointStoreInterface;
import com.crh.logon.business.stores.sap.ContractItemStoreInterface;
import com.crh.logon.business.stores.sap.ShiptoSareaStoreInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.authorization.Authorizer;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.MatPlantEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import com.crh.logon.persistence.entities.refdata.DistributionDestBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusTxEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusTxPK;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapBpSareaEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.BimodalRailOrderEntity;
import com.crh.logon.persistence.entities.views.BizeventAuditDisplayEntity;
import com.crh.logon.persistence.entities.views.ContainerMonitoringEntity;
import com.crh.logon.persistence.entities.views.CustordOnetimeDataEntity;
import com.crh.logon.persistence.entities.views.CustorderCreateEntity;
import com.crh.logon.persistence.entities.views.CustorderFinishedEntity;
import com.crh.logon.persistence.entities.views.DispoDriverBreakListEntity;
import com.crh.logon.persistence.entities.views.EditbpSoldshipSareaEntity;
import com.crh.logon.persistence.entities.views.OrdUnloadSchedInfoEntity;
import com.crh.logon.persistence.entities.views.OrderCreateEntity;
import com.crh.logon.persistence.entities.views.OrderFinishedEntity;
import com.crh.logon.persistence.entities.views.OrderMonitoringEntity;
import com.crh.logon.persistence.entities.views.OrderSearchEntity;
import com.crh.logon.persistence.entities.views.ProdPlantNameEntity;
import com.crh.logon.persistence.entities.views.SapMatSareaContractEntity;
import com.crh.logon.persistence.entities.views.SapMaterialDescEntity;
import com.crh.logon.persistence.entities.views.SdoOnetimeDataEntity;
import com.crh.logon.persistence.entities.views.ShiptoSareaDetailEntity;
import com.crh.logon.persistence.entities.views.ShiptoSoldtoLnfDataEntity;
import com.crh.logon.persistence.entities.views.SoldshipBestFlexSrchEntity;
import com.crh.logon.persistence.entities.views.SoldshipSareaFlexSrchEntity;
import com.crh.logon.persistence.entities.views.VehicleEntity;
import com.crh.logon.persistence.entities.views.WeborderShipToSoldToEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.time.DateTimeUtils;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@AdnTrace
@Interceptors(UserInfoInterceptor.class)
public class BestellungServiceBean extends BestellungServiceDispatcher implements BestellungServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(BestellungServiceBean.class);

	private static final int COUNT_OF_DELIVERY_TYPES = 12;
	private static final long COUNT_OF_ORDER_STATUS = 19;
	/**
	 * 1 - 13, and 18
	 */
	private static final long COUNT_OF_ORDER_STATUS_FOR_DELIVERY_MONITORING = 15;
	private static final String COMMERCIAL_OR_EXECUTING_CARRIER_ID_FILTER_KEY = "carrierId";

	@EJB private ConfigServiceLocalInterface configService;

	@EJB
	private MaterialEnhancementsServiceLocalInterface materialEnhancementsService;

	@EJB
	private DispoServiceLocalInterface dispoService;

	@EJB
	private RefDataGenericsServiceLocalInterface referentialDataService;
	@EJB private TimeZoneServiceLocalInterface timeZoneService;
	@EJB private DeliverySettingServiceLocalInterface deliverySettingService;

	@EJB private ShippingPointStoreInterface shippingpointStore;
	@EJB private ContractItemStoreInterface sapContractItemStore;

	@EJB
	ShiptoSareaStoreInterface shiptoSareaStore;


	public List<OrderCreateEntity> fetchOrders(Long shipToSapBpId, Locale locale) {
		final ReadOnlyDao<OrderCreateEntity> orderDao = getDaoProvider().getOrderCreateEntityDao();
		final Filter<OrderCreateEntity> filter = orderDao.createFilter();

		filter.addExpression(Restrictions.eq(OrderCreateEntity.ATTR_LANG_CD, locale.getLanguage()));
		filter.addExpression(Restrictions.eq(OrderCreateEntity.ATTR_SHIPTO_SAP_BP_ID, shipToSapBpId));
		filter.addExpression(Restrictions.isNotNull(OrderCreateEntity.ATTR_BILLTO_SAP_BP_ID));
		filter.addExpression(Order.desc(OrderCreateEntity.ATTR_ORDER_ID));

		return orderDao.find(filter);
	}

	public List<ShiptoSoldtoLnfDataEntity> fetchLatestLnfAddresses(Long shipToSapBpId,
			Long soldtoSapBpId) {
		final ReadOnlyDao<ShiptoSoldtoLnfDataEntity> orderDao = getDaoProvider().getShiptoSoldtoLnfDataEntityDao();
		final Filter<ShiptoSoldtoLnfDataEntity> filter = orderDao.createFilter();

		filter.addExpression(Restrictions.eq(ShiptoSoldtoLnfDataEntity.ATTR_SHIPTO_SAP_BP_ID,
				shipToSapBpId));
		filter.addExpression(Restrictions.eq(ShiptoSoldtoLnfDataEntity.ATTR_SOLDTO_SAP_BP_ID,
				soldtoSapBpId));

		return orderDao.findNRecs(0, getConfigBean().getAppDefaultOrderNumOfLnfHistory(), filter);
	}


	public List<OrderStatusTxEntity> fetchOrderStatusTxList(Locale locale) {
		return referentialDataService.findTxEntityList(OrderStatusTxEntity.class, locale);
	}

	/**
	 * Returns a list of {@code SelectableGeneralDisplayNameDto} objects where each entry is mapped to one
	 * existing {@code OrderStatusTxEntity}. The 'name' and 'id' attribute is set based on the Tx entity.
	 * @param locale - used for I18N Tx name lookup
	 * @param preSelect - 'selected' attribute will be set based on this for all the returned objects
	 * @return created list
	 */
	public List<SelectableGeneralDisplayNameDto> fetchOrderStatusDisplayNameDtosForLocale(Locale locale, boolean preSelect){
		ArrayList<SelectableGeneralDisplayNameDto> result = new ArrayList<SelectableGeneralDisplayNameDto>();

		List<OrderStatusTxEntity> findTxEntityList = referentialDataService.findTxEntityList(
				OrderStatusTxEntity.class, locale);
		for (OrderStatusTxEntity orderStatusTx : findTxEntityList) {
			OrderStatusTxPK orderStatusTxPK = orderStatusTx.getOrderStatusTxPK();
			OrderStatusBsEntity orderStatusBs = orderStatusTxPK.getOrderStatusBs();
			Long orderStatusCd = orderStatusBs.getOrderStatusCd();
			String txt = orderStatusTx.getTxt();
			result.add(new SelectableGeneralDisplayNameDto(orderStatusCd, txt, preSelect));
		}

		return result;
	}

	public List<OrderCreateEntity> fetchOpenedOrders(boolean webOrder, Long shipToSapBpId,
			Locale locale) {
		final ReadOnlyDao<OrderCreateEntity> orderDao = getDaoProvider().getOrderCreateEntityDao();
		final Filter<OrderCreateEntity> filter = orderDao.createFilter();

		filter.addExpression(Restrictions.eq(OrderCreateEntity.ATTR_LANG_CD, locale.getLanguage()));
		filter.addExpression(Restrictions.eq(OrderCreateEntity.ATTR_SHIPTO_SAP_BP_ID, shipToSapBpId));
		filter.addExpression(Order.desc(OrderCreateEntity.ATTR_ORDER_ID));
		filter.addExpression(Order.asc(OrderCreateEntity.ATTR_ORDER_ITEM_NO));

		final List<OrderCreateEntity> orders;
		int maxRecs = webOrder ? getConfigBean().getAppDefaultOrderNumOfDisplayedComplWebOrders()
				: getConfigBean().getAppDefaultOrderNumOfDisplayedComplOrders();
		orders = orderDao.findNRecs(0, maxRecs, filter);

		return orders;
	}

	public List<OrderFinishedEntity> fetchFinishedOrders(boolean webOrder, Long shipToSapBpId,
			Locale locale) {
		final ReadOnlyDao<OrderFinishedEntity> orderDao = getDaoProvider().getOrderFinishedEntityDao();
		final Filter<OrderFinishedEntity> filter = orderDao.createFilter();

		filter.addExpression(Restrictions.eq(OrderFinishedEntity.ATTR_LANG_CD, locale.getLanguage()));
		filter.addExpression(Restrictions.eq(OrderFinishedEntity.ATTR_SHIPTO_SAP_BP_ID,
				shipToSapBpId));
		filter.addExpression(Order.desc(OrderFinishedEntity.ATTR_ORDER_ID));
		filter.addExpression(Order.asc(OrderFinishedEntity.ATTR_ORDER_ITEM_NO));

		final List<OrderFinishedEntity> orders;
		int maxRecs = webOrder ? getConfigBean().getAppDefaultOrderNumOfDisplayedComplWebOrders()
				: getConfigBean().getAppDefaultOrderNumOfDisplayedComplOrders();
		orders = orderDao.findNRecs(0, maxRecs, filter);

		return orders;
	}

	public List<DateTimeAwareOrderWrapper> fetchDateTimeAwareOrdersByStatus(
			boolean webOrder,
			ShiptoEntity shipTo,
			boolean isCompletedOrder,
			Locale locale,
			TimeZone userTz) {
		TimeZone shipToTz = timeZoneService.loadForShipTo(shipTo, locale);
		ArrayList<DateTimeAwareOrderWrapper> result = new ArrayList<DateTimeAwareOrderWrapper>();

		if (isCompletedOrder) {
			List<OrderFinishedEntity> orders = fetchFinishedOrders(webOrder,
					shipTo.getShiptoSapBp().getSapBpId(), locale);
			for (OrderFinishedEntity entity : orders) {
				DateTimeAwareOrderWrapper bean = new DateTimeAwareOrderWrapper(entity, shipToTz,
						userTz);
				result.add(bean);
			}
		}
		else {
			List<OrderCreateEntity> orders = fetchOpenedOrders(webOrder,
					shipTo.getShiptoSapBp().getSapBpId(), locale);
			for (OrderCreateEntity entity : orders) {
				DateTimeAwareOrderWrapper bean = new DateTimeAwareOrderWrapper(entity, shipToTz,
						userTz);
				result.add(bean);
			}
		}
		return result;
	}

	public List<CustorderCreateEntity> fetchOpenedCustorders(boolean webOrder, Long shipToSapBpId, Locale locale) {
		final ReadOnlyDao<CustorderCreateEntity> custorderDao = getDaoProvider().getCustorderCreateEntityDao();
		final Filter<CustorderCreateEntity> filter = custorderDao.createFilter();

		final String langCd = locale.getLanguage();

		filter.addExpression(Restrictions.eq(CustorderCreateEntity.ATTR_LANG_CD, langCd));
		filter.addExpression(Restrictions.eq(CustorderCreateEntity.ATTR_SHIPTO_SAP_BP_ID,
				shipToSapBpId));
		filter.addExpression(Order.desc(CustorderCreateEntity.ATTR_CUSTORDER_NUMBER));
		filter.addExpression(Order.asc(CustorderCreateEntity.ATTR_CUSTORDER_ITEM_NO));

		final List<CustorderCreateEntity> custorders;

		int maxRecs = (webOrder ? getConfigBean().getAppDefaultOrderNumOfDisplayedComplWebCustorders()
				: getConfigBean().getAppDefaultOrderNumOfDisplayedComplCustorders());

		custorders = custorderDao.findNRecs(0, maxRecs, filter);

		return custorders;
	}

	public List<CustorderFinishedEntity> fetchFinishedCustorders(boolean webOrder, Long shipToSapBpId, Locale locale) {
		final ReadOnlyDao<CustorderFinishedEntity> custorderDao = getDaoProvider().getCustorderFinishedEntityDao();
		final Filter<CustorderFinishedEntity> filter = custorderDao.createFilter();

		final String langCd = locale.getLanguage();

		filter.addExpression(Restrictions.eq(CustorderFinishedEntity.ATTR_LANG_CD, langCd));
		filter.addExpression(Restrictions.eq(CustorderFinishedEntity.ATTR_SHIPTO_SAP_BP_ID,
				shipToSapBpId));
		filter.addExpression(Order.desc(CustorderCreateEntity.ATTR_CUSTORDER_NUMBER));
		filter.addExpression(Order.asc(CustorderCreateEntity.ATTR_CUSTORDER_ITEM_NO));

		final List<CustorderFinishedEntity> custorders;


		int maxRecs = (webOrder
					? getConfigBean().getAppDefaultOrderNumOfDisplayedComplWebCustorders()
					: getConfigBean().getAppDefaultOrderNumOfDisplayedComplCustorders());

		custorders = custorderDao.findNRecs(0, maxRecs, filter);

		return custorders;
	}

	public List<DateTimeAwareCustomerOrderWrapper> fetchDateTimeAwareCustordersByStatus(
			boolean webOrder,
			ShiptoEntity shipTo,
			boolean isClosedCustorder,
			Locale locale,
			TimeZone userTz) {
		TimeZone shipToTz = timeZoneService.loadForShipTo(shipTo, locale);
		ArrayList<DateTimeAwareCustomerOrderWrapper> result = new ArrayList<DateTimeAwareCustomerOrderWrapper>();

		if(isClosedCustorder) {
			List<CustorderFinishedEntity> custorders = fetchFinishedCustorders(
					webOrder,
					shipTo.getShiptoSapBp().getSapBpId(),
					locale);

			for (CustorderFinishedEntity entity: custorders) {
				DateTimeAwareCustomerOrderWrapper bean =
						new DateTimeAwareCustomerOrderWrapper(entity, shipToTz, userTz);
				result.add(bean);
			}

		}
		else {
			List<CustorderCreateEntity> custorders = fetchOpenedCustorders(
					webOrder,
					shipTo.getShiptoSapBp().getSapBpId(),
					locale);

			for (CustorderCreateEntity entity: custorders) {
				DateTimeAwareCustomerOrderWrapper bean =
						new DateTimeAwareCustomerOrderWrapper(entity, shipToTz, userTz);
				result.add(bean);
			}
		}
		return result;
	}

	public List<DateTimeAwareOrderWrapper> fetchDateTimeAwareOrdersByCustorderAndStatus(
			boolean webOrder,
			ShiptoEntity shipTo,
			Long custorderId,
			boolean isClosedOrder,
			Locale locale,
			TimeZone userTz) {
		TimeZone shipToTz = timeZoneService.loadForShipTo(shipTo, locale);

		List<DateTimeAwareOrderWrapper> result = new ArrayList<DateTimeAwareOrderWrapper>();

		if (isClosedOrder) {
			List<OrderFinishedEntity> orders = fetchFinishedOrdersByCustorder(webOrder,
					shipTo.getShiptoSapBp().getSapBpId(), custorderId, locale);

			for (OrderFinishedEntity entity : orders) {
				DateTimeAwareOrderWrapper bean = new DateTimeAwareOrderWrapper(entity, shipToTz,
						userTz);
				result.add(bean);
			}
		}
		else {
			List<OrderCreateEntity> orders = fetchOpenedOrdersByCustorder(
					shipTo.getShiptoSapBp().getSapBpId(), custorderId, locale);

			for (OrderCreateEntity entity : orders) {
				DateTimeAwareOrderWrapper bean = new DateTimeAwareOrderWrapper(entity, shipToTz,
						userTz);
				result.add(bean);
			}
		}

		return result;
	}

	List<OrderCreateEntity> fetchOpenedOrdersByCustorder(Long shiptoSapBpId, Long custorderId, Locale locale) {
		final ReadOnlyDao<OrderCreateEntity> orderDao = getDaoProvider().getOrderCreateEntityDao();
		final Filter<OrderCreateEntity> filter = orderDao.createFilter();
		final String langCd = locale.getLanguage();

		filter.addExpression(Restrictions.eq(OrderCreateEntity.ATTR_LANG_CD, langCd));
		filter.addExpression(Restrictions.eq(OrderCreateEntity.ATTR_SHIPTO_SAP_BP_ID, shiptoSapBpId));
		filter.addExpression(Restrictions.eq(OrderCreateEntity.ATTR_CUSTORDER_ID, custorderId));
		filter.addExpression(Order.desc(OrderCreateEntity.ATTR_ORDER_ID));
		filter.addExpression(Order.asc(OrderCreateEntity.ATTR_ORDER_ITEM_NO));

		List<OrderCreateEntity> orders;

		orders = orderDao.find(filter);

		return orders;
	}

	List<OrderFinishedEntity> fetchFinishedOrdersByCustorder(boolean webOrder, Long shiptoSapBpId, Long custorderId,
			Locale locale) {
		final ReadOnlyDao<OrderFinishedEntity> orderDao = getDaoProvider().getOrderFinishedEntityDao();
		final Filter<OrderFinishedEntity> filter = orderDao.createFilter();
		final String langCd = locale.getLanguage();

		filter.addExpression(Restrictions.eq(OrderFinishedEntity.ATTR_LANG_CD, langCd));
		filter.addExpression(Restrictions.eq(OrderFinishedEntity.ATTR_SHIPTO_SAP_BP_ID,
				shiptoSapBpId));
		filter.addExpression(Restrictions.eq(OrderFinishedEntity.ATTR_CUSTORDER_ID, custorderId));
		filter.addExpression(Order.desc(OrderFinishedEntity.ATTR_ORDER_ID));
		filter.addExpression(Order.asc(OrderFinishedEntity.ATTR_ORDER_ITEM_NO));

		List<OrderFinishedEntity> orders;

		int maxRecs = (webOrder ? getConfigBean().getAppDefaultOrderNumOfDisplayedComplWebOrders()
				: getConfigBean().getAppDefaultOrderNumOfDisplayedComplOrders());
		orders = orderDao.findNRecs(0, maxRecs, filter);

		return orders;
	}

	/**
	 * Loads the shipto sales area by all means. (Not considering sold-to or  partner functions
	 * between them.)
	 */
	public ShiptoSareaEntity getShiptoSarea(Long shiptoId, Long sareaId) {
		return shiptoSareaStore.getShiptoSarea(shiptoId, sareaId);
	}

	public List<ShiptoSareaDetailEntity> getShiptoSareaRestrprdDetailedList(Long shiptoSareaId, Locale locale) {
		final ReadOnlyDao<ShiptoSareaDetailEntity> shiptoSareaDetailDao = getDaoProvider().getShiptoSareaDetailEntityDao();
		final Filter<ShiptoSareaDetailEntity> filter = shiptoSareaDetailDao.createFilter();

		filter.addExpression(Restrictions.eq(ShiptoSareaDetailEntity.ATTR_SHIPTO_SAREA_ID,
				shiptoSareaId));
		filter.addExpression(Restrictions.eq(ShiptoSareaDetailEntity.ATTR_ENABLED_FLAG,
				Boolean.TRUE));
		filter.addExpression(Restrictions.eq(
				ShiptoSareaDetailEntity.ATTR_AVAILABLE_IN_CUST_WEB_FLAG, Boolean.TRUE));
		filter.addExpression(Restrictions.or(
				Restrictions.eq(ShiptoSareaDetailEntity.ATTR_LANG_CD, locale.getLanguage()),
				Restrictions.isNull(ShiptoSareaDetailEntity.ATTR_SAP_MATERIAL_ID)) // Null == Wildcard (*)
		);

		Authorizer.disableNextAccessRestriction();

		return shiptoSareaDetailDao.find(filter);
	}

	public List<ShiptoSareaDetailEntity> getShiptoSareaRestrprdDetailedList(List<Long> restrictionIds) {
		if (restrictionIds == null || restrictionIds.isEmpty()) {
			return new ArrayList<ShiptoSareaDetailEntity>();
		}

		final ReadOnlyDao<ShiptoSareaDetailEntity> shipToSalesAreaDetailDao = getDaoProvider().getShiptoSareaDetailEntityDao();
		final Filter<ShiptoSareaDetailEntity> filter = shipToSalesAreaDetailDao.createFilter();

		filter.addExpression(Restrictions.in(ShiptoSareaDetailEntity.ATTR_SHIPTO_SAREA_RESTR_ID,
				restrictionIds));

		return shipToSalesAreaDetailDao.find(filter);
	}

	public ShiptoSareaRestrEntity getShiptoSareaRestrprd(Long sareaId, Long sapMatId, Long shiptoSapBpId) {
		final ReadOnlyDao<ShiptoSareaRestrEntity> dao = getDaoProvider().getShiptoSareaRestrEntityDao();
		final Filter<ShiptoSareaRestrEntity> f = dao.createFilter();

		f.addExpression(Restrictions.eq(ShiptoSareaRestrEntity.ATTR_SAP_MATERIAL_ID, sapMatId));
		f.addExpression(Restrictions.eq(ShiptoSareaRestrEntity.ATTR_SHIPTO_SAREA + '.'
				+ ShiptoSareaEntity.ATTR_SHIPTO_ID, shiptoSapBpId));
		f.addExpression(Restrictions.eq(ShiptoSareaRestrEntity.ATTR_SHIPTO_SAREA + '.'
				+ ShiptoSareaEntity.ATTR_SAREA_ID, sareaId));
		Authorizer.disableNextAccessRestriction();

		return dao.findSingle(f);
	}

	public LoadingpointEntity getLoadingPointById(Long lpId) {
			final ReadOnlyDao<LoadingpointEntity> lpDao = getDaoProvider().getLoadingpointEntityDao();
		return lpDao.findById(lpId);
	}

	public SapMaterialEntity getSapMaterialById(Long matId) {
		final ReadOnlyDao<SapMaterialEntity> smDao = getDaoProvider().getSapMaterialEntityDao();
		final SapMaterialEntity sapMaterial = smDao.findById(matId);
		sapMaterial.initialize();
		return sapMaterial;
	}

	public List<SapMaterialEntity> getSapMaterialsById(List<Long> materialIds) {
		if (materialIds == null || materialIds.isEmpty()) {
			return Collections.emptyList();
		}
		ReadOnlyDao<SapMaterialEntity> sapMaterialDao = getDaoProvider().getSapMaterialEntityDao();
		Filter<SapMaterialEntity> filter = sapMaterialDao.createFilter();
		filter.addExpression(DbUtils.createSplittedInRestrictionOfIds(
				SapMaterialEntity.ATTR_SAP_MATERIAL_ID, materialIds));

		List<SapMaterialEntity> sapMaterialList = sapMaterialDao.find(filter);
		List<SapMaterialEntity> correctOrderSapMaterialList = new ArrayList<SapMaterialEntity>();

		for (Long materialId : materialIds) {
			for (SapMaterialEntity sapMaterial : sapMaterialList) {
				if (materialId.equals(sapMaterial.getSapMaterialId())) {
					correctOrderSapMaterialList.add(sapMaterial);
					break;
				}
			}
		}

		return correctOrderSapMaterialList;
	}


	/**
	 * Returns the list of Shipping Points where a given product is available
	 * Used by OrderAjaxService
	 *
	 * @param sapMatId
	 * @return
	 */
	public List<ShippingpointEntity> getShippingPointListByPlantCd(String plantCode, Locale locale) {
		final ReadOnlyDao<ShippingpointEntity> spDao = getDaoProvider().getShippingpointEntityDao();
		final Filter<ShippingpointEntity> filter = spDao.createFilter();

		if (plantCode != null) {
			filter.addExpression(Restrictions.eq(ShippingpointEntity.ATTR_PLANT_CD, plantCode));
		}
		filter.addExpression(Order.asc(ShippingpointEntity.ATTR_SHIPPINGPOINT_NAME));

		return spDao.find(filter);
	}

	/**
	 * deliverySetting is expected to be attached to a planning material, which must have a contract item.
	 * It finds the contract items (and so the materials) which can replace the planning material in a later
	 * phase, and collects the delivery plant (=supply plant), shipping point, loading point of the potential
	 * replacing materials. If plant is already given, only shipping points within that will be found. If
	 * shipping point is given, only loading points within that will be found.
	 *
	 * @param sapContractItem of the planning material
	 * @param shiptoSarea in which the new order is being created
	 * @param sareaMap sales area map, which is given when creating a new order
	 * @param deliveryPlantCd delivery plant (supply plant) code (comes from the gui set by the user)
	 *						- not considered if shipping point is given
	 * @param shippingPointId id of the shipping point (comes from the gui set by the user)
	 *
	 * @return a Map lists:
	 *  supplyPlantList: {@link SupplyPlantDTO};
	 *  shippingpointList: {@link ShippingpointDTO};
	 *  loadingpointList: {@link LoadingPointDTO};
	 *  prodPlantList: {@link PlantDTO};
	 */
	public Map<String, Object> calcDeliveryPlantDetails(SapContractItemEntity sapContractItem, ShiptoSareaEntity shiptoSarea,
			DeliverySettingSalesAreaMap sareaMap, String deliveryPlantCd, Long shippingPointId, TimeZone timeZone) {
		if (sapContractItem == null) {
			throw new LogonException("no contract mustn't be null", Reason.RES_IMPL_BUG);
		}
		final List<SapContractItemEntity> replacingSapContractItemList = materialEnhancementsService.getReplacingContractItemList(sapContractItem.getSapContractItemId());

		if (replacingSapContractItemList == null) {
			throw new LogonException(
					"this contract item has no sibling contract item, which could replace it.",
					Reason.RES_IMPL_BUG);
		}

		Set<SupplyPlantDTO> supplyPlantSet = new HashSet<SupplyPlantDTO>();
		Set<ShippingpointDTO> shippingpointSet = new HashSet<ShippingpointDTO>();
		Set<LoadingPointDTO> loadingpointSet = new HashSet<LoadingPointDTO>();
		Set<PlantDTO> prodPlantSet = new HashSet<PlantDTO>();

		List<SupplyPlantDTO> supplyPlantList = new ArrayList<SupplyPlantDTO>();
		List<ShippingpointDTO> shippingpointList = new ArrayList<ShippingpointDTO>();
		List<LoadingPointDTO> loadingpointList = new ArrayList<LoadingPointDTO>();
		List<PlantDTO> prodPlantList = new ArrayList<PlantDTO>();
		String prodPlantCode = null;

		SapMaterialEntity replacingSapMaterial;
		DeliverySettingDTO realMaterialDeliverySetting;
		DeliveryOptionConfigurationDTO realDeliveryOptionConfiguration;

		for (SapContractItemEntity contractItem : replacingSapContractItemList) {
			replacingSapMaterial = contractItem.getSapMaterial();
			realMaterialDeliverySetting = new DeliverySettingDTO(replacingSapMaterial, shiptoSarea, timeZone);
			realDeliveryOptionConfiguration = DeliverySettingFilterLogic.getDeliveryOptionConfiguration(
					realMaterialDeliverySetting, sareaMap);

			supplyPlantSet.addAll(realDeliveryOptionConfiguration.getSupplyPlantList());
			prodPlantSet.addAll(realDeliveryOptionConfiguration.getProdPlantList());
			prodPlantCode = realMaterialDeliverySetting.getProductionPlantCd();

			if (deliveryPlantCd == null) {
				shippingpointSet.addAll(realDeliveryOptionConfiguration.getShippingpoinList());
			}
			else {
				for (SupplyPlantDTO plant : realDeliveryOptionConfiguration.getSupplyPlantList()) {
					if (plant.getPlantCd().equals(deliveryPlantCd)) {
						shippingpointSet.addAll(realDeliveryOptionConfiguration.getShippingpoinList());
						break;
					}
				}
			}

			if (shippingPointId == null) {
				loadingpointSet.addAll(realDeliveryOptionConfiguration.getLoadingpointList());
			}
			else {
				for (LoadingPointDTO loadingPoint : realDeliveryOptionConfiguration.getLoadingpointList()) {
					if (loadingPoint.getShippingpointId().equals(shippingPointId)) {
						loadingpointSet.addAll(realDeliveryOptionConfiguration.getLoadingpointList());
						break;
					}
				}
			}
		}
		supplyPlantList.addAll(supplyPlantSet);
		shippingpointList.addAll(shippingpointSet);
		loadingpointList.addAll(loadingpointSet);

		prodPlantList.addAll(prodPlantSet);

		// TODO (orsika Aug 1, 2013):create a class instead of this ugly hashmap
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("supplyPlantList", supplyPlantList);
		result.put("shippingpointList", shippingpointList);
		result.put("loadingpointList", loadingpointList);
		result.put("prodPlantList", prodPlantList);
		result.put("prodPlantCode", prodPlantCode);

		return result;
	}

	protected Filter<SapBpEntity> createSapBpListFilter(String query, boolean isSoldto, boolean isShipto, boolean isBillto, boolean includingDummyBp) {
		final Filter<SapBpEntity> filter;
		filter = new FilterImpl<SapBpEntity>();
		if (isSoldto) {
			filter.addExpression(Restrictions.eq(SapBpEntity.ATTR_BP_IS_SOLDTO, true));
		}
		if (isShipto) {
			filter.addExpression(Restrictions.eq(SapBpEntity.ATTR_BP_IS_SHIPTO, true));
		}
		if (isBillto) {
			filter.addExpression(Restrictions.eq(SapBpEntity.ATTR_BP_IS_BILLTO, true));
		}
		if (!includingDummyBp) {
			filter.addExpression(Restrictions.not(Restrictions.like(SapBpEntity.ATTR_SAP_ID, "DMY%")));
		}
		filter.addExpression(Restrictions.or(
				Restrictions.ilike(SapBpEntity.ATTR_NAME1, "%" + query + "%"),
				Restrictions.ilike(SapBpEntity.ATTR_NAME2, "%" + query + "%"),
				Restrictions.eq(SapBpEntity.ATTR_SAP_ID, query)));
		filter.addExpression(Order.asc(SapBpEntity.ATTR_NAME1));

		return filter;
	}

	public List<SapBpEntity> fetchSapBpList(int skip, int num, String query, boolean isSoldto, boolean isShipto, boolean isBillto, boolean includingDummyBp) {
		final ReadOnlyDao<SapBpEntity> dao = getDaoProvider().getSapBpEntityDao();
		final Filter<SapBpEntity> filter = createSapBpListFilter(query, isSoldto, isShipto,
				isBillto, includingDummyBp);

		return dao.findNRecs(skip, num, filter);
	}

	public long countSapBpList(String query, boolean isSoldto, boolean isShipto, boolean isBillto, boolean includingDummyBp) {
		final ReadOnlyDao<SapBpEntity> dao = getDaoProvider().getSapBpEntityDao();
		final Filter<SapBpEntity> filter = createSapBpListFilter(query, isSoldto, isShipto,
				isBillto, includingDummyBp);

		return dao.count(filter);
	}


	private Filter<OrderSearchEntity> getOrderSearchFilter(HashMap<String, String> searchCrit, TimeZone userTimeZone) throws ParseException {
		final Filter<OrderSearchEntity> filter = new FilterImpl<OrderSearchEntity>();
		filter.addExpression(Restrictions.eq(OrderSearchEntity.ATTR_LANG_CD, searchCrit.get(OrderSearchEntity.ATTR_LANG_CD)));

		if (!StringUtils.empty(searchCrit.get(OrderSearchEntity.ATTR_ORDER_NUMBER))) {
			filter.addExpression(Restrictions.eq(OrderSearchEntity.ATTR_ORDER_NUMBER, Long.valueOf(searchCrit.get(OrderSearchEntity.ATTR_ORDER_NUMBER))));
		}
		
		if (!StringUtils.empty(searchCrit.get(OrderSearchEntity.ATTR_LDS_DELIVERY_NOTE_NO))) {
			filter.addExpression(Restrictions.eq(OrderSearchEntity.ATTR_LDS_DELIVERY_NOTE_NO, searchCrit.get(OrderSearchEntity.ATTR_LDS_DELIVERY_NOTE_NO)));
		}
		
		// --- Sold/Shipto/Product ---
		if (!StringUtils.empty(searchCrit.get(OrderSearchEntity.ATTR_SOLDTO_SAP_BP_ID))) {
			filter.addExpression(Restrictions.eq(OrderSearchEntity.ATTR_SOLDTO_SAP_BP_ID, Long.valueOf(searchCrit.get(OrderSearchEntity.ATTR_SOLDTO_SAP_BP_ID))));
		}
		if (!StringUtils.empty(searchCrit.get(OrderSearchEntity.ATTR_SHIPTO_SAP_BP_ID))) {
			filter.addExpression(Restrictions.eq(OrderSearchEntity.ATTR_SHIPTO_SAP_BP_ID, Long.valueOf(searchCrit.get(OrderSearchEntity.ATTR_SHIPTO_SAP_BP_ID))));
		}
		if (!StringUtils.empty(searchCrit.get(OrderSearchEntity.ATTR_BILLTO_SAP_BP_ID))) {
			filter.addExpression(Restrictions.eq(OrderSearchEntity.ATTR_BILLTO_SAP_BP_ID, Long.valueOf(searchCrit.get(OrderSearchEntity.ATTR_BILLTO_SAP_BP_ID))));
		}
		if (!StringUtils.empty(searchCrit.get(OrderSearchEntity.ATTR_SAP_MATERIAL_ID))) {
			filter.addExpression(Restrictions.eq(OrderSearchEntity.ATTR_SAP_MATERIAL_ID, Long.valueOf(searchCrit.get(OrderSearchEntity.ATTR_SAP_MATERIAL_ID))));
		}
		// --- Delivery from/to ---

		String timeZone = userTimeZone.getId();
		
		if (!StringUtils.empty(searchCrit.get(OrderSearchEntity.ATTR_DELIVERY_FROM_DAT))) {
			Date fromDate = DateTimeUtils.parseDayMonthYearInGMTZone(searchCrit.get(OrderSearchEntity.ATTR_DELIVERY_FROM_DAT));
			fromDate = DateTimeUtils.getMorningOfDayInGMTZone(fromDate);

			filter.addExpression(Restrictions.ge(OrderSearchEntity.ATTR_DELIVERY_FROM_DAT, fromDate));
		}
		if (!StringUtils.empty(searchCrit.get(OrderSearchEntity.ATTR_DELIVERY_TO_DAT))) {
			Date toDate = DateTimeUtils.parseDayMonthYearInGMTZone(searchCrit.get(OrderSearchEntity.ATTR_DELIVERY_TO_DAT));
			toDate = DateTimeUtils.getMidnightOfDayInGMTZone(toDate);

			filter.addExpression(Restrictions.le(OrderSearchEntity.ATTR_DELIVERY_TO_DAT, toDate));
		}
		// --- Shipping point ---
		if (!StringUtils.empty(searchCrit.get(OrderSearchEntity.ATTR_SHIPPINGPOINT_ID))) {
			filter.addExpression(Restrictions.eq(OrderSearchEntity.ATTR_SHIPPINGPOINT_ID, Long.valueOf(searchCrit.get(OrderSearchEntity.ATTR_SHIPPINGPOINT_ID))));
		}
		// --- Pool / Carrier ---
		if (!StringUtils.empty(searchCrit.get(OrderSearchEntity.ATTR_POOL_ID))) {
			filter.addExpression(Restrictions.eq(OrderSearchEntity.ATTR_POOL_ID, Long.valueOf(searchCrit.get(OrderSearchEntity.ATTR_POOL_ID))));
		}

		// --- Executing, Commercial carrier
		
		if (!StringUtils.empty(searchCrit.get(OrderSearchEntity.ATTR_EXEC_CARRIER_ID))) {
			filter.addExpression(Restrictions.eq(OrderSearchEntity.ATTR_EXEC_CARRIER_ID, Long.valueOf(searchCrit.get(OrderSearchEntity.ATTR_EXEC_CARRIER_ID))));
		}

		if (!StringUtils.empty(searchCrit.get("carrierIdList"))) {
			String[] attr = (searchCrit.get("carrierIdList")).split(",");
			List<Long> carrierIdList = new ArrayList<Long>();
			for (String carrierIdx : attr) {
				carrierIdList.add(Long.valueOf(carrierIdx));
			}
			filter.addExpression(Restrictions.or(
					Restrictions.in(OrderSearchEntity.ATTR_COMM_CARRIER_ID, carrierIdList),
					Restrictions.in(OrderSearchEntity.ATTR_EXEC_CARRIER_ID, carrierIdList)
					));
		}
		
		if (!StringUtils.empty(searchCrit.get(OrderSearchEntity.ATTR_SAREA_ID))) {
			filter.addExpression(Restrictions.eq(OrderSearchEntity.ATTR_SAREA_ID, Long.valueOf(searchCrit.get(OrderSearchEntity.ATTR_SAREA_ID))));
		}
		if (!StringUtils.empty(searchCrit.get(OrderSearchEntity.ATTR_SHIPTO_SOFFICE_CD))) {
			filter.addExpression(Restrictions.eq(OrderSearchEntity.ATTR_SHIPTO_SOFFICE_CD,searchCrit.get(OrderSearchEntity.ATTR_SHIPTO_SOFFICE_CD)));
		}
		if (!StringUtils.empty(searchCrit.get(OrderSearchEntity.ATTR_SHIPTO_SGROUP_CD))) {
			filter.addExpression(Restrictions.eq(OrderSearchEntity.ATTR_SHIPTO_SGROUP_CD, searchCrit.get(OrderSearchEntity.ATTR_SHIPTO_SGROUP_CD)));
		}
		if (!StringUtils.empty(searchCrit.get(OrderSearchEntity.ATTR_TRUCK_ID))) {
			filter.addExpression(Restrictions.eq(OrderSearchEntity.ATTR_TRUCK_ID, Long.valueOf(searchCrit.get(OrderSearchEntity.ATTR_TRUCK_ID))));
		}
		if (!StringUtils.empty(searchCrit.get(OrderSearchEntity.ATTR_WAGON_ID))) {
			filter.addExpression(Restrictions.eq(OrderSearchEntity.ATTR_WAGON_ID, Long.valueOf(searchCrit.get(OrderSearchEntity.ATTR_WAGON_ID))));
		}
		if (!StringUtils.empty(searchCrit.get(OrderSearchEntity.ATTR_SIMPLE_VEHICLE_ID))) {
			filter.addExpression(Restrictions.eq(OrderSearchEntity.ATTR_SIMPLE_VEHICLE_ID, Long.valueOf(searchCrit.get(OrderSearchEntity.ATTR_SIMPLE_VEHICLE_ID))));
		}
		// --- Distribution destination list ---
		if (!StringUtils.empty(searchCrit.get("destinationCdList"))) {
			String[] destinationCodeArray = (searchCrit.get("destinationCdList")).split(",");
			List<String> destinationCodes = new ArrayList<String>();
			for (int i=0; i<destinationCodeArray.length; i++) {
				destinationCodes.add(destinationCodeArray[i]);
			}

			if ((destinationCodes.size() < Constant.TOTAL_DISTRIBUTION_DESTINATION_CODES_IN_LOGON)
					|| (((Long) getDaoProvider().getDirectJpaDao().findSingle(
							"SELECT COUNT(x) FROM "
									+ DistributionDestBsEntity.class.getSimpleName() + " x")).longValue() != destinationCodes.size())) {
				// "optimization": only add this filter if not all status' are selected
				// Check1: if distCdList has less items than the current amount of dist cd's, then add the filter
				// Check2: otherwise, double check if all are selected (this should take less time than the overhead of adding this data in the query)
				filter.addExpression(Restrictions.in(OrderSearchEntity.ATTR_DISTRIBUTION_DEST_CD,
						destinationCodes));
			}
		}
		// --- Status list ---
		if (!StringUtils.empty(searchCrit.get("statusList"))) {
			String[] statArr = (searchCrit.get("statusList")).split(",");
			List<Long> statusCodeList = new ArrayList<Long>();
			for (int i=0; i<statArr.length; i++) {
				statusCodeList.add(Long.valueOf(statArr[i]));
			}

			if ( statusCodeList.size() < COUNT_OF_ORDER_STATUS ) {
				// "optimization": see distCdList
				filter.addExpression(Restrictions.in(OrderSearchEntity.ATTR_ORDER_STATUS_CD, statusCodeList));
			}
		}
		// --- Status list ---
		if (!StringUtils.empty(searchCrit.get("creationTypeList"))) {
			String[] creTypeArr = (searchCrit.get("creationTypeList")).split(",");
			List<Long> creationTypeList = new ArrayList<Long>();
			for (int i=0; i<creTypeArr.length; i++) {
				creationTypeList.add(Long.valueOf(creTypeArr[i]));
			}

			filter.addExpression(Restrictions.in(OrderSearchEntity.ATTR_ORDER_CREATION_TYPE_CD, creationTypeList));
		}

		if (!StringUtils.empty(searchCrit.get("deliveryTypeCdList"))) {
			String[] attr = (searchCrit.get("deliveryTypeCdList")).split(",");
			if (attr.length < COUNT_OF_DELIVERY_TYPES) {
				List<Long> deliveryTypeList = new ArrayList<Long>();
				for (int i=0; i<attr.length; i++) {
					deliveryTypeList.add(Long.valueOf(attr[i]));
				}

				filter.addExpression(Restrictions.in(OrderSearchEntity.ATTR_DELIVERY_TYPE_CD, deliveryTypeList));
			}
		}
		
		
		if (!StringUtils.empty(searchCrit.get("plantCdList"))) {
			String[] attr = (searchCrit.get("plantCdList")).split(",");
			List<String> plantCdList = new ArrayList<String>();
			for (String plantCdIdx : attr) {
				plantCdList.add(plantCdIdx);
			}
			filter.addExpression(Restrictions.in(OrderSearchEntity.ATTR_PLANT_CD, plantCdList));
		}

		// --- Material packaging type (Order Monitoring only) ---
		String matPackagingType =  searchCrit.get("matPackagingType");
		if (Constant.MATERIAL_PACKAGE_BAG.equals(matPackagingType)) {
			final String[] bagTypes = {
					Constant.MATERIAL_PACKAGE_BAG,
					Constant.MATERIAL_PACKAGE_PALET_EMPTY};
			filter.addExpression(Restrictions.in(OrderSearchEntity.ATTR_MATERIAL_PACKAGE_CD,
					bagTypes));
		}
		else if (Constant.MATERIAL_PACKAGE_BULK_POWDER.equals(matPackagingType)) {
			final String[] bulkTypes = {
					Constant.MATERIAL_PACKAGE_NULL,
					Constant.MATERIAL_PACKAGE_BULK_POWDER,
					Constant.MATERIAL_PACKAGE_SILO};
			filter.addExpression(Restrictions.in(OrderSearchEntity.ATTR_MATERIAL_PACKAGE_CD,
					bulkTypes));
		}
		else if (Constant.MATERIAL_PACKAGE_BULK_DUMPED.equals(matPackagingType)) {
			final String[] bulkTypes = {
					Constant.MATERIAL_PACKAGE_BULK_DUMPED};
			filter.addExpression(Restrictions.in(OrderSearchEntity.ATTR_MATERIAL_PACKAGE_CD,
					bulkTypes));
		}

		
		return filter;
	}

	private Filter<OrderMonitoringEntity> getOrderMonitoringFilter(HashMap<String, String> searchCrit, TimeZone userTimeZone) throws ParseException {

		final Filter<OrderMonitoringEntity> filter = new FilterImpl<OrderMonitoringEntity>();
		filter.addExpression(Restrictions.eq(OrderMonitoringEntity.ATTR_LANG_CD, searchCrit.get(OrderMonitoringEntity.ATTR_LANG_CD)));

		if (!StringUtils.empty(searchCrit.get(OrderMonitoringEntity.ATTR_ORDER_NUMBER))) {
			filter.addExpression(Restrictions.eq(OrderMonitoringEntity.ATTR_ORDER_NUMBER, Long.valueOf(searchCrit.get(OrderMonitoringEntity.ATTR_ORDER_NUMBER))));
		}
		// --- Sold/Shipto/Product ---
		if (!StringUtils.empty(searchCrit.get(OrderMonitoringEntity.ATTR_SOLDTO_SAP_BP_ID))) {
			filter.addExpression(Restrictions.eq(OrderMonitoringEntity.ATTR_SOLDTO_SAP_BP_ID, Long.valueOf(searchCrit.get(OrderMonitoringEntity.ATTR_SOLDTO_SAP_BP_ID))));
		}
		if (!StringUtils.empty(searchCrit.get(OrderMonitoringEntity.ATTR_SHIPTO_SAP_BP_ID))) {
			filter.addExpression(Restrictions.eq(OrderMonitoringEntity.ATTR_SHIPTO_SAP_BP_ID, Long.valueOf(searchCrit.get(OrderMonitoringEntity.ATTR_SHIPTO_SAP_BP_ID))));
		}
		if (!StringUtils.empty(searchCrit.get(OrderMonitoringEntity.ATTR_BILLTO_SAP_BP_ID))) {
			filter.addExpression(Restrictions.eq(OrderMonitoringEntity.ATTR_BILLTO_SAP_BP_ID, Long.valueOf(searchCrit.get(OrderMonitoringEntity.ATTR_BILLTO_SAP_BP_ID))));
		}
		if (!StringUtils.empty(searchCrit.get(OrderMonitoringEntity.ATTR_SAP_MATERIAL_ID))) {
			filter.addExpression(Restrictions.eq(OrderMonitoringEntity.ATTR_SAP_MATERIAL_ID, Long.valueOf(searchCrit.get(OrderMonitoringEntity.ATTR_SAP_MATERIAL_ID))));
		}
		
		// --- Delivery from/to ---

		if (!StringUtils.empty(searchCrit.get(OrderMonitoringEntity.ATTR_DELIVERY_FROM_DAT))) {
			Date fromDate = DateTimeUtils.parseDayMonthYearInGMTZone(searchCrit.get(OrderMonitoringEntity.ATTR_DELIVERY_FROM_DAT));
			fromDate = DateTimeUtils.getMorningOfDayInGMTZone(fromDate);

			filter.addExpression(Restrictions.ge(OrderMonitoringEntity.ATTR_DELIVERY_FROM_DAT, fromDate));
		}
		if (!StringUtils.empty(searchCrit.get(OrderMonitoringEntity.ATTR_DELIVERY_TO_DAT))) {
			Date toDate = DateTimeUtils.parseDayMonthYearInGMTZone(searchCrit.get(OrderMonitoringEntity.ATTR_DELIVERY_TO_DAT));
			toDate = DateTimeUtils.getMidnightOfDayInGMTZone(toDate);

			filter.addExpression(Restrictions.le(OrderMonitoringEntity.ATTR_DELIVERY_TO_DAT, toDate));
		}
		
		// --- Shipping point ---
		if (!StringUtils.empty(searchCrit.get(OrderMonitoringEntity.ATTR_SHIPPINGPOINT_ID))) {
			filter.addExpression(Restrictions.eq(OrderMonitoringEntity.ATTR_SHIPPINGPOINT_ID, Long.valueOf(searchCrit.get(OrderMonitoringEntity.ATTR_SHIPPINGPOINT_ID))));
		}
		// --- Pool / Carrier ---
		if (!StringUtils.empty(searchCrit.get(OrderMonitoringEntity.ATTR_POOL_ID))) {
			filter.addExpression(Restrictions.eq(OrderMonitoringEntity.ATTR_POOL_ID, Long.valueOf(searchCrit.get(OrderMonitoringEntity.ATTR_POOL_ID))));
		}
		// --- Delivery plant ---
		if (!StringUtils.empty(searchCrit.get(OrderMonitoringEntity.ATTR_PLANT_CD))) {
			filter.addExpression(Restrictions.eq(OrderMonitoringEntity.ATTR_PLANT_CD,
					searchCrit.get(OrderMonitoringEntity.ATTR_PLANT_CD)));
		}

		// --- Vehicle name ---
		if (!StringUtils.empty(searchCrit.get(OrderMonitoringEntity.ATTR_VEHICLE_NAME))) {
			filter.addExpression(Restrictions.eq(OrderMonitoringEntity.ATTR_VEHICLE_NAME,
					searchCrit.get(OrderMonitoringEntity.ATTR_VEHICLE_NAME)));
		}
		
		// --- Executing, Commercial carrier
		
		if (!StringUtils.empty(searchCrit.get(OrderMonitoringEntity.ATTR_EXEC_CARRIER_ID))) {
			filter.addExpression(Restrictions.eq(OrderMonitoringEntity.ATTR_EXEC_CARRIER_ID, Long.valueOf(searchCrit.get(OrderMonitoringEntity.ATTR_EXEC_CARRIER_ID))));
		}
		
		if (!StringUtils.empty(searchCrit.get("carrierIdList"))) {
			String[] attr = (searchCrit.get("carrierIdList")).split(",");
			List<Long> carrierIdList = new ArrayList<Long>();
			for (String carrierIdx : attr) {
				carrierIdList.add(Long.valueOf(carrierIdx));
			}
			filter.addExpression(Restrictions.or(
					Restrictions.in(OrderMonitoringEntity.ATTR_COMM_CARRIER_ID, carrierIdList),
					Restrictions.in(OrderMonitoringEntity.ATTR_EXEC_CARRIER_ID, carrierIdList)
					));
		}
		
		
		if (!StringUtils.empty(searchCrit.get(OrderMonitoringEntity.ATTR_SAREA_ID))) {
			filter.addExpression(Restrictions.eq(OrderMonitoringEntity.ATTR_SAREA_ID, Long.valueOf(searchCrit.get(OrderMonitoringEntity.ATTR_SAREA_ID))));
		}
		if (!StringUtils.empty(searchCrit.get(OrderMonitoringEntity.ATTR_SHIPTO_SOFFICE_CD))) {
			filter.addExpression(Restrictions.eq(OrderMonitoringEntity.ATTR_SHIPTO_SOFFICE_CD,searchCrit.get(OrderMonitoringEntity.ATTR_SHIPTO_SOFFICE_CD)));
		}
		if (!StringUtils.empty(searchCrit.get(OrderMonitoringEntity.ATTR_SHIPTO_SGROUP_CD))) {
			filter.addExpression(Restrictions.eq(OrderMonitoringEntity.ATTR_SHIPTO_SGROUP_CD,
					searchCrit.get(OrderMonitoringEntity.ATTR_SHIPTO_SGROUP_CD)));
		}

		// --- Status list ---
		if (!StringUtils.empty(searchCrit.get("statusList"))) {
			String[] statArr = searchCrit.get("statusList").split(",");
			List<Long> statusCodeList = new ArrayList<Long>();
			for (int i=0; i<statArr.length; i++) {
				statusCodeList.add(Long.valueOf(statArr[i]));
			}

			if ( statusCodeList.size() < COUNT_OF_ORDER_STATUS_FOR_DELIVERY_MONITORING) {
				// "optimization": see distCdList
				filter.addExpression(Restrictions.in(OrderMonitoringEntity.ATTR_ORDER_STATUS_CD, statusCodeList));
			}
		}
		
		// --- Creation Type list ---
			if (!StringUtils.empty(searchCrit.get("creationTypeList"))) {
				String[] creTypeArr = (searchCrit.get("creationTypeList")).split(",");
				List<Long> creationTypeList = new ArrayList<Long>();
				for (int i=0; i<creTypeArr.length; i++) {
					creationTypeList.add(Long.valueOf(creTypeArr[i]));
				}

				filter.addExpression(Restrictions.in(OrderMonitoringEntity.ATTR_ORDER_CREATION_TYPE_CD, creationTypeList));
			}
	
		// --- Destination code list ---
		if (!StringUtils.empty(searchCrit.get("destinationCdList"))) {
			String[] destinationCodes = (searchCrit.get("destinationCdList")).split(",");
			List<String> allowedDestinationCodes = new ArrayList<String>();
			for (int i = 0; i < destinationCodes.length; i++) {
				allowedDestinationCodes.add(destinationCodes[i]);
			}
			filter.addExpression(Restrictions.in(OrderMonitoringEntity.ATTR_DISTRIBUTION_DEST_CD,
					allowedDestinationCodes));
		}
		// --- Delivery type list ---
		if (!StringUtils.empty(searchCrit.get("deliveryTypeCdList"))) {
			String[] attr = (searchCrit.get("deliveryTypeCdList")).split(",");
			if (attr.length < COUNT_OF_DELIVERY_TYPES) {
				List<Long> deliveryTypeList = new ArrayList<Long>();
				for (int i=0; i<attr.length; i++) {
					deliveryTypeList.add(Long.valueOf(attr[i]));
				}

				filter.addExpression(Restrictions.in(OrderMonitoringEntity.ATTR_DELIVERY_TYPE_CD, deliveryTypeList));
			}
		}

		// --- Material packaging type (Order Monitoring only) ---
		String matPackagingType =  searchCrit.get("matPackagingType");
		if (Constant.MATERIAL_PACKAGE_BAG.equals(matPackagingType)) {
			final String[] bagTypes = {
					Constant.MATERIAL_PACKAGE_BAG,
					Constant.MATERIAL_PACKAGE_PALET_EMPTY};
			filter.addExpression(Restrictions.in(OrderMonitoringEntity.ATTR_MATERIAL_PACKAGE_CD,
					bagTypes));
		}
		else if (Constant.MATERIAL_PACKAGE_BULK_POWDER.equals(matPackagingType)) {
			final String[] bulkTypes = {
					Constant.MATERIAL_PACKAGE_NULL,
					Constant.MATERIAL_PACKAGE_BULK_POWDER,
					Constant.MATERIAL_PACKAGE_SILO};
			filter.addExpression(Restrictions.in(OrderMonitoringEntity.ATTR_MATERIAL_PACKAGE_CD,
					bulkTypes));
		}
		else if (Constant.MATERIAL_PACKAGE_BULK_DUMPED.equals(matPackagingType)) {
			final String[] bulkTypes = {
					Constant.MATERIAL_PACKAGE_BULK_DUMPED};
			filter.addExpression(Restrictions.in(OrderMonitoringEntity.ATTR_MATERIAL_PACKAGE_CD,
					bulkTypes));
		}

		return filter;
	}

	/**
	 * Used by UC 4015 (Order Search) and UC 40XX (Order Monitoring)
	 */
	public OrderSearchListRange searchOrders(int skip, int num, String sort,
											 boolean asc,
											 HashMap<String, String> searchCrit, boolean monitoring, Locale userLocale,
											 TimeZone userTimeZone) {
		try {
			long count;

			final OrderSearchListRange orderSearchListRange;

			if (monitoring) {
				ReadOnlyDao<OrderMonitoringEntity> orderMonitoringDao = getDaoProvider().getOrderMonitoringEntityDao();
				Filter<OrderMonitoringEntity> filter = getOrderMonitoringFilter(searchCrit, userTimeZone);
				filter.addExpression(asc ? Order.asc(sort) : Order.desc(sort));
				//Paging order needs to be consistent so it must use a unique id also
				filter.addExpression(Order.desc(OrderMonitoringEntity.ATTR_COMPOSITE_ID));
				List<OrderMonitoringEntity> orderMonitoringList = orderMonitoringDao.findNRecs(
						skip, num, filter);
				orderSearchListRange = getOrderDeliveryList(orderMonitoringList, userLocale,
						userTimeZone);

				int dataSize = orderSearchListRange.getData().length;

				if ((skip == 0) && (dataSize < num)) {
					count = dataSize;
				}
				else {
					count = orderMonitoringDao.count(filter);
				}
			}
			else {
				ReadOnlyDao<OrderSearchEntity> orderSearchDao = getDaoProvider().getOrderSearchEntityDao();
				Filter<OrderSearchEntity> filter = getOrderSearchFilter(searchCrit, userTimeZone);
				filter.addExpression(asc ? Order.asc(sort) : Order.desc(sort));
				//Paging order needs to be consistent so it must use a unique id also
				filter.addExpression(Order.desc(OrderSearchEntity.ATTR_COMPOSITE_ID));
				List<OrderSearchEntity> orderSearchList = orderSearchDao.findNRecs(skip, num,
						filter);
				orderSearchListRange = getOrderSearchList(orderSearchList, userLocale, userTimeZone);

				int dataSize = orderSearchListRange.getData().length;

				if ((skip == 0) && (dataSize < num)) {
					count = dataSize;
				}
				else {
					count = orderSearchDao.count(filter);
				}
			}

			orderSearchListRange.setTotalSize(count);

			return orderSearchListRange;
		}
		catch (Exception exception) {
			LOG.error("Error while querying order monitoring / order search view", exception);
			return null;
		}
	}


	private OrderSearchListRange getOrderDeliveryList(List<OrderMonitoringEntity> orderList,
			Locale userLocale, TimeZone userTimeZone) {
		// --- Convert to beans allowing for easier date/time conversions ---//
		TimeZone defaultSystemTz = timeZoneService.createDefault(userLocale);

		List<OrderSearchBean> orderSearchBeans = new ArrayList<OrderSearchBean>();
		TimeZone shiptoTz = defaultSystemTz;
		String oldTimeZoneCd = null;
		final Set<Long> orderIdSet = new HashSet<Long>();
		for (OrderMonitoringEntity orderMonitoring : orderList) {
			String timeZoneCd = orderMonitoring.getShiptoTimezoneCd();
			Date deliveryFromDate = orderMonitoring.getDeliveryFromDat();
			Date deliveryToDate = orderMonitoring.getDeliveryToDat();
			Date plannedDeliveryDate = orderMonitoring.getPlannedDeliveryDat();
			Date loadingDate = orderMonitoring.getLoadingTs();
			
			if (StringUtils.notEmpty(timeZoneCd) && !timeZoneCd.equals(oldTimeZoneCd)) {
				shiptoTz = new TimeZone(timeZoneCd, userLocale);
				oldTimeZoneCd = timeZoneCd;
			}
			else if (StringUtils.empty(timeZoneCd)){
				shiptoTz = defaultSystemTz;
				oldTimeZoneCd = timeZoneCd;
			}

			OrderSearchBean bean = new OrderSearchBean(orderMonitoring);
			bean.setDeliveryFrom(shiptoTz.fmtAsDayMonthYearHourMinuteWithOffset(
					deliveryFromDate, userTimeZone));
			bean.setDeliveryTo(shiptoTz.fmtAsDayMonthYearHourMinuteWithOffset(
					deliveryToDate, userTimeZone));
			bean.setPlannedDelivery(shiptoTz.fmtAsDayMonthYearHourMinuteWithOffset(
					plannedDeliveryDate, userTimeZone));
			bean.setLoadingStr(shiptoTz.fmtAsDayMonthYearHourMinuteWithOffset(
					loadingDate, userTimeZone));
			
			orderSearchBeans.add(bean);

			final Long orderId = orderMonitoring.getOrderId();
			orderIdSet.add(orderId);
		}

		final int uniqueOrderCount = orderIdSet.size();

		final OrderSearchListRange orderSearchListRange = new OrderSearchListRange();
		orderSearchListRange.setOrderCount(uniqueOrderCount);
		OrderSearchBean[] orderSearchBeanArray = new OrderSearchBean[orderSearchBeans.size()];
		orderSearchBeanArray = orderSearchBeans.toArray(orderSearchBeanArray);
		orderSearchListRange.setData(orderSearchBeanArray);

		return orderSearchListRange;
	}

	private OrderSearchListRange getOrderSearchList(List<OrderSearchEntity> orderList,
			Locale userLocale, TimeZone userTimeZone) {
	// --- Convert to beans allowing for easier date/time conversions ---//
		TimeZone defaultSystemTz = timeZoneService.createDefault(userLocale);

		List<OrderSearchBean> orderSearchBeanList = new ArrayList<OrderSearchBean>();
		TimeZone shiptoTz = defaultSystemTz;
		String oldTimeZoneCd = null;
		final Set<Long> orderIdSet = new HashSet<Long>();
		for (OrderSearchEntity orderSearch : orderList) {
			String timeZoneCd = orderSearch.getShiptoTimezoneCd();
			Date deliveryFromDate = orderSearch.getDeliveryFromDat();
			Date deliveryToDate = orderSearch.getDeliveryToDat();
			Date plannedDeliveryDate = orderSearch.getPlannedDeliveryDat();
			Date loadingDate = orderSearch.getLoadingTs();

			if (StringUtils.notEmpty(timeZoneCd) && !timeZoneCd.equals(oldTimeZoneCd)) {
				shiptoTz = new TimeZone(timeZoneCd, userLocale);
				oldTimeZoneCd = timeZoneCd;
			}
			else if (StringUtils.empty(timeZoneCd)){
				shiptoTz = defaultSystemTz;
				oldTimeZoneCd = timeZoneCd;
			}

			OrderSearchBean bean = new OrderSearchBean(orderSearch);
			bean.setDeliveryFrom(shiptoTz.fmtAsDayMonthYearHourMinuteWithOffset(
					deliveryFromDate, userTimeZone));
			bean.setDeliveryTo(shiptoTz.fmtAsDayMonthYearHourMinuteWithOffset(
					deliveryToDate, userTimeZone));
			bean.setPlannedDelivery(shiptoTz.fmtAsDayMonthYearHourMinuteWithOffset(
					plannedDeliveryDate, userTimeZone));
			bean.setLoadingStr(shiptoTz.fmtAsDayMonthYearHourMinuteWithOffset(
					loadingDate, userTimeZone));

			orderSearchBeanList.add(bean);

			final Long orderId = orderSearch.getOrderId();
			orderIdSet.add(orderId);
		}

		final int uniqueOrderCount = orderIdSet.size();

		final OrderSearchListRange orderSearchListRange = new OrderSearchListRange();
		orderSearchListRange.setOrderCount(uniqueOrderCount);
		OrderSearchBean[] orderSearchBeanArray = new OrderSearchBean[orderSearchBeanList.size()];
		orderSearchBeanArray = orderSearchBeanList.toArray(orderSearchBeanArray);
		orderSearchListRange.setData(orderSearchBeanArray);
		return orderSearchListRange;
	}


	/**
	 * Used by UC 4015 (Order Search) and UC 40XX (Order Monitoring)
	 * @throws ParseException
	 */
	public OrderSearchInformations getOrderSearchInformations(int skip, int num, String sort,
			boolean asc, HashMap<String, String> searchCrit, boolean monitoring, Locale userLocale,
			TimeZone userTimeZone) throws ParseException {
		long totalOrderCount, orderCountBeforeActive;

		if (monitoring) {
			ReadOnlyDao<OrderMonitoringEntity> dao = getDaoProvider().getOrderMonitoringEntityDao();
			Filter<OrderMonitoringEntity> filter = getOrderMonitoringFilter(searchCrit, userTimeZone);
			String orderNumberAttr = OrderMonitoringEntity.ATTR_ORDER_NUMBER;
			totalOrderCount = getTotalOrderCount(dao, filter, orderNumberAttr);
			orderCountBeforeActive = getOrderCountBefore(skip, sort, asc, dao, filter,
					orderNumberAttr);
		}
		else {
			ReadOnlyDao<OrderSearchEntity> dao = getDaoProvider().getOrderSearchEntityDao();
			Filter<OrderSearchEntity> filter = getOrderSearchFilter(searchCrit, userTimeZone);
			String orderNumberAttr = OrderSearchEntity.ATTR_ORDER_NUMBER;
			totalOrderCount = getTotalOrderCount(dao, filter, orderNumberAttr);
			orderCountBeforeActive = getOrderCountBefore(skip, sort, asc, dao, filter,
					orderNumberAttr);
		}

		return new OrderSearchInformations(totalOrderCount, orderCountBeforeActive);
	}

	private <T extends BaseEntity> long getTotalOrderCount(ReadOnlyDao<T> dao, Filter<T> filter, String orderNumberAttr) {
		JPAQuery<T> jpaQuery = dao.createJPAQuery();
		jpaQuery.addSelect(SelectExpression.countDistinct(orderNumberAttr));
		jpaQuery.setFilter(filter);
		return (Long) dao.findSingle(jpaQuery);
	}

	private <T extends BaseEntity> long getOrderCountBefore(int skip, String sort, boolean asc,
			ReadOnlyDao<T> dao, Filter<T> filter, String orderNumberAttr) {
		if (skip <= 0) {
			return 0;
		}

		JPAQuery<T> jpaQuery = dao.createJPAQuery();
		jpaQuery = dao.createJPAQuery();
		jpaQuery.addSelect(SelectExpression.simpleSelect(orderNumberAttr));
		jpaQuery.setFilter(filter);
		filter.addExpression(asc ? Order.asc(sort) : Order.desc(sort));
		@SuppressWarnings("unchecked")
		List<Long> orderNumbers = dao.findNRecs(0, skip, jpaQuery);

		return new HashSet<Long>(orderNumbers).size();
	}

	public double getSummedQuantities(HashMap<String, String> searchCrit, TimeZone userTimeZone) throws ParseException {
		Filter<OrderSearchEntity> filter = getOrderSearchFilter(searchCrit, userTimeZone);
		filter.addExpression(Restrictions.neq(OrderSearchEntity.ATTR_ORDER_STATUS_CD, Constant.ORDER_STATUS_DELETED));
		DaoProvider daoProvider = getDaoProvider();
		ReadOnlyDao<OrderSearchEntity> orderSearchDao = daoProvider.getOrderSearchEntityDao();
		JPAQuery<OrderSearchEntity> jpaQuery = orderSearchDao.createJPAQuery();
		jpaQuery.addSelect(SelectExpression.sum(OrderSearchEntity.ATTR_TONS));
		jpaQuery.setFilter(filter);
		Double summedQuantity = (Double) orderSearchDao.findSingle(jpaQuery);
		return (summedQuantity == null) ? 0d : summedQuantity;
	}

	/**
	 * Used by the Order Search screen / Product filter
	 *
	 * @param skip
	 * @param num
	 * @return
	 */
	public ListRange<SapMaterialDescEntity> fetchSapMaterialList(int skip, int num, String query,
			Locale locale) {
		ReadOnlyDao<SapMaterialDescEntity> sapMatDao = getDaoProvider().getSapMaterialDescEntityDao();

		Filter<SapMaterialDescEntity> filter = new FilterImpl<SapMaterialDescEntity>();
		filter.addExpression(Restrictions.eq(SapMaterialDescEntity.ATTR_LANG_CD,
				locale.getLanguage()));
		filter.addExpression(Restrictions.ilike(SapMaterialDescEntity.ATTR_DISPLAY, "%" + query
				+ "%"));
		filter.addExpression(Restrictions.eq(SapMaterialDescEntity.ATTR_MARKED_FOR_DELETION,
				Boolean.FALSE));
		filter.addExpression(Order.asc(SapMaterialDescEntity.ATTR_TXT));

		ListRange<SapMaterialDescEntity> filteredMaterialList = new ListRange<SapMaterialDescEntity>();
		List<SapMaterialDescEntity> filteredMaterials = sapMatDao.findNRecs(skip, num, filter);
		SapMaterialDescEntity[] filteredMaterialArray = new SapMaterialDescEntity[filteredMaterials.size()];
		filteredMaterialArray = filteredMaterials.toArray(filteredMaterialArray);
		filteredMaterialList.setData(filteredMaterialArray);
		filteredMaterialList.setTotalSize(sapMatDao.count(filter));

		return filteredMaterialList;
	}


	/**
	 * Used by the Order Search screen / Vehicle filter
	 *
	 * @param skip
	 * @param num
	 * @return
	 */
	public ListRange<VehicleEntity> fetchVehicleList(int skip, int num, String query) {
		ReadOnlyDao<VehicleEntity> vehicleDao = getDaoProvider().getVehicleEntityDao();
		Filter<VehicleEntity> filter = new FilterImpl<VehicleEntity>();
		filter.addExpression(Restrictions.ilike(VehicleEntity.ATTR_VEHICLE_NAME, "%" + query + "%"));
		filter.addExpression(Order.asc(VehicleEntity.ATTR_VEHICLE_NAME));

		ListRange<VehicleEntity> vehicles = new ListRange<VehicleEntity>();
		List<VehicleEntity> vehiclesFromDB = vehicleDao.findNRecs(skip, num, filter);
		VehicleEntity[] vehiclesFromDBArray = new VehicleEntity[vehiclesFromDB.size()];
		vehiclesFromDBArray = vehiclesFromDB.toArray(vehiclesFromDBArray);
		vehicles.setData(vehiclesFromDBArray);
		vehicles.setTotalSize(vehicleDao.count(filter));

		return vehicles;
	}


	/**
	 * Used by the Order Search screen / SP filter
	 *
	 * @param skip
	 * @param num
	 * @return
	 */
	@AdnTrace
	public List<ShippingpointDTO> fetchShippingpointList() {

		return shippingpointStore.fetchShippingpointList();
	}


	public SapBpSareaEntity getSapBpSareaByShiptoSarea(ShiptoSareaEntity shiptoSarea) {
		final SapBpEntity shiptoSapBp = shiptoSarea.getShipto().getShiptoSapBp();
		final Long sareaId = shiptoSarea.getSarea().getSareaId();

		final SapBpSareaEntity findSingle = getSapBpSareaBySapBpAndSareaId(shiptoSapBp, sareaId);

		return findSingle;
	}

	/**
	 * @param shiptoSapBp
	 * @param sareaId
	 * @return
	 */
	public SapBpSareaEntity getSapBpSareaBySapBpAndSareaId(final SapBpEntity shiptoSapBp,
			final Long sareaId) {
		ReadOnlyDao<SapBpSareaEntity> dao = getDaoProvider().getSapBpSareaEntityDao();
		Filter<SapBpSareaEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(SapBpSareaEntity.ATTR_SAP_BP_ID,
				shiptoSapBp.getSapBpId()));
		filter.addExpression(Restrictions.eq(SapBpSareaEntity.ATTR_SAREA_ID, sareaId));
		filter.addExpression(Restrictions.eq(SapBpSareaEntity.ATTR_MARKED_FOR_DELETION, "0"));

		final SapBpSareaEntity findSingle = dao.findSingle(filter);
		return findSingle;
	}

	public List<SareaEntity> fetchSapBpSareaListBySapBp(SapBpEntity sapBp) {
			final ReadOnlyDao<SapBpSareaEntity> bpSareaDao = getDaoProvider().getSapBpSareaEntityDao();
		final Filter<SapBpSareaEntity> bpSareaFilter = bpSareaDao.createFilter();
		bpSareaFilter.addExpression(Restrictions.eq(SapBpSareaEntity.ATTR_SAP_BP_ID,
				sapBp.getSapBpId()));
		final List<SapBpSareaEntity> bpSareas = bpSareaDao.find(bpSareaFilter);

		final List<SareaEntity> sareas = new ArrayList<SareaEntity>();
		for (SapBpSareaEntity bpSarea : bpSareas) {
			sareas.add(bpSarea.getSarea());
		}

		return sareas;
	}

	/**
	 * Queries the first 100 ship to - sold to combination
	 */
	public List<WeborderShipToSoldToEntity> fetchWeborderShipToSoldToList() {
		Filter<WeborderShipToSoldToEntity> filter = new FilterImpl<WeborderShipToSoldToEntity>();
		ReadOnlyDao<WeborderShipToSoldToEntity> weborderShipToSoldToEntityDao = getDaoProvider().getWeborderShipToSoldToEntityDao();
		return weborderShipToSoldToEntityDao.findNRecs(0, 100, filter);
	}

	/**
	 * @param skipAuthorization In case the call comes from web order, the customer does not have authorization to query for soldtos shipping the
	 * bimodal rail part to the freiverlad, but the related bimodal rail order must be created despite. Therefore here it is needed to skip
	 * the restrictions, as far otherwise no bimodal rail part order can be created.
	 */
	public List<SapBpEntity> fetchSoldtoByShiptoSarea(Long shiptoSapBpId, Long salesAreaId,
			Boolean skipAuthorization) throws LogonException {
		ReadWriteDao<SapBpEntity> sapBpDao = getDaoProvider().getSapBpEntityDao();
		ReadOnlyDao<SoldshipSareaFlexSrchEntity> srchDao = getDaoProvider().getSoldshipSareaFlexSrchEntityDao();

		if (skipAuthorization != null && skipAuthorization.equals(Boolean.TRUE)) {
			Authorizer.disableNextAccessRestriction();
		}
		Filter<SoldshipSareaFlexSrchEntity> srchFilter = srchDao.createFilter();
		srchFilter.addExpression(Restrictions.eq(SoldshipSareaFlexSrchEntity.ATTR_SHIPTO_SAP_BP_ID,
				shiptoSapBpId));
		srchFilter.addExpression(Restrictions.eq(SoldshipSareaFlexSrchEntity.ATTR_SAREA_ID, salesAreaId));
		srchFilter.addExpression(Restrictions.eq(
				SoldshipSareaFlexSrchEntity.ATTR_DAILY_ORDER_STATUS_LANG_CD, "de")); // Lang CD is just needed to guarantee a single result
		srchFilter.addExpression(Restrictions.eq(
				SoldshipSareaFlexSrchEntity.ATTR_SHIPTO_MARKED_FOR_DELETION, "0"));
		JPAQuery<SoldshipSareaFlexSrchEntity> query = srchDao.createJPAQuery();
		query.addSelect(SelectExpression.simpleSelect(SoldshipSareaFlexSrchEntity.ATTR_SOLDTO_SAP_BP_ID));
		query.setFilter(srchFilter);

		try {
			@SuppressWarnings("unchecked")
			List<Long> soldToSapBpIds = srchDao.find(query);

			if (soldToSapBpIds.isEmpty()) {
				return new ArrayList<SapBpEntity>(0);
			}

			Filter<SapBpEntity> soldToFilter = sapBpDao.createFilter();
			soldToFilter.addExpression(Restrictions.in(SapBpEntity.ATTR_SAP_BP_ID, soldToSapBpIds));
			soldToFilter.addExpression(Restrictions.eq(SapBpEntity.ATTR_BP_IS_SOLDTO, true)); // For authorization

			if (skipAuthorization != null && skipAuthorization.equals(Boolean.TRUE)) {
				Authorizer.disableNextAccessRestriction();
			}
			return sapBpDao.find(soldToFilter);
		}
		catch (Exception exception) {
			throw new LogonException(exception);
		}
	}

	/**
	 * Service method to provide data for the Sold-to/Ship-to/Sarea paging combobox for Order Taking (UC 4012)
	 *
	 * @param skip from pager
	 * @param num from pager
	 * @param query user input, will be transformed and passed as search value on sevaral fields
	 * @param custWithoutOrderToday if true, only customers without an order for the current day will be returned (for UC 4016)
	 * @param activeCem if true, only values with active CEM contracts will be returned
	 * @param activeAgg if true, only values with active AGG contracts will be returned
	 * @return List of SapSoldShipBestSrchEntity entities matching the query
	 * @adnlog.exclude return
	 */
	public ListRange<SoldshipBestFlexSrchEntity> fetchSoldtoShiptoBestList(int skip, int num,
			String query, boolean custWithoutOrderToday, boolean activeCem, boolean activeAgg, Locale locale) {

		ReadOnlyDao<SoldshipBestFlexSrchEntity> searchDao = getDaoProvider().getSoldshipBestFlexSrchEntityDao();

		ListRange<SoldshipBestFlexSrchEntity> soldToShipToBestellungList = new ListRange<SoldshipBestFlexSrchEntity>();
		List<SoldshipBestFlexSrchEntity> shipTos = new LinkedList<SoldshipBestFlexSrchEntity>();
		Long totalSize = 0L;
		boolean isDummyQuery = query.startsWith("DMY");

		Filter<SoldshipBestFlexSrchEntity> filter = new FilterImpl<SoldshipBestFlexSrchEntity>();

		if (!StringUtils.empty(query)) {
			if (isDummyQuery) {
				filter.addExpression(Restrictions.or(
						Restrictions.ilike(SoldshipBestFlexSrchEntity.ATTR_SHIPTO_NAME, query),
						Restrictions.ilike(SoldshipBestFlexSrchEntity.ATTR_SOLDTO_NAME, query),
						Restrictions.ilike(SoldshipBestFlexSrchEntity.ATTR_POSTAL_CODE, query),
						Restrictions.ilike(SoldshipBestFlexSrchEntity.ATTR_CITY, query),
						Restrictions.eq(SoldshipBestFlexSrchEntity.ATTR_SHIPTO_SAP_ID, query),
						Restrictions.eq(SoldshipBestFlexSrchEntity.ATTR_SOLDTO_SAP_ID, query)));
			}
			else {
				query = replaceWildcards(query);

				filter.addExpression(Restrictions.ilike(
						SoldshipBestFlexSrchEntity.ATTR_COMPOSITE_SEARCH_STRING, query));
			}
		}
		filter.addExpression(Restrictions.eq(
				SoldshipBestFlexSrchEntity.ATTR_SHIPTO_MARKED_FOR_DELETION, "0"));
		filter.addExpression(Restrictions.eq(
				SoldshipBestFlexSrchEntity.ATTR_SOLDTO_MARKED_FOR_DELETION, "0"));

		// --- Language filter (daily order status is the only lang dependent field) ---
		filter.addExpression(Restrictions.eq(SoldshipBestFlexSrchEntity.ATTR_LANG_CD,
				locale.getLanguage()));
		// --- Customers without ordering today ---
		if (custWithoutOrderToday) {
			filter.addExpression(Restrictions.eq(
					SoldshipBestFlexSrchEntity.ATTR_DAILY_ORDER_CONTACT_FLAG, true));
			filter.addExpression(Order.asc(SoldshipBestFlexSrchEntity.ATTR_DAILY_ORDER_STATUS_CD));
		}

		final Date today = DateTimeUtils.getMorningOfDay(new Date());
		final Expression activeCemExpression = Restrictions.and(
				Restrictions.isNotNull(SoldshipBestFlexSrchEntity.ATTR_CEM_VALID_UNTIL_DAT),
				Restrictions.ge(SoldshipBestFlexSrchEntity.ATTR_CEM_VALID_UNTIL_DAT, today)
		);
		final Expression activeAggExpression = Restrictions.and(
				Restrictions.isNotNull(SoldshipBestFlexSrchEntity.ATTR_AGG_VALID_UNTIL_DAT),
				Restrictions.ge(SoldshipBestFlexSrchEntity.ATTR_AGG_VALID_UNTIL_DAT, today)
		);

		// --- With active CEM contract or active AGG contract (when both checkboxes are selected)
		if (activeCem && activeAgg) {
			filter.addExpression(Restrictions.or(
					activeCemExpression,
					activeAggExpression
			));
		}
		// --- With active CEM contract
		else if (activeCem) {
			filter.addExpression(activeCemExpression);
		}
		// --- With active AGG contract
		else if (activeAgg){
			filter.addExpression(activeAggExpression);
		}

		filter.addExpression(Order.asc(SoldshipBestFlexSrchEntity.ATTR_SHIPTO_NAME));
		filter.addExpression(Order.asc(SoldshipBestFlexSrchEntity.ATTR_SOLDTO_NAME));

		shipTos = searchDao.findNRecs(skip, num, filter);
		totalSize = searchDao.count(filter);

		SoldshipBestFlexSrchEntity[] shipToArray = new SoldshipBestFlexSrchEntity[shipTos.size()];
		shipToArray = shipTos.toArray(shipToArray);
		soldToShipToBestellungList.setData(shipToArray);
		soldToShipToBestellungList.setTotalSize(totalSize);

		return soldToShipToBestellungList;
	}

	/**
	 * Service method to provide data for the "Modify BP" function's drop down in case of delivered order,
	 * on "order detail" screen using vlgnc_editbp_soldship_sarea.
	 *
	 * @param skip from pager
	 * @param num from pager
	 * @param query user input, will be transformed and passed as search value on sevaral fields
	 * @param custWithoutOrderToday if true, only customers without an order for the current day will be returned (for UC 4016)
	 * @return List of SapSoldShipBestSrchEntity entities matching the query
	 * @adnlog.exclude return
	 */
	public ListRange<EditbpSoldshipSareaEntity> fetchSoldtoShiptoModifyBpList(int skip, int num,
			String query, Locale locale) {
		ReadOnlyDao<EditbpSoldshipSareaEntity> searchDao = getDaoProvider().getEditbpSoldshipSareaEntityDao();

		ListRange<EditbpSoldshipSareaEntity> soldToShipToModifyBpList = new ListRange<EditbpSoldshipSareaEntity>();
		List<EditbpSoldshipSareaEntity> shipTos = new LinkedList<EditbpSoldshipSareaEntity>();
		Long totalSize = 0L;
		boolean isDummyQuery = query.startsWith("DMY");

		Filter<EditbpSoldshipSareaEntity> filter = new FilterImpl<EditbpSoldshipSareaEntity>();

		if (!StringUtils.empty(query)) {
			if (isDummyQuery) {

				filter.addExpression(Restrictions.or(
						Restrictions.ilike(EditbpSoldshipSareaEntity.ATTR_SHIPTO_NAME, query),
						Restrictions.ilike(EditbpSoldshipSareaEntity.ATTR_SOLDTO_NAME, query),
						Restrictions.ilike(EditbpSoldshipSareaEntity.ATTR_SHIPTO_POSTAL_CODE, query),
						Restrictions.ilike(EditbpSoldshipSareaEntity.ATTR_SHIPTO_CITY, query),
						Restrictions.eq(EditbpSoldshipSareaEntity.ATTR_SHIPTO_SAP_ID, query),
						Restrictions.eq(EditbpSoldshipSareaEntity.ATTR_SOLDTO_SAP_ID, query)));
			}
			else {
				query = replaceWildcards(query);

				filter.addExpression(Restrictions.ilike(
						EditbpSoldshipSareaEntity.ATTR_COMPOSITE_SEARCH_STRING, query));
			}
		}
		filter.addExpression(Restrictions.eq(
				EditbpSoldshipSareaEntity.ATTR_SHIPTO_MARKED_FOR_DELETION, "0"));
		filter.addExpression(Restrictions.eq(
				EditbpSoldshipSareaEntity.ATTR_SOLDTO_MARKED_FOR_DELETION, "0"));

		// --- Language filter (daily order status is the only lang dependent field) ---
		filter.addExpression(Restrictions.eq(
				EditbpSoldshipSareaEntity.ATTR_DAILY_ORDER_STATUS_LANG_CD, locale.getLanguage()));

		filter.addExpression(Order.asc(EditbpSoldshipSareaEntity.ATTR_SHIPTO_NAME));
		filter.addExpression(Order.asc(EditbpSoldshipSareaEntity.ATTR_SOLDTO_NAME));

		shipTos = searchDao.findNRecs(skip, num, filter);
		totalSize = searchDao.count(filter);

		EditbpSoldshipSareaEntity[] shipToArray = new EditbpSoldshipSareaEntity[shipTos.size()];
		shipToArray = shipTos.toArray(shipToArray);
		soldToShipToModifyBpList.setData(shipToArray);
		soldToShipToModifyBpList.setTotalSize(totalSize);

		return soldToShipToModifyBpList;
	}

	public ListRange<SoldshipSareaFlexSrchEntity> fetchRealSoldtoShiptoSareaList(int skip, int num,
			String query, Long soldtoSapBpIdFilter, Locale locale) {
		ReadOnlyDao<SoldshipSareaFlexSrchEntity> searchDao = getDaoProvider().getSoldshipSareaFlexSrchEntityDao();

		ListRange<SoldshipSareaFlexSrchEntity> realSoldtoShiptoSareaList = new ListRange<SoldshipSareaFlexSrchEntity>();
		List<SoldshipSareaFlexSrchEntity> shipTos = new LinkedList<SoldshipSareaFlexSrchEntity>();
		Long totalSize = 0L;
		boolean isDummyQuery = query.startsWith("DMY");

		Filter<SoldshipSareaFlexSrchEntity> filter = new FilterImpl<SoldshipSareaFlexSrchEntity>();

		if (!StringUtils.empty(query)) {
			if (isDummyQuery) {
				filter.addExpression(Restrictions.or(
						Restrictions.ilike(SoldshipSareaFlexSrchEntity.ATTR_SHIPTO_NAME, query),
						Restrictions.ilike(SoldshipSareaFlexSrchEntity.ATTR_SOLDTO_NAME, query),
						Restrictions.ilike(SoldshipSareaFlexSrchEntity.ATTR_POSTAL_CODE, query),
						Restrictions.ilike(SoldshipSareaFlexSrchEntity.ATTR_CITY, query),
						Restrictions.eq(SoldshipSareaFlexSrchEntity.ATTR_SHIPTO_NR, query),
						Restrictions.eq(SoldshipSareaFlexSrchEntity.ATTR_SOLDTO_NR, query)));
			}
			else {
				query = replaceWildcards(query);

				filter.addExpression(Restrictions.ilike(
						SoldshipSareaFlexSrchEntity.ATTR_COMPOSITE_SEARCH_STRING, query));
			}
		}

		// --- Language filter (daily order status is the only lang dependent field) ---
		filter.addExpression(Restrictions.eq(
				SoldshipSareaFlexSrchEntity.ATTR_DAILY_ORDER_STATUS_LANG_CD, locale.getLanguage()));
		filter.addExpression(Restrictions.eq(
				SoldshipSareaFlexSrchEntity.ATTR_SOLDTO_MARKED_FOR_DELETION, "0"));
		filter.addExpression(Restrictions.eq(
				SoldshipSareaFlexSrchEntity.ATTR_SHIPTO_MARKED_FOR_DELETION, "0"));
		filter.addExpression(Restrictions.not(Restrictions.like(
				SoldshipSareaFlexSrchEntity.ATTR_SHIPTO_NR, "DMY%")));

		if (soldtoSapBpIdFilter != null) {
			filter.addExpression(Restrictions.eq(SoldshipSareaFlexSrchEntity.ATTR_SOLDTO_SAP_BP_ID,
					soldtoSapBpIdFilter));
		}

		filter.addExpression(Order.asc(SoldshipSareaFlexSrchEntity.ATTR_SHIPTO_NAME));
		filter.addExpression(Order.asc(SoldshipSareaFlexSrchEntity.ATTR_SOLDTO_NAME));
		filter.addExpression(Order.asc(SoldshipSareaFlexSrchEntity.ATTR_SAREA_DISPLAY_LONGNAME));

		shipTos = searchDao.findNRecs(skip, num, filter);
		totalSize = searchDao.count(filter);

		SoldshipSareaFlexSrchEntity[] shipToArray = new SoldshipSareaFlexSrchEntity[shipTos.size()];
		shipToArray = shipTos.toArray(shipToArray);
		realSoldtoShiptoSareaList.setData(shipToArray);
		realSoldtoShiptoSareaList.setTotalSize(totalSize);

		return realSoldtoShiptoSareaList;
	}

	public List<GeneralDisplayNameDto> getSapBpDisplayTexts(Set<Long> sapBpIds) {
		ArrayList<GeneralDisplayNameDto> result = new ArrayList<GeneralDisplayNameDto>();

		final String entityName = SapBpEntity.class.getSimpleName();
		StringBuilder jpqlQuery = new StringBuilder();
		jpqlQuery.append(
				"SELECT x." + SapBpEntity.ATTR_SAP_BP_ID + ", x." + SapBpEntity.ATTR_NAME1
						+ " FROM ").append(entityName).append(" x ");
		jpqlQuery.append("WHERE " + SapBpEntity.ATTR_SAP_BP_ID + " IN (:sapBpIds) ");
		Query query = em.createQuery(jpqlQuery.toString());
		query.setParameter("sapBpIds", sapBpIds);

		@SuppressWarnings("unchecked")
		final List<Object[]> sapBpIdAndNames = query.getResultList();

		GeneralDisplayNameDto idNameDto;
		for (Object[] sapBpIdAndName : sapBpIdAndNames) {
			Long sapBpId = (Long) sapBpIdAndName[0];
			String sapBpName = (String) sapBpIdAndName[1];
			idNameDto = new GeneralDisplayNameDto(sapBpId, sapBpName);
			result.add(idNameDto);
		}
		return result;
	}

	public GeneralDisplayNameDto getSapMaterialDisplayTexts(Long sapMaterialId, Locale locale) {
		final ReadOnlyDao<SapMaterialDescEntity> dao = getDaoProvider().getSapMaterialDescEntityDao();
		Filter<SapMaterialDescEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(SapMaterialDescEntity.ATTR_SAP_MATERIAL_ID,
				sapMaterialId));
		filter.addExpression(Restrictions.eq(SapMaterialDescEntity.ATTR_LANG_CD,
				locale.getLanguage()));
		SapMaterialDescEntity sapMaterialDescEntity = dao.findSingle(filter);
		if (sapMaterialDescEntity != null) {
			return new GeneralDisplayNameDto(sapMaterialDescEntity.getSapMaterialId(),
					sapMaterialDescEntity.getDisplay());
		}
		return null;
	}

	private String replaceWildcards(String query) {
		query = query.replace("|", "||");
		query = query.replace("%", "|%");
		query = query.replace("_", "|_");
		/* now replace the query wildcard characlter '*'
		 * by the JPAQL escape character*/
		query = query.replace('*', '%');
		// replace white spaces with wildcard
		query = query.replaceAll("\\s", "%");
		/* now convert to uppercase */
		query = query.toUpperCase();
		/* finally add a wildcard character at the end and at the begining of the String*/
		query = '%' + query + '%';
		return query;
	}

	/**
	 * Method called by the "copy" icon in Order Taking (using an existing order as a template for
	 * a new order). We only copy the first item!
	 *
	 * @param orderId
	 * @return
	 */
	public OrderCreateEntity getExistingOrder(Long orderItemId) {
		ReadOnlyDao<OrderCreateEntity> oDao = getDaoProvider().getOrderCreateEntityDao();
		Filter<OrderCreateEntity> filter = oDao.createFilter();

		filter.addExpression(Restrictions.eq(OrderCreateEntity.ATTR_ORDER_ITEM_ID, orderItemId));
		// Hard-coded language is OK, since no language dependent data is needed for the cloning
		filter.addExpression(Restrictions.eq(OrderCreateEntity.ATTR_LANG_CD,
				Locale.ENGLISH.getLanguage()));

		return oDao.findSingle(filter);
	}

	/**
	 * Method called when adding single-delivery orders to a customer order.
	 *
	 * @param custorderId
	 * @return
	 */
	public CustorderCreateEntity getExistingCustorder(Long custorderItemId) {
		ReadOnlyDao<CustorderCreateEntity> oDao = getDaoProvider().getCustorderCreateEntityDao();
		Filter<CustorderCreateEntity> filter = oDao.createFilter();

		filter.addExpression(Restrictions.eq(CustorderCreateEntity.ATTR_CUSTORDER_ITEM_ID,
				custorderItemId));
		// Hard-coded language is OK, since no language dependent data is needed for the "cloning"
		filter.addExpression(Restrictions.eq(CustorderCreateEntity.ATTR_LANG_CD,
				Locale.ENGLISH.getLanguage()));

		return oDao.findSingle(filter);
	}

	/**
	 * Retrieves a list with assignment related time info.
	 * @param orderIds
	 * @param context depending on the context filter is going to be added
	 * @return
	 */
	public List<OrdUnloadSchedInfoEntity> getAssignmentTimeInfoForOrders(List<Long> orderIds) {
		List<OrdUnloadSchedInfoEntity> arrivalTimes = new ArrayList<OrdUnloadSchedInfoEntity>();
		if (!orderIds.isEmpty()) {
			Expression orderRestriction = DbUtils.createSplittedInRestrictionOfIds(
					OrdUnloadSchedInfoEntity.ATTR_ORDER_ID, orderIds);

			Filter<OrdUnloadSchedInfoEntity> filter = new FilterImpl<OrdUnloadSchedInfoEntity>();
			filter.addExpression(orderRestriction);

			DaoProvider daoProvider = getDaoProvider();
			ReadOnlyDao<OrdUnloadSchedInfoEntity> dao = daoProvider.getOrdUnloadSchedInfoEntityDao();
			arrivalTimes = dao.find(filter);
		}
		return arrivalTimes;
	}

	/**
	 * Fetch BimodalRailOrder list regarding to the truck part.
	 *
	 * @param sapMatId The sap material ID
	 * @param propPlantCd The id of the production plant
	 * @param fvlSpId The id of the preferred shipto
	 * @param fvlMixShipToPerWagon
	 * @param truckPartShipToSapBpId The id of the truckPart of shippingto
	 * @param truckPartDelToDat the Date of the truckpart delivery
	 * @param locale The current locale
	 *
	 * */
	public List<BimodalRailOrderEntity> fetchRailOrderListForBimodalTruckPart(Long sapMatId, String prodPlantCd,Long fvlSpId, Boolean fvlMixShiptoPerWagon, Long truckPartShiptoSapBpId, Date truckPartDelToDat, Locale locale) throws LogonException {
		ReadWriteDao<ShiptoEntity> shiptoDao = getDaoProvider().getShiptoEntityDao();
		JPAQuery<ShiptoEntity> jpaQuery = getDaoProvider().getShiptoEntityDao().createJPAQuery();

		Filter<ShiptoEntity> shiptoFilter = new FilterImpl<ShiptoEntity>();
		shiptoFilter.addExpression(Restrictions.eq(ShiptoEntity.ATTR_MAT_CHAIN_SHIPPINGPOINT_ID,
				fvlSpId));

		jpaQuery.setFilter(shiptoFilter);
		jpaQuery.addSelect(SelectExpression.simpleSelect(ShiptoEntity.ATTR_SHIPTO_SAP_BP_ID));
		@SuppressWarnings("unchecked")
		List<Long> shipToIds = shiptoDao.find(jpaQuery);

		ReadOnlyDao<BimodalRailOrderEntity> orderDao = getDaoProvider().getBimodalRailOrderEntityDao();
		Date truckPartDelToDatMidnight = DateTimeUtils.getMidnightOfDay(truckPartDelToDat);
		if (!shipToIds.isEmpty()) {
			//get bimodal rail orders
			Filter<BimodalRailOrderEntity> filter = getFilterForBimodalRailSearch(sapMatId,
					prodPlantCd, shipToIds, truckPartDelToDatMidnight, null, locale.getLanguage());
			filter.addExpression(Order.asc(BimodalRailOrderEntity.ATTR_ACTUAL_DELIVERY_DAT));

			// --- Checking dependent orders: If the shipping point has FVL_MIX_SHIPTO_PER_WAGON_FLAG = false: exclude those rail orders ---
			if (!fvlMixShiptoPerWagon) {
				StringBuilder depItemSelect = new StringBuilder();
				depItemSelect.append(
						"not exists ( from OrderItemEntity o join o.bimodalRailOrderItem bimodal "
								+ "join o.order orderEntity join orderEntity.orderStatus orderStatus join orderEntity.shiptoSapBp shiptoSapBp where ").append(
						OrderEntity.ATTR_ORDER_STATUS + "."
								+ OrderStatusBsEntity.ATTR_ORDER_STATUS_CD).append("<").append(
						Constant.ORDER_STATUS_DELETED).append(" ").append(
						"and " + OrderEntity.ATTR_SHIPTO_SAP_BP + "." + SapBpEntity.ATTR_SAP_BP_ID).append(
						"<>").append(truckPartShiptoSapBpId).append(
						"and bimodal.orderItemId = x.orderItemId").append(")");
				//extend query if there is dependant truckpart in the orderitem table.
				filter.addExpression(Restrictions.jpaqlExpression(depItemSelect.toString(),
						new HashMap<String, Object>()));
			}
			return orderDao.find(filter);
		}

		return Collections.emptyList();
	}

	public Filter<BimodalRailOrderEntity> getFilterForBimodalRailSearch(Long materialId,
			String productionPlantCode, List<Long> fvlShipToIds, Date truckPartDelToDatMidnight,
			String companyCode, String languageCode) {

		Filter<BimodalRailOrderEntity> filter = new FilterImpl<BimodalRailOrderEntity>();
		if (materialId != null) {
			filter.addExpression(Restrictions.eq(BimodalRailOrderEntity.ATTR_SAP_MATERIAL_ID,
					materialId));
		}
		if (productionPlantCode != null && productionPlantCode != "-1") {
			filter.addExpression(Restrictions.eq(BimodalRailOrderEntity.ATTR_PRODUCTION_PLANT_CD,
					productionPlantCode));
		}
		filter.addExpression(Restrictions.or(Restrictions.lt(
				BimodalRailOrderEntity.ATTR_ORDER_STATUS_CD, Constant.ORDER_STATUS_DELIVERED),
				Restrictions.eq(BimodalRailOrderEntity.ATTR_ORDER_STATUS_CD,
						Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL)));
		filter.addExpression(Restrictions.eq(BimodalRailOrderEntity.ATTR_DELIVERY_TYPE_CD,
				Constant.DELIVERY_TYPE_BIMODAL_RAIL));
		if (fvlShipToIds != null && !fvlShipToIds.isEmpty()) {
			filter.addExpression(DbUtils.createSplittedInRestrictionOfIds(
					BimodalRailOrderEntity.ATTR_SHIPTO_SAP_BP_ID, fvlShipToIds));
		}
		if (companyCode != null) {
			filter.addExpression(Restrictions.eq(BimodalRailOrderEntity.ATTR_COMPANYCODE_CD,
					companyCode));
		}
		if (truckPartDelToDatMidnight != null) {
			filter.addExpression(Restrictions.le(BimodalRailOrderEntity.ATTR_ACTUAL_DELIVERY_DAT,
					truckPartDelToDatMidnight));
		}

		filter.addExpression(Restrictions.eq(BimodalRailOrderEntity.ATTR_LANG_CD, languageCode));

		return filter;
	}

	public List<BimodalRailOrderEntity> fetchDependentTruckOrderList(List<Long> railOrderItemIds, String languageCode) {
		if (railOrderItemIds == null || railOrderItemIds.isEmpty()) {
			return new ArrayList<BimodalRailOrderEntity>(0);
		}

		ReadOnlyDao<BimodalRailOrderEntity> bimodalRailOrderItemDao = getDaoProvider().getBimodalRailOrderEntityDao();

		Filter<BimodalRailOrderEntity> bimodalRailOrderItemFilter = new FilterImpl<BimodalRailOrderEntity>();

		bimodalRailOrderItemFilter.addExpression(Restrictions.in(BimodalRailOrderEntity.ATTR_BIMODAL_RAIL_ORDER_ITEM_ID, railOrderItemIds));
		bimodalRailOrderItemFilter.addExpression(Restrictions.lt(BimodalRailOrderEntity.ATTR_ORDER_STATUS_CD, Constant.ORDER_STATUS_DELETED));
		bimodalRailOrderItemFilter.addExpression(Restrictions.eq(BimodalRailOrderEntity.ATTR_LANG_CD, languageCode));

		return bimodalRailOrderItemDao.find(bimodalRailOrderItemFilter);
	}

	private Filter<ContainerMonitoringEntity> getFilterForContainerMonitoringSearch(String prodPlantCd, Long sapMatId, List<Long> selectedShippingPoints, String langCd, ReadOnlyDao<ContainerMonitoringEntity> itemDao){
		Filter<ContainerMonitoringEntity> filter = itemDao.createFilter();

		if (sapMatId != null) {
			filter.addExpression(Restrictions.eq(ContainerMonitoringEntity.ATTR_SAP_MATERIAL_ID,
					sapMatId));
		}

		if (StringUtils.notEmpty(prodPlantCd) && prodPlantCd != "-1") {
			filter.addExpression(Restrictions.eq(
					ContainerMonitoringEntity.ATTR_PRODUCTION_PLANT_CD, prodPlantCd));
		}

		if (selectedShippingPoints != null && selectedShippingPoints.size() != 0) {
			filter.addExpression(Restrictions.in(ContainerMonitoringEntity.ATTR_SHIPPINGPOINT_ID,
					selectedShippingPoints));
		}

		if (StringUtils.notEmpty(langCd)) {
			filter.addExpression(Restrictions.eq(BizeventAuditDisplayEntity.ATTR_LANG_CD, langCd));
		}

		return filter;
	}

	public List<ContainerMonitoringEntity> fetchContainerMonitoringEntries(String prodPlantCd, Long sapMatId, List<Long> selectedShippingPoints, String orderByColumn, boolean asc, String langCd) {
		ReadOnlyDao<ContainerMonitoringEntity> itemDao = getDaoProvider().getContainerMonitoringEntityDao();
		Filter<ContainerMonitoringEntity> filter = getFilterForContainerMonitoringSearch(
				prodPlantCd, sapMatId, selectedShippingPoints, langCd, itemDao);

		if (StringUtils.notEmpty(orderByColumn)) {
			if (asc) {
				filter.addExpression(Order.asc(orderByColumn));
			}
			else {
				filter.addExpression(Order.desc(orderByColumn));
			}
		}

		return itemDao.find(filter);
	}

	public List<ShippingpointEntity> fetchContainerTerminalShippingPoints(){
		ReadOnlyDao<ShippingpointEntity> dao = getDaoProvider().getShippingpointEntityDao();
		Filter<ShippingpointEntity> filter = dao.createFilter();

		filter.addExpression(Restrictions.eq(ShippingpointEntity.ATTR_SHIPPPOINT_TYPE_CD,
				Constant.SHIPPINGPOINT_TYPE_CONTAINER));
		return dao.find(filter);
	}

	public ListRange<ProdPlantNameEntity> fetchContainerMonitoringPlantList(int skip, int num, String queryStr) {
		ListRange<ProdPlantNameEntity> result = new ListRange<ProdPlantNameEntity>();
		ReadOnlyDao<ContainerMonitoringEntity> containerMonitoringDao = getDaoProvider().getContainerMonitoringEntityDao();
		Filter<ContainerMonitoringEntity> srchFilter = containerMonitoringDao.createFilter();

		srchFilter.addExpression(Restrictions.ilike(ContainerMonitoringEntity.ATTR_PRODUCTION_PLANT_CD, "%" + queryStr
				+ "%"));

		final JPAQuery<ContainerMonitoringEntity> query = containerMonitoringDao.createJPAQuery();
		query.setDistinct(true);
		query.setFilter(srchFilter);
		query.addSelect(SelectExpression.simpleSelect(ContainerMonitoringEntity.ATTR_PRODUCTION_PLANT_CD));

		@SuppressWarnings("unchecked")
		final List<String> plantCodes = containerMonitoringDao.find(query);
		if (plantCodes.isEmpty()) {
			result.setData(new ProdPlantNameEntity[] {});
			result.setTotalSize(0);
			return result;
		}

		Filter<ProdPlantNameEntity> plantFilter = new FilterImpl<ProdPlantNameEntity>();
		plantFilter.addExpression(Restrictions.in(ProdPlantNameEntity.ATTR_PLANT_CD, plantCodes));
		plantFilter.addExpression(Order.asc(ProdPlantNameEntity.ATTR_PLANT_NAME));

		ReadOnlyDao<ProdPlantNameEntity> productionPlantDao = getDaoProvider().getProdPlantNameEntityDao();
		List<ProdPlantNameEntity> productionPlants = productionPlantDao.findNRecs(skip, num, plantFilter);

		ProdPlantNameEntity[] productionPlantArray = new ProdPlantNameEntity[productionPlants.size()];
		productionPlantArray = productionPlants.toArray(productionPlantArray);

		result.setData(productionPlantArray);
		result.setTotalSize(new Long(productionPlants.size()));

		return result;
	}

	public ListRange<SapMaterialDescEntity> fetchContainerMonitoringMaterials(int skip,
			int num, String queryStr, String languageCode) {
		ListRange<SapMaterialDescEntity> result = new ListRange<SapMaterialDescEntity>();

		ReadOnlyDao<ContainerMonitoringEntity> itemDao = getDaoProvider().getContainerMonitoringEntityDao();
		Filter<ContainerMonitoringEntity> srchFilter = itemDao.createFilter();

		srchFilter.addExpression(Restrictions.ilike(ContainerMonitoringEntity.ATTR_MATERIAL_NAME, "%" + queryStr
				+ "%"));
		final JPAQuery<ContainerMonitoringEntity> query = itemDao.createJPAQuery();
		query.setDistinct(true);
		query.setFilter(srchFilter);
		query.addSelect(SelectExpression.simpleSelect(ContainerMonitoringEntity.ATTR_SAP_MATERIAL_ID));

		@SuppressWarnings("unchecked")
		final List<Long> materialIds = itemDao.find(query);
		if (materialIds.isEmpty()) {
			result.setData(new SapMaterialDescEntity[] {});
			result.setTotalSize(0);
			return result;
		}
		final ReadOnlyDao<SapMaterialDescEntity> sapMaterialDescEntityDao = getDaoProvider().getSapMaterialDescEntityDao();
		Filter<SapMaterialDescEntity> materialFilter = new FilterImpl<SapMaterialDescEntity>();
		materialFilter.addExpression(Restrictions.in(SapMaterialDescEntity.ATTR_SAP_MATERIAL_ID, materialIds));
		materialFilter.addExpression(Order.asc(SapMaterialDescEntity.ATTR_TXT));
		materialFilter.addExpression(Restrictions.eq(SapMaterialDescEntity.ATTR_LANG_CD, languageCode));
		final List<SapMaterialDescEntity> materialDescriptions = sapMaterialDescEntityDao.findNRecs(skip, num,
				materialFilter);

		SapMaterialDescEntity[] materialDescriptionArray = new SapMaterialDescEntity[materialDescriptions.size()];
		materialDescriptionArray = materialDescriptions.toArray(materialDescriptionArray);
		result.setData(materialDescriptionArray);
		result.setTotalSize(new Long(materialDescriptions.size()));

		return result;
	}

	public List<ShiptoSareaRestrEntity> fetchShiptoRestrListForBimodalRailPart(
			Long fvlShippingPointId, Long materialId, String productionPlantCode, Long salesAreaId) {
		//added mainly because of the crazyjpa exception, but anyway one must see the bimodal rail part in order to be able to create the road part
		Authorizer.disableNextAccessRestriction();
		ReadWriteDao<ShiptoSareaRestrEntity> restrictionDao = getDaoProvider().getShiptoSareaRestrEntityDao();

		Filter<ShiptoSareaRestrEntity> restrictionFilter = restrictionDao.createFilter();

		restrictionFilter.addExpression(Restrictions.eq(ShiptoSareaRestrEntity.ATTR_SAP_MATERIAL_ID,
				materialId));
		restrictionFilter.addExpression(Restrictions.eq(ShiptoSareaRestrEntity.ATTR_PRODUCTION_PLANT_CD,
				productionPlantCode));
		restrictionFilter.addExpression(Restrictions.eq(ShiptoSareaRestrEntity.ATTR_SHIPTO_SAREA + "."
				+ ShiptoSareaEntity.ATTR_SAREA + "." + SareaEntity.ATTR_SAREA_ID, salesAreaId));
		restrictionFilter.addExpression(Restrictions.eq(ShiptoSareaRestrEntity.ATTR_SHIPTO_SAREA + "."
				+ ShiptoSareaEntity.ATTR_SHIPTO + "." + ShiptoEntity.ATTR_MAT_CHAIN_SHIPPINGPOINT
				+ "." + ShippingpointEntity.ATTR_SHIPPINGPOINT_ID, fvlShippingPointId));
		restrictionFilter.addExpression(Restrictions.eq(ShiptoSareaRestrEntity.ATTR_SHIPTO_SAREA + "."
				+ ShiptoSareaEntity.ATTR_SHIPTO + "." + ShiptoEntity.ATTR_SHIPTO_TYPE_CD,
				Constant.SHIPTO_TYPE_FVL));
		restrictionFilter.addExpression(Restrictions.eq(ShiptoSareaRestrEntity.ATTR_ENABLED_FLAG,
				Boolean.TRUE));
		restrictionFilter.addExpression(Restrictions.isNotNull(ShiptoSareaRestrEntity.ATTR_SAP_CONTRACT_ITEM_ID));

		List<ShiptoSareaRestrEntity> restrictions = restrictionDao.find(restrictionFilter);
		for (ShiptoSareaRestrEntity restriction : restrictions) {
			restriction.initialize();
		}

		return restrictions;
	}

	/**
	 * Maybe already exists somewhere
	 */
	public OrderItemEntity fetchOrderItem(Long orderItemId) {
		ReadWriteDao<OrderItemEntity> orderItemDao = getDaoProvider().getOrderItemEntityDao();
		return orderItemDao.findById(orderItemId);
	}

	/**
	 * Returns statuses according to the provided
	 * @return
	 */
	public List<OrderStatusTxEntity> getStatusList(List<Long> orderStatusCodes, String languageCode) {
		if (orderStatusCodes == null || orderStatusCodes.isEmpty()) {
			return new ArrayList<OrderStatusTxEntity>(0);
		}
		Filter<OrderStatusTxEntity> filter = new FilterImpl<OrderStatusTxEntity>();
		filter.addExpression(Restrictions.eq("orderStatusTxPK.langCd", languageCode));
		filter.addExpression(Restrictions.in("orderStatusTxPK.orderStatusBs.orderStatusCd", orderStatusCodes));
		filter.addExpression(Order.asc("orderStatusTxPK.orderStatusBs.sortingNo"));
		return referentialDataService.findTxEntityFiltered(OrderStatusTxEntity.class, filter);
	}

	/**
	 * @param day
	 * @param truckIdList
	 * @return
	 */
	public List<DispoDriverBreakListEntity> getBreaksForTruckOnDay(Long truckId, Date day) {
		if (truckId == null || day == null ) {
			return new ArrayList<DispoDriverBreakListEntity>();
		}
		ReadWriteDao<DriverEntity> driverDao = getDaoProvider().getDriverEntityDao();
		Filter<DriverEntity> filter = new FilterImpl<DriverEntity>();
		filter.addExpression(Restrictions.eq(DriverEntity.ATTR_CURRENT_TRUCK_ID, truckId));
		DriverEntity driver = driverDao.findSingle(filter);
		if ( driver == null ) {
			return new ArrayList<DispoDriverBreakListEntity>();
		}
		Long driverId = driver.getDriverId();
		List<DispoDriverBreakListEntity> breaks = DriverBreaksUtils.getBreaksIncludingLunchBreak(
				driverId, day, dispoService, configService);
		return breaks;
	}

	/**
	 * Fetches matplan by plantCd and sapMaterialId
	 */
	public MatPlantEntity fetchMatPlantEntity(String plantCode, Long sapMaterialId) {
		Filter<MatPlantEntity> filter = new FilterImpl<MatPlantEntity>();
		filter.addExpression(Restrictions.eq(MatPlantEntity.ATTR_SAP_MATERIAL_ID, sapMaterialId));
		filter.addExpression(Restrictions.eq(MatPlantEntity.ATTR_PLANT_CD, plantCode));
		ReadWriteDao<MatPlantEntity> matPlantDao = getDaoProvider().getMatPlantEntityDao();
		return matPlantDao.findSingle(filter);
	}

	/**
     * For certain sap materials there is a need to automatically add further sap materials to the order. Adding an
     * additional material depends mainly on the {@link MatPlantEntity} entry for material + production plant, but
     * basic availability criteria for the sap contract needs also to be met, in case the auto added material is not
     * available for in the contract, or for services in the sales area, then it cannot be added to the order.
     *
     * As parameter the function receives a set containing information to look up if autoadd material is necessary to be added
     * for the received {@link AutoaddMaterialFilterDTO}.
     * Returns a {@link AutoaddMaterialFilterDTO} representing the original sap material as key in case it needs an additional
     * material to be added to the order and a {@link AutoaddMaterialDTO} as value in case all data is available to successfully
     * add the autoadd material, if the material shoul be added, but no availability information could be found, then value will
     * null.
     */
    public Map<AutoaddMaterialFilterDTO, AutoaddMaterialDTO> fetchAutoaddMaterials(SareaEntity sarea, Long shiptoSapBpId, Long soldtoSapBpId, Long sapContractId, Set<AutoaddMaterialFilterDTO> autoAddMaterialFilterDTOs){

    	final Map autoAddMaterialMap = new HashMap<AutoaddMaterialFilterDTO, AutoaddMaterialDTO>();

    	Filter filter = new FilterImpl();
    	List<Expression> sapMaterialAtProductionPlantOrExpressions = new ArrayList<Expression>();

    	for (AutoaddMaterialFilterDTO autoAddCandidate : autoAddMaterialFilterDTOs) {
    		final Expression orExpression = Restrictions.and(
    				Restrictions.eq(MatPlantEntity.ATTR_SAP_MATERIAL_ID, autoAddCandidate.getSapMaterialId()),
    				Restrictions.eq(MatPlantEntity.ATTR_PLANT_CD, autoAddCandidate.getPlantCd()),
    				Restrictions.eq(MatPlantEntity.ATTR_AUTOADD_N_UOM_CD, autoAddCandidate.getUomCd()),
    				Restrictions.isNotNull(MatPlantEntity.ATTR_AUTOADD_1_PER_N_OF_UOM),
    				Restrictions.neq(MatPlantEntity.ATTR_AUTOADD_1_PER_N_OF_UOM, BigDecimal.ZERO),
    				Restrictions.isNotNull(MatPlantEntity.ATTR_AUTOADD_SAP_MATERIAL)
    		);
    		sapMaterialAtProductionPlantOrExpressions.add(orExpression);
    	}

    	if(!sapMaterialAtProductionPlantOrExpressions.isEmpty()) {
        	filter.addExpression(Restrictions.or(sapMaterialAtProductionPlantOrExpressions));
    	}

    	List<MatPlantEntity> materialPlantDefaults = getDaoProvider().getMatPlantEntityDao().find(filter);

    	if (!materialPlantDefaults.isEmpty()) {

    		Set<Long> sapMaterialIds = new HashSet<Long>();

    		for (MatPlantEntity materialPlantDefault : materialPlantDefaults) {
    			if(materialPlantDefault.getAutoaddSapMaterial() != null) {
        			sapMaterialIds.add(materialPlantDefault.getAutoaddSapMaterial().getSapMaterialId());
    			}
    		}

    		final Set<Long> validContractIds = deliverySettingService.loadValidContracts(sarea.getSareaId(), shiptoSapBpId, soldtoSapBpId, true);
    		final Map<Long, List<SapMatSareaContractEntity>> sapMaterialsInContracts;

    		if (!validContractIds.isEmpty()) {
    			boolean removeMaterialsWithZeroQuantity = true;
    			boolean allowOnlyGroupLeadersForQuantityControlledContract = true;
    			sapMaterialsInContracts = deliverySettingService.getContractMaterials(validContractIds, sapMaterialIds, allowOnlyGroupLeadersForQuantityControlledContract, removeMaterialsWithZeroQuantity, null);
    		}
    		else {
    			sapMaterialsInContracts = null;
    		}
    		final Set<Long> servicesNotInContract = deliverySettingService.getServicesNotInContract(sarea.getSorg().getSorgCd(), sarea.getDistributionChannel(), sapMaterialIds);

    		for (MatPlantEntity materialPlantDefault : materialPlantDefaults) {

    			final PlantBsEntity plant = materialPlantDefault.getPlant();
    			final SapMaterialEntity autoaddSapMaterial = materialPlantDefault.getAutoaddSapMaterial();
    			final SapMaterialEntity sapMaterial = materialPlantDefault.getSapMaterial();
				final UomBsEntity autoaddNUom = materialPlantDefault.getAutoaddNUom();
				final BigDecimal autoadd1PerNOfUom = materialPlantDefault.getAutoadd1PerNOfUom();

				if(autoaddNUom == null || autoadd1PerNOfUom == null) {
					continue;
				}

				final AutoaddMaterialFilterDTO filterDTO = new AutoaddMaterialFilterDTO(plant.getPlantCd(), sapMaterial.getSapMaterialId(), autoaddNUom.getUomCd());

	    		AutoaddMaterialDTO autoAddMaterialDTO = null;

				if (sapMaterialsInContracts != null && autoaddSapMaterial != null && sapMaterialsInContracts.containsKey(autoaddSapMaterial.getSapMaterialId())) {
	    			final List<SapMatSareaContractEntity> autoAddMaterialInContracts = sapMaterialsInContracts.get(autoaddSapMaterial.getSapMaterialId());

	    			for (SapMatSareaContractEntity autoAddMaterialInContract : autoAddMaterialInContracts) {

	    				if (autoAddMaterialInContract.getSapContractId().equals(sapContractId)) {
	    					SapContractItemEntity sapContractItem = sapContractItemStore.findById(autoAddMaterialInContract.getSapContractItemId());
	    					autoAddMaterialDTO = new AutoaddMaterialDTO(plant, autoaddSapMaterial, sapContractItem, autoadd1PerNOfUom);
	    					break;
	    				}
	    			}
	    		}
				else if (autoaddSapMaterial != null && servicesNotInContract.contains(autoaddSapMaterial.getSapMaterialId())) {
					autoAddMaterialDTO = new AutoaddMaterialDTO(plant, autoaddSapMaterial, null, autoadd1PerNOfUom);
				}

				autoAddMaterialMap.put(filterDTO, autoAddMaterialDTO);
    		}
    		return autoAddMaterialMap;
    	}
    	return null;
    }

	public SdoOnetimeDataEntity getOneTimeDataForOrder(Long orderId) {
		ReadOnlyDao<SdoOnetimeDataEntity> dao = getDaoProvider().getSdoOnetimeDataEntityDao();
		Filter<SdoOnetimeDataEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(SdoOnetimeDataEntity.ATTR_ORDER_ID, orderId));
		return dao.findSingle(filter);
	}

	public CustordOnetimeDataEntity getOneTimeDataForCustorder(Long customerOrderId) {
		ReadOnlyDao<CustordOnetimeDataEntity> dao = getDaoProvider().getCustordOnetimeDataEntityDao();
		Filter<CustordOnetimeDataEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(CustordOnetimeDataEntity.ATTR_CUSTORDER_ID,
				customerOrderId));
		return dao.findSingle(filter);
	}

	/**
	 * Used by Order taking to get txt data for the sap_material
	 *
	 * @param skip
	 * @param num
	 * @return
	 */
	public SapMaterialDescEntity fetchSapMaterialDesc(Long sapMaterialId, Locale locale) {
		ReadOnlyDao<SapMaterialDescEntity> sapMatDao = getDaoProvider().getSapMaterialDescEntityDao();
		Filter<SapMaterialDescEntity> filter = new FilterImpl<SapMaterialDescEntity>();
		filter.addExpression(Restrictions.eq(SapMaterialDescEntity.ATTR_LANG_CD,
				locale.getLanguage()));
		filter.addExpression(Restrictions.eq(SapMaterialDescEntity.ATTR_SAP_MATERIAL_ID,
				sapMaterialId));
		return sapMatDao.findSingle(filter);

	}

	public PlantBsEntity findPlantByCdNoAuthorization(String plantCd){
		Authorizer.disableNextAccessRestriction();
		return referentialDataService.findBsEntity(PlantBsEntity.class, plantCd);
	}

	public ShippingpointEntity findShippingpointByIdNoAuthorization(Long shippingpointId){
		Authorizer.disableNextAccessRestriction();
		return getDaoProvider().getShippingpointEntityDao().findById(shippingpointId);
	}

	public Boolean getShippingpointFvlFlag(Long shippingpointId){
		Authorizer.disableNextAccessRestriction();
		Boolean fvlFlag = shippingpointStore.getShippingpointFvlFlag(shippingpointId);
		return fvlFlag;
	}

	public LoadingpointEntity findLoadingpointByIdNoAuthorization(Long loadingpointId){
		Authorizer.disableNextAccessRestriction();
		return getDaoProvider().getLoadingpointEntityDao().findById(loadingpointId);
	}
}

