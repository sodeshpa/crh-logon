/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdf.console;

import com.aspose.words.Document;
import com.crh.logon.pdf.PdfGenerator;
import com.crh.logon.pdf.aspose.DocxTemplateProcessor;
import com.crh.logon.pdf.aspose.KeyValueDataProvider;
import com.crh.logon.pdf.templating.DataProvider;
import com.crh.logon.pdf.templating.ImageValue;
import com.crh.logon.pdf.templating.Path;
import com.crh.logon.pdf.templating.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map.Entry;
import java.util.Properties;

public class Program {
	public static void main(String[] args) throws Exception {
		if ("generate-pdf".equals(args[0])) {
			generate(args, true);
		}
		else if ("generate-ps".equals(args[0])) {
			generate(args, false);
		}
		else if ("extract".equals(args[0])) {
			extract(args);
		}
		else if ("dump".equals(args[0])) {
			dump(args);
		}
		else {
			dumpOutput(args);
			System.out.println();
			printUsage();
			System.exit(1);
		}
	}

	private static void printUsage() {
		System.err.println("usage:");
		System.err.println(" generate-pdf <input.docx> data.txt");
		System.err.println(" generate-ps <input.docx> data.txt");
		System.err.println(" extract <input.docx> (output goes to stdout)");
		System.err.println(" dump <input.docx>");
	}

	private static void dump(String[] args) throws Exception {
		if (args.length != 2) {
			printUsage();
			System.exit(1);
		}

		InputStream input = loadAsResourceStream(args[1]);

		DocxTemplateProcessor processor = new DocxTemplateProcessor(null);
		Document document = new Document(input);
		processor.dump(document, "");
	}

	private static void generate(String[] args, boolean generatePDF) throws Exception {
		if (args.length != 3) {
			printUsage();
			System.exit(1);
		}

		DataProvider provider = createProviderFromProperties(args[2]);

		InputStream input = loadAsResourceStream(args[1]);

		PdfGenerator pdfGenerator = new PdfGenerator(provider);

		String licenseFile = System.getProperty("aspose.license.file");
		if (licenseFile != null) {
			InputStream is = new FileInputStream(licenseFile);
			pdfGenerator.setLicense(is);
		}

		PdfGenerator.Document document = pdfGenerator.generate(input);

		File outfile = File.createTempFile("logon-test-", generatePDF ? ".pdf" : ".ps");
		FileOutputStream fos = new FileOutputStream(outfile);
		if (generatePDF) {
			fos.write(document.toPDF().toByteArray());
		}
		else {
			fos.write(document.toPS().toByteArray());
		}
		fos.close();

		System.out.println("Generated output file " + outfile);
	}

	private static void extract(String[] args) throws Exception {
		if (args.length != 2) {
			printUsage();
			System.exit(1);
		}

		String inputFile = args[1];
		Document document = new Document(inputFile);
		KeyValueDataProvider provider = (KeyValueDataProvider) DocxTemplateProcessor.extract(document);

		for (Entry<String, Value> entry : provider.entries()) {
			System.out.println(String.format("%s:%s", entry.getKey(), entry.getValue()));
		}
	}

	private static void dumpOutput(String[] args) {
		for (String arg : args) {
			System.out.println(arg);
		}
	}

	private static DataProvider createProviderFromProperties(String propertiesFile) throws Exception {
		Properties props = new Properties();
		props.load(new InputStreamReader(new FileInputStream(propertiesFile), "UTF-8"));

		props.store(new OutputStreamWriter(System.out, "UTF-8"), "Debug");
		KeyValueDataProvider provider = new KeyValueDataProvider(props);

		reAddAsImage("SorgLogo", provider, props);
		reAddAsImage("BannerImage", provider, props);
		reAddAsImage("DriverSignatureImage", provider, props);
		reAddAsImage("CustomerSignatureImage", provider, props);

		// HACK: re-add material logo images - we know that there are 6 max + generic
		for (int i=1; i <=6; i++) {
			reAddAsImage(String.format("MatLogoImage%s", i), provider, props);
		}
		reAddAsImage("MatLogoImageGeneric", provider, props);

		return provider;
	}

	private static void reAddAsImage(String key, KeyValueDataProvider provider, Properties props) throws Exception {
		String value = props.getProperty(key);
		if (value != null) {
			Path path = Path.parseExtended(key).getPath();

			Value document = ImageValue.Load(loadAsResourceStream(value));
			provider.put(path, document);
		}
	}

	private static InputStream loadAsResourceStream(String resource) {
		return Program.class.getClassLoader().getResourceAsStream(resource);
	}
}
