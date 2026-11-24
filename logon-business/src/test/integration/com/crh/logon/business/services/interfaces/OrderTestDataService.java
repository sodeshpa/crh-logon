package com.crh.logon.business.services.interfaces;

import com.crh.logon.persistence.entities.common.OrderEntity;

public interface OrderTestDataService {

	/**
	 * Creates dummy test order to test transfer delivery document generation
	 */
	OrderEntity createOrderForTransferDeliveryDocumentGenerationTesting();

}