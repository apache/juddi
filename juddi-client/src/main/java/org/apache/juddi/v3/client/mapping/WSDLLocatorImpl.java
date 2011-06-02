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
 */
package org.apache.juddi.v3.client.mapping;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.wsdl.xml.WSDLLocator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;


public class WSDLLocatorImpl implements WSDLLocator {
	
	  private final Log log = LogFactory.getLog(this.getClass());
      private URL wsdlURL;
      private String latestImportURI;
      private InputStream is = null;

      public WSDLLocatorImpl(URL wsdlFile)
      {
         if (wsdlFile == null)
            throw new IllegalArgumentException("WSDL file argument cannot be null");

         this.wsdlURL = wsdlFile;
      }

      public InputSource getBaseInputSource()
      {
         log.debug("getBaseInputSource [wsdlUrl=" + wsdlURL + "]");
         try
         {
            is = wsdlURL.openStream();
            if (is == null)
               throw new IllegalArgumentException("Cannot obtain wsdl from [" + wsdlURL + "]");

            return new InputSource(is);
         }
         catch (IOException e)
         {
            throw new RuntimeException("Cannot access wsdl from [" + wsdlURL + "], " + e.getMessage());
         }
      }

      public String getBaseURI()
      {
         return wsdlURL.toExternalForm();
      }

      public InputSource getImportInputSource(String parent, String resource)
      {
         log.debug("getImportInputSource [parent=" + parent + ",resource=" + resource + "]");

         URL parentURL = null;
         try
         {
            parentURL = new URL(parent);
         }
         catch (MalformedURLException e)
         {
            log.error("Not a valid URL: " + parent);
            return null;
         }

         String wsdlImport = null;
         String external = parentURL.toExternalForm();

         // An external URL
         if (resource.startsWith("http://") || resource.startsWith("https://"))
         {
            wsdlImport = resource;
         }

         // Absolute path
         else if (resource.startsWith("/"))
         {
            String beforePath = external.substring(0, external.indexOf(parentURL.getPath()));
            wsdlImport = beforePath + resource;
         }

         // A relative path
         else
         {
            String parentDir = external.substring(0, external.lastIndexOf("/"));

            // remove references to current dir
            while (resource.startsWith("./"))
               resource = resource.substring(2);

            // remove references to parentdir
            while (resource.startsWith("../"))
            {
               parentDir = parentDir.substring(0, parentDir.lastIndexOf("/"));
               resource = resource.substring(3);
            }

            wsdlImport = parentDir + "/" + resource;
         }

         try
         {
            log.debug("Resolved to: " + wsdlImport);
            InputStream is = new URL(wsdlImport).openStream();
            if (is == null)
               throw new IllegalArgumentException("Cannot import wsdl from [" + wsdlImport + "]");

            latestImportURI = wsdlImport;
            return new InputSource(is);
         }
         catch (IOException e)
         {
            throw new RuntimeException("Cannot access imported wsdl [" + wsdlImport + "], " + e.getMessage());
         }
      }

      public String getLatestImportURI()
      {
         return latestImportURI;
      }

	public void close() {
		if (is!=null)
			try {
				is.close();
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
		
	}
   }

