/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.useradmin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.forms.useradmin.UserAdminForm;
import com.crh.logon.business.services.interfaces.UserAdminService;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.persistence.entities.admin.RoleEntity;
import com.crh.logon.persistence.entities.refdata.UserTypeTxEntity;
import com.crh.logon.persistence.entities.views.UserTypeViewEntity;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_UserAdminForm" path="/SelfAdmin" scope="session"
 *  validate="true" input="UserAdminTile.def"
 *
 * @struts.action-forward name="fetchfirst" path="/UserAdminEntry.do"
 * @struts.action-forward name="input" path="UserAdminTile.def"
 */
public class UserAdminHelper extends AdminActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(UserAdminHelper.class);

    public void initEntryAction(HttpServletRequest request, UserAdminForm helpForm, String source) {
		
	    helpForm.clear();
	    
	    Locale locale = getLocale(request);
	    
	    helpForm.setLocale(locale);

	    UserAdminService userAdminService = getServiceLocator().getUserAdminService();
	    List userLanguageList = userAdminService.findLangTxEntityListDependingOnAdminSource(source, locale);
	    helpForm.setUserLanguageList(userLanguageList);
	    
	    //loading roles
	    List rolesToDisplay = new ArrayList();
	    //setting an additional role for indicating the empty role
	    RoleEntity emptyRole = new RoleEntity();
	    emptyRole.setRoleName("");
	    rolesToDisplay.add(emptyRole);
	    rolesToDisplay.addAll(userAdminService.fetchRoleList(RoleEntity.ATTR_ROLE_ID, true));
	    helpForm.setRoleList(rolesToDisplay);
	    
	    helpForm.setSearchState(UserAdminForm.SEARCH_STATE_WITHOUT_FILTER);
	    //loading user types
	    List userTypesAllowedToCreate = getAllowedUserTypesByLocale(locale);
	    helpForm.setAllowedUserTypeList(userTypesAllowedToCreate);
	    
	    //some parts of the code uses this part as cache so I could not delete it.
	    List userTypes = getRefDataGenericsService().findTxEntityList(UserTypeTxEntity.class, request.getLocale());
	    helpForm.setAllUserTypeList(userTypes);

		List<TimeZone> zones = getTimeZoneService().loadTimeZonesForLocale(locale);
		Collections.sort(zones, new TimeZone.AscComparator(locale));
        helpForm.setTimeZoneList(zones);
	}
    
    private List<UserTypeViewEntity> getAllowedUserTypesByLocale(Locale locale){
    	UserAdminService userAdminService = getServiceLocator().getUserAdminService();
    	List<UserTypeViewEntity> result = userAdminService.getUserTypesToCreate(locale.getLanguage());
    	filter(result);
    	return result;
    }
    
    private void filter(List<UserTypeViewEntity> userTypes) {
    	Iterator<UserTypeViewEntity> it = userTypes.iterator();
    	
    	while (it.hasNext()) {
    		UserTypeViewEntity current = it.next();
    		if (!Authorization.isAllowed(current.getFunctionId())) {
    			it.remove();
    		}
    	}
    }
}
