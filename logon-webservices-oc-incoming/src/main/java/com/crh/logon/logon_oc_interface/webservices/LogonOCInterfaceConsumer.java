/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.logon_oc_interface.webservices;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.localinterfaces.ConfigServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.LocalServices;
import com.crh.logon.business.services.utils.LogonEventContextBean;
import com.crh.logon.constants.Constant;
import com.crh.logon.logonocinterface.common_in.Header;
import com.crh.logon.logonocinterface.common_in.OcRequestId_type1;
import com.crh.logon.logonocinterface.services.AvailableQuantityInTons_type1;
import com.crh.logon.logonocinterface.services.AvailableQuantity_type1;
import com.crh.logon.logonocinterface.services.ContractNumber_type5;
import com.crh.logon.logonocinterface.services.ContractQuantityCalculation_type0;
import com.crh.logon.logonocinterface.services.ContractType_type1;
import com.crh.logon.logonocinterface.services.CreatePickupCustomerOrderRequest;
import com.crh.logon.logonocinterface.services.CreatePickupCustomerOrderResponse;
import com.crh.logon.logonocinterface.services.CreateRoadDirectCustomerOrderRequest;
import com.crh.logon.logonocinterface.services.CreateRoadDirectCustomerOrderResponse;
import com.crh.logon.logonocinterface.services.DeliveryType_type1;
import com.crh.logon.logonocinterface.services.EncodedOrderNumbers_type1;
import com.crh.logon.logonocinterface.services.GetContractQuantityCalculationRequest;
import com.crh.logon.logonocinterface.services.GetContractQuantityCalculationResponse;
import com.crh.logon.logonocinterface.services.GetOrdersRequest;
import com.crh.logon.logonocinterface.services.GetOrdersResponse;
import com.crh.logon.logonocinterface.services.LogonOrderItemNumber_type1;
import com.crh.logon.logonocinterface.services.LogonOrderNo_type1;
import com.crh.logon.logonocinterface.services.LogonOrderNo_type3;
import com.crh.logon.logonocinterface.services.LogonOrderNo_type5;
import com.crh.logon.logonocinterface.services.MaterialNo_type11;
import com.crh.logon.logonocinterface.services.MaterialNo_type5;
import com.crh.logon.logonocinterface.services.MaterialQuantity_type0;
import com.crh.logon.logonocinterface.services.OpenQuantityInTons_type1;
import com.crh.logon.logonocinterface.services.OpenQuantity_type1;
import com.crh.logon.logonocinterface.services.OrderItems_type0;
import com.crh.logon.logonocinterface.services.OrderStatus_type1;
import com.crh.logon.logonocinterface.services.Order_type1;
import com.crh.logon.logonocinterface.services.Order_type4;
import com.crh.logon.logonocinterface.services.QuantityUom_type9;
import com.crh.logon.logonocinterface.services.Quantity_type9;
import com.crh.logon.logonocinterface.services.SelectedLogonOrders_type1;
import com.crh.logon.logonocinterface.services.ShippingpointNo_type5;
import com.crh.logon.logonocinterface.services.ShiptoSapId_type3;
import com.crh.logon.logonocinterface.services.ShiptoSapId_type5;
import com.crh.logon.logonocinterface.services.SoldToSapId_type3;
import com.crh.logon.logonocinterface.services.SoldToSapId_type5;
import com.crh.logon.logonocinterface.services.SorgCd_type3;
import com.crh.logon.logonocinterface.services.SupplyPlantNo_type5;
import com.crh.logon.logonocinterface.services.TargetQuantityUom_type1;
import com.crh.logon.logonocinterface.services.TotalNumberOfPages_type1;
import com.crh.logon.logonocinterface.services.TotalTargetQuantity_type1;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.holcim.logon.eventbeans.in.logon_oc_interface.ObjectFactory;
import com.holcim.logon.eventbeans.in.logon_oc_interface.Request;
import com.holcim.logon.eventbeans.in.logon_oc_interface.Response;
import com.holcim.logon.eventbeans.in.logon_oc_interface.ResponseCode;

