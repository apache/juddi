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
package org.apache.juddi.uddi4j;


import java.util.Vector;
import java.util.Properties;

import org.apache.juddi.proxy.RegistryProxy;
import org.apache.juddi.datatype.publisher.Publisher;
import org.apache.juddi.datatype.request.AuthInfo;
import org.apache.juddi.datatype.response.AuthToken;
import org.apache.juddi.datatype.response.PublisherDetail;
import org.apache.juddi.registry.IRegistry;

/**
 * @author Steve Viens (sviens@users.sourceforge.net)
 * @author Andy Cutright (acutright@users.sourceforge.net)
 */
public class PublisherManager
{
  public PublisherManager() {
  }

  public static boolean createPublisher(String name, String identifier, Properties testprops) {
    Properties props = new Properties();
    props.setProperty(RegistryProxy.ADMIN_ENDPOINT_PROPERTY_NAME, testprops.getProperty("adminURL"));
    props.setProperty(RegistryProxy.INQUIRY_ENDPOINT_PROPERTY_NAME, testprops.getProperty("inquiryURL"));
    props.setProperty(RegistryProxy.PUBLISH_ENDPOINT_PROPERTY_NAME, testprops.getProperty("publishURL"));
    IRegistry proxy = new RegistryProxy(props);

    boolean ret = false;

    try
    {
      // execute a GetAuthToken request
      AuthToken token = proxy.getAuthToken("juddi", "password");
      AuthInfo authInfo = token.getAuthInfo();

      // create a publisher
      Publisher publisher = new Publisher(name, identifier);

      // put the Publisher object into a Vector
      Vector vector = new Vector(1);
      vector.add(publisher);

      // make the request
      PublisherDetail detail = proxy.savePublisher(authInfo, vector);

      Vector pubVector = detail.getPublisherVector();
      if (pubVector.size() == 1) {
        ret = true;
      }

      return ret;
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
    return ret;
  }

  public static String getExpiredAuthToken(String publisher, String password) {
    RegistryProxy proxy = new RegistryProxy();
    AuthToken token = null;
    AuthInfo authInfo = null;
    String ret = null;
    try {
      token = proxy.getAuthToken(publisher, password);
      authInfo = token.getAuthInfo();
      ret = authInfo.getValue();
      proxy.discardAuthToken(authInfo);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    return ret;
  }
}

