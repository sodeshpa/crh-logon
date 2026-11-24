/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdf;

import com.aspose.words.License;
import com.aspose.words.SaveFormat;
import com.crh.logon.pdf.aspose.DocxTemplateProcessor;
import com.crh.logon.pdf.templating.DataProvider;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PdfGenerator {
	public static final class Document {
		private final ByteArrayOutputStream pdfOutStream;
		private final ByteArrayOutputStream psOutStream;

		private Document(ByteArrayOutputStream pdfOutStream, ByteArrayOutputStream psOutStream) {
			this.pdfOutStream = pdfOutStream;
			this.psOutStream = psOutStream;
		}

		public ByteArrayOutputStream toPDF() {
			return pdfOutStream;
		}

		public ByteArrayOutputStream toPS() {
			return psOutStream;
		}
	}

	private final DataProvider provider;

	public PdfGenerator(DataProvider provider) {
		this.provider = provider;
	}

	public void setLicense(InputStream is) {
		try {
			License license = new License();
			license.setLicense(is);
		}
		catch (Exception exc) {
			throw new IllegalArgumentException("Invalid inputstream to ASPOSE library", exc);
		}
	}

	public Document generate(String filePath) throws IOException {
		return generate(new FileInputStream(filePath));
	}

	public Document generate(InputStream is) throws IOException {
		try {
			com.aspose.words.Document document = new com.aspose.words.Document(is);

			DocxTemplateProcessor processor = new DocxTemplateProcessor(provider);
			processor.process(document);

			ByteArrayOutputStream pdfOut = new ByteArrayOutputStream();
			document.save(pdfOut, SaveFormat.PDF);

			ByteArrayOutputStream psOut = new ByteArrayOutputStream();
			document.save(pdfOut, SaveFormat.PS);

			return new Document(pdfOut, psOut);
		}
		catch (IOException exc) {
			throw exc;
		}
		catch (Exception exc) {
			throw new IOException("Generating PDF failed", exc);
		}
	}
}
