/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.logon_lds_interface.webservices;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interfaces.LogonEventService;
import com.crh.logon.business.services.localinterfaces.ConfigServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.LocalServices;
import com.crh.logon.business.services.utils.LogonEventException;
import com.crh.logon.business.services.utils.LogonEventSimulationException;
import com.crh.logon.constants.Constant;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.web.utils.ServiceLocatorProvider;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.logon_lds_interface.AttorneyData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.BusinessPartnerData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.CompletedLoadOrderItemData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.CreatedOrder;
import com.holcim.logon.eventbeans.in.logon_lds_interface.CreatedOrderData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.DispatcherData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.DriverData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.EndLoadingPartData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.EndLoadingPartData.AffectedOrders;
import com.holcim.logon.eventbeans.in.logon_lds_interface.EndLoadingRequest.ReturnPallets;
import com.holcim.logon.eventbeans.in.logon_lds_interface.FirstWeighingData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.LoadOrderData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.LoadOrderItemData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.LoadedMaterialData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.LocationData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.MaterialData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.ObjectFactory;
import com.holcim.logon.eventbeans.in.logon_lds_interface.OrderData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.OrderItemData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.OrderItemData.PossibleMaterial;
import com.holcim.logon.eventbeans.in.logon_lds_interface.PriceData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.ReducedLoadOrderData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.ReducedLoadOrderItemData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.ReducedOrderData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.ReducedOrderItemData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.Request;
import com.holcim.logon.eventbeans.in.logon_lds_interface.Response;
import com.holcim.logon.eventbeans.in.logon_lds_interface.ResponseCode;
import com.holcim.logon.eventbeans.in.logon_lds_interface.SchedulingData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.SecondWeighingData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.SelectLoadOrderForLoadingRequest.OrderValidation;
import com.holcim.logon.eventbeans.in.logon_lds_interface.ServiceMaterialData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.ShiptoMaterialData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.VehicleData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.VendorData;
import com.holcim.logon.logonldsinterface.common_in.DeliveryType_type3;
import com.holcim.logon.logonldsinterface.common_in.Header;
import com.holcim.logon.logonldsinterface.common_in.LdsRequestId_type1;
import com.holcim.logon.logonldsinterface.common_in.LogonOrderItemNumber_type3;
import com.holcim.logon.logonldsinterface.common_in.LogonOrderNo_type5;
import com.holcim.logon.logonldsinterface.common_in.MaterialNo_type7;
import com.holcim.logon.logonldsinterface.common_in.OrderItemReturnData;
import com.holcim.logon.logonldsinterface.common_in.OrderListData;
import com.holcim.logon.logonldsinterface.common_in.OrderListItemData;
import com.holcim.logon.logonldsinterface.common_in.OrderValidationCode_type5;
import com.holcim.logon.logonldsinterface.common_in.QuantityUom_type3;
import com.holcim.logon.logonldsinterface.common_in.Quantity_type3;
import com.holcim.logon.logonldsinterface.common_in.ShippingpointNo_type5;
import com.holcim.logon.logonldsinterface.common_in.SorgCd_type1;
import com.holcim.logon.logonldsinterface.common_in.SupplyPlantNo_type5;
import com.holcim.logon.logonldsinterface.services.AddReturnOrderRequest;
import com.holcim.logon.logonldsinterface.services.AddReturnOrderResponse;
import com.holcim.logon.logonldsinterface.services.AddServiceMaterialsToOrderRequest;
import com.holcim.logon.logonldsinterface.services.AddServiceMaterialsToOrderResponse;
import com.holcim.logon.logonldsinterface.services.CancelLoadingRequest;
import com.holcim.logon.logonldsinterface.services.CancelLoadingResponse;
import com.holcim.logon.logonldsinterface.services.CloseCustomerOrderRequest;
import com.holcim.logon.logonldsinterface.services.CloseCustomerOrderResponse;
import com.holcim.logon.logonldsinterface.services.CreateCompletedCustomerOrderRequest;
import com.holcim.logon.logonldsinterface.services.CreateCompletedCustomerOrderResponse;
import com.holcim.logon.logonldsinterface.services.CreateCustomerOrderBasedOnMasterDataDefaultRequest;
import com.holcim.logon.logonldsinterface.services.CreateCustomerOrderBasedOnMasterDataDefaultResponse;
import com.holcim.logon.logonldsinterface.services.CreateCustomerOrderRequest;
import com.holcim.logon.logonldsinterface.services.CreateCustomerOrderResponse;
import com.holcim.logon.logonldsinterface.services.EncodedOrderNumbers_type3;
import com.holcim.logon.logonldsinterface.services.EndLoadingRequest;
import com.holcim.logon.logonldsinterface.services.EndLoadingResponse;
import com.holcim.logon.logonldsinterface.services.GetLoadOrdersForVehicleRequest;
import com.holcim.logon.logonldsinterface.services.GetLoadOrdersForVehicleResponse;
import com.holcim.logon.logonldsinterface.services.GetMaterialsForShiptoRequest;
import com.holcim.logon.logonldsinterface.services.GetMaterialsForShiptoResponse;
import com.holcim.logon.logonldsinterface.services.GetOrdersForCarriersRequest;
import com.holcim.logon.logonldsinterface.services.GetOrdersForCarriersResponse;
import com.holcim.logon.logonldsinterface.services.GetPickupLoadOrdersForShiptoRequest;
import com.holcim.logon.logonldsinterface.services.GetPickupLoadOrdersForShiptoResponse;
import com.holcim.logon.logonldsinterface.services.RegularizationRequest;
import com.holcim.logon.logonldsinterface.services.RegularizationResponse;
import com.holcim.logon.logonldsinterface.services.ReleaseAssignmentRequest;
import com.holcim.logon.logonldsinterface.services.ReleaseAssignmentResponse;
import com.holcim.logon.logonldsinterface.services.ScheduleAssignmentRequest;
import com.holcim.logon.logonldsinterface.services.ScheduleAssignmentResponse;
import com.holcim.logon.logonldsinterface.services.SelectLoadOrderForLoadingRequest;
import com.holcim.logon.logonldsinterface.services.SelectLoadOrderForLoadingResponse;
import com.holcim.logon.logonldsinterface.services.SetLoadOrder2LoadedRequest;
import com.holcim.logon.logonldsinterface.services.SetLoadOrder2LoadedResponse;
import com.holcim.logon.logonldsinterface.services.StartLoadingRequest;
import com.holcim.logon.logonldsinterface.services.StartLoadingResponse;
import com.holcim.logon.logonldsinterface.services.TotalNumberOfPages_type1;
import com.holcim.logon.logonldsinterface.services.UpdatePlanningMaterialRequest;
import com.holcim.logon.logonldsinterface.services.UpdatePlanningMaterialResponse;
import com.holcim.logon.logonldsinterface.services.UpdateSchedulingRequest;
import com.holcim.logon.logonldsinterface.services.UpdateSchedulingResponse;
import com.holcim.logon.logonldsinterface.services.ValidateCustomerOrderRequest;
import com.holcim.logon.logonldsinterface.services.ValidateCustomerOrderResponse;

