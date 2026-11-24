/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.security.srv;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.crh.logon.persistence.dao.jdbc.PersonDAOJDBCImpl;
import com.crh.logon.persistence.dao.to.PersonValue;
import com.crh.logon.persistence.dao.util.ColumnRestriction;
import com.crh.logon.persistence.dao.util.DAOException;
import com.crh.logon.persistence.dao.util.DAOExpression;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class PersonServiceManager extends ServiceManagerBase {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PersonServiceManager.class);
	
    /**
     * Constructor. Visible only in the package.
     * NOTE: No public constructor for this manager
     * 
     * @param conn
     * 			A connection (session) with a specific database. (Cannot be null).
     */
    protected PersonServiceManager(Connection conn) {
    	super(conn);
		// [[
		Object[][] params = {{"conn", conn}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "PersonServiceManager", params);
		}
		// ]]
		try {
			; // Avoid PMD
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }
	
	/**
	 * Load the person ({@link PersonValue}) information for the given <code>personId</code>.
	 * 
	 * @param personId
	 * @return
	 * @throws DAOException
	 */
	public PersonValue getPersonValueByPersonId(Long personId) throws DAOException {
		// [[
		PersonValue result = null;
		Object[][] params = {{"personId", personId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getPersonValueByPersonId", params);
		}
		// ]]
		try {
			if (personId == null) {
				String errorMsg = "The 'personId' parameter cannot be null!";
				LOG.error(errorMsg);
				throw new IllegalArgumentException(errorMsg);
			}
			
			Map<String, ColumnRestriction> restriction = new HashMap<String, ColumnRestriction>();
			ColumnRestriction columnRestriction = new ColumnRestriction(personId, PersonValue.CN_NAME_PERSON_ID, DAOExpression.EQUAL);
			restriction.put(PersonValue.CN_NAME_PERSON_ID, columnRestriction);
			
			PersonDAOJDBCImpl dao = new PersonDAOJDBCImpl(getConnection());
			List<PersonValue> usersList = dao.findRecords((short)0, (short)0, restriction);
			
			if (usersList == null || usersList.isEmpty()) {
				return result;
			}
			
			if (usersList.size() > 1) {
				String errorMsg = "More than one person found with personId: '" + personId + "'!";
				LOG.error(errorMsg);
				throw new DAOException(errorMsg);				
			}
			
			return result = usersList.get(0);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

}
