/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ch.nevis.jcan.print.base.CellFormat;
import ch.nevis.jcan.print.base.Color;
import ch.nevis.jcan.print.base.Footer;
import ch.nevis.jcan.print.base.Header;
import ch.nevis.jcan.print.base.Macro;
import ch.nevis.jcan.print.base.Measurable;
import ch.nevis.jcan.print.base.OutputGen;
import ch.nevis.jcan.print.base.Position;
import ch.nevis.jcan.print.base.PrinterException;
import ch.nevis.jcan.print.fonts.Font;
import ch.nevis.jcan.print.images.Image;

/**
 * @author AdNovum Informatik AG
 */
public class MultiOutputGenWrapper extends OutputGen {
	
	private Map<String, OutputGen> generators = new HashMap();
	private OutputGen primaryGenerator;
	
	public MultiOutputGenWrapper() throws PrinterException {
		super();
	}

	public Collection<OutputGen> getGenerators() {
		return generators.values();
	}
	
	public void setGenerator(String label,OutputGen generator) {
		this.generators.put(label, generator);
	}

	public OutputGen getPrimaryGenerator() {
		return primaryGenerator;
	}

	public void setPrimaryGenerator(OutputGen primaryGenerator) {
		if (!generators.values().contains(primaryGenerator)) {
			throw new IllegalArgumentException("PrimaryGenerator must be one of the generators");
		}
		this.primaryGenerator = primaryGenerator;
	}
	
