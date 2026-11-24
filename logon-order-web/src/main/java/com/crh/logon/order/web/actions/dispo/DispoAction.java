/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.dispo;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.business.services.interfaces.DispoService;
import com.crh.logon.business.services.interfaces.UserService;
import com.crh.logon.business.services.utils.DispoCarrierWrapper;
import com.crh.logon.business.services.utils.PreferencesKey;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.constants.Constant;
import com.crh.logon.order.web.forms.dispo.DispoForm;
import com.crh.logon.persistence.entities.admin.PreferencesEntity;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.common.PoolTxEntity;
import com.crh.logon.persistence.entities.common.PoolTxPK;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_dispo_DispoForm"
 *  path="/dispo/Dispo" scope="session"
 *  validate="true" input="DispoTile2.def"
 *
 * @struts.action-forward name="input" path="DispoTile2.def"
 * @struts.action-forward name="carriers" path="DispoCarriersTile.def"
 * @struts.action-forward name="editCarriers" path="DispoCarriersEditTile.def"
 * @struts.action-forward name="initfailed" path="/dispokarte/DispoEntry.do"
 * @struts.action-forward name="createExternalOrderPopup" path="DispoCreateExternalOrderPopup.def"
 * @struts.action-forward name="trucks" path="DispoTrucksTile.def"
 * @struts.action-forward name="editTrucks" path="DispoTrucksEditTile.def"
 * @struts.action-forward name="deliveringPlants" path="DispoDeliveringPlantsTile.def"
 * @struts.action-forward name="editDeliveringPlants" path="DispoDeliveringPlantsEditTile.def" 
 * @struts.action-forward name="orderStatuses" path="DispoOrderStatusTile.def"
 * @struts.action-forward name="editOrderStatuses" path="DispoOrderStatusEditTile.def"
 * @struts.action-forward name="display" path="DispoDisplayTile.def"
 * @struts.action-forward name="editDisplay" path="DispoDisplayEditTile.def"
 * @struts.action-forward name="showDispoExportPopup" path="ShowDispoExportPopup.def"
 */
@AdnTrace
public class DispoAction extends OrderActionSupport {

	private static final List<DispoCarrierWrapper> NO_CARRIERS_SELECTED = Collections.<DispoCarrierWrapper> emptyList();

	public static final String ACTION_SAVE_PREFERENCES = "savePreferences";

	public static final String ACTION_RESET_PREFERENCES = "resetPreferences";

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException
	{
		Authorization.authorize(Authorization.DISPATCHING_R);
		setSelectedTab("dispo", request);

		DispoForm helpForm = (DispoForm) form;
		String forwardName = "input";

		setSelectedTab("dispokarte", request);
		ServiceLocator serviceLocator = getServiceLocator();
		DispoService dispoService = serviceLocator.getDispoService();

		String formAction = helpForm.getFormAction();
		final UserService userService = getUserService();
		if (!StringUtils.empty(formAction)) {
			UserEntity user = getUser();
			if (formAction.equals(ACTION_SAVE_PREFERENCES)) {
				final List<PreferencesEntity> preferences = copyFormDataToPreferences(helpForm);
				userService.savePreferences(user, Constant.USER_PREFERENCE_SCREEN_TRUCK_SCHEDULING,
						preferences);
			}
			else if (formAction.equals(ACTION_RESET_PREFERENCES)) {
				userService.removePreferences(user,
						Constant.USER_PREFERENCE_SCREEN_TRUCK_SCHEDULING);
				helpForm.clearDefaults();
			}
			// Intentionally leaving forwardName on "input" --> DispoTile2.jsp
		}
		else if (request.getParameter("setPool") != null) {
			List<PoolTxEntity> poolTexts = helpForm.getPools();
			if (poolTexts != null) {
				int selectedPoolIndex = helpForm.getSelectedPool();
				PoolTxEntity selectedPoolText = poolTexts.get(selectedPoolIndex);
				PoolTxPK keyOfSelectedPool = selectedPoolText.getPoolTxPK();
				Long selectedPoolIdentifier = keyOfSelectedPool.getPoolId();
				List<DispoCarrierWrapper> carriersForPool = dispoService.fetchCarriersForPool(selectedPoolIdentifier);
				helpForm.setCarriers(carriersForPool);
			}
			else {
				helpForm.setCarriers(NO_CARRIERS_SELECTED);
			}
			helpForm.restoreSelectedCarriersForSelectedPool();
			forwardName = "carriers";
		}
		else if (request.getParameter("setCarrierrs") != null) {
			forwardName = "carriers";
		}
		else if (request.getParameter("editCarriers") != null) {
			forwardName = "editCarriers";
		}
		else if (request.getParameter("setTrucks") != null) {
			forwardName = "trucks";
		}
		else if (request.getParameter("editTrucks") != null) {
			forwardName = "editTrucks";
		}
		else if (request.getParameter("setDeliveringPlants") != null) {
			forwardName = "deliveringPlants";
		}
		else if (request.getParameter("editDeliveringPlants") != null) {
			forwardName = "editDeliveringPlants";
		}
		else if (request.getParameter("setOrderStatuses") != null) {
			forwardName = "orderStatuses";
		}
		else if (request.getParameter("editOrderStatuses") != null) {
			forwardName = "editOrderStatuses";
		}
		else if (request.getParameter("setDisplay") != null) {
			forwardName = "display";
		}
		else if (request.getParameter("editDisplay") != null) {
			forwardName = "editDisplay";
		}
		else if (request.getParameter("showDispoExportPopup") != null) {
			showDispoExportPopup(request, helpForm);
			forwardName = "showDispoExportPopup";
		}

		return mapping.findForward(forwardName);
	}

