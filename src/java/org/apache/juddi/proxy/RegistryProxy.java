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

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.request.Admin;
import org.apache.juddi.datatype.request.AuthInfo;
import org.apache.juddi.datatype.request.Inquiry;
import org.apache.juddi.datatype.request.Publish;
import org.apache.juddi.datatype.request.SecurityPolicy;
import org.apache.juddi.datatype.response.AuthToken;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.handler.HandlerMaker;
import org.apache.juddi.handler.IHandler;
import org.apache.juddi.registry.Registry;
import org.apache.juddi.transport.Transport;
import org.apache.juddi.transport.TransportFactory;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Represents a vesion 2.0 UDDI registry and implements all
 * services as specified in the UDDI version 2.0 specification.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class RegistryProxy extends Registry
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(RegistryProxy.class);

  // jUDDI XML Handler maker
  private static HandlerMaker maker = HandlerMaker.getInstance();

  // jUDDI SOAP Transport
  private static Transport transport = TransportFactory.getTransport();

  /**
   * Default constructor
   */
  public RegistryProxy()
  {
  }

  /**
   * "Used to request an authentication token from an Operator Site.
   *  Authentication tokens are required to use all other APIs defined
   *  in the publishers API.  This server serves as the program's
   *  equivalent of a login request."
   *
   * @exception org.apache.juddi.error.RegistryException
   */
  public AuthToken getAuthToken()
    throws RegistryException
  {
    String userID = Config.getStringProperty("juddi.userID",null);
    if ((userID == null) || (userID.trim().length() == 0))
      throw new RegistryException("A juddi.userID property " +
        "value was not found in the juddi.properties " +
        "file: juddi.userID = "+userID);

    String password = Config.getStringProperty("juddi.password",null);
    if (password == null)
      throw new RegistryException("A juddi.password property " +
        "value was not found in the juddi.properties " +
        "file: juddi.password = "+password);

    return this.getAuthToken(userID,password);
  }

  /**
   *
   */
  public AuthToken getAuthToken(String userID,String password)
    throws RegistryException
  {
    if ((userID == null) || (userID.trim().length() == 0))
      throw new RegistryException("An invalid userID " +
        "value was specified: userID = "+userID);

    if (password == null)
      throw new RegistryException("An invalid password " +
        "value was specified: password = "+password);

    return super.getAuthToken(userID,password);
  }

  /**
   *
   */
  public RegistryObject execute(RegistryObject uddiRequest)
    throws RegistryException
  {
    // using the type of the request determine
    // which URL we're going to point this
    // web service request at.

    URL endPointURL = null;
    if (uddiRequest instanceof Inquiry)
      endPointURL = Config.getInquiryURL();
    else if (uddiRequest instanceof Publish || uddiRequest instanceof SecurityPolicy)
      endPointURL = Config.getPublishURL();
    else if (uddiRequest instanceof Admin)
      endPointURL = Config.getAdminURL();
    else
      throw new RegistryException("Unsupported Request: The " +
        "request '"+uddiRequest.getClass().getName()+"' is an " +
        "invalid or unknown request type.");

    // create a new 'temp' XML element. This
    // element is used as a container in which
    // to marshal the UDDI request into.

    Document document = XMLUtils.createDocument();
    Element temp = document.createElement("temp");

    // lookup the appropriate request handler
    // and marshal the RegistryObject into the
    // appropriate xml format (we only support
    // uddi v2.0 at this time) attaching results
    // to the temporary 'temp' element.

    String requestName = uddiRequest.getClass().getName();
    IHandler requestHandler = maker.lookup(requestName);
    requestHandler.marshal(uddiRequest,temp);
    Element request = (Element)temp.getFirstChild();

    request.setAttribute("generic",Config.getStringProperty("juddi.clientGeneric",Registry.UDDI_V2_GENERIC));
    request.setAttribute("xmlns",Config.getStringProperty("juddi.clientXMLNS",Registry.UDDI_V2_NAMESPACE));

    // A SOAP request is made and a SOAP response
    // is returned.

    Element response = transport.send(request,endPointURL);

    // First, let's make sure that a response
    // (any response) is found in the SOAP Body.

    String responseName = response.getLocalName();
    if (responseName == null)
    {
      throw new RegistryException("Unsupported response " +
        "from registry. A value was not present.");
    }

    // Let's now try to determine which UDDI response
    // we received and unmarshal it appropriately or
    // throw a RegistryException if it's unknown.

    IHandler handler = maker.lookup(responseName.toLowerCase());
    if (handler == null)
    {
      throw new RegistryException("Unsupported response " +
        "from registry. Response type '" + responseName +
        "' is unknown.");
    }

    // Well, we have now determined that something was
    // returned and it is "a something" that we know
    // about so let's unmarshal it into a RegistryObject

    RegistryObject uddiResponse = handler.unmarshal(response);

    // Next, let's make sure we didn't recieve a SOAP
    // Fault. If it is a SOAP Fault then throw it
    // immediately.

    if (uddiResponse instanceof RegistryException)
      throw ((RegistryException)uddiResponse);

    // That's it. Return the response to the caller.

    return uddiResponse;
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String[] args)
    throws RegistryException
  {
    RegistryProxy proxy = new RegistryProxy();
    AuthToken authToken = proxy.getAuthToken("sviens","password");
    AuthInfo authInfo = authToken.getAuthInfo();

    System.out.println("AuthToken: "+authInfo.getValue());
  }
}