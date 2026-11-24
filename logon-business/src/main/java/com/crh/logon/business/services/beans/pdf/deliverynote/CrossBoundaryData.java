/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans.pdf.deliverynote;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.crh.logon.persistence.dto.BlobDTO;

/**
 * @author AdNovum Informatik AG
 */
public class CrossBoundaryData implements Serializable {
	private static final long serialVersionUID = 3649948894515757458L;
	
	/**
	 * NHM (Nomenclature Harmonisée Marchandises ) code
	 */
	private String nhm;
	
	/**
	 * Crossboundary text
	 */
	private String crossBoundary;
	
	/**
	 * Banner image {@link BlobDTO}
	 */
	private BlobDTO bannerImage;
	
	/**
	 * Name of the banner image
	 */
	private String bannerImageName;
	
	/**
	 * Returns the Banner Image, could be <code>null</code> 
	 * @return the Banner Image, could be <code>null</code>
	 */
	public BlobDTO getBannerImage() {
		return bannerImage;
	}

	/**
	 * Sets the Banner Image
	 * @param bannerImage the Banner Image {@link BlobDTO}
	 */
	public void setBannerImage(BlobDTO bannerImage) {
		this.bannerImage = bannerImage;
	}
	
	/**
	 * Returns true, if the banner image exists
	 * @return true, if the banner image exists
	 */
	public boolean isBannerImageExists() {
		if (this.bannerImage != null) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Returns true, if the NHM code should be rendered (not empty)
	 * @return true, if the NHM code should be rendered (not empty)
	 */
	public boolean renderNhm() {
		return StringUtils.isNotEmpty(nhm);
	}
	
	/**
	 * Returns true, if the cross boundary code should be rendered (not empty)
	 * @return true, if the cross boundary code should be rendered (not empty)
	 */
	public boolean renderCrossBoundary() {
		return StringUtils.isNotEmpty(crossBoundary);
	}
	
	/**
	 * Returns true, if the banner image (on top of the Material List/ in place of the NHM code) 
	 * should be rendered on the Delivery Note.
	 * <p>It returns true, if:
	 *  <ul>
	 *  <li>The banner image is not null, and
	 *  <li>crossBoundary text is null or empty, and
	 *  <li>nhm code is null or empty
	 *  </ul>
	 * @param header the {@link HeaderData}
	 * @return true if the banner image should be rendered to the Delivery Note
	 */
	public boolean renderBanner() {
		
		return isBannerImageExists() && 
			   !renderCrossBoundary() && 
			   !renderNhm();
	}
	
	/**
	 * Returns the name of the banner image
	 * @return the bannerImageName
	 */
	public String getBannerImageName() {
		return bannerImageName;
	}

	/**
	 * Sets the name of the banner image
	 * @param bannerImageName the bannerImageName to set
	 */
	public void setBannerImageName(String bannerImageName) {
		this.bannerImageName = bannerImageName;
	}
	
	/**
	 * Returns the NHM code
	 * @return the NHM code
	 */
	public String getNhm() {
		return nhm;
	}
	
	/**
	 * Sets the NHM code
	 * @param nhm the NHM code
	 */
	public void setNhm(String nhm) {
		this.nhm = nhm;
	}
	
	/**
	 * Returns the cross-boundary text
	 * @return the cross-boundary text
	 */
	public String getCrossBoundary() {
		return crossBoundary;
	}
	
	/**
	 * Sets the cross-boundary text
	 * @param crossBoundary the cross-boundary text
	 */
	public void setCrossBoundary(String crossBoundary) {
		this.crossBoundary = crossBoundary;
	}
}
