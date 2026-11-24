/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.dispokarte;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.interfaces.UserService;
import com.crh.logon.business.services.utils.PreferencesKey;
import com.crh.logon.business.services.utils.UserPreferenceUtil;
import com.crh.logon.constants.Constant;
import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.forms.dispokarte.DispokarteForm;
import com.crh.logon.persistence.entities.admin.PreferencesEntity;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_dispokarte_DispokarteForm"
 *  path="/dispokarte/DispokarteShipToEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="DispokarteShipToTile.def"
 */
public class DispokarteShipToEntryAction extends OrderActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(DispokarteShipToEntryAction.class);

	public static final String ACTION_SAVE_PREFERENCES = "savePreferences";

	public static final String ACTION_RESET_PREFERENCES = "resetPreferences";

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
			Authorization.authorize(Authorization.MASTER_DATA_SHIPTO_CARD_R);
			setSelectedTab("masterdata", request);
			setSelectedSub1Tab("dispokarte", request);
			
			DispokarteForm helpForm;
			String forwardName = "success";
			
		    // Get the struts form.
		    helpForm = (DispokarteForm) form;

			String formAction = helpForm.getFormAction();
			final UserService userService = getUserService();
			UserEntity user = getUser();
			if (!StringUtils.empty(formAction)) {
				if (formAction.equals(ACTION_SAVE_PREFERENCES)) {
					final List<PreferencesEntity> preferences = copyFormDataToPreferences(helpForm);
					//using the same preference as the Order shipto search screen
					userService.savePreferences(user, Constant.USER_PREFERENCE_SCREEN_ORDER_SHIPTO, preferences);
				}
				else if (formAction.equals(ACTION_RESET_PREFERENCES)) {
					userService.removePreferences(user, Constant.USER_PREFERENCE_SCREEN_ORDER_SHIPTO);
					helpForm.clear();
				}
			} else {
				helpForm.completeInit();
			}

			helpForm.setTab(DispokarteForm.TAB_SHIPTO);
			helpForm.setShiptoTypeTab(DispokarteForm.TAB_SHIPTO_TYPE_ROAD);

			copyPreferencesToFormData(userService.fetchPreferencesList(user, Constant.USER_PREFERENCE_SCREEN_ORDER_SHIPTO), helpForm);
		    
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

	private static List<PreferencesEntity> copyFormDataToPreferences(DispokarteForm dispokarteForm) {
		final List<PreferencesEntity> preferencesList = new ArrayList<PreferencesEntity>();

		PreferencesEntity preferencesEntity = new PreferencesEntity();
		preferencesEntity.setPreferenceKey(PreferencesKey.ORDER_SHIPTO_ACTIVE_AGG_CONTRACT.name());
		final BigDecimal isWithActiveAggContract = new BigDecimal(dispokarteForm.isWithActiveAggContracts() ? 1 : 0);
		preferencesEntity.setValNum(isWithActiveAggContract);
		preferencesList.add(preferencesEntity);

		preferencesEntity = new PreferencesEntity();
		preferencesEntity.setPreferenceKey(PreferencesKey.ORDER_SHIPTO_ACTIVE_CEM_CONTRACT.name());
		final BigDecimal isWithActiveCemContract = new BigDecimal(dispokarteForm.isWithActiveCemContracts() ? 1 : 0);
		preferencesEntity.setValNum(isWithActiveCemContract);
		preferencesList.add(preferencesEntity);

		return preferencesList;
	}

	private static void copyPreferencesToFormData(List<PreferencesEntity> preferencesList, DispokarteForm dispokarteForm) {
		Map<String,Object> preferencesMap = UserPreferenceUtil.createPreferencesMapfromList(preferencesList);

		final Boolean withActiveAggContract = UserPreferenceUtil.getBoolean(preferencesMap,
				PreferencesKey.ORDER_SHIPTO_ACTIVE_AGG_CONTRACT);
		if (withActiveAggContract != null) {
			dispokarteForm.setWithActiveAggContracts(withActiveAggContract);
		}

		final Boolean withActiveCemContract = UserPreferenceUtil.getBoolean(preferencesMap,
				PreferencesKey.ORDER_SHIPTO_ACTIVE_CEM_CONTRACT);
		if (withActiveCemContract != null) {
			dispokarteForm.setWithActiveCemContracts(withActiveCemContract);
		}
	}
}
