/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.unittest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.crh.logon.business.services.pdftools.layout.DeliveryNoteData;
import com.crh.logon.business.services.pdftools.layout.DeliveryNoteLabel;
import com.crh.logon.business.services.pdftools.layout.OrderItemDTO;
import com.crh.logon.business.services.pdftools.layout.colldelnote.CollDelNoteMainLayout;
import com.crh.logon.business.services.pdftools.layout.colldelnote.OrderItemTotalDTO;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.pdftool.store.AbstractPdfDataStore;

/**
 * @author AdNovum Informatik AG
 */
public class CollDeliveryNoteTest extends AbstractPdfCases {
	
	public static void fillStoreWithBasicCollData(AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store) {
		// basic collective delivery note specific initialization
		store.addLabel(DeliveryNoteLabel.ORDER_NUMBER, Message.lookup(Locale.ENGLISH, "pdf.coll.orderno"));
	}
	
	@Before
	public void initCollDelNoteData() {
		// parent already initialized store
		fillStoreWithBasicCollData(store);
	}
	
	@Test
	public void createCollDeliveryNote() throws IOException {
		store.addData(DeliveryNoteData.CUST_REF_CONTRACT_NUMBER_TILE, "Label");
		store.addData(DeliveryNoteData.CUST_REF_CONTRACT_NUMBER_DATA, "custRefData");
		CollDelNoteMainLayout.generateCollectiveDeliveryNote(store, renderer, null);
		fileName = "coll1";
	}
	
	@Test
	public void createCollDeliveryNote2() throws IOException {
		OrderItemDTO dto = new OrderItemDTO("00423443241", "123456789101", "name name name name name a", "long long long long long long description", "numbernumb", "2011.23.21", "32323", BigDecimal.TEN, "Bag 23kg2", "mix-design", "prod-plant");
		store.getDTOList(OrderItemDTO.class).add(dto);
		OrderItemTotalDTO totalDto = new OrderItemTotalDTO("000234", "Phys1", BigDecimal.valueOf(23), "E", true);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Serv1", BigDecimal.valueOf(23), "Erwe pack12", false);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("0002344234324234234234111111111", "Phys1dsfsdfsdfdfdffddddddddssssssssf fdsf fdsfdsf fsdf df sdf    fdfdsfsdff  dsfdfdf     fdsfdsf  dfsdf", BigDecimal.valueOf(23), "E", true);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Serv1", BigDecimal.valueOf(23), "E", false);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Phys1", BigDecimal.valueOf(23), "E", true);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Serv1", BigDecimal.valueOf(23), "E", false);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Phys1", BigDecimal.valueOf(23), "E", true);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Serv1", BigDecimal.valueOf(23), "E", false);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Phys1", BigDecimal.valueOf(23), "E", true);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Serv1", BigDecimal.valueOf(23), "E", false);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Phys1", BigDecimal.valueOf(23), "E", true);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Serv1", BigDecimal.valueOf(23), "E", false);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Phys1", BigDecimal.valueOf(23), "E", true);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Serv1", BigDecimal.valueOf(23), "E", false);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Phys1", BigDecimal.valueOf(23), "E", true);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Serv1", BigDecimal.valueOf(23), "E", false);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Phys1", BigDecimal.valueOf(23), "E", true);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Serv1", BigDecimal.valueOf(23), "E", false);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Phys1", BigDecimal.valueOf(23), "E", true);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Serv1", BigDecimal.valueOf(23), "E", false);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Phys1", BigDecimal.valueOf(23), "E", true);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Phys1", BigDecimal.valueOf(23), "E", true);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Phys1", BigDecimal.valueOf(23), "E", true);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Phys1", BigDecimal.valueOf(23), "E", true);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Phys1", BigDecimal.valueOf(23), "E", true);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Phys1", BigDecimal.valueOf(23), "E", true);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Phys1", BigDecimal.valueOf(23), "E", true);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Phys1", BigDecimal.valueOf(23), "E", true);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Phys1", BigDecimal.valueOf(23), "E", true);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Phys1", BigDecimal.valueOf(23), "E", true);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Phys1", BigDecimal.valueOf(23), "E", true);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Serv1", BigDecimal.valueOf(23), "E", false);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Serv1", BigDecimal.valueOf(23), "E", false);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Serv1", BigDecimal.valueOf(23), "E", false);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Serv1", BigDecimal.valueOf(23), "E", false);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Serv1", BigDecimal.valueOf(23), "E", false);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Serv1", BigDecimal.valueOf(23), "E", false);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Serv1", BigDecimal.valueOf(23), "E", false);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Serv1", BigDecimal.valueOf(23), "E", false);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		totalDto = new OrderItemTotalDTO("000234", "Serv1", BigDecimal.valueOf(23), "E", false);
		store.getDTOList(OrderItemTotalDTO.class).add(totalDto);
		
		store.addData(DeliveryNoteData.WAITING_TIME, "4 hrs.");
		
		CollDelNoteMainLayout.generateCollectiveDeliveryNote(store, renderer, null);
		fileName = "coll2";
	}

}
