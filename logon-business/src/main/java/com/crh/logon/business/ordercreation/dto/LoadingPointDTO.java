package com.crh.logon.business.ordercreation.dto;

import java.io.Serializable;


public class LoadingPointDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
    private Long loadingpointId;

    private String loadingpointName;

    private Long packageTypeCd;
    
    private Long shippingpointId;

	public LoadingPointDTO(Long loadingpointId, String loadingpointName, Long packageTypeCd, Long shippingpointId) {
		super();
		this.loadingpointId = loadingpointId;
		this.loadingpointName = loadingpointName;
		this.packageTypeCd = packageTypeCd;
		this.shippingpointId = shippingpointId;
	}

	/**
	 * @return the shippingpointId
	 */
	public Long getShippingpointId() {
		return shippingpointId;
	}

	/**
	 * @return the loadingpointId
	 */
	public Long getLoadingpointId() {
		return loadingpointId;
	}

	/**
	 * @return the loadingpointName
	 */
	public String getLoadingpointName() {
		return loadingpointName;
	}

	/**
	 * @return the packageTypeCd
	 */
	public Long getPackageTypeCd() {
		return packageTypeCd;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((loadingpointId == null) ? 0 : loadingpointId.hashCode());
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
		LoadingPointDTO other = (LoadingPointDTO) obj;
		if (loadingpointId == null) {
			if (other.loadingpointId != null)
				return false;
		}
		else if (!loadingpointId.equals(other.loadingpointId))
			return false;
		return true;
	}

    
}
