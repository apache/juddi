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
public class DeleteTModelSample
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
    IRegistry registry = new RegistryProxy(props);

    String userID = "sviens";
    String password = "password";

    DispositionReport dispReport = null;
    Vector resultVector = null;

    try
    {      
      System.out.println("delete_tModel Sample");
      System.out.println("--------------------");
      System.out.println("userID: "+userID);
      System.out.println("password: "+password);
      System.out.println("");

      AuthToken authToken = registry.getAuthToken(userID,password);
      AuthInfo authInfo = authToken.getAuthInfo();
      System.out.println("AuthToken: "+authInfo.getValue());

      Vector keyVector = new Vector();
      for (int i=0; i<args.length; i++)
      {
        String key = (String)args[i];
        keyVector.add(key);
        System.out.println("Deleting: "+key); 
      }
      
      registry.deleteTModel(authInfo,keyVector);
      System.out.println("(done)");        
    }
    catch(RegistryException regex)
    {
      dispReport = regex.getDispositionReport();
      resultVector = dispReport.getResultVector();
      if (resultVector != null)
      {
        Result result = (Result)resultVector.elementAt(0);
        ErrInfo errInfo = result.getErrInfo();
        int errNo = result.getErrno();
        System.out.println("Error Number: "+errNo);
      }
    }
  }
}