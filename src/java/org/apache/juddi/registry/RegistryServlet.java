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
package org.apache.juddi.registry;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Detail;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.IRegistry;
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
import org.apache.juddi.util.Config;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This servlet is ONLY used to initialize the jUDDI webapp on
 * startup and cleanup the jUDDI webapp on shutdown.
 * 
 * @author Steve Viens (sviens@apache.org)
 */
public class RegistryServlet extends HttpServlet
{
  // config file property name (used to look up the juddi property file name).
  private static final String CONFIG_FILE_PROPERTY_NAME = "juddi.propertiesFile";
  
  // default config file name. 
  private static final String DEFAULT_PROPERTY_FILE = "/WEB-INF/juddi.properties";
   
  // private reference to the webapp's logger.
  private static Log log = LogFactory.getLog(RegistryServlet.class);
  
  // jUDDI XML Handler maker
  private static HandlerMaker maker = HandlerMaker.getInstance();
  
  // XML Document Builder
  private static DocumentBuilder docBuilder = null;

  // registry singleton instance
  private static RegistryEngine registry = null;

  /**
   * Create the shared instance of jUDDI's Registry class
   * and call it's "init()" method to initialize all core 
   * components.
   */
  public void init(ServletConfig config) 
    throws ServletException
  {
    super.init(config);    

    Properties props = new Properties();

    try
    {      
      log.info("Loading jUDDI configuration.");
        
      // determine the name of the juddi property file to use from web.xml
      String propFile = config.getInitParameter(CONFIG_FILE_PROPERTY_NAME);
      if ((propFile == null) || (propFile.trim().length() == 0))
        propFile = DEFAULT_PROPERTY_FILE;
      
      InputStream is = 
        getServletContext().getResourceAsStream(propFile);
        
      if (is != null)
      {
        log.info("Resources loaded from: "+propFile);

        // Load jUDDI configuration from the 
        // juddi.properties file found in the 
        // WEB-INF directory.

        props.load(is);
      }
      else
      {
        log.warn("Could not locate jUDDI properties '" + propFile + 
                "'. Using defaults.");

        // A juddi.properties file doesn't exist
        // yet so create create a new Properties 
        // instance using default property values.
        
        props.put(RegistryEngine.PROPNAME_OPERATOR_NAME,
                  RegistryEngine.DEFAULT_OPERATOR_NAME);
        
        props.put(RegistryEngine.PROPNAME_OPERATOR_URL,
                  RegistryEngine.DEFAULT_OPERATOR_URL);
        
        props.put(RegistryEngine.PROPNAME_ADMIN_EMAIL_ADDRESS,
                  RegistryEngine.DEFAULT_ADMIN_EMAIL_ADDRESS);
        
        props.put(RegistryEngine.PROPNAME_DATASOURCE_NAME,
                  RegistryEngine.DEFAULT_DATASOURCE_NAME);
              
        props.put(RegistryEngine.PROPNAME_AUTH_CLASS_NAME,
                  RegistryEngine.DEFAULT_AUTH_CLASS_NAME);
        
        props.put(RegistryEngine.PROPNAME_CRYPTOR_CLASS_NAME,
                  RegistryEngine.DEFAULT_CRYPTOR_CLASS_NAME);
        
        props.put(RegistryEngine.PROPNAME_DATASTORE_CLASS_NAME,
                  RegistryEngine.DEFAULT_DATASTORE_CLASS_NAME);
        
        props.put(RegistryEngine.PROPNAME_MONITOR_CLASS_NAME,
                  RegistryEngine.DEFAULT_MONITOR_CLASS_NAME);
        
        props.put(RegistryEngine.PROPNAME_UUIDGEN_CLASS_NAME,
                  RegistryEngine.DEFAULT_UUIDGEN_CLASS_NAME);
        
        props.put(RegistryEngine.PROPNAME_VALIDATOR_CLASS_NAME,
                  RegistryEngine.DEFAULT_VALIDATOR_CLASS_NAME);  

        props.put(RegistryEngine.PROPNAME_MAX_NAME_ELEMENTS,
                  Integer.toString(RegistryEngine.DEFAULT_MAX_NAME_ELEMENTS));
        
        props.put(RegistryEngine.PROPNAME_MAX_NAME_LENGTH,
                  Integer.toString(RegistryEngine.DEFAULT_MAX_NAME_LENGTH));

        props.put(RegistryEngine.PROPNAME_MAX_MESSAGE_SIZE,
                  Integer.toString(RegistryEngine.DEFAULT_MAX_MESSAGE_SIZE));        

        props.put(RegistryEngine.PROPNAME_MAX_BUSINESS_ENTITIES_PER_USER,
                  Integer.toString(RegistryEngine.DEFAULT_MAX_BUSINESS_ENTITIES_PER_USER));
        
        props.put(RegistryEngine.PROPNAME_MAX_BUSINESS_SERVICES_PER_BUSINESS,
                  Integer.toString(RegistryEngine.DEFAULT_MAX_BUSINESS_SERVICES_PER_BUSINESS));
        
        props.put(RegistryEngine.PROPNAME_MAX_BINDING_TEMPLATES_PER_SERVICE,
                  Integer.toString(RegistryEngine.DEFAULT_MAX_BINDING_TEMPLATES_PER_SERVICE));
        
        props.put(RegistryEngine.PROPNAME_MAX_TMODELS_PER_USER,
                  Integer.toString(RegistryEngine.DEFAULT_MAX_TMODELS_PER_USER));
        
        props.put(RegistryEngine.PROPNAME_MAX_ROWS_LIMIT,           
                  Integer.toString(RegistryEngine.DEFAULT_MAX_ROWS_LIMIT));
      }
    }
    catch(IOException ioex) {
      log.error(ioex.getMessage(),ioex);
    }

    log.info("Initializing jUDDI components.");
    
    registry = new RegistryEngine(props);
    registry.init();
  }

