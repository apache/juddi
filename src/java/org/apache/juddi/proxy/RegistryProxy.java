/*
 * Copyright (C) 2004, Liberty Mutual Group
 * All Rights Reserved
 */

package org.apache.juddi.proxy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.Vector;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.AbstractRegistry;
import org.apache.juddi.IRegistry;
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
    // jUDDI logger
    private static Log log = LogFactory.getLog(RegistryProxy.class);
    
    // jUDDI XML Handler maker
    private static HandlerMaker maker = HandlerMaker.getInstance();

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
      // We need to have a non-null Properties
      // instance so initialization takes place.
      if (props == null)
        props = new Properties();
      
      // Override defaults with specific specific values
      try 
      {
        String iURL = props.getProperty(INQUIRY_ENDPOINT_PROPERTY_NAME);
        if (iURL != null)
          this.setInquiryURL(new URL(iURL));
        else
          this.setInquiryURL(new URL(DEFAULT_INQUIRY_ENDPOINT));
        
        String pURL = props.getProperty(PUBLISH_ENDPOINT_PROPERTY_NAME);
        if (pURL != null)
          this.setPublishURL(new URL(pURL));
        else
          this.setPublishURL(new URL(DEFAULT_PUBLISH_ENDPOINT));
        
        String aURL = props.getProperty(ADMIN_ENDPOINT_PROPERTY_NAME);
        if (aURL != null)
          this.setAdminURL(new URL(aURL));
        else
          this.setAdminURL(new URL(DEFAULT_ADMIN_ENDPOINT));
      }
      catch(MalformedURLException muex) {
        muex.printStackTrace();
      } 
              
      String secProvider = props.getProperty(SECURITY_PROVIDER_PROPERTY_NAME);
      if (secProvider != null)
        this.setSecurityProvider(secProvider);
      else
        this.setSecurityProvider(DEFAULT_SECURITY_PROVIDER);
    
      String protoHandler = props.getProperty(PROTOCOL_HANDLER_PROPERTY_NAME);
      if (protoHandler != null)
        this.setProtocolHandler(protoHandler);
      else
        this.setProtocolHandler(DEFAULT_PROTOCOL_HANDLER);

      String uddiVer = props.getProperty(UDDI_VERSION_PROPERTY_NAME);
      if (uddiVer != null)
        this.setUddiVersion(uddiVer);
      else
        this.setUddiVersion(DEFAULT_UDDI_VERSION);
    
      String uddiNS = props.getProperty(UDDI_NAMESPACE_PROPERTY_NAME);
      if (uddiNS != null)
        this.setUddiNamespace(uddiNS);
      else
        this.setUddiNamespace(DEFAULT_UDDI_NAMESPACE);
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

      Element response = send(request,endPointURL);

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

    public Element send(Element request,URL endpointURL) throws RegistryException
    {    
        Service service = null;
        Call call = null;
        Element response = null;
    
        log.debug("\nRequest message:\n" + XMLUtils.toString(request));

      try {
      service = new Service();
      call = (Call)service.createCall();
      call.setTargetEndpointAddress(endpointURL);

      String requestString = XMLUtils.toString(request);
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

    log.debug("\nResponse message:\n" + XMLUtils.toString(response));


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
  
  
  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String[] args)
    throws RegistryException
  {
    // Option #1 (grab proxy property values from uddi.properties in classpath)
    //IRegistry registry = new RegistryProxy();
      
    // Option #2 (import proxy property values from a specified properties file)
    //Properties props = new Properties();
    //props.load(new FileInputStream("proxy.properties"));
    //IRegistry registry = new RegistryProxy(props);
      
    // Option #3 (explicitly set the proxy property values)
    Properties props = new Properties();
    props.setProperty(ADMIN_ENDPOINT_PROPERTY_NAME,"http://localhost:8080/juddi/v2/admin");
    props.setProperty(INQUIRY_ENDPOINT_PROPERTY_NAME,"http://localhost:8080/juddi/v2/inquiry");
    props.setProperty(PUBLISH_ENDPOINT_PROPERTY_NAME,"http://localhost:8080/juddi/v2/publish");
    props.setProperty(SECURITY_PROVIDER_PROPERTY_NAME,"com.sun.net.ssl.internal.ssl.Provider");
    props.setProperty(PROTOCOL_HANDLER_PROPERTY_NAME,"com.sun.net.ssl.internal.www.protocol");    
    IRegistry registry = new RegistryProxy(props);
    
    // Option #4 (Microsoft Test Site)
    //Properties props = new Properties();
    //props.setProperty(INQUIRY_ENDPOINT_PROPERTY_NAME,"http://test.uddi.microsoft.com/inquire");
    //props.setProperty(PUBLISH_ENDPOINT_PROPERTY_NAME,"https://test.uddi.microsoft.com/publish");
    //props.setProperty(SECURITY_PROVIDER_PROPERTY_NAME,"com.sun.net.ssl.internal.ssl.Provider");
    //props.setProperty(PROTOCOL_HANDLER_PROPERTY_NAME,"com.sun.net.ssl.internal.www.protocol");
    //IRegistry registry = new RegistryProxy(props);
      
    AuthToken authToken = registry.getAuthToken("sviens","password");
    AuthInfo authInfo = authToken.getAuthInfo();
    System.out.println(authInfo.getValue());
  }
}
