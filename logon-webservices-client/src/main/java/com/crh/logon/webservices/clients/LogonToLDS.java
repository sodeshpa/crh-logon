/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients;

import java.util.Calendar;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import com.crh.logon.webservices.clients.beans.logon_lds_interface.DeleteLoadOrderDTO;
import com.crh.logon.webservices.clients.beans.logon_lds_interface.PdaLoginDTO;
import com.crh.logon.webservices.clients.util.ClientConfig;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.engine.Phase;
import org.apache.axis2.handlers.AbstractHandler;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.holcim.logon.eventbeans.in.logon_lds_interface.BusinessPartnerData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.LoadOrderData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.LoadOrderItemData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.LocationData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.MaterialData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.OrderData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.OrderItemData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.OrderItemData.PossibleMaterial;
import com.holcim.logon.eventbeans.in.logon_lds_interface.PriceData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.SchedulingData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.ServiceMaterialData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.VehicleData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.VendorData;
import com.crh.logon.to.lds.LogonLDSInterfaceStub;
import com.crh.logon.to.lds.LogonLDSInterfaceStub.DeleteLoadOrderRequest;
import com.crh.logon.to.lds.LogonLDSInterfaceStub.DeleteLoadOrderResponse;
import com.crh.logon.to.lds.LogonLDSInterfaceStub.Header;
import com.crh.logon.to.lds.LogonLDSInterfaceStub.LoadOrderNotificationRequest;
import com.crh.logon.to.lds.LogonLDSInterfaceStub.LoadOrderNotificationResponse;
import com.crh.logon.to.lds.LogonLDSInterfaceStub.PdaLoginRequest;
import com.crh.logon.to.lds.LogonLDSInterfaceStub.PdaLoginResponse;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.webservices.clients.beans.logon_lds_interface.LoadOrderNotificationDTO;
import com.crh.logon.webservices.clients.util.WebserviceClientUtil;

/**
 * @author AdNovum Informatik AG
 */