	/**
	 * Checks whether export of scheduling can be done, that is, whether to display the export popup.
	 * It also initializes the list of carriers to be included in the report.
	 * 
	 * @param request
	 * @param helpForm
	 */
	private static void showDispoExportPopup(HttpServletRequest request, DispoForm helpForm) {
		boolean error = false;

		// *** Check if there are any carriers selected.
		List<DispoCarrierWrapper> carriers = helpForm.getCarriers();

		if (carriers == null || carriers.isEmpty()) {
			MessageUtils.addActionWarning(request, "dispo.errors.export.noCarriersAvailable");
			error = true;
		}

		Set<Long> selectedCarriers = helpForm.getSelectedCarriers();

		// *** Check if there is at least one carrier requiring pre-scheduling.
		if (!error) {
			if (selectedCarriers.isEmpty()) {
				MessageUtils.addActionWarning(request,
						"dispo.errors.export.noPreschedulingCarriersExist");
				error = true;
			}
		}

		// *** End of error checks.
		if (error) {
			request.setAttribute("showDispoExportPopupFailed", true);
		}
	}

	/**
	 * Constructs a user preferences map from the DispoForm variables.
	 * 
	 * @param dispoForm - The DispoForm instance.
	 * 
	 * @return The user preferences in a map as key-value pairs. 
	 */
	private static List<PreferencesEntity> copyFormDataToPreferences(DispoForm dispoForm) {

		final List<PreferencesEntity> preferencesList = new ArrayList<PreferencesEntity>();

		PreferencesEntity preferencesEntity = new PreferencesEntity();
		preferencesEntity.setPreferenceKey(PreferencesKey.DISPO_POOL_INDEX.name());
		final BigDecimal selectedPool = new BigDecimal(dispoForm.getSelectedPool());
		preferencesEntity.setValNum(selectedPool);
		preferencesList.add(preferencesEntity);

		final Set<Long> selectedCarriers = dispoForm.getSelectedCarriers();
		if (selectedCarriers != null && !selectedCarriers.isEmpty()) {
			final String selectedCarriersJoined = StringUtils.join(selectedCarriers, ",");
			preferencesEntity = new PreferencesEntity();
			preferencesEntity.setPreferenceKey(PreferencesKey.DISPO_CARRIER_ID_LIST.name());
			preferencesEntity.setValStr(selectedCarriersJoined);
			preferencesList.add(preferencesEntity);
		}

		preferencesEntity = new PreferencesEntity();
		preferencesEntity.setPreferenceKey(PreferencesKey.DISPO_TIMEGRID.name());
		final BigDecimal timegrid = new BigDecimal(dispoForm.getTimegrid());
		preferencesEntity.setValNum(timegrid);
		preferencesList.add(preferencesEntity);
		preferencesEntity = new PreferencesEntity();
		preferencesEntity.setPreferenceKey(PreferencesKey.DISPO_RANGE.name());
		final BigDecimal hourSpan = new BigDecimal(dispoForm.getHourSpan());
		preferencesEntity.setValNum(hourSpan);
		preferencesList.add(preferencesEntity);

		final Set<String> selectedPlants = dispoForm.getSelectedDeliveringPlants();
		if (selectedPlants != null && !selectedPlants.isEmpty()) {
			final String selectedPlantsJoined = StringUtils.join(selectedPlants, ",");
			preferencesEntity = new PreferencesEntity();
			preferencesEntity.setPreferenceKey(PreferencesKey.DISPO_PLANTS_CD_LIST.name());
			preferencesEntity.setValStr(selectedPlantsJoined);
			preferencesList.add(preferencesEntity);
		}
		
		final Set<Long> selectedOrderStatuses = dispoForm.getSelectedOrderStatuses();
		if (selectedOrderStatuses != null && !selectedOrderStatuses.isEmpty()) {
			final String selectedOrderStatusesJoined = StringUtils.join(selectedOrderStatuses, ",");
			preferencesEntity = new PreferencesEntity();
			preferencesEntity.setPreferenceKey(PreferencesKey.DISPO_ORDER_STATUS_CD_LIST.name());
			preferencesEntity.setValStr(selectedOrderStatusesJoined);
			preferencesList.add(preferencesEntity);
		}

		preferencesEntity = new PreferencesEntity();
		preferencesEntity.setPreferenceKey(PreferencesKey.DISPO_HIDE_PLANNING_TRUCKS.name());
		final BigDecimal isHidePlanningTrucks = new BigDecimal(dispoForm.isHidePlanningTrucks() ? 1 : 0);
		preferencesEntity.setValNum(isHidePlanningTrucks);
		preferencesList.add(preferencesEntity);
		
		preferencesEntity = new PreferencesEntity();
		preferencesEntity.setPreferenceKey(PreferencesKey.DISPO_HIDE_REAL_TRUCKS.name());
		final BigDecimal isHideRealTrucks = new BigDecimal(dispoForm.isHideRealTrucks() ? 1 : 0);
		preferencesEntity.setValNum(isHideRealTrucks);
		preferencesList.add(preferencesEntity);
		
		preferencesEntity = new PreferencesEntity();
		preferencesEntity.setPreferenceKey(PreferencesKey.DISPO_HIDE_INACTIVE_TRUCKS.name());
		final BigDecimal isHideInactiveTrucks = new BigDecimal(dispoForm.isHideInactiveTrucks() ? 1 : 0);
		preferencesEntity.setValNum(isHideInactiveTrucks);
		preferencesList.add(preferencesEntity);
		
		preferencesEntity = new PreferencesEntity();
		preferencesEntity.setPreferenceKey(PreferencesKey.DISPO_HIDE_TRUCKS_WITHOUT_DELIVERIES.name());
		final BigDecimal isHideTrucksWithoutDeliveries = new BigDecimal(dispoForm.isHideTrucksWithoutDeliveries() ? 1 : 0);
		preferencesEntity.setValNum(isHideTrucksWithoutDeliveries);
		preferencesList.add(preferencesEntity);

		preferencesEntity = new PreferencesEntity();
		preferencesEntity.setPreferenceKey(PreferencesKey.DISPO_HIDE_SCHEDULED_CUSTORDERS.name());
		final BigDecimal isHideScheduledCustorders = new BigDecimal(dispoForm.isHideScheduledCustorders() ? 1 : 0);
		preferencesEntity.setValNum(isHideScheduledCustorders);
		preferencesList.add(preferencesEntity);
		
		preferencesEntity = new PreferencesEntity();
		preferencesEntity.setPreferenceKey(PreferencesKey.DISPO_HIDE_CLOSED_CUSTORDERS.name());
		final BigDecimal isHideClosedCustorders = new BigDecimal(dispoForm.isHideClosedCustorders() ? 1 : 0);
		preferencesEntity.setValNum(isHideClosedCustorders);
		preferencesList.add(preferencesEntity);

		preferencesEntity = new PreferencesEntity();
		preferencesEntity.setPreferenceKey(PreferencesKey.DISPO_HIDE_CUSTORDER_HEADERS.name());
		final BigDecimal isHideCustorderHeaders = new BigDecimal(dispoForm.isHideCustorderHeaders() ? 1 : 0);
		preferencesEntity.setValNum(isHideCustorderHeaders);
		preferencesList.add(preferencesEntity);
		
		preferencesEntity = new PreferencesEntity();
		preferencesEntity.setPreferenceKey(PreferencesKey.DISPO_VISUAL_FILTER.name());
		final BigDecimal selectedFilterForVisual = new BigDecimal(dispoForm.getSelectedFilterForVisual());
		preferencesEntity.setValNum(selectedFilterForVisual);
		preferencesList.add(preferencesEntity);
		
		preferencesEntity = new PreferencesEntity();
		preferencesEntity.setPreferenceKey(PreferencesKey.DISPO_HIDE_TRUCKS_AND_ORDER_OF_PARTNERS.name());
		final BigDecimal isHideExecutingOrdersAndTrucks = new BigDecimal(dispoForm.isHideTrucksAndOrderOfPartners() ? 1 : 0);
		preferencesEntity.setValNum(isHideExecutingOrdersAndTrucks);
		preferencesList.add(preferencesEntity);
		
		preferencesEntity = new PreferencesEntity();
		preferencesEntity.setPreferenceKey(PreferencesKey.DISPO_TRUCK_PRIORITY_FILTER.name());
		Long truckPriority = dispoForm.getTruckPriority();
		final BigDecimal truckPriorityFilter = truckPriority == null ? null : new BigDecimal(dispoForm.getTruckPriority());
		preferencesEntity.setValNum(truckPriorityFilter);
		preferencesList.add(preferencesEntity);
		
		return preferencesList;
	}

}
