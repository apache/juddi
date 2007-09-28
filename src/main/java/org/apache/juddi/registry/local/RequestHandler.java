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
package org.apache.juddi.registry.local;

import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.response.DispositionReport;
import org.apache.juddi.datatype.response.ErrInfo;
import org.apache.juddi.datatype.response.Result;
import org.apache.juddi.error.BusyException;
import org.apache.juddi.error.FatalErrorException;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.error.UnsupportedException;
import org.apache.juddi.handler.HandlerMaker;
import org.apache.juddi.handler.IHandler;
import org.apache.juddi.registry.RegistryEngine;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Kurt Stam (kurt.stam@redhat.com)
 */
public class RequestHandler implements Runnable
{
  // private reference to the webapp's logger.
  private static Log log = LogFactory.getLog(RequestHandler.class);
  
  // XML Document Builder
  private static DocumentBuilder docBuilder = null;
  
  private String version;
  private String operation;
  private Element uddiReq;
  private Node response;
  private String exception;
   
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
          throw new FatalErrorException("A UDDI request was not " +
            "found in the SOAP message.");

      String operation = uddiReq.getLocalName();
      if ((operation == null) || (operation.trim().length() == 0))
        throw new FatalErrorException("The UDDI service operation " +
          "could not be identified.");
      setOperation(operation);
      return operation;
  }
  /**
   * Grab the generic attribute value (version value).  If 
   * one isn't specified or the value specified is not "2.0" 
   * then throw an exception (this value must be specified 
   * for all UDDI requests and currently only vesion 2.0
   * UDDI requests are supported).
   *   
   * @param uddiReq
   * @return
   * @throws Exception
   */
  public String getVersion(Element uddiReq, String operation) throws Exception
  {
      String version = uddiReq.getAttribute("generic");
      if (version == null)
        throw new FatalErrorException("A UDDI generic attribute " +
          "value was not found for UDDI request: "+operation+" (The " +
          "'generic' attribute must be present)");
      setVersion(version);
      return version;
  }
  
  public void run()
  {
    try 
    { 
      // Lookup the appropriate XML handler.  Throw an 
      // UnsupportedException if one could not be located.
      HandlerMaker maker = HandlerMaker.getInstance();
			IHandler requestHandler = maker.lookup(operation);
      if (requestHandler == null)
        throw new UnsupportedException("The UDDI service operation " +
          "specified is unknown or unsupported: " +operation);
      
      // Unmarshal the raw xml into the appropriate jUDDI
      // request object.

      RegistryObject uddiReqObj = requestHandler.unmarshal(uddiReq);
      
      // Grab a reference to the shared jUDDI registry 
      // instance (make sure it's running) and execute the 
      // requested UDDI function.
      
      RegistryObject uddiResObj = null;      
      RegistryEngine registry = Registry.getRegistry();
      if ((registry != null) && (registry.isAvailable()))
        uddiResObj = registry.execute(uddiReqObj);
      else
        throw new BusyException("The Registry is currently unavailable.");
      
      // Lookup the appropriate response handler which will
      // be used to marshal the UDDI object into the appropriate 
      // xml format.
      
      IHandler responseHandler = maker.lookup(uddiResObj.getClass().getName());
      if (responseHandler == null)
        throw new FatalErrorException("The response object " +
          "type is unknown: " +uddiResObj.getClass().getName());
      
      // Create a new 'temp' XML element to use as a container 
      // in which to marshal the UDDI response data into.
        
      DocumentBuilder docBuilder = getDocumentBuilder();
      Document document = docBuilder.newDocument();
      Element element = document.createElement("temp");
      
      // Lookup the appropriate response handler and marshal 
      // the juddi object into the appropriate xml format (we 
      // only support UDDI v2.0 at this time).  Attach the
      // results to the body of the SOAP response.
        
      responseHandler.marshal(uddiResObj,element);
      
      // Grab a reference to the 'temp' element's
      // only child here (this has the effect of
      // discarding the temp element) and append 
      // this child to the soap response body
      document.appendChild(element.getFirstChild());
      setResponse(document);
    } 
    catch(Exception ex) // Catch ALL exceptions
    {
      // SOAP Fault values
      String faultCode = null;
      String faultString = null;
      String faultActor = null;
      
      // UDDI DispositionReport values
      String errno = null;
      String errCode = null;
      String errText = null;
      
      // All RegistryException and subclasses of RegistryException
      // should contain values for populating a SOAP Fault as well
      // as a UDDI DispositionReport with specific information 
      // about the problem.
      //
      // We've got to dig out the dispositionReport and populate  
      // the SOAP Fault 'detail' element with this information.        
      
      if (ex instanceof RegistryException)
      {
        // Since we've intercepted a RegistryException type
        // then we can assume this is a "controlled" event
        // and simply log the error message without a stack
        // trace.

        log.error(ex.getMessage());

        RegistryException rex = (RegistryException)ex;
        
        faultCode = rex.getFaultCode();  // SOAP Fault faultCode
        faultString = rex.getFaultString();  // SOAP Fault faultString
        faultActor = rex.getFaultActor();  // SOAP Fault faultActor
        
        DispositionReport dispRpt = rex.getDispositionReport();
        if (dispRpt != null)
        {
          Result result = null;
          ErrInfo errInfo = null;
        
          Vector results = dispRpt.getResultVector();
          if ((results != null) && (!results.isEmpty()))
            result = (Result)results.elementAt(0);
        
          if (result != null)
          {
            errno = String.valueOf(result.getErrno());  // UDDI Result errno
            errInfo = result.getErrInfo();
          
            if (errInfo != null)
            {
              errCode = errInfo.getErrCode();  // UDDI ErrInfo errCode
              errText = errInfo.getErrMsg();  // UDDI ErrInfo errMsg
            }
          }
        }
      }
      else if (ex instanceof SOAPException)
      {
        log.error(ex.getMessage());
          
        // Because something occured that jUDDI wasn't expecting
        // let's set default SOAP Fault values.  Since SOAPExceptions
        // here are most likely XML validation errors let's blame the
        // client by placing "Client" in the Fault Code and pass
        // the Exception message back to the client.
        
        faultCode = "Client";
        faultString = ex.getMessage();
        faultActor = null;
        
        // Let's set default values for the UDDI DispositionReport
        // here.  While we didn't catch a RegistryException (or 
        // subclass) we're going to be friendly and include a
        // FatalError DispositionReport within the message from the 
        // SAX parsing problem in the SOAP Fault anyway.
        
        errno = String.valueOf(Result.E_FATAL_ERROR);
        errCode = Result.lookupErrCode(Result.E_FATAL_ERROR); 
        errText = Result.lookupErrText(Result.E_FATAL_ERROR) + 
                  " " + ex.getMessage();
      }
      else // anything else
      {
        // All other exceptions (other than SOAPException or 
        // RegistryException and subclasses) are either a result 
        // of a jUDDI configuration problem or something that 
        // we *should* be catching and converting to a 
        // RegistryException but are not (yet!).
            
        log.error(ex.getMessage(),ex);

        // Because something occured that jUDDI wasn't expecting
        // let's set default SOAP Fault values.  Since jUDDI
        // should be catching anything significant let's blame 
        // jUDDI by placing "Server" in the Fault Code and pass
        // the Exception message on to the client.
        
        faultCode = "Server";
        faultString = ex.getMessage();
        faultActor = null;
          
        // Let's set default values for the UDDI DispositionReport
        // here.  While we didn't catch a RegistryException (or 
        // subclass) but we're going to be friendly and include a
        // FatalError DispositionReport within the SOAP Fault anyway.
        
        errno = String.valueOf(Result.E_FATAL_ERROR);
        errCode = Result.lookupErrCode(Result.E_FATAL_ERROR); 
        errText = Result.lookupErrText(Result.E_FATAL_ERROR) +
                  " An internal UDDI server error has " +
                  "occurred. Please report this error " +
                  "to the UDDI server administrator.";
      }
      
      // We should have everything we need to assemble 
      // the SOAPFault so lets piece it together and 
      // send it on it's way.
      String fault = "faultCode=" + faultCode + ", faultString=" + faultString 
  	+ ", faultActor=" + faultActor + ", errno=" + errno + ", errCode=" + errCode
  	+ ", errText=" + errText;
      setException(fault);
    }
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
public Node getResponse() {
    return response;
}
public void setResponse(Node response) {
    this.response = response;
}
public Element getUddiReq() {
    return uddiReq;
}
public void setUddiReq(Element uddiReq) {
    this.uddiReq = uddiReq;
}
public String getVersion() {
    return version;
}
public void setVersion(String version) {
    this.version = version;
}
public String getException() {
    return exception;
}
public void setException(String exception) {
    this.exception = exception;
}
}