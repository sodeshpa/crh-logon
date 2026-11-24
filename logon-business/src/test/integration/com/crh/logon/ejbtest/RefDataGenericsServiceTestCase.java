/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.ejbtest;

import java.util.List;
import java.util.Locale;

import com.crh.logon.constants.Constant;
import org.junit.Test;

import ch.nevis.gdi.jpa.filter.Filter;

import com.crh.logon.business.services.interfaces.RefDataGenericsService;
import com.crh.logon.persistence.entities.refdata.LangTxEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusTxEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;

/**
 * @author AdNovum Informatik AG
 */
public class RefDataGenericsServiceTestCase extends JunitSupport {
	private static final int EXCESS_SKIPPING = 5;

	private static final int RECORDS_TO_SKIP = 5;

	private static final int FIRST_INDEX = 0;

	private static final int RECORD_COUNT = 3;

	final static int NUMBER_OF_ORDER_STATUSES = 21;

	/**
	 * hr, sr, de, en, fr, it, es, cs ,sk, hu, ro, bg, ru, pl, az
	 */
	final static int NUMBER_OF_LANGUAGES = 15;

	private static final Integer NO_RECORD_COUNT = null;

	private static final Filter<OrderStatusBsEntity> DO_NOT_FILTER = null;

	private static final int DO_NOT_SKIP = 0;

	final RefDataGenericsService service = serviceLocator.getRefDataGenericsService();
	
	public RefDataGenericsServiceTestCase(String testCaseName) {
		super(testCaseName);
	}
	
	/**
	 * Test that we can load a BsEntity by passing a code (Long), and that the returned entity
	 * indeed has the right code.
	 */
	@Test
	public void testFindBsEntityByLongCd() {
		final OrderStatusBsEntity orderStatusNew = service.findBsEntity(OrderStatusBsEntity.class,
				Constant.ORDER_STATUS_NEW);
		
		assertNotNull(orderStatusNew);
		Long orderStatusCodeNew = orderStatusNew.getOrderStatusCd();
		assertEquals("Loaded order status entity has wrong status code!",
				Constant.ORDER_STATUS_NEW, orderStatusCodeNew.longValue());
	}
	
	/**
	 * Test that we can load a BsEntity by passing a code (String), and that the returned entity
	 * indeed has the right code.
	 */
	@Test
	public void testFindBsEntityByStringCd() {
		final UomBsEntity unitOfMeasure = service.findBsEntity(UomBsEntity.class,
				Constant.UOM_TONS);
		
		assertNotNull(unitOfMeasure);
		assertEquals("Loaded UOM entity has wrong status code!",
				Constant.UOM_TONS, unitOfMeasure.getUomCd());
	}
	
	/**
	 * Test that we can load a TxEntity by passing a BsEntity and a Locale, and that the returned
	 * TxEntity contains the expected text.
	 */
	@Test
	public void testFindTxEntityByBsEntity() {
		final OrderStatusBsEntity transmittedToCarrier = 
				service.findBsEntity(OrderStatusBsEntity.class,
				Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER);
		final Locale locale = new Locale("de");
		
		assertNotNull("OrderStatusBsEntity unexpectedly not found", transmittedToCarrier);
		
		final OrderStatusTxEntity transmittedToCarrierText = service.findTxEntity(
				OrderStatusTxEntity.class, locale, transmittedToCarrier);
		
		assertNotNull("Expected order status TX entity not found", transmittedToCarrierText);
		
		final String EXPECTED_TXT = "An komm. Transporteur \u00fcbermittelt";
		assertEquals("Unexpected reference data text. Ensure the \"expected\" text here is " +
				"what's in the DB.", EXPECTED_TXT, transmittedToCarrierText.getTxt());
	}
	
	/**
	 * Test that a TxEntity with by a Long primary key is loaded correctly.
	 */
	@Test
	public void testFindTxEntityByLongPrimaryKey() {
		OrderStatusTxEntity orderStatusNew = service.findTxEntity(OrderStatusTxEntity.class,
				new Locale("en"),
				Constant.ORDER_STATUS_NEW);
		assertEquals("Unexpected order status text", "New", orderStatusNew.getTxt());
	}
	
