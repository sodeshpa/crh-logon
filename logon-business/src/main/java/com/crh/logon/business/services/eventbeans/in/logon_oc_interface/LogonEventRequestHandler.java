/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.eventbeans.in.logon_oc_interface;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import javax.ejb.EJB;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.beans.UomConversionResult;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UomConverterServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.refdata.BizeventTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.utils.LogonConversionException;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.logon_oc_interface.BusinessPartnerContactData;
import com.holcim.logon.eventbeans.in.logon_oc_interface.Request;
import com.holcim.logon.eventbeans.in.logon_oc_interface.Response;
import com.holcim.logon.eventbeans.in.logon_oc_interface.ResponseCode;
import org.apache.commons.lang.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
public abstract class LogonEventRequestHandler extends com.crh.logon.business.services.eventbeans.in.LogonEventRequestHandler {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LogonEventRequestHandler.class);

	protected static final String FIELD_CLIENT_TYPE = "header.clientType";
	protected static final String CLIENT_TYPE_CUSTOMER_PORTAL = "RS CP";

	@EJB RefDataGenericsServiceLocalInterface referentialDataService;
	@EJB UomConverterServiceLocalInterface unitOfMeasurementConverterService;


	public LogonEventRequestHandler() {
		super();
	}

	@Override
	protected final Locale getLocale(LogonEventRequest bean) {
		return new Locale(((Request) bean).getLanguage());
	}

	protected final <T extends LogonEventResponse> T createLogonOKEventResponse(Class<T> clazz) {
		T response;
		try {
			response = clazz.newInstance();
		}
		catch (Exception e) {

			throw new LogonException("Error creating new LogonEventResponse", Reason.RES_ERROR_IMPL);
		}
		BigInteger responseCode = ResponseCode.OK.value();
		response.setCode(responseCode);
		response.setContextValue(null);
		resolveMessageText(response, responseCode, null, (Object[]) null);
		return response;
	}

	@Override
	protected final LogonEventResponse createLogonEventResponse(BigInteger statusCode, String contextValue, Object... fmtParams) { // NOPMD

		LogonEventResponse response = createBasicLogonEventResponse(
				statusCode, contextValue);

		resolveMessageText(response, statusCode, contextValue, fmtParams);

		return response;
	}

	@Override
	protected Response createBasicLogonEventResponse(
			BigInteger statusCode, String contextValue) {
		Response response = null;
		try {
			response = Response.class.newInstance();
		}
		catch (Exception e) {

			throw new LogonException("Error creating new LogonEventResponse", Reason.RES_ERROR_IMPL);
		}
		response.setCode(statusCode);
		response.setContextValue(contextValue);
		return response;
	}

	protected final Long parse(String fieldValue, String fieldName, boolean required) {
		if (StringUtils.isNotEmpty(fieldValue)) {
			try {
				return Long.parseLong(fieldValue);
			}
			catch (NumberFormatException e) {
				throw newFieldParseError(fieldName, fieldValue);
			}
		}
		else if (required) {
			throw newMandatoryFieldEmpty(fieldName, fieldValue);
		}
		else {
			return null;
		}
	}


	protected final BizeventEntity createBizevent(long eventType, Date eventTs, boolean persist) {
		BizeventEntity bizevent = new BizeventEntity();

		final BizeventTypeBsEntity type = new BizeventTypeBsEntity();
		type.setBizeventTypeCd(eventType);

		bizevent.setBizeventType(type);
		bizevent.setEventTs(eventTs);

		if (persist) {
			final ReadWriteDao<BizeventEntity> dao = getDaoProvider().getBizeventEntityDao();
			bizevent = dao.persist(bizevent, true);
		}

		return bizevent;
	}


	protected final BigDecimal getAsT(BigDecimal quantity, UomBsEntity uom, SapMaterialEntity material) {

		final BigDecimal result;

		final UomBsEntity uomT = referentialDataService.findBsEntity(UomBsEntity.class, Constant.UOM_TONS);
		final UomConversionResult ucr;
		try {
			ucr = unitOfMeasurementConverterService.convert(quantity, uom, uomT, material, null, null, false);
		}
		catch (LogonConversionException e) {
			final String msg = "Could not convert from UOM " + uom + " to tons for material " +
					material + " (" + e.getMessage() + ")";
			LOG.error(msg);
			throw new LogonException(msg, e);
		}
		result = ucr.getQuantity();

		return result;
	}

	protected void validateMandatory(String fieldName, Object value) {
		if (isEmpty(value)) {
			throw newMandatoryFieldMissing(fieldName);
		}
	}

	protected void preProcessForOneTimeSoldToAndShipTo(CustorderEntity custorderEntity,
			BusinessPartnerContactData soldToContactData, BusinessPartnerContactData shipToContactData) {
		final Collection<OrderEntity> orders = custorderEntity.getOrders();
		for (OrderEntity order : orders) {

			if (soldToContactData != null) {
				order.setOneTimeSoldtoName(soldToContactData.getName1());
				order.setOneTimeSoldtoStreetHouseno(soldToContactData.getStreet());
				order.setOneTimeSoldtoCity(soldToContactData.getCity());
				order.setOneTimeSoldtoPostalCode(soldToContactData.getZipCode());
				order.setOneTimeSoldtoCountry(soldToContactData.getCountry());
				order.setOneTimeSoldtoVatNo(soldToContactData.getVatRegistrationNumber());
			}

			if (shipToContactData != null) {
				order.setLnfSiteName(shipToContactData.getName1());
				order.setLnfSiteStreetHouseNo(shipToContactData.getStreet());
				order.setLnfSiteCountry(shipToContactData.getCountry());
				order.setLnfSitePostalCode(shipToContactData.getZipCode());
				order.setLnfSiteCity(shipToContactData.getCity());
				order.setLnfSiteVatNo(shipToContactData.getVatRegistrationNumber());
			}
		}
	}
}

