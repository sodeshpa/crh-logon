/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.ExtOrderEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.SimpleVehicleEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.refdata.AssignPartStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.AssignmentPartTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.AssignmentTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.MaterialPackageBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
public class AssignmentUtils {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(AssignmentUtils.class);

	/**
	 * An assignment part maker is a object that encapsulates
	 * the different use cases of creating assignment parts
	 * (load assignment, unload assignment, ...).
	 * An instance of an assignment maker is always for a single
	 * assignment, which must be specified on construction.
	 * Internally, the assignment maker keeps a counter to
	 * automatically set the sequence number, and a calendar
	 * to update the assignment part times.
	 * <p>
	 * By design, the assignment part maker works only for new
	 * assignments. Thus, the assignment must not yet have any
	 * parts. If an assignment with existing parts is specified,
	 * an exception is thrown, to prevent from overwriting any
	 * already persisted data.
	 */
	public static class AssignmentPartMaker
	{
		/**
		 * The assignment to which this part maker adds parts.
		 */
		final private AssignmentEntity assignment;

		// Preloaded ref data constants
		final private AssignPartStatusBsEntity PART_STATUS_NEW;
		final private AssignmentPartTypeBsEntity PART_TYPE_PRELOAD;
		final private AssignmentPartTypeBsEntity PART_TYPE_LOAD;
		final private AssignmentPartTypeBsEntity PART_TYPE_TO;
		final private AssignmentPartTypeBsEntity PART_TYPE_UNLOAD;
		final private AssignmentPartTypeBsEntity PART_TYPE_FROM;
		final private AssignmentPartTypeBsEntity PART_TYPE_FINISHED;

		/**
		 * The assignment parts must be numbered in sequence.
		 * this counter contains the sequence number of the next
		 * created assignment.
		 */
		private long sequenceNo = 0;

		/**
		 * This calendar is automatically updated whenever a new
		 * part is added to the assignment. Its value is always
		 * the planned start time of the assignment part that
		 * will be created next.
		 */
		final private Calendar cal = Calendar.getInstance();

		/**
		 * The load DAO is needed because we must persist a created load
		 * before setting it in the order item.
		 */
		final private ReadWriteDao<LoadEntity> loadDao;

		/**
		 * Creates a new assignment part maker.
		 * We keep it private and instead have a
		 * static createAssignmentMaker()
		 * function in the util class, to keep the interface simple.
		 * @param assignment The the assignment for which
		 * this assignment part maker creates the assignment
		 * parts. Every created assignment part is automatically
		 * added to the parts list of this assignment.
		 * @param deliveryStart The planned start time of the
		 * first assignment part.
		 * @param referentialDataService a reference :-) to the reference data
		 * service, used to obtain reference data entities such as
		 * assignment part type and assignment part status.
		 * @param loadDao The DAO for loads is required, because
		 * the assignment part maker must create loads and set them
		 * in an existing order item, and for this the load has to be
		 * persisted, otherwise you get a hibernate error.
		 * @throws IllegalArgumentException if any of the arguments is null, or
		 * if the assignment has an existing parts list.
		 */
		private AssignmentPartMaker(
				AssignmentEntity assignment,
				Date deliveryStart,
				RefDataGenericsServiceLocalInterface referentialDataService,
				ReadWriteDao<LoadEntity> loadDao)
		throws IllegalArgumentException
		{
			if (assignment == null) {
				throw new IllegalArgumentException("assignment must not be null");
			}
			if (referentialDataService == null) {
				throw new IllegalArgumentException("refDataService must not be null");
			}
			if (assignment.getAssignmentParts() != null) {
				throw new IllegalArgumentException("An assignment was specified that already has a part list. " +
						"To prevent data loss, this is not permitted. See JavaDoc of AssignmentPartMaker.");
			}

			// Set assignment and initialize part list
			this.assignment = assignment;
			this.assignment.setAssignmentParts(new LinkedList<AssignmentPartEntity>());

			// First assignment part starts at start time of the delivery
			cal.setTime(deliveryStart);

			// Initialize ref data constants
			PART_STATUS_NEW    = referentialDataService.findBsEntity(AssignPartStatusBsEntity.class, Constant.ASSIGN_PART_STATUS_NEW);
			PART_TYPE_PRELOAD  = referentialDataService.findBsEntity(AssignmentPartTypeBsEntity.class, Constant.ASSIGNMENT_PART_TYPE_PRE_LOAD);
			PART_TYPE_LOAD     = referentialDataService.findBsEntity(AssignmentPartTypeBsEntity.class, Constant.ASSIGNMENT_PART_TYPE_LOAD);
			PART_TYPE_TO       = referentialDataService.findBsEntity(AssignmentPartTypeBsEntity.class, Constant.ASSIGNMENT_PART_TYPE_TO);
			PART_TYPE_UNLOAD   = referentialDataService.findBsEntity(AssignmentPartTypeBsEntity.class, Constant.ASSIGNMENT_PART_TYPE_UNLOAD);
			PART_TYPE_FROM     = referentialDataService.findBsEntity(AssignmentPartTypeBsEntity.class, Constant.ASSIGNMENT_PART_TYPE_FROM);
			PART_TYPE_FINISHED = referentialDataService.findBsEntity(AssignmentPartTypeBsEntity.class, Constant.ASSIGNMENT_PART_TYPE_FINISHED);

			// Store DAO
			this.loadDao = loadDao;
		}


