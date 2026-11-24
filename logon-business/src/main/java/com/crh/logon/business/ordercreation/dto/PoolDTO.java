package com.crh.logon.business.ordercreation.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

public class PoolDTO implements Serializable{
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PoolDTO.class);
	private static final long serialVersionUID = 1L;

	private Long poolId;
	private String sapTrspModeCd;

	public PoolDTO(Long poolId, String sapTrspModeCd) {
		this.poolId = poolId;
		this.sapTrspModeCd = sapTrspModeCd;
	}
	
	public Long getPoolId() {
		return poolId;
	}

	public String getSapTrspModeCd() {
		return sapTrspModeCd;
	}

	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this);
		tsb.append(super.toString());
		if (LOG.isDebugEnabled()) {
			tsb.append("poolId", poolId);
			tsb.append("sapTrspModeCd", "sapTrspModeCd");
		}
		else if (LOG.isInfoEnabled()) {
			tsb.append("poolId", poolId); // ID
		}
		return tsb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((poolId == null) ? 0 : poolId.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PoolDTO other = (PoolDTO) obj;
		if (poolId == null) {
			if (other.poolId != null)
				return false;
		} else if (!poolId.equals(other.poolId))
			return false;
		return true;
	}
}
