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

import org.apache.juddi.datatype.response.DispositionReport;
import org.apache.juddi.datatype.response.ErrInfo;
import org.apache.juddi.datatype.response.Result;
import org.apache.juddi.datatype.response.TModelInfo;
import org.apache.juddi.datatype.response.TModelInfos;
import org.apache.juddi.datatype.response.TModelList;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.proxy.RegistryProxy;
import org.apache.juddi.registry.IRegistry;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class FindTModelSample
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
      System.out.println("find_tModel Sample");
      System.out.println("------------------");
      TModelList list = registry.findTModel("uddi-org",null,null,null,0);
      TModelInfos infos = list.getTModelInfos();
      Vector vector = infos.getTModelInfoVector();

      for (int i=0; i<vector.size(); i++)
      {
        TModelInfo info = (TModelInfo)vector.elementAt(i);
        System.out.println(info.getNameValue() + "["+info.getTModelKey()+"]");
      }
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