public class LogonToLDS {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LogonToLDS.class);
	
	private static final String CLIENT_TYPE_LOGON = "LOGON";
	
	private final ConfigBean configBean;
	
	private Header getHeader() {
		
		Header header = new Header();
		header.setClientType(CLIENT_TYPE_LOGON);
		String senderInfo = configBean.getInstance();
		header.setSenderInfo(senderInfo);
		header.setSendersTime(Calendar.getInstance());
		
		return header;
	}

	public LogonToLDS(ConfigBean configBean) {
		this.configBean = configBean;
	}

	private static void setWsVendorData(LogonLDSInterfaceStub.VendorData wsVendorData, VendorData vendorData) {
		
		if (vendorData.getSapVendorId() != null) {
			LogonLDSInterfaceStub.SapVendorId_type1 data = new LogonLDSInterfaceStub.SapVendorId_type1();
			data.setSapVendorId_type0(vendorData.getSapVendorId());
			wsVendorData.setSapVendorId(data);
		}
		if (vendorData.getName1() != null) {
			LogonLDSInterfaceStub.Name1_type1 data = new LogonLDSInterfaceStub.Name1_type1();
			data.setName1_type0(vendorData.getName1());
			wsVendorData.setName1(data);
		}
		if (vendorData.getName2() != null) {
			LogonLDSInterfaceStub.Name2_type1 data = new LogonLDSInterfaceStub.Name2_type1();
			data.setName2_type0(vendorData.getName2());
			wsVendorData.setName2(data);
		}
		if (vendorData.getStreet() != null) {
			LogonLDSInterfaceStub.Street_type1 data = new LogonLDSInterfaceStub.Street_type1();
			data.setStreet_type0(vendorData.getStreet());
			wsVendorData.setStreet(data);
		}
		if (vendorData.getZipCode() != null) {
			LogonLDSInterfaceStub.ZipCode_type1 data = new LogonLDSInterfaceStub.ZipCode_type1();
			data.setZipCode_type0(vendorData.getZipCode());
			wsVendorData.setZipCode(data);
		}
		if (vendorData.getCity() != null) {
			LogonLDSInterfaceStub.City_type1 data = new LogonLDSInterfaceStub.City_type1();
			data.setCity_type0(vendorData.getCity());
			wsVendorData.setCity(data);
		}
		if (vendorData.getCountry() != null) {
			LogonLDSInterfaceStub.Country_type1 data = new LogonLDSInterfaceStub.Country_type1();
			data.setCountry_type0(vendorData.getCountry());
			wsVendorData.setCountry(data);
		}
		if (vendorData.getPhoneNumber() != null) {
			LogonLDSInterfaceStub.PhoneNumber_type1 data = new LogonLDSInterfaceStub.PhoneNumber_type1();
			data.setPhoneNumber_type0(vendorData.getPhoneNumber());
			wsVendorData.setPhoneNumber(data);
		}
		if (vendorData.getTaxNumber() != null) {
			LogonLDSInterfaceStub.TaxNumber_type1 data = new LogonLDSInterfaceStub.TaxNumber_type1();
			data.setTaxNumber_type0(vendorData.getTaxNumber());
			wsVendorData.setTaxNumber(data);
		}
	}
	
	private static void setWsLocationData(LogonLDSInterfaceStub.LocationData wsLocationData, LocationData locationData) {
		
		if (locationData.getSupplyPlantNo() != null) {
			LogonLDSInterfaceStub.SupplyPlantNo_type5 data = new LogonLDSInterfaceStub.SupplyPlantNo_type5();
			data.setSupplyPlantNo_type4(locationData.getSupplyPlantNo());
			wsLocationData.setSupplyPlantNo(data);
		}
		if (locationData.getShippingpointNo() != null) {
			LogonLDSInterfaceStub.ShippingpointNo_type5 data = new LogonLDSInterfaceStub.ShippingpointNo_type5();
			data.setShippingpointNo_type4(locationData.getShippingpointNo());
			wsLocationData.setShippingpointNo(data);
		}
		if (locationData.getLoadingpointNo() != null) {
			LogonLDSInterfaceStub.LoadingpointNo_type5 data = new LogonLDSInterfaceStub.LoadingpointNo_type5();
			data.setLoadingpointNo_type4(locationData.getLoadingpointNo());
			wsLocationData.setLoadingpointNo(data);
		}
		if (locationData.getLoadingpointName() != null) {
			LogonLDSInterfaceStub.LoadingpointName_type3 data = new LogonLDSInterfaceStub.LoadingpointName_type3();
			String loadingPointName = locationData.getLoadingpointName();
			data.setLoadingpointName_type2(loadingPointName);
			wsLocationData.setLoadingpointName(data);
		}
	}
	
	private static void setWsVehicleData(LogonLDSInterfaceStub.VehicleData wsVehicleData, VehicleData vehicleData) {
		
		if (vehicleData.getCommercialCarrier() != null) {
			LogonLDSInterfaceStub.VendorData wsVendorData = new LogonLDSInterfaceStub.VendorData();
			setWsVendorData(wsVendorData, vehicleData.getCommercialCarrier());
			wsVehicleData.setCommercialCarrier(wsVendorData);
		}
		if (vehicleData.getExecutingCarrier() != null) {
			LogonLDSInterfaceStub.VendorData wsVendorData = new LogonLDSInterfaceStub.VendorData();
			setWsVendorData(wsVendorData, vehicleData.getExecutingCarrier());
			wsVehicleData.setExecutingCarrier(wsVendorData);
		}
		if (vehicleData.getVehicleType() != null) {
			LogonLDSInterfaceStub.VehicleType_type1 data = new LogonLDSInterfaceStub.VehicleType_type1();
			data.setVehicleType_type0(vehicleData.getVehicleType());
			wsVehicleData.setVehicleType(data);
		}
		if (vehicleData.getAllowedVehicleType() != null) {
			LogonLDSInterfaceStub.AllowedVehicleType_type1[] data = new LogonLDSInterfaceStub.AllowedVehicleType_type1[vehicleData.getAllowedVehicleType().size()];
			for (int i=0; i<vehicleData.getAllowedVehicleType().size(); i++) {
				String allowedVehicleType = vehicleData.getAllowedVehicleType().get(i);
				LogonLDSInterfaceStub.AllowedVehicleType_type1 wsAllowedVehicleType = new LogonLDSInterfaceStub.AllowedVehicleType_type1();
				wsAllowedVehicleType.setAllowedVehicleType_type0(allowedVehicleType);
				data[i] = wsAllowedVehicleType;
			}
			wsVehicleData.setAllowedVehicleType(data);
		}
		if (vehicleData.getTruckId() != null) {
			LogonLDSInterfaceStub.TruckId_type1 data = new LogonLDSInterfaceStub.TruckId_type1();
			data.setTruckId_type0(vehicleData.getTruckId());
			wsVehicleData.setTruckId(data);
		}
		if (vehicleData.getTruckPlateNo() != null) {
			LogonLDSInterfaceStub.TruckPlateNo_type1 data = new LogonLDSInterfaceStub.TruckPlateNo_type1();
			data.setTruckPlateNo_type0(vehicleData.getTruckPlateNo());
			wsVehicleData.setTruckPlateNo(data);
		}
		if (vehicleData.getSapTruckNo() != null) {
			LogonLDSInterfaceStub.SapTruckNo_type1 data = new LogonLDSInterfaceStub.SapTruckNo_type1();
			data.setSapTruckNo_type0(vehicleData.getSapTruckNo());
			wsVehicleData.setSapTruckNo(data);
		}
		if (vehicleData.getTrailerId() != null) {
			LogonLDSInterfaceStub.TrailerId_type1 data = new LogonLDSInterfaceStub.TrailerId_type1();
			data.setTrailerId_type0(vehicleData.getTrailerId());
			wsVehicleData.setTrailerId(data);
		}
		if (vehicleData.getTrailerPlateNo() != null) {
			LogonLDSInterfaceStub.TrailerPlateNo_type1 data = new LogonLDSInterfaceStub.TrailerPlateNo_type1();
			data.setTrailerPlateNo_type0(vehicleData.getTrailerPlateNo());
			wsVehicleData.setTrailerPlateNo(data);
		}
		if (vehicleData.getSapTrailerNo() != null) {
			LogonLDSInterfaceStub.SapTrailerNo_type1 data = new LogonLDSInterfaceStub.SapTrailerNo_type1();
			data.setSapTrailerNo_type0(vehicleData.getSapTrailerNo());
			wsVehicleData.setSapTrailerNo(data);
		}
		if (vehicleData.getExternalVehicleId() != null) {
			LogonLDSInterfaceStub.ExternalVehicleId_type1 data = new LogonLDSInterfaceStub.ExternalVehicleId_type1();
			data.setExternalVehicleId_type0(vehicleData.getExternalVehicleId());
			wsVehicleData.setExternalVehicleId(data);
		}
		if (vehicleData.getVehicleCompTareWeight() != null) {
			LogonLDSInterfaceStub.VehicleCompTareWeight_type1 data = new LogonLDSInterfaceStub.VehicleCompTareWeight_type1();
			data.setVehicleCompTareWeight_type0(vehicleData.getVehicleCompTareWeight());
			wsVehicleData.setVehicleCompTareWeight(data);
		}
		if (vehicleData.getVehicleMaxGrossWeight() != null) {
			LogonLDSInterfaceStub.VehicleMaxGrossWeight_type1 data = new LogonLDSInterfaceStub.VehicleMaxGrossWeight_type1();
			data.setVehicleMaxGrossWeight_type0(vehicleData.getVehicleMaxGrossWeight());
			wsVehicleData.setVehicleMaxGrossWeight(data);
		}
	}
	
	private static void setWsBusinessPartnerData(LogonLDSInterfaceStub.BusinessPartnerData wsBusinessPartnerData, BusinessPartnerData businessPartnerData) {
		
		if (businessPartnerData.getSapBpId() != null) {
			LogonLDSInterfaceStub.SapBpId_type1 data = new LogonLDSInterfaceStub.SapBpId_type1();
			data.setSapBpId_type0(businessPartnerData.getSapBpId());
			wsBusinessPartnerData.setSapBpId(data);
		}
		if (businessPartnerData.isIsOneTime() != null) {
			wsBusinessPartnerData.setIsOneTime(businessPartnerData.isIsOneTime());
		}
		if (businessPartnerData.getName1() != null) {
			LogonLDSInterfaceStub.Name1_type3 data = new LogonLDSInterfaceStub.Name1_type3();
			data.setName1_type2(businessPartnerData.getName1());
			wsBusinessPartnerData.setName1(data);
		}
		if (businessPartnerData.getName2() != null) {
			LogonLDSInterfaceStub.Name2_type3 data = new LogonLDSInterfaceStub.Name2_type3();
			data.setName2_type2(businessPartnerData.getName2());
			wsBusinessPartnerData.setName2(data);
		}
		if (businessPartnerData.getStreet() != null) {
			LogonLDSInterfaceStub.Street_type3 data = new LogonLDSInterfaceStub.Street_type3();
			data.setStreet_type2(businessPartnerData.getStreet());
			wsBusinessPartnerData.setStreet(data);
		}
		if (businessPartnerData.getZipCode() != null) {
			LogonLDSInterfaceStub.ZipCode_type3 data = new LogonLDSInterfaceStub.ZipCode_type3();
			data.setZipCode_type2(businessPartnerData.getZipCode());
			wsBusinessPartnerData.setZipCode(data);
		}
		if (businessPartnerData.getCity() != null) {
			LogonLDSInterfaceStub.City_type3 data = new LogonLDSInterfaceStub.City_type3();
			data.setCity_type2(businessPartnerData.getCity());
			wsBusinessPartnerData.setCity(data);
		}
		if (businessPartnerData.getCountry() != null) {
			LogonLDSInterfaceStub.Country_type3 data = new LogonLDSInterfaceStub.Country_type3();
			data.setCountry_type2(businessPartnerData.getCountry());
			wsBusinessPartnerData.setCountry(data);
		}
		if (businessPartnerData.getPhoneNumber() != null) {
			LogonLDSInterfaceStub.PhoneNumber_type3 data = new LogonLDSInterfaceStub.PhoneNumber_type3();
			data.setPhoneNumber_type2(businessPartnerData.getPhoneNumber());
			wsBusinessPartnerData.setPhoneNumber(data);
		}
		if (businessPartnerData.getVatRegistrationNumber() != null) {
			LogonLDSInterfaceStub.VatRegistrationNumber_type1 data = new LogonLDSInterfaceStub.VatRegistrationNumber_type1();
			data.setVatRegistrationNumber_type0(businessPartnerData.getVatRegistrationNumber());
			wsBusinessPartnerData.setVatRegistrationNumber(data);
		}
	}
	
	private static void setWsSchedulingData(LogonLDSInterfaceStub.SchedulingData wsSchedulingData, SchedulingData schedulingData) {
		
		if (schedulingData.isReleasedForLoading() != null) {
			wsSchedulingData.setReleasedForLoading(schedulingData.isReleasedForLoading());
		}
		if (schedulingData.getPlannedLoadingTime() != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(schedulingData.getPlannedLoadingTime());
			wsSchedulingData.setPlannedLoadingTime(cal);
		}
	}
	
	private static void setWsMaterialData(LogonLDSInterfaceStub.MaterialData wsMaterialData, MaterialData materialData) {
		
		if (materialData.getMaterialNo() != null) {
			LogonLDSInterfaceStub.MaterialNo_type1 data = new LogonLDSInterfaceStub.MaterialNo_type1();
			data.setMaterialNo_type0(materialData.getMaterialNo());
			wsMaterialData.setMaterialNo(data);
		}
		if (materialData.getContractItemNumber() != null) {
			LogonLDSInterfaceStub.ContractItemNumber_type1 data = new LogonLDSInterfaceStub.ContractItemNumber_type1();
			data.setContractItemNumber_type0(materialData.getContractItemNumber());
			wsMaterialData.setContractItemNumber(data);
		}
		if (materialData.getBrandSalesName() != null) {
			LogonLDSInterfaceStub.BrandSalesName_type1 data = new LogonLDSInterfaceStub.BrandSalesName_type1();
			data.setBrandSalesName_type0(materialData.getBrandSalesName());
			wsMaterialData.setBrandSalesName(data);
		}
		if (materialData.getSapMaterialDescription() != null) {
			LogonLDSInterfaceStub.SapMaterialDescription_type1 data = new LogonLDSInterfaceStub.SapMaterialDescription_type1();
			data.setSapMaterialDescription_type0(materialData.getSapMaterialDescription());
			wsMaterialData.setSapMaterialDescription(data);
		}
		if (materialData.getProdPlantNo() != null) {
			LogonLDSInterfaceStub.ProdPlantNo_type1 data = new LogonLDSInterfaceStub.ProdPlantNo_type1();
			data.setProdPlantNo_type0(materialData.getProdPlantNo());
			wsMaterialData.setProdPlantNo(data);
		}
		if (materialData.getMixDesignID() != null) {
			LogonLDSInterfaceStub.MixDesignID_type1 data = new LogonLDSInterfaceStub.MixDesignID_type1();
			data.setMixDesignID_type0(materialData.getMixDesignID());
			wsMaterialData.setMixDesignID(data);
		}
		if (materialData.getQuantity() != null) {
			LogonLDSInterfaceStub.Quantity_type1 data = new LogonLDSInterfaceStub.Quantity_type1();
			data.setQuantity_type0(materialData.getQuantity());
			wsMaterialData.setQuantity(data);
		}
		if (materialData.getQuantityPerDelivery() != null) {
			LogonLDSInterfaceStub.QuantityPerDelivery_type1 data = new LogonLDSInterfaceStub.QuantityPerDelivery_type1();
			data.setQuantityPerDelivery_type0(materialData.getQuantityPerDelivery());
			wsMaterialData.setQuantityPerDelivery(data);
		}
		if (materialData.getQuantityUom() != null) {
			LogonLDSInterfaceStub.QuantityUom_type1 data = new LogonLDSInterfaceStub.QuantityUom_type1();
			data.setQuantityUom_type0(materialData.getQuantityUom());
			wsMaterialData.setQuantityUom(data);
		}
		if (materialData.getBaseUom() != null) {
			LogonLDSInterfaceStub.BaseUom_type1 data = new LogonLDSInterfaceStub.BaseUom_type1();
			data.setBaseUom_type0(materialData.getBaseUom());
			wsMaterialData.setBaseUom(data);
		}
		if (materialData.getNumerator() != null) {
			LogonLDSInterfaceStub.Numerator_type1 data = new LogonLDSInterfaceStub.Numerator_type1();
			data.setNumerator_type0(materialData.getNumerator());
			wsMaterialData.setNumerator(data);
		}
		if (materialData.getDenominator() != null) {
			LogonLDSInterfaceStub.Denominator_type1 data = new LogonLDSInterfaceStub.Denominator_type1();
			data.setDenominator_type0(materialData.getDenominator());
			wsMaterialData.setDenominator(data);
		}
	}
	
	private static void setWsPriceData(LogonLDSInterfaceStub.PriceData wsPriceData, PriceData priceData) {
		
		if (priceData.getCurrency() != null) {
			LogonLDSInterfaceStub.Currency_type1 data = new LogonLDSInterfaceStub.Currency_type1();
			data.setCurrency_type0(priceData.getCurrency());
			wsPriceData.setCurrency(data);
		}
		if (priceData.getNetPrice() != null) {
			LogonLDSInterfaceStub.NetPrice_type1 data = new LogonLDSInterfaceStub.NetPrice_type1();
			data.setNetPrice_type0(priceData.getNetPrice());
			wsPriceData.setNetPrice(data);
		}
		if (priceData.getVatValue() != null) {
			LogonLDSInterfaceStub.VatValue_type1 data = new LogonLDSInterfaceStub.VatValue_type1();
			data.setVatValue_type0(priceData.getVatValue());
			wsPriceData.setVatValue(data);
		}
		if (priceData.getGrossPrice() != null) {
			LogonLDSInterfaceStub.GrossPrice_type1 data = new LogonLDSInterfaceStub.GrossPrice_type1();
			data.setGrossPrice_type0(priceData.getGrossPrice());
			wsPriceData.setGrossPrice(data);
		}
	}
	
	private static void setWsOrderItemData(LogonLDSInterfaceStub.OrderItemData wsOrderItemData, OrderItemData orderItemData) {
		
		if (orderItemData.getLogonOrderItemNumber() != null) {
			LogonLDSInterfaceStub.LogonOrderItemNumber_type1 data = new LogonLDSInterfaceStub.LogonOrderItemNumber_type1();
			data.setLogonOrderItemNumber_type0(orderItemData.getLogonOrderItemNumber());
			wsOrderItemData.setLogonOrderItemNumber(data);
		}
		if (orderItemData.getLdsOrderItemNumber() != null) {
			LogonLDSInterfaceStub.LdsOrderItemNumber_type1 data = new LogonLDSInterfaceStub.LdsOrderItemNumber_type1();
			data.setLdsOrderItemNumber_type0(orderItemData.getLdsOrderItemNumber());
			wsOrderItemData.setLdsOrderItemNumber(data);
		}
		if (orderItemData.getLoadId() != null) {
			LogonLDSInterfaceStub.LoadId_type1 data = new LogonLDSInterfaceStub.LoadId_type1();
			data.setLoadId_type0(orderItemData.getLoadId());
			wsOrderItemData.setLoadId(data);
		}
		if (orderItemData.getMaterial() != null) {
			LogonLDSInterfaceStub.MaterialData wsMaterialData = new LogonLDSInterfaceStub.MaterialData();
			setWsMaterialData(wsMaterialData, orderItemData.getMaterial());
			wsOrderItemData.setMaterial(wsMaterialData);
		}
		if (orderItemData.getLoadingpointNo() != null) {
			LogonLDSInterfaceStub.LoadingpointNo_type3 data = new LogonLDSInterfaceStub.LoadingpointNo_type3();
			data.setLoadingpointNo_type2(orderItemData.getLoadingpointNo());
			wsOrderItemData.setLoadingpointNo(data);
		}
		if (orderItemData.getLoadingpointName() != null) {
			LogonLDSInterfaceStub.LoadingpointName_type1 data = new LogonLDSInterfaceStub.LoadingpointName_type1();
			String loadingPointName = orderItemData.getLoadingpointName();
			data.setLoadingpointName_type0(loadingPointName);
			wsOrderItemData.setLoadingpointName(data);
		}
		if (orderItemData.getPossibleMaterial() != null) {
			LogonLDSInterfaceStub.PossibleMaterial_type0[] list = new LogonLDSInterfaceStub.PossibleMaterial_type0[orderItemData.getPossibleMaterial().size()];
			for (int i=0; i<orderItemData.getPossibleMaterial().size(); i++) {
				PossibleMaterial possibleMaterial = orderItemData.getPossibleMaterial().get(i);
				LogonLDSInterfaceStub.PossibleMaterial_type0 wsPossibleMaterial = new LogonLDSInterfaceStub.PossibleMaterial_type0();
				if (possibleMaterial.getMaterialNo() != null) {
					LogonLDSInterfaceStub.MaterialNo_type3 data = new LogonLDSInterfaceStub.MaterialNo_type3();
					data.setMaterialNo_type2(possibleMaterial.getMaterialNo());
					wsPossibleMaterial.setMaterialNo(data);
				}
				if (possibleMaterial.getProdPlantNo() != null) {
					LogonLDSInterfaceStub.ProdPlantNo_type3 data = new LogonLDSInterfaceStub.ProdPlantNo_type3();
					data.setProdPlantNo_type2(possibleMaterial.getProdPlantNo());
					wsPossibleMaterial.setProdPlantNo(data);
				}
				list[i] = wsPossibleMaterial;
			}
			wsOrderItemData.setPossibleMaterial(list);
		}
		if (orderItemData.getPriceData() != null) {
			LogonLDSInterfaceStub.PriceData wsPriceData = new LogonLDSInterfaceStub.PriceData();
			setWsPriceData(wsPriceData, orderItemData.getPriceData());
			wsOrderItemData.setPriceData(wsPriceData);
		}
	}
	
	private static void setWsServiceMaterialData(LogonLDSInterfaceStub.ServiceMaterialData wsServiceMaterialData, ServiceMaterialData serviceMaterialData) {
		
		if (serviceMaterialData.getMaterial() != null) {
			LogonLDSInterfaceStub.MaterialData wsMaterialData = new LogonLDSInterfaceStub.MaterialData();
			setWsMaterialData(wsMaterialData, serviceMaterialData.getMaterial());
			wsServiceMaterialData.setMaterial(wsMaterialData);
		}
		if (serviceMaterialData.getPriceData() != null) {
			LogonLDSInterfaceStub.PriceData wsPriceData = new LogonLDSInterfaceStub.PriceData();
			setWsPriceData(wsPriceData, serviceMaterialData.getPriceData());
			wsServiceMaterialData.setPriceData(wsPriceData);
		}		
	}
	
	private static void setWsOrderData(LogonLDSInterfaceStub.OrderData wsOrderData, OrderData orderData) {
		
		if (orderData.getLogonCustomerOrderNo() != null) {
			LogonLDSInterfaceStub.LogonCustomerOrderNo_type1 data = new LogonLDSInterfaceStub.LogonCustomerOrderNo_type1();
			data.setLogonCustomerOrderNo_type0(orderData.getLogonCustomerOrderNo());
			wsOrderData.setLogonCustomerOrderNo(data);
		}
		if (orderData.getLdsCustomerOrderNo() != null) {
			LogonLDSInterfaceStub.LdsCustomerOrderNo_type1 data = new LogonLDSInterfaceStub.LdsCustomerOrderNo_type1();
			data.setLdsCustomerOrderNo_type0(orderData.getLdsCustomerOrderNo());
			wsOrderData.setLdsCustomerOrderNo(data);
		}
		if (orderData.getLogonOrderNo() != null) {
			LogonLDSInterfaceStub.LogonOrderNo_type1 data = new LogonLDSInterfaceStub.LogonOrderNo_type1();
			data.setLogonOrderNo_type0(orderData.getLogonOrderNo());
			wsOrderData.setLogonOrderNo(data);
		}
		if (orderData.getSequentialLogonOrderNo() != null) {
			LogonLDSInterfaceStub.SequentialLogonOrderNo_type1 data = new LogonLDSInterfaceStub.SequentialLogonOrderNo_type1();
			data.setSequentialLogonOrderNo_type0(orderData.getSequentialLogonOrderNo());
			wsOrderData.setSequentialLogonOrderNo(data);
		}
		if (orderData.getContractType() != null) {
			LogonLDSInterfaceStub.ContractType_type1 data = new LogonLDSInterfaceStub.ContractType_type1();
			data.setContractType_type0(orderData.getContractType());
			wsOrderData.setContractType(data);
		}
		if (orderData.getContractNumber() != null) {
			LogonLDSInterfaceStub.ContractNumber_type1 data = new LogonLDSInterfaceStub.ContractNumber_type1();
			data.setContractNumber_type0(orderData.getContractNumber());
			wsOrderData.setContractNumber(data);
		}
		if (orderData.getIncoterms() != null) {
			LogonLDSInterfaceStub.Incoterms_type1 data = new LogonLDSInterfaceStub.Incoterms_type1();
			data.setIncoterms_type0(orderData.getIncoterms());
			wsOrderData.setIncoterms(data);
		}
		if (orderData.getCustorderCustrefNo() != null) {
			LogonLDSInterfaceStub.CustorderCustrefNo_type1 data = new LogonLDSInterfaceStub.CustorderCustrefNo_type1();
			data.setCustorderCustrefNo_type0(orderData.getCustorderCustrefNo());
			wsOrderData.setCustorderCustrefNo(data);
		}
		if (orderData.getCommercialCarrier() != null) {
			LogonLDSInterfaceStub.VendorData wsVendorData = new LogonLDSInterfaceStub.VendorData();
			setWsVendorData(wsVendorData, orderData.getCommercialCarrier());
			wsOrderData.setCommercialCarrier(wsVendorData);
		}
		if (orderData.getExecutingCarrier() != null) {
			LogonLDSInterfaceStub.VendorData wsVendorData = new LogonLDSInterfaceStub.VendorData();
			setWsVendorData(wsVendorData, orderData.getExecutingCarrier());
			wsOrderData.setExecutingCarrier(wsVendorData);
		}
		if (orderData.getSoldTo() != null) {
			LogonLDSInterfaceStub.BusinessPartnerData wsBusinessPartnerData = new LogonLDSInterfaceStub.BusinessPartnerData();
			setWsBusinessPartnerData(wsBusinessPartnerData, orderData.getSoldTo());
			wsOrderData.setSoldTo(wsBusinessPartnerData);
		}
		if (orderData.getShipTo() != null) {
			LogonLDSInterfaceStub.BusinessPartnerData wsBusinessPartnerData = new LogonLDSInterfaceStub.BusinessPartnerData();
			setWsBusinessPartnerData(wsBusinessPartnerData, orderData.getShipTo());
			wsOrderData.setShipTo(wsBusinessPartnerData);
		}
		if (orderData.getBillTo() != null) {
			LogonLDSInterfaceStub.BusinessPartnerData wsBusinessPartnerData = new LogonLDSInterfaceStub.BusinessPartnerData();
			setWsBusinessPartnerData(wsBusinessPartnerData, orderData.getBillTo());
			wsOrderData.setBillTo(wsBusinessPartnerData);
		}
		if (orderData.getPayer() != null) {
			LogonLDSInterfaceStub.BusinessPartnerData wsBusinessPartnerData = new LogonLDSInterfaceStub.BusinessPartnerData();
			setWsBusinessPartnerData(wsBusinessPartnerData, orderData.getPayer());
			wsOrderData.setPayer(wsBusinessPartnerData);
		}
		if (orderData.getSupplyPlantNo() != null) {
			LogonLDSInterfaceStub.SupplyPlantNo_type3 data = new LogonLDSInterfaceStub.SupplyPlantNo_type3();
			data.setSupplyPlantNo_type2(orderData.getSupplyPlantNo());
			wsOrderData.setSupplyPlantNo(data);
		}
		if (orderData.getShippingpointNo() != null) {
			LogonLDSInterfaceStub.ShippingpointNo_type3 data = new LogonLDSInterfaceStub.ShippingpointNo_type3();
			data.setShippingpointNo_type2(orderData.getShippingpointNo());
			wsOrderData.setShippingpointNo(data);
		}
		if (orderData.getDeliveryType() != null) {
			LogonLDSInterfaceStub.DeliveryType_type1 data = new LogonLDSInterfaceStub.DeliveryType_type1();
			data.setDeliveryType_type0(orderData.getDeliveryType());
			wsOrderData.setDeliveryType(data);
		}
		if (orderData.getNoteDelivery() != null) {
			LogonLDSInterfaceStub.NoteDelivery_type1 data = new LogonLDSInterfaceStub.NoteDelivery_type1();
			data.setNoteDelivery_type0(orderData.getNoteDelivery());
			wsOrderData.setNoteDelivery(data);
		}
		if (orderData.getNoteScheduling() != null) {
			LogonLDSInterfaceStub.NoteScheduling_type1 data = new LogonLDSInterfaceStub.NoteScheduling_type1();
			data.setNoteScheduling_type0(orderData.getNoteScheduling());
			wsOrderData.setNoteScheduling(data);
		}
		if (orderData.getDriverComment() != null) {
			LogonLDSInterfaceStub.DriverComment_type1 data = new LogonLDSInterfaceStub.DriverComment_type1();
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
			LogonLDSInterfaceStub.DeliveryFreqDelayBtwnDeliveries_type1 data = new LogonLDSInterfaceStub.DeliveryFreqDelayBtwnDeliveries_type1();
			data.setDeliveryFreqDelayBtwnDeliveries_type0(orderData.getDeliveryFreqDelayBtwnDeliveries());
			wsOrderData.setDeliveryFreqDelayBtwnDeliveries(data);
		}
		if (orderData.getDeliveryFreqDelayBtwnDeliveriesUom() != null) {
			LogonLDSInterfaceStub.DeliveryFreqDelayBtwnDeliveriesUom_type1 data = new LogonLDSInterfaceStub.DeliveryFreqDelayBtwnDeliveriesUom_type1();
			data.setDeliveryFreqDelayBtwnDeliveriesUom_type0(orderData.getDeliveryFreqDelayBtwnDeliveriesUom());
			wsOrderData.setDeliveryFreqDelayBtwnDeliveriesUom(data);
		}
		if (orderData.getDeliveryFreqQtyPerHour() != null) {
			LogonLDSInterfaceStub.DeliveryFreqQtyPerHour_type1 data = new LogonLDSInterfaceStub.DeliveryFreqQtyPerHour_type1();
			data.setDeliveryFreqQtyPerHour_type0(orderData.getDeliveryFreqQtyPerHour());
			wsOrderData.setDeliveryFreqQtyPerHour(data);
		}
		if (orderData.getDeliveryFreqQtyPerHourUom() != null) {
			LogonLDSInterfaceStub.DeliveryFreqQtyPerHourUom_type1 data = new LogonLDSInterfaceStub.DeliveryFreqQtyPerHourUom_type1();
			data.setDeliveryFreqQtyPerHourUom_type0(orderData.getDeliveryFreqQtyPerHourUom());
			wsOrderData.setDeliveryFreqQtyPerHourUom(data);
		}
		if (orderData.getDeliveryTimeTolerance() != null) {
			LogonLDSInterfaceStub.DeliveryTimeTolerance_type1 data = new LogonLDSInterfaceStub.DeliveryTimeTolerance_type1();
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
			LogonLDSInterfaceStub.ChromReduce_type1 data = new LogonLDSInterfaceStub.ChromReduce_type1();
			data.setChromReduce_type0(orderData.getChromReduce());
			wsOrderData.setChromReduce(data);
		}
		if (orderData.getOrderValidationCode() != null) {
			LogonLDSInterfaceStub.OrderValidationCode_type1 data = new LogonLDSInterfaceStub.OrderValidationCode_type1();
			data.setOrderValidationCode_type0(orderData.getOrderValidationCode());
			wsOrderData.setOrderValidationCode(data);
		}
		if (orderData.getOrderItem() != null) {
			LogonLDSInterfaceStub.OrderItemData[] data = new LogonLDSInterfaceStub.OrderItemData[orderData.getOrderItem().size()];
			for (int i=0; i<orderData.getOrderItem().size(); i++) {
				OrderItemData orderItemData = orderData.getOrderItem().get(i);
				LogonLDSInterfaceStub.OrderItemData wsOrderItemData = new LogonLDSInterfaceStub.OrderItemData();
				setWsOrderItemData(wsOrderItemData, orderItemData);
				data[i] = wsOrderItemData;
			}
			wsOrderData.setOrderItem(data);
		}
		if (orderData.getServiceMaterial() != null) {
			LogonLDSInterfaceStub.ServiceMaterialData[] data = new LogonLDSInterfaceStub.ServiceMaterialData[orderData.getServiceMaterial().size()];
			for (int i=0; i<orderData.getServiceMaterial().size(); i++) {
				ServiceMaterialData serviceMaterialData = orderData.getServiceMaterial().get(i);
				LogonLDSInterfaceStub.ServiceMaterialData wsServiceMaterialData = new LogonLDSInterfaceStub.ServiceMaterialData();
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
			LogonLDSInterfaceStub.PriceData wsPriceData = new LogonLDSInterfaceStub.PriceData();
			setWsPriceData(wsPriceData, orderData.getOrderPriceData());
			wsOrderData.setOrderPriceData(wsPriceData);
		}
	}
	
	private static void setWsLoadOrderItemData(LogonLDSInterfaceStub.LoadOrderItemData wsLoadOrderItemData, LoadOrderItemData loadOrderItemData) {
		
		if (loadOrderItemData.getLoadOrderItemId() != null) {
			LogonLDSInterfaceStub.LoadOrderItemId_type1 data = new LogonLDSInterfaceStub.LoadOrderItemId_type1();
			data.setLoadOrderItemId_type0(loadOrderItemData.getLoadOrderItemId());
			wsLoadOrderItemData.setLoadOrderItemId(data);
		}
		if (loadOrderItemData.getMaterial() != null) {
			LogonLDSInterfaceStub.MaterialData wsMaterialData = new LogonLDSInterfaceStub.MaterialData();
			setWsMaterialData(wsMaterialData, loadOrderItemData.getMaterial());
			wsLoadOrderItemData.setMaterial(wsMaterialData);
		}
		if (loadOrderItemData.getLoadingpointNo() != null) {
			LogonLDSInterfaceStub.LoadingpointNo_type1 data = new LogonLDSInterfaceStub.LoadingpointNo_type1();
			data.setLoadingpointNo_type0(loadOrderItemData.getLoadingpointNo());
			wsLoadOrderItemData.setLoadingpointNo(data);
		}
		if (loadOrderItemData.isExactDeliveryQuantity() != null) {
			wsLoadOrderItemData.setExactDeliveryQuantity(loadOrderItemData.isExactDeliveryQuantity());
		}
		if (loadOrderItemData.getChamberNo() != null) {
			LogonLDSInterfaceStub.ChamberNo_type1 data = new LogonLDSInterfaceStub.ChamberNo_type1();
			data.setChamberNo_type0(loadOrderItemData.getChamberNo());
			wsLoadOrderItemData.setChamberNo(data);
		}
	}
	
	private static void setWsLoadOrderData(LogonLDSInterfaceStub.LoadOrderData wsLoadOrderData, LoadOrderData loadOrderData) {

		if (loadOrderData.getLoadOrderId() != null) {
			LogonLDSInterfaceStub.LoadOrderId_type1 data = new LogonLDSInterfaceStub.LoadOrderId_type1();
			data.setLoadOrderId_type0(loadOrderData.getLoadOrderId());
			wsLoadOrderData.setLoadOrderId(data);
		}
		if (loadOrderData.getSupplyPlantNo() != null) {
			LogonLDSInterfaceStub.SupplyPlantNo_type1 data = new LogonLDSInterfaceStub.SupplyPlantNo_type1();
			data.setSupplyPlantNo_type0(loadOrderData.getSupplyPlantNo());
			wsLoadOrderData.setSupplyPlantNo(data);
		}
		if (loadOrderData.getShippingpointNo() != null) {
			LogonLDSInterfaceStub.ShippingpointNo_type1 data = new LogonLDSInterfaceStub.ShippingpointNo_type1();
			data.setShippingpointNo_type0(loadOrderData.getShippingpointNo());
			wsLoadOrderData.setShippingpointNo(data);
		}
		if (loadOrderData.getLoadOrderItem() != null) {
			LogonLDSInterfaceStub.LoadOrderItemData[] data = new LogonLDSInterfaceStub.LoadOrderItemData[loadOrderData.getLoadOrderItem().size()];
			for (int i=0; i<loadOrderData.getLoadOrderItem().size(); i++) {
				LoadOrderItemData loadOrderItemData = loadOrderData.getLoadOrderItem().get(i);
				LogonLDSInterfaceStub.LoadOrderItemData wsLoadOrderItemData = new LogonLDSInterfaceStub.LoadOrderItemData();
				setWsLoadOrderItemData(wsLoadOrderItemData, loadOrderItemData);
				data[i] = wsLoadOrderItemData;
			}
			wsLoadOrderData.setLoadOrderItem(data);
		}
		if (loadOrderData.getScheduling() != null) {
			LogonLDSInterfaceStub.SchedulingData wsSchedulingData = new LogonLDSInterfaceStub.SchedulingData();
			setWsSchedulingData(wsSchedulingData, loadOrderData.getScheduling());
			wsLoadOrderData.setScheduling(wsSchedulingData);
		}
		if (loadOrderData.getVehicle() != null) {
			LogonLDSInterfaceStub.VehicleData wsVehicleData = new LogonLDSInterfaceStub.VehicleData();
			setWsVehicleData(wsVehicleData, loadOrderData.getVehicle());
			wsLoadOrderData.setVehicle(wsVehicleData);
		}
		if (loadOrderData.getOrder() != null) {
			LogonLDSInterfaceStub.OrderData[] data = new LogonLDSInterfaceStub.OrderData[loadOrderData.getOrder().size()];
			for (int i=0; i<loadOrderData.getOrder().size(); i++) {
				OrderData orderData = loadOrderData.getOrder().get(i);
				LogonLDSInterfaceStub.OrderData wsOrderData = new LogonLDSInterfaceStub.OrderData();
				setWsOrderData(wsOrderData, orderData);
				data[i] = wsOrderData;
			}
			wsLoadOrderData.setOrder(data);
		}
		if (loadOrderData.getNoteLds() != null) {
			LogonLDSInterfaceStub.NoteLds_type1 data = new LogonLDSInterfaceStub.NoteLds_type1();
			data.setNoteLds_type0(loadOrderData.getNoteLds());
			wsLoadOrderData.setNoteLds(data);
		}
	}
	
	private PdaLoginRequest createPdaLoginRequest(PdaLoginDTO request) {
		
		final PdaLoginRequest wsRequest = new PdaLoginRequest();
		wsRequest.setHeader(getHeader());
		
		if (request.getLocationData() != null) {
			LogonLDSInterfaceStub.LocationData wsLocationData = new LogonLDSInterfaceStub.LocationData();
			setWsLocationData(wsLocationData, request.getLocationData());
			wsRequest.setLocation(wsLocationData);
		}
		
		if (request.getVehicleData() != null) {
			LogonLDSInterfaceStub.VehicleData wsVehicleData = new LogonLDSInterfaceStub.VehicleData();
			setWsVehicleData(wsVehicleData, request.getVehicleData());
			wsRequest.setVehicle(wsVehicleData);
		}
		
		return wsRequest;
	}
	
	private LoadOrderNotificationRequest createLoadOrderNotificationRequest(LoadOrderNotificationDTO request) {
		
		final LoadOrderNotificationRequest wsRequest = new LoadOrderNotificationRequest();
		wsRequest.setHeader(getHeader());
		
		if (request.getMessageFunction() != null) {
			if (LogonLDSInterfaceStub.MessageFunction_type1._value1.equals(request.getMessageFunction().value())) {
				wsRequest.setMessageFunction(LogonLDSInterfaceStub.MessageFunction_type1.value1);
			}
			else if (LogonLDSInterfaceStub.MessageFunction_type1._value2.equals(request.getMessageFunction().value())) {
				wsRequest.setMessageFunction(LogonLDSInterfaceStub.MessageFunction_type1.value2);
			}
		}
		if (request.getLoadOrder() != null) {
			LogonLDSInterfaceStub.LoadOrderData wsLoadOrderData = new LogonLDSInterfaceStub.LoadOrderData();
			setWsLoadOrderData(wsLoadOrderData, request.getLoadOrder());
			wsRequest.setLoadOrder(wsLoadOrderData);
		}
		
		return wsRequest;
	}
	
	private DeleteLoadOrderRequest createDeleteLoadOrderRequest(DeleteLoadOrderDTO request) {
		final DeleteLoadOrderRequest wsRequest = new DeleteLoadOrderRequest();
		wsRequest.setHeader(getHeader());
		
		if (request.getLoadOrderId() != null) {
			LogonLDSInterfaceStub.LoadOrderId_type3 data = new LogonLDSInterfaceStub.LoadOrderId_type3();
			data.setLoadOrderId_type2(request.getLoadOrderId());
			wsRequest.setLoadOrderId(data);
		}
		
		return wsRequest;
	}
	
	public String getXml(PdaLoginDTO request) {
		// [[
		String result = null;
		Object[][] params = { { "request", request } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getXml", params);
		}
		// ]]
		try {
			final PdaLoginRequest wsRequest = createPdaLoginRequest(request);
			return result = WebserviceClientUtil.getSerializedString(wsRequest, "pdaLoginRequest");
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}
	
	public String getXml(LoadOrderNotificationDTO request) {
		// [[
		String result = null;
		Object[][] params = { { "request", request } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getXml", params);
		}
		// ]]
		try {
			final LoadOrderNotificationRequest wsRequest =
					createLoadOrderNotificationRequest(request);
			return result = WebserviceClientUtil.getSerializedString(wsRequest, "loadOrderNotificationRequest");
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}
	
	public String getXml(DeleteLoadOrderDTO request) {
		// [[
		String result = null;
		Object[][] params = { { "request", request } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getXml", params);
		}
		// ]]
		try {
			final DeleteLoadOrderRequest wsRequest = createDeleteLoadOrderRequest(request);
			return result = WebserviceClientUtil.getSerializedString(wsRequest, "deleteLoadOrderRequest");
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}
	
	private static void removeTypeAttribute(ClientConfig clientConfig) {
		ConfigurationContext configurationContext = clientConfig.getConfigurationContext();
		// By default, AXIS2 will generate an 'xsi:type' attribute for the
		// 'Request' element, which is wrong. See also:
		// https://issues.apache.org/jira/browse/AXIS-2832.
		// The handler below removes exactly this attribute. If the WSDL
		// changes, this handler might need to be changed as well.
		// Before:
		// <soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">
		// <soapenv:Body>
		// <ns3:OrderNotification xmlns:ns3="http://fra-lille-s52/AphroditeWS/Logon">
		// <Request xsi:type="Request_type0"
		// xmlns="http://logon.crh.com/LogonOrderServices/notifications"
		// xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
		// After:
		// <soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">
		// <soapenv:Body>
		// <ns3:OrderNotification xmlns:ns3="http://fra-lille-s52/AphroditeWS/Logon">
		// <Request xmlns="http://logon.crh.com/LogonOrderServices/notifications"
		// xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

		final AxisConfiguration axisConfiguration = configurationContext.getAxisConfiguration();
		final List<Phase> phases = axisConfiguration.getOutFlowPhases();
		for (Phase p : phases) {
			if (p.getPhaseName().equals("MessageOut")) {
				p.addHandler(new AbstractHandler() {
					public InvocationResponse invoke(MessageContext mc) throws AxisFault {
						if ((mc != null) && (mc.getEnvelope() != null) && (mc.getEnvelope().getBody() != null)) {
							OMElement orderNotificationElement = mc.getEnvelope().getBody().getFirstElement();
							if (orderNotificationElement != null) {
								OMElement requestElement = orderNotificationElement.getFirstElement();
								OMAttribute xsiTypeAttr = requestElement.getAttribute(new QName("http://www.w3.org/2001/XMLSchema-instance", "type"));
								if (xsiTypeAttr != null) {
									requestElement.removeAttribute(xsiTypeAttr);
								}
							}
						}
						return InvocationResponse.CONTINUE;
					}
				});
			}
		}
	}
	
	public String sendPdaLoginForTesting(XMLStreamReader xmlStreamReader, String configInfix, String serverName) {
		// [[
		String result = null;
		Object[][] params = { { "xmlStreamReader", xmlStreamReader },
				{ "configInfix", configInfix },
				{ "serverName", serverName } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sendPdaLoginForTesting", params);
		}
		// ]]
		try {
			try {
				final PdaLoginResponse wsResponse = sendPdaLogin(
						PdaLoginRequest.Factory.parse(xmlStreamReader), configInfix, serverName);

				return result = WebserviceClientUtil.getSerializedString(wsResponse, "pdaLoginResponse");
			}
			catch (Exception e) {
				if (e instanceof LogonException) {
					throw (LogonException) e;
				}
				throw new LogonException(e, Reason.RES_ERROR_WSCLIENT);
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}
	
	public String sendLoadOrderNotificationForTesting(XMLStreamReader xmlStreamReader, String configInfix, String serverName) {
		// [[
		String result = null;
		Object[][] params = { { "xmlStreamReader", xmlStreamReader },
				{ "configInfix", configInfix },
				{ "serverName", serverName } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sendLoadOrderNotificationForTesting", params);
		}
		// ]]
		try {
			
			try {
				LoadOrderNotificationRequest wsRequest = LoadOrderNotificationRequest.Factory.parse(xmlStreamReader);
				final LoadOrderNotificationResponse wsResponse = sendLoadOrderNotification(
						wsRequest, configInfix, serverName);

				return result = WebserviceClientUtil.getSerializedString(wsResponse, "loadOrderNotificationResponse");
			}
			catch (Exception e) {
				if (e instanceof LogonException) {
					throw (LogonException) e;
				}
				throw new LogonException(e, Reason.RES_ERROR_WSCLIENT);
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}
	
	public String sendDeleteLoadOrderForTesting(XMLStreamReader xmlStreamReader, String configInfix, String serverName) {
		// [[
		String result = null;
		Object[][] params = { { "xmlStreamReader", xmlStreamReader },
				{ "configInfix", configInfix },
				{ "serverName", serverName } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sendDeleteLoadOrderForTesting", params);
		}
		// ]]
		try {
			try {
				final DeleteLoadOrderResponse wsResponse = sendDeleteLoadOrder(
						configBean, DeleteLoadOrderRequest.Factory.parse(xmlStreamReader), configInfix, serverName);

				return result = WebserviceClientUtil.getSerializedString(wsResponse, "deleteLoadOrderResponse");
			}
			catch (Exception e) {
				if (e instanceof LogonException) {
					throw (LogonException) e;
				}
				throw new LogonException(e, Reason.RES_ERROR_WSCLIENT);
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}
	
	public PdaLoginResponse sendPdaLogin(PdaLoginDTO request, String configInfix, String serverName) {
		// [[
		PdaLoginResponse result = null;
		Object[][] params = { { "request", request },
				{ "configInfix", configInfix },
				{ "serverName", serverName } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sendPdaLogin", params);
		}
		// ]]
		try {

			try {
				return result = sendPdaLogin(createPdaLoginRequest(request), configInfix, serverName);
			}
			catch (Exception e) {
				if (e instanceof LogonException) {
					throw (LogonException) e;
				}
				throw new LogonException(e, Reason.RES_ERROR_WSCLIENT);
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}
	
	public LoadOrderNotificationResponse sendLoadOrderNotification(LoadOrderNotificationDTO request, String configInfix, String serverName) {
		// [[
		LoadOrderNotificationResponse result = null;
		Object[][] params = { { "request", request },
				{ "configInfix", configInfix },
				{ "serverName", serverName } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sendLoadOrderNotification", params);
		}
		// ]]
		try {
			try {
				LoadOrderNotificationRequest loadOrderNotificationRequest = createLoadOrderNotificationRequest(request);
				return result = sendLoadOrderNotification(loadOrderNotificationRequest, configInfix, serverName);
			}
			catch (Exception e) {
				if (e instanceof LogonException) {
					throw (LogonException) e;
				}
				throw new LogonException(e, Reason.RES_ERROR_WSCLIENT);
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}
	
	public DeleteLoadOrderResponse sendDeleteLoadOrder(DeleteLoadOrderDTO request, String configInfix, String serverName) {
		// [[
		DeleteLoadOrderResponse result = null;
		Object[][] params = { { "request", request },
				{ "configInfix", configInfix },
				{ "serverName", serverName } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sendDeleteLoadOrder", params);
		}
		// ]]
		try {
			try {
				return result = sendDeleteLoadOrder(configBean, createDeleteLoadOrderRequest(request), configInfix, serverName);
			}
			catch (Exception e) {
				if (e instanceof LogonException) {
					throw (LogonException) e;
				}
				throw new LogonException(e, Reason.RES_ERROR_WSCLIENT);
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}
	
	private PdaLoginResponse sendPdaLogin(PdaLoginRequest wsRequest, String configInfix, String serverName) throws AxisFault {
		// Set up the enhanced client
		final ClientConfig clientConfig = new ClientConfig(configBean, configInfix, serverName);
		removeTypeAttribute(clientConfig);
		final EnhancedLogonLDSInterfaceStub stub = new EnhancedLogonLDSInterfaceStub(clientConfig);
		PdaLoginResponse result = stub.pdaLogin(wsRequest);
		return result;
	}

	private LoadOrderNotificationResponse sendLoadOrderNotification(LoadOrderNotificationRequest wsRequest, String configInfix, String serverName) throws AxisFault {
		// Set up the enhanced client
		final ClientConfig clientConfig = new ClientConfig(configBean, configInfix, serverName);
		removeTypeAttribute(clientConfig);
		final EnhancedLogonLDSInterfaceStub stub = new EnhancedLogonLDSInterfaceStub(clientConfig);
		return stub.loadOrderNotification(wsRequest);
	}

	private static DeleteLoadOrderResponse sendDeleteLoadOrder(ConfigBean configBean, DeleteLoadOrderRequest wsRequest, String configInfix, String serverName) throws AxisFault {
		// Set up the enhanced client
		final ClientConfig clientConfig = new ClientConfig(configBean, configInfix, serverName);
		removeTypeAttribute(clientConfig);
		final EnhancedLogonLDSInterfaceStub stub = new EnhancedLogonLDSInterfaceStub(clientConfig);
		return stub.deleteLoadOrder(wsRequest);
	}
}
