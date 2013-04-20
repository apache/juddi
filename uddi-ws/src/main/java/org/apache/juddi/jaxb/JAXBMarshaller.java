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
package org.apache.juddi.jaxb;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class JAXBMarshaller {
	private static Log logger = LogFactory.getLog(JAXBMarshaller.class);
	
	public static final String PACKAGE_UDDIAPI = "org.uddi.api_v3";
	public static final String PACKAGE_SUBSCRIPTION = "org.uddi.sub_v3";
	public static final String PACKAGE_SUBSCR_RES = "org.uddi.subr_v3";
	public static final String PACKAGE_JUDDIAPI = "org.apache.juddi.api_v3";
	public static final String PACKAGE_JUDDI = "org.apache.juddi";
	private static final Map<String, JAXBContext> JAXBContexts = new HashMap<String, JAXBContext>();

	private static JAXBContext getContext(String packageName) {
		if (!JAXBContexts.containsKey(packageName)) {
			try {
				JAXBContexts.put(packageName, JAXBContext.newInstance(packageName));
			} catch (JAXBException e) {
				logger.error("Initialization of JAXBMarshaller failed:" + e, e);
				throw new ExceptionInInitializerError(e);
			}
		}
		
		return JAXBContexts.get(packageName);
	}
	
	@SuppressWarnings("rawtypes")
	public static Object unmarshallFromInputStream(InputStream inputStream, String thePackage) throws JAXBException {
		Object obj = null;
		if (inputStream != null) {
			JAXBContext jc = getContext(thePackage);
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

	public static String marshallToString(Object object, String thePackage) {
		String rawObject = null;

		try {
			JAXBContext jc = getContext(thePackage);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			marshaller.marshal(object, baos);
			rawObject = baos.toString();
		} catch (JAXBException e) {
			logger.error(e.getMessage(),e);
		}
		
		return rawObject;
	}

	public static Element marshallToElement(Object object, String thePackage, Element element) throws JAXBException {
		
		JAXBContext jc = getContext(thePackage);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.marshal(object, element);	
		return element;
	}
	
	public static Object unmarshallFromElement(Element element, String thePackage) throws JAXBException {
		JAXBContext jc = getContext(thePackage);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		@SuppressWarnings("rawtypes")
		Object obj = ((JAXBElement) unmarshaller.unmarshal(element)).getValue();
		return obj;
	}
	
}
