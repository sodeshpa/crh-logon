/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.export.raildispo;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.crh.logon.business.services.beans.RailDispoExportOrderRow;
import com.crh.logon.business.services.beans.RailDispoExportParameters;
import com.crh.logon.business.services.beans.RailDispoExportWagonRow;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.refdata.TimezoneBsEntity;
import com.crh.logon.persistence.entities.views.DispoPlantTrackListEntity;
import com.crh.logon.persistence.entities.views.RailPlantEntity;
import com.crh.logon.persistence.entities.views.RailPlantSpEntity;

/**
 * @author AdNovum Informatik AG
 */
public class RailDispoExporterTest {

	private Workbook workbook;
	private CreationHelper createHelper;

	private RailDispoExportParameters parameters;
	private RailDispoExporter exporter;

	// Mock data from common
	private String plantCd = "8101";
	private String utcOffset = "UTC+04:00";
	private String timeOfExport = "21.10.2013  16:06:00";
	private Locale englishLocale = Locale.ENGLISH;
	private String exportingUser = "Michael Schmid (mschmid)";

	// Mock data for wagon and order rows
	private Long wagonNo = 338593210077L;
	private String wagonKeeper = "WagonKeep86";
	private String wagonType = "Sgns";
	private BigDecimal writtenTareWeightT = new BigDecimal("25.1");
	private String trackName = "V3";
	private Long position = 11L;
	private String containerName = "mContainer";
	private Long orderNo = 20039294L;
	private String etranOut = "etranOut";
	private String etranRet = "etranRet";
	private String orderMaterialName = "Fluvio";
	private BigDecimal orderQty = new BigDecimal("12");
	private String orderUom = "Palett";
	private String shippintPtName = "ADN Rail Münchenstein";
	private String loadingPtName = "ADN Bahnverlad 1";
	private String shippintPtStationNo = "001198";
	private String shippintPtStationName = "F: Münchenstein";
	private Long shippintPtCountryCd = 87L;
	private String soldToNumber = "46";
	private String soldToName = "ADN SoldTo";
	private String soldToName2;
	private String shipToNumber = "45";
	private String shipToName = "ADN Kies und Beton Regli AG";
	private String shipToName2;
	private String shipToStationNo = "051136";
	private String shipToStationName = "F:Altdorf";
	private Long shipToCountryCd = 87L;
	private Calendar manipulationTime = Calendar.getInstance();
	private Calendar deliveryFrom = Calendar.getInstance();
	private Calendar deliveryTo = Calendar.getInstance();
	private String carrierName = "AdNovum SBB TTC-ON";
	private String loadMaterial = "Fluvio 4 (EC)";
	private BigDecimal loadTare = new BigDecimal("25.4");
	private BigDecimal loadNet = new BigDecimal("24.6");
	private BigDecimal loadGross = new BigDecimal("50.0");
	private String nhm = "25232900";
	private Calendar toRailCo = Calendar.getInstance();
	private Calendar departure = Calendar.getInstance();
	private Calendar arrival = Calendar.getInstance();
	private String shipToWagonPrefs = "FIXME; 54t - Cattaneo/MBA (Z4/932); 50Pal - Habins Bag-Wagon (ZS4); Plaster;";
	private String noteRailwayDisp = "TLGNC_ORDER.NOTE_RAILWAY_DISP";
	private String noteRailwaySped = "TLGNC_WAGON.NOTE_RAILWAY_RAILWAY_SPEDITION";
	private String noteDisp = "TLGNC_ORDER.NOTE_DISPATCHING";
	private String wagonNoteDisp = "TLGNC_WAGON.WAGON_NOTE_DISPOSITION";

	private Long statusCds[] = new Long[] {
		Constant.WAGON_STATUS_INCOMING, 
		Constant.WAGON_STATUS_AT_SHIPPINGPOINT,
		Constant.WAGON_STATUS_LOADED,
		Constant.WAGON_STATUS_SUBMITTED
	};

	private List<RailDispoExportWagonRow> wagons;
	private List<RailDispoExportOrderRow> orders;

	private Long nextWagonNo = wagonNo;

	// Test control data
	int regionIndex = 0;
	int rowIndex = 0;
	int cellIndex = 0;

