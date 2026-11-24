/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.tags;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import com.crh.logon.business.services.utils.EdifactException;


/**
 * @author AdNovum Informatik AG
 */
public class UNB extends Tag {
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyMMdd");
	private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HHmm");
	private DataElementMeta syntaxId = new DataElementMeta(0, 0, true);
	private DataElementMeta syntaxVersionNumber = new DataElementMeta(0, 1, true);
	private DataElementMeta senderId = new DataElementMeta(1, 0, true);
	private DataElementMeta senderPartnerIdCodeQualifier = new DataElementMeta(1, 1, false);
	private DataElementMeta recipientId = new DataElementMeta(2, 0, true);
	private DataElementMeta recipientPartnerIdCodeQualifier = new DataElementMeta(2, 1, false);
	private DataElementMeta prepDate = new DataElementMeta(3, 0, true);
	private DataElementMeta prepTime = new DataElementMeta(3, 1, true);
	private DataElementMeta interchangeConrolReference = new DataElementMeta(4, 0, true);
	private DataElementMeta testInd = new DataElementMeta(8, 0, false);
	private DataElementMeta[] dataElementMetas = { syntaxId, syntaxVersionNumber, senderId, senderPartnerIdCodeQualifier, recipientId, recipientPartnerIdCodeQualifier, prepDate, prepTime, interchangeConrolReference, testInd };
	
	public UNB() {
		
		super("UNB", "Interchange header");
	}

	public String getSyntaxId() {
		return syntaxId.getValue();
	}

	public void setSyntaxId(String syntaxId) {
		this.syntaxId.setValue(syntaxId);
	}

	public String getSyntaxVersionNumber() {
		return syntaxVersionNumber.getValue();
	}

	public void setSyntaxVersionNumber(String syntaxVersionNumber) {
		this.syntaxVersionNumber.setValue(syntaxVersionNumber);
	}

	public String getSenderId() {
		return senderId.getValue();
	}

	public void setSenderId(String senderId) {
		this.senderId.setValue(senderId);
	}

	public String getSenderPartnerIdCodeQualifier() {
		return senderPartnerIdCodeQualifier.getValue();
	}

	public void setSenderPartnerIdCodeQualifier(String senderPartnerIdCodeQualifier) {
		this.senderPartnerIdCodeQualifier.setValue(senderPartnerIdCodeQualifier);
	}

	public String getRecipientId() {
		return recipientId.getValue();
	}

	public void setRecipientId(String recipientId) {
		this.recipientId.setValue(recipientId);
	}

	public String getRecipientPartnerIdCodeQualifier() {
		return recipientPartnerIdCodeQualifier.getValue();
	}

	public void setRecipientPartnerIdCodeQualifier(String partnerIdCodeQualifier) {
		this.recipientPartnerIdCodeQualifier.setValue(partnerIdCodeQualifier);
	}

	public Date getPrepDateTime() {
		
		Calendar date = Calendar.getInstance();
		Calendar time = Calendar.getInstance();
		try {
			synchronized (DATE_FORMAT) {
				date.setTime(DATE_FORMAT.parse(this.prepDate.getValue()));
			}
			synchronized (TIME_FORMAT) {
				time.setTime(TIME_FORMAT.parse(this.prepTime.getValue()));
			}
		} catch (ParseException e) {
			throw new EdifactException(e);
		}
		time.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
		Calendar dateTime = DateUtils.truncate(time, Calendar.MINUTE);
		
		return dateTime.getTime();
	}

	public void setPrepDateTime(Date prepDateTime) {
		
		synchronized (DATE_FORMAT) {
			this.prepDate.setValue(DATE_FORMAT.format(prepDateTime));
		}
		synchronized(TIME_FORMAT) {
			this.prepTime.setValue(TIME_FORMAT.format(prepDateTime));
		}
	}

	public String getInterchangeConrolReference() {
		return interchangeConrolReference.getValue();
	}

	public void setInterchangeConrolReference(String interchangeConrolReference) {
		this.interchangeConrolReference.setValue(interchangeConrolReference);
	}

	public String getTestInd() {
		return testInd.getValue();
	}

	public void setTestInd(String testInd) {
		this.testInd.setValue(testInd);
	}
	
	@Override
	protected DataElementMeta[] getDataElementMetas() {
		
		return dataElementMetas;
	}
}
