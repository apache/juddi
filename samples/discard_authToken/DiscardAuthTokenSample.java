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

import java.util.Properties;
import java.util.Vector;

import org.apache.juddi.datatype.request.AuthInfo;
import org.apache.juddi.datatype.response.AuthToken;
import org.apache.juddi.datatype.response.DispositionReport;
import org.apache.juddi.datatype.response.ErrInfo;
import org.apache.juddi.datatype.response.Result;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.proxy.RegistryProxy;
import org.apache.juddi.registry.IRegistry;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class DiscardAuthTokenSample
{
  public static void main(String[] args)
  {
    // Option #1 (grabs properties from juddi.properties)
    //IRegistry registry = new RegistryProxy();
    
    // Option #2
    Properties props = new Properties();
    props.setProperty(RegistryProxy.ADMIN_ENDPOINT_PROPERTY_NAME,"http://localhost:8080/juddi/admin");
    props.setProperty(RegistryProxy.INQUIRY_ENDPOINT_PROPERTY_NAME,"http://localhost:8080/juddi/inquiry");
    props.setProperty(RegistryProxy.PUBLISH_ENDPOINT_PROPERTY_NAME,"http://localhost:8080/juddi/publish");
//    props.setProperty(RegistryProxy.SECURITY_PROVIDER_PROPERTY_NAME,"com.sun.net.ssl.internal.ssl.Provider");
//    props.setProperty(RegistryProxy.PROTOCOL_HANDLER_PROPERTY_NAME,"com.sun.net.ssl.internal.www.protocol");    
    IRegistry registry = new RegistryProxy(props);

    String userID = "sviens";
    String password = "password";

    try
    {
      System.out.println("discard_authToken Sample");
      System.out.println("------------------------");
      System.out.println("userID: "+userID);
      System.out.println("password: "+password);
      System.out.println("");

      AuthToken authToken = registry.getAuthToken(userID,password);
      AuthInfo authInfo = authToken.getAuthInfo();
      System.out.println("AuthToken: "+authInfo.getValue());

      DispositionReport dispReport = registry.discardAuthToken(authInfo);
      Vector resultVector = dispReport.getResultVector();
      if (resultVector != null)
      {
        System.out.println("Discard: "+authInfo.getValue());
        Result result = (Result)resultVector.elementAt(0);
        ErrInfo errInfo = result.getErrInfo();
        int errNo = result.getErrno();
        System.out.println("Errno:   "+errNo);
      }

      System.out.println("Discard: "+authInfo.getValue());
      registry.discardAuthToken(authInfo);
      // the line above should throw a RegistryException
    }
    catch (RegistryException regex)
    {
      DispositionReport dispReport = regex.getDispositionReport();
      Vector resultVector = dispReport.getResultVector();
      if (resultVector != null)
      {
        Result result = (Result)resultVector.elementAt(0);
        int errNo = result.getErrno();
        System.out.println("Errno:   "+errNo);

        ErrInfo errInfo = result.getErrInfo();
        if (errInfo != null)
        {
          System.out.println("ErrCode: "+errInfo.getErrCode());
          System.out.println("ErrMsg:  "+errInfo.getErrMsg());
        }        
      }
    }

    System.out.println("\ndone.");    
  }
}