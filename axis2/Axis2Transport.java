/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
 */
package org.apache.juddi.proxy;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.util.StAXUtils;
//import org.apache.axiom.soap.SOAP11Constants;
//import org.apache.axiom.soap.SOAP12Constants;
//import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.util.XMLUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.error.RegistryException;
import org.w3c.dom.Element;

/**
 * @author Jeff Faath (jeff@esigma.com)
 * 
 * Transport class for jUDDI that works with Axis2
 * 
 * TODO:  Possibly use a static ServiceClient as it takes the longest to instantiate and doesn't really need to be remade every call. 
 */
public class Axis2Transport implements Transport {
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(Axis2Transport.class);

  public Element send(Element request,URL endpointURL) throws RegistryException {    

	  ServiceClient sender = null;
	  OMElement responseElem = null;
	  Element response = null;

	  try {log.debug("\nRequest message:\n" + doXmlToString(request));} catch(Exception e) {e.printStackTrace();}

	  try {

		  Options options = new Options();
          options.setTo(new EndpointReference(endpointURL.toExternalForm()));

          //options.setSoapVersionURI(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
          //options.setSoapVersionURI(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
          //options.setProperty(HTTPConstants.CHUNKED, "false");
          
          sender = new ServiceClient();
          sender.setOptions(options);
          
          OMElement bodyElemPayload = XMLUtils.toOM(request); 
          responseElem = (OMElement)sender.sendReceive(bodyElemPayload);
          
          if (responseElem != null)
        	  response = XMLUtils.toDOM(responseElem);
        	  
	  }
	  catch (AxisFault fault) {
		  fault.printStackTrace();
	  }
	  catch (Exception ex) {
		  throw new RegistryException(ex);
	  }
	  try {log.debug("\nResponse message:\n" + doXmlToString(response));} catch(Exception e) {e.printStackTrace();}

	  return response;
  }
  
  public String send(String request,URL endpointURL) throws RegistryException {    
	  ServiceClient sender = null;
	  OMElement responseElem = null;
	  String response = null;

	  log.debug("\nRequest message:\n" + request);

	  try {
		  Options options = new Options();
          options.setTo(new EndpointReference(endpointURL.toExternalForm()));

          //options.setSoapVersionURI(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
          //options.setSoapVersionURI(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
          //options.setProperty(HTTPConstants.CHUNKED, "false");
          
          sender = new ServiceClient();
          sender.setOptions(options);
          
          ByteArrayInputStream is = new ByteArrayInputStream(request!=null?request.getBytes():null);
          XMLStreamReader reader = StAXUtils.createXMLStreamReader(is);
          StAXOMBuilder builder = new StAXOMBuilder(reader);
          builder.setCache(true);
          
          OMElement bodyElemPayload = builder.getDocumentElement();
          responseElem = (OMElement)sender.sendReceive(bodyElemPayload);
          
          if (responseElem != null)
        	  response = doXmlToString(XMLUtils.toDOM(responseElem));

	  }
	  catch (AxisFault fault) {
		  fault.printStackTrace();
	  }
	  catch (Exception ex) {
		  throw new RegistryException(ex);
	  }

	  log.debug("\nResponse message:\n" + response);

	  return response;

  }

	public String doXmlToString(Element elem) throws TransformerConfigurationException, TransformerException {
        String resultString = null;

        if (elem != null) {
	        // Prepare the DOM document for writing
	        Source source = new DOMSource(elem);
	
	        StringWriter sw = new StringWriter();
	        StreamResult result = new StreamResult(sw);
	
	        // Write the DOM document to the file
	        Transformer xformer = TransformerFactory.newInstance().newTransformer();
	        xformer.transform(source, result);
	        
	        if (result != null) {
	        	resultString = result.getWriter().toString();
	        }
        }
        return resultString;
    }	

}
