/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.raildispo;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.forms.raildispo.DispoForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.RequestUtils;

import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.business.services.interfaces.DispoService;
import com.crh.logon.business.services.interfaces.UserService;
import com.crh.logon.business.services.utils.PreferencesKey;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.admin.PreferencesEntity;
import com.crh.logon.persistence.entities.views.DispoPlantTrackListEntity;
import com.crh.logon.persistence.entities.views.RailPlantSpEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_raildispo_DispoForm"
 *  path="/raildispo/Dispo" scope="session"
 *  validate="true" input="RailDispoTile.def"
 *
 * @struts.action-forward name="input" path="RailDispoTile.def"
 * @struts.action-forward name="shippingpointsAndTracks" path="RailDispoShippingpointsTracksTile.def"
 * @struts.action-forward name="editShippingpoints" path="RailDispoShippingpointsEditTile.def"
 * @struts.action-forward name="editTracks" path="RailDispoTracksEditTile.def"
 * @struts.action-forward name="editStatus" path="RailDispoStatusEditTile.def"
 * @struts.action-forward name="entryAction" path="/raildispo/DispoEntry.do"
 */
@AdnTrace
public class DispoAction extends OrderActionSupport {

	public static final String ACTION_SAVE_PREFERENCES = "savePreferences";

	public static final String ACTION_RESET_PREFERENCES = "resetPreferences";

	public static final String ACTION_FILTER_RAIL_DISPOLIST = "filterRailDispoList";

	public static final String ACTION_RESET_FILTER_RAIL_DISPOLIST = "resetFilterRailDispoList";

	private static final String FILTER_INPUT_PATTERN = "[^\\w\\d]";

	public ActionForward execute(
		ActionMapping mapping, ActionForm form,
		HttpServletRequest request,	HttpServletResponse response)
		throws IOException, ServletException
	{
		Authorization.authorize(Authorization.RAIL_DISPATCHING_R);
		setSelectedTab("rail", request);

		DispoForm helpForm = (DispoForm) form;

		String forwardName = "input";

		DispoService dispoService = getServiceLocator().getDispoService();

		/* Handle direct calls of this action by redirecting to entry action. */
		if (helpForm.getSelectedTracks() == null) {
			forwardName = "entryAction";
			return mapping.findForward(forwardName);
		}

		final String formAction = !StringUtils.empty(helpForm.getFormAction()) ? helpForm.getFormAction()
				: "";
		if (!StringUtils.empty(formAction)) {
			if (formAction.equals(ACTION_SAVE_PREFERENCES)) {
				final List<PreferencesEntity> preferencesList = copyFormDataToPreferences(helpForm);
				final UserService userService = getUserService();
				userService.savePreferences(getUser(),
						Constant.USER_PREFERENCE_SCREEN_RAIL_SCHEDULING, preferencesList);
			}
			else if (formAction.equals(ACTION_RESET_PREFERENCES)) {
				// Remove the previous user settings for this screen.
				getUserService().removePreferences(getUser(),
						Constant.USER_PREFERENCE_SCREEN_RAIL_SCHEDULING);
				helpForm.clearDefaults();
			}
			else if (formAction.equals(ACTION_FILTER_RAIL_DISPOLIST)) {
				helpForm.setRailDispoFiltering(true);

				//Handle the user inputs and purge them to the right format
				String purgedWagonNumberFilter = helpForm.getWagonNumberFilter().replaceAll(
						FILTER_INPUT_PATTERN, "");
				String purgedOrderNumberFilter = helpForm.getOrderNumberFilter().replaceAll(
						FILTER_INPUT_PATTERN, "");
				String purgedMaterialFilter = helpForm.getProductNameFilter().replaceAll(
						FILTER_INPUT_PATTERN, "");

				helpForm.setWagonNumberFilter(purgedWagonNumberFilter);
				helpForm.setOrderNumberFilter(purgedOrderNumberFilter);
				helpForm.setProductNameFilter(purgedMaterialFilter);
			}
			else if (formAction.equals(ACTION_RESET_FILTER_RAIL_DISPOLIST)) {
				helpForm.setRailDispoFiltering(false);
				helpForm.setWagonNumberFilter(null);
				helpForm.setOrderNumberFilter(null);
				helpForm.setProductNameFilter(null);
			}
			// Intentionally leaving forwardName on "input" --> RailDispoTile.jsp
		}
		else if (request.getParameter("setPlant") != null) {
			if (helpForm.getSelectedPlant() != null) {
				helpForm.setShippingpoints(dispoService.fetchRailShippingpontsForPlant(helpForm.getSelectedPlant()));
				helpForm.setTracks(dispoService.fetchRailSchedulingTrackList(helpForm.getSelectedPlant(),
						RequestUtils.getUserLocale(request, null).getLanguage()));
			}
			else {
				helpForm.setShippingpoints(new LinkedList<RailPlantSpEntity>());
				helpForm.setTracks(new LinkedList<DispoPlantTrackListEntity>());
			}
			helpForm.restoreSelectedShippingpointsForSelectedPool();
			helpForm.restoreSelectedTracksForSelectedPool();
			helpForm.initSelectedStatus();
			forwardName = "shippingpointsAndTracks";
		}
		else if (request.getParameter("setShippingpoints") != null) {
			forwardName = "shippingpointsAndTracks";
		}
		else if (request.getParameter("setTracks") != null) {
			forwardName = "shippingpointsAndTracks";
		}
		else if (request.getParameter("setShowNoTrack") != null) {
			forwardName = "shippingpointsAndTracks";
		}
		else if (request.getParameter("setStatus") != null) {
			forwardName = "shippingpointsAndTracks";
		}
		else if (request.getParameter("editShippingpoints") != null) {
			forwardName = "editShippingpoints";
		}
		else if (request.getParameter("editTracks") != null) {
			forwardName = "editTracks";
		}
		else if (request.getParameter("editStatus") != null) {
			forwardName = "editStatus";
		}

		return mapping.findForward(forwardName);
	}
	
