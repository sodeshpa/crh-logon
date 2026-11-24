/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdf.templating;

import java.io.IOException;

/**
 * Returns values of a (possible pseudo) tree-like data structure. 
 */
public interface DataProvider {

	public abstract DocumentValue getInclude(Path path) throws IOException;

	/**
	 * Returns the value belonging to a key.
	 */
	public abstract Value getValue(Path path);

	/**
	 * Returns the number of array items of a given path exist.
	 */
	public abstract int count(Path path);
	
}