/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.reporting;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;

import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.utils.text.Transliterator;
import com.crh.logon.utils.time.DateTimeZoneFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.dto.BlobDTO;
import com.crh.logon.persistence.entities.views.TruckRevenuePlanMatEntity;
import com.crh.logon.persistence.entities.views.TruckRevenuePlanningEntity;
import com.crh.logon.utils.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
public class TruckRevenuePlanningXlsHelper {

	private static final JcanLogger LOG =
		JcanLoggerFactory.getLogger(TruckRevenuePlanningXlsHelper.class);

	public static final String EXAMPLE_FILE = "example_truck_revenue_planning_report.xls";

	private static final String HEADER_TITLE_FRENCH =
		"Rapport journalier du transporteur ''{0}'' pour le {1}";

	private static final Pos POS_HEADER_TITLE = new Pos(2, 4);
	private static final Pos POS_HEADER_LOGO = new Pos(1, 17);
	private static final Pos POS_HEADER_REPORT_DATE = new Pos(6, 17);
	private static final Pos POS_HEADER_REPORT_TIME = new Pos(6, 19);
	private static final Pos POS_HEADER_CARRIER = new Pos(8, 0);
	private static final Pos POS_HEADER_TRUCK = new Pos(9, 0);

	private static final int ROW_TABLE_HEADER = 11;
	private static final int ROW_TABLE_BODY = 12;
	private static final int ROW_TABLE_BODY_EXAMPLE_TRUCK = 13;
	private static final int ROW_TABLE_BODY_EXAMPLE_ORDER_ITEM = 14;
	private static final int ROW_TABLE_BODY_EXAMPLE_COMMENT = 32;

	private static final int COL_TABLE_HEADER_ORDER_NUMBER = 0;
	private static final int COL_TABLE_HEADER_SHIPTO_NAME = 1;
	private static final int COL_TABLE_HEADER_SHIPTO_ADDRESS = 2;
	private static final int COL_TABLE_HEADER_ARRIVING_TIME = 3;
	private static final int COL_TABLE_HEADER_ORDER_ITEM = 4;
	private static final int COL_TABLE_HEADER_PLANT = 5;
	private static final int COL_TABLE_HEADER_QUANTITY = 6;
	private static final int COL_TABLE_BODY_CARRIER = 0;
	private static final int COL_TABLE_BODY_TRUCK = 0;
	private static final int COL_TABLE_BODY_COMMENT_TITLE = 1;
	private static final int COL_TABLE_BODY_COMMENT_TEXT = 3;


	private final Locale locale;
	private final Transliterator transliterator;

	public TruckRevenuePlanningXlsHelper(Locale locale, Transliterator transliterator) {
		this.locale = locale;
		this.transliterator = transliterator;
	}

