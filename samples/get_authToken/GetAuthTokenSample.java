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

import org.apache.juddi.datatype.request.AuthInfo;
import org.apache.juddi.datatype.response.AuthToken;
import org.apache.juddi.proxy.RegistryProxy;
import org.apache.juddi.registry.IRegistry;
import org.apache.juddi.util.Config;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class GetAuthTokenSample
{
  public static void main(String[] args)
  {
    IRegistry registry = new RegistryProxy();

    String userID = "sviens";
    String password = "password";
    AuthToken authToken = null;
    AuthInfo authInfo = null;

    try
    {
      System.out.println("get_authToken Sample #1");
      System.out.println("-----------------------");
      System.out.println("Using userID and password values pass as method parameters: ");
      System.out.println("  userID: "+userID);
      System.out.println("  password: "+password);

      authToken = registry.getAuthToken(userID,password);
      authInfo = authToken.getAuthInfo();
      System.out.println("AuthToken: "+authInfo.getValue());
      System.out.println();


      System.out.println("get_authToken Sample #2");
      System.out.println("-----------------------");
      System.out.println("Using userID and password values from 'juddi.properties' file: ");
      System.out.println("  juddi.userID: "+Config.getStringProperty("juddi.userID",null));
      System.out.println("  juddi.password: "+Config.getStringProperty("juddi.password",null));

      authToken = registry.getAuthToken(userID,password);
      authInfo = authToken.getAuthInfo();
      System.out.println("AuthToken: "+authInfo.getValue());
      System.out.println();
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }
}
