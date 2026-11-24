/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout.utils;

import java.util.List;

import com.crh.logon.pdftool.labelprovider.DTOMarker;

/**
 * Class for holding physical and service item DTOs for Collective Delivery Note Generator
 */
public class CollectiveDeliveryNotesDTOs {

	/**
	 * List of physical order items
	 */
	private List<DTOMarker> physicalDTOs;

	/**
	 * List of service order items
	 */
	private List<DTOMarker> serviceDTOs;

	/**
	 * Constructor
	 * @param physicalDTOs  List of physical order items
	 * @param serviceDTOs List of service order items
	 */
	public CollectiveDeliveryNotesDTOs(List<DTOMarker> physicalDTOs, List<DTOMarker> serviceDTOs) {
		super();
		this.physicalDTOs = physicalDTOs;
		this.serviceDTOs = serviceDTOs;
	}

	/**
	 * @return the physicalDTOs
	 */
	public List<DTOMarker> getPhysicalDTOs() {
		return physicalDTOs;
	}

	/**
	 * @return the serviceDTOs
	 */
	public List<DTOMarker> getServiceDTOs() {
		return serviceDTOs;
	}

	/**
	 * Returns true, if physicalDTOs and serviceDTOs are not null.
	 * @return true, if physicalDTOs and serviceDTOs are not null
	 */
	public boolean isDefined() {
		return physicalDTOs != null && serviceDTOs != null;
	}

	/**
	 * @param physicalDTOs the physicalDTOs to set
	 */
	public void setPhysicalDTOs(List<DTOMarker> physicalDTOs) {
		this.physicalDTOs = physicalDTOs;
	}

	/**
	 * @param serviceDTOs the serviceDTOs to set
	 */
	public void setServiceDTOs(List<DTOMarker> serviceDTOs) {
		this.serviceDTOs = serviceDTOs;
	}
}