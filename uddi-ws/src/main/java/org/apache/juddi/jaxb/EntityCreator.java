/*
 * Copyright 2001-2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.juddi.jaxb;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EntityCreator {

	private static Log logger = LogFactory.getLog(EntityCreator.class);
	
	@SuppressWarnings("rawtypes")
	public static Object buildFromDoc(String fileName, String thePackage) throws JAXBException, IOException {
		Object obj = null;
		URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
		if (url==null) {
			logger.error("Could not find resource: " + fileName);
		} else {
			InputStream resourceStream =url.openStream();
	
			JAXBContext jc = JAXBContext.newInstance(thePackage);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			obj = ((JAXBElement)unmarshaller.unmarshal(resourceStream)).getValue();
		}
		return obj;
	}

	@SuppressWarnings("rawtypes")
	public static Object buildFromString(String source, String thePackage) throws JAXBException, IOException {
		Object obj = null;
		JAXBContext jc = JAXBContext.newInstance(thePackage);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		obj = ((JAXBElement)unmarshaller.unmarshal(new StringReader(source)));
		return obj;
	}
		
	public static void outputEntity(Object obj, String thePackage) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(thePackage);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.marshal( new JAXBElement<Object>(new javax.xml.namespace.QName("uri","local"), Object.class, obj), System.out);
		
	}
	
}
