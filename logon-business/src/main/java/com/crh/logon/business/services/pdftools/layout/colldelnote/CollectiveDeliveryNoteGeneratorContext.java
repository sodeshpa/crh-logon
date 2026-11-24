/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout.colldelnote;

import java.util.ArrayList;
import java.util.List;

import com.crh.logon.pdftool.labelprovider.DTOMarker;

/**
 * Context value holder for Collective Delivery Note generator
 */
public class CollectiveDeliveryNoteGeneratorContext {
	
	/**
	 * Current page
	 */
	private int currentPage = 1;

	/**
	 * Footer is displayed on the page
	 */
	private boolean footerShown = false;

	/**
	 * List of physical order items
	 */
	private List<DTOMarker> physicalDTOs = null;

	/**
	 * list of physical items are displayed on the current page
	 */
	private boolean physicalTableStarted = false;

	/**
	 * Separator line in service items
	 */
	private boolean printSeparatorLine = true;

	/**
	 * List of service order items
	 */
	private List<DTOMarker> serviceDTOs = null;

	/**
	 * list of service items are displayed on the current page
	 */
	private boolean servicesTableStarted = false;

	/**
	 * Number of all pages of the collective delivery note
	 */
	private int totalNumberOfPages = 0;

	/**
	 * @return the currentPage
	 */
	public int getCurrentPage() {
		return currentPage;
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
	 * @return the totalNumberOfPages
	 */
	public int getTotalNumberOfPages() {
		return totalNumberOfPages;
	}

	/**
	 * @return the footerShown
	 */
	public boolean isFooterShown() {
		return footerShown;
	}

	/**
	 * @return the physicalTableStarted
	 */
	public boolean isPhysicalTableStarted() {
		return physicalTableStarted;
	}

	/**
	 * @return the printSeparatorLine
	 */
	public boolean isPrintSeparatorLine() {
		return printSeparatorLine;
	}

	/**
	 * @return the servicesTableStarted
	 */
	public boolean isServicesTableStarted() {
		return servicesTableStarted;
	}

	/**
	 * @param currentPage the currentPage to set
	 */
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	/**
	 * @param footerShown the footerShown to set
	 */
	public void setFooterShown(boolean footerShown) {
		this.footerShown = footerShown;
	}

	/**
	 * @param physicalDTOs the physicalDTOs to set
	 */
	public void copyPhysicalDTOs(List<DTOMarker> physicalDTOs) {
		this.physicalDTOs = new ArrayList<DTOMarker>(physicalDTOs);
	}

	/**
	 * @param physicalTableStarted the physicalTableStarted to set
	 */
	public void setPhysicalTableStarted(boolean physicalTableStarted) {
		this.physicalTableStarted = physicalTableStarted;
	}

	/**
	 * @param printSeparatorLine the printSeparatorLine to set
	 */
	public void setPrintSeparatorLine(boolean printSeparatorLine) {
		this.printSeparatorLine = printSeparatorLine;
	}

	/**
	 * @param serviceDTOs the serviceDTOs to set
	 */
	public void copyServiceDTOs(List<DTOMarker> serviceDTOs) {
		this.serviceDTOs = new ArrayList<DTOMarker>(serviceDTOs);
	}

	/**
	 * @param servicesTableStarted the servicesTableStarted to set
	 */
	public void setServicesTableStarted(boolean servicesTableStarted) {
		this.servicesTableStarted = servicesTableStarted;
	}

	/**
	 * @param totalNumberOfPages the totalNumberOfPages to set
	 */
	public void setTotalNumberOfPages(int totalNumberOfPages) {
		this.totalNumberOfPages = totalNumberOfPages;
	}
}