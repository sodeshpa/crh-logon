/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout.logoutils;

import java.util.ArrayList;
import java.util.List;

import ch.nevis.jcan.print.base.Color;
import ch.nevis.jcan.print.base.OutputGen;
import ch.nevis.jcan.print.base.PrinterException;

import com.crh.logon.business.services.pdftools.layout.LogoPrintSection;
import com.crh.logon.utils.LogonException;

public class LogoUtils {

	/**
	 * Renders a bounding box around the area available for Logo Printing
	 * @param output {@link OutputGen}
	 */
	public static void renderLogoAreaBoundingBox(OutputGen output, LogoPrintSection logoPrintSection) {
		try {

			double x1 = logoPrintSection.getLogoBlockLeftX();
			double x2 = logoPrintSection.getLogoBlockRightX();
			double y1 = logoPrintSection.getStartYCoordinate();
			double y2 = logoPrintSection.getEndYCoordinate();

			// Marks the place for logos with a Red rectangle
			output.drawRect(Color.GREEN, x1, y1, x2 - x1, y2 - y1, 0.5);

		}
		catch (PrinterException e) {
			throw new LogonException(e);
		}
	}

	/**
	 * Creates a {@link List} of {@link String}s from three {@link String}s
	 * @param text1 the first {@link String}
	 * @param text2 the second {@link String}
	 * @param text3 the third {@link String}
	 * @return the {@link List} of {@link String}s
	 */
	public static List<String> getList(String text1, String text2, String text3) {
		List<String> result = new ArrayList<String>();
		result.add(text1);
		result.add(text2);
		result.add(text3);
		return result;
	}

}
