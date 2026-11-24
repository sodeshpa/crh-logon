/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.utils;

import static org.hamcrest.CoreMatchers.describedAs;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.Comparator;

import org.junit.Test;

import com.crh.logon.persistence.entities.common.ShippingpointEntity;

/**
 * @author AdNovum Informatik AG
 * Test of ShpippingpointEntity comparator
 */
public class ShippingpointComparatorTest {
	private static final int SHIPTOS_NOT_EQUALS_LHS = -1;
	private static final int SHIPTOS_NOT_EQUALS_RHS = 1;
	private static final int SHIPTOS_EQUALS = 0;
	private static final boolean TRANSITIVE = true;
	
	@Test
	public void compareIfBothNull() {
		ShippingpointEntity lhs = null;
		ShippingpointEntity rhs = null;
		Comparator<ShippingpointEntity> comp = new ShippingpointComparator();
		
		assertThat(
				comp.compare(lhs, rhs),
				describedAs("The 2 object is equals and the result has to be :",
						is(equalTo(SHIPTOS_EQUALS)))); 
	}
	
	@Test
	public void compareIfLhsIsNull() {
		ShippingpointEntity lhs = null;
		ShippingpointEntity rhs = new ShippingpointEntity();
		Comparator<ShippingpointEntity> comp = new ShippingpointComparator();
		
		assertThat(
				comp.compare(lhs, rhs),
				describedAs("The 2 object is not equals and the result has to be (RHS is bigger than LHS):",
						is(equalTo(SHIPTOS_NOT_EQUALS_LHS))));
	}
	
	@Test
	public void compareIfRhsIsNull() {
		ShippingpointEntity lhs = new ShippingpointEntity();
		ShippingpointEntity rhs = null;
		Comparator<ShippingpointEntity> comp = new ShippingpointComparator();
		
		assertThat(
				comp.compare(lhs, rhs),
				describedAs("The 2 object is not equals and the result has to be (LHS is bigger than RHS):",
						is(equalTo(SHIPTOS_NOT_EQUALS_RHS))));
	}
	
	@Test
	public void compareIfLhsShipToNameIsNull() {
		ShippingpointEntity lhs = new ShippingpointEntity();
		lhs.setShippingpointName(null);
		
		ShippingpointEntity rhs = new ShippingpointEntity();
		rhs.setShippingpointName("Pt Exp Gare Lessines");
		Comparator<ShippingpointEntity> comp = new ShippingpointComparator();
		
		assertThat(
				comp.compare(lhs, rhs),
				describedAs("The 2 object is not equals and the result has to be (RHS is bigger because the LHS name field was null):",
						is(equalTo(SHIPTOS_NOT_EQUALS_LHS))));
	}
	
	@Test
	public void compareIfRhsShipToNameIsNull() {
		ShippingpointEntity lhs = new ShippingpointEntity();
		lhs.setShippingpointName("Pt Exp Gare Lessines");
		
		ShippingpointEntity rhs = new ShippingpointEntity();
		rhs.setShippingpointName(null);
		Comparator<ShippingpointEntity> comp = new ShippingpointComparator();
		
		assertThat(
				comp.compare(lhs, rhs),
				describedAs("The 2 object is not equals and the result has to be (LHS is bigger because the RHS name field was null):",
						is(equalTo(SHIPTOS_NOT_EQUALS_RHS))));
	}
	
	@Test
	public void compareIfEquals() {
		ShippingpointEntity lhs = new ShippingpointEntity();
		lhs.setShippingpointName("Pt Exp Gare Lessines");
		
		ShippingpointEntity rhs = new ShippingpointEntity();
		rhs.setShippingpointName("Pt Exp Gare Lessines");
		Comparator<ShippingpointEntity> comp = new ShippingpointComparator();
		
		assertThat(
				comp.compare(lhs, rhs),
				describedAs("The 2 object equals based on the comparator and the result has to be (2 name field):",
						is(equalTo(SHIPTOS_EQUALS))));
	}
	
	@Test
	public void compareIfNotEquals() {
		ShippingpointEntity lhs = new ShippingpointEntity();
		lhs.setShippingpointName("Pt");
		
		ShippingpointEntity rhs = new ShippingpointEntity();
		rhs.setShippingpointName("Pt Exp Gare Lessines");
		Comparator<ShippingpointEntity> comp = new ShippingpointComparator();
		assertThat(
				comp.compare(lhs, rhs),
				describedAs("The 2 object is not equals and the result has to be : (RHS is bigger than LHS based on the name field comparison)",
						is(not((SHIPTOS_EQUALS)))));
	}
	
