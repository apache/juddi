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
package org.apache.juddi.transport.axis;

import java.util.Vector;

import javax.xml.soap.Detail;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFault;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.IRegistry;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.response.DispositionReport;
import org.apache.juddi.datatype.response.ErrInfo;
import org.apache.juddi.datatype.response.Result;
import org.apache.juddi.error.BusyException;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.error.UnsupportedException;
import org.apache.juddi.handler.HandlerMaker;
import org.apache.juddi.handler.IHandler;
import org.apache.juddi.monitor.Monitor;
import org.apache.juddi.monitor.MonitorFactory;
import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.registry.RegistryServlet;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Steve Viens (sviens@apache.org)
 * @author Anou Mana (anou_mana@users.sourceforge.net)
 */
public class AxisProcessor
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(AxisProcessor.class);

  // jUDDI XML Handler maker
  private static HandlerMaker maker = HandlerMaker.getInstance();

  /**
   * @param soapResponse
   * @param messageContext
   */
  public AxisProcessor(Message soapResponse,MessageContext messageContext)
  {
    // get a new monitor from the MonitorFactory
    
    Monitor monitor = MonitorFactory.getMonitor();
    
    // if monitoring is turned on inspect the MessageContext

    if (monitor != null)
      monitor.inspectMessageContext(messageContext);

    // the soap request and response objects.

    SOAPEnvelope soapReqEnv = null;
    SOAPEnvelope soapResEnv = null;

    // grab a reference to the SOAP request from
    // the Message Context

    Message soapRequest = messageContext.getRequestMessage();

    // write the SOAP request XML out to the log (on debug)

    try { log.debug(soapRequest.getSOAPPartAsString()); }
    catch(AxisFault af) {
      af.printStackTrace();
    }

    Element request = null;
    Element response = null;
    String function = null;
    String generic = null;

    try
    {
      // pull the soap request and response objects from 
      // raw request and response message objects.

      soapReqEnv = soapRequest.getSOAPEnvelope();
      soapResEnv = soapResponse.getSOAPEnvelope();

      // pull the uddi request xml element from
      // the body of the soapRequest

      SOAPBodyElement requestBody = soapReqEnv.getFirstBody();
      request = requestBody.getAsDOM();

      // make the monitor inspect the SOAP Body
      // DOM element
      if (monitor != null)
        monitor.inspectUDDIRequest(request);

      // grab the function name from this element -
      // we'll need this to lookup the xml handler
      // to use to unmarshal the xml into a juddi
      // object.

      function = request.getLocalName();

      // grab the generic value - we'll need it in
      // the event that an exception is thrown.

      generic = request.getAttribute("generic");
      if (generic == null)
        generic = IRegistry.UDDI_V2_GENERIC;

      // lookup the appropriate xml handler, throw
      // an UnsupportedException if one could not be
      // located.

      IHandler requestHandler = maker.lookup(function);
      if (requestHandler == null)
        throw new UnsupportedException("The request " +
          "type is unknown: " +function);

      // unmarshal the raw xml into an associated
      // jUDDI request object.

      RegistryObject uddiRequest = requestHandler.unmarshal(request);

      // make the monitor inspect the UDDI request
      // object
      if (monitor != null)
        monitor.inspectRegistryObject(uddiRequest);

      // Determine if this message came from through
      // the Publish, Inquiry or Admin API and handle
      // it appropriately.

      Object juddiServlet = messageContext.getProperty("transport.http.servlet");

      // confirm that the the appropriate endpoint
      // was used to invoke the selected jUDDI/UDDI
      // function.

      if((juddiServlet instanceof InquiryServlet) &&
         (!(uddiRequest instanceof org.apache.juddi.datatype.request.Inquiry)))
      {
        throw new RegistryException("Inquiry API " +
          "does not support function: "+function);
      }
      else if (juddiServlet instanceof PublishServlet &&
         (!(uddiRequest instanceof org.apache.juddi.datatype.request.Publish) &&
          !(uddiRequest instanceof org.apache.juddi.datatype.request.SecurityPolicy)))
      {
        throw new RegistryException("Publish API " +
          "does not support function: "+function);
      }
      else if ((juddiServlet instanceof AdminServlet) &&    // Admin
         (!(uddiRequest instanceof org.apache.juddi.datatype.request.Admin)))
      {
        throw new RegistryException("Admin API " +
          "does not support function: "+function);
      }

      // grab a reference to the shared jUDDI registry
      // instance (make sure it's running) and execute 
      // the requested UDDI function.

      RegistryObject uddiResponse = null;
      
      RegistryEngine registry = RegistryServlet.getRegistry();
      if ((registry != null) && (registry.isAvailable()))
        uddiResponse = registry.execute(uddiRequest);
      else
        throw new BusyException("The Registry is unavailable");

      // create a new 'temp' XML element. This
      // element is used as a container in which
      // to marshal the UDDI response into.

      Document document = XMLUtils.createDocument();
      Element element = document.createElement("temp");

      // lookup the appropriate response handler
      // and marshal the juddi object into the
      // appropriate xml format (we only support
      // uddi v2.0 at this time) attaching results
      // to the temporary 'temp' element.

      IHandler responseHandler = maker.lookup(uddiResponse.getClass().getName());
      responseHandler.marshal(uddiResponse,element);

      // grab a reference to the 'temp' element's
      // only child here (this has the effect of
      // discarding the temp element) and appending
      // this child to the soap response body.

      response = (Element)element.getFirstChild();      
      SOAPBodyElement soapRespBody = new SOAPBodyElement(response);
      SOAPEnvelope soapRespEnv = soapResponse.getSOAPEnvelope();
      soapRespEnv.addBodyElement(soapRespBody);       
    }
    catch(Exception ex)
    {
      log.error(ex.getMessage(),ex);

      String faultCode = null;
      String faultString = null;
      String faultActor = null;      
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
        RegistryException rex = (RegistryException)ex;
        
        faultCode = rex.getFaultCode();  // SOAP Fault faultCode
        faultString = rex.getFaultString();  // SOAP Fault faultString
        faultActor = rex.getFaultActor();  // SOAP Fault faultActor
        
        if (monitor != null)
            monitor.addMonitorFault(faultString);
        
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
            errno = String.valueOf(result.getErrno());  // UDDI DispositionReport errno
            errInfo = result.getErrInfo();
          
            if (errInfo != null)
            {
              errCode = errInfo.getErrCode();  // UDDI DispositionReport errCode
              errText = errInfo.getErrMsg();  // UDDI DispositionReport errMsg
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
          
        faultCode = "Server";
        faultString = ex.getMessage();
        faultActor = null;
          
        // TODO Must lookup 'errCode' and pull 'errMsg' from the MessageBundle.
        errno = String.valueOf(Result.E_FATAL_ERROR);
        errCode = "E_fatalError";
        errText = "An internal UDDI server error has " +
                  "occurred. Please report this error " +
                  "to the UDDI server administrator.";
      }
            
      // We should have everything we need to assemble 
      // the SOAPFault so lets piece it together and 
      // send it on it's way.
              
      try {
        SOAPBody soapResBody = soapResEnv.getBody();
        SOAPFault soapFault = soapResBody.addFault();
        soapFault.setFaultCode(faultCode);
        soapFault.setFaultString(faultString);
        soapFault.setFaultActor(faultActor);
        
        Detail faultDetail = soapFault.addDetail();
        
        SOAPElement dispRpt = faultDetail.addChildElement("dispositionReport","",IRegistry.UDDI_V2_NAMESPACE);        
        dispRpt.setAttribute("generic",IRegistry.UDDI_V2_GENERIC);
        dispRpt.setAttribute("operator",Config.getOperator());
        
        SOAPElement result = dispRpt.addChildElement("result");
        result.setAttribute("errno",errno);
        
        SOAPElement errInfo = result.addChildElement("errInfo");
        errInfo.setAttribute("errCode",errCode);
        errInfo.setValue(errText);
      } 
      catch (Exception e) { 
        e.printStackTrace(); 
      }
    }
    finally
    {
      // write the monitored information to the currently
      // configured 'Monitor' implemented registry (the
      // default Monitor implementation writes the monitored
      // information to a database table via JDBC).

      if (monitor != null)
        monitor.log();
    }

    // write the SOAP response XML out to the log (on debug)
    try { log.debug(soapResponse.getSOAPPartAsString()); }
    catch(AxisFault af) {
      af.printStackTrace();
    }
  }
}
