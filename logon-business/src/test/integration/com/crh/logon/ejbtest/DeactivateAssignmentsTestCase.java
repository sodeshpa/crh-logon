/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.ejbtest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.crh.logon.business.services.interfaces.TestService;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class DeactivateAssignmentsTestCase extends JunitSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(
			CustorderServiceTestCase.class);

	public DeactivateAssignmentsTestCase(String testCaseName) {
		super(testCaseName);
	}

	@Test
	public void testDeactivateActiveAssignments()
	{
		final TestService testService = serviceLocator.getTestService();
		
		final List<Long> truckIds = new ArrayList<Long>();
		truckIds.add(-100L);
		truckIds.add(-1001L);
		truckIds.add(-403L);
		truckIds.add(-419L);
		
		final List<Long> statusCds = new ArrayList<Long>();
		statusCds.add(0L);
		statusCds.add(2L);
		statusCds.add(3L);
		
		testService.deactivateActiveAssignments(truckIds, statusCds);
	}
}
