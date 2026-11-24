/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ipp;

import java.net.URI;

import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.ServiceUIFactory;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.event.PrintServiceAttributeListener;

/**
 * @author AdNovum Informatik AG
 */
public class IppPrintService implements PrintService {
	
	private URI uri;
	
	public IppPrintService(URI uri) {
		
		this.uri = uri;
	}

	public void addPrintServiceAttributeListener(PrintServiceAttributeListener listener) {
		// TODO Auto-generated method stub
		
	}

	public DocPrintJob createPrintJob() {

		return new IppDocPrintJob(this, uri);
	}

	public <T extends PrintServiceAttribute> T getAttribute(Class<T> category) {
		// TODO Auto-generated method stub
		return null;
	}

	public PrintServiceAttributeSet getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getDefaultAttributeValue(Class<? extends Attribute> category) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		
		return uri.toString();
	}

	public ServiceUIFactory getServiceUIFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	public Class<?>[] getSupportedAttributeCategories() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getSupportedAttributeValues(Class<? extends Attribute> category,
			DocFlavor flavor, AttributeSet attributes) {
		// TODO Auto-generated method stub
		return null;
	}

	public DocFlavor[] getSupportedDocFlavors() {

		return new DocFlavor[] { DocFlavor.BYTE_ARRAY.AUTOSENSE };
	}

	public AttributeSet getUnsupportedAttributes(DocFlavor flavor, AttributeSet attributes) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isAttributeCategorySupported(Class<? extends Attribute> category) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isAttributeValueSupported(Attribute attrval, DocFlavor flavor,
			AttributeSet attributes) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isDocFlavorSupported(DocFlavor flavor) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removePrintServiceAttributeListener(PrintServiceAttributeListener listener) {
		// TODO Auto-generated method stub
		
	}

}
