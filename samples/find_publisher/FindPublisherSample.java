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

import java.util.Vector;

import org.apache.juddi.datatype.response.PublisherInfo;
import org.apache.juddi.datatype.response.PublisherInfos;
import org.apache.juddi.datatype.response.PublisherList;
import org.apache.juddi.proxy.RegistryProxy;
import org.apache.juddi.registry.IRegistry;

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
  public static void main(String[] args)
  {
    IRegistry registry = new RegistryProxy();

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