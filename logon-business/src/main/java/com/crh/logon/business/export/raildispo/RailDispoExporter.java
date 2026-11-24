/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.export.raildispo;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.crh.logon.business.services.beans.RailDispoExportOrderRow;
import com.crh.logon.business.services.beans.RailDispoExportParameters;
import com.crh.logon.business.services.beans.RailDispoExportWagonRow;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.views.DispoPlantTrackListEntity;
import com.crh.logon.persistence.entities.views.RailPlantEntity;
import com.crh.logon.persistence.entities.views.RailPlantSpEntity;

/**
 * @author AdNovum Informatik AG
 */
public class RailDispoExporter {

	public static final String WAGON_STATUS_TEXT_LOADED = "raildispo.texte.status.loaded";
	public static final String WAGON_STATUS_TEXT_AT_SHIPPINGPOINT = "raildispo.texte.status.atSp";
	public static final String WAGON_STATUS_TEXT_INCOMING = "raildispo.texte.status.incoming";
	public static final String WAGON_STATUS_TEXT_SUBMITTED = "raildispo.texte.status.transmitted";

	public static final String WAGON_SHEET_MSG_KEY = "application.label.wagon";
	public static final String ORDER_SHEET_MSG_KEY = "raildispo.export.orders.unallocated";

	public static final String PLANT_LABEL = "raildispo.texte.plant";
	public static final String EXPORTING_USER_LABEL = "raildispo.export.exportinguser";
	public static final String TIMEZONE_LABEL = "application.label.timezone";
	public static final String TIME_OF_EXPORT_LABEL = "raildispo.export.timeofexport";
	public static final String SP_AND_TRACKS_INC_LABEL = "raildispo.export.shippingpointsandtracksincluded";
	public static final String WAGON_STATUS_INC_LABEL = "raildispo.export.wagonstatusesincluded";

	// Order Group Columns
	public static final String ORDTAB_GRP_ORDER_COLUMN_LABEL = "dispo.texte.order";
	public static final String ORDTAB_GRP_SHIPPING_POINT_COLUMN_LABEL = "raildispo.texte.shippingpoints";
	public static final String ORDTAB_GRP_SOLDTO_COLUMN_LABEL = "raildispo.texte.customer";
	public static final String ORDTAB_GRP_SHIPTO_COLUMN_LABEL = "raildispo.texte.Shipto";
	public static final String ORDTAB_GRP_DELIVERY_WINDOW_COLUMN_LABEL = "raildispo.export.deliverywindow";
	public static final String ORDTAB_GRP_CARRIER_COLUMN_LABEL = "raildispo.texte.Carrier";
	public static final String ORDTAB_GRP_SHIPTO_WAGON_PREFS_COLUMN_LABEL = "raildispo.export.shipto.wagon.preferences";
	public static final String ORDTAB_GRP_NOTES_COLUMN_LABEL = "application.label.notes";

	// Order Columns 
	public static final String ORDTAB_ORDER_NUMBER_COLUMN_LABEL = "application.label.number";
	public static final String ORDTAB_ORDER_MATERIAL_COLUMN_LABEL = "dispo.texte.Product";
	public static final String ORDTAB_ORDER_QUANTITY_COLUMN_LABEL = "dispo.texte.quantity";
	public static final String ORDTAB_SHIPPING_PT_NAME_COLUMN_LABEL = "application.label.name"; 
	public static final String ORDTAB_SHIPPING_PT_LOADING_PT_COLUMN_LABEL = "dispo.texte.Loadingpoint";
	public static final String ORDTAB_SHIPPING_PT_RAILWAY_STATION_COLUMN_LABEL = "railwaystation.popup.title";
	public static final String ORDTAB_SHIPPING_PT_COUNTRY_COLUMN_LABEL = "dispo.texte.country";
	public static final String ORDTAB_SOLDTO_NR_COLUMN_LABEL = "application.label.id";
	public static final String ORDTAB_SOLDTO_NAME_COLUMN_LABEL = "application.label.name";
	public static final String ORDTAB_SHIPTO_NR_COLUMN_LABEL = "application.label.id";
	public static final String ORDTAB_SHIPTO_NAME_COLUMN_LABEL = "application.label.name";  
	public static final String ORDTAB_SHIPTO_RAILWAY_STATION_COLUMN_LABEL = "railwaystation.popup.title";
	public static final String ORDTAB_SHIPTO_COUNTRY_COLUMN_LABEL = "dispo.texte.country";
	public static final String ORDTAB_DEPARTURE_TIME_COLUMN_LABEL = "raildispo.texte.SwitchingTimeDep";
	public static final String ORDTAB_DELIVERY_WINDOW_FROM_COLUMN_LABEL = "dispo.texte.assignmentfrom";
	public static final String ORDTAB_DELIVERY_WINDOW_TO_COLUMN_LABEL = "dispo.texte.assignmentto";
	public static final String ORDTAB_CARRIER_NAME_COLUMN_LABEL = "application.label.name";
	public static final String ORDTAB_WAGON_PREFS_COLUMN_LABEL = "";
	public static final String ORDTAB_WEIGHING_SYSTEM_COLUMN_LABEL = "application.label.weighingSystem";
	public static final String ORDTAB_SCHEDULING_COLUMN_LABEL = "dispo.texte.disposition";

	// Wagon Group Columns
	public static final String WAGONTAB_GRP_WAGON_COLUMN_LABEL = "application.label.wagon";
	public static final String WAGONTAB_GRP_ORDER_ALLOCATED_COLUMN_LABEL = "raildispo.export.order.allocated";
	public static final String WAGONTAB_GRP_SHIPPING_POINT_COLUMN_LABEL = "raildispo.texte.shippingpoints";
	public static final String WAGONTAB_GRP_SOLDTO_COLUMN_LABEL = "raildispo.texte.customer";
	public static final String WAGONTAB_GRP_SHIPTO_COLUMN_LABEL = "raildispo.texte.Shipto";
	public static final String WAGONTAB_GRP_DELIVERY_WINDOW_COLUMN_LABEL = "raildispo.export.deliverywindow";
	public static final String WAGONTAB_GRP_CARRIER_COLUMN_LABEL = "raildispo.texte.Carrier";
	public static final String WAGONTAB_GRP_LOAD_COLUMN_LABEL = "application.label.load";
	public static final String WAGONTAB_GRP_LOG_COLUMN_LABEL = "raildispo.texte.disposition";
	public static final String WAGONTAB_GRP_WEIGHING_SYSTEM_NOTE_COLUMN_LABEL = "wagon.note_railway_spedition";
	public static final String WAGONTAB_GRP_SCHEDULING_NOTE_COLUMN_LABEL = "raildispo.export.noteForScheduling";

