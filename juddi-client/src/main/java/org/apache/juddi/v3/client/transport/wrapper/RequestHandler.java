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
package org.apache.juddi.v3.client.transport.wrapper;

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.Holder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.jaxb.JAXBMarshaller;
import org.uddi.api_v3.AssertionStatusItem;
import org.uddi.api_v3.AssertionStatusReport;
import org.uddi.api_v3.CompletionStatus;
import org.uddi.api_v3.DispositionReport;
import org.uddi.api_v3.GetAssertionStatusReport;
import org.uddi.api_v3.GetPublisherAssertions;
import org.uddi.api_v3.PublisherAssertion;
import org.uddi.api_v3.PublisherAssertions;
import org.uddi.api_v3.PublisherAssertionsResponse;
import org.uddi.api_v3.SetPublisherAssertions;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Tom Cunningham (tcunning@apache.org)
 * @author Kurt Stam (kurt.stam@redhat.com)
 */
public class RequestHandler
{
  // private reference to the webapp's logger.
  //private static Log log = LogFactory.getLog(RequestHandler.class);
  
  // XML Document Builder
  private static DocumentBuilder docBuilder = null;
  
  private volatile String version;
  private volatile String operation;

  private volatile Remote portType; 
  private volatile String methodName;
  private volatile Class<?> operationClass;
  private static TransformerFactory transFactory = null;
  
    /**
   * Grab the local name of the UDDI request element
   * from the UDDI Request. If a value isn't returned 
   * (either null or an empty String is returned) then 
   * throw a FatalError exception. This is probably a 
   * configuration problem related to the XML Parser 
   * that jUDDI is using.
   * @param uddiReq
   * @return 
   * @throws Exception
   */
  public String getOperation(Element uddiReq) throws Exception
  {
	  if (uddiReq == null)
	  		throw new UnsupportedOperationException("UDDI Request is null");
	  
	  String operation = uddiReq.getLocalName();
      if ((operation == null) || (operation.trim().length() == 0))
	    	throw new UnsupportedOperationException("operation " + operation + " not supported");
      setOperation(operation);
      return operation;
  }
  /**
   * Grab the generic attribute value (version value).  If 
   * one isn't specified or the value specified is not "2.0" 
   * then throw an exception (this value must be specified 
   * for all UDDI requests and currently only version 2.0
   * UDDI requests are supported).
   *   
   * @param uddiReq
   * @return
   * @throws Exception
   */
  public String getVersion(Element uddiReq, String operation) throws Exception
  {
       String version = uddiReq.getAttribute("generic");
      if ((version == null) || ("".equals(version))) {
    	  if ("urn:uddi-org:api_v3".equals(uddiReq.getNamespaceURI())) {
    		  version = "3.0";
    	  }
      }
      if (!"3.0".equals(version))
	  		throw new UnsupportedOperationException("version needs to be 3.0");
      setVersion(version);
      return version;
  }
  
