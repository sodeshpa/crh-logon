/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.crh.logon.business.services.beans.UomConversionResult;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.CustorderItemEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;

/**
 * @author AdNovum Informatik AG
 */
public class AdjustTimesUtilsDeliveryToCaseTest extends AdjustTimesUtilsTest {

	@Before
	public void setUp() {
		super.setUp();
	}

	@Test
	public void testDeliveryToIsImportantFromLRQ() {
		
		initializeShipTo("07:00", "12:00", "13:00", "17:00");

		String[][] deliveryWindows = new String[][] { { "10:45", "11:15" }, { "11:21", "11:51" },
				{ "11:57", "12:27" }, { "12:33", "13:03" }, { "13:09", "13:39" } };

		String[][] expectedDeliveryWindows = new String[][] { { "10:45", "11:15" },
				{ "11:21", "11:51" }, { "13:00", "13:27" }, { "13:33", "14:03" } };

		double[] initialQtys = new double[] { 30, 30, 30, 30, 30 };
		double[] expectedQtys = new double[] { 30, 30, 30, 10 };

		BigDecimal deliveryFreqQtyperhre = new BigDecimal("50");
		
		String[] custorderDeliveryWindow = new String[] { "11:00", "14:00" };
		adjustDeliveryWindowForShipTo(deliveryWindows, expectedDeliveryWindows, initialQtys, expectedQtys, custorderDeliveryWindow, deliveryFreqQtyperhre, 15D);

	}
	
	@Test
	public void testDeliveryToIsImportantShipToOpensLater() {
		
		initializeShipTo("10:00", "12:00", "13:00", "17:00");

		// 09:15, 10:15, 11:15
		String[][] deliveryWindows = new String[][] { 
				{ "09:00", "09:30" }, 
				{ "10:00", "10:30" },
				{ "11:00", "11:30" }
		};

		String[][] expectedDeliveryWindows = new String[][] {
				{ "10:00", "10:30" },
				{ "11:00", "11:30" }				
		};

		double[] initialQtys = new double[] { 30, 30, 30 };
		double[] expectedQtys = new double[] { 30, 30 };
		
		BigDecimal deliveryFreqQtyperhre = new BigDecimal("50");

		adjustDeliveryWindowForShipTo(deliveryWindows, expectedDeliveryWindows, initialQtys, expectedQtys, new String[] { "11:00",
						"14:00" }, deliveryFreqQtyperhre, 15D);

	}
	
	@Test
	public void testDeliveryToIsImportantShipToClosedAlready() {
		
		initializeShipTo("10:00", "12:00", "13:00", "16:00");

		// 14:15, 15:15, 15:15, 16:15, 17:15
		String[][] deliveryWindows = new String[][] { 
				{ "14:00", "14:30" }, 
				{ "15:00", "15:30" },
				{ "16:00", "16:30" },
				{ "17:00", "17:30" }
		};

		String[][] expectedDeliveryWindows = new String[][] {
				{ "14:00", "14:30" },
				{ "15:00", "15:30" }
		};

		double[] initialQtys = new double[] { 30, 30, 30, 30 };
		double[] expectedQtys = new double[] { 30, 22.5D };

		String[] custorderDeliveryWindow = new String[] { "14:15", "17:15" };
		
		BigDecimal deliveryFreqQtyperhre = new BigDecimal("30");
		
		adjustDeliveryWindowForShipTo(deliveryWindows, expectedDeliveryWindows, initialQtys, expectedQtys, custorderDeliveryWindow, deliveryFreqQtyperhre, 15D);

	}
	
	@Test
	public void testDeliveryToIsImportantShipToOpensLaterIntersect() {
		
		initializeShipTo("10:00", "12:00", "13:00", "17:00");

		String[][] deliveryWindows = new String[][] { 
				{ "09:45", "10:15" }, 
				{ "10:45", "11:15" }
		};

		String[][] expectedDeliveryWindows = new String[][] {
				{ "10:00", "10:15" },
				{ "10:45", "11:15" }
		};

		double[] initialQtys = new double[] { 30, 30 };
		double[] expectedQtys = new double[] { 30, 30 };
		
		BigDecimal deliveryFreqQtyperhre = new BigDecimal("50");

		adjustDeliveryWindowForShipTo(deliveryWindows, expectedDeliveryWindows, initialQtys, expectedQtys, new String[] { "11:00", "14:00" }, deliveryFreqQtyperhre, 15D);

	}
	
