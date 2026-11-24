/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.unittest;

import java.math.BigDecimal;
import java.util.Calendar;

import com.crh.logon.utils.time.DateTimeFormat;
import org.junit.Test;

import com.crh.logon.business.services.pdftools.layout.DeliveryNoteData;
import com.crh.logon.business.services.pdftools.layout.EichbelegDTO;
import com.crh.logon.business.services.pdftools.layout.OrderItemDTO;
import com.crh.logon.business.services.pdftools.layout.delnote.DeliveryNoteMainLayout;

/**
 * @author AdNovum Informatik AG
 */
public class DeliveryNoteTest extends AbstractPdfCases {
	
	private static final BigDecimal HUNDRED = new  BigDecimal(100);
	
	private static final String MATERIAL_DESCRIPTION = "CEM II/A-M (V-LL) 42.5 R /  SN EN 197-11";

	private static final String BAG_UOM = "pall";
	/**
	 * Create basic delivery note.
	 */
	@Test
	public void createDeliveryNote() {
		addCommonData();
		
		int matCount = 10;
		for(int i = 0; i < matCount; i++) {
			store.getDTOList(OrderItemDTO.class).add(new OrderItemDTO("4234", "ADN3434", (i+1) + ". TestMaterial", MATERIAL_DESCRIPTION, "AZX-434325", DateTimeFormat.asDayMonth(Calendar.getInstance().getTime()), DateTimeFormat.asDayMonth(Calendar.getInstance().getTime()), BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, null, "t", "mix-design", "prod-plant"));
			store.getDTOList(OrderItemDTO.class).add(new OrderItemDTO("SAPNR4343", "Plant name {landfill etc. info}"));
			store.getDTOList(OrderItemDTO.class).add(new OrderItemDTO("Net Price: 500.05 CHF", "VAT: 80.00 CHF Gross price: 1080.00 CHF"));
		}
		
		DeliveryNoteMainLayout.generateDeliveryNotePDF(store, renderer, null);
		fileName = "normalDelNote";		
	}
	
	/**
	 * LOGON-6236 Pallet item not printed in the DN. Only 8 lines allowed.
	 * Test for long material names.
	 * Material name longer than 28 characters would break into new row 
	 * resulting some material missing at the end of the table.
	 */
	@Test
	public void createDeliveryNoteWithLongMatName() {
		addCommonData();
		
		int matCount = 10;
		for(int i = 0; i < matCount; i++) {
			store.getDTOList(OrderItemDTO.class).add(
					new OrderItemDTO("4234", "ADN3434", (i + 1)
							+ "GA2 Granulato di calcestruzzo 0/32 S461",
							MATERIAL_DESCRIPTION, "AZX-434325",
							DateTimeFormat.asDayMonth(Calendar.getInstance().getTime()),
							DateTimeFormat.asDayMonth(Calendar.getInstance().getTime()),
							HUNDRED, HUNDRED, HUNDRED, null, "t", "mix-design", "prod-plant"));
			store.getDTOList(OrderItemDTO.class).add(new OrderItemDTO("SAPNR4343", "Plant name {landfill etc. info}"));
			store.getDTOList(OrderItemDTO.class).add(new OrderItemDTO("Net Price: 500.05 CHF", "VAT: 80.00 CHF Gross price: 1080.00 CHF"));
		}
		
		DeliveryNoteMainLayout.generateDeliveryNotePDF(store, renderer, null);
		fileName = "normalDelNoteLongMat";
	}
	
	/**
	 * Test for long material names with return quantity
	 * Material name longer than 28 characters would break into new row 
	 * resulting some material missing at the end of the table.
	 */
	@Test
	public void createDeliveryNoteWithLongMatNameRet() {
		addCommonData();
		
		store.addData(DeliveryNoteData.RETURN_DATA_FLAG, "true");
		
		//Did not work in the test as expected
		store.addData(DeliveryNoteData.CROSSBOUNDARY, "AdNovum-TESTDATA: Grenz�berschreitende Lieferung ZA01-CH, siehe tlgnr_text_bs/tx, pdf.deliverynote.crossboundary.ZA01CH (de)");
		
		
		int normalMatCount = 7;
		for(int i = 0; i < normalMatCount; i++) {
			store.getDTOList(OrderItemDTO.class).add(
					new OrderItemDTO("4234", "ADN3434", (i + 1)
							+ "GA2 Granulato di calcestruzzo 0/32 S461",
							MATERIAL_DESCRIPTION, "AZX-434325",
							DateTimeFormat.asDayMonth(Calendar.getInstance().getTime()),
							DateTimeFormat.asDayMonth(Calendar.getInstance().getTime()),
							HUNDRED, HUNDRED, HUNDRED, HUNDRED, "t", "mix-design", "prod-plant"));
			store.getDTOList(OrderItemDTO.class).add(new OrderItemDTO("SAPNR4343", "Plant name {landfill etc. info}"));
			store.getDTOList(OrderItemDTO.class).add(new OrderItemDTO("Net Price: 500.05 CHF", "VAT: 80.00 CHF Gross price: 1080.00 CHF"));
		}
		
		DeliveryNoteMainLayout.generateDeliveryNotePDF(store, renderer, null);
		fileName = "normalDelNoteLongMatRet";
	}
	
