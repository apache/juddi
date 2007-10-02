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
import java.net.URL;
import java.util.Vector;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.error.RegistryException;
import org.w3c.dom.Element;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class AxisTransport implements Transport
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(AxisTransport.class);

  public Element send(Element request,URL endpointURL)
    throws RegistryException
  {    
    Service service = null;
    Call call = null;
    Element response = null;

    log.debug("\nRequest message:\n" + XMLUtils.ElementToString(request));

    try {
      service = new Service();
      call = (Call)service.createCall();
      call.setTargetEndpointAddress(endpointURL);

      String requestString = XMLUtils.ElementToString(request);
      SOAPBodyElement body = new SOAPBodyElement(new ByteArrayInputStream(requestString.getBytes("UTF-8")));
      Object[] soapBodies = new Object[] { body };

      Vector result = (Vector)call.invoke(soapBodies);
      response = ((SOAPBodyElement)result.elementAt(0)).getAsDOM();
    }
    catch (AxisFault fault) {

      fault.printStackTrace();

      try {
        Message msg = call.getResponseMessage();
        response = msg.getSOAPEnvelope().getFirstBody().getAsDOM();
      }
      catch (Exception ex) {
        throw new RegistryException(ex);
      }
    }
    catch (Exception ex) {
      throw new RegistryException(ex);
    }

    log.debug("\nResponse message:\n" + XMLUtils.ElementToString(response));


    return response;
  }
  
  public String send(String request,URL endpointURL)
    throws RegistryException
  {    
    Service service = null;
    Call call = null;
    String response = null;

    log.debug("\nRequest message:\n" + request);

    try {
        
      service = new Service();
      call = (Call)service.createCall();
      call.setTargetEndpointAddress(endpointURL);
    
      SOAPBodyElement body = new SOAPBodyElement(new ByteArrayInputStream(request.getBytes("UTF-8")));
      Object[] soapBodies = new Object[] { body };
    
      Vector result = (Vector)call.invoke(soapBodies);
      response = ((SOAPBodyElement)result.elementAt(0)).getAsString();
    }
    catch (AxisFault fault) {

      fault.printStackTrace();

      try {
        Message msg = call.getResponseMessage();
        response = msg.getSOAPEnvelope().getFirstBody().getAsString();
      }
      catch (Exception ex) {
        throw new RegistryException(ex);
      }
    }
    catch (Exception ex) {
      throw new RegistryException(ex);
    }

    log.debug("\nResponse message:\n" + response);

    return response;
  }
}
