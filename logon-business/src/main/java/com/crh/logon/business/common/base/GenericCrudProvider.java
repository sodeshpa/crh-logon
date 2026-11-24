/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.business.common.base;

import ch.nevis.gdi.jpa.BaseEntity;

public abstract class GenericCrudProvider<T extends BaseEntity> extends GenericReadOnlyProvider<T> implements GenericCrudProviderInterface<T> {  
	  
    public void create(T persistableEntity) {  
    	em.persist(persistableEntity);
    }  
  
    public T update(T mergableEntity) {  
    	return em.merge(mergableEntity);         
    }
    
    public void delete(T removableEntity) {  
        em.remove(removableEntity);  
    }    
}  
