/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdftool.labelprovider;

import java.io.Serializable;

import com.crh.logon.pdftool.store.AbstractPdfDataStore;

/**
 * Marker interface for DTO objects. Used for getting back DTO objects
 * from {@link AbstractPdfDataStore#getDTOList(Class)}
 * <br />
 * <br />
 * This interface just marks an object as a DTO, for the sake of 
 * clean code. 
 * @author AdNovum Informatik AG
 */
public interface DTOMarker extends Serializable {

}
