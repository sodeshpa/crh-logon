/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.util;

import java.io.StringWriter;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axis2.databinding.ADBBean;

import com.crh.logon.utils.LogonException;

/**
 * @author AdNovum Informatik AG
 */
public class WebserviceClientUtil {
	
	public static String getSerializedString(ADBBean adbBean, String elementName) {
		
		StringWriter stringWriter = new StringWriter();
		try {
			XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(stringWriter);
			adbBean.serialize(new QName(elementName), xmlStreamWriter);
			xmlStreamWriter.flush();
		}
		catch (Exception e) {
			
			throw new LogonException(e);
		}
		
		return stringWriter.toString();
	}
	
}
