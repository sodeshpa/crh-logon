/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.unittest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import com.crh.logon.pdftool.objects.SimplePdfText;
import com.crh.logon.pdftool.renderer.PdfFont;
import com.crh.logon.pdftool.renderer.PdfFontStore;
import org.junit.Test;

import ch.nevis.jcan.print.fonts.Font;
import ch.nevis.jcan.print.fonts.FontFactory;

import com.crh.logon.business.services.pdftools.layout.JcanPdfRenderer;

/**
 * @author AdNovum Informatik AG
 */
public class JcanPdfRendererTest {

	private Font font = FontFactory.getInstance().createFont("Helvetica", 9);
	private int padding = 0;
	
	@Test
	public void testCutTextToWidth() {
		
		String text1 = "add1add1add1add1add1add1add1add1add1";
		String text2 = "Wolfgang Matthiessen GmbH & Co KG.,   ";
		String text3 = "ALFONS GRETEN BETONWERK GMBH & CO. KG  ";

		checkCutResult(text1, 150 - padding);
		checkCutResult(text2, 160 - padding);
		checkCutResult(text3, 160 - padding);
		
		checkSimplePdfCutResult(text1, 150 - padding);
		checkSimplePdfCutResult(text2, 160 - padding);
		checkSimplePdfCutResult(text3, 160 - padding);
		
	}
	
	@Test
	public void testCutTextToWidthInvalidWidth() {

		Font font = FontFactory.getInstance().createFont("Helvetica", 9);
		String text = "add1add1add1add1add1add1add1add1add1";
		String cutText = JcanPdfRenderer.cutTextToWidth(text, font, -1);		
		assertEquals(text, cutText);
		
	}
	
	private void checkCutResult(String text, int width) {
		String cutText = JcanPdfRenderer.cutTextToWidth(text,  font, width);
		double cutWidth = font.getWidth(cutText);
		assertTrue(String.format("Result width = %f Result text = %s", cutWidth, cutText), cutWidth <= width);
	}

	private void checkSimplePdfCutResult(String text, double width){
		
		SimplePdfText pdfText = new SimplePdfText(text);
		pdfText.setFont(new PdfFont(PdfFontStore.Helvetica, PdfFontStore.SIZE_9));
		pdfText = JcanPdfRenderer.cutTextToWidth(pdfText, width);

		Font font = FontFactory.getInstance().createFont("Helvetica", 9);
		String cutText = JcanPdfRenderer.cutTextToWidth(text, font, width);
		double cutWidth = font.getWidth(cutText);
		double pdfTextCutWidth = font.getWidth(pdfText.getText());
		
		assertEquals(cutWidth, pdfTextCutWidth, 0.0001);
	}
	
}