  /**
   *
   */
  public void doGet(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException
  {
    res.setHeader("Allow","POST");
    res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,"The request " +
      "method 'GET' is not allowed by the UDDI Inquiry API.");
  }

  /**
   *
   */
  public void doPost(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException
  {
    res.setContentType("text/xml; charset=utf-8");
      
    SOAPMessage soapReq = null;
    SOAPMessage soapRes = null;

    try 
    {
      // Create a MessageFactory, parse the XML found
      // in the HTTP payload into a SOAP request message 
      // and create a new SOAP response message.

      MessageFactory msgFactory = MessageFactory.newInstance();
      soapReq = msgFactory.createMessage(null,req.getInputStream());
      soapRes = msgFactory.createMessage();
           
      // Extract the UDDI request from the SOAPBody
      // of the SOAP Request message. If a UDDI request
      // message does not exist within the SOAP Body
      // then throw a FatalErrorException and indicate
      // that this is a Client-side (consumer) error.

      SOAPBody soapReqBody = soapReq.getSOAPBody();
      Element uddiReq = (Element)soapReqBody.getFirstChild();
      if (uddiReq == null)
        throw new FatalErrorException("A UDDI request was not " +
          "found in the SOAP message.");
      
      // Grab the local name of the UDDI request element
      // from the UDDI Request. If a value isn't returned 
      // (either null or an empty String is returned) then 
      // throw a FatalError exception. This is probably a 
      // configuration problem related to the XML Parser 
      // that jUDDI is using.
      
      String function = uddiReq.getLocalName();
      if ((function == null) || (function.trim().length() == 0))
        throw new FatalErrorException("The name of the UDDI request " +
          "could not be identified.");
      
      // Lookup the appropriate XML handler.  Throw an 
      // UnsupportedException if one could not be located.

      IHandler requestHandler = maker.lookup(function);
      if (requestHandler == null)
        throw new UnsupportedException("The UDDI request " +
          "type specified is unknown: " +function);
      
      // Grab the generic attribute value.  If one isn't 
      // specified or the value specified is not "2.0" then 
      // throw an exception (this value must be specified 
      // for all UDDI requests and currently only vesion 2.0
      // UDDI requests are supported).

      String generic = uddiReq.getAttribute("generic");
      if (generic == null)
        throw new FatalErrorException("A UDDI generic attribute " +
          "value was not found for UDDI request: "+function+" (The " +
          "'generic' attribute must be present)");
      else if (!generic.equals(IRegistry.UDDI_V2_GENERIC))
        throw new UnsupportedException("Currently only UDDI v2 " +
          "requests are supported. The generic attribute value " +
          "received was: "+generic);

      // Unmarshal the raw xml into the appropriate jUDDI
      // request object.

      RegistryObject uddiReqObj = requestHandler.unmarshal(uddiReq);
      
      // Grab a reference to the shared jUDDI registry 
      // instance (make sure it's running) and execute the 
      // requested UDDI function.
      
      RegistryObject uddiResObj = null;      
      RegistryEngine registry = RegistryServlet.getRegistry();
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
      log.debug(XMLUtils.toString((Element)element.getFirstChild()));
      
      // Grab a reference to the 'temp' element's
      // only child here (this has the effect of
      // discarding the temp element) and append 
      // this child to the soap response body
      
      document.appendChild(element.getFirstChild());
      soapRes.getSOAPBody().addDocument(document);
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
      String errMsg = null;
      
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
              errMsg = errInfo.getErrMsg();  // UDDI ErrInfo errMsg
            }
          }
        }
      }
      else
      {
        // All other exceptions (other than RegistryException
        // and subclasses) are either a result of a jUDDI 
        // configuration problem or something that we *should* 
        // be catching and converting to a RegistryException 
        // but are not (yet!).
            
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
        errCode = Result.E_FATAL_ERROR_CODE;
        errMsg = "An internal UDDI server error has " +
                 "occurred. Please report this error " +
                 "to the UDDI server administrator.";
      }
      
      // We should have everything we need to assemble 
      // the SOAPFault so lets piece it together and 
      // send it on it's way.
      
      try {
        SOAPBody soapResBody = soapRes.getSOAPBody();     
        SOAPFault soapFault = soapResBody.addFault();
        soapFault.setFaultCode(faultCode);
        soapFault.setFaultString(faultString);
        soapFault.setFaultActor(faultActor);
        
        // We're always going to include a DispositionReport (for
        // the hell of it) so that's what we're doing here.
       
        Detail faultDetail = soapFault.addDetail();
        
        SOAPElement dispRpt = faultDetail.addChildElement("dispositionReport","",IRegistry.UDDI_V2_NAMESPACE);        
        dispRpt.setAttribute("generic",IRegistry.UDDI_V2_GENERIC);
        dispRpt.setAttribute("operator",Config.getOperator());
        
        SOAPElement result = dispRpt.addChildElement("result");
        result.setAttribute("errno",errno);
        
        SOAPElement errInfo = result.addChildElement("errInfo");
        errInfo.setAttribute("errCode",errCode);
        errInfo.setValue(errMsg);
      } 
      catch (Exception e) { // if we end up in here it's just NOT good.
        log.error("A serious error has occured while assembling the SOAP Fault.",e);
      }
    }
    finally 
    {
      try {               
        soapRes.writeTo(res.getOutputStream());     
      }
      catch(SOAPException sex) {
        log.error(sex);
      }
    }
  }
  
  /**
   * Grab the shared instance of jUDDI's Registry class and
   * call it's "dispose()" method to notify all sub-components
   * to stop any background threads and release any external
   * resources they may have aquired.
   */
  public void destroy()
  {
    super.destroy();
    
    log.info("jUDDI Stopping: Cleaning up existing resources.");

    RegistryEngine registry = RegistryServlet.getRegistry();
    if (registry != null)
      registry.dispose();
  }

  /**
   *
   */
  public static RegistryEngine getRegistry()
  {
    return registry;
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
     //factory.setNamespaceAware(true);
     //factory.setValidating(true);

     docBuilder = factory.newDocumentBuilder();
    }
    catch(ParserConfigurationException pcex) {
      pcex.printStackTrace();
    }

    return docBuilder;
  }
}