/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.ejbtest;
import com.crh.logon.business.services.interfaces.RFIDService;

/**
 * This testcase is called from 'logon-business/build-test.xml',
 * update-testdata target.
 * 
 * @author AdNovum Informatik AG
 */
public class RFIDObjectLabelCleanupTestCase extends JunitSupport {
	
	public RFIDObjectLabelCleanupTestCase(String testCaseName) {
		super(testCaseName);
	}

	/**
	 * Simulate the work of 'rfid_object_label_cleanup' cronjob.
	 */
	public void testRunRFIDObjectLabelCleanup() {
		RFIDService rfidService = serviceLocator.getRFIDService();
		rfidService.syncRFIDLabels();
	}
}