	@Before
	public void setup() {

		List<RailPlantSpEntity> shippingPoints = new ArrayList<RailPlantSpEntity>();
		List<DispoPlantTrackListEntity> selectedTracks = new ArrayList<DispoPlantTrackListEntity>();

		// ADN Rail Münchenstein (V1, V3), ADN Rail Siggenthal
		RailPlantSpEntity railPlant = new RailPlantSpEntity();
		railPlant.setPlantCd(plantCd);
		railPlant.setShippingpointName(shippintPtName);
		shippingPoints.add(railPlant);

		DispoPlantTrackListEntity selectedTrack = new DispoPlantTrackListEntity();
		selectedTrack.setLangCd("en");
		selectedTrack.setPlantCd(plantCd);
		selectedTrack.setPlantTrackCd(1L);
		selectedTrack.setTrackName(trackName);
		selectedTracks.add(selectedTrack);

		// Mock plants
		RailPlantEntity plant = new RailPlantEntity();
		plant.setPlantCd(plantCd);
		plant.setPlantName("Crh-Werk Eclépens");

		// Mock a wagon result
		wagons = new ArrayList<RailDispoExportWagonRow>();

		for (Long statusCd : statusCds) {
			RailDispoExportWagonRow wagon = buildWagonDispoRow(statusCd);
			wagons.add(wagon);
		}

		// Mock an order result
		orders = new ArrayList<RailDispoExportOrderRow>();
		RailDispoExportOrderRow orderDispoEntity = buildOrderDispoRow();
		orders.add(orderDispoEntity);

		ShippingpointEntity shippingpointEntity = new ShippingpointEntity();
		TimezoneBsEntity timezoneBsEntity = new TimezoneBsEntity();
		shippingpointEntity.setTimezone(timezoneBsEntity);

		Map<Long, Boolean> statuses = new HashMap<Long, Boolean>();
		statuses.put(Constant.WAGON_STATUS_AT_SHIPPINGPOINT, Boolean.FALSE);
		statuses.put(Constant.WAGON_STATUS_INCOMING, Boolean.TRUE);
		statuses.put(Constant.WAGON_STATUS_LOADED, Boolean.FALSE);
		statuses.put(Constant.WAGON_STATUS_SUBMITTED, Boolean.TRUE);

		parameters = new RailDispoExportParameters(englishLocale, exportingUser, timeOfExport, utcOffset, plant, shippingPoints, selectedTracks, orders, wagons, statuses);

		exporter = new RailDispoExporter(parameters);

		// There should be a workbook
		workbook = exporter.export();

	}

	@After
	public void tearDown () throws IOException {

		/*
		FileOutputStream outFile = new FileOutputStream(new File("/home/balakirev/Excel/export.xls"));
		workbook.write(outFile);
		outFile.close();
		*/

	}

	@Test
	public void testStoresCellStyle() {

		short backGroundColor = RailDispoExporter.COLOR_BG_DEFAULT;
		short cellAlignment = CellStyle.ALIGN_CENTER;
		Font font = workbook.createFont();
		String format = RailDispoExporter.CELL_FORMAT_DATE_TIME;

		CellStyle firstStyle = exporter.getCellStyle(backGroundColor, cellAlignment, font, format);
		CellStyle secondStyle = exporter.getCellStyle(backGroundColor, cellAlignment, font, format);

		Assert.assertNotNull(firstStyle);
		Assert.assertNotNull(secondStyle);

		Assert.assertSame(firstStyle, secondStyle);

	}

	@Test
	public void testStoresMultipleCellStyle() {

		short backGroundColor = RailDispoExporter.COLOR_BG_DEFAULT;
		short cellAlignmentCenter = CellStyle.ALIGN_CENTER;
		short cellAlignmentRight = CellStyle.ALIGN_RIGHT;
		Font font = workbook.createFont();
		String format = RailDispoExporter.CELL_FORMAT_DATE_TIME;

		CellStyle firstStyle = exporter.getCellStyle(backGroundColor, cellAlignmentCenter, font, format);
		CellStyle secondStyle = exporter.getCellStyle(backGroundColor, cellAlignmentRight, font, format);

		Assert.assertNotNull(firstStyle);
		Assert.assertNotNull(secondStyle);

		Assert.assertFalse(firstStyle.equals(secondStyle));

	}

	@Test
	public void testStructure() {

		createHelper = workbook.getCreationHelper();

		String wagonMessageKey = getMessage(RailDispoExporter.WAGON_SHEET_MSG_KEY);
		String orderMessageKey = getMessage(RailDispoExporter.ORDER_SHEET_MSG_KEY);

		// There should be a sheet for wagons 
		Sheet wagonSheet = workbook.getSheet(wagonMessageKey);
		validateWagonSheet(wagonSheet, wagonMessageKey);

		// There should be a sheet for wagons 
		Sheet orderSheet = workbook.getSheet(orderMessageKey);
		validateOrderSheet(orderSheet, orderMessageKey);

	}

