import java.io.FileInputStream;
import java.util.Properties;
import java.util.Vector;

import org.apache.juddi.IRegistry;
import org.apache.juddi.datatype.Description;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.OverviewDoc;
import org.apache.juddi.datatype.request.AuthInfo;
import org.apache.juddi.datatype.response.AuthToken;
import org.apache.juddi.datatype.response.DispositionReport;
import org.apache.juddi.datatype.response.ErrInfo;
import org.apache.juddi.datatype.response.Result;
import org.apache.juddi.datatype.response.TModelDetail;
import org.apache.juddi.datatype.tmodel.TModel;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.proxy.RegistryProxy;
import org.apache.juddi.util.Language;

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


/**
 * @author Steve Viens (sviens@apache.org)
 */
public class SaveTModelSample
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
      System.out.println("save_tModel Sample");
      System.out.println("------------------");
      System.out.println("userID: "+userID);
      System.out.println("password: "+password);
      System.out.println("");

      AuthToken authToken = registry.getAuthToken(userID,password);
      AuthInfo authInfo = authToken.getAuthInfo();
      System.out.println("AuthToken: "+authInfo.getValue());

      OverviewDoc overviewDoc = new OverviewDoc();
      overviewDoc.setOverviewURL("http://www.viens.net/juddi/taxonomy_models.html#domain");
      overviewDoc.addDescription(new Description("Taxonomy used to categorize entities by top level domain.",Language.ENGLISH));

      TModel tModelIn = new TModel();
      tModelIn.setAuthorizedName("Steve Viens");
      tModelIn.setOperator("Viens.net");
      tModelIn.setName("viens-net:domain");
      tModelIn.setOverviewDoc(overviewDoc);
      tModelIn.addDescription(new Description("Categorize entities by top level domain name.",Language.ENGLISH));
      tModelIn.addCategory(new KeyedReference("uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4","types","categorization"));
      tModelIn.addCategory(new KeyedReference("uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4","types","checked"));

      Vector tModelVector = new Vector();
      tModelVector.add(tModelIn);

      TModelDetail detail = registry.saveTModel(authInfo,tModelVector);
      TModel tModelOut = null;
      if (detail.getTModelVector().size() > 0)
        tModelOut = (TModel)detail.getTModelVector().elementAt(0);

      System.out.println("TModel Key: "+tModelOut.getTModelKey());
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