	/**
	 * The implementor must also ensure that the relation is transitive: ((compare(x, y)>0) && (compare(y, z)>0)) implies compare(x, z)>0.
	 * */
	@Test
	public void transitivityTest() {
		ShippingpointEntity sp1 = new ShippingpointEntity();
		sp1.setShippingpointName("Pt Exp Gare Lessines");
		
		ShippingpointEntity sp2 = new ShippingpointEntity();
		sp2.setShippingpointName("Pt");
		
		ShippingpointEntity sp3 = new ShippingpointEntity();
		sp3.setShippingpointName("P");
		
		Comparator<ShippingpointEntity> comp = new ShippingpointComparator();
		
		int oneAndTwoShipToComparison = comp.compare(sp1, sp2);
		int twoAndThreeShpComparison = comp.compare(sp2, sp3);
		int oneAndThreeShipToComparison = comp.compare(sp1, sp3);
		
		
		boolean isTransitive = ((oneAndTwoShipToComparison > 0) && (twoAndThreeShpComparison > 0) && (oneAndThreeShipToComparison > 0));
		assertThat(isTransitive, describedAs("Transitivity test : ((compare(x, y)>0) && (compare(y, z)>0)) implies compare(x, z)>0.",
				is(equalTo(TRANSITIVE))));
	}
	/**
	 * The implementor must ensure that sgn(compare(x, y)) == -sgn(compare(y, x)) for all x and y.
	 * */
	@Test
	public void simmetryTest() {
		ShippingpointEntity sp1 = new ShippingpointEntity();
		sp1.setShippingpointName("Pt Exp Gare Lessines");
		
		ShippingpointEntity sp2 = new ShippingpointEntity();
		sp2.setShippingpointName("Pt");
		
		Comparator<ShippingpointEntity> comp = new ShippingpointComparator();
		
		double oneAndTwoSpComparison = Math.signum(comp.compare(sp1, sp2));
		double twoAndOneSpComparison = -Math.signum(comp.compare(sp2, sp1));
		
		boolean isSimmetry = (oneAndTwoSpComparison == twoAndOneSpComparison);
		assertThat(isSimmetry, describedAs("that sgn(compare(x, y)) == -sgn(compare(y, x)) for all x and y. The simmetry has to be granted for every x and y",
				is(equalTo(true))));
	}
	
	/**
	 * the implementor must ensure that compare(x, y)==0 implies that sgn(compare(x, z))==sgn(compare(y, z)) for all z.
	 * */
	@Test
	public void implicationTest() {
		ShippingpointEntity sp1 = new ShippingpointEntity();
		sp1.setShippingpointName("Pt");
		
		ShippingpointEntity sp2 = new ShippingpointEntity();
		sp2.setShippingpointName("Pt");
		
		ShippingpointEntity sp3 = new ShippingpointEntity();
		sp3.setShippingpointName("Pt Exp Gare Lessines");
		
		Comparator<ShippingpointEntity> comp = new ShippingpointComparator();
		
		double oneAndThreeSpComparison = Math.signum(comp.compare(sp1, sp3));
		double twoAndThreeSpComparison = Math.signum(comp.compare(sp2, sp3));
		
		boolean isImplicable = (oneAndThreeSpComparison == twoAndThreeSpComparison);
		assertThat(isImplicable, describedAs("compare(x, y)==0 implies that sgn(compare(x, z))==sgn(compare(y, z)) for all z. This rule has to be true for every Z: ",
				is(equalTo(true))));
	}
	
	@Test
	public void compareIfBothNameIsNull() {
		ShippingpointEntity sp1 = new ShippingpointEntity();
		sp1.setShippingpointName(null);
		
		ShippingpointEntity sp2 = new ShippingpointEntity();
		sp2.setShippingpointName(null);
		
		Comparator<ShippingpointEntity> comp = new ShippingpointComparator();
		
		assertThat(
				comp.compare(sp1, sp2),
				describedAs("The 2 object is equals and the result has to be :",
						is(equalTo(SHIPTOS_EQUALS)))); 
	}
}
