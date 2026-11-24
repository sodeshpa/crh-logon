package com.crh.logon.pdf.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class CollectionTool {
	private CollectionTool() {
		
	}
	
	/**
	 * Puts every i-th item of each collection in to a pair, or null if the collection has no i-th pair.
	 */
	public static <L, R> Collection<Pair<L, R>> zipMax(Collection<L> left, Collection<R> right) {
		ArrayList<Pair<L, R>> result = new ArrayList<Pair<L, R>>();
		
		
		Iterator<L> lIter = left.iterator();
		Iterator<R> rIter = right.iterator();
		
		while (lIter.hasNext() || rIter.hasNext()) {
			L l = null;
			R r = null;
			if (lIter.hasNext()) {
				l = lIter.next();
			}
			if (rIter.hasNext()) {
				r = rIter.next();
			}
			result.add(Pair.of(l, r));
		}
		
		return result;
	}
}
