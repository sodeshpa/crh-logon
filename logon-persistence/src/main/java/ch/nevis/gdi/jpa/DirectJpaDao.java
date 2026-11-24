/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package ch.nevis.gdi.jpa;

import java.util.List;

/**
 * @author AdNovum Informatik AG
 */
public interface DirectJpaDao {

	// gg20081211: Varargs are used to provide full flexibility for queries with varying number
	// of bind parameters in the "WHERE" statement
	
	public abstract List find(String queryString, Object... values); // NOPMD
	public abstract List findNRecs(int skip, int num, String queryString, Object... values); // NOPMD
	public abstract Object findSingle(String queryString, Object... values); // NOPMD

	public abstract List findNRecsNative(int skip, int num, String queryString, Object[] values);
	public abstract List findNative(String queryString, Object[] values);

	/**
	 * Executes a native query (not a select)
	 * @param queryString
	 * @param values
	 */
	public abstract int execNative(String queryString, Object[] values);

	public void flush();
}
