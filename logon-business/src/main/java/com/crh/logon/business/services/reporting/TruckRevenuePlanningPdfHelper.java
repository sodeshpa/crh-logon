/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.reporting;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import ch.nevis.jcan.print.base.Color;
import ch.nevis.jcan.print.base.Measurable;
import ch.nevis.jcan.print.base.Millimeters;
import ch.nevis.jcan.print.base.PrinterException;
import ch.nevis.jcan.print.fonts.Font;
import ch.nevis.jcan.print.layout.Alignment;
import ch.nevis.jcan.print.layout.Border;
import ch.nevis.jcan.print.layout.Cell;
import ch.nevis.jcan.print.layout.Container;
import ch.nevis.jcan.print.layout.Image;
import ch.nevis.jcan.print.layout.Paragraph;
import ch.nevis.jcan.print.layout.Table;
import ch.nevis.jcan.print.pdfgen.PdfGen;

import com.crh.logon.business.services.utils.Message;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.persistence.dto.BlobDTO;
import com.crh.logon.persistence.entities.views.TruckRevenuePlanMatEntity;
import com.crh.logon.persistence.entities.views.TruckRevenuePlanningEntity;
import com.crh.logon.utils.text.Transliterator;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.time.DateTimeZoneFactory;

/**
 * @author AdNovum Informatik AG
 */
public class TruckRevenuePlanningPdfHelper {

	private static final JcanLogger LOG =
		JcanLoggerFactory.getLogger(TruckRevenuePlanningPdfHelper.class);

	public static final String ARIAL_TTF_FILE = "arial.ttf";
	public static final String ARIAL_BOLD_TTF_FILE = "arialbd.ttf";

	public static final Measurable MARGIN_TOP = new Millimeters(10);
	public static final Measurable MARGIN_BOTTOM = new Millimeters(10);
	public static final Measurable MARGIN_LEFT = new Millimeters(20);
	public static final Measurable MARGIN_RIGHT = new Millimeters(20);

	private static final int NR_OF_COLUMNS_WITH_FREIGHT_RATES = 11;
	private static final int NR_OF_COLUMNS_WITHOUT_FREIGHT_RATES = 10;
	private static final int FREIGHT_COST_ROUNDING_SCALE = 2;
	private static final int FREIGHT_COST_ROUNDING_MODE = BigDecimal.ROUND_HALF_UP;

	private static final Border TITLE_BORDER = new Border(Color.RED, 3);
	private static final Border HEADER_BORDER_TOP = new Border(Border.TOP, Color.RED, 2);
	private static final Border HEADER_BORDER_BOTTOM = new Border(Border.BOTTOM, Color.RED, 2);
	private static final Border BODY_BORDER = new Border(Border.BOTTOM, Color.RED, 1);
	private static final Border SUMMARY_BORDER = new Border(Border.TOP, Color.RED, 2);

	private static final double HORIZONTAL_PADDING_1POINT = 0;
	private static final double HORIZONTAL_PADDING_3POINT = 3;
	private static final double HORIZONTAL_PADDING_10POINT = 10;
	private static final double HORIZONTAL_PADDING_20POINT = 20;
	private static final double VERTICAL_PADDING_5POINT = 5;
	private static final double VERTICAL_PADDING_0POINT = 0;

	private static final double VALIDATION_CODE_COLUMN_WIDTH = 60;
	private static final double ARTICLE_COLUMN_WIDTH = 80;
	private static final double PLANNED_DELIVERY_COLUMN_WIDTH = 40;


	private final Locale locale;
	private final Transliterator transliterator;

	public TruckRevenuePlanningPdfHelper(Locale locale, Transliterator transliterator) {
		this.locale = locale;
		this.transliterator = transliterator;
	}