		/**
		 * Creates a new "load" assignment part for a simple vehicle order
		 * and adds it to the assignment.
		 * @param vehicle The simple vehicle whose assignment it is
		 * @param orderItem The order item for which to create the "load" assignment.
		 * Note that the created load object is set in this order item.
		 * @param shippingpoint The shipping point where the loading is to be done
		 * @param loadNumber The sequence number of the load, this must be
		 * provided from an external sequence (see the comment of the LOAD_NUMBER
		 * column in the DB)
		 */
		public void addSimpleVehicleLoadPart(
				SimpleVehicleEntity vehicle,
				OrderItemEntity orderItem,
				ShippingpointEntity shippingpoint,
				Long loadNumber)
		{
			final int loadingTime = orderItem.getPlannedLoadingTimeMin().intValue();
			final AssignmentPartEntity part = addNewPart(PART_TYPE_LOAD, loadingTime);

			part.setHomebaseFlag(Boolean.FALSE);
			part.setShippingpoint(shippingpoint); // TODO (marius Nov 14, 2012): To support landfill, set shipto instead
			part.setLnfShiptoFlag(Boolean.FALSE); // load is always at shipping point, so LNF is irrelevant

			/*
			 * TODO (marius Nov 14, 2012):
			 * General questions for landfill:
			 * - Should LNF shipto's also be supported? In the current implementation
			 *   of createNewRoadAssignment, the shipto LNF fields are not copied
			 *   for landfill orders.
			 * - Shouldn't a "to" part come at the beginning for landfill orders, assuming
			 *   that it is a Crh truck originating at a shipping point that goes to
			 *   get the landfill material at a customer and brings it back to the
			 *   shippingpoint?
			 */

			final LoadEntity load = createSimpleVehicleLoadEntity(vehicle, orderItem, loadNumber);

			/* We must immediately persist the load, otherwise we
			 * cannot set it in the (managed) order entity.
			 */
			loadDao.persist(load);

			part.setLoad(load);
			orderItem.setLoad(load);
		}


		/**
		 * Creates a new "to" assignment part for a simple vehicle order
		 * and adds it to the assignment.
		 * @param shippingPoint The shipping point where the vehicle starts
		 * from
		 * @param transportDuration The duration of the trip from the
		 * shipping point to the shipto.
		 */
		public void addSimpleVehicleToPart(ShippingpointEntity shippingPoint, int transportDuration)
		{
			final AssignmentPartEntity part = addNewPart(PART_TYPE_TO, transportDuration);
			part.setHomebaseFlag(Boolean.FALSE);
			part.setLnfShiptoFlag(Boolean.FALSE); // "to" part starts always at shipping point, so LNF is irrelevant
			part.setShippingpoint(shippingPoint); // TODO (marius Nov 14, 2012): To support landfill, set shipto instead
		}


		/**
		 * Creates a new "unload" assignment part for a simple vehicle order
		 * and adds it to the assignment.
		 * @param order The order that must be unloaded
		 * @param shipto The shipto where the order must be unloaded
		 */
		public void addSimpleVehicleUnloadPart(OrderEntity order, ShiptoEntity shipto)
		{
			final int unloadDuration = order.getPlannedUnloadingTimeMin().intValue();
			final AssignmentPartEntity part = addNewPart(PART_TYPE_UNLOAD, unloadDuration);
			part.setOrder(order);
			part.setHomebaseFlag(Boolean.FALSE);

			/* TODO (marius Nov 14, 2012):
			 * to support landfill, set shippingpoint
			 * and shippingpoint name and coordinates instead
			 */
			part.setShipto(shipto);
			part.setLnfShiptoFlag(isLnfShipto(shipto));
			copyLnfFieldsFromOrder(part, order);
		}


