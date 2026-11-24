/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.unittest;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;

import com.crh.logon.business.atlas.dto.LineItemDTO;
import com.crh.logon.business.atlas.dto.PackageRecordDTO;
import com.crh.logon.business.atlas.dto.TransactionHeaderDTO;
import com.crh.logon.utils.LogonException;

/**
 * @author AdNovum Informatik AG
 */
public class AtlasAssemblerTest {

	@Test
	public void BigDecimalFormatting() {
		final BigDecimal test = new BigDecimal("-44566.1230");
		int precision = test.precision();
		int scale = test.scale();
		test.setScale(5);
		String output = test.toPlainString();
		BigDecimal scaled = test.setScale(2, BigDecimal.ROUND_HALF_UP);
		output = scaled.toPlainString();
		precision = scaled.precision();
	}
	
	@Test
	public void AtlasPositive() {
		final TransactionHeaderDTO header = new TransactionHeaderDTO();
		final LineItemDTO item = new LineItemDTO();
		final PackageRecordDTO packageRecord = new PackageRecordDTO();
		
		header.setVersion("4.1");
		header.setConsignmentNumber(36);
		header.setTargetDespatchDate(new Date());
		header.setIncotermCountryIsoCode(123);
		header.setFixedTime(new Date());
		header.setReferenceValue(new BigDecimal("-123456.7890"));
		
		item.setHazardousGoods(Boolean.TRUE);
		
		header.getLineItems().add(item);
		header.getPackageRecords().add(packageRecord);
		
		String result = header.assemble();
		System.out.println(result);
	}
	
	@Test
	public void AtlasNegative() {
		final TransactionHeaderDTO header = new TransactionHeaderDTO();
		final LineItemDTO item = new LineItemDTO();
		final PackageRecordDTO packageRecord = new PackageRecordDTO();
		
		//header.setIncotermCountryIsoCode(1234);
		header.setReferenceValue(new BigDecimal("-1234567890123456.7890"));
		
		header.getLineItems().add(item);
		header.getPackageRecords().add(packageRecord);
		
		boolean thrown = false;
		try {
			String result = header.assemble();
		}
		catch (LogonException e) {
			thrown = true;
		}
		
		assertTrue(thrown);
	}
}
