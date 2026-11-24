package com.crh.logon.business.services.pdftools.layout.delnote;

import ch.nevis.jcan.print.base.Color;
import ch.nevis.jcan.print.base.OutputGen;
import ch.nevis.jcan.print.base.PrinterException;

import com.crh.logon.pdftool.renderer.CustomPrintSection;
import com.crh.logon.utils.LogonException;

public class DeliveryNoteTotalLinesPrintSection implements CustomPrintSection {

	private static final int HEIGHT_OF_TOTALS_LINE = 12;
	private static final int HEIGHT_OF_TOTAL_AS_TEXT = 11;
	private static final int HEIGHT_OF_EXPORT_TOTAL_TEXT = 11;
	private static final int RIGHT_EDGE = 490;
	private static final int LEFT_EDGE = 40;
	private static final int DEFAULT_BUFFER_SPACE = 82;
	
	private double availableSpace;
	private boolean totalPrintedAsText;
	private boolean totalsLinePresent;
	private boolean exportTotalTextPresent;

	public DeliveryNoteTotalLinesPrintSection(boolean totalsLinePresent, boolean printTotalAsText, boolean exportTotalTextPresent, double availableSpace){
		this.availableSpace = availableSpace + DEFAULT_BUFFER_SPACE;
		this.totalPrintedAsText = printTotalAsText;
		this.totalsLinePresent = totalsLinePresent;
		this.exportTotalTextPresent = exportTotalTextPresent;
	}
	
	public void print(Object outputObject) {

		if (!(outputObject instanceof OutputGen)) {
			throw new UnsupportedOperationException(
					"Output gens other than jcan-print are not supported");
		}
		
		OutputGen output = (OutputGen) outputObject;
		
		try {
			output.drawLine(Color.BLACK, LEFT_EDGE, availableSpace, RIGHT_EDGE, availableSpace, 1.0);
			if (totalPrintedAsText){
				availableSpace += HEIGHT_OF_TOTAL_AS_TEXT;
			}
			if (totalsLinePresent) {
				availableSpace += HEIGHT_OF_TOTALS_LINE;
			}
			if (exportTotalTextPresent){
				availableSpace += HEIGHT_OF_EXPORT_TOTAL_TEXT;
			}
			
			output.drawLine(Color.BLACK, LEFT_EDGE, availableSpace, RIGHT_EDGE, availableSpace, 1.0);
			
		}catch (PrinterException e) {
			throw new LogonException(e);
		}
	}

}