		/**
		 * Creates a new "from" assignment part for a simple vehicle
		 * order and adds it to the assignment.
		 * @param order The order for which the assignment is created.
		 * It is only used to set the LNF fields in the assignment part
		 * for an LNF shipto.
		 * @param shipto The shipto where the order has been unloaded.
		 * @param transportDuration The duration of the trip from the shipto
		 * back to the shipping point.
		 */
		public void addSimpleVehicleFromPart(OrderEntity order, ShiptoEntity shipto, int transportDuration)
		{
			final AssignmentPartEntity part = addNewPart(PART_TYPE_FROM, transportDuration);
			part.setHomebaseFlag(Boolean.FALSE);

			/* TODO (marius Nov 14, 2012):
			 * to support landfill, set shippingpoint
			 * and shippingpoint name and coordinates instead
			 */
			part.setShipto(shipto);
			part.setLnfShiptoFlag(isLnfShipto(shipto));
			copyLnfFieldsFromOrder(part, order);
		}


		/**
		 * Creates a new "finished" assignment part for a simple vehicle
		 * order and adds it to the assignment.
		 * @param shippingPoint The shipping point where the vehicle turned
		 * back to
		 */
		public void addSimpleVehicleFinishedPart(ShippingpointEntity shippingPoint)
		{
			final AssignmentPartEntity part = addNewPart(PART_TYPE_FINISHED, 0); // Finished part has no duration
			part.setLnfShiptoFlag(Boolean.FALSE);

			/* Home plant is currently not supported for simple vehicles;
			 * always return to the shipping point.
			 */
			part.setShippingpoint(shippingPoint);
			part.setHomebaseFlag(Boolean.FALSE);
		}


		/**
		 * Creates a new assignment part and adds it to the assignment.
		 * The idea of this method is that you can call it for each
		 * assignment part type and then only fill the fields that are
		 * specific to each particular part type.
		 * @param type The type of the part to create
		 * @param duration The duration of the assignment part. Used to determine
		 * the planned start time of the next created assignment part.
		 * @return a new assignment part, with the following fields already
		 * preset:
		 * <ul>
		 * 	<li>assignment</li>
		 * 	<li>assignmentPartType</li>
		 * 	<li>assignmentPartStatus</li>
		 * 	<li>assignmentPartSeqNo</li>
		 * 	<li>plannedStart</li>
		 * </ul>
		 */
		private AssignmentPartEntity addNewPart(AssignmentPartTypeBsEntity type, int duration)
		{
			AssignmentPartEntity part = new AssignmentPartEntity();
			assignment.getAssignmentParts().add(part);
			part.setAssignment(assignment);
			part.setAssignmentPartType(type);
			part.setAssignPartStatus(PART_STATUS_NEW);
			part.setAssignmentPartSeqNo(sequenceNo);
			part.setPlannedStart(cal.getTime());

			// Update mutable fields
			sequenceNo++;
			cal.add(Calendar.MINUTE, duration);

			return part;
		}


		/**
		 * Creates a new load entity for a Simple Vehicle order item.
		 * Note that this method only creates the entity instance but does not persist
		 * it on the DB.
		 * @param vehicle The vehicle set in the load
		 * @param orderItem The orderItem for which this load is created. Used to
		 * determine the load UOM.
		 * @param loadNumber The load number, must be taken from the sequence SLGNC_LOAD_NUMBER.
		 * @return The created entity (not jpa-managed yet because not yet persisted!)
		 */
		private LoadEntity createSimpleVehicleLoadEntity(SimpleVehicleEntity vehicle, OrderItemEntity orderItem, Long loadNumber)
		{
			final LoadEntity load = new LoadEntity();
			load.setChamberNumber(Long.valueOf(1)); // TODO (marius Nov 14, 2012): Does this matter?
			load.setNetQuantityUom(appropriateUomForLoad(orderItem));
			load.setProductionPlant(orderItem.getProductionPlant());
			load.setSapMaterial(orderItem.getSapMaterial());
			load.setSimpleVehicle(vehicle);
			load.setAccurateQuantitiesFlag(Boolean.TRUE); // TODO (marius Nov 14, 2012): What to set this to?
			load.setWsTareQuitWarnFlag(Boolean.FALSE); // TODO (marius Nov 14, 2012): What to set this to?
			load.setLoadNumber(loadNumber);
			load.setLdsOfflineMode(Boolean.FALSE);

			return load;
		}


