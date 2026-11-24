/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author AdNovum Informatik AG
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
		IntervalTest.class,
		AdjustTimesUtilsElseCaseTest.class,
        AdjustTimesUtilsTest.class,
        AdjustTimesUtilsDeliveryToCaseTest.class,
        AdjustTimesUtilsQuantityCaseTest.class
})
public class AdjustTimesTestSuite {

}
