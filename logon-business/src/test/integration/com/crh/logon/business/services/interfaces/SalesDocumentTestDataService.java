/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.interfaces;

import java.util.List;

import com.crh.logon.webservices.clients.beans.deliverydocument.SalesDocumentDTO;

/**
 * @author AdNovum Informatik AG
 */
public interface SalesDocumentTestDataService {

	/**
	 * @return
	 */
	List<SalesDocumentDTO> getBagOrderSalesDocumentSampleData();

	/**
	 * For tests when no specific sales document needed.
	 */
	SalesDocumentDTO getDummySalesDocument();

}
