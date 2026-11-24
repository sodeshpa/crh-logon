
package com.crh.logon.business.services.eventbeans.in.logon_lds_interface;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.views.RepTransplusEntity;
import com.crh.logon.utils.LogonException;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.logon_lds_interface.BusinessPartnerData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.GetOrdersForCarriersRequest;
import com.holcim.logon.eventbeans.in.logon_lds_interface.GetOrdersForCarriersResponse;
import com.holcim.logon.eventbeans.in.logon_lds_interface.OrderListData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.OrderListItemData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.VendorData;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class GetOrdersForCarriersRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(GetOrdersForCarriersRequestHandler.class);

	private static final int PAGE_SIZE = 50;

	private static final int MAX_ORDER_COUNT = 1000;

	private static final String FIELD_SELECTED_CARRIERS = "selectedCarriers";

	private static final String FIELD_PART_SAP_VENDOR_ID = "sapVendorId";

	private static final String FIELD_LAST_CHANGE_FROM = "lastChangeFrom";

	private static final String FIELD_LAST_CHANGE_TO = "lastChangeTo";

	private static final String FIELD_PAGE = "page";

	private static final String FIELD_ENCODED_PAGE_NUMBERS = "encodedOrderNumbers";

	@Override
	protected void validate(LogonEventRequest eventBean) {
		final GetOrdersForCarriersRequest request = (GetOrdersForCarriersRequest) eventBean;

		validateMandatory(FIELD_CLIENT_TYPE, request.getClientType());
		validateMandatoryListItems(FIELD_SELECTED_CARRIERS, request.getSelectedCarriers(),
				validateMandatoryFieldFunction(VendorData::getSapVendorId, FIELD_PART_SAP_VENDOR_ID)
		);
		validateMandatory(FIELD_LAST_CHANGE_FROM, request.getLastChangeFrom());
		validateMandatory(FIELD_LAST_CHANGE_TO, request.getLastChangeTo());
		if (!request.getLastChangeFrom().isBefore(request.getLastChangeTo())) {
			throw newApplicationError(String.format("%s is not before %s", FIELD_LAST_CHANGE_FROM, FIELD_LAST_CHANGE_TO));
		}
		BigDecimal page = request.getPage();
		validateMandatory(FIELD_PAGE, page);
		if (page.compareTo(BigDecimal.ZERO) <= 0) {
			throw newApplicationError(FIELD_PAGE + " is not a positive number");
		}
		if (page.stripTrailingZeros().scale() > 0) {
			throw newApplicationError(FIELD_PAGE + " is not an integer number");
		}
		if (page.compareTo(BigDecimal.ONE) > 0 && isEmpty(request.getEncodedOrderNumbers())) {
			throw newApplicationError(FIELD_ENCODED_PAGE_NUMBERS + " is required for further paging.");
		}
	}

	@Override
	@AdnTrace
	protected LogonEventResponse perform(LogonEventRequest eventBean) {

		final GetOrdersForCarriersRequest request = (GetOrdersForCarriersRequest) eventBean;
		final GetOrdersForCarriersResponse response = createLogonOKEventResponse(GetOrdersForCarriersResponse.class);
		List<String> selectedSapVendors = request.getSelectedCarriers().stream().map(VendorData::getSapVendorId)
				.collect(Collectors.toList());
		Date fromDate = toDBDate(request.getLastChangeFrom());
		Date toDate = toDBDate(request.getLastChangeTo());
		int filterHash = calculateFilterHash(selectedSapVendors, fromDate, toDate);

		List<Long> allOrderNumbers;
		if (StringUtils.isNotEmpty(request.getEncodedOrderNumbers())) {
			// we have the exact order numbers encoded, decode them into order numbers for paging
			allOrderNumbers = decodeOrderNumbers(filterHash, request.getEncodedOrderNumbers());
		}
		else {
			long count = ldsService.countOrdersForCarriers(selectedSapVendors, fromDate, toDate);
			if (count > MAX_ORDER_COUNT) {
				throw newApplicationError(
						"The number of results exceeds " + MAX_ORDER_COUNT + " orders. Please refine filter criteria.");
			}
			allOrderNumbers = ldsService.getOrderNumbersForCarriers(selectedSapVendors, fromDate, toDate);
			response.setEncodedOrderNumbers(encodeOrderNumbers(filterHash, allOrderNumbers));
		}

		int orderCount = allOrderNumbers.size();
		int page = request.getPage().intValue();
		int skip = (page - 1) * PAGE_SIZE;

		response.setTotalNumberOfPages(BigDecimal.valueOf((orderCount + PAGE_SIZE - 1) / PAGE_SIZE));

		if (orderCount == 0) {
			return response;
		}

		if (skip >= orderCount) {
			throw newApplicationError("Page number is too big");
		}

		List<Long> relevantOrderNumbers = allOrderNumbers.subList(skip, Math.min(skip + PAGE_SIZE, orderCount));
		List<RepTransplusEntity> results = ldsService.getOrdersForCarriers(relevantOrderNumbers, selectedSapVendors);
		Long orderNumber = null;
		OrderListData orderlistData = null;
		for (RepTransplusEntity result : results) {

			if (!result.getOOrderNumber().equals(orderNumber)) {
				orderNumber = result.getOOrderNumber();
				orderlistData = createOrderListData(result);
				response.getOrder().add(orderlistData);
			}
			OrderListItemData orderListItemData = createOrderListItemData(result);
			orderlistData.getOrderListItem().add(orderListItemData);
		}

		return response;
	}

	private List<Long> decodeOrderNumbers(int filterHash, String encodedOrderNumbers) {
		// 1.) base64 decode the string to a byte array
		byte[] decodedBytes = Base64.getDecoder().decode(encodedOrderNumbers);
		// 2.) ungzip the bytes
		byte[] uncompressedBytes;
		try (ByteArrayInputStream inStream = new ByteArrayInputStream(decodedBytes);
				GZIPInputStream compressStream = new GZIPInputStream(inStream)) {
			uncompressedBytes = IOUtils.toByteArray(compressStream);
		}
		catch (IOException e) {
			throw new LogonException("Failed to uncompress the incoming encodedOrderNumbers", e);
		}
		// 3.) verify the signature
		ByteBuffer byteBuffer = ByteBuffer.wrap(uncompressedBytes);
		int orderNumberHash = byteBuffer.getInt();
		if (orderNumberHash != filterHash) {
			LOG.error("The filter hash {} from the decoded order numbers does not match the filter hash of the request {}",
					orderNumberHash, filterHash);
			throw newApplicationError("The encodedOrderNumbers is for a different request.");
		}
		// 4.) turn them into numbers
		List<Long> result = new ArrayList<>(uncompressedBytes.length / Long.BYTES + 1);
		long previousNumber = byteBuffer.getLong();
		result.add(previousNumber);
		while (byteBuffer.hasRemaining()) {
			long current = byteBuffer.getLong();
			// 4.1) Because we store diffs, we have to rebuild the list accordingly
			previousNumber += current;
			result.add(previousNumber);
		}
		return result;
	}

	private String encodeOrderNumbers(int filterHash, List<Long> orderNumber) {
		int count = orderNumber.size();
		if (count <= PAGE_SIZE) {
			// we don't calculate the encodedOrderNumbers if we don't have paging
			return null;
		}
		ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES + count * Long.BYTES);
		// 1.) add the signature hash
		byteBuffer.putInt(filterHash);
		// 2.) turn them into bytes
		Iterator<Long> iterator = orderNumber.iterator();
		long lastOrderNumber = iterator.next();
		byteBuffer.putLong(lastOrderNumber);
		while (iterator.hasNext()) {
			long current = iterator.next();
			// 2.1) Because we store diffs, we have to calculate the diff from the previous order number
			byteBuffer.putLong(current - lastOrderNumber);
			lastOrderNumber = current;
		}
		// 3.) gzip encode the result bytes
		byte[] compressedBytes;
		try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
			try (GZIPOutputStream compressOutStream = new GZIPOutputStream(outStream)) {
				compressOutStream.write(byteBuffer.array());
			}
			compressedBytes = outStream.toByteArray();
		}
		catch (IOException e) {
			throw new LogonException("Failed to compress to the encodedOrderNumbers", e);
		}

		// 4.) base64 encode the compressed array
		return Base64.getEncoder().encodeToString(compressedBytes);
	}

	private int calculateFilterHash(List<String> selectedSapVendors, Date fromDate, Date toDate) {
		return Objects.hash(selectedSapVendors, fromDate, toDate);
	}

	private OrderListData createOrderListData(RepTransplusEntity oe) {

		final OrderListData order = new OrderListData();

		/*
		 * (Single delivery) order related information
		 */
		final Long orderNumber = oe.getOOrderNumber();
		if (orderNumber != null) {
			order.setLogonOrderNo(orderNumber.toString());
		}
		order.setSorgCd(oe.getSareaSorgCd());

		/*
		 * Vendors
		 */
		if (oe.getCommCarrierVSapId() != null) {

			VendorData vendor = new VendorData();

			vendor.setSapVendorId(oe.getCommCarrierVSapId());

			order.setCommercialCarrier(vendor);
		}

		if (oe.getExecCarrierVSapId() != null) {
			VendorData vendor = new VendorData();

			vendor.setSapVendorId(oe.getExecCarrierVSapId());

			order.setExecutingCarrier(vendor);
		}

		/*
		 * Business partners
		 */
		BusinessPartnerData soldToPartnerData = new BusinessPartnerData();

		soldToPartnerData.setSapBpId(oe.getAgSapId());

		soldToPartnerData.setName1(oe.getAgName1());
		soldToPartnerData.setStreet(oe.getAgStreet() != null ? (oe.getAgStreet() + " " + oe.getAgHouseNumber()) : "");
		soldToPartnerData.setZipCode(oe.getAgPostalCode());
		soldToPartnerData.setCity(oe.getAgCity());
		soldToPartnerData.setCountry(oe.getAgCountry());

		BusinessPartnerData shipToPartnerData = new BusinessPartnerData();


		shipToPartnerData.setSapBpId(oe.getWeSapId());

		shipToPartnerData.setName1(oe.getWeName1());
		shipToPartnerData.setStreet(oe.getWeStreet());
		shipToPartnerData.setZipCode(oe.getWePostalCode());
		shipToPartnerData.setCity(oe.getWeCity());
		shipToPartnerData.setCountry(oe.getWeCountry());

		order.setSoldTo(soldToPartnerData);
		order.setShipTo(shipToPartnerData);


		order.setSupplyPlantNo(oe.getSpPlantCd());
		order.setShippingpointNo(oe.getSpSapShippingpointNo());

		if (oe.getODeliveryTypeCd().equals(Constant.DELIVERY_TYPE_PICKUP)) {
			order.setDeliveryType(Constant.INCOTERMS_EXW);
		}
		else {
			order.setDeliveryType(Constant.INCOTERMS_DDP);
		}


		// autoScheduleRelease: N/A for outgoing interface

		order.setEarliestDeliveryTime(toOffsetDateTimeInDBTimeZone(oe.getODeliveryFromDat()));
		order.setLatestDeliveryTime(toOffsetDateTimeInDBTimeZone(oe.getODeliveryToDat()));
		order.setPlannedDeliveryTime(toOffsetDateTimeInDBTimeZone(oe.getUnloadingPlannedDat()));
		order.setOrderValidationCode(oe.getPinCode());

		return order;
	}

	private OrderListItemData createOrderListItemData(RepTransplusEntity oie) {
		OrderListItemData item = new OrderListItemData();
		item.setLogonOrderItemNumber(BigDecimal.valueOf(oie.getOiOrderItemNo()));
		item.setMaterialNo(oie.getMatSapMaterialNo());
		item.setQuantity(oie.getOiOrderedQuantity());
		item.setQuantityUom(oie.getOiOrderedUomCd());

		return item;
	}
}
