/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans.pdf.deliverynote;

import java.io.Serializable;

import com.crh.logon.persistence.dto.BlobDTO;

/**
 * @author AdNovum Informatik AG
 */
public class SalesOrganizationData implements Serializable {
	private static final long serialVersionUID = 2585481659627018274L;

	private String crhLogoName;
	
	private String destinationAddress1;
	
	private String destinationAddress2;
	
	private String destinationAddress3;
	
	private String destinationEmail;
	
	private String destinationFax;
	
	private String destinationTel;
	
	private String destinationTax;
	
	private BlobDTO crhLogo;

	public String getCrhLogoName() {
		return crhLogoName;
	}

	public void setCrhLogoName(String crhLogoName) {
		this.crhLogoName = crhLogoName;
	}

	public String getDestinationAddress1() {
		return destinationAddress1;
	}

	public void setDestinationAddress1(String destinationAddress1) {
		this.destinationAddress1 = destinationAddress1;
	}

	public String getDestinationAddress2() {
		return destinationAddress2;
	}

	public void setDestinationAddress2(String destinationAddress2) {
		this.destinationAddress2 = destinationAddress2;
	}

	public String getDestinationAddress3() {
		return destinationAddress3;
	}

	public void setDestinationAddress3(String destinationAddress3) {
		this.destinationAddress3 = destinationAddress3;
	}

	public String getDestinationEmail() {
		return destinationEmail;
	}

	public void setDestinationEmail(String destinationEmail) {
		this.destinationEmail = destinationEmail;
	}

	public String getDestinationFax() {
		return destinationFax;
	}

	public void setDestinationFax(String destinationFax) {
		this.destinationFax = destinationFax;
	}

	public String getDestinationTel() {
		return destinationTel;
	}

	public void setDestinationTel(String destinationTel) {
		this.destinationTel = destinationTel;
	}

	public String getDestinationTax() {
		return destinationTax;
	}

	public void setDestinationTax(String destinationTax) {
		this.destinationTax = destinationTax;
	}

	public BlobDTO getCrhLogo() {
		return crhLogo;
	}

	public void setCrhLogo(BlobDTO crhLogo) {
		this.crhLogo = crhLogo;
	}
	
	
}
