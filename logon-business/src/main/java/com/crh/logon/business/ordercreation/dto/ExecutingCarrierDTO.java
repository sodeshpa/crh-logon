package com.crh.logon.business.ordercreation.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

public class ExecutingCarrierDTO implements Serializable{
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ExecutingCarrierDTO.class);
    private static final long serialVersionUID = 1L;

    private Long carrierId;
    private String displayName;

    public ExecutingCarrierDTO(Long carrierId, String displayName, Boolean carrierRequiresPlanningFlag) {
	}

	public ExecutingCarrierDTO(Long execCarrierId, String execCarrierDisplayName) {
		this.carrierId = execCarrierId;
		this.displayName = execCarrierDisplayName;		
	}

	public Long getCarrierId() {
        return carrierId;
    }

    public String getDisplayName() {
        return displayName;
    }

    
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this);
        tsb.append(super.toString());
        if (LOG.isDebugEnabled()) {
            tsb.append("carrierId", carrierId);
            tsb.append("displayName", "displayName");         
        }
        else if (LOG.isInfoEnabled()) {
            tsb.append("carrierId", carrierId); // ID
        }
        return tsb.toString();
    }
}