	// Wagon Columns 
	public static final String WAGONTAB_WAGON_NUMBER_COLUMN_LABEL = "application.label.number";
	public static final String WAGONTAB_WAGON_KEEPER_COLUMN_LABEL = "wagon.keeper_name";
	public static final String WAGONTAB_WAGON_TYPE_COLUMN_LABEL = "raildispo.texte.RailcarType";
	public static final String WAGONTAB_WAGON_WRITTEN_TARE_WEIGHT_T = "raildispo.export.written.tare";
	public static final String WAGONTAB_WAGON_TRACK_COLUMN_LABEL = "raildispo.texte.Track";
	public static final String WAGONTAB_WAGON_POSITION_COLUMN_LABEL = "raildispo.texte.Position";
	public static final String WAGONTAB_WAGON_CONTAINER_COLUMN_LABEL = "raildispo.texte.Container";
	public static final String WAGONTAB_WAGON_STATUS_COLUMN_LABEL = "raildispo.texte.wagonstatus";
	public static final String WAGONTAB_ORDER_NUMBER_COLUMN_LABEL = "application.label.number";
	public static final String WAGONTAB_ORDER_ETRAN_OUT_COLUMN_LABEL = "raildispo.etran.out";
	public static final String WAGONTAB_ORDER_ETRAN_RET_COLUMN_LABEL = "raildispo.etran.return";
	public static final String WAGONTAB_ORDER_MATERIAL_COLUMN_LABEL = "dispo.texte.Product";
	public static final String WAGONTAB_ORDER_QUANTITY_COLUMN_LABEL = "dispo.texte.quantity";
	public static final String WAGONTAB_SHIPPING_PT_NAME_COLUMN_LABEL = "application.label.name"; 
	public static final String WAGONTAB_SHIPPING_PT_LOADING_PT_COLUMN_LABEL = "dispo.texte.Loadingpoint";
	public static final String WAGONTAB_SHIPPING_PT_RAILWAY_STATION_COLUMN_LABEL = "railwaystation.popup.title";
	public static final String WAGONTAB_SHIPPING_PT_COUNTRY_COLUMN_LABEL = "dispo.texte.country";
	public static final String WAGONTAB_SOLDTO_NR_COLUMN_LABEL = "application.label.id";
	public static final String WAGONTAB_SOLDTO_NAME_COLUMN_LABEL = "application.label.name";
	public static final String WAGONTAB_SHIPTO_NR_COLUMN_LABEL = "application.label.id";
	public static final String WAGONTAB_SHIPTO_NAME_COLUMN_LABEL = "application.label.name"; 
	public static final String WAGONTAB_SHIPTO_RAILWAY_STATION_COLUMN_LABEL = "railwaystation.popup.title";
	public static final String WAGONTAB_SHIPTO_COUNTRY_COLUMN_LABEL = "dispo.texte.country";
	public static final String WAGONTAB_DEPARTURE_TIME_COLUMN_LABEL = "raildispo.texte.SwitchingTimeDep";
	public static final String WAGONTAB_DELIVERY_WINDOW_FROM_COLUMN_LABEL = "dispo.texte.assignmentfrom";
	public static final String WAGONTAB_DELIVERY_WINDOW_TO_COLUMN_LABEL = "dispo.texte.assignmentto";
	public static final String WAGONTAB_CARRIER_NAME_COLUMN_LABEL = "application.label.name";
	public static final String WAGONTAB_LOAD_MATERIAL_COLUMN_LABEL = "dispo.texte.Product";
	public static final String WAGONTAB_LOAD_TARE_COLUMN_LABEL = "pdf.weights.tare";
	public static final String WAGONTAB_LOAD_NET_COLUMN_LABEL = "pdf.weights.net";
	public static final String WAGONTAB_LOAD_GROSS_COLUMN_LABEL = "pdf.weights.gross";
	public static final String WAGONTAB_LOAD_NHM_COLUMN_LABEL = "pdf.nhm.code";
	public static final String WAGONTAB_LOG_TO_RAIL_CO_COLUMN_LABEL = "raildispo.export.to.rail.company";
	public static final String WAGONTAB_LOG_DEPARTURE_COLUMN_LABEL = "raildispo.texte.Departure";
	public static final String WAGONTAB_LOG_ARRIVAL_COLUMN_LABEL = "raildispo.texte.Arrival";
	public static final String WAGONTAB_ORDER_REMARKS_COLUMN_LABEL = "dispo.texte.order";
	public static final String WAGONTAB_WAGON_REMARKS_COLUMN_LABEL = "application.label.wagon";

	public static final int COLUMN_LABEL_ROW_NUM = 5;
	public static final int FROZEN_PANE_LABEL_ROW_NUM = 6;

	public static final String CELL_FORMAT_WAGON_NUM = "0000-0000-000-0";
	public static final String CELL_FORMAT_INT = "0";
	public static final String CELL_FORMAT_DEC = "0.0";
	public static final String CELL_FORMAT_DATE = "dd.MM.yyyy";
	public static final String CELL_FORMAT_DATE_TIME = "dd.MM.yyyy hh:mm";

	private static final short[] colors = new short[5];

	static final short COLOR_BG_INCOMING = 0;
	static final short COLOR_BG_DEFAULT = 1;
	static final short COLOR_BG_LOADED = 2;
	static final short COLOR_BG_SUBMITTED = 3;
	static final short COLOR_BG_ALTERNATE = 4;

	private static final short FONT_SIZE = 10;
	private static final String FONT_FAMILY = "Calibri";

	private HSSFWorkbook workBook;
	private CreationHelper createHelper;
	private HSSFPalette palette;
	private CellStyle regularStyle;
	private CellStyle boldStyle;
	private Font regularFont;
	private Font boldFont;

	private Map<CellStyleWrapper, CellStyle> cellStyleMap = new HashMap<CellStyleWrapper, CellStyle>();

	private RailDispoExportParameters parameters;

	private Set<String> spRailwayStationNames;
	private Set<Long> spRailwayStIds = new HashSet<Long>();
	private Set<String> statusText;

	private int cellIndex;
	private int rowIndex;

	class CellStyleWrapper {

		int backGroundColor;
		short cellAlignment;
		Font font;
		String format;

		public CellStyleWrapper(int backGroundColor, short cellAlignment, Font font, String format) {
			this.backGroundColor = backGroundColor;
			this.cellAlignment = cellAlignment;
			this.font = font;
			this.format = format;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + backGroundColor;
			result = prime * result + cellAlignment;
			result = prime * result + ((font == null) ? 0 : font.hashCode());
			result = prime * result + ((format == null) ? 0 : format.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CellStyleWrapper other = (CellStyleWrapper) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (backGroundColor != other.backGroundColor)
				return false;
			if (cellAlignment != other.cellAlignment)
				return false;
			if (font == null) {
				if (other.font != null)
					return false;
			}
			else if (!font.equals(other.font))
				return false;
			if (format == null) {
				if (other.format != null)
					return false;
			}
			else if (!format.equals(other.format))
				return false;
			return true;
		}

		private RailDispoExporter getOuterType() {
			return RailDispoExporter.this;
		}

	}

	public RailDispoExporter(RailDispoExportParameters parameters) {
		this.parameters = parameters;
	}

	public Workbook export() {

		this.workBook = new HSSFWorkbook();
		this.createHelper = workBook.getCreationHelper();
		this.palette = workBook.getCustomPalette();

		palette.setColorAtIndex(HSSFColor.GREY_40_PERCENT.index, (byte)217, (byte)217, (byte)217);
		palette.setColorAtIndex(HSSFColor.LIGHT_BLUE.index, (byte)194, (byte)219, (byte)243);
		palette.setColorAtIndex(HSSFColor.PLUM.index, (byte)151, (byte)151, (byte)240);
		palette.setColorAtIndex(HSSFColor.GREY_25_PERCENT.index, (byte)237, (byte)237, (byte)237);

		colors[COLOR_BG_INCOMING] = HSSFColor.GREY_40_PERCENT.index;
		colors[COLOR_BG_DEFAULT] = HSSFColor.WHITE.index;
		colors[COLOR_BG_LOADED] = HSSFColor.LIGHT_BLUE.index;
		colors[COLOR_BG_SUBMITTED] = HSSFColor.PLUM.index;
		colors[COLOR_BG_ALTERNATE] = HSSFColor.GREY_25_PERCENT.index;

		regularFont = workBook.createFont();
		regularFont.setFontHeightInPoints(FONT_SIZE);
		regularFont.setFontName(FONT_FAMILY);

		boldFont = workBook.createFont();
		boldFont.setFontHeightInPoints(FONT_SIZE);
		boldFont.setFontName(FONT_FAMILY);
		boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);

