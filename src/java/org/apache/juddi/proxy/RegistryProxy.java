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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

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
import org.apache.juddi.registry.AbstractRegistry;
import org.apache.juddi.registry.IRegistry;
import org.apache.juddi.transport.Transport;
import org.apache.juddi.transport.TransportFactory;
import org.apache.juddi.util.Loader;
import org.apache.juddi.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Represents a version 2.0 UDDI registry and implements
 * all services as specified in the v2.0 specification.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class RegistryProxy extends AbstractRegistry
{
  // jUDDI XML Handler maker
  private static HandlerMaker maker = HandlerMaker.getInstance();

  // jUDDI SOAP Transport
  private static Transport transport = TransportFactory.getTransport();
  
  // jUDDI Proxy Property File Name
  private static final String PROPFILE_NAME = "juddi.properties";
  
  // jUDDI Proxy Property Names
  public static final String INQUIRY_ENDPOINT_PROPERTY_NAME = "juddi.proxy.inquiryURL";
  public static final String PUBLISH_ENDPOINT_PROPERTY_NAME = "juddi.proxy.publishURL";
  public static final String ADMIN_ENDPOINT_PROPERTY_NAME = "juddi.proxy.adminURL";    
  public static final String SECURITY_PROVIDER_PROPERTY_NAME = "juddi.proxy.securityProvider";
  public static final String PROTOCOL_HANDLER_PROPERTY_NAME = "juddi.proxy.protocolHandler";
  public static final String UDDI_VERSION_PROPERTY_NAME = "juddi.proxy.uddiVersion";
  public static final String UDDI_NAMESPACE_PROPERTY_NAME = "juddi.proxy.uddiNamespace";

  // jUDDI Proxy Default Property Values
  public static final String DEFAULT_INQUIRY_ENDPOINT = "http://localhost/juddi/inquiry";
  public static final String DEFAULT_PUBLISH_ENDPOINT = "http://localhost/juddi/publish";
  public static final String DEFAULT_ADMIN_ENDPOINT = "http://localhost/juddi/admin";    
  public static final String DEFAULT_SECURITY_PROVIDER = "com.sun.net.ssl.internal.ssl.Provider";
  public static final String DEFAULT_PROTOCOL_HANDLER = "com.sun.net.ssl.internal.www.protocol";
  public static final String DEFAULT_UDDI_VERSION = "2.0";
  public static final String DEFAULT_UDDI_NAMESPACE = "urn:uddi-org:api_v2";
  
  // jUDDI Proxy Properties
  private URL inquiryURL;
  private URL publishURL;
  private URL adminURL;
  private String securityProvider;
  private String protocolHandler;
  private String uddiVersion;
  private String uddiNamespace;
  
  /**
   * Create a new instance of RegistryProxy.  This constructor
   * looks in the classpath for a file named 'juddi.properties'
   * and uses property values in this file to initialize the
   * new instance. Default values are used if the file does not
   * exist or if a particular property value is not present.
   */
  public RegistryProxy()
  {
    super();
    
    // attempt to load proxy properties from
    // the juddi.properties file which should
    // be located in the classpath. 
    Properties props = new Properties();

    try {
      InputStream stream = Loader.getResourceAsStream(PROPFILE_NAME);
      if (stream != null)
        props.load(stream);
    }
    catch (IOException ioex) {
    	ioex.printStackTrace();
    }

    this.init(props);
  }

  /**
   * Creates a new instance of RegistryProxy. This constructor
   * uses the property values passed in the Properties parameter
   * to initialize the new RegistryProxy instance. Default values 
   * are used if the file does not exist or if a particular 
   * property value is not present.
   */
  public RegistryProxy(Properties props)
  {
    super();
    
    this.init(props);
  }

  /**
   * 
   */
  private void init(Properties props)
  { 
    // Initialize proxy with default values
    try {
      this.setInquiryURL(new URL(DEFAULT_INQUIRY_ENDPOINT));
      this.setPublishURL(new URL(DEFAULT_PUBLISH_ENDPOINT));
      this.setAdminURL(new URL(DEFAULT_ADMIN_ENDPOINT));
      this.setUddiVersion(IRegistry.UDDI_V2_GENERIC);
      this.setUddiNamespace(IRegistry.UDDI_V2_NAMESPACE);
    }
    catch(MalformedURLException muex) {
      muex.printStackTrace();
    }
    
    // There's no point in going any further
    // if the Properties parameter is null.
    if (props == null)
      return;
    
    // Override defaults with specific specific values
    try {
      String iURL = props.getProperty(INQUIRY_ENDPOINT_PROPERTY_NAME);
      if (iURL != null)
        this.setInquiryURL(new URL(iURL));
        
      String pURL = props.getProperty(PUBLISH_ENDPOINT_PROPERTY_NAME);
      if (pURL != null)
        this.setPublishURL(new URL(pURL));
    
      String aURL = props.getProperty(ADMIN_ENDPOINT_PROPERTY_NAME);
      if (aURL != null)
        this.setAdminURL(new URL(aURL));
    }
    catch(MalformedURLException muex) {
      muex.printStackTrace();
    } 

    String secProvider = props.getProperty(SECURITY_PROVIDER_PROPERTY_NAME);
    if (secProvider != null)
      this.setSecurityProvider(secProvider);
  
    String protoHandler = props.getProperty(PROTOCOL_HANDLER_PROPERTY_NAME);
    if (protoHandler != null)
      this.setProtocolHandler(protoHandler);

    String uddiVer = props.getProperty(UDDI_VERSION_PROPERTY_NAME);
    if (uddiVer != null)
      this.setUddiVersion(uddiVer);
  
    String uddiNS = props.getProperty(UDDI_NAMESPACE_PROPERTY_NAME);
    if (uddiNS != null)
      this.setUddiNamespace(uddiNS);
  }
   
   /**
   * @return Returns the adminURL.
   */
  public URL getAdminURL() 
  {
    return this.adminURL;
  }
  
  /**
   * @param adminURL The adminURL to set.
   */
  public void setAdminURL(URL url) 
  {
    this.adminURL = url;
  }
  
  /**
   * @return Returns the inquiryURL.
   */
  public URL getInquiryURL() 
  {
    return this.inquiryURL;
  }
  
  /**
   * @param inquiryURL The inquiryURL to set.
   */
  public void setInquiryURL(URL url) 
  {
    this.inquiryURL = url;
  }
  
  /**
   * @return Returns the publishURL.
   */
  public URL getPublishURL() 
  {
    return this.publishURL;
  }
  
  /**
   * @param publishURL The publishURL to set.
   */
  public void setPublishURL(URL url) 
  {
    this.publishURL = url;
  }
  
  /**
   * @return Returns the protocolHandler.
   */
  public String getProtocolHandler() 
  {
    return this.protocolHandler;
  }

  /**
   * @param protocolHandler The protocolHandler to set.
   */
  public void setProtocolHandler(String protoHandler)
  {
    this.protocolHandler = protoHandler;
  }

  /**
   * @return Returns the securityProvider.
   */
  public String getSecurityProvider() 
  {
    return this.securityProvider;
  }

  /**
   * @param securityProvider The securityProvider to set.
   */
  public void setSecurityProvider(String secProvider) 
  {
    this.securityProvider = secProvider;
  }

  /**
   * @return Returns the uddiNS.
   */
  public String getUddiNamespace()
  {
    return this.uddiNamespace;
  }
  
  /**
   * @param uddiNS The uddiNS to set.
   */
  public void setUddiNamespace(String uddiNS)
  {
    this.uddiNamespace = uddiNS;
  }
  
  /**
   * @return Returns the uddiVersion.
   */
  public String getUddiVersion()
  {
    return this.uddiVersion;
  }
  
  /**
   * @param uddiVersion The uddiVersion to set.
   */
  public void setUddiVersion(String uddiVer) 
  {
    this.uddiVersion = uddiVer;
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
      endPointURL = this.getInquiryURL();
    else if (uddiRequest instanceof Publish || uddiRequest instanceof SecurityPolicy)
      endPointURL = this.getPublishURL();
    else if (uddiRequest instanceof Admin)
      endPointURL = this.getAdminURL();
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

    request.setAttribute("generic",this.getUddiVersion());
    request.setAttribute("xmlns",this.getUddiNamespace());

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
    Properties proxyProps = new Properties();
    proxyProps.setProperty("1","one");
    proxyProps.setProperty("2","two");
    proxyProps.setProperty("3","three");
    proxyProps.setProperty("4","four");
    proxyProps.setProperty("5","five");    
    
    IRegistry registry = new RegistryProxy(proxyProps);    
    AuthToken authToken = registry.getAuthToken("sviens","password");
    AuthInfo authInfo = authToken.getAuthInfo();

    System.out.println("AuthToken: "+authInfo.getValue());
  }
}