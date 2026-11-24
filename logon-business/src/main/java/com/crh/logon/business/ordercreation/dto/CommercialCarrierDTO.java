package com.crh.logon.business.ordercreation.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

public class CommercialCarrierDTO implements Serializable{
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(CommercialCarrierDTO.class);
    private static final long serialVersionUID = 1L;

    private Long carrierId;
    private String displayName;
    private Boolean carrierRequiresPlanningFlag;

    public CommercialCarrierDTO(Long carrierId, String displayName, Boolean carrierRequiresPlanningFlag) {
    	this.carrierId = carrierId;
    	this.displayName = displayName;
    	this.carrierRequiresPlanningFlag = carrierRequiresPlanningFlag;
	}

	public Long getCarrierId() {
        return carrierId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Boolean getCarrierRequiresPlanningFlag() {
        return carrierRequiresPlanningFlag;
    }
    
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this);
        tsb.append(super.toString());
        if (LOG.isDebugEnabled()) {
            tsb.append("carrierId", carrierId);
            tsb.append("displayName", "displayName");         
            tsb.append("carrierRequiresPlanningFlag", "carrierRequiresPlanningFlag");
        }
        else if (LOG.isInfoEnabled()) {
            tsb.append("carrierId", carrierId); // ID
        }
        return tsb.toString();
    }
}