	private void validateOrderSheet(Sheet orderSheet, String sheetName) {

		regionIndex = 0;
		
		validateSheetCommon(orderSheet, sheetName);

		rowIndex = 4;
		cellIndex = 0;

		cellShouldHaveTextValueInNextRegion(orderSheet, 4, getMessage(RailDispoExporter.ORDTAB_GRP_ORDER_COLUMN_LABEL));							// 'Order'
		cellShouldHaveTextValueInNextRegion(orderSheet, 5, getMessage(RailDispoExporter.ORDTAB_GRP_SHIPPING_POINT_COLUMN_LABEL)); 					// 'Shippingpoint'
		cellShouldHaveTextValueInNextRegion(orderSheet, 3, getMessage(RailDispoExporter.ORDTAB_GRP_SOLDTO_COLUMN_LABEL));	 						// 'SoldTo'
		cellShouldHaveTextValueInNextRegion(orderSheet, 7, getMessage(RailDispoExporter.ORDTAB_GRP_SHIPTO_COLUMN_LABEL));	 						// 'ShipTo'
		cellShouldHaveTextValueInNextRegion(orderSheet, 2, getMessage(RailDispoExporter.ORDTAB_GRP_DELIVERY_WINDOW_COLUMN_LABEL));					// 'Delivery window'
		cellShouldHaveTextValueInNextRegion(orderSheet, 1, getMessage(RailDispoExporter.ORDTAB_GRP_CARRIER_COLUMN_LABEL));							// 'Carrier'
		cellShouldHaveTextValueInNextRegion(orderSheet, 1, getMessage(RailDispoExporter.ORDTAB_GRP_SHIPTO_WAGON_PREFS_COLUMN_LABEL));				// 'Ship-to wagon prefs.'
		cellShouldHaveTextValueInNextRegion(orderSheet, 2, getMessage(RailDispoExporter.ORDTAB_GRP_NOTES_COLUMN_LABEL));							// 'Notes'

		nextLine();

		cellShouldHaveTextValueInNextRegion(orderSheet, 1, getMessage(RailDispoExporter.ORDTAB_ORDER_NUMBER_COLUMN_LABEL));							// 'Number'
		cellShouldHaveTextValueInNextRegion(orderSheet, 1, getMessage(RailDispoExporter.ORDTAB_ORDER_MATERIAL_COLUMN_LABEL));						// 'Material'
		cellShouldHaveTextValueInNextRegion(orderSheet, 2, getMessage(RailDispoExporter.ORDTAB_ORDER_QUANTITY_COLUMN_LABEL));						// 'Quantity'
		cellShouldHaveTextValueInNextRegion(orderSheet, 1, getMessage(RailDispoExporter.ORDTAB_SHIPPING_PT_NAME_COLUMN_LABEL));						// 'Name'
		cellShouldHaveTextValueInNextRegion(orderSheet, 1, getMessage(RailDispoExporter.ORDTAB_SHIPPING_PT_LOADING_PT_COLUMN_LABEL));				// 'Loading point'
		cellShouldHaveTextValueInNextRegion(orderSheet, 2, getMessage(RailDispoExporter.ORDTAB_SHIPPING_PT_RAILWAY_STATION_COLUMN_LABEL));			// 'Railway station'
		cellShouldHaveTextValueInNextRegion(orderSheet, 1, getMessage(RailDispoExporter.ORDTAB_SHIPPING_PT_COUNTRY_COLUMN_LABEL));					// 'Country'
		cellShouldHaveTextValueInNextRegion(orderSheet, 1, getMessage(RailDispoExporter.ORDTAB_SOLDTO_NR_COLUMN_LABEL));							// 'SoldTo Nr'
		cellShouldHaveTextValueInNextRegion(orderSheet, 1, getMessage(RailDispoExporter.ORDTAB_SOLDTO_NAME_COLUMN_LABEL));							// 'SoldTo Name'
		cellShouldHaveTextValueInNextRegion(orderSheet, 1, "");																						// 'SoldTo Name 2'
		cellShouldHaveTextValueInNextRegion(orderSheet, 1, getMessage(RailDispoExporter.ORDTAB_SHIPTO_NR_COLUMN_LABEL));							// 'ShipTo Nr'
		cellShouldHaveTextValueInNextRegion(orderSheet, 1, getMessage(RailDispoExporter.ORDTAB_SHIPTO_NAME_COLUMN_LABEL));							// 'ShipTo Name'
		cellShouldHaveTextValueInNextRegion(orderSheet, 1, "");																						// 'ShipTo name 2'
		cellShouldHaveTextValueInNextRegion(orderSheet, 2, getMessage(RailDispoExporter.ORDTAB_SHIPTO_RAILWAY_STATION_COLUMN_LABEL));				// 'Railway station'
		cellShouldHaveTextValueInNextRegion(orderSheet, 1, getMessage(RailDispoExporter.ORDTAB_SHIPTO_COUNTRY_COLUMN_LABEL));						// 'Country'
		cellShouldHaveTextValueInNextRegion(orderSheet, 1, getMessage(RailDispoExporter.ORDTAB_DEPARTURE_TIME_COLUMN_LABEL));						// 'Manipulating time departure'
		cellShouldHaveTextValueInNextRegion(orderSheet, 1, getMessage(RailDispoExporter.ORDTAB_DELIVERY_WINDOW_FROM_COLUMN_LABEL));					// 'Delivery window'
		cellShouldHaveTextValueInNextRegion(orderSheet, 1, getMessage(RailDispoExporter.ORDTAB_DELIVERY_WINDOW_TO_COLUMN_LABEL));					// 'Delivery window'
		cellShouldHaveTextValueInNextRegion(orderSheet, 1, getMessage(RailDispoExporter.ORDTAB_CARRIER_NAME_COLUMN_LABEL));							// 'Name'
		cellShouldHaveTextValueInNextRegion(orderSheet, 1, "");																						// ''
		cellShouldHaveTextValueInNextRegion(orderSheet, 1, getMessage(RailDispoExporter.ORDTAB_WEIGHING_SYSTEM_COLUMN_LABEL));						// 'Weighing system'
		cellShouldHaveTextValueInNextRegion(orderSheet, 1, getMessage(RailDispoExporter.ORDTAB_SCHEDULING_COLUMN_LABEL));							// 'Scheduling'

		nextLine();

		cellShouldHaveTextValue(orderSheet, Long.toString(orderNo));
		cellShouldHaveTextValue(orderSheet, orderMaterialName);
		cellShouldHaveNumericValue(orderSheet, orderQty.doubleValue());
		cellShouldHaveTextValue(orderSheet, orderUom);
		cellShouldHaveTextValue(orderSheet, shippintPtName);
		cellShouldHaveTextValue(orderSheet, loadingPtName);
		cellShouldHaveTextValue(orderSheet, shippintPtStationNo);
		cellShouldHaveTextValue(orderSheet, shippintPtStationName);
		cellShouldHaveNumericValue(orderSheet, shippintPtCountryCd);
		cellShouldHaveTextValue(orderSheet, soldToNumber);
		cellShouldHaveTextValue(orderSheet, soldToName);
		cellShouldHaveTextValue(orderSheet, soldToName2);
		cellShouldHaveTextValue(orderSheet, shipToNumber);
		cellShouldHaveTextValue(orderSheet, shipToName);
		cellShouldHaveTextValue(orderSheet, shipToName2);
		cellShouldHaveTextValue(orderSheet, shipToStationNo);
		cellShouldHaveTextValue(orderSheet, shipToStationName);
		cellShouldHaveNumericValue(orderSheet, shipToCountryCd);
		cellShouldHaveDateValue(orderSheet, manipulationTime, RailDispoExporter.CELL_FORMAT_DATE_TIME);
		cellShouldHaveDateValue(orderSheet, deliveryFrom, RailDispoExporter.CELL_FORMAT_DATE);
		cellShouldHaveDateValue(orderSheet, deliveryTo, RailDispoExporter.CELL_FORMAT_DATE);
		cellShouldHaveTextValue(orderSheet, carrierName);
		cellShouldHaveTextValue(orderSheet, shipToWagonPrefs);
		cellShouldHaveTextValue(orderSheet, noteRailwayDisp);
		cellShouldHaveTextValue(orderSheet, noteDisp);

		validateColumnWidthWithDescriptor(orderSheet, RailDispoExporterColumnDescriptor.ORDER_WEIGHING_NOTE_ORDER);
		validateColumnWidthWithDescriptor(orderSheet, RailDispoExporterColumnDescriptor.ORDER_SCHEDULING_NOTE_ORDER);

	}