		/**
		 * Copies the LNF fields from an order into an assignment part.
		 * This method copies the LNF address fields (name, coordinates, country, city, etc.)
		 * from the given order to the corresponding fields of an assignment part.
		 * Use this to set the LNF fields in an assignment part of type "to" or "from".
		 * @param part The assignment part where the LNF fields are copied to
		 * @param order The order where the LNF fields are copied from
		 */
		private void copyLnfFieldsFromOrder(final AssignmentPartEntity part, OrderEntity order) {
			part.setSiteName(order.getLnfSiteName());
			part.setCoordLat(order.getLnfCoordLat());
			part.setCoordLon(order.getLnfCoordLon());
			part.setSiteCountry(order.getLnfSiteCountry());
			part.setSiteCity(order.getLnfSiteCity());
			part.setSitePostalCode(order.getLnfSitePostalCode());
			part.setSiteStreetHouseNo(order.getLnfSiteStreetHouseNo());
		}


		/**
		 * Checks if a shipto is of type "location not fixed" (LNF).
		 * @param shipto A shipto
		 * @return true if the ship to is of type LNF, false otherwise
		 * @throws IllegalArgumentException if the shipto is null
		 */
		private static boolean isLnfShipto(ShiptoEntity shipto)
		{
			if (shipto == null) {
				throw new IllegalArgumentException("shipto must not be null");
			}

			return shipto.getShiptoType().getShiptoTypeCd().equals(Constant.SHIPTO_TYPE_LOCATION_NOT_FIXED);
		}


	}

	public static class AssignmentSeqComparator implements Comparator<AssignmentPartEntity> {

		public int compare(AssignmentPartEntity o1, AssignmentPartEntity o2) {
			return o1.getAssignmentPartSeqNo().compareTo(o2.getAssignmentPartSeqNo());
		}
	}

	public static class PlannedStartTimeComparator implements Comparator<AssignmentEntity> {

		public int compare(AssignmentEntity o1, AssignmentEntity o2) {

			Date d1 = null;
			Date d2 = null;

			for (AssignmentPartEntity part : getSortedAssignmentParts(o1)) {
				if (part.getAssignmentPartType().getAssignmentPartTypeCd().equals(Constant.ASSIGNMENT_PART_TYPE_LOAD)) {
					d1 = part.getPlannedStart();
					break;
				}
			}

			for (AssignmentPartEntity part : getSortedAssignmentParts(o2)) {
				if (part.getAssignmentPartType().getAssignmentPartTypeCd().equals(Constant.ASSIGNMENT_PART_TYPE_LOAD)) {
					d2 = part.getPlannedStart();
					break;
				}
			}

			if (d1 == null || d2 == null || d1.compareTo(d2) == 0) {
				return o1.getAssignmentId().compareTo(o2.getAssignmentId());
			}

			return d1.compareTo(d2);
		}
	}

	/**
	 * Returns a new assignment maker for a given assignment
	 * @param assignmentID The ID of an assignment. This assignment must
	 * not have any existing parts, because the sequence numbers
	 * of the parts created by the assignment part maker start at zero.
	 * @param assignment The the assignment for which
	 * the returned assignment part maker creates the assignment
	 * parts. Every created assignment part is automatically
	 * added to the parts list of this assignment.
	 * @param deliveryStart The planned start time of the
	 * first assignment part.
	 * @param refDataService a reference :-) to the reference data
	 * service, used to obtain reference data entities such as
	 * assignment part type and assignment part status.
	 * @param loadDao The DAO for loads is required, because
	 * the assignment part maker must create loads and set them
	 * in an existing order item, and for this the load has to be
	 * persisted, otherwise you get a hibernate error.
	 * @return an assignment maker that creates parts for the
	 * specified assignments and whose first created part has sequence
	 * number zero.
	 * @throws IllegalArgumentException if any of the arguments is null
	 */
	public static AssignmentPartMaker createPartMaker(
			AssignmentEntity assignment,
			Date deliveryStart,
			RefDataGenericsServiceLocalInterface refDataService,
			ReadWriteDao<LoadEntity> loadDao)
	throws IllegalArgumentException
	{
		return new AssignmentPartMaker(assignment, deliveryStart, refDataService, loadDao);
	}

