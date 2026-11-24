/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision: $
 * Date of last edit : $Date: $
 */

package com.crh.logon.admin.web.beans;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class ClassFieldBeanTest {
	
	@Test
	public void correctEqualsBehaviorWithAllFieldsEqual() {
		
		ClassFieldBean b1 = new ClassFieldBean(Object.class, "foobar");
		ClassFieldBean b2 = new ClassFieldBean(Object.class, "foobar");
		
		b1.setBean(true);
		b1.setAssignable(true);
		b1.setLength(10);
		
		b2.setBean(b1.isBean());
		b2.setAssignable(b1.isAssignable());
		b2.setLength(b1.getLength());
		
		assertTrue(b1.equals(b2));
	}
	
	@Test
	public void correctEqualsBehaviorWithNullableFieldsNull() {
		
		ClassFieldBean b1 = new ClassFieldBean(null, null);
		ClassFieldBean b2 = new ClassFieldBean(null, null);
		
		b1.setBean(true);
		b1.setAssignable(true);
		b1.setLength(0);
		
		b2.setBean(true);
		b2.setAssignable(true);
		b2.setLength(0);
		
		assertTrue(b1.equals(b2));
	}
	
	@Test public void correctEqualsBehaviorWithOnlyTestedFieldsEqual() {
		
		/* The implementation of equals() is such that only the attributes
		 * 'type' and 'name' are tested. So equals() should return true even if
		 * the other attributes are different.
		 */
		
		ClassFieldBean b1 = new ClassFieldBean(Object.class, "foobar");
		ClassFieldBean b2 = new ClassFieldBean(Object.class, "foobar");
		
		b1.setBean(true);
		b1.setAssignable(true);
		b1.setLength(10);
		
		b2.setBean(false);
		b2.setAssignable(false);
		b2.setLength(20);
		
		assertTrue(b1.equals(b2));
	}
	
	@Test
	public void correctEqualsBehaviorWithAllTestedFieldsDifferent() {
		
		ClassFieldBean b1 = new ClassFieldBean(Object.class, "foobar");
		ClassFieldBean b2 = new ClassFieldBean(String.class, "barfoo");
		
		assertFalse(b1.equals(b2));
	}
	
	@Test
	public void correctEqualsBehaviorWithOnlyFirstTestedFieldDifferent() {
		
		ClassFieldBean b1 = new ClassFieldBean(Object.class, "foobar");
		ClassFieldBean b2 = new ClassFieldBean(String.class, "foobar");
		
		assertFalse(b1.equals(b2));
	}
	
	@Test
	public void correctEqualsBehaviorWithOnlySecondTestedFieldDifferent() {
		
		ClassFieldBean b1 = new ClassFieldBean(Object.class, "foobar");
		ClassFieldBean b2 = new ClassFieldBean(Object.class, "barfoo");
		
		assertFalse(b1.equals(b2));
	}
		
	@Test
	public void correctEqualsBehaviorWithOnlyFirstTestedFieldNull() {
		
		ClassFieldBean b1 = new ClassFieldBean(Object.class, "foobar");
		ClassFieldBean b2 = new ClassFieldBean(null, "foobar");
		
		// Test in both directions to ensure symmetry.
		assertFalse(b1.equals(b2));
		assertFalse(b2.equals(b1));
	}
	
	@Test
	public void correctEqualsBehaviorWithOnlySecondTestedFieldNull() {
		
		ClassFieldBean b1 = new ClassFieldBean(Object.class, "foobar");
		ClassFieldBean b2 = new ClassFieldBean(Object.class, null);
		
		// Test in both directions to ensure symmetry.
		assertFalse(b1.equals(b2));
		assertFalse(b2.equals(b1));
	}

	@Test
	public void correctEqualsBehaviorWithDifferentType() {
		
		ClassFieldBean b1 = new ClassFieldBean(Object.class, "foobar");
		String s = "hello";
		
		assertFalse(b1.equals(s));
	}
	
	@Test
	public void correctEqualsBehaviorWhenComparedWithNull () {
		
		ClassFieldBean b1 = new ClassFieldBean(Object.class, "foobar");
		ClassFieldBean b2 = null;
		
		assertFalse(b1.equals(b2));
	}
	
	public void correctEqualsBehaviorWhenComparedWithNullOfDifferentType() {
		
		ClassFieldBean b1 = new ClassFieldBean(Object.class, "foobar");
		String s = null;
		
		assertFalse(b1.equals(s));
	}
	
	@Test
	public void hashCodeDoesntChangeIfFieldsAffectingEqualsDontChange() {
		
		ClassFieldBean b1 = new ClassFieldBean(Object.class, "foobar");
		ClassFieldBean b2 = new ClassFieldBean(Object.class, "foobar");
		
		b1.setAssignable(true);
		b1.setBean(true);
		b1.setLength(10);
		
		b2.setAssignable(false);
		b2.setBean(false);
		b2.setLength(20);
		
		assertEquals(b1.hashCode(), b2.hashCode());
	}

	@Test
	public void hashCodeChangesIfOnlyFieldsAffectingEqualsChange() {
		
		ClassFieldBean b1 = new ClassFieldBean(Object.class, "foobar");
		ClassFieldBean b2 = new ClassFieldBean(Object.class, "barfoo");
		ClassFieldBean b3 = new ClassFieldBean(String.class, "foobar");
		
		b1.setAssignable(true);
		b1.setBean(true);
		b1.setLength(10);
		
		b2.setAssignable(b1.isAssignable());
		b2.setBean(b1.isBean());
		b2.setLength(b1.getLength());
		
		b3.setAssignable(b1.isAssignable());
		b3.setBean(b1.isBean());
		b3.setLength(b1.getLength());
		
		assertNotEquals(b1.hashCode(), b2.hashCode());
		assertNotEquals(b1.hashCode(), b3.hashCode());
		assertNotEquals(b2.hashCode(), b3.hashCode());
	}
}
