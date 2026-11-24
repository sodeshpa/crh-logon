/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.ejbtest;

import java.util.Arrays;
import java.util.List;

import com.crh.logon.business.services.interfaces.SapImportService;
import com.crh.logon.constants.Constant;

/**
 * This testcase is called from 'logon-business/build-test.xml',
 * update-testdata target.
 * 
 * @author AdNovum Informatik AG
 */
public class SapImportTestCase extends JunitSupport {
	
	public SapImportTestCase(String testCaseName) {
		super(testCaseName);
	}

	/**
	 * Run all SAP import cronjobs in the same order as they would run in production.
	 * Check 'logon-admin-web/etc/gdi/webconfig/merge/application-context.xml' for details.
	 */
	public void testRunSapImports() {
		SapImportService sapImportService = serviceLocator.getSapImportService();
		List<String> sapImportCronStatuses = Arrays.asList(
				Constant.CRONSTATUS_SAPIMPORT_MN,
				Constant.CRONSTATUS_SAPIMPORT_BPN,
				Constant.CRONSTATUS_SAPIMPORT_VN,
				Constant.CRONSTATUS_SAPIMPORT_SDN,
				Constant.CRONSTATUS_SAPIMPORT_VEHN,
				Constant.CRONSTATUS_SAPIMPORT_MNN);
		for (String cronstatusName : sapImportCronStatuses) {
			if (sapImportService.shouldImportJobRun(cronstatusName)) {
				sapImportService.doSapImport(cronstatusName);
			}
		}
	}

}






