/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.orderdetail.dto;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * @author AdNovum Informatik AG
 */
public class BimodalTruckOrderItemDTOTest {

	@Test
	public void testWeightRepresentation() {
		String truckName = "mytruck";
		Long orderId = 1L;
		Long orderNumber = 1L;
		boolean authorized = false;
		
		BigDecimal netQuantity = new BigDecimal("12.47");
		String uomTxt = " krumpli";
		BimodalTruckOrderItemDTO dto 
			= new BimodalTruckOrderItemDTO(truckName , orderId , orderNumber , authorized , netQuantity , uomTxt);

		Assert.assertEquals(String.format("%s %s", netQuantity.toPlainString(), uomTxt), dto.getWeightRepresentation());
	}

	@Test
	public void testWeightRepresentationMissingQuantity() {
		String truckName = "mytruck";
		Long orderId = 1L;
		Long orderNumber = 1L;
		boolean authorized = false;
		
		BigDecimal netQuantity = null;
		String uomTxt = " krumpli";
		BimodalTruckOrderItemDTO dto 
			= new BimodalTruckOrderItemDTO(truckName , orderId , orderNumber , authorized , netQuantity , uomTxt);

		Assert.assertEquals("", dto.getWeightRepresentation());
	}

}