public class LogonOCInterfaceConsumer implements LogonOCInterfaceConsumerInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LogonOCInterfaceConsumer.class);

	private static final String CLIENT_TYPE_LOGON = "LOGON";

	private static final ObjectFactory OC_INTERFACE_OBJECT_FACTORY = new ObjectFactory();

	/*
	 * Copies header information from webservice request received from the order creation source
	 * to an internal request object.
	 * @param request The internal request object where the header data is set
	 * @param wsRequest The incoming request from order creation source*/
	private static void copyWsRequestHeader(Request request, com.crh.logon.logonocinterface.common_in.Request wsRequest) {

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
		request.setSimulateOnly(Boolean.FALSE);
	}

	private static Header createHeader(OcRequestId_type1 ocRequestId) {
		Header header = new Header();
		header.setClientType(CLIENT_TYPE_LOGON);
		String senderInfo = getSenderInfo();
		header.setSenderInfo(senderInfo);
		header.setSendersTime(Calendar.getInstance());
		header.setOcRequestId(ocRequestId);

		return header;
	}

	private static String getSenderInfo() {
		try {
			InitialContext initContext = new InitialContext();
			ConfigServiceLocalInterface configService = (ConfigServiceLocalInterface) initContext
					.lookup(LocalServices.APPLICATION_NAME
							+ "/ConfigServiceBean!com.crh.logon.business.services.localinterfaces.ConfigServiceLocalInterface");
			ConfigBean configBean = configService.getConfigBean();
			return configBean.getInstance();
		}
		catch (NamingException e) {
			LOG.error(e.getMessage());
		}
		return null;
	}

	private static void setWsResponse(com.crh.logon.logonocinterface.common_in.Response wsResponse, Response response,
			OcRequestId_type1 ocRequestId) {

		wsResponse.setHeader(createHeader(ocRequestId));
		if (response.getCode() == null) {

			throw new LogonException("Response code is null", Reason.RES_ERROR_UNSPECIFIED);
		}
		wsResponse.setCode(response.getCode().toString());
		if (response.getTxt() != null) {
			wsResponse.setTxt(response.getTxt() + (response.getContextValue() != null ? " (" + response.getContextValue() + ")"
					: ""));
		}
	}

	private static void setWsResponse(com.crh.logon.logonocinterface.common_in.Response wsResponse, OcRequestId_type1 ocRequestId,
			Throwable t) {

		LogonException e;
		if (t instanceof LogonException) {
			e = (LogonException) t;
		}
		else {
			e = new LogonException(t);
		}
		ExceptionUtil.errException(LOG, e, null);
		wsResponse.setHeader(createHeader(ocRequestId));
		wsResponse.setCode(ResponseCode.TECHNICAL_ERROR.value().toString());
		wsResponse.setTxt((e.getMessage() != null ? e.getMessage() + " " : "") + "(" + e.getId() + ")");
	}

	private static String getSupportedLanguage(String language) {

		String[] languages = new String[] { Constant.LANGUAGE_CODE_ENGLISH,
				Constant.LANGUAGE_CODE_FRENCH,
				Constant.LANGUAGE_CODE_GERMAN,
				Constant.LANGUAGE_CODE_ITALIAN,
				Constant.LANGUAGE_CODE_SPANISH };

		if (language == null || !Arrays.asList(languages).contains(language)) {

			return Constant.LANGUAGE_CODE_ENGLISH;
		}

		return language;
	}


	private static void getWsBusinessPartnerContactData(
			com.holcim.logon.eventbeans.in.logon_oc_interface.BusinessPartnerContactData businessPartnerData,
			com.crh.logon.logonocinterface.common_in.BusinessPartnerContactData wsBusinessPartnerData) {


		if (wsBusinessPartnerData.getName1() != null) {
			businessPartnerData.setName1(wsBusinessPartnerData.getName1().getName1_type0());
		}
		if (wsBusinessPartnerData.getName2() != null) {
			businessPartnerData.setName2(wsBusinessPartnerData.getName2().getName2_type0());
		}
		if (wsBusinessPartnerData.getStreet() != null) {
			businessPartnerData.setStreet(wsBusinessPartnerData.getStreet().getStreet_type0());
		}
		if (wsBusinessPartnerData.getZipCode() != null) {
			businessPartnerData.setZipCode(wsBusinessPartnerData.getZipCode().getZipCode_type0());
		}
		if (wsBusinessPartnerData.getCity() != null) {
			businessPartnerData.setCity(wsBusinessPartnerData.getCity().getCity_type0());
		}
		if (wsBusinessPartnerData.getCountry() != null) {
			businessPartnerData.setCountry(wsBusinessPartnerData.getCountry().getCountry_type0());
		}
		if (wsBusinessPartnerData.getPhoneNumber() != null) {
			businessPartnerData.setPhoneNumber(wsBusinessPartnerData.getPhoneNumber().getPhoneNumber_type0());
		}
		if (wsBusinessPartnerData.getVatRegistrationNumber() != null) {
			businessPartnerData
					.setVatRegistrationNumber(wsBusinessPartnerData.getVatRegistrationNumber().getVatRegistrationNumber_type0());
		}
	}

	private static void getWsOrderItemData(
			com.holcim.logon.eventbeans.in.logon_oc_interface.CreateRoadDirectCustomerOrderRequest.Order.OrderItem orderItemData,
			com.crh.logon.logonocinterface.services.OrderItem_type0 wsOrderItemData) {

		if (wsOrderItemData.getMaterialNo() != null) {
			orderItemData.setMaterialNo(wsOrderItemData.getMaterialNo().getMaterialNo_type0());
		}
		if (wsOrderItemData.getLoadingpointNo() != null) {
			orderItemData.setLoadingpointNo(wsOrderItemData.getLoadingpointNo().getLoadingpointNo_type0());
		}

		if (wsOrderItemData.getContractItemNumber() != null) {
			orderItemData.setContractItemNumber(wsOrderItemData.getContractItemNumber().getContractItemNumber_type0());
		}
		if (wsOrderItemData.getProdPlantNo() != null) {
			orderItemData.setProdPlantNo(wsOrderItemData.getProdPlantNo().getProdPlantNo_type0());
		}
		if (wsOrderItemData.getQuantity() != null) {
			orderItemData.setQuantity(wsOrderItemData.getQuantity().getQuantity_type0());
		}
		if (wsOrderItemData.getQuantityUom() != null) {
			orderItemData.setQuantityUom(wsOrderItemData.getQuantityUom().getQuantityUom_type0());
		}
	}

	private static void getWsOrderItemData(
			com.holcim.logon.eventbeans.in.logon_oc_interface.CreatePickupCustomerOrderRequest.Order.OrderItem orderItemData,
			com.crh.logon.logonocinterface.services.OrderItem_type1 wsOrderItemData) {

		if (wsOrderItemData.getMaterialNo() != null) {
			orderItemData.setMaterialNo(wsOrderItemData.getMaterialNo().getMaterialNo_type6());
		}
		if (wsOrderItemData.getLoadingpointNo() != null) {
			orderItemData.setLoadingpointNo(wsOrderItemData.getLoadingpointNo().getLoadingpointNo_type2());
		}

		if (wsOrderItemData.getContractItemNumber() != null) {
			orderItemData.setContractItemNumber(wsOrderItemData.getContractItemNumber().getContractItemNumber_type4());
		}
		if (wsOrderItemData.getProdPlantNo() != null) {
			orderItemData.setProdPlantNo(wsOrderItemData.getProdPlantNo().getProdPlantNo_type4());
		}
		if (wsOrderItemData.getQuantity() != null) {
			orderItemData.setQuantity(wsOrderItemData.getQuantity().getQuantity_type4());
		}
		if (wsOrderItemData.getQuantityUom() != null) {
			orderItemData.setQuantityUom(wsOrderItemData.getQuantityUom().getQuantityUom_type4());
		}
	}

	private static void setWsVendorData(com.crh.logon.logonocinterface.common_in.VendorData wsVendorData,
			com.holcim.logon.eventbeans.in.logon_oc_interface.VendorData vendorData) {

		if (vendorData.getSapVendorId() != null) {
			com.crh.logon.logonocinterface.common_in.SapVendorId_type1 data =
					new com.crh.logon.logonocinterface.common_in.SapVendorId_type1();
			data.setSapVendorId_type0(vendorData.getSapVendorId());
			wsVendorData.setSapVendorId(data);
		}
		if (vendorData.getName1() != null) {
			com.crh.logon.logonocinterface.common_in.Name1_type3 data =
					new com.crh.logon.logonocinterface.common_in.Name1_type3();
			data.setName1_type2(vendorData.getName1());
			wsVendorData.setName1(data);
		}
		if (vendorData.getName2() != null) {
			com.crh.logon.logonocinterface.common_in.Name2_type3 data =
					new com.crh.logon.logonocinterface.common_in.Name2_type3();
			data.setName2_type2(vendorData.getName2());
			wsVendorData.setName2(data);
		}
		if (vendorData.getStreet() != null) {
			com.crh.logon.logonocinterface.common_in.Street_type3 data =
					new com.crh.logon.logonocinterface.common_in.Street_type3();
			data.setStreet_type2(vendorData.getStreet());
			wsVendorData.setStreet(data);
		}
		if (vendorData.getZipCode() != null) {
			com.crh.logon.logonocinterface.common_in.ZipCode_type3 data =
					new com.crh.logon.logonocinterface.common_in.ZipCode_type3();
			data.setZipCode_type2(vendorData.getZipCode());
			wsVendorData.setZipCode(data);
		}
		if (vendorData.getCity() != null) {
			com.crh.logon.logonocinterface.common_in.City_type3 data = new com.crh.logon.logonocinterface.common_in.City_type3();
			data.setCity_type2(vendorData.getCity());
			wsVendorData.setCity(data);
		}
		if (vendorData.getCountry() != null) {
			com.crh.logon.logonocinterface.common_in.Country_type3 data =
					new com.crh.logon.logonocinterface.common_in.Country_type3();
			data.setCountry_type2(vendorData.getCountry());
			wsVendorData.setCountry(data);
		}
		if (vendorData.getPhoneNumber() != null) {
			com.crh.logon.logonocinterface.common_in.PhoneNumber_type3 data =
					new com.crh.logon.logonocinterface.common_in.PhoneNumber_type3();
			data.setPhoneNumber_type2(vendorData.getPhoneNumber());
			wsVendorData.setPhoneNumber(data);
		}
		if (vendorData.getTaxNumber() != null) {
			com.crh.logon.logonocinterface.common_in.TaxNumber_type1 data =
					new com.crh.logon.logonocinterface.common_in.TaxNumber_type1();
			data.setTaxNumber_type0(vendorData.getTaxNumber());
			wsVendorData.setTaxNumber(data);
		}
	}

	private static void getWsServiceMaterialData(
			com.holcim.logon.eventbeans.in.logon_oc_interface.CreateRoadDirectCustomerOrderRequest.Order.ServiceMaterial serviceMaterialData,
			com.crh.logon.logonocinterface.services.ServiceMaterial_type0 wsServiceMaterialData) {

		if (wsServiceMaterialData.getMaterialNo() != null) {
			serviceMaterialData.setMaterialNo(wsServiceMaterialData.getMaterialNo().getMaterialNo_type2());
		}

		if (wsServiceMaterialData.getContractItemNumber() != null) {
			serviceMaterialData
					.setContractItemNumber(wsServiceMaterialData.getContractItemNumber().getContractItemNumber_type2());
		}
		if (wsServiceMaterialData.getProdPlantNo() != null) {
			serviceMaterialData.setProdPlantNo(wsServiceMaterialData.getProdPlantNo().getProdPlantNo_type2());
		}
		if (wsServiceMaterialData.getQuantity() != null) {
			serviceMaterialData.setQuantity(wsServiceMaterialData.getQuantity().getQuantity_type2());
		}
		if (wsServiceMaterialData.getQuantityUom() != null) {
			serviceMaterialData.setQuantityUom(wsServiceMaterialData.getQuantityUom().getQuantityUom_type2());
		}
	}

	private void getWsServiceMaterialData(
			com.holcim.logon.eventbeans.in.logon_oc_interface.CreatePickupCustomerOrderRequest.Order.ServiceMaterial serviceMaterialData,
			com.crh.logon.logonocinterface.services.ServiceMaterial_type1 wsServiceMaterialData) {

		if (wsServiceMaterialData.getMaterialNo() != null) {
			serviceMaterialData.setMaterialNo(wsServiceMaterialData.getMaterialNo().getMaterialNo_type8());
		}

		if (wsServiceMaterialData.getContractItemNumber() != null) {
			serviceMaterialData
					.setContractItemNumber(wsServiceMaterialData.getContractItemNumber().getContractItemNumber_type6());
		}
		if (wsServiceMaterialData.getProdPlantNo() != null) {
			serviceMaterialData.setProdPlantNo(wsServiceMaterialData.getProdPlantNo().getProdPlantNo_type6());
		}
		if (wsServiceMaterialData.getQuantity() != null) {
			serviceMaterialData.setQuantity(wsServiceMaterialData.getQuantity().getQuantity_type6());
		}
		if (wsServiceMaterialData.getQuantityUom() != null) {
			serviceMaterialData.setQuantityUom(wsServiceMaterialData.getQuantityUom().getQuantityUom_type6());
		}
	}


	private static void setWsCreatedOrderData(com.crh.logon.logonocinterface.services.Order_type3 wsCreatedOrderData,
			com.holcim.logon.eventbeans.in.logon_oc_interface.CreateRoadDirectCustomerOrderResponse.Order createdOrderData) {

		if (createdOrderData.isWarningMultipleDispocard() != null) {
			wsCreatedOrderData.setWarningMultipleDispocard(createdOrderData.isWarningMultipleDispocard());
		}

		if (createdOrderData.getLogonOrderNo() != null) {
			com.crh.logon.logonocinterface.services.LogonOrderNo_type3 logonOrderNo = new LogonOrderNo_type3();
			logonOrderNo.setLogonOrderNo_type2(createdOrderData.getLogonOrderNo());
			wsCreatedOrderData.setLogonOrderNo(logonOrderNo);
		}

		if (createdOrderData.getOrderStatusCd() != null) {
			com.crh.logon.logonocinterface.services.OrderStatusCd_type1 orderStatusCd = new com.crh.logon.logonocinterface.services.OrderStatusCd_type1();
			orderStatusCd.setOrderStatusCd_type0(createdOrderData.getOrderStatusCd());
			wsCreatedOrderData.setOrderStatusCd(orderStatusCd);
		}

		if (createdOrderData.getCommercialCarrier() != null) {
			com.crh.logon.logonocinterface.common_in.VendorData data = new com.crh.logon.logonocinterface.common_in.VendorData();
			setWsVendorData(data, createdOrderData.getCommercialCarrier());
			wsCreatedOrderData.setCommercialCarrier(data);
		}
		if (createdOrderData.getExecutingCarrier() != null) {
			com.crh.logon.logonocinterface.common_in.VendorData data = new com.crh.logon.logonocinterface.common_in.VendorData();
			setWsVendorData(data, createdOrderData.getExecutingCarrier());
			wsCreatedOrderData.setExecutingCarrier(data);
		}
		if (createdOrderData.getPoolName() != null) {
			com.crh.logon.logonocinterface.services.PoolName_type1 data =
					new com.crh.logon.logonocinterface.services.PoolName_type1();
			data.setPoolName_type0(createdOrderData.getPoolName());
			wsCreatedOrderData.setPoolName(data);
		}

	}

	private static void setWsCreatedPickupOrderData(com.crh.logon.logonocinterface.services.Order_type0 wsCreatedPickupOrderData,
			com.holcim.logon.eventbeans.in.logon_oc_interface.CreatePickupCustomerOrderResponse.Order createdPickupOrderData){

		if (createdPickupOrderData.getLogonOrderNo() != null) {
			LogonOrderNo_type1 data = new LogonOrderNo_type1();
			data.setLogonOrderNo_type0(createdPickupOrderData.getLogonOrderNo());
			wsCreatedPickupOrderData.setLogonOrderNo(data);
		}
	}

	@AdnTrace
	public CreatePickupCustomerOrderResponse createPickupCustomerOrder(
			CreatePickupCustomerOrderRequest wsRequest) {
		Response response = null;

		CreatePickupCustomerOrderResponse wsResponse = new CreatePickupCustomerOrderResponse();
			try {
				com.holcim.logon.eventbeans.in.logon_oc_interface.CreatePickupCustomerOrderRequest request =
				OC_INTERFACE_OBJECT_FACTORY.createCreatePickupCustomerOrderRequest();
				copyWsRequestHeader(request, wsRequest);
					request.setOrder(
							new com.holcim.logon.eventbeans.in.logon_oc_interface.CreatePickupCustomerOrderRequest.Order());

				final com.holcim.logon.eventbeans.in.logon_oc_interface.CreatePickupCustomerOrderRequest.Order orderData =
						request.getOrder();

				com.crh.logon.logonocinterface.services.Order_type2 wsOrderData = wsRequest.getOrder();

				if (wsOrderData.getContractNumber() != null) {
					orderData.setContractNumber(wsOrderData.getContractNumber().getContractNumber_type6());
				}

				if (wsOrderData.getCustorderCustrefNo() != null) {
					orderData.setCustorderCustrefNo(wsOrderData.getCustorderCustrefNo().getCustorderCustrefNo_type2());
				}
				if (wsOrderData.getSoldTo() != null) {
					orderData.setSoldTo(
							new com.holcim.logon.eventbeans.in.logon_oc_interface.CreatePickupCustomerOrderRequest.Order.SoldTo());
					orderData.getSoldTo().setBusinessPartnerContactData(
							new com.holcim.logon.eventbeans.in.logon_oc_interface.BusinessPartnerContactData());
					orderData.getSoldTo().setSapId(wsOrderData.getSoldTo().getSapId().getSapId_type4());
					if (wsOrderData.getSoldTo().getBusinessPartnerContactData() != null) {
						getWsBusinessPartnerContactData(orderData.getSoldTo().getBusinessPartnerContactData(),
								wsOrderData.getSoldTo().getBusinessPartnerContactData());
					}

				}
				if (wsOrderData.getShipTo() != null) {
					orderData.setShipTo(
							new com.holcim.logon.eventbeans.in.logon_oc_interface.CreatePickupCustomerOrderRequest.Order.ShipTo());
					orderData.getShipTo().setSapId(wsOrderData.getShipTo().getSapId().getSapId_type6());
					orderData.getShipTo().setBusinessPartnerContactData(
							new com.holcim.logon.eventbeans.in.logon_oc_interface.BusinessPartnerContactData());
					if (wsOrderData.getShipTo().getBusinessPartnerContactData() != null) {
						getWsBusinessPartnerContactData(orderData.getShipTo().getBusinessPartnerContactData(),
								wsOrderData.getShipTo().getBusinessPartnerContactData());
					}
				}
				if (wsOrderData.getBillToSapId() != null) {
					orderData.setBillToSapId(wsOrderData.getBillToSapId().getBillToSapId_type2());
				}
				if (wsOrderData.getPayerSapId() != null) {
					orderData.setPayerSapId(wsOrderData.getPayerSapId().getPayerSapId_type2());
				}
				if (wsOrderData.getSupplyPlantNo() != null) {
					orderData.setSupplyPlantNo(wsOrderData.getSupplyPlantNo().getSupplyPlantNo_type2());
				}
				if (wsOrderData.getShippingpointNo() != null) {
					orderData.setShippingpointNo(wsOrderData.getShippingpointNo().getShippingpointNo_type2());
				}

				if (wsOrderData.getEarliestDeliveryTime() != null) {
					orderData.setEarliestDeliveryTime(wsOrderData.getEarliestDeliveryTime().getTime());
				}
				if (wsOrderData.getLatestDeliveryTime() != null) {
					orderData.setLatestDeliveryTime(wsOrderData.getLatestDeliveryTime().getTime());
				}
				orderData.setPrintDeliveryNote(wsOrderData.getPrintDeliveryNote());

				for (com.crh.logon.logonocinterface.services.OrderItem_type1 wsOrderItemData : wsOrderData.getOrderItem()) {
					com.holcim.logon.eventbeans.in.logon_oc_interface.CreatePickupCustomerOrderRequest.Order.OrderItem
							orderItemData =
							new com.holcim.logon.eventbeans.in.logon_oc_interface.CreatePickupCustomerOrderRequest.Order.OrderItem();
					getWsOrderItemData(orderItemData, wsOrderItemData);
					orderData.getOrderItem().add(orderItemData);
				}

				if (wsOrderData.getServiceMaterial() != null) {
					for (com.crh.logon.logonocinterface.services.ServiceMaterial_type1 wsServiceMaterialData : wsOrderData
							.getServiceMaterial()) {
						com.holcim.logon.eventbeans.in.logon_oc_interface.CreatePickupCustomerOrderRequest.Order.ServiceMaterial
								serviceMaterialData =
								new com.holcim.logon.eventbeans.in.logon_oc_interface.CreatePickupCustomerOrderRequest.Order.ServiceMaterial();
						getWsServiceMaterialData(serviceMaterialData, wsServiceMaterialData);
						orderData.getServiceMaterial().add(serviceMaterialData);
					}
				}

				LogonEventContextBean contextBean = new LogonEventContextBean();
				contextBean.setInterfaceTypeCd(Constant.INTERFACE_TYPE_ORDER_CREATION_INTERFACE);

				RequestHandlerLocalInterface handler = LocalServices.getLocalInterfaceForRequestHandler(request.getClass()
				.getSimpleName());
				response = (Response) handler.handle(request, contextBean);

				setWsResponse(wsResponse, response, wsRequest.getHeader().getOcRequestId());
				com.holcim.logon.eventbeans.in.logon_oc_interface.CreatePickupCustomerOrderResponse
						CreatePickupCustomerOrderResponse =
						(com.holcim.logon.eventbeans.in.logon_oc_interface.CreatePickupCustomerOrderResponse) response;
				if (CreatePickupCustomerOrderResponse.getCustomerOrderNo() != null) {
					com.crh.logon.logonocinterface.services.CustomerOrderNo_type1 wsLogonCustomerOrderNo = new com.crh.logon
							.logonocinterface.services.CustomerOrderNo_type1();
					wsLogonCustomerOrderNo.setCustomerOrderNo_type0(CreatePickupCustomerOrderResponse.getCustomerOrderNo());
					wsResponse.setCustomerOrderNo(wsLogonCustomerOrderNo);
				}
				if (CreatePickupCustomerOrderResponse.getOrder() != null) {
					List<com.holcim.logon.eventbeans.in.logon_oc_interface.CreatePickupCustomerOrderResponse.Order>
							masterOrderDataList = CreatePickupCustomerOrderResponse.getOrder();
					if (masterOrderDataList != null) {
						com.crh.logon.logonocinterface.services.Order_type0[] wsMasterOrderDataArray =
								new com.crh.logon.logonocinterface.services.Order_type0[masterOrderDataList.size()];
						for (int i = 0; i < masterOrderDataList.size(); i++) {
							com.holcim.logon.eventbeans.in.logon_oc_interface.CreatePickupCustomerOrderResponse.Order
									masterOrderData = masterOrderDataList.get(i);
							wsMasterOrderDataArray[i] = new com.crh.logon.logonocinterface.services.Order_type0();
							setWsCreatedPickupOrderData(wsMasterOrderDataArray[i], masterOrderData);
						}
						wsResponse.setOrder(wsMasterOrderDataArray);
					}
				}

			} 
			catch (Throwable t) {
				setWsResponse(wsResponse, wsRequest.getHeader().getOcRequestId(), t);
			}


		return wsResponse;
	}

	@AdnTrace
	public CreateRoadDirectCustomerOrderResponse createRoadDirectCustomerOrder(
			CreateRoadDirectCustomerOrderRequest wsRequest) {
		Response response = null;

		CreateRoadDirectCustomerOrderResponse wsResponse = new
				CreateRoadDirectCustomerOrderResponse();
		try {
			com.holcim.logon.eventbeans.in.logon_oc_interface.CreateRoadDirectCustomerOrderRequest request =
					OC_INTERFACE_OBJECT_FACTORY.createCreateRoadDirectCustomerOrderRequest();
			copyWsRequestHeader(request, wsRequest);
			com.holcim.logon.eventbeans.in.logon_oc_interface.CreateRoadDirectCustomerOrderRequest.Order orderData =
					new com.holcim.logon.eventbeans.in.logon_oc_interface.CreateRoadDirectCustomerOrderRequest.Order();
			Order_type1 wsOrderData = wsRequest.getOrder();
			if (wsOrderData.getContractNumber() != null) {
				orderData.setContractNumber(wsOrderData.getContractNumber().getContractNumber_type0());
			}

			if (wsOrderData.getCustorderCustrefNo() != null) {
				orderData.setCustorderCustrefNo(wsOrderData.getCustorderCustrefNo().getCustorderCustrefNo_type0());
			}
			if (wsOrderData.getSoldTo() != null) {
				orderData.setSoldTo(
						new com.holcim.logon.eventbeans.in.logon_oc_interface.CreateRoadDirectCustomerOrderRequest.Order.SoldTo());
				orderData.getSoldTo().setBusinessPartnerContactData(
						new com.holcim.logon.eventbeans.in.logon_oc_interface.BusinessPartnerContactData());
				orderData.getSoldTo().setSapId(wsOrderData.getSoldTo().getSapId().getSapId_type0());
				if (wsOrderData.getSoldTo().getBusinessPartnerContactData() != null) {
					getWsBusinessPartnerContactData(orderData.getSoldTo().getBusinessPartnerContactData(),
							wsOrderData.getSoldTo().getBusinessPartnerContactData());
				}

			}
			if (wsOrderData.getShipTo() != null) {
				orderData.setShipTo(
						new com.holcim.logon.eventbeans.in.logon_oc_interface.CreateRoadDirectCustomerOrderRequest.Order.ShipTo());
				orderData.getShipTo().setSapId(wsOrderData.getShipTo().getSapId().getSapId_type2());
				orderData.getShipTo().setBusinessPartnerContactData(
						new com.holcim.logon.eventbeans.in.logon_oc_interface.BusinessPartnerContactData());
				if (wsOrderData.getShipTo().getBusinessPartnerContactData() != null) {
					getWsBusinessPartnerContactData(orderData.getShipTo().getBusinessPartnerContactData(),
							wsOrderData.getShipTo().getBusinessPartnerContactData());
				}
			}
			if (wsOrderData.getBillToSapId() != null) {
				orderData.setBillToSapId(wsOrderData.getBillToSapId().getBillToSapId_type0());
			}
			if (wsOrderData.getPayerSapId() != null) {
				orderData.setPayerSapId(wsOrderData.getPayerSapId().getPayerSapId_type0());
			}
			if (wsOrderData.getSupplyPlantNo() != null) {
				orderData.setSupplyPlantNo(wsOrderData.getSupplyPlantNo().getSupplyPlantNo_type0());
			}
			if (wsOrderData.getShippingpointNo() != null) {
				orderData.setShippingpointNo(wsOrderData.getShippingpointNo().getShippingpointNo_type0());
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
			if (wsOrderData.getEarliestDeliveryTime() != null) {
				orderData.setEarliestDeliveryTime(wsOrderData.getEarliestDeliveryTime().getTime());
			}
			if (wsOrderData.getLatestDeliveryTime() != null) {
				orderData.setLatestDeliveryTime(wsOrderData.getLatestDeliveryTime().getTime());
			}
			orderData.setPrintDeliveryNote(wsOrderData.getPrintDeliveryNote());
			orderData.setPrintCollectiveDeliveryNote(wsOrderData.getPrintCollectiveDeliveryNote());


			for (com.crh.logon.logonocinterface.services.OrderItem_type0 wsOrderItemData : wsOrderData.getOrderItem()) {
				com.holcim.logon.eventbeans.in.logon_oc_interface.CreateRoadDirectCustomerOrderRequest.Order.OrderItem
						orderItemData =
						new com.holcim.logon.eventbeans.in.logon_oc_interface.CreateRoadDirectCustomerOrderRequest.Order.OrderItem();
				getWsOrderItemData(orderItemData, wsOrderItemData);
				orderData.getOrderItem().add(orderItemData);
			}

			if (wsOrderData.getServiceMaterial() != null) {
				for (com.crh.logon.logonocinterface.services.ServiceMaterial_type0 wsServiceMaterialData : wsOrderData
						.getServiceMaterial()) {
					com.holcim.logon.eventbeans.in.logon_oc_interface.CreateRoadDirectCustomerOrderRequest.Order.ServiceMaterial
							serviceMaterialData =
							new com.holcim.logon.eventbeans.in.logon_oc_interface.CreateRoadDirectCustomerOrderRequest.Order.ServiceMaterial();
					getWsServiceMaterialData(serviceMaterialData, wsServiceMaterialData);
					orderData.getServiceMaterial().add(serviceMaterialData);
				}
			}
			request.setOrder(orderData);


			LogonEventContextBean contextBean = new LogonEventContextBean();
			contextBean.setInterfaceTypeCd(Constant.INTERFACE_TYPE_ORDER_CREATION_INTERFACE);

			RequestHandlerLocalInterface handler = LocalServices.getLocalInterfaceForRequestHandler(request.getClass()
					.getSimpleName());
			response = (Response) handler.handle(request, contextBean);

			setWsResponse(wsResponse, response, wsRequest.getHeader().getOcRequestId());

			com.holcim.logon.eventbeans.in.logon_oc_interface.CreateRoadDirectCustomerOrderResponse
					createRoadDirectCustomerOrderResponse = (com.holcim.logon.eventbeans.in.logon_oc_interface
					.CreateRoadDirectCustomerOrderResponse) response;
			if (createRoadDirectCustomerOrderResponse.getCustomerOrderNo() != null) {
				com.crh.logon.logonocinterface.services.CustomerOrderNo_type3 wsLogonCustomerOrderNo = new com.crh.logon
						.logonocinterface.services.CustomerOrderNo_type3();
				wsLogonCustomerOrderNo.setCustomerOrderNo_type2(createRoadDirectCustomerOrderResponse
						.getCustomerOrderNo());
				wsResponse.setCustomerOrderNo(wsLogonCustomerOrderNo);
			}

			List<com.holcim.logon.eventbeans.in.logon_oc_interface.CreateRoadDirectCustomerOrderResponse.Order>
					masterOrderDataList = createRoadDirectCustomerOrderResponse
					.getOrder();
			if (masterOrderDataList != null) {
				com.crh.logon.logonocinterface.services.Order_type3[] wsMasterOrderDataArray =
						new com.crh.logon.logonocinterface.services.Order_type3[masterOrderDataList.size()];
				for (int i = 0; i < masterOrderDataList.size(); i++) {
					com.holcim.logon.eventbeans.in.logon_oc_interface.CreateRoadDirectCustomerOrderResponse.Order
							masterOrderData = masterOrderDataList.get(i);
					wsMasterOrderDataArray[i] = new com.crh.logon.logonocinterface.services.Order_type3();
					setWsCreatedOrderData(wsMasterOrderDataArray[i], masterOrderData);
				}
				wsResponse.setOrder(wsMasterOrderDataArray);
			}

		}
		catch (Throwable t) {
			setWsResponse(wsResponse, wsRequest.getHeader().getOcRequestId(), t);
		}


		return wsResponse;

	}

	@AdnTrace
	public GetContractQuantityCalculationResponse getContractQuantityCalculation(
			GetContractQuantityCalculationRequest wsRequest) {
		GetContractQuantityCalculationResponse wsResponse = new GetContractQuantityCalculationResponse();
		com.holcim.logon.eventbeans.in.logon_oc_interface.GetContractQuantityCalculationRequest request =
				OC_INTERFACE_OBJECT_FACTORY.createGetContractQuantityCalculationRequest();
		copyWsRequestHeader(request, wsRequest);
		LogonEventContextBean contextBean = new LogonEventContextBean();
		contextBean.setInterfaceTypeCd(Constant.INTERFACE_TYPE_ORDER_CREATION_INTERFACE);
		try {
			request.setSoldToSapId(wsRequest.getSoldToSapId().getSoldToSapId_type0());
			if (wsRequest.getShiptoSapId() != null) {
				request.setShiptoSapId(wsRequest.getShiptoSapId().getShiptoSapId_type0());
			}
			if (wsRequest.getSorgCd() != null) {
				request.setSorgCd(wsRequest.getSorgCd().getSorgCd_type0());
			}
			if (wsRequest.getContractNumber() != null) {
				request.setContractNumber(wsRequest.getContractNumber().getContractNumber_type2());
			}

			RequestHandlerLocalInterface handler =
					LocalServices.getLocalInterfaceForRequestHandler(request.getClass().getSimpleName());
			Response response = (Response) handler.handle(request, contextBean);
			setWsResponse(wsResponse, response, wsRequest.getHeader().getOcRequestId());

				com.holcim.logon.eventbeans.in.logon_oc_interface.GetContractQuantityCalculationResponse
						contractQuantityCalculationResponse =
						(com.holcim.logon.eventbeans.in.logon_oc_interface.GetContractQuantityCalculationResponse) response;
				List<com.holcim.logon.eventbeans.in.logon_oc_interface.GetContractQuantityCalculationResponse.ContractQuantityCalculation>
						contractQuantityCalculationList = contractQuantityCalculationResponse.getContractQuantityCalculation();
				if (contractQuantityCalculationList != null) {
					com.crh.logon.logonocinterface.services.ContractQuantityCalculation_type0[]
							wsContractQuantityCalculationArray =
							new ContractQuantityCalculation_type0[contractQuantityCalculationList.size()];
					for (int i = 0; i < contractQuantityCalculationList.size(); i++) {
						com.holcim.logon.eventbeans.in.logon_oc_interface.GetContractQuantityCalculationResponse.ContractQuantityCalculation
								masterData = contractQuantityCalculationList.get(i);
						wsContractQuantityCalculationArray[i] = new ContractQuantityCalculation_type0();
						setWsContractQuantityCalculationData(wsContractQuantityCalculationArray[i], masterData);
					}
					wsResponse.setContractQuantityCalculation(wsContractQuantityCalculationArray);
				}

		}
		catch (NamingException e) {
			LOG.error(e.getMessage());
		}
		return wsResponse;
	}

	@AdnTrace
	public GetOrdersResponse getOrders(GetOrdersRequest wsRequest) {
		GetOrdersResponse wsResponse = new GetOrdersResponse();
		com.holcim.logon.eventbeans.in.logon_oc_interface.GetOrdersRequest request =
				OC_INTERFACE_OBJECT_FACTORY.createGetOrdersRequest();
		LogonEventContextBean contextBean = new LogonEventContextBean();
		contextBean.setInterfaceTypeCd(Constant.INTERFACE_TYPE_ORDER_CREATION_INTERFACE);
		try {
			request.setSoldToSapId(wsRequest.getSoldToSapId().getSoldToSapId_type6());
			request.setPage(wsRequest.getPage().getPage_type0());

			if (wsRequest.getShiptoSapId() != null) {
				request.setShiptoSapId(wsRequest.getShiptoSapId().getShiptoSapId_type6());
			}
			if (wsRequest.getOrderCreationType() != null) {
				request.setOrderCreationType(wsRequest.getOrderCreationType().getOrderCreationType_type0());
			}
			if (wsRequest.getEncodedOrderNumbers() != null) {
				request.setEncodedOrderNumbers(wsRequest.getEncodedOrderNumbers().getEncodedOrderNumbers_type2());
			}
			SelectedLogonOrders_type1[] selectedLogonOrderArray = wsRequest.getSelectedLogonOrders();
			if (selectedLogonOrderArray != null && selectedLogonOrderArray.length != 0) {
				List<String> selectedLogonOrders = new ArrayList<>();
				for (SelectedLogonOrders_type1 selectedLogonOrder : selectedLogonOrderArray) {
					selectedLogonOrders.add(selectedLogonOrder.getSelectedLogonOrders_type0());
				}
				request.getSelectedLogonOrders().addAll(selectedLogonOrders);
			}

			RequestHandlerLocalInterface handler =
					LocalServices.getLocalInterfaceForRequestHandler(request.getClass().getSimpleName());
			copyWsRequestHeader(request, wsRequest);
			Response response = (Response) handler.handle(request, contextBean);
			setWsResponse(wsResponse, response, wsRequest.getHeader().getOcRequestId());

			if (response instanceof com.holcim.logon.eventbeans.in.logon_oc_interface.GetOrdersResponse) {
				com.holcim.logon.eventbeans.in.logon_oc_interface.GetOrdersResponse getOrdersResponse =
						(com.holcim.logon.eventbeans.in.logon_oc_interface.GetOrdersResponse) response;
				List<com.holcim.logon.eventbeans.in.logon_oc_interface.GetOrdersResponse.Order> orderList =
						getOrdersResponse.getOrder();
				if (orderList != null) {
					com.crh.logon.logonocinterface.services.Order_type4[] wsOrderArray = new Order_type4[orderList.size()];
					for (int i = 0; i < orderList.size(); i++) {
						com.holcim.logon.eventbeans.in.logon_oc_interface.GetOrdersResponse.Order masterData = orderList.get(i);
						wsOrderArray[i] = new Order_type4();
						setWsOrderData(wsOrderArray[i], masterData);
					}
					wsResponse.setOrder(wsOrderArray);
				}
				if (getOrdersResponse.getEncodedOrderNumbers() != null) {
					EncodedOrderNumbers_type1 data = new EncodedOrderNumbers_type1();
					data.setEncodedOrderNumbers_type0(getOrdersResponse.getEncodedOrderNumbers());
					wsResponse.setEncodedOrderNumbers(data);
				}
				TotalNumberOfPages_type1 data = new TotalNumberOfPages_type1();
				if (getOrdersResponse.getTotalNumberOfPages() != null) {
					data.setTotalNumberOfPages_type0(getOrdersResponse.getTotalNumberOfPages());
				} else {
					data.setTotalNumberOfPages_type0(BigDecimal.valueOf(1));
				}
				wsResponse.setTotalNumberOfPages(data);
			}
		}
		catch (NamingException e) {
			LOG.error(e.getMessage());
		}
		return wsResponse;
	}

	private static void setWsOrderData(Order_type4 wsCreatedData,
			com.holcim.logon.eventbeans.in.logon_oc_interface.GetOrdersResponse.Order createdData) {

		if (createdData.getLogonOrderNo() != null) {
			LogonOrderNo_type5 data = new LogonOrderNo_type5();
			data.setLogonOrderNo_type4(createdData.getLogonOrderNo());
			wsCreatedData.setLogonOrderNo(data);
		}
		if (createdData.getSorgCd() != null) {
			SorgCd_type3 data = new SorgCd_type3();
			data.setSorgCd_type2(createdData.getSorgCd());
			wsCreatedData.setSorgCd(data);
		}
		if (createdData.getSoldToSapId() != null) {
			SoldToSapId_type5 data = new SoldToSapId_type5();
			data.setSoldToSapId_type4(createdData.getSoldToSapId());
			wsCreatedData.setSoldToSapId(data);
		}
		if (createdData.getShiptoSapId() != null) {
			ShiptoSapId_type5 data = new ShiptoSapId_type5();
			data.setShiptoSapId_type4(createdData.getShiptoSapId());
			wsCreatedData.setShiptoSapId(data);
		}
		if (createdData.getEarliestDeliveryTime() != null) {
			Calendar data = Calendar.getInstance();
			data.setTime(createdData.getEarliestDeliveryTime());
			wsCreatedData.setEarliestDeliveryTime(data);
		}
		if (createdData.getLatestDeliveryTime() != null) {
			Calendar data = Calendar.getInstance();
			data.setTime(createdData.getLatestDeliveryTime());
			wsCreatedData.setLatestDeliveryTime(data);
		}
		if (createdData.getSupplyPlantNo() != null) {
			SupplyPlantNo_type5 data = new SupplyPlantNo_type5();
			data.setSupplyPlantNo_type4(createdData.getSupplyPlantNo());
			wsCreatedData.setSupplyPlantNo(data);
		}
		if (createdData.getShippingpointNo() != null) {
			ShippingpointNo_type5 data = new ShippingpointNo_type5();
			data.setShippingpointNo_type4(createdData.getShippingpointNo());
			wsCreatedData.setShippingpointNo(data);
		}
		if (createdData.getDeliveryType() != null) {
			DeliveryType_type1 data = new DeliveryType_type1();
			data.setDeliveryType_type0(createdData.getDeliveryType());
			wsCreatedData.setDeliveryType(data);
		}
		if (createdData.getOrderStatus() != null) {
			OrderStatus_type1 data = new OrderStatus_type1();
			data.setOrderStatus_type0(createdData.getOrderStatus());
			wsCreatedData.setOrderStatus(data);
		}
		if (createdData.getOrderItems() != null) {
			OrderItems_type0[] data = new OrderItems_type0[createdData.getOrderItems().size()];
			for (int i = 0; i < createdData.getOrderItems().size(); i++) {
				com.holcim.logon.eventbeans.in.logon_oc_interface.GetOrdersResponse.Order.OrderItems
						orderItemsData = createdData.getOrderItems().get(i);
				data[i] = new OrderItems_type0();
				setWsOrderItemsData(data[i], orderItemsData);
			}
			wsCreatedData.setOrderItems(data);
		}
	}

	private static void setWsOrderItemsData(OrderItems_type0 wsCreatedData,
			com.holcim.logon.eventbeans.in.logon_oc_interface.GetOrdersResponse.Order.OrderItems createdData) {

		if (createdData.getLogonOrderItemNumber() != null) {
			LogonOrderItemNumber_type1 data = new LogonOrderItemNumber_type1();
			data.setLogonOrderItemNumber_type0(createdData.getLogonOrderItemNumber());
			wsCreatedData.setLogonOrderItemNumber(data);
		}
		if (createdData.getMaterialNo() != null) {
			MaterialNo_type11 data = new MaterialNo_type11();
			data.setMaterialNo_type10(createdData.getMaterialNo());
			wsCreatedData.setMaterialNo(data);
		}
		if (createdData.getQuantity() != null) {
			Quantity_type9 data = new Quantity_type9();
			data.setQuantity_type8(createdData.getQuantity());
			wsCreatedData.setQuantity(data);
		}
		if (createdData.getQuantityUom() != null) {
			QuantityUom_type9 data = new QuantityUom_type9();
			data.setQuantityUom_type8(createdData.getQuantityUom());
			wsCreatedData.setQuantityUom(data);
		}
	}

	private static void setWsContractQuantityCalculationData(ContractQuantityCalculation_type0 wsCreatedData,
			com.holcim.logon.eventbeans.in.logon_oc_interface.GetContractQuantityCalculationResponse.ContractQuantityCalculation createdData) {

		if (createdData.getSoldToSapId() != null) {
			SoldToSapId_type3 data = new SoldToSapId_type3();
			data.setSoldToSapId_type2(createdData.getSoldToSapId());
			wsCreatedData.setSoldToSapId(data);
		}
		if (createdData.getShiptoSapId() != null) {
			ShiptoSapId_type3 data = new ShiptoSapId_type3();
			data.setShiptoSapId_type2(createdData.getShiptoSapId());
			wsCreatedData.setShiptoSapId(data);
		}
		if (createdData.getContractNumber() != null) {
			ContractNumber_type5 data = new ContractNumber_type5();
			data.setContractNumber_type4(createdData.getContractNumber());
			wsCreatedData.setContractNumber(data);
		}
		if (createdData.getContractType() != null) {
			ContractType_type1 data = new ContractType_type1();
			data.setContractType_type0(createdData.getContractType());
			wsCreatedData.setContractType(data);
		}
		if (createdData.getMaterialQuantity() != null) {
			MaterialQuantity_type0[] data = new MaterialQuantity_type0[createdData.getMaterialQuantity().size()];
			for (int i = 0; i < createdData.getMaterialQuantity().size(); i++) {
				com.holcim.logon.eventbeans.in.logon_oc_interface.GetContractQuantityCalculationResponse.ContractQuantityCalculation.MaterialQuantity
						materialQuantityData = createdData.getMaterialQuantity().get(i);
				data[i] = new MaterialQuantity_type0();
				setWsMaterialQuantityData(data[i], materialQuantityData);
			}
			wsCreatedData.setMaterialQuantity(data);
		}
	}

	private static void setWsMaterialQuantityData(MaterialQuantity_type0 wsCreatedData,
			com.holcim.logon.eventbeans.in.logon_oc_interface.GetContractQuantityCalculationResponse.ContractQuantityCalculation.MaterialQuantity createdData) {

		if (createdData.getMaterialNo() != null) {
			MaterialNo_type5 data = new MaterialNo_type5();
			data.setMaterialNo_type4(createdData.getMaterialNo());
			wsCreatedData.setMaterialNo(data);
		}
		if (createdData.getTargetQuantityUom() != null) {
			TargetQuantityUom_type1 data = new TargetQuantityUom_type1();
			data.setTargetQuantityUom_type0(createdData.getTargetQuantityUom());
			wsCreatedData.setTargetQuantityUom(data);
		}
		if (createdData.getTotalTargetQuantity() != null) {
			TotalTargetQuantity_type1 data = new TotalTargetQuantity_type1();
			data.setTotalTargetQuantity_type0(createdData.getTotalTargetQuantity());
			wsCreatedData.setTotalTargetQuantity(data);
		}
		if (createdData.getOpenQuantity() != null) {
			OpenQuantity_type1 data = new OpenQuantity_type1();
			data.setOpenQuantity_type0(createdData.getOpenQuantity());
			wsCreatedData.setOpenQuantity(data);
		}
		if (createdData.getOpenQuantityInTons() != null) {
			OpenQuantityInTons_type1 data = new OpenQuantityInTons_type1();
			data.setOpenQuantityInTons_type0(createdData.getOpenQuantityInTons());
			wsCreatedData.setOpenQuantityInTons(data);
		}
		if (createdData.getAvailableQuantity() != null) {
			AvailableQuantity_type1 data = new AvailableQuantity_type1();
			data.setAvailableQuantity_type0(createdData.getAvailableQuantity());
			wsCreatedData.setAvailableQuantity(data);
		}
		if (createdData.getAvailableQuantityInTons() != null) {
			AvailableQuantityInTons_type1 data = new AvailableQuantityInTons_type1();
			data.setAvailableQuantityInTons_type0(createdData.getAvailableQuantityInTons());
			wsCreatedData.setAvailableQuantityInTons(data);
		}
	}
}

