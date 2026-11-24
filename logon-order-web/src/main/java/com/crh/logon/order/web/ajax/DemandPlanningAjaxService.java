/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.order.web.ajax;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.beans.DemandPlanningMatDTO;
import com.crh.logon.business.services.beans.DemandPlanningSapBpDTO;
import com.crh.logon.business.services.interfaces.DemandPlanningService;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.order.web.dto.DemandPlanDiffDTO;
import com.crh.logon.order.web.dto.DemandPlanExpectedDTO;
import com.crh.logon.order.web.dto.DemandPlanMatDTO;
import com.crh.logon.order.web.dto.ResponseDTO;
import com.crh.logon.order.web.dto.FileTransfer;
import com.crh.logon.persistence.entities.views.DemandPlanDiffEntity;
import com.crh.logon.persistence.entities.views.DemandPlanPlanEntity;
import com.crh.logon.persistence.entities.views.SapMaterialDescEntity;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.ResourceBundleUtil;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.utils.Authorization;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author AdNovum Informatik AG
 * <p>
 * The purpose of this class is to make business functions available to Ajax components.
 * @adnbus.service name="DemandPlanningAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.view.DemandPlanPlanEntity"
 * exclude="shiptoSapBpId, startDate, endDate"/>
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.order.web.dto.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.services.beans.DemandPlanningSapBpDTO"
 */