		regularStyle = workBook.createCellStyle();
		regularStyle.setFont(regularFont);

		boldStyle = workBook.createCellStyle();
		boldStyle.setFont(boldFont);

		buildShippingPointsTextSet();
		buildStatusesTexts();

		buildWagonSheet();
		buildOrderSheet();

		return workBook;

	}

	private void buildStatusesTexts() {

		statusText = new HashSet<String>();
		Map<Long, Boolean> statuses = parameters.getStatuses();
		if (statuses.get(Constant.WAGON_STATUS_AT_SHIPPINGPOINT)) {
			statusText.add(getWagonStatusText(Constant.WAGON_STATUS_AT_SHIPPINGPOINT));
		}
		if (statuses.get(Constant.WAGON_STATUS_INCOMING)) {
			statusText.add(getWagonStatusText(Constant.WAGON_STATUS_INCOMING));
		}
		if (statuses.get(Constant.WAGON_STATUS_LOADED)) {
			statusText.add(getWagonStatusText(Constant.WAGON_STATUS_LOADED));
		}
		if (statuses.get(Constant.WAGON_STATUS_SUBMITTED)) {
			statusText.add(getWagonStatusText(Constant.WAGON_STATUS_SUBMITTED));
		}

	}

	private void buildShippingPointsTextSet() {

		// Format: example: ADN Rail Münchenstein (V1, V3), ADN Rail Siggenthal
		spRailwayStationNames = new HashSet<String>();

		for (RailPlantSpEntity plantSp : parameters.getShippingPoints()) {
			// Save the first shippingPointsId
			spRailwayStIds.add(plantSp.getRailwayStationId());
			Set<String> plantTrackCdsOfShippingPt = new HashSet<String>();
			for (DispoPlantTrackListEntity trackEntity : parameters.getSelectedTracks()) {
				if (trackEntity.getPlantCd().equals(plantSp.getPlantCd())) {
					plantTrackCdsOfShippingPt.add(trackEntity.getTrackName());
				}
			}
			spRailwayStationNames.add(String.format("%s (%s)", plantSp.getShippingpointName(), StringUtils.join(plantTrackCdsOfShippingPt.toArray(), ", ")));
		}

	}

