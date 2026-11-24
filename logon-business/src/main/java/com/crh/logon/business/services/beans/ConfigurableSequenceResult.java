/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans;

import java.io.Serializable;

import com.crh.logon.persistence.entities.refdata.SeqUsageBsEntity;

/**
 * @author AdNovum Informatik AG
 */
public class ConfigurableSequenceResult implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final String sequenceNumber;
	private final SeqUsageBsEntity usage;
	
	public ConfigurableSequenceResult(String sequenceNumber, SeqUsageBsEntity usage) {
		this.sequenceNumber = sequenceNumber;
		this.usage = usage;
	}
	
	public String getSequenceNumber() {
		return sequenceNumber;
	}
	
	public SeqUsageBsEntity getUsage() {
		return usage;
	}
	
	@Override
	public String toString() {
		return "ConfigurableSequnceResult [sequenceNumber = " + sequenceNumber 
				+ ", usage desc = " + usage.getSeqUsageDesc() + "]";
	}
}
