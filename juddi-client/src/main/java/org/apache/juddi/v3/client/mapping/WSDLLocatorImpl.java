/*
 * Copyright 2001-2011 The Apache Software Foundation.
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
 *
 */
package org.apache.juddi.v3.client.mapping;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.wsdl.xml.WSDLLocator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;

/**
 * Implementation of the interface {@link WSDLLocatorImpl}. 
 *  
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 *
 */
public class WSDLLocatorImpl implements WSDLLocator {

	private final Log log = LogFactory.getLog(this.getClass());
	private InputStream inputStream = null;
	private URI baseURI;
	private String latestImportURI;
	/**
	 * Constructor taking the URI to the WSDL. This class implements the {@link WSDLLocatorImpl}
	 * Interface.
	 * 
	 * @param baseURI - URI of the WSDL
	 */
	public WSDLLocatorImpl(URI baseURI) {
		this.baseURI = baseURI;
	}
	/**
	 * @see WSDLLocatorImpl.getBaseInputSource
	 */
	public InputSource getBaseInputSource() {
		InputSource inputSource = null;
		try {
			inputStream = baseURI.toURL().openStream();
			inputSource =new InputSource(inputStream);
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		}
		return inputSource;
	}
	/**
	 * Internal method to normalize the importUrl. The importLocation can be relative to
	 * the parentLocation. 
	 * 
	 * @param parentLocation
	 * @param importLocation
	 * @return
	 */
	protected URL constructImportUrl(String parentLocation, String importLocation) {
		URL importUrl = null;
		try {
			URI importLocationURI = new URI(importLocation);
			if (importLocationURI.getScheme()!=null || parentLocation==null) {
				importUrl = importLocationURI.toURL();
			} else {
				String parentDir = parentLocation.substring(0, parentLocation.lastIndexOf("/"));
				URI uri = new URI(parentDir + "/" + importLocation);
				importUrl = uri.normalize().toURL();
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		if (importUrl != null)
			log.debug("importUrl: " + importUrl.toExternalForm());
		else
			log.error("importUrl is null!");
		return importUrl;
	}

	/**
	 * @see WSDLLocatorImpl.getImportInputSource
	 */
	public InputSource getImportInputSource(String parentLocation, String importLocation)
	{
		InputSource inputSource = null;
		try {
			URL importUrl = constructImportUrl(parentLocation, importLocation);
			InputStream inputStream = importUrl.openStream();
			inputSource = new InputSource(inputStream);
			latestImportURI = importUrl.toExternalForm();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return inputSource;
	}
	/**
	 * @see WSDLLocatorImpl.getBaseURI
	 */
	public String getBaseURI() {
		String baseURIStr = null;
		try {
			baseURIStr = baseURI.toURL().toExternalForm();
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		}
		return baseURIStr;
	}
	/**
	 * @see WSDLLocatorImpl.getLatestImportURI
	 */
	public String getLatestImportURI() {
		return latestImportURI;
	}
	/**
	 * @see WSDLLocatorImpl.close
	 */
	public void close() {
		if (inputStream!=null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
		}
	}

}

