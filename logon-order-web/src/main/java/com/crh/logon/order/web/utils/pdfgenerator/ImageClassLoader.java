package com.crh.logon.order.web.utils.pdfgenerator;

import java.io.IOException;

import org.zefer.cache.ResourceProvider;

import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.persistence.dto.BlobDTO;
import com.crh.logon.web.utils.ServiceLocatorProvider;

@AdnTrace
public class ImageClassLoader extends ResourceProvider{
	
	private static final String IDENTIFIER = "file:image=";
	private static final String ENTITYNAME = "ImageEntity";
	private static final String KEYNAME = "imageName";
	private static final Boolean AUTHORIZED = true;

	public byte[] getResourceAsBytes(String resource, boolean debugOn) throws IOException {
			System.out.println("Resource url: "+ resource);
			if (resource!=null && resource.startsWith(IDENTIFIER)) {
				//Authorization.authorize(Authorization.PUBLIC_FUNCTION);
				ServiceLocator serviceLocator = ServiceLocatorProvider.getServiceLocator();
				String keyValue = resource.substring(IDENTIFIER.length());
			 	BlobDTO blob = serviceLocator.getGenericBlobService().fetchBlobWithStringKeyValue(ENTITYNAME, KEYNAME, keyValue, AUTHORIZED);
			 	
		        return blob.getStream();
			}
			else {
				return null;
			}
	    }  
}
