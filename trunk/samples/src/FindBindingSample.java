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
import org.apache.juddi.datatype.CategoryBag;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.binding.AccessPoint;
import org.apache.juddi.datatype.binding.BindingTemplate;
import org.apache.juddi.datatype.binding.HostingRedirector;
import org.apache.juddi.datatype.response.BindingDetail;
import org.apache.juddi.datatype.response.DispositionReport;
import org.apache.juddi.datatype.response.ErrInfo;
import org.apache.juddi.datatype.response.Result;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.proxy.RegistryProxy;

/**
 * @author Steve Viens (sviens@users.sourceforge.net)
 */
public class FindBindingSample
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

    try
    {
    	String serviceKey = "5E2D4E60-9876-11D8-AE77-AC68422E7D92";

      CategoryBag categoryBag = new CategoryBag();
      Vector keyedRefVector = new Vector();
      keyedRefVector.addElement(new KeyedReference("ntis-gov:NAICS:1997","51121"));
      keyedRefVector.addElement(new KeyedReference("Mining","21"));
      keyedRefVector.addElement(new KeyedReference("alpha","abcdefghi"));
      keyedRefVector.addElement(new KeyedReference("cff049d0-c460-40c2-91c7-aa2261123dc7","Yadda, Yadda, Yadda"));
      keyedRefVector.addElement(new KeyedReference("1775f0f8-cd47-451d-88da-73ce508836f3","blah, blah, blah"));
      categoryBag.setKeyedReferenceVector(keyedRefVector);

      BindingDetail detail = registry.findBinding(serviceKey,categoryBag,null,null,100);
      Vector bindings = detail.getBindingTemplateVector();
      if (bindings != null)
      {
        for (int i=0; i<bindings.size(); i++)
        {
        	BindingTemplate binding = (BindingTemplate)bindings.elementAt(i);

        	System.out.println("ServiceKey: "+binding.getServiceKey());

        	AccessPoint accessPoint = binding.getAccessPoint();
          if (accessPoint != null)
          {
          	System.out.println("AccessPoint.url: "+accessPoint.getURL());
          	System.out.println("AccessPoint.urlType: "+accessPoint.getURLType());
          }

        	HostingRedirector hostingRedirector = binding.getHostingRedirector();
        	if (hostingRedirector != null)
        		System.out.println("HostingRedirector: "+hostingRedirector.getBindingKey());
        }
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