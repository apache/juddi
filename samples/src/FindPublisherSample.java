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
import org.apache.juddi.datatype.response.PublisherInfo;
import org.apache.juddi.datatype.response.PublisherInfos;
import org.apache.juddi.datatype.response.PublisherList;
import org.apache.juddi.proxy.RegistryProxy;

/**
 * @author Steve Viens (sviens@apache.org)
 * @author Andy Cutright (acutright@users.sourceforge.net)
 *
 * This class demonstrates one of the proprietary jUDDI UDDI Registry
 * extension. To enable the registry to respond, the application's
 * /admin context must be enabled. Edit web.xml, enabling this section:
 *
 *  <servlet-mapping>
 *   <servlet-name>jUDDIAdminServlet</servlet-name>
 *   <url-pattern>/admin</url-pattern>
 *  </servlet-mapping>
 *
 */
public class FindPublisherSample
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

  	// NOTE: Find Publisher is a proprietary API
  	// call and is unsupported by Microsoft or
  	// any of the other UDDI implementations.

  	try
    {
      /**
       * Retrieve all publishers listed in the registry
       */
      PublisherList publisherList = registry.findPublisher("%","%",null,0);

      if (publisherList == null) {
        System.err.println("Unable to invoke 'find_publisher'");
        System.exit(-1);
      }
      PublisherInfos publisherInfos = publisherList.getPublisherInfos();
      if (publisherInfos == null) {
        System.err.println("Unable to retrieve 'PublisherInfos'");
        System.exit(-2);
      }
      Vector publisherInfoVector = publisherInfos.getPublisherInfoVector();
      if (publisherInfoVector == null) {
        System.out.println("No publishers found matching criteria");
        System.exit(0);
      }
      for (int index = 0; index < publisherInfoVector.size(); index++) {
        PublisherInfo publisherInfo = (PublisherInfo)publisherInfoVector.elementAt(index);
        System.out.println("Publisher ID is ["
                           + publisherInfo.getPublisherID()
                           + "], Publisher name is ["
                           + publisherInfo.getNameValue()
                           + "]");
      }
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }
}