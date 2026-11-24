/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import static com.crh.logon.business.services.utils.DistanceValidationInfoBean.POPUP_LITDICT_KEY_ACTIVE;
import static com.crh.logon.business.services.utils.DistanceValidationInfoBean.POPUP_LITDICT_KEY_INACTIVE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.utils.validation.ValidationErrorReason;
import com.crh.logon.constants.CustorderError;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.views.SareaDisplayEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.time.DateTimeUtils;


@AdnTrace
public class DeliveryDistanceValidationUtils {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DeliveryDistanceValidationUtils.class);

	private static List <CustorderError> sanityErrors = new ArrayList<CustorderError>();
	static {
		sanityErrors.add(CustorderError.LEADTIME_DISTANCE_SANITY_ALL_FIELDS_ARE_MANDATORY);
		sanityErrors.add(CustorderError.LEADTIME_DISTANCE_SANITY_L1_LESS_THAN_L2);
		sanityErrors.add(CustorderError.LEADTIME_DISTANCE_SANITY_DX_VALUES_INCREMENTAL);
		sanityErrors.add(CustorderError.LEADTIME_DISTANCE_SANITY_ONLY_ONE);
	}

	/**
	 * Validate validation setting only based on sarea data.
	 *
	 * @param sareaDisplayEntity
	 * @param refersToNewEntity
	 * @return
	 */
	public List<ValidationErrorReason> sanityCheckSalesAreEdit(SareaDisplayEntity sareaDisplayEntity, boolean refersToNewEntity) {
		LeadTimeValidationBaseBean leadTimeValidationData = new LeadTimeValidationBaseBean(sareaDisplayEntity);

		List<ValidationErrorReason> validationErrorReasons = new LinkedList<ValidationErrorReason>();

		// skip sanity check in when all fields are empty
		if(leadTimeValidationData.isBaseDataEmpty()) {
			return validationErrorReasons;
		}

		List<CustorderError> custorderErrors = sanityCheck(leadTimeValidationData);

		processValidationErrorReasons(refersToNewEntity, validationErrorReasons, custorderErrors);

		return validationErrorReasons;
	}

	/**
	 * Validate validation setting based on shiptoSareaEntity data.
	 * If no shiptoSareaEntity value given than consider sareaEntity value.
	 *
	 * @param shiptoSareaEntity
	 * @param refersToNewEntity
	 * @param sareaEntity
	 * @return
	 */
	public List<ValidationErrorReason> sanityCheckShipToEdit(ShiptoSareaEntity shiptoSareaEntity, boolean refersToNewEntity, SareaEntity
			sareaEntity) {
		LeadTimeValidationBaseBean leadTimeValidationData = new LeadTimeValidationBaseBean(shiptoSareaEntity, sareaEntity);
		List<ValidationErrorReason> validationErrorReasons = new LinkedList<ValidationErrorReason>();

		List<CustorderError> custorderErrors = sanityCheck(leadTimeValidationData);

		processValidationErrorReasons(refersToNewEntity, validationErrorReasons, custorderErrors);

		return validationErrorReasons;
	}

	private void processValidationErrorReasons(boolean refersToNewEntity, List<ValidationErrorReason> validationErrorReasons,
			List<CustorderError> custorderErrors) {
		if (custorderErrors != null && custorderErrors.size() > 0) {
			for (CustorderError error : custorderErrors) {
				validationErrorReasons.add(new ValidationErrorReason(
						error, refersToNewEntity,
						null, null));
			}
		}
	}

	public DistanceValidationInfoBean distanceBaseLeadTimeCheckCreatePopUpText(ShiptoSareaEntity shiptoSareaEntity,
			SareaEntity sareaEntity,
			CustorderEntity custorderEntity, Long distanceMeters, Locale locale) {
		LeadTimeValidationBaseBean leadTimeValidationData = new LeadTimeValidationBaseBean(shiptoSareaEntity, sareaEntity);
		DistanceValidationInfoBean distanceValidationInfoBean = new DistanceValidationInfoBean();

		if (null == distanceMeters) {
			distanceMeters = custorderEntity!=null?custorderEntity.getTransportDistanceM():null;
		}
		populateCommonInfoBeanFields(leadTimeValidationData, distanceMeters, distanceValidationInfoBean);
		// stop processing when validation is turned off
		if (!Boolean.TRUE.equals(leadTimeValidationData.getDbltActiveFlag())) {
			return distanceValidationInfoBean;
		}

		Date processingTime = new Date();
		Date deliveryFromDat = custorderEntity!=null?custorderEntity.getDeliveryFromDat():null;
		boolean refersToNewEntity = custorderEntity!=null ?  custorderEntity.getCustorderId() == null : true;
		distanceValidationInfoBean =
				distanceBaseLeadTimeCheck(leadTimeValidationData, processingTime, deliveryFromDat, distanceMeters,
						refersToNewEntity, distanceValidationInfoBean);

		return distanceValidationInfoBean;
	}

	public DistanceValidationInfoBean distanceBaseLeadTimeCheckOrderTaking(LeadTimeValidationBaseBean leadTimeValidationData,
			OrderEntity order) {
		DistanceValidationInfoBean distanceValidationInfoBean = new DistanceValidationInfoBean();

		Date processingTime = new Date();
		Date deliveryFromDat = order.getDeliveryFromDat();

		Long distance = order.getTransportDistanceM();
		populateCommonInfoBeanFields(leadTimeValidationData, distance, distanceValidationInfoBean);
		// stop processing when validation is turned off
		if (!Boolean.TRUE.equals(leadTimeValidationData.getDbltActiveFlag())) {
			return distanceValidationInfoBean;
		}

		boolean refersToNewEntity = order.getOrderId() == null;
		List<ValidationErrorReason> validationErrorReasons = new LinkedList<ValidationErrorReason>();
		List<CustorderError> custorderErrors = distanceBaseLeadTimeCheck(leadTimeValidationData, processingTime,
				deliveryFromDat, distance, distanceValidationInfoBean);
		boolean sanityErrorExist = false;
		if (custorderErrors != null && custorderErrors.size() > 0) {
			for (Iterator<CustorderError> iterator = custorderErrors.iterator(); iterator.hasNext();) {
				CustorderError error = iterator.next();
				String additionalInfo = null;
				if (sanityErrors.contains(error)) {
					LOG.error("Wrong sanity rule set! "+ leadTimeValidationData.getIdsForTrack());
					iterator.remove();
					sanityErrorExist = true;
					continue;
				}

				if (CustorderError.LEADTIME_DISTANCE_ERROR.equals(error)) {
					additionalInfo = getDistanceInKilometerString(distance);
				}
				validationErrorReasons.add(new ValidationErrorReason(
						error, refersToNewEntity,
						Boolean.TRUE, additionalInfo));
			}
			// substitute here all of sanity errors with one general error
			if (sanityErrorExist) {
				validationErrorReasons.add(new ValidationErrorReason(
						CustorderError.LEADTIME_DISTANCE_SANITY_GENERAL, refersToNewEntity,
						Boolean.TRUE, null));
			}
		}


		distanceValidationInfoBean.addDistanceBasedErrorReasons(validationErrorReasons);

		return distanceValidationInfoBean;
	}

	private DistanceValidationInfoBean distanceBaseLeadTimeCheck(LeadTimeValidationBaseBean leadTimeValidationData,
			Date processingTime,
			Date deliveryFromDat,
			Long distance,
			boolean refersToNewEntity,
			DistanceValidationInfoBean distanceValidationInfoBean) {
		List<ValidationErrorReason> validationErrorReasons = new LinkedList<ValidationErrorReason>();
		List<CustorderError> custorderErrors = distanceBaseLeadTimeCheck(leadTimeValidationData, processingTime,
				deliveryFromDat, distance, distanceValidationInfoBean);
		if (custorderErrors != null && custorderErrors.size() > 0) {
			for (CustorderError error : custorderErrors) {
				String additionalInfo = null;
				if (CustorderError.LEADTIME_DISTANCE_ERROR.equals(error)) {
					additionalInfo = getDistanceInKilometerString(distance);
				}
				validationErrorReasons.add(new ValidationErrorReason(
						error, refersToNewEntity,
						Boolean.TRUE, additionalInfo));
			}
		}
		distanceValidationInfoBean.addDistanceBasedErrorReasons(validationErrorReasons);

		return distanceValidationInfoBean;
	}


	protected List<CustorderError> sanityCheck(LeadTimeValidationBaseBean leadTimeValidationData) {

		List<CustorderError> result = new ArrayList<CustorderError>();

		if (leadTimeValidationData == null) {
			return null;
		}

		Boolean dbltActiveFlag = leadTimeValidationData.getDbltActiveFlag();
		// if the flag is false we do not do sanity check
		if (Boolean.FALSE.equals(dbltActiveFlag) ) {
			return null;
		}

		Boolean sdltAndNdltActive = leadTimeValidationData.getSdltAndNdltActive();
		if(Boolean.TRUE.equals(dbltActiveFlag) && Boolean.TRUE.equals(sdltAndNdltActive)) {
			result.add(CustorderError.LEADTIME_DISTANCE_SANITY_ONLY_ONE);
			return result;
		}

		Long dbltDistanceL1 = leadTimeValidationData.getDbltDistanceL1();
		Long dbltDistanceL2 = leadTimeValidationData.getDbltDistanceL2();
		Long dbltOffsetD1 = leadTimeValidationData.getDbltOffsetD1();
		Long dbltOffsetD2 = leadTimeValidationData.getDbltOffsetD2();
		Long dbltOffsetD3 = leadTimeValidationData.getDbltOffsetD3();
		Long dbltOffsetD4 = leadTimeValidationData.getDbltOffsetD4();
		Long dbltOffsetD5 = leadTimeValidationData.getDbltOffsetD5();
		Long dbltOffsetD6 = leadTimeValidationData.getDbltOffsetD6();
		Long h1EarlyOrderLimit = leadTimeValidationData.getH1EarlyOrderLimit();
		Long h2EarliestDeliveryFrom = leadTimeValidationData.getH2EarliestDeliveryFrom();

		// all the configuration fields have to be filled
		if (dbltDistanceL1 == null || dbltDistanceL2 == null ||
				dbltOffsetD1 == null || dbltOffsetD2 == null
				|| dbltOffsetD3 == null || dbltOffsetD4 == null
				|| dbltOffsetD5 == null || dbltOffsetD6 == null
				|| h1EarlyOrderLimit == null || h2EarliestDeliveryFrom == null){
			result.add(CustorderError.LEADTIME_DISTANCE_SANITY_ALL_FIELDS_ARE_MANDATORY);
		}

		// L1 =< L2
		if (dbltDistanceL1 != null && dbltDistanceL2 != null && dbltDistanceL1 > dbltDistanceL2) {
			result.add(CustorderError.LEADTIME_DISTANCE_SANITY_L1_LESS_THAN_L2);
		}

		// the offset values have to be incremental by pairs
		checkOffsetsAreIncremental(result, dbltOffsetD1, dbltOffsetD2, dbltOffsetD3, dbltOffsetD4, dbltOffsetD5, dbltOffsetD6);

		return result;
	}

	protected List<CustorderError> distanceBaseLeadTimeCheck(LeadTimeValidationBaseBean leadTimeValidationData,
			Date processingTime,
			Date deliveryFromDat,
			Long distance, DistanceValidationInfoBean distanceValidationInfoBean) {

		// stop here if the dataset do not pass the basic checks
		List<CustorderError> sanityCheckErrors = sanityCheck(leadTimeValidationData);
		if (sanityCheckErrors != null && !sanityCheckErrors.isEmpty()) {
			return sanityCheckErrors;
		}

		List<CustorderError> result = new ArrayList<CustorderError>();


		/*
			D <= L1
		*/
			CustorderError error = distanceUnderL1(leadTimeValidationData, processingTime, deliveryFromDat,
					distanceValidationInfoBean, distance);
			if (error != null) {
				result.add(error);
			}

		/*
			L1 < D <= L2
		*/
			error = distanceBetweenL1AndL2(leadTimeValidationData, processingTime, deliveryFromDat,
					distanceValidationInfoBean, distance);
			if (error != null) {
				result.add(error);
			}


		/*
			L2 < D
		*/
			error = distanceOverL2(leadTimeValidationData, processingTime, deliveryFromDat,
					distanceValidationInfoBean, distance);
			if (error != null) {
				result.add(error);
			}

		return result;
	}

	private void populateCommonInfoBeanFields(LeadTimeValidationBaseBean leadTimeValidationData, Long distance,
			DistanceValidationInfoBean distanceValidationInfoBean) {

		distanceValidationInfoBean.setDbltActiveFlag(Boolean.TRUE.equals(leadTimeValidationData.getDbltActiveFlag()));
		String distanceInKilometerString = getDistanceInKilometerString(distance);
		distanceValidationInfoBean.setDistance(distanceInKilometerString);
		String distanceInKilometerStringL1 = getDistanceInKilometerString(leadTimeValidationData.getDbltDistanceL1());
		distanceInKilometerStringL1 = StringUtils.empty(distanceInKilometerStringL1) ? "L1" : distanceInKilometerStringL1;
		distanceValidationInfoBean.setL1(distanceInKilometerStringL1);
		String distanceInKilometerStringL2 = getDistanceInKilometerString(leadTimeValidationData.getDbltDistanceL2());
		distanceInKilometerStringL2 = StringUtils.empty(distanceInKilometerStringL2) ? "L2" : distanceInKilometerStringL2;
		distanceValidationInfoBean.setL2(distanceInKilometerStringL2);
		String timeStringH1 = getTimeStringFromLong(leadTimeValidationData.getH1EarlyOrderLimit());
		timeStringH1 = StringUtils.empty(timeStringH1) ? "H1" : timeStringH1;
		distanceValidationInfoBean.setH1(timeStringH1);
		String timeStringH2 = getTimeStringFromLong(leadTimeValidationData.getH2EarliestDeliveryFrom());
		timeStringH2 = StringUtils.empty(timeStringH2) ? "H2" : timeStringH2;
		distanceValidationInfoBean.setH2(timeStringH2);
	}

	private String getDistanceInKilometerString(Long distanceInMeter) {
		if (distanceInMeter == null) {
			return "";
		}
		return String.valueOf(distanceInMeter.doubleValue() * 0.001);
	}

	protected String getTimeStringFromLong (Long timePartInMinutes) {
		if (timePartInMinutes == null) {
			return "";
		}
		Calendar hoursMinutesCal = DateTimeUtils.getHoursMinutesCal(timePartInMinutes);
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		return format.format(hoursMinutesCal.getTime());
	}

	private String getShiftedDateString(Date date, Long shift) {
		if (shift == null) {
			shift = new Long(0);
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, shift.intValue());
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(cal.getTime());
	}


	protected CustorderError distanceUnderL1(LeadTimeValidationBaseBean leadTimeValidationData, final Date processingTime,
			final Date
					deliveryFromDat, DistanceValidationInfoBean distanceValidationInfoBean, Long distance) {

		Long dbltOffsetD1 = leadTimeValidationData.getDbltOffsetD1();
		Long dbltOffsetD2 = leadTimeValidationData.getDbltOffsetD2();

		DistanceValidationInfoRow distanceUnderL1 = new DistanceValidationInfoRow();
		distanceValidationInfoBean.setDistanceUnderL1(distanceUnderL1);

		boolean realCase = false;
		Long dbltDistanceL1 = leadTimeValidationData.getDbltDistanceL1();
		if (distance != null && dbltDistanceL1 != null && distance <= dbltDistanceL1 ) {
			realCase = true;
		}



		return calculateEarliestPossibleDelivery(leadTimeValidationData, processingTime, deliveryFromDat, dbltOffsetD1,
				dbltOffsetD2, realCase, distanceUnderL1, distanceValidationInfoBean);

	}

	private CustorderError distanceBetweenL1AndL2(LeadTimeValidationBaseBean leadTimeValidationData, Date processingTime,
			Date deliveryFromDat, DistanceValidationInfoBean distanceValidationInfoBean, Long distance) {

		Long dbltOffsetD3 = leadTimeValidationData.getDbltOffsetD3();
		Long dbltOffsetD4 = leadTimeValidationData.getDbltOffsetD4();

		DistanceValidationInfoRow distanceBetweenL1AndL2 = new DistanceValidationInfoRow();
		distanceValidationInfoBean.setDistanceBetweenL1AndL2(distanceBetweenL1AndL2);

		boolean realCase = false;
		Long dbltDistanceL1 = leadTimeValidationData.getDbltDistanceL1();
		Long dbltDistanceL2 = leadTimeValidationData.getDbltDistanceL2();
		if (distance != null && dbltDistanceL1 != null && dbltDistanceL2 != null &&
				(dbltDistanceL1 < distance && distance <= dbltDistanceL2)) {
			realCase = true;
		}


		return calculateEarliestPossibleDelivery(leadTimeValidationData, processingTime, deliveryFromDat, dbltOffsetD3,
				dbltOffsetD4, realCase, distanceBetweenL1AndL2, distanceValidationInfoBean);
	}

	private CustorderError distanceOverL2(LeadTimeValidationBaseBean leadTimeValidationData, Date processingTime,
			Date deliveryFromDat, DistanceValidationInfoBean distanceValidationInfoBean, Long distance) {

		Long dbltOffsetD5 = leadTimeValidationData.getDbltOffsetD5();
		Long dbltOffsetD6 = leadTimeValidationData.getDbltOffsetD6();

		DistanceValidationInfoRow distanceOverL2 = new DistanceValidationInfoRow();
		distanceValidationInfoBean.setDistanceOverL2(distanceOverL2);

		boolean realCase = false;
		Long dbltDistanceL2 = leadTimeValidationData.getDbltDistanceL2();
		if (distance != null && dbltDistanceL2 != null && dbltDistanceL2 < distance) {
			realCase = true;
		}



			return calculateEarliestPossibleDelivery(leadTimeValidationData, processingTime, deliveryFromDat, dbltOffsetD5,
				dbltOffsetD6, realCase, distanceOverL2, distanceValidationInfoBean);
	}

	/**
	 *
	 * @param leadTimeValidationData basic dataset to process
	 * @param processingTime    current time
	 * @param deliveryFromDat
	 * @param offsetBeforeH1    offset to shift in days if current timepoint is before H1
	 * @param offsetAfterH1        offset to shift in days if current timepoint is after H1
	 * @param realCase            if true do validation, if false only fetch the datas for the distanceValidationInfoRow
	 * @param distanceValidationInfoRow        holds value for the infoBean
	 * @param distanceValidationInfoBean
	 * @return
	 */
	protected CustorderError calculateEarliestPossibleDelivery(
			LeadTimeValidationBaseBean leadTimeValidationData,
			Date processingTime,
			Date deliveryFromDat,
			Long offsetBeforeH1,
			Long offsetAfterH1, boolean realCase,
			DistanceValidationInfoRow distanceValidationInfoRow,
			DistanceValidationInfoBean distanceValidationInfoBean) {

		/*
			fetch values for calculation
		 */
		Long h1EarlyOrderLimit = leadTimeValidationData.getH1EarlyOrderLimit();
		Long h2EarliestDeliveryFrom = leadTimeValidationData.getH2EarliestDeliveryFrom();
		Calendar processingTimeCalendar = Calendar.getInstance();
		processingTimeCalendar.setTime(processingTime);

		Calendar earliestPossibleDeliveryTimeBeforeH1 = Calendar.getInstance();
		earliestPossibleDeliveryTimeBeforeH1.setTime(processingTime);

		Calendar earliestPossibleDeliveryTimeAfterH1 = Calendar.getInstance();
		earliestPossibleDeliveryTimeAfterH1.setTime(processingTime);

		long processingTimeInMinutes = DateTimeUtils.getTimePartInMinutes(processingTimeCalendar);
		int offsetWeekendDaysConsideredBeforeH1 = 0;
		int offsetWeekendDaysConsideredAfterH1 = 0;

		/*
			Calculate boundaries for the error message row and the validation, depending on timepoint is before/after H1
		 */
		offsetWeekendDaysConsideredBeforeH1 = addWeekendDaysToOffset(
				processingTimeCalendar,
				offsetBeforeH1.intValue(),
				SalesAreaWebOrderOnWeekendSettings.valueOf(leadTimeValidationData.getAllowWebOrderOnWeekend()));
		earliestPossibleDeliveryTimeBeforeH1.add(Calendar.DATE, offsetWeekendDaysConsideredBeforeH1);
		earliestPossibleDeliveryTimeBeforeH1 = DateTimeUtils.setTimePartInMinutes(earliestPossibleDeliveryTimeBeforeH1, h2EarliestDeliveryFrom.longValue());

		offsetWeekendDaysConsideredAfterH1 = addWeekendDaysToOffset(
				processingTimeCalendar,
				offsetAfterH1.intValue(),
				SalesAreaWebOrderOnWeekendSettings.valueOf(leadTimeValidationData.getAllowWebOrderOnWeekend()));
		earliestPossibleDeliveryTimeAfterH1.add(Calendar.DATE, offsetWeekendDaysConsideredAfterH1);
		earliestPossibleDeliveryTimeAfterH1 = DateTimeUtils.setTimePartInMinutes(earliestPossibleDeliveryTimeAfterH1, h2EarliestDeliveryFrom.longValue());

		distanceValidationInfoRow.setBeforeH1(DateTimeUtils.formatDayMonthYear(earliestPossibleDeliveryTimeBeforeH1.getTime(),
				Locale.ENGLISH));
		distanceValidationInfoRow.setAfterH1(DateTimeUtils.formatDayMonthYear(earliestPossibleDeliveryTimeAfterH1.getTime(),
				Locale.ENGLISH));

		Calendar deliveryFromCal = null;
		if (deliveryFromDat != null){
			deliveryFromCal = Calendar.getInstance();
			deliveryFromCal.setTime(deliveryFromDat);
		}

		/*
			Validate only if we are in the exact situation (realCase = true)
		 */
		if (realCase) {

			Calendar earliestPossibleDeliveryFromDat = null;
			if (processingTimeInMinutes <= h1EarlyOrderLimit.longValue()) {
				earliestPossibleDeliveryFromDat = earliestPossibleDeliveryTimeBeforeH1;
			}
			else {
				earliestPossibleDeliveryFromDat = earliestPossibleDeliveryTimeAfterH1;
			}

			distanceValidationInfoBean.setEarliestPossibleDeliveryFromDate(earliestPossibleDeliveryFromDat.getTime());

			if (deliveryFromCal != null && deliveryFromCal.before(earliestPossibleDeliveryFromDat)) {
				return CustorderError.LEADTIME_DISTANCE_ERROR;
			}
		}
		return null;
	}

	public int addWeekendDaysToOffset(Calendar processingTimeCalendar, int offset, SalesAreaWebOrderOnWeekendSettings weekendSetting) {

		if (SalesAreaWebOrderOnWeekendSettings.SATURDAY_AND_SUNDAY.equals(weekendSetting)) {
				return offset;
		}

		int numberOfDaysToShift = 0;
		Calendar processingTimeCalendarClone = (Calendar) processingTimeCalendar.clone();

		if (SalesAreaWebOrderOnWeekendSettings.SATURDAY.equals(weekendSetting)) {
			int upperbound = offset;
			// observe wether first day is Sunday
			if(Calendar.SUNDAY == processingTimeCalendarClone.get(Calendar.DAY_OF_WEEK)) {
				upperbound++;
			}

			for (int i = 1; i <= upperbound; i++ ) {
				processingTimeCalendarClone.add(Calendar.DATE, 1);
				numberOfDaysToShift++; //increase the counter at every day
				if(Calendar.SUNDAY == processingTimeCalendarClone.get(Calendar.DAY_OF_WEEK)) {
					upperbound++;
				}
			}

			return numberOfDaysToShift;
		}


		if (SalesAreaWebOrderOnWeekendSettings.DISABLED.equals(weekendSetting)) {
			int upperbound = offset;
			// observe wether first day is Sunday
			if(Calendar.SUNDAY == processingTimeCalendarClone.get(Calendar.DAY_OF_WEEK)) {
				upperbound++;
			}
			if(Calendar.SATURDAY == processingTimeCalendarClone.get(Calendar.DAY_OF_WEEK)) {
				upperbound+=2;
			}

			for (int i = 1; i <= upperbound; i++ ) {
				processingTimeCalendarClone.add(Calendar.DATE, 1);
				numberOfDaysToShift++; //increase the counter at every day
				if(Calendar.SATURDAY == processingTimeCalendarClone.get(Calendar.DAY_OF_WEEK)) {
					upperbound+=2;
				}

			}
			return numberOfDaysToShift;
		}
		return numberOfDaysToShift;
	}

	public static String getDistanceValidationPopUpText(DistanceValidationInfoBean distanceValidationInfoBean, Locale locale){

		if (distanceValidationInfoBean != null && distanceValidationInfoBean.isDbltActiveFlag()
				&& !distanceValidationInfoBean.isRowDataEmpty()) {
			String[] arguments = {
					distanceValidationInfoBean.getL1(),
					distanceValidationInfoBean.getH1(), distanceValidationInfoBean.getDistanceUnderL1().getBeforeH1(),
					distanceValidationInfoBean.getH2(),
					distanceValidationInfoBean.getH1(), distanceValidationInfoBean.getDistanceUnderL1().getAfterH1(),
					distanceValidationInfoBean.getH2(),

					distanceValidationInfoBean.getL1(), distanceValidationInfoBean.getL2(),
					distanceValidationInfoBean.getH1(), distanceValidationInfoBean.getDistanceBetweenL1AndL2().getBeforeH1(),
					distanceValidationInfoBean.getH2(),
					distanceValidationInfoBean.getH1(), distanceValidationInfoBean.getDistanceBetweenL1AndL2().getAfterH1(),
					distanceValidationInfoBean.getH2(),

					distanceValidationInfoBean.getL2(),
					distanceValidationInfoBean.getH1(), distanceValidationInfoBean.getDistanceOverL2().getBeforeH1(),
					distanceValidationInfoBean.getH2(),
					distanceValidationInfoBean.getH1(), distanceValidationInfoBean.getDistanceOverL2().getAfterH1(),
					distanceValidationInfoBean.getH2(),
			};
			return Message.lookup(locale, POPUP_LITDICT_KEY_ACTIVE, arguments);
		}
		else {
			return Message.lookup(locale, POPUP_LITDICT_KEY_INACTIVE);
		}
	}


	protected void checkOffsetsAreIncremental(List<CustorderError> result, Long dbltOffsetD1, Long dbltOffsetD2, Long
			dbltOffsetD3,
			Long dbltOffsetD4, Long dbltOffsetD5, Long dbltOffsetD6) {

		if ((dbltOffsetD1 != null && dbltOffsetD2 != null && dbltOffsetD1 > dbltOffsetD2 )||
				(dbltOffsetD3 != null && dbltOffsetD4 != null && dbltOffsetD3 > dbltOffsetD4)	||
				(dbltOffsetD5 != null && dbltOffsetD6 != null && dbltOffsetD5  > dbltOffsetD6)) {
			result.add(CustorderError.LEADTIME_DISTANCE_SANITY_DX_VALUES_INCREMENTAL);
		}
	}
}