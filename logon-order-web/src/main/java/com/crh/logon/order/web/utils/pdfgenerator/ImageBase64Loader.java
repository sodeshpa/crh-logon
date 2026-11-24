/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.utils.pdfgenerator;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.dto.BlobDTO;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 * @author AdNovum Informatik AG
 */
@AdnTrace
public class ImageBase64Loader {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ImageBase64Loader.class);

	private static final String BASE64_HEAD = "data:image/";

	private static final String BASE64_BODY = ";base64,";

	private static final String DEFAULT_EXTENSION = "png";

	public static String getBase64CssImageContent(byte[] imageData, String extension) {

		// [[
		String result = "";
		Object[][] params = { { "imageData", imageData }, { "extension", extension } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ImageBase64Loader.class, "getBase64CssImageContent", params);
		}
		// ]]
		try {
			String base64 = "";

			if (imageData != null) {
				base64 = Base64.encode(imageData);
			}

			StringBuilder cssData = new StringBuilder();
			extension = extension != null ? extension : DEFAULT_EXTENSION;

			cssData.append(BASE64_HEAD);
			cssData.append(extension);
			cssData.append(BASE64_BODY);
			cssData.append(base64);

			result = cssData.toString();

			return result;
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
	 * Return the {@link String} containing the Base64 encoding of the <code>imageData</code>, 
	 * or a non-null empty {@link String}, if <code>imageData</code> is null 
	 * @param imageData the BlobDTO containing the image
	 * @return the {@link String} containing the base64 encoding of the image, or an empty {@link String}, 
	 * if the <code>imageData</code> is null.
	 */
	public static String getBase64CssImageContent(BlobDTO imageData) {
		// [[
		String result = "";
		Object[][] params = { { "imageData", imageData } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ImageBase64Loader.class, "getBase64CssImageContent", params);
		}
		// ]]
		try {

			// if there is no image data, just return an empty String
			if (imageData != null) {

				result = getBase64CssImageContent(imageData.getStream(), imageData.getExtension());

			}
			return result;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}
}
