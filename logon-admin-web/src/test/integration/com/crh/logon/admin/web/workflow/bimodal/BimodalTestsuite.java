/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.workflow.bimodal;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author AdNovum Informatik AG
 */
// Deactivated test, because it never worked in the way it was supposed to.
// Mar 13, 2015/fszabo: see ticket LOGON-9269
@RunWith(Suite.class)
@SuiteClasses({
	// FIXME Mar 16, 2015/fszabo: keep in mind to check this test, too!
	BimodalCreatingShippingpointTest.class //,
//	BimodalCreatingOrderTemplateTest.class,
//	BimodalCreatingWagonTypeForShiptoTest.class,
//	BimodalCreatingRoadOrderTest.class,
//	BimodalCreatingEmptyWagonTest.class,
//	BimodalDeliveringRailOrderTest.class,
//	BimodalCreatingBulkPowerTruckTest.class,
//	BimodalSchedulingLoadingRoadOrderTest.class
	})
public class BimodalTestsuite {}
