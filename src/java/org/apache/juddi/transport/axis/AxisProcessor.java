/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "jUDDI" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.juddi.transport.axis;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.response.DispositionReport;
import org.apache.juddi.error.BusyException;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.error.UnsupportedException;
import org.apache.juddi.handler.DispositionReportHandler;
import org.apache.juddi.handler.HandlerMaker;
import org.apache.juddi.handler.IHandler;
import org.apache.juddi.monitor.Monitor;
import org.apache.juddi.monitor.MonitorFactory;
import org.apache.juddi.registry.Registry;
import org.apache.juddi.registry.RegistryEngine;
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

  // jUDDI Monitor switch
  private static boolean useMonitor = Config.getBooleanProperty("juddi.useMonitor",false);


  /**
   * @param soapResponse
   * @param messageContext
   */
  public AxisProcessor(Message soapResponse,MessageContext messageContext)
  {
    // get a new monitor from the MonitorFactory and
    // inspect the MessageContext

    Monitor monitor = null;
    if (useMonitor == true)
      monitor = MonitorFactory.getMonitor();

    if (monitor != null)
      monitor.inspectMessageContext(messageContext);

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
      // pull the uddi request xml element from
      // the body of the soapRequest

      SOAPEnvelope env = soapRequest.getSOAPEnvelope();
      SOAPBodyElement requestBody = env.getFirstBody();
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
        generic = Registry.UDDI_V2_GENERIC;

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

      // grab a reference to the uddi registry
      // instance (make sure it's running) and
      // execute the requested uddi function.

      RegistryObject uddiResponse = null;
      RegistryEngine registry = RegistryEngine.getInstance();
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
    }
    catch(RegistryException rex)
    {
      log.error(rex.getMessage(),rex);

      String fCode = rex.getFaultCode();
      String fString = rex.getFaultString();
      String fActor = rex.getFaultActor();

      DispositionReport dispRpt = rex.getDispositionReport();
      if (dispRpt != null)
      {
        dispRpt.setGeneric(generic);
        dispRpt.setOperator(Config.getOperator());
      }

      if (monitor != null)
        monitor.addMonitorFault(fString);

      response = createFault(fCode,fString,fActor,dispRpt);
    }
    catch(AxisFault axf)
    {
      log.error(axf.getMessage(),axf);

      String fCode = String.valueOf(axf.getFaultCode());
      String fString = axf.getFaultString();
      String fActor = axf.getFaultActor();

      if (monitor != null)
        monitor.addMonitorFault(fString);

      response = createFault(fCode,fString,fActor,null);
    }
    catch(Exception ex)
    {
      log.error(ex.getMessage(),ex);

      String fCode = null;
      String fString = ex.getMessage();
      String fActor = null;

      if (monitor != null)
        monitor.addMonitorFault(fString);

      response = createFault(fCode,fString,fActor,null);
    }
    finally
    {
      // write the monitored information to the currently
      // configured 'Monitor' implemneted registry (the
      // default Monitor implementation writes the monitored
      // information to a database table via JDBC).

      if (monitor != null)
        monitor.log();
    }

    try {
      SOAPBodyElement soapRespBody = new SOAPBodyElement(response);
      SOAPEnvelope soapRespEnv = soapResponse.getSOAPEnvelope();
      soapRespEnv.addBodyElement(soapRespBody);
    }
    catch(AxisFault af) {
      af.printStackTrace();
    }

    // write the SOAP response XML out to the log (on debug)
    try { log.debug(soapResponse.getSOAPPartAsString()); }
    catch(AxisFault af) {
      af.printStackTrace();
    }

  }

  private static Element createFault(String fCode,String fString,String fActor,DispositionReport dispRpt)
  {
    // create a new 'Fault' XML element.

    Document document = XMLUtils.createDocument();
    Element fault = document.createElement("Fault");

    if (fCode != null)
    {
      Element fCodeElement = document.createElement("faultcode");
      fCodeElement.appendChild(document.createTextNode(fCode));
      fault.appendChild(fCodeElement);
    }

    if (fString == null)
      fString = "";

    Element fStringElement = document.createElement("faultstring");
    fStringElement.appendChild(document.createTextNode(fString));
    fault.appendChild(fStringElement);

    if (fActor != null)
    {
      Element fActorElement = document.createElement("faultactor");
      fActorElement.appendChild(document.createTextNode(fActor));
      fault.appendChild(fActorElement);
    }

    // check for a DispositionReport in the exception and if one exists,
    // grab it, marshal it into xml and stuff it into a SOAP fault
    // detail element.

    if (dispRpt != null)
    {
      Element fDetailElement = document.createElement("detail");
      IHandler handler = maker.lookup(DispositionReportHandler.TAG_NAME);
      handler.marshal(dispRpt,fDetailElement);
      fault.appendChild(fDetailElement);
    }

    return fault;
  }
}
