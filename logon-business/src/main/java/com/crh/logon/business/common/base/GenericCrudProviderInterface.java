/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.business.common.base;

import ch.nevis.gdi.jpa.BaseEntity;

public interface GenericCrudProviderInterface<T extends BaseEntity> extends GenericReadOnlyInterface<T> {
    
	/**
	 * It persist the given entity into the PersistenceContext
	 * @param persistableEntity
	 */
	void create(T persistableEntity);
	
	/**
	 * It merges the given entity into the PersistenceContext
	 * @param mergableEntity
	 */
    T update(T mergableEntity);
    
    /**
     * It removes the given entity from the PersistenceContext
     * @param removableEntity
     */
    void delete(T removableEntity);      
}
