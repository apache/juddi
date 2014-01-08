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
package org.apache.juddi.v3.client.mapping.wsdl;

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
import java.io.File;
import java.net.MalformedURLException;

/**
 * A WSDL parser/reader
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a> - Modified for supporting http based credentials
 */
public class ReadWSDL {
	
        private boolean IgnoreSSLErrors = false;
	private final Log log = LogFactory.getLog(this.getClass());
	
	public Definition readWSDL(String fileName) throws WSDLException  {
                Definition wsdlDefinition = null;
                WSDLFactory factory = WSDLFactoryImpl.newInstance();
                WSDLReader reader = factory.newWSDLReader();

                try {
                        File f = new File(fileName);
                        URL url = null;
                        if (f.exists()) {
                                url = f.toURI().toURL();
                        } else {
                                url = ClassUtil.getResource(fileName, this.getClass());
                        }
                        if (url==null)
                                throw new WSDLException("null input", fileName);
                        URI uri = url.toURI();
                        WSDLLocator locator = new WSDLLocatorImpl(uri);
                        wsdlDefinition = reader.readWSDL(locator);
                } catch (URISyntaxException e) {
                        log.error(e.getMessage(), e);
                } catch (MalformedURLException ex) {
                        log.error(ex.getMessage(), ex);
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
        public Definition readWSDL(URL wsdlUrl, String username, String password) throws WSDLException, Exception {
	
		Definition wsdlDefinition = null;
		WSDLFactory factory = WSDLFactoryImpl.newInstance();
		WSDLReader reader = factory.newWSDLReader();
                URI uri=null;
		try {
                        uri = wsdlUrl.toURI();
                } catch (Exception e) {
			log.error(e.getMessage(),e);
                        throw new WSDLException("Unable to parse the URL", null, e);
		}
		WSDLLocatorImpl locator = new WSDLLocatorImpl(uri, username, password, IgnoreSSLErrors );
                try{
			wsdlDefinition = reader.readWSDL(locator);
		} catch (Exception e) {
                        log.error(e.getMessage(),e);
                        if (locator.getLastException()!=null)
                        {
                            log.error(e.getMessage(),locator.getLastException());
                            throw locator.getLastException();
                        }
                        throw e;
                        //throw new WSDLException("Error loading from " + wsdlUrl.toString(), null, e);
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
	public Definition readWSDL(URL wsdlUrl) throws Exception {
		return readWSDL(wsdlUrl, null, null);
	}

        /**
         * It is optional to ignore SSL errors when attempting to parse a remote WSDL via https
         * @return true if we are ignoring SSL errors
         */
    public boolean isIgnoreSSLErrors() {
        return IgnoreSSLErrors;
    }

    /**
     * It is optional to ignore SSL errors when attempting to parse a remote WSDL via https
     * @param IgnoreSSLErrors 
     */
    public void setIgnoreSSLErrors(boolean IgnoreSSLErrors) {
        this.IgnoreSSLErrors = IgnoreSSLErrors;
    }
	
	
	
	
}
