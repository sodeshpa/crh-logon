/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout.utils;

/**
 * Class for holding result data for Collective Delivery Note Generator
 */
public class CollectiveDeliveryNoteGenerationResult {
	
	/**
	 * Number of pages calculated while generating the Collective Delivery Note
	 */
	private int numberOfPages;
	
	/**
	 * Success flag
	 */
	private boolean success;
	
	/**
	 * {@link CollectiveDeliveryNotesDTOs}
	 */
	private CollectiveDeliveryNotesDTOs physicalAndServiceItems;

	/**
	 * Constructor
	 * @param numberOfPages number of pages for the collective delivery note.
	 * @param success success flag
	 * @param physicalAndServiceItems {@link CollectiveDeliveryNotesDTOs}
	 */
	public CollectiveDeliveryNoteGenerationResult(int numberOfPages, boolean success,
			CollectiveDeliveryNotesDTOs physicalAndServiceItems) {
		super();
		this.numberOfPages = numberOfPages;
		this.success = success;
		this.physicalAndServiceItems = physicalAndServiceItems;
	}

	/**
	 * @return the physicalAndServiceItems
	 */
	public CollectiveDeliveryNotesDTOs getPhysicalAndServiceItems() {
		return physicalAndServiceItems;
	}

	/**
	 * @param physicalAndServiceItems the physicalAndServiceItems to set
	 */
	public void setPhysicalAndServiceItems(CollectiveDeliveryNotesDTOs physicalAndServiceItems) {
		this.physicalAndServiceItems = physicalAndServiceItems;
	}

	/**
	 * @param numberOfPages the numberOfPages to set
	 */
	public void setNumberOfPages(int numberOfPages) {
		this.numberOfPages = numberOfPages;
	}

	/**
	 * @param success the success to set
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * @return the numberOfPages
	 */
	public int getNumberOfPages() {
		return numberOfPages;
	}
	
	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}

}
