/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.auftrag;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author AdNovum Informatik AG
 */
public class OrderDetailActionTests
{
	@Test
	public void removingEmptySetResultsInOriginalSet()
	{
		final List<Long> a = Collections.unmodifiableList((Arrays.asList(new Long[] {1L, 2L})));
		final List<Long> b = Collections.unmodifiableList(new LinkedList<Long>());
		final List<Long> c = OrderDetailAction.subtractList(a, b);
		
		assertEquals(a, c);
	}
	
	@Test
	public void noErrorToRemoveElementsNotInOriginalSet()
	{
		final List<Long> a = Collections.unmodifiableList(Arrays.asList(new Long[] {1L, 2L}));
		final List<Long> b = Collections.unmodifiableList(Arrays.asList(new Long[] {3L, 4L}));
		final List<Long> c = OrderDetailAction.subtractList(a, b);
		
		assertEquals(a, c);
	}

	@Test
	public void correctElementsAreRemoved()
	{
		final List<Long> a = Collections.unmodifiableList(Arrays.asList(new Long[] {1L, 2L, 3L}));
		final List<Long> b = Collections.unmodifiableList(Arrays.asList(new Long[] {3L, 4L, 5L}));
		final List<Long> c = Collections.unmodifiableList(Arrays.asList(new Long[] {1L, 2L}));
		final List<Long> d = Collections.unmodifiableList(Arrays.asList(new Long[] {4L, 5L}));
		
		Assert.assertEquals(c, OrderDetailAction.subtractList(a, b));
		Assert.assertEquals(d, OrderDetailAction.subtractList(b, a));

	}
}
