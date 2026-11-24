/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.util.Comparator;

import com.crh.logon.persistence.entities.common.OrderTpSapVendorEntity;
import com.crh.logon.persistence.entities.common.TpSapVendorEntity;
import com.crh.logon.persistence.entities.views.TpSapVendorSearchEntity;

/**
 * DTO containing the data for the items displayed in 3rd Party Freighters tab.
 */
public class BindingsDTO implements Serializable {
	
	/**
	 * Comparator for BindingsDTO for displaying an ordered list
	 */
	private static class BindingsDTOComparator implements Comparator<BindingsDTO> {

		/**
		 * Sort by
		 * <ul>
		 * <li>SAP Vendor Name
		 * <li>Partner Function code
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(BindingsDTO o1, BindingsDTO o2) {
			
			int nameCompare = o1.sapVendorName.compareTo(o2.getSapVendorName());
			if (nameCompare == 0) {
				int pfCompare = o1.pfCode.compareTo(o2.pfCode);
				return pfCompare;
			}
			else {
				return nameCompare;
			}
		}
		
	}
	
	private static final long serialVersionUID = -6930041729383072051L;
	
	/**
	 * Factory method for creating a {@link BindingsDTO} from {@link OrderTpSapVendorEntity}
	 * @param orderTpSapVendorEntity the {@link OrderTpSapVendorEntity}
	 * @return the {@link BindingsDTO}
	 */
	public static BindingsDTO createBindingsDTO(OrderTpSapVendorEntity orderTpSapVendorEntity) {
		OrderTpSapVendorEntity e = orderTpSapVendorEntity;
		
		BindingsDTO result = new BindingsDTO();
		
		result.setId(e.getTpSapVendor().getTpSapVendorId());
		result.setPfCode(e.getTpSapVendor().getSapTpSapVendorPf().getSapTpSapVendorPfCd());
		result.setSapVendorCode(e.getTpSapVendor().getSapVendor().getSapIfcId());
		result.setSapVendorName(e.getTpSapVendor().getSapVendor().getName1());
		return result;
	}
	
	/**
	 * Factory method for creating a {@link BindingsDTO} from {@link TpSapVendorEntity}
	 * @param tpSapVendorEntity the {@link TpSapVendorEntity}
	 * @return the {@link BindingsDTO}
	 */
	public static BindingsDTO createBindingsDTO(TpSapVendorEntity tpSapVendorEntity) {
		TpSapVendorEntity e = tpSapVendorEntity;
		
		BindingsDTO result = new BindingsDTO();
		
		result.setId(e.getTpSapVendorId());
		result.setPfCode(e.getSapTpSapVendorPf().getSapTpSapVendorPfCd());
		result.setSapVendorCode(e.getSapVendor().getSapIfcId());
		result.setSapVendorName(e.getSapVendor().getName1());
		return result;

	} 
	
	/**
	 * Factory method for creating a {@link BindingsDTO} from {@link TpSapVendorSearchEntity}
	 * @param searchEntity the {@link TpSapVendorSearchEntity}
	 * @return the {@link BindingsDTO}
	 */
	public static BindingsDTO createBindingsDTO(TpSapVendorSearchEntity searchEntity) {
		
		BindingsDTO result = new BindingsDTO();
		result.setId(searchEntity.getTpSapVendorId());
		result.setPfCode(searchEntity.getSapTpSapVendorPfCd());
		result.setSapVendorCode(searchEntity.getSapIfcId());
		result.setSapVendorName(searchEntity.getName1());
		return result;
	}

	/**
	 * Returns a new instance of {@link BindingsDTOComparator}
	 * @return a new instance of {@link BindingsDTOComparator}
	 */
	public static BindingsDTOComparator getComparator() {
		return new BindingsDTOComparator();
	}

	/**
	 * Id (TpSapVendorId)
	 */
	private Long id;

	/**
	 * Partner Function Code
	 */
	private String pfCode;

	/**
	 * SAP Vendor Code
	 */
	private String sapVendorCode;

	/**
	 * SAP Vendor Name
	 */
	private String sapVendorName;

	/**
	 * Constructor
	 */
	public BindingsDTO() {
		super();
	}

	/**
	 * Constructor.
	 * @param id TpSapVendorId
	 * @param pfCode Partner Function Code
	 * @param sapVendorCode SAP Vendor Code
	 * @param sapVendorName SAP Vendor Name
	 */
	public BindingsDTO(Long id, String pfCode, String sapVendorCode, String sapVendorName) {
		super();
		this.id = id;
		this.pfCode = pfCode;
		this.sapVendorCode = sapVendorCode;
		this.sapVendorName = sapVendorName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BindingsDTO other = (BindingsDTO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		return true;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Returns the line to be displayed for a binding 
	 * @return the label
	 */
	public String getLabel() {
		StringBuilder label = new StringBuilder();
		label.append(this.sapVendorName);
		label.append(" ( ");
		label.append(this.sapVendorCode);
		label.append(" ) ");
		label.append(" - ");
		label.append(this.pfCode);
		return label.toString();
	}

	/**
	 * @return the pfCode
	 */
	public String getPfCode() {
		return pfCode;
	}

	/**
	 * @return the sapVendorCode
	 */
	public String getSapVendorCode() {
		return sapVendorCode;
	}

	/**
	 * @return the sapVendorName
	 */
	public String getSapVendorName() {
		return sapVendorName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	/**
	 * Sets the id
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Sets the Partner Function code
	 * @param pfCode the pfCode to set
	 */
	public void setPfCode(String pfCode) {
		this.pfCode = pfCode;
	}

	/**
	 * Sets the SAP Vendor code
	 * @param sapVendorCode the sapVendorCode to set
	 */
	public void setSapVendorCode(String sapVendorCode) {
		this.sapVendorCode = sapVendorCode;
	}

	/**
	 * Sets the SAP Vendor Name
	 * @param sapVendorName the sapVendorName to set
	 */
	public void setSapVendorName(String sapVendorName) {
		this.sapVendorName = sapVendorName;
	}



}
