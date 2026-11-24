/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.ajax;

import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.business.rfidadministration.parameters.RFIDDataCache;
import com.crh.logon.business.services.interfaces.RFIDService;
import com.crh.logon.business.services.localinterfaces.RFIDServiceLocalInterface;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.persistence.entities.common.RfidtagEntity;
import com.crh.logon.utils.ListRange;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.forms.RFIDPopupForm;
import com.crh.logon.web.utils.Authorization;

/**
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to 
 * Ajax components. 
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="RFIDAdminAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.admin.web.utils.*"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 * 
 */
@AdnTrace
public class RFIDAdminAjaxService extends DwrServiceSupport {

	public ListRange searchRFIDTags(int skip, int count, String sortingInformation,
			HashMap<String, String> query, HttpServletRequest request, HttpSession session) {
		Authorization.authorize(Authorization.MASTER_DATA_RFID_MAINTENANCE);
		
		ServiceLocator serviceLocator = getServiceLocator();
		RFIDService rfidService = serviceLocator.getRFIDService();

		String sortBy = RfidtagEntity.ATTR_RFID_IDENTITY;
		boolean sortAscending = true;
		if (!StringUtils.isEmpty(sortingInformation)) {
			String[] sortingWords = sortingInformation.split(" ");
			sortBy = sortingWords[0];
			sortAscending = "ASC".equals(sortingWords[1]);
		}

		Locale locale = request.getLocale();
		return rfidService.fetchRfidDetailListForAjax(skip, count, sortBy, 
				sortAscending, query, locale);
	}


	/**
	 * Called when RFID popup is opened to fill RFID drop down 
	 */
	public ListRange fetchRfidList(int start, int count, String query, HttpSession session) {
		Authorization.authorize(Authorization.PUBLIC_FUNCTION);

		RFIDDataCache rfidDataCache = RFIDPopupForm.getRFIDDataCache(session);

		ServiceLocator serviceLocator = getServiceLocator();
		final RFIDServiceLocalInterface rfidService = serviceLocator.getLocalRFIDService();

		return rfidService.findAssignableRfids(start, count, query, rfidDataCache);
	}
}
