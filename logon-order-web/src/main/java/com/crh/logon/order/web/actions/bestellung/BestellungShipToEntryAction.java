/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.bestellung;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.interfaces.UserService;
import com.crh.logon.business.services.utils.PreferencesKey;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.business.services.utils.UserPreferenceUtil;
import com.crh.logon.constants.Constant;
import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.forms.bestellung.BestellungForm;
import com.crh.logon.persistence.entities.admin.PreferencesEntity;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_bestellung_BestellungForm"
 *  path="/bestellung/BestellungShipToEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="BestellungShipToTile.def"
 */
public class BestellungShipToEntryAction extends OrderActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(BestellungShipToEntryAction.class);

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
			Authorization.authorize(Authorization.ORDER);
			
			setSelectedTab("ccbestellung", request);
			
			BestellungForm helpForm;
			String forwardName = "success";
			
		    helpForm = (BestellungForm) form;
		    
		    // --- Status message if arriving from Daily order status change screen ---
		    if ("changeDailyOrderStatus".equals(helpForm.getReferrer()) && helpForm.getShipTo() != null && helpForm.getShipTo().getShiptoSapBp() != null) {
		    	MessageUtils.addActionMessage(request, "daily.order.status.success", helpForm.getShipTo().getShiptoSapBp().getName1());
		    }

			String formAction = helpForm.getFormAction();
			final UserService userService = getUserService();
			UserEntity user = getUser();
			if (!StringUtils.empty(formAction)) {
				if (formAction.equals(ACTION_SAVE_PREFERENCES)) {
					final List<PreferencesEntity> preferences = copyFormDataToPreferences(helpForm);
					userService.savePreferences(user, Constant.USER_PREFERENCE_SCREEN_ORDER_SHIPTO, preferences);
				}
				else if (formAction.equals(ACTION_RESET_PREFERENCES)) {
					userService.removePreferences(user, Constant.USER_PREFERENCE_SCREEN_ORDER_SHIPTO);
					helpForm.clear();
				}
			} else {
				helpForm.clear();
			}

			Locale locale = getLocale(request);
			helpForm.setLocale(locale);

			List<TimeZone> zones = getTimeZoneService().loadTimeZonesForLocale(locale);
			Collections.sort(zones, new TimeZone.AscComparator(locale));
			helpForm.setTimeZoneList(zones);

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

	private static List<PreferencesEntity> copyFormDataToPreferences(BestellungForm bestellungForm) {
		final List<PreferencesEntity> preferencesList = new ArrayList<PreferencesEntity>();

		PreferencesEntity preferencesEntity = new PreferencesEntity();
		preferencesEntity.setPreferenceKey(PreferencesKey.ORDER_SHIPTO_ACTIVE_AGG_CONTRACT.name());
		final BigDecimal isWithActiveAggContract = new BigDecimal(bestellungForm.isWithActiveAggContracts() ? 1 : 0);
		preferencesEntity.setValNum(isWithActiveAggContract);
		preferencesList.add(preferencesEntity);

		preferencesEntity = new PreferencesEntity();
		preferencesEntity.setPreferenceKey(PreferencesKey.ORDER_SHIPTO_ACTIVE_CEM_CONTRACT.name());
		final BigDecimal isWithActiveCemContract = new BigDecimal(bestellungForm.isWithActiveCemContracts() ? 1 : 0);
		preferencesEntity.setValNum(isWithActiveCemContract);
		preferencesList.add(preferencesEntity);

		return preferencesList;
	}

	private static void copyPreferencesToFormData(List<PreferencesEntity> preferencesList, BestellungForm bestellungForm) {
		Map<String,Object> preferencesMap = UserPreferenceUtil.createPreferencesMapfromList(preferencesList);

		final Boolean withActiveAggContract = UserPreferenceUtil.getBoolean(preferencesMap,
				PreferencesKey.ORDER_SHIPTO_ACTIVE_AGG_CONTRACT);
		if (withActiveAggContract != null) {
			bestellungForm.setWithActiveAggContracts(withActiveAggContract);
		}

		final Boolean withActiveCemContract = UserPreferenceUtil.getBoolean(preferencesMap,
				PreferencesKey.ORDER_SHIPTO_ACTIVE_CEM_CONTRACT);
		if (withActiveCemContract != null) {
			bestellungForm.setWithActiveCemContracts(withActiveCemContract);
		}
	}
}
