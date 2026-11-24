/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package ch.nevis.gdi.jpa;

import javax.persistence.EntityManager;

import ch.nevis.gdi.jpa.filter.Expression;
import ch.nevis.gdi.jpa.filter.Filter;

/**
 * @author AdNovum Informatik AG
 */
public interface Authorizer {
	
	Expression getRestriction(EntityManager em, Filter filter, String clazzname, String identificationVariable);
	
}