@AdnTrace
@SuppressWarnings("unused")
public class DemandPlanningAjaxService extends DwrServiceSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DemandPlanningAjaxService.class);

	private static final MathContext MC_DELTA = new MathContext(4);

	private static final String COLUMN_SHIPTO_KEY = "demandplan.expected.column.shipto";

	private static final Set<Reason> DEMAND_PLAN_REASONS = Stream.of(Reason.RES_ERROR_DEMANDPLAN_INVALID_DATE,
			Reason.RES_ERROR_DEMANDPLAN_INVALID_WEIGHT, Reason.RES_ERROR_DEMANDPLAN_NO_SHIPTO,
			Reason.RES_ERROR_DEMANDPLAN_NO_MAT, Reason.RES_ERROR_DEMANDPLAN_NO_PLAN,
			Reason.RES_ERROR_DEMANDPLAN_MAT_CANNOT_CHANGE, Reason.RES_ERROR_DEMANDPLAN_MAT_NOT_UNIQUE)
			.collect(toSet());

	private static final Collector<DemandPlanDiffEntity, ?, DiffCombiner> DIFF_REDUCING_COLLECTOR = reducing(
			new DiffCombiner(null, null),
			curr -> new DiffCombiner(curr.getPlannedQty(), curr.getDeliveryQty()),
			(acc1, acc2) -> new DiffCombiner(acc1.planned.add(acc2.planned),
					acc1.ordered.add(acc2.ordered)));

	private static String buildMaterialText(SapMaterialDescEntity desc, LongFunction<String> plantNameFetcher) {
		String plantName = plantNameFetcher.apply(desc.getSapMaterialId());
		// copy from DeliverySettingServiceBean.resolveFields
		return desc.getTxt() + " (" + plantName + ")";
	}

	private static String buildMaterialText(Long matId) {
		// copy from DeliverySettingServiceBean.resolveFields
		LOG.warn("Material description for SAP material id. {} missing in user's language, displaying fallback.", matId);

		return matId + "_NO_LANG_DESC";
	}

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

	private LocalDate toLocalDate(String date) {
		return LocalDate.parse(date);
	}

	private LocalDate toLocalDateForUser(Date date) {
		TimeZone userTimeZone = getUserTimeZone();
		return ZonedDateTime.ofInstant(date.toInstant(), userTimeZone.getZoneId()).toLocalDate();
	}

	private Map<Long, String> toMaterialDescriptionMap(DemandPlanningService demandPlanningService, Set<Long> materialIds,
			LocalDate startLocalDate, LocalDate endLocalDate, boolean sorted) {
		List<DemandPlanningMatDTO> demandPlanMats =
				demandPlanningService.fetchMaterialList(startLocalDate, endLocalDate, new ArrayList<>(materialIds));

		Map<Long, String> map = demandPlanMats.stream()
				.collect(toMap(DemandPlanningMatDTO::getSapMaterialId, DemandPlanningMatDTO::getPlantName));

		return toMaterialDescriptionMap(materialIds, map::get, sorted);
	}

	private Map<Long, String> toMaterialDescriptionMap(Set<Long> materialIds, LongFunction<String> plantNameFetcher,
			boolean sorted) {
		if (materialIds.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Long, String> result = getServiceLocator().getDispokarteService()
				.fetchSapMaterialDescByLang(new ArrayList<>(materialIds), getUserLocale().getLanguage())
				.stream().collect(toMap(SapMaterialDescEntity::getSapMaterialId, it -> buildMaterialText(it, plantNameFetcher),
						(u, v) -> {
							throw new IllegalStateException(String.format("Duplicate key %s", u));
						}, sorted ? TreeMap::new : HashMap::new));
		// create copy to not modify the original set
		Set<Long> ids = new HashSet<>(materialIds);
		ids.removeAll(result.keySet());
		// add elements that didn't have matching translations
		ids.forEach(it -> result.put(it, buildMaterialText(it)));

		return result;
	}

	private List<DemandPlanMatDTO> toMaterials(List<DemandPlanningMatDTO> demandPlanMats) {
		Map<Long, DemandPlanningMatDTO> map =
				demandPlanMats.stream().collect(toMap(DemandPlanningMatDTO::getSapMaterialId, Function.identity()));

		Map<Long, String> matDescMap = toMaterialDescriptionMap(map.keySet(), id -> map.get(id).getPlantName(), false);


		return map.values().stream()
				.map(entry -> new DemandPlanMatDTO(entry.getSapMaterialId(), matDescMap.get(entry.getSapMaterialId()),
						entry.isAvailable())).sorted(Comparator.comparing(DemandPlanMatDTO::getName)).collect(toList());
	}

	public ResponseDTO<ListRange<DemandPlanningSapBpDTO>> fetchSoldtoList(int start, int count, String orderBy,
			String query) {
		Authorization.authorize(Authorization.DEMAND_PLANNING);

		return responseHandler(() -> {
			DemandPlanningService demandPlanningService = getServiceLocator().fetchDemandPlanningService();

			ListRange<DemandPlanningSapBpDTO> listRange = new ListRange<>();
			listRange.setData(demandPlanningService.fetchSoldtoList(start, count, query).toArray(new DemandPlanningSapBpDTO[0]));
			listRange.setTotalSize(demandPlanningService.countSoldtoList(query));
			return listRange;
		});
	}

	public ResponseDTO<ListRange<DemandPlanningSapBpDTO>> fetchShiptoList(int start, int count, String orderBy,
			Long soldtoSapBpId, String query) {
		Authorization.authorize(Authorization.DEMAND_PLANNING);

		return responseHandler(() -> {
			DemandPlanningService demandPlanningService = getServiceLocator().fetchDemandPlanningService();

			ListRange<DemandPlanningSapBpDTO> listRange = new ListRange<>();
			listRange.setData(demandPlanningService.fetchShiptoList(start, count, soldtoSapBpId, query)
					.toArray(new DemandPlanningSapBpDTO[0]));
			listRange.setTotalSize(demandPlanningService.countShiptoList(soldtoSapBpId, query));
			return listRange;
		});
	}

	public ResponseDTO<List<DemandPlanPlanEntity>> fetchPlanningList(String date, long shiptoSapBpId) {
		Authorization.authorize(Authorization.DEMAND_PLANNING);

		return responseHandler(() -> {
			DemandPlanningService demandPlanningService = getServiceLocator().fetchDemandPlanningService();
			return demandPlanningService.fetchPlanningList(toLocalDate(date), null, shiptoSapBpId);
		});
	}

	public ResponseDTO<List<DemandPlanMatDTO>> fetchMaterialList(String date, long shiptoSapBpId) {
		Authorization.authorize(Authorization.DEMAND_PLANNING);

		return responseHandler(() -> {
			DemandPlanningService demandPlanningService = getServiceLocator().fetchDemandPlanningService();
			List<DemandPlanningMatDTO> demandPlanMats = demandPlanningService.fetchMaterialList(toLocalDate(date),
					shiptoSapBpId);
			return toMaterials(demandPlanMats);
		});
	}

	public ResponseDTO<Long> createPlanning(String date, long shiptoSapBpId, long sapMaterialId, BigDecimal weight) {
		Authorization.authorize(Authorization.DEMAND_PLANNING);

		return responseHandler(() -> {
			DemandPlanningService demandPlanningService = getServiceLocator().fetchDemandPlanningService();
			return demandPlanningService.createPlanning(toLocalDate(date), shiptoSapBpId, sapMaterialId, weight,
					Authorization.isAllowed(Authorization.DEMAND_PLANNING_ADMIN_RIGHTS));
		});
	}

	public ResponseDTO<Void> updatePlanning(long demandPlanId, long sapMaterialId, BigDecimal weight) {
		Authorization.authorize(Authorization.DEMAND_PLANNING);

		return responseHandler(() -> {
			DemandPlanningService demandPlanningService = getServiceLocator().fetchDemandPlanningService();
			demandPlanningService.updatePlanning(demandPlanId, sapMaterialId, weight,
					Authorization.isAllowed(Authorization.DEMAND_PLANNING_ADMIN_RIGHTS));

			return null;
		});
	}

	public ResponseDTO<Void> deletePlanning(long demandPlanId) {
		Authorization.authorize(Authorization.DEMAND_PLANNING);

		return responseHandler(() -> {
			DemandPlanningService demandPlanningService = getServiceLocator().fetchDemandPlanningService();
			demandPlanningService
					.deletePlanning(demandPlanId, Authorization.isAllowed(Authorization.DEMAND_PLANNING_ADMIN_RIGHTS));
			return null;
		});
	}

	public ResponseDTO<DemandPlanExpectedDTO> getExpectedReportData(Long soldtoSapBpId, Long shiptoSapBpId,
			String startDate, String endDate) {
		Authorization.authorize(Authorization.DEMAND_PLANNING_REPORT_EXPECTED_DEMAND);

		return fetchExpectedData(soldtoSapBpId, shiptoSapBpId, startDate, endDate, false,
				(plannings, materialDescMap, startLocalDate, endLocalDate) -> {
					DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd. MMMM", getUserLocale());
					List<String> dateHeaders = new ArrayList<>();
					Map<Long, DemandPlanExpectedDTO.DemandPlanExpectedRowDTO> materialMap = new HashMap<>();
					Date oldStartDate = null;
					int columnCounter = -1;
					for (DemandPlanPlanEntity planning : plannings) {
						if (!planning.getStartDate().equals(oldStartDate)) {
							// new period, create the date header for it
							dateHeaders
									.add(dateFormatter.format(toLocalDateForUser(planning.getStartDate())) + " - " + dateFormatter
											.format(toLocalDateForUser(planning.getEndDate())));
							oldStartDate = planning.getStartDate();
							columnCounter++;
						}
						// add the new item to the row
						materialMap.computeIfAbsent(planning.getSapMaterialId(),
								id -> new DemandPlanExpectedDTO.DemandPlanExpectedRowDTO(materialDescMap.get(id))
						).addAmountToColumn(planning.getPlannedWeightT(), columnCounter);
					}
					ArrayList<DemandPlanExpectedDTO.DemandPlanExpectedRowDTO> rows = new ArrayList<>(materialMap.values());
					// by default we sort by material name
					rows.sort(Comparator.comparing(DemandPlanExpectedDTO.DemandPlanExpectedRowDTO::getMaterialName));
					return new DemandPlanExpectedDTO(dateHeaders, rows);
				});
	}

	public ResponseDTO<FileTransfer> getExpectedReportXLS(Long soldtoSapBpId, Long shiptoSapBpId, String startDate,
			String endDate) {
		Authorization.authorize(Authorization.DEMAND_PLANNING_REPORT_EXPECTED_DEMAND);

		return fetchExpectedData(soldtoSapBpId, shiptoSapBpId, startDate, endDate, true,
				(plannings, materialDescMap, startLocalDate, endLocalDate) -> {
					Locale userLocale = getUserLocale();
					ResourceBundle bundle = ResourceBundleUtil.getBundle(userLocale);
					DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd. MMMM", userLocale);
					DemandPlanningService demandPlanningService = getServiceLocator().getDemandPlanningService();

					XSSFWorkbook workbook = new XSSFWorkbook();
					XSSFSheet sheet = workbook.createSheet();
					// create header row
					int rowNum = -1;
					XSSFRow rowH = sheet.createRow(++rowNum);
					rowH.createCell(0).setCellValue(bundle.getString(COLUMN_SHIPTO_KEY));

					// maintain an additional map that maps the start date to texts
					Map<Date, String> headerDateNames = new LinkedHashMap<>();
					// we build a map, where the first key is the shipto id, then material id, then the planning date, then total
					Map<Long, Map<Long, Map<Date, BigDecimal>>> dataMap =
							plannings.stream().peek(planning -> headerDateNames.computeIfAbsent(planning.getStartDate(),
									x -> dateFormatter.format(toLocalDateForUser(planning.getStartDate())) + " - " +
											dateFormatter.format(toLocalDateForUser(planning.getEndDate())))
							).collect(groupingBy(DemandPlanPlanEntity::getShiptoSapBpId,
									groupingBy(DemandPlanPlanEntity::getSapMaterialId,
											groupingBy(DemandPlanPlanEntity::getStartDate,
													mapping(DemandPlanPlanEntity::getPlannedWeightT,
															reducing(BigDecimal.ZERO, BigDecimal::add))))));

					// we fill out the header, using the fact that the sorting was by date in the plannings
					int index = 1;
					List<Date> dateHeaderList = new ArrayList<>(headerDateNames.size());
					for (Map.Entry<Date, String> dateHeader : headerDateNames.entrySet()) {
						rowH.createCell(index++).setCellValue(dateHeader.getValue());
						dateHeaderList.add(dateHeader.getKey());
					}
					rowH.createCell(index).setCellValue(bundle.getString("demandplan.expected.column.totalamount"));

					// we fetch the shiptos (just the ones with planning)
					List<DemandPlanningSapBpDTO> shiptos = demandPlanningService.fetchShiptoList(
							Optional.ofNullable(shiptoSapBpId).map(Collections::singletonList)
									.orElseGet(() -> new ArrayList<>(dataMap.keySet())), true);

					XSSFCellStyle separatorStyle = workbook.createCellStyle();
					separatorStyle.setFillForegroundColor(IndexedColors.RED.index);
					separatorStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

					for (DemandPlanningSapBpDTO shipto : shiptos) {
						// create row with shipto name and separator styles
						XSSFRow row = sheet.createRow(++rowNum);
						row.createCell(0).setCellValue(shipto.getName());
						// only need the separator if we have multiple shiptos
						if (shiptoSapBpId == null) {
							// the +1 offset is for the shipto header text
							// another +1 offset is for the last, total cell
							for (int i = 1; i < dateHeaderList.size() + 2; i++) {
								row.createCell(i).setCellStyle(separatorStyle);
							}
						}
						// get values
						Map<Long, Map<Date, BigDecimal>> values = dataMap.get(shipto.getSapBpId());
						if (values != null) {
							List<Pair<Long, String>> materials =
									values.keySet().stream().map(it -> new Pair<>(it, "- " + materialDescMap.get(it)))
											.sorted(Comparator.comparing(Pair::getRight)).collect(toList());
							for (Pair<Long, String> material : materials) {
								row = sheet.createRow(++rowNum);
								row.createCell(0).setCellValue(material.right);
								Map<Date, BigDecimal> materialValues = values.get(material.left);
								for (Map.Entry<Date, BigDecimal> entry : materialValues.entrySet()) {
									row.createCell(dateHeaderList.indexOf(entry.getKey()) + 1, Cell.CELL_TYPE_NUMERIC)
											.setCellValue(entry.getValue().doubleValue());
								}
								row.createCell(dateHeaderList.size() + 1, Cell.CELL_TYPE_FORMULA)
										// 'B' is the cell reference for column index 1
										.setCellFormula("SUM(B" + (rowNum + 1) + ":" + CellReference
												.convertNumToColString(dateHeaderList.size()) + (rowNum + 1) + ")");
							}
						}
					}
					for (int i = 0; i < dateHeaderList.size() + 2; i++) {
						sheet.autoSizeColumn(i);
					}

					return toFileTransfer("Forecasted volume", workbook);
				});
	}

	public ResponseDTO<List<DemandPlanDiffDTO>> getDifferenceReportData(Long soldtoSapBpId, Long shiptoSapBpId,
			String startDate, String endDate) {
		Authorization.authorize(Authorization.DEMAND_PLANNING_REPORT_PLAN_VS_REAL);

		return fetchDifferenceData(soldtoSapBpId, shiptoSapBpId, startDate, endDate, false,
				(diffs, matDescMap, startLocalDate, endLocalDate) -> {
					// we sum it up regardless of shiptos
					Map<Long, DiffCombiner> differenceMap =
							// first we do a stream and group the elements by materialId
							diffs.stream().collect(groupingBy(DemandPlanDiffEntity::getSapMaterialId, DIFF_REDUCING_COLLECTOR));

					return differenceMap.entrySet().stream().map(it -> new DemandPlanDiffDTO(matDescMap.get(it.getKey()),
							it.getValue().planned, it.getValue().ordered))
							.sorted(Comparator.comparing(DemandPlanDiffDTO::getName))
							.collect(toList());
				});
	}

	public ResponseDTO<FileTransfer> getDifferenceReportXLS(Long soldtoSapBpId, Long shiptoSapBpId, String startDate,
			String endDate) {
		Authorization.authorize(Authorization.DEMAND_PLANNING_REPORT_PLAN_VS_REAL);

		return fetchDifferenceData(soldtoSapBpId, shiptoSapBpId, startDate, endDate, true,
				(diffs, matDescMap, startLocalDate, endLocalDate) -> {

					Locale userLocale = getUserLocale();
					ResourceBundle bundle = ResourceBundleUtil.getBundle(userLocale);
					DemandPlanningService demandPlanningService = getServiceLocator().getDemandPlanningService();

					XSSFWorkbook workbook = new XSSFWorkbook();
					XSSFSheet sheet = workbook.createSheet();
					// create header, row 1
					int rowNum = -1;
					XSSFRow rowH1 = sheet.createRow(++rowNum);
					int index = 0;
					for (String mat : matDescMap.values()) {
						sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, index * 3 + 1, index * 3 + 3));
						rowH1.createCell(index * 3 + 1).setCellValue(mat);
						index++;
					}

					// create header, row 2
					String tonsText = demandPlanningService.getUoMTonsText(userLocale);
					String planTitle = bundle.getString("demandplan.expectedvsrealized.column.plan") + " " + tonsText;
					String realTitle = bundle.getString("demandplan.expectedvsrealized.column.real") + " " + tonsText;
					String deltaTitle = bundle.getString("demandplan.expectedvsrealized.column.delta");
					XSSFRow rowH2 = sheet.createRow(++rowNum);
					rowH2.createCell(0).setCellValue(bundle.getString(COLUMN_SHIPTO_KEY));
					for (index = 0; index < matDescMap.size(); index++) {
						rowH2.createCell(index * 3 + 1).setCellValue(planTitle);
						rowH2.createCell(index * 3 + 2).setCellValue(realTitle);
						rowH2.createCell(index * 3 + 3).setCellValue(deltaTitle);
					}

					// we build a map, where the first key is the shipto id, then material id, then the diff
					Map<Long, Map<Long, DiffCombiner>> values =
							diffs.stream().collect(groupingBy(DemandPlanDiffEntity::getShiptoSapBpId,
									groupingBy(DemandPlanDiffEntity::getSapMaterialId, DIFF_REDUCING_COLLECTOR)));

					// we fetch the shiptos (just the ones with planning)
					List<DemandPlanningSapBpDTO> shiptos = demandPlanningService.fetchShiptoList(
							Optional.ofNullable(shiptoSapBpId).map(Collections::singletonList)
									.orElseGet(() -> new ArrayList<>(values.keySet())), true);

					for (DemandPlanningSapBpDTO shipto : shiptos) {
						XSSFRow row = sheet.createRow(++rowNum);
						row.createCell(0).setCellValue(shipto.getName());

						Map<Long, DiffCombiner> matMap = values.get(shipto.getSapBpId());
						if (matMap != null) {
							index = 0;
							for (Long sapMatId : matDescMap.keySet()) {
								DiffCombiner diffCombiner = matMap.get(sapMatId);
								if (diffCombiner != null) {
									row.createCell(index * 3 + 1).setCellValue(diffCombiner.planned.doubleValue());
									row.createCell(index * 3 + 2).setCellValue(diffCombiner.ordered.doubleValue());
									BigDecimal delta = diffCombiner.getDelta();
									if (delta != null) {
										row.createCell(index * 3 + 3).setCellValue(delta.doubleValue());
									}
								}
								index++;
							}
						}
					}

					return toFileTransfer("Variance among planned and real", workbook);
				});
	}

	private <T> ResponseDTO<T> responseHandler(Supplier<T> supplier) {
		try {
			return ResponseDTO.success(supplier.get());
		}
		catch (LogonException e) {
			Reason reason = e.getReason();
			if (DEMAND_PLAN_REASONS.contains(reason)) {
				return ResponseDTO.error(Message.lookup(getUserLocale(), reason.getReason()), reason);
			}
			throw e;
		}
	}

	private <T> ResponseDTO<T> fetchExpectedData(Long soldtoSapBpId, Long shiptoSapBpId, String startDate,
			String endDate,
			boolean sortedMaterialMap, ListMapper<DemandPlanPlanEntity, T> mapper) {
		return responseHandler(() -> {
			DemandPlanningService demandPlanningService = getServiceLocator().getDemandPlanningService();

			LocalDate startLocalDate = toLocalDate(startDate);
			LocalDate endLocalDate = toLocalDate(endDate);

			// plannings are sorted by date ascending
			List<DemandPlanPlanEntity> plannings =
					demandPlanningService.fetchPlanningList(startLocalDate, endLocalDate, soldtoSapBpId, shiptoSapBpId);
			// only fetch the used material ids
			Set<Long> materialIds =
					plannings.stream().map(DemandPlanPlanEntity::getSapMaterialId).collect(toSet());
			Map<Long, String> materialDescriptionMap =
					toMaterialDescriptionMap(demandPlanningService, materialIds, startLocalDate, endLocalDate,
							sortedMaterialMap);

			return mapper.apply(plannings, materialDescriptionMap, startLocalDate, endLocalDate);
		});
	}

	private <T> ResponseDTO<T> fetchDifferenceData(Long soldtoSapBpId, Long shiptoSapBpId, String startDate,
			String endDate, boolean sortedMaterialMap, ListMapper<DemandPlanDiffEntity, T> mapper) {
		return responseHandler(() -> {
			DemandPlanningService demandPlanningService = getServiceLocator().getDemandPlanningService();

			LocalDate startLocalDate = toLocalDate(startDate);
			LocalDate endLocalDate = toLocalDate(endDate);

			List<DemandPlanDiffEntity> diffs =
					demandPlanningService
							.fetchDifferenceBetweenOrderedAndPlanned(startLocalDate, endLocalDate, soldtoSapBpId, shiptoSapBpId);

			Set<Long> materialIds =
					diffs.stream().map(DemandPlanDiffEntity::getSapMaterialId).collect(toSet());
			Map<Long, String> materialDescriptionMap =
					toMaterialDescriptionMap(demandPlanningService, materialIds, startLocalDate, endLocalDate,
							sortedMaterialMap);

			return mapper.apply(diffs, materialDescriptionMap, startLocalDate, endLocalDate);
		});
	}

	private static final class DiffCombiner {

		private final BigDecimal planned;

		private final BigDecimal ordered;

		private DiffCombiner(BigDecimal planned, BigDecimal ordered) {
			this.planned = planned != null ? planned : BigDecimal.ZERO;
			this.ordered = ordered != null ? ordered : BigDecimal.ZERO;
		}

		private DiffCombiner add(BigDecimal planned, BigDecimal ordered) {
			return new DiffCombiner(planned != null ? this.planned.add(planned) : this.planned, ordered != null ?
					this.ordered.add(ordered) : this.ordered);
		}

		private BigDecimal getDelta() {
			if (BigDecimal.ZERO.equals(planned)) {
				return null;
			}
			return ordered.subtract(planned).divide(planned, MC_DELTA).multiply(BigDecimal.valueOf(100), MC_DELTA);
		}
	}

	private static class Pair<T, U> {
		final T left;
		final U right;

		Pair(T left, U right) {
			this.left = left;
			this.right = right;
		}

		T getLeft() {
			return left;
		}

		U getRight() {
			return right;
		}
	}

	@FunctionalInterface
	private interface ListMapper<T, U> {

		U apply(List<T> list, Map<Long, String> matMap, LocalDate startLocalDate, LocalDate endLocalDate);
	}

}
