/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.business.common.base;

import java.util.List;
import ch.nevis.gdi.jpa.BaseEntity;

public interface GenericReadOnlyInterface<T extends BaseEntity> {
    
    /**
     * It search for an object based on the given id (primary key)
     * @param id
     * @return T - object with the defined type
     */
    T findById(Long id);
    
    /**
     * It returns all of entities. Be careful with the usage.  
     * 
     * @return List<T> - list of objects with the defined type
     */
    List<T> fetchAll();
}