  public static String getText(Element element) throws TransformerException
  {
	  if (transFactory == null) {
		  transFactory = TransformerFactory.newInstance();		  
	  }
      Transformer trans = transFactory.newTransformer();
      StringWriter sw = new StringWriter();
      trans.transform(new DOMSource(element), new StreamResult(sw));
      return new String(sw.toString());
  }

  
  @SuppressWarnings("unchecked")
public Node invoke(Element uddiReq) throws Exception
  {
    Node response = null;
    // Create a new 'temp' XML element to use as a container 
    // in which to marshal the UDDI response data into.
    DocumentBuilder docBuilder = getDocumentBuilder();
    Document document = docBuilder.newDocument();
    Element element = document.createElement("temp");
    try 
    { 
      // Lookup the appropriate XML handler.  Throw an 
      // UnsupportedException if one could not be located.
      //String reqString = getText(uddiReq);
      //Object uddiReqObj = JAXBMarshaller.unmarshallFromString(reqString, "org.uddi.api_v3");
      Object uddiReqObj = JAXBMarshaller.unmarshallFromElement(uddiReq, "org.uddi.api_v3");
      Object result = null;
      if (operationClass.equals(GetAssertionStatusReport.class)) {
          GetAssertionStatusReport getAssertionStatusReport = (GetAssertionStatusReport) uddiReqObj;
          Method method = portType.getClass().getMethod(methodName, String.class, CompletionStatus.class);
          result = method.invoke(portType, getAssertionStatusReport.getAuthInfo(), getAssertionStatusReport.getCompletionStatus());
          AssertionStatusReport assertionStatusReport = new AssertionStatusReport();
          assertionStatusReport.getAssertionStatusItem().addAll((List<AssertionStatusItem>)result);
          result = assertionStatusReport;
      } else if (operationClass.equals(SetPublisherAssertions.class)) {
          SetPublisherAssertions setPublisherAssertions = (SetPublisherAssertions) uddiReqObj;
          Method method = portType.getClass().getMethod(methodName, String.class, Holder.class);
          Holder<List<PublisherAssertion>> holder = new Holder<List<PublisherAssertion>>(setPublisherAssertions.getPublisherAssertion());
          result = method.invoke(portType, setPublisherAssertions.getAuthInfo(), holder);
          PublisherAssertions assertions = new PublisherAssertions();
          if (holder.value!=null) {
              assertions.getPublisherAssertion().addAll(holder.value);
          }
          result = assertions;
      } else if (operationClass.equals(GetPublisherAssertions.class)) {
          GetPublisherAssertions getPublisherAssertions = (GetPublisherAssertions) uddiReqObj;
          Method method = portType.getClass().getMethod(methodName, String.class);
          result = method.invoke(portType, getPublisherAssertions.getAuthInfo());
          List<PublisherAssertion> assertionList = (List<PublisherAssertion>) result;
          PublisherAssertionsResponse publisherAssertionsResponse = new PublisherAssertionsResponse();
          if (assertionList!=null) {
              publisherAssertionsResponse.getPublisherAssertion().addAll(assertionList);
          }
          result = publisherAssertionsResponse;
      } else {
          Method method = portType.getClass().getMethod(methodName, operationClass);
          result = method.invoke(portType, (Object) uddiReqObj);
      }
      
      // Lookup the appropriate response handler which will
      // be used to marshal the UDDI object into the appropriate 
      // xml format.
      
      /*
      IHandler responseHandler = maker.lookup(uddiResObj.getClass().getName());
      if (responseHandler == null)
        throw new FatalErrorException("The response object " +
          "type is unknown: " +uddiResObj.getClass().getName());
      */
      
      // Lookup the appropriate response handler and marshal 
      // the juddi object into the appropriate xml format (we 
      // only support UDDI v2.0 at this time).  Attach the
      // results to the body of the SOAP response.
      if (result!=null) {
          JAXBMarshaller.marshallToElement(result, "org.uddi.api_v3", element);
       // Grab a reference to the 'temp' element's
          // only child here (this has the effect of
          // discarding the temp element) and append 
          // this child to the soap response body
          document.appendChild(element.getFirstChild());
      }
      response = document;
    } catch (Exception e) {
    	DispositionReport dr = DispositionReportFaultMessage.getDispositionReport(e);
    	if (dr != null) {
    	    JAXBMarshaller.marshallToElement(dr, "org.uddi.api_v3", element);
    	    document.appendChild(element.getFirstChild());
    	    response = document;
    	} else {
    	    throw e;
    	}
    	//log.error(e.getMessage(),e);
    }
    return response;
  }
  
  /**
   *
   */
  private DocumentBuilder getDocumentBuilder()
  {
    if (docBuilder == null)
      docBuilder = createDocumentBuilder();    
    return docBuilder;
  }

  /**
   *
   */
  private synchronized DocumentBuilder createDocumentBuilder()
  {
    if (docBuilder != null)
      return docBuilder;

    try {
     DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
     factory.setNamespaceAware(true);
     //factory.setValidating(true);

     docBuilder = factory.newDocumentBuilder();
    }
    catch(ParserConfigurationException pcex) {
      pcex.printStackTrace();
    }

    return docBuilder;
  }
public String getOperation() {
    return operation;
}
public void setOperation(String operation) {
    this.operation = operation;
}

public Remote getPortType() {
	return portType;
}
public void setPortType(Remote portType) {
	this.portType = portType;
}
public String getMethodName() {
	return methodName;
}
public void setMethodName(String methodName) {
	this.methodName = methodName;
}
public Class<?> getOperationClass() {
	return operationClass;
}
public void setOperationClass (Class<?> operationClass) {
	this.operationClass = operationClass;
}
public String getVersion() {
    return version;
}
public void setVersion(String version) {
    this.version = version;
}

}
