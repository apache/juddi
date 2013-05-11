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
package org.apache.juddi.v3.client.mapping;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLLocator;
import javax.wsdl.xml.WSDLReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.ClassUtil;

import com.ibm.wsdl.factory.WSDLFactoryImpl;

/**
 * A WSDL parser/reader
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * Modified for supporting http based credentials by Alex O'Ree
 */
public class ReadWSDL {
	
	private final Log log = LogFactory.getLog(this.getClass());
	
	public Definition readWSDL(String fileName) throws WSDLException {
            
		Definition wsdlDefinition = null;
		WSDLFactory factory = WSDLFactoryImpl.newInstance();
		WSDLReader reader = factory.newWSDLReader();
		URL url = ClassUtil.getResource(fileName, this.getClass());
		try {
			URI uri = url.toURI();
			WSDLLocator locator = new WSDLLocatorImpl(uri);
			wsdlDefinition = reader.readWSDL(locator);
		} catch (URISyntaxException e) {
			log.error(e.getMessage(),e);
		}
		return wsdlDefinition;
	}
	
        /**
         * Reads a WSDL file, assumes that credentials are required. This is useful for when the WSDL document itself
         * is protected by HTTP based authentication mechanisms
         * @param wsdlUrl
         * @param username
         * @param password
         * @param domain
         * @return a Definition object representing the WSDL
         * @throws WSDLException 
         */
        public Definition readWSDL(URL wsdlUrl, String username, String password) throws WSDLException {
	
		Definition wsdlDefinition = null;
		WSDLFactory factory = WSDLFactoryImpl.newInstance();
		WSDLReader reader = factory.newWSDLReader();
		try {
			URI uri = wsdlUrl.toURI();
			WSDLLocator locator = new WSDLLocatorImpl(uri);
			wsdlDefinition = reader.readWSDL(locator);
		} catch (URISyntaxException e) {
			log.error(e.getMessage(),e);
		}
		return wsdlDefinition;
	}
        /**
         * Reads a WSDL file, assumes that credentials are not required. This is a convenience wrapper for
         * readWSDL(URL wsdlUrl, null, null, null)
         * @param wsdlUrl
         * @return a Definition object representing the WSDL
         * @throws WSDLException 
         */
	public Definition readWSDL(URL wsdlUrl) throws WSDLException {
		return readWSDL(wsdlUrl, null, null);
	}
	
	
	
	
}
