/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import static org.junit.Assert.*;

import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

public class DefaultTimeZoneTest {

	static {
		java.util.TimeZone tempTimeZone = java.util.TimeZone.getTimeZone("Europe/Vaduz");
		tempTimeZone.setDefault(tempTimeZone);
	}
	
	@Test
	public void defaultTimeZoneShouldBeEuropeVaduz(){
		assertEquals("Europe/Vaduz", TimeZone.getDefault().getID());
	}
	
	@Test
	public void defaultTimeZoneDisplayNameShouldBeCET(){
		assertEquals("Central European Time", TimeZone.getDefault().getDisplayName());
	}
	
	@Test
	public void defaultLocaleShouldBeENUS(){
		assertEquals("en_US",Locale.getDefault().toString());
	}
}
