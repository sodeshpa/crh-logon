/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.holcim.logon.eventbeans.in.logon_lds_interface.CreatedOrderData;
import com.holcim.logon.eventbeans.in.logon_oc_interface.CreateRoadDirectCustomerOrderResponse;
import com.holcim.logon.eventbeans.in.logon_oc_interface.VendorData;

public class CreateCustorderFromMasterDefaultDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String custorderNumber;
	private final List<CreatedOrderData> createdOrderData;

	/**
	 *
	 */
	public CreateCustorderFromMasterDefaultDTO(String custorderNumber) {
		this.custorderNumber = custorderNumber;
		this.createdOrderData = new ArrayList<CreatedOrderData>();
	}

	/**
	 * @return the custorderNumber
	 */
	public String getCustorderNumber() {
		return custorderNumber;
	}

	/**
	 * @return the loadOrderData
	 */
	public List<CreatedOrderData> getCreatedOrderData() {
		return createdOrderData;
	}

	/**
	 * Converts the LDS {@link com.holcim.logon.eventbeans.in.logon_lds_interface.CreateCustomerOrderBasedOnMasterDataDefaultResponse}
	 * data to an {@link com.holcim.logon.eventbeans.in.logon_oc_interface.CreateRoadDirectCustomerOrderResponse}
	 * @return convertedOrders
	 */
	public List<CreateRoadDirectCustomerOrderResponse.Order> getCreatedOcOrders() {

		List<CreatedOrderData> createdOrderData = this.createdOrderData;
		Function<CreatedOrderData, List<CreateRoadDirectCustomerOrderResponse.Order>> function = createdOrderDataListItem -> {
			List<CreateRoadDirectCustomerOrderResponse.Order> result = new ArrayList<>();
			createdOrderDataListItem.getOrder().forEach(createdOrderDataListItemOrder -> {
				CreateRoadDirectCustomerOrderResponse.Order convertedOrderDataListItem =
						new CreateRoadDirectCustomerOrderResponse.Order();
				if ( createdOrderDataListItemOrder.getCommercialCarrier() != null) {
					VendorData commCarrierVendorData = new VendorData();
					commCarrierVendorData.setSapVendorId(createdOrderDataListItemOrder.getCommercialCarrier().getSapVendorId());
					commCarrierVendorData.setCity(createdOrderDataListItemOrder.getCommercialCarrier().getCity());
					commCarrierVendorData.setCountry(createdOrderDataListItemOrder.getCommercialCarrier().getCountry());
					commCarrierVendorData.setName2(createdOrderDataListItemOrder.getCommercialCarrier().getName2());
					commCarrierVendorData.setName1(createdOrderDataListItemOrder.getCommercialCarrier().getName1());
					commCarrierVendorData.setPhoneNumber(createdOrderDataListItemOrder.getCommercialCarrier().getPhoneNumber());
					commCarrierVendorData.setStreet(createdOrderDataListItemOrder.getCommercialCarrier().getStreet());
					commCarrierVendorData.setTaxNumber(createdOrderDataListItemOrder.getCommercialCarrier().getTaxNumber());
					commCarrierVendorData.setZipCode(createdOrderDataListItemOrder.getCommercialCarrier().getZipCode());
					convertedOrderDataListItem.setCommercialCarrier(commCarrierVendorData);
				}

				if (createdOrderDataListItemOrder.getExecutingCarrier() != null ) {
					VendorData execCarrierVendorData = new VendorData();
					execCarrierVendorData.setSapVendorId(createdOrderDataListItemOrder.getExecutingCarrier().getSapVendorId());
					execCarrierVendorData.setCity(createdOrderDataListItemOrder.getExecutingCarrier().getCity());
					execCarrierVendorData.setCountry(createdOrderDataListItemOrder.getExecutingCarrier().getCountry());
					execCarrierVendorData.setName2(createdOrderDataListItemOrder.getExecutingCarrier().getName2());
					execCarrierVendorData.setName1(createdOrderDataListItemOrder.getExecutingCarrier().getName1());
					execCarrierVendorData.setPhoneNumber(createdOrderDataListItemOrder.getExecutingCarrier().getPhoneNumber());
					execCarrierVendorData.setStreet(createdOrderDataListItemOrder.getExecutingCarrier().getStreet());
					execCarrierVendorData.setTaxNumber(createdOrderDataListItemOrder.getExecutingCarrier().getTaxNumber());
					execCarrierVendorData.setZipCode(createdOrderDataListItemOrder.getExecutingCarrier().getZipCode());
					convertedOrderDataListItem.setExecutingCarrier(execCarrierVendorData);
				}

				convertedOrderDataListItem.setPoolName(createdOrderDataListItemOrder.getPoolName());
				convertedOrderDataListItem.setLogonOrderNo(createdOrderDataListItemOrder.getLogonOrderNo());
				convertedOrderDataListItem.setOrderStatusCd(createdOrderDataListItemOrder.getOrderStatusCd());
				convertedOrderDataListItem.setWarningMultipleDispocard(createdOrderDataListItem.isWarningMultipleDispocard());

				result.add(convertedOrderDataListItem);
			});
			return result;
		};

		return createdOrderData.stream().map(function).flatMap(List::stream).collect(Collectors.toList());
	}
}