	public void printHeader(PdfGen pdfGen, Container container, Font headerFont,
			String reportDateFormatted, String reportTimeFormatted, String plannedStartDayFormatted,
			BlobDTO crhLogoDTO)
	throws IOException, PrinterException {
		// [[
		Object[][] params = {{"pdfGen", pdfGen},
							{"container", container},
							{"headerFont", headerFont},
							{"reportDateFormatted", reportDateFormatted},
							{"reportTimeFormatted", reportTimeFormatted},
							{"plannedStartDayFormatted", plannedStartDayFormatted},
							{"crhLogoDTO", crhLogoDTO}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "printHeader", params);
		}
		// ]]
		try {
			double pageWidth = pdfGen.getPageWidth() - pdfGen.getLeftMargin() - pdfGen.getRightMargin();
			double titleWidth = pageWidth * ((double) 2 / 3);
			double leftCellWidth = (pageWidth - titleWidth) / 2;
			double rightCellsWidth = leftCellWidth / 2;
			double padding = 10;

			Table table = new Table(new double[] { leftCellWidth, titleWidth, rightCellsWidth,
					rightCellsWidth });

				// Row 1, Col 1: Empty cell
				Cell emptyCell = new Cell();
				table.add(emptyCell);
				emptyCell.setHorizontalPadding(padding);
				emptyCell.setVerticalPadding(padding);
					Paragraph emptyParagraph = new Paragraph();
					emptyCell.add(emptyParagraph);

				// Row 1, Col 2: Title cell
				Cell titleCell = new Cell();
				table.add(titleCell);
				titleCell.setHorizontalPadding(padding);
				titleCell.setVerticalPadding(padding);
					titleCell.setBorder(TITLE_BORDER);
					String titleText = Message.lookup(locale, "truckrevenueplanning.pdf.title", plannedStartDayFormatted);
					Paragraph titleParagraph = new Paragraph(Alignment.CENTER, headerFont, transliterate(titleText));
					titleCell.add(titleParagraph);

				// Row 1, Col 3 + Col 4: Logo cell
				Cell logoCell = new Cell(2, 1);
				table.add(logoCell);
				logoCell.setHorizontalPadding(padding);
				logoCell.setVerticalPadding(padding);
				logoCell.setHAlignment(Alignment.RIGHT);
					byte[] crhLogoBytes = crhLogoDTO.getStream();

					if (crhLogoBytes != null) {
						double scale = TruckRevenuePlanningCommons.calculateScale(crhLogoBytes);
						ch.nevis.jcan.print.images.Image crhLogoImage = PDFUtils.createImage(
								crhLogoBytes);
						Image crhLogoImageLayout = new Image(crhLogoImage, scale, scale);
						logoCell.add(crhLogoImageLayout);
					}
					else {
						logoCell.add(emptyParagraph);
					}

				// Row 2, Col 1: Empty cell
				table.add(emptyCell);

				// Row 2, Col 2: Empty cell
				table.add(emptyCell);

				// Row 2, Col 3: Date cell
				Cell dateCell = new Cell();
				table.add(dateCell);
				dateCell.setHorizontalPadding(padding);
				dateCell.setVerticalPadding(padding);
					Paragraph dateParagraph = new Paragraph(Alignment.RIGHT, pdfGen.getFont(),
							reportDateFormatted);
					dateCell.add(dateParagraph);

				// Row 2, Col 4: Time cell
				Cell timeCell = new Cell();
				table.add(timeCell);
				timeCell.setHorizontalPadding(padding);
				timeCell.setVerticalPadding(padding);
					Paragraph timeParagraph = new Paragraph(Alignment.RIGHT, pdfGen.getFont(),
							reportTimeFormatted);
					timeCell.add(timeParagraph);

			table.attachTo(container);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}


