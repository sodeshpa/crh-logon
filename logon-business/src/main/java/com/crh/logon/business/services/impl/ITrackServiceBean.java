/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;


import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.itrack.ITrackService;
import com.crh.logon.business.itrack.dto.PositionDTO;
import com.crh.logon.business.itrack.model.OdometerReadings;
import com.crh.logon.business.itrack.model.Position;
import com.crh.logon.business.itrack.model.Target;
import com.crh.logon.business.itrack.model.TollAmount;
import com.crh.logon.business.itrack.model.Unload;
import com.crh.logon.business.itrack.model.Unloads;
import com.crh.logon.business.services.dispatchers.ITrackServiceDispatcher;
import com.crh.logon.business.services.impl.cron.ScheduledTask;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.interfaces.ConfigService;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.EmailServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.FaxAndSmsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.ITrackServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.PDFGeneratorServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.utils.AssignmentUtils;
import com.crh.logon.business.services.utils.LangUtil;
import com.crh.logon.business.services.utils.LogonStatusUtils;
import com.crh.logon.constants.ITrackStatus;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.refdata.ItrackStatusBsEntity;
import com.crh.logon.persistence.entities.views.UnloadsForTargetEntity;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import org.apache.commons.lang.StringUtils;

@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@AdnTrace
@Alternative
@Singleton
@Interceptors(UserInfoInterceptor.class)
public class ITrackServiceBean extends ITrackServiceDispatcher implements ITrackServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ITrackServiceBean.class);

	private static final UnaryOperator<String> PLATE_NUMBER_MAPPER = plateNo -> plateNo.toUpperCase(Locale.ENGLISH)
			.replaceAll("[-_ ]", "");

	@EJB
	private DispoServiceLocalInterface dispoService;

	@EJB
	private OrderServiceLocalInterface orderService;

	@EJB
	private ITrackServiceLocalInterface iTrackService;

	@Inject
	private ITrackService iTrackClientService;

	@EJB
	private PDFGeneratorServiceLocalInterface pdfGeneratorService;

	@EJB
	private EmailServiceLocalInterface emailService;

	@EJB
	FaxAndSmsServiceLocalInterface faxAndSmsService;

	@EJB
	private RefDataGenericsServiceLocalInterface referentialDataService;

	@Resource
	private TimerService timerService;

	@EJB
	private ConfigService configService;

	private final ConcurrentHashMap<String, Integer> plateToTargetIds = new ConcurrentHashMap<>();

	private Timer itrackRefreshTimer = null;

	private static final Map<ITrackStatus, String> ITRACK_STATUS_EMAIL_MESSAGES;

	private static final Set<ITrackStatus> ITRACK_STATUS_FAILED_CALLS;

	static {
		Map<ITrackStatus, String> itrackStatusEmailMessages = new HashMap<>();
		itrackStatusEmailMessages.put(ITrackStatus.DELIVERED_PARAMETER_MISSING, "Toll fee or Odometer is missing");
		itrackStatusEmailMessages.put(ITrackStatus.SHIPPING_UNLOAD_INFORMATION_MISSING, "I-Data call to get Unload "
				+ "information is failed");
		ITRACK_STATUS_EMAIL_MESSAGES = Collections.unmodifiableMap(itrackStatusEmailMessages);

		Set<ITrackStatus> itractStatusFailedCalls = new HashSet<>();
		itractStatusFailedCalls.add(ITrackStatus.DELIVERED_UNLOAD_INFORMATION_MISSING);
		itractStatusFailedCalls.add(ITrackStatus.DELIVERED_PARAMETER_MISSING);
		itractStatusFailedCalls.add(ITrackStatus.SHIPPING_UNLOAD_INFORMATION_MISSING);
		ITRACK_STATUS_FAILED_CALLS = Collections.unmodifiableSet(itractStatusFailedCalls);
	}

	@PostConstruct
	protected void init() {
		Timer oldITrackRefreshTimer = itrackRefreshTimer;
		// every 60 minute
		itrackRefreshTimer = timerService.createIntervalTimer(0, 60L * 60 * 1000, new TimerConfig("", false));
		if (oldITrackRefreshTimer != null) {
			oldITrackRefreshTimer.cancel();
		}
	}

	@Timeout
	@TransactionAttribute(TransactionAttributeType.NEVER)
	protected void refresh(Timer timer) {
		List<Target> allTargets;
		try {
			allTargets = iTrackClientService.getAllTargets();
		}
		catch (IllegalStateException e) {
			LOG.warn("Disabling iTrack polling", e);
			timer.cancel();
			return;
		}
		Map<String, Integer> newPlateToTargetIds = Optional.ofNullable(allTargets).map(List::stream)
				.map(stream -> stream
						.filter(it -> it.getId() != null && it.getName() != null)
						.collect(Collectors.toMap(
								it -> PLATE_NUMBER_MAPPER.apply(it.getName()), Target::getId, (v1, v2) -> {
									LOG.warn("iTrack targetIds map to the same plate: " + v1 + ", " + v2);
									return v1;
								}))).orElse(null);
		if (newPlateToTargetIds != null) {
			// set the new target
			plateToTargetIds.putAll(newPlateToTargetIds);
			// remove what we don't need
			plateToTargetIds.keySet().removeIf(plate -> !newPlateToTargetIds.containsKey(plate));
		}
	}

	public PositionDTO getLastPosition(String truckPlateNo) {
		Integer targetId = plateToTargetIds.get(PLATE_NUMBER_MAPPER.apply(truckPlateNo));
		if (targetId == null) {
			throw new LogonException("Truck can not be found with plate number: " + truckPlateNo, Reason.RES_ERROR_IMPL);
		}

		return Optional.ofNullable(iTrackClientService.getLastPositions(Collections.singletonList(targetId)))
				.map(Collection::stream)
				.flatMap(Stream::findFirst)
				.map(this::converterPosition)
				.orElse(null);

	}

	private PositionDTO converterPosition(Position position) {
		PositionDTO positionDTO = new PositionDTO();
		positionDTO.setLatitude(position.getLatitude());
		positionDTO.setLongitude(position.getLongitude());
		return positionDTO;

	}

	@ScheduledTask(name = "unloads_for_target", second = "5", minute = "1/5",
			configurationMethodName = "getUnloadsForTargetCronEnabled")
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void getUnloadsForTargetRequest() {
		ReadOnlyDao<UnloadsForTargetEntity> unloadsForTargetEntityDao = getDaoProvider().getUnloadsForTargetEntityDao();
		Filter<UnloadsForTargetEntity> uftFilter = unloadsForTargetEntityDao.createFilter();
		List<UnloadsForTargetEntity> unloadsForTarget = unloadsForTargetEntityDao.find(uftFilter);
		LOG.debug("Processing " + unloadsForTarget.size() + " UnloadsForTargetEntity instances");
		if (plateToTargetIds.isEmpty() && !unloadsForTarget.isEmpty()) {
			LOG.error("Failed to process UnloadsForTarget. Truck plate number - targetId mapping is not initialized.");
			return;
		}
		unloadsForTarget
				.forEach(uft -> {
					Integer targetId = plateToTargetIds.get(PLATE_NUMBER_MAPPER.apply(uft.getTruckPlateNo()));
					try {
						iTrackService.processUnloadsForTarget(uft, targetId);
					}
					catch (Exception e) {
						LOG.error("Failed to process UnloadsForTarget. " + toLog(uft), e);
					}
				});

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void processUnloadsForTarget(UnloadsForTargetEntity uft, Integer targetId) {
		LOG.debug("Processing with targetId " + targetId + ". " + toLog(uft));
		if (targetId == null) {
			LOG.warn("Truck can not be found. " + toLog(uft));
		}
		UnloadCallResult unloadCallResult = null;
		OrderEntity order = getDaoProvider().getOrderEntityDao().findById(uft.getOrderId());
		ITrackStatus oldItrackStatus = ITrackStatus.valueOf(uft.getItrackStatusCd());
		ITrackStatus newItrackStatus = oldItrackStatus;

		if (oldItrackStatus == null || oldItrackStatus == ITrackStatus.SHIPPING_UNLOAD_INFORMATION_MISSING
				|| oldItrackStatus == ITrackStatus.DELIVERED_UNLOAD_INFORMATION_MISSING) {
			// try to unload the truck
			unloadCallResult = tryUnload(order, uft, targetId);
			UnloadOutcome unloadOutcome = unloadCallResult.unloadOutcome;
			LOG.debug("Unload outcome " + unloadOutcome + " " + toLog(uft));

			switch (unloadOutcome) {
				case SKIPPED:
					// there is nothing to do, return
					return;
				case NO_UNLOAD:
					// delivered_unload_info_missing doesn't have a state that aligns with null
					newItrackStatus = oldItrackStatus == ITrackStatus.DELIVERED_UNLOAD_INFORMATION_MISSING
							? ITrackStatus.DELIVERED_UNLOAD_INFORMATION_MISSING : null;
					break;
				case RIGHT_LOCATION:
					newItrackStatus = ITrackStatus.DELIVERED_LOCATION_APPROVED;
					break;
				case WRONG_LOCATION:
				case TRUCK_ALREADY_RELOADED:
				case TIME_FRAME_EXCEEDED:
				case UNLOAD_TOO_FAST:
					newItrackStatus = ITrackStatus.DELIVERED_WRONG_LOCATION;
					break;
				case ERROR:
					newItrackStatus = oldItrackStatus == ITrackStatus.DELIVERED_UNLOAD_INFORMATION_MISSING
							? ITrackStatus.DELIVERED_UNLOAD_INFORMATION_MISSING
							: ITrackStatus.SHIPPING_UNLOAD_INFORMATION_MISSING;
					break;
				default:
					throw new IllegalArgumentException("Unknown UnloadOutcome " + unloadOutcome);
			}
		}

		if (newItrackStatus == ITrackStatus.DELIVERED_LOCATION_APPROVED
				|| newItrackStatus == ITrackStatus.DELIVERED_PARAMETER_MISSING) {
			AssignmentPartEntity assignmentPart = orderService.getAssignmentPartUnload(order);

			// no information about odo/toll, try to fetch them
			boolean success = tryTollFeeAndOdoMeter(uft, order, uft.getLoadingTs(), assignmentPart.getRealStart(), targetId);
			newItrackStatus = success ? ITrackStatus.OK : ITrackStatus.DELIVERED_PARAMETER_MISSING;
		}

		if (newItrackStatus == ITrackStatus.DELIVERED_WRONG_LOCATION) {
			// send SMS if it was delivered at the wrong location
			smsSending(uft, order);
		}

		if ((newItrackStatus != ITrackStatus.OK && newItrackStatus != oldItrackStatus) ||
				(newItrackStatus == ITrackStatus.OK && ITRACK_STATUS_FAILED_CALLS.contains(oldItrackStatus))) {
			// send email if it wasn't okay OR if it became okay from an iTrack-related issue
			emailSending(uft, order, newItrackStatus, unloadCallResult);
		}

		if (newItrackStatus != oldItrackStatus) {
			ItrackStatusBsEntity itrackStatusBsEntity = newItrackStatus != null ?
					referentialDataService.findBsEntity(ItrackStatusBsEntity.class, newItrackStatus.getStatusCode()) : null;
			order.setItrackStatus(itrackStatusBsEntity);
		}
	}

	/**
	 * Tries to unload the order/truck.
	 */
	private UnloadCallResult tryUnload(OrderEntity order, UnloadsForTargetEntity uft, Integer targetId) {
		UnloadCallResult result;
		Instant now = Instant.now();
		//iTrack returns error if we call with current time, therefore always ask for the results now - 5 minutes
		OffsetDateTime to = OffsetDateTime.ofInstant(now, ZoneOffset.UTC).minusMinutes(5);
		Date unloadTime = null;
		Date startTime = null;

		// fetch the next load after this order for the truck - if any
		AssignmentPartEntity unloadPart = orderService.getAssignmentPartUnload(order);
		ReadWriteDao<LoadEntity> loadEntityDao = getDaoProvider().getLoadEntityDao();
		Filter<LoadEntity> filter = loadEntityDao.createFilter();
		filter.addExpression(Restrictions.eq(LoadEntity.ATTR_TRUCK, unloadPart.getAssignment().getTruck()));
		filter.addExpression(Restrictions.gt(LoadEntity.ATTR_LOADING_TS, uft.getLoadingTs()));
		Optional<LoadEntity> nextLoad = loadEntityDao.findNRecs(0,1,filter).stream().findFirst();

		if (nextLoad.isPresent()) {
			//if there is a new load this might be normal if the order was created later, than it was loaded see CRHLOGON-1450
			LOG.info("The truck " + uft.getTruckPlateNo() + " was already reloaded. Therefore asking for unloads "
					+ "between " + uft.getLoadingTs() + " and " + nextLoad.get().getLoadingTs());
			to = OffsetDateTime.ofInstant(nextLoad.get().getLoadingTs().toInstant(), ZoneOffset.UTC);
		}

		if (targetId != null) {
			// we have a targetId, let's try to call unload
			UnloadCallResult unloadCallResult = callUnload(order, uft, targetId, to);
			LOG.debug("tryUnload " + unloadCallResult + " " + toLog(uft));
			result = unloadCallResult;
			unloadTime = unloadCallResult.unloadTime;
			startTime = unloadCallResult.startTime;
		} else {
			result = new UnloadCallResult(UnloadOutcome.ERROR, null, null, null);
		}

		// we didn't have an unload, check timebox
		if (result.unloadOutcome != UnloadOutcome.RIGHT_LOCATION && result.unloadOutcome != UnloadOutcome.WRONG_LOCATION) {
			// add 12 hours to the delivery to date
			Instant timeboxUpperLimit = order.getDeliveryToDat().toInstant().plus(12, ChronoUnit.HOURS);
			if (now.isAfter(timeboxUpperLimit)) {
				// we waited long enough and no unloads, mark it as incorrect location
				LOG.info("Order reached the unload timebox's upper limit. " + toLog(uft));
				result = new UnloadCallResult(UnloadOutcome.TIME_FRAME_EXCEEDED, null, null, null);
				unloadTime = Date.from(now);
			}
		}

		AssignmentPartEntity[] assignmentParts = null;

		// we still didn't have an unload, check truck loading
		if (result.unloadOutcome != UnloadOutcome.RIGHT_LOCATION && result.unloadOutcome != UnloadOutcome.WRONG_LOCATION && result.unloadOutcome != UnloadOutcome.TIME_FRAME_EXCEEDED) {
			// fetch the assignment
			assignmentParts = getUnloadAndReturnAssignmentPart(order);
			AssignmentEntity assignment = assignmentParts[0].getAssignment();

			// fetch the number of loads after this order for the truck
			filter = loadEntityDao.createFilter();
			filter.addExpression(Restrictions.eq(LoadEntity.ATTR_TRUCK, assignment.getTruck()));
			filter.addExpression(Restrictions.gt(LoadEntity.ATTR_LOADING_TS, uft.getLoadingTs()));

			long count = loadEntityDao.count(filter);

			if (count > 0) {
				// there is a newer load
				LOG.info("Truck was loaded with a new order, marking the old order as wrong location. " + toLog(uft));
				result = new UnloadCallResult(UnloadOutcome.TRUCK_ALREADY_RELOADED, null, null, null);
				unloadTime = Date.from(now);
			}
		}

		if (unloadTime != null && startTime != null) {
			ConfigBean configBean = configService.getConfigBean();
			int config = configBean.getItrackMinimumUnloadingtime();

			Instant unloadLimit = startTime.toInstant().plus(config, ChronoUnit.MINUTES);
			// if unloading happened under the specified time limit, order should be set to delivered-erroneous
			if (unloadTime.toInstant().isBefore(unloadLimit)) {
				result = new UnloadCallResult(UnloadOutcome.UNLOAD_TOO_FAST, null, null, null);
			}
		}

		// move the order to the correct state
		if (result.unloadOutcome == UnloadOutcome.RIGHT_LOCATION || result.unloadOutcome == UnloadOutcome.WRONG_LOCATION || result.unloadOutcome
				== UnloadOutcome.TRUCK_ALREADY_RELOADED || result.unloadOutcome == UnloadOutcome.TIME_FRAME_EXCEEDED ||
				result.unloadOutcome == UnloadOutcome.UNLOAD_TOO_FAST) {
			if (unloadTime == null) {
				throw new IllegalStateException("An unload happened, but the unloadTime was not set!");
			}
			if (assignmentParts == null) {
				assignmentParts = getUnloadAndReturnAssignmentPart(order);
			}
			AssignmentEntity assignment = assignmentParts[0].getAssignment();

			// calculate returnTime
			Date returnTime = unloadTime;
			if (assignmentParts[1] != null) {
				// there is a return part, calculate the time difference between planned unload and planned finish
				long difference = assignmentParts[2].getPlannedStart().getTime() - assignmentParts[1].getPlannedStart().getTime();
				// add that difference to the unloadTime
				returnTime = Date.from(unloadTime.toInstant().plus(difference, ChronoUnit.MILLIS));
			}

			if (LogonStatusUtils.isBeforeDelivered(order.getOrderStatus())) {
				// if we are before delivered, we have to unload the order

				if (startTime != null) {
					BizeventEntity bizevent = new BizeventEntity();

					AssignmentPartEntity part = assignmentParts[0];
					if (part != null) {
						bizevent.setAssignment(part.getAssignment());
						bizevent.setOrder(part.getOrder());
						bizevent.setShipto(part.getShipto());
						bizevent.setEventTs(startTime);

						orderService.startUnload(bizevent);
					}
				}

				dispoService.setTruckUnloaded(assignmentParts[0], unloadTime);

				dispoService.setReturned(assignment.getAssignmentId(), returnTime);
			}
			else {
				// it's already in the correct status, so we just have to update the dates
				assignmentParts[0].setRealStart(unloadTime);
				if (assignmentParts[1] != null) {
					assignmentParts[1].setRealStart(unloadTime);
				}
				assignmentParts[2].setRealStart(returnTime);

				Date finalUnloadTime = unloadTime;
				// update the load item's unload date
				order.getOrderItems().stream().map(OrderItemEntity::getLoad).filter(Objects::nonNull)
						.forEach(it -> it.setUnloadingTs(finalUnloadTime));

				dispoService.saveAssignment(assignment);
			}
		}

		return result;
	}

	private UnloadCallResult callUnload(OrderEntity order, UnloadsForTargetEntity uft, Integer targetId, OffsetDateTime to) {
		// fetch the unloads for the target
		OffsetDateTime from = uft.getLoadingTs().toInstant().atOffset(ZoneOffset.UTC);
		if (from.isAfter(to)) {
			LOG.debug("Skipping the ITrackClientService.getUnloadsForTarget. " + toLog(uft));
			return new UnloadCallResult(UnloadOutcome.SKIPPED, null, null, null);
		}
		List<Unload> unloads = getUnloadsForTarget(uft, targetId, from, to);
		Date loadTime = uft.getLoadingTs();

		if (unloads == null) {
			return new UnloadCallResult(UnloadOutcome.ERROR, null, null, null);
		}
		else if (unloads.isEmpty()) {
			// there were no unloads in timeslot

			return new UnloadCallResult(UnloadOutcome.NO_UNLOAD, null, null, null);
		} else {
			// there was at least one unload
			Unload unload = unloads.get(0);
			Date unloadTime;
			Date startTime;

			if (unload.getUnloadTime() != null) {
				unloadTime = Date.from(unload.getUnloadTime().toInstant());
				if (loadTime.compareTo(unloadTime) > 0) {
					LOG.warn("Unload time is before load time. Probably a mock issue. " + toLog(uft));
				}
			}
			else {
				LOG.warn("Unload without time information. " + toLog(uft));
				unloadTime = Date.from(to.toInstant());
			}

			if (unload.getStartTime() != null) {
				startTime = Date.from(unload.getStartTime().toInstant());
				if (loadTime.compareTo(startTime) > 0) {
					LOG.warn("Unload start time is before load time. Probably a mock issue. " + toLog(uft));
				}
			}
			else {
				LOG.warn("Unload without start time information. " + toLog(uft));
				startTime = Date.from(to.toInstant());
			}

			boolean correctLocation = false;
			if (unload.getShipToId() != null && !unload.getShipToId().isEmpty()) {
				List<String> iTrackShiptoIds = Arrays.asList(unload.getShipToId().split("\\s*,\\s*"));
				correctLocation = iTrackShiptoIds.contains(order.getShiptoSapBp().getSapId());
			}

			// if none of the shipToIds match, we mark it as wrong location
			if (!correctLocation) {
				LOG.info("The shipto id received does not match the Logon shiptoId " + toLog(uft) + " Received: " + unload.getShipToId());
			}
			return new UnloadCallResult( correctLocation ? UnloadOutcome.RIGHT_LOCATION : UnloadOutcome.WRONG_LOCATION,
					unloadTime, startTime, unload.getShipToId());
		}
	}

	private static void assertPartNotSet(AssignmentPartEntity part) {
		if (part != null) {
			throw new IllegalStateException("There were multiple parts with type " + part.getAssignmentPartType().getAssignmentPartTypeCd());
		}
	}

	private AssignmentPartEntity[] getUnloadAndReturnAssignmentPart(OrderEntity order) {
		// fetch the unload part, this essentially just fetches an assignment part, where the orderId is the same
		AssignmentPartEntity unloadPart = orderService.getAssignmentPartUnload(order);

		AssignmentEntity assignment = unloadPart.getAssignment();
		List<AssignmentPartEntity> assignmentParts = assignment.getAssignmentParts();

		// return is optional
		AssignmentPartEntity returnPart = null;
		// finish is mandatory
		AssignmentPartEntity finishPart = null;

		for (AssignmentPartEntity part : assignmentParts) {
			if (AssignmentUtils.isUnloadPart(part) && !part.equals(unloadPart)) {
				throw new IllegalStateException("There were multiple unload parts");
			}
			else if (AssignmentUtils.isDrivingFromPart(part)) {
				assertPartNotSet(returnPart);
				returnPart = part;
			}
			else if (AssignmentUtils.isFinishedPart(part)) {
				// shouldn't really be possible
				assertPartNotSet(finishPart);
				finishPart = part;
			}
		}
		return new AssignmentPartEntity[] {unloadPart, returnPart, finishPart};
	}

	/**
	 * Tries to fetch toll fee and odo meter information.
	 *
	 * @param order       The order to update
	 * @param loadingTs   The loading time
	 * @param unloadingTs The unloading time
	 * @param targetId    The truck's targetId in ITrack.
	 * @return returns true if save is successful, otherwise false
	 */
	private boolean tryTollFeeAndOdoMeter(UnloadsForTargetEntity uft, OrderEntity order, Date loadingTs, Date unloadingTs,
			Integer targetId) {
		if (targetId != null) {
			OffsetDateTime loadTime = loadingTs.toInstant().atOffset(ZoneOffset.UTC);
			OffsetDateTime unloadTime = unloadingTs.toInstant().atOffset(ZoneOffset.UTC);

			TollAmount tollAmount = iTrackClientService.getTollAmountForTarget(targetId, loadTime, unloadTime);
			LOG.debug("TollFee response " + tollAmount + " " + toLog(uft));

			if (tollAmount != null) {
				order.setTollFee(tollAmount.getTollAmountNet());
			}

			OdometerReadings odometerReadings = iTrackClientService.getOdometerReadings(targetId, loadTime, unloadTime);
			LOG.debug("Odometer response " + odometerReadings + " " + toLog(uft));

			if (odometerReadings != null && odometerReadings.getFromKm() != null && odometerReadings.getToKm() != null) {
				order.setItrackDistanceKm(odometerReadings.getToKm() - odometerReadings.getFromKm());
			}

			if (order.getTollFee() != null && order.getItrackDistanceKm() != null) {
				return true;
			}
		}

		// check the timebox, add 12 hours to the delivery to date
		Instant timeboxUpperLimit = order.getDeliveryToDat().toInstant().plus(12, ChronoUnit.HOURS);
		if (Instant.now().isAfter(timeboxUpperLimit)) {
			// we waited long enough and not all info was gathered, mark it as completed
			LOG.info("Order reached the tollFee/odoMeter timebox's upper limit. " + toLog(uft));
			return true;
		}

		return false;
	}

	private List<Unload> getUnloadsForTarget(UnloadsForTargetEntity uft, Integer targetId, OffsetDateTime from,
			OffsetDateTime to) {
		Unloads unloadsForTarget = iTrackClientService.getUnloadsForTarget(targetId, from, to);
		if (unloadsForTarget == null) {
			LOG.warn("ITrackClientService.getUnloadsForTarget failed. " + toLog(uft));
			return null;
		}
		else {
			List<Unload> unloads = unloadsForTarget.getValues();
			if (unloads == null || unloads.isEmpty()) {
				LOG.debug("Truck with provided from time has no finished unload entry. " + toLog(uft));
				return Collections.emptyList();
			}
			else if (unloads.size() > 1) {
				LOG.warn("Truck with provided from time has more than one finished unload entry. " + toLog(uft));
			}
			return unloads;
		}
	}

	private void emailSending(UnloadsForTargetEntity uft, OrderEntity order, ITrackStatus iTrackStatus, UnloadCallResult unloadCallResult) {

		Locale locale = LangUtil.getOrderLocale(order);
		String emailSubject;
		String emailText;
		if (ITrackStatus.DELIVERED_WRONG_LOCATION == iTrackStatus) {
			if (unloadCallResult == null){
				LOG.error("Status will be set to delivered wrong location, whereas it is not known from what reason. Email "
						+ "sending will be skipped.");
				return;
			}
			final UnloadOutcome unloadOutcome = unloadCallResult.unloadOutcome;
			if (unloadOutcome == UnloadOutcome.TIME_FRAME_EXCEEDED) {
				emailSubject = pdfGeneratorService.formatText(
						"unloads.for.target.deadline.email.subject", locale, new Object [] { uft.getTruckPlateNo()});
				emailText = pdfGeneratorService.formatText("unloads.for.target.deadline.email.text",
						locale, new Object[] { uft.getTruckPlateNo(), order.getOrderNumber().toString()});
			}
			else if (unloadOutcome == UnloadOutcome.TRUCK_ALREADY_RELOADED) {
				emailSubject = pdfGeneratorService.formatText(
						"unloads.for.target.reloaded.email.subject", locale, new Object [] { uft.getTruckPlateNo()});
				emailText = pdfGeneratorService.formatText("unloads.for.target.reloaded.email.text",
						locale, new Object[] { uft.getTruckPlateNo(), order.getOrderNumber().toString()});
			}
			else if (unloadOutcome == UnloadOutcome.UNLOAD_TOO_FAST) {
				emailSubject = pdfGeneratorService.formatText(
						"unloads.for.target.short.unload.email.subject", locale, new Object [] { uft.getTruckPlateNo()});
				emailText = pdfGeneratorService.formatText("unloads.for.target.short.unload.email.text",
						locale, new Object[] { uft.getTruckPlateNo(), order.getOrderNumber().toString()});
			}
			else {
				emailSubject = pdfGeneratorService.formatText(
						"unloading.different.location.email.subject", locale, new Object [] { uft.getTruckPlateNo()});
				emailText = pdfGeneratorService.formatText("unloading.different.location.email.text",
						locale, new Object[] { uft.getTruckPlateNo(), order.getOrderNumber().toString(),
								unloadCallResult.shiptoId});
			}
		}
		else if (ITrackStatus.OK == iTrackStatus) {
			emailSubject = pdfGeneratorService.formatText(
					"unloads.for.target.ok.email.subject", locale, new Object [] { uft.getTruckPlateNo()});
			emailText = pdfGeneratorService.formatText("unloads.for.target.ok.email.text",
					locale, new Object[] { uft.getTruckPlateNo(), order.getOrderNumber().toString() });
		}
		else if (iTrackStatus == null){
			emailSubject = pdfGeneratorService.formatText(
					"unloads.for.target.conn.back.email.subject", locale, new Object [] { uft.getTruckPlateNo()});
			emailText = pdfGeneratorService.formatText("unloads.for.target.conn.back.email.text",
					locale, new Object[] { uft.getTruckPlateNo(), order.getOrderNumber().toString() });
		}
		else {
			emailSubject = pdfGeneratorService.formatText(
					"unloads.for.target.error.email.subject", locale, new Object[]{uft.getTruckPlateNo()});
			emailText = pdfGeneratorService.formatText("unloads.for.target.error.email.text",
					locale, new Object[] { ITRACK_STATUS_EMAIL_MESSAGES.get(iTrackStatus), uft.getTruckPlateNo(),
							order.getOrderNumber().toString() });
		}
		final String[] to = StringUtils.split(getConfigBean().getMailUnloadingDifferentLocationTo(), ',');
		emailService.send(to, null, null, emailSubject, emailText, null);
	}

	private void smsSending(UnloadsForTargetEntity uft, OrderEntity order) {
		if (StringUtils.isEmpty(getConfigBean().getSmsUnloadingDifferentLocation())) {
			LOG.error("No sms number specified for Unloads For Target Scheduled Job");
			return;
		}
		Locale locale = LangUtil.getOrderLocale(order);
		String message = pdfGeneratorService.formatText("unloading.different.location.sms.text", locale,
				new Object[] { uft.getTruckPlateNo(), order.getOrderNumber().toString() });
		faxAndSmsService.callSendSms(getConfigBean().getSmsUnloadingDifferentLocation(), message);
	}

	private static String toLog(UnloadsForTargetEntity uft) {
		return "" + uft.getOrderId() + ";" + uft.getTruckPlateNo() + ";" + uft.getLoadingTs();
	}

	private enum UnloadOutcome {
		SKIPPED, NO_UNLOAD, ERROR, WRONG_LOCATION, RIGHT_LOCATION, TRUCK_ALREADY_RELOADED, TIME_FRAME_EXCEEDED, UNLOAD_TOO_FAST
	}

	private static class UnloadCallResult {
		private final UnloadOutcome unloadOutcome;
		private final Date unloadTime;
		private final Date startTime;
		private final String shiptoId;

		private UnloadCallResult(UnloadOutcome unloadOutcome, Date unloadTime, Date startTime, String shiptoId) {
			this.unloadOutcome = unloadOutcome;
			this.unloadTime = unloadTime;
			this.startTime = startTime;
			this.shiptoId = shiptoId;
		}

		@Override
		public String toString() {
			return "UnloadCallResult{" +
					"unloadOutcome=" + unloadOutcome +
					", unloadTime=" + unloadTime +
					", startTime=" + startTime +
					", shiptoId='" + shiptoId + '\'' +
					'}';
		}
	}
}
