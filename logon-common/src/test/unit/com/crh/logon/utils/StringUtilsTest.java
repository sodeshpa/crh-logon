/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author AdNovum Informatik AG
 */
public class StringUtilsTest {

	@Test
	public void testFormatMin() {
		
		Long min = 80L;
		String formatted = StringUtils.formatHreMinString(min);
		Assert.assertEquals("00000120", formatted);

		min = 122L;
		formatted = StringUtils.formatHreMinString(min);
		Assert.assertEquals("00000202", formatted);
		
	}

}
