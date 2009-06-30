/*
 * Copyright 2001-2008 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.juddi.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class JAXBMarshaller {
	private static Logger logger = Logger.getLogger(JAXBMarshaller.class);
	
	public static final String PACKAGE_UDDIAPI = "org.uddi.api_v3";
	public static final String PACKAGE_SUBSCRIPTION = "org.uddi.sub_v3";

	private static final Map<String, JAXBContext> JAXBContexts = new HashMap<String, JAXBContext>();
	static {
		try {
			JAXBContexts.put(PACKAGE_UDDIAPI, JAXBContext.newInstance(PACKAGE_UDDIAPI));
			JAXBContexts.put(PACKAGE_SUBSCRIPTION, JAXBContext.newInstance(PACKAGE_SUBSCRIPTION));
		} catch (JAXBException e) {
			logger.error("Initialization of JAXBMarshaller failed:" + e, e);
			throw new ExceptionInInitializerError(e);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public static Object unmarshallFromInputStream(InputStream inputStream, String thePackage) throws JAXBException {
		Object obj = null;
		if (inputStream != null) {
			JAXBContext jc = JAXBContexts.get(thePackage);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			obj = ((JAXBElement)unmarshaller.unmarshal(inputStream)).getValue();
		}
		else
			logger.error("A null input stream was provided");

		return obj;
		
	}
	
	public static Object unmarshallFromFileResource(String fileName, String thePackage) throws JAXBException, IOException {
		Object obj = null;
		URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
		if (url==null) {
			logger.error("Could not find resource: " + fileName);
		} else {
			InputStream resourceStream =url.openStream();
	
			obj = unmarshallFromInputStream(resourceStream, thePackage);
		}
		return obj;
	}

	public static Object unmarshallFromString(String rawObject, String thePackage) throws JAXBException {
		Object obj = null;
		if (rawObject != null && rawObject.length() > 0) {
			ByteArrayInputStream bais = new ByteArrayInputStream(rawObject.getBytes());
			obj = unmarshallFromInputStream(bais, thePackage);
		}
		else
			logger.error("The raw object provided is null or empty");
		return obj;
	}

	public static String marshallToString(Object object, String thePackage) throws JAXBException {
		String rawObject = null;

		JAXBContext jc = JAXBContexts.get(thePackage);
		Marshaller marshaller = jc.createMarshaller();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		marshaller.marshal(object, baos);
		rawObject = baos.toString();
		
		return rawObject;
	}
	
}