	private void buildOrderSheet() {

		Locale locale = parameters.getLocale();

		HSSFSheet orderSheet = createSheetWithName(ORDER_SHEET_MSG_KEY);

		buildSheetCommon(orderSheet);

		nextLine();

		Row fifthRow = orderSheet.createRow(rowIndex);

		Cell orderColumnLabelCell = createMergedRegionCell(orderSheet, fifthRow, 4, boldStyle);
		orderColumnLabelCell.setCellValue(getMessageByKey(locale, ORDTAB_GRP_ORDER_COLUMN_LABEL));

		Cell shippingPointsColumnLabelCell = createMergedRegionCell(orderSheet, fifthRow, 5, boldStyle);
		shippingPointsColumnLabelCell.setCellValue(getMessageByKey(locale, ORDTAB_GRP_SHIPPING_POINT_COLUMN_LABEL));

		Cell soldToColumnLabelCell = createMergedRegionCell(orderSheet, fifthRow, 3, boldStyle);
		soldToColumnLabelCell.setCellValue(getMessageByKey(locale, ORDTAB_GRP_SOLDTO_COLUMN_LABEL));

		Cell shipToColumnLabelCell = createMergedRegionCell(orderSheet, fifthRow, 7, boldStyle);
		shipToColumnLabelCell.setCellValue(getMessageByKey(locale, ORDTAB_GRP_SHIPTO_COLUMN_LABEL));

		Cell deliveryWindowColumnLabelCell = createMergedRegionCell(orderSheet, fifthRow, 2, boldStyle);
		deliveryWindowColumnLabelCell.setCellValue(getMessageByKey(locale, ORDTAB_GRP_DELIVERY_WINDOW_COLUMN_LABEL));

		Cell carrierColumnLabelCell = createMergedRegionCell(orderSheet, fifthRow, 1, boldStyle);
		carrierColumnLabelCell.setCellValue(getMessageByKey(locale, ORDTAB_GRP_CARRIER_COLUMN_LABEL));

		Cell shipToWagonPrefsColumnLabelCell = createMergedRegionCell(orderSheet, fifthRow, 1, boldStyle);
		shipToWagonPrefsColumnLabelCell.setCellValue(getMessageByKey(locale, ORDTAB_GRP_SHIPTO_WAGON_PREFS_COLUMN_LABEL));

		Cell orderRemarksColumnLabelCell = createMergedRegionCell(orderSheet, fifthRow, 2, boldStyle);
		orderRemarksColumnLabelCell.setCellValue(getMessageByKey(locale, ORDTAB_GRP_NOTES_COLUMN_LABEL));

		// 6th row
		nextLine();

		Row sixthRow = orderSheet.createRow(rowIndex);

		Cell orderNumberColumnLabelCell = createMergedRegionCell(orderSheet, sixthRow, 1, regularStyle);
		orderNumberColumnLabelCell.setCellValue(getMessageByKey(locale, ORDTAB_ORDER_NUMBER_COLUMN_LABEL));

		Cell orderMaterialColumnLabelCell = createMergedRegionCell(orderSheet, sixthRow, 1, regularStyle);
		orderMaterialColumnLabelCell.setCellValue(getMessageByKey(locale, ORDTAB_ORDER_MATERIAL_COLUMN_LABEL));

		Cell orderQuantityColumnLabelCell = createMergedRegionCell(orderSheet, sixthRow, 2, regularStyle);
		orderQuantityColumnLabelCell.setCellValue(getMessageByKey(locale, ORDTAB_ORDER_QUANTITY_COLUMN_LABEL));

		Cell shippingPtNameColumnLabelCell = createMergedRegionCell(orderSheet, sixthRow, 1, regularStyle);
		shippingPtNameColumnLabelCell.setCellValue(getMessageByKey(locale, ORDTAB_SHIPPING_PT_NAME_COLUMN_LABEL));

		Cell shippingPtLoadingPtColumnLabelCell = createMergedRegionCell(orderSheet, sixthRow, 1, regularStyle);
		shippingPtLoadingPtColumnLabelCell.setCellValue(getMessageByKey(locale, ORDTAB_SHIPPING_PT_LOADING_PT_COLUMN_LABEL));

		Cell shippingPtRailwayStColumnLabelCell = createMergedRegionCell(orderSheet, sixthRow, 2, regularStyle);
		shippingPtRailwayStColumnLabelCell.setCellValue(getMessageByKey(locale, ORDTAB_SHIPPING_PT_RAILWAY_STATION_COLUMN_LABEL));

		Cell shippingPtCountryColumnLabelCell = createMergedRegionCell(orderSheet, sixthRow, 1, regularStyle);
		shippingPtCountryColumnLabelCell.setCellValue(getMessageByKey(locale, ORDTAB_SHIPPING_PT_COUNTRY_COLUMN_LABEL));

		Cell soldToNrColumnLabelCell = createMergedRegionCell(orderSheet, sixthRow, 1, regularStyle);
		soldToNrColumnLabelCell.setCellValue(getMessageByKey(locale, ORDTAB_SOLDTO_NR_COLUMN_LABEL));

		Cell soldToNameColumnLabelCell = createMergedRegionCell(orderSheet, sixthRow, 1, regularStyle);
		soldToNameColumnLabelCell.setCellValue(getMessageByKey(locale, ORDTAB_SOLDTO_NAME_COLUMN_LABEL));

		Cell soldToName2ColumnLabelCell = createMergedRegionCell(orderSheet, sixthRow, 1, regularStyle);

		Cell shipToNrColumnLabelCell = createMergedRegionCell(orderSheet, sixthRow, 1, regularStyle);
		shipToNrColumnLabelCell.setCellValue(getMessageByKey(locale, ORDTAB_SHIPTO_NR_COLUMN_LABEL));

		Cell shipToNameColumnLabelCell = createMergedRegionCell(orderSheet, sixthRow, 1, regularStyle);
		shipToNameColumnLabelCell.setCellValue(getMessageByKey(locale, ORDTAB_SHIPTO_NAME_COLUMN_LABEL));

		Cell shipToName2ColumnLabelCell = createMergedRegionCell(orderSheet, sixthRow, 1, regularStyle);

		Cell shiptoRailwayStColumnLabelCell = createMergedRegionCell(orderSheet, sixthRow, 2, regularStyle);
		shiptoRailwayStColumnLabelCell.setCellValue(getMessageByKey(locale, ORDTAB_SHIPTO_RAILWAY_STATION_COLUMN_LABEL));

		Cell shiptoCountryColumnLabelCell = createMergedRegionCell(orderSheet, sixthRow, 1, regularStyle);
		shiptoCountryColumnLabelCell.setCellValue(getMessageByKey(locale, ORDTAB_SHIPTO_COUNTRY_COLUMN_LABEL));

		Cell departureTimeColumnLabelCell = createMergedRegionCell(orderSheet, sixthRow, 1, regularStyle);
		departureTimeColumnLabelCell.setCellValue(getMessageByKey(locale, ORDTAB_DEPARTURE_TIME_COLUMN_LABEL));

		Cell deliveryWindowFromColumnLabelCell = createMergedRegionCell(orderSheet, sixthRow, 1, regularStyle);
		deliveryWindowFromColumnLabelCell.setCellValue(getMessageByKey(locale, ORDTAB_DELIVERY_WINDOW_FROM_COLUMN_LABEL));

		Cell deliveryWindowToColumnLabelCell = createMergedRegionCell(orderSheet, sixthRow, 1, regularStyle);
		deliveryWindowToColumnLabelCell.setCellValue(getMessageByKey(locale, ORDTAB_DELIVERY_WINDOW_TO_COLUMN_LABEL));

		Cell carrierNameColumnLabelCell = createMergedRegionCell(orderSheet, sixthRow, 1, regularStyle);
		carrierNameColumnLabelCell.setCellValue(getMessageByKey(locale, ORDTAB_CARRIER_NAME_COLUMN_LABEL));

		Cell wagonPrefsColumnLabelCell = createMergedRegionCell(orderSheet, sixthRow, 1, regularStyle); // no cell value

		Cell weighingRemarksColumnLabelCell = createMergedRegionCell(orderSheet, sixthRow, 1, regularStyle);
		weighingRemarksColumnLabelCell.setCellValue(getMessageByKey(locale, ORDTAB_WEIGHING_SYSTEM_COLUMN_LABEL));

		Cell schedulingRemarksColumnLabelCell = createMergedRegionCell(orderSheet, sixthRow, 1, regularStyle);
		schedulingRemarksColumnLabelCell.setCellValue(getMessageByKey(locale, ORDTAB_SCHEDULING_COLUMN_LABEL));

		// From the first colum and just before the value rows we create a pane to separate values from headers
		orderSheet.createFreezePane(0, FROZEN_PANE_LABEL_ROW_NUM, 0, FROZEN_PANE_LABEL_ROW_NUM);

		Long lastOrderNo = null;
		int bgColor = COLOR_BG_ALTERNATE;

		List<RailDispoExportOrderRow> orderResult = parameters.getOrderResult();

		for (RailDispoExportOrderRow order : orderResult) {

			nextLine();

			Row valueRow = orderSheet.createRow(rowIndex);

			// We have to switch if it's a new order
			if (shouldSwitchColor(lastOrderNo, order.getOrderNumber())) {
				if (bgColor == COLOR_BG_ALTERNATE) {
					bgColor = COLOR_BG_DEFAULT;
				}
				else {
					bgColor = COLOR_BG_ALTERNATE;
				}
				lastOrderNo = order.getOrderNumber();
			}

			Cell orderNumberValueLabelCell = createNewCell(valueRow, bgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(orderNumberValueLabelCell, Long.toString(order.getOrderNumber()));

			Cell orderMaterialValueLabelCell = createNewCell(valueRow, bgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(orderMaterialValueLabelCell, order.getOrderMaterialName());

			Cell orderQuantityValueLabelCell = createNewCellUsingFormat(valueRow, CELL_FORMAT_INT, bgColor, CellStyle.ALIGN_RIGHT);
			setCellValueIfNotNull(orderQuantityValueLabelCell, order.getOrderQty());

			Cell orderUomValueLabelCell = createNewCell(valueRow, bgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(orderUomValueLabelCell, order.getOrderUom());

			Cell shippingPtNameValueLabelCell = createNewCell(valueRow, bgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(shippingPtNameValueLabelCell, order.getShippintPtName());

			Cell shippingPtLoadingPtValueLabelCell = createNewCell(valueRow, bgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(shippingPtLoadingPtValueLabelCell, order.getLoadingPtName());

			Cell shippingPtStationNoValueLabelCell = createNewCell(valueRow, bgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(shippingPtStationNoValueLabelCell, order.getShippintPtStationNo());

			Cell shippingPtStationNameValueLabelCell = createNewCell(valueRow, bgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(shippingPtStationNameValueLabelCell, order.getShippintPtStationName());

			Cell shippingPtCountryCdValueLabelCell = createNewCell(valueRow, bgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(shippingPtCountryCdValueLabelCell, order.getShippintPtCountryCd());

			Cell soldToNumberValueLabelCell = createNewCell(valueRow, bgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(soldToNumberValueLabelCell, order.getSoldToNumber());

			Cell soldToName1ValueLabelCell = createNewCell(valueRow, bgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(soldToName1ValueLabelCell, order.getSoldToName());

			Cell soldToName2ValueLabelCell = createNewCell(valueRow, bgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(soldToName2ValueLabelCell, order.getSoldToName2());

			Cell shipToNumberValueLabelCell = createNewCell(valueRow, bgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(shipToNumberValueLabelCell, order.getShipToNumber());

			Cell shipToName1ValueLabelCell = createNewCell(valueRow, bgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(shipToName1ValueLabelCell, order.getShipToName());

			Cell shipToName2ValueLabelCell = createNewCell(valueRow, bgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(shipToName2ValueLabelCell, order.getShipToName2());

			Cell shipToStationNoValueLabelCell = createNewCell(valueRow, bgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(shipToStationNoValueLabelCell, order.getShipToStationNo());

			Cell shipToStationNameValueLabelCell = createNewCell(valueRow, bgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(shipToStationNameValueLabelCell, order.getShipToStationName());

			Cell shipToCountryCdValueLabelCell = createNewCell(valueRow, bgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(shipToCountryCdValueLabelCell, order.getShipToCountryCd());

			Cell manupulationTimeValueLabelCell = createNewCellUsingFormat(valueRow, CELL_FORMAT_DATE_TIME, bgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(manupulationTimeValueLabelCell, order.getManipulationTime());

			Cell deliveryTimeFromValueLabelCell = createNewCellUsingFormat(valueRow, CELL_FORMAT_DATE, bgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(deliveryTimeFromValueLabelCell, order.getDeliveryFrom());

			Cell deliveryTimeToValueLabelCell = createNewCellUsingFormat(valueRow, CELL_FORMAT_DATE, bgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(deliveryTimeToValueLabelCell, order.getDeliveryTo());

			Cell carrierValueLabelCell = createNewCell(valueRow, bgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(carrierValueLabelCell, order.getCarrierName());

			Cell wagonPrefsValueLabelCell = createNewCell(valueRow, bgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(wagonPrefsValueLabelCell, order.getShipToPrefs());

			Cell noteRailwayDispValueLabelCell = createNewCell(valueRow, bgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(noteRailwayDispValueLabelCell, order.getNoteRailwayDisp());

			Cell noteDispatchingValueLabelCell = createNewCell(valueRow, bgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(noteDispatchingValueLabelCell, order.getNoteDispatching());

		}

		orderSheet.setAutoFilter(new CellRangeAddress(COLUMN_LABEL_ROW_NUM, rowIndex, 0, cellIndex - 1));

		for (int i = 0; i<cellIndex; i++) {
			orderSheet.autoSizeColumn(i);
		}

		orderSheet.setColumnWidth(RailDispoExporterColumnDescriptor.ORDER_WEIGHING_NOTE_ORDER.getColumnIndex(), RailDispoExporterColumnDescriptor.ORDER_WEIGHING_NOTE_ORDER.getCalculatedWidth());
		orderSheet.setColumnWidth(RailDispoExporterColumnDescriptor.ORDER_SCHEDULING_NOTE_ORDER.getColumnIndex(), RailDispoExporterColumnDescriptor.ORDER_SCHEDULING_NOTE_ORDER.getCalculatedWidth());

	}

	private void buildWagonSheet() {

		Locale locale = parameters.getLocale();

		HSSFSheet wagonSheet = createSheetWithName(WAGON_SHEET_MSG_KEY);

		buildSheetCommon(wagonSheet);

		nextLine();

		Row fifthRow = wagonSheet.createRow(rowIndex);

		Cell wagonColumnLabelCell = createMergedRegionCell(wagonSheet, fifthRow, 8, boldStyle);
		wagonColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_GRP_WAGON_COLUMN_LABEL));

		Cell allocatedOrderColumnLabelCell = createMergedRegionCell(wagonSheet, fifthRow, 6, boldStyle);
		allocatedOrderColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_GRP_ORDER_ALLOCATED_COLUMN_LABEL));

		Cell shippintPointsColumnLabelCell = createMergedRegionCell(wagonSheet, fifthRow, 5, boldStyle);
		shippintPointsColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_GRP_SHIPPING_POINT_COLUMN_LABEL));

		Cell soldToColumnLabelCell = createMergedRegionCell(wagonSheet, fifthRow, 3, boldStyle);
		soldToColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_GRP_SOLDTO_COLUMN_LABEL));

		Cell shipToColumnLabelCell = createMergedRegionCell(wagonSheet, fifthRow, 7, boldStyle);
		shipToColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_GRP_SHIPTO_COLUMN_LABEL));

		Cell deliverWindowColumnLabelCell = createMergedRegionCell(wagonSheet, fifthRow, 2, boldStyle);
		deliverWindowColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_GRP_DELIVERY_WINDOW_COLUMN_LABEL));

		Cell carrierColumnLabelCell = createMergedRegionCell(wagonSheet, fifthRow, 1, boldStyle);
		carrierColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_GRP_CARRIER_COLUMN_LABEL));

		Cell loadColumnLabelCell = createMergedRegionCell(wagonSheet, fifthRow, 5, boldStyle);
		loadColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_GRP_LOAD_COLUMN_LABEL));

		Cell logColumnLabelCell = createMergedRegionCell(wagonSheet, fifthRow, 3, boldStyle);
		logColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_GRP_LOG_COLUMN_LABEL));

		Cell orderRemarksLabelCell = createMergedRegionCell(wagonSheet, fifthRow, 2, boldStyle);
		orderRemarksLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_GRP_WEIGHING_SYSTEM_NOTE_COLUMN_LABEL));

		Cell wagonRemarksLabelCell = createMergedRegionCell(wagonSheet, fifthRow, 2, boldStyle);
		wagonRemarksLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_GRP_SCHEDULING_NOTE_COLUMN_LABEL));

		nextLine();

		Row sixthRow = wagonSheet.createRow(rowIndex);

		Cell wagonNumberColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		wagonNumberColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_WAGON_NUMBER_COLUMN_LABEL));

		Cell wagonKeeperColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		wagonKeeperColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_WAGON_KEEPER_COLUMN_LABEL));

		Cell wagonTypeColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		wagonTypeColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_WAGON_TYPE_COLUMN_LABEL));
		
		Cell wagonWrittenTareColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		wagonWrittenTareColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_WAGON_WRITTEN_TARE_WEIGHT_T));

		Cell wagonTrackColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		wagonTrackColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_WAGON_TRACK_COLUMN_LABEL));

		Cell wagonPositionColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		wagonPositionColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_WAGON_POSITION_COLUMN_LABEL));

		Cell wagonContainerColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		wagonContainerColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_WAGON_CONTAINER_COLUMN_LABEL));

		Cell wagonStatusColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		wagonStatusColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_WAGON_STATUS_COLUMN_LABEL));

		Cell orderNumberColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		orderNumberColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_ORDER_NUMBER_COLUMN_LABEL));

		Cell orderEtranOutColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		orderEtranOutColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_ORDER_ETRAN_OUT_COLUMN_LABEL));

		Cell orderEtranRetColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		orderEtranRetColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_ORDER_ETRAN_RET_COLUMN_LABEL));

		Cell orderMaterialColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		orderMaterialColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_ORDER_MATERIAL_COLUMN_LABEL));

		Cell orderQuantityColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 2, regularStyle);
		orderQuantityColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_ORDER_QUANTITY_COLUMN_LABEL));

		Cell shippingPtNameColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		shippingPtNameColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_SHIPPING_PT_NAME_COLUMN_LABEL));

		Cell shippingPtLoadingPtColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		shippingPtLoadingPtColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_SHIPPING_PT_LOADING_PT_COLUMN_LABEL));

		Cell shippingPtRailwayStationColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 2, regularStyle);
		shippingPtRailwayStationColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_SHIPPING_PT_RAILWAY_STATION_COLUMN_LABEL));

		Cell shippingPtCountryColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		shippingPtCountryColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_SHIPPING_PT_COUNTRY_COLUMN_LABEL));

		Cell soldToNrColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		soldToNrColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_SOLDTO_NR_COLUMN_LABEL));

		Cell soldToNameColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		soldToNameColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_SOLDTO_NAME_COLUMN_LABEL));

		Cell soldToName2ColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);

		Cell shipToNrColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		shipToNrColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_SHIPTO_NR_COLUMN_LABEL));

		Cell shipToNameColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		shipToNameColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_SHIPTO_NAME_COLUMN_LABEL));

		Cell shipToName2ColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);

		Cell shipToRailwayStationColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 2, regularStyle);
		shipToRailwayStationColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_SHIPTO_RAILWAY_STATION_COLUMN_LABEL));

		Cell shipToCountryColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		shipToCountryColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_SHIPTO_COUNTRY_COLUMN_LABEL));

		Cell departureTimeColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		departureTimeColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_DEPARTURE_TIME_COLUMN_LABEL));

		Cell deliveryTimeFromWindowColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		deliveryTimeFromWindowColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_DELIVERY_WINDOW_FROM_COLUMN_LABEL));

		Cell deliveryTimeToWindowColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		deliveryTimeToWindowColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_DELIVERY_WINDOW_TO_COLUMN_LABEL));

		Cell carrierNameColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		carrierNameColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_CARRIER_NAME_COLUMN_LABEL));

		Cell loadMaterialColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		loadMaterialColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_LOAD_MATERIAL_COLUMN_LABEL));

		Cell loadTareColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		loadTareColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_LOAD_TARE_COLUMN_LABEL) + "/t");

		Cell loadNetColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		loadNetColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_LOAD_NET_COLUMN_LABEL) + "/t");

		Cell loadGrossColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		loadGrossColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_LOAD_GROSS_COLUMN_LABEL) + "/t");

		Cell loadNHMColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		loadNHMColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_LOAD_NHM_COLUMN_LABEL));

		Cell logToRailCoColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		logToRailCoColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_LOG_TO_RAIL_CO_COLUMN_LABEL));

		Cell logDepartureColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		logDepartureColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_LOG_DEPARTURE_COLUMN_LABEL));

		Cell logArrivalColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		logArrivalColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_LOG_ARRIVAL_COLUMN_LABEL));

		Cell weighingOrderRemarkColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		weighingOrderRemarkColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_ORDER_REMARKS_COLUMN_LABEL));

		Cell weighingWagonRemarkColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		weighingWagonRemarkColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_WAGON_REMARKS_COLUMN_LABEL));

		Cell schedulingOrderRemarkColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		schedulingOrderRemarkColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_ORDER_REMARKS_COLUMN_LABEL));

		Cell schedulingWagonRemarkColumnLabelCell = createMergedRegionCell(wagonSheet, sixthRow, 1, regularStyle);
		schedulingWagonRemarkColumnLabelCell.setCellValue(getMessageByKey(locale, WAGONTAB_WAGON_REMARKS_COLUMN_LABEL));

		// From the first colum and just before the value rows we create a pane to separate values from headers
		wagonSheet.createFreezePane(0, FROZEN_PANE_LABEL_ROW_NUM, 0, FROZEN_PANE_LABEL_ROW_NUM);

		Long lastWagonNo = null;
		int rowBgColor = COLOR_BG_ALTERNATE;

		List<RailDispoExportWagonRow> wagonResult = parameters.getWagonResult();
		for (RailDispoExportWagonRow wagon : wagonResult) {

			nextLine();

			Row valueRow = wagonSheet.createRow(rowIndex);

			// We have to switch if it's a new order
			if (shouldSwitchColor(lastWagonNo, wagon.getWagonNo())) {
				if (rowBgColor == COLOR_BG_ALTERNATE) {
					rowBgColor = COLOR_BG_DEFAULT;
				}
				else {
					rowBgColor = COLOR_BG_ALTERNATE;
				}
				lastWagonNo = wagon.getWagonNo();
			}

			Long wagonStatus = wagon.getStatus();
			int statusRowBgColor = COLOR_BG_DEFAULT;

			if (wagonStatus.equals(Constant.WAGON_STATUS_SUBMITTED)) {
				statusRowBgColor = COLOR_BG_SUBMITTED;
			}
			else if (wagonStatus.equals(Constant.WAGON_STATUS_LOADED)) {
				statusRowBgColor = COLOR_BG_LOADED;
			}
			else if (wagonStatus.equals(Constant.WAGON_STATUS_INCOMING)) {
				statusRowBgColor = COLOR_BG_INCOMING;
			}
			else if (wagonStatus.equals(Constant.WAGON_STATUS_AT_SHIPPINGPOINT)) {
				statusRowBgColor = COLOR_BG_DEFAULT;
			}
			else {
				rowBgColor = COLOR_BG_DEFAULT;
			}

			Cell wagonNumberValueLabelCell = createNewCellUsingFormat(valueRow, CELL_FORMAT_WAGON_NUM, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(wagonNumberValueLabelCell, wagon.getWagonNo());

			Cell wagonKeeperValueLabelCell = createNewCell(valueRow, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(wagonKeeperValueLabelCell, wagon.getWagonKeeperName());

			Cell wagonTypeValueLabelCell = createNewCell(valueRow, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(wagonTypeValueLabelCell, wagon.getWagonType());
			
			Cell wagonWrittenTareWeightTLabelCell = createNewCell(valueRow, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(wagonWrittenTareWeightTLabelCell, wagon.getWrittenTareWeightT());

			Cell trackValueLabelCell = createNewCell(valueRow, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(trackValueLabelCell, wagon.getTrackName());

			Cell positionValueLabelCell = createNewCellUsingFormat(valueRow, CELL_FORMAT_INT, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(positionValueLabelCell, wagon.getPosition());

			Cell containerValueLabelCell = createNewCell(valueRow, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(containerValueLabelCell, wagon.getContainerNames());

			Cell statusValueLabelCell = createNewCell(valueRow, statusRowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(statusValueLabelCell, getWagonStatusText(wagonStatus));

			Cell orderNumberValueLabelCell = createNewCell(valueRow, rowBgColor, CellStyle.ALIGN_LEFT);
			String orderNo = wagon.getOrderNo() == null ? null : Long.toString(wagon.getOrderNo());
			setCellValueIfNotNull(orderNumberValueLabelCell, orderNo);

			Cell orderEtranOutValueLabelCell = createNewCell(valueRow, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(orderEtranOutValueLabelCell, wagon.getEtranOut());

			Cell orderEtranRetValueLabelCell = createNewCell(valueRow, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(orderEtranRetValueLabelCell, wagon.getEtranRet());

			Cell orderMaterialValueLabelCell = createNewCell(valueRow, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(orderMaterialValueLabelCell, wagon.getOrderMaterialName());

			Cell orderQuantityValueLabelCell = createNewCellUsingFormat(valueRow, CELL_FORMAT_INT, rowBgColor, CellStyle.ALIGN_RIGHT);
			setCellValueIfNotNull(orderQuantityValueLabelCell, wagon.getOrderQty());

			Cell orderUomValueLabelCell = createNewCell(valueRow, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(orderUomValueLabelCell, wagon.getOrderUom());

			Cell shippingPointNameValueLabelCell = createNewCell(valueRow, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(shippingPointNameValueLabelCell, wagon.getShippintPtName());

			Cell loadingPointNameValueLabelCell = createNewCell(valueRow, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(loadingPointNameValueLabelCell, wagon.getLoadingPtName());

			Cell shippingPtStationNoValueLabelCell = createNewCell(valueRow, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(shippingPtStationNoValueLabelCell, wagon.getShippintPtStationNo());

			Cell shippingPtStationNameValueLabelCell = createNewCell(valueRow, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(shippingPtStationNameValueLabelCell, wagon.getShippintPtStationName());

			Cell railwayStationCountryValueLabelCell = createNewCell(valueRow, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(railwayStationCountryValueLabelCell, wagon.getShippintPtCountryCd());

			Cell soldToNumberValueLabelCell = createNewCell(valueRow, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(soldToNumberValueLabelCell, wagon.getSoldToNumber());

			Cell soldToName1ValueLabelCell = createNewCell(valueRow, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(soldToName1ValueLabelCell, wagon.getSoldToName());

			Cell soldToName2ValueLabelCell = createNewCell(valueRow, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(soldToName2ValueLabelCell, wagon.getSoldToName2());

			Cell shipToNumberValueLabelCell = createNewCell(valueRow, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(shipToNumberValueLabelCell, wagon.getShipToNumber());

			Cell shipToName1ValueLabelCell = createNewCell(valueRow, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(shipToName1ValueLabelCell, wagon.getShipToName());

			Cell shipToName2ValueLabelCell = createNewCell(valueRow, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(shipToName2ValueLabelCell, wagon.getShipToName2());

			Cell shipToStationNoValueLabelCell = createNewCell(valueRow, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(shipToStationNoValueLabelCell, wagon.getShipToStationNo());

			Cell shipToStationNameValueLabelCell = createNewCell(valueRow, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(shipToStationNameValueLabelCell, wagon.getShipToStationName());

			Cell shipToStationCountryValueLabelCell = createNewCell(valueRow, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(shipToStationCountryValueLabelCell, wagon.getShipToCountryCd());

			Cell manupulationTimeValueLabelCell = createNewCellUsingFormat(valueRow, CELL_FORMAT_DATE_TIME, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(manupulationTimeValueLabelCell, wagon.getManipulationTime());

			Cell deliveryTimeFromValueLabelCell = createNewCellUsingFormat(valueRow, CELL_FORMAT_DATE, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(deliveryTimeFromValueLabelCell, wagon.getDeliveryFrom());

			Cell deliveryTimeToValueLabelCell = createNewCellUsingFormat(valueRow, CELL_FORMAT_DATE, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(deliveryTimeToValueLabelCell, wagon.getDeliveryTo());

			Cell carrierValueLabelCell = createNewCell(valueRow, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(carrierValueLabelCell, wagon.getCarrierName());

			Cell loadMaterialValueLabelCell = createNewCell(valueRow, statusRowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(loadMaterialValueLabelCell, wagon.getLoadMaterial());

			Cell loadTareValueLabelCell = createNewCellUsingFormat(valueRow, CELL_FORMAT_DEC, statusRowBgColor, CellStyle.ALIGN_RIGHT);
			setCellValueIfNotNull(loadTareValueLabelCell, wagon.getLoadTare());

			Cell loadNetValueLabelCell = createNewCellUsingFormat(valueRow, CELL_FORMAT_DEC, statusRowBgColor, CellStyle.ALIGN_RIGHT);
			setCellValueIfNotNull(loadNetValueLabelCell, wagon.getLoadNet());

			Cell loadGrossValueLabelCell = createNewCellUsingFormat(valueRow, CELL_FORMAT_DEC, statusRowBgColor, CellStyle.ALIGN_RIGHT);
			setCellValueIfNotNull(loadGrossValueLabelCell, wagon.getLoadGross());

			Cell loadNHMValueLabelCell = createNewCell(valueRow, statusRowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(loadNHMValueLabelCell, wagon.getNhm());

			Cell toRailCoValueLabelCell = createNewCellUsingFormat(valueRow, CELL_FORMAT_DATE_TIME, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(toRailCoValueLabelCell, wagon.getToRailCo());

			Cell departureValueLabelCell = createNewCellUsingFormat(valueRow, CELL_FORMAT_DATE_TIME, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(departureValueLabelCell, wagon.getDeparture());

			Cell arrivingValueLabelCell = createNewCellUsingFormat(valueRow, CELL_FORMAT_DATE_TIME, rowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(arrivingValueLabelCell, wagon.getArrival());

			Cell noteRailwayDispValueLabelCell = createNewCell(valueRow, statusRowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(noteRailwayDispValueLabelCell, wagon.getNoteRailwayDisp());

			Cell wagonNoteRailwaySpeditionValueLabelCell = createNewCell(valueRow, statusRowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(wagonNoteRailwaySpeditionValueLabelCell, wagon.getWagonNoteRailwaySpedition());

			Cell noteDispatchingValueLabelCell = createNewCell(valueRow, statusRowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(noteDispatchingValueLabelCell, wagon.getNoteDispatching());

			Cell wagonNoteDispositionValueLabelCell = createNewCell(valueRow, statusRowBgColor, CellStyle.ALIGN_LEFT);
			setCellValueIfNotNull(wagonNoteDispositionValueLabelCell, wagon.getWagonNoteDisposition());

		}

		wagonSheet.setAutoFilter(new CellRangeAddress(COLUMN_LABEL_ROW_NUM, rowIndex, 0, cellIndex - 1));

		for (int i = 0; i<cellIndex; i++) {
			wagonSheet.autoSizeColumn(i);
		}

		wagonSheet.setColumnWidth(RailDispoExporterColumnDescriptor.WAGON_KEEPER.getColumnIndex(), RailDispoExporterColumnDescriptor.WAGON_KEEPER.getCalculatedWidth());
		wagonSheet.setColumnWidth(RailDispoExporterColumnDescriptor.WAGON_ETRAN_OUT.getColumnIndex(), RailDispoExporterColumnDescriptor.WAGON_ETRAN_OUT.getCalculatedWidth());
		wagonSheet.setColumnWidth(RailDispoExporterColumnDescriptor.WAGON_ETRAN_RET.getColumnIndex(), RailDispoExporterColumnDescriptor.WAGON_ETRAN_RET.getCalculatedWidth());
		wagonSheet.setColumnWidth(RailDispoExporterColumnDescriptor.WAGON_WEIGHING_NOTE_ORDER.getColumnIndex(), RailDispoExporterColumnDescriptor.WAGON_WEIGHING_NOTE_ORDER.getCalculatedWidth());
		wagonSheet.setColumnWidth(RailDispoExporterColumnDescriptor.WAGON_WEIGHING_NOTE_WAGON.getColumnIndex(), RailDispoExporterColumnDescriptor.WAGON_WEIGHING_NOTE_WAGON.getCalculatedWidth());
		wagonSheet.setColumnWidth(RailDispoExporterColumnDescriptor.WAGON_SCHEDULING_NOTE_ORDER.getColumnIndex(), RailDispoExporterColumnDescriptor.WAGON_SCHEDULING_NOTE_ORDER.getCalculatedWidth());
		wagonSheet.setColumnWidth(RailDispoExporterColumnDescriptor.WAGON_SCHEDULING_NOTE_WAGON.getColumnIndex(), RailDispoExporterColumnDescriptor.WAGON_SCHEDULING_NOTE_WAGON.getCalculatedWidth());

	}

	private boolean shouldSwitchColor(Long lastOrderNo, Long compareOrderNo) {
		return compareOrderNo == null || lastOrderNo != compareOrderNo;
	}

	private void buildSheetCommon(HSSFSheet sheet) {

		Locale locale = parameters.getLocale();

		RailPlantEntity plant = parameters.getPlant();

		cellIndex = 0;
		rowIndex = 0;

		Row firstRow = sheet.createRow(rowIndex);

		Cell plantLabelCell = createMergedRegionCell(sheet, firstRow, 1, boldStyle); 
		plantLabelCell.setCellValue(getMessageByKey(locale, PLANT_LABEL));

		Cell plantValueCell = createMergedRegionCell(sheet, firstRow, 4, regularStyle);
		plantValueCell.setCellValue(plant.getPlantName());

		Cell exportingUserLabelCell = createMergedRegionCell(sheet, firstRow, 1, boldStyle);
		exportingUserLabelCell.setCellValue(getMessageByKey(locale, EXPORTING_USER_LABEL));

		Cell exportingUserValueCell = createMergedRegionCell(sheet, firstRow, 4, regularStyle);
		exportingUserValueCell.setCellValue(parameters.getExportingUser());

		nextLine();

		Row secondRow = sheet.createRow(rowIndex);

		Cell timezoneLabelCell = createMergedRegionCell(sheet, secondRow, 1, boldStyle);
		timezoneLabelCell.setCellValue(getMessageByKey(locale, TIMEZONE_LABEL));

		Cell timezoneValueCell = createMergedRegionCell(sheet, secondRow, 4, regularStyle);
		timezoneValueCell.setCellValue(parameters.getUtcOffset());

		Cell timeOfExportLabelCell = createMergedRegionCell(sheet, secondRow, 1, boldStyle);
		timeOfExportLabelCell.setCellValue(getMessageByKey(locale, TIME_OF_EXPORT_LABEL));

		Cell timeOfExportValueCell = createMergedRegionCell(sheet, secondRow, 4, regularStyle);
		timeOfExportValueCell.setCellValue(parameters.getTimeOfExport());

		nextLine();

		Row thirdRow = sheet.createRow(rowIndex);

		Cell spAndTracksLabelCell = createMergedRegionCell(sheet, thirdRow, 5, boldStyle);
		spAndTracksLabelCell.setCellValue(getMessageByKey(locale, SP_AND_TRACKS_INC_LABEL));

		Cell spAndTracksValueCell = createMergedRegionCell(sheet, thirdRow, 4, regularStyle);
		spAndTracksValueCell.setCellValue(StringUtils.join(spRailwayStationNames, ", ")); 

		nextLine();

		Row forthRow = sheet.createRow(rowIndex);

		Cell wagonStatusLabelCell = createMergedRegionCell(sheet, forthRow, 5, boldStyle);
		wagonStatusLabelCell.setCellValue(getMessageByKey(locale, WAGON_STATUS_INC_LABEL));

		Cell wagonStatusValueCell = createMergedRegionCell(sheet, forthRow, 4, regularStyle);
		wagonStatusValueCell.setCellValue(StringUtils.join(statusText, ", "));

	}

	private HSSFSheet createSheetWithName(String messageKey) {
		return workBook.createSheet(getMessageByKey(parameters.getLocale(), messageKey));
	}

	private void setCellValueIfNotNull(Cell cell, Calendar value) {
		if (value != null) {
			cell.setCellValue(value);
		}
	}

	private void setCellValueIfNotNull(Cell cell, Long value) {
		if (value != null) {
			cell.setCellValue(value);
		}
	}

	private void setCellValueIfNotNull(Cell cell, String value) {
		if (value != null) {
			cell.setCellValue(value);
		}
	}

	private void setCellValueIfNotNull(Cell cell, BigDecimal value) {
		if (value != null) {
			cell.setCellValue(value.doubleValue());
		}
	}

	private Cell createMergedRegionCell(Sheet sheet, Row row, int cellNum, CellStyle cellStyle) {

		int fromCellNum = cellIndex;

		Cell resultCell = null;

		// We need to create 'dummy cells' to give space for the merged region
		for (int i = 0; i < cellNum; i++) {
			 Cell cell = row.createCell(cellIndex++);
			 if (resultCell == null) {
				 resultCell = cell;
			 }
			cell.setCellStyle(cellStyle);
		}

		// Create merged region on sheet
		sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, fromCellNum, fromCellNum + cellNum - 1));

		return resultCell;

	}

	private Cell createNewCell(Row row, int bgColor, short cellAlignment) {

		return createNewCellUsingFormat(row, null, bgColor, cellAlignment);

	}

	private Cell createNewCellUsingFormat(Row row, String format, int bgColor, short cellAlignment) {

		Cell cell = row.createCell(cellIndex++);

		CellStyle style = getCellStyle(bgColor, cellAlignment, regularFont, format);

		cell.setCellStyle(style);

		return cell;

	}

	CellStyle getCellStyle(int backGroundColor, short cellAlignment, Font font, String format) {

		CellStyleWrapper cellStyleWrapper = new CellStyleWrapper(backGroundColor, cellAlignment, font, format);

		CellStyle cellStyle = cellStyleMap.get(cellStyleWrapper);

		if (cellStyle == null) {

			cellStyle = workBook.createCellStyle();

			// For default we don't print at all.
			if (backGroundColor != COLOR_BG_DEFAULT) {
				cellStyle.setFillForegroundColor(colors[backGroundColor]);
				cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			}

			cellStyle.setAlignment(cellAlignment);
			cellStyle.setFont(font);

			if (format != null) {
				cellStyle.setDataFormat(createHelper.createDataFormat().getFormat(format));
			}

			cellStyleMap.put(cellStyleWrapper, cellStyle);

		}

		return cellStyle;

	}
	
	private void nextLine() {
		rowIndex++;
		cellIndex = 0;
	}

	private String getMessageByKey(Locale locale, String messageKey) {
		return Message.lookup(locale, messageKey);
	}

	public String getWagonStatusText(Long statusCd) {
		if (statusCd == Constant.WAGON_STATUS_LOADED) {
			return getMessageByKey(parameters.getLocale(), WAGON_STATUS_TEXT_LOADED);
		}
		else if (statusCd == Constant.WAGON_STATUS_AT_SHIPPINGPOINT) {
			return getMessageByKey(parameters.getLocale(), WAGON_STATUS_TEXT_AT_SHIPPINGPOINT);
		}
		else if (statusCd == Constant.WAGON_STATUS_INCOMING) {
			return getMessageByKey(parameters.getLocale(), WAGON_STATUS_TEXT_INCOMING);
		}
		else if (statusCd == Constant.WAGON_STATUS_SUBMITTED) {
			return getMessageByKey(parameters.getLocale(), WAGON_STATUS_TEXT_SUBMITTED);
		}
		else {
			return "";
		}
	}

}