	@Override
	public void ashow(double arg0, double arg1, String arg2) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.ashow(arg0, arg1, arg2);
			}
		}
	}

	@Override
	public void awidthshow(double arg0, double arg1, char arg2, double arg3, double arg4,
			String arg5) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.awidthshow(arg0, arg1, arg2, arg3, arg4, arg5);
			}
		}
	}

	@Override
	public void box(int arg0, int arg1) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.box(arg0, arg1);
			}
		}
	}

	@Override
	public void carriageReturn() throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.carriageReturn();
			}
		}
	}

	@Override
	public void drawArrowedLine(Color arg0, double arg1, double arg2, double arg3, double arg4,
			double arg5) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.drawArrowedLine(arg0, arg1, arg2, arg3, arg4, arg5);
			}
		}
	}

	@Override
	public void drawDottedLine(Color arg0, double arg1, double arg2, double arg3, double arg4,
			double arg5, double arg6) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.drawDottedLine(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
			}
		}
	}

	@Override
	public void drawLine(Color arg0, double arg1, double arg2, double arg3, double arg4, double arg5)
			throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.drawLine(arg0, arg1, arg2, arg3, arg4, arg5);
			}
		}
	}

	@Override
	public void drawRect(Color arg0, double arg1, double arg2, double arg3, double arg4, double arg5)
			throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.drawRect(arg0, arg1, arg2, arg3, arg4, arg5);
			}
		}
	}

	@Override
	public void end() throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.end();
			}
		}
	}

	@Override
	public void fillRect(Color arg0, double arg1, double arg2, double arg3, double arg4)
			throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.fillRect(arg0, arg1, arg2, arg3, arg4);
			}
		}
	}

	@Override
	public Font getAlternateFont() {
		Font result = null;
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				if (gen == primaryGenerator) {
					result = gen.getAlternateFont();
				}
				else {
					gen.getAlternateFont();
				}
			}
		}

		return result;
	}

	@Override
	public double getBottomMargin() {
		double result = 0;
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				if (gen == primaryGenerator) {
					result = gen.getBottomMargin();
				}
				else {
					gen.getBottomMargin();
				}
			}
		}

		return result;
	}

	@Override
	public int getCurPageNumber() {
		int result = 0;
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				if (gen == primaryGenerator) {
					result = gen.getCurPageNumber();
				}
				else {
					gen.getCurPageNumber();
				}
			}
		}

		return result;
	}

	@Override
	public Position getCurPos() {
		Position result = null;
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				if (gen == primaryGenerator) {
					result = gen.getCurPos();
				}
				else {
					gen.getCurPos();
				}
			}
		}

		return result;
	}

	@Override
	public double getCurX() {
		double result = 0;
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				if (gen == primaryGenerator) {
					result = gen.getCurX();
				}
				else {
					gen.getCurX();
				}
			}
		}

		return result;
	}

	@Override
	public double getCurY() {
		double result = 0;
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				if (gen == primaryGenerator) {
					result = gen.getCurY();
				}
				else {
					gen.getCurY();
				}
			}
		}

		return result;
	}

	@Override
	public Color getFillColor() {
		Color result = null;
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				if (gen == primaryGenerator) {
					result = gen.getFillColor();
				}
				else {
					gen.getFillColor();
				}
			}
		}

		return result;
	}

	@Override
	public Footer getFirstFooter() {
		Footer result = null;
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				if (gen == primaryGenerator) {
					result = gen.getFirstFooter();
				}
				else {
					gen.getFirstFooter();
				}
			}
		}

		return result;
	}

	@Override
	public Header getFirstHeader() {
		Header result = null;
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				if (gen == primaryGenerator) {
					result = gen.getFirstHeader();
				}
				else {
					gen.getFirstHeader();
				}
			}
		}

		return result;
	}

	@Override
	public Font getFont() {
		Font result = null;
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				if (gen == primaryGenerator) {
					result = gen.getFont();
				}
				else {
					gen.getFont();
				}
			}
		}

		return result;
	}

	@Override
	public Footer getFooter() {
		Footer result = null;
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				if (gen == primaryGenerator) {
					result = gen.getFooter();
				}
				else {
					gen.getFooter();
				}
			}
		}

		return result;
	}

	@Override
	public Header getHeader() {
		Header result = null;
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				if (gen == primaryGenerator) {
					result = gen.getHeader();
				}
				else {
					gen.getHeader();
				}
			}
		}

		return result;
	}

	@Override
	public double getLeading() {
		double result = 0;
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				if (gen == primaryGenerator) {
					result = gen.getLeading();
				}
				else {
					gen.getLeading();
				}
			}
		}

		return result;
	}

	@Override
	public double getLeftMargin() {
		double result = 0;
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				if (gen == primaryGenerator) {
					result = gen.getLeftMargin();
				}
				else {
					gen.getLeftMargin();
				}
			}
		}

		return result;
	}

	@Override
	public Color getLineColor() {
		Color result = null;
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				if (gen == primaryGenerator) {
					result = gen.getLineColor();
				}
				else {
					gen.getLineColor();
				}
			}
		}

		return result;
	}

	@Override
	public int getNumberOfLines(String arg0, Measurable arg1) throws PrinterException {
		int result = 0;
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				if (gen == primaryGenerator) {
					result = gen.getNumberOfLines(arg0, arg1);
				}
				else {
					gen.getNumberOfLines(arg0, arg1);
				}
			}
		}

		return result;
	}

	@Override
	public void goToTab(int arg0) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.goToTab(arg0);
			}
		}
	}

	@Override
	public void goToTabLine(int arg0) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.goToTabLine(arg0);
			}
		}
	}

	@Override
	public void insertFont(Font arg0) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.insertFont(arg0);
			}
		}
	}

	@Override
	public void newPage() throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.newPage();
			}
		}
	}

	@Override
	public void restoreGraphicsState() throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.restoreGraphicsState();
			}
		}
	}

	@Override
	public void rmove(double arg0, double arg1) {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.rmove(arg0, arg1);
			}
		}
	}

	@Override
	public void rmove(Position dp) {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.rmove(dp);
			}
		}
	}

	@Override
	public void rmoveX(double arg0) {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.rmoveX(arg0);
			}
		}
	}

	@Override
	public void rmoveY(double arg0) {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.rmoveY(arg0);
			}
		}
	}

	@Override
	public void saveGraphicsState() throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.saveGraphicsState();
			}
		}
	}

	@Override
	public void setAlternateFont(Font font) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.setAlternateFont(font);
			}
		}
	}

	@Override
	public void setFillColor(Color color) {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.setFillColor(color);
			}
		}
	}

	@Override
	public void setFirstFooter(Footer footer) {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.setFirstFooter(footer);
			}
		}
	}

	@Override
	public void setFirstHeader(Header header) {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.setFirstHeader(header);
			}
		}
	}

	@Override
	public void setFirstMargins(Measurable arg0, Measurable arg1) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.setFirstMargins(arg0, arg1);
			}
		}
	}

	@Override
	public Font setFont(Font arg0) throws PrinterException {
		Font result = null;
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				if (gen == primaryGenerator) {
					result = gen.setFont(arg0);
				}
				else {
					gen.setFont(arg0);
				}
			}
		}

		return result;
	}

	@Override
	public void setFooter(Footer footer) {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.setFooter(footer);
			}
		}
	}

	@Override
	public void setHeader(Header header) {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.setHeader(header);
			}
		}
	}

	@Override
	public void setLandscape() throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.setLandscape();
			}
		}
	}

	@Override
	public void setLineColor(Color color) {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.setLineColor(color);
			}
		}
	}

	@Override
	public void setLineSpacingFactor(double arg0) {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.setLineSpacingFactor(arg0);
			}
		}
	}

	@Override
	public void setLineThickness(double arg0) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.setLineThickness(arg0);
			}
		}
	}

	@Override
	public void setMargins(double arg0, double arg1, double arg2, double arg3)
			throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.setMargins(arg0, arg1, arg2, arg3);
			}
		}
	}

	@Override
	public void setMargins(Measurable arg0, Measurable arg1, Measurable arg2, Measurable arg3)
			throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.setMargins(arg0, arg1, arg2, arg3);
			}
		}
	}

	@Override
	public void setPaperSize(Measurable arg0, Measurable arg1) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.setPaperSize(arg0, arg1);
			}
		}
	}

	@Override
	public void setPortrait() throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.setPortrait();
			}
		}
	}

	@Override
	public void setPos(double arg0, double arg1) {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.setPos(arg0, arg1);
			}
		}
	}

	@Override
	public void setPos(Position pos) {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.setPos(pos);
			}
		}
	}

	@Override
	public void setPosX(double arg0) {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.setPosX(arg0);
			}
		}
	}

	@Override
	public void setPosY(double arg0) {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.setPosY(arg0);
			}
		}
	}

	@Override
	public void setTab(int arg0, Measurable arg1) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.setTab(arg0, arg1);
			}
		}
	}

	@Override
	public void setTextColor(Color color) {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.setTextColor(color);
			}
		}
	}

	@Override
	public void setWatermark(Macro watermark) {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.setWatermark(watermark);
			}
		}
	}

	@Override
	public void show(String arg0, int arg1) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.show(arg0, arg1);
			}
		}
	}

	@Override
	public void show(String text) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.show(text);
			}
		}
	}

	@Override
	public void showAngled(String arg0, int arg1) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.showAngled(arg0, arg1);
			}
		}
	}

	@Override
	public void showBlock(String arg0, double arg1, Measurable arg2) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.showBlock(arg0, arg1, arg2);
			}
		}
	}

	@Override
	public void showBoxed(String arg0) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.showBoxed(arg0);
			}
		}
	}

	@Override
	public void showBoxedLenAndColor(String arg0, Measurable arg1, Measurable arg2, Color arg3,
			boolean arg4, int arg5) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.showBoxedLenAndColor(arg0, arg1, arg2, arg3, arg4, arg5);
			}
		}
	}

	@Deprecated
	@Override
	public void showBoxedLenAndColor(String arg0, Measurable arg1, Measurable arg2, Color arg3,
			boolean arg4) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.showBoxedLenAndColor(arg0, arg1, arg2, arg3, arg4);
			}
		}
	}

	@Deprecated
	@Override
	public void showBoxedLenAndColorCenter(String arg0, Measurable arg1, Measurable arg2,
			Color arg3, boolean arg4) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.showBoxedLenAndColorCenter(arg0, arg1, arg2, arg3, arg4);
			}
		}
	}

	@Deprecated
	@Override
	public void showBoxedLenAndColorRight(String arg0, Measurable arg1, Measurable arg2,
			Color arg3, boolean arg4) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.showBoxedLenAndColorRight(arg0, arg1, arg2, arg3, arg4);
			}
		}
	}

	@Override
	public void showCentered(String arg0) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.showCentered(arg0);
			}
		}
	}

	@Override
	public void showImage(Image arg0, double arg1, double arg2, double arg3, double arg4)
			throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.showImage(arg0, arg1, arg2, arg3, arg4);
			}
		}
	}

	@Override
	public void showLabelAndValue(String arg0, CellFormat arg1, String arg2, CellFormat arg3,
			Measurable arg4, boolean arg5) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.showLabelAndValue(arg0, arg1, arg2, arg3, arg4, arg5);
			}
		}
	}

	@Override
	public void showLongString(String arg0, int arg1) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.showLongString(arg0, arg1);
			}
		}
	}

	@Override
	public void showMultiline(String arg0, boolean arg1) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.showMultiline(arg0, arg1);
			}
		}
	}

	@Override
	public void showMultilineCentered(String arg0, int arg1, boolean arg2) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.showMultilineCentered(arg0, arg1, arg2);
			}
		}
	}

	@Override
	public void showRight(String arg0, int arg1) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.showRight(arg0, arg1);
			}
		}
	}

	@Override
	public void showRightMetric(String arg0, int arg1) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.showRightMetric(arg0, arg1);
			}
		}
	}

	@Override
	public void showText(Color arg0, double arg1, double arg2, String arg3, Font arg4)
			throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.showText(arg0, arg1, arg2, arg3, arg4);
			}
		}
	}

	@Override
	public void showToLeft(String arg0, int arg1) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.showToLeft(arg0, arg1);
			}
		}
	}

	@Override
	public void showToLeft(String text) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.showToLeft(text);
			}
		}
	}

	@Override
	public void tab(int arg0) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.tab(arg0);
			}
		}
	}

	@Override
	public void tabLine(int arg0) throws PrinterException {
		if ((generators != null) && (generators.values() != null)) {
			for (OutputGen gen : this.generators.values()) {
				gen.tabLine(arg0);
			}
		}
	}

}