	private void validateWagonSheet(Sheet wagonSheet, String sheetName) {

		regionIndex = 0;

		validateSheetCommon(wagonSheet, sheetName);

		rowIndex = 4;
		cellIndex = 0;

		cellShouldHaveTextValueInNextRegion(wagonSheet, 8, getMessage(RailDispoExporter.WAGONTAB_GRP_WAGON_COLUMN_LABEL));							// 'Wagon'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 6, getMessage(RailDispoExporter.WAGONTAB_GRP_ORDER_ALLOCATED_COLUMN_LABEL));				// 'Order allocated'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 5, getMessage(RailDispoExporter.WAGONTAB_GRP_SHIPPING_POINT_COLUMN_LABEL));					// 'Shipping point'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 3, getMessage(RailDispoExporter.WAGONTAB_GRP_SOLDTO_COLUMN_LABEL));							// 'Sold-to'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 7, getMessage(RailDispoExporter.WAGONTAB_GRP_SHIPTO_COLUMN_LABEL));							// 'Ship-to'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 2, getMessage(RailDispoExporter.WAGONTAB_GRP_DELIVERY_WINDOW_COLUMN_LABEL));				// 'Delivery Window'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_GRP_CARRIER_COLUMN_LABEL));						// 'Carrier'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 5, getMessage(RailDispoExporter.WAGONTAB_GRP_LOAD_COLUMN_LABEL));							// 'Load'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 3, getMessage(RailDispoExporter.WAGONTAB_GRP_LOG_COLUMN_LABEL));							// 'Log'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 2, getMessage(RailDispoExporter.WAGONTAB_GRP_WEIGHING_SYSTEM_NOTE_COLUMN_LABEL));			// 'Note for weighing system'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 2, getMessage(RailDispoExporter.WAGONTAB_GRP_SCHEDULING_NOTE_COLUMN_LABEL));				// 'Note for scheduling'

		nextLine();

		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_WAGON_NUMBER_COLUMN_LABEL));						// 'Number'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_WAGON_KEEPER_COLUMN_LABEL));						// 'Keeper'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_WAGON_TYPE_COLUMN_LABEL));							// 'Type'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_WAGON_WRITTEN_TARE_WEIGHT_T));							// 'Written weight t'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_WAGON_TRACK_COLUMN_LABEL));						// 'Track'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_WAGON_POSITION_COLUMN_LABEL));						// 'Position'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_WAGON_CONTAINER_COLUMN_LABEL));					// 'Container'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_WAGON_STATUS_COLUMN_LABEL));						// 'Status'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_ORDER_NUMBER_COLUMN_LABEL));						// 'Number'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_ORDER_ETRAN_OUT_COLUMN_LABEL));					// 'ETRAN Out'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_ORDER_ETRAN_RET_COLUMN_LABEL));					// 'ETRAN Ret'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_ORDER_MATERIAL_COLUMN_LABEL));						// 'Material'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 2, getMessage(RailDispoExporter.WAGONTAB_ORDER_QUANTITY_COLUMN_LABEL));						// 'Quantity'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_SHIPPING_PT_NAME_COLUMN_LABEL));					// 'Name'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_SHIPPING_PT_LOADING_PT_COLUMN_LABEL));				// 'Loading point'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 2, getMessage(RailDispoExporter.WAGONTAB_SHIPPING_PT_RAILWAY_STATION_COLUMN_LABEL));		// 'Railway Station'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_SHIPPING_PT_COUNTRY_COLUMN_LABEL));				// 'Country'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_SOLDTO_NR_COLUMN_LABEL));							// 'SoldTo Nr'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_SOLDTO_NAME_COLUMN_LABEL));						// 'SoldTo Name'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, "");																						// 'SoldTo Name 2'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_SHIPTO_NR_COLUMN_LABEL));							// 'Shipto Number'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_SHIPTO_NAME_COLUMN_LABEL));						// 'ShiptoName'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, "");																						// 'ShipTo name 2'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 2, getMessage(RailDispoExporter.WAGONTAB_SHIPTO_RAILWAY_STATION_COLUMN_LABEL));				// 'Railway Station'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_SHIPTO_COUNTRY_COLUMN_LABEL));						// 'Country'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_DEPARTURE_TIME_COLUMN_LABEL));						// 'Manipulating time'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_DELIVERY_WINDOW_FROM_COLUMN_LABEL));				// 'From'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_DELIVERY_WINDOW_TO_COLUMN_LABEL));					// 'To'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_CARRIER_NAME_COLUMN_LABEL));						// 'Name'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_LOAD_MATERIAL_COLUMN_LABEL));						// 'Material'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_LOAD_TARE_COLUMN_LABEL) + "/t");					// 'Tare'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_LOAD_NET_COLUMN_LABEL) + "/t");					// 'Net'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_LOAD_GROSS_COLUMN_LABEL) + "/t");					// 'Gross'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_LOAD_NHM_COLUMN_LABEL));							// 'NHM'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_LOG_TO_RAIL_CO_COLUMN_LABEL));						// 'To rail co.'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_LOG_DEPARTURE_COLUMN_LABEL));						// 'Departure'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_LOG_ARRIVAL_COLUMN_LABEL));						// 'Arriving'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_ORDER_REMARKS_COLUMN_LABEL));						// 'Order'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_WAGON_REMARKS_COLUMN_LABEL));						// 'Wagon'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_ORDER_REMARKS_COLUMN_LABEL));						// 'Order'
		cellShouldHaveTextValueInNextRegion(wagonSheet, 1, getMessage(RailDispoExporter.WAGONTAB_WAGON_REMARKS_COLUMN_LABEL));						// 'Wagon'

		for (RailDispoExportWagonRow wagon : wagons) {

			nextLine();

			cellShouldHaveNumericValue(wagonSheet, wagon.getWagonNo(), RailDispoExporter.CELL_FORMAT_WAGON_NUM, CellStyle.ALIGN_LEFT);
			cellShouldHaveTextValue(wagonSheet, wagon.getWagonKeeperName());
			cellShouldHaveTextValue(wagonSheet, wagon.getWagonType());
			cellShouldHaveNumericValue(wagonSheet, wagon.getWrittenTareWeightT().doubleValue());
			cellShouldHaveTextValue(wagonSheet, wagon.getTrackName());
			cellShouldHaveNumericValue(wagonSheet, wagon.getPosition());
			cellShouldHaveTextValue(wagonSheet, wagon.getContainerNames());
			cellShouldHaveTextValue(wagonSheet, exporter.getWagonStatusText(wagon.getStatus()));
			cellShouldHaveTextValue(wagonSheet, Long.toString(wagon.getOrderNo()));
			cellShouldHaveTextValue(wagonSheet, wagon.getEtranOut());
			cellShouldHaveTextValue(wagonSheet, wagon.getEtranRet());
			cellShouldHaveTextValue(wagonSheet, wagon.getOrderMaterialName());
			cellShouldHaveNumericValue(wagonSheet, wagon.getOrderQty().doubleValue(), RailDispoExporter.CELL_FORMAT_INT, CellStyle.ALIGN_RIGHT);
			cellShouldHaveTextValue(wagonSheet, wagon.getOrderUom());
			cellShouldHaveTextValue(wagonSheet, wagon.getShippintPtName());
			cellShouldHaveTextValue(wagonSheet, wagon.getLoadingPtName());
			cellShouldHaveTextValue(wagonSheet, wagon.getShippintPtStationNo());
			cellShouldHaveTextValue(wagonSheet, wagon.getShippintPtStationName());
			cellShouldHaveNumericValue(wagonSheet, wagon.getShippintPtCountryCd());
			cellShouldHaveTextValue(wagonSheet, wagon.getSoldToNumber());
			cellShouldHaveTextValue(wagonSheet, wagon.getSoldToName());
			cellShouldHaveTextValue(wagonSheet, wagon.getSoldToName2());
			cellShouldHaveTextValue(wagonSheet, wagon.getShipToNumber());
			cellShouldHaveTextValue(wagonSheet, wagon.getShipToName());
			cellShouldHaveTextValue(wagonSheet, wagon.getShipToName2());
			cellShouldHaveTextValue(wagonSheet, wagon.getShipToStationNo());
			cellShouldHaveTextValue(wagonSheet, wagon.getShipToStationName());
			cellShouldHaveNumericValue(wagonSheet, wagon.getShipToCountryCd());
			cellShouldHaveDateValue(wagonSheet, wagon.getManipulationTime(), RailDispoExporter.CELL_FORMAT_DATE_TIME);
			cellShouldHaveDateValue(wagonSheet, wagon.getDeliveryFrom(), RailDispoExporter.CELL_FORMAT_DATE);
			cellShouldHaveDateValue(wagonSheet, wagon.getDeliveryTo(), RailDispoExporter.CELL_FORMAT_DATE);
			cellShouldHaveTextValue(wagonSheet, wagon.getCarrierName());
			cellShouldHaveTextValue(wagonSheet, wagon.getLoadMaterial());
			cellShouldHaveNumericValue(wagonSheet, wagon.getLoadTare().doubleValue(), RailDispoExporter.CELL_FORMAT_DEC, CellStyle.ALIGN_RIGHT);
			cellShouldHaveNumericValue(wagonSheet, wagon.getLoadNet().doubleValue(), RailDispoExporter.CELL_FORMAT_DEC, CellStyle.ALIGN_RIGHT);
			cellShouldHaveNumericValue(wagonSheet, wagon.getLoadGross().doubleValue(), RailDispoExporter.CELL_FORMAT_DEC, CellStyle.ALIGN_RIGHT);
			cellShouldHaveTextValue(wagonSheet, wagon.getNhm());
			cellShouldHaveDateValue(wagonSheet, wagon.getToRailCo(), RailDispoExporter.CELL_FORMAT_DATE_TIME);
			cellShouldHaveDateValue(wagonSheet, wagon.getDeparture(), RailDispoExporter.CELL_FORMAT_DATE_TIME);
			cellShouldHaveDateValue(wagonSheet, wagon.getArrival(), RailDispoExporter.CELL_FORMAT_DATE_TIME);
			cellShouldHaveTextValue(wagonSheet, wagon.getNoteRailwayDisp());
			cellShouldHaveTextValue(wagonSheet, wagon.getWagonNoteRailwaySpedition());
			cellShouldHaveTextValue(wagonSheet, wagon.getNoteDispatching());
			cellShouldHaveTextValue(wagonSheet, wagon.getWagonNoteDisposition());

		}

		// Some columns are resized after autoresize 
		validateColumnWidthWithDescriptor(wagonSheet, RailDispoExporterColumnDescriptor.WAGON_KEEPER);
		validateColumnWidthWithDescriptor(wagonSheet, RailDispoExporterColumnDescriptor.WAGON_ETRAN_OUT);
		validateColumnWidthWithDescriptor(wagonSheet, RailDispoExporterColumnDescriptor.WAGON_ETRAN_RET);
		validateColumnWidthWithDescriptor(wagonSheet, RailDispoExporterColumnDescriptor.WAGON_WEIGHING_NOTE_ORDER);
		validateColumnWidthWithDescriptor(wagonSheet, RailDispoExporterColumnDescriptor.WAGON_WEIGHING_NOTE_WAGON);
		validateColumnWidthWithDescriptor(wagonSheet, RailDispoExporterColumnDescriptor.WAGON_SCHEDULING_NOTE_ORDER);
		validateColumnWidthWithDescriptor(wagonSheet, RailDispoExporterColumnDescriptor.WAGON_SCHEDULING_NOTE_WAGON);

	}

	private void validateColumnWidthWithDescriptor(Sheet wagonSheet, RailDispoExporterColumnDescriptor descriptor) {
		int columnWidth = wagonSheet.getColumnWidth(descriptor.getColumnIndex());
		Assert.assertEquals(descriptor.getCalculatedWidth(), columnWidth);
	}

	private void validateSheetCommon(Sheet sheet, String sheetName) {

		rowIndex = 0;
		cellIndex = 0;

		Assert.assertNotNull(sheet);
		Assert.assertEquals(sheetName, sheet.getSheetName());

		cellShouldHaveTextValueInNextRegion(sheet, 1, getMessage(RailDispoExporter.PLANT_LABEL)); 											// 'Plant'
		cellShouldHaveTextValueInNextRegion(sheet, 4, parameters.getPlant().getPlantName());														// 'Crh-Werk Eclépens'
		cellShouldHaveTextValueInNextRegion(sheet, 1, getMessage(RailDispoExporter.EXPORTING_USER_LABEL)); 							// 'Exporting user'
		cellShouldHaveTextValueInNextRegion(sheet, 4, parameters.getExportingUser()); 																// Michael Schmid (mschmid)

		nextLine();

		cellShouldHaveTextValueInNextRegion(sheet, 1, getMessage(RailDispoExporter.TIMEZONE_LABEL));										// 'Time zone'
		cellShouldHaveTextValueInNextRegion(sheet, 4, utcOffset);																									// 'UTC+02:00'
		cellShouldHaveTextValueInNextRegion(sheet, 1, getMessage(RailDispoExporter.TIME_OF_EXPORT_LABEL)); 							// 'Time of export'
		cellShouldHaveTextValueInNextRegion(sheet, 4, parameters.getTimeOfExport());																	// '21.10.2013  16:06:00'

		nextLine();

		cellShouldHaveTextValueInNextRegion(sheet, 5, getMessage(RailDispoExporter.SP_AND_TRACKS_INC_LABEL));					// 'Shipping points and tracks included'
		cellShouldHaveTextValueInNextRegion(sheet, 4, String.format("%s (%s)", shippintPtName, trackName));								// 'ADN Rail Münchenstein (V1, V3), ADN Rail Siggenthal'

		nextLine();

		cellShouldHaveTextValueInNextRegion(sheet, 5, getMessage(RailDispoExporter.WAGON_STATUS_INC_LABEL));						// 'Wagon statuses included'
		cellShouldHaveTextValueInNextRegion(sheet, 4, "incoming, submitted");								// 'at shippingpoint, loaded, submitted'

	}

	private void cellShouldHaveTextValueInNextRegion(Sheet sheet, int regionCellWidth, String expectedCellValue) {

		CellRangeAddress region = sheet.getMergedRegion(regionIndex);

		if (region == null) {
			Assert.fail(String.format("Region [%d] does not exist!", regionIndex));
		}

		int lastCelIndex = cellIndex + regionCellWidth;

		for (int colInd = cellIndex; colInd < lastCelIndex; colInd++) {
			String assertationMsg 
				= String.format("Cell [%d:%d] is not part of merged region [%d:%d][%d:%d]", rowIndex, colInd, rowIndex, cellIndex, rowIndex, lastCelIndex);
			Assert.assertTrue(assertationMsg, region.isInRange(rowIndex, colInd));
		}

		cellShouldHaveTextValue(sheet, expectedCellValue);

		cellIndex += lastCelIndex - cellIndex;
		regionIndex++;

	}

	private void cellShouldHaveDateValue(Sheet sheet, Calendar expectedCellValue, String format) {

		Cell cell = cellShouldHaveDateValue(sheet, expectedCellValue);

		if (expectedCellValue == null) {
			return;
		}

		CellStyle cellStyle = cell.getCellStyle();
		if (cellStyle == null) {
			Assert.fail("Cell does not have formatting!");
		}

		short actual = cellStyle.getDataFormat();
		short expected = createHelper.createDataFormat().getFormat(format);

		Assert.assertEquals(String.format("Cell should have format: %s", format), expected, actual);

	}

	private Cell cellShouldHaveDateValue(Sheet sheet, Calendar expectedCellValue) {

		int cellNum = cellIndex++;

		Cell cell = cellExists(sheet, rowIndex, cellNum);

		Calendar actualCellValue = Calendar.getInstance();

		if (actualCellValue == null && expectedCellValue == null) {
			return cell;
		}

		actualCellValue.setTime(cell.getDateCellValue());

		if (actualCellValue.get(Calendar.YEAR) != expectedCellValue.get(Calendar.YEAR) ||
			actualCellValue.get(Calendar.MONTH) != expectedCellValue.get(Calendar.MONTH) ||
			actualCellValue.get(Calendar.DAY_OF_YEAR) != expectedCellValue.get(Calendar.DAY_OF_YEAR) ||
			actualCellValue.get(Calendar.HOUR_OF_DAY) != expectedCellValue.get(Calendar.HOUR_OF_DAY) ||
			actualCellValue.get(Calendar.MINUTE) != expectedCellValue.get(Calendar.MINUTE) ||
			actualCellValue.get(Calendar.SECOND) != expectedCellValue.get(Calendar.SECOND)) {
			Assert.fail(String.format("Row %d Cell %d expected=%s actual=%s", rowIndex, cellNum, expectedCellValue, actualCellValue));
		}

		return cell;

	}

	private void cellShouldHaveTextValue(Sheet sheet, String expectedCellValue, String format, String alignment) {

		Cell cell = cellShouldHaveTextValue(sheet, expectedCellValue);

		CellStyle cellStyle = cell.getCellStyle();
		if (cellStyle == null) {
			Assert.fail("Cell does not have formatting!");
		}

		short actualFormula = cellStyle.getDataFormat();
		short expectedFormula = createHelper.createDataFormat().getFormat(format);

		Assert.assertEquals(String.format("Cell should have format: %s", format), expectedFormula, actualFormula);

	}

	private Cell cellShouldHaveTextValue(Sheet sheet, String expectedCellValue) {

		int cellNum = cellIndex++;

		Cell cell = cellExists(sheet, rowIndex, cellNum);

		if (expectedCellValue == null) {
			return cell;
		}

		String actualCellValue = cell.getStringCellValue();

		Assert.assertEquals(String.format("Row %d Cell %d expected=%s actual=%s", rowIndex, cellNum, expectedCellValue, actualCellValue), expectedCellValue, actualCellValue);

		return cell;

	}

	private void cellShouldHaveNumericValue(Sheet sheet, double expectedCellValue, String format, short expectedCellAlignment) {

		Cell cell = cellShouldHaveNumericValue(sheet, expectedCellValue);

		CellStyle cellStyle = cell.getCellStyle();

		if (cellStyle == null) {
			Assert.fail("Cell does not have formatting!");
		}

		short actualFormat = cellStyle.getDataFormat();
		short expectedFormat = createHelper.createDataFormat().getFormat(format);

		Assert.assertEquals(String.format("Cell should have format: %s", format), expectedFormat, actualFormat);

		short actualAlignment = cellStyle.getAlignment();

		Assert.assertEquals(String.format("Cell should have alignment: %d", expectedCellAlignment), expectedCellAlignment, actualAlignment);

	}

	private Cell cellShouldHaveNumericValue(Sheet sheet, double expectedCellValue) {

		int cellNum = cellIndex++;

		Cell cell = cellExists(sheet, rowIndex, cellNum);

		double numericCellValue = cell.getNumericCellValue();

		Assert.assertEquals(String.format("Row %d Cell %d expected=%s actual=%s", rowIndex, cellNum, expectedCellValue, numericCellValue), expectedCellValue, numericCellValue, 0);

		return cell;

	}

	private Cell cellExists(Sheet sheet, int rowNum, int cellNum) {

		Row row = sheet.getRow(rowNum);
		if (row == null) {
			throw new IllegalArgumentException(String.format("Row %d does not exist", rowNum));
		}

		Cell cell = row.getCell(cellNum);
		if (cell == null) {
			throw new IllegalArgumentException(String.format("Cell %d does not exist in row %d", cellNum, rowNum));
		}

		return cell;

	}

	private void nextLine() {
		rowIndex++;
		cellIndex = 0;
	}

	private RailDispoExportWagonRow buildWagonDispoRow(Long wagonStatusCd) {
		RailDispoExportWagonRow wagonDispoEntity = new RailDispoExportWagonRow();
		wagonDispoEntity.setWagonNo(nextWagonNo++);
		wagonDispoEntity.setWagonKeeperName(wagonKeeper);
		wagonDispoEntity.setWagonType(wagonType);
		wagonDispoEntity.setWrittenTareWeightT(writtenTareWeightT);
		wagonDispoEntity.setTrackName(trackName);
		wagonDispoEntity.setPosition(position);
		wagonDispoEntity.setContainerNames(containerName);
		wagonDispoEntity.setStatus(wagonStatusCd);
		wagonDispoEntity.setOrderNo(orderNo);
		wagonDispoEntity.setEtranOut(etranOut);
		wagonDispoEntity.setEtranRet(etranRet);
		wagonDispoEntity.setOrderMaterialName(orderMaterialName);
		wagonDispoEntity.setOrderQty(orderQty);
		wagonDispoEntity.setOrderUom(orderUom);
		wagonDispoEntity.setShippintPtName(shippintPtName);
		wagonDispoEntity.setLoadingPtName(loadingPtName);
		wagonDispoEntity.setShippintPtStationNo(shippintPtStationNo);
		wagonDispoEntity.setShippintPtStationName(shippintPtStationName);
		wagonDispoEntity.setShippintPtCountryCd(shippintPtCountryCd);
		wagonDispoEntity.setSoldToNumber(soldToNumber);
		wagonDispoEntity.setSoldToName(soldToName);
		wagonDispoEntity.setSoldToName2(soldToName2);
		wagonDispoEntity.setShipToNumber(shipToNumber);
		wagonDispoEntity.setShipToName(shipToName);
		wagonDispoEntity.setShipToName2(shipToName2);
		wagonDispoEntity.setShipToStationNo(shipToStationNo);
		wagonDispoEntity.setShipToStationName(shipToStationName);
		wagonDispoEntity.setShipToCountryCd(shipToCountryCd);
		wagonDispoEntity.setManipulationTime(manipulationTime);
		wagonDispoEntity.setDeliveryFrom(deliveryFrom);
		wagonDispoEntity.setDeliveryTo(deliveryTo);
		wagonDispoEntity.setCarrierName(carrierName);
		wagonDispoEntity.setLoadMaterial(loadMaterial);
		wagonDispoEntity.setLoadTare(loadTare);
		wagonDispoEntity.setLoadNet(loadNet);
		wagonDispoEntity.setLoadGross(loadGross);
		wagonDispoEntity.setNhm(nhm);
		wagonDispoEntity.setToRailCo(toRailCo);
		wagonDispoEntity.setDeparture(departure);
		wagonDispoEntity.setArrival(arrival);
		wagonDispoEntity.setNoteRailwayDisp(noteRailwayDisp);
		wagonDispoEntity.setWagonNoteRailwaySpedition(noteRailwaySped);
		wagonDispoEntity.setNoteDispatching(noteDisp);
		wagonDispoEntity.setWagonNoteDisposition(wagonNoteDisp);
		return wagonDispoEntity;
	}	

	private RailDispoExportOrderRow buildOrderDispoRow() {
		RailDispoExportOrderRow orderDispoEntity = new RailDispoExportOrderRow();
		orderDispoEntity.setOrderNumber(orderNo);
		orderDispoEntity.setOrderMaterialName(orderMaterialName);
		orderDispoEntity.setOrderQty(orderQty);
		orderDispoEntity.setOrderUom(orderUom);
		orderDispoEntity.setShippintPtName(shippintPtName);
		orderDispoEntity.setLoadingPtName(loadingPtName);
		orderDispoEntity.setShippintPtStationNo(shippintPtStationNo);
		orderDispoEntity.setShippintPtStationName(shippintPtStationName);
		orderDispoEntity.setShippintPtCountryCd(shippintPtCountryCd);
		orderDispoEntity.setSoldToNumber(soldToNumber);
		orderDispoEntity.setSoldToName(soldToName);
		orderDispoEntity.setSoldToName2(soldToName2);
		orderDispoEntity.setShipToNumber(shipToNumber);
		orderDispoEntity.setShipToName(shipToName);
		orderDispoEntity.setShipToName2(shipToName2);
		orderDispoEntity.setShipToStationNo(shipToStationNo);
		orderDispoEntity.setShipToStationName(shipToStationName);
		orderDispoEntity.setShipToCountryCd(shipToCountryCd);
		orderDispoEntity.setManipulationTime(manipulationTime);
		orderDispoEntity.setDeliveryFrom(deliveryFrom);
		orderDispoEntity.setDeliveryTo(deliveryTo);
		orderDispoEntity.setCarrierName(carrierName);
		orderDispoEntity.setShipToPrefs(shipToWagonPrefs);
		orderDispoEntity.setNoteRailwayDisp(noteRailwayDisp);
		orderDispoEntity.setNoteDispatching(noteDisp);
		return orderDispoEntity;
	}

	private String getMessage(String messageKey) {
		return Message.lookup(parameters.getLocale(), messageKey);
	}

}
