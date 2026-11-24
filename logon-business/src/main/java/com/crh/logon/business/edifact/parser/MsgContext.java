/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.parser;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.edifact.tags.Tag;
import com.crh.logon.business.edifact.tags.UNB;
import com.crh.logon.business.edifact.tags.UNH;
import com.crh.logon.business.edifact.tags.UNT;
import com.crh.logon.business.edifact.tags.UNZ;
import com.crh.logon.business.services.utils.EdifactException;

/**
 * @author AdNovum Informatik AG
 * This class is only used as a default handler to do some consistency checks. 
 * It could be used in the future in case tags need to share information during 
 * parsing.
 */
public class MsgContext implements Handler {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(MsgContext.class);
	private static final String SYNTAX_ID_UNOA = "UNOA";
	private static final String VERSION_DRAFT_VERSION = "D";
	private static final String RELEASE_NUMBER_99B = "99D"; // SBB sends the invalid(?) release number 99D
	private static final String AGENCY_UN = "UN";
	
	private String interchangeControlReference;
	private String messageReferenceNumber;
	private int segmentCount;
	private int messageCount;
	
	public MsgContext() {
	}
	
	public void tag(Tag tag) {
		
		segmentCount += 1;
		if (tag instanceof UNB) {
			UNB unb = (UNB)tag;
			if (!SYNTAX_ID_UNOA.equals(unb.getSyntaxId())) {
				
				throw new EdifactException("Unsupported syntax ID " + unb.getSyntaxId() + ". Supported: " + SYNTAX_ID_UNOA);
			}
			interchangeControlReference = unb.getInterchangeConrolReference();
			messageCount = 0;
		}
		if (tag instanceof UNH) {
			UNH unh = (UNH)tag;
			if ( (!VERSION_DRAFT_VERSION.equals(unh.getVersion())) || (!RELEASE_NUMBER_99B.equals(unh.getRelease())) || (!AGENCY_UN.equals(unh.getAgency())) ) {
				
				throw new EdifactException("Unsupported version: " + unh.getVersion() + "/" + unh.getRelease() + "/" + unh.getAgency() + ". Supported: " + VERSION_DRAFT_VERSION + "/" + RELEASE_NUMBER_99B + "/" + AGENCY_UN);
			}
			messageReferenceNumber = unh.getMessageReferenceNumber();
			segmentCount = 1;
			messageCount += 1;
		}
		if (tag instanceof UNT) {
			UNT unt = (UNT)tag;
			if (!unt.getMessageReferenceNumber().equals(messageReferenceNumber)) {
				
				throw new EdifactException("Message reference number mismatch, " + messageReferenceNumber + " != " + unt.getMessageReferenceNumber());
			}
			int count = Integer.parseInt(unt.getNumberOfSegments());
			if (count != segmentCount) {
				
				throw new EdifactException("Read " + segmentCount + " segments, should be " + count);
			}
		}
		if (tag instanceof UNZ) {
			UNZ unz = (UNZ)tag;
			if (!unz.getInterchangeControlReference().equals(interchangeControlReference)) {
				
				throw new EdifactException("Interchange control reference mismatch, " + interchangeControlReference + " != " + unz.getInterchangeControlReference());
			}
			int count = Integer.parseInt(unz.getInterchangeControlCount());
			if (count != messageCount) {
				
				throw new EdifactException("Read " + messageCount + " messages, should be " + count);
			}
		}
	}

	public void warning(EdifactException warning) {
	}	
	
	public String getInterchangeControlReference() {
		return interchangeControlReference;
	}

	public String getMessageReferenceNumber() {
		
		return messageReferenceNumber;
	}
}
