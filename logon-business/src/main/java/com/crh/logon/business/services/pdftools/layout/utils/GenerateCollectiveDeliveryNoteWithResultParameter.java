/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout.utils;

import com.crh.logon.business.services.localinterfaces.GenericBlobServiceLocalInterface;
import com.crh.logon.business.services.pdftools.layout.DeliveryNoteData;
import com.crh.logon.business.services.pdftools.layout.DeliveryNoteLabel;
import com.crh.logon.pdftool.renderer.AbstractPdfRenderer;
import com.crh.logon.pdftool.store.AbstractPdfDataStore;

/**
 * Parameter Object for Collective Delivery Note Generator
 */
public class GenerateCollectiveDeliveryNoteWithResultParameter {

	/**
	 * {@link GenericBlobServiceLocalInterface}
	 */
	private GenericBlobServiceLocalInterface blobService;

	/**
	 * Flag to indicate that the actual rendering of the document could be skipped (if only the page numbers should be counted)
	 */
	private boolean doNotRenderDocument;

	/**
	 * Number of pages
	 */
	private int numberOfPages;
	
	/**
	 * {@link CollectiveDeliveryNotesDTOs}
	 */
	private CollectiveDeliveryNotesDTOs physicalAndServiceItems;
	
	/**
	 * {@link AbstractPdfRenderer}
	 */
	private AbstractPdfRenderer renderer;

	/**
	 * {@link AbstractPdfDataStore}
	 */
	private AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store;

	/**
	 * Constructor
	 * @param store {@link AbstractPdfDataStore}
	 * @param renderer {@link AbstractPdfRenderer} 
	 * @param blobService {@link GenericBlobServiceLocalInterface}
	 * @param numberOfPages number of pages (should be 0 in the first run, and the result of the first run should be set on the second run)
	 * @param physicalAndServiceItems {@link CollectiveDeliveryNotesDTOs}
	 * @param doNotRenderDocument flag to signal if the actual rendering of the document should be performed. <code>true</code> means rendering enabled, <code>false</code> rendering disabled. 
	 */
	public GenerateCollectiveDeliveryNoteWithResultParameter(
			AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store,
			AbstractPdfRenderer renderer, GenericBlobServiceLocalInterface blobService,
			int numberOfPages, CollectiveDeliveryNotesDTOs physicalAndServiceItems,
			boolean doNotRenderDocument) {
		this.setStore(store);
		this.setRenderer(renderer);
		this.setBlobService(blobService);
		this.setNumberOfPages(numberOfPages);
		this.setPhysicalAndServiceItems(physicalAndServiceItems);
		this.setDoNotRenderDocument(doNotRenderDocument);
	}

	/**
	 * @return the blobService
	 */
	public GenericBlobServiceLocalInterface getBlobService() {
		return blobService;
	}

	/**
	 * @return the numberOfPages
	 */
	public int getNumberOfPages() {
		return numberOfPages;
	}

	/**
	 * @return the physicalAndServiceItems
	 */
	public CollectiveDeliveryNotesDTOs getPhysicalAndServiceItems() {
		return physicalAndServiceItems;
	}

	/**
	 * @return the renderer
	 */
	public AbstractPdfRenderer getRenderer() {
		return renderer;
	}

	/**
	 * @return the store
	 */
	public AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> getStore() {
		return store;
	}

	/**
	 * @return the doNotRenderDocument
	 */
	public boolean isDoNotRenderDocument() {
		return doNotRenderDocument;
	}

	/**
	 * Returns true, if the physical and service items are set
	 * @return true, if the physical and service items are set
	 */
	public boolean isPhysicalAndServiceItemsDefined() {
		return (physicalAndServiceItems != null) && physicalAndServiceItems.isDefined();
	}

	/**
	 * @param blobService the blobService to set
	 */
	public void setBlobService(GenericBlobServiceLocalInterface blobService) {
		this.blobService = blobService;
	}

	/**
	 * @param doNotRenderDocument the doNotRenderDocument to set
	 */
	public void setDoNotRenderDocument(boolean doNotRenderDocument) {
		this.doNotRenderDocument = doNotRenderDocument;
	}

	/**
	 * @param numberOfPages the numberOfPages to set
	 */
	public void setNumberOfPages(int numberOfPages) {
		this.numberOfPages = numberOfPages;
	}

	/**
	 * @param physicalAndServiceItems the physicalAndServiceItems to set
	 */
	public void setPhysicalAndServiceItems(CollectiveDeliveryNotesDTOs physicalAndServiceItems) {
		this.physicalAndServiceItems = physicalAndServiceItems;
	}

	/**
	 * @param renderer the renderer to set
	 */
	public void setRenderer(AbstractPdfRenderer renderer) {
		this.renderer = renderer;
	}

	/**
	 * @param store the store to set
	 */
	public void setStore(AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store) {
		this.store = store;
	}
}