	public void cloneColumnFormatting(Sheet dstSheet, Sheet srcSheet, int nrOfColumns) {
		// [[
		Object[][] params = {{"dstSheet", dstSheet},
							{"srcSheet", srcSheet},
							{"nrOfColumns", nrOfColumns + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "cloneColumnFormatting", params);
		}
		// ]]
		try {
			for (int i = 0; i < nrOfColumns; i++) {
				// Copy default column style (may not really be necessary)
				CellStyle columnStyle = srcSheet.getColumnStyle(i);
				
				if (columnStyle != null) { // Can't set a null style!
					dstSheet.setDefaultColumnStyle(i, columnStyle);
				}
			
				// Copy column width
				int columnWidth = srcSheet.getColumnWidth(i);
				dstSheet.setColumnWidth(i, columnWidth);
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}


	/**
	 * Clone a row, including cell/text formatting, but not the text itself.
	 * 
	 * @param dstWorkbook destination workbook that will have the new cell style
	 * @param dstSheet the worksheet containing the destination row
	 * @param srcSheet the worksheet containing the source row
	 * @param dstRowNum the destination row
	 * @param srcRowNum the source row
	 */
	public void cloneRowFormatting(Workbook dstWorkbook, Sheet dstSheet, Sheet srcSheet,
			int dstRowNum, int srcRowNum) {
		// [[
		Object[][] params = {{"dstWorkbook", dstWorkbook},
							{"dstSheet", dstSheet},
							{"srcSheet", srcSheet},
							{"dstRowNum", dstRowNum + ""},
							{"srcRowNum", srcRowNum + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "cloneRowFormatting", params);
		}
		// ]]
		try {
			// Create destination row
			Row dstRow = dstSheet.getRow(dstRowNum);
			Row srcRow = srcSheet.getRow(srcRowNum);
			
			if (dstRow != null) {
				dstSheet.shiftRows(dstRowNum, dstSheet.getLastRowNum(), 1);
			} else {
				dstRow = dstSheet.createRow(dstRowNum);
			}
			
			// Copy height
			short height = srcRow.getHeight();
			dstRow.setHeight(height);
			
			// Copy cells
			for (int i = 0; i < srcRow.getLastCellNum(); i++) {
				Cell srcCell = srcRow.getCell(i);
			
				if (srcCell == null) {
					continue;
				}
			
				Cell dstCell = dstRow.createCell(i);
			
				// Copy cell style
				CellStyle dstCellStyle = dstWorkbook.createCellStyle();
				dstCellStyle.cloneStyleFrom(srcCell.getCellStyle());
				dstCell.setCellStyle(dstCellStyle);
			
				// Set the cell data type
				dstCell.setCellType(srcCell.getCellType());
			}
			
			// Copy merged regions
			List<CellRangeAddress> srcMergedRegions = findMergedRegionsInRow(srcSheet, srcRowNum);
			
			for (CellRangeAddress srcCellRangeAddress: srcMergedRegions) {
				CellRangeAddress dstCellRangeAddress = new CellRangeAddress(
						dstRow.getRowNum(),
						(dstRow.getRowNum() + (srcCellRangeAddress.getLastRow()
								- srcCellRangeAddress.getFirstRow())),
						srcCellRangeAddress.getFirstColumn(),
						srcCellRangeAddress.getLastColumn());
		
				dstSheet.addMergedRegion(dstCellRangeAddress);
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}


	/**
	 * Clone the values of the cells of the given rows.
	 * 
	 * @param dstSheet the worksheet containing the destination row
	 * @param srcSheet the worksheet containing the source row
	 * @param dstRowNum the destination row
	 * @param srcRowNum the source row
	 */
	private void cloneRowValues(Sheet dstSheet, Sheet srcSheet, int dstRowNum, int srcRowNum) {
		// [[
		Object[][] params = {{"dstSheet", dstSheet},
							{"srcSheet", srcSheet},
							{"dstRowNum", dstRowNum + ""},
							{"srcRowNum", srcRowNum + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "cloneRowValues", params);
		}
		// ]]
		try {
			// These rows are expected to be valid rows!
			Row dstRow = dstSheet.getRow(dstRowNum);
			Row srcRow = srcSheet.getRow(srcRowNum);
			
			// Copy cells
			for (int i = 0; i < srcRow.getLastCellNum(); i++) {
				Cell dstCell = dstRow.getCell(i);
				Cell srcCell = srcRow.getCell(i);
			
				if (srcCell == null) {
					continue;
				}
				
				copyCellValue(dstCell, srcCell);
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}


	//private void cloneCellValue(Sheet dstSheet, Sheet srcSheet, Pos dstCellPos, Pos srcCellPos) {
	//	// [[
	//	Object[][] params = {{"dstSheet", dstSheet},
	//						{"srcSheet", srcSheet},
	//						{"dstCellPos", dstCellPos},
	//						{"srcCellPos", srcCellPos}};
	//	boolean logDebugEnabled = LOG.isDebugEnabled();
	//	if (logDebugEnabled) {
	//		LOG.entering(this, "cloneCellValue", params);
	//	}
	//	// ]]
	//	try {
	//		int dstRowNum = dstCellPos.getRowNum();
	//		int dstColNum = dstCellPos.getColNum();
	//		int srcRowNum = srcCellPos.getRowNum();
	//		int srcColNum = srcCellPos.getColNum();
	//		
	//		cloneCellValue(dstSheet, srcSheet, dstRowNum, dstColNum, srcRowNum, srcColNum);
	//	}
	//	finally {
	//	// [[
	//		if (logDebugEnabled) {
	//			LOG.exiting();
	//		}
	//	// ]]
	//	}
	//}


	private void cloneCellValue(Sheet dstSheet, Sheet srcSheet, int dstRowNum, int dstColNum,
			int srcRowNum, int srcColNum) {
		// [[
		Object[][] params = {{"dstSheet", dstSheet},
							{"srcSheet", srcSheet},
							{"dstRowNum", dstRowNum + ""},
							{"dstColNum", dstColNum + ""},
							{"srcRowNum", srcRowNum + ""},
							{"srcColNum", srcColNum + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "cloneCellValue", params);
		}
		// ]]
		try {
			Row dstRow = dstSheet.getRow(dstRowNum);
			Cell dstCell = dstRow.getCell(dstColNum);
			Row srcRow = srcSheet.getRow(srcRowNum);
			Cell srcCell = srcRow.getCell(srcColNum);
			
			copyCellValue(dstCell, srcCell);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}


	private void copyCellValue(Cell dstCell, Cell srcCell) {
		// [[
		Object[][] params = {{"dstCell", dstCell},
							{"srcCell", srcCell}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "copyCellValue", params);
		}
		// ]]
		try {
			switch (srcCell.getCellType()) {
				case Cell.CELL_TYPE_BOOLEAN:
					boolean booleanCellValue = srcCell.getBooleanCellValue();
					dstCell.setCellValue(booleanCellValue);
					break;
				case Cell.CELL_TYPE_ERROR:
					byte errorCellValue = srcCell.getErrorCellValue();
					dstCell.setCellErrorValue(errorCellValue);
					break;
				case Cell.CELL_TYPE_FORMULA:
					String cellFormula = srcCell.getCellFormula();
					dstCell.setCellFormula(cellFormula);
					break;
				case Cell.CELL_TYPE_NUMERIC:
					double numericCellValue = srcCell.getNumericCellValue();
					dstCell.setCellValue(numericCellValue);
					break;
				case Cell.CELL_TYPE_STRING:
					RichTextString richStringCellValue = srcCell.getRichStringCellValue();
					dstCell.setCellValue(richStringCellValue);
					break;
				default:
					String stringCellValue = srcCell.getStringCellValue();
					dstCell.setCellValue(stringCellValue);
					break;
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}


	public void printHeader(Workbook wb, Sheet sheet, Sheet exampleWbSheet, String plannedStartDayFormatted,
			Date reportDate, String carrierName, BlobDTO crhLogoDTO, TimeZone userTimeZone)
	throws IOException {
		// [[
		Object[][] params = {{"wb", wb},
							{"sheet", sheet},
							{"exampleWbSheet", exampleWbSheet},
							{"plannedStartDayFormatted", plannedStartDayFormatted},
							{"reportDate", reportDate},
							{"carrierName", carrierName},
							{"crhLogoDTO", crhLogoDTO},
							{"userTimeZone", userTimeZone}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "printHeader", params);
		}
		// ]]
		try {
			//String titleText = Message.lookup(locale, "truckrevenueplanning.xls.title",
			//		carrierName, plannedStartDayFormatted);
			
			String titleText = MessageFormat.format(HEADER_TITLE_FRENCH, carrierName,
					plannedStartDayFormatted);
			
			setCellValue(sheet, POS_HEADER_TITLE, titleText);
			
			byte[] crhLogoBytes = crhLogoDTO.getStream();
			
			if (crhLogoBytes != null) {
				double scale = TruckRevenuePlanningCommons.calculateScale(crhLogoBytes);
				
				/*
				 * NOTE (romek Jul 6, 2011): Picture#resize(double) did not scale to the correct size,
				 * probably because of the different font sizes. Hence we do the resize ourselves, generating
				 * a picture that is always a PNG ==> we are no longer restricted to PNGs and JPGs (see
				 * Workbook.PICTURE_TYPE_*, the two essential formats that Apache POI supports), but to all
				 * the formats that ImageIO supports. The same could be done for the PDF reports too, but
				 * currently there is no such requirement.
				 */
				crhLogoBytes = resizeImage(crhLogoBytes, scale);
				
				CreationHelper helper = wb.getCreationHelper();
				ClientAnchor anchor = helper.createClientAnchor();
				anchor.setCol1(POS_HEADER_LOGO.getColNum());
				anchor.setRow1(POS_HEADER_LOGO.getRowNum());
				int pictureIdx = wb.addPicture(crhLogoBytes, Workbook.PICTURE_TYPE_PNG);
				// NOTE (romek Jul 6, 2011): Only one patriarch per sheet for all images!
				Drawing drawing = sheet.createDrawingPatriarch();
				Picture crhLogo = drawing.createPicture(anchor, pictureIdx);
				crhLogo.resize(); // Must do!
			}
			
			// Report date and time
			String reportDateFormatted = TruckRevenuePlanningCommons.formatReportDate(reportDate, userTimeZone);
			String reportTimeFormatted = TruckRevenuePlanningCommons.formatReportTime(reportDate, userTimeZone);
			
			setCellValue(sheet, POS_HEADER_REPORT_DATE, reportDateFormatted);
			setCellValue(sheet, POS_HEADER_REPORT_TIME, reportTimeFormatted);
			
			// Table headers
			
			//setCellValue(sheet, POS_HEADER_CARRIER,
			//		Message.lookup(locale, "truckrevenueplanning.header.carrier"));
			//setCellValue(sheet, POS_HEADER_TRUCK,
			//		Message.lookup(locale, "truckrevenueplanning.header.truck"));
			//
			//setCellValue(sheet, ROW_TABLE_HEADER, COL_TABLE_HEADER_ORDER_NUMBER,
			//		Message.lookup(locale, "truckrevenueplanning.header.ordernumber"));
			//setCellValue(sheet, ROW_TABLE_HEADER, COL_TABLE_HEADER_SHIPTO_NAME,
			//		Message.lookup(locale, "truckrevenueplanning.header.shiptoname"));
			//setCellValue(sheet, ROW_TABLE_HEADER, COL_TABLE_HEADER_SHIPTO_ADDRESS,
			//		Message.lookup(locale, "truckrevenueplanning.header.shiptoaddress"));
			//setCellValue(sheet, ROW_TABLE_HEADER, COL_TABLE_HEADER_ARRIVING_TIME,
			//		Message.lookup(locale, "truckrevenueplanning.header.arrivingtime"));
			//setCellValue(sheet, ROW_TABLE_HEADER, COL_TABLE_HEADER_ORDER_ITEM,
			//		Message.lookup(locale, "truckrevenueplanning.header.orderitemname"));
			//setCellValue(sheet, ROW_TABLE_HEADER, COL_TABLE_HEADER_PLANT,
			//		Message.lookup(locale, "truckrevenueplanning.header.plant"));
			//setCellValue(sheet, ROW_TABLE_HEADER, COL_TABLE_HEADER_QUANTITY,
			//		Message.lookup(locale, "truckrevenueplanning.header.quantity"));
			
			final int[] rowsToClone = new int[] { POS_HEADER_CARRIER.getRowNum(),
					POS_HEADER_TRUCK.getRowNum(), ROW_TABLE_HEADER };
			
			for (int rowNum: rowsToClone) {
				cloneRowValues(sheet, exampleWbSheet, rowNum, rowNum);
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}


	public void printBody(Workbook wb, Sheet sheet, Sheet exampleWbSheet,
								 String carrierName,
								 Map<Long, List<TruckRevenuePlanningEntity>> ordersByTruck,
								 Map<Long, List<TruckRevenuePlanMatEntity>> planningMatsByOrder,
								 Map<Long, String> truckNamesById) {
		// [[
		Object[][] params = {{"wb", wb},
							{"sheet", sheet},
							{"exampleWbSheet", exampleWbSheet},
							{"carrierName", carrierName},
							{"ordersByTruck", ordersByTruck},
							{"planningMatsByOrder", planningMatsByOrder},
							{"truckNamesById", truckNamesById}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "printBody", params);
		}
		// ]]
		try {
			int currentRowNum = ROW_TABLE_BODY;
			
			// Name of carrier
			setCellValue(sheet, currentRowNum++, COL_TABLE_BODY_CARRIER, carrierName);
			
			for (Long truckId: ordersByTruck.keySet()) {
				String truckName = truckNamesById.get(truckId);
				
				// Name of truck
				cloneRowFormatting(wb, sheet, exampleWbSheet, currentRowNum,
						ROW_TABLE_BODY_EXAMPLE_TRUCK);
				setCellValue(sheet, currentRowNum++, COL_TABLE_BODY_TRUCK, truckName);
				
				// Order items per truck
				List<TruckRevenuePlanningEntity> orders = ordersByTruck.get(truckId);
				
				for (TruckRevenuePlanningEntity planning: orders) {
					Long orderId = planning.getOrderId();
					boolean hasComment = !StringUtils.empty(planning.getSchedulingComment());
					List<TruckRevenuePlanMatEntity> planningMats = planningMatsByOrder.get(orderId);

					TimeZone shipToTz = new TimeZone(planning.getShiptoTimezoneCd() != null
															 ? planning.getShiptoTimezoneCd()
															 : DateTimeZoneFactory.getDefaultTimeZoneId(),
													 locale);

					for (TruckRevenuePlanMatEntity planningMat: planningMats) {

						String orderedUomCd = planningMat.getOrderedUomCd();
						
						if (Constant.UOM_TONS.equals(orderedUomCd)) {
							orderedUomCd = "T";
						}
						
						cloneRowFormatting(wb, sheet, exampleWbSheet, currentRowNum,
								ROW_TABLE_BODY_EXAMPLE_ORDER_ITEM);
						
						String orderNumber = String.valueOf(planning.getOrderNumber());
						String orderValidationCode = planning.getOrderValidationCode();
						
						orderNumber = StringUtils.empty(orderValidationCode)
								? orderNumber
								: orderNumber + "-" + orderValidationCode;
						setCellValue(sheet, currentRowNum, COL_TABLE_HEADER_ORDER_NUMBER,
								orderNumber);
						
						setCellValue(sheet, currentRowNum, COL_TABLE_HEADER_SHIPTO_NAME,
								planning.getShiptoName1());
						
						setCellValue(sheet, currentRowNum, COL_TABLE_HEADER_SHIPTO_ADDRESS,
								TruckRevenuePlanningCommons.buildAddress(planning));
						
						setCellValue(sheet, currentRowNum, COL_TABLE_HEADER_ARRIVING_TIME,
								TruckRevenuePlanningCommons.formatPlannedArrival(planning.getPlannedStartOfUnloading(), shipToTz));
						
						setCellValue(sheet, currentRowNum, COL_TABLE_HEADER_ORDER_ITEM,
								planningMat.getOrderItemName());
						
						setCellValue(sheet, currentRowNum, COL_TABLE_HEADER_PLANT,
								planning.getShipptPlant());
						
						BigDecimal orderedQuantity = planningMat.getOrderedQuantity();
						
						setCellValue(sheet, currentRowNum, COL_TABLE_HEADER_QUANTITY,
								TruckRevenuePlanningCommons.formatQuantity(orderedQuantity, locale)
										+ " " + orderedUomCd);
						
						currentRowNum++;
						
						if (hasComment) {
							cloneRowFormatting(wb, sheet, exampleWbSheet, currentRowNum,
									ROW_TABLE_BODY_EXAMPLE_COMMENT);
							
							//setCellValue(sheet, currentRowNum, COL_TABLE_BODY_COMMENT_TITLE,
							//		Message.lookup(locale, "truckrevenueplanning.order.schedulingcomment")
							//		+ ":");
							
							cloneCellValue(sheet, exampleWbSheet, currentRowNum,
									COL_TABLE_BODY_COMMENT_TITLE, ROW_TABLE_BODY_EXAMPLE_COMMENT,
									COL_TABLE_BODY_COMMENT_TITLE);
							
							setCellValue(sheet, currentRowNum, COL_TABLE_BODY_COMMENT_TEXT,
									planning.getSchedulingComment());
							
							currentRowNum++;
						}
					}
				}
			}
			// TODO
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}


	private List<CellRangeAddress> findMergedRegionsInRow(Sheet sheet, int rowNum) {
		// [[
		List result = null;
		Object[][] params = {{"sheet", sheet},
							{"rowNum", rowNum + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findMergedRegionsInRow", params);
		}
		// ]]
		try {
			List<CellRangeAddress> mergedRegions = new ArrayList<CellRangeAddress>();
			
			for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
				CellRangeAddress cellRangeAddress = sheet.getMergedRegion(i);
			
				if (cellRangeAddress.getFirstRow() == rowNum) {
					mergedRegions.add(cellRangeAddress);
				}
			}
			
			return result = mergedRegions;
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
	 * @param sheet
	 * @param pos
	 * @param value
	 * 
	 * @adnlogger.notrace
	 */
	private void setCellValue(Sheet sheet, Pos pos, String value) {
		setCellValue(sheet, pos.getRowNum(), pos.getColNum(), value);
	}


	private void setCellValue(Sheet sheet, int rowNum, int colNum, String value) {
		// [[
		Object[][] params = {{"sheet", sheet},
							{"rowNum", rowNum + ""},
							{"colNum", colNum + ""},
							{"value", value}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setCellValue", params);
		}
		// ]]
		try {
			Row row = sheet.getRow(rowNum);
			Cell cell = row.getCell(colNum);

			String transliteratedValue = transliterate(value);
			cell.setCellValue(transliteratedValue);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}


	/**
	 * {@link Picture#resize(double)} is inaccurate for non-default font sizes, therefore a different
	 * approach is required to resize images properly.
	 * 
	 * @param data
	 * @param scale
	 * @return
	 * @throws IOException 
	 */
	private byte[] resizeImage(byte[] data, double scale) throws IOException {
		// [[
		byte[] result = null;
		Object[][] params = {{"data", data},
							{"scale", scale + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "resizeImage", params);
		}
		// ]]
		try {
			BufferedImage image = null;
			BufferedImage resizedImage = null;
			
			try {
				ByteArrayInputStream bais = new ByteArrayInputStream(data);
				image = ImageIO.read(bais);
			
				int newWidth = (int) (image.getWidth() * scale);
				int newHeight = (int) (image.getHeight() * scale);
				resizedImage = resize(image, newWidth, newHeight);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(resizedImage, "PNG", baos);
			
				byte[] scaledData = baos.toByteArray();
				return result = scaledData;
			}
			finally {
				// NOTE (romek Jul 6, 2011): The following is probably not necessary.
				if (image != null) {
					image.flush();
				}
				if (resizedImage != null) {
					resizedImage.flush();
				}
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


	/**
	 * Resize an image with some rendering hints to generate a fair-quality result. 
	 * 
	 * @param image
	 * @param width
	 * @param height
	 * @return
	 */
	private BufferedImage resize(BufferedImage image, int width, int height) {
		// [[
		BufferedImage result = null;
		Object[][] params = {{"image", image},
							{"width", width + ""},
							{"height", height + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "resize", params);
		}
		// ]]
		try {
			int type = image.getType() == 0? BufferedImage.TYPE_INT_ARGB : image.getType();
			BufferedImage resizedImage = new BufferedImage(width, height, type);
			Graphics2D g = resizedImage.createGraphics();
			g.setComposite(AlphaComposite.Src);
			
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			
			g.setRenderingHint(RenderingHints.KEY_RENDERING,
			RenderingHints.VALUE_RENDER_QUALITY);
			
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
			
			g.drawImage(image, 0, 0, width, height, null);
			g.dispose();
			return result = resizedImage;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	private String transliterate(String input) {
		return transliterator.transliterate(input);
	}


	private static class Pos {

		private final int rowNum;
		private final int colNum;


		public Pos(int rowNum, int colNum) {
			super();
			this.rowNum = rowNum;
			this.colNum = colNum;
		}


		public int getRowNum() {
			return rowNum;
		}


		public int getColNum() {
			return colNum;
		}

	}

}
