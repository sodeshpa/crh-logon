/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.order.web.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.crh.logon.business.services.beans.pdf.PdfReport;
import com.crh.logon.persistence.dto.BlobDTO;
import org.junit.Test;

public class WhiteListObjectInputStreamTest {

	private static final String TEST_PAYLOAD_RESOURCE_PATH = "WhiteListObjectInputStreamTest/";

	private static final String DESERIALIZATION_ATTACK_PAYLOAD_PATH = "DeserializationAttackPayload/";

	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

	// allowed classes & packages as in PdfGeneratorServlet
	private static final Set<Package> allowedPackagesForDeserialization = Stream.of(PdfReport.class.getPackage(),
			BlobDTO.class.getPackage()
	).collect(Collectors.toSet());
	private static final Set<Class> allowedClassesForDeserialization = Stream.of(java.lang.Enum.class,
			java.util.ArrayList.class,
			byte[].class,
			int[].class,
			char[].class,
			long[].class,
			double[].class,
			float[].class,
			short[].class,
			Locale.class
	).collect(Collectors.toSet());

	@Test
	public void loadValidPayload() throws IOException, ClassNotFoundException {
		readObjectFromCP(
				TEST_PAYLOAD_RESOURCE_PATH + "ValidPDFPayload.bin");
	}

	@Test(expected = ClassNotFoundException.class)
	public void dontLoadAttackBeanShell1() throws IOException, ClassNotFoundException {
		readObjectFromCP(
				TEST_PAYLOAD_RESOURCE_PATH + DESERIALIZATION_ATTACK_PAYLOAD_PATH + "BeanShell1.bin");
	}

	@Test(expected = ClassNotFoundException.class)
	public void dontLoadAttackClojure() throws IOException, ClassNotFoundException {
		readObjectFromCP(
				TEST_PAYLOAD_RESOURCE_PATH + DESERIALIZATION_ATTACK_PAYLOAD_PATH + "Clojure.bin");
	}

	@Test(expected = ClassNotFoundException.class)
	public void dontLoadAttackCommonsCollections1() throws IOException, ClassNotFoundException {
		readObjectFromCP(
				TEST_PAYLOAD_RESOURCE_PATH + DESERIALIZATION_ATTACK_PAYLOAD_PATH + "CommonsCollections1.bin");
	}



	@Test(expected = ClassNotFoundException.class)
	public void dontLoadAttackCommonsCollections5() throws IOException, ClassNotFoundException {
		readObjectFromCP(
				TEST_PAYLOAD_RESOURCE_PATH + DESERIALIZATION_ATTACK_PAYLOAD_PATH + "CommonsCollections5.bin");
	}

	@Test(expected = ClassNotFoundException.class)
	public void dontLoadAttackCommonsCollections6() throws IOException, ClassNotFoundException {
		readObjectFromCP(
				TEST_PAYLOAD_RESOURCE_PATH + DESERIALIZATION_ATTACK_PAYLOAD_PATH + "CommonsCollections6.bin");
	}

	@Test(expected = ClassNotFoundException.class)
	public void dontLoadAttackGroovy1() throws IOException, ClassNotFoundException {
		readObjectFromCP(
				TEST_PAYLOAD_RESOURCE_PATH + DESERIALIZATION_ATTACK_PAYLOAD_PATH + "Groovy1.bin");
	}

	@Test(expected = ClassNotFoundException.class)
	public void dontLoadAttackHibernate2() throws IOException, ClassNotFoundException {
		readObjectFromCP(
				TEST_PAYLOAD_RESOURCE_PATH + DESERIALIZATION_ATTACK_PAYLOAD_PATH + "Hibernate2.bin");
	}

	@Test(expected = ClassNotFoundException.class)
	public void dontLoadAttackJRMPClient() throws IOException, ClassNotFoundException {
		readObjectFromCP(
				TEST_PAYLOAD_RESOURCE_PATH + DESERIALIZATION_ATTACK_PAYLOAD_PATH + "JRMPClient.bin");
	}


	@Test(expected = ClassNotFoundException.class)
	public void dontLoadAttackMyfaces1() throws IOException, ClassNotFoundException {
		readObjectFromCP(
				TEST_PAYLOAD_RESOURCE_PATH + DESERIALIZATION_ATTACK_PAYLOAD_PATH + "Myfaces1.bin");
	}


	private Object readObjectFromCP(String ressource) throws IOException, ClassNotFoundException{
		InputStream in =  classLoader.getResourceAsStream(ressource);
		WhiteListObjectInputStream whiteListObjectInputStream = new WhiteListObjectInputStream(in, allowedClassesForDeserialization,
				allowedPackagesForDeserialization);
		return whiteListObjectInputStream.readObject();
	}

}
