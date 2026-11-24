/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.eventbeans.in;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import javax.ejb.EJB;

import ch.nevis.gdi.jpa.BaseEntity;
import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Expression;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.beans.FieldsForRankEvaluationDTO;
import com.crh.logon.business.services.impl.ServiceSupport;
import com.crh.logon.business.services.interfaces.RefDataGenericsService;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.utils.CreateCustOrderFromDefaultMasterSecondRoundRankingComparator;
import com.crh.logon.business.services.utils.LogonEventContextBean;
import com.crh.logon.business.services.utils.LogonEventException;
import com.crh.logon.business.services.utils.LogonStatusUtils;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.stores.dictionaries.MopStatusStoreInterface;
import com.crh.logon.business.stores.domain.CarrierStoreInterface;
import com.crh.logon.business.stores.domain.ShippingPointStoreInterface;
import com.crh.logon.business.stores.sap.BusinessPartnerInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoBilltoEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import com.crh.logon.persistence.entities.refdata.MapSapUomBsEntity;
import com.crh.logon.persistence.entities.refdata.MopStatusTxEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.utils.RefDataUtil;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.time.DateTimeUtils;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.ResponseCodeName;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.holcim.logon.eventbeans.in.parallel_rail.ParallelRailResponseCode;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
public abstract class LogonEventRequestHandler extends ServiceSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LogonEventRequestHandler.class);

	protected static final String FIELD_VERLADEAUFTRAG_ID = "verladeauftragID";
	protected static final String FIELD_WERK_ID = "werkID";
	protected static final String FIELD_VERLADESTRASSE_ID = "verladestrasseID";
	protected static final String FIELD_LOGON_ORDER_ID = "logonOrderID";
	protected static final String FIELD_TARE_WEIGHT = "tareWeightKg";
	protected static final String FIELD_TOTAL_WEIGHT = "totalWeightKg";
	protected static final String FIELD_NET_WEIGHT = "netWeightKg";

	private static final Integer RANK_2 = 2;

	private static final Integer RANK_3 = 3;

	private static final Integer RANK_4 = 4;

	private static final Integer RANK_5 = 5;

	// this seems sketchy and it is, but it's only accessed during the call of handle(), from this instance only so it's safer
	protected LogonEventContextBean contextBean;
	protected Locale locale;

	@EJB
	private ShippingPointStoreInterface shippingPointStore;

	@EJB
	private MopStatusStoreInterface mopStatusStore;

	@EJB
	private BusinessPartnerInterface businessPartnerStore;

	@EJB
	RefDataGenericsServiceLocalInterface referentialDataService;

	@EJB
	private CarrierStoreInterface carrierStore;

	public LogonEventRequestHandler() {
		super();
	}

	protected static Date getDeliveryToDat(ShiptoEntity shipto, int defaultShiptoOpenMin, Date latestDeliveryTime, Date deliveryFromDat) {
		if (latestDeliveryTime != null)
		{
			return latestDeliveryTime;
		}
		else {
			/* Delivery time not specified.
			 * Rule: Choose closing time of shipto, on the first working day on or after the
			 * earliest delivery time.
			 */
			final Calendar deliveryFrom = Calendar.getInstance();
			deliveryFrom.setTime(deliveryFromDat);

			final Long openToMin = (Long) ObjectUtils.defaultIfNull(shipto.getOpen2To(), defaultShiptoOpenMin);
			final Calendar openToToday = DateTimeUtils.getHoursMinutesCal(openToMin);

			// Find the date of the closing time on the day of the earliest delivery timestamp.
			final Calendar openToOnEarliestDeliveryDay = Calendar.getInstance();
			openToOnEarliestDeliveryDay.setTimeInMillis(0);
			openToOnEarliestDeliveryDay.set(Calendar.YEAR, deliveryFrom.get(Calendar.YEAR));
			openToOnEarliestDeliveryDay.set(Calendar.MONTH, deliveryFrom.get(Calendar.MONTH));
			openToOnEarliestDeliveryDay.set(Calendar.DAY_OF_MONTH, deliveryFrom.get(Calendar.DAY_OF_MONTH));
			openToOnEarliestDeliveryDay.set(Calendar.HOUR, openToToday.get(Calendar.HOUR_OF_DAY));
			openToOnEarliestDeliveryDay.set(Calendar.MINUTE, openToToday.get(Calendar.MINUTE));

			if (openToOnEarliestDeliveryDay.before(deliveryFrom)
					|| openToOnEarliestDeliveryDay.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
					|| openToOnEarliestDeliveryDay.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
			{
				return DateTimeUtils.getNextWorkingDay(openToOnEarliestDeliveryDay).getTime();
			}
			else {
				return openToOnEarliestDeliveryDay.getTime();
			}
		}
	}

	protected LogonEventContextBean getContextBean() {
		return contextBean;
	}

	protected Locale getLocale() {
		return locale;
	}

	protected void validate(LogonEventRequest eventBean) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("No validation for " + eventBean.getClass().getSimpleName());
		}
		// subclasses may or may not override this one
	}

	protected abstract Locale getLocale(LogonEventRequest bean);

	protected abstract LogonEventResponse perform(LogonEventRequest eventBean);

	protected BigInteger getResponseCode(ResponseCodeName codeName) {

		switch ((int)contextBean.getInterfaceTypeCd()) {
		case (int)Constant.INTERFACE_TYPE_MOP_11:
			return Enum.valueOf(MopResponseCode.class, codeName.name()).value();
		case (int)Constant.INTERFACE_TYPE_PWS_10:
			return Enum.valueOf(ParallelRailResponseCode.class, codeName.name()).value();
		case (int)Constant.INTERFACE_TYPE_LOGON_LDS_INTERFACE_10:
			return Enum.valueOf(com.holcim.logon.eventbeans.in.logon_lds_interface.ResponseCode.class, codeName.name()).value();
		case (int)Constant.INTERFACE_TYPE_ORDER_CREATION_INTERFACE:
			return Enum.valueOf(com.holcim.logon.eventbeans.in.logon_oc_interface.ResponseCode.class, codeName.name()).value();
		default:
			throw new LogonException("Cannot determine status code for interface type " + contextBean.getInterfaceTypeCd(), Reason.RES_ERROR_IMPL);
		}
	}

	@AdnTrace
	public LogonEventResponse handle(LogonEventRequest eventBean, LogonEventContextBean contextBean) {
		try {
			this.contextBean = contextBean;
			LogonEventResponse result = null;

			try {

				locale = getLocale(eventBean);
				validate(eventBean);

				result = perform(eventBean);
				if (result == null) {
					throw newTechnicalError("Message not yet implemented!");
				}
				return result;

			}
			catch (LogonException e) {
				ExceptionUtil.errException(LOG, e, eventBean);
				throw newApplicationError(e.getMessage());
			}
		} finally {
			this.contextBean = null;
			locale = null;
		}
	}

	/**
	 * Creates an empty response using the statuscode and contextValue parameters.
	 * Txt field is populated with a formatted text using the parameters contextValue
	 * and fmtParams.
	 * 
	 * @param statusCode
	 * @param contextValue
	 * @param fmtParams
	 */
	protected abstract LogonEventResponse createLogonEventResponse(BigInteger statusCode, String contextValue, Object... fmtParams); // NOPMD

	/**
	 * Creates an empty response using the statuscode and contextValue parameters. 
	 * 
	 * @param statusCode
	 * @param contextValue
	 */
	protected abstract LogonEventResponse createBasicLogonEventResponse(BigInteger statusCode, String contextValue);

	/**
	 * Resolves the txt attribute of the response object provided.
	 * 
	 * @param response
	 * @param statusCode
	 * @param contextValue
	 * @param fmtParams
	 */
	protected void resolveMessageText(LogonEventResponse response, BigInteger statusCode, String contextValue, Object... fmtParams) { // NOPMD

		response.setTxt(getResponseMessage(statusCode, ArrayUtils.addAll(new Object[] { contextValue }, fmtParams)));

		return;
	}

	protected BigDecimal getKgAsTons(BigDecimal number) {
		if (number == null) {
			return null;
		}
		return number.movePointLeft(3).setScale(2, RoundingMode.HALF_UP);
	}


	protected LoadEntity getLoadByLoadOrderId(String fieldName, Long loadOrderId) {

		final LoadEntity load;
		if (loadOrderId != null) {
			final ReadOnlyDao<LoadEntity> dao = getDaoProvider().getLoadEntityDao();
			final Filter<LoadEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(LoadEntity.ATTR_LOAD_ORDER_ID, loadOrderId));
			load = dao.findSingle(filter);

			if (load == null) {
				throw newEntityNotFoundError(LoadEntity.class, fieldName, loadOrderId);
			}
		}
		else {
			load = null;
		}

		return load;
	}


	protected LoadEntity getLoadByVerladeauftragId(Long loadOrderId) {
		return getLoadByLoadOrderId(FIELD_VERLADEAUFTRAG_ID, loadOrderId);
	}

	protected LoadingpointEntity getLoadingpoint(String plantCd, String loadingpointNo) {
		if ((StringUtils.isNotEmpty(plantCd)) && (StringUtils.isNotEmpty(loadingpointNo))) {
			final ReadOnlyDao<LoadingpointEntity> dao = getDaoProvider().getLoadingpointEntityDao();
			final Filter<LoadingpointEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(LoadingpointEntity.ATTR_SHIPPINGPOINT + "." + ShippingpointEntity.ATTR_PLANT + "." + PlantBsEntity.ATTR_PLANT_CD, plantCd));
			filter.addExpression(Restrictions.eq(LoadingpointEntity.ATTR_WEIGH_SYSTEM_LOADINGPOINT_NO, loadingpointNo));
			filter.addExpression(Restrictions.eq(LoadingpointEntity.ATTR_WEIGH_SYSTEM_LP_CD, Constant.WEIGH_SYSTEM_LP_WS));
			filter.addExpression(Restrictions.eq(LoadingpointEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			final List<LoadingpointEntity> lpList = dao.find(filter);

			if ((lpList == null) || (lpList.isEmpty())) {
				throw newEntityNotFoundError(LoadingpointEntity.class, 
						LoadingpointEntity.ATTR_WEIGH_SYSTEM_LOADINGPOINT_NO, loadingpointNo, 
						"plantCd = " + plantCd);
			}

			if (lpList.size() > 1) {
				throw newApplicationError(String.format("Loadingpoint cannot be identified (multiple loadingpoints for %s=%s, %s=%s).",
						LoadingpointEntity.ATTR_WEIGH_SYSTEM_LOADINGPOINT_NO, loadingpointNo,
						"plantCd", plantCd));
			}

			return lpList.get(0);
		}
		else {
			return null;
		}
	}

	protected AssignmentPartEntity getAssignmentPart(LoadEntity load) {
		final ReadOnlyDao<AssignmentPartEntity> dao = getDaoProvider().getAssignmentPartEntityDao();
		final Filter<AssignmentPartEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_LOAD_ID, load.getLoadId()));
		return dao.findSingle(filter);
	}

	protected boolean isEmpty(Object value) {
		return value == null || ((value instanceof String) && (StringUtils.isEmpty((String) value)));
	}

	protected void validateMandatory(String fieldName, Object value) {
		if (isEmpty(value)) {
			throw newMandatoryFieldMissing(fieldName);
		}
	}

	protected void atLeastOneMandatory(String fieldName, String... values) { //NOPMD
		for (String value: values) {
			if (StringUtils.isNotEmpty(value)) {
				return;
			}
		}

		throw newMandatoryFieldMissing(String.format("From %s at least one should be specified!", fieldName), fieldName);
	}

	protected <T> void validateMandatoryListItems(String listFieldName, List<T> list, Function<T, String>... listItemCheckers) {
		if (list == null || list.isEmpty()) {
			throw newMandatoryFieldMissing(listFieldName);
		}
		else {
			ListIterator<T> iterator = list.listIterator();
			while(iterator.hasNext()) {
				T it = iterator.next();
				int index = iterator.previousIndex();
				for (Function<T, String> function : listItemCheckers) {
					String failedFieldName = function.apply(it);
					if (failedFieldName != null) {
						throw newMandatoryFieldMissing(failedFieldName + '[' + index + ']' + failedFieldName);
					}
				}
			}
		}
	}

	protected <T> Function<T, String> validateMandatoryFieldFunction(Function<T, ?> fieldFetcher, String fieldName) {
		return (item -> {
			Object fieldValue = fieldFetcher.apply(item);
			return isEmpty(fieldValue) ? fieldName : null;
		});
	}

	// TECHNICAL_ERROR

	protected LogonEventException newTechnicalError(String contextTxt) {
		return new LogonEventException(createBasicLogonEventResponse(getResponseCode(ResponseCodeName.TECHNICAL_ERROR), contextTxt));
	}

	// MANDATORY_FIELD_MISSING

	protected LogonEventException newMandatoryFieldEmpty(String fieldName, Object value) {
		throw newMandatoryFieldMissing(String.format("Field %s is required, but it is empty (value: %s)!", fieldName, value), fieldName);
	}

	protected LogonEventException newMandatoryFieldMissing(String fieldName) {
		throw newMandatoryFieldMissing(String.format("Field %s is required, but it is missing!", fieldName), fieldName);
	}

	protected LogonEventException newEntityNotFoundError(Class<? extends BaseEntity> entityClazz, String fieldName, Object value) {
		return newEntityNotFoundError(entityClazz, fieldName, value, null);
	}

	protected LogonEventException newEntityNotFoundError(Class<? extends BaseEntity> entityClazz, String fieldName, Object value, String additionalFieldTxt) {
		return newEntityNotFoundError(StringUtils.removeEnd(entityClazz.getSimpleName(), "Entity"), fieldName, value, additionalFieldTxt);
	}

	protected LogonEventException newEntityNotFoundError(String entityName, String fieldName, Object value) {
		return newEntityNotFoundError(entityName, fieldName, value, null);
	}

	protected LogonEventException newEntityNotFoundError(String entityName, String fieldName, Object value, String additionalFieldTxt) {
		final StringBuilder sb = new StringBuilder();
		sb.append("using ");
		sb.append(fieldName);
		sb.append(" = ");
		sb.append(value);

		LOG.error(sb + ", " + additionalFieldTxt);

		final String msg = String.format("%s not found in logon (%s).", 
				entityName,  
				sb.toString());
		return newMandatoryFieldError(msg, fieldName, value);
	}

	private LogonEventException newMandatoryFieldMissing(String contextTxt, String fieldName) {
		return new LogonEventException(createBasicLogonEventResponse(getResponseCode(ResponseCodeName.MANDATORY_FIELD_MISSING), contextTxt), fieldName);
	}

	// MANDATORY_FIELD_ERROR

	protected LogonEventException newFieldParseError(String fieldName, Object value) {
		throw newMandatoryFieldError(String.format("Unable to parse field %s (value: %s)!", fieldName, value), fieldName, value);
	}

	private LogonEventException newMandatoryFieldError(String contextTxt, String fieldName, Object value) {
		return new LogonEventException(createBasicLogonEventResponse(getResponseCode(ResponseCodeName.MANDATORY_FIELD_ERROR), contextTxt), fieldName, value);
	}

	// APPLICATION_ERROR

	protected LogonEventException newApplicationError(String contextTxt) {
		return newApplicationError(contextTxt, null);
	}

	protected LogonEventException newApplicationError(String contextTxt, String msg) {
		return new LogonEventException(createBasicLogonEventResponse(getResponseCode(ResponseCodeName.APPLICATION_ERROR), contextTxt), msg);
	}

	// SCHEDULING ERROR
	protected LogonEventException createSchedulingError(String contextTxt) {
		return new LogonEventException(createBasicLogonEventResponse(getResponseCode(ResponseCodeName.SCHEDULING_ERROR), contextTxt));
	}

	protected void verifyNoPlanningMaterial(List<OrderEntity> orders) {
		for (OrderEntity order : orders) {
			for (OrderItemEntity orderItem: order.getOrderItems()) {
				if (LogonUtil.isAggPlanningMaterial(orderItem.getSapMaterial())) {
					throw newApplicationError("Order [" + order.getOrderNumber()
							+ "] is still an unresolved agg planning material.");
				}
				else {
					boolean landfillPlanningMaterial = LogonUtil.isLandfillPlanningMaterial(orderItem.getSapMaterial());
					boolean arrivedAtUnloadingPointOrBeyond = LogonStatusUtils.isArrivedAtUnloadingPointOrBeyond(order.getOrderStatus().getOrderStatusCd());
					if (landfillPlanningMaterial && arrivedAtUnloadingPointOrBeyond) {
						throw newApplicationError("Order [" + order.getOrderNumber()
								+ "] is still an unresolved landfill planning material.");
					}
				}
			}
		}
	}

	public String getResponseMessage(LogonEventRequest request, BigInteger code, Object[] fmtParams) {

		// CRHLOGON-85: The locale is normally already set earlier, during the call to handle().
		// In case of a LOGON Mobile request, a call to getLocale() at this point returns only
		// the default locale, because the PdaSession will no longer be available at the time
		// the response is processed. To avoid this, we only assign the locale if it is not yet
		// set here.
		if (locale == null) {
			locale = getLocale(request);
		}
		return getResponseMessage(code, fmtParams);
	}

	private String getResponseMessage(BigInteger code, Object[] formatParameters) {

		MopStatusTxEntity mopStatusTx = mopStatusStore.findText(code.toString(), locale.getLanguage());
		return LogonUtil.resolveMopStatusText(locale, formatParameters, mopStatusTx);
	}

	protected final CarrierEntity getCarrier(String sapVendorIfcId, String sapVendorIfcIdFieldName) {

		if (sapVendorIfcId == null) {
			throw newMandatoryFieldMissing(sapVendorIfcIdFieldName);
		}

		final CarrierEntity carrier = carrierStore.findCarrierEntityByVendorSapIfcId(sapVendorIfcId);

		if (carrier == null) {
			throw newEntityNotFoundError(CarrierEntity.class, sapVendorIfcIdFieldName, sapVendorIfcId);
		}

		return carrier;

	}

	protected final CarrierEntity getCommercialCarrier(String sapVendorIfcId, String sapVendorIfcIdFieldName) {

		final CarrierEntity carrier = getCarrier(sapVendorIfcId, sapVendorIfcIdFieldName);

		if (!carrier.getSapVendor().getVnIsCommCarrier()) {
			String context = String.format("sapVendorId = %s", sapVendorIfcId);
			throw newApplicationError(context, "Received carrier is not a commercial carrier.");
		}

		return carrier;
	}

	protected final CarrierEntity getExecutingCarrier(String sapVendorIfcId, String sapVendorIfcIdFieldName) {

		final CarrierEntity carrier = getCarrier(sapVendorIfcId, sapVendorIfcIdFieldName);

		if (!carrier.getSapVendor().getVnIsExecCarrier()) {
			String context = String.format("sapVendorId = %s", sapVendorIfcId);
			throw newApplicationError(context, "Received carrier is not an executing carrier.");
		}

		return carrier;
	}

	protected final SapMaterialEntity getMaterial(String materialNo, String materialNoFieldName) {
		final ReadOnlyDao<SapMaterialEntity> sapMaterialDao = getDaoProvider().getSapMaterialEntityDao();
		final Filter<SapMaterialEntity> sapMaterialFilter = sapMaterialDao.createFilter();
		sapMaterialFilter.addExpression(Restrictions.eq(SapMaterialEntity.ATTR_SAP_MATERIAL_IFC_NO, materialNo));
		final SapMaterialEntity sapMaterial = sapMaterialDao.findSingle(sapMaterialFilter);

		if (sapMaterial == null) {
			throw newEntityNotFoundError(SapMaterialEntity.class, materialNoFieldName, materialNo);
		}

		return sapMaterial;
	}

	/**
	 * Try to find the received UoM first in TLGNR_MAP_SAP_UOM and maps it to our LOGON UoM if found.
	 * As a fallback, look it up directly in TLGNR_UOM_BS.
	 * @param weightUom The UoM received in the request
	 * @param uomFieldName The field name in the request
	 * @return The LOGON UoM which corresponds to the UoM received
	 */
	protected final UomBsEntity getUom(String weightUom, String uomFieldName) {
		return getUom(weightUom, uomFieldName, Boolean.FALSE);
	}

	/**
	 * Try to find the received UoM first in TLGNR_MAP_SAP_UOM and maps it to our LOGON UoM if found.
	 * As a fallback, look it up directly in TLGNR_UOM_BS.
	 * @param weightUom The UoM received in the request
	 * @param uomFieldName The field name in the request
	 * @return The LOGON UoM which corresponds to the UoM received
	 */
	protected final UomBsEntity getUom(String weightUom, String uomFieldName, Boolean fuzzyAllowed) {
		final MapSapUomBsEntity mapUom = referentialDataService.findBsEntity(MapSapUomBsEntity.class, weightUom);
		if (mapUom != null) {
			return mapUom.getUom();
		}
		else {
			final Filter<UomBsEntity> filter = new FilterImpl<UomBsEntity>();
			filter.addExpression(Restrictions.eq(UomBsEntity.ATTR_UOM_CD, weightUom));
			filter.addExpression(Restrictions.eq(UomBsEntity.ATTR_CTL_ACT_CD, Boolean.TRUE));
			if (!fuzzyAllowed) {
				filter.addExpression(Restrictions.eq(UomBsEntity.ATTR_FUZZY_QUANTITY_FLAG, Boolean.FALSE));
			}
			final UomBsEntity uom = RefDataUtil.findUomBsEntity(em, filter);
			if (uom == null) {
				throw newEntityNotFoundError(UomBsEntity.class, uomFieldName, weightUom);
			}
			return uom;
		}
	}

	/**
	 * Looks up a {@link PlantBsEntity} using the {@link RefDataGenericsService} by the provided plantNo
	 * @param plantNo
	 * @return loaded entity
	 * @throws LogonEventException if the entity cannot be found
	 */
	protected final PlantBsEntity getPlant(String plantNo, String plantNoFieldName) {
	    final PlantBsEntity supplyPlant = referentialDataService.findBsEntity(PlantBsEntity.class, plantNo);
    	if (supplyPlant == null) {
			throw newEntityNotFoundError(PlantBsEntity.class, plantNoFieldName, plantNo);
	    }
	    return supplyPlant;
    }

	/**
	 * Loads a {@link SapContractItemEntity} based on the contractItemNumber and the contract
	 * @param contractItemNumber
	 * @param contract
	 * @return loaded entity
	 * @throws LogonEventException if entity cannot be found
	 */
	protected final SapContractItemEntity getSapContractItemEntity(SapContractEntity contract, String contractItemNumber, String fieldName) {
		if (contractItemNumber == null) {
			return null;
		}
		Long contractItemNumberL = Long.valueOf(contractItemNumber);
		return getSapContractItemEntity(contract, contractItemNumberL, fieldName);
	}

	protected final SapContractItemEntity getSapContractItemEntity(SapContractEntity contract, Long contractItemNumber, String fieldName) {
		if ((contract == null) || (contractItemNumber == null)) {
			return null;
		}
    	final ReadOnlyDao<SapContractItemEntity> dao = getDaoProvider().getSapContractItemEntityDao();
    	final Filter<SapContractItemEntity> filter = dao.createFilter();
	    filter.addExpression(Restrictions.eq(SapContractItemEntity.ATTR_SAP_CONTRACT, contract));
	    filter.addExpression(Restrictions.eq(SapContractItemEntity.ATTR_CONTRACT_ITEM_NO, contractItemNumber));

	    SapContractItemEntity contractItem = dao.findSingle(filter);
	    if (contractItem == null) {
	    	throw newEntityNotFoundError(SapContractItemEntity.class, fieldName, contractItemNumber, "SapContract = " + contract.getSapContractNo());
	    }

		boolean quantityControlledContract = LogonUtil.isQuantityControlledContract(contract);
		if (quantityControlledContract && !contractItem.getIsApGroupLeader()) {
			contractItem = contractItem.getApGroupLeader();
		}

		return contractItem;
	}

	protected final SapBpEntity getSoldTo(String sapIfcId, String sapIfcIdFieldName) {
		final SapBpEntity soldtoSapBp = businessPartnerStore.getSoldToSapBpEntityBySapIfcId(sapIfcId);
		if (soldtoSapBp == null) {
	    	throw newEntityNotFoundError("SoldTo", sapIfcIdFieldName, sapIfcId);
	    }

		return soldtoSapBp;
	}

	protected final ShiptoEntity getShipTo(String sapIfcId, String sapIfcIdFieldName) {
		final ShiptoEntity shipto = businessPartnerStore.getShiptoEntityBySapIfcId(sapIfcId);
		if (shipto == null) {
			throw newEntityNotFoundError("ShipTo", sapIfcIdFieldName, sapIfcId);
	    }
		return shipto;
	}

	protected List<ShiptoSareaRestrEntity> getHighestRankMatch(List<ShiptoSareaRestrEntity> shiptoSareaRestrEntities) {
		Map<Integer, List<ShiptoSareaRestrEntity>> filterSSRestrByRank = new HashMap<Integer, List<ShiptoSareaRestrEntity>>();
		for(ShiptoSareaRestrEntity ssRest : shiptoSareaRestrEntities) {
			if (ssRest.getShippingpoint() != null && ssRest.getProductionPlant() != null) {
				putSSRestToRankMap(RANK_2, filterSSRestrByRank, ssRest);
			}
			else if (ssRest.getShippingpoint() != null && ssRest.getProductionPlant() == null) {
				putSSRestToRankMap(RANK_3, filterSSRestrByRank, ssRest);
			}
			else if (ssRest.getShippingpoint() == null && ssRest.getProductionPlant() != null) {
				putSSRestToRankMap(RANK_4, filterSSRestrByRank, ssRest);
			}
			else {
				putSSRestToRankMap(RANK_5, filterSSRestrByRank, ssRest);
			}
		}
		Integer maxRank = Collections.min(filterSSRestrByRank.keySet());


		return filterSSRestrByRank.get(maxRank);
	}

	private void putSSRestToRankMap(Integer rank, Map<Integer, List<ShiptoSareaRestrEntity>> filterSSRestrByRank,
			ShiptoSareaRestrEntity ssRest) {
		if(filterSSRestrByRank.get(rank) == null){
			filterSSRestrByRank.put(rank, new ArrayList<ShiptoSareaRestrEntity>(Arrays.asList(ssRest)));
		}
		else {
			filterSSRestrByRank.get(rank).add(ssRest);
		}
	}

	protected ShiptoSareaRestrEntity getHighestRankFromSecondRoundEvaluation(List<ShiptoSareaRestrEntity> matchedSSRestrictions) {
		//actually get the lowest rank number, but theoretically it is the highest rank
		return Collections.max(matchedSSRestrictions, new CreateCustOrderFromDefaultMasterSecondRoundRankingComparator());
	}

	protected List<ShiptoSareaRestrEntity> getMatchedSSRestrictions(FieldsForRankEvaluationDTO fieldsForRankEvaluationDTO) {
		final ReadOnlyDao<ShiptoSareaRestrEntity> dao = getDaoProvider().getShiptoSareaRestrEntityDao();
		Filter<ShiptoSareaRestrEntity> f = dao.createFilter();

		Expression mandatoryRestrictions = Restrictions.and(
				Restrictions.eq(ShiptoSareaRestrEntity.ATTR_ENABLED_FLAG, Boolean.TRUE),
				Restrictions.eq(ShiptoSareaRestrEntity.ATTR_DELIVERY_TYPE_CD, fieldsForRankEvaluationDTO.getDeliveryTypeCd()),
				Restrictions.eq(ShiptoSareaRestrEntity.ATTR_SHIPTO_SAREA_ID,fieldsForRankEvaluationDTO.getShiptoSareaId()),
				Restrictions.eq(ShiptoSareaRestrEntity.ATTR_SAP_MATERIAL_ID, fieldsForRankEvaluationDTO.getSapMaterialId()),
				Restrictions.eq(ShiptoSareaRestrEntity.ATTR_SAP_CONTRACT_ITEM_ID, fieldsForRankEvaluationDTO.getSapContractItemId()));

		Expression mandatoryRestrictionsOnlyForExactMatch = Restrictions.and(
				Restrictions.eq(ShiptoSareaRestrEntity.ATTR_PRODUCTION_PLANT_CD,fieldsForRankEvaluationDTO.getProductionPlantCd()),
				Restrictions.eq(ShiptoSareaRestrEntity.ATTR_SHIPPINGPOINT_ID, fieldsForRankEvaluationDTO.getShippingpointId()),
				Restrictions.eq(ShiptoSareaRestrEntity.ATTR_LOADINGPOINT_ID, fieldsForRankEvaluationDTO.getLoadingpointId()),
				Restrictions.eq(ShiptoSareaRestrEntity.ATTR_UOM_CD, fieldsForRankEvaluationDTO.getUomCd()));

		Expression restrictionsOnlyForRank2Match = Restrictions.and(
				Restrictions.or(
						Restrictions.eq(ShiptoSareaRestrEntity.ATTR_PRODUCTION_PLANT_CD,fieldsForRankEvaluationDTO.getProductionPlantCd()),
						Restrictions.isNull(ShiptoSareaRestrEntity.ATTR_PRODUCTION_PLANT_CD)),
				Restrictions.or(
						Restrictions.eq(ShiptoSareaRestrEntity.ATTR_SHIPPINGPOINT_ID, fieldsForRankEvaluationDTO.getShippingpointId()),
						Restrictions.isNull(ShiptoSareaRestrEntity.ATTR_SHIPPINGPOINT_ID)
						)
		);

		f.addExpression(mandatoryRestrictions);
		f.addExpression(mandatoryRestrictionsOnlyForExactMatch);
		List<ShiptoSareaRestrEntity> shiptoSareaRestrEntities = dao.find(f);
		//if exact match found then return
		if(!shiptoSareaRestrEntities.isEmpty()) {
			return shiptoSareaRestrEntities;
		}

		//if no exact match found then go on with less strict matching rules
		f = dao.createFilter();
		f.addExpression(mandatoryRestrictions);
		f.addExpression(restrictionsOnlyForRank2Match);
		shiptoSareaRestrEntities = dao.find(f);
		if(shiptoSareaRestrEntities.size() == 1) {
			return shiptoSareaRestrEntities;
		}
		else if (!shiptoSareaRestrEntities.isEmpty()) {
			//As more then 1 non-exact match found, therefore we should find the highest ranking one(s)
			return getHighestRankMatch(shiptoSareaRestrEntities);
		}
		return shiptoSareaRestrEntities;
	}

	protected final ShippingpointEntity getShippingpoint(String sapShippingpointNo, PlantBsEntity supplyPlant) {

		final String sapShippingPointNo = StringUtils.defaultString(sapShippingpointNo, supplyPlant.getPlantCd());

		final ShippingpointEntity shippingPoint = shippingPointStore
				.getShippingpointEntityByPlantCdAndSapNo(supplyPlant, sapShippingPointNo);

		if (shippingPoint == null) {
	    	throw newEntityNotFoundError(ShippingpointEntity.class,
	    			ShippingpointEntity.ATTR_SAP_SHIPPINGPOINT_NO, sapShippingPointNo,
	    			ShippingpointEntity.ATTR_PLANT_CD + " = " + supplyPlant.getPlantCd());
	    }

	    return shippingPoint;
    }


	protected CarrierEntity getCommCarrier(Optional<String> commCarrierSapId, Optional<String> execCarrierSapId,
			String commCarrierSapVendorIfcIdFieldName, String execCarrierSapVendorIfcIdFieldName) {
		CarrierEntity result = null;
		if (commCarrierSapId.isPresent() && StringUtils.isNotEmpty(commCarrierSapId.get())) {

			result = getCommercialCarrier(commCarrierSapId.get(),
					commCarrierSapVendorIfcIdFieldName);
		}
		else if (execCarrierSapId.isPresent() && StringUtils.isNotEmpty(execCarrierSapId.get())) {

			//if the commercial carrier is not set, but executing is, try to use as commercial
			CarrierEntity candidate = getCarrier(execCarrierSapId.get(), commCarrierSapVendorIfcIdFieldName);
			if (candidate.getSapVendor().getVnIsCommCarrier()) {
				result = candidate;
			}
		}
		return result;
	}

	/**
	 * If the payer attribute is null in the {@link} orderData the soldTo will be returned.
	 * @param payerSapId
	 */
	protected final SapBpEntity getPayerSapBp(String payerSapId, String payerFieldName, SapBpEntity soldtoSapBp,
			SareaEntity sarea) {
		final SapBpEntity payerSapBp;
		if (payerSapId == null || StringUtils.isEmpty(payerSapId)) {
	    	if (BooleanUtils.isTrue(soldtoSapBp.getBpIsPayer())) {
	    		payerSapBp = soldtoSapBp;
	    	}
	    	else {
				final List<SapBpEntity> payerSapBpList = businessPartnerStore.loadPayers(sarea.getSareaId(), soldtoSapBp.getSapBpId());
				if ((payerSapBpList == null) || (payerSapBpList.isEmpty())) {
		    		throw newApplicationError(String.format(
		    				"Payer (%s) is not exactly specified, unable to use soldTo (%s) as payer, and no payers defined for the salesarea (id: %d, %s).",
							payerFieldName,
		    				soldtoSapBp.getSapIfcId(),
		    				sarea.getSareaId(),
		    				sarea.getSareaDisplayLongname()
		    		));
	    		}
	    		if (payerSapBpList.size() > 1) {
		    		throw newApplicationError(String.format(
		    				"Payer (%s) is not exactly specified, unable to use soldTo (%s) as payer, and multiple payers defined for the salesarea (id: %d, %s).",
							payerFieldName,
		    				soldtoSapBp.getSapIfcId(),
		    				sarea.getSareaId(),
		    				sarea.getSareaDisplayLongname()
		    		));
	    		}
	    		payerSapBp = payerSapBpList.iterator().next();
	    	}
	    }
		else {
			final String sapIfcId = payerSapId;
			payerSapBp = businessPartnerStore.getPayerSapBpEntityBySapId(sapIfcId);
		}
		return payerSapBp;
	}

	/**
	 * If the billTo attribute is null in the {@link} orderData the soldTo will be returned.
	 * @param billToSapId
	 */
	protected final SapBpEntity getBillToSapBp(String billToSapId, String billToFieldName,
			SapBpEntity soldtoSapBp,
			SareaEntity sarea, ShiptoEntity shipto) {
		final SapBpEntity billtoSapBp;
		if (billToSapId == null || StringUtils.isEmpty(billToSapId)) {
	    	if (BooleanUtils.isTrue(soldtoSapBp.getBpIsBillto())) {
	    		billtoSapBp = soldtoSapBp;
	    	}
	    	else {
				final List<ShiptoBilltoEntity> billToSapBpList = businessPartnerStore.loadBilltos(shipto.getShiptoId(), sarea.getSareaId());
				if ((billToSapBpList == null) || (billToSapBpList.isEmpty())) {
		    		throw newApplicationError(String.format(
		    				"BillTo (%s) is not exactly specified, unable to use soldTo (%s) as billTo, and no billTos defined for the salesarea (id: %d, %s; shipto: %s).",
							billToFieldName,
		    				soldtoSapBp.getSapIfcId(),
		    				sarea.getSareaId(),
		    				sarea.getSareaDisplayLongname(),
		    				shipto.getShiptoId()
		    		));
	    		}
	    		if (billToSapBpList.size() > 1) {
		    		throw newApplicationError(String.format(
		    				"BillTo (%s) is not exactly specified, unable to use soldTo (%s) as billTo, and multiple billTos defined for the salesarea (id: %d, %s; shipto: %s).",
							billToFieldName,
		    				soldtoSapBp.getSapIfcId(),
		    				sarea.getSareaId(),
		    				sarea.getSareaDisplayLongname(),
		    				shipto.getShiptoId()
		    		));
	    		}
	    		billtoSapBp = billToSapBpList.iterator().next().getBilltoSapBp();
	    	}
	    }
	    else {
			final String sapIfcId = billToSapId;
			billtoSapBp = businessPartnerStore.getBilltoSapBpEntityBySapIfcId(sapIfcId);
			if (billtoSapBp == null) {
				throw newEntityNotFoundError("BillTo", billToFieldName, sapIfcId);
		    }
	    }
		return billtoSapBp;
	}
}