	public static List<OrderEntity> getOrdersFromAssignment(AssignmentEntity assignment) {

		List<OrderEntity> orders = new ArrayList<OrderEntity>();

		List<AssignmentPartEntity> assignmentParts = getSortedAssignmentParts(assignment);

		if (assignment.getAssignmentType().getAssignmentTypeCd().longValue() == Constant.ASSIGNMENT_TYPE_EXTERNAL) {
			return orders;
		}

		for (AssignmentPartEntity ape : assignmentParts) {
			if (ape.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_UNLOAD) {
				orders.add(ape.getOrder());
			}
		}

		return orders;
	}

	public static List<ExtOrderEntity> getExternalOrdersFromAssignment(AssignmentEntity assignment) {

		List<ExtOrderEntity> orders = new ArrayList<ExtOrderEntity>();

		List<AssignmentPartEntity> assignmentParts = getSortedAssignmentParts(assignment);

		if (assignment.getAssignmentType().getAssignmentTypeCd().longValue() == Constant.ASSIGNMENT_TYPE_INTERNAL) {
			return orders;
		}

		for (AssignmentPartEntity ape : assignmentParts) {
			if (ape.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_UNLOAD) {
				orders.add(ape.getExtOrder());
			}
		}

		return orders;
	}

	public static List<LoadEntity> getLoadsFromAssignment(AssignmentEntity assignment) {

		List<LoadEntity> loads = new ArrayList<LoadEntity>();

		List<AssignmentPartEntity> assignmentParts = getSortedAssignmentParts(assignment);

		for (AssignmentPartEntity ape : assignmentParts) {
			if (ape.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_LOAD) {
				loads.add(ape.getLoad());
			}
		}

		return loads;
	}

	public static List<AssignmentPartEntity> getSortedAssignmentParts(AssignmentEntity assignment) {

		return getSortedAssignmentParts(assignment, null, null);
	}

	public static List<AssignmentPartEntity> getSortedAssignmentParts(AssignmentEntity assignment,
			List<AssignmentPartEntity> loadAssignmentParts,
			List<AssignmentPartEntity> unloadAssignmentParts) {

		List<AssignmentPartEntity> assignmentParts = new ArrayList<AssignmentPartEntity>(assignment.getAssignmentParts());
		Collections.sort(assignmentParts, new AssignmentSeqComparator());

		if (loadAssignmentParts != null || unloadAssignmentParts != null) {
			for (AssignmentPartEntity ape : assignmentParts) {
				if (ape.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_LOAD
						&& loadAssignmentParts != null) {
					loadAssignmentParts.add(ape);
				}
				if (ape.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_UNLOAD
						&& unloadAssignmentParts != null) {
					unloadAssignmentParts.add(ape);
				}
			}
		}

		return assignmentParts;
	}

	public static AssignmentPartEntity getFirstLoadAssignmentPart(AssignmentEntity assignment) {
		List<AssignmentPartEntity> loadAssignmentParts = new ArrayList<AssignmentPartEntity>();
		getSortedAssignmentParts(assignment, loadAssignmentParts, null);
		if (!loadAssignmentParts.isEmpty() ) {
			AssignmentPartEntity firstLoadAssignmentPart = loadAssignmentParts.get(0);
			return firstLoadAssignmentPart;
		}
		else {
			return null;
		}
	}

	public static boolean isTour(AssignmentEntity assignment) {

		List<OrderEntity> orders = getOrdersFromAssignment(assignment);

		if (orders.size() > 1) {
			return true;
		}

		return false;
	}

