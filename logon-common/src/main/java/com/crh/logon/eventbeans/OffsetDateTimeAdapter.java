/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.eventbeans;

import java.time.OffsetDateTime;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class OffsetDateTimeAdapter extends XmlAdapter<String, OffsetDateTime> {

	public static OffsetDateTime parseDate(String value) {
		return OffsetDateTime.parse(value);
	}


	public static String printDate(OffsetDateTime value) {
		return value.toString();
	}

	@Override
	public OffsetDateTime unmarshal(String value) throws Exception {
		return parseDate(value);
	}

	@Override
	public String marshal(OffsetDateTime value) throws Exception {
		return printDate(value);
	}
}