	/**
	 * Constructs a list of Preferences entities from the DispoForm variables.
	 * 
	 * @param dispoForm - The DispoForm instance.
	 * 
	 * @return The list of Preferences entities.
	 */
	private List<PreferencesEntity> copyFormDataToPreferences(DispoForm dispoForm) {

		final List<PreferencesEntity> preferencesList = new ArrayList<PreferencesEntity>();

		PreferencesEntity preferencesEntity = new PreferencesEntity();
		preferencesEntity.setPreferenceKey(PreferencesKey.RAILDISPO_PLANT_CD.name());
		final String selectedPlant = dispoForm.getSelectedPlant();
		preferencesEntity.setValStr(selectedPlant);
		preferencesList.add(preferencesEntity);
		
		final Set<Long> selectedShippingpoints = dispoForm.getSelectedShippingpoints();
		if (selectedShippingpoints != null) {
			final String selectedShippingpointsJoined = StringUtils.join(selectedShippingpoints, ",");
			preferencesEntity = new PreferencesEntity();
			preferencesEntity.setPreferenceKey(PreferencesKey.RAILDISPO_SHIPPINGPOINT_ID_LIST.name());
			preferencesEntity.setValStr(selectedShippingpointsJoined);
			preferencesList.add(preferencesEntity);
		}
		
		final Set<Long> selectedTracks = dispoForm.getSelectedTracks();
		if (selectedTracks != null) {
			final String selectedTracksJoined = StringUtils.join(selectedTracks, ",");
			preferencesEntity = new PreferencesEntity();
			preferencesEntity.setPreferenceKey(PreferencesKey.RAILDISPO_TRACKS_PLANTTRACK_CD_LIST.name());
			preferencesEntity.setValStr(selectedTracksJoined);
			preferencesList.add(preferencesEntity);
		}
		
		preferencesEntity = new PreferencesEntity();
		preferencesEntity.setPreferenceKey(PreferencesKey.RAILDISPO_SHOW_NO_TRACK.name());
		final BigDecimal showNoTrack = new BigDecimal(dispoForm.getShowNoTrack() ? 1 : 0);
		preferencesEntity.setValNum(showNoTrack);
		preferencesList.add(preferencesEntity);
		
		return preferencesList;
	}
}