	/**
	 * Decides the assignment's type
	 *
	 * @param assignment not null
	 * @return true, if it's assignmentType is ASSIGNMENT_TYPE_INTERNAL
	 * 		   false, if it's other
	 */
	public static boolean isInternal(AssignmentEntity assignment) {
		// [[
		boolean result = false;
		Object[][] params = {{"assignment", assignment}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(AssignmentUtils.class, "isInternal", params);
		}
		// ]]
		try {
			AssignmentTypeBsEntity assignmentType = assignment.getAssignmentType();
			long assignmentTypeCd = assignmentType.getAssignmentTypeCd();
			return result = assignmentTypeCd == Constant.ASSIGNMENT_TYPE_INTERNAL;
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
	 * Decides the assignment's type
	 *
	 * @param assignment not null
	 * @return true, if it's assignmentType is ASSIGNMENT_TYPE_EXTERNAL
	 * 		   false, if it's other
	 */
	public static boolean isExternal(AssignmentEntity assignment) {
		// [[
		boolean result = false;
		Object[][] params = {{"assignment", assignment}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(AssignmentUtils.class, "isExternal", params);
		}
		// ]]
		try {
			AssignmentTypeBsEntity assignmentType = assignment.getAssignmentType();
			long assignmentTypeCd = assignmentType.getAssignmentTypeCd();
			return result = assignmentTypeCd == Constant.ASSIGNMENT_TYPE_EXTERNAL;
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
	 * Returns true in case there is at least one order which is on-hold.
	 *
	 * @param assignment
	 * @return
	 */
	public static boolean isOnHold(AssignmentEntity assignment) {

		List<OrderEntity> orders = getOrdersFromAssignment(assignment);

		for (OrderEntity order : orders ) {
			if (order.getOnHoldOrderAndLockedFlag().equals(Boolean.TRUE)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns true in case the assignment is an iTrack tracked assignment.
	 * An assignment is iTrack tracked if both the sales area and also the truck allows
	 * tracking, and besides it is a bulk order.
	 * Tours are not supported, therefore enough to check the first order.
	 *
	 * @param assignment
	 * @return
	 */
	public static boolean isITrackAssignment(AssignmentEntity assignment) {

		AssignmentPartEntity unloadPart = findAssignmentPartByType(assignment, Constant.ASSIGNMENT_PART_TYPE_UNLOAD);

		if (unloadPart == null || unloadPart.getOrder() == null) {
			return false;
		}

		final SareaEntity sarea = unloadPart.getOrder().getSarea();
		final TruckEntity truck = assignment.getTruck();
		if (truck == null){
			return false;
		}

		final boolean isITrackAssignment =
				Boolean.TRUE.equals(truck.getITrackAvailableFlag()) && Boolean.TRUE.equals(sarea.getITrackAvailableFlag());

		final boolean isBulk = unloadPart.getOrder().getOrderItems()
				.stream()
				.anyMatch(orderItem -> {
					SapMaterialEntity sapMaterial = orderItem.getSapMaterial();
					MaterialPackageBsEntity materialPackage = sapMaterial.getMaterialPackage();
					String materialPackageCode = materialPackage.getMaterialPackageCd();
					return Constant.bulkOrderMaterialPackageCds.contains(materialPackageCode);
				});

		return isITrackAssignment && isBulk;
	}

	/**
	 * Input of this method is a list of {@link AssignmentPartEntity} which can have different
	 * {@link AssignmentEntity}s.
	 * The method processes the list and populate a list of {@link AssignmentEntity}s from them.
	 * @param parts
	 * @return the populated list
	 */
	public static List<AssignmentEntity> fetchAssignmentFromParts(List<AssignmentPartEntity> parts) {

		List<AssignmentEntity> assignments = new ArrayList<AssignmentEntity>();

		Map<Long, AssignmentEntity> assignmentMap = new HashMap<Long, AssignmentEntity>();

		for (AssignmentPartEntity assignmentPartEntity : parts) {
			AssignmentEntity assignment = assignmentPartEntity.getAssignment();
			if (assignmentMap.containsKey(assignment.getAssignmentId())) {
				continue;
			}
			assignmentMap.put(assignment.getAssignmentId(), assignment);
			assignments.add(assignment);
		}

		return assignments;
	}

	/**
	 * Returns the UOM to set in the load. This method returns the appropriate
	 * UOM to set in a load, depending on the order item's package type.
	 * For example, for bag orders it is the ordered UOM that must be set
	 * in the load, while for bulk orders, it is the base UOM that must be set.
	 * @param orderItem The order item for which the UOM is to be set in the load.
	 * @return The appropriate UOM
	 */
	public static UomBsEntity appropriateUomForLoad(OrderItemEntity orderItem) {
		UomBsEntity oiUom = null;

		if (LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(orderItem)){
			oiUom = orderItem.getSapMaterial().getBaseUom();
		}
		else {
			oiUom = orderItem.getOrderedUom();
		}

		return oiUom;
	}

	public static MaterialPackageBsEntity getMaterialPackage(AssignmentEntity assignment) {

		final MaterialPackageBsEntity mp;
		final List<LoadEntity> loads = getLoadsFromAssignment(assignment);
		if (loads != null && !loads.isEmpty()) {
			mp = loads.get(0).getSapMaterial().getMaterialPackage();
		}
		else {
			throw new LogonException(
					String.format("Cannot determine material package for assignment (assignment_id=%s)",
							assignment.getAssignmentId()),
					Reason.RES_ERROR_IMPL);
		}

		return mp;
	}

	/**
	 * Iterates over the {@link AssignmentPartEntity}s of the given {@link AssignmentEntity} and
	 * returns the <b>first</b> that has the type code equal to 'assignmentPartType'
	 * @return - the found assignment part or null if none found
	 */
	public static AssignmentPartEntity findAssignmentPartByType(AssignmentEntity assignment,
			long assignmentPartType) {
		// [[
		AssignmentPartEntity result = null;
		Object[][] params = {{"assignment", assignment},
							{"assignmentPartTypeFinished", assignmentPartType + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(AssignmentUtils.class, "findAssignmentPartsByType", params);
		}
		// ]]
		try {
			List<AssignmentPartEntity> assignmentParts = assignment.getAssignmentParts();
			for (AssignmentPartEntity assignmentPartEntity : assignmentParts) {
				if (assignmentPartEntity.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == assignmentPartType) {
					return assignmentPartEntity;
				}
			}
			return result = null;
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
	 * Collect assignmentParts where {@link OrderEntity} field exists.
	 */
	public static List<AssignmentPartEntity> collectOrderParts(List<AssignmentPartEntity> assignmentParts) {
		// [[
		List<AssignmentPartEntity> result = null;
		Object[][] params = {{"assignmentParts", assignmentParts}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(AssignmentUtils.class, "collectOrderParts", params);
		}
		// ]]
		try {

			result = new ArrayList<AssignmentPartEntity>();

			for (AssignmentPartEntity assignmentPartEntity : assignmentParts) {
				OrderEntity order = assignmentPartEntity.getOrder();
				if (order != null) {
					result.add(assignmentPartEntity);
				}
			}

			return result;
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
	 * It returns true if the parameter's type is ASSIGNMENT_PART_TYPE_FINISHED
	 * @param assignmentPart
	 * @return boolean
	 */
	public static boolean isFinishedPart(AssignmentPartEntity assignmentPart)
	{
		// [[
		boolean result = false;
		Object[][] params = {{"assignmentPart", assignmentPart}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(AssignmentUtils.class, "isFinishedPart", params);
		}
		// ]]
		try {
			long assignmentPartTypeCd = getAssignmentPartTypeCd(assignmentPart);
			return result = assignmentPartTypeCd == Constant.ASSIGNMENT_PART_TYPE_FINISHED;
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
	 * It returns true if the parameter's type is ASSIGNMENT_PART_TYPE_FROM
	 * @param assignmentPart
	 * @return boolean
	 */
	public static boolean isDrivingFromPart(AssignmentPartEntity assignmentPart)
	{
		// [[
		boolean result = false;
		Object[][] params = {{"assignmentPart", assignmentPart}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(AssignmentUtils.class, "isDrivingFromPart", params);
		}
		// ]]
		try {
			long assignmentPartTypeCd = getAssignmentPartTypeCd(assignmentPart);
			return result = assignmentPartTypeCd == Constant.ASSIGNMENT_PART_TYPE_FROM;
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
	 * It returns true if the parameter's type is ASSIGNMENT_PART_TYPE_TO
	 * @param assignmentPart
	 * @return boolean
	 */
	public static boolean isDrivingToPart(AssignmentPartEntity assignmentPart)
	{
		// [[
		boolean result = false;
		Object[][] params = {{"assignmentPart", assignmentPart}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(AssignmentUtils.class, "isDrivingToPart", params);
		}
		// ]]
		try {
			long assignmentPartTypeCd = getAssignmentPartTypeCd(assignmentPart);
			return result = assignmentPartTypeCd == Constant.ASSIGNMENT_PART_TYPE_TO;
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
	 * It returns true if the parameter's type is ASSIGNMENT_PART_TYPE_UNLOAD
	 * @param assignmentPart
	 * @return boolean
	 */
	public static boolean isUnloadPart(AssignmentPartEntity assignmentPart)
	{
		// [[
		boolean result = false;
		Object[][] params = {{"assignmentPart", assignmentPart}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(AssignmentUtils.class, "isUnloadPart", params);
		}
		// ]]
		try {
			long assignmentPartTypeCd = getAssignmentPartTypeCd(assignmentPart);
			return result = assignmentPartTypeCd == Constant.ASSIGNMENT_PART_TYPE_UNLOAD;
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
	 * It returns true if the parameter's type is ASSIGNMENT_PART_TYPE_LOAD
	 * @param assignmentPart
	 * @return boolean
	 */
	public static boolean isLoadPart(AssignmentPartEntity assignmentPart)
	{
		// [[
		boolean result = false;
		Object[][] params = {{"assignmentPart", assignmentPart}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(AssignmentUtils.class, "isLoadPart", params);
		}
		// ]]
		try {
			long assignmentPartTypeCd = getAssignmentPartTypeCd(assignmentPart);
			return result = assignmentPartTypeCd == Constant.ASSIGNMENT_PART_TYPE_LOAD;
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
	 * It returns true if the parameter's type is ASSIGNMENT_PART_TYPE_PRE_LOAD
	 * @param assignmentPart
	 * @return boolean
	 */
	public static boolean isPreLoadPart(AssignmentPartEntity assignmentPart)
	{
		// [[
		boolean result = false;
		Object[][] params = {{"assignmentPart", assignmentPart}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(AssignmentUtils.class, "isPreLoadPart", params);
		}
		// ]]
		try {
			long assignmentPartTypeCd = getAssignmentPartTypeCd(assignmentPart);
			return result = assignmentPartTypeCd == Constant.ASSIGNMENT_PART_TYPE_PRE_LOAD;
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
	 * Creates a new Truck assignment with the given parameters.
	 *
	 * For Assignments, the assignment timestamp is always stored in the timezone of the application. Since
	 * our DB DATE fields are not timezone aware and assignments can be created by any number of users in
	 * different timezones, we need to choose one as the reference timezone.
	 *
	 * There are two main motivations for this change are as follows, The current code base has always
	 * created assignments with using only the date portion of the given <em>date</em> That is, the
	 * hour, minute, and seconds are cleared before setting the assignment date. During lookup, a simple
	 * ASSIGNMENT_DAT = date expression is used. This no longer works with multiple timezones since when
	 * searching for an asssignment, we would need to know in which timezone the timestamp was created in.
	 * To get around this issue, we just choose the DB timezone as our reference.
	 *
	 * Note, we could have gotten around this problem by using a RANGE operation when searching
	 * for assignments. I am just not sure what performance impact this could have and given the current
	 * performance problems, we tried to keep the current implementation unchanged.
	 */
	public static AssignmentEntity createTruckAssignment(
			TruckEntity truck,
			AssignmentTypeBsEntity type,
			Long number,
			Date date, TimeZone timeZone) {

		// convert the given date to the DB timezone
		Date startTime = timeZone.shiftToTimeZone(date, TimeZone.getDBTimeZone());
		startTime = TimeZone.getDBTimeZone().getDayStart(startTime);

		AssignmentEntity assignment = new AssignmentEntity();
		assignment.setManuallyScheduled(Boolean.TRUE);
		assignment.setTruck(truck);
		assignment.setActiveFlag(Boolean.TRUE);
		assignment.setAssignmentType(type);
		assignment.setAssignmentDat(startTime);
		assignment.setAssignmentNumber(number);

		return assignment;
	}

	/**
	 * It gives back the assignemPartTypeCd of the given assignmentPartType
	 * @param assignmentPart
	 * @return long
	 */
	private static long getAssignmentPartTypeCd(
			AssignmentPartEntity assignmentPart) {
		// [[
		long result = 0L;
		Object[][] params = {{"assignmentPart", assignmentPart}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(AssignmentUtils.class, "getAssignmentPartTypeCd", params);
		}
		// ]]
		try {
			AssignmentPartTypeBsEntity assignmentPartType = assignmentPart.getAssignmentPartType();
			long assignmentPartTypeCd = assignmentPartType.getAssignmentPartTypeCd();
			return result = assignmentPartTypeCd;
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
