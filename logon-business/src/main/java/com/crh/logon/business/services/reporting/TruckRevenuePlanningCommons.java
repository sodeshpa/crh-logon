/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.reporting;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.Locale;

import javax.imageio.ImageIO;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.localinterfaces.GenericBlobServiceLocalInterface;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.persistence.dto.BlobDTO;
import com.crh.logon.persistence.entities.views.TruckRevenuePlanningEntity;

/**
 * @author AdNovum Informatik AG
 */
public class TruckRevenuePlanningCommons {

	private static final JcanLogger LOG =
		JcanLoggerFactory.getLogger(TruckRevenuePlanningCommons.class);

	public static final int CRH_LOGO_WIDTH = 117;
	public static final int CRH_LOGO_HEIGHT = 55;
	
	private static final int QUANTITY_ROUNDING_SCALE = 2;
	private static final int QUANTITY_ROUNDING_MODE = BigDecimal.ROUND_HALF_UP;
	
	private static final String CRH_LOGO_IMAGE_NAME = "truck_revenue_planning_report";


	public static String formatReportDate(Date reportDate, TimeZone timezone) {
		// [[
		String result = null;
		Object[][] params = {{"reportDate", reportDate},
							{"timezone", timezone}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(TruckRevenuePlanningCommons.class, "formatReportDate", params);
		}
		// ]]
		try {
			return result = timezone.fmtAsDayMonthYear(reportDate);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public static String formatReportTime(Date reportDate, TimeZone timezone) {
		// [[
		String result = null;
		Object[][] params = {{"reportDate", reportDate},
							 {"timezone", timezone}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(TruckRevenuePlanningCommons.class, "formatReportTime", params);
		}
		// ]]
		try {
			return result = timezone.fmtAsHourMinuteSecondWithOffset(reportDate);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public static String formatPlannedArrival(Date plannedArrival, TimeZone shiptoTz) {
		// [[
		String result = null;
		Object[][] params = {{"plannedArrival", plannedArrival}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(TruckRevenuePlanningCommons.class, "formatPlannedArrival", params);
		}
		// ]]
		try {
			return result = shiptoTz.fmtAsHourMinuteWithOffset(plannedArrival);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public static String formatQuantity(BigDecimal quantity, Locale locale) {
		// [[
		String result = null;
		Object[][] params = {{"quantity", quantity},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(TruckRevenuePlanningCommons.class, "formatQuantity", params);
		}
		// ]]
		try {
			quantity = quantity.setScale(QUANTITY_ROUNDING_SCALE, QUANTITY_ROUNDING_MODE);
			
			DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
			symbols.setDecimalSeparator(',');
			symbols.setGroupingSeparator(' ');	
			
			DecimalFormat formatter = new DecimalFormat("###,###.##", symbols);
			return result = formatter.format(quantity);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}


	public static String buildAddress(TruckRevenuePlanningEntity planning) {
		// [[
		String result = null;
		Object[][] params = {{"planning", planning}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(TruckRevenuePlanningCommons.class, "buildAddress", params);
		}
		// ]]
		try {
			StringBuilder sb = new StringBuilder();
			
			String shiptoStreet = planning.getShiptoStreet();
			String shiptoHouseNumber = planning.getShiptoHouseNumber();
			String shiptoPostalCode = planning.getShiptoPostalCode();
			String shiptoCity = planning.getShiptoCity();
			
			if (shiptoStreet != null) {
				sb.append(shiptoStreet);
			}
			
			if (shiptoHouseNumber != null) {
				if (shiptoStreet != null) {
					sb.append(" ");
				}
			
				sb.append(shiptoHouseNumber);
			}
			
			if (shiptoStreet != null || shiptoHouseNumber != null) {
				sb.append(", ");
			}
			
			sb.append(shiptoPostalCode);
			sb.append(", ");
			sb.append(shiptoCity);
			
			return result = sb.toString();
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
	 * Fetch the Crh logo that is used to display in the headers of the Truck Revenue Planning PDF/Excel
	 * reports. (We currently use the same Logo for both reports.)
	 * 
	 * Note: some image formats may not be supported by jcan-print or Apache POI. One known example is
	 * the original GIF format (logon-common-web/src/main/webapp/images/logo_crh.gif), that is not
	 * supported by either of them.
	 * 
	 * @param genericBlobService 
	 * @return
	 */
	public static BlobDTO fetchCrhLogo(GenericBlobServiceLocalInterface genericBlobService) {
		// [[
		BlobDTO result = null;
		Object[][] params = {{"genericBlobService", genericBlobService}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(TruckRevenuePlanningCommons.class, "fetchCrhLogo", params);
		}
		// ]]
		try {
			BlobDTO crhLogoDTO = genericBlobService.fetchBlobWithStringKeyValue("ImageEntity", "imageName",
					CRH_LOGO_IMAGE_NAME, true);
			
			PDFUtils.checkImageIntegrity(crhLogoDTO, CRH_LOGO_IMAGE_NAME);
			
			return result = crhLogoDTO;
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
	 * Calculates the image width/height scale that is used to resize an image to fit the predefined
	 * CRH_LOGO_WIDTH and CRH_LOGO_HEIGHT, keeping aspect ratio.
	 * 
	 * @param data image data - is expected to be non-null and to contain a valid image - you can use
	 *              {@link PDFUtils#checkImageIntegrity(BlobDTO, String)} to ensure validity.
	 * @return
	 * @throws IOException
	 */
	public static double calculateScale(byte[] data)
	throws IOException {
		// [[
		double result = 0.0;
		Object[][] params = {{"data", data}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(TruckRevenuePlanningCommons.class, "calculateScale", params);
		}
		// ]]
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			BufferedImage image = ImageIO.read(bais);
			double scaledWidth = (double) CRH_LOGO_WIDTH / image.getWidth();
			double scaledHeight = (double) CRH_LOGO_HEIGHT / image.getHeight();
			double scale = Math.min(scaledWidth, scaledHeight);
			image.flush();
			
			return result = scale;
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
