package com.crh.logon.business.services.beans.pdf;

import java.io.Serializable;

public interface PdfReport extends Serializable {

	public abstract PdfPageSettings getPageSettings();
	public abstract void setPageSettings(PdfPageSettings pageSettings);
}