	/**
	 * Test that a TxEntity with by a String primary key is loaded correctly.
	 */
	@Test
	public void testFindTxEntityByStringPrimaryKey() {
		UomTxEntity tonsInEnglish = service.findTxEntity(UomTxEntity.class, new Locale("en"),
				Constant.UOM_TONS);
		assertEquals("Unexpected UOM text", "tons", tonsInEnglish.getTxt());
	}	
	
	/**
	 * Test that we can load a list of BsEntities, and that the list has a reasonable size.
	 */
	@Test
	public void testFindBsEntityList() {
		final List<OrderStatusBsEntity> orderStatuses;
		orderStatuses= service.findBsEntityList(OrderStatusBsEntity.class);
		assertEquals(orderStatuses.size(), NUMBER_OF_ORDER_STATUSES);
	}	
	/**
	 * Test that we can load a list of TxEntities, and that the list has a reasonable size.
	 */
	@Test
	public void testFindTxEntityList() {
		final Locale locale = new Locale("en");
		final List<OrderStatusTxEntity> orderStatusTexts;
		orderStatusTexts= service.findTxEntityList(OrderStatusTxEntity.class, locale);
		assertEquals(orderStatusTexts.size(), NUMBER_OF_ORDER_STATUSES);
	}
	
	/**
	 * Test that the LangTx entities are correctly returned.
	 */
	@Test
	public void testLangTxEntityList() {
		Locale englishLocale = new Locale("en");
		final List<LangTxEntity> translationsOfEnglishLanguageName = service.findLangTxEntityList(englishLocale);
		assertTrue("Unexpected number of LangTxEntities returned",
				NUMBER_OF_LANGUAGES <=
				translationsOfEnglishLanguageName.size());
	}

	@Test
	public void testFindBsEntityFilteredRecordSkipping() {
		List<OrderStatusBsEntity> orderStatusesSkippingSome = service.findBsEntityFiltered(
				OrderStatusBsEntity.class, DO_NOT_FILTER, NO_RECORD_COUNT, RECORDS_TO_SKIP);

		List<OrderStatusBsEntity> orderStatuesSkippingSomeMore = service.findBsEntityFiltered(
				OrderStatusBsEntity.class, DO_NOT_FILTER, NO_RECORD_COUNT, RECORDS_TO_SKIP
						+ EXCESS_SKIPPING);
		assertEquals(
				orderStatusesSkippingSome.get(FIRST_INDEX + EXCESS_SKIPPING).getOrderStatusCd(),
				orderStatuesSkippingSomeMore.get(FIRST_INDEX).getOrderStatusCd()
		);
	}

	@Test
	public void testFindBsEntityFilteredRecordCount() {
		List<OrderStatusBsEntity> orderStatuses =
				service.findBsEntityFiltered(OrderStatusBsEntity.class, DO_NOT_FILTER, RECORD_COUNT, DO_NOT_SKIP);
		assertEquals(RECORD_COUNT, orderStatuses.size());
	}

	@Test
	public void testFindBsEntityFilteredRetrieveAll() {
		List<OrderStatusBsEntity> allOrderStatus = service.findBsEntityFiltered(
				OrderStatusBsEntity.class, DO_NOT_FILTER);
		assertTrue(allOrderStatus.size() >= NUMBER_OF_ORDER_STATUSES);
	}
	
	/**
	 * This test should ensure that the findTxEntityFiltered method 
	 * works with the recordCount and skip parameters.  
	 */
	@Test
	public void testFindTxEntityFiltered() {
		Filter<LangTxEntity> filter = null;
		List<LangTxEntity> translationsOfLanguageNames = null;
		
		translationsOfLanguageNames = service.findTxEntityFiltered(LangTxEntity.class, filter);
		int totalSize = translationsOfLanguageNames.size();
		assertTrue(totalSize >= NUMBER_OF_LANGUAGES * NUMBER_OF_LANGUAGES);
		
		translationsOfLanguageNames = 
				service.findTxEntityFiltered(LangTxEntity.class, filter, RECORD_COUNT, RECORDS_TO_SKIP);
		assertEquals(RECORD_COUNT, translationsOfLanguageNames.size());

		translationsOfLanguageNames = 
				service.findTxEntityFiltered(LangTxEntity.class, filter, NO_RECORD_COUNT, RECORDS_TO_SKIP);
		assertTrue(translationsOfLanguageNames.size() < totalSize);
	}
}
