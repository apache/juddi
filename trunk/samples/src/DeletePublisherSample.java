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

import java.io.FileInputStream;
import java.util.Properties;
import java.util.Vector;

import org.apache.juddi.IRegistry;
import org.apache.juddi.datatype.publisher.Publisher;
import org.apache.juddi.datatype.request.AuthInfo;
import org.apache.juddi.datatype.response.AuthToken;
import org.apache.juddi.datatype.response.PublisherDetail;
import org.apache.juddi.proxy.RegistryProxy;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class DeletePublisherSample
{
  public static void main(String[] args) throws Exception
  {
    // Option #1 (grab proxy property values from juddi.properties in classpath)
    //IRegistry registry = new RegistryProxy();

    // Option #2 (import proxy property values from a specified properties file)
  	Properties props = new Properties();
  	props.load(new FileInputStream(args[0]));
  	IRegistry registry = new RegistryProxy(props);

    // Option #3 (explicitly set the proxy property values)
    //Properties props = new Properties();
    //props.setProperty(RegistryProxy.ADMIN_ENDPOINT_PROPERTY_NAME,"http://localhost:8080/juddi/admin");
    //props.setProperty(RegistryProxy.INQUIRY_ENDPOINT_PROPERTY_NAME,"http://localhost:8080/juddi/inquiry");
    //props.setProperty(RegistryProxy.PUBLISH_ENDPOINT_PROPERTY_NAME,"http://localhost:8080/juddi/publish");
    //props.setProperty(RegistryProxy.TRANSPORT_CLASS_PROPERTY_NAME,"org.apache.juddi.proxy.AxisTransport");
    //props.setProperty(RegistryProxy.SECURITY_PROVIDER_PROPERTY_NAME,"com.sun.net.ssl.internal.ssl.Provider");
    //props.setProperty(RegistryProxy.PROTOCOL_HANDLER_PROPERTY_NAME,"com.sun.net.ssl.internal.www.protocol");
    //IRegistry registry = new RegistryProxy(props);

    // Option #4 (Microsoft Test Site)
  	//Properties props = new Properties();
  	//props.setProperty(RegistryProxy.INQUIRY_ENDPOINT_PROPERTY_NAME,"http://test.uddi.microsoft.com/inquire");
  	//props.setProperty(RegistryProxy.PUBLISH_ENDPOINT_PROPERTY_NAME,"https://test.uddi.microsoft.com/publish");
  	//props.setProperty(RegistryProxy.TRANSPORT_CLASS_PROPERTY_NAME,"org.apache.juddi.proxy.AxisTransport");
  	//props.setProperty(RegistryProxy.SECURITY_PROVIDER_PROPERTY_NAME,"com.sun.net.ssl.internal.ssl.Provider");
  	//props.setProperty(RegistryProxy.PROTOCOL_HANDLER_PROPERTY_NAME,"com.sun.net.ssl.internal.www.protocol");
  	//props.setProperty(RegistryProxy.HTTP_PROXY_HOST_PROPERTY_NAME,"na6v13a01.fmr.com");
  	//props.setProperty(RegistryProxy.HTTP_PROXY_PORT_PROPERTY_NAME,"8000");
  	//IRegistry registry = new RegistryProxy(props);

    String userID = "sviens";
    String password = "password";

  	try
    {
      // execute a GetAuthToken request
      AuthToken token = registry.getAuthToken(userID,password);
      AuthInfo authInfo = token.getAuthInfo();

      // create a publisher with administrative privileges
      Publisher publisher = new Publisher("BlueNoteIdentifier", "Blue Note", true);
      publisher.setEnabled(true);
      // create a publisher to remove
      Publisher perisher = new Publisher("removeMe", "Remove Me", false);
      // put the Publisher objects into a Vector
      Vector vector = new Vector(2);
      vector.add(publisher);
      vector.add(perisher);

      // make the request
      PublisherDetail detail = registry.savePublisher(authInfo, vector);

      System.out.println("publishers saved = " + detail.getPublisherVector().size());

      // get an authToken using the publisher with administrative privileges
      token = registry.getAuthToken("BlueNoteIdentifier", "password");
      authInfo = token.getAuthInfo();

      // create a vector of strings containing the
      String publisherID = "removeMe";
      vector = new Vector(1);
      vector.add(publisherID);
      registry.deletePublisher(authInfo, vector);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }
}