	@Test
	public void testDeliveryToIsImportantApplyToNotChangedSet() {
		
		initializeShipTo("07:00", "12:00", "13:00", "17:00");

		String[][] deliveryWindows = new String[][] { 
				{ "07:00", "07:00" }, 
				{ "08:00", "08:00" },
				{ "09:00", "09:00" }
		};

		String[][] expectedDeliveryWindows = new String[][] {
				{ "07:00", "07:00" }, 
				{ "08:00", "08:00" },
				{ "09:00", "09:00" }
		};

		double[] initialQtys = new double[] { 10, 10, 10 };
		double[] expectedQtys = new double[] { 10, 10, 10 };
		
		BigDecimal deliveryFreqQtyperhre = new BigDecimal("10");

		String[] custorderDeliveryWindow = new String[] { "07:00", "10:00" };
		
		double deliveryToleranceMin = 0D;
		
		adjustDeliveryWindowForShipTo(deliveryWindows, expectedDeliveryWindows, initialQtys, expectedQtys, custorderDeliveryWindow, deliveryFreqQtyperhre, deliveryToleranceMin);

	}
	
	@Test
	public void testDeliveryToIsImportantShipToClosedAlreadyIntersect() {
		
		initializeShipTo("10:00", "12:00", "13:00", "17:00");

		String[][] deliveryWindows = new String[][] { 
				{ "15:45", "16:15" }, 
				{ "16:45", "17:15" }
		};

		String[][] expectedDeliveryWindows = new String[][] {
				{ "15:45", "16:15" },
		};

		double[] initialQtys = new double[] { 30, 30 };
		double[] expectedQtys = new double[] { 30 };

		String[] custorderDeliveryWindow = new String[] { "16:00", "17:00" };
		
		BigDecimal deliveryFreqQtyperhre = new BigDecimal("30");
		
		adjustDeliveryWindowForShipTo(deliveryWindows, expectedDeliveryWindows, initialQtys, expectedQtys, custorderDeliveryWindow, deliveryFreqQtyperhre, 15D);

	}

	/**
	 * @param deliveryWindows
	 * @param expectedDeliveryWindows
	 * @param initialQtys
	 * @param expectedQtys
	 * @param custorderDeliveryWindowArray - will be used to calculate the custorder delivery window; form: String[] { "11:00", "14:00" }
	 * @param deliveryToleranceMin TODO
	 */
	public void adjustDeliveryWindowForShipTo(String[][] deliveryWindows,
			String[][] expectedDeliveryWindows, double[] initialQtys, double[] expectedQtys, String[] custorderDeliveryWindowArray, BigDecimal deliveryFreqQtyperhre, double deliveryToleranceMin) {
		UomBsEntity orderedUom = new UomBsEntity();
		orderedUom.setUomCd("TON");
		
		CustorderEntity custorder = createBulkCustorderWithSDOs(deliveryWindows, initialQtys);
		custorder.setDeliveryFreqQtyperhre(deliveryFreqQtyperhre);
		custorder.setDeliveryFreqQtyperhreUom(orderedUom);
		custorder.setDeliveryTimeTolerance(deliveryToleranceMin);
		custorder.setDeliveryTimeToleranceUom("MIN");

		Calendar[] custorderDeliveryWindow = createCalDeliveryWindow(custorderDeliveryWindowArray);

		Date custorderFrom = custorderDeliveryWindow[0].getTime();
		Date custorderTo = custorderDeliveryWindow[1].getTime();

		custorder.setDeliveryFromDat(custorderFrom);
		custorder.setDeliveryToDat(custorderTo);

		BigDecimal perSdorderQuantity = new BigDecimal("30");
		
		CustorderItemEntity custorderItem = new CustorderItemEntity();
		custorderItem.setExactCustorderQtyFlag(Boolean.FALSE);
		custorderItem.setPerSdorderQuantity(perSdorderQuantity);
		custorderItem.setOrderedUom(orderedUom);

		List<CustorderItemEntity> custorderItemEntities = new ArrayList<CustorderItemEntity>();
		custorderItemEntities.add(custorderItem);

		custorder.setCustorderItems(custorderItemEntities);

		UomConversionResult conversionResult = mock(UomConversionResult.class);
		when(conversionResult.getQuantity()).thenReturn(deliveryFreqQtyperhre);
		
		CustorderSplitWrapper wrapper = mock(CustorderSplitWrapper.class);
		when(wrapper.convertQuantity(Mockito.any(SapMaterialEntity.class), Mockito.anyString(), Mockito.anyString(), Mockito.any(BigDecimal.class))).thenReturn(conversionResult);
		when(wrapper.get_perSdoQtyInOrderedUom()).thenReturn(perSdorderQuantity);
		
		List<SplittedOrderData> splittedOrderData = AdjustTimesUtils.adjustTimesDeliveryTo(opening, custorder, shiptoEntity, wrapper);

		Assert.assertEquals(expectedDeliveryWindows.length, splittedOrderData.size());
		Assert.assertEquals(expectedDeliveryWindows.length, custorder.getOrders().size());

		for (int i = 0; i < expectedDeliveryWindows.length; i++) {
			
			SplittedOrderData orderData = splittedOrderData.get(i);
			
			OrderEntity order = orderData.getOrder();
			
			Calendar[] expectedDeliveryWindow = createCalDeliveryWindow(expectedDeliveryWindows[i]);
			assertOrderIsSameAsDeliveryWindow(order, expectedDeliveryWindow);
			assertBulkOrderHasQtyOf(order, expectedQtys[i]);
			Assert.assertEquals(expectedDeliveryWindows[i][0],orderData.getDeliveryFromTime());
			Assert.assertEquals(expectedDeliveryWindows[i][1],orderData.getDeliveryToTime());
		}
	}
	
