/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout;

import java.text.DecimalFormat;
import java.util.ArrayList;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.localinterfaces.GenericBlobServiceLocalInterface;
import com.crh.logon.pdftool.PdfDocumentWrapper;
import com.crh.logon.pdftool.layout.PdfBorderData;
import com.crh.logon.pdftool.objects.PdfObject;
import com.crh.logon.pdftool.objects.SimplePdfText;
import com.crh.logon.persistence.dto.BlobDTO;

/**
 * @author AdNovum Informatik AG
 */
public class PdfLayoutGenerationTool {
	
	private static final JcanLogger LOG = JcanLoggerFactory
			.getLogger(PdfLayoutGenerationTool.class);
	public static final String COLON_SPACE = ": ";
	public static final double LOGO_WIDTH = 100.0; // pixels
	public static final double LOGO_HEIGHT = 50.0; // pixels
	public static final int LOGO_PADDING_LEFT = 240; // pixels
	public static final int LOGO_PADDING_TOP = 71; // pixels
	public static final double BANNER_WIDTH = 510.0; // pixels
	public static final double BANNER_HEIGHT = 100.0; // pixels
	public static final double SIGNATURE_WIDTH = 135.0; // pixels
	public static final double SIGNATURE_HEIGHT = 75.0; // pixels
	
	//weight formatting
	public static final DecimalFormat WEIGHT_FORMAT = new DecimalFormat("0.00");

	public static final PdfBorderData RIGHT_ALIGN = new PdfBorderData(PdfBorderData.HOrientation.RIGHT);
	public static final PdfBorderData CENTER_ALIGN = new PdfBorderData(PdfBorderData.HOrientation.CENTER);
	public static final PdfBorderData LEFT_ALIGN = new PdfBorderData(PdfBorderData.HOrientation.LEFT);
	
	/**
	 * @param bannerImageName
	 * @param i
	 * @param j
	 * @param bannerWidth
	 * @param bannerHeight
	 */
	public static void showImage(JcanPdfRenderer renderer, byte[] imgData, double x, double y, double width,
			double height) {
		((JcanPdfRenderer)renderer).addImage(imgData, x, y, width, height);
	}

	public static void addToListIfNotEmpty(ArrayList<String> list, String... strings) {
		for (String string : strings) {
			if (!string.isEmpty()) {
				LOG.debug("Putting into pdf: " + string);
				list.add(string);
			}
		}
	}
	
	/**
	 * @param bannerImageName
	 * @param i
	 * @param j
	 * @param bannerWidth
	 * @param bannerHeight
	 */
	public static void showImage(GenericBlobServiceLocalInterface blobService, JcanPdfRenderer renderer, String imageName, double x, double y, double width,
			double height) {
		if (blobService != null) {			
			BlobDTO imageDTO = blobService.fetchBlobWithStringKeyValue("ImageEntity", "imageName", imageName, true);
			if (imageDTO != null ) {
				((JcanPdfRenderer)renderer).addImage(imageDTO, imageName, x, y, width, height);
			}
		} else {
			// blobDto is null in test mode; just draw an empty line
			((JcanPdfRenderer)renderer).addTestImage(x, y, width, height);
		}
	}
	
	
	/**
	 * Returns the Y coordinate for the {@link PdfDocumentWrapper} 
	 * where the next inserted element would be placed.
	 * @param document {@link PdfDocumentWrapper}
	 * @return the Y coordinate for the {@link PdfDocumentWrapper} 
	 */
	public static double getCurrentYCoordinate(PdfDocumentWrapper document) {
		double y = document.getMarginTop();
		for (PdfObject object : document.getContent()) {
			y += object.getHeight();
		}
		return y;
	}
	
	/**
	 * Returns the not yet occupied vertical space on the page. 
	 * The height of the page is decreased by the top and bottom margins first, 
	 * then decreased by the height of the current visible elements on the page and
	 * lastly it is decreased by 80 points.
	 * @param document the {@link PdfDocumentWrapper}
	 * @return the not yet occupied vertical space on the page
	 */
	public static int getAvailableSpace(PdfDocumentWrapper document) {
		int sum = 0;
		for (PdfObject object : document.getContent()) {
			sum += object.getHeight();
		}
		return (int)(document.getHeight() - document.getMarginTop() - document.getMarginBottom() - sum - 80);
	}
	
	public static SimplePdfText getRightAlignedText(String text) {
		SimplePdfText pdfText = SimplePdfText.create(text);
		return rightAlignText(pdfText);
	}
	
	public static SimplePdfText rightAlignText(SimplePdfText pdfText) {
		return alignText(pdfText, RIGHT_ALIGN);
	}
	
	public static SimplePdfText centerAlignText(SimplePdfText pdfText) {
		return alignText(pdfText, CENTER_ALIGN);
	}
	
	public static SimplePdfText leftAlignText(SimplePdfText pdfText) {
		return alignText(pdfText, LEFT_ALIGN);
	}
	
	private static SimplePdfText alignText(SimplePdfText pdfText, PdfBorderData borderData) {
		pdfText.setLayoutData(borderData);
		return pdfText;
	}
	
}