	/**
	 * Adds test data to the store.
	 */
	private void addCommonData() {
		store.addData(DeliveryNoteData.IS_BULK, "true");
		store.addData(DeliveryNoteData.IS_BAG, "false");
		store.addData(DeliveryNoteData.IS_CASH_SALES, "true");
		store.addData(DeliveryNoteData.ORDER_NR_ALT, "LT:ws code");
		store.addData(DeliveryNoteData.ORDER_NR_ALT2, "Logon:alt ord numb3223");
		store.addData(DeliveryNoteData.RETURN_DATA_FLAG, "false");
		
		store.addData(DeliveryNoteData.DEST_ADDRESS1, "Crh Aggregati Calcestruzzi S.r.L.");
		store.addData(DeliveryNoteData.DEST_TEL, "Tel. +39 031 616111");
		store.addData(DeliveryNoteData.DEST_ADDRESS2, "Via volta, 1");
		store.addData(DeliveryNoteData.DEST_FAX, "Fax. +39 031 616305");
		store.addData(DeliveryNoteData.DEST_ADDRESS3, "22046 Merona (CO), Italia");
		store.addData(DeliveryNoteData.DEST_TAX, "www.crh.it");
		store.addData(DeliveryNoteData.DEST_EMAIL, "giovanni.trapattoni@crh.it");
		
		store.addData(DeliveryNoteData.RETURNED_PALLETS, "3");
		store.addData(DeliveryNoteData.CHROMAT_REDUCTION_COMM, "Chromat reduction");
		store.addData(DeliveryNoteData.NHM, "25232900");
		store.addData(DeliveryNoteData.STATION_TITLE_PAR, "Kombilieferung von Bahnauftrag {0}, wagon {1}, Menge Bahnwagen Total: {2}");
		store.addData(DeliveryNoteData.COMMENT, "Multi line comment\nNewLine\nNewLine");
		store.addData(DeliveryNoteData.FOOTER_TITLE, "footer tile");
		store.addData(DeliveryNoteData.FOOTER_TEXT, "footer text");
		store.addData(DeliveryNoteData.EICHBELEG_TEXT, "Messwerte aus frei programmierbarer Zusatzeinrichtung. Diegeeichten Messwerte k�nnen eingesehen werden.");
		store.getDTOList(EichbelegDTO.class).add(new EichbelegDTO("Calibration record no: Tare measurement {0}:{1} ({2}{3})"));
		store.getDTOList(EichbelegDTO.class).add(new EichbelegDTO("Gross measurement {0}: {1} ({2}{3})"));
	}
	
	@Test
	public void createDeliveryNote2() {
		store.addData(DeliveryNoteData.COMMENT, "comment");
		DeliveryNoteMainLayout.generateDeliveryNotePDF(store, renderer, null);
		fileName = "normalDelNote2";
		
	}
	
	/**
	 * Test for long material names with return quantity
	 * Material name longer than 28 characters would break into new row 
	 * resulting some material missing at the end of the table.
	 */
	@Test
	public void createBagDeliveryNoteWithLongMatName() {
		addCommonData();
		
		store.addData(DeliveryNoteData.IS_BULK, "false");
		store.addData(DeliveryNoteData.IS_BAG, "true");
		
		int normalMatCount = 7;
		for(int i = 0; i < normalMatCount; i++) {
			store.getDTOList(OrderItemDTO.class).add(
					new OrderItemDTO("4234", "ADN3434", (i + 1)
							+ "GA2 Granulato di calcestruzzo 0/32 S461",
							MATERIAL_DESCRIPTION, "AZX-434325",
							DateTimeFormat.asDayMonth(Calendar.getInstance().getTime()),
							DateTimeFormat.asDayMonth(Calendar.getInstance().getTime()),
							HUNDRED, HUNDRED, HUNDRED, BAG_UOM, BAG_UOM, BAG_UOM, "mix-design", "prod-plant"));
			store.getDTOList(OrderItemDTO.class).add(new OrderItemDTO("SAPNR4343", "Plant name {landfill etc. info}"));
			store.getDTOList(OrderItemDTO.class).add(new OrderItemDTO("Net Price: 500.05 CHF", "VAT: 80.00 CHF Gross price: 1080.00 CHF"));
		}
		
		DeliveryNoteMainLayout.generateDeliveryNotePDF(store, renderer, null);
		fileName = "normalBagDelNoteLongMatRet";
	}
	

}
