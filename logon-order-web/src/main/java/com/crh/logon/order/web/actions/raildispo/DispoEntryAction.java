/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.raildispo;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.forms.raildispo.DispoForm;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.DispoService;
import com.crh.logon.business.services.interfaces.UserService;
import com.crh.logon.business.services.utils.PreferencesKey;
import com.crh.logon.business.services.utils.UserPreferenceUtil;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.admin.PreferencesEntity;
import com.crh.logon.persistence.entities.views.DispoPlantTrackListEntity;
import com.crh.logon.persistence.entities.views.RailPlantEntity;
import com.crh.logon.persistence.entities.views.RailPlantSpEntity;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_raildispo_DispoForm"
 *  path="/raildispo/DispoEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="RailDispoTile.def"
 */
public class DispoEntryAction extends OrderActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(DispoEntryAction.class);

	public ActionForward execute(
		ActionMapping mapping, ActionForm form,
		HttpServletRequest request,	HttpServletResponse response)
		throws IOException, ServletException
	{
		
		// [[
		ActionForward result = null;
		Object[][] params = {{"mapping", mapping},
							{"form", form},
							{"request", request},
							{"response", response}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "execute", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.RAIL_DISPATCHING_R);
			setSelectedTab("rail", request);
			
			DispoForm helpForm;
			String forwardName = "success";
			
		    // Get the struts form.
		    helpForm = (DispoForm) form;
		    
		    if (helpForm.getHeightFactor() < 0){
		    	helpForm.setHeightFactor((float)getConfigBean().getAppDefaultDispoHeightFactor());
		    }
			
		    if (helpForm.getPlants() == null) {
			    DispoService dispoService = getServiceLocator().getDispoService();
			    List<RailPlantEntity> plants = dispoService.fetchAllRailPlants();
			    if ( !plants.isEmpty() ){
				    helpForm.setPlants(plants);
			    } else {
			    	helpForm.setDbIsEmpty(Boolean.TRUE);
			    	MessageUtils.addActionWarning(request, "raildispo.warning.emptydb");
			    	LOG.info("The DB is empty yet");
			    }
		    }

			//--- Ensure we have a timezone set early on ---//
			helpForm.setUserTimeZone(getUserTimezone());

			//--- Now set out initial date ---//
			helpForm.setDate(getUserTimezone().today());
		    
			if (!helpForm.isApplyUserDefaultSettings()) {
				copyPreferencesToForm(helpForm, request.getLocale());
			}
			
			return result = mapping.findForward(forwardName);
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
	 * Loads the user preferences and sets the struts form values based on them.
	 * 
	 * @param dispoForm - The struts form instance.
	 * @param locale - The locale of the user.
	 */
	private void copyPreferencesToForm(DispoForm dispoForm, Locale locale) {
		dispoForm.clearDefaults();
		
		final UserService userService = getUserService();
		final List<PreferencesEntity> preferencesList = userService.fetchPreferencesList(getUser(),
				Constant.USER_PREFERENCE_SCREEN_RAIL_SCHEDULING);
		if (preferencesList != null && !preferencesList.isEmpty()) {
			saveDefaultValuesToForm(dispoForm, locale, preferencesList);
		}
	}
	
	/**
	 * Uses the user preferences and to set the struts form values based.
	 * 
	 * @param dispoForm - The struts form instance.
	 * @param locale - The locale of the user.
	 * @param preferencesList - The map which containes the user prefernce key-value pairs.
	 */
	private void saveDefaultValuesToForm(DispoForm dispoForm, Locale locale,
			List<PreferencesEntity> preferencesList) {

		final Map<String, Object> preferencesMap = UserPreferenceUtil.createPreferencesMapfromList(preferencesList);

		dispoForm.setApplyUserDefaultSettings(true);
		
		final List<RailPlantEntity> plants = dispoForm.getPlants();
		
		if ((plants != null) && (plants.size() > 0)){	
			
			// Setting the selected plant
			String defaultSelectedPlantCd = UserPreferenceUtil.getString(preferencesMap, PreferencesKey.RAILDISPO_PLANT_CD);
			if (defaultSelectedPlantCd != null && !StringUtils.isEmpty(defaultSelectedPlantCd) && 
					isPlantCdInPlantList(defaultSelectedPlantCd, plants)) {
				dispoForm.setSelectedPlant(defaultSelectedPlantCd);
			}
			else {
				// Set the first plant as selected
				defaultSelectedPlantCd = plants.get(0).getPlantCd();
				dispoForm.setSelectedPlant(defaultSelectedPlantCd);
			}
			
			// Loading all the shippingpoints based on the selected plant			
			final DispoService dispoService = getServiceLocator().getDispoService();
			final List<RailPlantSpEntity> allShippingpointEntities = dispoService.fetchRailShippingpontsForPlant(defaultSelectedPlantCd);
			dispoForm.setShippingpoints(allShippingpointEntities);
			
			// Set the selected shippingpoints based on and user setting
			final Set<Long> selectedShippingpoints = dispoForm.getSelectedShippingpoints();
			final Object selectedShippingpointIDsJoined = preferencesMap.get(PreferencesKey.RAILDISPO_SHIPPINGPOINT_ID_LIST.name());
			if (selectedShippingpointIDsJoined != null && selectedShippingpointIDsJoined instanceof String) {
				final String selectedShippingpointIDsJoinedString = (String) selectedShippingpointIDsJoined;
				final String[] defaultShippingpointIDs = StringUtils.split(
						selectedShippingpointIDsJoinedString, ",");
				if (defaultShippingpointIDs != null && defaultShippingpointIDs.length > 0) {
					for (String defaultShippingpointIDString : defaultShippingpointIDs) {
						final Long defaultShippingpointID = Long.parseLong(defaultShippingpointIDString);
						// Only those user selected shippingpoints are considered valid, which are in the allShippingpointEntities list.
						if (isShippingpointIdInShippingpointList(defaultShippingpointID,
								allShippingpointEntities)) {
							selectedShippingpoints.add(defaultShippingpointID);
						}
					}
				}
			}
			dispoForm.setSelectedShippingpoints(selectedShippingpoints);
			
			// Loading the tracks based on the selected plant
			final List<DispoPlantTrackListEntity> allTracks = dispoService.fetchRailSchedulingTrackList(defaultSelectedPlantCd, locale.getLanguage()); 
			dispoForm.setTracks(allTracks);
			
			// Set the selected tracks based on and user setting
			final Set<Long> selectedTracks = dispoForm.getSelectedTracks();
			final Object selectedTrackCdsJoined = preferencesMap.get(PreferencesKey.RAILDISPO_TRACKS_PLANTTRACK_CD_LIST.name());
			if (selectedTrackCdsJoined != null && selectedTrackCdsJoined instanceof String) {
				final String selectedTrackCdsJoinedString = (String) selectedTrackCdsJoined;
				final String[] defaultTrackCds = StringUtils.split(
						selectedTrackCdsJoinedString, ",");
				if (defaultTrackCds != null && defaultTrackCds.length > 0) {
					for (String defaultTrackCdString : defaultTrackCds) {
						final Long defaultTrackCd = Long.parseLong(defaultTrackCdString);
						if (isPlantTrackCdIdInTrackList(defaultTrackCd, allTracks)) {
							selectedTracks.add(defaultTrackCd);
						}
					}
				}
			}
			dispoForm.setSelectedTracks(selectedTracks);
	    }
	    else{
	    	dispoForm.setShippingpoints(new LinkedList<RailPlantSpEntity>());
	    	dispoForm.setTracks(new LinkedList<DispoPlantTrackListEntity>());
	    }
		dispoForm.initShowNoTrackCache();
		dispoForm.initSelectedShippingpointsCache();
		dispoForm.initselectedTracksCache();

		final Boolean showNoTrack = UserPreferenceUtil.getBoolean(preferencesMap, PreferencesKey.RAILDISPO_SHOW_NO_TRACK);
		if (showNoTrack != null) {
			dispoForm.setShowNoTrack(showNoTrack);
		}

		dispoForm.initSelectedStatus();
	}
	
	private boolean isPlantCdInPlantList(String plantCd, List<RailPlantEntity> plants) {
		if (plantCd == null || plants == null || plants.isEmpty()) {
			return false;
		}
		for (RailPlantEntity railPlantEntity : plants) {
			if (railPlantEntity.getPlantCd().equals(plantCd)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isShippingpointIdInShippingpointList(Long shippingpointId, List<RailPlantSpEntity> shippingpoints) {
		if (shippingpointId == null || shippingpoints == null || shippingpoints.isEmpty()) {
			return false;
		}
		else {
			for (RailPlantSpEntity shippingpoint : shippingpoints) {
				if (shippingpoint.getShippingpointId().equals(shippingpointId)) {
					return true;
				}
			}
			return false;
		}
	}
	
	private boolean isPlantTrackCdIdInTrackList(Long plantTrackCd, List<DispoPlantTrackListEntity> tracks) {
		if (plantTrackCd == null || tracks == null || tracks.isEmpty()) {
			return false;
		}
		else {
			for (DispoPlantTrackListEntity track : tracks) {
				if (track.getPlantTrackCd().equals(plantTrackCd)) {
					return true;
				}
			}
			return false;
		}
	}
}
