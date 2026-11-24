
package com.crh.logon.web.utils;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DropdownEntityTest {
	
	@Test
	public void equalsIsTrueIfBothFieldsEqual() {
		
		assertTrue((new DropdownEntity("foobar", "toto")).equals(new DropdownEntity("foobar", "toto")));
	}
	
	@Test
	public void equalsIsTrueIfBothFieldsEqualAndFirstFieldNull() {
		
		assertTrue((new DropdownEntity(null, "toto")).equals(new DropdownEntity(null, "toto")));
	}
	
	@Test
	public void equalsIsTrueIfBothFieldsEqualAndSecondFieldNull() {
		
		assertTrue((new DropdownEntity("foobar", null)).equals(new DropdownEntity("foobar", null)));
	}
	
	@Test
	public void equalsIsTrueIfBothFieldsEqualAndBothFieldsNull() {
		
		assertTrue((new DropdownEntity(null, null)).equals(new DropdownEntity(null, null)));
	}
	
	@Test
	public void equalsIsFalseIfFirstFieldDifferent() {
		
		assertFalse((new DropdownEntity("foobar", "toto")).equals(new DropdownEntity("barfoo", "toto")));
	}
	
	@Test
	public void equalsIsFalseIfSecondFieldDifferent() {
		assertFalse((new DropdownEntity("foobar", "toto")).equals(new DropdownEntity("foobar", "titi")));
	}

	@Test
	public void equalsIsFalseIfBothFieldsDifferent() {
		assertFalse((new DropdownEntity("foobar", "toto")).equals(new DropdownEntity("barfoo", "titi")));
	}

	@Test
	public void equalsIsFalseIfSecondArgumentNull() {
		
		DropdownEntity nullValue = null;
		
		assertFalse((new DropdownEntity("foobar", "toto")).equals(nullValue));
	}
	
	@Test
	public void equalsIsFalseIfSecondArgumentIsDifferentClass() {
		
		assertFalse((new DropdownEntity("foobar", "toto")).equals("Hello"));
	}
	
	@Test
	public void hashCodeIsEqualForEqualValues() {
		
		assertEquals((new DropdownEntity("foobar", "toto")).hashCode(), (new DropdownEntity("foobar", "toto")).hashCode());
	}
}
