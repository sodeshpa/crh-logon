/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.admin.web.ajax;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import com.crh.logon.admin.web.forms.test.ManualWebserviceTriggerForm;
import com.crh.logon.business.services.utils.FileNameSelectionBean;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.utils.Authorization;

/**
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to 
 * AJAX components. 
 * 
 * This is used by the test page.
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="TestAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.services.utils.FileNameSelectionBean"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 */
public class TestAjaxService extends DwrServiceSupport {
    
	public List <FileNameSelectionBean> getFileNameList(String[] path, HttpSession session) {
    	
    	Authorization.authorize(Authorization.TEST_WEBSERVICE);
    	ManualWebserviceTriggerForm wsForm = (ManualWebserviceTriggerForm)session.getAttribute("admin_ManualWebserviceTriggerForm");
    	List <FileNameSelectionBean> fileNameSelectionList = getServiceLocator().getTestService().getFileNameSelectionList(StringUtils.join(path, File.separator));
    	wsForm.getWebServiceOptions().setPathElements(Arrays.asList(path));
		if (fileNameSelectionList == null || fileNameSelectionList.isEmpty()) {
			return fileNameSelectionList = Collections.emptyList();
		}
    	
    	return fileNameSelectionList;   	
    }
}