	@Test
	public void testSelectFreqModuloQtyPerHre() {
		
		BigDecimal deliveryFreqQtyperhre = new BigDecimal("13.45");
		
		CustorderEntity custorder = new CustorderEntity();
		custorder.setDeliveryFreqQtyperhre(deliveryFreqQtyperhre);
		
		BigDecimal selectFreqModulo = AdjustTimesUtils.selectFreqDivisorForDeliveryToCase(custorder, null);
		Assert.assertEquals(deliveryFreqQtyperhre, selectFreqModulo);
		
	}
	
	@Test
	public void testSelectFreqModuloEveryXMinutes() {
		 assertFreqDivisorCorrect("12", Constant.UOM_MIN, "11", 55D);
		 assertFreqDivisorCorrect("5", Constant.UOM_MIN, "10", 120D);
	}
	
	@Test
	public void testSelectFreqModuloEveryXHour() {
		 assertFreqDivisorCorrect("2", Constant.UOM_HRE, "11", 5.5D);
		 assertFreqDivisorCorrect("2", Constant.UOM_HRE, "1", 0.5D);
		 assertFreqDivisorCorrect("0.5", Constant.UOM_HRE, "1", 2.0D);
	}
	
	@Test(expected=IllegalStateException.class)
	public void testSelectFreqModuloDeliveryToIllegalState() {
		CustorderEntity custorder = new CustorderEntity();
		AdjustTimesUtils.selectFreqDivisorForDeliveryToCase(custorder, null);
	}
	
	private void assertFreqDivisorCorrect(String deliveryFreqEveryTimeStr, String uom, String sdoQty, double expectedDivisor) {
		
		Double deliveryFreqEveryTime = new Double(deliveryFreqEveryTimeStr);
		
		CustorderEntity custorder = new CustorderEntity();
		custorder.setDeliveryFreqEveryTime(deliveryFreqEveryTime);
		custorder.setDeliveryFreqEveryTimeUom(uom);
		
		BigDecimal perSdoOrderQtyInOrderedUom = new BigDecimal(sdoQty);
		BigDecimal selectFreqModulo = AdjustTimesUtils.selectFreqDivisorForDeliveryToCase(custorder, perSdoOrderQtyInOrderedUom);
		 Assert.assertEquals(expectedDivisor, selectFreqModulo.doubleValue());
		 
	}

}