/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdftool.labelprovider;

import com.crh.logon.pdftool.objects.PdfObject;
import com.crh.logon.pdftool.objects.PdfTable;
import com.crh.logon.pdftool.store.AbstractPdfDataStore;

/**
 * Used for providing data for a {@link PdfTable}. For each input object and row a table has, the
 * {@link TableLabelProvider#getTextObject(Object, int)} will be called. Hence the
 * special label providers are the only classes for the table which have references to
 * business object.<br />
 * Note that if heavy service calls occur, information from the objects should be cached
 * to avoid unnecessary load.
 * <br />
 * <br />
 * Recommended use: build DTOMarker objects in {@link AbstractPdfDataStore} and process
 * only these DTOs in the actual label provider. This allows the solution to be testable
 * from unit tests without any direct dependencies to business logic other than the
 * {@link AbstractPdfDataStore} implementation.
 *  
 * @author AdNovum Informatik AG
 */
public interface TableLabelProvider {
	
	public PdfObject getTextObject(Object object, int column);

}
