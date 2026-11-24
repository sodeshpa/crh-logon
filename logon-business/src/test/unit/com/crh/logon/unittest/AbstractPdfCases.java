/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.unittest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import com.crh.logon.pdftool.renderer.AbstractPdfRenderer;
import com.crh.logon.pdftool.store.AbstractPdfDataStore;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import com.crh.logon.business.services.pdftools.layout.DeliveryNoteData;
import com.crh.logon.business.services.pdftools.layout.DeliveryNoteLabel;
import com.crh.logon.business.services.pdftools.layout.JcanPdfRenderer;

/**
 * @author AdNovum Informatik AG
 */
public class AbstractPdfCases {
	
	protected MockPdfDataStore store;
	protected String fileName = null;
	protected AbstractPdfRenderer renderer;
	private ByteArrayOutputStream pdfOutStream;
	private ByteArrayOutputStream psOutStream;
	
	public static void fillStoreWithBasicData(AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store) {
		// Add common initial data for delivery note testing
		store.addData(DeliveryNoteData.DEST_ADDRESS1, "missing address name");
		store.addData(DeliveryNoteData.DEST_ADDRESS2, "missing address street");
		store.addData(DeliveryNoteData.DEST_ADDRESS3, "missing address countrycity");
		store.addData(DeliveryNoteData.DEST_FAX, "missing line fax");
		store.addData(DeliveryNoteData.DEST_TAX, "missing line tax");
		store.addData(DeliveryNoteData.DEST_TEL, "missing line phone");
		store.addData(DeliveryNoteData.DEST_EMAIL, "missing line email");
		store.addData(DeliveryNoteData.ORDER_NR, "10000264");
		store.addData(DeliveryNoteData.DISTANCE, "200");
		store.addData(DeliveryNoteData.DELIVERY_WINDOW, "32-43");
		store.addData(DeliveryNoteData.SHIPTO_SAP, "recsap");
		store.addData(DeliveryNoteData.SHIPTO_ADDRESS1, "recadd1");
		store.addData(DeliveryNoteData.SHIPTO_ADDRESS2, "recadd2");
		store.addData(DeliveryNoteData.SHIPTO_ADDRESS3, "recadd3");
		store.addData(DeliveryNoteData.SOLDTO_SAP, "clientSAP");
		store.addData(DeliveryNoteData.SOLDTO_ADDRESS1, "add1add1add1add1add1add1add1add1add1");
		store.addData(DeliveryNoteData.SOLDTO_ADDRESS2, "add2");
		store.addData(DeliveryNoteData.SOLDTO_ADDRESS3, "add3");
		store.addData(DeliveryNoteData.SOLDTO_PHONE, "phone");
		store.addData(DeliveryNoteData.SOLDTO_VATREG, "vatreg");
		store.addData(DeliveryNoteData.PAYER_SAP, "payerSAP");
		store.addData(DeliveryNoteData.PAYER_ADDRESS1, "recadd1");
		store.addData(DeliveryNoteData.PAYER_ADDRESS2, "recadd2");
		store.addData(DeliveryNoteData.PAYER_ADDRESS3, "recadd3");
		store.addData(DeliveryNoteData.BILLTO_SAP, "billToSap");
		store.addData(DeliveryNoteData.BILLTO_ADDRESS1, "recadd1");
		store.addData(DeliveryNoteData.BILLTO_ADDRESS2, "recadd2");
		store.addData(DeliveryNoteData.BILLTO_ADDRESS3, "recadd3");
		store.addData(DeliveryNoteData.COMM_CARRIER_ADDRESS1, "ALFONS GRETEN BETONWERK GMBH & CO. KG  ");
		store.addData(DeliveryNoteData.COMM_CARRIER_ADDRESS2, "recadd2");
		store.addData(DeliveryNoteData.COMM_CARRIER_ADDRESS3, "recadd3");
		store.addData(DeliveryNoteData.COMM_CARRIER_PHONE, "phone");
		store.addData(DeliveryNoteData.COMM_CARRIER_PIVA_IA, "piva");
		store.addData(DeliveryNoteData.EXEC_CARRIER_ADDRESS1, "Wolfgang Matthiessen GmbH & Co KG.,   ");
		store.addData(DeliveryNoteData.EXEC_CARRIER_ADDRESS2, "recadd2");
		store.addData(DeliveryNoteData.EXEC_CARRIER_ADDRESS3, "recadd3");
		store.addData(DeliveryNoteData.EXEC_CARRIER_PHONE, "phone");
		store.addData(DeliveryNoteData.EXEC_CARRIER_PIVA_IA, "piva");
		store.addData(DeliveryNoteData.BANNER_IMG_NAME, "test");
		store.addData(DeliveryNoteData.CRH_LOGO, "test");
		store.addData(DeliveryNoteData.TRUCK_PLATE_NR, "truck");
		store.addData(DeliveryNoteData.WAGON_NR, "wagon");
		store.addData(DeliveryNoteData.CUST_REF_CONTRACT_NUMBER_TILE, "Custorder");
		store.addData(DeliveryNoteData.CUST_REF_CONTRACT_NUMBER_DATA, "Data");
		store.addData(DeliveryNoteData.INCOTERM, "incoTest");
		store.addData(DeliveryNoteData.DELIVERY_TYPE, "delType");
		store.addData(DeliveryNoteData.CONTAINER_NR, "cont nr.");
		store.addData(DeliveryNoteData.PLANT, "plantData");
		store.addData(DeliveryNoteData.SHIPPING_POINT, "shipping point");
		store.addData(DeliveryNoteData.LOAD_DATE, "2012.07.20");
		store.addData(DeliveryNoteData.DELIVERY_DATE, "2012.07.20");

	}
	
	@BeforeClass
	public static void init() {
		// Uncomment to show layout lines
		/*
		System.setProperty("ch.nevis.jcan.print.DebugCurPos", "true");
		System.setProperty("ch.nevis.jcan.print.DebugMargins", "true");
		System.setProperty("ch.nevis.jcan.print.layout.DebugBorder", "true");
		System.setProperty("ch.nevis.jcan.print.layout.DebugDisplacement", "true");
		*/
	}
	
	@Before
	public void prepareStoreAndRenderer() {
		pdfOutStream = new ByteArrayOutputStream();
		psOutStream = new ByteArrayOutputStream();
		renderer = new JcanPdfRenderer(pdfOutStream, psOutStream);
		// initialize store
		store = new MockPdfDataStore(Locale.ENGLISH);
		fillStoreWithBasicData(store);
	}
	
	@After
	public void createResultFile() throws IOException {
		if (fileName != null) {
			File pdfFile = new File("build/test/unit/pdfs/" + fileName + ".pdf");
			// create directory structure
			pdfFile.getParentFile().mkdirs();
			FileOutputStream ostream = new FileOutputStream(pdfFile);
			ostream.write(pdfOutStream.toByteArray());			
		}
		fileName = null;
	}

}
