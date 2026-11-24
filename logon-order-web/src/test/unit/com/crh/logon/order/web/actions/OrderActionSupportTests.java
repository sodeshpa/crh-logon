/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author AdNovum Informatik AG
 */
public class OrderActionSupportTests 
{
	@Test
	public void emptyStringIsParsedToEmptyList()
	{
		assertTrue(OrderActionSupport.parseNumberList("").isEmpty());
	}
	
	@Test
	public void parsedListContainsCorrectNumberOfElements()
	{
		Assert.assertEquals(3, OrderActionSupport.parseNumberList("1,2,3").size());
	}
	
	@Test
	public void correctNumbersAreParsed()
	{
		final List<Long> expectedList = new LinkedList<Long>();
		expectedList.add(1L);
		expectedList.add(2L);
		expectedList.add(3L);
		
		Assert.assertEquals(expectedList, OrderActionSupport.parseNumberList("1,2,3"));
	}

	@Test
	public void parsingIsDoneUntilIllegalNumber()
	{
		final List<Long> expectedList = new LinkedList<Long>();
		expectedList.add(1L);
		expectedList.add(2L);

		Assert.assertEquals(expectedList, OrderActionSupport.parseNumberList("1,2,x"));
	}
	
}