public class LogonLDSInterfaceConsumer implements LogonLDSInterfaceConsumerInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LogonLDSInterfaceConsumer.class);
	private static final String CLIENT_TYPE_LOGON = "LOGON";
	private static final ObjectFactory LDS_INTERFACE_OBJECT_FACTORY = new ObjectFactory();

	private OffsetDateTime createOffsetDateTime(Calendar cal) {
		return OffsetDateTime.ofInstant(cal.toInstant(), cal.getTimeZone().toZoneId());
	}

	private Calendar createCalendar(OffsetDateTime dateTime, Calendar originalTimeZoneCalendar) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dateTime.toInstant().toEpochMilli());
		if (originalTimeZoneCalendar != null) {
			cal.setTimeZone(originalTimeZoneCalendar.getTimeZone());
		}
		return cal;
	}

	/**
	 * Copies header information from webservice request received from LDS
	 * to an internal request object.
	 * @param request The internal request object where the header data is set
	 * @param wsRequest The incoming request from LDS
	 */
	private void copyWsRequestHeader(Request request, com.holcim.logon.logonldsinterface.common_in.Request wsRequest) {
		
		if (wsRequest.getHeader() == null) {
			
			throw new LogonException("Header is null", Reason.RES_ERROR_UNSPECIFIED);
		}
		if (wsRequest.getHeader().getSendersTime() == null) {
			
			throw new LogonException("SendersTime is null", Reason.RES_ERROR_UNSPECIFIED);
		}
		request.setEventTime(wsRequest.getHeader().getSendersTime().getTime());
		request.setSenderInfo(wsRequest.getHeader().getSenderInfo());
		request.setEventName(wsRequest.getClass().toString());
		request.setClientType(wsRequest.getHeader().getClientType());
		request.setLanguage(getSupportedLanguage(wsRequest.getHeader().getLanguage()));
		request.setSimulateOnly(wsRequest.getSimulateOnly());
	}
	
	private static Header createHeader(LdsRequestId_type1 ldsRequestId) {
		Header header = new Header();
		header.setClientType(CLIENT_TYPE_LOGON);
		String senderInfo = getSenderInfo();
		header.setSenderInfo(senderInfo);
		header.setSendersTime(Calendar.getInstance());
		header.setLdsRequestId(ldsRequestId);
		
		return header;
	}
	
	private static String getSenderInfo() {
		try {
    		InitialContext initContext = new InitialContext();
    		ConfigServiceLocalInterface configService = (ConfigServiceLocalInterface) initContext.lookup(LocalServices.APPLICATION_NAME + "/ConfigServiceBean!com.crh.logon.business.services.localinterfaces.ConfigServiceLocalInterface");
    		ConfigBean configBean = configService.getConfigBean();
    		return configBean.getInstance();
    	}
		catch (NamingException e) {
			LOG.error(e.getMessage());
		}
		return null;
	}

	private void setWsResponse(com.holcim.logon.logonldsinterface.common_in.Response wsResponse, Response response, LdsRequestId_type1 ldsRequestId) {
		
		wsResponse.setHeader(createHeader(ldsRequestId));
		if (response.getCode() == null) {
			
			throw new LogonException("Response code is null", Reason.RES_ERROR_UNSPECIFIED);
		}
		wsResponse.setCode(response.getCode().toString());
		if (response.getTxt() != null) {
			wsResponse.setTxt(response.getTxt() + ((response.getContextValue()!=null?" (" + response.getContextValue() + ")":"")));
		}
	}
	
	private void setWsResponse(com.holcim.logon.logonldsinterface.common_in.Response wsResponse, LdsRequestId_type1 ldsRequestId, Throwable t) {
		
		LogonException e;
		if (t instanceof LogonException) {
			e = (LogonException)t;
		}
		else {
			e = new LogonException(t);
		}
		ExceptionUtil.errException(LOG, e, null);
		wsResponse.setHeader(createHeader(ldsRequestId));
		wsResponse.setCode(ResponseCode.TECHNICAL_ERROR.value().toString());
		wsResponse.setTxt((e.getMessage()!=null?e.getMessage() + " ":"") + "(" + e.getId() + ")");
	}
	
	private String getSupportedLanguage(String language) {
		
		String[] languages = new String[] {	Constant.LANGUAGE_CODE_ENGLISH,
											Constant.LANGUAGE_CODE_FRENCH,
											Constant.LANGUAGE_CODE_GERMAN,
											Constant.LANGUAGE_CODE_ITALIAN,
											Constant.LANGUAGE_CODE_SPANISH};
		
		if (language == null || !Arrays.asList(languages).contains(language)) {
			
			return Constant.LANGUAGE_CODE_ENGLISH;
		}
		
		return language;
	}
	
	private void getWsVendorData(VendorData vendorData, com.holcim.logon.logonldsinterface.common_in.VendorData wsVendorData) {
		
		if (wsVendorData.getSapVendorId() != null) {
			vendorData.setSapVendorId(wsVendorData.getSapVendorId().getSapVendorId_type0());
		}
		if (wsVendorData.getName1() != null) {
			vendorData.setName1(wsVendorData.getName1().getName1_type0());
		}
		if (wsVendorData.getName2() != null) {
			vendorData.setName2(wsVendorData.getName2().getName2_type0());
		}
		if (wsVendorData.getStreet() != null) {
			vendorData.setStreet(wsVendorData.getStreet().getStreet_type0());
		}
		if (wsVendorData.getZipCode() != null) {
			vendorData.setZipCode(wsVendorData.getZipCode().getZipCode_type0());
		}
		if (wsVendorData.getCity() != null)	 {
			vendorData.setCity(wsVendorData.getCity().getCity_type0());
		}
		if (wsVendorData.getCountry() != null) {
			vendorData.setCountry(wsVendorData.getCountry().getCountry_type0());
		}
		if (wsVendorData.getPhoneNumber() != null) {
			vendorData.setPhoneNumber(wsVendorData.getPhoneNumber().getPhoneNumber_type0());
		}
		if (wsVendorData.getTaxNumber() != null) {
			vendorData.setTaxNumber(wsVendorData.getTaxNumber().getTaxNumber_type0());
		}
	}
	
	private void getWsBusinessPartnerData(BusinessPartnerData businessPartnerData, com.holcim.logon.logonldsinterface.common_in.BusinessPartnerData wsBusinessPartnerData) {
		
		if (wsBusinessPartnerData.getSapBpId() != null) {
			businessPartnerData.setSapBpId(wsBusinessPartnerData.getSapBpId().getSapBpId_type0());
		}
		businessPartnerData.setIsOneTime(wsBusinessPartnerData.getIsOneTime());
		if (wsBusinessPartnerData.getName1() != null) {
			businessPartnerData.setName1(wsBusinessPartnerData.getName1().getName1_type2());
		}
		if (wsBusinessPartnerData.getName2() != null) {
			businessPartnerData.setName2(wsBusinessPartnerData.getName2().getName2_type2());
		}
		if (wsBusinessPartnerData.getStreet() != null) {
			businessPartnerData.setStreet(wsBusinessPartnerData.getStreet().getStreet_type2());
		}
		if (wsBusinessPartnerData.getZipCode() != null)	 {
			businessPartnerData.setZipCode(wsBusinessPartnerData.getZipCode().getZipCode_type2());
		}
		if (wsBusinessPartnerData.getCity() != null) {
			businessPartnerData.setCity(wsBusinessPartnerData.getCity().getCity_type2());
		}
		if (wsBusinessPartnerData.getCountry() != null) {
			businessPartnerData.setCountry(wsBusinessPartnerData.getCountry().getCountry_type2());
		}
		if (wsBusinessPartnerData.getPhoneNumber() != null) {
			businessPartnerData.setPhoneNumber(wsBusinessPartnerData.getPhoneNumber().getPhoneNumber_type2());
		}
		if (wsBusinessPartnerData.getVatRegistrationNumber() != null) {
			businessPartnerData.setVatRegistrationNumber(wsBusinessPartnerData.getVatRegistrationNumber().getVatRegistrationNumber_type0());
		}
	}
	
	private void getWsPriceData(PriceData priceData, com.holcim.logon.logonldsinterface.common_in.PriceData wsPriceData) {
		
		if (wsPriceData.getCurrency() != null) {
			priceData.setCurrency(wsPriceData.getCurrency().getCurrency_type0());
		}
		if (wsPriceData.getNetPrice() != null) {
			priceData.setNetPrice(wsPriceData.getNetPrice().getNetPrice_type0());
		}
		if (wsPriceData.getVatValue() != null) {
			priceData.setVatValue(wsPriceData.getVatValue().getVatValue_type0());
		}
		if (wsPriceData.getGrossPrice() != null) {
			priceData.setGrossPrice(wsPriceData.getGrossPrice().getGrossPrice_type0());
		}
	}
	
	private void getWsLocationData(LocationData locationData, com.holcim.logon.logonldsinterface.common_in.LocationData wsLocationData) {
		
		if (wsLocationData.getSupplyPlantNo() != null) {
			locationData.setSupplyPlantNo(wsLocationData.getSupplyPlantNo().getSupplyPlantNo_type2());
		}
		if (wsLocationData.getShippingpointNo() != null) {
			locationData.setShippingpointNo(wsLocationData.getShippingpointNo().getShippingpointNo_type2());
		}
		if (wsLocationData.getLoadingpointNo() != null) {
			locationData.setLoadingpointNo(wsLocationData.getLoadingpointNo().getLoadingpointNo_type2());
		}
		if (wsLocationData.getLoadingpointName() != null) {
			String loadingPointName = wsLocationData.getLoadingpointName().getLoadingpointName_type2();
			locationData.setLoadingpointName(loadingPointName);
		}
	}
	
	/**
	 * Copies data from a received WS vehicle data object to an internal object.
	 * @param vehicleData The internal object to which the data is copied
	 * @param wsVehicleData The external object from which the data is read
	 */
	private void copyWsVehicleData(VehicleData vehicleData, com.holcim.logon.logonldsinterface.common_in.VehicleData wsVehicleData) {
		
		if (wsVehicleData.getCommercialCarrier() != null) {
			vehicleData.setCommercialCarrier(new VendorData());
			getWsVendorData(vehicleData.getCommercialCarrier(), wsVehicleData.getCommercialCarrier());
		}
		if (wsVehicleData.getExecutingCarrier() != null) {
			vehicleData.setExecutingCarrier(new VendorData());
			getWsVendorData(vehicleData.getExecutingCarrier(), wsVehicleData.getExecutingCarrier());
		}
		if (wsVehicleData.getVehicleType() != null) {
			vehicleData.setVehicleType(wsVehicleData.getVehicleType().getVehicleType_type0());
		}
		if (wsVehicleData.getAllowedVehicleType() != null) {
			for (com.holcim.logon.logonldsinterface.common_in.AllowedVehicleType_type1 wsAllowedVehicleType : wsVehicleData.getAllowedVehicleType()) {
				vehicleData.getAllowedVehicleType().add(wsAllowedVehicleType.getAllowedVehicleType_type0());
			}
		}
		if (wsVehicleData.getTruckId() != null) {
			vehicleData.setTruckId(wsVehicleData.getTruckId().getTruckId_type0());
		}
		if (wsVehicleData.getTruckPlateNo() != null) {
			vehicleData.setTruckPlateNo(wsVehicleData.getTruckPlateNo().getTruckPlateNo_type0());
		}
		if (wsVehicleData.getSapTruckNo() != null) {
			vehicleData.setSapTruckNo(wsVehicleData.getSapTruckNo().getSapTruckNo_type0());
		}
		if (wsVehicleData.getTrailerId() != null) {
			vehicleData.setTrailerId(wsVehicleData.getTrailerId().getTrailerId_type0());
		}
		if (wsVehicleData.getTrailerPlateNo() != null) {
			vehicleData.setTrailerPlateNo(wsVehicleData.getTrailerPlateNo().getTrailerPlateNo_type0());
		}
		if (wsVehicleData.getSapTrailerNo() != null) {
			vehicleData.setSapTrailerNo(wsVehicleData.getSapTrailerNo().getSapTrailerNo_type0());
		}
		if (wsVehicleData.getExternalVehicleId() != null) {
			vehicleData.setExternalVehicleId(wsVehicleData.getExternalVehicleId().getExternalVehicleId_type0());
		}
		if (wsVehicleData.getVehicleCompTareWeight() != null) {
			vehicleData.setVehicleCompTareWeight(wsVehicleData.getVehicleCompTareWeight().getVehicleCompTareWeight_type0());
		}
		if (wsVehicleData.getVehicleMaxGrossWeight() != null) {
			vehicleData.setVehicleMaxGrossWeight(wsVehicleData.getVehicleMaxGrossWeight().getVehicleMaxGrossWeight_type0());
		}
	}
	
	/**
	 * Copies data from a received WS driver data object to an internal object.
	 * @param driverData The internal object to which the data is copied
	 * @param wsDriverData The external object from which the data is read
	 */
	private void copyWsDriverData(DriverData driverData, com.holcim.logon.logonldsinterface.common_in.DriverData wsDriverData) {
		
		if (wsDriverData.getDriverName() != null) {
			driverData.setDriverName(wsDriverData.getDriverName().getDriverName_type0());
		}
		if (wsDriverData.getIdCardNumber() != null) {
			driverData.setIdCardNumber(wsDriverData.getIdCardNumber().getIdCardNumber_type0());
		}
		if (wsDriverData.getPersonalUniqueNumber() != null) {
			driverData.setPersonalUniqueNumber(wsDriverData.getPersonalUniqueNumber().getPersonalUniqueNumber_type0());
		}
	}
	
	/**
	 * Copies data from a received WS attorney data object to an internal object.
	 * @param attorneyData The internal object to which the data is copied
	 * @param wsAttorneyData The external object from which the data is read
	 */
	private void copyWsAttorneyData(AttorneyData attorneyData, com.holcim.logon.logonldsinterface.common_in.AttorneyData wsAttorneyData) {
	     if (wsAttorneyData.getName() != null) {
	         attorneyData.setName(wsAttorneyData.getName().getName_type0());
	     }
	     if (wsAttorneyData.getStart() != null) {
			attorneyData.setStart(wsAttorneyData.getStart().getStart_type0().getTime());
		}
	 }

	
	/**
	 * Copies data from a received WS dispatcher data object to an internal object.
	 * @param dispatcherData The internal object to which the data is copied
	 * @param wsDispatcherData The external object from which the data is read
	 */
	private void copyWsDispatcherData(DispatcherData dispatcherData, com.holcim.logon.logonldsinterface.common_in.DispatcherData wsDispatcherData) {
		
		if (wsDispatcherData.getName() != null) {
			dispatcherData.setName(wsDispatcherData.getName().getName_type2());
		}
		if (wsDispatcherData.getContact() != null) {
			dispatcherData.setContact(wsDispatcherData.getContact().getContact_type0());
		}
		if (wsDispatcherData.getIdNumber() != null) {
			dispatcherData.setIdNumber(wsDispatcherData.getIdNumber().getIdNumber_type0());
		}
	}
	
	private void getWsMaterialData(MaterialData materialData, com.holcim.logon.logonldsinterface.common_in.MaterialData wsMaterialData) {
		
		if (wsMaterialData.getMaterialNo() != null) {
			materialData.setMaterialNo(wsMaterialData.getMaterialNo().getMaterialNo_type0());
		}
		if (wsMaterialData.getContractItemNumber() != null) {
			materialData.setContractItemNumber(wsMaterialData.getContractItemNumber().getContractItemNumber_type0());
		}
		if (wsMaterialData.getBrandSalesName() != null) {
			materialData.setBrandSalesName(wsMaterialData.getBrandSalesName().getBrandSalesName_type0());
		}
		if (wsMaterialData.getSapMaterialDescription() != null) {
			materialData.setSapMaterialDescription(wsMaterialData.getSapMaterialDescription().getSapMaterialDescription_type0());
		}
		if (wsMaterialData.getProdPlantNo() != null) {
			materialData.setProdPlantNo(wsMaterialData.getProdPlantNo().getProdPlantNo_type0());
		}
		if (wsMaterialData.getMixDesignID() != null) {
			materialData.setMixDesignID(wsMaterialData.getMixDesignID().getMixDesignID_type0());
		}
		if (wsMaterialData.getQuantity() != null) {
			materialData.setQuantity(wsMaterialData.getQuantity().getQuantity_type0());
		}
		if (wsMaterialData.getQuantityPerDelivery() != null) {
			materialData.setQuantityPerDelivery(wsMaterialData.getQuantityPerDelivery().getQuantityPerDelivery_type0());
		}
		if (wsMaterialData.getQuantityUom() != null) {
			materialData.setQuantityUom(wsMaterialData.getQuantityUom().getQuantityUom_type0());
		}
		if (wsMaterialData.getBaseUom() != null) {
			materialData.setBaseUom(wsMaterialData.getBaseUom().getBaseUom_type0());
		}
		if (wsMaterialData.getNumerator() != null) {
			materialData.setNumerator(wsMaterialData.getNumerator().getNumerator_type0());
		}
		if (wsMaterialData.getDenominator() != null) {
			materialData.setDenominator(wsMaterialData.getDenominator().getDenominator_type0());
		}
		if (wsMaterialData.getPlanningMaterialType() != null) {
			materialData.setPlanningMaterialType(wsMaterialData.getPlanningMaterialType().getPlanningMaterialType_type0());
		}
	}
	
	private void getWsLoadedMaterialData(LoadedMaterialData loadedMaterialData, com.holcim.logon.logonldsinterface.common_in.LoadedMaterialData wsLoadedMaterialData) {
		
		if (wsLoadedMaterialData.getProdPlantNo() != null) {
			loadedMaterialData.setProdPlantNo(wsLoadedMaterialData.getProdPlantNo().getProdPlantNo_type4());
		}
		if (wsLoadedMaterialData.getMaterialNo() != null) {
			loadedMaterialData.setMaterialNo(wsLoadedMaterialData.getMaterialNo().getMaterialNo_type4());
		}
		if (wsLoadedMaterialData.getAggRmxCode() != null) {
			loadedMaterialData.setAggRmxCode(wsLoadedMaterialData.getAggRmxCode().getAggRmxCode_type0());
		}
		if (wsLoadedMaterialData.getMixDesignID() != null) {
			loadedMaterialData.setMixDesignID(wsLoadedMaterialData.getMixDesignID().getMixDesignID_type2());
		}
		if (wsLoadedMaterialData.getSapStorageLocation() != null) {
			loadedMaterialData.setSapStorageLocation(wsLoadedMaterialData.getSapStorageLocation().getSapStorageLocation_type0());
		}
		if (wsLoadedMaterialData.getIssuingLocationId() != null) {
			loadedMaterialData.setIssuingLocationId(wsLoadedMaterialData.getIssuingLocationId().getIssuingLocationId_type0());
		}
		if (wsLoadedMaterialData.getIssuingLocationName() != null) {
			loadedMaterialData.setIssuingLocationName(wsLoadedMaterialData.getIssuingLocationName().getIssuingLocationName_type0());
		}
		if (wsLoadedMaterialData.getLdsSampleNo() != null) {
			loadedMaterialData.setLdsSampleNo(wsLoadedMaterialData.getLdsSampleNo().getLdsSampleNo_type0());
		}
		if (wsLoadedMaterialData.getChromatReductionMonths() != null) {
			loadedMaterialData.setChromatReductionMonths(wsLoadedMaterialData.getChromatReductionMonths().getChromatReductionMonths_type0());
		}
		if (wsLoadedMaterialData.getSiloNo() != null) {
			loadedMaterialData.setSiloNo(wsLoadedMaterialData.getSiloNo().getSiloNo_type0());
		}
	}
	
	private void getWsFirstWeighingData(FirstWeighingData firstWeighingData, com.holcim.logon.logonldsinterface.common_in.FirstWeighingData wsFirstWeighingData) {
		
		if (wsFirstWeighingData.getW1WeighingPointNo() != null) {
			firstWeighingData.setW1WeighingPointNo(wsFirstWeighingData.getW1WeighingPointNo().getW1WeighingPointNo_type0());
		}
		if (wsFirstWeighingData.getLoadingStartTime() != null) {
			firstWeighingData.setLoadingStartTime(wsFirstWeighingData.getLoadingStartTime().getTime());
		}
		if (wsFirstWeighingData.getW1Quantity() != null) {
			firstWeighingData.setW1Quantity(wsFirstWeighingData.getW1Quantity().getW1Quantity_type0());
		}
		if (wsFirstWeighingData.getW1UoM() != null) {
			firstWeighingData.setW1UoM(wsFirstWeighingData.getW1UoM().getW1UoM_type0());
		}
		if (wsFirstWeighingData.getLdsW1RecordId() != null) {
			firstWeighingData.setLdsW1RecordId(wsFirstWeighingData.getLdsW1RecordId().getLdsW1RecordId_type0());
		}
		if (wsFirstWeighingData.getLdsW1Time() != null) {
			firstWeighingData.setLdsW1Time(wsFirstWeighingData.getLdsW1Time().getTime());
		}
		if (wsFirstWeighingData.getLdsW1BalanceId() != null) {
			firstWeighingData.setLdsW1BalanceId(wsFirstWeighingData.getLdsW1BalanceId().getLdsW1BalanceId_type0());
		}
	}
	
	private void getWsSecondWeighingData(SecondWeighingData secondWeighingData, com.holcim.logon.logonldsinterface.common_in.SecondWeighingData wsSecondWeighingData) {
		
		if (wsSecondWeighingData.getW2WeighingPointNo() != null) {
			secondWeighingData.setW2WeighingPointNo(wsSecondWeighingData.getW2WeighingPointNo().getW2WeighingPointNo_type0());
		}
		if (wsSecondWeighingData.getLoadingEndTime() != null) {
			secondWeighingData.setLoadingEndTime(wsSecondWeighingData.getLoadingEndTime().getTime());
		}
		if (wsSecondWeighingData.getW2Quantity() != null) {
			secondWeighingData.setW2Quantity(wsSecondWeighingData.getW2Quantity().getW2Quantity_type0());
		}
		if (wsSecondWeighingData.getW2UoM() != null) {
			secondWeighingData.setW2UoM(wsSecondWeighingData.getW2UoM().getW2UoM_type0());
		}
		if (wsSecondWeighingData.getLdsW2RecordId() != null) {
			secondWeighingData.setLdsW2RecordId(wsSecondWeighingData.getLdsW2RecordId().getLdsW2RecordId_type0());
		}
		if (wsSecondWeighingData.getLdsW2Time() != null) {
			secondWeighingData.setLdsW2Time(wsSecondWeighingData.getLdsW2Time().getTime());
		}
		if (wsSecondWeighingData.getLdsW2BalanceId() != null) {
			secondWeighingData.setLdsW2BalanceId(wsSecondWeighingData.getLdsW2BalanceId().getLdsW2BalanceId_type0());
		}
	}
	
	private void getWsEndLoadingPartData(EndLoadingPartData endLoadingPartData, com.holcim.logon.logonldsinterface.common_in.EndLoadingPartData wsEndLoadingPartData) {
		
		if (wsEndLoadingPartData.getLoadOrderId() != null) {
			endLoadingPartData.setLoadOrderId(wsEndLoadingPartData.getLoadOrderId().getLoadOrderId_type10());
		}
		if (wsEndLoadingPartData.getLoadOrderItemId() != null) {
			endLoadingPartData.setLoadOrderItemId(wsEndLoadingPartData.getLoadOrderItemId().getLoadOrderItemId_type4());
		}
		if (wsEndLoadingPartData.getAffectedOrders() != null) {
			for (com.holcim.logon.logonldsinterface.common_in.AffectedOrders_type0 wsAffectedOrders : wsEndLoadingPartData.getAffectedOrders()) {
				AffectedOrders affectedOrders = new AffectedOrders();
				if (wsAffectedOrders.getLogonOrderNo() != null) {
					affectedOrders.setLogonOrderNo(wsAffectedOrders.getLogonOrderNo().getLogonOrderNo_type16());
				}
				if (wsAffectedOrders.getLdsDeliveryNoteNo() != null) {
					affectedOrders.setLdsDeliveryNoteNo(wsAffectedOrders.getLdsDeliveryNoteNo().getLdsDeliveryNoteNo_type2());
				}
				if (wsAffectedOrders.getLogonOrderItemNumber() != null) {
					affectedOrders.setLogonOrderItemNumber(wsAffectedOrders.getLogonOrderItemNumber().getLogonOrderItemNumber_type6());
				}
				if (wsAffectedOrders.getNetWeight() != null) {
					affectedOrders.setNetWeight(wsAffectedOrders.getNetWeight().getNetWeight_type2());
				}
				if (wsAffectedOrders.getNetWeightUoM() != null) {
					affectedOrders.setNetWeightUoM(wsAffectedOrders.getNetWeightUoM().getNetWeightUoM_type2());
				}
				endLoadingPartData.getAffectedOrders().add(affectedOrders);
			}
		}
		if (wsEndLoadingPartData.getMaterial() != null) {
			endLoadingPartData.setMaterial(new LoadedMaterialData());
			getWsLoadedMaterialData(endLoadingPartData.getMaterial(), wsEndLoadingPartData.getMaterial());
		}
		if (wsEndLoadingPartData.getFirstWeighing() != null) {
			endLoadingPartData.setFirstWeighing(new FirstWeighingData());
			getWsFirstWeighingData(endLoadingPartData.getFirstWeighing(), wsEndLoadingPartData.getFirstWeighing());
		}
		if (wsEndLoadingPartData.getSecondWeighing() != null) {
			endLoadingPartData.setSecondWeighing(new SecondWeighingData());
			getWsSecondWeighingData(endLoadingPartData.getSecondWeighing(), wsEndLoadingPartData.getSecondWeighing());
		}
		if (wsEndLoadingPartData.getNetWeight() != null) {
			endLoadingPartData.setNetWeight(wsEndLoadingPartData.getNetWeight().getNetWeight_type4());
		}
		if (wsEndLoadingPartData.getNetWeightUoM() != null) {
			endLoadingPartData.setNetWeightUoM(wsEndLoadingPartData.getNetWeightUoM().getNetWeightUoM_type4());
		}
	}
	
	private void getWsLoadOrderItemData(LoadOrderItemData loadOrderItemData, com.holcim.logon.logonldsinterface.common_in.LoadOrderItemData wsLoadOrderItemData) {
		
		if (wsLoadOrderItemData.getLoadOrderItemId() != null) {
			loadOrderItemData.setLoadOrderItemId(wsLoadOrderItemData.getLoadOrderItemId().getLoadOrderItemId_type0());
		}
		if (wsLoadOrderItemData.getMaterial() != null) {
			loadOrderItemData.setMaterial(new MaterialData());
			getWsMaterialData(loadOrderItemData.getMaterial(), wsLoadOrderItemData.getMaterial());
		}
		if (wsLoadOrderItemData.getLoadingpointNo() != null) {
			loadOrderItemData.setLoadingpointNo(wsLoadOrderItemData.getLoadingpointNo().getLoadingpointNo_type4());
		}
		loadOrderItemData.setExactDeliveryQuantity(wsLoadOrderItemData.getExactDeliveryQuantity());
		if (wsLoadOrderItemData.getChamberNo() != null) {
			loadOrderItemData.setChamberNo(wsLoadOrderItemData.getChamberNo().getChamberNo_type0());
		}
	}
	
	/**
	 * Converts an external WS load order item object to an internal one.
	 * @param wsCompletedLoadOrderItemData A load order item data object received in an incoming web
	 * service request.
	 * @return A corresponding internal load order item data object
	 */
	private CompletedLoadOrderItemData getWsCompletedLoadOrderItemData(com.holcim.logon.logonldsinterface.common_in.CompletedLoadOrderItemData wsCompletedLoadOrderItemData) {
		
		final CompletedLoadOrderItemData completedLoadOrderItemData = new CompletedLoadOrderItemData();
		
		if (wsCompletedLoadOrderItemData.getLoadOrderItem() != null	) {
			completedLoadOrderItemData.setLoadOrderItem(new LoadOrderItemData());
			getWsLoadOrderItemData(completedLoadOrderItemData.getLoadOrderItem(), wsCompletedLoadOrderItemData.getLoadOrderItem());
		}
		if (wsCompletedLoadOrderItemData.getMaterial() != null) {
			completedLoadOrderItemData.setMaterial(new LoadedMaterialData());
			getWsLoadedMaterialData(completedLoadOrderItemData.getMaterial(), wsCompletedLoadOrderItemData.getMaterial());
		}
		if (wsCompletedLoadOrderItemData.getFirstWeighing() != null) {
			completedLoadOrderItemData.setFirstWeighing(new FirstWeighingData());
			getWsFirstWeighingData(completedLoadOrderItemData.getFirstWeighing(), wsCompletedLoadOrderItemData.getFirstWeighing());
		}
		if (wsCompletedLoadOrderItemData.getSecondWeighing() != null) {
			completedLoadOrderItemData.setSecondWeighing(new SecondWeighingData());
			getWsSecondWeighingData(completedLoadOrderItemData.getSecondWeighing(), wsCompletedLoadOrderItemData.getSecondWeighing());
		}
		if (wsCompletedLoadOrderItemData.getNetWeight() != null) {
			completedLoadOrderItemData.setNetWeight(wsCompletedLoadOrderItemData.getNetWeight().getNetWeight_type0());
		}
		if (wsCompletedLoadOrderItemData.getNetWeightUoM() != null) {
			completedLoadOrderItemData.setNetWeightUoM(wsCompletedLoadOrderItemData.getNetWeightUoM().getNetWeightUoM_type0());
		}
		
		return completedLoadOrderItemData;
	}
	
	private void getWsOrderItemData(OrderItemData orderItemData, com.holcim.logon.logonldsinterface.common_in.OrderItemData wsOrderItemData) {
		
		if (wsOrderItemData.getLogonOrderItemNumber() != null) {
			orderItemData.setLogonOrderItemNumber(wsOrderItemData.getLogonOrderItemNumber().getLogonOrderItemNumber_type0());
		}
		if (wsOrderItemData.getLdsOrderItemNumber() != null) {
			orderItemData.setLdsOrderItemNumber(wsOrderItemData.getLdsOrderItemNumber().getLdsOrderItemNumber_type0());
		}
		if (wsOrderItemData.getLoadId() != null) {
			orderItemData.setLoadId(wsOrderItemData.getLoadId().getLoadId_type0());
		}
		if (wsOrderItemData.getMaterial() != null) {
			orderItemData.setMaterial(new MaterialData());
			getWsMaterialData(orderItemData.getMaterial(), wsOrderItemData.getMaterial());
		}
		if (wsOrderItemData.getLoadingpointNo() != null) {
			orderItemData.setLoadingpointNo(wsOrderItemData.getLoadingpointNo().getLoadingpointNo_type0());
		}
		if (wsOrderItemData.getLoadingpointName() != null) {
			String loadingPointName = wsOrderItemData.getLoadingpointName().getLoadingpointName_type0();
			orderItemData.setLoadingpointName(loadingPointName);
		}
		if (wsOrderItemData.getPossibleMaterial() != null) {
			for (com.holcim.logon.logonldsinterface.common_in.PossibleMaterial_type0 wsPossibleMaterial : wsOrderItemData.getPossibleMaterial()) {
				PossibleMaterial possibleMaterial = new PossibleMaterial();
				if (wsPossibleMaterial.getMaterialNo() != null) {
					possibleMaterial.setMaterialNo(wsPossibleMaterial.getMaterialNo().getMaterialNo_type2());
				}
				if (wsPossibleMaterial.getProdPlantNo() != null) {
					possibleMaterial.setProdPlantNo(wsPossibleMaterial.getProdPlantNo().getProdPlantNo_type2());
				}
				orderItemData.getPossibleMaterial().add(possibleMaterial);
			}
		}
		if (wsOrderItemData.getPriceData() != null) {
			orderItemData.setPriceData(new PriceData());
			getWsPriceData(orderItemData.getPriceData(), wsOrderItemData.getPriceData());
		}
	}
	
	private void getWsServiceMaterialData(ServiceMaterialData serviceMaterialData, com.holcim.logon.logonldsinterface.common_in.ServiceMaterialData wsServiceMaterialData) {
		
		if (wsServiceMaterialData.getMaterial() != null) {
			serviceMaterialData.setMaterial(new MaterialData());
			getWsMaterialData(serviceMaterialData.getMaterial(), wsServiceMaterialData.getMaterial());
		}
		if (wsServiceMaterialData.getPriceData() != null) {
			serviceMaterialData.setPriceData(new PriceData());
			getWsPriceData(serviceMaterialData.getPriceData(), wsServiceMaterialData.getPriceData());
		}
	}
	
	/**
	 * Copies order data from an incoming WS order object to an internal one.
	 * @param orderData The internal order data object to which the data is copied
	 * @param wsOrderData Incoming order data object from which the data is read
	 */
	private void copyWsOrderData(OrderData orderData, com.holcim.logon.logonldsinterface.common_in.OrderData wsOrderData) {
		
		if (wsOrderData.getLogonCustomerOrderNo() != null) {
			orderData.setLogonCustomerOrderNo(wsOrderData.getLogonCustomerOrderNo().getLogonCustomerOrderNo_type0());
		}
		if (wsOrderData.getLdsCustomerOrderNo() != null) {
			orderData.setLdsCustomerOrderNo(wsOrderData.getLdsCustomerOrderNo().getLdsCustomerOrderNo_type0());
		}
		if (wsOrderData.getLogonOrderNo() != null) {
			orderData.setLogonOrderNo(wsOrderData.getLogonOrderNo().getLogonOrderNo_type0());
		}
		if (wsOrderData.getSequentialLogonOrderNo() != null) {
			orderData.setSequentialLogonOrderNo(wsOrderData.getSequentialLogonOrderNo().getSequentialLogonOrderNo_type0());
		}
		if (wsOrderData.getContractType() != null) {
			orderData.setContractType(wsOrderData.getContractType().getContractType_type0());
		}
		if (wsOrderData.getContractNumber() != null) {
			orderData.setContractNumber(wsOrderData.getContractNumber().getContractNumber_type0());
		}
		if (wsOrderData.getIncoterms() != null) {
			orderData.setIncoterms(wsOrderData.getIncoterms().getIncoterms_type0());
		}
		if (wsOrderData.getCustorderCustrefNo() != null) {
			orderData.setCustorderCustrefNo(wsOrderData.getCustorderCustrefNo().getCustorderCustrefNo_type0());
		}
		if (wsOrderData.getCommercialCarrier() != null) {
			orderData.setCommercialCarrier(new VendorData());
			getWsVendorData(orderData.getCommercialCarrier(), wsOrderData.getCommercialCarrier());
		}
		if (wsOrderData.getExecutingCarrier() != null) {
			orderData.setExecutingCarrier(new VendorData());
			getWsVendorData(orderData.getExecutingCarrier(), wsOrderData.getExecutingCarrier());
		}
		if (wsOrderData.getSoldTo() != null) {
			orderData.setSoldTo(new BusinessPartnerData());
			getWsBusinessPartnerData(orderData.getSoldTo(), wsOrderData.getSoldTo());
		}
		if (wsOrderData.getShipTo() != null) {
			orderData.setShipTo(new BusinessPartnerData());
			getWsBusinessPartnerData(orderData.getShipTo(), wsOrderData.getShipTo());
		}
		if (wsOrderData.getBillTo() != null) {
			orderData.setBillTo(new BusinessPartnerData());
			getWsBusinessPartnerData(orderData.getBillTo(), wsOrderData.getBillTo());
		}
		if (wsOrderData.getPayer() != null) {
			orderData.setPayer(new BusinessPartnerData());
			getWsBusinessPartnerData(orderData.getPayer(), wsOrderData.getPayer());
		}
		if (wsOrderData.getSupplyPlantNo() != null) {
			orderData.setSupplyPlantNo(wsOrderData.getSupplyPlantNo().getSupplyPlantNo_type0());
		}
		if (wsOrderData.getShippingpointNo() != null) {
			orderData.setShippingpointNo(wsOrderData.getShippingpointNo().getShippingpointNo_type0());
		}
		if (wsOrderData.getDeliveryType() != null) {
			orderData.setDeliveryType(wsOrderData.getDeliveryType().getDeliveryType_type0());
		}
		if (wsOrderData.getNoteDelivery() != null) {
			orderData.setNoteDelivery(wsOrderData.getNoteDelivery().getNoteDelivery_type0());
		}
		if (wsOrderData.getNoteScheduling() != null) {
			orderData.setNoteScheduling(wsOrderData.getNoteScheduling().getNoteScheduling_type0());
		}
		if (wsOrderData.getDriverComment() != null) {
			orderData.setDriverComment(wsOrderData.getDriverComment().getDriverComment_type0());
		}
		orderData.setAutoScheduleRelease(wsOrderData.getAutoScheduleRelease());
		if (wsOrderData.getEarliestDeliveryTime() != null) {
			orderData.setEarliestDeliveryTime(wsOrderData.getEarliestDeliveryTime().getTime());
		}
		if (wsOrderData.getLatestDeliveryTime() != null) {
			orderData.setLatestDeliveryTime(wsOrderData.getLatestDeliveryTime().getTime());
		}
		if (wsOrderData.getPlannedDeliveryTime() != null) {
			orderData.setPlannedDeliveryTime(wsOrderData.getPlannedDeliveryTime().getTime());
		}
		if (wsOrderData.getDeliveryFreqDelayBtwnDeliveries() != null) {
			orderData.setDeliveryFreqDelayBtwnDeliveries(wsOrderData.getDeliveryFreqDelayBtwnDeliveries().getDeliveryFreqDelayBtwnDeliveries_type0());
		}
		if (wsOrderData.getDeliveryFreqDelayBtwnDeliveriesUom() != null) {
			orderData.setDeliveryFreqDelayBtwnDeliveriesUom(wsOrderData.getDeliveryFreqDelayBtwnDeliveriesUom().getDeliveryFreqDelayBtwnDeliveriesUom_type0());
		}
		if (wsOrderData.getDeliveryFreqQtyPerHour() != null) {
			orderData.setDeliveryFreqQtyPerHour(wsOrderData.getDeliveryFreqQtyPerHour().getDeliveryFreqQtyPerHour_type0());
		}
		if (wsOrderData.getDeliveryFreqQtyPerHourUom() != null) {
			orderData.setDeliveryFreqQtyPerHourUom(wsOrderData.getDeliveryFreqQtyPerHourUom().getDeliveryFreqQtyPerHourUom_type0());
		}
		if (wsOrderData.getDeliveryTimeTolerance() != null) {
			orderData.setDeliveryTimeTolerance(wsOrderData.getDeliveryTimeTolerance().getDeliveryTimeTolerance_type0());
		}
		orderData.setPrintDeliveryNoteLds(wsOrderData.getPrintDeliveryNoteLds());
		orderData.setPrintCollectiveDeliveryNote(wsOrderData.getPrintCollectiveDeliveryNote());
		if (wsOrderData.getChromReduce() != null) {
			orderData.setChromReduce(wsOrderData.getChromReduce().getChromReduce_type0());
		}
		if (wsOrderData.getOrderValidationCode() != null) {
			orderData.setOrderValidationCode(wsOrderData.getOrderValidationCode().getOrderValidationCode_type0());
		}
		if (wsOrderData.getOrderItem() != null) {
			for (com.holcim.logon.logonldsinterface.common_in.OrderItemData wsOrderItemData : wsOrderData.getOrderItem()) {
				OrderItemData orderItemData = new OrderItemData();
				getWsOrderItemData(orderItemData, wsOrderItemData);
				orderData.getOrderItem().add(orderItemData);
			}
		}
		if (wsOrderData.getServiceMaterial() != null) {
			for (com.holcim.logon.logonldsinterface.common_in.ServiceMaterialData wsServiceMaterialData : wsOrderData.getServiceMaterial()) {
				ServiceMaterialData serviceMaterialData = new ServiceMaterialData();
				getWsServiceMaterialData(serviceMaterialData, wsServiceMaterialData);
				orderData.getServiceMaterial().add(serviceMaterialData);
			}
		}
		orderData.setExactDeliveryQuantity(wsOrderData.getExactDeliveryQuantity());
		orderData.setExactCustomerOrderQuantity(wsOrderData.getExactCustomerOrderQuantity());
		if (wsOrderData.getOrderPriceData() != null) {
			orderData.setOrderPriceData(new PriceData());
			getWsPriceData(orderData.getOrderPriceData(), wsOrderData.getOrderPriceData());
		}
	}

	private void setWsVendorData(com.holcim.logon.logonldsinterface.common_in.VendorData wsVendorData, VendorData vendorData) {
		
		if (vendorData.getSapVendorId() != null) {
			com.holcim.logon.logonldsinterface.common_in.SapVendorId_type1 data = new com.holcim.logon.logonldsinterface.common_in.SapVendorId_type1();
			data.setSapVendorId_type0(vendorData.getSapVendorId());
			wsVendorData.setSapVendorId(data);
		}
		if (vendorData.getName1() != null) {
			com.holcim.logon.logonldsinterface.common_in.Name1_type1 data = new com.holcim.logon.logonldsinterface.common_in.Name1_type1();
			data.setName1_type0(vendorData.getName1());
			wsVendorData.setName1(data);
		}
		if (vendorData.getName2() != null) {
			com.holcim.logon.logonldsinterface.common_in.Name2_type1 data = new com.holcim.logon.logonldsinterface.common_in.Name2_type1();
			data.setName2_type0(vendorData.getName2());
			wsVendorData.setName2(data);
		}
		if (vendorData.getStreet() != null) {
			com.holcim.logon.logonldsinterface.common_in.Street_type1 data = new com.holcim.logon.logonldsinterface.common_in.Street_type1();
			data.setStreet_type0(vendorData.getStreet());
			wsVendorData.setStreet(data);
		}
		if (vendorData.getZipCode() != null) {
			com.holcim.logon.logonldsinterface.common_in.ZipCode_type1 data = new com.holcim.logon.logonldsinterface.common_in.ZipCode_type1();
			data.setZipCode_type0(vendorData.getZipCode());
			wsVendorData.setZipCode(data);
		}
		if (vendorData.getCity() != null) {
			com.holcim.logon.logonldsinterface.common_in.City_type1 data = new com.holcim.logon.logonldsinterface.common_in.City_type1();
			data.setCity_type0(vendorData.getCity());
			wsVendorData.setCity(data);
		}
		if (vendorData.getCountry() != null) {
			com.holcim.logon.logonldsinterface.common_in.Country_type1 data = new com.holcim.logon.logonldsinterface.common_in.Country_type1();
			data.setCountry_type0(vendorData.getCountry());
			wsVendorData.setCountry(data);
		}
		if (vendorData.getPhoneNumber() != null) {
			com.holcim.logon.logonldsinterface.common_in.PhoneNumber_type1 data = new com.holcim.logon.logonldsinterface.common_in.PhoneNumber_type1();
			data.setPhoneNumber_type0(vendorData.getPhoneNumber());
			wsVendorData.setPhoneNumber(data);
		}
		if (vendorData.getTaxNumber() != null) {
			com.holcim.logon.logonldsinterface.common_in.TaxNumber_type1 data = new com.holcim.logon.logonldsinterface.common_in.TaxNumber_type1();
			data.setTaxNumber_type0(vendorData.getTaxNumber());
			wsVendorData.setTaxNumber(data);
		}
	}
	
	private void setWsVehicleData(com.holcim.logon.logonldsinterface.common_in.VehicleData wsVehicleData, VehicleData vehicleData) {
		
		if (vehicleData.getCommercialCarrier() != null) {
			com.holcim.logon.logonldsinterface.common_in.VendorData wsVendorData = new com.holcim.logon.logonldsinterface.common_in.VendorData();
			setWsVendorData(wsVendorData, vehicleData.getCommercialCarrier());
			wsVehicleData.setCommercialCarrier(wsVendorData);
		}
		if (vehicleData.getExecutingCarrier() != null) {
			com.holcim.logon.logonldsinterface.common_in.VendorData wsVendorData = new com.holcim.logon.logonldsinterface.common_in.VendorData();
			setWsVendorData(wsVendorData, vehicleData.getExecutingCarrier());
			wsVehicleData.setExecutingCarrier(wsVendorData);
		}
		if (vehicleData.getVehicleType() != null) {
			com.holcim.logon.logonldsinterface.common_in.VehicleType_type1 data = new com.holcim.logon.logonldsinterface.common_in.VehicleType_type1();
			data.setVehicleType_type0(vehicleData.getVehicleType());
			wsVehicleData.setVehicleType(data);
		}
		if (vehicleData.getAllowedVehicleType() != null) {
			com.holcim.logon.logonldsinterface.common_in.AllowedVehicleType_type1[] data = new com.holcim.logon.logonldsinterface.common_in.AllowedVehicleType_type1[vehicleData.getAllowedVehicleType().size()];
			for (int i=0; i<vehicleData.getAllowedVehicleType().size(); i++) {
				String allowedVehicleType = vehicleData.getAllowedVehicleType().get(i);
				com.holcim.logon.logonldsinterface.common_in.AllowedVehicleType_type1 wsAllowedVehicleType = new com.holcim.logon.logonldsinterface.common_in.AllowedVehicleType_type1();
				wsAllowedVehicleType.setAllowedVehicleType_type0(allowedVehicleType);
				data[i] = wsAllowedVehicleType;
			}
			wsVehicleData.setAllowedVehicleType(data);
		}
		if (vehicleData.getTruckId() != null) {
			com.holcim.logon.logonldsinterface.common_in.TruckId_type1 data = new com.holcim.logon.logonldsinterface.common_in.TruckId_type1();
			data.setTruckId_type0(vehicleData.getTruckId());
			wsVehicleData.setTruckId(data);
		}
		if (vehicleData.getTruckPlateNo() != null) {
			com.holcim.logon.logonldsinterface.common_in.TruckPlateNo_type1 data = new com.holcim.logon.logonldsinterface.common_in.TruckPlateNo_type1();
			data.setTruckPlateNo_type0(vehicleData.getTruckPlateNo());
			wsVehicleData.setTruckPlateNo(data);
		}
		if (vehicleData.getSapTruckNo() != null) {
			com.holcim.logon.logonldsinterface.common_in.SapTruckNo_type1 data = new com.holcim.logon.logonldsinterface.common_in.SapTruckNo_type1();
			data.setSapTruckNo_type0(vehicleData.getSapTruckNo());
			wsVehicleData.setSapTruckNo(data);
		}
		if (vehicleData.getTrailerId() != null) {
			com.holcim.logon.logonldsinterface.common_in.TrailerId_type1 data = new com.holcim.logon.logonldsinterface.common_in.TrailerId_type1();
			data.setTrailerId_type0(vehicleData.getTrailerId());
			wsVehicleData.setTrailerId(data);
		}
		if (vehicleData.getTrailerPlateNo() != null) {
			com.holcim.logon.logonldsinterface.common_in.TrailerPlateNo_type1 data = new com.holcim.logon.logonldsinterface.common_in.TrailerPlateNo_type1();
			data.setTrailerPlateNo_type0(vehicleData.getTrailerPlateNo());
			wsVehicleData.setTrailerPlateNo(data);
		}
		if (vehicleData.getSapTrailerNo() != null) {
			com.holcim.logon.logonldsinterface.common_in.SapTrailerNo_type1 data = new com.holcim.logon.logonldsinterface.common_in.SapTrailerNo_type1();
			data.setSapTrailerNo_type0(vehicleData.getSapTrailerNo());
			wsVehicleData.setSapTrailerNo(data);
		}
		if (vehicleData.getExternalVehicleId() != null) {
			com.holcim.logon.logonldsinterface.common_in.ExternalVehicleId_type1 data = new com.holcim.logon.logonldsinterface.common_in.ExternalVehicleId_type1();
			data.setExternalVehicleId_type0(vehicleData.getExternalVehicleId());
			wsVehicleData.setExternalVehicleId(data);
		}
		if (vehicleData.getVehicleCompTareWeight() != null) {
			com.holcim.logon.logonldsinterface.common_in.VehicleCompTareWeight_type1 data = new com.holcim.logon.logonldsinterface.common_in.VehicleCompTareWeight_type1();
			data.setVehicleCompTareWeight_type0(vehicleData.getVehicleCompTareWeight());
			wsVehicleData.setVehicleCompTareWeight(data);
		}
		if (vehicleData.getVehicleMaxGrossWeight() != null) {
			com.holcim.logon.logonldsinterface.common_in.VehicleMaxGrossWeight_type1 data = new com.holcim.logon.logonldsinterface.common_in.VehicleMaxGrossWeight_type1();
			data.setVehicleMaxGrossWeight_type0(vehicleData.getVehicleMaxGrossWeight());
			wsVehicleData.setVehicleMaxGrossWeight(data);
		}
	}
	
	private void setWsBusinessPartnerData(com.holcim.logon.logonldsinterface.common_in.BusinessPartnerData wsBusinessPartnerData, BusinessPartnerData businessPartnerData) {
		
		if (businessPartnerData.getSapBpId() != null) {
			com.holcim.logon.logonldsinterface.common_in.SapBpId_type1 data = new com.holcim.logon.logonldsinterface.common_in.SapBpId_type1();
			data.setSapBpId_type0(businessPartnerData.getSapBpId());
			wsBusinessPartnerData.setSapBpId(data);
		}
		if (businessPartnerData.isIsOneTime() != null) {
			wsBusinessPartnerData.setIsOneTime(businessPartnerData.isIsOneTime());
		}
		if (businessPartnerData.getName1() != null) {
			com.holcim.logon.logonldsinterface.common_in.Name1_type3 data = new com.holcim.logon.logonldsinterface.common_in.Name1_type3();
			data.setName1_type2(businessPartnerData.getName1());
			wsBusinessPartnerData.setName1(data);
		}
		if (businessPartnerData.getName2() != null) {
			com.holcim.logon.logonldsinterface.common_in.Name2_type3 data = new com.holcim.logon.logonldsinterface.common_in.Name2_type3();
			data.setName2_type2(businessPartnerData.getName2());
			wsBusinessPartnerData.setName2(data);
		}
		if (businessPartnerData.getStreet() != null) {
			com.holcim.logon.logonldsinterface.common_in.Street_type3 data = new com.holcim.logon.logonldsinterface.common_in.Street_type3();
			data.setStreet_type2(businessPartnerData.getStreet());
			wsBusinessPartnerData.setStreet(data);
		}
		if (businessPartnerData.getZipCode() != null) {
			com.holcim.logon.logonldsinterface.common_in.ZipCode_type3 data = new com.holcim.logon.logonldsinterface.common_in.ZipCode_type3();
			data.setZipCode_type2(businessPartnerData.getZipCode());
			wsBusinessPartnerData.setZipCode(data);
		}
		if (businessPartnerData.getCity() != null) {
			com.holcim.logon.logonldsinterface.common_in.City_type3 data = new com.holcim.logon.logonldsinterface.common_in.City_type3();
			data.setCity_type2(businessPartnerData.getCity());
			wsBusinessPartnerData.setCity(data);
		}
		if (businessPartnerData.getCountry() != null) {
			com.holcim.logon.logonldsinterface.common_in.Country_type3 data = new com.holcim.logon.logonldsinterface.common_in.Country_type3();
			data.setCountry_type2(businessPartnerData.getCountry());
			wsBusinessPartnerData.setCountry(data);
		}
		if (businessPartnerData.getPhoneNumber() != null) {
			com.holcim.logon.logonldsinterface.common_in.PhoneNumber_type3 data = new com.holcim.logon.logonldsinterface.common_in.PhoneNumber_type3();
			data.setPhoneNumber_type2(businessPartnerData.getPhoneNumber());
			wsBusinessPartnerData.setPhoneNumber(data);
		}
		if (businessPartnerData.getVatRegistrationNumber() != null) {
			com.holcim.logon.logonldsinterface.common_in.VatRegistrationNumber_type1 data = new com.holcim.logon.logonldsinterface.common_in.VatRegistrationNumber_type1();
			data.setVatRegistrationNumber_type0(businessPartnerData.getVatRegistrationNumber());
			wsBusinessPartnerData.setVatRegistrationNumber(data);
		}
	}
	
	private void setWsSchedulingData(com.holcim.logon.logonldsinterface.common_in.SchedulingData wsSchedulingData, SchedulingData schedulingData) {
		
		if (schedulingData.isReleasedForLoading() != null) {
			wsSchedulingData.setReleasedForLoading(schedulingData.isReleasedForLoading());
		}
		if (schedulingData.getPlannedLoadingTime() != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(schedulingData.getPlannedLoadingTime());
			wsSchedulingData.setPlannedLoadingTime(cal);
		}
	}
	
	private void setWsMaterialData(com.holcim.logon.logonldsinterface.common_in.MaterialData wsMaterialData, MaterialData materialData) {
		
		if (materialData.getMaterialNo() != null) {
			com.holcim.logon.logonldsinterface.common_in.MaterialNo_type1 data = new com.holcim.logon.logonldsinterface.common_in.MaterialNo_type1();
			data.setMaterialNo_type0(materialData.getMaterialNo());
			wsMaterialData.setMaterialNo(data);
		}
		if (materialData.getContractItemNumber() != null) {
			com.holcim.logon.logonldsinterface.common_in.ContractItemNumber_type1 data = new com.holcim.logon.logonldsinterface.common_in.ContractItemNumber_type1();
			data.setContractItemNumber_type0(materialData.getContractItemNumber());
			wsMaterialData.setContractItemNumber(data);
		}
		if (materialData.getBrandSalesName() != null) {
			com.holcim.logon.logonldsinterface.common_in.BrandSalesName_type1 data = new com.holcim.logon.logonldsinterface.common_in.BrandSalesName_type1();
			data.setBrandSalesName_type0(materialData.getBrandSalesName());
			wsMaterialData.setBrandSalesName(data);
		}
		if (materialData.getSapMaterialDescription() != null) {
			com.holcim.logon.logonldsinterface.common_in.SapMaterialDescription_type1 data = new com.holcim.logon.logonldsinterface.common_in.SapMaterialDescription_type1();
			data.setSapMaterialDescription_type0(materialData.getSapMaterialDescription());
			wsMaterialData.setSapMaterialDescription(data);
		}
		if (materialData.getProdPlantNo() != null) {
			com.holcim.logon.logonldsinterface.common_in.ProdPlantNo_type1 data = new com.holcim.logon.logonldsinterface.common_in.ProdPlantNo_type1();
			data.setProdPlantNo_type0(materialData.getProdPlantNo());
			wsMaterialData.setProdPlantNo(data);
		}
		if (materialData.getMixDesignID() != null) {
			com.holcim.logon.logonldsinterface.common_in.MixDesignID_type1 data = new com.holcim.logon.logonldsinterface.common_in.MixDesignID_type1();
			data.setMixDesignID_type0(materialData.getMixDesignID());
			wsMaterialData.setMixDesignID(data);
		}
		if (materialData.getQuantity() != null) {
			com.holcim.logon.logonldsinterface.common_in.Quantity_type1 data = new com.holcim.logon.logonldsinterface.common_in.Quantity_type1();
			data.setQuantity_type0(materialData.getQuantity());
			wsMaterialData.setQuantity(data);
		}
		if (materialData.getQuantityPerDelivery() != null) {
			com.holcim.logon.logonldsinterface.common_in.QuantityPerDelivery_type1 data = new com.holcim.logon.logonldsinterface.common_in.QuantityPerDelivery_type1();
			data.setQuantityPerDelivery_type0(materialData.getQuantityPerDelivery());
			wsMaterialData.setQuantityPerDelivery(data);
		}
		if (materialData.getQuantityUom() != null) {
			com.holcim.logon.logonldsinterface.common_in.QuantityUom_type1 data = new com.holcim.logon.logonldsinterface.common_in.QuantityUom_type1();
			data.setQuantityUom_type0(materialData.getQuantityUom());
			wsMaterialData.setQuantityUom(data);
		}
		if (materialData.getBaseUom() != null) {
			com.holcim.logon.logonldsinterface.common_in.BaseUom_type1 data = new com.holcim.logon.logonldsinterface.common_in.BaseUom_type1();
			data.setBaseUom_type0(materialData.getBaseUom());
			wsMaterialData.setBaseUom(data);
		}
		if (materialData.getNumerator() != null) {
			com.holcim.logon.logonldsinterface.common_in.Numerator_type1 data = new com.holcim.logon.logonldsinterface.common_in.Numerator_type1();
			data.setNumerator_type0(materialData.getNumerator());
			wsMaterialData.setNumerator(data);
		}
		if (materialData.getDenominator() != null) {
			com.holcim.logon.logonldsinterface.common_in.Denominator_type1 data = new com.holcim.logon.logonldsinterface.common_in.Denominator_type1();
			data.setDenominator_type0(materialData.getDenominator());
			wsMaterialData.setDenominator(data);
		}
		if (materialData.getPlanningMaterialType() != null) {
			com.holcim.logon.logonldsinterface.common_in.PlanningMaterialType_type1 data = new com.holcim.logon.logonldsinterface.common_in.PlanningMaterialType_type1();
			data.setPlanningMaterialType_type0(materialData.getPlanningMaterialType());
			wsMaterialData.setPlanningMaterialType(data);
		}
	}
	
	private void setWsPriceData(com.holcim.logon.logonldsinterface.common_in.PriceData wsPriceData, PriceData priceData) {
		
		if (priceData.getCurrency() != null) {
			com.holcim.logon.logonldsinterface.common_in.Currency_type1 data = new com.holcim.logon.logonldsinterface.common_in.Currency_type1();
			data.setCurrency_type0(priceData.getCurrency());
			wsPriceData.setCurrency(data);
		}
		if (priceData.getNetPrice() != null) {
			com.holcim.logon.logonldsinterface.common_in.NetPrice_type1 data = new com.holcim.logon.logonldsinterface.common_in.NetPrice_type1();
			data.setNetPrice_type0(priceData.getNetPrice());
			wsPriceData.setNetPrice(data);
		}
		if (priceData.getVatValue() != null) {
			com.holcim.logon.logonldsinterface.common_in.VatValue_type1 data = new com.holcim.logon.logonldsinterface.common_in.VatValue_type1();
			data.setVatValue_type0(priceData.getVatValue());
			wsPriceData.setVatValue(data);
		}
		if (priceData.getGrossPrice() != null) {
			com.holcim.logon.logonldsinterface.common_in.GrossPrice_type1 data = new com.holcim.logon.logonldsinterface.common_in.GrossPrice_type1();
			data.setGrossPrice_type0(priceData.getGrossPrice());
			wsPriceData.setGrossPrice(data);
		}
	}
	
	private void setWsShiptoMaterialData(com.holcim.logon.logonldsinterface.common_in.ShiptoMaterialData wsShiptoMaterialData, ShiptoMaterialData shiptoMaterialData) {
		
		if (shiptoMaterialData.getContractNumber() != null) {
			com.holcim.logon.logonldsinterface.common_in.ContractNumber_type3 data = new com.holcim.logon.logonldsinterface.common_in.ContractNumber_type3();
			data.setContractNumber_type2(shiptoMaterialData.getContractNumber());
			wsShiptoMaterialData.setContractNumber(data);
		}
		if (shiptoMaterialData.getSoldTo() != null) {
			com.holcim.logon.logonldsinterface.common_in.SoldTo_type5 data = new com.holcim.logon.logonldsinterface.common_in.SoldTo_type5();
			data.setSoldTo_type4(shiptoMaterialData.getSoldTo());
			wsShiptoMaterialData.setSoldTo(data);
		}
		if (shiptoMaterialData.getShipTo() != null) {
			com.holcim.logon.logonldsinterface.common_in.ShipTo_type5 data = new com.holcim.logon.logonldsinterface.common_in.ShipTo_type5();
			data.setShipTo_type4(shiptoMaterialData.getShipTo());
			wsShiptoMaterialData.setShipTo(data);
		}
		if (shiptoMaterialData.getIncoterms() != null) {
			com.holcim.logon.logonldsinterface.common_in.Incoterms_type3 data = new com.holcim.logon.logonldsinterface.common_in.Incoterms_type3();
			data.setIncoterms_type2(shiptoMaterialData.getIncoterms());
			wsShiptoMaterialData.setIncoterms(data);
		}
		if (shiptoMaterialData.getContractItemNumber() != null) {
			com.holcim.logon.logonldsinterface.common_in.ContractItemNumber_type5 data = new com.holcim.logon.logonldsinterface.common_in.ContractItemNumber_type5();
			data.setContractItemNumber_type4(shiptoMaterialData.getContractItemNumber());
			wsShiptoMaterialData.setContractItemNumber(data);
		}
		if (shiptoMaterialData.getMaterialNo() != null) {
			com.holcim.logon.logonldsinterface.common_in.MaterialNo_type15 data =
					new com.holcim.logon.logonldsinterface.common_in.MaterialNo_type15();
			data.setMaterialNo_type14(shiptoMaterialData.getMaterialNo());
			wsShiptoMaterialData.setMaterialNo(data);
		}
		if (shiptoMaterialData.getProdPlantNo() != null) {
			com.holcim.logon.logonldsinterface.common_in.ProdPlantNo_type9 data = new com.holcim.logon.logonldsinterface.common_in.ProdPlantNo_type9();
			data.setProdPlantNo_type8(shiptoMaterialData.getProdPlantNo());
			wsShiptoMaterialData.setProdPlantNo(data);
		}
		if (shiptoMaterialData.getPriceData() != null) {
			com.holcim.logon.logonldsinterface.common_in.PriceData wsPriceData = new com.holcim.logon.logonldsinterface.common_in.PriceData();
			setWsPriceData(wsPriceData, shiptoMaterialData.getPriceData());
			wsShiptoMaterialData.setPriceData(wsPriceData);
		}
	}
	
	private void setWsReducedOrderItemData(com.holcim.logon.logonldsinterface.common_in.ReducedOrderItemData wsOrderItemData, ReducedOrderItemData orderItemData) {
		
		if (orderItemData.getLogonOrderItemNumber() != null) {
			com.holcim.logon.logonldsinterface.common_in.LogonOrderItemNumber_type5 data = new com.holcim.logon.logonldsinterface.common_in.LogonOrderItemNumber_type5();
			data.setLogonOrderItemNumber_type4((orderItemData.getLogonOrderItemNumber()));
			wsOrderItemData.setLogonOrderItemNumber(data);
		}
	}
	
	private void setWsOrderItemData(com.holcim.logon.logonldsinterface.common_in.OrderItemData wsOrderItemData, OrderItemData orderItemData) {
		
		if (orderItemData.getLogonOrderItemNumber() != null) {
			com.holcim.logon.logonldsinterface.common_in.LogonOrderItemNumber_type1 data = new com.holcim.logon.logonldsinterface.common_in.LogonOrderItemNumber_type1();
			data.setLogonOrderItemNumber_type0(orderItemData.getLogonOrderItemNumber());
			wsOrderItemData.setLogonOrderItemNumber(data);
		}
		if (orderItemData.getLdsOrderItemNumber() != null) {
			com.holcim.logon.logonldsinterface.common_in.LdsOrderItemNumber_type1 data = new com.holcim.logon.logonldsinterface.common_in.LdsOrderItemNumber_type1();
			data.setLdsOrderItemNumber_type0(orderItemData.getLdsOrderItemNumber());
			wsOrderItemData.setLdsOrderItemNumber(data);
		}
		if (orderItemData.getLoadId() != null) {
			com.holcim.logon.logonldsinterface.common_in.LoadId_type1 data = new com.holcim.logon.logonldsinterface.common_in.LoadId_type1();
			data.setLoadId_type0(orderItemData.getLoadId());
			wsOrderItemData.setLoadId(data);
		}
		if (orderItemData.getMaterial() != null) {
			com.holcim.logon.logonldsinterface.common_in.MaterialData wsMaterialData = new com.holcim.logon.logonldsinterface.common_in.MaterialData();
			setWsMaterialData(wsMaterialData, orderItemData.getMaterial());
			wsOrderItemData.setMaterial(wsMaterialData);
		}
		if (orderItemData.getLoadingpointNo() != null) {
			com.holcim.logon.logonldsinterface.common_in.LoadingpointNo_type1 data = new com.holcim.logon.logonldsinterface.common_in.LoadingpointNo_type1();
			data.setLoadingpointNo_type0(orderItemData.getLoadingpointNo());
			wsOrderItemData.setLoadingpointNo(data);
		}
		
		if (orderItemData.getLoadingpointName() != null) {
			com.holcim.logon.logonldsinterface.common_in.LoadingpointName_type1 data = new com.holcim.logon.logonldsinterface.common_in.LoadingpointName_type1();
			String loadingPointName = orderItemData.getLoadingpointName();
			data.setLoadingpointName_type0(loadingPointName);
			wsOrderItemData.setLoadingpointName(data);
		}
		if (orderItemData.getPossibleMaterial() != null) {
			com.holcim.logon.logonldsinterface.common_in.PossibleMaterial_type0[] list = new com.holcim.logon.logonldsinterface.common_in.PossibleMaterial_type0[orderItemData.getPossibleMaterial().size()];
			for (int i=0; i<orderItemData.getPossibleMaterial().size(); i++) {
				PossibleMaterial possibleMaterial = orderItemData.getPossibleMaterial().get(i);
				com.holcim.logon.logonldsinterface.common_in.PossibleMaterial_type0 wsPossibleMaterial = new com.holcim.logon.logonldsinterface.common_in.PossibleMaterial_type0();
				if (possibleMaterial.getMaterialNo() != null) {
					com.holcim.logon.logonldsinterface.common_in.MaterialNo_type3 data = new com.holcim.logon.logonldsinterface.common_in.MaterialNo_type3();
					data.setMaterialNo_type2(possibleMaterial.getMaterialNo());
					wsPossibleMaterial.setMaterialNo(data);
				}
				if (possibleMaterial.getProdPlantNo() != null) {
					com.holcim.logon.logonldsinterface.common_in.ProdPlantNo_type3 data = new com.holcim.logon.logonldsinterface.common_in.ProdPlantNo_type3();
					data.setProdPlantNo_type2(possibleMaterial.getProdPlantNo());
					wsPossibleMaterial.setProdPlantNo(data);
				}
				list[i] = wsPossibleMaterial;
			}
			wsOrderItemData.setPossibleMaterial(list);
		}
		if (orderItemData.getPriceData() != null) {
			com.holcim.logon.logonldsinterface.common_in.PriceData wsPriceData = new com.holcim.logon.logonldsinterface.common_in.PriceData();
			setWsPriceData(wsPriceData, orderItemData.getPriceData());
			wsOrderItemData.setPriceData(wsPriceData);
		}
	}
	
	private void setWsServiceMaterialData(com.holcim.logon.logonldsinterface.common_in.ServiceMaterialData wsServiceMaterialData, ServiceMaterialData serviceMaterialData) {
		
		if (serviceMaterialData.getMaterial() != null) {
			com.holcim.logon.logonldsinterface.common_in.MaterialData wsMaterialData = new com.holcim.logon.logonldsinterface.common_in.MaterialData();
			setWsMaterialData(wsMaterialData, serviceMaterialData.getMaterial());
			wsServiceMaterialData.setMaterial(wsMaterialData);
		}
		if (serviceMaterialData.getPriceData() != null) {
			com.holcim.logon.logonldsinterface.common_in.PriceData wsPriceData = new com.holcim.logon.logonldsinterface.common_in.PriceData();
			setWsPriceData(wsPriceData, serviceMaterialData.getPriceData());
			wsServiceMaterialData.setPriceData(wsPriceData);
		}		
	}
	
	private void setWsReducedOrderData(com.holcim.logon.logonldsinterface.common_in.ReducedOrderData wsOrderData, ReducedOrderData orderData) {
		if (orderData.getLogonOrderNo() != null) {
			com.holcim.logon.logonldsinterface.common_in.LogonOrderNo_type15 data =
					new com.holcim.logon.logonldsinterface.common_in.LogonOrderNo_type15();
			data.setLogonOrderNo_type14(orderData.getLogonOrderNo());
			wsOrderData.setLogonOrderNo(data);
		}
		if (orderData.getOrderValidationCode() != null) {
			com.holcim.logon.logonldsinterface.common_in.OrderValidationCode_type7 data = new com.holcim.logon.logonldsinterface.common_in.OrderValidationCode_type7();
			data.setOrderValidationCode_type6(orderData.getOrderValidationCode());
			wsOrderData.setOrderValidationCode(data);
		}
		if (orderData.getOrderItem() != null) {
			com.holcim.logon.logonldsinterface.common_in.ReducedOrderItemData[] data = new com.holcim.logon.logonldsinterface.common_in.ReducedOrderItemData[orderData.getOrderItem().size()];
			for (int i=0; i<orderData.getOrderItem().size(); i++) {
				ReducedOrderItemData orderItemData = orderData.getOrderItem().get(i);
				com.holcim.logon.logonldsinterface.common_in.ReducedOrderItemData wsOrderItemData = new com.holcim.logon.logonldsinterface.common_in.ReducedOrderItemData();
				setWsReducedOrderItemData(wsOrderItemData, orderItemData);
				data[i] = wsOrderItemData;
			}
			wsOrderData.setOrderItem(data);
		}
	}
	
	private void setWsOrderData(com.holcim.logon.logonldsinterface.common_in.OrderData wsOrderData, OrderData orderData) {
		
		if (orderData.getLogonCustomerOrderNo() != null) {
			com.holcim.logon.logonldsinterface.common_in.LogonCustomerOrderNo_type1 data = new com.holcim.logon.logonldsinterface.common_in.LogonCustomerOrderNo_type1();
			data.setLogonCustomerOrderNo_type0(orderData.getLogonCustomerOrderNo());
			wsOrderData.setLogonCustomerOrderNo(data);
		}
		if (orderData.getLdsCustomerOrderNo() != null) {
			com.holcim.logon.logonldsinterface.common_in.LdsCustomerOrderNo_type1 data = new com.holcim.logon.logonldsinterface.common_in.LdsCustomerOrderNo_type1();
			data.setLdsCustomerOrderNo_type0(orderData.getLdsCustomerOrderNo());
			wsOrderData.setLdsCustomerOrderNo(data);
		}
		if (orderData.getLogonOrderNo() != null) {
			com.holcim.logon.logonldsinterface.common_in.LogonOrderNo_type1 data = new com.holcim.logon.logonldsinterface.common_in.LogonOrderNo_type1();
			data.setLogonOrderNo_type0(orderData.getLogonOrderNo());
			wsOrderData.setLogonOrderNo(data);
		}
		if (orderData.getSequentialLogonOrderNo() != null) {
			com.holcim.logon.logonldsinterface.common_in.SequentialLogonOrderNo_type1 data = new com.holcim.logon.logonldsinterface.common_in.SequentialLogonOrderNo_type1();
			data.setSequentialLogonOrderNo_type0(orderData.getSequentialLogonOrderNo());
			wsOrderData.setSequentialLogonOrderNo(data);
		}
		if (orderData.getContractType() != null) {
			com.holcim.logon.logonldsinterface.common_in.ContractType_type1 data = new com.holcim.logon.logonldsinterface.common_in.ContractType_type1();
			data.setContractType_type0(orderData.getContractType());
			wsOrderData.setContractType(data);
		}
		if (orderData.getContractNumber() != null) {
			com.holcim.logon.logonldsinterface.common_in.ContractNumber_type1 data = new com.holcim.logon.logonldsinterface.common_in.ContractNumber_type1();
			data.setContractNumber_type0(orderData.getContractNumber());
			wsOrderData.setContractNumber(data);
		}
		if (orderData.getIncoterms() != null) {
			com.holcim.logon.logonldsinterface.common_in.Incoterms_type1 data = new com.holcim.logon.logonldsinterface.common_in.Incoterms_type1();
			data.setIncoterms_type0(orderData.getIncoterms());
			wsOrderData.setIncoterms(data);
		}
		if (orderData.getCustorderCustrefNo() != null) {
			com.holcim.logon.logonldsinterface.common_in.CustorderCustrefNo_type1 data = new com.holcim.logon.logonldsinterface.common_in.CustorderCustrefNo_type1();
			data.setCustorderCustrefNo_type0(orderData.getCustorderCustrefNo());
			wsOrderData.setCustorderCustrefNo(data);
		}
		if (orderData.getCommercialCarrier() != null) {
			com.holcim.logon.logonldsinterface.common_in.VendorData wsVendorData = new com.holcim.logon.logonldsinterface.common_in.VendorData();
			setWsVendorData(wsVendorData, orderData.getCommercialCarrier());
			wsOrderData.setCommercialCarrier(wsVendorData);
		}
		if (orderData.getExecutingCarrier() != null) {
			com.holcim.logon.logonldsinterface.common_in.VendorData wsVendorData = new com.holcim.logon.logonldsinterface.common_in.VendorData();
			setWsVendorData(wsVendorData, orderData.getExecutingCarrier());
			wsOrderData.setExecutingCarrier(wsVendorData);
		}
		if (orderData.getSoldTo() != null) {
			com.holcim.logon.logonldsinterface.common_in.BusinessPartnerData wsBusinessPartnerData = new com.holcim.logon.logonldsinterface.common_in.BusinessPartnerData();
			setWsBusinessPartnerData(wsBusinessPartnerData, orderData.getSoldTo());
			wsOrderData.setSoldTo(wsBusinessPartnerData);
		}
		if (orderData.getShipTo() != null) {
			com.holcim.logon.logonldsinterface.common_in.BusinessPartnerData wsBusinessPartnerData = new com.holcim.logon.logonldsinterface.common_in.BusinessPartnerData();
			setWsBusinessPartnerData(wsBusinessPartnerData, orderData.getShipTo());
			wsOrderData.setShipTo(wsBusinessPartnerData);
		}
		if (orderData.getBillTo() != null) {
			com.holcim.logon.logonldsinterface.common_in.BusinessPartnerData wsBusinessPartnerData = new com.holcim.logon.logonldsinterface.common_in.BusinessPartnerData();
			setWsBusinessPartnerData(wsBusinessPartnerData, orderData.getBillTo());
			wsOrderData.setBillTo(wsBusinessPartnerData);
		}
		if (orderData.getPayer() != null) {
			com.holcim.logon.logonldsinterface.common_in.BusinessPartnerData wsBusinessPartnerData = new com.holcim.logon.logonldsinterface.common_in.BusinessPartnerData();
			setWsBusinessPartnerData(wsBusinessPartnerData, orderData.getPayer());
			wsOrderData.setPayer(wsBusinessPartnerData);
		}
		if (orderData.getSupplyPlantNo() != null) {
			com.holcim.logon.logonldsinterface.common_in.SupplyPlantNo_type1 data = new com.holcim.logon.logonldsinterface.common_in.SupplyPlantNo_type1();
			data.setSupplyPlantNo_type0(orderData.getSupplyPlantNo());
			wsOrderData.setSupplyPlantNo(data);
		}
		if (orderData.getShippingpointNo() != null) {
			com.holcim.logon.logonldsinterface.common_in.ShippingpointNo_type1 data = new com.holcim.logon.logonldsinterface.common_in.ShippingpointNo_type1();
			data.setShippingpointNo_type0(orderData.getShippingpointNo());
			wsOrderData.setShippingpointNo(data);
		}
		if (orderData.getDeliveryType() != null) {
			com.holcim.logon.logonldsinterface.common_in.DeliveryType_type1 data = new com.holcim.logon.logonldsinterface.common_in.DeliveryType_type1();
			data.setDeliveryType_type0(orderData.getDeliveryType());
			wsOrderData.setDeliveryType(data);
		}
		if (orderData.getNoteDelivery() != null) {
			com.holcim.logon.logonldsinterface.common_in.NoteDelivery_type1 data = new com.holcim.logon.logonldsinterface.common_in.NoteDelivery_type1();
			data.setNoteDelivery_type0(orderData.getNoteDelivery());
			wsOrderData.setNoteDelivery(data);
		}
		if (orderData.getNoteScheduling() != null) {
			com.holcim.logon.logonldsinterface.common_in.NoteScheduling_type1 data = new com.holcim.logon.logonldsinterface.common_in.NoteScheduling_type1();
			data.setNoteScheduling_type0(orderData.getNoteScheduling());
			wsOrderData.setNoteScheduling(data);
		}
		if (orderData.getDriverComment() != null) {
			com.holcim.logon.logonldsinterface.common_in.DriverComment_type1 data = new com.holcim.logon.logonldsinterface.common_in.DriverComment_type1();
			data.setDriverComment_type0(orderData.getDriverComment());
			wsOrderData.setDriverComment(data);
		}
		if (orderData.isAutoScheduleRelease() != null) {
			wsOrderData.setAutoScheduleRelease(orderData.isAutoScheduleRelease());
		}
		if (orderData.getEarliestDeliveryTime() != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(orderData.getEarliestDeliveryTime());
			wsOrderData.setEarliestDeliveryTime(cal);
		}
		if (orderData.getLatestDeliveryTime() != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(orderData.getLatestDeliveryTime());
			wsOrderData.setLatestDeliveryTime(cal);
		}
		if (orderData.getPlannedDeliveryTime() != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(orderData.getPlannedDeliveryTime());
			wsOrderData.setPlannedDeliveryTime(cal);
		}
		if (orderData.getDeliveryFreqDelayBtwnDeliveries() != null) {
			com.holcim.logon.logonldsinterface.common_in.DeliveryFreqDelayBtwnDeliveries_type1 data = new com.holcim.logon.logonldsinterface.common_in.DeliveryFreqDelayBtwnDeliveries_type1();
			data.setDeliveryFreqDelayBtwnDeliveries_type0(orderData.getDeliveryFreqDelayBtwnDeliveries());
			wsOrderData.setDeliveryFreqDelayBtwnDeliveries(data);
		}
		if (orderData.getDeliveryFreqDelayBtwnDeliveriesUom() != null) {
			com.holcim.logon.logonldsinterface.common_in.DeliveryFreqDelayBtwnDeliveriesUom_type1 data = new com.holcim.logon.logonldsinterface.common_in.DeliveryFreqDelayBtwnDeliveriesUom_type1();
			data.setDeliveryFreqDelayBtwnDeliveriesUom_type0(orderData.getDeliveryFreqDelayBtwnDeliveriesUom());
			wsOrderData.setDeliveryFreqDelayBtwnDeliveriesUom(data);
		}
		if (orderData.getDeliveryFreqQtyPerHour() != null) {
			com.holcim.logon.logonldsinterface.common_in.DeliveryFreqQtyPerHour_type1 data = new com.holcim.logon.logonldsinterface.common_in.DeliveryFreqQtyPerHour_type1();
			data.setDeliveryFreqQtyPerHour_type0(orderData.getDeliveryFreqQtyPerHour());
			wsOrderData.setDeliveryFreqQtyPerHour(data);
		}
		if (orderData.getDeliveryFreqQtyPerHourUom() != null) {
			com.holcim.logon.logonldsinterface.common_in.DeliveryFreqQtyPerHourUom_type1 data = new com.holcim.logon.logonldsinterface.common_in.DeliveryFreqQtyPerHourUom_type1();
			data.setDeliveryFreqQtyPerHourUom_type0(orderData.getDeliveryFreqQtyPerHourUom());
			wsOrderData.setDeliveryFreqQtyPerHourUom(data);
		}
		if (orderData.getDeliveryTimeTolerance() != null) {
			com.holcim.logon.logonldsinterface.common_in.DeliveryTimeTolerance_type1 data = new com.holcim.logon.logonldsinterface.common_in.DeliveryTimeTolerance_type1();
			data.setDeliveryTimeTolerance_type0(orderData.getDeliveryTimeTolerance());
			wsOrderData.setDeliveryTimeTolerance(data);
		}
		if (orderData.isPrintDeliveryNoteLds() != null) {
			wsOrderData.setPrintDeliveryNoteLds(orderData.isPrintDeliveryNoteLds());
		}
		if (orderData.isPrintCollectiveDeliveryNote() != null) {
			wsOrderData.setPrintCollectiveDeliveryNote(orderData.isPrintCollectiveDeliveryNote());
		}
		if (orderData.getChromReduce() != null) {
			com.holcim.logon.logonldsinterface.common_in.ChromReduce_type1 data = new com.holcim.logon.logonldsinterface.common_in.ChromReduce_type1();
			data.setChromReduce_type0(orderData.getChromReduce());
			wsOrderData.setChromReduce(data);
		}
		if (orderData.getOrderValidationCode() != null) {
			com.holcim.logon.logonldsinterface.common_in.OrderValidationCode_type1 data = new com.holcim.logon.logonldsinterface.common_in.OrderValidationCode_type1();
			data.setOrderValidationCode_type0(orderData.getOrderValidationCode());
			wsOrderData.setOrderValidationCode(data);
		}
		if (orderData.getOrderItem() != null) {
			com.holcim.logon.logonldsinterface.common_in.OrderItemData[] data = new com.holcim.logon.logonldsinterface.common_in.OrderItemData[orderData.getOrderItem().size()];
			for (int i=0; i<orderData.getOrderItem().size(); i++) {
				OrderItemData orderItemData = orderData.getOrderItem().get(i);
				com.holcim.logon.logonldsinterface.common_in.OrderItemData wsOrderItemData = new com.holcim.logon.logonldsinterface.common_in.OrderItemData();
				setWsOrderItemData(wsOrderItemData, orderItemData);
				data[i] = wsOrderItemData;
			}
			wsOrderData.setOrderItem(data);
		}
		if (orderData.getServiceMaterial() != null) {
			com.holcim.logon.logonldsinterface.common_in.ServiceMaterialData[] data = new com.holcim.logon.logonldsinterface.common_in.ServiceMaterialData[orderData.getServiceMaterial().size()];
			for (int i=0; i<orderData.getServiceMaterial().size(); i++) {
				ServiceMaterialData serviceMaterialData = orderData.getServiceMaterial().get(i);
				com.holcim.logon.logonldsinterface.common_in.ServiceMaterialData wsServiceMaterialData = new com.holcim.logon.logonldsinterface.common_in.ServiceMaterialData();
				setWsServiceMaterialData(wsServiceMaterialData, serviceMaterialData);
				data[i] = wsServiceMaterialData;
			}
			wsOrderData.setServiceMaterial(data);
		}
		if (orderData.isExactDeliveryQuantity() != null) {
			wsOrderData.setExactDeliveryQuantity(orderData.isExactDeliveryQuantity());
		}
		if (orderData.isExactCustomerOrderQuantity() != null) {
			wsOrderData.setExactCustomerOrderQuantity(orderData.isExactCustomerOrderQuantity());
		}
		if (orderData.getOrderPriceData() != null) {
			com.holcim.logon.logonldsinterface.common_in.PriceData wsPriceData = new com.holcim.logon.logonldsinterface.common_in.PriceData();
			setWsPriceData(wsPriceData, orderData.getOrderPriceData());
			wsOrderData.setOrderPriceData(wsPriceData);
		}
	}

	private void setWsReducedLoadOrderItemData(com.holcim.logon.logonldsinterface.common_in.ReducedLoadOrderItemData wsLoadOrderItemData, ReducedLoadOrderItemData loadOrderItemData) {
		if (loadOrderItemData.getLoadOrderItemId() != null) {
			com.holcim.logon.logonldsinterface.common_in.LoadOrderItemId_type3 data = new com.holcim.logon.logonldsinterface.common_in.LoadOrderItemId_type3();
			data.setLoadOrderItemId_type2(loadOrderItemData.getLoadOrderItemId());
			wsLoadOrderItemData.setLoadOrderItemId(data);
		}
	}
	
	private void setWsLoadOrderItemData(com.holcim.logon.logonldsinterface.common_in.LoadOrderItemData wsLoadOrderItemData, LoadOrderItemData loadOrderItemData) {
		
		if (loadOrderItemData.getLoadOrderItemId() != null) {
			com.holcim.logon.logonldsinterface.common_in.LoadOrderItemId_type1 data = new com.holcim.logon.logonldsinterface.common_in.LoadOrderItemId_type1();
			data.setLoadOrderItemId_type0(loadOrderItemData.getLoadOrderItemId());
			wsLoadOrderItemData.setLoadOrderItemId(data);
		}
		if (loadOrderItemData.getMaterial() != null) {
			com.holcim.logon.logonldsinterface.common_in.MaterialData wsMaterialData = new com.holcim.logon.logonldsinterface.common_in.MaterialData();
			setWsMaterialData(wsMaterialData, loadOrderItemData.getMaterial());
			wsLoadOrderItemData.setMaterial(wsMaterialData);
		}
		if (loadOrderItemData.getLoadingpointNo() != null) {
			com.holcim.logon.logonldsinterface.common_in.LoadingpointNo_type5 data = new com.holcim.logon.logonldsinterface.common_in.LoadingpointNo_type5();
			data.setLoadingpointNo_type4(loadOrderItemData.getLoadingpointNo());
			wsLoadOrderItemData.setLoadingpointNo(data);
		}
		if (loadOrderItemData.isExactDeliveryQuantity() != null) {
			wsLoadOrderItemData.setExactDeliveryQuantity(loadOrderItemData.isExactDeliveryQuantity());
		}
		if (loadOrderItemData.getChamberNo() != null) {
			com.holcim.logon.logonldsinterface.common_in.ChamberNo_type1 data = new com.holcim.logon.logonldsinterface.common_in.ChamberNo_type1();
			data.setChamberNo_type0(loadOrderItemData.getChamberNo());
			wsLoadOrderItemData.setChamberNo(data);
		}
	}

	private void setWsReducedLoadOrderData(com.holcim.logon.logonldsinterface.common_in.ReducedLoadOrderData wsLoadOrderData, ReducedLoadOrderData loadOrderData) {
		if (loadOrderData.getLoadOrderId() != null) {
			com.holcim.logon.logonldsinterface.common_in.LoadOrderId_type9 data = new com.holcim.logon.logonldsinterface.common_in.LoadOrderId_type9();
			data.setLoadOrderId_type8(loadOrderData.getLoadOrderId());
			wsLoadOrderData.setLoadOrderId(data);
		}
		if (loadOrderData.getLoadOrderItem() != null) {
			com.holcim.logon.logonldsinterface.common_in.ReducedLoadOrderItemData[] data = new com.holcim.logon.logonldsinterface.common_in.ReducedLoadOrderItemData[loadOrderData.getLoadOrderItem().size()];
			for (int i=0; i<loadOrderData.getLoadOrderItem().size(); i++) {
				ReducedLoadOrderItemData loadOrderItemData = loadOrderData.getLoadOrderItem().get(i);
				com.holcim.logon.logonldsinterface.common_in.ReducedLoadOrderItemData wsLoadOrderItemData = new com.holcim.logon.logonldsinterface.common_in.ReducedLoadOrderItemData();
				setWsReducedLoadOrderItemData(wsLoadOrderItemData, loadOrderItemData);
				data[i] = wsLoadOrderItemData;
			}
			wsLoadOrderData.setLoadOrderItem(data);
		}
		if (loadOrderData.getOrder() != null) {
			com.holcim.logon.logonldsinterface.common_in.ReducedOrderData[] data = new com.holcim.logon.logonldsinterface.common_in.ReducedOrderData[loadOrderData.getOrder().size()];
			for (int i=0; i<loadOrderData.getOrder().size(); i++) {
				ReducedOrderData orderData = loadOrderData.getOrder().get(i);
				com.holcim.logon.logonldsinterface.common_in.ReducedOrderData wsOrderData = new com.holcim.logon.logonldsinterface.common_in.ReducedOrderData();
				setWsReducedOrderData(wsOrderData, orderData);
				data[i] = wsOrderData;
			}
			wsLoadOrderData.setOrder(data);
		}
	}

	private void setWsCreatedOrderData(com.holcim.logon.logonldsinterface.common_in.CreatedOrderData wsCreatedOrderData,
			CreatedOrderData createdOrderData) {

		if (createdOrderData.getOrder() != null) {
			com.holcim.logon.logonldsinterface.common_in.CreatedOrder[] data = new com.holcim.logon.logonldsinterface.common_in.CreatedOrder[createdOrderData.getOrder().size()];
			for (int i=0; i<createdOrderData.getOrder().size(); i++) {
				CreatedOrder createdOrder = createdOrderData.getOrder().get(i);
				com.holcim.logon.logonldsinterface.common_in.CreatedOrder wsCreatedOrder = new com.holcim.logon.logonldsinterface.common_in.CreatedOrder();
				setWsCreatedOrder(wsCreatedOrder, createdOrder);
				data[i] = wsCreatedOrder;
			}
			wsCreatedOrderData.setOrder(data);
		}
		if (createdOrderData.isWarningMultipleDispocard() != null) {
			wsCreatedOrderData.setWarningMultipleDispocard(createdOrderData.isWarningMultipleDispocard());
		}
	}

	private void setWsCreatedOrder(com.holcim.logon.logonldsinterface.common_in.CreatedOrder wsCreatedOrder,
			CreatedOrder createdOrder) {
		if (createdOrder.getLogonOrderNo() != null) {
			com.holcim.logon.logonldsinterface.common_in.LogonOrderNo_type23 data = new com.holcim.logon.logonldsinterface
					.common_in.LogonOrderNo_type23();
			data.setLogonOrderNo_type22(createdOrder.getLogonOrderNo());
			wsCreatedOrder.setLogonOrderNo(data);
		}
		if (createdOrder.getOrderStatusCd() != null) {
			com.holcim.logon.logonldsinterface.common_in.OrderStatusCd_type1 data = new com.holcim.logon.logonldsinterface.common_in.OrderStatusCd_type1();
			data.setOrderStatusCd_type0(createdOrder.getOrderStatusCd());
			wsCreatedOrder.setOrderStatusCd(data);
		}
		if (createdOrder.getCommercialCarrier() != null) {
			com.holcim.logon.logonldsinterface.common_in.VendorData data = new com.holcim.logon.logonldsinterface.common_in.VendorData();
			setWsVendorData(data, createdOrder.getCommercialCarrier());
			wsCreatedOrder.setCommercialCarrier(data);
		}
		if (createdOrder.getExecutingCarrier() != null) {
			com.holcim.logon.logonldsinterface.common_in.VendorData data = new com.holcim.logon.logonldsinterface.common_in.VendorData();
			setWsVendorData(data, createdOrder.getExecutingCarrier());
			wsCreatedOrder.setExecutingCarrier(data);
		}
		if (createdOrder.getPoolName() != null) {
			com.holcim.logon.logonldsinterface.common_in.PoolName_type1 data = new com.holcim.logon.logonldsinterface.common_in.PoolName_type1();
			data.setPoolName_type0(createdOrder.getPoolName());
			wsCreatedOrder.setPoolName(data);
		}

	}
	private void setWsLoadOrderData(com.holcim.logon.logonldsinterface.common_in.LoadOrderData wsLoadOrderData, LoadOrderData loadOrderData) {

		if (loadOrderData.getLoadOrderId() != null) {
			com.holcim.logon.logonldsinterface.common_in.LoadOrderId_type5 data = new com.holcim.logon.logonldsinterface.common_in.LoadOrderId_type5();
			data.setLoadOrderId_type4(loadOrderData.getLoadOrderId());
			wsLoadOrderData.setLoadOrderId(data);
		}
		if (loadOrderData.getSupplyPlantNo() != null) {
			com.holcim.logon.logonldsinterface.common_in.SupplyPlantNo_type7 data = new com.holcim.logon.logonldsinterface.common_in.SupplyPlantNo_type7();
			data.setSupplyPlantNo_type6(loadOrderData.getSupplyPlantNo());
			wsLoadOrderData.setSupplyPlantNo(data);
		}
		if (loadOrderData.getShippingpointNo() != null) {
			com.holcim.logon.logonldsinterface.common_in.ShippingpointNo_type7 data = new com.holcim.logon.logonldsinterface.common_in.ShippingpointNo_type7();
			data.setShippingpointNo_type6(loadOrderData.getShippingpointNo());
			wsLoadOrderData.setShippingpointNo(data);
		}
		if (loadOrderData.getLoadOrderItem() != null) {
			com.holcim.logon.logonldsinterface.common_in.LoadOrderItemData[] data = new com.holcim.logon.logonldsinterface.common_in.LoadOrderItemData[loadOrderData.getLoadOrderItem().size()];
			for (int i=0; i<loadOrderData.getLoadOrderItem().size(); i++) {
				LoadOrderItemData loadOrderItemData = loadOrderData.getLoadOrderItem().get(i);
				com.holcim.logon.logonldsinterface.common_in.LoadOrderItemData wsLoadOrderItemData = new com.holcim.logon.logonldsinterface.common_in.LoadOrderItemData();
				setWsLoadOrderItemData(wsLoadOrderItemData, loadOrderItemData);
				data[i] = wsLoadOrderItemData;
			}
			wsLoadOrderData.setLoadOrderItem(data);
		}
		if (loadOrderData.getScheduling() != null) {
			com.holcim.logon.logonldsinterface.common_in.SchedulingData wsSchedulingData = new com.holcim.logon.logonldsinterface.common_in.SchedulingData();
			setWsSchedulingData(wsSchedulingData, loadOrderData.getScheduling());
			wsLoadOrderData.setScheduling(wsSchedulingData);
		}
		if (loadOrderData.getVehicle() != null) {
			com.holcim.logon.logonldsinterface.common_in.VehicleData wsVehicleData = new com.holcim.logon.logonldsinterface.common_in.VehicleData();
			setWsVehicleData(wsVehicleData, loadOrderData.getVehicle());
			wsLoadOrderData.setVehicle(wsVehicleData);
		}
		if (loadOrderData.getOrder() != null) {
			com.holcim.logon.logonldsinterface.common_in.OrderData[] data = new com.holcim.logon.logonldsinterface.common_in.OrderData[loadOrderData.getOrder().size()];
			for (int i=0; i<loadOrderData.getOrder().size(); i++) {
				OrderData orderData = loadOrderData.getOrder().get(i);
				com.holcim.logon.logonldsinterface.common_in.OrderData wsOrderData = new com.holcim.logon.logonldsinterface.common_in.OrderData();
				setWsOrderData(wsOrderData, orderData);
				data[i] = wsOrderData;
			}
			wsLoadOrderData.setOrder(data);
		}
		if (loadOrderData.getNoteLds() != null) {
			com.holcim.logon.logonldsinterface.common_in.NoteLds_type1 data = new com.holcim.logon.logonldsinterface.common_in.NoteLds_type1();
			data.setNoteLds_type0(loadOrderData.getNoteLds());
			wsLoadOrderData.setNoteLds(data);
		}
	}

	public CancelLoadingResponse cancelLoading(CancelLoadingRequest wsRequest) {
		// [[
		CancelLoadingResponse result = null;
		Object[][] params = {{"wsRequest", wsRequest}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "cancelLoading", params);
		}
		// ]]
		try {
			
			CancelLoadingResponse wsResponse = new CancelLoadingResponse();
			try {
				com.holcim.logon.eventbeans.in.logon_lds_interface.CancelLoadingRequest request = LDS_INTERFACE_OBJECT_FACTORY.createCancelLoadingRequest();
				copyWsRequestHeader(request, wsRequest);
				if (wsRequest.getLoadOrderId() != null) {
					request.setLoadOrderId(wsRequest.getLoadOrderId().getLoadOrderId_type0());
				}
				
				Response response = processLogonEventServiceBean(request, Constant.INTERFACE_TYPE_LOGON_LDS_INTERFACE_10, params);
				setWsResponse(wsResponse, response, wsRequest.getHeader().getLdsRequestId());
			}
			catch (Throwable t) {
				setWsResponse(wsResponse, wsRequest.getHeader().getLdsRequestId(), t);
			}
			
			return result = wsResponse;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public CreateCustomerOrderResponse createCustomerOrder(
			CreateCustomerOrderRequest wsRequest) {
		// [[
		CreateCustomerOrderResponse result = null;
		Object[][] params = {{"wsRequest", wsRequest}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createCustomerOrder", params);
		}
		// ]]
		Response response = null;
		try {
			
			CreateCustomerOrderResponse wsResponse = new CreateCustomerOrderResponse();
			try {
				com.holcim.logon.eventbeans.in.logon_lds_interface.CreateCustomerOrderRequest request = LDS_INTERFACE_OBJECT_FACTORY.createCreateCustomerOrderRequest();
				copyWsRequestHeader(request, wsRequest);
				if (wsRequest.getOrder() != null) {
					request.setOrder(new OrderData());
					copyWsOrderData(request.getOrder(), wsRequest.getOrder());
				}
				
				response = processLogonEventServiceBean(request, Constant.INTERFACE_TYPE_LOGON_LDS_INTERFACE_10, params);					
				
				setWsResponse(wsResponse, response, wsRequest.getHeader().getLdsRequestId());
				if (response instanceof com.holcim.logon.eventbeans.in.logon_lds_interface.CreateCustomerOrderResponse) {
					com.holcim.logon.eventbeans.in.logon_lds_interface.CreateCustomerOrderResponse createCustomerOrderResponse = (com.holcim.logon.eventbeans.in.logon_lds_interface.CreateCustomerOrderResponse)response;
					if (createCustomerOrderResponse.getCustomerOrderNo() != null) {
						com.holcim.logon.logonldsinterface.services.CustomerOrderNo_type3 wsLogonCustomerOrderNo = new com.holcim.logon.logonldsinterface.services.CustomerOrderNo_type3();
						wsLogonCustomerOrderNo.setCustomerOrderNo_type2(createCustomerOrderResponse.getCustomerOrderNo());
						wsResponse.setCustomerOrderNo(wsLogonCustomerOrderNo);						
					}
					
					List<ReducedLoadOrderData> loadOrderDataList = createCustomerOrderResponse.getLoadOrderData();
					if (loadOrderDataList != null) {
						com.holcim.logon.logonldsinterface.common_in.ReducedLoadOrderData[] wsLoadOrderDataArray = new com.holcim.logon.logonldsinterface.common_in.ReducedLoadOrderData[loadOrderDataList.size()];
						for (int i = 0; i < loadOrderDataList.size(); i++) {
							ReducedLoadOrderData loadOrderData = loadOrderDataList.get(i);
							wsLoadOrderDataArray[i] = new com.holcim.logon.logonldsinterface.common_in.ReducedLoadOrderData();
							setWsReducedLoadOrderData(wsLoadOrderDataArray[i], loadOrderData);
						}
						wsResponse.setLoadOrderData(wsLoadOrderDataArray);												
					}
				}
			} 
			catch (Throwable t) {
				setWsResponse(wsResponse, wsRequest.getHeader().getLdsRequestId(), t);
			}

			
			return result = wsResponse;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public CreateCustomerOrderBasedOnMasterDataDefaultResponse createCustomerOrderBasedOnMasterDataDefault(
			CreateCustomerOrderBasedOnMasterDataDefaultRequest wsRequest) {
		// [[
		CreateCustomerOrderBasedOnMasterDataDefaultResponse result = null;
		Object[][] params = {{"wsRequest", wsRequest}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createCustomerOrder", params);
		}
		// ]]
		Response response = null;
	try {

		CreateCustomerOrderBasedOnMasterDataDefaultResponse wsResponse = new
				CreateCustomerOrderBasedOnMasterDataDefaultResponse();
			try {
				com.holcim.logon.eventbeans.in.logon_lds_interface.CreateCustomerOrderBasedOnMasterDataDefaultRequest request =
						LDS_INTERFACE_OBJECT_FACTORY.createCreateCustomerOrderBasedOnMasterDataDefaultRequest();
				copyWsRequestHeader(request, wsRequest);
				if (wsRequest.getOrder() != null) {
					request.setOrder(new OrderData());
					copyWsOrderData(request.getOrder(), wsRequest.getOrder());
				}

				response = processLogonEventServiceBean(request, Constant.INTERFACE_TYPE_LOGON_LDS_INTERFACE_10, params);

				setWsResponse(wsResponse, response, wsRequest.getHeader().getLdsRequestId());
				if (response instanceof com.holcim.logon.eventbeans.in.logon_lds_interface.CreateCustomerOrderBasedOnMasterDataDefaultResponse) {
					com.holcim.logon.eventbeans.in.logon_lds_interface.CreateCustomerOrderBasedOnMasterDataDefaultResponse createCustomerOrderBasedOnMasterDataDefaultResponse = (com.holcim.logon.eventbeans.in.logon_lds_interface.CreateCustomerOrderBasedOnMasterDataDefaultResponse)response;
					if (createCustomerOrderBasedOnMasterDataDefaultResponse.getCustomerOrderNo() != null) {
						com.holcim.logon.logonldsinterface.services.CustomerOrderNo_type5 wsLogonCustomerOrderNo = new com.holcim.logon.logonldsinterface.services.CustomerOrderNo_type5();
						wsLogonCustomerOrderNo.setCustomerOrderNo_type4(createCustomerOrderBasedOnMasterDataDefaultResponse.getCustomerOrderNo());
						wsResponse.setCustomerOrderNo(wsLogonCustomerOrderNo);
					}

					List<CreatedOrderData> masterOrderDataList = createCustomerOrderBasedOnMasterDataDefaultResponse.getOrderData();
					if (masterOrderDataList != null) {
						com.holcim.logon.logonldsinterface.common_in.CreatedOrderData[] wsMasterOrderDataArray = new com.holcim.logon.logonldsinterface.common_in.CreatedOrderData[masterOrderDataList.size()];
						for (int i = 0; i < masterOrderDataList.size(); i++) {
							CreatedOrderData masterOrderData = masterOrderDataList.get(i);
							wsMasterOrderDataArray[i] = new com.holcim.logon.logonldsinterface.common_in.CreatedOrderData();
							setWsCreatedOrderData(wsMasterOrderDataArray[i], masterOrderData);
						}
						wsResponse.setOrderData(wsMasterOrderDataArray);
					}
				}
			}
			catch (Throwable t) {
				setWsResponse(wsResponse, wsRequest.getHeader().getLdsRequestId(), t);
			}


			return result = wsResponse;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public EndLoadingResponse endLoading(EndLoadingRequest wsRequest) {
		// [[
		EndLoadingResponse result = null;
		Object[][] params = {{"wsRequest", wsRequest}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "endLoading", params);
		}
		// ]]
		try {
			
			EndLoadingResponse wsResponse = new EndLoadingResponse();
			try {
				com.holcim.logon.eventbeans.in.logon_lds_interface.EndLoadingRequest request = LDS_INTERFACE_OBJECT_FACTORY.createEndLoadingRequest();
				copyWsRequestHeader(request, wsRequest);
				if (wsRequest.getVehicle() != null) {
					request.setVehicle(new VehicleData());
					copyWsVehicleData(request.getVehicle(), wsRequest.getVehicle());
				}
				if (wsRequest.getDriver() != null) {
					request.setDriver(new DriverData());
					copyWsDriverData(request.getDriver(), wsRequest.getDriver());
				}
				if (wsRequest.getAttorney() != null) {
					request.setAttorney(new AttorneyData());
					copyWsAttorneyData(request.getAttorney(), wsRequest.getAttorney());
				}
				if (wsRequest.getDispatcher() != null) {
					request.setDispatcher(new DispatcherData());
					copyWsDispatcherData(request.getDispatcher(), wsRequest.getDispatcher());
				}
				if (wsRequest.getEndLoadingPart() != null) {
					for (com.holcim.logon.logonldsinterface.common_in.EndLoadingPartData wsEndLoadingPartData : wsRequest.getEndLoadingPart()) {
						EndLoadingPartData endLoadingPartData = new EndLoadingPartData();
						getWsEndLoadingPartData(endLoadingPartData, wsEndLoadingPartData);
						request.getEndLoadingPart().add(endLoadingPartData);
					}
				}
				if (wsRequest.getReturnPallets() != null) {
					for (com.holcim.logon.logonldsinterface.services.ReturnPallets_type0 wsReturnPallet : wsRequest.getReturnPallets()) {
						ReturnPallets returnPallet = new ReturnPallets();
						if (wsReturnPallet.getLogonOrderNo() != null) {
							returnPallet.setLogonOrderNo(wsReturnPallet.getLogonOrderNo().getLogonOrderNo_type18());
						}
						if (wsReturnPallet.getMaterialNo() != null) {
							returnPallet.setMaterialNo(wsReturnPallet.getMaterialNo().getMaterialNo_type10());
						}
						if (wsReturnPallet.getQuantity() != null) {
							returnPallet.setQuantity(wsReturnPallet.getQuantity().getQuantity_type4());
						}
						if (wsReturnPallet.getQuantityUom() != null) {
							returnPallet.setQuantityUom(wsReturnPallet.getQuantityUom().getQuantityUom_type4());
						}
						request.getReturnPallets().add(returnPallet);
					}
				}
				if (wsRequest.getFirstWeighing() != null) {
					request.setFirstWeighing(new FirstWeighingData());
					getWsFirstWeighingData(request.getFirstWeighing(), wsRequest.getFirstWeighing());
				}
				if (wsRequest.getSecondWeighing() != null) {
					request.setSecondWeighing(new SecondWeighingData());
					getWsSecondWeighingData(request.getSecondWeighing(), wsRequest.getSecondWeighing());
				}
				if (wsRequest.getTareDeviationDriverACK() != null) {
					request.setTareDeviationDriverACK(wsRequest.getTareDeviationDriverACK().getTareDeviationDriverACK_type2());
				}
				if (wsRequest.getMaxGrossExceededDriverACK() != null) {
					request.setMaxGrossExceededDriverACK(wsRequest.getMaxGrossExceededDriverACK().getMaxGrossExceededDriverACK_type2());
				}
				if (wsRequest.getLdsCustomerOrderNo() != null) {
					request.setLdsCustomerOrderNo(wsRequest.getLdsCustomerOrderNo().getLdsCustomerOrderNo_type4());
				}
				if (wsRequest.getLdsDeliveryNoteNo() != null) {
					request.setLdsDeliveryNoteNo(wsRequest.getLdsDeliveryNoteNo().getLdsDeliveryNoteNo_type4());
				}
				if (wsRequest.getLdsEnterShippingpointTime() != null) {
					request.setLdsEnterShippingpointTime(wsRequest.getLdsEnterShippingpointTime().getTime());
				}
				if (wsRequest.getLdsLeaveShippingpointTime() != null) {
					request.setLdsLeaveShippingpointTime(wsRequest.getLdsLeaveShippingpointTime().getTime());
				}
				if (wsRequest.getDeliveryNotePrintingPoint() != null) {
					request.setDeliveryNotePrintingPoint(wsRequest.getDeliveryNotePrintingPoint().getDeliveryNotePrintingPoint_type0());
				}
				if (wsRequest.getSuppressDeliveryNotePrinting() != null) {
					request.setSuppressDeliveryNotePrinting(wsRequest.getSuppressDeliveryNotePrinting().getSuppressDeliveryNotePrinting_type2());
				}
				if (wsRequest.getNoteDelivery() != null) {
					request.setNoteDelivery(wsRequest.getNoteDelivery().getNoteDelivery_type4());
				}
				if (wsRequest.getNoteLDS() != null) {
					request.setNoteLDS(wsRequest.getNoteLDS().getNoteLDS_type2());
				}
				if (wsRequest.getCarrierLetter() != null) {
					request.setCarrierLetter(wsRequest.getCarrierLetter().getCarrierLetter_type0());
				}
				if (wsRequest.getSeals() != null) {
					request.setSeals(wsRequest.getSeals().getSeals_type0());
				}

				Response response = processLogonEventServiceBean(request, Constant.INTERFACE_TYPE_LOGON_LDS_INTERFACE_10, params);
				
				setWsResponse(wsResponse, response, wsRequest.getHeader().getLdsRequestId());
				if (response instanceof com.holcim.logon.eventbeans.in.logon_lds_interface.EndLoadingResponse) {
					com.holcim.logon.eventbeans.in.logon_lds_interface.EndLoadingResponse endLoadingResponse = (com.holcim.logon.eventbeans.in.logon_lds_interface.EndLoadingResponse)response;
					if (endLoadingResponse.getOrder() != null) {
						com.holcim.logon.logonldsinterface.common_in.OrderData[] data = new com.holcim.logon.logonldsinterface.common_in.OrderData[endLoadingResponse.getOrder().size()];
						for (int i=0; i<endLoadingResponse.getOrder().size(); i++) {
							OrderData orderData = endLoadingResponse.getOrder().get(i);
							com.holcim.logon.logonldsinterface.common_in.OrderData wsOrderData = new com.holcim.logon.logonldsinterface.common_in.OrderData();
							setWsOrderData(wsOrderData, orderData);
							data[i] = wsOrderData;
						}
						wsResponse.setOrder(data);
					}
				}
			}
			catch (Throwable t) {
				setWsResponse(wsResponse, wsRequest.getHeader().getLdsRequestId(), t);
			}
			
			return result = wsResponse;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public RegularizationResponse regularization(RegularizationRequest wsRequest) {
		// [[
		RegularizationResponse result = null;
		Object[][] params = {{"wsRequest", wsRequest}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "regularization", params);
		}
		// ]]
		try {

			RegularizationResponse wsResponse = new RegularizationResponse();
			try {
				com.holcim.logon.eventbeans.in.logon_lds_interface.RegularizationRequest request = LDS_INTERFACE_OBJECT_FACTORY.createRegularizationRequest();
				copyWsRequestHeader(request, wsRequest);
				if (wsRequest.getVehicle() != null) {
					request.setVehicle(new VehicleData());
					copyWsVehicleData(request.getVehicle(), wsRequest.getVehicle());
				}
				if (wsRequest.getDriver() != null) {
					request.setDriver(new DriverData());
					copyWsDriverData(request.getDriver(), wsRequest.getDriver());
				}
				if (wsRequest.getAttorney() != null) {
					request.setAttorney(new AttorneyData());
					copyWsAttorneyData(request.getAttorney(), wsRequest.getAttorney());
				}
				if (wsRequest.getDispatcher() != null) {
					request.setDispatcher(new DispatcherData());
					copyWsDispatcherData(request.getDispatcher(), wsRequest.getDispatcher());
				}
				if (wsRequest.getEndLoadingPart() != null) {
					for (com.holcim.logon.logonldsinterface.common_in.EndLoadingPartData wsEndLoadingPartData : wsRequest.getEndLoadingPart()) {
						EndLoadingPartData endLoadingPartData = new EndLoadingPartData();
						getWsEndLoadingPartData(endLoadingPartData, wsEndLoadingPartData);
						request.getEndLoadingPart().add(endLoadingPartData);
					}
				}
				if (wsRequest.getReturnPallets() != null) {
					for (com.holcim.logon.logonldsinterface.services.ReturnPallets_type1 wsReturnPallet :
							wsRequest.getReturnPallets()) {
						com.holcim.logon.eventbeans.in.logon_lds_interface.RegularizationRequest.ReturnPallets returnPallet =
								new com.holcim.logon.eventbeans.in.logon_lds_interface.RegularizationRequest.ReturnPallets();
						if (wsReturnPallet.getLogonOrderNo() != null) {
							returnPallet.setLogonOrderNo(wsReturnPallet.getLogonOrderNo().getLogonOrderNo_type20());
						}
						if (wsReturnPallet.getMaterialNo() != null) {
							returnPallet.setMaterialNo(wsReturnPallet.getMaterialNo().getMaterialNo_type12());
						}
						if (wsReturnPallet.getQuantity() != null) {
							returnPallet.setQuantity(wsReturnPallet.getQuantity().getQuantity_type6());
						}
						if (wsReturnPallet.getQuantityUom() != null) {
							returnPallet.setQuantityUom(wsReturnPallet.getQuantityUom().getQuantityUom_type6());
						}
						request.getReturnPallets().add(returnPallet);
					}
				}
				if (wsRequest.getFirstWeighing() != null) {
					request.setFirstWeighing(new FirstWeighingData());
					getWsFirstWeighingData(request.getFirstWeighing(), wsRequest.getFirstWeighing());
				}
				if (wsRequest.getSecondWeighing() != null) {
					request.setSecondWeighing(new SecondWeighingData());
					getWsSecondWeighingData(request.getSecondWeighing(), wsRequest.getSecondWeighing());
				}
				if (wsRequest.getTareDeviationDriverACK() != null) {
					request.setTareDeviationDriverACK(wsRequest.getTareDeviationDriverACK().getTareDeviationDriverACK_type4());
				}
				if (wsRequest.getMaxGrossExceededDriverACK() != null) {
					request.setMaxGrossExceededDriverACK(wsRequest.getMaxGrossExceededDriverACK().getMaxGrossExceededDriverACK_type4());
				}
				if (wsRequest.getLdsCustomerOrderNo() != null) {
					request.setLdsCustomerOrderNo(wsRequest.getLdsCustomerOrderNo().getLdsCustomerOrderNo_type6());
				}
				if (wsRequest.getLdsDeliveryNoteNo() != null) {
					request.setLdsDeliveryNoteNo(wsRequest.getLdsDeliveryNoteNo().getLdsDeliveryNoteNo_type6());
				}
				if (wsRequest.getLdsEnterShippingpointTime() != null) {
					request.setLdsEnterShippingpointTime(wsRequest.getLdsEnterShippingpointTime().getTime());
				}
				if (wsRequest.getLdsLeaveShippingpointTime() != null) {
					request.setLdsLeaveShippingpointTime(wsRequest.getLdsLeaveShippingpointTime().getTime());
				}
				if (wsRequest.getDeliveryNotePrintingPoint() != null) {
					request.setDeliveryNotePrintingPoint(wsRequest.getDeliveryNotePrintingPoint().getDeliveryNotePrintingPoint_type2());
				}
				if (wsRequest.getSuppressDeliveryNotePrinting() != null) {
					request.setSuppressDeliveryNotePrinting(wsRequest.getSuppressDeliveryNotePrinting().getSuppressDeliveryNotePrinting_type4());
				}
				if (wsRequest.getNoteDelivery() != null) {
					request.setNoteDelivery(wsRequest.getNoteDelivery().getNoteDelivery_type6());
				}
				if (wsRequest.getNoteLDS() != null) {
					request.setNoteLDS(wsRequest.getNoteLDS().getNoteLDS_type4());
				}
				if (wsRequest.getCarrierLetter() != null) {
					request.setCarrierLetter(wsRequest.getCarrierLetter().getCarrierLetter_type2());
				}
				if (wsRequest.getSeals() != null) {
					request.setSeals(wsRequest.getSeals().getSeals_type2());
				}

				Response response = processLogonEventServiceBean(request, Constant.INTERFACE_TYPE_LOGON_LDS_INTERFACE_10, params);

				setWsResponse(wsResponse, response, wsRequest.getHeader().getLdsRequestId());
				if (response instanceof com.holcim.logon.eventbeans.in.logon_lds_interface.RegularizationResponse) {
					com.holcim.logon.eventbeans.in.logon_lds_interface.RegularizationResponse regularizationResponse = (com.holcim.logon.eventbeans.in.logon_lds_interface.RegularizationResponse)response;
					if (regularizationResponse.getOrder() != null) {
						com.holcim.logon.logonldsinterface.common_in.OrderData[] data = new com.holcim.logon.logonldsinterface.common_in.OrderData[regularizationResponse.getOrder().size()];
						for (int i=0; i<regularizationResponse.getOrder().size(); i++) {
							OrderData orderData = regularizationResponse.getOrder().get(i);
							com.holcim.logon.logonldsinterface.common_in.OrderData wsOrderData = new com.holcim.logon.logonldsinterface.common_in.OrderData();
							setWsOrderData(wsOrderData, orderData);
							data[i] = wsOrderData;
						}
						wsResponse.setOrder(data);
					}
				}
			}
			catch (Throwable t) {
				setWsResponse(wsResponse, wsRequest.getHeader().getLdsRequestId(), t);
			}

			return result = wsResponse;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public SetLoadOrder2LoadedResponse setLoadOrder2Loaded(SetLoadOrder2LoadedRequest wsRequest) {
		// [[
		SetLoadOrder2LoadedResponse result = null;
		Object[][] params = {{"wsRequest", wsRequest}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setLoadOrder2Loaded", params);
		}
		// ]]
		try {
			SetLoadOrder2LoadedResponse wsResponse = new SetLoadOrder2LoadedResponse();
			try {
				com.holcim.logon.eventbeans.in.logon_lds_interface.SetLoadOrder2LoadedRequest request = LDS_INTERFACE_OBJECT_FACTORY.createSetLoadOrder2LoadedRequest();
				copyWsRequestHeader(request, wsRequest);
				if (wsRequest.getVehicle() != null) {
					request.setVehicle(new VehicleData());
					copyWsVehicleData(request.getVehicle(), wsRequest.getVehicle());
				}
				if (wsRequest.getLoadOrderId() != null) {
					request.setLoadOrderId(wsRequest.getLoadOrderId().getLoadOrderId_type6());
				}
				if (wsRequest.getFirstWeighing() != null) {
					request.setFirstWeighing(new FirstWeighingData());
					getWsFirstWeighingData(request.getFirstWeighing(), wsRequest.getFirstWeighing());
				}
				if (wsRequest.getSecondWeighing() != null) {
					request.setSecondWeighing(new SecondWeighingData());
					getWsSecondWeighingData(request.getSecondWeighing(), wsRequest.getSecondWeighing());
				}
				if (wsRequest.getTareDeviationDriverACK() != null) {
					request.setTareDeviationDriverACK(wsRequest.getTareDeviationDriverACK().getTareDeviationDriverACK_type0());
				}
				if (wsRequest.getMaxGrossExceededDriverACK() != null) {
					request.setMaxGrossExceededDriverACK(wsRequest.getMaxGrossExceededDriverACK().getMaxGrossExceededDriverACK_type0());
				}
				if (wsRequest.getLdsCustomerOrderNo() != null) {
					request.setLdsCustomerOrderNo(wsRequest.getLdsCustomerOrderNo().getLdsCustomerOrderNo_type2());
				}
				if (wsRequest.getLdsDeliveryNoteNo() != null) {
					request.setLdsDeliveryNoteNo(wsRequest.getLdsDeliveryNoteNo().getLdsDeliveryNoteNo_type0());
				}
				if (wsRequest.getLdsEnterShippingpointTime() != null) {
					request.setLdsEnterShippingpointTime(wsRequest.getLdsEnterShippingpointTime().getTime());
				}
				if (wsRequest.getLdsLeaveShippingpointTime() != null) {
					request.setLdsLeaveShippingpointTime(wsRequest.getLdsLeaveShippingpointTime().getTime());
				}
				if (wsRequest.getSuppressDeliveryNotePrinting() != null) {
					request.setSuppressDeliveryNotePrinting(wsRequest.getSuppressDeliveryNotePrinting().getSuppressDeliveryNotePrinting_type0());
				}
				if (wsRequest.getNoteDelivery() != null) {
					request.setNoteDelivery(wsRequest.getNoteDelivery().getNoteDelivery_type2());
				}
				if (wsRequest.getNoteLDS() != null) {
					request.setNoteLDS(wsRequest.getNoteLDS().getNoteLDS_type0());
				}

				Response response = processLogonEventServiceBean(request, Constant.INTERFACE_TYPE_LOGON_LDS_INTERFACE_10, params);
				
				setWsResponse(wsResponse, response, wsRequest.getHeader().getLdsRequestId());
				if (response instanceof com.holcim.logon.eventbeans.in.logon_lds_interface.SetLoadOrder2LoadedResponse) {
					com.holcim.logon.eventbeans.in.logon_lds_interface.SetLoadOrder2LoadedResponse setLoadOrder2LoadedResponse = (com.holcim.logon.eventbeans.in.logon_lds_interface.SetLoadOrder2LoadedResponse)response;
					if (setLoadOrder2LoadedResponse.getOrder() != null) {
						com.holcim.logon.logonldsinterface.common_in.OrderData[] data = new com.holcim.logon.logonldsinterface.common_in.OrderData[setLoadOrder2LoadedResponse.getOrder().size()];
						for (int i=0; i<setLoadOrder2LoadedResponse.getOrder().size(); i++) {
							OrderData orderData = setLoadOrder2LoadedResponse.getOrder().get(i);
							com.holcim.logon.logonldsinterface.common_in.OrderData wsOrderData = new com.holcim.logon.logonldsinterface.common_in.OrderData();
							setWsOrderData(wsOrderData, orderData);
							data[i] = wsOrderData;
						}
						wsResponse.setOrder(data);
					}
				}
			}
			catch (Throwable t) {
				setWsResponse(wsResponse, wsRequest.getHeader().getLdsRequestId(), t);
			}
			
			return result = wsResponse;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public AddServiceMaterialsToOrderResponse addServiceMaterialsToOrder(
			AddServiceMaterialsToOrderRequest wsRequest) {
		// [[
		AddServiceMaterialsToOrderResponse result = null;
		Object[][] params = {{"wsRequest", wsRequest}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "addServiceMaterialsToOrder", params);
		}
		// ]]
		try {
			
			AddServiceMaterialsToOrderResponse wsResponse = new AddServiceMaterialsToOrderResponse();
			try {
				com.holcim.logon.eventbeans.in.logon_lds_interface.AddServiceMaterialsToOrderRequest request = LDS_INTERFACE_OBJECT_FACTORY.createAddServiceMaterialsToOrderRequest();
				copyWsRequestHeader(request, wsRequest);
				if (wsRequest.getLogonOrderNo() != null) {
					request.setLogonOrderNo(wsRequest.getLogonOrderNo().getLogonOrderNo_type8());
				}
				if (wsRequest.getServiceMaterial() != null) {
					for (com.holcim.logon.logonldsinterface.common_in.ServiceMaterialData wsServiceMaterialData : wsRequest.getServiceMaterial()) {
						ServiceMaterialData serviceMaterialData = new ServiceMaterialData();
						getWsServiceMaterialData(serviceMaterialData, wsServiceMaterialData);
						request.getServiceMaterial().add(serviceMaterialData);
					}
				}
				
				Response response = processLogonEventServiceBean(request, Constant.INTERFACE_TYPE_LOGON_LDS_INTERFACE_10, params);				
				
				setWsResponse(wsResponse, response, wsRequest.getHeader().getLdsRequestId());
				if (response instanceof com.holcim.logon.eventbeans.in.logon_lds_interface.AddServiceMaterialsToOrderResponse) {
					com.holcim.logon.eventbeans.in.logon_lds_interface.AddServiceMaterialsToOrderResponse addServiceMaterialsToOrderResponse = (com.holcim.logon.eventbeans.in.logon_lds_interface.AddServiceMaterialsToOrderResponse)response;
					if (addServiceMaterialsToOrderResponse.getOrder() != null) {
						com.holcim.logon.logonldsinterface.common_in.OrderData wsOrderData = new com.holcim.logon.logonldsinterface.common_in.OrderData();
						setWsOrderData(wsOrderData, addServiceMaterialsToOrderResponse.getOrder());
						wsResponse.setOrder(wsOrderData);
					}
				}
			}
			catch (Throwable t) {
				setWsResponse(wsResponse, wsRequest.getHeader().getLdsRequestId(), t);
			}
			
			return result = wsResponse;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public GetLoadOrdersForVehicleResponse getLoadOrdersForVehicle(
			GetLoadOrdersForVehicleRequest wsRequest) {
		// [[
		GetLoadOrdersForVehicleResponse result = null;
		Object[][] params = {{"wsRequest", wsRequest}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getLoadOrdersForVehicle", params);
		}
		// ]]
		try {
			
			GetLoadOrdersForVehicleResponse wsResponse = new GetLoadOrdersForVehicleResponse();
			try {
				com.holcim.logon.eventbeans.in.logon_lds_interface.GetLoadOrdersForVehicleRequest request = LDS_INTERFACE_OBJECT_FACTORY.createGetLoadOrdersForVehicleRequest();
				copyWsRequestHeader(request, wsRequest);
				if (wsRequest.getVehicle() != null) {
					request.setVehicle(new VehicleData());
					copyWsVehicleData(request.getVehicle(), wsRequest.getVehicle());
				}
				if (wsRequest.getTareWeight() != null) {
					request.setTareWeight(wsRequest.getTareWeight().getTareWeight_type0());
				}
				if (wsRequest.getTareWeightUom() != null) {
					request.setTareWeightUom(wsRequest.getTareWeightUom().getTareWeightUom_type0());
				}
				if (wsRequest.getLocation() != null) {
					request.setLocation(new LocationData());
					getWsLocationData(request.getLocation(), wsRequest.getLocation());
				}
				if (wsRequest.getMaterialRestriction() != null) {
					for (com.holcim.logon.logonldsinterface.services.MaterialRestriction_type3 wsMaterialRestriction : wsRequest.getMaterialRestriction()) {
						request.getMaterialRestriction().add(wsMaterialRestriction.getMaterialRestriction_type2());
					}
				}
				
				Response response = processLogonEventServiceBean(request, Constant.INTERFACE_TYPE_LOGON_LDS_INTERFACE_10, params);
				
				setWsResponse(wsResponse, response, wsRequest.getHeader().getLdsRequestId());
				if (response instanceof com.holcim.logon.eventbeans.in.logon_lds_interface.GetLoadOrdersForVehicleResponse) {
					com.holcim.logon.eventbeans.in.logon_lds_interface.GetLoadOrdersForVehicleResponse getLoadOrdersForVehicleResponse = (com.holcim.logon.eventbeans.in.logon_lds_interface.GetLoadOrdersForVehicleResponse)response;
					if (getLoadOrdersForVehicleResponse.isTareCheckResult() != null) {
						com.holcim.logon.logonldsinterface.services.TareCheckResult_type1 wsTareCheckResult = new com.holcim.logon.logonldsinterface.services.TareCheckResult_type1();
						wsTareCheckResult.setTareCheckResult_type0(getLoadOrdersForVehicleResponse.isTareCheckResult());
						wsResponse.setTareCheckResult(wsTareCheckResult);
					}
					if (getLoadOrdersForVehicleResponse.getLoadOrder() != null) {
						com.holcim.logon.logonldsinterface.common_in.LoadOrderData[] data = new com.holcim.logon.logonldsinterface.common_in.LoadOrderData[getLoadOrdersForVehicleResponse.getLoadOrder().size()];
						for (int i=0; i<getLoadOrdersForVehicleResponse.getLoadOrder().size(); i++) {
							LoadOrderData loadOrderData = getLoadOrdersForVehicleResponse.getLoadOrder().get(i);
							com.holcim.logon.logonldsinterface.common_in.LoadOrderData wsLoadOrderData = new com.holcim.logon.logonldsinterface.common_in.LoadOrderData();
							setWsLoadOrderData(wsLoadOrderData, loadOrderData);
							data[i] = wsLoadOrderData;
						}
						wsResponse.setLoadOrder(data);
					}
				}
			}
			catch (Throwable t) {
				setWsResponse(wsResponse, wsRequest.getHeader().getLdsRequestId(), t);
			}
			
			return result = wsResponse;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public UpdateSchedulingResponse updateScheduling(UpdateSchedulingRequest wsRequest) {
		// [[
		UpdateSchedulingResponse result = null;
		Object[][] params = {{"wsRequest", wsRequest}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateScheduling", params);
		}
		// ]]
		try {
			
			UpdateSchedulingResponse wsResponse = new UpdateSchedulingResponse();
			try {
				com.holcim.logon.eventbeans.in.logon_lds_interface.UpdateSchedulingRequest request = LDS_INTERFACE_OBJECT_FACTORY.createUpdateSchedulingRequest();
				copyWsRequestHeader(request, wsRequest);
				if (wsRequest.getLoadOrderId() != null) {
					request.setLoadOrderId(wsRequest.getLoadOrderId().getLoadOrderId_type12());
				}
				if (wsRequest.getVehicle() != null) {
					request.setVehicle(new VehicleData());
					copyWsVehicleData(request.getVehicle(), wsRequest.getVehicle());
				}
				
				Response response = processLogonEventServiceBean(request, Constant.INTERFACE_TYPE_LOGON_LDS_INTERFACE_10, params);
				
				setWsResponse(wsResponse, response, wsRequest.getHeader().getLdsRequestId());
				if (response instanceof com.holcim.logon.eventbeans.in.logon_lds_interface.UpdateSchedulingResponse) {
					com.holcim.logon.eventbeans.in.logon_lds_interface.UpdateSchedulingResponse updateSchedulingResponse = (com.holcim.logon.eventbeans.in.logon_lds_interface.UpdateSchedulingResponse)response;
					if (updateSchedulingResponse.getLoadOrder() != null) {
						com.holcim.logon.logonldsinterface.common_in.LoadOrderData wsLoadOrderData = new com.holcim.logon.logonldsinterface.common_in.LoadOrderData();
						setWsLoadOrderData(wsLoadOrderData, updateSchedulingResponse.getLoadOrder());
						wsResponse.setLoadOrder(wsLoadOrderData);
					}
				}
			}
			catch (Throwable t) {
				setWsResponse(wsResponse, wsRequest.getHeader().getLdsRequestId(), t);
			}
			
			return result = wsResponse;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public GetMaterialsForShiptoResponse getMaterialsForShipto(
			GetMaterialsForShiptoRequest wsRequest) {
		// [[
		GetMaterialsForShiptoResponse result = null;
		Object[][] params = {{"wsRequest", wsRequest}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getMaterialsForShipto", params);
		}
		// ]]
		try {
			
			GetMaterialsForShiptoResponse wsResponse = new GetMaterialsForShiptoResponse();
			try {
				com.holcim.logon.eventbeans.in.logon_lds_interface.GetMaterialsForShiptoRequest request = LDS_INTERFACE_OBJECT_FACTORY.createGetMaterialsForShiptoRequest();
				copyWsRequestHeader(request, wsRequest);
				if (wsRequest.getShipTo() != null) {
					request.setShipTo(wsRequest.getShipTo().getShipTo_type0());
				}
				if (wsRequest.getSoldTo() != null) {
					request.setSoldTo(wsRequest.getSoldTo().getSoldTo_type0());
				}
				if (wsRequest.getLocation() != null) {
					request.setLocation(new LocationData());
					getWsLocationData(request.getLocation(), wsRequest.getLocation());
				}
				if (wsRequest.getMaterialRestriction() != null) {
					for (com.holcim.logon.logonldsinterface.services.MaterialRestriction_type1 wsMaterialRestriction : wsRequest.getMaterialRestriction()) {
						request.getMaterialRestriction().add(wsMaterialRestriction.getMaterialRestriction_type0());
					}
				}
				
				Response response = processLogonEventServiceBean(request, Constant.INTERFACE_TYPE_LOGON_LDS_INTERFACE_10, params);
				
				setWsResponse(wsResponse, response, wsRequest.getHeader().getLdsRequestId());
				if (response instanceof com.holcim.logon.eventbeans.in.logon_lds_interface.GetMaterialsForShiptoResponse) {
					com.holcim.logon.eventbeans.in.logon_lds_interface.GetMaterialsForShiptoResponse getMaterialsForShiptoResponse = (com.holcim.logon.eventbeans.in.logon_lds_interface.GetMaterialsForShiptoResponse)response;
					if (getMaterialsForShiptoResponse.getMaterial() != null) {
						com.holcim.logon.logonldsinterface.common_in.ShiptoMaterialData[] data = new com.holcim.logon.logonldsinterface.common_in.ShiptoMaterialData[getMaterialsForShiptoResponse.getMaterial().size()];
						for (int i=0; i<getMaterialsForShiptoResponse.getMaterial().size(); i++) {
							ShiptoMaterialData shiptoMaterialData = getMaterialsForShiptoResponse.getMaterial().get(i);
							com.holcim.logon.logonldsinterface.common_in.ShiptoMaterialData wsShiptoMaterialData = new com.holcim.logon.logonldsinterface.common_in.ShiptoMaterialData();
							setWsShiptoMaterialData(wsShiptoMaterialData, shiptoMaterialData);
							data[i] = wsShiptoMaterialData;
						}
						wsResponse.setMaterial(data);
					}
				}
			}
			catch (Throwable t) {
				setWsResponse(wsResponse, wsRequest.getHeader().getLdsRequestId(), t);
			}
			
			return result = wsResponse;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public UpdatePlanningMaterialResponse updatePlanningMaterial(
			UpdatePlanningMaterialRequest wsRequest) {
		// [[
		UpdatePlanningMaterialResponse result = null;
		Object[][] params = {{"wsRequest", wsRequest}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updatePlanningMaterial", params);
		}
		// ]]
		try {
			
			UpdatePlanningMaterialResponse wsResponse = new UpdatePlanningMaterialResponse();
			try {
				com.holcim.logon.eventbeans.in.logon_lds_interface.UpdatePlanningMaterialRequest request = LDS_INTERFACE_OBJECT_FACTORY.createUpdatePlanningMaterialRequest();
				copyWsRequestHeader(request, wsRequest);
				if (wsRequest.getLogonOrderNo() != null) {
					request.setLogonOrderNo(wsRequest.getLogonOrderNo().getLogonOrderNo_type6());
				}
				if (wsRequest.getProdPlantNo() != null) {
					request.setProdPlantNo(wsRequest.getProdPlantNo().getProdPlantNo_type6());
				}
				if (wsRequest.getMaterialNo() != null) {
					request.setMaterialNo(wsRequest.getMaterialNo().getMaterialNo_type8());
				}
				if (wsRequest.getContractItemNumber() != null) {
					request.setContractItemNumber(wsRequest.getContractItemNumber().getContractItemNumber_type2());
				}
				if (wsRequest.getUom() != null) {
					request.setUom(wsRequest.getUom().getUom_type0());
				}
				if (wsRequest.getQty() != null) {
					request.setQty(wsRequest.getQty().getQty_type0());
				}
				
				Response response = processLogonEventServiceBean(request, Constant.INTERFACE_TYPE_LOGON_LDS_INTERFACE_10, params);				
				
				setWsResponse(wsResponse, response, wsRequest.getHeader().getLdsRequestId());
				if (response instanceof com.holcim.logon.eventbeans.in.logon_lds_interface.UpdatePlanningMaterialResponse) {
					com.holcim.logon.eventbeans.in.logon_lds_interface.UpdatePlanningMaterialResponse updatePlanningMaterialResponse = (com.holcim.logon.eventbeans.in.logon_lds_interface.UpdatePlanningMaterialResponse)response;
					if (updatePlanningMaterialResponse.getLoadOrder() != null) {
						com.holcim.logon.logonldsinterface.common_in.LoadOrderData wsLoadOrderData = new com.holcim.logon.logonldsinterface.common_in.LoadOrderData();
						setWsLoadOrderData(wsLoadOrderData, updatePlanningMaterialResponse.getLoadOrder());
						wsResponse.setLoadOrder(wsLoadOrderData);
					}
				}
			}
			catch (Throwable t) {
				setWsResponse(wsResponse, wsRequest.getHeader().getLdsRequestId(), t);
			}
			
			return result = wsResponse;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public CloseCustomerOrderResponse closeCustomerOrder(
			CloseCustomerOrderRequest wsRequest) {
		// [[
		CloseCustomerOrderResponse result = null;
		Object[][] params = {{"wsRequest", wsRequest}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "closeCustomerOrder", params);
		}
		// ]]
		try {
			
			CloseCustomerOrderResponse wsResponse = new CloseCustomerOrderResponse();
			try {
				com.holcim.logon.eventbeans.in.logon_lds_interface.CloseCustomerOrderRequest request = LDS_INTERFACE_OBJECT_FACTORY.createCloseCustomerOrderRequest();
				copyWsRequestHeader(request, wsRequest);
				if (wsRequest.getCustomerOrderNo() != null) {
					request.setCustomerOrderNo(wsRequest.getCustomerOrderNo().getCustomerOrderNo_type0());
				}
				
				Response response = processLogonEventServiceBean(request, Constant.INTERFACE_TYPE_LOGON_LDS_INTERFACE_10, params);
				
				setWsResponse(wsResponse, response, wsRequest.getHeader().getLdsRequestId());
			}
			catch (Throwable t) {
				setWsResponse(wsResponse, wsRequest.getHeader().getLdsRequestId(), t);
			}
			
			return result = wsResponse;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public GetPickupLoadOrdersForShiptoResponse getPickupLoadOrdersForShipto(
			GetPickupLoadOrdersForShiptoRequest wsRequest) {
		// [[
		GetPickupLoadOrdersForShiptoResponse result = null;
		Object[][] params = {{"wsRequest", wsRequest}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getPickupLoadOrdersForShipto", params);
		}
		// ]]
		try {
			
			GetPickupLoadOrdersForShiptoResponse wsResponse = new GetPickupLoadOrdersForShiptoResponse();
			try {
				com.holcim.logon.eventbeans.in.logon_lds_interface.GetPickupLoadOrdersForShiptoRequest request = LDS_INTERFACE_OBJECT_FACTORY.createGetPickupLoadOrdersForShiptoRequest();
				copyWsRequestHeader(request, wsRequest);
				if (wsRequest.getShipTo() != null) {
					request.setShipTo(wsRequest.getShipTo().getShipTo_type2());
				}
				if (wsRequest.getSoldTo() != null) {
					request.setSoldTo(wsRequest.getSoldTo().getSoldTo_type2());
				}
				if (wsRequest.getMaterialRestriction() != null) {
					for (com.holcim.logon.logonldsinterface.services.MaterialRestriction_type5 wsMaterialRestriction : wsRequest.getMaterialRestriction()) {
						request.getMaterialRestriction().add(wsMaterialRestriction.getMaterialRestriction_type4());
					}
				}
				
				Response response = processLogonEventServiceBean(request, Constant.INTERFACE_TYPE_LOGON_LDS_INTERFACE_10, params);
				
				setWsResponse(wsResponse, response, wsRequest.getHeader().getLdsRequestId());
				if (response instanceof com.holcim.logon.eventbeans.in.logon_lds_interface.GetPickupLoadOrdersForShiptoResponse) {
					com.holcim.logon.eventbeans.in.logon_lds_interface.GetPickupLoadOrdersForShiptoResponse getPickupLoadOrdersForShiptoResponse = (com.holcim.logon.eventbeans.in.logon_lds_interface.GetPickupLoadOrdersForShiptoResponse)response;
					if (getPickupLoadOrdersForShiptoResponse.getLoadOrder() != null) {
						com.holcim.logon.logonldsinterface.common_in.LoadOrderData[] data = new com.holcim.logon.logonldsinterface.common_in.LoadOrderData[getPickupLoadOrdersForShiptoResponse.getLoadOrder().size()];
						for (int i=0; i<getPickupLoadOrdersForShiptoResponse.getLoadOrder().size(); i++) {
							LoadOrderData loadOrderData = getPickupLoadOrdersForShiptoResponse.getLoadOrder().get(i);
							com.holcim.logon.logonldsinterface.common_in.LoadOrderData wsLoadOrderData = new com.holcim.logon.logonldsinterface.common_in.LoadOrderData();
							setWsLoadOrderData(wsLoadOrderData, loadOrderData);
							data[i] = wsLoadOrderData;
						}
						wsResponse.setLoadOrder(data);
					}
				}
			}
			catch (Throwable t) {
				setWsResponse(wsResponse, wsRequest.getHeader().getLdsRequestId(), t);
			}
			
			return result = wsResponse;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public StartLoadingResponse startLoading(StartLoadingRequest wsRequest) {
		// [[
		StartLoadingResponse result = null;
		Object[][] params = {{"wsRequest", wsRequest}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "startLoading", params);
		}
		// ]]
		try {
			
			StartLoadingResponse wsResponse = new StartLoadingResponse();
			try {
				com.holcim.logon.eventbeans.in.logon_lds_interface.StartLoadingRequest request = LDS_INTERFACE_OBJECT_FACTORY.createStartLoadingRequest();
				copyWsRequestHeader(request, wsRequest);
				if (wsRequest.getLoadOrderItemId() != null) {
					request.setLoadOrderItemId(wsRequest.getLoadOrderItemId().getLoadOrderItemId_type6());
				}
				if (wsRequest.getVehicle() != null) {
					request.setVehicle(new VehicleData());
					copyWsVehicleData(request.getVehicle(), wsRequest.getVehicle());
				}
				if (wsRequest.getMaterial() != null) {
					request.setMaterial(new LoadedMaterialData());
					getWsLoadedMaterialData(request.getMaterial(), wsRequest.getMaterial());
				}
				if (wsRequest.getFirstWeighing() != null) {
					request.setFirstWeighing(new FirstWeighingData());
					getWsFirstWeighingData(request.getFirstWeighing(), wsRequest.getFirstWeighing());
				}
				if (wsRequest.getLocation() != null) {
					request.setLocation(new LocationData());
					getWsLocationData(request.getLocation(), wsRequest.getLocation());
				}
				if (wsRequest.getTareDeviationDriverACK() != null) {
					request.setTareDeviationDriverACK(wsRequest.getTareDeviationDriverACK().getTareDeviationDriverACK_type6());
				}
				if (wsRequest.getTareWarnIgnore() != null) {
					request.setTareWarnIgnore(wsRequest.getTareWarnIgnore().getTareWarnIgnore_type0());
				}
				if (wsRequest.getNoteDelivery() != null) {
					request.setNoteDelivery(wsRequest.getNoteDelivery().getNoteDelivery_type8());
				}
				if (wsRequest.getNoteLDS() != null) {
					request.setNoteLDS(wsRequest.getNoteLDS().getNoteLDS_type6());
				}
				
				Response response = processLogonEventServiceBean(request, Constant.INTERFACE_TYPE_LOGON_LDS_INTERFACE_10, params);
				
				setWsResponse(wsResponse, response, wsRequest.getHeader().getLdsRequestId());
			}
			catch (Throwable t) {
				setWsResponse(wsResponse, wsRequest.getHeader().getLdsRequestId(), t);
			}
			
			return result = wsResponse;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public ValidateCustomerOrderResponse validateCustomerOrder(
			ValidateCustomerOrderRequest wsRequest) {
		// [[
		ValidateCustomerOrderResponse result = null;
		Object[][] params = {{"wsRequest", wsRequest}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validateCustomerOrder", params);
		}
		// ]]
		try {
			
			ValidateCustomerOrderResponse wsResponse = new ValidateCustomerOrderResponse();
			try {
				com.holcim.logon.eventbeans.in.logon_lds_interface.ValidateCustomerOrderRequest request = LDS_INTERFACE_OBJECT_FACTORY.createValidateCustomerOrderRequest();
				copyWsRequestHeader(request, wsRequest);
				if (wsRequest.getOrder() != null) {
					request.setOrder(new OrderData());
					copyWsOrderData(request.getOrder(), wsRequest.getOrder());
				}
				
				Response response = processLogonEventServiceBean(request, Constant.INTERFACE_TYPE_LOGON_LDS_INTERFACE_10, params);
				
				setWsResponse(wsResponse, response, wsRequest.getHeader().getLdsRequestId());
				if (response instanceof com.holcim.logon.eventbeans.in.logon_lds_interface.ValidateCustomerOrderResponse) {
					com.holcim.logon.eventbeans.in.logon_lds_interface.ValidateCustomerOrderResponse validateCustomerOrderResponse = (com.holcim.logon.eventbeans.in.logon_lds_interface.ValidateCustomerOrderResponse)response;
					if (validateCustomerOrderResponse.getOrder() != null) {
						com.holcim.logon.logonldsinterface.common_in.OrderData[] data = new com.holcim.logon.logonldsinterface.common_in.OrderData[validateCustomerOrderResponse.getOrder().size()];
						for (int i=0; i<validateCustomerOrderResponse.getOrder().size(); i++) {
							OrderData orderData = validateCustomerOrderResponse.getOrder().get(i);
							com.holcim.logon.logonldsinterface.common_in.OrderData wsOrderData = new com.holcim.logon.logonldsinterface.common_in.OrderData();
							setWsOrderData(wsOrderData, orderData);
							data[i] = wsOrderData;
						}
						wsResponse.setOrder(data);
					}
				}
			}
			catch (Throwable t) {
				setWsResponse(wsResponse, wsRequest.getHeader().getLdsRequestId(), t);
			}
			
			return result = wsResponse;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public CreateCompletedCustomerOrderResponse createCompletedCustomerOrder(
			CreateCompletedCustomerOrderRequest wsRequest) {
		// [[
		CreateCompletedCustomerOrderResponse result = null;
		Object[][] params = {{"wsRequest", wsRequest}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createCompletedCustomerOrder", params);
		}
		// ]]
		try {
			
			CreateCompletedCustomerOrderResponse wsResponse = new CreateCompletedCustomerOrderResponse();
			try {
				final LogonEventService eventService = ServiceLocatorProvider.getServiceLocator().getLogonEventService();
				final com.holcim.logon.eventbeans.in.logon_lds_interface.CreateCompletedCustomerOrderRequest request;
				
				request = internalCompletedCustomerOrderRequest(wsRequest);
				
				Response response = (Response) processLogonEventServiceBean(eventService, request, Constant.INTERFACE_TYPE_LOGON_LDS_INTERFACE_10, params);
					
				// Convert response back and set in response object.
				setWsResponse(wsResponse, response, wsRequest.getHeader().getLdsRequestId());
			}
			catch (Throwable t) {
				setWsResponse(wsResponse, wsRequest.getHeader().getLdsRequestId(), t);
			}
			
			return result = wsResponse;
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
	 * Converts an external createCompletedCustomerOrder request into an internal one.
	 * @param wsRequest The WS request received
	 * @return An internal representation of the request
	 */
	private com.holcim.logon.eventbeans.in.logon_lds_interface.CreateCompletedCustomerOrderRequest internalCompletedCustomerOrderRequest(
			CreateCompletedCustomerOrderRequest wsRequest) {
		final com.holcim.logon.eventbeans.in.logon_lds_interface.CreateCompletedCustomerOrderRequest request;
		request = LDS_INTERFACE_OBJECT_FACTORY.createCreateCompletedCustomerOrderRequest();
		
		copyWsRequestHeader(request, wsRequest);
		if (wsRequest.getOrder() != null) {
			request.setOrder(new OrderData());
			copyWsOrderData(request.getOrder(), wsRequest.getOrder());
		}
		if (wsRequest.getVehicle() != null) {
			request.setVehicle(new VehicleData());
			copyWsVehicleData(request.getVehicle(), wsRequest.getVehicle());
		}
		if (wsRequest.getCompletedLoadOrderItem() != null) {
			for (com.holcim.logon.logonldsinterface.common_in.CompletedLoadOrderItemData wsCompletedLoadOrderItemData : wsRequest.getCompletedLoadOrderItem()) {
				final CompletedLoadOrderItemData completedLoadOrderItemData;
				completedLoadOrderItemData = getWsCompletedLoadOrderItemData(wsCompletedLoadOrderItemData);
				request.getCompletedLoadOrderItem().add(completedLoadOrderItemData);
			}
		}
		return request;
	}

	public SelectLoadOrderForLoadingResponse selectLoadOrderForLoading(
			SelectLoadOrderForLoadingRequest wsRequest) {
		// [[
		SelectLoadOrderForLoadingResponse result = null;
		Object[][] params = {{"wsRequest", wsRequest}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "selectLoadOrderForLoading", params);
		}
		// ]]
		try {
			
			SelectLoadOrderForLoadingResponse wsResponse = new SelectLoadOrderForLoadingResponse();
			try {
				com.holcim.logon.eventbeans.in.logon_lds_interface.SelectLoadOrderForLoadingRequest request = LDS_INTERFACE_OBJECT_FACTORY.createSelectLoadOrderForLoadingRequest();
				copyWsRequestHeader(request, wsRequest);
				if (wsRequest.getLoadOrderId() != null) {
					request.setLoadOrderId(wsRequest.getLoadOrderId().getLoadOrderId_type2());
				}
				if (wsRequest.getOrderValidation() != null) {
					for (com.holcim.logon.logonldsinterface.services.OrderValidation_type0 wsReturnPallet : wsRequest.getOrderValidation()) {
						OrderValidation orderValidation = new OrderValidation();
						if (wsReturnPallet.getLogonOrderNo() != null) {
							orderValidation.setLogonOrderNo(wsReturnPallet.getLogonOrderNo().getLogonOrderNo_type2());
						}
						if (wsReturnPallet.getOrderValidationCode() != null) {
							orderValidation.setOrderValidationCode(wsReturnPallet.getOrderValidationCode().getOrderValidationCode_type2());
						}
						request.getOrderValidation().add(orderValidation);
					}
				}
				if (wsRequest.getLocation() != null) {
					request.setLocation(new LocationData());
					getWsLocationData(request.getLocation(), wsRequest.getLocation());
				}
				
				Response response = processLogonEventServiceBean(request, Constant.INTERFACE_TYPE_LOGON_LDS_INTERFACE_10, params);
				
				setWsResponse(wsResponse, response, wsRequest.getHeader().getLdsRequestId());
			}
			catch (Throwable t) {
				setWsResponse(wsResponse, wsRequest.getHeader().getLdsRequestId(), t);
			}
			
			return result = wsResponse;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	private Response processLogonEventServiceBean(LogonEventService logonEventService, LogonEventRequest request, long ifcType, Object[][] params) {
		Response response = null;
		try {
			response = (Response)logonEventService.processEventBean(request, ifcType);					
		}
		catch (LogonEventSimulationException e) {
			// this is an intended exception, do not log an error
			response = (Response) e.getLogonEventResponse();
		}
		catch (LogonEventException logonEventException) {
			ExceptionUtil.errException(LOG, logonEventException, params);
			String requestClassName = request.getClass().getSimpleName();
			try {
				RequestHandlerLocalInterface handler = LocalServices.getLocalInterfaceForRequestHandler(requestClassName);
				String resolvedText = handler.getResponseMessage(request, logonEventException.getLogonEventResponse().getCode(), logonEventException.getFormatParameters());
				logonEventException.getLogonEventResponse().setTxt(resolvedText);
			}
			catch (Exception exception) {
				LOG.error("Could not resolve language dependent message.", exception);
			}
			response = (Response)logonEventException.getLogonEventResponse();
		}			
		return response; 		
	}
	
	private Response processLogonEventServiceBean(LogonEventRequest request, long ifcType, Object[][] params) {
		return processLogonEventServiceBean(ServiceLocatorProvider.getServiceLocator().getLogonEventService(), request, ifcType, params);
	}

	@Override
	public ScheduleAssignmentResponse scheduleAssignment(
			ScheduleAssignmentRequest wsRequest) {
		Object[][] params = { { "wsRequest", wsRequest } };

		ScheduleAssignmentResponse wsResponse = new ScheduleAssignmentResponse();
		try {
			com.holcim.logon.eventbeans.in.logon_lds_interface.ScheduleAssignmentRequest request =
					LDS_INTERFACE_OBJECT_FACTORY.createScheduleAssignmentRequest();
			copyWsRequestHeader(request, wsRequest);
			if (wsRequest.getLogonOrderNo() != null) {
				request.setLogonOrderNo(wsRequest.getLogonOrderNo().getLogonOrderNo_type12());
			}
			if (wsRequest.getVehicle() != null) {
				request.setVehicle(new VehicleData());
				copyWsVehicleData(request.getVehicle(), wsRequest.getVehicle());
			}
			if (wsRequest.getPlannedLoading() != null) {
				request.setPlannedLoading(createOffsetDateTime(wsRequest.getPlannedLoading()));
			}

			Response response = processLogonEventServiceBean(request, Constant.INTERFACE_TYPE_LOGON_LDS_INTERFACE_10, params);

			setWsResponse(wsResponse, response, wsRequest.getHeader().getLdsRequestId());

		}
		catch (Throwable t) {
			setWsResponse(wsResponse, wsRequest.getHeader().getLdsRequestId(), t);
		}

		return wsResponse;
	}

	private void setWsOrderListItemData(OrderListItemData wsOrderListItemData,
			com.holcim.logon.eventbeans.in.logon_lds_interface.OrderListItemData orderListItemData) {
		if (orderListItemData.getLogonOrderItemNumber() != null) {
			LogonOrderItemNumber_type3 wsLogonOrderItemNumber = new LogonOrderItemNumber_type3();
			wsLogonOrderItemNumber.setLogonOrderItemNumber_type2(orderListItemData.getLogonOrderItemNumber());
			wsOrderListItemData.setLogonOrderItemNumber(wsLogonOrderItemNumber);
		}
		if (orderListItemData.getMaterialNo() != null) {
			MaterialNo_type7 wsMaterialNo = new MaterialNo_type7();
			wsMaterialNo.setMaterialNo_type6(orderListItemData.getMaterialNo());
			wsOrderListItemData.setMaterialNo(wsMaterialNo);
		}
		if (orderListItemData.getQuantity() != null) {
			Quantity_type3 wsQuantity = new Quantity_type3();
			wsQuantity.setQuantity_type2(orderListItemData.getQuantity());
			wsOrderListItemData.setQuantity(wsQuantity);
		}
		if (orderListItemData.getQuantityUom() != null) {
			QuantityUom_type3 wsQuantityUom = new QuantityUom_type3();
			wsQuantityUom.setQuantityUom_type2(orderListItemData.getQuantityUom());
			wsOrderListItemData.setQuantityUom(wsQuantityUom);
		}
	}

	private void setWsOrderListData(GetOrdersForCarriersRequest wsRequest, OrderListData wsOrderListData,
			com.holcim.logon.eventbeans.in.logon_lds_interface.OrderListData orderListData) {
		if (orderListData.getLogonOrderNo() != null) {
			LogonOrderNo_type5 wsLogonOrderNo = new LogonOrderNo_type5();
			wsLogonOrderNo.setLogonOrderNo_type4(orderListData.getLogonOrderNo());
			wsOrderListData.setLogonOrderNo(wsLogonOrderNo);
		}
		if (orderListData.getSorgCd() != null) {
			SorgCd_type1 wsSorgCd = new SorgCd_type1();
			wsSorgCd.setSorgCd_type0(orderListData.getSorgCd());
			wsOrderListData.setSorgCd(wsSorgCd);
		}
		if (orderListData.getCommercialCarrier() != null) {
			com.holcim.logon.logonldsinterface.common_in.VendorData wsCommCarrier =
					new com.holcim.logon.logonldsinterface.common_in.VendorData();
			setWsVendorData(wsCommCarrier, orderListData.getCommercialCarrier());
			wsOrderListData.setCommercialCarrier(wsCommCarrier);
		}
		if (orderListData.getExecutingCarrier() != null) {
			com.holcim.logon.logonldsinterface.common_in.VendorData wsExecCarrier =
					new com.holcim.logon.logonldsinterface.common_in.VendorData();
			setWsVendorData(wsExecCarrier, orderListData.getExecutingCarrier());
			wsOrderListData.setExecutingCarrier(wsExecCarrier);
		}
		if (orderListData.getSoldTo() != null) {
			com.holcim.logon.logonldsinterface.common_in.BusinessPartnerData wsSoldTo =
					new com.holcim.logon.logonldsinterface.common_in.BusinessPartnerData();
			setWsBusinessPartnerData(wsSoldTo, orderListData.getSoldTo());
			wsOrderListData.setSoldTo(wsSoldTo);
		}
		if (orderListData.getShipTo() != null) {
			com.holcim.logon.logonldsinterface.common_in.BusinessPartnerData wsShipTo =
					new com.holcim.logon.logonldsinterface.common_in.BusinessPartnerData();
			setWsBusinessPartnerData(wsShipTo, orderListData.getShipTo());
			wsOrderListData.setShipTo(wsShipTo);
		}
		if (orderListData.getSupplyPlantNo() != null) {
			SupplyPlantNo_type5 supplyPlantNo = new SupplyPlantNo_type5();
			supplyPlantNo.setSupplyPlantNo_type4(orderListData.getSupplyPlantNo());
			wsOrderListData.setSupplyPlantNo(supplyPlantNo);
		}
		if (orderListData.getShippingpointNo() != null) {
			ShippingpointNo_type5 shippingPointNo = new ShippingpointNo_type5();
			shippingPointNo.setShippingpointNo_type4(orderListData.getSupplyPlantNo());
			wsOrderListData.setShippingpointNo(shippingPointNo);
		}
		if (orderListData.getDeliveryType() != null) {
			DeliveryType_type3 wsDeliveryType = new DeliveryType_type3();
			wsDeliveryType.setDeliveryType_type2(orderListData.getDeliveryType());
			wsOrderListData.setDeliveryType(wsDeliveryType);
		}
		if (orderListData.getEarliestDeliveryTime() != null) {
			wsOrderListData.setEarliestDeliveryTime(
					createCalendar(orderListData.getEarliestDeliveryTime(), wsRequest.getHeader().getSendersTime()));
		}
		if (orderListData.getLatestDeliveryTime() != null) {
			wsOrderListData.setLatestDeliveryTime(
					createCalendar(orderListData.getLatestDeliveryTime(), wsRequest.getHeader().getSendersTime()));
		}
		if (orderListData.getPlannedDeliveryTime() != null) {
			wsOrderListData.setPlannedDeliveryTime(
					createCalendar(orderListData.getPlannedDeliveryTime(), wsRequest.getHeader().getSendersTime()));
		}
		if (orderListData.getOrderValidationCode() != null) {
			OrderValidationCode_type5 wsOrderValidationCode = new OrderValidationCode_type5();
			wsOrderValidationCode.setOrderValidationCode_type4(orderListData.getOrderValidationCode());
			wsOrderListData.setOrderValidationCode(wsOrderValidationCode);
		}
		wsOrderListData.setOrderListItem(orderListData.getOrderListItem().stream()
				.map(it -> {
					OrderListItemData result = new OrderListItemData();
					setWsOrderListItemData(result, it);
					return result;
				})
				.toArray(OrderListItemData[]::new));
	}

	@Override
	public GetOrdersForCarriersResponse getOrdersForCarriers(
			GetOrdersForCarriersRequest wsRequest) {
		Object[][] params = { { "wsRequest", wsRequest } };

		GetOrdersForCarriersResponse wsResponse = new GetOrdersForCarriersResponse();
		try {
			com.holcim.logon.eventbeans.in.logon_lds_interface.GetOrdersForCarriersRequest request =
					LDS_INTERFACE_OBJECT_FACTORY.createGetOrdersForCarriersRequest();
			copyWsRequestHeader(request, wsRequest);
			if (wsRequest.getSelectedCarriers() != null) {
				request.getSelectedCarriers().addAll(Arrays.stream(wsRequest.getSelectedCarriers())
						.map(it -> {
							VendorData result = new VendorData();
							getWsVendorData(result, it);
							return result;
						}).collect(Collectors.toList()));
			}
			if (wsRequest.getLastChangeFrom() != null) {
				request.setLastChangeFrom(createOffsetDateTime(wsRequest.getLastChangeFrom()));
			}
			if (wsRequest.getLastChangeTo() != null) {
				request.setLastChangeTo(createOffsetDateTime(wsRequest.getLastChangeTo()));
			}
			if (wsRequest.getPage() != null) {
				request.setPage(wsRequest.getPage().getPage_type0());
			}
			if (wsRequest.getEncodedOrderNumbers() != null) {
				request.setEncodedOrderNumbers(wsRequest.getEncodedOrderNumbers().getEncodedOrderNumbers_type0());
			}

			Response response = processLogonEventServiceBean(request, Constant.INTERFACE_TYPE_LOGON_LDS_INTERFACE_10, params);

			setWsResponse(wsResponse, response, wsRequest.getHeader().getLdsRequestId());
			if (response instanceof com.holcim.logon.eventbeans.in.logon_lds_interface.GetOrdersForCarriersResponse) {
				com.holcim.logon.eventbeans.in.logon_lds_interface.GetOrdersForCarriersResponse getOrdersForCarriersResponse =
						(com.holcim.logon.eventbeans.in.logon_lds_interface.GetOrdersForCarriersResponse) response;
				wsResponse.setOrder(getOrdersForCarriersResponse.getOrder().stream().map(it -> {
					OrderListData result = new OrderListData();
					setWsOrderListData(wsRequest, result, it);
					return result;
				}).toArray(OrderListData[]::new));
				if (getOrdersForCarriersResponse.getTotalNumberOfPages() != null) {
					TotalNumberOfPages_type1 wsTotalNumberOfPages = new TotalNumberOfPages_type1();
					wsTotalNumberOfPages.setTotalNumberOfPages_type0(getOrdersForCarriersResponse.getTotalNumberOfPages());
					wsResponse.setTotalNumberOfPages(wsTotalNumberOfPages);
				}
				if (getOrdersForCarriersResponse.getEncodedOrderNumbers() != null) {
					EncodedOrderNumbers_type3 wsEncodedOrderNumbers = new EncodedOrderNumbers_type3();
					wsEncodedOrderNumbers.setEncodedOrderNumbers_type2(getOrdersForCarriersResponse.getEncodedOrderNumbers());
					wsResponse.setEncodedOrderNumbers(wsEncodedOrderNumbers);
				}
			}

		}
		catch (Throwable t) {
			setWsResponse(wsResponse, wsRequest.getHeader().getLdsRequestId(), t);
		}
		return wsResponse;
	}

	@Override
	public ReleaseAssignmentResponse releaseAssignment(
			ReleaseAssignmentRequest wsRequest) {
		Object[][] params = { { "wsRequest", wsRequest } };

		ReleaseAssignmentResponse wsResponse = new ReleaseAssignmentResponse();
		try {
			com.holcim.logon.eventbeans.in.logon_lds_interface.ReleaseAssignmentRequest request =
					LDS_INTERFACE_OBJECT_FACTORY.createReleaseAssignmentRequest();
			copyWsRequestHeader(request, wsRequest);

			if (wsRequest.getLogonOrderNo() != null) {
				request.setLogonOrderNo(wsRequest.getLogonOrderNo().getLogonOrderNo_type10());
			}

			Response response = processLogonEventServiceBean(request, Constant.INTERFACE_TYPE_LOGON_LDS_INTERFACE_10, params);

			setWsResponse(wsResponse, response, wsRequest.getHeader().getLdsRequestId());

		}
		catch (Throwable t) {
			setWsResponse(wsResponse, wsRequest.getHeader().getLdsRequestId(), t);
		}
		return wsResponse;
	}

	public AddReturnOrderResponse addReturnOrder(
			AddReturnOrderRequest wsRequest) {
		// [[
		AddReturnOrderResponse result = null;
		Object[][] params = {{"wsRequest", wsRequest}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "addServiceMaterialsToOrder", params);
		}
		// ]]
		try {

			AddReturnOrderResponse wsResponse = new AddReturnOrderResponse();
			try {
				com.holcim.logon.eventbeans.in.logon_lds_interface.AddReturnOrderRequest request = LDS_INTERFACE_OBJECT_FACTORY.createAddReturnOrderRequest();
				copyWsRequestHeader(request, wsRequest);
				if (wsRequest.getLogonOrderNo() != null) {
					request.setLogonOrderNo(wsRequest.getLogonOrderNo().getLogonOrderNo_type24());
				}
				OrderItemReturnData[] itemReturnDataArray = wsRequest.getOrderItemReturnData();
				if (itemReturnDataArray != null ) {
					for (OrderItemReturnData itemReturnData : Arrays.asList(itemReturnDataArray)) {
						com.holcim.logon.eventbeans.in.logon_lds_interface.OrderItemReturnData orderItemReturnData =
								LDS_INTERFACE_OBJECT_FACTORY.createOrderItemReturnData();

						if (itemReturnData.getSapMaterial() != null) {
							orderItemReturnData.setSapMaterial(itemReturnData.getSapMaterial().getSapMaterial_type0());
						}
						if (itemReturnData.getDateOfReturn() != null) {
							orderItemReturnData.setDateOfReturn(itemReturnData.getDateOfReturn().getTime());
						}
						if (itemReturnData.getDeliveredQuantity() != null) {
							orderItemReturnData.setDeliveredQuantity(itemReturnData.getDeliveredQuantity());
						}
						if (itemReturnData.getReturnedQuantity() != null) {
							orderItemReturnData.setReturnedQuantity(itemReturnData.getReturnedQuantity());
						}
						if (itemReturnData.getReturnedUom() != null) {
							orderItemReturnData.setReturnedUom(itemReturnData.getReturnedUom().getReturnedUom_type0());
						}
						if (itemReturnData.getReturnOrderReason() != null) {
							orderItemReturnData
									.setReturnOrderReason(itemReturnData.getReturnOrderReason().getReturnOrderReason_type0());
						}
						if (itemReturnData.getReturnMaterialUsageCd() != null) {
							orderItemReturnData.setReturnMaterialUsageCd(itemReturnData.getReturnMaterialUsageCd());
						}
						request.getOrderItemReturnData().add(orderItemReturnData);
					}
				}


				Response response = processLogonEventServiceBean(request, Constant.INTERFACE_TYPE_LOGON_LDS_INTERFACE_10, params);

				setWsResponse(wsResponse, response, wsRequest.getHeader().getLdsRequestId());
				if (response instanceof com.holcim.logon.eventbeans.in.logon_lds_interface.AddReturnOrderResponse) {
					com.holcim.logon.eventbeans.in.logon_lds_interface.AddReturnOrderResponse addReturnOrderResponse
							= (com.holcim.logon.eventbeans.in.logon_lds_interface.AddReturnOrderResponse)response;
					if (addReturnOrderResponse.getOrder() != null) {
						com.holcim.logon.logonldsinterface.common_in.OrderData wsOrderData = new com.holcim.logon.logonldsinterface.common_in.OrderData();
						setWsOrderData(wsOrderData, addReturnOrderResponse.getOrder());
						wsResponse.setOrder(wsOrderData);
					}
				}
			}
			catch (Throwable t) {
				setWsResponse(wsResponse, wsRequest.getHeader().getLdsRequestId(), t);
			}

			return result = wsResponse;
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