	public void printBody(PdfGen pdfGen, Container container, String plannedStartDayFormatted,
			Map<Long, Map<Long, List<TruckRevenuePlanningEntity>>> trucksByCarrier,
			Map<Long, List<TruckRevenuePlanMatEntity>> planningMatsByOrder,
			Map<Long, String> carrierNamesById, Map<Long, String> truckNamesById,
			boolean includeFreightRates)
	throws PrinterException {
		// [[
		Object[][] params = {{"pdfGen", pdfGen},
							{"container", container},
							{"plannedStartDayFormatted", plannedStartDayFormatted},
							{"trucksByCarrier", trucksByCarrier},
							{"planningMatsByOrder", planningMatsByOrder},
							{"carrierNamesById", carrierNamesById},
							{"truckNamesById", truckNamesById},
							{"includeFreightRates", includeFreightRates + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "printBody", params);
		}
		// ]]
		try {
			Font font = pdfGen.getFont();
			Font boldFont = pdfGen.getAlternateFont();

			int nrOfColumns = (includeFreightRates ? NR_OF_COLUMNS_WITH_FREIGHT_RATES
					: NR_OF_COLUMNS_WITHOUT_FREIGHT_RATES);

			Table bodyTable = new Table(nrOfColumns);
			bodyTable.setHeaderRows(3);
			bodyTable.setRenderHeaderRowsOnNewPage(true);

				// A full line of headers
				addCell(bodyTable, HEADER_BORDER_TOP, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, boldFont,
						Message.lookup(locale, "truckrevenueplanning.header.date"));
				addCell(bodyTable, HEADER_BORDER_TOP, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, boldFont,
						Message.lookup(locale, "truckrevenueplanning.header.ordernumber"));
				addCellWithAlignment(bodyTable, HEADER_BORDER_TOP, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, boldFont,
						Message.lookup(locale, "truckrevenueplanning.header.validationcode"), Alignment.LEFT, VALIDATION_CODE_COLUMN_WIDTH);
				addCellWithAlignment(bodyTable, HEADER_BORDER_TOP, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, boldFont,
						Message.lookup(locale, "truckrevenueplanning.header.orderitemname"), Alignment.LEFT, ARTICLE_COLUMN_WIDTH);
				addCell(bodyTable, HEADER_BORDER_TOP, HORIZONTAL_PADDING_3POINT, VERTICAL_PADDING_5POINT, boldFont,
						Message.lookup(locale, "truckrevenueplanning.header.plant"));
				addCell(bodyTable, HEADER_BORDER_TOP, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, boldFont,
						Message.lookup(locale, "truckrevenueplanning.header.planneddeliverytime"));
				addCell(bodyTable, HEADER_BORDER_TOP, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, boldFont,
						Message.lookup(locale, "truckrevenueplanning.header.deliverytime"));
				addCell(bodyTable, HEADER_BORDER_TOP, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, boldFont,
						Message.lookup(locale, "truckrevenueplanning.header.shiptoname"));
				addCell(bodyTable, HEADER_BORDER_TOP, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, boldFont,
						Message.lookup(locale, "truckrevenueplanning.header.shiptoaddress"));
				addCell(bodyTable, HEADER_BORDER_TOP, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, boldFont,
						Message.lookup(locale, "truckrevenueplanning.header.quantity"));

				if (includeFreightRates) {
					addCell(bodyTable, HEADER_BORDER_TOP, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, boldFont,
							Message.lookup(locale, "truckrevenueplanning.header.ca"));
				}

				// 2nd and 3rd line for additional headers
				addCell(bodyTable, null, HORIZONTAL_PADDING_10POINT, VERTICAL_PADDING_0POINT, boldFont,
						Message.lookup(locale, "truckrevenueplanning.header.carrier"), nrOfColumns);
				addCell(bodyTable, HEADER_BORDER_BOTTOM, HORIZONTAL_PADDING_20POINT, VERTICAL_PADDING_5POINT, boldFont,
						Message.lookup(locale, "truckrevenueplanning.header.truck"), nrOfColumns);

				// Date
				addCell(bodyTable, BODY_BORDER, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, boldFont, plannedStartDayFormatted,
						nrOfColumns);

				int nrOfDeliveriesPerDay = 0;
				BigDecimal totalRevenuePerDay = BigDecimal.ZERO;
				String freightCostCurrencyCdPerDay = null;

				for (Long carrierId: trucksByCarrier.keySet()) {
					String carrierName = carrierNamesById.get(carrierId);

					// Name of carrier
					addCell(bodyTable, BODY_BORDER, HORIZONTAL_PADDING_10POINT, VERTICAL_PADDING_5POINT, boldFont, carrierName,
							nrOfColumns);

					int nrOfDeliveriesPerCarrier = 0;
					BigDecimal totalRevenuePerCarrier = BigDecimal.ZERO;
					String freightCostCurrencyCdPerCarrier = null;

					Map<Long, List<TruckRevenuePlanningEntity>> ordersByTruck =
						trucksByCarrier.get(carrierId);

					for (Long truckId: ordersByTruck.keySet()) {
						String truckName = truckNamesById.get(truckId);

						// Name of truck
						addCell(bodyTable, BODY_BORDER, HORIZONTAL_PADDING_20POINT, VERTICAL_PADDING_5POINT, boldFont, truckName,
								nrOfColumns);

						// Order items per truck
						List<TruckRevenuePlanningEntity> orders = ordersByTruck.get(truckId);
						int nrOfDeliveriesPerTruck = 0;
						BigDecimal totalRevenuePerTruck = BigDecimal.ZERO;
						String freightCostCurrencyCdPerTruck = null;

						for (TruckRevenuePlanningEntity planning: orders) {
							Long orderId = planning.getOrderId();
							boolean hasComment = !StringUtils.empty(planning.getSchedulingComment());
							BigDecimal freightCost = planning.getFreightCost();
							
							/*
							 * NOTE (stich Jan 31, 2012): From now on, we'll not make any assumptions
							 * about currency anymore. We will show order items in their original currency
							 * and calculate/display sums only when all values are non-null and in the
							 * same currency.
							 * In the following, a totalRevenuePerXXX == null serves as test whether
							 * summing has been aborted for the current branch of the tree.
							 * NOTE (romek Sep 5, 2011): For now we will just assume that freight cost
							 * currency is the same for all orders per day, as it does not seem to be
							 * feasible that we would have to cope with multiple currencies in reality.
							 * If we need currency conversion, such functionality should already exist
							 * in Logon. (I have not yet checked myself.)
							 */
							if (includeFreightRates && totalRevenuePerTruck != null) {
								if (freightCostCurrencyCdPerTruck == null) {
									freightCostCurrencyCdPerTruck = planning.getFreightCostCurrencyCd();
								}
								else if (!freightCostCurrencyCdPerTruck.equals(planning.getFreightCostCurrencyCd())) {
									// summing over different currencies not possible, deactivate the whole branch
									totalRevenuePerTruck = null;
									totalRevenuePerCarrier = null;
									totalRevenuePerDay = null;
								}
							}

							TimeZone shipToTz = new TimeZone(planning.getShiptoTimezoneCd() != null
																	 ? planning.getShiptoTimezoneCd()
																	 : DateTimeZoneFactory.getDefaultTimeZoneId(),
															 locale);

							String formattedDeliveryFrom = shipToTz.fmtAsHourMinuteWithOffset(
									planning.getDeliveryFromDat());
							String formattedDeliveryTo = shipToTz.fmtAsHourMinuteWithOffset(
									planning.getDeliveryToDat());

							List<TruckRevenuePlanMatEntity> planningMats = planningMatsByOrder.get(orderId);

							if (planningMats != null) {
								int planningMatsSize = planningMats.size();

								for (int i = 0; i < planningMatsSize; i++) {
									TruckRevenuePlanMatEntity planningMat = planningMats.get(i);
									Border border = (!hasComment || i < planningMatsSize - 1
											? BODY_BORDER : null);

									// Order/material data
									addCell(bodyTable, border, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, font, "");

									addCell(bodyTable, border, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, boldFont,
											i == 0 ? String.valueOf(planning.getOrderNumber()) : "");

									addCellWithAlignment(bodyTable, border, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, font,
											i == 0 ? planning.getOrderValidationCode() : "", Alignment.LEFT, VALIDATION_CODE_COLUMN_WIDTH);

									addCellWithAlignment(bodyTable, border, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, font,
											planningMat.getOrderItemName(), Alignment.LEFT, ARTICLE_COLUMN_WIDTH);

									addCell(bodyTable, border, HORIZONTAL_PADDING_3POINT, VERTICAL_PADDING_5POINT, boldFont,
											i == 0 ? planning.getShipptPlant() : "");

									addCellWithAlignment(bodyTable, border, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, boldFont,
											i == 0 ? TruckRevenuePlanningCommons.formatPlannedArrival(
													 planning.getPlannedStartOfUnloading(), shipToTz) : "", Alignment.CENTER, PLANNED_DELIVERY_COLUMN_WIDTH);

									addCell(bodyTable, border, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, boldFont,
											i == 0 ? (formattedDeliveryFrom + " - " + formattedDeliveryTo)
													: "");
									addCell(bodyTable, border, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, font,
											i == 0 ? planning.getShiptoName1() : "");

									addCell(bodyTable, border, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, font,
											i == 0 ? TruckRevenuePlanningCommons.buildAddress(planning)
													: "");
									addCell(bodyTable, border, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, font,
											TruckRevenuePlanningCommons.formatQuantity(
													planningMat.getOrderedQuantity(), locale)
											+ " " + planningMat.getOrderedUomTxt());

									if (includeFreightRates) {
										if (freightCost != null) {
											BigDecimal formattedFreightCost = freightCost.setScale(
													FREIGHT_COST_ROUNDING_SCALE, FREIGHT_COST_ROUNDING_MODE);

											addCell(bodyTable, border, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, font,
													i == 0 ? (formattedFreightCost + " "
															+ planning.getFreightCostCurrencyCd()) : "");
										}
										else {
											totalRevenuePerTruck = null;
											totalRevenuePerCarrier = null;
											totalRevenuePerDay = null;

											addCell(bodyTable, border, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, font,
													i == 0 ? "N/A" : "");
										}
									}
								}
							}
							else {
								// This should not happen.
								throw new LogonException("Can't find materials for order_id " + orderId
										+ " in the planning data (truck_id " + truckId
										+ ", carrier_id " + carrierId + ", day " + plannedStartDayFormatted
										+ ")", Reason.RES_ERROR_IMPL);
							}

							if (hasComment) {
								addCell(bodyTable, BODY_BORDER, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, font,
										Message.lookup(locale, "truckrevenueplanning.order.schedulingcomment") + ":");
								addCell(bodyTable, BODY_BORDER, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, font,
										planning.getSchedulingComment(), nrOfColumns - 1);
							}

							nrOfDeliveriesPerTruck++;

							if (includeFreightRates && totalRevenuePerTruck != null) {
								totalRevenuePerTruck = totalRevenuePerTruck.add(freightCost);
							}
						}

						// Total number of orders and revenue per truck
						addCell(bodyTable, null, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, font, "",
								(includeFreightRates ? 6 : 7));
						addCell(bodyTable, null, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, boldFont,
								Message.lookup(locale, "truckrevenueplanning.summary.numberofdeliveries"), 2);
						addCell(bodyTable, null, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, boldFont,
								String.valueOf(nrOfDeliveriesPerTruck));

						if (includeFreightRates) {
							addCell(bodyTable, null, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, boldFont,
									Message.lookup(locale, "truckrevenueplanning.summary.totalrevenue"));

							if (totalRevenuePerTruck != null) {
								BigDecimal formattedTotalFreightCost = totalRevenuePerTruck.setScale(
										FREIGHT_COST_ROUNDING_SCALE, FREIGHT_COST_ROUNDING_MODE);

								addCell(bodyTable, null, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, boldFont,
										formattedTotalFreightCost + " " + freightCostCurrencyCdPerTruck);
							}
							else {
								addCell(bodyTable, null, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, boldFont, "N/A");
							}
						}

						nrOfDeliveriesPerCarrier += nrOfDeliveriesPerTruck;

						if (includeFreightRates && totalRevenuePerCarrier != null) {
							if (freightCostCurrencyCdPerCarrier == null) {
								freightCostCurrencyCdPerCarrier = freightCostCurrencyCdPerTruck;
								totalRevenuePerCarrier = totalRevenuePerCarrier.add(totalRevenuePerTruck);
							}
							else if (!freightCostCurrencyCdPerCarrier.equals(freightCostCurrencyCdPerTruck)) {
								totalRevenuePerCarrier = null;
								totalRevenuePerDay = null;
							}
							else {
								totalRevenuePerCarrier = totalRevenuePerCarrier.add(totalRevenuePerTruck);
							}
						}
					}

					// Total number of orders and revenue per carrier
					addCell(bodyTable, BODY_BORDER, HORIZONTAL_PADDING_20POINT, VERTICAL_PADDING_5POINT, font,
							Message.lookup(locale, "truckrevenueplanning.summary.nrofdeliveriespercarrier")
							+ ":", 3);
					addCell(bodyTable, BODY_BORDER, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, boldFont,
							String.valueOf(nrOfDeliveriesPerCarrier), (includeFreightRates ? 2 : 7));

					if (includeFreightRates) {
						addCell(bodyTable, BODY_BORDER, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, font,
								Message.lookup(locale, "truckrevenueplanning.summary.totalrevenuepercarrier")
								+ ":", 2);
						if (totalRevenuePerCarrier != null) {
							BigDecimal formattedTotalFreightCostPerCarrier = totalRevenuePerCarrier.setScale(
									FREIGHT_COST_ROUNDING_SCALE, FREIGHT_COST_ROUNDING_MODE);

							addCell(bodyTable, BODY_BORDER, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, boldFont,
									formattedTotalFreightCostPerCarrier + " " + freightCostCurrencyCdPerCarrier, 4);
						}
						else {
							addCell(bodyTable, BODY_BORDER, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, boldFont, "N/A", 4);
						}
					}

					nrOfDeliveriesPerDay += nrOfDeliveriesPerCarrier;

					if (includeFreightRates && totalRevenuePerDay != null) {
						if (freightCostCurrencyCdPerDay == null) {
							freightCostCurrencyCdPerDay = freightCostCurrencyCdPerCarrier;
							totalRevenuePerDay = totalRevenuePerDay.add(totalRevenuePerCarrier);
						}
						else if (!freightCostCurrencyCdPerDay.equals(freightCostCurrencyCdPerCarrier)) {
							totalRevenuePerDay = null;
						}
						else {
							totalRevenuePerDay = totalRevenuePerDay.add(totalRevenuePerCarrier);
						}
					}
				}

				// Total number of orders and revenue per day
				addCell(bodyTable, SUMMARY_BORDER, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, font, "",
						(includeFreightRates ? 6 : 7));
				addCell(bodyTable, SUMMARY_BORDER, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, boldFont,
						Message.lookup(locale, "truckrevenueplanning.summary.numberofdeliveries"), 2);
				addCell(bodyTable, SUMMARY_BORDER, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, boldFont,
						String.valueOf(nrOfDeliveriesPerDay));

				if (includeFreightRates) {
					addCell(bodyTable, SUMMARY_BORDER, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, boldFont,
							Message.lookup(locale, "truckrevenueplanning.summary.totalrevenue"));
					if (totalRevenuePerDay != null) {
						BigDecimal formattedTotalFreightCostPerDay = totalRevenuePerDay.setScale(
								FREIGHT_COST_ROUNDING_SCALE, FREIGHT_COST_ROUNDING_MODE);

						addCell(bodyTable, SUMMARY_BORDER, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, boldFont,
								formattedTotalFreightCostPerDay + " " + freightCostCurrencyCdPerDay);
					}
					else {
						addCell(bodyTable, SUMMARY_BORDER, HORIZONTAL_PADDING_1POINT, VERTICAL_PADDING_5POINT, boldFont, "N/A");
					}
				}

			bodyTable.attachTo(container);
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
	 * Add a cell to the given table.
	 *
	 * @param table
	 *            the table to put the cell into
	 * @param border
	 *            optional border
	 * @param hPadding
	 *            horizontal padding
	 * @param vPadding
	 *            vertical padding
	 * @param font
	 *            font of the text to print inside the cell
	 * @param text
	 *            text to print inside the cell
	 * @param colSpan
	 *            number of columns to span, null or 1 for no span
	 * @param alignment
	 *            horizontal alignment of the content
	 * @param width
	 *            width of the content
	 * @throws PrinterException
	 */
	private void addCell(Table table, Border border, double hPadding,
			double vPadding, Font font, String text, Integer colSpan,
			Alignment alignment, Double width) throws PrinterException {
		// [[
		Object[][] params = { { "table", table }, { "border", border },
				{ "hPadding", hPadding + "" }, { "vPadding", vPadding + "" },
				{ "font", font }, { "text", text }, { "colSpan", colSpan },
				{ "alignment", alignment }, { "width", width } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(TruckRevenuePlanningPdfHelper.class, "addCell", params);
		}
		// ]]
		try {
			Cell cell;

			if (colSpan == null) {
				cell = new Cell();
			} else {
				cell = new Cell(colSpan, 1);
			}

			cell.setHAlignment(alignment);
			cell.setWidth(width);

			table.add(cell);

			if (border != null) {
				cell.setBorder(border);
			}

			cell.setHorizontalPadding(hPadding);
			cell.setVerticalPadding(vPadding);

			String transliteratedValue = transliterate(text);
			Paragraph paragraph1 = new Paragraph(font, transliteratedValue);
			paragraph1.setAlignment(alignment);
			paragraph1.setWidth(width);

			cell.add(paragraph1);
		} finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}


	/**
	 * Add a cell to the given table using left alignment.
	 *
	 * @param table the table to put the cell into
	 * @param border optional border
	 * @param hPadding horizontal padding
	 * @param vPadding vertical padding
	 * @param font font of the text to print inside the cell
	 * @param text text to print inside the cell
	 * @param colSpan number of columns to span, null or 1 for no span
	 * @throws PrinterException
	 */
	private void addCell(Table table, Border border, double hPadding, double vPadding, Font font,
			String text, Integer colSpan)
	throws PrinterException {
		addCell(table,border,hPadding,vPadding,font,text,colSpan,Alignment.LEFT);
	}

	/**
	 * Add a cell to the given table.
	 *
	 * @param table the table to put the cell into
	 * @param border optional border
	 * @param hPadding horizontal padding
	 * @param vPadding vertical padding
	 * @param font font of the text to print inside the cell
	 * @param text text to print inside the cell
	 * @param colSpan number of columns to span, null or 1 for no span
	 * @param alignment horizontal alignment of the content
	 * @throws PrinterException
	 */
	private void addCell(Table table, Border border, double hPadding, double vPadding, Font font,
			String text, Integer colSpan, Alignment alignment)
	throws PrinterException {
		// [[
		Object[][] params = {{"table", table},
							{"border", border},
							{"hPadding", hPadding + ""},
							{"vPadding", vPadding + ""},
							{"font", font},
							{"text", text},
							{"colSpan", colSpan},
							{"alignment", alignment}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(TruckRevenuePlanningPdfHelper.class, "addCell", params);
		}
		// ]]
		try {
			Cell cell;

			if (colSpan == null) {
				cell = new Cell();
			}
			else {
				cell = new Cell(colSpan, 1);
			}

			cell.setHAlignment(alignment);

			table.add(cell);

			if (border != null) {
				cell.setBorder(border);
			}

			cell.setHorizontalPadding(hPadding);
			cell.setVerticalPadding(vPadding);

			String transliteratedValue = transliterate(text);
			Paragraph paragraph1 = new Paragraph(font, transliteratedValue);
			cell.add(paragraph1);
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
	 * Add a cell to the given table. Same as {@link #addCell(Table, Border, double, double, Font, String,
	 * Integer, Integer)} but with automatic width calculation and no column span.
	 *
	 * @param table the table to put the cell into
	 * @param border optional border
	 * @param hPadding horizontal padding
	 * @param vPadding vertical padding
	 * @param font font of the text to print inside the cell
	 * @param text text to print inside the cell
	 * @throws PrinterException
	 *
	 * @adnlogger.notrace
	 */
	private void addCell(Table table, Border border, double hPadding, double vPadding, Font font,
			String text)
	throws PrinterException {
		addCell(table, border, hPadding, vPadding, font, text, null);
	}

	/**
	 * Add a cell to the given table. Same as {@link #addCell(Table, Border, double, double, Font, String,
	 * Integer, Integer)} but with automatic width calculation and no column span and alignment.
	 *
	 * @param table the table to put the cell into
	 * @param border optional border
	 * @param hPadding horizontal padding
	 * @param vPadding vertical padding
	 * @param font font of the text to print inside the cell
	 * @param text text to print inside the cell
	 * @param alignment horizontal alignment of the content
	 * @throws PrinterException
	 *
	 * @adnlogger.notrace
	 */
	private void addCellWithAlignment(Table table, Border border, double hPadding, double vPadding, Font font,
			String text,Alignment alignment)
	throws PrinterException {
		addCell(table, border, hPadding, vPadding, font, text, null, alignment);
	}

	/**
	 * Add a cell to the given table. Same as {@link #addCell(Table, Border, double, double, Font, String,
	 * Integer, Integer)} but with automatic width calculation and no column span and alignment.
	 *
	 * @param table the table to put the cell into
	 * @param border optional border
	 * @param hPadding horizontal padding
	 * @param vPadding vertical padding
	 * @param font font of the text to print inside the cell
	 * @param text text to print inside the cell
	 * @param alignment horizontal alignment of the content
	 * @param width width of the content
	 * @throws PrinterException
	 *
	 * @adnlogger.notrace
	 */
	private void addCellWithAlignment(Table table, Border border, double hPadding, double vPadding, Font font,
			String text,Alignment alignment, Double width)
	throws PrinterException {
		addCell(table, border, hPadding, vPadding, font, text, null, alignment, width);
	}

	private String transliterate(String input) {
		return transliterator.transliterate(input);
	}
}
