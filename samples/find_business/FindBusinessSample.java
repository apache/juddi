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

import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.response.BusinessInfo;
import org.apache.juddi.datatype.response.BusinessInfos;
import org.apache.juddi.datatype.response.BusinessList;
import org.apache.juddi.proxy.RegistryProxy;
import org.apache.juddi.registry.IRegistry;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class FindBusinessSample
{
  public static void main(String[] args)
  {
    // Option #1 (grabs properties from juddi.properties)
    //IRegistry registry = new RegistryProxy();
    
    // Option #2
    Properties props = new Properties();
    props.setProperty("juddi.proxy.adminURL","http://localhost:8080/juddi/admin");
    props.setProperty("juddi.proxy.inquiryURL","http://localhost:8080/juddi/inquiry");
    props.setProperty("juddi.proxy.publishURL","http://localhost:8080/juddi/publish");
    props.setProperty("juddi.proxy.securityProvider","com.sun.net.ssl.internal.ssl.Provider");
    props.setProperty("juddi.proxy.protocolHandler","com.sun.net.ssl.internal.www.protocol");    
    IRegistry registry = new RegistryProxy(props);

    try
    {
      Vector inNames = new Vector();
      inNames.add(new Name("Sun"));
      
        BusinessList list = registry.findBusiness(inNames,null,null,null,null,null,100);
      BusinessInfos infos = list.getBusinessInfos();
      
      Vector businesses = infos.getBusinessInfoVector();
      
      if (businesses != null)
      {
        for (int i=0; i<businesses.size(); i++)
        {
          BusinessInfo info = (BusinessInfo)businesses.elementAt(i);
          Vector outNames = info.getNameVector();
          for (int j=0; j<outNames.size(); j++)
          {
            Name name = (Name)outNames.elementAt(j);
            System.out.println(name.getValue());
          }
        }
      }
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }
}