/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.order.web.ajax;

import static java.util.stream.Collectors.toSet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.beans.GeneralDisplayNameDto;
import com.crh.logon.business.services.beans.ITrackReportFilterParametersDto;
import com.crh.logon.business.services.interfaces.ITrackReportingService;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.order.web.dto.FileTransfer;
import com.crh.logon.order.web.dto.ResponseDTO;
import com.crh.logon.persistence.entities.views.ItrackReportEntity;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.ResourceBundleUtil;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.utils.Authorization;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author AdNovum Informatik AG
 * <p>
 * The purpose of this class is to make business functions available to Ajax components.
 * @adnbus.service name="ITrackReportingAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.view.ItrackShiptoSapBpEntity"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.view.ItrackSoldtoSapBpEntity"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.order.web.dto.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.services.beans.DemandPlanningSapBpDTO"
 */
@AdnTrace
@SuppressWarnings("unused")
public class ITrackReportingAjaxService extends DwrServiceSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ITrackReportingAjaxService.class);

	private static final Set<Reason> ITRACK_REPORTING_REASONS = Stream.of(Reason.RES_ERROR_ITRACK_REPORTING_FILTER_REQUIRED,
			Reason.RES_ERROR_ITRACK_REPORTING_INVALID_DATE).collect(toSet());

	private FileTransfer toFileTransfer(String baseFileName, XSSFWorkbook workbook) {
		try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
			workbook.write(outStream);
			return toFileTransfer(baseFileName, "xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
					outStream.toByteArray());
		}
		catch (IOException e) {
			throw new LogonException(e);
		}
	}

	private FileTransfer toFileTransfer(String baseFileName, String extension, String mimeType,
			byte[] content) {
		String fileName = String.format("%s-%s.%s", baseFileName,
				DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(ZonedDateTime.now(getUserTimeZone().getZoneId())),
				extension);
		return new FileTransfer(fileName, mimeType, content);
	}


	public ResponseDTO<ListRange<GeneralDisplayNameDto>> fetchShippingpointList(int start, int count, String orderBy,
			String query) {
		Authorization.authorize(Authorization.ITRACK_REPORTING);

		return responseHandler(() -> {
			ITrackReportingService itrackReportingService = getServiceLocator().fetchITrackReportingService();

			ListRange<GeneralDisplayNameDto> listRange = new ListRange<>();
			listRange.setData(
					itrackReportingService.fetchShippingpointList(start, count, query).toArray(new GeneralDisplayNameDto[0]));
			listRange.setTotalSize(itrackReportingService.countShippingpointList(query));
			return listRange;
		});
	}

	public ResponseDTO<ListRange<GeneralDisplayNameDto>> fetchCommCarrierList(int start, int count, String orderBy,
			String query) {
		Authorization.authorize(Authorization.ITRACK_REPORTING);

		return responseHandler(() -> {
			ITrackReportingService itrackReportingService = getServiceLocator().fetchITrackReportingService();

			ListRange<GeneralDisplayNameDto> listRange = new ListRange<>();
			listRange.setData(
					itrackReportingService.fetchCommCarrierList(start, count, query).toArray(new GeneralDisplayNameDto[0]));
			listRange.setTotalSize(itrackReportingService.countCommCarrierList(query));
			return listRange;
		});
	}

	public ResponseDTO<ListRange<GeneralDisplayNameDto>> fetchExecCarrierList(int start, int count, String orderBy,
			String query) {
		Authorization.authorize(Authorization.ITRACK_REPORTING);

		return responseHandler(() -> {
			ITrackReportingService itrackReportingService = getServiceLocator().fetchITrackReportingService();

			ListRange<GeneralDisplayNameDto> listRange = new ListRange<>();
			listRange.setData(
					itrackReportingService.fetchExecCarrierList(start, count, query).toArray(new GeneralDisplayNameDto[0]));
			listRange.setTotalSize(itrackReportingService.countExecCarrierList(query));
			return listRange;
		});
	}

	public ResponseDTO<ListRange<GeneralDisplayNameDto>> fetchSareaList(int start, int count, String orderBy,
			String query) {
		Authorization.authorize(Authorization.ITRACK_REPORTING);

		return responseHandler(() -> {
			ITrackReportingService itrackReportingService = getServiceLocator().fetchITrackReportingService();

			ListRange<GeneralDisplayNameDto> listRange = new ListRange<>();
			listRange.setData(itrackReportingService.fetchSareaList(start, count, query).toArray(new GeneralDisplayNameDto[0]));
			listRange.setTotalSize(itrackReportingService.countSareaList(query));
			return listRange;
		});
	}

	public ResponseDTO<ListRange<GeneralDisplayNameDto>> fetchSoldtoList(int start, int count, String orderBy,
			String query) {
		Authorization.authorize(Authorization.ITRACK_REPORTING);

		return responseHandler(() -> {
			ITrackReportingService itrackReportingService = getServiceLocator().fetchITrackReportingService();

			ListRange<GeneralDisplayNameDto> listRange = new ListRange<>();
			listRange.setData(itrackReportingService.fetchSoldtoList(start, count, query).toArray(new GeneralDisplayNameDto[0]));
			listRange.setTotalSize(itrackReportingService.countSoldtoList(query));
			return listRange;
		});
	}

	public ResponseDTO<ListRange<GeneralDisplayNameDto>> fetchShiptoList(int start, int count, String orderBy,
			String query) {
		Authorization.authorize(Authorization.ITRACK_REPORTING);

		return responseHandler(() -> {
			ITrackReportingService itrackReportingService = getServiceLocator().fetchITrackReportingService();

			ListRange<GeneralDisplayNameDto> listRange = new ListRange<>();
			listRange.setData(itrackReportingService.fetchShiptoList(start, count, query)
					.toArray(new GeneralDisplayNameDto[0]));
			listRange.setTotalSize(itrackReportingService.countShiptoList(query));
			return listRange;
		});
	}

	public ResponseDTO<FileTransfer> getReportData(ITrackReportFilterParametersDto parameters) {
		Authorization.authorize(Authorization.ITRACK_REPORTING);
		parameters.setUserTimeZone(getUserTimeZone());
		parameters.validate();
		ITrackReportingService itrackReportingService = getServiceLocator().fetchITrackReportingService();
		List<ItrackReportEntity> reportEntries = itrackReportingService.fetchReportList(parameters);


		Locale userLocale = getUserLocale();
		ResourceBundle bundle = ResourceBundleUtil.getBundle(userLocale);

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();
		// create header, row 1
		int rowNum = -1;
		sheet.createRow(++rowNum);

		// create header, row 2
		String flowTypeTitle = bundle.getString("itrackreporting.xls.flow.type.column");
		String salesOrganizationTitle = bundle.getString("itrackreporting.xls.sales.organization.column");
		String orderNumberTitle = bundle.getString("itrackreporting.xls.ordernumber.column");
		String commCarrierSapIdTitle = bundle.getString("itrackreporting.xls.comm.carrier.sapid.column");
		String execCarrierSapIdTitle = bundle.getString("itrackreporting.xls.exec.carrier.sapid.column");
		String plateNrTitle = bundle.getString("itrackreporting.xls.platenr.column");
		String plantCodeTitle = bundle.getString("itrackreporting.xls.plant.code.column");
		String loadingStartTitle = bundle.getString("itrackreporting.xls.loading.start.column");
		String loadingEndTitle = bundle.getString("itrackreporting.xls.loading.end.column");
		String shipmentEndTitle = bundle.getString("itrackreporting.xls.shipment.end.column");
		String tollFeeTitle = bundle.getString("itrackreporting.xls.toll.fee.column");
		String currencyTitle = bundle.getString("itrackreporting.xls.currency.column");
		String commcarrierTitle = bundle.getString("itrackreporting.xls.commcarrier.column");
		String execCarrierTitle = bundle.getString("itrackreporting.xls.execcarrier.column");
		String sareaTitle = bundle.getString("itrackreporting.xls.salesarea.column");
		String shippingpointTitle = bundle.getString("itrackreporting.xls.shippingpoint.column");
		String shiptoTitle = bundle.getString("itrackreporting.xls.shipto.column");
		String soldtoTitle = bundle.getString("itrackreporting.xls.soldto.column");
		String unloadingTimeTitle = bundle.getString("itrackreporting.xls.unloadingtime.column");
		String deliveryWindowTitle = bundle.getString("itrackreporting.xls.deliverywindow.column");
		String differenceTitle = bundle.getString("itrackreporting.xls.difference.column");
		String categoryTitle = bundle.getString("itrackreporting.xls.category.column");
		String distanceTitle = bundle.getString("itrackreporting.xls.distance.column");

		XSSFRow rowH2 = sheet.createRow(++rowNum);
		rowH2.createCell(0).setCellValue(flowTypeTitle);
		rowH2.createCell(1).setCellValue(salesOrganizationTitle);
		rowH2.createCell(2).setCellValue(orderNumberTitle);
		rowH2.createCell(3).setCellValue(commCarrierSapIdTitle);
		rowH2.createCell(4).setCellValue(execCarrierSapIdTitle);
		rowH2.createCell(5).setCellValue(plateNrTitle);
		rowH2.createCell(6).setCellValue(plantCodeTitle);
		rowH2.createCell(7).setCellValue(loadingStartTitle);
		rowH2.createCell(8).setCellValue(loadingEndTitle);
		rowH2.createCell(9).setCellValue(shipmentEndTitle);
		rowH2.createCell(10).setCellValue(tollFeeTitle);
		rowH2.createCell(11).setCellValue(currencyTitle);
		rowH2.createCell(12).setCellValue(commcarrierTitle);
		rowH2.createCell(13).setCellValue(execCarrierTitle);
		rowH2.createCell(14).setCellValue(sareaTitle);
		rowH2.createCell(15).setCellValue(shippingpointTitle);
		rowH2.createCell(16).setCellValue(shiptoTitle);
		rowH2.createCell(17).setCellValue(soldtoTitle);
		rowH2.createCell(18).setCellValue(unloadingTimeTitle);
		rowH2.createCell(19).setCellValue(deliveryWindowTitle);
		rowH2.createCell(20).setCellValue(differenceTitle);
		rowH2.createCell(21).setCellValue(categoryTitle);
		rowH2.createCell(22).setCellValue(distanceTitle);

		for (ItrackReportEntity reportEntity : reportEntries) {
			XSSFRow row = sheet.createRow(++rowNum);
			row.createCell(0).setCellValue(reportEntity.getFlowType());
			row.createCell(1).setCellValue(reportEntity.getSareaSorgCd());
			row.createCell(2).setCellValue(reportEntity.getOrderNumber());
			row.createCell(3).setCellValue(reportEntity.getCommCarrierVSapId());
			row.createCell(4).setCellValue(reportEntity.getExecCarrierVSapId());
			row.createCell(5).setCellValue(reportEntity.getTruckPlateNo());
			row.createCell(6).setCellValue(reportEntity.getSpPlantCd());

			String loadStartDate = reportEntity.getLoadStartDate() != null ?
					DateTimeUtils.formatDayMonthYear(reportEntity.getLoadStartDate(), getUserLocale()) : "";
			row.createCell(7).setCellValue(loadStartDate);

			String loadEndDate = reportEntity.getLoadEndDate() != null ?
					DateTimeUtils.formatDayMonthYear(reportEntity.getLoadEndDate(), getUserLocale()) : "";
			row.createCell(8).setCellValue(loadEndDate);

			String shipmentEnd = reportEntity.getUnloadingTime() != null ?
					DateTimeUtils.formatDayMonthYear(reportEntity.getUnloadingTime(), getUserLocale()) : "";
			row.createCell(9).setCellValue(shipmentEnd);

			if (reportEntity.getTollFee() != null){
				row.createCell(10).setCellValue(reportEntity.getTollFee().doubleValue());
			}

			row.createCell(11).setCellValue("HUF");
			row.createCell(12).setCellValue(reportEntity.getCommCarrierDisplayName());
			row.createCell(13).setCellValue(reportEntity.getExecCarrierDisplayName());
			row.createCell(14).setCellValue(reportEntity.getSareaSareaDisplayLongname());
			row.createCell(15).setCellValue(reportEntity.getSpShippingpointName());
			row.createCell(16).setCellValue(reportEntity.getShiptoName1());
			row.createCell(17).setCellValue(reportEntity.getSoldtoName1());

			String unloadingTime = reportEntity.getUnloadingTime() != null ?
					DateTimeUtils.formatDayMonthYear(reportEntity.getUnloadingTime(), getUserLocale()) + " " + DateTimeUtils
							.formatHoursMinutes(reportEntity.getUnloadingTime())
					: "";
			row.createCell(18).setCellValue(unloadingTime);

			row.createCell(19).setCellValue(
					DateTimeUtils.formatDayMonthYear(reportEntity.getDeliveryFromDat(), getUserLocale()) + " " + DateTimeUtils
							.formatHoursMinutes(reportEntity.getDeliveryFromDat()) +
							" - " +
							DateTimeUtils.formatDayMonthYear(reportEntity.getDeliveryToDat(), getUserLocale()) + " "
							+ DateTimeUtils.formatHoursMinutes(reportEntity.getDeliveryToDat()));

			Long diff = null;
			String category = "";
			if (reportEntity.getUnloadingTime() == null) {
				category = "n/a";
			}
			else if (reportEntity.getUnloadingTime().before(reportEntity.getDeliveryFromDat())) {
				diff = reportEntity.getDiffVsDelFrom();
				category = bundle.getString("itrackreporting.xls.category.early");
			}
			else if (reportEntity.getUnloadingTime().after(reportEntity.getDeliveryToDat())) {
				diff = reportEntity.getDiffVsDelTo();
				if (reportEntity.getDiffVsDelTo() <= 60 )
					category = bundle.getString("itrackreporting.xls.category.late.less.than.1.hour");
				else if (reportEntity.getDiffVsDelTo() > 60 && reportEntity.getDiffVsDelTo() <= 120 )
					category = bundle.getString("itrackreporting.xls.category.late.1.to.2.hour");
				else if (reportEntity.getDiffVsDelTo() > 120 && reportEntity.getDiffVsDelTo() <= 180 )
					category = bundle.getString("itrackreporting.xls.category.late.2.to.3.hour");
				else if (reportEntity.getDiffVsDelTo() > 180 && reportEntity.getDiffVsDelTo() <= 240 )
					category = bundle.getString("itrackreporting.xls.category.late.3.to.4.hour");
				else
					category = bundle.getString("itrackreporting.xls.category.late.above.4.hour");
			}
			else {
				diff = 0L;
				category = bundle.getString("itrackreporting.xls.category.on.time");
			}

			if (diff != null){
				row.createCell(20).setCellValue(diff.doubleValue());
			}

			row.createCell(21).setCellValue(category);

			if (reportEntity.getItrackDistanceKm() != null){
				row.createCell(22).setCellValue(reportEntity.getItrackDistanceKm().doubleValue());
			}
		}

		return responseHandler(() -> toFileTransfer(bundle.getString("itrackreporting.xls.report.filename"), workbook));

	}


	private <T> ResponseDTO<T> responseHandler(Supplier<T> supplier) {
		try {
			return ResponseDTO.success(supplier.get());
		}
		catch (LogonException e) {
			Reason reason = e.getReason();
			if (ITRACK_REPORTING_REASONS.contains(reason)) {
				return ResponseDTO.error(Message.lookup(getUserLocale(), reason.getReason()), reason);
			}
			throw e;
		}
	}

}
