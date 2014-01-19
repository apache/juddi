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
import java.io.File;
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

/**
 * This class is the logical opposite of PrintUDDI. It will create
 * UDDI objects from a string or file.
 * @see PrintUDDI
 * @author unknown, but probably Kurt Stam
 */
public class EntityCreator {

    public static final String UDDIv3_Package="org.uddi.api_v3";
    public static final String JUDDIv3_Package="org.apache.juddi.api_v3";
    
	private static Log logger = LogFactory.getLog(EntityCreator.class);
	
        /**
         * Builds UDDI data from a document, URL, file, etc
         * @param fileName
         * @param thePackage
         * @return UDDI entity from the file or null
         * @throws JAXBException
         * @throws IOException 
         */
	@SuppressWarnings("rawtypes")
	public static Object buildFromDoc(String fileName, String thePackage) throws JAXBException, IOException {
		Object obj = null;
                File f = new File(fileName);
                URL url=null;
                if (f.exists()){
                    url = f.toURI().toURL();
                }
                if (url==null) {
		    url = Thread.currentThread().getContextClassLoader().getResource(fileName);
                }
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

        /**
         * converts a XML in a String to a UDDI entity
         * @param source
         * @param thePackage
         * @return UDDI entity from the file
         * @throws JAXBException
         * @throws IOException 
         */
	@SuppressWarnings("rawtypes")
	public static Object buildFromString(String source, String thePackage) throws JAXBException, IOException {
		Object obj = null;
		JAXBContext jc = JAXBContext.newInstance(thePackage);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		obj = ((JAXBElement)unmarshaller.unmarshal(new StringReader(source)));
		return obj;
	}
		
        /**
         * Only use this class for debugging purposes. Output may not be valid XML
         * @param obj
         * @param thePackage
         * @throws JAXBException
         * @deprecated
         */
        @Deprecated
	public static void outputEntity(Object obj, String thePackage) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(thePackage);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.marshal( new JAXBElement<Object>(new javax.xml.namespace.QName("uri","local"), Object.class, obj), System.out);
		
	}
	
}
