/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.reporting;

import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import ch.nevis.jcan.print.base.PrinterException;
import ch.nevis.jcan.print.images.Image;
import ch.nevis.jcan.print.images.ImageFactory;

import com.crh.logon.business.services.interfaces.GenericBlobService;
import com.crh.logon.persistence.dto.BlobDTO;
import com.crh.logon.persistence.entities.common.ImageEntity;

/**
 * @author AdNovum Informatik AG
 */
public class PDFUtils {

	private static final JcanLogger LOG =
		JcanLoggerFactory.getLogger(PDFUtils.class);

	/**
	 * Tries to create the image with jcan-print support.
	 * 
	 * As far jcan print does not support color type 4 and 6 of pngs and most of
	 * the pictures are present in the db as such, as a workaround we fill the
	 * transparent pixels with color white, and thus make compatible the png for
	 * jcanprint, and return the printed png so.
	 * 
	 * @param data
	 * @return
	 * @throws PrinterException
	 */
	public static Image createImage(byte[] data) throws PrinterException {
		// [[
		Image result = null;
		Object[][] params = {{"data", data}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(PDFUtils.class, "createImage", params);
		}
		// ]]
		try {
			if (data == null) {
				return result = null;
			}
			Image driImg;
			try {
				driImg = ImageFactory.createImage(data);
			} catch (PrinterException e) {
				// only these two type of printerexceptions we try to
				// workaround, nothing else
				if (e.getMessage().equals(
						"InputStream PNG: Colortype 6 is not suported.")) {
					driImg = convertToPNGWithoutAlpha(data);
				} else if (e.getMessage().equals(
						"InputStream PNG: Colortype 4 is not suported.")) {
					// TODO (emoke Jun 3, 2009):this is not tested, since I had
					// no such png in db
					driImg = convertToPNGWithoutAlpha(data);
				} else {
					throw e;
				}
			}
			return result = driImg;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * This method tries to cut off the alpha channel of the provided image,
	 * thus makes compatible with jcanprint.
	 * 
	 * @param data
	 *            - byte []
	 * @return Image pngImage
	 * 
	 * @throws PrinterException
	 */
	public static Image convertToPNGWithoutAlpha(byte[] data) throws PrinterException {
		// [[
		Image result = null;
		Object[][] params = {{"data", data}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(PDFUtils.class, "convertToPNGWithoutAlpha", params);
		}
		// ]]
		try {
			Image pngImage = null;
			BufferedImage inputImage = null;
			
			try {
				ByteArrayInputStream bais = new ByteArrayInputStream(data);
				inputImage = ImageIO.read(bais);
			
				if (inputImage.getColorModel().getTransparency() != Transparency.OPAQUE) {
					inputImage = fillTransparentPixels(inputImage, java.awt.Color.WHITE);
				}
			
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(inputImage, "PNG", baos);
				byte[] b = baos.toByteArray();
				pngImage = ImageFactory.createImage(b);
			} catch (FileNotFoundException e) {
				LOG.error("Could not convert to png", e);
			} catch (IOException e) {
				LOG.error("Could not convert to png", e);
			}
			finally {
				// NOTE (romek Jul 6, 2011): The following is probably not necessary.
				if (inputImage != null) {
					inputImage.flush();
				}
			}
			
			return result = pngImage;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * Cuts off the apha channel from the image.
	 * 
	 * @param image
	 * @param fillColor
	 * @return
	 */
	public static BufferedImage fillTransparentPixels(BufferedImage image,
			java.awt.Color fillColor) {
		// [[
		BufferedImage result = null;
		Object[][] params = {{"image", image},
							{"fillColor", fillColor}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(PDFUtils.class, "fillTransparentPixels", params);
		}
		// ]]
		try {
			int w = image.getWidth();
			int h = image.getHeight();
			BufferedImage image2 = new BufferedImage(w, h,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image2.createGraphics();
			g.setColor(fillColor);
			g.fillRect(0, 0, w, h);
			g.drawRenderedImage(image, null);
			g.dispose();
			return result = image2;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * Check if the image data is valid so that it can be loaded successfully. If there is any problem with
	 * the data, handle it nicely (calls {@link BlobDTO#setStream(byte[])} with a null parameter and logs
	 * the error), instead of throwing a random exception eg. at the time of a PDF document generation.
	 * 
	 * @param imageBdto the image to check - should never be null. {@link GenericBlobService} always returns
	 *                   a non-null BlobDTO.
	 * @param imageName should be {@link ImageEntity#getImageName()}
	 */
	public static void checkImageIntegrity(BlobDTO imageBdto, String imageName) {
		// [[
		Object[][] params = {{"imageBdto", imageBdto},
							{"imageName", imageName}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(PDFUtils.class, "checkImageIntegrity", params);
		}
		// ]]
		try {
			byte[] imageBytes = imageBdto.getStream();
			
			if (imageBytes == null) {
				// Treat as valid, do not log it as an error (ie. just do not display the image).
				return;
			}
			
			boolean invalid = false;
			
			if (imageBytes.length == 0) {
				LOG.error("Error loading image '" + imageName + "': is of zero length.");
				invalid = true;
			}
			else {
				try {
					ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
					BufferedImage image = ImageIO.read(bais);
					
					if (image == null) {
						LOG.error("Error loading image '" + imageName
								+ "': unknown or invalid image format");
						invalid = true;
					}
					else {
						image.flush();
					}
				}
				catch (IOException e) {
					LOG.error("Error loading image '" + imageName + "'", e);
					invalid = true;
				}
			}
			
			if (invalid) {
				imageBdto.setStream(null);
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

}
