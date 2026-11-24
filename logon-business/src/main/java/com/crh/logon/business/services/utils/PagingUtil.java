/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.services.utils;

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
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.utils.LogonException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Util class for helping order paging with encoding and decoding
 *
 * @author AdNovum Informatik AG
 */
//todo kardos: use this class for paging also in GetOrdersForCarriersRequestHandler
public class PagingUtil {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PagingUtil.class);

	private static final String FIELD_PAGE = "page";

	private static final String FIELD_ENCODED_PAGE_NUMBERS = "encodedOrderNumbers";

	protected static boolean isEmpty(Object value) {
		return value == null || ((value instanceof String) && (StringUtils.isEmpty((String) value)));
	}

	public static void validatePageField(BigDecimal page, String encodedOrderNumbers) {
		if (page.compareTo(BigDecimal.ZERO) <= 0) {
			throw new RuntimeException(FIELD_PAGE + " is not a positive number");
		}
		if (page.stripTrailingZeros().scale() > 0) {
			throw new RuntimeException(FIELD_PAGE + " is not an integer number");
		}
		if (page.compareTo(BigDecimal.ONE) > 0 && isEmpty(encodedOrderNumbers)) {
			throw new RuntimeException(FIELD_ENCODED_PAGE_NUMBERS + " is required for further paging.");
		}
	}

	public static List<Long> decodeOrderNumbers(int filterHash, String encodedOrderNumbers) {
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
			throw new RuntimeException("The encodedOrderNumbers is for a different request.");
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

	public static String encodeOrderNumbers(int filterHash, List<Long> orderNumber, int pageSize) {
		int count = orderNumber.size();
		if (count <= pageSize) {
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

	public static int calculateFilterHash(List<String> selectedSapVendors, Date fromDate, Date toDate) {
		return Objects.hash(selectedSapVendors, fromDate, toDate);
	}

	public static int calculateFilterHash(String soldToSapId, String shipToSapId, List<Long> selectedLogonOrders) {
		return Objects.hash(soldToSapId, shipToSapId, selectedLogonOrders);
	}

	public static List<Long> getRelevantOrderNumbers(List<Long> allOrderNumbers, int skip, int pageSize) {
		int orderCount = allOrderNumbers.size();
		return allOrderNumbers.subList(skip, Math.min(skip + pageSize, orderCount));
	}

	public static BigDecimal calculateTotalNumberOfPages(int orderCount, int pageSize) {
		return BigDecimal.valueOf((orderCount + pageSize - 1) / pageSize);
	}
}
